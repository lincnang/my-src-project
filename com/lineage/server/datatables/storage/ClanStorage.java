package com.lineage.server.datatables.storage;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Clan;

import java.util.Map;

public abstract interface ClanStorage {
    public abstract void load();

    public abstract void addDeClan(Integer paramInteger, L1Clan paramL1Clan);

    public abstract L1Clan createClan(L1PcInstance paramL1PcInstance, String paramString);

    public abstract void updateClan(L1Clan paramL1Clan);

    public abstract void deleteClan(String paramString);

    public abstract L1Clan getTemplate(int paramInt);

    //public L1Clan getTemplate(final int clan_id);
    public abstract Map<Integer, L1Clan> get_clans();

    public abstract void updateClanSkill(L1Clan paramL1Clan);
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.storage.ClanStorage JD-Core Version: 0.6.2
 */