package com.lineage.managerUI;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ChatWhisperTo;
import com.lineage.server.world.World;

/**
 * 管理器聊天控制 類名稱：ServerChat<br>
 * 修改時間：2022年4月26日 下午5:10:11<br>
 * 修改人:QQ:263075225<br>
 *
 * @version<br>
 */
public class ServerChat {
    private static volatile ServerChat uniqueInstance = null;

    private ServerChat() {
    }

    static public ServerChat getInstance() {
        if (uniqueInstance == null) {
            synchronized (ServerChat.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new ServerChat();
                }
            }
        }
        return uniqueInstance;
    }

    public void sendChatToAll(String message) {
        World.get().broadcastServerMessage(message);
    }

    public boolean whisperToPlayer(String userName, String message) {
        L1PcInstance player = World.get().getPlayer(userName);
        if (player != null) {
            player.sendPackets(new S_ChatWhisperTo("******", message));
            return true;
        }
        return false;
    }
}
