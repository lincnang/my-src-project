package com.lineage.server;

import com.lineage.config.ConfigFreeKill;
import com.lineage.server.datatables.MapsTable;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.MapData;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.ScheduledFuture;

/**
 * 掃街系統
 */
public final class CheckFightTimeController implements Runnable { //src015
    private static final Log _log = LogFactory.getLog(CheckFightTimeController.class);
    private static final Random _random = new Random();
    private static final LinkedList<Calendar> TIME_LIST_2 = new LinkedList<>();
    private static volatile CheckFightTimeController _instance;
    private static boolean SWITCH_2;
    private static Calendar TIME_3;
    private static Calendar TIME_4;
    private static Set<Integer> MAP_LIST_2;
    private ScheduledFuture<?> _timer;

    public static CheckFightTimeController getInstance() {
        if (_instance == null) {
            synchronized (CheckFightTimeController.class) {
                if (_instance == null) {
                    _instance = create();
                }
            }
        }
        return _instance;
    }

    private static CheckFightTimeController create() {
        CheckFightTimeController temp = new CheckFightTimeController();
        temp.hashCode();
        return temp;
    }

    public void run() {
        try {
            Calendar calendar = Calendar.getInstance();
            if (ConfigFreeKill.FREE_FIGHT_SWITCH) {
                if ((SWITCH_2) && (!MAP_LIST_2.isEmpty())) {
                    if (TIME_4.before(calendar)) {
                        World.get().broadcastPacketToAll(new S_SystemMessage("<<上一階段地圖掃街時間結束>>"));
                        MAP_LIST_2.clear();
                        SWITCH_2 = false;
                        TIME_3 = (Calendar) TIME_LIST_2.getFirst();
                        for (Calendar tmpCal : TIME_LIST_2) {
                            if (TIME_3.before(calendar)) {
                                TIME_3 = tmpCal;
                            }
                        }
                        if (TIME_3 == TIME_LIST_2.getLast()) {
                            TIME_3 = (Calendar) TIME_3.clone();
                            for (Calendar tmpCal : TIME_LIST_2) {
                                tmpCal.add(Calendar.DATE, 1); // 日
                            }
                        }
                        TIME_4 = (Calendar) TIME_3.clone();
                        //TIME_4.add(Calendar.HOUR_OF_DAY, ConfigOther.FREE_FIGHT_REMAIN_TIME); // 時
                        TIME_4.add(Calendar.MINUTE, ConfigFreeKill.FREE_FIGHT_REMAIN_TIME); // 分
                    }
                } else if ((TIME_3.before(calendar)) && (TIME_4.after(calendar))) {
                    setMapList2();
                    SWITCH_2 = true;
                }
            }
        } catch (Exception e) {
            _log.info("PC檢查時間軸 [隨機加倍、隨機掃街、摸塔福利]異常重啟");
            GeneralThreadPool.get().cancel(this._timer, false);
            _instance = new CheckFightTimeController();
            _instance.start();
        }
    }

