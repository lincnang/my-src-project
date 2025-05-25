package com.lineage.data.npc.other2;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Party;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_Militia extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Militia.class);
    private int _locX = 0;
    private int _locY = 0;
    private int _mapId = 0;
    private int _count = 0;
    private String _htmlid = null;
    private int _lv = 0;

    private Npc_Militia() {
    }

    public static NpcExecutor get() {
        return new Npc_Militia();
    }

    @Override
    public int type() {
        return 3;
    }

    @Override
    public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), _htmlid));
    }

    @Override
    public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
        if (cmd.equals("militiaURL")) {
            if (pc.getParty() != null) {
                if (!(pc.getParty().isLeader(pc)) && pc.getParty().getLeader().getMapId() == _mapId) {
                    if (pc.getLevel() >= _lv) {
                        L1Teleport.teleport(pc, _locX, _locY, (short) _mapId, 0, true);
                        return;
                    }
                }
            }
            if (!pc.isInParty()) {
                pc.sendPackets(new S_SystemMessage("請組隊前往。"));
                return;
            }
            if (!pc.getParty().isLeader(pc)) {
                pc.sendPackets(new S_SystemMessage("您不是隊長。"));
                return;
            }
            if (pc.getParty().getNumOfMembers() < _count) {
                pc.sendPackets(new S_SystemMessage("最低人數:" + _count + " 希望能再召集一些同行的同伴！！"));
                return;
            }
            if (pc.getLevel() < _lv) {
                pc.sendPackets(new S_SystemMessage("低於等級:" + _lv + "無法前往。"));
                return;
            }
            teltport_all(pc, _locX, _locY, _mapId);
        }
    }

    private void teltport_all(final L1PcInstance pc, final int locx, final int locy, final int mapId) {
        try {
            final L1Party party = pc.getParty();
            for (L1PcInstance otherPc : party.getMemberList()) {
                if (otherPc.getMapId() == party.getLeader().getMapId()) {
                    if (otherPc.getLevel() >= _lv) {
                        L1Teleport.teleport(otherPc, locx, locy, (short) mapId, 0, true);
                    }
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void set_set(String[] set) {
        try {
            _locX = Integer.parseInt(set[1]);
            _locY = Integer.parseInt(set[2]);
            _mapId = Integer.parseInt(set[3]);
            _count = Integer.parseInt(set[4]);
            _htmlid = set[5];
            _lv = Integer.parseInt(set[6]);
        } catch (final Exception e) {
            _log.error("Npc_Militia設置出錯請檢查。");
        }
    }
}
