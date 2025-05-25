package com.lineage.server.datatables;

import com.lineage.DatabaseFactoryLogin;
import com.lineage.config.Config;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class DeGlobalChatTable {
    private static final Log _log = LogFactory.getLog(DeGlobalChatTable.class);
    private static final ArrayList<String> _chatList = new ArrayList<>();
    private static final Random _random = new Random();
    private static DeGlobalChatTable _instance;

    public static DeGlobalChatTable get() {
        if (_instance == null) {
            _instance = new DeGlobalChatTable();
        }
        return _instance;
    }

    public void load() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactoryLogin.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `de_shopds_global`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                String detitle = "";
                if (Config.CLIENT_LANGUAGE == 3) {
                    detitle = rs.getString("chatinfobig5");
                } else {
                    detitle = rs.getString("chatinfo");
                }
                _chatList.add(detitle);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public String getChat() {
        int index = _random.nextInt(_chatList.size());
        return (String) _chatList.get(index);
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.DeGlobalChatTable JD-Core Version: 0.6.2
 */