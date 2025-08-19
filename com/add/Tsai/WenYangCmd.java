package com.add.Tsai;

import com.lineage.server.Manly.L1WenYang;
import com.lineage.server.Manly.L1WenYangJiLu;
import com.lineage.server.Manly.WenYangJiLuTable;
import com.lineage.server.Manly.WenYangTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 紋樣指令（重整版）
 * - 成功次數上限=20（每個 type 獨立計次）
 * - 成功時有機率+2或+3 等級（仍只算 1 次成功）
 * - 逐玩家動畫鎖（不會全域卡住）
 * - 統一成本與成功率計算（UI顯示與實扣一致）
 * - 修正原碼多處 typos（a3/a4/a5/a6 分支）
 *
 * @author hero
 */
public class WenYangCmd {

    /** DB 沒填或填 0 時的保底預設值（單位：%）*/
    private static final int PLUS2_DEFAULT = 10;
    private static final int PLUS3_DEFAULT = 3;

    /** 面板上下按鈕每次調整的百分比（UI步進用） */
    private static final int STEP_RATE = 2;

    /** 伺服器紋樣等級硬上限 */
    private static final int LEVEL_CAP = 30;

    /** 成功次數上限（每個 type 各計） */
    private static final int SUCCESS_CAP = 20;

    /** 一次+2 機率(%)（可依需求調整） */
    private static final int PLUS2_RATE = 10;

    /** 一次+3 機率(%)（可依需求調整；建議 < PLUS2_RATE） */
    private static final int PLUS3_RATE = 3;

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

