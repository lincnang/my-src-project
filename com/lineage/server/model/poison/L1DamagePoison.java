package com.lineage.server.model.poison;

import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.serverpackets.S_SkillIconPoison;
import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.TimeUnit;

public class L1DamagePoison extends L1Poison {
    private static final Log _log = LogFactory.getLog(L1DamagePoison.class);
    private final L1Character _attacker;
    private final L1Character _target;
    private final int _damageSpan;
    private final int _damage;
    private Thread _timer;

    private L1DamagePoison(L1Character attacker, L1Character cha, int damageSpan, int damage) {
        _attacker = attacker;
        _target = cha;
        _damageSpan = damageSpan;
        _damage = damage;
        doInfection();
    }

    public static boolean doInfection(L1Character attacker, L1Character cha, int damageSpan, int damage) {
        if (!isValidTarget(cha)) {
            return false;
        }
        cha.setPoison(new L1DamagePoison(attacker, cha, damageSpan, damage));
        return true;
    }

    boolean isDamageTarget(L1Character cha) {
        return ((cha instanceof L1PcInstance)) || ((cha instanceof L1MonsterInstance));
    }

    private void doInfection() {
        _target.setSkillEffect(1006, 30000);
        _target.setPoisonEffect(1);
        if (_target instanceof L1PcInstance) {
            L1PcInstance _pc = (L1PcInstance) _target;
            _pc.sendPackets(new S_SkillIconPoison(1, 30));
        }
        if (isDamageTarget(_target)) {
            _timer = new NormalPoisonTimer();
            GeneralThreadPool.get().execute(_timer);
        }
    }

    public int getEffectId() {
        return 1;
    }

    public void cure() {
        _target.setPoisonEffect(0);
        _target.killSkillEffectTimer(1006);
        _target.setPoison(null);
        if (_target instanceof L1PcInstance) {
            L1PcInstance _pc = (L1PcInstance) _target;
            _pc.sendPackets(new S_SkillIconPoison(1, 0));
        }
        if (_timer != null) {
            _timer.interrupt();
        }
    }

    private class NormalPoisonTimer extends Thread {
        private NormalPoisonTimer() {
        }

        public void run() {
            try {
                while (_target.hasSkillEffect(1006)) {
                    TimeUnit.MILLISECONDS.sleep(_damageSpan);
                    if (!_target.hasSkillEffect(1006)) {
                        break;
                    }
                    if ((_target instanceof L1PcInstance)) {
                        L1PcInstance player = (L1PcInstance) _target;
                        player.receiveDamage(_attacker, _damage, false, true);
                        if (player.isDead()) {
                            break;
                        }
                    } else if ((_target instanceof L1MonsterInstance)) {
                        L1MonsterInstance mob = (L1MonsterInstance) _target;
                        mob.receiveDamage(_attacker, _damage);
                        if (mob.isDead()) {
                            break;
                        }
                    }
                }
            } catch (InterruptedException e) {
                // 睡眠被中斷通常是正常情況：中毒解除(cure 呼叫 interrupt)或伺服器正在關閉/重載。
                // 不將其視為錯誤，恢復中斷旗標以便上層可感知目前狀態。
                Thread.currentThread().interrupt();
            }
            cure();
        }
    }
}
