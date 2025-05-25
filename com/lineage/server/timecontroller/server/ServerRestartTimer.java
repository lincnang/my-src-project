package com.lineage.server.timecontroller.server;

import com.lineage.config.Config;
import com.lineage.config.ConfigOtherSet2;
import com.lineage.config.ConfigQuest;
import com.lineage.config.Configcamp_war;
import com.lineage.data.event.QuestMobSet;
import com.lineage.server.Shutdown;
import com.lineage.server.datatables.ServerQuestMobTable;
import com.lineage.server.datatables.lock.CharacterQuestReading;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.L1PcQuest;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

/**
 * 自動重啟
 *
 * @author dexc
 */
public class ServerRestartTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(ServerRestartTimer.class);
    private static final ArrayList<Calendar> _restartList = new ArrayList<Calendar>();
    private static Calendar _restart = null;
    private static String _string = "yyyy/MM/dd HH:mm:ss";
    private static String _startTime = null;
    private static String _restartTime = null;
    private ScheduledFuture<?> _timer;

    /**
     * 重新啟動時間
     *
     */
    public static String get_restartTime() {
        return _restartTime;
    }

    /**
     * 啟動時間
     *
     */
    public static String get_startTime() {
        return _startTime;
    }

    /**
     * 距離關機小逾10分鐘
     *
     */
    public static boolean isRtartTime() {
        if (_restart == null) {
            return false;
        }
        return (_restart.getTimeInMillis() - System.currentTimeMillis()) <= (10 * 60 * 1000);
    }

    private static Calendar timestampToCalendar() {
        final TimeZone _tz = TimeZone.getTimeZone(Config.TIME_ZONE);
        final Calendar cal = Calendar.getInstance(_tz);
        return cal;
    }

    public void start() {
        if (Config.AUTORESTART == null) {
            return;
        }
        final Calendar cals = timestampToCalendar();
        if (_startTime == null) {
            final String nowDate = new SimpleDateFormat(_string).format(cals.getTime());
            _startTime = nowDate;
        }
        if (Config.AUTORESTART != null) {
            final String HH = new SimpleDateFormat("HH").format(cals.getTime());
            int HHi = Integer.parseInt(HH);
            final String mm = new SimpleDateFormat("mm").format(cals.getTime());
            int mmi = Integer.parseInt(mm);
            for (String hm : Config.AUTORESTART) {
                String[] hm_b = hm.split(":");
                String hh_b = hm_b[0];
                String mm_b = hm_b[1];
                int newHH = Integer.parseInt(hh_b);
                int newMM = Integer.parseInt(mm_b);
                final Calendar cal = timestampToCalendar();
                int xh = -1;
                int xhh = newHH - HHi;
                if (xhh > 0) {
                    xh = xhh;
                } else {
                    xh = (24 - HHi) + newHH;
                }
                int xm = newMM - mmi;
                cal.add(Calendar.HOUR, xh);
                cal.add(Calendar.MINUTE, xm);
                _restartList.add(cal);
            }
            for (Calendar tmpCal : _restartList) {
                if (_restart == null) {
                    _restart = tmpCal;
                } else {
                    boolean re = tmpCal.before(_restart);
                    if (re) {
                        _restart = tmpCal;
                    }
                }
            }
        }
        final String restartTime = new SimpleDateFormat(_string).format(_restart.getTime());
        _restartTime = restartTime;
        _log.warn("\n\r--------------------------------------------------" + "\n\r       開機完成時間為:" + _startTime + "\n\r       設置關機時間為:" + _restartTime + "\n\r--------------------------------------------------");
        final int timeMillis = 60 * 1000;// 1分鐘
        _timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
    }

    @Override
    public void run() {
        try {
            startCommand();
        } catch (final Exception e) {
            _log.error("自動重啟時間軸異常重啟", e);
            GeneralThreadPool.get().cancel(_timer, false);
            final ServerRestartTimer restartTimer = new ServerRestartTimer();
            restartTimer.start();
        }
    }

    private void startCommand() {
        if (Config.AUTORESTART != null) {
            final Calendar cals = Calendar.getInstance();
            // 每日任務
            if (ConfigOtherSet2.QUEST_SET_RESET_TIME != null && ConfigOtherSet2.QUEST_SET_RESET_TIME.before(cals)) {
                final int questId[] = {L1PcQuest.BAO_QUEST_1, L1PcQuest.BAO_QUEST_2, L1PcQuest.BAO_QUEST_3, L1PcQuest.BAO_QUEST_4, L1PcQuest.BAO_QUEST_5};
                for (int i = 0; i < questId.length; i++) {
                    CharacterQuestReading.get().delQuest2(questId[i]);
                    _log.info("------- 刪除每日任務編號:" + questId[i] + " -------");
                }
                if (QuestMobSet.START) {
                    if (ServerQuestMobTable.get().getQuestMobListId() != null) {
                        for (Integer questIdEx : ServerQuestMobTable.get().getQuestMobListId()) {
                            CharacterQuestReading.get().delQuest2(questIdEx);
                            _log.info("------- 刪除每日狩獵任務編號:" + questIdEx + " -------");
                        }
                    }
                }
                World.get().broadcastServerMessage("\\fX目前所有每日任務已重置完畢。");
                ConfigOtherSet2.QUEST_SET_RESET_TIME = null;
                _log.info("------- 刪除每日任務完成 -------");
                _log.info("------- 刪除每日狩獵任務完成 -------");
            }
            // 媽祖狀態
            if (ConfigOtherSet2.MAZU_RESET_TIME != null && ConfigOtherSet2.MAZU_RESET_TIME.before(cals)) {
                CharacterQuestReading.get().delQuest2(30000);
                _log.info("------- 重置媽祖狀態 -------");
                // 發送系統提示訊息
                World.get().broadcastServerMessage("\\fX媽祖狀態已經重置，趕快來參拜吧！");
                // 清除完畢，回收物件
                ConfigOtherSet2.MAZU_RESET_TIME = null;
            }
            // 每日獎勵
            if (ConfigQuest.Day_Reward_Reset_Time != null && ConfigQuest.Day_Reward_Reset_Time.before(cals)) {
                CharacterQuestReading.get().delQuest2(ConfigQuest.Day_Reward_QuestId);
                _log.info("------- 每日獎勵任務編號重置完成 -------");
                // 發送系統提示訊息
                World.get().broadcastServerMessage("\\fX每日獎勵已經重置，趕快來領取吧！");
                // 清除完畢，回收物件
                ConfigQuest.Day_Reward_Reset_Time = null;
            }
            // 對戰獎勵
            if (Configcamp_war.RedBlueReward_RESET_TIME != null && Configcamp_war.RedBlueReward_RESET_TIME.before(cals)) {
                CharacterTable.resetRedblueReward();
                _log.info("------- 重置對戰獎勵次數完成 -------");
                // 發送系統提示訊息
                World.get().broadcastServerMessage("\\fX對戰獎勵次數已經重置，快來決鬥吧！");
                // 清除完畢，回收物件
                Configcamp_war.RedBlueReward_RESET_TIME = null;
            }
            // 關機重啟
            if (_restart.before(cals)) {
                Shutdown.getInstance().startShutdown(null, 300, true);
            }
        }
    }
}
