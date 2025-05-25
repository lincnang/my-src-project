package com.lineage.data.npc.other;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_arwe extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_arwe.class);

    public static NpcExecutor get() {
        return new Npc_arwe();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_artifact1"));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        if (cmd.equalsIgnoreCase("artifact_glass")) {
            String[] info = new String[4];
            if (pc.get_other().getArtifact() > 0) {// 歷練
                info[0] = String.valueOf(pc.get_other().getArtifact());
            } else {
                info[0] = "無";
            }
            if (pc.get_other().getLv_Artifact() > 0) {// 階級
                info[1] = String.valueOf(pc.get_other().getLv_Artifact());
            } else {
                info[1] = "無";
            }
            if (pc.get_other().getArtifact1() > 0) {// 防具歷練
                info[2] = String.valueOf(pc.get_other().getArtifact1());
            } else {
                info[2] = "無";
            }
            if (pc.get_other().getLv_Redmg_Artifact() > 0) {// 階級
                info[3] = String.valueOf(pc.get_other().getLv_Redmg_Artifact());
            } else {
                info[3] = "無";
            }
            pc.sendPackets(new S_NPCTalkReturn(pc, "y_artifact", info));
            if (cmd.equalsIgnoreCase("weapon_glass")) {
                pc.getInventory().storeItem(641395, 5);
                pc.sendPackets(new S_ServerMessage("獲得分解武器藥劑*5"));
            }
            if (cmd.equalsIgnoreCase("armor_glass")) {
                pc.getInventory().storeItem(641396, 5);
                pc.sendPackets(new S_ServerMessage("獲得分解防具藥劑*5"));
            }
        }
    }
}