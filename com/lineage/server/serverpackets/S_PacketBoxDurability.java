package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;

/**
 * 更新武器的損壞狀態(會變動滑鼠鼠標攻擊狀態)
 *
 * @author Roy
 */
public class S_PacketBoxDurability extends ServerBasePacket {
    /**
     * 更新武器的損壞狀態(會變動滑鼠鼠標攻擊狀態)
     *
     * @param item
     */
    public S_PacketBoxDurability(final L1ItemInstance item) {
        writeC(S_EVENT);
        writeC(S_PacketBox.WEAPON_DURABILITY);// 138
        writeC(item.get_durability());
    }

    /**
     * 更新武器的損壞狀態(空)
     *
     * @param 空
     */
    public S_PacketBoxDurability() {
        writeC(S_EVENT);
        writeC(S_PacketBox.WEAPON_DURABILITY);// 138
        writeC(0x00);
    }

    @Override
    public byte[] getContent() {
        return getBytes();
    }

    @Override
    public String getType() {
        return "[S] " + this.getClass().getSimpleName() + " [S->C 發送封包盒子(更新武器的損壞狀態)]";
    }
}
