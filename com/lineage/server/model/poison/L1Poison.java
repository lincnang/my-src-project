package com.lineage.server.model.poison;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_Poison;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 特別狀態
 *
 * @author dexc
 */
public abstract class L1Poison {
    /**
     * 毒性抵抗狀態
     *
     * @return true:中毒 false:不會中毒
     */
    protected static boolean isValidTarget(L1Character cha) {
        if (cha == null) {
            return false;
        }
        // 已經中毒
        if (cha.getPoison() != null) {
            return false;
        }
        if (!(cha instanceof L1PcInstance)) {
            return true;
        }
        L1PcInstance player = (L1PcInstance) cha;
        if (player.get_venom_resist() > 0) {// 裝備毒性抵抗
            return false;
        }
        //		if (player.hasSkillEffect(L1SkillId.VENOM_RESIST)) {// 毒性抵抗
        //			return false;
        //		}
        if (player.hasSkillEffect(L1SkillId.DRAGON5)) {// 生命之魔眼
            return false;
        }
        return true;
    }

    /**
     * 傳送訊息
     *
     */
    protected static void sendMessageIfPlayer(L1Character cha, int msgId) {
        if (!(cha instanceof L1PcInstance)) {
            return;
        }
        L1PcInstance pc = (L1PcInstance) cha;
        pc.sendPackets(new S_ServerMessage(msgId));
    }

    /**
     * この毒のエフェクトIDを返す。
     *
     * @return S_Poisonで使用されるエフェクトID
     * @see S_Poison#S_Poison(int, int)
     */
    public abstract int getEffectId();

    /**
     * この毒の效果を取り除く。<br>
     *
     * @see L1Character#curePoison()
     */
    public abstract void cure();
}
