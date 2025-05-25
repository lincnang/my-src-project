package com.lineage.server.model;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_EffectLocation;
import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

public class L1Chaser extends TimerTask {
    private static final Log _log = LogFactory.getLog(L1Chaser.class);
    private static final Random _random = new Random();
    private final L1PcInstance _pc;
    private final L1Character _cha;
    private ScheduledFuture<?> _future = null;
    private int _timeCounter = 0;

    public L1Chaser(L1PcInstance pc, L1Character cha) {
        _cha = cha;
        _pc = pc;
    }

    public void run() {
        try {
            if ((_cha == null) || (_cha.isDead())) {
                stop();
                return;
            }
            attack();
            _timeCounter += 1;
            if (_timeCounter >= 3) {
                stop();
                return;
            }
        } catch (Throwable e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void begin() {
        _future = GeneralThreadPool.get().scheduleAtFixedRate(this, 0L, 1000L);
    }

    public void stop() {
        if (_future != null) {
            _future.cancel(false);
        }
    }

    private void attack() {
        double damage = getDamage(_pc, _cha);
        if ((_cha.getCurrentHp() - (int) damage <= 0) && (_cha.getCurrentHp() != 1)) {
            damage = _cha.getCurrentHp() - 1;
        } else if (_cha.getCurrentHp() == 1) {
            damage = 0.0D;
        }
        _pc.sendPacketsAll(new S_EffectLocation(_cha.getX(), _cha.getY(), 7025));
        if ((_cha instanceof L1PcInstance)) {
            L1PcInstance pc = (L1PcInstance) _cha;
            pc.sendPacketsAll(new S_DoActionGFX(pc.getId(), 2));
            pc.receiveDamage(_pc, damage, false, false);
        } else if ((_cha instanceof L1NpcInstance)) {
            L1NpcInstance npc = (L1NpcInstance) _cha;
            npc.broadcastPacketX10(new S_DoActionGFX(npc.getId(), 2));
            npc.receiveDamage(_pc, (int) damage);
        }
    }

    private double getDamage(L1PcInstance pc, L1Character cha) {
        double dmg = 0.0D;
        int spByItem = pc.getSp() - pc.getTrueSp();
        int intel = pc.getInt();
        int charaIntelligence = pc.getInt() + spByItem - 12;
        double coefficientA = 1.0D + 0.09375D * charaIntelligence;
        if (coefficientA < 1.0D) {
            coefficientA = 1.0D;
        }
        double coefficientB = 0.0D;
        if (intel > 25) {
            coefficientB = 1.625D;
        } else if (intel <= 12) {
            coefficientB = 0.78D;
        } else {
            coefficientB = intel * 0.065D;
        }
        double coefficientC = 0.0D;
        if (intel > 25) {
            coefficientC = 25.0D;
        } else if (intel <= 12) {
            coefficientC = 12.0D;
        } else {
            coefficientC = intel;
        }
        double bsk = 0.0D;
        if (pc.hasSkillEffect(55)) {
            bsk = 0.1D;
        }
        dmg = (_random.nextInt(6) + 1 + 7) * (1.0D + bsk) * coefficientA * coefficientB / 10.5D * coefficientC * 2.0D;
        return L1WeaponSkill.calcDamageReduction(pc, cha, dmg, 0) * 0.66D;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.L1Chaser JD-Core Version: 0.6.2
 */