package com.lineage.server.model;

import com.lineage.server.datatables.DoorSpawnTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.*;
import javolution.util.FastTable;

import java.util.Random;
import java.util.TimerTask;

public class L1PolyRace {
    public static final int STATUS_NONE = 0;
    public static final int STATUS_READY = 1;
    public static final int STATUS_PLAYING = 2;
    public static final int STATUS_END = 3;
    private static L1PolyRace instance;
    private static int readyTime = 60000;
    private static int limitTime = 240000;
    private static int POLY_EFFECT = 15566;
    private static int SPEED_EFFECT = 18333;
    private int[] polyList = {936, 3134, 1642, 931, 96, 4038, 938, 929, 1540, 3783, 2145, 934, 3918, 3199, 3184, 3132, 3107, 3188, 3211, 3143, 3182, 3156, 3154, 3178, 4133, 5089, 945, 4171, 2541, 2001, 1649, 29};
    private FastTable<L1PcInstance> playerList = new FastTable<>();
    private FastTable<L1PcInstance> orderList = new FastTable<>();
    private FastTable<L1PcInstance> position = new FastTable<>();
    private int _status = 0;
    private int _time = 0;
    private L1PcInstance _winner = null;
    private CompareTimer compareTimer;
    private GameTimeLimitTimer limitTimer;

    public static L1PolyRace getInstance() {
        if (instance == null) {
            instance = new L1PolyRace();
        }
        return instance;
    }

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

    public void enterGame(L1PcInstance pc) {
        if (pc.getLevel() < 30) {
            pc.sendPackets(new S_ServerMessage(1273, "30", "99"));
            return;
        }
        if (!pc.getInventory().consumeItem(40308, 1000L)) {
            pc.sendPackets(new S_ServerMessage(189));
            return;
        }
        if (playerList.size() + orderList.size() >= 10) {
            pc.sendPackets(new S_SystemMessage("遊戲人數已達上限"));
            return;
        }
        if ((getGameStatus() == 2) || (getGameStatus() == 3)) {
            pc.sendPackets(new S_ServerMessage(1182));
            return;
        }
        if (getGameStatus() == 0) {
            addOrderList(pc);
            return;
        }
        addPlayerList(pc);
        L1Teleport.teleport(pc, 32768, 32849, (short) 5143, 6, true);
    }

    public void removeOrderList(L1PcInstance pc) {
        orderList.remove(pc);
    }

    public void addOrderList(L1PcInstance pc) {
        if (orderList.contains(pc)) {
            pc.sendPackets(new S_ServerMessage(1254));
            return;
        }
        orderList.add(pc);
        pc.setInOrderList(true);
        pc.sendPackets(new S_ServerMessage(1253, String.valueOf(orderList.size())));
        if (orderList.size() >= 2) {
            for (L1PcInstance player : orderList) {
                player.sendPackets(new S_Message_YN(1256, null));
            }
            setGameStatus(1);
            startReadyTimer();
        }
    }

    private boolean checkPlayersOK() {
        if (getGameStatus() == 1) {
            return playerList.size() >= 2;
        }
        return false;
    }

    private void setGameStart() {
        setGameStatus(2);
        for (L1PcInstance pc : playerList) {
            speedUp(pc, 0, 0);
            randomPoly(pc, 0, 0);
            pc.sendPackets(new S_ServerMessage(1257));
            pc.sendPackets(new S_Race(64));
            pc.sendPackets(new S_Race(4, pc.getLap()));
            pc.sendPackets(new S_Race(playerList, pc));
        }
        startCompareTimer();
        startClockTimer();
    }

    private void setGameWinner(L1PcInstance pc) {
        if (getWinner() == null) {
            setWinner(pc);
            setGameEnd(1);
        }
    }

    private void setGameEnd(int type) {
        setGameStatus(3);
        switch (type) {
            case 1:
                stopCompareTimer();
                stopGameTimeLimitTimer();
                sendEndMessage();
                break;
            case 2:
                stopCompareTimer();
                sendEndMessage();
                break;
            case 3:
                for (L1PcInstance pc : playerList) {
                    pc.sendPackets(new S_ServerMessage(1264));
                    pc.getInventory().storeItem(40308, 1000L);
                }
        }
        startEndTimer();
    }

    private void giftWinner() {
        L1PcInstance winner = getWinner();
        L1ItemInstance item = ItemTable.get().createItem(41308);
        if ((winner == null) || (item == null)) {
            return;
        }
        if (winner.getInventory().checkAddItem(item, 1L) == 0) {
            item.setCount(1L);
            winner.getInventory().storeItem(item);
            winner.sendPackets(new S_ServerMessage(403, item.getLogName()));
        }
    }

