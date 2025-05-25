package com.lineage.server.model.Instance;

import com.add.L1Config;
import com.lineage.data.event.SubItemSet;
import com.lineage.server.datatables.*;
import com.lineage.server.datatables.lock.PetReading;
import com.lineage.server.model.L1EquipmentTimer;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.*;
import com.lineage.server.utils.RangeLong;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import static com.lineage.server.model.skill.L1SkillId.BLESS_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.SHADOW_FANG;

public class L1ItemInstance extends L1Object {
    private static final Log _log = LogFactory.getLog(L1ItemInstance.class);
    private static final long serialVersionUID = 1L;
    private final LastStatus _lastStatus = new LastStatus();
    /**
     * 是否為第2把武器
     */
    public boolean _isSecond = false;
    private long _count;
    private String _gamNo1;
    private int _itemId;
    private boolean _isEquipped;
    private boolean _isEquippedTemp;
    private int _enchantLevel;
    private boolean _isIdentified;
    private int _durability;
    private int _chargeCount;
    private int _remainingTime;
    private int _lastWeight;
    private boolean _isRunning;
    private int _bless;
    private int _attrEnchantKind;
    private int _attrEnchantLevel;
    private int _ItemAttack;// terry770106
    private int _ItemBowAttack;// terry770106
    private int _ItemReductionDmg;// terry770106
    private int _ItemSp;// terry770106
    private int _Itemprobability;// terry770106
    private int _ItemStr;// terry770106
    private int _ItemDex;// terry770106
    private int _ItemInt;// terry770106
    private int _ItemHp;// terry770106
    private int _ItemMp;// terry770106
    private int _ItemCon;
    private int _ItemCha;
    private int _ItemWis;
    private String _racegamno;
    private L1Item _item;
    private Timestamp _lastUsed;
    private L1PcInstance _pc;
    private EnchantTimer _timer;
    /**
     * [原碼] 怪物對戰系統
     */
    private int _gamNo;
    private int _gamNpcId;
    /**
     * [原碼] 大樂透系統
     */
    private String _starNpcId;
    private int _acByMagic = 0;
    private int _dmgByMagic = 0;
    private int _holyDmgByMagic = 0;
    private int _hitByMagic = 0;
    private int _itemOwnerId = 0;
    private L1EquipmentTimer _equipmentTimer;
    private boolean _isNowLighting = false;
    private boolean _isMatch = false;
    private int _char_objid = -1;
    private Timestamp _time = null;
    private int _card_use = 0;
    private int _keyId = 0;
    private int _innNpcId = 0;
    private boolean _isHall;
    private Timestamp _dueTime;
    private String _gamno;
    // 古文字
    private L1ItemPower_name _power_name = null;
    private L1ItemPower_name _powerdata = null;
    /**
     * 四戒指顯示裝備
     *
     */
    private int _ringId;
    /**
     * 雙耳環顯示裝備
     *
     */
    private int _earringId;
    /**
     * [原碼] 裝備保護卷軸
     */
    private boolean proctect = false;
    private int _hasProtect = 0;  //src040
    private byte _eqId;
    // 凹槽
    private L1ItemPower_bless _power_bless = null;
    // 強化擴充能力
    // 力量
    private int _updateStr = 0;
    // 敏捷
    private int _updateDex = 0;
    // 體質
    private int _updateCon = 0;
    // 精神
    private int _updateWis = 0;
    // 智力
    private int _updateInt = 0;
    // 魅力
    private int _updateCha = 0;
    /**
     * 炫色系統
     */
    private L1ItemSpecialAttributeChar _ItemAttrName2 = null;
    // 血量
    private int _updateHp = 0;
    // 魔量
    private int _updateMp = 0;
    // 地屬性
    private int _updateEarth = 0;
    // 風屬性
    private int _updateWind = 0;
    // 水屬性
    private int _updateWater = 0;
    // 火屬性
    private int _updateFire = 0;
    // 抗魔
    private int _updateMr = 0;
    // 防禦
    private int _updateAc = 0;
    // 回血
    private int _updateHpr = 0;
    // 回魔
    private int _updateMpr = 0;
    // 魔法攻擊
    private int _updateSp = 0;
    // 近戰攻擊
    private int _updateDmgModifier = 0;
    // 近戰命中
    private int _updateHitModifier = 0;
    // 遠攻攻擊
    private int _updateBowDmgModifier = 0;
    // 遠攻命中
    private int _updateBowHitModifier = 0;
    // pvp攻擊
    private int _updatePVPdmg = 0;
    // pvp減免
    private int _updatePVPdmg_R = 0;
    // 武器劍靈系統
    private int _updateWeaponSoul = 0;
    private L1CharaterTrade _itemCharaterTrade = null;
    private int _is_isColorWashout;
    //暗黑系統裝備
    private L1ItemAttr _ItemAttrName = null;
    private CharItemSublimation _sublimation = null;
    //昇華系統//
    // 新增欄位
    private int _dmgChanceHp;
    private int _dmgChanceMp;
    private boolean _withstandDmg;
    private boolean _withstandMagic;
    private boolean _returnDmg;
    private boolean _returnMagic;
    private boolean _returnSkills;
    private int _returnChanceHp;
    private int _returnChanceMp;

    public L1ItemInstance() {
        _count = 1L;
        _enchantLevel = 0;
    }

    public L1ItemInstance(L1Item item, long count) {
        this();
        setItem(item);
        setCount(count);
    }

    public String getName() {
        return _item.getName();
    }

    public String getNameEva() {
        return _item.getName();
    }

    public long getCount() {
        return _count;
    }

    public void setCount(long count) {
        _count = count;
    }

    /**
     * 設定場次代號
     *
     */
    public void setraceGamNo(final String racegamno) {
        _racegamno = racegamno;
    }

    public int getItemAttack() {
        return _ItemAttack;
    }

    public void setItemAttack(int i) {
        _ItemAttack = i;
    }

    public int getItemBowAttack() {
        return _ItemBowAttack;
    }

    public void setItemBowAttack(int i) {
        _ItemBowAttack = i;
    }

    public int getItemReductionDmg() {
        return _ItemReductionDmg;
    }

    public void setItemReductionDmg(int i) {
        _ItemReductionDmg = i;
    }

    public int getItemSp() {
        return _ItemSp;
    }

    public void setItemSp(int i) {
        _ItemSp = i;
    }

    public int getItemprobability() {
        return _Itemprobability;
    }

    public void setItemprobability(int i) {
        _Itemprobability = i;
    }

    public boolean isIdentified() {
        return _isIdentified;
    }

    /**
     * 設置鑒定狀態
     *
     * @param identified 確認true、未確認false。
     */
    public void setIdentified(boolean identified) {
        _isIdentified = identified;
    }

    public int getItemStr() {
        return _ItemStr;
    }

    public void setItemStr(int i) {
        _ItemStr = i;
    }

    public int getItemDex() {
        return _ItemDex;
    }

    public void setItemDex(int i) {
        _ItemDex = i;
    }

    public int getItemInt() {
        return _ItemInt;
    }

    public void setItemInt(int i) {
        _ItemInt = i;
    }

    public int getItemHp() {
        return _ItemHp;
    }

    public void setItemHp(int i) {
        _ItemHp = i;
    }

    public String getraceGamNo() {
        return _racegamno;
    }

    public int getItemMp() {
        return _ItemMp;
    }

