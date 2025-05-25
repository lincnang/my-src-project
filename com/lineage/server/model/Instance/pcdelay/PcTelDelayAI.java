package com.lineage.server.model.Instance.pcdelay;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_EffectLocation;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PcTelDelayAI {
    public static final int[] EFFECT_SPR = {169, 2235, 2236, 2281};
    public static final int[] EFFECT_TIME = {196, 308, 308, 784};
    //    public static final int[] EFFECT_TIME = {280, 440, 440, 1120}; // 实际是 x 0.7
    private static final Log _log = LogFactory.getLog(PcTelDelayAI.class);

    private PcTelDelayAI() {
    }

    /**
     * 發送傳送封包開始計時器工作 by 聖子默默<br>
     * 計時結束後定義新位置<br>
     * 傳送封包 169, 2235, 2236, 2281<br>
     * 使用 169 可造成角色已經不在原地的效果<br>
     * 對應的延遲時間 196, 308, 308, 784<br>
     *
     * @param pc        被執行對像
     * @param skillType 傳送封包特效類型<br>
     *                  0 = 169<br>
     *                  1 = 2235<br>
     *                  2 = 2236<br>
     *                  3 = 2281<br>
     * @param x         坐標 x
     * @param y         坐標 y
     * @param map       前往的地圖編號
     */
    public static void onPcAITeleportDelay(L1PcInstance pc, int skillType, int x, int y, int map) {
        if (pc.isParalyzedX()) {
            return;
        }
        if (skillType > EFFECT_SPR.length) {
            skillType = EFFECT_SPR.length;
        }
        try {
            S_SkillSound packet = new S_SkillSound(pc.getId(), EFFECT_SPR[skillType]);
            pc.sendPacketsAll(packet);
            PcTelAIDelayTimer telDelay = new PcTelAIDelayTimer(pc, x, y, map);
            GeneralThreadPool.get().schedule(telDelay, EFFECT_TIME[skillType]);
        } catch (Exception e) {
            _log.error("自動狩獵延時傳送冷卻時間軸啟動異常,不進行任何處理", e);
        }
    }

    private static class PcTelAIDelayTimer implements Runnable {
        private final L1PcInstance _pc;
        private final int _x;
        private final int _y;
        private final int _map;

        public PcTelAIDelayTimer(L1PcInstance pc, int x, int y, int map) {
            _pc = pc;
            _x = x;
            _y = y;
            _map = map;
        }

        @Override
        public void run() {
            stopDelayTimer();
        }

        public void stopDelayTimer() {
            try {
                teleport();
            } catch (Exception e) {
                _log.error("自動狩獵延時傳送冷卻時間軸結束異常,不進行任何處理", e);
            }
        }

        private void teleport() {
            if (_pc == null) {
                return;
            }
            if (_pc.isDead()) {
                return;
            }
            if (_pc.isGhost()) {
                return;
            }
            if (_pc.getOnlineStatus() == 0) {
                return;
            }
            _pc.flushedLocation(_x, _y, _map);
            // 測試效果以調整 落地動畫效果 若無此spr請註銷此 S_EffectLocation 語句
            _pc.sendPacketsAll(new S_EffectLocation(_pc.getLocation(), 12446));
            _pc.resetAI();
        }
    }
}