    private void sendEndMessage() {
        L1PcInstance winner = getWinner();
        for (L1PcInstance pc : playerList) {
            if (winner != null) {
                pc.sendPackets(new S_ServerMessage(1259));
                pc.sendPackets(new S_Race(winner.getName(), _time * 2));
            } else {
                pc.sendPackets(new S_Race(69));
            }
        }
    }

    private void setGameInit() {
        for (L1PcInstance pc : playerList) {
            pc.sendPackets(new S_Race(70));
            pc.setLap(1);
            pc.setLapCheck(0);
            L1Teleport.teleport(pc, 32616, 32782, (short) 4, 5, true);
            removeSkillEffect(pc);
        }
        setDoorClose(true);
        setGameStatus(0);
        setWinner(null);
        playerList.clear();
        clearTime();
    }

    public void checkLeaveGame(L1PcInstance pc) {
        if (pc.getMapId() == 5143) {
            removePlayerList(pc);
            L1PolyMorph.undoPoly(pc);
        }
        if (pc.isInOrderList()) {
            removeOrderList(pc);
        }
    }

    public void requsetAttr(L1PcInstance pc, int c) {
        if (c == 0) {
            removeOrderList(pc);
            pc.setInOrderList(false);
            pc.sendPackets(new S_ServerMessage(1255));
        } else {
            addPlayerList(pc);
            L1Teleport.teleport(pc, 32768, 32849, (short) 5143, 6, true);
            removeSkillEffect(pc);
            removeOrderList(pc);
            pc.setInOrderList(false);
        }
    }

    private void comparePosition() {
        FastTable<L1PcInstance> temp = new FastTable<>();
        int size = playerList.size();
        int count = 0;
        while (size > count) {
            int maxLapScore = 0;
            for (L1PcInstance pc : playerList) {
                if (temp.contains(pc)) {
                    continue;
                }
                if (pc.getLapScore() >= maxLapScore) {
                    maxLapScore = pc.getLapScore();
                }
            }
            for (L1PcInstance player : playerList) {
                if (player.getLapScore() == maxLapScore) {
                    temp.add(player);
                }
            }
            count++;
        }
        if (!position.equals(temp)) {
            position.clear();
            position.addAll(temp);
            for (L1PcInstance pc : playerList) {
                pc.sendPackets(new S_Race(position, pc));// info
            }
        }
    }

    private void setDoorClose(boolean isClose) {
        L1DoorInstance[] list = DoorSpawnTable.get().getDoorList();
        for (L1DoorInstance door : list) {
            if (door.getMapId() == 5143) {
                if (isClose) {
                    door.close();
                } else {
                    door.open();
                }
            }
        }
    }

    public void removeSkillEffect(L1PcInstance pc) {
        L1SkillUse skill = new L1SkillUse();
        skill.handleCommands(pc, 44, pc.getId(), pc.getX(), pc.getY(), 0, 1);
    }

    private void onEffectTrap(L1PcInstance pc) {
        int x = pc.getX();
        int y = pc.getY();
        if ((x == 32748) && ((y == 32845) || (y == 32846))) {
            speedUp(pc, 32748, 32845);
        } else if ((x == 32748) && ((y == 32847) || (y == 32848))) {
            speedUp(pc, 32748, 32847);
        } else if ((x == 32748) && ((y == 32849) || (y == 32850))) {
            speedUp(pc, 32748, 32849);
        } else if ((x == 32748) && (y == 32851)) {
            speedUp(pc, 32748, 32851);
        } else if ((x == 32762) && ((y == 32811) || (y == 32812))) {
            speedUp(pc, 32762, 32811);
        } else if (((x == 32799) || (x == 32800)) && (y == 32830)) {
            speedUp(pc, 32800, 32830);
        } else if (((x == 32736) || (x == 32737)) && (y == 32840)) {
            randomPoly(pc, 32737, 32840);
        } else if (((x == 32738) || (x == 32739)) && (y == 32840)) {
            randomPoly(pc, 32739, 32840);
        } else if (((x == 32740) || (x == 32741)) && (y == 32840)) {
            randomPoly(pc, 32741, 32840);
        } else if ((x == 32749) && ((y == 32818) || (y == 32817))) {
            randomPoly(pc, 32749, 32817);
        } else if ((x == 32749) && ((y == 32816) || (y == 32815))) {
            randomPoly(pc, 32749, 32815);
        } else if ((x == 32749) && ((y == 32814) || (y == 32813))) {
            randomPoly(pc, 32749, 32813);
        } else if ((x == 32749) && ((y == 32812) || (y == 32811))) {
            randomPoly(pc, 32749, 32811);
        } else if ((x == 32790) && ((y == 32812) || (y == 32813))) {
            randomPoly(pc, 32790, 32812);
        } else if (((x == 32793) || (x == 32794)) && (y == 32831)) {
            randomPoly(pc, 32794, 32831);
        }
    }

