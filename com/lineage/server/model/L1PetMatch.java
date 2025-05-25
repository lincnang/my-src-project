package com.lineage.server.model;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.lock.PetReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1Pet;
import com.lineage.server.world.World;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class L1PetMatch {
    public static final int STATUS_NONE = 0;
    public static final int STATUS_READY1 = 1;
    public static final int STATUS_READY2 = 2;
    public static final int STATUS_PLAYING = 3;
    public static final int MAX_PET_MATCH = 1;
    private static final short[] PET_MATCH_MAPID = {5125, 5131, 5132, 5133, 5134};
    private static L1PetMatch _instance;
    private String[] _pc1Name = new String[1];
    private String[] _pc2Name = new String[1];
    private L1PetInstance[] _pet1 = new L1PetInstance[1];
    private L1PetInstance[] _pet2 = new L1PetInstance[1];

    public static L1PetMatch getInstance() {
        if (_instance == null) {
            _instance = new L1PetMatch();
        }
        return _instance;
    }

    public int setPetMatchPc(int petMatchNo, L1PcInstance pc, L1PetInstance pet) {
        int status = getPetMatchStatus(petMatchNo);
        if (status == 0) {
            _pc1Name[petMatchNo] = pc.getName();
            _pet1[petMatchNo] = pet;
            return 1;
        }
        if (status == 1) {
            _pc2Name[petMatchNo] = pc.getName();
            _pet2[petMatchNo] = pet;
            return 3;
        }
        if (status == 2) {
            _pc1Name[petMatchNo] = pc.getName();
            _pet1[petMatchNo] = pet;
            return 3;
        }
        return 0;
    }

    private synchronized int getPetMatchStatus(int petMatchNo) {
        L1PcInstance pc1 = null;
        if (_pc1Name[petMatchNo] != null) {
            pc1 = World.get().getPlayer(_pc1Name[petMatchNo]);
        }
        L1PcInstance pc2 = null;
        if (_pc2Name[petMatchNo] != null) {
            pc2 = World.get().getPlayer(_pc2Name[petMatchNo]);
        }
        if ((pc1 == null) && (pc2 == null)) {
            return 0;
        }
        if ((pc1 == null) && (pc2 != null)) {
            if (pc2.getMapId() == PET_MATCH_MAPID[petMatchNo]) {
                return 2;
            }
            _pc2Name[petMatchNo] = null;
            _pet2[petMatchNo] = null;
            return 0;
        }
        if ((pc1 != null) && (pc2 == null)) {
            if (pc1.getMapId() == PET_MATCH_MAPID[petMatchNo]) {
                return 1;
            }
            _pc1Name[petMatchNo] = null;
            _pet1[petMatchNo] = null;
            return 0;
        }
        if ((pc1.getMapId() == PET_MATCH_MAPID[petMatchNo]) && (pc2.getMapId() == PET_MATCH_MAPID[petMatchNo])) {
            return 3;
        }
        if (pc1.getMapId() == PET_MATCH_MAPID[petMatchNo]) {
            _pc2Name[petMatchNo] = null;
            _pet2[petMatchNo] = null;
            return 1;
        }
        if (pc2.getMapId() == PET_MATCH_MAPID[petMatchNo]) {
            _pc1Name[petMatchNo] = null;
            _pet1[petMatchNo] = null;
            return 2;
        }
        return 0;
    }

    private int decidePetMatchNo() {
        for (int i = 0; i < 1; i++) {
            int status = getPetMatchStatus(i);
            if ((status == 1) || (status == 2)) {
                return i;
            }
        }
        for (int i = 0; i < 1; i++) {
            int status = getPetMatchStatus(i);
            if (status == 0) {
                return i;
            }
        }
        return -1;
    }

    public synchronized boolean enterPetMatch(L1PcInstance pc, int amuletId) {
        int petMatchNo = decidePetMatchNo();
        if (petMatchNo == -1) {
            return false;
        }
        L1PetInstance pet = withdrawPet(pc, amuletId);
        L1Teleport.teleport(pc, 32799, 32868, PET_MATCH_MAPID[petMatchNo], 0, true);
        L1PetMatchReadyTimer timer = new L1PetMatchReadyTimer(petMatchNo, pc, pet);
        timer.begin();
        return true;
    }

    private L1PetInstance withdrawPet(L1PcInstance pc, int amuletId) {
        L1Pet l1pet = PetReading.get().getTemplate(amuletId);
        if (l1pet == null) {
            return null;
        }
        L1Npc npcTemp = NpcTable.get().getTemplate(l1pet.get_npcid());
        L1PetInstance pet = new L1PetInstance(npcTemp, pc, l1pet);
        pet.setPetcost(6);
        return pet;
    }

    public void startPetMatch(int petMatchNo) {
        _pet1[petMatchNo].setCurrentPetStatus(1);
        _pet1[petMatchNo].setTarget(_pet2[petMatchNo]);
        _pet2[petMatchNo].setCurrentPetStatus(1);
        _pet2[petMatchNo].setTarget(_pet1[petMatchNo]);
        L1PetMatchTimer timer = new L1PetMatchTimer(_pet1[petMatchNo], _pet2[petMatchNo], petMatchNo);
        timer.begin();
    }

    public void endPetMatch(int petMatchNo, int winNo) {
        L1PcInstance pc1 = World.get().getPlayer(_pc1Name[petMatchNo]);
        L1PcInstance pc2 = World.get().getPlayer(_pc2Name[petMatchNo]);
        if (winNo == 1) {
            giveMedal(pc1, petMatchNo, true);
            giveMedal(pc2, petMatchNo, false);
        } else if (winNo == 2) {
            giveMedal(pc1, petMatchNo, false);
            giveMedal(pc2, petMatchNo, true);
        } else if (winNo == 3) {
            giveMedal(pc1, petMatchNo, false);
            giveMedal(pc2, petMatchNo, false);
        }
        qiutPetMatch(petMatchNo);
    }

    private void giveMedal(L1PcInstance pc, int petMatchNo, boolean isWin) {
        if (pc == null) {
            return;
        }
        if (pc.getMapId() != PET_MATCH_MAPID[petMatchNo]) {
            return;
        }
        if (isWin) {
            pc.sendPackets(new S_ServerMessage(1166, pc.getName()));
            L1ItemInstance item = ItemTable.get().createItem(41309);
            if ((item != null) && (pc.getInventory().checkAddItem(item, 3L) == 0)) {
                item.setCount(3L);
                pc.getInventory().storeItem(item);
                pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
            }
        } else {
            L1ItemInstance item = ItemTable.get().createItem(41309);
            if ((item != null) && (pc.getInventory().checkAddItem(item, 1L) == 0)) {
                item.setCount(1L);
                pc.getInventory().storeItem(item);
                pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
            }
        }
    }

    private void qiutPetMatch(int petMatchNo) {
        L1PcInstance pc1 = World.get().getPlayer(_pc1Name[petMatchNo]);
        L1PetInstance pet;
        if ((pc1 != null) && (pc1.getMapId() == PET_MATCH_MAPID[petMatchNo])) {
            for (Object object : pc1.getPetList().values().toArray()) {
                if ((object instanceof L1PetInstance)) {
                    pet = (L1PetInstance) object;
                    pet.dropItem();
                    pc1.getPetList().remove(Integer.valueOf(pet.getId()));
                    pet.deleteMe();
                }
            }
            L1Teleport.teleport(pc1, 32630, 32744, (short) 4, 4, true);
        }
        _pc1Name[petMatchNo] = null;
        _pet1[petMatchNo] = null;
        L1PcInstance pc2 = World.get().getPlayer(_pc2Name[petMatchNo]);
        if ((pc2 != null) && (pc2.getMapId() == PET_MATCH_MAPID[petMatchNo])) {
            for (Object object : pc2.getPetList().values().toArray()) {
                if ((object instanceof L1PetInstance)) {
                    pet = (L1PetInstance) object;
                    pet.dropItem();
                    pc2.getPetList().remove(Integer.valueOf(pet.getId()));
                    pet.deleteMe();
                }
            }
            L1Teleport.teleport(pc2, 32630, 32744, (short) 4, 4, true);
        }
        _pc2Name[petMatchNo] = null;
        _pet2[petMatchNo] = null;
    }

    public static class L1PetMatchReadyTimer extends TimerTask {
        private final int _petMatchNo;
        private final L1PcInstance _pc;
        private final L1PetInstance _pet;

        public L1PetMatchReadyTimer(int petMatchNo, L1PcInstance pc, L1PetInstance pet) {
            _petMatchNo = petMatchNo;
            _pc = pc;
            _pet = pet;
        }

        public void begin() {
            Timer timer = new Timer();
            timer.schedule(this, 3000L);
        }

        public void run() {
            try {
                do {
                    TimeUnit.MILLISECONDS.sleep(1000L);
                    if ((_pc == null) || (_pet == null)) {
                        cancel();
                        return;
                    }
                } while (_pc.isTeleport());
                if (L1PetMatch.getInstance().setPetMatchPc(_petMatchNo, _pc, _pet) == 3) {
                    L1PetMatch.getInstance().startPetMatch(_petMatchNo);
                }
                cancel();
                return;
            } catch (Throwable localThrowable) {
            }
        }
    }

    public static class L1PetMatchTimer extends TimerTask {
        private final L1PetInstance _pet1;
        private final L1PetInstance _pet2;
        private final int _petMatchNo;
        private int _counter = 0;

        public L1PetMatchTimer(L1PetInstance pet1, L1PetInstance pet2, int petMatchNo) {
            _pet1 = pet1;
            _pet2 = pet2;
            _petMatchNo = petMatchNo;
        }

        public void begin() {
            Timer timer = new Timer();
            timer.schedule(this, 0L);
        }

        public void run() {
            try {
                do {
                    TimeUnit.MILLISECONDS.sleep(3000L);
                    _counter += 1;
                    if ((_pet1 == null) || (_pet2 == null)) {
                        cancel();
                        return;
                    }
                    if ((_pet1.isDead()) || (_pet2.isDead())) {
                        int winner = 0;
                        if ((!_pet1.isDead()) && (_pet2.isDead())) {
                            winner = 1;
                        } else if ((_pet1.isDead()) && (!_pet2.isDead())) {
                            winner = 2;
                        } else {
                            winner = 3;
                        }
                        L1PetMatch.getInstance().endPetMatch(_petMatchNo, winner);
                        cancel();
                        return;
                    }
                } while (_counter != 100);
                L1PetMatch.getInstance().endPetMatch(_petMatchNo, 3);
                cancel();
                return;
            } catch (Throwable localThrowable) {
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.L1PetMatch JD-Core Version: 0.6.2
 */