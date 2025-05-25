package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

public class Tscroll extends ItemExecutor {
    private static boolean ALT_TALKINGSCROLLQUEST = true;

    public static ItemExecutor get() {
        return new Tscroll();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (ALT_TALKINGSCROLLQUEST) {
            if (pc.getQuest().get_step(39) == 0) {
                pc.sendPackets(new S_NPCTalkReturn(pc, "tscrolla"));
            } else if (pc.getQuest().get_step(39) == 1) {
                pc.sendPackets(new S_NPCTalkReturn(pc, "tscrollb"));
            } else if (pc.getQuest().get_step(39) == 2) {
                pc.sendPackets(new S_NPCTalkReturn(pc, "tscrollc"));
            } else if (pc.getQuest().get_step(39) == 3) {
                pc.sendPackets(new S_NPCTalkReturn(pc, "tscrolld"));
            } else if (pc.getQuest().get_step(39) == 4) {
                pc.sendPackets(new S_NPCTalkReturn(pc, "tscrolle"));
            } else if (pc.getQuest().get_step(39) == 5) {
                pc.sendPackets(new S_NPCTalkReturn(pc, "tscrollf"));
            } else if (pc.getQuest().get_step(39) == 6) {
                pc.sendPackets(new S_NPCTalkReturn(pc, "tscrollg"));
            } else if (pc.getQuest().get_step(39) == 7) {
                pc.sendPackets(new S_NPCTalkReturn(pc, "tscrollh"));
            } else if (pc.getQuest().get_step(39) == 8) {
                pc.sendPackets(new S_NPCTalkReturn(pc, "tscrolli"));
            } else if (pc.getQuest().get_step(39) == 9) {
                pc.sendPackets(new S_NPCTalkReturn(pc, "tscrollj"));
            } else if (pc.getQuest().get_step(39) == 10) {
                pc.sendPackets(new S_NPCTalkReturn(pc, "tscrollk"));
            } else if (pc.getQuest().get_step(39) == 11) {
                pc.sendPackets(new S_NPCTalkReturn(pc, "tscrolll"));
            } else if (pc.getQuest().get_step(39) == 12) {
                pc.sendPackets(new S_NPCTalkReturn(pc, "tscrollm"));
            } else if (pc.getQuest().get_step(39) == 13) {
                pc.sendPackets(new S_NPCTalkReturn(pc, "tscrolln"));
            } else if (pc.getQuest().get_step(39) == 255) {
                pc.sendPackets(new S_NPCTalkReturn(pc, "tscrollo"));
            }
        } else {
            pc.sendPackets(new S_NPCTalkReturn(pc, "tscrollp"));
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.Tscroll JD-Core Version: 0.6.2
 */