    /**
     * 入口：處理 NPC 指令
     */
    public boolean Cmd(final L1PcInstance pc, final String cmd) {
        try {
            // 讀表（若玩家等級==0 => 視為要顯示第1頁）
            final L1WenYang wenYang1 = WenYangTable.getInstance().getTemplate(pc.getWyType1(), pc.getWyLevel1() == 0 ? 1 : pc.getWyLevel1());
            if (wenYang1 == null) return false;
            final L1WenYang wenYang2 = WenYangTable.getInstance().getTemplate(pc.getWyType2(), pc.getWyLevel2() == 0 ? 1 : pc.getWyLevel2());
            if (wenYang2 == null) return false;
            final L1WenYang wenYang3 = WenYangTable.getInstance().getTemplate(pc.getWyType3(), pc.getWyLevel3() == 0 ? 1 : pc.getWyLevel3());
            if (wenYang3 == null) return false;
            final L1WenYang wenYang4 = WenYangTable.getInstance().getTemplate(pc.getWyType4(), pc.getWyLevel4() == 0 ? 1 : pc.getWyLevel4());
            if (wenYang4 == null) return false;
            final L1WenYang wenYang5 = WenYangTable.getInstance().getTemplate(pc.getWyType5(), pc.getWyLevel5() == 0 ? 1 : pc.getWyLevel5());
            if (wenYang5 == null) return false;
            final L1WenYang wenYang6 = WenYangTable.getInstance().getTemplate(pc.getWyType6(), pc.getWyLevel6() == 0 ? 1 : pc.getWyLevel6());
            if (wenYang6 == null) return false;

            // ===== 顯示面板 =====
            if (cmd.equals("wenyang01_01")) {
                showPanel(pc, pc.getWyType1(), wenYang1, pc.getWenyangRate1(), pc.getWyLevel1());
                return true;
            }
            if (cmd.equals("wenyang02_01")) {
                showPanel(pc, pc.getWyType2(), wenYang2, pc.getWenyangRate2(), pc.getWyLevel2());
                return true;
            }
            if (cmd.equals("wenyang03_01")) {
                showPanel(pc, pc.getWyType3(), wenYang3, pc.getWenyangRate3(), pc.getWyLevel3());
                return true;
            }
            if (cmd.equals("wenyang04_01")) { // 修：原碼誤用 wenYang1
                showPanel(pc, pc.getWyType4(), wenYang4, pc.getWenyangRate4(), pc.getWyLevel4());
                return true;
            }
            if (cmd.equals("wenyang05_01")) {
                showPanel(pc, pc.getWyType5(), wenYang5, pc.getWenyangRate5(), pc.getWyLevel5());
                return true;
            }
            if (cmd.equals("wenyang06_01")) {
                showPanel(pc, pc.getWyType6(), wenYang6, pc.getWenyangRate6(), pc.getWyLevel6());
                return true;
            }

            // ===== Rate 調整按鈕 =====
            if (cmd.equals("a1-1-up")) { stepRateUp(pc, wenYang1, 1); return true; }
            if (cmd.equals("a1-1-dn")) { stepRateDn(pc, wenYang1, 1); return true; }

            if (cmd.equals("a2-1-up")) { stepRateUp(pc, wenYang2, 2); return true; }
            if (cmd.equals("a2-1-dn")) { stepRateDn(pc, wenYang2, 2); return true; }

            if (cmd.equals("a3-1-up")) { stepRateUp(pc, wenYang3, 3); return true; }
            if (cmd.equals("a3-1-dn")) { stepRateDn(pc, wenYang3, 3); return true; } // 修：原碼誤用 wenYang1/rate1

            if (cmd.equals("a4-1-up")) { stepRateUp(pc, wenYang4, 4); return true; }
            if (cmd.equals("a4-1-dn")) { stepRateDn(pc, wenYang4, 4); return true; }

            if (cmd.equals("a5-1-up")) { stepRateUp(pc, wenYang5, 5); return true; }
            if (cmd.equals("a5-1-dn")) { stepRateDn(pc, wenYang5, 5); return true; }

            if (cmd.equals("a6-1-up")) { stepRateUp(pc, wenYang6, 6); return true; }
            if (cmd.equals("a6-1-dn")) { stepRateDn(pc, wenYang6, 6); return true; } // 修：原碼誤用 wenYang1/rate1

            // ===== 開始強化 =====
            if (cmd.equals("a1-1")) { tryEnhance(pc, 1, wenYang1, pc.getWenyangRate1()); return true; }
            if (cmd.equals("a2-1")) { tryEnhance(pc, 2, wenYang2, pc.getWenyangRate2()); return true; }
            if (cmd.equals("a3-1")) { tryEnhance(pc, 3, wenYang3, pc.getWenyangRate3()); return true; }
            if (cmd.equals("a4-1")) { tryEnhance(pc, 4, wenYang4, pc.getWenyangRate4()); return true; }
            if (cmd.equals("a5-1")) { tryEnhance(pc, 5, wenYang5, pc.getWenyangRate5()); return true; }
            if (cmd.equals("a6-1")) { tryEnhance(pc, 6, wenYang6, pc.getWenyangRate6()); return true; }

            return false;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    // =========================================================
    // 基本工具：面板資料、動畫、收尾、記錄
    // =========================================================

    /** 顯示面板 */
    private void showPanel(final L1PcInstance pc, final int type, final L1WenYang wy, final int rateSel, final int level) {
        String[] data = buildPanelData(pc, wy, rateSel, level);
        pc.sendPackets(new S_NPCTalkReturn(pc, getHtmlId(type, level), data));
    }

    /** 組面板資料：等級/積分/顯示成功率/成本 */
    private String[] buildPanelData(final L1PcInstance pc, final L1WenYang wy, final int rateSel, final int level) {
        int effectiveRate = Math.max(rateSel, wy.getRate());          // UI選擇與最低率取大
        effectiveRate = Math.min(effectiveRate, wy.getMaxRate());     // 夾上限
        int cost = calcCost(wy, effectiveRate);

        String[] data = new String[4];
        data[0] = String.valueOf(level);
        data[1] = String.valueOf(pc.getWenyangJiFen());
        data[2] = String.valueOf(effectiveRate);
        data[3] = String.valueOf(cost);
        return data;
    }

    /** 成本計算：高於表定率，每多 10% 加一次 costUp */
    private int calcCost(final L1WenYang wy, final int effectiveRate) {
        int base = wy.getCost();
        int minRate = wy.getRate();
        if (effectiveRate > minRate) {
            int extraSteps = (effectiveRate - minRate) / 2;
            base += extraSteps * wy.getCostUp();
        }
        return base;
    }


    /** 播放動畫（逐玩家鎖） */
    public void playAnimation(final L1PcInstance pc, final L1NpcInstance npc, int type, L1WenYang wy, int effectiveRate) {
        final int pid = pc.getId();
        _animating.put(pid, true);
        try {
            // 動畫期間不需顯示真實等級，用 0 當作面板上的等級展示（或保留 getWyLevel(pc,type) 也行）
            String[] data = buildPanelData(pc, wy, effectiveRate, 0);

            // 逐幀播放：01..31
            for (int i = 1; i <= 30; i++) {
                String htmlId = String.format("wenyang%02d_%02d", type, i);
                pc.sendPackets(new S_NPCTalkReturn(pc, htmlId, data));
                TimeUnit.MILLISECONDS.sleep(50);
            }
        } catch (InterruptedException ignored) {
            // 忽略中斷，finishAnimation 會做解鎖
        }
    }


    /** 動畫結束顯示 + 解鎖 */
    public void finishAnimation(final L1PcInstance pc, final L1NpcInstance npc, int type, L1WenYang wy, int effectiveRate, int level) {
        try {
            String[] data = buildPanelData(pc, wy, effectiveRate, level);
            pc.sendPackets(new S_NPCTalkReturn(pc, getHtmlId(type, level), data));
        } finally {
            _animating.remove(pc.getId());
        }
    }

    /** HTML 檔名：type 與 level+1（沿用舊邏輯） */
    private String getHtmlId(int type, int level) {
        return String.format("wenyang%02d_%02d", type, Math.min(LEVEL_CAP, level + 1));
    }

    /** 寫入成功紀錄（路線B：單筆覆蓋＋成功次數自增） */
    private void addLog(final L1PcInstance pc, final int type, final int level) {
        final L1WenYangJiLu row = new L1WenYangJiLu();
        row.setPcid(pc.getId());
        row.setType(type);
        row.setLevel(level);
        // 若 storeItem 已改成 UPSERT，就保持 storeItem(...)
        // 若你另做了新方法，請改呼叫：
        WenYangJiLuTable.getInstance().storeOrUpdateSuccess(pc, row);
    }


    // =========================================================
    // Rate 上下調整
    // =========================================================

    private void stepRateUp(final L1PcInstance pc, final L1WenYang wy, final int slot) {
        int cur = getUiRate(pc, slot);
        if (cur < wy.getRate()) cur = wy.getRate();
        int next = cur + STEP_RATE;
        if (next > wy.getMaxRate()) return; // 超過最大顯示率，忽略
        setUiRate(pc, slot, next);
        showPanel(pc, getWyType(pc, slot), wy, next, getWyLevelBySlot(pc, slot));
    }

    private void stepRateDn(final L1PcInstance pc, final L1WenYang wy, final int slot) {
        int cur = getUiRate(pc, slot);
        if (cur <= wy.getRate()) cur = wy.getRate();
        int next = cur - STEP_RATE;
        if (next < wy.getRate()) return; // 低於最低顯示率，忽略
        setUiRate(pc, slot, next);
        showPanel(pc, getWyType(pc, slot), wy, next, getWyLevelBySlot(pc, slot));
    }

    // =========================================================
    // 開始強化
    // =========================================================

    /** 開始強化（入口檢查 + 丟動畫執行緒） */
    private void tryEnhance(final L1PcInstance pc, final int slot, final L1WenYang wy, final int rateSel) {
        // 目前這格的類型與等級
        final int type  = getWyType(pc, slot);
        final int level = getWyLevelBySlot(pc, slot);

        // 成功率夾上下限 + 成本
        int effectiveRate = Math.max(rateSel, wy.getRate());
        effectiveRate = Math.min(effectiveRate, wy.getMaxRate());
        final int cost = calcCost(wy, effectiveRate);

        // 以 DB 為準檢查 20 次上限（避免快取/併發偏差）
        int successCount = WenYangJiLuTable.getInstance()
                .getSuccessCountFromDB(pc.getId(), type);
        if (successCount >= SUCCESS_CAP) {
            pc.sendPackets(new S_SystemMessage("該紋樣強化成功次數已達 " + SUCCESS_CAP + " 次上限，無法再強化。"));
            return;
        }

        // 積分檢查
        if (pc.getWenyangJiFen() < cost) {
            pc.sendPackets(new S_SystemMessage("您的紋樣積分不足"));
            return;
        }

        // 等級上限檢查
        if (level >= LEVEL_CAP) {
            pc.sendPackets(new S_SystemMessage("伺服器最高紋樣等級為 " + LEVEL_CAP + " 等"));
            return;
        }

        // 動畫鎖（逐玩家）
        if (_animating.containsKey(pc.getId())) {
            pc.sendPackets(new S_SystemMessage("請稍後再試"));
            return;
        }

        // 進入動畫流程（真正的扣點/判定/收尾在執行緒內進行）
        GeneralThreadPool.get().execute(
                new WenYangAnimation(pc, type, wy, effectiveRate, cost, slot)
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
        // 若你的 type 與 slot 一致可直接用 slot；這裡保留舊版 getWyLevel(type) 的查法：
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

    private int getUiRate(final L1PcInstance pc, final int slot) {
        switch (slot) {
            case 1: return pc.getWenyangRate1();
            case 2: return pc.getWenyangRate2();
            case 3: return pc.getWenyangRate3();
            case 4: return pc.getWenyangRate4();
            case 5: return pc.getWenyangRate5();
            case 6: return pc.getWenyangRate6();
            default: return 0;
        }
    }

    private void setUiRate(final L1PcInstance pc, final int slot, final int v) {
        switch (slot) {
            case 1: pc.setWenyangRate1(v); break;
            case 2: pc.setWenyangRate2(v); break;
            case 3: pc.setWenyangRate3(v); break;
            case 4: pc.setWenyangRate4(v); break;
            case 5: pc.setWenyangRate5(v); break;
            case 6: pc.setWenyangRate6(v); break;
        }
    }

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
    // 動畫執行緒：含成功+2/+3 與成功次數上限二次防呆
    // =========================================================
    class WenYangAnimation implements Runnable {
        private final L1PcInstance _pc;
        private final int _type;      // HTML 用的 type（ex: 01, 02...）
        private final L1WenYang _wy;
        private final int _effectiveRate;
        private final int _cost;
        private final int _slot;      // 1..6，用於直接存取該組 level/rate

        public WenYangAnimation(final L1PcInstance pc, int type, L1WenYang wy, int effectiveRate, int cost, int slot) {
            _pc = pc;
            _type = type;
            _wy = wy;
            _effectiveRate = effectiveRate;
            _cost = cost;
            _slot = slot;
        }

        @Override
        public void run() {
            try {
                // 播動畫（加鎖在內）
                playAnimation(_pc, null, _type, _wy, _effectiveRate);

                // 二次防呆：成功次數上限（避免競態）
                int successCountNow = WenYangJiLuTable.getInstance().getSuccessCount(_pc.getId(), _type);
                if (successCountNow >= SUCCESS_CAP) {
                    _pc.sendPackets(new S_SystemMessage("該紋樣強化成功次數已達 " + SUCCESS_CAP + " 次上限，無法再強化。"));
                    finishAnimation(_pc, null, _type, _wy, _effectiveRate, getWyLevel(_pc, _type));
                    return;
                }

                // 先扣積分（無論成功/失敗）
                _pc.setWenyangJiFen(_pc.getWenyangJiFen() - _cost);
                if (_pc.getWenyangJiFen() < 0) { // 保底：不為負
                    _pc.setWenyangJiFen(0);
                }

                // 成功判定
                boolean success = ThreadLocalRandom.current().nextInt(100) < _effectiveRate;

                if (success) {
                    // ★ 改成讀取 DB 裡的數值，若 DB 沒設定（=0）就用全域預設
                    int p3 = _wy.getPlus3Rate() > 0 ? _wy.getPlus3Rate() : PLUS3_DEFAULT;
                    int p2 = _wy.getPlus2Rate() > 0 ? _wy.getPlus2Rate() : PLUS2_DEFAULT;

                    // 避免總和超過 100%
                    int total = Math.min(100, p3 + p2);

                    int add = 1;
                    int roll = ThreadLocalRandom.current().nextInt(100); // 0..99
                    if (roll < p3) {
                        add = 3;
                    } else if (roll < total) {
                        add = 2;
                    }

                    addWyLevelBySlot(_pc, _slot, add);
                    _pc.sendPackets(new S_SystemMessage("恭喜升級成功 (+" + add + ")，重登後獲得紋樣屬性"));
                    _pc.save();

                    addLog(_pc, _type, getWyLevel(_pc, _type));

                } else {
                    _pc.sendPackets(new S_SystemMessage("很遺憾升級失敗"));
                    _pc.save();
                }

                // UI 收尾顯示
                finishAnimation(_pc, null, _type, _wy, _effectiveRate, getWyLevel(_pc, _type));

            } catch (Exception e) {
                _log.error(e.getLocalizedMessage(), e);
                try {
                    finishAnimation(_pc, null, _type, _wy, _effectiveRate, getWyLevel(_pc, _type));
                } catch (Exception ignore) {}
            }
        }
    }
}
