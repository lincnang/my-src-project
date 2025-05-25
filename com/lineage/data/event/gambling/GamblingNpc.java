package com.lineage.data.event.gambling;

import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.serverpackets.S_MoveCharPacket;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.types.Point;
import com.lineage.server.utils.L1SpawnUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GamblingNpc implements Runnable {
    private static final Log _log = LogFactory.getLog(GamblingNpc.class);
    private static final byte[] HEADING_TABLE_X = {0, 1, 1, 1, 0, -1, -1, -1};
    private static final byte[] HEADING_TABLE_Y = {-1, -1, 0, 1, 1, 1, 0, -1};
    private static final int[][] xx = new int[5][1];
    private Gambling _gambling;
    private L1NpcInstance _npc;
    private boolean _isOver = false;
    private Point[] _tgLoc;
    private int _xId;
    private int _sId = 1;
    private long _adena;
    private double _rate = 2.0D;
    private Point _loc;
    private Random _random = new Random();
    private int _randomTime;

    public GamblingNpc(Gambling gambling) {
        _gambling = gambling;
    }

    public Gambling get_gambling() {
        return _gambling;
    }

    public double get_rate() {
        return _rate;
    }

    public void set_rate(double rate) {
        _rate = rate;
    }

    public void add_adena(long adena) {
        _adena += adena;
    }

    public long get_adena() {
        return _adena;
    }

    public void delNpc() {
        _npc.deleteMe();
    }

    public L1NpcInstance get_npc() {
        return _npc;
    }

    public int get_xId() {
        return _xId;
    }

    public void showNpc(int npcid, int i) {
        _xId = i;
        L1Npc npc = NpcTable.get().getTemplate(npcid);
        if (npc != null) {
            _tgLoc = GamblingConfig.TGLOC[i];
            int x = _tgLoc[0].getX();
            int y = _tgLoc[0].getY();
            int[] gfxids;
            if (GamblingConfig.ISGFX) {
                gfxids = GamblingConfig.GFX[i];
            } else {
                gfxids = GamblingConfig.GFXD[i];
            }
            int gfxid = gfxids[_random.nextInt(gfxids.length)];
            _npc = L1SpawnUtil.spawn(npcid, x, y, (short) 4, 6, gfxid);
        }
    }

    public void getStart() {
        GeneralThreadPool.get().schedule(this, 10L);
    }

    public void run() {
        try {
            _loc = _tgLoc[1];
            while (!_isOver) {
                if (_xId == _gambling.WIN) {
                    _randomTime = 150;
                } else {
                    _randomTime = 190;
                }
                int ss = 190;
                if (_random.nextInt(100) < 25) {
                    ss = 150;
                }
                int speed = ss + _random.nextInt(_randomTime);
                TimeUnit.MILLISECONDS.sleep(speed);
                actionStart();
            }
        } catch (InterruptedException e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void setDirectionMove(int heading) {
        if (heading >= 0) {
            int locx = _npc.getX();
            int locy = _npc.getY();
            locx += HEADING_TABLE_X[heading];
            locy += HEADING_TABLE_Y[heading];
            _npc.setHeading(heading);
            _npc.setX(locx);
            _npc.setY(locy);
            _npc.broadcastPacketAll(new S_MoveCharPacket(_npc));
        }
    }

    private void actionStart() {
        int x = _loc.getX();
        int y = _loc.getY();
        try {
            int dir = _npc.targetDirection(x, y);
            setDirectionMove(dir);
            if (_npc.getLocation().getTileLineDistance(_loc) < 2) {
                _loc = _tgLoc[_sId];
                _sId += 1;
            }
        } catch (Exception e) {
            if (_gambling.get_oneNpc() == null) {
                _gambling.set_oneNpc(this);
                xx[_xId][0] += 1;
            }
            int dir = _npc.targetDirection(x, y);
            setDirectionMove(dir);
            _isOver = true;
        }
    }
}