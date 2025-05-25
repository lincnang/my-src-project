package com.lineage.server.model.Instance;

import com.lineage.server.IdFactoryNpc;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_FollowerPack;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1FollowerInstance extends L1NpcInstance {
    private static final long serialVersionUID = 1L;
    private static final Log _log = LogFactory.getLog(L1FollowerInstance.class);

    public L1FollowerInstance(L1Npc template, L1NpcInstance target, L1Character master) {
        super(template);
        if (is(master)) {
            return;
        }
        _master = master;
        setId(IdFactoryNpc.get().nextId());
        set_showId(master.get_showId());
        setMaster(master);
        setX(target.getX());
        setY(target.getY());
        setMap(target.getMapId());
        setHeading(target.getHeading());
        setLightSize(target.getLightSize());
        target.setParalyzed(true);
        target.deleteMe();
        World.get().storeObject(this);
        World.get().addVisibleObject(this);
        for (L1PcInstance pc : World.get().getRecognizePlayer(this)) {
            onPerceive(pc);
        }
        onNpcAI();
        master.addFollower(this);
    }

    public boolean noTarget() {
        if (ATTACK != null) {
            L1PcInstance pc = null;
            if ((_master instanceof L1PcInstance)) {
                pc = (L1PcInstance) _master;
            }
            ATTACK.attack(pc, this);
        } else {
            for (L1Object object : World.get().getVisibleObjects(this)) {
                if ((object instanceof L1NpcInstance)) {
                    L1NpcInstance tgnpc = (L1NpcInstance) object;
                    if (tgnpc.getNpcTemplate().get_npcId() == 71061) {
                        if (getNpcTemplate().get_npcId() == 71062) {
                            if (getLocation().getTileLineDistance(_master.getLocation()) >= 3) {
                                continue;
                            }
                            L1PcInstance pc = (L1PcInstance) _master;
                            if ((pc.getX() < 32448) || (pc.getX() > 32452) || (pc.getY() < 33048) || (pc.getY() > 33052) || (pc.getMapId() != 440)) {
                                continue;
                            }
                            setParalyzed(true);
                            if (!pc.getInventory().checkItem(40711)) {
                                createNewItem(pc, 40711, 1L);
                                pc.getQuest().set_step(31, 3);
                            }
                            deleteMe();
                            return true;
                        }
                    }
                    if (tgnpc.getNpcTemplate().get_npcId() == 71074) {
                        if (getNpcTemplate().get_npcId() == 71075) {
                            if (getLocation().getTileLineDistance(_master.getLocation()) < 3) {
                                L1PcInstance pc = (L1PcInstance) _master;
                                if ((pc.getX() >= 32731) && (pc.getX() <= 32735) && (pc.getY() >= 32854) && (pc.getY() <= 32858) && (pc.getMapId() == 480)) {
                                    setParalyzed(true);
                                    if (!pc.getInventory().checkItem(40633)) {
                                        createNewItem(pc, 40633, 1L);
                                        pc.getQuest().set_step(34, 2);
                                    }
                                    setreSpawn(true);
                                    deleteMe();
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (destroyed()) {
            return true;
        }
        if (_master == null) {
            return true;
        }
        if ((_master.isDead()) || (getLocation().getTileLineDistance(_master.getLocation()) > 13)) {
            setParalyzed(true);
            spawn(getNpcTemplate().get_npcId(), getX(), getY(), getHeading(), getMapId());
            deleteMe();
            return true;
        }
        if ((_master != null) && (_master.getMapId() == getMapId())) {
            if ((getLocation().getTileLineDistance(_master.getLocation()) > 2) && (_npcMove != null)) {
                _npcMove.setDirectionMove(_npcMove.moveDirection(_master.getX(), _master.getY()));
                setSleepTime(calcSleepTime(getPassispeed(), 0));
            }
        }
        return false;
    }

    private boolean is(L1Character master) {
        if (master.getFollowerList().size() > 0) {
            return true;
        }
        return false;
    }

    public void onNpcAI() {
        if (isAiRunning()) {
            return;
        }
        startAI();
    }

    public synchronized void deleteMe() {
        _master.removeFollower(this);
        setMaster(null);
        getMap().setPassable(getLocation(), true);
        super.deleteMe();
    }

    public void onAction(L1PcInstance pc) {
        try {
            L1AttackMode attack = new L1AttackPc(pc, this);
            if (attack.calcHit()) {
                attack.calcDamage();
            }
            attack.action();
            attack.commit();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void onTalkAction(L1PcInstance player) {
        if (isDead()) {
            return;
        }
        switch (getNpcTemplate().get_npcId()) {
            case 71062:
                if (_master.equals(player)) {
                    player.sendPackets(new S_NPCTalkReturn(getId(), "kamit2"));
                } else {
                    player.sendPackets(new S_NPCTalkReturn(getId(), "kamit1"));
                }
                break;
            case 71075:
                if (_master.equals(player)) {
                    player.sendPackets(new S_NPCTalkReturn(getId(), "llizard2"));
                } else {
                    player.sendPackets(new S_NPCTalkReturn(getId(), "llizard1a"));
                }
                break;
        }
    }

    public void onPerceive(L1PcInstance perceivedFrom) {
        try {
            if (perceivedFrom.get_showId() != get_showId()) {
                return;
            }
            perceivedFrom.addKnownObject(this);
            perceivedFrom.sendPackets(new S_FollowerPack(this, perceivedFrom));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void createNewItem(L1PcInstance pc, int item_id, long count) {
        L1ItemInstance item = ItemTable.get().createItem(item_id);
        item.setCount(count);
        if (item != null) {
            if (pc.getInventory().checkAddItem(item, count) == 0) {
                pc.getInventory().storeItem(item);
            } else {
                item.set_showId(pc.get_showId());
                World.get().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
            }
            pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
        }
    }

    public void spawn(int npcId, int x, int y, int h, short m) {
        try {
            L1NpcInstance mob = L1SpawnUtil.spawn(npcId, x, y, m, h);
            L1QuestInstance newnpc = (L1QuestInstance) mob;
            newnpc.onNpcAI();
            newnpc.startChat(0);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void receiveDamage(L1Character attacker, int damage) {
        if (getMaxHp() > 100) {
            if ((getCurrentHp() > 0) && (!isDead())) {
                int newHp = getCurrentHp() - damage;
                if ((newHp <= 0) && (!isDead())) {
                    setCurrentHpDirect(0);
                    setDead(true);
                    setStatus(8);
                    Death death = new Death();
                    GeneralThreadPool.get().execute(death);
                }
                if (newHp > 0) {
                    setCurrentHp(newHp);
                }
            } else if (!isDead()) {
                setDead(true);
                setStatus(8);
                Death death = new Death();
                GeneralThreadPool.get().execute(death);
            }
        }
    }

    private class Death implements Runnable {
        private Death() {
        }

        public void run() {
            try {
                getMap().setPassable(getLocation(), true);
                setDeathProcessing(true);
                broadcastPacketAll(new S_DoActionGFX(getId(), 8));
                setDeathProcessing(false);
                startDeleteTimer(10);
            } catch (Exception e) {
                L1FollowerInstance._log.error(e.getLocalizedMessage(), e);
            }
        }
    }
}
