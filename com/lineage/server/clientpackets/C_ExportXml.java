package com.lineage.server.clientpackets;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class C_ExportXml {
    private final Map<Integer, Map<String, String>> allData = new HashMap<Integer, Map<String, String>>();

    public C_ExportXml() {
    }

    public static void write(Map<?, ?> data) {
        try {
            String npcId = (String) data.get("npcId");
            String name = (String) data.get("name");
            String nameId = (String) data.get("nameId");
            String rnd = (String) data.get("rnd");
            String newItem = (String) data.get("newItem");
            String newItemEnchantlv = (String) data.get("newItemEnchantlv");
            String newItemCount = (String) data.get("newItemCount");
            String ncount = (String) data.get("ncount");
            File DeleteLog = new File("./物品操作記錄/log/itemMake.txt");
            BufferedWriter out;
            if (DeleteLog.createNewFile()) {
                out = new BufferedWriter(new FileWriter("./物品操作記錄/log/itemMake.txt", false));
                out.close();
            }
            out = new BufferedWriter(new FileWriter("./物品操作記錄/log/itemMake.txt", true));
            out.newLine();// 換行
            out.write("<Action>");
            out.newLine();// 換行
            out.write("<!--" + name + "-->");
            out.newLine();// 換行
            out.write("<MakeItem NpcId=\"" + npcId + "\" nameId=\"" + nameId + "\" succeedRandom=\"" + rnd + "0000\">");
            out.newLine();// 換行
            for (int i = 0; i < Integer.parseInt(ncount); i++) {
                String needItem = (String) data.get("needItem" + i);
                String needItemEnchantlv = (String) data.get("needenchant" + i);
                String needcounts = (String) data.get("needcounts" + i);
                if (needItemEnchantlv.equals("0")) {
                    out.write("<Material Amount=\"" + needcounts + "\" ItemId=\"" + needItem + "\" />");
                } else {
                    out.write("<Material Amount=\"" + needcounts + "\" ItemId=\"" + needItem + "\" enchantLevel=\"" + needItemEnchantlv + "\"/>");
                }
                out.newLine();// 換行
            }
            out.write("<AidMaterial enchantLevel=\"0\" bless=\"1\" Amount=\"5\" ItemId=\"80027\" ></AidMaterial>");
            out.newLine();// 換行
            if (newItemEnchantlv.equals("0")) {
                out.write("<Item Amount=\"" + newItemCount + "\" ItemId=\"" + newItem + "\" />");
            } else {
                out.write("<Item Amount=\"" + newItemCount + "\" ItemId=\"" + newItem + "\" enchantLevel=\"" + newItemEnchantlv + "\"  />");
            }
            //			<!-- 成功消息 -->
            //			<Succeed broadcast="true" ServerMessageId="79" SystemMessage="恭喜玩家[%1$s]獲得[%2$s]." >
            //				<ShowHtml HtmlId="aldran5" />
            //			</Succeed>
            //
            //			<!-- 失敗消息 -->
            //			<Fail ServerMessageId="" SystemMessage="兌換物品失敗." >
            //				<!-- <ShowHtml HtmlId="aldran4" /> -->
            //			</Fail>
            out.newLine();// 換行
            out.write("<!-- 成功消息 -->");
            out.newLine();// 換行
            out.write("<Succeed broadcast=\"true\" ServerMessageId=\"79\" SystemMessage=\"恭喜玩家[%1$s]獲得[%2$s].\" >");
            out.newLine();// 換行
            out.write("<ShowHtml HtmlId=\"aldran5\" />");
            out.newLine();// 換行
            out.write("</Succeed>");
            out.newLine();// 換行
            out.write("<Fail ServerMessageId=\"\" SystemMessage=\"兌換物品失敗.\" >");
            out.newLine();// 換行
            out.write("<!-- <ShowHtml HtmlId=\"aldran4\" /> -->");
            out.newLine();// 換行
            out.write("</Fail>");
            out.newLine();// 換行
            out.write("</MakeItem>");
            out.newLine();// 換行
            out.write("</Action>");
            out.close();
        } catch (IOException e) {
            System.out.println("以下是錯誤訊息: " + e.getMessage());
        }
    }

    public static void writeTest(Queue<String> str) {
        try {
            File DeleteLog = new File("./物品操作記錄/log/itemArmorTest.txt");
            BufferedWriter out;
            if (DeleteLog.createNewFile()) {
                out = new BufferedWriter(new FileWriter("./物品操作記錄/log/itemArmorTest.txt", false));
                out.close();
            }
            out = new BufferedWriter(new FileWriter("./物品操作記錄/log/itemArmorTest.txt", true));
            out.newLine();// 換行
            if (str.size() > 0) {
                for (final Iterator<String> iter = str.iterator(); iter.hasNext(); ) {
                    final String item = iter.next();// 返回迭代的下一個元素。
                    out.write(item);
                    out.newLine();// 換行
                }
            }
            out.close();
        } catch (IOException e) {
            System.out.println("以下是錯誤訊息: " + e.getMessage());
        }
    }

    public boolean export() {
        System.out.println("Get data start!");
        getData();
        System.out.println("Get data end!");
        File DeleteLog = new File("./log/itemMake.txt");
        if (DeleteLog.exists() && !DeleteLog.isDirectory()) {
            boolean success = DeleteLog.delete();
            System.out.println("delete itemMake:" + success);
        }
        System.out.println("log data start!");
        for (int i = 0; i < allData.size(); i++) {
            Map<?, ?> map = allData.get(i);
            write(map);
        }
        System.out.println("log data end!");
        // System.out.println("get armor data start!");
        // test();
        // System.out.println("get armor data end!");
        return true;
    }

    public void getData() {
        new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int k = 0;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM system_item_blend ");
            rs = pstm.executeQuery();
            while (rs.next()) {
                Map<String, String> dataMap = new HashMap<>();
                int npcid = rs.getInt("npcid");
                int rnd = rs.getInt("rnd");
                int newItem = rs.getInt("new_item");
                int newItemEnchantlv = rs.getInt("new_item_Enchantlvl");
                int newItemCount = rs.getInt("new_item_counts");
                L1ItemInstance tgitem = ItemTable.get().createItem(newItem, false);
                String tableName = "";
                if (tgitem.getItem().getType2() == 0) {
                    tableName = "etcitem";
                } else if (tgitem.getItem().getType2() == 1) {
                    tableName = "weapon";
                } else if (tgitem.getItem().getType2() == 2) {
                    tableName = "armor";
                }
                String[] tmp = getName(tableName, newItem);
                dataMap.put("nameId", tmp[0]);
                dataMap.put("name", tmp[1]);
                dataMap.put("npcId", String.valueOf(npcid));
                dataMap.put("rnd", String.valueOf(rnd));
                dataMap.put("newItem", String.valueOf(newItem));
                dataMap.put("newItemEnchantlv", String.valueOf(newItemEnchantlv));
                dataMap.put("newItemCount", String.valueOf(newItemCount));
                // 材料
                String materials = rs.getString("materials").replaceAll(" ", "");
                final String[] needids_tmp = materials.split(",");
                dataMap.put("ncount", String.valueOf(needids_tmp.length));
                for (int i = 0; i < needids_tmp.length; i++) {
                    dataMap.put("needItem" + i, needids_tmp[i]);
                }
                // 材料數量
                String materials_count = rs.getString("materials_count").replaceAll(" ", "");
                final String[] needcounts_tmp = materials_count.split(",");
                for (int i = 0; i < needcounts_tmp.length; i++) {
                    dataMap.put("needcounts" + i, needcounts_tmp[i]);
                }
                // 材料加成
                String materials_enchantlv = rs.getString("materials_enchants").replaceAll(" ", "");
                final String[] needenchant_tmp = materials_enchantlv.split(",");
                for (int i = 0; i < needenchant_tmp.length; i++) {
                    dataMap.put("needenchant" + i, needenchant_tmp[i]);
                }
                allData.put(k, dataMap);
                k++;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    private String[] getName(String tableName, int itemID) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        String[] tmp = new String[2];
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT name,name_id FROM " + tableName + " where item_Id=" + itemID);
            rs = pstm.executeQuery();
            if (rs.next()) {
                String nameId = rs.getString("name_id").substring(1);
                String name = rs.getString("name");
                tmp[0] = nameId;
                tmp[1] = name;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return tmp;
    }

    private Map<Integer, String[]> getNameAll(String tableName) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        HashMap<Integer, String[]> map = new HashMap<Integer, String[]>();
        String[] tmp = new String[3];
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT item_id,name,name_id FROM " + tableName);
            rs = pstm.executeQuery();
            int i = 0;
            while (rs.next()) {
                String nameId = rs.getString("name_id").substring(1);
                String name = rs.getString("name");
                int itemId = rs.getInt("item_id");
                tmp[0] = nameId;
                tmp[1] = name;
                tmp[2] = String.valueOf(itemId);
                map.put(i, tmp);
                i++;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return map;
    }

    public void test() {
        Map<Integer, String[]> map = getNameAll("armor");
        Queue<String> itemListX = new ConcurrentLinkedQueue<String>();
        for (int i = 0; i < map.size(); i++) {
            String[] value = (String[]) map.get(i);
            String itemId = value[2];
            String name = value[1];
            String nameId = value[0];
            final L1Item item = ItemTable.get().getTemplate(Integer.parseInt(itemId));
            System.out.println(itemId + "," + name + "," + nameId + "," + item.getNameId());
            itemListX.offer(itemId + "-" + name + "-" + nameId + "(" + item.getNameId() + ")");
        }
        writeTest(itemListX);
        itemListX.clear();
        /*
         * final L1Item item = ItemTable.get().getTemplate(20001);
         *
         * System.out.println("test:"+item.getNameId());
         */
    }
}
