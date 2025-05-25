package com.lineage.server.serverpackets;

/**
 * 計時地圖左上角計時器
 *
 * @author Psnnwe
 */
public final class S_MapTimer extends ServerBasePacket {
    /**
     * 顯示定時器地圖的剩餘時間
     **/
    public static final int MAP_TIMER = 153;
    private static final String S_MAP_TIMER = "[S] S_MapTimer";
    /**
     * 位元組
     */
    private byte[] _byte;

    /**
     * 地圖計時器
     *
     * @param value 剩餘的時間(秒)
     */
    public S_MapTimer(final int value) {
        this.writeC(OpcodesServer.S_EVENT);
        this.writeC(MAP_TIMER);
        this.writeH(value);
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
        return S_MAP_TIMER;
    }
}