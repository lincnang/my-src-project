package com.lineage.data.item_etcitem.teleport;

import com.lineage.config.ConfigSkillDragon;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.GetbackTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.CheckUtil;

import static com.lineage.server.model.skill.L1SkillId.THUNDER_GRAB;

public class GoHome_Reel extends ItemExecutor {
    public static ItemExecutor get() {
        return new GoHome_Reel();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (!CheckUtil.getUseItem(pc)) {
            return;
        }
        if (pc.hasSkillEffect(THUNDER_GRAB) && ConfigSkillDragon.SLAY_BREAK_NOT_TELEPORT == true) {
            pc.sendPackets(new S_ServerMessage("\\fY身上有奪命之雷的效果無法瞬移"));
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
        } else {
        }
        if ((pc.getMap().isEscapable()) || (pc.isGm())) {
            int[] loc = GetbackTable.GetBack_Location(pc, true);
            L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);
            pc.getInventory().removeItem(item, 1L);
        } else {
            pc.sendPackets(new S_ServerMessage(647));
            pc.sendPackets(new S_Paralysis(7, false));
        }
        L1BuffUtil.cancelAbsoluteBarrier(pc);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.teleport.GoHome_Reel JD-Core Version: 0.6.2
 */