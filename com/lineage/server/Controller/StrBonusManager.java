package com.lineage.server.Controller;

import com.lineage.server.datatables.StrSettingTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_OwnCharStatus2;
import com.lineage.server.templates.StrSetting;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 力量加成管理器（不入侵 L1PcInstance 結構）：
 * - 針對玩家目前「總力量值」：回收舊加成 → 套用新加成 → 刷新面板
 * - 何時呼叫：登入完成、裝備穿脫後、洗點/BUFF/被動造成力量變動後、GM熱重載後
 */
public class StrBonusManager {
    private static final Log _log = LogFactory.getLog(StrBonusManager.class);
    private static final StrBonusManager INSTANCE = new StrBonusManager();

    /** 紀錄「本管理器上次套在玩家身上的數值」，用於回收避免疊加 */
    private static class Applied {
        int atk; int hit; int critChance; int critPercent; int critFx;
    }
    private final Map<Integer, Applied> appliedMap = new ConcurrentHashMap<>();

    public static StrBonusManager get() { return INSTANCE; }

    /** 對指定玩家重新套用一次（會先回收舊值） */
    public void reapply(L1PcInstance pc) {
        if (pc == null) return;

        // 1) 先回收舊加成
        Applied old = appliedMap.remove(pc.getId());
        if (old != null) {
            safeAddDmgup(pc, -old.atk);
            safeAddHitup(pc, -old.hit);
            PcCritRepo.clear(pc.getId());
        }

        // 2) 查表：依目前力量值取對應設定
        final int totalStr = getTotalStr(pc); // 你若有 getTrueStr()/getTotalStr() 就換掉
        StrSetting setting = StrSettingTable.getInstance().findByStr(totalStr);

        // 3) 套用新加成（有設定才套）
        if (setting != null) {
            Applied neo = new Applied();
            neo.atk = setting.atk;
            neo.hit = setting.hit;
            neo.critChance = setting.critChance;
            neo.critPercent = setting.critPercent;
            neo.critFx = setting.critFx;

            safeAddDmgup(pc, neo.atk);
            safeAddHitup(pc, neo.hit);
            PcCritRepo.set(pc.getId(), neo.critChance, neo.critPercent, neo.critFx);

            appliedMap.put(pc.getId(), neo);
        }

        // 4) 刷新面板（讓玩家 UI 立刻顯示）
        pc.sendPackets(new S_OwnCharStatus2(pc));
    }

    /** 取得「對應查表」的力量值：有 total/true 的話換成你的專案方法 */
// 取查表用的「當前力量」（已含被動/裝備/洗點等最終值）
    private int getTotalStr(com.lineage.server.model.Instance.L1PcInstance pc) {
        return pc.getStr(); // ← 直接用你貼的 getStr()
    }
    // === 依你的專案 API 替換這兩個加成方法 ===
    private void safeAddDmgup(L1PcInstance pc, int v) {
        if (v != 0) { try { pc.addDmgup(v); } catch (Throwable ignore) {} }
    }
    private void safeAddHitup(L1PcInstance pc, int v) {
        if (v != 0) { try { pc.addHitup(v); } catch (Throwable ignore) {} }
    }
}
