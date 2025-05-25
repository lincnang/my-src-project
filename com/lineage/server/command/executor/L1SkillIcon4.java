package com.lineage.server.command.executor;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBoxIcon2;
import com.lineage.server.serverpackets.S_SystemMessage;

import java.util.StringTokenizer;

public class L1SkillIcon4 implements L1CommandExecutor {
    public static L1CommandExecutor getInstance() {
        return new L1SkillIcon4();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer st = new StringTokenizer(arg);
            int type = 0;
            if (st.hasMoreTokens()) {
                type = Integer.parseInt(st.nextToken());
            }
            switch (type) {
                case 1:
                    for (int i = 0; i < 1000; i++) {
                        pc.sendPackets(new S_PacketBoxIcon2(i, 0, 0, 0));
                    }
                    break;
                case 2:
                    for (int i = 0; i < 1000; i++) {
                        pc.sendPackets(new S_PacketBoxIcon2(0, i, 0, 0));
                    }
                    break;
                case 3:
                    for (int i = 0; i < 1000; i++) {
                        pc.sendPackets(new S_PacketBoxIcon2(0, 0, i, 0));
                    }
                    break;
                case 4:
                    for (int i = 0; i < 1000; i++) {
                        pc.sendPackets(new S_PacketBoxIcon2(0, 0, 0, i));
                    }
                    break;
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