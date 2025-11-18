package com.lineage.data.item_etcitem.hp;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_YouFeelBetter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UserAddHpPermanent extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(UserAddHpPermanent.class);
    private int _min_hp;
    private int _max_addhp;
    private int _gfxid;

    public static ItemExecutor get() {
        return new UserAddHpPermanent();
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
                L1BuffUtil.cancelAbsoluteBarrier(pc);
                if (this._gfxid > 0) {
                    pc.sendPacketsAll(new S_SkillSound(pc.getId(), this._gfxid));
                }
                int addhp = this._min_hp;
                if (this._max_addhp > 0) {
                    addhp += (int) (Math.random() * this._max_addhp);
                }
                if (pc.get_up_hp_potion() > 0) {
                    addhp += addhp * pc.get_up_hp_potion() / 100;
                    addhp += pc.get_uhp_number();
                }
                if (pc.hasSkillEffect(173)) {
                    addhp >>= 1;
                }
                if (pc.hasSkillEffect(230)) {
                    addhp >>= 1;
                }
                if (pc.hasSkillEffect(4012)) {
                    addhp >>= 1;
                }
                if (pc.hasSkillEffect(4011)) {
                    addhp *= -1;
                }
                if ((addhp > 0) //&&
                    /*(ConfigOther.Show_Hp_Msg)*/) {
                    pc.sendPackets(new S_YouFeelBetter());
                }
                pc.setCurrentHp(pc.getCurrentHp() + addhp);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void set_set(String[] set) {
        try {
            this._min_hp = Integer.parseInt(set[1]);
            if (this._min_hp <= 0) {
                this._min_hp = 1;
                _log.error("UserAddHpPermanent 設置錯誤:最小恢復值小於等於0! 使用預設1");
            }
        } catch (Exception localException) {
        }
        try {
            int max_hp = Integer.parseInt(set[2]);
            if (max_hp >= this._min_hp) {
                this._max_addhp = (max_hp - this._min_hp + 1);
            } else {
                this._max_addhp = 0;
                _log.error("UserAddHpPermanent 設置錯誤:最大恢復值小於最小恢復值!(" + this._min_hp + " " + max_hp + ")");
            }
        } catch (Exception localException1) {
        }
        try {
            this._gfxid = Integer.parseInt(set[3]);
        } catch (Exception localException2) {
        }
    }
}