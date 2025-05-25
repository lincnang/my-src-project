package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.S_SkillBuyItem;
import com.lineage.server.world.World;

public class C_SkillBuyItem extends ClientBasePacket {
    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            read(decrypt);
            L1PcInstance pc = client.getActiveChar();
            if (pc.isGhost())
                ;
            while ((pc.isDead()) || (pc.isTeleport()) || (pc.isPrivateShop())) {
                return;
            }
            int objid = readD();
            L1Object obj = World.get().findObject(objid);
            if (obj == null) {
                return;
            }
            L1NpcInstance npc = null;
            if ((obj instanceof L1NpcInstance)) {
                npc = (L1NpcInstance) obj;
            }
            if (npc == null) {
                return;
            }
            npc.getNpcId();
            pc.get_other().set_shopSkill(false);
            pc.sendPackets(new S_SkillBuyItem(pc, npc));
        } catch (Exception localException) {
        } finally {
            over();
        }
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}