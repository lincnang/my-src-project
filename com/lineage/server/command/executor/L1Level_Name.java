package com.lineage.server.command.executor;

import com.lineage.server.datatables.ExpTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.utils.RangeInt;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.StringTokenizer;

/**
 * 變更該GM人物等級
 *
 * @author dexc
 */
public class L1Level_Name implements L1CommandExecutor {
    private static final Log _log = LogFactory.getLog(L1Level_Name.class);

    private L1Level_Name() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Level_Name();
    }

    @Override
    public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
        try {
            final StringTokenizer tok = new StringTokenizer(arg);
            final String name = tok.nextToken();
            final int level = Integer.parseInt(tok.nextToken());
            final L1PcInstance tg = World.get().getPlayer(name);
            if (tg == null) {
                pc.sendPackets(new S_ServerMessage(73, name));
                return;
            }
            if (level == tg.getLevel()) {
                return;
            }
            if (!RangeInt.includes(level, 1, ExpTable.MAX_LEVEL)) {
                pc.sendPackets(new S_SystemMessage("範圍限制 1~" + ExpTable.MAX_LEVEL));
                return;
            }
            final long nowexp = tg.getExp();
            final long levelexp = ExpTable.getExpByLevel(level);
            final long add = levelexp - nowexp;
            tg.addExp(add);
            // pc.setExp(ExpTable.getExpByLevel(level));
            tg.onChangeExp();
        } catch (final Exception e) {
            _log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
            // 261 \f1指令錯誤。
            pc.sendPackets(new S_ServerMessage(261));
        }
    }
}
