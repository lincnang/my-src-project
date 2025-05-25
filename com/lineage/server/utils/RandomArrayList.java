package com.lineage.server.utils;

public class RandomArrayList {
    private static int listint = 0;
    private static double[] ArrayDouble = new double[32767];
    private static boolean haveNextGaussian = false;
    private static double nextGaussian;

    static {
        for (listint = 0; listint < 32767; listint += 1) {
            ArrayDouble[listint] = Math.random();
        }
    }

    public static void setArrayList() {
        for (listint = 0; listint < 32767; listint += 1) {
            ArrayDouble[listint] = Math.random();
        }
    }

    private static int getlistint() {
        if (listint < 32766) {
            return ++listint;
        }
        return RandomArrayList.listint = 0;
    }

    public static void getByte(byte[] arr) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = ((byte) (int) getValue(128));
        }
    }

    public static double getGaussian() {
        if (haveNextGaussian) {
            haveNextGaussian = false;
            return nextGaussian;
        }
        double v1;
        double v2;
        double s;
        do {
            v1 = 2.0D * ArrayDouble[getlistint()] - 1.0D;
            v2 = 2.0D * ArrayDouble[getlistint()] - 1.0D;
            s = v1 * v1 + v2 * v2;
        } while ((s >= 1.0D) || (s == 0.0D));
        double multiplier = Math.sqrt(-2.0D * Math.log(s) / s);
        nextGaussian = v2 * multiplier;
        haveNextGaussian = true;
        return v1 * multiplier;
    }

    private static double getValue() {
        return ArrayDouble[getlistint()];
    }

    private static double getValue(int rang) {
        return getValue() * rang;
    }

    private static double getValue(double rang) {
        return getValue() * rang;
    }

    public static int getInt(int rang) {
        return (int) getValue(rang);
    }

    public static int getInt(double rang) {
        return (int) getValue(rang);
    }

    public static double getDouble() {
        return getValue();
    }

    public static double getDouble(double rang) {
        return getValue(rang);
    }

    public static int getInc(int rang, int increase) {
        return getInt(rang) + increase;
    }

    public static int getInc(double rang, int increase) {
        return getInt(rang) + increase;
    }

    public static double getDc(int rang, int increase) {
        return getValue(rang) + increase;
    }

    public static double getDc(double rang, int increase) {
        return getValue(rang) + increase;
    }
}