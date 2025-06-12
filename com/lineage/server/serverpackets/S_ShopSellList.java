package com.lineage.server.serverpackets;

import com.lineage.config.ConfigOther;
import com.lineage.config.ConfigRate;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.ShopTable;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.lock.CharSkillReading;
import com.lineage.server.datatables.lock.ShopLimitReading;
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

    /**
     * 修改過的建構子
     * @param pc 觀看商店的玩家
     * @param objId 商店NPC的OBJID
     */
    public S_ShopSellList(final L1PcInstance pc, final int objId) {
        // --- ▼▼▼【修正】將封包代碼改回您專案中原有的 S_BUY_LIST ▼▼▼ ---
        writeC(S_BUY_LIST);
        // --- ▲▲▲ ---

        writeD(objId);
        L1Object npcObj = World.get().findObject(objId);
        if (!(npcObj instanceof L1NpcInstance)) {
            writeH(0);
            return;
        }
        int npcId = ((L1NpcInstance) npcObj).getNpcTemplate().get_npcId();
        L1Shop shop = ShopTable.get().get(npcId);
        if (shop == null) {
            writeH(0);
            return;
        }
        List<L1ShopItem> shopItems = shop.getSellingItems();
        if (shopItems.isEmpty()) {
            writeH(0);
            return;
        }
        writeH(shopItems.size());
        for (int i = 0; i < shopItems.size(); i++) {
            L1ShopItem shopItem = shopItems.get(i);
            L1Item item = shopItem.getItem();
            if (item == null) {
                continue;
            }
            int price = (int) (shopItem.getPrice() * ConfigRate.RATE_SHOP_SELLING_PRICE);

            writeD(i); // 排序
            writeH(item.getGfxId()); // 圖形
            writeD(price); // 價格

            // --- ▼▼▼ 限購數量顯示邏輯 ▼▼▼ ---
            String itemName = item.getNameId(); // 注意：您的版本是使用 NameId
            final int dailyLimit = shopItem.getDailyLimit();

            // 如果商品有限購
            if (dailyLimit > 0) {
                final int currentCount = ShopLimitReading.get().getPurchaseCount(pc.getId(), shopItem.getItemId());
                itemName = String.format("%s (%d/%d)", itemName, currentCount, dailyLimit);
            }

            // 處理強化等級和組合包顯示
            String nameString = "";
            if (shopItem.getEnchantLevel() > 0) { // 改為 > 0 就顯示
                nameString = ("+" + shopItem.getEnchantLevel() + " ");
            }

            if (shopItem.getPackCount() > 1) {
                writeS(nameString + itemName + " (" + shopItem.getPackCount() + ")");
            } else {
                writeS(nameString + itemName);
            }
            // --- ▲▲▲ ---

            L1Item template = ItemTable.get().getTemplate(item.getItemId());
            this.writeD(template.getUseType());
            if (ConfigOther.SHOPINFO) {
                L1ItemStatus itemInfo = new L1ItemStatus(item);
                byte[] status = itemInfo.getStatusBytes(true).getBytes();
                writeC(status.length);
                for (byte b : status) {
                    writeC(b);
                }
            } else {
                writeC(0);
            }
        }
        writeH(0x0007);
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
        for (Integer value : autoSkillTable) {
            if (CharSkillReading.get().spellCheck(pc.getId(), value)) {
                skills.add(value);
            }
        }
        writeH(skills.size());
        for (Integer integer : skills) {
            L1Skills skill = SkillsTable.get().getTemplate(integer);
            if (skill == null) {
                continue;
            }
            // 取得 autoSkillTable 中的技能ID
            for (int j = 0; j < autoSkillTable.length; j++) {
                if (autoSkillTable[j] == integer) {
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
