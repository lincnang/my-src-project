package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.data.ItemClass;
import com.lineage.data.item_armor.set.ArmorSet;
import com.lineage.server.IdFactory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.templates.L1Armor;
import com.lineage.server.templates.L1EtcItem;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1Weapon;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ItemTable {
    public static final List<Integer> itembuff = new ArrayList<>();
    public static final List<String> itembuffs = new ArrayList<>();
    private static final Log _log = LogFactory.getLog(ItemTable.class);
    // 防具類型核心分類
    private static final Map<String, Integer> _armorTypes = new HashMap<>();
    // 武器類型核心分類
    private static final Map<String, Integer> _weaponTypes = new HashMap<>();
    // 武器類型觸發事件
    private static final Map<String, Integer> _weaponId = new HashMap<>();
    // 材質類型核心分類
    private static final Map<String, Integer> _materialTypes = new HashMap<>();
    // 道具類型核心分類
    private static final Map<String, Integer> _etcItemTypes = new HashMap<>();
    // 道具類型觸發事件
    private static final Map<String, Integer> _useTypes = new HashMap<>();
    public static Map<Integer, L1EtcItem> _etcitems;
    public static Map<Integer, L1Armor> _armors;
    public static Map<Integer, L1Weapon> _weapons;
    private static ItemTable _instance;
    private static int _cdescid = 20000;

    static {
        // 物品類型
        _etcItemTypes.put("arrow", 0);// 箭
        _etcItemTypes.put("wand", 1);// 魔杖
        _etcItemTypes.put("light", 2);// 照明
        _etcItemTypes.put("gem", 3);// 寶石
        _etcItemTypes.put("totem", 4);// 圖騰
        _etcItemTypes.put("firecracker", 5);// 煙火
        _etcItemTypes.put("potion", 6);// 藥水
        _etcItemTypes.put("food", 7);// 食物
        _etcItemTypes.put("scroll", 8);// 卷軸
        _etcItemTypes.put("questitem", 9);// 任務物品
        _etcItemTypes.put("spellbook", 10);// 魔法書
        _etcItemTypes.put("petitem", 11);// 寵物物品
        _etcItemTypes.put("other", 12);// 其他
        _etcItemTypes.put("material", 13);// 材料
        _etcItemTypes.put("event", 14);// 活動物品
        _etcItemTypes.put("sting", 15);// 飛刀
        _etcItemTypes.put("treasure_box", 16);// 寶盒
        _etcItemTypes.put("doll_card", 17);// 娃娃卡
        _etcItemTypes.put("poly_card", 18);// 變身卡
        _etcItemTypes.put("holy_card", 19);// 聖物
        // 物品使用封包類型(編號由S_AddItem測出)
        _useTypes.put("petitem", -12); // 寵物用具
        _useTypes.put("other", -11); // 對讀取方法調用無法分類的物品
        _useTypes.put("power", -10); // 加速藥水
        _useTypes.put("book", -9); // 技術書
        _useTypes.put("makecooking", -8);// 料理書
        _useTypes.put("hpr", -7);// 增HP道具
        _useTypes.put("mpr", -6);// 增MP道具
        _useTypes.put("ticket", -5); // 食人妖精競賽票/死亡競賽票/彩票
        _useTypes.put("petcollar", -4); // 項圈
        _useTypes.put("sting", -3); // 飛刀
        _useTypes.put("arrow", -2); // 箭
        _useTypes.put("none", -1); // 無法使用(材料等)
        _useTypes.put("normal", 0);// 一般物品
        _useTypes.put("weapon", 1);// 武器
        _useTypes.put("armor", 2);// 盔甲
        _useTypes.put("spell_1", 3); // 創造怪物魔杖(無須選取目標 -
        // 無數量:沒有任何事情發生)
        _useTypes.put("4", 4); // 希望魔杖 XXX
        _useTypes.put("spell_long", 5); // 魔杖類型(須選取目標/座標)
        _useTypes.put("ntele", 6);// 瞬間移動卷軸
        _useTypes.put("identify", 7);// 鑒定卷軸
        _useTypes.put("res", 8);// 復活卷軸
        _useTypes.put("home", 9); // 傳送回家的卷軸
        _useTypes.put("light", 10); // 照明道具
        _useTypes.put("letter", 12);// 信紙
        _useTypes.put("letter_card", 13); // 信紙(寄出)
        _useTypes.put("choice", 14);// 請選擇一個物品(道具欄位)
        _useTypes.put("instrument", 15);// 哨子
        _useTypes.put("sosc", 16);// 變形卷軸
        _useTypes.put("spell_short", 17); // 選取目標 (近距離)
        _useTypes.put("T", 18);// T恤
        _useTypes.put("cloak", 19);// 斗篷
        _useTypes.put("glove", 20); // 手套
        _useTypes.put("boots", 21);// 靴
        _useTypes.put("helm", 22);// 頭盔
        _useTypes.put("ring", 23);// 戒指
        _useTypes.put("amulet", 24);// 項鏈
        _useTypes.put("shield", 25);// 盾牌
        _useTypes.put("guarder", 25);// 臂甲
        _useTypes.put("dai", 26);// 對武器施法的卷軸
        _useTypes.put("zel", 27);// 對盔甲施法的卷軸
        _useTypes.put("blank", 28);// 空的魔法卷軸
        _useTypes.put("btele", 29);// 瞬間移動卷軸(祝福)
        _useTypes.put("spell_buff", 30); // 魔法卷軸選取目標 (遠距離 無XY座標傳回)
        _useTypes.put("ccard", 31);// 聖誕卡片
        _useTypes.put("ccard_w", 32);// 聖誕卡片(寄出)
        _useTypes.put("vcard", 33);// 情人節卡片
        _useTypes.put("vcard_w", 34);// 情人節卡片(寄出)
        _useTypes.put("wcard", 35);// 白色情人節卡片
        _useTypes.put("wcard_w", 36);// 白色情人節卡片(寄出)
        _useTypes.put("belt", 37);// 腰帶
        _useTypes.put("food", 38); // 食物
        _useTypes.put("spell_long2", 39); // 選取目標 (遠距離)
        _useTypes.put("earring", 40); // 耳環
        _useTypes.put("fishing_rod", 42);// 釣魚桿
        _useTypes.put("talisman", 43); // 符石/遺物 上左(鎖定)
        _useTypes.put("runeword_left", 44); // 符石/遺物 下左
        _useTypes.put("runeword_right", 45); // 符石/遺物 下右
        _useTypes.put("enc", 46); // 飾品強化卷軸
        _useTypes.put("pants", 70); // 脛甲
        _useTypes.put("runeword_middle", 48); // 六芒星護身符 下中
        _useTypes.put("talisman2", 49); // 蒂蜜特的紋樣系列 上右
        _useTypes.put("talisman3", 51); // 蒂蜜特的符文
        _useTypes.put("vip", 52); // vip
        _useTypes.put("choice_doll", 55);// 請選擇魔法娃娃
        _useTypes.put("lottery", 62);//抽抽樂
        _useTypes.put("lottery2", 65);//抽抽樂
        // 防具類型
        _armorTypes.put("none", 0);
        _armorTypes.put("helm", 1);// 頭盔
        _armorTypes.put("armor", 2);// 盔甲
        _armorTypes.put("T", 3);// 內衣
        _armorTypes.put("cloak", 4);// 斗篷
        _armorTypes.put("glove", 5);// 手套
        _armorTypes.put("boots", 6);// 長靴
        _armorTypes.put("shield", 7);// 盾牌
        _armorTypes.put("amulet", 8);// 項鏈
        _armorTypes.put("ring", 9);// 戒指
        _armorTypes.put("belt", 10);// 腰帶
        _armorTypes.put("ring2", 11);// 戒指2
        _armorTypes.put("earring", 12);// 耳環
        _armorTypes.put("guarder", 13);// 臂甲
        _armorTypes.put("talisman", 14);// 符石  上左(鎖定)
        _armorTypes.put("runeword_left", 15);// 符石  下左
        _armorTypes.put("runeword_right", 17);// 符石   下右
        _armorTypes.put("pants", 16);// 脛甲
        _armorTypes.put("runeword_middle", 18);// 六芒星護身符  下中
        _armorTypes.put("talisman2", 23);// 蒂蜜特的紋樣系列 //2017/04/21 上右
        _armorTypes.put("talisman3", 20);// 蒂蜜特的符文
        _armorTypes.put("vip", 21);// vip
        // 武器類型
        _weaponTypes.put("none", 0);// 空手
        _weaponTypes.put("sword", 1);// 劍(單手)
        _weaponTypes.put("dagger", 2);// 匕首(單手)
        _weaponTypes.put("tohandsword", 3);// 雙手劍(雙手)
        _weaponTypes.put("bow", 4);// 弓(雙手)
        _weaponTypes.put("spear", 5);// 矛(雙手)
        _weaponTypes.put("blunt", 6);// 斧(單手)
        _weaponTypes.put("staff", 7);// 魔杖(單手)
        _weaponTypes.put("throwingknife", 8);// 飛刀
        _weaponTypes.put("arrow", 9);// 箭
        _weaponTypes.put("gauntlet", 10);// 鐵手甲
        _weaponTypes.put("claw", 11);// 鋼爪(雙手)
        _weaponTypes.put("edoryu", 12);// 雙刀(雙手)
        _weaponTypes.put("singlebow", 13);// 弓(單手)
        _weaponTypes.put("singlespear", 14);// 矛(單手)
        _weaponTypes.put("tohandblunt", 15);// 雙手斧(雙手)
        _weaponTypes.put("tohandstaff", 16);// 魔杖(雙手)
        _weaponTypes.put("kiringku", 17);// 奇古獸(單手)
        _weaponTypes.put("chainsword", 18);// 鎖鏈劍(單手)
        _weaponId.put("sword", 4);// 劍
        _weaponId.put("dagger", 46);// 匕首
        _weaponId.put("tohandsword", 50);// 雙手劍
        _weaponId.put("bow", 20);// 弓
        _weaponId.put("blunt", 11);// 斧(單手)
        _weaponId.put("spear", 24);// 矛(雙手)
        _weaponId.put("staff", 40);// 魔杖
        _weaponId.put("throwingknife", 2922);// 飛刀
        _weaponId.put("arrow", 66);// 箭
        _weaponId.put("gauntlet", 62);// 鐵手甲
        _weaponId.put("claw", 58);// 鋼爪
        _weaponId.put("edoryu", 54);// 雙刀
        _weaponId.put("singlebow", 20);// 弓(單手)
        _weaponId.put("singlespear", 24);// 矛(單手)
        _weaponId.put("tohandblunt", 11);// 雙手斧
        _weaponId.put("tohandstaff", 40);// 魔杖(雙手)
        _weaponId.put("kiringku", 58);// 奇古獸
        _weaponId.put("chainsword", 24);// 鎖鏈劍
        // 材質
        _materialTypes.put("none", 0);// 無
        _materialTypes.put("liquid", 1);// 憶體
        _materialTypes.put("web", 2);// 蠟
        _materialTypes.put("vegetation", 3);// 植物
        _materialTypes.put("animalmatter", 4);// 動物
        _materialTypes.put("paper", 5);// 紙
        _materialTypes.put("cloth", 6);// 布
        _materialTypes.put("leather", 7);// 皮革
        _materialTypes.put("wood", 8);// 木
        _materialTypes.put("bone", 9);// 骨頭
        _materialTypes.put("dragonscale", 10);// 龍鱗
        _materialTypes.put("iron", 11);// 鐵
        _materialTypes.put("steel", 12);// 鋼
        _materialTypes.put("copper", 13);// 銅
        _materialTypes.put("silver", 14);// 銀
        _materialTypes.put("gold", 15);// 黃金
        _materialTypes.put("platinum", 16);// 白金
        _materialTypes.put("mithril", 17);// 米索莉
        _materialTypes.put("blackmithril", 18);// 黑色米索莉
        _materialTypes.put("glass", 19);// 玻璃
        _materialTypes.put("gemstone", 20);// 寶石
        _materialTypes.put("mineral", 21);// 礦物
        _materialTypes.put("oriharukon", 22);// 奧裡哈魯根
    }

    private L1Item _allTemplates[];

    public static Map<String, Integer> ArmorTypes() {
        return _armorTypes;
    }

    public static Map<String, Integer> WeaponTypes() {
        return _weaponTypes;
    }

    public static synchronized int cdescid() {
        return _cdescid++;
    }

    public static ItemTable get() {
        if (_instance == null) {
            _instance = new ItemTable();
        }
        return _instance;
    }

    public static void init() {
        _instance = new ItemTable();
    }

    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        _etcitems = this.allEtcItem();
        _weapons = this.allWeapon();
        _armors = this.allArmor();
        this.buildFastLookupTable();
        _log.info("讀取->道具,武器,防具資料: " + _etcitems.size() + "+" + _weapons.size() + "+" + _armors.size() + "=" + (_etcitems.size() + _weapons.size() + _armors.size()) + "(" + timer.get() + "ms)");
    }

    public void loadarmors() {
        PerformanceTimer timer = new PerformanceTimer();
        _armors = allArmor();
        this.buildFastLookupTable();
        _log.info("讀取->防具資料: " + _armors.size() + "=" + "(" + timer.get() + "ms)");
    }

    public void loadweapons() {
        PerformanceTimer timer = new PerformanceTimer();
        _weapons = allWeapon();
        this.buildFastLookupTable();
        _log.info("讀取->武器資料: " + _weapons.size() + "=" + "(" + timer.get() + "ms)");
    }

    public void loaditems() {
        PerformanceTimer timer = new PerformanceTimer();
        _etcitems = allEtcItem();
        this.buildFastLookupTable();
        _log.info("讀取->道具資料: " + _etcitems.size() + "=" + "(" + timer.get() + "ms)");
    }

    private Map<Integer, L1EtcItem> allEtcItem() {
        Map<Integer, L1EtcItem> result = new HashMap<>();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        L1EtcItem item = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `etcitem`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                item = new L1EtcItem();
                int itemid = rs.getInt("item_id");
                item.setItemId(itemid);
                item.setName(rs.getString("name"));
                final String classname = rs.getString("classname");
                if (classname.startsWith("ItemBuff")) {
                    itembuff.add(itemid);
                    itembuffs.add(classname);
                }
                item.setClassname(classname);
                item.setNameId(rs.getString("name_id"));
                item.setType((Integer) _etcItemTypes.get(rs.getString("item_type")));
                item.setUseType((Integer) _useTypes.get(rs.getString("use_type")));
                item.setType2(0);
                item.setMaterial((Integer) _materialTypes.get(rs.getString("material")));
                item.setWeight(rs.getInt("weight"));
                item.setGfxId(rs.getInt("invgfx"));
                item.setGroundGfxId(rs.getInt("grdgfx"));
                int itemDescId = rs.getInt("itemdesc_id");
                itemDescId = itemDescId <= 0 ? cdescid() : itemDescId;
                item.setItemDescId(itemDescId);
                item.setMinLevel(rs.getInt("min_lvl"));
                item.setMaxLevel(rs.getInt("max_lvl"));
                item.setBless(rs.getInt("bless"));
                item.setTradable(rs.getInt("trade") == 0);
                item.setCantDelete(rs.getInt("cant_delete") == 1);
                item.setDmgSmall(rs.getInt("dmg_small"));
                item.setDmgLarge(rs.getInt("dmg_large"));
                item.set_stackable(rs.getInt("stackable") == 1);
                item.setMaxChargeCount(rs.getInt("max_charge_count"));
                item.setMaxUseTime(rs.getInt("max_use_time"));
                item.set_delayid(rs.getInt("delay_id"));
                item.set_delaytime(rs.getInt("delay_time"));
                item.set_delayEffect(rs.getInt("delay_effect"));
                item.setFoodVolume(rs.getInt("food_volume"));
                item.setToBeSavedAtOnce(rs.getBoolean("save_at_once"));
                // 職業使用判斷欄位 (1王族.2騎士.4妖精.8法師.16黑妖.32龍騎.64幻術.128戰士.255共用)
                final int use_career = rs.getInt("use_career");
                item.setUseRoyal((use_career & 1) == 1 ? true : false);
                item.setUseKnight((use_career & 2) == 2 ? true : false);
                item.setUseElf((use_career & 4) == 4 ? true : false);
                item.setUseMage((use_career & 8) == 8 ? true : false);
                item.setUseDarkelf((use_career & 16) == 16 ? true : false);
                item.setUseDragonknight((use_career & 32) == 32 ? true : false);
                item.setUseIllusionist((use_career & 64) == 64 ? true : false);
                item.setUseWarrior((use_career & 128) == 128 ? true : false);
                // 陣營使用判斷欄位 (1-魏.2-蜀.4-吳.7-共用)
                item.setCampSet(rs.getInt("use_camp"));
                // 最低使用需求 (轉生次數) by terry0412
                item.setMeteLevel(rs.getInt("MeteLevel"));
                // 最高使用需求 (轉生次數) by terry0412
                item.setMeteLevelMAX(rs.getInt("MeteLevelMAX"));
                // 是否可存倉
                item.setWarehouseable(rs.getInt("是否可存倉") == 0);
                ItemClass.get().addList(itemid, classname, 0);
                result.put(item.getItemId(), item);
            }
        } catch (NullPointerException e) {
            _log.error("加載失敗: " + item.getItemId(), e);
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return result;
    }

    private Map<Integer, L1Weapon> allWeapon() {
        Map<Integer, L1Weapon> result = new HashMap<>();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        L1Weapon weapon = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `weapon`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                weapon = new L1Weapon();
                int itemid = rs.getInt("item_id");
                weapon.setItemId(itemid);
                weapon.setName(rs.getString("name"));
                String classname = rs.getString("classname");
                weapon.setClassname(classname);
                weapon.setNameId(rs.getString("name_id"));
                weapon.setType((Integer) _weaponTypes.get(rs.getString("type")));
                weapon.setType1((Integer) _weaponId.get(rs.getString("type")));
                weapon.setType2(1);
                weapon.setUseType(1);
                weapon.setMaterial((Integer) _materialTypes.get(rs.getString("material")));
                weapon.setWeight(rs.getInt("weight"));
                weapon.setGfxId(rs.getInt("invgfx"));
                weapon.setGroundGfxId(rs.getInt("grdgfx"));
                int itemDescId = rs.getInt("itemdesc_id");
                itemDescId = itemDescId <= 0 ? cdescid() : itemDescId;
                weapon.setItemDescId(itemDescId);
                weapon.setDmgSmall(rs.getInt("dmg_small"));
                weapon.setDmgLarge(rs.getInt("dmg_large"));
                weapon.setRange(rs.getInt("range"));
                weapon.set_safeenchant(rs.getInt("safenchant"));
                weapon.setUseRoyal(rs.getInt("use_royal") != 0);
                weapon.setUseKnight(rs.getInt("use_knight") != 0);
                weapon.setUseElf(rs.getInt("use_elf") != 0);
                weapon.setUseMage(rs.getInt("use_mage") != 0);
                weapon.setUseDarkelf(rs.getInt("use_darkelf") != 0);
                weapon.setUseDragonknight(rs.getInt("use_dragonknight") != 0);
                weapon.setUseIllusionist(rs.getInt("use_illusionist") != 0);
                weapon.setUseWarrior(rs.getInt("use_warrior") == 0 ? false : true);
                weapon.setHitModifier(rs.getInt("hitmodifier"));
                weapon.setDmgModifier(rs.getInt("dmgmodifier"));
                weapon.set_addstr(rs.getByte("add_str"));
                weapon.set_adddex(rs.getByte("add_dex"));
                weapon.set_addcon(rs.getByte("add_con"));
                weapon.set_addint(rs.getByte("add_int"));
                weapon.set_addwis(rs.getByte("add_wis"));
                weapon.set_addcha(rs.getByte("add_cha"));
                weapon.set_addhp(rs.getInt("add_hp"));
                weapon.set_addmp(rs.getInt("add_mp"));
                weapon.set_addhpr(rs.getInt("add_hpr"));
                weapon.set_addmpr(rs.getInt("add_mpr"));
                weapon.set_addsp(rs.getInt("add_sp"));
                weapon.set_mdef(rs.getInt("m_def"));
                weapon.setDoubleDmgChance(rs.getInt("double_dmg_chance"));
                weapon.setMagicDmgModifier(rs.getInt("magicdmgmodifier"));
                weapon.set_canbedmg(rs.getInt("canbedmg"));
                weapon.setMinLevel(rs.getInt("min_lvl"));
                weapon.setMaxLevel(rs.getInt("max_lvl"));
                weapon.setBless(rs.getInt("bless"));
                weapon.setTradable(rs.getInt("trade") == 0);
                weapon.setCantDelete(rs.getInt("cant_delete") == 1);
                weapon.setHasteItem(rs.getInt("haste_item") != 0);
                weapon.setMaxUseTime(rs.getInt("max_use_time"));
                weapon.setExpPoint(rs.getInt("exp_point"));
                // 陣營使用判斷欄位 (1-魏.2-蜀.4-吳.7-共用)
                weapon.setCampSet(rs.getInt("use_camp"));
                // 最低使用需求 (轉生次數) by terry0412
                weapon.setMeteLevel(rs.getInt("MeteLevel"));
                // 最高使用需求 (轉生次數) by terry0412
                weapon.setMeteLevelMAX(rs.getInt("MeteLevelMAX"));
                weapon.set_stunlvl(rs.getInt("衝暈等級"));
                //weapon.set_stunPVP(rs.getInt("stunPVP"));
                weapon.setPvpDmg(rs.getInt("PVP傷害"));// 增加PVP傷害
                weapon.setPvpDmg_R(rs.getInt("PVP減免"));// 减免PVP傷害
                ItemClass.get().addList(itemid, classname, 1);
                result.put(weapon.getItemId(), weapon);
            }
        } catch (NullPointerException e) {
            _log.error("加載失敗: " + weapon.getItemId(), e);
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return result;
    }

    private Map<Integer, L1Armor> allArmor() {
        Map<Integer, L1Armor> result = new HashMap<>();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        L1Armor armor = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `armor`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                armor = new L1Armor();
                int itemid = rs.getInt("item_id");
                armor.setItemId(itemid);
                armor.setName(rs.getString("name"));
                String classname = rs.getString("classname");
                armor.setClassname(classname);
                armor.setNameId(rs.getString("name_id"));
                armor.setType((Integer) _armorTypes.get(rs.getString("type")));
                armor.setType2(2);
                armor.setUseType((Integer) _useTypes.get(rs.getString("type")));
                armor.setMaterial((Integer) _materialTypes.get(rs.getString("material")));
                armor.setWeight(rs.getInt("weight"));
                armor.setGfxId(rs.getInt("invgfx"));
                armor.setGroundGfxId(rs.getInt("grdgfx"));
                int itemDescId = rs.getInt("itemdesc_id");
                itemDescId = itemDescId <= 0 ? cdescid() : itemDescId;
                armor.setItemDescId(itemDescId);
                armor.set_ac(rs.getInt("ac"));
                armor.set_safeenchant(rs.getInt("safenchant"));
                armor.setUseRoyal(rs.getInt("use_royal") != 0);
                armor.setUseKnight(rs.getInt("use_knight") != 0);
                armor.setUseElf(rs.getInt("use_elf") != 0);
                armor.setUseMage(rs.getInt("use_mage") != 0);
                armor.setUseDarkelf(rs.getInt("use_darkelf") != 0);
                armor.setUseDragonknight(rs.getInt("use_dragonknight") != 0);
                armor.setUseIllusionist(rs.getInt("use_illusionist") != 0);
                armor.setUseWarrior(rs.getInt("use_warrior") == 0 ? false : true);
                armor.set_addstr(rs.getByte("add_str"));
                armor.set_addcon(rs.getByte("add_con"));
                armor.set_adddex(rs.getByte("add_dex"));
                armor.set_addint(rs.getByte("add_int"));
                armor.set_addwis(rs.getByte("add_wis"));
                armor.set_addcha(rs.getByte("add_cha"));
                armor.set_addhp(rs.getInt("add_hp"));
                armor.set_addmp(rs.getInt("add_mp"));
                armor.set_addhpr(rs.getInt("add_hpr"));
                armor.set_addmpr(rs.getInt("add_mpr"));
                armor.set_addsp(rs.getInt("add_sp"));
                armor.setMinLevel(rs.getInt("min_lvl"));
                armor.setMaxLevel(rs.getInt("max_lvl"));
                armor.set_mdef(rs.getInt("m_def"));
                armor.setDamageReduction(rs.getInt("damage_reduction"));
                armor.setWeightReduction(rs.getInt("weight_reduction"));
                armor.setHitModifierByArmor(rs.getInt("hit_modifier"));
                armor.setDmgModifierByArmor(rs.getInt("dmg_modifier"));
                armor.setBowHitModifierByArmor(rs.getInt("bow_hit_modifier"));
                armor.setBowDmgModifierByArmor(rs.getInt("bow_dmg_modifier"));
                armor.setHasteItem(rs.getInt("haste_item") != 0);
                armor.setBless(rs.getInt("bless"));
                armor.setTradable(rs.getInt("trade") == 0);
                armor.setCantDelete(rs.getInt("cant_delete") == 1);
                armor.set_defense_earth(rs.getInt("defense_earth"));
                armor.set_defense_water(rs.getInt("defense_water"));
                armor.set_defense_wind(rs.getInt("defense_wind"));
                armor.set_defense_fire(rs.getInt("defense_fire"));
                armor.set_regist_stun(rs.getInt("regist_stun"));
                armor.set_regist_stone(rs.getInt("regist_stone"));
                armor.set_regist_sleep(rs.getInt("regist_sleep"));
                armor.set_regist_freeze(rs.getInt("regist_freeze"));
                armor.set_regist_sustain(rs.getInt("regist_sustain"));
                armor.set_regist_blind(rs.getInt("regist_blind"));
                armor.setMaxUseTime(rs.getInt("max_use_time"));
                armor.set_greater(rs.getInt("greater"));
                armor.setExpPoint(rs.getInt("exp_point"));
                // 陣營使用判斷欄位 (1-魏.2-蜀.4-吳.7-共用)
                armor.setCampSet(rs.getInt("use_camp"));
                // 最低使用需求 (轉生次數) by terry0412
                armor.setMeteLevel(rs.getInt("MeteLevel"));
                // 最高使用需求 (轉生次數) by terry0412
                armor.setMeteLevelMAX(rs.getInt("MeteLevelMAX"));
                armor.setMagicHitModifierByArmor(rs.getInt("magic_hit_modifier"));
                armor.set_up_hp_potion(rs.getInt("up_hp_potion"));
                armor.set_uhp_number(rs.getInt("uhp_number"));
                //armor.set_stunPVP2(rs.getInt("stunPVP"));
                armor.setActivity(rs.getBoolean("is_activity"));  //src013
                armor.set_kitType(rs.getInt("kit_type"));
                armor.setSuperRune(rs.getBoolean("是否附魔超能系統"));
                armor.setPvpDmg(rs.getInt("PVP傷害"));// 增加PVP傷害
                armor.setPvpDmg_R(rs.getInt("PVP減免"));// 减免PVP傷害
                armor.setDice_dmg(rs.getInt("機率給予爆擊"));
                armor.setDmg(rs.getInt("機率給予爆擊值"));
                armor.set_influence_safe(rs.getInt("influence_safe"));
                armor.set_influence_str(rs.getInt("influence_str"));
                armor.set_influence_dex(rs.getInt("influence_dex"));
                armor.set_influence_con(rs.getInt("influence_con"));
                armor.set_influence_int(rs.getInt("influence_int"));
                armor.set_influence_wis(rs.getInt("influence_wis"));
                armor.set_influence_cha(rs.getInt("influence_cha"));
                armor.set_influence_sp(rs.getInt("influence_sp"));
                armor.set_influence_mr(rs.getInt("influence_mr"));
                armor.set_influence_hp(rs.getInt("influence_hp"));
                armor.set_influence_mp(rs.getInt("influence_mp"));
                armor.set_influence_dmgR(rs.getInt("influence_dmgR"));
                armor.set_influence_hitAndDmg(rs.getInt("influence_hitAndDmg"));
                armor.set_influence_bowHitAndDmg(rs.getInt("influence_bowHitAndDmg"));
                ItemClass.get().addList(itemid, classname, 2);
                result.put(armor.getItemId(), armor);
            }
        } catch (NullPointerException e) {
            _log.error("加載失敗: " + armor.getItemId(), e);
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return result;
    }

    private void buildFastLookupTable() {
        int highestId = 0;
        final Collection<L1EtcItem> items = _etcitems.values();
        for (final L1EtcItem item : items) {
            if (item.getItemId() > highestId) {
                highestId = item.getItemId();
            }
        }
        final Collection<L1Weapon> weapons = _weapons.values();
        for (final L1Weapon weapon : weapons) {
            if (weapon.getItemId() > highestId) {
                highestId = weapon.getItemId();
            }
        }
        final Collection<L1Armor> armors = _armors.values();
        for (final L1Armor armor : armors) {
            if (armor.getItemId() > highestId) {
                highestId = armor.getItemId();
            }
        }
        this._allTemplates = new L1Item[highestId + 1];
        for (final Integer id : _etcitems.keySet()) {
            final L1EtcItem item = _etcitems.get(id);
            this._allTemplates[id] = item;
        }
        for (final Integer id : _weapons.keySet()) {
            final L1Weapon item = _weapons.get(id);
            this._allTemplates[id] = item;
        }
        for (final Integer id : _armors.keySet()) {
            final L1Armor item = _armors.get(id);
            this._allTemplates[id] = item;
        }
    }

    /**
     * 套裝效果
     */
    public void se_mode() {
        PerformanceTimer timer = new PerformanceTimer();
        for (L1Item item : _allTemplates) {
            if (item != null) {
                for (Integer key : ArmorSet.getAllSet().keySet()) {
                    ArmorSet armorSet = (ArmorSet) ArmorSet.getAllSet().get(key);
                    if (armorSet.isPartOfSet(item.getItemId())) {
                        item.set_mode(armorSet.get_mode());
                    }
                }
            }
        }
        _log.info("讀取->套裝效果數字陣列: " + timer.get() + "ms)");
    }

    /**
     * 傳回指定編號物品資料
     *
     */
    public L1Item getTemplate(final int itemid) {
        try {
            return this._allTemplates[itemid];
        } catch (final Exception e) {
        }
        return null;
    }

    /**
     * 傳回指定名稱物品資料
     *
     */
    public L1Item getTemplate(final String nameid) {
        for (final L1Item item : this._allTemplates) {
            if ((item != null) && item.getNameId().equals(nameid)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 產生新物件
     *
     */
    public L1ItemInstance createItem(final int itemId) {
        final L1Item temp = this.getTemplate(itemId);
        if (temp == null) {
            return null;
        }
        final L1ItemInstance item = new L1ItemInstance();
        item.setId(IdFactory.get().nextId());
        item.setItem(temp);
        item.setBless(temp.getBless());
        World.get().storeObject(item);
        return item;
    }

    public L1ItemInstance createItem(final int itemId, final boolean flag) {
        final L1Item temp = this.getTemplate(itemId);
        if (temp == null) {
            return null;
        }
        final L1ItemInstance item = new L1ItemInstance();
        item.setItem(temp);
        if (flag) {
            item.setId(IdFactory.get().nextId());
            item.setBless(temp.getBless());
            World.get().storeObject(item);
        }
        return item;
    }

    /**
     * 依名稱(NameId)找回itemid
     *
     */
    public int findItemIdByName(final String name) {
        int itemid = 0;
        for (final L1Item item : this._allTemplates) {
            if ((item != null) && item.getNameId().equals(name)) {
                itemid = item.getItemId();
                break;
            }
        }
        return itemid;
    }

    /**
     * 依名稱(中文)找回itemid
     *
     */
    public int findItemIdByNameWithoutSpace(final String name) {
        int itemid = 0;
        for (final L1Item item : this._allTemplates) {
            if ((item != null) && item.getNameId().replace(" ", "").equals(name)) {
                itemid = item.getItemId();
                break;
            }
        }
        return itemid;
    }

    /**
     * 依名稱(中文)找回itemid
     *
     */
    public int findItemIdByNameWithoutSpace2(final String name) {
        int itemid = 0;
        for (final L1Item item : this._allTemplates) {
            if ((item != null) && item.getName().replace(" ", "").equals(name)) {
                itemid = item.getItemId();
                break;
            }
        }
        return itemid;
    }

    public String findItemIdByName(int itemid) {
        String name = null;
        for (L1Item item : _allTemplates) {
            if (item != null && item.getItemId() == itemid) {
                name = item.getName();
                return name;
            }
        }
        return null;
    }
}
