package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;

public class C_ExtraCommand extends ClientBasePacket {
    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            read(decrypt);
            L1PcInstance pc = client.getActiveChar();
            if (pc.isGhost()) { // 鬼魂模式
                return;
            }
            if (pc.isDead()) { // 死亡
                return;
            }
            if (pc.isTeleport()) { // 傳送中
                return;
            }
            if (pc.isInvisble()) { // 隱身狀態
                return;
            }
            int actionId = readC();
            if (pc.hasSkillEffect(67)) {
                int gfxId = pc.getTempCharGfx();
                if ((gfxId != 6080) && (gfxId != 6094)) {
                    return;
                }
            }
            switch (actionId) {
                case 66:
                case 67:
                case 68:
                case 69:
                    pc.broadcastPacketAll(new S_DoActionGFX(pc.getId(), actionId));
                    pc.set_actionId(actionId);
            }
        } catch (Exception localException) {
        } finally {
            over();
        }
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.clientpackets.C_ExtraCommand JD-Core Version: 0.6.2
 */