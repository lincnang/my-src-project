package com.lineage.server.timecontroller.pc;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldDarkelf;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

/**
 * 暗隱術每秒MP消耗計時器 (專給黑妖)
 * 每秒檢查黑妖角色是否有暗隱術狀態，並消耗MP 1%，MP 不足時自動解除
 */
public class BlindHidingMpTimer extends TimerTask {

    private static final Log _log = LogFactory.getLog(BlindHidingMpTimer.class);
    private ScheduledFuture<?> _timer;

    public void start() {
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, 1000L, 1000L);
        _log.info("BlindHidingMpTimer 啟動完成，每秒檢查黑妖暗隱術消耗。");
    }

    @Override
    public void run() {
        try {
            Collection<L1PcInstance> allPc = WorldDarkelf.get().all();
            if (allPc.isEmpty()) {
                return;
            }
            for (L1PcInstance pc : allPc) {
                // 檢查是否有暗隱術效果
                if (pc.hasSkillEffect(L1SkillId.BLIND_HIDING)) {
                    int maxMp = pc.getMaxMp();
                    int cost = Math.max(1, maxMp / 100); // 至少扣1點
                    int nowMp = pc.getCurrentMp();
                    if (nowMp > cost) {
                        pc.setCurrentMp(nowMp - cost);
                    } else {
                        // MP 不足，移除技能效果，並通知玩家
                        pc.removeSkillEffect(L1SkillId.BLIND_HIDING);
                        pc.sendPackets(new S_ServerMessage("暗隱術因MP不足自動解除。"));
                    }
                }
            }
        } catch (Exception e) {
            _log.error("BlindHidingMpTimer 執行異常，自動重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            BlindHidingMpTimer timer = new BlindHidingMpTimer();
            timer.start();
        }
    }
}
