package william;

import com.lineage.DatabaseFactory;
import com.lineage.Server;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.utils.SQLUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 元神系統
 *
 * @author WIN7
 */
public class login_Artiface {
    public static final String TOKEN = ",";
    private static ArrayList<ArrayList<Object>> aData = new ArrayList<ArrayList<Object>>();
    private static boolean BUILD_DATA = false;
    private static login_Artiface _instance;

    public static login_Artiface getInstance() {
        if (_instance == null) {
            _instance = new login_Artiface();
        }
        return _instance;
    }

    public static void main(String a[]) {
        while (true) {
            try {
                Server.main(null);
            } catch (Exception ex) {
            }
        }
    }

    public static void forIntensifyArmor(L1PcInstance pc) {
        ArrayList<Object> aTempData = null;
        //L1ItemInstance tgItem = pc.getInventory().getItem(l);
        if (!BUILD_DATA) {
            BUILD_DATA = true;
            getData();
        }
		/*int clan_level = 0;
		if (pc.getClanid() != 0) {
			L1Clan clan = WorldClan.get().getClan(pc.getClanname());
			if (clan != null) {
				clan_level = clan.getClanLevel();
			}
		}*/
        for (int i = 0; i < aData.size(); i++) {
            aTempData = (ArrayList<Object>) aData.get(i);
            if (pc.get_other().getLv_Artifact() == ((Integer) aTempData.get(1)).intValue() && ((Integer) aTempData.get(0)).intValue() == 0) {//目前神器LV幾
                String add1 = "";
                String add2 = "";
                String add3 = "";
                String add4 = "";
                String add5 = "";
                String add6 = "";
                String add7 = "";
                String add8 = "";
                String add9 = "";
                String add10 = "";
                String add11 = "";
                String add12 = "";
                String add13 = "";
                String add14 = "";
                String add15 = "";
                String add16 = "";
                String add17 = "";
                String add18 = "";
                String add19 = "";
                String add20 = "";
                String add21 = "";
                String add22 = "";
                String add23 = "";
                String add24 = "";
                String add25 = "";
                String add26 = "";
                // 增加各能力的選項
                if (((Integer) aTempData.get(2)).intValue() != 0) { // HP
                    pc.addMaxHp(((Integer) aTempData.get(2)).intValue());
                    add1 = "(HP+ " + ((Integer) aTempData.get(2)).intValue() + ")";
                }
                if (((Integer) aTempData.get(3)).intValue() != 0) { // MP
                    pc.addMaxMp(((Integer) aTempData.get(3)).intValue());
                    add2 = "(MP+ " + ((Integer) aTempData.get(3)).intValue() + ")";
                }
                if (((Integer) aTempData.get(4)).intValue() != 0) { // Dmg
                    pc.addDmgup(((Integer) aTempData.get(4)).intValue());
                    add3 = "(傷害+ " + ((Integer) aTempData.get(4)).intValue() + ")";
                }
                if (((Integer) aTempData.get(5)).intValue() != 0) { // BowDmg
                    pc.addBowDmgup(((Integer) aTempData.get(5)).intValue());
                    add4 = "(遠攻傷害+ " + ((Integer) aTempData.get(5)).intValue() + ")";
                }
                if (((Integer) aTempData.get(6)).intValue() != 0) { // Hit
                    pc.addHitup(((Integer) aTempData.get(6)).intValue());
                    add5 = "(命中+ " + ((Integer) aTempData.get(6)).intValue() + ")";
                }
                if (((Integer) aTempData.get(7)).intValue() != 0) { // BowHit
                    pc.addBowHitup(((Integer) aTempData.get(7)).intValue());
                    add6 = "(遠攻命中+ " + ((Integer) aTempData.get(7)).intValue() + ")";
                }
                if (((Integer) aTempData.get(8)).intValue() != 0) { // Mr
                    pc.addMr(((Integer) aTempData.get(8)).intValue());
                    add7 = "(抗魔+ " + ((Integer) aTempData.get(8)).intValue() + ")";
                }
                if (((Integer) aTempData.get(9)).intValue() != 0) { // Sp
                    pc.addSp(((Integer) aTempData.get(9)).intValue());
                    add8 = "(魔攻+ " + ((Integer) aTempData.get(9)).intValue() + ")";
                }
                if (((Integer) aTempData.get(10)).intValue() != 0) { // Ac
                    pc.addAc(-((Integer) aTempData.get(10)).intValue());
                    add9 = "(防禦+ " + ((Integer) aTempData.get(10)).intValue() + ")";
                }
                if (((Integer) aTempData.get(11)).intValue() != 0) { // Fire
                    pc.addFire(((Integer) aTempData.get(11)).intValue());
                    add10 = "(火屬性+ " + ((Integer) aTempData.get(11)).intValue() + ")";
                }
                if (((Integer) aTempData.get(12)).intValue() != 0) { // Wind
                    pc.addWind(((Integer) aTempData.get(12)).intValue());
                    add11 = "(風屬性+ " + ((Integer) aTempData.get(12)).intValue() + ")";
                }
                if (((Integer) aTempData.get(13)).intValue() != 0) { // Earth
                    pc.addEarth(((Integer) aTempData.get(13)).intValue());
                    add12 = "(地屬性+ " + ((Integer) aTempData.get(13)).intValue() + ")";
                }
                if (((Integer) aTempData.get(14)).intValue() != 0) { // Water
                    pc.addWater(((Integer) aTempData.get(14)).intValue());
                    add13 = "(水屬性+ " + ((Integer) aTempData.get(14)).intValue() + ")";
                }
                if (((Integer) aTempData.get(15)).intValue() != 0) { // Str
                    pc.addStr(((Integer) aTempData.get(15)).intValue());
                    add14 = "(力量+ " + ((Integer) aTempData.get(15)).intValue() + ")";
                }
                if (((Integer) aTempData.get(16)).intValue() != 0) { // Dex
                    pc.addDex(((Integer) aTempData.get(16)).intValue());
                    add15 = "(敏捷+ " + ((Integer) aTempData.get(16)).intValue() + ")";
                }
                if (((Integer) aTempData.get(17)).intValue() != 0) { // Con
                    pc.addCon(((Integer) aTempData.get(17)).intValue());
                    add16 = "(體質+ " + ((Integer) aTempData.get(17)).intValue() + ")";
                }
                if (((Integer) aTempData.get(18)).intValue() != 0) { // Wis
                    pc.addWis(((Integer) aTempData.get(18)).intValue());
                    add17 = "(精神+ " + ((Integer) aTempData.get(18)).intValue() + ")";
                }
                if (((Integer) aTempData.get(19)).intValue() != 0) { // Int
                    pc.addInt(((Integer) aTempData.get(19)).intValue());
                    add18 = "(智力+ " + ((Integer) aTempData.get(19)).intValue() + ")";
                }
                if (((Integer) aTempData.get(20)).intValue() != 0) { // Cha
                    pc.addCha(((Integer) aTempData.get(20)).intValue());
                    add19 = "(魅力+ " + ((Integer) aTempData.get(20)).intValue() + ")";
                }
                if (((Integer) aTempData.get(21)).intValue() != 0) { // ReductionDmg
                    pc.addDamageReductionByArmor(((Integer) aTempData.get(21)).intValue());
                    add20 = "(減免傷害+ " + ((Integer) aTempData.get(21)).intValue() + ")";
                }
								/*if (((Integer)aTempData.get(22)).intValue() != 0) { // MagicReductionDmg
									pc.add_Clanmagic_reduction_dmg(((Integer)aTempData.get(22)).intValue());
									add21 = "(減免魔法傷害+ " + ((Integer)aTempData.get(22)).intValue() + ")";
								}*/
								/*if (((Double)aTempData.get(23)).doubleValue() > 0) { // ExpRate
									pc.addExpByArmor(((Double)aTempData.get(23)).doubleValue());
									add22 = "(經驗增加" + ((Double)aTempData.get(23)).doubleValue() + "倍)";
								}*/
                if (((Integer) aTempData.get(24)).intValue() != 0) { // AddHpr
                    pc.addHpr(((Integer) aTempData.get(24)).intValue());
                    add23 = "(回血+ " + ((Integer) aTempData.get(24)).intValue() + ")";
                }
                if (((Integer) aTempData.get(25)).intValue() != 0) { // AddMpr
                    pc.addMpr(((Integer) aTempData.get(25)).intValue());
                    add24 = "(回魔+ " + ((Integer) aTempData.get(25)).intValue() + ")";
                }
                if (((Integer) aTempData.get(26)).intValue() != 0) { // AddWeight
                    pc.addWeightReduction(((Integer) aTempData.get(26)).intValue());
                    add25 = "(負重+ " + ((Integer) aTempData.get(26)).intValue() + ")";
                }
                pc.sendPackets(new S_SystemMessage("\\fY[武器]歷練階級" + pc.get_other().getLv_Artifact() + "階"));
                pc.sendPackets(new S_SystemMessage("\\fU獲得:" + add1 + add2 + add3 + add4 + add5 + add6 + add7 + add8 + add9 + add10 + add11 + add12 + add13 + add14 + add15 + add16 + add17 + add18 + add19 + add20 + add21 + add22 + add23 + add24 + add25 + add26));
                pc.sendPackets(new S_SPMR(pc));
                pc.sendPackets(new S_OwnCharStatus(pc));
                // 找到就跳出迴圈
            }
            //防器
            if (pc.get_other().getLv_Redmg_Artifact() == ((Integer) aTempData.get(1)).intValue() && ((Integer) aTempData.get(0)).intValue() == 1) {//目前元神LV幾
                String add1 = "";
                String add2 = "";
                String add3 = "";
                String add4 = "";
                String add5 = "";
                String add6 = "";
                String add7 = "";
                String add8 = "";
                String add9 = "";
                String add10 = "";
                String add11 = "";
                String add12 = "";
                String add13 = "";
                String add14 = "";
                String add15 = "";
                String add16 = "";
                String add17 = "";
                String add18 = "";
                String add19 = "";
                String add20 = "";
                String add21 = "";
                String add22 = "";
                String add23 = "";
                String add24 = "";
                String add25 = "";
                String add26 = "";
                // 增加各能力的選項
                if (((Integer) aTempData.get(2)).intValue() != 0) { // HP
                    pc.addMaxHp(((Integer) aTempData.get(2)).intValue());
                    add1 = "(HP+ " + ((Integer) aTempData.get(2)).intValue() + ")";
                }
                if (((Integer) aTempData.get(3)).intValue() != 0) { // MP
                    pc.addMaxMp(((Integer) aTempData.get(3)).intValue());
                    add2 = "(MP+ " + ((Integer) aTempData.get(3)).intValue() + ")";
                }
                if (((Integer) aTempData.get(4)).intValue() != 0) { // Dmg
                    pc.addDmgup(((Integer) aTempData.get(4)).intValue());
                    add3 = "(傷害+ " + ((Integer) aTempData.get(4)).intValue() + ")";
                }
                if (((Integer) aTempData.get(5)).intValue() != 0) { // BowDmg
                    pc.addBowDmgup(((Integer) aTempData.get(5)).intValue());
                    add4 = "(遠攻傷害+ " + ((Integer) aTempData.get(5)).intValue() + ")";
                }
                if (((Integer) aTempData.get(6)).intValue() != 0) { // Hit
                    pc.addHitup(((Integer) aTempData.get(6)).intValue());
                    add5 = "(命中+ " + ((Integer) aTempData.get(6)).intValue() + ")";
                }
                if (((Integer) aTempData.get(7)).intValue() != 0) { // BowHit
                    pc.addBowHitup(((Integer) aTempData.get(7)).intValue());
                    add6 = "(遠攻命中+ " + ((Integer) aTempData.get(7)).intValue() + ")";
                }
                if (((Integer) aTempData.get(8)).intValue() != 0) { // Mr
                    pc.addMr(((Integer) aTempData.get(8)).intValue());
                    add7 = "(抗魔+ " + ((Integer) aTempData.get(8)).intValue() + ")";
                }
                if (((Integer) aTempData.get(9)).intValue() != 0) { // Sp
                    pc.addSp(((Integer) aTempData.get(9)).intValue());
                    add8 = "(魔攻+ " + ((Integer) aTempData.get(9)).intValue() + ")";
                }
                if (((Integer) aTempData.get(10)).intValue() != 0) { // Ac
                    pc.addAc(-((Integer) aTempData.get(10)).intValue());
                    add9 = "(防禦+ " + ((Integer) aTempData.get(10)).intValue() + ")";
                }
                if (((Integer) aTempData.get(11)).intValue() != 0) { // Fire
                    pc.addFire(((Integer) aTempData.get(11)).intValue());
                    add10 = "(火屬性+ " + ((Integer) aTempData.get(11)).intValue() + ")";
                }
                if (((Integer) aTempData.get(12)).intValue() != 0) { // Wind
                    pc.addWind(((Integer) aTempData.get(12)).intValue());
                    add11 = "(風屬性+ " + ((Integer) aTempData.get(12)).intValue() + ")";
                }
                if (((Integer) aTempData.get(13)).intValue() != 0) { // Earth
                    pc.addEarth(((Integer) aTempData.get(13)).intValue());
                    add12 = "(地屬性+ " + ((Integer) aTempData.get(13)).intValue() + ")";
                }
                if (((Integer) aTempData.get(14)).intValue() != 0) { // Water
                    pc.addWater(((Integer) aTempData.get(14)).intValue());
                    add13 = "(水屬性+ " + ((Integer) aTempData.get(14)).intValue() + ")";
                }
                if (((Integer) aTempData.get(15)).intValue() != 0) { // Str
                    pc.addStr(((Integer) aTempData.get(15)).intValue());
                    add14 = "(力量+ " + ((Integer) aTempData.get(15)).intValue() + ")";
                }
                if (((Integer) aTempData.get(16)).intValue() != 0) { // Dex
                    pc.addDex(((Integer) aTempData.get(16)).intValue());
                    add15 = "(敏捷+ " + ((Integer) aTempData.get(16)).intValue() + ")";
                }
                if (((Integer) aTempData.get(17)).intValue() != 0) { // Con
                    pc.addCon(((Integer) aTempData.get(17)).intValue());
                    add16 = "(體質+ " + ((Integer) aTempData.get(17)).intValue() + ")";
                }
                if (((Integer) aTempData.get(18)).intValue() != 0) { // Wis
                    pc.addWis(((Integer) aTempData.get(18)).intValue());
                    add17 = "(精神+ " + ((Integer) aTempData.get(18)).intValue() + ")";
                }
                if (((Integer) aTempData.get(19)).intValue() != 0) { // Int
                    pc.addInt(((Integer) aTempData.get(19)).intValue());
                    add18 = "(智力+ " + ((Integer) aTempData.get(19)).intValue() + ")";
                }
                if (((Integer) aTempData.get(20)).intValue() != 0) { // Cha
                    pc.addCha(((Integer) aTempData.get(20)).intValue());
                    add19 = "(魅力+ " + ((Integer) aTempData.get(20)).intValue() + ")";
                }
                if (((Integer) aTempData.get(21)).intValue() != 0) { // ReductionDmg
                    pc.addDamageReductionByArmor(((Integer) aTempData.get(21)).intValue());
                    add20 = "(減免傷害+ " + ((Integer) aTempData.get(21)).intValue() + ")";
                }
								/*if (((Integer)aTempData.get(22)).intValue() != 0) { // MagicReductionDmg
									pc.add_Clanmagic_reduction_dmg(((Integer)aTempData.get(22)).intValue());
									add21 = "(減免魔法傷害+ " + ((Integer)aTempData.get(22)).intValue() + ")";
								}
								if (((Double)aTempData.get(23)).doubleValue() > 0) { // ExpRate
									pc.addExpByArmor(((Double)aTempData.get(23)).doubleValue());
									add22 = "(經驗增加" + ((Double)aTempData.get(23)).doubleValue() + "倍)";
								}*/
                if (((Integer) aTempData.get(24)).intValue() != 0) { // AddHpr
                    pc.addHpr(((Integer) aTempData.get(24)).intValue());
                    add23 = "(回血+ " + ((Integer) aTempData.get(24)).intValue() + ")";
                }
                if (((Integer) aTempData.get(25)).intValue() != 0) { // AddMpr
                    pc.addMpr(((Integer) aTempData.get(25)).intValue());
                    add24 = "(回魔+ " + ((Integer) aTempData.get(25)).intValue() + ")";
                }
                if (((Integer) aTempData.get(26)).intValue() != 0) { // AddWeight
                    pc.addWeightReduction(((Integer) aTempData.get(26)).intValue());
                    add25 = "(負重+ " + ((Integer) aTempData.get(26)).intValue() + ")";
                }
                pc.sendPackets(new S_SystemMessage("\\fY[防具]歷練階級" + pc.get_other().getLv_Redmg_Artifact() + "階"));
                pc.sendPackets(new S_SystemMessage("\\fU獲得:" + add1 + add2 + add3 + add4 + add5 + add6 + add7 + add8 + add9 + add10 + add11 + add12 + add13 + add14 + add15 + add16 + add17 + add18 + add19 + add20 + add21 + add22 + add23 + add24 + add25 + add26));
                pc.sendPackets(new S_SPMR(pc));
                pc.sendPackets(new S_OwnCharStatus(pc));
                // 找到就跳出迴圈
            }
        }
    }

