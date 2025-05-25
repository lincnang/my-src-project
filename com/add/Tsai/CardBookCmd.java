package com.add.Tsai;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
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
                        stringBuilder.append(String.valueOf(card1.getAddcgfxid()) + ",");//添加到字符串
                    } else {
                        stringBuilder.append(String.valueOf(card1.getAddhgfxid()) + ",");//添加到字符串
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

    private void CardAllSet(final L1PcInstance pc) { //顯示變身卡組合
        try {
            int str = 0; //力量
            int dex = 0;
            int con = 0;
            int Int = 0;
            int wis = 0;
            int cha = 0;
            int ac = 0;
            int hp = 0;
            int mp = 0;
            int hpr = 0;
            int mpr = 0;
            int dmg = 0;
            int bdmg = 0;
            int hit = 0;
            int bhit = 0;
            int dr = 0;
            int mdr = 0;
            int sp = 0;
            int mhit = 0;
            int mr = 0;
            int f = 0;
            int wind = 0;
            int w = 0;
            int e = 0;
            for (int i = 0; i <= ACardTable.get().ACardSize(); i++) { //檢查變身卡DB資料
                final ACard card = ACardTable.get().getCard(i); //檢查變身卡DB資料
                if (card != null) { //檢查變身卡DB資料
                    if (CardQuestTable.get().IsQuest(pc, card.getQuestId())) {
                        str += card.getAddStr();
                        dex += card.getAddDex();
                        con += card.getAddCon();
                        Int += card.getAddInt();
                        wis += card.getAddWis();
                        cha += card.getAddCha();
                        ac += card.getAddAc();
                        hp += card.getAddHp();
                        mp += card.getAddMp();
                        hpr += card.getAddHpr();
                        mpr += card.getAddMpr();
                        dmg += card.getAddDmg();
                        bdmg += card.getAddBowDmg();
                        hit += card.getAddHit();
                        bhit += card.getAddBowHit();
                        dr += card.getAddDmgR();
                        mdr += card.getAddMagicDmgR();
                        sp += card.getAddSp();
                        mhit += card.getAddMagicHit();
                        mr += card.getAddMr();
                        f += card.getAddFire();
                        wind += card.getAddWind();
                        e += card.getAddEarth();
                        w += card.getAddWater();
                    }
                }
            }
            for (int i = 0; i <= CardSetTable.get().CardCardSize(); i++) {//檢查變身組合DB資料
                final CardPolySet cards = CardSetTable.get().getCard(i);
                if (cards != null) {
                    if (CardQuestTable.get().IsQuest(pc, cards.getQuestId())) {
                        str += cards.getAddStr();
                        dex += cards.getAddDex();
                        con += cards.getAddCon();
                        Int += cards.getAddInt();
                        wis += cards.getAddWis();
                        cha += cards.getAddCha();
                        ac += cards.getAddAc();
                        hp += cards.getAddHp();
                        mp += cards.getAddMp();
                        hpr += cards.getAddHpr();
                        mpr += cards.getAddMpr();
                        dmg += cards.getAddDmg();
                        bdmg += cards.getAddBowDmg();
                        hit += cards.getAddHit();
                        bhit += cards.getAddBowHit();
                        dr += cards.getAddDmgR();
                        mdr += cards.getAddMagicDmgR();
                        sp += cards.getAddSp();
                        mhit += cards.getAddMagicHit();
                        mr += cards.getAddMr();
                        f += cards.getAddFire();
                        wind += cards.getAddWind();
                        e += cards.getAddEarth();
                        w += cards.getAddWater();
                    }
                }
            }
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("力量 +" + str + ",");
            stringBuilder.append("敏捷 +" + dex + ",");
            stringBuilder.append("體質 +" + con + ",");
            stringBuilder.append("智力 +" + Int + ",");
            stringBuilder.append("精神 +" + wis + ",");
            stringBuilder.append("魅力 +" + cha + ",");
            stringBuilder.append("防禦提升 +" + ac + ",");
            stringBuilder.append("HP +" + hp + ",");
            stringBuilder.append("MP +" + mp + ",");
            stringBuilder.append("血量回復 +" + hpr + ",");
            stringBuilder.append("魔力回復 +" + mpr + ",");
            stringBuilder.append("近距離傷害 +" + dmg + ",");
            stringBuilder.append("遠距離傷害 +" + bdmg + ",");
            stringBuilder.append("近距離命中 +" + hit + ",");
            stringBuilder.append("遠距離命中 +" + bhit + ",");
            stringBuilder.append("物理傷害減免 +" + dr + ",");
            stringBuilder.append("魔法傷害減免 +" + mdr + ",");
            stringBuilder.append("魔攻 +" + sp + ",");
            stringBuilder.append("魔法命中 +" + mhit + ",");
            stringBuilder.append("魔法防禦 +" + mr + ",");
            stringBuilder.append("火屬性防禦 +" + f + ",");
            stringBuilder.append("風屬性防禦 +" + wind + ",");
            stringBuilder.append("地屬性防禦 +" + e + ",");
            stringBuilder.append("水屬性防禦 +" + w + ",");
            final String[] clientStrAry = stringBuilder.toString().split(",");
            pc.sendPackets(new S_NPCTalkReturn(pc, "card_11", clientStrAry));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
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
                            stringBuilder.append(card.getAddcgfxid() + ",");    // 0
                        } else {
                            stringBuilder.append(card.getAddhgfxid() + ",");    // 0
                        }
                        stringBuilder.append(card.getMsg2() + ",");//1
                        stringBuilder.append(card.getMsg1() + ",");//2


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