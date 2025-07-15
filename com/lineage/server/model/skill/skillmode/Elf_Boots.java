package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.types.Point;

/**
 * 精靈之翼靴 <br>
 * 效果：無論有無目標，施放後皆朝自身面向前進5格。
 * @author 老爹
 */
public class Elf_Boots extends SkillMode {

    public Elf_Boots() {
    }

    // _targetDirection 方法在此邏輯下已不再需要，但可以保留。
    private static int _targetDirection(int h, int x, int y, int tx, int ty) {
        // ... (此方法內容不變，但不會被呼叫到)
        try {
            float dis_x = Math.abs(x - tx);
            float dis_y = Math.abs(y - ty);
            float dis = Math.max(dis_x, dis_y);
            if (dis == 0) {
                return h;
            }
            int avg_x = (int) Math.floor((dis_x / dis) + 0.59f);
            int avg_y = (int) Math.floor((dis_y / dis) + 0.59f);
            int dir_x = 0;
            int dir_y = 0;
            if (x < tx) dir_x = 1;
            if (x > tx) dir_x = -1;
            if (y < ty) dir_y = 1;
            if (y > ty) dir_y = -1;
            if (avg_x == 0) dir_x = 0;
            if (avg_y == 0) dir_y = 0;
            switch (dir_x) {
                case -1:
                    switch (dir_y) {
                        case -1: return 7;
                        case 0:  return 6;
                        case 1:  return 5;
                    }
                    break;
                case 0:
                    switch (dir_y) {
                        case -1: return 0;
                        case 1:  return 4;
                    }
                    break;
                case 1:
                    switch (dir_y) {
                        case -1: return 1;
                        case 0:  return 2;
                        case 1:  return 3;
                    }
                    break;
            }
        } catch (final Exception e) {
            // _log.error(e.getLocalizedMessage(), e);
        }
        return h;
    }

    /**
     * 技能施放主邏輯
     */
    public int start(final L1PcInstance srcpc, final L1Character target, final L1Magic magic, final int skillId) throws Exception {
        // 統一邏輯：永遠朝著施法者(srcpc)自身面向前進5格。
        int destX = srcpc.getX();
        int destY = srcpc.getY();
        int destHeading = srcpc.getHeading();
        short destMapId = srcpc.getMapId();

        int steps = 5;

        Point current = new Point(destX, destY);
        Point next = null;
        for (int i = 0; i < steps; i++) {
            next = calcForwardPosition(current.getX(), current.getY(), destHeading);
            if (!isWalkable(next.getX(), next.getY(), destMapId)) {
                break;
            }
            current = next;
        }

        if (!srcpc.glanceCheck(current.getX(), current.getY())) {
            srcpc.sendPackets(new S_SystemMessage("你無法看到前方的地點！"));
            return -1;
        }

        L1Teleport.teleport(srcpc, current.getX(), current.getY(), destMapId, destHeading, false, 0);
        return 0;
    }

    @Override
    public void start(final L1PcInstance srcpc, final Object obj) throws Exception {
        this.start(srcpc, (L1Character) null, null, 0);
    }

    /**
     * 計算往指定方向前進一格的座標
     */
    private Point calcForwardPosition(int x, int y, int heading) {
        //【修正點】修正座標系，使其與遊戲實際座標方向一致
        final int[][] DIRECTION_OFFSETS = {
                {0, -1},   // 0: 北 (North)
                {1, -1},   // 1: 北東 (Northeast)
                {1, 0},    // 2: 東 (East)
                {1, 1},    // 3: 南東 (Southeast)
                {0, 1},    // 4: 南 (South)
                {-1, 1},   // 5: 南西 (Southwest)
                {-1, 0},   // 6: 西 (West)
                {-1, -1}    // 7: 北西 (Northwest)
        };
        int dx = DIRECTION_OFFSETS[heading][0];
        int dy = DIRECTION_OFFSETS[heading][1];
        return new Point(x + dx, y + dy);
    }

    /**
     * 檢查該座標是否可通行
     */
    private boolean isWalkable(int x, int y, short mapId) {
        // TODO: 實作地形障礙/碰撞檢查
        return true;
    }

    @Override
    public int start(final L1NpcInstance npc, final L1Character cha, final L1Magic magic, final int integer) throws Exception {
        return 0;
    }

    @Override
    public void stop(final L1Character cha) throws Exception {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.stopMoveHandDarkness();
            pc.flushedLocation();
            if (pc.getSkin(13135) != null) {
                pc.getSkin(13135).deleteMe();
                pc.removeSkin(13135);
            }
        }
    }
}