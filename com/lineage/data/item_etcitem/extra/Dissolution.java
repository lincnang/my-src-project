package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ResolventEXTable;
import com.lineage.server.datatables.ResolventTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;

public class Dissolution extends ItemExecutor {

    public static ItemExecutor get() {
        return new Dissolution();
    }

    @Override
    public void execute(int[] data, L1PcInstance pc, L1ItemInstance resolvent) {
        if (pc == null || resolvent == null || data == null || data.length == 0) {
            return;
        }
        int targetObjId = data[0];
        L1ItemInstance target = pc.getInventory().getItem(targetObjId);
        if (target == null) {
            pc.sendPackets(new S_ServerMessage(79)); // 沒有任何事情發生。
            return;
        }
        useResolvent(pc, target, resolvent);
    }

    /**
     * 使用溶解劑分解目標道具
     * 流程：
     *  1) 先走 ResolventEXTable：可檢查武器/防具最低強化門檻，並直接發獎勵道具
     *  2) 不成立再回退 ResolventTable：沿用舊邏輯(武器/防具需 +0)，僅移除來源道具
     */
    private void useResolvent(L1PcInstance pc, L1ItemInstance target, L1ItemInstance resolvent) {
        if (pc == null || target == null || resolvent == null) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }

        // 已穿戴不可分解
        if (target.isEquipped()) {
            pc.sendPackets(new S_SystemMessage("請先把裝備脫掉再分解。"));
            return;
        }

        // 未鑑定不可分解（你要允許未鑑定就拿掉這段）
        if (!target.isIdentified()) {
            pc.sendPackets(new S_SystemMessage("此物品未鑑定，請先鑑定後再分解。"));
            return;
        }

        final int itemId = target.getItemId();
        final boolean hasExRule = ResolventEXTable.get().hasRule(itemId);

        if (hasExRule) {
            // ★EX 有規則：只走 EX，不得回退舊表
            boolean ok = ResolventEXTable.get().getCrystalCount(pc, target); // 傳「實例」才能檢查 +7
            if (!ok) {
                // 可能是未達最低強化值 +7、或背包不足
                pc.sendPackets(new S_ServerMessage(1161)); // 無法溶解。
                return;
            }
            // EX 成功 -> 回收來源與溶解劑
            pc.getInventory().removeItem(target, 1L);
            pc.getInventory().removeItem(resolvent, 1L);
            pc.sendPackets(new S_SystemMessage("分解完成，獎勵已發送至背包。"));
            return;
        }

        // ===== 無 EX 規則：才回退舊表 =====
        long crystalCount = ResolventTable.get().getCrystalCount(itemId);
        if (crystalCount == 0L) {
            pc.sendPackets(new S_ServerMessage(1161));
            return;
        }

        // 舊表：若你原本規定武器/防具必須 +0，再保留這段
        int type2 = target.getItem().getType2(); // 1武器 2防具 0其他
        if ((type2 == 1 || type2 == 2) && target.getEnchantLevel() != 0) {
            pc.sendPackets(new S_ServerMessage(1161));
            return;
        }

        // 舊表：維持你原行為（只移除來源與溶解劑；如需發獎勵，另行補上）
        pc.getInventory().removeItem(target, 1L);
        pc.getInventory().removeItem(resolvent, 1L);
        pc.sendPackets(new S_SystemMessage("分解完成。"));
    }

}
