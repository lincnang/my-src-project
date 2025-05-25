package com.add.MJBookQuestSystem.Templates;

import com.add.MJBookQuestSystem.Loader.WeekQuestLoader;
import com.lineage.DatabaseFactory;
import com.lineage.config.ConfigLIN;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 每周任務時間
 **/
public class WeekQuestDateCalculator {
    private static Logger _log = Logger.getLogger(WeekQuestDateCalculator.class.getName());
    private static WeekUpdator _updator = null;
    private static WeekQuestDateCalculator _instance;
    private Timestamp _updateStamp;
    private ScheduledFuture<?> _future;

    private WeekQuestDateCalculator() {
    }

    public static WeekQuestDateCalculator getInstance() {
        if (_instance == null) {
            _instance = new WeekQuestDateCalculator();
        }
        return _instance;
    }

    private void setLastTime() {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("insert into tb_weekquest_updateInfo set id=1, lastTime=? on duplicate key update lastTime=?");
            pstm.setTimestamp(1, _updateStamp);
            pstm.setTimestamp(2, _updateStamp);
            pstm.executeUpdate();
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("[ERROR - WeekQuestDateCalculator] setLastTime()...").append(_updateStamp).append(" write error. \r\n").append(e.getLocalizedMessage());
            _log.log(Level.SEVERE, sb.toString(), e);
            System.out.println(sb.toString());
            e.printStackTrace();
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    private Date getLastTime() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String column = "";
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("select * from tb_weekquest_updateInfo");
            rs = pstm.executeQuery();
            if (rs.next()) {
                Timestamp ts = rs.getTimestamp("lastTime");
                return new Date(ts.getTime());
            }
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("[ERROR - WeekQuestDateCalculator] getLastTime()...").append(column).append(" read error. \r\n").append(e.getLocalizedMessage());
            _log.log(Level.SEVERE, sb.toString(), e);
            System.out.println(sb.toString());
            e.printStackTrace();
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return null;
    }

    public void run() {
        long sleepTime = 0;
        long nowMillis = System.currentTimeMillis();
        // 導入以前的更新資訊
        Date oldDate = getLastTime();
        // 沒有以前的更新資訊
        if (oldDate == null) {
            // 現在更新
            setUpdate(nowMillis);
        } else {
            // 從以前的更新資訊中導入以下更新資訊
            Calendar cal = getNextWeekCalendar(oldDate.getTime());
            // 如果目前時間大於下一次更新時間，則不會更新
            if (nowMillis >= cal.getTimeInMillis()) {
                // 更新到目前更新時間
                setUpdate(nowMillis);
                // 現在還不是更新的時候
            } else {
                // 映射以前的更新資訊
                _updateStamp = new Timestamp(oldDate.getTime());
                // 節省時間直到下次更新
                sleepTime = cal.getTimeInMillis() - nowMillis;
                // 休息
                setNextUpdate(sleepTime);
            }
        }
    }

    /**
     * 返回從當前日曆到下一周所需的天數
     **/
    private int getDayToNextWeek(Calendar cal) {
        int week = cal.get(Calendar.DAY_OF_WEEK);// 獲得一周中的一天
        int nextWeek = 0;
        if (ConfigLIN.WQ_UPDATE_TYPE == 0) // 如果是日常類型，則返回一天
        {
            nextWeek = 1;
        } else if (week >= ConfigLIN.WQ_UPDATE_WEEK) {
            nextWeek = (ConfigLIN.WQ_UPDATE_WEEK + 7) - week;
        } else {
            nextWeek = ConfigLIN.WQ_UPDATE_WEEK - week;
        }
        return nextWeek;
    }

    /**
     * 返回給定時間下個星期的日曆
     **/
    private Calendar getNextWeekCalendar(long sysmillis) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTime(new Date(sysmillis));
        Calendar nextCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        nextCal.setTime(cal.getTime());
        nextCal.add(Calendar.DATE, getDayToNextWeek(cal));
        nextCal.set(Calendar.HOUR_OF_DAY, ConfigLIN.WQ_UPDATE_TIME);
        nextCal.set(Calendar.MINUTE, 0);
        nextCal.set(Calendar.SECOND, 0);
        return nextCal;
    }

    /**
     * 返回更新的每週任務
     **/
    public boolean isUpdateWeekQuest(Timestamp ts) {
        if (ts == null) {
            return true;
        }
        // timezone 該設置有時被誤認為是幾分之一毫秒的錯誤，有時不會發生錯誤，1秒內的錯誤都是一樣的
        long time = Math.abs(ts.getTime() - _updateStamp.getTime());
        if (time <= 1000) {
            return false;
        }
        return true;
    }

    public Timestamp getUpdateStamp() {
        return _updateStamp;
    }

    /**
     * 從現在開始進行更新
     **/
    public void setUpdate() {
        setUpdate(System.currentTimeMillis());
    }

    /**
     * 在有限的時間裡實施更新
     **/
    public void setUpdate(long nowMillis) {
        _updateStamp = new Timestamp(nowMillis); // 指定當前時間
        setLastTime(); // 保存到數據庫
        // 休息至下周
        long sleepTime = getNextWeekCalendar(_updateStamp.getTime()).getTimeInMillis() - _updateStamp.getTime();
        setNextUpdate(sleepTime);
    }

    /**
     * 進行下一次更新
     **/
    private void setNextUpdate(long sleepTime) {
        WeekQuestLoader.reload();
        // 如果由於某種原因有可能重新載入
        if (_updator == null) {
            _updator = new WeekUpdator();
        }
        _future = GeneralThreadPool.get().schedule(_updator, sleepTime);
    }

    public synchronized void reloadTime() {
        if (_future != null) {
            _future.cancel(true);
        }
        setUpdate(System.currentTimeMillis());
        World.get().broadcastPacketToAll(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, "每周任務已更新，您可以通過重新登入來獲得新的任務列表。"));
    }

    /**
     * 定期更新提示
     **/
    class WeekUpdator implements Runnable {
        @Override
        public void run() {
            try {
                long now = System.currentTimeMillis();
                if (_updateStamp.getTime() > now) {
                    GeneralThreadPool.get().schedule(this, _updateStamp.getTime() - now);
                    return;
                }
                String s = "Week Quest Updates now time is " + now;
                System.out.println(s);
                _log.log(Level.SEVERE, s);
                WeekQuestDateCalculator.getInstance().setUpdate(now);
                TimeUnit.MILLISECONDS.sleep(500);
                World.get().broadcastPacketToAll(new S_PacketBox(S_PacketBox.MSG_COLOR_MESSAGE, "每周任務已更新，您可以通過重新登入來獲得新的任務列表。"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
