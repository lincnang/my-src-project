package com.lineage.server.Controller;

import com.lineage.config.ConfigOtherSet2;
import com.lineage.server.datatables.lock.AccountReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.serverpackets.S_TamWindow;
import com.lineage.server.templates.L1Account;
import com.lineage.server.world.World;

/**
 * 成長果實系統(Tam幣)
 */
public class TamController implements Runnable {
    //public static final int SLEEP_TIME = ConfigOther.Tam_Time * 60 * 1000; // 分鐘
    public static final int SLEEP_TIME = ConfigOtherSet2.Tam_Time * 1000; // 秒
    private static TamController _instance;

    public static TamController getInstance() {
        if (_instance == null) {
            _instance = new TamController();
        }
        return _instance;
    }

    @Override
    public void run() {
        try {
            PremiumTime();
        } catch (Exception e) {
        }
    }

    private void PremiumTime() {
        for (L1PcInstance pc : World.get().getAllPlayers()) {
            if (!pc.isPrivateShop() && pc != null && !pc.isDead()) {
                int tamcount = pc.tamcount();
                if (tamcount > 0) {
                    int addtam = ConfigOtherSet2.TAM_COUNT * tamcount;
                    L1Account account = pc.getNetConnection().getAccount();
                    account.set_tam_point(account.get_tam_point() + addtam);
                    try {
                        AccountReading.get().updatetam(pc.getAccountName(), account.get_tam_point());
                    } catch (Exception e) {
                    }
                    if (ConfigOtherSet2.Tam_Msg) {
                        //pc.sendPackets(new S_SystemMessage("\\aA通知：成長鏈(" + tamcount + ")段階，Tam幣\\aG(" + addtam + ")\\aA個獲得。"));
                        //pc.sendPackets(new S_SystemMessage("\\aA獲得Tam幣\\aG(" + addtam + ")\\aA個,目前賬戶Tam幣有\\aG"+ account.get_tam_point() +"\\aA個。"));
                        pc.sendPackets(new S_SystemMessage("\\aA恭喜獲得Tam幣\\aG(" + addtam + ")\\aA個。"));
                    }
                    try {
                        pc.sendPackets(new S_TamWindow(pc));
                    } catch (Exception e) {
                    }
                }
            }
        }
    }
}
