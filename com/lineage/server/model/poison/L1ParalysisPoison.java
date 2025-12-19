package com.lineage.server.model.poison;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_SkillIconPoison;
import com.lineage.server.thread.GeneralThreadPool;

import java.util.concurrent.TimeUnit;

public class L1ParalysisPoison extends L1Poison {
    private final L1Character _target;
    private final int _delay;
    private final int _time;
    private Thread _timer;
    private int _effectId = 1;

    private L1ParalysisPoison(L1Character cha, int delay, int time) {
        _target = cha;
        _delay = delay;
        _time = time;
        doInfection();
    }

    public static boolean doInfection(L1Character cha, int delay, int time) {
        if (!L1Poison.isValidTarget(cha)) {
            return false;
        }
        cha.setPoison(new L1ParalysisPoison(cha, delay, time));
        return true;
    }

    private void doInfection() {
        sendMessageIfPlayer(_target, 212);
        _target.setPoisonEffect(1);
        //_target.setPoisonIcon(2,_time);
        if (_target instanceof L1PcInstance) {
            L1PcInstance _pc = (L1PcInstance) _target;
            _pc.sendPackets(new S_SkillIconPoison(2, _time / 1000));
        }
        if ((_target instanceof L1PcInstance)) {
            _timer = new ParalysisPoisonTimer();
            GeneralThreadPool.get().execute(_timer);
        }
    }

    public int getEffectId() {
        return _effectId;
    }

    public void cure() {
        _target.setPoisonEffect(0);
        // _target.setPoisonIcon(2,0);
        if (_target instanceof L1PcInstance) {
            L1PcInstance _pc = (L1PcInstance) _target;
            _pc.sendPackets(new S_SkillIconPoison(2, 0));
        }
        _target.setPoison(null);
        if (_timer != null) {
            _timer.interrupt();
        }
        if (_target.hasSkillEffect(1009)) {
            _target.killSkillEffectTimer(1009);
            if (_target instanceof L1PcInstance) {
                L1PcInstance _pc = (L1PcInstance) _target;
                _pc.sendPackets(new S_Paralysis(1, false, 0));
            }
        }
        if (_target.hasSkillEffect(1008)) {
            _target.killSkillEffectTimer(1008);
        }
    }

    private class ParalysisPoisonTimer extends Thread {
        private ParalysisPoisonTimer() {
        }

        public void run() {
            // 檢查目標是否仍然有效
            if (_target == null) {
                return;
            }

            try {
                _target.setSkillEffect(1008, 0);
            } catch (Exception e) {
                // 捕獲可能的並發異常
                System.err.println("Error in ParalysisPoisonTimer.setSkillEffect: " + e.getMessage());
                return;
            }

            try {
                TimeUnit.MILLISECONDS.sleep(_delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            // 再次檢查目標
            if (_target == null) {
                return;
            }

            _effectId = 2;
            _target.setPoisonEffect(2);
            if ((_target instanceof L1PcInstance)) {
                L1PcInstance player = (L1PcInstance) _target;
                if (!player.isDead()) {
                    player.sendPackets(new S_Paralysis(1, true, _time));
                    _timer = new L1ParalysisPoison.ParalysisTimer();
                    GeneralThreadPool.get().execute(_timer);
                    if (isInterrupted()) {
                        _timer.interrupt();
                    }
                }
            }
        }
    }

    private class ParalysisTimer extends Thread {
        private ParalysisTimer() {
        }

        public void run() {
            // 檢查目標是否仍然有效
            if (_target == null) {
                return;
            }

            try {
                _target.killSkillEffectTimer(1008);
                _target.setSkillEffect(1009, 0);
            } catch (Exception e) {
                // 捕獲可能的並發異常
                System.err.println("Error in ParalysisTimer - phase 1: " + e.getMessage());
                return;
            }

            try {
                TimeUnit.MILLISECONDS.sleep(_time);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            // 再次檢查目標
            if (_target == null) {
                return;
            }

            try {
                _target.killSkillEffectTimer(1009);
            } catch (Exception e) {
                System.err.println("Error in ParalysisTimer - phase 2: " + e.getMessage());
                return;
            }

            if ((_target instanceof L1PcInstance)) {
                L1PcInstance player = (L1PcInstance) _target;
                if (!player.isDead()) {
                    player.sendPackets(new S_Paralysis(1, false, 0));
                    cure();
                }
            }
        }
    }
}
