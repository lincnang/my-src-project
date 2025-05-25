package com.lineage.data.item_etcitem.teleport;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.data.quest.KnightLv30_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.CheckUtil;

public class SecretRoom_Key extends ItemExecutor {
    public static ItemExecutor get() {
        return new SecretRoom_Key();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (!CheckUtil.getUseItem(pc)) {
            return;
        }
        if ((pc.isKnight()) && (pc.getX() >= 32806) && (pc.getX() <= 32814) && (pc.getY() >= 32798) && (pc.getY() <= 32807) && (pc.getMapId() == 813)) {
            if (pc.isKnight()) {
                if (pc.getQuest().isEnd(KnightLv30_1.QUEST.get_id())) {
                    pc.sendPackets(new S_ServerMessage(79));
                    return;
                }
                if (!pc.getQuest().isStart(KnightLv30_1.QUEST.get_id())) {
                    pc.sendPackets(new S_ServerMessage(79));
                } else {
                    L1Teleport.teleport(pc, 32727, 32789, (short) 237, 5, false);
                }
            }
        } else {
            pc.sendPackets(new S_ServerMessage(79));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.teleport.SecretRoom_Key JD-Core Version: 0.6.2
 */