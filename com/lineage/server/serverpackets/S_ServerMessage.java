package com.lineage.server.serverpackets;

public class S_ServerMessage extends ServerBasePacket {
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
        writeS(msg);
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
        this.writeS(itemName);
        this.writeD(icongfxId);
        this.writeS(pcName);
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
        this.writeS(name);
    }

    private void buildPacket(int type, String[] info) {
        writeC(S_MESSAGE_CODE);
        writeH(type);
        if (info == null) {
            writeC(0);
        } else {
            writeC(info.length);
            for (int i = 0; i < info.length; i++) {
                writeS(info[i]);
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
 * com.lineage.server.serverpackets.S_ServerMessage JD-Core Version: 0.6.2
 */