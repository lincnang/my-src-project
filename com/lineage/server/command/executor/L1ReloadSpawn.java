package com.lineage.server.command.executor;

import com.lineage.server.datatables.SpawnTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;

public class L1ReloadSpawn implements L1CommandExecutor {
    private L1ReloadSpawn() {}
    public static L1CommandExecutor getInstance() {
        return new L1ReloadSpawn();
    }
    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
        SpawnTable.get().restSpawn();
        pc.sendPackets(new S_SystemMessage("Spawnlist 已重讀完成！"));
    }
}
