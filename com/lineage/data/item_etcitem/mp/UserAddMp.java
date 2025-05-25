package com.lineage.data.item_etcitem.mp;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UserAddMp extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(UserAddMp.class);
    private int _min_mp;
    private int _max_addmp;
    private int _gfxid;
    private int _consumeItemId;
    private int _consumeItemCount;

    public static ItemExecutor get() {
        return new UserAddMp();
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
                if (_consumeItemId > 0) {
                    if (!pc.getInventory().consumeItem(_consumeItemId, _consumeItemCount)) {
                        pc.sendPackets(new S_SystemMessage("所需道具不足"));
                        return;  //src038
                    }
                } else {
                    pc.getInventory().removeItem(item, 1L);
                }
                L1BuffUtil.cancelAbsoluteBarrier(pc);
                if (_gfxid > 0) {
                    pc.sendPacketsX8(new S_SkillSound(pc.getId(), _gfxid));
                }
                int addmp = _min_mp;
                if (_max_addmp > 0) {
                    addmp += (int) (Math.random() * _max_addmp);
                }
                if (addmp > 0) {
                    pc.sendPackets(new S_ServerMessage(338, "$1084"));
                }
                pc.setCurrentMp(pc.getCurrentMp() + addmp);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void set_set(String[] set) {
        try {
            _min_mp = Integer.parseInt(set[1]);
            if (_min_mp <= 0) {
                _min_mp = 1;
                _log.error("UserMpr 設置錯誤:最小恢復值小於等於0! 使用預設1");
            }
        } catch (Exception localException) {
        }
        try {
            int max_hp = Integer.parseInt(set[2]);
            if (max_hp >= _min_mp) {
                _max_addmp = (max_hp - _min_mp + 1);
            } else {
                _max_addmp = 0;
                _log.error("UserMpr 設置錯誤:最大恢復值小於最小恢復值!(" + _min_mp + " " + max_hp + ")");
            }
        } catch (Exception localException1) {
        }
        try {
            _gfxid = Integer.parseInt(set[3]);
        } catch (Exception localException2) {
        }
        try {
            _consumeItemId = Integer.parseInt(set[4]);
            _consumeItemCount = Integer.parseInt(set[5]);
        } catch (Exception localException3) {
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.mp.UserAddMp JD-Core Version: 0.6.2
 */