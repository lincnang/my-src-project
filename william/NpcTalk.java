package william;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.S_NpcChatPacket;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NpcTalk extends TimerTask {
    private static final Logger log = Logger.getLogger(NpcTalk.class.getName());
    private static final ConcurrentHashMap<Integer, ArrayList<Chat>> _chatList = new ConcurrentHashMap<>();
    private static int index;

    public static void load() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM william_npc_talk");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int npcid = rs.getInt("npc_id");
                Chat chat = new Chat();
                chat.chatType = rs.getByte("shout");
                chat.chatContent = rs.getString("talk");
                chat.chatTime = (rs.getInt("sec") * 1000);
                if (_chatList.containsKey(npcid)) {
                    ((ArrayList<Chat>) _chatList.get(npcid)).add(chat);
                } else {
                    ArrayList<Chat> fastTable = new ArrayList<>();
                    fastTable.add(chat);
                    _chatList.put(npcid, fastTable);
                }
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
        GeneralThreadPool.get().scheduleAtFixedRate(new NpcTalk(), 0L, 1000L);
    }

    public static void nextIndex() {
        index += 1;
    }

    public static void startDummyChat(L1NpcInstance npc) {
        ArrayList<?> chatTable = (ArrayList<?>) _chatList.get(npc.getNpcId());
        if (chatTable == null) {
            return;
        }
        Chat c = (Chat) chatTable.get(index % chatTable.size());
        long currentTime = System.currentTimeMillis();
        if (currentTime > c.lastTime) {
            c.lastTime = (currentTime + c.chatTime);
            String content = c.chatContent;
            byte type = c.chatType;
            npc.broadcastPacketAll(new S_NpcChatPacket(npc, content, type));
        }
    }

    public final void run() {
        for (L1Object object : World.get().getAllVisibleObjects().values()) {
            if ((object instanceof L1NpcInstance)) {
                startDummyChat((L1NpcInstance) object);
            }
        }
    }

    public static class Chat {
        private byte chatType;
        private String chatContent;
        private int chatTime;
        private long lastTime;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * william.NpcTalk JD-Core Version: 0.6.2
 */