package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * 各職業傷害設置  by 老爹改寫
 *
 * @author shinogame
 */
public final class Config_Occupational_Damage {
    private static final String Config_Occupational_Damage = "./config/☆老爹專屬製作☆遊戲設定☆/各職業魔法設置/各職業傷害設定表.properties";
    /**
     * 當 王族 攻擊 王族 增加傷害
     */
    public static double Crown1;
    /**
     * 當 王族 攻擊 騎士 增加傷害
     */
    public static double Crown2;
    /**
     * 當 王族 攻擊 妖精 增加傷害
     */
    public static double Crown3;
    /**
     * 當 王族 攻擊 法師 增加傷害
     */
    public static double Crown4;
    /**
     * 當 王族 攻擊 黑妖 增加傷害
     */
    public static double Crown5;
    /**
     * 當 王族 攻擊 龍騎士 增加傷害
     */
    public static double Crown6;
    /**
     * 當 王族 攻擊 幻術師 增加傷害
     */
    public static double Crown7;
    /**
     * 當王族  攻擊 狂戰士 增加傷害
     */
    public static double Crown8;
    /**
     * 當 騎士 攻擊 王族 增加傷害
     */
    public static double Knight1;
    /**
     * 當 騎士攻擊 騎士 增加傷害
     */
    public static double Knight2;
    /**
     * 當 騎士 攻擊 妖精 增加傷害
     */
    public static double Knight3;
    /**
     * 當 騎士 攻擊 法師 增加傷害
     */
    public static double Knight4;
    /**
     * 當 騎士 攻擊 黑妖 增加傷害
     */
    public static double Knight5;
    /**
     * 當 騎士 攻擊 龍騎士 增加傷害
     */
    public static double Knight6;
    /**
     * 當 騎士 攻擊 幻術師 增加傷害
     */
    public static double Knight7;
    /**
     * 當 騎士  攻擊 狂戰士 增加傷害
     */
    public static double Knight8;
    /**
     * 當 妖精 攻擊 王族 增加傷害
     */
    public static double Elf1;
    /**
     * 當 妖精 攻擊 騎士 增加傷害
     */
    public static double Elf2;
    /**
     * 當 妖精 攻擊 妖精 增加傷害
     */
    public static double Elf3;
    /**
     * 當 妖精 攻擊 法師 增加傷害
     */
    public static double Elf4;
    /**
     * 當 妖精 攻擊 黑妖 增加傷害
     */
    public static double Elf5;
    /**
     * 當 妖精 攻擊 龍騎士 增加傷害
     */
    public static double Elf6;
    /**
     * 當 妖精 攻擊 幻術師 增加傷害
     */
    public static double Elf7;
    /**
     * 當 妖精  攻擊 狂戰士 增加傷害
     */
    public static double Elf8;
    /**
     * 當 法師 攻擊 王族 增加傷害
     */
    public static double Wizard1;
    /**
     * 當 法師 攻擊 騎士 增加傷害
     */
    public static double Wizard2;
    /**
     * 當 法師 攻擊 妖精 增加傷害
     */
    public static double Wizard3;
    /**
     * 當 法師 攻擊 法師 增加傷害
     */
    public static double Wizard4;
    /**
     * 當 法師 攻擊 黑妖 增加傷害
     */
    public static double Wizard5;
    /**
     * 當 法師 攻擊 龍騎士 增加傷害
     */
    public static double Wizard6;
    /**
     * 當 法師 攻擊 幻術師 增加傷害
     */
    public static double Wizard7;
    /**
     * 當 法師 攻擊 狂戰士 增加傷害
     */
    public static double Wizard8;
    /**
     * 當 黑妖 攻擊 王族 增加傷害
     */
    public static double Darkelf1;
    /**
     * 當 黑妖 攻擊 騎士 增加傷害
     */
    public static double Darkelf2;
    /**
     * 當 黑妖 攻擊 妖精 增加傷害
     */
    public static double Darkelf3;
    /**
     * 當 黑妖 攻擊 法師 增加傷害
     */
    public static double Darkelf4;
    /**
     * 當 黑妖 攻擊 黑妖 增加傷害
     */
    public static double Darkelf5;
    /**
     * 當 黑妖 攻擊 龍騎士 增加傷害
     */
    public static double Darkelf6;
    /**
     * 當 黑妖 攻擊 幻術師 增加傷害
     */
    public static double Darkelf7;
    /**
     * 當 黑妖 攻擊 狂戰士 增加傷害
     */
    public static double Darkelf8;
    /**
     * 當 龍騎士 攻擊 王族 增加傷害
     */
    public static double DragonKnight1;
    /**
     * 當 龍騎士 攻擊 騎士 增加傷害
     */
    public static double DragonKnight2;
    /**
     * 當 龍騎士 攻擊 妖精 增加傷害
     */
    public static double DragonKnight3;
    /**
     * 當 龍騎士 攻擊 法師 增加傷害
     */
    public static double DragonKnight4;
    /**
     * 當 龍騎士 攻擊 黑妖 增加傷害
     */
    public static double DragonKnight5;
    /**
     * 當 龍騎士 攻擊 龍騎士 增加傷害
     */
    public static double DragonKnight6;
    /**
     * 當 龍騎士 攻擊 幻術師 增加傷害
     */
    public static double DragonKnight7;
    /**
     * 當 龍騎士 攻擊 狂戰士 增加傷害
     */
    public static double DragonKnight8;
    /**
     * 當 幻術師 攻擊 王族 增加傷害
     */
    public static double Illusionist1;
    /**
     * 當 幻術師 攻擊 騎士 增加傷害
     */
    public static double Illusionist2;
    /**
     * 當 幻術師 攻擊 妖精 增加傷害
     */
    public static double Illusionist3;
    /**
     * 當 幻術師 攻擊 法師 增加傷害
     */
    public static double Illusionist4;
    /**
     * 當 幻術師 攻擊 黑妖 增加傷害
     */
    public static double Illusionist5;
    /**
     * 當 幻術師 攻擊 龍騎士 增加傷害
     */
    public static double Illusionist6;
    /**
     * 當 幻術師 攻擊 幻術師 增加傷害
     */
    public static double Illusionist7;
    /**
     * 當 幻術師 攻擊 狂戰士 增加傷害
     */
    public static double Illusionist8;
    /**
     * 當 狂戰士 攻擊 王族 增加傷害
     */
    public static double Warrior1;
    /**
     * 當 狂戰士攻擊 騎士 增加傷害
     */
    public static double Warrior2;
    /**
     * 當 狂戰士 攻擊 妖精 增加傷害
     */
    public static double Warrior3;
    /**
     * 當 狂戰士 攻擊 法師 增加傷害
     */
    public static double Warrior4;
    /**
     * 當 狂戰士 攻擊 黑妖 增加傷害
     */
    public static double Warrior5;
    /**
     * 當 狂戰士攻擊 龍騎士 增加傷害
     */
    public static double Warrior6;
    /**
     * 當 狂戰士 攻擊 幻術師 增加傷害
     */
    public static double Warrior7;
    /**
     * 當 狂戰士 攻擊 狂戰士 增加傷害
     */
    public static double Warrior8;
    /**
     * 王族對怪物的傷害倍數
     */
    public static double Other_To_isCrownnpc;//王族對怪物的傷害倍數
    /**
     * 騎士對怪物的傷害倍數
     */
    public static double Other_To_isKnightnpc;//騎士對怪物的傷害倍數
    /**
     * 法師對怪物的傷害倍數
     */
    public static double Other_To_isWizardnpc;//法師對怪物的傷害倍數
    /**
     * 妖精對怪物的傷害倍數
     */
    public static double Other_To_isElfnpc;//妖精對怪物的傷害倍數
    /**
     * 黑妖對怪物的傷害倍數
     */
    public static double Other_To_isDarkelfnpc;//黑妖對怪物的傷害倍數
    /**
     * 龍騎對怪物的傷害倍數
     */
    public static double Other_To_isDragonKnightnpc;//龍騎對怪物的傷害倍數
    /**
     * 幻術對怪物的傷害倍數
     */
    public static double Other_To_isIllusionistnpc;//幻術對怪物的傷害倍數
    /**
     * 戰士對怪物的傷害倍數
     */
    public static double Other_To_isWarriornpc;//戰士對怪物的傷害倍數

