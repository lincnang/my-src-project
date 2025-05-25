package com.lineage.server.serverpackets;

import com.lineage.config.ConfigOther;
import com.lineage.config.ConfigRate;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.ShopTable;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.lock.CharSkillReading;
import com.lineage.server.model.Instance.L1ItemStatus;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.shop.L1Shop;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ShopItem;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.world.World;

import java.util.ArrayList;
import java.util.List;

public class S_ShopSellList extends ServerBasePacket {
    private byte[] _byte = null;

    public S_ShopSellList(int objId) {
        writeC(S_BUY_LIST);
        writeD(objId);
        L1Object npcObj = World.get().findObject(objId);
        if (!(npcObj instanceof L1NpcInstance)) {
            writeH(0);
            return;
        }
        int npcId = ((L1NpcInstance) npcObj).getNpcTemplate().get_npcId();
        // L1TaxCalculator calc = new L1TaxCalculator(npcId);
        L1Shop shop = ShopTable.get().get(npcId);
        List<L1ShopItem> shopItems = shop.getSellingItems();
        if (shopItems.size() <= 0) {
            writeH(0);
            return;
        }
        writeH(shopItems.size());
        for (int i = 0; i < shopItems.size(); i++) {
            L1ShopItem shopItem = (L1ShopItem) shopItems.get(i);
            L1Item item = shopItem.getItem();
            int price = (int) (shopItem.getPrice() * ConfigRate.RATE_SHOP_SELLING_PRICE);// 物品單價
            writeD(i);// 排序
            writeH(shopItem.getItem().getGfxId());// 圖形
            writeD(price);
            /** [原碼] 出售強化物品 */
            String nameString = "";
            if (shopItem.getEnchantLevel() > 1) {
                nameString = ("+" + shopItem.getEnchantLevel() + " ");
            }
            if (shopItem.getPackCount() > 1) {
                writeS(item.getNameId() + " (" + shopItem.getPackCount() + ")");// 名稱
            } else {
                writeS(nameString + item.getNameId());// 名稱
            }
            // writeD(0);
            L1Item template = ItemTable.get().getTemplate(item.getItemId());
            this.writeD(template.getUseType());// XXX 7.6新增商品分類
            if (ConfigOther.SHOPINFO) {
                L1ItemStatus itemInfo = new L1ItemStatus(item);
                // 取回物品資訊
                byte[] status = itemInfo.getStatusBytes(true).getBytes();
                writeC(status.length);
                for (byte b : status) {
                    writeC(b);
                }
            } else {
                // 降低封包量 不傳送詳細資訊
                writeC(0);
            }
        }
        writeH(0x0007); // 7 = 金幣為單位 顯示總金額
    }

    public S_ShopSellList(L1NpcInstance npc) {
        writeC(S_BUYABLE_SPELL_LIST);
        writeD(npc.getId());
        int npcId = npc.getNpcTemplate().get_npcId();
        L1Shop shop = ShopTable.get().get(npcId);
        List<L1ShopItem> shopItems = shop.getSellingItems();
        if (shopItems.size() <= 0) {
            writeH(0);
            return;
        }
        writeH(shopItems.size());
        for (int i = 0; i < shopItems.size(); i++) {
            L1ShopItem shopItem = (L1ShopItem) shopItems.get(i);
            L1Item item = shopItem.getItem();
            int price = shopItem.getPrice();
            writeD(i);
            writeH(shopItem.getItem().getGfxId());
            writeD(price);
            if (shopItem.getPackCount() > 1) {
                writeS(item.getNameId() + " (" + shopItem.getPackCount() + ")");
            } else {
                writeS(item.getNameId());
            }
            writeC(0);
        }
        writeH(7);
    }

    public S_ShopSellList(final L1PcInstance pc) {
        Integer[] autoSkillTable = new Integer[]{132, 1, 33, 20, 25, 27, 43, 54, 60, 187, 225};
        Integer[] autoSkillIconTable = new Integer[]{1755, 386, 643, 655, 398, 401, 661, 659, 653, 3081, 6191};
        writeC(S_BUY_LIST);
        writeD(pc.getId());
        ArrayList<Integer> skills = new ArrayList<>();
        for (int i = 0; i < autoSkillTable.length; i++) {
            if (CharSkillReading.get().spellCheck(pc.getId(), autoSkillTable[i])) {
                skills.add(autoSkillTable[i]);
            }
        }
        writeH(skills.size());
        for (int i = 0; i < skills.size(); i++) {
            L1Skills skill = SkillsTable.get().getTemplate(skills.get(i));
            if (skill == null) {
                continue;
            }
            // 取得 autoSkillTable 中的技能ID
            for (int j = 0; j < autoSkillTable.length; j++) {
                if (autoSkillTable[j] == skills.get(i)) {
                    writeD(autoSkillTable[j]);
                    writeH(autoSkillIconTable[j]);            // Gfx
                    break;
                }
            }
            writeD(0);                        // Price
            writeS(skill.getName());                // Name
            writeD(0);
            writeC(0x00);
        }
        writeH(7);
    }

    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
