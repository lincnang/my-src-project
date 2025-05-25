package com.lineage.data.npc.quest2;

import com.lineage.data.QuestClass;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ADLv80_2;
import com.lineage.server.datatables.InnKeyTable;
import com.lineage.server.datatables.InnTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.QuestMapTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Inn;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldQuest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Npc_DragonA2 extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_DragonA2.class);
    // 龍門OBJID的參加者列表
    private static final Map<Integer, ArrayList<Integer>> _DOORPLAYERMAP = new HashMap<Integer, ArrayList<Integer>>();
    private ArrayList<Integer> key_runout_doorid = new ArrayList<Integer>();// 已售出鑰匙的龍門OBJID

    public static NpcExecutor get() {
        return new Npc_DragonA2();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_n_dragon1", new String[]{"法利昂棲息地"}));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        if (cmd.equalsIgnoreCase("0")) {
            if (isError2(pc, npc)) {
                return;
            }
            // 進入訓練所
            int npcId = npc.getNpcTemplate().get_npcId();
            for (L1ItemInstance item : pc.getInventory().getItems()) {
                if (item.getInnNpcId() == npcId) {// 對應的npcid相同
                    for (int i = 0; i < 16; i++) {
                        L1Inn inn = InnTable.getInstance().getTemplate(npcId, i);
                        if (inn.getKeyId() == item.getKeyId()) {// 鑰匙編號相同
                            Timestamp dueTime = item.getDueTime();
                            if (dueTime != null) {// 具有租約時間
                                Calendar cal = Calendar.getInstance();
                                if ((cal.getTimeInMillis() - dueTime.getTime()) / 1000L < 0L) {// 還未到退房時間
                                    pc.set_showId(item.getKeyId());// 設置副本編號
                                    staraQuestA(npc, pc);// 開始執行副本任務
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (cmd.equalsIgnoreCase("1")) {// 購買龍門憑證
            amount = 32;// 給予憑證數量
            int npcId = npc.getNpcTemplate().get_npcId();
            boolean canRent = false;// 是否可以租房
            boolean findRoom = false;// 是否找到空房
            boolean isRented = false;// 是否已有租房
            int roomNumber = 0;// 空房的房號
            byte roomCount = 0;// 已佔用房數
            for (int i = 0; i < 16; i++) {
                L1Inn inn = InnTable.getInstance().getTemplate(npcId, i);
                if (inn != null) {
                    Timestamp dueTime = inn.getDueTime();
                    Calendar cal = Calendar.getInstance();
                    long checkDueTime = (cal.getTimeInMillis() - dueTime.getTime()) / 1000L;
                    if (pc.getInventory().checkItem(82504, 1)) {// 身上有龍門憑證
                        isRented = true;
                        break;
                    }
                    if ((!findRoom) && (!isRented)) {
                        if (checkDueTime >= 0L) {// 已超過時間
                            canRent = true;
                            findRoom = true;
                            roomNumber = inn.getRoomNumber();
                        } else if (inn.getLodgerId() == pc.getId()) {// 自己租過的房間
                            canRent = true;
                            findRoom = true;
                            roomNumber = inn.getRoomNumber();
                        } else {
                            roomCount = (byte) (roomCount + 1);// 已佔用房數
                        }
                    }
                }
            }
            if (isRented) {// 已租用訓練所
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_n_dragon6"));
            } else if (roomCount >= 16) {// 已佔用房數
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_n_dragon3"));
            } else if (canRent && !key_runout_doorid.contains(npc.getId())) {// 可以租用且龍門尚未售出鑰匙
                if (pc.getInventory().checkItem(L1ItemId.ADENA, (300 * amount))) {
                    L1Inn inn = InnTable.getInstance().getTemplate(npcId, roomNumber);
                    if (inn != null) {
                        // 租用時間 2小時
                        Timestamp ts = new Timestamp(System.currentTimeMillis() + (60 * 60 * 2 * 1000));
                        // 登入旅館資料
                        L1ItemInstance item = ItemTable.get().createItem(82504); // 龍門憑證
                        if (item != null) {
                            item.setKeyId(item.getId()); // 鑰匙編號
                            item.setInnNpcId(npcId); // 旅館NPC
                            item.setHall(false); // 判斷租房間 or 會議室
                            item.setDueTime(ts); // 租用時間
                            item.setCount(amount); // 鑰匙數量
                            inn.setKeyId(item.getKeyId()); // 旅館鑰匙編號
                            inn.setLodgerId(pc.getId()); // 租用人
                            inn.setHall(false); // 判斷租房間 or 會議室
                            inn.setDueTime(ts); // 租用時間
                            // DB更新
                            InnTable.getInstance().updateInn(inn);
                            pc.getInventory().consumeItem(L1ItemId.ADENA, (300 * amount)); // 扣除金幣
                            // 給予鑰匙並登入鑰匙資料
                            L1Inventory inventory;
                            if (pc.getInventory().checkAddItem(item, amount) == L1Inventory.OK) {
                                inventory = pc.getInventory();
                            } else {
                                inventory = World.get().getInventory(pc.getLocation());
                            }
                            inventory.storeItem(item);
                            if (InnKeyTable.checkey(item)) {// 鑰匙資料已存在
                                InnKeyTable.DeleteKey(item);
                                InnKeyTable.StoreKey(item);
                            } else {
                                InnKeyTable.StoreKey(item);
                            }
                            // \f1%0%s 給你 %1%o 。
                            pc.sendPackets(new S_ServerMessage(143, npc.getName(), item.getLogName()));
                            // 要一起使用房間的話，請把鑰匙給其他人，往旁邊的樓梯上去即可。
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_n_dragon2"));
                            key_runout_doorid.add(npc.getId());// 加入已售出鑰匙龍門OBJID列表
                        }
                    }
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_n_dragon5"));
                }
            } else if (key_runout_doorid.contains(npc.getId())) {// 龍門憑證已售出
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_n_dragon4"));
            }
        }
    }

    /**
     * 開始執行副本任務
     *
     */
    private void staraQuestA(L1NpcInstance npc, L1PcInstance pc) {
        try {
            int questid = ADLv80_2.QUEST.get_id();
            ArrayList<Integer> playerlist = _DOORPLAYERMAP.get(npc.getId());// 此龍門的參加者列表
            if (playerlist == null) {
                playerlist = new ArrayList<Integer>();
            }
            /*逃離副本無法再進入*/
            if (playerlist.contains(pc.getId())) {
                pc.sendPackets(new S_BlueMessage(166, "\\f3逃離副本後無法再進入"));
				/*pc.sendPacketsX8(new S_SkillSound(pc.getId(), 7854));
				SkillMode mode = L1SkillMode.get().getSkill(6798);
				if (mode != null) {
					mode.start(pc, null, null, 43200);
				}*/
                pc.sendPackets(new S_CloseList(pc.getId()));
                return;
            }
            playerlist.add(pc.getId());// 加入參加者列表
            _DOORPLAYERMAP.put(npc.getId(), playerlist);// 更新此龍門的參加者列表
            QuestClass.get().startQuest(pc, ADLv80_2.QUEST.get_id());// 設定開始任務
            QuestClass.get().endQuest(pc, ADLv80_2.QUEST.get_id());// 設定結束任務
            L1QuestUser quest = WorldQuest.get().put(pc.get_showId(), 1011, questid, pc);// 加入副本清單
            L1Teleport.teleport(pc, 32957, 32743, (short) 1011, 1, true);// 傳送至副本地圖
            if (quest == null) {
                _log.error("副本設置過程發生異常!!");
                pc.sendPackets(new S_CloseList(pc.getId()));
                return;
            }
            Integer time = QuestMapTable.get().getTime(1011);
            if (time != null) {
                quest.set_time(time.intValue());
            }
            L1SpawnUtil.spawnDoor(quest, 10008, 7858, 32741, 32712, (short) 1011, 0);
            L1SpawnUtil.spawnDoor(quest, 10009, 7859, 32779, 32681, (short) 1011, 1);
            L1SpawnUtil.spawnDoor(quest, 10010, 7858, 32861, 32709, (short) 1011, 0);
            for (L1NpcInstance npc2 : quest.npcList()) {// 清空怪物身上道具(BOSS除外)
                if ((npc2 instanceof L1MonsterInstance)) {
                    L1MonsterInstance mob = (L1MonsterInstance) npc2;
                    if (mob.getNpcId() != 71026 && mob.getNpcId() != 71027 && mob.getNpcId() != 71028) {
                        mob.set_storeDroped(true);
                        mob.getInventory().clearItems();
                    }
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 是否無法進入副本
     *
     */
    private boolean isError2(L1PcInstance pc, L1NpcInstance npc) {
        if (!pc.getInventory().checkItem(82504, 1)) {
            pc.sendPackets(new S_SystemMessage("身上必須要有龍門憑證才能進入。"));
            return true;
        }
        if (pc.getLevel() < 60) {
            pc.sendPackets(new S_SystemMessage("等級未滿60級，無法進入"));
            return true;
        }
        if (pc.hasSkillEffect(6798)) {
            // 龍之血痕已穿透全身，在血痕的氣味消失之前，無法再進入龍之門扉。
            pc.sendPackets(new S_ServerMessage(1626));
            return true;
        }
        int users = QuestMapTable.get().getTemplate(1011);// 地圖人數限制
        if (users == -1) {
            users = 127;
        }
        ArrayList<Integer> playerlist = _DOORPLAYERMAP.get(npc.getId());// 此龍門的參加者列表
        if (playerlist != null && playerlist.size() >= users) {
            pc.sendPackets(new S_SystemMessage("已達進入人數上限，無法進入"));
            return true;
        }
        return false;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.quest2.Npc_DragonA1 JD-Core Version: 0.6.2
 */