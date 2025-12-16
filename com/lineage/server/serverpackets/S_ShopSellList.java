package com.lineage.server.serverpackets;

import com.lineage.config.ConfigOther;
import com.lineage.config.ConfigRate;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.ShopTable;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.lock.CharSkillReading;
import com.lineage.server.datatables.lock.ShopLimitReading;
import com.lineage.server.model.Instance.L1ItemStatus;
import com.lineage.server.model.Instance.L1ItemInstance;
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

            // 傳送穩定的識別碼，避免客戶端排序後造成索引錯置
            writeD(shopItem.getItemId());
            writeH(item.getGfxId()); // 圖形
            writeD(price); // 價格

            // --- ▼▼▼ 限購數量顯示邏輯 ▼▼▼ ---
            String itemName = item.getNameId(); // 注意：您的版本是使用 NameId
            final int dailyLimit = shopItem.getDailyLimit();

            // 如果商品有限購，顯示 (已購/上限)
            if (dailyLimit > 0) {
                final int currentCount = ShopLimitReading.get().getPurchaseCount(pc.getId(), shopItem.getItemId());
                final int displayCount = Math.max(0, Math.min(currentCount, dailyLimit));
                itemName = String.format("%s (%d/%d)", itemName, displayCount, dailyLimit);
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

            // 維持原本封包格式：名稱後附帶 useType 與狀態資訊（或 0）
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
        Integer[] autoSkillTable = new Integer[]{77,65,132, 1, 33, 20, 25, 27, 43, 54, 60, 187, 225,45};
        Integer[] autoSkillIconTable = new Integer[]{10916,10904,1755, 386, 643, 655, 398, 401, 661, 659, 653, 3081, 6191,10496};
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

    /**
     * 額外清單：以商店清單樣式顯示玩家當前配戴裝備
     * @param viewer 觀看者（用於限購顯示與語系等用途）
     * @param anchorObjId 視窗錨定物件ID（通常給NPC的objId；若無NPC可傳玩家ID）
     * @param target 要顯示其已配戴裝備的對象
     */
    public S_ShopSellList(final L1PcInstance viewer, final int anchorObjId, final L1PcInstance target) {
        writeC(S_BUY_LIST);
        writeD(anchorObjId);

        // 彙整目標當前配戴裝備（只取已裝備且為武器/防具類型）
        final List<L1ItemInstance> equippedItems = new ArrayList<>();
        for (L1ItemInstance it : target.getInventory().getItems()) {
            if (it == null) {
                continue;
            }
            if (!it.isEquipped()) {
                continue;
            }
            // 僅顯示武器(type2=1)與防具(type2=2)，避免將一般道具也列入
            int type2 = it.getItem().getType2();
            if (type2 == 1 || type2 == 2) {
                equippedItems.add(it);
            }
        }

        if (equippedItems.isEmpty()) {
            writeH(0);
            return;
        }

        writeH(equippedItems.size());
        for (L1ItemInstance itemInst : equippedItems) {
            // 唯一識別用ID（不做購買行為，僅供顯示，發送穩定ID即可）
            writeD(itemInst.getId());
            writeH(itemInst.getItem().getGfxId());
            writeD(0); // 價格顯示為 0（純顯示用途）
            writeS(itemInst.getLogName());

            // 商品分類（沿用用法：useType）
            writeD(itemInst.getItem().getUseType());

            if (ConfigOther.SHOPINFO) {
                // 顯示裝備的詳細狀態（以實例生成），與一般商店一致的資料格式
                L1ItemStatus itemInfo = new L1ItemStatus(itemInst);
                byte[] status = itemInfo.getStatusBytes(true).getBytes();
                writeC(status.length);
                for (byte b : status) {
                    writeC(b);
                }
            } else {
                writeC(0);
            }
        }
        writeH(0x0007); // 金幣圖示（僅樣式用途）
    }

    /**
     * 自訂清單：以商店清單樣式顯示一組 L1ShopItem（不收費，純選擇用途）
     * - 會把索引與對應項目存入 pc.get_otherList().add_cnList(index, shopItem)
     * - 用戶端回傳將以「自己作為錨點」回來，需由 C_Result 針對 pc.getTemporary() 進行分流
     */
    public S_ShopSellList(final L1PcInstance pc, final int anchorObjId, final List<L1ShopItem> items) {
        writeC(S_BUY_LIST);
        writeD(anchorObjId);
        if (items == null || items.isEmpty()) {
            writeH(0);
            return;
        }
        writeH(items.size());
        int i = 0;
        for (final L1ShopItem shopItem : items) {
            i++;
            // 改用自選寶箱專用映射，不與商城共用
            pc.get_otherList().add_chooseList(i, shopItem);
            final L1Item item = shopItem.getItem();
            if (item == null) {
                // 保底占位，避免客戶端不同步
                writeD(i);
                writeH(0);
                writeD(0);
                writeS("");
                writeD(0);
                writeC(0);
                continue;
            }
            writeD(i);
            writeH(item.getGfxId());
            writeD(0); // 價格顯示 0（純選擇用途）

            String nameString = "";
            if (shopItem.getEnchantLevel() > 0) {
                nameString = ("+" + shopItem.getEnchantLevel() + " ");
            }
            if (shopItem.getPackCount() > 1) {
                writeS(nameString + item.getNameId() + " (" + shopItem.getPackCount() + ")");
            } else {
                writeS(nameString + item.getNameId());
            }

            // useType 與狀態資訊（維持一致封包格式）
            L1Item template = ItemTable.get().getTemplate(item.getItemId());
            writeD(template != null ? template.getUseType() : 0);
            writeC(0); // 不附帶詳細狀態，減少封包量
        }
        writeH(0x0007);
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
