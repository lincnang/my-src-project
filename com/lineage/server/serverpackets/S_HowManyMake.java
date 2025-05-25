package com.lineage.server.serverpackets;

public class S_HowManyMake extends ServerBasePacket {
    private byte[] _byte = null;

    public S_HowManyMake(int objId, int max, String htmlId) {
        writeC(S_HYPERTEXT_INPUT);
        writeD(objId);
        writeD(0);
        writeD(0);
        writeD(0);
        writeD(max);
        writeH(0);
        writeS("request");
        writeS(htmlId);
    }

    public S_HowManyMake(int objId, long min, long max, String action, String htmlId) {
        writeC(S_OPCODE_INPUTAMOUNT);
        writeD(objId);
        writeD(0); // ?
        writeD(0); // 初期價格
        writeD((int) min); // 價格下限
        writeD((int) max); // 價格上限
        writeH(0); // ?
        writeS(htmlId);
        writeS(action);
    }

    public S_HowManyMake(int objId, long min, long max, String action, String htmlId, String[] data) {
        writeC(S_OPCODE_INPUTAMOUNT);
        writeD(objId);
        writeD(0); // ?
        writeD(0); // 初期價格
        writeD((int) min); // 價格下限
        writeD((int) max); // 價格上限
        writeH(0); // ?
        writeS(htmlId);
        writeS(action);
        if (data != null && data.length > 0) {
            writeH(data.length);
            for (String s : data) {
                writeS(s);
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
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.serverpackets.S_HowManyMake JD-Core Version: 0.6.2
 */