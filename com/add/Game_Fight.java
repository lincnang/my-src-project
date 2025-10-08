/**
 * [非正原碼] 隊伍對決場系統
 */
package com.add;

import com.lineage.server.clientpackets.C_NPCAction;
import com.lineage.server.datatables.DoorSpawnTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_BlueMessage;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Game_Fight {
    public static final int STATUS_NONE = 0;
    public static final int STATUS_READY = 1;
    public static final int STATUS_PLAYING = 2;
    public static final int STATUS_CLEANING = 4;
    private static final Log _log = LogFactory.getLog(C_NPCAction.class);
    private static Game_Fight _instance;
    private final List<L1PcInstance> playerListA = new CopyOnWriteArrayList<L1PcInstance>();
    private final List<L1PcInstance> playerListB = new CopyOnWriteArrayList<L1PcInstance>();
    private final List<L1PcInstance> playerListC = new CopyOnWriteArrayList<L1PcInstance>();
    private final List<L1PcInstance> playerListD = new CopyOnWriteArrayList<L1PcInstance>();
    private volatile int _FightStatus = 0;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public static Game_Fight getInstance() {
        if (_instance == null) {
            _instance = new Game_Fight();
        }
        return _instance;
    }

    public void addPlayerList(L1PcInstance pc) {
        if (!this.playerListA.contains(pc)) {
            this.playerListA.add(pc);
        }
        if (!this.playerListB.contains(pc)) {
            this.playerListB.add(pc);
        }
    }

    public void addPlayerListA(L1PcInstance pc) {
        if (!this.playerListA.contains(pc)) {
            this.playerListA.add(pc);
            pc.getInventory().consumeItem(L1Config._2038, L1Config._2039);
            Game_Fight.this.sendMessage("藍隊人數 " + playerListA.size() + " 人，紅隊人數 " + playerListB.size() + " 人");
        }
        if ((getMembersCountA() == 1) && (getFightStatus() == 0)) {
            _log.info("藍隊 隊伍對決啟動");
        }
        if (running.compareAndSet(false, true)) {
            GeneralThreadPool.get().execute(new runFight());
        }
    }

    public void addPlayerListB(L1PcInstance pc) {
        if (!this.playerListB.contains(pc)) {
            this.playerListB.add(pc);
            Game_Fight.this.sendMessage("藍隊人數 " + playerListA.size() + " 人，紅隊人數 " + playerListB.size() + " 人");
            pc.getInventory().consumeItem(L1Config._2038, L1Config._2039);
        }
        if ((getMembersCountB() == 1) && (getFightStatus() == 0)) {
            _log.info("紅隊  隊伍對決啟動");
        }
        if (running.compareAndSet(false, true)) {
            GeneralThreadPool.get().execute(new runFight());
        }
    }

    public void addPlayerListC(L1PcInstance pc) {
        if (!this.playerListC.contains(pc)) {
            this.playerListC.add(pc);
        }
    }

    public void addPlayerListD(L1PcInstance pc) {
        if (!this.playerListD.contains(pc)) {
            this.playerListD.add(pc);
        }
    }

    public void removePlayerList(L1PcInstance pc) {
        if (this.playerListA.contains(pc)) {
            this.playerListA.remove(pc);
            pc.setATeam(false);
        }
        if (this.playerListB.contains(pc)) {
            this.playerListB.remove(pc);
            pc.setBTeam(false);
        }
    }

    public String enterA(L1PcInstance pc) {
        if (pc.getLevel() <= L1Config._2031) {
            pc.sendPackets(new S_SystemMessage("等級必須" + L1Config._2031 + "級以上，才可參加隊伍對決。"));
            return "";
        }
        if (getInstance().getFightStatus() == 4) {
            pc.sendPackets(new S_SystemMessage("目前隊伍對決場地正在清潔中，請稍候。"));
            return "";
        }
        if (getInstance().getFightStatus() == 2) {
            pc.sendPackets(new S_SystemMessage("目前隊伍對決已經開始，請等待下一場。"));
            return "";
        }
        if (this.playerListA.size() >= L1Config._2034) {
            pc.sendPackets(new S_SystemMessage("隊伍對決藍隊人數已經額滿，請等待下一場。"));
            return "";
        }
        if (!pc.getInventory().checkItem(L1Config._2038, L1Config._2039)) {
            pc.sendPackets(new S_ServerMessage(189));
            return "";
        }
        if (!playerListB.contains(pc)) {
            addPlayerListA(pc);
            // pc.sendPackets(new S_Paralysis(6, true));// 1:麻痺 5:昏迷 6:雙腳受困
            pc.setATeam(true);
            final L1Item item = ItemTable.get().getTemplate(L1Config._2038);
            pc.sendPackets(new S_SystemMessage("參加隊伍對決藍隊扣除報名費:" + item.getName() + "(" + L1Config._2039 + ")。"));
            final L1Item prize = ItemTable.get().getTemplate(L1Config._2040);
            pc.sendPackets(new S_SystemMessage("隊伍對決優勝可獲得:" + prize.getName() + "(" + L1Config._2041 + ")。"));
            L1Teleport.teleport(pc, 32867, 32598, (short) 515, pc.getHeading(), true);
        }
        return "";
    }

    public String enterB(L1PcInstance pc) {
        if (pc.getLevel() <= L1Config._2031) {
            pc.sendPackets(new S_SystemMessage("等級必須52級以上，才可參加隊伍對決。"));
            return "";
        }
        if (getInstance().getFightStatus() == 4) {
            pc.sendPackets(new S_SystemMessage("目前隊伍對決場地正在清潔中，請稍候。"));
            return "";
        }
        if (getInstance().getFightStatus() == 2) {
            pc.sendPackets(new S_SystemMessage("目前隊伍對決已經開始，請等待下一場。"));
            return "";
        }
        if (this.playerListB.size() >= L1Config._2034) {
            pc.sendPackets(new S_SystemMessage("隊伍對決紅隊人數已經額滿，請等待下一場。"));
            return "";
        }
        if (!pc.getInventory().checkItem(L1Config._2038, L1Config._2039)) {
            pc.sendPackets(new S_ServerMessage(189));
            return "";
        }
        if (!playerListA.contains(pc)) {
            addPlayerListB(pc);
            // pc.sendPackets(new S_Paralysis(6, true));
            pc.setBTeam(true);
            final L1Item item = ItemTable.get().getTemplate(L1Config._2038);
            pc.sendPackets(new S_SystemMessage("參加隊伍對決紅隊扣除報名費:" + item.getName() + "(" + L1Config._2039 + ")。"));
            final L1Item prize = ItemTable.get().getTemplate(L1Config._2040);
            pc.sendPackets(new S_SystemMessage("隊伍對決優勝可獲得:" + prize.getName() + "(" + L1Config._2041 + ")。"));
            L1Teleport.teleport(pc, 32862, 32679, (short) 515, pc.getHeading(), true);
        }
        return "";
    }

    // 查看
    public String LookATeam(L1PcInstance pc) {
        for (L1PcInstance tg : this.playerListA) {
            String name = String.valueOf(tg.getName());
            String level = String.valueOf(tg.getLevel());
            // String[] info = new String[] { " 藍隊玩家:" + name ," 等級:" + level };
            // pc.sendPackets(new S_SystemMessage(""+ info + ""));
            pc.sendPackets(new S_SystemMessage("【藍隊玩家】: " + name + " 【等級】: " + level + ""));
        }
        return "";
    }

    public String LookBTeam(L1PcInstance pc) {
        for (L1PcInstance tg : this.playerListB) {
            String name = String.valueOf(tg.getName());
            String level = String.valueOf(tg.getLevel());
            // String[] info = { " 紅隊玩家:" + name ," 等級:" + level };
            // pc.sendPackets(new S_SystemMessage(""+ info + ""));
            pc.sendPackets(new S_SystemMessage("【紅隊玩家】: " + name + " 【等級】: " + level + ""));
        }
        return "";
    }

    /*
     * private void Paralysis() { for (L1PcInstance pc : this.playerListA) {
     * pc.sendPackets(new S_Paralysis(6, false)); } for (L1PcInstance pc :
     * this.playerListB) pc.sendPackets(new S_Paralysis(6, false)); }
     */
    private boolean checkPlayerCount() {
        if (getMembersCountA() != getMembersCountB()) {
            setFightStatus(4);
            sendMessage("因兩方隊伍人數不相等，為公平起見，隊伍對決強制結束");
            if (getMembersCountA() < L1Config._2033 || getMembersCountB() < L1Config._2033) {
                setFightStatus(4);
                sendMessage("因一方隊伍人數不達最低限制，隊伍對決強制結束");
            }
            for (L1PcInstance pc : this.playerListA) {
                pc.setATeam(false);
                pc.getInventory().storeItem(L1Config._2038, L1Config._2039);
                final L1Item item = ItemTable.get().getTemplate(L1Config._2038);
                pc.sendPackets(new S_SystemMessage("隊伍對決退還報名費:" + item.getName() + "(" + L1Config._2039 + ")。"));
                // pc.sendPackets(new S_Paralysis(6, false));
                L1Teleport.teleport(pc, 33442, 32797, (short) 4, pc.getHeading(), true);
            }
            for (L1PcInstance pc : this.playerListB) {
                pc.setBTeam(false);
                pc.getInventory().storeItem(L1Config._2038, L1Config._2039);
                final L1Item item = ItemTable.get().getTemplate(L1Config._2038);
                pc.sendPackets(new S_SystemMessage("隊伍對決退還報名費:" + item.getName() + "(" + L1Config._2039 + ")。"));
                // pc.sendPackets(new S_Paralysis(6, false));
                L1Teleport.teleport(pc, 33442, 32797, (short) 4, pc.getHeading(), true);
            }
            clearMembers();
            return false;
        }
        return true;
    }

    private void WinTeam() {
        for (L1PcInstance pc : this.playerListA) {
            if ((pc != null) && (!pc.isDead()) && (!pc.isGhost()) && (pc.getMapId() == 515)) {
                addPlayerListC(pc);
            }
        }
        for (L1PcInstance pc : this.playerListB) {
            if ((pc != null) && (!pc.isDead()) && (!pc.isGhost()) && (pc.getMapId() == 515)) {
                addPlayerListD(pc);
            }
        }
        if (this.playerListC.size() == this.playerListD.size()) {
            sendMessage("雙方平手");
            for (L1PcInstance pc : this.playerListA) {
                final L1Item prize = ItemTable.get().getTemplate(L1Config._2040);
                pc.sendPackets(new S_SystemMessage("雙方平手，無人獲得" + prize.getName() + "(" + L1Config._2041 + ")。"));
            }
        } else if (this.playerListC.size() > this.playerListD.size()) {
            for (L1PcInstance pc : this.playerListA) {
                final L1Item prize = ItemTable.get().getTemplate(L1Config._2040);
                sendMessage("恭喜【藍隊】獲得隊伍對決優勝，獲得:" + prize.getName() + "(" + L1Config._2041 + ")");
                pc.sendPackets(new S_SystemMessage("恭喜【藍隊】獲得隊伍對決優勝，獲得:" + prize.getName() + "(" + L1Config._2041 + ")。"));
                pc.getInventory().storeItem(L1Config._2040, L1Config._2041); // 獲得道具
            }
        } else if (this.playerListD.size() > this.playerListC.size()) {
            for (L1PcInstance pc : this.playerListB) {
                final L1Item prize = ItemTable.get().getTemplate(L1Config._2040);
                sendMessage("恭喜【紅隊】獲得隊伍對決優勝，獲得:" + prize.getName() + "(" + L1Config._2041 + ")");
                pc.sendPackets(new S_SystemMessage("恭喜【紅隊】獲得隊伍對決優勝，獲得:" + prize.getName() + "(" + L1Config._2041 + ")。"));
                pc.getInventory().storeItem(L1Config._2040, L1Config._2041); // 獲得道具
            }
        }
    }

    private void endFight() {
        setFightStatus(4);
        sendMessage("隊伍對決結束， 下一場將在" + L1Config._2037 / 60 / 1000 + "分鐘後開始");
        for (L1PcInstance pc : this.playerListA) {
            pc.setATeam(false);
            /*
             * if ((pc.getCurrentHp() == 0) && (pc.isDead())) {//復活
             * pc.broadcastPacket(new S_SkillSound(pc.getId(), 3944));
             * pc.sendPackets(new S_SkillSound(pc.getId(), 3944));
             * pc.setTempID(pc.getId()); pc.sendPackets(new S_Message_YN(322,
             * "")); }
             */
            L1Teleport.teleport(pc, 33442, 32797, (short) 4, pc.getHeading(), true);
        }
        for (L1PcInstance pc : this.playerListB) {
            pc.setBTeam(false);
            /*
             * if ((pc.getCurrentHp() == 0) && (pc.isDead())) {//復活
             * pc.broadcastPacket(new S_SkillSound(pc.getId(), 3944));
             * pc.sendPackets(new S_SkillSound(pc.getId(), 3944));
             * pc.setTempID(pc.getId()); pc.sendPackets(new S_Message_YN(322,
             * "")); }
             */
            L1Teleport.teleport(pc, 33442, 32797, (short) 4, pc.getHeading(), true);
        }
        clearMembers();
        clearColosseum();
    }

    private void clearColosseum() {
        for (Object obj : World.get().getVisibleObjects(4941).values()) {
            if ((obj instanceof L1Inventory)) {
                L1Inventory inventory = (L1Inventory) obj;
                inventory.clearItems();
            }
        }
    }

    private void sendMessage(String msg) {
        for (L1PcInstance pc : this.playerListA) {
            if (pc.getMapId() == 515) {
                pc.sendPackets(new S_BlueMessage(166, "\\f3" + msg));
            }
        }
        for (L1PcInstance pc : this.playerListB) {
            if (pc.getMapId() == 515) {
                pc.sendPackets(new S_BlueMessage(166, "\\f3" + msg));
            }
        }
    }

    public void checkFightGame(L1PcInstance pc) {
        if (pc.getMapId() == 515) {
            removePlayerList(pc);
        }
    }

    private void setDoorClose(boolean isClose) {
        L1DoorInstance[] list = DoorSpawnTable.get().getDoorList();
        for (L1DoorInstance door : list) {
            if (door.getMapId() == 515) {
                if (isClose) {
                    door.close();
                } else {
                    door.open();
                }
            }
        }
    }

    private int getFightStatus() {
        return this._FightStatus;
    }

    private void setFightStatus(int i) {
        this._FightStatus = i;
    }

    private void clearMembers() {
        this.playerListA.clear();
        this.playerListB.clear();
        this.playerListC.clear();
        this.playerListD.clear();
        setDoorClose(true);
    }

    private int getMembersCountA() {
        return this.playerListA.size();
    }

    private int getMembersCountB() {
        return this.playerListB.size();
    }

    private class runFight implements Runnable {
        private runFight() {
        }

        public void run() {
            try {
                Game_Fight.this.setFightStatus(1);
                TimeUnit.MILLISECONDS.sleep(L1Config._2035); // 等待時間
                Game_Fight.this.sendMessage("隊伍對決即將在10秒後開始，目前參加人數 藍隊: " + playerListA.size() + "人 紅隊: " + playerListB.size() + "人");
                TimeUnit.MILLISECONDS.sleep(5000L);
                Game_Fight.this.sendMessage("隊伍對決即將在5秒後開始，目前參加人數 藍隊: " + playerListA.size() + "人 紅隊: " + playerListB.size() + "人");
                TimeUnit.MILLISECONDS.sleep(1000L);
                Game_Fight.this.sendMessage("隊伍對決即將在4秒後開始，目前參加人數 藍隊: " + playerListA.size() + "人 紅隊: " + playerListB.size() + "人");
                TimeUnit.MILLISECONDS.sleep(1000L);
                Game_Fight.this.sendMessage("隊伍對決即將在3秒後開始，目前參加人數 藍隊: " + playerListA.size() + "人 紅隊: " + playerListB.size() + "人");
                TimeUnit.MILLISECONDS.sleep(1000L);
                Game_Fight.this.sendMessage("隊伍對決即將在2秒後開始，目前參加人數 藍隊: " + playerListA.size() + "人 紅隊: " + playerListB.size() + "人");
                TimeUnit.MILLISECONDS.sleep(1000L);
                Game_Fight.this.sendMessage("隊伍對決即將在1秒後開始，目前參加人數 藍隊: " + playerListA.size() + "人 紅隊: " + playerListB.size() + "人");
                TimeUnit.MILLISECONDS.sleep(1000L);
                setDoorClose(false);
                if (Game_Fight.this.checkPlayerCount()) {
                    // Game_Fight.this.Paralysis();
                    Game_Fight.this.setFightStatus(2);
                    Game_Fight.this.sendMessage("隊伍對決開始，請在" + L1Config._2036 / 1000 / 60 + "分鐘內將對手打敗");
                    TimeUnit.MILLISECONDS.sleep(L1Config._2036 / 3);
                    Game_Fight.this.sendMessage("隊伍對決將在" + L1Config._2036 / 3 * 2 / 1000 / 60 + "分鐘後結束");
                    TimeUnit.MILLISECONDS.sleep(L1Config._2036 / 3);
                    Game_Fight.this.sendMessage("隊伍對決將在" + L1Config._2036 / 3 / 1000 / 60 + "分鐘後結束");
                    TimeUnit.MILLISECONDS.sleep(L1Config._2036 / 3);
                    Game_Fight.this.sendMessage("隊伍對決即將結束與公佈優勝隊伍");
                    TimeUnit.MILLISECONDS.sleep(5000);
                    Game_Fight.this.WinTeam();
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    Game_Fight.this.sendMessage("隊伍對決將在10秒後關閉");
                    TimeUnit.MILLISECONDS.sleep(5000L);
                    Game_Fight.this.sendMessage("隊伍對決將在5秒後關閉");
                    TimeUnit.MILLISECONDS.sleep(1000L);
                    Game_Fight.this.sendMessage("隊伍對決將在4秒後關閉");
                    TimeUnit.MILLISECONDS.sleep(1000L);
                    Game_Fight.this.sendMessage("隊伍對決將在3秒後關閉");
                    TimeUnit.MILLISECONDS.sleep(1000L);
                    Game_Fight.this.sendMessage("隊伍對決將在2秒後關閉");
                    TimeUnit.MILLISECONDS.sleep(1000L);
                    Game_Fight.this.sendMessage("隊伍對決將在1秒後關閉");
                    TimeUnit.MILLISECONDS.sleep(1000L);
                    Game_Fight.this.endFight();
                }
                TimeUnit.MILLISECONDS.sleep(L1Config._2037);
                Game_Fight.this.setFightStatus(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                running.set(false);
            }
        }
    }
}
