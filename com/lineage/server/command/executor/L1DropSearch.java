package com.lineage.server.command.executor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.utils.SQLUtil;

/**
 * 查詢掉落物品與機率
 */
public class L1DropSearch implements L1CommandExecutor {

    private static final Log _log = LogFactory.getLog(L1DropSearch.class);

    private L1DropSearch() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1DropSearch();
    }

    @Override
    public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
        try {
            if (arg == null || arg.isEmpty()) {
                pc.sendPackets(new S_SystemMessage("請輸入查詢對象名稱 (例如: .掉落 對武器施法)"));
                return;
            }

            findDrop(pc, arg);

        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
            pc.sendPackets(new S_SystemMessage("指令執行錯誤。"));
        }
    }

    private void findDrop(L1PcInstance pc, String name) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<FoundItem> foundItems = new ArrayList<>();

        try {
            con = DatabaseFactory.get().getConnection();
            
            // 搜尋 weapon, armor, etcitem
            String sql = "SELECT item_id, name FROM weapon WHERE name LIKE ? " +
                         "UNION ALL " +
                         "SELECT item_id, name FROM armor WHERE name LIKE ? " +
                         "UNION ALL " +
                         "SELECT item_id, name FROM etcitem WHERE name LIKE ?";
            
            pstm = con.prepareStatement(sql);
            String searchName = "%" + name + "%";
            pstm.setString(1, searchName);
            pstm.setString(2, searchName);
            pstm.setString(3, searchName);
            
            rs = pstm.executeQuery();
            while (rs.next()) {
                foundItems.add(new FoundItem(rs.getInt("item_id"), rs.getString("name")));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
            pc.sendPackets(new S_SystemMessage("資料庫查詢錯誤。"));
            return;
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        
        if (foundItems.isEmpty()) {
            pc.sendPackets(new S_SystemMessage("找不到名稱包含 '" + name + "' 的物品。"));
            return;
        }

        // 查詢掉落資料
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT mobId, chance FROM droplist WHERE itemId = ? ORDER BY chance DESC");
            // 格式化機率顯示，顯示到小數點後6位 (因為百萬分之一)
            DecimalFormat df = new DecimalFormat("#.######%"); 

            int totalDropsFound = 0;

            for (FoundItem item : foundItems) {
                pstm.setInt(1, item.id);
                rs = pstm.executeQuery();
                boolean hasDrop = false;
                while (rs.next()) {
                     int mobId = rs.getInt("mobId");
                     int chance = rs.getInt("chance");
                     String mobName = NpcTable.get().getNpcName(mobId);
                     
                     if (mobName != null) {
                         double rate = (double) chance / 1000000.0;
                         // 這裡S_SystemMessage格式依需求調整
                         pc.sendPackets(new S_SystemMessage(item.name + " -> " + mobName + " (機率: " + df.format(rate) + ")"));
                         hasDrop = true;
                         totalDropsFound++;
                     }
                }
                rs.close();
            }
            
            if (totalDropsFound == 0) {
                pc.sendPackets(new S_SystemMessage("符合的物品目前沒有掉落設定。"));
            }

        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
            pc.sendPackets(new S_SystemMessage("查詢掉落資料時發生錯誤。"));
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }
    
    private static class FoundItem {
        int id;
        String name;
        public FoundItem(int id, String name) { 
            this.id = id; 
            this.name = name; 
        }
    }
}
