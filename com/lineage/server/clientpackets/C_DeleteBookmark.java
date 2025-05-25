package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1BookMark;

/**
 * 要求刪除記憶座標
 *
 * @author daien
 */
public class C_DeleteBookmark extends ClientBasePacket {
    //private static final Log _log = LogFactory.getLog(C_DeleteBookmark.class);
    /*
     * public C_DeleteBookmark() { }
     *
     * public C_DeleteBookmark(final byte[] abyte0, final ClientExecutor client)
     * { super(abyte0); try { this.start(abyte0, client);
     *
     * } catch (final Exception e) { _log.error(e.getLocalizedMessage(), e); } }
     */
    @Override
    public void start(final byte[] decrypt, final ClientExecutor client) {
        try {
            // 資料載入
            this.read(decrypt);
            final L1PcInstance pc = client.getActiveChar();
            if (pc == null) {
                return;
            }
            final String bookmarkname = this.readS();
            if (!bookmarkname.isEmpty()) {
                L1BookMark.deleteBookmark(pc, bookmarkname); // 日版記憶座標
            }
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
