package com.lineage.server.datatables.storage;

import com.lineage.server.templates.CharQuest;

import java.util.Map;

public interface CharacterQuestStorage {
    public void load();

    public Map<Integer, CharQuest> get(final int char_id);

    public void storeQuest(final int char_id, final int key, final CharQuest paramCharQuest);

    public void storeQuest(final int paramInt1, final int paramInt2, final CharQuest paramCharQuest, final int paramInt3);

    public void updateQuest(final int char_id, final int key, final CharQuest paramCharQuest);

    public void delQuest(final int char_id, final int key);

    public void storeQuest2(int char_id, int key, int value);

    public void delQuest2(final int key);
}