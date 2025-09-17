package com.add.Tsai;

/**
 * 玩家覺醒進度
 */
public class CardAwakenProgress {
    private final String account;
    private final int cardId;
    private int stage;
    private int exp; // 0~10000 (代表 0~100%)

    public CardAwakenProgress(String account, int cardId, int stage, int exp) {
        this.account = account;
        this.cardId = cardId;
        this.stage = stage;
        this.exp = exp;
    }

    public String getAccount() { return account; }
    public int getCardId() { return cardId; }
    public int getStage() { return stage; }
    public void setStage(int stage) { this.stage = stage; }
    public int getExp() { return exp; }
    public void setExp(int exp) { this.exp = exp; }
}


