package com.lineage.data.item_etcitem.shop;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ExpTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_OwnCharStatus2;
import com.lineage.server.serverpackets.S_SkillSound;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NewLevelup extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(NewLevelup.class);
    private int _levelupto = 1;

    public static ItemExecutor get() {
        return new NewLevelup();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (item == null) {
            return;
        }
        if (pc == null) {
            return;
        }
        if (pc.getLevel() > 1) {
            return;
        }
        int level = this._levelupto;
        if (pc.getLevel() < level) {
            pc.addExp(ExpTable.getExpByLevel(level));
        }
        pc.getInventory().removeItem(item, 1L);
        pc.sendPackets(new S_OwnCharStatus(pc));
        pc.sendPackets(new S_OwnCharStatus2(pc));
        pc.sendPacketsX8(new S_SkillSound(pc.getId(), 9714));
        try {
            pc.save();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void set_set(String[] set) {
        try {
            this._levelupto = Integer.parseInt(set[1]);
        } catch (Exception localException) {
        }
    }
}