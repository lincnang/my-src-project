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
 * 力量加成管理器（PVP/PVE 分開）：
 * - 針對玩家目前「總力量值」：回收舊加成 → 套用新加成 → 刷新面板
 * - 何時呼叫：登入完成、裝備穿脫後、洗點/BUFF/被動造成力量變動後、GM熱重載後
 */
public class StrBonusManager {
    private static final Log _log = LogFactory.getLog(StrBonusManager.class);
    private static final StrBonusManager INSTANCE = new StrBonusManager();

    /** 紀錄「本管理器上次套在玩家身上的數值」，用於回收避免疊加 */
    public static class Applied {
        // PVP 加成
        public int pvpAtk; public int pvpHit;
        // PVE 加成
        public int pveAtk; public int pveHit;
        // 爆擊相關（PVP/PVE 共用）
        public int critChance; public int critPercent; public int critFx;
    }
    private final Map<Integer, Applied> appliedMap = new ConcurrentHashMap<>();

    public static StrBonusManager get() { return INSTANCE; }

    /** 清除指定玩家的加成記錄（通常在登出時調用） */
    public void clear(L1PcInstance pc) {
        if (pc == null) return;
        int key = System.identityHashCode(pc);
        appliedMap.remove(key);
        PcCritRepo.clear(pc.getId());
    }

    /** 對指定玩家重新套用一次（會先回收舊值） */
    public void reapply(L1PcInstance pc) {
        if (pc == null) return;

        synchronized (pc) {
            int key = System.identityHashCode(pc);

            // 1) 先回收舊加成記錄
            Applied old = appliedMap.remove(key);
            if (old != null) {
                PcCritRepo.clear(pc.getId());
            }

            // 2) 查表：依目前力量值取對應設定
            final int totalStr = getTotalStr(pc);
            StrSetting setting = StrSettingTable.getInstance().findByStr(totalStr);

            // 3) 套用新加成（有設定才套）
            if (setting != null) {
                Applied neo = new Applied();
                neo.pvpAtk = setting.pvpAtk;
                neo.pvpHit = setting.pvpHit;
                neo.pveAtk = setting.pveAtk;
                neo.pveHit = setting.pveHit;
                neo.critChance = setting.critChance;
                neo.critPercent = setting.critPercent;
                neo.critFx = setting.critFx;

                PcCritRepo.set(pc.getId(), neo.critChance, neo.critPercent, neo.critFx);
                appliedMap.put(key, neo);
            }

            // 4) 刷新面板（讓玩家 UI 立刻顯示）
            pc.sendPackets(new S_OwnCharStatus2(pc));
        }
    }

    /** 取得「對應查表」的力量值 */
    private int getTotalStr(L1PcInstance pc) {
        return pc.getStr();
    }

    // ==================== PVP 取得方法 ====================

    /** 取得目前玩家的 PVP 力量加成攻擊值 */
    public static int getPvpAtk(L1PcInstance pc) {
        if (pc == null) return 0;
        int key = System.identityHashCode(pc);
        Applied applied = INSTANCE.appliedMap.get(key);
        return (applied != null) ? applied.pvpAtk : 0;
    }

    /** 取得目前玩家的 PVP 力量加成命中值 */
    public static int getPvpHit(L1PcInstance pc) {
        if (pc == null) return 0;
        int key = System.identityHashCode(pc);
        Applied applied = INSTANCE.appliedMap.get(key);
        return (applied != null) ? applied.pvpHit : 0;
    }

    // ==================== PVE 取得方法 ====================

    /** 取得目前玩家的 PVE 力量加成攻擊值 */
    public static int getPveAtk(L1PcInstance pc) {
        if (pc == null) return 0;
        int key = System.identityHashCode(pc);
        Applied applied = INSTANCE.appliedMap.get(key);
        return (applied != null) ? applied.pveAtk : 0;
    }

    /** 取得目前玩家的 PVE 力量加成命中值 */
    public static int getPveHit(L1PcInstance pc) {
        if (pc == null) return 0;
        int key = System.identityHashCode(pc);
        Applied applied = INSTANCE.appliedMap.get(key);
        return (applied != null) ? applied.pveHit : 0;
    }

    // ==================== 完整資料取得 ====================

    /** 取得目前玩家的力量加成完整資料 */
    public static Applied getApplied(L1PcInstance pc) {
        if (pc == null) return null;
        int key = System.identityHashCode(pc);
        return INSTANCE.appliedMap.get(key);
    }

    // ==================== 舊版相容方法（保留以避免編譯錯誤） ====================

    /** @deprecated 使用 getPvpAtk() 代替 */
    @Deprecated
    public static int getStrAtk(L1PcInstance pc) {
        return getPvpAtk(pc);
    }

    /** @deprecated 使用 getPvpHit() 代替 */
    @Deprecated
    public static int getStrHit(L1PcInstance pc) {
        return getPvpHit(pc);
    }
}
