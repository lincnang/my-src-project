package com.lineage.server.model.Instance;

import com.lineage.config.ConfigAlt;
import com.lineage.server.datatables.NPCTalkDataTable;
import com.lineage.server.model.*;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.types.Point;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class L1GuardInstance extends L1NpcInstance {
    private static final long serialVersionUID = 1L;
    private static final Log _log = LogFactory.getLog(L1GuardInstance.class);

    public L1GuardInstance(L1Npc template) {
        super(template);
    }

    private static L1PcInstance searchTarget(L1GuardInstance npc) {
        L1PcInstance targetPlayer = null;
        for (L1PcInstance pc : World.get().getVisiblePlayer(npc)) {
            try {
                TimeUnit.MILLISECONDS.sleep(10L);
            } catch (InterruptedException e) {
                _log.error(e.getLocalizedMessage(), e);
            }
            if ((pc.getCurrentHp() > 0) && (!pc.isDead()) && (!pc.isGm()) && (!pc.isGhost())) {
                if (npc.get_showId() == pc.get_showId()) {
                    if (((!pc.isInvisble()) || (npc.getNpcTemplate().is_agrocoi())) && (pc.isWanted())) {
                        targetPlayer = pc;
                        break;
                    }
                }
            }
        }
        return targetPlayer;
    }

    public void searchTarget() {
        L1PcInstance targetPlayer = searchTarget(this);
        if (targetPlayer != null) {
            _hateList.add(targetPlayer, 0);
            _target = targetPlayer;
        }
    }

    public void setTarget(L1PcInstance targetPlayer) {
        if (targetPlayer != null) {
            _hateList.add(targetPlayer, 0);
            _target = targetPlayer;
        }
    }

    public boolean noTarget() {
        if (getLocation().getTileLineDistance(new Point(getHomeX(), getHomeY())) > 0) {
            if (_npcMove != null) {
                int dir = _npcMove.moveDirection(getHomeX(), getHomeY());
                if (dir != -1) {
                    _npcMove.setDirectionMove(dir);
                    setSleepTime(calcSleepTime(getPassispeed(), 0));
                } else {
                    teleport(getHomeX(), getHomeY(), 1);
                }
            }
        } else if (World.get().getRecognizePlayer(this).size() == 0) {
            return true;
        }
        return false;
    }

    public void onNpcAI() {
        if (isAiRunning()) {
            return;
        }
        setActived(false);
        startAI();
    }

    public void onAction(L1PcInstance pc) {
        try {
            if (!isDead()) {
                if (getCurrentHp() > 0) {
                    L1AttackMode attack = new L1AttackPc(pc, this);
                    if (attack.calcHit()) {
                        attack.calcDamage();
                    }
                    attack.action();
                    attack.commit();
                } else {
                    L1AttackMode attack = new L1AttackPc(pc, this);
                    attack.calcHit();
                    attack.action();
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void onTalkAction(L1PcInstance player) {
        int objid = getId();
        L1NpcTalkData talking = NPCTalkDataTable.get().getTemplate(getNpcTemplate().get_npcId());
        int npcid = getNpcTemplate().get_npcId();
        String htmlid = null;
        String[] htmldata = null;
        boolean hascastle;
        String clan_name = " ";
        String pri_name = " ";
        if (talking != null) {
            switch (npcid) {
                case 70549:
                case 70985:
                    hascastle = checkHasCastle(player, 1);
                    if (hascastle) {
                        htmlid = "gateokeeper";
                        htmldata = new String[]{player.getName()};
                    } else {
                        htmlid = "gatekeeperop";
                    }
                    break;
                case 70656:
                    hascastle = checkHasCastle(player, 1);
                    if (hascastle) {
                        htmlid = "gatekeeper";
                        htmldata = new String[]{player.getName()};
                    } else {
                        htmlid = "gatekeeperop";
                    }
                    break;
                case 70600:
                case 70986:
                    hascastle = checkHasCastle(player, 2);
                    if (hascastle) {
                        htmlid = "orckeeper";
                    } else {
                        htmlid = "orckeeperop";
                    }
                    break;
                case 70687:
                case 70987:
                    hascastle = checkHasCastle(player, 3);
                    if (hascastle) {
                        htmlid = "gateokeeper";
                        htmldata = new String[]{player.getName()};
                    } else {
                        htmlid = "gatekeeperop";
                    }
                    break;
                case 70778:
                    hascastle = checkHasCastle(player, 3);
                    if (hascastle) {
                        htmlid = "gatekeeper";
                        htmldata = new String[]{player.getName()};
                    } else {
                        htmlid = "gatekeeperop";
                    }
                    break;
                case 70800:
                case 70988:
                case 70989:
                case 70990:
                case 70991:
                    hascastle = checkHasCastle(player, 4);
                    if (hascastle) {
                        htmlid = "gateokeeper";
                        htmldata = new String[]{player.getName()};
                    } else {
                        htmlid = "gatekeeperop";
                    }
                    break;
                case 70817:
                    hascastle = checkHasCastle(player, 4);
                    if (hascastle) {
                        htmlid = "gatekeeper";
                        htmldata = new String[]{player.getName()};
                    } else {
                        htmlid = "gatekeeperop";
                    }
                    break;
                case 70862:
                case 70992:
                    hascastle = checkHasCastle(player, 5);
                    if (hascastle) {
                        htmlid = "gateokeeper";
                        htmldata = new String[]{player.getName()};
                    } else {
                        htmlid = "gatekeeperop";
                    }
                    break;
                case 70863:
                    hascastle = checkHasCastle(player, 5);
                    if (hascastle) {
                        htmlid = "gatekeeper";
                        htmldata = new String[]{player.getName()};
                    } else {
                        htmlid = "gatekeeperop";
                    }
                    break;
                case 70993:
                case 70994:
                    hascastle = checkHasCastle(player, 6);
                    if (hascastle) {
                        htmlid = "gateokeeper";
                        htmldata = new String[]{player.getName()};
                    } else {
                        htmlid = "gatekeeperop";
                    }
                    break;
                case 70995:
                    hascastle = checkHasCastle(player, 6);
                    if (hascastle) {
                        htmlid = "gatekeeper";
                        htmldata = new String[]{player.getName()};
                    } else {
                        htmlid = "gatekeeperop";
                    }
                    break;
                case 70996:
                    hascastle = checkHasCastle(player, 7);
                    if (hascastle) {
                        htmlid = "gatekeeper";
                        htmldata = new String[]{player.getName()};
                    } else {
                        htmlid = "gatekeeperop";
                    }
                    break;
                case 60514: {
                    Collection<?> allClans = WorldClan.get().getAllClans();
                    for (Object allClan : allClans) {
                        L1Clan clan = (L1Clan) allClan;
                        if (clan.getCastleId() == 1) {
                            clan_name = clan.getClanName();
                            pri_name = clan.getLeaderName();
                            break;
                        }
                    }
                    htmlid = "ktguard6";
                    htmldata = new String[]{getName(), clan_name, pri_name};
                    break;
                }
                case 60560: {
                    Collection<?> allClans = WorldClan.get().getAllClans();
                    for (Object allClan : allClans) {
                        L1Clan clan = (L1Clan) allClan;
                        if (clan.getCastleId() == 2) {
                            clan_name = clan.getClanName();
                            pri_name = clan.getLeaderName();
                            break;
                        }
                    }
                    htmlid = "orcguard6";
                    htmldata = new String[]{getName(), clan_name, pri_name};
                    break;
                }
                case 60552: {
                    Collection<?> allClans = WorldClan.get().getAllClans();
                    for (Object allClan : allClans) {
                        L1Clan clan = (L1Clan) allClan;
                        if (clan.getCastleId() == 3) {
                            clan_name = clan.getClanName();
                            pri_name = clan.getLeaderName();
                            break;
                        }
                    }
                    htmlid = "wdguard6";
                    htmldata = new String[]{getName(), clan_name, pri_name};
                    break;
                }
                case 60524:
                case 60525:
                case 60529: {
                    Collection<?> allClans = WorldClan.get().getAllClans();
                    for (Object allClan : allClans) {
                        L1Clan clan = (L1Clan) allClan;
                        if (clan.getCastleId() == 4) {
                            clan_name = clan.getClanName();
                            pri_name = clan.getLeaderName();
                            break;
                        }
                    }
                    htmlid = "grguard6";
                    htmldata = new String[]{getName(), clan_name, pri_name};
                    break;
                }
                case 70857: {
                    Collection<?> allClans = WorldClan.get().getAllClans();
                    for (Object allClan : allClans) {
                        L1Clan clan = (L1Clan) allClan;
                        if (clan.getCastleId() == 5) {
                            clan_name = clan.getClanName();
                            pri_name = clan.getLeaderName();
                            break;
                        }
                    }
                    htmlid = "heguard6";
                    htmldata = new String[]{getName(), clan_name, pri_name};
                    break;
                }
                case 60530:
                case 60531: {
                    Collection<?> allClans = WorldClan.get().getAllClans();
                    for (Object allClan : allClans) {
                        L1Clan clan = (L1Clan) allClan;
                        if (clan.getCastleId() == 6) {
                            clan_name = clan.getClanName();
                            pri_name = clan.getLeaderName();
                            break;
                        }
                    }
                    htmlid = "dcguard6";
                    htmldata = new String[]{getName(), clan_name, pri_name};
                    break;
                }
                case 60533:
                case 60534: {
                    Collection<?> allClans = WorldClan.get().getAllClans();
                    for (Object allClan : allClans) {
                        L1Clan clan = (L1Clan) allClan;
                        if (clan.getCastleId() == 7) {
                            clan_name = clan.getClanName();
                            pri_name = clan.getLeaderName();
                            break;
                        }
                    }
                    htmlid = "adguard6";
                    htmldata = new String[]{getName(), clan_name, pri_name};
                    break;
                }
                case 81156: {
                    Collection<?> allClans = WorldClan.get().getAllClans();
                    for (Object allClan : allClans) {
                        L1Clan clan = (L1Clan) allClan;
                        if (clan.getCastleId() == 8) {
                            clan_name = clan.getClanName();
                            pri_name = clan.getLeaderName();
                            break;
                        }
                    }
                    htmlid = "ktguard6";
                    htmldata = new String[]{getName(), clan_name, pri_name};
                    break;
                }
            }
            if (htmlid != null) {
                if (htmldata != null) {
                    player.sendPackets(new S_NPCTalkReturn(objid, htmlid, htmldata));
                } else {
                    player.sendPackets(new S_NPCTalkReturn(objid, htmlid));
                }
            } else if (player.getLawful() < -1000) {
                player.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
            } else {
                player.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
            }
        }
    }

    public void onFinalAction() {
    }

    public void doFinalAction() {
    }

    public void receiveDamage(L1Character attacker, int damage) {
        ISASCAPE = false;
        if ((getCurrentHp() > 0) && (!isDead())) {
            if (damage >= 0) {
                if (!(attacker instanceof L1EffectInstance)) {
                    if ((attacker instanceof L1IllusoryInstance)) {
                        L1IllusoryInstance ill = (L1IllusoryInstance) attacker;
                        attacker = ill.getMaster();
                        setHate(attacker, damage);
                    } else {
                        setHate(attacker, damage);
                    }
                }
            }
            if (damage > 0) {
                removeSkillEffect(66);
                removeSkillEffect(212);
            }
            onNpcAI();
            if (((attacker instanceof L1PcInstance)) && (damage > 0)) {
                L1PcInstance pc = (L1PcInstance) attacker;
                pc.setPetTarget(this);
            }
            int newHp = getCurrentHp() - damage;
            if ((newHp <= 0) && (!isDead())) {
                setCurrentHpDirect(0);
                setDead(true);
                setStatus(8);
                Death death = new Death(attacker);
                GeneralThreadPool.get().execute(death);
            }
            if (newHp > 0) {
                setCurrentHp(newHp);
            }
        } else if ((getCurrentHp() != 0) || (isDead())) {
            if (!isDead()) {
                setDead(true);
                setStatus(8);
                Death death = new Death(attacker);
                GeneralThreadPool.get().execute(death);
            }
        }
    }

    public void setCurrentHp(int i) {
        int currentHp = Math.min(i, getMaxHp());
        if (getCurrentHp() == currentHp) {
            return;
        }
        setCurrentHpDirect(currentHp);
    }

    private boolean checkHasCastle(L1PcInstance pc, int castleId) {
        boolean isExistDefenseClan = false;
        Collection<?> allClans = WorldClan.get().getAllClans();
        for (Object allClan : allClans) {
            L1Clan clan = (L1Clan) allClan;
            if (castleId == clan.getCastleId()) {
                isExistDefenseClan = true;
                break;
            }
        }
        if (!isExistDefenseClan) {
            return true;
        }
        if (pc.getClanid() != 0) {
            L1Clan clan = WorldClan.get().getClan(pc.getClanname());
            return (clan != null) && (clan.getCastleId() == castleId);
        }
        return false;
    }

    class Death implements Runnable {
        L1Character _lastAttacker;

        public Death(L1Character lastAttacker) {
            _lastAttacker = lastAttacker;
        }

        public void run() {
            setDeathProcessing(true);
            setCurrentHpDirect(0);
            setDead(true);
            setStatus(8);
            getMap().setPassable(getLocation(), true);
            broadcastPacketAll(new S_DoActionGFX(getId(), 8));
            startChat(1);
            setDeathProcessing(false);
            allTargetClear();
            startDeleteTimer(ConfigAlt.NPC_DELETION_TIME);
        }
    }
}
