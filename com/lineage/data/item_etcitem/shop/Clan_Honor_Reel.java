package com.lineage.data.item_etcitem.shop;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SkillSound;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Clan_Honor_Reel extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(Clan_Honor_Reel.class);

    public static ItemExecutor get() {
        return new Clan_Honor_Reel();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (item == null) {
            return;
        }
        if (pc == null) {
            return;
        }
        if (pc.getLevel() >= 70) {
            return;
        }
        pc.addExp(813177894L);
        pc.getInventory().removeItem(item, 1L);
        pc.sendPacketsX8(new S_SkillSound(pc.getId(), 9714));
        try {
            pc.save();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.shop.Clan_Honor_Reel JD-Core Version: 0.6.2
 */