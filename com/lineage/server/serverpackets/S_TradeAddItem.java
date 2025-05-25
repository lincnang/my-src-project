package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;

/**
 * 交易增加物品
 *
 * @author dexc
 */
public class S_TradeAddItem extends ServerBasePacket {
    /**
     * 交易增加物品
     *
     */
    public S_TradeAddItem(L1ItemInstance item, long count, int type) {
        writeC(S_ADD_XCHG);
        writeC(type); // 0:交易視窗上半部 1:交易視窗下半部
        writeH(item.getItem().getGfxId());
        String name = item.getNumberedViewName(count);
        writeS(name);
        //writeC(item.getBless());
        // 0:祝福 1：一般 2：詛咒 3：未鑑定
        if (!item.isIdentified()) { // 未鑑定
            writeC(0x03);
            writeC(0x00);
        } else {
            writeC(item.getBless());
            final byte[] status = item.getStatusBytes();
            writeC(status.length);
            for (final byte b : status) {
                writeC(b);
            }
        }
    }

    /**
     * 交易增加物品 - 測試
     */
    public S_TradeAddItem() {
        writeC(S_ADD_XCHG);
        writeC(1);
        writeH(714);
        writeS("測試物品(55)");
        writeC(0);
    }

    @Override
    public byte[] getContent() {
        return getBytes();
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }
}
