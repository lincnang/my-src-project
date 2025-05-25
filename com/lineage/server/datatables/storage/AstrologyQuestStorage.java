package com.lineage.server.datatables.storage;

import com.add.Tsai.Astrology.AstrologyQuest;

public interface AstrologyQuestStorage {
    public void load();

    public AstrologyQuest get(final int objId, final int astrologyType);

    public void storeQuest(final int objId, final int key, final int num);

    public void updateQuest(final int objId, final int key, final int num);

    public void delQuest(final int objId, final int key);

    public void storeQuest2(int objId, int key, int value);

    public void delQuest2(final int key);
}