package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.storage.CharacterStorage;
import com.lineage.server.storage.mysql.MySqlCharacterStorage;
import com.lineage.server.templates.L1CharName;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CharacterTable {
    private static final Log _log = LogFactory.getLog(CharacterTable.class);
    private static final Map<String, L1CharName> _charNameList = new HashMap<String, L1CharName>();
    private static CharacterTable _instance;
    private CharacterStorage _charStorage;

    private CharacterTable() {
        _charStorage = new MySqlCharacterStorage();
    }

    public static CharacterTable get() {
        if (_instance == null) {
            _instance = new CharacterTable();
        }
        return _instance;
    }

    public static void clearSpeedError() {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE `characters` SET `SpeedError`='0'");
            pstm.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public static void clearBanError() {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE `characters` SET `BanError`='0'");
            pstm.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public static void clearInputBanError() {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE `characters` SET `InputBanError`='0'");
            pstm.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public static void clearOnlineStatus() {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE `characters` SET `OnlineStatus`='0'");
            pstm.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public static void updateOnlineStatus(L1PcInstance pc) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE `characters` SET `OnlineStatus`=? WHERE `objid`=?");
            pstm.setInt(1, pc.getOnlineStatus());
            pstm.setInt(2, pc.getId());
            pstm.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public static void updatePartnerId(int targetId) {
        updatePartnerId(targetId, 0);
    }

    public static void updatePartnerId(int targetId, int partnerId) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE `characters` SET `PartnerID`=? WHERE `objid`=?");
            pstm.setInt(1, partnerId);
            pstm.setInt(2, targetId);
            pstm.execute();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public static void saveCharStatus(L1PcInstance pc) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE `characters` SET `OriginalStr`=?,`OriginalCon`=?,`OriginalDex`=?,`OriginalCha`=?,`OriginalInt`=?,`OriginalWis`=? WHERE `objid`=?");
            pstm.setInt(1, pc.getBaseStr());
            pstm.setInt(2, pc.getBaseCon());
            pstm.setInt(3, pc.getBaseDex());
            pstm.setInt(4, pc.getBaseCha());
            pstm.setInt(5, pc.getBaseInt());
            pstm.setInt(6, pc.getBaseWis());
            pstm.setInt(7, pc.getId());
            pstm.execute();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    /**
     * 重建人物背包及倉庫資料
     *
     */
    public static void restoreInventory(L1PcInstance pc) {
        pc.getInventory().loadItems();
        pc.getDwarfInventory().loadItems();
        pc.getDwarfForChaInventory().loadItems();
        pc.getDwarfForElfInventory().loadItems();
        pc.getDwarfForGameMall().loadItems();
        pc.getPandoraInventory().loadItems();//潘朵拉抽抽樂倉庫存取
        pc.getRemoveItemInventory().loadItems();//輔助(自動刪物)倉庫存取
    }

    /**
     * 此名稱是否存在資料庫中
     *
     */
    public static boolean doesCharNameExist(String name) {
        boolean result = true;
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT `account_name` FROM `characters` WHERE `char_name`=?");
            pstm.setString(1, name);
            rs = pstm.executeQuery();
            result = rs.next();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return result;
    }

    /**
     * 載入所有角色名稱資料
     */
    public static void loadAllCharName() {
        L1CharName cn = null;
        String name = null;
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `characters`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                cn = new L1CharName();
                name = rs.getString("char_name");
                cn.setName(name);
                cn.setId(rs.getInt("objid"));
                _charNameList.put(name, cn);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    /**
     * 傳回此角色名稱的帳號
     *
     */
    public static String getAccountName(String name) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT `account_name` FROM `characters` WHERE `char_name`=?");
            pstm.setString(1, name);
            rs = pstm.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        SQLUtil.close(rs);
        SQLUtil.close(pstm);
        SQLUtil.close(con);
        return "";
    }

    /**
     * 傳回此角色名稱的帳號
     *
     */
    public static HashMap<Integer, String> getAllNameByLevel(int level) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        HashMap<Integer, String> maps = new HashMap<Integer, String>();
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT `char_name` FROM `characters` WHERE `level`>=" + level + " ");
            rs = pstm.executeQuery();
            int i = 0;
            while (rs.next()) {
                maps.put(i, rs.getString(1));
                i++;
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        SQLUtil.close(rs);
        SQLUtil.close(pstm);
        SQLUtil.close(con);
        return maps;
    }

    /**
     * 更新戒指欄位擴充狀態 by terry0412
     *
     */
    public static void updateRingsExpansion(final L1PcInstance pc) {  //src013
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE `characters` SET `RingsExpansion`=? WHERE `objid`=?");
            pstm.setByte(1, pc.getRingsExpansion());
            pstm.setInt(2, pc.getId());
            pstm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public static void updateEarringsExpansion(final L1PcInstance pc) {  //src014
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE `characters` SET `EarringsExpansion`=? WHERE `objid`=?");
            pstm.setByte(1, pc.getEarringsExpansion());
            pstm.setInt(2, pc.getId());
            pstm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    // src1003 符文擴充
    public static void updateEquipmentIndexAmulet(final L1PcInstance pc) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE `characters` SET `EquipmentIndexAmulet`=? WHERE `objid`=?");
            pstm.setByte(1, pc.getEquipmentIndexAmulet());
            pstm.setInt(2, pc.getId());
            pstm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public static void updateRedblueReward(final L1PcInstance pc) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE `characters` SET `RedblueReward`=? WHERE `objid`=?");
            pstm.setByte(1, pc.getRedblueReward());
            pstm.setInt(2, pc.getId());
            pstm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public static void resetRedblueReward() {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE `characters` SET `RedblueReward`='0'");
            pstm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    /**
     * 新增角色資料
     *
     */
    public void storeNewCharacter(L1PcInstance pc) throws Exception {
        synchronized (pc) {
            _charStorage.createCharacter(pc);
            String name = pc.getName();
            if (!_charNameList.containsKey(name)) {
                L1CharName cn = new L1CharName();
                cn.setName(name);
                cn.setId(pc.getId());
                _charNameList.put(name, cn);
            }
        }
    }

    /**
     * 更新角色的DB資料
     *
     */
    public void storeCharacter(L1PcInstance pc) throws Exception {
        synchronized (pc) {
            _charStorage.storeCharacter(pc);
        }
    }

    /**
     * 更新角色的VIP DB資料
     *
     */
    public void updateVipTime(L1PcInstance pc) throws Exception {
        synchronized (pc) {
            _charStorage.updateVipTime(pc);
        }
    }

    public void deleteCharacter(String accountName, String charName) throws Exception {
        _charStorage.deleteCharacter(accountName, charName);
        if (_charNameList.containsKey(charName)) {
            _charNameList.remove(charName);
        }
    }

    public L1PcInstance restoreCharacter(String charName) throws Exception {
        L1PcInstance pc = _charStorage.loadCharacter(charName);
        return pc;
    }

    public L1PcInstance loadCharacter(String charName) {
        L1PcInstance pc = null;
        try {
            pc = restoreCharacter(charName);
            if (pc != null) {
                L1Map map = L1WorldMap.get().getMap(pc.getMapId());
                if (!map.isInMap(pc.getX(), pc.getY())) {
                    pc.setX(33087);
                    pc.setY(33396);
                    pc.setMap((short) 4);
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return pc;
    }

    public L1PcInstance loadCharacter(int objid) {
        return _charStorage.loadCharacter(objid);
    }

    /**
     * 新建角色名稱資料
     *
     */
    public void newCharName(int objid, String name) {
        L1CharName cn = new L1CharName();
        cn.setName(name);
        cn.setId(objid);
        _charNameList.put(name, cn);
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE `characters` SET `char_name`=? WHERE `objid`=?");
            pstm.setString(1, name);
            pstm.setInt(2, objid);
            pstm.execute();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    /**
     * 傳回所有角色名稱列表
     *
     */
    public L1CharName[] getCharNameList() {
        return (L1CharName[]) _charNameList.values().toArray(new L1CharName[_charNameList.size()]);
    }

    /**
     * 依照objid取回人物名稱
     *
     */
    public String getCharName(int objid) {
        for (L1CharName charName : _charNameList.values()) {
            if (charName.getId() == objid) {
                return charName.getName();
            }
        }
        return null;
    }
}
