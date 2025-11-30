package com.add.Tsai;

import com.lineage.server.Manly.L1WenYang;
import com.lineage.server.Manly.L1WenYangJiLu;
import com.lineage.server.Manly.WenYangJiLuTable;
import com.lineage.server.Manly.WenYangTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_MPUpdate;
import com.lineage.server.serverpackets.S_OwnCharAttrDef;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 紋樣指令（精簡重整版）
 * - 不再使用 rate / maxRate / costup / cost
 * - a*-1-up：只重抽 +1/+2/+3 分布（可一直按）
 * - a*-1：開始強化（成本=0），成功依當前分布決定 +1/+2/+3
 * - 面板 #2 顯示 1(XX.x%) 2(YY.x%) 3(ZZ.x%)（0.1% 精度）
 * - 強化完成後自動刷新下一輪分布
 */
public class WenYangCmd {

    /** 依玩家/格位暫存當前分布：key="pcId:slot" → int[]{p1,p2,p3}；單位：0.1%，總和=1000 */
    private static final Map<String, int[]> _distCache = new ConcurrentHashMap<>();
    private static String k(int pid, int slot) { return pid + ":" + slot; }

    /** 伺服器紋樣等級硬上限 */
    private static final int LEVEL_CAP = 30;

    /** 成功次數上限（每個 type 各計） */
    private static final int SUCCESS_CAP = 20;

    private static final Log _log = LogFactory.getLog(WenYangCmd.class);
    private static WenYangCmd _instance;

    /** 逐玩家動畫鎖：key=pcId，value=true 表示該玩家正在動畫中 */
    private static final Map<Integer, Boolean> _animating = new ConcurrentHashMap<>();

