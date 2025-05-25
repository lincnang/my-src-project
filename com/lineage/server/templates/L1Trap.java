package com.lineage.server.templates;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.poison.L1DamagePoison;
import com.lineage.server.model.poison.L1ParalysisPoison;
import com.lineage.server.model.poison.L1SilencePoison;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_EffectLocation;
import com.lineage.server.utils.Dice;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.ResultSet;

public class L1Trap {
    private static final Log _log = LogFactory.getLog(L1Trap.class);
    private String _type;
    private int _trap;
    private int _id;
    private int _gfxId;
    private boolean _isDetectionable;
    private Dice _dice;
    private int _base;
    private int _diceCount;
    private int _npcId;
    private int _count;
    private int _poisonType;
    private int _delay;
    private int _time;
    private int _damage;
    private int _skillId;
    private int _skillTimeSeconds;
    private L1Location _loc;

    public L1Trap(ResultSet rs) {
        try {
            _id = rs.getInt("id");
            _gfxId = rs.getInt("gfxId");
            _isDetectionable = rs.getBoolean("isDetectionable");
            _type = rs.getString("type");
            if (_type.equalsIgnoreCase("L1DamageTrap")) {
                _trap = 1;
                _dice = new Dice(rs.getInt("dice"));
                _base = rs.getInt("base");
                _diceCount = rs.getInt("diceCount");
            } else if (_type.equalsIgnoreCase("L1HealingTrap")) {
                _trap = 2;
                _dice = new Dice(rs.getInt("dice"));
                _base = rs.getInt("base");
                _diceCount = rs.getInt("diceCount");
            } else if (_type.equalsIgnoreCase("L1MonsterTrap")) {
                _trap = 3;
                _npcId = rs.getInt("monsterNpcId");
                _count = rs.getInt("monsterCount");
            } else if (_type.equalsIgnoreCase("L1PoisonTrap")) {
                _trap = 4;
                String poisonType = rs.getString("poisonType");
                if (poisonType.equalsIgnoreCase("d")) {
                    _poisonType = 1;
                } else if (poisonType.equalsIgnoreCase("s")) {
                    _poisonType = 2;
                } else if (poisonType.equalsIgnoreCase("p")) {
                    _poisonType = 3;
                }
                _delay = rs.getInt("poisonDelay");
                _time = rs.getInt("poisonTime");
                _damage = rs.getInt("poisonDamage");
            } else if (_type.equalsIgnoreCase("L1SkillTrap")) {
                _trap = 5;
                _skillId = rs.getInt("skillId");
                _skillTimeSeconds = rs.getInt("skillTimeSeconds");
            } else if (_type.equalsIgnoreCase("L1TeleportTrap")) {
                _trap = 6;
                int x = rs.getInt("teleportX");
                int y = rs.getInt("teleportY");
                int mapId = rs.getInt("teleportMapId");
                _loc = new L1Location(x, y, mapId);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public String getType() {
        return _type + "(" + _trap + "-" + _id + ")";
    }

    public int getId() {
        return _id;
    }

    public int getGfxId() {
        return _gfxId;
    }

    private void sendEffect(L1Object trapObj) {
        try {
            if (getGfxId() == 0) {
                return;
            }
            S_EffectLocation effect = new S_EffectLocation(trapObj.getLocation(), getGfxId());
            for (L1PcInstance pc : World.get().getRecognizePlayer(trapObj)) {
                pc.sendPackets(effect);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void onTrod(L1PcInstance trodFrom, L1Object trapObj) {
        switch (_trap) {
            case 0:
                _log.error("陷阱的處理未定義: " + _id);
                break;
            case 1:
                onType1(trodFrom, trapObj);
                break;
            case 2:
                onType2(trodFrom, trapObj);
                break;
            case 3:
                onType3(trodFrom, trapObj);
                break;
            case 4:
                onType4(trodFrom, trapObj);
                break;
            case 5:
                onType5(trodFrom, trapObj);
                break;
            case 6:
                onType6(trodFrom, trapObj);
        }
    }

    public void onDetection(L1PcInstance caster, L1Object trapObj) {
        if (_isDetectionable) {
            sendEffect(trapObj);
        }
    }

    private void onType1(L1PcInstance trodFrom, L1Object trapObj) {
        try {
            if (_trap != 1) {
                return;
            }
            if (_base <= 0) {
                return;
            }
            sendEffect(trapObj);
            int dmg = _dice.roll(_diceCount) + _base;
            trodFrom.receiveDamage(trodFrom, dmg, false, true);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void onType2(L1PcInstance trodFrom, L1Object trapObj) {
        try {
            if (_trap != 2) {
                return;
            }
            if (_base <= 0) {
                return;
            }
            sendEffect(trapObj);
            int pt = _dice.roll(_diceCount) + _base;
            trodFrom.healHp(pt);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void onType3(L1PcInstance trodFrom, L1Object trapObj) {
        try {
            if (_trap != 3) {
                return;
            }
            if (_npcId <= 0) {
                return;
            }
            sendEffect(trapObj);
            for (int i = 0; i < _count; i++) {
                L1Location loc = trodFrom.getLocation().randomLocation(5, false);
                L1NpcInstance newNpc = L1SpawnUtil.spawn(_npcId, loc);
                newNpc.setLink(trodFrom);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void onType4(L1PcInstance trodFrom, L1Object trapObj) {
        try {
            if (_trap != 4) {
                return;
            }
            sendEffect(trapObj);
            switch (_poisonType) {
                case 1:
                    L1DamagePoison.doInfection(trodFrom, trodFrom, _time, _damage);
                    break;
                case 2:
                    L1SilencePoison.doInfection(trodFrom);
                    break;
                case 3:
                    L1ParalysisPoison.doInfection(trodFrom, _delay, _time);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void onType5(L1PcInstance trodFrom, L1Object trapObj) {
        try {
            if (_trap != 5) {
                return;
            }
            if (_skillId <= 0) {
                return;
            }
            sendEffect(trapObj);
            new L1SkillUse().handleCommands(trodFrom, _skillId, trodFrom.getId(), trodFrom.getX(), trodFrom.getY(), _skillTimeSeconds, 4);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void onType6(L1PcInstance trodFrom, L1Object trapObj) {
        try {
            if (_trap != 6) {
                return;
            }
            if (_loc == null) {
                return;
            }
            sendEffect(trapObj);
            L1Teleport.teleport(trodFrom, _loc.getX(), _loc.getY(), (short) _loc.getMapId(), 5, true, 2);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.templates.L1Trap JD-Core Version: 0.6.2
 */