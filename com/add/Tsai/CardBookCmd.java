package com.add.Tsai;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.serverpackets.S_Sound;
import com.lineage.server.thread.GeneralThreadPool;
// import placeholders removed
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

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
            // 載入覺醒設定與玩家進度
            try {
                CardAwakenConfigTable.get().load();
                CardAwakenProgressTable.get().load();
            } catch (Exception ignored) {
            }
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
                case "polycard": // 變身
                    final ACard card = ACardTable.get().getCard(pc.getCardId());
                    if (card != null) {
                        if (CardQuestTable.get().IsQuest(pc, card.getQuestId())) {
                            if (card.getPolyId() != 0) {
                                boolean doPoly = false;
                                // 覺醒覆寫：若玩家已覺醒，套用覺醒後 polyId 與時效加秒
                                int usePolyId = card.getPolyId();
                                int usePolyTime = card.getPolyTime();
                                int awakenedStageNow = getPlayerAwakenStage(pc, pc.getCardId());
                                if (awakenedStageNow > 0) {
                                    CardAwakenConfig acfg = CardAwakenConfigTable.get().getConfig(pc.getCardId(), awakenedStageNow);
                                    if (acfg != null) {
                                        Integer awPoly = acfg.getAwakenPolyId();
                                        if (awPoly != null && awPoly > 0) {
                                            usePolyId = awPoly;
                                        }
                                        usePolyTime = usePolyTime + Math.max(0, acfg.getAddPolyTimeSec());
                                    }
                                }
                                // 需要消耗道具
                                if (card.getPolyItemId() != 0 && card.getPolyItemCount() > 0) {
                                    if (pc.getInventory().checkItem(card.getPolyItemId(), card.getPolyItemCount())) {
                                        L1PolyMorph.doPoly(pc, usePolyId, usePolyTime, L1PolyMorph.MORPH_BY_ITEMMAGIC);
                                        pc.getInventory().consumeItem(card.getPolyItemId(), card.getPolyItemCount());
                                        doPoly = true;
                                    } else {
                                        pc.sendPackets(new S_SystemMessage("變身需求道具不足"));
                                    }
                                } else {
                                    // 不需要道具，直接變身
                                    L1PolyMorph.doPoly(pc, usePolyId, usePolyTime, L1PolyMorph.MORPH_BY_ITEMMAGIC);
                                    doPoly = true;
                                }
                                if (doPoly) {
                                    pc.setLastPolyCardId(usePolyId);
                                    new com.lineage.server.storage.mysql.MySqlCharacterStorage().storeCharacter(pc);
                                    pc.sendPackets(new S_SystemMessage("已自動記錄此變身卡為自動變身卡！"));

                                    // === 啟動自動續變身排程 ===
                                    final L1PcInstance refPc = pc;
                                    GeneralThreadPool.get().schedule(new Runnable() {
                                        @Override
                                        public void run() {
                                            // 續變身也要檢查條件與道具
                                            if (refPc != null && !refPc.isDead() && !refPc.hasSkillEffect(L1SkillId.SHAPE_CHANGE)) {
                                                // 這裡再次取得自動變身卡（支援切換、升級）
                                                int lastPolyId = refPc.getLastPolyCardId();
                                                ACard autoCard = ACardTable.get().getCardByPolyId(lastPolyId);
                                                if (autoCard != null && CardQuestTable.get().IsQuest(refPc, autoCard.getQuestId())) {
                                                    boolean canPoly = false;
                                                    if (autoCard.getPolyItemId() != 0 && autoCard.getPolyItemCount() > 0) {
                                                        if (refPc.getInventory().checkItem(autoCard.getPolyItemId(), autoCard.getPolyItemCount())) {
                                                            L1PolyMorph.doPoly(refPc, autoCard.getPolyId(), autoCard.getPolyTime(), L1PolyMorph.MORPH_BY_ITEMMAGIC);
                                                            refPc.getInventory().consumeItem(autoCard.getPolyItemId(), autoCard.getPolyItemCount());
                                                            canPoly = true;
                                                            refPc.sendPackets(new S_SystemMessage("自動續變身完成(消耗道具)"));
                                                        } else {
                                                            refPc.sendPackets(new S_SystemMessage("自動續變身失敗，背包道具不足"));
                                                        }
                                                    } else {
                                                        L1PolyMorph.doPoly(refPc, autoCard.getPolyId(), autoCard.getPolyTime(), L1PolyMorph.MORPH_BY_ITEMMAGIC);
                                                        canPoly = true;
                                                        refPc.sendPackets(new S_SystemMessage("自動續變身完成"));
                                                    }
                                                    // 再次成功才再排下一次
                                                    if (canPoly) {
                                                        GeneralThreadPool.get().schedule(this, autoCard.getPolyTime() * 1000);
                                                    }
                                                }
                                            }
                                        }
                                    }, card.getPolyTime() * 1000);
                                }
                            } else {
                                pc.sendPackets(new S_SystemMessage("無法變身"));
                            }
                        } else {
                            pc.sendPackets(new S_SystemMessage("尚未開啟卡片、請先解鎖"));
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
                case "card_awaken":
                    openAwaken(pc);
                    ok = true;
                    break;
                default:
                    // 覺醒餵養：card_awaken_feed_1 / card_awaken_feed_10 / card_awaken_feed_99...
                    if (cmd.startsWith("card_awaken_feed_")) {
                        int count = 1;
                        try {
                            count = Integer.parseInt(cmd.substring("card_awaken_feed_".length()));
                        } catch (Exception ignored) {}
                        feedAwaken(pc, count);
                        ok = true;
                        break;
                    }
                    if (cmd.equals("card_awaken_try")) {
                        tryAwaken(pc);
                        ok = true;
                        break;
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


    private int getPlayerAwakenStage(final L1PcInstance pc, final int cardId) {
        CardAwakenProgress p = CardAwakenProgressTable.get().get(pc.getAccountName(), cardId);
        return p == null ? 0 : p.getStage();
    }

    private void openAwaken(final L1PcInstance pc) {
        final int cardId = pc.getCardId();
        CardAwakenProgress prog = CardAwakenProgressTable.get().get(pc.getAccountName(), cardId);
        if (prog == null) prog = new CardAwakenProgress(pc.getAccountName(), cardId, 0, 0);
        // 若已達最高階段，直接提示並返回
        int maxStage = CardAwakenConfigTable.get().getMaxStage(cardId);
        if (maxStage > 0 && prog.getStage() >= maxStage) {
            pc.sendPackets(new S_SystemMessage("此卡片已經完成覺醒"));
            return;
        }
        final int nextStage = prog.getStage() + 1; // 下一階設定頁
        CardAwakenConfig cfg = CardAwakenConfigTable.get().getConfig(cardId, nextStage);
        if (cfg == null) {
            pc.sendPackets(new S_SystemMessage("此卡片目前沒有覺醒設定"));
            return;
        }

        String title = cfg.getTitle();
        String desc = cfg.getDescription();

        // #1 彩色圖片（取系統_變身卡冊的彩圖ID）
        String colorGfx = "0";
        ACard baseCard = ACardTable.get().getCard(cardId);
        if (baseCard != null && baseCard.getAddcgfxid() != null) {
            colorGfx = baseCard.getAddcgfxid();
        }

        // 準備參數
        List<String> args = new ArrayList<>();
        // #0 標題
        args.add(title == null ? "" : title);
        // #1 彩色圖片ID
        args.add(colorGfx);
        // #2-#10 說明（以逗號分割）
        if (desc != null && !desc.isEmpty()) {
            String[] parts = desc.split("[，,]");
            for (String p : parts) {
                if (p != null && !p.isEmpty()) args.add(p.trim());
            }
        }
        while (args.size() < 11) args.add("");

        // #11 覺醒 EXP 文字進度條
        int percentVal = Math.max(0, Math.min(100, prog.getExp()));
        int filledBlocks = percentVal / 10; // 0..10
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < filledBlocks; i++) bar.append('■');
        for (int i = filledBlocks; i < 10; i++) bar.append('□');
        args.add("Exp " + percentVal + "% [" + bar + "]");

        // #12 顯示需求道具清單：直接使用資料表的繁中說明
        args.add(cfg.getDemandDisplay());

        pc.sendPackets(new S_NPCTalkReturn(pc, cfg.getHtmlKey(), args.toArray(new String[0])));
    }

    private void feedAwaken(final L1PcInstance pc, final int feedTimes) {
        final int cardId = pc.getCardId();
        CardAwakenProgress prog = CardAwakenProgressTable.get().get(pc.getAccountName(), cardId);
        if (prog == null) prog = new CardAwakenProgress(pc.getAccountName(), cardId, 0, 0);
        // 已滿等暫停餵養
        if (prog.getExp() >= 100) {
            pc.sendPackets(new S_SystemMessage("覺醒Exp已滿，請先嘗試覺醒"));
            openAwaken(pc);
            return;
        }
        // 已達最大階段暫停餵養
        int maxStage = CardAwakenConfigTable.get().getMaxStage(cardId);
        if (maxStage > 0 && prog.getStage() >= maxStage) {
            pc.sendPackets(new S_SystemMessage("此卡片已經完成覺醒"));
            return;
        }
        final int nextStage = prog.getStage() + 1;
        final CardAwakenConfig cfg = CardAwakenConfigTable.get().getConfig(cardId, nextStage);
        if (cfg == null) {
            pc.sendPackets(new S_SystemMessage("此卡片目前沒有覺醒設定"));
            return;
        }

        // 計算每次餵養應消耗的素材道具
        int expPerFeed = Math.max(1, cfg.getExpPerFeed()); // 10 代表 +10%
        int needPerFeed = 1; // 每次餵養所需的道具張數（可擴充設定）

        // 素材來源：若 feedRule=list 則用清單，否則以同群組卡識別（此版僅支援 list）
        int[] feedItems = cfg.getFeedItemIds();
        if (feedItems == null || feedItems.length == 0) {
            pc.sendPackets(new S_SystemMessage("未設定增加道具清單"));
            return;
        }
        if (pc.getInventory() == null) {
            pc.sendPackets(new S_SystemMessage("無法取得背包資訊，請稍後再試"));
            return;
        }

        int totalFeedsDone = 0;
        for (int t = 0; t < feedTimes; t++) {
            // 找到一種可用素材並扣除 needPerFeed 張
            boolean consumed = false;
            for (int itemId : feedItems) {
                if (itemId <= 0) continue;
                try {
                    if (pc.getInventory().checkItem(itemId, needPerFeed)) {
                        pc.getInventory().consumeItem(itemId, needPerFeed);
                        consumed = true;
                        break;
                    }
                } catch (Exception ignored) {
                }
            }
            if (!consumed) break; // 沒素材就停止

            int newExp = Math.min(100, prog.getExp() + expPerFeed);
            prog.setExp(newExp);
            totalFeedsDone++;
            if (newExp >= 100) break; // 滿了就停
        }

        if (totalFeedsDone > 0) {
            CardAwakenProgressTable.get().upsert(prog);
        } else {
            pc.sendPackets(new S_SystemMessage("背包無可用素材，或數量不足"));
        }
        // 立即刷新畫面，讓 #11 立刻顯示最新 EXP
        openAwaken(pc);
    }

    private void tryAwaken(final L1PcInstance pc) {
        final int cardId = pc.getCardId();
        CardAwakenProgress prog = CardAwakenProgressTable.get().get(pc.getAccountName(), cardId);
        if (prog == null) {
            pc.sendPackets(new S_SystemMessage("請先增加覺醒經驗"));
            return;
        }
        final int nextStage = prog.getStage() + 1;
        final CardAwakenConfig cfg = CardAwakenConfigTable.get().getConfig(cardId, nextStage);
        if (cfg == null) {
            pc.sendPackets(new S_SystemMessage("此卡片目前沒有覺醒設定"));
            return;
        }
        if (prog.getExp() < 100) {
            pc.sendPackets(new S_SystemMessage("\\f=覺醒經驗不足，需達 100%"));
            return;
        }
        int roll = (int) (Math.random() * 100);
        if (roll < cfg.getSuccessRate()) {
            // 成功：升階、清 EXP、寫入任務
            prog.setStage(nextStage);
            prog.setExp(0);
            CardAwakenProgressTable.get().upsert(prog);
            CardQuestTable.get().storeQuest(pc.getAccountName(), cfg.getSuccessQuestId(), new CardQuest(pc.getAccountName(), cfg.getSuccessQuestId()));
            pc.sendPackets(new S_PacketBoxGree(24));
            pc.sendPacketsX8(new S_Sound(20360));
            pc.sendPackets(new S_SystemMessage("\\f=覺醒成功！"));
        } else {
            int keep = Math.max(0, Math.min(100, cfg.getFailKeepPercent()));
            int newExp = (prog.getExp() * keep) / 100;
            prog.setExp(newExp);
            CardAwakenProgressTable.get().upsert(prog);
            pc.sendPackets(new S_PacketBoxGree(16));
            pc.sendPackets(new S_SystemMessage("\\f=覺醒失敗，經驗返回 " + keep + "%"));
        }
        // 刷新畫面顯示最新階段/EXP
        openAwaken(pc);
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

                        // 覺醒覆寫：顯示文字
                        String showMsg2 = card.getMsg2();
                        String showMsg1 = card.getMsg1();
                        if (CardQuestTable.get().IsQuest(pc, card.getQuestId())) {
                            int awakenedStage = getPlayerAwakenStage(pc, i);
                            if (awakenedStage > 0) {
                                CardAwakenConfig cfg = CardAwakenConfigTable.get().getConfig(i, awakenedStage);
                                if (cfg != null) {
                                    if (cfg.getTitle() != null && !cfg.getTitle().isEmpty()) showMsg2 = cfg.getTitle();
                                    if (cfg.getDescription() != null && !cfg.getDescription().isEmpty()) showMsg1 = cfg.getDescription();
                                }
                            }
                            stringBuilder.append(card.getAddcgfxid()).append(",");    // 0
                        } else {
                            stringBuilder.append(card.getAddhgfxid()).append(",");    // 0
                        }
                        stringBuilder.append(showMsg2).append(",");
                        stringBuilder.append(showMsg1).append(",");


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