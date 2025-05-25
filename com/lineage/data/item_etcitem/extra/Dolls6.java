package com.lineage.data.item_etcitem.extra;

import com.add.Tsai.*;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 娃娃卡6
 *
 * @author hero
 */
public class Dolls6 extends ItemExecutor {
    private static Dolls6 _instance;
    private int _quest;
    private int _gfx;
    private int _itemid;

    public static ItemExecutor get() {
        return new Dolls6();
    }

    public static Dolls6 getInstance() {
        if (_instance == null) {
            _instance = new Dolls6();
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
        boolean ok = false;
        for (int i = 0; i <= dollTable.get().dollSize(); i++) {
            final doll doll = dollTable.get().getDoll(i);
            if (doll != null) {
                if (doll.getQuestId() == _quest) {
                    if (dollQuestTable.get().IsQuest(pc, doll.getQuestId())) {
                        //pc.getQuest().set_end(card.getQuestId());
                        dollQuestTable.get().storeQuest(pc.getAccountName(), doll.getQuestId(), null);
                        pc.getInventory().removeItem(item, 1L);
                        pc.sendPackets(new S_SystemMessage("你已成功登錄此卡片 請重新登入獲得能力加成 並享有此娃娃能力"));
                        pc.sendPacketsAll(new S_SkillSound(pc.getId(), _gfx));
                        ok = true;
                    } else {
                        pc.sendPackets(new S_SystemMessage("無法重複登錄娃娃卡片"));
                    }
                    break;
                }
            }
        }
        final StringBuilder stringBuilder = new StringBuilder();
        int size = dollTable.get().dollSize();
        for (int i = 0; i <= size; i++) {
            final doll doll = dollTable.get().getDoll(i);
            //獲取列表id
            if (doll != null) {//列表ID空白不啟動
                if (dollQuestTable.get().IsQuest(pc, doll.getQuestId())) {
                    stringBuilder.append(String.valueOf(doll.getAddcgfxid())).append(",");
                } else {
                    stringBuilder.append(String.valueOf(doll.getAddhgfxid())).append(",");
                }
            }
        }
        final String[] msg = stringBuilder.toString().split(",");//從0開始分裂以逗號為單位
        pc.sendPackets(new S_NPCTalkReturn(pc, "Book_07", msg));
        if (ok) {
            for (int i = 0; i <= dollSetTable.get().CardDollSize(); i++) {//檢查變身組合DB資料
                final dollPolySet dolls = dollSetTable.get().getDoll(i);
                if (dolls != null) {
                    int k = 0;
                    for (int j = 0; j < dolls.getNeedQuest().length; j++) {
                        if (dollQuestTable.get().IsQuest(pc, dolls.getNeedQuest()[j])) {
                            k++;
                        }
                    }
                    if (k == dolls.getNeedQuest().length) {
                        //pc.getQuest().set_end(cards.getQuestId());
                        dollQuestTable.get().storeQuest(pc.getAccountName(), dolls.getQuestId(), null);
                        pc.sendPackets(new S_SystemMessage("恭喜你啟動此娃娃卡套裝卡能力"));
                    }
                }
            }
        }
        pc.getInventory().storeItem(_itemid, 1);
    }

    public void set_set(String[] set) {
        try {
            _quest = Integer.parseInt(set[1]);
        } catch (Exception localException) {
        }
        try {
            _gfx = Integer.parseInt(set[2]);
        } catch (Exception localException) {
        }
        try {
            _itemid = Integer.parseInt(set[3]);
        } catch (Exception localException) {
        }
    }
}