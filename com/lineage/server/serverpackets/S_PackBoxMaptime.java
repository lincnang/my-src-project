package com.lineage.server.serverpackets;

/**
 * 地監時間顯示 3.53C
 *
 * @author Xiao&Roy
 */
public class S_PackBoxMaptime extends ServerBasePacket {
    private byte[] _byte = null;

    public S_PackBoxMaptime(final int time) {
        writeC(S_EVENT);
        writeC(153);
        writeD(time);
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    @Override
    public String getType() {
        return (new StringBuilder("[S] ")).append(getClass().getSimpleName()).append(" [S_PackBoxMaptime]").toString();
    }
}
