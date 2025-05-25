package william;

import com.lineage.DatabaseFactory;
import com.lineage.Server;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.RandomArrayList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * 道具能力系統
 */
public class ItemUse {
    public static final String TOKEN = ",";
    private static ArrayList<ArrayList<Object>> aData15b = new ArrayList<>();
    private static boolean NO_MORE_GET_DATA15b = false;

    public static void main(final String[] a) {
        try {
            Server.main(null);
        } catch (final Exception localException) {
        }
    }

    public static void forItemUSe(final L1PcInstance user, final L1ItemInstance itemInstance) {
        final int itemid = itemInstance.getItemId();
        ArrayList<Object> aTempData = null;
        boolean isSave = false;
        if (!(NO_MORE_GET_DATA15b)) {
            NO_MORE_GET_DATA15b = true;
            getData15b();
        }
        for (ArrayList<Object> objects : aData15b) {
            aTempData = objects;
            if ((Integer) aTempData.get(0) != itemid) {
                continue;
            }
            if ((Integer) aTempData.get(1) != 0) {
                byte class_id = 0;
                String msg = "";
                if (user.isCrown()) {
                    class_id = 1;
                } else if (user.isKnight()) {
                    class_id = 2;
                } else if (user.isWizard()) {
                    class_id = 3;
                } else if (user.isElf()) {
                    class_id = 4;
                } else if (user.isDarkelf()) {
                    class_id = 5;
                } else if (user.isDragonKnight()) {
                    class_id = 6;
                } else if (user.isIllusionist()) {
                    class_id = 7;
                } else if (user.isWarrior()) {
                    class_id = 8;
                }
                switch ((Integer) aTempData.get(1)) {
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
                        msg = "黑妖";
                        break;
                    case 6:
                        msg = "龍騎";
                        break;
                    case 7:
                        msg = "幻術";
                        break;
                    case 8:
                        msg = "戰士";
                }
                if ((Integer) aTempData.get(1) != class_id) {
                    user.sendPackets(new S_SystemMessage("你的職業無法使用" + msg + "的專屬道具。"));
                    return;
                }
            }
            if (((Integer) aTempData.get(14) != 0) && (user.getLevel() < (Integer) aTempData.get(14))) {
                user.sendPackets(new S_SystemMessage("等級" + (Integer) aTempData.get(14) + "以上才可使用此道具。"));
                return;
            }
            if (((Integer) aTempData.get(2) != 0) && (!(user.getInventory().checkItem((Integer) aTempData.get(2))))) {
                final L1Item temp = ItemTable.get().getTemplate((Integer) aTempData.get(2));
                user.sendPackets(new S_SystemMessage("使用此道具需要(" + temp.getName() + ")來作為媒介。"));
                return;
            }
            if ((Integer) aTempData.get(3) != 0) {
                final L1ItemInstance item = user.getInventory().findItemId((Integer) aTempData.get(0));
                user.getInventory().removeItem(item.getId(), 1);
            }
            if ((Integer) aTempData.get(4) != 0) {
                if (user.hasSkillEffect(67)) {
                    user.removeSkillEffect(67);
                }
                L1PolyMorph.doPoly(user, (Integer) aTempData.get(4), (Integer) aTempData.get(5), 1);
            }
            if ((Integer) aTempData.get(6) != 0) {
                user.addBaseMaxHp((short) ((Integer) aTempData.get(6)).intValue());
                user.sendPackets(new S_SystemMessage("HP永久增加了(" + (Integer) aTempData.get(6) + ")滴。"));
                user.sendPackets(new S_OwnCharStatus(user));
                isSave = true;
            }
            if ((Integer) aTempData.get(7) != 0) {
                user.addBaseMaxMp((short) ((Integer) aTempData.get(7)).intValue());
                user.sendPackets(new S_SystemMessage("MP永久增加了(" + (Integer) aTempData.get(7) + ")滴。"));
                user.sendPackets(new S_OwnCharStatus(user));
                isSave = true;
            }
            if ((Integer) aTempData.get(8) != 0) {
                user.addBaseStr((byte) ((Integer) aTempData.get(8)).intValue());
                user.sendPackets(new S_SystemMessage("力量永久增加了(" + (Integer) aTempData.get(8) + ")點。"));
                user.sendPackets(new S_OwnCharStatus2(user));
                user.setElixirStats(user.getElixirStats() + (Integer) aTempData.get(8));
                user.sendDetails();
                isSave = true;
            }
            if ((Integer) aTempData.get(9) != 0) {
                user.addBaseDex((byte) ((Integer) aTempData.get(9)).intValue());
                user.sendPackets(new S_SystemMessage("敏捷永久增加了(" + (Integer) aTempData.get(9) + ")點。"));
                user.sendPackets(new S_OwnCharStatus2(user));
                user.setElixirStats(user.getElixirStats() + (Integer) aTempData.get(9));
                user.sendDetails();
                isSave = true;
            }
            if ((Integer) aTempData.get(10) != 0) {
                user.addBaseCon((byte) ((Integer) aTempData.get(10)).intValue());
                user.sendPackets(new S_SystemMessage("體質永久增加了(" + (Integer) aTempData.get(10) + ")點。"));
                user.sendPackets(new S_OwnCharStatus2(user));
                user.setElixirStats(user.getElixirStats() + (Integer) aTempData.get(10));
                user.sendDetails();
                isSave = true;
            }
            if ((Integer) aTempData.get(11) != 0) {
                user.addBaseWis((byte) ((Integer) aTempData.get(11)).intValue());
                user.sendPackets(new S_SystemMessage("精神永久增加了(" + (Integer) aTempData.get(11) + ")點。"));
                user.sendPackets(new S_OwnCharStatus2(user));
                user.setElixirStats(user.getElixirStats() + (Integer) aTempData.get(11));
                user.sendDetails();
                isSave = true;
            }
            if ((Integer) aTempData.get(12) != 0) {
                user.addBaseInt((byte) ((Integer) aTempData.get(12)).intValue());
                user.sendPackets(new S_SystemMessage("智力永久增加了(" + (Integer) aTempData.get(12) + ")點。"));
                user.sendPackets(new S_OwnCharStatus2(user));
                user.setElixirStats(user.getElixirStats() + (Integer) aTempData.get(12));
                user.sendDetails();
                isSave = true;
            }
            if ((Integer) aTempData.get(13) != 0) {
                user.addBaseCha((byte) ((Integer) aTempData.get(13)).intValue());
                user.sendPackets(new S_SystemMessage("魅力永久增加了(" + (Integer) aTempData.get(13) + ")點。"));
                user.sendPackets(new S_OwnCharStatus2(user));
                user.setElixirStats(user.getElixirStats() + (Integer) aTempData.get(13));
                isSave = true;
            }
            if ((Integer) aTempData.get(15) != 0) {
                if (user.hasSkillEffect(71)) {
                    user.sendPackets(new S_ServerMessage(698));
                    return;
                }
                if (user.hasSkillEffect(5167)) {
                    user.setCurrentHp(user.getCurrentHp() - RandomArrayList.getInc(32, 12));
                } else {
                    user.setCurrentHp(user.getCurrentHp() + (Integer) aTempData.get(15));
                }
            }
            if ((Integer) aTempData.get(16) != 0) {
                if (user.hasSkillEffect(71)) {
                    user.sendPackets(new S_ServerMessage(698));
                    return;
                }
                user.setCurrentMp(user.getCurrentMp() + (Integer) aTempData.get(16));
                user.sendPackets(new S_ServerMessage(338, "$1084"));
            }
            if ((Integer) aTempData.get(20) != 0) {
                user.setExp(user.getExp() + (Integer) aTempData.get(20));
                user.sendPackets(new S_OwnCharStatus(user));
                isSave = true;
            }
            if ((Integer) aTempData.get(21) != 0) {
                //int Lawful = user.getLawful() + ((Integer) aTempData.get(21)).intValue();
                int Lawful = (Integer) aTempData.get(21);
                if (Lawful > 32767) {
                    Lawful = 32767;
                } else if (Lawful < -32768) {
                    Lawful = -32768;
                }
                user.addLawful(Lawful);
                user.sendPackets(new S_OwnCharStatus(user));
                isSave = true;
            }
            if ((Integer) aTempData.get(22) != 0) {
                user.sendPackets(new S_SkillSound(user.getId(), (Integer) aTempData.get(22)));
                user.broadcastPacketAll(new S_SkillSound(user.getId(), (Integer) aTempData.get(22)));
            }
            if ((int[]) aTempData.get(27) != null) {
                final int[] Skills = (int[]) aTempData.get(27);
                final int time = (Integer) aTempData.get(28);
                for (int skill : Skills) {
                    final L1SkillUse l1skilluse = new L1SkillUse();
                    l1skilluse.handleCommands(user, skill, user.getId(), user.getX(), user.getY(), time, L1SkillUse.TYPE_GMBUFF);
                }
            }
            if ((Integer) aTempData.get(33) == 0) {
                return;
            }
            user.setKarma(user.getKarma() + (Integer) aTempData.get(33));
            return;
        }
        if (isSave) {
            try {
                user.save();
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }

    private static void getData15b() {
        Connection con = null;
        try {
            con = DatabaseFactory.get().getConnection();
            final Statement stat = con.createStatement();
            final ResultSet rset = stat.executeQuery("SELECT * FROM 其他_道具特化");
            ArrayList<Object> aReturn = null;
            if (rset != null) {
                while (rset.next()) {
                    aReturn = new ArrayList<>();
                    aReturn.add(0, rset.getInt("道具編號"));
                    aReturn.add(1, rset.getInt("職業確認"));
                    aReturn.add(2, rset.getInt("需要的道具編號才能使用"));
                    aReturn.add(3, rset.getInt("使用後是否刪除"));
                    aReturn.add(4, rset.getInt("變身編號"));
                    aReturn.add(5, rset.getInt("變身持續時間"));
                    aReturn.add(6, rset.getInt("增加血"));
                    aReturn.add(7, rset.getInt("增加魔"));
                    aReturn.add(8, rset.getInt("增加力量"));
                    aReturn.add(9, rset.getInt("增加敏捷"));
                    aReturn.add(10, rset.getInt("增加體質"));
                    aReturn.add(11, rset.getInt("增加精神"));
                    aReturn.add(12, rset.getInt("增加智力"));
                    aReturn.add(13, rset.getInt("增加魅力"));
                    aReturn.add(14, rset.getInt("等級"));
                    aReturn.add(15, rset.getInt("恢復血量"));
                    aReturn.add(16, rset.getInt("恢復魔量"));
                    aReturn.add(17, 0);
                    aReturn.add(18, 0);
                    aReturn.add(19, 0);
                    aReturn.add(20, rset.getInt("經驗"));
                    aReturn.add(21, rset.getInt("正義值"));
                    aReturn.add(22, rset.getInt("特效"));
                    aReturn.add(23, 0);
                    aReturn.add(24, 0);
                    aReturn.add(25, 0);
                    aReturn.add(26, 0);
                    if ((rset.getString("技能編號") != null) && !rset.getString("技能編號").equals("") && !rset.getString("技能編號").equals("0")) {
                        aReturn.add(27, getArray(rset.getString("技能編號"), ",", 1));
                    } else {
                        aReturn.add(27, null);
                    }
                    aReturn.add(28, rset.getInt("技能時間"));
                    aReturn.add(29, 0);
                    aReturn.add(30, 0);
                    aReturn.add(31, 0);
                    aReturn.add(32, 0);
                    aReturn.add(33, rset.getInt("友好度"));
                    aData15b.add(aReturn);
                }
            }
            if ((con != null) && (!(con.isClosed()))) {
                con.close();
            }
        } catch (final Exception localException) {
        }
    }

    private static Object getArray(final String s, final String sToken, final int iType) {
        final StringTokenizer st = new StringTokenizer(s, sToken);
        final int iSize = st.countTokens();
        String sTemp = null;
        if (iType == 1) {
            final int iReturn[] = new int[iSize];
            for (int i = 0; i < iSize; i++) {
                sTemp = st.nextToken();
                iReturn[i] = Integer.parseInt(sTemp);
            }
            return iReturn;
        }
        if (iType == 2) {
            final String sReturn[] = new String[iSize];
            for (int i = 0; i < iSize; i++) {
                sTemp = st.nextToken();
                sReturn[i] = sTemp;
            }
            return sReturn;
        }
        if (iType == 3) {
            String sReturn = null;
            for (int i = 0; i < iSize; i++) {
                sTemp = st.nextToken();
                sReturn = sTemp;
            }
            return sReturn;
        } else {
            return null;
        }
    }
}