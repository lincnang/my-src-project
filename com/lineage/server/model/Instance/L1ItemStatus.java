package com.lineage.server.model.Instance;

import com.add.ArmorEnchantSystem;
import com.add.JiezEnchant;
import com.add.Tsai.NewEnchantSystem;
import com.lineage.server.datatables.*;
import com.lineage.server.model.L1WeaponSkill;
import com.lineage.server.model.doll.L1DollExecutor;
import com.lineage.server.templates.*;
import com.lineage.server.utils.BinaryOutputStream;
import william.L1WeaponSoul;
import william.WeaponSoul;
import william.WilliamItemMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 物品詳細資料
 *
 * @author dexc
 */
public class L1ItemStatus {
    // 記錄時間格式
    private static final SimpleDateFormat delTime = new SimpleDateFormat("-[MM-dd HH:mm]");
    private final L1ItemInstance _itemInstance;
    private final L1Item _item;
    private final BinaryOutputStream _os;
    private final L1ItemPower _itemPower;

    /**
     * 物品詳細資料
     *
     * @param itemInstance L1ItemInstance
     */
    public L1ItemStatus(L1ItemInstance itemInstance) {
        _itemInstance = itemInstance;
        _item = itemInstance.getItem();
        _os = new BinaryOutputStream();
        _itemPower = new L1ItemPower(_itemInstance);
    }

    public L1ItemStatus(L1Item template) {
        _itemInstance = new L1ItemInstance();
        _itemInstance.setItem(template);
        _item = template;
        _os = new BinaryOutputStream();
        _itemPower = new L1ItemPower(_itemInstance);
    }

    public L1ItemStatus(final L1Item template, final int enchantLevel) {
        this(template);
        this._itemInstance.setEnchantLevel(enchantLevel);
    }

    /**
     * vip 顯示設定
     */
    private BinaryOutputStream etcitem_card(L1ItemVIP vip) {
        int add_ac = vip.get_add_ac(); // 防禦
        if (add_ac != 0) {
            _os.writeC(56);
            _os.writeC(add_ac);
        }
        int add_str = vip.get_add_str(); // 力量
        if (add_str != 0) {
            _os.writeC(8);
            _os.writeC(add_str);
        }
        int add_dex = vip.get_add_dex(); // 敏捷
        if (add_dex != 0) {
            _os.writeC(9);
            _os.writeC(add_dex);
        }
        int add_con = vip.get_add_con(); // 體質
        if (add_con != 0) {
            _os.writeC(10);
            _os.writeC(add_con);
        }
        int add_wis = vip.get_add_wis(); // 精神
        if (add_wis != 0) {
            _os.writeC(11);
            _os.writeC(add_wis);
        }
        int add_int = vip.get_add_int(); // 智力
        if (add_int != 0) {
            _os.writeC(12);
            _os.writeC(add_int);
        }
        int add_cha = vip.get_add_cha(); // 魅力
        if (add_cha != 0) {
            _os.writeC(13);
            _os.writeC(add_cha);
        }
        int add_dmg = vip.get_add_dmg(); // 近戰傷害
        if (add_dmg != 0) {
            _os.writeC(47);
            _os.writeC(add_dmg);
        }
        int add_hit = vip.get_add_hit(); // 近戰命中
        if (add_hit != 0) {
            _os.writeC(48);
            _os.writeC(add_hit);
        }
        int add_bow_dmg = vip.get_add_bow_dmg(); // 遠攻傷害
        if (add_bow_dmg != 0) {
            _os.writeC(35);
            _os.writeC(add_bow_dmg);
        }
        int add_bow_hit = vip.get_add_bow_hit(); // 遠攻命中
        if (add_bow_hit != 0) {
            _os.writeC(24);
            _os.writeC(add_bow_hit);
        }
        int add_mr = vip.get_add_mr(); // 魔法防禦
        if (add_mr != 0) {
            _os.writeC(15);
            _os.writeH(add_mr);
        }
        int add_sp = vip.get_add_sp(); // 魔攻
        if (add_sp != 0) {
            _os.writeC(17);
            _os.writeC(add_sp);
        }
        int add_fire = vip.get_add_fire(); // 火屬性
        if (add_fire != 0) {
            _os.writeC(27);
            _os.writeC(add_fire);
        }
        int add_wind = vip.get_add_wind(); // 風屬性
        if (add_wind != 0) {
            _os.writeC(29);
            _os.writeC(add_wind);
        }
        int add_earth = vip.get_add_earth(); // 地屬性
        if (add_earth != 0) {
            _os.writeC(30);
            _os.writeC(add_earth);
        }
        int add_water = vip.get_add_water(); // 水屬性
        if (add_water != 0) {
            _os.writeC(28);
            _os.writeC(add_water);
        }
        int add_hp = vip.get_add_hp(); // 血量
        if (add_hp != 0) {
            _os.writeC(14);
            _os.writeH(add_hp);
        }
        int add_mp = vip.get_add_mp(); // 魔量
        if (add_mp != 0) {
            _os.writeC(0x20);
            _os.writeH(add_mp);
        }
        int add_hpr = vip.get_add_hpr(); // 回血
        if (add_hpr != 0) {
            _os.writeC(37);
            _os.writeC(add_hpr);
        }
        int add_mpr = vip.get_add_mpr(); // 回魔
        if (add_mpr != 0) {
            _os.writeC(38);
            _os.writeC(add_mpr);
        }
        /*
         * int add_freeze = vip.get_add_freeze(); if (add_freeze != 0) {
         * _os.writeC(33); _os.writeC(1); _os.writeC(add_freeze); }
         *
         * int add_stone = vip.get_add_stone(); if (add_stone != 0) {
         * _os.writeC(33); _os.writeC(2); _os.writeC(add_stone); }
         *
         * int add_sleep = vip.get_add_sleep(); if (add_sleep != 0) {
         * _os.writeC(33); _os.writeC(3); _os.writeC(add_sleep); }
         *
         * int add_blind = vip.get_add_blind(); if (add_blind != 0) {
         * _os.writeC(33); _os.writeC(4); _os.writeC(add_blind); } int add_stun
         * = vip.get_add_stun(); if (add_stun != 0) { _os.writeC(33);
         * _os.writeC(5); _os.writeC(add_stun); }
         *
         * int add_sustain = vip.get_add_sustain(); if (add_sustain != 0) {
         * _os.writeC(33); _os.writeC(6); _os.writeC(add_sustain); }
         */
        int wmd = vip.get_add_wmd(); // 魔武傷害增加
        if (wmd != 0) {
            _os.writeC(39);
            _os.writeS("魔武傷害:" + wmd + "%");
        }
        int wmc = vip.get_add_wmc(); // 魔武發動增加
        if (wmc != 0) {
            _os.writeC(39);
            _os.writeS("魔武發動:" + wmc + "%");
        }
        int dmgr = vip.get_add_dmg_r(); // 物理減傷
        if (dmgr != 0) {
            _os.writeC(39);
            _os.writeS("物理減傷: +" + dmgr);
        }
        int mdmgr = vip.get_add_magic_r(); // 魔法減傷
        if (mdmgr != 0) {
            _os.writeC(39);
            _os.writeS("魔法減傷: +" + mdmgr);
        }
        int exp = vip.get_add_exp(); // 經驗值增加
        if (exp != 0) {
            if (exp <= 120) {
                _os.writeC(36);
                _os.writeC(exp);
            } else {
                _os.writeC(0x27);
                _os.writeS("$6134 " + exp + "%");
            }
        }
        int gf = vip.get_add_adena(); // 金幣倍率增加
        if (gf != 0) {
            _os.writeC(39);
            _os.writeS("金幣倍率:" + gf + "%");
        }
        boolean item = vip.get_death_item(); // 防噴道具
        if (item) {
            _os.writeC(39);
            _os.writeS("防噴道具");
        }
        boolean exp1 = vip.get_death_exp(); // 防噴經驗
        if (exp1) {
            _os.writeC(39);
            _os.writeS("防噴經驗");
        }
        boolean skill = vip.get_death_skill(); // 防噴技能
        if (skill) {
            _os.writeC(39);
            _os.writeS("防噴技能");
        }
        boolean score = vip.get_death_score(); // 防噴積分
        if (score) {
            _os.writeC(39);
            _os.writeS("防噴積分");
        }
        int potion = vip.get_up_hp_potion(); // 藥水回復
        if (potion != 0) {
            _os.writeC(39);
            _os.writeS("藥水回復: +" + potion + "%");
        }
        int PVP = vip.getPvpDmg(); // PVP攻擊
        if (PVP != 0) {
            _os.writeC(39);
            _os.writeS("PVP攻擊: +" + PVP);
        }
        int PVP_R = vip.getPvpDmg_R(); // PVP減免
        if (PVP_R != 0) {
            _os.writeC(39);
            _os.writeS("PVP減免: +" + PVP_R);
        }
        int magic_hit = vip.getOriginalMagicHit(); // 魔法命中
        if (magic_hit != 0) {
            _os.writeC(39);
            _os.writeS("魔法命中: +" + magic_hit);
        }
        int StunLv = vip.getStunLevel(); // 昏迷命中
        if (StunLv != 0) {
            _os.writeC(39);
            _os.writeS("昏迷命中: +" + StunLv);
        }
        int CloseCritical = vip.getCloseCritical(); // 近距離爆擊
        if (CloseCritical != 0) {
            _os.writeC(39);
            _os.writeS("近距離爆擊: +" + CloseCritical + "%");
        }
        int BowCritical = vip.getBowCritical(); // 遠距離爆擊率
        if (BowCritical != 0) {
            _os.writeC(39);
            _os.writeS("遠距離爆擊率: +" + BowCritical + "%");
        }
        int add_DiceDmg = vip.getDiceDmg(); // 爆擊機率
        if (add_DiceDmg != 0) {
            _os.writeC(39);
            _os.writeS("決勝打擊: +" + (add_DiceDmg/10) + "%");
        }
        int add_DiceDmg_dmherm = vip.getDmg(); // 爆擊質數
        if (add_DiceDmg_dmherm != 0) {
            _os.writeC(39);
            _os.writeS("決勝打擊增幅 +" + (add_DiceDmg_dmherm) + "%");
        }

        int add_DamageReductionPVE = vip.get_DamageReductionPVE(); //PVE
        if (add_DamageReductionPVE != 0) {
            _os.writeC(39);
            _os.writeS("PVE攻擊: +" + add_DamageReductionPVE);
        }
        int add_DamageReductionPVE_R = vip.get_DamageReductionPVE_R(); //PVE
        if (add_DamageReductionPVE_R != 0) {
            _os.writeC(39);
            _os.writeS("PVE減免: +" + add_DamageReductionPVE_R);
        }
        int add_dice_hp = vip.get_dice_hp();
        if (add_dice_hp != 0) {
            _os.writeC(39);
            _os.writeS("HP吸收");
        }
        int add_dice_mp = vip.get_dice_mp();
        if (add_dice_mp != 0) {
            _os.writeC(39);
            _os.writeS("MP吸收");
        }

        // 2017/04/21
        ArrayList<String> as = new ArrayList<>();
        try {
            for (String s : WilliamItemMessage.getItemInfo(_itemInstance)) {
                if (s != null && !s.isEmpty()) {
                    _os.writeC(39);
                    _os.writeS(s);
                }
            }
        } finally {
            as.clear();
        }
        this._os.writeC(0x17); // 材質
        this._os.writeC(this._item.getMaterial());
        this._os.writeD(this._itemInstance.getWeight());
        showItemDelTimer();
        // 昇華能力顯示
        if (_itemInstance.getSublimation() != null) {
            CharItemSublimation sub = _itemInstance.getSublimation();

        }
        return _os;
    }
    /**
     * 顯示刪除日期時間
     */
    private void showItemDelTimer() {
        if (_itemInstance.get_time() != null) {
            final long iTime = _itemInstance.get_time().getTime();
            String dTime = "$376"+ delTime.format(iTime);
            _os.writeC(0x27);
            _os.writeS(dTime);
        }
    }

    /**
     * 傳回物品描述
     *
     */
    public BinaryOutputStream getStatusBytes(boolean statusx) {
        int use_type = _item.getUseType();
        switch (use_type) {
            case -11: // 對讀取方法調用無法分類的物品
            case -10: // 加速藥水
            case -9: // 技術書
            case -8: // 料理書
            case -7: // 增HP道具
            case -6: // 增MP道具
            case -5: // 食人妖精競賽票
            case -4: // 項圈
            case -1: // 無法使用(材料等)
            case 0: // 一般物品
            case 3: // 創造怪物魔杖(無須選取目標 - 無數量:沒有任何事情發生)
            case 5: // 魔杖類型(須選取目標)
            case 6: // 瞬間移動卷軸
            case 7: // 鑒定卷軸
            case 9: // 傳送回家的卷軸
            case 8: // 復活卷軸
            case 12: // 信紙
            case 13: // 信紙(寄出)
            case 14: // 請選擇一個物品(道具欄位)
            case 15: // 哨子
            case 16: // 變形卷軸
            case 17: // 選取目標 (近距離)
            case 26: // 對武器施法的卷軸
            case 27: // 對盔甲施法的卷軸
            case 28: // 空的魔法卷軸
            case 29: // 瞬間移動卷軸(祝福)
            case 30: // 魔法卷軸選取目標 (遠距離 無XY座標傳回)
            case 31: // 聖誕卡片
            case 32: // 聖誕卡片(寄出)
            case 33: // 情人節卡片
            case 34: // 情人節卡片(寄出)
            case 35: // 白色情人節卡片
            case 36: // 白色情人節卡片(寄出)
            case 39: // 選取目標 (遠距離)
            case 42: // 釣魚桿
            case 46: // 飾品強化卷軸
            case 55: // 請選擇魔法娃娃
            case 62: // 抽抽樂
            case 65: // 抽抽樂
                String classname = _item.getclassname();
                if (classname.startsWith("shop.VIP_Card_")) {
                    return etcitem_card(classname);
                }
                if (ItemVIPTable.get().checkVIP(_item.getItemId())) {
                    return etcitem_card(ItemVIPTable.get().getVIP(_item.getItemId()));
                } else if (classname.equalsIgnoreCase("doll.Magic_Doll")) {
                    return etcitem_doll();
                } else if (classname.equalsIgnoreCase("doll.Magic_Doll2")) {
                    return etcitem_doll();
                } else if (classname.equalsIgnoreCase("doll.Magic_Doll_Power")) {
                    return etcitem_doll();
                } else if (ItemBuffTable.get().checkItem(_item.getItemId())) { // 道具狀態系統
                    return etcitem_Buff(ItemBuffTable.get().getUseEX(_item.getItemId()));
                } else if (CheckItemPowerTable.get().checkItem(_item.getItemId())) { // 身上持有道具給予能力系統
                    return etcitem_checkitempower(CheckItemPowerTable.get().getItem(_item.getItemId()));
                } else if ((_item.getItemId() == 56147) || // 真 妲蒂斯魔石
                        (_item.getItemId() == 56148) || // 妲蒂斯魔石
                        (_item.getItemId() == 56150) || // 守護者的靈魂
                        (_item.getItemId() == 56152) // 戰神之魂
                ) {
                    return effective_item();
                }
                return etcitem();
            case -12: // 寵物用具
                final L1PetItem petItem = PetItemTable.get().getTemplate(this._item.getItemId());
                // 武器
                if (petItem.isWeapom()) {
                    return this.petweapon(petItem);
                    // 防具
                } else {
                    return this.petarmor(petItem);
                }
            case -3: // 飛刀
            case -2: // 箭
                return this.arrow();
            case 38: // 食物
                return this.fooditem();
            case 10: // 照明道具
                return this.lightitem();
            case 2: // 盔甲
            case 18: // T恤
            case 19: // 斗篷
            case 20: // 手套
            case 21: // 靴
            case 22: // 頭盔
            case 25: // 盾牌
            case 70: // 脛甲
                return this.armor();
            case 40: // 耳環
            case 23: // 戒指
            case 24: // 項鏈
            case 37: // 腰帶
                return this.accessories();
            case 43: // 符文道具左
            case 44: // 符文道具右
            case 45: // 符文道具中
            case 48: // 六芒星護身符
            case 49: // 蒂蜜特祝福系列
            case 51: // 蒂蜜特符文
            case 52: // VIP
            case 53: // VIP2
                return this.accessories2();
            case 1: // 武器
                return this.weapon();
        }
        return null;
    }

    /**
     * 身上持有道具給予能力系統
     */
    private BinaryOutputStream etcitem_checkitempower(L1CheckItemPower power) {
        if (power.get_ac() != 0) { // 防禦
            _os.writeC(56);
            _os.writeC(power.get_ac());
        }
        if (power.get_str() != 0) { // 力量
            _os.writeC(8);
            _os.writeC(power.get_str());
        }
        if (power.get_dex() != 0) { // 敏捷
            _os.writeC(9);
            _os.writeC(power.get_dex());
        }
        if (power.get_con() != 0) { // 體質
            _os.writeC(10);
            _os.writeC(power.get_con());
        }
        if (power.get_wis() != 0) { // 精神
            _os.writeC(11);
            _os.writeC(power.get_wis());
        }
        if (power.get_intel() != 0) { // 智力
            _os.writeC(12);
            _os.writeC(power.get_intel());
        }
        if (power.get_cha() != 0) { // 魅力
            _os.writeC(13);
            _os.writeC(power.get_cha());
        }
        if (power.get_hp() != 0) { // 血量
            _os.writeC(14);
            _os.writeH(power.get_hp());
        }
        if (power.get_mp() != 0) { // 魔量
            _os.writeC(0x20);
            _os.writeH(power.get_mp());
        }
        if (power.get_mr() != 0) { // 魔法防禦
            _os.writeC(15);
            _os.writeH(power.get_mr());
        }
        if (power.get_sp() != 0) { // 魔攻
            _os.writeC(17);
            _os.writeC(power.get_sp());
        }
        if (power.get_dmg() != 0) { // 近戰傷害
            _os.writeC(47);
            _os.writeC(power.get_dmg());
        }
        if (power.get_bow_dmg() != 0) { // 遠攻傷害
            _os.writeC(35);
            _os.writeC(power.get_bow_dmg());
        }
        if (power.get_hit() != 0) { // 近戰命中
            _os.writeC(48);
            _os.writeC(power.get_hit());
        }
        if (power.get_bow_hit() != 0) { // 遠攻命中
            _os.writeC(24);
            _os.writeC(power.get_bow_hit());
        }
        if (power.get_dmg_r() != 0) { // 物理減傷
            _os.writeC(39);
            _os.writeS("物理減傷 +" + power.get_dmg_r());
        }
        if (power.get_magic_r() != 0) { // 魔法減傷
            _os.writeC(39);
            _os.writeS("魔法減傷 +" + power.get_magic_r());
        }
        if (power.get_fire() != 0) { // 火屬性
            _os.writeC(27);
            _os.writeC(power.get_fire());
        }
        if (power.get_water() != 0) { // 水屬性
            _os.writeC(28);
            _os.writeC(power.get_water());
        }
        if (power.get_wind() != 0) { // 風屬性
            _os.writeC(29);
            _os.writeC(power.get_wind());
        }
        if (power.get_earth() != 0) { // 地屬性
            _os.writeC(30);
            _os.writeC(power.get_earth());
        }
        if (power.get_freeze() != 0) { // 冰凍耐性
            _os.writeC(33);
            _os.writeC(1);
            _os.writeC(power.get_freeze());
        }
        if (power.get_stone() != 0) { // 石化耐性
            _os.writeC(33);
            _os.writeC(2);
            _os.writeC(power.get_stone());
        }
        if (power.get_sleep() != 0) { // 睡眠耐性
            _os.writeC(33);
            _os.writeC(3);
            _os.writeC(power.get_sleep());
        }
        if (power.get_blind() != 0) { // 暗黑耐性
            _os.writeC(33);
            _os.writeC(4);
            _os.writeC(power.get_blind());
        }
        if (power.get_stun() != 0) { // 昏迷耐性
            _os.writeC(33);
            _os.writeC(5);
            _os.writeC(power.get_stun());
        }
        if (power.get_sustain() != 0) { // 支撐耐性
            _os.writeC(33);
            _os.writeC(6);
            _os.writeC(power.get_sustain());
        }
        if (power.get_hpr() != 0) { // 體力回復量
            _os.writeC(37);
            _os.writeC(power.get_hpr());
        }
        if (power.get_mpr() != 0) { // 魔力回復量
            _os.writeC(38);
            _os.writeC(power.get_mpr());
        }
        if (power.get_exp() != 0) { // 狩獵經驗值
            if (power.get_exp() <= 120) {
                _os.writeC(36);
                _os.writeC(power.get_exp());
            } else {
                _os.writeC(0x27);
                _os.writeS("$6134 " + power.get_exp() + "%");
            }
        }
        if (power.get_gf() != 0) { // 金幣倍率
            _os.writeC(39);
            _os.writeS("金幣倍率 +" + power.get_gf() + "%");
        }
        boolean item = power.get_death_item(); // 死亡不噴道具
        if (item) {
            _os.writeC(39);
            _os.writeS("防噴道具");
        }
        boolean exp = power.get_death_exp(); // 死亡不噴經驗值
        if (exp) {
            _os.writeC(39);
            _os.writeS("防噴經驗");
        }
        boolean skill = power.get_death_skill(); // 死亡不噴技能
        if (skill) {
            _os.writeC(39);
            _os.writeS("防噴技能");
        }
        boolean score = power.get_death_score(); // 死亡不掉積分
        if (score) {
            _os.writeC(39);
            _os.writeS("防噴積分");
        }
        // 2017/04/21
        ArrayList<String> as = new ArrayList<>();
        try {
            for (String s : WilliamItemMessage.getItemInfo(_itemInstance)) {
                if (s != null && !s.isEmpty()) {
                    _os.writeC(39);
                    _os.writeS(s);
                }
            }
        } finally {
            as.clear();
        }
        this._os.writeC(0x17); // 材質
        this._os.writeC(this._item.getMaterial());
        this._os.writeD(this._itemInstance.getWeight());
        showItemDelTimer();
        return _os;
    }

    // TODO 持有效果道具
    private BinaryOutputStream effective_item() {
        if (_item.getItemId() == 56147) {// 真 妲蒂斯魔石
            _os.writeC(14);// 血量上限
            _os.writeH(100);
            _os.writeC(0x20);// 魔力上限
            _os.writeH(100);
            _os.writeC(47);// 近距離傷害
            _os.writeC(5);
            _os.writeC(35);// 遠距離傷害
            _os.writeC(5);
            _os.writeC(17);// 魔攻
            _os.writeC(5);
            _os.writeC(63); // 傷害減免
            _os.writeC(5);
            _os.writeC(37);// 體力回復量
            _os.writeC(5);
            _os.writeC(38);// 魔力回復量
            _os.writeC(5);
        } else if (_item.getItemId() == 56148) {// 妲蒂斯魔石
            _os.writeC(14);// 血量上限
            _os.writeH(30);
            _os.writeC(0x20);// 魔力上限
            _os.writeH(30);
            _os.writeC(47);// 近距離傷害
            _os.writeC(2);
            _os.writeC(35);// 遠距離傷害
            _os.writeC(2);
            _os.writeC(17);// 魔攻
            _os.writeC(2);
            _os.writeC(63); // 傷害減免
            _os.writeC(2);
            _os.writeC(37);// 體力回復量
            _os.writeC(2);
            _os.writeC(38);// 魔力回復量
            _os.writeC(2);
        } else if (_item.getItemId() == 56150) {// 守護者的靈魂
            _os.writeC(14);// 血量上限
            _os.writeH(400);
            _os.writeC(0x20);// 魔力上限
            _os.writeH(200);
            _os.writeC(47);// 近距離傷害
            _os.writeC(100);
            _os.writeC(17);// 魔攻
            _os.writeC(30);
            _os.writeC(63); // 傷害減免
            _os.writeC(60);
            _os.writeC(39);
            _os.writeS("防噴經驗");
            _os.writeC(39);
            _os.writeS("防噴道具");
        } else if (_item.getItemId() == 56152) {// 戰神之魂
            _os.writeC(14);// 血量上限
            _os.writeH(120);
            _os.writeC(0x20);// 魔力上限
            _os.writeH(100);
            _os.writeC(47); // 近距離傷害
            _os.writeC(15);
            _os.writeC(35);// 遠距離傷害
            _os.writeC(15);
            _os.writeC(17);// 魔攻
            _os.writeC(5);
            _os.writeC(63); // 傷害減免
            _os.writeC(8);
        }
		/*if (_statusx) {
			if (!_item.isTradable()) {
				_os.writeC(39);
				_os.writeS("無法交易");
			}
			if (_item.isCantDelete()) {
				_os.writeC(39);
				_os.writeS("無法刪除");
			}
			if (_item.get_safeenchant() < 0) {
				_os.writeC(39);
				_os.writeS("無法強化");
			}
		}*/
        // 2017/04/21
        ArrayList<String> as = new ArrayList<>();
        try {
            for (String s : WilliamItemMessage.getItemInfo(_itemInstance)) {
                if (s != null && !s.isEmpty()) {
                    _os.writeC(39);
                    _os.writeS(s);
                }
            }
        } finally {
            as.clear();
        }
        showItemDelTimer();
        return _os;
    }

    /**
     * 娃娃能力描述
     */
    private BinaryOutputStream etcitem_doll() {
        final L1Doll doll = DollPowerTable.get().get_type(_item.getItemId());
        String msg = null;
        this._os.writeC(39);
        this._os.writeS("詳細數值如下");
        if (!doll.getPowerList().isEmpty()) {
            for (L1DollExecutor power : doll.getPowerList()) {
                if (power.getDollAc() != 0) { // 防禦
                    _os.writeC(56);
                    _os.writeC(power.getDollAc());
                }
                if (power.getDollStr() != 0) { // 力量
                    _os.writeC(8);
                    _os.writeC(power.getDollStr());
                }
                if (power.getDollDex() != 0) { // 敏捷
                    _os.writeC(9);
                    _os.writeC(power.getDollDex());
                }
                if (power.getDollCon() != 0) { // 體質
                    _os.writeC(10);
                    _os.writeC(power.getDollCon());
                }
                if (power.getDollWis() != 0) { // 精神
                    _os.writeC(11);
                    _os.writeC(power.getDollWis());
                }
                if (power.getDollInt() != 0) { // 智力
                    _os.writeC(12);
                    _os.writeC(power.getDollInt());
                }
                if (power.getDollCha() != 0) { // 魅力
                    _os.writeC(13);
                    _os.writeC(power.getDollCha());
                }
                if (power.getDollHp() != 0) { // 血量
                    _os.writeC(14);
                    _os.writeH(power.getDollHp());
                }
                if (power.getDollMp() != 0) { // 魔量
                    _os.writeC(0x20);
                    _os.writeH(power.getDollMp());
                }
                if (power.getDollHpr() != 0) { // 體力回覆量
                    _os.writeC(37);
                    _os.writeC(power.getDollHpr());
                }
                if (power.getDollMpr() != 0) { // 魔力回覆量
                    _os.writeC(38);
                    _os.writeC(power.getDollMpr());
                }
                if (power.getDollMr() != 0) { // 魔法防禦
                    _os.writeC(15);
                    _os.writeH(power.getDollMr());
                }
                if (power.getDollSp() != 0) { // 魔攻
                    _os.writeC(17);
                    _os.writeC(power.getDollSp());
                }
                if (power.getDollDmg() != 0) { // 近戰傷害
                    _os.writeC(47);
                    _os.writeC(power.getDollDmg());
                }
                if (power.getDollHit() != 0) { // 近戰命中
                    _os.writeC(48);
                    _os.writeC(power.getDollHit());
                }
                if (power.getDollBowDmg() != 0) { // 遠攻傷害
                    _os.writeC(35);
                    _os.writeC(power.getDollBowDmg());
                }
                if (power.getDollBowHit() != 0) { // 遠攻命中
                    _os.writeC(24);
                    _os.writeC(power.getDollBowHit());
                }
                if (power.getDollAllDmg_R() != 0) { // 傷害減免
                    _os.writeC(63);
                    _os.writeC(power.getDollAllDmg_R());
                }
                if (power.getDollExp() != 0) { // 狩獵經驗值
                    if (power.getDollExp() <= 120) {
                        _os.writeC(36);
                        _os.writeC(power.getDollExp());
                    } else {
                        _os.writeC(0x27);
                        _os.writeS("$6134 " + power.getDollExp() + "%");
                    }
                }
                if (power.getDollWeight() != 0) { // 負重增加率(%)
                    _os.writeC(68);
                    _os.writeC(power.getDollWeight());
                }
                if (power.getDollWeight_R() != 0) { // 增加負重 +X
                    _os.writeC(90);
                    _os.writeH(power.getDollWeight_R());
                }
                if (power.getDollFire() != 0) { // 火屬性
                    _os.writeC(27);
                    _os.writeC(power.getDollFire());
                }
                if (power.getDollWater() != 0) { // 水屬性
                    _os.writeC(28);
                    _os.writeC(power.getDollWater());
                }
                if (power.getDollWind() != 0) { // 風屬性
                    _os.writeC(29);
                    _os.writeC(power.getDollWind());
                }
                if (power.getDollEarth() != 0) { // 地屬性
                    _os.writeC(30);
                    _os.writeC(power.getDollEarth());
                }
                if (power.getDollFreeze() != 0) { // 冰凍耐性
                    _os.writeC(33);
                    _os.writeC(1);
                    _os.writeC(power.getDollFreeze());
                }
                if (power.getDollStone() != 0) { // 石化耐性
                    _os.writeC(33);
                    _os.writeC(2);
                    _os.writeC(power.getDollStone());
                }
                if (power.getDollSleep() != 0) { // 睡眠耐性
                    _os.writeC(33);
                    _os.writeC(3);
                    _os.writeC(power.getDollSleep());
                }
                if (power.getDollBlind() != 0) { // 暗黑耐性
                    _os.writeC(33);
                    _os.writeC(4);
                    _os.writeC(power.getDollBlind());
                }
                if (power.getDollStun() != 0) { // 昏迷耐性
                    _os.writeC(33);
                    _os.writeC(5);
                    _os.writeC(power.getDollStun());
                }
                if (power.getDollSustain() != 0) { // 支撐耐性
                    _os.writeC(33);
                    _os.writeC(6);
                    _os.writeC(power.getDollSustain());
                }
                if (power.getDollHaste() != 0) { // // 具備加速效果
                    _os.writeC(0x12);
                }
                if (power.getDollStunLv() != 0) { // 昏迷等級
                    _os.writeC(98);
                    _os.writeD(23521); // 昏迷等級提升 +%d
                    _os.writeH(power.getDollStunLv());
                }
                if (power.getDollBreakLv() != 0) { // 破壞等級提升
                    _os.writeC(98);
                    _os.writeD(11147); // 破壞盔甲命中 +%d--- 26608破壞等級 +3
                    _os.writeH(power.getDollBreakLv());
                }
                if (power.getDollFoeSlayer() != 0) { // 屠宰者階段別傷害
                    _os.writeC(101);
                    _os.writeC(power.getDollFoeSlayer());
                }
                if (power.getDollTiTanHp() != 0) { // 泰坦系列技能發動 HP 區間增加
                    _os.writeC(102);
                    _os.writeC(power.getDollTiTanHp());// 5%
                }
                if (power.get_note() != null && !power.get_note().isEmpty()) {
                    msg = power.get_note();
                    _os.writeC(39);
                    _os.writeS(msg);
                }
            }
        }
        if (doll.get_level() > 0) { // 娃娃等級
            //_os.writeC(0x27);
            //_os.writeS("娃娃等級 " + doll.get_level());
            _os.writeC(26); // desc-c.tbl -> 1184行
            _os.writeH(doll.get_level());
        }
        String classname = _item.getclassname();
        if (!classname.equalsIgnoreCase("doll.Magic_Doll_Power")) { // 祭司娃娃不顯示
            if (doll != null) {
                if (doll.get_need() != null) {
                    final int[] itemids = doll.get_need();
                    final int[] counts = doll.get_counts();
                    for (int i = 0; i < itemids.length; i++) {
                        if (itemids.length == 1 && itemids[i] == 41246) { // 魔法結晶體
                            _os.writeC(0x27);
                            //_os.writeS("需求魔法結晶體(" + counts[i] + ")個");
                            _os.writeS("需求$5240 " + counts[i]);
                        }
                    }
                }
            }
        }
        // 2017/04/21
        ArrayList<String> as = new ArrayList<>();
        try {
            for (String s : WilliamItemMessage.getItemInfo(_itemInstance)) {
                if (s != null && !s.isEmpty()) {
                    _os.writeC(39);
                    _os.writeS(s);
                }
            }
        } finally {
            as.clear();
        }
        this._os.writeC(0x17); // 材質
        this._os.writeC(this._item.getMaterial());
        this._os.writeD(this._itemInstance.getWeight());
		/*if (_statusx) {
			if (!_item.isTradable()) {
				_os.writeC(39);
				_os.writeS("無法交易");
			}
			if (_item.isCantDelete()) {
				_os.writeC(39);
				_os.writeS("無法刪除");
			}
			if (_item.get_safeenchant() < 0) {
				_os.writeC(39);
				_os.writeS("無法強化");
			}
		}*/
        showItemDelTimer();
        return _os;
    }

