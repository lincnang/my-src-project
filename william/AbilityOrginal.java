package william;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.SQLUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 強化擴充能力
 */
public class AbilityOrginal {
    private static final HashMap<Integer, L1WilliamAbilityOrginal> _gfxIdIndex = new HashMap<>();
    private static Logger _log = Logger.getLogger(AbilityOrginal.class.getName());
    private static AbilityOrginal _instance;

    private AbilityOrginal() {
        this.loadAbilityOrginal();
    }

    public static AbilityOrginal getInstance() {
        if (_instance == null) {
            _instance = new AbilityOrginal();
        }
        return _instance;
    }

    public static void reload() {
        _gfxIdIndex.clear();
        _instance = null;
        getInstance();
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

    private void loadAbilityOrginal() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM 其他_能力擴充系統");
            rs = pstm.executeQuery();
            this.fillWeaponSkill(rs);
        } catch (SQLException e) {
            _log.log(Level.SEVERE, "error while creating 其他_能力擴充系統 table", e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    private void fillWeaponSkill(final ResultSet rs) throws SQLException {
        while (rs.next()) {
            final int itemId = rs.getInt("道具編號");
            int itemType = rs.getInt("道具類型");
            if (itemType > 2) {
                itemType = 2;
            }
            int itemType_Aromr = rs.getInt("裝備類型");
            int itemidok = rs.getInt("是否啟用指定對象道具");
            int[] itemid = getArray(rs.getString("對象道具編號"));
            int evel = rs.getInt("強化值所需");
            int haveAbility = rs.getInt("是否要有該能力才能強化");
            int max_capacity = rs.getInt("能力最大上限值");
            String msg1 = rs.getString("能力上限訊息");
            String msg2 = rs.getString("成功訊息");
            String msg3 = rs.getString("失敗訊息");
            int fixed_Or_Random = rs.getInt("擴充能力是否隨機");
            int cleaning_ability = rs.getInt("點之前是否洗掉能力");
            int random = rs.getInt("擴充機率");
            byte addStr = rs.getByte("力量");
            byte addDex = rs.getByte("敏捷");
            byte addCon = rs.getByte("體質");
            byte addInt = rs.getByte("智力");
            byte addWis = rs.getByte("精神");
            byte addCha = rs.getByte("魅力");
            int addMaxHp = rs.getInt("血量");
            int addMaxMp = rs.getInt("魔量");
            int addMr = rs.getInt("抗魔");
            int addSp = rs.getInt("魔攻");
            int addWeaponDmg = rs.getInt("近戰攻擊");
            int addWeaponHit = rs.getInt("近戰命中");
            int addWeaponBowDmg = rs.getInt("遠攻攻擊");
            int addWeaponBowHit = rs.getInt("遠攻命中");
            int addHpr = rs.getInt("回血");
            int addMpr = rs.getInt("回魔");
            int addAc = rs.getInt("防禦");
            int addFire = rs.getInt("火屬性");
            int addWind = rs.getInt("風屬性");
            int addEarth = rs.getInt("地屬性");
            int addWater = rs.getInt("水屬性");
            int PVPdmg = rs.getInt("pvp攻擊");
            int PVPdmg_R = rs.getInt("pvp減免");
            final L1WilliamAbilityOrginal gfxIdOrginal = new L1WilliamAbilityOrginal(itemId, itemType, itemType_Aromr, itemidok, itemid, evel, haveAbility, max_capacity, msg1, msg2, msg3, fixed_Or_Random, cleaning_ability, random, addStr, addDex, addCon, addInt, addWis, addCha, addMaxHp, addMaxMp, addMr, addSp, addWeaponDmg, addWeaponHit, addWeaponBowDmg, addWeaponBowHit, addHpr, addMpr, addAc, addFire, addWind, addEarth, addWater, PVPdmg, PVPdmg_R);
            _gfxIdIndex.put(itemId, gfxIdOrginal);
        }
    }

    public L1WilliamAbilityOrginal getTemplate(final int gfxId) {
        return _gfxIdIndex.get(gfxId);
    }

    public L1WilliamAbilityOrginal[] getGfxIdList() {
        return _gfxIdIndex.values().toArray(new L1WilliamAbilityOrginal[0]);
    }
}
