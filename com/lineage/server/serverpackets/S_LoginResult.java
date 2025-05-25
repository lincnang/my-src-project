package com.lineage.server.serverpackets;

public class S_LoginResult extends ServerBasePacket {
    public static final int REASON_LOGIN_OK = 0;
    public static final int REASON_ACCESS_FAILED = 8;
    public static final int REASON_ACCOUNT_IN_USE = 22;
    public static final int EVENT_CANT_USE = 26;
    public static final int EVENT_CANT_LOGIN = 28;
    public static final int EVENT_REGISTER_ACCOUNTS = 47;
    public static final int EVENT_LAN_ERROR = 39;
    public static final int EVENT_MAIL_ERROR = 11;
    public static final int REASON_ACCOUNT_ALREADY_EXISTS = 7;
    public static final int EVENT_C_ERROR = 17;
    public static final int EVENT_RE_LOGIN = 4;
    public static final int EVENT_CLOCK_ERROR = 50;
    public static final int EVENT_NAME_ERROR = 52;
    public static final int EVENT_PASS_ERROR = 53;
    public static final int EVENT_PASS_CHECK = 10;
    public static final int EVENT_ERROR_USER = 155;
    public static final int EVENT_ERROR_PASS = 149;
    private byte[] _byte = null;

    public S_LoginResult(int reason) {
        buildPacket(reason);
    }

    private void buildPacket(int reason) {
        writeC(93);
        writeH(reason);
        writeD(0);
        writeD(0);
        writeD(0);
    }

    public byte[] getContent() {
        if (this._byte == null) {
            this._byte = getBytes();
        }
        return this._byte;
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}