    private static void getData() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseFactory.get().getConnection();
            pstmt = conn.prepareStatement("SELECT * FROM 寶_武防歷練值加成能力設置");
            rs = pstmt.executeQuery();
            ArrayList<Object> aReturn = null;
            if (rs != null) {
                while (rs.next()) {
                    aReturn = new ArrayList<Object>();
                    aReturn.add(0, new Integer(rs.getInt("type")));
                    aReturn.add(1, new Integer(rs.getInt("Lv_Artifact")));
                    aReturn.add(2, new Integer(rs.getInt("AddMaxHp")));
                    aReturn.add(3, new Integer(rs.getInt("AddMaxMp")));
                    aReturn.add(4, new Integer(rs.getInt("AddDmg")));
                    aReturn.add(5, new Integer(rs.getInt("AddBowDmg")));
                    aReturn.add(6, new Integer(rs.getInt("AddHit")));
                    aReturn.add(7, new Integer(rs.getInt("AddBowHit")));
                    aReturn.add(8, new Integer(rs.getInt("AddMr")));
                    aReturn.add(9, new Integer(rs.getInt("AddSp")));
                    aReturn.add(10, new Integer(rs.getInt("AddAc")));
                    aReturn.add(11, new Integer(rs.getInt("AddFire")));
                    aReturn.add(12, new Integer(rs.getInt("AddWind")));
                    aReturn.add(13, new Integer(rs.getInt("AddEarth")));
                    aReturn.add(14, new Integer(rs.getInt("AddWater")));
                    aReturn.add(15, new Integer(rs.getInt("AddStr")));
                    aReturn.add(16, new Integer(rs.getInt("AddDex")));
                    aReturn.add(17, new Integer(rs.getInt("AddCon")));
                    aReturn.add(18, new Integer(rs.getInt("AddWis")));
                    aReturn.add(19, new Integer(rs.getInt("AddInt")));
                    aReturn.add(20, new Integer(rs.getInt("AddCha")));
                    aReturn.add(21, new Integer(rs.getInt("reduction_dmg")));
                    aReturn.add(22, new Integer(rs.getInt("reduction_magic_dmg")));
                    aReturn.add(23, new Double(rs.getDouble("ExpRate")));
                    aReturn.add(24, new Integer(rs.getInt("AddHpr")));
                    aReturn.add(25, new Integer(rs.getInt("AddMpr")));
                    aReturn.add(26, new Integer(rs.getInt("AddWeight")));
                    aData.add(aReturn);
                }
            }
        } catch (SQLException e) {
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstmt);
            SQLUtil.close(conn);
        }
    }
}
