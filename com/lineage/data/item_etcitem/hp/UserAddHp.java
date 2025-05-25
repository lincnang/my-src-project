package com.lineage.data.item_etcitem.hp;

import com.lineage.config.ConfigAutoAll;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.serverpackets.ServerBasePacket;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UserAddHp extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(UserAddHp.class);
    private int _min_hp;
    private int _max_addhp;
    private int _gfxid;
    private int _consumeItemId;
    private int _consumeItemCount;

    public static ItemExecutor get() {
        return new UserAddHp();
    }

    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        try {
            if (item == null) {
                return;
            }
            if (pc == null) {
                return;
            }
            // 開啟自動喝藥是否無法手動使用
            if (!ConfigAutoAll.AutoWater) {
                if (pc.isAutoHpAll()) {
                    if (pc.isAutoHp1() && pc.getInventory().checkItem(pc.getAutoItemId1()) || pc.isAutoHp2() && pc.getInventory().checkItem(pc.getAutoItemId2())) {
                        pc.sendPackets(new S_SystemMessage("\\aE開啟自動輔助喝水時無法手動喝水"));
                        return;
                    }
                }
            }
            if (L1BuffUtil.stopPotion(pc)) {
                if (_consumeItemId > 0) {
                    if (!pc.getInventory().consumeItem(_consumeItemId, _consumeItemCount)) {
                        pc.sendPackets(new S_SystemMessage("所需道具不足，無法使用。")); // src036
                        return;
                    }
                } else {
                    pc.getInventory().removeItem(item, 1L);
                }
                L1BuffUtil.cancelAbsoluteBarrier(pc);
                if (this._gfxid > 0) { // src015
                    pc.sendPacketsUserAddHp(new S_SkillSound(pc.getId(), this._gfxid));
                }
                int addhp = _min_hp;
                if (_max_addhp > 0) {
                    addhp += (int) (Math.random() * _max_addhp);
                }
                if (pc.get_up_hp_potion() > 0) {
                    addhp += addhp * pc.get_up_hp_potion() / 100;
                    addhp += pc.get_uhp_number();
                }
                if (pc.hasSkillEffect(173)) { // 污濁之水
                    addhp >>= 1;
                }
                if (pc.hasSkillEffect(230)) { // 亡命之徒
                    addhp >>= 1;
                }
                if (pc.hasSkillEffect(4012)) { // 污濁的水流
                    addhp >>= 1;
                }
                if (pc.hasSkillEffect(4011)) { // 藥水侵蝕術
                    addhp *= -1;
                }
                if (addhp > 0) {
                    pc.sendPackets(new S_ServerMessage(77));
                }
                if (pc.isGm())
                    pc.sendPackets((ServerBasePacket) new S_SystemMessage("GM顯示當下喝水量:" + addhp));
                pc.setCurrentHp(pc.getCurrentHp() + addhp);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void set_set(final String[] set) {
        try {
            _min_hp = Integer.parseInt(set[1]);
            if (_min_hp <= 0) {
                _min_hp = 1;
                _log.error("UserHpr 設置錯誤:最小恢復值小於等於0! 使用預設1");
            }
        } catch (final Exception localException) {
        }
        try {
            final int max_hp = Integer.parseInt(set[2]);
            if (max_hp >= _min_hp) {
                _max_addhp = (max_hp - _min_hp + 1);
            } else {
                _max_addhp = 0;
                _log.error("UserHpr 設置錯誤:最大恢復值小於最小恢復值!(" + _min_hp + " " + max_hp + ")");
            }
        } catch (final Exception localException1) {
        }
        try {
            _gfxid = Integer.parseInt(set[3]);
        } catch (final Exception localException2) {
        }
        try {
            _consumeItemId = Integer.parseInt(set[4]);
            _consumeItemCount = Integer.parseInt(set[5]);
        } catch (final Exception localException3) {
        }
    }
}
