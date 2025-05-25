package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.L1Party;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_SkillSound;

import static com.lineage.server.model.skill.L1SkillId.IMMUNE_TO_HARM;
import static com.lineage.server.model.skill.L1SkillId.IMMUNE_TO_HARM2;

public class IMMUNE_TO_HARM2 extends SkillMode { //SRC0808
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = 0;
        if ((cha instanceof L1PcInstance)) {
            L1PcInstance pc = (L1PcInstance) cha;
            // 聖結界
            //			if(srcpc.getMeteLevel()<2){
            //				pc.setSkillEffect(IMMUNE_TO_HARM2,  integer * 1000);
            //				pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_I2H, integer ));
            //			}else{
            final L1Party party = pc.getParty();
            // 對同畫面玩家檢查並施放
            if (party != null) {
                for (L1PcInstance find_pc : party.partyUsers()) {
                    // 組隊與血盟人員
                    if (find_pc.hasSkillEffect(IMMUNE_TO_HARM)) {
                        find_pc.removeSkillEffect(IMMUNE_TO_HARM);
                    }
                    find_pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_I2H, integer));
                    find_pc.sendPacketsAll(new S_SkillSound(find_pc.getId(), 228));
                    find_pc.setSkillEffect(IMMUNE_TO_HARM2, integer * 1000);
                }
            }
            //			}
        } else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1PetInstance))) {
            return 0;
        }
        return dmg;
    }

    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        return 0;
    }

    public void start(L1PcInstance srcpc, Object obj) throws Exception {
    }

    public void stop(L1Character cha) throws Exception {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            //            pc.removeSkillEffect(IMMUNE_TO_HARM2);
            pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_I2H, 0));
        } else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1PetInstance))) {
        }
    }
}