    /**
     * 道具狀態系統
     *
     */
    private BinaryOutputStream etcitem_Buff(L1ItemBuff power) {
        if (power.get_ac() != 0) { // 防禦
            _os.writeC(56);
            _os.writeC(power.get_ac());
        }
        if (power.get_str() != 0) { // 力量
            _os.writeC(8);
            _os.writeC(power.get_str());
        }
        if (power.get_dex() != 0) { // 敏捷
            _os.writeC(9);
            _os.writeC(power.get_dex());
        }
        if (power.get_con() != 0) { // 體質
            _os.writeC(10);
            _os.writeC(power.get_con());
        }
        if (power.get_wis() != 0) { // 精神
            _os.writeC(11);
            _os.writeC(power.get_wis());
        }
        if (power.get_intel() != 0) { // 智力
            _os.writeC(12);
            _os.writeC(power.get_intel());
        }
        if (power.get_cha() != 0) { // 魅力
            _os.writeC(13);
            _os.writeC(power.get_cha());
        }
        if (power.get_hp() != 0) { // 血量
            _os.writeC(14);
            _os.writeH(power.get_hp());
        }
        if (power.get_mp() != 0) { // 魔量
            _os.writeC(0x20);
            _os.writeH(power.get_mp());
        }
        if (power.get_mr() != 0) { // 魔法防禦
            _os.writeC(15);
            _os.writeH(power.get_mr());
        }
        if (power.get_sp() != 0) { // 魔攻
            _os.writeC(17);
            _os.writeC(power.get_sp());
        }
        if (power.get_dmg() != 0) { // 近戰傷害
            _os.writeC(47);
            _os.writeC(power.get_dmg());
        }
        if (power.get_bow_dmg() != 0) { // 遠攻傷害
            _os.writeC(35);
            _os.writeC(power.get_bow_dmg());
        }
        if (power.get_hit() != 0) { // 近戰命中
            _os.writeC(48);
            _os.writeC(power.get_hit());
        }
        if (power.get_bow_hit() != 0) { // 遠攻命中
            _os.writeC(24);
            _os.writeC(power.get_bow_hit());
        }
        if (power.get_dmg_r() != 0) { // 物理減傷
            _os.writeC(39);
            _os.writeS("物理減傷 +" + power.get_dmg_r());
        }
        if (power.get_magic_r() != 0) { // 魔法減傷
            _os.writeC(39);
            _os.writeS("魔法減傷 +" + power.get_magic_r());
        }
        if (power.get_fire() != 0) { // 火屬性
            _os.writeC(27);
            _os.writeC(power.get_fire());
        }
        if (power.get_water() != 0) { // 水屬性
            _os.writeC(28);
            _os.writeC(power.get_water());
        }
        if (power.get_wind() != 0) { // 風屬性
            _os.writeC(29);
            _os.writeC(power.get_wind());
        }
        if (power.get_earth() != 0) { // 地屬性
            _os.writeC(30);
            _os.writeC(power.get_earth());
        }
        if (power.get_freeze() != 0) { // 冰凍耐性
            _os.writeC(33);
            _os.writeC(1);
            _os.writeC(power.get_freeze());
        }
        if (power.get_stone() != 0) { // 石化耐性
            _os.writeC(33);
            _os.writeC(2);
            _os.writeC(power.get_stone());
        }
        if (power.get_sleep() != 0) { // 睡眠耐性
            _os.writeC(33);
            _os.writeC(3);
            _os.writeC(power.get_sleep());
        }
        if (power.get_blind() != 0) { // 暗黑耐性
            _os.writeC(33);
            _os.writeC(4);
            _os.writeC(power.get_blind());
        }
        if (power.get_stun() != 0) { // 昏迷耐性
            _os.writeC(33);
            _os.writeC(5);
            _os.writeC(power.get_stun());
        }
        if (power.get_sustain() != 0) { // 支撐耐性
            _os.writeC(33);
            _os.writeC(6);
            _os.writeC(power.get_sustain());
        }
        if (power.get_pvpdmg() != 0) { // 增加PVP傷害
            _os.writeC(59);
            _os.writeC(power.get_pvpdmg());
        }
        if (power.get_pvpdmg_r() != 0) { // 減免PVP傷害
            _os.writeC(60);
            _os.writeC(power.get_pvpdmg_r());
        }
        if (power.get_hpr() != 0) { // 體力回復量
            _os.writeC(37);
            _os.writeC(power.get_hpr());
        }
        if (power.get_mpr() != 0) { // 魔力回復量
            _os.writeC(38);
            _os.writeC(power.get_mpr());
        }
        if (power.get_exp() != 0) { // 狩獵經驗值
            if (power.get_exp() <= 120) {
                _os.writeC(36);
                _os.writeC(power.get_exp());
            } else {
                _os.writeC(0x27);
                _os.writeS("$6134 " + power.get_exp() + "%");
            }
        }
        if (power.get_gf() != 0) { // 金幣倍率
            _os.writeC(39);
            _os.writeS("金幣倍率 +" + power.get_gf() + "%");
        }
        boolean item = power.get_death_item(); // 死亡不噴道具
        if (item) {
            _os.writeC(39);
            _os.writeS("防噴道具");
        }
        boolean exp = power.get_death_exp(); // 死亡不噴經驗值
        if (exp) {
            _os.writeC(39);
            _os.writeS("防噴經驗");
        }
        boolean skill = power.get_death_skill(); // 死亡不噴技能
        if (skill) {
            _os.writeC(39);
            _os.writeS("防噴技能");
        }
        boolean score = power.get_death_score(); // 死亡不掉積分
        if (score) {
            _os.writeC(39);
            _os.writeS("防噴積分");
        }
        if (power.get_buff_time() != 0) {
            _os.writeC(39);
            _os.writeS("\\f3時效 " + power.get_buff_time() + "秒");
        }
        if (power.getVipLevel() != 0) {
            _os.writeC(39);
            _os.writeS("\\f3使用VIP " + power.getVipLevel());
        }
        if (power.is_buff_save()) {
            _os.writeC(39);
            _os.writeS("\\aE重登狀態保留");
        } else {
            _os.writeC(39);
            _os.writeS("\\aE重登狀態消失");
        }
        // 2017/04/21
        ArrayList<String> as = new ArrayList<>();
        try {
            for (String s : WilliamItemMessage.getItemInfo(_itemInstance)) {
                if (s != null && !s.isEmpty()) {
                    _os.writeC(39);
                    _os.writeS(s);
                }
            }
        } finally {
            as.clear();
        }
        this._os.writeC(0x17); // 材質
        this._os.writeC(this._item.getMaterial());
        this._os.writeD(this._itemInstance.getWeight());
        showItemDelTimer();
        return _os;
    }

    private BinaryOutputStream etcitem_card(String classname) {
        int card_id = 0;
        try {
            String cardmode = classname.substring(14);
            card_id = Integer.parseInt(cardmode);
        } catch (Exception e) {
            String cardmode = classname.substring(15);
            card_id = Integer.parseInt(cardmode);
        }
        if (card_id == 0) {
            return _os;
        }
        int freeze = 0;
        int stone = 0;
        int sleep = 0;
        int blind = 0;
        int stun = 0;
        int sustain = 0;
        int addstr = 0;
        int adddex = 0;
        int addcon = 0;
        int addwis = 0;
        int addint = 0;
        int addcha = 0;
        String msg1 = "";
        String msg2 = "";
        String msg3 = "";
        switch (card_id) {
            case 1:
                msg1 = "Exp +10%";
                msg2 = "死亡不會損失經驗值";
                stun = 3;
                freeze = 3;
                stone = 3;
                sleep = 3;
                break;
            case 2:
                msg1 = "Exp +20%";
                msg2 = "死亡不會損失積分";
                addstr = 1;
                adddex = 1;
                addcon = 1;
                addwis = 1;
                addint = 1;
                addcha = 1;
                break;
            case 3:
                msg1 = "Exp +30%";
                msg2 = "死亡不會掉落道具";
                addstr = 2;
                adddex = 2;
                addcon = 2;
                addwis = 2;
                addint = 2;
                addcha = 2;
                break;
            case 4:
                msg1 = "Exp +40%";
                msg2 = "$5539 +5";
                msg3 = "$5541 +5";
                addstr = 3;
                adddex = 3;
                addcon = 3;
                addwis = 3;
                addint = 3;
                addcha = 3;
                break;
            case 5:
                msg1 = "Exp +50%";
                msg2 = "$5539 +10";
                msg3 = "$5541 +10";
                addstr = 4;
                adddex = 4;
                addcon = 4;
                addwis = 4;
                addint = 4;
                addcha = 4;
        }
        if (msg1.length() > 0) {
            _os.writeC(39);
            _os.writeS(msg1);
        }
        if (msg2.length() > 0) {
            _os.writeC(39);
            _os.writeS(msg2);
        }
        if (msg3.length() > 0) {
            _os.writeC(39);
            _os.writeS(msg3);
        }
        if (addstr != 0) {
            _os.writeC(8);
            _os.writeC(addstr);
        }
        if (adddex != 0) {
            _os.writeC(9);
            _os.writeC(adddex);
        }
        if (addcon != 0) {
            _os.writeC(10);
            _os.writeC(addcon);
        }
        if (addwis != 0) {
            _os.writeC(11);
            _os.writeC(addwis);
        }
        if (addint != 0) {
            _os.writeC(12);
            _os.writeC(addint);
        }
        if (addcha != 0) {
            _os.writeC(13);
            _os.writeC(addcha);
        }
        if (freeze != 0) {
            _os.writeC(15);
            _os.writeH(freeze);
            _os.writeC(33);
            _os.writeC(1);
        }
        if (stone != 0) {
            _os.writeC(15);
            _os.writeH(stone);
            _os.writeC(33);
            _os.writeC(2);
        }
        if (sleep != 0) {
            _os.writeC(15);
            _os.writeH(sleep);
            _os.writeC(33);
            _os.writeC(3);
        }
        if (blind != 0) {
            _os.writeC(15);
            _os.writeH(blind);
            _os.writeC(33);
            _os.writeC(4);
        }
        if (stun != 0) {
            _os.writeC(15);
            _os.writeH(stun);
            _os.writeC(33);
            _os.writeC(5);
        }
        if (sustain != 0) {
            _os.writeC(15);
            _os.writeH(sustain);
            _os.writeC(33);
            _os.writeC(6);
        }
		/*if (_statusx) {
			if (!_item.isTradable()) {
				_os.writeC(39);
				_os.writeS("無法交易");
			}
			if (_item.isCantDelete()) {
				_os.writeC(39);
				_os.writeS("無法刪除");
			}
			if (_item.get_safeenchant() < 0) {
				_os.writeC(39);
				_os.writeS("無法強化");
			}
		}*/
        // 2017/04/21
        ArrayList<String> as = new ArrayList<>();
        try {
            for (String s : WilliamItemMessage.getItemInfo(_itemInstance)) {
                if (s != null && !s.isEmpty()) {
                    _os.writeC(39);
                    _os.writeS(s);
                }
            }
        } finally {
            as.clear();
        }
        showItemDelTimer();
        return _os;
    }

    /**
     * 飛刀 箭
     *
     */
    private BinaryOutputStream arrow() {
        _os.writeC(1);
        _os.writeC(_item.getDmgSmall());
        _os.writeC(_item.getDmgLarge());
        _os.writeC(_item.getMaterial());
        _os.writeD(_itemInstance.getWeight());
		/*if (_statusx) {
			if (!_item.isTradable()) {
				_os.writeC(39);
				_os.writeS("無法交易");
			}
			if (_item.isCantDelete()) {
				_os.writeC(39);
				_os.writeS("無法刪除");
			}
			if (_item.get_safeenchant() < 0) {
				_os.writeC(39);
				_os.writeS("無法強化");
			}
		}*/
        // 2017/04/21
        ArrayList<String> as = new ArrayList<>();
        try {
            for (String s : WilliamItemMessage.getItemInfo(_itemInstance)) {
                if (s != null && !s.isEmpty()) {
                    _os.writeC(39);
                    _os.writeS(s);
                }
            }
        } finally {
            as.clear();
        }
        return _os;
    }

    /**
     * 食物
     *
     */
    private BinaryOutputStream fooditem() {
        _os.writeC(21);
        // 榮養
        _os.writeH(_item.getFoodVolume());
        _os.writeC(_item.getMaterial());
        _os.writeD(_itemInstance.getWeight());
		/*if (_statusx) {
			if (!_item.isTradable()) {
				_os.writeC(39);
				_os.writeS("無法交易");
			}
			if (_item.isCantDelete()) {
				_os.writeC(39);
				_os.writeS("無法刪除");
			}
			if (_item.get_safeenchant() < 0) {
				_os.writeC(39);
				_os.writeS("無法強化");
			}
		}*/
        if (_item.getItemId() == 49825) { // 強壯的牛排
            _os.writeC(47); // 近距離攻擊+2
            _os.writeC(2);  // 近距離攻擊+2
            _os.writeC(48); // 近距離命中+1
            _os.writeC(1);  // 近距離命中+1
            _os.writeC(15); // 魔法防禦+10
            _os.writeH(10); // 魔法防禦+10
            _os.writeC(30); // 地屬性防禦+10
            _os.writeC(10); // 地屬性防禦+10
            _os.writeC(28); // 水屬性防禦+10
            _os.writeC(10); // 水屬性防禦+10
            _os.writeC(27); // 火屬性防禦+10
            _os.writeC(10); // 火屬性防禦+10
            _os.writeC(29); // 風屬性防禦+10
            _os.writeC(10); // 風屬性防禦+10
            _os.writeC(37); // 體力回復量+2
            _os.writeC(2);  // 體力回復量+2
            _os.writeC(38); // 魔力回復量+2
            _os.writeC(2);  // 魔力回復量+2
            _os.writeC(63); // 傷害減免+2
            _os.writeC(2);  // 傷害減免+2
            _os.writeC(39);
            _os.writeS("狩獵經驗值 x1.02");
            _os.writeC(39);
            _os.writeS("\\f3效果時間： 1800秒 ");
        }
        if (_item.getItemId() == 49826) { // 敏捷的煎鮭魚
            _os.writeC(35); // 遠距離攻擊+2
            _os.writeC(2);  // 遠距離攻擊+2
            _os.writeC(24); // 遠距離命中+1
            _os.writeC(1);  // 遠距離命中+1
            _os.writeC(15); // 魔法防禦+10
            _os.writeH(10); // 魔法防禦+10
            _os.writeC(30); // 地屬性防禦+10
            _os.writeC(10); // 地屬性防禦+10
            _os.writeC(28); // 水屬性防禦+10
            _os.writeC(10); // 水屬性防禦+10
            _os.writeC(27); // 火屬性防禦+10
            _os.writeC(10); // 火屬性防禦+10
            _os.writeC(29); // 風屬性防禦+10
            _os.writeC(10); // 風屬性防禦+10
            _os.writeC(37); // 體力回復量+2
            _os.writeC(2);  // 體力回復量+2
            _os.writeC(38); // 魔力回復量+2
            _os.writeC(2);  // 魔力回復量+2
            _os.writeC(63); // 傷害減免+2
            _os.writeC(2);  // 傷害減免+2
            _os.writeC(39);
            _os.writeS("狩獵經驗值 x1.02");
            _os.writeC(39);
            _os.writeS("\\f3效果時間： 1800秒 ");
        }
        if (_item.getItemId() == 49827) { // 炭烤的火雞
            _os.writeC(17); // 魔攻+2
            _os.writeC(2);  // 魔攻+2
            _os.writeC(15); // 魔法防禦+10
            _os.writeH(10); // 魔法防禦+10
            _os.writeC(30); // 地屬性防禦+10
            _os.writeC(10); // 地屬性防禦+10
            _os.writeC(28); // 水屬性防禦+10
            _os.writeC(10); // 水屬性防禦+10
            _os.writeC(27); // 火屬性防禦+10
            _os.writeC(10); // 火屬性防禦+10
            _os.writeC(29); // 風屬性防禦+10
            _os.writeC(10); // 風屬性防禦+10
            _os.writeC(37); // 體力回復量+2
            _os.writeC(2);  // 體力回復量+2
            _os.writeC(38); // 魔力回復量+3
            _os.writeC(3);  // 魔力回復量+3
            _os.writeC(63); // 傷害減免+2
            _os.writeC(2);  // 傷害減免+2
            _os.writeC(39);
            _os.writeS("狩獵經驗值 x1.02");
            _os.writeC(39);
            _os.writeS("\\f3效果時間： 1800秒 ");
        }
        // 2017/04/21
        ArrayList<String> as = new ArrayList<>();
        try {
            for (String s : WilliamItemMessage.getItemInfo(_itemInstance)) {
                if (s != null && !s.isEmpty()) {
                    _os.writeC(39);
                    _os.writeS(s);
                }
            }
        } finally {
            as.clear();
        }
        return _os;
    }
    // TODO 防具類

    /**
     * 照明道具
     *
     */
    private BinaryOutputStream lightitem() {
        _os.writeC(22);
        _os.writeH(_item.getLightRange());
        _os.writeC(_item.getMaterial());
        _os.writeD(_itemInstance.getWeight());
		/*if (_statusx) {
			if (!_item.isTradable()) {
				_os.writeC(39);
				_os.writeS("無法交易");
			}
			if (_item.isCantDelete()) {
				_os.writeC(39);
				_os.writeS("無法刪除");
			}
			if (_item.get_safeenchant() < 0) {
				_os.writeC(39);
				_os.writeS("無法強化");
			}
		}*/
        return _os;
    }
    // TODO 飾品類

