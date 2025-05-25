package com.lineage.data.event.MiniGame;

import com.lineage.config.ConfigThebes;
import com.lineage.server.ActionCodes;
import com.lineage.server.IdFactory;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Party;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_MiniGameIcon;
import com.lineage.server.serverpackets.S_PackBoxMaptime;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.lineage.server.model.skill.L1SkillId.*;

/**
 * 底比斯大戰遊戲
 */
public class MiniSiege extends Thread {
    private final static ArrayList<L1PcInstance> _teammembers = new ArrayList<>();
    private static final int playtime = ConfigThebes.MiniSiege_PlayTime; // 遊戲時間 (秒)
    private static final int[] BossId = ConfigThebes.MiniSiege_BossId; // 召喚BOSS編號
    private static final int BossStartTime = 20; // 有獲勝陣營,遊戲結束,BOSS召喚等待時間 (秒)
    private static MiniSiege ins;
    private final int minPlayer = ConfigThebes.MiniSiege_MinPlayer; // 最少玩家數
    public boolean running = false;
    public int count = 0;
    L1Party party1 = new L1Party();
    L1Party party2 = new L1Party();
    L1Party party3 = new L1Party();
    Random rnd = new Random();
    private HashMap<Integer, ArrayList<L1PcInstance>> teamcount = new HashMap<>();
    private int stage = 0; // 0 : 準備階段、1：遊戲開始、2：遊戲進行中、3：有獲勝陣營,遊戲結束、4：人數不足結束、5：時間到結束
    private boolean destroyed[] = {false, false, false, false, false, false, false, false, false};
    private String msg[] = {"說明：還在召集參賽者、請稍後！", "說明：按照敵方守護塔、中塔、司令塔順序擊破！", "說明：如果破壞了敵方的塔、會得到額外的獎勵！"};

    public static MiniSiege getInstance() {
        if (ins == null) {
            ins = new MiniSiege();
        }
        return ins;
    }

