package com.lineage.server.model;

import com.lineage.data.event.SubItemSet;
import com.lineage.server.ActionCodes;
import com.lineage.server.model.Instance.L1DollInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_SkillSound;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Random;

public abstract class L1MagicMode {
    protected static final int PC_PC = 1;
    protected static final int PC_NPC = 2;
    protected static final int NPC_PC = 3;
    protected static final int NPC_NPC = 4;

    protected static final Random _random = new Random();
    private static final Log _log = LogFactory.getLog(L1MagicMode.class);
    protected int _calcType;

    protected L1PcInstance _pc = null;
    protected L1PcInstance _targetPc = null;

    protected L1Character _target = null;

    protected L1NpcInstance _npc = null;
    protected L1NpcInstance _targetNpc = null;
    protected int _leverage = 10;

    protected static boolean dmg0(L1Character character) {
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

    protected static double getDamageUpByClan(L1PcInstance pc) {
        double dmg = 0.0D;
        try {
            if (pc == null) {
                return 0.0D;
            }
            dmg += pc.get_magic_modifier_dmg();
            L1Clan clan = pc.getClan();
            if (clan == null) {
                return dmg;
            }
            if (clan.isClanskill()) {
                if (pc.get_other().get_clanskill() == 4) {
                    int clanMan = clan.getOnlineClanMemberSize50();
                    dmg += 0.25D * clanMan;
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
            return 0.0D;
        }
        return dmg;
    }

    protected static double getDamageReductionByClan(L1PcInstance targetPc) {
        double dmg = 0.0D;
        try {
            if (targetPc == null) {
                return 0.0D;
            }
            dmg += targetPc.get_magic_reduction_dmg();
            L1Clan clan = targetPc.getClan();
            if (clan == null) {
                return 0.0D;
            }
            if (clan.isClanskill()) {
                if (targetPc.get_other().get_clanskill() == 8) {
                    int clanMan = clan.getOnlineClanMemberSize50();
                    dmg += 0.25D * clanMan;
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
            return 0.0D;
        }
        return dmg;
    }

    protected int getLeverage() {
        return _leverage;
    }

    public void setLeverage(int i) {
        _leverage = i;
    }

    /**
     * 計算施展者額外增加魔攻
     *
     */
    protected int getTargetSp() {
        int sp = 0;
        switch (_calcType) {
            case PC_PC:
            case PC_NPC:
                sp = _pc.getSp() - _pc.getTrueSp();
                switch (_pc.guardianEncounter()) {
                    case 3:// 邪惡的守護 Lv.1
                        sp++;
                        break;
                    case 4:// 邪惡的守護 Lv.2
                        sp += 2;
                        break;
                    case 5:// 邪惡的守護 Lv.3
                        sp += 3;
                        break;
                }
                break;
            case NPC_NPC:
            case NPC_PC:
                sp = _npc.getSp() - _npc.getTrueSp();
                break;
        }
        return sp;
    }

    /**
     * 取得對方魔防
     */
    protected int getTargetMr() {
        int mr = 0;
        switch (_calcType) {
            case 1:
            case 3:
                if (_targetPc == null) {
                    return 0;
                }
                mr = _targetPc.getMr();
                switch (_targetPc.guardianEncounter()) {
                    case 0: // 正義的守護 Lv.1
                        mr += 3;
                        break;
                    case 1: // 正義的守護 Lv.2
                        mr += 6;
                        break;
                    case 2: // 正義的守護 Lv.3
                        mr += 9;
                        break;
                }
                break;
            case 2:
            case 4:
                if (_targetNpc == null) {
                    return 0;
                }
                mr = _targetNpc.getMr();
        }
        return mr;
    }

    /**
     * 完全閃避率計算 亂數1000
     */
    protected boolean calcEvasion() {
        if (_targetPc == null) {
            return false;
        }
        int ev = _targetPc.get_evasion();
        if (ev == 0) {
            return false;
        }
        int rnd = _random.nextInt(1000) + 1;
        if (rnd <= ev) {
            if (!_targetPc.getDolls().isEmpty()) {
                for (L1DollInstance doll : _targetPc.getDolls().values()) {
                    doll.show_action(2);
                }
            }
            return true;
        }
        return false;
    }

    protected double calcAttrResistance(int attr) {
        int resist = 0;
        switch (_calcType) {
            case 1:
            case 3:
                if (_targetPc == null) {
                    return 0.0D;
                }
                switch (attr) {
                    case 1:
                        resist = _targetPc.getEarth();
                        break;
                    case 2:
                        resist = _targetPc.getFire();
                        break;
                    case 4:
                        resist = _targetPc.getWater();
                        break;
                    case 8:
                        resist = _targetPc.getWind();
                    case 3:
                    case 5:
                    case 6:
                    case 7:
                }
            case 2:
            case 4:
                if (_targetNpc == null) {
                    return 0.0D;
                }
                switch (attr) {
                    case 1:
                        resist = _targetNpc.getEarth();
                        break;
                    case 2:
                        resist = _targetNpc.getFire();
                        break;
                    case 4:
                        resist = _targetNpc.getWater();
                        break;
                    case 8:
                        resist = _targetNpc.getWind();
                    case 3:
                    case 5:
                    case 6:
                    case 7:
                }
        }
        int resistFloor = (int) (0.16D * Math.abs(resist));
        if (resist >= 0) {
            resistFloor *= 1;
        } else {
            resistFloor *= -1;
        }
        double attrDeffence = resistFloor / 32.0D;
        return attrDeffence;
    }

    public abstract boolean calcProbabilityMagic(int paramInt);

    public abstract int calcMagicDamage(int paramInt);

    public abstract int calcHealing(int paramInt);

    public abstract void commit(int paramInt1, int paramInt2);

    protected double calcSubMagic(final double dmg, final L1Character cha, final L1Character tgcha) {
        double newdmg = dmg;
        if (cha != null && _pc != null && _target != null) {
            for (final L1ItemInstance item : _pc.getInventory().getItems()) {
                if (newdmg > 0.0 && item.isEquipped()) {
                    if (SubItemSet.START && item.getSublimation() != null) {
                        final L1AttackItemSub sub = new L1AttackItemSub(_pc, _target, null, item.getSublimation());
                        newdmg = sub.add_dmg_forPc(newdmg, false);
                    }
                }
            }
            if (!_pc.getDolls().isEmpty() && newdmg > 0.0) {
                for (final L1DollInstance doll : _pc.getDolls().values()) {
                    if (SubItemSet.START && doll.getInstance().getSublimation() != null) {
                        final L1AttackItemSub sub = new L1AttackItemSub(_pc, _target, null, doll.getInstance().getSublimation());
                        newdmg = sub.add_dmg_forPc(newdmg, false);
                    }
                }
            }
        }

        if (SubItemSet.START && tgcha != null && _targetPc != null) {
            for (final L1ItemInstance item : _targetPc.getInventory().getItems()) {
                if (item.isEquipped()) {
                    if (item.getSublimation() != null) {
                        L1AttackItemSub sub = null;
                        if (_pc != null) {
                            sub = new L1AttackItemSub(null, _targetPc, _pc, item.getSublimation());
                        } else {
                            sub = new L1AttackItemSub(null, _targetPc, _npc, item.getSublimation());
                        }
                        newdmg = sub.add_dmg_forTarget((int) newdmg, false);
                    }
                }
            }

            if (!_targetPc.getDolls().isEmpty()) {
                final Collection<L1DollInstance> dollList = _targetPc.getDolls().values();
                for (final L1DollInstance doll : dollList) {
                    if (doll.getInstance().getSublimation() != null) {
                        L1AttackItemSub sub = null;
                        if (_pc != null) {
                            sub = new L1AttackItemSub(null, _targetPc, _pc, doll.getInstance().getSublimation());
                        } else {
                            sub = new L1AttackItemSub(null, _targetPc, _npc, doll.getInstance().getSublimation());
                        }
                        newdmg = sub.add_dmg_forTarget(newdmg, false);
                    }
                }
            }
        }
        return newdmg;
    }


    public void commitTitan(final int dmg) {
        if (dmg == 0) {
            return;
        }
        if (_calcType == PC_PC) {
            if ((_pc != null) && (_targetPc != null)) {
                _pc.receiveDamage(_targetPc, dmg, false, false);
            }
        } else if (_calcType == NPC_PC) {
            if ((_npc != null) && (_targetPc != null)) {
                _npc.receiveDamage(_targetPc, dmg);
            }
        }
    }

    public void actionTitan() {
        final int targetId = _targetPc.getId();
        final int target_x = _targetPc.getX();
        final int target_y = _targetPc.getY();
        if (_calcType == PC_PC) {
            _pc.setHeading(_pc.targetDirection(target_x, target_y));
            _pc.sendPacketsAll(new S_DoActionGFX(_pc.getId(), ActionCodes.ACTION_Damage));
            _pc.sendPacketsAll(new S_SkillSound(targetId, 12559));
        } else if (_calcType == NPC_PC) {
            _npc.setHeading(_npc.targetDirection(target_x, target_y));
            _npc.broadcastPacketAll(new S_DoActionGFX(_npc.getId(), ActionCodes.ACTION_Damage));
            _npc.broadcastPacketAll(new S_SkillSound(targetId, 12559));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.L1MagicMode JD-Core Version: 0.6.2
 */