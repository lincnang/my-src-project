package com.lineage.data.item_etcitem.shop;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ChatStop extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(ChatStop.class);

    public static ItemExecutor get() {
        return new ChatStop();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (item == null) {
            return;
        }
        if (pc == null) {
            return;
        }
        try {
            int targObjId = data[0];
            L1Object object = World.get().findObject(targObjId);
            if (object != null) {
                if ((object instanceof L1PcInstance)) {
                    L1PcInstance target = (L1PcInstance) object;
                    if (target.isGm()) {
                        pc.sendPackets(new S_ServerMessage(166, "你不能對GM使用禁言卡"));
                    } else if (!target.hasSkillEffect(7004)) {
                        pc.getInventory().removeItem(item, 1L);
                        pc.sendPacketsX8(new S_DoActionGFX(pc.getId(), 17));
                        target.setSkillEffect(7004, 60000);
                        target.sendPackets(new S_PacketBox(36, 60));
                        target.sendPackets(new S_ServerMessage(287, String.valueOf(1)));
                        target.sendPackets(new S_ServerMessage(166, pc.getName() + "對你施展禁言卡"));
                        pc.sendPackets(new S_ServerMessage(166, "對" + target.getName() + "施展禁言卡"));
                    } else {
                        pc.sendPackets(new S_ServerMessage(166, target.getName() + "已經在禁言狀態"));
                    }
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.shop.ChatStop JD-Core Version: 0.6.2
 */