    private void randomPoly(L1PcInstance pc, int x, int y) {
        if (pc.hasSkillEffect(POLY_EFFECT)) {
            return;
        }
        pc.setSkillEffect(POLY_EFFECT, 4000);
        Random random = new Random();
        int i = random.nextInt(polyList.length);
        L1PolyMorph.doPoly(pc, polyList[i], 3600, 4);
        for (L1PcInstance player : playerList) {
            player.sendPackets(new S_EffectLocation(x, y, 6675));
        }
    }

    // TODO 加速處理（加速 速度２倍） end
    // TODO 加速處理（加速 速度２倍） start
    private void speedUp(L1PcInstance pc, int x, int y) {
        pc.setSkillEffect(SPEED_EFFECT, 15 * 1000);
        int time = 15;
        int objectId = pc.getId();
        pc.setSkillEffect(L1SkillId.HASTE, time * 1000);
        pc.setSkillEffect(L1SkillId.STATUS_BRAVE, time * 1000);
        pc.setBraveSpeed(1);
        pc.sendPacketsAll(new S_SkillBrave(objectId, 5, time));
        for (L1PcInstance player : playerList) {
            player.sendPackets(new S_EffectLocation(x, y, 6674));
        }
    }

    public void checkLapFinish(L1PcInstance pc) {
        if ((pc.getMapId() != 5143) || (getGameStatus() != 2)) {
            return;
        }
        onEffectTrap(pc);
        int x = pc.getX();
        int y = pc.getY();
        int check = pc.getLapCheck();
        if ((x == 32762) && (y >= 32845) && (check == 0)) {
            pc.setLapCheck(check + 1);
        } else if ((x == 32754) && (y >= 32845) && (check == 1)) {
            pc.setLapCheck(check + 1);
        } else if ((x == 32748) && (y >= 32845) && (check == 2)) {
            pc.setLapCheck(check + 1);
        } else if ((x <= 32743) && (y == 32844) && (check == 3)) {
            pc.setLapCheck(check + 1);
        } else if ((x <= 32742) && (y == 32840) && (check == 4)) {
            pc.setLapCheck(check + 1);
        } else if ((x <= 32742) && (y == 32835) && (check == 5)) {
            pc.setLapCheck(check + 1);
        } else if ((x <= 32742) && (y == 32830) && (check == 6)) {
            pc.setLapCheck(check + 1);
        } else if ((x <= 32742) && (y == 32826) && (check == 7)) {
            pc.setLapCheck(check + 1);
        } else if ((x <= 32742) && (y == 32822) && (check == 8)) {
            pc.setLapCheck(check + 1);
        } else if ((x == 32749) && (y <= 32818) && (check == 9)) {
            pc.setLapCheck(check + 1);
        } else if ((x == 32755) && (y <= 32818) && (check == 10)) {
            pc.setLapCheck(check + 1);
        } else if ((x == 32760) && (y <= 32818) && (check == 11)) {
            pc.setLapCheck(check + 1);
        } else if ((x == 32765) && (y <= 32818) && (check == 12)) {
            pc.setLapCheck(check + 1);
        } else if ((x == 32770) && (y <= 32818) && (check == 13)) {
            pc.setLapCheck(check + 1);
        } else if ((x == 32775) && (y <= 32818) && (check == 14)) {
            pc.setLapCheck(check + 1);
        } else if ((x == 32780) && (y <= 32818) && (check == 15)) {
            pc.setLapCheck(check + 1);
        } else if ((x == 32785) && (y <= 32818) && (check == 16)) {
            pc.setLapCheck(check + 1);
        } else if ((x == 32789) && (y <= 32818) && (check == 17)) {
            pc.setLapCheck(check + 1);
        } else if ((x >= 32792) && (y == 32821) && (check == 18)) {
            pc.setLapCheck(check + 1);
        } else if ((x >= 32793) && (y == 32826) && (check == 19)) {
            pc.setLapCheck(check + 1);
        } else if ((x >= 32793) && (y == 32831) && (check == 20)) {
            pc.setLapCheck(check + 1);
        } else if ((x >= 32793) && (y == 32836) && (check == 21)) {
            pc.setLapCheck(check + 1);
        } else if ((x >= 32793) && (y == 32842) && (check == 22)) {
            pc.setLapCheck(check + 1);
        } else if ((x == 32790) && (y >= 32845) && (check == 23)) {
            pc.setLapCheck(check + 1);
        } else if ((x == 32785) && (y >= 32845) && (check == 24)) {
            pc.setLapCheck(check + 1);
        } else if ((x == 32780) && (y >= 32845) && (check == 25)) {
            pc.setLapCheck(check + 1);
        } else if ((x == 32775) && (y >= 32845) && (check == 26)) {
            pc.setLapCheck(check + 1);
        } else if ((x == 32770) && (y >= 32845) && (check == 27)) {
            pc.setLapCheck(check + 1);
        } else if ((x == 32764) && (y >= 32845) && (check == 28)) {
            if (pc.getLap() == 4) {
                setGameWinner(pc);
                return;
            }
            pc.setLapCheck(0);
            pc.setLap(pc.getLap() + 1);
            pc.sendPackets(new S_Race(4, pc.getLap()));
        }
    }

