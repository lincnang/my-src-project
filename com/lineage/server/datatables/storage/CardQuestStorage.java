package com.lineage.server.datatables.storage;

import com.add.Tsai.CardQuest;

import java.util.List;

public interface CardQuestStorage {
    public void load();

    public List<CardQuest> get(final String account);

    public void storeQuest(final String account, final int key, final CardQuest paramCharQuest);

    public void storeQuest(final String paramInt1, final int paramInt2, final CardQuest paramCharQuest, final int paramInt3);

    public void updateQuest(final String account, final int key, final CardQuest paramCharQuest);

    public void delQuest(final String account, final int key);

    public void storeQuest2(String account, int key, int value);

    public void delQuest2(final int key);
}