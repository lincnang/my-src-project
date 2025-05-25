package com.lineage.server.timecontroller;

import com.lineage.server.timecontroller.pet.*;

import java.util.concurrent.TimeUnit;

public class StartTimer_Pet {
    public void start() throws InterruptedException {
        PetHprTimer petHprTimer = new PetHprTimer();
        petHprTimer.start();
        TimeUnit.MILLISECONDS.sleep(50L);
        PetMprTimer petMprTimer = new PetMprTimer();
        petMprTimer.start();
        TimeUnit.MILLISECONDS.sleep(50L);
        SummonHprTimer summonHprTimer = new SummonHprTimer();
        summonHprTimer.start();
        TimeUnit.MILLISECONDS.sleep(50L);
        SummonMprTimer summonMprTimer = new SummonMprTimer();
        summonMprTimer.start();
        TimeUnit.MILLISECONDS.sleep(50L);
        SummonTimer summon_Timer = new SummonTimer();
        summon_Timer.start();
        TimeUnit.MILLISECONDS.sleep(50L);
        DollTimer dollTimer = new DollTimer();
        dollTimer.start();
        DollHprTimer dollHpTimer = new DollHprTimer();
        dollHpTimer.start();
        DollMprTimer dollMpTimer = new DollMprTimer();
        dollMpTimer.start();
        DollGetTimer dollGetTimer = new DollGetTimer();
        dollGetTimer.start();
        DollAidTimer dollAidTimer = new DollAidTimer();
        dollAidTimer.start();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.StartTimer_Pet JD-Core Version: 0.6.2
 */