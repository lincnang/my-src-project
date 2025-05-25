package com.lineage.server.templates;

import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1CurseParalysis;
import com.lineage.server.model.L1WeaponSkill;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.*;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.utils.RandomArrayList;

import java.util.Iterator;
import java.util.Random;

import static com.lineage.server.model.skill.L1SkillId.POWERGRIP;

public class L1BossWeapon {
    private static final Random _random = new Random();
    private final int _item_id;
    private final int _boss_lv;
    private final String _boss_name;
    private final int _success_random;
    private final int _max_use_time;
    private final String _success_msg;
    private final String _failure_msg;
    private final int _probability;
    private final boolean _isLongRange;
    private final int _fixDamage;
    private final int _randomDamage;
    private final double _doubleDmgValue;
    private final int _gfxId;
    private final boolean _gfxIdTarget;
    private final boolean _arrowType;
    private final int _effectId;
    private final int _effectTime;
    private final int _negativeId; // 新增額外負面技能類型(不受距離限制)
    private final int _negativeTime; // 新增額外負面技能時間(不受距離限制)
    private final int _attr;
    private final int _hpAbsorb;
    private final int _mpAbsorb;
    private final boolean _type_remove_weapon;
    private final int _type_remove_armor;

    public L1BossWeapon(int item_id, int boss_lv, String boss_name, int success_random, int max_use_time, String success_msg, String failure_msg, int probability, boolean isLongRange, int fixDamage, int randomDamage, double doubleDmgValue, int gfxId, boolean gfxIdTarget, boolean arrowType, int effectId, int effectTime, final int negativeId, final int negativeTime, int attr, int hpAbsorb, int mpAbsorb, boolean type_remove_weapon, int type_remove_armor) {
        this._item_id = item_id;
        this._boss_lv = boss_lv;
        this._boss_name = boss_name;
        this._success_random = success_random;
        this._max_use_time = max_use_time;
        this._success_msg = success_msg;
        this._failure_msg = failure_msg;
        this._probability = probability;
        this._isLongRange = isLongRange;
        this._fixDamage = fixDamage;
        this._randomDamage = randomDamage;
        this._doubleDmgValue = doubleDmgValue;
        this._gfxId = gfxId;
        this._gfxIdTarget = gfxIdTarget;
        this._arrowType = arrowType;
        this._effectId = effectId;
        this._effectTime = effectTime;
        _negativeId = negativeId;
        _negativeTime = negativeTime;
        this._attr = attr;
        this._hpAbsorb = hpAbsorb;
        this._mpAbsorb = mpAbsorb;
        this._type_remove_weapon = type_remove_weapon;
        this._type_remove_armor = type_remove_armor;
    }

