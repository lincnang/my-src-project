package com.lineage.data.npc.shop;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ShopSellListCn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Strange extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Strange.class);
    private int _itemid = 0;
    private String _htmlid = null;

    public static NpcExecutor get() {
        return new Npc_Strange();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        if (_htmlid != null) {
            pc.set_temp_adena(_itemid);
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), _htmlid));
        } else {
            pc.set_temp_adena(_itemid);
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_shop"));
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        if (cmd.equalsIgnoreCase("a")) {
            pc.sendPackets(new S_ShopSellListCn(pc, npc));
        }
    }

    public void set_set(String[] set) {
        try {
            _itemid = Integer.parseInt(set[1]);
        } catch (Exception e) {
            _log.error("NPC專屬貨幣設置錯誤:檢查CLASSNAME為Npc_Strange的NPC設置!");
        }
        try {
            _htmlid = set[2];
        } catch (Exception localException1) {
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.shop.Npc_Strange JD-Core Version: 0.6.2
 */