    /**
     * 防具類
     *
     */
    private BinaryOutputStream armor() {
        // TODO 查修
        // 【最佳實踐】在方法的開頭進行 Null 檢查
        if (this._item == null) {
            // 記錄錯誤日誌，指出是哪個 L1ItemInstance 物件的模板為 null
            System.err.println(
                    "NullPointerException prevented: L1Item template is null for item instance objid: " +
                            _itemInstance.getId()
            );
            // 回傳一個空的或預設的資料流，避免伺服器崩潰
            return _os;
        }

        _os.writeC(19);
        int ac = _item.get_ac();
        if (ac < 0) {
            ac = Math.abs(ac);
        }
        _os.writeC(ac);
        _os.writeC(_item.getMaterial());
        _os.writeC(_item.get_greater());
        _os.writeD(_itemInstance.getWeight());
        if (_itemInstance.getEnchantLevel() != 0) {
            _os.writeC(2);
            _os.writeC(_itemInstance.getEnchantLevel());
        }
        if (_itemInstance.get_durability() != 0) {
            _os.writeC(3);
            _os.writeC(_itemInstance.get_durability());
        }
        int s6_1 = 0;
        int s6_2 = 0;
        int s6_3 = 0;
        int s6_4 = 0;
        int s6_5 = 0;
        int s6_6 = 0;
        int aH_1 = 0;
        int aM_1 = 0;
        int aMR_1 = 0;
        int aSP_1 = 0;
        int aSS_1 = 0;
        int d4_1 = 0;
        int d4_2 = 0;
        int d4_3 = 0;
        int d4_4 = 0;
        int k6_1 = 0;
        int k6_2 = 0;
        int k6_3 = 0;
        int k6_4 = 0;
        int k6_5 = 0;
        int k6_6 = 0;
        int aHpr = 0;
        int aMpr = 0;
        int admg = 0;
        int drd = 0;
        int mdmg = 0;
        int mdrd = 0;
        int bdmg = 0;
        int hit = 0;
        int bhit = 0;
        int mcri = 0;
        // 古文字顯示
        if (_itemInstance.get_power_name() != null && _itemInstance.get_power_name().get_power_id() > 0) {
            this._os.writeC(0x27);
            this._os.writeS(_itemInstance.get_power_name().get_power_name());
        }
        this._os.writeC(0x27);
        this._os.writeS("安定值: " + _item.get_safeenchant());
		/*final int oldEnchantLvl = _itemInstance.getEnchantLevel(); // 物品強化值
		final int safe_enchant = _item.get_safeenchant(); // 物品安定值
		int Reduce = 0; // 強化值減安定值
		if (oldEnchantLvl > safe_enchant) {
			Reduce = oldEnchantLvl - safe_enchant;
		}
		// 防具如果是祝福
		if (Reduce > 0) {
			//this._os.writeC(0x27);
			//this._os.writeS("減免傷害: +" + Reduce);
			_os.writeC(63);
			_os.writeC(Reduce);
		}*/
		/*int RatePlus = 0;
		String[] ratebyarmor = this._item.getclassname().split(" ");
		if ((ratebyarmor[0].equals("RatePlus")) && (Integer.valueOf(ratebyarmor[1]).intValue() > 0)) {
			RatePlus += Integer.valueOf(ratebyarmor[1]).intValue();
		}
		if (RatePlus != 0) {
			this._os.writeC(39);
			this._os.writeS("武器魔法發動率: +" + RatePlus + "%");
		}*/
        int RatePlus2 = 0;
        String[] ratebyarmor2 = this._item.getclassname().split(" ");
        if ((ratebyarmor2[0].equals("RatePlus")) && (Integer.parseInt(ratebyarmor2[2]) > 0)) {
            RatePlus2 += Integer.parseInt(ratebyarmor2[2]);
        }
        if (RatePlus2 != 0) {
            this._os.writeC(39);
            this._os.writeS("屬性發動率: +" + RatePlus2 + "%");
        }
		/*if (_itemInstance.isMatch()) {// 套裝效果
			s6_1 = _item.get_mode()[0];// 套裝效果:力量增加
			s6_2 = _item.get_mode()[1];// 套裝效果:敏捷增加
			s6_3 = _item.get_mode()[2];// 套裝效果:體質增加
			s6_4 = _item.get_mode()[3];// 套裝效果:精神增加
			s6_5 = _item.get_mode()[4];// 套裝效果:智力增加
			s6_6 = _item.get_mode()[5];// 套裝效果:魅力增加
			aH_1 = _item.get_mode()[6];// 套裝效果:HP增加
			aM_1 = _item.get_mode()[7];// 套裝效果:MP增加
			aMR_1 = _item.get_mode()[8];// 套裝效果:抗魔增加
			aSP_1 = _item.get_mode()[9];// SP(魔攻) XXX
			aSS_1 = _item.get_mode()[10];// 加速效果 XXX
			d4_1 = _item.get_mode()[11];// 套裝效果:火屬性增加
			d4_2 = _item.get_mode()[12];// 套裝效果:水屬性增加
			d4_3 = _item.get_mode()[13];// 套裝效果:風屬性增加
			d4_4 = _item.get_mode()[14];// 套裝效果:地屬性增加
			k6_1 = _item.get_mode()[15];// 套裝效果:寒冰耐性增加
			k6_2 = _item.get_mode()[16];// 套裝效果:石化耐性增加
			k6_3 = _item.get_mode()[17];// 套裝效果:睡眠耐性增加
			k6_4 = _item.get_mode()[18];// 套裝效果:暗闇耐性增加
			k6_5 = _item.get_mode()[19];// 套裝效果:暈眩耐性增加
			k6_6 = _item.get_mode()[20];// 套裝效果:支撐耐性增加
			aHpr = _item.get_mode()[21];// 套裝效果:回血量增加
			aMpr = _item.get_mode()[22];// 套裝效果:回魔量增加
			admg = _item.get_mode()[23];// 套裝效果:套裝增加物理傷害
			drd = _item.get_mode()[24];// 套裝效果:套裝減免物理傷害
			mdmg = _item.get_mode()[25];// 套裝效果:套裝增加魔法傷害
			mdrd = _item.get_mode()[26];// 套裝效果:套裝減免魔法傷害
			bdmg = _item.get_mode()[27];// 套裝效果:套裝增加弓的物理傷害
			hit = _item.get_mode()[28];// 套裝效果:套裝增加近距離命中率
			bhit = _item.get_mode()[29];// 套裝效果:套裝增加遠距離命中率
			mcri = _item.get_mode()[30];// 套裝效果:套裝增加魔法爆擊率
		}*/
        int pw_s1 = _item.get_addstr();
        int pw_s2 = _item.get_adddex();
        int pw_s3 = _item.get_addcon();
        int pw_s4 = _item.get_addwis();
        int pw_s5 = _item.get_addint();
        int pw_s6 = _item.get_addcha();
        int pw_sHp = _itemPower.get_addhp();
        int pw_sMp = _itemPower.get_addmp();
        int pw_sMr = _itemPower.getMr();
        int pw_sSp = _itemPower.getSp() + _itemInstance.getItemSp();
        int pw_sWr = _item.getWeightReduction(); // 防具負重顯示
        int pw_sDg = _item.getDmgModifierByArmor();
        int pw_sHi = _itemPower.getHitModifierByArmor();
        int pw_mHi = _item.getMagicHitModifierByArmor();
        int pw_dice_dmg = _item.get_dice_dmg();
        int pw_dmg = _item.getDmg();
        int pw_bDg = _item.getBowDmgModifierByArmor();
        int pw_bHi = _item.getBowHitModifierByArmor();
        int pw_d4_1 = _item.get_defense_fire();
        int pw_d4_2 = _item.get_defense_water();
        int pw_d4_3 = _item.get_defense_wind();
        int pw_d4_4 = _item.get_defense_earth();
        int pw_k6_1 = _item.get_regist_freeze();
        int pw_k6_2 = _item.get_regist_stone();
        int pw_k6_3 = _item.get_regist_sleep();
        int pw_k6_4 = _item.get_regist_blind();
        int pw_k6_5 = _item.get_regist_stun();
        int pw_k6_6 = _item.get_regist_sustain();
        int pw_sHpr = _item.get_addhpr();
        int pw_sMpr = _itemPower.getMpr();
        int addexp = _item.getExpPoint();
        int pw_drd = _itemPower.getDamageReduction();
        int uhp = _item.get_up_hp_potion();// 增加藥水回復量%
        int uhp_num = _item.get_uhp_number();// 增加藥水回復指定量
        int add_hit = pw_sHi + hit;// 近距離命中率
        if (add_hit != 0) {
            _os.writeC(48); // 近戰命中
            _os.writeC(add_hit);
        } else if (_item.getItemId() == 21309) {
            int value = (_itemInstance.getEnchantLevel() - 5) + 1;
            _os.writeC(48); // 近戰命中
            _os.writeC(value);
            // 激怒手套
            // 【+5】近距離命中+1
            // 【+6】近距離命中+2
            // 【+7】近距離命中+3
            // 【+8】近距離命中+4
            // 【+9】近距離命中+5
            // 【+10】近距離命中+6
            // 【+11】近距離命中+7
        }
        int add_sdg = pw_sDg + /* greater()[3] + */ admg + _itemInstance.getItemAttack();// 近距離傷害
        if (add_sdg != 0) {
            _os.writeC(47);
            _os.writeC(add_sdg);
        }
        int add_bowhit = pw_bHi + bhit;// 遠距離命中率
        if (add_bowhit != 0) {
            _os.writeC(24);
            _os.writeC(add_bowhit);
        }
        int add_bdg = pw_bDg + /* greater()[4] + */ bdmg + _itemInstance.getItemBowAttack();// 遠距離傷害
        if (add_bdg != 0) {
            _os.writeC(35);
            _os.writeC(add_bdg);
        }
        if (mdmg != 0) {// 魔法傷害
            _os.writeC(39);
            _os.writeS("魔法傷害 +" + mdmg);
        }
        if (mdrd != 0) {
            _os.writeC(39);
            _os.writeS("魔法傷害減免 +" + mdrd);
        }
        int bit = 0;
        bit |= (_item.isUseRoyal() ? 1 : 0);
        bit |= (_item.isUseKnight() ? 2 : 0);
        bit |= (_item.isUseElf() ? 4 : 0);
        bit |= (_item.isUseMage() ? 8 : 0);
        bit |= (_item.isUseDarkelf() ? 16 : 0);
        bit |= (_item.isUseDragonknight() ? 32 : 0);
        bit |= (_item.isUseIllusionist() ? 64 : 0);
        bit |= (_item.isUseWarrior() ? 128 : 0);
        _os.writeC(7);
        _os.writeC(bit);
        /*
         * int safeenchant = _item.get_safeenchant(); if (safeenchant >= 0) {
         * _os.writeC(39); _os.writeS("安定值: " + _item.get_safeenchant()); }
         */
        int addstr = pw_s1 + s6_1 + _itemInstance.getItemStr();//力量
        int adddex = pw_s2 + s6_2 + _itemInstance.getItemDex();//敏捷
        int addcon = pw_s3 + s6_3 + _itemInstance.getItemCon();//體質
        int addwis = pw_s4 + s6_4 + _itemInstance.getItemWis();
        ;//精神.
        int addint = pw_s5 + s6_5 + _itemInstance.getItemInt();//智力
        int addcha = pw_s6 + s6_6 + _itemInstance.getItemCha();
        ;//魅力
        if (addstr == 1 && adddex == 1 && addcon == 1 && addwis == 1 && addint == 1 && addcha == 1) {
            _os.writeC(39);
            _os.writeS("全能力值 +1");
        } else {
            if (addstr != 0) {
                _os.writeC(8);
                _os.writeC(addstr);
            }
            if (adddex != 0) {
                _os.writeC(9);
                _os.writeC(adddex);
            }
            if (addcon != 0) {
                _os.writeC(10);
                _os.writeC(addcon);
            }
            if (addwis != 0) {
                _os.writeC(11);
                _os.writeC(addwis);
            }
            if (addint != 0) {
                _os.writeC(12);
                _os.writeC(addint);
            }
            if (addcha != 0) {
                _os.writeC(13);
                _os.writeC(addcha);
            }
        }
        int addhp = pw_sHp + aH_1 /* + greater()[1] */;// 血量上限
        if (addhp != 0) {
            _os.writeC(14);
            _os.writeH(addhp);
        }
        int addmp = pw_sMp + aM_1 /* + greater()[0] */;// 魔量上限;
        if (addmp != 0) {
            _os.writeC(0x20);
            _os.writeH(addmp);
        }
        int addhpr = pw_sHpr + aHpr; // 體力回復量
        if (addhpr != 0) {
            _os.writeC(37);
            _os.writeC(addhpr);
        }
        int addmpr = pw_sMpr + aMpr; // 魔力回復量
        if (addmpr != 0) {
            _os.writeC(38);
            _os.writeC(addmpr);
        }
        int freeze = pw_k6_1 + k6_1; // 冰凍耐性
        if (freeze != 0) {
            _os.writeC(33);
            _os.writeC(1);
            _os.writeC(freeze);
        }
        int stone = pw_k6_2 + k6_2; // 石化耐性
        if (stone != 0) {
            _os.writeC(33);
            _os.writeC(2);
            _os.writeC(stone);
        }
        int sleep = pw_k6_3 + k6_3; // 睡眠耐性
        if (sleep != 0) {
            _os.writeC(33);
            _os.writeC(3);
            _os.writeC(sleep);
        }
        int blind = pw_k6_4 + k6_4; // 暗黑耐性
        if (blind != 0) {
            _os.writeC(33);
            _os.writeC(4);
            _os.writeC(blind);
        }
        int stun = pw_k6_5 + k6_5; // 昏迷耐性
        if (stun != 0) {
            _os.writeC(33);
            _os.writeC(5);
            _os.writeC(stun);
        }
        int sustain = pw_k6_6 + k6_6; // 支撐耐性
        if (sustain != 0) {
            _os.writeC(33);
            _os.writeC(6);
            _os.writeC(sustain);
        }
        int addmr = pw_sMr + aMR_1 /* + greater()[10] */; // 魔防
        if (addmr != 0) {
            _os.writeC(15);
            _os.writeH(addmr);
        }
        int addsp = pw_sSp + aSP_1 /* + greater()[9] */; // 魔攻
        if (addsp != 0) {
            _os.writeC(17);
            _os.writeC(addsp);
        }
        if (pw_sWr > 0) { // 防具負重
            //_os.writeC(0x5a); // 90 增加負重 +X
            //_os.writeH(pw_sWr);
            _os.writeC(68); // 68 負重增加率(%)
            _os.writeC(pw_sWr);
        }
        boolean haste = _item.isHasteItem();
        if (aSS_1 == 1) {
            haste = true;
        }
        if (haste) {
            _os.writeC(18);
        }
        int fire = pw_d4_1 + d4_1;
        if (fire != 0) {
            _os.writeC(27);
            _os.writeC(fire);
        }
        int water = pw_d4_2 + d4_2;
        if (water != 0) {
            _os.writeC(28);
            _os.writeC(water);
        }
        int wind = pw_d4_3 + d4_3;
        if (wind != 0) {
            _os.writeC(29);
            _os.writeC(wind);
        }
        int earth = pw_d4_4 + d4_4;
        if (earth != 0) {
            _os.writeC(30);
            _os.writeC(earth);
        }
        if (addexp != 0) { // 經驗加成 2017/0425
            if (addexp <= 120) {
                _os.writeC(36);
                _os.writeC(addexp);
            } else {
                this._os.writeC(39);
                this._os.writeS("$6134 " + addexp + "%");
            }
        }
        //int alldrd = pw_drd + drd /* + greater()[5] */;
        int alldrd = pw_drd + drd + _itemInstance.getItemReductionDmg(); // 傷害減免
        int d = 0;
        if (alldrd != 0) {
            _os.writeC(63);
            if (_item.getItemId() >= 21200 && _item.getItemId() <= 21203) {
                switch (_itemInstance.getEnchantLevel()) {
                    case 7:
                        d = 1;
                        break;
                    case 8:
                        d = 2;
                        break;
                    case 9:
                        d = 3;
                        break;
                    default:
                        break;
                }
            }
            _os.writeC(alldrd + d);
        }
        int allmHi = pw_mHi /* + greater()[6] */;
        if (allmHi != 0) {// 魔法命中
            //_os.writeC(39);
            //_os.writeS("魔法命中 +" + allmHi);
            _os.writeC(40);
            _os.writeC(allmHi);
        }
        if (mcri != 0) {
            _os.writeC(39);
            _os.writeS("魔法爆擊率 +" + mcri);
        }
        StringBuilder name = new StringBuilder();
        int adduhp = uhp /* + greater()[2] */;
        if (adduhp != 0) {// 增加藥水回復量%
            name.append("藥水回復量 +").append(adduhp).append("%");
            if (uhp_num != 0) {// 增加藥水回復指定量
                name.append("+").append(uhp_num);
            }
            _os.writeC(39);
            _os.writeS(name.toString());
        }
        int pvpDmg = 0/* +greater()[7] */;
        switch (_item.getItemId()) {
            case 21384:
            case 21385:
            case 21386:
                pvpDmg = 1;
                break;
            case 21387:
            case 21388:
            case 21389:
                pvpDmg = 2;
                break;
            case 21390:
            case 21391:
            case 21392:
                pvpDmg = 4;
                break;
            case 21393:
            case 21394:
            case 21395:
                pvpDmg = 5;
                break;
            default:
                break;
        }
        int addpvpdmg = pvpDmg + _item.getPvpDmg(); // 增加PVP傷害
        if (addpvpdmg != 0) {
            // _os.writeC(39);
            // _os.writeS("\\f2PVP 額外傷害 +" + addpvpdmg);
            _os.writeC(59);
            _os.writeC(addpvpdmg);
        }
        int pvpDmgdrd = 0 /* +greater()[11] */;
        switch (_item.getItemId()) {
            case 330012:
            case 330013:
            case 330014:
                pvpDmgdrd = 2;
                break;
            case 330015:
            case 330016:
            case 330017:
                pvpDmgdrd = 5;
                break;
            default:
                break;
        }
        int addpvpdmg_r = pvpDmgdrd + _item.getPvpDmg_R(); // 减免PVP傷害
        if (addpvpdmg_r != 0) {
            // _os.writeC(39);
            // _os.writeS("\\f2PVP 傷害減免 +" + pvpDmgdrd);
            _os.writeC(60);
            _os.writeC(addpvpdmg_r);
        }
        if ((_item.getclassname().equalsIgnoreCase("Venom_Resist")) || (_item.getclassname().equalsIgnoreCase("ElitePlateMail_Antharas"))) {
            //_os.writeC(39);
            //_os.writeS("防護中毒");
            _os.writeC(57); // 57 毒耐性
            _os.writeD(19128);
        }
        final L1ItemPower_bless bless = this._itemInstance.get_power_bless();
        if (bless != null) {
            StringBuilder stringBuilder = new StringBuilder();
            StringBuilder stringBuilder1 = new StringBuilder();
            if (bless.get_hole_count() > 0) {
                this._os.writeC(0x27);
                for (int i = 0; i < bless.get_hole_count(); i++) {
                    switch (i) {
                        case 0:
                            name.append(set_hole_name(bless.get_hole_1()));
                            break;
                        case 1:
                            name.append(set_hole_name(bless.get_hole_2()));
                            break;
                        case 2:
                            name.append(set_hole_name(bless.get_hole_3()));
                            break;
                        case 3:
                            name.append(set_hole_name(bless.get_hole_4()));
                            break;
                        case 4:
                            name.append(set_hole_name(bless.get_hole_5()));
                            break;
                    }
                }
                this._os.writeS(name.toString());
            }
            if ((bless.get_hole_str() != 0) || (bless.get_hole_dex() != 0) || (bless.get_hole_int() != 0) || (bless.get_hole_dmg() != 0) || (bless.get_hole_bowdmg() != 0) || (bless.get_hole_mcdmg() != 0)) {
                if (bless.get_hole_str() >= 0) {
                    stringBuilder.append("力+").append(bless.get_hole_str()).append(" ");
                } else {
                    stringBuilder.append("力").append(bless.get_hole_str()).append(" ");
                }
                if (bless.get_hole_dex() >= 0) {
                    stringBuilder.append("敏+").append(bless.get_hole_dex()).append(" ");
                } else {
                    stringBuilder.append("敏").append(bless.get_hole_dex()).append(" ");
                }
                if (bless.get_hole_int() >= 0) {
                    stringBuilder.append("智+").append(bless.get_hole_int()).append(" ");
                } else {
                    stringBuilder.append("智").append(bless.get_hole_int()).append(" ");
                }
                if (bless.get_hole_dmg() >= 0) {
                    stringBuilder1.append("近戰+").append(bless.get_hole_dmg()).append(" ");
                } else {
                    stringBuilder1.append("近戰").append(bless.get_hole_dmg()).append(" ");
                }
                if (bless.get_hole_bowdmg() >= 0) {
                    stringBuilder1.append("遠弓+").append(bless.get_hole_bowdmg()).append(" ");
                } else {
                    stringBuilder1.append("遠弓").append(bless.get_hole_bowdmg()).append(" ");
                }
                if (bless.get_hole_mcdmg() >= 0) {
                    stringBuilder1.append("魔法+").append(bless.get_hole_mcdmg()).append(" ");
                } else {
                    stringBuilder1.append("魔法").append(bless.get_hole_mcdmg()).append(" ");
                }
                // this._os.writeC(39);
                // this._os.writeS("祝福強化:");
                this._os.writeC(39);
                this._os.writeS(stringBuilder.toString().trim());
                this._os.writeC(39);
                this._os.writeS(stringBuilder1.toString().trim());
            }
        }
        // TODO PVP系統 防具減少傷害
		/*if (ConfigOther.PVP_ARMOR && _itemInstance.getItemId() >= 20001 && _itemInstance.getItemId() <= 5000000
				&& _itemInstance.getEnchantLevel() >= ConfigOther.PVP_plus2) {
			_os.writeC(60);
			_os.writeC(_itemInstance.getEnchantLevel() - ConfigOther.PVP_plus2 + 1);
		}* /
		if (pvpDmg != 0 || _itemInstance.getItemReductionDmg() !=0) {
			// _os.writeC(39);
			// _os.writeS("PVP傷害 +" + pvpDmg);
			_os.writeC(60);
			_os.writeC(pvpDmg + _itemInstance.getItemReductionDmg());
		}*/
        if (_itemInstance.getSublimation() != null) {
            CharItemSublimation sub = _itemInstance.getSublimation();
            String title = sub.get_item_name() != null ? sub.get_item_name() : "未命名";
            int level = sub.getLv();
            // 顯示名稱與等級
            _os.writeC(0x27);
            _os.writeS("\\f2昇華：" + title + " Lv." + level);
            // 顯示昇華能力細節（從資料表讀取）
            L1ItemSublimation data = ItemSublimationTable.get().getItemSublimation(sub);
            if (data != null) {
                if (data.getDmgChanceHp() > 0) {
                    _os.writeC(0x27);
                    _os.writeS("傷害機率 +" + data.getDmgChanceHp() + "%");
                }
                if (data.getDmgChanceMp() > 0) {
                    _os.writeC(0x27);
                    _os.writeS("傷害機率 +" + data.getDmgChanceMp() + "%");
                }
                if (data.isWithstandDmg()) {
                    _os.writeC(0x27);
                    _os.writeS("抵擋物理傷害");
                }
                if (data.isWithstandMagic()) {
                    _os.writeC(0x27);
                    _os.writeS("可抵擋魔法傷害");
                }
                if (data.isReturnDmg()) {
                    _os.writeC(0x27);
                    _os.writeS("反彈物理攻擊");
                }
                if (data.isReturnMagic()) {
                    _os.writeC(0x27);
                    _os.writeS("反彈魔法攻擊");
                }
                if (data.isReturnSkills()) {
                    _os.writeC(0x27);
                    _os.writeS("反彈技能效果");
                }
                if (data.getReturnChanceHp() > 0) {
                    _os.writeC(0x27);
                    _os.writeS("反彈 HP 傷害機率 +" + data.getReturnChanceHp() + "%");
                }
                if (data.getReturnChanceMp() > 0) {
                    _os.writeC(0x27);
                    _os.writeS("反彈 MP 傷害機率 +" + data.getReturnChanceMp() + "%");
                }
                if (data.getMessage() != null && !data.getMessage().isEmpty()) {
                    _os.writeC(0x27);
                    _os.writeS("" + data.getMessage());
                }
            }
        }
        final int updatepvpdmg = _itemInstance.getUpdatePVPdmg();// pvp攻擊
        if (updatepvpdmg != 0) {
            _os.writeC(0x27);
            _os.writeS("\\f2PVP 額外傷害 +" + updatepvpdmg);
            //_os.writeC(59);
            //_os.writeC(updatepvpdmg);
        }
        final int updatepvpdmg_r = _itemInstance.getUpdatePVPdmg_R();// pvp減免
        if (updatepvpdmg_r != 0) {
            _os.writeC(0x27);
            _os.writeS("\\f2PVP 傷害減免 +" + updatepvpdmg_r);
            //_os.writeC(60);
            //_os.writeC(updatepvpdmg_r);
        }
        final int updateac = _itemInstance.getUpdateAc();// 防禦
        if (updateac != 0) {
            _os.writeC(56); // 額外防禦
            _os.writeC(updateac);
        }
        final int updatedmg = _itemInstance.getUpdateDmgModifier();// 近戰攻擊
        if (updatedmg != 0) {
            _os.writeC(47);
            _os.writeC(updatedmg);
        }
        final int updatehit = _itemInstance.getUpdateHitModifier();// 近戰命中
        if (updatehit != 0) {
            _os.writeC(48);
            _os.writeC(updatehit);
        }
        final int updatebowdmg = _itemInstance.getUpdateBowDmgModifier();// 遠攻攻擊
        if (updatebowdmg != 0) {
            _os.writeC(35);
            _os.writeC(updatebowdmg);
        }
        final int updatebowhit = _itemInstance.getUpdateBowHitModifier();// 遠攻命中
        if (updatebowhit != 0) {
            _os.writeC(24);
            _os.writeC(updatebowhit);
        }
        final int updatestr = _itemInstance.getUpdateStr();// 力量
        if (updatestr != 0) {
            _os.writeC(8);
            _os.writeC(updatestr);
        }
        final int updatedex = _itemInstance.getUpdateDex();// 敏捷
        if (updatedex != 0) {
            _os.writeC(9);
            _os.writeC(updatedex);
        }
        final int updatecon = _itemInstance.getUpdateCon();// 體質
        if (updatecon != 0) {
            _os.writeC(10);
            _os.writeC(updatecon);
        }
        final int updatewis = _itemInstance.getUpdateWis();// 精神
        if (updatewis != 0) {
            _os.writeC(11);
            _os.writeC(updatewis);
        }
        final int updateint = _itemInstance.getUpdateInt();// 智力
        if (updateint != 0) {
            _os.writeC(12);
            _os.writeC(updateint);
        }
        final int updatecha = _itemInstance.getUpdateCha();// 魅力
        if (updatecha != 0) {
            _os.writeC(13);
            _os.writeC(updatecha);
        }
        final int updatehp = _itemInstance.getUpdateHp();// 血量
        if (updatehp != 0) {
            _os.writeC(14);
            _os.writeH(updatehp);
        }
        final int updatemp = _itemInstance.getUpdateMp();// 魔量
        if (updatemp != 0) {
            _os.writeC(0x20);
            _os.writeH(updatemp);
        }
        final int updatehpr = _itemInstance.getUpdateHpr();// 回血
        if (updatehpr != 0) {
            _os.writeC(37);
            _os.writeC(updatehpr);
        }
        final int updatempr = _itemInstance.getUpdateMpr();// 回魔
        if (updatempr != 0) {
            _os.writeC(38);
            _os.writeC(updatempr);
        }
        final int updatemr = _itemInstance.getUpdateMr();// 抗魔
        if (updatemr != 0) {
            _os.writeC(15);
            _os.writeH(updatemr);
        }
        final int updatesp = _itemInstance.getUpdateSp();// 魔法攻擊
        if (updatesp != 0) {
            _os.writeC(17);
            _os.writeC(updatesp);
        }
        final int updatefire = _itemInstance.getUpdateFire();// 火屬性
        if (updatefire != 0) {
            _os.writeC(27);
            _os.writeC(updatefire);
        }
        final int updatewater = _itemInstance.getUpdateWater();// 水屬性
        if (updatewater != 0) {
            _os.writeC(28);
            _os.writeC(updatewater);
        }
        final int updatewind = _itemInstance.getUpdateWind();// 風屬性
        if (updatewind != 0) {
            _os.writeC(29);
            _os.writeC(updatewind);
        }
        final int updateearth = _itemInstance.getUpdateEarth();// 地屬性
        if (updateearth != 0) {
            _os.writeC(30);
            _os.writeC(updateearth);
        }
        // 強化擴充能力 end
		/*if (_statusx) {
			if (!_item.isTradable()) {
				_os.writeC(39);
				_os.writeS("無法交易");
			}
			if (_item.isCantDelete()) {
				_os.writeC(39);
				_os.writeS("無法刪除");
			}
			if (_item.get_safeenchant() < 0) {
				_os.writeC(39);
				_os.writeS("無法強化");
			}
		}*/
        //if ((ArmorSetTable.get().checkArmorSet(_itemInstance.getItemId())) && (getQuality1() != null) && (!getQuality1().equals(""))) { // src008

        if ((ArmorSetTable.get().checkArmorSet(_itemInstance.getItemId()))
                && (_item.get_mode()[0] != 0  // 套裝效果:力量增加
                || _item.get_mode()[1] != 0  // 套裝效果:敏捷增加
                || _item.get_mode()[2] != 0  // 套裝效果:體質增加
                || _item.get_mode()[3] != 0  // 套裝效果:精神增加
                || _item.get_mode()[4] != 0  // 套裝效果:智力增加
                || _item.get_mode()[5] != 0  // 套裝效果:魅力增加
                || _item.get_mode()[6] != 0  // 套裝效果:HP增加
                || _item.get_mode()[7] != 0  // 套裝效果:MP增加
                || _item.get_mode()[8] != 0  // 套裝效果:抗魔增加
                || _item.get_mode()[9] != 0  // 套裝效果:魔攻
                || _item.get_mode()[10] != 0 // 套裝效果:加速效果
                || _item.get_mode()[11] != 0 // 套裝效果:火屬性增加
                || _item.get_mode()[12] != 0 // 套裝效果:水屬性增加
                || _item.get_mode()[13] != 0 // 套裝效果:風屬性增加
                || _item.get_mode()[14] != 0 // 套裝效果:地屬性增加
                || _item.get_mode()[15] != 0 // 套裝效果:寒冰耐性增加
                || _item.get_mode()[16] != 0 // 套裝效果:石化耐性增加
                || _item.get_mode()[17] != 0 // 套裝效果:睡眠耐性增加
                || _item.get_mode()[18] != 0 // 套裝效果:暗闇耐性增加
                || _item.get_mode()[19] != 0 // 套裝效果:暈眩耐性增加
                || _item.get_mode()[20] != 0 // 套裝效果:支撐耐性增加
                || _item.get_mode()[21] != 0 // 套裝效果:回血量增加
                || _item.get_mode()[22] != 0 // 套裝效果:回魔量增加
                || _item.get_mode()[23] != 0 // 套裝效果:套裝增加物理傷害
                || _item.get_mode()[24] != 0 // 套裝效果:套裝減免物理傷害
                || _item.get_mode()[25] != 0 // 套裝效果:套裝增加魔法傷害
                || _item.get_mode()[26] != 0 // 套裝效果:套裝減免魔法傷害
                || _item.get_mode()[27] != 0 // 套裝效果:套裝增加弓的物理傷害
                || _item.get_mode()[28] != 0 // 套裝效果:套裝增加近距離命中率
                || _item.get_mode()[29] != 0 // 套裝效果:套裝增加遠距離命中率
                || _item.get_mode()[30] != 0 // 套裝效果:套裝增加魔法爆擊率
                || _item.get_mode()[31] != 0 // 套裝效果:套裝增加防禦
                || _item.get_mode()[32] > 0  // 套裝效果:套裝變身
        )) {
            _os.writeC(69);
            if (isMatch()) {
                _os.writeC(1);
            } else {
                _os.writeC(2);
            }
            if (_item.get_mode()[32] > 0 && _item.get_mode()[33] > 0) { // 變身
                _os.writeC(71);
                _os.writeH(_item.get_mode()[33]);
            } else if (_item.get_mode()[32] > 0 && _item.get_mode()[33] <= 0) {
                _os.writeC(0x27);
                _os.writeS("變身效果");
            }
            if (_item.get_mode()[31] != 0) { // 防禦
                _os.writeC(56); // 額外防禦
                _os.writeC(_item.get_mode()[31]);
            }
            if (_item.get_mode()[0] != 0) { // 力量
                _os.writeC(0x08);
                _os.writeC(_item.get_mode()[0]);
            }
            if (_item.get_mode()[1] != 0) { // 敏捷
                _os.writeC(0x09);
                _os.writeC(_item.get_mode()[1]);
            }
            if (_item.get_mode()[2] != 0) { // 體質
                _os.writeC(0x0a);
                _os.writeC(_item.get_mode()[2]);
            }
            if (_item.get_mode()[3] != 0) { // 精神
                _os.writeC(0x0b);
                _os.writeC(_item.get_mode()[3]);
            }
            if (_item.get_mode()[4] != 0) { // 智力
                _os.writeC(0x0c);
                _os.writeC(_item.get_mode()[4]);
            }
            if (_item.get_mode()[5] != 0) { // 魅力
                _os.writeC(0x0d);
                _os.writeC(_item.get_mode()[5]);
            }
            if (_item.get_mode()[6] != 0) { // 血量上限
                _os.writeC(0x0e);
                _os.writeH(_item.get_mode()[6]);
            }
            if (_item.get_mode()[7] != 0) { // 魔量上限
                _os.writeC(0x20);
                _os.writeH(_item.get_mode()[7]);
            }
            if (_item.get_mode()[8] != 0) { // 抗魔
                _os.writeC(0x0f);
                _os.writeH(_item.get_mode()[8]);
            }
            if (_item.get_mode()[9] != 0) { // 魔攻
                _os.writeC(0x11);
                _os.writeC(_item.get_mode()[9]);
            }
            if (_item.get_mode()[10] != 0) { // 加速效果
                _os.writeC(0x12);
            }
            if (_item.get_mode()[11] != 0) { // 火屬性
                _os.writeC(0x1b);
                _os.writeC(_item.get_mode()[11]);
            }
            if (_item.get_mode()[12] != 0) { // 水屬性
                _os.writeC(0x1c);
                _os.writeC(_item.get_mode()[12]);
            }
            if (_item.get_mode()[13] != 0) { // 風屬性
                _os.writeC(0x1d);
                _os.writeC(_item.get_mode()[13]);
            }
            if (_item.get_mode()[14] != 0) { // 地屬性
                _os.writeC(0x1e);
                _os.writeC(_item.get_mode()[14]);
            }
            if (_item.get_mode()[15] != 0) { // 寒冰耐性
                _os.writeC(33);
                _os.writeC(1);
                _os.writeC(_item.get_mode()[15]);
            }
            if (_item.get_mode()[16] != 0) { // 石化耐性
                _os.writeC(33);
                _os.writeC(2);
                _os.writeC(_item.get_mode()[16]);
            }
            if (_item.get_mode()[17] != 0) { // 睡眠耐性
                _os.writeC(33);
                _os.writeC(3);
                _os.writeC(_item.get_mode()[17]);
            }
            if (_item.get_mode()[18] != 0) { // 暗黑耐性
                _os.writeC(33);
                _os.writeC(4);
                _os.writeC(_item.get_mode()[18]);
            }
            if (_item.get_mode()[19] != 0) { // 昏迷耐性
                _os.writeC(33);
                _os.writeC(5);
                _os.writeC(_item.get_mode()[19]);
            }
            if (_item.get_mode()[20] != 0) { // 支撐耐性
                _os.writeC(33);
                _os.writeC(6);
                _os.writeC(_item.get_mode()[20]);
            }
            if (_item.get_mode()[21] != 0) { // 體力回復量
                _os.writeC(37);
                _os.writeC(_item.get_mode()[21]);
            }
            if (_item.get_mode()[22] != 0) { // 魔力回復量
                _os.writeC(38);
                _os.writeC(_item.get_mode()[22]);
            }
            if (_item.get_mode()[24] != 0) { // 減免物理傷害 【傷害減免】
                _os.writeC(63);
                _os.writeC(_item.get_mode()[24]);
            }
            if (_item.get_mode()[23] != 0) { // 增加物理傷害 【近距離傷害】
                _os.writeC(47);
                _os.writeC(_item.get_mode()[23]);
            }
            if (_item.get_mode()[28] != 0) { // 增加近距離命中率
                _os.writeC(48);
                _os.writeC(_item.get_mode()[28]);
            }
            if (_item.get_mode()[27] != 0) { // 增加弓的物理傷害 【遠距離傷害】
                _os.writeC(35);
                _os.writeC(_item.get_mode()[27]);
            }
            if (_item.get_mode()[29] != 0) { // 增加遠距離命中率
                _os.writeC(24);
                _os.writeC(_item.get_mode()[29]);
            }
            if (_item.get_mode()[25] != 0) { // 增加魔法傷害
                _os.writeC(39);
                _os.writeS("魔法增傷 +" + _item.get_mode()[25]);
            }
            if (_item.get_mode()[26] != 0) { // 減免魔法傷害
                _os.writeC(39);
                _os.writeS("魔法減傷 +" + _item.get_mode()[26]);
            }
            if (_item.get_mode()[30] != 0) { // 增加魔法爆擊率
                _os.writeC(39);
                _os.writeS("魔法爆擊 +" + _item.get_mode()[30]);
            }
            if ((getQuality1() != null) && (!getQuality1().equals(""))) {
                _os.writeC(39);
                _os.writeS(getQuality1());
            }
            if ((getQuality2() != null) && (!getQuality2().equals(""))) {
                _os.writeC(39);
                _os.writeS(getQuality2());
            }
            if ((getQuality3() != null) && (!getQuality3().equals(""))) {
                _os.writeC(39);
                _os.writeS(getQuality3());
            }
            if ((getQuality4() != null) && (!getQuality4().equals(""))) {
                _os.writeC(39);
                _os.writeS(getQuality4());
            }
            if ((getQuality5() != null) && (!getQuality5().equals(""))) {
                _os.writeC(39);
                _os.writeS(getQuality5());
            }
            if ((getQuality6() != null) && (!getQuality6().equals(""))) {
                _os.writeC(39);
                _os.writeS(getQuality6());
            }
            if ((getQuality7() != null) && (!getQuality7().equals(""))) {
                _os.writeC(39);
                _os.writeS(getQuality7());
            }
            if ((getQuality8() != null) && (!getQuality8().equals(""))) {
                _os.writeC(39);
                _os.writeS(getQuality8());
            }
            if ((getQuality9() != null) && (!getQuality9().equals(""))) {
                _os.writeC(39);
                _os.writeS(getQuality9());
            }
            _os.writeC(69);
            _os.writeC(0);
        }
        /**祝福化說明系統 台灣JAVA技術老爹製作 LINE:@422ygvzx*/
        if (_itemInstance.getBless() == 0) {
            int item = _item.getItemId();
            L1Zhufu zhufu = Zhufu.getInstance().getTemplate(item, 2);
            if (zhufu == null) {
                zhufu = Zhufu.getInstance().getTemplateByType(_item.getType(), 2);
            }
            if (zhufu != null) {
                _os.writeC(39);
                _os.writeS("祝福化屬性：");
                if (zhufu.getAddStr() != 0) {
                    _os.writeC(39);
                    _os.writeS("力量：+" + zhufu.getAddStr());
                }
                if (zhufu.getAddDex() != 0) {
                    _os.writeC(39);
                    _os.writeS("敏捷：+" + zhufu.getAddDex());
                }
                if (zhufu.getAddCon() != 0) {
                    _os.writeC(39);
                    _os.writeS("體質：+" + zhufu.getAddCon());
                }
                if (zhufu.getAddInt() != 0) {
                    _os.writeC(39);
                    _os.writeS("智力：+" + zhufu.getAddInt());
                }
                if (zhufu.getAddWis() != 0) {
                    _os.writeC(39);
                    _os.writeS("精神：+" + zhufu.getAddWis());
                }
                if (zhufu.getAddCha() != 0) {
                    _os.writeC(39);
                    _os.writeS("魅力：+" + zhufu.getAddCha());
                }
                if (zhufu.getAddAc() != 0) {
                    _os.writeC(39);
                    _os.writeS("防禦：+" + zhufu.getAddAc());
                }
                if (zhufu.getAddMaxHp() != 0) {
                    _os.writeC(39);
                    _os.writeS("HP：+" + zhufu.getAddMaxHp());
                }
                if (zhufu.getAddMaxMp() != 0) {
                    _os.writeC(39);
                    _os.writeS("MP：+" + zhufu.getAddMaxMp());
                }
                if (zhufu.getAddHpr() != 0) {
                    _os.writeC(39);
                    _os.writeS("回血：+" + zhufu.getAddHpr());
                }
                if (zhufu.getAddMpr() != 0) {
                    _os.writeC(39);
                    _os.writeS("回魔：+" + zhufu.getAddMpr());
                }
                if (zhufu.getAddDmg() != 0) {
                    _os.writeC(39);
                    _os.writeS("近距離傷害：+" + zhufu.getAddDmg());
                }
                if (zhufu.getAddHit() != 0) {
                    _os.writeC(39);
                    _os.writeS("近距離命中：+" + zhufu.getAddHit());
                }
                if (zhufu.getAddBowDmg() != 0) {
                    _os.writeC(39);
                    _os.writeS("遠距離傷害：+" + zhufu.getAddBowDmg());
                }
                if (zhufu.getAddBowHit() != 0) {
                    _os.writeC(39);
                    _os.writeS("遠距離命中：+" + zhufu.getAddBowHit());
                }
                if (zhufu.getReduction_dmg() != 0) {
                    _os.writeC(39);
                    _os.writeS("物理傷害減免：+" + zhufu.getReduction_dmg());
                }
                if (zhufu.getAddMr() != 0) {
                    _os.writeC(39);
                    _os.writeS("抗魔：+" + zhufu.getAddMr());
                }
                if (zhufu.getAddSp() != 0) {
                    _os.writeC(39);
                    _os.writeS("魔攻：+" + zhufu.getAddSp());
                }
                if (zhufu.getAddFire() != 0) {
                    _os.writeC(39);
                    _os.writeS("抗火屬性：+" + zhufu.getAddFire());
                }
                if (zhufu.getAddWind() != 0) {
                    _os.writeC(39);
                    _os.writeS("抗風屬性：+" + zhufu.getAddWind());
                }
                if (zhufu.getAddEarth() != 0) {
                    _os.writeC(39);
                    _os.writeS("抗地屬性：+" + zhufu.getAddEarth());
                }
                if (zhufu.getAddWater() != 0) {
                    _os.writeC(39);
                    _os.writeS("抗水屬性：+" + zhufu.getAddWater());
                }
                if (zhufu.getwljm() != 0) {
                    _os.writeC(39);
                    _os.writeS("PVP傷害減免：+" + zhufu.getwljm());
                }
                if (zhufu.getwljmbai() != 0) {
                    _os.writeC(39);
                    _os.writeS("PVP傷害減免：+" + zhufu.getwljmbai() + "%");
                }
                if (zhufu.getmojm() != 0) {
                    _os.writeC(39);
                    _os.writeS("PVP魔法傷害減免：+" + zhufu.getmojm());
                }
                if (zhufu.getmojmbai() != 0) {
                    _os.writeC(39);
                    _os.writeS("PVP魔法傷害減免：+" + zhufu.getmojmbai() + "%");
                }
            }
        }
        /**祝福化說明系統 製作 QQ263075225*/
        // 顯示特殊屬性物品額外名稱
        L1ItemSpecialAttributeChar attr_char = _itemInstance.get_ItemAttrName();
        if (attr_char != null) {
            L1ItemSpecialAttribute attr = ItemSpecialAttributeTable.get().getAttrId(attr_char.get_attr_id());
            if (attr != null) {
                _os.writeC(39);
                _os.writeS("【炫色屬性】");
                if (attr.get_add_cha() != 0) {
                    _os.writeC(39);
                    _os.writeS("魅力 + " + attr.get_add_cha());
                }
                if (attr.get_add_con() != 0) {
                    _os.writeC(39);
                    _os.writeS("體質 + " + attr.get_add_con());
                }
                if (attr.get_add_dex() != 0) {
                    _os.writeC(39);
                    _os.writeS("敏捷 + " + attr.get_add_dex());
                }
                if (attr.get_add_str() != 0) {
                    _os.writeC(39);
                    _os.writeS("力量 + " + attr.get_add_str());
                }
                if (attr.get_add_wis() != 0) {
                    _os.writeC(39);
                    _os.writeS("精神 + " + attr.get_add_wis());
                }
                if (attr.get_add_int() != 0) {
                    _os.writeC(39);
                    _os.writeS("智力 + " + attr.get_add_int());
                }
                if (attr.get_add_drain_max_hp() != 0) {
                    _os.writeC(39);
                    _os.writeS("炫色吸取體力");
                }
                if (attr.get_add_drain_max_mp() != 0) {
                    _os.writeC(39);
                    _os.writeS("炫色吸取魔力");
                }
                if (attr.get_add_hp() != 0) {
                    _os.writeC(39);
                    _os.writeS("Hp + " + attr.get_add_hp());
                }
                if (attr.get_add_mp() != 0) {
                    _os.writeC(39);
                    _os.writeS("Mp + " + attr.get_add_mp());
                }
                if (attr.get_add_hpr() != 0) {
                    _os.writeC(39);
                    _os.writeS("體力回復 + " + attr.get_add_hpr());
                }
                if (attr.get_add_mpr() != 0) {
                    _os.writeC(39);
                    _os.writeS("魔力回復 + " + attr.get_add_mpr());
                }
                if (attr.get_add_m_def() != 0) {
                    _os.writeC(39);
                    _os.writeS("魔防 + " + attr.get_add_m_def());
                }
                if (attr.get_add_sp() != 0) {
                    _os.writeC(39);
                    _os.writeS("魔攻 + " + attr.get_add_sp());
                }
                if (attr.get_add_skill_dmg() != 0) {
                    _os.writeC(39);
                    _os.writeS("魔法傷害 + " + attr.get_add_skill_dmg());
                }
                if (attr.get_dmg_large() != 0) {
                    _os.writeC(39);
                    _os.writeS("最大攻擊力 + " + attr.get_dmg_large());
                }
                if (attr.get_dmg_small() != 0) {
                    _os.writeC(39);
                    _os.writeS("最小攻擊力 + " + attr.get_dmg_small());
                }
                if (attr.get_dmgmodifier() != 0) {
                    _os.writeC(39);
                    _os.writeS("額外攻擊力 + " + attr.get_dmgmodifier());
                }
                if (attr.get_hitmodifier() != 0) {
                    _os.writeC(39);
                    _os.writeS("命中 + " + attr.get_hitmodifier());
                }
                if (attr.getShanghaijianmian() != 0) {
                    _os.writeC(39);
                    _os.writeS("傷害減免 + " + attr.getShanghaijianmian());
                }
            }
        }
        //		ArmorEnchantSystem AE_List = ArmorEnchantSystem.get().get(this._itemInstance.getItemId(), _itemInstance.getEnchantLevel() - _item.get_safeenchant());
        //		//ArmorEnchantSystem AE_List = ArmorEnchantSystem.get().get(this._itemInstance.getItemId(), enchantLevel);
        //
        //		if (AE_List != null && _item.get_safeenchant() >= 0/**安定值-1 沒效果*/) {
        //			_os.writeC(0x27);
        //			_os.writeS("強化加成:");
        //			if (AE_List.getHp() != 0) {
        //				_os.writeC(0x27);
        //				_os.writeS("HP+"+ AE_List.getHp());
        //			}
        //			if (AE_List.getMp() != 0) {
        //				_os.writeC(0x27);
        //				_os.writeS("MP+"+ AE_List.getMp());
        //			}
        //			if (AE_List.getStr() != 0) {
        //				_os.writeC(0x27);
        //				_os.writeS("力量+"+ AE_List.getStr());
        //			}
        //			if (AE_List.getDex() != 0) {
        //				_os.writeC(0x27);
        //				_os.writeS("敏捷+"+ AE_List.getDex());
        //			}
        //			if (AE_List.getInt() != 0) {
        //				_os.writeC(0x27);
        //				_os.writeS("智力+"+ AE_List.getInt());
        //			}
        //			if (AE_List.getCon() != 0) {
        //				_os.writeC(0x27);
        //				_os.writeS("體質+"+ AE_List.getCon());
        //			}
        //			if (AE_List.getWis() != 0) {
        //				_os.writeC(0x27);
        //				_os.writeS("精神+"+ AE_List.getWis());
        //			}
        //			if (AE_List.getCha() != 0) {
        //				_os.writeC(0x27);
        //				_os.writeS("魅力+"+ AE_List.getCha());
        //			}
        //			if (AE_List.getAc() != 0) {
        //				_os.writeC(0x27);
        //				_os.writeS("防禦+"+ AE_List.getAc());
        //			}
        //
        //			if (AE_List.getDmgR() != 0) {
        //				_os.writeC(0x27);
        //				_os.writeS("傷害減免+"+ AE_List.getDmgR());
        //			}    //
        //
        //		}
        ArmorEnchantSystem AE_List2 = ArmorEnchantSystem.get().get2(_itemInstance.getSafeEnchantLevel(), _itemInstance.getEnchantLevel());
        //ArmorEnchantSystem AE_List2 = ArmorEnchantSystem.get().get2(es);
        if (AE_List2 != null && _item.get_safeenchant() >= 0/**安定值-1 沒效果*/) {
            _os.writeC(0x27);
            _os.writeS("防具加成:");
            if (AE_List2.getHp() != 0) {
                _os.writeC(0x27);
                _os.writeS("HP+" + AE_List2.getHp());
            }
            if (AE_List2.getMp() != 0) {
                _os.writeC(0x27);
                _os.writeS("MP+" + AE_List2.getMp());
            }
            if (AE_List2.getStr() != 0) {
                _os.writeC(0x27);
                _os.writeS("力量+" + AE_List2.getStr());
            }
            if (AE_List2.getDex() != 0) {
                _os.writeC(0x27);
                _os.writeS("敏捷+" + AE_List2.getDex());
            }
            if (AE_List2.getInt() != 0) {
                _os.writeC(0x27);
                _os.writeS("智力+" + AE_List2.getInt());
            }
            if (AE_List2.getCon() != 0) {
                _os.writeC(0x27);
                _os.writeS("體質+" + AE_List2.getCon());
            }
            if (AE_List2.getWis() != 0) {
                _os.writeC(0x27);
                _os.writeS("精神+" + AE_List2.getWis());
            }
            if (AE_List2.getCha() != 0) {
                _os.writeC(0x27);
                _os.writeS("魅力+" + AE_List2.getCha());
            }
            if (AE_List2.getAc() != 0) {
                _os.writeC(0x27);
                _os.writeS("防禦+" + AE_List2.getAc());
            }
            if (AE_List2.getDmgR() != 0) {
                _os.writeC(0x27);
                _os.writeS("傷害減免+" + AE_List2.getDmgR());
            }    //
        }
        // 2017/04/21
        ArrayList<String> as = new ArrayList<>();
        try {
            for (String s : WilliamItemMessage.getItemInfo(_itemInstance)) {
                if (s != null && !s.isEmpty()) {
                    _os.writeC(39);
                    _os.writeS(s);
                }
            }
        } finally {
            as.clear();
        }
        String[] byarmor = this._item.getclassname().split(" "); // SRC0711
        if (byarmor[0].equals("SkillByArmor")) {
            int bonus2 = this._itemInstance.getEnchantLevel() - this._item.get_safeenchant();
            if (bonus2 <= 0) {
                bonus2 = 0;
            }
            if (bonus2 >= 0) {
                this._os.writeC(39);
                this._os.writeS("目前+" + this._itemInstance.getEnchantLevel() + " " + (bonus2 + Integer.parseInt(byarmor[1])) + "%機率 發動" + byarmor[4]);
                this._os.writeC(39);
                this._os.writeS("傷害 " + Integer.valueOf(byarmor[3]));
            }
        }
        if (pw_dice_dmg != 0) {
            this._os.writeC(39);
            this._os.writeS("爆擊機率 " + pw_dice_dmg + "%");
        }
        if (pw_dmg != 0) {
            this._os.writeC(39);
            this._os.writeS("爆擊傷害 " + pw_dmg);
        }
        showItemDelTimer();
        checkArmorSet(); // 套裝能力顯示
        return _os;
    }

