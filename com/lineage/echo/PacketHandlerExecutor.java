package com.lineage.echo;

/**
 * 客戶端封包處理 abstract
 *
 * @author dexc
 */
public abstract class PacketHandlerExecutor extends OpcodesClient {
    /**
     * 客戶端封包處理
     *
     */
    public abstract void handlePacket(byte decrypt[]);
}