    public static WenYangCmd get() {
        if (_instance == null) {
            _instance = new WenYangCmd();
        }
        return _instance;
    }
    /** 依槽位抓「下一階」的模板；等級0視為抓第1階 */
    private L1WenYang getNextStageTemplate(final L1PcInstance pc, final int slot) {
        final int type = getWyType(pc, slot);
        final int level = getWyLevelBySlot(pc, slot);
        final int queryLevel = (level <= 0) ? 1 : level + 1; // ★ 關鍵：用下一階
        return WenYangTable.getInstance().getTemplate(type, queryLevel);
    }
    /** 入口：處理 NPC 指令 */
    public boolean Cmd(final L1PcInstance pc, final String cmd) {
        try {
            // ★ 每格位都抓「下一階」模板
            final L1WenYang wy1 = getNextStageTemplate(pc, 1); if (wy1 == null) return false;
            final L1WenYang wy2 = getNextStageTemplate(pc, 2); if (wy2 == null) return false;
            final L1WenYang wy3 = getNextStageTemplate(pc, 3); if (wy3 == null) return false;
            final L1WenYang wy4 = getNextStageTemplate(pc, 4); if (wy4 == null) return false;
            final L1WenYang wy5 = getNextStageTemplate(pc, 5); if (wy5 == null) return false;
            final L1WenYang wy6 = getNextStageTemplate(pc, 6); if (wy6 == null) return false;

            // ===== 顯示面板（保留你的分路；面板內 #3/#4 會用 wy 的 costup/cost）=====
            if (cmd.equals("wenyang01_01")) { showPanel(pc, pc.getWyType1(), wy1, pc.getWyLevel1()); return true; }
            if (cmd.equals("wenyang02_01")) { showPanel(pc, pc.getWyType2(), wy2, pc.getWyLevel2()); return true; }
            if (cmd.equals("wenyang03_01")) { showPanel(pc, pc.getWyType3(), wy3, pc.getWyLevel3()); return true; }
            if (cmd.equals("wenyang04_01")) { showPanel(pc, pc.getWyType4(), wy4, pc.getWyLevel4()); return true; }
            if (cmd.equals("wenyang05_01")) { showPanel(pc, pc.getWyType5(), wy5, pc.getWyLevel5()); return true; }
            if (cmd.equals("wenyang06_01")) { showPanel(pc, pc.getWyType6(), wy6, pc.getWyLevel6()); return true; }

            // ===== 變更機率（扣 wy.getCostUp()，重抽分布）=====
            if (cmd.equals("a1-1-up")) { stepChange(pc, wy1, 1); return true; }
            if (cmd.equals("a2-1-up")) { stepChange(pc, wy2, 2); return true; }
            if (cmd.equals("a3-1-up")) { stepChange(pc, wy3, 3); return true; }
            if (cmd.equals("a4-1-up")) { stepChange(pc, wy4, 4); return true; }
            if (cmd.equals("a5-1-up")) { stepChange(pc, wy5, 5); return true; }
            if (cmd.equals("a6-1-up")) { stepChange(pc, wy6, 6); return true; }

            // ===== 開始強化（扣 wy.getCost()）=====
            if (cmd.equals("a1-1")) { tryEnhance(pc, 1, wy1, pc.getWenyangRate1()); return true; }
            if (cmd.equals("a2-1")) { tryEnhance(pc, 2, wy2, pc.getWenyangRate2()); return true; }
            if (cmd.equals("a3-1")) { tryEnhance(pc, 3, wy3, pc.getWenyangRate3()); return true; }
            if (cmd.equals("a4-1")) { tryEnhance(pc, 4, wy4, pc.getWenyangRate4()); return true; }
            if (cmd.equals("a5-1")) { tryEnhance(pc, 5, wy5, pc.getWenyangRate5()); return true; }
            if (cmd.equals("a6-1")) { tryEnhance(pc, 6, wy6, pc.getWenyangRate6()); return true; }

            return false;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }


    // =========================================================
    // 面板資料 / 動畫 / 記錄
    // =========================================================

    /** 顯示面板 */
    private void showPanel(final L1PcInstance pc, final int type, final L1WenYang wy, final int level) {
        String[] data = buildPanelData(pc, type, wy, level);
        pc.sendPackets(new S_NPCTalkReturn(pc, getHtmlId(type, level), data));
    }
    /** 面板資料：
     *  #0 等級
     *  #1 玩家現有積分
     *  #2 +1/+2/+3 機率（顯示一位小數）
     *  #3 變更機率扣點（costup：每按一次 a*-1-up 扣）
     *  #4 強化消耗點數（cost：按 a*-1 時扣）
     */
    private String[] buildPanelData(final L1PcInstance pc, final int type, final L1WenYang wy, final int level) {
        int slot = getSlotByType(pc, type);
        int[] dist = ensureDist(pc, slot, wy); // 單位：0.1%，總和=1000

        String distStr = String.format("1(%.1f%%)  2(%.1f%%)  3(%.1f%%)",
                dist[0] / 10.0, dist[1] / 10.0, dist[2] / 10.0);

        String[] data = new String[5];
        data[0] = String.valueOf(level);
        data[1] = String.valueOf(pc.getWenyangJiFen());
        data[2] = distStr;
        data[3] = String.valueOf(wy.getCostUp()); // 變更機率扣點
        data[4] = String.valueOf(wy.getCost());   // ★ 強化消耗點數
        return data;
    }



    /** 重抽分布（總和=1000；受 +2/+3 cap 影響） */
    private void randomizeDist(final L1PcInstance pc, final int slot, final L1WenYang wy) {
        final String key = k(pc.getId(), slot);

        int cap2 = (wy.getPlus2Cap() > 0) ? Math.min(wy.getPlus2Cap(), 100) : 100;
        int cap3 = (wy.getPlus3Cap() > 0) ? Math.min(wy.getPlus3Cap(), 100) : 100;

        int cap2x = cap2 * 10; // 0.1% 精度
        int cap3x = cap3 * 10;

        int p2 = ThreadLocalRandom.current().nextInt(cap2x + 1);                                  // 0..cap2x
        int p3 = ThreadLocalRandom.current().nextInt(Math.min(cap3x, 1000 - p2) + 1);             // 0..min(cap3x,1000-p2)
        int p1 = Math.max(0, 1000 - p2 - p3);

        _distCache.put(key, new int[]{p1, p2, p3});
    }

    /** 讀取當前分布（若沒有就先建一組） */
    private int[] ensureDist(final L1PcInstance pc, final int slot, final L1WenYang wy) {
        final String key = k(pc.getId(), slot);
        int[] d = _distCache.get(key);
        if (d == null || d.length != 3 || d[0] + d[1] + d[2] != 1000) {
            randomizeDist(pc, slot, wy);
            d = _distCache.get(key);
        }
        return new int[]{d[0], d[1], d[2]};
    }

    private int getSlotByType(final L1PcInstance pc, final int type) {
        if (type == pc.getWyType1()) return 1;
        if (type == pc.getWyType2()) return 2;
        if (type == pc.getWyType3()) return 3;
        if (type == pc.getWyType4()) return 4;
        if (type == pc.getWyType5()) return 5;
        if (type == pc.getWyType6()) return 6;
        return 0;
    }

    /** 播放動畫（逐玩家鎖） */
    public void playAnimation(final L1PcInstance pc, final L1NpcInstance npc, int type, L1WenYang wy, int ignored) {
        final int pid = pc.getId();
        _animating.put(pid, true);
        try {
            String[] data = buildPanelData(pc, type, wy, /*level*/0);
            for (int i = 1; i <= 31; i++) {
                String htmlId = String.format("wenyang%02d_%02d", type, i);
                pc.sendPackets(new S_NPCTalkReturn(pc, htmlId, data));
                TimeUnit.MILLISECONDS.sleep(50);
            }
        } catch (InterruptedException ignoredEx) {
            // 忽略中斷
        }
    }

    /** 動畫結束顯示 + 解鎖 */
    public void finishAnimation(final L1PcInstance pc, final L1NpcInstance npc, int type, L1WenYang wy, int ignored, int level) {
        try {
            String[] data = buildPanelData(pc, type, wy, level);
            pc.sendPackets(new S_NPCTalkReturn(pc, getHtmlId(type, level), data));
        } finally {
            _animating.remove(pc.getId());
        }
    }

    /** HTML 檔名：type 與 level+1（沿用舊邏輯） */
    private String getHtmlId(int type, int level) {
        return String.format("wenyang%02d_%02d", type, Math.min(LEVEL_CAP, level + 1));
    }

    /** 寫入成功紀錄 */
    private void addLog(final L1PcInstance pc, final int type, final int level) {
        final L1WenYangJiLu row = new L1WenYangJiLu();
        row.setPcid(pc.getId());
        row.setType(type);
        row.setLevel(level);
        WenYangJiLuTable.getInstance().storeOrUpdateSuccess(pc, row);
    }

    // =========================================================
    // 變更（原 stepRateUp）：只重抽分布並重畫
    // =========================================================
    /** 變更機率：每按一次扣 costup，然後重抽 +1/+2/+3 分布（可無限按） */
    private void stepChange(final L1PcInstance pc, final L1WenYang wy, final int slot) throws Exception {
        int stepCost = wy.getCostUp();
        if (stepCost > 0) {
            if (pc.getWenyangJiFen() < stepCost) {
                pc.sendPackets(new S_SystemMessage("您的紋樣積分不足，無法變更機率"));
                return;
            }
            pc.setWenyangJiFen(pc.getWenyangJiFen() - stepCost);
            if (pc.getWenyangJiFen() < 0) pc.setWenyangJiFen(0);
            pc.save();
        }

        // 重抽分布（受 +2/+3 上限限制；0.1% 精度）
        randomizeDist(pc, slot, wy);

        // 重畫面板（注意 showPanel 這版不需要 rateSel 參數）
        showPanel(pc, getWyType(pc, slot), wy, getWyLevelBySlot(pc, slot));
    }


    /** 開始強化（入口檢查 + 丟動畫執行緒） */
    private void tryEnhance(final L1PcInstance pc, final int slot, final L1WenYang wy, final int ignoredRateSel) {
        final int type  = getWyType(pc, slot);
        final int level = getWyLevelBySlot(pc, slot);

        // 成功次數上限
        int successCount = WenYangJiLuTable.getInstance().getSuccessCountFromDB(pc.getId(), type);
        if (successCount >= SUCCESS_CAP) {
            pc.sendPackets(new S_SystemMessage("該紋樣強化成功次數已達 " + SUCCESS_CAP + " 次上限，無法再強化。"));
            return;
        }

        // 等級上限
        if (level >= LEVEL_CAP) {
            pc.sendPackets(new S_SystemMessage("伺服器最高紋樣等級為 " + LEVEL_CAP + " 等"));
            return;
        }

        // ★ 恢復：強化消耗點數
        final int cost = Math.max(0, wy.getCost());
        if (pc.getWenyangJiFen() < cost) {
            pc.sendPackets(new S_SystemMessage("您的紋樣積分不足，無法進行強化"));
            return;
        }

        // 動畫鎖
        if (_animating.containsKey(pc.getId())) {
            pc.sendPackets(new S_SystemMessage("請稍後再試"));
            return;
        }

        // 傳遞 cost 進動畫執行緒
        GeneralThreadPool.get().execute(
                new WenYangAnimation(pc, type, wy, /*effectiveRateIgnored*/0, /*cost*/cost, slot)
        );
    }


    // =========================================================
    // 內部工具：存取玩家的六組資料（type / level / rate）
    // =========================================================
    private int getWyType(final L1PcInstance pc, final int slot) {
        switch (slot) {
            case 1: return pc.getWyType1();
            case 2: return pc.getWyType2();
            case 3: return pc.getWyType3();
            case 4: return pc.getWyType4();
            case 5: return pc.getWyType5();
            case 6: return pc.getWyType6();
            default: return 0;
        }
    }

    private int getWyLevel(final L1PcInstance pc, final int type) {
        if (type == pc.getWyType1()) return pc.getWyLevel1();
        if (type == pc.getWyType2()) return pc.getWyLevel2();
        if (type == pc.getWyType3()) return pc.getWyLevel3();
        if (type == pc.getWyType4()) return pc.getWyLevel4();
        if (type == pc.getWyType5()) return pc.getWyLevel5();
        if (type == pc.getWyType6()) return pc.getWyLevel6();
        return 0;
    }

    private int getWyLevelBySlot(final L1PcInstance pc, final int slot) {
        switch (slot) {
            case 1: return pc.getWyLevel1();
            case 2: return pc.getWyLevel2();
            case 3: return pc.getWyLevel3();
            case 4: return pc.getWyLevel4();
            case 5: return pc.getWyLevel5();
            case 6: return pc.getWyLevel6();
            default: return 0;
        }
    }
    /** 依槽位把玩家紋樣等級 +delta（至少 +1；封頂 LEVEL_CAP） */
    private void addWyLevelBySlot(final L1PcInstance pc, final int slot, final int delta) {
        int cur = getWyLevelBySlot(pc, slot);
        int next = Math.min(LEVEL_CAP, Math.max(0, cur + Math.max(1, delta)));
        switch (slot) {
            case 1: pc.setWyLevel1(next); break;
            case 2: pc.setWyLevel2(next); break;
            case 3: pc.setWyLevel3(next); break;
            case 4: pc.setWyLevel4(next); break;
            case 5: pc.setWyLevel5(next); break;
            case 6: pc.setWyLevel6(next); break;
        }
    }

    // =========================================================
    // 動畫執行緒
    // =========================================================
    class WenYangAnimation implements Runnable {
        private final L1PcInstance _pc;
        private final int _type;
        private final L1WenYang _wy;
        private final int _effectiveRateIgnored;
        private final int _cost;   // ★ 這裡改名成 _cost
        private final int _slot;

        public WenYangAnimation(final L1PcInstance pc, int type, L1WenYang wy, int effectiveRateIgnored, int cost, int slot) {
            _pc = pc;
            _type = type;
            _wy = wy;
            _effectiveRateIgnored = effectiveRateIgnored;
            _cost = cost; // ★
            _slot = slot;
        }

        @Override
        public void run() {
            try {
                // 播動畫（加鎖）
                playAnimation(_pc, null, _type, _wy, 0);

                // 二次防呆：成功次數上限
                int successCountNow = WenYangJiLuTable.getInstance().getSuccessCount(_pc.getId(), _type);
                if (successCountNow >= SUCCESS_CAP) {
                    _pc.sendPackets(new S_SystemMessage("該紋樣強化成功次數已達 " + SUCCESS_CAP + " 次上限，無法再強化。"));
                    finishAnimation(_pc, null, _type, _wy, 0, getWyLevel(_pc, _type));
                    return;
                }

                // 扣強化消耗點數（下一階的 cost）
                if (_cost > 0) {
                    _pc.setWenyangJiFen(_pc.getWenyangJiFen() - _cost);
                    if (_pc.getWenyangJiFen() < 0) _pc.setWenyangJiFen(0);
                    _pc.save();
                }

                // 依分布決定 +1 / +2 / +3（0.1% 精度）
                int[] dist = ensureDist(_pc, _slot, _wy);
                int p1 = dist[0], p2 = dist[1], p3 = dist[2];
                int roll = ThreadLocalRandom.current().nextInt(1000);
                int add = (roll < p3) ? 3 : (roll < p3 + p2) ? 2 : 1;

                // 實際升級
                addWyLevelBySlot(_pc, _slot, add);

                // 立即應用新的紋樣能力
                applyWyAbilities(_pc, _slot);

                _pc.sendPackets(new S_SystemMessage("恭喜升級成功 (+" + add + ")，紋樣屬性已立即生效"));
                _pc.save();

                // 記錄一次成功
                addLog(_pc, _type, getWyLevel(_pc, _type));

                // ★ 升級後重新抓「新下一階模板」用於顯示與分布上限
                L1WenYang wyNext = getNextStageTemplate(_pc, _slot);
                if (wyNext == null) {
                    // 已達上限：用目前這階的 wy 作為顯示基底，或你也可以顯示 cost=0
                    wyNext = _wy;
                }

                // 強化完成後：刷新下一輪分布（用新下一階的 cap）
                randomizeDist(_pc, _slot, wyNext);

                // 收尾顯示（用新下一階 wyNext）
                finishAnimation(_pc, null, _type, wyNext, 0, getWyLevel(_pc, _type));

            } catch (Exception e) {
                _log.error(e.getLocalizedMessage(), e);
                try {
                    finishAnimation(_pc, null, _type, _wy, 0, getWyLevel(_pc, _type));
                } catch (Exception ignore) {}
            }
        }
    }

    /**
     * 立即應用指定槽位的紋樣能力
     * @param pc 玩家實例
     * @param slot 槽位編號 (1-6)
     */
    private void applyWyAbilities(final L1PcInstance pc, final int slot) {
        try {
            int type = getWyType(pc, slot);
            int level = getWyLevelBySlot(pc, slot);

            if (type == 0 || level <= 0) {
                return; // 沒有紋樣或等級為0，不應用能力
            }

            L1WenYang wenYang = WenYangTable.getInstance().getTemplate(type, level);
            if (wenYang == null) {
                return; // 找不到對應的紋樣模板
            }

            // 應用紋樣能力（複製自 C_LoginToServer.java 中的邏輯）
            int liliang = wenYang.getliliang();
            if (liliang != 0) {
                pc.addStr(liliang);
            }
            int minjie = wenYang.getminjie();
            if (minjie != 0) {
                pc.addDex(minjie);
            }
            int zhili = wenYang.getzhili();
            if (zhili != 0) {
                pc.addInt(zhili);
            }
            int jingshen = wenYang.getjingshen();
            if (jingshen != 0) {
                pc.addWis(jingshen);
            }
            int tizhi = wenYang.gettizhi();
            if (tizhi != 0) {
                pc.addCon(tizhi);
            }
            int meili = wenYang.getmeili();
            if (meili != 0) {
                pc.addCha(meili);
            }
            int xue = wenYang.getxue();
            if (xue != 0) {
                pc.addMaxHp(xue);
                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            }
            int mo = wenYang.getmo();
            if (mo != 0) {
                pc.addMaxMp(mo);
                pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
            }
            int huixue = wenYang.gethuixue();
            if (huixue != 0) {
                pc.addHpr(huixue);
            }
            int huimo = wenYang.gethuimo();
            if (huimo != 0) {
                pc.addMpr(huimo);
            }
            int ewai = wenYang.getewai();
            if (ewai != 0) {
                pc.addDmgup(ewai);
            }
            int chenggong = wenYang.getchenggong();
            if (chenggong != 0) {
                pc.addHitup(chenggong);
            }
            int mogong = wenYang.getmogong();
            if (mogong != 0) {
                pc.addSp(mogong);
            }
            int mofang = wenYang.getmofang();
            if (mofang != 0) {
                pc.addMr(mofang);
            }
            int feng = wenYang.getfeng();
            if (feng != 0) {
                pc.addWind(feng);
            }
            int shui = wenYang.getshui();
            if (shui != 0) {
                pc.addWater(shui);
            }
            int tu = wenYang.gettu();
            if (tu != 0) {
                pc.addEarth(tu);
            }
            int huo = wenYang.gethuo();
            if (huo != 0) {
                pc.addFire(huo);
            }

            // 處理防禦屬性
            int addAc = wenYang.getAc();
            if (addAc != 0) {
                pc.addAc(addAc);
                // 更新客戶端防禦屬性顯示
                pc.sendPackets(new S_OwnCharAttrDef(pc));
            }

            // 更新狀態顯示
            pc.sendPackets(new S_OwnCharStatus(pc));
            pc.sendPackets(new S_SPMR(pc));

        } catch (Exception e) {
            _log.error("應用紋樣能力時發生錯誤: " + e.getLocalizedMessage(), e);
        }
    }
}