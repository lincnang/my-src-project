package com.lineage.server.Controller;

import com.lineage.server.datatables.WisSettingTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_OwnCharStatus2;
import com.lineage.server.templates.WisSetting;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 精神加成管理器：依《系統_強化精神設置》回收/套用 抗魔/回魔
 */
public class WisBonusManager {
    private static final Log _log = LogFactory.getLog(WisBonusManager.class);
    private static final WisBonusManager INSTANCE = new WisBonusManager();

    private static class Applied { int mr; int mpr; }
    private final Map<Integer, Applied> appliedMap = new ConcurrentHashMap<>();

    public static WisBonusManager get() { return INSTANCE; }

    public void clear(L1PcInstance pc) {
        if (pc == null) return;
        appliedMap.remove(System.identityHashCode(pc));
    }

    public void reapply(L1PcInstance pc) {
        if (pc == null) return;
        int key = System.identityHashCode(pc);
        Applied old = appliedMap.remove(key);
        if (old != null) {
            safeAddMr(pc, -old.mr);
            safeAddMpr(pc, -old.mpr);
        }

        final int totalWis = getTotalWis(pc);
        WisSetting setting = WisSettingTable.getInstance().findByWis(totalWis);
        if (setting != null) {
            Applied neo = new Applied();
            neo.mr = setting.mr;
            neo.mpr = setting.mpr;
            safeAddMr(pc, neo.mr);
            safeAddMpr(pc, neo.mpr);
            appliedMap.put(key, neo);
        }

        pc.sendPackets(new S_OwnCharStatus2(pc));
    }

    private int getTotalWis(L1PcInstance pc) { return pc.getWis(); }

    private void safeAddMr(L1PcInstance pc, int v) { if (v != 0) { try { pc.addMr(v); } catch (Throwable ignore) {} } }
    private void safeAddMpr(L1PcInstance pc, int v) { if (v != 0) { try { pc.addMpr(v); } catch (Throwable ignore) {} } }
}




