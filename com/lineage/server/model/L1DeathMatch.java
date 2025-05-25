/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.lineage.server.model;

import com.add.L1Config;
import com.lineage.config.Config;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_DeathMatch;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.utils.Random;
import com.lineage.server.utils.collections.Lists;
import com.lineage.server.world.World;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 專用媒介處理煩雜導入??? 待機時間仕樣組迂。 //5回入場不可能
 */
public class L1DeathMatch {
    public static final int STATUS_NONE = 0;
    public static final int STATUS_READY = 1;
    // ?化屋敷??勝利欠片４種類
    public static final int STATUS_PLAYING = 2;
    public static final int STATUS_END = 3;
    private static final int maxPlayer = L1Config._2012;// 最大競技可能人數
    private static final int END_STATUS_WINNER = 1;
    private static final int END_STATUS_NOWINNER = 2;
    private static final int END_STATUS_NOPLAYER = 3;
    private static L1DeathMatch instance;
    private static int gameStartMinPlayer = L1Config._2011; // 最小開始人數
    private static int orderEntMinPlayer = L1Config._2013; // 入場受付開始人數
    private static int limitTime = 60 * L1Config._2014 * 1000; // 競技時間最大 30分
    private static int limitPenaltyTime = 60 * L1Config._2015 * 1000;// 競技開始10分
    private final int[] fragment = {47024, 47025, 47026, 47027};
    private final int ISSUE_ITEM = 50499;// 記念品箱
    // 競技參加中
    private final List<L1PcInstance> playerList = Lists.newArrayList();
    // 最終利用時間
    private final HashMap<String, Date> lastPlayTime = new HashMap<>();
    /**
     * 1270 最小參加人數5名滿、決鬥強制終了。 1000返。 1273
     * 選Lv%0?Lv%1參加。 1521 一日參加回數%0回超。
     */
    // 參加予約者
    private final List<L1PcInstance> orderList = Lists.newArrayList();
    //
    // 予約
    private final HashMap<String, Integer> orderCancelList = new HashMap<>();
    long startTime;// = System.currentTimeMillis();
    private short mapId;
    private int _status = 0;
    private L1PcInstance _winner = null;
    private GameTimeLimitTimer limitTimer;
    private LimitPenaltyTimer _penalty;
    private IssueItem _issue;
    // 初期化+ 會場準備 處理 end
    private int _startPlayerNum;
    private L1PcInstance _1stwinner = null;
    private L1PcInstance _4thwinner = null;
    private L1PcInstance _2ndwinner = null;
    private L1PcInstance _3rdwinner = null;
    private L1PcInstance _5thwinner;
    private L1PcInstance _6thwinner;
    private int _remainder = 0;

    private L1DeathMatch() {
        setMapId((short) 5153);
    }

    public static L1DeathMatch getInstance() {
        if (instance == null) {
            instance = new L1DeathMatch();
        }
        return instance;
    }

    // 賞品處理 start
    public void addPlayerList(L1PcInstance pc) {
        if (!playerList.contains(pc)) {
            playerList.add(pc);
        }
    }

