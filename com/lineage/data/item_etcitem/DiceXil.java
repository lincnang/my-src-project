package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SkillSound;

public class DiceXil extends ItemExecutor {
    public static ItemExecutor get() {
        return new DiceXil();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int gfxid = 0;
        switch (item.getEnchantLevel()) {
            case 1:
                gfxid = 3204;
                break;
            case 2:
                gfxid = 3205;
                break;
            case 3:
                gfxid = 3206;
                break;
            case 4:
                gfxid = 3207;
                break;
            case 5:
                gfxid = 3208;
                break;
            case 6:
                gfxid = 3209;
        }
        if (gfxid != 0) {
            pc.sendPacketsAll(new S_SkillSound(pc.getId(), gfxid));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.DiceXil JD-Core Version: 0.6.2
 */