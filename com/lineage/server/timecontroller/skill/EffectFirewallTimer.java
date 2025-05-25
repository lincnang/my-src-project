package com.lineage.server.timecontroller.skill;

import com.lineage.server.model.Instance.L1EffectInstance;
import com.lineage.server.model.Instance.L1EffectType;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1AttackList;
import com.lineage.server.model.L1Character;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldEffect;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class EffectFirewallTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(EffectFirewallTimer.class);
    private static Random _random = new Random();
    private ScheduledFuture<?> _timer;

    private static void firewall(L1EffectInstance effect) {
        try {
            L1PcInstance user = (L1PcInstance) effect.getMaster();
            ArrayList<L1Character> list = WorldEffect.get().getFirewall(effect);
            for (L1Character object : list) {
                if (effect.get_showId() == object.get_showId()) {
                    if ((object instanceof L1PcInstance)) {
                        L1PcInstance tgpc = (L1PcInstance) object;
                        topc(user, tgpc);
                    } else if ((object instanceof L1MonsterInstance)) {
                        L1MonsterInstance tgmob = (L1MonsterInstance) object;
                        tonpc(user, tgmob);
                    }
                }
            }
        } catch (Exception e) {
            _log.error("Npc L1Effect法師技能(火牢)狀態送出時間軸發生異常", e);
            effect.deleteMe();
        }
    }

    private static void tonpc(L1PcInstance user, L1MonsterInstance tgmob) {
        if (dmg0(tgmob)) {
            return;
        }
        double attrDeffence = 0.0D;
        int weakAttr = tgmob.getFire();
        if (weakAttr > 0) {
            attrDeffence = calcAttrResistance(weakAttr);
        }
        int srcDmg = 19 + _random.nextInt(Math.max(user.getInt() / 2, 1));
        int damage = (int) ((1.0D - attrDeffence) * srcDmg);
        damage = Math.max(damage, 0);
        if (damage <= 0) {
            return;
        }
        tgmob.broadcastPacketX10(new S_DoActionGFX(tgmob.getId(), 2));
        tgmob.receiveDamage(user, damage);
    }

    private static void topc(L1PcInstance user, L1PcInstance tgpc) {
        if ((user.getClanid() != 0) && (tgpc.getClanid() == user.getClanid())) {
            return;
        }
        if (tgpc.isSafetyZone()) {
            return;
        }
        if (dmg0(tgpc)) {
            return;
        }
        double attrDeffence = 0.0D;
        int weakAttr = tgpc.getFire();
        if (weakAttr > 0) {
            attrDeffence = calcAttrResistance(weakAttr);
        }
        int srcDmg = 19 + _random.nextInt(Math.max(user.getInt() / 2, 1));
        int damage = (int) ((1.0D - attrDeffence) * srcDmg);
        damage = Math.max(damage, 0);
        boolean dmgX2 = false;
        if ((!tgpc.getSkillisEmpty()) && (tgpc.getSkillEffect().size() > 0)) {
            try {
                for (Integer skillid : L1AttackList.SKD3.keySet()) {
                    if (tgpc.hasSkillEffect(skillid)) {
                        Integer integer = (Integer) L1AttackList.SKD3.get(skillid);
                        if (integer != null) {
                            if (integer.equals(skillid)) {
                                dmgX2 = true;
                            } else {
                                damage += integer;
                            }
                        }
                    }
                }
            } catch (ConcurrentModificationException localConcurrentModificationException) {
            } catch (Exception e) {
                _log.error(e.getLocalizedMessage(), e);
            }
        }
        if (dmgX2) {
            damage /= 2;
        }
        if (damage <= 0) {
            return;
        }
        tgpc.sendPacketsAll(new S_DoActionGFX(tgpc.getId(), 2));
        tgpc.receiveDamage(user, damage, false, true);
    }

    private static double calcAttrResistance(int resist) {
        int resistFloor = (int) (0.16D * Math.abs(resist));
        if (resist >= 0) {
            resistFloor *= 1;
        } else {
            resistFloor *= -1;
        }
        double attrDeffence = resistFloor / 32.0D;
        return attrDeffence;
    }

    private static boolean dmg0(L1Character character) {
        try {
            if (character == null) {
                return false;
            }
            if (character.getSkillisEmpty()) {
                return false;
            }
            if (character.getSkillEffect().size() <= 0) {
                return false;
            }
            for (Integer key : character.getSkillEffect()) {
                Integer integer = (Integer) L1AttackList.SKM0.get(key);
                if (integer != null) {
                    return true;
                }
            }
        } catch (ConcurrentModificationException localConcurrentModificationException) {
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
            return false;
        }
        return false;
    }

    public void start() {
        int timeMillis = 1650;
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
    }

    public void run() {
        try {
            Collection<?> allNpc = WorldEffect.get().all();
            if (allNpc.isEmpty()) {
                return;
            }
            for (Object o : allNpc) {
                L1EffectInstance effect = (L1EffectInstance) o;
                if (effect.effectType() == L1EffectType.isFirewall) {
                    firewall(effect);
                    TimeUnit.MILLISECONDS.sleep(1L);
                }
            }
        } catch (Exception e) {
            _log.error("Npc L1Effect法師技能(火牢)狀態送出時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            EffectFirewallTimer firewallTimer = new EffectFirewallTimer();
            firewallTimer.start();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.skill.EffectFirewallTimer JD-Core Version:
 * 0.6.2
 */