    /**
     * 飾品類
     *
     */
    private BinaryOutputStream accessories() {
        _os.writeC(19);
        int ac = _item.get_ac() + greater()[8];
        if (ac < 0) {
            ac = Math.abs(ac);
        }
        _os.writeC(ac);
        _os.writeC(_item.getMaterial());
        _os.writeC(-1);
        _os.writeD(_itemInstance.getWeight());
        if (_item.get_greater() == 0) {// 0:耐性(耳環/項鏈)
            _os.writeC(0x43); // 特性：耐性
            _os.writeC(0x2b); // 特性：耐性
        } else if (_item.get_greater() == 1) {// 1:熱情(戒指)
            _os.writeC(0x43); // 特性：熱情
            _os.writeC(0x2c); // 特性：熱情
        } else if (_item.get_greater() == 2) {// 2:意志(皮帶)
            _os.writeC(0x43); // 特性：意志
            _os.writeC(0x2d); // 特性：意志
        }
        int s6_1 = 0;
        int s6_2 = 0;
        int s6_3 = 0;
        int s6_4 = 0;
        int s6_5 = 0;
        int s6_6 = 0;
        int aH_1 = 0;
        int aM_1 = 0;
        int aMR_1 = 0;
        int aSP_1 = 0;
        int aSS_1 = 0;
        int d4_1 = 0;
        int d4_2 = 0;
        int d4_3 = 0;
        int d4_4 = 0;
        int k6_1 = 0;
        int k6_2 = 0;
        int k6_3 = 0;
        int k6_4 = 0;
        int k6_5 = 0;
        int k6_6 = 0;
        int aHpr = 0;
        int aMpr = 0;
        int admg = 0;
        int drd = 0;
        int mdmg = 0;
        int mdrd = 0;
        int bdmg = 0;
        int hit = 0;
        int bhit = 0;
        int mcri = 0;
		/*if (_itemInstance.isMatch()) {// 套裝效果
			s6_1 = _item.get_mode()[0];// 套裝效果:力量增加
			s6_2 = _item.get_mode()[1];// 套裝效果:敏捷增加
			s6_3 = _item.get_mode()[2];// 套裝效果:體質增加
			s6_4 = _item.get_mode()[3];// 套裝效果:精神增加
			s6_5 = _item.get_mode()[4];// 套裝效果:智力增加
			s6_6 = _item.get_mode()[5];// 套裝效果:魅力增加
			aH_1 = _item.get_mode()[6];// 套裝效果:HP增加
			aM_1 = _item.get_mode()[7];// 套裝效果:MP增加
			aMR_1 = _item.get_mode()[8];// 套裝效果:抗魔增加
			aSP_1 = _item.get_mode()[9];// SP(魔攻) XXX
			aSS_1 = _item.get_mode()[10];// 加速效果 XXX
			d4_1 = _item.get_mode()[11];// 套裝效果:火屬性增加
			d4_2 = _item.get_mode()[12];// 套裝效果:水屬性增加
			d4_3 = _item.get_mode()[13];// 套裝效果:風屬性增加
			d4_4 = _item.get_mode()[14];// 套裝效果:地屬性增加
			k6_1 = _item.get_mode()[15];// 套裝效果:寒冰耐性增加
			k6_2 = _item.get_mode()[16];// 套裝效果:石化耐性增加
			k6_3 = _item.get_mode()[17];// 套裝效果:睡眠耐性增加
			k6_4 = _item.get_mode()[18];// 套裝效果:暗闇耐性增加
			k6_5 = _item.get_mode()[19];// 套裝效果:暈眩耐性增加
			k6_6 = _item.get_mode()[20];// 套裝效果:支撐耐性增加
			aHpr = _item.get_mode()[21];// 套裝效果:回血量增加
			aMpr = _item.get_mode()[22];// 套裝效果:回魔量增加
			admg = _item.get_mode()[23];// 套裝效果:套裝增加物理傷害
			drd = _item.get_mode()[24];// 套裝效果:套裝減免物理傷害
			mdmg = _item.get_mode()[25];// 套裝效果:套裝增加魔法傷害
			mdrd = _item.get_mode()[26];// 套裝效果:套裝減免魔法傷害
			bdmg = _item.get_mode()[27];// 套裝效果:套裝增加弓的物理傷害
			hit = _item.get_mode()[28];// 套裝效果:套裝增加近距離命中率
			bhit = _item.get_mode()[29];// 套裝效果:套裝增加遠距離命中率
			mcri = _item.get_mode()[30];// 套裝效果:套裝增加魔法爆擊率
		}*/
        int pw_s1 = _item.get_addstr() + _itemInstance.getItemStr();
        int pw_s2 = _item.get_adddex() + _itemInstance.getItemDex();
        int pw_s3 = _item.get_addcon() + _itemInstance.getItemCon();
        int pw_s4 = _item.get_addwis() + _itemInstance.getItemWis();
        int pw_s5 = _item.get_addint() + _itemInstance.getItemInt();
        int pw_s6 = _item.get_addcha() + _itemInstance.getItemCha();
        int pw_sHp = _itemPower.get_addhp();
        int pw_sMp = _itemPower.get_addmp();
        int pw_sMr = _itemPower.getMr();
        int pw_sSp = _itemPower.getSp() + _itemInstance.getItemSp();
        int pw_sWr = _item.getWeightReduction(); // 飾品負重顯示
        int pw_sDg = _item.getDmgModifierByArmor();
        int pw_sHi = _itemPower.getHitModifierByArmor();
        int pw_mHi = _item.getMagicHitModifierByArmor();
        int pw_bDg = _item.getBowDmgModifierByArmor();
        int pw_bHi = _item.getBowHitModifierByArmor();
        int pw_d4_1 = _item.get_defense_fire();
        int pw_d4_2 = _item.get_defense_water();
        int pw_d4_3 = _item.get_defense_wind();
        int pw_d4_4 = _item.get_defense_earth();
        int pw_k6_1 = _item.get_regist_freeze();
        int pw_k6_2 = _item.get_regist_stone();
        int pw_k6_3 = _item.get_regist_sleep();
        int pw_k6_4 = _item.get_regist_blind();
        int pw_k6_5 = _item.get_regist_stun();
        int pw_k6_6 = _item.get_regist_sustain();
        int pw_sHpr = _item.get_addhpr();
        int pw_sMpr = _itemPower.getMpr();
        int addexp = _item.getExpPoint();
        int pw_drd = _itemPower.getDamageReduction();
        int uhp = _itemPower.getUhp();// 增加藥水回復量%
        int uhp_num = _itemPower.getUhp_NUM();// 增加藥水回復指定量
        int add_hit = pw_sHi + hit;// 近距離命中率
        if (add_hit != 0) {
            _os.writeC(48);
            _os.writeC(add_hit);
        }
        // 古文字顯示
        if (_itemInstance.get_power_name() != null && _itemInstance.get_power_name().get_power_id() > 0) {
            this._os.writeC(0x27);
            this._os.writeS(_itemInstance.get_power_name().get_power_name());
        }
        final L1WeaponSkill weaponSkill = WeaponSkillTable.get().getTemplate(_itemInstance.getItemId());
        if (weaponSkill != null) {
            if (weaponSkill.getProbability() > 0) {
                this._os.writeC(0x27);
                this._os.writeS("魔法武器發動率: + " + (weaponSkill.getProbability() + _itemInstance.getEnchantLevel()) + "%");
            }
            if (_itemInstance.getEnchantLevel() > 0) {
                this._os.writeC(0x27);
                this._os.writeS("武器魔法傷害: + " + _itemInstance.getEnchantLevel() + "%");
            }
        }
        this._os.writeC(0x27);
        this._os.writeS("安定值: " + _item.get_safeenchant());
        /**戒指說明 20231222*/
        if (_itemInstance.getBless() != 0) {
            int item = _item.getItemId();
            JiezEnchant enchant = JiezEnchant.get().get(item, _itemInstance.getEnchantLevel());
            if (enchant != null) {
                this._os.writeC(0x27);
                this._os.writeS("\\aG戒指加成：");
                if (enchant.getStr() != 0) {
                    _os.writeC(39);
                    _os.writeS("力量增加：+" + enchant.getStr());
                }
                if (enchant.getCon() != 0) {
                    _os.writeC(39);
                    _os.writeS("體質增加：+" + enchant.getCon());
                }
                if (enchant.getDex() != 0) {
                    _os.writeC(39);
                    _os.writeS("敏捷增加：+" + enchant.getDex());
                }
                if (enchant.getInt() != 0) {
                    _os.writeC(39);
                    _os.writeS("智力增加：+" + enchant.getInt());
                }
                if (enchant.getWis() != 0) {
                    _os.writeC(39);
                    _os.writeS("精神增加：+" + enchant.getWis());
                }
                if (enchant.getCha() != 0) {
                    _os.writeC(39);
                    _os.writeS("魅力增加：+" + enchant.getCha());
                }
                if (enchant.getAc() != 0) {
                    _os.writeC(39);
                    _os.writeS("防禦增加：+" + enchant.getAc());
                }
                if (enchant.getDmgR() != 0) {
                    _os.writeC(39);
                    _os.writeS("傷害減免：+" + enchant.getDmgR());
                }
                if (enchant.getHp() != 0) {
                    _os.writeC(39);
                    _os.writeS("血量增加：+" + enchant.getHp());
                }
                if (enchant.getMp() != 0) {
                    _os.writeC(39);
                    _os.writeS("魔力增加：+" + enchant.getMp());
                }
            }
        }
        /*
         * if (FeatureItemSet.POWER_START) { // 附加屬性 final int attrEnchantLevel
         * = _itemInstance.getAttrEnchantLevel(); if (attrEnchantLevel > 0) {
         * int type = 0; switch (_itemInstance.getAttrEnchantKind()) { case 1:
         * // 地 type = 0; break;
         *
         * case 2: // 火 type = 1; break;
         *
         * case 4: // 水 type = 2; break;
         *
         * case 8: // 風 type = 3; break;
         *
         * case 16: // 光 type = 4; break;
         *
         * case 32: // 暗 type = 5; break;
         *
         * case 64: // 聖 type = 6; break;
         *
         * case 128: // 邪 type = 7; break; } this._os.writeC(0x27);
         * this._os.writeS("魔化: " +
         * _attrEnchantString[type][attrEnchantLevel-1]); } }
         */
        // 攻擊成功
        // int addHitModifier = this._item.getHitModifier() + pw_sHi;
        // if (addHitModifier != 0 || _item.getInfluenceHitAndDmg() != 0) {
        // this._os.writeC(0x05);
        // this._os.writeC(addHitModifier + _itemInstance.getEnchantLevel()
        // * _item.getInfluenceHitAndDmg());
        // }
        // 追加打擊
        // int addDmgModifier = this._item.getDmgModifier() + pw_sDg;
        // if (addDmgModifier != 0 || _item.getInfluenceHitAndDmg() != 0) {
        // this._os.writeC(0x06);
        // this._os.writeC(addDmgModifier + _itemInstance.getEnchantLevel()
        // * _item.getInfluenceHitAndDmg());
        // }
        int add_sdg = pw_sDg + greater()[3] + admg + _itemInstance.getItemAttack();// 近距離傷害
        if (add_sdg != 0) {
            _os.writeC(47);
            _os.writeC(add_sdg);
        }
        int add_bowhit = pw_bHi + bhit;// 遠距離命中率
        if (add_bowhit != 0) {
            _os.writeC(24);
            _os.writeC(add_bowhit);
        }
        int add_bdg = pw_bDg + greater()[4] + bdmg + _itemInstance.getItemBowAttack();// 遠距離傷害
        if (add_bdg != 0) {
            _os.writeC(35);
            _os.writeC(add_bdg);
        }
        if (mdmg != 0) {// 魔法傷害
            _os.writeC(39);
            _os.writeS("魔法傷害 +" + mdmg);
        }
        if (mdrd != 0) {
            _os.writeC(39);
            _os.writeS("魔法傷害減免 +" + mdrd);
        }
        int bit = 0;
        bit |= (_item.isUseRoyal() ? 1 : 0);
        bit |= (_item.isUseKnight() ? 2 : 0);
        bit |= (_item.isUseElf() ? 4 : 0);
        bit |= (_item.isUseMage() ? 8 : 0);
        bit |= (_item.isUseDarkelf() ? 16 : 0);
        bit |= (_item.isUseDragonknight() ? 32 : 0);
        bit |= (_item.isUseIllusionist() ? 64 : 0);
        bit |= (_item.isUseWarrior() ? 128 : 0);
        _os.writeC(7);
        _os.writeC(bit);
        int addstr = pw_s1 + s6_1;
        if (addstr != 0) {
            _os.writeC(8);
            _os.writeC(addstr);
        }
        int adddex = pw_s2 + s6_2;
        if (adddex != 0) {
            _os.writeC(9);
            _os.writeC(adddex);
        }
        int addcon = pw_s3 + s6_3;
        if (addcon != 0) {
            _os.writeC(10);
            _os.writeC(addcon);
        }
        int addwis = pw_s4 + s6_4;
        if (addwis != 0) {
            _os.writeC(11);
            _os.writeC(addwis);
        }
        int addint = pw_s5 + s6_5;
        if (addint != 0) {
            _os.writeC(12);
            _os.writeC(addint);
        }
        int addcha = pw_s6 + s6_6;
        if (addcha != 0) {
            _os.writeC(13);
            _os.writeC(addcha);
        }
        int addhp = pw_sHp + aH_1 + greater()[1];// 血量上限
        if (addhp != 0) {
            _os.writeC(14);
            _os.writeH(addhp);
        }
        int addmp = pw_sMp + aM_1 + greater()[0];// 魔量上限;
        if (addmp != 0) {
            _os.writeC(0x20);
            _os.writeH(addmp);
        }
        int addhpr = pw_sHpr + aHpr; // 體力回復量
        if (addhpr != 0) {
            _os.writeC(37);
            _os.writeC(addhpr);
        }
        int addmpr = pw_sMpr + aMpr; // 魔力回復量
        if (addmpr != 0) {
            _os.writeC(38);
            _os.writeC(addmpr);
        }
        int freeze = pw_k6_1 + k6_1; // 冰凍耐性
        if (freeze != 0) {
            _os.writeC(33);
            _os.writeC(1);
            _os.writeC(freeze);
        }
        int stone = pw_k6_2 + k6_2; // 石化耐性
        if (stone != 0) {
            _os.writeC(33);
            _os.writeC(2);
            _os.writeC(stone);
        }
        int sleep = pw_k6_3 + k6_3; // 睡眠耐性
        if (sleep != 0) {
            _os.writeC(33);
            _os.writeC(3);
            _os.writeC(sleep);
        }
        int blind = pw_k6_4 + k6_4; // 暗黑耐性
        if (blind != 0) {
            _os.writeC(33);
            _os.writeC(4);
            _os.writeC(blind);
        }
        int stun = pw_k6_5 + k6_5; // 昏迷耐性
        if (stun != 0) {
            _os.writeC(33);
            _os.writeC(5);
            _os.writeC(stun);
        }
        int sustain = pw_k6_6 + k6_6; // 支撐耐性
        if (sustain != 0) {
            _os.writeC(33);
            _os.writeC(6);
            _os.writeC(sustain);
        }
        int addmr = pw_sMr + aMR_1 + greater()[10];// 魔防
        if (addmr != 0) {
            _os.writeC(15);
            _os.writeH(addmr);
        }
        int addsp = pw_sSp + aSP_1 + greater()[9] + _itemInstance.getItemSp();// 魔攻
        if (addsp != 0) {
            _os.writeC(17);
            _os.writeC(addsp);
        }
        if (pw_sWr > 0) { // 飾品負重顯示
            //_os.writeC(0x5a); // 90 增加負重 +X
            //_os.writeH(pw_sWr);
            _os.writeC(68); // 68 負重增加率(%)
            _os.writeC(pw_sWr);
        }
        boolean haste = _item.isHasteItem();
        if (aSS_1 == 1) {
            haste = true;
        }
        if (haste) {
            _os.writeC(18);
        }
        int defense_fire = pw_d4_1 + d4_1;
        if (defense_fire != 0) {
            _os.writeC(27);
            _os.writeC(defense_fire);
        }
        int defense_water = pw_d4_2 + d4_2;
        if (defense_water != 0) {
            _os.writeC(28);
            _os.writeC(defense_water);
        }
        int defense_wind = pw_d4_3 + d4_3;
        if (defense_wind != 0) {
            _os.writeC(29);
            _os.writeC(defense_wind);
        }
        int defense_earth = pw_d4_4 + d4_4;
        if (defense_earth != 0) {
            _os.writeC(30);
            _os.writeC(defense_earth);
        }
        if (addexp != 0) { // 經驗加成 2017/0425
            if (addexp <= 120) {
                _os.writeC(36);
                _os.writeC(addexp);
            } else {
                this._os.writeC(39);
                this._os.writeS("$6134 " + addexp + "%");
            }
        }
        int alldrd = pw_drd + drd + greater()[5] + _itemInstance.getItemReductionDmg(); // 傷害減免
        if (alldrd != 0) {
            _os.writeC(63);
            _os.writeC(alldrd);
        }
        int allmHi = pw_mHi + greater()[6]; // 魔法命中
        if (allmHi != 0) {
            //_os.writeC(39);
            //_os.writeS("魔法命中 +" + allmHi);
            _os.writeC(40);
            _os.writeC(allmHi);
        }
        if (mcri != 0) {
            _os.writeC(39);
            _os.writeS("魔法爆擊率 +" + mcri);
        }
        StringBuilder name = new StringBuilder();
        int adduhp = uhp + greater()[2];
        if (adduhp != 0) {// 增加藥水回復量%
            name.append("藥水回復量 +").append(adduhp).append("%");
            if (uhp_num != 0) {// 增加藥水回復指定量
                name.append("+").append(uhp_num);
            }
            _os.writeC(39);
            _os.writeS(name.toString());
        }
        int pvpDmg = greater()[7];
        int addpvpdmg = _item.getPvpDmg() + pvpDmg; // 增加PVP傷害
        if (addpvpdmg != 0) {
            _os.writeC(59);
            _os.writeC(addpvpdmg);
        }
        int pvpDmgdrd = greater()[11];
        int addpvpdmg_r = _item.getPvpDmg_R() + pvpDmgdrd; // 减免PVP傷害
        if (addpvpdmg_r != 0) {
            _os.writeC(60);
            _os.writeC(addpvpdmg_r);
        }
        if ((_item.getclassname().equalsIgnoreCase("Venom_Resist")) || (_item.getclassname().equalsIgnoreCase("ElitePlateMail_Antharas"))) {
            //_os.writeC(39);
            //_os.writeS("防護中毒");
            _os.writeC(57); // 57 毒耐性
            _os.writeD(19128);
        }
        if (_itemInstance.getSublimation() != null) {
            CharItemSublimation sub = _itemInstance.getSublimation();
            String title = sub.get_item_name() != null ? sub.get_item_name() : "未命名";
            int level = sub.getLv();
            // 顯示名稱與等級
            _os.writeC(0x27);
            _os.writeS("\\f2昇華：" + title + " Lv." + level);
            // 顯示昇華能力細節（從資料表讀取）
            L1ItemSublimation data = ItemSublimationTable.get().getItemSublimation(sub);
            if (data != null) {
                if (data.getDmgChanceHp() > 0) {
                    _os.writeC(0x27);
                    _os.writeS("傷害機率 +" + data.getDmgChanceHp() + "%");
                }
                if (data.getDmgChanceMp() > 0) {
                    _os.writeC(0x27);
                    _os.writeS("傷害機率 +" + data.getDmgChanceMp() + "%");
                }
                if (data.isWithstandDmg()) {
                    _os.writeC(0x27);
                    _os.writeS("抵擋物理傷害");
                }
                if (data.isWithstandMagic()) {
                    _os.writeC(0x27);
                    _os.writeS("可抵擋魔法傷害");
                }
                if (data.isReturnDmg()) {
                    _os.writeC(0x27);
                    _os.writeS("反彈物理攻擊");
                }
                if (data.isReturnMagic()) {
                    _os.writeC(0x27);
                    _os.writeS("反彈魔法攻擊");
                }
                if (data.isReturnSkills()) {
                    _os.writeC(0x27);
                    _os.writeS("反彈技能效果");
                }
                if (data.getReturnChanceHp() > 0) {
                    _os.writeC(0x27);
                    _os.writeS("反彈 HP 傷害機率 +" + data.getReturnChanceHp() + "%");
                }
                if (data.getReturnChanceMp() > 0) {
                    _os.writeC(0x27);
                    _os.writeS("反彈 MP 傷害機率 +" + data.getReturnChanceMp() + "%");
                }
                if (data.getMessage() != null && !data.getMessage().isEmpty()) {
                    _os.writeC(0x27);
                    _os.writeS("" + data.getMessage());
                }
            }
        }
//        // 強化擴充能力
//        if (_itemInstance.getUpdateStr() != 0 || _itemInstance.getUpdateDex() != 0 || _itemInstance.getUpdateCon() != 0 || _itemInstance.getUpdateWis() != 0 || _itemInstance.getUpdateInt() != 0 || _itemInstance.getUpdateCha() != 0 || _itemInstance.getUpdateHp() != 0 || _itemInstance.getUpdateMp() != 0 || _itemInstance.getUpdateEarth() != 0 || _itemInstance.getUpdateWind() != 0 || _itemInstance.getUpdateWater() != 0 || _itemInstance.getUpdateFire() != 0 || _itemInstance.getUpdateMr() != 0 || _itemInstance.getUpdateAc() != 0 || _itemInstance.getUpdateHpr() != 0 || _itemInstance.getUpdateMpr() != 0 || _itemInstance.getUpdateSp() != 0 || _itemInstance.getUpdateDmgModifier() != 0 || _itemInstance.getUpdateHitModifier() != 0 || _itemInstance.getUpdateBowDmgModifier() != 0 || _itemInstance.getUpdateBowHitModifier() != 0 || _itemInstance.getUpdatePVPdmg() != 0 || _itemInstance.getUpdatePVPdmg_R() != 0) {
//            _os.writeC(0x27);
//            _os.writeS("\\f3擴充能力：");
//        }
        final int updatepvpdmg = _itemInstance.getUpdatePVPdmg();// pvp攻擊
        if (updatepvpdmg != 0) {
            _os.writeC(0x27);
            _os.writeS("\\f2PVP 額外傷害 +" + updatepvpdmg);
            //_os.writeC(59);
            //_os.writeC(updatepvpdmg);
        }
        final int updatepvpdmg_r = _itemInstance.getUpdatePVPdmg_R();// pvp減免
        if (updatepvpdmg_r != 0) {
            _os.writeC(0x27);
            _os.writeS("\\f2PVP 傷害減免 +" + updatepvpdmg_r);
            //_os.writeC(60);
            //_os.writeC(updatepvpdmg_r);
        }
        final int updateac = _itemInstance.getUpdateAc();// 防禦
        if (updateac != 0) {
            _os.writeC(56); // 額外防禦
            _os.writeC(updateac);
        }
        final int updatedmg = _itemInstance.getUpdateDmgModifier();// 近戰攻擊
        if (updatedmg != 0) {
            _os.writeC(47);
            _os.writeC(updatedmg);
        }
        final int updatehit = _itemInstance.getUpdateHitModifier();// 近戰命中
        if (updatehit != 0) {
            _os.writeC(48);
            _os.writeC(updatehit);
        }
        final int updatebowdmg = _itemInstance.getUpdateBowDmgModifier();// 遠攻攻擊
        if (updatebowdmg != 0) {
            _os.writeC(35);
            _os.writeC(updatebowdmg);
        }
        final int updatebowhit = _itemInstance.getUpdateBowHitModifier();// 遠攻命中
        if (updatebowhit != 0) {
            _os.writeC(24);
            _os.writeC(updatebowhit);
        }
        final int updatestr = _itemInstance.getUpdateStr();// 力量
        if (updatestr != 0) {
            _os.writeC(8);
            _os.writeC(updatestr);
        }
        final int updatedex = _itemInstance.getUpdateDex();// 敏捷
        if (updatedex != 0) {
            _os.writeC(9);
            _os.writeC(updatedex);
        }
        final int updatecon = _itemInstance.getUpdateCon();// 體質
        if (updatecon != 0) {
            _os.writeC(10);
            _os.writeC(updatecon);
        }
        final int updatewis = _itemInstance.getUpdateWis();// 精神
        if (updatewis != 0) {
            _os.writeC(11);
            _os.writeC(updatewis);
        }
        final int updateint = _itemInstance.getUpdateInt();// 智力
        if (updateint != 0) {
            _os.writeC(12);
            _os.writeC(updateint);
        }
        final int updatecha = _itemInstance.getUpdateCha();// 魅力
        if (updatecha != 0) {
            _os.writeC(13);
            _os.writeC(updatecha);
        }
        final int updatehp = _itemInstance.getUpdateHp();// 血量
        if (updatehp != 0) {
            _os.writeC(14);
            _os.writeH(updatehp);
        }
        final int updatemp = _itemInstance.getUpdateMp();// 魔量
        if (updatemp != 0) {
            _os.writeC(0x20);
            _os.writeH(updatemp);
        }
        final int updatehpr = _itemInstance.getUpdateHpr();// 回血
        if (updatehpr != 0) {
            _os.writeC(37);
            _os.writeC(updatehpr);
        }
        final int updatempr = _itemInstance.getUpdateMpr();// 回魔
        if (updatempr != 0) {
            _os.writeC(38);
            _os.writeC(updatempr);
        }
        final int updatemr = _itemInstance.getUpdateMr();// 抗魔
        if (updatemr != 0) {
            _os.writeC(15);
            _os.writeH(updatemr);
        }
        final int updatesp = _itemInstance.getUpdateSp();// 魔法攻擊
        if (updatesp != 0) {
            _os.writeC(17);
            _os.writeC(updatesp);
        }
        final int updatefire = _itemInstance.getUpdateFire();// 火屬性
        if (updatefire != 0) {
            _os.writeC(27);
            _os.writeC(updatefire);
        }
        final int updatewater = _itemInstance.getUpdateWater();// 水屬性
        if (updatewater != 0) {
            _os.writeC(28);
            _os.writeC(updatewater);
        }
        final int updatewind = _itemInstance.getUpdateWind();// 風屬性
        if (updatewind != 0) {
            _os.writeC(29);
            _os.writeC(updatewind);
        }
        final int updateearth = _itemInstance.getUpdateEarth();// 地屬性
        if (updateearth != 0) {
            _os.writeC(30);
            _os.writeC(updateearth);
        }
        // 強化擴充能力 end
		/*if (_statusx) {
			if (!_item.isTradable()) {
				_os.writeC(39);
				_os.writeS("無法交易");
			}
			if (_item.isCantDelete()) {
				_os.writeC(39);
				_os.writeS("無法刪除");
			}
			if (_item.get_safeenchant() < 0) {
				_os.writeC(39);
				_os.writeS("無法強化");
			}
		}*/
        //if ((ArmorSetTable.get().checkArmorSet(_itemInstance.getItemId())) && (getQuality1() != null) && (!getQuality1().equals(""))) {
        if ((ArmorSetTable.get().checkArmorSet(_itemInstance.getItemId())) && (_item.get_mode()[0] != 0  // 套裝效果:力量增加
                || _item.get_mode()[1] != 0  // 套裝效果:敏捷增加
                || _item.get_mode()[2] != 0  // 套裝效果:體質增加
                || _item.get_mode()[3] != 0  // 套裝效果:精神增加
                || _item.get_mode()[4] != 0  // 套裝效果:智力增加
                || _item.get_mode()[5] != 0  // 套裝效果:魅力增加
                || _item.get_mode()[6] != 0  // 套裝效果:HP增加
                || _item.get_mode()[7] != 0  // 套裝效果:MP增加
                || _item.get_mode()[8] != 0  // 套裝效果:抗魔增加
                || _item.get_mode()[9] != 0  // 套裝效果:魔攻
                || _item.get_mode()[10] != 0 // 套裝效果:加速效果
                || _item.get_mode()[11] != 0 // 套裝效果:火屬性增加
                || _item.get_mode()[12] != 0 // 套裝效果:水屬性增加
                || _item.get_mode()[13] != 0 // 套裝效果:風屬性增加
                || _item.get_mode()[14] != 0 // 套裝效果:地屬性增加
                || _item.get_mode()[15] != 0 // 套裝效果:寒冰耐性增加
                || _item.get_mode()[16] != 0 // 套裝效果:石化耐性增加
                || _item.get_mode()[17] != 0 // 套裝效果:睡眠耐性增加
                || _item.get_mode()[18] != 0 // 套裝效果:暗闇耐性增加
                || _item.get_mode()[19] != 0 // 套裝效果:暈眩耐性增加
                || _item.get_mode()[20] != 0 // 套裝效果:支撐耐性增加
                || _item.get_mode()[21] != 0 // 套裝效果:回血量增加
                || _item.get_mode()[22] != 0 // 套裝效果:回魔量增加
                || _item.get_mode()[23] != 0 // 套裝效果:套裝增加物理傷害
                || _item.get_mode()[24] != 0 // 套裝效果:套裝減免物理傷害
                || _item.get_mode()[25] != 0 // 套裝效果:套裝增加魔法傷害
                || _item.get_mode()[26] != 0 // 套裝效果:套裝減免魔法傷害
                || _item.get_mode()[27] != 0 // 套裝效果:套裝增加弓的物理傷害
                || _item.get_mode()[28] != 0 // 套裝效果:套裝增加近距離命中率
                || _item.get_mode()[29] != 0 // 套裝效果:套裝增加遠距離命中率
                || _item.get_mode()[30] != 0 // 套裝效果:套裝增加魔法爆擊率
                || _item.get_mode()[31] != 0 // 套裝效果:套裝增加防禦
                || _item.get_mode()[32] > 0  // 套裝效果:套裝變身
        )) {
            _os.writeC(69);
            if (isMatch()) {
                _os.writeC(1);
            } else {
                _os.writeC(2);
            }
            if (_item.get_mode()[32] > 0 && _item.get_mode()[33] > 0) { // 變身
                _os.writeC(71);
                _os.writeH(_item.get_mode()[33]);
            } else if (_item.get_mode()[32] > 0 && _item.get_mode()[33] <= 0) {
                _os.writeC(0x27);
                _os.writeS("變身效果");
            }
            if (_item.get_mode()[31] != 0) { // 防禦
                _os.writeC(56); // 額外防禦
                _os.writeC(_item.get_mode()[31]);
            }
            if (_item.get_mode()[0] != 0) { // 力量
                _os.writeC(0x08);
                _os.writeC(_item.get_mode()[0]);
            }
            if (_item.get_mode()[1] != 0) { // 敏捷
                _os.writeC(0x09);
                _os.writeC(_item.get_mode()[1]);
            }
            if (_item.get_mode()[2] != 0) { // 體質
                _os.writeC(0x0a);
                _os.writeC(_item.get_mode()[2]);
            }
            if (_item.get_mode()[3] != 0) { // 精神
                _os.writeC(0x0b);
                _os.writeC(_item.get_mode()[3]);
            }
            if (_item.get_mode()[4] != 0) { // 智力
                _os.writeC(0x0c);
                _os.writeC(_item.get_mode()[4]);
            }
            if (_item.get_mode()[5] != 0) { // 魅力
                _os.writeC(0x0d);
                _os.writeC(_item.get_mode()[5]);
            }
            if (_item.get_mode()[6] != 0) { // 血量上限
                _os.writeC(0x0e);
                _os.writeH(_item.get_mode()[6]);
            }
            if (_item.get_mode()[7] != 0) { // 魔量上限
                _os.writeC(0x20);
                _os.writeH(_item.get_mode()[7]);
            }
            if (_item.get_mode()[8] != 0) { // 抗魔
                _os.writeC(0x0f);
                _os.writeH(_item.get_mode()[8]);
            }
            if (_item.get_mode()[9] != 0) { // 魔攻
                _os.writeC(0x11);
                _os.writeC(_item.get_mode()[9]);
            }
            if (_item.get_mode()[10] != 0) { // 加速效果
                _os.writeC(0x12);
            }
            if (_item.get_mode()[11] != 0) { // 火屬性
                _os.writeC(0x1b);
                _os.writeC(_item.get_mode()[11]);
            }
            if (_item.get_mode()[12] != 0) { // 水屬性
                _os.writeC(0x1c);
                _os.writeC(_item.get_mode()[12]);
            }
            if (_item.get_mode()[13] != 0) { // 風屬性
                _os.writeC(0x1d);
                _os.writeC(_item.get_mode()[13]);
            }
            if (_item.get_mode()[14] != 0) { // 地屬性
                _os.writeC(0x1e);
                _os.writeC(_item.get_mode()[14]);
            }
            if (_item.get_mode()[15] != 0) { // 寒冰耐性
                _os.writeC(33);
                _os.writeC(1);
                _os.writeC(_item.get_mode()[15]);
            }
            if (_item.get_mode()[16] != 0) { // 石化耐性
                _os.writeC(33);
                _os.writeC(2);
                _os.writeC(_item.get_mode()[16]);
            }
            if (_item.get_mode()[17] != 0) { // 睡眠耐性
                _os.writeC(33);
                _os.writeC(3);
                _os.writeC(_item.get_mode()[17]);
            }
            if (_item.get_mode()[18] != 0) { // 暗黑耐性
                _os.writeC(33);
                _os.writeC(4);
                _os.writeC(_item.get_mode()[18]);
            }
            if (_item.get_mode()[19] != 0) { // 昏迷耐性
                _os.writeC(33);
                _os.writeC(5);
                _os.writeC(_item.get_mode()[19]);
            }
            if (_item.get_mode()[20] != 0) { // 支撐耐性
                _os.writeC(33);
                _os.writeC(6);
                _os.writeC(_item.get_mode()[20]);
            }
            if (_item.get_mode()[21] != 0) { // 體力回復量
                _os.writeC(37);
                _os.writeC(_item.get_mode()[21]);
            }
            if (_item.get_mode()[22] != 0) { // 魔力回復量
                _os.writeC(38);
                _os.writeC(_item.get_mode()[22]);
            }
            if (_item.get_mode()[24] != 0) { // 減免物理傷害 【傷害減免】
                _os.writeC(63);
                _os.writeC(_item.get_mode()[24]);
            }
            if (_item.get_mode()[23] != 0) { // 增加物理傷害 【近距離傷害】
                _os.writeC(47);
                _os.writeC(_item.get_mode()[23]);
            }
            if (_item.get_mode()[28] != 0) { // 增加近距離命中率
                _os.writeC(48);
                _os.writeC(_item.get_mode()[28]);
            }
            if (_item.get_mode()[27] != 0) { // 增加弓的物理傷害 【遠距離傷害】
                _os.writeC(35);
                _os.writeC(_item.get_mode()[27]);
            }
            if (_item.get_mode()[29] != 0) { // 增加遠距離命中率
                _os.writeC(24);
                _os.writeC(_item.get_mode()[29]);
            }
            if (_item.get_mode()[25] != 0) { // 增加魔法傷害
                _os.writeC(39);
                _os.writeS("魔法增傷 +" + _item.get_mode()[25]);
            }
            if (_item.get_mode()[26] != 0) { // 減免魔法傷害
                _os.writeC(39);
                _os.writeS("魔法減傷 +" + _item.get_mode()[26]);
            }
            if (_item.get_mode()[30] != 0) { // 增加魔法爆擊率
                _os.writeC(39);
                _os.writeS("魔法爆擊 +" + _item.get_mode()[30]);
            }
            if ((getQuality1() != null) && (!getQuality1().equals(""))) {
                _os.writeC(39);
                _os.writeS(getQuality1());
            }
            if ((getQuality2() != null) && (!getQuality2().equals(""))) {
                _os.writeC(39);
                _os.writeS(getQuality2());
            }
            if ((getQuality3() != null) && (!getQuality3().equals(""))) {
                _os.writeC(39);
                _os.writeS(getQuality3());
            }
            if ((getQuality4() != null) && (!getQuality4().equals(""))) {
                _os.writeC(39);
                _os.writeS(getQuality4());
            }
            if ((getQuality5() != null) && (!getQuality5().equals(""))) {
                _os.writeC(39);
                _os.writeS(getQuality5());
            }
            if ((getQuality6() != null) && (!getQuality6().equals(""))) {
                _os.writeC(39);
                _os.writeS(getQuality6());
            }
            if ((getQuality7() != null) && (!getQuality7().equals(""))) {
                _os.writeC(39);
                _os.writeS(getQuality7());
            }
            if ((getQuality8() != null) && (!getQuality8().equals(""))) {
                _os.writeC(39);
                _os.writeS(getQuality8());
            }
            if ((getQuality9() != null) && (!getQuality9().equals(""))) {
                _os.writeC(39);
                _os.writeS(getQuality9());
            }
            _os.writeC(69);
            _os.writeC(0);
        }
        // 2017/04/21
        /**祝福化說明系統 台灣JAVA技術老爹製作 line:@422ygvzx*/
        if (_itemInstance.getBless() == 0) {
            int item = _item.getItemId();
            L1Zhufu zhufu = Zhufu.getInstance().getTemplate(item, 2);
            if (zhufu == null) {
                zhufu = Zhufu.getInstance().getTemplateByType(_item.getType(), 2);
            }
            if (zhufu != null) {
                _os.writeC(39);
                _os.writeS("祝福化屬性：");
                if (zhufu.getAddStr() != 0) {
                    _os.writeC(39);
                    _os.writeS("力量：+" + zhufu.getAddStr());
                }
                if (zhufu.getAddDex() != 0) {
                    _os.writeC(39);
                    _os.writeS("敏捷：+" + zhufu.getAddDex());
                }
                if (zhufu.getAddCon() != 0) {
                    _os.writeC(39);
                    _os.writeS("體質：+" + zhufu.getAddCon());
                }
                if (zhufu.getAddInt() != 0) {
                    _os.writeC(39);
                    _os.writeS("智力：+" + zhufu.getAddInt());
                }
                if (zhufu.getAddWis() != 0) {
                    _os.writeC(39);
                    _os.writeS("精神：+" + zhufu.getAddWis());
                }
                if (zhufu.getAddCha() != 0) {
                    _os.writeC(39);
                    _os.writeS("魅力：+" + zhufu.getAddCha());
                }
                if (zhufu.getAddAc() != 0) {
                    _os.writeC(39);
                    _os.writeS("防禦：+" + zhufu.getAddAc());
                }
                if (zhufu.getAddMaxHp() != 0) {
                    _os.writeC(39);
                    _os.writeS("HP：+" + zhufu.getAddMaxHp());
                }
                if (zhufu.getAddMaxMp() != 0) {
                    _os.writeC(39);
                    _os.writeS("MP：+" + zhufu.getAddMaxMp());
                }
                if (zhufu.getAddHpr() != 0) {
                    _os.writeC(39);
                    _os.writeS("回血：+" + zhufu.getAddHpr());
                }
                if (zhufu.getAddMpr() != 0) {
                    _os.writeC(39);
                    _os.writeS("回魔：+" + zhufu.getAddMpr());
                }
                if (zhufu.getAddDmg() != 0) {
                    _os.writeC(39);
                    _os.writeS("近距離傷害：+" + zhufu.getAddDmg());
                }
                if (zhufu.getAddHit() != 0) {
                    _os.writeC(39);
                    _os.writeS("近距離命中：+" + zhufu.getAddHit());
                }
                if (zhufu.getAddBowDmg() != 0) {
                    _os.writeC(39);
                    _os.writeS("遠距離傷害：+" + zhufu.getAddBowDmg());
                }
                if (zhufu.getAddBowHit() != 0) {
                    _os.writeC(39);
                    _os.writeS("遠距離命中：+" + zhufu.getAddBowHit());
                }
                if (zhufu.getReduction_dmg() != 0) {
                    _os.writeC(39);
                    _os.writeS("物理傷害減免：+" + zhufu.getReduction_dmg());
                }
                if (zhufu.getAddMr() != 0) {
                    _os.writeC(39);
                    _os.writeS("抗魔：+" + zhufu.getAddMr());
                }
                if (zhufu.getAddSp() != 0) {
                    _os.writeC(39);
                    _os.writeS("魔攻：+" + zhufu.getAddSp());
                }
                if (zhufu.getAddFire() != 0) {
                    _os.writeC(39);
                    _os.writeS("抗火屬性：+" + zhufu.getAddFire());
                }
                if (zhufu.getAddWind() != 0) {
                    _os.writeC(39);
                    _os.writeS("抗風屬性：+" + zhufu.getAddWind());
                }
                if (zhufu.getAddEarth() != 0) {
                    _os.writeC(39);
                    _os.writeS("抗地屬性：+" + zhufu.getAddEarth());
                }
                if (zhufu.getAddWater() != 0) {
                    _os.writeC(39);
                    _os.writeS("抗水屬性：+" + zhufu.getAddWater());
                }
                if (zhufu.getwljm() != 0) {
                    _os.writeC(39);
                    _os.writeS("PVP傷害減免：+" + zhufu.getwljm());
                }
                if (zhufu.getwljmbai() != 0) {
                    _os.writeC(39);
                    _os.writeS("PVP傷害減免：+" + zhufu.getwljmbai() + "%");
                }
                if (zhufu.getmojm() != 0) {
                    _os.writeC(39);
                    _os.writeS("PVP魔法傷害減免：+" + zhufu.getmojm());
                }
                if (zhufu.getmojmbai() != 0) {
                    _os.writeC(39);
                    _os.writeS("PVP魔法傷害減免：+" + zhufu.getmojmbai() + "%");
                }
            }
        }
        // 2017/04/21
        ArrayList<String> as = new ArrayList<>();
        try {
            for (String s : WilliamItemMessage.getItemInfo(_itemInstance)) {
                if (s != null && !s.isEmpty()) {
                    _os.writeC(39);
                    _os.writeS(s);
                }
            }
        } finally {
            as.clear();
        }
        String[] byarmor = this._item.getclassname().split(" "); // SRC0711
        if (byarmor[0].equals("SkillByArmor")) {
            int bonus2 = this._itemInstance.getEnchantLevel() - this._item.get_safeenchant();
            if (bonus2 <= 0) {
                bonus2 = 0;
            }
            if (bonus2 >= 0) {
                this._os.writeC(39);
                this._os.writeS("目前+" + this._itemInstance.getEnchantLevel() + " " + (bonus2 + Integer.parseInt(byarmor[1])) + "%機率 發動" + byarmor[4]);
                this._os.writeC(39);
                this._os.writeS("傷害 " + Integer.valueOf(byarmor[3]));
            }
        }
        showItemDelTimer();
        checkArmorSet(); // 套裝能力顯示
        return _os;
    }
    // TODO 武器

