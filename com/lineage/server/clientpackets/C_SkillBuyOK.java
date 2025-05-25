package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.list.PcLvSkillList;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.lock.CharSkillReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_AddSkill;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1Skills;

import java.util.ArrayList;

public class C_SkillBuyOK extends ClientBasePacket {
    private static final int[] PRICE = {1, 4, 9, 16, 25, 36, 49, 64, 81, 100, 121, 144, 169, 196, 225, 0, 289, 324, 361, 400, 441, 484, 529, 576, 625, 676, 729, 784};

    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            read(decrypt);
            L1PcInstance pc = client.getActiveChar();
            if (pc.isGhost())
                ;
            while ((pc.isDead()) || (pc.isTeleport()) || (pc.isPrivateShop())) {
                return;
            }
            int count = readH();
            ArrayList<?> skillList = null;
            if (pc.isCrown()) {
                skillList = PcLvSkillList.isCrown(pc);
            } else if (pc.isKnight()) {
                skillList = PcLvSkillList.isKnight(pc);
            } else if (pc.isElf()) {
                skillList = PcLvSkillList.isElf(pc);
            } else if (pc.isWizard()) {
                skillList = PcLvSkillList.isWizard(pc);
            } else if (pc.isDarkelf()) {
                skillList = PcLvSkillList.isDarkelf(pc);
            } else if (pc.isDragonKnight()) {
                skillList = PcLvSkillList.isDragonKnight(pc);
            } else if (pc.isIllusionist()) {
                skillList = PcLvSkillList.isIllusionist(pc);
            } else if (pc.isIllusionist()) {
                skillList = PcLvSkillList.isWarrior(pc);
            }
            if (skillList == null) {
                return;
            }
            boolean isGfx = false;
            boolean shopSkill = false;
            if (pc.get_other().get_shopSkill()) {
                shopSkill = true;
            }
            for (int i = 0; i < count; i++) {
                int skillId = readD() + 1;
                if ((!CharSkillReading.get().spellCheck(pc.getId(), skillId)) && (skillList.contains(new Integer(skillId - 1)))) {
                    L1Skills l1skills = SkillsTable.get().getTemplate(skillId);
                    int skillLvPrice = PRICE[(l1skills.getSkillLevel() - 1)];
                    int price = (shopSkill ? com.lineage.server.serverpackets.S_SkillBuyCN.PCTYPE[pc.getType()] : 6000) * skillLvPrice;
                    if (pc.getInventory().checkItem(40308, price)) {
                        pc.getInventory().consumeItem(40308, price);
                        CharSkillReading.get().spellMastery(pc.getId(), l1skills.getSkillId(), l1skills.getName(), 0, 0);
                        pc.sendPackets(new S_AddSkill(pc, skillId));
                        isGfx = true;
                    } else {
                        pc.sendPackets(new S_ServerMessage(189));
                    }
                }
            }
            if (isGfx) {
                pc.sendPacketsX8(new S_SkillSound(pc.getId(), 224));
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
 * com.lineage.server.clientpackets.C_SkillBuyOK JD-Core Version: 0.6.2
 */