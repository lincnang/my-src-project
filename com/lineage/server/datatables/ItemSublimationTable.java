package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.CharItemSublimation;
import com.lineage.server.templates.L1ItemSublimation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

/**
 * 昇華系統資料表
 */
public class ItemSublimationTable {

    private static final Log _log = LogFactory.getLog(ItemSublimationTable.class);

    private static final HashMap<Integer, L1ItemSublimation> _array = new HashMap<>();
    private static final HashMap<Integer, String[]> _subName = new HashMap<>();

    private static ItemSublimationTable _instance;

    private ItemSublimationTable() {
        load();
        _log.info("讀取->昇華系統設置載入筆數: " + _array.size());
    }

    public static ItemSublimationTable get() {
        if (_instance == null) {
            _instance = new ItemSublimationTable();
        }
        return _instance;
    }

    /**
     * 載入資料表
     */
    private void load() {
        try (Connection cn = DatabaseFactory.get().getConnection();
             Statement ps = cn.createStatement();
             ResultSet rs = ps.executeQuery("SELECT * FROM 系統_武器防具昇華 ORDER BY SublimationType, SublimationLv")) {

            while (rs.next()) {
                final int id = rs.getInt("id");
                final int itemId = rs.getInt("Item_Id");
                final int type = rs.getInt("SublimationType");
                final int lv = rs.getInt("SublimationLv");
                final int checkType = rs.getInt("CheckType");
                final int checkType2 = rs.getInt("CheckType2");

//                rs.getInt("Unknown1"); // 跳過未使用欄位
//                rs.getInt("Unknown2"); // 跳過未使用欄位

                final String title = rs.getString("Title");
                final int random = rs.getInt("Random");
                final int failureMod = rs.getInt("FailureMod");
                final int successGifid = rs.getInt("SuccessGifid");
                final int triggerChance = rs.getInt("TriggerChance");

                final double dmg = rs.getDouble("Damage");
                final double magicDmg = rs.getDouble("MagicDmg");

                final int dmgChanceHp = rs.getInt("DmgChanceHp");
                final int dmgChanceMp = rs.getInt("DmgChanceMp");

                final boolean withstandDmg = rs.getBoolean("WithstandDmg");
                final boolean withstandMagic = rs.getBoolean("WithstandMagic");
                final boolean returnDmg = rs.getBoolean("ReturnDmg");
                final boolean returnMagic = rs.getBoolean("ReturnMagic");
                final boolean returnSkills = rs.getBoolean("ReturnSkills");

                final int returnChanceHp = rs.getInt("ReturnChanceHp");
                final int returnChanceMp = rs.getInt("ReturnChanceMp");

                final String message = rs.getString("Message");
                final int messageType = rs.getInt("MessageType");
                final int gfxid = rs.getInt("Gfxid");
                final int gfxidType = rs.getInt("GfxidType");

                // 額外名稱快取
                if (type != 0 && lv > 0) {
                    _subName.computeIfAbsent(type, k -> new String[lv]);
                    String[] names = _subName.get(type);
                    if (names.length < lv) {
                        String[] newNames = new String[lv];
                        System.arraycopy(names, 0, newNames, 0, names.length);
                        names = newNames;
                    }
                    names[lv - 1] = title;
                    _subName.put(type, names);
                }

                final L1ItemSublimation data = new L1ItemSublimation();
                data.setItemId(itemId);
                data.setSublimationType(type);
                data.setSublimationLv(lv);
                data.setCheckType(checkType);
                data.setCheckType2(checkType2);
                data.setTitle(title);
                data.setRandom(random);
                data.setFailureMod(failureMod);
                data.setSuccessGifid(successGifid);
                data.setTriggerChance(triggerChance);
                data.setDamage(dmg);
                data.setMagicDmg(magicDmg);
                data.setDmgChanceHp(dmgChanceHp);
                data.setDmgChanceMp(dmgChanceMp);
                data.setWithstandDmg(withstandDmg);
                data.setWithstandMagic(withstandMagic);
                data.setReturnDmg(returnDmg);
                data.setReturnMagic(returnMagic);
                data.setReturnSkills(returnSkills);
                data.setReturnChanceHp(returnChanceHp);
                data.setReturnChanceMp(returnChanceMp);
                data.setMessage(message);
                data.setMessageType(messageType);
                data.setGfxid(gfxid);
                data.setGfxidType(gfxidType);

                _array.put(id, data);
            }

        } catch (final SQLException e) {
            _log.error("昇華資料讀取失敗", e);
        }
    }


    /**
     * 取得某 itemId 的最大昇華等級
     */
    public static int getItemSublimationMaxLv(final int itemId) {
        int maxLv = 0;
        for (final L1ItemSublimation data : _array.values()) {
            if (data.getItemId() == itemId && data.getSublimationLv() > maxLv) {
                maxLv = data.getSublimationLv();
            }
        }
        return maxLv;
    }

    /**
     * 取得某 itemId + type 的最大昇華等級
     */
    public static int getItemSublimationMaxLv(final int itemId, final int type) {
        int maxLv = 0;
        for (final L1ItemSublimation data : _array.values()) {
            if (data.getItemId() == itemId && data.getSublimationType() == type && data.getSublimationLv() > maxLv) {
                maxLv = data.getSublimationLv();
            }
        }
        return maxLv;
    }

    public static boolean checkItemId(final int itemId) {
        for (final L1ItemSublimation data : _array.values()) {
            if (data.getItemId() == itemId) {
                return true;
            }
        }
        return false;
    }

    public static L1ItemSublimation getItemSublimation(final int itemId, final int lv) {
        for (final L1ItemSublimation data : _array.values()) {
            if (data.getItemId() == itemId && data.getSublimationLv() == lv) {
                return data;
            }
        }
        return null;
    }

    public static L1ItemSublimation getItemSublimation(final int itemId) {
        for (final L1ItemSublimation data : _array.values()) {
            if (data.getItemId() == itemId) {
                return data;
            }
        }
        return null;
    }

    public static L1ItemSublimation getItemSublimation(final CharItemSublimation sublimation) {
        for (final L1ItemSublimation data : _array.values()) {
            if (data.getSublimationType() == sublimation.getType()
                    && data.getSublimationLv() == sublimation.getLv()) {
                return data;
            }
        }
        return null;
    }

    public String getSubName(final int type, final int lv) {
        if (_subName.containsKey(type)) {
            final String[] names = _subName.get(type);
            if (lv - 1 >= 0 && lv - 1 < names.length) {
                return names[lv - 1];
            }
        }
        return null;
    }
}