    /**
     * 副助道具
     *
     */
    private BinaryOutputStream accessories2() {
        _os.writeC(19);
        int ac = _item.get_ac();
        if (ac < 0) {
            ac = Math.abs(ac);
        }
        _os.writeC(ac);
        _os.writeC(_item.getMaterial());
        _os.writeC(_item.get_greater());
        _os.writeD(_itemInstance.getWeight());
        int pw_s1 = _item.get_addstr();
        int pw_s2 = _item.get_adddex();
        int pw_s3 = _item.get_addcon();
        int pw_s4 = _item.get_addwis();
        int pw_s5 = _item.get_addint();
        int pw_s6 = _item.get_addcha();
        int pw_sHp = _itemPower.get_addhp();
        int pw_sMp = _itemPower.get_addmp();
        int pw_sMr = _itemPower.getMr();
        int pw_sSp = _itemPower.getSp();
        int pw_sWr = _item.getWeightReduction(); // 副助道具負重顯示
        int pw_sDg = _item.getDmgModifierByArmor();
        int pw_sHi = _itemPower.getHitModifierByArmor();
        int pw_mHi = _item.getMagicHitModifierByArmor();
        int pw_bDg = _item.getBowDmgModifierByArmor();
        int pw_bHi = _item.getBowHitModifierByArmor();
        int pw_d4_1 = _item.get_defense_fire();
        int pw_d4_2 = _item.get_defense_water();
        int pw_d4_3 = _item.get_defense_wind();
        int pw_d4_4 = _item.get_defense_earth();
        int pw_k6_1 = _item.get_regist_freeze();
        int pw_k6_2 = _item.get_regist_stone();
        int pw_k6_3 = _item.get_regist_sleep();
        int pw_k6_4 = _item.get_regist_blind();
        int pw_k6_5 = _item.get_regist_stun();
        int pw_k6_6 = _item.get_regist_sustain();
        int pw_sHpr = _item.get_addhpr();
        int pw_sMpr = _itemPower.getMpr();
        int addexp = _item.getExpPoint();
        int pw_drd = _itemPower.getDamageReduction();
        int uhp = _item.get_up_hp_potion();// 增加藥水回復量%
        int uhp_num = _item.get_uhp_number();// 增加藥水回復指定量
        if (pw_sHi != 0) { // 近戰命中
            _os.writeC(48);
            _os.writeC(pw_sHi);
        }
        int add_sdg = pw_sDg;// 近距離傷害
        if (add_sdg != 0) {
            _os.writeC(47);
            _os.writeC(add_sdg);
        }
        if (pw_bHi != 0) {
            _os.writeC(24);
            _os.writeC(pw_bHi);
        }
        int add_bdg = pw_bDg;// 遠距離傷害
        if (add_bdg != 0) {
            _os.writeC(35);
            _os.writeC(add_bdg);
        }
        int bit = 0;
        bit |= (_item.isUseRoyal() ? 1 : 0);
        bit |= (_item.isUseKnight() ? 2 : 0);
        bit |= (_item.isUseElf() ? 4 : 0);
        bit |= (_item.isUseMage() ? 8 : 0);
        bit |= (_item.isUseDarkelf() ? 16 : 0);
        bit |= (_item.isUseDragonknight() ? 32 : 0);
        bit |= (_item.isUseIllusionist() ? 64 : 0);
        bit |= (_item.isUseWarrior() ? 128 : 0);
        _os.writeC(7);
        _os.writeC(bit);
        int addstr = pw_s1;// 力量
        int adddex = pw_s2;// 敏捷
        int addcon = pw_s3;// 體質
        int addwis = pw_s4;// 精神.
        int addint = pw_s5;// 智力
        int addcha = pw_s6;// 魅力
        if (addstr == 1 && adddex == 1 && addcon == 1 && addwis == 1 && addint == 1 && addcha == 1) {
            _os.writeC(39);
            _os.writeS("全能力值 +1");
        } else {
            if (addstr != 0) {
                _os.writeC(8);
                _os.writeC(addstr);
            }
            if (adddex != 0) {
                _os.writeC(9);
                _os.writeC(adddex);
            }
            if (addcon != 0) {
                _os.writeC(10);
                _os.writeC(addcon);
            }
            if (addwis != 0) {
                _os.writeC(11);
                _os.writeC(addwis);
            }
            if (addint != 0) {
                _os.writeC(12);
                _os.writeC(addint);
            }
            if (addcha != 0) {
                _os.writeC(13);
                _os.writeC(addcha);
            }
        }
        int addhp = pw_sHp;
        if (addhp != 0) {
            _os.writeC(14);
            _os.writeH(addhp);
        }
        int addmp = pw_sMp;
        if (addmp != 0) {
            _os.writeC(0x20);
            _os.writeH(addmp);
        }
        int addhpr = pw_sHpr; // 體力回復量
        if (addhpr != 0) {
            _os.writeC(37);
            _os.writeC(addhpr);
        }
        int addmpr = pw_sMpr; // 魔力回復量
        if (addmpr != 0) {
            _os.writeC(38);
            _os.writeC(addmpr);
        }
        int freeze = pw_k6_1; // 冰凍耐性
        if (freeze != 0) {
            _os.writeC(33);
            _os.writeC(1);
            _os.writeC(freeze);
        }
        int stone = pw_k6_2; // 石化耐性
        if (stone != 0) {
            _os.writeC(33);
            _os.writeC(2);
            _os.writeC(stone);
        }
        int sleep = pw_k6_3; // 睡眠耐性
        if (sleep != 0) {
            _os.writeC(33);
            _os.writeC(3);
            _os.writeC(sleep);
        }
        int blind = pw_k6_4; // 暗黑耐性
        if (blind != 0) {
            _os.writeC(33);
            _os.writeC(4);
            _os.writeC(blind);
        }
        int stun = pw_k6_5; // 昏迷耐性
        if (stun != 0) {
            _os.writeC(33);
            _os.writeC(5);
            _os.writeC(stun);
        }
        int sustain = pw_k6_6; // 支撐耐性
        if (sustain != 0) {
            _os.writeC(33);
            _os.writeC(6);
            _os.writeC(sustain);
        }
        int addmr = pw_sMr;
        if (addmr != 0) {
            _os.writeC(15);
            _os.writeH(addmr);
        }
        int addsp = pw_sSp;
        if (addsp != 0) {
            _os.writeC(17);
            _os.writeC(addsp);
        }
        if (pw_sWr > 0) { // 副助道具負重顯示
            //_os.writeC(0x5a); // 90 增加負重 +X
            //_os.writeH(pw_sWr);
            _os.writeC(68); // 68 負重增加率(%)
            _os.writeC(pw_sWr);
        }
        boolean haste = _item.isHasteItem();
        if (haste) {
            _os.writeC(18);
        }
        int defense_fire = pw_d4_1;
        if (defense_fire != 0) {
            _os.writeC(27);
            _os.writeC(defense_fire);
        }
        int defense_water = pw_d4_2;
        if (defense_water != 0) {
            _os.writeC(28);
            _os.writeC(defense_water);
        }
        int defense_wind = pw_d4_3;
        if (defense_wind != 0) {
            _os.writeC(29);
            _os.writeC(defense_wind);
        }
        int defense_earth = pw_d4_4;
        if (defense_earth != 0) {
            _os.writeC(30);
            _os.writeC(defense_earth);
        }
        if (addexp != 0) { // 經驗加成 2017/0425
            if (addexp <= 120) {
                _os.writeC(36);
                _os.writeC(addexp);
            } else {
                this._os.writeC(39);
                this._os.writeS("$6134 " + addexp + "%");
            }
        }
        if (pw_drd != 0) {// 傷害減免
            _os.writeC(63);
            _os.writeC(pw_drd);
        }
        if (pw_mHi != 0) {// 魔法命中
            //_os.writeC(39);
            //_os.writeS("魔法命中 +" + pw_mHi);
            _os.writeC(40);
            _os.writeC(pw_mHi);
        }
        StringBuilder name = new StringBuilder();
        int adduhp = uhp + greater()[2];
        if (adduhp != 0) {// 增加藥水回復量%
            name.append("藥水回復量 +").append(adduhp).append("%");
            if (uhp_num != 0) {// 增加藥水回復指定量
                name.append("+").append(uhp_num);
            }
            _os.writeC(39);
            _os.writeS(name.toString());
        }
        int addpvpdmg = _item.getPvpDmg(); // 增加PVP傷害
        if (addpvpdmg != 0) {
            _os.writeC(59);
            _os.writeC(addpvpdmg);
        }
        int addpvpdmg_r = _item.getPvpDmg_R(); // 减免PVP傷害
        if (addpvpdmg_r != 0) {
            _os.writeC(60);
            _os.writeC(addpvpdmg_r);
        }
        // 強化擴充能力
        if (_itemInstance.getUpdateStr() != 0 || _itemInstance.getUpdateDex() != 0 || _itemInstance.getUpdateCon() != 0 || _itemInstance.getUpdateWis() != 0 || _itemInstance.getUpdateInt() != 0 || _itemInstance.getUpdateCha() != 0 || _itemInstance.getUpdateHp() != 0 || _itemInstance.getUpdateMp() != 0 || _itemInstance.getUpdateEarth() != 0 || _itemInstance.getUpdateWind() != 0 || _itemInstance.getUpdateWater() != 0 || _itemInstance.getUpdateFire() != 0 || _itemInstance.getUpdateMr() != 0 || _itemInstance.getUpdateAc() != 0 || _itemInstance.getUpdateHpr() != 0 || _itemInstance.getUpdateMpr() != 0 || _itemInstance.getUpdateSp() != 0 || _itemInstance.getUpdateDmgModifier() != 0 || _itemInstance.getUpdateHitModifier() != 0 || _itemInstance.getUpdateBowDmgModifier() != 0 || _itemInstance.getUpdateBowHitModifier() != 0 || _itemInstance.getUpdatePVPdmg() != 0 || _itemInstance.getUpdatePVPdmg_R() != 0) {
            _os.writeC(0x27);
            _os.writeS("\\f3擴充能力：");
        }
        final int updatepvpdmg = _itemInstance.getUpdatePVPdmg();// pvp攻擊
        if (updatepvpdmg != 0) {
            _os.writeC(0x27);
            _os.writeS("\\f2PVP 額外傷害 +" + updatepvpdmg);
            //_os.writeC(59);
            //_os.writeC(updatepvpdmg);
        }
        final int updatepvpdmg_r = _itemInstance.getUpdatePVPdmg_R();// pvp減免
        if (updatepvpdmg_r != 0) {
            _os.writeC(0x27);
            _os.writeS("\\f2PVP 傷害減免 +" + updatepvpdmg_r);
            //_os.writeC(60);
            //_os.writeC(updatepvpdmg_r);
        }
        final int updateac = _itemInstance.getUpdateAc();// 防禦
        if (updateac != 0) {
            _os.writeC(56); // 額外防禦
            _os.writeC(updateac);
        }
        final int updatedmg = _itemInstance.getUpdateDmgModifier();// 近戰攻擊
        if (updatedmg != 0) {
            _os.writeC(47);
            _os.writeC(updatedmg);
        }
        final int updatehit = _itemInstance.getUpdateHitModifier();// 近戰命中
        if (updatehit != 0) {
            _os.writeC(48);
            _os.writeC(updatehit);
        }
        final int updatebowdmg = _itemInstance.getUpdateBowDmgModifier();// 遠攻攻擊
        if (updatebowdmg != 0) {
            _os.writeC(35);
            _os.writeC(updatebowdmg);
        }
        final int updatebowhit = _itemInstance.getUpdateBowHitModifier();// 遠攻命中
        if (updatebowhit != 0) {
            _os.writeC(24);
            _os.writeC(updatebowhit);
        }
        final int updatestr = _itemInstance.getUpdateStr();// 力量
        if (updatestr != 0) {
            _os.writeC(8);
            _os.writeC(updatestr);
        }
        final int updatedex = _itemInstance.getUpdateDex();// 敏捷
        if (updatedex != 0) {
            _os.writeC(9);
            _os.writeC(updatedex);
        }
        final int updatecon = _itemInstance.getUpdateCon();// 體質
        if (updatecon != 0) {
            _os.writeC(10);
            _os.writeC(updatecon);
        }
        final int updatewis = _itemInstance.getUpdateWis();// 精神
        if (updatewis != 0) {
            _os.writeC(11);
            _os.writeC(updatewis);
        }
        final int updateint = _itemInstance.getUpdateInt();// 智力
        if (updateint != 0) {
            _os.writeC(12);
            _os.writeC(updateint);
        }
        final int updatecha = _itemInstance.getUpdateCha();// 魅力
        if (updatecha != 0) {
            _os.writeC(13);
            _os.writeC(updatecha);
        }
        final int updatehp = _itemInstance.getUpdateHp();// 血量
        if (updatehp != 0) {
            _os.writeC(14);
            _os.writeH(updatehp);
        }
        final int updatemp = _itemInstance.getUpdateMp();// 魔量
        if (updatemp != 0) {
            _os.writeC(0x20);
            _os.writeH(updatemp);
        }
        final int updatehpr = _itemInstance.getUpdateHpr();// 回血
        if (updatehpr != 0) {
            _os.writeC(37);
            _os.writeC(updatehpr);
        }
        final int updatempr = _itemInstance.getUpdateMpr();// 回魔
        if (updatempr != 0) {
            _os.writeC(38);
            _os.writeC(updatempr);
        }
        final int updatemr = _itemInstance.getUpdateMr();// 抗魔
        if (updatemr != 0) {
            _os.writeC(15);
            _os.writeH(updatemr);
        }
        final int updatesp = _itemInstance.getUpdateSp();// 魔法攻擊
        if (updatesp != 0) {
            _os.writeC(17);
            _os.writeC(updatesp);
        }
        final int updatefire = _itemInstance.getUpdateFire();// 火屬性
        if (updatefire != 0) {
            _os.writeC(27);
            _os.writeC(updatefire);
        }
        final int updatewater = _itemInstance.getUpdateWater();// 水屬性
        if (updatewater != 0) {
            _os.writeC(28);
            _os.writeC(updatewater);
        }
        final int updatewind = _itemInstance.getUpdateWind();// 風屬性
        if (updatewind != 0) {
            _os.writeC(29);
            _os.writeC(updatewind);
        }
        final int updateearth = _itemInstance.getUpdateEarth();// 地屬性
        if (updateearth != 0) {
            _os.writeC(30);
            _os.writeC(updateearth);
        }
        // 強化擴充能力 end
		/*if (_statusx) {
			if (!_item.isTradable()) {
				_os.writeC(39);
				_os.writeS("無法交易");
			}
			if (_item.isCantDelete()) {
				_os.writeC(39);
				_os.writeS("無法刪除");
			}
			if (_item.get_safeenchant() < 0) {
				_os.writeC(39);
				_os.writeS("無法強化");
			}
		}*/
        //if ((ArmorSetTable.get().checkArmorSet(_itemInstance.getItemId())) && (getQuality1() != null) && (!getQuality1().equals(""))) {
        if ((ArmorSetTable.get().checkArmorSet(_itemInstance.getItemId())) && (_item.get_mode()[0] != 0  // 套裝效果:力量增加
                || _item.get_mode()[1] != 0  // 套裝效果:敏捷增加
                || _item.get_mode()[2] != 0  // 套裝效果:體質增加
                || _item.get_mode()[3] != 0  // 套裝效果:精神增加
                || _item.get_mode()[4] != 0  // 套裝效果:智力增加
                || _item.get_mode()[5] != 0  // 套裝效果:魅力增加
                || _item.get_mode()[6] != 0  // 套裝效果:HP增加
                || _item.get_mode()[7] != 0  // 套裝效果:MP增加
                || _item.get_mode()[8] != 0  // 套裝效果:抗魔增加
                || _item.get_mode()[9] != 0  // 套裝效果:魔攻
                || _item.get_mode()[10] != 0 // 套裝效果:加速效果
                || _item.get_mode()[11] != 0 // 套裝效果:火屬性增加
                || _item.get_mode()[12] != 0 // 套裝效果:水屬性增加
                || _item.get_mode()[13] != 0 // 套裝效果:風屬性增加
                || _item.get_mode()[14] != 0 // 套裝效果:地屬性增加
                || _item.get_mode()[15] != 0 // 套裝效果:寒冰耐性增加
                || _item.get_mode()[16] != 0 // 套裝效果:石化耐性增加
                || _item.get_mode()[17] != 0 // 套裝效果:睡眠耐性增加
                || _item.get_mode()[18] != 0 // 套裝效果:暗闇耐性增加
                || _item.get_mode()[19] != 0 // 套裝效果:暈眩耐性增加
                || _item.get_mode()[20] != 0 // 套裝效果:支撐耐性增加
                || _item.get_mode()[21] != 0 // 套裝效果:回血量增加
                || _item.get_mode()[22] != 0 // 套裝效果:回魔量增加
                || _item.get_mode()[23] != 0 // 套裝效果:套裝增加物理傷害
                || _item.get_mode()[24] != 0 // 套裝效果:套裝減免物理傷害
                || _item.get_mode()[25] != 0 // 套裝效果:套裝增加魔法傷害
                || _item.get_mode()[26] != 0 // 套裝效果:套裝減免魔法傷害
                || _item.get_mode()[27] != 0 // 套裝效果:套裝增加弓的物理傷害
                || _item.get_mode()[28] != 0 // 套裝效果:套裝增加近距離命中率
                || _item.get_mode()[29] != 0 // 套裝效果:套裝增加遠距離命中率
                || _item.get_mode()[30] != 0 // 套裝效果:套裝增加魔法爆擊率
                || _item.get_mode()[31] != 0 // 套裝效果:套裝增加防禦
                || _item.get_mode()[32] > 0  // 套裝效果:套裝變身
        )) {
            _os.writeC(69);
            if (isMatch()) {
                _os.writeC(1);
            } else {
                _os.writeC(2);
            }
            if (_item.get_mode()[32] > 0 && _item.get_mode()[33] > 0) { // 變身
                _os.writeC(71);
                _os.writeH(_item.get_mode()[33]);
            } else if (_item.get_mode()[32] > 0 && _item.get_mode()[33] <= 0) {
                _os.writeC(0x27);
                _os.writeS("變身效果");
            }
            if (_item.get_mode()[31] != 0) { // 防禦
                _os.writeC(56); // 額外防禦
                _os.writeC(_item.get_mode()[31]);
            }
            if (_item.get_mode()[0] != 0) { // 力量
                _os.writeC(0x08);
                _os.writeC(_item.get_mode()[0]);
            }
            if (_item.get_mode()[1] != 0) { // 敏捷
                _os.writeC(0x09);
                _os.writeC(_item.get_mode()[1]);
            }
            if (_item.get_mode()[2] != 0) { // 體質
                _os.writeC(0x0a);
                _os.writeC(_item.get_mode()[2]);
            }
            if (_item.get_mode()[3] != 0) { // 精神
                _os.writeC(0x0b);
                _os.writeC(_item.get_mode()[3]);
            }
            if (_item.get_mode()[4] != 0) { // 智力
                _os.writeC(0x0c);
                _os.writeC(_item.get_mode()[4]);
            }
            if (_item.get_mode()[5] != 0) { // 魅力
                _os.writeC(0x0d);
                _os.writeC(_item.get_mode()[5]);
            }
            if (_item.get_mode()[6] != 0) { // 血量上限
                _os.writeC(0x0e);
                _os.writeH(_item.get_mode()[6]);
            }
            if (_item.get_mode()[7] != 0) { // 魔量上限
                _os.writeC(0x20);
                _os.writeH(_item.get_mode()[7]);
            }
            if (_item.get_mode()[8] != 0) { // 抗魔
                _os.writeC(0x0f);
                _os.writeH(_item.get_mode()[8]);
            }
            if (_item.get_mode()[9] != 0) { // 魔攻
                _os.writeC(0x11);
                _os.writeC(_item.get_mode()[9]);
            }
            if (_item.get_mode()[10] != 0) { // 加速效果
                _os.writeC(0x12);
            }
            if (_item.get_mode()[11] != 0) { // 火屬性
                _os.writeC(0x1b);
                _os.writeC(_item.get_mode()[11]);
            }
            if (_item.get_mode()[12] != 0) { // 水屬性
                _os.writeC(0x1c);
                _os.writeC(_item.get_mode()[12]);
            }
            if (_item.get_mode()[13] != 0) { // 風屬性
                _os.writeC(0x1d);
                _os.writeC(_item.get_mode()[13]);
            }
            if (_item.get_mode()[14] != 0) { // 地屬性
                _os.writeC(0x1e);
                _os.writeC(_item.get_mode()[14]);
            }
            if (_item.get_mode()[15] != 0) { // 寒冰耐性
                _os.writeC(33);
                _os.writeC(1);
                _os.writeC(_item.get_mode()[15]);
            }
            if (_item.get_mode()[16] != 0) { // 石化耐性
                _os.writeC(33);
                _os.writeC(2);
                _os.writeC(_item.get_mode()[16]);
            }
            if (_item.get_mode()[17] != 0) { // 睡眠耐性
                _os.writeC(33);
                _os.writeC(3);
                _os.writeC(_item.get_mode()[17]);
            }
            if (_item.get_mode()[18] != 0) { // 暗黑耐性
                _os.writeC(33);
                _os.writeC(4);
                _os.writeC(_item.get_mode()[18]);
            }
            if (_item.get_mode()[19] != 0) { // 昏迷耐性
                _os.writeC(33);
                _os.writeC(5);
                _os.writeC(_item.get_mode()[19]);
            }
            if (_item.get_mode()[20] != 0) { // 支撐耐性
                _os.writeC(33);
                _os.writeC(6);
                _os.writeC(_item.get_mode()[20]);
            }
            if (_item.get_mode()[21] != 0) { // 體力回復量
                _os.writeC(37);
                _os.writeC(_item.get_mode()[21]);
            }
            if (_item.get_mode()[22] != 0) { // 魔力回復量
                _os.writeC(38);
                _os.writeC(_item.get_mode()[22]);
            }
            if (_item.get_mode()[24] != 0) { // 減免物理傷害 【傷害減免】
                _os.writeC(63);
                _os.writeC(_item.get_mode()[24]);
            }
            if (_item.get_mode()[23] != 0) { // 增加物理傷害 【近距離傷害】
                _os.writeC(47);
                _os.writeC(_item.get_mode()[23]);
            }
            if (_item.get_mode()[28] != 0) { // 增加近距離命中率
                _os.writeC(48);
                _os.writeC(_item.get_mode()[28]);
            }
            if (_item.get_mode()[27] != 0) { // 增加弓的物理傷害 【遠距離傷害】
                _os.writeC(35);
                _os.writeC(_item.get_mode()[27]);
            }
            if (_item.get_mode()[29] != 0) { // 增加遠距離命中率
                _os.writeC(24);
                _os.writeC(_item.get_mode()[29]);
            }
            if (_item.get_mode()[25] != 0) { // 增加魔法傷害
                _os.writeC(39);
                _os.writeS("魔法增傷 +" + _item.get_mode()[25]);
            }
            if (_item.get_mode()[26] != 0) { // 減免魔法傷害
                _os.writeC(39);
                _os.writeS("魔法減傷 +" + _item.get_mode()[26]);
            }
            if (_item.get_mode()[30] != 0) { // 增加魔法爆擊率
                _os.writeC(39);
                _os.writeS("魔法爆擊 +" + _item.get_mode()[30]);
            }
            if ((getQuality1() != null) && (!getQuality1().equals(""))) {
                _os.writeC(39);
                _os.writeS(getQuality1());
            }
            if ((getQuality2() != null) && (!getQuality2().equals(""))) {
                _os.writeC(39);
                _os.writeS(getQuality2());
            }
            if ((getQuality3() != null) && (!getQuality3().equals(""))) {
                _os.writeC(39);
                _os.writeS(getQuality3());
            }
            if ((getQuality4() != null) && (!getQuality4().equals(""))) {
                _os.writeC(39);
                _os.writeS(getQuality4());
            }
            if ((getQuality5() != null) && (!getQuality5().equals(""))) {
                _os.writeC(39);
                _os.writeS(getQuality5());
            }
            if ((getQuality6() != null) && (!getQuality6().equals(""))) {
                _os.writeC(39);
                _os.writeS(getQuality6());
            }
            if ((getQuality7() != null) && (!getQuality7().equals(""))) {
                _os.writeC(39);
                _os.writeS(getQuality7());
            }
            if ((getQuality8() != null) && (!getQuality8().equals(""))) {
                _os.writeC(39);
                _os.writeS(getQuality8());
            }
            if ((getQuality9() != null) && (!getQuality9().equals(""))) {
                _os.writeC(39);
                _os.writeS(getQuality9());
            }
            _os.writeC(69);
            _os.writeC(0);
        }
        final L1ItemPower_name power = _itemInstance.get_power_name();
        if (_item.isSuperRune()) {
            if (power != null) {
                if (_item.isSuperRune()) {
                    _os.writeC(0x31);
                    _os.writeC(0x01);
                    for (int i = 0; i < 4; i++) {
                        final StringBuilder powername1 = new StringBuilder();
                        this._os.writeC(0x27);
                        switch (i) {
                            case 0:
                                powername1.append("欄位1:").append(set_rune_name(power.get_super_rune_1()));
                                break;
                            case 1:
                                powername1.append("欄位2:").append(set_rune_name(power.get_super_rune_2()));
                                break;
                            case 2:
                                powername1.append("欄位3:").append(set_rune_name(power.get_super_rune_3()));
                                break;
                            case 3:
                                powername1.append("欄位4:").append(set_rune_name2(power.get_super_rune_4()));
                                break;
                        }
                        this._os.writeS(powername1.toString());
                    }
                    _os.writeC(0x31);
                    _os.writeC(0x00);
                }
            } else {
                if (_item.isSuperRune()) {
                    _os.writeC(0x31);
                    _os.writeC(0x01);
                    for (int i = 0; i < 4; i++) {
                        final StringBuilder powername2 = new StringBuilder();
                        this._os.writeC(0x27);
                        switch (i) {
                            case 0:
                                powername2.append("欄位1:").append(set_rune_name(0));
                                break;
                            case 1:
                                powername2.append("欄位2:").append(set_rune_name(0));
                                break;
                            case 2:
                                powername2.append("欄位3:").append(set_rune_name(0));
                                break;
                            case 3:
                                powername2.append("欄位4:").append(set_rune_name2(0));
                                break;
                        }
                        this._os.writeS(powername2.toString());
                    }
                    _os.writeC(0x31);
                    _os.writeC(0x00);
                }
            }
        }
        // 2017/04/21
        ArrayList<String> as = new ArrayList<>();
        try {
            for (String s : WilliamItemMessage.getItemInfo(_itemInstance)) {
                if (s != null && !s.isEmpty()) {
                    _os.writeC(39);
                    _os.writeS(s);
                }
            }
        } finally {
            as.clear();
        }
        String[] byarmor = this._item.getclassname().split(" "); // SRC0711
        if (byarmor[0].equals("SkillByArmor")) {
            int bonus2 = this._itemInstance.getEnchantLevel() - this._item.get_safeenchant();
            if (bonus2 <= 0) {
                bonus2 = 0;
            }
            if (bonus2 >= 0) {
                this._os.writeC(39);
                this._os.writeS("目前+" + this._itemInstance.getEnchantLevel() + " " + (bonus2 + Integer.parseInt(byarmor[1])) + "%機率 發動" + byarmor[4]);
                this._os.writeC(39);
                this._os.writeS("傷害 " + Integer.valueOf(byarmor[3]));
            }
        }
        showItemDelTimer();
        checkArmorSet(); // 套裝能力顯示
        return _os;
    }

