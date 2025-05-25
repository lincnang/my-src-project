package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1SkinInstance;
import com.lineage.server.serverpackets.S_ChangeHeading;

import java.util.Iterator;
import java.util.Map;

public class C_ChangeHeading extends ClientBasePacket {
    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            read(decrypt);
            L1PcInstance pc = client.getActiveChar();
            if (pc.isGhost())
                ;
            while ((pc.isDead()) || (pc.isTeleport())) {
                return;
            }
            int heading = readC();
            switch (heading) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    pc.setHeading(heading);
                    if ((!pc.isGmInvis()) && (!pc.isGhost()) && (!pc.isInvisble())) {
                        pc.broadcastPacketAll(new S_ChangeHeading(pc));
                    }
                {
                }
                Map<Integer, L1SkinInstance> skinList = pc.getSkins();
                if (skinList.size() > 0) {
                    L1SkinInstance skin;
                    for (Iterator<Integer> iterator = skinList.keySet().iterator(); iterator.hasNext(); skin.broadcastPacketAll(new S_ChangeHeading(skin))) {
                        Integer gfxid = (Integer) iterator.next();
                        skin = (L1SkinInstance) skinList.get(gfxid);
                        skin.setHeading(heading);
                    }
                }
                break;
            }
        } catch (final Exception e) {
            // _log.error(e.getLocalizedMessage(), e);
        } finally {
            this.over();
        }
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}