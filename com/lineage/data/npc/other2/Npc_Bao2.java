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
 * 寶2區任務  - 接取任務
 */
public class Npc_Bao2 extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Bao2.class);

    private Npc_Bao2() {
    }

    public static NpcExecutor get() {
        return new Npc_Bao2();
    }

    @Override
    public int type() {
        return 3;
    }

    @Override
    public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
        try {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bao_map2"));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
        try {
            if (cmd.equalsIgnoreCase("bao_quest2")) {
                if (pc.getQuest().isEnd(L1PcQuest.BAO_QUEST_2)) {
                    pc.sendPackets(new S_SystemMessage("您今日已經解過該任務。"));
                    return;
                }
                if (pc.getQuest().isStart(L1PcQuest.BAO_QUEST_2)) {
                    pc.sendPackets(new S_SystemMessage("您已經接獲任務。"));
                    return;
                }
                if (pc.getMeteLevel() < ConfigQuest.Bao2_Quest_Levelmet) {
                    pc.sendPackets(new S_SystemMessage("您未達" + ConfigQuest.Bao2_Quest_Levelmet + "轉，無法接取任務。"));
                    return;
                }
                if (pc.getLevel() + ((pc.getMeteLevel() - ConfigQuest.Bao2_Quest_Levelmet) * 99) < ConfigQuest.Bao2_Quest_Lv) {
                    pc.sendPackets(new S_SystemMessage("您未達" + ConfigQuest.Bao2_Quest_Lv + "等，無法接取任務。"));
                    return;
                }
                pc.getQuest().set_step(L1PcQuest.BAO_QUEST_2, L1PcQuest.QUEST_START);
                pc.sendPackets(new S_SystemMessage("任務接獲成功。"));
                pc.sendPacketsX8(new S_EffectLocation(pc.getX(), pc.getY(), 14037));
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
