package com.lineage.server.model.skillUse;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Skills;

/**
 * 技能相關使用條件/限制
 *
 * @author daien
 */
public class L1SkillUseMode {
    /**
     * 該技能所需耗用的HP/MP/材料/正義質
     *
     * @param user  執行者
     * @param skill 技能資料
     */
    public boolean isConsume(L1Character user, L1Skills skill) {
        int mpConsume = skill.getMpConsume();
        int hpConsume = skill.getHpConsume();
        int itemConsume = skill.getItemConsumeId();
        int itemConsumeCount = skill.getItemConsumeCount();
        int lawful = skill.getLawful();
        int skillId = skill.getSkillId();
        int currentMp = 0;
        int currentHp = 0;
        L1NpcInstance useNpc = null;
        L1PcInstance usePc = null;
        if ((user instanceof L1NpcInstance)) {
            useNpc = (L1NpcInstance) user;
            currentMp = useNpc.getCurrentMp();
            currentHp = useNpc.getCurrentHp();
            boolean isStop = false;
            if (useNpc.hasSkillEffect(64)) {
                isStop = true;
            }
            if ((useNpc.hasSkillEffect(161)) && (!isStop)) {
                isStop = true;
            }
            if ((useNpc.hasSkillEffect(1007)) && (!isStop)) {
                isStop = true;
            }
            if (isStop) {
                return false;
            }
        }
        if ((user instanceof L1PcInstance)) {
            usePc = (L1PcInstance) user;
            currentMp = usePc.getCurrentMp();
            currentHp = usePc.getCurrentHp();
            if ((usePc.getInt() > 12) && (skillId > 8) && (skillId <= 80)) {
                mpConsume--;
            }
            if ((usePc.getInt() > 13) && (skillId > 16) && (skillId <= 80)) {
                mpConsume--;
            }
            if ((usePc.getInt() > 14) && (skillId > 23) && (skillId <= 80)) {
                mpConsume--;
            }
            if ((usePc.getInt() > 15) && (skillId > 32) && (skillId <= 80)) {
                mpConsume--;
            }
            if ((usePc.getInt() > 16) && (skillId > 40) && (skillId <= 80)) {
                mpConsume--;
            }
            if ((usePc.getInt() > 17) && (skillId > 48) && (skillId <= 80)) {
                mpConsume--;
            }
            if ((usePc.getInt() > 18) && (skillId > 56) && (skillId <= 80)) {
                mpConsume--;
            }
            if ((usePc.getInt() > 12) && (skillId >= 87) && (skillId <= 91)) {
                mpConsume -= usePc.getInt() - 12;
            }
            switch (skillId) {
                case 26:
                    if (usePc.getInventory().checkEquipped(20013)) {
                        mpConsume >>= 1;
                    }
                    break;
                case 43:
                    if (usePc.getInventory().checkEquipped(20013)) {
                        mpConsume >>= 1;
                    }
                    if (usePc.getInventory().checkEquipped(20008)) {
                        mpConsume >>= 1;
                    }
                    break;
                case 1:
                    if (usePc.getInventory().checkEquipped(20014)) {
                        mpConsume >>= 1;
                    }
                    break;
                case 19:
                    if (usePc.getInventory().checkEquipped(20014)) {
                        mpConsume >>= 1;
                    }
                    break;
                case 12:
                    if (usePc.getInventory().checkEquipped(20015)) {
                        mpConsume >>= 1;
                    }
                    break;
                case 13:
                    if (usePc.getInventory().checkEquipped(20015)) {
                        mpConsume >>= 1;
                    }
                    break;
                case 42:
                    if (usePc.getInventory().checkEquipped(20015)) {
                        mpConsume >>= 1;
                    }
                    break;
                case 54:
                    if (usePc.getInventory().checkEquipped(20023)) {
                        mpConsume >>= 1;
                    }
                    break;
            }
            if (usePc.getOriginalMagicConsumeReduction() > 0) {
                mpConsume -= usePc.getOriginalMagicConsumeReduction();
            }
            if (skill.getMpConsume() > 0) {
                mpConsume = Math.max(mpConsume, 1);
            }
            if (usePc.isElf()) {
                boolean isError = false;
                String msg = null;
                if ((skill.getSkillLevel() >= 17) && (skill.getSkillLevel() <= 22)) {
                    int magicattr = skill.getAttr();
                    switch (magicattr) {
                        case 1:
                            if (magicattr != usePc.getElfAttr()) {
                                isError = true;
                                msg = "$1062";
                            }
                            break;
                        case 2:
                            if (magicattr != usePc.getElfAttr()) {
                                isError = true;
                                msg = "$1059";
                            }
                            break;
                        case 4:
                            if (magicattr != usePc.getElfAttr()) {
                                isError = true;
                                msg = "$1060";
                            }
                            break;
                        case 8:
                            if (magicattr != usePc.getElfAttr()) {
                                isError = true;
                                msg = "$1061";
                            }
                            break;
                        case 3:
                        case 5:
                        case 6:
                        case 7:
                    }
                    if ((skillId == 147) && (usePc.getElfAttr() == 0)) {
                        usePc.sendPackets(new S_ServerMessage(280));
                        return false;
                    }
                }
                if ((isError) && (!usePc.isGm())) {
                    usePc.sendPackets(new S_ServerMessage(1385, msg));
                    return false;
                }
            }
            /*
             * if (usePc.isWizard()) { if ((skillId == 77) && (usePc.getLawful()
             * < 500)) { usePc.sendPackets(new S_ServerMessage(352, "$967"));
             * return false; }
             *
             * }
             */
            if ((usePc.isDarkelf()) && (skillId == 108)) {
                hpConsume = currentHp - 100;
                mpConsume = currentMp - 1;
            }
            if ((itemConsume != 0) && // 需要消耗材料
                    (!usePc.getInventory().checkItem(itemConsume, itemConsumeCount))) {// 材料不足
                if (!usePc.isGm()) {
                    usePc.sendPackets(new S_ServerMessage(299));
                    return false;
                }
            }
        }
        if (currentHp < hpConsume + 1) {
            if (usePc != null) {
                usePc.sendPackets(new S_ServerMessage(279));
                if (usePc.isGm()) { // GM自動補滿血
                    usePc.setCurrentHp(usePc.getMaxHp());
                }
            }
            return false;
        }
        if (currentMp < mpConsume) {
            if (usePc != null) {
                usePc.sendPackets(new S_ServerMessage(278));
                if (usePc.isGm()) { // GM自動補滿魔力
                    usePc.setCurrentMp(usePc.getMaxMp());
                }
            }
            return false;
        }
        if (usePc != null) {
            if (lawful != 0) {
                usePc.addLawful(lawful);
            }
            if ((itemConsume != 0) && (!usePc.isGm())) { // 消耗材料
                usePc.getInventory().consumeItem(itemConsume, itemConsumeCount);
            }
        }
        int current_hp = user.getCurrentHp() - hpConsume;
        user.setCurrentHp(current_hp);
        int current_mp = user.getCurrentMp() - mpConsume;
        user.setCurrentMp(current_mp);
        return true;
    }
}
