package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.config.ConfigBoxMsg;
import com.lineage.data.event.BoxAllSet;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Box;
import com.lineage.server.templates.L1Item;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import william.server_lv;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 箱子開出物設置
 *
 * @author dexc
 */
public class ItemBoxTable {
    private static final Log _log = LogFactory.getLog(ItemBoxTable.class);
    private static final Random _random = new Random();
    private static final Map<Integer, ArrayList<L1Box>> _box = new HashMap<Integer, ArrayList<L1Box>>();
    private static final Map<Integer, ArrayList<L1Box>> _boxs = new HashMap<Integer, ArrayList<L1Box>>();
    private static final Map<Integer, HashMap<Integer, ArrayList<L1Box>>> _boxkey = new HashMap<Integer, HashMap<Integer, ArrayList<L1Box>>>();
    private static ItemBoxTable _instance;

    public static ItemBoxTable get() {
        if (_instance == null) {
            _instance = new ItemBoxTable();
        }
        return _instance;
    }

    /**
     * 更新物品注記
     *
     */
    private static void updata_name(int key, int itemId) {
        Connection cn = null;
        PreparedStatement ps = null;
        String boxname = ItemTable.get().getTemplate(key).getName();
        String itemname = ItemTable.get().getTemplate(itemId).getName();
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("UPDATE `etcitem_box` SET `name`=? WHERE `box_item_id`=? AND `get_item_id`=?");
            int i = 0;
            ps.setString(++i, boxname + "=>" + itemname);
            ps.setInt(++i, key);
            ps.setInt(++i, itemId);
            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    /**
     * 更新物品注記
     *
     */
    private static void updata_name2(int key, int itemId) {
        Connection cn = null;
        PreparedStatement ps = null;
        String boxname = ItemTable.get().getTemplate(key).getName();
        String itemname = ItemTable.get().getTemplate(itemId).getName();
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("UPDATE `etcitem_boxs` SET `name`=? WHERE `box_item_id`=? AND `get_item_id`=?");
            int i = 0;
            ps.setString(++i, boxname + "=>" + itemname);
            ps.setInt(++i, key);
            ps.setInt(++i, itemId);
            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    /**
     * 更新物品注記
     *
     */
    private static void updata_name3(int key, int itemId) {
        Connection cn = null;
        PreparedStatement ps = null;
        String boxname = ItemTable.get().getTemplate(key).getName();
        String itemname = ItemTable.get().getTemplate(itemId).getName();
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("UPDATE `etcitem_box_key` SET `name`=? WHERE `box_item_id`=? AND `get_item_id`=?");
            int i = 0;
            ps.setString(++i, boxname + "=>" + itemname);
            ps.setInt(++i, key);
            ps.setInt(++i, itemId);
            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    /**
     * 取回盒子物件
     *
     */
    private static void outItem(final L1PcInstance pc, final L1Box box, final String tgitemName) {
        if (box != null) {
            // 找回物品資訊
            final L1Item temp = ItemTable.get().getTemplate(box.get_item_id());
            // 給予隨機的數量
            int count = 1;
            if (box.get_min_count() < box.get_max_count()) {
                count = _random.nextInt(box.get_max_count() - box.get_min_count()) + box.get_min_count();
            } else {
                count = box.get_min_count();
            }
            L1ItemInstance item = null;
            int enchantlvl = box.get_enchant();  //src053
            if (temp.isStackable()) {
                item = ItemTable.get().createItem(box.get_item_id());
                item.setCount(count);
                if (enchantlvl > 0) {
                    item.setEnchantLevel(enchantlvl);
                }
                server_lv.forIntensifyArmor(pc, item);
                createNewItem(pc, item);
            } else {
                for (int i = 0; i < count; i++) {
                    item = ItemTable.get().createItem(box.get_item_id());
                    if (enchantlvl > 0) {
                        item.setEnchantLevel(enchantlvl);
                    }
                    server_lv.forIntensifyArmor(pc, item);
                    createNewItem(pc, item);
                }
            }
			/*if (item != null && box.is_out()) {
				final String itemName = item.getName();
				World.get().broadcastPacketToAll(
						new S_ServerMessage(" \\fW恭喜玩家 : \\fR" + pc.getName()));
				World.get().broadcastPacketToAll(
						new S_ServerMessage(" \\fW開啟了 【 \\fR" + tgitemName
								+ " \\fW】"));
				World.get().broadcastPacketToAll(
						new S_ServerMessage(" \\fW獲得物品 \\fR 【 " + itemName + " (" + count
								+ ") \\fW】"));
				// 記錄文件檔 by terry0412
				WriteLogTxt.Recording("開箱紀錄",
						"IP("
								+ pc.getNetConnection().getIp()
								+ ")玩家【"
								+ pc.getName()
								+ "】開啟了【"
								+  tgitemName +"】獲得【"+ itemName+"】【"+count
								+ "】個 "+", (ObjId: " + item.getId()
								+ "】.");
			}
		}
	}*/
            if (item != null && box.is_out()) {
                final String itemName = item.getName();
                ConfigBoxMsg.msg(pc.getName(), tgitemName, itemName);
                WriteLogTxt.Recording("開啟寶箱紀錄", "玩家:" + "【" + pc.getName() + "】" + "開啟寶箱" + "【" + tgitemName + "】" + " 獲得物品公告編號:" + "【" + item.getItemId() + "】");
                RecordTable.get().recordbox(pc.getName(), tgitemName, itemName, count);
            }
        }
    }

    /**
     * 給予物件的處理
     *
     */
    private static void createNewItem(final L1PcInstance pc, final L1ItemInstance item) {
        try {
            pc.getInventory().storeItem(item);
            pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // 獲得0%。
            pc.saveInventory();
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void load() {
        _box.clear();
        _boxs.clear();
        _boxkey.clear();
        if (BoxAllSet.START) {
            // load_box_all();
        } else {
            load_box();
            load_boxs();
        }
        load_box_key();
    }

    public void load_box() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int i = 0;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `etcitem_box`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int key = rs.getInt("box_item_id");
                // 找回物品資訊
                final L1Item temp = ItemTable.get().getTemplate(key);
                if (temp == null) {
                    del_box(key);
                    continue;
                }
                if (temp.getType() != 16) { // treasure_box
                    // del_box(key);
                    continue;
                }
                final int get_item_id = rs.getInt("get_item_id");
                String note = rs.getString("name");// 物品注記
                // 找回物品資訊
                final L1Item tempget = ItemTable.get().getTemplate(key);
                if (tempget == null) {
                    del_box2(key);
                    continue;
                }
                final L1Box box = new L1Box();
                box.set_box_item_id(key);
                box.set_get_item_id(get_item_id);
                box.set_randomint(rs.getInt("randomint"));
                box.set_random(rs.getInt("random"));
                box.set_min_count(rs.getInt("min_count"));
                box.set_max_count(rs.getInt("max_count"));
                box.set_enchant(rs.getInt("enchant"));    //src053
                box.set_out(rs.getBoolean("out"));
                ArrayList<L1Box> list = _box.get(key);
                if (list == null) {
                    list = new ArrayList<L1Box>();
                }
                list.add(box);
                _box.put(key, list);
                i++;
                if (!note.contains("=>")) {
                    updata_name(key, get_item_id);
                }
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->箱子開出物設置: " + _box.size() + "/" + i + "(" + timer.get() + "ms)");
    }

    private void del_box(final int id) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `etcitem_box` WHERE `box_item_id`=?");
            ps.setInt(1, id);
            ps.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    private void del_box2(final int get_item_id) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `etcitem_box` WHERE `get_item_id`=?");
            ps.setInt(1, get_item_id);
            ps.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    private void load_boxs() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int i = 0;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `etcitem_boxs`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                final int key = rs.getInt("box_item_id");
                // 找回物品資訊
                final L1Item temp = ItemTable.get().getTemplate(key);
                if (temp == null) {
                    del_boxs(key);
                    continue;
                }
                if (temp.getType() != 16) { // treasure_box
                    // del_boxs(key);
                    continue;
                }
                final int get_item_id = rs.getInt("get_item_id");
                String note = rs.getString("name");// 物品注記
                // 找回物品資訊
                final L1Item tempget = ItemTable.get().getTemplate(key);
                if (tempget == null) {
                    del_boxs2(key);
                    continue;
                }
                final L1Box box = new L1Box();
                box.set_box_item_id(key);
                box.set_get_item_id(get_item_id);
                box.set_randomint(100);
                box.set_random(-1);
                int count = rs.getInt("count");
                box.set_min_count(count);
                box.set_max_count(count);
                box.set_out(false);
                //box.set_enchantlvl(rs.getInt("enchantlvl"));
                box.set_use_type(rs.getInt("use_type"));
                box.set_broad(rs.getInt("broad"));
                box.set_enchant(rs.getInt("enchant"));    //src053
                box.set_out(rs.getBoolean("out"));
                ArrayList<L1Box> list = _boxs.get(key);
                if (list == null) {
                    list = new ArrayList<L1Box>();
                }
                list.add(box);
                _boxs.put(key, list);
                i++;
                if (!note.contains("=>")) {
                    updata_name2(key, get_item_id);
                }
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->箱子開出物設置(多種): " + _boxs.size() + "/" + i + "(" + timer.get() + "ms)");// */
    }

    private void del_boxs(final int id) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `etcitem_boxs` WHERE `box_item_id`=?");
            ps.setInt(1, id);
            ps.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    private void del_boxs2(final int get_item_id) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `etcitem_boxs` WHERE `get_item_id`=?");
            ps.setInt(1, get_item_id);
            ps.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    private void load_box_key() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int i = 0;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `etcitem_box_key`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                final int keyId = rs.getInt("key_itemid");
                final L1Item tempkey = ItemTable.get().getTemplate(keyId);
                if (tempkey == null) {
                    del_box_key(keyId);
                    continue;
                }
                final int key = rs.getInt("box_item_id");
                String note = rs.getString("name");// 物品注記
                // 找回物品資訊
                final L1Item temp = ItemTable.get().getTemplate(key);
                if (temp == null) {
                    del_box_key2(key);
                    continue;
                }
                if (temp.getType() != 16) { // treasure_box
                    // del_box_key2(key);
                    continue;
                }
                final int get_item_id = rs.getInt("get_item_id");
                final L1Item tempget = ItemTable.get().getTemplate(get_item_id);
                if (tempget == null) {
                    del_box_key3(get_item_id);
                    continue;
                }
                final L1Box box = new L1Box();
                box.set_box_item_id(key);
                box.set_get_item_id(get_item_id);
                box.set_randomint(rs.getInt("randomint"));
                box.set_random(rs.getInt("random"));
                box.set_min_count(rs.getInt("min_count"));
                box.set_max_count(rs.getInt("max_count"));
                box.set_out(rs.getBoolean("out"));
                box.set_use_type(127);
                HashMap<Integer, ArrayList<L1Box>> map = _boxkey.get(key);
                if (map == null) {
                    map = new HashMap<Integer, ArrayList<L1Box>>();
                }
                ArrayList<L1Box> keylist = map.get(keyId);
                if (keylist == null) {
                    keylist = new ArrayList<L1Box>();
                }
                keylist.add(box);
                map.put(keyId, keylist);
                _boxkey.put(key, map);
                i++;
                if (!note.contains("=>")) {
                    updata_name3(key, get_item_id);
                }
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->箱子開出物設置(指定使用物品開啟): " + _boxkey.size() + "/" + i + "(" + timer.get() + "ms)");// */
    }

    private void del_box_key(final int key_itemid) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `etcitem_box_key` WHERE `key_itemid`=?");
            ps.setInt(1, key_itemid);
            ps.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    private void del_box_key2(final int box_item_id) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `etcitem_box_key` WHERE `box_item_id`=?");
            ps.setInt(1, box_item_id);
            ps.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    private void del_box_key3(final int get_item_id) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `etcitem_box_key` WHERE `get_item_id`=?");
            ps.setInt(1, get_item_id);
            ps.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    public ArrayList<L1Box> get(final L1PcInstance pc, final L1ItemInstance tgitem) {
        try {
            final ArrayList<L1Box> list = _box.get(tgitem.getItemId());
            if (list != null) {
                final BoxRandom boxs = new BoxRandom(pc, tgitem, list);
                boxs.getStart();
                return list;
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    public void get_all(final L1PcInstance pc, final L1ItemInstance tgitem) {
        try {
            final ArrayList<L1Box> list = _boxs.get(tgitem.getItemId());
            if (list != null) {
                if (list.size() <= 0) {
                    return;
                }
                if (list.isEmpty()) {
                    return;
                }
                int MaxChargeCount = tgitem.getItem().getMaxChargeCount();
                int chargecount = tgitem.getChargeCount();
                final String name = tgitem.getName();
                int newchargecount = chargecount - 1;
                if (MaxChargeCount > 0) {
                    if (chargecount > 0) {
                        tgitem.setChargeCount(newchargecount);
                        pc.getInventory().updateItem(tgitem, 128);
                        for (L1Box box : list) {
                            if (!box.is_use(pc)) {
                                continue;
                            }
                            outItem(pc, box, name);
                        }
                    }
                    if (newchargecount <= 0) {
                        pc.getInventory().deleteItem(tgitem);
                    }
                } else if (pc.getInventory().removeItem(tgitem, 1) == 1) {// 刪除道具
                    for (L1Box box : list) {
                        if (!box.is_use(pc)) {
                            continue;
                        }
                        outItem(pc, box, name);
                    }
                }
            } else {
                // 沒有任何事情發生
                pc.sendPackets(new S_ServerMessage(79));
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 是否是正確鑰匙
     *
     */
    public boolean is_key(final int tgid, final int keyid) {
        final HashMap<Integer, ArrayList<L1Box>> map = _boxkey.get(tgid);
        if (map != null) {
            final ArrayList<L1Box> keylist = map.get(keyid);
            if (keylist != null) {
                return true;
            }
        }
        return false;
    }

    public void get_key(final L1PcInstance pc, final L1ItemInstance tgitem, final int keyid) {
        try {
            final HashMap<Integer, ArrayList<L1Box>> map = _boxkey.get(tgitem.getItemId());
            if (map != null) {
                final ArrayList<L1Box> keylist = map.get(keyid);
                if (keylist != null) {
                    if (keylist.size() <= 0) {
                        return;
                    }
                    if (keylist.isEmpty()) {
                        return;
                    }
                    final BoxRandom boxs = new BoxRandom(pc, tgitem, keylist);
                    boxs.getStart();
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 新增 etcitem_box
     *
     */
    public void set_box(final int box_item_id, final int get_item_id, final String name, final int randomint, final int random, final int min_count, final int max_count, int enchant) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("INSERT INTO `etcitem_box` SET `box_item_id`=?,`get_item_id`=?," + "`name`=?,`randomint`=?,`random`=?," + "`min_count`=?,`max_count`=?," + "`enchant`=?,`out`=?");
            int i = 0;
            ps.setInt(++i, box_item_id);
            ps.setInt(++i, get_item_id);
            ps.setString(++i, name);
            ps.setInt(++i, randomint);
            ps.setInt(++i, random);
            ps.setInt(++i, min_count);
            ps.setInt(++i, max_count);
            ps.setInt(++i, enchant);
            ps.setBoolean(++i, false);
            ps.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    /**
     * 新增 etcitem_boxs
     *
     */
    public void set_boxs(int box_item_id, int get_item_id, String name, int count, int broad, int enchant, int out) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("INSERT INTO `etcitem_boxs` SET `box_item_id`=?,`get_item_id`=?,`name`=?,`count`=?,`use_type`=?,`broad`=?,`enchant`=?,`out`=?");
            int i = 0;
            ps.setInt(++i, box_item_id);
            ps.setInt(++i, get_item_id);
            ps.setString(++i, name);
            ps.setInt(++i, count);
            ps.setInt(++i, 127);
            ps.setInt(++i, broad);
            ps.setInt(++i, enchant);
            ps.setInt(++i, out);
            ps.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    private static class BoxRandom implements Runnable {
        private final L1PcInstance _pc;
        private final ArrayList<L1Box> _list_tmp = new ArrayList<L1Box>();
        private final L1ItemInstance _tgitem;

        public BoxRandom(final L1PcInstance pc, final L1ItemInstance tgitem, final ArrayList<L1Box> list) {
            _pc = pc;
            _tgitem = tgitem;
            _list_tmp.addAll(list);
        }

        /**
         * 啟動
         *
         */
        public void getStart() {
            GeneralThreadPool.get().schedule(this, 0);
        }

        @Override
        public void run() {
            try {
                if (_list_tmp.size() <= 0) {
                    return;
                }
                if (_list_tmp.isEmpty()) {
                    return;
                }
                // 次數紀錄(獲取物品ID/次數)
                final Map<Integer, Integer> tempList = new HashMap<Integer, Integer>();
                L1Box box = null;
                int i = 0;
                while (box == null) {
                    box = runItem(tempList);
                    TimeUnit.MILLISECONDS.sleep(1);
                    i++;
                    if (i >= 300) {
                        final Object[] obj = _list_tmp.toArray();
                        box = (L1Box) obj[_random.nextInt(obj.length)];
                        if (box != null) {
                            break;
                        }
                    }
                }
                if (box != null) {
                    int MaxChargeCount = _tgitem.getItem().getMaxChargeCount();// 最大使用次數
                    int chargecount = _tgitem.getChargeCount();// 剩餘使用次數
                    String name = _tgitem.getName();
                    int newchargecount = chargecount - 1;
                    if (MaxChargeCount > 0) {
                        if (chargecount > 0) {
                            _tgitem.setChargeCount(newchargecount);
                            _pc.getInventory().updateItem(_tgitem, 128);
                            ItemBoxTable.outItem(_pc, box, name);
                        }
                        if (newchargecount <= 0) {
                            _pc.getInventory().deleteItem(_tgitem);
                        }
                    } else if (_pc.getInventory().removeItem(_tgitem, 1L) == 1L) {
                        ItemBoxTable.outItem(_pc, box, name);
                    }
                }
            } catch (final Exception e) {
                _log.error("寶盒物品設置或取率可能太低,本次開啟未獲得任何物品(寶盒不會被刪除) 寶盒編號:" + _tgitem.getItemId());
            }
        }

        /**
         * 開始計算機率並傳回獲得物品
         *
         */
        private L1Box runItem(final Map<Integer, Integer> tempList) {
            try {
                if (_list_tmp.size() <= 0) {
                    return null;
                }
                if (_list_tmp.isEmpty()) {
                    return null;
                }
                final int index = _random.nextInt(_list_tmp.size());
                // 抽出隨機物件
                final L1Box score = _list_tmp.get(index);
                final int random = _random.nextInt(score.get_randomint());// 比對機率亂數
                final int srcrandom = score.get_random();// 獲取機率
                if (random < srcrandom) {// 比對機率亂數 < 獲取機率
                    return score;// 獲得物品
                } else {// 計算機率後獲取失敗
                    Integer tmp = tempList.get(score.get_item_id());// 取回已嘗試次數紀錄
                    if (tmp != null) {// 已有次數紀錄
                        tmp++;// 嘗試次數+1
                        tempList.put(score.get_item_id(), tmp);
                    } else {// 無次數計錄
                        tmp = 1;
                        tempList.put(score.get_item_id(), tmp);
                    }
                    boolean isremove = false;
                    if (srcrandom < 5000) {// 以5000為單位計算一次機率
                        isremove = true;
                    } else if (srcrandom >= 5000 && srcrandom < 10000) {
                        if (tmp > 2) {
                            isremove = true;
                        }
                    } else if (srcrandom >= 10000 && srcrandom < 20000) {
                        if (tmp > 4) {
                            isremove = true;
                        }
                    } else if (srcrandom >= 20000 && srcrandom < 40000) {
                        if (tmp > 8) {
                            isremove = true;
                        }
                    } else if (srcrandom >= 40000 && srcrandom < 80000) {
                        if (tmp > 16) {
                            isremove = true;
                        }
                    } else if (srcrandom >= 80000 && srcrandom < 160000) {
                        if (tmp > 32) {
                            isremove = true;
                        }
                    } else if (srcrandom >= 160000 && srcrandom < 320000) {
                        if (tmp > 64) {
                            isremove = true;
                        }
                    }
                    if (isremove) {
                        _list_tmp.remove(score);// 移出可獲得物品列表
                    }
                }
            } catch (final Exception e) {
                _log.error(e.getLocalizedMessage(), e);
                return null;
            }
            return null;
        }
    }
}