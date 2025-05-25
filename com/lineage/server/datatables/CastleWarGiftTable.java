package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.lock.CharItemsReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Clan;
import com.lineage.server.serverpackets.S_InventoryIcon;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.timecontroller.pc.VIPGfxTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 攻城獎勵設置
 *
 * @author dexc
 */
public class CastleWarGiftTable {
    public static final Map<Integer, ArrayList<Gift>> _gifts = new HashMap<>();
    private static final Log _log = LogFactory.getLog(CastleWarGiftTable.class);
    private static CastleWarGiftTable _instance;

    public static CastleWarGiftTable get() {
        if (_instance == null) {
            _instance = new CastleWarGiftTable();
        }
        return _instance;
    }

    /**
     * 初始化載入
     */
    public void load() {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `系統_城堡狀態設置`");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int key = rs.getInt("castle_id");
                final int itemid = rs.getInt("itemid");
                final int count = rs.getInt("count");
                final boolean recover = rs.getBoolean("recover");
                final int buff_iconid = rs.getInt("偵測圖檔tbl編號");
                final int buff_stringid = rs.getInt("偵測圖檔string編號");
                final int open_string = rs.getInt("登入是否顯示");
                final boolean buff_save = rs.getBoolean("是否登出保存");
                final Gift e = new Gift();
                e._itemid = itemid;
                e._count = count;
                e._recover = recover;
                //狀態圖示
                e._buff_iconid = buff_iconid;
                e._buff_stringid = buff_stringid;
                e._open_string = open_string;
                e._buff_save = buff_save;
                // 城堡額外附加能力 by terry0412
                e._ac = rs.getInt("ac");
                e._hp = rs.getInt("hp");
                e._mp = rs.getInt("mp");
                e._hpr = rs.getInt("hpr");
                e._mpr = rs.getInt("mpr");
                e._str = rs.getInt("str");
                e._con = rs.getInt("con");
                e._dex = rs.getInt("dex");
                e._wis = rs.getInt("wis");
                e._cha = rs.getInt("cha");
                e._int = rs.getInt("intel");
                e._sp = rs.getInt("sp");
                e._mr = rs.getInt("mr");
                e._hit_modifier = rs.getInt("hit_modifier");
                e._dmg_modifier = rs.getInt("dmg_modifier");
                e._bow_hit_modifier = rs.getInt("bow_hit_modifier");
                e._bow_dmg_modifier = rs.getInt("bow_dmg_modifier");
                e._defense_water = rs.getInt("defense_water");
                e._defense_wind = rs.getInt("defense_wind");
                e._defense_fire = rs.getInt("defense_fire");
                e._defense_earth = rs.getInt("defense_earth");
                e._regist_stun = rs.getInt("regist_stun");
                e._regist_stone = rs.getInt("regist_stone");
                e._regist_sleep = rs.getInt("regist_sleep");
                e._regist_freeze = rs.getInt("regist_freeze");
                e._regist_sustain = rs.getInt("regist_sustain");
                e._regist_blind = rs.getInt("regist_blind");
                e._gfx = rs.getInt("gfx");//特效
                e._time = rs.getInt("time");
                ArrayList<Gift> list = _gifts.get(key);
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(e);
                _gifts.put(key, list);
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    /**
     * 是否具有攻城獎勵設置
     *
     */
    public boolean isGift(final int key) {
        final ArrayList<Gift> list = _gifts.get(key);
        if (list == null) {
            return false;
        }
        return true;
    }

    /**
     * 給予攻城獎勵
     *
     */
    public void get_gift(final int key) {
        L1Clan castle_clan = null;
        final ArrayList<Gift> list = _gifts.get(key);
        if (list == null) {
            return;
        }
        try {
            castle_clan = L1CastleLocation.castleClan(key);
            /*
             * final Collection<L1Clan> allClan = WorldClan.get().getAllClans();
             * // 不包含元素 if (allClan.isEmpty()) { return; }
             *
             * for (final Iterator<L1Clan> iter = allClan.iterator();
             * iter.hasNext();) { final L1Clan clan = iter.next(); if
             * (clan.getCastleId() == key) { castle_clan = clan; // 設置血盟 } }
             */
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            if (castle_clan != null) {
                for (final Gift gift : list) {
                    if (gift._recover) { // 該物品設置回收
                        recover_item(gift);
                    }
                    get_gift(castle_clan, gift);
                }
            }
        }
    }

    /**
     * 給予物品
     *
     */
    private void get_gift(final L1Clan castle_clan, final Gift gift) {
        try {
            if (castle_clan.getOnlineClanMemberSize() > 0) {
                // 取回線上成員
                for (L1PcInstance tgpc : castle_clan.getOnlineClanMember()) {
                    final L1ItemInstance item = ItemTable.get().createItem(gift._itemid);
                    if (item != null) {
                        if (L1CastleLocation.checkInAllWarArea(tgpc.getX(), tgpc.getY(), tgpc.getMapId())) {
                            item.setCount(gift._count);
                            // 加入背包
                            tgpc.getInventory().storeItem(item);
                            // 送出訊息
                            tgpc.sendPackets(new S_ServerMessage("獲得攻城獎勵: " + item.getLogName()));
                            String[] castleName = {"", "肯特城", "妖魔城", "風木城", "奇岩城", "海音城", "侏儒城", "亞丁城", "狄亞得要塞"};
                            RecordTable.get().recordCastleReward(castleName[castle_clan.getCastleId()], tgpc.getName(), item
                                    .getLogName(), gift._count, item.getId(), tgpc.getIp());
                        }
                    }
                    // 給予額外附加能力效果 by terry0412
                    if (!tgpc.isCastleAbility(gift.hashCode())) {
                        this.effectBuff(tgpc, gift, 1);
                        tgpc.addCastleAbility(gift.hashCode());
                    }
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 回收其他人物該物品
     *
     */
    private void recover_item(final Gift gift) {
        try {
            // 全部線上人物
            final Collection<L1PcInstance> allpc = World.get().getAllPlayers();
            for (final L1PcInstance tgpc : allpc) {
                final L1ItemInstance t1 = tgpc.getInventory().findItemId(gift._itemid);
                if (t1 != null) {
                    if (t1.isEquipped()) {
                        tgpc.getInventory().setEquipped(t1, false, false, false);
                    }
                    tgpc.getInventory().removeItem(t1);
                    // \f1%0%s 消失。
                    tgpc.sendPackets(new S_ServerMessage(158, t1.getLogName()));
                }
                // 移除額外附加能力效果 by terry0412
                if (tgpc.isCastleAbility(gift.hashCode())) {
                    this.effectBuff(tgpc, gift, -1);
                    tgpc.removeCastleAbility(gift.hashCode());
                }
            }
            // 刪除該物品全部數據
            CharItemsReading.get().del_item(gift._itemid);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void effectBuff(final L1PcInstance pc, final Gift value, final int negative) {
        pc.addAc(value._ac * negative);
        pc.addMaxHp(value._hp * negative);
        pc.addMaxMp(value._mp * negative);
        pc.addHpr(value._hpr * negative);
        pc.addMpr(value._mpr * negative);
        pc.addStr(value._str * negative);
        pc.addCon(value._con * negative);
        pc.addDex(value._dex * negative);
        pc.addWis(value._wis * negative);
        pc.addCha(value._cha * negative);
        pc.addInt(value._int * negative);
        pc.addSp(value._sp * negative);
        pc.addMr(value._mr * negative);
        pc.addHitup(value._hit_modifier * negative);
        pc.addDmgup(value._dmg_modifier * negative);
        pc.addBowHitup(value._bow_hit_modifier * negative);
        pc.addBowDmgup(value._bow_dmg_modifier * negative);
        pc.addWater(value._defense_water * negative);
        pc.addWind(value._defense_wind * negative);
        pc.addFire(value._defense_fire * negative);
        pc.addEarth(value._defense_earth * negative);
        pc.addRegistStun(value._regist_stun * negative);
        pc.addRegistStone(value._regist_stone * negative);
        pc.addRegistSleep(value._regist_sleep * negative);
        pc.add_regist_freeze(value._regist_freeze * negative);
        pc.addRegistSustain(value._regist_sustain * negative);
        pc.addRegistBlind(value._regist_blind * negative);
        pc.set_gfx(value._gfx * negative);//特效
        pc.set_time(value._time * negative);
        if (value.get_buff_iconid() != 0 && value.get_buff_stringid() != 0) { // 自訂狀態圖示和訊息
            if (value.get_open_string() == 0) { // 有設狀態圖示和訊息的使用和登入是否顯示圖示裡面的訊息內容
                pc.sendPackets(new S_InventoryIcon(value.get_buff_iconid(), true, value.get_buff_stringid(), pc.getSkillEffectTimeSec(negative))); // 重登不會有訊息
            } else {
                pc.sendPackets(new S_InventoryIcon(value.get_buff_iconid(), true, value.get_buff_stringid(), value.get_buff_stringid(), pc.getSkillEffectTimeSec(negative))); // 重登有訊息
            }
        }
        final int add_gif = pc.get_gfx();
        final int add_gif_time = pc.get_time();
        if (add_gif != 0 && add_gif_time != 0) {
            pc.set_gfx(add_gif);
            pc.set_time(add_gif_time);
            VIPGfxTimer.addMember(pc);
        }
    }

    /**
     * 登入遊戲給予附加能力 by terry0412
     *
     */
    public final void login_gift(final L1PcInstance tgpc) {
        try {
            final L1Clan clan = tgpc.getClan();
            if (clan != null && clan.getCastleId() > 0) {
                final ArrayList<Gift> list = _gifts.get(clan.getCastleId());
                if (list == null) {
                    return;
                }
                for (final Gift gift : list) {
                    // 給予額外附加能力效果 by terry0412
                    if (!tgpc.isCastleAbility(gift.hashCode())) {
                        this.effectBuff(tgpc, gift, 1);
                        tgpc.addCastleAbility(gift.hashCode());
                    }
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private static class Gift {
        private int _itemid;
        private int _count;
        private boolean _recover;
        private int _buff_iconid;
        private int _buff_stringid;
        private int _open_string;
        private boolean _buff_save;
        private int _ac; // 防禦
        private int _hp; // 血量
        private int _mp; // 魔量
        private int _hpr; // 回血量
        private int _mpr; // 回魔量
        private int _str; // 力量
        private int _con; // 體質
        private int _dex; // 敏捷
        private int _wis; // 精神
        private int _cha; // 魅力
        private int _int; // 智力
        private int _sp; // 魔攻
        private int _mr; // 魔防
        private int _hit_modifier; // 近戰攻擊命中
        private int _dmg_modifier; // 近戰攻擊傷害
        private int _bow_hit_modifier; // 遠距攻擊命中
        private int _bow_dmg_modifier; // 遠距攻擊傷害
        private int _defense_water; // 水屬性防禦
        private int _defense_wind; // 風屬性防禦
        private int _defense_fire; // 火屬性防禦
        private int _defense_earth; // 地屬性防禦
        private int _regist_stun; // 昏迷耐性
        private int _regist_stone; // 石化耐性
        private int _regist_sleep; // 睡眠耐性
        private int _regist_freeze; // 寒冰耐性
        private int _regist_sustain; // 支撐耐性
        private int _regist_blind; // 闇黑耐性
        private int _gfx;//特效
        private int _time;//時間軸

        public int get_buff_iconid() {
            return this._buff_iconid;
        }

        public void set_buff_iconid(final int i) {
            this._buff_iconid = i;
        }

        public int get_buff_stringid() {
            return this._buff_stringid;
        }

        public void set_buff_stringid(final int i) {
            this._buff_stringid = i;
        }

        public int get_open_string() {
            return this._open_string;
        }

        public void set_open_string(final int i) {
            this._open_string = i;
        }

        public boolean is_buff_save() {
            return this._buff_save;
        }

        public void set_buff_save(final boolean i) {
            this._buff_save = i;
        }
    }
}
