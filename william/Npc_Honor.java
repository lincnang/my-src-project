package william;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;

public class Npc_Honor {

    public static boolean Cmd(L1PcInstance pc, String cmd) {
        if (cmd.startsWith("enter_stage")) {
            try {
                int stage = Integer.parseInt(cmd.replace("enter_stage", ""));
                Honor.getInstance().enterStage(pc, stage);
                return true;
            } catch (NumberFormatException e) {
                pc.sendPackets(new S_SystemMessage("階段指令格式錯誤。"));
            }
        }
        return false;
    }
}