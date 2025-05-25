package com.lineage.data.npc.Tsai;

import com.add.Tsai.GuardTower;
import com.lineage.config.ConfigGuardTower;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ShopSellList;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;

/**
 * @author hero
 */
public class Npc_GuardTower extends NpcExecutor {
    //private static final Log _log = LogFactory.getLog(Npc_GuardTower.class);
    private Npc_GuardTower() {
        // TODO Auto-generated constructor stub
    }

    public static NpcExecutor get() {
        return new Npc_GuardTower();
    }

    @Override
    public int type() {
        return 3;
    }

    @Override
    public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ConfigGuardTower.LEVEL).append(",");
        L1Item item = ItemTable.get().getTemplate(ConfigGuardTower.NEEDITEM);
        stringBuilder.append(item.getName()).append(",");
        stringBuilder.append(ConfigGuardTower.NEEDITEMCOUNT).append(",");
        L1Item winitem = ItemTable.get().getTemplate(ConfigGuardTower.GIFT);
        stringBuilder.append(winitem.getName()).append(",");
        L1Item lostitem = ItemTable.get().getTemplate(ConfigGuardTower.COMFORTGIFT);
        stringBuilder.append(lostitem.getName()).append(",");
        stringBuilder.append(ConfigGuardTower.MAXPC).append(",");
        final String[] clientStrAry = stringBuilder.toString().split(",");
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_guard_0", clientStrAry));
    }

    @Override
    public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
        if (cmd.equalsIgnoreCase("g1")) {
            if (pc.getLevel() >= ConfigGuardTower.LEVEL) {
                GuardTower.get().enterRoom(pc);
            } else {
                pc.sendPackets(new S_SystemMessage("\\fR等級不足。"));
            }
        } else if (cmd.equalsIgnoreCase("buy")) {
            pc.sendPackets(new S_ShopSellList(npc.getId()));
        }
    }
}
