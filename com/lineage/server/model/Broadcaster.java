package com.lineage.server.model;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.world.World;

import java.util.ArrayList;

public class Broadcaster {
    /**
     * キャラクターの可視範圍にあるプレーヤーでは、パケットを送信する。
     *
     * @param packet 送信するパケットを示すServerBasePacketオブジェクト。
     */
    public static void broadcastPacket(L1Character cha, ServerBasePacket packet) {
        ArrayList<L1PcInstance> list = null;
        list = World.get().getVisiblePlayer(cha);
        for (L1PcInstance pc : list) {
            pc.sendPackets(packet);
        }
    }

    public static void broadcastPacket(L1Character cha, ServerBasePacket packet, boolean clear) {
        try {
            if (cha == null) {
                return;
            }
            for (L1PcInstance pc : World.get().getVisiblePlayer(cha)) {
                pc.sendPackets(packet);
            }
            if (clear) {
                packet.clear();
                packet = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * キャラクターの可視範圍にあるプレーヤーでは、パケットを送信する。ただし、ターゲットの畫面には送信しない。
     *
     * @param packet 送信するパケットを示すServerBasePacketオブジェクト。
     */
    public static void broadcastPacketExceptTargetSight(L1Character cha, ServerBasePacket packet, L1Character target) {
        ArrayList<L1PcInstance> list = null;
        list = World.get().getVisiblePlayerExceptTargetSight(cha, target);
        for (L1PcInstance pc : list) {
            pc.sendPackets(packet);
        }
    }

    /**
     * キャラクターの50マス以內にいるプレイヤーに、パケットを送信する。
     *
     * @param packet 送信するパケットを示すServerBasePacketオブジェクト。
     */
    public static void wideBroadcastPacket(L1Character cha, ServerBasePacket packet) {
        ArrayList<L1PcInstance> list = null;
        list = World.get().getVisiblePlayer(cha, 50);
        for (L1PcInstance pc : list) {
            pc.sendPackets(packet);
        }
    }
}
