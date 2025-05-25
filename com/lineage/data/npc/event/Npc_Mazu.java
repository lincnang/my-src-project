package com.lineage.data.npc.event;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.lineage.server.model.skill.L1SkillId.Mazu;

;

public class Npc_Mazu extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Mazu.class);
    private int _mazutime;

    public static NpcExecutor get() {
        return new Npc_Mazu();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (pc.getQuest().get_step(30000) != 255) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_e_g_01"));
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_e_g_02"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        try {
            // String ole = _playList.get(pc.getId());
            //
            // if (ole != null) {
            // pc.sendPackets(new S_SystemMessage("你今天已經使用過媽祖了。"));
            // return;
            // }
            if (pc.getQuest().get_step(30000) == 255) {
                pc.sendPackets(new S_SystemMessage("你今天已經拜過媽祖了。"));
                return;
            }
            if (cmd.equalsIgnoreCase("0") && (pc.getQuest().get_step(30000) != 255)) {
                // _playList.put(pc.getId(), pc.getName());
                //pc.setMazuTime(1);
                pc.set_mazu(true);
                pc.setSkillEffect(Mazu, _mazutime * 60 * 1000);
                pc.getQuest().set_step(30000, 255);
                pc.sendPacketsX8(new S_SkillSound(pc.getId(), 7321));
                pc.set_mazu_time(_mazutime * 60);
                pc.sendPackets(new S_SystemMessage("獲得媽祖狀態。"));
            } else {
                pc.sendPackets(new S_SystemMessage("你今天已經拜過媽祖了。"));
            }
            pc.sendPackets(new S_CloseList(pc.getId()));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void set_set(String[] set) {
        try {
            _mazutime = Integer.parseInt(set[1]);
        } catch (Exception e) {
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.event.Npc_Mazu JD-Core Version: 0.6.2
 */