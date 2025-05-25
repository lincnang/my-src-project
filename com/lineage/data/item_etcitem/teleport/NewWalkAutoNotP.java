package com.lineage.data.item_etcitem.teleport;

import com.add.NewAuto.AutoAttackWalk;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.Shutdown;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Trade;
import com.lineage.server.serverpackets.S_CharTitle;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.timecontroller.server.ServerRestartTimer;

/**
 * @author 老爹
 */
public class NewWalkAutoNotP extends ItemExecutor {
    private static NewWalkAutoNotP _instance;

    public static ItemExecutor get() {
        return new NewWalkAutoNotP();
    }

    public static NewWalkAutoNotP getInstance() {
        if (_instance == null) {
            _instance = new NewWalkAutoNotP();
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
        Shutdown.getInstance();
        if (Shutdown.SHUTDOWN == true) {
            pc.sendPackets(new S_SystemMessage("伺服器即將關機 無法使用"));
            return;
        }
        if (!pc.getMap().isAutoBot()) {
            pc.sendPackets(new S_ServerMessage("\\aD 此地圖無法掛機.."));
            return;
        }
        if (pc.getTradeID() != 0) {
            L1Trade trade = new L1Trade();
            trade.tradeCancel(pc);
        }
        if (!pc.Test_Auto()) {
            AutoAttackWalk auto = new AutoAttackWalk(pc);
            auto.begin();
            pc.set_Test_Auto(true);
            // 封號
            StringBuilder title = new StringBuilder();
            title.append("***掛機中***");
            pc.setTitle(title.toString());
            pc.sendPacketsAll(new S_CharTitle(pc.getId(), title));
        } else if (pc.Test_Auto()) {
            pc.set_Test_Auto(false);
            // 封號
            StringBuilder title = new StringBuilder();
            title.append(title);
            pc.setTitle(title.toString());
            pc.sendPacketsAll(new S_CharTitle(pc.getId(), title));
            pc.retitle(false);
        }
    }
}