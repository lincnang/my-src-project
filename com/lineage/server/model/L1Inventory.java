package com.lineage.server.model;

import com.add.L1Config;
import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigRate;
import com.lineage.data.event.ProtectorSet;
import com.lineage.data.item_etcitem.extra.Cards;
import com.lineage.data.item_etcitem.extra.Dolls;
import com.lineage.data.item_etcitem.extra.Holys;
import com.lineage.server.IdFactory;
import com.lineage.server.datatables.InnKeyTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.ItemTimeTable;
import com.lineage.server.datatables.lock.CharItemsTimeReading;
import com.lineage.server.datatables.lock.FurnitureSpawnReading;
import com.lineage.server.datatables.sql.LetterTable;
import com.lineage.server.model.Instance.L1FurnitureInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ItemTime;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldNpc;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 背包
 *
 * @author dexc
 */
public class L1Inventory extends L1Object {
    public static final int MAX_WEIGHT = 1500;
    public static final int OK = 0;
    public static final int SIZE_OVER = 1;
    public static final int WEIGHT_OVER = 2;
    public static final int AMOUNT_OVER = 3;
    public static final int WAREHOUSE_OVER = 4;
    public static final int WAREHOUSE_TYPE_PERSONAL = 0;
    public static final int WAREHOUSE_TYPE_CLAN = 1;
    private static final Log _log = LogFactory.getLog(L1Inventory.class);
    private static final long serialVersionUID = 1L;
    public int[] slot_ring = new int[4];
    public int[] slot_earring = new int[2];
    protected List<L1ItemInstance> _items = new CopyOnWriteArrayList<L1ItemInstance>();

    public L1Inventory() {
    }

    /**
     * 背包內全部數量
     *
     * @return
     */
    public int getSize() {
        if (_items.isEmpty()) {
            return 0;
        }
        return _items.size();
    }

    /**
     * 背包內全物件清單
     *
     * @return
     */
    public List<L1ItemInstance> getItems() {
        return _items;
    }

    /**
     * 背包內全部重量
     *
     * @return
     */
    public int getWeight() {
        int weight = 0;
        for (L1ItemInstance item : _items) {
            weight += item.getWeight();
        }
        return weight;
    }

    /**
     * 增加物品是否成功(背包)
     *
     * @param item  物品
     * @param count 數量
     * @return 0:成功 1:超過可攜帶數量 2:超過可攜帶重量 3:超過LONG最大質
     */
    public int checkAddItem(final L1ItemInstance item, final long count) {
        if (item == null) {
            return -1;
        }
        if (item.getCount() <= 0 || count <= 0) {
            return -1;
        }
        if (this.getSize() > ConfigAlt.MAX_NPC_ITEM || this.getSize() == ConfigAlt.MAX_NPC_ITEM && (!item.isStackable() || !this.checkItem(item.getItem().getItemId()))) { // 容量確認
            return SIZE_OVER;
        }
        final long weight = this.getWeight() + item.getItem().getWeight() * count / 1000 + 1;
        if (weight < 0 || item.getItem().getWeight() * count / 1000 < 0) {
            return WEIGHT_OVER;
        }
        if (weight > MAX_WEIGHT * ConfigRate.RATE_WEIGHT_LIMIT_PET) { // 重量確認
            return WEIGHT_OVER;
        }
        final L1ItemInstance itemExist = this.findItemId(item.getItemId());
        if (itemExist != null && itemExist.getCount() + count > Long.MAX_VALUE) {
            return AMOUNT_OVER;
        }
        return OK;
    }

    /**
     * 增加物品是否成功(倉庫)
     *
     * @param item  物品
     * @param count 數量
     * @param type  模式 0:個人/角色專屬/精靈倉庫 1:血盟倉庫
     * @return 0:成功 1:超過數量
     */
    public int checkAddItemToWarehouse(final L1ItemInstance item, final long count, final int type) {
        if (item == null) {
            return -1;
        }
        if (item.getCount() <= 0 || count <= 0) {
            return -1;
        }
        // 不可存倉
        if (!item.getItem().isWarehouseable()) {
            return WAREHOUSE_OVER;
        }
        int maxSize = 100;
        if (type == WAREHOUSE_TYPE_PERSONAL) {
            maxSize = ConfigAlt.MAX_PERSONAL_WAREHOUSE_ITEM;
        } else if (type == WAREHOUSE_TYPE_CLAN) {
            maxSize = ConfigAlt.MAX_CLAN_WAREHOUSE_ITEM;
        }
        if (this.getSize() > maxSize || this.getSize() == maxSize && (!item.isStackable() || !this.checkItem(item.getItem().getItemId()))) { // 容量確認
            return SIZE_OVER;
        }
        return OK;
    }

    public synchronized L1ItemInstance storeItem(int id, int count, int enchant) {
        if (count <= 0) {
            return null;
        }
        L1Item temp = ItemTable.get().getTemplate(id);
        if (temp == null) {
            return null;
        }
        if (temp.isStackable()) {
            L1ItemInstance item = new L1ItemInstance(temp, count);
            if (id == 600248) {
                item.setIdentified(true);
            }
            if (findItemId(id) == null) {
                item.setId(IdFactory.get().nextId());
                World.get().storeObject(item);
            }
            return storeItem(item);
        }
        L1ItemInstance result = null;
        L1ItemInstance item = null;
        for (int i = 0; i < count; i++) {
            item = new L1ItemInstance(temp, 1);
            item.setId(IdFactory.get().nextId());
            item.setEnchantLevel(enchant);
            item.setIdentified(true);
            World.get().storeObject(item);
            storeItem(item);
            result = item;
        }
        return result;
    }

