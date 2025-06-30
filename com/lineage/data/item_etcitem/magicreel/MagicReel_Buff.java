package com.lineage.data.item_etcitem.magicreel;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

import java.util.Random;

public class MagicReel_Buff extends ItemExecutor {
    private final Random _random = new Random();
    private int _skillid = 0;
    private int _consume = 1;

    public static ItemExecutor get() {
        return new MagicReel_Buff();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (pc == null) {
            return;
        }
        if (item == null) {
            return;
        }
        int targetID = data[0];
        if (targetID == 0) {
            pc.sendPackets(new S_ServerMessage(281));
            return;
        }
        L1Object target = World.get().findObject(targetID);
        int spellsc_x = target.getX();
        int spellsc_y = target.getY();
        pc.getInventory().removeItem(item, _consume);
        L1BuffUtil.cancelAbsoluteBarrier(pc);
        if (_random.nextInt(100) < 80) {
            L1SkillUse l1skilluse = new L1SkillUse();
            l1skilluse.handleCommands(pc, _skillid, targetID, spellsc_x, spellsc_y, 0, 2);
        } else {
            // 280：\f1施咒失敗。
            pc.sendPackets(new S_ServerMessage(280));
        }
    }

    public void set_set(String[] set) {
        try {
            _skillid = Integer.parseInt(set[1]);
        } catch (Exception localException) {
        }
        try {
            _consume = Integer.parseInt(set[2]);
        } catch (Exception localException1) {
        }
    }
}
