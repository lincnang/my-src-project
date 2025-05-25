package com.lineage.server.serverpackets;

/**
 * 狀態圖示自訂包<BR>
 * 可自訂所有狀態圖示時間<BR>
 *
 * @author sainai
 */
public class S_IconConfig extends ServerBasePacket {

    /**
     * 狀態圖示(可設定時間)
     */
    public static final int SKILL_ICON = 0x9a; // 154

    private byte[] _byte = null;

    /**
     * 狀態圖示<BR>
     * 可控制時間<BR>
     * 範例: pc.sendPackets(new S_IconConfig(S_IconConfig.SKILL_ICON, 2176, 1200 , false, false));
     *
     * @param subCode
     * @param type    (List 效果編號)
     * @param time    (單位 :秒)
     * @param second  false
     * @param temp    false
     */
    public S_IconConfig(int subCode, int type, int time, boolean second, boolean temp) {
        this.writeC(S_EVENT); // 7.0 = 8
        this.writeC(subCode);
        if (subCode == SKILL_ICON) {
            this.writeH(time);
            this.writeH(type); // 讀取 effectlist2-c.xml 的 graphic(變身檔魔法效果)
            this.writeH(0x00);
            // 0x01=刪除 0x00=附加
            this.writeH(second ? 0x01 : 0x00);
        }// b0 04 80 08 00 00 00 00
    }

    //@Override
    public byte[] getContent() {
        if (this._byte == null) {
            this._byte = this.getBytes();
        }
        return this._byte;
    }

    //@Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
