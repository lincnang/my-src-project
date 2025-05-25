package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.doll.L1DollExecutor;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.serverpackets.doll.S_DollCompoundInit;
import com.lineage.server.serverpackets.doll.S_DollCompoundItem;
import com.lineage.server.serverpackets.doll.S_DollCompoundMaterial;
import com.lineage.server.serverpackets.doll.S_DollCompoundRoll;
import com.lineage.server.templates.L1Doll;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DollPowerTable {
    public static final CopyOnWriteArrayList<ServerBasePacket> DOLL_PACKET_CACHE = new CopyOnWriteArrayList<>();// 魔法娃娃合成數據封包緩存
    private static final Log _log = LogFactory.getLog(DollPowerTable.class);
    private static final HashMap<Integer, L1Doll> _powerMap = new HashMap<>();
    private static final HashMap<Integer, L1DollExecutor> _classList = new HashMap<>();
    private static final ArrayList<String> _checkList = new ArrayList<>();
    private static DollPowerTable _instance;
    private static MessageDigest _alg;// sha-1校驗實例
    private static byte[] _digest;// sha-1校驗碼
    private static boolean _begin;// 輸出緩存是否起始輸出
    private final TreeMap<Integer, ArrayList<L1Item>> _levelList = new TreeMap<>();

    public static DollPowerTable get() {
        if (_instance == null) {
            _instance = new DollPowerTable();
        }
        return _instance;
    }

    /**
     * 比較道具製作數據的緩存校驗
     *
     */
    public static boolean isEqual(final byte[] check) {
        if (_digest == null) {
            return false;
        }
        return MessageDigest.isEqual(_digest, check);
    }

    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `etcitem_doll_power`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String classname = rs.getString("classname");
                int type1 = rs.getInt("type1");
                int type2 = rs.getInt("type2");
                int type3 = rs.getInt("type3");
                String note = rs.getString("note");
                // 新增娃娃能力描述↓↓↓
                final int doll_ac = rs.getInt("ac");
                final int doll_str = rs.getInt("str");
                final int doll_dex = rs.getInt("dex");
                final int doll_con = rs.getInt("con");
                final int doll_intel = rs.getInt("int");
                final int doll_wis = rs.getInt("wis");
                final int doll_cha = rs.getInt("cha");
                final int doll_hp = rs.getInt("hp");
                final int doll_mp = rs.getInt("mp");
                final int doll_hpr = rs.getInt("hpr");
                final int doll_mpr = rs.getInt("mpr");
                final int doll_mr = rs.getInt("mr");
                final int doll_sp = rs.getInt("sp");
                final int doll_dmg = rs.getInt("dmg");
                final int doll_hit = rs.getInt("hit");
                final int doll_bowdmg = rs.getInt("bowdmg");
                final int doll_bowhit = rs.getInt("bowhit");
                final int doll_alldmg_r = rs.getInt("alldmg_r");
                final int doll_exp = rs.getInt("exp");
                final int doll_weight = rs.getInt("weight");
                final int doll_weight_r = rs.getInt("weight_r");
                final int doll_earth = rs.getInt("earth");
                final int doll_wind = rs.getInt("wind");
                final int doll_water = rs.getInt("water");
                final int doll_fire = rs.getInt("fire");
                final int doll_stun = rs.getInt("stun");
                final int doll_stone = rs.getInt("stone");
                final int doll_sleep = rs.getInt("sleep");
                final int doll_freeze = rs.getInt("freeze");
                final int doll_sustain = rs.getInt("sustain");
                final int doll_blind = rs.getInt("blind");
                final int doll_haste = rs.getInt("haste");
                final int doll_stunlv = rs.getInt("stunlv");
                final int doll_breaklv = rs.getInt("breaklv");
                final int doll_foeslayer = rs.getInt("foeslayer");
                final int doll_titanhp = rs.getInt("titanhp");
                // 新增娃娃能力描述↑↑↑
                String ch = classname + "=" + type1 + "=" + type2 + "=" + type3 + "=" + note;
                if (_checkList.lastIndexOf(ch) == -1) {
                    _checkList.add(ch);
                    //addList(id, classname, type1, type2, type3, note);
                    // 新增娃娃能力描述↓↓↓
                    addList(id, classname, type1, type2, type3, note, doll_ac, doll_str, doll_dex, doll_con, doll_intel, doll_wis, doll_cha, doll_hp, doll_mp, doll_hpr, doll_mpr, doll_mr, doll_sp, doll_dmg, doll_hit, doll_bowdmg, doll_bowhit, doll_alldmg_r, doll_exp, doll_weight, doll_weight_r, doll_earth, doll_wind, doll_water, doll_fire, doll_stun, doll_stone, doll_sleep, doll_freeze, doll_sustain, doll_blind, doll_haste, doll_stunlv, doll_breaklv, doll_foeslayer, doll_titanhp);
                    // 新增娃娃能力描述↑↑↑
                } else {
                    _log.error("娃娃能力設置重複:id=" + id + " type1=" + type1 + " type2=" + type2 + " type3=" + type3);
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
            _checkList.clear();
        }
        _log.info("讀取->娃娃能力資料數量: " + _classList.size() + "(" + timer.get() + "ms)");
        setDollType();
    }

    private void setDollType() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `etcitem_doll_type`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int itemid = rs.getInt("itemid");
                String powers = rs.getString("powers").replaceAll(" ", "");
                String need = rs.getString("need").replaceAll(" ", "");
                String count = rs.getString("count").replaceAll(" ", "");
                int time = rs.getInt("time");
                int gfxid = rs.getInt("gfxid");
                String nameid = rs.getString("nameid");
                final int level = rs.getInt("level"); // XXX 7.6 魔法娃娃合成等級
                boolean iserr = false;
                ArrayList<L1DollExecutor> powerList = new ArrayList<>();
                if ((powers != null) && (!powers.equals(""))) {
                    String[] set1 = powers.split(",");
                    for (String string : set1) {
                        L1DollExecutor e = (L1DollExecutor) _classList.get(Integer.parseInt(string));
                        if (e != null) {
                            powerList.add(e);
                        } else {
                            _log.error("娃娃能力取回錯誤-沒有這個編號:" + string);
                            iserr = true;
                        }
                    }
                }
                int[] needs = (int[]) null;
                if ((need != null) && (!need.equals(""))) {
                    String[] set2 = need.split(",");
                    needs = new int[set2.length];
                    for (int i = 0; i < set2.length; i++) {
                        int itemid_n = Integer.parseInt(set2[i]);
                        L1Item temp = ItemTable.get().getTemplate(itemid_n);
                        if (temp == null) {
                            _log.error("物品資訊取回錯誤-沒有這個編號:" + itemid_n);
                            iserr = true;
                        }
                        needs[i] = itemid_n;
                    }
                }
                int[] counts = (int[]) null;
                if ((count != null) && (!count.equals(""))) {
                    String[] set3 = count.split(",");
                    counts = new int[set3.length];
                    if (set3.length != needs.length) {
                        _log.error("物品資訊對應錯誤-長度不吻合: itemid:" + itemid);
                        iserr = true;
                    }
                    for (int i = 0; i < set3.length; i++) {
                        int count_n = Integer.parseInt(set3[i]);
                        counts[i] = count_n;
                    }
                }
                if (!iserr) {
                    L1Doll doll_power = new L1Doll();
                    doll_power.set_itemid(itemid);
                    doll_power.setPowerList(powerList);
                    doll_power.set_need(needs);
                    doll_power.set_counts(counts);
                    doll_power.set_time(time);
                    doll_power.set_gfxid(gfxid);
                    doll_power.set_nameid(nameid);
                    doll_power.set_level(level);
                    // 鎖定1~5級的娃娃 加入魔法娃娃合成系統
                    if (doll_power.get_level() >= 1 && doll_power.get_level() <= 5) {
                        this.addLevelMap(doll_power.get_level(), ItemTable.get().getTemplate(itemid));
                    }
                    _powerMap.put(itemid, doll_power);
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
            if (!_levelList.isEmpty()) {
                this.buildData();
            }
            _classList.clear();
        }
        _log.info("讀取->娃娃能力資料數量: " + _classList.size() + "(" + timer.get() + "ms)");
    }

    /**
     * 加入娃娃等級群組
     *
     */
    private void addLevelMap(final int level, final L1Item tmp) {
        ArrayList<L1Item> levelList = _levelList.get(level);
        if (levelList == null) {
            levelList = new ArrayList<>();
            levelList.add(tmp);
            _levelList.put(level, levelList);
        } else {
            levelList.add(tmp);
        }
    }

    private void buildData() {
        try {
            // 初始化sha1校驗實例
            _alg = MessageDigest.getInstance("SHA1");
            _alg.update(new byte[]{0x08, 0x02});// 校驗開頭
            this.buildMaterialData();
            this.buildDollItemData();
            this.buildRouletteData();
            // 生成效驗碼
            _digest = _alg.digest();
            // 緩存輸出 寫入完成
            DOLL_PACKET_CACHE.add(new S_DollCompoundInit(0x02));
            //PacketCache.ALCHEMY_PACKET_CACHE.add(new S_AlchemyDesignAck(0x02));
            //System.out.println("魔法娃娃合成緩存sha1雜湊碼:" + CodecUtil.bytesToHex(_digest).toUpperCase().replace(":", " "));
        } catch (final NoSuchAlgorithmException e0) {
            //_log.(Level.SEVERE, e0.getLocalizedMessage(), e0);
        } catch (final Exception e1) {
            //_log.log(Level.SEVERE, e1.getLocalizedMessage(), e1);
        }
    }

    private void buildMaterialData() {
        try {
            for (int level = 1; level <= 4; level++) {
                final ArrayList<L1Item> levelList = _levelList.get(level);
                if (levelList != null) {
                    byte[] data = new S_DollCompoundMaterial(2, level, levelList).getContent();
                    _alg.update(data);
                    // 生成封包緩存
                    if (!_begin) {// 寫入數據緩存 開始
                        _begin = true;
                        DOLL_PACKET_CACHE.add(new S_DollCompoundInit(0, data));
                    } else {// 寫入數據緩存 繼續
                        DOLL_PACKET_CACHE.add(new S_DollCompoundInit(1, data));
                    }
                }
            }
        } catch (final Exception e1) {
            //_log.log(Level.SEVERE, e1.getLocalizedMessage(), e1);
        }
    }

    private void buildDollItemData() {
        try {
            for (int level = 1; level <= 5; level++) {
                final ArrayList<L1Item> levelList = _levelList.get(level);
                if (levelList != null) {
                    byte[] data = new S_DollCompoundItem(3, level, levelList).getContent();
                    _alg.update(data);
                    // 生成封包緩存
                    if (!_begin) {
                        _begin = true;
                        DOLL_PACKET_CACHE.add(new S_DollCompoundInit(0, data));
                    } else {
                        DOLL_PACKET_CACHE.add(new S_DollCompoundInit(1, data));
                    }
                }
            }
        } catch (final Exception e1) {
            //_log.log(Level.SEVERE, e1.getLocalizedMessage(), e1);
        }
    }

    private void buildRouletteData() {
        try {
            for (int level = 1; level <= 4; level++) {
                byte[] data = new S_DollCompoundRoll(4, level).getContent();
                _alg.update(data);
                // 生成封包緩存
                if (!_begin) {
                    _begin = true;
                    DOLL_PACKET_CACHE.add(new S_DollCompoundInit(0, data));
                } else {
                    DOLL_PACKET_CACHE.add(new S_DollCompoundInit(1, data));
                }
            }
        } catch (final Exception e1) {
            //_log.log(Level.SEVERE, e1.getLocalizedMessage(), e1);
        }
    }

    /**
     * 取回魔法娃娃等級群組
     *
     */
    public ArrayList<L1Item> getDollLevelList(final int level) {
        return _levelList.get(level);
    }

    /**
     * 是否有指定等級娃娃群組
     *
     */
    public boolean isExistDollLevelList(final int level) {
        return _levelList.containsKey(level);
    }

    /**
     * 加入CLASS清單
     *
     */
    //private void addList(int powerid, String className, int int1, int int2, int int3, String note) {
    // 新增娃娃能力描述↓↓↓
    private void addList(final int powerid, final String className, final int int1, final int int2, final int int3, final String note, final int doll_ac, final int doll_str, final int doll_dex, final int doll_con, final int doll_intel, final int doll_wis, final int doll_cha, final int doll_hp, final int doll_mp, final int doll_hpr, final int doll_mpr, final int doll_mr, final int doll_sp, final int doll_dmg, final int doll_hit, final int doll_bowdmg, final int doll_bowhit, final int doll_alldmg_r, final int doll_exp, final int doll_weight, final int doll_weight_r, final int doll_earth, final int doll_wind, final int doll_water, final int doll_fire, final int doll_stun, final int doll_stone, final int doll_sleep, final int doll_freeze, final int doll_sustain, final int doll_blind, final int doll_haste, final int doll_stunlv, final int doll_breaklv, final int doll_foeslayer, final int doll_titanhp) {
        // 新增娃娃能力描述↑↑↑
        if (className.equals("0")) {
            return;
        }
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("com.lineage.server.model.doll.");
            stringBuilder.append(className);
            final Class<?> cls = Class.forName(stringBuilder.toString());
            final L1DollExecutor exe = (L1DollExecutor) cls.getMethod("get").invoke(null);
            exe.set_power(int1, int2, int3);
            exe.set_note(note);
            // 新增娃娃能力描述↓↓↓
            exe.setDollAc(doll_ac);
            exe.setDollStr(doll_str);
            exe.setDollDex(doll_dex);
            exe.setDollCon(doll_con);
            exe.setDollInt(doll_intel);
            exe.setDollWis(doll_wis);
            exe.setDollCha(doll_cha);
            exe.setDollHp(doll_hp);
            exe.setDollMp(doll_mp);
            exe.setDollHpr(doll_hpr);
            exe.setDollMpr(doll_mpr);
            exe.setDollMr(doll_mr);
            exe.setDollSp(doll_sp);
            exe.setDollDmg(doll_dmg);
            exe.setDollHit(doll_hit);
            exe.setDollBowDmg(doll_bowdmg);
            exe.setDollBowHit(doll_bowhit);
            exe.setDollAllDmg_R(doll_alldmg_r);
            exe.setDollExp(doll_exp);
            exe.setDollWeight(doll_weight);
            exe.setDollWeight_R(doll_weight_r);
            exe.setDollEarth(doll_earth);
            exe.setDollWind(doll_wind);
            exe.setDollWater(doll_water);
            exe.setDollFire(doll_fire);
            exe.setDollStun(doll_stun);
            exe.setDollStone(doll_stone);
            exe.setDollSleep(doll_sleep);
            exe.setDollFreeze(doll_freeze);
            exe.setDollSustain(doll_sustain);
            exe.setDollBlind(doll_blind);
            exe.setDollHaste(doll_haste);
            exe.setDollStunLv(doll_stunlv);
            exe.setDollBreakLv(doll_breaklv);
            exe.setDollFoeSlayer(doll_foeslayer);
            exe.setDollTiTanHp(doll_titanhp);
            // 新增娃娃能力描述↑↑↑
            _classList.put(powerid, exe);
        } catch (ClassNotFoundException e) {
            String error = "發生[娃娃能力檔案]錯誤, 檢查檔案是否存在:" + className + " 娃娃能力編號:" + powerid;
            _log.error(error);
        } catch (IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException |
                 IllegalAccessException e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public L1Doll get_type(int key) {
        return (L1Doll) _powerMap.get(key);
    }

    public HashMap<Integer, L1Doll> map() {
        return _powerMap;
    }
}
