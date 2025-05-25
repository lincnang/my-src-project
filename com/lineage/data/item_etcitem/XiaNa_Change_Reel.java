package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.serverpackets.S_ServerMessage;

public class XiaNa_Change_Reel extends ItemExecutor {
    public static ItemExecutor get() {
        return new XiaNa_Change_Reel();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (pc.getMapId() == 5490) {
            pc.sendPackets(new S_ServerMessage(1170));
            return;
        }
        if (pc.getMapId() == 9000) {
            pc.sendPackets(new S_ServerMessage(1170));
            return;
        }
        if (pc.getMapId() == 9100) {
            pc.sendPackets(new S_ServerMessage(1170));
            return;
        }
        int itemId = item.getItemId();
        usePolyPotion(pc, itemId);
        pc.getInventory().removeItem(item, 1L);
    }

    private void usePolyPotion(L1PcInstance pc, int itemId) {
        int awakeSkillId = pc.getAwakeSkillId();
        if ((awakeSkillId == 185) || (awakeSkillId == 190) || (awakeSkillId == 195)) {
            pc.sendPackets(new S_ServerMessage(1384));
            return;
        }
        //		basic warrior female	12614	1	28	4095	1	7	原職業
        //		basic illusionist female	11421	1	554	4095	1	7	原職業
        //		basic dragonknight female	11419	1	1054	4095	1	7	原職業
        //		basic darkelf female	11417	1	459	4095	1	7	原職業
        //		basic wizard female	11415	1	291	4095	1	7	原職業
        //		basic elf female	11413	1	267	4095	1	7	原職業
        //		basic knight female	11411	1	319	4095	1	7	原職業
        //		basic prince female	11409	1	319	4095	1	7	原職業
        //		basic warrior male	12613	1	28	4095	1	7	原職業
        //		basic illusionist  male	11420	1	554	4095	1	7	原職業
        //		basic dragonknight male	11418	1	1054	4095	1	7	原職業
        //		basic darkelf male	11416	1	459	4095	1	7	原職業
        //		basic wizard male	11414	1	291	4095	1	7	原職業
        //		basic elf male	11412	1	267	4095	1	7	原職業
        //		basic knight male	11410	1	319	4095	1	7	原職業
        //		basic prince male	11408	1	319	4095	1	7	原職業
        int polyId = 0;
        if (itemId >= 82225 && itemId <= 82233) {
            if ((pc.get_sex() == 0) && (pc.isCrown())) {
                polyId = 11408;
            } else if ((pc.get_sex() == 1) && (pc.isCrown())) {
                polyId = 11409;
            } else if ((pc.get_sex() == 0) && (pc.isKnight())) {
                polyId = 11410;
            } else if ((pc.get_sex() == 1) && (pc.isKnight())) {
                polyId = 11411;
            } else if ((pc.get_sex() == 0) && (pc.isElf())) {
                polyId = 11412;
            } else if ((pc.get_sex() == 1) && (pc.isElf())) {
                polyId = 11413;
            } else if ((pc.get_sex() == 0) && (pc.isWizard())) {
                polyId = 11414;
            } else if ((pc.get_sex() == 1) && (pc.isWizard())) {
                polyId = 11415;
            } else if ((pc.get_sex() == 0) && (pc.isDarkelf())) {
                polyId = 11416;
            } else if ((pc.get_sex() == 1) && (pc.isDarkelf())) {
                polyId = 11417;
            } else if ((pc.get_sex() == 0) && (pc.isDragonKnight())) {
                polyId = 11418;
            } else if ((pc.get_sex() == 1) && (pc.isDragonKnight())) {
                polyId = 11419;
            } else if ((pc.get_sex() == 0) && (pc.isIllusionist())) {
                polyId = 11420;
            } else if ((pc.get_sex() == 1) && (pc.isIllusionist())) {
                polyId = 11421;
            } else if ((pc.get_sex() == 0) && (pc.isWarrior())) {
                polyId = 12613;
            } else if ((pc.get_sex() == 1) && (pc.isWarrior())) {
                polyId = 12614;
            }
        }
        L1PolyMorph.doPoly(pc, polyId, 1800, 1);
    }
}
/*
 * Location: C:\Users\kenny\Desktop\ Qualified Name:
 * com.lineage.data.item_etcitem.XiaNa_Change_Reel JD-Core Version: 0.6.2
 */