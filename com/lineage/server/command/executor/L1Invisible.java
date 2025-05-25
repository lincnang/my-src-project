package com.lineage.server.command.executor;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 啟用/取消GM隱身
 *
 * @author dexc
 */
public class L1Invisible implements L1CommandExecutor {
    private static final Log _log = LogFactory.getLog(L1Invisible.class);

    private L1Invisible() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Invisible();
    }

    @Override
    public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
        try {
            if (pc.isGmInvis()) {
                pc.setGmInvis(false);
                pc.sendPackets(new S_Invis(pc.getId(), 0));
                pc.broadcastPacketAll(new S_OtherCharPacks(pc));
                pc.sendPackets(new S_SystemMessage("取消GM隱身!"));
            } else {
                pc.setGmInvis(true);
                pc.sendPackets(new S_Invis(pc.getId(), 1));
                pc.broadcastPacketAll(new S_RemoveObject(pc));
                pc.sendPackets(new S_SystemMessage("啟用GM隱身!"));
            }
        } catch (final Exception e) {
            _log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
            // 261 \f1指令錯誤。
            pc.sendPackets(new S_ServerMessage(261));
        }
    }
}
