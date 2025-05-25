package com.lineage.server.timecontroller.pc;

import com.lineage.config.ConfigOtherSet2;
import com.lineage.server.datatables.lock.UpdateLocReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_Teleport;
import com.lineage.server.thread.PcOtherThreadPool;
import com.lineage.server.world.World;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;

public class UnfreezingTimer extends TimerTask {
    public void start() {
        int timeMillis = 1000;
        PcOtherThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
    }

    @Override
    public void run() {
        Collection<L1PcInstance> all = World.get().getAllPlayers();
        if (all.isEmpty()) {
            return;
        }
        for (L1PcInstance tgpc : all) {
            if (ConfigOtherSet2.APPRENTICE_SWITCH) {
                tgpc.checkEffect();
            }
            if (tgpc.get_unfreezingTime() != 0) {
                int time = tgpc.get_unfreezingTime() - 1;
                tgpc.set_unfreezingTime(time);
                if (time <= 0) {
                    UpdateLocReading.get().setPcLoc(tgpc.getAccountName());
                    tgpc.sendPackets(new S_ServerMessage("\\fR帳號內其他人物傳送回指定位置！"));
                    L1Teleport.teleport(tgpc, 32781, 32856, (short) 340, 5, true);
                } else {
                    tgpc.sendPackets(new S_ServerMessage("\\fR" + time + "秒後傳送回指定位置！"));
                }
            }
            if (tgpc.get_misslocTime() != 0) {
                int time = tgpc.get_misslocTime() - 1;
                tgpc.set_misslocTime(time);
                if (time <= 0) {
                    // 原地順移並更新Client端畫面
                    tgpc.setTeleportX(tgpc.getX());
                    tgpc.setTeleportY(tgpc.getY());
                    tgpc.setTeleportMapId(tgpc.getMapId());
                    tgpc.setTeleportHeading(5);
                    tgpc.sendPacketsAll(new S_SkillSound(tgpc.getId(), 169));
                    tgpc.sendPackets(new S_Teleport(tgpc));
                    tgpc.sendPackets(new S_ServerMessage("\\fR解除人物錯位！"));
                } else {
                    tgpc.sendPackets(new S_ServerMessage("\\fR" + time + "秒後解除人物錯位！"));
                }
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.pc.UnfreezingTimer JD-Core Version: 0.6.2
 */