    public void setItemMp(int i) {
        _ItemMp = i;
    }

    public int getItemCon() {
        return _ItemCon;
    }

    public void setItemCon(int i) {
        _ItemCon = i;
    }

    public int getItemWis() {
        return _ItemWis;
    }

    public void setItemWis(int i) {
        _ItemWis = i;
    }

    public int getItemCha() {
        return _ItemCha;
    }

    public void setItemCha(int i) {
        _ItemCha = i;
    }

    /**
     * 物品裝備狀態
     *
     * @return 已裝備true、未裝備false。
     */
    public boolean isEquipped() {
        return _isEquipped;
    }

    public void setEquipped(boolean equipped) {
        _isEquipped = equipped;
    }

    public L1Item getItem() {
        return _item;
    }

    public void setItem(L1Item item) {
        _item = item;
        _itemId = item.getItemId();
    }

    public int getItemId() {
        return _itemId;
    }

    public void setItemId(int itemId) {
        _itemId = itemId;
    }

    public boolean isStackable() {
        return _item.isStackable();
    }

    public void onAction(L1PcInstance player) {
    }

    public int getEnchantLevel() {
        return _enchantLevel;
    }

    public void setEnchantLevel(int enchantLevel) {
        _enchantLevel = enchantLevel;
    }

    public int getSafeEnchantLevel() {
        return _item.get_safeenchant();
    }

    public int get_gfxid() {
        int gfxid = _item.getGfxId();
        return gfxid;
    }

    public int get_durability() {
        return _durability;
    }

    public void set_durability(int i) {
        if (i < 0) {
            i = 0;
        }
        if (i > 127) {
            i = 127;
        }
        _durability = i;
    }

    public int getChargeCount() {
        return _chargeCount;
    }

    public void setChargeCount(int i) {
        _chargeCount = i;
    }

    public int getRemainingTime() {
        return _remainingTime;
    }

    public void setRemainingTime(int i) {
        _remainingTime = i;
    }

    public Timestamp getLastUsed() {
        return _lastUsed;
    }

    public void setLastUsed(Timestamp t) {
        _lastUsed = t;
    }

    public int getLastWeight() {
        return _lastWeight;
    }

    public void setLastWeight(int weight) {
        _lastWeight = weight;
    }

    public int getBless() {
        return _bless;
    }

    public void setBless(int i) {
        _bless = i;
    }

    public int getAttrEnchantKind() {
        return _attrEnchantKind;
    }

    public void setAttrEnchantKind(int i) {
        _attrEnchantKind = i;
    }

    public int getAttrEnchantLevel() {
        return _attrEnchantLevel;
    }

    public void setAttrEnchantLevel(int i) {
        _attrEnchantLevel = i;
    }

    public int getWeight() {
        if (getItem().getWeight() == 0) {
            return 0;
        }
        return (int) Math.max(getCount() * getItem().getWeight() / 1000L, 1L);
    }

    public LastStatus getLastStatus() {
        return _lastStatus;
    }

    /**
     * 前回DB保存時變化集合返。
     */
    public int getRecordingColumns() {
        int column = 0;
        if (getCount() != _lastStatus.count) {
            column += L1PcInventory.COL_COUNT;
        }
        if (getItemId() != _lastStatus.itemId) {
            column += L1PcInventory.COL_ITEMID;
        }
        if (isEquipped() != _lastStatus.isEquipped) {
            column += L1PcInventory.COL_EQUIPPED;
        }
        if (getEnchantLevel() != _lastStatus.enchantLevel) {
            column += L1PcInventory.COL_ENCHANTLVL;
        }
        if (get_durability() != _lastStatus.durability) {
            column += L1PcInventory.COL_DURABILITY;
        }
        if (getChargeCount() != _lastStatus.chargeCount) {
            column += L1PcInventory.COL_CHARGE_COUNT;
        }
        if (getLastUsed() != _lastStatus.lastUsed) {
            column += L1PcInventory.COL_DELAY_EFFECT;
        }
        if (isIdentified() != _lastStatus.isIdentified) {
            column += L1PcInventory.COL_IS_ID;
        }
        if (getRemainingTime() != _lastStatus.remainingTime) {
            column += L1PcInventory.COL_REMAINING_TIME;
        }
        if (getBless() != _lastStatus.bless) {
            column += L1PcInventory.COL_BLESS;
        }
        if (getAttrEnchantKind() != _lastStatus.attrEnchantKind) {
            column += L1PcInventory.COL_ATTR_ENCHANT_KIND;
        }
        if (getAttrEnchantLevel() != _lastStatus.attrEnchantLevel) {
            column += L1PcInventory.COL_ATTR_ENCHANT_LEVEL;
        }
        return column;
    }

    /**
     * 取回屬性強化階段描述
     *
     */
    private StringBuilder attrEnchantLevelEva() {
        StringBuilder attrEnchant = new StringBuilder();
        int attrEnchantLevel = getAttrEnchantLevel();
        L1AttrWeapon attrWeapon = ExtraAttrWeaponTable.getInstance().get(getAttrEnchantKind(), attrEnchantLevel);
        if (attrWeapon != null) {
            attrEnchant.append(attrWeapon.getName());
        }
        return attrEnchant;
    }

    /**
     * 物件完整名稱取回
     *
     * @param count 數量
     */
    public String getItemNameEva(final long count) {
        final StringBuilder name = new StringBuilder();
        switch (_item.getUseType()) {
            case 1:// 武器
                // 附加屬性
                final int attrEnchantLevel = getAttrEnchantLevel();
                if (attrEnchantLevel > 0) {
                    name.append(attrEnchantLevelEva());
                }
                // 追加值
                if (getEnchantLevel() >= 0) {
                    name.append("+").append(getEnchantLevel()).append(" ");
                } else if (getEnchantLevel() < 0) {
                    name.append(String.valueOf(getEnchantLevel())).append(" ");
                }
                break;
            case 2: // 盔甲
            case 20: // 手套
            case 21: // 靴
            case 22: // 頭盔
            case 18: // T恤
            case 19: // 斗篷
            case 25: // 盾牌
            case 23:// 戒指
            case 24:// 項鏈
            case 37:// 腰帶
            case 40:// 耳環
                // 追加值
                if (getEnchantLevel() >= 0) {
                    name.append("+").append(getEnchantLevel()).append(" ");
                } else if (getEnchantLevel() < 0) {
                    name.append(String.valueOf(getEnchantLevel())).append(" ");
                }
                break;
        }
        name.append(_item.getName());
        if (_item.getUseType() == -5) { // 食人妖精競賽票
            name.append("\\f_[").append(getGamNo()).append("]");
        }
        if (this.isIdentified()) {
            // 資料庫原始最大可用次數大於0
            if (getItem().getMaxChargeCount() > 0) {
                name.append(" (").append(getChargeCount()).append(")");
            } else {
                switch (_item.getItemId()) {
                    case 20383: // 軍馬頭盔
                        name.append(" (").append(getChargeCount()).append(")");
                        break;
                    default:
                        break;
                }
            }
            if (_time == null) {
                // 武器/防具 具有使用時間
                if ((getItem().getMaxUseTime() > 0) && (getItem().getType2() != 0)) {
                    name.append(" (").append(getRemainingTime()).append(")");
                }
            }
        }
        if (count > 1) {
            if (count < 1000000000) {
                name.append(" (").append(count).append(")");
            } else {
                name.append(" (").append(RangeLong.scount(count)).append(")");
            }
        }
        return name.toString();
    }

