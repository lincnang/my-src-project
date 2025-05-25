package com.lineage.server.model.skill;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.serverpackets.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.lineage.server.model.skill.L1SkillId.*;

/**
 * 衝突技能抵銷
 *
 * @author dexc
 */
public class L1BuffUtil {  //src029
    private static final Log _log = LogFactory.getLog(L1BuffUtil.class);

    /**
     * 無法使用指定類型道具(傳送卷軸)
     *
     * @param pc 檢查對象
     * @return
     */
    public static boolean getUseItemTeleport(final L1PcInstance pc) {
        if (pc.hasSkillEffect(EARTH_BIND)) {
            return false;
        }
        if (pc.hasSkillEffect(SHOCK_SKIN)) {
            return false;
        }
        if (pc.hasSkillEffect(4000)) {
            return false;
        }
        return !pc.hasSkillEffect(MOVE_STOP);
    }

    /**
     * 無法使用藥水
     *
     * @param pc
     * @return true:可以使用 false:無法使用
     */
    public static boolean stopPotion(L1PcInstance pc) {
        if (pc.is_decay_potion()) { // 藥水霜化術
            // 698 喉嚨灼熱，無法喝東西。
            pc.sendPackets(new S_ServerMessage(698));
            return false;
        }
        return true;
    }

