package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1SkinInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.utils.L1SpawnUtil;

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
        if (!magic.calcProbabilityMagic(HAND_DARKNESS)) {
            return 0;
        }
        L1PcInstance targetPc = (L1PcInstance) cha;
        if (!targetPc.hasSkillEffect(HAND_DARKNESS) || cha.hasSkillEffect(Phantom_Blade)) {// 效果不可重疊 避免資源累積過多
            targetPc.setSkillEffect(HAND_DARKNESS, integer * 1000); // 秒數
        }
        // 注意: 21543 應該是屬於Effect動畫 它的0編碼可能是effect效果，而並非移動效果
        L1SkinInstance skin = L1SpawnUtil.spawnSkin(targetPc, 13135);
        if (skin != null) {
            skin.setMoveType(1);
        }
        targetPc.addSkin(skin, 13135);
        targetPc.beginMoveHandDarkness();
        return 50; // 造成5點傷害 ... 瞎寫
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
