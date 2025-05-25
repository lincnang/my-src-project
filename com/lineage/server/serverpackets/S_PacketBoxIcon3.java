package com.lineage.server.serverpackets;

public class S_PacketBoxIcon3 extends ServerBasePacket {
    public static final int BUFFICON = 154;
    private static final int ICON_OTHER3 = 154;// 魔法圖示
    private byte[] _byte = null;

    public S_PacketBoxIcon3(int time, int type) {
        writeC(S_EVENT);
        writeC(ICON_OTHER3);
        writeH(time);
        writeC(type);
    }

    /**
     * 時效技能狀態圖示型號代碼
     * <p>
     * type=10            不死的意志</font><br>
     * type=20            不在害怕巨型骷髏(圖示tbt編號為5894)</font><br>
     * type=4914       黑蟒的祝福(圖示tbt編號為5596)</font><br>
     * type=11032    月餅效果</font><br>
     * type=11894    神秘的buff,受到神秘的buff時不會收到道具受損的警告</font><br>
     * type=13160    近距離傷害+2 體力+50 力量+1 體力恢復量+3(應該是衝鋒魔石)(圖示tbt編號為5441)</font><br>
     * type=13249    經驗EXP圖示(會跟一階段經驗類重疊)</font><br>
     * type=13564    對攻擊對象發動黑帝斯的詛咒(圖示tbt編號為6914)</font><br>
     * type=13565    發動大地女神的祈禱(圖示tbt編號為6913)</font><br>
     * </p>
     */
    public S_PacketBoxIcon3(int subCode, int time, int type) {
        writeC(S_EVENT);
        writeC(subCode); // 154
        writeH(time);
        writeH(type);
        writeH(0);
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
