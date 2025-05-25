/*package com.lineage.server.command.executor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.lineage.data.event.MiniSiege;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
/ **
 * 底比斯迷你圍塔遊戲(參數:1開啟)
 * 
 * /
public class L1MiniSiege implements L1CommandExecutor {
	private static final Log _log = LogFactory.getLog(L1MiniSiege.class);
	private L1MiniSiege() {
	}
	public static L1CommandExecutor getInstance() {
		return new L1MiniSiege();
	}
	@Override
	public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
		if (arg.equalsIgnoreCase("1")) {
            if (!MiniSiege.getInstance().running) {
            	MiniSiege.getInstance().ini();
            } else {
            	pc.sendPackets(new S_ServerMessage("底比斯迷你圍塔遊戲還未結束,無法再次開啟。"));
            }
		} else {
			_log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
			// 261 \f1指令錯誤。
			pc.sendPackets(new S_ServerMessage(261));
		}
	}
}*/
