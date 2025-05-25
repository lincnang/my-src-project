package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.storage.UpdateLocStorage;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateLocTable implements UpdateLocStorage {
    private static final Log _log = LogFactory.getLog(UpdateLocTable.class);

    public void setPcLoc(String accName) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE `characters` SET `LocX`=32781,`LocY`=32856,`MapID`=340 WHERE `account_name`=?");
            pstm.setString(1, accName);
            pstm.execute();
            pstm.close();
            con.close();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.sql.UpdateLocTable JD-Core Version: 0.6.2
 */