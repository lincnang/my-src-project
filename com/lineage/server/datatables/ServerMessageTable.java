package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class ServerMessageTable {
    private static final Log _log = LogFactory.getLog(ServerMessageTable.class);
    private static ServerMessageTable _instance;
    private final HashMap<Integer, L1ServerMessage> _msgList = new HashMap<Integer, L1ServerMessage>();

    private ServerMessageTable() {
        load();
    }

    public static ServerMessageTable get() {
        if (_instance == null) {
            _instance = new ServerMessageTable();
        }
        return _instance;
    }

    private void load() {
        Connection co = null;
        PreparedStatement pm = null;
        ResultSet rs = null;
        try {
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement("SELECT * FROM server_message");
            rs = pm.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                boolean show = rs.getBoolean("show");
                String message = rs.getString("message");

                L1ServerMessage msg = new L1ServerMessage();

                msg._show = show;
                msg._message = message;

                this._msgList.put(Integer.valueOf(id), msg);
            }
        } catch (SQLException e) {
            _log.error("error while creating server_message table", e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
        _log.info("自定訊息顯示->" + this._msgList.size());
    }

    public boolean checkMsg(int id) {
        if (!this._msgList.containsKey(Integer.valueOf(id))) {
            return false;
        }
        if (!((L1ServerMessage) this._msgList.get(Integer.valueOf(id)))._show) {
            return false;
        }

        return !((L1ServerMessage) this._msgList.get(Integer.valueOf(id)))._message.isEmpty();
    }

    public String getMsg(int id) {
        return ((L1ServerMessage) this._msgList.get(Integer.valueOf(id)))._message;
    }

    public void showServerMsg(int id, L1PcInstance pc) {
        if (checkMsg(id))
            if (pc == null)
                World.get().broadcastPacketToAll(new S_ServerMessage(((L1ServerMessage) this._msgList.get(Integer.valueOf(id)))._message));
            else
                pc.sendPackets(new S_ServerMessage(((L1ServerMessage) this._msgList.get(Integer.valueOf(id)))._message));
    }

    public void showServerMsg(int id, String s1, L1PcInstance pc) {
        if (checkMsg(id)) {
            String out = String.format(((L1ServerMessage) this._msgList.get(Integer.valueOf(id)))._message, new Object[]{s1});
            if (pc == null)
                World.get().broadcastPacketToAll(new S_ServerMessage(out));
            else
                pc.sendPackets(new S_ServerMessage(out));
        }
    }

    public void showServerMsg1(int id, String s1, L1PcInstance pc) {
        if (checkMsg(id)) {
            String out = String.format(((L1ServerMessage) this._msgList.get(Integer.valueOf(id)))._message, new Object[]{s1});
            if (pc == null)
                World.get().broadcastPacketToAll(new S_PacketBoxGree(out));
            else
                pc.sendPackets(new S_PacketBoxGree(out));
        }
    }

    public void showServerMsg(int id, String s1, String s2, L1PcInstance pc) {
        if (checkMsg(id)) {
            String out = String.format(((L1ServerMessage) this._msgList.get(Integer.valueOf(id)))._message, new Object[]{s1, s2});
            if (pc == null)
                World.get().broadcastPacketToAll(new S_ServerMessage(out));
            else
                pc.sendPackets(new S_ServerMessage(out));
        }
    }

    public void showServerMsg(int id, String s1, String s2, String s3, L1PcInstance pc) {
        if (checkMsg(id)) {
            String out = String.format(((L1ServerMessage) this._msgList.get(Integer.valueOf(id)))._message, new Object[]{s1, s2, s3});
            if (pc == null)
                World.get().broadcastPacketToAll(new S_ServerMessage(out));
            else
                pc.sendPackets(new S_ServerMessage(out));
        }
    }

    public void showServerMsg(int id, int s1, L1PcInstance pc) {
        if (checkMsg(id)) {
            String out = String.format(((L1ServerMessage) this._msgList.get(Integer.valueOf(id)))._message, new Object[]{String.valueOf(s1)});
            if (pc == null)
                World.get().broadcastPacketToAll(new S_ServerMessage(out));
            else
                pc.sendPackets(new S_ServerMessage(out));
        }
    }

    public void showServerMsg(int id, int s1, int s2, L1PcInstance pc) {
        if (checkMsg(id)) {
            String out = String.format(((L1ServerMessage) this._msgList.get(Integer.valueOf(id)))._message, new Object[]{String.valueOf(s1), String.valueOf(s2)});
            if (pc == null)
                World.get().broadcastPacketToAll(new S_ServerMessage(out));
            else
                pc.sendPackets(new S_ServerMessage(out));
        }
    }

    public void showServerMsg(int id, String s1, int s2, L1PcInstance pc) {
        if (checkMsg(id)) {
            String out = String.format(((L1ServerMessage) this._msgList.get(Integer.valueOf(id)))._message, new Object[]{s1, String.valueOf(s2)});
            if (pc == null)
                World.get().broadcastPacketToAll(new S_ServerMessage(out));
            else
                pc.sendPackets(new S_ServerMessage(out));
        }
    }

    private static class L1ServerMessage {
        private boolean _show;
        private String _message;

        private L1ServerMessage() {
        }
    }
}