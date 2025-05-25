package com.lineage.managerUI;

import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.TimeUnit;

import static com.lineage.server.model.skill.L1SkillId.*;

/**
 * 全體玩家buff 類名稱：AllPcBuff<br>
 * 修改時間：2022年4月26日 下午5:09:51<br>
 *
 * @version<br>
 */
public class AllPcBuff {
    private static final Log _log = LogFactory.getLog(AllPcBuff.class);
    private static final int[] allBuffSkill = {PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, BLESS_WEAPON, BERSERKERS, IMMUNE_TO_HARM, //
            //            IMMUNE_TO_HARM2, //
            ADVANCE_SPIRIT, //
            REDUCTION_ARMOR, BOUNCE_ATTACK,//
            SOLID_CARRIAGE, BURNING_SPIRIT
            /*VENOM_RESIST*/, DOUBLE_BREAK, UNCANNY_DODGE, DRESS_EVASION, //
            GLOWING_AURA, BRAVE_AURA, RESIST_MAGIC, CLEAR_MIND, ELEMENTAL_PROTECTION, AQUA_PROTECTER, BURNING_WEAPON, IRON_SKIN, EXOTIC_VITALIZE, WATER_LIFE, ELEMENTAL_FIRE, SOUL_OF_FLAME, ADDITIONAL_FIRE, DRAGON_SKIN, BURNING_SLASH};
    private static AllPcBuff _instance;

    protected static AllPcBuff get() {
        if (_instance == null) {
            _instance = new AllPcBuff();
        }
        return _instance;
    }

    public static void startPc(final L1PcInstance target) {
        L1BuffUtil.haste(target, 3600 * 1000);
        L1BuffUtil.brave(target, 3600 * 1000);
        L1ItemInstance weapon = target.getWeapon();
        if (weapon != null) {
            int weaponType = weapon.getItem().getType();
            int polyid = -1;
            switch (weaponType) {
                case 1:// 劍
                case 2:// 匕首
                case 3:// 雙手劍
                case 6:// 斧(單手)
                case 15:// 斧(雙手)
                    polyid = 6276;// 白金騎士
                    break;
                case 11:// 鋼爪
                case 12:// 雙刀
                    polyid = 6282;// 白金刺客
                    break;
                case 16:// 魔杖(雙手)
                case 7:// 魔杖(單手)
                case 17:// 奇古獸
                    polyid = 6277;// 白金法師
                    break;
                case 4:// 弓(雙手)
                case 13:// 弓(單手)
                case 10:// 鐵手甲
                    polyid = 6278;// 白金巡守
                    break;
                case 18:// 鎖鏈劍
                case 14:// 矛(單手)
                case 5:// 矛(雙手)
                    polyid = 7341;// 狂暴將軍
                    break;
            }
            if (polyid != -1) {
                L1PolyMorph.doPoly(target, polyid, 7200, L1PolyMorph.MORPH_BY_ITEMMAGIC);
            }
        }
        for (int i = 0; i < allBuffSkill.length; i++) {
            final L1Skills skill = SkillsTable.get().getTemplate(allBuffSkill[i]);
            final L1SkillUse skillUse = new L1SkillUse();
            skillUse.handleCommands(target, allBuffSkill[i], target.getId(), target.getX(), target.getY(), skill.getBuffDuration(), L1SkillUse.TYPE_GMBUFF);// */
        }
    }

    /**
     * 開始執行給予全體玩家buff
     */
    public void startAllBuff() {
        AllBuffRunnable allBuffRunnable = new AllBuffRunnable();
        GeneralThreadPool.get().execute(allBuffRunnable);
    }

    /**
     * 執行多數物件技能狀態給予
     *
     * @author daien
     */
    private static class AllBuffRunnable implements Runnable {
        @Override
        public void run() {
            try {
                // int i = 0;
                for (L1PcInstance tgpc : World.get().getAllPlayers()) {
                    startPc(tgpc);
                    TimeUnit.MILLISECONDS.sleep(1);
                }
            } catch (Exception e) {
                _log.error(e.getLocalizedMessage(), e);
            }
        }
    }
}
