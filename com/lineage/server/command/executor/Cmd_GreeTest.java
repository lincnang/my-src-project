package com.lineage.server.command.executor;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Cmd_GreeTest implements L1CommandExecutor {

    private static final Log _log = LogFactory.getLog(Cmd_GreeTest.class);

    // ✅ 必加！給系統反射呼叫
    public static L1CommandExecutor getInstance() {
        return new Cmd_GreeTest();
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            int effect = Integer.parseInt(arg);
            World.get().broadcastPacketToAll(new S_PacketBoxGree(effect));
            pc.sendPackets(new S_SystemMessage("老爹天堂已廣播 Gree 效果碼: " + effect));
            _log.info("Cmd_GreeTest 執行成功，效果碼：" + effect);
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(" 指令格式錯誤，請輸入 .gree 數字"));
            _log.error(" Cmd_GreeTest 執行失敗: " + e.getMessage(), e);
        }
    }
}
