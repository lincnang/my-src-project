package com.add.NewAuto;

import com.lineage.server.clientpackets.AcceleratorChecker;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.*;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.serverpackets.*;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.types.Point;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.lineage.server.model.skill.L1SkillId.MEDITATION;

/**
 * 掛機
 *
 * @author 老爹
 */
public class AutoAttackWalk extends TimerTask {
    protected static final byte[] HEADING_TABLE_X = {0, 1, 1, 1, 0, -1, -1, -1};
    protected static final byte[] HEADING_TABLE_Y = {-1, -1, 0, 1, 1, 1, 0, -1};
    protected static int _heading2[] = {7, 0, 1, 2, 3, 4, 5, 6};
    protected static int _heading3[] = {1, 2, 3, 4, 5, 6, 7, 0};
    private static Log _log = LogFactory.getLog(AutoAttackWalk.class);
    protected int[][] DIR_TABLE = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}};
    /**
     * 攻擊對像
     */
    L1Character target = null;
    private L1PcInstance pc = null;
    /**
     * 與怪物之間的距離
     */
    private int X = 0;
    /**
     * 內掛畫面更新條件
     */
    private int Y = 0;
    /**
     * 與怪物之間的距離
     */
    private int Ranged = 0;
    private int update = 0;
    /**
     * 內掛畫面更新條件
     */
    private int _updateautocount = 0;
    private Timer _timeHandler = new Timer(true);
    private int _ErrorCount;// 尋找路徑錯誤次數
    private int courceRange = 20;

    public AutoAttackWalk(L1PcInstance user) {
        pc = user;
    }

    /**
     * 搜尋怪物
     *
     */
    private static L1Character SearchTarget(L1PcInstance pc) {
        try {
            for (int i = 1; i <= 8; i++) {
                for (L1Object objid : World.get().getVisibleObjects(pc, i)) {
                    if (objid instanceof L1MonsterInstance) {
                        L1MonsterInstance mob = (L1MonsterInstance) objid;
                        if (mob.isDead()) {
                            continue;
                        }
                        if (mob.getHiddenStatus() == 1 || mob.getHiddenStatus() == 2 || mob.getHiddenStatus() == 3) {
                            continue;
                        }
                        if (pc.glanceCheck(mob.getX(), mob.getY())) {
                            if (pc.get_AutoAttackNpcid() != mob.getId()) {
                                pc.set_AutoAttackNpcid(0);
                                pc.set_AutoAttackCount(0);
                            }
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

    /**
     * 隨機傳送處理
     *
     */
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
            if ((!map.isInMap(x, y)) && (!pc.isGm())) {
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
            pc.killSkillEffectTimer(32);// 冥想術
            pc.setCallClanId(0);
            if (!pc.isGhost()) {
                // 可以攜帶寵物
                if (pc.getMap().isTakePets()) {
                    // 寵物的跟隨移動
                    for (L1NpcInstance petNpc : pc.getPetList().values()) {
                        L1Location loc = pc.getLocation().randomLocation(3, false);
                        int nx = loc.getX();
                        int ny = loc.getY();
                        teleport(petNpc, nx, ny, pc.getMapId(), pc.getHeading());
                        if (petNpc instanceof L1SummonInstance) { // 召喚獸的跟隨移動
                            L1SummonInstance summon = (L1SummonInstance) petNpc;
                            pc.sendPackets(new S_NPCPack_Summon(summon, pc));
                        } else if (petNpc instanceof L1PetInstance) { // 寵物的跟隨移動
                            L1PetInstance pet = (L1PetInstance) petNpc;
                            pc.sendPackets(new S_NPCPack_Pet(pet, pc));
                        }
                        for (L1PcInstance visiblePc : World.get().getVisiblePlayer(petNpc)) {
                            // 畫面內可見人物更新
                            visiblePc.removeKnownObject(petNpc);
                        }
                    }
                }
            }
            /**祭司移動*/
            if (pc.getHierarchs() != null) {
                L1Location loc = pc.getLocation().randomLocation(3, false);
                int nx = loc.getX();
                int ny = loc.getY();
                teleport(pc.getHierarchs(), nx, ny, pc.getMapId(), pc.getHeading());
                pc.sendPackets(new S_NPCPack_Hierarch(pc.getHierarchs()));
                pc.getHierarchs().set_showId(pc.get_showId());
                for (L1PcInstance visiblePc : World.get().getVisiblePlayer(pc.getHierarchs())) {
                    visiblePc.removeKnownObject(pc.getHierarchs());
                    if (visiblePc.get_showId() == pc.getHierarchs().get_showId()) {
                        visiblePc.removeKnownObject(pc.getHierarchs());
                    }
                }
            }
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
            // 新增座標障礙宣告
            if (!pc.isGmInvis()) {// 排除GM隱身
                pc.getMap().setPassable(pc.getLocation(), false);
            }
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

    private static void broadcastPacket(L1Character cha, ServerBasePacket packet) {
        ArrayList<L1PcInstance> list = null;
        list = World.get().getVisiblePlayer(cha);
        for (L1PcInstance pc : list) {
            pc.sendPackets(packet);
        }
    }

    /**
     * 寵物的傳送
     *
     */
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

    /**
     * 開始執行線程
     */
    public void begin() {
        _timeHandler.schedule(this, 250, 250);
        GeneralThreadPool.get().execute(this);
    }

    /**
     * 停止動作釋放資源
     */
    public void stop() {
        try {
            // 移除時間軸 釋放資源
            if (this.cancel()) {
                // 停止時間軸
                _timeHandler.purge();
            }
            target = null;
            Ranged = 0;
            X = 0;
            Y = 0;
            _updateautocount = 0;
            update = 0;
            pc.set_Test_Auto(false);
            // 運行失效資源回收
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void run() {
        try {
            if (pc == null || pc.isDead() || pc.getCurrentHp() <= 0 || !pc.Test_Auto() || pc.getNetConnection().getIp() == null || pc.getWeapon() == null) {
                stop();
                return;
            }
            if (_updateautocount >= 8) {
                UpdateChaPack();
                _updateautocount = 0;
            }
            if (target != null) {
                if (target.isDead()) {
                    target = null;
                }
                if (target != null) {
                    if (!pc.glanceCheck(target.getX(), target.getY())) {
                        target = null;
                    }
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
                    if (pc.get_AutoAttackCount() > 30 && pc.get_AutoAttackNpcid() != 0) {
                        target = null;
                    }
                }
				/*if (pc.getWeapon() != null) {
				Ranged = pc.getWeapon().getItem().getRange();
				if (Ranged > 2) {
					Ranged = 8;
				} else {
					Ranged = 1;
				}
			}*/
                if (pc.getWeapon().getItem().getType1() == 20 || pc.getWeapon().getItem().getType1() == 62) {
                    Ranged = 10;
                } else {
                    Ranged = pc.getWeapon().getItem().getRange();
                    // Ranged = 2;
                }
                if (target != null) {
                    final int location = pc.getLocation().getTileLineDistance(target.getLocation());
                    if (location <= Ranged) {
                        AttackMon(pc, target);
                    } else {
                        Move(pc, target);
                    }
                }
            } else {
				/*target = SearchTarget(pc);
				if (target == null) {
					if (X == 0 || Y == 0) {
						Random random = new Random();
						X = (pc.getX() - 10) + random.nextInt(20);
						Y = (pc.getY() - 10) + random.nextInt(20);
						while(!pc.glanceCheck(X, Y)){
							X = (pc.getX() - 10) + random.nextInt(20);
							Y = (pc.getY() - 10) + random.nextInt(20);
						}
					}
					final int dir = moveDirection(pc, X, Y);
					if (dir > 0) {
						setDirectionMove(pc, dir);//人物移動
					} else {
						隨機瞬移();
						L1Location newLocation = pc.getLocation().randomLocation(2, true);
						int newX = newLocation.getX();
						int newY = newLocation.getY();
						pc.setTeleportX(newX);
						pc.setTeleportY(newY);
						pc.setTeleportMapId((short) pc.getMapId());
						pc.setTeleportHeading(pc.getHeading());
						teleportation(pc);
						X = 0;
					}
				}
			}
		} catch (Exception e) {
			stop();
			_log.error(e.getLocalizedMessage(), e);
		}
	}*/
                target = SearchTarget(pc);
                if (target == null) {
                    TimeUnit.MILLISECONDS.sleep(1800);
                    _updateautocount = 0;
                    L1Location newLocation = pc.getLocation().randomLocation(200, true);
                    int newX = newLocation.getX();
                    int newY = newLocation.getY();
                    pc.setTeleportX(newX);
                    pc.setTeleportY(newY);
                    pc.setTeleportMapId((short) pc.getMapId());
                    pc.setTeleportHeading(5);
                    teleportation(pc);
                }
            }
        } catch (Exception e) {
            pc.set_Test_Auto(false);
            stop();
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void 隨機瞬移() {
        L1Teleport.randomTeleport(pc, true);
        try {
            //	TimeUnit.MILLISECONDS.sleep(ThreadPoolSet.MoveTeleport);
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            // TODO 自動生成的 catch 塊
            // e.printStackTrace();
        }
        // System.out.println("完成人物瞬移");
    }

    /**
     * 確認怪物目標進行攻擊
     *
     */
    private void AttackMon(L1PcInstance pc, L1Character target) {
        try {
            if (!pc.Test_Auto()) {
                pc.sendPackets(new S_SystemMessage("停止自動練功"));
                return;
            }
            if (pc.glanceCheck(target.getX(), target.getY()) && !target.isDead()) {
                int sleep = pc.getAcceleratorChecker().getRightInterval(AcceleratorChecker.ACT_TYPE.ATTACK) + 100;
                if (sleep < 250) {
                    sleep = 250;
                }
                if (pc.get_AutoAttackNpcid() == 0) {//目前攻擊目標Objid
                    pc.set_AutoAttackNpcid(target.getId());
                }
                TimeUnit.MILLISECONDS.sleep(sleep);
                //TimeUnit.MILLISECONDS.sleep(ThreadPoolSet.ATTACTK_SLEEP);
                target.onAction(pc);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 移動判斷
     *
     */
    private void Move(L1PcInstance pc, L1Character target) {
        try {
            if (!pc.Test_Auto()) {
                pc.sendPackets(new S_SystemMessage("停止自動練功"));
                return;
            }
            if (!pc.glanceCheck(target.getX(), target.getY())) {
                return;
            }
            int sleep = pc.getAcceleratorChecker().getRightInterval(AcceleratorChecker.ACT_TYPE.MOVE);
            if (sleep < 300) {
                sleep = 300;
            }
            TimeUnit.MILLISECONDS.sleep(sleep);
            //TimeUnit.MILLISECONDS.sleep(ThreadPoolSet.MOVE_SLEEP);
            int location = pc.getLocation().getTileLineDistance(target.getLocation());
			/*if (pc.getWeapon() != null) {
			Ranged = pc.getWeapon().getItem().getRange();
			if (Ranged > 2) {
				Ranged = 10;
			}
		} else {
			Ranged = 1;
		}*/
            if (pc.getWeapon().getItem().getType1() == 20 || pc.getWeapon().getItem().getType1() == 62) {
                Ranged = 10;
            } else {
                Ranged = pc.getWeapon().getItem().getRange();
                // Ranged = 2;
            }
            if (location <= Ranged) {
                AttackMon(pc, target);
            } else {
                int dir = moveDirection(pc, target.getX(), target.getY());
                if (dir > 0) {
                    setDirectionMove(pc, dir);//人物移動
                } else {
                    L1Location newLocation = pc.getLocation().randomLocation(3, true);
                    int newX = newLocation.getX();
                    int newY = newLocation.getY();
                    pc.setTeleportX(newX);
                    pc.setTeleportY(newY);
                    pc.setTeleportMapId((short) pc.getMapId());
                    pc.setTeleportHeading(pc.getHeading());
                    teleportation(pc);
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 追蹤方向返回
     */
    private int moveDirection(L1PcInstance pc, int x, int y) { // 目標點Ｘ目標點Ｙ
        return moveDirection(pc, x, y, pc.getLocation().getLineDistance(new Point(x, y)));
    }

    public void clear() {
        _ErrorCount = 0;
    }

    private int moveDirection(L1PcInstance pc, int x, int y, double d) { // 目標點Ｘ 目標點Ｙ 目標距離
        int dir = 0;
        if ((pc.hasSkillEffect(40) == true) && (d >= 2D)) { // 被施放黑闇之影 距離超過2
            return -1;
        } else if (d > 30D) { // 距離超過30 (追蹤停止)
            return -1;
        } else if (d > L1NpcInstance.DISTANCE) { // 距離超過courceRange (重新取回移動方向)
            dir = _targetDirection(pc.getHeading(), pc.getX(), pc.getY(), x, y);
            dir = checkObject(pc, dir);
        } else { // 距離20格內決定最短距離方向
            dir = _serchCource(pc, x, y);// 搜尋最短路徑
            dir = checkObject(pc, dir);
            if (dir == -1) { // 前進方向有障礙物
                _ErrorCount++;
                if (_ErrorCount < 20) {
                    dir = _targetDirection(pc.getHeading(), pc.getX(), pc.getY(), x, y);
                    dir = checkObject(pc, dir);
                } else {// 嘗試錯誤次數20次以上
                    clear();// 清除錯誤次數
                    return -1;
                }
            } else {// 順利通行
                clear();// 清除錯誤次數
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

    /**
     * 判斷前進方向
     *
     */
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

    /**
     * 人物進行移動
     *
     */
    private void setDirectionMove(L1PcInstance pc, int dir) {
        update++;
        if (update > 8) {
            UpdateChaPack();
            update = 0;
        }
        int nx = 0;
        int ny = 0;
        nx = HEADING_TABLE_X[dir];
        ny = HEADING_TABLE_Y[dir];
        pc.setHeading(dir);
        final int nnx = pc.getX() + nx;
        final int nny = pc.getY() + ny;
        pc.getLocation().set(nnx, nny);
        broadcastPacket(pc, new S_MoveCharPacket(pc));//人物移動
        pc.broadcastPacketAll(new S_OtherCharPacks(pc));//更新其他玩家
        pc.sendPackets(new S_MoveCharPacket(pc));//自己移動
        pc.sendPackets(new S_CharVisualUpdate(pc));//更換武器
    }

    /**
     * 視野更新
     */
    private void UpdateChaPack() {
        try {
            if (pc.getOnlineStatus() == 0) {
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
            // 解除就座標障礙宣告
            pc.getMap().setPassable(pc.getLocation(), true);
            // 地圖水中狀態
            boolean isUnderwater = pc.getMap().isUnderwater();
            // 更新所在地圖
            pc.sendPackets(new S_MapID(pc, pc.getMapId(), isUnderwater));
            // 發送人物資訊給周邊物件
            if (!pc.isGhost() && !pc.isGmInvis() && !pc.isInvisble()) {
                pc.broadcastPacketAll(new S_OtherCharPacks(pc));
            }
            pc.sendPackets(new S_OwnCharPack(pc));
            pc.removeAllKnownObjects();
            pc.sendVisualEffectAtTeleport();
            pc.updateObject();
            pc.sendPackets(new S_CharVisualUpdate(pc));
            pc.killSkillEffectTimer(MEDITATION);
            pc.setCallClanId(0); // 唱後移動召喚無效
            HashSet<L1PcInstance> subjects = new HashSet<>();
            subjects.add(pc);
            if (!pc.isGhost()) {
                // 可以攜帶寵物
                if (pc.getMap().isTakePets()) {
                    // 寵物的跟隨移動
                    for (L1NpcInstance petNpc : pc.getPetList().values()) {
                        L1Location loc = pc.getLocation().randomLocation(3, false);
                        int nx = loc.getX();
                        int ny = loc.getY();
                        teleport(petNpc, nx, ny, pc.getMapId(), pc.getHeading());
                        if (petNpc instanceof L1SummonInstance) { // 召喚獸的跟隨移動
                            L1SummonInstance summon = (L1SummonInstance) petNpc;
                            pc.sendPackets(new S_NPCPack_Summon(summon, pc));
                        } else if (petNpc instanceof L1PetInstance) { // 寵物的跟隨移動
                            L1PetInstance pet = (L1PetInstance) petNpc;
                            pc.sendPackets(new S_NPCPack_Pet(pet, pc));
                        }
                        for (L1PcInstance visiblePc : World.get().getVisiblePlayer(petNpc)) {
                            // 畫面內可見人物更新
                            visiblePc.removeKnownObject(petNpc);
                        }
                    }
                }
                /**祭司移動*/
                if (pc.getHierarchs() != null) {
                    L1Location loc = pc.getLocation().randomLocation(3, false);
                    int nx = loc.getX();
                    int ny = loc.getY();
                    teleport(pc.getHierarchs(), nx, ny, pc.getMapId(), pc.getHeading());
                    pc.sendPackets(new S_NPCPack_Hierarch(pc.getHierarchs()));
                    pc.getHierarchs().set_showId(pc.get_showId());
                    for (L1PcInstance visiblePc : World.get().getVisiblePlayer(pc.getHierarchs())) {
                        visiblePc.removeKnownObject(pc.getHierarchs());
                        if (visiblePc.get_showId() == pc.getHierarchs().get_showId()) {
                            subjects.add(visiblePc);
                        }
                    }
                }
                // 魔法娃娃的跟隨移動
                if (!pc.getDolls().isEmpty()) {
                    // 主人身邊隨機座標取回
                    L1Location loc = pc.getLocation().randomLocation(3, false);
                    int nx = loc.getX();
                    int ny = loc.getY();
                    Object[] dolls = pc.getDolls().values().toArray();
                    for (Object obj : dolls) {
                        L1DollInstance doll = (L1DollInstance) obj;
                        teleport(doll, nx, ny, pc.getMapId(), pc.getHeading());
                        pc.sendPackets(new S_NPCPack_Doll(doll, pc));
                        for (L1PcInstance visiblePc : World.get().getVisiblePlayer(doll)) {
                            // 畫面內可見人物更新
                            visiblePc.removeKnownObject(doll);
                        }
                    }
                }
            }
            for (L1PcInstance updatePc : subjects) {
                updatePc.updateObject();
            }
            pc.setTeleport(false);
            //            pc.getMap().setPassable(pc.getLocation(), false); // 20240815
            pc.getPetModel();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}