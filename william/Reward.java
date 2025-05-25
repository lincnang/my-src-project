package william;

import com.lineage.DatabaseFactory;
import com.lineage.Server;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Reward {
    public static final String TOKEN = ",";
    private static ArrayList<ArrayList<Object>> array = new ArrayList<>();
    private static boolean GET_ITEM = false;

    public static void main(String[] a) {
        try {
            Server.main(null);
        } catch (Exception localException) {
        }
    }

    public static void getItem(L1PcInstance pc) {
        ArrayList<?> data = null;
        if (!GET_ITEM) {
            GET_ITEM = true;
            getItemData();
        }
        for (ArrayList<Object> objects : array) {
            data = (ArrayList<?>) objects;
            if ((pc.getLevel() >= (Integer) data.get(0)) && ((int[]) data.get(9) != null) && ((int[]) data.get(10) != null) && ((int[]) data.get(11) != null) && (pc.getQuest().get_step((Integer) data.get(12)) != (Integer) data.get(13))) {
                if (((Integer) data.get(1) != 0) && (pc.isCrown())) {
                    boolean isGet = false;
                    int[] materials = (int[]) data.get(9);
                    int[] counts = (int[]) data.get(10);
                    int[] enchantLevel = (int[]) data.get(11);
                    for (int j = 0; j < materials.length; j++) {
                        L1ItemInstance item = ItemTable.get().createItem(materials[j]);
                        if (item.isStackable()) {
                            item.setCount(counts[j]);
                        } else {
                            item.setCount(1L);
                        }
                        if ((item.getItem().getType2() == 1) || (item.getItem().getType2() == 2)) {
                            item.setEnchantLevel(enchantLevel[j]);
                        } else {
                            item.setEnchantLevel(0);
                        }
                        if (item != null) {
                            if (((String) data.get(14) != null) && (!isGet)) {
                                pc.sendPackets(new S_SystemMessage((String) data.get(14)));
                                isGet = true;
                            }
                            if (pc.getInventory().checkAddItem(item, counts[j]) == 0) {
                                pc.getInventory().storeItem(item);
                            } else {
                                World.get().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
                            }
                            pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                            pc.getQuest().set_step((Integer) data.get(12), (Integer) data.get(13));
                        }
                    }
                }
                if (((Integer) data.get(2) != 0) && (pc.isKnight())) {
                    boolean isGet = false;
                    int[] materials = (int[]) data.get(9);
                    int[] counts = (int[]) data.get(10);
                    int[] enchantLevel = (int[]) data.get(11);
                    for (int j = 0; j < materials.length; j++) {
                        L1ItemInstance item = ItemTable.get().createItem(materials[j]);
                        if (item.isStackable()) {
                            item.setCount(counts[j]);
                        } else {
                            item.setCount(1L);
                        }
                        if ((item.getItem().getType2() == 1) || (item.getItem().getType2() == 2)) {
                            item.setEnchantLevel(enchantLevel[j]);
                        } else {
                            item.setEnchantLevel(0);
                        }
                        if (item != null) {
                            if (((String) data.get(14) != null) && (!isGet)) {
                                pc.sendPackets(new S_SystemMessage((String) data.get(14)));
                                isGet = true;
                            }
                            if (pc.getInventory().checkAddItem(item, counts[j]) == 0) {
                                pc.getInventory().storeItem(item);
                            } else {
                                World.get().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
                            }
                            pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                            pc.getQuest().set_step((Integer) data.get(12), (Integer) data.get(13));
                        }
                    }
                }
                if (((Integer) data.get(3) != 0) && (pc.isWizard())) {
                    boolean isGet = false;
                    int[] materials = (int[]) data.get(9);
                    int[] counts = (int[]) data.get(10);
                    int[] enchantLevel = (int[]) data.get(11);
                    for (int j = 0; j < materials.length; j++) {
                        L1ItemInstance item = ItemTable.get().createItem(materials[j]);
                        if (item.isStackable()) {
                            item.setCount(counts[j]);
                        } else {
                            item.setCount(1L);
                        }
                        if ((item.getItem().getType2() == 1) || (item.getItem().getType2() == 2)) {
                            item.setEnchantLevel(enchantLevel[j]);
                        } else {
                            item.setEnchantLevel(0);
                        }
                        if (item != null) {
                            if (((String) data.get(14) != null) && (!isGet)) {
                                pc.sendPackets(new S_SystemMessage((String) data.get(14)));
                                isGet = true;
                            }
                            if (pc.getInventory().checkAddItem(item, counts[j]) == 0) {
                                pc.getInventory().storeItem(item);
                            } else {
                                World.get().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
                            }
                            pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                            pc.getQuest().set_step((Integer) data.get(12), (Integer) data.get(13));
                        }
                    }
                }
                if (((Integer) data.get(4) != 0) && (pc.isElf())) {
                    boolean isGet = false;
                    int[] materials = (int[]) data.get(9);
                    int[] counts = (int[]) data.get(10);
                    int[] enchantLevel = (int[]) data.get(11);
                    for (int j = 0; j < materials.length; j++) {
                        L1ItemInstance item = ItemTable.get().createItem(materials[j]);
                        if (item.isStackable()) {
                            item.setCount(counts[j]);
                        } else {
                            item.setCount(1L);
                        }
                        if ((item.getItem().getType2() == 1) || (item.getItem().getType2() == 2)) {
                            item.setEnchantLevel(enchantLevel[j]);
                        } else {
                            item.setEnchantLevel(0);
                        }
                        if (item != null) {
                            if (((String) data.get(14) != null) && (!isGet)) {
                                pc.sendPackets(new S_SystemMessage((String) data.get(14)));
                                isGet = true;
                            }
                            if (pc.getInventory().checkAddItem(item, counts[j]) == 0) {
                                pc.getInventory().storeItem(item);
                            } else {
                                World.get().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
                            }
                            pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                            pc.getQuest().set_step((Integer) data.get(12), (Integer) data.get(13));
                        }
                    }
                }
                if (((Integer) data.get(5) != 0) && (pc.isDarkelf())) {
                    boolean isGet = false;
                    int[] materials = (int[]) data.get(9);
                    int[] counts = (int[]) data.get(10);
                    int[] enchantLevel = (int[]) data.get(11);
                    for (int j = 0; j < materials.length; j++) {
                        L1ItemInstance item = ItemTable.get().createItem(materials[j]);
                        if (item.isStackable()) {
                            item.setCount(counts[j]);
                        } else {
                            item.setCount(1L);
                        }
                        if ((item.getItem().getType2() == 1) || (item.getItem().getType2() == 2)) {
                            item.setEnchantLevel(enchantLevel[j]);
                        } else {
                            item.setEnchantLevel(0);
                        }
                        if (item != null) {
                            if (((String) data.get(14) != null) && (!isGet)) {
                                pc.sendPackets(new S_SystemMessage((String) data.get(14)));
                                isGet = true;
                            }
                            if (pc.getInventory().checkAddItem(item, counts[j]) == 0) {
                                pc.getInventory().storeItem(item);
                            } else {
                                World.get().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
                            }
                            pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                            pc.getQuest().set_step((Integer) data.get(12), (Integer) data.get(13));
                        }
                    }
                }
                if (((Integer) data.get(6) != 0) && (pc.isDragonKnight())) {
                    boolean isGet = false;
                    int[] materials = (int[]) data.get(9);
                    int[] counts = (int[]) data.get(10);
                    int[] enchantLevel = (int[]) data.get(11);
                    for (int j = 0; j < materials.length; j++) {
                        L1ItemInstance item = ItemTable.get().createItem(materials[j]);
                        if (item.isStackable()) {
                            item.setCount(counts[j]);
                        } else {
                            item.setCount(1L);
                        }
                        if ((item.getItem().getType2() == 1) || (item.getItem().getType2() == 2)) {
                            item.setEnchantLevel(enchantLevel[j]);
                        } else {
                            item.setEnchantLevel(0);
                        }
                        if (item != null) {
                            if (((String) data.get(14) != null) && (!isGet)) {
                                pc.sendPackets(new S_SystemMessage((String) data.get(14)));
                                isGet = true;
                            }
                            if (pc.getInventory().checkAddItem(item, counts[j]) == 0) {
                                pc.getInventory().storeItem(item);
                            } else {
                                World.get().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
                            }
                            pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                            pc.getQuest().set_step((Integer) data.get(12), (Integer) data.get(13));
                        }
                    }
                }
                if (((Integer) data.get(7) != 0) && (pc.isIllusionist())) {
                    boolean isGet = false;
                    int[] materials = (int[]) data.get(9);
                    int[] counts = (int[]) data.get(10);
                    int[] enchantLevel = (int[]) data.get(11);
                    for (int j = 0; j < materials.length; j++) {
                        L1ItemInstance item = ItemTable.get().createItem(materials[j]);
                        if (item.isStackable()) {
                            item.setCount(counts[j]);
                        } else {
                            item.setCount(1L);
                        }
                        if ((item.getItem().getType2() == 1) || (item.getItem().getType2() == 2)) {
                            item.setEnchantLevel(enchantLevel[j]);
                        } else {
                            item.setEnchantLevel(0);
                        }
                        if (item != null) {
                            if (((String) data.get(14) != null) && (!isGet)) {
                                pc.sendPackets(new S_SystemMessage((String) data.get(14)));
                                isGet = true;
                            }
                            if (pc.getInventory().checkAddItem(item, counts[j]) == 0) {
                                pc.getInventory().storeItem(item);
                            } else {
                                World.get().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
                            }
                            pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                            pc.getQuest().set_step((Integer) data.get(12), (Integer) data.get(13));
                        }
                    }
                }
                if (((Integer) data.get(8) != 0) && (pc.isWarrior())) {
                    boolean isGet = false;
                    int[] materials = (int[]) data.get(9);
                    int[] counts = (int[]) data.get(10);
                    int[] enchantLevel = (int[]) data.get(11);
                    for (int j = 0; j < materials.length; j++) {
                        L1ItemInstance item = ItemTable.get().createItem(materials[j]);
                        if (item.isStackable()) {
                            item.setCount(counts[j]);
                        } else {
                            item.setCount(1L);
                        }
                        if ((item.getItem().getType2() == 1) || (item.getItem().getType2() == 2)) {
                            item.setEnchantLevel(enchantLevel[j]);
                        } else {
                            item.setEnchantLevel(0);
                        }
                        if (item != null) {
                            if (((String) data.get(14) != null) && (!isGet)) {
                                pc.sendPackets(new S_SystemMessage((String) data.get(14)));
                                isGet = true;
                            }
                            if (pc.getInventory().checkAddItem(item, counts[j]) == 0) {
                                pc.getInventory().storeItem(item);
                            } else {
                                World.get().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
                            }
                            pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                            pc.getQuest().set_step((Integer) data.get(12), (Integer) data.get(13));
                        }
                    }
                }
            }
        }
    }

    private static void getItemData() {
        Connection con = null;
        try {
            con = DatabaseFactory.get().getConnection();
            Statement stat = con.createStatement();
            ResultSet rset = stat.executeQuery("SELECT * FROM 系統_等級升級獎勵");
            ArrayList<Object> arraylist = null;
            if (rset != null) {
                while (rset.next()) {
                    arraylist = new ArrayList<>();
                    arraylist.add(0, rset.getInt("level"));
                    arraylist.add(1, rset.getInt("give_royal"));
                    arraylist.add(2, rset.getInt("give_knight"));
                    arraylist.add(3, rset.getInt("give_mage"));
                    arraylist.add(4, rset.getInt("give_elf"));
                    arraylist.add(5, rset.getInt("give_darkelf"));
                    arraylist.add(6, rset.getInt("give_dragonKnight"));
                    arraylist.add(7, rset.getInt("give_illusionist"));
                    arraylist.add(8, rset.getInt("give_warrior"));
                    arraylist.add(9, getArray(rset.getString("getItem"), ",", 1));
                    arraylist.add(10, getArray(rset.getString("count"), ",", 1));
                    arraylist.add(11, getArray(rset.getString("enchantlvl"), ",", 1));
                    arraylist.add(12, rset.getInt("quest_id"));
                    arraylist.add(13, rset.getInt("quest_step"));
                    arraylist.add(14, rset.getString("message"));
                    array.add(arraylist);
                }
            }
            if ((con != null) && (!con.isClosed())) {
                con.close();
            }
        } catch (Exception localException) {
        }
    }

    private static Object getArray(String s, String sToken, int iType) {
        StringTokenizer st = new StringTokenizer(s, sToken);
        int iSize = st.countTokens();
        String sTemp = null;
        if (iType == 1) {
            int[] iReturn = new int[iSize];
            for (int i = 0; i < iSize; i++) {
                sTemp = st.nextToken();
                iReturn[i] = Integer.parseInt(sTemp);
            }
            return iReturn;
        }
        if (iType == 2) {
            String[] sReturn = new String[iSize];
            for (int i = 0; i < iSize; i++) {
                sTemp = st.nextToken();
                sReturn[i] = sTemp;
            }
            return sReturn;
        }
        if (iType == 3) {
            String sReturn = null;
            for (int i = 0; i < iSize; i++) {
                sTemp = st.nextToken();
                sReturn = sTemp;
            }
            return sReturn;
        }
        return null;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * william.Reward JD-Core Version: 0.6.2
 */