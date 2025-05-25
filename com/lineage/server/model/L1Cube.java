package com.lineage.server.model;

import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

public class L1Cube extends TimerTask {
    private static final Log _log = LogFactory.getLog(L1Cube.class);
    private final L1Character _effect;
    private final L1Character _cha;
    private final int _skillId;
    private ScheduledFuture<?> _future = null;
    private int _timeCounter = 0;

    public L1Cube(L1Character effect, L1Character cha, int skillId) {
        _effect = effect;
        _cha = cha;
        _skillId = skillId;
    }

    public void run() {
        try {
            if (_cha.isDead()) {
                stop();
                return;
            }
            if (!_cha.hasSkillEffect(_skillId)) {
                stop();
                return;
            }
            _timeCounter += 1;
            giveEffect();
        } catch (Throwable e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void begin() {
        _future = GeneralThreadPool.get().scheduleAtFixedRate(this, 900L, 1000L);
    }

    public void stop() {
        if (_future != null) {
            _future.cancel(false);
        }
    }

    public void giveEffect() {
        switch (_skillId) {
            case 1019:
                if (_timeCounter % 4 != 0) {
                    return;
                }
                if (_cha.hasSkillEffect(4000)) {
                    return;
                }
                if (_cha.hasSkillEffect(78)) {
                    return;
                }
                if (_cha.hasSkillEffect(50)) {
                    return;
                }
                if (_cha.hasSkillEffect(157)) {
                    return;
                }
                if ((_cha instanceof L1PcInstance)) {
                    L1PcInstance pc = (L1PcInstance) _cha;
                    pc.sendPacketsAll(new S_DoActionGFX(pc.getId(), 2));
                    pc.receiveDamage(_effect, 10.0D, false, true);
                } else if ((_cha instanceof L1MonsterInstance)) {
                    L1MonsterInstance mob = (L1MonsterInstance) _cha;
                    mob.broadcastPacketX10(new S_DoActionGFX(mob.getId(), 2));
                    mob.receiveDamage(_effect, 10);
                }
                break;
            case 1021:
                if (_timeCounter % 4 != 0) {
                    return;
                }
                if (_cha.hasSkillEffect(4000)) {
                    return;
                }
                if (_cha.hasSkillEffect(78)) {
                    return;
                }
                if (_cha.hasSkillEffect(50)) {
                    return;
                }
                if (_cha.hasSkillEffect(157)) {
                    return;
                }
                if ((_cha instanceof L1PcInstance)) {
                    L1PcInstance pc = (L1PcInstance) _cha;
                    pc.setSkillEffect(4000, 1000);
                    pc.sendPackets(new S_Paralysis(6, true));
                } else if ((_cha instanceof L1MonsterInstance)) {
                    L1MonsterInstance mob = (L1MonsterInstance) _cha;
                    mob.setSkillEffect(4000, 1000);
                    // mob.setParalyzed(true);
                    mob.setPassispeed(0);
                }
                break;
            case 1023:
                _cha.setSkillEffect(1024, 4000);
                break;
            case 1025:
                if (_timeCounter % 4 == 0) {
                    int newMp = _cha.getCurrentMp() + 5;
                    if (newMp < 0) {
                        newMp = 0;
                    }
                    _cha.setCurrentMp(newMp);
                }
                if (_timeCounter % 5 == 0) {
                    if ((_cha instanceof L1PcInstance)) {
                        L1PcInstance pc = (L1PcInstance) _cha;
                        pc.receiveDamage(_effect, 25.0D, false, true);
                    } else if ((_cha instanceof L1MonsterInstance)) {
                        L1MonsterInstance mob = (L1MonsterInstance) _cha;
                        mob.receiveDamage(_effect, 25);
                    }
                }
                break;
            case 1020:
            case 1022:
            case 1024:
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.L1Cube JD-Core Version: 0.6.2
 */