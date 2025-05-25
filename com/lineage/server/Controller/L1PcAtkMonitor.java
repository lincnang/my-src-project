package com.lineage.server.Controller;

import com.lineage.echo.OpcodesClient;
import com.lineage.server.clientpackets.AcceleratorChecker.ACT_TYPE;
import com.lineage.server.clientpackets.C_Attack;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.monitor.L1PcMonitor;
import com.lineage.server.utils.BinaryOutputStream;
import com.lineage.server.world.World;

import java.util.concurrent.TimeUnit;

/**
 * l1j-tw連續攻擊
 */
public class L1PcAtkMonitor extends L1PcMonitor {
    public L1PcAtkMonitor(final int oId) {
        super(oId);
    }

    @Override
    public void execTask(final L1PcInstance pc) {
        try {
            while (pc.getAttackTargetId() != 0) {
                final L1Object obj = World.get().findObject(pc.getAttackTargetId());
                if (!(obj instanceof L1Character)) {
                    return;
                }
                final L1Character target = (L1Character) World.get().findObject(pc.getAttackTargetId());
                if (target == null || pc.isDead()) {
                    return;
                }
                final BinaryOutputStream os = new BinaryOutputStream();
                os.writeC(OpcodesClient.C_ATTACK);
                os.writeD(pc.getAttackTargetId());
                os.writeH(target.getX());
                os.writeH(target.getY());
                new C_Attack().start(os.getBytes(), pc.getNetConnection());
                os.close();
                final int interval = pc.speed_Attack().getRightInterval(ACT_TYPE.ATTACK);
                // System.out.println(interval);
                TimeUnit.MILLISECONDS.sleep(interval);
            }
        } catch (final Exception e) {
            // log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }
}