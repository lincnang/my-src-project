package com.add.NewAuto;

import com.lineage.config.ThreadPoolSetNew;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.SprTable;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.thread.PcAutoThreadPoolNew;
import com.lineage.server.types.Point;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 自動練功 自由版
 *
 * @author 老爹
 */
public class AutoAttack2020_2 extends TimerTask {
    protected static final byte[] HEADING_TABLE_X = {0, 1, 1, 1, 0, -1, -1, -1};
    protected static final byte[] HEADING_TABLE_Y = {-1, -1, 0, 1, 1, 1, 0, -1};
    protected static int _heading0[] = {7, 0, 1, 2, 3, 4, 5, 6};
    protected static int _heading1[] = {1, 2, 3, 4, 5, 6, 7, 0};
    private static Log _log = LogFactory.getLog(AutoAttack2020_2.class);
    private static Random _random = new Random();
    protected int[][] DIR_TABLE = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}};
    L1Character target;
    Random random = new Random();
    private int MoveSerch = 0;
    private L1PcInstance pc = null;
    private int 更新畫面 = 0;
    private int 卡牆用 = 0;
    private int h = -1;
    private int error = 0;
    private ArrayList<Integer> _list = new ArrayList<Integer>();
    private ScheduledFuture<?> _timer;
    private int courceRange = 200;

    public AutoAttack2020_2(L1PcInstance user) {
        pc = user;
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
            pc.getMap().setPassable(pc.getLocation(), true);
            short mapId = pc.getTeleportMapId();
            if ((pc.isDead()) && (mapId != 9101)) {
                return;
            }
            int x = pc.getTeleportX();
            int y = pc.getTeleportY();
            int head = pc.getTeleportHeading();
            // 玩家傳送前所在地圖 by 老爹
            L1Map map = L1WorldMap.get().getMap(mapId);
            if ((!map.isInMap(x, y)) && (!pc.isGm())) {
                x = pc.getX();
                y = pc.getY();
                mapId = pc.getMapId();
            }
            World.get().moveVisibleObject(pc, mapId);
            pc.setLocation(x, y, mapId);// 設置角色新座標
            pc.setHeading(head);
            pc.setOleLocX(x);
            pc.setOleLocY(y);
            boolean isUnderwater = pc.getMap().isUnderwater();
            pc.sendPackets(new S_MapID(pc, pc.getMapId(), isUnderwater));
            if ((!pc.isGhost()) && (!pc.isGmInvis()) && (!pc.isInvisble())) {
                pc.broadcastPacketAll(new S_OtherCharPacks(pc));
            }
            if (pc.isReserveGhost()) {
                pc.endGhost();
            }
            pc.sendPackets(new S_OwnCharPack(pc));
            pc.removeAllKnownObjects();
            pc.updateObject();
            pc.sendVisualEffectAtTeleport();
            pc.sendPackets(new S_CharVisualUpdate(pc));
            pc.killSkillEffectTimer(32);// 冥想術
            pc.setCallClanId(0);
            final HashSet<L1PcInstance> subjects = new HashSet<L1PcInstance>();
            subjects.add(pc);
            if (!pc.isGhost()) {
                // 可以攜帶寵物
                if (pc.getMap().isTakePets()) {
                    // 寵物的跟隨移動
                    for (final L1NpcInstance petNpc : pc.getPetList().values()) {
                        // 主人身邊隨機座標取回
                        final L1Location loc = pc.getLocation().randomLocation(3, false);
                        int nx = loc.getX();
                        int ny = loc.getY();
                        if ((pc.getMapId() == 5125) || (pc.getMapId() == 5131) || (pc.getMapId() == 5132) || (pc.getMapId() == 5133) || (pc.getMapId() == 5134)) { // 寵物戰戰場
                            nx = 32799 + ThreadLocalRandom.current().nextInt(5) - 3;
                            ny = 32864 + ThreadLocalRandom.current().nextInt(5) - 3;
                        }
                        // 設置副本編號
                        petNpc.set_showId(pc.get_showId());
                        teleport(petNpc, nx, ny, mapId, head);
                        if (petNpc instanceof L1SummonInstance) { // 召喚獸的跟隨移動
                            final L1SummonInstance summon = (L1SummonInstance) petNpc;
                            pc.sendPackets(new S_NPCPack_Summon(summon, pc));
                        } else if (petNpc instanceof L1PetInstance) { // 寵物的跟隨移動
                            final L1PetInstance pet = (L1PetInstance) petNpc;
                            pc.sendPackets(new S_NPCPack_Pet(pet, pc));
                        }
                        for (final L1PcInstance visiblePc : World.get().getVisiblePlayer(petNpc)) {
                            // 畫面內可見人物 認識更新
                            visiblePc.removeKnownObject(petNpc);
                            if (visiblePc.get_showId() == petNpc.get_showId()) {
                                subjects.add(visiblePc);
                            }
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
            }
            /**
             * 可見物更新處理
             */
            for (L1PcInstance updatePc : subjects) {
                updatePc.updateObject();
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
    /**
     * 先確認是否有無敵人<br>
     * true = 有敵人 (執行飛行) <br>
     * false = 沒有敵人 (繼續往下執行)<br>
     *
     * @param pc
     * @return
     */
	/*private boolean ConfirmTheBOSS(final L1PcInstance pc) {
		try {
			boolean ok = false;
			for (L1Object objid : World.get().getVisibleObjects(pc, 5)) {
				if (objid instanceof L1NpcInstance) {
					L1NpcInstance _npc = (L1NpcInstance) objid;
					if (_npc.getNpcTemplate().is_boss()) {
						FlyChecking(pc);
						ok = true;
						break;
					}
				}
			}
			if (ok) {
				return true;
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}*/

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

    public void begin() {
        final int timeMillis = 350;// 1秒
        _timer = PcAutoThreadPoolNew.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
    }

    /**
     * 停止動作釋放資源
     */
    public void stop() {
        try {
            target = null;
            更新畫面 = 0;
            卡牆用 = 0;
            h = -1;
            _list.clear();
            pc.setIsAuto(false);
            PcAutoThreadPoolNew.get().cancel(_timer, false);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void run() {
        try {
            if (pc == null || pc.isDead() || pc.getCurrentHp() <= 0 || !pc.IsAuto() || pc.getNetConnection().getIp() == null || pc.getWeapon() == null || !pc.getMap().isAutoBot()) {
                stop();
            } else {
				/*if (ConfirmTheBOSS(pc)) {
					return;
				}*/
                if (ConfirmTheEnemy(pc)) {
                    return;
                }
                if (CheckStatus(pc)) {
                    return;
                }
                if (pc.getInventory().getWeight240() >= 240) {
                    // 82 此物品太重了，所以你無法攜帶。
                    //pc.sendPackets(new S_ServerMessage(82));
                    pc.sendPackets(new S_SystemMessage("此物品太重了，所以你無法進行掛機。"));
                    return;
                }
                if (target != null) {
                    if (target.isDead()) {// 目標已經死亡
                        target = null;
                        return;// 重新執行
                    }
                    if (target instanceof L1MonsterInstance) {// 目標進入遁地或是飛空狀態
                        L1MonsterInstance mob = (L1MonsterInstance) target;
                        if (mob.getHiddenStatus() == 1 || mob.getHiddenStatus() == 2 || mob.getHiddenStatus() == 3) {
                            target = null;
                            return;// 重新執行
                        }
                    }
                    int Ranged = 1;// 武器最初攻擊距離
                    if (pc.getWeapon() != null) {// 判定攻擊距離
                        Ranged = pc.getWeapon().getItem().getRange();
                        if (Ranged == -1 || Ranged > 3) {// 拿遠距離武器狀態
                            Ranged = 8; // 遠距離武器限制最遠距離
                        } else {
                            Ranged = 1;
                        }
                    }
                    final int location = // 與怪物的距離(直線)
                            pc.getLocation().getTileLineDistance(target.getLocation());
                    if (location <= Ranged && // 與怪物距離已經到達可以攻擊的距離
                            pc.glanceCheck(target.getX(), target.getY())) {// 並且我與怪物距離內並無任何障礙物
                        if (AttackMon(pc, target)) {// 執行攻擊一下
                            target = null;
                            return;
                        }
                    } else {// 還未到可以攻擊的距離
                        if (!Move(pc, target)) {
                            target = null;
                            return;
                        }
                    }
                } else {
                    if (!SearchTarget(pc)) {
                        target = null;//當怪沒有
                        TimeUnit.MILLISECONDS.sleep(500);//延遲0.5
                        FlyChecking(pc);//飛
                    } else {
                        if (target == null) { // 如果目標等於空
                            //target = null;//當怪沒有
                            TimeUnit.MILLISECONDS.sleep(500);//延遲0.5
                            //pc.setNA432(3);
                            FlyChecking(pc);//飛
                        }
                    }
                }
            }
        } catch (Exception e) {
            stop();
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 先確認是否有無敵人<br>
     * true = 異常 (執行飛行) <br>
     * false = 正常 (繼續往下執行)<br>
     *
     */
    private boolean CheckStatus(final L1PcInstance pc) {
        try {
            if (pc.isParalyzed()) {
                return true;
            }
            if (pc.hasSkillEffect(L1SkillId.CURSE_PARALYZE)) {
                return true;
            }
            if (pc.hasSkillEffect(L1SkillId.SHOCK_STUN)) {
                return true;
            }
            if (pc.hasSkillEffect(L1SkillId.KINGDOM_STUN)) {
                return true;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    /**
     * 無目標行走 true = 移動結束 false = 無法移動
     *
     */
    private boolean GoallessWalking(final L1PcInstance pc) {
        try {
            if (h == -1) {
                do {
                    Random r = new Random();
                    h = r.nextInt(7);
                } while (!pc.getMap().isPassable(pc.getX() + DIR_TABLE[h][0], pc.getY() + DIR_TABLE[h][1], h, pc));
            } else {
                h = checkObject(pc, h);
            }
            if (h != -1) {
                setDirectionMove(pc, h);// 人物移動
                //int SPEED = pc.getAcceleratorChecker().getRightInterval(AcceleratorChecker.ACT_TYPE.MOVE);
                int SPEED = SprTable.get().getMoveSpeed(this.pc.getTempCharGfx(), this.pc.getCurrentWeapon());
                if (SPEED < ThreadPoolSetNew.WALK_SPEED) {
                    SPEED = ThreadPoolSetNew.WALK_SPEED;
                }
                SPEED = (SPEED * ThreadPoolSetNew.REXT) / 100;
                TimeUnit.MILLISECONDS.sleep(ThreadPoolSetNew.WALK_SPEED);
                return true;
            } else {
                h = -1;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    /**
     * 傳送前先檢查
     *
     */
    private void FlyChecking(final L1PcInstance pc) {
        try {
            if (卡牆用 >= 30) {
                L1Location newLocation = pc.getLocation().randomLocation(25, true);
                final int newX = newLocation.getX();
                final int newY = newLocation.getY();
                pc.setTeleportX(newX);
                pc.setTeleportY(newY);
                pc.setTeleportMapId((short) pc.getMapId());
                pc.setTeleportHeading(pc.getHeading());
                teleportation(pc);
                target = null;
                卡牆用 = 0;
            }
            if (ThreadPoolSetNew.TELEPORT_NEED_COUNT == -1) {
                target = null;
                if (GoallessWalking(pc)) {
                }
            } else {
                if (pc.IsAutoTeleport()) {// 是否開啟內掛瞬移
                    if (pc.getInventory().checkItem(40308, ThreadPoolSetNew.TELEPORT_NEED_COUNT)) {
                        pc.getInventory().consumeItem(40308, ThreadPoolSetNew.TELEPORT_NEED_COUNT);
                        L1Location newLocation = pc.getLocation().randomLocation(25, true);
                        final int newX = newLocation.getX();
                        final int newY = newLocation.getY();
                        pc.setTeleportX(newX);
                        pc.setTeleportY(newY);
                        pc.setTeleportMapId((short) pc.getMapId());
                        pc.setTeleportHeading(pc.getHeading());
                        teleportation(pc);
                        target = null;
                    } else {
                        pc.sendPackets(new S_ServerMessage("無道具或金幣供瞬移使用"));
                        target = null;
                        if (GoallessWalking(pc)) {
                        }
                    }
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 先確認是否有無敵人<br>
     * true = 有敵人 (執行飛行) <br>
     * false = 沒有敵人 (繼續往下執行)<br>
     *
     */
    private boolean ConfirmTheEnemy(final L1PcInstance pc) {
        try {
            if (pc.IsAttackTeleportHp()) {//已經開啟被攻擊立即瞬移狀態並且假設正在被攻擊中
                FlyChecking(pc);
                pc.setIsAttackTeleportHp(false);
                return true;
            }
            if (pc.IsEnemyTeleport()) {// 遇見仇人進行順移開啟
                boolean ok = false;
                for (L1Object objid : World.get().getVisibleObjects(pc, 5)) {
                    if (objid instanceof L1PcInstance) {
                        L1PcInstance _pc = (L1PcInstance) objid;
                        if (pc.isInEnemyList(_pc.getName())) {
                            FlyChecking(pc);
                            ok = true;
                            break;
                        }
                    }
                }
                if (ok) {
                    return true;
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    /**
     * true = 有搜尋到目標 false = 無搜尋到目標 搜尋怪物
     *
     */
    private boolean SearchTarget(final L1PcInstance pc) {
        try {
            if (pc.hasSkillEffect(L1SkillId.STATUS_CURSE_PARALYZED)) {
                return false;
            }
            if (pc.hasSkillEffect(L1SkillId.STATUS_POISON_PARALYZED)) {
                return false;
            }
            if (pc.hasSkillEffect(L1SkillId.STATUS_FREEZE)) {
                return false;
            }
            L1MonsterInstance mob1 = null;
            double max_d = 15;
            for (int i = 10; i >= 0; i--) {
                for (L1Object objid : World.get().getVisibleObjectsForAuto(pc, i)) {
                    if (objid instanceof L1MonsterInstance) {
                        L1MonsterInstance mob = (L1MonsterInstance) objid;
                        if (mob.isDead()) {
                            continue;
                        }
                        if (mob.getHiddenStatus() == 1 || mob.getHiddenStatus() == 2 || mob.getHiddenStatus() == 3) {
                            continue;
                        }
                        if (pc.glanceCheck(mob.getX(), mob.getY())) {
                            mob1 = mob;
                            break;
                        }
                        boolean HaveTargetList = false;
                        for (Integer id : pc.TargetList()) {
                            if (mob.getId() == id) {
                                HaveTargetList = true;
                            }
                        }
                        if (HaveTargetList) {
                            continue;
                        }
                        final double d = pc.getLocation().getLineDistance(new Point(mob.getX(), mob.getY()));
                        if (d <= max_d && d > 0) {
                            max_d = d;
                            mob1 = mob;
                        }
                    }
                }
            }
            if (mob1 == null) {
                //FlyChecking(pc);
                return false;
            } else {
				/*if (pc.getNA432() < 0) {
					pc.setNA432(3);
				}*/
                target = mob1;
                return true;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    /**
     * 技能攻擊
     *
     */
    private boolean SkillDmg(final L1PcInstance pc, final L1Character targets) {
        try {
            if (pc.getCurrentHp() > pc.getHH() && pc.getCurrentMp() > pc.getMM()) {
                if (pc.AttackSkillSize() > 0 && ThreadLocalRandom.current().nextInt(100) <= ThreadPoolSetNew.ATTACK_SKILL_RANDOM) {
                    final int skillid = pc.AttackSkillId();
                    if (!pc.isSkillDelay()) {
                        final L1Skills skill = SkillsTable.get().getTemplate(skillid);
                        if (pc.getCurrentMp() >= skill.getMpConsume()) {
                            final L1SkillUse skillUse = new L1SkillUse();
                            skillUse.handleCommands(pc, skillid, targets.getId(), targets.getX(), targets.getY(), skill.getBuffDuration(), L1SkillUse.TYPE_NORMAL);
                        }
                    }
                    if (!targets.isDead()) {
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    /**
     * 確認怪物目標進行攻擊<br>
     * true = 怪物已經死亡<br>
     * false = 怪物尚未死亡<br>
     *
     */
    private boolean AttackMon(final L1PcInstance pc, final L1Character targets) {
        try {
            //pc.setNA432(-1);
            if (pc.TargetListSize() > 0) {
                pc.clearTargetList();
            }
            卡牆用 = 0;
            if (SkillDmg(pc, targets)) {
                return true;
            }
            targets.onAction(pc);
            //int SPEED = pc.getAcceleratorChecker().getRightInterval
            //		(AcceleratorChecker.ACT_TYPE.ATTACK);//依據變身檔判定的攻擊速度
            int SPEED = SprTable.get().getAttackSpeed(this.pc.getTempCharGfx(), this.pc.getCurrentWeapon() + 1);
            if (SPEED < ThreadPoolSetNew.ATTACK_SPEED) {// 攻擊速度低於我設定的速度
                SPEED = ThreadPoolSetNew.ATTACK_SPEED;//
            }
            SPEED = (SPEED * ThreadPoolSetNew.REXT) / 100;// 速度在*上速度微調器*/
            TimeUnit.MILLISECONDS.sleep(ThreadPoolSetNew.ATTACK_SPEED);// 停頓時間
            if (!targets.isDead()) {
                return false;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    /**
     * 移動判斷 <br>
     * true = 正確往敵人前進一格<br>
     * false = 尋找不到可以前進的方向<br>
     *
     */
    private boolean Move(final L1PcInstance pc, final L1Character targets) {
        try {
			/*if (pc.getNA432() == 0) {
				target = null;
				TimeUnit.MILLISECONDS.sleep(500);//延遲0.5
				pc.setNA432(3);
				FlyChecking(pc);//飛
			}*/
            // 尋找前進方向
            final int dir = moveDirection(pc, targets.getX(), targets.getY());
            if (dir >= 0) {// 無障礙
                MoveSerch++;
                if (MoveSerch == 5) {
                    target = null;
                    MoveSerch = 0;
                }
                setDirectionMove(pc, dir);// 人物移動
                //int SPEED = pc.getAcceleratorChecker().getRightInterval(AcceleratorChecker.ACT_TYPE.MOVE);
                int SPEED = SprTable.get().getMoveSpeed(this.pc.getTempCharGfx(), this.pc.getCurrentWeapon());
                if (SPEED < ThreadPoolSetNew.WALK_SPEED) {
                    SPEED = ThreadPoolSetNew.WALK_SPEED;
                }
                SPEED = (SPEED * ThreadPoolSetNew.REXT) / 100;
                TimeUnit.MILLISECONDS.sleep(ThreadPoolSetNew.WALK_SPEED);
                return true;
            } else {// 有障礙
                卡牆用++;
                if (卡牆用 > ThreadPoolSetNew.STOP_COUNT) {
                    pc.addTargetList(targets.getId());
                }
                if (卡牆用 >= 30) {
                    TimeUnit.MILLISECONDS.sleep(500);//延遲0.5
                    FlyChecking(pc);
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    /**
     * 計算走到目標處的初始面向
     *
     * @param targetsx 目標X
     * @param targetsy 目標Y
     * @return dir 前進方向
     */
    private int moveDirection(final L1PcInstance pc, final int targetsx, final int targetsy) { // 目標點Ｘ目標點Ｙ
        return moveDirection(pc, targetsx, targetsy, pc.getLocation().getLineDistance(new Point(targetsx, targetsy)));
    }

    /**
     * 走到目標處的距離
     *
     * @param d        與目標直線距離
     * @return dir 前進方向
     */
    private int moveDirection(final L1PcInstance pc, final int targetsx, final int targetsy, final double d) {
        int dir = 0;// 方向為0
        if (d > 30) {// 距離超過30 (追蹤停止)
            return -1;
        } else {
            dir = _serchCource(pc, targetsx, targetsy);// 搜尋最短路徑
            if (dir == -1) { // 前進方向有障礙物
                dir = _targetDirection(pc.getHeading(), pc.getX(), pc.getY(), targetsx, targetsy);
                dir = checkObject(pc, dir);
            }
        }
        return dir;
    }

    protected int _serchCource(L1PcInstance pc, int x, int y) {
        int i;
        int locCenter = courceRange + 1;
        int diff_x = x - locCenter; // 與X實際位置的差異
        int diff_y = y - locCenter; // Ｙ實際差
        int[] locBace = {pc.getX() - diff_x, pc.getY() - diff_y, 0, 0}; // Ｘ
        // Ｙ
        // 方向
        // 初期方向
        int[] locNext = new int[4];
        int[] locCopy;
        int[] dirFront = new int[5];
        boolean serchMap[][] = new boolean[locCenter * 2 + 1][locCenter * 2 + 1];
        LinkedList<int[]> queueSerch = new LinkedList<int[]>();
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
            int h2 = _heading0[h];
            int h3 = _heading1[h];
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
    private void setDirectionMove(final L1PcInstance pc, final int dir) {
        int locx = pc.getX();
        int locy = pc.getY();
        locx += HEADING_TABLE_X[dir];
        locy += HEADING_TABLE_Y[dir];
        pc.setHeading(dir);
        // 解除舊座標障礙宣告
        pc.getMap().setPassable(pc.getLocation(), true);
        pc.setX(locx);
        pc.setY(locy);
        pc.getMap().setPassable(pc.getLocation(), false);
        pc.broadcastPacketAll(new S_MoveCharPacket(pc));
        pc.sendPackets(new S_MoveCharPacket(pc));
        更新畫面++;
        if (更新畫面 > 7) {
            L1Location newLocation = pc.getLocation().randomLocation(1, true);
            int newX = newLocation.getX();
            int newY = newLocation.getY();
            pc.setTeleportX(newX);
            pc.setTeleportY(newY);
            pc.setTeleportMapId((short) pc.getMapId());
            pc.setTeleportHeading(dir);
            teleportation(pc);
            更新畫面 = 0;
        }
    }
}