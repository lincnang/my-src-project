package com.lineage.server.model.drop;

import com.lineage.config.ConfigRate;
import com.lineage.data.event.PowerItemSet;
import com.lineage.server.datatables.*;
import com.lineage.server.datatables.lock.CharItemPowerReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.templates.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;

/**
 * NPC持有物品取回
 *
 * @author dexc
 */
public class SetDrop implements SetDropExecutor {//src016
    private static final Log _log = LogFactory.getLog(SetDrop.class);
    private static final Random _random = new Random();
    private static Map<Integer, ArrayList<L1Drop>> _droplist;
    private static Map<Integer, ArrayList<L1DropMap>> _droplistX;
    private static Map<Integer, L1DropMob> _moblist; // 全怪物掉落

    /**
     * 設置掉落資料
     *
     * @param droplists
     */
    public void addDropMap(final Map<Integer, ArrayList<L1Drop>> droplists) {
        if (_droplist != null) {
            _droplist.clear();
        }
        _droplist = droplists;
    }

    /**
     * 設置指定MAP掉落資料
     *
     * @param droplists
     */
    @Override
    public void addDropMapX(final Map<Integer, ArrayList<L1DropMap>> droplists) {
        if (_droplistX != null) {
            _droplistX.clear();
        }
        _droplistX = droplists;
    }

    /**
     * 設定全怪物掉落資料
     *
     * @param droplists
     */
    public void addDropMob(Map<Integer, L1DropMob> droplists) {
        if (_moblist != null) {
            _moblist.clear();
        }
        _moblist = droplists;
    }

    /**
     * NPC持有物品資料取回
     *
     * @param npc
     * @param inventory
     */
    @Override
    public void setDrop(final L1NpcInstance npc, final L1Inventory inventory) {
        setDrop(npc, inventory, 0.0);
    }

    /**
     * NPC持有物品資料取回
     *
     * @param npc
     * @param inventory
     * @param random
     */
    @Override
    public void setDrop(final L1NpcInstance npc, final L1Inventory inventory, final double random) {
        // NPC掉落資料取回
        final int mobId = npc.getNpcTemplate().get_npcId();
        // NPC位置
        final int mapid = npc.getMapId();
        ArrayList<L1DropMap> droplistX = _droplistX.get(mapid);
        if (droplistX != null) {
            setDrop(npc, inventory, droplistX);
        }
        // 全怪掉落
        if (_moblist != null) {
            setDrop(npc, inventory, _moblist);
        }
        final ArrayList<L1Drop> dropList = _droplist.get(mobId);
        if (dropList == null) {
            return;
        }
        final Calendar cal = Calendar.getInstance();
        final int day_of_week = cal.get(Calendar.DAY_OF_WEEK);
        // 取回增加倍率
        double droprate = ConfigRate.RATE_DROP_ITEMS;
        if (day_of_week == Calendar.SUNDAY || day_of_week == Calendar.SATURDAY) {
            droprate = (droprate + ConfigRate.RATE_DROP_ITEMS_WEEK);
        }
        if (droprate <= 0) {
            droprate = 0;
        }
        droprate += random;
        double adenarate = ConfigRate.RATE_DROP_ADENA;
        if (day_of_week == Calendar.SUNDAY || day_of_week == Calendar.SATURDAY) {
            adenarate = (adenarate + ConfigRate.RATE_DROP_ADENA_WEEK);
        }
        if (adenarate <= 0) {
            adenarate = 0;
        }
        if ((droprate <= 0) && (adenarate <= 0)) {
            return;
        }
        for (final L1Drop drop : dropList) {
            // 掉落物品編號
            final int itemId = drop.getItemid();
            // 物品為金幣掉落數量為0
            if ((adenarate == 0) && (itemId == L1ItemId.ADENA)) {
                continue;
            }
            // 取回隨機機率
            final int randomChance = _random.nextInt(0xf4240) + 1;
            // 地圖增加掉率
            final double rateOfMapId = MapsTable.get().getDropRate(npc.getMapId());
            // 指定物品增加掉率
            final double rateOfItem = DropItemTable.get().getDropRate(itemId);
            if ((droprate == 0) || (drop.getChance() * droprate * rateOfMapId * rateOfItem < randomChance)) {
                continue;
            }
            // 指定的物件提高掉落數量倍率
            final double amount = DropItemTable.get().getDropAmount(itemId);
            final long min = (long) (drop.getMin() * amount);
            final long max = (long) (drop.getMax() * amount);
            long itemCount = min;
            final long addCount = max - min + 1;
            if (addCount > 1) {
                itemCount += _random.nextInt((int) addCount);
            }
            // 物件為金幣 加入倍率
            if (itemId == L1ItemId.ADENA) {
                itemCount *= adenarate;
            }
            // 數量為0
            if (itemCount < 0) {
                itemCount = 0;
            }
            // 限制持有數量
            if (itemCount > 2000000000) {
                itemCount = 2000000000;
            }
            if (itemCount > 0) {
                additem(npc, inventory, itemId, itemCount);
            } else {
                _log.error("NPC加入背包物件數量為0(" + mobId + " itemId: " + itemId + ")");
            }
        }
    }

