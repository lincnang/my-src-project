package com.lineage.server.timecontroller.pc;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.thread.GeneralThreadPool;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * VIP定時特效更新處理 時間軸
 *
 * @author juonena
 */
public class VIPGfxTimer extends TimerTask {
    // 角色清單
    private static final List<L1PcInstance> _vipList = new ArrayList<>();
    private int _time = 0;
    private ScheduledFuture<?> _timer;

    /**
     * 判斷
     *
     * @return true:執行 false:不執行
     */
    private static boolean check(L1PcInstance tgpc) {
        try {
            if (tgpc == null) {
                removeMember(tgpc);
                return false;
            }
            if (tgpc.getOnlineStatus() == 0) {
                removeMember(tgpc);
                return false;
            }
            if (tgpc.getNetConnection() == null) {
                removeMember(tgpc);
                return false;
            }
            if (tgpc.isTeleport()) {
                return false;
            }
        } catch (final Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 加入清單
     *
     */
    public static void addMember(final L1PcInstance pc) {
        if ((pc == null) || _vipList.contains(pc)) {
            return;
        }
        _vipList.add(pc);
    }

    /**
     * 移出清單
     *
     */
    public static void removeMember(final L1PcInstance pc) {
        if ((pc == null) || !_vipList.contains(pc)) {
            return;
        }
        _vipList.remove(pc);
    }

    public void start() {
        final int timeMillis = 1000; // 1秒
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
    }

    @Override
    public void run() {
        try {
            _time++;
            // 不包含元素
            if (_vipList.isEmpty()) {
                return;
            }
            for (final L1PcInstance tgpc : _vipList) {
                if (check(tgpc)) {
                    int add_gif = tgpc.get_gfx();
                    int add_gif_time = tgpc.get_time();
                    if (add_gif != 0 && add_gif_time != 0) {
                        if (_time % add_gif_time == 0) {
                            tgpc.sendPacketsArmorYN(new S_SkillSound(tgpc.getId(), add_gif));
                            TimeUnit.MILLISECONDS.sleep(1);
                        }
                    }
                }
            }
        } catch (final Exception e) {
            // _log.error("VIP定時特效處理時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            final VIPGfxTimer expTimer = new VIPGfxTimer();
            expTimer.start();
        }
    }
}
