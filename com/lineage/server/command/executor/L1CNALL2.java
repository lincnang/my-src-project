package com.lineage.server.command.executor;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.DwarfForChaReading;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * 指定倉庫加入指定物品
 *
 * @author dexc
 */
public class L1CNALL2 implements L1CommandExecutor {
    private static final Log _log = LogFactory.getLog(L1CNALL2.class);

    private L1CNALL2() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1CNALL2();
    }

    @Override
    public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
        try {
            /*
             * if (pc == null) { _log.warn("這個命令只能在遊戲中執行!"); }
             */
            final StringTokenizer st = new StringTokenizer(arg);
            final int pc_level = Integer.parseInt(st.nextToken());// 限制等級
            final int item_id = Integer.parseInt(st.nextToken());// ITEMID
            int count = 0;
            try {
                count = Integer.parseInt(st.nextToken());// COUNT
            } catch (final Exception e) {
                count = 1;
            }
            //for (L1PcInstance onlinePc : World.get().getAllPlayers()) {
            HashMap<Integer, String> charName = CharacterTable.getAllNameByLevel(pc_level);
            int j = 0;
            for (int i = 0; i < charName.size(); i++) {
                String cn = (String) charName.get(i);
                final L1ItemInstance item = ItemTable.get().createItem(item_id);
                if (item != null) {
                    if (cn != null) {
                        item.setCount(count);
                        // if(onlinePc.getLevel()>40){
                        // DwarfReading.get().insertItem(accountName, item);
                        DwarfForChaReading.get().insertItem(cn, item);// 加入角色專屬倉庫
                        // }
                        _log.info("發送物品: " + item.getLogName() + "至 " + cn);
                        //onlinePc.sendPackets(new S_ServerMessage(166, "管理員在你的角色專屬倉庫加入物品：" + item.getLogName()));
                        // 重整倉庫數據
                        //onlinePc.getDwarfForChaInventory().loadItems();
                        j++;
                    }
                }
            }
            //}
            _log.info("發送數量: " + j);
        } catch (final Exception e) {
            _log.error("錯誤的GM指令格式: ");
            // 261 \f1指令錯誤。
            // pc.sendPackets(new S_ServerMessage(261));
        }
    }
}
