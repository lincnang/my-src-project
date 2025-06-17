package com.add.Tsai;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 卡冊指令
 *
 * @author hero
 */
public class CardBookCmd {
    private static final Log _log = LogFactory.getLog(CardBookCmd.class);
    private static CardBookCmd _instance;

    public static CardBookCmd get() {
        if (_instance == null) {
            _instance = new CardBookCmd();
        }
        return _instance;
    }

    public boolean Cmd(final L1PcInstance pc, final String cmd) {
        try {
            final StringBuilder stringBuilder = new StringBuilder();//建立一個新的字符串
            int size = ACardTable.get().ACardSize();//獲取卡片數量
            for (int i = 0; i <= size; i++) {//循環
                final ACard card1 = ACardTable.get().getCard(i);//獲取卡片
                //獲取列表id
                if (card1 != null) {//列表ID空白不啟動
                    if (CardQuestTable.get().IsQuest(pc, card1.getQuestId())) {//檢查任務
                        stringBuilder.append(String.valueOf(card1.getAddcgfxid())).append(",");//添加到字符串
                    } else {
                        stringBuilder.append(String.valueOf(card1.getAddhgfxid())).append(",");//添加到字符串
                    }
                }
            }
            final String[] msg = stringBuilder.toString().split(",");//從0開始分裂以逗號為單位
            boolean ok = false;
            switch (cmd) {
                case "C_1":
                    ok = true;
                    pc.setCarId(-pc.getCardId());
                    pc.sendPackets(new S_NPCTalkReturn(pc, "card_02", msg));
                    break;
                case "C_2":
                    pc.sendPackets(new S_NPCTalkReturn(pc, "card_03", msg));
                    ok = true;
                    pc.setCarId(-pc.getCardId());
                    break;
                case "C_3":
                    pc.sendPackets(new S_NPCTalkReturn(pc, "card_04", msg));
                    ok = true;
                    pc.setCarId(-pc.getCardId());
                    break;
                case "C_4":
                    pc.sendPackets(new S_NPCTalkReturn(pc, "card_05", msg));
                    ok = true;
                    pc.setCarId(-pc.getCardId());
                    break;
                case "C_5":
                    pc.sendPackets(new S_NPCTalkReturn(pc, "card_06", msg));
                    ok = true;
                    pc.setCarId(-pc.getCardId());
                    break;
                case "C_6":
                    pc.sendPackets(new S_NPCTalkReturn(pc, "card_07", msg));
                    ok = true;
                    pc.setCarId(-pc.getCardId());
                    break;
                case "C_7":
                    pc.sendPackets(new S_NPCTalkReturn(pc, "card_08", msg));
                    ok = true;
                    pc.setCarId(-pc.getCardId());
                    break;
                case "polycard": //變身
                    ACard card = ACardTable.get().getCard(pc.getCardId()); //檢查變身卡DB資料
                    if (card != null) { //檢查變身卡DB資料
                        if (CardQuestTable.get().IsQuest(pc, card.getQuestId())) {
                            if (card.getPolyId() != 0) {
                                if (card.getPolyItemId() != 0) {
                                    if (pc.getInventory().checkItem(card.getPolyItemId(), card.getPolyItemCount())) {
                                        L1PolyMorph.doPoly(pc, card.getPolyId(), card.getPolyTime(), L1PolyMorph.MORPH_BY_ITEMMAGIC);
                                        pc.getInventory().consumeItem(card.getPolyItemId(), card.getPolyItemCount());
                                    } else {
                                        pc.sendPackets(new S_SystemMessage("變身需求道具不足"));
                                    }
                                } else {
                                    L1PolyMorph.doPoly(pc, card.getPolyId(), card.getPolyTime(), L1PolyMorph.MORPH_BY_ITEMMAGIC);
                                }
                            } else {
                                pc.sendPackets(new S_SystemMessage("無法變身"));
                            }
                        }
                    }
                    ok = true;
                    break;
                case "cardset":
                    int questId = ACardTable.get().getQuestIdByCardId(pc.getCardId());
                    CardSet(pc, questId);//組合變身卡
                    ok = true;
                    break;
                case "cardset2":
                    CardAllSet(pc);//已加成所有能力
                    ok = true;
                    break;
            }
            if (ok) {
                return true;
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    public void CardAllSet(final L1PcInstance pc) { //顯示變身卡組合
        try {
            // 屬性名稱陣列
            String[] attrs = { "力量", "敏捷", "體質", "智力", "精神", "魅力", "防禦提升", "HP", "MP", "血量回復", "魔力回復", "近距離傷害", "遠距離傷害", "近距離命中", "遠距離命中", "物理傷害減免", "魔法傷害減免", "魔攻", "魔法命中", "魔法防禦", "火屬性防禦", "風屬性防禦", "地屬性防禦", "水屬性防禦" };
            // 紀錄最大值/已解鎖值
            Map<String, Integer> maxMap = new LinkedHashMap<>();
            Map<String, Integer> playerMap = new LinkedHashMap<>();
            for (String attr : attrs) {
                maxMap.put(attr, 0);
                playerMap.put(attr, 0);
            }

            // 1. 計算所有組合卡最大值
            for (int i = 0; i <= CardSetTable.get().CardCardSize(); i++) {
                final CardPolySet cards = CardSetTable.get().getCard(i);
                if (cards == null) continue;
                maxMap.put("力量", maxMap.get("力量") + cards.getAddStr());
                maxMap.put("敏捷", maxMap.get("敏捷") + cards.getAddDex());
                maxMap.put("體質", maxMap.get("體質") + cards.getAddCon());
                maxMap.put("智力", maxMap.get("智力") + cards.getAddInt());
                maxMap.put("精神", maxMap.get("精神") + cards.getAddWis());
                maxMap.put("魅力", maxMap.get("魅力") + cards.getAddCha());
                maxMap.put("防禦提升", maxMap.get("防禦提升") + cards.getAddAc());
                maxMap.put("HP", maxMap.get("HP") + cards.getAddHp());
                maxMap.put("MP", maxMap.get("MP") + cards.getAddMp());
                maxMap.put("血量回復", maxMap.get("血量回復") + cards.getAddHpr());
                maxMap.put("魔力回復", maxMap.get("魔力回復") + cards.getAddMpr());
                maxMap.put("近距離傷害", maxMap.get("近距離傷害") + cards.getAddDmg());
                maxMap.put("遠距離傷害", maxMap.get("遠距離傷害") + cards.getAddBowDmg());
                maxMap.put("近距離命中", maxMap.get("近距離命中") + cards.getAddHit());
                maxMap.put("遠距離命中", maxMap.get("遠距離命中") + cards.getAddBowHit());
                maxMap.put("物理傷害減免", maxMap.get("物理傷害減免") + cards.getAddDmgR());
                maxMap.put("魔法傷害減免", maxMap.get("魔法傷害減免") + cards.getAddMagicDmgR());
                maxMap.put("魔攻", maxMap.get("魔攻") + cards.getAddSp());
                maxMap.put("魔法命中", maxMap.get("魔法命中") + cards.getAddMagicHit());
                maxMap.put("魔法防禦", maxMap.get("魔法防禦") + cards.getAddMr());
                maxMap.put("火屬性防禦", maxMap.get("火屬性防禦") + cards.getAddFire());
                maxMap.put("風屬性防禦", maxMap.get("風屬性防禦") + cards.getAddWind());
                maxMap.put("地屬性防禦", maxMap.get("地屬性防禦") + cards.getAddEarth());
                maxMap.put("水屬性防禦", maxMap.get("水屬性防禦") + cards.getAddWater());

                // 已解鎖的才加到玩家
                if (CardQuestTable.get().IsQuest(pc, cards.getQuestId())) {
                    playerMap.put("力量", playerMap.get("力量") + cards.getAddStr());
                    playerMap.put("敏捷", playerMap.get("敏捷") + cards.getAddDex());
                    playerMap.put("體質", playerMap.get("體質") + cards.getAddCon());
                    playerMap.put("智力", playerMap.get("智力") + cards.getAddInt());
                    playerMap.put("精神", playerMap.get("精神") + cards.getAddWis());
                    playerMap.put("魅力", playerMap.get("魅力") + cards.getAddCha());
                    playerMap.put("防禦提升", playerMap.get("防禦提升") + cards.getAddAc());
                    playerMap.put("HP", playerMap.get("HP") + cards.getAddHp());
                    playerMap.put("MP", playerMap.get("MP") + cards.getAddMp());
                    playerMap.put("血量回復", playerMap.get("血量回復") + cards.getAddHpr());
                    playerMap.put("魔力回復", playerMap.get("魔力回復") + cards.getAddMpr());
                    playerMap.put("近距離傷害", playerMap.get("近距離傷害") + cards.getAddDmg());
                    playerMap.put("遠距離傷害", playerMap.get("遠距離傷害") + cards.getAddBowDmg());
                    playerMap.put("近距離命中", playerMap.get("近距離命中") + cards.getAddHit());
                    playerMap.put("遠距離命中", playerMap.get("遠距離命中") + cards.getAddBowHit());
                    playerMap.put("物理傷害減免", playerMap.get("物理傷害減免") + cards.getAddDmgR());
                    playerMap.put("魔法傷害減免", playerMap.get("魔法傷害減免") + cards.getAddMagicDmgR());
                    playerMap.put("魔攻", playerMap.get("魔攻") + cards.getAddSp());
                    playerMap.put("魔法命中", playerMap.get("魔法命中") + cards.getAddMagicHit());
                    playerMap.put("魔法防禦", playerMap.get("魔法防禦") + cards.getAddMr());
                    playerMap.put("火屬性防禦", playerMap.get("火屬性防禦") + cards.getAddFire());
                    playerMap.put("風屬性防禦", playerMap.get("風屬性防禦") + cards.getAddWind());
                    playerMap.put("地屬性防禦", playerMap.get("地屬性防禦") + cards.getAddEarth());
                    playerMap.put("水屬性防禦", playerMap.get("水屬性防禦") + cards.getAddWater());
                }
            }

            // 2. 組合顯示
            StringBuilder sb = new StringBuilder();
            for (String attr : attrs) {
                int now = playerMap.get(attr);
                int max = maxMap.get(attr);
                if (max == 0) continue;
                sb.append(attr).append(" +").append(now)
                        .append(" (").append(now).append("/").append(max).append("),");
            }
            final String[] clientStrAry = sb.toString().split(",");
            pc.sendPackets(new S_NPCTalkReturn(pc, "card_11", clientStrAry));
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }


    private void CardSet(final L1PcInstance pc, final int questId) {
        try {
            final StringBuilder stringBuilder = new StringBuilder();
            CardSetTable cardSetTable = CardSetTable.get();

            for (int i = 0; i < cardSetTable.CardCardSize(); i++) {
                final CardPolySet cards = cardSetTable.getCard(i);
                if (cards != null && cards.hasGroup()) {
                    boolean anyTaskUncompleted = false;

                    if (!cards.hasNeedQuest(questId))
                        continue;
                    ;

                    stringBuilder.append(cards.getMsg1()).append(",");

                    for (int j = 0; j < cards.getNeedQuest().length; j++) {
                        if (!CardQuestTable.get().IsQuest(pc, cards.getNeedQuest()[j])) {
                            anyTaskUncompleted = true;
                            stringBuilder.append(cards.getNeedName()[j]).append("(未開啟),");
                        }
                    }

                    if (anyTaskUncompleted) {
                        appendStats(stringBuilder, cards);
                    } else {

                        int lastCommaIndex = stringBuilder.lastIndexOf(cards.getMsg1() + ",");
                        stringBuilder.delete(lastCommaIndex, stringBuilder.length());
                    }
                }
            }

            if (stringBuilder.length() > 0) {
                final String[] clientStrAry = stringBuilder.toString().split(",");
                pc.sendPackets(new S_NPCTalkReturn(pc, "card_10", clientStrAry));
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }


    private void appendStats(StringBuilder stringBuilder, CardPolySet cards) {
        Map<String, Integer> attributes = new HashMap<>();
        attributes.put("力量", cards.getAddStr());
        attributes.put("敏捷", cards.getAddDex());
        attributes.put("體質", cards.getAddCon());
        attributes.put("智力", cards.getAddInt());
        attributes.put("精神", cards.getAddWis());
        attributes.put("魅力", cards.getAddCha());
        attributes.put("防禦提升", cards.getAddAc());
        attributes.put("HP", cards.getAddHp());
        attributes.put("MP", cards.getAddMp());
        attributes.put("血量回復", cards.getAddHpr());
        attributes.put("魔力回復", cards.getAddMpr());
        attributes.put("近距離傷害", cards.getAddDmg());
        attributes.put("遠距離傷害", cards.getAddBowDmg());
        attributes.put("遠距離命中", cards.getAddHit());
        attributes.put("遠距離命中", cards.getAddBowHit());
        attributes.put("物理傷害減免", cards.getAddDmgR());
        attributes.put("魔法傷害減免", cards.getAddMagicDmgR());
        attributes.put("魔攻", cards.getAddSp());
        attributes.put("魔法命中", cards.getAddMagicHit());
        attributes.put("魔法防禦", cards.getAddMr());
        attributes.put("火屬性防禦", cards.getAddFire());
        attributes.put("風屬性防禦", cards.getAddWind());
        attributes.put("地屬性防禦", cards.getAddEarth());
        attributes.put("水屬性防禦", cards.getAddWater());

        for (Map.Entry<String, Integer> attribute : attributes.entrySet()) {
            if (attribute.getValue() != 0) {
                stringBuilder.append(attribute.getKey()).append(" +").append(attribute.getValue()).append(",");
            }
        }
        stringBuilder.append("<---------------------------->,");
    }

    public boolean PolyCmd(final L1PcInstance pc, final String cmd) { //變身卡指令
        try {//變身卡指令
            boolean ok = false;//變身卡指令
            final StringBuilder stringBuilder = new StringBuilder();//建立一個新的字符串
            for (int i = 0; i <= ACardTable.get().ACardSize(); i++) {//檢查變身卡DB資料

                final ACard card = ACardTable.get().getCard(i);//獲取卡片
                if (card != null) {//檢查變身卡DB資料
                    if (cmd.equals(card.getCmd())) {//檢查變身卡DB資料
                        pc.setCarId(i);

                        if (CardQuestTable.get().IsQuest(pc, card.getQuestId())) {
                            stringBuilder.append(card.getAddcgfxid()).append(",");    // 0
                        } else {
                            stringBuilder.append(card.getAddhgfxid()).append(",");    // 0
                        }
                        stringBuilder.append(card.getMsg2()).append(",");//1
                        stringBuilder.append(card.getMsg1()).append(",");//2


                        final String[] clientStrAry = stringBuilder.toString().split(",");//從0開始分裂以逗號為單位
                        pc.sendPackets(new S_NPCTalkReturn(pc, "card_0", clientStrAry));
                        ok = true;
                        break;
                    }
                }
            }
            if (ok) {
                return true;
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }
}