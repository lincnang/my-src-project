package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.serverpackets.S_ServerVersion;
import com.lineage.server.netty.NettyChannelRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 要求接收伺服器版本
 *
 * @author daien
 */
public class C_ServerVersion extends ClientBasePacket {
    private static final Log _log = LogFactory.getLog(C_ServerVersion.class);

    @Override
    public void start(final byte[] decrypt, final ClientExecutor client) {
        try {
            // 資料載入
            read(decrypt);
            // 標記版本驗證通過（Netty 連線時會有 GameClient 可標記）
            try { NettyChannelRegistry.markVersionVerified(client); } catch (Throwable ignore) {}
            client.out().encrypt(new S_ServerVersion());
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            over();
        }
    }

    @Override
    public String getType() {
        return super.getType();
    }
}
