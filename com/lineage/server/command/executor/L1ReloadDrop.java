package com.lineage.server.command.executor;

import com.lineage.server.datatables.DropTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1ReloadDrop implements L1CommandExecutor {
    private static final Log _log = LogFactory.getLog(L1ReloadDrop.class);

    public static L1CommandExecutor getInstance() {
        return new L1ReloadDrop();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        if (pc == null) {
            _log.warn("系統命令執行: " + cmdName + "重新載入掉寶資料數量。");
        } else {
            pc.sendPackets(new S_SystemMessage("重新載入掉寶資料數量"));
        }
        DropTable.get().load();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.command.executor.L1ReloadDrop JD-Core Version: 0.6.2
 */