    /**
     * 全新物件加入背包
     *
     * @param itemid
     * @param count
     * @return
     */
    public synchronized L1ItemInstance storeItem(final int itemid, final long count) {
        try {
            if (count <= 0) {
                return null;
            }
            final L1Item temp = ItemTable.get().getTemplate(itemid);
            if (temp == null) {
                return null;
            }
            if (temp.isStackable()) {// 可以堆疊的物品
                final L1ItemInstance item = new L1ItemInstance(temp, count);
                if (this.findItemId(itemid) == null) { // 新しく生成する必要がある場合のみIDの發行とL1Worldへの登錄を行う
                    item.setId(IdFactory.get().nextId());
                    World.get().storeObject(item);
                }
                return this.storeItem(item);
            }
            // 無法堆疊的物品
            L1ItemInstance result = null;
            for (int i = 0; i < count; i++) {
                final L1ItemInstance item = new L1ItemInstance(temp, 1);
                item.setId(IdFactory.get().nextId());
                World.get().storeObject(item);
                this.storeItem(item);
                result = item;
            }
            // 最後に作ったアイテムを返す。配列を戾すようにメソッド定義を變更したほうが良いかもしれない。
            return result;
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * 背包中新物品的增加(物品已加入世界) (道具製造/寶箱開出/交易物品/商店購買)
     *
     * @param item
     * @return
     */
    public synchronized L1ItemInstance storeItem(L1ItemInstance item) {
        try {
            if (item == null) {
                return null;
            }
            if (item.getCount() <= 0L) {
                return null;
            }
            int itemId = item.getItem().getItemId();
            if (item.isStackable()) {// 可堆疊的物品
                if (itemId == 40309) {// 賭狗場彩票
                    L1ItemInstance[] items = findItemsId(itemId);
                    for (L1ItemInstance tgitem : items) {
                        String racegamNo = tgitem.getraceGamNo();
                        if (item.getraceGamNo().equals(racegamNo)) {
                            tgitem.setCount(tgitem.getCount() + item.getCount());
                            updateItem(tgitem);
                            return tgitem;
                        }
                    }
                }
                /** [原碼] 怪物對戰系統 */
                if (itemId == L1Config._2155) {
                    L1ItemInstance[] gamItems = findItemsId(itemId);
                    for (L1ItemInstance tgitem : gamItems) {
                        int GamNpcId = tgitem.getGamNpcId();// 怪物編號
                        int GamNo = tgitem.getGamNo();// 場次編號
                        if (item.getGamNpcId() == GamNpcId && item.getGamNo() == GamNo) {// 兩樣都相同
                            tgitem.setCount(tgitem.getCount() + item.getCount());
                            updateItem(tgitem);
                            return tgitem;
                        }
                    }
                }
                /** [原碼] 大樂透系統 */
                if (itemId == L1Config._2170) {
                    L1ItemInstance[] gamItems = findItemsId(itemId);
                    for (L1ItemInstance tgitem : gamItems) {
                        String StartNpcId = tgitem.getStarNpcId();// 開獎彩號
                        int GamNo = tgitem.getGamNo();// 場次編號
                        if (item.getStarNpcId() == StartNpcId && item.getGamNo() == GamNo) {// 兩樣都相同
                            tgitem.setCount(tgitem.getCount() + item.getCount());
                            updateItem(tgitem);
                            return tgitem;
                        }
                    }
                }
                if (itemId == 40312) {// 旅館鑰匙
                    L1ItemInstance findItem = findKeyId(item.getKeyId());
                    if (findItem != null) {
                        findItem.setCount(findItem.getCount() + item.getCount());
                        updateItem(findItem);
                        return findItem;
                    }
                }
                if (itemId == 82503) {// 訓練所鑰匙
                    L1ItemInstance findItem = findKeyId(item.getKeyId());
                    if (findItem != null) {
                        findItem.setCount(findItem.getCount() + item.getCount());
                        updateItem(findItem);
                        return findItem;
                    }
                }
                // if (itemId == 82504) {// 龍門憑證
                // L1ItemInstance findItem = findKeyId(item.getKeyId());
                // if (findItem != null) {
                // findItem.setCount(findItem.getCount() + item.getCount());
                // updateItem(findItem);
                // return findItem;
                // }
                // }
                boolean SKIP = false;
                switch (itemId) { // 略過的物品ID
                    case 40312:// 旅館鑰匙
                    case 82503:// 訓練所鑰匙
                        // case 82504:// 龍門憑證
                    case 40309:// 賽狗場競賽票
                    case 50138:// 怪物對戰競賽票
                    case 50139:// 大樂透彩票
                        SKIP = true;
                        break;
                }
                L1ItemInstance findItem = findItemId(itemId);// 尋找相同ITEMID的物品
                if (findItem != null && !SKIP) {// 背包中已有相同ITEMID的物品
                    final int OneHundredMillion = 100000000;
                    final int Limit = 1000000000;
                    long TotalCount = findItem.getCount() + item.getCount();
                    if (findItem.getItemId() == 40308) {// 金幣
                        if (TotalCount > Limit) {
                            long count = TotalCount / OneHundredMillion;
                            if (this instanceof L1PcInventory) {// 只有對玩家有效
                                for (int i = 0; i < count; i++) {
                                    TotalCount -= OneHundredMillion;
                                }
                                storeItem(50308, count);// 換成支票一張(支票道具編號 , 數量)
                                L1PcInventory pc_inv = (L1PcInventory) this;
                                L1PcInstance pc = pc_inv.getOwner();
                                pc.sendPackets(new S_SystemMessage("身上金幣超過十億，自動轉換成天票。"));
                            }
                        }
                    }
                    findItem.setCount(TotalCount);
                    updateItem(findItem);
                    return findItem;
                }
            }
            if (item.getItem().getType2() == 0 && item.getItem().getType() == 17) {
                L1PcInventory pc_inv = (L1PcInventory) this;
                L1PcInstance pc = pc_inv.getOwner();
                Dolls.getInstance().set_set(item.getItem().getclassname().split(" "));
                if (!Dolls.getInstance().checkQuest(pc)) {
                    Dolls.getInstance().execute(null, pc, item);
                    if (Dolls.getInstance()._isUseOK) {
                        return null;
                    }
                }
            }
            if (item.getItem().getType2() == 0 && item.getItem().getType() == 18) {
                L1PcInventory pc_inv = (L1PcInventory) this;
                L1PcInstance pc = pc_inv.getOwner();
                Cards.getInstance().set_set(item.getItem().getclassname().split(" "));
                if (!Cards.getInstance().checkQuest(pc)) {
                    Cards.getInstance().execute(null, pc, item);
                    if (Cards.getInstance()._isUseOK) {
                        return null;
                    }
                }
            }
            if (item.getItem().getType2() == 0 && item.getItem().getType() == 19) {
                L1PcInventory pc_inv = (L1PcInventory) this;
                L1PcInstance pc = pc_inv.getOwner();
                Holys.getInstance().set_set(item.getItem().getclassname().split(" "));
                if (!Holys.getInstance().checkQuest(pc)) {
                    Holys.getInstance().execute(null, pc, item);
                    if (Holys.getInstance()._isUseOK) {
                        return null;
                    }
                }
            }
            item.setX(getX());
            item.setY(getY());
            item.setMap(getMapId());
            int maxchargeCount = item.getItem().getMaxChargeCount();
            switch (itemId) {
                case 20383:
                    maxchargeCount = 50;
                    break;
                case 40006:
                case 40007:
                case 40008:
                case 40009:
                case 140006:
                case 140008:
                    Random random1 = new Random();
                    maxchargeCount -= random1.nextInt(5);
                    break;
            }
            if (item.getChargeCount() > 0) {// 已有使用次數資料
                item.setChargeCount(item.getChargeCount());
            } else {
                item.setChargeCount(maxchargeCount);
            }
            if (item.getRemainingTime() > 0) {// 已有使用秒數資料
                item.setRemainingTime(item.getRemainingTime());
            }
            item.setBless(item.getItem().getBless());
            // 守護者系統
            if (itemId == ProtectorSet.ITEM_ID && this instanceof L1PcInventory) {
                L1PcInventory pc_inv = (L1PcInventory) this;
                L1PcInstance pc = pc_inv.getOwner();
                if (pc != null && !pc.isProtector()) {
                    pc.setProtector(true);
                    World.get().broadcastPacketToAll(new S_ServerMessage(2924));
                }
            }
            // 戰神之魂
            if (itemId == 56152 && this instanceof L1PcInventory) {
                L1PcInventory pc_inv = (L1PcInventory) this;
                L1PcInstance pc = pc_inv.getOwner();
                // 身上沒有戰神之魂效果
                if (pc != null && !pc.isMars()) {
                    pc.setMars(true);
                }
            }
            // 妲蒂絲魔石
            if (itemId == 56148 && this instanceof L1PcInventory) {
                L1PcInventory pc_inv = (L1PcInventory) this;
                L1PcInstance pc = pc_inv.getOwner();
                // 身上沒有妲蒂絲魔石效果也沒有真 妲蒂絲魔石效果
                if (pc != null && !pc.isEffectGS() && !pc.isEffectDADIS()) {
                    pc.setGS(true);
                }
            }
            // 真 妲蒂絲魔石
            if (itemId == 56147 && this instanceof L1PcInventory) {
                L1PcInventory pc_inv = (L1PcInventory) this;
                L1PcInstance pc = pc_inv.getOwner();
                // 身上沒有妲蒂絲魔石效果也沒有真 妲蒂絲魔石效果
                if (pc != null && !pc.isEffectGS() && !pc.isEffectDADIS()) {
                    pc.setDADIS(true);
                }
            }
            if (itemId == 40312 && !InnKeyTable.checkey(item)) {// 旅館鑰匙
                item.setIdentified(true);// 設定為已鑒定狀態
                InnKeyTable.StoreKey(item);
            }
            if (itemId == 82503 && !InnKeyTable.checkey(item)) {// 訓練所鑰匙
                item.setIdentified(true);// 設定為已鑒定狀態
                InnKeyTable.StoreKey(item);
            }
            // if ((itemId == 82504) && (!InnKeyTable.checkey(item))) {// 龍門憑證
            // item.setIdentified(true);// 設定為已鑒定狀態
            // InnKeyTable.StoreKey(item);
            // }
            //if (item.getItem().getType() == 14) {
            //item.setIdentified(false);// 設定為未鑒定狀態
            //}
            set_time_item(item);
            _items.add(item);
            insertItem(item);
            return item;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * 背包中新物品的增加 (托售領回/倉庫取出及存入/撿取物品/丟棄物品)
     *
     * @param item
     * @return
     */
    public synchronized L1ItemInstance storeTradeItem(L1ItemInstance item) {
        try {
            if (item == null) {
                return null;
            }
            if (item.getCount() <= 0L) {
                return null;
            }
            int itemId = item.getItem().getItemId();
            if (item.isStackable()) {// 可堆疊的物品
                if (itemId == 40309) {// 賭狗場彩票
                    L1ItemInstance[] items = findItemsId(itemId);
                    for (L1ItemInstance tgitem : items) {
                        String racegamNo = tgitem.getraceGamNo();
                        if (item.getraceGamNo().equals(racegamNo)) {
                            tgitem.setCount(tgitem.getCount() + item.getCount());
                            updateItem(tgitem);
                            return tgitem;
                        }
                    }
                }
                /** [原碼] 怪物對戰系統 */
                if (itemId == L1Config._2155) {
                    L1ItemInstance[] gamItems = findItemsId(itemId);
                    for (L1ItemInstance tgitem : gamItems) {
                        int GamNpcId = tgitem.getGamNpcId();// 怪物編號
                        int GamNo = tgitem.getGamNo();// 場次編號
                        if (item.getGamNpcId() == GamNpcId && item.getGamNo() == GamNo) {// 兩樣都相同
                            tgitem.setCount(tgitem.getCount() + item.getCount());
                            updateItem(tgitem);
                            return tgitem;
                        }
                    }
                }
                /** [原碼] 大樂透系統 */
                if (itemId == L1Config._2170) {
                    L1ItemInstance[] gamItems = findItemsId(itemId);
                    for (L1ItemInstance tgitem : gamItems) {
                        String StartNpcId = tgitem.getStarNpcId();// 開獎彩號
                        int GamNo = tgitem.getGamNo();// 場次編號
                        if (item.getStarNpcId() == StartNpcId && item.getGamNo() == GamNo) {// 兩樣都相同
                            tgitem.setCount(tgitem.getCount() + item.getCount());
                            updateItem(tgitem);
                            return tgitem;
                        }
                    }
                }
                if (itemId == 40312) {// 旅館鑰匙
                    L1ItemInstance findItem = findKeyId(item.getKeyId());
                    if (findItem != null) {
                        findItem.setCount(findItem.getCount() + item.getCount());
                        updateItem(findItem);
                        return findItem;
                    }
                }
                if (itemId == 82503) {// 訓練所鑰匙
                    L1ItemInstance findItem = findKeyId(item.getKeyId());
                    if (findItem != null) {
                        findItem.setCount(findItem.getCount() + item.getCount());
                        updateItem(findItem);
                        return findItem;
                    }
                }
                // if (itemId == 82504) {// 龍門憑證
                // L1ItemInstance findItem = findKeyId(item.getKeyId());
                // if (findItem != null) {
                // findItem.setCount(findItem.getCount() + item.getCount());
                // updateItem(findItem);
                // return findItem;
                // }
                // }
                boolean SKIP = false;
                switch (itemId) { // 略過的物品ID
                    case 40312:// 旅館鑰匙
                    case 82503:// 訓練所鑰匙
                        // case 82504:// 龍門憑證
                    case 40309:// 賽狗場競賽票
                    case 50138:// 怪物對戰競賽票
                    case 50139: // 大樂透彩票
                        SKIP = true;
                        break;
                }
                L1ItemInstance findItem = findItemId(itemId);// 尋找相同ITEMID的物品
                if (findItem != null && !SKIP) {// 背包中已有相同ITEMID的物品
                    final int OneHundredMillion = 100000000;
                    final int Limit = 1000000000;
                    long TotalCount = findItem.getCount() + item.getCount();
                    if (findItem.getItemId() == 40308) {// 金幣
                        if (TotalCount > Limit) {
                            long count = TotalCount / OneHundredMillion;
                            if (this instanceof L1PcInventory) {// 只有對玩家有效
                                for (int i = 0; i < count; i++) {
                                    TotalCount -= OneHundredMillion;
                                }
                                storeItem(50308, count);// 換成支票一張(支票道具編號 , 數量)
                                L1PcInventory pc_inv = (L1PcInventory) this;
                                L1PcInstance pc = pc_inv.getOwner();
                                pc.sendPackets(new S_SystemMessage("身上金幣超過十億，自動轉換成天票。"));
                            }
                        }
                    }
                    findItem.setCount(TotalCount);
                    updateItem(findItem);
                    return findItem;
                }
            }
            item.setX(getX());
            item.setY(getY());
            item.setMap(getMapId());
            // 守護者系統
            if (itemId == ProtectorSet.ITEM_ID && this instanceof L1PcInventory) {
                L1PcInventory pc_inv = (L1PcInventory) this;
                L1PcInstance pc = pc_inv.getOwner();
                if (pc != null && !pc.isProtector()) {
                    pc.setProtector(true);
                    World.get().broadcastPacketToAll(new S_ServerMessage(2924));
                }
            }
            // 戰神之魂
            if (itemId == 56152 && this instanceof L1PcInventory) {
                L1PcInventory pc_inv = (L1PcInventory) this;
                L1PcInstance pc = pc_inv.getOwner();
                // 身上沒有戰神之魂效果
                if (pc != null && !pc.isMars()) {
                    pc.setMars(true);
                }
            }
            // 妲蒂絲魔石
            if (itemId == 56148 && this instanceof L1PcInventory) {
                L1PcInventory pc_inv = (L1PcInventory) this;
                L1PcInstance pc = pc_inv.getOwner();
                // 身上沒有妲蒂絲魔石效果也沒有真 妲蒂絲魔石效果
                if (pc != null && !pc.isEffectGS() && !pc.isEffectDADIS()) {
                    pc.setGS(true);
                }
            }
            // 真 妲蒂絲魔石
            if (itemId == 56147 && this instanceof L1PcInventory) {
                L1PcInventory pc_inv = (L1PcInventory) this;
                L1PcInstance pc = pc_inv.getOwner();
                // 身上沒有妲蒂絲魔石效果也沒有真 妲蒂絲魔石效果
                if (pc != null && !pc.isEffectGS() && !pc.isEffectDADIS()) {
                    pc.setDADIS(true);
                }
            }
            if (itemId == 40312 && !InnKeyTable.checkey(item)) {
                InnKeyTable.StoreKey(item);
            }
            if (itemId == 82503 && !InnKeyTable.checkey(item)) {// 訓練所鑰匙
                InnKeyTable.StoreKey(item);
            }
            // if ((itemId == 82504) && (!InnKeyTable.checkey(item))) {// 龍門憑證
            // InnKeyTable.StoreKey(item);
            // }
            //if (item.getItem().getType() == 14) {
            //item.setIdentified(false);// 設定為未鑒定狀態
            //}
            set_time_item(item);
            _items.add(item);
            insertItem(item);
            return item;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * 刪除指定編號物品
     *
     * @param itemid - 刪除物品的編號
     * @param count  - 刪除的數量
     * @return true:刪除完成 false:刪除失敗
     */
    public boolean consumeItem(final int itemid) {
        // 物品可以堆疊
        if (ItemTable.get().getTemplate(itemid).isStackable()) {
            final L1ItemInstance item = this.findItemId(itemid);
            if (item != null) {
                this.removeItem(item, item.getCount());
                return true;
            }
        } else {
            L1ItemInstance[] items = findItemsId(itemid);
            if (items != null) {
                for (L1ItemInstance item : items) {
                    removeItem(item);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 刪除指定編號物品及數量 (可堆疊、不可堆疊共用)
     *
     * @param itemid 刪除物品的編號
     * @param count  刪除的數量
     * @return true:刪除完成 false:刪除失敗
     */
    public boolean consumeItem(int itemid, long count) {
        if (count <= 0L) {
            return false;
        }
        if (ItemTable.get().getTemplate(itemid).isStackable()) {
            L1ItemInstance item = findItemId(itemid);
            if (item != null && item.getCount() >= count) {
                removeItem(item, count);
                return true;
            }
        } else {
            L1ItemInstance[] itemList = findItemsId(itemid);
            if (itemList.length == count) {
                for (int i = 0; i < count; i++) {
                    removeItem(itemList[i], 1L);
                }
                return true;
            }
            if (itemList.length > count) {
                DataComparator dc = new DataComparator();
                Arrays.sort(itemList, dc);
                for (int i = 0; i < count; i++) {
                    removeItem(itemList[i], 1L);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 刪除指定編號物品及數量 (可堆疊、不可堆疊共用)
     *
     * @param itemid 刪除物品的編號
     * @param count  刪除的數量
     * @return true:刪除完成 false:刪除失敗
     */
    public boolean deleteItem(int objId, long count) {
        L1ItemInstance item = getItem(objId);
        int itemid = item.getItemId();
        if (count <= 0L) {
            return false;
        }
        if (ItemTable.get().getTemplate(itemid).isStackable()) {
            //L1ItemInstance item = findItemId(itemid);
            if (item != null && item.getCount() >= count) {
                removeItem(item, count);
                return true;
            }
        } else {
            L1ItemInstance[] itemList = findItemsId(itemid, objId);
            if (itemList.length == count) {
                for (int i = 0; i < count; i++) {
                    removeItem(itemList[i], 1L);
                }
                return true;
            }
            if (itemList.length > count) {
                DataComparator dc = new DataComparator();
                Arrays.sort(itemList, dc);
                for (int i = 0; i < count; i++) {
                    removeItem(itemList[i], 1L);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 移轉物品
     *
     * @param objectId
     * @param count
     * @return
     */
    public L1ItemInstance shiftingItem(int objectId, long count) {
        L1ItemInstance item = getItem(objectId);
        if (item == null) {
            return null;
        }
        if (item.getCount() <= 0L || count <= 0L) {
            return null;
        }
        if (item.getCount() < count) {
            return null;
        }
        if (item.getCount() == count && !item.isEquipped()) {
            deleteItem(item);
            return item;
        }
        return null;
    }

    /**
     * 指定OBJID以及數量 刪除物品
     *
     * @param objectId
     * @param count
     * @return 實際刪除數量
     */
    public long removeItem(int objectId, long count) {
        L1ItemInstance item = getItem(objectId);
        return removeItem(item, count);
    }

    /**
     * 指定物品(全部數量) 刪除物品
     *
     * @param item
     * @return 實際刪除數量
     */
    public long removeItem(L1ItemInstance item) {
        return removeItem(item, item.getCount());
    }

    /**
     * 指定物品以及數量 刪除物品
     *
     * @param item
     * @param count
     * @return 實際刪除數量
     */
    public long removeItem(L1ItemInstance item, long count) {
        if (item == null) {
            return 0L;
        }
        if (!_items.contains(item)) {
            return 0L;
        }
        if (item.getCount() <= 0L || count <= 0L) {
            return 0L;
        }
        if (item.getCount() < count) {
            count = item.getCount();
        }
        if (item.getCount() == count) {
            int itemId = item.getItem().getItemId();
            if (itemId >= 49016 && itemId <= 49025) {
                LetterTable lettertable = new LetterTable();
                lettertable.deleteLetter(item.getId());
            } else if (itemId >= 41383 && itemId <= 41400) {
                for (L1NpcInstance l1object : WorldNpc.get().all()) {
                    if (l1object instanceof L1FurnitureInstance) {
                        L1FurnitureInstance furniture = (L1FurnitureInstance) l1object;
                        if (furniture.getItemObjId() == item.getId()) {
                            FurnitureSpawnReading.get().deleteFurniture(furniture);
                        }
                    }
                }
            }
            deleteItem(item);
            World.get().removeObject(item);
        } else {
            item.setCount(item.getCount() - count);
            updateItem(item);
        }
        return count;
    }

    /**
     * 物品資料消除
     *
     * @param item
     */
    public void deleteItem(final L1ItemInstance item) {
        int itemid = item.getItemId();
        if (itemid == 40312) {// 旅館鑰匙
            InnKeyTable.DeleteKey(item);// 刪除鑰匙資料
        }
        if (itemid == 82503) {// 訓練所鑰匙
            InnKeyTable.DeleteKey(item);// 刪除鑰匙資料
        }
        // if (itemid == 82504) {// 龍門憑證
        // InnKeyTable.DeleteKey(item);// 刪除鑰匙資料
        // }
        _items.remove(item);
    }

    /**
     * 個人/精靈/角色專屬/血盟倉庫存入
     *
     * @param objectId
     * @param count
     * @param inventory
     * @return
     */
    public synchronized L1ItemInstance tradeItem(int objectId, long count, L1Inventory inventory) {
        L1ItemInstance item = getItem(objectId);
        return tradeItem(item, count, inventory);
    }

    /**
     * 丟棄物品到地面
     *
     * @param item      轉移的物品
     * @param count     移出的數量
     * @param showId    副本編號
     * @param inventory 移出對象的背包
     */
    public synchronized L1ItemInstance tradeItem(L1ItemInstance item, int count, int showId, L1GroundInventory inventory) {
        if (item == null) {
            return null;
        }
        if (item.getCount() <= 0L || count <= 0) {
            return null;
        }
        if (item.isEquipped()) {
            return null;
        }
        if (item.getCount() < count) {
            return null;
        }
        L1ItemInstance carryItem;
        if (item.getCount() == count) {// 全部數量轉移
            this.deleteItem(item);
            carryItem = item;
            carryItem.set_showId(showId);// 副本編號
        } else {// 還有剩餘數量
            item.setCount(item.getCount() - count);// 更新數量
            updateItem(item);
            // 創造新物品並轉移給對方
            carryItem = ItemTable.get().createItem(item.getItem().getItemId());
            carryItem.set_showId(showId);// 副本編號
            carryItem.setCount(count);
            carryItem.setEnchantLevel(item.getEnchantLevel());
            carryItem.setIdentified(item.isIdentified());
            carryItem.set_durability(item.get_durability());
            carryItem.setChargeCount(item.getChargeCount());
            carryItem.setRemainingTime(item.getRemainingTime());
            carryItem.setLastUsed(item.getLastUsed());
            carryItem.setBless(item.getBless());
            if (carryItem.getItem().getItemId() == 40312// 旅館鑰匙
                    || carryItem.getItem().getItemId() == 82503// 訓練所鑰匙
                    || carryItem.getItem().getItemId() == 82504) {// 龍門憑證
                carryItem.setInnNpcId(item.getInnNpcId());
                carryItem.setKeyId(item.getKeyId());
                carryItem.setHall(item.checkRoomOrHall());
                carryItem.setDueTime(item.getDueTime());
            }
        }
        return inventory.storeTradeItem(carryItem);
    }

    /**
     * 衝裝贖回系統
     *
     * @param item
     * @param bless
     */
    public synchronized L1ItemInstance storeItem(L1ItemInstance item, int bless) {
        try {
            if (item == null) {
                return null;
            }
            if (item.getCount() <= 0L) {
                return null;
            }
            if (this._items.contains(item)) {
                return null;
            }
            if (item.isStackable()) {
                if (item.getItem().getUseType() == -5) {
                    L1ItemInstance[] items = findItemsId(item.getItemId());
                    L1ItemInstance[] arrayOfL1ItemInstance1;
                    int j = (arrayOfL1ItemInstance1 = items).length;
                    for (int i = 0; i < j; i++) {
                        L1ItemInstance tgitem = arrayOfL1ItemInstance1[i];
                        String gamNo1 = tgitem.getGamNo1();
                        if (item.getGamNo1().equals(gamNo1)) {
                            tgitem.setCount(tgitem.getCount() + item.getCount());
                            updateItem(tgitem);
                            return tgitem;
                        }
                    }
                } else {
                    L1ItemInstance findItem = findItemId(item.getItem().getItemId());
                    if (findItem != null) {
                        findItem.setCount(findItem.getCount() + item.getCount());
                        updateItem(findItem);
                        return findItem;
                    }
                }
            }
            item.setX(getX());
            item.setY(getY());
            item.setMap(getMapId());
            int chargeCount = item.getItem().getMaxChargeCount();
            switch (item.getItem().getItemId()) {
                case 40006:
                case 40007:
                case 40008:
                case 40009:
                    Random random1 = new Random();
                    chargeCount -= random1.nextInt(5);
                    break;
            }
            item.setChargeCount(chargeCount);
            if (item.getItem().getType2() == 0 && item.getItem().getType() == 2) {
                item.setRemainingTime(item.getItem().getLightFuel());
            } else {
                item.setRemainingTime(item.getItem().getMaxUseTime());
            }
            item.setBless(bless);
            this._items.add(item);
            insertItem(item);
            return item;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * 刪除itemid陣列及數量陣列 (可堆疊、不可堆疊共用)
     *
     * @param itemsid 刪除物品的編號陣列
     * @param counts  刪除的數量陣列
     * @return true:刪除完成 false:刪除失敗
     */
    public boolean consumeItemsId(final int[] itemsid, long[] counts) {
        if (itemsid.length != counts.length) {
            return false;
        }
        int removecount = 0;
        for (int i = 0; i < itemsid.length; i++) {
            if (ItemTable.get().getTemplate(itemsid[i]).isStackable()) {// 可堆疊道具
                L1ItemInstance item = findItemId(itemsid[i]);
                if (item != null && item.getCount() >= counts[i]) {
                    removeItem(item, counts[i]);
                    removecount++;
                }
                if (removecount == itemsid.length) {
                    return true;
                }
            } else {// 不可堆疊道具
                L1ItemInstance[] itemList = findItemsId(itemsid[i]);
                if (itemList.length == counts[i]) {
                    for (int c = 0; c < counts[i]; c++) {
                        removeItem(itemList[c], 1L);
                    }
                    removecount++;
                    if (removecount == itemsid.length) {
                        return true;
                    }
                }
                if (itemList.length > counts[i]) {
                    DataComparator dc = new DataComparator();
                    Arrays.sort(itemList, dc);
                    for (int c = 0; c < counts[i]; c++) {
                        removeItem(itemList[c], 1L);
                    }
                    removecount++;
                    if (removecount == itemsid.length) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 刪除itemid列表所有道具1個 (可堆疊、不可堆疊共用)
     *
     * @param itemsidarray 刪除物品的編號列表
     * @return true:刪除完成 false:刪除失敗
     */
    public boolean consumeItemsIdArray(ArrayList<Integer> itemsidarray) {
        final int[] itemsid = new int[itemsidarray.size()];
        final long[] counts = new long[itemsidarray.size()];
        for (int i = 0; i < itemsidarray.size(); i++) {// 將列表還原為數字陣列
            itemsid[i] = itemsidarray.get(i);
            counts[i] = 1;
        }
        return consumeItemsId(itemsid, counts);
    }

    /**
     * 刪除itemid陣列中所有道具1個 (可堆疊、不可堆疊共用)
     *
     * @param itemsid 刪除物品的編號陣列
     * @return true:刪除完成 false:刪除失敗
     */
    public boolean consumeItemsId(int[] itemsid) {
        final long[] counts = new long[itemsid.length];
        for (int i = 0; i < itemsid.length; i++) {// 將數量陣列全部設為1
            counts[i] = 1;
        }
        return consumeItemsId(itemsid, counts);
    }

    /**
     * 物品轉移 /怪物背包轉移/倉庫取出 /撿取物品
     *
     * @param item      轉移的物品
     * @param count     移出的數量
     * @param inventory 移入對象的背包
     * @return
     */
    public synchronized L1ItemInstance tradeItem(L1ItemInstance item, long count, L1Inventory inventory) {
        if (item == null) {
            return null;
        }
        if (item.getCount() <= 0L || count <= 0L) {
            return null;
        }
        if (item.isEquipped()) {
            return null;
        }
        if (item.getCount() < count) {
            return null;
        }
        L1ItemInstance carryItem;
        if (item.getCount() == count) {// 全部數量轉移
            deleteItem(item);
            carryItem = item;
        } else {// 還有剩餘數量
            item.setCount(item.getCount() - count);
            updateItem(item);
            // 創造新物品並轉移給對方
            carryItem = ItemTable.get().createItem(item.getItem().getItemId());
            carryItem.setCount(count);
            carryItem.setEnchantLevel(item.getEnchantLevel());
            carryItem.setIdentified(item.isIdentified());
            carryItem.set_durability(item.get_durability());
            carryItem.setChargeCount(item.getChargeCount());
            carryItem.setRemainingTime(item.getRemainingTime());
            carryItem.setLastUsed(item.getLastUsed());
            carryItem.setBless(item.getBless());
            if (carryItem.getItem().getItemId() == 40312// 旅館鑰匙
                    || carryItem.getItem().getItemId() == 82503// 訓練所鑰匙
            ) {// 龍門憑證
                carryItem.setInnNpcId(item.getInnNpcId());
                carryItem.setKeyId(item.getKeyId());
                carryItem.setHall(item.checkRoomOrHall());
                carryItem.setDueTime(item.getDueTime());
            }
        }
        return inventory.storeTradeItem(carryItem);
    }

    public L1ItemInstance receiveDamage(int objectId) {
        L1ItemInstance item = getItem(objectId);
        return receiveDamage(item);
    }

    public L1ItemInstance receiveDamage(L1ItemInstance item) {
        return receiveDamage(item, 1);
    }

    public L1ItemInstance receiveDamage(L1ItemInstance item, int count) {
        if (item == null) {
            return null;
        }
        int itemType = item.getItem().getType2();
        int currentDurability = item.get_durability();
        if (currentDurability == 0 && itemType == 0 || currentDurability < 0) {
            item.set_durability(0);
            return null;
        }
        if (itemType == 0) {
            int minDurability = (item.getEnchantLevel() + 5) * -1;
            int durability = currentDurability - count;
            if (durability < minDurability) {
                durability = minDurability;
            }
            if (currentDurability > durability) {
                item.set_durability(durability);
            }
        } else {
            int maxDurability = item.getEnchantLevel() + 5;
            int durability = currentDurability + count;
            if (durability > maxDurability) {
                durability = maxDurability;
            }
            if (currentDurability < durability) {
                item.set_durability(durability);
            }
        }
        updateItem(item, 1);
        return item;
    }

    public L1ItemInstance recoveryDamage(L1ItemInstance item) {
        if (item == null) {
            return null;
        }
        int itemType = item.getItem().getType2();
        int durability = item.get_durability();
        if (durability == 0 && itemType != 0 || durability < 0) {
            item.set_durability(0);
            return null;
        }
        if (itemType == 0) {
            item.set_durability(durability + 1);
        } else {
            item.set_durability(durability - 1);
        }
        updateItem(item, 1);
        return item;
    }

    /**
     * 找尋指定物品(未裝備)
     *
     * @param itemId
     * @return
     */
    public L1ItemInstance findItemIdNoEq(final int itemId) {
        for (final L1ItemInstance item : this._items) {
            if (item.getItem().getItemId() == itemId && !item.isEquipped()) {
                return item;
            }
        }
        return null;
    }

    /**
     * 找尋指定物品 不檢查裝備狀態
     *
     * @param itemId
     * @return
     */
    public L1ItemInstance findItemId(final int itemId) {
        for (final L1ItemInstance item : this._items) {
            if (item.getItem().getItemId() == itemId) {
                return item;
            }
        }
        return null;
    }

    /**
     * 找尋指定物品 不檢查裝備狀態
     *
     * @param nameid
     * @return
     */
    public L1ItemInstance findItemId(final String nameid) {
        for (final L1ItemInstance item : this._items) {
            if (item.getName().equals(nameid)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 找回相同KEYID的物品
     *
     * @param id
     * @return
     */
    public L1ItemInstance findKeyId(int keyid) {
        for (L1ItemInstance item : _items) {
            if (item.getKeyId() == keyid) {
                return item;
            }
        }
        return null;
    }

    /**
     * 傳回該編號物品(陣列) (不可堆疊物品)
     *
     * @param itemId 物品編號
     * @return
     */
    public L1ItemInstance[] findItemsId(final int itemId) {
        final ArrayList<L1ItemInstance> itemList = new ArrayList<L1ItemInstance>();
        for (final L1ItemInstance item : _items) {
            if (item.getItemId() == itemId) {// itemid相等
                itemList.add(item);
            }
        }
        return itemList.toArray(new L1ItemInstance[]{});
    }

    public L1ItemInstance[] findItemsId(final int itemId, int objId) {
        final ArrayList<L1ItemInstance> itemList = new ArrayList<L1ItemInstance>();
        for (final L1ItemInstance item : _items) {
            if (item.getItemId() == itemId && item.getId() == objId) {// itemid相等
                itemList.add(item);
            }
        }
        return itemList.toArray(new L1ItemInstance[]{});
    }

    /**
     * 未裝備物品清單(陣列) (不可堆疊物品)
     *
     * @param itemId
     * @return
     */
    public L1ItemInstance[] findItemsIdNotEquipped(final int itemId) {
        final ArrayList<L1ItemInstance> itemList = new ArrayList<L1ItemInstance>();
        for (final L1ItemInstance item : _items) {
            if (item.getItemId() == itemId) {
                if (!item.isEquipped()) {
                    itemList.add(item);
                }
            }
        }
        return itemList.toArray(new L1ItemInstance[]{});
    }

    /**
     * 未裝備物品清單包含強化值(陣列) (不可堆疊物品)
     *
     * @param itemId
     * @param enchant 檢查強化值
     * @return
     */
    public L1ItemInstance[] findItemsIdNoEqWithEnchant(final int itemId, int enchant) {
        final ArrayList<L1ItemInstance> itemList = new ArrayList<L1ItemInstance>();
        for (final L1ItemInstance item : _items) {
            if (item.getItemId() == itemId && item.getEnchantLevel() == enchant) {
                if (!item.isEquipped()) {
                    itemList.add(item);
                }
            }
        }
        return itemList.toArray(new L1ItemInstance[]{});
    }

    /**
     * 未裝備物品清單(陣列) (不可堆疊物品)
     *
     * @param nameid
     * @return
     */
    public L1ItemInstance[] findItemsIdNotEquipped(final String nameid) {
        final ArrayList<L1ItemInstance> itemList = new ArrayList<L1ItemInstance>();
        for (final L1ItemInstance item : _items) {
            if (item.getName().equals(nameid)) {
                if (!item.isEquipped()) {
                    itemList.add(item);
                }
            }
        }
        return itemList.toArray(new L1ItemInstance[]{});
    }

    /**
     * 傳回指定OBJID物品
     *
     * @param objectId
     * @return
     */
    public L1ItemInstance getItem(final int objectId) {
        for (final Object itemObject : this._items) {
            final L1ItemInstance item = (L1ItemInstance) itemObject;
            if (item.getId() == objectId) {
                return item;
            }
        }
        return null;
    }

    /**
     * 檢查指定物品是否足夠數量1（矢 魔石的確認）
     *
     * @param itemid
     * @return
     */
    public boolean checkItem(final int itemid) {
        return this.checkItem(itemid, 1);
    }

    /**
     * 檢查指定物品是否足夠數量 (可堆疊、不可堆疊通用) (不檢查裝備狀態)
     *
     * @param itemId 物品編號
     * @param count  需要數量
     * @return
     */
    public boolean checkItem(final int itemId, final long count) {
        if (count <= 0) {
            return true;
        }
        // 可堆疊
        if (ItemTable.get().getTemplate(itemId).isStackable()) {
            final L1ItemInstance item = this.findItemId(itemId);
            if (item != null && item.getCount() >= count) {
                return true;
            }
            // 不可堆疊
        } else {
            final Object[] itemList = this.findItemsId(itemId);
            if (itemList.length >= count) {
                return true;
            }
        }
        return false;
    }

    /**
     * 檢查指定物品是否足夠數量(可堆疊物品) (不檢查裝備狀態)
     *
     * @param item  物品
     * @param count 需要數量
     * @return
     */
    public boolean checkItem(final L1ItemInstance item, final long count) {
        if (count <= 0) {
            return true;
        }
        if (item.getCount() >= count) {
            return true;
        }
        return false;
    }

    /**
     * 指定物品編號以及數量(可堆疊物品)<BR>
     * 該物件未在裝備狀態
     *
     * @param itemid
     * @param count
     * @return 足夠傳回物品
     */
    public L1ItemInstance checkItemX(final int itemid, final long count) {
        if (count <= 0) {
            return null;
        }
        if (ItemTable.get().getTemplate(itemid) != null) {
            final L1ItemInstance item = this.findItemIdNoEq(itemid);
            if (item != null && item.getCount() >= count) {
                return item;
            }
        }
        return null;
    }

    /**
     * 是否具有未裝備指定的物品包含強化質 (可堆疊 不可堆疊通用)
     *
     * @param id      指定物件編號
     * @param enchant 指定強化質
     * @param count   數量
     * @return
     */
    public boolean checkEnchantItem(final int itemid, final int enchant, final long count) {
        if (ItemTable.get().getTemplate(itemid).isStackable()) {// 可以堆疊的物品
            L1ItemInstance item = findItemIdNoEq(itemid);
            if (item != null && item.getEnchantLevel() == enchant && item.getCount() >= count) {
                return true;
            }
        } else {// 無法堆疊的物品
            int num = 0;
            for (final L1ItemInstance item : this._items) {
                if (item.isEquipped()) { // 物品裝備狀態
                    continue;
                }
                if (item.getItemId() == itemid && item.getEnchantLevel() == enchant) {
                    num++;
                    if (num == count) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public L1ItemInstance checkItemX_Lv(int itemid, int lv, long count) {
        if (count <= 0L) {
            return null;
        }
        if (ItemTable.get().getTemplate(itemid) != null) {
            L1ItemInstance item = findItemIdNoEq(itemid);
            if (item != null && item.getCount() >= count && item.getEnchantLevel() == lv) {
                return item;
            }
        }
        return null;
    }

    /**
     * 刪除未裝備指定的物品包含強化質 (可堆疊 不可堆疊通用)
     *
     * @param id      指定物件編號
     * @param enchant 指定強化質
     * @param count   數量
     * @return
     */
    public boolean consumeEnchantItem(final int itemid, final int enchant, final long count) {
        if (ItemTable.get().getTemplate(itemid).isStackable()) {// 可以堆疊的物品
            L1ItemInstance item = findItemIdNoEq(itemid);
            if (item != null && item.getEnchantLevel() == enchant && item.getCount() >= count) {
                removeItem(item, count);
                return true;
            }
        } else {// 無法堆疊的物品
            L1ItemInstance[] itemList = findItemsIdNoEqWithEnchant(itemid, enchant);
            if (itemList.length == count) {// 身上物品數量等於指定數量
                for (int i = 0; i < count; i++) {
                    removeItem(itemList[i], 1L);
                }
                return true;
            } else if (itemList.length > count) {// 身上物品數量超過指定數量，則優先刪除強化值最低的物品
                DataComparator dc = new DataComparator();
                Arrays.sort(itemList, dc);
                for (int i = 0; i < count; i++) {
                    removeItem(itemList[i], 1L);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 檢查材料是否足夠
     *
     * @param nameid
     * @param count
     * @return true 材料足夠 ; false 材料不足
     */
    public boolean checkItemNotEquipped(final String nameid, final long count) {
        if (count == 0) {
            return true;
        }
        return count <= this.countItems(nameid);
    }

    /**
     * 檢查材料是否足夠(不可堆疊道具只尋找未裝備道具)
     *
     * @param itemid
     * @param count
     * @return true 材料足夠 ; false 材料不足
     */
    public boolean checkItemNotEquipped(final int itemid, final long count) {
        if (count == 0) {
            return true;
        }
        return count <= this.countItems(itemid);
    }

    // 特定のアイテムを全て必要な個數所持しているか確認（イベントとかで複數のアイテムを所持しているか確認するため）
    public boolean checkItem(final int[] ids) {
        final int len = ids.length;
        final int[] counts = new int[len];
        for (int i = 0; i < len; i++) {
            counts[i] = 1;
        }
        return this.checkItem(ids, counts);
    }

    public boolean checkItem(int[] ids, int[] counts) {
        for (int i = 0; i < ids.length; i++) {
            if (!checkItem(ids[i], counts[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 查找未裝備物品數量 (可堆疊 不可堆疊通用)
     *
     * @param itemId
     * @return 數量
     */
    public long countItems(final int itemId) {
        // 可堆疊
        if (ItemTable.get().getTemplate(itemId).isStackable()) {
            final L1ItemInstance item = this.findItemId(itemId);
            if (item != null) {
                return item.getCount();
            }
            // 不可堆疊
        } else {
            final Object[] itemList = this.findItemsIdNotEquipped(itemId);
            return itemList.length;
        }
        return 0;
    }

    /**
     * 查找未裝備物品數量 (可堆疊 不可堆疊通用)
     *
     * @param nameid
     * @return 數量
     */
    public long countItems(final String nameid) {
        // 可堆疊
        if (ItemTable.get().getTemplate(nameid).isStackable()) {
            final L1ItemInstance item = this.findItemId(nameid);
            if (item != null) {
                return item.getCount();
            }
            // 不可堆疊
        } else {
            final Object[] itemList = this.findItemsIdNotEquipped(nameid);
            return itemList.length;
        }
        return 0;
    }

    public void shuffle() {
        Collections.shuffle(this._items);
    }

    /**
     * 背包內全部物件刪除
     */
    public void clearItems() {
        for (final Object itemObject : this._items) {
            final L1ItemInstance item = (L1ItemInstance) itemObject;
            World.get().removeObject(item);
        }
    }

    public void loadItems() {
    }

    public void loadItemtrades() {
    }

    public void insertItem(L1ItemInstance item) {
    }

    public void updateItem(L1ItemInstance item) {
    }

    public void updateItem(L1ItemInstance item, int colmn) {
    }

    /**
     * 限制道具存在時間
     *
     * @param item
     */
    private void set_time_item(L1ItemInstance item) {
        //		if (item.get_time() == null) {
        //			int date = -1;
        //			if (ItemTimeTable.TIME.get(item.getItemId()) != null) {
        //				date = ItemTimeTable.TIME.get(item.getItemId()).intValue();
        //			}
        //
        //			if (date != -1) {
        //				long time = System.currentTimeMillis();// 目前時間豪秒
        //				long x1 = date * 60 * 60;// 指定小時耗用秒數
        //				long x2 = x1 * 1000;// 轉為豪秒
        //				long upTime = x2 + time;// 目前時間 加上指定天數耗用秒數
        //
        //				// 時間數據
        //				final Timestamp ts = new Timestamp(upTime);
        //				item.set_time(ts);
        //				item.setIdentified(true);
        //				// 人物背包物品使用期限資料
        //				CharItemsTimeReading.get().addTime(item.getId(), ts);
        //				L1PcInventory pc_inv = (L1PcInventory) this;
        //				L1PcInstance pc = pc_inv.getOwner();
        //				pc.sendPackets(new S_ItemName(item));
        //			}
        //		}
        final L1ItemTime itemTime = ItemTimeTable.TIME.get(item.getItemId());
        if (itemTime != null) {
            if (itemTime != null && !itemTime.is_equipped()) {
                final long upTime = System.currentTimeMillis() + itemTime.get_remain_time() * 60 * 1000;
                final Timestamp ts = new Timestamp(upTime);
                item.set_time(ts);
                CharItemsTimeReading.get().addTime(item.getId(), ts);
                if (this instanceof L1PcInventory) {
                    L1PcInventory pc_inv = (L1PcInventory) this;
                    L1PcInstance pc = pc_inv.getOwner();
                    if (pc != null) {
                        _log.info("人物: " + pc.getName() + ", 道具: " + item.getName() + ", 到期日: " + ts);
                    }
                }
            }
        }
    }

    /**
     * [原碼] 怪物對戰系統
     */
    public L1ItemInstance[] findMob() {
        ArrayList<L1ItemInstance> itemList = new ArrayList<L1ItemInstance>();
        for (L1ItemInstance item : this._items) {
            if (item.getItem().getItemId() == L1Config._2155) {
                itemList.add(item);
            }
        }
        return (L1ItemInstance[]) itemList.toArray(new L1ItemInstance[0]);
    }

    /**
     * [原碼] 大樂透系統
     */
    public L1ItemInstance[] findBigHot() {
        ArrayList<L1ItemInstance> itemList = new ArrayList<L1ItemInstance>();
        for (L1ItemInstance item : this._items) {
            if (item.getItem().getItemId() == L1Config._2170) {
                itemList.add(item);
            }
        }
        return (L1ItemInstance[]) itemList.toArray(new L1ItemInstance[0]);
    }

    /**
     * 龍騎士新技能 撕裂護甲
     */
    public L1ItemInstance findEquippedItemId(int id) {
        for (L1ItemInstance item : _items) {
            if (item == null) {
                continue;
            }
            if (item.getItem().getItemId() == id && item.isEquipped()) {
                return item;
            }
        }
        return null;
    }

    /**
     * 按照強化質 由低至高排列物品
     *
     * @author daien
     */
    public class DataComparator implements Comparator<Object> {
        @Override
        public int compare(final Object item1, final Object item2) {
            return ((L1ItemInstance) item1).getEnchantLevel() - ((L1ItemInstance) item2).getEnchantLevel();
        }
    }
}
