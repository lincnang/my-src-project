package com.lineage.data.item_etcitem.brave;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillBrave;
import com.lineage.server.serverpackets.S_SkillSound;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StatusRibrave extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(StatusRibrave.class);
    private int _time = 480;
    private int _gfxid = 0;
    private boolean _isCrown;
    private boolean _isKnight;
    private boolean _isElf;
    private boolean _isWizard;
    private boolean _isDarkelf;
    private boolean _isDragonKnight;
    private boolean _isIllusionist;
    private boolean _isWarrior;

    public static ItemExecutor get() {
        return new StatusRibrave();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (item == null) {
            return;
        }
        if (pc == null) {
            return;
        }
        if (pc.getBraveSpeed() > 0) {
            // 1,413：目前情況是無法使用。
            pc.sendPackets(new S_ServerMessage(1413));
            return;
        }
        if (L1BuffUtil.stopPotion(pc)) {
            if (check(pc)) {
                pc.getInventory().removeItem(item, 1L);
                L1BuffUtil.cancelAbsoluteBarrier(pc);
                L1BuffUtil.braveStart(pc);
                if (_gfxid > 0) {
                    pc.sendPacketsX8(new S_SkillSound(pc.getId(), _gfxid));
                }
                pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 4, 0));
                pc.setSkillEffect(1017, _time * 1000);
                pc.setBraveSpeed(4);
            } else {
                pc.sendPackets(new S_ServerMessage(79));
            }
        }
    }

    private boolean check(L1PcInstance pc) {
        try {
            if ((pc.isCrown()) && (_isCrown)) {
                return true;
            }
            if ((pc.isKnight()) && (_isKnight)) {
                return true;
            }
            if ((pc.isElf()) && (_isElf)) {
                return true;
            }
            if ((pc.isWizard()) && (_isWizard)) {
                return true;
            }
            if ((pc.isDarkelf()) && (_isDarkelf)) {
                return true;
            }
            if ((pc.isDragonKnight()) && (_isDragonKnight)) {
                return true;
            }
            if ((pc.isIllusionist()) && (_isIllusionist)) {
                return true;
            }
            if ((pc.isWarrior()) && (_isWarrior)) {
                return true;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    private void set_use_type(int use_type) {
        try {
            if (use_type >= 128) {
                use_type -= 128;
                _isWarrior = true;
            }
            if (use_type >= 64) {
                use_type -= 64;
                _isIllusionist = true;
            }
            if (use_type >= 32) {
                use_type -= 32;
                _isDragonKnight = true;
            }
            if (use_type >= 16) {
                use_type -= 16;
                _isDarkelf = true;
            }
            if (use_type >= 8) {
                use_type -= 8;
                _isWizard = true;
            }
            if (use_type >= 4) {
                use_type -= 4;
                _isElf = true;
            }
            if (use_type >= 2) {
                use_type -= 2;
                _isKnight = true;
            }
            if (use_type >= 1) {
                use_type--;
                _isCrown = true;
            }
            if (use_type > 0) {
                _log.error("StatusRibrave 可執行職業設定錯誤:餘數大於0");
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void set_set(String[] set) {
        try {
            _time = Integer.parseInt(set[1]);
            if (_time <= 0) {
                _log.error("StatusRibrave 設置錯誤:技能效果時間小於等於0! 使用預設480秒");
                _time = 480;
            }
        } catch (Exception localException) {
        }
        try {
            _gfxid = Integer.parseInt(set[2]);
        } catch (Exception localException1) {
        }
        try {
            int user_type = Integer.parseInt(set[3]);
            set_use_type(user_type);
        } catch (Exception localException2) {
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.brave.StatusRibrave JD-Core Version: 0.6.2
 */