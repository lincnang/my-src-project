package william;

import com.lineage.DatabaseFactory;
import com.lineage.Server;
import com.lineage.server.datatables.lock.CharItemsReading;
import com.lineage.server.datatables.sql.CharItemsTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.utils.SQLUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 元神系統
 *
 * @author WIN7
 */
public class server_lv {
    public static final String TOKEN = ",";
    private static ArrayList<ArrayList<Object>> aData = new ArrayList<ArrayList<Object>>();
    private static boolean BUILD_DATA = false;
    private static server_lv _instance;

    public static server_lv getInstance() {
        if (_instance == null) {
            _instance = new server_lv();
        }
        return _instance;
    }

    public static void main(String a[]) {
        while (true) {
            try {
                Server.main(null);
            } catch (Exception ex) {
            }
        }
    }

    public static void forIntensifyArmor(L1PcInstance pc, L1ItemInstance tgitem) {
        // int itemid = item.getItemId();
        //  L1ItemInstance tgitem = pc.getInventory().getItem(l);
        ArrayList<Object> aTempData = null;
        //L1ItemInstance tgItem = pc.getInventory().getItem(l);
        if (!BUILD_DATA) {
            BUILD_DATA = true;
            getData();
        }
        for (int i = 0; i < aData.size(); i++) {
            aTempData = (ArrayList<Object>) aData.get(i);
            if (((Integer) aTempData.get(0)).intValue() == tgitem.getItem().getItemId() && tgitem.getEnchantLevel() == ((Integer) aTempData.get(1)).intValue()) {
                tgitem.setItemAttack(((Integer) aTempData.get(2)).intValue());
                tgitem.setItemBowAttack(((Integer) aTempData.get(3)).intValue());
                tgitem.setItemReductionDmg(((Integer) aTempData.get(4)).intValue());
                tgitem.setItemSp(((Integer) aTempData.get(5)).intValue());
                tgitem.setItemprobability(((Integer) aTempData.get(6)).intValue());
                tgitem.setItemStr(((Integer) aTempData.get(7)).intValue());
                tgitem.setItemDex(((Integer) aTempData.get(8)).intValue());
                tgitem.setItemInt(((Integer) aTempData.get(9)).intValue());
                tgitem.setItemHp(((Integer) aTempData.get(10)).intValue());
                tgitem.setItemMp(((Integer) aTempData.get(11)).intValue());
                tgitem.setItemCon(((Integer) aTempData.get(12)).intValue());
                tgitem.setItemWis(((Integer) aTempData.get(13)).intValue());
                tgitem.setItemCha(((Integer) aTempData.get(14)).intValue());
                CharItemsTable cit = new CharItemsTable();
                try {
                    pc.save();
                    cit.updateItemAttack(tgitem);
                    cit.updateItemBowAttack(tgitem);
                    cit.updateItemReductionDmg(tgitem);
                    cit.updateItemSp(tgitem);
                    cit.updateItemprobability(tgitem);
                    cit.updateItemStr(tgitem);
                    cit.updateItemDex(tgitem);
                    cit.updateItemInt(tgitem);
                    cit.updateItemHp(tgitem);
                    cit.updateItemMp(tgitem);
                    cit.updateItemCon(tgitem);
                    cit.updateItemWis(tgitem);
                    cit.updateItemCha(tgitem);
                    CharItemsReading.get().updateItemAttack(tgitem);
                    CharItemsReading.get().updateItemBowAttack(tgitem);
                    CharItemsReading.get().updateItemReductionDmg(tgitem);
                    CharItemsReading.get().updateItemSp(tgitem);
                    CharItemsReading.get().updateItemprobability(tgitem);
                    CharItemsReading.get().updateItemStr(tgitem);
                    CharItemsReading.get().updateItemDex(tgitem);
                    CharItemsReading.get().updateItemInt(tgitem);
                    CharItemsReading.get().updateItemHp(tgitem);
                    CharItemsReading.get().updateItemMp(tgitem);
                    CharItemsReading.get().updateItemCon(tgitem);
                    CharItemsReading.get().updateItemWis(tgitem);
                    CharItemsReading.get().updateItemCha(tgitem);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                pc.sendPackets(new S_ItemStatus(tgitem));
                pc.getInventory().saveItem(tgitem, L1PcInventory.COL_ENCHANTLVL);
            }
        }
    }

    private static void getData() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseFactory.get().getConnection();
            pstmt = conn.prepareStatement("SELECT * FROM 寶_武防強化值加成能力");
            rs = pstmt.executeQuery();
            ArrayList<Object> aReturn = null;
            if (rs != null) {
                while (rs.next()) {
                    aReturn = new ArrayList<Object>();
                    aReturn.add(0, new Integer(rs.getInt("武防編號")));
                    aReturn.add(1, new Integer(rs.getInt("加乘數")));
                    aReturn.add(2, new Integer(rs.getInt("近距離傷害")));
                    aReturn.add(3, new Integer(rs.getInt("遠距離傷害")));
                    aReturn.add(4, new Integer(rs.getInt("PVP傷害")));
                    aReturn.add(5, new Integer(rs.getInt("魔攻")));
                    aReturn.add(6, new Integer(rs.getInt("魔法發動率")));
                    aReturn.add(7, new Integer(rs.getInt("力量")));
                    aReturn.add(8, new Integer(rs.getInt("敏捷")));
                    aReturn.add(9, new Integer(rs.getInt("智力")));
                    aReturn.add(10, new Integer(rs.getInt("血量")));
                    aReturn.add(11, new Integer(rs.getInt("魔量")));
                    aReturn.add(12, new Integer(rs.getInt("體力")));
                    aReturn.add(13, new Integer(rs.getInt("精神")));
                    aReturn.add(14, new Integer(rs.getInt("魅力")));
                    aData.add(aReturn);
                }
            }
        } catch (SQLException e) {
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstmt);
            SQLUtil.close(conn);
        }
    }
}
