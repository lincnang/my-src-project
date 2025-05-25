package com.lineage.server.model;

import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.ItemSublimationTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.CharItemSublimation;
import com.lineage.server.templates.L1ItemSublimation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

/**
 * 昇華系統 - 傷害處理
 */
public class L1AttackItemSub {

    private static final Log _log = LogFactory.getLog(L1AttackItemSub.class);
    private static final Random _random = new Random();

    private final L1PcInstance _pc;
    private final L1Character _target;
    private L1PcInstance _pc2;
    private L1NpcInstance _npc;
    private L1PcInstance _targetPc;

    private final L1ItemSublimation sublimationItem;

    public L1AttackItemSub(final L1PcInstance pc, final L1Character target, final L1Character target2,
                           final CharItemSublimation charItemSublimation) {
        _pc = pc;
        _target = target;

        if (target instanceof L1PcInstance) {
            _targetPc = (L1PcInstance) target;
        }

        if (target2 instanceof L1NpcInstance) {
            _npc = (L1NpcInstance) target2;
        } else if (target2 instanceof L1PcInstance) {
            _pc2 = (L1PcInstance) target2;
        }

        sublimationItem = ItemSublimationTable.getItemSublimation(charItemSublimation);
    }

    public double add_dmg_forPc(final double dmg, final boolean magicDmg) {
        double resultDmg = dmg;
        boolean triggered = false;

        try {
            final int chance = _random.nextInt(100) + 1;
            final int triggerRate = sublimationItem.getTriggerChance();

            if (chance <= triggerRate) {

                if (magicDmg && sublimationItem.getMagicDmg() != 0) {
                    resultDmg *= sublimationItem.getMagicDmg();
                    triggered = true;
                } else {
                    if (sublimationItem.getDamage() != 0) {
                        resultDmg *= sublimationItem.getDamage();
                        triggered = true;
                    }

                    if (sublimationItem.getDmgChanceHp() > 0) {
                        int healHp = (int) (dmg * sublimationItem.getDmgChanceHp() / 100.0);
                        resultDmg -= healHp;
                        _pc.setCurrentHp(_pc.getCurrentHp() + healHp);
                        triggered = true;

                    }

                    if (sublimationItem.getDmgChanceMp() > 0) {
                        int healMp = (int) (dmg * sublimationItem.getDmgChanceMp() / 100.0);
                        resultDmg -= healMp;
                        _pc.setCurrentMp(_pc.getCurrentMp() + healMp);
                        triggered = true;

                    }
                }

                if (triggered) {
                    messageAndGfxid(sublimationItem);
                }
            }

        } catch (Exception e) {
            _log.error("昇華玩家效果發生錯誤", e);
        }

        return Math.max(resultDmg, 0);
    }

    public double add_dmg_forTarget(final double dmg, final boolean magicDmg) {
        double resultDmg = dmg;
        boolean triggered = false;

        try {
            final int chance = _random.nextInt(100) + 1;
            final int triggerRate = sublimationItem.getTriggerChance();

            if (chance <= triggerRate) {
                if (magicDmg) {
                    if (sublimationItem.isWithstandMagic()) {
                        resultDmg = 0;
                        triggered = true;
                    }
                    if (sublimationItem.isReturnMagic()) {
                        resultDmg = 0;
                        attackReflect(dmg, true);
                        triggered = true;
                    }
                } else {
                    if (sublimationItem.isWithstandDmg()) {
                        resultDmg = 0;
                        triggered = true;
                    }
                    if (sublimationItem.getReturnChanceHp() > 0) {
                        int reflectHp = (int) (dmg * sublimationItem.getReturnChanceHp() / 100.0);
                        resultDmg -= reflectHp;
                        _targetPc.setCurrentHp(_targetPc.getCurrentHp() + reflectHp);
                        triggered = true;
                    }
                    if (sublimationItem.getReturnChanceMp() > 0) {
                        int reflectMp = (int) (dmg * sublimationItem.getReturnChanceMp() / 100.0);
                        resultDmg -= reflectMp;
                        _targetPc.setCurrentMp(_targetPc.getCurrentMp() + reflectMp);
                        triggered = true;
                    }
                    if (sublimationItem.isReturnDmg()) {
                        resultDmg = 0;
                        attackReflect(dmg, false);
                        triggered = true;
                    }
                }

                if (triggered) {
                    messageAndGfxid(sublimationItem);
                }
            }

        } catch (Exception e) {
            _log.error("昇華目標效果發生錯誤", e);
        }

        return Math.max(resultDmg, 0);
    }

    private void attackReflect(double dmg, boolean magic) {
        if (_pc2 != null) {
            _pc2.sendPackets(new S_DoActionGFX(_pc2.getId(), ActionCodes.ACTION_Damage));
            _pc2.broadcastPacketX8(new S_DoActionGFX(_pc2.getId(), ActionCodes.ACTION_Damage));
            _pc2.receiveDamage(_targetPc, dmg, magic, true);
        } else if (_npc != null) {
            _npc.broadcastPacketX8(new S_DoActionGFX(_npc.getId(), ActionCodes.ACTION_Damage));
            _npc.receiveDamage(_targetPc, (int) dmg);
        }
    }

    private void messageAndGfxid(final L1ItemSublimation sub) {
        if (sub.getMessage() != null) {
            int type = sub.getMessageType();
            String msg = sub.getMessage();

            switch (type) {
                case 0:
                    if (_pc != null) _pc.sendPackets(new S_ServerMessage(166, msg));
                    else if (_targetPc != null) _targetPc.sendPackets(new S_ServerMessage(166, msg));
                    break;
                case 1:
                    if (_pc2 != null) _pc2.sendPackets(new S_ServerMessage(166, msg));
                    else if (_targetPc != null) _targetPc.sendPackets(new S_ServerMessage(166, msg));
                    break;
            }
        }

        if (sub.getGfxid() != 0) {
            int gfxid = sub.getGfxid();
            int type = sub.getGfxidType();

            switch (type) {
                case 0:
                    if (_pc2 != null) {
                        _pc2.sendPackets(new S_SkillSound(_pc2.getId(), gfxid));
                    } else if (_targetPc != null) {
                        _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), gfxid));
                    }

                    if (_npc != null) {
                        _npc.broadcastPacketX8(new S_SkillSound(_npc.getId(), gfxid));
                    } else {
                        _target.broadcastPacketX8(new S_SkillSound(_target.getId(), gfxid));
                    }
                    break;

                case 1:
                    if (_pc != null) {
                        _pc.sendPackets(new S_SkillSound(_pc.getId(), gfxid));
                        _pc.broadcastPacketX8(new S_SkillSound(_pc.getId(), gfxid));
                    } else if (_targetPc != null) {
                        _targetPc.sendPackets(new S_SkillSound(_targetPc.getId(), gfxid));
                        _targetPc.broadcastPacketX8(new S_SkillSound(_targetPc.getId(), gfxid));
                    }
                    break;
            }
        }
    }
}
