package com.lineage.data.item_etcitem.dragon;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillMode;
import com.lineage.server.model.skill.skillmode.SkillMode;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;

public class EyeX_Birth extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(EyeX_Birth.class);

    public static ItemExecutor get() {
        return new EyeX_Birth();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        try {
            if (item == null) {
                return;
            }
            if (pc == null) {
                return;
            }
            int time = L1BuffUtil.cancelDragon(pc);
            if (time != -1) {
                pc.sendPackets(new S_ServerMessage(1139, item.getLogName() + " " + String.valueOf(time / 60)));
                return;
            }
            pc.sendPacketsX8(new S_SkillSound(pc.getId(), 7675));
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            item.setLastUsed(ts);
            pc.getInventory().updateItem(item, 32);
            pc.getInventory().saveItem(item, 32);
            SkillMode mode = L1SkillMode.get().getSkill(6688);
            if (mode != null) {
                mode.start(pc, null, null, 600);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.dragon.EyeX_Birth JD-Core Version: 0.6.2
 */