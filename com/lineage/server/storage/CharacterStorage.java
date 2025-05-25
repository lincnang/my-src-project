package com.lineage.server.storage;

import com.lineage.server.model.Instance.L1PcInstance;

public abstract interface CharacterStorage {
    public abstract void createCharacter(L1PcInstance paramL1PcInstance) throws Exception;

    public abstract void deleteCharacter(String paramString1, String paramString2) throws Exception;

    public abstract void storeCharacter(L1PcInstance paramL1PcInstance) throws Exception;

    public abstract void updateVipTime(L1PcInstance paramL1PcInstance) throws Exception;

    public abstract L1PcInstance loadCharacter(String paramString) throws Exception;

    public L1PcInstance loadCharacter(final int objid);
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.storage.CharacterStorage JD-Core Version: 0.6.2
 */