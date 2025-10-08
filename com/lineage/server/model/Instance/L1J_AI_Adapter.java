package com.lineage.server.model.Instance;

import com.lineage.server.world.World;

import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * L1J 風格的 NPC AI 目標搜尋（全域共用）。
 * 採用優先級佇列，以距離為主要排序依據；
 * 同時套用 Karma、隱身偵測、職業/外型等條件過濾。
 */
public final class L1J_AI_Adapter {

    private L1J_AI_Adapter() {}

    // 與 L1J 相同的職業外型群組對應
    private static final int[][] CLASS_GFX_ID = {
            {0, 1},       // 王族
            {48, 61},     // 騎士
            {37, 138},    // 妖精
            {734, 1186},  // 法師
            {2786, 2796}  // 黑妖
    };

    public static void searchTarget(final L1NpcInstance npc) {
        if (npc == null || npc.isDead()) {
            return;
        }

        // 目前僅鎖定玩家做目標（與 L1J 常見實作一致）
        final Collection<L1PcInstance> players = World.get().getVisiblePlayer(npc);
        if (players == null || players.isEmpty()) {
            return;
        }

        final PriorityQueue<L1PcInstance> candidates = new PriorityQueue<>(
                Math.max(16, players.size()), new TargetSorter(npc));

        for (final L1PcInstance pc : players) {
            if (isValidTarget(npc, pc)) {
                candidates.offer(pc);
            }
        }

        if (!candidates.isEmpty()) {
            final L1PcInstance target = candidates.poll();
            npc._hateList.add(target, 0);
            npc._target = target;
        }
    }

    private static boolean isValidTarget(final L1NpcInstance npc, final L1PcInstance pc) {
        if (pc == null) {
            return false;
        }
        if (pc.getCurrentHp() <= 0 || pc.isDead() || pc.isGhost() || pc.isGm()) {
            return false;
        }
        // 副本 ID 必須相同
        if (npc.get_showId() != pc.get_showId()) {
            return false;
        }

        // Karma 相容邏輯（善/惡 陣營友好判斷）
        if (npc.getNpcTemplate().getKarma() < 0 && pc.getKarmaLevel() >= 1) {
            return false;
        }
        if (npc.getNpcTemplate().getKarma() > 0 && pc.getKarmaLevel() <= -1) {
            return false;
        }

        // 隱身偵測：未具偵測隱身能力時忽略隱身玩家
        if (pc.isInvisble() && !npc.getNpcTemplate().is_agrocoi()) {
            return false;
        }

        // 攻擊性/特定外型偵測
        boolean canAggro = npc.getNpcTemplate().is_agro();
        if (!canAggro) {
            // 變身偵測（透明披風/變形）
            if (pc.hasSkillEffect(67) && npc.getNpcTemplate().is_agrososc()) {
                canAggro = true;
            }
        }

        // 若設定針對特定外型，則符合其一視為可攻擊
        final int agrogfx1 = npc.getNpcTemplate().is_agrogfxid1();
        final int agrogfx2 = npc.getNpcTemplate().is_agrogfxid2();
        final int pcGfx = pc.getGfxId();

        // 當 agrogfx 介於 0..4 時，代表職業群組，需轉為實際外型比對
        if (agrogfx1 >= 0 && agrogfx1 <= 4) {
            if (pcGfx == CLASS_GFX_ID[agrogfx1][0] || pcGfx == CLASS_GFX_ID[agrogfx1][1]) {
                canAggro = true;
            }
        } else if (agrogfx1 > 4) {
            if (pcGfx == agrogfx1) {
                canAggro = true;
            }
        }

        if (agrogfx2 >= 0 && agrogfx2 <= 4) {
            if (pcGfx == CLASS_GFX_ID[agrogfx2][0] || pcGfx == CLASS_GFX_ID[agrogfx2][1]) {
                canAggro = true;
            }
        } else if (agrogfx2 > 4) {
            if (pcGfx == agrogfx2) {
                canAggro = true;
            }
        }

        // 非主動攻擊型怪：僅追擊極惡玩家（與既有邏輯一致）
        if (!canAggro && pc.getLawful() < -1000) {
            canAggro = true;
        }

        return canAggro;
    }

    private static final class TargetSorter implements Comparator<L1PcInstance> {
        private final L1NpcInstance npc;

        private TargetSorter(final L1NpcInstance npc) {
            this.npc = npc;
        }

        @Override
        public int compare(final L1PcInstance a, final L1PcInstance b) {
            final int da = npc.getLocation().getTileLineDistance(a.getLocation());
            final int db = npc.getLocation().getTileLineDistance(b.getLocation());
            // 將仇恨值納入排序，仇恨高者優先
            final int ha = npc._hateList.get(a);
            final int hb = npc._hateList.get(b);
            final int pa = da * 10 - ha;
            final int pb = db * 10 - hb;
            return Integer.compare(pa, pb);
        }
    }
}


