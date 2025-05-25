package com.lineage.server.model;

import com.lineage.server.datatables.lock.CastleReading;
import com.lineage.server.serverpackets.S_War;
import com.lineage.server.templates.L1Castle;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import com.lineage.server.world.WorldWar;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class L1War {
    private static final Log _log = LogFactory.getLog(L1War.class);
    private final ConcurrentHashMap<String, L1Clan> _attackList;
    private String _attackClanName = null;
    private String _defenceClanName = null;
    private L1Clan _defenceClan = null;
    private int _warType = 0;
    private int _castleId = 0;
    private boolean _isWarTimerDelete = false;

    public L1War() {
        _attackList = new ConcurrentHashMap<>();
    }

    public int get_castleId() {
        return _castleId;
    }

    public String get_defenceClanName() {
        return _defenceClanName;
    }

    public String get_attackClanName() {
        return _attackClanName;
    }

    public boolean isWarTimerDelete() {
        return _isWarTimerDelete;
    }

    public void handleCommands(int war_type, String attack_clan_name, String defence_clan_name) {
        try {
            _attackList.clear();
            _warType = war_type;
            _defenceClanName = defence_clan_name;
            _defenceClan = WorldClan.get().getClan(_defenceClanName);
            addAttackClan(attack_clan_name);
            declareWar(attack_clan_name, defence_clan_name);
            switch (war_type) {
                case 1:
                    _castleId = getCastleId();
                    break;
                case 2:
                    SimWarTimer sim_war_timer = new SimWarTimer();
                    GeneralThreadPool.get().execute(sim_war_timer);
                    /** [原碼] 血盟對決系統 */
                case 3:
                    SimWarTimerPVP sim_war_timerPVP = new SimWarTimerPVP();
                    GeneralThreadPool.get().execute(sim_war_timerPVP);
            }
            WorldWar.get().addWar(this);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void requestCastleWar(int type, String attack_clan_name) {
        if (attack_clan_name == null) {
            return;
        }
        try {
            L1Clan attack_clan = WorldClan.get().getClan(attack_clan_name);
            if (attack_clan != null) {
                if (_defenceClan != null) {
                    switch (type) {
                        case 1:
                            World.get().broadcastPacketToAll(new S_War(1, attack_clan_name, _defenceClanName));
                            break;
                        case 2:
                            World.get().broadcastPacketToAll(new S_War(2, attack_clan_name, _defenceClanName));
                            World.get().broadcastPacketToAll(new S_War(4, _defenceClanName, attack_clan_name));
                            removeAttackClan(attack_clan_name);
                            break;
                        case 3:
                            World.get().broadcastPacketToAll(new S_War(3, attack_clan_name, _defenceClanName));
                            removeAttackClan(attack_clan_name);
                    }
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void requestSimWar(int type, String clan1_name, String clan2_name) {
        try {
            if ((clan1_name == null) || (clan2_name == null)) {
                return;
            }
            L1Clan clan1 = WorldClan.get().getClan(clan1_name);
            if (clan1 == null) {
                return;
            }
            L1Clan clan2 = WorldClan.get().getClan(clan2_name);
            if (clan2 == null) {
                return;
            }
            switch (type) {
                case 1:
                    clan1.sendPacketsAll(new S_War(1, clan1_name, clan2_name));
                    clan2.sendPacketsAll(new S_War(1, clan1_name, clan2_name));
                    break;
                case 2:
                    clan1.sendPacketsAll(new S_War(2, clan1_name, clan2_name));
                    clan2.sendPacketsAll(new S_War(4, clan2_name, clan1_name));
                    clan1.sendPacketsAll(new S_War(3, clan1_name, clan2_name));
                    clan2.sendPacketsAll(new S_War(3, clan1_name, clan2_name));
                    break;
                case 3:
                    clan1.sendPacketsAll(new S_War(3, clan1_name, clan2_name));
                    clan2.sendPacketsAll(new S_War(3, clan1_name, clan2_name));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            switch (type) {
                case 2:
                case 3:
                    WorldWar.get().removeWar(this);
                    _isWarTimerDelete = true;
                    delete();
            }
        }
    }

    public void winCastleWar(String clan_name) {
        try {
            WorldWar.get().removeWar(this);
            _isWarTimerDelete = true;
            Set<?> clanList = getAttackClanList();
            if (!clanList.isEmpty()) {
                World.get().broadcastPacketToAll(new S_War(4, clan_name, _defenceClanName));
                for (Object value : clanList) {
                    String enemy_clan_name = (String) value;
                    if (!clan_name.equalsIgnoreCase(enemy_clan_name)) {
                        World.get().broadcastPacketToAll(new S_War(4, _defenceClanName, enemy_clan_name));
                    }
                }
                for (Object o : clanList) {
                    String enemy_clan_name = (String) o;
                    World.get().broadcastPacketToAll(new S_War(3, _defenceClanName, enemy_clan_name));
                    _attackList.remove(enemy_clan_name);
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            delete();
        }
    }

    public void ceaseCastleWar() {
        try {
            WorldWar.get().removeWar(this);
            _isWarTimerDelete = true;
            Set<?> clanList = getAttackClanList();
            if (!clanList.isEmpty()) {
                for (Object value : clanList) {
                    String enemy_clan_name = (String) value;
                    World.get().broadcastPacketToAll(new S_War(4, _defenceClanName, enemy_clan_name));
                }
                for (Object o : clanList) {
                    String enemy_clan_name = (String) o;
                    World.get().broadcastPacketToAll(new S_War(3, _defenceClanName, enemy_clan_name));
                    _attackList.remove(enemy_clan_name);
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            delete();
        }
    }

    public void declareWar(String attack_clan_name, String defence_clan_name) {
        try {
            if (getWarType() == 1) {
                requestCastleWar(1, attack_clan_name);
            } else {
                _attackClanName = attack_clan_name;
                requestSimWar(1, attack_clan_name, defence_clan_name);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void surrenderWar(String clan1_name, String clan2_name) {
        try {
            if (getWarType() == 1) {
                requestCastleWar(2, clan1_name);
            } else {
                requestSimWar(2, clan1_name, clan2_name);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void ceaseWar(String clan1_name, String clan2_name) {
        try {
            if (getWarType() == 1) {
                requestCastleWar(3, clan1_name);
            } else {
                requestSimWar(3, clan1_name, clan2_name);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void ceaseWar() {
        try {
            ceaseWar(_attackClanName, _defenceClanName);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void winWar(String clan1_name, String clan2_name) {
        try {
            if (getWarType() == 1) {
                requestCastleWar(4, clan1_name);
            } else {
                requestSimWar(4, clan1_name, clan2_name);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public boolean checkClanInWar(String clan_name) {
        if (_isWarTimerDelete) {
            WorldWar.get().removeWar(this);
            delete();
            return false;
        }
        boolean ret = false;
        if (_defenceClanName.equalsIgnoreCase(clan_name)) {
            ret = true;
        } else {
            ret = checkAttackClan(clan_name);
        }
        return ret;
    }

    public boolean checkClanInSameWar(String player_clan_name, String target_clan_name) {
        boolean player_clan_flag = false;
        boolean target_clan_flag = false;
        if (_defenceClanName.equalsIgnoreCase(player_clan_name)) {
            player_clan_flag = true;
        } else {
            player_clan_flag = checkAttackClan(player_clan_name);
        }
        if (_defenceClanName.equalsIgnoreCase(target_clan_name)) {
            target_clan_flag = true;
        } else {
            target_clan_flag = checkAttackClan(target_clan_name);
        }
        if ((player_clan_flag) && (target_clan_flag)) {
            return true;
        }
        return false;
    }

    public String getEnemyClanName(String player_clan_name) {
        if (_defenceClanName.equalsIgnoreCase(player_clan_name)) {
            Set<?> clanList = getAttackClanList();
            if (!clanList.isEmpty()) {
                Iterator<?> iter = clanList.iterator();
                if (iter.hasNext()) {
                    return (String) iter.next();
                }
            }
        } else {
            return _defenceClanName;
        }
        return null;
    }

    public void delete() {
        try {
            _log.info(_defenceClanName + " 戰爭終止完成 剩餘戰爭清單數量:" + WorldWar.get().getWarList().size());
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            _attackList.clear();
            _attackClanName = null;
            _defenceClanName = null;
            _defenceClan = null;
            _warType = 0;
            _castleId = 0;
            _isWarTimerDelete = true;
        }
    }

    public int getWarType() {
        return _warType;
    }

    public void addAttackClan(String attack_clan_name) {
        L1Clan attack_clan = WorldClan.get().getClan(attack_clan_name);
        if (attack_clan != null) {
            _attackList.put(attack_clan_name.toLowerCase(), attack_clan);
        }
    }

    public void removeAttackClan(String attack_clan_name) {
        if (_attackList.get(attack_clan_name.toLowerCase()) != null) {
            _attackList.remove(attack_clan_name.toLowerCase());
        }
    }

    public boolean checkAttackClan(String attack_clan_name) {
        if (_attackList.get(attack_clan_name.toLowerCase()) != null) {
            return true;
        }
        return false;
    }

    public Set<String> getAttackClanList() {
        return _attackList.keySet();
    }

    public int getCastleId() {
        switch (_warType) {
            case 1:
                L1Clan clan = WorldClan.get().getClan(_defenceClanName);
                if (clan != null) {
                    int castle_id = clan.getCastleId();
                    return castle_id;
                }
                break;
            case 2:
        }
        return 0;
    }

    public L1Castle getCastle() {
        switch (_warType) {
            case 1:
                L1Clan clan = WorldClan.get().getClan(_defenceClanName);
                if (clan != null) {
                    int castle_id = clan.getCastleId();
                    L1Castle castle = CastleReading.get().getCastleTable(castle_id);
                    return castle;
                }
                break;
            case 2:
        }
        return null;
    }

    class SimWarTimer implements Runnable {
        public SimWarTimer() {
        }

        public void run() {
            for (int loop = 0; loop < 240; loop++) {
                try {
                    TimeUnit.MILLISECONDS.sleep(60000L);
                } catch (Exception exception) {
                    break;
                }
                if (_isWarTimerDelete) {
                    return;
                }
            }
            WorldWar.get().removeWar(L1War.this);
            ceaseWar(_attackClanName, _defenceClanName);
            delete();
        }
    }

    /**
     * [原碼] 血盟對決系統
     */
    class SimWarTimerPVP implements Runnable {
        public void run() {
            for (int loop = 0; loop < 20; loop++) {
                try {
                    TimeUnit.MILLISECONDS.sleep(60000L);
                } catch (Exception exception) {
                    break;
                }
                if (_isWarTimerDelete) {
                    return;
                }
            }
            ceaseWar(_attackClanName, _defenceClanName);
            delete();
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.L1War JD-Core Version: 0.6.2
 */