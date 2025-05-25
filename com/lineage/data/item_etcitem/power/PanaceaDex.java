package com.lineage.data.item_etcitem.power;

import com.lineage.config.ConfigAlt;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_OwnCharStatus2;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PanaceaDex extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(PanaceaDex.class);

    public static ItemExecutor get() {
        return new PanaceaDex();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (pc.getBaseDex() < ConfigAlt.POWERMEDICINE) {
            if (pc.getElixirStats() < ConfigAlt.MEDICINE) {
                pc.addBaseDex(1);
                pc.resetBaseAc();
                pc.setElixirStats(pc.getElixirStats() + 1);
                pc.getInventory().removeItem(item, 1L);
                pc.sendPackets(new S_OwnCharStatus2(pc));
                pc.sendDetails();
                try {
                    pc.save();
                } catch (Exception e) {
                    _log.error(e.getLocalizedMessage(), e);
                }
            } else {
                pc.sendPackets(new S_ServerMessage(79));
            }
        } else {
            pc.sendPackets(new S_ServerMessage(481));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.power.PanaceaDex JD-Core Version: 0.6.2
 */