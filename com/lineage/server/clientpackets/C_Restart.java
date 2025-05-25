package com.lineage.server.clientpackets;

import com.lineage.config.Configcamp_war;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.GetbackTable;
import com.lineage.server.model.Instance.L1EffectInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.*;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 要求死亡後重新開始
 *
 * @author daien
 */
public class C_Restart extends ClientBasePacket {
    @SuppressWarnings("unused")
    private static final Log _log = LogFactory.getLog(C_Restart.class);

    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            L1PcInstance pc = client.getActiveChar();
            if (pc != null) {
                int[] loc = new int[3];
                if (pc.getHellTime() > 0) {
                    loc = new int[3];
                    loc[0] = 32701;
                    loc[1] = 32777;
                    loc[2] = 666;
                } else if (pc.get_redbluejoin() > 0) {
                    if (pc.get_redbluejoin() == 11) {
                        loc = new int[3];
                        loc[0] = Configcamp_war.RedBlueRed_map1[0];
                        loc[1] = Configcamp_war.RedBlueRed_map1[1];
                        loc[2] = Configcamp_war.RedBlueRed_map1[2];
                    } else if (pc.get_redbluejoin() == 12) {
                        loc = new int[3];
                        loc[0] = Configcamp_war.RedBlueBlue_map1[0];
                        loc[1] = Configcamp_war.RedBlueBlue_map1[1];
                        loc[2] = Configcamp_war.RedBlueBlue_map1[2];
                    } else if (pc.get_redbluejoin() == 21) {
                        loc = new int[3];
                        loc[0] = Configcamp_war.RedBlueRed_map2[0];
                        loc[1] = Configcamp_war.RedBlueRed_map2[1];
                        loc[2] = Configcamp_war.RedBlueRed_map2[2];
                    } else if (pc.get_redbluejoin() == 22) {
                        loc = new int[3];
                        loc[0] = Configcamp_war.RedBlueBlue_map2[0];
                        loc[1] = Configcamp_war.RedBlueBlue_map2[1];
                        loc[2] = Configcamp_war.RedBlueBlue_map2[2];
                    }
                } else if (pc.isSiege) { // 底比斯大戰
                    switch (pc.getTeam()) {
                        case 0:
                            loc[0] = 32771;
                            loc[1] = 32815;
                            loc[2] = 10502;
                            break;
                        case 1:
                            loc[0] = 32691;
                            loc[1] = 32895;
                            loc[2] = 10502;
                            break;
                        case 2:
                            loc[0] = 32771;
                            loc[1] = 32975;
                            loc[2] = 10502;
                            break;
                    }
                } else {
                    // 返回村莊
                    loc = GetbackTable.GetBack_Location(pc, true);
                }
                // 刪除人物墓碑
                L1EffectInstance tomb = pc.get_tomb();
                if (tomb != null) {
                    tomb.broadcastPacketAll(new S_DoActionGFX(tomb.getId(), 8));
                    tomb.deleteMe();
                }
                pc.stopPcDeleteTimer();
                pc.removeAllKnownObjects();
                pc.broadcastPacketAll(new S_RemoveObject(pc));
                pc.setCurrentHp(pc.getLevel());
                pc.set_food(40);
                pc.setStatus(0);
                World.get().moveVisibleObject(pc, loc[2]);
                pc.setX(loc[0]);
                pc.setY(loc[1]);
                pc.setMap((short) loc[2]);
                pc.set_showId(-1);
                pc.sendPackets(new S_MapID(pc, pc.getMapId(), pc.getMap().isUnderwater()));
                pc.broadcastPacketAll(new S_OtherCharPacks(pc));
                pc.sendPackets(new S_OwnCharPack(pc));
                pc.sendPackets(new S_CharVisualUpdate(pc));
                pc.startHpRegeneration();
                pc.startMpRegeneration();
                pc.sendPackets(new S_Weather(World.get().getWeather()));
                pc.sendPackets(new S_PacketBox(S_PacketBox.UPDATE_ER, pc.getEr()));// 迴避率更新
                pc.sendPackets(new S_PacketBoxIcon1(true, pc.get_dodge()));// 閃避率更新
                if (pc.getHellTime() > 0) {// 有地獄處罰時間
                    pc.setHellTime(pc.getHellTime() - 1);// 死亡重登一次減少一秒
                    pc.beginHell(false);
                }
            }
        } catch (Exception localException) {
        } finally {
            over();
        }
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.clientpackets.C_Restart JD-Core Version: 0.6.2
 */