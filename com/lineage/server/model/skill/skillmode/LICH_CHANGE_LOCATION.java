package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.types.Point;

/**
 * 換位術 <br>
 *
 * @author 老爹
 */
public class LICH_CHANGE_LOCATION extends SkillMode {

    public LICH_CHANGE_LOCATION() {
    }

    /**
     * 目標點方向計算
     *
     * @param h  目前面向
     * @param x  目前X
     * @param y  目前Y
     * @param tx 目標X
     * @param ty 目標Y
     * @return
     */
    private static int _targetDirection(int h, int x, int y, int tx, int ty) {
        try {
            float dis_x = Math.abs(x - tx); // X點方向距離
            float dis_y = Math.abs(y - ty); // Y點方向距離
            float dis = Math.max(dis_x, dis_y); // 取回2者最大質
            if (dis == 0) {
                return h; // 距離為0表示不須改變面向
            }
            int avg_x = (int) Math.floor((dis_x / dis) + 0.59f); // 上下左右優先丸
            int avg_y = (int) Math.floor((dis_y / dis) + 0.59f); // 上下左右優先丸
            int dir_x = 0;
            int dir_y = 0;
            if (x < tx) {
                dir_x = 1;
            }
            if (x > tx) {
                dir_x = -1;
            }
            if (y < ty) {
                dir_y = 1;
            }
            if (y > ty) {
                dir_y = -1;
            }
            if (avg_x == 0) {
                dir_x = 0;
            }
            if (avg_y == 0) {
                dir_y = 0;
            }
            switch (dir_x) {
                case -1:
                    switch (dir_y) {
                        case -1:
                            return 7; // 左
                        case 0:
                            return 6; // 左下
                        case 1:
                            return 5; // 下
                    }
                    break;
                case 0:
                    switch (dir_y) {
                        case -1:
                            return 0; // 左上
                        case 1:
                            return 4; // 右下
                    }
                    break;
                case 1:
                    switch (dir_y) {
                        case -1:
                            return 1; // 上
                        case 0:
                            return 2; // 右上
                        case 1:
                            return 3; // 右
                    }
                    break;
            }
        } catch (final Exception e) {
            //            _log.error(e.getLocalizedMessage(), e);
        }
        return h;
    }

    public int start(final L1PcInstance srcpc, final L1Character target, final L1Magic magic, final int skillId) throws Exception {
        // 1. 取得目標位置與地圖
        final int targetX = target.getX();
        final int targetY = target.getY();
        final short targetMapId = target.getMapId();
        // 2. 取得目標者當前朝向
        final int targetHeading = _targetDirection(srcpc.getHeading(), srcpc.getX(), srcpc.getY(), targetX, targetY);
        // 3. 計算前方一格
        Point frontPoint = calcForwardPosition(targetX, targetY, targetHeading);
        // 4. 檢查是否可通行
        if (!isWalkable(frontPoint.getX(), frontPoint.getY(), targetMapId)) {
            // 若不能通行，可選擇 return 或退到可行格子
            return -1; // 代表技能失敗或中斷
        }
        // 5. (可選) glanceCheck，是否可視
        if (!srcpc.glanceCheck(frontPoint.getX(), frontPoint.getY())) {
            // 若遊戲規則允許看不到也能衝，則可省略
            return -1;
        }
        // 6. 傳送（或衝鋒）
        L1Teleport.teleport(srcpc, frontPoint.getX(), frontPoint.getY(), targetMapId, targetHeading, false, 0);

        // 7. 回傳結果 (自訂)
        return 0;
    }

    /**
     * 計算「往指定方向前進一格」的座標
     */
    private Point calcForwardPosition(int x, int y, int heading) {
        // 8 方向偏移表
        final int[][] DIRECTION_OFFSETS = {
                {0, 1}, // 0: 北
                {-1, 1}, // 1: 北東
                {-1, 0}, // 2: 東
                {-1, -1}, // 3: 南東
                {0, -1}, // 4: 南
                {1, -1}, // 5: 南西
                {1, 0}, // 6: 西
                {1, 1}  // 7: 北西
        };
        int dx = DIRECTION_OFFSETS[heading][0];
        int dy = DIRECTION_OFFSETS[heading][1];
        return new Point(x + dx, y + dy);
    }

    /**
     * 簡易檢查該座標是否可通行
     * （實際檢查可根據遊戲地圖系統實作）
     */
    private boolean isWalkable(int x, int y, short mapId) {
        // TODO: 實作實際的地形碰撞、障礙物、NPC 佔位檢查
        return true;
    }


    @Override
    public int start(final L1NpcInstance npc, final L1Character cha, final L1Magic magic, final int integer) throws Exception {
        return 0;
    }

    @Override
    public void start(final L1PcInstance srcpc, final Object obj) throws Exception {
        // TODO Auto-generated method stub
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
