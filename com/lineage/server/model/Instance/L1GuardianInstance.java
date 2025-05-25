package com.lineage.server.model.Instance;

import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigRate;
import com.lineage.server.datatables.NPCTalkDataTable;
import com.lineage.server.model.*;
import com.lineage.server.model.drop.DropShare;
import com.lineage.server.model.drop.DropShareExecutor;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.CalcExp;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class L1GuardianInstance extends L1NpcInstance {
    private static final long serialVersionUID = 1L;
    private static final Log _log = LogFactory.getLog(L1GuardianInstance.class);
    private Random _random = new Random();
    private L1GuardianInstance _npc = this;

    public L1GuardianInstance(L1Npc template) {
        super(template);
    }

    private static L1PcInstance searchTarget(L1GuardianInstance npc) {
        L1PcInstance targetPlayer = null;
        for (L1PcInstance pc : World.get().getVisiblePlayer(npc)) {
            try {
                TimeUnit.MILLISECONDS.sleep(10L);
            } catch (InterruptedException e) {
                _log.error(e.getLocalizedMessage(), e);
            }
            if ((pc.getCurrentHp() > 0) && (!pc.isDead()) && (!pc.isGm()) && (!pc.isGhost())) {
                if (npc.get_showId() == pc.get_showId()) {
                    if ((!pc.isInvisble()) || (npc.getNpcTemplate().is_agrocoi())) {
                        if (!pc.isElf()) {
                            targetPlayer = pc;
                            npc.wideBroadcastPacket(new S_NpcChatShouting(npc, "$804"));
                            break;
                        }
                        if ((pc.isElf()) && (pc.isWantedForElf())) {
                            targetPlayer = pc;
                            npc.wideBroadcastPacket(new S_NpcChat(npc, "$815"));
                            break;
                        }
                    }
                }
            }
        }
        return targetPlayer;
    }

    public void searchTarget() {
        L1PcInstance targetPlayer = searchTarget(this);
        if (targetPlayer != null) {
            _hateList.add(targetPlayer, 0);
            _target = targetPlayer;
        }
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

    public void onAction(L1PcInstance player) {
        try {
            if ((player.getType() == 2) && (player.getCurrentWeapon() == 0) && (player.isElf())) {
                L1AttackMode attack = new L1AttackPc(player, this);
                if (attack.calcHit()) {
                    if (getNpcTemplate().get_npcId() == 70848) {
                        int chance = _random.nextInt(100) + 1;
                        if (this.hasSkillEffect(77779)) {
                            this.wideBroadcastPacket(new S_NpcChatShouting(this, "我現在沒有樹枝，樹皮，水果。請稍後再來..."));
                        } else if (chance <= 2 && this.hasSkillEffect(77778)) {
                            player.getInventory().storeItem(40506, 1L);
                            player.sendPackets(new S_ServerMessage(143, "$755", "$794"));
                            this.killSkillEffectTimer(77778);
                            this.setSkillEffect(77779, 1200 * 1000);
                        } else if ((chance <= 20) && (chance > 10) && (this.hasSkillEffect(77777))) {
                            player.getInventory().storeItem(40505, 1L);
                            player.sendPackets(new S_ServerMessage(143, "$755", "$770"));
                            this.killSkillEffectTimer(77777);
                            this.setSkillEffect(77778, 300 * 1000);
                        } else if ((chance <= 20) && (chance > 10) && (this.hasSkillEffect(77776))) {
                            player.getInventory().storeItem(40505, 1L);
                            player.sendPackets(new S_ServerMessage(143, "$755", "$770"));
                            this.killSkillEffectTimer(77776);
                            this.setSkillEffect(77777, 300 * 1000);
                        } else if ((chance <= 20) && (chance > 10) && (this.hasSkillEffect(77775))) {
                            player.getInventory().storeItem(40505, 1L);
                            player.sendPackets(new S_ServerMessage(143, "$755", "$770"));
                            this.killSkillEffectTimer(77775);
                            this.setSkillEffect(77776, 300 * 1000);
                        } else if ((chance <= 30) && (chance > 20) && (this.hasSkillEffect(77774))) {
                            player.getInventory().storeItem(40507, 6L);
                            player.sendPackets(new S_ServerMessage(143, "$755", "$763"));
                            this.killSkillEffectTimer(77774);
                            this.setSkillEffect(77775, 300 * 1000);
                        } else if ((chance <= 30) && (chance > 20) && (this.hasSkillEffect(77773))) {
                            player.getInventory().storeItem(40507, 6L);
                            player.sendPackets(new S_ServerMessage(143, "$755", "$763"));
                            this.killSkillEffectTimer(77773);
                            this.setSkillEffect(77774, 300 * 1000);
                        } else if ((chance <= 30) && (chance > 20) && (this.hasSkillEffect(77772))) {
                            player.getInventory().storeItem(40507, 6L);
                            player.sendPackets(new S_ServerMessage(143, "$755", "$763"));
                            this.killSkillEffectTimer(77772);
                            this.setSkillEffect(77773, 300 * 1000);
                        } else if ((chance <= 30) && (chance > 20) && (this.hasSkillEffect(77771))) {
                            player.getInventory().storeItem(40507, 6L);
                            player.sendPackets(new S_ServerMessage(143, "$755", "$763"));
                            this.killSkillEffectTimer(77771);
                            this.setSkillEffect(77772, 300 * 1000);
                        } else if ((chance <= 30) && (chance > 20)) {
                            player.getInventory().storeItem(40507, 6L);
                            player.sendPackets(new S_ServerMessage(143, "$755", "$763"));
                            this.setSkillEffect(77771, 300 * 1000);
                        }
                    }
                    if (getNpcTemplate().get_npcId() == 70850) {
                        int chance = _random.nextInt(100) + 1;
                        if (this.hasSkillEffect(77775)) {
                            this.wideBroadcastPacket(new S_NpcChatShouting(this, "我現在沒有潘毛，請稍後再來..."));
                        } else if (chance <= 30 && this.hasSkillEffect(77774)) {
                            player.getInventory().storeItem(40519, 5L);
                            player.sendPackets(new S_ServerMessage(143, "$753", "$760 (5)"));
                            this.killSkillEffectTimer(77774);
                            this.setSkillEffect(77775, 1200 * 1000);
                        } else if (chance <= 30 && this.hasSkillEffect(77773)) {
                            player.getInventory().storeItem(40519, 5L);
                            player.sendPackets(new S_ServerMessage(143, "$753", "$760 (5)"));
                            this.killSkillEffectTimer(77773);
                            this.setSkillEffect(77774, 300 * 1000);
                        } else if (chance <= 30 && this.hasSkillEffect(77772)) {
                            player.getInventory().storeItem(40519, 5L);
                            player.sendPackets(new S_ServerMessage(143, "$753", "$760 (5)"));
                            this.killSkillEffectTimer(77772);
                            this.setSkillEffect(77773, 300 * 1000);
                        } else if (chance <= 30 && this.hasSkillEffect(77771)) {
                            player.getInventory().storeItem(40519, 5L);
                            player.sendPackets(new S_ServerMessage(143, "$753", "$760 (5)"));
                            this.killSkillEffectTimer(77771);
                            this.setSkillEffect(77772, 300 * 1000);
                        } else if (chance <= 30) {
                            player.getInventory().storeItem(40519, 5L);
                            player.sendPackets(new S_ServerMessage(143, "$753", "$760 (5)"));
                            this.setSkillEffect(77771, 300 * 1000);
                        }
                    }
                    if (getNpcTemplate().get_npcId() == 70846) {
                        int chance = _random.nextInt(100) + 1;
                        if (this.hasSkillEffect(77775)) {
                            this.wideBroadcastPacket(new S_NpcChatShouting(this, "我現在沒有網，請稍後再來..."));
                        } else if (chance <= 30 && this.hasSkillEffect(77774)) {
                            player.getInventory().storeItem(40503, 1L);
                            player.sendPackets(new S_ServerMessage(143, "$752", "$769"));
                            this.killSkillEffectTimer(77774);
                            this.setSkillEffect(77775, 1200 * 1000);
                        } else if (chance <= 30 && this.hasSkillEffect(77773)) {
                            player.getInventory().storeItem(40503, 1L);
                            player.sendPackets(new S_ServerMessage(143, "$752", "$769"));
                            this.killSkillEffectTimer(77773);
                            this.setSkillEffect(77774, 300 * 1000);
                        } else if (chance <= 30 && this.hasSkillEffect(77772)) {
                            player.getInventory().storeItem(40503, 1L);
                            player.sendPackets(new S_ServerMessage(143, "$752", "$769"));
                            this.killSkillEffectTimer(77772);
                            this.setSkillEffect(77773, 300 * 1000);
                        } else if (chance <= 30 && this.hasSkillEffect(77771)) {
                            player.getInventory().storeItem(40503, 1L);
                            player.sendPackets(new S_ServerMessage(143, "$752", "$769"));
                            this.killSkillEffectTimer(77771);
                            this.setSkillEffect(77772, 300 * 1000);
                        } else if (chance <= 30) {
                            player.getInventory().storeItem(40503, 1L);
                            player.sendPackets(new S_ServerMessage(143, "$752", "$769"));
                            this.setSkillEffect(77771, 300 * 1000);
                        }
                    }
                    attack.calcDamage();
                }
                attack.action();
                attack.commit();
            } else if ((getCurrentHp() > 0) && (!isDead())) {
                L1AttackMode attack = new L1AttackPc(player, this);
                if (attack.calcHit()) {
                    attack.calcDamage();
                }
                attack.action();
                attack.commit();
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void onTalkAction(L1PcInstance player) {
        int objid = getId();
        L1NpcTalkData talking = NPCTalkDataTable.get().getTemplate(getNpcTemplate().get_npcId());
        L1Object object = World.get().findObject(getId());
        L1NpcInstance target = (L1NpcInstance) object;
        if (talking != null) {
            int pcx = player.getX();
            int pcy = player.getY();
            int npcx = target.getX();
            int npcy = target.getY();
            if ((pcx == npcx) && (pcy < npcy)) {
                setHeading(0);
            } else if ((pcx > npcx) && (pcy < npcy)) {
                setHeading(1);
            } else if ((pcx > npcx) && (pcy == npcy)) {
                setHeading(2);
            } else if ((pcx > npcx) && (pcy > npcy)) {
                setHeading(3);
            } else if ((pcx == npcx) && (pcy > npcy)) {
                setHeading(4);
            } else if ((pcx < npcx) && (pcy > npcy)) {
                setHeading(5);
            } else if ((pcx < npcx) && (pcy == npcy)) {
                setHeading(6);
            } else if ((pcx < npcx) && (pcy < npcy)) {
                setHeading(7);
            }
            broadcastPacketAll(new S_ChangeHeading(this));
            if (player.getLawful() < -1000) {
                player.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
            } else {
                player.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
            }
            set_stop_time(10);
            setRest(true);
        }
    }

    public void receiveDamage(L1Character attacker, int damage) {
        ISASCAPE = false;
        if (attacker instanceof L1PcInstance && (damage > 0)) {
            L1PcInstance pc = (L1PcInstance) attacker;
            if ((pc.getType() != 2) || (pc.getCurrentWeapon() != 0)) {
                if ((getCurrentHp() > 0) && (!isDead())) {
                    setHate(attacker, damage);
                    removeSkillEffect(66);
                    removeSkillEffect(212);
                    onNpcAI();
                    serchLink(pc, getNpcTemplate().get_family());
                    pc.setPetTarget(this);
                    int newHp = getCurrentHp() - damage;
                    if ((newHp <= 0) && (!isDead())) {
                        setCurrentHpDirect(0);
                        setDead(true);
                        setStatus(8);
                        Death death = new Death(attacker);
                        GeneralThreadPool.get().execute(death);
                    }
                    if (newHp > 0) {
                        setCurrentHp(newHp);
                    }
                } else if (!isDead()) {
                    setDead(true);
                    setStatus(8);
                    Death death = new Death(attacker);
                    GeneralThreadPool.get().execute(death);
                }
            }
        }
    }

    public void setCurrentHp(int i) {
        int currentHp = Math.min(i, getMaxHp());
        if (getCurrentHp() == currentHp) {
            return;
        }
        setCurrentHpDirect(currentHp);
    }

    public void setCurrentMp(int i) {
        int currentMp = Math.min(i, getMaxMp());
        if (getCurrentMp() == currentMp) {
            return;
        }
        setCurrentMpDirect(currentMp);
    }

    public void onFinalAction(final L1PcInstance pc, final String s) {
        super.onFinalAction(pc, s);
    }

    public void doFinalAction(L1PcInstance player) {
    }

    class Death implements Runnable {
        L1Character _lastAttacker;

        public Death(L1Character lastAttacker) {
            _lastAttacker = lastAttacker;
        }

        public void run() {
            setDeathProcessing(true);
            setCurrentHpDirect(0);
            setDead(true);
            setStatus(8);
            int targetobjid = getId();
            getMap().setPassable(getLocation(), true);
            broadcastPacketAll(new S_DoActionGFX(targetobjid, 8));
            L1PcInstance player = null;
            if ((_lastAttacker instanceof L1PcInstance)) {
                player = (L1PcInstance) _lastAttacker;
            } else if ((_lastAttacker instanceof L1PetInstance)) {
                player = (L1PcInstance) ((L1PetInstance) _lastAttacker).getMaster();
            } else if ((_lastAttacker instanceof L1SummonInstance)) {
                player = (L1PcInstance) ((L1SummonInstance) _lastAttacker).getMaster();
            } else if ((_lastAttacker instanceof L1IllusoryInstance)) {
                player = (L1PcInstance) ((L1IllusoryInstance) _lastAttacker).getMaster();
            } else if ((_lastAttacker instanceof L1EffectInstance)) {
                player = (L1PcInstance) ((L1EffectInstance) _lastAttacker).getMaster();
            }
            if (player != null) {
                ArrayList<?> targetList = _hateList.toTargetArrayList();
                ArrayList<Integer> hateList = _hateList.toHateArrayList();
                long exp = getExp();
                CalcExp.calcExp(player, targetobjid, targetList, hateList, exp);
                ArrayList<L1Character> dropTargetList = _dropHateList.toTargetArrayList();
                ArrayList<Integer> dropHateList = _dropHateList.toHateArrayList();
                try {
                    DropShareExecutor dropShareExecutor = new DropShare();
                    dropShareExecutor.dropShare(_npc, dropTargetList, dropHateList);
                } catch (Exception e) {
                    L1GuardianInstance._log.error(e.getLocalizedMessage(), e);
                }
                player.addKarma((int) (getKarma() * ConfigRate.RATE_KARMA));
            }
            setDeathProcessing(false);
            setKarma(0);
            setExp(0L);
            allTargetClear();
            startDeleteTimer(ConfigAlt.NPC_DELETION_TIME);
        }
    }
}
