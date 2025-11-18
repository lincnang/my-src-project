package com.lineage.server.serverpackets;

/**
 * 畫面中藍色訊息
 *
 * @author dexc
 */
public class S_BlueMessage extends ServerBasePacket {
    private byte[] _byte = null;

    /**
     * 畫面中藍色訊息
     *
     */
    public S_BlueMessage(final int type) {
        this.buildPacket(type, null);
    }

    /**
     * 畫面中藍色訊息
     *
     */
    public S_BlueMessage(final int type, final String msg1) {
        this.buildPacket(type, new String[]{msg1});
    }

    /**
     * 畫面中藍色訊息
     *
     */
    public S_BlueMessage(final int type, final String msg1, final String msg2) {
        this.buildPacket(type, new String[]{msg1, msg2});
    }

    /**
     * 畫面中藍色訊息
     *
     */
    public S_BlueMessage(final int type, final String[] info) {
        this.buildPacket(type, info);
    }

    private void buildPacket(final int type, final String[] info) {
        this.writeC(S_WARNING_CODE);
        this.writeH(type);
        if (info == null) {
            this.writeC(0x00);
        } else {
            this.writeC(info.length);
            for (String s : info) {
                this.writeS(sanitizeForClient(s));
            }
        }
    }

    // 與 S_ServerMessage 相同的最小清理策略
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
        if (sb.length() > MAX_LEN) return sb.substring(0, MAX_LEN);
        return sb.toString();
    }

    @Override
    public byte[] getContent() {
        if (this._byte == null) {
            this._byte = this.getBytes();
        }
        return this._byte;
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
