package william;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.C1_Name_Table;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * NPC魔法輔助DB化系統
 */
public class WilliamBuff {
    private static final Log _log = LogFactory.getLog(WilliamBuff.class);
    private static final ArrayList<ArrayList<Object>> datas = new ArrayList<>();

    public static boolean giveBuff(L1PcInstance pc, L1NpcInstance npc, String args) {
        boolean f = false;
        //for (ArrayList<?> os : datas) {
        for (ArrayList<Object> os : datas) {
            int[] npcids = (int[]) os.get(0);
            for (int id : npcids) {
                if (id == npc.getNpcId()) {
                    f = true;
                    break;
                }
            }
            if (f) {
                f = false;
                String[] cmd = (String[]) os.get(1);
                for (String c : cmd) {
                    if (c.equals(args)) {
                        f = true;
                        break;
                    }
                }
                if (f) {
                    int[] skills = (int[]) os.get(2);
                    int[] times = (int[]) os.get(3);
                    int[] m = (int[]) os.get(4);
                    int[] mc = (int[]) os.get(5);
                    if ((Integer) os.get(6) != 0) {
                        byte class_id = 0;
                        String msg = "";
                        if (pc.isCrown()) {
                            class_id = 1;
                        } else if (pc.isKnight()) {
                            class_id = 2;
                        } else if (pc.isWizard()) {
                            class_id = 3;
                        } else if (pc.isElf()) {
                            class_id = 4;
                        } else if (pc.isDarkelf()) {
                            class_id = 5;
                        } else if (pc.isDragonKnight()) {
                            class_id = 6;
                        } else if (pc.isIllusionist()) {
                            class_id = 7;
                        } else if (pc.isWarrior()) {
                            class_id = 8;
                        }
                        switch ((Integer) os.get(6)) {
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
                                msg = "戰士";
                        }
                        if ((Integer) os.get(6) != class_id) {
                            pc.sendPackets(new S_SystemMessage("你的職業無法使用" + msg + "的專屬魔法輔助。"));
                            return true;
                        }
                    }
                    if ((Integer) os.get(7) != 0) {
                        final String typeName = C1_Name_Table.get().get((Integer) os.get(7));
                        if (pc.get_c_power() == null) {
                            pc.sendPackets(new S_ServerMessage("你的陣營必須是" + typeName + "才可使用此魔法輔助。"));
                            return true;
                        }
                        if (pc.get_c_power().get_c1_type() >= 0 && pc.get_c_power().get_c1_type() != (Integer) os.get(7)) {
                            pc.sendPackets(new S_SystemMessage("你的陣營必須是" + typeName + "才可使用此魔法輔助。"));
                            return true;
                        }
                    }
                    if ((Integer) os.get(8) != 0 && (Integer) os.get(9) == 0) {
                        if (pc.getLevel() < (Integer) os.get(8)) {
                            pc.sendPackets(new S_SystemMessage("你的等級必須要大於" + (Integer) os.get(8) + "才可使用此魔法輔助。"));
                            return true;
                        }
                    }
                    if ((Integer) os.get(8) == 0 && (Integer) os.get(9) != 0) {
                        if (pc.getLevel() > (Integer) os.get(9)) {
                            pc.sendPackets(new S_SystemMessage("你的等級必須要小於" + (Integer) os.get(9) + "才可使用此魔法輔助。"));
                            return true;
                        }
                    }
                    if ((Integer) os.get(8) != 0 && (Integer) os.get(9) != 0) {
                        if (pc.getLevel() > (Integer) os.get(9) || pc.getLevel() < (Integer) os.get(8)) {
                            pc.sendPackets(new S_SystemMessage("你的等級必須要介於" + (Integer) os.get(8) + "~" + (Integer) os.get(9) + "之間才可使用此魔法輔助。"));
                            return true;
                        }
                    }
                    if ((Integer) os.get(10) != 0) {
                        if (pc.getMeteLevel() < (Integer) os.get(10)) {
                            pc.sendPackets(new S_SystemMessage("你的轉生等級必須要大於" + (Integer) os.get(10) + "才可使用此魔法輔助。"));
                            return true;
                        }
                    }
                    for (int i = 0; i < m.length; i++) {
                        if (!pc.getInventory().consumeItem(m[i], mc[i])) {
                            L1Item item = ItemTable.get().getTemplate(m[i]);
                            pc.sendPackets(new S_SystemMessage(item.getName() + "(" + mc[i] + ")不足，無法為您施放輔助魔法。"));
                            return true;
                        }
                    }
                    for (int i = 0; i < skills.length; i++) {
                        new L1SkillUse().handleCommands(pc, skills[i], pc.getId(), pc.getX(), pc.getY(), times[i], 4);
                        pc.sendPackets(new S_CloseList(pc.getId())); // 加完輔助魔法關閉對話檔
                    }
                    if (f) {
                        return true;
                    }
                }
            }
        }
        return f;
    }

    public static void load() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `其他_npc魔法輔助系統`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                ArrayList<Object> data = new ArrayList<>();
                data.add(convert(rs.getString("NPC編號").split(",")));
                data.add(rs.getString("指令按鈕").split(","));
                data.add(convert(rs.getString("技能編號").split(",")));
                data.add(convert(rs.getString("技能時間").split(",")));
                data.add(convert(rs.getString("所需道具").split(",")));
                data.add(convert(rs.getString("所需道具數量").split(",")));
                data.add(6, rs.getInt("職業判斷"));
                data.add(7, rs.getInt("陣營判斷"));
                data.add(8, rs.getInt("等級最低判斷"));
                data.add(9, rs.getInt("等級最高判斷"));
                data.add(10, rs.getInt("轉身判斷"));
                datas.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        _log.info("讀取->[系統]_Npc魔法輔助DB化系統數量: " + datas.size());
    }

    private static int[] convert(String[] data) {
        int[] i32 = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            i32[i] = Integer.parseInt(data[i]);
        }
        return i32;
    }
}
