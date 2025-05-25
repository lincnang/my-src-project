package com.lineage.server.model.Instance;

import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.model.L1Character;
import com.lineage.server.serverpackets.S_NPCPack;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.TimeUnit;

public class L1MonsterGuardInstance extends L1NpcInstance {
    private static final long serialVersionUID = 1L;
    private static final Log _log = LogFactory.getLog(L1MonsterGuardInstance.class);

    public L1MonsterGuardInstance(L1Npc template) {
        super(template);
    }

    public void onPerceive(L1PcInstance perceivedFrom) {
        try {
            if (perceivedFrom.get_showId() != get_showId()) {
                return;
            }
            perceivedFrom.addKnownObject(this);
            perceivedFrom.sendPackets(new S_NPCPack(this));
            if (getCurrentHp() > 0) {
                onNpcAI();
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void searchTarget() {
        L1PcInstance targetPlayer = searchTarget(this);
        if (targetPlayer != null) {
            _hateList.add(targetPlayer, 0);
            _target = targetPlayer;
        } else {
            ISASCAPE = false;
        }
    }

    private L1PcInstance searchTarget(L1MonsterGuardInstance npc) {
        L1PcInstance targetPlayer = null;
        for (L1PcInstance pc : World.get().getVisiblePlayer(npc)) {
            try {
                TimeUnit.MILLISECONDS.sleep(10L);
            } catch (InterruptedException e) {
                _log.error(e.getLocalizedMessage(), e);
            }
            if (pc.getCurrentHp() > 0) {
                if (!pc.isDead()) {
                    if (!pc.isGhost()) {
                        if (!pc.isGm()) {
                            if (npc.get_showId() == pc.get_showId()) {
                                if ((pc.getClan() == null) || (pc.getClan().getCastleId() != 2)) {
                                    boolean isCheck = false;
                                    if (!pc.isInvisble()) {
                                        isCheck = true;
                                    }
                                    if (npc.getNpcTemplate().is_agrocoi()) {
                                        isCheck = true;
                                    }
                                    if (isCheck) {
                                        if ((pc.hasSkillEffect(67)) && (npc.getNpcTemplate().is_agrososc())) {
                                            targetPlayer = pc;
                                            return targetPlayer;
                                        }
                                        if (npc.getNpcTemplate().is_agro()) {
                                            targetPlayer = pc;
                                            return targetPlayer;
                                        }
                                        if ((npc.getNpcTemplate().is_agrogfxid1() >= 0) && (pc.getGfxId() == npc.getNpcTemplate().is_agrogfxid1())) {
                                            targetPlayer = pc;
                                            return targetPlayer;
                                        }
                                        if ((npc.getNpcTemplate().is_agrogfxid2() >= 0) && (pc.getGfxId() == npc.getNpcTemplate().is_agrogfxid2())) {
                                            targetPlayer = pc;
                                            return targetPlayer;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return targetPlayer;
    }

    public void setLink(L1Character cha) {
        if (get_showId() != cha.get_showId()) {
            return;
        }
        if ((cha != null) && (_hateList.isEmpty())) {
            _hateList.add(cha, 0);
            checkTarget();
        }
    }

    public void onNpcAI() {
        if (isAiRunning()) {
            return;
        }
        setActived(false);
        startAI();
    }

    public void onAction(L1PcInstance pc) {
        if (ATTACK != null) {
            ATTACK.attack(pc, this);
        }
        if ((getCurrentHp() > 0) && (!isDead())) {
            L1AttackMode attack = new L1AttackPc(pc, this);
            if (attack.calcHit()) {
                attack.calcDamage();
            }
            attack.action();
            attack.commit();
        }
    }

    public void ReceiveManaDamage(L1Character attacker, int mpDamage) {
    }

    public void receiveDamage(L1Character attacker, int damage) {
    }

    public void setCurrentHp(int i) {
    }

    public void setCurrentMp(int i) {
    }
}
