package com.add.Tsai;

/**
 * 娃娃卡任務編號
 *
 * @author hero
 */
public class dollQuest {
    private String _account;
    private int _questid;

    /**
     * @param account 帳號
     * @param questId 任務編號
     */
    public dollQuest(String account, int questId) {
        _account = account;
        _questid = questId;
    }

    public String getAccount() {
        return _account;
    }

    public int getQuestId() {
        return _questid;
    }
}