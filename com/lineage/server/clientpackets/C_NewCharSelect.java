package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ChangeName;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_PacketBoxIcon1;
import com.lineage.server.serverpackets.S_PacketBoxSelect;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.TimeUnit;

public class C_NewCharSelect extends ClientBasePacket {
    private static final Log _log = LogFactory.getLog(C_NewCharSelect.class);

    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            L1PcInstance pc = client.getActiveChar();
            if (pc == null)
                return;
            if (pc.getMapId() == 9000 || pc.getMapId() == 9101) {
                return;
            }
            pc.sendPackets(new S_ChangeName(pc, false));
            pc.sendPackets(new S_PacketBox(S_PacketBox.UPDATE_ER, pc.getEr()));// 迴避率更新
            pc.sendPackets(new S_PacketBoxIcon1(true, pc.get_dodge()));// 閃避率更新
            TimeUnit.MILLISECONDS.sleep(250L);
            client.quitGame();
            client.out().encrypt(new S_PacketBoxSelect());
            _log.info("角色切換: " + pc.getName());
        } catch (Exception ignored) {
        } finally {
            over();
        }
    }

    public String getType() {
        return super.getType();
    }
}