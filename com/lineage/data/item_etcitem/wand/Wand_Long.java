package com.lineage.data.item_etcitem.wand;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Wand_Long extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(Wand_Long.class);
    private boolean _isCrown;
    private boolean _isKnight;
    private boolean _isElf;
    private boolean _isWizard;
    private boolean _isDarkelf;
    private boolean _isDragonKnight;
    private boolean _isIllusionist;
    private boolean _isWarrior;
    private int _skillid = 0;

    public static ItemExecutor get() {
        return new Wand_Long();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (pc == null) {
            return;
        }
        if (item == null) {
            return;
        }
        if ((pc.isInvisble()) || (pc.isInvisDelay())) {
            pc.sendPackets(new S_ServerMessage(281));
            return;
        }
        int targetID = data[0];
        int spellsc_x = data[1];
        int spellsc_y = data[2];
        if ((targetID == pc.getId()) || (targetID == 0)) {
            pc.sendPackets(new S_ServerMessage(281));
            return;
        }
        if (check(pc)) {
            L1BuffUtil.cancelAbsoluteBarrier(pc);
            L1SkillUse l1skilluse = new L1SkillUse();
            l1skilluse.handleCommands(pc, _skillid, targetID, spellsc_x, spellsc_y, 0, 0);
        } else {
            // \f1你的職業無法使用此道具。
            pc.sendPackets(new S_ServerMessage(264));
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
                _log.error("Wand_Long2 可執行職業設定錯誤:餘數大於0");
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void set_set(String[] set) {
        try {
            _skillid = Integer.parseInt(set[1]);
        } catch (Exception localException) {
        }
        try {
            int user_type = Integer.parseInt(set[2]);
            set_use_type(user_type);
        } catch (Exception localException2) {
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.wand.ARMOR_BREAK JD-Core Version: 0.6.2
 */