    public static double getWeaponSkillDamage(L1PcInstance pc, L1Character cha, double damage, L1BossWeapon bossWeapon, boolean isLongRange) {
        if ((pc == null) || (cha == null) || (bossWeapon == null)) {
            return 0.0D;
        }
        int chance;
        if ((isLongRange) && (bossWeapon.isLongRange())) {
            chance = _random.nextInt(200);
        } else {
            chance = _random.nextInt(100);
        }
        if (bossWeapon.getProbability() < chance) {
            return 0.0D;
        }
        // 戰爭期間 by terry0412
        //boolean isNowWar = ServerWarExecutor.get().isNowWar();
        int gfxId = bossWeapon.getGfxId();
        if (gfxId != 0) {
            if (bossWeapon.isGfxIdTarget()) {
                if (bossWeapon.isArrowType()) {
                    S_UseAttackSkill packet = new S_UseAttackSkill(pc, cha.getId(), gfxId, cha.getX(), cha.getY(), 1, false);
                    pc.sendPacketsBossWeaponAll(packet);
                } else {
                    pc.sendPacketsBossWeaponAll(new S_SkillSound(cha.getId(), gfxId));
                }
            } else {
                pc.sendPacketsBossWeaponAll(new S_SkillSound(pc.getId(), gfxId));
            }
        }
        int effectTime = bossWeapon.getEffectTime();
        if (effectTime > 0) {
            effectTime *= 1000;
        }
        int effectId = bossWeapon.getEffectId();
        if (effectId > 0) {
            L1Character target;
            if (bossWeapon.isGfxIdTarget()) {
                target = cha;
            } else {
                target = pc;
            }
            L1SkillUse l1skilluse = new L1SkillUse();
            l1skilluse.handleCommands(pc, bossWeapon.getEffectId(), target.getId(), target.getX(), target.getY(), bossWeapon.getEffectTime(), 4);
        }
        // 新增額外負面技能類型(不受距離限制)
        int negativeTime = bossWeapon._negativeTime;
        // 新增額外負面技能類型(不受距離限制)
        final int negativeId = bossWeapon._negativeId;
        if (negativeId > 0) {
            if (cha.hasSkillEffect(L1SkillId.COUNTER_MAGIC)) {
                cha.removeSkillEffect(L1SkillId.COUNTER_MAGIC);
                int castgfx2 = SkillsTable.get().getTemplate(L1SkillId.COUNTER_MAGIC).getCastGfx2();
                cha.broadcastPacketAll(new S_SkillSound(cha.getId(), castgfx2));
                if ((cha instanceof L1PcInstance)) {
                    L1PcInstance tgpc = (L1PcInstance) cha;
                    tgpc.sendPacketsAll(new S_SkillSound(tgpc.getId(), castgfx2));
                }
                return negativeId;
            }
            switch (negativeId) {
                case 1: // 20闇盲咒術
                    if ((cha instanceof L1PcInstance)) {
                        L1PcInstance player = (L1PcInstance) cha;
                        if (player.hasSkillEffect(1012)) {
                            player.sendPackets(new S_CurseBlind(2));
                        } else {
                            player.sendPackets(new S_CurseBlind(1));
                        }
                    }
                    cha.setSkillEffect(L1SkillId.DARKNESS, negativeTime * 1000);
                    break;
                case 2: // 29緩速術
                    if (cha.getMoveSpeed() == 0) {
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance player = (L1PcInstance) cha;
                            player.sendPackets(new S_SkillHaste(player.getId(), 2, negativeTime * 1000));
                        }
                        cha.broadcastPacketAll(new S_SkillHaste(cha.getId(), 2, negativeTime * 1000));
                        cha.setMoveSpeed(2);
                        cha.setSkillEffect(29, negativeTime * 1000);
                    } else if (cha.getMoveSpeed() == 1) {
                        int skillNum = 0;
                        if (cha.hasSkillEffect(43)) {
                            skillNum = 43;
                        } else if (cha.hasSkillEffect(54)) {
                            skillNum = 54;
                        } else if (cha.hasSkillEffect(1001)) {
                            skillNum = 1001;
                        }
                        if (skillNum != 0) {
                            cha.removeSkillEffect(skillNum);
                            cha.removeSkillEffect(29);
                            cha.setMoveSpeed(0);
                        }
                    } else if (cha.getMoveSpeed() == 2) {
                        cha.setSkillEffect(29, negativeTime * 1000);
                    }
                    pc.sendPackets(new S_SkillSound(cha.getId(), 752));
                    pc.broadcastPacketAll(new S_SkillSound(cha.getId(), 752));
                    break;
                case 3: // 33木乃伊的詛咒
                    if ((!cha.hasSkillEffect(157)) && (!cha.hasSkillEffect(50))) {
                        if ((cha instanceof L1PcInstance)) {
                            L1CurseParalysis.curse(cha, 5000, negativeTime * 1000, 1);
                        } else if ((cha instanceof L1MonsterInstance)) {
                            L1CurseParalysis.curse(cha, 5000, negativeTime * 1000, 0);
                        }
                        pc.sendPackets(new S_SkillSound(cha.getId(), 746));
                        pc.broadcastPacketAll(new S_SkillSound(cha.getId(), 746));
                    }
                    break;
                case 4: // 39魔力奪取
                    int radMp = RandomArrayList.getInt(10) + 5;
                    int drainMana = radMp + (pc.getInt() / 2);
                    if (cha.getCurrentMp() < drainMana) {
                        drainMana = cha.getCurrentMp();
                        cha.setCurrentMp(0);
                    } else {
                        cha.setCurrentMp(cha.getCurrentMp() - drainMana);
                    }
                    if ((pc.getCurrentMp() + drainMana) > (int) pc.getMaxMp()) {
                        pc.setCurrentMp((int) pc.getMaxMp());
                    } else {
                        pc.setCurrentMp(pc.getCurrentMp() + drainMana);
                    }
                    pc.sendPackets(new S_SkillSound(cha.getId(), 2171));
                    pc.broadcastPacketAll(new S_SkillSound(cha.getId(), 2171));
                    break;
                case 5: // 64魔法封印
                    if ((cha instanceof L1PcInstance)) {
                        cha.setSkillEffect(64, negativeTime * 1000);
                        pc.sendPackets(new S_SkillSound(cha.getId(), 2177));
                        pc.broadcastPacketAll(new S_SkillSound(cha.getId(), 2177));
                    }
                    break;
                case 6: // 71藥水霜化術
                    if ((cha instanceof L1PcInstance)) {
                        cha.setSkillEffect(71, negativeTime * 1000);
                        pc.sendPackets(new S_SkillSound(cha.getId(), 2232));
                        pc.broadcastPacketAll(new S_SkillSound(cha.getId(), 2232));
                    }
                    break;
                case 7: // 74流星雨
                    final L1SkillUse l1skilluse = new L1SkillUse();
                    l1skilluse.handleCommands(pc, 74, cha.getId(), cha.getX(), cha.getY(), negativeTime, L1SkillUse.TYPE_GMBUFF);
                    break;
                case 8: // 87衝擊之暈
                    if (((cha instanceof L1PcInstance)) && (cha.hasSkillEffect(87))) {
                        negativeTime += cha.getSkillEffectTimeSec(87);
                    }
                    cha.setSkillEffect(87, negativeTime * 1000);
                    L1SpawnUtil.spawnEffect(81162, negativeTime, cha.getX(), cha.getY(), pc.getMapId(), pc, 0);
                    if ((cha instanceof L1PcInstance)) {
                        L1PcInstance player = (L1PcInstance) cha;
                        player.sendPackets(new S_Paralysis(5, true));
                    } else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1PetInstance))) {
                        L1NpcInstance tgnpc = (L1NpcInstance) cha;
                        tgnpc.setParalyzed(true);
                    }
                    break;
                case 9: // 173污濁之水
                    if ((cha instanceof L1PcInstance)) {
                        cha.setSkillEffect(173, negativeTime * 1000);
                        pc.sendPackets(new S_SkillSound(cha.getId(), 5830));
                        pc.broadcastPacketAll(new S_SkillSound(cha.getId(), 5830));
                    }
                    break;
                case 10: // 183護衛毀滅
                    if ((cha instanceof L1PcInstance)) {
                        L1PcInstance player = (L1PcInstance) cha;
                        if (player.hasSkillEffect(183)) {
                            player.removeSkillEffect(183);
                        }
                        player.setSkillEffect(183, negativeTime * 1000);
                        player.addAc(10);
                        player.sendPackets(new S_OwnCharStatus(player)); // 防禦更新
                        pc.sendPackets(new S_SkillSound(player.getId(), 6508));
                        pc.broadcastPacketAll(new S_SkillSound(player.getId(), 6508));
                    }
                    break;
                case 11: // 放逐-使對方無法攻擊
                    if ((cha instanceof L1PcInstance)) {
                        L1PcInstance player = (L1PcInstance) cha;
                        player.setSkillEffect(L1SkillId.ReiSkill_2, negativeTime * 1000);
                        pc.sendPackets(new S_SystemMessage("發動武器技能，使對手無法攻擊。"));
                        player.sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 1, 566));// 開啟圖示
                    }
                    break;
                case 12: // 禁錮-束縛對方
                    int grip = negativeTime;
                    if ((cha instanceof L1PcInstance) && cha.hasSkillEffect(POWERGRIP)) {
                        grip += cha.getSkillEffectTimeSec(POWERGRIP);
                    }
                    if (grip > 6) {
                        grip = 6;
                    }
                    cha.setSkillEffect(POWERGRIP, grip * 1000);
                    L1SpawnUtil.spawnEffect(93004, grip, cha.getX(), cha.getY(), cha.getMapId(), cha, 0);
                    if (cha instanceof L1PcInstance) {
                        final L1PcInstance player = (L1PcInstance) cha;
                        player.sendPackets(new S_Paralysis(S_Paralysis.TYPE_POWERGRIP, true));
                    } else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) {
                        final L1NpcInstance npc = (L1NpcInstance) cha;
                        npc.setGripped(true);
                    }
                    break;
                case 13: // 破甲-使對方沒有任何減傷(PVP減傷減半)
                    if ((cha instanceof L1PcInstance)) {
                        L1PcInstance player = (L1PcInstance) cha;
                        player.setSkillEffect(L1SkillId.negativeId13, negativeTime * 1000);
                        pc.sendPackets(new S_SystemMessage("發動武器技能，使對方沒有任何減傷(PVP減傷減半)。"));
                        player.sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 1, 561));// 開啟圖示
                    }
                    break;
                case 14: // 滅魔-使對方失明(並扣除對方總魔力)
                    if ((cha instanceof L1PcInstance)) {
                        L1PcInstance player = (L1PcInstance) cha;
                        if (player.hasSkillEffect(1012)) {
                            player.sendPackets(new S_CurseBlind(2));
                        } else {
                            player.sendPackets(new S_CurseBlind(1));
                        }
                    }
                    cha.setSkillEffect(L1SkillId.DARKNESS, 5 * 1000);
                    //int drainMp = (cha.getCurrentMp() / 100) * negativeTime;
                    int drainMp = (cha.getMaxMp() / 100) * negativeTime;
                    if (cha.getCurrentMp() < drainMp) {
                        drainMp = cha.getCurrentMp();
                        cha.setCurrentMp(0);
                    } else {
                        cha.setCurrentMp(cha.getCurrentMp() - drainMp);
                    }
                    if ((pc.getCurrentMp() + drainMp) > (int) pc.getMaxMp()) {
                        pc.setCurrentMp((int) pc.getMaxMp());
                    } else {
                        pc.setCurrentMp(pc.getCurrentMp() + drainMp);
                    }
                    pc.sendPackets(new S_SkillSound(cha.getId(), 2171));
                    pc.broadcastPacketAll(new S_SkillSound(cha.getId(), 2171));
                    break;
                case 15: // 虛弱-使對方攻擊減半
                    if ((cha instanceof L1PcInstance)) {
                        L1PcInstance player = (L1PcInstance) cha;
                        player.setSkillEffect(L1SkillId.negativeId15, negativeTime * 1000);
                        pc.sendPackets(new S_SystemMessage("發動武器技能，使對方攻擊減半。"));
                        player.sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 1, 488));// 開啟圖示
                    }
                    break;
                case 16: // 收割-使對方轉移最大生命給自己
                    int drainHp = (cha.getCurrentHp() / 100) * negativeTime;
                    //int drainHp = (cha.getMaxHp() / 100) * negativeTime;
                    if (cha.getCurrentHp() < drainHp) {
                        drainHp = cha.getCurrentHp();
                        cha.setCurrentHp(0);
                    } else {
                        cha.setCurrentHp(cha.getCurrentHp() - drainHp);
                    }
                    if ((pc.getCurrentHp() + drainHp) > (int) pc.getMaxHp()) {
                        pc.setCurrentHp((int) pc.getMaxHp());
                    } else {
                        pc.setCurrentHp(pc.getCurrentHp() + drainHp);
                    }
                    pc.sendPackets(new S_SkillSound(cha.getId(), 236));
                    pc.broadcastPacketAll(new S_SkillSound(cha.getId(), 236));
                    break;
            }
        }
        double fixDamage = bossWeapon.getFixDamage();
        int randomDamage = bossWeapon.getRandomDamage();
        if (randomDamage > 0) {
            fixDamage += _random.nextInt(randomDamage);
        }
        double doubleDmgValue = bossWeapon.getDoubleDmgValue();
        if (doubleDmgValue > 0.0D) {
            fixDamage += damage * doubleDmgValue;
        }
        if (bossWeapon.getHpAbsorb() > 0) {
            pc.setCurrentHp(pc.getCurrentHp() + bossWeapon.getHpAbsorb());
        }
        if ((bossWeapon.getMpAbsorb() > 0) && (cha.getCurrentMp() > 0)) {
            cha.setCurrentMp(Math.max(cha.getCurrentMp() - bossWeapon.getMpAbsorb(), 0));
            pc.setCurrentMp(pc.getCurrentMp() + bossWeapon.getMpAbsorb());
        }
        if ((cha instanceof L1PcInstance)) {
            L1PcInstance targetPc = (L1PcInstance) cha;
            if ((bossWeapon.gettype_remove_weapon()) && (targetPc != null) && (targetPc.getWeapon() != null)) {
                targetPc.getInventory().setEquipped(targetPc.getWeapon(), false, false, false);
                targetPc.sendPackets(new S_ServerMessage(1027));
            }
            if (bossWeapon.gettype_remove_armor() > 0 && targetPc != null) {
                int counter = _random.nextInt(bossWeapon.gettype_remove_armor()) + 1;
                StringBuffer sbr = new StringBuffer();
                Iterator<L1ItemInstance> iterator2 = targetPc.getInventory().getItems().iterator();
                while (iterator2.hasNext()) {
                    L1ItemInstance item = (L1ItemInstance) iterator2.next();
                    if (item.getItem().getType2() != 2 || !item.isEquipped()) {
                        continue;
                    }
                    targetPc.getInventory().setEquipped(item, false, false, false);
                    sbr.append("[").append(item.getNumberedName(1L, false)).append("]");
                    if (--counter <= 0) {
                        break;
                    }
                }
                if (sbr.length() > 0) {
                    targetPc.sendPackets(new S_SystemMessage("以下裝備被對方卸除:" + sbr.toString()));
                    pc.sendPackets(new S_SystemMessage("成功卸除對方以下裝備:" + sbr.toString()));
                }
            }
        }
        return L1WeaponSkill.calcDamageReduction(pc, cha, fixDamage, bossWeapon.getAttr());
    }

    public final int getItemId() {
        return this._item_id;
    }

    public int get_boss_lv() {
        return this._boss_lv;
    }

    public final String getBossName() {
        return this._boss_name;
    }

    public final int getSuccessRandom() {
        return this._success_random;
    }

    public final int getMaxUseTime() {
        return this._max_use_time;
    }

    public final String getSuccessMsg() {
        return this._success_msg;
    }

    public final String getFailureMsg() {
        return this._failure_msg;
    }

    public final int getProbability() {
        return this._probability;
    }

    public final boolean isLongRange() {
        return this._isLongRange;
    }

    public final int getFixDamage() {
        return this._fixDamage;
    }

    public final int getRandomDamage() {
        return this._randomDamage;
    }

    public final double getDoubleDmgValue() {
        return this._doubleDmgValue;
    }

    public final int getGfxId() {
        return this._gfxId;
    }

    public final boolean isGfxIdTarget() {
        return this._gfxIdTarget;
    }

    public final boolean isArrowType() {
        return this._arrowType;
    }

    public final int getEffectId() {
        return this._effectId;
    }

    public final int getEffectTime() {
        return this._effectTime;
    }

    /**
     * 新增額外負面技能類型(不受距離限制)
     *
     */
    public final int getNegativeId() {
        return _negativeId;
    }

    /**
     * 新增額外負面技能時間(不受距離限制)
     *
     */
    public final int getNegativeTime() {
        return _negativeTime;
    }

    public final int getAttr() {
        return this._attr;
    }

    public final int getHpAbsorb() {
        return this._hpAbsorb;
    }

    public final int getMpAbsorb() {
        return this._mpAbsorb;
    }

    public final boolean gettype_remove_weapon() {
        return this._type_remove_weapon;
    }

    public final int gettype_remove_armor() {
        return this._type_remove_armor;
    }
}
