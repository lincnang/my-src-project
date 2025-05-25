package com.lineage.data.item_etcitem.wand;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Remove_Objects_Blink extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(Remove_Objects_Blink.class);

    public static ItemExecutor get() {
        return new Remove_Objects_Blink();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        try {
            if (!pc.isGm()) {
                pc.sendPackets(new S_SystemMessage("你不是管理者！刪除非法道具！"));
                pc.getInventory().removeItem(item, 1L);
                return;
            }
            int targObjId = data[0];
            L1Object target = World.get().findObject(targObjId);
            if ((target instanceof L1MonsterInstance)) {
                L1MonsterInstance mob = (L1MonsterInstance) target;
                if (!mob.isDead()) {
                    mob.setDead(true);
                    mob.setStatus(8);
                    mob.setCurrentHpDirect(0);
                    mob.deleteMe();
                    pc.sendPackets(new S_SystemMessage(mob.getName() + "已被您刪除了。"));
                }
            } else if ((target instanceof L1NpcInstance)) {
                L1NpcInstance mob = (L1NpcInstance) target;
                mob.deleteMe();
                pc.sendPackets(new S_SystemMessage(mob.getName() + "已被您刪除了。"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}