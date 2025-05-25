package com.lineage.data.item_etcitem.dragon;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TearsOfDragon extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(TearsOfDragon.class);

    public static ItemExecutor get() {
        return new TearsOfDragon();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        try {
            if (item == null) {
                return;
            }
            if (pc == null) {
                return;
            }
            if ((pc.getMapId() >= 1005) && (pc.getMapId() <= 1023)) {
                if (L1BuffUtil.stopPotion(pc)) {
                    pc.getInventory().removeItem(item, 1L);
                    L1BuffUtil.cancelAbsoluteBarrier(pc);
                    pc.sendPacketsAll(new S_SkillSound(pc.getId(), 189));
                    int addhp = 191;
                    addhp += (int) (Math.random() * 323.0D);
                    if ((addhp > 0) //&&
                        /*(ConfigOther.Show_Hp_Msg)*/) {
                        pc.sendPackets(new S_ServerMessage(77));
                    }
                    pc.setCurrentHp(pc.getCurrentHp() + addhp);
                }
            } else {
                pc.sendPackets(new S_SystemMessage("只能在屠龍副本裡使用。"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}