package com.lineage.data.item_etcitem.event;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_InventoryIcon;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

public class SExp500 extends ItemExecutor {
    private int _Time;

    public static ItemExecutor get() {
        return new SExp500();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (item == null) {
            return;
        }
        if (pc == null) {
            return;
        }
        if (L1BuffUtil.cancelExpSkill_2(pc)) {
            if (pc.getInventory().removeItem(item, 1L) == 1L) {
                pc.setSkillEffect(5005, _Time * 1000);
                pc.sendPackets(new S_ServerMessage("第二段 經驗值提升500%(" + _Time + "秒)"));
                pc.sendPackets(new S_SkillSound(pc.getId(), 750));
                pc.sendPackets(new S_InventoryIcon(3069, true, 1292, _Time)); // 2565(一階段經驗料理圖示編號)
            }
        }
    }

    @Override
    public void set_set(String[] set) {
        try {
            _Time = Integer.parseInt(set[1]);
        } catch (Exception e) {
        }
    }
}