package com.lineage.data.item_etcitem.extra;

import com.add.Tsai.*;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 聖物1
 *
 * @author hero
 */
public class Holys extends ItemExecutor {
    private static String[] HOLY_HTML = new String[]{"Holy_D02", "Holy_D03", "Holy_D04", "Holy_D05", "Holy_D06", "Holy_D07", "Holy_D08",};
    private static Holys _instance;
    public boolean _isUseOK = false;
    private int _quest;
    private int _gfx;
    private int _itemid;

    public static ItemExecutor get() {
        return new Holys();
    }

    public static Holys getInstance() {
        if (_instance == null) {
            _instance = new Holys();
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
        int group = 0;
        boolean ok = false;
        for (int i = 0; i <= holyTable.get().holySize(); i++) {
            final holy holy = holyTable.get().getHoly(i);
            if (holy != null) {
                if (holy.getQuestId() == _quest) {
                    if (!dollQuestTable.get().IsQuest(pc, holy.getQuestId())) {
                        //pc.getQuest().set_end(card.getQuestId());
                        dollQuestTable.get().storeQuest(pc.getAccountName(), holy.getQuestId(), null);
                        pc.getInventory().removeItem(item, 1L);
                        pc.sendPackets(new S_SystemMessage("你已成功登錄此卡片 請重新登入獲得能力加成 並享有此聖物能力"));
                        pc.sendPacketsAll(new S_SkillSound(pc.getId(), _gfx));
                        group = holy.getGroup();
                        ok = true;
                    } else {
                        pc.sendPackets(new S_SystemMessage("無法重複登錄聖物卡片"));
                    }
                    break;
                }
            }
        }
        final StringBuilder stringBuilder = new StringBuilder();
        int size = holyTable.get().holySize();
        for (int i = 0; i <= size; i++) {
            final holy holy = holyTable.get().getHoly(i);
            //獲取列表id
            if (holy != null) {//列表ID空白不啟動
                if (dollQuestTable.get().IsQuest(pc, holy.getQuestId())) {
                    stringBuilder.append(String.valueOf(holy.getAddcgfxid())).append(",");
                } else {
                    stringBuilder.append(String.valueOf(holy.getAddhgfxid())).append(",");
                }
            }
        }
        final String[] msg = stringBuilder.toString().split(",");//從0開始分裂以逗號為單位
        if (group > 0) {
            pc.sendPackets(new S_NPCTalkReturn(pc, getBookHtml(group), msg));
        }
        if (ok) {
            for (int i = 0; i <= holySetTable.get().HolySize(); i++) {//檢查變身組合DB資料
                final holyPolySet holys = holySetTable.get().getHoly(i);
                if (holys != null) {
                    int k = 0;
                    for (int j = 0; j < holys.getNeedQuest().length; j++) {
                        if (dollQuestTable.get().IsQuest(pc, holys.getNeedQuest()[j])) {
                            k++;
                        }
                    }
                    if (k == holys.getNeedQuest().length) {
                        //pc.getQuest().set_end(cards.getQuestId());
                        dollQuestTable.get().storeQuest(pc.getAccountName(), holys.getQuestId(), null);
                        pc.sendPackets(new S_SystemMessage("恭喜你啟動此娃娃卡套裝卡能力"));
                    }
                }
            }
        }
        pc.getInventory().storeItem(_itemid, 1);
        _isUseOK = ok;
    }

    private String getBookHtml(int group) {
        if (group <= 0) {
            return HOLY_HTML[0];
        }
        return HOLY_HTML[group - 1];
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

    public boolean checkQuest(L1PcInstance pc) {
        return dollQuestTable.get().IsQuest(pc, _quest);
    }
}