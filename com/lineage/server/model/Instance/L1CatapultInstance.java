/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.lineage.server.model.Instance;

import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.NPCTalkDataTable;
import com.lineage.server.model.*;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import com.lineage.server.world.WorldWar;

import java.util.ArrayList;

public class L1CatapultInstance extends L1NpcInstance {
    private static final long serialVersionUID = 1L;
    private int _castle_id;

    public L1CatapultInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void onPerceive(L1PcInstance perceivedFrom) {
        if (perceivedFrom == null) {
            return;
        }
        perceivedFrom.addKnownObject(this);
        perceivedFrom.sendPackets(new S_NPCPack(this));
    }

    @Override
    public void onTalkAction(L1PcInstance pc) {
        if (pc == null) {
            return;
        }
        int objid = getId();
        L1NpcTalkData talking = NPCTalkDataTable.get().getTemplate(getNpcTemplate().get_npcId());
        String htmlid = null;
        int npcId = getNpcTemplate().get_npcId();
        if (npcId >= 90327 && npcId <= 90330) {
            _castle_id = L1CastleLocation.KENT_CASTLE_ID;
        } else if (npcId >= 90331 && npcId <= 90334) {
            _castle_id = L1CastleLocation.GIRAN_CASTLE_ID;
        } else if (npcId >= 90335 && npcId <= 90337) {
            _castle_id = L1CastleLocation.OT_CASTLE_ID;
        }
        boolean existDefenseClan = false;
        L1Clan clan = WorldClan.get().getClan(pc.getClanname());
        if (clan != null) {
            int clanCastleId = clan.getCastleId();
            if (clanCastleId == _castle_id) {
                existDefenseClan = true;
            }
        }
        boolean isProclamation = false;
        for (L1War war : WorldWar.get().getWarList()) {
            if (_castle_id == war.get_castleId()) {
                isProclamation = war.checkClanInWar(pc.getClanname());
                break;
            }
        }
        if (!pc.isGm()) {
            if (clan != null) {
                if (!ServerWarExecutor.get().isNowWar(_castle_id)) {
                    if (pc.isCrown() && pc.getId() == clan.getLeaderId()) {
                        // 使用投石器: 失敗(限攻城時間內使用)
                        pc.sendPackets(new S_ServerMessage(3683));
                        return;
                    } else {
                        // 只有血盟君主才可。
                        pc.sendPackets(new S_ServerMessage(2498));
                        return;
                    }
                } else {
                    if (isProclamation) {
                        if (pc.isCrown() && pc.getId() == clan.getLeaderId()) {
                            if (existDefenseClan) {
                                if (npcId == 90327 || npcId == 90328 || npcId == 90331 || npcId == 90332 || npcId == 90335 || npcId == 90336) {
                                    // 使用投石器: 失敗(宣戰的君主才能使用)
                                    pc.sendPackets(new S_ServerMessage(3681));
                                    return;
                                }
                            } else {
                                if (npcId == 90329 || npcId == 90330 || npcId == 90333 || npcId == 90334 || npcId == 90337) {
                                    // 使用投石器: 失敗(守城的君主才能使用)
                                    pc.sendPackets(new S_ServerMessage(3682));
                                    return;
                                }
                            }
                        } else {
                            if (npcId == 90327 || npcId == 90328 || npcId == 90331 || npcId == 90332 || npcId == 90335 || npcId == 90336) {
                                // 使用投石器: 失敗(宣戰的君主才能使用)
                                pc.sendPackets(new S_ServerMessage(3681));
                                return;
                            }
                            if (npcId == 90329 || npcId == 90330 || npcId == 90333 || npcId == 90334 || npcId == 90337) {
                                // 使用投石器: 失敗(守城的君主才能使用)
                                pc.sendPackets(new S_ServerMessage(3682));
                                return;
                            }
                        }
                    } else {
                        // 只有血盟君主才可。
                        pc.sendPackets(new S_ServerMessage(2498));
                        return;
                    }
                }
            } else {
                // 只有血盟君主才可。
                pc.sendPackets(new S_ServerMessage(2498));
                return;
            }
        }
        switch (npcId) {
            case 90327:
            case 90328:
                htmlid = "ckenta";
                break;
            case 90329:
            case 90330:
                htmlid = "ckentd";
                break;
            case 90331:
            case 90332:
                htmlid = "cgirana";
                break;
            case 90333:
            case 90334:
                htmlid = "cgirand";
                break;
            case 90335:
            case 90336:
                htmlid = "corca";
                break;
            case 90337:
                htmlid = "corcd";
                break;
            default:
                break;
        }
        if (htmlid != null) {
            pc.sendPackets(new S_NPCTalkReturn(objid, htmlid));
        } else {
            if (pc.getLawful() < -1000) {
                pc.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
            } else {
                pc.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
            }
        }
    }

