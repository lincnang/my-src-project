package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.Shutdown;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Trade;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.timecontroller.server.ServerRestartTimer;

/**
 * 轉生點數
 *
 * @author 老爹
 */
public class TalnetSkill_add extends ItemExecutor {
    private static TalnetSkill_add _instance;

    public static ItemExecutor get() {
        return new TalnetSkill_add();
    }

    public static TalnetSkill_add getInstance() {
        if (_instance == null) {
            _instance = new TalnetSkill_add();
        }
        return _instance;
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (item == null) {
            return;
        }
        if (pc == null) {
            return;
        }
        if (pc.isPrivateShop()) {
            return;
        }
        if (pc.isFishing()) {
            return;
        }
        if (ServerRestartTimer.isRtartTime()) {
            pc.sendPackets(new S_SystemMessage("伺服器即將關機 無法使用"));
            return;
        }
        if (Shutdown.SHUTDOWN == true) {
            pc.sendPackets(new S_SystemMessage("伺服器即將關機 無法使用"));
            return;
        }
        if (pc.getTradeID() != 0) {
            L1Trade trade = new L1Trade();
            trade.tradeCancel(pc);
        }
        pc.getInventory().consumeItem(item.getItemId(), 1);
        pc.setTurnLifeSkillCount(pc.getTurnLifeSkillCount() + 1);
        pc.sendPackets(new S_SystemMessage("\\aG技能熟練度增加"));
    }
}