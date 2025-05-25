package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.storage.ServerCnInfoStorage;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ServerCnInfoTable implements ServerCnInfoStorage {
    private static final Log _log = LogFactory.getLog(ServerCnInfoTable.class);

    public void create(L1PcInstance pc, L1Item itemtmp, long count, boolean mode) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            Timestamp lastactive = new Timestamp(System.currentTimeMillis());
            con = DatabaseFactory.get().getConnection();
            String sqlstr = "INSERT INTO `日誌_商店購買紀錄` SET `itemname`=?,`itemid`=?,`selling_price`=?,`time`=?,`pcobjid`=?,`mode`=?";
            pstm = con.prepareStatement(sqlstr);
            int i = 0;
            String pcinfo = "(玩家)";
            if (pc.isGm()) {
                pcinfo = "(管理者)";
            }
            pstm.setString(++i, itemtmp.getName() + pcinfo);
            pstm.setInt(++i, itemtmp.getItemId());
            pstm.setLong(++i, count);
            pstm.setTimestamp(++i, lastactive);
            pstm.setInt(++i, pc.getId());
            pstm.setBoolean(++i, mode);
            pstm.execute();
            if (mode) {
                _log.info("建立商城紀錄 人物:" + pc.getName() + " 買入:" + itemtmp.getName() + " 花費商城幣:" + count);
            } else {
                _log.info("建立商城紀錄 人物:" + pc.getName() + " 賣出:" + itemtmp.getName() + " 獲得商城幣:" + count);
            }
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
 * com.lineage.server.datatables.sql.ServerCnInfoTable JD-Core Version: 0.6.2
 */