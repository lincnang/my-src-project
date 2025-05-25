package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;

/**
 * 要求顧用傭兵
 *
 * @author daien
 */
public class C_HireSoldier extends ClientBasePacket {
    /*
     * public C_HireSoldier() { }
     *
     * public C_HireSoldier(final byte[] abyte0, final ClientExecutor client) {
     * super(abyte0); try { this.start(abyte0, client);
     *
     * } catch (final Exception e) { _log.error(e.getLocalizedMessage(), e); } }
     */
    @Override
    public void start(final byte[] decrypt, final ClientExecutor client) {
        try {
            // 資料載入
            this.read(decrypt);
            //final int something1 = this.readH(); // S_HireSoldier引數
            //final int something2 = this.readH(); // S_HireSoldier引數
            //final int something3 = this.readD(); // 1以外入？
            //final int something4 = this.readD(); // S_HireSoldier引數
            //final int number = this.readH(); // 僱用數
            // < 傭兵僱用處理
        } catch (final Exception e) {
            // _log.error(e.getLocalizedMessage(), e);
        } finally {
            this.over();
        }
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
