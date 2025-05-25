package william;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * 首儲送禮<BR>
 * 維護:冰雕寵兒
 */
@SuppressWarnings("unused")
public class PayBonus {
    public static void getItem(L1PcInstance pc, long count) {
        Connection conn = null;
        try {
            conn = DatabaseFactory.get().getConnection();
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery("SELECT * FROM shop_user_累存");
            ArrayList<?> arraylist = null;
            int nowb = pc.get_other().get_getbonus();
            if (rs != null) {
                while (rs.next()) {
                    int money = rs.getInt("滿額金額");
                    int itemid = rs.getInt("物品編號");
                    int itemcount = rs.getInt("物品數量");
                    if (((int) count + nowb >= money) && (nowb < money)) {
                        L1ItemInstance items = pc.getInventory().storeItem(itemid, itemcount);
                        pc.sendPackets(new S_ServerMessage("\\fW獲得贊助滿" + money + "好禮:" + items.getName() + "(" + itemcount + ")"));
                    }
                }
            }
            pc.get_other().set_getbonus(pc.get_other().get_getbonus() + (int) count);
            if ((conn != null) && (!conn.isClosed())) {
                conn.close();
            }
        } catch (Exception ex) {
        }
    }
}
