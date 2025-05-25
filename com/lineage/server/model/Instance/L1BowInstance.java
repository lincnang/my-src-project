package com.lineage.server.model.Instance;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.S_RemoveObject;
import com.lineage.server.serverpackets.S_UseArrowSkill;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;

public class L1BowInstance extends L1NpcInstance {
    private static final long serialVersionUID = 1L;
    private static final Log _log = LogFactory.getLog(L1BowInstance.class);
    private int _bowid = 66;
    private int _time = 1000;
    private int _dmg = 15;
    private int _out_x = 0;
    private int _out_y = 0;
    private boolean _start = true;

    public L1BowInstance(L1Npc template) {
        super(template);
    }

    public void set_info(int bowid, int h, int dmg, int time) {
        _bowid = bowid;
        _dmg = dmg;
        _time = time;
    }

    public int get_dmg() {
        return _dmg;
    }

    public void set_dmg(int dmg) {
        _dmg = dmg;
    }

    public int get_time() {
        return _time;
    }

    public void set_time(int time) {
        _time = time;
    }

    public int get_bowid() {
        return _bowid;
    }

    public void set_bowid(int bowid) {
        _bowid = bowid;
    }

    public boolean get_start() {
        return _start;
    }

    public void onPerceive(L1PcInstance perceivedFrom) {
        try {
            if ((_out_x == 0) && (_out_y == 0)) {
                set_atkLoc();
            }
            if (!_start) {
                _start = true;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void deleteMe() {
        try {
            _destroyed = true;
            World.get().removeVisibleObject(this);
            World.get().removeObject(this);
            for (L1PcInstance pc : World.get().getRecognizePlayer(this)) {
                pc.removeKnownObject(this);
                pc.sendPackets(new S_RemoveObject(this));
            }
            removeAllKnownObjects();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void atkTrag() {
        try {
            int out_x = _out_x;
            int out_y = _out_y;
            int tgid = 0;
            L1Character tg = checkTg();
            if (tg != null) {
                tgid = tg.getId();
                switch (getHeading()) {
                    case 0:
                        out_y = tg.getY();
                        break;
                    case 2:
                        out_x = tg.getX();
                        break;
                    case 4:
                        out_y = tg.getY();
                        break;
                    case 6:
                        out_x = tg.getX();
                    case 1:
                    case 3:
                    case 5:
                }
                if ((tg instanceof L1PcInstance)) {
                    L1PcInstance trag = (L1PcInstance) tg;
                    trag.receiveDamage(null, _dmg, false, true);
                } else if ((tg instanceof L1PetInstance)) {
                    L1PetInstance trag = (L1PetInstance) tg;
                    trag.receiveDamage(null, _dmg);
                } else if ((tg instanceof L1SummonInstance)) {
                    L1SummonInstance trag = (L1SummonInstance) tg;
                    trag.receiveDamage(null, _dmg);
                } else if ((tg instanceof L1MonsterInstance)) {
                    L1MonsterInstance trag = (L1MonsterInstance) tg;
                    trag.receiveDamage(null, _dmg);
                }
            }
            broadcastPacketAll(new S_UseArrowSkill(this, tgid, _bowid, out_x, out_y, _dmg, 1));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public boolean checkPc() {
        try {
            if (World.get().getRecognizePlayer(this).size() <= 0) {
                _start = false;
                return false;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return true;
    }

    private L1Character checkTg() {
        ArrayList<L1Object> tgs = World.get().getVisibleObjects(this, -1);
        for (L1Object object : tgs) {
            if ((object instanceof L1Character)) {
                L1Character cha = (L1Character) object;
                boolean isCheck = false;
                if ((cha instanceof L1PcInstance)) {
                    isCheck = true;
                } else if ((cha instanceof L1PetInstance)) {
                    isCheck = true;
                } else if ((cha instanceof L1SummonInstance)) {
                    isCheck = true;
                } else if ((cha instanceof L1MonsterInstance)) {
                    isCheck = true;
                }
                if (isCheck) {
                    switch (getHeading()) {
                        case 0:
                            if ((object.getX() == getX()) && (object.getY() <= getY()) && (object.getY() >= _out_y)) {
                                return cha;
                            }
                            break;
                        case 2:
                            if ((object.getX() >= getX()) && (object.getX() <= _out_x) && (object.getY() == getY())) {
                                return cha;
                            }
                            break;
                        case 4:
                            if ((object.getX() == getX()) && (object.getY() >= getY()) && (object.getY() <= _out_y)) {
                                return cha;
                            }
                            break;
                        case 6:
                            if ((object.getX() <= getX()) && (object.getX() >= _out_x) && (object.getY() == getY())) {
                                return cha;
                            }
                            break;
                        case 1:
                        case 3:
                        case 5:
                    }
                }
            }
        }
        return null;
    }

    private void set_atkLoc() {
        try {
            boolean test = true;
            int x = getX();
            int y = getY();
            switch (getHeading()) {
                case 0:
                    while (test) {
                        int gab = getMap().getOriginalTile(x, y--);
                        if (gab == 0) {
                            test = false;
                        }
                    }
                    break;
                case 2:
                    while (test) {
                        int gab = getMap().getOriginalTile(x++, y);
                        if (gab == 0) {
                            test = false;
                        }
                    }
                    break;
                case 4:
                    while (test) {
                        int gab = getMap().getOriginalTile(x, y++);
                        if (gab == 0) {
                            test = false;
                        }
                    }
                    break;
                case 6:
                    while (test) {
                        int gab = getMap().getOriginalTile(x--, y);
                        if (gab == 0) {
                            test = false;
                        }
                    }
                case 1:
                case 3:
                case 5:
            }
            if (!test) {
                _out_x = x;
                _out_y = y;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