    /**
     * 指定地圖NPC持有物品資料取回
     *
     * @param npc
     * @param inventory 怪物背包
     * @param dropList
     */
    private void setDrop(final L1NpcInstance npc, final L1Inventory inventory, final ArrayList<L1DropMap> dropListX) {
        final Calendar cal = Calendar.getInstance();
        final int day_of_week = cal.get(Calendar.DAY_OF_WEEK);
        // 取回增加倍率
        double droprate = ConfigRate.RATE_DROP_ITEMS;
        if (day_of_week == Calendar.SUNDAY || day_of_week == Calendar.SATURDAY) {
            droprate = (droprate + ConfigRate.RATE_DROP_ITEMS_WEEK);
        }
        if (droprate <= 0) {
            droprate = 0;
        }
        double adenarate = ConfigRate.RATE_DROP_ADENA;
        if (day_of_week == Calendar.SUNDAY || day_of_week == Calendar.SATURDAY) {
            adenarate = (adenarate + ConfigRate.RATE_DROP_ADENA_WEEK);
        }
        if (adenarate <= 0) {
            adenarate = 0;
        }
        if ((droprate <= 0) && (adenarate <= 0)) {
            return;
        }
        for (final L1DropMap drop : dropListX) {
            // 掉落物品編號
            final int itemId = drop.getItemid();
            // 物品為金幣掉落數量為0
            if ((adenarate == 0) && (itemId == L1ItemId.ADENA)) {
                continue;
            }
            // 怪物所在地圖不等於設定地圖則略過
            if (npc.getMapId() != drop.get_mapid()) {
                continue;
            }
            // 取回隨機機率
            final int randomChance = _random.nextInt(0xf4240) + 1;
            final double rateOfMapId = MapsTable.get().getDropRate(npc.getMapId());
            final double rateOfItem = DropItemTable.get().getDropRate(itemId);
            boolean noadd = ((drop.getChance() * droprate * rateOfMapId * rateOfItem) < randomChance);
            if ((droprate == 0) || noadd) {
                continue;
            }
            // 指定的物件提高掉落數量倍率
            final double amount = DropItemTable.get().getDropAmount(itemId);
            final long min = (long) (drop.getMin() * amount);
            final long max = (long) (drop.getMax() * amount);
            long itemCount = min;
            final long addCount = max - min + 1;
            if (addCount > 1) {
                itemCount += _random.nextInt((int) addCount);
            }
            // 物件為金幣 加入倍率
            if (itemId == L1ItemId.ADENA) {
                itemCount *= adenarate;
            }
            // 數量為0
            if (itemCount < 0) {
                itemCount = 0;
            }
            // 限制持有數量
            if (itemCount > 2000000000) {
                itemCount = 2000000000;
            }
            if (itemCount > 0) {
                // System.out.println("add:"+npc.getName() + " droprate:" +
                // droprate +" itemId:"+itemId + " itemCount:"+itemCount);
                additem(npc, inventory, itemId, itemCount);
            } else {
                _log.error("NPC加入背包物件數量為0(" + npc.getNpcId() + " itemId: " + itemId + ") 指定地圖");
            }
        }
    }

