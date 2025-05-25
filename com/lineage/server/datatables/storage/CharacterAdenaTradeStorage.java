package com.lineage.server.datatables.storage;

import com.lineage.server.templates.L1CharacterAdenaTrade;

import java.util.Collection;
import java.util.Map;

public interface CharacterAdenaTradeStorage {
    public void load();

    public boolean createAdenaTrade(final L1CharacterAdenaTrade adenaTrade);

    public boolean updateAdenaTrade(final int id, final int over);

    public Map<Integer, L1CharacterAdenaTrade> getAdenaTrades();

    public L1CharacterAdenaTrade getCharacterAdenaTrade(final int id);

    public int nextId();

    public Collection<L1CharacterAdenaTrade> getAllCharacterAdenaTrades();
}
