package com.lineage.data.item_etcitem.event;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_PacketBoxCooking;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

public class Exp70 extends ItemExecutor {
    private int _Time;

    public static ItemExecutor get() {
        return new Exp70();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (item == null) {
            return;
        }
        if (pc == null) {
            return;
        }
        if (L1BuffUtil.cancelExpSkill(pc)) {
            pc.setSkillEffect(6679, _Time * 1000);
            pc.getInventory().removeItem(item, 1L);
            pc.sendPackets(new S_ServerMessage("經驗值提升700%(" + _Time + "秒)"));
            pc.sendPackets(new S_SkillSound(pc.getId(), 750));
            // 狩獵的經驗職將會增加
            pc.sendPackets(new S_PacketBoxCooking(pc, 32, _Time));
        }
    }

    @Override
    public void set_set(String[] set) {
        try {
            _Time = Integer.parseInt(set[1]);
        } catch (Exception e) {
        }
    }
}