package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.CharSkillTable;
import com.lineage.server.datatables.storage.CharSkillStorage;
import com.lineage.server.templates.L1UserSkillTmp;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CharSkillReading {
    private static CharSkillReading _instance;
    private final Lock _lock;
    private final CharSkillStorage _storage;

    private CharSkillReading() {
        _lock = new ReentrantLock(true);
        _storage = new CharSkillTable();
    }

    public static CharSkillReading get() {
        if (_instance == null) {
            _instance = new CharSkillReading();
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

    public ArrayList<L1UserSkillTmp> skills(int playerobjid) {
        _lock.lock();
        ArrayList<L1UserSkillTmp> tmp;
        try {
            tmp = _storage.skills(playerobjid);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public void spellMastery(int playerobjid, int skillid, String skillname, int active, int time) {
        _lock.lock();
        try {
            if (skillname.equals("none")) {
                return;
            }
            _storage.spellMastery(playerobjid, skillid, skillname, active, time);
        } finally {
            _lock.unlock();
        }
    }

    public void spellLost(int playerobjid, int skillid) {
        _lock.lock();
        try {
            _storage.spellLost(playerobjid, skillid);
        } finally {
            _lock.unlock();
        }
    }

    public boolean spellCheck(int playerobjid, int skillid) {
        _lock.lock();
        boolean tmp;
        try {
            tmp = _storage.spellCheck(playerobjid, skillid);
        } finally {
            _lock.unlock();
        }
        return tmp;
    }

    public void setAuto(int mode, int objid, int skillid) {
        _lock.lock();
        try {
            _storage.setAuto(mode, objid, skillid);
        } finally {
            _lock.unlock();
        }
    }
}
