package com.lineage.data.item_etcitem.extra;

import com.add.Tsai.*;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 變身卡1
 *
 * @author hero
 */
public class Cards extends ItemExecutor {
    private static String[] CARD_HTML = new String[]{"card_02", "card_03", "card_04", "card_05", "card_06", "card_07",};
    private static Cards _instance;
    public boolean _isUseOK = false;
    private int _quest;
    private int _gfx;
    private int _itemid;

    public static ItemExecutor get() {
        return new Cards();
    }

    public static Cards getInstance() {
        if (_instance == null) {
            _instance = new Cards();
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
        int group = 0; // 顯示變身卡組合
        boolean ok = false; // 顯示變身卡組合
        for (int i = 0; i <= ACardTable.get().ACardSize(); i++) {//檢查變身卡DB資料
            final ACard card = ACardTable.get().getCard(i);//檢查變身卡DB資料
            if (card != null) {//檢查變身卡DB資料
                if (card.getQuestId() == _quest) {//檢查變身卡DB資料
                    if (!CardQuestTable.get().IsQuest(pc, card.getQuestId())) {//檢查變身卡DB資料
                        //pc.getQuest().set_end(card.getQuestId());
                        CardQuestTable.get().storeQuest(pc.getAccountName(), card.getQuestId(), null);//檢查變身卡DB資料
                        pc.getInventory().removeItem(item, 1L);//檢查變身卡DB資料
                        pc.sendPackets(new S_SystemMessage("你已成功登錄此卡片 請重新登入獲得能力加成 並享有此變身能力"));
                        pc.sendPacketsAll(new S_SkillSound(pc.getId(), _gfx));//檢查變身卡DB資料
                        ok = true;
                        group = card.getGroup();
                    } else {
                        pc.sendPackets(new S_SystemMessage("無法重複登錄卡片"));
                    }
                    break;
                }
            }
        }
        final StringBuilder stringBuilder = new StringBuilder(); // 顯示變身卡組合
        int size = ACardTable.get().ACardSize(); // 顯示變身卡組合
        for (int i = 0; i <= size; i++) { // 顯示變身卡組合
            final ACard card1 = ACardTable.get().getCard(i); // 顯示變身卡組合
            //獲取列表id
            if (card1 != null) {//列表ID空白不啟動
                if (CardQuestTable.get().IsQuest(pc, card1.getQuestId())) {
                    stringBuilder.append(String.valueOf(card1.getAddcgfxid())).append(",");
                } else {
                    stringBuilder.append(String.valueOf(card1.getAddhgfxid())).append(",");
                }
            }
        }

        //自動開吃卡後開啟卡冊邏輯
//        final String[] msg = stringBuilder.toString().split(",");//從0開始分裂以逗號為單位
////        if (group > 0) {
////            pc.sendPackets(new S_NPCTalkReturn(pc, getBookHtml(group), msg));
////        }
        if (ok) {
            for (int i = 0; i <= CardSetTable.get().CardCardSize(); i++) {//檢查變身組合DB資料
                final CardPolySet cards = CardSetTable.get().getCard(i);
                if (cards != null) {
                    int k = 0;
                    for (int j = 0; j < cards.getNeedQuest().length; j++) {
                        if (CardQuestTable.get().IsQuest(pc, cards.getNeedQuest()[j])) {
                            k++;
                        }
                    }
                    if (k == cards.getNeedQuest().length) {
                        //pc.getQuest().set_end(cards.getQuestId());
                        CardQuestTable.get().storeQuest(pc.getAccountName(), cards.getQuestId(), null);
                        pc.sendPackets(new S_SystemMessage("恭喜你啟動此變身卡套裝卡能力"));
                    }
                }
            }
        }
        pc.getInventory().storeItem(_itemid, 1);
        _isUseOK = ok;
    }

    private String getBookHtml(int group) { // 顯示變身卡組合
        if (group <= 0) { // 顯示變身卡組合
            return CARD_HTML[0];// 顯示變身卡組合
        }
        return CARD_HTML[group - 1];// 顯示變身卡組合
    }

    public void set_set(String[] set) {// 設定變身卡
        try {
            _quest = Integer.parseInt(set[1]); // 設定變身卡
        } catch (Exception localException) {// 設定變身卡
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

    public boolean checkQuest(L1PcInstance pc) {
        return CardQuestTable.get().IsQuest(pc, _quest);
    }
}