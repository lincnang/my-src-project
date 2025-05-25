package com.lineage.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Properties;

public final class ConfigQuest {
    private static final Log _log = LogFactory.getLog(ConfigQuest.class);
    private static final String QUEST = "./config/☆老爹專屬製作☆遊戲設定☆/活動任務設置.properties";
    public static int Bao1_Quest_Lv;
    public static int Bao1_Quest_Levelmet;
    public static int[][] BAO1_QUEST_ITEM;
    public static int Bao1_Stone_MaxCount;
    public static int Bao2_Quest_Lv;
    public static int Bao2_Quest_Levelmet;
    public static int[][] BAO2_QUEST_ITEM;
    public static int Bao2_Stone_MaxCount;
    public static int Bao3_Quest_Lv;
    public static int Bao3_Quest_Levelmet;
    public static int[][] BAO3_QUEST_ITEM;
    public static int Bao3_Stone_MaxCount;
    public static int Bao4_Quest_Lv;
    public static int Bao4_Quest_Levelmet;
    public static int[][] BAO4_QUEST_ITEM;
    public static int Bao4_Stone_MaxCount;
    public static int Bao5_Quest_Lv;
    public static int Bao5_Quest_Levelmet;
    public static int[][] BAO5_QUEST_ITEM;
    public static int Bao5_Stone_MaxCount;
    /**
     * 每日獎勵等級限制
     */
    public static int Day_Reward_Level;
    /**
     * 每日獎勵VIP等級限制
     */
    public static int Day_Reward_Levelvip;
    /**
     * 每日獎勵轉生等級限制
     */
    public static int Day_Reward_Levelmet;
    /**
     * 每日獎勵任務編號
     */
    public static int Day_Reward_QuestId;
    /**
     * 每日獎勵道具
     */
    public static int[][] Day_Reward_Item;
    /**
     * 每日獎勵任務編號重置時間
     */
    public static Calendar Day_Reward_Reset_Time;
    /**
     * 每日獎勵道具
     */
    public static int[][] BAO1_REWARD_ITEM;
    public static int[][] BAO2_REWARD_ITEM;
    public static int[][] BAO3_REWARD_ITEM;
    public static int[][] BAO4_REWARD_ITEM;
    public static int[][] BAO5_REWARD_ITEM;

