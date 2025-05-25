package com.lineage.server.model;

import com.lineage.server.IdFactoryNpc;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_NPCPack;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

public class L1MobSkillUseSpawn {
    private static final Log _log = LogFactory.getLog(L1MobSkillUseSpawn.class);
    private Random _rnd = new Random();

    /**
     * NPC召喚技能
     *
     * @param attacker 執行的NPC
     * @param target   目標物件
     * @param summonId 召喚的NPC編號
     * @param count    數量
     */
    public void mobspawn(L1Character attacker, L1Character target, int summonId, int count) {
        for (int i = 0; i < count; i++) {
            mobspawn(attacker, target, summonId);
        }
    }

    public L1MonsterInstance mobspawnX(L1Character attacker, L1Character target, int summonId) {
        return mobspawn(attacker, target, summonId);
    }

    /**
     * NPC召喚技能
     *
     * @param attacker 執行的NPC
     * @param target   目標物件
     * @param summonId 召喚的NPC編號
     * @return
     */
    private L1MonsterInstance mobspawn(L1Character attacker, L1Character target, int summonId) {
        try {
            L1NpcInstance mob = NpcTable.get().newNpcInstance(summonId);
            if (mob == null) {
                _log.error("NPC召喚技能 目標NPCID設置異常 異常編號: " + summonId);
                return null;
            }
            mob.setId(IdFactoryNpc.get().nextId());
            L1Location loc = null;
            switch (mob.getNpcId()) {
                case 86121:
                case 86122:
                case 97221:// 奇怪的烏雲
                    loc = L1Location.randomLocation(attacker.getLocation(), 5, 15, false);
                    break;
                default:
                    loc = attacker.getLocation().randomLocation(6, false);
                    break;
            }
            int heading = _rnd.nextInt(8);
            mob.setX(loc.getX());
            mob.setY(loc.getY());
            mob.setHomeX(loc.getX());
            mob.setHomeY(loc.getY());
            short mapid = attacker.getMapId();
            mob.setMap(mapid);
            mob.setHeading(heading);
            mob.set_showId(attacker.get_showId());
            World.get().storeObject(mob);
            World.get().addVisibleObject(mob);
            L1Object object = World.get().findObject(mob.getId());
            L1MonsterInstance newnpc = (L1MonsterInstance) object;
            newnpc.set_storeDroped(true);
            int gfx = newnpc.getTempCharGfx();
            switch (gfx) {
                case 7548:
                case 7550:
                case 7552:
                case 7554:
                case 7585:
                case 7591:
                case 7840:
                case 8096:
                    newnpc.broadcastPacketAll(new S_NPCPack(newnpc));
                    newnpc.broadcastPacketAll(new S_DoActionGFX(newnpc.getId(), 11));
                    break;
                case 7539:
                case 7557:
                case 7558:
                case 7864:
                case 7869:
                case 7870:
                case 8036:
                case 8054:
                case 8055:
                    for (L1PcInstance _pc : World.get().getVisiblePlayer(newnpc, 50)) {
                        if (newnpc.getTempCharGfx() == 7539) {
                            _pc.sendPackets(new S_ServerMessage(1570));
                        } else if (newnpc.getTempCharGfx() == 7864) {
                            _pc.sendPackets(new S_ServerMessage(1657));
                        } else if (newnpc.getTempCharGfx() == 8036) {
                            _pc.sendPackets(new S_ServerMessage(1755));
                        }
                    }
                    newnpc.broadcastPacketAll(new S_NPCPack(newnpc));
                    newnpc.broadcastPacketAll(new S_DoActionGFX(newnpc.getId(), 11));
                    break;
                case 145:
                case 3957:
                case 3969:
                case 3989:
                case 3984:
                case 3547:
                case 3566:
                case 2158:
                case 7719:
                case 10071:
                case 10947:// 傳送旋風
                case 11465:
                case 11467:
                case 12467:
                    newnpc.broadcastPacketAll(new S_NPCPack(newnpc));
                    newnpc.broadcastPacketAll(new S_DoActionGFX(newnpc.getId(), 4));
                    break;
                case 30:
                    newnpc.broadcastPacketAll(new S_NPCPack(newnpc));
                    newnpc.broadcastPacketAll(new S_DoActionGFX(newnpc.getId(), 30));
                    break;
            }
            newnpc.onNpcAI();
            newnpc.startChat(0);
            if (gfx == 10947) {// 傳送旋風
                newnpc.set_spawnTime(15);
            } else {
                newnpc.set_spawnTime(60);
            }
            if (target != null) {
                switch (newnpc.getNpcId()) {
                    case 86121:// 新版吉爾塔斯-地面大火
                    case 86122:// 新版吉爾塔斯-地面小火
                    case 99083:// 巨蟻女皇召喚-傳送旋風
                    case 99084:// 沙漠地區-沙塵暴
                    case 99030:// 巨型骷髏召喚-傳送旋風
                    case 107037:// 傑羅斯召喚-鬼火
                    case 97221:// 奇怪的烏雲
                        break;
                    default:
                        newnpc.setLink(target);
                        break;
                }
            }
            if (newnpc != null) {
                return newnpc;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * NPC召喚技能
     *
     * @param attacker
     * @param target
     * @param summonId
     */
    public void mobspawnSrc(L1Character attacker, L1Character target, int summonId) {
        try {
            L1NpcInstance npc = NpcTable.get().newNpcInstance(summonId);
            npc.setId(IdFactoryNpc.get().nextId());
            npc.setMap(attacker.getMapId());
            npc.setX(attacker.getX());
            npc.setY(attacker.getY());
            npc.setHomeX(npc.getX());
            npc.setHomeY(npc.getY());
            npc.setHeading(attacker.getHeading());
            npc.set_showId(attacker.get_showId());
            World.get().storeObject(npc);
            World.get().addVisibleObject(npc);
            npc.turnOnOffLight();
            npc.startChat(0);
            npc.setLink(target);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.L1MobSkillUseSpawn JD-Core Version: 0.6.2
 */