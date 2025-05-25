package com.lineage.server.datatables.lock;

import com.lineage.server.datatables.sql.CharOtherTable1;
import com.lineage.server.datatables.storage.CharOtherStorage1;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1PcOther1;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CharOtherReading1 {
  private final Lock _lock = new ReentrantLock(true);

  private final CharOtherStorage1 _storage = (CharOtherStorage1)new CharOtherTable1();

  private static CharOtherReading1 _instance;

  public static CharOtherReading1 get() {
    if (_instance == null)
      _instance = new CharOtherReading1();
    return _instance;
  }

  public void load() {
    this._lock.lock();
    try {
      this._storage.load();
    } finally {
      this._lock.unlock();
    }
  }

  public L1PcOther1 getOther(L1PcInstance pc) {
    L1PcOther1 tmp;
    this._lock.lock();
    try {
      tmp = this._storage.getOther(pc);
    } finally {
      this._lock.unlock();
    }
    return tmp;
  }

  public void storeOther(int objId, L1PcOther1 other) {
    this._lock.lock();
    try {
      this._storage.storeOther(objId, other);
    } finally {
      this._lock.unlock();
    }
  }

  public void tam() {
    this._lock.lock();
    try {
      this._storage.tam();
    } finally {
      this._lock.unlock();
    }
  }
}
