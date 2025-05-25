package com.lineage.server.command.executor;

import com.lineage.echo.ClientExecutor;
import com.lineage.list.OnlineUser;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.DwarfReading;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.StringTokenizer;

public class L1CN2 implements L1CommandExecutor {   //src053
    private static final Log _log = LogFactory.getLog(L1CN.class);

    private L1CN2() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1CN2();
    }

    public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
        try {
            if (pc == null) {
                _log.warn("這個命令只能在遊戲中執行!");
            }
            final StringTokenizer st = new StringTokenizer(arg);
            final String pcname = st.nextToken();
            L1PcInstance target = null;
            target = World.get().getPlayer(pcname);
            if (target == null) {
                target = CharacterTable.get().restoreCharacter(pcname);
            }
            if (target != null) {
                final int item_id = Integer.parseInt(st.nextToken());
                int count = 0;
                try {
                    count = Integer.parseInt(st.nextToken());
                } catch (final Exception e) {
                    count = 1;
                }
                final L1ItemInstance item = ItemTable.get().createItem(item_id);
                if (item != null) {
                    item.setCount(count);
                    pc.sendPackets(new S_ServerMessage(166, "指定角色(" + pcname + ") 加入物品" + item_id + " 數量" + count));
                    DwarfReading.get().insertItem(target.getAccountName(), item);
                    ClientExecutor cl = OnlineUser.get().get(target.getAccountName());
                    if (cl != null) {
                        final L1PcInstance tgpc = cl.getActiveChar();
                        if (tgpc != null) {
                            tgpc.sendPackets(new S_ServerMessage(166, "伺服器已經把物品送達倉庫-物品" + item.getName() + " 數量" + count));
                            tgpc.getDwarfInventory().loadItems();
                        }
                    }
                }
            } else {
                pc.sendPackets(new S_ServerMessage(166, "指令異常: 沒有該角色(" + pcname + ")!!"));
            }
        } catch (final Exception e) {
            _log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
            pc.sendPackets(new S_ServerMessage(261));
        }
    }
}
