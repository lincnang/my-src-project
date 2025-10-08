package com.lineage.server.timecontroller.pc;

import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PcGhostTimer extends TimerTask {
    protected static final Random _random = new Random();
    private static final Log _log = LogFactory.getLog(PcGhostTimer.class);
    private ScheduledFuture<?> _timer;

    private static void check(L1PcInstance tgpc, Integer time) {
        if (time > 0) {
            if (tgpc.isDead()) {
                tgpc.set_ghostTime(-1);
            } else {
                tgpc.set_ghostTime(time);
            }
        } else {
            tgpc.set_ghostTime(-1);
            if (tgpc.getNetConnection() != null) {
                outPc(tgpc);
            }
        }
    }

    private static void outPc(L1PcInstance tgpc) {
        try {
            if (tgpc != null) {
                tgpc.makeReadyEndGhost();
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void start() {
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, 1100L, 1100L);
    }

    public void run() {
        try {
            Collection<L1PcInstance> all = World.get().getAllPlayers();
            if (all.isEmpty()) {
                return;
            }
            for (L1PcInstance tgpc : all) {
                if (tgpc.IsAuto()) {
                    if (tgpc.getNA432() > 0) {
                        tgpc.setNA432(tgpc.getNA432() - 1);
                    }
                    if (tgpc.getNA61() > 0) {
                        tgpc.setNA61(tgpc.getNA61() - 1);
                    }
                }
                if (!tgpc.isDead()) {
                    if (tgpc.hasSkillEffect(7007)) {
                        wind_dmg(tgpc);
                    } else if (tgpc.hasSkillEffect(7008)) {
                        earth_dmg(tgpc);
                    }
                    if (tgpc.isGhost()) {
                        int time = tgpc.get_ghostTime();
                        time--;
                        check(tgpc, time);
                        TimeUnit.MILLISECONDS.sleep(1L);
                    }
                }
            }
        } catch (Exception e) {
            _log.error("PC 鬼魂模式處理時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            PcGhostTimer pcghostTimer = new PcGhostTimer();
            pcghostTimer.start();
        }
    }

    private void earth_dmg(L1PcInstance pc) {
        try {
            int damage = c3_power_dmg(pc, 4);
            for (L1Object tgobj : World.get().getVisibleObjects(pc, 1)) {
                if ((tgobj instanceof L1PcInstance)) {
                    L1PcInstance tgpc = (L1PcInstance) tgobj;
                    if (!tgpc.isDead()) {
                        if ((tgpc.getClanid() != pc.getClanid()) || (tgpc.getClanid() == 0)) {
                            if (!tgpc.getMap().isSafetyZone(tgpc.getLocation())) {
                                int resist = tgpc.getEarth();
                                if (resist > 0) {
                                    damage = c3_power_dmg_down(damage, Math.min(100, resist));
                                } else if (resist < 0) {
                                    damage = c3_power_dmg_up(damage, Math.min(0, resist));
                                }
                                tgpc.receiveDamage(pc, damage, false, true);
                                tgpc.sendPacketsX8(new S_DoActionGFX(tgpc.getId(), 2));
                            }
                        }
                    }
                } else if ((tgobj instanceof L1MonsterInstance)) {
                    L1MonsterInstance tgmob = (L1MonsterInstance) tgobj;
                    if (!tgmob.isDead()) {
                        tgmob.receiveDamage(pc, damage, 1);
                        tgmob.broadcastPacketX8(new S_DoActionGFX(tgmob.getId(), 2));
                    }
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void wind_dmg(L1PcInstance pc) {
        try {
            int damage = c3_power_dmg(pc, 3);
            for (L1Object tgobj : World.get().getVisibleObjects(pc, 1)) {
                if ((tgobj instanceof L1PcInstance)) {
                    L1PcInstance tgpc = (L1PcInstance) tgobj;
                    if (!tgpc.isDead()) {
                        if ((tgpc.getClanid() != pc.getClanid()) || (tgpc.getClanid() == 0)) {
                            if (!tgpc.getMap().isSafetyZone(tgpc.getLocation())) {
                                int resist = tgpc.getWind();
                                if (resist > 0) {
                                    damage = c3_power_dmg_down(damage, Math.min(100, resist));
                                } else if (resist < 0) {
                                    damage = c3_power_dmg_up(damage, Math.min(0, resist));
                                }
                                tgpc.receiveDamage(pc, damage, false, true);
                                tgpc.sendPacketsX8(new S_DoActionGFX(tgpc.getId(), 2));
                            }
                        }
                    }
                } else if ((tgobj instanceof L1MonsterInstance)) {
                    L1MonsterInstance tgmob = (L1MonsterInstance) tgobj;
                    if (!tgmob.isDead()) {
                        tgmob.receiveDamage(pc, damage, 8);
                        tgmob.broadcastPacketX8(new S_DoActionGFX(tgmob.getId(), 2));
                    }
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private int c3_power_dmg_down(int damage, int resist) {
        int r = 100 - resist;
        int dmg = damage * r / 100;
        return Math.max(5, dmg);
    }

    private int c3_power_dmg_up(int damage, int resist) {
        int dmg = damage - damage * resist / 100;
        return Math.abs(dmg);
    }

    private int c3_power_dmg(L1PcInstance pc, int type) {
        int damage = 0;
        int level = pc.getLevel();
        switch (type) {
            case 3:
                if ((level >= 50) && (level < 70)) {
                    damage = random_dmg(15, 35);
                } else if ((level >= 70) && (level < 90)) {
                    damage = random_dmg(20, 40);
                } else if ((level >= 90) && (level < 110)) {
                    damage = random_dmg(25, 45);
                } else if ((level >= 110) && (level < 130)) {
                    damage = random_dmg(30, 50);
                } else if ((level >= 130) && (level < 150)) {
                    damage = random_dmg(35, 55);
                } else if ((level >= 150) && (level < 175)) {
                    damage = random_dmg(40, 60);
                } else if ((level >= 175) && (level < 190)) {
                    damage = random_dmg(45, 65);
                } else if ((level >= 190) && (level < 200)) {
                    damage = random_dmg(50, 70);
                } else {
                    damage = random_dmg(60, 100);
                }
                break;
            case 4:
                if ((level >= 50) && (level < 70)) {
                    damage = random_dmg(15, 35);
                } else if ((level >= 70) && (level < 90)) {
                    damage = random_dmg(20, 40);
                } else if ((level >= 90) && (level < 110)) {
                    damage = random_dmg(25, 45);
                } else if ((level >= 110) && (level < 130)) {
                    damage = random_dmg(30, 50);
                } else if ((level >= 130) && (level < 150)) {
                    damage = random_dmg(35, 55);
                } else if ((level >= 150) && (level < 175)) {
                    damage = random_dmg(40, 60);
                } else if ((level >= 175) && (level < 190)) {
                    damage = random_dmg(45, 65);
                } else if ((level >= 190) && (level < 200)) {
                    damage = random_dmg(50, 70);
                } else {
                    damage = random_dmg(60, 100);
                }
                break;
        }
        return damage;
    }

    private int random_dmg(int i, int j) {
        return _random.nextInt(j - i) + i;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.pc.PcGhostTimer JD-Core Version: 0.6.2
 */