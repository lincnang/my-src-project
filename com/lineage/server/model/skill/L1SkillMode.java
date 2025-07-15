package com.lineage.server.model.skill;
//*import static com.lineage.server.model.skill.L1SkillId.ENCHANT_WEAPON;

import com.lineage.server.model.skill.skillmode.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

import static com.lineage.server.model.skill.L1SkillId.*;

public class L1SkillMode {
    private static final Log _log = LogFactory.getLog(L1SkillMode.class);
    private static final Map<Integer, SkillMode> _skillMode = new HashMap<>();
    private static L1SkillMode _instance;

    public static L1SkillMode get() {
        if (_instance == null) {
            _instance = new L1SkillMode();
        }
        return _instance;
    }

    /**
     * 不會被相消的技能
     *
     */
    public boolean isNotCancelable(int skillNum) {
        return /*(skillNum == ENCHANT_WEAPON) || */(skillNum == BLESSED_ARMOR) ||
                (skillNum == ADVANCE_SPIRIT) || (skillNum == SHOCK_STUN) || (skillNum == KINGDOM_STUN) ||
                (skillNum == REDUCTION_ARMOR) || (skillNum == BOUNCE_ATTACK) || (skillNum == SOLID_CARRIAGE) ||
                (skillNum == COUNTER_BARRIER) || (skillNum == UNCANNY_DODGE) || (skillNum == SHADOW_ARMOR) || (skillNum == ARMOR_BREAK) ||
                (skillNum == SHADOW_FANG) || (skillNum == DRESS_MIGHTY) || (skillNum == DRESS_DEXTERITY) || (skillNum == DRESS_EVASION) ||
                (skillNum == AWAKEN_ANTHARAS) || (skillNum == AWAKEN_FAFURION) || (skillNum == AWAKEN_VALAKAS) || (skillNum == ILLUSION_OGRE) ||
                (skillNum == ILLUSION_LICH) || (skillNum == ILLUSION_DIA_GOLEM) || (skillNum == ILLUSION_AVATAR) || (skillNum == BONE_BREAK) ||
                (skillNum == STATUS_BRAVE3);
    }

