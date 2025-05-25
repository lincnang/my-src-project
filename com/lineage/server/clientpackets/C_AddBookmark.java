package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1HouseLocation;
import com.lineage.server.model.L1TownLocation;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1BookMark;

/**
 * 要求增加記憶座標
 *
 * @author dexc
 */
public class C_AddBookmark extends ClientBasePacket {
    @Override
    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            // 資料載入
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
            final String locName = readS();
            if (pc.getMap().isMarkable() || pc.isGm()) {
                if (pc.getX() == 33395 && pc.getY() == 32343 && pc.getMapId() == 4) {
                    // \f1這個地點不能夠標記。
                    pc.sendPackets(new S_ServerMessage(214));
                    return;
                }
                if (pc.getX() == 33265 && pc.getY() == 32401 && pc.getMapId() == 4) {
                    // \f1這個地點不能夠標記。
                    pc.sendPackets(new S_ServerMessage(214));
                    return;
                }
                if (pc.getX() == 33338 && pc.getY() == 32435 && pc.getMapId() == 4) {
                    // \f1這個地點不能夠標記。
                    pc.sendPackets(new S_ServerMessage(214));
                    return;
                }
                if (L1CastleLocation.checkInAllWarArea(pc.getX(), pc.getY(), pc.getMapId()) || L1HouseLocation.isInHouse(pc.getX(), pc.getY(), pc.getMapId())) {
                    // \f1這個地點不能夠標記。
                    pc.sendPackets(new S_ServerMessage(214));
                } else {
                    if (L1TownLocation.isGambling(pc)) {
                        // \f1這個地點不能夠標記。
                        pc.sendPackets(new S_ServerMessage(214));
                        return;
                    }
                    L1BookMark.addBookmark(pc, locName); // 日版記憶座標
                }
            } else {
                // \f1這個地點不能夠標記。
                pc.sendPackets(new S_ServerMessage(214));
            }
        } catch (final Exception e) {
            // _log.error(e.getLocalizedMessage(), e);
        } finally {
            over();
        }
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }
}
