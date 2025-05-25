package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.storage.LogChatStorage;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LogChatTable implements LogChatStorage {
    private static final Log _log = LogFactory.getLog(LogChatTable.class);

    public void isTarget(L1PcInstance pc, L1PcInstance target, String content, int type) {
        if (target == null) {
            return;
        }
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            String account_name = pc.getAccountName();
            int char_id = pc.getId();
            String name = pc.isGm() ? "******" : pc.getName();
            int clan_id = pc.getClanid();
            String clan_name = pc.getClanname();
            int locx = pc.getX();
            int locy = pc.getY();
            short mapid = pc.getMapId();
            String target_account_name = target.getAccountName();
            int target_id = target.getId();
            String target_name = target.getName();
            int target_clan_id = target.getClanid();
            String target_clan_name = target.getClanname();
            int target_locx = target.getX();
            int target_locy = target.getY();
            short target_mapid = target.getMapId();
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("INSERT INTO 日誌_聊天紀錄 (account_name, char_id, name, clan_id, clan_name, locx, locy, mapid, type, target_account_name, target_id, target_name, target_clan_id, target_clan_name, target_locx, target_locy, target_mapid, content, datetime) VALUE (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE())");
            pstm.setString(1, account_name);
            pstm.setInt(2, char_id);
            pstm.setString(3, name);
            pstm.setInt(4, clan_id);
            pstm.setString(5, clan_name);
            pstm.setInt(6, locx);
            pstm.setInt(7, locy);
            pstm.setInt(8, mapid);
            pstm.setInt(9, type);
            pstm.setString(10, target_account_name);
            pstm.setInt(11, target_id);
            pstm.setString(12, target_name);
            pstm.setInt(13, target_clan_id);
            pstm.setString(14, target_clan_name);
            pstm.setInt(15, target_locx);
            pstm.setInt(16, target_locy);
            pstm.setInt(17, target_mapid);
            pstm.setString(18, content);
            pstm.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void noTarget(L1PcInstance pc, String content, int type) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            String account_name = pc.getAccountName();
            int char_id = pc.getId();
            String name = pc.isGm() ? "******" : pc.getName();
            int clan_id = pc.getClanid();
            String clan_name = pc.getClanname();
            int locx = pc.getX();
            int locy = pc.getY();
            short mapid = pc.getMapId();
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("INSERT INTO 日誌_聊天紀錄 (account_name, char_id, name, clan_id, clan_name, locx, locy, mapid, type, content, datetime) VALUE (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE())");
            pstm.setString(1, account_name);
            pstm.setInt(2, char_id);
            pstm.setString(3, name);
            pstm.setInt(4, clan_id);
            pstm.setString(5, clan_name);
            pstm.setInt(6, locx);
            pstm.setInt(7, locy);
            pstm.setInt(8, mapid);
            pstm.setInt(9, type);
            pstm.setString(10, content);
            pstm.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }
}