    @Override
    public void onAction(L1PcInstance pc) {
        if (pc != null && !isDead()) {
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
    }

    @Override
    public void receiveDamage(L1Character attacker, int damage) {
        int npcId = getNpcTemplate().get_npcId();
        if (npcId >= 90327 && npcId <= 90330) {
            _castle_id = L1CastleLocation.KENT_CASTLE_ID;
        } else if (npcId >= 90331 && npcId <= 90334) {
            _castle_id = L1CastleLocation.GIRAN_CASTLE_ID;
        } else if (npcId >= 90335 && npcId <= 90337) {
            _castle_id = L1CastleLocation.OT_CASTLE_ID;
        }
        if (_castle_id > 0 && ServerWarExecutor.get().isNowWar(_castle_id)) {
            if (getCurrentHp() > 0 && !isDead()) {
                if (damage >= 0) {
                    if (!(attacker instanceof L1EffectInstance)) {
                        setHate(attacker, damage);
                    }
                }
                L1PcInstance pc = null;
                if (attacker instanceof L1PcInstance) {
                    pc = (L1PcInstance) attacker;
                } else if (attacker instanceof L1PetInstance) {
                    pc = (L1PcInstance) ((L1PetInstance) attacker).getMaster();
                } else if (attacker instanceof L1SummonInstance) {
                    pc = (L1PcInstance) ((L1SummonInstance) attacker).getMaster();
                }
                if (pc == null) {
                    return;
                }
                boolean existDefenseClan = false;
                for (L1Clan clan : WorldClan.get().getAllClans()) {
                    int clanCastleId = clan.getCastleId();
                    if (clanCastleId == _castle_id) {
                        existDefenseClan = true;
                        break;
                    }
                }
                boolean isProclamation = false;
                for (L1War war : WorldWar.get().getWarList()) {
                    if (_castle_id == war.get_castleId()) {
                        isProclamation = war.checkClanInWar(pc.getClanname());
                    }
                }
                if (existDefenseClan == true && isProclamation == false) {
                    return;
                }
                int newHp = getCurrentHp() - damage;
                if (newHp <= 0 && !isDead()) {
                    setCurrentHp(0);
                    setDead(true);
                    setStatus(ActionCodes.ACTION_Die);
                    death();
                }
                if (newHp > 0) {
                    setCurrentHp(newHp);
                }
            } else if (!isDead()) {
                setDead(true);
                setStatus(ActionCodes.ACTION_Die);
                death();
            }
        }
    }

    private void death() {
        try {
            setDeathProcessing(true);
            setCurrentHp(0);
            setDead(true);
            setStatus(ActionCodes.ACTION_Die);
            getMap().setPassable(getLocation(), true);
            broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_Die));
        } catch (Exception e) {
        }
    }

    @Override
    public void deleteMe() {
        _destroyed = true;
        if (getInventory() != null) {
            getInventory().clearItems();
        }
        World.get().removeVisibleObject(this);
        World.get().removeObject(this);
        ArrayList<L1PcInstance> list = null;
        list = World.get().getRecognizePlayer(this);
        for (L1PcInstance pc : list) {
            if (pc == null) {
                continue;
            }
            pc.removeKnownObject(this);
            pc.sendPackets(new S_RemoveObject(this));
        }
        removeAllKnownObjects();
    }
}
