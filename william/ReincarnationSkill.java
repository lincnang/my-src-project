package william;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1SystemMessage;
import com.lineage.server.utils.SQLUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 轉生天賦
 */
public class ReincarnationSkill {
    /**
     * 轉生天賦技能名稱
     */
    public static String[][] SKILLS = {
            {
                    L1SystemMessage.ShowMessage(6500),
                    L1SystemMessage.ShowMessage(6501),
                    L1SystemMessage.ShowMessage(6502)
            },
            {
                    L1SystemMessage.ShowMessage(6503),
                    L1SystemMessage.ShowMessage(6504),
                    L1SystemMessage.ShowMessage(6505)
            },
            {
                    L1SystemMessage.ShowMessage(6506),
                    L1SystemMessage.ShowMessage(6507),
                    L1SystemMessage.ShowMessage(6508)
            },
            {
                    L1SystemMessage.ShowMessage(6509),
                    L1SystemMessage.ShowMessage(6510),
                    L1SystemMessage.ShowMessage(6511)
            },
            {
                    L1SystemMessage.ShowMessage(6512),
                    L1SystemMessage.ShowMessage(6513),
                    L1SystemMessage.ShowMessage(6514)
            },
            {
                    L1SystemMessage.ShowMessage(6515),
                    L1SystemMessage.ShowMessage(6516),
                    L1SystemMessage.ShowMessage(6517)
            },
            {
                    L1SystemMessage.ShowMessage(6518),
                    L1SystemMessage.ShowMessage(6519),
                    L1SystemMessage.ShowMessage(6520)
            },
            {
                    L1SystemMessage.ShowMessage(6521),
                    L1SystemMessage.ShowMessage(6522),
                    L1SystemMessage.ShowMessage(6523)
            }
    };
    private static ReincarnationSkill instance;

    public static ReincarnationSkill getInstance() {
        if (instance == null) {
            instance = new ReincarnationSkill();
        }
        return instance;
    }

    /**
     * 重置玩家的天賦點數。
     *
     * @param pc 玩家實例。
     */
    public void resetPoint(L1PcInstance pc) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("DELETE FROM 其他_天賦技能 WHERE pid=?");
            pstm.setInt(1, pc.getId());
            pstm.execute();
            pc.getReincarnationSkill()[0] = 0;
            pc.getReincarnationSkill()[1] = 0;
            pc.getReincarnationSkill()[2] = 0;
        } catch (SQLException localSQLException) {
            // 如果需要，處理 SQL 異常
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    /**
     * 從數據庫檢索玩家的天賦點數。
     *
     * @param pc 玩家實例。
     */
    public void getPoint(L1PcInstance pc) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM 其他_天賦技能 WHERE pid=?");
            pstm.setInt(1, pc.getId());
            rs = pstm.executeQuery();
            while (rs.next()) {
                int sid = rs.getInt("sid");
                if (sid < 3) {
                    pc.getReincarnationSkill()[sid] = rs.getInt("point");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
    }

    /**
     * 為玩家的特定技能添加一個天賦點數。
     *
     * @param pc 玩家實例。
     * @param i  技能索引。
     */
    public void addPoint(L1PcInstance pc, int i) {
        int pt = pc.getReincarnationSkill()[i];
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        if (pt > 0) {
            pt++;
            try {
                con = DatabaseFactory.get().getConnection();
                pstm = con.prepareStatement("UPDATE 其他_天賦技能 SET point=? WHERE pid=? AND sid=?");
                pstm.setInt(1, pt);
                pstm.setInt(2, pc.getId());
                pstm.setInt(3, i);
                pstm.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                SQLUtil.close(pstm);
                SQLUtil.close(con);
            }
        } else {
            pt = 1;
            try {
                con = DatabaseFactory.get().getConnection();
                pstm = con.prepareStatement("INSERT INTO 其他_天賦技能 SET pid=?, sid=?, point=?");
                pstm.setInt(1, pc.getId());
                pstm.setInt(2, i);
                pstm.setInt(3, 1);
                pstm.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                SQLUtil.close(rs, pstm, con);
            }
        }
        pc.getReincarnationSkill()[i] = pt;
    }

    /**
     * 檢查並提示玩家是否要將天賦點數分配到某個技能。
     *
     * @param pc 玩家實例。
     * @param i  技能索引。
     */
    public void addCheck(L1PcInstance pc, int i) {
        if (pc.getType() > 7) {
            return;
        }
        String name = SKILLS[pc.getType()][i];
        int rei_pt = 0;
        for (int pt : pc.getReincarnationSkill()) {
            rei_pt += pt;
        }
        rei_pt = pc.getTurnLifeSkillCount() - rei_pt;
        if (rei_pt <= 0) {
            pc.sendPackets(new S_SystemMessage(L1SystemMessage.ShowMessage(6499))); // 天賦點數不足。
        } else {
            pc.setSi(i);
            pc.sendPackets(new S_Message_YN(2760, name)); // 2760 您確定要將點數分配到此技能嗎？
        }
    }
}
