package com.lineage.server.serverpackets;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.ShopCnTable;
import com.lineage.server.model.Instance.L1ItemStatus;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ShopItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * 特殊商店(天寶)
 *
 * @author dexc
 */
public class S_ShopSellListCn extends ServerBasePacket {
    private byte[] _byte = null;

    public S_ShopSellListCn(final L1PcInstance pc, final L1NpcInstance npc) {
        this.buildPacket(pc, npc.getId(), npc.getNpcId());
    }

    private void buildPacket(final L1PcInstance pc, final int tgObjid, final int npcid) {
        this.writeC(S_BUY_LIST);
        this.writeD(tgObjid);
        final ArrayList<L1ShopItem> shopItems = ShopCnTable.get().get(npcid);
        //排序
        Collections.sort(shopItems, new Comparator<L1ShopItem>() {
            @Override
            public int compare(L1ShopItem o1, L1ShopItem o2) {
                return o1.getId() - o2.getId();
            }
        });
        if (shopItems.size() <= 0) {
            this.writeH(0x0000);
            return;
        }
        this.writeH(shopItems.size());
        int i = 0;
        for (final L1ShopItem shopItem : shopItems) {
            i++;
            pc.get_otherList().add_cnList(i, shopItem);
            final L1Item item = shopItem.getItem();
            this.writeD(i);
            this.writeH(item.getGfxId());
            this.writeD(shopItem.getPrice());
            String nameString = "";
            if (shopItem.getEnchantLevel() > 1) {
                nameString = ("+" + shopItem.getEnchantLevel() + " ");
            }
            if (shopItem.getPackCount() > 1) {
                this.writeS(item.getNameId() + " (" + shopItem.getPackCount() + ")");
            } else {
                this.writeS(nameString + item.getNameId());
            }
            L1Item template = ItemTable.get().getTemplate(item.getItemId());
            this.writeD(template.getUseType());// XXX 7.6新增商品分類
            final L1ItemStatus itemInfo = new L1ItemStatus(item);
            // 取回物品資訊
            final byte[] status = itemInfo.getStatusBytes(true).getBytes();
            this.writeC(status.length);
            for (final byte b : status) {
                this.writeC(b);
            }
            // 降低封包量 不傳送詳細資訊
            // this.writeC(0x00);
        }
        //this.writeH(0x17d4); // 0x0000:無顯示 0x0001:珍珠 0x0007:金幣 0x17d4:天寶
        final L1Item adena_Dis = ItemTable.get().getTemplate(pc.get_temp_adena());
        this.writeH(adena_Dis.getItemDescId());
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
