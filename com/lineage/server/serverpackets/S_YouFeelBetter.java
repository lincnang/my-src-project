package com.lineage.server.serverpackets;

/**
 * 安全取代舊型客戶端會崩潰的 77 號 ServerMessage。
 * 採用對話欄系統訊息顯示「你覺得舒服多了。」以維持玩家體驗。
 */
public class S_YouFeelBetter extends S_SystemMessage {

    private static final String DEFAULT_MESSAGE = "你覺得舒服多了。";

    public S_YouFeelBetter() {
        super(DEFAULT_MESSAGE);
    }
}
