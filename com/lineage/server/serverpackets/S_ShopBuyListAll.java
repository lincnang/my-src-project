package com.lineage.server.serverpackets;

import com.lineage.server.datatables.ShopTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;

import java.util.HashMap;
import java.util.Map;

public class S_ShopBuyListAll extends ServerBasePacket {
    private byte[] _byte = null;

    public S_ShopBuyListAll(L1PcInstance pc, L1NpcInstance npc) {
        Map<L1ItemInstance, Integer> assessedItems = assessItems(pc.getInventory());
        if (assessedItems.isEmpty()) {
            pc.sendPackets(new S_NoSell(npc));
            return;
        }
        if (assessedItems.size() <= 0) {
            pc.sendPackets(new S_NoSell(npc));
            return;
        }
        writeC(S_SELL_LIST);
        writeD(npc.getId());
        writeH(assessedItems.size());
        for (L1ItemInstance key : assessedItems.keySet()) {
            writeD(key.getId());
            writeD(((Integer) assessedItems.get(key)).intValue());
        }
        writeH(0x0007); // 0x0000:無顯示 0x0001:珍珠 0x0007:金幣 0x17d4:天寶
    }

    private Map<L1ItemInstance, Integer> assessItems(L1PcInventory inv) {
        Map<L1ItemInstance, Integer> result = new HashMap<L1ItemInstance, Integer>();
        for (L1ItemInstance item : inv.getItems()) {
            switch (item.getItem().getItemId()) {
                case 40308: // 金幣
                case 41246: // 魔法結晶體
                case 40314: // 項圈
                case 40316: // 高等寵物項圈
                case 83000: // 貝利
                case 83022: // 黃金貝利
                case 80033: // 推廣銀幣
                case 83048: // 龍之幣
                    break;
                default:
                    if (!item.isEquipped()) {
                        int itemid = item.getItemId();
                        //有時間性的
                        if (item.get_time() != null) {
                            continue;
                        }
					/*ArrayList<Integer> cnlist = ShopCnTable.get().get_cnitemidlist();
					if (cnlist.contains(itemid)) {// 如果在商城物品清單裡則略過
						continue;   //src045
					}
					ArrayList<Integer> uplist = ItemPowerUpdateTable.get().get_updeatitemidlist();
					if (uplist.contains(itemid)) {// 如果在物品升級列表則略過
						continue;
					}
					boolean contains = ShopCnTable.get().isSelling(110811, itemid);// 推廣銀幣商人
					if (contains) {// 在推廣銀幣商人販賣物品中則略過
						continue;
					}
					L1Shop shop = ShopTable.get().get(110641);// 貝利商人
					if (shop != null) {
						if (shop.isSelling(itemid)) {// 如果在貝利販賣物品中則略過
							continue;
						}
						if (shop.isPurchasing(itemid)) {// 如果在貝利收購物品中則略過
							continue;
						}
					}
					L1Shop shop2 = ShopTable.get().get(200206);// 湖中女神妲蒂斯
					if (shop2 != null) {
						if (shop2.isSelling(itemid)) {// 如果在湖中女神妲蒂斯販賣物品中則略過
							continue;
						}
						if (shop2.isPurchasing(itemid)) {// 如果在湖中女神妲蒂斯收購物品中則略過
							continue;
						}
					}
					L1Shop shop3 = ShopTable.get().get(110642);// 星月收集者
					if (shop3 != null) {
						if (shop3.isSelling(itemid)) {// 如果在星月收集者販賣物品中則略過
							continue;
						}
						if (shop3.isPurchasing(itemid)) {// 如果在星月收集者收購物品中則略過
							continue;
						}
					}*/
                        if (item.getBless() >= 128) {// 封印狀態
                            continue;
                        }
                        double price = ShopTable.get().getPrice(itemid);// 取回回收單價
                        double maxchargeCount = item.getItem().getMaxChargeCount();// 最大使用次數
                        double chargecount = item.getChargeCount();// 剩餘使用次數
                        if (maxchargeCount > 0) {
                            price = price * (chargecount / maxchargeCount);
                            // System.out.println("price =" + price);
                        }
                        if (price > 0) {
                            result.put(item, new Integer((int) price));
                        }
                    }
                    break;
            }
        }
        return result;
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
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.serverpackets.S_ShopBuyListAll JD-Core Version: 0.6.2
 */