package com.lineage.server.serverpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class S_ServerMessage extends ServerBasePacket {
    private static final Log _log = LogFactory.getLog(S_ServerMessage.class);
    /**
     * [原碼] 潘朵拉抽抽樂
     */
    public static final int luckylottery = 3736;
    private byte[] _byte = null;

    /**
     * 服務器訊息(NPC對話)
     *
     */
    public S_ServerMessage(String msg) {
        writeC(S_SAY_CODE);
        writeC(0);
        writeD(0);
        writeS(sanitizeForClient(msg));
    }

    public S_ServerMessage(int type) {
        writeC(S_MESSAGE_CODE);
        writeH(type);
        writeC(0);
    }

    public S_ServerMessage(int type, String msg1) {
        buildPacket(type, new String[]{msg1});
    }

    public S_ServerMessage(int type, String msg1, String msg2) {
        buildPacket(type, new String[]{msg1, msg2});
    }

    public S_ServerMessage(int type, String msg1, String msg2, String msg3) {
        buildPacket(type, new String[]{msg1, msg2, msg3});
    }

    public S_ServerMessage(int type, String msg1, String msg2, String msg3, String msg4) {
        buildPacket(type, new String[]{msg1, msg2, msg3, msg4});
    }

    public S_ServerMessage(int type, String msg1, String msg2, String msg3, String msg4, String msg5) {
        buildPacket(type, new String[]{msg1, msg2, msg3, msg4, msg5});
    }

    public S_ServerMessage(int type, String[] info) {
        buildPacket(type, info);
    }

    /**
     * @**抽抽樂**
     * @作者:冰雕寵兒
     */
    public S_ServerMessage(final String itemName, final int icongfxId, final String pcName, final int l) {
        this._byte = null;
        // this.writeC(158);
        this.writeC(S_MESSAGE_CODE);
        this.writeH(3736);
        this.writeS(sanitizeForClient(itemName));
        this.writeD(icongfxId);
        this.writeS(sanitizeForClient(pcName));
        this.writeH(0);
    }

    public S_ServerMessage(final int type, final int number) {
        this._byte = null;
        // this.writeC(158);
        this.writeC(S_MESSAGE_CODE);
        this.writeH(type);
        this.writeD(number);
    }//抽抽樂END

    public S_ServerMessage(final String name, final int color) {
        this.writeC(S_SAY_CODE);
        this.writeC(color);
        this.writeS(sanitizeForClient(name));
    }

    private void buildPacket(int type, String[] info) {
        writeC(S_MESSAGE_CODE);
        writeH(type);

        // 針對已知容易觸發舊客戶端崩潰的訊息模板做防呆
        // 143: 常見為「%0が%1をくれました。」→ 期望 2 個參數
        int expectArgs = -1;
        if (type == 143) expectArgs = 2;

        if (info == null) {
            // 若模板需要參數，補齊空字串避免客戶端讀取越界
            if (expectArgs > 0) {
                writeC(expectArgs);
                for (int i = 0; i < expectArgs; i++) writeS("");
            } else {
                writeC(0);
            }
            return;
        }

        // 參數數量調整：不足補空字串，過多截斷
        String[] args = info;
        if (expectArgs > 0) {
            if (info.length < expectArgs) {
                String[] fixed = new String[expectArgs];
                System.arraycopy(info, 0, fixed, 0, info.length);
                for (int i = info.length; i < expectArgs; i++) fixed[i] = "";
                args = fixed;
                if (_log.isWarnEnabled()) {
                    _log.warn("S_ServerMessage type=" + type + " args padded from " + info.length + " to " + expectArgs);
                }
            } else if (info.length > expectArgs) {
                String[] fixed = new String[expectArgs];
                System.arraycopy(info, 0, fixed, 0, expectArgs);
                args = fixed;
                if (_log.isWarnEnabled()) {
                    _log.warn("S_ServerMessage type=" + type + " args truncated from " + info.length + " to " + expectArgs);
                }
            }
        }

        writeC(args.length);
        for (int idx = 0; idx < args.length; idx++) {
            String before = args[idx];
            String after = sanitizeForClient(before);
            if (_log.isDebugEnabled() && before != null) {
                int replaced = 0;
                for (int j = 0; j < before.length(); j++) if (before.charAt(j) == '%') replaced++;
                if (replaced > 0) {
                    _log.debug("S_ServerMessage sanitize: replaced % count=" + replaced);
                }
            }
            writeS(after);
        }
    }

    /**
     * 為了避免舊版客戶端解析 ServerMessage 模板字串時因未轉義的 '%' 或控制字元導致崩潰，
     * 在送出前做最小幅度的清理：
     正在摘要交談記錄
     
     你問得很好—我簡單說明背壓與限速要做的「用意」，並對應到你現在的 L1J + Netty 伺服器情境。
     
     背壓（Backpressure）的用意
     
     定義：當下游處理速度跟不上上游產生的資料時，主動「放慢來源」或「暫停讀取」，避免隊列堆爆。
     目的：保護系統資源（CPU、記憶體、DB 連線池）不被尖峰流量或慢消費者拖垮，防止 OOM、GC 風暴、連鎖斷線。
     在你專案的施力點：
     Netty inbound：DecoderManager 的工作佇列過高時暫停 channel 讀取（暫停 OP_READ），等佇列回落再恢復，避免現在「佇列滿就關連線」的硬斷。
     Netty outbound：用 write buffer watermarks 與 isWritable() 搭配「延遲/合併 flush」減少頻繁 flush 導致的阻塞。
     專案現狀關聯：你目前 DecoderManager 佇列滿會立即斷線，背壓能把「丟/斷」變成「暫停/緩一下」，大幅降低誤傷正常玩家。
     限速（Rate limiting）的用意
     
     定義：限制每單位時間的請求/封包數（可按連線、IP、帳號、Opcode 分維度）。
     目的：阻擋濫用與 DoS/刷頻，確保公平與穩定，避免聊天、交易、登入等高頻操作把系統拖慢。
     在你專案的施力點：
     每連線封包率：例如每秒 50 包（突發桶 100），超標先延遲，持續超標才丟低優先封包或斷線。
     每 IP 連線/登入嘗試：限制同時連線與每秒新連線數，防爆登入流程與 DB。
     特定行為：聊天/廣播、交易邀請、合成/查詢等高頻指令做獨立配額，避免造成 143 類訊息或 DB 壓力連鎖。
     
     * - 將 '%' 轉為全形字元 '％'（避免與 %0/%1 佔位符誤判）
     * - 將換行/回車/Tab 轉為空白
     * - 限制最大長度，避免極端長字串
     */
    private static String sanitizeForClient(String input) {
        if (input == null) return "";
        String s = input
                .replace('%', '％')
                .replace('\n', ' ')
                .replace('\r', ' ')
                .replace('\t', ' ');
        // 去除客戶端色碼/控制轉義（例如 \fX 或 \aX）
        StringBuilder cleaned = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 2 < s.length()) {
                char c1 = s.charAt(i + 1);
                if ((c1 == 'f' || c1 == 'F' || c1 == 'a' || c1 == 'A')) {
                    // 跳過三字元序列
                    i += 2;
                    continue;
                }
            }
            cleaned.append(c);
        }
        s = cleaned.toString();
        // 過濾其他不可列印控制字元
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 0x20 && c != 0x7F) {
                sb.append(c);
            } else {
                sb.append(' ');
            }
        }
        // 壓縮連續空白
        for (int i = 1; i < sb.length(); i++) {
            if (sb.charAt(i) == ' ' && sb.charAt(i - 1) == ' ') {
                sb.deleteCharAt(i);
                i--;
            }
        }
        // 長度保護（以字元數計，足以顯示名稱/物品字串）
        final int MAX_LEN = 200;
        if (sb.length() > MAX_LEN) {
            return sb.substring(0, MAX_LEN);
        }
        String out = sb.toString();
        // 避免完全空字串觸發舊客戶端模板處理邊界行為
        if (out.isEmpty()) return " ";
        return out;
    }

    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.serverpackets.S_ServerMessage JD-Core Version: 0.6.2
 */