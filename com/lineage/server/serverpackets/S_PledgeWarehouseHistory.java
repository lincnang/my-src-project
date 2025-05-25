/**
 * License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE").
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR
 * COPYRIGHT LAW IS PROHIBITED.
 * <p>
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND
 * AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE
 * MAY BE CONSIDERED TO BE A CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 */
package com.lineage.server.serverpackets;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.SQLUtil;

import java.sql.*;
//Referenced classes of package l1j.server.server.serverpackets:
//ServerBasePacket

/**
 * 處理查詢血盟倉庫使用紀錄的封包
 */
public class S_PledgeWarehouseHistory extends ServerBasePacket {
    private static final String S_PledgeWarehouseHistory = "[S] S_PledgeWarehouseHistory";
    private byte[] _byte = null;

    public S_PledgeWarehouseHistory(int clanId) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            // 刪除超過3天的紀錄
            pstm = con.prepareStatement("DELETE FROM clan_warehouse_history WHERE clan_id=? AND record_time < ?");
            pstm.setInt(1, clanId);
            pstm.setTimestamp(2, new Timestamp(System.currentTimeMillis() - 259200000));
            pstm.execute();
            pstm.close();
            // 查詢紀錄
            pstm = con.prepareStatement("SELECT * FROM clan_warehouse_history WHERE clan_id=? ORDER BY id DESC");
            pstm.setInt(1, clanId);
            rs = pstm.executeQuery();
            rs.last(); // 為了取得總列數，先將指標拉到最後
            int rowcount = rs.getRow(); // 取得總列數
            rs.beforeFirst(); // 將指標移回最前頭
            /** 封包部分 */
            writeC(S_EVENT);
            writeC(S_PacketBox.HTML_CLAN_WARHOUSE_RECORD);
            writeD(rowcount); // 總共筆數
            while (rs.next()) {
                writeS(rs.getString("char_name"));
                writeC(rs.getInt("type")); // 領出: 1, 存入: 0
                writeS(rs.getString("item_name")); // 物品名稱
                writeD(rs.getInt("item_count")); // 物品數量
                writeD((int) ((System.currentTimeMillis() - rs.getTimestamp("record_time").getTime()) / 60000)); // 過了幾分鐘
            }
        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = _bao.toByteArray();
        }
        return _byte;
    }

    @Override
    public String getType() {
        return S_PledgeWarehouseHistory;
    }
}
