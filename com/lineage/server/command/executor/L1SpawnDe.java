package com.lineage.server.command.executor;

import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1DeInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.DeName;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.L1SpawnUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

public class L1SpawnDe implements L1CommandExecutor {
    private static final Log _log = LogFactory.getLog(L1SpawnDe.class);

    public static L1CommandExecutor getInstance() {
        return new L1SpawnDe();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            if (arg.startsWith("s")) {
                L1DeInstance de = L1SpawnUtil.spawnde(pc, null, 0, 0);
                de.start_shop();
                return;
            }
            StringTokenizer tok = new StringTokenizer(arg);
            int count = 1;
            if (tok.hasMoreTokens()) {
                count = Integer.parseInt(tok.nextToken());
            }
            int randomrange = 10;
            if (tok.hasMoreTokens()) {
                randomrange = Integer.parseInt(tok.nextToken());
            }
            L1Npc npc = NpcTable.get().getTemplate(81002);
            if (npc == null) {
                pc.sendPackets(new S_SystemMessage("找不到該npc。"));
                return;
            }
            if (count > 100) {
                pc.sendPackets(new S_SystemMessage("一次召喚數量不能超過100。"));
                return;
            }
            if (count > 20) {
                DeRunnable deRunnable = new DeRunnable(pc, null, randomrange, count);
                GeneralThreadPool.get().execute(deRunnable);
            } else {
                for (int i = 0; i < count; i++) {
                    L1SpawnUtil.spawnde(pc, null, randomrange, 0);
                }
            }
            String msg = String.format("%s(%d) (%d) 召喚。 (範圍:%d)", new Object[]{npc.get_name(), 81002, count, randomrange});
            pc.sendPackets(new S_SystemMessage(msg));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
            _log.error("錯誤的GM指令格式: " + getClass().getSimpleName() + " 執行的GM:" + pc.getName());
            pc.sendPackets(new S_ServerMessage(261));
        }
    }

    private static class DeRunnable implements Runnable {
        private L1PcInstance _pc;
        private int _randomrange;
        private int _count;

        private DeRunnable(L1PcInstance pc, DeName de, int randomrange, int count) {
            _pc = pc;
            _randomrange = randomrange;
            _count = count;
        }

        public void run() {
            try {
                for (int i = 0; i < _count; i++) {
                    L1SpawnUtil.spawnde(_pc, null, _randomrange, 0);
                    TimeUnit.MILLISECONDS.sleep(1L);
                }
            } catch (InterruptedException e) {
                L1SpawnDe._log.error(e.getLocalizedMessage(), e);
            }
        }
    }
}
/*
 * Location: D:\九尾天堂模擬器檔案\九尾二服\Server_Game.jar Qualified Name:
 * com.lineage.server.command.executor.L1SpawnDe JD-Core Version: 0.6.2
 */