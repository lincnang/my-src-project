package com.lineage.server.clientpackets;

import com.add.NewAuto.AutoAttackUpdate;
import com.lineage.config.ConfigAutoAll;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.ActionCodes;
import com.lineage.server.clientpackets.AcceleratorChecker.ACT_TYPE;
import com.lineage.server.datatables.PolyTable;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.model.skill.L1AutoRecycleSkill;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.serverpackets.ability.S_WeightStatus;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.lineage.server.model.skill.L1SkillId.*;

/**
 * 要求使用技能
 *
 * @author daien
 */
public class C_UseSkill extends ClientBasePacket {
    private static final Log _log = LogFactory.getLog(C_UseSkill.class);
    /*
     * public C_UseSkill() { } public C_UseSkill(final byte[] abyte0, final
     * ClientExecutor client) { super(abyte0); try { this.start(abyte0, client);
     * } catch (final Exception e) { _log.error(e.getLocalizedMessage(), e); } }
     */
    // 隱身狀態下可用的魔法
    private static final int[] _cast_with_invis = {/* HEAL,*/ LIGHT, SHIELD, TELEPORT,/* HOLY_WEAPON,*/ CURE_POISON,/* ENCHANT_WEAPON, */DETECTION, 14, 19, 21, 26, 31, 32, 35, 37, 48, 49, 50, 52, 55, 57, 61, 63, 72, IMMUNE_TO_HARM2, REDUCTION_ARMOR, BOUNCE_ATTACK, SOLID_CARRIAGE, COUNTER_BARRIER, 97, 98, 99, 100, 101, 102, 104, 105, 106, 107, 109, 110, 111, 113, 114, 115, 116, 117, 118, 129, 130, 131, 133, 134, 137, 138, 146, 147, 148, 149, 150, 151, 155, 156, 158, 159, 163, 164, 165, 166, 168, 169, 170, 171, SOUL_OF_FLAME, ADDITIONAL_FIRE, DRAGON_SKIN, AWAKEN_ANTHARAS, AWAKEN_FAFURION, AWAKEN_VALAKAS, MIRROR_IMAGE, ILLUSION_OGRE, ILLUSION_LICH, PATIENCE, ILLUSION_DIA_GOLEM, INSIGHT, ASSASSIN, // 黑妖新技能 暗殺者
            ILLUSION_AVATAR};

