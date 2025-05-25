package com.lineage.server.model.Instance;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.S_MoveCharPacket;
import com.lineage.server.types.Point;
import com.lineage.server.utils.Random;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * 移動AI
 *
 * @author dexc
 */
public class NpcMove extends NpcMoveExecutor {
    private static final Log _log = LogFactory.getLog(NpcMove.class);
    protected final int[][] DIR_TABLE = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}};
    private final L1NpcInstance _npc;
    private Iterator<int[]> _list = null;
    private int _error;
    private int _heading; // ● 面向 0.左上 1.上 2.右上 3.右 4.右下 5.下 6.左下 7.左

    public NpcMove(final L1NpcInstance npc) {
        _npc = npc;
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
    private static int _targetDirection(final int h, final int x, final int y, final int tx, final int ty) {
        try {
            final float dis_x = Math.abs(x - tx); // X點方向距離
            final float dis_y = Math.abs(y - ty); // Y點方向距離
            final float dis = Math.max(dis_x, dis_y); // 取回2者最大質
            if (dis == 0) {
                return h; // 距離為0表示不須改變面向
            }
            final int avg_x = (int) Math.floor((dis_x / dis) + 0.59f); // 上下左右がちょっと優先な丸め
            final int avg_y = (int) Math.floor((dis_y / dis) + 0.59f); // 上下左右がちょっと優先な丸め
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
            _log.error(e.getLocalizedMessage(), e);
        }
        return h;
    }

    /**
     * 往指定面向移動1格
     *
     * @param dir 方向
     */
    @Override
    public void setDirectionMove(final int dir) {
        if (dir >= 0) {
            int locx = _npc.getX();//目前座標X
            int locy = _npc.getY();//目前座標Y
            locx += HEADING_TABLE_X[dir];
            locy += HEADING_TABLE_Y[dir];
            _npc.setHeading(dir);
            if (_npc.setPassableInstance()) {
                // 解除舊座標障礙宣告
                this._npc.getMap().setPassable(_npc.getLocation(), true);
            }
            _npc.setX(locx);
            _npc.setY(locy);
            if (_npc.setPassableInstance()) {
                // 新增座標障礙宣告
                _npc.getMap().setPassable(_npc.getLocation(), false);
            }
            //送出移動封包 動作編號0
            _npc.broadcastPacketAll(new S_MoveCharPacket(_npc));
            /*
             *精準目標特效跟隨
            ArrayList<L1EffectInstance> effectlist = _npc.get_TrueTargetEffectList();
            if (effectlist != null) {//身上具有精準目標特效
                for (L1EffectInstance effect : effectlist) {
                    effect.Follow(_npc);
                }
            }
            */
            _npc.setTrueTargetMoveSpeed();
            // movement_distancet 超過最大移動距離
            if (_npc.getMovementDistance() > 0) {
                if ((_npc instanceof L1GuardInstance) || (_npc instanceof L1MerchantInstance) || (_npc instanceof L1MonsterInstance)) {
                    if (_npc.getLocation().getLineDistance(new Point(_npc.getHomeX(), _npc.getHomeY())) > _npc.getMovementDistance()) {
                        _npc.teleport(_npc.getHomeX(), _npc.getHomeY(), _npc.getHeading());
                    }
                }
            }
            // 古魯丁墓園怪物
            if ((_npc.getNpcTemplate().get_npcId() >= 45912) && (_npc.getNpcTemplate().get_npcId() <= 45916)) {
                if ((_npc.getX() < 32591 || _npc.getX() > 32644) && (_npc.getY() < 32643 || _npc.getY() > 32688) && (_npc.getMapId() == 4)) {
                    _npc.teleport(_npc.getHomeX(), _npc.getHomeY(), _npc.getHeading());
                }
            }
        }
    }

    /**
     * 清除錯誤次數
     */
    @Override
    public void clear() {
        if (_list != null) {
            _list = null;
        }
        _error = 0;
    }

    /**
     * 追蹤方向返回
     */
    public int moveDirection(int x, int y) { // 目標點Ｘ目標點Ｙ
        return moveDirection(x, y, _npc.getLocation().getLineDistance(new Point(x, y)));
    }

    // 目標までの距離に應じて最適と思われるルーチンで進む方向を返す
    public int moveDirection(int x, int y, double d) { // 目標點Ｘ 目標點Ｙ 目標までの距離
        int dir = 0;
        if ((_npc.hasSkillEffect(40)) && (d >= 2D)) { // 被施放黑闇之影 距離超過2 (追蹤停止)
            return -1;
        } else if (d > 30D) {  // 距離超過30 (追蹤停止)
            return -1;
        } else if (d > L1NpcInstance.DISTANCE) { // 距離超過courceRange (重新取回移動方向)
            dir = _targetDirection(_npc.getHeading(), _npc.getX(), _npc.getY(), x, y);
            dir = checkObject(dir);
            dir = openDoor(dir);
        } else { // 距離20格內決定最短距離方向
            dir = _serchCource(x, y);
            if (dir == -1) { // 目標までの經路がなっかた場合はとりあえず近づいておく
                _error++;
                if (_error < 10) {
                    dir = _targetDirection(_npc.getHeading(), _npc.getX(), _npc.getY(), x, y);
                    if (!_exsistCharacterBetweenTarget(dir)) {
                        dir = checkObject(dir);
                        dir = openDoor(dir);
                    }
                } else {
                    dir = Random.nextInt(8);//隨機面向
                    if (!_exsistCharacterBetweenTarget(dir)) {
                        dir = checkObject(dir);
                        dir = openDoor(dir);
                    }
                    clear();//清除錯誤次數
                }
            }
        }
        return dir;
    }

    /**
     * 前進方向障礙者攻擊判斷
     *
     * @param dir
     * @return
     */
    private boolean _exsistCharacterBetweenTarget(final int dir) {
        try {
            // 執行者非MOB
            if (!(_npc instanceof L1MonsterInstance)) {
                return false;
            }
            // 無首要目標
            if (_npc.is_now_target() == null) {
                return false;
            }
            final int locX = _npc.getX();
            final int locY = _npc.getY();
            final int targetX = locX + HEADING_TABLE_X[dir];
            ;
            final int targetY = locY + HEADING_TABLE_Y[dir];
            final ArrayList<L1Object> objects = World.get().getVisibleObjects(_npc, 1);
            for (final Iterator<L1Object> iter = objects.iterator(); iter.hasNext(); ) {
                final L1Object object = iter.next();
                boolean isCheck = false;
                if ((object.getX() == targetX) && (object.getY() == targetY) && (object.getMapId() == _npc.getMapId())) {
                    isCheck = true;
                }
                if (isCheck) {
                    boolean isHate = false;
                    // 判斷障礙
                    if (object instanceof L1PcInstance) {// 障礙者是玩家
                        final L1PcInstance pc = (L1PcInstance) object;
                        if (!pc.isGhost() && !pc.isGmInvis()) { // 鬼魂模式及GM隱身排除
                            isHate = true;
                        }
                    } else if (object instanceof L1PetInstance) {// 障礙者是寵物
                        isHate = true;
                    } else if (object instanceof L1SummonInstance) {// 障礙者是 召換獸
                        isHate = true;
                    }
                    if (isHate) {
                        // 重新設置障礙者為攻擊目標
                        final L1Character cha = (L1Character) object;
                        _npc._hateList.add(cha, 0);
                        _npc._target = cha;
                        return true;
                    }
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    // 目標の逆方向を返す
    public int targetReverseDirection(int tx, int ty) { // 目標點Ｘ目標點Ｙ
        int dir = targetDirection(tx, ty);
        dir += 4;
        if (dir > 7) {
            dir -= 8;
        }
        return dir;
    }

    /**
     * 目標點方向計算
     *
     * @param tx 目標X
     * @param ty 目標Y
     * @return
     */
    public int targetDirection(int tx, int ty) {
        float dis_x = Math.abs(_npc.getX() - tx); // Ｘ方向のターゲットまでの距離
        float dis_y = Math.abs(_npc.getY() - ty); // Ｙ方向のターゲットまでの距離
        float dis = Math.max(dis_x, dis_y); // ターゲットまでの距離
        if (dis == 0) {
            return getHeading(); // 同じ位置ならいま向いてる方向を返しとく
        }
        int avg_x = (int) Math.floor((dis_x / dis) + 0.59f); // 上下左右がちょっと優先な丸め
        int avg_y = (int) Math.floor((dis_y / dis) + 0.59f); // 上下左右がちょっと優先な丸め
        int dir_x = 0;
        int dir_y = 0;
        if (_npc.getX() < tx) {
            dir_x = 1;
        }
        if (_npc.getX() > tx) {
            dir_x = -1;
        }
        if (_npc.getY() < ty) {
            dir_y = 1;
        }
        if (_npc.getY() > ty) {
            dir_y = -1;
        }
        if (avg_x == 0) {
            dir_x = 0;
        }
        if (avg_y == 0) {
            dir_y = 0;
        }
        if ((dir_x == 1) && (dir_y == -1)) {
            return 1; // 上
        }
        if ((dir_x == 1) && (dir_y == 0)) {
            return 2; // 右上
        }
        if ((dir_x == 1) && (dir_y == 1)) {
            return 3; // 右
        }
        if ((dir_x == 0) && (dir_y == 1)) {
            return 4; // 右下
        }
        if ((dir_x == -1) && (dir_y == 1)) {
            return 5; // 下
        }
        if ((dir_x == -1) && (dir_y == 0)) {
            return 6; // 左下
        }
        if ((dir_x == -1) && (dir_y == -1)) {
            return 7; // 左
        }
        if ((dir_x == 0) && (dir_y == -1)) {
            return 0; // 左上
        }
        return getHeading(); // ここにはこない。はず
    }

    public int getHeading() {
        return _heading;
    }

    public void setHeading(int i) {
        _heading = i;
    }

    // 目標までの最短經路の方向を返す
    // ※目標を中心とした探索範圍のマップで探索
    private int _serchCource(final int x, final int y) { // 目標點Ｘ 目標點Ｙ
        int i;
        int courceRange = 20;
        final int locCenter = courceRange + 1;
        final int diff_x = x - locCenter; // Ｘの實際のロケーションとの差
        final int diff_y = y - locCenter; // Ｙの實際のロケーションとの差
        int[] locBace = {_npc.getX() - diff_x, _npc.getY() - diff_y, 0, 0}; // Ｘ
        // Ｙ
        // 方向
        // 初期方向
        final int[] locNext = new int[4];
        int[] locCopy;
        final int[] dirFront = new int[5];
        final boolean serchMap[][] = new boolean[locCenter * 2 + 1][locCenter * 2 + 1];
        final LinkedList<int[]> queueSerch = new LinkedList<int[]>();
        // 設置探索地圖
        for (int j = courceRange * 2 + 1; j > 0; j--) {
            for (i = courceRange - Math.abs(locCenter - j); i >= 0; i--) {
                serchMap[j][locCenter + i] = true;
                serchMap[j][locCenter - i] = true;
            }
        }
        // 初期方向の設置
        final int[] firstCource = {2, 4, 6, 0, 1, 3, 5, 7};
        for (i = 0; i < 8; i++) {
            System.arraycopy(locBace, 0, locNext, 0, 4);
            this._moveLocation(locNext, firstCource[i]);
            if ((locNext[0] - locCenter == 0) && (locNext[1] - locCenter == 0)) {
                // 最短經路が見つかった場合:鄰
                return firstCource[i];
            }
            if (serchMap[locNext[0]][locNext[1]]) {
                final int tmpX = locNext[0] + diff_x;
                final int tmpY = locNext[1] + diff_y;
                boolean found = false;
                /*switch (i) {
                case 0:
                    found = _npc.getMap().isPassableDna(tmpX, tmpY + 1, i);
                    break;
                case 1:
                    found = _npc.getMap().isPassableDna(tmpX - 1, tmpY + 1, i);
                    break;
                case 2:
                    found = _npc.getMap().isPassableDna(tmpX - 1, tmpY, i);
                    break;
                case 3:
                    found = _npc.getMap().isPassableDna(tmpX - 1, tmpY - 1, i);
                    break;
                case 4:
                    found = _npc.getMap().isPassableDna(tmpX, tmpY - 1, i);
                    break;
                case 5:
                    found = _npc.getMap().isPassableDna(tmpX + 1, tmpY - 1, i);
                    break;
                case 6:
                    found = _npc.getMap().isPassableDna(tmpX + 1, tmpY, i);
                    break;
                case 7:
                    found = _npc.getMap().isPassableDna(tmpX + 1, tmpY + 1, i);
                    break;
                }*/
                if (i == 0) {
                    found = _npc.getMap().isPassable(tmpX, tmpY + 1, i);
                } else if (i == 1) {
                    found = _npc.getMap().isPassable(tmpX - 1, tmpY + 1, i);
                } else if (i == 2) {
                    found = _npc.getMap().isPassable(tmpX - 1, tmpY, i);
                } else if (i == 3) {
                    found = _npc.getMap().isPassable(tmpX - 1, tmpY - 1, i);
                } else if (i == 4) {
                    found = _npc.getMap().isPassable(tmpX, tmpY - 1, i);
                } else if (i == 5) {
                    found = _npc.getMap().isPassable(tmpX + 1, tmpY - 1, i);
                } else if (i == 6) {
                    found = _npc.getMap().isPassable(tmpX + 1, tmpY, i);
                } else if (i == 7) {
                    found = _npc.getMap().isPassable(tmpX + 1, tmpY + 1, i);
                }
                if (found) {// 移動經路があった場合
                    locCopy = new int[4];
                    System.arraycopy(locNext, 0, locCopy, 0, 4);
                    locCopy[2] = firstCource[i];
                    locCopy[3] = firstCource[i];
                    queueSerch.add(locCopy);
                }
                serchMap[locNext[0]][locNext[1]] = false;
            }
        }
        locBace = null;
        // 最短經路を探索
        while (queueSerch.size() > 0) {
            locBace = queueSerch.removeFirst();
            this._getFront(dirFront, locBace[2]);
            for (i = 4; i >= 0; i--) {
                System.arraycopy(locBace, 0, locNext, 0, 4);
                this._moveLocation(locNext, dirFront[i]);
                if ((locNext[0] - locCenter == 0) && (locNext[1] - locCenter == 0)) {
                    return locNext[3];
                }
                if (serchMap[locNext[0]][locNext[1]]) {
                    final int tmpX = locNext[0] + diff_x;
                    final int tmpY = locNext[1] + diff_y;
                    boolean found = false;
                    /*switch (i) {
                        case 0:
                            found = _npc.getMap().isPassableDna(tmpX, tmpY + 1, i);
                            break;
                        case 1:
                            found = _npc.getMap().isPassableDna(tmpX - 1,
                                    tmpY + 1, i);
                            break;
                        case 2:
                            found = _npc.getMap().isPassableDna(tmpX - 1, tmpY, i);
                            break;
                        case 3:
                            found = _npc.getMap().isPassableDna(tmpX - 1,
                                    tmpY - 1, i);
                            break;
                        case 4:
                            found = _npc.getMap().isPassableDna(tmpX, tmpY - 1, i);
                            break;
                    }*/
                    if (i == 0) {
                        found = _npc.getMap().isPassable(tmpX, tmpY + 1, i);
                    } else if (i == 1) {
                        found = _npc.getMap().isPassable(tmpX - 1, tmpY + 1, i);
                    } else if (i == 2) {
                        found = _npc.getMap().isPassable(tmpX - 1, tmpY, i);
                    } else if (i == 3) {
                        found = _npc.getMap().isPassable(tmpX - 1, tmpY - 1, i);
                    } else if (i == 4) {
                        found = _npc.getMap().isPassable(tmpX, tmpY - 1, i);
                    }
                    if (found) { // 移動經路があった場合
                        locCopy = new int[4];
                        System.arraycopy(locNext, 0, locCopy, 0, 4);
                        locCopy[2] = dirFront[i];
                        queueSerch.add(locCopy);
                    }
                    serchMap[locNext[0]][locNext[1]] = false;
                }
            }
            locBace = null;
        }
        return -1; // 目標までの經路がない場合
    }

    private void _moveLocation(int[] ary, int dir) {
        ary[0] += DIR_TABLE[dir][0];
        ary[1] += DIR_TABLE[dir][1];
        ary[2] = dir;
    }

    private void _getFront(int[] ary, int d) {
        if (d == 1) {
            ary[4] = 2;
            ary[3] = 0;
            ary[2] = 1;
            ary[1] = 3;
            ary[0] = 7;
        } else if (d == 2) {
            ary[4] = 2;
            ary[3] = 4;
            ary[2] = 0;
            ary[1] = 1;
            ary[0] = 3;
        } else if (d == 3) {
            ary[4] = 2;
            ary[3] = 4;
            ary[2] = 1;
            ary[1] = 3;
            ary[0] = 5;
        } else if (d == 4) {
            ary[4] = 2;
            ary[3] = 4;
            ary[2] = 6;
            ary[1] = 3;
            ary[0] = 5;
        } else if (d == 5) {
            ary[4] = 4;
            ary[3] = 6;
            ary[2] = 3;
            ary[1] = 5;
            ary[0] = 7;
        } else if (d == 6) {
            ary[4] = 4;
            ary[3] = 6;
            ary[2] = 0;
            ary[1] = 5;
            ary[0] = 7;
        } else if (d == 7) {
            ary[4] = 6;
            ary[3] = 0;
            ary[2] = 1;
            ary[1] = 5;
            ary[0] = 7;
        } else if (d == 0) {
            ary[4] = 2;
            ary[3] = 6;
            ary[2] = 0;
            ary[1] = 1;
            ary[0] = 7;
        }
    }

    /**
     * 對於前進方向是否有障礙的確認
     * 第一點移動產生障礙
     * 轉向2,3點判斷
     *
     * @param h 方向
     * @return
     */
    @Override
    public int checkObject(final int h) {
        if ((h >= 0) && (h <= 7)) {
            final int x = _npc.getX();
            final int y = _npc.getY();
            final int h2 = _heading2[h];
            final int h3 = _heading3[h];
            if (_npc.getMap().isPassable(x, y, h, _npc)) {
                return h;
            } else if (_npc.getMap().isPassable(x, y, h2, _npc)) {
                return h2;
            } else if (_npc.getMap().isPassable(x, y, h3, _npc)) {
                return h3;
            }
        }
        return -1;
    }

    @Override
    public int openDoor(final int h) {
        if (h != -1) {
            if (!_npc.getMap().isDoorPassable(_npc.getX(), _npc.getY(), h, _npc)) {
                return -1;
            }
        }
        return h;
    }
}
