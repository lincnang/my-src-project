package com.lineage.server.command.executor;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * GM重讀資料表
 *
 * @author dexc
 */
public class L1ReloadEtcitem implements L1CommandExecutor {
    private static final Log _log = LogFactory.getLog(L1ReloadEtcitem.class);

    private L1ReloadEtcitem() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1ReloadEtcitem();
    }

    @Override
    public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
        try {
            ItemTable.get().load();
            pc.sendPackets(new S_SystemMessage("更新[etcitem]資料表。"));
        } catch (final Exception e) {
            _log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
            // 261 \f1指令錯誤。
            pc.sendPackets(new S_ServerMessage(261));
        }
    }
}
