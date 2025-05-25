package com.lineage.server.command.executor;

import com.lineage.server.command.GMCommands;
import com.lineage.server.datatables.CommandsTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Command;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 顯示管理指令群
 *
 * @author daien
 */
public class L1CommandHelp implements L1CommandExecutor {
    private static final Log _log = LogFactory.getLog(L1CommandHelp.class);

    /**
     * 顯示管理指令群
     */
    private L1CommandHelp() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1CommandHelp();
    }

    @Override
    public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
        for (final L1Command command : CommandsTable.get().getList()) {
            if (pc == null) {
                if (command.isSystem()) {
                    _log.info("可用命令: " + command.getName() + ": " + command.get_note());
                }
            } else {
                List<L1Command> list = GMCommands.availableCommandList(pc.getAccessLevel());
                showCommand(list, pc);
            }
        }
        if (pc == null) {
            _log.info("可用命令: c: 對遊戲中玩家廣播公告.");
        }
    }

    private void showCommand(List<L1Command> list, L1PcInstance pc) {
        List<String> commands = World.get().getHtmlString();
        commands.add("☆☆ 管理員指令清單 ☆☆");
        for (L1Command cmd : list) {
            commands.add("[" + cmd.getName() + "] ");
            commands.add(cmd.get_note());
        }
        World.showHtmlInfo(pc, "cominfo");

    }
}
