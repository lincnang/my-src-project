package com.lineage.server.serverpackets;

/**
 * 新手任務
 *
 * @author l1j-jp
 */
public class S_MatizCloudia extends ServerBasePacket {
    private static final String S_MatizCloudia = "[S] S_MatizCloudia";
    private byte[] _byte = null;

    public S_MatizCloudia(final int type, final int value) {
        writeC(S_EXTENDED_PROTOBUF);
        writeC(0x07);
        writeC(0x02);
        writeC(0x0a);
        switch (type) {
            case 0:
                writeC(0x13);
                writeC(0x08);
                writeC(0x80);
                break;
            case 1:
                writeC(0x1b);
                writeC(0x08);
                writeC(0x81);
                break;
        }
        writeC(0x02);
        writeC(0x10);
        switch (type) {
            case 0:
                writeC(0x9e);
                writeC(0x96);
                writeC(0x95);
                break;
            case 1:
                writeC(0x9e);
                writeC(0xdb);
                writeC(0xc3);
                break;
        }
        writeC(0xc2);
        writeC(0x05);
        writeC(0x22);
        writeC(0x06);
        writeC(0x08);
        writeC(0x01);
        writeC(0x10);
        switch (type) {
            case 0:
                writeC(value);
                writeC(0x18);
                writeC(0x05);
                writeC(0x28);
                writeC(0x01);
                break;
            case 1:// value : 1速2辺3ワン
                if (value == 1 || value == 3) {
                    writeC(0x01);
                } else {
                    writeC(0x00);
                }
                writeC(0x18);
                writeC(0x01);
                writeC(0x22);
                writeC(0x06);
                writeC(0x08);
                writeC(0x02);
                writeC(0x10);
                if (value == 2 || value == 3) {
                    writeC(0x01);
                } else {
                    writeC(0x00);
                }
                writeC(0x18);
                writeC(0x01);
                writeC(0x28);
                writeC(0x01);
                break;
        }
        writeH(0);
    }

    public S_MatizCloudia(final int type) {
        writeC(S_EXTENDED_PROTOBUF);
        switch (type) {
            case 1:
            case 3:
                // 自動攻撃 "
                writeC(0x09);
                break;
            case 2:// 製法だな
            case 4:
                writeC(0x0d);
                break;
        }
        writeC(0x02);
        writeC(0x08);
        writeC(0);
        writeC(0x10);
        switch (type) {
            case 1:
                writeC(0xa8);
                break;
            case 2:
                writeC(0x80);
                break;
            case 3:
            case 4:
                writeC(0x81);
                break;
        }
        writeC(0x02);
        writeH(0);
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = _bao.toByteArray();
        }
        return _byte;
    }

    @Override
    public String getType() {
        return S_MatizCloudia;
    }
}
