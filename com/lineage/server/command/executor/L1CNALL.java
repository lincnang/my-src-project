package com.lineage.server.command.executor;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.DwarfForChaReading;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.StringTokenizer;

/**
 * 指定倉庫加入指定物品
 *
 * @author dexc
 */
public class L1CNALL implements L1CommandExecutor {
    private static final Log _log = LogFactory.getLog(L1CNALL.class);

    private L1CNALL() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1CNALL();
    }

    @Override
    public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
        try {
            /*
             * if (pc == null) { _log.warn("這個命令只能在遊戲中執行!"); }
             */
            final StringTokenizer st = new StringTokenizer(arg);
            final int item_id = Integer.parseInt(st.nextToken());// ITEMID
            int count = 0;
            try {
                count = Integer.parseInt(st.nextToken());// COUNT
            } catch (final Exception e) {
                count = 1;
            }
            for (L1PcInstance onlinePc : World.get().getAllPlayers()) {
                String accountName = CharacterTable.getAccountName(onlinePc.getName());
                final L1ItemInstance item = ItemTable.get().createItem(item_id);
                if (item != null) {
                    if (accountName != null) {
                        item.setCount(count);
                        // if(onlinePc.getLevel()>40){
                        // DwarfReading.get().insertItem(accountName, item);
                        DwarfForChaReading.get().insertItem(onlinePc.getName(), item);// 加入角色專屬倉庫
                        // }
                        onlinePc.sendPackets(new S_ServerMessage(166, "管理員在你的角色專屬倉庫加入物品：" + item.getLogName()));
                        // 重整倉庫數據
                        onlinePc.getDwarfForChaInventory().loadItems();
                    }
                }
            }
        } catch (final Exception e) {
            _log.error("錯誤的GM指令格式: ");
            // 261 \f1指令錯誤。
            // pc.sendPackets(new S_ServerMessage(261));
        }
    }
}