    public static void load() throws ConfigErrorException {
        final Properties set = new Properties();
        try {
            final InputStream is = new FileInputStream(new File(QUEST));
            set.load(is);
            is.close();
            // 每日獎勵等級限制
            Day_Reward_Level = Integer.parseInt(set.getProperty("day_reward_level", "70"));
            // 每日獎勵VIP等級限制
            Day_Reward_Levelvip = Integer.parseInt(set.getProperty("day_reward_levelvip", "1"));
            // 每日獎勵轉生等級限制
            Day_Reward_Levelmet = Integer.parseInt(set.getProperty("day_reward_levelmet", "1"));
            // 每日獎勵任務編號
            Day_Reward_QuestId = Integer.parseInt(set.getProperty("day_reward_questId", "35553"));
            // 每日獎勵道具
            final String temp25 = set.getProperty("day_reward_item", "");
            if (!temp25.equalsIgnoreCase("null")) {
                final String[] temp1 = temp25.split(",");
                final int size = temp1.length;
                Day_Reward_Item = new int[size][2];
                for (int i = 0; i < size; i++) {
                    final String[] temp2 = temp1[i].split(":");
                    Day_Reward_Item[i][0] = Integer.valueOf(temp2[0]);
                    Day_Reward_Item[i][1] = Integer.valueOf(temp2[1]);
                }
            }
            // 每日獎勵任務編號重置時間
            final String tmp1 = set.getProperty("day_reward_reset_time", "");
            if (!tmp1.equalsIgnoreCase("null")) {
                final String[] temp = tmp1.split(":");
                if (temp.length == 3) {
                    final Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(temp[0]));
                    cal.set(Calendar.MINUTE, Integer.parseInt(temp[1]));
                    cal.set(Calendar.SECOND, Integer.parseInt(temp[2]));
                    Day_Reward_Reset_Time = cal;
                } else {
                    _log.info("[每日獎勵編號]重置時間有誤, 請重新設置!");
                }
            }
            Bao1_Quest_Lv = Integer.parseInt(set.getProperty("Bao1_Quest_Lv", "75"));
            Bao1_Quest_Levelmet = Integer.parseInt(set.getProperty("Bao1_Quest_Levelmet", "0"));
            Bao1_Stone_MaxCount = Integer.parseInt(set.getProperty("Bao1_Stone_MaxCount", "100"));
            final String temp20 = set.getProperty("Bao1_Quest_Item", "");
            if (!temp20.equalsIgnoreCase("null")) {
                final String[] temp1 = temp20.split(",");
                final int size = temp1.length;
                BAO1_QUEST_ITEM = new int[size][2];
                for (int i = 0; i < size; i++) {
                    final String[] temp2 = temp1[i].split(":");
                    BAO1_QUEST_ITEM[i][0] = Integer.valueOf(temp2[0]);
                    BAO1_QUEST_ITEM[i][1] = Integer.valueOf(temp2[1]);
                }
            }
            Bao2_Quest_Lv = Integer.parseInt(set.getProperty("Bao2_Quest_Lv", "85"));
            Bao2_Quest_Levelmet = Integer.parseInt(set.getProperty("Bao2_Quest_Levelmet", "0"));
            Bao2_Stone_MaxCount = Integer.parseInt(set.getProperty("Bao2_Stone_MaxCount", "100"));
            final String temp21 = set.getProperty("Bao2_Quest_Item", "");
            if (!temp21.equalsIgnoreCase("null")) {
                final String[] temp1 = temp21.split(",");
                final int size = temp1.length;
                BAO2_QUEST_ITEM = new int[size][2];
                for (int i = 0; i < size; i++) {
                    final String[] temp2 = temp1[i].split(":");
                    BAO2_QUEST_ITEM[i][0] = Integer.valueOf(temp2[0]);
                    BAO2_QUEST_ITEM[i][1] = Integer.valueOf(temp2[1]);
                }
            }
            Bao3_Quest_Lv = Integer.parseInt(set.getProperty("Bao3_Quest_Lv", "95"));
            Bao3_Quest_Levelmet = Integer.parseInt(set.getProperty("Bao3_Quest_Levelmet", "0"));
            Bao3_Stone_MaxCount = Integer.parseInt(set.getProperty("Bao3_Stone_MaxCount", "100"));
            final String temp22 = set.getProperty("Bao3_Quest_Item", "");
            if (!temp22.equalsIgnoreCase("null")) {
                final String[] temp1 = temp22.split(",");
                final int size = temp1.length;
                BAO3_QUEST_ITEM = new int[size][2];
                for (int i = 0; i < size; i++) {
                    final String[] temp2 = temp1[i].split(":");
                    BAO3_QUEST_ITEM[i][0] = Integer.valueOf(temp2[0]);
                    BAO3_QUEST_ITEM[i][1] = Integer.valueOf(temp2[1]);
                }
            }
            Bao4_Quest_Lv = Integer.parseInt(set.getProperty("Bao4_Quest_Lv", "95"));
            Bao4_Quest_Levelmet = Integer.parseInt(set.getProperty("Bao4_Quest_Levelmet", "0"));
            Bao4_Stone_MaxCount = Integer.parseInt(set.getProperty("Bao4_Stone_MaxCount", "100"));
            final String temp23 = set.getProperty("Bao4_Quest_Item", "");
            if (!temp23.equalsIgnoreCase("null")) {
                final String[] temp1 = temp23.split(",");
                final int size = temp1.length;
                BAO4_QUEST_ITEM = new int[size][2];
                for (int i = 0; i < size; i++) {
                    final String[] temp2 = temp1[i].split(":");
                    BAO4_QUEST_ITEM[i][0] = Integer.valueOf(temp2[0]);
                    BAO4_QUEST_ITEM[i][1] = Integer.valueOf(temp2[1]);
                }
            }
            Bao5_Quest_Lv = Integer.parseInt(set.getProperty("Bao5_Quest_Lv", "95"));
            Bao5_Quest_Levelmet = Integer.parseInt(set.getProperty("Bao5_Quest_Levelmet", "0"));
            Bao5_Stone_MaxCount = Integer.parseInt(set.getProperty("Bao5_Stone_MaxCount", "100"));
            final String temp24 = set.getProperty("Bao5_Quest_Item", "");
            if (!temp24.equalsIgnoreCase("null")) {
                final String[] temp1 = temp24.split(",");
                final int size = temp1.length;
                BAO5_QUEST_ITEM = new int[size][2];
                for (int i = 0; i < size; i++) {
                    final String[] temp2 = temp1[i].split(":");
                    BAO5_QUEST_ITEM[i][0] = Integer.valueOf(temp2[0]);
                    BAO5_QUEST_ITEM[i][1] = Integer.valueOf(temp2[1]);
                }
            }
            final String tempReward = set.getProperty("Bao1_Reward_Item", "");
            if (!tempReward.equalsIgnoreCase("null")) {
                final String[] temp1 = tempReward.split(",");
                final int size = temp1.length;
                BAO1_REWARD_ITEM = new int[size][2];
                for (int i = 0; i < size; i++) {
                    final String[] temp2 = temp1[i].split(":");
                    BAO1_REWARD_ITEM[i][0] = Integer.valueOf(temp2[0]); // itemId
                    BAO1_REWARD_ITEM[i][1] = Integer.valueOf(temp2[1]); // count
                }
            }
            // BAO2_REWARD_ITEM
            final String tempReward2 = set.getProperty("Bao2_Reward_Item", "");
            if (!tempReward2.equalsIgnoreCase("null")) {
                final String[] temp1 = tempReward2.split(",");
                final int size = temp1.length;
                BAO2_REWARD_ITEM = new int[size][2];
                for (int i = 0; i < size; i++) {
                    final String[] temp2 = temp1[i].split(":");
                    BAO2_REWARD_ITEM[i][0] = Integer.parseInt(temp2[0]);
                    BAO2_REWARD_ITEM[i][1] = Integer.parseInt(temp2[1]);
                }
            }

            // BAO3_REWARD_ITEM
            final String tempReward3 = set.getProperty("Bao3_Reward_Item", "");
            if (!tempReward3.equalsIgnoreCase("null")) {
                final String[] temp1 = tempReward3.split(",");
                final int size = temp1.length;
                BAO3_REWARD_ITEM = new int[size][2];
                for (int i = 0; i < size; i++) {
                    final String[] temp2 = temp1[i].split(":");
                    BAO3_REWARD_ITEM[i][0] = Integer.parseInt(temp2[0]);
                    BAO3_REWARD_ITEM[i][1] = Integer.parseInt(temp2[1]);
                }
            }

            // BAO4_REWARD_ITEM
            final String tempReward4 = set.getProperty("Bao4_Reward_Item", "");
            if (!tempReward4.equalsIgnoreCase("null")) {
                final String[] temp1 = tempReward4.split(",");
                final int size = temp1.length;
                BAO4_REWARD_ITEM = new int[size][2];
                for (int i = 0; i < size; i++) {
                    final String[] temp2 = temp1[i].split(":");
                    BAO4_REWARD_ITEM[i][0] = Integer.parseInt(temp2[0]);
                    BAO4_REWARD_ITEM[i][1] = Integer.parseInt(temp2[1]);
                }
            }

            // BAO5_REWARD_ITEM
            final String tempReward5 = set.getProperty("Bao5_Reward_Item", "");
            if (!tempReward5.equalsIgnoreCase("null")) {
                final String[] temp1 = tempReward5.split(",");
                final int size = temp1.length;
                BAO5_REWARD_ITEM = new int[size][2];
                for (int i = 0; i < size; i++) {
                    final String[] temp2 = temp1[i].split(":");
                    BAO5_REWARD_ITEM[i][0] = Integer.parseInt(temp2[0]);
                    BAO5_REWARD_ITEM[i][1] = Integer.parseInt(temp2[1]);
                }
            }

        } catch (final Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + QUEST);
        } finally {
            set.clear();
        }
    }
}
