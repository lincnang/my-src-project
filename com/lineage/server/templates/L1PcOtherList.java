package com.lineage.server.templates;

import com.add.system.L1FireCrystal;
import com.add.system.L1FireSmithCrystalTable;
import com.lineage.DatabaseFactory;
import com.lineage.data.event.GamblingSet;
import com.lineage.data.event.ShopXSet;
import com.lineage.data.event.gambling.GamblingNpc;
import com.lineage.server.datatables.ItemPowerUpdateTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.ShopCnTable;
import com.lineage.server.datatables.ShopTable;
import com.lineage.server.datatables.lock.CharRemoveItemReading;
import com.lineage.server.datatables.lock.DwarfShopReading;
import com.lineage.server.datatables.lock.GamblingReading;
import com.lineage.server.datatables.lock.ServerCnInfoReading;
import com.lineage.server.model.Instance.L1IllusoryInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.timecontroller.event.GamblingTime;
import com.lineage.server.utils.ListMapUtil;
import com.lineage.server.utils.RangeInt;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import william.server_lv;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 人物其他項清單列表
 *
 * @author DaiEn
 */
public class L1PcOtherList {
    private static final Log _log = LogFactory.getLog(L1PcOtherList.class);
    public Map<Integer, L1ItemInstance> DELIST;// 虛擬商店買入清單(ORDERID/指定的物品)
    public Map<Integer, L1Quest> QUESTMAP;// 暫存任務清單
    public Map<Integer, L1ShopS> SHOPXMAP;// 暫存出售紀錄清單
    public ArrayList<Integer> ATKNPC;// 暫存需要攻擊的NPCID
    public Map<Integer, Integer> EZPAYLIST;// 暫存EZPAY商品清單
    public Map<Integer, int[]> SHOPLIST;// 商品領回暫時清單
    private L1PcInstance _pc;
    private Map<Integer, L1ShopItem> _cnList;// 購買奇怪的商人物品清單(ORDERID/指定的物品數據)
    // 自選寶箱專用，避免與商城共用
    private Map<Integer, L1ShopItem> _chooseList; // 顯示用索引 -> L1ShopItem
    private Map<Integer, L1ChooseBoxOption> _chooseData; // 索引 -> 對應選項資料
    private Map<Integer, L1ShopAutoHp> _autoHpList; // 特殊商店 -> 購買自動喝水補魔道具
    private Map<Integer, L1ItemInstance> _removeItemSellList;// 特殊商店 -> 添加刪除物品
    private Map<Integer, L1ItemInstance> _removeItemBuyList;// 特殊商店 -> 取消刪除物品
    private Map<Integer, L1ItemInstance> _cnSList;// 購買托售商人物品清單(ORDERID/指定的物品數據)
    private Map<Integer, GamblingNpc> _gamList;// 購買食人妖精競賽票清單(ORDERID/指定的參賽者數據)
    private Map<Integer, L1Gambling> _gamSellList;// 賣出食人妖精競賽票清單(物品OBJID/妖精競賽紀錄緩存)
    private Map<Integer, L1IllusoryInstance> _illusoryList;// 召喚分身清單(分身OBJID/分身數據)
    private Map<Integer, L1TeleportLoc> _teleport;// NPC傳送點緩存(傳點排序編號/傳點數據)
    private Map<Integer, Integer> _uplevelList;// 屬性重置清單(模式/增加數值總合)
    private Map<Integer, String[]> _shiftingList;// 裝備轉移人物清單(帳戶中人物排序編號/String[]{OBJID/人物名稱})
    private Map<Integer, L1ItemInstance> _sitemList;// 裝備交換清單(ORDERID/指定的物品)
    private Map<Integer, Integer> _sitemList2;// 裝備交換清單(ORDERID/指定的物品ITEMID)
    private int[] _is;// 暫存人物原始素質改變