    /**
     * 武器
     *
     */
    private BinaryOutputStream weapon() {
        _os.writeC(1);
        _os.writeC(_item.getDmgSmall());
        _os.writeC(_item.getDmgLarge());
        _os.writeC(_item.getMaterial());
        _os.writeD(_itemInstance.getWeight());
        if (_itemInstance.getEnchantLevel() != 0) {
            _os.writeC(2);
            _os.writeC(_itemInstance.getEnchantLevel());
        }
        // 武器劍靈系統
        L1WeaponSoul weaponsoul = WeaponSoul.getInstance().getTemplate(_itemInstance.getItemId());
        if (weaponsoul != null) {
            final int updateweaponsoul = _itemInstance.getUpdateWeaponSoul();
            if (updateweaponsoul > 0) {
                _os.writeC(0x27);
                _os.writeS("\\f3劍靈值：" + updateweaponsoul);
            } else {
                _os.writeC(0x27);
                _os.writeS("\\f3劍靈值：0");
            }
        }
        if (_itemInstance.get_durability() != 0) {
            _os.writeC(3);
            _os.writeC(_itemInstance.get_durability());
        }
        if (_item.isTwohandedWeapon()) {
            _os.writeC(4);
        }
        int get_addstr = _item.get_addstr() + _itemInstance.getItemStr();
        int get_adddex = _item.get_adddex() + _itemInstance.getItemDex();
        int get_addcon = _item.get_addcon() + _itemInstance.getItemCon();
        int get_addwis = _item.get_addwis() + _itemInstance.getItemWis();
        int get_addint = _item.get_addint() + _itemInstance.getItemInt();
        int get_addcha = _item.get_addcha() + _itemInstance.getItemCha();
        int get_addhp = _itemPower.get_addhp();
        int get_addmp = _itemPower.get_addmp();
        int mr = _itemPower.getMr();
        int addWeaponSp = _itemPower.getSp() + _itemInstance.getItemSp();
        int addDmgModifier = _item.getDmgModifier();
        int addHitModifier = _item.getHitModifier();
        int addmagicdmg = _item.getMagicDmgModifier();
        int pw_d4_1 = _item.get_defense_fire();
        int pw_d4_2 = _item.get_defense_water();
        int pw_d4_3 = _item.get_defense_wind();
        int pw_d4_4 = _item.get_defense_earth();
        int pw_k6_1 = _item.get_regist_freeze();
        int pw_k6_2 = _item.get_regist_stone();
        int pw_k6_3 = _item.get_regist_sleep();
        int pw_k6_4 = _item.get_regist_blind();
        int pw_k6_5 = _item.get_regist_stun();
        int pw_k6_6 = _item.get_regist_sustain();
        int pw_sHpr = _item.get_addhpr();
        int pw_sMpr = _itemPower.getMpr();
        int addexp = _item.getExpPoint();
        int stunlvl = _item.get_stunlvl();
        int safeenchant = _item.get_safeenchant();
        if (safeenchant >= 0) {
            _os.writeC(39);
            _os.writeS("安定值: " + _item.get_safeenchant());
        }
        if (addHitModifier != 0) {
            if (_item.getType() == 4 || _item.getType() == 13) {
                //_os.writeC(39);
                //_os.writeS("遠距離命中 +" + addHitModifier);
                _os.writeC(24); // 遠距離命中率
                _os.writeC(addHitModifier);
            } else {
                _os.writeC(48); // 近戰命中
                _os.writeC(addHitModifier);
            }
        }
        if (addDmgModifier != 0) {
            if (_item.getType() == 4 || _item.getType() == 13) {
                //_os.writeC(39);
                //_os.writeS("遠距離攻擊力 +" + addDmgModifier );
                _os.writeC(35); // 遠距離傷害
                _os.writeC(addDmgModifier);
            } else {
                _os.writeC(47); // 近戰攻擊
                _os.writeC(addDmgModifier);
            }
        }
        if (addmagicdmg != 0) {
            _os.writeC(39);
            _os.writeS("魔法傷害: " + addmagicdmg);
        }
        int bit = 0;
        bit |= (_item.isUseRoyal() ? 1 : 0);
        bit |= (_item.isUseKnight() ? 2 : 0);
        bit |= (_item.isUseElf() ? 4 : 0);
        bit |= (_item.isUseMage() ? 8 : 0);
        bit |= (_item.isUseDarkelf() ? 16 : 0);
        bit |= (_item.isUseDragonknight() ? 32 : 0);
        bit |= (_item.isUseIllusionist() ? 64 : 0);
        bit |= (_item.isUseWarrior() ? 128 : 0);
        _os.writeC(7);
        _os.writeC(bit);
        if ((_itemInstance.getItemId() == 126) || (_itemInstance.getItemId() == 127) || (_itemInstance.getItemId() == 259) || (_itemInstance.getItemId() == 305) || (_itemInstance.getItemId() == 310) || (_itemInstance.getItemId() == 315)) {
            _os.writeC(16);
        }
        if ((_itemInstance.getItemId() == 262) || (_itemInstance.getItemId() == 410157) || (_itemInstance.getItemId() == 12) || (_itemInstance.getItemId() == 410117) || (_itemInstance.getItemId() == 410164)) {
            _os.writeC(34);
        }
        if (get_addstr != 0) {
            _os.writeC(8);
            _os.writeC(get_addstr);
        }
        if (get_adddex != 0) {
            _os.writeC(9);
            _os.writeC(get_adddex);
        }
        if (get_addcon != 0) {
            _os.writeC(10);
            _os.writeC(get_addcon);
        }
        if (get_addwis != 0) {
            _os.writeC(11);
            _os.writeC(get_addwis);
        }
        if (get_addint != 0) {
            _os.writeC(12);
            _os.writeC(get_addint);
        }
        if (get_addcha != 0) {
            _os.writeC(13);
            _os.writeC(get_addcha);
        }
        if (get_addhp != 0) {
            _os.writeC(14);
            _os.writeH(get_addhp);
        }
        if (get_addmp != 0) {
            _os.writeC(0x20);
            _os.writeH(get_addmp);
        }
        // 體力回復量
        if (pw_sHpr != 0) {
            _os.writeC(37);
            _os.writeC(pw_sHpr);
        }
        // 魔力回復量
        if (pw_sMpr != 0) {
            _os.writeC(38);
            _os.writeC(pw_sMpr);
        }
        if (mr != 0) {
            _os.writeC(15);
            _os.writeH(mr);
        }
        if (addWeaponSp != 0) {
            _os.writeC(17);
            _os.writeC(addWeaponSp);
        }
        if (_item.isHasteItem()) {
            _os.writeC(18);
        }
        if (pw_d4_1 != 0) {
            _os.writeC(27);
            _os.writeC(pw_d4_1);
        }
        if (pw_d4_2 != 0) {
            _os.writeC(28);
            _os.writeC(pw_d4_2);
        }
        if (pw_d4_3 != 0) {
            _os.writeC(29);
            _os.writeC(pw_d4_3);
        }
        if (pw_d4_4 != 0) {
            _os.writeC(30);
            _os.writeC(pw_d4_4);
        }
        if (pw_k6_1 != 0) {
            // _os.writeC(15);
            _os.writeC(33);
            _os.writeC(1);
            _os.writeC(pw_k6_1);
        }
        if (pw_k6_2 != 0) {
            // _os.writeC(15);
            _os.writeC(33);
            _os.writeC(2);
            _os.writeC(pw_k6_2);
        }
        if (pw_k6_3 != 0) {
            // _os.writeC(15);
            _os.writeC(33);
            _os.writeC(3);
            _os.writeC(pw_k6_3);
        }
        if (pw_k6_4 != 0) {
            // _os.writeC(15);
            _os.writeC(33);
            _os.writeC(4);
            _os.writeC(pw_k6_4);
        }
        if (pw_k6_5 != 0) {
            // _os.writeC(15);
            _os.writeC(33);
            _os.writeC(5);
            _os.writeC(pw_k6_5);
        }
        if (pw_k6_6 != 0) {
            // _os.writeC(15);
            _os.writeC(33);
            _os.writeC(6);
            _os.writeC(pw_k6_6);
        }
        if (addexp != 0) { // 經驗加成 2017/0425
            if (addexp <= 120) {
                _os.writeC(36);
                _os.writeC(addexp);
            } else {
                this._os.writeC(39);
                this._os.writeS("$6134 " + addexp + "%");
            }
        }
        if (stunlvl != 0) { // 昏迷等級
            _os.writeC(98);
            _os.writeD(23521); // 昏迷等級提升 +%d
            _os.writeH(stunlvl);
        }
        int addpvpdmg = _item.getPvpDmg(); // 增加PVP傷害
        if (addpvpdmg != 0) {
            _os.writeC(59);
            _os.writeC(addpvpdmg);
        }
        int addpvpdmg_r = _item.getPvpDmg_R(); // 减免PVP傷害
        if (addpvpdmg_r != 0) {
            _os.writeC(60);
            _os.writeC(addpvpdmg_r);
        }
        // 魔法武器DIY系統
        final L1ItemPower_name power = this._itemInstance.get_power_name();
        if (power != null) {
            final L1CriticalHitStone criticalHitStone = power.get_critical_hit_stone();
            if (criticalHitStone != null) {
                this._os.writeC(0x27);
                final StringBuilder name = new StringBuilder().append(criticalHitStone.getName());
                this._os.writeS(name.toString());
            }
            final L1MagicWeapon magicWeapon = power.get_magic_weapon();
            if (magicWeapon != null) {
                this._os.writeC(0x27);
                final StringBuilder name = new StringBuilder().append(magicWeapon.getSkillName());
                // 使用期限
                if (magicWeapon.getMaxUseTime() > 0 && power.get_date_time() != null) {
                    name.append(delTime.format(power.get_date_time()));
                }
                this._os.writeS(name.toString());
            }
            L1BossWeapon bossWeapon = power.get_boss_weapon();
            if (bossWeapon != null) {
                this._os.writeC(0x27);
                final StringBuilder name = new StringBuilder().append(bossWeapon.getBossName());
                if (bossWeapon.getMaxUseTime() > 0 && power.get_boss_date_time() != null) {
                    name.append(delTime.format(power.get_boss_date_time()));
                }
                this._os.writeS(name.toString());
            }
        }
        final L1ItemPower_bless bless = this._itemInstance.get_power_bless();
        if (bless != null) {
            StringBuilder stonepower = new StringBuilder();
            StringBuilder stringBuilder = new StringBuilder();
            StringBuilder stringBuilder1 = new StringBuilder();
            if (bless.get_hole_count() > 0) {
                this._os.writeC(0x27);
                for (int i = 0; i < bless.get_hole_count(); i++) {
                    switch (i) {
                        case 0:
                            stonepower.append(set_hole_name(bless.get_hole_1()));
                            break;
                        case 1:
                            stonepower.append(set_hole_name(bless.get_hole_2()));
                            break;
                        case 2:
                            stonepower.append(set_hole_name(bless.get_hole_3()));
                            break;
                        case 3:
                            stonepower.append(set_hole_name(bless.get_hole_4()));
                            break;
                        case 4:
                            stonepower.append(set_hole_name(bless.get_hole_5()));
                            break;
                    }
                }
                this._os.writeS(stonepower.toString());
            }
            if ((bless.get_hole_str() != 0) || (bless.get_hole_dex() != 0) || (bless.get_hole_int() != 0) || (bless.get_hole_dmg() != 0) || (bless.get_hole_bowdmg() != 0) || (bless.get_hole_mcdmg() != 0)) {
                if (bless.get_hole_str() >= 0) {
                    stringBuilder.append("力+").append(bless.get_hole_str()).append(" ");
                } else {
                    stringBuilder.append("力").append(bless.get_hole_str()).append(" ");
                }
                if (bless.get_hole_dex() >= 0) {
                    stringBuilder.append("敏+").append(bless.get_hole_dex()).append(" ");
                } else {
                    stringBuilder.append("敏").append(bless.get_hole_dex()).append(" ");
                }
                if (bless.get_hole_int() >= 0) {
                    stringBuilder.append("智+").append(bless.get_hole_int()).append(" ");
                } else {
                    stringBuilder.append("智").append(bless.get_hole_int()).append(" ");
                }
                if (bless.get_hole_dmg() >= 0) {
                    stringBuilder1.append("近戰+").append(bless.get_hole_dmg()).append(" ");
                } else {
                    stringBuilder1.append("近戰").append(bless.get_hole_dmg()).append(" ");
                }
                if (bless.get_hole_bowdmg() >= 0) {
                    stringBuilder1.append("遠弓+").append(bless.get_hole_bowdmg()).append(" ");
                } else {
                    stringBuilder1.append("遠弓").append(bless.get_hole_bowdmg()).append(" ");
                }
                if (bless.get_hole_mcdmg() >= 0) {
                    stringBuilder1.append("魔法+").append(bless.get_hole_mcdmg()).append(" ");
                } else {
                    stringBuilder1.append("魔法").append(bless.get_hole_mcdmg()).append(" ");
                }
                // this._os.writeC(39);
                // this._os.writeS("祝福強化:");
                this._os.writeC(39);
                this._os.writeS(stringBuilder.toString().trim());
                this._os.writeC(39);
                this._os.writeS(stringBuilder1.toString().trim());
            }
        }
        // TODO PVP系統 武器傷害增加
		/*if (ConfigOther.PVP_WEAPON && _itemInstance.getItemId() >= 1 && _itemInstance.getItemId() <= 5000000
				&& _itemInstance.getEnchantLevel() >= ConfigOther.PVP_plus) {
			_os.writeC(59);
			_os.writeC(_itemInstance.getEnchantLevel() - ConfigOther.PVP_plus + 1);
		}*/
        if (_itemInstance.getItemAttack() != 0 || _itemInstance.getItemBowAttack() != 0) {
            _os.writeC(59); // 增加PVP傷害 ？
            _os.writeC(_itemInstance.getItemAttack() + _itemInstance.getItemBowAttack());
        }
        /**
         * 2025/04/04 老爹
         * 昇華顯示（獨立顯示，不影響其他擴充能力邏輯）
         */
        if (_itemInstance.getSublimation() != null) {
            CharItemSublimation sub = _itemInstance.getSublimation();
            String title = sub.get_item_name() != null ? sub.get_item_name() : "未命名";
            int level = sub.getLv();
            // 顯示名稱與等級
            _os.writeC(0x27);
            _os.writeS("\\f2昇華：" + title + " Lv." + level);
            // 顯示昇華能力細節（從資料表讀取）
            L1ItemSublimation data = ItemSublimationTable.get().getItemSublimation(sub);
            if (data != null) {
                if (data.getDmgChanceHp() > 0) {
                    _os.writeC(0x27);
                    _os.writeS("傷害機率 +" + data.getDmgChanceHp() + "%");
                }
                if (data.getDmgChanceMp() > 0) {
                    _os.writeC(0x27);
                    _os.writeS("傷害機率 +" + data.getDmgChanceMp() + "%");
                }
                if (data.isWithstandDmg()) {
                    _os.writeC(0x27);
                    _os.writeS("抵擋物理傷害");
                }
                if (data.isWithstandMagic()) {
                    _os.writeC(0x27);
                    _os.writeS("可抵擋魔法傷害");
                }
                if (data.isReturnDmg()) {
                    _os.writeC(0x27);
                    _os.writeS("反彈物理攻擊");
                }
                if (data.isReturnMagic()) {
                    _os.writeC(0x27);
                    _os.writeS("反彈魔法攻擊");
                }
                if (data.isReturnSkills()) {
                    _os.writeC(0x27);
                    _os.writeS("反彈技能效果");
                }
                if (data.getReturnChanceHp() > 0) {
                    _os.writeC(0x27);
                    _os.writeS("反彈 HP 傷害機率 +" + data.getReturnChanceHp() + "%");
                }
                if (data.getReturnChanceMp() > 0) {
                    _os.writeC(0x27);
                    _os.writeS("反彈 MP 傷害機率 +" + data.getReturnChanceMp() + "%");
                }
                if (data.getMessage() != null && !data.getMessage().isEmpty()) {
                    _os.writeC(0x27);
                    _os.writeS("" + data.getMessage());
                }
            }
        }



//        // 強化擴充能力（維持原邏輯，不改動）
//        if (_itemInstance.getUpdateStr() != 0 || _itemInstance.getUpdateDex() != 0 || _itemInstance.getUpdateCon() != 0 || _itemInstance.getUpdateWis() != 0 || _itemInstance.getUpdateInt() != 0 || _itemInstance.getUpdateCha() != 0 || _itemInstance.getUpdateHp() != 0 || _itemInstance.getUpdateMp() != 0 || _itemInstance.getUpdateEarth() != 0 || _itemInstance.getUpdateWind() != 0 || _itemInstance.getUpdateWater() != 0 || _itemInstance.getUpdateFire() != 0 || _itemInstance.getUpdateMr() != 0 || _itemInstance.getUpdateAc() != 0 || _itemInstance.getUpdateHpr() != 0 || _itemInstance.getUpdateMpr() != 0 || _itemInstance.getUpdateSp() != 0 || _itemInstance.getUpdateDmgModifier() != 0 || _itemInstance.getUpdateHitModifier() != 0 || _itemInstance.getUpdateBowDmgModifier() != 0 || _itemInstance.getUpdateBowHitModifier() != 0 || _itemInstance.getUpdatePVPdmg() != 0 || _itemInstance.getUpdatePVPdmg_R() != 0) {
//            _os.writeC(0x27);
//            _os.writeS("\\f3擴充能力：");
//        }

        // 以下原邏輯完全保留（能力逐項寫入）
        final int updatepvpdmg = _itemInstance.getUpdatePVPdmg(); // pvp攻擊
        if (updatepvpdmg != 0) {
            _os.writeC(0x27);
            _os.writeS("\\f2PVP 額外傷害 +" + updatepvpdmg);
        }

        final int updatepvpdmg_r = _itemInstance.getUpdatePVPdmg_R();// pvp減免
        if (updatepvpdmg_r != 0) {
            _os.writeC(0x27);
            _os.writeS("\\f2PVP 傷害減免 +" + updatepvpdmg_r);
            //_os.writeC(60);
            //_os.writeC(updatepvpdmg_r);
        }
        final int updateac = _itemInstance.getUpdateAc();// 防禦
        if (updateac != 0) {
            _os.writeC(56); // 額外防禦
            _os.writeC(updateac);
        }
        final int updatedmg = _itemInstance.getUpdateDmgModifier();// 近戰攻擊
        if (updatedmg != 0) {
            _os.writeC(47);
            _os.writeC(updatedmg);
        }
        final int updatehit = _itemInstance.getUpdateHitModifier();// 近戰命中
        if (updatehit != 0) {
            _os.writeC(48);
            _os.writeC(updatehit);
        }
        final int updatebowdmg = _itemInstance.getUpdateBowDmgModifier();// 遠攻攻擊
        if (updatebowdmg != 0) {
            _os.writeC(35);
            _os.writeC(updatebowdmg);
        }
        final int updatebowhit = _itemInstance.getUpdateBowHitModifier();// 遠攻命中
        if (updatebowhit != 0) {
            _os.writeC(24);
            _os.writeC(updatebowhit);
        }
        final int updatestr = _itemInstance.getUpdateStr();// 力量
        if (updatestr != 0) {
            _os.writeC(8);
            _os.writeC(updatestr);
        }
        final int updatedex = _itemInstance.getUpdateDex();// 敏捷
        if (updatedex != 0) {
            _os.writeC(9);
            _os.writeC(updatedex);
        }
        final int updatecon = _itemInstance.getUpdateCon();// 體質
        if (updatecon != 0) {
            _os.writeC(10);
            _os.writeC(updatecon);
        }
        final int updatewis = _itemInstance.getUpdateWis();// 精神
        if (updatewis != 0) {
            _os.writeC(11);
            _os.writeC(updatewis);
        }
        final int updateint = _itemInstance.getUpdateInt();// 智力
        if (updateint != 0) {
            _os.writeC(12);
            _os.writeC(updateint);
        }
        final int updatecha = _itemInstance.getUpdateCha();// 魅力
        if (updatecha != 0) {
            _os.writeC(13);
            _os.writeC(updatecha);
        }
        final int updatehp = _itemInstance.getUpdateHp();// 血量
        if (updatehp != 0) {
            _os.writeC(14);
            _os.writeH(updatehp);
        }
        final int updatemp = _itemInstance.getUpdateMp();// 魔量
        if (updatemp != 0) {
            _os.writeC(0x20);
            _os.writeH(updatemp);
        }
        final int updatehpr = _itemInstance.getUpdateHpr();// 回血
        if (updatehpr != 0) {
            _os.writeC(37);
            _os.writeC(updatehpr);
        }
        final int updatempr = _itemInstance.getUpdateMpr();// 回魔
        if (updatempr != 0) {
            _os.writeC(38);
            _os.writeC(updatempr);
        }
        final int updatemr = _itemInstance.getUpdateMr();// 抗魔
        if (updatemr != 0) {
            _os.writeC(15);
            _os.writeH(updatemr);
        }
        final int updatesp = _itemInstance.getUpdateSp();// 魔法攻擊
        if (updatesp != 0) {
            _os.writeC(17);
            _os.writeC(updatesp);
        }
        final int updatefire = _itemInstance.getUpdateFire();// 火屬性
        if (updatefire != 0) {
            _os.writeC(27);
            _os.writeC(updatefire);
        }
        final int updatewater = _itemInstance.getUpdateWater();// 水屬性
        if (updatewater != 0) {
            _os.writeC(28);
            _os.writeC(updatewater);
        }
        final int updatewind = _itemInstance.getUpdateWind();// 風屬性
        if (updatewind != 0) {
            _os.writeC(29);
            _os.writeC(updatewind);
        }
        final int updateearth = _itemInstance.getUpdateEarth();// 地屬性
        if (updateearth != 0) {
            _os.writeC(30);
            _os.writeC(updateearth);
        }
        // 強化擴充能力 end
		/*if (_statusx) {
			if (!_item.isTradable()) {
				_os.writeC(39);
				_os.writeS("無法交易");
			}
			if (_item.isCantDelete()) {
				_os.writeC(39);
				_os.writeS("無法刪除");
			}
			if (_item.get_safeenchant() < 0) {
				_os.writeC(39);
				_os.writeS("無法強化");
			}
		}*/
        /**祝福化說明系統 台灣JAVA技術老爹製作 LINE:@422ygvzx*/
        if (_itemInstance.getBless() == 0) {
            int item = _item.getItemId();
            L1Zhufu zhufu = Zhufu.getInstance().getTemplate(item, 1);
            if (zhufu == null) {
                zhufu = Zhufu.getInstance().getTemplateByType(_item.getType(), 1);
            }
            if (zhufu != null) {
                _os.writeC(39);
                _os.writeS("祝福化屬性：");
                if (zhufu.getAddStr() != 0) {
                    _os.writeC(39);
                    _os.writeS("力量：+" + zhufu.getAddStr());
                }
                if (zhufu.getAddDex() != 0) {
                    _os.writeC(39);
                    _os.writeS("敏捷：+" + zhufu.getAddDex());
                }
                if (zhufu.getAddCon() != 0) {
                    _os.writeC(39);
                    _os.writeS("體質：+" + zhufu.getAddCon());
                }
                if (zhufu.getAddInt() != 0) {
                    _os.writeC(39);
                    _os.writeS("智力：+" + zhufu.getAddInt());
                }
                if (zhufu.getAddWis() != 0) {
                    _os.writeC(39);
                    _os.writeS("精神：+" + zhufu.getAddWis());
                }
                if (zhufu.getAddCha() != 0) {
                    _os.writeC(39);
                    _os.writeS("魅力：+" + zhufu.getAddCha());
                }
                if (zhufu.getAddAc() != 0) {
                    _os.writeC(39);
                    _os.writeS("防禦：+" + zhufu.getAddAc());
                }
                if (zhufu.getAddMaxHp() != 0) {
                    _os.writeC(39);
                    _os.writeS("HP：+" + zhufu.getAddMaxHp());
                }
                if (zhufu.getAddMaxMp() != 0) {
                    _os.writeC(39);
                    _os.writeS("MP：+" + zhufu.getAddMaxMp());
                }
                if (zhufu.getAddHpr() != 0) {
                    _os.writeC(39);
                    _os.writeS("回血：+" + zhufu.getAddHpr());
                }
                if (zhufu.getAddMpr() != 0) {
                    _os.writeC(39);
                    _os.writeS("回魔：+" + zhufu.getAddMpr());
                }
                if (zhufu.getAddDmg() != 0) {
                    _os.writeC(39);
                    _os.writeS("近距離傷害：+" + zhufu.getAddDmg());
                }
                if (zhufu.getAddHit() != 0) {
                    _os.writeC(39);
                    _os.writeS("近距離命中：+" + zhufu.getAddHit());
                }
                if (zhufu.getAddBowDmg() != 0) {
                    _os.writeC(39);
                    _os.writeS("遠距離傷害：+" + zhufu.getAddBowDmg());
                }
                if (zhufu.getAddBowHit() != 0) {
                    _os.writeC(39);
                    _os.writeS("遠距離命中：+" + zhufu.getAddBowHit());
                }
                if (zhufu.getReduction_dmg() != 0) {
                    _os.writeC(39);
                    _os.writeS("物理傷害減免：+" + zhufu.getReduction_dmg());
                }
                if (zhufu.getAddMr() != 0) {
                    _os.writeC(39);
                    _os.writeS("抗魔：+" + zhufu.getAddMr());
                }
                if (zhufu.getAddSp() != 0) {
                    _os.writeC(39);
                    _os.writeS("魔攻：+" + zhufu.getAddSp());
                }
                if (zhufu.getAddFire() != 0) {
                    _os.writeC(39);
                    _os.writeS("抗火屬性：+" + zhufu.getAddFire());
                }
                if (zhufu.getAddWind() != 0) {
                    _os.writeC(39);
                    _os.writeS("抗風屬性：+" + zhufu.getAddWind());
                }
                if (zhufu.getAddEarth() != 0) {
                    _os.writeC(39);
                    _os.writeS("抗地屬性：+" + zhufu.getAddEarth());
                }
                if (zhufu.getAddWater() != 0) {
                    _os.writeC(39);
                    _os.writeS("抗水屬性：+" + zhufu.getAddWater());
                }
                if (zhufu.getwljm() != 0) {
                    _os.writeC(39);
                    _os.writeS("PVP傷害減免：+" + zhufu.getwljm());
                }
                if (zhufu.getwljmbai() != 0) {
                    _os.writeC(39);
                    _os.writeS("PVP傷害減免：+" + zhufu.getwljmbai() + "%");
                }
                if (zhufu.getmojm() != 0) {
                    _os.writeC(39);
                    _os.writeS("PVP魔法傷害減免：+" + zhufu.getmojm());
                }
                if (zhufu.getmojmbai() != 0) {
                    _os.writeC(39);
                    _os.writeS("PVP魔法傷害減免：+" + zhufu.getmojmbai() + "%");
                }
            }
        }
        /* 祝福化說明系統  */
        L1ItemSpecialAttributeChar attr_char = _itemInstance.get_ItemAttrName();
        if (attr_char != null) {
            L1ItemSpecialAttribute attr = ItemSpecialAttributeTable.get().getAttrId(attr_char.get_attr_id());
            if (attr != null) {
                _os.writeC(39);
                _os.writeS("【炫色屬性】");
                if (attr.get_add_cha() != 0) {
                    _os.writeC(39);
                    _os.writeS("魅力 + " + attr.get_add_cha());
                }
                if (attr.get_add_con() != 0) {
                    _os.writeC(39);
                    _os.writeS("體質 + " + attr.get_add_con());
                }
                if (attr.get_add_dex() != 0) {
                    _os.writeC(39);
                    _os.writeS("敏捷 + " + attr.get_add_dex());
                }
                if (attr.get_add_str() != 0) {
                    _os.writeC(39);
                    _os.writeS("力量 + " + attr.get_add_str());
                }
                if (attr.get_add_wis() != 0) {
                    _os.writeC(39);
                    _os.writeS("精神 + " + attr.get_add_wis());
                }
                if (attr.get_add_int() != 0) {
                    _os.writeC(39);
                    _os.writeS("智力 + " + attr.get_add_int());
                }
                if (attr.get_add_drain_max_hp() != 0) {
                    _os.writeC(39);
                    _os.writeS("炫色吸取體力");
                }
                if (attr.get_add_drain_max_mp() != 0) {
                    _os.writeC(39);
                    _os.writeS("炫色吸取魔力");
                }
                if (attr.get_add_hp() != 0) {
                    _os.writeC(39);
                    _os.writeS("Hp + " + attr.get_add_hp());
                }
                if (attr.get_add_mp() != 0) {
                    _os.writeC(39);
                    _os.writeS("Mp + " + attr.get_add_mp());
                }
                if (attr.get_add_hpr() != 0) {
                    _os.writeC(39);
                    _os.writeS("體力回復 + " + attr.get_add_hpr());
                }
                if (attr.get_add_mpr() != 0) {
                    _os.writeC(39);
                    _os.writeS("魔力回復 + " + attr.get_add_mpr());
                }
                if (attr.get_add_m_def() != 0) {
                    _os.writeC(39);
                    _os.writeS("魔防 + " + attr.get_add_m_def());
                }
                if (attr.get_add_sp() != 0) {
                    _os.writeC(39);
                    _os.writeS("魔攻 + " + attr.get_add_sp());
                }
                if (attr.get_add_skill_dmg() != 0) {
                    _os.writeC(39);
                    _os.writeS("魔法傷害 + " + attr.get_add_skill_dmg());
                }
                if (attr.get_dmg_large() != 0) {
                    _os.writeC(39);
                    _os.writeS("最大攻擊力 + " + attr.get_dmg_large());
                }
                if (attr.get_dmg_small() != 0) {
                    _os.writeC(39);
                    _os.writeS("最小攻擊力 + " + attr.get_dmg_small());
                }
                if (attr.get_dmgmodifier() != 0) {
                    _os.writeC(39);
                    _os.writeS("額外攻擊力 + " + attr.get_dmgmodifier());
                }
                if (attr.get_hitmodifier() != 0) {
                    _os.writeC(39);
                    _os.writeS("命中 + " + attr.get_hitmodifier());
                }
                if (attr.getShanghaijianmian() != 0) {
                    _os.writeC(39);
                    _os.writeS("傷害減免 + " + attr.getShanghaijianmian());
                }
            }
        }
        /**武器加成顯示*/
        //		NewEnchantSystem AE_List = NewEnchantSystem.get().get(this._itemInstance.getItemId(), _itemInstance.getEnchantLevel());
        //		//ArmorEnchantSystem AE_List = ArmorEnchantSystem.get().get(this._itemInstance.getItemId(), enchantLevel);
        //
        //		if (AE_List != null) {
        //			_os.writeC(0x27);
        //			_os.writeS("武器強化加成:");
        //			if (AE_List.getHp() != 0) {
        //				_os.writeC(0x27);
        //				_os.writeS("HP+"+ AE_List.getHp());
        //			}
        //			if (AE_List.getMp() != 0) {
        //				_os.writeC(0x27);
        //				_os.writeS("MP+"+ AE_List.getMp());
        //			}
        //			if (AE_List.getExpUp() != 0) {
        //				_os.writeC(0x27);
        //				_os.writeS("狩獵經驗值+"+ AE_List.getExpUp());
        //			}
        //			if (AE_List.getCritDmg() != 0) {
        //				_os.writeC(0x27);
        //				_os.writeS("暴擊傷害倍率+"+ AE_List.getCritDmg());
        //			}
        //			if (AE_List.getExtraMagicDmg() != 0) {
        //				_os.writeC(0x27);
        //				_os.writeS("魔法傷害+"+ AE_List.getExtraMagicDmg());
        //			}
        //			if (AE_List.getExtraDmg() != 0) {
        //				_os.writeC(0x27);
        //				_os.writeS("物理傷害+"+ AE_List.getExtraDmg());
        //			}
        //			if (AE_List.getHit() != 0) {
        //				_os.writeC(0x27);
        //				_os.writeS("物理命中+"+ AE_List.getHit());
        //			}
        //			if (AE_List.getsjj()) {
        //				_os.writeC(0x27);
        //				_os.writeS("破除聖結界");
        //			}
        //			if (AE_List.getsjjjilv() != 0) {
        //				_os.writeC(0x27);
        //				_os.writeS("破除聖結界機率+"+ AE_List.getsjjjilv());
        //			}
        //		}

        // 顯示屬性傷害資訊
        L1AttrWeapon attrWeapon = ExtraAttrWeaponTable.getInstance().get(_itemInstance.getAttrEnchantKind(), _itemInstance.getAttrEnchantLevel());
        if (attrWeapon != null) {
            if (attrWeapon.getAttrDmg() > 0) {
                _os.writeC(0x27);
                _os.writeS("屬性傷害+" + attrWeapon.getAttrDmg());
            }
            if (attrWeapon.getArrtDmgCritical() > 0) {
                _os.writeC(0x27);
                _os.writeS("屬性爆擊+" + attrWeapon.getArrtDmgCritical()+ "%");
            }
        }
        NewEnchantSystem AE_List2 = NewEnchantSystem.get().get2(_itemInstance.getSafeEnchantLevel(), _itemInstance.getEnchantLevel(), _item.getType());
        //ArmorEnchantSystem AE_List2 = ArmorEnchantSystem.get().get2(es);
        if (/*AE_List == null && */AE_List2 != null) {
//            _os.writeC(0x27);
//            _os.writeS("武器強化加成:");
            if (AE_List2.getHp() != 0) {
                _os.writeC(0x27);
                _os.writeS("HP+" + AE_List2.getHp());
            }
            if (AE_List2.getMp() != 0) {
                _os.writeC(0x27);
                _os.writeS("MP+" + AE_List2.getMp());
            }
            if (AE_List2.getExpUp() != 0) {
                _os.writeC(0x27);
                _os.writeS("狩獵經驗值+" + AE_List2.getExpUp());
            }
            if (AE_List2.getCritDmg() != 0) {
                _os.writeC(0x27);
                _os.writeS("傷害增加+" + AE_List2.getCritDmg() + "%");
            }
            if (AE_List2.getExtraMagicDmg() != 0) {
                _os.writeC(0x27);
                _os.writeS("魔法傷害+" + AE_List2.getExtraMagicDmg());
            }
            if (AE_List2.getExtraDmg() != 0) {
                _os.writeC(0x27);
                _os.writeS("額外攻擊+" + AE_List2.getExtraDmg());
            }
            if (AE_List2.getHit() != 0) {
                _os.writeC(0x27);
                _os.writeS("武器命中+" + AE_List2.getHit());
            }
            if (AE_List2.getsjj()) {
                _os.writeC(0x27);
                _os.writeS("破除聖結界");
            }
            if (AE_List2.getsjjjilv() != 0) {
                _os.writeC(0x27);
                _os.writeS("破除聖結界機率+" + AE_List2.getsjjjilv() + "%");
            }
        }

        // terry770106
        int dr = _itemInstance.getItemprobability();

        L1WeaponSkill weaponSkill = WeaponSkillTable.get().getTemplate(_itemInstance.getItemId());
        if (weaponSkill != null) {
            // 技能名稱
            String skillName = weaponSkill.getSkillName(); // 例如：衝擊之暈眩、寒冰風暴等
            // 計算強化加成：+0/+1→1，+2以上照強化值
            int enchantBonus = Math.max(_itemInstance.getEnchantLevel(), 1);
            // 最終發動機率
            int baseProbability = weaponSkill.getProbability();
            int probability = baseProbability + enchantBonus;
            // 顯示
            _os.writeC(74); // 這行你原本就有
            _os.writeS(skillName + probability + "%");
        }

        // 2017/04/21
        ArrayList<String> as = new ArrayList<>();
        try {
            for (String s : WilliamItemMessage.getItemInfo(_itemInstance)) {
                if (s != null && !s.isEmpty()) {
                    _os.writeC(39);
                    _os.writeS(s);
                }
            }
        } finally {
            as.clear();
        }
        showItemDelTimer();
        return _os;
    }

