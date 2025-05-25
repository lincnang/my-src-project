package com.lineage.server.model.c1;

import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class C1_DownDmg extends C1Executor {
    private static final Log _log = LogFactory.getLog(C1_DownDmg.class);
    private int _int1;

    public static C1Executor get() {
        return new C1_DownDmg();
    }

    public void set_power(int int1, int int2, int int3) {
        try {
            _int1 = int1;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void set_c1(L1PcInstance pc) {
        try {
            pc.addDamageReductionByArmor(_int1);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void remove_c1(L1PcInstance pc) {
        try {
            pc.addDamageReductionByArmor(-_int1);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.c1.C1_DownDmg JD-Core Version: 0.6.2
 */