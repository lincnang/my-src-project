 /*
  * This program is free software; you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation; either version 2, or (at your option)
  * any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * You should have received a copy of the GNU General Public License
  * along with this program; if not, write to the Free Software
  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
  * 02111-1307, USA.
  *
  * http://www.gnu.org/copyleft/gpl.html
  */
 package com.lineage.server.datatables;

 import com.lineage.DatabaseFactory;
 import com.lineage.server.templates.L1LuckyLottery;
 import com.lineage.server.utils.PerformanceTimer;
 import com.lineage.server.utils.SQLUtil;

 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.logging.Level;
 import java.util.logging.Logger;

 /**
  * @**抽抽樂**
  */
 public class LuckyLotteryTable {
     private static Logger _log = Logger.getLogger(LuckyLotteryTable.class.getName());
     private static LuckyLotteryTable _instance;
     public int total = 0;
     private ArrayList<L1LuckyLottery> _list;

     private LuckyLotteryTable() {
         this._list = new ArrayList<>();
         loadData();
     }

     public static LuckyLotteryTable getInstance() {
         if (_instance == null) {
             _instance = new LuckyLotteryTable();
         }
         return _instance;
     }

     public static void reload() {
         LuckyLotteryTable oldInstance = _instance;
         _instance = new LuckyLotteryTable();
         oldInstance._list.clear();
     }

     public void loadData() {
         final PerformanceTimer timer = new PerformanceTimer();
         Connection con = null;
         PreparedStatement pstm = null;
         ResultSet rs = null;
         try {
             con = DatabaseFactory.get().getConnection();
             pstm = con.prepareStatement("SELECT * FROM 功能_潘朵拉抽獎設置");
             rs = pstm.executeQuery();
             while (rs.next()) {
                 L1LuckyLottery item = new L1LuckyLottery();
                 item.setItem_id(rs.getInt("道具編號"));
                 item.setCount(rs.getInt("數量"));
                 item.setEnchantlvl(rs.getInt("加乘數"));
                 item.setIsbroad(rs.getInt("是否公告") == 1);
                 int value = rs.getInt("機率");
                 item.setRandom(this.total + 1);
                 this.total += value;
                 item.setTotal(this.total);
                 this._list.add(item);
             }
         } catch (SQLException e) {
             _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
         } finally {
             SQLUtil.close(rs);
             SQLUtil.close(pstm);
             SQLUtil.close(con);
         }
         _log.info("讀取->功能_潘朵拉抽獎設置數量: " + _list.size() + "(" + timer.get() + "ms)");
     }

     public ArrayList<L1LuckyLottery> getList() {
         return this._list;
     }
 }