    public int getGameStatus() {
        return _status;
    }

    public void setGameStatus(int i) {
        _status = i;
    }

    private void clearTime() {
        _time = 0;
    }

    private void addTime() {
        _time += 1;
    }

    public L1PcInstance getWinner() {
        return _winner;
    }

    public void setWinner(L1PcInstance pc) {
        _winner = pc;
    }

    private void startReadyTimer() {
        new ReadyTimer().begin();
    }

    private void startCheckTimer() {
        new CheckTimer().begin();
    }

    private void startClockTimer() {
        new ClockTimer().begin();
    }

    private void startGameTimeLimitTimer() {
        limitTimer = new GameTimeLimitTimer();
        com.lineage.server.thread.GeneralThreadPool.get().schedule(limitTimer, limitTime);
    }

    private void stopGameTimeLimitTimer() {
        limitTimer.stopTimer();
    }

    private void startEndTimer() {
        new EndTimer().begin();
    }

    private void startCompareTimer() {
        compareTimer = new CompareTimer();
        com.lineage.server.thread.GeneralThreadPool.get().scheduleAtFixedRate(compareTimer, 2000L, 2000L);
    }

    private void stopCompareTimer() {
        compareTimer.stopTimer();
    }

    private class CheckTimer extends TimerTask {
        private CheckTimer() {
        }

        public void run() {
            if (L1PolyRace.this.checkPlayersOK()) {
                L1PolyRace.this.setGameStart();
            } else {
                L1PolyRace.this.setGameEnd(3);
            }
            cancel();
        }

        public void begin() {
            com.lineage.server.thread.GeneralThreadPool.get().schedule(this, 30000L);
        }
    }

    private class ClockTimer extends TimerTask {
        private ClockTimer() {
        }

        public void run() {
            for (L1PcInstance pc : playerList) {
                pc.sendPackets(new S_Race(65));
            }
            L1PolyRace.this.setDoorClose(false);
            L1PolyRace.this.startGameTimeLimitTimer();
            cancel();
        }

        public void begin() {
            com.lineage.server.thread.GeneralThreadPool.get().schedule(this, 5000L);
        }
    }

    private class CompareTimer extends TimerTask {
        private CompareTimer() {
        }

        public void run() {
            L1PolyRace.this.comparePosition();
            L1PolyRace.this.addTime();
        }

        public void stopTimer() {
            cancel();
        }
    }

    private class EndTimer extends TimerTask {
        private EndTimer() {
        }

        public void run() {
            L1PolyRace.this.giftWinner();
            L1PolyRace.this.setGameInit();
            cancel();
        }

        public void begin() {
            com.lineage.server.thread.GeneralThreadPool.get().schedule(this, 5000L);
        }
    }

    private class GameTimeLimitTimer extends TimerTask {
        private GameTimeLimitTimer() {
        }

        public void run() {
            L1PolyRace.this.setGameEnd(2);
            cancel();
        }

        public void stopTimer() {
            cancel();
        }
    }

    private class ReadyTimer extends TimerTask {
        private ReadyTimer() {
        }

        public void run() {
            for (L1PcInstance pc : playerList) {
                pc.sendPackets(new S_ServerMessage(1258));
            }
            L1PolyRace.this.startCheckTimer();
            cancel();
        }

        public void begin() {
            com.lineage.server.thread.GeneralThreadPool.get().schedule(this, L1PolyRace.readyTime);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.L1PolyRace JD-Core Version: 0.6.2
 */