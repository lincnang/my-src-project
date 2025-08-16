package com.lineage.server.Controller;

import java.util.concurrent.ConcurrentHashMap;

public class PcCritRepo {
    public static class Crit {
        public final int chance;   // %
        public final int percent;  // %
        public final int fx;       // 特效ID

        // ★ 這裡改成 public，外部才能 new PcCritRepo.Crit(...)
        public Crit(int c, int p, int f) {
            this.chance = c;
            this.percent = p;
            this.fx = f;
        }
    }

    private static final ConcurrentHashMap<Integer, Crit> MAP = new ConcurrentHashMap<>();

    public static void set(int pcId, int chance, int percent, int fx) {
        MAP.put(pcId, new Crit(chance, percent, fx));
    }
    public static Crit get(int pcId) { return MAP.get(pcId); }
    public static void clear(int pcId) { MAP.remove(pcId); }
}