    /**
     * 取回道具顯示名稱(強化值、屬性、使用次數、數量)
     *
     */

    public String getNumberedViewName(long count) {
        StringBuilder name = new StringBuilder(getNumberedName(count, true));
        if (_time != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            switch (_card_use) {
                case 0:
                    name.append("[").append(sdf.format(_time)).append("]"); // 使用期限
                    break;
                case 1:
                    //name.append("\\f<[月卡已開啟:" + sdf.format(_time) + " 到期]"); // 使用期限
                    name.append("[").append(sdf.format(_time)).append("]"); // 使用期限
                    break;
                case 2:
                    //name.append("\\f3[月卡已經在:" + sdf.format(_time) + " 到期]"); // 使用期限
                    name.append("[").append(sdf.format(_time)).append("]"); // 使用期限
                    break;
            }
        }
        if (get_hasProtect() != 0) {
            switch (get_hasProtect()) {
                case 1:
                    name.append(" 防爆中(失敗歸零)");
                    break;
                case 2:
                    name.append(" 防爆中(失敗倒扣)");
                    break;
                case 3:
                    name.append(" 防爆中(失敗沒事)");
                    break;
                default:
                    break;
            }
        }
        switch (_item.getUseType()) {
            default:
                if (isEquippedTemp()) {
                    name.append(" ($117)");
                }
                if (ItemVIPTable.get().checkVIP(_itemId) && isEquipped()) {
                    name.append(" ($117)");
                }
                break;
            case -12:
                if (isEquipped()) {
                    name.append(" ($117)");
                }
                break;
            case -4:
                L1Pet pet = PetReading.get().getTemplate(getId());
                if (pet != null) {
                    L1Npc npc = NpcTable.get().getTemplate(pet.get_npcid());
                    name.append("[Lv.").append(pet.get_level()).append("]").append(pet.get_name()).append(" HP").append(pet.get_hp()).append(" ").append(npc.get_nameid());
                }
                break;
            case 1:
                if (isEquipped()) {
                    name.append(" ($9)");
                }
                break;
            case 10:
                if (isNowLighting()) {
                    name.append(" ($10)");
                }
                switch (_item.getItemId()) {
                    case 40001:
                    case 40002:
                        if (getRemainingTime() <= 0) {
                            name.append(" ($11)");
                        }
                        break;
                }
                break;
            case 2:// 盔甲
            case 18:// T恤
            case 19:// 斗篷
            case 20:// 手套
            case 21:// 靴
            case 22:// 頭盔
            case 23:// 戒指
            case 24:// 項鏈
            case 25:// 盾牌
            case 37:// 腰帶
            case 40:// 耳環
            case 43:// 符石 左
            case 44:// 符中 右
            case 45:// 符石 中
            case 70:// 脛甲
            case 48:// 六芒星護身符
            case 49:// 蒂蜜特祝福系列
            case 51: // 蒂蜜特符文
            case 52: // vip
            case 53: // vip2
                if (isEquipped()) {
                    name.append(" ($117)");
                }
                break;
        }
        return name.toString();
    }

    public String getViewName() {
        return getNumberedViewName(_count);
    }

    /**
     * 提供給 Log 顯示用的道具名稱（強化等級 + 名稱 + 數量）
     */
    public String getLogName() {
        return getNumberedName(_count, true); // 不需要再前面加一次昇華標籤了
    }


    /**
     * 取得昇華系統顯示用字串
     *
     * @return 例如： 【魔焰斬 Lv.3】，沒有則回空字串
     */
    public String getSublimationLabel() {
        if (SubItemSet.START && _sublimation != null) {
            String title = _sublimation.get_item_name();
            int lv = _sublimation.getLv();
            return "【" + (title != null ? title : "未命名") + " Lv." + lv + "】";
        }
        return "";
    }

    /**
     * 舊版相容用（兩參數）
     */
    public String getNumberedName(long count, boolean showCount) {
        return getNumberedName(count, true, showCount);
    }

    /**
     * 物件完整名稱取回
     *
     * @param count     數量
     * @param mode      模式（未使用）
     * @param showCount 是否顯示數量
     * @return 完整名稱
     */
    public String getNumberedName(long count, boolean mode, boolean showCount) {
        StringBuilder name = new StringBuilder();

        // 昇華標籤優先插入前面
        name.append(getSublimationLabel());

        // 炫色系統
        L1ItemSpecialAttributeChar attr_char = get_ItemAttrName();
        if (attr_char != null) {
            L1ItemSpecialAttribute attr = ItemSpecialAttributeTable.get().getAttrId(attr_char.get_attr_id());
            name.append(attr.get_colour());
            name.append(attr.get_name()).append(" ");
        }

        if (isIdentified()) {
            int useType = _item.getUseType();
            if (useType == 1) { // 武器
                if (getEnchantLevel() != 0) {
                    name.append(getEnchantLevel() > 0 ? "+" + getEnchantLevel() : getEnchantLevel()).append(" ");
                }

                int attrEnchantLevel = getAttrEnchantLevel();
                if (attrEnchantLevel > 0) {
                    name.append(attrEnchantLevel()).append(" ");
                }

                final L1ItemAttr itemattr = get_ItemAttrName2();
                if (itemattr != null) {
                    switch (itemattr.get_level()) {
                        case 1:
                            name.append(itemattr.get_itemname()).append("之");
                            break;
                        case 2:
                        case 3:
                            name.append(itemattr.get_itemname()).append("之").append(itemattr.get_itemname2());
                            break;
                    }
                }

                if (SubItemSet.START && _sublimation != null) {
                    final String subname = ItemSublimationTable.get().getSubName(_sublimation.getType(), _sublimation.getLv());
                    if (subname != null) {
//                        name.append(subname);
                    }
                }

                if (_gamno != null) {
                    final String[] _skillEnchant = {"嗜血的", "偷竊的", "凍結的", "閃避的", "暈眩的", "擴散的", "嗜魔的", "鬼眼狂刀的", "犬夜叉的",
                            "狂暴的", "傳說之", "寶石之", "四魂之", "木乃伊之", "魔封之", "藥霜之", "汙水之", "殘忍之", "破膽之", "吞滅之",
                            "吞噬之", "異變之", "恐懼之", "光擊之", "幻象之", "繳械之", "失心之", "狂之流星", "狂之雷霆", "狂之光裂"};
                    name.append(_skillEnchant[Integer.parseInt(_gamno) - 1]);
                }
            } else if (
                    useType == 2 || useType == 18 || useType == 19 || useType == 20 || useType == 21 ||
                            useType == 22 || useType == 23 || useType == 24 || useType == 25 || useType == 37 ||
                            useType == 40 || useType == 47 || useType == 48 || useType == 49 || useType == 51 || useType == 70) {

                if (getEnchantLevel() != 0) {
                    name.append(getEnchantLevel() > 0 ? "+" + getEnchantLevel() : getEnchantLevel()).append(" ");
                }

                final L1ItemAttr itemattr1 = get_ItemAttrName2();
                if (itemattr1 != null) {
                    switch (itemattr1.get_level()) {
                        case 1:
                            name.append(itemattr1.get_itemname()).append("之");
                            break;
                        case 2:
                        case 3:
                            name.append(itemattr1.get_itemname()).append("之").append(itemattr1.get_itemname2());
                            break;
                    }
                }
            }
        }

        name.append(_item.getName());

        if (_item.getUseType() == -5) {
            name.append("\\f_[").append(getraceGamNo()).append("]");
        }

        if (isIdentified()) {
            if (getItem().getMaxChargeCount() > 0) {
                name.append(" (").append(getChargeCount()).append(")");
            }

            if (getItem().getItemId() == L1Config._2155) {
                L1Npc npc = NpcTable.get().getTemplate(getGamNpcId());
                name.append("(第").append(getGamNo()).append("場-").append(npc.get_name()).append(")");
            }

            if (getItem().getItemId() == L1Config._2170) {
                name.append("(第").append(getGamNo()).append("期-").append(getStarNpcId()).append(")");
            }

            if (_item.getItemId() == 20383) {
                name.append(" (").append(getChargeCount()).append(")");
            }

            if (_time == null && getItem().getMaxUseTime() > 0) {
                name.append(" [").append(getRemainingTime()).append("]");
            }
        }

        if (showCount && isStackable() && count > 1) {
            name.append(" (").append(count).append(")");
        }

        return name.toString();
    }

