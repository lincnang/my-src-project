package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1SkinInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.templates.L1Skills;

import java.util.Random;

import static com.lineage.server.model.skill.L1SkillId.HAND_DARKNESS;
import static com.lineage.server.model.skill.L1SkillId.Phantom_Blade;

/**
 * 黑暗之手 给予负面状态 移动方向变更为随机<br>
 *
 * @author 圣子默默
 */
public class HAND_DARKNESS extends SkillMode {
    private static final Random _random = new Random();

    public HAND_DARKNESS() {
    }

    @Override
    public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer) throws Exception {
        // 獲取技能資訊
        L1Skills skill = SkillsTable.get().getTemplate(HAND_DARKNESS);
        if (skill == null) {
            return 0;
        }

        if (cha.hasSkillEffect(HAND_DARKNESS) || cha.hasSkillEffect(Phantom_Blade)) {
            return 0;// 效果不可重疊 避免資源累積過多
        }
        if (!(cha instanceof L1PcInstance)) { // 僅可對玩家使用
            return 0;
        }
        if (cha.isSafetyZone()) {
            srcpc.sendPackets(new com.lineage.server.serverpackets.S_ServerMessage("安全區域無法施放",17));
            return 0;
        }
        L1PcInstance targetPc = (L1PcInstance) cha;
        // 使用技能表中的命中率計算
        boolean isSuccess = false;
        if (skill != null) {
            int probability = 0;
            // 使用 skills 表中的 probability_dice 和 probability_value 計算命中率
            int dice = skill.getProbabilityDice();
            int diceCount = srcpc.getMagicBonus() + srcpc.getMagicLevel();
            diceCount = Math.max(diceCount, 1);

            // 擲骰子計算基礎命中率
            for (int i = 0; i < diceCount; i++) {
                if (dice > 0) {
                    probability += (_random.nextInt(dice) + 1);
                }
            }

            // 加上基礎命中率值
            probability += skill.getProbabilityValue();

            // 加上施法者的魔法命中
            probability += srcpc.getOriginalMagicHit();

            // 減去目標的魔防
            probability -= (targetPc.getMr() / 10);

            // 確保命中率在合理範圍內
            probability = Math.max(0, Math.min(100, probability));

            // 根據最終命中率決定是否成功
            isSuccess = (_random.nextInt(100) < probability);
        } else {
            // 如果無法取得技能資訊，使用原有的計算方式
            isSuccess = magic.calcProbabilityMagic(HAND_DARKNESS);
        }

        if (!isSuccess) {
            return 0;
        }
        if (!targetPc.hasSkillEffect(HAND_DARKNESS) || cha.hasSkillEffect(Phantom_Blade)) {// 效果不可重疊 避免資源累積過多
            // 使用技能表中的持續時間，如果為0則使用預設值
            int duration = skill.getBuffDuration() > 0 ? skill.getBuffDuration() : integer;
            targetPc.setSkillEffect(HAND_DARKNESS, duration * 1000); // 秒數
        }
        // 注意: 21543 應該是屬於Effect動畫 它的0編碼可能是effect效果，而並非移動效果
        L1SkinInstance skin = L1SpawnUtil.spawnSkin(targetPc, 13135);
        if (skin != null) {
            skin.setMoveType(1);
        }
        targetPc.addSkin(skin, 13135);
        targetPc.beginMoveHandDarkness();
        // 使用技能表中的傷害值
        return skill.getDamageValue() > 0 ? skill.getDamageValue() : 50; // 預設造成5點傷害
    }

    @Override
    public int start(final L1NpcInstance npc, final L1Character cha, final L1Magic magic, final int integer) throws Exception {
        return 0;
    }

    @Override
    public void start(final L1PcInstance srcpc, final Object obj) throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public void stop(final L1Character cha) throws Exception {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.stopMoveHandDarkness();
            pc.flushedLocation();
            if (pc.getSkin(13135) != null) {
                pc.getSkin(13135).deleteMe();
                pc.removeSkin(13135);
            }
        }
    }
}