    /*private final void setMapList2() {
        int length = _random.nextInt(ConfigOther.FREE_FIGHT_MAP_MAX - ConfigOther.FREE_FIGHT_MAP_MIN + 1) + ConfigOther.FREE_FIGHT_MAP_MIN;
        while ((MAP_LIST_2.size() < length) && (MAP_LIST_2.size() < ConfigOther.FREE_FIGHT_MAP_LIST.length)) {
            int randomInt = _random.nextInt(ConfigOther.FREE_FIGHT_MAP_LIST.length);
            int mapId = Integer.parseInt(ConfigOther.FREE_FIGHT_MAP_LIST[randomInt]);
            MAP_LIST_2.add(Integer.valueOf(mapId));
        }
        World.get().broadcastPacketToAll(new S_SystemMessage(getMapList2().toString()));
    }
    public final StringBuilder getMapList2() {
        if (MAP_LIST_2.isEmpty()) {
            return null;
        }
        StringBuilder sbr = new StringBuilder();
        sbr.append(">>當前指定掃街地圖為:");
        for (Iterator<Integer> localIterator = MAP_LIST_2.iterator(); localIterator.hasNext();) {
            int mapId = ((Integer)localIterator.next()).intValue();
            sbr.append("[").append(MapsTable.get().locationname(mapId)).append("]");
        }
        sbr.append("請注意! 該地圖內不管善惡值均會噴裝!");
        return sbr;
    }*/
    private void setMapList2() {
        if (ConfigFreeKill.FREE_FIGHT_ALLMAP) {
            // 全部地圖掃街
            final Map<Integer, MapData> mapDatas = MapsTable.get().getMaps();
            for (final Integer key : mapDatas.keySet()) {
                final MapData mapData = mapDatas.get(key);
                final int mapId = mapData.mapId;
                MAP_LIST_2.add(mapId);
            }
        } else {
            // 隨機地圖掃街
            int length = _random.nextInt(ConfigFreeKill.FREE_FIGHT_MAP_MAX - ConfigFreeKill.FREE_FIGHT_MAP_MIN + 1) + ConfigFreeKill.FREE_FIGHT_MAP_MIN;
            while ((MAP_LIST_2.size() < length) && (MAP_LIST_2.size() < ConfigFreeKill.FREE_FIGHT_MAP_LIST.length)) {
                int randomInt = _random.nextInt(ConfigFreeKill.FREE_FIGHT_MAP_LIST.length);
                int mapId = Integer.parseInt(ConfigFreeKill.FREE_FIGHT_MAP_LIST[randomInt]);
                MAP_LIST_2.add(mapId);
            }
        }
        World.get().broadcastPacketToAll(new S_SystemMessage(getMapList2().toString()));
    }

    public StringBuilder getMapList2() {
        if (MAP_LIST_2.isEmpty()) {
            return null;
        }
        final StringBuilder sbr = new StringBuilder();
        if (ConfigFreeKill.FREE_FIGHT_ALLMAP) {
            sbr.append(">>當前指定掃街地圖為:[全地圖]");
        } else {
            sbr.append(">>當前指定掃街地圖為:");
            for (Integer integer : MAP_LIST_2) {
                int mapId = integer;
                sbr.append("[").append(MapsTable.get().locationname(mapId)).append("]");
            }
        }
        sbr.append("請注意! 該地圖內不管善惡值均會噴裝!");
        return sbr;
    }

    public boolean isFightMap(final int mapId) {
        return MAP_LIST_2.contains(mapId);
    }

    public void start() {
        Calendar cal_now = Calendar.getInstance();
        boolean canStart = false;
        if (ConfigFreeKill.FREE_FIGHT_SWITCH) {
            canStart = true;
            SWITCH_2 = false;
            if (MAP_LIST_2 != null) {
                MAP_LIST_2.clear();
            }
            MAP_LIST_2 = new HashSet<>();
            for (String hm : ConfigFreeKill.FREE_FIGHT_TIME_LIST) {
                String[] hm_b = hm.split(":");
                String hh_b = hm_b[0];
                String mm_b = hm_b[1];
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hh_b)); // 時
                cal.set(Calendar.MINUTE, Integer.parseInt(mm_b)); // 分
                TIME_LIST_2.addLast(cal);
            }
            TIME_3 = (Calendar) TIME_LIST_2.getFirst();
            for (Calendar tmpCal : TIME_LIST_2) {
                if (TIME_3.before(cal_now)) {
                    TIME_3 = tmpCal;
                }
            }
            if (TIME_3 == TIME_LIST_2.getLast()) {
                TIME_3 = (Calendar) TIME_3.clone();
                for (Calendar tmpCal : TIME_LIST_2) {
                    tmpCal.add(Calendar.DATE, 1); // 日
                }
            }
            TIME_4 = (Calendar) TIME_3.clone();
            //TIME_4.add(Calendar.HOUR_OF_DAY, ConfigOther.FREE_FIGHT_REMAIN_TIME); // 時
            TIME_4.add(Calendar.MINUTE, ConfigFreeKill.FREE_FIGHT_REMAIN_TIME); // 分
        }
        if (canStart) {
            int timeMillis = 10000;
            this._timer = GeneralThreadPool.get().scheduleAtFixedRate(this, 1000L, timeMillis);
            _log.info("讀取->掃街系統完成");
        }
    }
}
