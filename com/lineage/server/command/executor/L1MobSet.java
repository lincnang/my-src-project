/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.lineage.server.command.executor;

import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.SpawnTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.utils.L1SpawnUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.StringTokenizer;

/**
 * 設置定點MOB(參數:MOB編號)
 *
 * @author dexc
 */
public class L1MobSet implements L1CommandExecutor {
    private static final Log _log = LogFactory.getLog(L1MobSet.class);

    private L1MobSet() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1MobSet();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        String msg = null;
        try {
            StringTokenizer tok = new StringTokenizer(arg);
            int npcId = Integer.parseInt(tok.nextToken().trim());
            L1Npc template = NpcTable.get().getTemplate(npcId);
            if (template == null) {
                msg = "找不到符合條件的Mob。";
                return;
            }
			/*if (!template.getImpl().equals("L1Monster")) {
				msg = "指定的npc不是L1Monster。";
				return;
			}*/
            SpawnTable.storeSpawn(pc, template);
            L1SpawnUtil.spawn(pc, npcId, 0, 0);
            msg = new StringBuilder().append(template.get_name()).append(" (").append(npcId).append(") ").append("新增到資料庫中。").toString();
        } catch (Exception e) {
            _log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
            msg = cmdName + " MOBID 請輸入。";
        } finally {
            if (msg != null) {
                pc.sendPackets(new S_SystemMessage(msg));
            }
        }
    }
}
