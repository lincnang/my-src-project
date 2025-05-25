package com.lineage.data.item_etcitem.event;

import com.lineage.data.event.LeavesSet;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.data.item_etcitem.brave.StatusBrave;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBoxExp;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GemOfDragon extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(StatusBrave.class);
    private int _percentage = 100;
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
        return new GemOfDragon();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (item == null) {
            return;
        }
        if (pc == null) {
            return;
        }
        if (!LeavesSet.START) {
            pc.sendPackets(new S_ServerMessage("\\fT尚未開放"));
            return;
        }
        if (check(pc)) {
            pc.getInventory().removeItem(item, 1L);
            int exp = pc.get_other().get_leaves_time_exp();
            int addexp = exp + _percentage * LeavesSet.EXP;
            pc.get_other().set_leaves_time_exp(addexp);
            if (_gfxid > 0) {
                pc.sendPacketsX8(new S_SkillSound(pc.getId(), _gfxid));
            }
            if (exp != pc.get_other().get_leaves_time_exp()) {
                pc.sendPackets(new S_PacketBoxExp(pc.get_other().get_leaves_time_exp() / LeavesSet.EXP));
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
                _log.error("StatusBrave 可執行職業設定錯誤:餘數大於0");
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void set_set(String[] set) {
        try {
            _percentage = Integer.parseInt(set[1]);
            if (_percentage <= 0) {
                _log.error("GemOfDragon 設置錯誤:恢復百分比小於等於0! 使用預設100");
                _percentage = 100;
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
 * com.lineage.data.item_etcitem.event.GemOfDragon JD-Core Version: 0.6.2
 */