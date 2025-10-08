package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.lock.CharacterQuestReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcQuest;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_TrueTarget;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1ServerQuestMob;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

public class ServerQuestMobTable {  //src027
    private static final Log _log = LogFactory.getLog(ServerQuestMobTable.class);
    private static final HashMap<Integer, HashMap<Integer, L1ServerQuestMob>> _questMobList = new HashMap<Integer, HashMap<Integer, L1ServerQuestMob>>();
    private static ServerQuestMobTable _instance;

    private ServerQuestMobTable() {
        load();
    }

    public boolean Cmd(L1PcInstance pc, String cmd) {
        // 【1】evd_list：查詢主線任務內容與進度
        if (cmd.equalsIgnoreCase("evd_list")) {
            String[] evdList = getEvdList(pc); // 動態取得玩家當前任務內容
            pc.sendPackets(new S_NPCTalkReturn(pc, "evd_list", evdList));
            return true;
        }
        String action = cmd;
        if (action.equalsIgnoreCase("quest_goto")) {
            int quest_id = getCurrentMainQuestId(pc);
            L1ServerQuestMob quest = getQuestMobById(quest_id);
            // 【B】扣除道具判斷
            int needItemId = quest.get_itemid();
            int needPrice = quest.get_price();
            if (needItemId > 0 && needPrice > 0) {
                if (!pc.getInventory().consumeItem(needItemId, needPrice)) {
                    pc.sendPackets(new S_ServerMessage("需要 " + needPrice + " 個道具才能傳送！"));
                    return true;
                }
            }
            // 【D】傳送
            doTeleport(pc, quest);
            return true;
        }
        return false;
    }

    public static ServerQuestMobTable get() {
        if (_instance == null) {
            _instance = new ServerQuestMobTable();
        }
        return _instance;
    }

    private static int[] getArray(String s) {
        if (s == null || s.trim().isEmpty()) {
            return new int[0];  // 回傳空陣列，不會爆炸
        }
        StringTokenizer st = new StringTokenizer(s, ",");
        int iSize = st.countTokens();
        int[] iReturn = new int[iSize];
        for (int i = 0; i < iSize; i++) {
            iReturn[i] = Integer.parseInt(st.nextToken().trim());
        }
        return iReturn;
    }


    private void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `系統_主線系統`");
            rs = pstm.executeQuery();

