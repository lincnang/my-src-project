package com.lineage.server.utils;

import java.util.Random;

public class RangeInt {
    private static final Random _rnd = new Random();
    private int _low;
    private int _high;

    public RangeInt(int low, int high) {
        _low = low;
        _high = high;
    }

    public RangeInt(RangeInt range) {
        this(range._low, range._high);
    }

    public static boolean includes(int i, int low, int high) {
        return (low <= i) && (i <= high);
    }

    public static int ensure(int n, int low, int high) {
        int r = n;
        r = low <= r ? r : low;
        r = r <= high ? r : high;
        return r;
    }

    public boolean includes(int i) {
        return (_low <= i) && (i <= _high);
    }

    public int ensure(int i) {
        int r = i;
        r = _low <= r ? r : _low;
        r = r <= _high ? r : _high;
        return r;
    }

    public int randomValue() {
        return _rnd.nextInt(getWidth() + 1) + _low;
    }

    public int getLow() {
        return _low;
    }

    public int getHigh() {
        return _high;
    }

    public int getWidth() {
        return _high - _low;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof RangeInt)) {
            return false;
        }
        RangeInt range = (RangeInt) obj;
        return (_low == range._low) && (_high == range._high);
    }

    public String toString() {
        return "low=" + _low + ", high=" + _high;
    }
}
