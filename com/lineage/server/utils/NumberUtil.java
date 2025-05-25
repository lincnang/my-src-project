package com.lineage.server.utils;

import java.util.Random;

public class NumberUtil {
    public static int randomRound(double number) {
        double percentage = (number - Math.floor(number)) * 100.0D;
        if (percentage == 0.0D) {
            return (int) number;
        }
        int r = new Random().nextInt(100);
        if (r < percentage) {
            return (int) number + 1;
        }
        return (int) number;
    }
}
