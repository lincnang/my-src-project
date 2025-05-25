package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.lock.CharItemsReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * 物品升級系統
 *
 * @author
 */
public class ItemIntegrationTable {
    private static final Log _log = LogFactory.getLog(ItemIntegrationTable.class);
    private final static Random random = new Random();
    // private static final String TOKEN = ",";
    private static HashMap<Integer, ItemIntegration> _array = new HashMap<Integer, ItemIntegration>();
    private static ItemIntegrationTable _instance;

    private ItemIntegrationTable() {
        PerformanceTimer timer = new PerformanceTimer();
        getData();
        _log.info("讀取->[系統]_道具升級系統設置數量: " + _array.size() + "(" + timer.get() + "ms)");
        if (_array.size() <= 0) {
            _array.clear();
            _array = null;
        }
        // System.out.println(_array.get(1)._item_id);
    }

    public static ItemIntegrationTable get() {
        if (_instance == null) {
            _instance = new ItemIntegrationTable();
        }
        return _instance;
    }

    public static void forItemIntegration(L1PcInstance pc, L1ItemInstance item, L1ItemInstance itemG) {
        if (_array == null) {
            return;
        }
        int itemid = item.getItem().getItemId();
        int itemG_id = itemG.getItem().getItemId();
        int key = -1;
        for (int i = 0; i < _array.size(); i++) {
            if (_array.get(i) == null) {
                System.out.println("警告!!第:" + i + "筆發生錯誤");
                break;
            }
            if (_array.get(i)._item_id == itemid && _array.get(i)._Integration_ID == itemG_id) {
                key = i;
                break;
            }
        }
        if (key < 0) {
            pc.sendPackets(new S_ServerMessage(166, itemG.getName() + "無法升級"));
            return;
        }
        // 身上物品太多無法合成
        if (pc.getInventory().getSize() >= 160) {
            pc.sendPackets(new S_ServerMessage(263));
            return;
        }
        // 身上負重無法合成
        if ((pc.getInventory().getWeight() / pc.getMaxWeight() * 100) > 90) {
            pc.sendPackets(new S_ServerMessage(82));
            return;
        }
        // 職業判斷
        if (_array.get(key)._checkClass != 0) {
            byte class_id = 0;
            String msg = "";
            if (pc.isCrown()) { // 王族
                class_id = 1;
            } else if (pc.isKnight()) { // 騎士
                class_id = 2;
            } else if (pc.isWizard()) { // 法師
                class_id = 3;
            } else if (pc.isElf()) { // 妖精
                class_id = 4;
            } else if (pc.isDarkelf()) { // 黑妖
                class_id = 5;
            } else if (pc.isDragonKnight()) { // 龍騎士
                class_id = 6;
            } else if (pc.isIllusionist()) { // 幻術士
                class_id = 7;
            } else if (pc.isWarrior()) { // 狂戰士
                class_id = 8;
            }
            switch (_array.get(key)._checkClass) {
                case 1:
                    msg = "王族";
                    break;
                case 2:
                    msg = "騎士";
                    break;
                case 3:
                    msg = "法師";
                    break;
                case 4:
                    msg = "妖精";
                    break;
                case 5:
                    msg = "黑暗妖精";
                    break;
                case 6:
                    msg = "龍騎士";
                    break;
                case 7:
                    msg = "幻術士";
                    break;
                case 8:
                    msg = "狂戰士";
                    break;
            }
            if (_array.get(key)._checkClass != class_id && !pc.isGm()) { // 職業不符
                pc.sendPackets(new S_ServerMessage(166, "你的職業無法使用" + msg + "的專屬道具"));
                return;
            }
        }
        /**
         * 檢查種族
         */
        /*
         * if (_array.get(key)._checkrace != 0) { if (pc.getRace() !=
         * _array.get(key)._checkrace) { pc.sendPackets(new
         * S_ServerMessage(3142, "不符")); return; } }
         */
        // 等級限制
        if (_array.get(key)._level != 0 && !pc.isGm()) {
            if (pc.getLevel() < _array.get(key)._level) {
                pc.sendPackets(new S_ServerMessage(166, "等級" + _array.get(key)._level + "以上才可使用此道具"));
                return;
            }
        }
        // 檢查道具數量
        if (_array.get(key)._needCount != 0) {
            if (_array.get(key)._needCount > item.getCount()) {
                pc.sendPackets(new S_ServerMessage(337, item.getName() + "(" + (_array.get(key)._needCount - item.getCount()) + ")"));
                return;
            }
        }
        // 檢查升級道具的數量
        if (_array.get(key)._Integration_count != 0) {
            if (_array.get(key)._Integration_count > itemG.getCount()) {
                pc.sendPackets(new S_ServerMessage(337, itemG.getName() + "(" + (_array.get(key)._Integration_count - itemG.getCount()) + ")"));
                return;
            }
        }
        // 檢查材料
        if (_array.get(key)._materials != null && _array.get(key)._counts != null) {
            int[] materials = _array.get(key)._materials;
            int[] counts = _array.get(key)._counts;
            boolean isCreate = true;
            // 檢查身上的物品
            for (int j = 0; j < materials.length; j++) {
                if (!pc.getInventory().checkItem(materials[j], counts[j])) {
                    L1Item temp = ItemTable.get().getTemplate(materials[j]);
                    pc.sendPackets(new S_ServerMessage(337, temp.getName() + "(" + (counts[j] - pc.getInventory().countItems(temp.getItemId())) + ")"));
                    isCreate = false;
                }
                /*
                 * L1ItemInstance item1 =
                 * pc.getInventory().findItemId(materials[j]); if (item1 !=
                 * null) { if (item1.getDeleteTime() != null) {
                 * pc.sendPackets(new S_SystemMessage(item1.getName() +
                 * " 是期限商品。")); isCreate = false; } }
                 */
            }
            // 刪除材料
            if (isCreate) {
                for (int k = 0; k < materials.length; k++) {
                    pc.getInventory().consumeItem(materials[k], counts[k]);
                }
            } else {
                return;
            }
        }
        // 刪除使用道具
        if (_array.get(key)._needCount != 0) {
            pc.getInventory().removeItem(item, _array.get(key)._needCount);
        }
        // 檢查機率
        boolean isOK = false;
        boolean consumeItem = false;
        int[] newitemid = null;
        int[] newcounts = null;
        if (_array.get(key)._random != null) {
            int r = random.nextInt(100) + 1;
            int size = _array.get(key)._random.length;
            if (size == 1) { // 單物品隨機
                if (r <= _array.get(key)._random[0]) {
                    isOK = true;
                }
            } else { // 多物品隨機
                for (int i = 0, c = 0; i < size; i++) {
                    c += _array.get(key)._random[i];
                    // System.out.println("AAAAAA"+_array.get(key)._new_item[i]);
                    if (r <= c) {
                        // System.out.println("BBBBB"+_array.get(key)._new_item[i]);
                        if (_array.get(key)._new_item[i] > 0) { // -1為失敗機率
                            newitemid = new int[]{_array.get(key)._new_item[i]};
                            newcounts = new int[]{_array.get(key)._new_item_counts[i]};
                            isOK = true;
                        } else if (_array.get(key)._new_item[i] < 0) { // -1為失敗機率
                            consumeItem = true;
                            // System.out.println("隨機到的物品ID:"+_array.get(key)._new_item[i]);
                        }
                        break; // 隨機到就跳出
                    }
                }
            }
        }
        if (isOK) { // 隨機成功給予道具
            if (newitemid == null) {
                newitemid = _array.get(key)._new_item;
            }
            if (newcounts == null) {
                newcounts = _array.get(key)._new_item_counts;
            }
            final int[] giveMaterials = (_array.get(key)._new_item);
            if (_array.get(key)._new_Retention == 1) {
                // for (int k = 0; k < materials.length; k++)
                // {//(int[]) aTempData.get(9) != null
                L1Item item2 = ItemTable.get().getTemplate(giveMaterials[0]);
                pc.sendPackets(new S_DeleteInventoryItem(itemG));
                itemG.setItemId(giveMaterials[0]);
                itemG.setIdentified(false); // 防止訊息太多掉線 設置為未鑒定狀態
                itemG.setItem(item2);
                try {
                    CharItemsReading.get().updateItemId_Name(itemG);
                } catch (Exception e) {
                    // _log.error(e.getLocalizedMessage(), e);
                }
                pc.sendPackets(new S_AddItem(itemG));
            } else {
            }
            for (int i = 0; i < newitemid.length; i++) {
                // 刪除被升級物品
                if (_array.get(key)._Integration_count != 0) {
                    L1ItemInstance item2 = ItemTable.get().createItem(newitemid[i]);
                    if (item2.isStackable()) { // 可重疊
                        item2.setCount(newcounts[i]); // 數量
                    } else {
                        item2.setCount(1);
                        /*
                         * if (_array.get(key)._new_Retention == 0)
                         * pc.getInventory().removeItem(itemG,
                         * _array.get(key)._Integration_count);
                         * pc.getInventory().storeItem(item2); }
                         *
                         * if (_array.get(key)._new_Retention == 1) try {
                         * pc.getInventory().removeItem(itemG,
                         * _array.get(key)._Integration_count);
                         * CharItemsReading.get().updateItemId_Name(itemG);
                         *
                         *
                         *
                         *
                         *
                         * } catch (Exception e) {
                         */
                        // _log.error(e.getLocalizedMessage(), e);
                    }
                    // 顯示的內容
                    if (_array.get(key)._msg != null) {
                        pc.sendPackets(new S_SystemMessage(_array.get(key)._msg));
                    } else {
                        pc.sendPackets(new S_ServerMessage(403, item2.getLogName()));
                    }
                    // TODO 紀錄
                    /*
                     * RecordTable.get().recordFailureArmor(pc.getName(),
                     * item.getName(), itemG.getLogName(), itemG.getId(),
                     * itemG.getProctectStatus(), "過", //"成功-得"+item2.getName(),
                     * pc.getNetConnection().getIp().toString());
                     */
                } else {
                    if (itemG.isEquipped()) {
                        itemG.setEquipped(false);
                    }
                    L1Item item2 = ItemTable.get().getTemplate(newitemid[i]);
                    pc.sendPackets(new S_DeleteInventoryItem(itemG));
                    itemG.setItemId(newitemid[i]);
                    itemG.setItem(item2);
                    try {
                        CharItemsReading.get().updateItemId_Name(itemG);
                    } catch (Exception e) {
                        _log.error(e.getLocalizedMessage(), e);
                    }
                    pc.sendPackets(new S_AddItem(itemG));
                    // 顯示的內容
                    if (_array.get(key)._msg != null) {
                        pc.sendPackets(new S_SystemMessage(_array.get(key)._msg));
                    } else {
                        pc.sendPackets(new S_ServerMessage(403, item2.getName()));
                    }
                    // TODO 紀錄
                    /*
                     * RecordTable.get().recordFailureArmor(pc.getName(),
                     * item.getName(), itemG.getLogName(), itemG.getId(),
                     * itemG.getProctectStatus(), "過", //"成功",
                     * pc.getNetConnection().getIp().toString());
                     */
                }
                // 顯示特效
                if (_array.get(key)._gfxId != 0) {
                    int gif = _array.get(key)._gfxId;
                    pc.sendPackets(new S_SkillSound(pc.getId(), gif));
                    pc.broadcastPacketAll(new S_SkillSound(pc.getId(), gif));
                }
            }
        } else {
            // 刪除被升級物品
            if (_array.get(key)._Integration_count != 0 || consumeItem) {
                if (itemG.isEquipped()) {
                    itemG.setEquipped(false);
                }
                int count = _array.get(key)._Integration_count;
                if (count == 0) {
                    count++;
                }
                if (_array.get(key)._falue_Del == 1) {
                    pc.getInventory().removeItem(itemG, count);
                }
                // TODO 紀錄
                /*
                 * RecordTable.get().recordFailureArmor(pc.getName(),
                 * item.getName(), itemG.getLogName(), itemG.getId(),
                 * itemG.getProctectStatus(), "爆", //"失敗刪",
                 * pc.getNetConnection().getIp().toString());
                 */
            } else {
                // TODO 紀錄
                /*
                 * RecordTable.get().recordFailureArmor(pc.getName(),
                 * item.getName(), itemG.getLogName(), itemG.getId(),
                 * itemG.getProctectStatus(), "敗", //"失敗",
                 * pc.getNetConnection().getIp().toString());
                 */
            }
            // 失敗訊息
            if (_array.get(key)._msg_falue != null) {
                pc.sendPackets(new S_SystemMessage(_array.get(key)._msg_falue));
                if (_array.get(key)._falue_Del == 1) {
                    pc.getInventory().removeItem(itemG, 1);
                }
            } else {
                pc.sendPackets(new S_ServerMessage(166, itemG.getName() + " 升級失敗"));
                if (_array.get(key)._falue_Del == 1) {
                    pc.getInventory().removeItem(itemG, 1);
                }
            }
        }
    }

