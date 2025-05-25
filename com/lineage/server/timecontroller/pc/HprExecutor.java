package com.lineage.server.timecontroller.pc;

import com.add.Tsai.RevertHpMp;
import com.lineage.config.ConfigOther;
import com.lineage.data.quest.CKEWLv50_1;
import com.lineage.data.quest.DarkElfLv50_1;
import com.lineage.server.model.Instance.L1EffectInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1HouseLocation;
import com.lineage.server.model.L1Object;
import com.lineage.server.types.Point;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.lineage.server.model.skill.L1SkillId.*;

public class HprExecutor {
    private static final Log _log = LogFactory.getLog(HprExecutor.class);
    private static final Map<Integer, Integer> _skill = new HashMap<>();
    private static final Map<Integer, Integer> _mapIdU = new HashMap<>();
    private static final Map<Integer, Integer> _mapIdD = new HashMap<>();
    private static HprExecutor _instance;

    private HprExecutor() {
        // 技能回復HP增加
        _skill.put(NATURES_TOUCH, 15);
        _skill.put(COOKING_1_5_N, 3);
        _skill.put(COOKING_1_5_S, 3);
        _skill.put(COOKING_2_4_N, 2);
        _skill.put(COOKING_2_4_S, 2);
        _skill.put(COOKING_3_6_N, 2);
        _skill.put(COOKING_3_6_S, 2);
        // 地圖回復HP增加
        // 旅館
        _mapIdU.put(16384, ConfigOther.INNHPR);
        _mapIdU.put(16896, ConfigOther.INNHPR);
        _mapIdU.put(17408, ConfigOther.INNHPR);
        _mapIdU.put(17920, ConfigOther.INNHPR);
        _mapIdU.put(18432, ConfigOther.INNHPR);
        _mapIdU.put(18944, ConfigOther.INNHPR);
        _mapIdU.put(19968, ConfigOther.INNHPR);
        _mapIdU.put(19456, ConfigOther.INNHPR);
        _mapIdU.put(20480, ConfigOther.INNHPR);
        _mapIdU.put(20992, ConfigOther.INNHPR);
        _mapIdU.put(21504, ConfigOther.INNHPR);
        _mapIdU.put(22016, ConfigOther.INNHPR);
        _mapIdU.put(22528, ConfigOther.INNHPR);
        _mapIdU.put(23040, ConfigOther.INNHPR);
        _mapIdU.put(23552, ConfigOther.INNHPR);
        _mapIdU.put(24064, ConfigOther.INNHPR);
        _mapIdU.put(24576, ConfigOther.INNHPR);
        _mapIdU.put(25088, ConfigOther.INNHPR);
        // 城堡
        _mapIdU.put(15, ConfigOther.CASTLEHPR);// 肯特內城
        _mapIdU.put(29, ConfigOther.CASTLEHPR);// 風木城內城
        _mapIdU.put(52, ConfigOther.CASTLEHPR);// 奇巖內城
        _mapIdU.put(64, ConfigOther.CASTLEHPR);// 海音城堡
        _mapIdU.put(260, ConfigOther.CASTLEHPR);// 妖堡內城
        // _mapIdU.put(66, ConfigOther.CASTLEHPR);// 鐵門公會
        _mapIdU.put(300, ConfigOther.CASTLEHPR);// 亞丁內城
        // HP減少的MAP(任務MAP)
        _mapIdD.put(410, -10);// 魔族神殿
        _mapIdD.put(CKEWLv50_1.MAPID, -10);// 再生聖殿 1樓/2樓/3樓
        _mapIdD.put(DarkElfLv50_1.MAPID, -10);// 黑暗妖精試煉地監
    }

    protected static HprExecutor get() {
        if (_instance == null) {
            _instance = new HprExecutor();
        }
        return _instance;
    }

