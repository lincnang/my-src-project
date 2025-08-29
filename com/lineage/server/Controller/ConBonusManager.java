package com.lineage.server.Controller;

import com.lineage.server.datatables.ConSettingTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_OwnCharStatus2;
import com.lineage.server.templates.ConSetting;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 體質加成管理器：依《系統_強化體質設置》回收/套用 減傷/PVP/抗魔/魔攻(SP)
 */
public class ConBonusManager {
    private static final Log _log = LogFactory.getLog(ConBonusManager.class);
    private static final ConBonusManager INSTANCE = new ConBonusManager();

    private static class Applied { int dr; int pvpDmg; int pvpRed; int mr; int matk; }
    private final Map<Integer, Applied> appliedMap = new ConcurrentHashMap<>();

    public static ConBonusManager get() { return INSTANCE; }

    public void reapply(L1PcInstance pc) {
        if (pc == null) return;
        Applied old = appliedMap.remove(pc.getId());
        if (old != null) {
            safeAddDmgR(pc, -old.dr);
            safeSetPvpDmg(pc, -old.pvpDmg);
            safeSetPvpRed(pc, -old.pvpRed);
            safeAddMr(pc, -old.mr);
            safeAddMatk(pc, -old.matk);
        }

        final int totalCon = getTotalCon(pc);
        ConSetting setting = ConSettingTable.getInstance().findByCon(totalCon);
        if (setting != null) {
            Applied neo = new Applied();
            neo.dr = setting.dmgReduction;
            neo.pvpDmg = setting.pvpDamage;
            neo.pvpRed = setting.pvpReduction;
            neo.mr = setting.mr;
            neo.matk = setting.magicAttack;

            safeAddDmgR(pc, neo.dr);
            safeSetPvpDmg(pc, neo.pvpDmg);
            safeSetPvpRed(pc, neo.pvpRed);
            safeAddMr(pc, neo.mr);
            safeAddMatk(pc, neo.matk);

            appliedMap.put(pc.getId(), neo);
        }

        pc.sendPackets(new S_OwnCharStatus2(pc));
    }

    private int getTotalCon(L1PcInstance pc) { return pc.getCon(); }

    private void safeAddDmgR(L1PcInstance pc, int v) { if (v != 0) { try { pc.addDmgR(v); } catch (Throwable ignore) {} } }
    private void safeSetPvpDmg(L1PcInstance pc, int v) { if (v != 0) { try { pc.setPvpDmg(v); } catch (Throwable ignore) {} } }
    private void safeSetPvpRed(L1PcInstance pc, int v) { if (v != 0) { try { pc.setPvpDmg_R(v); } catch (Throwable ignore) {} } }
    private void safeAddMr(L1PcInstance pc, int v) { if (v != 0) { try { pc.addMr(v); } catch (Throwable ignore) {} } }
    // 體質表中的「魔攻」對應 Lineage 的 SP（法術攻擊力）
    private void safeAddMatk(L1PcInstance pc, int v) { if (v != 0) { try { pc.addSp(v); } catch (Throwable ignore) {} } }
}


