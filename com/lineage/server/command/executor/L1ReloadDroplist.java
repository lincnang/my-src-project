package com.lineage.server.command.executor;
//import com.lineage.server.datatables.DropMapLimitTable;

import com.lineage.server.datatables.DropMapTable;
import com.lineage.server.datatables.DropTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 掉寶資料重置資料庫
 *
 * @author dexc
 */
public class L1ReloadDroplist implements L1CommandExecutor {
    private L1ReloadDroplist() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1ReloadDroplist();
    }

    @Override
    public void execute(L1PcInstance paramL1PcInstance, String paramString1, String paramString2) {
        DropTable.get().load();
        DropMapTable.get().load();
        //DropMapLimitTable.get();
        //DropMapLimitTable.reload();
        paramL1PcInstance.sendPackets(new S_SystemMessage("[droplist]和[droplist_map]資料庫已重讀完成!"));
    }
}
