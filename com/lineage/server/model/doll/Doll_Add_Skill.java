package com.lineage.server.model.doll;

import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.templates.L1Skills;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.lineage.server.model.skill.L1SkillId.*;

/**
 * 娃娃效果:對PC施展技能(副助) 參數：TYPE1(技能編號)
 */
public class Doll_Add_Skill extends L1DollExecutor {
    private static final Log _log = LogFactory.getLog(Doll_Add_Skill.class);
    private int _int1;// 值1
    private String _note;

    /**
     * 娃娃效果:對PC施展技能(副助)
     */
    public Doll_Add_Skill() {
    }

    public static L1DollExecutor get() {
        return new Doll_Add_Skill();
    }

    @Override
    public void set_power(int int1, int int2, int int3) {
        try {
            _int1 = int1;
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public String get_note() {
        return _note;
    }

    public void set_note(String note) {
        _note = note;
    }

    @Override
    public void setDoll(L1PcInstance pc) {
        try {
            L1ItemInstance tgItem = null;
            switch (_int1) {
                //			case HOLY_WEAPON:
                //			case ENCHANT_WEAPON:
                case BLESS_WEAPON:
                case SHADOW_FANG:
                    tgItem = pc.getWeapon();
                    if (tgItem == null) {
                        return;
                    }
                    break;
                case BLESSED_ARMOR:// 鎧甲護持
                    tgItem = pc.getInventory().getItemEquipped(2, 2);// 盔甲
                    if (tgItem == null) {
                        return;
                    }
                    break;
                default:
                    break;
            }
            boolean is = false;
            if (tgItem != null) {
                if (!tgItem.isRunning()) {
                    is = true;
                }
            } else {
                if (!pc.hasSkillEffect(_int1)) {
                    is = true;
                }
            }
            if (is) {
                final L1Skills skill = SkillsTable.get().getTemplate(_int1);
                final L1SkillUse skillUse = new L1SkillUse();
                skillUse.handleCommands(pc, _int1, pc.getId(), pc.getX(), pc.getY(), skill.getBuffDuration(), L1SkillUse.TYPE_GMBUFF);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void removeDoll(L1PcInstance pc) {
        try {
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public boolean is_reset() {
        return true;
    }
}