    /**
     * 一般道具
     *
     */
    private BinaryOutputStream etcitem() {
        if (_item.getItemId() == 40312) {// 旅館鑰匙
            _os.writeC(39);
            _os.writeS("旅館編號:" + _itemInstance.getInnKeyName());
            _os.writeC(39);
            _os.writeS("到期時間:(" + _itemInstance.getDueTime() + ")");
        }
        if (_item.getItemId() == 82503) {// 訓練所鑰匙
            _os.writeC(39);
            _os.writeS("訓練所編號:" + _itemInstance.getKeyId());
            _os.writeC(39);
            _os.writeS("到期時間:(" + _itemInstance.getDueTime() + ")");
        }
        if (_item.getItemId() == 640321) { // 下層戰鬥強化卷軸
            _os.writeC(47); // 近距離傷害+30
            _os.writeC(30); // 近距離傷害+30
            _os.writeC(48); // 近距離命中率+30
            _os.writeC(30); // 近距離命中率+30
            _os.writeC(35); // 遠距離攻擊+30
            _os.writeC(30); // 遠距離攻擊+30
            _os.writeC(24); // 遠距離命中率+30
            _os.writeC(30); // 遠距離命中率+30
            _os.writeC(17); // 魔攻+30
            _os.writeC(30); // 魔攻+30
            _os.writeC(39);
            _os.writeS("\\f2限屍魂副本使用");
            //			_os.writeC(39);
            //			_os.writeS("\\f3效果時間： 600秒");
        }
        if (_item.getItemId() == 640322) { // 下層防禦強化卷軸
            _os.writeC(56); // 額外防禦+50
            _os.writeC(50); // 額外防禦+50
            _os.writeC(39);
            _os.writeS("\\f2限屍魂副本使用");
            //			_os.writeC(39);
            //			_os.writeS("\\f3效果時間： 600秒");
        }
        if (_item.getItemId() >= 640404 && _item.getItemId() <= 640406) { // 潘朵拉近戰魔石
            _os.writeC(14); // 體力上限+50
            _os.writeH(50); // 體力上限+50
            _os.writeC(47); // 近距離傷害+2
            _os.writeC(2);  // 近距離傷害+2
            _os.writeC(37); // 體力回復量+3
            _os.writeC(3);  // 體力回復量+3
            _os.writeC(8); // 力量+1
            _os.writeC(1); // 力量+1
            _os.writeC(39);
            _os.writeS("\\f2使用需魔法結晶體100個");
        }
        if (_item.getItemId() >= 640407 && _item.getItemId() <= 640409) { // 潘朵拉遠攻魔石
            _os.writeC(14); // 體力上限+25
            _os.writeH(25); // 體力上限+25
            _os.writeC(35); // 遠距離攻擊+2
            _os.writeC(2);  // 遠距離攻擊+2
            _os.writeC(0x20); // 魔力上限+25
            _os.writeH(25);   // 魔力上限+25
            _os.writeC(37); // 體力回復量+1
            _os.writeC(1);  // 體力回復量+1
            _os.writeC(38); // 魔力回復量+1
            _os.writeC(1);  // 魔力回復量+1
            _os.writeC(9); // 敏捷+1
            _os.writeC(1); // 敏捷+1
            _os.writeC(39);
            _os.writeS("\\f2使用需魔法結晶體100個");
        }
        if (_item.getItemId() >= 640410 && _item.getItemId() <= 640412) { // 潘朵拉魔攻魔石
            _os.writeC(0x20); // 魔力上限+50
            _os.writeH(50);   // 魔力上限+50
            _os.writeC(38); // 魔力回復量+3
            _os.writeC(3);  // 魔力回復量+3
            _os.writeC(12); // 智力+1
            _os.writeC(1);  // 智力+1
            _os.writeC(17); // 魔攻+2
            _os.writeC(2);  // 魔攻+2
            _os.writeC(39);
            _os.writeS("\\f2使用需魔法結晶體100個");
        }
        if (_item.getItemId() >= 640413 && _item.getItemId() <= 640415) { // 潘朵拉防禦魔石
            _os.writeC(14); // 體力上限+30
            _os.writeH(30); // 體力上限+30
            _os.writeC(0x20); // 魔力上限+30
            _os.writeH(30);   // 魔力上限+30
            _os.writeC(38); // 魔力回復量+3
            _os.writeC(3);  // 魔力回復量+3
            _os.writeC(56); // 額外防禦+5
            _os.writeC(5);  // 額外防禦+5
            _os.writeC(15); // 魔法防禦+10
            _os.writeH(10); // 魔法防禦+10
            _os.writeC(63); // 傷害減免+2
            _os.writeC(1);  // 傷害減免+2
            _os.writeC(39);
            _os.writeS("\\f2使用需魔法結晶體100個");
        }
        // if (_item.getItemId() == 82504) {// 龍門憑證
        // String npcname =
        // NpcTable.get().getNpcName(_itemInstance.getInnNpcId());//龍門名稱
        // _os.writeC(39);
        // _os.writeS(npcname);
        // _os.writeC(39);
        // _os.writeS("副本編號:" + _itemInstance.getKeyId());
        // _os.writeC(39);
        // _os.writeS("到期時間:(" + _itemInstance.getDueTime() + ")");
        // }
        _os.writeC(23);
        _os.writeC(_item.getMaterial());
        _os.writeD(_itemInstance.getWeight());
		/*if (_statusx) {
			if (!_item.isTradable()) {
				_os.writeC(39);
				_os.writeS("無法交易");
			}
			if (_item.isCantDelete()) {
				_os.writeC(39);
				_os.writeS("無法刪除");
			}
			if (_item.get_safeenchant() < 0) {
				_os.writeC(39);
				_os.writeS("無法強化");
			}
		}*/
        // 2017/04/21
        ArrayList<String> as = new ArrayList<>();
        try {
            for (String s : WilliamItemMessage.getItemInfo(_itemInstance)) {
                if (s != null && !s.isEmpty()) {
                    _os.writeC(39);
                    _os.writeS(s);
                }
            }
        } finally {
            as.clear();
        }
        showItemDelTimer();
        return _os;
    }