    /**
     * 血盟倉庫使用履歷表示名前
     */
    public String getWarehouseHistoryName() {
        StringBuilder name = new StringBuilder();
        if (isIdentified()) {
            if (getItem().getType2() == 1) { // 武器
                int attrEnchantLevel = getAttrEnchantLevel();
                if (attrEnchantLevel > 0) {
                    String attrStr = null;
                    switch (getAttrEnchantKind()) {
                        case 1: // 地
                            if (attrEnchantLevel == 1) {
                                attrStr = "$6124";
                            } else if (attrEnchantLevel == 2) {
                                attrStr = "$6125";
                            } else if (attrEnchantLevel == 3) {
                                attrStr = "$6126";
                            } else if (attrEnchantLevel == 4) {
                                attrStr = "$14364";
                            } else if (attrEnchantLevel == 5) {
                                attrStr = "$14368";
                            }
                            break;
                        case 2: // 火
                            if (attrEnchantLevel == 1) {
                                attrStr = "$6115";
                            } else if (attrEnchantLevel == 2) {
                                attrStr = "$6116";
                            } else if (attrEnchantLevel == 3) {
                                attrStr = "$6117";
                            } else if (attrEnchantLevel == 4) {
                                attrStr = "$14361";
                            } else if (attrEnchantLevel == 5) {
                                attrStr = "$14365";
                            }
                            break;
                        case 4: // 水
                            if (attrEnchantLevel == 1) {
                                attrStr = "$6118";
                            } else if (attrEnchantLevel == 2) {
                                attrStr = "$6119";
                            } else if (attrEnchantLevel == 3) {
                                attrStr = "$6120";
                            } else if (attrEnchantLevel == 4) {
                                attrStr = "$14362";
                            } else if (attrEnchantLevel == 5) {
                                attrStr = "$14366";
                            }
                            break;
                        case 8: // 風
                            if (attrEnchantLevel == 1) {
                                attrStr = "$6121";
                            } else if (attrEnchantLevel == 2) {
                                attrStr = "$6122";
                            } else if (attrEnchantLevel == 3) {
                                attrStr = "$6123";
                            } else if (attrEnchantLevel == 4) {
                                attrStr = "$14363";
                            } else if (attrEnchantLevel == 5) {
                                attrStr = "$14367";
                            }
                            break;
                        default:
                            break;
                    }
                    name.append(attrStr).append(" ");
                }
            }
            if (getItem().getType2() == 1 || getItem().getType2() == 2) { // 武器?防具
                if (getEnchantLevel() >= 0) {
                    name.append("+").append(getEnchantLevel()).append(" ");
                } else if (getEnchantLevel() < 0) {
                    name.append(String.valueOf(getEnchantLevel())).append(" ");
                }
            }
        }
        name.append(_item.getName());
        if (isIdentified()) {
            if (getItem().getMaxChargeCount() > 0) {
                name.append(" (").append(getChargeCount()).append(")");
            }
            if (getItem().getItemId() == 20383) { // 騎馬用
                name.append(" (").append(getChargeCount()).append(")");
            }
        }
        return name.toString();
    }

    public String getInnKeyName() {
        StringBuilder name = new StringBuilder();
        name.append(" #");
        String chatText = String.valueOf(getKeyId());
        StringBuilder s1 = new StringBuilder();
        String s2 = "";
        for (int i = 0; i < chatText.length(); i++) {
            if (i >= 5) {
                break;
            }
            s1.append(String.valueOf(chatText.charAt(i)));
        }
        name.append(s1);
        for (int i = 0; i < chatText.length(); i++) {
            if (i % 2 == 0) {
                s1 = new StringBuilder(String.valueOf(chatText.charAt(i)));
            } else {
                s2 = s1 + String.valueOf(chatText.charAt(i));
                name.append(Integer.toHexString(Integer.parseInt(s2)).toLowerCase());
            }
        }
        return name.toString();
    }

    /**
     * 取回屬性強化階段描述
     *
     */
    private StringBuilder attrEnchantLevel() {
        StringBuilder attrEnchant = new StringBuilder();
        int attrEnchantLevel = getAttrEnchantLevel();
        L1AttrWeapon attrWeapon = ExtraAttrWeaponTable.getInstance().get(getAttrEnchantKind(), attrEnchantLevel);
        if (attrWeapon != null) {
            attrEnchant.append(attrWeapon.getName());
        }
        return attrEnchant;
    }

    /**
     * 取回抗魔值(計算加成變化)
     *
     */
    public int getMr() {
        L1ItemPower itemPower = new L1ItemPower(this);
        return itemPower.getMr();
    }

    /**
     * 取回回魔量(計算加成變化)
     *
     */
    public int getMpr() {
        L1ItemPower itemPower = new L1ItemPower(this);
        return itemPower.getMpr();
    }

    /**
     * 取回魔攻(計算加成變化)
     *
     */
    public int getSp() {
        L1ItemPower itemPower = new L1ItemPower(this);
        return itemPower.getSp();
    }

    /**
     * 取回雙擊率(計算加成變化)
     *
     */
    public int getDoubleDmgChance() {
        L1ItemPower itemPower = new L1ItemPower(this);
        return itemPower.getDoubleDmgChance();
    }

    /**
     * 取回防具增加命中率(計算加成變化)
     *
     */
    public int getHitModifierByArmor() {
        L1ItemPower itemPower = new L1ItemPower(this);
        return itemPower.getHitModifierByArmor();
    }

    /**
     * 取回血量上限(計算加成變化)
     *
     */
    public int get_addhp() {
        L1ItemPower itemPower = new L1ItemPower(this);
        return itemPower.get_addhp();
    }

    /**
     * 取回MP上限(計算加成變化)
     *
     */
    public int get_addmp() {
        L1ItemPower itemPower = new L1ItemPower(this);
        return itemPower.get_addmp();
    }

    /**
     * 取回傷害減免(計算加成變化)
     *
     */
    public int getDamageReduction() {
        L1ItemPower itemPower = new L1ItemPower(this);
        return itemPower.getDamageReduction();
    }

    /**
     * 飾品加成效果
     */
    public void greater(L1PcInstance owner, boolean equipment) {
        L1ItemPower itemPower = new L1ItemPower(this);
        itemPower.greater(owner, equipment);
    }

