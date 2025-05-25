package com.lineage.data.cmd;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.serverpackets.S_MoveCharPacket;
import com.lineage.server.types.Point;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NpcWorkMove {
    private static final Log _log = LogFactory.getLog(NpcWorkMove.class);
    private static final byte[] HEADING_TABLE_X = {0, 1, 1, 1, 0, -1, -1, -1};
    private static final byte[] HEADING_TABLE_Y = {-1, -1, 0, 1, 1, 1, 0, -1};
    private L1NpcInstance _npc;

    public NpcWorkMove(L1NpcInstance npc) {
        _npc = npc;
    }

    public boolean actionStart(Point point) {
        int x = point.getX();
        int y = point.getY();
        try {
            int dir = _npc.targetDirection(x, y);
            setDirectionMove(dir);
            if (_npc.getLocation().getTileLineDistance(point) == 0) {
                return false;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return true;
    }

    private void setDirectionMove(int heading) {
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