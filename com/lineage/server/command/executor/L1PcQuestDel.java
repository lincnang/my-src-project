package com.lineage.server.command.executor;

import com.lineage.server.datatables.lock.CharacterQuestReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.StringTokenizer;

/**
 * 刪除已存人物保留技能(參數:人物objid/選單)
 *
 * @author dexc
 */
public class L1PcQuestDel implements L1CommandExecutor {
    private static final Log _log = LogFactory.getLog(L1PcQuestDel.class);

    private L1PcQuestDel() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1PcQuestDel();
    }

    @Override
    public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
        try {
            StringTokenizer st = new StringTokenizer(arg);
            final String char_name = st.nextToken();
            int questid = Integer.parseInt(st.nextToken());
            L1PcInstance target = null;
            target = World.get().getPlayer(char_name);
            CharacterQuestReading.get().delQuest(target.getId(), questid);
            pc.sendPackets(new S_ServerMessage("角色:" + target.getName() + "任務編號:" + questid + "紀錄已從資料庫刪除。"));
            _log.warn("[GM指令]角色:" + target.getName() + "任務編號:" + questid + "清除!");
        } catch (final Exception e) {
            if (pc == null) {
                _log.error("錯誤的命令格式: " + this.getClass().getSimpleName());
            } else {
                _log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
                // 261 \f1指令錯誤。
                pc.sendPackets(new S_ServerMessage(261));
            }
        }
    }
}
