package com.lineage.data.npc.shop;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

public class NPC_OtherShop extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(NPC_OtherShop.class);
    private static Random _random = new Random();

    public static NpcExecutor get() {
        return new NPC_OtherShop();
    }

    public int type() {
        return 1;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            int htmlidr = _random.nextInt(6);
            String htmlid = "yiwei_Shop" + htmlidr;
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), htmlid));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.shop.NPC_OtherShop JD-Core Version: 0.6.2
 */