    public L1PcOtherList(final L1PcInstance pc) {
        this._pc = pc;
        this.DELIST = new HashMap<>();
        this._cnList = new HashMap<>();
        this._chooseList = new HashMap<>();
        this._chooseData = new HashMap<>();
        this._autoHpList = new HashMap<>(); // 特殊商店 -> 購買自動喝水補魔道具
        this._removeItemSellList = new HashMap<>();// 特殊商店 -> 添加刪除物品
        this._removeItemBuyList = new HashMap<>();// 特殊商店 -> 取消刪除物品
        this._cnSList = new HashMap<>();
        this._gamList = new HashMap<>();
        this._gamSellList = new HashMap<>();
        this._illusoryList = new HashMap<>();
        this._teleport = new HashMap<>();
        this._uplevelList = new HashMap<>();
        this._shiftingList = new HashMap<>();
        this._sitemList = new HashMap<>();
        this._sitemList2 = new HashMap<>();
        this.QUESTMAP = new HashMap<>();
        this.SHOPXMAP = new HashMap<>();
        this.ATKNPC = new ArrayList<>();
        this.EZPAYLIST = new HashMap<>();
        this.SHOPLIST = new HashMap<>();
    }

    /**
     * 清空全部資料
     */
    public void clearAll() {
        try {
            ListMapUtil.clear(DELIST);
            ListMapUtil.clear(_cnList);
            ListMapUtil.clear(_autoHpList); // 特殊商店 -> 購買自動喝水補魔道具
            ListMapUtil.clear(_removeItemSellList); // 特殊商店 -> 添加刪除物品
            ListMapUtil.clear(_removeItemBuyList); // 特殊商店 -> 取消刪除物品
            ListMapUtil.clear(_cnSList);
            ListMapUtil.clear(_gamList);
            ListMapUtil.clear(_gamSellList);
            ListMapUtil.clear(_illusoryList);
            ListMapUtil.clear(_chooseList);
            ListMapUtil.clear(_chooseData);
            ListMapUtil.clear(_teleport);
            ListMapUtil.clear(_uplevelList);
            ListMapUtil.clear(_shiftingList);
            ListMapUtil.clear(_sitemList);
            ListMapUtil.clear(_sitemList2);
            ListMapUtil.clear(QUESTMAP);
            ListMapUtil.clear(SHOPXMAP);
            ListMapUtil.clear(ATKNPC);
            ListMapUtil.clear(EZPAYLIST);
            ListMapUtil.clear(SHOPLIST);
            this.DELIST = null;// 虛擬商店買入清單
            this._cnList = null;
            this._autoHpList = null; // 特殊商店 -> 購買自動喝水補魔道具
            this._removeItemSellList = null; // 特殊商店 -> 添加刪除物品
            this._removeItemBuyList = null; // 特殊商店 -> 取消刪除物品
            this._cnSList = null;
            this._chooseList = null;
            this._chooseData = null;
            this._gamList = null;
            this._gamSellList = null;
            this._illusoryList = null;
            this._teleport = null;
            this._uplevelList = null;
            this._shiftingList = null;
            this._sitemList = null;
            this._sitemList2 = null;
            this.QUESTMAP = null;
            this.SHOPXMAP = null;
            this.ATKNPC = null;
            this.EZPAYLIST = null;
            this.SHOPLIST = null;
            this._is = null;
            this._pc = null;
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
    // TODO 裝備交換清單

    /**
     * 傳回裝備交換清單(可換)
     *
     * @return _sitemList2
     */
    public Map<Integer, Integer> get_sitemList2() {
        return this._sitemList2;
    }

    /**
     * 加入裝備交換清單(可換)
     *
     */
    public void add_sitemList2(final Integer key, final Integer value) {
        this._sitemList2.put(key, value);
    }

    /**
     * 清空裝備交換清單(可換)
     */
    public void clear_sitemList2() {
        this._sitemList2.clear();
    }
    // TODO 裝備交換清單

    /**
     * 傳回裝備交換清單(準備)
     *
     * @return _sitemList
     */
    public Map<Integer, L1ItemInstance> get_sitemList() {
        return this._sitemList;
    }

    /**
     * 加入裝備交換清單(準備)
     *
     */
    public void add_sitemList(final Integer key, final L1ItemInstance value) {
        this._sitemList.put(key, value);
    }

    /**
     * 清空裝備交換清單(準備)
     */
    public void clear_sitemList() {
        this._sitemList.clear();
    }
    // TODO 帳戶人物清單

    /**
     * 傳回帳戶人物清單
     *
     * @return _shiftingList
     */
    public Map<Integer, String[]> get_shiftingList() {
        return this._shiftingList;
    }

    /**
     * 加入帳戶人物清單
     *
     */
    public void add_shiftingList(final Integer key, final String[] value) {
        this._shiftingList.put(key, value);
    }

    /**
     * 移出帳戶人物清單
     *
     */
    public void remove_shiftingList(final Integer key) {
        this._shiftingList.remove(key);
    }

    /**
     * 讀取人物列表<BR>
     * 將資料置入MAP中
     */
    public void set_shiftingList() {
        try {
            _shiftingList.clear();
            Connection conn = null;
            PreparedStatement pstm = null;
            ResultSet rs = null;
            try {
                conn = DatabaseFactory.get().getConnection();
                pstm = conn.prepareStatement("SELECT * FROM `characters` WHERE `account_name`=?");
                pstm.setString(1, this._pc.getAccountName());
                rs = pstm.executeQuery();
                int key = 0;
                while (rs.next()) {
                    final int objid = rs.getInt("objid");
                    final String name = rs.getString("char_name");
                    if (!name.equalsIgnoreCase(this._pc.getName())) {
                        key++;
                        this.add_shiftingList(key, new String[]{String.valueOf(objid), name});
                    }
                }
            } catch (final Exception e) {
                _log.error(e.getLocalizedMessage(), e);
            } finally {
                SQLUtil.close(rs);
                SQLUtil.close(pstm);
                SQLUtil.close(conn);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
    // TODO

    /**
     * 傳回分身
     *
     * @return _illusoryList
     */
    public Map<Integer, L1IllusoryInstance> get_illusoryList() {
        return this._illusoryList;
    }

    /**
     * 加入分身清單
     *
     */
    public void addIllusoryList(final Integer key, final L1IllusoryInstance value) {
        this._illusoryList.put(key, value);
    }

    /**
     * 移出分身清單
     *
     */
    public void removeIllusoryList(final Integer key) {
        try {
            if (_illusoryList == null) {
                return;
            }
            if (_illusoryList.get(key) != null) {
                this._illusoryList.remove(key);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
    // TODO 傳送

    /**
     * 傳送點緩存
     *
     */
    public void teleport(final HashMap<Integer, L1TeleportLoc> teleportMap) {
        try {
            ListMapUtil.clear(_teleport);
            _teleport.putAll(teleportMap);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 傳送點緩存
     *
     * @return _teleport
     */
    public Map<Integer, L1TeleportLoc> teleportMap() {
        return this._teleport;
    }

    /**
     * 賣出全部物品
     *
     */
    public void sellall(final Map<Integer, Integer> sellallMap) {
        try {
            int getprice = 0;
            int totalprice = 0;
            for (final Integer integer : sellallMap.keySet()) {
                final L1ItemInstance item = this._pc.getInventory().getItem(integer);
                if (item != null) {
                    final int key = item.getItemId();
                    final int price = ShopTable.get().getPrice(key);// 單價
                    final Integer count = sellallMap.get(integer);// 賣出數量
                    if (price >= 200000 && count > 9999) {// 單價大於等於20萬最多販賣9999個
                        // 販賣數量無法超過 %d個。
                        _pc.sendPackets(new S_SystemMessage("販賣數量無法超過 9999個。"));
                        return;
                    }
                    totalprice += (price * count);
                    if (!RangeInt.includes(totalprice, 0, 2000000000)) {
                        _pc.sendPackets(new S_SystemMessage("總共販賣價格無法超過 20億金幣。"));
                        return;
                    }
                    final long remove = this._pc.getInventory().removeItem(integer, count);
                    if (remove == count) {
                        getprice += (price * count);
                    }
                }
            }
            if (getprice > 0) {
                // 物品(金幣)
                final L1ItemInstance item = ItemTable.get().createItem(40308);
                item.setCount(getprice);
                this.createNewItem(item);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 賣出物品取得火神結晶
     */
    public void sellforfirecrystal(final Map<Integer, Integer> FCMap) {
        try {
            int allcount = 0;
            for (final int objid : FCMap.keySet()) {
                final L1ItemInstance item = this._pc.getInventory().getItem(objid);
                if (item != null) {
                    int key = item.getItemId();
                    if (item.getBless() == 0) {// 祝福狀態
                        key = item.getItemId() - 100000;
                    } else if (item.getBless() == 2) {// 詛咒狀態
                        key = item.getItemId() - 200000;
                    }
                    L1FireCrystal firecrystal = L1FireSmithCrystalTable.get().getTemplate(key);
                    final int crystalcount = firecrystal.get_CrystalCount(item);// 火神結晶數量
                    final int count = FCMap.get(objid);// 物品數量
                    final long remove = this._pc.getInventory().removeItem(objid, count);
                    if (remove == count) {
                        allcount += (crystalcount * count);
                    }
                }
            }
            if (allcount > 0) {
                // 物品(火神結晶)
                final L1ItemInstance item = ItemTable.get().createItem(80029);
                item.setCount(allcount);
                this.createNewItem(item);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 玩家賣出物品給予商城道具回收專員
     */
    public void sellcnitem(final Map<Integer, Integer> cnsellMap) {
        try {
            int allprice = 0;
            ArrayList<Integer> uplist = ItemPowerUpdateTable.get().get_updeatitemidlist();// 升級物品列表
            for (final int objid : cnsellMap.keySet()) {
                final L1ItemInstance item = this._pc.getInventory().getItem(objid);
                if (item != null) {
                    final int key = item.getItemId();
                    int price = 0;
                    int type_id = ItemPowerUpdateTable.get().get_original_type(key);
                    int original_itemid = ItemPowerUpdateTable.get().get_original_itemid(type_id);
                    if (uplist.contains(key)) {// 如果在升級物品列表中
                        price = ShopCnTable.get().getPrice(original_itemid);// 取得原始物品價錢
                    } else {
                        price = ShopCnTable.get().getPrice(key);// 取回回收單價
                    }
                    final int count = cnsellMap.get(objid);// 物品數量
                    final long remove = this._pc.getInventory().removeItem(objid, count);
                    if (remove == count) {
                        allprice += (price * count);
                    }
                    toGmMsg(item.getItem(), allprice, false);
                }
            }
            if (allprice > 0) {
                // 物品(商城幣44070)
                final L1ItemInstance item = ItemTable.get().createItem(44070);
                item.setCount(allprice);
                this.createNewItem(item);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
    // TODO 購物清單

    /**
     * 清空全部買入資料
     */
    public void clear() {
        try {
            ListMapUtil.clear(this._cnList);
            ListMapUtil.clear(this._gamList);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
    // 奇巖賭場

    /**
     * 複製賣出資料(清空舊資料)
     *
     */
    public void set_gamSellList(final Map<Integer, L1Gambling> sellList) {
        try {
            ListMapUtil.clear(_gamSellList);
            _gamSellList.putAll(sellList);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 賣出食人妖精競賽票
     *
     */
    public void get_sellGam(final int objid, final int count) {
        try {
            final L1Gambling element = _gamSellList.get(objid);
            if (element == null) {
                return;
            }
            final long countx = (long) (element.get_rate() * GamblingSet.GAMADENA) * count;
            final long remove = this._pc.getInventory().removeItem(objid, count);
            if (remove == count) {
                final int outcount = element.get_outcount() - count;
                if (outcount < 0) {
                    return;
                }
                element.set_outcount(outcount);
                GamblingReading.get().updateGambling(element.get_id(), outcount);
                // 奇巖賭場 下注使用物品編號(預設金幣40308)
                final L1ItemInstance item = ItemTable.get().createItem(GamblingSet.ADENAITEM);
                item.setCount(countx);
                this.createNewItem(item);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 加入購買食人妖精競賽票
     *
     */
    public void add_gamList(final GamblingNpc element, final int index) {
        this._gamList.put(index, element);
    }

    /**
     * 購買食人妖精競賽票
     *
     */
    public void get_buyGam(final Map<Integer, Integer> gamMap) {
        try {
            for (final Integer integer : gamMap.keySet()) {
                final int index = integer;
                final int count = gamMap.get(integer);
                this.get_gamItem(index, count);
            }
            ListMapUtil.clear(this._gamList);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void get_gamItem(final int index, final int count) {
        try {
            if (count <= 0) {
                return;
            }
            final GamblingNpc element = this._gamList.get(index);
            if (element == null) {
                return;
            }
            final int npcid = element.get_npc().getNpcId();// 比賽者NPCID
            final int no = GamblingTime.get_gamblingNo();// 比賽場次編號
            final long adena = (long) GamblingSet.GAMADENA * count;// 需要數量
            final long srcCount = this._pc.getInventory().countItems(GamblingSet.ADENAITEM);// 現有數量
            // 奇巖賭場 下注使用物品編號(預設金幣40308)檢查
            if (srcCount >= adena) {
                // 食人妖精競賽票
                final L1ItemInstance item = ItemTable.get().createItem(40309);
                // 容量重量確認
                if (this._pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
                    // 扣除奇巖賭場 下注使用物品編號(預設金幣40308)
                    this._pc.getInventory().consumeItem(GamblingSet.ADENAITEM, adena);
                    item.setCount(count);
                    item.setraceGamNo(no + "-" + npcid);
                    this.createNewItem(item);
                    element.add_adena(adena);
                } else {
                    // \f1當你負擔過重時不能交易。
                    this._pc.sendPackets(new S_ServerMessage(270));
                }
            } else {
                final L1Item item = ItemTable.get().getTemplate(GamblingSet.ADENAITEM);
                long nc = adena - srcCount;
                // 337：\f1%0不足%s。
                this._pc.sendPackets(new S_ServerMessage(337, item.getNameId() + "(" + nc + ")"));
                // 337：\f1%0不足%s。
                // this._pc.sendPackets(new S_ServerMessage(189));
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
    // 托售管理員

    /**
     * 加入購買托售管理員物品
     */
    public void add_cnSList(final L1ItemInstance shopItem, final int index) {
        this._cnSList.put(index, shopItem);
    }

    /**
     * 買入托售管理員物品
     */
    public void get_buyCnS(final Map<Integer, Integer> cnMap) {
        try {
            //final int itemid_cn = 40308;// 天幣 40308
            for (final Integer integer : cnMap.keySet()) {
                final int count = cnMap.get(integer);
                if (count > 0) {
                    // 取回賣出視窗對應排序編號物品
                    final L1ItemInstance element = this._cnSList.get(integer);
                    final L1ShopS shopS = DwarfShopReading.get().getShopS(element.getId());
                    if (element != null && shopS != null) {
                        if (shopS.get_end() != 0) {// 物品非出售中
                            continue;
                        }
                        if (shopS.get_item() == null) {// 物品設置為空
                            continue;
                        }
                        // 取回天幣數量
                        //final L1ItemInstance itemT = _pc.getInventory().checkItemX(itemid_cn, shopS.get_adena());
                        final L1ItemInstance itemT = _pc.getInventory().checkItemX(ShopXSet.ITEMID, shopS.get_adena());
                        if (itemT == null) {
                            // 337：\f1%0不足%s。 0_o"
                            //_pc.sendPackets(new S_ServerMessage(337, "天幣"));
                            final L1Item shopXcash = ItemTable.get().getTemplate(ShopXSet.ITEMID);
                            _pc.sendPackets(new S_ServerMessage(337, shopXcash.getName()));
                            continue;
                        }
                        shopS.set_end(1);// 設置資訊為售出
                        shopS.set_item(null);
                        shopS.set_buy_objid(_pc.getId());
                        shopS.set_buy_name(_pc.getName());
                        DwarfShopReading.get().updateShopS(shopS);
                        DwarfShopReading.get().deleteItem(element.getId());
                        //this._pc.getInventory().consumeItem(itemid_cn, shopS.get_adena());
                        this._pc.getInventory().consumeItem(ShopXSet.ITEMID, shopS.get_adena());
                        this._pc.getInventory().storeTradeItem(element);
                        this._pc.sendPackets(new S_ServerMessage(403, element.getLogName())); // 獲得0%。
                        // createNewItem(element);
                    }
                }
            }
            ListMapUtil.clear(this._cnList);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
    // 奇怪的商人

    /**
     * 加入購買奇怪的商人物品
     */
    public void add_cnList(final int index, final L1ShopItem shopItem) {
        this._cnList.put(index, shopItem);
    }

    /**
     * 取得臨時商城/清單對應表 (index -> L1ShopItem)
     */
    public Map<Integer, L1ShopItem> get_cnList() {
        return this._cnList;
    }

    // 自選寶箱專用
    public void add_chooseList(final int index, final L1ShopItem shopItem) {
        this._chooseList.put(index, shopItem);
    }

    public Map<Integer, L1ShopItem> get_chooseList() {
        return this._chooseList;
    }

    public void add_chooseData(final int index, final L1ChooseBoxOption option) {
        this._chooseData.put(index, option);
    }

    public Map<Integer, L1ChooseBoxOption> get_chooseData() {
        return this._chooseData;
    }

    public void clear_chooseList() {
        ListMapUtil.clear(this._chooseList);
        ListMapUtil.clear(this._chooseData);
    }

    /**
     * 買入商城物品
     */
    public void get_buyCn(final Map<Integer, Integer> cnMap) {
        try {
            for (final int index : cnMap.keySet()) {
                final int count = cnMap.get(index);
                if (count > 0) {
                    final L1ShopItem element = this._cnList.get(index);
                    if (element != null) {
                        this.get_cnItem(element, count);
                    }
                }
            }
            ListMapUtil.clear(this._cnList);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 取回商城買入的物品
     *
     */
    private void get_cnItem(final L1ShopItem element, final int count) {
        try {
            final int itemid_cn = _pc.get_temp_adena();// 設定的貨幣編號
            final int itemid = element.getItemId();// 物品編號
            final int packcount = element.getPackCount();// 堆疊數量
            int getCount = count;// 給予數量
            if (packcount > 1) {
                getCount = element.getPackCount() * count;// 給予數量
            }
            final int enchantlevel = element.getEnchantLevel();// 強化值
            final int adenaCount = element.getPrice() * count;// 花費
            // 檢查容量重量
            if (_pc.getInventory().checkAddItem(element.getItem(), getCount) != L1Inventory.OK) {
                return;
            }
            // 檢查設定的貨幣數量
            long srcCount = this._pc.getInventory().countItems(itemid_cn);
            if (srcCount >= adenaCount) {
                this._pc.getInventory().consumeItem(itemid_cn, adenaCount);
                // 找回物品
                final L1Item itemtmp = ItemTable.get().getTemplate(itemid);
                toGmMsg(itemtmp, adenaCount, true);
                if (itemtmp.isStackable()) {
                    // 找回物品
                    final L1ItemInstance item = ItemTable.get().createItem(itemid);
                    item.setCount(getCount);
                    item.setEnchantLevel(enchantlevel);
                    item.setIdentified(true);//false true
                    server_lv.forIntensifyArmor(_pc, item);//terry770106
                    this.createNewItem(item);
                } else {
                    for (int i = 0; i < getCount; i++) {
                        // 找回物品
                        final L1ItemInstance item = ItemTable.get().createItem(itemid);
                        item.setEnchantLevel(enchantlevel);
                        item.setIdentified(true);
                        server_lv.forIntensifyArmor(_pc, item);//terry770106
                        this.createNewItem(item);
                    }
                }
            } else {
                long nc = adenaCount - srcCount;
                L1Item item = ItemTable.get().getTemplate(itemid_cn);
                // 337：\f1%0不足%s。
                this._pc.sendPackets(new S_ServerMessage(337, item.getName() + "(" + nc + ")"));
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 特殊商店 -> 購買自動喝水補魔道具
     *
     */
    public void add_autoHpList(final L1ShopAutoHp shopItem, final int index) {
        this._autoHpList.put(index, shopItem);
    }

    /**
     * 特殊商店 -> 購買自動喝水補魔道具
     *
     */
    public void get_buyAutoHpList(final Map<Integer, Integer> autoHpMap) {
        try {
            for (final Integer integer : autoHpMap.keySet()) {
                final int index = integer;
                final int count = autoHpMap.get(integer);
                if (count > 0) {
                    final L1ShopAutoHp element = this._autoHpList.get(index);
                    if (element != null) {
                        this.get_autoHpItem(element, count);
                    }
                }
            }
            ListMapUtil.clear(this._autoHpList);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 特殊商店 -> 購買自動喝水補魔道具
     *
     */
    private void get_autoHpItem(final L1ShopAutoHp element, final int count) {
        try {
            final int itemid = element.get_itemId(); // 物品編號
            if (_pc.getTemporary() >= 1 && _pc.getTemporary() <= 4) {
                switch (_pc.getTemporary()) {
                    case 1:
                        _pc.setTemporary(0);
                        _pc.setAutoItemId1(itemid);
                        _pc.setAutoHp1(true);// 自動補血判定 開
                        final L1Item aotuitem1 = ItemTable.get().getTemplate(_pc.getAutoItemId1());
                        _pc.startSkillSound_autoHP1(aotuitem1.get_delaytime());
                        _pc.sendPackets(new S_SystemMessage("\\aE第一組設置完成"));
                        break;
                    case 2:
                        _pc.setTemporary(0);
                        _pc.setAutoItemId2(itemid);
                        _pc.setAutoHp2(true);// 自動補血判定 開
                        final L1Item aotuitem2 = ItemTable.get().getTemplate(_pc.getAutoItemId2());
                        _pc.startSkillSound_autoHP2(aotuitem2.get_delaytime());
                        _pc.sendPackets(new S_SystemMessage("\\aE第二組設置完成"));
                        break;
                    case 3:
                        _pc.setTemporary(0);
                        _pc.setAutoItemId3(itemid);
                        _pc.setAutoHp3(true);// 自動補血判定 開
                        final L1Item aotuitem3 = ItemTable.get().getTemplate(_pc.getAutoItemId3());
                        _pc.startSkillSound_autoHP3(aotuitem3.get_delaytime());
                        _pc.sendPackets(new S_SystemMessage("\\aE第三組設置完成"));
                        break;
                    case 4:
                        _pc.setTemporary(0);
                        _pc.setAutoItemId4(itemid);
                        _pc.sendPackets(new S_SystemMessage("\\aE設置完成"));
                        break;
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 特殊商店 -> 添加刪除物品
     *
     */
    public void add_removeItemSellList(final L1ItemInstance shopItem, final int index) {
        this._removeItemSellList.put(index, shopItem);
    }

    /**
     * 特殊商店 -> 添加刪除物品
     *
     */
    public void get_buyRemoveItemSellList(final Map<Integer, L1ItemInstance> removeItemSellMap) {
        try {
            for (final Integer integer : removeItemSellMap.keySet()) {
                final int index = integer;
                final L1ItemInstance element = this._removeItemSellList.get(index);
                this.get_removeItemSellItem(element);
            }
            ListMapUtil.clear(this._removeItemSellList);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 特殊商店 -> 添加刪除物品
     *
     */
    private void get_removeItemSellItem(final L1ItemInstance element) {
        try {
            final int itemid = element.getItemId(); // 物品編號
            final String itemName = element.getName(); // 物品名稱
            if (_pc.getTemporary() == 6) {
                _pc.setTemporary(0);
                if (CharRemoveItemReading.get().getUserItems(_pc.getId(), itemid)) {
                    return;
                }
                CharRemoveItemReading.get().insertItem(_pc.getId(), element);
                _pc.sendPackets(new S_SystemMessage("\\aE添加(" + itemName + ")完成"));
                final int size = _pc.getRemoveItemInventory().getSize();
                if (size == 0) {
                    _pc.getRemoveItemInventory().loadItems();//輔助(自動刪物)倉庫存取
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 特殊商店 -> 取消刪除物品
     *
     */
    public void add_removeItemBuyList(final L1ItemInstance item, final int index) {
        this._removeItemBuyList.put(index, item);
    }

    /**
     * 特殊商店 -> 取消刪除物品
     *
     */
    public void get_buyRemoveItemBuyList(final Map<Integer, Integer> removeItemBuyMap) {
        try {
            for (final Integer integer : removeItemBuyMap.keySet()) {
                final int index = integer;
                final int count = removeItemBuyMap.get(integer);
                if (count > 0) {
                    final L1ItemInstance element = this._removeItemBuyList.get(index);
                    if (element != null) {
                        this.get_removeItemBuyItem(element, count);
                    }
                }
            }
            ListMapUtil.clear(this._removeItemBuyList);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 特殊商店 -> 取消刪除物品
     *
     */
    private void get_removeItemBuyItem(final L1ItemInstance element, final int count) {
        try {
            //final int itemid = element.getItemId(); // 物品編號
            final String itemName = element.getName(); // 物品名稱
            if (_pc.getTemporary() == 7) {
                _pc.setTemporary(0);
                CharRemoveItemReading.get().deleteItem(_pc.getId(), element);
                _pc.sendPackets(new S_SystemMessage("\\aE取消(" + itemName + ")完成"));
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
    // 後處理

    /**
     * 通知GM
     *
     * @param mode       true:買入 false:賣出
     */
    private void toGmMsg(final L1Item itemtmp, final int adenaCount, boolean mode) {
        try {
            ServerCnInfoReading.get().create(this._pc, itemtmp, adenaCount, mode);
            final Collection<L1PcInstance> allPc = World.get().getAllPlayers();
            for (L1PcInstance tgpc : allPc) {
                if (tgpc.isGm()) {
                    final StringBuilder topc = new StringBuilder();
                    if (mode) {// 買入
                        topc.append("人物:").append(this._pc.getName()).append(" 買入:").append(itemtmp.getName()).append(" 花費商城幣:").append(adenaCount);
                        tgpc.sendPackets(new S_ServerMessage(166, topc.toString()));
                    } else {// 賣出
                        topc.append("人物:").append(this._pc.getName()).append(" 賣出:").append(itemtmp.getName()).append(" 獲得商城幣:").append(adenaCount);
                        tgpc.sendPackets(new S_ServerMessage(166, topc.toString()));
                    }
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 給予物件的處理
     *
     */
    private void createNewItem(final L1ItemInstance item) {
        try {
            server_lv.forIntensifyArmor(this._pc, item);////src056
            this._pc.getInventory().storeItem(item);
            this._pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // 獲得0%。
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
    // TODO 屬性重置處理

    /**
     * 屬性重置
     *
     * @param key   模式<BR>
     *              0 升級點數/萬能藥點數 可分配數量<BR>
     *              <p>
     *              1 力量 (原始)<BR>
     *              2 敏捷 (原始)<BR>
     *              3 體質 (原始)<BR>
     *              4 精神 (原始)<BR>
     *              5 智力 (原始)<BR>
     *              6 魅力 (原始)<BR>
     *              <p>
     *              7 力量 +-<BR>
     *              8 敏捷 +-<BR>
     *              9 體質 +-<BR>
     *              10 精神 +-<BR>
     *              11 智力 +-<BR>
     *              12 魅力 +-<BR>
     *              <p>
     *              13 目前分配點數模式 0:升級點數 1:萬能藥點數<BR>
     * @param value 增加數值總合
     */
    public void add_levelList(final int key, final int value) {
        _uplevelList.put(key, value);
    }

    /**
     * 屬性重置清單
     *
     */
    public Map<Integer, Integer> get_uplevelList() {
        return this._uplevelList;
    }

    /**
     * 指定數值參數
     *
     */
    public Integer get_uplevelList(int key) {
        return this._uplevelList.get(key);
    }

    /**
     * 清空屬性重置處理清單
     */
    public void clear_uplevelList() {
        ListMapUtil.clear(this._uplevelList);
    }

    /**
     * 傳回暫存人物原始素質改變
     *
     */
    public int[] get_newPcOriginal() {
        return this._is;
    }

    /**
     * 暫存人物原始素質改變
     *
     */
    public void set_newPcOriginal(final int[] is) {
        this._is = is;
    }
}
