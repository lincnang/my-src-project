package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Trade;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

/**
 * 要求完成交易(個人)
 *
 * @author daien
 */
public class C_TradeOK extends ClientBasePacket {
    //private static final Log _log = LogFactory.getLog(C_TradeOK.class);
    @Override
    public void start(byte[] decrypt, ClientExecutor client) {
        final L1PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }
        try {
            final L1PcInstance trading_partner = (L1PcInstance) World.get().findObject(player.getTradeID());
            if (trading_partner != null) {
                player.setTradeOk(true);
                if ((player.getTradeOk()) && (trading_partner.getTradeOk())) {
                    // 雙方背包判斷
                    if ((player.getInventory().getSize() < 160) && (trading_partner.getInventory().getSize() < 160)) {
                        final L1Trade trade = new L1Trade();
                        trade.tradeOK(player);
                    } else {
                        // \f1一個角色最多可攜帶180個道具。
                        player.sendPackets(new S_ServerMessage(263));
                        trading_partner.sendPackets(new S_ServerMessage(263));
                        final L1Trade trade = new L1Trade();
                        trade.tradeCancel(player);
                    }
                }
            }
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
