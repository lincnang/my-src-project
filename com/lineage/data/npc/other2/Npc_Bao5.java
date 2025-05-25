package com.lineage.data.npc.other2;

import com.lineage.config.ConfigQuest;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcQuest;
import com.lineage.server.serverpackets.S_EffectLocation;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 寶5區任務  - 接取任務
 */
public class Npc_Bao5 extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Bao5.class);

    private Npc_Bao5() {
    }

    public static NpcExecutor get() {
        return new Npc_Bao5();
    }

    @Override
    public int type() {
        return 3;
    }

    @Override
    public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
        try {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bao_map5"));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
        try {
            if (cmd.equalsIgnoreCase("bao_quest5")) {
                if (pc.getQuest().isEnd(L1PcQuest.BAO_QUEST_5)) {
                    pc.sendPackets(new S_SystemMessage("您今日已經解過該任務。"));
                    return;
                }
                if (pc.getQuest().isStart(L1PcQuest.BAO_QUEST_5)) {
                    pc.sendPackets(new S_SystemMessage("您已經接獲任務。"));
                    return;
                }
                if (pc.getMeteLevel() < ConfigQuest.Bao5_Quest_Levelmet) {
                    pc.sendPackets(new S_SystemMessage("您未達" + ConfigQuest.Bao5_Quest_Levelmet + "轉，無法接取任務。"));
                    return;
                }
                if (pc.getLevel() + ((pc.getMeteLevel() - ConfigQuest.Bao5_Quest_Levelmet) * 99) < ConfigQuest.Bao5_Quest_Lv) {
                    pc.sendPackets(new S_SystemMessage("您未達" + ConfigQuest.Bao5_Quest_Lv + "等，無法接取任務。"));
                    return;
                }
                pc.getQuest().set_step(L1PcQuest.BAO_QUEST_5, L1PcQuest.QUEST_START);
                pc.sendPackets(new S_SystemMessage("任務接獲成功。"));
                pc.sendPacketsX8(new S_EffectLocation(pc.getX(), pc.getY(), 14037));
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
