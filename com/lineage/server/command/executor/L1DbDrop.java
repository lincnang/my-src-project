package com.lineage.server.command.executor;

import com.lineage.config.ConfigRate;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_GmMessage;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.StringTokenizer;

/**
 * 變更掉寶倍率(參數:倍率)
 *
 * @author dexc
 */
public class L1DbDrop implements L1CommandExecutor {
    private static final Log _log = LogFactory.getLog(L1DbDrop.class);

    private L1DbDrop() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1DbDrop();
    }

    @Override
    public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
        try {
            final StringTokenizer st = new StringTokenizer(arg);
            final int rateDrop = Integer.parseInt(st.nextToken(), 10);
            String msgid = null;
            if (pc == null) {
                _log.warn("系統命令執行: " + cmdName + " 變更掉寶率" + arg);
            }
            if ((int) ConfigRate.RATE_DROP_ITEMS == rateDrop) {
                if (pc == null) {
                    _log.warn("目前掉寶率已經是: " + rateDrop);
                } else {
                    pc.sendPackets(new S_ServerMessage(166, "目前掉寶率已經是:" + rateDrop));
                }
                return;
            } else if (ConfigRate.RATE_DROP_ITEMS < rateDrop) {
                ConfigRate.RATE_DROP_ITEMS = rateDrop;
                msgid = "掉寶率變更為" + ConfigRate.RATE_DROP_ITEMS + "倍。";
            } else if (ConfigRate.RATE_DROP_ITEMS > rateDrop) {
                ConfigRate.RATE_DROP_ITEMS = rateDrop;
                msgid = "掉寶率恢復為" + ConfigRate.RATE_DROP_ITEMS + "倍。";
            }
            if (msgid != null) {
                World.get().broadcastPacketToAll(new S_GmMessage(msgid, "\\aE"));
            }
            if (pc == null) {
                _log.warn("目前掉寶率變更為: " + rateDrop);
            }
        } catch (final Exception e) {
            if (pc == null) {
                _log.error("錯誤的命令格式: " + this.getClass().getSimpleName());
            } else {
                _log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
                // 261 \f1指令錯誤。
                pc.sendPackets(new S_ServerMessage(261));
            }
        }
    }
}