            while (rs.next()) {
                // 取得每個可能異常的欄位
                String mobStr = rs.getString("怪物編號");
                String mobCountStr = rs.getString("怪物數量");
                String itemStr = rs.getString("物品獎勵");
                String itemCountStr = rs.getString("物品獎勵數量");
                // 只要有一個為空或null就秀出異常
                if (mobStr == null || mobStr.trim().isEmpty()
                        || mobCountStr == null || mobCountStr.trim().isEmpty()
                        || itemStr == null || itemStr.trim().isEmpty()
                        || itemCountStr == null || itemCountStr.trim().isEmpty()) {

                    System.out.println("[異常] 任務編號: " + rs.getInt("任務編號")
                            + " / 階段: " + rs.getInt("任務階段")
                            + " / 進度: " + rs.getInt("任務進度")
                            + " / 怪物編號: " + mobStr
                            + " / 怪物數量: " + mobCountStr
                            + " / 物品獎勵: " + itemStr
                            + " / 物品獎勵數量: " + itemCountStr);
                }
                int quest_id = rs.getInt("任務編號");
                int quest_stage = rs.getInt("任務階段");
                int quest_step = rs.getInt("任務進度");
                String note = rs.getString("說明");
                int lv = rs.getInt("等級判斷");
                int npc_gfxid = rs.getInt("怪物顯示特效");
                int[] mob_id = getArray(rs.getString("怪物編號"));
                int[] mob_count = getArray(rs.getString("怪物數量"));
                int[] item_id = getArray(rs.getString("物品獎勵"));
                int[] item_lv = getArray(rs.getString("物品獎勵等級"));
                int[] item_count = getArray(rs.getString("物品獎勵數量"));
                int save_quest_order = rs.getInt("儲存任務進度");
                int tele_x = rs.getInt("傳出座標_X");
                int tele_y = rs.getInt("傳出座標_Y");
                int tele_m = rs.getInt("傳出座標編號");
                int tele_delay = rs.getInt("tele_delay");
                int enter_x = rs.getInt("傳入座標_X"); // 任務進入用
                int enter_y = rs.getInt("傳入座標_Y");
                int enter_m = rs.getInt("傳入地圖編號");
                int itemid = rs.getInt("itemid");
                int price = rs.getInt("price");
                final L1ServerQuestMob serverQuestMob = new L1ServerQuestMob();
                serverQuestMob.set_quest_id(quest_id);
                serverQuestMob.set_quest_stage(quest_stage);
                serverQuestMob.set_quest_step(quest_step);
                serverQuestMob.set_note(note);
                serverQuestMob.set_lv(lv);
                serverQuestMob.set_npc_gfxid(npc_gfxid);
                serverQuestMob.set_mob_id(mob_id);
                serverQuestMob.set_mob_count(mob_count);
                serverQuestMob.set_item_id(item_id);
                serverQuestMob.set_item_lv(item_lv);
                serverQuestMob.set_item_count(item_count);
                serverQuestMob.set_save_quest_order(save_quest_order);
                serverQuestMob.set_tele_x(tele_x);
                serverQuestMob.set_tele_y(tele_y);
                serverQuestMob.set_tele_m(tele_m);
                serverQuestMob.set_tele_delay(tele_delay);
                serverQuestMob.set_teleport_x(enter_x);  // 新增
                serverQuestMob.set_teleport_y(enter_y);
                serverQuestMob.set_teleport_mapid(enter_m);
                serverQuestMob.set_itemid(itemid);
                serverQuestMob.set_price(price);
                HashMap<Integer, L1ServerQuestMob> questMap = (HashMap<Integer, L1ServerQuestMob>) _questMobList.get(quest_id);
                if (questMap == null) {
                    questMap = new HashMap<Integer, L1ServerQuestMob>();
                    questMap.put(quest_step, serverQuestMob);
                    _questMobList.put(quest_id, questMap);
                } else {
                    questMap.put(quest_step, serverQuestMob);
                }
            }
            _log.info("讀取->[改寫]狩獵任務設置資料數量: " + _questMobList.size() + "(" + timer.get() + "ms)");
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }


    /**
     * 判定玩家所接的任務編號，並且讓怪物顯示特效
     *
     */
    public synchronized void checkQuestMobGfx(final L1PcInstance pc, L1NpcInstance npc) {
        if (_questMobList == null) {
            return;
        }
        for (final Integer quest_id : _questMobList.keySet()) {
            final HashMap<Integer, L1ServerQuestMob> map = (HashMap<Integer, L1ServerQuestMob>) _questMobList.get(quest_id);
            if (pc.getQuest().isStart(quest_id.intValue())) {
                for (Integer quest_order : map.keySet()) {
                    if (((L1ServerQuestMob) map.get(quest_order)).get_npc_gfxid() != 0) {
                        final int[] mob_id = ((L1ServerQuestMob) map.get(quest_order)).get_mob_id();
                        final int[] mob_count = ((L1ServerQuestMob) map.get(quest_order)).get_mob_count();
                        final int[] pc_mob_count = pc.getQuest().get_mob_count(quest_id.intValue());
                        for (int i = 0; i < mob_id.length; i++) {
                            if (pc_mob_count[i] < mob_count[i]) {
                                final L1Npc l1npc = NpcTable.get().getTemplate(mob_id[i]);
                                if (l1npc != null) {
                                    if (npc.getNpcId() == l1npc.get_npcId()) {
                                        pc.sendPackets(new S_TrueTarget(npc.getId(), ((L1ServerQuestMob) map.get(quest_order)).get_npc_gfxid(), 1));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    public synchronized void checkQuestMob(final L1PcInstance pc, final int mobId) {
        if (_questMobList == null) {
            return;
        }
        int currentQuestId = ServerQuestMobTable.get().getCurrentMainQuestId(pc);
        if (currentQuestId > 0) {
            HashMap<Integer, L1ServerQuestMob> questMap = _questMobList.get(currentQuestId);
            if (questMap == null) {
                return;
            }
            cheakQuest(currentQuestId, mobId, pc, questMap);
            if (pc.getQuest().isStart(currentQuestId)) {
                checkMob(currentQuestId, mobId, pc, questMap);
            } else {
                System.out.println("[DEBUG] 任務尚未啟動，不進行擊殺紀錄。");
            }
//        } else {
//            System.out.println("[DEBUG] 玩家目前沒有正在進行中的主線任務，不執行任務紀錄。");
        }
    }


    /**
     * 任務進度偵測、如果未初始化自動啟動該階段
     */
    private void cheakQuest(final int quest_id, final int mobId, final L1PcInstance pc, final HashMap<Integer, L1ServerQuestMob> questMap) {
        if (!pc.getQuest().isStart(quest_id)) {
            Integer firstStep = questMap.keySet().stream().findFirst().orElse(1);
            L1ServerQuestMob mobData = questMap.get(firstStep);
            if (mobData != null) {
                int[] mobArr = new int[mobData.get_mob_id().length];
                pc.getQuest().set_step(quest_id, firstStep);
                // ★ 正確設定 nowStage
                pc.getQuest().set_now_stage(quest_id, mobData.get_quest_stage() - 1);
                pc.getQuest().set_mob_count(quest_id, mobArr);
            }
        }

        // 隊伍同步（同理防呆）
        if (pc.isInParty()) {
            Object[] pcs = pc.getParty().getMemberList().toArray();
            if (pcs.length <= 0) {
                return;
            }
            for (Object obj : pcs) {
                if (obj instanceof L1PcInstance) {
                    L1PcInstance tgpc = (L1PcInstance) obj;
                    if (pc != tgpc && World.get().getVisibleObjects(pc, tgpc)) {
                        if (!tgpc.getQuest().isStart(quest_id)) {
                            for (L1ServerQuestMob serverQuestMob : questMap.values()) {
                                int targetStage = serverQuestMob.get_quest_stage();
                                int nowStage = tgpc.getQuest().get_now_stage(quest_id);
                                if (targetStage > 1 && nowStage != (targetStage - 1)) {
                                    continue;
                                }
                                if (tgpc.getLevel() + (tgpc.getMeteLevel() * 99) < serverQuestMob.get_lv()) {
                                    return;
                                }
                                if (tgpc.getQuest().get_step(quest_id) == serverQuestMob.get_save_quest_order()) {
                                    return;
                                }
                                int[] mobid = serverQuestMob.get_mob_id();
                                for (int i = 0; i < mobid.length; i++) {
                                    if (mobid[i] == mobId) {
                                        tgpc.getQuest().set_step(quest_id, serverQuestMob.get_quest_step());
                                        if (tgpc.getQuest().isStart(quest_id)) {
                                            checkMob(quest_id, mobId, tgpc, questMap);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    /**
     * 判斷並更新當前階段擊殺進度、給予獎勵、標記結束。
     * 完成時只設本 quest_id 為結束，不會自動進行下一階段。
     */
    private void checkMob(final int quest_id, final int mobId, final L1PcInstance pc, final HashMap<Integer, L1ServerQuestMob> questMap) {
        int nowStage = ServerQuestMobTable.get().getQuestStageByStep(quest_id, pc.getQuest().get_step(quest_id));
        L1ServerQuestMob currQuest = null;
        Integer currStepKey = null;
        for (final Integer quest_step : questMap.keySet()) {
            final L1ServerQuestMob quest = questMap.get(quest_step);
            if (quest.get_quest_stage() == nowStage) {
                currQuest = quest;
                currStepKey = quest_step;
                break;
            }
        }
        if (currQuest == null) {
            return;
        }
        // 步驟不正確則修正
        if (pc.getQuest().get_step(quest_id) != currStepKey.intValue()) {
            pc.getQuest().set_step(quest_id, currStepKey);
            pc.getQuest().set_mob_count(quest_id, new int[currQuest.get_mob_id().length]);
        }

        // 取得怪物與玩家目前擊殺資料
        final int[] mob_id = currQuest.get_mob_id();
        final int[] mob_count = currQuest.get_mob_count();
        int[] pc_mob_count = pc.getQuest().get_mob_count(quest_id);
        if (pc_mob_count == null || pc_mob_count.length != mob_id.length) {
            pc_mob_count = new int[mob_id.length];
            pc.getQuest().set_mob_count(quest_id, pc_mob_count);
        }

        // 記錄擊殺進度
        boolean questCompleted = true;
        boolean updated = false;

        for (int i = 0; i < mob_id.length; i++) {
            if (mob_id[i] == mobId && pc_mob_count[i] < mob_count[i]) {
                pc_mob_count[i]++;
                updated = true;
                String npc_name = NpcTable.get().getTemplate(mob_id[i]).get_name();
                pc.sendPackets(new S_PacketBoxGree(currQuest.get_note() + npc_name + "(" + pc_mob_count[i] + "/" + mob_count[i] + ")"));
            }
            if (pc_mob_count[i] < mob_count[i]) {
                questCompleted = false;
            }
        }

        // 有擊殺就寫回
        if (updated) {
            pc.getQuest().set_mob_count(quest_id, pc_mob_count);
        }
        // 完成條件判斷（只要有擊殺才進入！）

        if (questCompleted && updated) {
            int mainQuestBaseId = 995000;  // 或直接寫995000
            System.out.println("[DEBUG] checkMob階段推進: mainQuestBaseId=" + mainQuestBaseId + ", currQuest.get_quest_stage()=" + currQuest.get_quest_stage());
            System.out.println("[DEBUG] checkMob before: now_stage=" + pc.getQuest().get_now_stage(mainQuestBaseId));
            // 1. 標記 now_stage 完成本階段
            pc.getQuest().set_now_stage(mainQuestBaseId, currQuest.get_quest_stage());
            System.out.println("[DEBUG] checkMob after: now_stage=" + pc.getQuest().get_now_stage(mainQuestBaseId));

            // 2. 標記 step=255（結束）
            pc.getQuest().set_step(quest_id, L1PcQuest.QUEST_END);
            // 3. 清空 mob_count
            pc.getQuest().set_mob_count(quest_id, new int[mob_id.length]);
            // 4. 更新資料庫
            CharacterQuestReading.get().updateQuest(pc.getId(), quest_id, pc.getQuest().getCharQuest(quest_id));
            // 5. 發送獎勵訊息
            pc.sendPackets(new S_ServerMessage("完成了狩獵任務，獎勵物品："));
            final int[] item_id = currQuest.get_item_id();
            final int[] item_lv = currQuest.get_item_lv();
            final int[] item_count = currQuest.get_item_count();
            for (int i = 0; i < item_id.length; i++) {
                L1ItemInstance item = ItemTable.get().createItem(item_id[i]);
                if (item != null) {
                    item.setCount(item_count[i]);
                    if (item_lv.length > i && item_lv[i] != 0) {
                        item.setEnchantLevel(item_lv[i]);
                    }
                    if (pc.getInventory().checkAddItem(item, item_count[i]) == 0) {
                        pc.getInventory().storeItem(item);
                    } else {
                        item.set_showId(pc.get_showId());
                        World.get().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
                    }
                    pc.sendPackets(new S_ServerMessage(item.getName() + " (" + item.getCount() + ")"));
                } else {
                    _log.error("指定編號物品不存在: " + item_id[i]);
                }
            }
            pc.sendPackets(new S_ServerMessage(""));

            // 6. UI即時更新
            String[] evdList = ServerQuestMobTable.get().getEvdList(pc);
            pc.sendPackets(new S_NPCTalkReturn(pc, "evd_list", evdList));

            // 【傳送前先消耗道具】
            int itemid = currQuest.get_itemid();
            int price = currQuest.get_price();
            if (itemid > 0 && price > 0) {
                if (!pc.getInventory().consumeItem(itemid, price)) {
                    pc.sendPackets(new S_ServerMessage("需要 " + price + " 個傳送道具才可離開任務！"));
                    return; // 沒扣到不傳送
                }
            }

            // 7. 傳送座標
            final int x = currQuest.get_tele_x();
            final int y = currQuest.get_tele_y();
            final int tomapid = currQuest.get_tele_m();
            final int delay = currQuest.get_tele_delay();
            if (x != 0 && y != 0 && tomapid >= 0) {
                if (delay != 0) {
                    try { TimeUnit.MILLISECONDS.sleep(delay); } catch (InterruptedException e) { e.printStackTrace(); }
                }
                L1Teleport.teleport(pc, x, y, (short) tomapid, 5, true);
            }
            // 8. 結束，**不再自動進行下一主線**
            return;
        }
    }


    public int[] getMobCount(int quest_id, int quest_order) {
        if ((_questMobList.containsKey(Integer.valueOf(quest_id))) && (((HashMap<Integer, L1ServerQuestMob>) _questMobList.get(Integer.valueOf(quest_id))).containsKey(Integer.valueOf(quest_order)))) {
            int length = ((L1ServerQuestMob) ((HashMap<Integer, L1ServerQuestMob>) _questMobList.get(Integer.valueOf(quest_id))).get(Integer.valueOf(quest_order))).get_mob_count().length;
            int[] data = new int[length];
            return data;
        }
        return null;
    }
    /**
     * 根據 quest_id 和 quest_step 反查該 step 對應的 quest_stage
     */
    public int getQuestStageByStep(int quest_id, int quest_step) {
        HashMap<Integer, L1ServerQuestMob> questMap = _questMobList.get(quest_id);
        if (questMap == null) {
            return 0;
        }
        L1ServerQuestMob mob = questMap.get(quest_step);
        if (mob == null) {
            return 0;
        }
        return mob.get_quest_stage();
    }

    public Set<Integer> getQuestMobListId() {
        if (_questMobList != null) {
            return _questMobList.keySet();
        }
        return null;
    }
    /**
     * 根據玩家目前任務進度，取得顯示用的任務資料（UI用）
     * @param pc 玩家
     * @return String[] evdList
     */
    public String[] getEvdList(L1PcInstance pc) {
        String[] evdList = new String[15]; // #0~#14

        int quest_id = getCurrentMainQuestId(pc);
        if (quest_id == -1) {
            evdList[0] = "無主線任務進行中";
            for (int i = 1; i < evdList.length; i++) evdList[i] = "";
            return evdList;
        }

        HashMap<Integer, L1ServerQuestMob> questMap = _questMobList.get(quest_id);
        int questStep = 1;
        if (questMap != null) {
            for (Integer stepKey : questMap.keySet()) {
                questStep = stepKey;
                break;
            }
        }

        L1ServerQuestMob quest = getQuestMobByIdAndStep(quest_id, questStep);
        if (quest == null) {
            evdList[0] = "查無此任務";
            for (int i = 1; i < evdList.length; i++) evdList[i] = "";
            return evdList;
        }

        // #0 任務名稱
        evdList[0] = quest.get_note();

        // #1~#5 完成獎勵
        int[] itemIds = quest.get_item_id();
        int[] itemCounts = quest.get_item_count();
        for (int i = 0; i < 5; i++) {
            if (i < itemIds.length) {
                int itemId = itemIds[i];
                int count = (i < itemCounts.length) ? itemCounts[i] : 1;
                String itemName = "未知物品(" + itemId + ")";
                if (ItemTable.get().getTemplate(itemId) != null) {
                    itemName = ItemTable.get().getTemplate(itemId).getName();
                }
                evdList[1 + i] = itemName + " x" + count;
            } else {
                evdList[1 + i] = "";
            }
        }

        // #6~#11 擊殺進度
        int[] mobIds = quest.get_mob_id();
        int[] mobCounts = quest.get_mob_count();
        int[] pcMobCounts = pc.getQuest().get_mob_count(quest_id);
        if (pcMobCounts == null || pcMobCounts.length != mobIds.length) {
            pcMobCounts = new int[mobIds.length];
        }
        for (int i = 0; i < 6; i++) {
            if (i < mobIds.length) {
                String mobName = "未知怪物(" + mobIds[i] + ")";
                if (NpcTable.get().getTemplate(mobIds[i]) != null) {
                    mobName = NpcTable.get().getTemplate(mobIds[i]).get_name();
                }
                int pcCount = pcMobCounts[i];
                evdList[6 + i] = mobName + "(" + pcCount + "/" + mobCounts[i] + ")";
            } else {
                evdList[6 + i] = "";
            }
        }

        // #12 總任務完成
        int totalQuest = _questMobList.size();
        int completeQuest = 0;
        for (Integer qid : _questMobList.keySet()) {
            if (pc.getQuest().isEnd(qid)) completeQuest++;
        }
        evdList[12] = completeQuest + "/" + totalQuest;

        // #13 下一階段
        int curStage = quest.get_quest_stage();
        String nextNote = "無";
        for (HashMap<Integer, L1ServerQuestMob> map : _questMobList.values()) {
            for (L1ServerQuestMob mob : map.values()) {
                if (mob.get_quest_stage() == (curStage + 1)) {
                    nextNote = mob.get_note();
                    break;
                }
            }
            if (!nextNote.equals("無")) break;
        }
        evdList[13] = nextNote;

        // #14 傳送消耗
        int itemid = quest.get_itemid();
        int price = quest.get_price();
        String costText = "";
        if (itemid > 0 && price > 0) {
            String itemName = "未知物品(" + itemid + ")";
            if (ItemTable.get().getTemplate(itemid) != null) {
                itemName = ItemTable.get().getTemplate(itemid).getName();
            }
            costText = itemName + " x" + price + "個";
        }
        evdList[14] = costText;

        return evdList;
    }




    public int getCurrentMainQuestId(L1PcInstance pc) {
        int mainQuestBaseId = 995000; // 主線任務起始ID，可視你的需求調整
        int maxQuestIdOffset = 200;   // 支援編號 995000~995199
        // 取得角色目前主線階段
        int nowStage = pc.getQuest().get_now_stage(mainQuestBaseId);
        int nextStage = nowStage + 1;
        // 找尋 quest_stage==nextStage 的 quest_id
        for (int offset = 0; offset < maxQuestIdOffset; offset++) {
            int tryId = mainQuestBaseId + offset;
            HashMap<Integer, L1ServerQuestMob> questMap = _questMobList.get(tryId);
            if (questMap != null) {
                for (L1ServerQuestMob mob : questMap.values()) {
                    if (mob.get_quest_stage() == nextStage) {
                        return tryId;
                    }
                }
            }
        }
//        System.out.println("getCurrentMainQuestId: 找不到下一階段，全部任務已完成");
        return -1;
    }



    /**
     * 依任務編號取出任務階段物件
     * @param questId 任務編號
     * @return L1ServerQuestMob (預設回傳該任務的第一個 step，有多階段可自行調整)
     */
    public L1ServerQuestMob getQuestMobById(int questId) {
        HashMap<Integer, L1ServerQuestMob> questMap = _questMobList.get(questId);
        if (questMap != null && !questMap.isEmpty()) {
            // 直接回傳第一個
            for (L1ServerQuestMob mob : questMap.values()) {
                return mob;
            }
        }
        return null;
    }
    /**
     * 依 quest_id 及 step 取回對應 L1ServerQuestMob
     */
    public L1ServerQuestMob getQuestMobByIdAndStep(int questId, int questStep) {
        HashMap<Integer, L1ServerQuestMob> questMap = _questMobList.get(questId);
        if (questMap == null || questMap.isEmpty()) {
            return null;
        }
        return questMap.getOrDefault(questStep, questMap.values().iterator().next());
    }

    public void teleportToQuestMap(L1PcInstance pc, int quest_id) {
        L1ServerQuestMob quest = getQuestMobById(quest_id);
        if (quest == null) {
            pc.sendPackets(new S_ServerMessage("查無此任務"));
            return;
        }
        int x = quest.get_teleport_x();
        int y = quest.get_teleport_y();
        int mapid = quest.get_teleport_mapid();
        if (x == 0 || y == 0 || mapid <= 0) {
            pc.sendPackets(new S_ServerMessage("本任務尚未設定傳送座標"));
            return;
        }
        L1Teleport.teleport(pc, x, y, (short) mapid, 5, true);
        pc.sendPackets(new S_ServerMessage("已為您傳送到任務地圖"));
    }

    private void doTeleport(L1PcInstance pc, L1ServerQuestMob quest) {
        int x = quest.get_teleport_x();
        int y = quest.get_teleport_y();
        int mapid = quest.get_teleport_mapid();
        if (x == 0 || y == 0 || mapid <= 0) {
            pc.sendPackets(new S_ServerMessage("本任務尚未設定傳送座標"));
            return;
        }
        L1Teleport.teleport(pc, x, y, (short) mapid, 5, true);
        pc.sendPackets(new S_ServerMessage("已為您傳送到任務地圖"));
    }

    /**
     * 根據主線 base id 與目前完成階段，取得下一階段的 quest_id
     * @param mainQuestBaseId 主線任務系列起始ID（如995000）
     * @param nowStage        玩家已完成階段（例：0=未完成，1=第一階段...）
     * @return 下一階段的 quest_id，找不到則回傳 -1
     */
    public int getNextQuestIdByStage(int mainQuestBaseId, int nowStage) {
        int nextStage = nowStage + 1;
        int result = -1;
        int minQid = Integer.MAX_VALUE;
        for (Integer qid : _questMobList.keySet()) {
            if (qid >= mainQuestBaseId && qid < mainQuestBaseId + 100) { // 你主線範圍
                HashMap<Integer, L1ServerQuestMob> questMap = _questMobList.get(qid);
                for (L1ServerQuestMob mob : questMap.values()) {
                    if (mob.get_quest_stage() == nextStage && qid < minQid) {
                        minQid = qid;
                        result = qid;
                    }
                }
            }
        }
        return result;
    }
}