    public void removePlayerList(L1PcInstance pc) {
        if (playerList.contains(pc)) {
            playerList.remove(pc);
        }
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    //
    public void enterGame(L1PcInstance pc) {
        /*
         * if (!(pc.getLevel() > 29 && pc.getLevel() < 52)) { pc.sendPackets(new
         * S_ServerMessage(1273, "30", "51")); // 選Lv%0～Lv%1參加。
         * return; }
         */
        if (playerList.size() + orderList.size() >= maxPlayer) {
            pc.sendPackets(new S_ServerMessage(1536));// 1536
            // 定員達、入場。
            return;
        }
        if (!pc.getInventory().consumeItem(40308, 1000)) {
            pc.sendPackets(new S_ServerMessage(189)); // 189 \f1不足。
            return;
        }
        if (getOrderCancelList(pc) > 4) {
            if (lastPlayTime.containsKey(pc.getName())) {
                Calendar lasttime = Calendar.getInstance(TimeZone.getTimeZone(Config.TIME_ZONE));
                Calendar now = Calendar.getInstance(TimeZone.getTimeZone(Config.TIME_ZONE));
                lasttime.setTime(lastPlayTime.get(pc.getName()));
                now.setTime(new Date(System.currentTimeMillis()));
                if (lasttime.get(Calendar.MONTH) < now.get(Calendar.MONTH) || lasttime.get(Calendar.YEAR) < now.get(Calendar.YEAR) || lasttime.get(Calendar.DATE) < now.get(Calendar.DATE)) {
                    orderCancelList.remove(pc.getName());
                } else {
                    pc.sendPackets(new S_ServerMessage(1387));// 1387
                    // 今日利用。
                    return;
                }
            }
        }
        if (getGameStatus() == STATUS_END) {
            pc.sendPackets(new S_ServerMessage(1182)); // 始。
            return;
        }
        if (getGameStatus() == STATUS_PLAYING) {
            if ((System.currentTimeMillis() - getStartTime()) > 60 * 3000) {
                pc.sendPackets(new S_ServerMessage(1182)); // 始。
                return;
            }
        }
        if (getGameStatus() == STATUS_NONE) {
            addOrderList(pc);
            return;
        }
        pc.sendPackets(new S_Message_YN(1268, null));
        // 1268 出場？（Y/N）
    }

    // XXX for C_Attr.java
    public void requsetAttr(L1PcInstance pc, int c) {
        if (c == 0) { // NO
            if (orderList.contains(pc)) {
                removeOrderList(pc);
                addOrderCancelList(pc);
                if (getOrderCancelList(pc) == 4) {
                    pc.sendPackets(new S_ServerMessage(1390));// 1390
                    // 入場予約4回、最後1回殘。
                } else {
                    pc.sendPackets(new S_ServerMessage(1267, "" + orderCancelList.get(pc.getName()))); // 1267
                    // 出場予約%0回。
                }
            }
        } else { // YES
            if (getStartTime() == 0) {
                setStartTime(System.currentTimeMillis());
                new CheckTimer().begin();
            }
            // 加入場合解散脫退
            if (pc.isInParty()) {
                pc.getParty().leaveMember(pc);
            }
            if (getGameStatus() == STATUS_PLAYING) {
                L1Location loc = new L1Location(32625 + Random.nextInt(28), 32885 + Random.nextInt(28), getMapId());
                while (!loc.getMap().isPassable(loc.getX(), loc.getY())) {
                    loc.set(32625 + Random.nextInt(28), 32885 + Random.nextInt(28), getMapId());
                }
                L1Teleport.teleport(pc, loc.getX(), loc.getY(), (short) loc.getMapId(), 5, true);
            } else {
                if (Random.nextInt(2) > 0) {
                    L1Teleport.teleport(pc, 32637 + Random.nextInt(3), 32877 + Random.nextInt(6), (short) 5153, 6, true);
                } else {
                    L1Teleport.teleport(pc, 32655 + Random.nextInt(6), 32897 + Random.nextInt(4), (short) 5153, 6, true);
                }
            }
            /*
             * L1ItemInstance item = ItemTable.get() .createItem(49865);//
             * 死亡競技藥水箱 if (pc.getInventory().checkAddItem(item, 1) ==
             * L1Inventory.OK) { item.setCount(1);
             * pc.getInventory().storeItem(item); pc.sendPackets(new
             * S_ServerMessage(403, item.getLogName())); // %0手入。 }
             */
            // removeSkillEffect(pc);
            addPlayerList(pc);
            removeOrderList(pc);
            // 未實裝 待時間仕樣
            Calendar now = Calendar.getInstance(TimeZone.getTimeZone(Config.TIME_ZONE));
            now.setTime(new Date(System.currentTimeMillis()));
            addLastPlayTime(pc, now.getTime());
        }
    }

    private void addLastPlayTime(L1PcInstance pc, Date date) {
        if (!lastPlayTime.containsKey(pc.getName())) {
            lastPlayTime.put(pc.getName(), date);
        }
    }

    /*
     * private void removeLastPlayTime(L1PcInstance pc) {
     * if(lastPlayTime.containsKey(pc.getName())){
     * lastPlayTime.remove(pc.getName()); } }
     */
    public void addOrderCancelList(L1PcInstance pc) {
        if (orderCancelList.containsKey(pc.getName())) {
            int cnt = orderCancelList.get(pc.getName()) + 1;
            orderCancelList.remove(pc.getName());
            orderCancelList.put(pc.getName(), cnt);
        } else {// 
            orderCancelList.put(pc.getName(), 1);
        }
        if (getOrderCancelList(pc) > 4) {// 日付保存
            Calendar now = Calendar.getInstance(TimeZone.getTimeZone(Config.TIME_ZONE));
            now.setTime(new Date(System.currentTimeMillis()));
            addLastPlayTime(pc, now.getTime());
        }
    }

    private void removeOrderList(L1PcInstance pc) {
        orderList.remove(pc);
    }

    private int getOrderCancelList(L1PcInstance pc) {
        int cnt = 0;
        if (orderCancelList.containsKey(pc.getName())) {
            cnt = orderCancelList.get("" + pc.getName());
        }
        return cnt;
    }

    // 予約受付
    private void addOrderList(L1PcInstance pc) {
        if (orderList.contains(pc)) {
            pc.sendPackets(new S_ServerMessage(1266)); // 1266
            // 出場予約受付。
            return;
        }
        orderList.add(pc);
        pc.setInOrderList(true);
        pc.sendPackets(new S_ServerMessage(1265, String.valueOf(orderList.size()))); // 1265
        // %0出場予約。
        if (orderList.size() >= orderEntMinPlayer) {
            for (L1PcInstance player : orderList) {
                player.sendPackets(new S_Message_YN(1268, null)); // 1268
                // 出場？（Y/N）
            }
            setGameStatus(STATUS_READY);
        }
    }

    private boolean checkPlayersOK() {
        if (getGameStatus() == STATUS_READY) {
            return playerList.size() >= gameStartMinPlayer;
        }
        return false;
    }

    // 競技終了後處理 1:勝利者場合 2:時間切場合 3:參加人數不足
    private void setGameEnd(int type) {
        setGameStatus(STATUS_END);
        switch (type) {
            case END_STATUS_WINNER:
                stopGameTimeLimitTimer();
                if (_penalty != null) {
                    _penalty.cancel();
                }
                if (_issue != null) {
                    _issue.cancel();
                }
                sendEndMessage();
                break;
            case END_STATUS_NOWINNER:
                if (_penalty != null) {
                    _penalty.cancel();
                }
                sendEndMessage();
                break;
            case END_STATUS_NOPLAYER:
                for (L1PcInstance pc : playerList) {
                    // pc.sendPackets(new S_ServerMessage(1264)); 本鯖用
                    // 最小參加人數2名滿、強制終了。 1000返。
                    pc.sendPackets(new S_SystemMessage("人數不達最小參戰人數，遊戲強制結束，退還入場費1000金幣。"));
                    pc.getInventory().storeItem(40308, 1000);
                }
                break;
        }
        for (L1PcInstance pc : playerList) {
            /*
             * L1ItemInstance
             * IssuedItem=pc.getInventory().findItemId(ISSUE_ITEM);
             * if(IssuedItem!=null){ pc.getInventory().deleteItem(IssuedItem); }
             */
            pc.startHpRegeneration();
            pc.startMpRegeneration();
        }
        startEndTimer();// 5秒後村移動
    }

    private void giftWinner() {
        L1PcInstance winner1 = get1stWinner();
        L1PcInstance winner2 = get2ndWinner();
        L1PcInstance winner3 = get3rdWinner();
        L1PcInstance winner4 = get4thWinner();
        L1PcInstance winner5 = get5thWinner();
        L1PcInstance winner6 = get6thWinner();
        int BraveCount[] = new int[6];
        /**
         * 參加人數 1～5人 : 1位(1)支給 參加人數 6～8人 : 1位(2)、2位(1)支給 參加人數 9～11人 :
         * 1位(3)、2位(2)、3位(1)支給 參加人數 12～14人 : 1位(4)、2位(3)、3位(2)、4位(1)支給 參加人數
         * 15～17人 : 1位(5)、2位(4)、3位(3)、4位(2)、5位(1)支給 參加人數 18～20人 :
         * 1位(6)、2位(5)、3位(4)、4位(3)、5位(2)、6位(1)支給 ※參加人數報酬"順位(勇者證)"
         */
        if (_startPlayerNum >= 1 && 5 >= _startPlayerNum) {
            BraveCount[0] = 1;
        } else if (_startPlayerNum >= 6 && 8 >= _startPlayerNum) {
            BraveCount[0] = 2;
            BraveCount[1] = 1;
        } else if (_startPlayerNum >= 9 && 11 >= _startPlayerNum) {
            BraveCount[0] = 3;
            BraveCount[1] = 2;
            BraveCount[2] = 1;
        } else if (_startPlayerNum >= 12 && 14 >= _startPlayerNum) {
            BraveCount[0] = 4;
            BraveCount[1] = 3;
            BraveCount[2] = 2;
            BraveCount[3] = 1;
        } else if (_startPlayerNum >= 15 && 17 >= _startPlayerNum) {
            BraveCount[0] = 5;
            BraveCount[1] = 4;
            BraveCount[2] = 3;
            BraveCount[3] = 2;
            BraveCount[4] = 1;
        } else if (_startPlayerNum >= 18 && 20 >= _startPlayerNum) {
            BraveCount[0] = 6;
            BraveCount[1] = 5;
            BraveCount[2] = 4;
            BraveCount[3] = 3;
            BraveCount[4] = 2;
            BraveCount[5] = 1;
        }
        L1ItemInstance item1 = ItemTable.get().createItem(41402);// 勇者證
        if (winner1 == null) {
            return;
        }
        if (winner1.getInventory().checkAddItem(item1, BraveCount[0]) == L1Inventory.OK) {
            item1.setCount(BraveCount[0]);
            winner1.getInventory().storeItem(41402, BraveCount[0]);
            winner1.sendPackets(new S_ServerMessage(403, item1.getLogName()));
            // %0手入。
        }
        if (BraveCount[1] != 0) {
            if (winner2.getInventory().checkAddItem(item1, BraveCount[1]) == L1Inventory.OK) {
                item1.setCount(BraveCount[1]);
                winner2.getInventory().storeItem(41402, BraveCount[1]);
                winner2.sendPackets(new S_ServerMessage(403, item1.getLogName()));
                // %0手入。
            }
        }
        if (BraveCount[2] != 0) {
            if (winner3.getInventory().checkAddItem(item1, BraveCount[2]) == L1Inventory.OK) {
                item1.setCount(BraveCount[2]);
                winner3.getInventory().storeItem(41402, BraveCount[2]);
                winner3.sendPackets(new S_ServerMessage(403, item1.getLogName()));
                // %0手入。
            }
        }
        if (BraveCount[3] != 0) {
            if (winner4.getInventory().checkAddItem(item1, BraveCount[3]) == L1Inventory.OK) {
                item1.setCount(BraveCount[3]);
                winner4.getInventory().storeItem(41402, BraveCount[3]);
                winner4.sendPackets(new S_ServerMessage(403, item1.getLogName()));
                // %0手入。
            }
        }
        if (BraveCount[4] != 0) {
            if (winner5.getInventory().checkAddItem(item1, BraveCount[4]) == L1Inventory.OK) {
                item1.setCount(BraveCount[4]);
                winner5.getInventory().storeItem(41402, BraveCount[4]);
                winner5.sendPackets(new S_ServerMessage(403, item1.getLogName()));
                // %0手入。
            }
        }
        if (BraveCount[5] != 0) {
            if (winner6.getInventory().checkAddItem(item1, BraveCount[5]) == L1Inventory.OK) {
                item1.setCount(BraveCount[5]);
                winner6.getInventory().storeItem(41402, BraveCount[5]);
                winner6.sendPackets(new S_ServerMessage(403, item1.getLogName()));
                // %0手入。
            }
        }
        // 勝利欠片 處理 4位渡
        fragment(winner1, winner2, winner3, winner4);
    }

    /**
     * // 勝利欠片入手判定
     *
     */
    private void fragment(L1PcInstance winner1, L1PcInstance winner2, L1PcInstance winner3, L1PcInstance winner4) {
        L1ItemInstance item = ItemTable.get().createItem(47024);// 勝利欠片
        // 1位支給
        if (winner1.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
            item.setCount(1);
            winner1.getInventory().storeItem(item);
            winner1.sendPackets(new S_ServerMessage(403, item.getLogName()));
            // %0 手入。
        }
        if ((Random.nextInt(100) + 1) <= 40) { // 40%確率各種勝利欠片入手
            int i = Random.nextInt(fragment.length);
            item = ItemTable.get().createItem(fragment[i]);// 他勝利欠片１
            if (winner1.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
                item.setCount(1);
                winner1.getInventory().storeItem(item);
                winner1.sendPackets(new S_ServerMessage(403, item.getLogName())); // %0手入。
            }
        }
        /**********/
        if (winner2 != null) {
            if ((Random.nextInt(100) + 1) <= 30 * 2) {
                item = ItemTable.get().createItem(47024);// 勝利欠片
                if (winner2.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
                    winner2.getInventory().storeItem(47024, 1);
                    winner2.sendPackets(new S_ServerMessage(403, item.getLogName())); // %0手入。
                }
            }
            if ((Random.nextInt(100) + 1) <= 30) { // 30%確率他勝利欠片入手
                int i = Random.nextInt(fragment.length);
                item = ItemTable.get().createItem(fragment[i]);// 他勝利欠片１
                if (winner2.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
                    item.setCount(1);
                    winner2.getInventory().storeItem(item);
                    winner2.sendPackets(new S_ServerMessage(403, item.getLogName())); // %0手入。
                }
            }
        }
        /**********/
        if (winner3 != null) {
            if ((Random.nextInt(100) + 1) <= 20 * 2) {
                item = ItemTable.get().createItem(47024);// 勝利欠片
                if (winner3.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
                    winner3.getInventory().storeItem(47024, 1);
                    winner3.sendPackets(new S_ServerMessage(403, item.getLogName())); // %0手入。
                }
            }
            if ((Random.nextInt(100) + 1) <= 20) { // 20%確率他勝利欠片入手
                int i = Random.nextInt(fragment.length);
                item = ItemTable.get().createItem(fragment[i]);// 他勝利欠片１
                if (winner3.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
                    item.setCount(1);
                    winner3.getInventory().storeItem(item);
                    winner3.sendPackets(new S_ServerMessage(403, item.getLogName())); // %0手入。
                }
            }
        }
        /**********/
        if (winner4 != null) {
            if ((Random.nextInt(10) + 1) <= 10 * 2) {
                item = ItemTable.get().createItem(47024);// 勝利欠片
                if (winner4.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
                    winner4.getInventory().storeItem(47024, 1);
                    winner4.sendPackets(new S_ServerMessage(403, item.getLogName())); // %0手入。
                }
            }
            if ((Random.nextInt(100) + 1) <= 10) { // 10%確率他勝利欠片入手
                int i = Random.nextInt(fragment.length);
                item = ItemTable.get().createItem(fragment[i]);// 他勝利欠片１
                if (winner4.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
                    item.setCount(1);
                    winner4.getInventory().storeItem(item);
                    winner4.sendPackets(new S_ServerMessage(403, item.getLogName())); // %0手入。
                }
            }
        }
    }

    private void sendEndMessage() {
        L1PcInstance winner = get1stWinner();
        for (L1PcInstance pc : playerList) {
            pc.sendPackets(new S_DeathMatch(S_DeathMatch.CountDownOff));
            if (winner != null) {
                pc.sendPackets(new S_ServerMessage(1272, winner.getName()));// 1272
                // %0%s
                // 勝利收。
                /*
                 * if(winner.getId()==pc.getId()){ pc.sendPackets(new
                 * S_DeathMatch(S_DeathMatch.Winner)); }else{ pc.sendPackets(new
                 * S_DeathMatch(S_DeathMatch.GameOver)); }
                 */
            } else {
                pc.sendPackets(new S_ServerMessage(1275));// 1275
                // 終了時間。優勝者。終了。
                // pc.sendPackets(new S_DeathMatch(S_DeathMatch.GameOver));
            }
            pc.sendPackets(new S_ServerMessage(1259)); // 村移動。
        }
    }

    // 初期化
    private void setGameInit() {
        for (L1PcInstance pc : playerList) {
            // 死亡競技藥水箱為競技時必要的消耗性項目，於競賽結束時全部刪除
            /*
             * for (L1ItemInstance item : pc.getInventory().getItems()) { if
             * (item.getItemId() >= 49863 && item.getItemId() <= 49865) {
             * pc.getInventory().removeItem(item); } }
             */
            if (pc.isGhost()) {
                pc.setReserveGhost(true);
                L1Teleport.teleport(pc, 32616, 32782, (short) 4, 5, true);
            } else {
                L1Teleport.teleport(pc, 32616, 32782, (short) 4, 5, true);
            }
        }
        setStartTime(0);
        setGameStatus(STATUS_NONE);
        setWinner(null);
        set1stWinner(null);
        set2ndWinner(null);
        set3rdWinner(null);
        set4thWinner(null);
        set5thWinner(null);
        set6thWinner(null);
        _startPlayerNum = 0;
        _penalty = null;
        _issue = null;
        playerList.clear();
        for (L1PcInstance pc : orderList) {// 出場
            addOrderCancelList(pc);
        }
        orderList.clear();
    }

    // XXX for ClientThread.java
    public void checkLeaveGame(L1PcInstance pc) {
        if (pc.getMapId() == this.getMapId()) {
            // 死亡競技藥水箱為競技時必要的消耗性項目，於競賽結束時全部刪除
            /*
             * for (L1ItemInstance item : pc.getInventory().getItems()) { if
             * (item.getItemId() >= 49863 && item.getItemId() <= 49865) {
             * pc.getInventory().removeItem(item); } }
             */
            removePlayerList(pc);
            // addOrderCancelList(pc);//切斷場合同扱
            if (getGameStatus() == STATUS_PLAYING) {
                sendRemainder(null);
            }
            // L1PolyMorph.undoPoly(pc);
            L1Teleport.teleport(pc, 32624, 32813, (short) 4, 5, false);
        }
        if (pc.isInOrderList()) {
            removeOrderList(pc);
        }
    }

    public void removeSkillEffect(L1PcInstance pc) {
        L1SkillUse skill = new L1SkillUse();
        skill.handleCommands(pc, L1SkillId.CANCELLATION, pc.getId(), pc.getX(), pc.getY(), 0, L1SkillUse.TYPE_LOGIN);
    }

    public int getGameStatus() {
        return _status;
    }

    public void setGameStatus(int i) {
        _status = i;
    }

    public L1PcInstance getWinner() {
        return _winner;
    }

    public void setWinner(L1PcInstance pc) {
        _winner = pc;
    }

    private void startGameTimeLimitTimer() {
        Timer timer = new Timer();
        limitTimer = new GameTimeLimitTimer();
        timer.schedule(limitTimer, limitTime);
    }

    private void stopGameTimeLimitTimer() {
        if (limitTimer != null) {
            limitTimer.stopTimer();
        }
    }

    private void startEndTimer() {
        new EndTimer().begin();
    }

    public L1PcInstance get1stWinner() {
        return _1stwinner;
    }

    public void set1stWinner(L1PcInstance pc) {
        _1stwinner = pc;
    }

    public L1PcInstance get2ndWinner() {
        return _2ndwinner;
    }

    public void set2ndWinner(L1PcInstance pc) {
        _2ndwinner = pc;
    }

    public L1PcInstance get3rdWinner() {
        return _3rdwinner;
    }

    public void set3rdWinner(L1PcInstance pc) {
        _3rdwinner = pc;
    }

    public short getMapId() {
        return mapId;
    }

    public void setMapId(short mapId) {
        this.mapId = mapId;
    }

    public L1PcInstance get4thWinner() {
        return _4thwinner;
    }

    public void set4thWinner(L1PcInstance _4thwinner) {
        this._4thwinner = _4thwinner;
    }

    public L1PcInstance get5thWinner() {
        return _5thwinner;
    }

    public void set5thWinner(L1PcInstance _5thwinner) {
        this._5thwinner = _5thwinner;
    }

    public L1PcInstance get6thWinner() {
        return _6thwinner;
    }

    public void set6thWinner(L1PcInstance _6thwinner) {
        this._6thwinner = _6thwinner;
    }

    public void sendRemainder(L1PcInstance deathPlayer) {
        int players = getRemainder() - 1;
        L1PcInstance winner = null;
        setRemainder(players);
        if (players <= 1) {
            for (L1PcInstance pc : playerList) {
                if (!pc.isGhost() && !pc.isDead() && pc.getMapId() == 5153) {
                    winner = pc;
                    break;
                }
            }
            setWinner(winner);
            set1stWinner(winner);
            set2ndWinner(deathPlayer);
            setGameEnd(END_STATUS_WINNER);
        } else if (players == 2) {
            set3rdWinner(deathPlayer);
            for (L1PcInstance pc : playerList) {
                if (pc.getMapId() == getMapId()) {
                    pc.sendPackets(new S_ServerMessage(1274, "" + players));// 1274
                    // 生存者殘%0名。
                }
            }
        } else if (players == 3) {
            set4thWinner(deathPlayer);
            for (L1PcInstance pc : playerList) {
                if (pc.getMapId() == getMapId()) {
                    pc.sendPackets(new S_ServerMessage(1274, "" + players));// 1274
                    // 生存者殘%0名。
                }
            }
        } else if (players == 4) {
            set5thWinner(deathPlayer);
            for (L1PcInstance pc : playerList) {
                if (pc.getMapId() == getMapId()) {
                    pc.sendPackets(new S_ServerMessage(1274, "" + players));// 1274
                    // 生存者殘%0名。
                }
            }
        } else if (players == 5) {
            set6thWinner(deathPlayer);
            for (L1PcInstance pc : playerList) {
                if (pc.getMapId() == getMapId()) {
                    pc.sendPackets(new S_ServerMessage(1274, "" + players));// 1274
                    // 生存者殘%0名。
                }
            }
        } else {
            for (L1PcInstance pc : playerList) {
                if (pc.getMapId() == getMapId()) {
                    pc.sendPackets(new S_ServerMessage(1274, "" + players));// 1274
                    // 生存者殘%0名。
                }
            }
        }
    }

    public int getRemainder() {
        return _remainder;
    }

    public void setRemainder(int remainder) {
        this._remainder = remainder;
    }

    private class CheckTimer extends TimerTask {
        @Override
        public void run() {
            for (L1PcInstance pc : playerList) {
                pc.sendPackets(new S_ServerMessage(1269)); // 1269
                // 。
            }
            try {
                TimeUnit.MILLISECONDS.sleep(6 * 1000);
                CloseTimer temp = new CloseTimer();
                temp.begin();
                TimeUnit.MILLISECONDS.sleep(4 * 1000);// 50+10=60s
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (checkPlayersOK()) {
                setGameStatus(STATUS_PLAYING);
                for (L1PcInstance pc : playerList) {
                    pc.sendPackets(new S_DeathMatch(S_DeathMatch.CountDown));
                }
                startGameTimeLimitTimer();
                _issue = new IssueItem();
                _issue.begin();
                _penalty = new LimitPenaltyTimer();
                _penalty.begin();
                _startPlayerNum = playerList.size();
                setRemainder(_startPlayerNum);
                for (L1Object obj : World.get().getVisiblePoint(new L1Location(32638, 32880, getMapId()), 5)) {
                    if (obj instanceof L1PcInstance) {
                        L1Location loc = new L1Location(32625 + Random.nextInt(28), 32885 + Random.nextInt(28), getMapId());
                        while (!loc.getMap().isPassable(loc.getX(), loc.getY())) {
                            loc.set(32625 + Random.nextInt(28), 32885 + Random.nextInt(28), getMapId());
                        }
                        L1Teleport.teleport((L1PcInstance) obj, loc.getX(), loc.getY(), (short) loc.getMapId(), 5, false);
                    }
                }
                for (L1Object obj : World.get().getVisiblePoint(new L1Location(32658, 32899, getMapId()), 5)) {
                    if (obj instanceof L1PcInstance) {
                        L1Location loc = new L1Location(32625 + Random.nextInt(28), 32885 + Random.nextInt(28), getMapId());
                        while (!loc.getMap().isPassable(loc.getX(), loc.getY())) {
                            loc.set(32625 + Random.nextInt(28), 32885 + Random.nextInt(28), getMapId());
                        }
                        L1Teleport.teleport((L1PcInstance) obj, loc.getX(), loc.getY(), (short) loc.getMapId(), 5, false);
                    }
                }
            } else {
                setGameEnd(END_STATUS_NOPLAYER);
            }
            this.cancel();
        }

        public void begin() {
            Timer timer = new Timer();
            timer.schedule(this, 50 * 1000); // 50s
        }
    }

    private class GameTimeLimitTimer extends TimerTask {
        @Override
        public void run() {
            setGameEnd(END_STATUS_NOWINNER);
            this.cancel();
        }

        public void stopTimer() {
            this.cancel();
        }
    }

    // 死亡不明、１減
    // hp/mp減少?週期值 不明
    private class LimitPenaltyTimer extends TimerTask {
        @Override
        public void run() {
            if (getGameStatus() == STATUS_PLAYING) {
                for (L1PcInstance pc : playerList) {
                    if (!pc.isDead() && pc.getMapId() == getMapId()) {
                        if (Random.nextInt(2) > 0) {
                            int newHp = pc.getCurrentHp() - 10;
                            if (newHp < 1) {
                                newHp = 1;
                            }
                            pc.setCurrentHp(newHp);
                        } else {
                            int newMp = pc.getCurrentMp() - 20;
                            if (newMp < 1) {
                                newMp = 1;
                            }
                            pc.setCurrentMp(newMp);
                        }
                    }
                }
            } else {
                this.cancel();
            }
        }

        public void begin() {
            Timer timer = new Timer();
            timer.schedule(this, limitPenaltyTime, 3 * 1000); // 3秒週期
        }
    }

    // 支給品配布
    private class IssueItem extends TimerTask {
        @Override
        public void run() {
            if (getGameStatus() == STATUS_PLAYING) {
                for (L1PcInstance pc : playerList) {
                    if (pc.getMapId() == getMapId()) {// 生死關支給
                        // XXX
                        L1ItemInstance item = ItemTable.get().createItem(ISSUE_ITEM);//
                        if (pc.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
                            item.setCount(1);
                            pc.getInventory().storeItem(item);
                            pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // %0手入。
                        }
                    }
                }
            } else {
                this.cancel();
            }
        }

        public void begin() {
            Timer timer = new Timer();
            timer.schedule(this, 60 * 3 * 1000); // 3分鐘
        }
    }

    private class EndTimer extends TimerTask {
        @Override
        public void run() {
            giftWinner();
            setGameInit();
            this.cancel();
        }

        public void begin() {
            Timer timer = new Timer();
            timer.schedule(this, 10000);
        }
    }

    public class CloseTimer extends TimerTask {
        public CloseTimer() {
        }

        @Override
        public void run() {
            for (L1Object obj : World.get().getVisiblePoint(new L1Location(32638, 32880, getMapId()), 5)) {
                if (obj instanceof L1PcInstance) {
                    L1Location loc = new L1Location(32625 + Random.nextInt(28), 32885 + Random.nextInt(28), getMapId());
                    while (!loc.getMap().isPassable(loc.getX(), loc.getY())) {
                        loc.set(32625 + Random.nextInt(28), 32885 + Random.nextInt(28), getMapId());
                    }
                    L1Teleport.teleport((L1PcInstance) obj, loc.getX(), loc.getY(), (short) loc.getMapId(), 5, false);
                }
            }
            for (L1Object obj : World.get().getVisiblePoint(new L1Location(32658, 32899, getMapId()), 5)) {
                if (obj instanceof L1PcInstance) {
                    L1Location loc = new L1Location(32625 + Random.nextInt(28), 32885 + Random.nextInt(28), getMapId());
                    while (!loc.getMap().isPassable(loc.getX(), loc.getY())) {
                        loc.set(32625 + Random.nextInt(28), 32885 + Random.nextInt(28), getMapId());
                    }
                    L1Teleport.teleport((L1PcInstance) obj, loc.getX(), loc.getY(), (short) loc.getMapId(), 5, false);
                }
            }
        }

        public void begin() {
            Timer timer = new Timer();
            timer.schedule(this, 4 * 1000);
        }
    }
}
