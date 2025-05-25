package william;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GiveBack {
    public static final List<Integer> savepcid = new ArrayList<>();
    public static final List<L1ItemInstance> saveweapon = new ArrayList<>();
    public static final List<String> savename = new ArrayList<>();

    public static final Map<Integer, Integer> npc_itemid = new HashMap<>();
    public static final Map<Integer, Integer> npc_itemcount = new HashMap<>();

    public static void load() {
        Connection conn = null;
        try {
            conn = DatabaseFactory.get().getConnection();
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery("SELECT * FROM 系統_衝裝贖回設定");

            while ((rs != null) && (rs.next())) {
                int npcid = rs.getInt("npcid");
                int itemid = rs.getInt("itemid");
                int itemcount = rs.getInt("count");

                npc_itemid.put(npcid, itemid);
                npc_itemcount.put(npcid, itemcount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void addRecord(L1PcInstance pc, L1ItemInstance item) {
        int pcId = pc.getId();
        savepcid.add(pcId);
        saveweapon.add(item);
        savename.add(item.getNumberedViewName(1L));

        trimOldestIfExceeded(pcId);
    }

    private static void trimOldestIfExceeded(int pcId) {
        int count = 0;
        for (Integer id : savepcid) {
            if (id == pcId) {
                count++;
            }
        }

        if (count > 3) {
            for (int i = 0; i < savepcid.size(); i++) {
                if (savepcid.get(i) == pcId) {
                    savepcid.remove(i);
                    saveweapon.remove(i);
                    savename.remove(i);
                    break;
                }
            }
        }
    }
}
