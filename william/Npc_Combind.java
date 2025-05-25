package william;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1DollInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.utils.RandomArrayList;
import com.lineage.server.world.World;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

public class Npc_Combind {

    public static final String TOKEN = ",";
    private static ArrayList aData = new ArrayList();
    private static boolean NO_GET_DATA = false;

    public Npc_Combind() {
    }

    public static boolean forNpcQuest(String s, L1PcInstance pc, L1NpcInstance npc, int npcid, int oid) {
        ArrayList aTempData = null;
        if (!NO_GET_DATA) {
            NO_GET_DATA = true;
            getData();
        }
        for (int i = 0; i < aData.size(); i++) {
            aTempData = (ArrayList) aData.get(i);
            Random _random = new Random();
            if (aTempData.get(0) == null || ((Integer) aTempData.get(0)).intValue() != npcid || !((String) aTempData.get(1)).equals(s))
                continue;
            ArrayList cousumedolls = new ArrayList();
            boolean enough = false;
            // 取得需要消耗的物品ID陣列和給予的物品ID陣列
            int olddolls[] = (int[]) (int[]) aTempData.get(2);
            int giveItem[] = (int[]) (int[]) aTempData.get(5);
            // 隨機選擇一個給予的物品
            int rndItem = RandomArrayList.getInt(giveItem.length);
            int giveItemGet = giveItem[rndItem];
            // 取得需要的物品數量
            int needcount = ((Integer) aTempData.get(3)).intValue();
            int q = 0;
            do {
                if (q >= olddolls.length)
                    break;
                // 搜尋玩家揹包中指定ID的物品
                L1ItemInstance dolls[] = pc.getInventory().findItemsId(olddolls[q]);
                if (dolls != null) {
                    for (int c = 0; c < dolls.length; c++) {
                        int itemid = dolls[c].getItemId();
                        cousumedolls.add(Integer.valueOf(itemid));
                        // 如果已經達到需要的數量，則停止
                        if (cousumedolls.size() == needcount)
                            break;
                        // 如果玩家沒有任何娃娃，則繼續
                        if (pc.getDolls().isEmpty())
                            continue;
                        // 取得所有娃娃並刪除
                        Object dolls2[] = pc.getDolls().values().toArray();
                        Object array[];
                        int length = (array = dolls2).length;
                        for (int j = 0; j < length; j++) {
                            Object obj = array[j];
                            L1DollInstance doll = (L1DollInstance) obj;
                            if (doll != null)
                                doll.deleteDoll();
                        }
                        // 清空玩家的娃娃列表
                        pc.getDolls().clear();
                    }

                }
                // 如果已經收集到足夠的物品，則跳出迴圈
                if (cousumedolls.size() == needcount)
                    break;
                q++;
            } while (true);
            // 如果收集到足夠的物品
            if (cousumedolls.size() == needcount) {
                enough = true;
                // 嘗試消耗玩家揹包中的物品
                if (pc.getInventory().consumeItemsIdArray(cousumedolls))
                    // 根據機率決定是否給予物品
                    if (_random.nextInt(100) + 1 < ((Integer) aTempData.get(4)).intValue()) {
                        // 如果有訊息，則發送給玩家
                        if ((String) aTempData.get(8) != null)
                            pc.sendPackets(new S_SystemMessage((new StringBuilder()).append("\\fT").append((String) aTempData.get(8)).toString()));
                        // 建立並給予物品給玩家
                        L1ItemInstance item = ItemTable.get().createItem(giveItemGet);
                        item.setIdentified(true);
                        pc.getInventory().storeItem(item);
                        // 發送成功訊息給玩家
                        pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));


                        if ((String) aTempData.get(9) != null)
                            // 廣播訊息給所有玩家，格式化訊息包含玩家名稱和物品名稱
                            World.get().broadcastPacketToAll(new S_SystemMessage(String.format((String) aTempData.get(9), new Object[]{
                                    pc.getName(), item.getLogName()
                            })));
                        // 設置玩家任務的步驟為 aTempData 的第 11 個元素的值，步驟數量為 0
                        pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 0);
                    } else {
                        // 如果 aTempData 的第 11 個元素不為 0
                        if (((Integer) aTempData.get(11)).intValue() != 0) {
                            if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 0)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 1);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 1)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 2);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 2)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 3);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 3)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 4);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 4)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 5);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 5)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 6);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 6)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 7);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 7)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 8);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 8)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 9);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 9)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 10);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 10)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 11);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 11)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 12);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 12)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 13);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 13)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 14);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 14)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 15);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 15)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 16);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 16)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 17);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 17)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 18);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 18)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 19);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 19)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 20);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 20)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 21);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 21)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 22);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 22)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 23);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 23)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 24);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 24)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 25);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 25)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 26);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 26)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 27);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 27)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 28);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 28)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 29);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 29)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 30);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 30)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 31);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 31)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 32);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 32)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 33);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 33)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 34);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 34)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 35);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 35)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 36);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 36)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 37);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 37)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 38);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 38)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 39);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 39)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 40);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 40)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 41);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 41)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 42);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 42)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 43);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 43)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 44);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 44)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 45);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 45)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 46);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 46)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 47);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 47)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 48);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 48)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 49);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 49)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 50);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 50)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 51);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 51)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 52);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 52)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 53);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 53)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 54);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 54)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 55);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 55)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 56);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 56)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 57);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 57)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 58);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 58)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 59);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 59)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 60);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 60)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 61);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 61)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 62);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 62)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 63);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 63)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 64);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 64)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 65);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 65)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 66);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 66)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 67);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 67)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 68);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 68)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 69);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 69)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 70);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 70)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 71);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 71)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 72);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 72)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 73);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 73)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 74);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 74)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 75);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 75)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 76);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 76)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 77);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 77)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 78);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 78)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 79);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 79)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 80);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 80)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 81);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 81)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 82);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 82)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 83);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 83)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 84);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 84)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 85);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 85)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 86);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 86)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 87);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 87)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 88);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 88)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 89);
                            else if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == 89)
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 90);
                            // 檢查當前任務步驟是否等於 aTempData 的第 12 個元素的值
                            // 檢查玩家當前的任務步驟是否等於 aTempData 的第 12 個元素的值
                            if (pc.getQuest().get_step(((Integer) aTempData.get(11)).intValue()) == ((Integer) aTempData.get(12)).intValue()) {
                                // 如果 aTempData 的第 8 個元素不為 null，則向玩家發送系統訊息
                                if ((String) aTempData.get(8) != null)
                                    pc.sendPackets(new S_SystemMessage((new StringBuilder()).append("\\fT").append((String) aTempData.get(8)).toString()));

                                // 創建一個新的物品實例，並將其設置為已識別
                                L1ItemInstance item = ItemTable.get().createItem(giveItemGet);
                                item.setIdentified(true);

                                // 將物品存入玩家的揹包
                                pc.getInventory().storeItem(item);

                                // 向玩家發送伺服器訊息，通知獲得物品
                                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));

                                // 如果 aTempData 的第 9 個元素不為 null，則廣播訊息給所有玩家
                                if ((String) aTempData.get(9) != null)
                                    World.get().broadcastPacketToAll(new S_SystemMessage(String.format((String) aTempData.get(9), new Object[]{
                                            pc.getName(), item.getLogName()
                                    })));

                                // 將玩家的任務步驟設置回 0，表示任務完成或重置
                                pc.getQuest().set_step(((Integer) aTempData.get(11)).intValue(), 0);

                                // 返回 false，結束方法
                                return false;
                            }
                        }
                        // 如果 aTempData 的第 7 個元素不為 null，則向玩家發送失敗訊息
                        if ((String) aTempData.get(7) != null)
                            pc.sendPackets(new S_SystemMessage((new StringBuilder()).append("\\fY").append((String) aTempData.get(7)).toString()));

                        // 檢查 aTempData 的第 10 個元素是否為 1，表示是否需要退還物品
                        if (((Integer) aTempData.get(10)).intValue() == 1) {
                            // 隨機選擇一個已消耗的物品，創建其實例並設置為已識別
                            L1ItemInstance item2 = ItemTable.get().createItem(((Integer) cousumedolls.get(_random.nextInt(cousumedolls.size()))).intValue());
                            item2.setIdentified(true);

                            // 將退還的物品存入玩家的揹包
                            pc.getInventory().storeItem(item2);

                            // 向玩家發送系統訊息，通知退還的物品
                            pc.sendPackets(new S_SystemMessage((new StringBuilder()).append("\\fY失敗退還:").append(item2.getViewName()).toString()));
                        }

                        // 創建一個包含兩個元素的字串陣列
                        String info[] = new String[2];
                        info[1] = String.valueOf(pc.getQuest());
                    }
            }
            // 遍歷所有數據後，如果沒有足夠的物品並且 aTempData 的第 6 個元素不為 null，則向玩家發送提示訊息
            if (!enough && (String) aTempData.get(6) != null)
                pc.sendPackets(new S_SystemMessage((new StringBuilder()).append("\\fY").append((String) aTempData.get(6)).toString()));
        }

        // 最後，方法返回 false，表示任務未成功完成
        return false;
    }

    private static void getData() {
        Connection con = null;
        try {
            con = DatabaseFactory.get().getConnection();
            Statement stat = con.createStatement();
            ResultSet rset = stat.executeQuery("SELECT * FROM w_天M合成系統");
            ArrayList aReturn = null;
            String sTemp = null;
            if (rset != null)
                for (; rset.next(); aData.add(aReturn)) {
                    aReturn = new ArrayList();
                    aReturn.add(0, new Integer(rset.getInt("npcid")));
                    sTemp = rset.getString("action");
                    aReturn.add(1, sTemp);
                    aReturn.add(2, getArray(rset.getString("合成需求編號"), ",", 1));
                    aReturn.add(3, Integer.valueOf(rset.getInt("物品合成數量")));
                    aReturn.add(4, Integer.valueOf(rset.getInt("機率")));
                    aReturn.add(5, getArray(rset.getString("獲取合成編號"), ",", 1));
                    aReturn.add(6, rset.getString("物品不足Msg"));
                    aReturn.add(7, rset.getString("失敗Msg"));
                    aReturn.add(8, rset.getString("成功Msg"));
                    aReturn.add(9, rset.getString("世界廣播"));
                    aReturn.add(10, Integer.valueOf(rset.getInt("失敗是否退還")));
                    aReturn.add(11, Integer.valueOf(rset.getInt("保底紀錄編號")));
                    aReturn.add(12, Integer.valueOf(rset.getInt("保底次數")));
                }

            if (con != null && !con.isClosed())
                con.close();
        } catch (Exception exception) {
        }
    }

    // 取得陣列的方法，根據不同的類型(iType)回傳不同類型的資料
    private static Object getArray(String s, String sToken, int iType) {
        // 使用 StringTokenizer 將字串 s 按照分隔符號 sToken 分割
        StringTokenizer st = new StringTokenizer(s, sToken);
        int iSize = st.countTokens(); // 計算分割後的字串數量
        String sTemp = null; // 暫存每次分割的字串

        // 如果 iType 是 1，回傳整數型陣列
        if (iType == 1) {
            int iReturn[] = new int[iSize]; // 建立整數陣列，大小為分割後的字串數量
            for (int i = 0; i < iSize; i++) {
                sTemp = st.nextToken(); // 取得下一個分割字串
                iReturn[i] = Integer.parseInt(sTemp); // 將字串轉換為整數
            }
            return iReturn; // 回傳整數陣列
        }

        // 如果 iType 是 2，回傳字串型陣列
        if (iType == 2) {
            String sReturn[] = new String[iSize]; // 建立字串陣列，大小為分割後的字串數量
            for (int i = 0; i < iSize; i++) {
                sTemp = st.nextToken(); // 取得下一個分割字串
                sReturn[i] = sTemp; // 將字串儲存到陣列中
            }
            return sReturn; // 回傳字串陣列
        }

        // 如果 iType 是 3，回傳最後一個分割字串
        if (iType == 3) {
            String sReturn2 = null; // 初始化最後一個字串變數
            for (int i = 0; i < iSize; i++) {
                sTemp = sReturn2 = st.nextToken(); // 每次迴圈更新最後一個字串
            }
            return sReturn2; // 回傳最後一個字串
        } else {
            // 如果 iType 不符合上述條件，回傳 null
            return null;
        }
    }
}