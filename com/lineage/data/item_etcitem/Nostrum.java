package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_OwnCharStatus2;

public class Nostrum extends ItemExecutor {
    public static ItemExecutor get() {
        return new Nostrum();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int a = Integer.parseInt(get_set()[1]);
        try {
            for (int i = pc.getBaseStr(); i < a; i++) {
                pc.addBaseStr(1);
                pc.setBonusStats(pc.getBonusStats() + 1);
            }
            for (int i = pc.getBaseDex(); i < a; i++) {
                pc.addBaseDex(1);
                pc.setBonusStats(pc.getBonusStats() + 1);
            }
            for (int i = pc.getBaseCon(); i < a; i++) {
                pc.addBaseCon(1);
                pc.setBonusStats(pc.getBonusStats() + 1);
            }
            for (int i = pc.getBaseWis(); i < a; i++) {
                pc.addBaseWis(1);
                pc.setBonusStats(pc.getBonusStats() + 1);
            }
            for (int i = pc.getBaseCha(); i < a; i++) {
                pc.addBaseCha(1);
                pc.setBonusStats(pc.getBonusStats() + 1);
            }
            for (int i = pc.getBaseInt(); i < a; i++) {
                pc.addBaseInt(1);
                pc.setBonusStats(pc.getBonusStats() + 1);
            }
            pc.sendPackets(new S_OwnCharStatus2(pc));
            pc.sendDetails();
            pc.save();
            pc.getInventory().removeItem(item, 1L);
        } catch (Exception localException) {
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Nostrum JD-Core Version: 0.6.2
 */