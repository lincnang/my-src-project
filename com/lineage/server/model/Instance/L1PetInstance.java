package com.lineage.server.model.Instance;

import com.lineage.server.IdFactory;
import com.lineage.server.IdFactoryNpc;
import com.lineage.server.datatables.ExpTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.PetItemTable;
import com.lineage.server.datatables.PetTypeTable;
import com.lineage.server.datatables.lock.PetReading;
import com.lineage.server.model.*;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1Pet;
import com.lineage.server.templates.L1PetItem;
import com.lineage.server.templates.L1PetType;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class L1PetInstance extends L1NpcInstance {
    private static final long serialVersionUID = 1L;
    private static final Log _log = LogFactory.getLog(L1PetInstance.class);
    private static Random _random = new Random();
    private int _currentPetStatus;
    private int _checkMove = 0;
    private L1PcInstance _petMaster;
    private int _itemObjId;
    private L1PetType _type;
    private int _expPercent;
    private L1ItemInstance _weapon;
    private L1ItemInstance _armor;
    private int _hitByWeapon;
    private int _damageByWeapon;
    private int _tempModel = 3;

    /**
     * GM用寵物
     *
     */
    public L1PetInstance(L1Npc template, L1PcInstance master) {
        super(template);
        _petMaster = master;
        _itemObjId = -1;
        _type = null;
        setId(IdFactoryNpc.get().nextId());
        set_showId(master.get_showId());
        setName(template.get_name());
        setLevel(template.get_level());
        setMaxHp(template.get_hp());
        setCurrentHpDirect(template.get_hp());
        setMaxMp(template.get_mp());
        setCurrentMpDirect(template.get_mp());
        setExp(template.get_exp());
        setExpPercent(ExpTable.getExpPercentage(template.get_level(), template.get_exp()));
        setLawful(template.get_lawful());
        setTempLawful(template.get_lawful());
        setMaster(master);
        setX(master.getX() + _random.nextInt(5) - 2);
        setY(master.getY() + _random.nextInt(5) - 2);
        setMap(master.getMapId());
        setHeading(master.getHeading());
        setLightSize(template.getLightSize());
        _currentPetStatus = 3;
        World.get().storeObject(this);
        World.get().addVisibleObject(this);
        for (L1PcInstance pc : World.get().getRecognizePlayer(this)) {
            onPerceive(pc);
        }
        master.addPet(this);// 加入使用中寵物清單
        addMaster(master);
    }

    /**
     * 寵物領取
     *
     * @param template NPC資料
     * @param master   主人
     * @param pet      寵物資料
     */
    public L1PetInstance(L1Npc template, L1PcInstance master, L1Pet pet) {
        super(template);
        _petMaster = master;
        _itemObjId = pet.get_itemobjid();
        _type = PetTypeTable.getInstance().get(template.get_npcId());
        setId(pet.get_objid());
        set_showId(master.get_showId());
        setName(pet.get_name());
        setLevel(pet.get_level());
        setMaxHp(pet.get_hp());
        setCurrentHpDirect(pet.get_hp());
        setMaxMp(pet.get_mp());
        setCurrentMpDirect(pet.get_mp());
        setExp(pet.get_exp());
        setExpPercent(ExpTable.getExpPercentage(pet.get_level(), pet.get_exp()));
        setLawful(pet.get_lawful());
        setTempLawful(pet.get_lawful());
        setMaster(master);
        setX(master.getX() + _random.nextInt(5) - 2);
        setY(master.getY() + _random.nextInt(5) - 2);
        setMap(master.getMapId());
        setHeading(5);
        setLightSize(template.getLightSize());
        _currentPetStatus = 3;
        World.get().storeObject(this);
        World.get().addVisibleObject(this);
        for (L1PcInstance pc : World.get().getRecognizePlayer(this)) {
            onPerceive(pc);
        }
        master.addPet(this);
        addMaster(master);
    }

    /**
     * 馴養寵物
     *
     */
    public L1PetInstance(L1NpcInstance target, L1PcInstance master, int itemobjid) {
        super(null);
        _petMaster = master;
        _itemObjId = itemobjid;
        _type = PetTypeTable.getInstance().get(target.getNpcTemplate().get_npcId());
        setId(IdFactory.get().nextId());
        set_showId(master.get_showId());
        setting_template(target.getNpcTemplate());
        setCurrentHpDirect(target.getCurrentHp());
        setCurrentMpDirect(target.getCurrentMp());
        setExp(750L);
        setExpPercent(0);
        setLawful(0);
        setTempLawful(0);
        setMaster(master);
        setX(target.getX());
        setY(target.getY());
        setMap(target.getMapId());
        setHeading(target.getHeading());
        setLightSize(target.getLightSize());
        setPetcost(6);
        setInventory(target.getInventory());
        target.setInventory(null);
        _currentPetStatus = 3;
        target.deleteMe();
        World.get().storeObject(this);
        World.get().addVisibleObject(this);
        for (L1PcInstance pc : World.get().getRecognizePlayer(this)) {
            onPerceive(pc);
        }
        master.addPet(this);
        PetReading.get().storeNewPet(target, getId(), itemobjid);
        addMaster(master);
    }

    /**
     * 寵物取得
     *
     * @param npcid     寵物npcid
     * @param master    主人
     * @param itemobjid 項圈OBJID
     */
    public L1PetInstance(int npcid, L1PcInstance master, int itemobjid) {
        super(null);
        _petMaster = master;
        _itemObjId = itemobjid;
        _type = PetTypeTable.getInstance().get(npcid);
        L1Npc npc = NpcTable.get().getTemplate(npcid);
        setId(IdFactory.get().nextId());
        set_showId(master.get_showId());
        setting_template(npc);
        setCurrentHpDirect(npc.get_hp());
        setCurrentMpDirect(npc.get_mp());
        long exp = 750L;
        setExp(exp);
        setLevel(ExpTable.getLevelByExp(exp));
        setExpPercent(0);
        setLawful(0);
        setTempLawful(0);
        setMaster(master);
        L1Location loc = master.getLocation().randomLocation(5, false);
        setX(loc.getX());
        setY(loc.getY());
        setMap((short) loc.getMapId());
        setHeading(5);
        setLightSize(npc.getLightSize());
        setPetcost(6);
        _currentPetStatus = 3;
        World.get().storeObject(this);
        World.get().addVisibleObject(this);
        for (L1PcInstance pc : World.get().getRecognizePlayer(this)) {
            onPerceive(pc);
        }
        master.addPet(this);
        PetReading.get().storeNewPet(this, getId(), itemobjid);
        addMaster(master);
    }

    public boolean noTarget() {
        switch (_currentPetStatus) {
            case 3:// 休息
                return true;
            case 4:// 散開
                if ((_petMaster != null) && (_petMaster.getMapId() == getMapId()) && (getLocation().getTileLineDistance(_petMaster.getLocation()) < 5)) {
                    if (_npcMove != null) {
                        int dir = _npcMove.targetReverseDirection(_petMaster.getX(), _petMaster.getY());
                        dir = _npcMove.checkObject(dir);
                        _npcMove.setDirectionMove(dir);
                        setSleepTime(calcSleepTime(getPassispeed(), 0));
                    }
                } else {
                    _currentPetStatus = 3;
                    return true;
                }
                break;
            case 5:// 警戒
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
            case 7:// 哨子呼叫
                if ((_petMaster != null) && (_petMaster.getMapId() == getMapId())) {
                    if (getLocation().getTileLineDistance(_petMaster.getLocation()) <= 1) {
                        _currentPetStatus = 3;
                        return true;
                    }
                }
                if (_npcMove != null) {
                    int locx = _petMaster.getX() + _random.nextInt(1);
                    int locy = _petMaster.getY() + _random.nextInt(1);
                    int dirx = _npcMove.moveDirection(locx, locy);
                    if (dirx == -1) {
                        _currentPetStatus = 3;
                        return true;
                    }
                    _npcMove.setDirectionMove(dirx);
                    setSleepTime(calcSleepTime(getPassispeed(), 0));
                }
                break;
            case 6:
            default:
                if ((_petMaster != null) && (_petMaster.getMapId() == getMapId())) {
                    if ((getLocation().getTileLineDistance(_petMaster.getLocation()) > 2) && (_npcMove != null)) {
                        int dir = _npcMove.moveDirection(_petMaster.getX(), _petMaster.getY());
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

    /**
     * 受到攻擊HP減少
     */
    @Override
    public void receiveDamage(L1Character attacker, int damage) {
        ISASCAPE = false;
        if (getCurrentHp() > 0) {
            if (damage > 0) {
                setHate(attacker, 0);
                removeSkillEffect(66);
                removeSkillEffect(212);
            }
            if (((attacker instanceof L1PcInstance)) && (damage > 0)) {
                L1PcInstance player = (L1PcInstance) attacker;
                player.setPetTarget(this);
            }
            int newHp = getCurrentHp() - damage;
            if (newHp <= 0) {
                death(attacker);
            } else {
                setCurrentHp(newHp);
            }
        } else if (!isDead()) {
            death(attacker);
        }
    }

    /**
     * 寵物死亡
     *
     */
    public synchronized void death(L1Character lastAttacker) {
        if (!isDead()) {
            setDead(true);
            setStatus(8);
            setCurrentHp(0);
            setExpPercent(getExpPercent() - (5));/* 寵物死亡扣除經驗 */
            _currentPetStatus = 3;/* 修正寵物會爆走 by 431200 */
            getMap().setPassable(getLocation(), true);
            broadcastPacketAll(new S_DoActionGFX(getId(), 8));
        }
    }

    /**
     * 寵物進化的處理
     *
     */
    public void evolvePet(int new_itemobjid) {
        L1Pet pet = PetReading.get().getTemplate(_itemObjId);
        if (pet == null) {
            return;
        }
        int newNpcId = _type.getNpcIdForEvolving();
        int evolvItem = _type.getEvolvItemId();
        // 取得進化前最大血魔
        int tmpMaxHp = getMaxHp();
        int tmpMaxMp = getMaxMp();
        transform(newNpcId);
        _type = PetTypeTable.getInstance().get(newNpcId);
        setLevel(1);
        // 進化後血魔減半
        setMaxHp(tmpMaxHp / 2);
        setMaxMp(tmpMaxMp / 2);
        setCurrentHpDirect(getMaxHp());
        setCurrentMpDirect(getMaxMp());
        setExp(0L);
        setExpPercent(0);
        getInventory().consumeItem(evolvItem, 1); // 吃掉進化道具
        // 將原寵物身上道具移交到進化後的寵物身上
        L1Object obj = World.get().findObject(pet.get_objid());
        if ((obj != null) && (obj instanceof L1NpcInstance)) {
            L1PetInstance new_pet = (L1PetInstance) obj;
            L1Inventory new_petInventory = new_pet.getInventory();
            List<L1ItemInstance> itemList = getInventory().getItems();
            for (Object itemObject : itemList) {
                L1ItemInstance item = (L1ItemInstance) itemObject;
                if (item == null) {
                    continue;
                }
                if (item.isEquipped()) { // 裝備中
                    item.setEquipped(false);
                    L1PetItem petItem = PetItemTable.get().getTemplate(item.getItemId());
                    if (petItem.isWeapom()) { // 牙齒
                        setWeapon(null);
                        new_pet.usePetWeapon(this, item);
                    } else { // 盔甲
                        setArmor(null);
                        new_pet.usePetArmor(this, item);
                    }
                }
                if (new_pet.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
                    getInventory().tradeItem(item, item.getCount(), new_petInventory);
                } else { // 掉落地面
                    L1GroundInventory groundinv = World.get().getInventory(getX(), getY(), getMapId());
                    getInventory().tradeItem(item, item.getCount(), groundinv);
                }
            }
            new_pet.broadcastPacketAll(new S_SkillSound(new_pet.getId(), 2127)); // 升級光芒
        }
        // getInventory().clearItems();
        PetReading.get().deletePet(_itemObjId);// 刪除原寵物資料
        // 紀錄新寵物資料
        pet.set_itemobjid(new_itemobjid);
        pet.set_npcid(newNpcId);
        pet.set_name(getName());
        pet.set_level(getLevel());
        pet.set_hp(getMaxHp());
        pet.set_mp(getMaxMp());
        pet.set_exp((int) getExp());
        PetReading.get().storeNewPet(this, getId(), new_itemobjid);// 新建寵物資料
        _itemObjId = new_itemobjid;
    }

    /**
     * 解散
     */
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
        monster.setLevel(getLevel());
        monster.setMaxHp(getMaxHp());
        monster.setCurrentHpDirect(getCurrentHp());
        monster.setMaxMp(getMaxMp());
        monster.setCurrentMpDirect(getCurrentMp());
        _petMaster.getPetList().remove(Integer.valueOf(getId()));
        deleteMe();
        _petMaster.getInventory().removeItem(_itemObjId, 1L);
        PetReading.get().deletePet(_itemObjId);
        World.get().storeObject(monster);
        World.get().addVisibleObject(monster);
        for (L1PcInstance pc : World.get().getRecognizePlayer(monster)) {
            onPerceive(pc);
        }
    }

    /**
     * 收集
     *
     */
    public void collect(boolean isDepositnpc) {
        L1Inventory masterInv = _petMaster.getInventory();
        List<L1ItemInstance> items = _inventory.getItems();
        for (L1ItemInstance item : items) {
            if (item.isEquipped()) {// 物品裝備中
                if (!isDepositnpc) {// 不是托管寵物
                    continue;
                }
                int itemId = item.getItem().getItemId();
                L1PetItem petItem = PetItemTable.get().getTemplate(itemId);
                if (petItem != null) {
                    setHitByWeapon(0);
                    setDamageByWeapon(0);
                    addStr(-petItem.getAddStr());
                    addCon(-petItem.getAddCon());
                    addDex(-petItem.getAddDex());
                    addInt(-petItem.getAddInt());
                    addWis(-petItem.getAddWis());
                    addMaxHp(-petItem.getAddHp());
                    addMaxMp(-petItem.getAddMp());
                    addSp(-petItem.getAddSp());
                    addMr(-petItem.getAddMr());
                    setWeapon(null);
                    setArmor(null);
                    item.setEquipped(false);
                }
            }
            if (_petMaster.getInventory().checkAddItem(item, item.getCount()) == 0) {
                _inventory.tradeItem(item, item.getCount(), masterInv);
                _petMaster.sendPackets(new S_ServerMessage(143, getName(), item.getLogName()));
            } else {
                item.set_showId(get_showId());
                masterInv = World.get().getInventory(getX(), getY(), getMapId());
                _inventory.tradeItem(item, item.getCount(), masterInv);
            }
        }
        savePet();
    }

    /**
     * 背包內物品的掉落
     */
    public void dropItem() {
        L1Inventory worldInv = World.get().getInventory(getX(), getY(), getMapId());
        List<L1ItemInstance> items = _inventory.getItems();
        for (L1ItemInstance item : items) {
            item.set_showId(get_showId());
            if (item.isEquipped()) {
                int itemId = item.getItem().getItemId();
                L1PetItem petItem = PetItemTable.get().getTemplate(itemId);
                if (petItem != null) {
                    setHitByWeapon(0);
                    setDamageByWeapon(0);
                    addStr(-petItem.getAddStr());
                    addCon(-petItem.getAddCon());
                    addDex(-petItem.getAddDex());
                    addInt(-petItem.getAddInt());
                    addWis(-petItem.getAddWis());
                    addMaxHp(-petItem.getAddHp());
                    addMaxMp(-petItem.getAddMp());
                    addSp(-petItem.getAddSp());
                    addMr(-petItem.getAddMr());
                    setWeapon(null);
                    setArmor(null);
                    item.setEquipped(false);
                }
            }
            _inventory.tradeItem(item, item.getCount(), worldInv);
        }
        savePet();
    }

    /**
     * 儲存寵物狀態
     */
    private void savePet() {
        try {
            L1Pet pet = PetReading.get().getTemplate(_itemObjId);
            if (pet != null) {
                pet.set_exp((int) getExp());
                pet.set_level(getLevel());
                pet.set_hp(getMaxHp());
                pet.set_mp(getMaxMp());
                PetReading.get().storePet(pet);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 哨子呼叫
     */
    public void call() {
        if (_type != null) {
            int id = _type.getMessageId(L1PetType.getMessageNumber(getLevel()));
            if (id != 0) {
                broadcastPacketAll(new S_NpcChat(this, "$" + id));
            }
        }
        setCurrentPetStatus(7);
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

    public void setMasterSelectTarget(L1Character target) {
        if (target != null) {
            setHate(target, 0);
            if (!isAiRunning()) {
                startAI();
            }
        }
    }

    /**
     * 發送接觸資訊
     */
    @Override
    public void onPerceive(L1PcInstance perceivedFrom) {
        try {
            if (perceivedFrom.get_showId() != get_showId()) {
                return;
            }
            perceivedFrom.addKnownObject(this);
            perceivedFrom.sendPackets(new S_NPCPack_Pet(this, perceivedFrom));
            if ((getMaster() != null) && (perceivedFrom.getId() == getMaster().getId())) {
                //perceivedFrom.sendPackets(new S_HPMeter(getId(), 100 * getCurrentHp() / getMaxHp()));
                // 7.6
                perceivedFrom.sendPackets(new S_HPMeter(getId(), (100 * getCurrentHp()) / getMaxHp(), 0xff));
            }
            if (isDead()) {
                perceivedFrom.sendPackets(new S_DoActionGFX(getId(), 8));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
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
        if ((isSafetyZone()) || (player.isSafetyZone())) {
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
        if (_petMaster.equals(player)) {
            player.sendPackets(new S_PetMenuPacket(this, getExpPercent()));
            savePet();
        }
    }

    public void onFinalAction(L1PcInstance player, String action) {
        int status = Integer.parseInt(action);
        switch (status) {
            case 0:
                return;
            case 6:
                liberate();
                break;
            default:
                Object[] petList = _petMaster.getPetList().values().toArray();
                for (Object petObject : petList) {
                    if ((petObject instanceof L1PetInstance)) {
                        L1PetInstance pet = (L1PetInstance) petObject;
                        if (_petMaster != null) {
                            if (_petMaster.isGm()) {
                                pet.setCurrentPetStatus(status);
                                continue;
                            }
                        }
                        if (_petMaster != null && _petMaster.getLevel() >= pet.getLevel()) {
                            pet.setCurrentPetStatus(status);
                        } else {
                            if (this._type != null) {
                                int id = _type.getDefyMessageId();
                                if (id != 0) {
                                    broadcastPacketAll(new S_NpcChat(pet, "$" + id));
                                }
                            }
                        }
                    }
                }
        }
    }

    /**
     * 自動使用道具判斷
     */
    @Override
    public void onItemUse() {
        if (!isActived()) {
            useItem(1, 100);// 使用加速藥水
        }
        if (getCurrentHp() * 100 / getMaxHp() < 60) {// 血量低於60%
            useItem(0, 100);// 使用補血藥水
        }
    }

    /**
     * 獲得道具的處理
     */
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
        if (_petMaster != null) {
            int hpRatio = 100 * currentHp / getMaxHp();
            L1PcInstance master = _petMaster;
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

    public int getCurrentPetStatus() {
        return _currentPetStatus;
    }

    public void setCurrentPetStatus(int i) {
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

    public int getItemObjId() {
        return _itemObjId;
    }

    public int getExpPercent() {
        return _expPercent;
    }

    public void setExpPercent(int expPercent) {
        _expPercent = expPercent;
    }

    public L1ItemInstance getWeapon() {
        return _weapon;
    }

    public void setWeapon(L1ItemInstance weapon) {
        _weapon = weapon;
    }

    public L1ItemInstance getArmor() {
        return _armor;
    }

    public void setArmor(L1ItemInstance armor) {
        _armor = armor;
    }

    public int getHitByWeapon() {
        return _hitByWeapon;
    }

    public void setHitByWeapon(int i) {
        _hitByWeapon = i;
    }

    public int getDamageByWeapon() {
        return _damageByWeapon;
    }

    public void setDamageByWeapon(int i) {
        _damageByWeapon = i;
    }

    public L1PetType getPetType() {
        return _type;
    }

    public void set_tempModel() {
        _tempModel = _currentPetStatus;
    }

    public void get_tempModel() {
        _currentPetStatus = _tempModel;
    }

    // 使用寵物裝備
    public void usePetWeapon(L1PetInstance pet, L1ItemInstance weapon) {
        if (pet.getWeapon() == null) {
            setPetWeapon(pet, weapon);
        } else { // 既何裝備場合、前裝備
            if (pet.getWeapon().equals(weapon)) {
                removePetWeapon(pet, pet.getWeapon());
            } else {
                removePetWeapon(pet, pet.getWeapon());
                setPetWeapon(pet, weapon);
            }
        }
    }

    public void usePetArmor(L1PetInstance pet, L1ItemInstance armor) {
        if (pet.getArmor() == null) {
            setPetArmor(pet, armor);
        } else { // 既何裝備場合、前裝備
            if (pet.getArmor().equals(armor)) {
                removePetArmor(pet, pet.getArmor());
            } else {
                removePetArmor(pet, pet.getArmor());
                setPetArmor(pet, armor);
            }
        }
    }

    private void setPetWeapon(L1PetInstance pet, L1ItemInstance weapon) {
        int itemId = weapon.getItem().getItemId();
        L1PetItem petItem = PetItemTable.get().getTemplate(itemId);
        if (petItem == null) {
            return;
        }
        pet.setHitByWeapon(petItem.getHitModifier());
        pet.setDamageByWeapon(petItem.getDamageModifier());
        pet.addStr(petItem.getAddStr());
        pet.addCon(petItem.getAddCon());
        pet.addDex(petItem.getAddDex());
        pet.addInt(petItem.getAddInt());
        pet.addWis(petItem.getAddWis());
        pet.addMaxHp(petItem.getAddHp());
        pet.addMaxMp(petItem.getAddMp());
        pet.addSp(petItem.getAddSp());
        pet.addMr(petItem.getAddMr());
        pet.setWeapon(weapon);
        weapon.setEquipped(true);
    }

    private void removePetWeapon(L1PetInstance pet, L1ItemInstance weapon) {
        int itemId = weapon.getItem().getItemId();
        L1PetItem petItem = PetItemTable.get().getTemplate(itemId);
        if (petItem == null) {
            return;
        }
        pet.setHitByWeapon(0);
        pet.setDamageByWeapon(0);
        pet.addStr(-petItem.getAddStr());
        pet.addCon(-petItem.getAddCon());
        pet.addDex(-petItem.getAddDex());
        pet.addInt(-petItem.getAddInt());
        pet.addWis(-petItem.getAddWis());
        pet.addMaxHp(-petItem.getAddHp());
        pet.addMaxMp(-petItem.getAddMp());
        pet.addSp(-petItem.getAddSp());
        pet.addMr(-petItem.getAddMr());
        pet.setWeapon(null);
        weapon.setEquipped(false);
    }

    private void setPetArmor(L1PetInstance pet, L1ItemInstance armor) {
        int itemId = armor.getItem().getItemId();
        L1PetItem petItem = PetItemTable.get().getTemplate(itemId);
        if (petItem == null) {
            return;
        }
        pet.addAc(petItem.getAddAc());
        pet.addStr(petItem.getAddStr());
        pet.addCon(petItem.getAddCon());
        pet.addDex(petItem.getAddDex());
        pet.addInt(petItem.getAddInt());
        pet.addWis(petItem.getAddWis());
        pet.addMaxHp(petItem.getAddHp());
        pet.addMaxMp(petItem.getAddMp());
        pet.addSp(petItem.getAddSp());
        pet.addMr(petItem.getAddMr());
        pet.setArmor(armor);
        armor.setEquipped(true);
    }

    private void removePetArmor(L1PetInstance pet, L1ItemInstance armor) {
        int itemId = armor.getItem().getItemId();
        L1PetItem petItem = PetItemTable.get().getTemplate(itemId);
        if (petItem == null) {
            return;
        }
        pet.addAc(-petItem.getAddAc());
        pet.addStr(-petItem.getAddStr());
        pet.addCon(-petItem.getAddCon());
        pet.addDex(-petItem.getAddDex());
        pet.addInt(-petItem.getAddInt());
        pet.addWis(-petItem.getAddWis());
        pet.addMaxHp(-petItem.getAddHp());
        pet.addMaxMp(-petItem.getAddMp());
        pet.addSp(-petItem.getAddSp());
        pet.addMr(-petItem.getAddMr());
        pet.setArmor(null);
        armor.setEquipped(false);
    }
}
