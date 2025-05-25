package com.lineage.data.npc.other;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;

public class Npc_Vip extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Vip.class);

    public static NpcExecutor get() {
        return new Npc_Vip();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (npc.getNpcId() == 94004) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_buyVip"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        boolean isCloseList = false;
        if (npc.getNpcId() == 94004 && cmd.equalsIgnoreCase("vip")) {
            if (pc.getInventory().checkItem(80033, 500) && pc.getInventory().checkItem(40308, 10000000)) {
                L1ItemInstance item = ItemTable.get().createItem(10000);
                pc.getInventory().consumeItem(80033, 500);
                pc.getInventory().consumeItem(40308, 10000000);
                if (item == null) {
                    _log.info("玩家:" + pc.getName() + " 嘗試修改對話檔進行遊戲");
                    pc.sendPackets(new S_ServerMessage("玩家:" + pc.getName() + " 嘗試修改對話檔進行遊戲,系統已記錄"));
                    return;
                }
                pc.sendPackets(new S_ServerMessage("\\fVVIP升級成功，請重登一次。"));
                setvip(pc);
            } else {
                pc.sendPackets(new S_ServerMessage("\\fV材料不足。"));
            }
        }
        if (isCloseList) {
            pc.sendPackets(new S_CloseList(pc.getId()));
        }
    }

    //發送VIP
    private void setvip(L1PcInstance pc) {
        try {
            long timeNow = System.currentTimeMillis();
            Timestamp starttime = new Timestamp(timeNow);
            long x1 = 30 * 24 * 60 * 60; //1個月
            long x2 = x1 * 1000L;
            long upTime = x2 + timeNow;
            Timestamp endtime = new Timestamp(upTime);
            if (pc.getVipStartTime() == null && pc.getVipEndTime() == null) {
                pc.setVipStartTime(starttime);
                pc.setVipEndTime(endtime);
            }
            int oldviplvl = pc.get_vipLevel();
            if (oldviplvl + 1 > 5) {
                pc.set_vipLevel(5);
            } else {
                pc.set_vipLevel(oldviplvl + 1);
            }
            pc.saveVip();
            //pc.endVipStatus();
            //pc.setVipStatus();
            //pc.save();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage());
            //_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.other.Npc_Aanon JD-Core Version: 0.6.2
 */