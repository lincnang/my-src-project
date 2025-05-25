package com.lineage.server.timecontroller.server;

import com.lineage.server.datatables.lock.CharMapTimeReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_PacketBoxGame;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 地圖計時器
 *
 * @author sudaw
 */
public class TimeMap extends TimerTask {
    /**
     * 提示信息.
     */
    private static final Log _log = LogFactory.getLog(TimeMap.class);
    CopyOnWriteArrayList<L1PcInstance> pclist = new CopyOnWriteArrayList<>();
    int mapid;
    int id;
    int maptime;
    int locx;
    int locy;
    int teleportmapid;
    String mapName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * 地圖名稱
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * 地圖名稱
     */
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    /**
     * 地圖名稱
     */
    public int getMapid() {
        return mapid;
    }

    /**
     * 地圖名稱
     */
    public void setMapid(int mapid) {
        this.mapid = mapid;
    }

    /**
     * 地圖限制時間(秒)
     */
    public int getMaptime() {
        return maptime;
    }

    /**
     * 地圖限制時間(秒)
     */
    public void setMaptime(int maptime) {
        this.maptime = maptime;
    }

    /**
     * 傳出地圖的X坐標
     */
    public int getLocx() {
        return locx;
    }

    /**
     * 傳出地圖的X坐標
     */
    public void setLocx(int locx) {
        this.locx = locx;
    }

    /**
     * 傳出地圖的Y坐標
     */
    public int getLocy() {
        return locy;
    }

    /**
     * 傳出地圖的Y坐標
     */
    public void setLocy(int locy) {
        this.locy = locy;
    }

    /**
     * 傳出地圖ID
     */
    public int getTeleportmapid() {
        return teleportmapid;
    }

    /**
     * 傳出地圖ID
     */
    public void setTeleportmapid(int teleportmapid) {
        this.teleportmapid = teleportmapid;
    }

    // private L1PcInstance _pc;
    @Override
    public void run() {
        try {
            for (L1PcInstance _pc : pclist) {
                if (_pc.isDead()) {
                    return;
                }
                short map = _pc.getMapId();
                if (map >= 4001 && map <= 4050) {
                    map = 4001;
                }
                if (map == mapid) {// 人物是否在記時地圖
                    if (_pc.isTimeMap()) {// 人物是否開啟記時
                        final int time = _pc.getMapTime(map);
                        // final int maxTime = 14400; // 3小時
                        if (time >= maptime) {// 是否大於限制時間
                            _pc.stopTimeMap();
                            showTime(_pc, 2);
                            deluser(_pc);
                            _pc.sendPackets(new S_SystemMessage("你已經達到此地圖的限制時間"));
                            this.teleport(_pc);
                            return;
                        }
                        _pc.updateMapTime(1);
                        // this.message();
                        showTime(_pc, 1);
                        // System.out.println("增加時間");
                    } else {
                        _pc.setTimeMap(true);
                    }
                } else {// 人物不在記時地圖
                    showTime(_pc, 2);
                    _pc.stopTimeMap();
                    deluser(_pc);
                    // System.out.println("停止計時、地圖編號不對");
                }
            }
        } catch (final Throwable e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 左上角顯示剩餘時間
     */
    private void showTime(L1PcInstance _pc, int b) {
        if (mapid == 4001) {
            return;
        }
        if (b == 1) {// 顯示剩餘時間
            final int time = _pc.getMapTime(_pc.getMapId());
            _pc.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, maptime - time, null));
            // }
        } else if (b == 2) {// 時間結束
            _pc.sendPackets(new S_PacketBoxGame(S_PacketBoxGame.STARTTIMECLEAR));
        }
    }

    /**
     * 傳送.
     */
    private void teleport(L1PcInstance _pc) {
        L1Teleport.teleport(_pc, locx, locy, (short) teleportmapid, 5, true);
    }

    /**
     * 向記時器添加玩家
     */
    public void adduser(L1PcInstance _pc) {
        for (L1PcInstance pc : pclist) {
            if (pc.getId() == _pc.getId()) {
                return;
            }
        }
        pclist.add(_pc);
    }

    /**
     * 刪除記時器玩家
     */
    private void deluser(L1PcInstance _pc) {
        for (int i = 0; i < pclist.size(); i++) {
            if (pclist.get(i).getId() == _pc.getId()) {
                if (mapid != 4001) {
                    CharMapTimeReading.get().update(_pc.getId(), mapid, _pc.getMapTime(mapid));
                }
                pclist.remove(i);
            }
        }
    }
}