    /**
     * 飾品加成變化
     *
     */
    public void GreaterAtEnchant(L1PcInstance owner, int i) {
        L1ItemPower itemPower = new L1ItemPower(this);
        itemPower.GreaterAtEnchant(owner, i);
    }

    public int getAcByMagic() {
        return _acByMagic;
    }

    public void setAcByMagic(int i) {
        _acByMagic = i;
    }

    public int getDmgByMagic() {
        return _dmgByMagic;
    }

    public void setDmgByMagic(int i) {
        _dmgByMagic = i;
    }

    public int getHolyDmgByMagic() {
        return _holyDmgByMagic;
    }

    public void setHolyDmgByMagic(int i) {
        _holyDmgByMagic = i;
    }

    public int getHitByMagic() {
        return _hitByMagic;
    }

    public void setHitByMagic(int i) {
        _hitByMagic = i;
    }

    /**
     * [原碼] 怪物對戰系統
     */
    public int getGamNo() {
        return this._gamNo;
    }

    public void setGamNo(int i) {
        this._gamNo = i;
    }

    public int getGamNpcId() {
        return this._gamNpcId;
    }

    public void setGamNpcId(int i) {
        this._gamNpcId = i;
    }

    /**
     * 衝裝贖回系統
     */
    public String getGamNo1() {
        return this._gamNo1;
    }

    /**
     * [原碼] 大樂透系統
     */
    public String getStarNpcId() {
        return this._starNpcId;
    }

    /*
     * public void updateGamno() { this.gamno = getGamNo(); }
     */
    public void setStarNpcId(String i) {
        this._starNpcId = i;
    }

    /**
     * 附在防具上的魔法
     *
     */
    public void setSkillArmorEnchant(L1PcInstance pc, int skillId, int skillTime) {
        int type = getItem().getType();
        int type2 = getItem().getType2();
        if (_isRunning) {
            _timer.cancel();
            int objid = getId();
            if ((pc != null) && (pc.getInventory().getItem(objid) != null) && (type == 2) && (type2 == 2) && (isEquipped())) {
                pc.addAc(3);
                pc.sendPackets(new S_OwnCharStatus(pc));
            }
            setAcByMagic(0);
            _isRunning = false;
            _timer = null;
        }
        if ((type == 2) && (type2 == 2) && (isEquipped())) {
            pc.addAc(-3);
            pc.sendPackets(new S_OwnCharStatus(pc));
        }
        setAcByMagic(3);
        _pc = pc;
        _char_objid = _pc.getId();
        _timer = new EnchantTimer();
        new Timer().schedule(_timer, skillTime);
        _isRunning = true;
    }

    /**
     * 附在武器上的魔法
     *
     */
    public void setSkillWeaponEnchant(L1PcInstance pc, int skillId, int skillTime) {
        if (getItem().getType2() != 1) {
            return;
        }
        if (_isRunning) {
            _timer.cancel();
            setDmgByMagic(0);
            setHolyDmgByMagic(0);
            setHitByMagic(0);
            _isRunning = false;
            _timer = null;
        }
        switch (skillId) {
            //		case HOLY_WEAPON:// 神聖武器
            //			setHolyDmgByMagic(1);
            //			setHitByMagic(1);
            //			break;
            //		case ENCHANT_WEAPON:// 擬似魔法武器
            //			setDmgByMagic(2);
            //			break;
            case BLESS_WEAPON:// 祝福魔法武器
                setDmgByMagic(2);
                setHitByMagic(2);
                break;
            case SHADOW_FANG:// 暗影之牙
                setDmgByMagic(5);
                break;
        }
        _pc = pc;
        _char_objid = _pc.getId();
        _timer = new EnchantTimer();
        new Timer().schedule(_timer, skillTime);
        _isRunning = true;
    }

    public int getItemOwnerId() {
        return _itemOwnerId;
    }

    public void setItemOwnerId(int i) {
        _itemOwnerId = i;
    }

    public void startEquipmentTimer(L1PcInstance pc) {
        if (_time != null) {
            return;
        }
        if (getRemainingTime() > 0) {
            _equipmentTimer = new L1EquipmentTimer(pc, this);
            Timer timer = new Timer(true);
            timer.scheduleAtFixedRate(_equipmentTimer, 1000L, 1000L);
        }
    }

    public void stopEquipmentTimer(L1PcInstance pc) {
        if (_time != null) {
            return;
        }
        if (getRemainingTime() > 0) {
            _equipmentTimer.cancel();
            _equipmentTimer = null;
        }
    }

    public boolean isNowLighting() {
        return _isNowLighting;
    }

    public void setNowLighting(boolean flag) {
        _isNowLighting = flag;
    }

    public boolean isEquippedTemp() {
        return _isEquippedTemp;
    }

    public void set_isEquippedTemp(boolean isEquippedTemp) {
        _isEquippedTemp = isEquippedTemp;
    }

    public void setIsMatch(boolean isMatch) {
        _isMatch = isMatch;
    }

    public boolean isMatch() {
        return _isMatch;
    }

    /**
     * 取回使用者ID
     *
     */
    public int get_char_objid() {
        return _char_objid;
    }

    /**
     * 使用者ID
     *
     */
    public void set_char_objid(int char_objid) {
        _char_objid = char_objid;
    }

    /**
     * 傳回存在期限資料
     *
     */
    public Timestamp get_time() {
        return _time;
    }

    /**
     * 設定存在期限
     *
     */
    public void set_time(Timestamp time) {
        _time = time;
    }

    public boolean isRunning() {
        return _timer != null;
    }

    /**
     * 古文字
     *
     */
    public L1ItemPower_name get_power_name() {
        return _power_name;
    }

    /**
     * 古文字
     *
     */
    public void set_power_name(L1ItemPower_name power_name) {
        _power_name = power_name;
    }

    public L1ItemPower_name get_powerdata() {
        return _powerdata;
    }

    public void set_powerdata(L1ItemPower_name powerdata) {
        _powerdata = powerdata;
    }

    public int get_card_use() {
        return _card_use;
    }

    public void set_card_use(int card_use) {
        _card_use = card_use;
    }

    public String getNumberedName_to_String() {
        StringBuilder name = new StringBuilder();
        // 追加值
        if (getEnchantLevel() >= 0) {
            name.append("+").append(getEnchantLevel()).append(" ");
        } else if (getEnchantLevel() < 0) {
            name.append(String.valueOf(getEnchantLevel())).append(" ");
        }
        switch (_item.getUseType()) {
            case 1:// 武器
                int attrEnchantLevel = getAttrEnchantLevel();
                if (attrEnchantLevel > 0) {
                    L1AttrWeapon attrWeapon = ExtraAttrWeaponTable.getInstance().get(getAttrEnchantKind(), attrEnchantLevel);
                    if (attrWeapon != null) {
                        name.append(attrWeapon.getName()).append(" ");
                    }
                }
                break;

        }
        // 昇華系統
        if (SubItemSet.START) {
            if (_sublimation != null) {
                final String subname = ItemSublimationTable.get().getSubName(_sublimation.getType(), _sublimation.getLv());
                if (subname != null) {
//                    name.append(subname); // 例如「魔焰暴擊」
                }
            }
        }

        if (_gamno != null) {
            final String[] _skillEnchant = {"嗜血的", "偷竊的", "凍結的", "閃避的", "暈眩的", "擴散的", "嗜魔的", "鬼眼狂刀的", "犬夜叉的",
                    "狂暴的", "傳說之", "寶石之", "四魂之", "木乃伊之", "魔封之", "藥霜之", "汙水之", "殘忍之", "破膽之", "吞滅之", "吞噬之", "異變之",
                    "恐懼之", "光擊之", "幻象之", "繳械之", "失心之", "狂之流星", "狂之雷霆", "狂之光裂"};

            name.append(_skillEnchant[(Integer.parseInt(_gamno) - 1)]);
        }
        name.append(_item.getName());
        if (getItem().getMaxChargeCount() > 0) {
            name.append(" (").append(getChargeCount()).append(")");
        } else {
            switch (_item.getItemId()) {
                case 20383:
                    name.append(" (").append(getChargeCount()).append(")");
            }
        }
        long count = getCount();
        if (count > 1L) {
            if (count < 1000000000L) {
                name.append(" (").append(count).append(")");
            } else {
                name.append(" (").append(RangeLong.scount(count)).append(")");
            }
        }
        return name.toString();
    }

