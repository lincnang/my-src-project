package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 變身卡冊
 *
 * @author hero
 */
public class CardBook extends ItemExecutor {
    private static CardBook _instance;

    public static ItemExecutor get() {
        return new CardBook();
    }

    public static CardBook getInstance() {
        if (_instance == null) {
            _instance = new CardBook();
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
            pc.sendPackets(new S_SystemMessage("擺攤狀態下 無法使用"));
            return;
        }
        if (pc.isFishing()) {
            pc.sendPackets(new S_SystemMessage("釣魚狀態下 無法使用"));
            return;
        }
        pc.sendPackets(new S_NPCTalkReturn(pc, "card_01"));
    }
}