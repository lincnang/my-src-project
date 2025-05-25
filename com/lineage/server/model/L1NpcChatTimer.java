package com.lineage.server.model;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NpcChat;
import com.lineage.server.serverpackets.S_NpcChatGlobal;
import com.lineage.server.serverpackets.S_NpcChatShouting;
import com.lineage.server.templates.L1NpcChat;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class L1NpcChatTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(L1NpcChatTimer.class);
    private final L1NpcInstance _npc;
    private final L1NpcChat _npcChat;

    public L1NpcChatTimer(L1NpcInstance npc, L1NpcChat npcChat) {
        _npc = npc;
        _npcChat = npcChat;
    }

    public void run() {
        try {
            if ((_npc == null) || (_npcChat == null)) {
                return;
            }
            if ((_npc.getHiddenStatus() != 0) || (_npc._destroyed)) {
                return;
            }
            int chatTiming = _npcChat.getChatTiming();
            int chatInterval = _npcChat.getChatInterval();
            boolean isShout = _npcChat.isShout();
            boolean isWorldChat = _npcChat.isWorldChat();
            String chatId1 = _npcChat.getChatId1();
            String chatId2 = _npcChat.getChatId2();
            String chatId3 = _npcChat.getChatId3();
            String chatId4 = _npcChat.getChatId4();
            String chatId5 = _npcChat.getChatId5();
            if (!chatId1.equals("")) {
                chat(_npc, chatTiming, chatId1, isShout, isWorldChat);
            }
            if (!chatId2.equals("")) {
                TimeUnit.MILLISECONDS.sleep(chatInterval);
                chat(_npc, chatTiming, chatId2, isShout, isWorldChat);
            }
            if (!chatId3.equals("")) {
                TimeUnit.MILLISECONDS.sleep(chatInterval);
                chat(_npc, chatTiming, chatId3, isShout, isWorldChat);
            }
            if (!chatId4.equals("")) {
                TimeUnit.MILLISECONDS.sleep(chatInterval);
                chat(_npc, chatTiming, chatId4, isShout, isWorldChat);
            }
            if (!chatId5.equals("")) {
                TimeUnit.MILLISECONDS.sleep(chatInterval);
                chat(_npc, chatTiming, chatId5, isShout, isWorldChat);
            }
        } catch (Throwable e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void chat(L1NpcInstance npc, int chatTiming, String chatId, boolean isShout, boolean isWorldChat) {
        if ((chatTiming == 0) && (npc.isDead())) {
            return;
        }
        if ((chatTiming == 1) && (!npc.isDead())) {
            return;
        }
        if ((chatTiming == 2) && (npc.isDead())) {
            return;
        }
        if (!isShout) {
            npc.broadcastPacketX8(new S_NpcChat(npc, chatId));
        } else {
            npc.wideBroadcastPacket(new S_NpcChatShouting(npc, chatId));
        }
        if (isWorldChat) {
            Iterator<L1PcInstance> localIterator = World.get().getAllPlayers().iterator();
            if (localIterator.hasNext()) {
                L1PcInstance pc = (L1PcInstance) localIterator.next();
                if (pc != null) {
                    pc.sendPackets(new S_NpcChatGlobal(npc, chatId));
                }
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.L1NpcChatTimer JD-Core Version: 0.6.2
 */