    public int getKeyId() {
        return _keyId;
    }

    public void setKeyId(int i) {
        _keyId = i;
    }

    public int getInnNpcId() {
        return _innNpcId;
    }

    public void setInnNpcId(int i) {
        _innNpcId = i;
    }

    public boolean checkRoomOrHall() {
        return _isHall;
    }

    public void setHall(boolean i) {
        _isHall = i;
    }

    public Timestamp getDueTime() {
        return _dueTime;
    }

    public void setDueTime(Timestamp i) {
        _dueTime = i;
    }

    /**
     * 場次代號
     *
     */
    public String getGamNo2() {
        return _gamno;
    }

    /**
     * 設定場次代號
     *
     */
    public void setGamNo2(final String gamno) {
        _gamno = gamno;
    }

    /**
     * 四戒指顯示裝備
     *
     */
    public int getRingID() {
        return _ringId;
    }

    /**
     * 四戒指顯示裝備
     *
     */
    public void setRingID(int ringId) {
        _ringId = ringId;
    }

    /**
     * 雙耳環顯示裝備
     *
     */
    public int getEarRingID() {
        return _earringId;
    }

    /**
     * 雙耳環顯示裝備
     *
     */
    public void setEarRingID(int earringId) {
        _earringId = earringId;
    }

    public boolean getproctect() {
        return proctect;
    }

    public void setproctect(boolean i) {
        proctect = i;
    }

    public int getItemStatusX() {
        if (!isIdentified()) {
            return 0;
        }
        int statusX = 1;
        if (!getItem().isTradable()) {
            statusX |= 2; // 無法交易
        }
        if (getItem().isCantDelete()) {
            statusX |= 4; // 無法刪除
        }
        if (getItem().get_safeenchant() < 0) {
            statusX |= 8; // 無法強化
        }
        if (ItemRestrictionsTable.RESTRICTIONS.contains(getItemId())) {
            statusX |= 16; // 倉庫保管功能
        }
        final int bless = getBless();
        if (bless >= 128 && bless <= 131) {
            statusX |= 2; // 無法交易
            statusX |= 4; // 無法刪除
            statusX |= 8; // 無法強化
            statusX |= 32; // 封印狀態
        } else if (bless > 131) {
            statusX |= 64; // 特殊封印狀態
        }
        if (getItem().isStackable()) {
            statusX |= 128; // 可以堆疊
        }
        return statusX;
    }

    public final int get_hasProtect() {
        return this._hasProtect;
    }

    public final void set_hasProtect(int value) {
        this._hasProtect = value;
    }

    public byte getEqId() {
        return _eqId;
    }

    public void setEqId(final byte i) {
        _eqId = i;
    }

    public L1ItemPower_bless get_power_bless() {
        return _power_bless;
    }

    public void set_power_bless(L1ItemPower_bless power_bless) {
        _power_bless = power_bless;
    }

    /**
     * 物品詳細資料
     */
    public byte[] getStatusBytes() {
        final L1ItemStatus itemInfo = new L1ItemStatus(this);
        return itemInfo.getStatusBytes(false).getBytes();
    }

    /**
     * 日誌詳細資料
     */
    public final String getRecordName(long count) {
        StringBuilder name = new StringBuilder();
        int attrEnchantLevel = getAttrEnchantLevel();
        if (attrEnchantLevel > 0) {
            L1AttrWeapon attrWeapon = ExtraAttrWeaponTable.getInstance().get(getAttrEnchantKind(), attrEnchantLevel);
            if (attrWeapon != null) {
                name.append(attrWeapon.getName());
            }
        }
        if (getEnchantLevel() >= 0) {
            name.append("+").append(getEnchantLevel()).append(" ");
        } else if (getEnchantLevel() < 0) {
            name.append(getEnchantLevel()).append(" ");
        }
        name.append(this._item.getName());
        if (getChargeCount() > 0) {
            name.append(" (可用次數:").append(getChargeCount()).append(")");
        }
        if (this._time != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            name.append("[").append(sdf.format(this._time)).append("]");
        }
        if (this._power_name != null) {
            name.append("(鍛造:").append(this._power_name.get_power_name()).append(")");
        }
        if (this._power_name != null) {
            L1MagicWeapon magic_weapon = this._power_name.get_magic_weapon();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            if (magic_weapon != null) {
                name.append(" (武器附魔 : ").append(magic_weapon.getSkillName()).append(", 時間: ");
                if (this._power_name.get_date_time() != null) {
                    name.append(sdf.format(this._power_name.get_date_time()));
                } else {
                    name.append("永久");
                }
                name.append(")");
            }
            L1BossWeapon boss_weapon = this._power_name.get_boss_weapon();
            if (boss_weapon != null) {
                name.append(" (boss武器 : ").append(boss_weapon.getBossName()).append(",時間: ");
                if (this._power_name.get_boss_date_time() != null) {
                    name.append(sdf.format(this._power_name.get_boss_date_time()));
                } else {
                    name.append("永久");
                }
                name.append(")");
            }
        }
        if (this._power_bless != null) {
            name.append("古文字:");
            if (this._power_bless.get_hole_str() != 0) {
                name.append("強化力量+").append(this._power_bless.get_hole_str()).append(" ");
            } else if (this._power_bless.get_hole_str() < 0) {
                name.append(this._power_bless.get_hole_str()).append(" ");
            }
            if (this._power_bless.get_hole_dex() != 0) {
                name.append("強化敏捷+").append(this._power_bless.get_hole_dex()).append(" ");
            } else if (this._power_bless.get_hole_dex() < 0) {
                name.append(this._power_bless.get_hole_dex()).append(" ");
            }
            if (this._power_bless.get_hole_int() != 0) {
                name.append("強化智力+").append(this._power_bless.get_hole_int()).append(" ");
            } else if (this._power_bless.get_hole_int() < 0) {
                name.append(this._power_bless.get_hole_int()).append(" ");
            }
            if (this._power_bless.get_hole_dmg() != 0) {
                name.append("強化近戰+").append(this._power_bless.get_hole_dmg()).append(" ");
            } else if (this._power_bless.get_hole_dmg() < 0) {
                name.append(this._power_bless.get_hole_dmg()).append(" ");
            }
            if (this._power_bless.get_hole_bowdmg() != 0) {
                name.append("強化遠攻+").append(this._power_bless.get_hole_bowdmg()).append(" ");
            } else if (this._power_bless.get_hole_bowdmg() < 0) {
                name.append(this._power_bless.get_hole_bowdmg()).append(" ");
            }
            if (this._power_bless.get_hole_mcdmg() != 0) {
                name.append("強化魔傷+").append(this._power_bless.get_hole_mcdmg()).append(" ");
            } else if (this._power_bless.get_hole_mcdmg() < 0) {
                name.append(this._power_bless.get_hole_mcdmg()).append(" ");
            }
            for (int i = 0; i < _power_bless.get_hole_count(); i++) { // SRC0712
                switch (i) {
                    case 0:
                        name.append(" (洞1: ").append(_power_bless.get_hole_1());
                        break;
                    case 1:
                        name.append(" (洞2: ").append(_power_bless.get_hole_2());
                        break;
                    case 2:
                        name.append(" (洞3: ").append(_power_bless.get_hole_3());
                        break;
                    case 3:
                        name.append(" (洞4: ").append(_power_bless.get_hole_4());
                        break;
                    case 4:
                        name.append(" (洞5: ").append(_power_bless.get_hole_5());
                        break;
                }
            }
        }
        if (count > 1L) {
            if (count < 1000000000L) {
                name.append(" (").append(count).append(")");
            } else {
                name.append(" (").append(RangeLong.scount(count)).append(")");
            }
        }
        return name.toString();
    }

