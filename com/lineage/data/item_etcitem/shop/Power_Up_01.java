package com.lineage.data.item_etcitem.shop;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.datatables.ItemPowerUpdateTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.CharItemBlessReading;
import com.lineage.server.datatables.lock.CharItemPowerReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1ItemPowerUpdate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.Random;

/**
 * 附魔石類型
 */
public class Power_Up_01 extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(Power_Up_01.class);
    private static final Random _random = new Random();

    /**
     *
     */
    private Power_Up_01() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Power_Up_01();
    }

    /**
     * 道具物件執行
     *
     * @param data 參數
     * @param pc   執行者
     * @param item 物件
     */
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        try {
            // 對像OBJID
            final int targObjId = data[0];
            final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
            if (tgItem == null) {
                return;
            }
            // 取回物件屬性
            final String key = tgItem.getItemId() + "/" + item.getItemId();
            final L1ItemPowerUpdate info = ItemPowerUpdateTable.get().get(key);
            if (info == null) {
                // 79：\f1沒有任何事情發生。
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }
            if (tgItem.isEquipped()) {
                pc.sendPackets(new S_ServerMessage("(預防誤點機制啟動)裝備中無法強化"));// 沒有任何事發生
                return;
            }
            if (info.get_mode() == 5) {// 不能再強化
                // 79：\f1沒有任何事情發生。
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }
            if (info.get_nedid() != item.getItemId()) {
                // 79：\f1沒有任何事情發生。
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }
            // 同組物品清單
            final Map<Integer, L1ItemPowerUpdate> tmplist = ItemPowerUpdateTable.get().get_type_id(key);
            if (tmplist.isEmpty()) {
                // 79：\f1沒有任何事情發生。
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }
            final int order_id = info.get_order_id();// 排序
            final L1ItemPowerUpdate tginfo = tmplist.get(order_id + 1);// 取回下一個排序資料
            if (tginfo == null) {
                // 79：\f1沒有任何事情發生。
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }
            // 刪除卷軸
            pc.getInventory().removeItem(item, 1);
            if (_random.nextInt(1000) <= tginfo.get_random()) {
                // 強化成功
                pc.getInventory().removeItem(tgItem, 1);
                // CreateNewItem.createNewItem(pc, tginfo.get_itemid(), 1);
                // 產生新物件
                final L1ItemInstance tginfo_item = ItemTable.get().createItem(tginfo.get_itemid());
                if (tginfo_item != null) {
                    tginfo_item.setIdentified(true);
                    tginfo_item.setCount(1);
                    // 保留數值
                    tginfo_item.setEnchantLevel(tgItem.getEnchantLevel());
                    //tginfo_item.setIdentified(tgItem.isIdentified());
                    tginfo_item.setIdentified(false); // 防止訊息太多掉線 設置為未鑒定狀態
                    tginfo_item.set_durability(tgItem.get_durability());
                    tginfo_item.setChargeCount(tgItem.getChargeCount());
                    tginfo_item.setRemainingTime(tgItem.getRemainingTime());
                    tginfo_item.setLastUsed(tgItem.getLastUsed());
                    tginfo_item.setBless(tgItem.getBless());
                    tginfo_item.setAttrEnchantKind(tgItem.getAttrEnchantKind());
                    tginfo_item.setAttrEnchantLevel(tgItem.getAttrEnchantLevel());
                    tginfo_item.setItemAttack(tgItem.getItemAttack());
                    tginfo_item.setItemBowAttack(tgItem.getItemBowAttack());
                    tginfo_item.setItemReductionDmg(tgItem.getItemReductionDmg());
                    tginfo_item.setItemSp(tgItem.getItemSp());
                    tginfo_item.setItemprobability(tgItem.getItemprobability());
                    tginfo_item.setItemStr(tgItem.getItemStr());
                    tginfo_item.setItemDex(tgItem.getItemDex());
                    tginfo_item.setItemInt(tgItem.getItemInt());
                    if (tgItem.get_power_name() != null) {
                        tginfo_item.set_power_name(tgItem.get_power_name());
                        // 新建資料
                        CharItemPowerReading.get().storeItem(tginfo_item.getId(), tginfo_item.get_power_name());
                        // 刪除資料
                        CharItemPowerReading.get().delItem(tgItem.getId());
                    }
                    if (tgItem.get_power_bless() != null) {
                        tginfo_item.set_power_bless(tgItem.get_power_bless());
                        // 新建資料
                        CharItemBlessReading.get().storeItem(tginfo_item.getId(), tginfo_item.get_power_bless());
                        // 刪除資料
                        CharItemBlessReading.get().delItem(tgItem.getId());
                    }
                    pc.getInventory().storeItem(tginfo_item);
                    // 對\f1%0附加強大的魔法力量成功。
                    pc.sendPackets(new S_ServerMessage(1410, tgItem.getName()));
                    WriteLogTxt.Recording("道具升級紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【 " + pc.getName() + " 】的【" + tgItem.getRecordName(tgItem.getCount()) + "(ObjectId: " + tgItem.getId() + ")," + "】升級成功至" + tginfo_item.getRecordName(tginfo_item.getCount()) + "(ObjectId: " + tginfo_item.getId() + ").");
                    return;
                } else {
                    _log.error("給予物件失敗 原因: 指定編號物品不存在(" + tginfo.get_itemid() + ")");
                    return;
                }
            } else {
                // 強化失敗
                switch (info.get_mode()) {
                    case 0:
                        pc.sendPackets(new S_ServerMessage(160, tgItem.getLogName(), "$252", "$248"));
                        WriteLogTxt.Recording("道具升級紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【 " + pc.getName() + " 】的【" + tgItem.getRecordName(tgItem.getCount()) + "】升級失敗，沒任何事情發生。 (ObjectId: " + tgItem.getId() + ").");
                        break;
                    case 1:
                        L1ItemPowerUpdate ole1 = (L1ItemPowerUpdate) tmplist.get(Integer.valueOf(order_id - 1));
                        pc.sendPackets(new S_ServerMessage("\\fR" + tgItem.getName() + "升級失敗!"));
                        pc.getInventory().removeItem(tgItem, 1L);
                        CreateNewItem.createNewItem(pc, ole1.get_itemid(), 1L);
                        WriteLogTxt.Recording("道具升級紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【 " + pc.getName() + " 】的【" + tgItem.getRecordName(tgItem.getCount()) + "】升級失敗，退階了(不保留數值)。 (ObjectId: " + tgItem.getId() + ").");
                        break;
                    case 2:
                        pc.sendPackets(new S_ServerMessage(164, tgItem.getLogName(), "$252"));
                        pc.getInventory().removeItem(tgItem, 1L);
                        WriteLogTxt.Recording("道具升級紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【 " + pc.getName() + " 】的【" + tgItem.getRecordName(tgItem.getCount()) + "】升級失敗，道具消失了。 (ObjectId: " + tgItem.getId() + ").");
                        break;
                    case 3:
                        if (_random.nextBoolean()) {
                            L1ItemPowerUpdate ole2 = (L1ItemPowerUpdate) tmplist.get(Integer.valueOf(order_id - 1));
                            pc.getInventory().removeItem(tgItem, 1L);
                            CreateNewItem.createNewItem(pc, ole2.get_itemid(), 1L);
                            WriteLogTxt.Recording("道具升級紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【 " + pc.getName() + " 】的【" + tgItem.getRecordName(tgItem.getCount()) + "】升級失敗，退階了(數值不保留)。 (ObjectId: " + tgItem.getId() + ").");
                        } else {
                            pc.sendPackets(new S_ServerMessage(160, tgItem.getLogName(), "$252", "$248"));
                        }
                        break;
                    case 4:
                        if (_random.nextBoolean()) {
                            L1ItemPowerUpdate ole2 = (L1ItemPowerUpdate) tmplist.get(Integer.valueOf(order_id - 1));
                            pc.getInventory().removeItem(tgItem, 1L);
                            CreateNewItem.createNewItem(pc, ole2.get_itemid(), 1L);
                            WriteLogTxt.Recording("道具升級紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【 " + pc.getName() + " 】的【" + tgItem.getRecordName(tgItem.getCount()) + "】升級失敗，退階了(數值不保留)。 (ObjectId: " + tgItem.getId() + ").");
                        } else {
                            pc.sendPackets(new S_ServerMessage(164, tgItem.getLogName(), "$252"));
                            pc.getInventory().removeItem(tgItem, 1L);
                        }
                        break;
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}