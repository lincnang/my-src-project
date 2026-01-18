package com.lineage.server.model.Instance;

import com.add.MJBookQuestSystem.Loader.MonsterBookLoader;
import com.add.MJBookQuestSystem.Loader.UserMonsterBookLoader;
import com.add.MJBookQuestSystem.Loader.UserWeekQuestLoader;
import com.add.MJBookQuestSystem.MonsterBook;
import com.add.Mobbling.MobblingTime;
import com.add.Mobbling.MobblingTimeList;
import com.lineage.config.*;
import com.lineage.data.event.CampSet;
import com.lineage.data.event.LeavesSet;
import com.lineage.data.event.ProtectorSet;
import com.lineage.data.event.QuestMobSet;
import com.lineage.server.datatables.*;
import com.lineage.server.datatables.lock.CharItemsReading;
import com.lineage.server.model.*;
import com.lineage.server.model.drop.DropShare;
import com.lineage.server.model.drop.DropShareExecutor;
import com.lineage.server.model.drop.SetDrop;
import com.lineage.server.model.drop.SetDropExecutor;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillMode;
import com.lineage.server.model.skill.skillmode.SkillMode;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.types.Point;
import com.lineage.server.utils.CalcExp;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldNpc;
import com.lineage.server.world.WorldQuest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import william.Honor;
import william.L1WeaponSoul;
import william.L1WilliamHonor;

