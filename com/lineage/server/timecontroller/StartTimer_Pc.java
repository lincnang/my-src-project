package com.lineage.server.timecontroller;

import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigWeaponryEffects;
import com.lineage.server.timecontroller.pc.*;

import java.util.concurrent.TimeUnit;

public class StartTimer_Pc {
    public void start() throws InterruptedException {
        if (Config.AUTOSAVE_INTERVAL > 0) {
            PcAutoSaveTimer save = new PcAutoSaveTimer();
            save.start();
        }
        if (Config.AUTOSAVE_INTERVAL_INVENTORY > 0) {
            PcAutoSaveInventoryTimer save = new PcAutoSaveInventoryTimer();
            save.start();
        }
        UpdateObjectCTimer objectCTimer = new UpdateObjectCTimer();
        objectCTimer.start();
        UpdateObjectDKTimer objectDKTimer = new UpdateObjectDKTimer();
        objectDKTimer.start();
        UpdateObjectDTimer objectDTimer = new UpdateObjectDTimer();
        objectDTimer.start();
        UpdateObjectETimer objectETimer = new UpdateObjectETimer();
        objectETimer.start();
        UpdateObjectITimer objectITimer = new UpdateObjectITimer();
        objectITimer.start();
        UpdateObjectKTimer objectKTimer = new UpdateObjectKTimer();
        objectKTimer.start();
        UpdateObjectWTimer objectWTimer = new UpdateObjectWTimer();
        objectWTimer.start();
        UpdateObjectOTimer objectOTimer = new UpdateObjectOTimer();
        objectOTimer.start();
        TimeUnit.MILLISECONDS.sleep(50);// 延遲
        HprTimerCrown hprCrown = new HprTimerCrown();
        hprCrown.start();
        HprTimerDarkElf hprDarkElf = new HprTimerDarkElf();
        hprDarkElf.start();
        HprTimerDragonKnight hprDK = new HprTimerDragonKnight();
        hprDK.start();
        HprTimerElf hprElf = new HprTimerElf();
        hprElf.start();
        HprTimerIllusionist hprIllusionist = new HprTimerIllusionist();
        hprIllusionist.start();
        HprTimerKnight hprKnight = new HprTimerKnight();
        hprKnight.start();
        HprTimerWizard hprWizard = new HprTimerWizard();
        hprWizard.start();
        final HprTimerWarrior hprWarrior = new HprTimerWarrior();
        hprWarrior.start();
        TimeUnit.MILLISECONDS.sleep(50);// 延遲
        MprTimerCrown mprCrown = new MprTimerCrown();
        mprCrown.start();
        MprTimerDarkElf mprDarkElf = new MprTimerDarkElf();
        mprDarkElf.start();
        MprTimerDragonKnight mprDragonKnight = new MprTimerDragonKnight();
        mprDragonKnight.start();
        MprTimerElf mprElf = new MprTimerElf();
        mprElf.start();
        MprTimerIllusionist mprIllusionist = new MprTimerIllusionist();
        mprIllusionist.start();
        MprTimerKnight mprKnight = new MprTimerKnight();
        mprKnight.start();
        MprTimerWizard mprWizard = new MprTimerWizard();
        mprWizard.start();
        MprTimerWarrior mprWarrior = new MprTimerWarrior();
        mprWarrior.start();
        TimeUnit.MILLISECONDS.sleep(50);// 延遲
        PcDeleteTimer deleteTimer = new PcDeleteTimer();
        deleteTimer.start();
        PcGhostTimer ghostTimer = new PcGhostTimer();
        ghostTimer.start();
        UnfreezingTimer unfreezingTimer = new UnfreezingTimer();
        unfreezingTimer.start();
        PartyTimer partyTimer = new PartyTimer();
        partyTimer.start();
        /** 限時地圖時間軸 */
        MapTimerThread MapTimer = new MapTimerThread();
        MapTimer.start();
        TimeUnit.MILLISECONDS.sleep(50);// 延遲
        if (ConfigAlt.ALT_PUNISHMENT) {
            PcHellTimer hellTimer = new PcHellTimer();
            hellTimer.start();
        }
        // VIP定時特效計時時間軸
        final VIPGfxTimer vip = new VIPGfxTimer();
        vip.start();
        TimeUnit.MILLISECONDS.sleep(50);// 延遲
        // 城堡定時特效計時時間軸
        final VIPGfxTimer pc = new VIPGfxTimer();
        pc.start();
        TimeUnit.MILLISECONDS.sleep(50);// 延遲
        // 武器DIY特效
        final PcEffectTimer effectTimer = new PcEffectTimer();
        effectTimer.start();
        // PC 武器加成特效時間軸
        if (ConfigWeaponryEffects.WEAPON_EFFECT_DELAY > 0) {
            final PcWeaponTimer weaponTimer = new PcWeaponTimer();
            weaponTimer.start(ConfigWeaponryEffects.WEAPON_EFFECT_DELAY * 1000);
        }
    }
}
