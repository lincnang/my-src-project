package com.lineage.server.model;

import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.collections.Lists;
import com.lineage.server.world.WorldNpc;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class L1HauntedHouse {
    public static final int STATUS_NONE = 0;
    public static final int STATUS_READY = 1;
    public static final int STATUS_PLAYING = 2;
    private static L1HauntedHouse _instance;
    private final List<L1PcInstance> _members = Lists.newArrayList();
    private int _hauntedHouseStatus = 0;
    private int _winnersCount = 0;
    private int _goalCount = 0;

    public static L1HauntedHouse getInstance() {
        if (_instance == null) {
            _instance = new L1HauntedHouse();
        }
        return _instance;
    }

    private void readyHauntedHouse() {
        setHauntedHouseStatus(1);
        L1HauntedHouseReadyTimer hhrTimer = new L1HauntedHouseReadyTimer();
        hhrTimer.begin();
    }

    private void startHauntedHouse() {
        setHauntedHouseStatus(2);
        int membersCount = getMembersCount();
        if (membersCount <= 4) {
            setWinnersCount(1);
        } else if (5 == membersCount) {
            setWinnersCount(2);
        } else if (8 >= membersCount) {
            setWinnersCount(3);
        }
        for (L1PcInstance pc : getMembersArray()) {
            L1SkillUse l1skilluse = new L1SkillUse();
            l1skilluse.handleCommands(pc, 44, pc.getId(), pc.getX(), pc.getY(), 0, 1);
            L1PolyMorph.doPoly(pc, 6284, 300, 4);
        }
        for (final L1NpcInstance object : WorldNpc.get().all()) {
            if (object instanceof L1DoorInstance) {
                L1DoorInstance door = (L1DoorInstance) object;
                if (door.getMapId() == 5140) {
                    door.open();
                }
            }
        }
    }

    public void endHauntedHouse() {
        setHauntedHouseStatus(0);
        setWinnersCount(0);
        setGoalCount(0);
        for (L1PcInstance pc : getMembersArray()) {
            if (pc.getMapId() == 5140) {
                L1SkillUse l1skilluse = new L1SkillUse();
                l1skilluse.handleCommands(pc, 44, pc.getId(), pc.getX(), pc.getY(), 0, 1);
                L1Teleport.teleport(pc, 32624, 32813, (short) 4, 5, true);
            }
        }
        clearMembers();
        for (final L1NpcInstance object : WorldNpc.get().all()) {
            if (object instanceof L1DoorInstance) {
                L1DoorInstance door = (L1DoorInstance) object;
                if (door.getMapId() == 5140) {
                    door.close();
                }
            }
        }
    }

    public void removeRetiredMembers() {
        L1PcInstance[] temp = getMembersArray();
        for (L1PcInstance element : temp) {
            if (element.getMapId() != 5140) {
                removeMember(element);
            }
        }
    }

    public void sendMessage(int type, String msg) {
        for (L1PcInstance pc : getMembersArray()) {
            pc.sendPackets(new S_ServerMessage(type, msg));
        }
    }

    public void addMember(L1PcInstance pc) {
        if (!_members.contains(pc)) {
            _members.add(pc);
        }
        if (getMembersCount() == 1 && getHauntedHouseStatus() == 0) {
            readyHauntedHouse();
        }
    }

    public void removeMember(L1PcInstance pc) {
        _members.remove(pc);
    }

    public void clearMembers() {
        _members.clear();
    }

    public boolean isMember(L1PcInstance pc) {
        return _members.contains(pc);
    }

    public L1PcInstance[] getMembersArray() {
        return (L1PcInstance[]) _members.toArray(new L1PcInstance[_members.size()]);
    }

    public int getMembersCount() {
        return _members.size();
    }

    public int getHauntedHouseStatus() {
        return _hauntedHouseStatus;
    }

    private void setHauntedHouseStatus(int i) {
        _hauntedHouseStatus = i;
    }

    public int getWinnersCount() {
        return _winnersCount;
    }

    private void setWinnersCount(int i) {
        _winnersCount = i;
    }

    public int getGoalCount() {
        return _goalCount;
    }

    public void setGoalCount(int i) {
        _goalCount = i;
    }

    public class L1HauntedHouseReadyTimer extends TimerTask {
        public L1HauntedHouseReadyTimer() {
        }

        public void run() {
            L1HauntedHouse.this.startHauntedHouse();
            L1HauntedHouse.L1HauntedHouseTimer hhTimer = new L1HauntedHouse.L1HauntedHouseTimer();
            hhTimer.begin();
        }

        public void begin() {
            Timer timer = new Timer();
            timer.schedule(this, 90000L);
        }
    }

    public class L1HauntedHouseTimer extends TimerTask {
        public L1HauntedHouseTimer() {
        }

        public void run() {
            endHauntedHouse();
            cancel();
        }

        public void begin() {
            Timer timer = new Timer();
            timer.schedule(this, 300000L);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.L1HauntedHouse JD-Core Version: 0.6.2
 */