    /**
     * 全怪物掉落
     *
     * @param npc
     * @param inventory
     * @param dropList
     */
    private void setDrop(final L1NpcInstance npc, final L1Inventory inventory, final Map<Integer, L1DropMob> moblist) {
        for (Integer key : moblist.keySet()) {
            // 取回隨機機率
            final int random = _random.nextInt(0xf4240) + 1;
            if (moblist.get(key).getChance() < random) {
                continue;
            }
            int min = moblist.get(key).getMin();
            final int max = moblist.get(key).getMax();
            final int addCount = max - min + 1;
            if (addCount > 1) {
                min += _random.nextInt(addCount);
            }
            if (min > 0) {
                additem(npc, inventory, key, min);
            } else {
                _log.error("NPC加入背包物件數量為0(" + npc.getNpcId() + " itemId: " + key + ") 全怪掉落");
            }
        }
    }

    /**
     * 對指定背包加入物件
     *
     * @param npc
     * @param inventory 怪物背包
     * @param itemId
     * @param itemCount
     */
    private void additem(final L1NpcInstance npc, L1Inventory inventory, int itemId, long itemCount) {
        try {
            final L1Item tmp = ItemTable.get().getTemplate(itemId);
            if (tmp == null) {
                _log.error("掉落物品設置錯誤(無這編號物品): " + itemId);
                return;
            }
            if (tmp.isStackable()) {// 可以堆疊
                // 生成物品
                final L1ItemInstance item = ItemTable.get().createItem(itemId);
                if (item != null) {
                    item.setCount(itemCount);
                    // 取回此NPC掉落道具強化值資料
                    final ArrayList<L1DropEnchant> datalist = DropItemEnchantTable.get().getDatalist(npc.getNpcId());
                    if (datalist != null) {// 具有資料
                        for (L1DropEnchant data : datalist) {
                            if (data.getItemid() == itemId) {// 物品ID相等
                                int level = DropItemEnchantTable.get().getEnchant(data);// 隨機決定強化值
                                item.setEnchantLevel(level);// 設定強化值
                                break;
                            }
                        }
                    }
                    // 加入背包
                    inventory.storeItem(item);
                }
            } else {// 無法堆疊
                for (int i = 0; i < itemCount; i++) {
                    // 生成物品
                    final L1ItemInstance item = ItemTable.get().createItem(itemId);
                    if (item != null) {
                        item.setCount(1);
                        // 取回此NPC掉落道具強化值資料
                        final ArrayList<L1DropEnchant> datalist = DropItemEnchantTable.get().getDatalist(npc.getNpcId());
                        if (datalist != null) {// 具有資料
                            for (L1DropEnchant data : datalist) {
                                if (data.getItemid() == itemId) {// 物品ID相等
                                    int level = DropItemEnchantTable.get().getEnchant(data);// 隨機決定強化值
                                    item.setEnchantLevel(level);// 設定強化值
                                    break;
                                }
                            }
                        }
                        // 加入背包
                        inventory.storeItem(item);
                        //if (PowerItemSet.START) {
                        if (PowerItemSet.START && PowerItemSet.PowerItemSetDrop) { // 添加打到裝備隨機給能力開關
                            if (_random.nextBoolean()) {
                                // 古文字誕生
                                switch (item.getItem().getUseType()) {
                                    // case 1:// 武器
                                    case 2:// 盔甲
                                    case 18:// T恤
                                    case 19:// 斗篷
                                    case 20:// 手套
                                    case 21:// 靴
                                    case 22:// 頭盔
                                    case 25:// 盾牌
                                    case 70:// 脛甲
                                        int index = 0;
                                        L1ItemPower_name power = null;
                                        while (index <= 3) {
                                            if (!ItemPowerTable.POWER_NAME.isEmpty()) {
                                                final int key = _random.nextInt(ItemPowerTable.POWER_NAME.size()) + 1;
                                                final L1ItemPower_name v = ItemPowerTable.POWER_NAME.get(key);
                                                if (_random.nextInt(1000) <= v.get_dice()) {
                                                    power = v;
                                                }
                                            }
                                            index++;
                                        }
                                        if (power != null) {
                                            item.set_power_name(power);
                                            CharItemPowerReading.get().storeItem(item.getId(), item.get_power_name());
                                        }
                                        break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
