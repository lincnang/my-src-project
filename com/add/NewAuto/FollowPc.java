package com.add.NewAuto;

import com.lineage.config.ThreadPoolSet;
import com.lineage.server.clientpackets.AcceleratorChecker;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.lock.CharSkillReading;
import com.lineage.server.model.Instance.L1DollInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Trade;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.types.Point;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * 跟隨系統
 *
 * @author 老爹
 */
public class FollowPc extends TimerTask {
    protected static final byte[] HEADING_TABLE_X = {0, 1, 1, 1, 0, -1, -1, -1};
    protected static final byte[] HEADING_TABLE_Y = {-1, -1, 0, 1, 1, 1, 0, -1};
    public static L1Character target = null;
    protected static int[] _heading2 = {7, 0, 1, 2, 3, 4, 5, 6};
    protected static int[] _heading3 = {1, 2, 3, 4, 5, 6, 7, 0};
    private static Log _log = LogFactory.getLog(FollowPc.class);
    private final Timer _timeHandler = new Timer(true);
    protected int[][] DIR_TABLE = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}};
    private L1PcInstance pc;
    private int update = 0;
    private int courceRange = 20;

    public FollowPc(L1PcInstance user) {
        pc = user;
    }

    private static void teleportation(L1PcInstance pc) {
        try {
            if (pc == null) {
                return;
            }
            if (pc.getOnlineStatus() == 0) {
                return;
            }
            if (pc.getNetConnection().getIp() == null) {
                return;
            }
            if (pc.getNetConnection() == null) {
                return;
            }
            if (pc.isDead()) {
                return;
            }
            if (pc.isTeleport()) {
                return;
            }
            if (pc.getTradeID() != 0) {
                L1Trade trade = new L1Trade();
                trade.tradeCancel(pc);
            }
            // 解除舊座標障礙宣告
            pc.getMap().setPassable(pc.getLocation(), true);
            short mapId = pc.getTeleportMapId();
            int x = pc.getTeleportX();
            int y = pc.getTeleportY();
            int head = pc.getTeleportHeading();
            L1Map map = L1WorldMap.get().getMap(mapId);
            if ((!map.isInMap(x, y)) && (!pc.isGm() && (pc.isGm()))) {
                x = pc.getX();
                y = pc.getY();
                mapId = pc.getMapId();
            }
            World.get().moveVisibleObject(pc, mapId);
            pc.setLocation(x, y, mapId);//設置角色新座標
            pc.setHeading(head);
            pc.setOleLocX(x);
            pc.setOleLocY(y);
            boolean isUnderwater = pc.getMap().isUnderwater();
            pc.sendPackets(new S_MapID(pc, pc.getMapId(), isUnderwater));
            if ((!pc.isGhost()) && (!pc.isGmInvis()) && (!pc.isInvisble())) {
                pc.broadcastPacketAll(new S_OtherCharPacks(pc));
            }
            pc.sendPackets(new S_OwnCharPack(pc));
            pc.removeAllKnownObjects();
            pc.updateObject();
            pc.sendPackets(new S_CharVisualUpdate(pc));
            pc.setCallClanId(0);
            if (!pc.getDolls().isEmpty()) {
                // 主人身邊隨機座標取回
                L1Location loc = pc.getLocation().randomLocation(3, false);
                int nx = loc.getX();
                int ny = loc.getY();
                Object[] dolls = pc.getDolls().values().toArray();
                for (Object obj : dolls) {
                    L1DollInstance doll = (L1DollInstance) obj;
                    teleport(doll, nx, ny, mapId, head);
                    pc.sendPackets(new S_NPCPack_Doll(doll, pc));
                    // 設置副本編號
                    doll.set_showId(pc.get_showId());
                }
            }
            if (pc.hasSkillEffect(167)) {// 風之枷鎖
                pc.sendPackets(new S_PacketBoxWindShackle(pc.getId(), pc.getSkillEffectTimeSec(167)));
            }
            /*
            // 新增座標障礙宣告
            if (!pc.isGmInvis()) {// 排除GM隱身
                pc.getMap().setPassable(pc.getLocation(), false);
            }
            */
            pc.getPetModel();// 恢復寵物目前模式
        } catch (Exception e) {
            // 解除傳送鎖定
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            // 解除傳送鎖定
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
        }
    }

    /**
     * 目標點方向計算
     *
     * @param h  目前面向
     * @param x  目前X
     * @param y  目前Y
     * @param tx 目標X
     * @param ty 目標Y
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
            _log.error(e.getLocalizedMessage(), e);
        }
        return h;
    }

    private static void teleport(L1NpcInstance npc, int x, int y, short map, int head) {
        try {
            World.get().moveVisibleObject(npc, map);
            L1WorldMap.get().getMap(npc.getMapId()).setPassable(npc.getX(), npc.getY(), true, 2);
            npc.setX(x);
            npc.setY(y);
            npc.setMap(map);
            npc.setHeading(head);
            //            L1WorldMap.get().getMap(npc.getMapId()).setPassable(npc.getX(), npc.getY(), false, 2);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void begin() {
        _timeHandler.schedule(this, 250, 250);
        GeneralThreadPool.get().execute(this);
    }

    public void stop() {
        try {
            if (this.cancel()) {
                _timeHandler.purge();
            }
            target = null;
            update = 0;
            pc.setFollowId(0);
            pc.setFollow(false);
            pc.setFollowMode(0);
            pc.sendPackets(new S_SystemMessage("已設定停止或跟隨目標異常 跟隨停止"));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void run() {
        try {
            if (pc == null || pc.isDead() || pc.getCurrentHp() <= 0 || !pc.IsFollow() || pc.getNetConnection() == null) {
                stop();
            } else {
                L1Object targetM = World.get().findObject(pc.getFollowId());
                if (targetM == null) {
                    stop();
                    return;
                } else {
                    if (targetM instanceof L1PcInstance) {
                        final L1PcInstance targetPc = (L1PcInstance) targetM;
                        if (targetPc.getBeFollowId() == 0) {
                            stop();
                            return;
                        }
                    }
                }
                if (pc.getFollowMode() == 1) {
                    if (target != null) {
                        if (target.isDead()) {
                            target = null;
                        }
                        if (target != null) {
                            if (target instanceof L1MonsterInstance) {
                                L1MonsterInstance mob = (L1MonsterInstance) target;
                                if (mob.getHiddenStatus() == 1 || mob.getHiddenStatus() == 2 || mob.getHiddenStatus() == 3) {
                                    target = null;
                                }
                            }
                        }
                        if (target != null) {
                            int Ranged = 1;
                            if (pc.getWeapon() != null) {
                                Ranged = pc.getWeapon().getItem().getRange();
                                if (Ranged == -1 || Ranged > 2) {
                                    Ranged = 8;
                                }
                            }
                            final int location = pc.getLocation().getTileLineDistance(target.getLocation());
                            if (location <= Ranged && pc.glanceCheck(target.getX(), target.getY())) {
                                AttackMon(pc, target);
                                int SPEED = pc.getAcceleratorChecker().getRightInterval(AcceleratorChecker.ACT_TYPE.ATTACK);
                                if (SPEED < ThreadPoolSet.ATTACK_SPEED) {
                                    SPEED = ThreadPoolSet.ATTACK_SPEED;
                                }
                                SPEED = (SPEED * ThreadPoolSet.REXT) / 100;
                                TimeUnit.MILLISECONDS.sleep(SPEED);
                            } else {
                                Move(pc, target);
                                int SPEED = pc.getAcceleratorChecker().getRightInterval(AcceleratorChecker.ACT_TYPE.MOVE);
                                if (SPEED < ThreadPoolSet.WALK_SPEED) {
                                    SPEED = ThreadPoolSet.WALK_SPEED;
                                }
                                SPEED = (SPEED * ThreadPoolSet.REXT) / 100;
                                TimeUnit.MILLISECONDS.sleep(SPEED);
                            }
                        }
                    } else {
                        target = SearchTarget(pc);
                        if (target == null) {
                            L1Object target1 = World.get().findObject(pc.getFollowId());
                            if (target1 != null) {
                                if (target1 instanceof L1PcInstance) {
                                    final L1PcInstance targetPc = (L1PcInstance) target1;
                                    MoveCha(pc, targetPc);
                                }
                            } else {
                                stop();
                            }
                        }
                    }
                } else {
                    FollowCha(pc);
                }
            }
        } catch (Exception e) {
            stop();
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void FollowCha(final L1PcInstance pc) {
        L1Object target1 = World.get().findObject(pc.getFollowId());
        if (target1 != null) {
            if (target1 instanceof L1PcInstance) {
                final L1PcInstance targetPc = (L1PcInstance) target1;
                if (pc.getLocation().getTileLineDistance(targetPc.getLocation()) > 15 || pc.getMapId() != targetPc.getMapId()) {
                    pc.setTeleportX(targetPc.getX());
                    pc.setTeleportY(targetPc.getY());
                    pc.setTeleportMapId((short) targetPc.getMapId());
                    pc.setTeleportHeading(pc.getHeading());
                    teleportation(pc);
                    return;
                }
                MoveCha(pc, targetPc);
                if (CharSkillReading.get().spellCheck(pc.getId(), 35)) {
                    double FollowPcHp = targetPc.getMaxHp() * 0.5;
                    if (targetPc.getCurrentHp() < FollowPcHp) {
                        L1Skills skill = SkillsTable.get().getTemplate(35);
                        new L1SkillUse().handleCommands(pc, 35, targetPc.getId(), targetPc.getX(), targetPc.getY(), skill.getBuffDuration(), 0);
                    }
                }
            }
        }
    }

    private L1Character SearchTarget(L1PcInstance pc) {
        try {
            if (pc.hasSkillEffect(L1SkillId.STATUS_CURSE_PARALYZED)) {
                return null;
            }
            if (pc.hasSkillEffect(L1SkillId.STATUS_POISON_PARALYZED)) {
                return null;
            }
            if (pc.hasSkillEffect(L1SkillId.STATUS_FREEZE)) {
                return null;
            }
            L1Object target1 = World.get().findObject(pc.getFollowId());
            if (target1 != null) {
                if (target1 instanceof L1PcInstance) {
                    final L1PcInstance targetPc = (L1PcInstance) target1;
                    if (pc.getLocation().getTileLineDistance(targetPc.getLocation()) > 15 || pc.getMapId() != targetPc.getMapId()) {
                        pc.setTeleportX(targetPc.getX());
                        pc.setTeleportY(targetPc.getY());
                        pc.setTeleportMapId((short) targetPc.getMapId());
                        pc.setTeleportHeading(pc.getHeading());
                        teleportation(pc);
                        return null;
                    }
                    L1Object obj = World.get().findObject(targetPc.getAttackId());
                    if (obj instanceof L1MonsterInstance) {
                        final L1MonsterInstance mob = (L1MonsterInstance) obj;
                        if (mob.isDead()) {
                            return null;
                        }
                        if (mob.getHiddenStatus() == 1 || mob.getHiddenStatus() == 2 || mob.getHiddenStatus() == 3) {
                            return null;
                        }
                        if (pc.glanceCheck(mob.getX(), mob.getY())) {
                            return mob;
                        }
                    }
                }
            }
            return null;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
            return null;
        }
    }

    private void AttackMon(L1PcInstance pc, L1Character targets) {
        try {
            if (!pc.IsFollow()) {
                pc.sendPackets(new S_SystemMessage("停止跟隨"));
                return;
            }
            if (pc.glanceCheck(targets.getX(), targets.getY()) && !targets.isDead()) {
                targets.onAction(pc);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void MoveCha(L1PcInstance pc, L1Character targets) {
        try {
            if (!pc.IsFollow()) {
                pc.sendPackets(new S_SystemMessage("停止跟隨"));
                return;
            }
            int location = pc.getLocation().getTileLineDistance(targets.getLocation());
            int Ranged = 2;
            if (location > Ranged) {
                int dir = moveDirection(pc, targets.getX(), targets.getY());
                if (dir >= 0) {
                    setDirectionMove(pc, dir);
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void Move(L1PcInstance pc, L1Character targets) {
        try {
            if (!pc.IsFollow()) {
                pc.sendPackets(new S_SystemMessage("停止跟隨"));
                return;
            }
            int location = pc.getLocation().getTileLineDistance(targets.getLocation());
            int Ranged = 1;
            if (pc.getWeapon() != null) {
                Ranged = pc.getWeapon().getItem().getRange();
                if (Ranged == -1 || Ranged > 2) {
                    Ranged = 8;
                }
            }
            if (location <= Ranged && pc.glanceCheck(targets.getX(), targets.getY())) {
                AttackMon(pc, targets);
            } else {
                int dir = moveDirection(pc, targets.getX(), targets.getY());
                setDirectionMove(pc, dir);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 追蹤方向返回
     */
    private int moveDirection(L1PcInstance pc, int targetsx, int targetsy) { // 目標點Ｘ目標點Ｙ
        return moveDirection(pc, targetsx, targetsy, pc.getLocation().getLineDistance(new Point(targetsx, targetsy)));
    }

    private int moveDirection(L1PcInstance pc, int targetsx, int targetsy, double d) {
        int dir = 0;
        if ((pc.hasSkillEffect(40) == true) && (d >= 2D)) {
            return -1;
        } else if (d > 100D) {
            return -1;
        } else if (d > 100) {
            dir = _targetDirection(pc.getHeading(), pc.getX(), pc.getY(), targetsx, targetsy);
            dir = checkObject(pc, dir);
        } else {
            dir = _serchCource(pc, targetsx, targetsy);
            dir = checkObject(pc, dir);
            if (dir == -1) {
                dir = _targetDirection(pc.getHeading(), pc.getX(), pc.getY(), targetsx, targetsy);
                dir = checkObject(pc, dir);
            }
        }
        return dir;
    }

    protected int _serchCource(L1PcInstance pc, int x, int y) {
        int i;
        int locCenter = courceRange + 1;
        int diff_x = x - locCenter; // Ｘ實際差
        int diff_y = y - locCenter; // Ｙ實際差
        int[] locBace = {pc.getX() - diff_x, pc.getY() - diff_y, 0, 0}; // Ｘ
        // Ｙ
        // 方向
        // 初期方向
        int[] locNext = new int[4];
        int[] locCopy;
        int[] dirFront = new int[5];
        boolean serchMap[][] = new boolean[locCenter * 2 + 1][locCenter * 2 + 1];
        LinkedList<int[]> queueSerch = new LinkedList<>();
        // 探索用設定
        for (int j = courceRange * 2 + 1; j > 0; j--) {
            for (i = courceRange - Math.abs(locCenter - j); i >= 0; i--) {
                serchMap[j][locCenter + i] = true;
                serchMap[j][locCenter - i] = true;
            }
        }
        // 初期方向設置
        int[] firstCource = {2, 4, 6, 0, 1, 3, 5, 7};
        for (i = 0; i < 8; i++) {
            System.arraycopy(locBace, 0, locNext, 0, 4);
            _moveLocation(locNext, firstCource[i]);
            if (locNext[0] - locCenter == 0 && locNext[1] - locCenter == 0) {
                // 最短經路見場合:鄰
                return firstCource[i];
            }
            if (serchMap[locNext[0]][locNext[1]]) {
                int tmpX = locNext[0] + diff_x;
                int tmpY = locNext[1] + diff_y;
                boolean found = false;
                switch (i) {
                    case 0:
                        found = pc.getMap().isPassable(tmpX, tmpY + 1, i, pc);
                        break;
                    case 1:
                        found = pc.getMap().isPassable(tmpX - 1, tmpY + 1, i, pc);
                        break;
                    case 2:
                        found = pc.getMap().isPassable(tmpX - 1, tmpY, i, pc);
                        break;
                    case 3:
                        found = pc.getMap().isPassable(tmpX - 1, tmpY - 1, i, pc);
                        break;
                    case 4:
                        found = pc.getMap().isPassable(tmpX, tmpY - 1, i, pc);
                        break;
                    case 5:
                        found = pc.getMap().isPassable(tmpX + 1, tmpY - 1, i, pc);
                        break;
                    case 6:
                        found = pc.getMap().isPassable(tmpX + 1, tmpY, i, pc);
                        break;
                    case 7:
                        found = pc.getMap().isPassable(tmpX + 1, tmpY + 1, i, pc);
                        break;
                }
                if (found) {// 移動經路場合
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
        // 最短經路探索
        while (queueSerch.size() > 0) {
            locBace = queueSerch.removeFirst();
            _getFront(dirFront, locBace[2]);
            for (i = 4; i >= 0; i--) {
                System.arraycopy(locBace, 0, locNext, 0, 4);
                _moveLocation(locNext, dirFront[i]);
                if (locNext[0] - locCenter == 0 && locNext[1] - locCenter == 0) {
                    return locNext[3];
                }
                if (serchMap[locNext[0]][locNext[1]]) {
                    int tmpX = locNext[0] + diff_x;
                    int tmpY = locNext[1] + diff_y;
                    boolean found = false;
                    switch (i) {
                        case 0:
                            found = pc.getMap().isPassable(tmpX, tmpY + 1, i, pc);
                            break;
                        case 1:
                            found = pc.getMap().isPassable(tmpX - 1, tmpY + 1, i, pc);
                            break;
                        case 2:
                            found = pc.getMap().isPassable(tmpX - 1, tmpY, i, pc);
                            break;
                        case 3:
                            found = pc.getMap().isPassable(tmpX - 1, tmpY - 1, i, pc);
                            break;
                        case 4:
                            found = pc.getMap().isPassable(tmpX, tmpY - 1, i, pc);
                            break;
                    }
                    if (found) {// 移動經路場合
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
        return -1; // 目標經路場合
    }

    private void _moveLocation(int[] ary, int dir) {
        ary[0] += DIR_TABLE[dir][0];
        ary[1] += DIR_TABLE[dir][1];
        ary[2] = dir;
    }

    private void _getFront(int[] ary, int d) {
        switch (d) {
            case 1:
                ary[4] = 2;
                ary[3] = 0;
                ary[2] = 1;
                ary[1] = 3;
                ary[0] = 7;
                break;
            case 2:
                ary[4] = 2;
                ary[3] = 4;
                ary[2] = 0;
                ary[1] = 1;
                ary[0] = 3;
                break;
            case 3:
                ary[4] = 2;
                ary[3] = 4;
                ary[2] = 1;
                ary[1] = 3;
                ary[0] = 5;
                break;
            case 4:
                ary[4] = 2;
                ary[3] = 4;
                ary[2] = 6;
                ary[1] = 3;
                ary[0] = 5;
                break;
            case 5:
                ary[4] = 4;
                ary[3] = 6;
                ary[2] = 3;
                ary[1] = 5;
                ary[0] = 7;
                break;
            case 6:
                ary[4] = 4;
                ary[3] = 6;
                ary[2] = 0;
                ary[1] = 5;
                ary[0] = 7;
                break;
            case 7:
                ary[4] = 6;
                ary[3] = 0;
                ary[2] = 1;
                ary[1] = 5;
                ary[0] = 7;
                break;
            case 0:
                ary[4] = 2;
                ary[3] = 6;
                ary[2] = 0;
                ary[1] = 1;
                ary[0] = 7;
                break;
        }
    }

    private int checkObject(L1PcInstance pc, int h) {
        if ((h >= 0) && (h <= 7)) {
            int x = pc.getX();
            int y = pc.getY();
            int h2 = _heading2[h];
            int h3 = _heading3[h];
            if (pc.getMap().isPassable(x, y, h, pc)) {
                return h;
            } else if (pc.getMap().isPassable(x, y, h2, pc)) {
                return h2;
            } else if (pc.getMap().isPassable(x, y, h3, pc)) {
                return h3;
            }
        }
        return -1;
    }

    private void setDirectionMove(L1PcInstance pc, int dir) {
        int locx = pc.getX();
        int locy = pc.getY();
        locx += HEADING_TABLE_X[dir];
        locy += HEADING_TABLE_Y[dir];
        pc.setHeading(dir);
        // 解除舊座標障礙宣告
        pc.getMap().setPassable(pc.getLocation(), true);
        pc.setX(locx);
        pc.setY(locy);
        //        pc.getMap().setPassable(pc.getLocation(), false);
        pc.broadcastPacketAll(new S_MoveCharPacket(pc));
        pc.sendPackets(new S_MoveCharPacket(pc));
        update++;
        if (update > 9) {
            L1Location newLocation = pc.getLocation().randomLocation(1, true);
            int newX = newLocation.getX();
            int newY = newLocation.getY();
            pc.setTeleportX(newX);
            pc.setTeleportY(newY);
            pc.setTeleportMapId((short) pc.getMapId());
            pc.setTeleportHeading(dir);
            teleportation(pc);
            update = 0;
        }
    }
}