package com.lineage.data.item_etcitem.teleport;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.utils.CheckUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NewOM_TeleportRune_Phantom extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(NewOM_TeleportRune_Phantom.class);

    public static ItemExecutor get() {
        return new NewOM_TeleportRune_Phantom();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        try {
            if (!CheckUtil.getUseItem(pc)) {
                return;
            }
            if (!pc.getMap().isEscapable()) {
                return;
            }
            pc.sendPackets(new S_NPCTalkReturn(pc, "telBook4"));
            if (!pc.isPhantomTeleport()) {
                pc.setPhantomTeleport(true);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.teleport.Transmission_Reel_Seal JD-Core
 * Version: 0.6.2
 */