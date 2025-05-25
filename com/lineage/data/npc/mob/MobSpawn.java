package com.lineage.data.npc.mob;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.utils.L1SpawnUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MobSpawn extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(MobSpawn.class);
    private int _npcid;

    public static NpcExecutor get() {
        return new MobSpawn();
    }

    public int type() {
        return 8;
    }

    public L1PcInstance death(L1Character lastAttacker, L1NpcInstance npc) {
        try {
            L1PcInstance pc = CheckUtil.checkAtkPc(lastAttacker);
            if ((pc != null) && (_npcid != 0)) {
                L1Npc l1npc = NpcTable.get().getTemplate(_npcid);
                if (l1npc == null) {
                    _log.error("召喚NPC編號: " + _npcid + " 不存在!(mob.MobSpawn)");
                    return pc;
                }
                L1NpcInstance newnpc = L1SpawnUtil.spawnT(_npcid, npc.getX(), npc.getY(), npc.getMapId(), npc.getHeading(), 300);
                newnpc.onNpcAI();
                newnpc.startChat(0);
            }
            return pc;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    public void set_set(String[] set) {
        try {
            _npcid = Integer.parseInt(set[1]);
        } catch (Exception localException) {
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.mob.MobSpawn JD-Core Version: 0.6.2
 */