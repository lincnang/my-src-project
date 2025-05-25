package com.lineage.server.serverpackets;

import java.util.ArrayList;

public class S_PacketBoxGame extends ServerBasePacket {
    public static final int GAMESTART = 64;
    public static final int TIMESTART = 65;
    public static final int GAMEINFO = 66;
    public static final int GAMEOVER = 69;
    public static final int GAMECLEAR = 70;
    public static final int STARTTIME = 71;
    public static final int STARTTIMECLEAR = 72;
    private byte[] _byte = null;

    /**
     * 開始正向計時<br>
     * 移除比賽視窗<br>
     * 移除開始反向計時視窗<br>
     *
     * @param subCode
     */
    public S_PacketBoxGame(int subCode) {
        writeC(S_EVENT);
        writeC(subCode);
        switch (subCode) {
            case TIMESTART:// 開始正向計時
            case GAMECLEAR:// 移除比賽視窗
            case STARTTIMECLEAR:// 移除開始反向計時視窗
                break;
        }
    }

    /**
     * 倒數開始0~10<br>
     * 倒數結束0~10<br>
     * 開始反向計時0~3600<br>
     *
     * @param subCode
     * @param value
     */
    public S_PacketBoxGame(int subCode, int value) {
        writeC(S_EVENT);
        writeC(subCode);
        switch (subCode) {
            case GAMESTART:// 倒數開始
            case GAMEOVER:// 倒數結束
                writeC(value); // 倒數時間 0~10
                break;
            case STARTTIME:// 開始反向計時
                writeH(value); // 0~3600
                break;
        }
    }

    public S_PacketBoxGame(ArrayList<StringBuilder> list) {
        writeC(S_EVENT);
        writeC(66);
        writeC(list.size());
        writeC(0);
        writeC(0);
        writeC(0);
        if (list != null) {
            for (StringBuilder string : list) {
                writeS(string.toString());
            }
        }
    }

    public S_PacketBoxGame() {
        writeC(S_EVENT);
        writeC(66);
        writeC(1);
        writeC(0);
        writeC(0);
        writeC(0);
        writeS("測試");
    }

    public S_PacketBoxGame(StringBuilder title, ArrayList<StringBuilder> list) {
        writeC(S_EVENT);
        writeC(66);
        writeC(list.size() + 1);
        writeC(0);
        writeC(0);
        writeC(0);
        writeS(title.toString());
        if (list != null) {
            for (StringBuilder c : list) {
                writeS(c.toString());
            }
        }
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
