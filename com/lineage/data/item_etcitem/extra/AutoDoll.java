package com.lineage.data.item_etcitem.extra;

import com.add.Tsai.dollBookCmd;
import com.add.Tsai.holyBookCmd;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.Shutdown;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Trade;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.timecontroller.server.ServerRestartTimer;

/**
 * 自動招喚娃娃
 *
 * @author 老爹
 */
public class AutoDoll extends ItemExecutor {
    private static AutoDoll _instance;

    public static ItemExecutor get() {
        return new AutoDoll();
    }

    public static AutoDoll getInstance() {
        if (_instance == null) {
            _instance = new AutoDoll();
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
        if (pc.getLastDollId() == 0) {
            pc.sendPackets(new S_SystemMessage("尚未招喚過娃娃 請從卡冊中選擇娃娃"));
            return;
        }
        if (pc.getTradeID() != 0) {
            L1Trade trade = new L1Trade();
            trade.tradeCancel(pc);
        }
        pc.setDollId(pc.getLastDollId());
        dollBookCmd.get().polyDoll(pc);
        pc.setHolyId(pc.getLastHolyId2());
        holyBookCmd.get().polyHoly(pc);
    }
}