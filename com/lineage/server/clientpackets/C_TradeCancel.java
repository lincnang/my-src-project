package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Trade;

/**
 * 要求取消交易(個人/商店)
 *
 * @author L1jJP, Dexc, Tom
 */
public class C_TradeCancel extends ClientBasePacket {
    //private static final Log _log = LogFactory.getLog(C_TradeCancel.class);
    @Override
    public void start(byte[] decrypt, ClientExecutor client) {
        L1PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }
        try {
            L1Trade trade = new L1Trade();
            trade.tradeCancel(player);
        } catch (final Exception e) {
        } finally {
            over();
        }
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }
}
