package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_SystemMessage;

import java.util.Random;

/**
 * 強力治癒藥水40011<br>
 * 濃縮強力體力恢復劑40020
 */
public class Power_Reply_potion2 extends ItemExecutor {
    private static final Random _random = new Random();

    public static ItemExecutor get() {
        return new Power_Reply_potion2();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int ran = 20 + _random.nextInt(10) + 1;
        if (pc.hasSkillEffect(7911)) {
            pc.killSkillEffectTimer(7911);
            pc.sendPackets(new S_SystemMessage("外掛偵測驗證藥水成功"));
            pc.setSkillEffect(7905, ran * 60 * 1000);
            String msg1 = "AI驗證系統：";
            String msg2 = "感謝您的配合！";
            String msg3 = "您現在可以關閉視窗。";
            String msg[] = {msg1, msg2, msg3};
            pc.sendPackets(new S_NPCTalkReturn(pc, "a3_11", msg));
            pc.killSkillEffectTimer(7910);
            pc.killSkillEffectTimer(7911);
            pc.killSkillEffectTimer(7912);
            pc.killSkillEffectTimer(7913);
            pc.killSkillEffectTimer(7914);
            pc.killSkillEffectTimer(7902);
            pc.setATK_ai(false);
        }
        //	pc.sendPacketsX8(new S_SkillSound(pc.getId(), 194));
        pc.getInventory().removeItem(item, 1L);
    }
}