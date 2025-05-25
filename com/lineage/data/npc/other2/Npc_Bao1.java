package com.lineage.data.npc.other2;

import com.lineage.config.ConfigQuest;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcQuest;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 每日任務 整合處理：支援 bao_quest1~5 及獎勵領取
 * by 老爹修改
 */
public class Npc_Bao1 extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Bao1.class);
    private Npc_Bao1() {}

    public static NpcExecutor get() {
        return new Npc_Bao1();
    }

    @Override
    public int type() {
        return 3;
    }

    // 任務指令對應表
    private enum BaoQuestData {
        BAO1("bao_quest1", L1PcQuest.BAO_QUEST_1, 32775, 33035, (short) 2071),
        BAO2("bao_quest2", L1PcQuest.BAO_QUEST_2, 32775, 32629, (short) 2072),
        BAO3("bao_quest3", L1PcQuest.BAO_QUEST_3, 32750, 32815, (short) 2073),
        BAO4("bao_quest4", L1PcQuest.BAO_QUEST_4, 32696, 32851, (short) 2074),
        BAO5("bao_quest5", L1PcQuest.BAO_QUEST_5, 32706, 32744, (short) 2075);

        final String cmd;
        final int questId;
        final int x, y;
        final short mapId;

        BaoQuestData(String cmd, int questId, int x, int y, short mapId) {
            this.cmd = cmd;
            this.questId = questId;
            this.x = x;
            this.y = y;
            this.mapId = mapId;
        }

        static BaoQuestData fromCommand(String cmd) {
            for (BaoQuestData q : values()) {
                if (q.cmd.equalsIgnoreCase(cmd)) return q;
            }
            return null;
        }
    }

    private static int[][] getQuestItems(int questId) {
        switch (questId) {
            case L1PcQuest.BAO_QUEST_1:
                return ConfigQuest.BAO1_QUEST_ITEM;
            case L1PcQuest.BAO_QUEST_2:
                return ConfigQuest.BAO2_QUEST_ITEM;
            case L1PcQuest.BAO_QUEST_3:
                return ConfigQuest.BAO3_QUEST_ITEM;
            case L1PcQuest.BAO_QUEST_4:
                return ConfigQuest.BAO4_QUEST_ITEM;
            case L1PcQuest.BAO_QUEST_5:
                return ConfigQuest.BAO5_QUEST_ITEM;
        }
        return null;
    }

    // 處理玩家輸入的指令
    public static boolean Cmd(L1PcInstance pc, String cmd) throws Exception {
        BaoQuestData questData = BaoQuestData.fromCommand(cmd);
        if (questData != null) {
            if (pc.getQuest().isEnd(questData.questId)) {
                pc.sendPackets(new S_SystemMessage("您今日已完成任務。"));
                return true;
            }
            if (pc.getQuest().isStart(questData.questId)) {
                pc.sendPackets(new S_SystemMessage("任務進行中，請收集指定道具後使用。"));
                L1Teleport.teleport(pc, questData.x, questData.y, questData.mapId, 2, true);
                return true;
            }
            if (pc.getMeteLevel() < ConfigQuest.Bao1_Quest_Levelmet) {
                pc.sendPackets(new S_SystemMessage("您未達 " + ConfigQuest.Bao1_Quest_Levelmet + " 轉，無法接取任務。"));
                return true;
            }
            int totalLevel = pc.getLevel() + ((pc.getMeteLevel() - ConfigQuest.Bao1_Quest_Levelmet) * 99);
            if (totalLevel < ConfigQuest.Bao1_Quest_Lv) {
                pc.sendPackets(new S_SystemMessage("您未達 " + ConfigQuest.Bao1_Quest_Lv + " 等，無法接取任務。"));
                return true;
            }
            pc.getQuest().set_step(questData.questId, L1PcQuest.QUEST_START);
            pc.sendPackets(new S_SystemMessage("任務接取成功，請收集道具後換取獎勵。"));
            L1Teleport.teleport(pc, questData.x, questData.y, questData.mapId, 2, true);
            return true;
        }

        if (cmd.startsWith("bao_gift")) {
            int questId = -1;
            int[][] rewardItems = null;

            switch (cmd) {
                case "bao_gift1":
                    questId = L1PcQuest.BAO_QUEST_1;
                    rewardItems = ConfigQuest.BAO1_REWARD_ITEM;
                    break;
                case "bao_gift2":
                    questId = L1PcQuest.BAO_QUEST_2;
                    rewardItems = ConfigQuest.BAO2_REWARD_ITEM;
                    break;
                case "bao_gift3":
                    questId = L1PcQuest.BAO_QUEST_3;
                    rewardItems = ConfigQuest.BAO3_REWARD_ITEM;
                    break;
                case "bao_gift4":
                    questId = L1PcQuest.BAO_QUEST_4;
                    rewardItems = ConfigQuest.BAO4_REWARD_ITEM;
                    break;
                case "bao_gift5":
                    questId = L1PcQuest.BAO_QUEST_5;
                    rewardItems = ConfigQuest.BAO5_REWARD_ITEM;
                    break;
            }

            if (questId == -1 || rewardItems == null) {
                pc.sendPackets(new S_SystemMessage("任務資料錯誤，請聯繫管理員。"));
                return true;
            }

            if (pc.getQuest().isEnd(questId)) {
                pc.sendPackets(new S_SystemMessage("您今日已領取過獎勵。"));
                return true;
            }

            if (!pc.getQuest().isStart(questId)) {
                pc.sendPackets(new S_SystemMessage("尚未接任務。"));
                return true;
            }

            int[][] questItems = getQuestItems(questId);
            if (questItems == null) {
                pc.sendPackets(new S_SystemMessage("任務道具設定錯誤，請聯繫管理員。"));
                return true;
            }

            boolean error = false;
            int size = questItems.length;
            for (int i = 0; i < size; i++) {
                int itemId = questItems[i][0];
                int count = questItems[i][1];

                if (!pc.getInventory().checkItem(itemId, count)) {
                    pc.sendPackets(new S_SystemMessage("尚未收集完任務道具，請繼續努力！"));
                    error = true;
                    break;
                }
            }

            if (!error) {
                for (int i = 0; i < size; i++) {
                    pc.getInventory().consumeItem(questItems[i][0], questItems[i][1]);
                }
                pc.getQuest().set_end(questId);
                try {
                    for (int[] item : rewardItems) {
                        CreateNewItem.createNewItem(pc, item[0], item[1]);
                    }
                } catch (Exception e) {
                    pc.sendPackets(new S_SystemMessage("發送獎勵失敗，請聯繫管理員。"));
                    e.printStackTrace();
                }
                pc.sendPackets(new S_SystemMessage("任務完成，恭喜獲得獎勵！"));
                World.get().broadcastPacketToAll((new S_PacketBoxGree(6)));
                pc.save();
            }
            return true;
        }

        return false;
    }
}