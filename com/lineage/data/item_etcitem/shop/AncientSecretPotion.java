package com.lineage.data.item_etcitem.shop;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.*;

import static com.lineage.server.model.skill.L1SkillId.Ancient_Secretn;
import static com.lineage.server.model.skill.L1SkillId.STATUS_BRAVE3;

/**
 * 古老秘藥
 * <p>
 * 體力上限+100 魔力上限+100
 * 近距離命中率+10 近距離攻擊力+5
 * 遠距離命中率+10 遠距離攻擊力+5
 * 魔攻+5 防禦-10 魔法防禦+10 三段加速
 * 時效10分鐘
 */
public class AncientSecretPotion extends ItemExecutor {
    /**
     *
     */
    private AncientSecretPotion() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new AncientSecretPotion();
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
        //if (pc.hasSkillEffect(Ancient_Secretn)) {
        //pc.sendPackets(new S_ServerMessage("古老秘藥效果時間尚有" + pc.getSkillEffectTimeSec(Ancient_Secretn) + "秒。"));
        //return;
        //}
        if (L1BuffUtil.stopPotion(pc)) {
            if (pc.hasSkillEffect(STATUS_BRAVE3)) {
                pc.killSkillEffectTimer(STATUS_BRAVE3);
            }
            if (pc.hasSkillEffect(Ancient_Secretn)) {
                pc.removeSkillEffect(Ancient_Secretn);
            }
            pc.getInventory().removeItem(item, 1);
            L1BuffUtil.cancelAbsoluteBarrier(pc); // 解除魔法技能絕對屏障
            pc.addMaxHp(100);   // 體力上限+100
            pc.addMaxMp(100);   // 魔力上限+100
            pc.addHitup(10);    // 近距離命中+10
            pc.addDmgup(5);     // 近距離傷害+5
            pc.addBowHitup(10); // 遠距離命中+10
            pc.addBowDmgup(5);  // 遠距離傷害+5
            pc.addSp(5);        // 魔攻+5
            pc.addAc(-10);      // 防禦-10
            pc.addMr(10);       // 魔法防禦+10
            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            if (pc.isInParty()) { // 組隊中更新血條
                pc.getParty().updateMiniHP(pc);
            }
            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp())); // 魔力更新
            pc.sendPackets(new S_OwnCharStatus(pc)); // 防禦更新
            pc.sendPackets(new S_SPMR(pc)); // 魔攻魔防更新
            pc.sendPacketsAll(new S_Liquor(pc.getId(), 0x08)); // 巧克力蛋糕效果(速度增加1.15)
            pc.sendPackets(new S_ServerMessage(1065)); // 1065:將發生神秘的奇跡力量。
            pc.sendPacketsX8(new S_SkillSound(pc.getId(), 8031));
            pc.setSkillEffect(Ancient_Secretn, 600 * 1000);
            pc.setSkillEffect(STATUS_BRAVE3, 600 * 1000);
        }
    }
}