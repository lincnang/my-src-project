package com.lineage.server.command.executor;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBoxIcon3;
import com.lineage.server.serverpackets.S_SystemMessage;

import java.util.StringTokenizer;

public class L1SkillIcon1 implements L1CommandExecutor {
    public static L1CommandExecutor getInstance() {
        return new L1SkillIcon1();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer st = new StringTokenizer(arg);
            int time = 30;// 時間
            int type = 0;// 圖示編號
            if (st.hasMoreTokens()) {
                type = Integer.parseInt(st.nextToken());
            }
            pc.sendPackets(new S_PacketBoxIcon3(time, type));
            for (int i = 0; i < 1000; i++) {
                pc.sendPackets(new S_PacketBoxIcon3(time, type + i));
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + " Skillicon 請輸入 id 編碼。"));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.command.executor.L1SkillIcon JD-Core Version: 0.6.2
 */