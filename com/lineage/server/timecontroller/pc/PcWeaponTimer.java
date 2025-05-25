package com.lineage.server.timecontroller.pc;

import com.lineage.config.ConfigWeaponryEffects;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_EffectLocation;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.thread.PcOtherThreadPool;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

/**
 * PC 武器加成特效時間軸 (每隔幾秒出現1次)
 *
 * @author
 */
public class PcWeaponTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(PcWeaponTimer.class);
    private ScheduledFuture<?> _timer;
    private int _timeMillis;

    /**
     * PC 執行 判斷
     *
     * @param tgpc
     * @return true:執行 false:不執行
     */
    private static boolean check(final L1PcInstance tgpc) {
        try {
            // 人物為空
            if (tgpc == null) {
                return false;
            }
            // 人物登出
            if (tgpc.getOnlineStatus() == 0) {
                return false;
            }
            // 中斷連線
            if (tgpc.getNetConnection() == null) {
                return false;
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
            return false;
        }
        return true;
    }

    public void start(final int timeMillis) {
        _timeMillis = timeMillis;
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
    }

    @Override
    public void run() {
        try {
            final Collection<L1PcInstance> all = World.get().getAllPlayers();
            // 不包含元素
            if (all.isEmpty()) {
                return;
            }
            for (final Iterator<L1PcInstance> iter = all.iterator(); iter.hasNext(); ) {
                final L1PcInstance tgpc = iter.next();
                // PC 執行 判斷
                if (check(tgpc)) {
                    final L1ItemInstance weapon = tgpc.getWeapon();
                    if (weapon != null) {
                        final int value = weapon.getEnchantLevel() - weapon.getItem().get_safeenchant();
                        /** 使用`S_EffectLocation`避免人物異常斷線 */
                        if (value >= 30) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_30 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_30));
                            }
                        } else if (value >= 29) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_29 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_29));
                            }
                        } else if (value >= 28) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_28 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_28));
                            }
                        } else if (value >= 27) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_27 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_27));
                            }
                        } else if (value >= 26) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_26 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_26));
                            }
                        } else if (value >= 25) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_25 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_25));
                            }
                        } else if (value >= 24) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_24 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_24));
                            }
                        } else if (value >= 23) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_23 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_23));
                            }
                        } else if (value >= 22) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_22 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_22));
                            }
                        } else if (value >= 21) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_21 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_21));
                            }
                        } else if (value >= 20) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_20 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_20));
                            }
                        } else if (value >= 19) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_19 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_19));
                            }
                        } else if (value >= 18) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_18 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_18));
                            }
                        } else if (value >= 17) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_17 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_17));
                            }
                        } else if (value >= 16) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_16 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_16));
                            }
                        } else if (value >= 15) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_15 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_15));
                            }
                        } else if (value >= 14) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_14 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_14));
                            }
                        } else if (value >= 13) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_13 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_13));
                            }
                        } else if (value >= 12) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_12 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_12));
                            }
                        } else if (value >= 11) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_11 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_11));
                            }
                        } else if (value >= 10) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_10 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_10));
                            }
                        } else if (value >= 9) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_9 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_9));
                            }
                        } else if (value >= 8) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_8 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_8));
                            }
                        } else if (value >= 7) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_7 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_7));
                            }
                        } else if (value >= 6) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_6 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_6));
                            }
                        } else if (value >= 5) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_5 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_5));
                            }
                        } else if (value >= 4) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_4 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_4));
                            }
                        } else if (value >= 3) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_3 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_3));
                            }
                        } else if (value >= 2) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_2 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_2));
                            }
                        } else if (value >= 1) {
                            if (ConfigWeaponryEffects.WEAPON_EFFECT_1 > -1) {
                                // 送出封包
                                tgpc.sendPacketsYN(new S_EffectLocation(tgpc.getX(), tgpc.getY(), ConfigWeaponryEffects.WEAPON_EFFECT_1));
                            }
                        }
                    }
                }
            }
        } catch (final Exception e) {
            _log.error("PC武器加成特效時間軸 異常重啟", e);
            PcOtherThreadPool.get().cancel(_timer, false);
            final PcWeaponTimer pcWeaponTimer = new PcWeaponTimer();
            pcWeaponTimer.start(_timeMillis);
        }
    }
}
