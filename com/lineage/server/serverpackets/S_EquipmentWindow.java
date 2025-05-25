/**
 * License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE").
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR
 * COPYRIGHT LAW IS PROHIBITED.
 * <p>
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND
 * AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE
 * MAY BE CONSIDERED TO BE A CONTRACT,
 * THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 */
package com.lineage.server.serverpackets;

/**
 * 顯示指定物品到裝備窗口.
 *
 * @author jrwz
 * @version 1.0
 * @data 2013-5-2下午11:43:34
 */
public class S_EquipmentWindow extends ServerBasePacket {//src016
    /**
     * 頭盔.
     */
    public static final byte EQUIPMENT_INDEX_HEML = 1;
    /**
     * 盔甲.
     */
    public static final byte EQUIPMENT_INDEX_ARMOR = 2;
    /**
     * T恤.
     */
    public static final byte EQUIPMENT_INDEX_T = 3;
    /**
     * 斗篷.
     */
    public static final byte EQUIPMENT_INDEX_CLOAK = 4;
    /**
     * 靴子.
     */
    public static final byte EQUIPMENT_INDEX_BOOTS = 5;
    /**
     * 手套.
     */
    public static final byte EQUIPMENT_INDEX_GLOVE = 6;
    /**
     * 盾.
     */
    public static final byte EQUIPMENT_INDEX_SHIELD = 7;
    /**
     * 武器.
     */
    public static final byte EQUIPMENT_INDEX_WEAPON = 8;
    /**
     * 項鏈.
     */
    public static final byte EQUIPMENT_INDEX_NECKLACE = 10;
    /**
     * 腰帶.
     */
    public static final byte EQUIPMENT_INDEX_BELT = 11;
    /**
     * 耳環.
     */
    public static final byte EQUIPMENT_INDEX_EARRING = 12;
    /**
     * 戒指1.
     */
    public static final byte EQUIPMENT_INDEX_RING1 = 18;
    /**
     * 戒指2.
     */
    public static final byte EQUIPMENT_INDEX_RING2 = 19;
    /**
     * 戒指3.
     */
    public static final byte EQUIPMENT_INDEX_RING3 = 20;
    /**
     * 戒指4.
     */
    public static final byte EQUIPMENT_INDEX_RING4 = 21;
    /**
     * 符紋.
     */
    public static final byte EQUIPMENT_INDEX_AMULET1 = 22;
    public static final byte EQUIPMENT_INDEX_AMULET2 = 23;
    public static final byte EQUIPMENT_INDEX_AMULET3 = 24;
    public static final byte EQUIPMENT_INDEX_AMULET4 = 25;
    public static final byte EQUIPMENT_INDEX_AMULET5 = 26;
    private byte[] _byte = null;

    /**
     * 顯示指定物品到裝備窗口.
     *
     * @param itemObjId - 對像ID
     * @param index     - 序號
     * @param isEq      - 0:脫下 1:使用
     */
    public S_EquipmentWindow(int itemObjId, int index, boolean isEq) {
        writeC(S_VOICE_CHAT);
        writeC(0x42); // 66
        writeD(itemObjId);
        writeC(index);
        writeBoolean(isEq);
    }

    /*public S_EquipmentWindow(ArrayList<L1ItemInstance> items) {
        this.writeC(S_OPCODE_CHARRESET);
        this.writeC(65);// 41
        this.writeC(items.size());
        for (L1ItemInstance item : items) {
            this.writeD(item.getId());
            this.writeH(item.getEquipWindow());
            this.writeH(0);
        }
    }*/
    public S_EquipmentWindow(int size) {
        this.writeC(S_VOICE_CHAT);
        this.writeC(68);
        this.writeD(size);
        this.writeD(0);
    }

    /**
     * @param type 1是戒指耳環、2是符石
     */
    public S_EquipmentWindow(int type, int value) {
        writeC(S_VOICE_CHAT);
        if (type == 48 || type == 49) {
            writeC(type);
            writeD(value);
            writeC(149);
            writeC(25);
        } else {
            writeC(0x43); // 67
            writeD(type);
            writeD(value);
            writeD(0);
            writeD(0);
            writeD(0);
            writeC(0);
        }
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
