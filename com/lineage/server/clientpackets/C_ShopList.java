package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PrivateShop;

public class C_ShopList extends ClientBasePacket {
    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            read(decrypt);
            L1PcInstance pc = client.getActiveChar();
            if (pc.isGhost())
                ;
            while ((pc.isDead()) || (pc.isTeleport()) || (pc.isPrivateShop())) {
                return;
            }
            int mapId = pc.getMapId();
            boolean isShopMap = false;
            if (mapId == 340) {
                isShopMap = true;
            }
            if (mapId == 350) {
                isShopMap = true;
            }
            if (mapId == 360) {
                isShopMap = true;
            }
            if (mapId == 370) {
                isShopMap = true;
            }
            if (mapId == 800) {
                isShopMap = true;
            }
            if (isShopMap) {
                int type = readC();
                int objectId = readD();
                pc.sendPackets(new S_PrivateShop(pc, objectId, type));
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