    public void load() {
        try {
            _skillMode.put(Shadow_ACCELERATION, new HASTE3X());   //暗影加速
            _skillMode.put(41, new HASTE());   //加速術
            _skillMode.put(59, new CANCELLATION()); //魔法相消術
            _skillMode.put(9, new CURE_POISON()); //解毒術
            _skillMode.put(35, new REMOVE_CURSE()); //聖潔之光
            _skillMode.put(64, new SHAPE_CHANGE()); //變形術
//            _skillMode.put(61, new RESURRECTION()); //返生術
            _skillMode.put(75, new GREATER_RESURRECTION()); //終極返生術
            _skillMode.put(57, new ADVANCE_SPIRIT()); //靈魂昇華
            //			_skillMode.put(Integer.valueOf(33), new CURSE_PARALYZE());  //木乃伊
            _skillMode.put(4001, new CURSE_PARALYZE());
            //			_skillMode.put(Integer.valueOf(20), new CURSE_BLIND()); //闇盲咒術
            _skillMode.put(40, new CURSE_BLIND()); //黑闇之影
            //			_skillMode.put(Integer.valueOf(71), new DECAY_POTION()); //藥水霜化術
            _skillMode.put(Phantom_Blade, new Phantom_Blade()); //幻影之刃
            //			_skillMode.put(Integer.valueOf(116), new CALL_CLAN());  //呼喚盟友
            //			_skillMode.put(Integer.valueOf(118), new RUN_CLAN());  //援護盟友
            _skillMode.put(113, new TRUE_TARGET()); //精準目標
            _skillMode.put(HAND_DARKNESS, new HAND_DARKNESS()); //黑暗之手
            _skillMode.put(LICH_CHANGE_LOCATION, new LICH_CHANGE_LOCATION());
            _skillMode.put(PRIDE, new PRIDE());// 騎士榮耀
            _skillMode.put(87, new SHOCK_STUN()); //衝擊之暈
            _skillMode.put(119, new KINGDOM_STUN()); //王者之劍
            _skillMode.put(EMPIRE, new EMPIRE());// 暈眩之劍

            _skillMode.put(BOUNCE_ATTACK, new BOUNCE_ATTACK()); //尖刺盔甲
            _skillMode.put(SOLID_CARRIAGE, new SOLID_CARRIAGE()); //堅固防護
            _skillMode.put(Counter_attack, new Counter_attack()); //反制攻擊


            _skillMode.put(Star_Arrow, new Skill_MeteorArrow()); // 流星之箭 範圍傷害
            _skillMode.put(Elf_Boots, new Elf_Boots());// 精靈之靴

            _skillMode.put(165, new CALL_OF_NATURE()); //大地召喚
            _skillMode.put(133, new ELEMENTAL_FALL_DOWN());
            _skillMode.put(130, new BODY_TO_MIND());
            _skillMode.put(146, new BLOODY_SOUL());
            _skillMode.put(132, new TRIPLE_ARROW());
            _skillMode.put(131, new TELEPORT_TO_MATHER());
            _skillMode.put(AQUA_PROTECTER, new AQUA_PROTECTER()); //水之防護
            _skillMode.put(162, new GREATER_ELEMENTAL());
//            _skillMode.put(154, new LESSER_ELEMENTAL());//招喚小精靈
            _skillMode.put(EIF_EMPIRE, new EIF_EMPIRE()); // 精靈之暈
            _skillMode.put(167, new WIND_SHACKLE());
            _skillMode.put(155, new FIRE_BLESS());
            _skillMode.put(157, new EARTH_BIND());
            _skillMode.put(DRESS_EVASION, new DRESS_EVASION());
            _skillMode.put(UNCANNY_DODGE, new UNCANNY_DODGE());
            _skillMode.put(103, new DARK_BLIND());
            _skillMode.put(SHADOW_ARMOR, new SHADOW_ARMOR());
            _skillMode.put(AWAKEN_ANTHARAS, new AWAKEN_ANTHARAS());
            _skillMode.put(AWAKEN_FAFURION, new AWAKEN_FAFURION());
            _skillMode.put(AWAKEN_VALAKAS, new AWAKEN_VALAKAS());
            _skillMode.put(FOE_SLAYER, new FOE_SLAYER());
            _skillMode.put(186, new BLOODLUST());
            _skillMode.put(188, new RESIST_FEAR());
            _skillMode.put(192, new THUNDER_GRAB());
            _skillMode.put(74, new SUMMON_MONSTER());// 召喚寵物
            _skillMode.put(202, new CONFUSION());
            _skillMode.put(212, new PHANTASM());
            _skillMode.put(217, new PANIC());
            _skillMode.put(216, new INSIGHT());
            _skillMode.put(208, new BONE_BREAK());
            _skillMode.put(207, new MIND_BREAK());
            _skillMode.put(219, new ILLUSION_AVATAR());
            _skillMode.put(209, new ILLUSION_LICH());
            _skillMode.put(201, new MIRROR_IMAGE());
            _skillMode.put(GIGANTIC, new GIGANTIC());
            _skillMode.put(Blood_strength, new Blood_strength());
            _skillMode.put(61, new IMMUNE_TO_HARM()); //SRC0808
            _skillMode.put(79, new IMMUNE_TO_HARM2()); //集體聖結界
            _skillMode.put(174, new STRIKER_GALE()); //SRC0808
            // _skillMode.put(Integer.valueOf(218), new JOY_OF_PAIN());
            _skillMode.put(4000, new STATUS_FREEZE());
            _skillMode.put(6683, new DRAGONEYE_VALAKAS());
            _skillMode.put(6684, new DRAGONEYE_ANTHARAS());
            _skillMode.put(6685, new DRAGONEYE_FAFURION());
            _skillMode.put(6686, new DRAGONEYE_LINDVIOR());
            _skillMode.put(6687, new DRAGONEYE_LIFE());
            _skillMode.put(6688, new DRAGONEYE_BIRTH());
            _skillMode.put(6689, new DRAGONEYE_FIGURE());
            _skillMode.put(4009, new ADLV80_1());
            _skillMode.put(4010, new ADLV80_2());
            _skillMode.put(4018, new ADLV80_3());
            _skillMode.put(11060, new KIRTAS_BARRIER1());
            _skillMode.put(11059, new KIRTAS_BARRIER2());
            _skillMode.put(11058, new KIRTAS_BARRIER3());
            _skillMode.put(11057, new KIRTAS_BARRIER4());
            _skillMode.put(11061, new LINDVIOR_SKY_SPIKED());
            _skillMode.put(4500, new DS_GX00());
            _skillMode.put(4501, new DS_GX01());
            _skillMode.put(4502, new DS_GX02());
            _skillMode.put(4503, new DS_GX03());
            _skillMode.put(4504, new DS_GX04());
            _skillMode.put(4505, new DS_GX05());
            _skillMode.put(4506, new DS_GX06());
            _skillMode.put(4507, new DS_GX07());
            _skillMode.put(4508, new DS_GX08());
            _skillMode.put(4509, new DS_GX09());
            _skillMode.put(4510, new DS_AX00());
            _skillMode.put(4511, new DS_AX01());
            _skillMode.put(4512, new DS_AX02());
            _skillMode.put(4513, new DS_AX03());
            _skillMode.put(4514, new DS_AX04());
            _skillMode.put(4515, new DS_AX05());
            _skillMode.put(4516, new DS_AX06());
            _skillMode.put(4517, new DS_AX07());
            _skillMode.put(4518, new DS_AX08());
            _skillMode.put(4519, new DS_AX09());
            _skillMode.put(4520, new DS_WX00());
            _skillMode.put(4521, new DS_WX01());
            _skillMode.put(4522, new DS_WX02());
            _skillMode.put(4523, new DS_WX03());
            _skillMode.put(4524, new DS_WX04());
            _skillMode.put(4525, new DS_WX05());
            _skillMode.put(4526, new DS_WX06());
            _skillMode.put(4527, new DS_WX07());
            _skillMode.put(4528, new DS_WX08());
            _skillMode.put(4529, new DS_WX09());
            _skillMode.put(4530, new DS_ASX00());
            _skillMode.put(4531, new DS_ASX01());
            _skillMode.put(4532, new DS_ASX02());
            _skillMode.put(4533, new DS_ASX03());
            _skillMode.put(4534, new DS_ASX04());
            _skillMode.put(4535, new DS_ASX05());
            _skillMode.put(4536, new DS_ASX06());
            _skillMode.put(4537, new DS_ASX07());
            _skillMode.put(4538, new DS_ASX08());
            _skillMode.put(4539, new DS_ASX09());
            _skillMode.put(4401, new BS_GX01());
            _skillMode.put(4402, new BS_GX02());
            _skillMode.put(4403, new BS_GX03());
            _skillMode.put(4404, new BS_GX04());
            _skillMode.put(4405, new BS_GX05());
            _skillMode.put(4406, new BS_GX06());
            _skillMode.put(4407, new BS_GX07());
            _skillMode.put(4408, new BS_GX08());
            _skillMode.put(4409, new BS_GX09());
            _skillMode.put(4411, new BS_AX01());
            _skillMode.put(4412, new BS_AX02());
            _skillMode.put(4413, new BS_AX03());
            _skillMode.put(4414, new BS_AX04());
            _skillMode.put(4415, new BS_AX05());
            _skillMode.put(4416, new BS_AX06());
            _skillMode.put(4417, new BS_AX07());
            _skillMode.put(4418, new BS_AX08());
            _skillMode.put(4419, new BS_AX09());
            _skillMode.put(4421, new BS_WX01());
            _skillMode.put(4422, new BS_WX02());
            _skillMode.put(4423, new BS_WX03());
            _skillMode.put(4424, new BS_WX04());
            _skillMode.put(4425, new BS_WX05());
            _skillMode.put(4426, new BS_WX06());
            _skillMode.put(4427, new BS_WX07());
            _skillMode.put(4428, new BS_WX08());
            _skillMode.put(4429, new BS_WX09());
            _skillMode.put(4431, new BS_ASX01());
            _skillMode.put(4432, new BS_ASX02());
            _skillMode.put(4433, new BS_ASX03());
            _skillMode.put(4434, new BS_ASX04());
            _skillMode.put(4435, new BS_ASX05());
            _skillMode.put(4436, new BS_ASX06());
            _skillMode.put(4437, new BS_ASX07());
            _skillMode.put(4438, new BS_ASX08());
            _skillMode.put(4439, new BS_ASX09());
            _skillMode.put(6797, new DRAGON_BLOOD1());
            _skillMode.put(6798, new DRAGON_BLOOD2());
            _skillMode.put(6799, new DRAGON_BLOOD3());
            _skillMode.put(40001, new BAPHOMET());
            _skillMode.put(4018, new MOVE_STOP());
            _skillMode.put(POWERGRIP, new POWERGRIP());
            _skillMode.put(DESPERADO, new DESPERADO());
            _skillMode.put(TOMAHAWK, new TOMAHAWK());
            _skillMode.put(ARMOR_BREAK, new ARMOR_BREAK());
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public SkillMode getSkill(int skillid) {
        return (SkillMode) _skillMode.get(skillid);
    }
}
