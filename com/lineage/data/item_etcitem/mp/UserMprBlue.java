package com.lineage.data.item_etcitem.mp;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_SkillSound;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UserMprBlue extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(UserMprBlue.class);
    private int _time;
    private int _gfxid;

    public static ItemExecutor get() {
        return new UserMprBlue();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        try {
            if (item == null) {
                return;
            }
            if (pc == null) {
                return;
            }
            if (L1BuffUtil.stopPotion(pc)) {
                pc.getInventory().removeItem(item, 1L);
                if (pc.hasSkillEffect(1002)) {
                    pc.killSkillEffectTimer(1002);
                }
                L1BuffUtil.cancelAbsoluteBarrier(pc);
                if (_gfxid > 0) {
                    pc.sendPacketsX8(new S_SkillSound(pc.getId(), _gfxid));
                }
                //pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_BLUEPOTION, _time));
                // pc.sendPackets(new S_PacketBox(32, _time));
                pc.setSkillEffect(1002, _time * 1000);
                //pc.sendPackets(new S_ServerMessage(1007));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void set_set(String[] set) {
        try {
            _time = Integer.parseInt(set[1]);
            if (_time <= 0) {
                _time = 600;
                _log.error("UserMpr 設置錯誤:時效小於等於0! 使用預設600");
            }
        } catch (Exception localException) {
        }
        try {
            _gfxid = Integer.parseInt(set[2]);
        } catch (Exception localException1) {
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.mp.UserMprBlue JD-Core Version: 0.6.2
 */