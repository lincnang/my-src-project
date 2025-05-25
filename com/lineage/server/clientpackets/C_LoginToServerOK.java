package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;

public class C_LoginToServerOK extends ClientBasePacket {
    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            read(decrypt);
            int type = readC();
            int button = readC();
            L1PcInstance pc = client.getActiveChar();
            switch (type) {
                case 255:
                    switch (button) {
                        case 95:
                        case 127:
                            pc.setShowWorldChat(true);
                            pc.setCanWhisper(true);
                            break;
                        case 91:
                        case 123:
                            pc.setShowWorldChat(true);
                            pc.setCanWhisper(false);
                            break;
                        case 94:
                        case 126:
                            pc.setShowWorldChat(false);
                            pc.setCanWhisper(true);
                            break;
                        case 90:
                        case 122:
                            pc.setShowWorldChat(false);
                            pc.setCanWhisper(false);
                    }
                    break;
                case 0:
                    if (button == 0) {
                        pc.setShowWorldChat(false);
                    } else if (button == 1) {
                        pc.setShowWorldChat(true);
                    }
                    break;
                case 2:
                    if (button == 0) {
                        pc.setCanWhisper(false);
                    } else if (button == 1) {
                        pc.setCanWhisper(true);
                    }
                    break;
                case 6:
                    if (button == 0) {
                        pc.setShowTradeChat(false);
                    } else if (button == 1) {
                        pc.setShowTradeChat(true);
                    }
                    break;
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
 * com.lineage.server.clientpackets.C_LoginToServerOK JD-Core Version: 0.6.2
 */