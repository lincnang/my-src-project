package com.lineage.server.utils;

import java.util.Random;

public class Dice {
    private static final Random _rnd = new Random();
    private final int _faces;

    public Dice(int faces) {
        _faces = faces;
    }

    public int getFaces() {
        return _faces;
    }

    public int roll() {
        return _rnd.nextInt(_faces) + 1;
    }

    public int roll(int count) {
        int n = 0;
        for (int i = 0; i < count; i++) {
            n += roll();
        }
        return n;
    }
}