    /**
     * 寵物防具
     *
     */
    private BinaryOutputStream petarmor(L1PetItem petItem) {
        _os.writeC(19);
        int ac = petItem.getAddAc();
        if (ac < 0) {
            ac = Math.abs(ac);
        }
        _os.writeC(ac);
        _os.writeC(_item.getMaterial());
        _os.writeC(-1); // 飾品級別 - 0:上等 1:中等 2:初等 3:特等
        _os.writeD(_itemInstance.getWeight());
        if (petItem.getHitModifier() != 0) {
            _os.writeC(5);
            _os.writeC(petItem.getHitModifier());
        }
        if (petItem.getDamageModifier() != 0) {
            _os.writeC(6);
            _os.writeC(petItem.getDamageModifier());
        }
        if (petItem.isHigher()) {
            // _os.writeC(7);
            // _os.writeC(128);
            _os.writeC(39);
            _os.writeS("高等寵物限定");
        }
        if (petItem.getAddStr() != 0) {
            _os.writeC(8);
            _os.writeC(petItem.getAddStr());
        }
        if (petItem.getAddDex() != 0) {
            _os.writeC(9);
            _os.writeC(petItem.getAddDex());
        }
        if (petItem.getAddCon() != 0) {
            _os.writeC(10);
            _os.writeC(petItem.getAddCon());
        }
        if (petItem.getAddWis() != 0) {
            _os.writeC(11);
            _os.writeC(petItem.getAddWis());
        }
        if (petItem.getAddInt() != 0) {
            _os.writeC(12);
            _os.writeC(petItem.getAddInt());
        }
        if (petItem.getAddHp() != 0) {
            _os.writeC(14);
            _os.writeH(petItem.getAddHp());
        }
        if (petItem.getAddMp() != 0) {
            _os.writeC(32);
            _os.writeC(petItem.getAddMp());
        }
        if (petItem.getAddMr() != 0) {
            _os.writeC(15);
            _os.writeH(petItem.getAddMr());
        }
        if (petItem.getAddSp() != 0) {
            _os.writeC(17);
            _os.writeC(petItem.getAddSp());
        }
		/*if (_statusx) {
			if (!_item.isTradable()) {
				_os.writeC(39);
				_os.writeS("無法交易");
			}
			if (_item.isCantDelete()) {
				_os.writeC(39);
				_os.writeS("無法刪除");
			}
			if (_item.get_safeenchant() < 0) {
				_os.writeC(39);
				_os.writeS("無法強化");
			}
		}*/
        // 2017/04/21
        ArrayList<String> as = new ArrayList<>();
        try {
            for (String s : WilliamItemMessage.getItemInfo(_itemInstance)) {
                if (s != null && !s.isEmpty()) {
                    _os.writeC(39);
                    _os.writeS(s);
                }
            }
        } finally {
            as.clear();
        }
        return _os;
    }

    /**
     * 寵物武器
     *
     */
    private BinaryOutputStream petweapon(L1PetItem petItem) {
        _os.writeC(1);
        _os.writeC(0);
        _os.writeC(0);
        _os.writeC(_item.getMaterial());
        _os.writeD(_itemInstance.getWeight());
        if (petItem.getHitModifier() != 0) {
            _os.writeC(5);
            _os.writeC(petItem.getHitModifier());
        }
        if (petItem.getDamageModifier() != 0) {
            _os.writeC(6);
            _os.writeC(petItem.getDamageModifier());
        }
        if (petItem.isHigher()) {
            // _os.writeC(7);
            // _os.writeC(128);
            _os.writeC(39);
            _os.writeS("高等寵物限定");
        }
        if (petItem.getAddStr() != 0) {
            _os.writeC(8);
            _os.writeC(petItem.getAddStr());
        }
        if (petItem.getAddDex() != 0) {
            _os.writeC(9);
            _os.writeC(petItem.getAddDex());
        }
        if (petItem.getAddCon() != 0) {
            _os.writeC(10);
            _os.writeC(petItem.getAddCon());
        }
        if (petItem.getAddWis() != 0) {
            _os.writeC(11);
            _os.writeC(petItem.getAddWis());
        }
        if (petItem.getAddInt() != 0) {
            _os.writeC(12);
            _os.writeC(petItem.getAddInt());
        }
        if (petItem.getAddHp() != 0) {
            _os.writeC(14);
            _os.writeH(petItem.getAddHp());
        }
        if (petItem.getAddMp() != 0) {
            _os.writeC(32);
            _os.writeC(petItem.getAddMp());
        }
        if (petItem.getAddMr() != 0) {
            _os.writeC(15);
            _os.writeH(petItem.getAddMr());
        }
        if (petItem.getAddSp() != 0) {
            _os.writeC(17);
            _os.writeC(petItem.getAddSp());
        }
		/*if (_statusx) {
			if (!_item.isTradable()) {
				_os.writeC(39);
				_os.writeS("無法交易");
			}
			if (_item.isCantDelete()) {
				_os.writeC(39);
				_os.writeS("無法刪除");
			}
			if (_item.get_safeenchant() < 0) {
				_os.writeC(39);
				_os.writeS("無法強化");
			}
		}*/
        // 2017/04/21
        ArrayList<String> as = new ArrayList<>();
        try {
            for (String s : WilliamItemMessage.getItemInfo(_itemInstance)) {
                if (s != null && !s.isEmpty()) {
                    _os.writeC(39);
                    _os.writeS(s);
                }
            }
        } finally {
            as.clear();
        }
        return _os;
    }

    /**
     * [0]:MP [1]:HP [2]:藥水回復量% [3]:近距離傷害 [4]:遠距離傷害 [5]:傷害減免 [6]:魔法命中 [7]:PVP傷害
     * [8]:防禦 [9]:魔攻 [10]:魔防 [11]:PVP傷害值減少
     *
     */
    private int[] greater() {
        int level = _itemInstance.getEnchantLevel();
        if (level < 0) {// 強化負值不顯示加成
            level = 0;
        }
        int[] rint = new int[12];
        switch (_itemInstance.getItem().get_greater()) {
            case 0:// 0:耐性(耳環/項鏈)
                // { MP,HP,藥水回復量%, 近距離傷害, 遠距離傷害, 傷害減免 , 魔法命中, PVP傷害, 防禦, 魔攻, 魔防,
                // PVP傷害值減少 }
                switch (level) {
                    case 0:
                        break;
                    case 1:
                        rint = new int[]{0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                        break;
                    case 2:
                        rint = new int[]{0, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                        break;
                    case 3:
                        rint = new int[]{0, 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                        break;
                    case 4:
                        rint = new int[]{0, 30, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0};
                        break;
                    case 5:
                        rint = new int[]{0, 40, 4, 2, 2, 1, 0, 0, 0, 0, 0, 0};
                        break;
                    case 6:
                        rint = new int[]{0, 40, 6, 4, 4, 2, 2, 0, 0, 0, 0, 0};
                        break;
                    case 7:// { MP,HP,藥水回復量%, 近距離傷害, 遠距離傷害, 傷害減免 , 魔法命中 , PVP傷害, 防禦, 魔攻,
                        // 魔防, PVP傷害值減少 }
                        rint = new int[]{0, 50, 10, 5, 5, 3, 4, 1, 0, 0, 0, 0};
                        break;
                    case 8:
                        rint = new int[]{0, 50, 14, 6, 6, 3, 8, 2, 0, 0, 0, 0};
                        break;
                    case 9:
                        rint = new int[]{0, 100, 16, 8, 8, 5, 10, 5, 0, 0, 10, 0};
                        break;
                }
                break;
            case 1:// 1:熱情(戒指)
                switch (level) {
                    case 0:
                        break;
                    case 1:// { MP,HP,藥水回復量%, 近距離傷害, 遠距離傷害, 傷害減免, 魔法命中 , PVP傷害, 防禦, 魔攻,
                        // 魔防, PVP傷害值減少 }
                        rint = new int[]{0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                        break;
                    case 2:// { MP,HP,藥水回復量%, 近距離傷害, 遠距離傷害, 傷害減免, 魔法命中 , PVP傷害, 防禦 , 魔攻,
                        // 魔防, PVP傷害值減少 }
                        rint = new int[]{0, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                        break;
                    case 3:
                        rint = new int[]{0, 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                        break;
                    case 4:
                        rint = new int[]{0, 30, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0};
                        break;
                    case 5:// { MP,HP,藥水回復量%, 近距離傷害, 遠距離傷害, 傷害減免, 魔法命中 , PVP傷害, 防禦 , 魔攻,
                        // 魔防, PVP傷害值減少}
                        rint = new int[]{0, 40, 0, 3, 3, 3, 0, 0, -1, 0, 0, 0};
                        break;
                    case 6:
                        rint = new int[]{0, 40, 0, 4, 4, 3, 0, 0, -3, 1, 0, 0};
                        break;
                    case 7:// { MP,HP,藥水回復量%, 近距離傷害, 遠距離傷害, 傷害減免, 魔法命中 , PVP傷害, 防禦 , 魔攻,
                        // 魔防, PVP傷害值減少 }
                        rint = new int[]{0, 50, 0, 4, 4, 4, 0, 1, -4, 2, 0, 0};
                        break;
                    case 8:
                        rint = new int[]{0, 50, 0, 5, 5, 5, 0, 2, -5, 4, 0, 0};
                        break;
                    case 9:
                        rint = new int[]{0, 100, 0, 7, 7, 7, 0, 5, -7, 5, 0, 7};
                        break;
                }
                break;
            case 2:// 2:意志(皮帶)
                switch (level) {
                    case 0:
                        break;
                    case 1:// { MP,HP,藥水回復量%, 近距離傷害, 遠距離傷害, 傷害減免, 魔法命中 , PVP傷害, 防禦 , 魔攻,
                        // 魔防, PVP傷害值減少}
                        rint = new int[]{5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                        break;
                    case 2:// { MP,HP,藥水回復量%, 近距離傷害, 遠距離傷害, 傷害減免 , 魔法命中 , PVP傷害, 防禦 ,
                        // 魔攻, 魔防, PVP傷害值減少}
                        rint = new int[]{10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                        break;
                    case 3:
                        rint = new int[]{20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                        break;
                    case 4:
                        rint = new int[]{30, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0};
                        break;
                    case 5:
                        rint = new int[]{40, 0, 0, 0, 0, 2, 0, 0, 0, 0, 3, 0};
                        break;
                    case 6:
                        rint = new int[]{40, 0, 0, 0, 0, 4, 0, 0, -1, 0, 5, 0};
                        break;
                    case 7:// { MP,HP,藥水回復量%, 近距離傷害, 遠距離傷害, 傷害減免 , 魔法命中 , PVP傷害, 防禦 ,
                        // 魔攻, 魔防, PVP傷害值減少}
                        rint = new int[]{50, 0, 0, 0, 0, 4, 0, 0, -2, 0, 7, 1};
                        break;
                    case 8:
                        rint = new int[]{50, 0, 0, 0, 0, 5, 0, 0, -3, 0, 10, 2};
                        break;
                    case 9:
                        rint = new int[]{75, 0, 0, 0, 0, 7, 0, 0, -5, 0, 15, 5};
                        break;
                }
                break;
        }
        return rint;
    }

    public String getQuality1() {
        return ArmorSetTable.get().getQuality1(_itemInstance.getItemId());
    }

    public String getQuality2() {
        return ArmorSetTable.get().getQuality2(_itemInstance.getItemId());
    }

    public String getQuality3() {
        return ArmorSetTable.get().getQuality3(_itemInstance.getItemId());
    }

    public String getQuality4() {
        return ArmorSetTable.get().getQuality4(_itemInstance.getItemId());
    }

    public String getQuality5() {
        return ArmorSetTable.get().getQuality5(_itemInstance.getItemId());
    }

    public String getQuality6() {
        return ArmorSetTable.get().getQuality6(_itemInstance.getItemId());
    }

    public String getQuality7() {
        return ArmorSetTable.get().getQuality7(_itemInstance.getItemId());
    }

    public String getQuality8() {
        return ArmorSetTable.get().getQuality8(_itemInstance.getItemId());
    }

    public String getQuality9() {
        return ArmorSetTable.get().getQuality9(_itemInstance.getItemId());
    }

    public boolean isMatch() { // src008
        return _itemInstance.isMatch();
    }

    private String set_hole_name(int hole) {  //src039
        final L1StonePower l1StonePower = StonePowerTable.getInstance().get(hole);
        final StringBuilder stringBuilder = new StringBuilder();
        if (l1StonePower != null) {
            stringBuilder.append(l1StonePower.get_Note()).append(" ");
        } else {
            stringBuilder.append("○");
        }
        return stringBuilder.toString();
    }

    private String set_rune_name(int rune) {
        final L1SuperRune l1SuperRune = SuperRuneTable.getInstance().get(rune);
        final StringBuilder stringBuilder = new StringBuilder();
        if (l1SuperRune != null) {
            stringBuilder.append(l1SuperRune.get_Note());
            final int ac = l1SuperRune.getAc();
            if (ac != 0) {
                stringBuilder.append(ac);
            }
            final int hp = l1SuperRune.getHp();
            if (hp != 0) {
                stringBuilder.append(hp);
            }
            final int mp = l1SuperRune.getMp();
            if (mp != 0) {
                stringBuilder.append(mp);
            }
            final int hpr = l1SuperRune.getHpr();
            if (hpr != 0) {
                stringBuilder.append(hpr);
            }
            final int mpr = l1SuperRune.getMpr();
            if (mpr != 0) {
                stringBuilder.append(mpr);
            }
            final int str = l1SuperRune.getStr();
            if (str != 0) {
                stringBuilder.append(str);
            }
            final int con = l1SuperRune.getCon();
            if (con != 0) {
                stringBuilder.append(con);
            }
            final int dex = l1SuperRune.getDex();
            if (dex != 0) {
                stringBuilder.append(dex);
            }
            final int wis = l1SuperRune.getWis();
            if (wis != 0) {
                stringBuilder.append(wis);
            }
            final int cha = l1SuperRune.getCha();
            if (cha != 0) {
                stringBuilder.append(cha);
            }
            final int inter = l1SuperRune.getInt();
            if (inter != 0) {
                stringBuilder.append(inter);
            }
            final int sp = l1SuperRune.getSp();
            if (sp != 0) {
                stringBuilder.append(sp);
            }
            final int mr = l1SuperRune.getMr();
            if (mr != 0) {
                stringBuilder.append(mr);
            }
            final int hitModifer = l1SuperRune.getHitModifier();
            if (hitModifer != 0) {
                stringBuilder.append(hitModifer);
            }
            final int dmgModifer = l1SuperRune.getDmgModifier();
            if (dmgModifer != 0) {
                stringBuilder.append(dmgModifer);
            }
            final int bowHit = l1SuperRune.getBowHitModifier();
            if (bowHit != 0) {
                stringBuilder.append(bowHit);
            }
            final int bowDmg = l1SuperRune.getBowDmgModifier();
            if (bowDmg != 0) {
                stringBuilder.append(bowDmg);
            }
            final int magicDmg = l1SuperRune.getMagicDmgModifier();
            if (magicDmg != 0) {
                stringBuilder.append(magicDmg);
            }
            final int magicReduction = l1SuperRune.getMagicDmgReduction();
            if (magicReduction != 0) {
                stringBuilder.append(magicReduction);
            }
            final int reductionDmg = l1SuperRune.getReductionDmg();
            if (reductionDmg != 0) {
                stringBuilder.append(reductionDmg);
            }
            final int registStun = l1SuperRune.getRegistStun();
            if (registStun != 0) {
                stringBuilder.append(registStun);
            }
            final int registSustain = l1SuperRune.getRegistSustain();
            if (registSustain != 0) {
                stringBuilder.append(registSustain);
            }
            final int registStone = l1SuperRune.getRegistStone();
            if (registStone != 0) {
                stringBuilder.append(registStone);
            }
            final int registSleep = l1SuperRune.getRegistSleep();
            if (registSleep != 0) {
                stringBuilder.append(registSleep);
            }
            final int registFreeze = l1SuperRune.getRegistFreeze();
            if (registFreeze != 0) {
                stringBuilder.append(registFreeze);
            }
            final int registBlind = l1SuperRune.getRegistBlind();
            if (registBlind != 0) {
                stringBuilder.append(registBlind);
            }
        } else {
            stringBuilder.append("未附魔");
        }
        return stringBuilder.toString();
    }

    private String set_rune_name2(int rune) {
        final StringBuilder stringBuilder = new StringBuilder();
        if (rune == 0) {
            stringBuilder.append("未附魔");
        } else if (rune == 1) {
            stringBuilder.append("15%近戰還擊80傷害");
        } else if (rune == 2) {
            stringBuilder.append("15%遠攻還擊80傷害");
        }
        return stringBuilder.toString();
    }

    private void checkArmorSet() { //昇華系統顯示
        if ((ArmorSetTable.get().checkArmorSet(_itemInstance.getItemId()))
                && (_item.get_mode()[0] != 0 // 套裝效果:力量
                || _item.get_mode()[1] != 0 // 套裝效果:敏捷
                || _item.get_mode()[2] != 0 // 套裝效果:體質
                || _item.get_mode()[3] != 0 // 套裝效果:精神
                || _item.get_mode()[4] != 0 // 套裝效果:智力
                || _item.get_mode()[5] != 0 // 套裝效果:魅力
                || _item.get_mode()[6] != 0 // 套裝效果:HP
                || _item.get_mode()[7] != 0 // 套裝效果:MP
                || _item.get_mode()[8] != 0 // 套裝效果:魔防
                || _item.get_mode()[9] != 0 // 套裝效果:魔攻
                || _item.get_mode()[10] != 0 // 套裝效果:加速效果
                || _item.get_mode()[11] != 0 // 套裝效果:火屬性
                || _item.get_mode()[12] != 0 // 套裝效果:水屬性
                || _item.get_mode()[13] != 0 // 套裝效果:風屬性
                || _item.get_mode()[14] != 0 // 套裝效果:地屬性
                || _item.get_mode()[15] != 0 // 套裝效果:寒冰耐性
                || _item.get_mode()[16] != 0 // 套裝效果:石化耐性
                || _item.get_mode()[17] != 0 // 套裝效果:睡眠耐性
                || _item.get_mode()[18] != 0 // 套裝效果:暗闇耐性
                || _item.get_mode()[19] != 0 // 套裝效果:暈眩耐性
                || _item.get_mode()[20] != 0 // 套裝效果:支撐耐性
                || _item.get_mode()[21] != 0 // 套裝效果:近距離傷害
                || _item.get_mode()[22] != 0 // 套裝效果:遠距離傷害
                || _item.get_mode()[23] != 0 // 套裝效果:魔法傷害
                || _item.get_mode()[24] != 0 // 套裝效果:減免物理傷害
                || _item.get_mode()[25] != 0 // 套裝效果:減免魔法傷害
                || _item.get_mode()[26] != 0 // 套裝效果:增加近距離物理攻擊傷害反彈
                || _item.get_mode()[27] != 0 // 套裝效果:增加遠距離物理攻擊傷害反彈
                || _item.get_mode()[28] != 0 // 套裝效果:增加經驗 1=+1%
                || _item.get_mode()[29] != 0 // 套裝效果:增加防禦
                || _item.get_mode()[30] > 0  // 套裝效果:套裝變身
        )) {
            // 0x45=69 [額外組合] // 0:結尾 1:橘色 2:灰色
            _os.writeC(69);
            if (isMatch() && _itemInstance.isEquipped()) { // 完成套裝並且是使用中的就顯示1:橘色
                _os.writeC(1); // 1:橘色
            } else {
                _os.writeC(2); // 2:灰色
            }

            if (_item.get_mode()[30] > 0 // 變身
                    && _item.get_mode()[31] > 0) { // 變身名字
                _os.writeC(71);
                _os.writeH(_item.get_mode()[31]);

            } else if (_item.get_mode()[30] > 0 // 變身
                    && _item.get_mode()[31] <= 0) { // 變身名字
                _os.writeC(0x27);
                _os.writeS("變身效果");
            }

            if (_item.get_mode()[29] != 0) { // 防禦
                _os.writeC(56); // 56額外防禦
                _os.writeC(-_item.get_mode()[29]); // 多個 - 號
            }
            if (_item.get_mode()[0] != 0) { // 力量
                _os.writeC(0x08);
                _os.writeC(_item.get_mode()[0]);
            }
            if (_item.get_mode()[1] != 0) { // 敏捷
                _os.writeC(0x09);
                _os.writeC(_item.get_mode()[1]);
            }
            if (_item.get_mode()[2] != 0) { // 體質
                _os.writeC(0x0a);
                _os.writeC(_item.get_mode()[2]);
            }
            if (_item.get_mode()[3] != 0) { // 精神
                _os.writeC(0x0b);
                _os.writeC(_item.get_mode()[3]);
            }
            if (_item.get_mode()[4] != 0) { // 智力
                _os.writeC(0x0c);
                _os.writeC(_item.get_mode()[4]);
            }
            if (_item.get_mode()[5] != 0) { // 魅力
                _os.writeC(0x0d);
                _os.writeC(_item.get_mode()[5]);
            }
            if (_item.get_mode()[6] != 0) { // 血量上限
                _os.writeC(0x0e);
                _os.writeH(_item.get_mode()[6]);
            }
            if (_item.get_mode()[7] != 0) { // 魔量上限
                if (_item.get_mode()[7] <= 120) {
                    _os.writeC(32);
                    _os.writeC(_item.get_mode()[7]);
                } else {
                    _os.writeC(39);
                    _os.writeS("魔力上限 +" + _item.get_mode()[7]);
                }
            }
            if (_item.get_mode()[8] != 0) { // 魔防
                _os.writeC(0x0f);
                _os.writeH(_item.get_mode()[8]);
            }
            if (_item.get_mode()[9] != 0) { // 魔攻
                _os.writeC(0x11);
                _os.writeC(_item.get_mode()[9]);
            }
            if (_item.get_mode()[10] != 0) { // 加速效果
                _os.writeC(0x12);
            }
            if (_item.get_mode()[11] != 0) { // 火屬性
                _os.writeC(0x1b);
                _os.writeC(_item.get_mode()[11]);
            }
            if (_item.get_mode()[12] != 0) { // 水屬性
                _os.writeC(0x1c);
                _os.writeC(_item.get_mode()[12]);
            }
            if (_item.get_mode()[13] != 0) { // 風屬性
                _os.writeC(0x1d);
                _os.writeC(_item.get_mode()[13]);
            }
            if (_item.get_mode()[14] != 0) { // 地屬性
                _os.writeC(0x1e);
                _os.writeC(_item.get_mode()[14]);
            }
            if (_item.get_mode()[15] != 0) { // 寒冰耐性
                _os.writeC(33);
                _os.writeC(1);
                _os.writeC(_item.get_mode()[15]);
            }
            if (_item.get_mode()[16] != 0) { // 石化耐性
                _os.writeC(33);
                _os.writeC(2);
                _os.writeC(_item.get_mode()[16]);
            }
            if (_item.get_mode()[17] != 0) { // 睡眠耐性
                _os.writeC(33);
                _os.writeC(3);
                _os.writeC(_item.get_mode()[17]);
            }
            if (_item.get_mode()[18] != 0) { // 暗黑耐性
                _os.writeC(33);
                _os.writeC(4);
                _os.writeC(_item.get_mode()[18]);
            }
            if (_item.get_mode()[19] != 0) { // 昏迷耐性
                _os.writeC(33);
                _os.writeC(5);
                _os.writeC(_item.get_mode()[19]);
            }
            if (_item.get_mode()[20] != 0) { // 支撐耐性
                _os.writeC(33);
                _os.writeC(6);
                _os.writeC(_item.get_mode()[20]);
            }
            if (_item.get_mode()[21] != 0) { // 近距離傷害
                _os.writeC(47);
                _os.writeC(_item.get_mode()[21]);
            }
            if (_item.get_mode()[22] != 0) { // 遠距離傷害
                _os.writeC(35);
                _os.writeC(_item.get_mode()[22]);
            }
            if (_item.get_mode()[23] != 0) { // 增加魔法傷害
                _os.writeC(39);
                _os.writeS("魔法傷害 +" + _item.get_mode()[23]);
            }
            if (_item.get_mode()[24] != 0) { // 減免物理傷害
                _os.writeC(39);
                _os.writeS("物理減傷 +" + _item.get_mode()[24]);
            }
            if (_item.get_mode()[25] != 0) { // 減免魔法傷害
                _os.writeC(39);
                _os.writeS("魔法減傷 +" + _item.get_mode()[25]);
            }
            if (_item.get_mode()[26] != 0) { // 增加近距離物理攻擊傷害反彈
                _os.writeC(39);
                _os.writeS("機率反彈近戰" + _item.get_mode()[26] + "點傷害");
            }
            if (_item.get_mode()[27] != 0) { // 增加遠距離物理攻擊傷害反彈
                _os.writeC(39);
                _os.writeS("機率反彈遠程" + _item.get_mode()[27] + "點傷害");
            }
            if (_item.get_mode()[28] != 0) { // 增加經驗 1=+1%
                if (_item.get_mode()[28] <= 120) {
                    _os.writeC(36);
                    _os.writeC(_item.get_mode()[28]);
                } else {
                    _os.writeC(39);
                    _os.writeS("$6134 " + _item.get_mode()[28] + "%");
                }
            }
            // 0x45=69 [額外組合] // 0:結尾 1:橘色 2:灰色
            _os.writeC(69);
            _os.writeC(0); // 0:結尾
        }
    }
}
