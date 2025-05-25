package com.lineage.server.command.executor;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;

import java.util.StringTokenizer;

public class L1InvGfxId implements L1CommandExecutor {
    public static L1CommandExecutor getInstance() {
        return new L1InvGfxId();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer st = new StringTokenizer(arg);
            int gfxid = Integer.parseInt(st.nextToken(), 10);
            int count = Integer.parseInt(st.nextToken(), 10);
            for (int i = 0; i < count; i++) {
                L1ItemInstance item = ItemTable.get().createItem(40005);
                item.getItem().setGfxId(gfxid + i);
                item.getItem().setName(String.valueOf(gfxid + i));
                pc.getInventory().storeItem(item);
            }
        } catch (Exception exception) {
            pc.sendPackets(new S_SystemMessage(cmdName + " 請輸入 id 出現的數量。"));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.command.executor.L1InvGfxId JD-Core Version: 0.6.2
 */