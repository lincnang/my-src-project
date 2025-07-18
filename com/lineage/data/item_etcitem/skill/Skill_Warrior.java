package com.lineage.data.item_etcitem.skill;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

import static com.lineage.server.model.skill.L1SkillId.*;

public class Skill_Warrior extends ItemExecutor {
    private Skill_Warrior() {
    }

    public static ItemExecutor get() {
        return new Skill_Warrior();
    }

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
        if (!pc.isWarrior()) {
            final S_ServerMessage msg = new S_ServerMessage(79);
            pc.sendPackets(msg);
        } else {
            final String nameId = item.getItem().getNameId();
            int skillid = 0;
            final int attribute = 9;
            int magicLv = 0;
            if (nameId.equalsIgnoreCase("戰士的印記(粉碎)")) { // 粉碎
                skillid = PASSIVE_CRASH;
                magicLv = 73;
            } else if (nameId.equalsIgnoreCase("戰士的印記(戰士: 力量)")) { // 狂暴
                skillid = PASSIVE_FURY;
                magicLv = 76;
            } else if (nameId.equalsIgnoreCase("戰士的印記(屠戮者)")) { // 迅猛雙斧
                skillid = PASSIVE_SLAYER;
                magicLv = 71;
            } else if (nameId.equalsIgnoreCase("戰士的印記(戰士: 守衛)")) { // 護甲身軀
                skillid = PASSIVE_ARMORGARDE;
                magicLv = 74;
            } else if (nameId.equalsIgnoreCase("戰士的印記(泰坦: 岩石)")) { // 泰坦：岩石
                skillid = PASSIVE_TITANROCK;
                magicLv = 76;
            } else if (nameId.equalsIgnoreCase("戰士的印記(泰坦: 子彈)")) { // 泰坦：子彈
                skillid = PASSIVE_TITANBULLET;
                magicLv = 79;
            } else if (nameId.equalsIgnoreCase("戰士的印記(泰坦: 魔法)")) { // 泰坦：魔法
                skillid = PASSIVE_TITANMAGIC;
                magicLv = 78;
            } else if (nameId.equalsIgnoreCase("戰士的印記(咆哮)")) { // 咆哮
                skillid = HOWL;
                magicLv = 72;
            } else if (nameId.equalsIgnoreCase("戰士的印記(體能強化)")) { // 體能強化
                skillid = GIGANTIC;
                magicLv = 75;
            } else if (nameId.equalsIgnoreCase("戰士的印記(力量之血)")) { // 力量之血
                skillid = Blood_strength;
                magicLv = 75;
            } else if (nameId.equalsIgnoreCase("戰士的印記(拘束移動)")) { // 拘束移動
                skillid = POWERGRIP;
                magicLv = 77;
            } else if (nameId.equalsIgnoreCase("戰士的印記(戰斧投擲)")) { // 戰斧投擲
                skillid = TOMAHAWK;
                magicLv = 73;
            } else if (nameId.equalsIgnoreCase("戰士的印記(亡命之徒)")) { // 亡命之徒
                skillid = DESPERADO;
                magicLv = 76;
            } else if (nameId.equalsIgnoreCase("\\aE戰士的印記(泰坦狂暴)")) { // 泰坦狂暴
                skillid = TITANL_RISING;
                magicLv = 76;
            } else if (nameId.equalsIgnoreCase("戰士的印記(泰坦之暈)")) { // 泰坦狂暴
                skillid = TITAN_STUN;
                magicLv = 76;
            } else if (nameId.equalsIgnoreCase("戰士的印記(佔據)")) { // 泰坦狂暴
                skillid = Warrior_Charge;
                magicLv = 76;
            } else if (nameId.equalsIgnoreCase("戰士的印記(蓋亞)")) { // 泰坦狂暴
                skillid = GAIA;
                magicLv = 76;
            }

            Skill_Check.check(pc, item, skillid, magicLv, attribute);
        }
    }
}
