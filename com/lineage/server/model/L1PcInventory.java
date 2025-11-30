package com.lineage.server.model;

import com.lineage.config.ConfigRate;
import com.lineage.data.event.ProtectorSet;
import com.lineage.data.item_armor.set.ArmorSet;
import com.lineage.server.clientpackets.C_LoginToServer;
import com.lineage.server.datatables.*;
import com.lineage.server.datatables.lock.CharItemsReading;
import com.lineage.server.datatables.sql.CharItemSublimationTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.*;
import com.lineage.server.serverpackets.ability.S_WeightStatus;
import com.lineage.server.templates.CharItemSublimation;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1Weapon;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import william.L1WilliamLimitedReward;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class L1PcInventory extends L1Inventory {
    public static final int COL_ATTR_ENCHANT_LEVEL = 2048;
    public static final int COL_ATTR_ENCHANT_KIND = 1024;
    public static final int COL_BLESS = 512;
    public static final int COL_REMAINING_TIME = 256;
    public static final int COL_CHARGE_COUNT = 128;
    public static final int COL_ITEMID = 64;
    public static final int COL_DELAY_EFFECT = 32;
    public static final int COL_COUNT = 16;
    public static final int COL_EQUIPPED = 8;
    public static final int COL_ENCHANTLVL = 4;
    public static final int COL_IS_ID = 2;
    public static final int COL_DURABILITY = 1;
    public static final int COL_boos_ENCHANT_LEVEL = 16384;
    public static final int COL_boos_ENCHANT_KIND = 8192;
    private static final Log _log = LogFactory.getLog(L1PcInventory.class);
    private static final long serialVersionUID = 1L;
    private static final int MAX_SIZE = 180;
    private final L1PcInstance _owner;
    private final HashMap<Integer, L1ItemInstance> _RingList = new HashMap<>();
    private final HashMap<Integer, L1ItemInstance> _EarringList = new HashMap<>();
    private final HashMap<Integer, L1ItemInstance> _RuneList = new HashMap<>();
    private int _arrowId;
    private int _stingId;

    public L1PcInventory(L1PcInstance owner) {
        _owner = owner;
        _arrowId = 0;
        _stingId = 0;
    }

    public L1PcInstance getOwner() {
        return _owner;
    }

    public int getWeight240() {
        return calcWeight240(getWeight());
    }

    public int calcWeight240(long weight) {
        int weight240 = 0;
        if (ConfigRate.RATE_WEIGHT_LIMIT != 0.0D) {
            double maxWeight = _owner.getMaxWeight();
            if (weight > maxWeight) {
                weight240 = 240;
            } else {
                double wpTemp = weight * 100L / maxWeight * 240.0D / 100.0D;
                DecimalFormat df = new DecimalFormat("00.##");
                df.format(wpTemp);
                wpTemp = Math.round(wpTemp);
                weight240 = (int) wpTemp;
            }
        } else {
            weight240 = 0;
        }
        return weight240;
    }

    /**
     * 傳回100階段重量 XXX  7.6c add
     *
     */
    public int getWeight100() {
        return calcWeight100(getWeight());
    }

    /**
     * 100階段重量計算  XXX  7.6c add
     *
     */
    public int calcWeight100(int calcWeight) {
        int weight = 0;
        if (ConfigRate.RATE_WEIGHT_LIMIT != 0) {
            // 返回角色最大負重程度
            final int maxWeight = (int) this._owner.getMaxWeight();
            if (calcWeight > maxWeight) {
                weight = 100;
            } else {
                weight = calcWeight * 100 / maxWeight;
            }
        } else { // 如果重量始終為0
            weight = 0;
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
    @Override
    public int checkAddItem(final L1ItemInstance item, final long count) {
        return this.checkAddItem(item, count, true);
    }

    /**
     * 增加物品是否成功(背包)
     *
     * @param item    物品數據
     * @param count   數量
     * @return 0:成功 1:超過可攜帶數量 2:超過可攜帶重量 3:超過LONG最大質
     */
    public int checkAddItem(final L1Item item, final long count) {
        if (item == null) {
            return -1;
        }
        boolean isMaxSize = false;// 容量數據異常
        boolean isWeightOver = false;// 重量數據異常
        // 可以堆疊
        if (item.isStackable()) {
            // 身上不具備該物件
            if (!this.checkItem(item.getItemId())) {
                // 超過可攜帶數量
                if (this.getSize() + 1 >= MAX_SIZE) {
                    isMaxSize = true;
                }
            }
            // 不可以堆疊
        } else {
            // 超過可攜帶數量
            if (this.getSize() + 1 >= MAX_SIZE) {
                isMaxSize = true;
            }
        }
        if (isMaxSize) {
            // 263 \f1一個角色最多可攜帶180個道具。
            this.sendOverMessage(263);
            return SIZE_OVER;
        }
        // 現有重量 + (物品重量 * 數量 / 1000) + 1
        final long weight = this.getWeight() + item.getWeight() * count / 1000 + 1;
        // 重量數據異常 (重量計算表示小於0)
        if (weight < 0 || item.getWeight() * count / 1000 < 0) {
            isWeightOver = true;
        }
        // 超過可攜帶重量
        if (this.calcWeight240(weight) >= 240 && !isWeightOver) {
            isWeightOver = true;
        }
        if (isWeightOver) {
            // 82 此物品太重了，所以你無法攜帶。
            this.sendOverMessage(82);
            return WEIGHT_OVER;
        }
        return OK;
    }

    /**
     * 增加物品是否成功(背包)
     *
     * @param item    物品(物品已加入世界)
     * @param count   數量
     * @param message 發送訊息
     * @return 0:成功 1:超過可攜帶數量 2:超過可攜帶重量 3:超過LONG最大質
     */
    public int checkAddItem(final L1ItemInstance item, final long count, final boolean message) {
        if (item == null) {
            return -1;
        }
        if (count <= 0) {
            return -1;
        }
        boolean isMaxSize = false;// 容量數據異常
        boolean isWeightOver = false;// 重量數據異常
        // 可以堆疊
        if (item.isStackable()) {
            // 身上不具備該物件
            if (!this.checkItem(item.getItem().getItemId())) {
                // 超過可攜帶數量
                if (this.getSize() + 1 >= MAX_SIZE) {
                    isMaxSize = true;
                }
            }
            // 不可以堆疊
        } else {
            // 超過可攜帶數量
            if (this.getSize() + 1 >= MAX_SIZE) {
                isMaxSize = true;
            }
        }
        if (isMaxSize) {
            if (message) {
                // 263 \f1一個角色最多可攜帶180個道具。
                this.sendOverMessage(263);
            }
            return SIZE_OVER;
        }
        // 現有重量 + (物品重量 * 數量 / 1000) + 1
        final long weight = this.getWeight() + item.getItem().getWeight() * count / 1000 + 1;
        // 重量數據異常 (重量計算表示小於0)
        if (weight < 0 || item.getItem().getWeight() * count / 1000 < 0) {
            isWeightOver = true;
        }
        // 超過可攜帶重量
        if (this.calcWeight240(weight) >= 240 && !isWeightOver) {
            isWeightOver = true;
        }
        if (isWeightOver) {
            if (message) {
                // 82 此物品太重了，所以你無法攜帶。
                this.sendOverMessage(82);
            }
            return WEIGHT_OVER;
        }
        return OK;
    }

    public void sendOverMessage(int message_id) {
        _owner.sendPackets(new S_ServerMessage(message_id));
    }

    /**
     * 初始化人物背包資料
     */
    @Override
    public void loadItems() {
        try {
            final CopyOnWriteArrayList<L1ItemInstance> items = CharItemsReading.get().loadItems(this._owner.getId());
            if (items != null) {
                _items = items;
                List<L1ItemInstance> equipped = new CopyOnWriteArrayList<>();
                for (final L1ItemInstance item : items) {
                    int itemId = item.getItemId();

                    // ✅ 新增：讀取並套用昇華能力資料（若有）
                    CharItemSublimation sub = CharItemSublimationTable.get().loadItem(item.getId());
                    if (sub != null) {
                        item.setSublimation(sub);
                    }

                    if (item.isEquipped()) {
                        if (ItemVIPTable.get().checkVIP(itemId)) {
                            ItemVIPTable.get().addItemVIP(this._owner, itemId);
                        }
                        equipped.add(item);
                    }
                }

                // 若你日後要補裝備設為裝備狀態也可解開這段
                // for (final L1ItemInstance item : equipped) {
                //     this.setEquipped(item, true, true, false);
                // }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }


    /**
     * LIST物品資料新增
     */
    @Override
    public void insertItem(final L1ItemInstance item) {
        if (item.getCount() <= 0) {
            return;
        }
        // 設置使用者OBJID
        item.set_char_objid(this._owner.getId());
        this._owner.sendPackets(new S_AddItem(item));
        if (item.getItem().getWeight() != 0) {
            // 重量
            //this._owner.sendPackets(new S_PacketBox(S_PacketBox.WEIGHT, this.getWeight240()));
            // XXX 7.6 重量程度資訊
            _owner.sendPackets(new S_WeightStatus(_owner.getInventory().getWeight() * 100 / (int) _owner.getMaxWeight(), _owner.getInventory().getWeight(), (int) _owner.getMaxWeight()));
        }
        if (item.getItemId() == 44070) {
            _owner.updateGameMallMoney();
        }
        if (CheckItemPowerTable.get().checkItem(item.getItemId())) { // 身上持有道具給予能力系統
            CheckItemPowerTable.get().givepower(_owner, item.getItemId());
        }
        try {
            CharItemsReading.get().storeItem(this._owner.getId(), item);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 更新物品數量
     */
    @Override
    public void updateItem(final L1ItemInstance item) {
        this.updateItem(item, COL_COUNT);
        if (item.getItem().isToBeSavedAtOnce()) {
            this.saveItem(item, COL_COUNT);
        }
    }

    /**
     * 背包內物件狀態更新
     *
     * @param item   需要更新的物件
     * @param column 更新種類
     */
    @Override
    public void updateItem(final L1ItemInstance item, int column) {
        if (column >= COL_ATTR_ENCHANT_LEVEL) { // 屬性強化數
            this._owner.sendPackets(new S_ItemStatus(item));
            column -= COL_ATTR_ENCHANT_LEVEL;
        }
        if (column >= COL_ATTR_ENCHANT_KIND) { // 屬性強化種類
            this._owner.sendPackets(new S_ItemStatus(item));
            column -= COL_ATTR_ENCHANT_KIND;
        }
        if (column >= COL_BLESS) { // 祝福?封印
            this._owner.sendPackets(new S_ItemColor(item));
            column -= COL_BLESS;
        }
        if (column >= COL_REMAINING_TIME) { // 殘餘可用時間
            this._owner.sendPackets(new S_ItemName(item));
            column -= COL_REMAINING_TIME;
        }
        if (column >= COL_CHARGE_COUNT) { // 殘餘可用次數
            this._owner.sendPackets(new S_ItemName(item));
            column -= COL_CHARGE_COUNT;
        }
        if (column >= COL_ITEMID) { // 別場合(便箋開封)
            this._owner.sendPackets(new S_ItemStatus(item));
            this._owner.sendPackets(new S_ItemColor(item));
            //this._owner.sendPackets(new S_PacketBox(S_PacketBox.WEIGHT, this.getWeight240()));
            // 7.6
            _owner.sendPackets(new S_WeightStatus(_owner.getInventory().getWeight() * 100 / (int) _owner.getMaxWeight(), _owner.getInventory().getWeight(), (int) _owner.getMaxWeight()));
            column -= COL_ITEMID;
        }
        if (column >= COL_DELAY_EFFECT) { // 效果
            column -= COL_DELAY_EFFECT;
        }
        if (column >= COL_COUNT) { // 
            this._owner.sendPackets(new S_ItemStatus(item));
            final int weight = item.getWeight();
            if (weight != item.getLastWeight()) {
                item.setLastWeight(weight);
                this._owner.sendPackets(new S_ItemStatus(item));
            } else {
                this._owner.sendPackets(new S_ItemName(item));
            }
            if (item.getItem().getWeight() != 0) {
                // XXX 240段階變化場合送
                //this._owner.sendPackets(new S_PacketBox(S_PacketBox.WEIGHT, this.getWeight240()));
                // 7.6
                _owner.sendPackets(new S_WeightStatus(_owner.getInventory().getWeight() * 100 / (int) _owner.getMaxWeight(), _owner.getInventory().getWeight(), (int) _owner.getMaxWeight()));
            }
            if (item.getItemId() == 44070) {
                _owner.updateGameMallMoney();
            }
            column -= COL_COUNT;
            L1WilliamLimitedReward.check_Task_For_Item(this._owner, item.getItem().getItemId(), (int) item.getCount());
        }
        if (column >= COL_EQUIPPED) { // 裝備狀態
            this._owner.sendPackets(new S_ItemName(item));
            column -= COL_EQUIPPED;
        }
        if (column >= COL_ENCHANTLVL) { // 
            this._owner.sendPackets(new S_ItemStatus(item));
            column -= COL_ENCHANTLVL;
        }
        if (column >= COL_IS_ID) { // 確認狀態
            this._owner.sendPackets(new S_ItemStatus(item));
            this._owner.sendPackets(new S_ItemColor(item));
            column -= COL_IS_ID;
        }
        if (column >= COL_DURABILITY) { // 耐久性
            this._owner.sendPackets(new S_ItemStatus(item));
            column -= COL_DURABILITY;
        }
    }

    /**
     * 背包內資料更新(SQL)
     *
     * @param item   - 更新對像
     * @param column - 更新種類
     */
    public void saveItem(final L1ItemInstance item, int column) {
        if (column == 0) {
            return;
        }
        try {
            if (column >= COL_ATTR_ENCHANT_LEVEL) { // 屬性強化數
                CharItemsReading.get().updateItemAttrEnchantLevel(item);
                column -= COL_ATTR_ENCHANT_LEVEL;
            }
            if (column >= COL_ATTR_ENCHANT_KIND) { // 屬性強化種類
                CharItemsReading.get().updateItemAttrEnchantKind(item);
                column -= COL_ATTR_ENCHANT_KIND;
            }
            if (column >= COL_BLESS) { // 祝福?封印
                CharItemsReading.get().updateItemBless(item);
                column -= COL_BLESS;
            }
            if (column >= COL_REMAINING_TIME) { // 使用可能殘時間
                CharItemsReading.get().updateItemRemainingTime(item);
                column -= COL_REMAINING_TIME;
            }
            if (column >= COL_CHARGE_COUNT) { // 數
                CharItemsReading.get().updateItemChargeCount(item);
                column -= COL_CHARGE_COUNT;
            }
            if (column >= COL_ITEMID) { // 別場合(便箋開封)
                CharItemsReading.get().updateItemId(item);
                column -= COL_ITEMID;
            }
            if (column >= COL_DELAY_EFFECT) { // 效果
                CharItemsReading.get().updateItemDelayEffect(item);
                column -= COL_DELAY_EFFECT;
            }
            if (column >= COL_COUNT) { // 
                CharItemsReading.get().updateItemCount(item);
                column -= COL_COUNT;
            }
            if (column >= COL_EQUIPPED) { // 裝備狀態
                CharItemsReading.get().updateItemEquipped(item);
                column -= COL_EQUIPPED;
            }
            if (column >= COL_ENCHANTLVL) { // 
                CharItemsReading.get().updateItemEnchantLevel(item);
                column -= COL_ENCHANTLVL;
            }
            if (column >= COL_IS_ID) { // 確認狀態
                CharItemsReading.get().updateItemIdentified(item);
                column -= COL_IS_ID;
            }
            if (column >= COL_DURABILITY) { // 耐久性
                CharItemsReading.get().updateItemDurability(item);
                column -= COL_DURABILITY;
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * LIST物品資料移除
     */
    @Override
    public void deleteItem(L1ItemInstance item) {
        if (CheckItemPowerTable.get().checkItem(item.getItemId())) { // 身上持有道具給予能力系統
            CheckItemPowerTable.get().delepower(_owner, item.getItemId());
        }
        try {
            CharItemsReading.get().deleteItem(_owner.getId(), item);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        int itemid = item.getItemId();
        if (itemid == 40312) {// 旅館鑰匙
            InnKeyTable.DeleteKey(item);// 刪除鑰匙資料
        }
        if (itemid == 82503) {// 訓練所鑰匙
            InnKeyTable.DeleteKey(item);// 刪除鑰匙資料
        }
        if (itemid == 82504) {// 龍門憑證
            InnKeyTable.DeleteKey(item);// 刪除鑰匙資料
        }
        if (itemid == ProtectorSet.ITEM_ID && this instanceof L1PcInventory) {
            L1PcInventory pc_inv = (L1PcInventory) this;
            L1PcInstance pc = pc_inv.getOwner();
            if (pc != null && pc.isProtector()) {
                pc.setProtector(false);
            }
            World.get().broadcastPacketToAll(new S_ServerMessage(2925));
        }
        if (itemid == 56152 && this instanceof L1PcInventory) {
            L1PcInventory pc_inv = (L1PcInventory) this;
            L1PcInstance pc = pc_inv.getOwner();
            // 身上有戰神之魂效果
            if (pc != null && pc.isMars()) {
                pc.setMars(false);
                C_LoginToServer.checkforMars(pc);// 重新檢查戰神之魂持有狀態
            }
        }
        if (itemid == 56148 && this instanceof L1PcInventory) {
            L1PcInventory pc_inv = (L1PcInventory) this;
            L1PcInstance pc = pc_inv.getOwner();
            // 身上有妲蒂斯魔石效果
            if (pc != null && pc.isEffectGS()) {
                pc.setGS(false);
                C_LoginToServer.checkforDADISStone(pc);// 重新檢查妲蒂斯魔石持有狀態
            }
        }
        if (itemid == 56147 && this instanceof L1PcInventory) {
            L1PcInventory pc_inv = (L1PcInventory) this;
            L1PcInstance pc = pc_inv.getOwner();
            // 身上有真妲蒂斯魔石效果
            if (pc != null && pc.isEffectDADIS()) {
                pc.setDADIS(false);
                C_LoginToServer.checkforDADISStone(pc);// 重新檢查妲蒂斯魔石持有狀態
            }
        }
        if (item.isEquipped()) {
            setEquipped(item, false);
        }
        if (item != null) {
            _owner.sendPackets(new S_DeleteInventoryItem(item));
            _items.remove(item);
            if (item.getItem().getWeight() != 0) {
                //_owner.sendPackets(new S_PacketBox(10, getWeight240()));
                // 7.6
                _owner.sendPackets(new S_WeightStatus(_owner.getInventory().getWeight() * 100 / (int) _owner.getMaxWeight(), _owner.getInventory().getWeight(), (int) _owner.getMaxWeight()));
            }
            if (item.getItemId() == 44070) {
                _owner.updateGameMallMoney();
            }
        }
    }

    /**
     * 設置道具的穿脫狀態
     *
     */
    public void setEquipped(L1ItemInstance item, boolean equipped) {
        setEquipped(item, equipped, false, false);
    }

    public void setEquipped(final L1ItemInstance item, final boolean equipped, final boolean loaded, final boolean changeWeapon) {
        //System.out.println("item:"+item.getName()+" equipped:"+equipped);
        if (item.isEquipped() != equipped) { // 設定值違場合處理
            final L1Item temp = item.getItem();
            if (equipped) { // 裝著
                item.setEquipped(true);
                // 裝備穿著效果判斷
                _owner.getEquipSlot().set(item);
            } else { // 脫著
                if (!loaded) {
                    //  裝備中狀態場合狀態解除
                    if (temp.getItemId() == 20077 || temp.getItemId() == 20062 || temp.getItemId() == 120077) {
                        if (_owner.isInvisble()) {
                            _owner.delInvis();
                            return;
                        }
                    }
                }
                item.setEquipped(false);
                // 裝備脫除效果判斷
                _owner.getEquipSlot().remove(item);
            }
            if (!loaded) { // 最初讀迂時ＤＢ關連處理
                // System.out.println("物品裝備狀態");
                // XXX:意味
                _owner.setCurrentHp(_owner.getCurrentHp());
                _owner.setCurrentMp(_owner.getCurrentMp());
                this.updateItem(item, COL_EQUIPPED);
                _owner.sendPackets(new S_OwnCharStatus(_owner));
                // 武器場合更新。、武器持替武器脫著時更新
                if (temp.getType2() == 1 && !changeWeapon) {
                    _owner.sendPacketsAll(new S_CharVisualUpdate(_owner));
                }
            }
            CheckType(_owner, item, equipped);
        }
        if (item.getItem() instanceof L1Weapon) {
            boolean check = false;
            int range = 1;
            int type = 1;
            final L1ItemInstance weapon = _owner.getWeapon();
            if (weapon == null) {
                _owner.sendPackets(new S_PacketBox(S_PacketBox.WEAPON_RANGE, range, 0, check));
            } else {
                if (weapon.getItem().getType() == 4) {
                    range = 17;
                    //range = 15;
                } else if (weapon.getItem().getType() == 10 || weapon.getItem().getType() == 13) {
                    range = 14;
                } else if (weapon.getItem().getType() == 5 || weapon.getItem().getType() == 14 || weapon.getItem().getType() == 18) {
                    range = 1;
                    final int polyId = _owner.getTempCharGfx();
                    if (polyId == 11330 || polyId == 11344 || polyId == 11351 || polyId == 11368 || polyId == 12240 || polyId == 12237 || polyId == 11447 || polyId == 11408 || polyId == 11409 || polyId == 11410 || polyId == 11411 || polyId == 11418 || polyId == 11419 || polyId == 12613 || polyId == 12614) {
                        range = 2;
                    } else if (!_owner.hasSkillEffect(L1SkillId.SHAPE_CHANGE)) {
                        range = 2;
                    }
                }
                if (_owner.isKnight()) {
                    if (weapon.getItem().getType() == 3) {
                        check = true;
                    }
                } else if (_owner.isElf()) {
                    if (_owner.hasSkillEffect(L1SkillId.FIRE_BLESS)) {
                        check = true;
                    }
                    if ((weapon.getItem().getType() == 4 || weapon.getItem().getType() == 13) && weapon.getItem().getType1() == 20) {
                        type = 3;
                        check = true;
                    }
                } else if (_owner.isDragonKnight()) {
                    check = true;
                    if (weapon.getItem().getType() == 14 || weapon.getItem().getType() == 18) {
                        type = 10;
                    }
                } else if (_owner.isDarkelf()) { // 黑妖新技能 暗殺者
                    check = true;
                    if (weapon.getItem().getType() == 12) {
                        type = 4;
                    }
                }
                if (weapon.getItem().getType1() != 20 && weapon.getItem().getType1() != 62) {
                    _owner.sendPackets(new S_PacketBox(S_PacketBox.WEAPON_RANGE, range, type, check));
                } else {
                    _owner.sendPackets(new S_PacketBox(S_PacketBox.WEAPON_RANGE, range, 3, check));
                }
                _owner.setRange(range);
            }
        }
    }

    public L1ItemInstance checkEquippedItem(int id) {
        try {
            for (L1ItemInstance item : _items) {
                if (item.getItem().getItemId() == id && item.isEquipped()) {
                    return item;
                }
            }
        } catch (Exception ex) {
            _log.error(ex.getLocalizedMessage(), ex);
        }
        return null;
    }

    public boolean checkEquipped(int id) {
        try {
            for (L1ItemInstance item : _items) {
                if (item.getItem().getItemId() == id && item.isEquipped()) {
                    return true;
                }
            }
        } catch (Exception ex) {
            _log.error(ex.getLocalizedMessage(), ex);
        }
        return false;
    }

    public boolean checkCardEquipped(int itemid) {
        try {
            for (L1ItemInstance item : _items) {
                if (item.getItem().getItemId() == itemid && item.get_card_use() == 1) {
                    return true;
                }
            }
        } catch (Exception ex) {
            _log.error(ex.getLocalizedMessage(), ex);
        }
        return false;
    }

    public boolean checkEquipped(String nameid) {
        try {
            for (L1ItemInstance item : _items) {
                if (item.getName().equals(nameid) && item.isEquipped()) {
                    return true;
                }
            }
        } catch (Exception ex) {
            _log.error(ex.getLocalizedMessage(), ex);
        }
        return false;
    }

    public boolean checkEquipped(int[] ids) {
        try {
            for (int id : ids) {
                if (!checkEquipped(id)) {
                    return false;
                }
            }
        } catch (Exception ex) {
            _log.error(ex.getLocalizedMessage(), ex);
        }
        return true;
    }

    public boolean checkEquipped(String[] names) {
        try {
            for (String name : names) {
                if (!checkEquipped(name)) {
                    return false;
                }
            }
        } catch (Exception ex) {
            _log.error(ex.getLocalizedMessage(), ex);
        }
        return true;
    }

    /**
     * 裝備中指定類型物品數量
     *
     * @param type2 類型
     * @param type  物品分類
     * @return 裝備中指定類型物品數量
     */
    public int getTypeEquipped(final int type2, final int type) {
        int equipeCount = 0;// 裝備中指定位置物品數量
        try {
            for (final L1ItemInstance item : this._items) {
                // 物品類型相等 物品分類相等 並且在使用中
                if (item.getItem().getType2() == type2 && item.getItem().getType() == type && item.isEquipped()) {
                    equipeCount++;// 使用數量+1
                }
            }
        } catch (final Exception ex) {
            _log.error(ex.getLocalizedMessage(), ex);
        }
        return equipeCount;
    }

    /**
     * 裝備中耳環陣列
     *
     */
    public L1ItemInstance[] getEaringEquipped() {
        final L1ItemInstance equipeItem[] = new L1ItemInstance[2];
        try {
            int equipeCount = 0;
            for (final L1ItemInstance item : this._items) {
                // 物品為耳環 並且在使用中
                if (item.getItem().getUseType() == 40 && // 耳環
                        item.isEquipped()) {
                    equipeItem[equipeCount] = item;
                    equipeCount++;
                    if (equipeCount == 2) {
                        break;
                    }
                }
            }
        } catch (final Exception ex) {
            _log.error(ex.getLocalizedMessage(), ex);
        }
        return equipeItem;
    }

    /**
     * 裝備中指定類型物品
     *
     * @param type2 類型
     * @param type  物品分類
     * @return 裝備中指定類型物品
     */
    public L1ItemInstance getItemEquipped(final int type2, final int type) {
        L1ItemInstance equipeitem = null;
        try {
            for (final L1ItemInstance item : this._items) {
                // 物品類型相等 物品分類相等 並且在使用中
                if (item.getItem().getType2() == type2 && item.getItem().getType() == type && item.isEquipped()) {
                    equipeitem = item;
                    break;
                }
            }
        } catch (final Exception ex) {
            _log.error(ex.getLocalizedMessage(), ex);
        }
        return equipeitem;
    }

    /**
     * 設置 顯示/消除 套裝效果 XXX
     *
     * @param armorSet 套裝
     * @param isMode   是否顯示 額外屬性
     */
	/*public void setPartMode(final ArmorSet armorSet, final boolean isMode) {
	final int tgItemId = armorSet.get_ids()[0];// 取回套裝第一樣物品ID
	final L1ItemInstance[] tgItems = findItemsId(tgItemId);
	for (L1ItemInstance tgItem : tgItems) {
		tgItem.setIsMatch(isMode);
		this._owner.sendPackets(new S_ItemStatus(tgItem));
	}
}*/
    public void setPartMode(ArmorSet armorSet, boolean isMode) //src012
    {
        int[] tgItemId = armorSet.get_ids();
        int len = tgItemId.length;
        for (int j : tgItemId) {
            L1ItemInstance[] tgItems = findItemsId(j);
            for (L1ItemInstance tgItem : tgItems) {
                tgItem.setIsMatch(isMode);
                this._owner.sendPackets(new S_ItemStatus(tgItem));
            }
        }
    }

    /**
     * 裝備中界指陣列
     *
     */
    public L1ItemInstance[] getRingEquipped() {
        final L1ItemInstance equipeItem[] = new L1ItemInstance[4];
        try {
            int equipeCount = 0;
            for (final L1ItemInstance item : this._items) {
                // 物品為戒指 並且在使用中
                if (item.getItem().getUseType() == 23 && // 戒指
                        item.isEquipped()) {
                    equipeItem[equipeCount] = item;
                    equipeCount++;
                    if (equipeCount == 4) {
                        break;
                    }
                }
            }
        } catch (final Exception ex) {
            _log.error(ex.getLocalizedMessage(), ex);
        }
        return equipeItem;
    }

    /**
     * 裝備中耳環陣列
     *
     */
    public L1ItemInstance[] getEarRingEquipped() {
        L1ItemInstance[] equipeItem = new L1ItemInstance[2];
        try {
            int equipeCount = 0;
            for (L1ItemInstance item : _items) {
                // 物品為耳環 並且在使用中
                if (item.getItem().getUseType() == 40 && item.isEquipped()) {
                    equipeItem[equipeCount] = item;
                    equipeCount++;
                    if (equipeCount == 2) {
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            _log.error(ex.getLocalizedMessage(), ex);
        }
        return equipeItem;
    }

    public void takeoffEquip(int polyid) {
        takeoffWeapon(polyid);
        takeoffArmor(polyid);
    }

    private void takeoffWeapon(int polyid) {
        if (_owner.getWeapon() == null) {
            return;
        }
        boolean takeoff = false;
        int weapon_type = _owner.getWeapon().getItem().getType();
        takeoff = !L1PolyMorph.isEquipableWeapon(polyid, weapon_type);
        if (takeoff) {
            setEquipped(_owner.getWeapon(), false, false, false);
        }
    }

    public void takeoffWeaponFor2600() {
        L1ItemInstance item = _owner.getInventory().findItemId(5010);
        if (item != null) {
            if (_owner.getWeapon() != null) {
                setEquipped(_owner.getWeapon(), false, false, false);
            }
            _owner.getInventory().setEquipped(item, true);
            L1PolyMorph.doPoly(_owner, 12232, 3600, 1);
        }
    }

    private void takeoffArmor(int polyid) {
        L1ItemInstance armor = null;
        for (int type = 0; type <= 13; type++) {
            if (getTypeEquipped(2, type) != 0 && !L1PolyMorph.isEquipableArmor(polyid, type)) {
                if (type == 9) {// 脫下四顆戒指
                    armor = getItemEquipped(2, type);
                    if (armor != null) {
                        setEquipped(armor, false, false, false);
                    }
                    armor = getItemEquipped(2, type);
                    if (armor != null) {
                        setEquipped(armor, false, false, false);
                    }
                    armor = getItemEquipped(2, type);
                    if (armor != null) {
                        setEquipped(armor, false, false, false);
                    }
                    armor = getItemEquipped(2, type);
                    if (armor != null) {
                        setEquipped(armor, false, false, false);
                    }
                } else if (type == 12) {// 脫下兩個耳環
                    armor = getItemEquipped(2, type);
                    if (armor != null) {
                        setEquipped(armor, false, false, false);
                    }
                    armor = getItemEquipped(2, type);
                    if (armor != null) {
                        setEquipped(armor, false, false, false);
                    }
                } else {
                    armor = getItemEquipped(2, type);
                    if (armor != null) {
                        setEquipped(armor, false, false, false);
                    }
                }
            }
        }
    }

    public L1ItemInstance getArrow() {
        return getBullet(-2);
    }

    public void setArrow(int id) {
        _arrowId = id;
    }

    public L1ItemInstance getSting() {
        return getBullet(-3);
    }

    public void setSting(int id) {
        _stingId = id;
    }

    private L1ItemInstance getBullet(final int useType) {
        L1ItemInstance bullet;
        int priorityId = 0;
        if (useType == -2) {
            if (this._owner.getWeapon().getItemId() == 192) {// 水精靈之弓
                bullet = this.findItemId(40742);// 古代之箭
                if (bullet == null) {
                    // 329：\f1沒有具有 %0%o。
                    this._owner.sendPackets(new S_ServerMessage(329, "$2377"));
                }
                return bullet;
            } else {
                priorityId = this._arrowId; // 箭
            }
        }
        if (useType == -3) {
            priorityId = this._stingId; // 飛刀
        }
        if (priorityId > 0) {// 優先彈
            bullet = this.findItemId(priorityId);
            if (bullet != null) {
                return bullet;
            } else {// 場合優先消
                if (useType == -2) {
                    this._arrowId = 0;
                }
                if (useType == -3) {
                    this._stingId = 0;
                }
            }
        }
        for (final Object itemObject : this._items) {// 彈探
            bullet = (L1ItemInstance) itemObject;
            if (bullet.getItem().getUseType() == useType) {
                if (useType == -2) {// 箭
                    this._arrowId = bullet.getItem().getItemId(); // 優先
                }
                if (useType == -3) {
                    this._stingId = bullet.getItem().getItemId(); // 優先
                }
                return bullet;
            }
        }
        return null;
    }

    /**
     * 裝備 hp自然回復補正
     *
     */
    public int hpRegenPerTick() {
        int hpr = 0;
        try {
            for (final Object itemObject : this._items) {
                final L1ItemInstance item = (L1ItemInstance) itemObject;
                if (item.isEquipped()) {
                    hpr += item.getItem().get_addhpr();
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return hpr;
    }

    /**
     * 裝備 mp自然回復補正
     *
     */
    public int mpRegenPerTick() {
        int mpr = 0;
        try {
            for (final Object itemObject : this._items) {
                final L1ItemInstance item = (L1ItemInstance) itemObject;
                if (item.isEquipped()) {
                    mpr += item.getMpr();
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return mpr;
    }

    /**
     * 傳回死亡逞罰掉落物品
     *
     */
    public L1ItemInstance caoPenalty() {
        try {
            Random random = new Random();
            int rnd = random.nextInt(_items.size());
            L1ItemInstance penaltyItem = (L1ItemInstance) _items.get(rnd);
            if (penaltyItem.getItem().getItemId() == 80033) {// 推廣銀幣
                return null;
            }
            if (penaltyItem.getItem().getItemId() == 44070) {// 商城幣
                return null;
            }
            if (penaltyItem.getItem().getItemId() == 40308) {// 金幣
                return null;
            }
            if (penaltyItem.getItem().getItemId() == 50308) {// 天票
                return null;
            }
            if (penaltyItem.getItem().getItemId() == 83000) {// 貝利
                return null;
            }
            if (penaltyItem.getItem().getItemId() == 83022) {// 黃金貝利
                return null;
            }
            if (penaltyItem.getItem().getItemId() == 83048) {// 龍之幣
                return null;
            }
            if (penaltyItem.getItem().isCantDelete()) {// 無法刪除的物品
                return null;
            }
            if (!penaltyItem.getItem().isTradable()) {// 無法轉移的物品
                return null;
            }
            if (penaltyItem.get_time() != null) {// 具有時間限制的物品
                return null;
            }
            if (ItemRestrictionsTable.RESTRICTIONS.contains(penaltyItem.getItemId())) {// 在交易限制道具清單中
                return null;
            }
            // 寵物項圈
            Object[] petlist = _owner.getPetList().values().toArray();
            for (Object petObject : petlist) {
                if (petObject instanceof L1PetInstance) {
                    L1PetInstance pet = (L1PetInstance) petObject;
                    if (penaltyItem.getId() == pet.getItemObjId()) {
                        return null;
                    }
                }
            }
            // 取回娃娃
            if (_owner.getDoll(penaltyItem.getId()) != null) {
                return null;
            }
            // 取回娃娃
            if (_owner.getDoll2(penaltyItem.getId()) != null) {
                return null;
            }
            // 超級娃娃
            if (_owner.get_power_doll() != null) {
                if (penaltyItem.getId() == _owner.get_power_doll().getItemObjId()) {
                    return null;
                }
            }
            setEquipped(penaltyItem, false);
            return penaltyItem;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    public void delQuestItem(int itemId) {
        try {
            Random random = new Random();
            for (L1ItemInstance item : _items) {
                if (item.getItemId() == itemId) {
                    removeItem(item);
                    _owner.sendPackets(new S_ServerMessage(random.nextInt(4) + 445, item.getName()));
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public int checkAddItem_LV(L1ItemInstance item, int count, int EnchantLevel) {
        return checkAddItem_LV(item, count, EnchantLevel, true);
    }

    public int checkAddItem_LV(L1ItemInstance item, int count, int EnchantLevel, boolean message) {
        if (item == null) {
            return -1;
        }
        if (getSize() > 180 || getSize() == 180 && (!item.isStackable() || !checkItem(item.getItem().getItemId()))) {
            if (message) {
                sendOverMessage(263);
            }
            return 1;
        }
        int weight = getWeight() + item.getItem().getWeight() * count / 1000 + 1;
        if (weight < 0 || item.getItem().getWeight() * count / 1000 < 0) {
            if (message) {
                sendOverMessage(82);
            }
            return 2;
        }
        if (calcWeight240(weight) >= 240) {
            if (message) {
                sendOverMessage(82);
            }
            return 2;
        }
        L1ItemInstance itemExist = findItemId(item.getItemId());
        if (itemExist != null && itemExist.getCount() + count > 9223372036854775807L) {
            if (message) {
                getOwner().sendPackets(new S_ServerMessage(166, "所持有的金幣", "超過了2,000,000,000上限"));
            }
            return 3;
        }
        return 0;
    }

    public void CheckType(final L1PcInstance pc, final L1ItemInstance item, final boolean isEq) {
        final L1Item temp = item.getItem();
        int type = 0;
        switch (temp.getType2()) {
            case 1:
                type = 9;
                break;
            case 2:
                if (temp.getType() >= 1 && temp.getType() <= 4) {
                    type = temp.getType();
                } else if (temp.getType() == 5 || temp.getType() == 6) {
                    type = temp.getType() == 5 ? 7 : 6;
                } else if (temp.getType() == 7 || temp.getType() == 13) {
                    type = 8;
                } else if (temp.getType() == 8 || temp.getType() == 10) {
                    type = temp.getType() == 8 ? 11 : 12;
                } else if (temp.getType() == 15) {
                    type = 15;
                } else if (temp.getType() == 17) {
                    type = 16;
                } else if (temp.getType() == 18) {
                    type = 18;
                } else if (temp.getType() == 23) {
                    type = 27;
                } else if (temp.getType() == 16) {
                    type = 5;
                } else if (temp.getType() == 9) {
                    if (!isEq) {
                        for (int i = 0; i < 4; i++) {
                            if (_RingList.get(i) != null && _RingList.get(i) == item) {
                                _RingList.remove(i);
                                type = 19 + i;
                                break;
                            }
                        }
                    } else {
                        for (int i = 0; i < 4; i++) {
                            if (_RingList.get(i) == null) {
                                _RingList.put(i, item);
                                type = 19 + i;
                                break;
                            }
                        }
                        for (int i = 0; i < 4; i++) {
                            if (_RingList.get(i) != null && _RingList.get(i) == item) {
                                type = 19 + i;
                                break;
                            }
                        }
                    }
                } else if (temp.getType() == 12) {
                    if (isEq) {
                        for (int i = 0; i < 2; i++) {
                            if (_EarringList.get(i) == null) {
                                _EarringList.put(i, item);
                                type = 13;
                                if (i == 1) {
                                    type += 13;
                                }
                                break;
                            }
                        }
                        // 如果沒找到空槽位，預設使用第一個耳環槽位
                        if (type == 0) {
                            type = 13;
                        }
                    } else {
                        for (int i = 0; i < 2; i++) {
                            if (_EarringList.get(i) != null && _EarringList.get(i) == item) {
                                _EarringList.remove(i);
                                type = 13;
                                if (i == 1) {
                                    type += 13;
                                }
                                break;
                            }
                        }
                        // 如果沒找到對應耳環，預設使用第一個耳環槽位
                        if (type == 0) {
                            type = 13;
                        }
                    }
                } else if (temp.getType() == 14) {
                    if (isEq) {
                        for (int i = 0; i < 2; i++) {
                            if (_RuneList.get(i) == null) {
                                _RuneList.put(i, item);
                                type = 23;
                                if (i == 1) {
                                    type += 4;
                                }
                                break;
                            }
                        }
                        // 如果沒找到空槽位，預設使用第一個符文槽位
                        if (type == 0) {
                            type = 23;
                        }
                    } else {
                        for (int i = 0; i < 2; i++) {
                            if (_RuneList.get(i) != null && _RuneList.get(i) == item) {
                                _RuneList.remove(i);
                                type = 23;
                                if (i == 1) {
                                    type += 4;
                                }
                                break;
                            }
                        }
                        // 如果沒找到對應符文，預設使用第一個符文槽位
                        if (type == 0) {
                            type = 23;
                        }
                    }
                } else if (temp.getType() == 20) {
                    type = 51; // 蒂蜜特的符文 (talisman3)
                }
                break;
        }
        pc.sendPackets(new S_EquipmentSlot(item.getId(), type, isEq));
    }

    public void equippedLoad() {
        int count = 0;
        for (final L1ItemInstance item : _items) {
            if (item.isEquipped()) {
                if (item.getItemId() == 20383) {
                    continue;
                }
                count++;
            }
        }
        _owner.sendPackets(new S_EquipmentSlot(_owner, count));
    }

    public void viewItem() {
        final List<L1ItemInstance> itemlist = new CopyOnWriteArrayList<>();
        for (final L1ItemInstance item : _items) {
            if (item.isEquipped()) {
                item.setEquipped(false);
                if (item.getItem().getType2() == 0 && item.getItem().getType() == 2) { // 照明道具
                    item.setRemainingTime(item.getItem().getLightFuel());
                }
                if (item.getItemId() == ProtectorSet.ITEM_ID) {
                    _owner.giveProtector(true);
                }
                itemlist.add(item);
            }
        }
        boolean weaponck = false;
        for (final L1ItemInstance item : itemlist) {
            if (!_owner.isWarrior()) {
                if (item.getItemId() == 20383) {
                    continue;
                }
                setEquipped(item, true, true, false);
            } else {
                if (item.getItem().getType2() == 1) {
                    if (weaponck) {
                        setWarriorEquipped(item, true, true);
                    } else {
                        setEquipped(item, true, true, false);
                        weaponck = true;
                    }
                } else {
                    setEquipped(item, true, true, false);
                }
            }
        }
        itemlist.clear();
    }

    public void setWarriorEquipped(final L1ItemInstance item, final boolean equipped, final boolean isview) {
        if (equipped) {
            item.setEquipped(true);
            // 裝備穿著效果判斷
            _owner.getEquipSlot().setSecond(item);
            _owner.setWeaponWarrior(item);
            _owner.setCurrentWeapon(88);
            _owner.sendPackets(new S_OwnCharStatus(_owner));
        } else {
            item.setEquipped(false);
            // 裝備脫下效果判斷
            _owner.getEquipSlot().removeSecond(item);
            _owner.setWeaponWarrior(null);
            if (_owner.getWeapon() != null) {
                _owner.setCurrentWeapon(_owner.getWeapon().getItem().getType1());
            }
            _owner.sendPackets(new S_OwnCharStatus(_owner));
        }
        updateItem(item, COL_EQUIPPED);
        _owner.sendPackets(new S_EquipmentSlot(item.getId(), 8, isview));
    }

    public int checkAddItem(final int itemId, final int count) {
        return checkAddItem(ItemTable.get().createItem(itemId, false), count, true);
    }

    public int getGarderEquipped(final int type2, final int type, final int gd) {
        int equipeCount = 0;
        L1ItemInstance item = null;
        for (final Object itemObject : _items) {
            item = (L1ItemInstance) itemObject;
            if (item.getItem().getType2() == type2 && item.getItem().getType() == type && item.getItem().getUseType() != gd && item.isEquipped()) {
                equipeCount++;
            }
        }
        return equipeCount;
    }

    public boolean CheckSellPrivateShopItem(int id, int enchantLevel, int attr, int bless) {
        L1ItemInstance item = findItemId(id);
        if (item != null && !item.isEquipped() && item.getEnchantLevel() == enchantLevel && item.getAttrEnchantLevel() == attr && item.getBless() == bless) {
            return true;
        }
        return false;
    }

    /**
     * 取回身上指定道具的數量 (道具編號) by terry0412
     *
     * @param id 道具編號
     * @return 道具數量
     */
    public final int getEquippedCountByItemId(final int id) {  //src013
        int equippedCount = 0;
        // 背包道具查找
        for (final L1ItemInstance item : this._items) {
            // 物品在使用中並且編號相同
            if (item.isEquipped() && item.getItem().getItemId() == id) {
                equippedCount++;
            }
        }
        return equippedCount;
    }
    /** 檢查已裝備的相同道具數量 **/
	/*public int getTypeAndItemIdEquipped(int type2, int type, int itemId) {  //src013  功能重複 用不到
		int equipeCount = 0;
		L1ItemInstance item = null;
		for (Object itemObject : _items) {
			item = (L1ItemInstance) itemObject;
			if (item.getItem().getType2() == type2 && item.getItem().getType() == type 
					&& item.getItemId() == itemId && item.isEquipped()					
					) {
				equipeCount++;
			}
		}
		return equipeCount;
	}*/

    /**
     * 取回身上指定道具的數量 (活動戒指或收費戒指) by terry0412
     *
     * @return 道具數量
     */
    public final int getEquippedCountByActivityItem() {
        int equippedCount = 0;
        // 背包道具查找
        for (final L1ItemInstance item : this._items) {
            // 物品在使用中並且是活動戒指或收費戒指
            if (item.isEquipped() && item.getItem().isActivity()) {
                equippedCount++;
            }
        }
        return equippedCount;
    }

    // 裝備切換
    public L1ItemInstance findItemObjId(int id) {
        for (L1ItemInstance item : this._items) {
            if (item == null) {
                continue;
            }
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.L1PcInventory JD-Core Version: 0.6.2
 */