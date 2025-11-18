package com.lineage.server.command.executor;

import com.lineage.server.datatables.lock.CharBuffReading;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1CharName;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import william.Honor;

/**
 * 重置限時地監時間 + 每日爵位任務
 */
public class L1ResetTime implements L1CommandExecutor {
    private static final Log _log = LogFactory.getLog(L1ResetTime.class);

    private L1ResetTime() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1ResetTime();
    }

    @Override
    public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
        try {
            for (L1CharName charName : CharacterTable.get().getCharNameList()) {
                L1PcInstance tgpc = World.get().getPlayer(charName.getName());
                if (tgpc != null) {
                    tgpc.resetAllMapTime();
                    tgpc.save();
                    tgpc.removeSkillEffect(40000);
                    Honor.getInstance().resetHonorIfNotCompleted(tgpc, false);
                    tgpc.sendPackets(new S_ServerMessage("\\fT限時地監與爵位任務已重置。"));
                } else {
                    L1PcInstance offlinepc = CharacterTable.get().restoreCharacter(charName.getName());
                    if (offlinepc != null) {
                        offlinepc.resetAllMapTime();
                        offlinepc.removeSkillEffect(40000);
                        Honor.getInstance().resetHonorIfNotCompleted(offlinepc, true);
                        offlinepc.save();
                    }
                }
            }
            CharBuffReading.get().deleteBuff_skill(40000);
            Honor.getInstance().forceDailyReset();
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + "重置所有玩家限時地監與爵位任務。");
            } else {
                pc.sendPackets(new S_SystemMessage("重置所有玩家限時地監與爵位任務。"));
            }
        } catch (Exception e) {
            _log.info(e.getMessage());
            pc.sendPackets(new S_ServerMessage(261));
        }
    }
}