    /**
     * 解除魔法技能絕對屏障
     *
     * @param pc
     */
    public static void cancelAbsoluteBarrier(L1PcInstance pc) { // 解除魔法技能絕對屏障
        if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
            pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
            pc.startHpRegeneration();
            pc.startMpRegeneration();
        }
    }

    /**
     * 加速效果 抵銷對應技能
     *
     * @param pc
     */
    public static void hasteStart(L1PcInstance pc) {
        try {
            // 解除加速術
            if (pc.hasSkillEffect(HASTE)) {
                pc.killSkillEffectTimer(HASTE);
                pc.sendPacketsAll(new S_SkillHaste(pc.getId(), 0, 0));
                pc.setMoveSpeed(0);
            }
            // 解除強力加速術
            if (pc.hasSkillEffect(GREATER_HASTE)) {
                pc.killSkillEffectTimer(GREATER_HASTE);
                pc.sendPacketsAll(new S_SkillHaste(pc.getId(), 0, 0));
                pc.setMoveSpeed(0);
            }
            // 解除加速藥水
            if (pc.hasSkillEffect(STATUS_HASTE)) {
                pc.killSkillEffectTimer(STATUS_HASTE);
                pc.sendPacketsAll(new S_SkillHaste(pc.getId(), 0, 0));
                pc.setMoveSpeed(0);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 加速藥水效果
     *
     * @param pc
     * @param timeMillis
     */
    public static void haste(L1PcInstance pc, int timeMillis) {
        try {
            hasteStart(pc);
            // 加速藥水效果
            pc.setSkillEffect(STATUS_HASTE, timeMillis);
            int objId = pc.getId();
            pc.sendPackets(new S_SkillHaste(objId, 1, timeMillis / 1000));
            pc.broadcastPacketAll(new S_SkillHaste(objId, 1, 0));
            pc.sendPacketsX8(new S_SkillSound(objId, 191));
            pc.setMoveSpeed(1);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 勇敢效果 抵銷對應技能
     *
     * @param pc
     */
    public static void braveStart(L1PcInstance pc) {
        try {
            // 解除神聖疾走
            if (pc.hasSkillEffect(HOLY_WALK)) {
                pc.killSkillEffectTimer(HOLY_WALK);
                pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
                pc.setBraveSpeed(0);
            }
            // 解除行走加速
            if (pc.hasSkillEffect(MOVING_ACCELERATION)) {
                pc.killSkillEffectTimer(MOVING_ACCELERATION);
                pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
                pc.setBraveSpeed(0);
            }
            // 解除風之疾走
            if (pc.hasSkillEffect(WIND_WALK)) {
                pc.killSkillEffectTimer(WIND_WALK);
                pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
                pc.setBraveSpeed(0);
            }
            // 解除荒神加速
            if (pc.hasSkillEffect(STATUS_BRAVE2)) {
                pc.killSkillEffectTimer(STATUS_BRAVE2);
                pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
                pc.setBraveSpeed(0);
            }
            // 解除勇敢藥水效果
            if (pc.hasSkillEffect(STATUS_BRAVE)) {
                pc.killSkillEffectTimer(STATUS_BRAVE);
                pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
                pc.setBraveSpeed(0);
            }
            // 解除精靈餅乾效果
            if (pc.hasSkillEffect(STATUS_ELFBRAVE)) {
                pc.killSkillEffectTimer(STATUS_ELFBRAVE);
                pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
                pc.setBraveSpeed(0);
            }
            // 解除生命之樹果實效果
            if (pc.hasSkillEffect(STATUS_RIBRAVE)) {
                pc.killSkillEffectTimer(STATUS_RIBRAVE);
                pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
                pc.setBraveSpeed(0);
            }
            // 解除血之渴望
            if (pc.hasSkillEffect(BLOODLUST)) {
                pc.killSkillEffectTimer(BLOODLUST);
                pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
                pc.setBraveSpeed(0);
            }
            // 解除舞躍之火
            if (pc.hasSkillEffect(FIRE_BLESS)) {
                pc.killSkillEffectTimer(FIRE_BLESS);
                pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
                pc.setBraveSpeed(0);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 勇敢藥水效果
     *
     * @param pc         對象
     * @param timeMillis TIME
     */
    public static void brave(L1PcInstance pc, int timeMillis) {
        try {
            braveStart(pc);
            // 勇敢藥水效果
            pc.setSkillEffect(STATUS_BRAVE, timeMillis);
            int objId = pc.getId();
            pc.sendPackets(new S_SkillBrave(objId, 1, timeMillis / 1000));
            pc.broadcastPacketAll(new S_SkillBrave(objId, 1, 0));
            pc.sendPacketsX8(new S_SkillSound(objId, 751));
            pc.setBraveSpeed(1);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 荒神加速效果
     *
     * @param pc         對象
     * @param timeMillis TIME
     */
    public static void superbrave(final L1PcInstance pc, final int timeMillis) {
        try {
            braveStart(pc); // 勇敢效果 抵銷對應技能
            // 荒神加速效果
            pc.setSkillEffect(STATUS_BRAVE2, timeMillis);
            final int objId = pc.getId();
            pc.sendPackets(new S_SkillBrave(objId, 5, timeMillis / 1000));
            pc.broadcastPacketAll(new S_SkillBrave(objId, 5, 0));
            pc.sendPacketsX8(new S_SkillSound(objId, 751));
            pc.setBraveSpeed(5);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public static void doPoly(L1PcInstance pc) {
        try {
            int skillId = pc.getAwakeSkillId();
            int polyId = 0;
            switch (skillId) {
                case 185:
                    polyId = 9362;
                    break;
                case 190:
                    polyId = 9364;
                    break;
                case 195:
                    polyId = 9363;
            }
            if (pc.hasSkillEffect(67)) {
                pc.killSkillEffectTimer(67);
            }
            pc.setTempCharGfx(polyId);
            pc.sendPacketsAll(new S_ChangeShape(pc, polyId));
            L1ItemInstance weapon = pc.getWeapon();
            if (weapon != null) {
                pc.sendPacketsAll(new S_CharVisualUpdate(pc));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public static void undoPoly(L1PcInstance pc) {
        try {
            int classId = pc.getClassId();
            pc.setTempCharGfx(classId);
            pc.sendPacketsAll(new S_ChangeShape(pc, classId));
            L1ItemInstance weapon = pc.getWeapon();
            if (weapon != null) {
                pc.sendPacketsAll(new S_CharVisualUpdate(pc));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public static boolean cancelExpSkill(L1PcInstance pc) {
        /*
         * if (pc.hasSkillEffect(3007)) { pc.removeSkillEffect(3007); } if
         * (pc.hasSkillEffect(3015)) { pc.removeSkillEffect(3015); } if
         * (pc.hasSkillEffect(3023)) { pc.removeSkillEffect(3023); } if
         * (pc.hasSkillEffect(3031)) { pc.removeSkillEffect(3031); } if
         * (pc.hasSkillEffect(3039)) { pc.removeSkillEffect(3039); } if
         * (pc.hasSkillEffect(3047)) { pc.removeSkillEffect(3047); } if
         * (pc.hasSkillEffect(3051)) { pc.removeSkillEffect(3051); }
         */
        if (pc.hasSkillEffect(6668)) {
            int time = pc.getSkillEffectTimeSec(6668);
            pc.sendPackets(new S_ServerMessage("\\fX第一段130%經驗 剩餘時間(秒):" + time));
            return false;
        }
        if (pc.hasSkillEffect(6669)) {
            int time = pc.getSkillEffectTimeSec(6669);
            pc.sendPackets(new S_ServerMessage("\\fX第一段150%經驗 剩餘時間(秒):" + time));
            return false;
        }
        if (pc.hasSkillEffect(6670)) {
            int time = pc.getSkillEffectTimeSec(6670);
            pc.sendPackets(new S_ServerMessage("\\fX第一段170%經驗 剩餘時間(秒):" + time));
            return false;
        }
        if (pc.hasSkillEffect(6666)) {
            int time = pc.getSkillEffectTimeSec(6666);
            pc.sendPackets(new S_ServerMessage("\\fX第一段200%經驗 剩餘時間(秒):" + time));
            return false;
        }
        if (pc.hasSkillEffect(6667)) {
            int time = pc.getSkillEffectTimeSec(6667);
            pc.sendPackets(new S_ServerMessage("\\fX第一段250%經驗 剩餘時間(秒):" + time));
            return false;
        }
        if (pc.hasSkillEffect(6671)) {
            int time = pc.getSkillEffectTimeSec(6671);
            pc.sendPackets(new S_ServerMessage("\\fX第一段300%經驗 剩餘時間(秒):" + time));
            return false;
        }
        if (pc.hasSkillEffect(6672)) {
            int time = pc.getSkillEffectTimeSec(6672);
            pc.sendPackets(new S_ServerMessage("\\fX第一段350%經驗 剩餘時間(秒):" + time));
            return false;
        }
        if (pc.hasSkillEffect(6673)) {
            int time = pc.getSkillEffectTimeSec(6673);
            pc.sendPackets(new S_ServerMessage("\\fX第一段400%經驗 剩餘時間(秒):" + time));
            return false;
        }
        if (pc.hasSkillEffect(6674)) {
            int time = pc.getSkillEffectTimeSec(6674);
            pc.sendPackets(new S_ServerMessage("\\fX第一段450%經驗 剩餘時間(秒):" + time));
            return false;
        }
        if (pc.hasSkillEffect(6675)) {
            int time = pc.getSkillEffectTimeSec(6675);
            pc.sendPackets(new S_ServerMessage("\\fX第一段500%經驗 剩餘時間(秒):" + time));
            return false;
        }
        if (pc.hasSkillEffect(6676)) {
            int time = pc.getSkillEffectTimeSec(6676);
            pc.sendPackets(new S_ServerMessage("\\fX第一段550%經驗 剩餘時間(秒):" + time));
            return false;
        }
        if (pc.hasSkillEffect(6677)) {
            int time = pc.getSkillEffectTimeSec(6677);
            pc.sendPackets(new S_ServerMessage("\\fX第一段600%經驗 剩餘時間(秒):" + time));
            return false;
        }
        if (pc.hasSkillEffect(6678)) {
            int time = pc.getSkillEffectTimeSec(6678);
            pc.sendPackets(new S_ServerMessage("\\fX第一段650%經驗 剩餘時間(秒):" + time));
            return false;
        }
        if (pc.hasSkillEffect(6679)) {
            int time = pc.getSkillEffectTimeSec(6679);
            pc.sendPackets(new S_ServerMessage("\\fX第一段700%經驗 剩餘時間(秒):" + time));
            return false;
        }
        if (pc.hasSkillEffect(6680)) {
            int time = pc.getSkillEffectTimeSec(6680);
            pc.sendPackets(new S_ServerMessage("\\fX第一段750%經驗 剩餘時間(秒):" + time));
            return false;
        }
        if (pc.hasSkillEffect(6681)) {
            int time = pc.getSkillEffectTimeSec(6681);
            pc.sendPackets(new S_ServerMessage("\\fX第一段800%經驗 剩餘時間(秒):" + time));
            return false;
        }
        return true;
    }

    public static boolean cancelExpSkill_2(L1PcInstance pc) {
        if (pc.hasSkillEffect(L1SkillId.SEXP13)) {
            int time = pc.getSkillEffectTimeSec(L1SkillId.SEXP13);
            pc.sendPackets(new S_ServerMessage("\\fY第二段130%經驗 剩餘時間(秒):" + time));
            return false;
        }
        if (pc.hasSkillEffect(L1SkillId.SEXP30)) {
            int time = pc.getSkillEffectTimeSec(L1SkillId.SEXP30);
            pc.sendPackets(new S_ServerMessage("\\fY第二段300%經驗 剩餘時間(秒):" + time));
            return false;
        }
        if (pc.hasSkillEffect(L1SkillId.SEXP150)) {
            int time = pc.getSkillEffectTimeSec(L1SkillId.SEXP150);
            pc.sendPackets(new S_ServerMessage("\\fY第二段150%經驗 剩餘時間(秒):" + time));
            return false;
        }
        if (pc.hasSkillEffect(L1SkillId.SEXP175)) {
            int time = pc.getSkillEffectTimeSec(L1SkillId.SEXP175);
            pc.sendPackets(new S_ServerMessage("\\fY第二段175%經驗 剩餘時間(秒):" + time));
            return false;
        }
        if (pc.hasSkillEffect(L1SkillId.SEXP200)) {
            int time = pc.getSkillEffectTimeSec(L1SkillId.SEXP200);
            pc.sendPackets(new S_ServerMessage("\\fY第二段200%經驗 剩餘時間(秒):" + time));
            return false;
        }
        if (pc.hasSkillEffect(L1SkillId.SEXP225)) {
            int time = pc.getSkillEffectTimeSec(L1SkillId.SEXP225);
            pc.sendPackets(new S_ServerMessage("\\fY第二段225%經驗 剩餘時間(秒):" + time));
            return false;
        }
        if (pc.hasSkillEffect(L1SkillId.SEXP250)) {
            int time = pc.getSkillEffectTimeSec(L1SkillId.SEXP250);
            pc.sendPackets(new S_ServerMessage("\\fY第二段250%經驗 剩餘時間(秒):" + time));
            return false;
        }
        if (pc.hasSkillEffect(L1SkillId.SEXP500)) {
            int time = pc.getSkillEffectTimeSec(L1SkillId.SEXP500);
            pc.sendPackets(new S_ServerMessage("\\fY第二段500%經驗 剩餘時間(秒):" + time));
            return false;
        }
        return true;
    }

    public static int cancelDragon(L1PcInstance pc) {
        if (pc.hasSkillEffect(6683)) {
            return pc.getSkillEffectTimeSec(6683);
        }
        if (pc.hasSkillEffect(6684)) {
            return pc.getSkillEffectTimeSec(6684);
        }
        if (pc.hasSkillEffect(6685)) {
            return pc.getSkillEffectTimeSec(6685);
        }
        if (pc.hasSkillEffect(6686)) {
            return pc.getSkillEffectTimeSec(6686);
        }
        if (pc.hasSkillEffect(6687)) {
            return pc.getSkillEffectTimeSec(6687);
        }
        if (pc.hasSkillEffect(6688)) {
            return pc.getSkillEffectTimeSec(6688);
        }
        if (pc.hasSkillEffect(6689)) {
            return pc.getSkillEffectTimeSec(6689);
        }
        return -1;
    }

    public static int cancelBaphomet(L1PcInstance pc) {
        if (pc.hasSkillEffect(40001)) {
            return pc.getSkillEffectTimeSec(40001);
        }
        return -1;
    }

    public static void cancelBuffStone(L1PcInstance pc) {
        int[] skillids = {4401, 4402, 4403, 4404, 4405, 4406, 4407, 4408, 4409, 4411, 4412, 4413, 4414, 4415, 4416, 4417, 4418, 4419, 4421, 4422, 4423, 4424, 4425, 4426, 4427, 4428, 4429, 4431, 4432, 4433, 4434, 4435, 4436, 4437, 4438, 4439};
        for (int skillid : skillids) {
            if (pc.hasSkillEffect(skillid)) {
                pc.killSkillEffectTimer(skillid);
            }
        }
    }

    public static int cancelDragonSign(L1PcInstance pc) {
        int[] skillids = {4500, 4501, 4502, 4503, 4504, 4505, 4506, 4507, 4508, 4509, 4510, 4511, 4512, 4513, 4514, 4515, 4516, 4517, 4518, 4519, 4520, 4521, 4522, 4523, 4524, 4525, 4526, 4527, 4528, 4529, 4530, 4531, 4532, 4533, 4534, 4535, 4536, 4537, 4538, 4539};
        for (int skillid : skillids) {
            if (pc.hasSkillEffect(skillid)) {
                return pc.getSkillEffectTimeSec(skillid);
            }
        }
        return -1;
    }

    /**
     * 無法使用指定類型技能(傳送技能)
     *
     * @param pc 檢查對像
     * @return
     */
    public static boolean getUseSkillTeleport(final L1PcInstance pc) {
        // added by Erics4179
        return !pc.hasSkillEffect(MOVE_STOP);
    }

    /**
     * 持續BUFF <br>
     * 身上已有對應編號BUFF時重新刷新時間
     *
     * @param cha 目標
     * @param skillId BUFF編號
     * @param time 持續時間(秒)
     * @param i 星盤技能設定的近戰回避率降低數值
     * @param j 星盤技能設定的遠程閃避率降低數值
     */
    public static void effect(final L1Character cha, final int skillId, final int time, int i, int j) {
        if (cha.hasSkillEffect(skillId)) {
            cha.removeSkillEffect(skillId);
        }
        if (skillId == ASTROLOGY_DG_ER) {
            if (cha instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) cha;
                pc.add_dodge_down(i);
                pc.addEr(-j);
                pc.setAstrologyDG(i);
                pc.setAstrologyER(j);
                pc.sendPackets(new S_PacketBoxIcon1(false, pc.get_dodge_down()));
            }
        }
        cha.setSkillEffect(skillId, time * 1000);
    }
}
