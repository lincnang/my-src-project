package com.add.Mobbling;

import com.add.L1Config;
import com.lineage.server.model.Instance.L1NpcInstance;

public class MobblingTimeList {
    private static MobblingTimeList _instance;
    boolean _isStart = false;
    boolean _isWaiting = false;
    boolean _isBuy = false;
    private L1NpcInstance _npcMob1 = null;
    private L1NpcInstance _npcMob2 = null;
    private L1NpcInstance _npcMob3 = null;
    private L1NpcInstance _npcMob4 = null;
    private L1NpcInstance _npcMob5 = null;
    private L1NpcInstance _npcMob6 = null;
    private L1NpcInstance _npcMob7 = null;
    private L1NpcInstance _npcMob8 = null;
    private L1NpcInstance _npcMob9 = null;
    private L1NpcInstance _npcMob10 = null;
    private int _npcChip1A = 0;
    private int _npcChip2A = 0;
    private int _npcChip3A = 0;
    private int _npcChip4A = 0;
    private int _npcChip5A = 0;
    private int _npcChip6A = 0;
    private int _npcChip7A = 0;
    private int _npcChip8A = 0;
    private int _npcChip9A = 0;
    private int _npcChip10A = 0;
    private double _npcRate1A = 2.0D;
    private double _npcRate2A = 2.0D;
    private double _npcRate3A = 2.0D;
    private double _npcRate4A = 2.0D;
    private double _npcRate5A = 2.0D;
    private double _npcRate6A = 2.0D;
    private double _npcRate7A = 2.0D;
    private double _npcRate8A = 2.0D;
    private double _npcRate9A = 2.0D;
    private double _npcRate10A = 2.0D;
    private int _MobId = 0;

    public static MobblingTimeList Mob() {
        if (_instance == null) {
            _instance = new MobblingTimeList();
        }
        return _instance;
    }

    public void clear() {
        try {
            _npcMob1.deleteMe();
            _npcMob2.deleteMe();
            _npcMob3.deleteMe();
            _npcMob4.deleteMe();
            _npcMob5.deleteMe();
            _npcMob6.deleteMe();
            _npcMob7.deleteMe();
            _npcMob8.deleteMe();
            _npcMob9.deleteMe();
            _npcMob10.deleteMe();
            _npcMob1 = null;
            _npcMob2 = null;
            _npcMob3 = null;
            _npcMob4 = null;
            _npcMob5 = null;
            _npcMob6 = null;
            _npcMob7 = null;
            _npcMob8 = null;
            _npcMob9 = null;
            _npcMob10 = null;
            _npcChip1A = 0;
            _npcChip2A = 0;
            _npcChip3A = 0;
            _npcChip4A = 0;
            _npcChip5A = 0;
            _npcChip6A = 0;
            _npcChip7A = 0;
            _npcChip8A = 0;
            _npcChip9A = 0;
            _npcChip10A = 0;
            _npcRate1A = 2.0D;
            _npcRate2A = 2.0D;
            _npcRate3A = 2.0D;
            _npcRate4A = 2.0D;
            _npcRate5A = 2.0D;
            _npcRate6A = 2.0D;
            _npcRate7A = 2.0D;
            _npcRate8A = 2.0D;
            _npcRate9A = 2.0D;
            _npcRate10A = 2.0D;
            _isStart = false;
            _isBuy = false;
            _isWaiting = false;
            _MobId = 0;
        } catch (Exception e) {
            e.getLocalizedMessage();
        }
    }

    public boolean get_isStart() {
        return _isStart;
    }

    public void set_isStart(boolean b) {
        _isStart = b;
    }

    public boolean get_isBuy() {
        return _isBuy;
    }

    public void set_isBuy(boolean b) {
        _isBuy = b;
    }

    public boolean get_isWaiting() {
        return _isWaiting;
    }

    public void set_isWaiting(boolean b) {
        _isWaiting = b;
    }

    public int get_MobId() {
        return _MobId;
    }