    private static int[] getArray(String s) {
        StringTokenizer st = new StringTokenizer(s, ",");
        int iSize = st.countTokens();
        String sTemp = null;
        int[] iReturn = new int[iSize];
        for (int i = 0; i < iSize; i++) {
            sTemp = st.nextToken();
            iReturn[i] = Integer.parseInt(sTemp);
        }
        return iReturn;
    }

    public static HashMap<Integer, ItemIntegration> getSetList() {
        return _array;
    }

    private void getData() {
        Connection cn = null;
        Statement ps = null;
        ResultSet rs = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.createStatement();
            rs = ps.executeQuery("SELECT * FROM 功能_道具升級update");
            int i = 0;
            while (rs.next()) {
                ItemIntegration item = new ItemIntegration();
                // int id = rs.getInt("id");
                item._item_id = rs.getInt("物品編號");
                item._checkClass = rs.getInt("判定職業");
                item._level = rs.getInt("判定等級");
                item._needCount = rs.getInt("道具需要多少數量");
                item._Integration_ID = rs.getInt("被融合的道具編號");
                item._Integration_count = rs.getInt("被融合的道具數量");
                if ((rs.getString("融合機率") != null) && (!rs.getString("融合機率").equals("")) && (!rs.getString("融合機率").equals("0"))) {
                    item._random = getArray(rs.getString("融合機率"));
                } else {
                    item._random = null;
                }
                if ((rs.getString("融合道具") != null) && (!rs.getString("融合道具").equals("")) && (!rs.getString("融合道具").equals("0"))) {
                    item._materials = getArray(rs.getString("融合道具"));
                } else {
                    item._materials = null;
                }
                if ((rs.getString("融合道具數量") != null) && (!rs.getString("融合道具數量").equals("")) && (!rs.getString("融合道具數量").equals("0"))) {
                    item._counts = getArray(rs.getString("融合道具數量"));
                } else {
                    item._counts = null;
                }
                item._new_Retention = rs.getInt("是否保留數值");
                if ((rs.getString("給予道具") != null) && (!rs.getString("給予道具").equals("")) && (!rs.getString("給予道具").equals("0"))) {
                    item._new_item = getArray(rs.getString("給予道具"));
                } else {
                    item._new_item = null;
                }
                if ((rs.getString("給予道具數量") != null) && (!rs.getString("給予道具數量").equals("")) && (!rs.getString("給予道具數量").equals("0"))) {
                    item._new_item_counts = getArray(rs.getString("給予道具數量"));
                } else {
                    item._new_item_counts = null;
                }
                if ((rs.getString("顯示成功內容") != null) && (!rs.getString("顯示成功內容").equals("")) && (!rs.getString("顯示成功內容").equals("0"))) {
                    item._msg = rs.getString("顯示成功內容");
                } else {
                    item._msg = null;
                }
                if ((rs.getString("顯示失敗內容") != null) && (!rs.getString("顯示失敗內容").equals("")) && (!rs.getString("顯示失敗內容").equals("0"))) {
                    item._msg_falue = rs.getString("顯示失敗內容");
                } else {
                    item._msg_falue = null;
                }
                item._falue_Del = rs.getInt("是否刪除道具");
                item._gfxId = rs.getInt("特效編號");
                _array.put(i, item);
                i++;
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    private static class ItemIntegration {
        private int _item_id;
        private int _checkClass;
        private int _level;
        private int _needCount;
        private int _Integration_ID;
        private int _Integration_count;
        private int[] _random;
        private int[] _materials;
        private int[] _counts;
        private int _new_Retention;
        private int[] _new_item;
        private int[] _new_item_counts;
        private String _msg;
        private String _msg_falue;
        private int _falue_Del;
        private int _gfxId;
    }
}