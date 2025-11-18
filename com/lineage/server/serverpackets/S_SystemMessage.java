package com.lineage.server.serverpackets;


public class S_SystemMessage extends ServerBasePacket {
    private byte[] _byte = null;

    public S_SystemMessage(String msg) {
        writeC(S_MESSAGE);
        writeC(9);
        writeS(sanitizeForClient(msg));
    }

    public S_SystemMessage(String msg, boolean nameid) {
        writeC(S_SAY_CODE);
        writeC(2);
        writeD(0);
        writeS(sanitizeForClient(msg));
    }

    /**
     * 发送对话栏讯息<br>
     * 支持 "\r\n" 换行符
     *
     * @param msg   讯息
     * @param color 颜色代码<br>
     *              1 = 土红(浅) <font color="ffa0a0">同时发送声音通知 桌面任务栏会闪烁提醒</font><br>
     *              3 = 蓝绿(浅)<br>
     *              4 = 浅绿<br>
     *              11 = 暗黄<br>
     *              12 = 灰黄比较暗<br>
     *              13 = 浅绿(比 {@code 4} 深点)<br>
     *              15 = 浅蓝<br>
     *              17 = 暗绿<br>
     *              <font color="ff00ff">18 = 随机颜色(实际是 {@code S_AllChannelsChat} 可指定颜色)</font><br>
     *              19 = 白(同时屏幕中偏上方显示炫彩变色大字)<br>
     *              25 = 随机颜色 极高概率是 {@code 黑色}<br>
     *              26 = 仅屏幕上方绿字 类似 {@code greenMessage}<br>
     *              28 = 原色(同时屏幕上方显示绿字 类似 {@code greenMessage})<br>
     *              <font color="ff0000">22 = 不会发送任何讯息</font><br>
     *              其他(30以下) = 天堂原始的黄色系统色 (30以上未测试)
     */
    public S_SystemMessage(String msg, int color) {
        writeC(S_MESSAGE);
        writeC(color);
        writeS(sanitizeForClient(msg));
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

    /**
     * 與 S_ServerMessage 相同的最小清理策略，避免 '%' 與控制字元造成舊客戶端崩潰。
     */
    private static String sanitizeForClient(String input) {
        if (input == null) return "";
        String s = input
                .replace('%', '％')
                .replace('\n', ' ')
                .replace('\r', ' ')
                .replace('\t', ' ');
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 0x20 && c != 0x7F) {
                sb.append(c);
            } else {
                sb.append(' ');
            }
        }
        final int MAX_LEN = 200;
        if (sb.length() > MAX_LEN) {
            return sb.substring(0, MAX_LEN);
        }
        return sb.toString();
    }
}