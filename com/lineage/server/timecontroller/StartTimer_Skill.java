package com.lineage.server.timecontroller;

import com.lineage.server.timecontroller.skill.*;

public class StartTimer_Skill {
    public void start() {
        Skill_Awake_Timer awake_Timer = new Skill_Awake_Timer();
        awake_Timer.start();
        EffectFirewallTimer firewall = new EffectFirewallTimer();
        firewall.start();
        EffectCubeBurnTimer cubeBurn = new EffectCubeBurnTimer();
        cubeBurn.start();
        EffectCubeEruptionTimer cubeEruption = new EffectCubeEruptionTimer();
        cubeEruption.start();
        EffectCubeShockTimer cubeShock = new EffectCubeShockTimer();
        cubeShock.start();
        EffectCubeHarmonizeTimer cubeHarmonize = new EffectCubeHarmonizeTimer();
        cubeHarmonize.start();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.timecontroller.StartTimer_Skill JD-Core Version: 0.6.2
 */