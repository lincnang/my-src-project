package com.lineage.data.npc.quest2;

import com.lineage.config.ConfigOtherSet2;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

public class Npc_Conquest2 extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Conquest2.class);
    private int _itemId;
    private int _itemCount;
    private boolean _checkDead;

    private Npc_Conquest2() {
    }

    public static NpcExecutor get() {
        return new Npc_Conquest2();
    }

    @Override
    public int type() {
        return 8;
    }

    @Override
    public L1PcInstance death(final L1Character lastAttacker, final L1NpcInstance npc) {
        try {
            final L1PcInstance pc = CheckUtil.checkAtkPc(lastAttacker);
            if (pc != null) {
                final ArrayList<L1Character> targetList = npc.getHateList().toTargetArrayList();
                if (!targetList.isEmpty()) {
                    for (L1Character cha : targetList) {
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance find_pc = (L1PcInstance) cha;
                            if (find_pc != null) {
                                find_pc.sendPacketsX10(new S_SkillSound(find_pc.getId(), 7783));
                            }
                        }
                    }
                    final List<L1PcInstance> pcList = new ArrayList<L1PcInstance>();
                    for (L1Character cha : targetList) {
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance find_pc = (L1PcInstance) cha;
                            if (_checkDead && (find_pc.getCurrentHp() <= 0 || find_pc.isDead())) {
                                continue;
                            }
                            if (!World.get().getVisibleObjects(npc, find_pc)) {
                                continue;
                            }
                            CreateNewItem.createNewItem(find_pc, _itemId, _itemCount);
                            if (find_pc.isGm() || find_pc.getLevel() <= ConfigOtherSet2.Npc_Conquest2) {
                                continue;
                            }
                            pcList.add(find_pc);
                        }
                    }
                    World.get().broadcastPacketToAll(new S_ServerMessage(3320, npc.getNameId(), pcList.size() >= 1 ? pcList.get(0).getName() : "", pcList.size() >= 2 ? pcList.get(1).getName() : "", pcList.size() >= 3 ? pcList.get(2).getName() : ""));
                }
            }
            return pc;
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public void set_set(String[] set) {
        try {
            _itemId = Integer.parseInt(set[1]);
        } catch (Exception e) {
        }
        try {
            _itemCount = Integer.parseInt(set[2]);
        } catch (Exception e) {
        }
        try {
            _checkDead = Boolean.parseBoolean(set[3]);
        } catch (Exception e) {
        }
    }
}
