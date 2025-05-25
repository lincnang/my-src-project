package com.lineage.server.command.executor;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_SystemMessage;

import java.util.StringTokenizer;

public class L1SkillIcon2 implements L1CommandExecutor {
    public static L1CommandExecutor getInstance() {
        return new L1SkillIcon2();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer st = new StringTokenizer(arg);
            int type = 0;
            int n = 1;
            if (st.hasMoreTokens()) {
                type = Integer.parseInt(st.nextToken());
            }
            if (st.hasMoreTokens()) {
                n = Integer.parseInt(st.nextToken());
            }
            /*
             * for (int i = 0; i < 1000; i++) { pc.sendPackets(new
             * S_PacketBox(147, 1, type + i)); }
             */
            // pc.sendPackets(new S_PacketBox(154, 27 , 60*1000));
            //
            /*
             * if(type==1){ pc.sendPackets(new S_Paralysis(2, true, 180)); }else
             * if (type==2){ pc.sendPackets(new S_Paralysis(2, false, 0)); }else
             * if (type==3){ pc.sendPackets(new S_Poison(pc.getId(), 0)); }else
             * if (type==4){ pc.sendPackets(new S_Poison(pc.getId(), 1)); }else
             * if (type==5){ pc.sendPackets(new S_Poison(pc.getId(), 2)); }else
             * if (type==6){
             */
            //pc.sendPackets(new S_SkillIconPoison(type, n, 600));
            //pc.sendPackets(new S_PacketBoxGame(71,3600));
            //pc.sendPacketsX8(new S_SkillSound(pc.getId(), type));
            pc.sendPackets(new S_PacketBox(type, n, 3600 / 60));
            // }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + " Skillicon 請輸入 id 編碼。"));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.command.executor.L1SkillIcon JD-Core Version: 0.6.2
 */