import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class L1MonsterInstance extends L1NpcInstance {
    private static final long serialVersionUID = 1L;
    private static final Log _log = LogFactory.getLog(L1MonsterInstance.class);
    private static final Random _random = new Random();
    private static MobblingTimeList _Mob = MobblingTimeList.Mob();
    private boolean _storeDroped;
    private int _ubSealCount = 0; // 無限大賽可獲得的勇氣之證數量
    private int _ubId = 0; // UBID
    // 怪物上次回血時間
    private long _lasthprtime = 0;
    // 怪物上次回魔時間
    private long _lastmprtime = 0;
    
    // HP條更新頻率控制
    private long _lastHpBarTime = 0;

    public L1MonsterInstance(final L1Npc template) {
        super(template);
        this._storeDroped = false;
        applyAISettings();
    }

    private void applyAISettings() {
        try {
            final int npcId = getNpcTemplate().get_npcId();
            if (com.lineage.config.ConfigAI.EXCLUDE_NPC_IDS.contains(npcId)) {
                return; // 黑名單不啟用
            }
            if (!com.lineage.config.ConfigAI.L1J_AI_NPC_IDS.isEmpty()) {
                if (com.lineage.config.ConfigAI.L1J_AI_NPC_IDS.contains(npcId)) {
                    // 無需設旗標，因我們已將 searchTarget 統一改為 L1J；此函式保留日後細分擴充
                }
            } else if (com.lineage.config.ConfigAI.USE_L1J_AI) {
                // 全域啟用；目前 searchTarget 已統一切換
            }
        } catch (Exception ignored) {}
    }

    /**
     * NPC死亡開門的處理
     *
     */
    private static void openDoorWhenNpcDied(L1NpcInstance npc) {
        int[] npcId = {46143, 46144, 46145, 46146, 46147, 46148, 46149, 46150, 46151, 46152};
        int[] doorId = {5001, 5002, 5003, 5004, 5005, 5006, 5007, 5008, 5009, 5010};
        for (int i = 0; i < npcId.length; i++) {
            if (npc.getNpcTemplate().get_npcId() == npcId[i]) {
                openDoorInCrystalCave(doorId[i]);
            }
        }
    }

    /**
     * 開門的處理
     *
     */
    private static void openDoorInCrystalCave(int doorId) {
        for (final L1NpcInstance object : WorldNpc.get().all()) {
            if (object instanceof L1DoorInstance) {
                L1DoorInstance door = (L1DoorInstance) object;
                if (door.getDoorId() == doorId) {
                    door.open();
                }
            }
        }
    }

    public void onItemUse() {
        if (!isActived() && _target != null) {
            useItem(USEITEM_HEAL, 40);
            if (getNpcTemplate().is_doppel() && _target instanceof L1PcInstance) {// 變形怪的處理
                L1PcInstance targetPc = (L1PcInstance) _target;
                if (getNpcId() == 81069) {
                    setName(_target.getName() + "(法師的考驗)");
                    setNameId(_target.getName() + "(法師的考驗)");
                    setTitle("試煉專用怪↓");
                } else {
                    setName(_target.getName());
                    setNameId(_target.getName());
                    setTitle(_target.getTitle());
                }
                setTitle(_target.getTitle());
                setTempLawful(_target.getLawful());
                setTempCharGfx(targetPc.getClassId());
                setGfxId(targetPc.getClassId());
                setPassispeed(640);
                setAtkspeed(900);
                for (L1PcInstance pc : World.get().getRecognizePlayer(this)) {
                    pc.sendPackets(new S_RemoveObject(this));
                    pc.removeKnownObject(this);
                    pc.updateObject();
                }
            }
            if (getCurrentHp() * 100 / getMaxHp() < 40) {
                useItem(USEITEM_HASTE, 50);
            }
        }
    }

    /**
     * TODO 接觸資訊
     */
    @Override
    public void onPerceive(L1PcInstance perceivedFrom) {
        try {
            if (perceivedFrom.get_showId() != get_showId()) {
                return;
            }
            perceivedFrom.addKnownObject(this);
            if (getCurrentHp() > 0) {
                perceivedFrom.sendPackets(new S_NPCPack(this));
                onNpcAI();
                if (getBraveSpeed() == 1) {
                    perceivedFrom.sendPackets(new S_SkillBrave(getId(), 1, 600000));
                }
            } else {
                perceivedFrom.sendPackets(new S_NPCPack(this));
            }
            if (QuestMobSet.START) {
                ServerQuestMobTable.get().checkQuestMobGfx(perceivedFrom, this);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void searchTarget() {
        // 依照設定決定採用 L1J 或原生搜尋
        if (com.lineage.config.ConfigAI.useL1JAIForNpc(getNpcTemplate().get_npcId())) {
            L1J_AI_Adapter.searchTarget(this);
        } else {
            // 原生：先找玩家，再找NPC
            L1PcInstance tp = searchTargetPlayer();
            if (tp != null) {
                _target = tp;
                setHate(tp, 0);
            } else {
                L1MonsterInstance tn = searchTargetNpc();
                if (tn != null) {
                    _target = tn;
                    setHate(tn, 0);
                }
            }
        }
        if (_target == null) {
            ISASCAPE = false;
        }
    }

    // 舊版搜尋函式保留原始實作供必要時回退使用

    /**
     * 搜尋NPC目標
     *
     */
    @SuppressWarnings("unused")
    private L1MonsterInstance searchTargetNpc() {
        L1MonsterInstance targetNpc = null;
        // /** [原碼] 怪物對戰系統 * /
        if (getMapId() == 93) {
            Point npcPt = getLocation();
            for (L1Object object : World.get().getVisibleObjects(this, -1)) {
                if (object instanceof L1MonsterInstance) {
                    L1MonsterInstance tgnpc = (L1MonsterInstance) object;
                    if (getMapId() != tgnpc.getMapId() // 不同地圖
                            || !npcPt.isInScreen(tgnpc.getLocation())) { // 不在視野內
                        continue;
                    }
                    // 目標不為空
                    // 目標體力大於0
                    // 目標未死亡
                    // 唯一ID不相同
                    // 副本ID相同
                    if (tgnpc.getCurrentHp() > 0 && !tgnpc.isDead() && getId() != tgnpc.getId() && get_showId() == tgnpc.get_showId()
                            && MobblingTime.getMobbling()) { // 怪物對戰狀態
                        targetNpc = tgnpc;
                    }
                }
            }
        }
        // 黑騎士團打紅騎士團
		/*if (npc.getNpcId() >= 190449 && npc.getNpcId() <= 190456) {
			if (getMapId() == 4) {
				Point npcpt1 = getLocation();
				for (L1Object object : World.get().getVisibleObjects(this, -1)) {
					if (object instanceof L1MonsterInstance) {
						L1MonsterInstance tgnpc = (L1MonsterInstance) object;
						if (getMapId() != tgnpc.getMapId() // 不同地圖
								|| !npcpt1.isInScreen(tgnpc.getLocation())) { // 不在視野內
							continue;
						}
						if (tgnpc.getNpcId() >= 190355 && tgnpc.getNpcId() <= 190364 // 目標不為空
						        && tgnpc.getCurrentHp() > 0 // 目標體力大於0
								&& !tgnpc.isDead() // 目標未死亡
								&& getId() != tgnpc.getId() // 唯一ID不相同
								&& get_showId() == tgnpc.get_showId()) { // 副本ID相同
							targetNpc = tgnpc;
						}
					}
				}
			}
		}
		// 紅騎士團打黑騎士團
		if (npc.getNpcId() >= 190355 && npc.getNpcId() <= 190364) {
			if (getMapId() == 4) {
				//Point npcpt2 = getLocation();
				//for (L1Object object : World.get().getVisibleObjects(this, -1)) {
				for (L1Object object : World.get().getVisibleObjects(this, 1000)) {
					if (object instanceof L1MonsterInstance) {
						L1MonsterInstance tgnpc = (L1MonsterInstance) object;
						if (getMapId() != tgnpc.getMapId()) { // 不同地圖
							continue;
						}
						if (tgnpc.getNpcId() >= 190449 && tgnpc.getNpcId() <= 190456 // 目標不為空
							        && tgnpc.getCurrentHp() > 0 // 目標體力大於0
									&& !tgnpc.isDead() // 目標未死亡
									&& getId() != tgnpc.getId() // 唯一ID不相同
									&& get_showId() == tgnpc.get_showId()) { // 副本ID相同
							targetNpc = tgnpc;
						}
					}
				}
			}
		}*/
        return targetNpc;
    }

    /**
     * 搜尋玩家目標
     *
     */
    @SuppressWarnings("unused")
    private L1PcInstance searchTargetPlayer() {
        // 攻擊目標搜尋
        L1PcInstance targetPlayer = null;
        for (final L1PcInstance pc : World.get().getVisiblePlayer(this)) {
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                _log.error(e.getLocalizedMessage(), e);
            }
            if (pc.getCurrentHp() <= 0) {
                continue;
            }
            if (pc.isDead()) {
                continue;
            }
            if (pc.isGhost()) {
                continue;
            }
            if (pc.isGm()) {
                continue;
            }
            // 副本ID不相等
            if (this.get_showId() != pc.get_showId()) {
                continue;
            }
            if (this.getMapId() == 410) {// 魔族神殿的MOB
                // 忽略收到調職命令的小惡魔
                if (pc.getTempCharGfx() == 4261) {
                    continue;
                }
            }
            if (this.getNpcTemplate().get_family() == NpcTable.ORC) {
                if (pc.getClan() != null) {
                    if (pc.getClan().getCastleId() == L1CastleLocation.OT_CASTLE_ID) {
                        continue;
                    }
                }
            }
            final L1PcInstance tgpc1 = this.attackPc1(pc);
            if (tgpc1 != null) {
                targetPlayer = tgpc1;
                return targetPlayer;
            }
            final L1PcInstance tgpc2 = this.attackPc2(pc);
            if (tgpc2 != null) {
                targetPlayer = tgpc2;
                return targetPlayer;
            }
            // どちらかの條件を滿たす場合、友好と見なされ先制攻擊されない。
            // ?モンスターのカルマがマイナス值（バルログ側モンスター）でPCのカルマレベルが1以上（バルログ友好）
            // ?モンスターのカルマがプラス值（ヤヒ側モンスター）でPCのカルマレベルが-1以下（ヤヒ友好）
            if (this.getNpcTemplate().getKarma() < 0) {
                if (pc.getKarmaLevel() >= 1) {
                    continue;
                }
            }
            if (this.getNpcTemplate().getKarma() > 0) {
                if (pc.getKarmaLevel() <= -1) {
                    continue;
                }
            }
            // 見棄てられた者たちの地 カルマクエストの變身中は、各陣營のモンスターから先制攻擊されない
            if (pc.getTempCharGfx() == 6034) {
                if (this.getNpcTemplate().getKarma() < 0) {
                    continue;
                }
            }
            if (pc.getTempCharGfx() == 6035) {
                if (this.getNpcTemplate().getKarma() > 0) {
                    continue;
                }
                if (this.getNpcTemplate().get_npcId() == 46070) {// 被拋棄的魔族
                    continue;
                }
                if (this.getNpcTemplate().get_npcId() == 46072) {// 被拋棄的魔族
                    continue;
                }
            }
            // 邪惡玩家追殺
            final L1PcInstance tgpc = this.targetPlayer1000(pc);
            if (tgpc != null) {
                targetPlayer = tgpc;
                return targetPlayer;
            }
            boolean isCheck = false;
            if (!pc.isInvisble()) {
                isCheck = true;
            }
            if (this.getNpcTemplate().is_agrocoi()) {
                isCheck = true;
            }
            if (isCheck) { // 
                // 變形探知
                if (pc.hasSkillEffect(67)) { // 變形術
                    if (this.getNpcTemplate().is_agrososc()) {
                        targetPlayer = pc;
                        return targetPlayer;
                    }
                }
                // 主動攻擊
                if (this.getNpcTemplate().is_agro()) {
                    targetPlayer = pc;
                    return targetPlayer;
                }
                // 特定外型搜尋
                if (this.getNpcTemplate().is_agrogfxid1() >= 0) {
                    if (pc.getGfxId() == this.getNpcTemplate().is_agrogfxid1()) {
                        targetPlayer = pc;
                        return targetPlayer;
                    }
                }
                if (this.getNpcTemplate().is_agrogfxid2() >= 0) {
                    if (pc.getGfxId() == this.getNpcTemplate().is_agrogfxid2()) {
                        targetPlayer = pc;
                        return targetPlayer;
                    }
                }
            }
        }
        return targetPlayer;
    }

    /**
     * 克特追殺王族及黑妖
     *
     */
    private L1PcInstance attackPc2(final L1PcInstance pc) {
        if (this.getNpcId() == 45600) { // 克特
            if (pc.isCrown()) {// 王族
                if (pc.getTempCharGfx() == pc.getClassId()) {
                    return pc;
                }
            }
            if (pc.isDarkelf()) {// 黑妖
                return pc;
            }
        }
        return null;
    }

    /**
     * 競技場
     *
     */
    private L1PcInstance attackPc1(final L1PcInstance pc) {
        final int mapId = this.getMapId();
        boolean isCheck = false;
        if (mapId == 88) {
            isCheck = true;
        }
        if (mapId == 98) {
            isCheck = true;
        }
        if (mapId == 92) {
            isCheck = true;
        }
        if (mapId == 91) {
            isCheck = true;
        }
        if (mapId == 95) {
            isCheck = true;
        }
        if (isCheck) {
            if (!pc.isInvisble() || this.getNpcTemplate().is_agrocoi()) { // 
                return pc;
            }
        }
        return null;
    }

    /**
     * 邪惡玩家追殺
     *
     */
    private L1PcInstance targetPlayer1000(final L1PcInstance pc) {
        if (ConfigOther.KILLRED) {
            if (!this.getNpcTemplate().is_agro() && !this.getNpcTemplate().is_agrososc() && this.getNpcTemplate().is_agrogfxid1() < 0 && this.getNpcTemplate().is_agrogfxid2() < 0) { // 完全
                if (pc.getLawful() < -1000) { // 
                    return pc;
                }
            }
        }
        return null;
    }

    /**
     * 攻擊目標設置
     */
    @Override
    public void setLink(final L1Character cha) {
        // 副本ID不相等
        if (this.get_showId() != cha.get_showId()) {
            return;
        }
        if (cha != null && this._hateList.isEmpty()) {
            this._hateList.add(cha, 0);
            this.checkTarget();
        }
    }

    public void onNpcAI() {
        if (this.isAiRunning()) {
            return;
        }
        this.setAiRunning(true);// 修正怪物會爆走
        if (!this._storeDroped) {// 背包尚未加入掉落物品
            final SetDropExecutor setdrop = new SetDrop();
            setdrop.setDrop(this, this.getInventory());
            this.getInventory().shuffle();
            this._storeDroped = true;
        }
        this.setActived(false);
        this.startAI();
    }

    /**
     * 對話
     */
    @Override
    public void onTalkAction(final L1PcInstance pc) {
        // 改變面向
        this.setHeading(this.targetDirection(pc.getX(), pc.getY()));
        this.broadcastPacketAll(new S_ChangeHeading(this));
        // 動作暫停
        set_stop_time(REST_MILLISEC);
        this.setRest(true);
    }

    /**
     * 受到攻擊時的處理
     */
    @Override
    public void onAction(L1PcInstance pc) {
        if (ATTACK != null) {
            ATTACK.attack(pc, this);
        }
        if (getCurrentHp() > 0 && !isDead()) {
            L1AttackMode attack = new L1AttackPc(pc, this);
            if (attack.calcHit()) {
                attack.calcDamage();
            }
            attack.action();
            attack.commit();
        }
    }

    /**
     * 受攻擊mp減少計算
     */
    @Override
    public void ReceiveManaDamage(final L1Character attacker, final int mpDamage) {
        if (mpDamage > 0 && !this.isDead()) {
            this.setHate(attacker, mpDamage);
            this.onNpcAI();
            // NPC互相幫助的判斷
            if (attacker instanceof L1PcInstance) {
                this.serchLink((L1PcInstance) attacker, this.getNpcTemplate().get_family());
            }
            int newMp = this.getCurrentMp() - mpDamage;
            if (newMp < 0) {
                newMp = 0;
            }
            this.setCurrentMp(newMp);
        }
    }

    /**
     * 受到傷害的處理
     *
     */
    public void receiveDamage(L1Character attacker, double damage, int attr) {
        int mrdef = getMr();// 魔防
        int rnd = _random.nextInt(100) + 1;
        if (mrdef >= rnd) {
            damage /= 2.0D;
        }
        int resist = 0;
        switch (attr) {
            case 1:
                resist = getEarth();
                break;
            case 2:
                resist = getFire();
                break;
            case 4:
                resist = getWater();
                break;
            case 8:
                resist = getWind();
                break;
        }
        int resistFloor = (int) (0.16D * Math.abs(resist));
        if (resist >= 0) {
            resistFloor *= 1;
        } else {
            resistFloor *= -1;
        }
        double attrDeffence = resistFloor / 32.0D;
        double coefficient = 1 - attrDeffence;
        damage *= coefficient;
        receiveDamage(attacker, (int) damage);
    }

    /**
     * 受到傷害的處理
     *
     */
    @Override
    public void receiveDamage(L1Character attacker, int damage) {
        ISASCAPE = false;
        if (getCurrentHp() > 0 && !isDead()) {
            if (getHiddenStatus() == 1 || getHiddenStatus() == 2) {
                return;
            }
            if (damage >= 0) {
                if (attacker instanceof L1EffectInstance) {
                    L1EffectInstance effect = (L1EffectInstance) attacker;
                    attacker = effect.getMaster();
                    if (attacker != null) {
                        setHate(attacker, damage);
                    }
                } else if (attacker instanceof L1IllusoryInstance) {
                    L1IllusoryInstance ill = (L1IllusoryInstance) attacker;
                    attacker = ill.getMaster();
                    if (attacker != null) {
                        setHate(attacker, damage);
                    }
                } else if (attacker instanceof L1MonsterInstance) {
                    switch (getNpcTemplate().get_npcId()) {
                        case 91290: // 鐮刀死神的使者
                        case 91294: // 巴風特
                        case 91295: // 黑翼賽尼斯
                        case 91296: // 賽尼斯
                            setHate(attacker, damage);
                            damage = 0;
                            break;
                    }
                } else {
                    setHate(attacker, damage);
                }
            }
            if (damage > 0) {
                removeSkillEffect(66);
                removeSkillEffect(212);
            }
            onNpcAI();
            L1PcInstance atkpc = null;
            if (attacker instanceof L1PcInstance) {
                atkpc = (L1PcInstance) attacker;
                if (damage > 0) {
                    atkpc.setPetTarget(this);
                    switch (getNpcTemplate().get_npcId()) {
                        case 45681: // 林德拜爾
                        case 45682: // 安塔瑞斯
                        case 45683: // 法利昂
                        case 45684: // 巴拉卡斯
                            if (this.getLocation().getTileLineDistance(atkpc.getLocation()) > 6) {// 超過6格距離
                                L1Teleport.teleportToTargetFront(atkpc, this, 1);// 召喚至面前
                            }
                            break;
                    }
                }
                // NPC互相幫助的判斷
                serchLink(atkpc, getNpcTemplate().get_family());
            }
            if (hasSkillEffect(219)) {// 化身  //SRC0808
                damage *= ConfigSkillIllusion.ILLUSION_AVATAR_DAMAGE;
                ;
            }
            if (hasSkillEffect(1219)) {// 化身  //SRC0808
                damage *= 1 + ConfigSkillIllusion.IS4;
                ;
            }
            int newHp = getCurrentHp() - damage;
            if (newHp <= 0 && !isDead()) {
                if (attacker instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) attacker;
                    int bookId = getNpcTemplate().getBookId();
                    if (bookId > 0 && !isResurrect()) {    // 怪物圖鑒171020
                        MonsterBook book = MonsterBookLoader.getInstance().getTemplate(bookId);
                        pc.getMonsterBook().addMonster(book);
                        UserMonsterBookLoader.store(pc);
                        if (ConfigLIN.Week_Quest) {
                            pc.getWeekQuest().addMonster(book);
                            UserWeekQuestLoader.store(pc);
                        }
                    }
                }
                int transformId = getNpcTemplate().getTransformId();
                if (transformId == -1) {// 不會死亡變身 //src014
                    setCurrentHpDirect(0);
                    setDead(true);
                    setStatus(8);
                    openDoorWhenNpcDied(this);
                    Death death = new Death(attacker);
                    GeneralThreadPool.get().execute(death);
                    if (atkpc != null) {
                        if (getNpcTemplate().isBroadcast()) {
                            World.get().broadcastPacketToAll(new S_KillMessageMob(atkpc.getName(), getName()));
                        }
						/*if (!isResurrect()) {
							if ((ProtectorSet.CHANCE > 0) && (_random.nextInt(10000) < ProtectorSet.CHANCE)) {
								if (CharItemsReading.get().checkItemId(ProtectorSet.ITEM_ID) < ProtectorSet.DROP_LIMIT) {
									L1ItemInstance item = ItemTable.get().createItem(ProtectorSet.ITEM_ID);
									atkpc.getInventory().storeItem(item);
								}
							}
						}*/
                        //守護者靈魂
                        if (!isResurrect()) {
                            if (ProtectorSet.CHANCE > 0 && _random.nextInt(10000) < ProtectorSet.CHANCE) {
                                //terry770106 2017/0625
                                if (CharItemsReading.get().checkItemId(ProtectorSet.ITEM_ID) < ProtectorSet.DROP_LIMIT) {
                                    L1ItemInstance item = ItemTable.get().createItem(ProtectorSet.ITEM_ID);
                                    atkpc.getInventory().storeItem(item);
                                    World.get().broadcastPacketToAll(new S_SystemMessage("\\f=守護者再度出現於亞丁大陸"));
                                    switch (_random.nextInt(4) + 1) {
                                        case 1:
                                            World.get().broadcastPacketToAll(new S_PacketBoxGree(0x01));
                                            break;
                                        case 2:
                                            World.get().broadcastPacketToAll(new S_PacketBoxGree(6));
                                            break;
                                        case 3:
                                            World.get().broadcastPacketToAll(new S_PacketBoxGree(7));
                                            break;
                                        case 4:
                                            World.get().broadcastPacketToAll(new S_PacketBoxGree(8));
                                            break;
                                    }
                                }
                            }
                        }
                    }
                    if (getNpcId() >= 71014 && getNpcId() <= 71016) {// 新地龍
						/*if(getNpcId()==71016){
							for (L1Character cha : World.get().getAllPlayers()) {
								if ((cha instanceof L1PcInstance)) {
									L1PcInstance pc = (L1PcInstance) cha;
									if(pc.getMapId()==1005){
										System.out.println("玩家:"+pc.getName()+" 給予龍之血痕效果");
										pc.sendPacketsX8(new S_SkillSound(pc.getId(), 7854));
										SkillMode mode = L1SkillMode.get().getSkill(6797);
										if (mode != null) {
											try {
												mode.start(pc, null, null, 43200);
											} catch (Exception e) {
												_log.info(e.getMessage());
											}
										}
									}
								}
							}
						}*/
                        GeneralThreadPool.get().execute(new deathDragonTimer1(this, getMapId()));
                    } else if (getNpcId() >= 71026 && getNpcId() <= 71028) {// 新水龍
						/*if (getNpcId() == 71028) {
							for (L1Character cha : World.get().getAllPlayers()) {
								if ((cha instanceof L1PcInstance)) {
									L1PcInstance pc = (L1PcInstance) cha;
									if (pc.getMapId() == 1011) {
										System.out.println("玩家:" + pc.getName() + " 給予龍之血痕效果");
										pc.sendPacketsX8(new S_SkillSound(pc.getId(), 7854));
										SkillMode mode = L1SkillMode.get().getSkill(6798);
										if (mode != null) {
											try {
												mode.start(pc, null, null, 43200);
											} catch (Exception e) {
												_log.info(e.getMessage());
											}
										}
									}
								}
							}
						}*/
                        GeneralThreadPool.get().execute(new deathDragonTimer2(this, getMapId()));
                    } else if (getNpcId() >= 97204 && getNpcId() <= 97209) {// 新風龍
                        GeneralThreadPool.get().execute(new deathDragonTimer3(this));
                    }
                } else {// 死亡時會變身
                    transform(transformId);
                }
            }
            if (newHp > 0) {
                setCurrentHp(newHp);
                hide();// 一定機率躲藏
            }
			/*if ((ConfigOther.HPBAR) && (atkpc != null) && !getNameId().contains("BOSS")) {
				if ((attacker instanceof L1PcInstance)) {
					L1PcInstance player = (L1PcInstance) attacker;
					player.sendPackets(new S_HPMeter(this));
			    }
				broadcastPacketHP(atkpc);
			}*/
            // HP 顯示設置
            //if (ConfigOther.HPBAR && !getNameId().contains("BOSS")) {
            if (ConfigOther.HPBAR) {
                if (attacker instanceof L1PcInstance) {
                    L1PcInstance player = (L1PcInstance) attacker;
                    // 優化：只有在 HP 變化量大於一定比例，或是每隔一段時間才發送更新，避免高頻率攻擊造成封包風暴
                    // 這裡採用簡單的時間閥值控制 (例如 200ms)
                    long now = System.currentTimeMillis();
                    if (now - this._lastHpBarTime > 200) { 
                        player.sendPackets(new S_HPMeter(this));
                        this._lastHpBarTime = now;
                    }
                }
                // 讓寵物或召喚怪攻擊時也看得到怪物血條 by terry0412
                if (atkpc == null) {
                    if (attacker instanceof L1PetInstance) {
                        atkpc = (L1PcInstance) ((L1PetInstance) attacker).getMaster();
                    } else if (attacker instanceof L1SummonInstance) {
                        atkpc = (L1PcInstance) ((L1SummonInstance) attacker).getMaster();
                    }
                    // 存在PC主人
                    if (atkpc != null) {
                        broadcastPacketHP(atkpc);
                    }
                } else {
                    broadcastPacketHP(atkpc);
                }
            }
        } else if (!isDead()) {
            setDead(true);
            setStatus(8);
            Death death = new Death(attacker);
            GeneralThreadPool.get().execute(death);
        }
    }

    @Override
    public void setCurrentHp(int i) {
        int currentHp = Math.min(i, getMaxHp());
        if (getCurrentHp() == currentHp) {
            return;
        }
        setCurrentHpDirect(currentHp);
        if (getMaxHp() > getCurrentHp()) {
            startHpRegeneration();
        }
    }

    @Override
    public void setCurrentMp(int i) {
        int currentMp = Math.min(i, getMaxMp());
        if (getCurrentMp() == currentMp) {
            return;
        }
        setCurrentMpDirect(currentMp);
        if (getMaxMp() > getCurrentMp()) {
            startMpRegeneration();
        }
    }

    /**
     * 判斷主要攻擊者(最後殺死NPC的人)
     *
     */
    private void distributeExpDropKarma(L1Character lastAttacker) {
        if (lastAttacker == null) {
            return;
        }
        /** [原碼] 怪物對戰系統 */
        if (_Mob.get_isStart()) {
            L1MonsterInstance mob = null;
            if (lastAttacker instanceof L1MonsterInstance) {
                mob = (L1MonsterInstance) lastAttacker;
            }
            if (mob != null && isDead() && mob.getMapId() == 93) {
                for (L1PcInstance listner : World.get().getAllPlayers()) {
                    if (listner.getMapId() == 93) {
                        listner.sendPackets(new S_SystemMessage("怪物對戰：" + mob.getName() + " ☆擊敗了( " + getName() + " )"));
                    }
                }
            }
        }
        // 判斷主要攻擊者
        L1PcInstance pc = null;
        // NPC具有死亡判斷設置
        if (DEATH != null) {
            pc = DEATH.death(lastAttacker, this);
        } else {
            // 判斷主要攻擊者
            pc = CheckUtil.checkAtkPc(lastAttacker);
        }
        if (pc != null) {
            ArrayList<L1Character> targetList = _hateList.toTargetArrayList();
            ArrayList<Integer> hateList = _hateList.toHateArrayList();
            long exp = getExp();
            CalcExp.calcExp(pc, getId(), targetList, hateList, exp);

            if (ConfigOtherSet2.Prestigesnatch) {
                final int npcId = getNpcId();
                final int npcMapId = NpcHonorTable.get().getMapId(npcId); // 該怪物對應的任務地圖
                final int playerMapId = pc.getMapId(); // 玩家當前地圖

                if (npcMapId == playerMapId) {
                    final int honor = NpcHonorTable.get().get_score(npcId);
                    final int rand = NpcHonorTable.get().get_random(npcId);

                    if (honor > 0 && !isResurrect()) {
                        int roll = _random.nextInt(100);

                        if (roll < rand) {
                            int beforeHonor = pc.getHonor();
                            int afterHonor = beforeHonor + honor;
                            pc.setHonor(afterHonor);
                            // 抓升級前的階段 max 分數
                            int stageLevel = Honor.getInstance().getHonorLevel(beforeHonor);
                            L1WilliamHonor honorData = Honor.getInstance().getTemplate(stageLevel);
                            int nextMax = (honorData != null) ? honorData.getHonorMax() : 99999;

                            pc.sendPackets(new S_SystemMessage("\\fR目前爵位積分：" + afterHonor + " / " + nextMax));

                            // 判斷是否完成任務
                            if (NpcHonorTable.get().isTargetNpc(npcId)
                                    && beforeHonor < nextMax && afterHonor >= nextMax) {
                                Honor.getInstance().markDailyQuestComplete(pc);
                                Honor.getInstance().checkHonor(pc, true, true); // 加入強制傳送參數
                            }
                        }
                    }
                }
            }
            int score = NpcScoreTable.get().get_score(getNpcId());  //src041
            if (score > 0 && !isResurrect()) {
                if (pc.hasSkillEffect(7002)) {
                    score *= 2;
                }
                if (pc.hasSkillEffect(7003)) {
                    score *= 3;
                }
                pc.get_other().add_score(score);
                if (CampSet.CAMPSTART) {
                    if (pc.get_c_power() != null && pc.get_c_power().get_c1_type() != 0) {
                        int lv = C1_Name_Type_Table.get().getLv(pc.get_c_power().get_c1_type(), pc.get_other().get_score());
                        if (lv != pc.get_c_power().get_power().get_c1_id()) {
                            pc.get_c_power().set_power(pc, false);
                            pc.sendPackets(new S_ServerMessage("\\fR階級變更:" + pc.get_c_power().get_power().get_c1_name_type()));
                            pc.sendPacketsAll(new S_ChangeName(pc, true));
                        }
                    }
                }
            }
            if (QuestMobSet.START) {
                ServerQuestMobTable.get().checkQuestMob(pc, getNpcId());
            }
            giveWeaponSoulExp(pc); // 武器劍靈系統
            // 特定怪物死亡掉落道具或給予狀態系統
            if (MobItemTable.getSetList() != null) {
                MobItemTable.forMobItem(pc, this);
            }
            // 死亡後續處理
            if (isDead()) {
                // 掉落物品分配
                distributeDrop();
                // 陣營
                giveKarma(pc);
                // 立即移除怪物屍體，死亡即消失
                try {
                    deleteMe();
                } catch (Exception ignored) {}
            }
        }
    }

    /**
     * 掉落物品分配
     */
    private void distributeDrop() {
        ArrayList<L1Character> dropTargetList = _dropHateList.toTargetArrayList();
        ArrayList<Integer> dropHateList = _dropHateList.toHateArrayList();
        try {
            DropShareExecutor dropShareExecutor = new DropShare();
            dropShareExecutor.dropShare(this, dropTargetList, dropHateList);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 陣營
     *
     */
    private void giveKarma(L1PcInstance pc) {
        int karma = getKarma();
        if (karma != 0) {
            int karmaSign = Integer.signum(karma);
            int pcKarmaLevel = pc.getKarmaLevel();
            int pcKarmaLevelSign = Integer.signum(pcKarmaLevel);
            if (pcKarmaLevelSign != 0 && karmaSign != pcKarmaLevelSign) {
                karma *= 5;
            }
            pc.addKarma((int) (karma * ConfigRate.RATE_KARMA));
        }
    }

    /**
     * 給予勇者的勳章
     */
    private void giveUbSeal() {
        if (this.getUbSealCount() != 0) { // UB勇者證
            final L1UltimateBattle ub = UBTable.getInstance().getUb(this.getUbId());
            if (ub != null) {
                for (final L1PcInstance pc : ub.getMembersArray()) {
                    if (pc != null && !pc.isDead() && !pc.isGhost()) {
                        final L1ItemInstance item =
                                // 勇者的勳章(41402)
                                pc.getInventory().storeItem(41402, this.getUbSealCount());
                        // 403 獲得%0%o 。
                        pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                    }
                }
            }
        }
    }

    /**
     * 背包是否禁止加入掉落物品
     *
     * @return true:不加入 false:加入
     */
    public boolean is_storeDroped() {
        return this._storeDroped;
    }

    /**
     * 設置背包是否禁止加入掉落物品
     *
     * @param flag true:不加入 false:加入
     */
    public void set_storeDroped(final boolean flag) {
        this._storeDroped = flag;
    }

    /**
     * 給予勇氣之證數量
     *
     */
    public int getUbSealCount() {
        return this._ubSealCount;
    }

    /**
     * 設置給予勇氣之證數量
     *
     */
    public void setUbSealCount(final int i) {
        this._ubSealCount = i;
    }

    /**
     * UBID
     *
     */
    public int getUbId() {
        return this._ubId;
    }

    /**
     * UBID
     *
     */
    public void setUbId(final int i) {
        this._ubId = i;
    }

    /**
     * 一定機率躲藏
     */
    private void hide() {
        int npcid = getNpcTemplate().get_npcId();
        switch (npcid) {
            case 45061: // 弱化史巴托
            case 45161: // 史巴托
            case 45181: // 史巴托
            case 45455: // 殘暴的史巴托
                if (getMaxHp() / 3 > getCurrentHp()) {
                    int rnd = _random.nextInt(10);
                    if (rnd < 1) {
                        allTargetClear();
                        setHiddenStatus(1);
                        broadcastPacketAll(new S_DoActionGFX(getId(), 11));
                        setStatus(13);
                        broadcastPacketAll(new S_NPCPack(this));
                    }
                }
                break;
            case 45682: // 安塔瑞斯
                if (getMaxHp() / 3 > getCurrentHp()) {
                    int rnd = _random.nextInt(100);
                    if (rnd < 1) {
                        allTargetClear();
                        setHiddenStatus(1);
                        broadcastPacketAll(new S_DoActionGFX(getId(), 20));
                        setStatus(20);
                        broadcastPacketAll(new S_NPCPack(this));
                    }
                }
                break;
            case 97259: // 沙蟲
                if (getMaxHp() / 3 > getCurrentHp()) {
                    int rnd = _random.nextInt(100);
                    if (rnd < 1) {
                        allTargetClear();
                        setHiddenStatus(1);
                        broadcastPacketAll(new S_DoActionGFX(getId(), 11));
                        setStatus(11);
                        broadcastPacketAll(new S_NPCPack(this));
                    }
                }
                break;
            case 45067:// 弱化哈維
            case 45090:// 弱化格利芬
            case 45264:// 哈維
            case 45321:// 格利芬
            case 45445:// 格利芬(遺忘)
            case 45452:// 哈維 (遺忘)
                if (getMaxHp() / 3 > getCurrentHp()) {
                    int rnd = _random.nextInt(10);
                    if (rnd < 1) {
                        allTargetClear();
                        setHiddenStatus(2);
                        broadcastPacketAll(new S_DoActionGFX(getId(), 44));
                        setStatus(4);
                        broadcastPacketAll(new S_NPCPack(this));
                    }
                }
                break;
            case 46107: // 底比斯 曼陀羅草(白)
            case 46108: // 底比斯 曼陀羅草(黑)
                if (getMaxHp() / 4 > getCurrentHp()) {
                    int rnd = _random.nextInt(10);
                    if (rnd < 1) {
                        allTargetClear();
                        setHiddenStatus(1);
                        broadcastPacketAll(new S_DoActionGFX(getId(), 11));
                        setStatus(13);
                        broadcastPacketAll(new S_NPCPack(this));
                    }
                }
            case 105078: // 須曼
                if (getMaxHp() / 3 > getCurrentHp()) {
                    int rnd = _random.nextInt(100);
                    if (rnd < 1) {
                        allTargetClear();
                        setHiddenStatus(1);
                        broadcastPacketAll(new S_DoActionGFX(getId(), 4));
                        setStatus(4);
                        broadcastPacketAll(new S_NPCPack(this));
                    }
                }
                break;
        }
    }

    /**
     * 召喚後隱藏
     */
    public void initHide() {
        int npcid = getNpcTemplate().get_npcId();
        int rnd = _random.nextInt(3);
        switch (npcid) {
            case 45061: // 弱化史巴托
            case 45161: // 史巴托
            case 45181: // 史巴托
            case 45455: // 殘暴的史巴托
                if (1 > rnd) {
                    setHiddenStatus(1);
                    setStatus(13);
                }
                break;
            case 45045: // 弱化高侖石頭怪
            case 45126: // 高侖石頭怪
            case 45134: // 高侖石頭怪
            case 45281: // 奇巖 高侖石頭怪
                if (1 > rnd) {
                    setHiddenStatus(1);
                    setStatus(4);
                }
                break;
            case 45067:// 弱化哈維
            case 45090:// 弱化格利芬
            case 45264:// 哈維
            case 45321:// 格利芬
            case 45445:// 格利芬(遺忘)
            case 45452:// 哈維 (遺忘)
                setHiddenStatus(2);
                setStatus(4);
                break;
            case 45681:// 林德拜爾
                setHiddenStatus(2);
                setStatus(11);
                break;
            case 46107: // 底比斯 曼陀羅草(白)
            case 46108: // 底比斯 曼陀羅草(黑)
                if (1 > rnd) {
                    setHiddenStatus(1);
                    setStatus(13);
                }
                break;
            case 46125:// 高侖鋼鐵怪
            case 46126:// 萊肯
            case 46127:// 歐熊
            case 46128:// 冰原老虎
                setHiddenStatus(3);
                setStatus(4);
                break;
            case 97259: // 沙蟲
                setHiddenStatus(1);
                setStatus(11);
                break;
        }
    }

    /**
     * 隊員召喚後隱藏
     */
    public void initHideForMinion(L1NpcInstance leader) {
        int npcid = getNpcTemplate().get_npcId();
        if (leader.getHiddenStatus() == 1) {// 遁地
            switch (npcid) {
                case 45061: // 弱化史巴托
                case 45161: // 史巴托
                case 45181: // 史巴托
                case 45455: // 殘暴的史巴托
                    setHiddenStatus(1);
                    setStatus(13);
                    break;
                case 45045: // 弱化高侖石頭怪
                case 45126: // 高侖石頭怪
                case 45134: // 高侖石頭怪
                case 45281: // 奇巖 高侖石頭怪
                    setHiddenStatus(1);
                    setStatus(4);
                    break;
                case 46107: // 底比斯 曼陀羅草(白)
                case 46108: // 底比斯 曼陀羅草(黑)
                    setHiddenStatus(1);
                    setStatus(13);
                    break;
                default:
                    break;
            }
        } else if (leader.getHiddenStatus() == 2) {// 飛天
            switch (npcid) {
                case 45067:// 弱化哈維
                case 45090:// 弱化格利芬
                case 45264:// 哈維
                case 45321:// 格利芬
                case 45445:// 格利芬(遺忘)
                case 45452:// 哈維 (遺忘)
                    setHiddenStatus(2);
                    setStatus(4);
                    break;
                case 45681:// 林德拜爾
                    setHiddenStatus(2);
                    setStatus(11);
                    break;
                case 46125:// 高侖鋼鐵怪
                case 46126:// 萊肯
                case 46127:// 歐熊
                case 46128:// 冰原老虎
                    setHiddenStatus(3);
                    setStatus(4);
            }
        }
    }

    @Override
    public void transform(int transformId) {
        super.transform(transformId);
        getInventory().clearItems();
        SetDropExecutor setDropExecutor = new SetDrop();
        setDropExecutor.setDrop(this, getInventory());
        getInventory().shuffle();
    }

    private void sendServerMessage(int msgid) {
        L1QuestUser quest = WorldQuest.get().get(get_showId());
        if (quest != null && !quest.pcList().isEmpty()) {
            for (L1PcInstance pc : quest.pcList()) {
                pc.sendPackets(new S_ServerMessage(msgid));
            }
        }
    }

    public long getLastHprTime() {
        if (_lasthprtime == 0) {// 上次回血時間為0
            return _lasthprtime = System.currentTimeMillis() / 1000 - 5;// 前五秒的時間
        }
        return _lasthprtime;
    }

    public void setLastHprTime(long time) {
        _lasthprtime = time;
    }

    public long getLastMprTime() {
        if (_lastmprtime == 0) {// 上次回魔時間為0
            return _lastmprtime = System.currentTimeMillis() / 1000 - 5;// 前五秒的時間
        }
        return _lastmprtime;
    }

    public void setLastMprTime(long time) {
        _lastmprtime = time;
    }

    // 8.1連擊系統
    private void calcCombo(L1Character lastAttacker) {
        if (lastAttacker instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) lastAttacker;
            if (!pc.hasSkillEffect(L1SkillId.COMBO_BUFF)) {
                //if ((pc.getEinhasad() / 10000 > 100) && (CommonUtil.random(100) <= 10)) {
                if (LeavesSet.START && pc.get_other().get_leaves_time_exp() > 0) { // 還有剩餘經驗額度
                    pc.setComboCount(1);
                    pc.setSkillEffect(L1SkillId.COMBO_BUFF, 50000);
                    pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_COMBO_BUFF, pc.getComboCount()));
                }
            } else if (pc.getComboCount() < 30) {
                pc.setComboCount(pc.getComboCount() + 1);
                pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_COMBO_BUFF, pc.getComboCount()));
            } else {
                pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_COMBO_BUFF, 31));
            }
        }
    }

    /**
     * 武器劍靈系統
     */
    private void giveWeaponSoulExp(L1PcInstance pc) {
        int WeaponsoulExp = getNpcTemplate().getWeaponSoulExp();
        if (pc.getWeapon() != null && WeaponsoulExp != 0 && !isResurrect()) {
            L1WeaponSoul.storeWeaponSoulExp(pc, pc.getWeapon(), WeaponsoulExp);
        }
    }

    private final class CountDownTimer extends TimerTask {
        private final int _loc_x;
        private final int _loc_y;
        private final short _loc_mapId;
        private final L1QuestUser _quest;
        private final int _firstMsgId;

        public CountDownTimer(int loc_x, int loc_y, short loc_mapId, L1QuestUser quest, int firstMsgId) {
            _loc_x = loc_x;
            _loc_y = loc_y;
            _loc_mapId = loc_mapId;
            _quest = quest;
            _firstMsgId = firstMsgId;
        }

        public void run() {
            try {
                if (_firstMsgId != 0) {
                    L1MonsterInstance.this.sendServerMessage(_firstMsgId);
                }
                TimeUnit.MILLISECONDS.sleep(10000L);
                L1MonsterInstance.this.sendServerMessage(1476);
                TimeUnit.MILLISECONDS.sleep(10000L);
                L1MonsterInstance.this.sendServerMessage(1477);
                TimeUnit.MILLISECONDS.sleep(10000L);
                L1MonsterInstance.this.sendServerMessage(1478);
                TimeUnit.MILLISECONDS.sleep(5000L);
                L1MonsterInstance.this.sendServerMessage(1480);
                TimeUnit.MILLISECONDS.sleep(1000L);
                L1MonsterInstance.this.sendServerMessage(1481);
                TimeUnit.MILLISECONDS.sleep(1000L);
                L1MonsterInstance.this.sendServerMessage(1482);
                TimeUnit.MILLISECONDS.sleep(1000L);
                L1MonsterInstance.this.sendServerMessage(1483);
                TimeUnit.MILLISECONDS.sleep(1000L);
                L1MonsterInstance.this.sendServerMessage(1484);
                TimeUnit.MILLISECONDS.sleep(1000L);
                for (int i = 10; i > 0; i--) {
                    if (_quest != null && !_quest.pcList().isEmpty()) {
                        for (L1PcInstance pc : _quest.pcList()) {
                            L1Teleport.teleport(pc, _loc_x, _loc_y, _loc_mapId, pc.getHeading(), true);
                        }
                    }
                    TimeUnit.MILLISECONDS.sleep(500L);
                }
            } catch (Exception localException) {
            }
        }
    }

    /**
     * 死亡判斷
     *
     * @author daien
     */
    class Death implements Runnable {
        L1Character _lastAttacker;// 攻擊者

        /**
         * 死亡判斷
         *
         * @param lastAttacker 攻擊者
         */
        public Death(L1Character lastAttacker) {
            _lastAttacker = lastAttacker;
        }

        @Override
        public void run() {
            calcCombo(_lastAttacker); // 8.1連擊系統
            L1MonsterInstance mob = L1MonsterInstance.this;
            if (_lastAttacker instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) _lastAttacker;
                pc.setKillCount(pc.getKillCount() + 1);
                pc.sendPackets(new S_OwnCharStatus(pc));
                /** [原碼] 無限大戰計分系統 */
                if (mob.getUbId() != 0) {
                    int ubexp = (int) (getExp() / 10);
                    pc.setUbScore(pc.getUbScore() + ubexp);
                }
                // 殺死高骷隨機傳進大黑長者地區
                if (mob.getNpcId() == 45269 && mob.isAiRunning() || mob.getNpcId() == 45270 && mob.isAiRunning() || mob.getNpcId() == 45286 && mob.isAiRunning()) {
                    if (_random.nextInt(1000) < 15) {// 1.5%機率
                        int rnd = _random.nextInt(3);
                        switch (rnd) {
                            case 0:
                                L1Teleport.teleport(pc, 33335, 32437, (short) 4, 5, true);
                                break;
                            case 1:
                                L1Teleport.teleport(pc, 33262, 32403, (short) 4, 5, true);
                                break;
                            case 2:
                                L1Teleport.teleport(pc, 33392, 32345, (short) 4, 5, true);
                                break;
                        }
                    }
                }
            }
            mob.setDeathProcessing(true);
            mob.setCurrentHpDirect(0);
            mob.setDead(true);
            mob.setStatus(8);
            mob.broadcastPacketAll(new S_DoActionGFX(mob.getId(), 8));
            mob.getMap().setPassable(mob.getLocation(), true);
            mob.startChat(1);
            mob.distributeExpDropKarma(_lastAttacker);
            mob.giveUbSeal();
            mob.setDeathProcessing(false);
            mob.setExp(0L);
            mob.setKarma(0);
            mob.allTargetClear();
            
            // 改進：根據配置決定清理和重生方式
            boolean isBossMonster = false;
            int deltime = 0;
            
            switch (mob.getNpcId()) {
                case 71016:// 安塔瑞斯
                case 71028:// 法利昂
                case 46123:// 底比斯
                case 46124:
                case 92000:// 雙蛇
                case 92001:
                case 97206:// 林德拜爾
                    isBossMonster = true;
                    deltime = ConfigAlt.BOSS_MONSTER_DEATH_DISPLAY_TIME;
                    break;
                default:
                    deltime = ConfigAlt.MIN_MONSTER_DEATH_DISPLAY_TIME;
            }
            
            if (ConfigAlt.ENABLE_DIRECT_RESPAWN_TRIGGER && !isBossMonster) {
                // 普通怪物：死亡動畫播放後直接deleteMe()觸發重生邏輯
                mob.set_deadTimerTemp(-1); // 標記跳過NpcDeadTimer處理
                
                GeneralThreadPool.get().schedule(() -> {
                    if (mob.isDead() && !mob.destroyed()) {
                        // 直接刪除並觸發重生邏輯（在deleteMe()中會呼叫executeSpawnTask）
                        mob.deleteMe();
                    }
                }, deltime * 1000);
            } else {
                // BOSS怪物或關閉直接重生功能時，使用原有的延遲清理方式
                mob.startDeleteTimer(deltime);
            }
        }
    }

    private class deathDragonTimer1 extends TimerTask {
        private final L1MonsterInstance npc;
        private final short mapId;

        public deathDragonTimer1(L1MonsterInstance paramShort, short arg3) {
            npc = paramShort;
            mapId = arg3;
        }

        public void run() {
            try {
                if (npc.getNpcId() == 71014) {
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    L1MonsterInstance.this.sendServerMessage(1573);
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    L1MonsterInstance.this.sendServerMessage(1574);
                    TimeUnit.MILLISECONDS.sleep(10000L);
                    L1MonsterInstance.this.sendServerMessage(1575);
                    TimeUnit.MILLISECONDS.sleep(10000L);
                    L1MonsterInstance.this.sendServerMessage(1576);
                    TimeUnit.MILLISECONDS.sleep(10000L);
                    int i = 32776 + L1MonsterInstance._random.nextInt(20);
                    int k = 32679 + L1MonsterInstance._random.nextInt(20);
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    L1Location loc = new L1Location(i, k, mapId);
                    L1SpawnUtil.spawn(71015, loc, new Random().nextInt(8), get_showId());
                } else if (npc.getNpcId() == 71015) {
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    L1MonsterInstance.this.sendServerMessage(1577);
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    L1MonsterInstance.this.sendServerMessage(1578);
                    TimeUnit.MILLISECONDS.sleep(10000L);
                    L1MonsterInstance.this.sendServerMessage(1579);
                    TimeUnit.MILLISECONDS.sleep(10000L);
                    int j = 32776 + L1MonsterInstance._random.nextInt(20);
                    int m = 32679 + L1MonsterInstance._random.nextInt(20);
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    L1Location loc = new L1Location(j, m, mapId);
                    L1SpawnUtil.spawn(71016, loc, new Random().nextInt(8), get_showId());
                } else if (npc.getNpcId() == 71016) {
                    ArrayList<L1Character> targetList = npc.getHateList().toTargetArrayList();
                    if (!targetList.isEmpty()) {
                        for (L1Character cha : targetList) {
                            if (cha instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) cha;
                                System.out.println("玩家:" + pc.getName() + " 發動地龍-龍之血痕");
                                pc.sendPacketsX8(new S_SkillSound(pc.getId(), 7854));
                                SkillMode mode = L1SkillMode.get().getSkill(6797);
                                if (mode != null) {
                                    mode.start(pc, null, null, 43200);
                                }
                            }
                        }
                    }
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    L1MonsterInstance.this.sendServerMessage(1580);
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    L1MonsterInstance.this.sendServerMessage(1581);
                    TimeUnit.MILLISECONDS.sleep(10000L);
                    GeneralThreadPool.get().execute(new L1MonsterInstance.CountDownTimer(33718, 32506, (short) 4, WorldQuest.get().get(get_showId()), 0));
                    /*
                     * boolean hasDoor = false; for (L1Object obj :
                     * World.get().getObject()) { if ((obj instanceof
                     * L1NpcInstance)) { L1NpcInstance find_npc =
                     * (L1NpcInstance)obj; if (find_npc.getNpcId() == 70936) {
                     * hasDoor = true; return; } } } if (!hasDoor) {
                     * TimeUnit.MILLISECONDS.sleep(7000L);
                     *
                     * World.get().broadcastPacketToAll(new
                     * S_ServerMessage(1582)); TimeUnit.MILLISECONDS.sleep(10000L);
                     *
                     * World.get().broadcastPacketToAll(new
                     * S_ServerMessage(1583));
                     *
                     * L1Location loc = new L1Location(33725, 32506, 4);
                     * NpcSpawnTable.get().storeSpawn(70936, loc.getX(),
                     * loc.getY(), loc.getMapId(), 21600000);
                     * L1SpawnUtil.spawn(70936, loc, 0, -1); }
                     */
                }
            } catch (Exception localException) {
            }
        }
    }

    private class deathDragonTimer2 extends TimerTask {
        private final L1MonsterInstance npc;
        private final short mapId;

        public deathDragonTimer2(L1MonsterInstance paramShort, short arg3) {
            npc = paramShort;
            mapId = arg3;
        }

        public void run() {
            try {
                if (npc.getNpcId() == 71026) {
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    L1MonsterInstance.this.sendServerMessage(1661);
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    L1MonsterInstance.this.sendServerMessage(1662);
                    TimeUnit.MILLISECONDS.sleep(10000L);
                    L1MonsterInstance.this.sendServerMessage(1663);
                    TimeUnit.MILLISECONDS.sleep(10000L);
                    L1MonsterInstance.this.sendServerMessage(1664);
                    TimeUnit.MILLISECONDS.sleep(10000L);
                    int j = 32948 + L1MonsterInstance._random.nextInt(20);
                    int m = 32825 + L1MonsterInstance._random.nextInt(20);
                    L1Location loc = new L1Location(j, m, mapId);
                    L1SpawnUtil.spawn(71027, loc, new Random().nextInt(8), get_showId());
                } else if (npc.getNpcId() == 71027) {
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    L1MonsterInstance.this.sendServerMessage(1665);
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    L1MonsterInstance.this.sendServerMessage(1666);
                    TimeUnit.MILLISECONDS.sleep(10000L);
                    L1MonsterInstance.this.sendServerMessage(1667);
                    TimeUnit.MILLISECONDS.sleep(10000L);
                    int k = 32948 + L1MonsterInstance._random.nextInt(20);
                    int n = 32825 + L1MonsterInstance._random.nextInt(20);
                    L1Location loc = new L1Location(k, n, mapId);
                    L1SpawnUtil.spawn(71028, loc, new Random().nextInt(8), get_showId());
                } else if (npc.getNpcId() == 71028) {
                    ArrayList<L1Character> targetList = npc.getHateList().toTargetArrayList();
                    if (!targetList.isEmpty()) {
                        for (L1Character cha : targetList) {
                            if (cha instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) cha;
                                System.out.println("玩家:" + pc.getName() + " 發動水龍-龍之血痕");
                                pc.sendPacketsX8(new S_SkillSound(pc.getId(), 7854));
                                SkillMode mode = L1SkillMode.get().getSkill(6798);
                                if (mode != null) {
                                    mode.start(pc, null, null, 43200);
                                }
                            }
                        }
                    }
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    L1MonsterInstance.this.sendServerMessage(1668);
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    L1MonsterInstance.this.sendServerMessage(1669);
                    TimeUnit.MILLISECONDS.sleep(10000L);
                    GeneralThreadPool.get().execute(new L1MonsterInstance.CountDownTimer(33718, 32506, (short) 4, WorldQuest.get().get(get_showId()), 0));
                    /*
                     * boolean hasDoor = false; for (L1Object obj :
                     * World.get().getObject()) { if ((obj instanceof
                     * L1NpcInstance)) { L1NpcInstance find_npc =
                     * (L1NpcInstance)obj; if (find_npc.getNpcId() == 70936) {
                     * hasDoor = true; return; } } } if (!hasDoor) {
                     * TimeUnit.MILLISECONDS.sleep(7000L);
                     *
                     * World.get().broadcastPacketToAll(new
                     * S_ServerMessage(1582)); TimeUnit.MILLISECONDS.sleep(10000L);
                     *
                     * World.get().broadcastPacketToAll(new
                     * S_ServerMessage(1583));
                     *
                     * L1Location loc = new L1Location(33725, 32506, 4);
                     * NpcSpawnTable.get().storeSpawn(70936, loc.getX(),
                     * loc.getY(), loc.getMapId(), 21600000);
                     * L1SpawnUtil.spawn(70936, loc, 0, -1); }
                     */
                }
            } catch (Exception localException) {
            }
        }
    }

    private class deathDragonTimer3 extends TimerTask {
        private final L1MonsterInstance npc;

        public deathDragonTimer3(L1MonsterInstance npc) {
            this.npc = npc;
        }

        public void run() {
            try {
                if (this.npc.getNpcId() == 97204) {
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    L1MonsterInstance.this.sendServerMessage(1759);
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    L1MonsterInstance.this.sendServerMessage(1760);
                    TimeUnit.MILLISECONDS.sleep(10000L);
                    L1MonsterInstance.this.sendServerMessage(1761);
                    TimeUnit.MILLISECONDS.sleep(10000L);
                    L1MonsterInstance.this.sendServerMessage(1762);
                    TimeUnit.MILLISECONDS.sleep(10000L);
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    L1NpcInstance[] npc_list = {L1SpawnUtil.spawn(97207, new L1Location(32850, 32856, this.npc.getMapId()), 4, this.npc.get_showId()), L1SpawnUtil.spawn(97208, new L1Location(32864, 32862, this.npc.getMapId()), 5, this.npc.get_showId()), L1SpawnUtil.spawn(97209, new L1Location(32869, 32876, this.npc.getMapId()), 6, this.npc.get_showId())};
                    npc_list[L1MonsterInstance._random.nextInt(npc_list.length)].set_quest_id(1);
                } else if (this.npc.getNpcId() >= 97207 && this.npc.getNpcId() <= 97209) {
                    if (this.npc.get_quest_id() <= 0) {
                        return;
                    }
                    L1QuestUser quest = WorldQuest.get().get(this.npc.get_showId());
                    for (L1NpcInstance npc : quest.npcList()) {
                        if (!npc.isDead() && npc.getNpcId() >= 97207 && npc.getNpcId() <= 97209) {
                            npc.set_spawnTime(3);
                        }
                    }
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    L1MonsterInstance.this.sendServerMessage(1763);
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    L1MonsterInstance.this.sendServerMessage(1764);
                    TimeUnit.MILLISECONDS.sleep(10000L);
                    L1MonsterInstance.this.sendServerMessage(1765);
                    TimeUnit.MILLISECONDS.sleep(10000L);
                    L1MonsterInstance.this.sendServerMessage(1766);
                    TimeUnit.MILLISECONDS.sleep(10000L);
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    L1Location loc = new L1Location(32846, 32877, this.npc.getMapId()).randomLocation(10, true);
                    L1SpawnUtil.spawn(97205, loc, 0, this.npc.get_showId());
                } else if (this.npc.getNpcId() == 97205) {
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    L1MonsterInstance.this.sendServerMessage(1767);
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    L1MonsterInstance.this.sendServerMessage(1768);
                    TimeUnit.MILLISECONDS.sleep(10000L);
                    L1MonsterInstance.this.sendServerMessage(1769);
                    TimeUnit.MILLISECONDS.sleep(10000L);
                    L1MonsterInstance.this.sendServerMessage(1770);
                    TimeUnit.MILLISECONDS.sleep(10000L);
                    L1MonsterInstance.this.sendServerMessage(1771);
                    TimeUnit.MILLISECONDS.sleep(10000L);
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    L1Location loc = new L1Location(32846, 32877, this.npc.getMapId()).randomLocation(10, true);
                    L1SpawnUtil.spawn(97206, loc, 0, this.npc.get_showId());
                } else if (this.npc.getNpcId() == 97206) {
                    ArrayList<L1Character> targetList = this.npc.getHateList().toTargetArrayList();
                    if (!targetList.isEmpty()) {
                        for (L1Character cha : targetList) {
                            if (cha instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) cha;
                                System.out.println("玩家:" + pc.getName() + " 發動風龍-龍之血痕");
                                pc.sendPacketsX8(new S_SkillSound(pc.getId(), 7854));
                                SkillMode mode = L1SkillMode.get().getSkill(6799);
                                if (mode != null) {
                                    mode.start(pc, null, null, 43200);
                                }
                            }
                        }
                    }
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    L1MonsterInstance.this.sendServerMessage(1772);
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    L1MonsterInstance.this.sendServerMessage(1773);
                    TimeUnit.MILLISECONDS.sleep(10000L);
                    GeneralThreadPool.get().execute(new L1MonsterInstance.CountDownTimer(33718, 32506, (short) 4, WorldQuest.get().get(get_showId()), 0));
                    /*
                     * boolean hasDoor = false; for (L1Object obj :
                     * World.get().getObject()) { if ((obj instanceof
                     * L1NpcInstance)) { L1NpcInstance find_npc =
                     * (L1NpcInstance)obj; if (find_npc.getNpcId() == 70936) {
                     * hasDoor = true; return; } } } if (!hasDoor) {
                     * TimeUnit.MILLISECONDS.sleep(7000L);
                     *
                     * World.get().broadcastPacketToAll(new
                     * S_ServerMessage(1582)); TimeUnit.MILLISECONDS.sleep(10000L);
                     *
                     * World.get().broadcastPacketToAll(new
                     * S_ServerMessage(1583));
                     *
                     * L1Location loc = new L1Location(33725, 32506, 4);
                     * NpcSpawnTable.get().storeSpawn(70936, loc.getX(),
                     * loc.getY(), loc.getMapId(), 21600000);
                     * L1SpawnUtil.spawn(70936, loc, 0, -1); }
                     */
                }
            } catch (Exception localException) {
            }
        }
    }
}
