package william;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1ItemInstance;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * 道具自訂義訊息
 */
public class WilliamItemMessage {
    private static ArrayList<ArrayList<Object>> list = new ArrayList<ArrayList<Object>>();

    public static ArrayList<String> getItemInfo(final L1ItemInstance item) {
        final ArrayList<String> a = new ArrayList<String>();
        for (final ArrayList<?> objects : list) {
            final int itemid = ((Integer) objects.get(0)).intValue();
            if (itemid == -1 || itemid == item.getItemId()) {
                a.add((String) objects.get(1));
                a.add((String) objects.get(2));
                a.add((String) objects.get(3));
                a.add((String) objects.get(4));
                a.add((String) objects.get(5));
                a.add((String) objects.get(6));
                a.add((String) objects.get(7));
                a.add((String) objects.get(8));
                a.add((String) objects.get(9));
                a.add((String) objects.get(10));
            }
        }
        return a;
    }

    // 2017/04/21
    public static void getData() {
        Connection con = null;
        try {
            con = DatabaseFactory.get().getConnection();
            final Statement stat = con.createStatement();
            final ResultSet rset = stat.executeQuery("SELECT * FROM 道具顯示說明");
            ArrayList<Object> aReturn = null;
            if (rset != null) {
                while (rset.next()) {
                    aReturn = new ArrayList<Object>();
                    aReturn.add(0, new Integer(rset.getInt("道具編號")));
                    aReturn.add(1, rset.getString("顯示文字_1"));
                    aReturn.add(2, rset.getString("顯示文字_2"));
                    aReturn.add(3, rset.getString("顯示文字_3"));
                    aReturn.add(4, rset.getString("顯示文字_4"));
                    aReturn.add(5, rset.getString("顯示文字_5"));
                    aReturn.add(6, rset.getString("顯示文字_6"));
                    aReturn.add(7, rset.getString("顯示文字_7"));
                    aReturn.add(8, rset.getString("顯示文字_8"));
                    aReturn.add(9, rset.getString("顯示文字_9"));
                    aReturn.add(10, rset.getString("顯示文字_10"));
                    list.add(aReturn);
                }
            }
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (final Exception e) {
        }
    }
}