    public void set_MobId(int i) {
        _MobId = i;
    }

    public L1NpcInstance get_npcMob1() {
        return _npcMob1;
    }

    public void set_npcMob1(L1NpcInstance npc) {
        _npcMob1 = npc;
    }

    public L1NpcInstance get_npcMob2() {
        return _npcMob2;
    }

    public void set_npcMob2(L1NpcInstance npc) {
        _npcMob2 = npc;
    }

    public L1NpcInstance get_npcMob3() {
        return _npcMob3;
    }

    public void set_npcMob3(L1NpcInstance npc) {
        _npcMob3 = npc;
    }

    public L1NpcInstance get_npcMob4() {
        return _npcMob4;
    }

    public void set_npcMob4(L1NpcInstance npc) {
        _npcMob4 = npc;
    }

    public L1NpcInstance get_npcMob5() {
        return _npcMob5;
    }

    public void set_npcMob5(L1NpcInstance npc) {
        _npcMob5 = npc;
    }

    public L1NpcInstance get_npcMob6() {
        return _npcMob6;
    }

    public void set_npcMob6(L1NpcInstance npc) {
        _npcMob6 = npc;
    }

    public L1NpcInstance get_npcMob7() {
        return _npcMob7;
    }

    public void set_npcMob7(L1NpcInstance npc) {
        _npcMob7 = npc;
    }

    public L1NpcInstance get_npcMob8() {
        return _npcMob8;
    }

    public void set_npcMob8(L1NpcInstance npc) {
        _npcMob8 = npc;
    }

    public L1NpcInstance get_npcMob9() {
        return _npcMob9;
    }

    public void set_npcMob9(L1NpcInstance npc) {
        _npcMob9 = npc;
    }

    public L1NpcInstance get_npcMob10() {
        return _npcMob10;
    }

    public void set_npcMob10(L1NpcInstance npc) {
        _npcMob10 = npc;
    }

    public int get_npcChip1A() {
        return _npcChip1A;
    }

    public void add_npcChip1A(int i) {
        _npcChip1A += i;
    }

    public int get_npcChip2A() {
        return _npcChip2A;
    }

    public void add_npcChip2A(int i) {
        _npcChip2A += i;
    }

    public int get_npcChip3A() {
        return _npcChip3A;
    }

    public void add_npcChip3A(int i) {
        _npcChip3A += i;
    }

    public int get_npcChip4A() {
        return _npcChip4A;
    }

    public void add_npcChip4A(int i) {
        _npcChip4A += i;
    }

    public int get_npcChip5A() {
        return _npcChip5A;
    }

    public void add_npcChip5A(int i) {
        _npcChip5A += i;
    }

    public int get_npcChip6A() {
        return _npcChip6A;
    }

    public void add_npcChip6A(int i) {
        _npcChip6A += i;
    }

    public int get_npcChip7A() {
        return _npcChip7A;
    }

    public void add_npcChip7A(int i) {
        _npcChip7A += i;
    }

    public int get_npcChip8A() {
        return _npcChip8A;
    }

    public void add_npcChip8A(int i) {
        _npcChip8A += i;
    }

    public int get_npcChip9A() {
        return _npcChip9A;
    }

    public void add_npcChip9A(int i) {
        _npcChip9A += i;
    }

    public int get_npcChip10A() {
        return _npcChip10A;
    }

    public void add_npcChip10A(int i) {
        _npcChip10A += i;
    }

    public double get_npcRate1A() {
        return _npcRate1A;
    }

    public double get_npcRate2A() {
        return _npcRate2A;
    }

    public double get_npcRate3A() {
        return _npcRate3A;
    }

    public double get_npcRate4A() {
        return _npcRate4A;
    }

    public double get_npcRate5A() {
        return _npcRate5A;
    }

    public double get_npcRate6A() {
        return _npcRate6A;
    }

    public double get_npcRate7A() {
        return _npcRate7A;
    }

    public double get_npcRate8A() {
        return _npcRate8A;
    }

