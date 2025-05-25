package com.lineage.server.command.executor;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;

public class L1Save implements L1CommandExecutor {
    public static L1CommandExecutor getInstance() {
        return new L1Save();
    }

    public void execute(L1PcInstance paramL1PcInstance, String paramString1, String paramString2) {
        try {
            for (L1PcInstance localL1PcInstance : World.get().getAllPlayers()) {
                localL1PcInstance.save();
                localL1PcInstance.saveInventory();
                localL1PcInstance.sendPackets(new S_SystemMessage("您的資料已經受到儲存保護。"));
                paramL1PcInstance.sendPackets(new S_SystemMessage("伺服器資料儲存完畢。"));
                System.out.println("伺服器上的人物資料已儲存到資料庫中。 剩餘記憶體:" + Runtime.getRuntime().freeMemory() / 1024L / 1024L + "MB");
            }
        } catch (Exception localException) {
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.command.executor.L1Save JD-Core Version: 0.6.2
 */