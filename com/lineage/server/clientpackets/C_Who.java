package com.lineage.server.clientpackets;

import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigOther;
import com.lineage.config.ConfigRate;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1DeInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_WhoCharinfo;
import com.lineage.server.timecontroller.server.ServerRestartTimer;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldNpc;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

public class C_Who extends ClientBasePacket {
    public static int deCount() {
        int count = 0;
        Collection<L1NpcInstance> allObj = WorldNpc.get().all();
        if (allObj.isEmpty()) {
            return count;
        }
        for (L1NpcInstance obj : allObj) {
            if ((obj instanceof L1DeInstance)) {
                count++;
            }
        }
        return count;
    }

    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            read(decrypt);
            String s = readS();
            L1Character find = World.get().getPlayer(s);
            L1PcInstance pc = client.getActiveChar();
            if (find == null) {
                find = getDe(s);
            }
            if (find != null) {
                S_WhoCharinfo whoChar = new S_WhoCharinfo(find);
                pc.sendPackets(whoChar);
            } else {
                int count = World.get().getAllPlayers().size();
                int de = deCount();
                String amount = String.valueOf((int) (count * ConfigAlt.ALT_WHO_COUNT) + de);
                // \f1【目前線上有: %0 人 】
                //pc.sendPackets(new S_ServerMessage("\\fV目前線上有: " + amount));
                pc.sendPackets(new S_ServerMessage("\\fV歡迎來到" + Config.SERVERNAME + "目前線上有: " + amount));
                //pc.sendPackets(new S_ServerMessage("\\fR你的抗魔:"+pc.getMr()));
                //pc.sendPackets(new S_ServerMessage("\\fR你的幸運數字:"+pc.getSuper2()));
                // 在取得一次最新資料
                String attackhe = "";
                //
                if (pc.attackhe() == true) {
                    attackhe = "[ 開啟中 ]";
                } else {
                    attackhe = "[ 關閉中 ]";
                }
                String armorhe = "";
                //
                if (pc.armorhe() == true) {
                    armorhe = "[ 開啟中 ]";
                } else {
                    armorhe = "[ 關閉中 ]";
                }
                String droplist = "";
                //
                if (pc.droplist() == true) {
                    droplist = "[ 開啟中 ]";
                } else {
                    droplist = "[ 關閉中 ]";
                }
                String kill = "";
                //
                if (pc.kill() == true) {
                    kill = "[ 開啟中 ]";
                } else {
                    kill = "[ 關閉中 ]";
                }
                final Calendar cal = Calendar.getInstance();
                final int day_of_week = cal.get(Calendar.DAY_OF_WEEK);
                double EXP = 0.0;
                double ADEN = 0.0;
                double ITEM = 0.0;
                if (day_of_week == Calendar.SUNDAY || day_of_week == Calendar.SATURDAY) {
                    EXP = ((int) ConfigRate.RATE_XP + ConfigRate.RATE_XP_WEEK) * ConfigOther.RATE_XP_WHO;
                    ADEN = (int) ConfigRate.RATE_DROP_ADENA + ConfigRate.RATE_DROP_ADENA_WEEK;
                    ITEM = (int) ConfigRate.RATE_DROP_ITEMS + ConfigRate.RATE_DROP_ITEMS_WEEK;
                } else {
                    EXP = (int) ConfigRate.RATE_XP * ConfigOther.RATE_XP_WHO;
                    ADEN = (int) ConfigRate.RATE_DROP_ADENA;
                    ITEM = (int) ConfigRate.RATE_DROP_ITEMS;
                }
                if (ConfigAlt.ALT_WHO_COMMANDX) {
                    String nowDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
                    switch (ConfigAlt.ALT_WHO_TYPE) {
                        case 0:
                            pc.sendPackets(new S_ServerMessage("\\fV啟動時間: " + String.valueOf(ServerRestartTimer.get_startTime())));
                            pc.sendPackets(new S_ServerMessage("\\fV經驗倍率: " + EXP));
                            pc.sendPackets(new S_ServerMessage("\\fV金錢倍率: " + ADEN));
                            pc.sendPackets(new S_ServerMessage("\\fV掉寶倍率: " + ITEM));
                            pc.sendPackets(new S_ServerMessage("\\fV沖武倍率: " + ConfigRate.ENCHANT_CHANCE_WEAPON));
                            pc.sendPackets(new S_ServerMessage("\\fV沖防倍率: " + ConfigRate.ENCHANT_CHANCE_ARMOR));
                            pc.sendPackets(new S_ServerMessage("\\fV現實時間: " + nowDate));
                            pc.sendPackets(new S_ServerMessage("\\fV重啟時間: " + ServerRestartTimer.get_restartTime()));
                            break;
                        case 1:// 視窗顯示
                            //　pc.sendPackets(new S_ServerMessage("\\fV您的陣營積分: "+ pc.get_other().get_score()));
                            final String[] info = new String[]{Config.SERVERNAME,// 伺服器資訊:
                                    String.valueOf(amount),// 人數
                                    String.valueOf(EXP),// 經驗
                                    String.valueOf(ITEM),// 掉寶
                                    String.valueOf(ADEN),// 金幣
                                    String.valueOf(ConfigRate.ENCHANT_CHANCE_WEAPON),// 武器
                                    String.valueOf(ConfigRate.ENCHANT_CHANCE_ARMOR),// 防具
                                    String.valueOf(ConfigAlt.POWER),// 手點上限
                                    String.valueOf(ConfigAlt.POWERMEDICINE),// 單項萬能藥上限
                                    String.valueOf(ConfigAlt.MEDICINE),// 總和萬能藥瓶數
                                    nowDate,// 目前時間
                                    ServerRestartTimer.get_restartTime(), // 重啟時間
                                    String.valueOf(attackhe), String.valueOf(armorhe), String.valueOf(droplist), String.valueOf(kill), String.valueOf(pc.get_other().get_score())};
//                            pc.sendPackets(new S_NPCTalkReturn(pc, "y_status_1", info));
                            pc.sendPackets(new S_NPCTalkReturn(pc, "y_who", info));
                            break;
                    }
                }
            }
        } catch (final Exception e) {
            // _log.error(e.getLocalizedMessage(), e);
        } finally {
            this.over();
        }
    }

    private L1DeInstance getDe(String s) {
        Collection<L1NpcInstance> allObj = WorldNpc.get().all();
        if (allObj.isEmpty()) {
            return null;
        }
        for (L1NpcInstance obj : allObj) {
            if ((obj instanceof L1DeInstance)) {
                L1DeInstance de = (L1DeInstance) obj;
                if (de.getNameId().equalsIgnoreCase(s)) {
                    return de;
                }
            }
        }
        return null;
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