    /**
     * 強化擴充能力 - 力量
     */
    public int getUpdateStr() {
        return _updateStr;
    }

    public void setUpdateStr(int i) {
        _updateStr = i;
    }

    /**
     * 強化擴充能力 - 敏捷
     */
    public int getUpdateDex() {
        return _updateDex;
    }

    public void setUpdateDex(int i) {
        _updateDex = i;
    }

    /**
     * 強化擴充能力 - 體質
     */
    public int getUpdateCon() {
        return _updateCon;
    }

    public void setUpdateCon(int i) {
        _updateCon = i;
    }

    /**
     * 強化擴充能力 - 精神
     */
    public int getUpdateWis() {
        return _updateWis;
    }

    public void setUpdateWis(int i) {
        _updateWis = i;
    }

    /**
     * 強化擴充能力 - 智力
     */
    public int getUpdateInt() {
        return _updateInt;
    }

    public void setUpdateInt(int i) {
        _updateInt = i;
    }

    /**
     * 強化擴充能力 - 魅力
     */
    public int getUpdateCha() {
        return _updateCha;
    }

    public void setUpdateCha(int i) {
        _updateCha = i;
    }

    /**
     * 強化擴充能力 - 血量
     */
    public int getUpdateHp() {
        return _updateHp;
    }

    public void setUpdateHp(int i) {
        _updateHp = i;
    }

    /**
     * 強化擴充能力 - 魔量
     */
    public int getUpdateMp() {
        return _updateMp;
    }

    public void setUpdateMp(int i) {
        _updateMp = i;
    }

    /**
     * 強化擴充能力 - 地屬性
     */
    public int getUpdateEarth() {
        return _updateEarth;
    }

    public void setUpdateEarth(int i) {
        _updateEarth = i;
    }

    /**
     * 強化擴充能力 - 風屬性
     */
    public int getUpdateWind() {
        return _updateWind;
    }

    public void setUpdateWind(int i) {
        _updateWind = i;
    }

    /**
     * 強化擴充能力 - 水屬性
     */
    public int getUpdateWater() {
        return _updateWater;
    }

    public void setUpdateWater(int i) {
        _updateWater = i;
    }

    /**
     * 強化擴充能力 - 火屬性
     */
    public int getUpdateFire() {
        return _updateFire;
    }

    public void setUpdateFire(int i) {
        _updateFire = i;
    }

    /**
     * 強化擴充能力 - 抗魔
     */
    public int getUpdateMr() {
        return _updateMr;
    }

    public void setUpdateMr(int i) {
        _updateMr = i;
    }

    /**
     * 強化擴充能力 - 防禦
     */
    public int getUpdateAc() {
        return _updateAc;
    }

    public void setUpdateAc(int i) {
        _updateAc = i;
    }

    /**
     * 強化擴充能力 - 回血
     */
    public int getUpdateHpr() {
        return _updateHpr;
    }

    public void setUpdateHpr(int i) {
        _updateHpr = i;
    }

    /**
     * 強化擴充能力 - 回魔
     */
    public int getUpdateMpr() {
        return _updateMpr;
    }

    public void setUpdateMpr(int i) {
        _updateMpr = i;
    }

    /**
     * 強化擴充能力 - 魔法攻擊
     */
    public int getUpdateSp() {
        return _updateSp;
    }

    public void setUpdateSp(int i) {
        _updateSp = i;
    }

    /**
     * 強化擴充能力 - 近戰攻擊
     */
    public int getUpdateDmgModifier() {
        return _updateDmgModifier;
    }

    public void setUpdateDmgModifier(int i) {
        _updateDmgModifier = i;
    }

    /**
     * 強化擴充能力 - 近戰命中
     */
    public int getUpdateHitModifier() {
        return _updateHitModifier;
    }

    public void setUpdateHitModifier(int i) {
        _updateHitModifier = i;
    }
    // 強化擴充能力 end

    /**
     * 強化擴充能力 - 遠攻攻擊
     */
    public int getUpdateBowDmgModifier() {
        return _updateBowDmgModifier;
    }

    public void setUpdateBowDmgModifier(int i) {
        _updateBowDmgModifier = i;
    }

    /**
     * 強化擴充能力 - 遠攻命中
     */
    public int getUpdateBowHitModifier() {
        return _updateBowHitModifier;
    }

    public void setUpdateBowHitModifier(int i) {
        _updateBowHitModifier = i;
    }

    /**
     * 強化擴充能力 - pvp攻擊
     */
    public int getUpdatePVPdmg() {
        return _updatePVPdmg;
    }

    public void setUpdatePVPdmg(int i) {
        _updatePVPdmg = i;
    }

    /**
     * 強化擴充能力 - pvp減免
     */
    public int getUpdatePVPdmg_R() {
        return _updatePVPdmg_R;
    }

    public void setUpdatePVPdmg_R(int i) {
        _updatePVPdmg_R = i;
    }

    /**
     * 強化擴充能力 - 武器劍靈值
     */
    public int getUpdateWeaponSoul() {
        return _updateWeaponSoul;
    }

    /**
     * 強化擴充能力 - 武器劍靈值
     */
    public void setUpdateWeaponSoul(int i) {
        _updateWeaponSoul = i;
    }

    /**
     * 傳回人物物品特殊屬性資料表
     *
     */
    public L1ItemSpecialAttributeChar get_ItemAttrName() {
        return _ItemAttrName2;
    }

    public void set_ItemAttrName(L1ItemSpecialAttributeChar ItemAttrName) {
        _ItemAttrName2 = ItemAttrName;
    }

    public L1CharaterTrade getItemCharaterTrade() {
        return _itemCharaterTrade;
    }

    public void setItemCharaterTrade(final L1CharaterTrade charaterTrade) {
        _itemCharaterTrade = charaterTrade;
    }

    public int get_isColorWashout() {
        return this._is_isColorWashout;
    }

    public void set_isColorWashout(final int _isColorWashout) {
        this._is_isColorWashout = _isColorWashout;
    }

    public L1ItemAttr get_ItemAttrName2() {
        return _ItemAttrName;
    }

