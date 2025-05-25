package com.lineage.server.serverpackets;

import com.lineage.config.Config;

/**
 * 伺服器版本
 *
 * @author dexc
 */
public class S_ServerVersion extends ServerBasePacket {
    private static final int CLIENT_LANGUAGE = Config.CLIENT_LANGUAGE;
    private static final int UPTIME = (int) (System.currentTimeMillis() / 1000);
    public final int startTime = (int) (System.currentTimeMillis() / 1000);

    public S_ServerVersion() {
        writeC(S_VERSION_CHECK);
        writeC(0x00);
        //writeC(0x00);
        writeC(118); // Server Id= 118
        writeD(0x6565d322); // server version
        writeD(0x6565d322); // cache version
        writeD(0x781bd67d); // auth version
        writeD(0x6565d322); // npc version
        writeD(startTime);
        writeC(0); // newacc
        writeC(0); // eng
        writeC(CLIENT_LANGUAGE); // Country: 0.US 3.Taiwan 4.Janpan 5.China
        writeD(getSetting()); // Server Type =0x7cff7dc2
        writeD(UPTIME); // Uptime /time =目前時間
        writeD(0x08f5a69c);// global cache version
        writeD(0x08f3f1e5);// TAM version
        writeD(0x0901e36d);// arca version
        writeD(0x000001ef);
        writeD(0x09977c4d);
    }

    private int getSetting() {
        final boolean[] setting = { // 2的x次方
                false, // 0----------
                true, // 1* =是否載入.tbt?
                false, // 2 =在夢中不會受到死亡懲罰
                false, // 3----------
                false, // 4----------
                false, // 5----------
                false, // 6 =穿怪
                true, // 7*
                true, // 8*
                false, // 9----------
                false, // 10 =倉庫密碼加密
                true, // 11*
                true, // 12*
                true, // 13*
                true, // 14*
                false, // 15 =無法創新角色
                true, // 16*
                true, // 17*
                true, // 18*
                true, // 19*
                true, // 20*
                true, // 21*
                true, // 22*
                true, // 23*
                false, // 24----------
                false, // 25----------
                true, // 26*
                //true, // 27 =右鍵鎖定
                false, // 27 =右鍵鎖定
                true, // 28*
                true, // 29*
                true, // 30 =免服(經驗表/商城/抽抽樂)
        };
        int val = 0;
        for (int i = 0; i < setting.length; i++) {
            if (setting[i]) {
                val |= 1 << i; // 2^i
            }
        }
        return val;
    }

    @Override
    public byte[] getContent() {
        return getBytes();
    }

    @Override
    public String getType() {
        return "S_ServerVersion";
    }
}
