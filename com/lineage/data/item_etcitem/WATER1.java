package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.*;

import static com.lineage.server.model.skill.L1SkillId.STATUS_BRAVE3;

public class WATER1 extends ItemExecutor {
    /**
     *
     */
    private WATER1() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new WATER1();
    }

    /**
     * 道具物件執行
     *
     * @param data 參數
     * @param pc   執行者
     * @param item 物件
     */
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        // 例外狀況:物件為空
        if (item == null) {
            return;
        }
        // 例外狀況:人物為空
        if (pc == null) {
            return;
        }
        if (pc.hasSkillEffect(85501)) {
            final int time = pc.getSkillEffectTimeSec(85501);
            pc.sendPackets(new S_ServerMessage("真 死亡騎士之心效果剩餘時間(秒):" + time));
            return;
        }
        // 判斷經驗加倍技能
        final int time = 600;
        pc.setSkillEffect(85501, time * 1000);
        pc.sendPacketsAll(new S_SkillSound(pc.getId(), 245));
        pc.sendPacketsAll(new S_Liquor(pc.getId(), 0x08));
        // 1065:將發生神秘的奇蹟力量。
        pc.sendPackets(new S_ServerMessage(1065));
        pc.setSkillEffect(STATUS_BRAVE3, 600 * 1000);
        pc.addMaxHp(+100);
        pc.addMaxMp(+100);
        pc.addDmgup(+5);
        pc.addHitup(+10);
        pc.addBowDmgup(+5);
        pc.addBowHitup(+10);
        pc.addSp(+5);
        pc.addAc(-10);
        pc.addMr(+10);
        pc.sendPackets(new S_SPMR(pc));
        pc.sendPackets(new S_OwnCharAttrDef(pc));
        pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
        pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
        pc.sendPackets(new S_OwnCharStatus2(pc));
        pc.sendPackets(new S_ServerMessage(4699));
    }
}
