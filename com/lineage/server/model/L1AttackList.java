package com.lineage.server.model;

import com.lineage.server.datatables.lock.SpawnBossReading;

import java.util.HashMap;

import static com.lineage.server.model.skill.L1SkillId.*;

public class L1AttackList {
    /**
     * 傷害降低
     */
    public static final HashMap<Integer, Integer> SKD3 = new HashMap<>();
    /**
     * 受到下列法術效果 傷害為0
     */
    public static final HashMap<Integer, Integer> SKM0 = new HashMap<>();
    /**
     * 料理命中降低或追加(_weaponType != 20) && (_weaponType != 62)近距離武器
     */
    protected static final HashMap<Integer, Integer> SKU1 = new HashMap<>();
    /**
     * 料理命中降低或追加(_weaponType == 20) && (_weaponType == 62)遠距離武器
     */
    protected static final HashMap<Integer, Integer> SKU2 = new HashMap<>();
    /**
     * NPC需附加技能才可攻擊
     */
    protected static final HashMap<Integer, Integer> SKNPC = new HashMap<>();
    /**
     * NPC指定外型不可攻擊
     */
    protected static final HashMap<Integer, Integer> PLNPC = new HashMap<>();
    /**
     * 料理追加傷害(_weaponType != 20) && (_weaponType != 62)近距離武器
     */
    protected static final HashMap<Integer, Integer> SKD1 = new HashMap<>();
    /**
     * 料理追加傷害(_weaponType == 20) && (_weaponType == 62)遠距離武器
     */
    protected static final HashMap<Integer, Integer> SKD2 = new HashMap<>();
    /**
     * 力量增加命中
     */
    protected static final HashMap<Integer, Integer> STRH = new HashMap<>();
    /**
     * 敏捷增加命中
     */
    protected static final HashMap<Integer, Integer> DEXH = new HashMap<>();
    /**
     * 力量增加傷害
     */
    protected static final HashMap<Integer, Integer> STRD = new HashMap<>();
    /**
     * 敏捷增加傷害
     */
    protected static final HashMap<Integer, Integer> DEXD = new HashMap<>();
    /**
     * NPC抵抗技能(NPCID / 技能編號) 列表中該技能對該NPC施展失敗
     */
    protected static final HashMap<Integer, Integer[]> DNNPC = new HashMap<>();
    /**
     * 安全區域不可使用的技能
     */
    protected static final HashMap<Integer, Boolean> NZONE = new HashMap<>();

