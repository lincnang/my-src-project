package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.storage.OtherUserTradeStorage;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class OtherUserTradeTable implements OtherUserTradeStorage {
    private static final Log _log = LogFactory.getLog(OtherUserTradeTable.class);

    public void add(String itemname, int itemobjid, int itemadena, long itemcount, int pcobjid, String pcname, int srcpcobjid, String srcpcname) {
        Connection co = null;
        PreparedStatement ps = null;
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement("INSERT INTO `日誌_刪物紀錄` SET `itemname`=?,`itemobjid`=?,`itemadena`=?,`itemcount`=?,`pcobjid`=?,`pcname`=?,`srcpcobjid`=?,`srcpcname`=?,`datetime`=SYSDATE()");
            int i = 0;
            ps.setString(++i, itemname);
            ps.setInt(++i, itemobjid);
            ps.setInt(++i, itemadena);
            ps.setLong(++i, itemcount);
            ps.setInt(++i, pcobjid);
            ps.setString(++i, "移入人物:" + pcname);
            ps.setInt(++i, srcpcobjid);
            ps.setString(++i, "移出人物:" + srcpcname);
            ps.execute();
        } catch (Exception e) {
            SqlError.isError(_log, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(co);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.sql.OtherUserTradeTable JD-Core Version: 0.6.2
 */