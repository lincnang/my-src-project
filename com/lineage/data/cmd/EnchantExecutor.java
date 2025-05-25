package com.lineage.data.cmd;

// 匯入玩家角色與道具實例的類別

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

// 抽象類別：定義所有強化邏輯共用的基本框架
public abstract class EnchantExecutor {

    // 抽象方法：強化失敗時的處理邏輯（子類別需實作）
    public abstract void failureEnchant(L1PcInstance paramL1PcInstance, L1ItemInstance paramL1ItemInstance);

    // 抽象方法：強化成功時的處理邏輯（子類別需實作）
    public abstract void successEnchant(L1PcInstance paramL1PcInstance, L1ItemInstance paramL1ItemInstance, int paramInt);

    // 決定強化增加等級的方法
    public int randomELevel(L1ItemInstance item, int bless) {
        int level = 0; // 最終決定的強化加成等級

        switch (bless) {
            case 0: // 祝福卷軸
            case 128:
                // 只對非封印道具進行機率強化
                if (item.getBless() < 3) {
                    Random random = new Random();
                    // 產生 1~100 的隨機數字
                    int i = ThreadLocalRandom.current().nextInt(100) + 1;

                    // 如果這個道具是「非安全強化裝備」
                    if (item.getItem().get_safeenchant() == 0) {
                        if (i < 50) {
                            level = 1;
                        } else if ((i >= 65) && (i <= 88)) {   // 88 - 65 = 23%
                            level = 2;
                        } else if ((i >= 88) && (i <= 100)) {  // 100 - 88 = 12%
                            level = 3;
                        }

                        // 如果目前強化值在 +0～+2 之間
                    } else if (item.getEnchantLevel() <= 2) {
                        if (i < 32) {
                            level = 1; // 32% 機率 +1
                        } else if ((i >= 32) && (i <= 76)) {
                            level = 2; // 45% 機率 +2
                        } else if ((i >= 77) && (i <= 100)) {
                            level = 3; // 24% 機率 +3
                        }

                        // 如果目前強化值在 +3～+5 之間
                    } else if ((item.getEnchantLevel() >= 3) && (item.getEnchantLevel() <= 5)) {
                        if (i < 35) {
                            level = 2; // 35% 機率 +2
                        } else {
                            level = 1; // 65% 機率 +1
                        }

                        // +6 以上強化一律只加 +1
                    } else {
                        level = 1;
                    }
                }
                break;

            case 1: // 一般卷軸
            case 129:
                if (item.getBless() < 3) {
                    level = 1; // 一律 +1
                }
                break;

            case 2: // 詛咒卷軸
            case 130:
                if (item.getBless() < 3) {
                    level = -1; // 一律 -1（扣強化等級）
                }
                break;

            case 3: // 幻象卷軸
            case 131:
                if (item.getBless() == 3) {
                    level = 1; // 幻象裝備固定 +1
                }
                break;
        }

        // 回傳強化等級的變化值（可能是 -1, 0, 1, 2, 3）
        return level;
    }
}
