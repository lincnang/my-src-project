package com.lineage.server.model.c1;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_MPUpdate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class C1_HpMp extends C1Executor {
    private static final Log _log = LogFactory.getLog(C1_HpMp.class);
    private int _int1;
    private int _int2;

    public static C1Executor get() {
        return new C1_HpMp();
    }

    public void set_power(int int1, int int2, int int3) {
        try {
            _int1 = int1;
            _int2 = int2;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void set_c1(L1PcInstance pc) {
        try {
            pc.addMaxHp(_int1);
            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            pc.addMaxMp(_int2);
            pc.sendPackets(new S_MPUpdate(pc));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void remove_c1(L1PcInstance pc) {
        try {
            pc.addMaxHp(-_int1);
            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            pc.addMaxMp(-_int2);
            pc.sendPackets(new S_MPUpdate(pc));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.c1.C1_HpMp JD-Core Version: 0.6.2
 */