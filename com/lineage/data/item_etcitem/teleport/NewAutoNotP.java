package com.lineage.data.item_etcitem.teleport;

import com.add.NewAuto.AutoAttack;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.Shutdown;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.L1Trade;
import com.lineage.server.serverpackets.S_CharTitle;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.timecontroller.server.ServerRestartTimer;
import com.lineage.server.world.World;

/**
 * 自動掛機 360207
 *
 * @author 老爹
 */
public class NewAutoNotP extends ItemExecutor {
    private static NewAutoNotP _instance;
    private int _x;
    private int _y;

    public static ItemExecutor get() {
        return new NewAutoNotP();
    }

    public static NewAutoNotP getInstance() {
        if (_instance == null) {
            _instance = new NewAutoNotP();
        }
        return _instance;
    }

    private static int AutoMapCount(int mapid) {
        int PcCount = 0;
        for (Object obj : World.get().getVisibleObjects(mapid).values()) {
            if (obj instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) obj;
                if (pc != null) {
                    PcCount++;
                }
            }
        }
        return PcCount;
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
        if (pc.getTradeID() != 0) {
            L1Trade trade = new L1Trade();
            trade.tradeCancel(pc);
        }
        if (!pc.Test_Auto()) {
            for (int i = 632; i <= 641; i++) {
                if (AutoMapCount(i) < 30) {
                    L1Teleport.teleport(pc, _x, _y, (short) i, 5, true);
                    break;
                }
            }
            AutoAttack auto = new AutoAttack(pc);
            auto.begin();
            pc.set_Test_Auto(true);
            // 封號
            StringBuilder title = new StringBuilder();
            title.append("***掛機中***");
            pc.setTitle(title.toString());
            pc.sendPacketsAll(new S_CharTitle(pc.getId(), title));
        } else if (pc.Test_Auto()) {
            pc.set_Test_Auto(false);
            final int nx = 33448;
            final int ny = 32796;
            L1Teleport.teleport(pc, nx, ny, (short) 4, 5, true);
            // 封號
            StringBuilder title = new StringBuilder();
            title.append(title);
            pc.setTitle(title.toString());
            pc.sendPacketsAll(new S_CharTitle(pc.getId(), title));
            pc.retitle(false);
        }
    }

    public void set_set(String[] set) {
        try {
            _x = Integer.parseInt(set[1]);
        } catch (Exception localException) {
        }
        try {
            _y = Integer.parseInt(set[2]);
        } catch (Exception localException) {
        }
    }
}