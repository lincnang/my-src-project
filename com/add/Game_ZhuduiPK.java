package com.add;

import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.L1War;
import com.lineage.server.serverpackets.S_BlueMessage;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldWar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Game_ZhuduiPK {
    public static final int STATUS_NONE = 0; // 閒置
    public static final int STATUS_READY = 1; // 等待中
    public static final int STATUS_PLAYING = 2; // 遊戲開始
    public static final int STATUS_CLEANING = 4; // 清潔中
    private static Game_ZhuduiPK _instance;
    private static Logger _log = Logger.getLogger(Game_ZhuduiPK.class.getName());
    private static String enemyClanName = "";
    private static String clanName = "";
    private final ArrayList<L1PcInstance> _members = new ArrayList<L1PcInstance>();
    private int _ZhuduiPKStatus = STATUS_NONE;

    public static Game_ZhuduiPK getInstance() {
        if (_instance == null) {
            _instance = new Game_ZhuduiPK();
        }
        return _instance;
    }

    public String enterZhuduiPK(L1PcInstance pc) {
        if (Game_ZhuduiPK.getInstance().getZhuduiPKStatus() == Game_ZhuduiPK.STATUS_CLEANING) {
            pc.sendPackets(new S_SystemMessage("血盟PK大賽正在籌備中。"));
            return "";
        }
        if (Game_ZhuduiPK.getInstance().getZhuduiPKStatus() == Game_ZhuduiPK.STATUS_PLAYING) {
            pc.sendPackets(new S_ServerMessage(1182)); // 遊戲已經開始了。
            if (!pc.getClan().getClanName().equals(enemyClanName) && !pc.getClan().getClanName().equals(clanName)) {
                pc.sendPackets(new S_SystemMessage("只有交戰雙方成員才可以進入。"));
            } else {// 血盟成員直接進入
                pc.getInventory().consumeItem(L1Config._2054, L1Config._2055);
                L1Teleport.teleport(pc, 32766, 32801, (short) 515, pc.getHeading(), true);
            }
            return "";
        }
        if (getMembersCount() >= 2) {
            pc.sendPackets(new S_SystemMessage("血盟PK大賽已經到達飽和的狀態了。"));
            return "";
        }
        if (pc.getClassId() != 1 && pc.getClassId() != 0) {// 只有王子，公主才可以
            pc.sendPackets(new S_SystemMessage("請讓你們王子或者公主來報名。"));
            return "";
        }
        if (!pc.getInventory().checkItem(L1Config._2054, L1Config._2055) && !isMember(pc)) {
            pc.sendPackets(new S_SystemMessage("入場所需物品不足。")); // 金幣不足
            return "";
        }
        L1Teleport.teleport(pc, 32766, 32801, (short) 515, pc.getHeading(), true);
        addMember(pc);
        return "";
    }

    public void callPartyPlayers(L1PcInstance pc) {
        // L1Party party = pc.getParty();
        L1Clan party = pc.getClan();
        if (party != null) {
            int x = pc.getX();
            int y = pc.getY() + 2;
            short map = pc.getMapId();
            L1PcInstance[] players = party.getOnlineClanMember();
            for (L1PcInstance pc2 : players) {
                try {
                    if (!pc2.getParty().isLeader(pc2)) {
                        L1Teleport.teleport(pc2, x, y, map, 5, true);
                        pc2.sendPackets(new S_SystemMessage("您被傳喚到盟主身邊。"));
                    }
                } catch (Exception e) {
                    _log.log(Level.SEVERE, "", e);
                }
            }
        }
    }

    private void addMember(L1PcInstance pc) {
        if (!_members.contains(pc)) {
            _members.add(pc);
            pc.getInventory().consumeItem(L1Config._2054, L1Config._2055);
            callPartyPlayers(pc);
        }
        if (getMembersCount() == 1 && getZhuduiPKStatus() == STATUS_NONE) {
            enemyClanName = pc.getClanname();
        } else {
            clanName = pc.getClanname();
            GeneralThreadPool.get().execute(new runZhuduiPK());
        }
    }

    private void starWar() {
        boolean inWar = false;
        List<L1War> warList = WorldWar.get().getWarList(); // 全戰開啟
        for (L1War war : warList) {
            inWar = war.checkClanInSameWar(clanName, enemyClanName);
        }
        if (inWar == false) {
            L1War war = new L1War();
            war.handleCommands(3, clanName, enemyClanName); // 打架
        }
    }

    private boolean checkPlayerCount() {
        if (getMembersCount() <= 1) {
            setZhuduiPKStatus(STATUS_CLEANING);
            sendMessage("血盟不足 2隊，所以強制關閉遊戲");
            for (L1PcInstance pc : getMembersArray()) {
                pc.getInventory().storeItem(L1Config._2054, L1Config._2055);
                final L1Item item = ItemTable.get().getTemplate(L1Config._2054);
                pc.sendPackets(new S_SystemMessage("血盟PK大賽退還:" + item.getName() + "(" + L1Config._2055 + ")。"));
            }
            clearMembers();
            return false;
        }
        return true;
    }

    private void endZhuduiPK() {
        setZhuduiPKStatus(STATUS_CLEANING);
        sendMessage("血盟PK大賽已經結束，請下次再來");
        clearMembers();
    }

    private void clearColosseum() {
        for (Object obj : World.get().getVisibleObjects(515).values()) {
            if (obj instanceof L1MonsterInstance) {
                L1MonsterInstance mob = (L1MonsterInstance) obj;
                if (!mob.isDead()) {
                    mob.setDead(true);
                    mob.setStatus(ActionCodes.ACTION_Die);
                    mob.setCurrentHpDirect(0);
                    mob.deleteMe();
                }
            } else if (obj instanceof L1Inventory) {
                L1Inventory inventory = (L1Inventory) obj;
                inventory.clearItems();
            } else if (obj instanceof L1PcInstance) {
                L1PcInstance l1PcInstance = (L1PcInstance) obj;
                L1Teleport.teleport(l1PcInstance, 32850, 32888, (short) 800, l1PcInstance.getHeading(), true);
            }
        }
    }

    private void sendMessage(String msg) {
        for (L1PcInstance pc : getMembersArray()) {
            if (pc.getMapId() == 515) {
                pc.sendPackets(new S_BlueMessage(166, "\\f3" + msg)); // 夜小空
                // 創意廣播
            }
        }
    }

    private int getZhuduiPKStatus() {
        return _ZhuduiPKStatus;
    }

    private void setZhuduiPKStatus(int i) {
        _ZhuduiPKStatus = i;
    }

    private void clearMembers() {
        _members.clear();
        clearColosseum();
    }

    private boolean isMember(L1PcInstance pc) {
        return _members.contains(pc);
    }

    private L1PcInstance[] getMembersArray() {
        return _members.toArray(new L1PcInstance[_members.size()]);
    }

    private int getMembersCount() {
        return _members.size();
    }

    private class runZhuduiPK implements Runnable {
        public void run() {
            try {
                setZhuduiPKStatus(STATUS_READY);
                sendMessage((L1Config._2051 + 10) + "秒後開始遊戲");
                TimeUnit.MILLISECONDS.sleep(L1Config._2051);
                sendMessage("倒數10秒");
                TimeUnit.MILLISECONDS.sleep(5 * 1000);
                sendMessage("倒數5秒");
                TimeUnit.MILLISECONDS.sleep(1000);
                sendMessage("4秒");
                TimeUnit.MILLISECONDS.sleep(1000);
                sendMessage("3秒");
                TimeUnit.MILLISECONDS.sleep(1000);
                sendMessage("2秒");
                TimeUnit.MILLISECONDS.sleep(1000);
                sendMessage("1秒");
                TimeUnit.MILLISECONDS.sleep(1000);
                if (checkPlayerCount()) {
                    setZhuduiPKStatus(STATUS_PLAYING);
                    starWar();
                    while (true) {
                        if (_members.get(0).isDead() || _members.get(0).getMapId() != 515) {// 第一個血盟失敗
                            _members.get(1).getInventory().storeItem(L1Config._2056, L1Config._2057);
                            _members.get(1).getInventory().storeItem(L1Config._2058, L1Config._2059);
                            World.get().broadcastPacketToAll(new S_ServerMessage(166, (new StringBuilder()).append("血盟").append(_members.get(1).getClanname()).append("打贏了血盟").toString(), (new StringBuilder()).append(_members.get(0).getClanname()).toString()));
                            break;
                        } else if (_members.get(1).isDead() || _members.get(1).getMapId() != 515) {
                            _members.get(0).getInventory().storeItem(L1Config._2056, L1Config._2057);
                            _members.get(0).getInventory().storeItem(L1Config._2058, L1Config._2059);
                            World.get().broadcastPacketToAll(new S_ServerMessage(166, (new StringBuilder()).append("血盟").append(_members.get(0).getClanname()).append("打贏了血盟").toString(), (new StringBuilder()).append(_members.get(1).getClanname()).toString()));
                            break;
                        }
                        TimeUnit.MILLISECONDS.sleep(1000);
                    }
                    sendMessage("血盟PK大賽將於10秒後結束！");
                    TimeUnit.MILLISECONDS.sleep(10000);
                    endZhuduiPK();
                }
                TimeUnit.MILLISECONDS.sleep(L1Config._2053);
                setZhuduiPKStatus(STATUS_NONE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}