    /**
     * 底比斯大戰遊戲
     * <p>
     * 召喚塔
     */
    public static void spawn(int npcId, int locX, int locY, int mapid) {
        try {
            L1NpcInstance npc = NpcTable.get().newNpcInstance(npcId);
            npc.setId(IdFactory.get().nextId());
            npc.setMap((short) mapid);
            int tryCount = 0;
            do {
                tryCount++;
                npc.setX(locX);
                npc.setY(locY);
                if (npc.getMap().isInMap(npc.getLocation()) && npc.getMap().isPassable(npc.getLocation(), npc)) {
                    break;
                }
                TimeUnit.MILLISECONDS.sleep(1);
            } while (tryCount < 50);
            if (tryCount >= 50) {
                npc.getLocation().set(locX, locY, mapid);
                npc.getLocation().forward(1);
            }
            npc.setHomeX(npc.getX());
            npc.setHomeY(npc.getY());
            npc.setHeading(1);
            World.get().storeObject(npc);
            World.get().addVisibleObject(npc);
            npc.turnOnOffLight();
            npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE);
        } catch (Exception e) {
        }
    }

    /**
     * 清除全部參加玩家
     */
    public static void clearMembers() {
        _teammembers.clear();
    }

    /**
     * 全部參加玩家
     * <p>
     * 判斷人數用
     */
    public static int getMembersCount() {
        return _teammembers.size();
    }

    public boolean isDestory(int i) {
        return destroyed[i];
    }

    public void Exit() {
        for (int i = 0; i < 9; i++) {
            destroyed[i] = false;
        }
        running = false;
        teamcount.clear();
        clearMembers(); // 清除組成員
        stage = 0;
        count = 0;
        for (Object obj : World.get().getVisibleObjects(10502).values()) {
            if (obj instanceof L1EventTowerInstance) {
                L1EventTowerInstance eventtower = (L1EventTowerInstance) obj;
                if (!eventtower.isDead()) {
                    eventtower.setDead(true);
                    eventtower.setStatus(ActionCodes.ACTION_Die);
                    eventtower.setCurrentHpDirect(0);
                    eventtower.deleteMe();
                }
            }
        }
    }

    public void setDestroy(int i) {
        // 0,1,2,3,4,5,6,7,8 {順序A〜C 1次、2次、司令}
        switch (i) {
            case 0:
                sendMsg("【A隊】的守衛塔被摧毀了");
                destroyed[0] = true;
                break;
            case 1:
                sendMsg("【B隊】的守衛塔被摧毀了");
                destroyed[1] = true;
                break;
            case 2:
                sendMsg("【C隊】的守衛塔被摧毀了");
                destroyed[2] = true;
                break;
            case 3:
                sendMsg("【A隊】的中塔被摧毀了");
                destroyed[3] = true;
                break;
            case 4:
                sendMsg("【B隊】的中塔被摧毀了");
                destroyed[4] = true;
                break;
            case 5:
                sendMsg("【C隊】的中塔被摧毀了");
                destroyed[5] = true;
                break;
            case 6:
                sendMsg("【A隊】的司令塔被摧毀了");
                destroyed[6] = true;
                // System.out.println（「メンバーする見てみよう：「+ teamcount.get（0）.size（）+ "メンボブ：" + teamcount.get（0）.get（0）.getName（））;
                for (int j = 0; j < teamcount.get(0).size(); j++) {
                    L1Teleport.teleport(teamcount.get(0).get(j), 33438, 32810, (short) 4, teamcount.get(0).get(j).getHeading(), true);
                    //if (teamcount.get(0).get(j).isInParty()) { // 刪除組隊
                    //teamcount.get(0).get(j).getParty().leaveMember(teamcount.get(0).get(j));
                    //}
                    RemoveMiniGameIcon(teamcount.get(0).get(j)); // 刪除玩家徽章狀態
                    teamcount.get(0).get(j).setTeam(-1);
                    teamcount.get(0).get(j).isSiege = false;
                }
                //teamcount.remove(0);
                break;
            case 7:
                sendMsg("【B隊】的司令塔被摧毀了");
                destroyed[7] = true;
                for (int j = 0; j < teamcount.get(1).size(); j++) {
                    L1Teleport.teleport(teamcount.get(1).get(j), 33438, 32810, (short) 4, teamcount.get(1).get(j).getHeading(), true);
                    //if (teamcount.get(1).get(j).isInParty()) { // 刪除組隊
                    //teamcount.get(1).get(j).getParty().leaveMember(teamcount.get(1).get(j));
                    //}
                    RemoveMiniGameIcon(teamcount.get(1).get(j)); // 刪除玩家徽章狀態
                    teamcount.get(1).get(j).setTeam(-1);
                    teamcount.get(1).get(j).isSiege = false;
                }
                //teamcount.remove(1);
                break;
            case 8:
                sendMsg("【C隊】的司令塔被摧毀了");
                destroyed[8] = true;
                for (int j = 0; j < teamcount.get(2).size(); j++) {
                    L1Teleport.teleport(teamcount.get(2).get(j), 33438, 32810, (short) 4, teamcount.get(2).get(j).getHeading(), true);
                    //if (teamcount.get(2).get(j).isInParty()) { // 刪除組隊
                    //teamcount.get(2).get(j).getParty().leaveMember(teamcount.get(2).get(j));
                    //}
                    RemoveMiniGameIcon(teamcount.get(2).get(j)); // 刪除玩家徽章狀態
                    teamcount.get(2).get(j).setTeam(-1);
                    teamcount.get(2).get(j).isSiege = false;
                }
                //teamcount.remove(2);
                break;
        }
    }

    public void GiveReward(int type, int team2) {
        System.out.println("給予獎勵：" + type + " TEAM2 : " + team2);
        int itemid = 0;
        int count2 = 0;
        for (int i = 0; i < teamcount.get(team2).size(); i++) {
            switch (type) {
                case 1:
                    itemid = ConfigThebes.A_TowerItemId;
                    count2 = ConfigThebes.A_TowerItemCount;
                    L1ItemInstance item1 = teamcount.get(team2).get(i).getInventory().storeItem(itemid, count2);
                    teamcount.get(team2).get(i).sendPackets(new S_SystemMessage("恭喜摧毀守護塔，獲得【" + item1.getName() + "(" + count2 + ")】。"));
                    break;
                case 2:
                    itemid = ConfigThebes.B_TowerItemId;
                    count2 = ConfigThebes.B_TowerItemCount;
                    L1ItemInstance item2 = teamcount.get(team2).get(i).getInventory().storeItem(itemid, count2);
                    teamcount.get(team2).get(i).sendPackets(new S_SystemMessage("恭喜摧毀中塔，獲得【" + item2.getName() + "(" + count2 + ")】。"));
                    break;
            }
        }
    }

    public void ini() {
        if (!running) {
            for (Object obj : World.get().getVisibleObjects(10502).values()) {
                // 檢查地圖裡是不是上一輪的BOSS還沒清除
                if (obj instanceof L1MonsterInstance) {
                    final L1MonsterInstance mon = (L1MonsterInstance) obj;
                    for (final int bossid : BossId) {
                        final L1Npc boss = NpcTable.get().getTemplate(bossid);
                        if (boss != null) {
                            if (mon.getNpcId() == boss.get_npcId()) {
                                mon.deleteMe();
                            }
                        }
                    }
                }
                // 檢查地圖裡是不是有上輪的玩家在
                if (obj instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) obj;
                    if (pc.getMapId() == 10502) {
                        L1Teleport.teleport(pc, 33438, 32810, (short) 4, pc.getHeading(), true); // 傳回奇岩村
                    }
                }
            }
            for (int i = 0; i < 3; i++) {
                teamcount.put(i, new ArrayList<>());
            }
            //A陣形守護塔
            spawn(4201, 32771, 32871, 10502);
            spawn(4205, 32772, 32839, 10502);
            spawn(4209, 32773, 32798, 10502);
            //B陣形守護塔
            spawn(4202, 32747, 32895, 10502);
            spawn(4206, 32715, 32894, 10502);
            spawn(4210, 32678, 32894, 10502);
            //C陣形守護塔
            spawn(4203, 32771, 32919, 10502);
            spawn(4207, 32772, 32951, 10502);
            spawn(4211, 32771, 32987, 10502);
        }
        running = true;
        GeneralThreadPool.get().execute(new runMiniSiege());
    }

    private void sendMsg(String msg) {
        for (int i = 0; i < teamcount.size(); i++) {
            for (int j = 0; j < teamcount.get(i).size(); j++) {
                teamcount.get(i).get(j).sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, msg));
            }
        }
    }

    public synchronized void EnterTeam(L1PcInstance pc) {
        if (pc.getTeam() == -1) {
            System.out.println("傳入並保存" + count % 3);
            pc.setTeam(count % 3);
            teamcount.get(count % 3).add(pc);
            count++;
        } else {
            System.out.println("已經參加了此次活動");
        }
        System.out.println("COUNT:" + count + teamcount.get(count % 3) + " pc : " + pc.getTeam());
        switch (pc.getTeam()) {
            case 0:
                if (!pc.hasSkillEffect(MiniGameIcon_4) && !pc.hasSkillEffect(MiniGameIcon_5) && !pc.hasSkillEffect(MiniGameIcon_6)) { // 遊戲各類徽章狀態
                    pc.setSkillEffect(MiniGameIcon_4, 60 * 60 * 1000); // 1小時
                }
                L1Teleport.teleport(pc, 32771, 32815, (short) 10502, pc.getHeading(), true);
                //party1.addMember(pc); // 加入組隊
                //pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, true));
                break;
            case 1:
                if (!pc.hasSkillEffect(MiniGameIcon_4) && !pc.hasSkillEffect(MiniGameIcon_5) && !pc.hasSkillEffect(MiniGameIcon_6)) { // 遊戲各類徽章狀態
                    pc.setSkillEffect(MiniGameIcon_5, 60 * 60 * 1000); // 1小時
                }
                L1Teleport.teleport(pc, 32691, 32895, (short) 10502, pc.getHeading(), true);
                //party2.addMember(pc); // 加入組隊
                //pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, true));
                break;
            case 2:
                if (!pc.hasSkillEffect(MiniGameIcon_4) && !pc.hasSkillEffect(MiniGameIcon_5) && !pc.hasSkillEffect(MiniGameIcon_6)) { // 遊戲各類徽章狀態
                    pc.setSkillEffect(MiniGameIcon_6, 60 * 60 * 1000); // 1小時
                }
                L1Teleport.teleport(pc, 32771, 32975, (short) 10502, pc.getHeading(), true);
                //party3.addMember(pc); // 加入組隊
                //pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, true));
                break;
        }
        addMember(pc);
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int i) {
        stage = i;
    }

    public int checkWinTeam() {
        int i = -1;
        for (int j = 6; j < 9; j++) {
            if (!destroyed[j]) {
                i = j;
            }
        }
        i = i % 3;
        return i;
    }

    public int getNexuscount() {
        int c = 0;
        for (int j = 6; j < 9; j++) {
            if (destroyed[j]) {
                c++;
            }
        }
        if (c >= 2) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 全部參加玩家
     * <p>
     * 判斷人數用
     */
    private void addMember(L1PcInstance pc) {
        if (!_teammembers.contains(pc)) {
            _teammembers.add(pc);
        }
    }

    /**
     * 刪除玩家徽章狀態
     */
    private void RemoveMiniGameIcon(L1PcInstance pc) {
        if (pc.hasSkillEffect(L1SkillId.MiniGameIcon_4)) {
            pc.killSkillEffectTimer(L1SkillId.MiniGameIcon_4);
            pc.sendPackets(new S_MiniGameIcon(pc.getId())); // 刪除遊戲各類徽章
            pc.broadcastPacketAll(new S_MiniGameIcon(pc.getId())); // 刪除遊戲各類徽章
        }
        if (pc.hasSkillEffect(L1SkillId.MiniGameIcon_5)) {
            pc.killSkillEffectTimer(L1SkillId.MiniGameIcon_5);
            pc.sendPackets(new S_MiniGameIcon(pc.getId())); // 刪除遊戲各類徽章
            pc.broadcastPacketAll(new S_MiniGameIcon(pc.getId())); // 刪除遊戲各類徽章
        }
        if (pc.hasSkillEffect(L1SkillId.MiniGameIcon_6)) {
            pc.killSkillEffectTimer(L1SkillId.MiniGameIcon_6);
            pc.sendPackets(new S_MiniGameIcon(pc.getId())); // 刪除遊戲各類徽章
            pc.broadcastPacketAll(new S_MiniGameIcon(pc.getId())); // 刪除遊戲各類徽章
        }
    }

    private class runMiniSiege implements Runnable {
        public void run() {
            while (running) {
                try {
                    switch (stage) {
                        case 0: // 準備段階
                            int m = rnd.nextInt(3);
                            for (int i = 0; i < 3; i++) {
                                for (int j = 0; j < teamcount.get(i).size(); j++) {
                                    teamcount.get(i).get(j).sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, msg[m]));
                                }
                            }
                            TimeUnit.MILLISECONDS.sleep(8 * 1000);
                            break;
                        case 1:
                            for (int i = 0; i < 3; i++) {
                                for (int j = 0; j < teamcount.get(i).size(); j++) {
                                    teamcount.get(i).get(j).sendPackets(new S_SystemMessage("\\aE遊戲開始，最後守住自己司令塔不被摧毀的陣營將獲勝！"));
                                    teamcount.get(i).get(j).sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, "遊戲開始,最後守住自己司令塔不被摧毀的陣營將獲勝！"));
                                    //teamcount.get(i).get(j).sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, false));
                                    teamcount.get(i).get(j).sendPackets(new S_PackBoxMaptime(playtime)); // 屏幕顯示遊戲時間
                                }
                            }
                            stage = 2;
                            break;
                        case 2: // 進行中
                            for (int i = 0; i < 3; i++) {
                                if (getNexuscount() == 1) {
                                    stage = 3;
                                }
                            }
                            break;
                        case 3: // 有獲勝陣營,遊戲結束
                            int winteam = checkWinTeam();
                            int itemid = ConfigThebes.C_TowerItemId;
                            int count2 = ConfigThebes.C_TowerItemCount;
                            for (int i = 0; i < teamcount.get(winteam).size(); i++) {
                                L1ItemInstance item = teamcount.get(winteam).get(i).getInventory().storeItem(itemid, count2);
                                teamcount.get(winteam).get(i).sendPackets(new S_SystemMessage("恭喜獲得最終勝利獎勵【" + item.getName() + "(" + count2 + ")】。"));
                                teamcount.get(winteam).get(i).sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, "恭喜獲得勝利，" + BossStartTime + "秒後將召喚BOSS，請做好準備！"));
                                teamcount.get(winteam).get(i).sendPackets(new S_PackBoxMaptime(0));
                                RemoveMiniGameIcon(teamcount.get(winteam).get(i)); // 刪除玩家徽章狀態
                            }
                            TimeUnit.MILLISECONDS.sleep(BossStartTime * 1000);
                            for (int j = 0; j < teamcount.get(winteam).size(); j++) {
                                L1Teleport.teleport(teamcount.get(winteam).get(j), 32761, 32895, (short) 10502, teamcount.get(winteam).get(j).getHeading(), true); // 傳到中央位置殺BOSS
                                //if (teamcount.get(winteam).get(j).isInParty()) { // 刪除組隊
                                //teamcount.get(winteam).get(j).getParty().leaveMember(teamcount.get(winteam).get(j));
                                //}
                                teamcount.get(winteam).get(j).setTeam(-1);
                                teamcount.get(winteam).get(j).isSiege = false;
                            }
                            running = false;
                            L1Location loc = new L1Location(32771, 32895, 10502);
                            for (final int bossid : BossId) {
                                L1SpawnUtil.spawnR(loc, bossid, -1, 1);
                            }
                            break;
                        case 4: // 人數不足結束
                            for (int i = 0; i < 3; i++) {
                                for (int j = 0; j < teamcount.get(i).size(); j++) {
                                    L1Teleport.teleport(teamcount.get(i).get(j), 33438, 32810, (short) 4, teamcount.get(i).get(j).getHeading(), true); // 傳回奇岩村
                                    teamcount.get(i).get(j).sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, "人數不足(" + minPlayer + ")人，強制結束遊戲！"));
                                    //teamcount.get(i).get(j).sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, false));
                                    RemoveMiniGameIcon(teamcount.get(i).get(j)); // 刪除玩家徽章狀態
                                    teamcount.get(i).get(j).setTeam(-1);
                                    teamcount.get(i).get(j).isSiege = false;
                                }
                            }
                            running = false;
                            break;
                        case 5: // 時間到結束
                            for (int i = 0; i < 3; i++) {
                                for (int j = 0; j < teamcount.get(i).size(); j++) {
                                    L1Teleport.teleport(teamcount.get(i).get(j), 33438, 32810, (short) 4, teamcount.get(i).get(j).getHeading(), true); // 傳回奇岩村
                                    teamcount.get(i).get(j).sendPackets(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, "遊戲時間內未有獲勝者，強制結束遊戲！"));
                                    //teamcount.get(i).get(j).sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, false));
                                    RemoveMiniGameIcon(teamcount.get(i).get(j)); // 刪除玩家徽章狀態
                                    teamcount.get(i).get(j).setTeam(-1);
                                    teamcount.get(i).get(j).isSiege = false;
                                }
                            }
                            running = false;
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Exit();
        }
    }
}
