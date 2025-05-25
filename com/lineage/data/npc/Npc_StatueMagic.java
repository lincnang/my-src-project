package com.lineage.data.npc;

import com.lineage.config.ConfigOtherSet2;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillBrave;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;

import static com.lineage.server.model.skill.L1SkillId.STATUS_BRAVE;
import static com.lineage.server.model.skill.L1SkillId.StatueMagic;

/**
 * 潘朵拉銅像加狀態
 */
public class Npc_StatueMagic extends NpcExecutor {
    private Npc_StatueMagic() {
        // TODO Auto-generated constructor stub
    }

    public static NpcExecutor get() {
        return new Npc_StatueMagic();
    }

    @Override
    public int type() {
        return 1;
    }

    @Override
    public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
        final int StatueMagicDelayTime = 2; // 潘朵拉銅像延遲加狀態時間 (分)
        if (pc.hasSkillEffect(StatueMagic)) { // 判斷有延遲狀態不讓加
            pc.sendPackets(new S_SystemMessage(StatueMagicDelayTime + "分鐘內只能加1次，請稍後再來。"));
            return;
        }
        if (pc.getMapId() != 7783) { // 新手地圖不扣錢
            final int itemid = ConfigOtherSet2.STATUE_MAGIC_ITEMID; // 扣除道具
            final int count = ConfigOtherSet2.STATUE_MAGIC_ITEMCOUNT; // 扣除道具數量
            if (itemid > 0) {
                final L1ItemInstance item = pc.getInventory().findItemIdNoEq(itemid);
                final L1Item tgItem = ItemTable.get().getTemplate(itemid);
                if (!pc.getInventory().consumeItem(itemid, count)) {
                    // 337 \f1%0不足%s。
                    pc.sendPackets(new S_ServerMessage(337, tgItem.getNameId() + "(" + (count - (item == null ? 0 : item.getCount())) + ")"));
                    return;
                }
            }
        }
        // 指定輔助魔法ID(只能設定一些 基本的好用的法師魔法)
        final int[] allBuffSkill = ConfigOtherSet2.STATUE_MAGIC_SKILLID;
        //final int[] allBuffSkill = { 14,26,42,43,79,115,158 }; // 14負重強化 26通暢氣脈術 42體魄強健術 43加速術 79靈魂昇華 115閃亮之盾 158生命之泉
        // 指定輔助魔法時間(隨意設幾組都行,需使用逗號分開[對造上面設定排列,時間:秒])
        final int[] allBuffTime = ConfigOtherSet2.STATUE_MAGIC_SKILLTIME;
        //final int[] allBuffTime = { 1800,1200,1200,1200,1200,640,320 };
        for (int i = 0; i < allBuffSkill.length; i++) {
            final int skillid = allBuffSkill[i];
            final int time = allBuffTime[i];
            this.startSkill(pc, npc, skillid, time);
        }
        // 解除魔法技能絕對屏障
        L1BuffUtil.cancelAbsoluteBarrier(pc);
        // 勇水
        L1BuffUtil.braveStart(pc);// 勇敢效果 抵銷對應技能
        pc.sendPackets(new S_SkillBrave(pc.getId(), 1, 600));
        pc.broadcastPacketAll(new S_SkillBrave(pc.getId(), 1, 0));
        pc.sendPackets(new S_SkillSound(pc.getId(), 751));
        pc.broadcastPacketAll(new S_SkillSound(pc.getId(), 751));
        pc.setSkillEffect(STATUS_BRAVE, 600 * 1000);
        pc.setBraveSpeed(1);
        pc.setDrink(false);// 醉酒狀態解除
        if (ConfigOtherSet2.STATUE_MAGIC_MAXHP) { // 補滿血開關
            pc.setCurrentHp(pc.getMaxHp()); // 補滿血
        }
        pc.setSkillEffect(StatueMagic, StatueMagicDelayTime * 60 * 1000); // 給予潘朵拉銅像延遲狀態
    }

    private void startSkill(final L1PcInstance pc, L1NpcInstance npc, int skillid, int time) {
        final int objid = pc.getId();
        final int x = pc.getX();
        final int y = pc.getY();
        final L1SkillUse skillUse = new L1SkillUse();
        skillUse.handleCommands(pc, skillid, objid, x, y, time, L1SkillUse.TYPE_GMBUFF);
    }
}
