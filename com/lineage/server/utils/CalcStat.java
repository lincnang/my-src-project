package com.lineage.server.utils;

import com.lineage.config.ConfigCharSetting;

import java.util.Random;

public class CalcStat {
    /**
     * 各職業等級提升時HP上升值計算
     *
     */
    public static final int[] ORIGINAL_CON = new int[]{10, 14, 12, 12, 8, 14, 12, 16};
    private static Random rnd = new Random();

    public static int calcAc(int type, int level, int dex) {
        int levelBonus = 0;
        switch (type) {
            case 0:
                levelBonus -= level / 6;
                break;
            case 1:
                levelBonus -= level / 6;
                break;
            case 2:
                levelBonus -= level / 7;
                break;
            case 3:
                levelBonus -= level / 8;
                break;
            case 4:
                levelBonus -= level / 6;
                break;
            case 5:
                levelBonus -= level / 6;
                break;
            case 6:
                levelBonus -= level / 8;
                break;
            case 7:
                levelBonus -= level / 6;
                break;
        }
        try {
            int temp = 10;
            if (dex < 9) {
                temp -= 2;
            } else {
                temp += (-2 + (((dex - 6) / 3) * -1));
            }
            return temp + levelBonus;
        } catch (Exception e) {
            e.printStackTrace();
            return 8;
        }
    }

    /**
     * 精神增加魔防
     *
     * @return mrBonus
     */
    public static int calcStatMr(int wis) {
        int mrBonus = 0;
        if (wis <= 10) {
            mrBonus = 0;
        } else {
            mrBonus = (wis - 10) * 4;
        }
        return mrBonus;
    }

    public static int calcDiffMr(int wis, int diff) {
        return calcStatMr(wis + diff) - calcStatMr(wis);
    }

    public static short calcStatHp(int charType, int baseMaxHp, int baseCon, int originalHpup, int type) {
        short randomhp = 0;
        if (baseCon > 45) {
            baseCon = 45;
        }
        int originalCon = ORIGINAL_CON[type];
        //總共增加的con點數
        int addCon = baseCon - originalCon;
        int aValue = 0;
        int bValue = 0;
        if (baseCon >= 25) {
            aValue = 25 - originalCon;
            if (baseCon >= 26) {
                bValue = addCon - aValue;
            }
        } else if (baseCon >= 13 && baseCon < 25) {
            aValue = baseCon - originalCon;
        } else {
        }
        int totalValue = 0;
        if (bValue == 0) {
            totalValue = aValue;
        } else {
            totalValue = aValue + (bValue / 2);
        }
        switch (charType) {
            case 0:
                randomhp = (short) (totalValue + (short) (23 + rnd.nextInt(2)));
                if (baseMaxHp + randomhp > ConfigCharSetting.PRINCE_MAX_HP) {
                    randomhp = (short) (ConfigCharSetting.PRINCE_MAX_HP - baseMaxHp);
                }
                break;
            case 1:
                randomhp = (short) (totalValue + (short) (29 + rnd.nextInt(2)));
                if (baseMaxHp + randomhp > ConfigCharSetting.KNIGHT_MAX_HP) {
                    randomhp = (short) (ConfigCharSetting.KNIGHT_MAX_HP - baseMaxHp);
                }
                break;
            case 2:
                randomhp = (short) (totalValue + (short) (22 + rnd.nextInt(2)));
                if (baseMaxHp + randomhp > ConfigCharSetting.ELF_MAX_HP) {
                    randomhp = (short) (ConfigCharSetting.ELF_MAX_HP - baseMaxHp);
                }
                break;
            case 3:
                randomhp = (short) (totalValue + (short) (19 + rnd.nextInt(2)));
                if (baseMaxHp + randomhp > ConfigCharSetting.WIZARD_MAX_HP) {
                    randomhp = (short) (ConfigCharSetting.WIZARD_MAX_HP - baseMaxHp);
                }
                break;
            case 4:
                randomhp = (short) (totalValue + (short) (22 + rnd.nextInt(2)));
                if (baseMaxHp + randomhp > ConfigCharSetting.DARKELF_MAX_HP) {
                    randomhp = (short) (ConfigCharSetting.DARKELF_MAX_HP - baseMaxHp);
                }
                break;
            case 5:
                randomhp = (short) (totalValue + (short) (25 + rnd.nextInt(2)));
                if (baseMaxHp + randomhp > ConfigCharSetting.DRAGONKNIGHT_MAX_HP) {
                    randomhp = (short) (ConfigCharSetting.DRAGONKNIGHT_MAX_HP - baseMaxHp);
                }
                break;
            case 6:
                randomhp = (short) (totalValue + (short) (21 + rnd.nextInt(2)));
                if (baseMaxHp + randomhp > ConfigCharSetting.ILLUSIONIST_MAX_HP) {
                    randomhp = (short) (ConfigCharSetting.ILLUSIONIST_MAX_HP - baseMaxHp);
                }
                break;
            case 7:
                randomhp = (short) (totalValue + (short) (31 + rnd.nextInt(2)));
                if (baseMaxHp + randomhp > ConfigCharSetting.WARRIOR_MAX_HP) {
                    randomhp = (short) (ConfigCharSetting.WARRIOR_MAX_HP - baseMaxHp);
                }
                break;
        }
        randomhp = (short) (randomhp + originalHpup);
        if (randomhp < 0) {
            randomhp = 0;
        }
        return randomhp;
    }

