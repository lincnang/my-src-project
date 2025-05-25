package com.lineage.server.command.executor;

import com.lineage.server.datatables.ShopCnTable;
import com.lineage.server.datatables.ShopTable;
import com.lineage.server.datatables.T_GameMallTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 商店資料重置資料庫
 *
 * @author dexc
 */
public class L1ReloadShop implements L1CommandExecutor {
    private L1ReloadShop() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1ReloadShop();
    }

    @Override
    public void execute(L1PcInstance paramL1PcInstance, String paramString1, String paramString2) {
        ShopTable.get().restshop();
        ShopCnTable.get().restshopCn();
        T_GameMallTable.get().restGameMall();  //src036
        paramL1PcInstance.sendPackets(new S_SystemMessage("[shop]+[shop_cn]+[t_game_mall]資料庫已重讀完成!"));
    }
}
