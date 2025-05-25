package com.lineage.server.command.executor;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBoxTest;
import com.lineage.server.serverpackets.S_SystemMessage;

import java.util.StringTokenizer;

public class L1PacketIcon implements L1CommandExecutor {
    public static L1CommandExecutor getInstance() {
        return new L1PacketIcon();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer st = new StringTokenizer(arg);
            int type = 0;
            int time = 10;
            if (st.hasMoreTokens()) {
                type = Integer.parseInt(st.nextToken());
            }
            if (st.hasMoreTokens()) {
                time = Integer.parseInt(st.nextToken());
            }
            pc.sendPackets(new S_PacketBoxTest(type, time));
            /*
             * for (int i = 0 ; i < 1000; i++ ) { int nowiconid = iconid + i;
             * TimeUnit.MILLISECONDS.sleep(1000); pc.sendPackets(new S_PacketBox(nowiconid,
             * time)); pc.sendPackets(new S_SystemMessage("Packeticon 目前編號: " +
             * nowiconid + "。")); }
             */
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + " PacketIcon 請輸入 id 編碼。"));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.command.executor.L1SkillIcon JD-Core Version: 0.6.2
 */