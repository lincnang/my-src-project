package com.lineage.server.serverpackets;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;

/**
 * 物品資訊訊息(使用String-c.tbl)
 *
 * @author dexc
 */
public class S_IdentifyDesc extends ServerBasePacket {
    private byte[] _byte = null;

    /**
     * 物品資訊訊息(使用ItemDesc-c.tbl)
     */
    public S_IdentifyDesc(final L1ItemInstance item) {
        this.buildPacket(item);
    }

    /**
     * 物品資訊訊息(使用String-c.tbl) 測試
     */
    public S_IdentifyDesc() {
        // 骰子匕首
        L1ItemInstance item = ItemTable.get().createItem(2);
        this.writeC(S_IDENTIFY_CODE);
        this.writeH(item.getItem().getItemDescId());
        this.writeH(134); // \f1%0：小打擊%1 大打擊%2
        this.writeC(3);
        this.writeS(item.getName());
        this.writeS(item.getItem().getDmgSmall() + "+" + item.getEnchantLevel());
        this.writeS(item.getItem().getDmgLarge() + "+" + item.getEnchantLevel());
        this.writeS(String.valueOf(item.getWeight()));
    }

    private void buildPacket(final L1ItemInstance item) {
        this.writeC(S_IDENTIFY_CODE);
        this.writeH(item.getItem().getItemDescId());
        final StringBuilder name = new StringBuilder();
        switch (item.getItem().getBless()) {
            case 0:// 祝福
                name.append("$227 ");
                break;
            case 2:// 詛咒
                name.append("$228 ");
                break;
        }
        name.append(item.getItem().getNameId());
        switch (item.getItem().getType2()) {
            case 1: // weapon
                this.writeH(0x0086); // 134 \f1%0：小打擊%1 大打擊%2
                this.writeC(0x03);
                this.writeS(name.toString());
                this.writeS(item.getItem().getDmgSmall() + "+" + item.getEnchantLevel());
                this.writeS(item.getItem().getDmgLarge() + "+" + item.getEnchantLevel());
                break;
            case 2: // armor
                switch (item.getItem().getItemId()) {
                    default: // 其餘防具
                        this.writeH(0x0087); // 135 \f1%0：防禦力%1 防禦具
                        this.writeC(0x02);
                        this.writeS(name.toString());
                        this.writeS(Math.abs(item.getItem().get_ac()) + "+" + item.getEnchantLevel());
                        break;
                }
                break;
            case 0: // etcitem
                switch (item.getItem().getType()) {
                    case 1: // wand
                        this.writeH(0x0089); // 137 \f1%0：使用可能回數%1〔重%2〕
                        this.writeC(0x03);
                        this.writeS(name.toString());
                        this.writeS(String.valueOf(item.getChargeCount()));
                        break;
                    case 2: // light系
                        this.writeH(0x008a);// 138 \f1%0：〔重%1〕
                        this.writeC(0x02);
                        name.append(": $231 "); // 殘燃料
                        name.append(String.valueOf(item.getRemainingTime()));
                        this.writeS(name.toString());
                        break;
                    case 7: // food
                        this.writeH(0x0088); // 136 \f1%0：滿腹度%1〔重%2〕
                        this.writeC(0x03);
                        this.writeS(name.toString());
                        this.writeS(String.valueOf(item.getItem().getFoodVolume()));
                        break;
                    default:
                        this.writeH(0x008a); // 138 \f1%0：〔重%1〕
                        this.writeC(0x02);
                        this.writeS(name.toString());
                        break;
                }
                this.writeS(String.valueOf(item.getWeight()));
                break;
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