    public double get_npcRate9A() {
        return _npcRate9A;
    }

    public double get_npcRate10A() {
        return _npcRate10A;
    }

    public void computationRate() {
        // 全部彩金
        int totalprice = get_npcChip1A() + get_npcChip2A() + get_npcChip3A() + get_npcChip4A() + get_npcChip5A() + get_npcChip6A() + get_npcChip7A() + get_npcChip8A() + get_npcChip9A() + get_npcChip10A();
        int commission = L1Config._2152 / 100; // 佣金
        int aftertp = totalprice * (1 - commission); // 扣除佣金後剩餘彩金
        if ((aftertp != 0) && (get_npcChip1A() != 0)) {
            _npcRate1A = aftertp / get_npcChip1A();
            if (_npcRate1A > 100.0D) {
                _npcRate1A = 100.0D;
            }
            if (_npcRate1A < 2.0D) {
                _npcRate1A = 2.0D;
            }
        }
        if ((aftertp != 0) && (get_npcChip2A() != 0)) {
            _npcRate2A = aftertp / get_npcChip2A();
            if (_npcRate2A > 100.0D) {
                _npcRate2A = 100.0D;
            }
            if (_npcRate2A < 2.0D) {
                _npcRate2A = 2.0D;
            }
        }
        if ((aftertp != 0) && (get_npcChip3A() != 0)) {
            _npcRate3A = aftertp / get_npcChip3A();
            if (_npcRate3A > 100.0D) {
                _npcRate3A = 100.0D;
            }
            if (_npcRate3A < 2.0D) {
                _npcRate3A = 2.0D;
            }
        }
        if ((aftertp != 0) && (get_npcChip4A() != 0)) {
            _npcRate4A = aftertp / get_npcChip4A();
            if (_npcRate4A > 100.0D) {
                _npcRate4A = 100.0D;
            }
            if (_npcRate4A < 2.0D) {
                _npcRate4A = 2.0D;
            }
        }
        if ((aftertp != 0) && (get_npcChip5A() != 0)) {
            _npcRate5A = aftertp / get_npcChip5A();
            if (_npcRate5A > 100.0D) {
                _npcRate5A = 100.0D;
            }
            if (_npcRate5A < 2.0D) {
                _npcRate5A = 2.0D;
            }
        }
        if ((aftertp != 0) && (get_npcChip6A() != 0)) {
            _npcRate6A = aftertp / get_npcChip6A();
            if (_npcRate6A > 100.0D) {
                _npcRate6A = 100.0D;
            }
            if (_npcRate6A < 2.0D) {
                _npcRate6A = 2.0D;
            }
        }
        if ((aftertp != 0) && (get_npcChip7A() != 0)) {
            _npcRate7A = aftertp / get_npcChip7A();
            if (_npcRate7A > 100.0D) {
                _npcRate7A = 100.0D;
            }
            if (_npcRate7A < 2.0D) {
                _npcRate7A = 2.0D;
            }
        }
        if ((aftertp != 0) && (get_npcChip8A() != 0)) {
            _npcRate8A = aftertp / get_npcChip8A();
            if (_npcRate8A > 100.0D) {
                _npcRate8A = 100.0D;
            }
            if (_npcRate8A < 2.0D) {
                _npcRate8A = 2.0D;
            }
        }
        if ((aftertp != 0) && (get_npcChip9A() != 0)) {
            _npcRate9A = aftertp / get_npcChip9A();
            if (_npcRate9A > 100.0D) {
                _npcRate9A = 100.0D;
            }
            if (_npcRate9A < 2.0D) {
                _npcRate9A = 2.0D;
            }
        }
        if ((aftertp != 0) && (get_npcChip10A() != 0)) {
            _npcRate10A = aftertp / get_npcChip10A();
            if (_npcRate10A > 100.0D) {
                _npcRate10A = 100.0D;
            }
            if (_npcRate10A < 2.0D) {
                _npcRate10A = 2.0D;
            }
        }
    }
}