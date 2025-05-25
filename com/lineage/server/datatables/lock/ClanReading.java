package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.ClanTable;
import com.lineage.server.datatables.storage.ClanStorage;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Clan;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClanReading {
    private static ClanReading _instance;
    private final Lock _lock;
    private final ClanStorage _storage;

    private ClanReading() {
        _lock = new ReentrantLock(true);
        _storage = new ClanTable();
    }

    public static ClanReading get() {
        if (_instance == null) {
            _instance = new ClanReading();
        }
        return _instance;
    }

    public void load() {
        _lock.lock();
        try {
            _storage.load();
        } finally {
            _lock.unlock();
        }
    }

    public void addDeClan(Integer integer, L1Clan clan) {
        _lock.lock();
        try {
            _storage.addDeClan(integer, clan);
        } finally {
            _lock.unlock();
        }
    }

    public L1Clan createClan(L1PcInstance player, String clan_name) {
        _lock.lock();
        L1Clan tmp = null;
        try {
            tmp = _storage.createClan(player, clan_name);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public void updateClan(L1Clan clan) {
        _lock.lock();
        try {
            _storage.updateClan(clan);
        } finally {
            _lock.unlock();
        }
    }

    public void deleteClan(String clan_name) {
        _lock.lock();
        try {
            _storage.deleteClan(clan_name);
        } finally {
            _lock.unlock();
        }
    }

    public void updateClanSkill(L1Clan clan) {
        this._lock.lock();
        try {
            this._storage.updateClanSkill(clan);
        } finally {
            this._lock.unlock();
        }
    }

    public L1Clan getTemplate(int clan_id) {
        _lock.lock();
        L1Clan tmp = null;
        try {
            tmp = _storage.getTemplate(clan_id);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public Map<Integer, L1Clan> get_clans() {
        _lock.lock();
        Map<Integer, L1Clan> tmp = null;
        try {
            tmp = _storage.get_clans();
        } finally {
            _lock.unlock();
        }
        return tmp;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.lock.ClanReading JD-Core Version: 0.6.2
 */