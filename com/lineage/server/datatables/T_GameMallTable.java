package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.serverpackets.S_GameMall;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.T_GameMallModel;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class T_GameMallTable {
    public static final int _unit = 44070;
    private static final Log _log = LogFactory.getLog(T_GameMallTable.class);
    private static T_GameMallTable _instance;
    private final HashMap<Integer, T_GameMallModel> _mallList = new HashMap<>();
    private final ArrayList<ServerBasePacket> _paketList = new ArrayList<>();

    private T_GameMallTable() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM 功能_潘朵拉商城表");
            rs = pstm.executeQuery();
            T_GameMallModel model = null;
            L1ItemInstance item = null;
            while (rs.next()) {
                final int id = rs.getInt("商城道具序號");
                final int itemId = rs.getInt("道具編號");
                final int itemCount = rs.getInt("物品數量");
                final int enchantLevel = rs.getInt("物品強化等級");
                final int bless = rs.getInt("物品祝福狀態");
                final int shopItemDesc = rs.getInt("道具商城信息ID");
                final int itemPrice = rs.getInt("道具價格");
                final int itemSort = rs.getInt("商城分類");
                final boolean newItem = rs.getBoolean("是否新道具");
                final int vipLevel = rs.getInt("需要VIP");
                final boolean hotItem = rs.getBoolean("是否熱門道具");
                final String note = rs.getString("道具說明");
                if (check_item(itemId, note)) {
                    item = ItemTable.get().createItem(itemId, false);
                    if (item != null) {
                        item.setCount(itemCount);
                        item.setEnchantLevel(enchantLevel);
                        item.setBless(bless);
                        model = new T_GameMallModel(id, item, shopItemDesc, itemPrice, itemSort, newItem, vipLevel, hotItem);
                        _mallList.put(id, model);
                    }
                }
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->功能_潘朵拉商城表數量: " + _mallList.size() + "(" + timer.get() + "ms)");
    }

    public static T_GameMallTable get() {
        if (_instance == null) {
            _instance = new T_GameMallTable();
        }
        return _instance;
    }

    /**
     * 刪除錯誤物品資料
     *
     */
    private static void errorItem(int itemid) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("DELETE FROM `功能_潘朵拉商城表` WHERE `道具編號`=?");
            pstm.setInt(1, itemid);
            pstm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void restGameMall() {  //src036
        _mallList.clear();
        load();
    }

    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM 功能_潘朵拉商城表");
            rs = pstm.executeQuery();
            T_GameMallModel model = null;
            L1ItemInstance item = null;
            while (rs.next()) {
                final int id = rs.getInt("商城道具序號");
                final int itemId = rs.getInt("道具編號");
                final int itemCount = rs.getInt("物品數量");
                final int enchantLevel = rs.getInt("物品強化等級");
                final int bless = rs.getInt("物品祝福狀態");
                final int shopItemDesc = rs.getInt("道具商城信息ID");
                final int itemPrice = rs.getInt("道具價格");
                final int itemSort = rs.getInt("商城分類");
                final boolean newItem = rs.getBoolean("是否新道具");
                final int vipLevel = rs.getInt("需要VIP");
                final boolean hotItem = rs.getBoolean("是否熱門道具");
                final String note = rs.getString("道具說明");
                if (check_item(itemId, note)) {
                    item = ItemTable.get().createItem(itemId, false);
                    if (item != null) {
                        item.setCount(itemCount);
                        item.setEnchantLevel(enchantLevel);
                        item.setBless(bless);
                        model = new T_GameMallModel(id, item, shopItemDesc, itemPrice, itemSort, newItem, vipLevel, hotItem);
                        _mallList.put(id, model);
                    }
                }
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->功能_潘朵拉商城表數量: " + _mallList.size() + "(" + timer.get() + "ms)");
    }

    private boolean check_item(int itemId, String note) {
        final L1Item itemTemplate = ItemTable.get().getTemplate(itemId);
        if (itemTemplate == null) {
            // 無該物品資料 移除
            errorItem(itemId);
            return false;
        } else if (note == null || !note.contains("=>")) {
            String itemname = itemTemplate.getName();
            String itemType = itemTemplate.getType2() == 0 ? "道具" : itemTemplate.getType2() == 1 ? "武器" : "防具";
            // 更新掉落物品名稱
            updata_name(itemname, itemType, itemId);
            return true;
        }
        return true;
    }

    private void updata_name(String itemname, String itemType, int itemId) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("UPDATE `功能_潘朵拉商城表` SET `道具說明`=? WHERE `道具編號`=?");
            int i = 0;
            ps.setString(++i, itemType + "=>" + itemname);
            ps.setInt(++i, itemId);
            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    public T_GameMallModel getMallList(final int id) {
        return _mallList.get(id);
    }

    public ArrayList<ServerBasePacket> getPaketList() {
        if (_paketList.isEmpty() && !_mallList.isEmpty()) {
            final Collection<T_GameMallModel> allValues = _mallList.values();
            int index = 0;
            int page = 0;
            final int modelSize = _mallList.size();
            final int sumPage = (modelSize % 127) == 0 ? modelSize / 127 : (modelSize / 127) + 1;
            final HashMap<Integer, T_GameMallModel> pageBuff = new HashMap<>();
            for (final T_GameMallModel gameMallModel : allValues) {
                pageBuff.put(gameMallModel.getMallId(), gameMallModel);
                index++;
                if (index == 127) {
                    _paketList.add(new S_GameMall(pageBuff, sumPage, page));
                    pageBuff.clear();
                    index = 0;
                    page++;
                }
            }
            if (!pageBuff.isEmpty()) {
                _paketList.add(new S_GameMall(pageBuff, sumPage, page));
            }
        }
        return _paketList;
    }

    public void insertMallRecord(final int objId, final int targetId, final int itemId, final String itemName, final int buyCount, final int sumPrice) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("INSERT INTO character_潘朵拉商城  SET 道具objid=?," + "購買人物ID=?,道具編號=?,道具名稱=?,購買數量=?,購買總價=?,購買時間=?");
            pstm.setInt(1, objId);
            pstm.setInt(2, targetId);
            pstm.setInt(3, itemId);
            pstm.setString(4, itemName);
            pstm.setInt(5, buyCount);
            pstm.setInt(6, sumPrice);
            pstm.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            pstm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }
}