    /**
     * 各職業等級提升時MP上升值計算
     *
     * @return MP上升後數值
     */
    public static short calcStatMp(int charType, int baseMaxMp, int baseWis, int originalMpup) {
        int randommp = 0;
        int seedY = 0;
        int seedZ = 0;
        switch (baseWis) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 10:
            case 11:
                seedY = 2;
                break;
            case 9:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
                seedY = 3;
                break;
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 25:
            case 26:
            case 29:
            case 30:
            case 34:
                seedY = 4;
                break;
            case 24:
            case 27:
            case 28:
            case 31:
            case 32:
            case 33:
            default:
                seedY = 5;
        }
        switch (baseWis) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                seedZ = 0;
                break;
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                seedZ = 1;
                break;
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
                seedZ = 2;
                break;
            case 21:
            case 22:
            case 23:
            case 24:
                seedZ = 3;
                break;
            case 25:
            case 26:
            case 27:
            case 28:
                seedZ = 4;
                break;
            case 29:
            case 30:
            case 31:
            case 32:
                seedZ = 5;
                break;
            default:
                seedZ = 6;
        }
        randommp = rnd.nextInt(seedY + 1) + 2 + seedZ;
        switch (charType) {
            case 0: // 王族
                randommp = randommp * 2 / 3; // 調整為略快的增量
                if (baseMaxMp + randommp > ConfigCharSetting.PRINCE_MAX_MP) {
                    randommp = ConfigCharSetting.PRINCE_MAX_MP - baseMaxMp;
                }
                break;
            case 1: // 騎士
                randommp = randommp * 2 / 3; // 調整為略快的增量
                if (baseMaxMp + randommp > ConfigCharSetting.KNIGHT_MAX_MP) {
                    randommp = ConfigCharSetting.KNIGHT_MAX_MP - baseMaxMp;
                }
                break;
            case 2: // 妖精
                randommp = (int) (randommp * 2.5D);
                if (baseMaxMp + randommp > ConfigCharSetting.ELF_MAX_MP) {
                    randommp = ConfigCharSetting.ELF_MAX_MP - baseMaxMp;
                }
                break;
            case 3: // 法師
                randommp *= 3; // 調整增量為 3 倍
                if (baseMaxMp + randommp > ConfigCharSetting.WIZARD_MAX_MP) {
                    randommp = ConfigCharSetting.WIZARD_MAX_MP - baseMaxMp;
                }
                break;
            case 4: // 黑暗精靈
                randommp = (int) (randommp * 1.5D);
                if (baseMaxMp + randommp > ConfigCharSetting.DARKELF_MAX_MP) {
                    randommp = ConfigCharSetting.DARKELF_MAX_MP - baseMaxMp;
                }
                break;
            case 5: // 龍騎士
                randommp = randommp * 2 / 3;
                if (baseMaxMp + randommp > ConfigCharSetting.DRAGONKNIGHT_MAX_MP) {
                    randommp = ConfigCharSetting.DRAGONKNIGHT_MAX_MP - baseMaxMp;
                }
                break;
            case 6: // 幻術師
                randommp = randommp * 5 / 3;
                if (baseMaxMp + randommp > ConfigCharSetting.ILLUSIONIST_MAX_MP) {
                    randommp = ConfigCharSetting.ILLUSIONIST_MAX_MP - baseMaxMp;
                }
                break;
            case 7: // 戰士
                randommp = randommp * 2 / 3;
                if (baseMaxMp + randommp > ConfigCharSetting.WARRIOR_MAX_MP) {
                    randommp = ConfigCharSetting.WARRIOR_MAX_MP - baseMaxMp;
                }
                break;
        }

        randommp = randommp + originalMpup + 5;// 加固定增量
        if (randommp < 0) {
            randommp = 0;
        }
        return (short) randommp;
    }

    public static int calcStrDmg(int str, int basestr) {
        try {
            if (str < 10) {
                return 2;
            }
            int temp = 2 + ((str - 8) / 2);
            if (basestr >= 25) {
                temp++;
            }
            if (basestr >= 35) {
                temp++;
            }
            if (basestr >= 45) {
                temp += 5;
            }
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
            return 2;
        }
    }

    public static int calcStrHit(int str, int basestr) {
        try {
            if (str < 9) {
                return 5;
            }
            int temp = 5 + (((str - 6) / 3) * 2);
            if (basestr >= 25) {
                temp++;
            }
            if (basestr >= 35) {
                temp++;
            }
            if (basestr >= 45) {
                temp += 5;
            }
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
            return 2;
        }
    }

    public static int calcDexDmg(int dex, int basedex) {
        try {
            if (dex < 9) {
                return 2;
            }
            int temp = 2 + ((dex - 6) / 3);
            if (basedex >= 25) {
                temp++;
            }
            if (basedex >= 35) {
                temp++;
            }
            if (basedex >= 45) {
                temp += 5;
            }
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
            return 2;
        }
    }

    public static int calcDexHit(int dex, int basedex) {
        try {
            if (dex <= 7) {
                return -3;
            }
            int temp = -3 + (dex - 7);
            if (basedex >= 25) {
                temp++;
            }
            if (basedex >= 35) {
                temp++;
            }
            if (basedex >= 45) {
                temp += 5;
            }
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
            return -3;
        }
    }
}
