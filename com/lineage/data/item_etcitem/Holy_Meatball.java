package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

import static com.lineage.server.model.skill.L1SkillId.STATUS_SUNRISE;

public class Holy_Meatball extends ItemExecutor {
    public static ItemExecutor get() {
        return new Holy_Meatball();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (item == null) {
            return;
        }
        if (pc == null) {
            return;
        }
        int time = 300;
        if (item.getItemId() == 85003) {// 神聖的丸子(30分)
            time = 1800;
        }
        if (pc.hasSkillEffect(STATUS_SUNRISE)) {
            pc.killSkillEffectTimer(STATUS_SUNRISE);
            pc.removeSkillEffect(STATUS_SUNRISE);
        }
        pc.sendPackets(new S_SkillSound(pc.getId(), 7612));
        pc.sendPackets(new S_PacketBox(154, time, 20));// 20:日出之國右上圖示
        pc.setSkillEffect(STATUS_SUNRISE, time * 1000);
        pc.getInventory().removeItem(item, 1);
        // 不再害怕巨型骷髏
        pc.sendPackets(new S_ServerMessage(3673));
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.event.SExp13 JD-Core Version: 0.6.2
 */