package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.L1Character;

/**
 * 寵物控制介面
 *
 * @author L1jJP, Dexc, Tom
 */
public class S_PetCtrlMenu extends ServerBasePacket {
    private byte[] _byte = null;

    /**
     * 新的寵物控制界面
     *
     * @param cha
     * @param npc
     * @param open
     */
    public S_PetCtrlMenu(final L1Character cha, final L1NpcInstance npc, final boolean open) {
        if (open) {
            this.NewPetMenu(cha, npc);
        } else {
            this.DelPetMenu(cha, npc);
        }
    }

    public void NewPetMenu(final L1Character cha, final L1NpcInstance npc) {
        this.writeC(S_VOICE_CHAT);
        this.writeC(0x0c);
        this.writeH(cha.getPetList().size() * 3);
        this.writeD(0);
        this.writeD(npc.getId());
        this.writeD(npc.getMapId());
        this.writeH(npc.getX());
        this.writeH(npc.getY());
        //this.writeC(0); // ?
        this.writeC(!(npc instanceof L1PetInstance) ? 0 : 1);
        this.writeS(npc.getName());
    }

    public void DelPetMenu(final L1Character cha, final L1NpcInstance npc) {
        this.writeC(S_VOICE_CHAT);
        this.writeC(0x0c);
        this.writeH(cha.getPetList().size() * 3 - 3);
        this.writeD(1);
        this.writeD(npc.getId());
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = this.getBytes();
        }
        return _byte;
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }
}
