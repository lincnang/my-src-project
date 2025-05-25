package com.lineage.data.npc.other2;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Npc_LvGift extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_LvGift.class);
    private int _questid;
    private int _metelv;
    private int _level1;
    private int _level2;
    private int _itemid;
    private int _itemcount;
    private String _htmlid;

    private Npc_LvGift() {
    }

    public static NpcExecutor get() {
        return new Npc_LvGift();
    }

    @Override
    public int type() {
        return 3;
    }

    @Override
    public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
        try {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), this._htmlid));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
        try {
            if (cmd.equalsIgnoreCase("Get_Active_Gift")) {
                if (pc.getQuest().get_step(_questid) == 255) {
                    pc.sendPackets(new S_ServerMessage("\\fR這次的活動禮物已經領取過了。"));
                    return;
                }
                if (pc.getMeteLevel() < _metelv) {
                    pc.sendPackets(new S_ServerMessage("\\fR您的轉生次數低於 " + _metelv + "轉，再加把勁。"));
                    return;
                }
                if (pc.getLevel() < _level1) {
                    pc.sendPackets(new S_ServerMessage("\\fR您的等級低於 " + _level1 + "級，再加把勁。"));
                    return;
                }
                if (pc.getLevel() > _level2) {
                    pc.sendPackets(new S_ServerMessage("\\fR您的等級超過 " + _level2 + "級，無緣領取。"));
                    return;
                }
                pc.getQuest().set_step(_questid, 255);
                CreateNewItem.createNewItem(pc, _itemid, _itemcount);
                pc.save();
                pc.sendPackets(new S_CloseList(pc.getId()));
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void set_set(String[] set) {
        try {
            _questid = Integer.parseInt(set[1]);
            _metelv = Integer.parseInt(set[2]);
            _level1 = Integer.parseInt(set[3]);
            _level2 = Integer.parseInt(set[4]);
            _itemid = Integer.parseInt(set[5]);
            _itemcount = Integer.parseInt(set[6]);
            this._htmlid = set[7];
        } catch (Exception e) {
        }
    }
}
