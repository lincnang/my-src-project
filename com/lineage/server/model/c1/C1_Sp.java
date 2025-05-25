package com.lineage.server.model.c1;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SPMR;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 陣營階級能力:<BR>
 * 傷害減免相關<BR>
 * 傷害減免提高 參數：INT1(傷害減免質+)<BR>
 *
 * @author dexc
 */
public class C1_Sp extends C1Executor {
    private static final Log _log = LogFactory.getLog(C1_Sp.class);
    private int _int1;// 值1

    /**
     * 效果:傷害減免
     */
    public C1_Sp() {
    }

    public static C1Executor get() {
        return new C1_Sp();
    }

    @Override
    public void set_power(int int1, int int2, int int3) {
        try {
            _int1 = int1;
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void set_c1(L1PcInstance pc) {
        try {
            pc.addSp(_int1);
            pc.sendPackets(new S_SPMR(pc));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void remove_c1(L1PcInstance pc) {
        try {
            pc.addSp(-_int1);
            pc.sendPackets(new S_SPMR(pc));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