    /**
     * 新增 by:老爹 玩家對NPC命中率
     */
    public static double Other_To_isCrownnpc_Hit;//王族對怪物的命中率
    public static double Other_To_isKnightnpc_Hit;//騎士對怪物的命中率
    public static double Other_To_isWizardnpc_Hit;//法師對怪物的命中率
    public static double Other_To_isElfnpc_Hit;//妖精對怪物的命中率
    public static double Other_To_isDarkelfnpc_Hit;//黑妖對怪物的命中率
    public static double Other_To_isDragonKnightnpc_Hit;//龍騎對怪物的命中率
    public static double Other_To_isIllusionistnpc_Hit;//幻術對怪物的命中率
    public static double Other_To_isWarriornpc_Hit;//戰士對怪物的命中率


    /**
     * 新增 by:老爹 各職業玩家魔法打玩家
     */
    public static double Crown_ADD_MagicPC;// 王族
    public static double Knight_ADD_MagicPC;// 騎士
    public static double Elf_ADD_MagicPC;//妖精
    public static double Darkelf_ADD_MagicPC;//黑暗妖精
    public static double Wizard_ADD_MagicPC;//法師
    public static double DragonKnight_ADD_MagicPC;//龍騎士
    public static double Illusionist_ADD_MagicPC;// 幻術師
    public static double Warrior_ADD_MagicPC;// 幻術師
    /**
     * 怪物與BOSS額外置設定
     */
    public static double BOSS_POWER;
    public static int BOSS_HIT;
    public static double ModDmg; //怪物[Spawnlist清單列中]打玩家傷害調整
    public static int AllMobHit_chance; //怪物[Spawnlist清單列中]增加命中率(100%) (0=關閉此功能)
    public static double Mob_Critical_Strike; //全怪(包含Boss)爆擊率值(%)
    public static double Mob_Critical_Strike_Dmg; //全怪(包含Boss)爆擊傷害倍數

