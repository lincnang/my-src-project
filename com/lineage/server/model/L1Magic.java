package com.lineage.server.model;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1Magic {
    private static final Log _log = LogFactory.getLog(L1Magic.class);
    private static L1MagicMode _magicMode;

    public L1Magic(L1Character attacker, L1Character target) {
        if (attacker == null) {
            return;
        }
        try {
            if ((attacker instanceof L1PcInstance)) {
                L1PcInstance pc = (L1PcInstance) attacker;
                _magicMode = new L1MagicPc(pc, target);
            } else {
                L1NpcInstance npc = (L1NpcInstance) attacker;
                _magicMode = new L1MagicNpc(npc, target);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void setLeverage(int i) {
        _magicMode.setLeverage(i);
    }

    /**
     * 傷害資訊送出
     *
     * @param damage
     * @param drainMana
     */
    public void commit(int damage, int drainMana) {
        _magicMode.commit(damage, drainMana);
    }

    /**
     * 計算機率並返回是否成功
     *
     * @param skillId
     * @return
     */
    public boolean calcProbabilityMagic(int skillId) {
        return _magicMode.calcProbabilityMagic(skillId);
    }

    /**
     * 計算魔法傷害
     *
     * @param skillId
     * @return
     */
    public int calcMagicDamage(int skillId) {
        return _magicMode.calcMagicDamage(skillId);
    }

    /**
     * 計算治療量
     *
     * @param skillId
     * @return
     */
    public int calcHealing(int skillId) {
        return _magicMode.calcHealing(skillId);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.L1Magic JD-Core Version: 0.6.2
 */