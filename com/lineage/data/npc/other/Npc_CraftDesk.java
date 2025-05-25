package com.lineage.data.npc.other;

import com.add.system.L1BlendTable;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

public class Npc_CraftDesk extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_CraftDesk.class);

    public static NpcExecutor get() {
        return new Npc_CraftDesk();
    }

    public int type() {
        return 1;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            String msg0 = "";
            String msg1 = "";
            String msg2 = "";
            String msg3 = "";
            String msg4 = "";
            String msg5 = "";
            String msg6 = "";
            String msg7 = "";
            String msg8 = "";
            String msg9 = "";
            String msg10 = "";
            String msg11 = "";
            String msg12 = "";
            String msg13 = "";
            String msg14 = "";
            String msg15 = "";
            String msg16 = "";
            String msg17 = "";
            String msg18 = "";
            String msg19 = "";
            String msg20 = "";
            String msg21 = "";
            String msg22 = "";
            String msg23 = "";
            String msg24 = "";
            String msg25 = "";
            String msg26 = "";
            String msg27 = "";
            String msg28 = "";
            String msg29 = "";
            String msg30 = "";
            String msg31 = "";
            String msg32 = "";
            String msg33 = "";
            String msg34 = "";
            String msg35 = "";
            String msg36 = "";
            String msg37 = "";
            String msg38 = "";
            String msg39 = "";
            String msg40 = "";
            int npcid = npc.getNpcId();
            Map<String, String> craftlist = L1BlendTable.getInstance().get_craftlist();
            if (!craftlist.isEmpty()) {
                msg0 = craftlist.get(npcid + "A");
                msg1 = craftlist.get(npcid + "B");
                msg2 = craftlist.get(npcid + "C");
                msg3 = craftlist.get(npcid + "D");
                msg4 = craftlist.get(npcid + "E");
                msg5 = craftlist.get(npcid + "F");
                msg6 = craftlist.get(npcid + "G");
                msg7 = craftlist.get(npcid + "H");
                msg8 = craftlist.get(npcid + "I");
                msg9 = craftlist.get(npcid + "J");
                msg10 = craftlist.get(npcid + "K");
                msg11 = craftlist.get(npcid + "L");
                msg12 = craftlist.get(npcid + "M");
                msg13 = craftlist.get(npcid + "N");
                msg14 = craftlist.get(npcid + "O");
                msg15 = craftlist.get(npcid + "P");
                msg16 = craftlist.get(npcid + "Q");
                msg17 = craftlist.get(npcid + "R");
                msg18 = craftlist.get(npcid + "S");
                msg19 = craftlist.get(npcid + "T");
                msg20 = craftlist.get(npcid + "U");
                msg21 = craftlist.get(npcid + "V");
                msg22 = craftlist.get(npcid + "W");
                msg23 = craftlist.get(npcid + "X");
                msg24 = craftlist.get(npcid + "Y");
                msg25 = craftlist.get(npcid + "Z");
                msg26 = craftlist.get(npcid + "a1");
                msg27 = craftlist.get(npcid + "a2");
                msg28 = craftlist.get(npcid + "a3");
                msg29 = craftlist.get(npcid + "a4");
                msg30 = craftlist.get(npcid + "a5");
                msg31 = craftlist.get(npcid + "a6");
                msg32 = craftlist.get(npcid + "a7");
                msg33 = craftlist.get(npcid + "a8");
                msg34 = craftlist.get(npcid + "a9");
                msg35 = craftlist.get(npcid + "a10");
                msg36 = craftlist.get(npcid + "a11");
                msg37 = craftlist.get(npcid + "a12");
                msg38 = craftlist.get(npcid + "a13");
                msg39 = craftlist.get(npcid + "a14");
                msg40 = craftlist.get(npcid + "a15");
            }
            String msgs[] = {msg0, msg1, msg2, msg3, msg4, msg5, msg6, msg7, msg8, msg9, msg10, msg11, msg12, msg13, msg14, msg15, msg16, msg17, msg18, msg19, msg20, msg21, msg22, msg23, msg24, msg25, msg26, msg27, msg28, msg29, msg30, msg31, msg32, msg33, msg34, msg35, msg36, msg37, msg38, msg39, msg40};
            if (msg0 != null) {// 至少有設定一項道具製造資料
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "smithitem1", msgs));
            } else {
                pc.sendPackets(new S_SystemMessage("沒有可以製作的道具。"));
                return;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.other.Npc_Fishing_1 JD-Core Version: 0.6.2
 */