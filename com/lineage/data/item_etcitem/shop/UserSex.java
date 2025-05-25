package com.lineage.data.item_etcitem.shop;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ChangeShape;
import com.lineage.server.serverpackets.S_CharVisualUpdate;
import com.lineage.server.serverpackets.S_SkillSound;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.TimeUnit;

public class UserSex extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(UserSex.class);

    public static ItemExecutor get() {
        return new UserSex();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (item == null) {
            return;
        }
        if (pc == null) {
            return;
        }
        int sex = pc.get_sex();
        pc.getInventory().removeItem(item, 1L);
        int newSex = -1;
        int newType = -1;
        if (sex == 0) {
            newSex = 1;
            if (pc.isCrown()) {
                newType = 1;
            } else if (pc.isKnight()) {
                newType = 48;
            } else if (pc.isElf()) {
                newType = 37;
            } else if (pc.isWizard()) {
                newType = 1186;
            } else if (pc.isDarkelf()) {
                newType = 2796;
            } else if (pc.isDragonKnight()) {
                newType = 6661;
            } else if (pc.isIllusionist()) {
                newType = 6650;
            } else if (pc.isWarrior()) {
                newType = 12494;
            }
        } else {
            newSex = 0;
            if (pc.isCrown()) {
                newType = 0;
            } else if (pc.isKnight()) {
                newType = 61;
            } else if (pc.isElf()) {
                newType = 138;
            } else if (pc.isWizard()) {
                newType = 734;
            } else if (pc.isDarkelf()) {
                newType = 2786;
            } else if (pc.isDragonKnight()) {
                newType = 6658;
            } else if (pc.isIllusionist()) {
                newType = 6671;
            } else if (pc.isWarrior()) {
                newType = 12490;
            }
        }
        try {
            pc.sendPacketsX8(new S_SkillSound(pc.getId(), 196));
            TimeUnit.MILLISECONDS.sleep(50L);
            pc.sendPacketsX8(new S_SkillSound(pc.getId(), 197));
            TimeUnit.MILLISECONDS.sleep(50L);
            pc.sendPacketsX8(new S_SkillSound(pc.getId(), 198));
            pc.sendPacketsAll(new S_ChangeShape(pc, newType));
            L1ItemInstance weapon = pc.getWeapon();
            if (weapon != null) {
                pc.sendPacketsAll(new S_CharVisualUpdate(pc));
            }
            pc.set_sex(newSex);
            pc.setClassId(newType);
            pc.save();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.shop.UserSex JD-Core Version: 0.6.2
 */