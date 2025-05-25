package com.lineage.server.timecontroller.pc;

import com.add.Tsai.RevertHpMp;
import com.lineage.config.ConfigOther;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1HouseLocation;
import com.lineage.server.types.Point;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

import static com.lineage.server.model.skill.L1SkillId.*;

public class MprExecutor {
    private static final Log _log = LogFactory.getLog(MprExecutor.class);
    private static final Map<Integer, Integer> _skill = new HashMap<>();
    private static final Map<Integer, Integer> _mapId = new HashMap<>();
    private static MprExecutor _instance;

    private MprExecutor() {
        // 技能回復MP增加
        _skill.put(MEDITATION, 5);
        _skill.put(CONCENTRATION, 2);
        _skill.put(COOKING_1_2_N, 3);
        _skill.put(COOKING_1_2_S, 3);
        _skill.put(COOKING_2_4_N, 2);
        _skill.put(COOKING_2_4_S, 2);
        _skill.put(COOKING_3_5_N, 2);
        _skill.put(COOKING_3_5_S, 2);
        // 旅館
        _mapId.put(16384, ConfigOther.INNMPR);
        _mapId.put(16896, ConfigOther.INNMPR);
        _mapId.put(17408, ConfigOther.INNMPR);
        _mapId.put(17920, ConfigOther.INNMPR);
        _mapId.put(18432, ConfigOther.INNMPR);
        _mapId.put(18944, ConfigOther.INNMPR);
        _mapId.put(19968, ConfigOther.INNMPR);
        _mapId.put(19456, ConfigOther.INNMPR);
        _mapId.put(20480, ConfigOther.INNMPR);
        _mapId.put(20992, ConfigOther.INNMPR);
        _mapId.put(21504, ConfigOther.INNMPR);
        _mapId.put(22016, ConfigOther.INNMPR);
        _mapId.put(22528, ConfigOther.INNMPR);
        _mapId.put(23040, ConfigOther.INNMPR);
        _mapId.put(23552, ConfigOther.INNMPR);
        _mapId.put(24064, ConfigOther.INNMPR);
        _mapId.put(24576, ConfigOther.INNMPR);
        _mapId.put(25088, ConfigOther.INNMPR);
        // 城堡
        _mapId.put(15, ConfigOther.CASTLEMPR);// 肯特內城
        _mapId.put(29, ConfigOther.CASTLEMPR);// 風木城內城
        _mapId.put(52, ConfigOther.CASTLEMPR);// 奇巖內城
        _mapId.put(64, ConfigOther.CASTLEMPR);// 海音內城
        _mapId.put(260, ConfigOther.CASTLEMPR);// 妖堡內城
        // this._mapId.put(66, ConfigOther.CASTLEMPR);// 鐵門公會
        _mapId.put(300, ConfigOther.CASTLEMPR);// 亞丁內城
    }

    protected static MprExecutor get() {
        if (_instance == null) {
            _instance = new MprExecutor();
        }
        return _instance;
    }

    private static void regenMp(L1PcInstance tgpc) {
        tgpc.set_mpRegenType(0);
        if (tgpc.getMapId() == 201) {
            return;
        }
        int baseMpr = 1;
        switch (tgpc.getWis()) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                baseMpr = 1;
                break;
            case 15:
            case 16:
                baseMpr = 2;
                break;
            default:
                baseMpr = 3;
        }
        if ((!tgpc.getSkillisEmpty()) && (tgpc.getSkillEffect().size() > 0)) {
            try {
                if (tgpc.hasSkillEffect(1002)) {
                    if (tgpc.getWis() < 11) {
                        baseMpr++;
                    } else {
                        baseMpr += tgpc.getWis() - 10;
                    }
                }
                for (Integer skillid : _skill.keySet()) {
                    if (tgpc.hasSkillEffect(skillid)) {
                        Integer integer = (Integer) _skill.get(skillid);
                        if (integer != null) {
                            baseMpr += integer;
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
                        baseMpr += r.getMpr();
                        break;
                    } else {
                        if (tgpc.getX() >= r.getSx() && tgpc.getX() <= r.getEx() && tgpc.getY() >= r.getSy() && tgpc.getY() <= r.getEy()) {
                            baseMpr += r.getMpr();
                            break;
                        }
                    }
                }
            }
        }
        if (L1HouseLocation.isInHouse(tgpc.getX(), tgpc.getY(), tgpc.getMapId())) {
            baseMpr += ConfigOther.HOMEMPR;
        }
        if (L1HouseLocation.isInHouse(tgpc.getMapId())) {
            baseMpr += ConfigOther.HOMEMPR;
        }
        @SuppressWarnings("unlikely-arg-type") Integer rmp = (Integer) _mapId.get(tgpc.getMapId());
        if (rmp != null) {
            baseMpr += rmp;
        }
        if ((tgpc.isElf()) && (tgpc.getMapId() == 4) && (tgpc.getLocation().isInScreen(new Point(33055, 32336)))) {
            baseMpr += ConfigOther.FORESTMPR;
        }
        // 自訂地圖回魔
        if (tgpc.getMapId() == ConfigOther.CUSTOM_MAPID) {
            baseMpr += ConfigOther.CUSTOM_MPR;
        }
        if (tgpc.getOriginalMpr() > 0) {
            baseMpr += tgpc.getOriginalMpr();
        }
        int itemMpr = tgpc.getInventory().mpRegenPerTick();
        itemMpr += tgpc.getMpr();
        int mpr = baseMpr + itemMpr;
        int newMp = tgpc.getCurrentMp() + mpr;
        newMp = Math.max(newMp, 0);
        tgpc.setCurrentMp(newMp);
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
            if (tgpc.getCurrentMp() >= tgpc.getMaxMp()) {
                return false;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
            return false;
        }
        return true;
    }

    protected void checkRegenMp(L1PcInstance tgpc) {
        try {
            tgpc.set_mpRegenType(tgpc.mpRegenType() + tgpc.getMpRegenState());
            tgpc.setRegenState(4);
            if (tgpc.isRegenMp()) {
                regenMp(tgpc);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
