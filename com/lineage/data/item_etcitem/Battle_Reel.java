package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

import static com.lineage.server.model.skill.L1SkillId.EFFECT_ENCHANTING_BATTLE;

public class Battle_Reel extends ItemExecutor {
    public static ItemExecutor get() {
        return new Battle_Reel();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (item == null) {
            return;
        }
        if (pc == null) {
            return;
        }
        if (!pc.hasSkillEffect(EFFECT_ENCHANTING_BATTLE)) {// 尚未有此效果
            pc.setSkillEffect(EFFECT_ENCHANTING_BATTLE, 3600 * 1000);
            pc.getInventory().removeItem(item, 1L);
            pc.addHitup(3); // 攻擊成功 +3
            pc.addDmgup(3); // 額外攻擊點數 +3
            pc.addBowHitup(3); // 遠距離命中率 +3
            pc.addBowDmgup(3); // 遠距離攻擊力 +3
            pc.addSp(3); // 魔攻 +3
            pc.sendPackets(new S_SPMR(pc));
            pc.sendPacketsAll(new S_SkillSound(pc.getId(), 6995));
        } else {
            int time = pc.getSkillEffectTimeSec(EFFECT_ENCHANTING_BATTLE);
            pc.sendPackets(new S_ServerMessage("戰鬥強化卷軸 剩餘時間(秒):" + time));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Scale JD-Core Version: 0.6.2
 */