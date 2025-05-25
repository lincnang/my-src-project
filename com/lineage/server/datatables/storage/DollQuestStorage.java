package com.lineage.server.datatables.storage;

import com.add.Tsai.dollQuest;

import java.util.List;

public interface DollQuestStorage {
    public void load();

    public List<dollQuest> get(final String account);

    public void storeQuest(final String account, final int key, final dollQuest paramCharQuest);

    public void storeQuest(final String paramInt1, final int paramInt2, final dollQuest paramCharQuest, final int paramInt3);

    public void updateQuest(final String account, final int key, final dollQuest paramCharQuest);

    public void delQuest(final String account, final int key);

    public void storeQuest2(String account, int key, int value);

    public void delQuest2(final int key);
}