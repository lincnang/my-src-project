package com.lineage.server.model.doll;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.ability.S_WeightStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Doll_Weight extends L1DollExecutor {
    private static final Log _log = LogFactory.getLog(Doll_Weight.class);
    private int _int1;
    private int _int2;
    private String _note;

    public static L1DollExecutor get() {
        return new Doll_Weight();
    }

    public void set_power(int int1, int int2, int int3) {
        try {
            _int1 = int1;
            _int2 = int2;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public String get_note() {
        return _note;
    }

    public void set_note(String note) {
        _note = note;
    }

    public void setDoll(L1PcInstance pc) {
        try {
            if (_int1 != 0) {
                pc.addWeightReduction(_int1);
            }
            if (_int2 != 0) {
                pc.add_weightUP(_int2);
            }
            //pc.sendPackets(new S_PacketBox(10, pc.getInventory().getWeight240()));
            // 7.6
            pc.sendPackets(new S_WeightStatus(pc.getInventory().getWeight() * 100 / (int) pc.getMaxWeight(), pc.getInventory().getWeight(), (int) pc.getMaxWeight()));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void removeDoll(L1PcInstance pc) {
        try {
            if (_int1 != 0) {
                pc.addWeightReduction(-_int1);
            }
            if (_int2 != 0) {
                pc.add_weightUP(-_int2);
            }
            //pc.sendPackets(new S_PacketBox(10, pc.getInventory().getWeight240()));
            // 7.6
            pc.sendPackets(new S_WeightStatus(pc.getInventory().getWeight() * 100 / (int) pc.getMaxWeight(), pc.getInventory().getWeight(), (int) pc.getMaxWeight()));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public boolean is_reset() {
        return false;
    }
}
