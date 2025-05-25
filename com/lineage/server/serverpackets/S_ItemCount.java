package com.lineage.server.serverpackets;

/**
 * 選取物品數量 (NPC道具交換)
 *
 * @author dexc
 */
public class S_ItemCount extends ServerBasePacket {
    private byte[] _byte = null;

    /**
     * 選取物品數量 (NPC道具交換)
     *
     * @param objId NPC OBJID
     * @param max   可換數量上限
     * @param cmd   命令
     */
    public S_ItemCount(final int objId, final int max, final String cmd) {
        this.writeC(S_HYPERTEXT_INPUT);
        this.writeD(objId);
        this.writeD(0x00000000);// ?
        this.writeD(0x00000000);// 數量初始質
        this.writeD(0x00000000);// 最低可換數量
        this.writeD(max);// 最高可換數量
        this.writeH(0x0000);// c
        this.writeS("request");// HTML
        this.writeS(cmd);// 命令
    }

    /**
     * 選取物品數量 (NPC道具交換)
     *
     * @param objId NPC OBJID
     * @param max   可換數量上限
     * @param html  頁面
     * @param cmd   命令
     */
    public S_ItemCount(final int objId, final int max, final String html, final String cmd) {
        this.writeC(S_HYPERTEXT_INPUT);
        this.writeD(objId);
        this.writeD(0x00000000);// ?
        this.writeD(0x00000000);// 數量初始質
        this.writeD(0x00000000);// 最低可換數量
        this.writeD(max);// 最高可換數量
        this.writeH(0x0000);// c
        this.writeS(html);// HTML
        this.writeS(cmd);// 命令
    }

    /**
     * 選取物品數量 (道具進化使用)
     *
     */
    public S_ItemCount(final int objId, final int min, final int max, final String html, final String cmd, final String[] data) {
        this.writeC(S_HYPERTEXT_INPUT);
        this.writeD(objId);
        this.writeD(0x00000000);// ?
        this.writeD(min);// 數量初始質
        this.writeD(min);// 最低可換數量
        this.writeD(max);// 最高可換數量
        this.writeH(0x0000);// c
        this.writeS(html);// HTML
        this.writeS(cmd);// 命令
        if ((data != null) && (1 <= data.length)) {
            this.writeH(data.length); // 數量
            for (final String datum : data) {
                this.writeS(datum);
            }
        }
    }

    /**
     * 選取物品數量 (銀行管理員)
     *
     * @param objId NPC OBJID
     * @param max   可換數量最小質
     * @param max   可換數量最大質
     * @param html  頁面
     * @param cmd   命令
     */
    public S_ItemCount(final int objId, final int min, final int max, final String html, final String cmd) {
        this.writeC(S_HYPERTEXT_INPUT);
        this.writeD(objId);
        this.writeD(0x00000000);// ?
        this.writeD(min);// 數量初始質
        this.writeD(min);// 最低可換數量
        this.writeD(max);// 最高可換數量
        this.writeH(0x0000);// c
        this.writeS(html);// HTML
        this.writeS(cmd);// 命令
    }

    @Override
    public byte[] getContent() {
        if (this._byte == null) {
            this._byte = this.getBytes();
        }
        return this._byte;
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