    @Override
    public void start(final byte[] decrypt, final ClientExecutor client) {
        try {
            // 資料載入
            read(decrypt);
            final L1PcInstance pc = client.getActiveChar();
            if (pc == null) { // 不存在
                return;
            }
            if (pc.isDead()) { // 死亡
                return;
            }
            if (pc.isTeleport()) { // 傳送中
                return;
            }
            if (pc.isPrivateShop()) {// 商店村模式
                return;
            }
            if (pc.getInventory().getWeight240() >= 197) { // 重量過重
                pc.setTeleport(false);
                pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                // 316 \f1你攜帶太多物品，因此無法使用法術。
                pc.sendPackets(new S_ServerMessage(316));
                return;
            }
            if (!pc.getMap().isUsableSkill()) {
                pc.setTeleport(false);
                pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                // 563 \f1你無法在這個地方使用。
                pc.sendPackets(new S_ServerMessage(563));
                return;
            }
            // 加載封包內容
            final int row = readC();
            final int column = readC();
            // 計算使用的技能編號(>> 1: 除) (<< 1: 乘) 3=*8
            int skillId = (row << 3) + column + 1;
            if (skillId == 5) {
                skillId = 4;
            }
              if (skillId > 239) {
                return;
            }
            if (skillId < 0) {
                return;
            }

            // 技能延遲狀態
            if (pc.isSkillDelay()) {
                if (skillId == 4) {
                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                }
                return;
            }

                        boolean isError = false;
            // 變身限制
            final int polyId = pc.getTempCharGfx();
            final L1PolyMorph poly = PolyTable.get().getTemplate(polyId);
            // 該變身無法使用魔法
            if ((poly != null) && !poly.canUseSkill()) {
                isError = true;
            }

            // 下列狀態無法使用魔法(魔法封印相關 - 保留專門的魔法封印檢查)
            if (pc.hasSkillEffect(SILENCE) && !isError) {
                isError = true;
            }
            if (pc.hasSkillEffect(AREA_OF_SILENCE) && !isError) {
                isError = true;
            }
            
            final boolean isCurePoisonSkill = (skillId == CURE_POISON);

            // 使用統一控制狀態檢查，允許沉默毒施放解毒術
            if (!isError) {
                if (pc.isInAnyControlState(!isCurePoisonSkill)) {
                    isError = true;
                }
            }
            if (isError) {
                pc.setTeleport(false);
                pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                // 285 \f1在此狀態下無法使用魔法。
                pc.sendPackets(new S_ServerMessage(285));
                return;
            }
            // 所有改被動的技能 都在這里加一遍比較靠譜
            if (skillId == MASS_TELEPORT) { // 集傳改意志
                return;
            }
            if (skillId == HOLY_WALK2) { // 神聖迅猛
                return;
            }
            if (skillId == COUNTER_BARRIER_VETERAN) { // 反擊屏障:專家86
                return;
            }
            // 隱身狀態可用魔法限制
            if (pc.isInvisble() || pc.isInvisDelay()) {
                if (!isInvisUsableSkill(skillId)) {
                    // 1003：透明狀態無法使用的魔法。
                    pc.sendPackets(new S_ServerMessage(1003));
                    return;
                }
            }
            // 技能合法判斷
            if (!pc.isSkillMastery(skillId)) {
                // 2025-12-19 調試：記錄未學習的技能
                if (skillId == TORNADO) {
                    _log.info("玩家 " + pc.getName() + " 嘗試施放黑暗之星(TORNADO, ID:" + skillId + ")，但未學習此技能");
                    pc.sendPackets(new S_SystemMessage("\\aE您還未學習黑暗之星技能"));
                }
                return;
            }
            // 城戰旗幟內 - 技能魔法限制
            final int castleId = L1CastleLocation.getCastleIdByArea(pc);
            if (castleId != 0) {
                if (ServerWarExecutor.get().isNowWar(castleId)) {
                    switch (skillId) {
                        //                        case ICE_LANCE:// 冰矛圍籬
                        case SUMMON_MONSTER:// 召喚術
                        case 78:// 絕對屏障
                        case 157:// 大地屏障
                            return;
                    }
                }
            }
            // 開啟自動魂體後是否可以手動魂體
            if (!ConfigAutoAll.AutoIsBloodyForMent && (skillId == BODY_TO_MIND || skillId == BLOODY_SOUL)) {
                if (pc.isAutoHpAll() && pc.isAutoMp2()) {
                    pc.sendPackets(new S_SystemMessage("\\aE開啟自動魂體時無法手動魂體"));
                    return;
                }
            }
            // 檢查地圖使用權
            CheckUtil.isUserMap(pc);
            String charName = null;
            // String message = null;
            int targetId = 0;
            int targetX = 0;
            int targetY = 0;
            int result;
            // FIXME 判斷有向及無向的魔法
            if (SkillsTable.get().getTemplate(skillId).getActionId() == ActionCodes.ACTION_SkillAttack) {
                result = pc.speed_Attack().checkInterval(ACT_TYPE.SPELL_DIR);
            } else {
                result = pc.speed_Attack().checkInterval(ACT_TYPE.SPELL_NODIR);
            }
            if (result == AcceleratorChecker.R_DISPOSED && skillId != L1SkillId.TELEPORT) {
                //_log.info("魔法異常: " + pc.getName() + " 變身編號:" + polyId);
                return;
            }   //src042
            if (skillId != 5) {
                if (pc.IsAttackSkill()) {
                    if (AutoAttackUpdate.getInstance().isAttackSkill(skillId)) {
                        if (!pc.isAttackSkillList(skillId)) {
                            if (pc.AttackSkillSize() < 5) {
                                pc.setAttackSkillList(skillId);
                                final L1Skills skill = SkillsTable.get().getTemplate(skillId);
                                String name = skill.getName();
                                pc.sendPackets(new S_SystemMessage("成功將[" + name + "]登錄至自動施放陣列內"));
                                AutoAttackUpdate.getInstance().getSkillInfo(pc);
                            } else {
                                pc.sendPackets(new S_SystemMessage("內掛選單內可登錄技能最多5各"));
                            }
                        } else {
                            pc.sendPackets(new S_SystemMessage("此技能已登錄於內掛施放技能選單內"));
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("此技能無法登錄 請重新點選登錄Attack技能"));
                    }
                    pc.setAttackSkill(false);
                    return;
                }
            }
            if (decrypt.length > 4) {// 可選擇對像
                switch (skillId) {
                    /*
                    case 1160:// 呼喚盟友
                    case 1180:// 援護盟友
                        charName = readS();
                        break;
                        */
                    case SUMMON_MONSTER:
                        if (pc.getInventory().checkEquipped(20284) || pc.getInventory().checkEquipped(120284)) { // 有裝備召喚戒指
                            int summonId = readD();
                            pc.setSummonId(summonId);
                        } else {
                            targetId = readD();
                        }
                        break;
                    case 113:// 精準目標 1600
                        targetId = readD();
                        targetX = readH();
                        targetY = readH();
                        pc.setText(readS());
                        break;
                    case 4:// 指定傳送 1100
                        if (!L1BuffUtil.getUseSkillTeleport(pc)) {
                            pc.setTeleport(false);
                            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                            return;
                        }
                        targetId = readD();
                        targetX = readH();
                        targetY = readH();
                        break;
                        /*
                    case MASS_TELEPORT:// 集體傳送術 1200
                        try {
                            // 日版記憶座標
                            targetId = readH();
                            targetX = readH();
                            targetY = readH();
                        } catch (final Exception e) {
                        }
                        break;
                    case FIRE_WALL:// 火牢 1100
//                    case LIFE_STREAM:// 治癒能量風暴 1100
                        targetX = readH();
                        targetY = readH();
                        break;
                        */
                    case 87:// shock stun
                        if ((pc.getWeapon() == null) || (pc.getWeapon().getItem().getType() != 3)) {
                            return;
                        }
                        targetId = readD();
                        targetX = readH();
                        targetY = readH();
                        break;
                    default:
                        targetId = readD();
                        targetX = readH();
                        targetY = readH();
                        break;
                }
            } else {
                switch (skillId) {
                    case 91:// Counter Barrier
                        if ((pc.getWeapon() == null) || (pc.getWeapon().getItem().getType() != 3)) {
                            return;
                        }
                        break;
                }
            }
            // 絕對屏障解除
            if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
                pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
                pc.startHpRegeneration();
                pc.startMpRegeneration();
            }
            // 冥想術解除 - 已改為BUFF型，不再自動解除
            // pc.removeSkillEffect(MEDITATION);
            // pc.killSkillEffectTimer(MEDITATION);
            try {
                // 呼喚盟友/援護盟友
                //				if ((skillId == 116) || (skillId == 118)) {
                if (skillId == 1180) {
                    if (charName.isEmpty()) {
                        // 輸入名稱為空
                        return;
                    }
                    final L1PcInstance target = World.get().getPlayer(charName);
                    if (target == null) {
                        // 73 \f1%0%d 不在線上。
                        pc.sendPackets(new S_ServerMessage(73, charName));
                        return;
                    }
                    // 無法攻擊/使用道具/技能/回城的狀態 XXX
                    /*
                     * if (target.isParalyzedX()) { return; }
                     */
                    if (pc.getClanid() != target.getClanid()) {
                        // 您只能邀請您血盟中的成員。
                        pc.sendPackets(new S_ServerMessage(414));
                        return;
                    }
                    targetId = target.getId();
                    if (skillId == 116) {// 呼喚盟友
                        // 移動連續同員場合、向前回向
                        final int callClanId = pc.getCallClanId();
                        if ((callClanId == 0) || (callClanId != targetId)) {
                            pc.setCallClanId(targetId);
                            pc.setCallClanHeading(pc.getHeading());
                        }
                    }
                }
                final L1SkillUse skilluse = new L1SkillUse();
                skilluse.handleCommands(pc, skillId, targetId, targetX, targetY, 0, L1SkillUse.TYPE_NORMAL);
                
                // ===== 自動循環技能系統 =====
                // 只處理配置檔 other.properties 中 AutoRecycleSkills 列表內的技能
                if (com.lineage.config.ConfigOther.AUTO_RECYCLE_SKILLS != null) {
                    for (int autoSkillId : com.lineage.config.ConfigOther.AUTO_RECYCLE_SKILLS) {
                        if (skillId == autoSkillId) {
                            L1Skills skill = SkillsTable.get().getTemplate(skillId);
                            if (skill != null) {
                                int duration = skill.getBuffDuration() * 1000;
                                L1AutoRecycleSkill.toggleAutoRecycle(pc, skillId, duration);
                            }
                            break;
                        }
                    }
                }
                // ===== 自動循環技能系統結束 =====
                
                if (skillId == L1SkillId.DECREASE_WEIGHT || skillId == L1SkillId.JOY_OF_PAIN) {
                    // XXX 7.6 重量程度資訊
                    pc.sendPackets(new S_WeightStatus(pc.getInventory().getWeight() * 100 / (int) pc.getMaxWeight(), pc.getInventory().getWeight(), (int) pc.getMaxWeight()));
                }
            } catch (final Exception e) {
                /*
                 * OutErrorMsg.put(this.getClass().getSimpleName(),
                 * "檢查 C_UseSkill 程式執行位置(核心管理者參考) SkillId: " + skillId, e);
                 */
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            over();
        }
    }

    /**
     * 該技能可否在隱身狀態使用
     *
     * @param useSkillid 技能編號
     * @return true:可 false:不可
     */
    private boolean isInvisUsableSkill(final int useSkillid) {
        for (final int skillId : _cast_with_invis) {
            if (skillId == useSkillid) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}