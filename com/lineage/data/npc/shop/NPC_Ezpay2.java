package com.lineage.data.npc.shop;

import com.lineage.DatabaseFactory;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_GmMessage;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;

public class NPC_Ezpay2 extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(NPC_Ezpay2.class);

    public static NpcExecutor get() {
        return new NPC_Ezpay2();
    }

    /**
     * 領取贊助金額
     *
     */
    private static synchronized void getSponsor(L1PcInstance pc) throws ParseException {
        Connection con = null;
        PreparedStatement pstm = null;
        PreparedStatement pstm1 = null;
        PreparedStatement pstm2 = null;
        PreparedStatement pstm3 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        try {
            String AccountName = pc.getAccountName();
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("select * from ezpay where state = 0 and payname ='" + AccountName + "'");
            rs = pstm.executeQuery();
            boolean isfind = false;
            while (true) {
                if (!rs.next() || rs == null) {
                    break;
                }
                new SimpleDateFormat("yyyy/MM");
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM");
                dateFormat.parse(rs.getString("date"));
                pstm1 = con.prepareStatement("select * from ezpay_pc where account ='" + AccountName + "'");
                rs1 = pstm1.executeQuery();
                String serial = rs.getString("ordernumber");
                int count = rs.getInt("amount");
                // int count_action = rs.getInt("action_count");
                int count_ps = rs.getInt("ps");
                if (pc.getAccountName().equalsIgnoreCase(rs.getString("payname"))) {
                    isfind = true;
                    pstm2 = con.prepareStatement("update ezpay set state = 1 where ordernumber = ?");
                    pstm2.setString(1, serial);
                    pstm2.execute();
                    if (rs1.next() && rs1 != null) {
                        int money = rs1.getInt("amount") + count_ps;
                        pstm3 = con.prepareStatement("update ezpay_pc set amount = " + money + " where account = '" + AccountName + "'");
                        pstm3.execute();
                    } else {
                        pstm3 = con.prepareStatement("insert into ezpay_pc (account,amount) values('" + AccountName + "'," + count_ps + ")");
                        pstm3.execute();
                    }
                    GiveItem(pc, 44070, count, 1);
                }
            }
            if (!isfind) {
                pc.sendPackets(new S_GmMessage("沒有您的贊助資料。 ", "\\aE"));
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage());
            // _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(rs1);
            SQLUtil.close(pstm);
            SQLUtil.close(pstm2);
            SQLUtil.close(pstm3);
            SQLUtil.close(con);
        }
    }

    // 查詢總共累積
    private static synchronized void checkSponsor(L1PcInstance pc) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        PreparedStatement pstm2 = null;
        try {
            String AccountName = pc.getAccountName();
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("select sum(ps) as price from ezpay where payname ='" + AccountName + "'");
            rs = pstm.executeQuery();
            int price = 0;
            while (true) {
                if (!rs.next() || rs == null) {
                    break;
                }
                price = rs.getInt("price");
            }
            pc.sendPackets(new S_GmMessage("目前累積贊助金額: " + price, "\\aE"));
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage());
            // _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(pstm2);
            SQLUtil.close(con);
        }
    }

    public static void GiveItem(L1PcInstance pc, int itemId, int count, int type) {
        L1ItemInstance item = ItemTable.get().createItem(itemId);
        item.setCount(count);
        if (pc.getInventory().checkAddItem(item, count) == 0) {
            pc.getInventory().storeItem(item);
            pc.sendPackets(new S_GmMessage("領取: " + item.getLogName() + "。", "\\aE"));
            toGmMsg(pc, count, type);
        }
    }

    public static void GiveItem_gifts(L1PcInstance pc, int itemId, int count, int type) {
        L1ItemInstance item = ItemTable.get().createItem(itemId);
        item.setCount(count);
        if (pc.getInventory().checkAddItem(item, count) == 0) {
            pc.getInventory().storeItem(item);
            pc.sendPackets(new S_GmMessage("領取: " + item.getLogName() + "。", "\\aE"));
            toGmMsg(pc, count, type);
        }
    }

    /**
     * 通知GM
     */
    private static void toGmMsg(L1PcInstance pc, int adenaCount, int type) {
        try {
            String s = "";
            final Collection<L1PcInstance> allPc = World.get().getAllPlayers();
            if (type == 1) {
                s = "天寶";
            } else if (type == 2) {
                s = "滿額幣";
            } else if (type >= 3) {
                s = "累積禮";
            }
            // LOG紀錄
            writeSponsorlog(pc, adenaCount, s);
            for (L1PcInstance tgpc : allPc) {
                if (tgpc.isGm()) {
                    final StringBuilder topc = new StringBuilder();
                    topc.append("人物:").append(pc.getName()).append("領取").append(s).append(":").append(adenaCount);
                    tgpc.sendPackets(new S_ServerMessage(166, topc.toString()));
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 領取贊助
     *
     */
    public static void writeSponsorlog(L1PcInstance player, int count, String s) {
        try {
            File DeleteLog = new File("./物品操作記錄/log/※以下是玩家[領取贊助]的所有紀錄※.txt");
            BufferedWriter out;
            if (DeleteLog.createNewFile()) {
                out = new BufferedWriter(new FileWriter("./物品操作記錄/log/※以下是玩家[領取贊助]的所有紀錄※.txt", false));
                out.write("※以下是玩家[領取贊助]的所有紀錄※" + "\r\n");
                out.close();
            }
            out = new BufferedWriter(new FileWriter("./物品操作記錄/log/※以下是玩家[領取贊助]的所有紀錄※.txt", true));
            out.write("\r\n");// 每次填寫資料都控一行
            out.write("來自帳號: " + player.getAccountName() + "來自ip: " + player.getNetConnection().getIp() + ",來自玩家: " + player.getName() + ",領取了: " + count + " 個" + s + ",<領取時間:" + new Timestamp(System.currentTimeMillis()) + ">" + "\r\n");
            out.close();
        } catch (IOException e) {
            System.out.println("以下是錯誤訊息: " + e.getMessage());
        }
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_s_0"));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        if (cmd.equalsIgnoreCase("1")) {
            try {
                getSponsor(pc);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (cmd.equalsIgnoreCase("2")) {
            checkSponsor(pc);
        } /*
         * else if (cmd.equalsIgnoreCase("3")){
         *
         * try { getExpDoll(pc); } catch (ParseException e) { // TODO
         * Auto-generated catch block e.printStackTrace(); }
         *
         * }
         */
        pc.sendPackets(new S_CloseList(pc.getId()));
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.shop.NPC_Ezpay JD-Core Version: 0.6.2
 */