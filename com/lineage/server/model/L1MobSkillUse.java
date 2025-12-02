package com.lineage.server.model;

import com.lineage.server.datatables.MobSkillTable;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.skill.L1SkillDelayforMob;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1MobSkill;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

import static com.lineage.server.model.skill.L1SkillId.CANCELLATION;
import static com.lineage.server.model.skill.L1SkillId.SHOCK_STUN;

public class L1MobSkillUse {
    private static final Log _log = LogFactory.getLog(L1MobSkillUse.class);
    private static final Random _rnd = new Random();
    private L1MobSkill _mobSkillTemplate = null;
    private L1NpcInstance _attacker = null;
    private L1Character _target = null;
    private int _sleepTime = 0;
    private int[] _skillUseCount;
    /**
     * DB技能數量
     */
    private int _skillSize;

    public L1MobSkillUse(L1NpcInstance npc) {
        try {
            _sleepTime = 0;
            _mobSkillTemplate = MobSkillTable.getInstance().getTemplate(npc.getNpcTemplate().get_npcId());
            if (_mobSkillTemplate == null) {
                return;
            }
            _attacker = npc;
            _skillSize = getMobSkillTemplate().getSkillSize();
            _skillUseCount = new int[_skillSize];
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 傳回技能使用次數
     *
     */
    private int getSkillUseCount(int idx) {
        return _skillUseCount[idx];
    }

    /**
     * 技能使用次數+1
     *
     */
    private void skillUseCountUp(int idx) {
        _skillUseCount[idx] += 1;
    }

    /**
     * 歸零所有技能使用次數
     */
    public void resetAllSkillUseCount() {
        if (_mobSkillTemplate == null) {
            return;
        }
        for (int i = 0; i < _skillSize; i++) {
            _skillUseCount[i] = 0;
        }
    }

    /**
     * 動作間隔時間
     *
     */
    public int getSleepTime() {
        return _sleepTime;
    }

    /**
     * 動作間隔時間
     *
     */
    public void setSleepTime(int i) {
        _sleepTime = i;
    }

    /**
     * 傳回怪物技能設定
     *
     */
    public L1MobSkill getMobSkillTemplate() {
        return _mobSkillTemplate;
    }

    /**
     * 是否符合DB技能數量裡任何一種技能的使用條件
     *
     */
    public boolean isSkillTrigger(L1Character tg) {
        if (_mobSkillTemplate == null) {
            return false;
        }
        _target = tg;
        int type = getMobSkillTemplate().getType(0);
        if (type == 0) {
            return false;
        }
        for (int i = 0; i < _skillSize; i++) {
            if (isSkillUseble(i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 使用技能的判定
     *
     * @return true:技能使用成功;false:技能使用失敗
     */
    public boolean skillUse(L1Character tg) {
        if (_mobSkillTemplate == null) {
            return false;
        }
        _target = tg;
        int type = getMobSkillTemplate().getType(0);
        if (type == 0) {
            return false;
        }
        int[] skills = null;
        int skillSizeCounter = 0;// 符合施展條件的技能數量計數器
        if (_skillSize >= 0) {
            skills = new int[_skillSize];
        }
        for (int i = 0; i < _skillSize; i++) {
            if (isSkillUseble(i)) {
                skills[skillSizeCounter] = i;
                skillSizeCounter++;// 計數器+1
            }
        }
        if (skillSizeCounter != 0) {// 符合施展條件的技能數量不為0
            int num = _rnd.nextInt(skillSizeCounter);// 隨機使用一種技能
            if (useSkill(skills[num])) {
                return true;
            }
        }
        return false;
    }

    // 技能使用延遲的處理
    private void setDelay(int idx) {
        if (this._mobSkillTemplate.getReuseDelay(idx) > 0) {
            L1SkillDelayforMob.onSkillUse(_attacker, _mobSkillTemplate.getReuseDelay(idx), idx);
        }
    }

    /**
     * 使用技能是否成功
     *
     * @return true:技能使用成功;false:技能使用失敗
     */
    private boolean useSkill(int idx) {
        // System.out.println("使用技能 idx ==" + idx);
        // 變換目標的處理
        final int changeType = getMobSkillTemplate().getChangeTarget(idx);
        if (changeType > 0) {
            _target = this.changeTarget(changeType, idx);
        }
        // 技能延遲狀態
        if (_mobSkillTemplate.isSkillDelayIdx(idx)) {
            return false;
        }
        boolean isUseSkill = false;
        int rnd = getMobSkillTemplate().getTriggerRandom(idx);// 發動機率
        if ((rnd > 0) && (rnd <= _rnd.nextInt(100) + 1)) {// 符合發動機率
            int type = getMobSkillTemplate().getType(idx);
            switch (type) {// 技能種類
                case 1:
                    if (physicalAttack(idx)) {
                        skillUseCountUp(idx);
                        setDelay(idx);
                        isUseSkill = true;
                    }
                    break;
                case 2:
                    if (magicAttack(idx)) {
                        skillUseCountUp(idx);
                        setDelay(idx);
                        isUseSkill = true;
                    }
                    break;
                case 3:
                    if (summon(idx)) {
                        skillUseCountUp(idx);
                        setDelay(idx);
                        isUseSkill = true;
                    }
                    break;
                case 4:
                    if (poly(idx)) {
                        skillUseCountUp(idx);
                        setDelay(idx);
                        isUseSkill = true;
                    }
                    break;
                case 5:
                    if (areashock_stun(idx)) {
                        skillUseCountUp(idx);
                        setDelay(idx);
                        isUseSkill = true;
                    }
                    break;
                case 6:
                    if (areacancellation(idx)) {
                        skillUseCountUp(idx);
                        setDelay(idx);
                        isUseSkill = true;
                    }
                    break;
                case 7:
                    if (weapon_break(idx)) {
                        skillUseCountUp(idx);
                        setDelay(idx);
                        isUseSkill = true;
                    }
                    break;
                case 8:
                    if (potionturntodmg(idx)) {
                        skillUseCountUp(idx);
                        setDelay(idx);
                        isUseSkill = true;
                    }
                    break;
                case 9:
                    if (pollutewaterwave(idx)) {
                        skillUseCountUp(idx);
                        setDelay(idx);
                        isUseSkill = true;
                    }
                    break;
                case 10:
                    if (healturntodmg(idx)) {
                        skillUseCountUp(idx);
                        setDelay(idx);
                        isUseSkill = true;
                    }
                    break;
                case 11:
                    if (areasilence(idx)) {
                        skillUseCountUp(idx);
                        setDelay(idx);
                        isUseSkill = true;
                    }
                    break;
                case 12:
                    if (areadecaypotion(idx)) {
                        skillUseCountUp(idx);
                        setDelay(idx);
                        isUseSkill = true;
                    }
                    break;
                case 13:
                    if (areawindshackle(idx)) {
                        skillUseCountUp(idx);
                        setDelay(idx);
                        isUseSkill = true;
                    }
                    break;
                case 14:
                    if (areadebuff(idx)) {
                        skillUseCountUp(idx);
                        setDelay(idx);
                        isUseSkill = true;
                    }
                    break;
                case 15:
                    if (area_poison(idx)) {
                        skillUseCountUp(idx);
                        setDelay(idx);
                        isUseSkill = true;
                    }
                    break;
            }
        }
        return isUseSkill;
    }

    /**
     * 範圍毒霧(只生成一個毒霧)
     *
     */
    private boolean area_poison(int idx) {
        if (_attacker.getMapId() == 93) {// 怪物對戰
            return false;
        }
        int actId = getMobSkillTemplate().getActid(idx);
        int actionid = 1;
        if (actId > 0) {
            actionid = actId;
        }
        for (L1PcInstance pc : World.get().getVisiblePlayer(_attacker)) {
            if (!pc.isGmInvis()) {// 不是GM隱身狀態
                L1SpawnUtil.spawnEffect(86125, 3, pc.getX(), pc.getY(), _attacker.getMapId(), _attacker, 0);
            }
        }
        S_DoActionGFX gfx = new S_DoActionGFX(_attacker.getId(), actionid);
        _attacker.broadcastPacketAll(gfx);
        _sleepTime = _attacker.getNpcTemplate().getSubMagicSpeed();
        return true;
    }

    /**
     * 集體負面魔法(計算命中機率)
     *
     */
    private boolean areadebuff(int idx) {
        if (_attacker.getMapId() == 93) {// 怪物對戰
            return false;
        }
        int skillid = getMobSkillTemplate().getSkillId(idx);
        int actId = getMobSkillTemplate().getActid(idx);
        int gfxId = getMobSkillTemplate().getGfxid(idx);
        int time = getMobSkillTemplate().getLeverage(idx);// 借用欄位設定時間
        for (L1PcInstance pc : World.get().getVisiblePlayer(_attacker)) {
            if (skillid == 71 && pc.hasSkillEffect(4011)) {// 藥霜不與藥水侵蝕術共存
                continue;
            }
            L1SkillUse skillUse = new L1SkillUse();
            boolean canUseSkill = false;
            if (skillid > 0) {
                canUseSkill = skillUse.checkUseSkill(null, skillid, pc.getId(), pc.getX(), pc.getY(), time, // 時間
                        L1SkillUse.TYPE_GMBUFF, _attacker, actId, gfxId);
            }
            if (canUseSkill) {
                if (!pc.hasSkillEffect(skillid)) {
                    skillUse.handleCommands(null, skillid, pc.getId(), pc.getX(), pc.getY(), time, // 時間
                            L1SkillUse.TYPE_GMBUFF, _attacker);
                }
            }
        }
        int actionid = 19;// 預設施法動作
        if (actId > 0) {
            actionid = actId;
        }
        _attacker.broadcastPacketAll(new S_DoActionGFX(_attacker.getId(), actionid));
        _sleepTime = _attacker.getNpcTemplate().getSubMagicSpeed();
        return true;
    }

    /**
     * 範圍風之枷鎖(無視魔防)
     *
     */
    private boolean areawindshackle(int idx) {
        if (_attacker.getMapId() == 93) {// 怪物對戰
            return false;
        }
        int actId = getMobSkillTemplate().getActid(idx);
        int actionid = 19;
        if (actId > 0) {
            actionid = actId;
        }
        int gfxId = getMobSkillTemplate().getGfxid(idx);
        for (L1PcInstance pc : World.get().getVisiblePlayer(_attacker)) {
            if (!pc.hasSkillEffect(167)) {
                pc.sendPacketsAll(new S_SkillSound(pc.getId(), gfxId));
                pc.sendPackets(new S_PacketBoxWindShackle(pc.getId(), 16));
                pc.setSkillEffect(167, 16000);
            }
        }
        S_DoActionGFX gfx = new S_DoActionGFX(_attacker.getId(), actionid);
        _attacker.broadcastPacketAll(gfx);
        _sleepTime = _attacker.getNpcTemplate().getSubMagicSpeed();
        return true;
    }

    /**
     * 範圍藥霜(16秒)(無視魔防)
     *
     */
    private boolean areadecaypotion(int idx) {
        if (_attacker.getMapId() == 93) {// 怪物對戰
            return false;
        }
        int actId = getMobSkillTemplate().getActid(idx);
        int actionid = 19;
        if (actId > 0) {
            actionid = actId;
        }
        for (L1PcInstance pc : World.get().getVisiblePlayer(_attacker)) {
            if (!pc.hasSkillEffect(71)) {
                new L1SkillUse().handleCommands(pc, 71, pc.getId(), pc.getX(), pc.getY(), 0, L1SkillUse.TYPE_GMBUFF);
            }
        }
        S_DoActionGFX gfx = new S_DoActionGFX(_attacker.getId(), actionid);
        _attacker.broadcastPacketAll(gfx);
        _sleepTime = _attacker.getNpcTemplate().getSubMagicSpeed();
        return true;
    }

    /**
     * 範圍封印禁地(無視魔防)
     *
     */
    private boolean areasilence(int idx) {
        if (_attacker.getMapId() == 93) {// 怪物對戰
            return false;
        }
        int actId = getMobSkillTemplate().getActid(idx);
        int actionid = 19;
        if (actId > 0) {
            actionid = actId;
        }
        for (L1PcInstance pc : World.get().getVisiblePlayer(_attacker)) {
            if ((!pc.hasSkillEffect(64)) && (!pc.hasSkillEffect(161))) {
                pc.sendPacketsAll(new S_SkillSound(pc.getId(), 10708));
                pc.setSkillEffect(64, 16000);
            }
        }
        S_DoActionGFX gfx = new S_DoActionGFX(_attacker.getId(), actionid);
        _attacker.broadcastPacketAll(gfx);
        _sleepTime = _attacker.getNpcTemplate().getSubMagicSpeed();
        return true;
    }

    /**
     * 範圍治癒侵蝕術(無視魔防)
     *
     */
    private boolean healturntodmg(int idx) {
        if (_attacker.getMapId() == 93) {// 怪物對戰
            return false;
        }
        int actId = getMobSkillTemplate().getActid(idx);
        int actionid = 19;
        if (actId > 0) {
            actionid = actId;
        }
        for (L1PcInstance pc : World.get().getVisiblePlayer(_attacker)) {
            if ((!pc.hasSkillEffect(4011)) && (!pc.hasSkillEffect(4012)) && (!pc.hasSkillEffect(4013))) {
                pc.sendPacketsAll(new S_SkillSound(pc.getId(), 7780));
                pc.setSkillEffect(4013, 12000);
            }
        }
        S_DoActionGFX gfx = new S_DoActionGFX(_attacker.getId(), actionid);
        _attacker.broadcastPacketAll(gfx);
        _sleepTime = _attacker.getNpcTemplate().getSubMagicSpeed();
        return true;
    }

    /**
     * 範圍污濁的水流(無視魔防)
     *
     */
    private boolean pollutewaterwave(int idx) {
        if (_attacker.getMapId() == 93) {// 怪物對戰
            return false;
        }
        int actId = getMobSkillTemplate().getActid(idx);
        int actionid = 19;
        if (actId > 0) {
            actionid = actId;
        }
        for (L1PcInstance pc : World.get().getVisiblePlayer(_attacker)) {
            if ((!pc.hasSkillEffect(4011)) && (!pc.hasSkillEffect(4012)) && (!pc.hasSkillEffect(4013))) {
                pc.sendPacketsAll(new S_SkillSound(pc.getId(), 7782));
                pc.setSkillEffect(4012, 12000);
            }
        }
        S_DoActionGFX gfx = new S_DoActionGFX(_attacker.getId(), actionid);
        _attacker.broadcastPacketAll(gfx);
        _sleepTime = _attacker.getNpcTemplate().getSubMagicSpeed();
        return true;
    }

    /**
     * 範圍藥水侵蝕術(無視魔防)
     *
     */
    private boolean potionturntodmg(int idx) {
        if (_attacker.getMapId() == 93) {// 怪物對戰
            return false;
        }
        int actId = getMobSkillTemplate().getActid(idx);
        int actionid = 19;
        if (actId > 0) {
            actionid = actId;
        }
        for (L1PcInstance pc : World.get().getVisiblePlayer(_attacker)) {
            if (!pc.hasSkillEffect(4011) && !pc.hasSkillEffect(4012) && !pc.hasSkillEffect(4013) && !pc.hasSkillEffect(71)) {
                pc.sendPacketsAll(new S_SkillSound(pc.getId(), 7781));
                pc.setSkillEffect(4011, 12000);
            }
        }
        S_DoActionGFX gfx = new S_DoActionGFX(_attacker.getId(), actionid);
        _attacker.broadcastPacketAll(gfx);
        _sleepTime = _attacker.getNpcTemplate().getSubMagicSpeed();
        return true;
    }

    /**
     * 範圍壞物術(無視魔防)
     *
     */
    private boolean weapon_break(int idx) {
        if (_attacker.getMapId() == 93) {// 怪物對戰
            return false;
        }
        int actId = getMobSkillTemplate().getActid(idx);
        int actionid = 19;
        if (actId > 0) {
            actionid = actId;
        }
        for (L1PcInstance pc : World.get().getVisiblePlayer(_attacker)) {
            L1ItemInstance weapon = pc.getWeapon();
            if (weapon != null) {
                int weaponDamage = _rnd.nextInt(_attacker.getInt() / 3) + 1;
                pc.sendPacketsAll(new S_SkillSound(pc.getId(), 172));
                pc.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
                pc.getInventory().receiveDamage(weapon, weaponDamage);
            }
        }
        S_DoActionGFX gfx = new S_DoActionGFX(_attacker.getId(), actionid);
        _attacker.broadcastPacketAll(gfx);
        _sleepTime = _attacker.getNpcTemplate().getSubMagicSpeed();
        return true;
    }

    /**
     * 範圍相消(無視魔防)
     *
     */
    private boolean areacancellation(int idx) {
        if (_attacker.getMapId() == 93) {// 怪物對戰
            return false;
        }
        int actId = getMobSkillTemplate().getActid(idx);
        int actionid = 19;
        if (actId > 0) {
            actionid = actId;
        }
        for (L1PcInstance pc : World.get().getVisiblePlayer(_attacker)) {
            new L1SkillUse().handleCommands(pc, CANCELLATION, pc.getId(), pc.getX(), pc.getY(), 0, L1SkillUse.TYPE_GMBUFF);
        }
        S_DoActionGFX gfx = new S_DoActionGFX(_attacker.getId(), actionid);
        _attacker.broadcastPacketAll(gfx);
        _sleepTime = _attacker.getNpcTemplate().getSubMagicSpeed();
        return true;
    }

    /**
     * 範圍沖暈(無視抗暈)
     *
     */
    private boolean areashock_stun(int idx) {
        if (_attacker.getMapId() == 93) {// 怪物對戰
            return false;
        }
        int actId = getMobSkillTemplate().getActid(idx);
        int actionid = 1;
        if (actId > 0) {
            actionid = actId;
        }
        for (L1PcInstance pc : World.get().getVisiblePlayer(_attacker)) {
            Random random = new Random();
            int shock = random.nextInt(4) + 1;
            if (pc.hasSkillEffect(SHOCK_STUN)) {
                shock += pc.getSkillEffectTimeSec(SHOCK_STUN);
            }
            if (shock > 4) {// 最大暈眩四秒
                shock = 4;
            }
            if (!pc.isGmInvis()) {// 不是GM隱身狀態
                pc.setSkillEffect(SHOCK_STUN, shock * 1000);
                pc.sendPackets(new S_Paralysis(5, true));
                pc.setParalyzed(true);  // 修復：設置玩家麻痹狀態
                L1SpawnUtil.spawnEffect(81162, shock, pc.getX(), pc.getY(), _attacker.getMapId(), _attacker, 0);
            }
        }
        S_DoActionGFX gfx = new S_DoActionGFX(_attacker.getId(), actionid);
        _attacker.broadcastPacketAll(gfx);
        _sleepTime = _attacker.getNpcTemplate().getSubMagicSpeed();
        return true;
    }

    /**
     * 召喚怪物
     *
     */
    private boolean summon(int idx) {
        if (_attacker.getMapId() == 93) {// 怪物對戰不召怪
            return false;
        }
        int actId = getMobSkillTemplate().getActid(idx);
        int actionid = 19;
        if (actId > 0) {
            actionid = actId;
        }
        int summonId = getMobSkillTemplate().getSummon(idx);
        int min = getMobSkillTemplate().getSummonMin(idx);
        int max = getMobSkillTemplate().getSummonMax(idx);
        int count = 0;
        if (summonId == 0) {
            return false;
        }
        count = _rnd.nextInt(max) + min;
        L1MobSkillUseSpawn skillUseSpawn = new L1MobSkillUseSpawn();
        skillUseSpawn.mobspawn(_attacker, _target, summonId, count);
        _attacker.broadcastPacketAll(new S_SkillSound(_attacker.getId(), 761));
        S_DoActionGFX gfx = new S_DoActionGFX(_attacker.getId(), actionid);
        _attacker.broadcastPacketAll(gfx);
        _sleepTime = _attacker.getNpcTemplate().getSubMagicSpeed();
        return true;
    }

    /**
     * 集體變形(無視魔防)
     *
     */
    private boolean poly(int idx) {
        if (_attacker.getMapId() == 93) {// 怪物對戰
            return false;
        }
        int polyId = getMobSkillTemplate().getPolyId(idx);
        int actId = getMobSkillTemplate().getActid(idx);
        int actionid = 19;
        if (actId > 0) {
            actionid = actId;
        }
        boolean usePoly = false;
        if (polyId == 0) {
            return false;
        }
        for (L1PcInstance pc : World.get().getVisiblePlayer(_attacker)) {
            if (!pc.isDead()) {
                if (!pc.isGhost()) {
                    if (!pc.isGmInvis()) {
                        if (_attacker.glanceCheck(pc.getX(), pc.getY())) {
                            int npcId = _attacker.getNpcTemplate().get_npcId();
                            switch (npcId) {
                                case 81082:
                                    pc.getInventory().takeoffEquip(945);
                            }
                            L1PolyMorph.doPoly(pc, polyId, 1800, 4);
                            usePoly = true;
                        }
                    }
                }
            }
        }
        if (usePoly) {
            Iterator<L1PcInstance> localIterator = World.get().getVisiblePlayer(_attacker).iterator();
            if (localIterator.hasNext()) {
                L1PcInstance pc = (L1PcInstance) localIterator.next();
                pc.sendPacketsAll(new S_SkillSound(pc.getId(), 230));
            }
            S_DoActionGFX gfx = new S_DoActionGFX(_attacker.getId(), actionid);
            _attacker.broadcastPacketAll(gfx);
            _sleepTime = _attacker.getNpcTemplate().getSubMagicSpeed();
        }
        return usePoly;
    }

    /**
     * 魔法攻擊(計算命中機率)
     *
     */
    private boolean magicAttack(int idx) {
        L1SkillUse skillUse = new L1SkillUse();
        int skillid = getMobSkillTemplate().getSkillId(idx);
        int actId = getMobSkillTemplate().getActid(idx);
        int gfxId = getMobSkillTemplate().getGfxid(idx);
        boolean canUseSkill = false;
        if (skillid > 0) {
            canUseSkill = skillUse.checkUseSkill(null, skillid, _target.getId(), _target.getX(), _target.getY(), 0, L1SkillUse.TYPE_NORMAL, _attacker, actId, gfxId);
        }
        if (canUseSkill) {
            if (getMobSkillTemplate().getLeverage(idx) > 0) {
                skillUse.setLeverage(getMobSkillTemplate().getLeverage(idx));
            }
            skillUse.handleCommands(null, skillid, _target.getId(), _target.getX(), _target.getX(), 0, L1SkillUse.TYPE_NORMAL, _attacker);
            L1Skills skill = SkillsTable.get().getTemplate(skillid);
            if ((skill.getTarget().equals("attack")) && (skillid != L1SkillId.TURN_UNDEAD)) {
                _sleepTime = _attacker.getNpcTemplate().getAtkMagicSpeed();
            } else {
                _sleepTime = _attacker.getNpcTemplate().getSubMagicSpeed();
            }
            return true;
        }
        return false;
    }

    /**
     * 物理攻擊
     */
    private boolean physicalAttack(final int idx) {
        final Map<Integer, Integer> targetList = new HashMap<>();
        final int areaWidth = getMobSkillTemplate().getAreaWidth(idx);
        final int areaHeight = getMobSkillTemplate().getAreaHeight(idx);
        final int range = getMobSkillTemplate().getRange(idx);
        final int actId = getMobSkillTemplate().getActid(idx);
        final int gfxId = getMobSkillTemplate().getGfxid(idx);
        // 超過物理攻擊距離
        if (_attacker.getLocation().getTileLineDistance(_target.getLocation()) > range) {
            return false;
        }
        // 中間有障礙物的場合
        if (!_attacker.glanceCheck(_target.getX(), _target.getY())) {
            return false;
        }
        _attacker.setHeading(_attacker.targetDirection(_target.getX(), _target.getY())); // 向
        if (areaHeight > 0) {
            // 範圍攻擊
            final ArrayList<L1Object> objs = World.get().getVisibleBoxObjects(_attacker, _attacker.getHeading(), areaWidth, areaHeight);
            for (final L1Object obj : objs) {
                if (!(obj instanceof L1Character)) { // 以外場合何。
                    continue;
                }
                final L1Character cha = (L1Character) obj;
                if (cha.isDead()) { // 死對像外
                    continue;
                }
                // 狀態對像外
                if (cha instanceof L1PcInstance) {
                    if (((L1PcInstance) cha).isGhost()) {
                        continue;
                    }
                }
                // 障害物場合對像外
                if (!_attacker.glanceCheck(cha.getX(), cha.getY())) {
                    continue;
                }
                if ((_target instanceof L1PcInstance) || (_target instanceof L1SummonInstance) || (_target instanceof L1PetInstance)) {
                    // 對PC
                    if (((obj instanceof L1PcInstance) && !((L1PcInstance) obj).isGhost() && !((L1PcInstance) obj).isGmInvis()) || (obj instanceof L1SummonInstance) || (obj instanceof L1PetInstance)) {
                        targetList.put(obj.getId(), 0);
                    }
                } else {
                    // 對NPC
                    if (obj instanceof L1MonsterInstance) {
                        targetList.put(obj.getId(), 0);
                    }
                }
            }
        } else {
            // 單體攻擊
            targetList.put(_target.getId(), 0); // 追加
        }
        if (targetList.size() == 0) {
            return false;
        }
        for (int targetId : targetList.keySet()) {
            L1Object object = World.get().findObject(targetId);
            final L1AttackMode attack = new L1AttackNpc(_attacker, (L1Character) object);
            if (attack.calcHit()) {
                if (getMobSkillTemplate().getLeverage(idx) > 0) {
                    attack.setLeverage(getMobSkillTemplate().getLeverage(idx));
                }
                attack.calcDamage();
            }
            if (actId > 0) {
                attack.setActId(actId);
            }
            // 攻擊實際對行
            if (targetId == this._target.getId()) {
                if (gfxId > 0) {
                    _attacker.broadcastPacketAll(new S_SkillSound(_attacker.getId(), gfxId));
                }
                attack.action();
            }
            attack.commit();
        }
        _sleepTime = _attacker.getAtkspeed();
        return true;
    }

    /**
     * 是否符合施展條件
     *
     */
    private boolean isSkillUseble(int skillIdx) {
        boolean useble = false;
        if (getMobSkillTemplate().getTriggerHp(skillIdx) > 0) {// HP條件發動
            int hpRatio = _attacker.getCurrentHp() * 100 / _attacker.getMaxHp();
            if (hpRatio <= getMobSkillTemplate().getTriggerHp(skillIdx)) {
                useble = true;
            } else {
                return false;
            }
        }
        if (getMobSkillTemplate().getTriggerCompanionHp(skillIdx) > 0) {// 同族HP條件發動
            L1NpcInstance companionNpc = searchMinCompanionHp();
            if (companionNpc != null) {
                int hpRatio = companionNpc.getCurrentHp() * 100 / companionNpc.getMaxHp();
                if (hpRatio <= getMobSkillTemplate().getTriggerCompanionHp(skillIdx)) {
                    useble = true;
                    _target = companionNpc;
                } else {
                    return false;
                }
            }
        }
        if (getMobSkillTemplate().getTriggerRange(skillIdx) != 0) {// 發動距離不為0
            int distance = _attacker.getLocation().getTileLineDistance(_target.getLocation());
            if (getMobSkillTemplate().isTriggerDistance(skillIdx, distance)) {// 是否達到發動距離
                useble = true;
            } else {
                return false;
            }
        }
        if (getMobSkillTemplate().getTriggerCount(skillIdx) > 0) {// 次數限制不為0
            if (getSkillUseCount(skillIdx) < getMobSkillTemplate().getTriggerCount(skillIdx)) {// 沒超過使用次數限制
                useble = true;
            } else {
                return false;
            }
        }
        return useble;
    }

    /**
     * 傳回畫面內同族的怪物
     *
     */
    private L1NpcInstance searchMinCompanionHp() {
        L1NpcInstance minHpNpc = null;
        int family = _attacker.getNpcTemplate().get_family();
        for (L1Object object : World.get().getVisibleObjects(_attacker)) {
            if ((object instanceof L1NpcInstance)) {
                L1NpcInstance npc = (L1NpcInstance) object;
                if (npc.getNpcTemplate().get_family() == family) {
                    minHpNpc = npc;
                }
            }
        }
        return minHpNpc;
    }

    /**
     * 變更目標
     *
     */
    private L1Character changeTarget(final int type, final int idx) {
        L1Character target;
        switch (type) {
            case L1MobSkill.CHANGE_TARGET_ME:
                target = _attacker;
                break;
            case L1MobSkill.CHANGE_TARGET_RANDOM:
                // System.out.println("L1MobSkill.CHANGE_TARGET_RANDOM:");
                // 候補選定
                final List<L1Character> targetList = new ArrayList<>();
                for (final L1Object obj : World.get().getVisibleObjects(_attacker)) {
                    if ((obj instanceof L1PcInstance || obj instanceof L1PetInstance || obj instanceof L1SummonInstance) || (_attacker.getMapId() == 93 && obj instanceof L1MonsterInstance)) {// 怪物對戰
                        final L1Character cha = (L1Character) obj;
                        final int distance = _attacker.getLocation().getTileLineDistance(cha.getLocation());
                        // 發動範圍外的對象除外
                        if (!getMobSkillTemplate().isTriggerDistance(idx, distance)) {
                            continue;
                        }
                        // 有障礙物阻擋的對象除外
                        if (!_attacker.glanceCheck(cha.getX(), cha.getY())) {
                            continue;
                        }
                        // 不在仇恨清單的對象除外 怪物對戰例外
                        if (_attacker.getMapId() != 93) {
                            if (!_attacker.getHateList().containsKey(cha)) {
                                continue;
                            }
                        }
                        // 死亡的目標除外
                        if (cha.isDead()) {
                            continue;
                        }
                        // 如果對象是PC
                        if (cha instanceof L1PcInstance) {
                            if (((L1PcInstance) cha).isGhost()) {// 鬼魂模式排除
                                continue;
                            }
                            if (((L1PcInstance) cha).isGmInvis()) {// GM隱身排除
                                continue;
                            }
                        }
                        // 加入目標清單
                        targetList.add((L1Character) obj);
                    }
                }
                if (targetList.size() == 0) {
                    target = _target;// 目前目標
                } else {// 目標清單不為0
                    final int targetIndex = _rnd.nextInt(targetList.size());
                    target = targetList.get(targetIndex);
                }
                break;
            default:
                target = _target;
                break;
        }
        return target;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.L1MobSkillUse JD-Core Version: 0.6.2
 */