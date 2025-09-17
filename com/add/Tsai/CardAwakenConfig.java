package com.add.Tsai;

/**
 * 變身卡覺醒設定（對應資料表：系統_變身卡覺醒設定）
 */
public class CardAwakenConfig {
    private final int id;
    private final String cmd;           // 卡冊指令，如 p001
    private final int cardId;           // 系統_變身卡冊.流水號
    private final int stage;            // 覺醒階段（1..N）
    private final String htmlKey;       // 覺醒頁面鍵值
    private final String title;         // 標題文字
    private final String description;   // 說明文字
    private final int expPerFeed;       // 每素材增加的 EXP（百分比×100，或直接百分比數字）
    private final int successRate;      // 覺醒成功機率（%）
    private final int failKeepPercent;  // 失敗保留 EXP（%）
    private final int[] demandItemIds;  // 多選需求清單（任一滿足即可）
    private final int demandItemCount;  // 每個需求道具的數量要求
    private final int successQuestId;   // 覺醒成功後寫入的任務編號
    private final Integer awakenPolyId; // 覺醒後替換 polyId（可為 null）
    private final int addPolyTimeSec;   // 覺醒後時效加秒
    private final int costDiscount;     // 消耗折扣（%）
    private final String feedRule;      // 素材規則：group / list
    private final int[] feedItemIds;    // 若為 list，解析後的 itemId 陣列
    private final String demandDisplay; // 需求顯示文字（可填繁中）

    public CardAwakenConfig(int id, String cmd, int cardId, int stage, String htmlKey,
                            String title, String description, int expPerFeed, int successRate,
                            int failKeepPercent, int successQuestId,
                            Integer awakenPolyId, int addPolyTimeSec, int costDiscount,
                            String feedRule, int[] feedItemIds, int[] demandItemIds, int demandItemCount) {
        this(id, cmd, cardId, stage, htmlKey, title, description, expPerFeed, successRate,
                failKeepPercent, successQuestId, awakenPolyId, addPolyTimeSec, costDiscount,
                feedRule, feedItemIds, demandItemIds, demandItemCount, "");
    }

    public CardAwakenConfig(int id, String cmd, int cardId, int stage, String htmlKey,
                            String title, String description, int expPerFeed, int successRate,
                            int failKeepPercent, int successQuestId,
                            Integer awakenPolyId, int addPolyTimeSec, int costDiscount,
                            String feedRule, int[] feedItemIds, int[] demandItemIds, int demandItemCount,
                            String demandDisplay) {
        this.id = id;
        this.cmd = cmd;
        this.cardId = cardId;
        this.stage = stage;
        this.htmlKey = htmlKey;
        this.title = title;
        this.description = description;
        this.expPerFeed = expPerFeed;
        this.successRate = successRate;
        this.failKeepPercent = failKeepPercent;
        // legacy columns will be ignored moving forward
        this.demandItemIds = demandItemIds == null ? new int[0] : demandItemIds;
        this.demandItemCount = demandItemCount;
        this.successQuestId = successQuestId;
        this.awakenPolyId = awakenPolyId;
        this.addPolyTimeSec = addPolyTimeSec;
        this.costDiscount = costDiscount;
        this.feedRule = feedRule == null ? "group" : feedRule;
        this.feedItemIds = feedItemIds == null ? new int[0] : feedItemIds;
        this.demandDisplay = demandDisplay == null ? "" : demandDisplay;
    }

    public int getId() { return id; }
    public String getCmd() { return cmd; }
    public int getCardId() { return cardId; }
    public int getStage() { return stage; }
    public String getHtmlKey() { return htmlKey; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getExpPerFeed() { return expPerFeed; }
    public int getSuccessRate() { return successRate; }
    public int getFailKeepPercent() { return failKeepPercent; }
    // legacy getters kept for compatibility, but not used
    public int getNeedItem1() { return 0; }
    public int getNeedCount1() { return 0; }
    public int getNeedItem2() { return 0; }
    public int getNeedCount2() { return 0; }
    public int[] getDemandItemIds() { return demandItemIds; }
    public int getDemandItemCount() { return demandItemCount; }
    public int getSuccessQuestId() { return successQuestId; }
    public Integer getAwakenPolyId() { return awakenPolyId; }
    public int getAddPolyTimeSec() { return addPolyTimeSec; }
    public int getCostDiscount() { return costDiscount; }
    public String getFeedRule() { return feedRule; }
    public int[] getFeedItemIds() { return feedItemIds; }
    public String getDemandDisplay() { return demandDisplay; }
}


