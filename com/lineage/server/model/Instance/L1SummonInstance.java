package com.lineage.server.model.Instance;

import com.lineage.config.ConfigAlt;
import com.lineage.server.IdFactoryNpc;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.drop.SetDrop;
import com.lineage.server.model.drop.SetDropExecutor;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class L1SummonInstance extends L1NpcInstance {
    private static final long serialVersionUID = 1L;
    private static final Log _log = LogFactory.getLog(L1SummonInstance.class);
    private static final int _summonTime = 3600;
    private static final Random _random = new Random();
    private int _currentPetStatus;
    private int _checkMove = 0;
    private boolean _tamed;
    private boolean _isReturnToNature = false;
    private int _time = 0;
    private int _tempModel = 3;

    /**
     * 召喚獸
     *
     */
    public L1SummonInstance(L1Npc template, L1Character master) {
        super(template);
        setId(IdFactoryNpc.get().nextId());
        set_showId(master.get_showId());
        set_time(_summonTime);
        setMaster(master);
        setX(master.getX() + _random.nextInt(5) - 2);
        setY(master.getY() + _random.nextInt(5) - 2);
        setMap(master.getMapId());
        setHeading(5);
        setLightSize(template.getLightSize());
        _currentPetStatus = 3;
        _tamed = false;
        World.get().storeObject(this);
        World.get().addVisibleObject(this);
        for (L1PcInstance pc : World.get().getRecognizePlayer(this)) {
            onPerceive(pc);
        }
        master.addPet(this);// 加入使用中寵物清單
        if ((master instanceof L1PcInstance)) {
            addMaster((L1PcInstance) master);
        } else if ((master instanceof L1NpcInstance)) {
            _currentPetStatus = 1;
        }
    }

    /**
     * 造屍術
     *
     */
    public L1SummonInstance(L1NpcInstance target, L1Character master, boolean isCreateZombie) {
        super(null);
        setId(IdFactoryNpc.get().nextId());
        set_showId(master.get_showId());
        if (isCreateZombie) {
            int npcId = 45065;
            final L1PcInstance pc = (L1PcInstance) master;
            final int level = pc.getLevel();
            if (pc.isWizard()) {
                if ((level >= 24) && (level <= 31)) {
                    npcId = 81183;
                } else if ((level >= 32) && (level <= 39)) {
                    npcId = 81184;
                } else if ((level >= 40) && (level <= 43)) {
                    npcId = 81185;
                } else if ((level >= 44) && (level <= 47)) {
                    npcId = 81186;
                } else if ((level >= 48) && (level <= 51)) {
                    npcId = 81187;
                } else if (level >= 52) {
                    npcId = 81188;
                }
            } else if ((pc.isElf()) && (level >= 48)) {
                npcId = 81183;
            }
            L1Npc template = NpcTable.get().getTemplate(npcId).clone();
            setting_template(template);
        } else {
            setting_template(target.getNpcTemplate());
            setCurrentHpDirect(target.getCurrentHp());
            setCurrentMpDirect(target.getCurrentMp());
        }
        set_time(_summonTime);
        setMaster(master);
        setX(target.getX());
        setY(target.getY());
        setMap(target.getMapId());
        setHeading(target.getHeading());
        setLightSize(target.getLightSize());
        setPetcost(6);
        if (((target instanceof L1MonsterInstance)) && (!((L1MonsterInstance) target).is_storeDroped())) {
            SetDropExecutor setDropExecutor = new SetDrop();
            setDropExecutor.setDrop(target, target.getInventory());
        }
        setInventory(target.getInventory());
        target.setInventory(null);
        _currentPetStatus = 3;
        _tamed = true;
        for (L1NpcInstance each : master.getPetList().values()) {
            each.targetRemove(target);
        }
        target.deleteMe();
        World.get().storeObject(this);
        World.get().addVisibleObject(this);
        for (final L1PcInstance pc : World.get().getRecognizePlayer(this)) {
            this.onPerceive(pc);
        }
        master.addPet(this);
        if ((master instanceof L1PcInstance)) {
            addMaster((L1PcInstance) master);
        }
    }

    public L1SummonInstance(final L1Npc template, final L1Character master, final int summonTime) {
        super(template);
        this.setId(IdFactoryNpc.get().nextId());
        this.set_showId(master.get_showId());
        this.set_time(summonTime);
        this.setMaster(master);
        this.setX(master.getX() + _random.nextInt(5) - 2);
        this.setY(master.getY() + _random.nextInt(5) - 2);
        this.setMap(master.getMapId());
        this.setHeading(5);
        this.setLightSize(template.getLightSize());
        _currentPetStatus = 3;
        _tamed = false;
        World.get().storeObject(this);
        World.get().addVisibleObject(this);
        for (final L1PcInstance pc : World.get().getRecognizePlayer(this)) {
            this.onPerceive(pc);
        }
        master.addPet(this);
        if (master instanceof L1PcInstance) {
            addMaster((L1PcInstance) master);
        } else if (master instanceof L1NpcInstance) {
            _currentPetStatus = 1;
        }
    }

    public boolean tamed() {
        return _tamed;
    }

    public boolean noTarget() {
        switch (_currentPetStatus) {
            case 3:
                return true;
            case 4:
                if ((_master != null) && (_master.getMapId() == getMapId()) && (getLocation().getTileLineDistance(_master.getLocation()) < 5)) {
                    if (_npcMove != null) {
                        int dir = _npcMove.targetReverseDirection(_master.getX(), _master.getY());
                        dir = _npcMove.checkObject(dir);
                        _npcMove.setDirectionMove(dir);
                        setSleepTime(calcSleepTime(getPassispeed(), 0));
                    }
                } else {
                    _currentPetStatus = 3;
                    return true;
                }
                break;
            case 5:
                if (((Math.abs(getHomeX() - getX()) > 1) || (Math.abs(getHomeY() - getY()) > 1)) && (_npcMove != null)) {
                    int dir = _npcMove.moveDirection(getHomeX(), getHomeY());
                    if (dir == -1) {
                        setHomeX(getX());
                        setHomeY(getY());
                    } else {
                        _npcMove.setDirectionMove(dir);
                        setSleepTime(calcSleepTime(getPassispeed(), 0));
                    }
                }
                break;
            default:
                if ((_master != null) && (_master.getMapId() == getMapId())) {
                    int location = getLocation().getTileLineDistance(_master.getLocation());
                    if ((location > 2) && (_npcMove != null)) {
                        int dir = _npcMove.moveDirection(_master.getX(), _master.getY());
                        if (dir == -1) {
                            _checkMove += 1;
                            if (_checkMove >= 10) {
                                _checkMove = 0;
                                _currentPetStatus = 3;
                                return true;
                            }
                        } else {
                            _checkMove = 0;
                            _npcMove.setDirectionMove(dir);
                            setSleepTime(calcSleepTime(getPassispeed(), 0));
                        }
                    }
                } else {
                    _currentPetStatus = 3;
                    return true;
                }
                break;
        }
        return false;
    }

    public void receiveDamage(L1Character attacker, int damage) {
        ISASCAPE = false;
        if (getCurrentHp() > 0) {
            if (damage > 0) {
                setHate(attacker, 0);
                removeSkillEffect(66);
                removeSkillEffect(212);
                if (!isExsistMaster()) {
                    _currentPetStatus = 1;
                    setTarget(attacker);
                }
            }
            if (((attacker instanceof L1PcInstance)) && (damage > 0)) {
                L1PcInstance player = (L1PcInstance) attacker;
                player.setPetTarget(this);
            }
            int newHp = getCurrentHp() - damage;
            if (newHp <= 0) {
                Death(attacker);
            } else {
                setCurrentHp(newHp);
            }
        } else if (!isDead()) {
            _log.error("NPC hp減少處理失敗 可能原因: 初始hp為0(" + getNpcId() + ")");
            Death(attacker);
        }
    }

    public synchronized void Death(L1Character lastAttacker) {
        if (!isDead()) {
            setDead(true);
            setCurrentHp(0);
            setStatus(8);
            getMap().setPassable(getLocation(), true);
            L1Inventory targetInventory = null;
            if ((_master != null) && (_master.getInventory() != null)) {
                targetInventory = _master.getInventory();
            }
            List<L1ItemInstance> items = _inventory.getItems();
            for (L1ItemInstance item : items) {
                if (targetInventory != null) {
                    if (_master.getInventory().checkAddItem(item, item.getCount()) == 0) {
                        _inventory.tradeItem(item, item.getCount(), targetInventory);
                        ((L1PcInstance) _master).sendPackets(new S_ServerMessage(143, getName(), item.getLogName()));
                    } else {
                        item.set_showId(get_showId());
                        targetInventory = World.get().getInventory(getX(), getY(), getMapId());
                        _inventory.tradeItem(item, item.getCount(), targetInventory);
                    }
                } else {
                    item.set_showId(get_showId());
                    targetInventory = World.get().getInventory(getX(), getY(), getMapId());
                    _inventory.tradeItem(item, item.getCount(), targetInventory);
                }
            }
            if (_tamed) {
                broadcastPacketAll(new S_DoActionGFX(getId(), 8));
                startDeleteTimer(ConfigAlt.NPC_DELETION_TIME * 2);
            } else {
                deleteMe();
            }
        }
    }

    public synchronized void returnToNature() {
        _isReturnToNature = true;
        if (!_tamed) {
            getMap().setPassable(getLocation(), true);
            L1Inventory targetInventory = null;
            if ((_master != null) && (_master.getInventory() != null)) {
                targetInventory = _master.getInventory();
            }
            List<L1ItemInstance> items = _inventory.getItems();
            for (L1ItemInstance item : items) {
                if (targetInventory != null) {
                    if (_master.getInventory().checkAddItem(item, item.getCount()) == 0) {
                        _inventory.tradeItem(item, item.getCount(), targetInventory);
                        ((L1PcInstance) _master).sendPackets(new S_ServerMessage(143, getName(), item.getLogName()));
                    } else {
                        item.set_showId(get_showId());
                        targetInventory = World.get().getInventory(getX(), getY(), getMapId());
                        _inventory.tradeItem(item, item.getCount(), targetInventory);
                    }
                } else {
                    item.set_showId(get_showId());
                    targetInventory = World.get().getInventory(getX(), getY(), getMapId());
                    _inventory.tradeItem(item, item.getCount(), targetInventory);
                }
            }
            deleteMe();
        } else {
            liberate();
        }
    }

    public synchronized void deleteMe() {
        if (_master != null) {
            _master.removePet(this);
        }
        if (_destroyed) {
            return;
        }
        if ((!_tamed) && (!_isReturnToNature)) {
            broadcastPacketX8(new S_SkillSound(getId(), 169));
        }
        super.deleteMe();
    }

    public void liberate() {
        L1MonsterInstance monster = new L1MonsterInstance(getNpcTemplate());
        monster.setId(IdFactoryNpc.get().nextId());
        monster.setX(getX());
        monster.setY(getY());
        monster.setMap(getMapId());
        monster.setHeading(getHeading());
        monster.set_storeDroped(true);
        monster.setInventory(getInventory());
        setInventory(null);
        monster.setCurrentHpDirect(getCurrentHp());
        monster.setCurrentMpDirect(getCurrentMp());
        monster.setExp(0L);
        deleteMe();
        World.get().storeObject(monster);
        World.get().addVisibleObject(monster);
    }

    public void setTarget(L1Character target) {
        if ((target != null) && ((_currentPetStatus == 1) || (_currentPetStatus == 2) || (_currentPetStatus == 5))) {
            setHate(target, 0);
            if (!isAiRunning()) {
                startAI();
            }
        }
    }

    public void setMasterTarget(L1Character target) {
        if ((target != null) && ((_currentPetStatus == 1) || (_currentPetStatus == 5))) {
            setHate(target, 10);
            if (!isAiRunning()) {
                startAI();
            }
        }
    }

    public void onAction(L1PcInstance player) {
        if (player == null) {
            return;
        }
        L1Character cha = getMaster();
        if (cha == null) {
            return;
        }
        L1PcInstance master = (L1PcInstance) cha;
        if (master.isTeleport()) {
            return;
        }
        if (master.equals(player)) {
            L1AttackMode attack_mortion = new L1AttackPc(player, this);
            attack_mortion.action();
            return;
        }
        if (((isSafetyZone()) || (player.isSafetyZone())) && (isExsistMaster())) {
            L1AttackMode attack_mortion = new L1AttackPc(player, this);
            attack_mortion.action();
            return;
        }
        if (player.checkNonPvP(player, this)) {
            return;
        }
        L1AttackMode attack = new L1AttackPc(player, this);
        if (attack.calcHit()) {
            attack.calcDamage();
        }
        attack.action();
        attack.commit();
    }

    public void onTalkAction(L1PcInstance player) {
        if (isDead()) {
            return;
        }
        if (_master.equals(player)) {
            player.sendPackets(new S_PetMenuPacket(this, 0));
        }
    }

    public void onFinalAction(L1PcInstance player, String action) {
        int status = Integer.parseInt(action);
        switch (status) {
            case 0:
            case 6:
                if (_tamed) {
                    liberate();
                } else {
                    Death(null);
                }
                break;
            default:
                Object[] petList = _master.getPetList().values().toArray();
                for (Object petObject : petList) {
                    if ((petObject instanceof L1SummonInstance)) {
                        L1SummonInstance summon = (L1SummonInstance) petObject;
                        summon.set_currentPetStatus(status);
                    }
                }
        }
    }

    public void onPerceive(L1PcInstance perceivedFrom) {
        try {
            if (perceivedFrom.get_showId() != get_showId()) {
                return;
            }
            perceivedFrom.addKnownObject(this);
            perceivedFrom.sendPackets(new S_NPCPack_Summon(this, perceivedFrom));
            if ((getMaster() != null) && (perceivedFrom.getId() == getMaster().getId())) {
                //perceivedFrom.sendPackets(new S_HPMeter(getId(), 100 * getCurrentHp() / getMaxHp()));
                // 7.6
                perceivedFrom.sendPackets(new S_HPMeter(getId(), (100 * getCurrentHp()) / getMaxHp(), 0xff));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void onItemUse() {
        if (!isActived()) {
            useItem(1, 100);
        }
        if (getCurrentHp() * 100 / getMaxHp() < 40) {
            useItem(0, 100);
        }
    }

    public void onGetItem(L1ItemInstance item) {
        if (getNpcTemplate().get_digestitem() > 0) {
            setDigestItem(item);
        }
        Arrays.sort(healPotions);
        Arrays.sort(haestPotions);
        if (Arrays.binarySearch(healPotions, item.getItem().getItemId()) >= 0) {
            if (getCurrentHp() != getMaxHp()) {
                useItem(0, 100);
            }
        } else if (Arrays.binarySearch(haestPotions, item.getItem().getItemId()) >= 0) {
            useItem(1, 100);
        }
    }

    public void setCurrentHp(int i) {
        int currentHp = Math.min(i, getMaxHp());
        if (getCurrentHp() == currentHp) {
            return;
        }
        setCurrentHpDirect(currentHp);
        if ((_master instanceof L1PcInstance)) {
            final int hpRatio = (100 * currentHp) / getMaxHp();
            L1PcInstance master = (L1PcInstance) _master;
            //master.sendPackets(new S_HPMeter(getId(), hpRatio));
            master.sendPackets(new S_HPMeter(getId(), hpRatio, 0xff));// 7.6
        }
    }

    public void setCurrentMp(int i) {
        int currentMp = Math.min(i, getMaxMp());
        if (getCurrentMp() == currentMp) {
            return;
        }
        setCurrentMpDirect(currentMp);
    }

    public int get_currentPetStatus() {
        return _currentPetStatus;
    }

    public void set_currentPetStatus(int i) {
        _currentPetStatus = i;
        set_tempModel();
        switch (_currentPetStatus) {
            case 5:
                setHomeX(getX());
                setHomeY(getY());
                break;
            case 3:
                allTargetClear();
                break;
            case 4:
            default:
                if (!isAiRunning()) {
                    startAI();
                }
                break;
        }
    }

    /**
     * 主人是否在線上
     *
     */
    public boolean isExsistMaster() {
        boolean isExsistMaster = true;
        if (getMaster() != null) {
            String masterName = getMaster().getName();
            if (World.get().getPlayer(masterName) == null) {
                isExsistMaster = false;
            }
        }
        return isExsistMaster;
    }

    public int get_time() {
        return _time;
    }

    public void set_time(int time) {
        _time = time;
    }

    public void set_tempModel() {
        _tempModel = _currentPetStatus;
    }

    public void get_tempModel() {
        _currentPetStatus = _tempModel;
    }
}
