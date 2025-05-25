package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class C_AttackHandler extends ClientBasePacket {
    private static final Log _log = LogFactory.getLog(C_AttackHandler.class);

    @Override
    public void start(final byte[] decrypt, final ClientExecutor client) {
        try {
            // 資料載入
            read(decrypt);
            final L1PcInstance pc = client.getActiveChar();
            if (pc == null) {
                return;
            }
            final int targetId = readD();
            pc.setAttackTargetId(targetId);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            over();
        }
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}