package com.lineage.server.model.shop;

import com.lineage.config.ConfigRate;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.AccountReading;
import com.lineage.server.datatables.lock.CastleReading;
import com.lineage.server.datatables.lock.CharItemsTimeReading;
import com.lineage.server.datatables.lock.TownReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.L1TaxCalculator;
import com.lineage.server.model.L1TownLocation;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Account;
import com.lineage.server.templates.L1Castle;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ShopItem;
import com.lineage.server.utils.IntRange;
import com.lineage.server.utils.RangeInt;
import com.lineage.server.world.World;
import william.server_lv;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class L1Shop {
    private final int _npcId;
    private final int _currencyItemId;
    private final List<L1ShopItem> _sellingItems;
    private final List<L1ShopItem> _purchasingItems;

    public L1Shop(int npcId, int currencyItemId, List<L1ShopItem> sellingItems, List<L1ShopItem> purchasingItems) {
        if ((sellingItems == null) || (purchasingItems == null)) {
            throw new NullPointerException();
        }
        _npcId = npcId;
        _currencyItemId = currencyItemId;
        _sellingItems = sellingItems;
        _purchasingItems = purchasingItems;
    }

    public int getNpcId() {
        return _npcId;
    }

    /**
     * 傳回商店販賣列表
     *
     * @return
     */
    public List<L1ShopItem> getSellingItems() {
        return _sellingItems;
    }

    /**
     * 傳回商店購買列表
     *
     * @return
     */
    public List<L1ShopItem> getPurchasingItems() {
        return _purchasingItems;
    }

    /**
     * 是否有販賣此樣物品
     *
     * @param itemid
     * @return
     */
    public boolean isSelling(int itemid) {
        for (L1ShopItem shopitem : _sellingItems) {
            if (shopitem.getItemId() == itemid) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否有收購此樣物品
     *
     * @param itemid
     * @return
     */
    public boolean isPurchasing(int itemid) {
        for (L1ShopItem shopitem : _purchasingItems) {
            if (shopitem.getItemId() == itemid) {
                return true;
            }
        }
        return false;
    }

    private boolean isPurchaseableItem(L1ItemInstance item) {
        if (item == null) {
            return false;
        }
        if (item.isEquipped()) {
            return false;
        }
        if (item.getEnchantLevel() != 0) {
            return false;
        }
        if (item.getBless() >= 128) {
            return false;
        }
        return true;
    }

    private L1ShopItem getPurchasingItem(int itemId) {
        for (L1ShopItem shopItem : _purchasingItems) {
            if (shopItem.getItemId() == itemId) {
                return shopItem;
            }
        }
        return null;
    }

    public L1AssessedItem assessItem(L1ItemInstance item) {
        L1ShopItem shopItem = getPurchasingItem(item.getItemId());
        if (shopItem == null) {
            return null;
        }
        return new L1AssessedItem(item.getId(), getAssessedPrice(shopItem));
    }

    private int getAssessedPrice(L1ShopItem item) {
        return (int) (item.getPrice() * ConfigRate.RATE_SHOP_PURCHASING_PRICE / item.getPackCount());
    }

    public List<L1AssessedItem> assessItems(L1PcInventory inv) {
        final List<L1AssessedItem> result = new ArrayList<L1AssessedItem>();
        for (final L1ShopItem item : this._purchasingItems) {
            for (final L1ItemInstance targetItem : inv.findItemsId(item.getItemId())) {
                if (!this.isPurchaseableItem(targetItem)) {
                    continue;
                }
                result.add(new L1AssessedItem(targetItem.getId(), this.getAssessedPrice(item)));
            }
        }
        return result;
    }

    /**
     * 檢查販賣物品是否成功
     *
     * @param pc
     * @param orderList
     * @return
     */
    private boolean ensureSell(L1PcInstance pc, L1ShopBuyOrderList orderList) {
        int price = orderList.getTotalPrice();
        if (!RangeInt.includes(price, 0, 2000000000)) {
            pc.sendPackets(new S_ServerMessage(904, "2000000000"));
            return false;
        }
        for (L1ShopBuyOrder order : orderList.getList()) {
            int itemid = order.getItem().getItemId();
            if (itemid == 56148) {// 購買物品為妲蒂斯魔石
                if ((pc.getInventory().findItemId(56147) != null) || (pc.getInventory().findItemId(56148) != null)) {// 身上已有妲蒂斯魔石OR真妲蒂斯魔石
                    pc.sendPackets(new S_ServerMessage("身上已有妲蒂斯的魔力。"));
                    return false;
                }
            }
        }
        if (!pc.getInventory().checkItem(_currencyItemId, price)) {
            L1Item item = ItemTable.get().getTemplate(_currencyItemId);
            pc.sendPackets(new S_ServerMessage(337, item.getName())); // \%s不足。
            return false;
        }
        int currentWeight = pc.getInventory().getWeight() * 1000;
        if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000.0D) {
            pc.sendPackets(new S_ServerMessage(82));
            return false;
        }
        int totalCount = pc.getInventory().getSize();
        for (L1ShopBuyOrder order : orderList.getList()) {
            L1Item temp = order.getItem().getItem();
            if (temp.isStackable()) {
                if (!pc.getInventory().checkItem(temp.getItemId())) {
                    totalCount++;
                }
            } else {
                totalCount++;
            }
        }
        if (totalCount > 180) {
            pc.sendPackets(new S_ServerMessage(263));
            return false;
        }
        return true;
    }

    /**
     * 更新城堡寶庫稅金
     *
     * @param orderList
     */
    private void payCastleTax(L1ShopBuyOrderList orderList) {
        L1TaxCalculator calc = orderList.getTaxCalculator();
        int price = orderList.getTotalPrice();
        int castleId = L1CastleLocation.getCastleIdByNpcid(_npcId);
        int castleTax = calc.calcCastleTaxPrice(price);
        int nationalTax = calc.calcNationalTaxPrice(price);
        if ((castleId == 7) || (castleId == 8)) {
            castleTax += nationalTax;
            nationalTax = 0;
        }
        if ((castleId != 0) && (castleTax > 0)) {
            L1Castle castle = CastleReading.get().getCastleTable(castleId);
            synchronized (castle) {
                long money = castle.getPublicMoney();
                money += castleTax;
                castle.setPublicMoney(money);
                CastleReading.get().updateCastle(castle);
            }
            //			L1Castle kent = CastleReading.get().getCastleTable(1);
            //			synchronized (kent) {
            //				long money = kent.getPublicMoney();
            //				money += castleTax;
            //				kent.setPublicMoney(money);
            //				CastleReading.get().updateCastle(kent);
            //			}
            //			if (nationalTax > 0) {
            //				L1Castle aden = CastleReading.get().getCastleTable(7);
            //				synchronized (aden) {
            //					long money = aden.getPublicMoney();
            //					money += nationalTax;
            //					aden.setPublicMoney(money);
            //					CastleReading.get().updateCastle(aden);
            //				}
            //			}
        }
    }

    /**
     * 更新迪亞德寶庫稅金
     *
     * @param orderList
     */
    @SuppressWarnings("unused")
    private void payDiadTax(L1ShopBuyOrderList orderList) {
        L1TaxCalculator calc = orderList.getTaxCalculator();
        int price = orderList.getTotalPrice();
        int diadTax = calc.calcDiadTaxPrice(price);
        if (diadTax <= 0) {
            return;
        }
        L1Castle castle = CastleReading.get().getCastleTable(8);
        synchronized (castle) {
            long money = castle.getPublicMoney();
            money += diadTax;
            castle.setPublicMoney(money);
            CastleReading.get().updateCastle(castle);
        }
    }

    /**
     * 更新村莊稅金
     *
     * @param orderList
     */
    @SuppressWarnings("unused")
    private void payTownTax(L1ShopBuyOrderList orderList) {
        int price = orderList.getTotalPrice();
        if (World.get().isProcessingContributionTotal()) {
            int town_id = L1TownLocation.getTownIdByNpcid(_npcId);
            if ((town_id >= 1) && (town_id <= 10)) {
                TownReading.get().addSalesMoney(town_id, price);
            }
        }
    }

    /**
     * 更新各區域稅金
     *
     * @param orderList
     */
    @SuppressWarnings("unused")
    private void payTax(L1ShopBuyOrderList orderList) {
        payCastleTax(orderList);
        //payTownTax(orderList);
        //payDiadTax(orderList);
    }

    /**
     * 將商店出售物品轉移給玩家
     *
     * @param inv
     * @param orderList
     * @param pc
     */
    private void sellItems(L1PcInventory inv, L1ShopBuyOrderList orderList, L1PcInstance pc, int npcId) {
        if (orderList == null) {
            return;
        }
        int price = orderList.getTotalPrice();
        if (price <= 0) {
            return;
        }
        if (!inv.consumeItem(_currencyItemId, price)) {
            return;
        }
        //租借NPC
        boolean rent = npcId == 94005 ? true : false;
        for (L1ShopBuyOrder order : orderList.getList()) {
            int itemId = order.getItem().getItemId();
            long amount = order.getCount();// 堆疊數量
            int enchantlevel = order.getItem().getEnchantLevel();
            if (amount > 0L) {// 堆疊數量大於0
                L1ItemInstance item = ItemTable.get().createItem(itemId);
                item.setCount(amount);
                /** [原碼] 出售強化物品 */
                item.setEnchantLevel(enchantlevel);
                server_lv.forIntensifyArmor(pc, item);////src056
                item.setIdentified(true);
                if (rent) {
                    long time = System.currentTimeMillis();
                    long x1 = 10800L;// 10800秒(3小時)
                    //long x1 = 60L;// 60秒(測試)
                    long x2 = x1 * 1000L;
                    long upTime = x2 + time;
                    Timestamp ts = new Timestamp(upTime);
                    item.set_time(ts);
                    CharItemsTimeReading.get().addTime(item.getId(), ts);
                    //pc.sendPackets(new S_ItemName(item));
                }
                inv.storeItem(item);
                pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
            }
        }
    }

    /**
     * 商店出售物品給玩家
     *
     * @param pc
     * @param orderList
     */
    public void sellItems(L1PcInstance pc, L1ShopBuyOrderList orderList, int npcId) {
        if (orderList.isEmpty()) {
            return;
        }
        // 成長果實系統(Tam幣)
        if (getNpcId() == 7200002 || getNpcId() == 7200003) { // Tam幣商人增加
            if (!TamMerchant1(pc, orderList)) {
                return;
            }
            TamMerchant2(pc, pc.getInventory(), orderList);
            return;
        }
        if (!ensureSell(pc, orderList)) {
            return;
        }
        sellItems(pc.getInventory(), orderList, pc, npcId);
        //稅收
        payTax(orderList);
    }

    /**
     * 成長果實系統(Tam幣)
     *
     * @param pc
     * @param orderList
     * @return
     */
    private boolean TamMerchant1(L1PcInstance pc, L1ShopBuyOrderList orderList) {
        int price = orderList.getTotalPrice();
        if (!IntRange.includes(price, 0, 20000000)) {
            pc.sendPackets(new S_SystemMessage("系統：TAM幣總量超過2000萬，購買失敗。"));
            return false;
        }
        L1Account account = pc.getNetConnection().getAccount();
        if (account.get_tam_point() < price) {
            // 2綠色 3紅色  13黃色 14白色 22淺綠 44淺藍 45淺紅
            pc.sendPackets(new S_AllChannelsChat("TAM幣不足。", 2));
            return false;
        }
        int currentWeight = pc.getInventory().getWeight() * 1000;
        if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
            pc.sendPackets(new S_ServerMessage(82));
            return false;
        }
        int totalCount = pc.getInventory().getSize();
        L1Item temp = null;
        for (L1ShopBuyOrder order : orderList.getList()) {
            temp = order.getItem().getItem();
            if (temp.isStackable()) {
                if (!pc.getInventory().checkItem(temp.getItemId())) {
                    totalCount += 1;
                }
            } else {
                totalCount += 1;
            }
        }
        if (totalCount > 180) {
            pc.sendPackets(new S_ServerMessage(263));
            return false;
        }
        if (price <= 0 || price > 2000000000) {
            pc.sendPackets(new S_Disconnect());
            return false;
        }
        return true;
    }

    /**
     * 成長果實系統(Tam幣)
     *
     * @param pc
     * @param inv
     * @param orderList
     */
    private void TamMerchant2(L1PcInstance pc, L1PcInventory inv, L1ShopBuyOrderList orderList) {
        if (inv.getOwner().getNetConnection().getAccount().get_tam_point() < orderList.getTotalPrice()) {
            throw new IllegalStateException("TAM幣不足。.");
        }
        L1Account account = pc.getNetConnection().getAccount();
        if (orderList.getTotalPrice() <= account.get_tam_point()) {
            account.set_tam_point((account.get_tam_point()) - (orderList.getTotalPrice()));
            pc.sendPackets(new S_TamWindow(pc));
            try {
                AccountReading.get().updatetam(pc.getAccountName(), account.get_tam_point());
            } catch (Exception e) {
            }
        }
        L1ItemInstance item = null;
        for (L1ShopBuyOrder order : orderList.getList()) {
            int itemId = order.getItem().getItemId();
            int amount = order.getCount();
            item = ItemTable.get().createItem(itemId);
            item.setCount(amount);
            item.setIdentified(true);
            pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
            inv.storeItem(item);
        }
    }

    /**
     * 商店購買玩家物品並給予金幣
     *
     * @param orderList
     * @param pc
     */
    public void buyItems(L1ShopSellOrderList orderList, L1PcInstance pc) {
        L1PcInventory inv = orderList.getPc().getInventory();
        int totalPrice = 0;
        for (L1ShopSellOrder order : orderList.getList()) {
            long count = inv.removeItem(order.getItem().getTargetId(), order.getCount());
            totalPrice = (int) (totalPrice + order.getItem().getAssessedPrice() * count);
        }
        totalPrice = RangeInt.ensure(totalPrice, 0, 2000000000);
        if (totalPrice > 0) {
            L1ItemInstance item = ItemTable.get().createItem(_currencyItemId);
            item.setCount(totalPrice);
            pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
            inv.storeItem(item);
        }
    }

    public L1ShopBuyOrderList newBuyOrderList() {
        return new L1ShopBuyOrderList(this);
    }

    public L1ShopSellOrderList newSellOrderList(L1PcInstance pc) {
        return new L1ShopSellOrderList(this, pc);
    }
}
