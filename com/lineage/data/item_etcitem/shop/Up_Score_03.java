package com.lineage.data.item_etcitem.shop;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Up_Score_03 extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(Up_Score_03.class);

    public static ItemExecutor get() {
        return new Up_Score_03();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        try {
            if (item == null) {
                return;
            }
            if (pc == null) {
                return;
            }
            if (pc.hasSkillEffect(7002)) {
                pc.sendPackets(new S_ServerMessage("\\fR積分加倍(2倍)作用中!"));
                return;
            }
            if (pc.hasSkillEffect(7003)) {
                pc.sendPackets(new S_ServerMessage("\\fR積分加倍(3倍)作用中!"));
                return;
            }
            if (pc.getInventory().removeItem(item, 1L) != 1L) {
                return;
            }
            pc.sendPackets(new S_ServerMessage("\\fR積分加倍(3倍)啟動!"));
            pc.setSkillEffect(7003, 1200000);
            pc.sendPacketsX8(new S_SkillSound(pc.getId(), 9714));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.shop.Up_Score_03 JD-Core Version: 0.6.2
 */