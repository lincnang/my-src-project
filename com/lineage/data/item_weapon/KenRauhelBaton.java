package com.lineage.data.item_weapon;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class KenRauhelBaton extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(KenRauhelBaton.class);

    public static ItemExecutor get() {
        return new KenRauhelBaton();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        try {
            switch (data[0]) {
                case 0:
                    if (pc.hasSkillEffect(4008)) {
                        pc.removeSkillEffect(4008);
                    }
                    break;
                case 1:
                    pc.setSkillEffect(4008, -1);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_weapon.KenRauhelBaton JD-Core Version: 0.6.2
 */