    public static void load() throws ConfigErrorException {
        final Properties set = new Properties();
        try {
            final InputStream is = new FileInputStream(new File(Config_Occupational_Damage));
            set.load(is);
            is.close();
            // 以下從設定檔中讀取「王族」對各職業造成的額外傷害加成倍率（用字串 key 對應數值）
            Crown1 = Double.parseDouble(set.getProperty("Other_Crown_ADD_Crown_Damage", "0"));         // 王族打王族的加成倍率
            Crown2 = Double.parseDouble(set.getProperty("Other_Crown_ADD_Knight_Damage", "0"));        // 王族打騎士的加成倍率
            Crown3 = Double.parseDouble(set.getProperty("Other_Crown_ADD_Elf_Damage", "0"));           // 王族打妖精的加成倍率
            Crown4 = Double.parseDouble(set.getProperty("Other_Crown_ADD_Wizard_Damage", "0"));        // 王族打法師的加成倍率
            Crown5 = Double.parseDouble(set.getProperty("Other_Crown_ADD_Darkelf_Damage", "0"));       // 王族打黑妖的加成倍率
            Crown6 = Double.parseDouble(set.getProperty("Other_Crown_ADD_DragonKnight_Damage", "0"));  // 王族打龍騎的加成倍率
            Crown7 = Double.parseDouble(set.getProperty("Other_Crown_ADD_Illusionist_Damage", "0"));   // 王族打幻術的加成倍率
            Crown8 = Double.parseDouble(set.getProperty("Other_Crown_ADD_Warrior_Damage", "0"));       // 王族打戰士的加成倍率
            // 以下從設定檔中讀取「騎士」對各職業造成的額外傷害加成倍率（用字串 key 對應數值）
            Knight1 = Double.parseDouble(set.getProperty("Other_Knight_ADD_Crown_Damage", "0"));
            Knight2 = Double.parseDouble(set.getProperty("Other_Knight_ADD_Knight_Damage", "0"));
            Knight3 = Double.parseDouble(set.getProperty("Other_Knight_ADD_Elf_Damage", "0"));
            Knight4 = Double.parseDouble(set.getProperty("Other_Knight_ADD_Wizard_Damage", "0"));
            Knight5 = Double.parseDouble(set.getProperty("Other_Knight_ADD_Darkelf_Damage", "0"));
            Knight6 = Double.parseDouble(set.getProperty("Other_Knight_ADD_DragonKnight_Damage", "0"));
            Knight7 = Double.parseDouble(set.getProperty("Other_Knight_ADD_Illusionist_Damage", "0"));
            Knight8 = Double.parseDouble(set.getProperty("Other_Knight_ADD_Warrior_Damage", "0"));
            // 以下從設定檔中讀取「妖精」對各職業造成的額外傷害加成倍率（用字串 key 對應數值）
            Elf1 = Double.parseDouble(set.getProperty("Other_Elf_ADD_Crown_Damage", "0"));
            Elf2 = Double.parseDouble(set.getProperty("Other_Elf_ADD_Knight_Damage", "0"));
            Elf3 = Double.parseDouble(set.getProperty("Other_Elf_ADD_Elf_Damage", "0"));
            Elf4 = Double.parseDouble(set.getProperty("Other_Elf_ADD_Wizard_Damage", "0"));
            Elf5 = Double.parseDouble(set.getProperty("Other_Elf_ADD_Darkelf_Damage", "0"));
            Elf6 = Double.parseDouble(set.getProperty("Other_Elf_ADD_DragonKnight_Damage", "0"));
            Elf7 = Double.parseDouble(set.getProperty("Other_Elf_ADD_Illusionist_Damage", "0"));
            Elf8 = Double.parseDouble(set.getProperty("Other_Elf_ADD_Warrior_Damage", "0"));
            Wizard1 = Double.parseDouble(set.getProperty("Other_Wizard_ADD_Crown_Damage", "0"));
            Wizard2 = Double.parseDouble(set.getProperty("Other_Wizard_ADD_Knight_Damage", "0"));
            Wizard3 = Double.parseDouble(set.getProperty("Other_Wizard_ADD_Elf_Damage", "0"));
            Wizard4 = Double.parseDouble(set.getProperty("Other_Wizard_ADD_Wizard_Damage", "0"));
            Wizard5 = Double.parseDouble(set.getProperty("Other_Wizard_ADD_Darkelf_Damage", "0"));
            Wizard6 = Double.parseDouble(set.getProperty("Other_Wizard_ADD_DragonKnight_Damage", "0"));
            Wizard7 = Double.parseDouble(set.getProperty("Other_Wizard_ADD_Illusionist_Damage", "0"));
            Wizard8 = Double.parseDouble(set.getProperty("Other_Wizard_ADD_Warrior_Damage", "0"));
            Darkelf1 = Double.parseDouble(set.getProperty("Other_Darkelf_ADD_Crown_Damage", "0"));
            Darkelf2 = Double.parseDouble(set.getProperty("Other_Darkelf_ADD_Knight_Damage", "0"));
            Darkelf3 = Double.parseDouble(set.getProperty("Other_Darkelf_ADD_Elf_Damage", "0"));
            Darkelf4 = Double.parseDouble(set.getProperty("Other_Darkelf_ADD_Wizard_Damage", "0"));
            Darkelf5 = Double.parseDouble(set.getProperty("Other_Darkelf_ADD_Darkelf_Damage", "0"));
            Darkelf6 = Double.parseDouble(set.getProperty("Other_Darkelf_ADD_DragonKnight_Damage", "0"));
            Darkelf7 = Double.parseDouble(set.getProperty("Other_Darkelf_ADD_Illusionist_Damage", "0"));
            Darkelf8 = Double.parseDouble(set.getProperty("Other_Darkelf_ADD_Warrior_Damage", "0"));
            DragonKnight1 = Double.parseDouble(set.getProperty("Other_DragonKnight_ADD_Crown_Damage", "0"));
            DragonKnight2 = Double.parseDouble(set.getProperty("Other_DragonKnight_ADD_Knight_Damage", "0"));
            DragonKnight3 = Double.parseDouble(set.getProperty("Other_DragonKnight_ADD_Elf_Damage", "0"));
            DragonKnight4 = Double.parseDouble(set.getProperty("Other_DragonKnight_ADD_Wizard_Damage", "0"));
            DragonKnight5 = Double.parseDouble(set.getProperty("Other_DragonKnight_ADD_Darkelf_Damage", "0"));
            DragonKnight6 = Double.parseDouble(set.getProperty("Other_DragonKnight_ADD_DragonKnight_Damage", "0"));
            DragonKnight7 = Double.parseDouble(set.getProperty("Other_DragonKnight_ADD_Illusionist_Damage", "0"));
            DragonKnight8 = Double.parseDouble(set.getProperty("Other_DragonKnight_ADD_Warrior_Damage", "0"));
            Illusionist1 = Double.parseDouble(set.getProperty("Other_Illusionist_ADD_Crown_Damage", "0"));
            Illusionist2 = Double.parseDouble(set.getProperty("Other_Illusionist_ADD_Knight_Damage", "0"));
            Illusionist3 = Double.parseDouble(set.getProperty("Other_Illusionist_ADD_Elf_Damage", "0"));
            Illusionist4 = Double.parseDouble(set.getProperty("Other_Illusionist_ADD_Wizard_Damage", "0"));
            Illusionist5 = Double.parseDouble(set.getProperty("Other_Illusionist_ADD_Darkelf_Damage", "0"));
            Illusionist6 = Double.parseDouble(set.getProperty("Other_Illusionist_ADD_DragonKnight_Damage", "0"));
            Illusionist7 = Double.parseDouble(set.getProperty("Other_Illusionist_ADD_Illusionist_Damage", "0"));
            Illusionist8 = Double.parseDouble(set.getProperty("Other_Illusionist_ADD_Warrior_Damage", "0"));
            Warrior1 = Double.parseDouble(set.getProperty("Other_Warrior_ADD_Crown_Damage", "0"));
            Warrior2 = Double.parseDouble(set.getProperty("Other_Warrior_ADD_Knight_Damage", "0"));
            Warrior3 = Double.parseDouble(set.getProperty("Other_Warrior_ADD_Elf_Damage", "0"));
            Warrior4 = Double.parseDouble(set.getProperty("Other_Warrior_ADD_Wizard_Damage", "0"));
            Warrior5 = Double.parseDouble(set.getProperty("Other_Warrior_ADD_Darkelf_Damage", "0"));
            Warrior6 = Double.parseDouble(set.getProperty("Other_Warrior_ADD_DragonKnight_Damage", "0"));
            Warrior7 = Double.parseDouble(set.getProperty("Other_Warrior_ADD_Illusionist_Damage", "0"));
            Warrior8 = Double.parseDouble(set.getProperty("Other_Warrior_ADD_Warrior_Damage", "0"));
            Other_To_isCrownnpc = Double.parseDouble(set.getProperty("Other_To_isCrown_npc", "0.0"));   //王族對怪物的傷害倍數
            Other_To_isKnightnpc = Double.parseDouble(set.getProperty("Other_To_isKnight_npc", "0.0")); //騎士對怪物的傷害倍數
            Other_To_isWizardnpc = Double.parseDouble(set.getProperty("Other_To_isWizard_npc", "0.0")); //法師對怪物的傷害倍數
            Other_To_isElfnpc = Double.parseDouble(set.getProperty("Other_To_isElf_npc", "0.0"));        //妖精對怪物的傷害倍數
            Other_To_isDarkelfnpc = Double.parseDouble(set.getProperty("Other_To_isDarkelf_npc", "0.0"));//黑妖對怪物的傷害倍數
            Other_To_isDragonKnightnpc = Double.parseDouble(set.getProperty("Other_To_isDragonKnight_npc", "0.0"));//龍騎對怪物的傷害倍數
            Other_To_isIllusionistnpc = Double.parseDouble(set.getProperty("Other_To_isIllusionist_npc", "0.0"));//幻術對怪物的傷害倍數
            Other_To_isWarriornpc = Double.parseDouble(set.getProperty("Other_To_isWarrior_npc", "0.0"));//戰士對怪物的傷害倍數
            /** 新增 by:老爹 玩家對NPC命中率 */
            Other_To_isCrownnpc_Hit = Double.parseDouble(set.getProperty("Other_To_isCrown_npc_Hit", "0.0"));   //王族對怪物的命中率
            Other_To_isKnightnpc_Hit = Double.parseDouble(set.getProperty("Other_To_isKnight_npc_Hit", "0.0")); //騎士對怪物的命中率
            Other_To_isWizardnpc_Hit = Double.parseDouble(set.getProperty("Other_To_isWizard_npc_Hit", "0.0")); //法師對怪物的命中率
            Other_To_isElfnpc_Hit = Double.parseDouble(set.getProperty("Other_To_isElf_npc_Hit", "0.0"));        //妖精對怪物的命中率
            Other_To_isDarkelfnpc_Hit = Double.parseDouble(set.getProperty("Other_To_isDarkelf_npc_Hit", "0.0"));//黑妖對怪物的命中率
            Other_To_isDragonKnightnpc_Hit = Double.parseDouble(set.getProperty("Other_To_isDragonKnight_npc_Hit", "0.0"));//龍騎對怪物的命中率
            Other_To_isIllusionistnpc_Hit = Double.parseDouble(set.getProperty("Other_To_isIllusionist_npc_Hit", "0.0"));//幻術對怪物的命中率
            Other_To_isWarriornpc_Hit = Double.parseDouble(set.getProperty("Other_To_isWarrior_npc_Hit", "0.0"));//戰士對怪物的命中率

            /** 新增 by:老爹 各職業玩家魔法打玩家 */
            Crown_ADD_MagicPC = Double.parseDouble(set.getProperty("Other_ADD_isCrown_MagicPC", "1.0"));
            Knight_ADD_MagicPC = Double.parseDouble(set.getProperty("Other_ADD_isKnight_MagicPC", "1.0"));
            Elf_ADD_MagicPC = Double.parseDouble(set.getProperty("Other_ADD_isElf_MagicPC", "1.0"));
            Darkelf_ADD_MagicPC = Double.parseDouble(set.getProperty("Other_ADD_isDarkelf_MagicPC", "1.0"));
            Wizard_ADD_MagicPC = Double.parseDouble(set.getProperty("Other_ADD_isWizard_MagicPC", "1.0"));
            DragonKnight_ADD_MagicPC = Double.parseDouble(set.getProperty("Other_ADD_isDragonKnight_MagicPC", "1.0"));
            Illusionist_ADD_MagicPC = Double.parseDouble(set.getProperty("Other_ADD_isIllusionist_MagicPC", "1.0"));
            Warrior_ADD_MagicPC = Double.parseDouble(set.getProperty("Other_ADD_Warrior_MagicPC", "1.0"));
            /** 新增 by:老爹 怪物強度 */
            BOSS_HIT = Integer.parseInt(set.getProperty("Boss_hit", "50"));
            BOSS_POWER = Double.parseDouble(set.getProperty("Boss_Power", "3.0"));
            ModDmg = Double.parseDouble(set.getProperty("MODDOG", "1.0"));
            AllMobHit_chance = Integer.parseInt(set.getProperty("AllMobHit_chance", "0"));
            Mob_Critical_Strike = Double.parseDouble(set.getProperty("Mob_Critical_Strike", "3"));
            Mob_Critical_Strike_Dmg = Double.parseDouble(set.getProperty("Mob_Critical_Strike_Dmg", "1.1"));
        } catch (Exception e) {
            throw new ConfigErrorException("設置檔案遺失:./config/☆老爹專屬製作☆遊戲設定☆/各職業傷害設定表.properties");
        } finally {
            set.clear();
        }
    }
}
