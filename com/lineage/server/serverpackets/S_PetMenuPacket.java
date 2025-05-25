package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;

public class S_PetMenuPacket extends ServerBasePacket {
    private byte[] _byte = null;

    public S_PetMenuPacket(L1NpcInstance npc, int exppercet) {
        buildpacket(npc, exppercet);
    }

    private void buildpacket(L1NpcInstance npc, int exppercet) {
        writeC(S_HYPERTEXT);
        if ((npc instanceof L1PetInstance)) {
            L1PetInstance pet = (L1PetInstance) npc;
            writeD(pet.getId());
            writeS("anicom");
            writeC(0);
            writeH(10);
            switch (pet.getCurrentPetStatus()) {
                case 1:
                    writeS("$469");
                    break;
                case 2:
                    writeS("$470");
                    break;
                case 3:
                    writeS("$471");
                    break;
                case 5:
                    writeS("$472");
                    break;
                case 4:
                default:
                    writeS("$471");
            }
            writeS(Integer.toString(pet.getCurrentHp()));
            writeS(Integer.toString(pet.getMaxHp()));
            writeS(Integer.toString(pet.getCurrentMp()));
            writeS(Integer.toString(pet.getMaxMp()));
            writeS(Integer.toString(pet.getLevel()));
            writeS(pet.getName());
            writeS("$611");
            writeS(Integer.toString(exppercet));
            writeS(Integer.toString(pet.getLawful()));
        } else if ((npc instanceof L1SummonInstance)) {
            L1SummonInstance summon = (L1SummonInstance) npc;
            writeD(summon.getId());
            writeS("moncom");
            writeC(0);
            writeH(6);
            switch (summon.get_currentPetStatus()) {
                case 1:
                    writeS("$469");
                    break;
                case 2:
                    writeS("$470");
                    break;
                case 3:
                    writeS("$471");
                    break;
                case 5:
                    writeS("$472");
                    break;
                case 4:
                default:
                    writeS("$471");
            }
            writeS(Integer.toString(summon.getCurrentHp()));
            writeS(Integer.toString(summon.getMaxHp()));
            writeS(Integer.toString(summon.getCurrentMp()));
            writeS(Integer.toString(summon.getMaxMp()));
            writeS(Integer.toString(summon.getLevel()));
        }
    }

    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.serverpackets.S_PetMenuPacket JD-Core Version: 0.6.2
 */