    private static void regenHp(L1PcInstance tgpc) {
        tgpc.set_hpRegenType(0);
        int maxBonus = 1;
        if ((tgpc.getLevel() > 11) && (tgpc.getCon() >= 14)) {
            maxBonus = Math.min(tgpc.getCon() - 12, 14);
        }
        int equipHpr = tgpc.getInventory().hpRegenPerTick();
        equipHpr += tgpc.getHpr();
        Random random = new Random();
        int bonus = random.nextInt(maxBonus) + 1;
        if ((!tgpc.getSkillisEmpty()) && (tgpc.getSkillEffect().size() > 0)) {
            try {
                for (Integer skillid : _skill.keySet()) {
                    if (tgpc.hasSkillEffect(skillid)) {
                        Integer integer = (Integer) _skill.get(skillid);
                        if (integer != null) {
                            bonus += integer;
                        }
                    }
                }
            } catch (ConcurrentModificationException localConcurrentModificationException) {
            } catch (Exception e) {
                _log.error(e.getLocalizedMessage(), e);
            }
        }
        for (int i = 0; i <= RevertHpMp.get().RevertSize(); i++) {
            final RevertHpMp r = RevertHpMp.get().getRevert(i);
            if (r != null) {
                if (tgpc.getMapId() == r.getMapId()) {
                    if (r.getSx() == 0) {
                        bonus += r.getHpr();
                        break;
                    } else {
                        if (tgpc.getX() >= r.getSx() && tgpc.getX() <= r.getEx() && tgpc.getY() >= r.getSy() && tgpc.getY() <= r.getEy()) {
                            bonus += r.getHpr();
                            break;
                        }
                    }
                }
            }
        }
        if (L1HouseLocation.isInHouse(tgpc.getX(), tgpc.getY(), tgpc.getMapId())) {
            bonus += ConfigOther.HOMEHPR;
        }
        if (L1HouseLocation.isInHouse(tgpc.getMapId())) {
            bonus += ConfigOther.HOMEHPR;
        }
        Integer rhp = (Integer) _mapIdU.get((int) tgpc.getMapId());
        if (rhp != null) {
            bonus += rhp;
        }
        if ((tgpc.isElf()) && (tgpc.getMapId() == 4) && (tgpc.getLocation().isInScreen(new Point(33055, 32336)))) {
            bonus += ConfigOther.FORESTHPR;
        }
        // 自訂地圖回血
        if (tgpc.getMapId() == ConfigOther.CUSTOM_MAPID) {
            bonus += ConfigOther.CUSTOM_HPR;
        }
        if (tgpc.getOriginalHpr() > 0) {
            bonus += tgpc.getOriginalHpr();
        }
        boolean inLifeStream = false;
        if (isPlayerInLifeStream(tgpc)) {
            inLifeStream = true;
            bonus += 3;
        }
        int newHp = tgpc.getCurrentHp();
        newHp += bonus + equipHpr;
        newHp = Math.max(newHp, 1);
        if (isUnderwater(tgpc)) {
            newHp -= 20;
        }
        Integer dhp = (Integer) _mapIdD.get((int) tgpc.getMapId());
        if ((dhp != null) && (!inLifeStream)) {
            bonus += dhp;
        }
        newHp = Math.max(newHp, 0);
        tgpc.setCurrentHp(newHp);
    }

    private static boolean isUnderwater(L1PcInstance pc) {
        if (pc.getInventory().checkEquipped(20207)) {
            return false;
        }
        if (pc.hasSkillEffect(1003)) {
            return false;
        }
        if ((pc.getInventory().checkEquipped(21048)) && (pc.getInventory().checkEquipped(21049)) && (pc.getInventory().checkEquipped(21050))) {
            return false;
        }
        return pc.getMap().isUnderwater();
    }

    /**
     * 法師技能(治癒能量風暴)
     *
     * @param pc PC
     * @return true PC在4格範圍內
     */
    private static boolean isPlayerInLifeStream(L1PcInstance pc) {
        for (L1Object object : pc.getKnownObjects()) {
            if ((object instanceof L1EffectInstance)) {
                L1EffectInstance effect = (L1EffectInstance) object;
                if ((effect.getNpcId() == 81169) && (effect.getLocation().getTileLineDistance(pc.getLocation()) < 4)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean check(L1PcInstance tgpc) {
        try {
            if (tgpc == null) {
                return false;
            }
            if (tgpc.getOnlineStatus() == 0) {
                return false;
            }
            if (tgpc.getNetConnection() == null) {
                return false;
            }
            if (tgpc.isDead()) {
                return false;
            }
            if (tgpc.isTeleport()) {
                return false;
            }
            Integer dhp = (Integer) _mapIdD.get((int) tgpc.getMapId());
            if (dhp != null) {
                return true;
            }
            if (isUnderwater(tgpc)) {
                return true;
            }
            if (tgpc.getCurrentHp() >= tgpc.getMaxHp()) {
                return false;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
            return false;
        }
        return true;
    }

    protected void checkRegenHp(L1PcInstance tgpc) {
        try {
            tgpc.set_hpRegenType(tgpc.hpRegenType() + tgpc.getHpRegenState());
            tgpc.setRegenState(4);
            if (tgpc.isRegenHp()) {
                regenHp(tgpc);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