    public static void load() { // BOSS抵抗技能
        for (Integer bossid : SpawnBossReading.get().bossIds()) {
            Integer[] ids = {CURSE_PARALYZE, EARTH_BIND, DARK_BLIND, DARKNESS, CURSE_BLIND, SILENCE, DISEASE, WEAPON_BREAK, GUARD_BRAKE, RESIST_FEAR, HORROR_OF_DEATH, CONFUSION, PHANTASM, PANIC};
            DNNPC.putIfAbsent(bossid, ids);
        }
        // 安全區域不可使用的技能
        NZONE.put(27, Boolean.FALSE);
        NZONE.put(29, Boolean.FALSE);
        NZONE.put(33, Boolean.FALSE);
        NZONE.put(39, Boolean.FALSE);
        NZONE.put(40, Boolean.FALSE);
        NZONE.put(47, Boolean.FALSE);
        NZONE.put(56, Boolean.FALSE);
        NZONE.put(44, Boolean.FALSE);
        NZONE.put(71, Boolean.FALSE);
        NZONE.put(DEATH_HEAL, Boolean.FALSE); // 法師新技能 治癒逆行
        NZONE.put(76, Boolean.FALSE);
        NZONE.put(152, Boolean.FALSE);
        NZONE.put(153, Boolean.FALSE);
        NZONE.put(157, Boolean.FALSE);
        NZONE.put(161, Boolean.FALSE);
        NZONE.put(167, Boolean.FALSE);
        NZONE.put(174, Boolean.FALSE);
        NZONE.put(87, Boolean.FALSE);
        NZONE.put(66, Boolean.FALSE);
        NZONE.put(103, Boolean.FALSE);
        NZONE.put(212, Boolean.FALSE);
        NZONE.put(50, Boolean.FALSE);
        NZONE.put(80, Boolean.FALSE);
        NZONE.put(194, Boolean.FALSE);
        NZONE.put(173, Boolean.FALSE);
        NZONE.put(133, Boolean.FALSE);
        NZONE.put(145, Boolean.FALSE);
        NZONE.put(64, Boolean.FALSE);
        NZONE.put(193, Boolean.FALSE);
        NZONE.put(188, Boolean.FALSE);
        NZONE.put(183, Boolean.FALSE);
        // 料理追加傷害(_weaponType != 20) && (_weaponType != 62)近距離武器
        SKD1.put(3016, 1);
        SKD1.put(3024, 1);
        SKD1.put(3016, 1);
        SKD1.put(3016, 1);
        SKD1.put(COOKING_4_0_N, 2);
        // 料理追加傷害(_weaponType == 20) && (_weaponType == 62)遠距離武器
        SKD2.put(3019, 1);
        SKD2.put(3027, 1);
        SKD2.put(3032, 1);
        SKD2.put(3040, 1);
        SKD2.put(COOKING_4_1_N, 2);
        // 傷害降低
        SKD3.put(3008, -5);
        SKD3.put(3009, -5);
        SKD3.put(3010, -5);
        SKD3.put(3011, -5);
        SKD3.put(3012, -5);
        SKD3.put(3013, -5);
        SKD3.put(3014, -5);
        SKD3.put(3024, -5);
        SKD3.put(3025, -5);
        SKD3.put(3026, -5);
        SKD3.put(3027, -5);
        SKD3.put(3028, -5);
        SKD3.put(3029, -5);
        SKD3.put(3030, -5);
        SKD3.put(3040, -5);
        SKD3.put(3041, -5);
        SKD3.put(3042, -5);
        SKD3.put(3043, -5);
        SKD3.put(3044, -5);
        SKD3.put(3045, -5);
        SKD3.put(3046, -5);
        SKD3.put(3015, -5);
        SKD3.put(3031, -5);
        SKD3.put(3047, -5);
        SKD3.put(COOKING_4_0_N, -2);
        SKD3.put(COOKING_4_1_N, -2);
        SKD3.put(COOKING_4_2_N, -2);
        SKD3.put(COOKING_4_3_N, -2);
        SKD3.put(DRAGON_SKIN, -5);
        SKD3.put(PATIENCE, -2);
        SKD3.put(EARTH_BLESS, -2);
        SKD3.put(IMMUNE_TO_HARM, 61);
        SKD3.put(IMMUNE_TO_HARM2, 79);
        // 受到下列法術效果 傷害為0
        //SKM0.put(new Integer(ABSOLUTE_BARRIER), new Integer(0));// 絕對屏障
        //        SKM0.put(ICE_LANCE, 0);// 冰矛圍籬
        SKM0.put(EARTH_BIND, 0);// 大地屏障
        // 料理追加命中(_weaponType != 20) && (_weaponType != 62)近距離武器
        SKU1.put(3016, 1);
        SKU1.put(3024, 1);
        SKU1.put(3034, 2);
        SKU1.put(3042, 2);
        SKU1.put(COOKING_4_0_N, 1);
        // 料理命中追加(_weaponType == 20) && (_weaponType == 62)遠距離武器
        SKU2.put(3019, 1);
        SKU2.put(3027, 1);
        SKU2.put(3032, 1);
        SKU2.put(3040, 1);
        SKU2.put(COOKING_4_1_N, 1);
        // NPC需附加技能可攻擊
        SKNPC.put(45912, 1013);
        SKNPC.put(45913, 1013);
        SKNPC.put(45914, 1013);
        SKNPC.put(45915, 1013);
        SKNPC.put(45916, 1014);
        SKNPC.put(45941, 1015);
        SKNPC.put(45752, 4005);
        SKNPC.put(45753, 4005);
        SKNPC.put(45675, 4006);
        SKNPC.put(81082, 4006);
        SKNPC.put(45625, 4006);
        SKNPC.put(45674, 4006);
        SKNPC.put(45685, 4006);
        SKNPC.put(87000, 4007);
        SKNPC.put(45020, 4008);
        SKNPC.put(99019, 1027);// 巨型骷髏
        // NPC指定外型不可攻擊
        PLNPC.put(46069, 6035);
        PLNPC.put(46070, 6035);
        PLNPC.put(46071, 6035);
        PLNPC.put(46072, 6035);
        PLNPC.put(46073, 6035);
        PLNPC.put(46074, 6035);
        PLNPC.put(46075, 6035);
        PLNPC.put(46076, 6035);
        PLNPC.put(46077, 6035);
        PLNPC.put(46078, 6035);
        PLNPC.put(46079, 6035);
        PLNPC.put(46080, 6035);
        PLNPC.put(46081, 6035);
        PLNPC.put(46082, 6035);
        PLNPC.put(46083, 6035);
        PLNPC.put(46084, 6035);
        PLNPC.put(46085, 6035);
        PLNPC.put(46086, 6035);
        PLNPC.put(46087, 6035);
        PLNPC.put(46088, 6035);
        PLNPC.put(46089, 6035);
        PLNPC.put(46090, 6035);
        PLNPC.put(46091, 6035);
        PLNPC.put(46092, 6034);
        PLNPC.put(46093, 6034);
        PLNPC.put(46094, 6034);
        PLNPC.put(46095, 6034);
        PLNPC.put(46096, 6034);
        PLNPC.put(46097, 6034);
        PLNPC.put(46098, 6034);
        PLNPC.put(46099, 6034);
        PLNPC.put(46100, 6034);
        PLNPC.put(46101, 6034);
        PLNPC.put(46102, 6034);
        PLNPC.put(46103, 6034);
        PLNPC.put(46104, 6034);
        PLNPC.put(46105, 6034);
        PLNPC.put(46106, 6034);
        int strH = 0;
        STRH.put(++strH, -2);// 1
        STRH.put(++strH, -2);
        STRH.put(++strH, -2);
        STRH.put(++strH, -2);
        STRH.put(++strH, -2);
        STRH.put(++strH, -2);
        STRH.put(++strH, -2);
        STRH.put(++strH, -2);
        STRH.put(++strH, -1);
        STRH.put(++strH, -1);
        STRH.put(++strH, 0);
        STRH.put(++strH, 0);
        STRH.put(++strH, 1);
        STRH.put(++strH, 1);
        STRH.put(++strH, 2);
        STRH.put(++strH, 2);
        STRH.put(++strH, 3);
        STRH.put(++strH, 3);
        STRH.put(++strH, 4);
        STRH.put(++strH, 4);
        STRH.put(++strH, 5);
        STRH.put(++strH, 5);
        STRH.put(++strH, 5);
        STRH.put(++strH, 6);
        STRH.put(++strH, 6);
        STRH.put(++strH, 6);
        STRH.put(++strH, 7);
        STRH.put(++strH, 7);
        STRH.put(++strH, 7);
        STRH.put(++strH, 8);
        STRH.put(++strH, 8);
        STRH.put(++strH, 8);
        STRH.put(++strH, 9);
        STRH.put(++strH, 9);
        STRH.put(++strH, 9);// 35
        STRH.put(++strH, 10);
        STRH.put(++strH, 10);
        STRH.put(++strH, 10);
        STRH.put(++strH, 11);
        STRH.put(++strH, 11);
        STRH.put(++strH, 11);
        STRH.put(++strH, 12);
        STRH.put(++strH, 12);
        STRH.put(++strH, 12);
        STRH.put(++strH, 13);
        STRH.put(++strH, 13);
        STRH.put(++strH, 13);
        STRH.put(++strH, 14);
        STRH.put(++strH, 14);
        STRH.put(++strH, 14);
        STRH.put(++strH, 15);
        STRH.put(++strH, 15);
        STRH.put(++strH, 15);
        STRH.put(++strH, 16);
        STRH.put(++strH, 16);
        STRH.put(++strH, 16);
        STRH.put(++strH, 17);
        STRH.put(++strH, 17);
        STRH.put(++strH, 17);
        STRH.put(++strH, 18);// 60
        int dexH = 0;
        DEXH.put(++dexH, -2);
        DEXH.put(++dexH, -2);
        DEXH.put(++dexH, -2);
        DEXH.put(++dexH, -2);
        DEXH.put(++dexH, -2);
        DEXH.put(++dexH, -2);
        DEXH.put(++dexH, -1);
        DEXH.put(++dexH, -1);
        DEXH.put(++dexH, 0);
        DEXH.put(++dexH, 0);
        DEXH.put(++dexH, 1);
        DEXH.put(++dexH, 1);
        DEXH.put(++dexH, 2);
        DEXH.put(++dexH, 2);
        DEXH.put(++dexH, 3);
        DEXH.put(++dexH, 3);
        DEXH.put(++dexH, 4);
        DEXH.put(++dexH, 4);
        DEXH.put(++dexH, 5);
        DEXH.put(++dexH, 6);
        DEXH.put(++dexH, 7);
        DEXH.put(++dexH, 8);
        DEXH.put(++dexH, 9);
        DEXH.put(++dexH, 10);
        DEXH.put(++dexH, 11);
        DEXH.put(++dexH, 12);
        DEXH.put(++dexH, 13);
        DEXH.put(++dexH, 14);
        DEXH.put(++dexH, 15);
        DEXH.put(++dexH, 16);
        DEXH.put(++dexH, 17);
        DEXH.put(++dexH, 18);
        DEXH.put(++dexH, 19);
        DEXH.put(++dexH, 19);
        DEXH.put(++dexH, 19);
        DEXH.put(++dexH, 20);
        DEXH.put(++dexH, 20);
        DEXH.put(++dexH, 20);
        DEXH.put(++dexH, 21);
        DEXH.put(++dexH, 21);
        DEXH.put(++dexH, 21);
        DEXH.put(++dexH, 22);
        DEXH.put(++dexH, 22);
        DEXH.put(++dexH, 22);
        DEXH.put(++dexH, 23);
        DEXH.put(++dexH, 23);
        DEXH.put(++dexH, 23);
        DEXH.put(++dexH, 24);
        DEXH.put(++dexH, 24);
        DEXH.put(++dexH, 24);
        DEXH.put(++dexH, 25);
        DEXH.put(++dexH, 25);
        DEXH.put(++dexH, 25);
        DEXH.put(++dexH, 26);
        DEXH.put(++dexH, 26);
        DEXH.put(++dexH, 26);
        DEXH.put(++dexH, 27);
        DEXH.put(++dexH, 27);
        DEXH.put(++dexH, 27);
        DEXH.put(++dexH, 28);
        // 力量傷害補正
        for (int str = 0; str <= 8; str++) {
            // 1~8 -2
            STRD.put(str, -2);
        }
        for (int str = 9; str <= 10; str++) {
            // 9~10 -1
            STRD.put(str, -1);
        }
        STRD.put(11, 0);
        STRD.put(12, 0);
        STRD.put(13, 1);
        STRD.put(14, 1);
        STRD.put(15, 2);
        STRD.put(16, 2);
        STRD.put(17, 3);
        STRD.put(18, 3);
        STRD.put(19, 4);
        STRD.put(20, 4);
        STRD.put(21, 5);
        STRD.put(22, 5);
        STRD.put(23, 6);
        STRD.put(24, 6);
        STRD.put(25, 6);
        STRD.put(26, 7);
        STRD.put(27, 7);
        STRD.put(28, 7);
        STRD.put(29, 8);
        STRD.put(30, 8);
        STRD.put(31, 9);
        STRD.put(32, 9);
        STRD.put(33, 10);
        STRD.put(34, 11);
        STRD.put(35, 12);
        STRD.put(36, 12);
        STRD.put(37, 12);
        STRD.put(38, 12);
        STRD.put(39, 13);
        STRD.put(40, 13);
        STRD.put(41, 13);
        STRD.put(42, 13);
        STRD.put(43, 14);
        STRD.put(44, 14);
        STRD.put(45, 14);
        STRD.put(46, 14);
        STRD.put(47, 15);
        STRD.put(48, 15);
        STRD.put(49, 16);
        STRD.put(50, 17);
        int strdmg = 18;
        for (int str = 51; str <= 127; str++) { // 51~127 4＋1
            if (str % 4 == 1) {
                strdmg++;
            }
            STRD.put(str, strdmg);
        }
        //		int dmgStr = -6;
        //		for (int str = 0; str <= 22; str++) { // 0~22 每2+1
        //			if (str % 2 == 1) {
        //				dmgStr++;
        //			}
        //			STRD.put(new Integer(str), new Integer(dmgStr));
        //		}
        //		for (int str = 23; str <= 28; str++) { // 23~28 每3+1
        //			if (str % 3 == 2) {
        //				dmgStr++;
        //			}
        //			STRD.put(new Integer(str), new Integer(dmgStr));
        //		}
        //		for (int str = 29; str <= 32; str++) { // 29~32 每2+1
        //			if (str % 2 == 1) {
        //				dmgStr++;
        //			}
        //			STRD.put(new Integer(str), new Integer(dmgStr));
        //		}
        //		for (int str = 33; str <= 34; str++) { // 33~34 每1+1
        //			dmgStr++;
        //			STRD.put(new Integer(str), new Integer(dmgStr));
        //		}
        //		for (int str = 35; str <= 254; str++) { // 35~254 每4+1
        //			if (str % 4 == 1) {
        //				dmgStr++;
        //			}
        //			STRD.put(new Integer(str), new Integer(dmgStr));
        //		}
        // 敏捷傷害補正
        for (int dex = 0; dex <= 11; dex++) {
            // 0~11= 0
            DEXD.put(dex, 0);
        }
        int dexdmg = 1;
        for (int dex = 12; dex <= 127; dex++) { //12~127 =>4 +1
            if (dex % 4 == 1) {
                dexdmg++;
            }
            DEXD.put(dex, dexdmg);
        }
        //		for (int dex = 0; dex <= 14; dex++) {
        //			// 0~14 = 0
        //			DEXD.put(new Integer(dex), new Integer(0));
        //		}
        //
        //		DEXD.put(new Integer(15), new Integer(1));
        //		DEXD.put(new Integer(16), new Integer(2));
        //		DEXD.put(new Integer(17), new Integer(3));
        //		DEXD.put(new Integer(18), new Integer(4));
        //		DEXD.put(new Integer(19), new Integer(4));
        //		DEXD.put(new Integer(20), new Integer(4));
        //		DEXD.put(new Integer(21), new Integer(5));
        //		DEXD.put(new Integer(22), new Integer(5));
        //		DEXD.put(new Integer(23), new Integer(5));
        //
        //		int dmgDex = 5;
        //		for (int dex = 24; dex <= 35; dex++) { // 24~35 每3+1
        //			if (dex % 3 == 1) {
        //				dmgDex++;
        //			}
        //			DEXD.put(new Integer(dex), new Integer(dmgDex));
        //		}
        //		for (int dex = 36; dex <= 127; dex++) { // 36~127 每4+1
        //			if (dex % 4 == 1) {
        //				dmgDex++;
        //			}
        //			DEXD.put(new Integer(dex), new Integer(dmgDex));
        //		}
    }
}
