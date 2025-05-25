package com.lineage.server.model;

import com.lineage.server.IdFactoryNpc;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCPack;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1WarSpawn {
    private static final Log _log = LogFactory.getLog(L1WarSpawn.class);
    private static L1WarSpawn _instance;

    public static L1WarSpawn getInstance() {
        if (_instance == null) {
            _instance = new L1WarSpawn();
        }
        return _instance;
    }

    public void SpawnCatapult(int castleId) {
        if (castleId == L1CastleLocation.KENT_CASTLE_ID) {
            spawnCatapultK();
        } else if (castleId == L1CastleLocation.GIRAN_CASTLE_ID) {
            spawnCatapultG();
        } else if (castleId == L1CastleLocation.OT_CASTLE_ID) {
            spawnCatapultO();
        }
    }

    public void spawnTower(int castleId) {
        int npcId = 81111;
        if (castleId == 7) {
            npcId = 81189;
        }
        L1Npc l1npc = NpcTable.get().getTemplate(npcId);
        int[] loc = new int[3];
        loc = L1CastleLocation.getTowerLoc(castleId);
        SpawnWarObject(l1npc, loc[0], loc[1], (short) loc[2]);
        if (castleId == 7) {
            spawnSubTower();
        }
    }

    private void spawnSubTower() {
        int[] loc = new int[3];
        for (int i = 1; i <= 4; i++) {
            L1Npc l1npc = NpcTable.get().getTemplate(81189 + i);
            loc = L1CastleLocation.getSubTowerLoc(i);
            SpawnWarObject(l1npc, loc[0], loc[1], (short) loc[2]);
        }
    }

    public void SpawnCrown(int castleId) {
        L1Npc l1npc = NpcTable.get().getTemplate(81125);
        int[] loc = new int[3];
        loc = L1CastleLocation.getTowerLoc(castleId);
        SpawnWarObject(l1npc, loc[0], loc[1], (short) loc[2]);
    }

    public void SpawnFlag(int castleId) {
        L1Npc l1npc = NpcTable.get().getTemplate(81122);
        int[] loc = new int[5];
        loc = L1CastleLocation.getWarArea(castleId);
        int x = 0;
        int y = 0;
        int locx1 = loc[0];
        int locx2 = loc[1];
        int locy1 = loc[2];
        int locy2 = loc[3];
        short mapid = (short) loc[4];
        x = locx1;
        for (y = locy1; x <= locx2; x += 8) {
            SpawnWarObject(l1npc, x, y, mapid);
        }
        x = locx2;
        for (y = locy1; y <= locy2; y += 8) {
            SpawnWarObject(l1npc, x, y, mapid);
        }
        x = locx2;
        for (y = locy2; x >= locx1; x -= 8) {
            SpawnWarObject(l1npc, x, y, mapid);
        }
        x = locx1;
        for (y = locy2; y >= locy1; y -= 8) {
            SpawnWarObject(l1npc, x, y, mapid);
        }
        switch (castleId) {
            case 2:
                break;
            case 1:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
        }
    }

    private void spawnCatapultG() {
        L1Npc l1npc;
        int[] loc = new int[3];
        for (int i = 1; i <= 4; i++) {
            l1npc = NpcTable.get().getTemplate(90330 + i);
            loc = L1CastleLocation.getCatapultGLoc(i);
            SpawnWarObject(l1npc, loc[0], loc[1], (short) (loc[2]));
        }
    }

    private void spawnCatapultK() {
        L1Npc l1npc;
        int[] loc = new int[3];
        for (int i = 1; i <= 4; i++) {
            l1npc = NpcTable.get().getTemplate(90326 + i);
            loc = L1CastleLocation.getCatapultKLoc(i);
            SpawnWarObject(l1npc, loc[0], loc[1], (short) (loc[2]));
        }
    }

    private void spawnCatapultO() {
        L1Npc l1npc;
        int[] loc = new int[3];
        for (int i = 1; i <= 3; i++) {
            l1npc = NpcTable.get().getTemplate(90334 + i);
            loc = L1CastleLocation.getCatapultOLoc(i);
            SpawnWarObject(l1npc, loc[0], loc[1], (short) (loc[2]));
        }
    }

    private void SpawnWarObject(L1Npc l1npc, int locx, int locy, short mapid) {
        try {
            if (l1npc != null) {
                L1NpcInstance npc = NpcTable.get().newNpcInstance(l1npc);
                npc.setId(IdFactoryNpc.get().nextId());
                npc.setX(locx);
                npc.setY(locy);
                npc.setHomeX(locx);
                npc.setHomeY(locy);
                npc.setHeading(0);
                npc.setMap(mapid);
                World.get().storeObject(npc);
                World.get().addVisibleObject(npc);
                for (L1PcInstance pc : World.get().getAllPlayers()) {
                    npc.addKnownObject(pc);
                    pc.addKnownObject(npc);
                    pc.sendPacketsAll(new S_NPCPack(npc));
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 召喚黑騎士團 (守城)
     */
    public void BlackKnight(int castleId, L1Clan KnightClan) {
        final L1Npc l1npc1 = NpcTable.get().getTemplate(190449);
        final L1Npc l1npc2 = NpcTable.get().getTemplate(190450);
        final L1Npc l1npc3 = NpcTable.get().getTemplate(190451);
        final L1Npc l1npc4 = NpcTable.get().getTemplate(190452);
        //final L1Npc l1npc5 = NpcTable.get().getTemplate(190453);
        //final L1Npc l1npc6 = NpcTable.get().getTemplate(190454);
        final L1Npc l1npc7 = NpcTable.get().getTemplate(190455);
        final L1Npc l1npc8 = NpcTable.get().getTemplate(190456);
        int[] loc = new int[5];
        loc = L1CastleLocation.getWarArea(castleId);
        int x = 0;
        int y = 0;
        final short mapid = (short) loc[4];
        int KnightClanId = KnightClan.getClanId();
        if (castleId == 1) { // 肯特城
            for (x = 33112; x <= 33115; x++) {
                for (y = 32768; y <= 32772; y++) {
                    SpawnWarKnight(l1npc1, x, y, mapid, KnightClanId);
                }
            }
            for (x = 33116; x <= 33118; x++) {
                for (y = 32763; y <= 32766; y++) {
                    SpawnWarKnight(l1npc2, x, y, mapid, KnightClanId);
                }
            }
            for (x = 33116; x <= 33118; x++) {
                for (y = 32774; y <= 32777; y++) {
                    SpawnWarKnight(l1npc2, x, y, mapid, KnightClanId);
                }
            }
        } else if (castleId == 2) { // 妖魔城
            for (x = 32792; x <= 32797; x++) {
                for (y = 32317; y <= 32318; y++) {
                    SpawnWarKnight(l1npc3, x, y, mapid, KnightClanId);
                }
            }
            for (x = 32792; x <= 32797; x++) {
                for (y = 32308; y <= 32309; y++) {
                    SpawnWarKnight(l1npc4, x, y, mapid, KnightClanId);
                }
            }
        } else if (castleId == 4) { // 奇岩城
            for (x = 33629; x <= 33634; x++) {
                for (y = 32732; y <= 32733; y++) {
                    SpawnWarKnight(l1npc7, x, y, mapid, KnightClanId);
                }
            }
            for (x = 33629; x <= 33636; x++) {
                for (y = 32700; y <= 32701; y++) {
                    SpawnWarKnight(l1npc7, x, y, mapid, KnightClanId);
                }
            }
            for (x = 33623; x <= 33626; x++) {
                for (y = 32727; y <= 32729; y++) {
                    SpawnWarKnight(l1npc8, x, y, mapid, KnightClanId);
                }
            }
            for (x = 33637; x <= 33640; x++) {
                for (y = 32727; y <= 32729; y++) {
                    SpawnWarKnight(l1npc8, x, y, mapid, KnightClanId);
                }
            }
        }
    }

    /**
     * 召喚紅騎士團(攻城)
     */
    public void RedKnight(int castleId, L1Clan KnightClan) {
        final L1Npc l1npc9 = NpcTable.get().getTemplate(190355);
        final L1Npc l1npc10 = NpcTable.get().getTemplate(190361);
        final L1Npc l1npc11 = NpcTable.get().getTemplate(190362);
        final L1Npc l1npc12 = NpcTable.get().getTemplate(190363);
        final L1Npc l1npc13 = NpcTable.get().getTemplate(190364);
        int[] loc = new int[5];
        loc = L1CastleLocation.getWarArea(castleId);
        int x = 0;
        int y = 0;
        final short mapid = (short) loc[4];
        int KnightClanId = KnightClan.getClanId();
        if (castleId == 1) { // 肯特城
            for (x = 33099; x == 33099; x++) {
                for (y = 32770; y == 32770; y++) {
                    SpawnWarKnight(l1npc9, x, y, mapid, KnightClanId);
                }
            }
            for (x = 33092; x <= 33096; x++) {
                for (y = 32768; y == 32768; y++) {
                    SpawnWarKnight(l1npc10, x, y, mapid, KnightClanId);
                }
            }
            for (x = 33092; x <= 33096; x++) {
                for (y = 32769; y == 32769; y++) {
                    SpawnWarKnight(l1npc11, x, y, mapid, KnightClanId);
                }
            }
            for (x = 33092; x <= 33096; x++) {
                for (y = 32770; y == 32770; y++) {
                    SpawnWarKnight(l1npc12, x, y, mapid, KnightClanId);
                }
            }
            for (x = 33092; x <= 33096; x++) {
                for (y = 32771; y == 32771; y++) {
                    SpawnWarKnight(l1npc13, x, y, mapid, KnightClanId);
                }
            }
        } else if (castleId == 2) { // 妖魔城
            for (x = 32794; x == 32794; x++) {
                for (y = 32329; y == 32329; y++) {
                    SpawnWarKnight(l1npc9, x, y, mapid, KnightClanId);
                }
            }
            for (y = 32332; y <= 32336; y++) {
                for (x = 32791; x == 32791; x++) {
                    SpawnWarKnight(l1npc10, x, y, mapid, KnightClanId);
                }
            }
            for (y = 32332; y <= 32336; y++) {
                for (x = 32792; x == 32792; x++) {
                    SpawnWarKnight(l1npc11, x, y, mapid, KnightClanId);
                }
            }
            for (y = 32332; y <= 32336; y++) {
                for (x = 32793; x == 32793; x++) {
                    SpawnWarKnight(l1npc12, x, y, mapid, KnightClanId);
                }
            }
            for (y = 32332; y <= 32336; y++) {
                for (x = 32794; x == 32794; x++) {
                    SpawnWarKnight(l1npc13, x, y, mapid, KnightClanId);
                }
            }
        } else if (castleId == 4) { // 奇岩城
            for (y = 32748; y == 32748; y++) {
                for (x = 33630; x == 33630; x++) {
                    SpawnWarKnight(l1npc9, x, y, mapid, KnightClanId);
                }
            }
            for (y = 32748; y <= 32752; y++) {
                for (x = 33628; x == 33628; x++) {
                    SpawnWarKnight(l1npc10, x, y, mapid, KnightClanId);
                }
            }
            for (y = 32748; y <= 32752; y++) {
                for (x = 33629; x == 33629; x++) {
                    SpawnWarKnight(l1npc11, x, y, mapid, KnightClanId);
                }
            }
            for (y = 32748; y <= 32752; y++) {
                for (x = 33630; x == 33630; x++) {
                    SpawnWarKnight(l1npc12, x, y, mapid, KnightClanId);
                }
            }
            for (y = 32748; y <= 32752; y++) {
                for (x = 33631; x == 33631; x++) {
                    SpawnWarKnight(l1npc13, x, y, mapid, KnightClanId);
                }
            }
        }
    }

    /**
     * 召喚騎士團
     */
    private void SpawnWarKnight(L1Npc l1npc, int locx, int locy, short mapid, int clanid) {
        try {
            if (l1npc != null) {
                L1NpcInstance npc = NpcTable.get().newNpcInstance(l1npc);
                npc.setId(IdFactoryNpc.get().nextId());
                npc.setX(locx);
                npc.setY(locy);
                npc.setHomeX(locx);
                npc.setHomeY(locy);
                npc.setHeading(0);
                npc.setMap(mapid);
                if (clanid != 0) {
                    npc.setClanid(clanid);
                }
                World.get().storeObject(npc);
                World.get().addVisibleObject(npc);
                //npc.onNpcAI();
                npc.turnOnOffLight();
				/*for (L1PcInstance pc : World.get().getAllPlayers()) {
					npc.addKnownObject(pc);
					pc.addKnownObject(npc);
					pc.sendPacketsAll(new S_NPCPack(npc));
					if (npc.getNpcId() >= 190449 && npc.getNpcId() <= 190456) {
						npc.broadcastPacketAll(new S_WarIcon(npc.getId(), 2));
					} else if (npc.getNpcId() >= 190355 && npc.getNpcId() <= 190364) {
						npc.broadcastPacketAll(new S_WarIcon(npc.getId(), 1));
					}
				}*/
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