    public CharItemSublimation getSublimation() {
        return _sublimation;
    }

    public void setSublimation(final CharItemSublimation sublimation) {
        _sublimation = sublimation;
    }

    public String getAllName() {
        return getNumberedName(this._count, true);
    }

    // 對應 Getter / Setter
    public int getDmgChanceHp() {
        return _dmgChanceHp;
    }

    public void setDmgChanceHp(int dmgChanceHp) {
        this._dmgChanceHp = dmgChanceHp;
    }

    public int getDmgChanceMp() {
        return _dmgChanceMp;
    }

    public void setDmgChanceMp(int dmgChanceMp) {
        this._dmgChanceMp = dmgChanceMp;
    }

    public boolean isWithstandDmg() {
        return _withstandDmg;
    }

    public void setWithstandDmg(boolean withstandDmg) {
        this._withstandDmg = withstandDmg;
    }

    public boolean isWithstandMagic() {
        return _withstandMagic;
    }

    public void setWithstandMagic(boolean withstandMagic) {
        this._withstandMagic = withstandMagic;
    }

    public boolean isReturnDmg() {
        return _returnDmg;
    }

    public void setReturnDmg(boolean returnDmg) {
        this._returnDmg = returnDmg;
    }

    public boolean isReturnMagic() {
        return _returnMagic;
    }

    public void setReturnMagic(boolean returnMagic) {
        this._returnMagic = returnMagic;
    }

    public boolean isReturnSkills() {
        return _returnSkills;
    }

    public void setReturnSkills(boolean returnSkills) {
        this._returnSkills = returnSkills;
    }

    public int getReturnChanceHp() {
        return _returnChanceHp;
    }

    public void setReturnChanceHp(int returnChanceHp) {
        this._returnChanceHp = returnChanceHp;
    }

    public int getReturnChanceMp() {
        return _returnChanceMp;
    }

    public void setReturnChanceMp(int returnChanceMp) {
        this._returnChanceMp = returnChanceMp;
    }

    class EnchantTimer extends TimerTask {
        public EnchantTimer() {
        }

        public void run() {
            try {
                int type = getItem().getType();
                int type2 = getItem().getType2();
                int objid = getId();
                if ((_pc != null) && (_pc.getInventory().getItem(objid) != null) && (type == 2) && (type2 == 2) && (isEquipped())) {
                    _pc.addAc(3);
                    _pc.sendPackets(new S_OwnCharStatus(_pc));
                }
                setAcByMagic(0);
                setDmgByMagic(0);
                setHolyDmgByMagic(0);
                setHitByMagic(0);
                _pc.sendPackets(new S_ServerMessage(308, getLogName()));
                _isRunning = false;
                _timer = null;
            } catch (Exception e) {
                L1ItemInstance._log.warn("EnchantTimer: " + getItemId());
            }
        }
    }

    public class LastStatus {
        public long count;
        public int itemId;
        public boolean isEquipped = false;
        public int enchantLevel;
        public boolean isIdentified = true;
        public int durability;
        public int chargeCount;
        public int remainingTime;
        public Timestamp lastUsed = null;
        public int bless;
        public int attrEnchantKind;
        public int attrEnchantLevel;
        public int ItemAttack;
        public int ItemBowAttack;
        public int ItemReductionDmg;
        public int ItemSp;
        public int Itemprobability;
        public int ItemStr;
        public int ItemDex;
        public int ItemInt;
        public int ItemHp;
        public int ItemMp;
        public int ItemCon;
        public int ItemCha;
        public int ItemWis;
        /**
         * [原碼] 怪物對戰系統
         */
        public int gamNo;
        public int gamNpcId;
        /**
         * [原碼] 大樂透系統
         */
        public String starNpcId;

        public void updateAll() {
            count = getCount();
            itemId = getItemId();
            isEquipped = isEquipped();
            isIdentified = isIdentified();
            enchantLevel = getEnchantLevel();
            durability = get_durability();
            chargeCount = getChargeCount();
            remainingTime = getRemainingTime();
            lastUsed = getLastUsed();
            bless = getBless();
            attrEnchantKind = getAttrEnchantKind();
            attrEnchantLevel = getAttrEnchantLevel();
            ItemAttack = getItemAttack();
            ItemBowAttack = getItemBowAttack();
            ItemReductionDmg = getItemReductionDmg();
            ItemSp = getItemSp();
            Itemprobability = getItemprobability();
            ItemStr = getItemStr();
            ItemDex = getItemDex();
            ItemInt = getItemInt();
            ItemHp = getItemHp();
            ItemMp = getItemMp();
            ItemCon = getItemCon();
            ItemCha = getItemCha();
            ItemWis = getItemWis();
            /** [原碼] 怪物對戰系統 */
            this.gamNo = L1ItemInstance.this.getGamNo();
            this.gamNpcId = L1ItemInstance.this.getGamNpcId();
            /** [原碼] 大樂透系統 */
            this.starNpcId = L1ItemInstance.this.getStarNpcId();
        }

        /**
         * [原碼] 怪物對戰系統
         */
        public void updateGamNo() {
            this.gamNo = L1ItemInstance.this.getGamNo();
        }

        public void updateGamNpcId() {
            this.gamNpcId = L1ItemInstance.this.getGamNpcId();
        }

        /**
         * [原碼] 大樂透系統
         */
        public void updateStarNpcId() {
            this.starNpcId = L1ItemInstance.this.getStarNpcId();
        }

        public void updateCount() {
            count = getCount();
        }

        public void updateItemId() {
            itemId = getItemId();
        }

        public void updateEquipped() {
            isEquipped = isEquipped();
        }

        public void updateIdentified() {
            isIdentified = isIdentified();
        }

        public void updateEnchantLevel() {
            enchantLevel = getEnchantLevel();
        }

        public void updateDuraility() {
            durability = get_durability();
        }

        public void updateChargeCount() {
            chargeCount = getChargeCount();
        }

        public void updateRemainingTime() {
            remainingTime = getRemainingTime();
        }

        public void updateLastUsed() {
            lastUsed = getLastUsed();
        }

        public void updateBless() {
            bless = getBless();
        }

        public void updateAttrEnchantKind() {
            attrEnchantKind = getAttrEnchantKind();
        }

        public void updateAttrEnchantLevel() {
            attrEnchantLevel = getAttrEnchantLevel();
        }

        public void updateItemAttack() {
            ItemAttack = getItemAttack();
        }

        public void updateItemBowAttack() {
            ItemBowAttack = getItemBowAttack();
        }

        public void updateItemReductionDmg() {
            ItemReductionDmg = getItemReductionDmg();
        }

        public void updateItemSp() {
            ItemSp = getItemSp();
        }

        public void updateItemprobability() {
            Itemprobability = getItemprobability();
        }

        public void updateItemStr() {
            ItemStr = getItemStr();
        }

        public void updateItemDex() {
            ItemDex = getItemDex();
        }

        public void updateItemInt() {
            ItemInt = getItemInt();
        }

        public void updateItemHp() {
            ItemInt = getItemHp();
        }

        public void updateItemMp() {
            ItemInt = getItemMp();
        }

        public void updateItemCon() {
            ItemCon = getItemCon();
        }

        public void updateItemCha() {
            ItemCha = getItemCha();
        }

        public void updateItemWis() {
            ItemWis = getItemWis();
        }
    }
}