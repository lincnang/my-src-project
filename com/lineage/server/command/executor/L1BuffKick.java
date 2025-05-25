package com.lineage.server.command.executor;

import com.lineage.server.datatables.lock.CharBuffReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBoxGm;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 刪除已存人物保留技能(參數:人物objid/選單)
 *
 * @author dexc
 */
public class L1BuffKick implements L1CommandExecutor {
    private static final Log _log = LogFactory.getLog(L1BuffKick.class);

    private L1BuffKick() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1BuffKick();
    }

    @Override
    public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
        try {
            try {
                final int objid = Integer.parseInt(arg);
                CharBuffReading.get().deleteBuff(objid);
                if (pc == null) {
                    _log.warn("系統命令執行: 指定角色objid Buff清除!");
                } else {
                    pc.sendPackets(new S_ServerMessage(166, objid + " Buff清除!"));
                }
            } catch (final Exception e) {
                final int mode = 0;
                if (pc != null) {
                    pc.sendPackets(new S_PacketBoxGm(pc, mode));
                }
            }
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
