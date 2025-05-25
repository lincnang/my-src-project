package com.lineage.server.model;

import com.lineage.server.IdFactoryNpc;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.UBTable;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCPack;
import com.lineage.server.world.World;

public class L1UbSpawn implements Comparable<L1UbSpawn> {
    private int _id;
    private int _ubId;
    private int _pattern;
    private int _group;
    private int _npcTemplateId;
    private int _amount;
    private int _spawnDelay;
    private int _sealCount;
    private String _name;

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        _id = id;
    }

    public int getUbId() {
        return _ubId;
    }

    public void setUbId(int ubId) {
        _ubId = ubId;
    }

    public int getPattern() {
        return _pattern;
    }

    public void setPattern(int pattern) {
        _pattern = pattern;
    }

    public int getGroup() {
        return _group;
    }

    public void setGroup(int group) {
        _group = group;
    }

    public int getNpcTemplateId() {
        return _npcTemplateId;
    }

    public void setNpcTemplateId(int npcTemplateId) {
        _npcTemplateId = npcTemplateId;
    }

    public int getAmount() {
        return _amount;
    }

    public void setAmount(int amount) {
        _amount = amount;
    }

    public int getSpawnDelay() {
        return _spawnDelay;
    }

    public void setSpawnDelay(int spawnDelay) {
        _spawnDelay = spawnDelay;
    }

    public int getSealCount() {
        return _sealCount;
    }

    public void setSealCount(int i) {
        _sealCount = i;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public void spawnOne() {
        L1UltimateBattle ub = UBTable.getInstance().getUb(_ubId);
        L1Location loc = ub.getLocation().randomLocation((ub.getLocX2() - ub.getLocX1()) / 2, false);
        L1MonsterInstance mob = new L1MonsterInstance(NpcTable.get().getTemplate(getNpcTemplateId()));
        mob.setId(IdFactoryNpc.get().nextId());
        mob.setHeading(5);
        mob.setX(loc.getX());
        mob.setHomeX(loc.getX());
        mob.setY(loc.getY());
        mob.setHomeY(loc.getY());
        mob.setMap((short) loc.getMapId());
        mob.set_storeDroped(3 >= getGroup());
        mob.setUbSealCount(getSealCount());
        mob.setUbId(getUbId());
        World.get().storeObject(mob);
        World.get().addVisibleObject(mob);
        S_NPCPack s_npcPack = new S_NPCPack(mob);
        for (L1PcInstance pc : World.get().getRecognizePlayer(mob)) {
            pc.addKnownObject(mob);
            mob.addKnownObject(pc);
            pc.sendPackets(s_npcPack);
        }
        mob.onNpcAI();
        mob.turnOnOffLight();
    }

    public void spawnAll() {
        for (int i = 0; i < getAmount(); i++) {
            spawnOne();
        }
    }

    public int compareTo(L1UbSpawn rhs) {
        return Integer.compare(getId(), rhs.getId());
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.L1UbSpawn JD-Core Version: 0.6.2
 */