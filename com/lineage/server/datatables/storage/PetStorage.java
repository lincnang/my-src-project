package com.lineage.server.datatables.storage;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.templates.L1Pet;

public abstract interface PetStorage {
    public abstract void load();

    public abstract void storeNewPet(L1NpcInstance paramL1NpcInstance, int paramInt1, int paramInt2);

    public abstract void storePet(L1Pet paramL1Pet);

    public abstract void deletePet(int paramInt);

    public abstract boolean isNameExists(String paramString);

    public abstract L1Pet getTemplate(int paramInt);

    public abstract L1Pet getTemplateX(int paramInt);

    public abstract L1Pet[] getPetTableList();

    public abstract void buyNewPet(int petNpcId, int i, int id, int upLv, long lvExp);
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.storage.PetStorage JD-Core Version: 0.6.2
 */