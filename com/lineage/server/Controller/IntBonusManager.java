package com.lineage.server.Controller;

import com.lineage.server.datatables.IntSettingTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_OwnCharStatus2;
import com.lineage.server.templates.IntSetting;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 智力加成管理器（不入侵 L1PcInstance 結構）：
 * - 針對玩家目前「總智力值」：回收舊加成 → 套用新加成 → 刷新面板
 * - 何時呼叫：登入完成、裝備穿脫後、洗點/BUFF/被動造成智力變動後、GM熱重載後
 */
public class IntBonusManager {
    private static final Log _log = LogFactory.getLog(IntBonusManager.class);
    private static final IntBonusManager INSTANCE = new IntBonusManager();

    /** 紀錄「本管理器上次套在玩家身上的數值」，用於回收避免疊加 */
    private static class Applied {
        int magicAttack; 
        int magicHit; 
        int magicPenetration; 
        int ignoreMagicDefense; 
        int magicCritFx;
    }
    private final Map<Integer, Applied> appliedMap = new ConcurrentHashMap<>();

    public static IntBonusManager get() { return INSTANCE; }

    /** 對指定玩家重新套用一次（會先回收舊值） */
    public void reapply(L1PcInstance pc) {
        if (pc == null) return;

        // 1) 先回收舊加成
        Applied old = appliedMap.remove(pc.getId());
        if (old != null) {
            safeAddMagicDmgup(pc, -old.magicAttack);
            safeAddMagicHitup(pc, -old.magicHit);
            // 魔法穿透率和忽略魔防在攻擊時動態計算，不需要回收
        }

        // 2) 查表：依目前智力值取對應設定
        final int totalInt = getTotalInt(pc);
        IntSetting setting = IntSettingTable.getInstance().findByInt(totalInt);

        // 3) 套用新加成（有設定才套）
        if (setting != null) {
            Applied neo = new Applied();
            neo.magicAttack = setting.magicAttack;
            neo.magicHit = setting.magicHit;
            neo.magicPenetration = setting.magicPenetration;
            neo.ignoreMagicDefense = setting.ignoreMagicDefense;
            neo.magicCritFx = setting.magicCritFx;

            safeAddMagicDmgup(pc, neo.magicAttack);
            safeAddMagicHitup(pc, neo.magicHit);
            // 魔法穿透率和忽略魔防存儲到applied記錄中，供攻擊時查詢

            appliedMap.put(pc.getId(), neo);

            if (_log.isDebugEnabled()) {
                _log.debug("智力加成套用 - 角色:" + pc.getName() + 
                          ", 智力:" + totalInt + 
                          ", 魔攻加成:" + neo.magicAttack + 
                          ", 魔法命中:" + neo.magicHit + 
                          ", 穿透率:" + neo.magicPenetration + "%" + 
                          ", 忽略魔防:" + neo.ignoreMagicDefense);
            }
        }

        // 4) 刷新面板（讓玩家 UI 立刻顯示）
        pc.sendPackets(new S_OwnCharStatus2(pc));
    }

    /** 取得「對應查表」的智力值：有 total/true 的話換成你的專案方法 */
    private int getTotalInt(L1PcInstance pc) {
        return pc.getInt(); // 直接用現有的 getInt() 方法
    }

    /** 安全地添加魔攻加成 */
    private void safeAddMagicDmgup(L1PcInstance pc, int v) {
        if (v != 0) { 
            try { 
                // 使用現有的魔攻加成方法，如果沒有可以添加到現有的dmgup中
                pc.addDmgup(v); 
            } catch (Throwable ignore) {} 
        }
    }

    /** 安全地添加魔法命中加成 */
    private void safeAddMagicHitup(L1PcInstance pc, int v) {
        if (v != 0) { 
            try { 
                // 使用現有的命中加成方法
                pc.addHitup(v); 
            } catch (Throwable ignore) {} 
        }
    }

    /** 獲取玩家當前的魔法穿透率 */
    public int getMagicPenetration(L1PcInstance pc) {
        if (pc == null) return 0;
        
        Applied applied = appliedMap.get(pc.getId());
        if (applied != null) {
            return applied.magicPenetration;
        }
        
        // 如果沒有applied記錄，直接查表
        IntSetting setting = IntSettingTable.getInstance().findByInt(pc.getInt());
        return setting != null ? setting.magicPenetration : 0;
    }

    /** 獲取玩家當前的忽略魔防 */
    public int getIgnoreMagicDefense(L1PcInstance pc) {
        if (pc == null) return 0;
        
        Applied applied = appliedMap.get(pc.getId());
        if (applied != null) {
            return applied.ignoreMagicDefense;
        }
        
        // 如果沒有applied記錄，直接查表
        IntSetting setting = IntSettingTable.getInstance().findByInt(pc.getInt());
        return setting != null ? setting.ignoreMagicDefense : 0;
    }

    /** 獲取玩家當前的魔法爆擊特效 */
    public int getMagicCritFx(L1PcInstance pc) {
        if (pc == null) return 0;
        
        Applied applied = appliedMap.get(pc.getId());
        if (applied != null) {
            return applied.magicCritFx;
        }
        
        // 如果沒有applied記錄，直接查表
        IntSetting setting = IntSettingTable.getInstance().findByInt(pc.getInt());
        return setting != null ? setting.magicCritFx : 0;
    }

    /** 清理玩家數據（玩家離線時調用） */
    public void clearPlayer(int playerId) {
        appliedMap.remove(playerId);
    }

    /** 獲取當前管理的玩家數量 */
    public int getManagedPlayerCount() {
        return appliedMap.size();
    }
}
