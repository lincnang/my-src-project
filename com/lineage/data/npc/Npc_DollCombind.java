package com.lineage.data.npc;

import com.add.Tsai.dollBookCmd;
import com.add.Tsai.dollTable;
import com.lineage.config.ConfigDoll;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.*;
import com.lineage.server.serverpackets.*;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import com.lineage.server.thread.GeneralThreadPool;

/**
 * 娃娃卡合成系統
 * 技術老爹製作
 */
public class Npc_DollCombind extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_DollCombind.class);
    private final int[] doll1 = ConfigDoll.DOLL_LIST_1;
    private final int[] doll2 = ConfigDoll.DOLL_LIST_2;
    private final int[] doll3 = ConfigDoll.DOLL_LIST_3;
    private final int[] doll4 = ConfigDoll.DOLL_LIST_4;
    private final int[] doll5 = ConfigDoll.DOLL_LIST_5;
    private final int[] doll6 = ConfigDoll.DOLL_LIST_6;

    // 建議所有娃娃指令都加 doll_ 前綴
    public static boolean Cmd(L1PcInstance pc, String cmd) {
        try {
        } catch (Exception ignored) {
        }
        if (cmd != null) {
            cmd = cmd.trim();
        }
        if ("doolset2".equalsIgnoreCase(cmd)) {
            // 呼叫 dollBookCmd 的 DOLLAllSet (直接調用現有功能)
            dollBookCmd.get().DOLLAllSet(pc); // 你要import com.add.Tsai.dollBookCmd
            return true;
        }
        if ("Book_D01".equalsIgnoreCase(cmd)) {
            final StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                stringBuilder.append(dollTable.get().getDollHaveGroup(pc, i + 1)).append(",");
                stringBuilder.append(dollTable.get().getDollGroupSize(i + 1)).append(",");
            }
            final String[] clientStrAry = stringBuilder.toString().split(",");
            pc.sendPackets(new S_NPCTalkReturn(pc, "Book_D01", clientStrAry));
            return true;
        }
        // 首頁
        if ("dollcombind1".equalsIgnoreCase(cmd)) {
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind1"));
            return true;
        }
        // 分頁刷新
        if ("doll_A1".equalsIgnoreCase(cmd)) {
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME2 + pc.getDollrun2());
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind2", data));
            return true;
        }
        if ("doll_B1".equalsIgnoreCase(cmd)) {

            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME3 + pc.getDollrun3());
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind3", data));
            return true;
        }
        if ("doll_C1".equalsIgnoreCase(cmd)) {
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME4 + pc.getDollrun4());
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind4", data));
            return true;
        }
        if ("doll_D1".equalsIgnoreCase(cmd)) {
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME5 + pc.getDollrun5());
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind5", data));
            return true;
        }
        if ("doll_E1".equalsIgnoreCase(cmd)) {
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME6 + pc.getDollrun6());
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind6", data));
            return true;
        }

        // 合成機率調整
        if ("doll_A11".equalsIgnoreCase(cmd)) {
            if (ConfigDoll.CONSUME2 + pc.getDollrun2() < ConfigDoll.ZUIDAJILV2) {
                pc.setDollrun2(pc.getDollrun2() + ConfigDoll.ZENGJIAJILV2);
            }
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME2 + pc.getDollrun2());
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind2", data));
            return true;
        }
        if ("doll_A12".equalsIgnoreCase(cmd)) {
            if (ConfigDoll.CONSUME2 + pc.getDollrun2() > ConfigDoll.ZUIXIAOJILV2) {
                pc.setDollrun2(pc.getDollrun2() - ConfigDoll.ZENGJIAJILV2);
            }
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME2 + pc.getDollrun2());
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind2", data));
            return true;
        }
        if ("doll_B11".equalsIgnoreCase(cmd)) {
            if (ConfigDoll.CONSUME3 + pc.getDollrun3() < ConfigDoll.ZUIDAJILV3) {
                pc.setDollrun3(pc.getDollrun3() + ConfigDoll.ZENGJIAJILV3);
            }
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME3 + pc.getDollrun3());
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind3", data));
            return true;
        }
        if ("doll_B12".equalsIgnoreCase(cmd)) {
            if (ConfigDoll.CONSUME3 + pc.getDollrun3() > ConfigDoll.ZUIXIAOJILV3) {
                pc.setDollrun3(pc.getDollrun3() - ConfigDoll.ZENGJIAJILV3);
            }
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME3 + pc.getDollrun3());
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind3", data));
            return true;
        }
        if ("doll_C11".equalsIgnoreCase(cmd)) {
            if (ConfigDoll.CONSUME4 + pc.getDollrun4() < ConfigDoll.ZUIDAJILV4) {
                pc.setDollrun4(pc.getDollrun4() + ConfigDoll.ZENGJIAJILV4);
            }
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME4 + pc.getDollrun4());
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind4", data));
            return true;
        }
        if ("doll_C12".equalsIgnoreCase(cmd)) {
            if (ConfigDoll.CONSUME4 + pc.getDollrun4() > ConfigDoll.ZUIXIAOJILV4) {
                pc.setDollrun4(pc.getDollrun4() - ConfigDoll.ZENGJIAJILV4);
            }
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME4 + pc.getDollrun4());
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind4", data));
            return true;
        }
        if ("doll_D11".equalsIgnoreCase(cmd)) {
            if (ConfigDoll.CONSUME5 + pc.getDollrun5() < ConfigDoll.ZUIDAJILV5) {
                pc.setDollrun5(pc.getDollrun5() + ConfigDoll.ZENGJIAJILV5);
            }
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME5 + pc.getDollrun5());
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind5", data));
            return true;
        }
        if ("doll_D12".equalsIgnoreCase(cmd)) {
            if (ConfigDoll.CONSUME5 + pc.getDollrun5() > ConfigDoll.ZUIXIAOJILV5) {
                pc.setDollrun5(pc.getDollrun5() - ConfigDoll.ZENGJIAJILV5);
            }
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME5 + pc.getDollrun5());
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind5", data));
            return true;
        }
        if ("doll_E11".equalsIgnoreCase(cmd)) {
            if (ConfigDoll.CONSUME6 + pc.getDollrun6() < ConfigDoll.ZUIDAJILV6) {
                pc.setDollrun6(pc.getDollrun6() + ConfigDoll.ZENGJIAJILV6);
            }
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME6 + pc.getDollrun6());
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind6", data));
            return true;
        }
        if ("doll_E12".equalsIgnoreCase(cmd)) {
            if (ConfigDoll.CONSUME6 + pc.getDollrun6() > ConfigDoll.ZUIXIAOJILV6) {
                pc.setDollrun6(pc.getDollrun6() - ConfigDoll.ZENGJIAJILV6);
            }
            String[] data = new String[3];
            data[0] = String.valueOf(ConfigDoll.CONSUME6 + pc.getDollrun6());
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcombind6", data));
            return true;
        }

        // 合成主流程
        if (cmd.equalsIgnoreCase("doll_A") || cmd.equalsIgnoreCase("doll_A_total") ||
                cmd.equalsIgnoreCase("doll_B") || cmd.equalsIgnoreCase("doll_B_total") ||
                cmd.equalsIgnoreCase("doll_C") || cmd.equalsIgnoreCase("doll_C_total") ||
                cmd.equalsIgnoreCase("doll_D") || cmd.equalsIgnoreCase("doll_D_total") ||
                cmd.equalsIgnoreCase("doll_E") || cmd.equalsIgnoreCase("doll_E_total")) {
            new Npc_DollCombind().action(pc, null, cmd, 0);
            return true;
        }
        return false;
    }

    public static NpcExecutor get() {
        return new Npc_DollCombind();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dollcombind1"));
    }

    // 主要合成邏輯
    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        // ========== 主合成流程 ==========
        int chance = 0;
        int needcount = 0;
        int[] olddolls = null;
        int[] newdolls = null;
        final String[] data = new String[3];

        if (cmd.equalsIgnoreCase("doll_A") || cmd.equalsIgnoreCase("doll_A_total")) {
            chance = ConfigDoll.CONSUME2 + pc.getDollrun2();
            needcount = ConfigDoll.SHULIANG2 + pc.getDollCount();
            olddolls = doll1;
            newdolls = doll2;
            if (doll2 == null || doll2.length == 0) {
                pc.sendPackets(new S_SystemMessage("娃娃設定錯誤：DOLL_LIST_2 未設定或為空"));
                return;
            }
        } else if (cmd.equalsIgnoreCase("doll_B") || cmd.equalsIgnoreCase("doll_B_total")) {
            chance = ConfigDoll.CONSUME3 + pc.getDollrun3();
            needcount = ConfigDoll.SHULIANG3 + pc.getDollCount2();
            olddolls = doll2;
            newdolls = doll3;
            if (doll3 == null || doll3.length == 0) {
                pc.sendPackets(new S_SystemMessage("娃娃設定錯誤：DOLL_LIST_3 未設定或為空"));
                return;
            }
        } else if (cmd.equalsIgnoreCase("doll_C") || cmd.equalsIgnoreCase("doll_C_total")) {
            chance = ConfigDoll.CONSUME4 + pc.getDollrun4();
            needcount = ConfigDoll.SHULIANG4 + pc.getDollCount3();
            olddolls = doll3;
            newdolls = doll4;
            if (doll4 == null || doll4.length == 0) {
                pc.sendPackets(new S_SystemMessage("娃娃設定錯誤：DOLL_LIST_4 未設定或為空"));
                return;
            }
        } else if (cmd.equalsIgnoreCase("doll_D") || cmd.equalsIgnoreCase("doll_D_total")) {
            chance = ConfigDoll.CONSUME5 + pc.getDollrun5();
            needcount = ConfigDoll.SHULIANG5 + pc.getDollCount4();
            olddolls = doll4;
            newdolls = doll5;
            if (doll5 == null || doll5.length == 0) {
                pc.sendPackets(new S_SystemMessage("娃娃設定錯誤：DOLL_LIST_5 未設定或為空"));
                return;
            }
        } else if (cmd.equalsIgnoreCase("doll_E") || cmd.equalsIgnoreCase("doll_E_total")) {
            chance = ConfigDoll.CONSUME6 + pc.getDollrun6();
            needcount = ConfigDoll.SHULIANG6 + pc.getDollCount5();
            olddolls = doll5;
            newdolls = doll6;
            if (doll6 == null || doll6.length == 0) {
                pc.sendPackets(new S_SystemMessage("娃娃設定錯誤：DOLL_LIST_6 未設定或為空"));
                return;
            }
        } else {
            return;
        }

        // 檢查娃娃有沒有在外面
        for (Object babyObject : pc.getPetList().values().toArray()) {
            if (babyObject instanceof L1DollInstance || babyObject instanceof L1DollInstance2) {
                if (!pc.getPetList().isEmpty()) {
                    pc.sendPackets(new S_SystemMessage("請先收回娃娃"));
                    return;
                }
            }
        }

        // 判斷是否為批量合成模式
        boolean isTotalMode = cmd.toLowerCase().contains("_total");

        // 批量合成模式：異步執行避免卡頓
        if (isTotalMode) {
            final int[] fOlddolls = olddolls;
            final int[] fNewdolls = newdolls;
            final int fChance = chance;
            final int fNeedcount = needcount;
            final String fCmd = cmd;
            GeneralThreadPool.get().schedule(() -> doBatchCombine(pc, fCmd, fOlddolls, fNewdolls, fChance, fNeedcount), 100);
            return;
        }

        // ========== 單次合成模式（原有邏輯） ==========
        boolean enough = false;
        ArrayList<ItemConsume> consumeList = new ArrayList<>();

        // 堆疊數量判斷與消耗紀錄
        if (olddolls != null) {
            int totalCount = 0;
            outer:
            for (int olddoll : olddolls) {
                L1ItemInstance[] dolls = pc.getInventory().findItemsId(olddoll);
                if (dolls != null) {
                    for (L1ItemInstance doll : dolls) {
                        int remain = needcount - totalCount;
                        int stackCount = (int) doll.getCount();
                        int consumeCount = Math.min(remain, stackCount);
                        consumeList.add(new ItemConsume(doll, consumeCount));
                        totalCount += consumeCount;
                        if (totalCount >= needcount) break outer;
                    }
                }
            }
            enough = (totalCount >= needcount);
        }

        // 判斷並消耗材料，做合成
        if (enough) {
            boolean consumed = true;
            for (ItemConsume ic : consumeList) {
                if (!pc.getInventory().consumeItem(ic.itemInstance.getItemId(), ic.count)) {
                    consumed = false;
                    break;
                }
            }
            if (!consumed) {
                pc.sendPackets(new S_SystemMessage("消耗物品失敗，請聯絡管理員"));
                return;
            }

            // 重置調整參數
            pc.setDollCount(0);
            pc.setDollCount2(0);
            pc.setDollCount3(0);
            pc.setDollCount4(0);
            pc.setDollCount5(0);

            // 使用異步執行合成結果，避免阻塞
            final int fNewdoll = newdolls[ThreadLocalRandom.current().nextInt(newdolls.length)];
            final int fChance = chance;
            final ArrayList<ItemConsume> fConsumeList = new ArrayList<>(consumeList);
            final String[] fData = data;

            try {
                pc.sendHtmlCastGfx(fData);
            } catch (Exception ignored) {}

            GeneralThreadPool.get().schedule(() -> {
                try {
                    doSingleCombine(pc, true, fNewdoll, fChance, fConsumeList);
                } catch (Exception e) {
                    _log.error("娃娃合成失敗", e);
                }
            }, 2100);

        } else {
            pc.sendPackets(new S_SystemMessage("你的娃娃不足【" + needcount + "】個"));
            sendBackToPage(pc, cmd);
        }
    }

    // ======================== 單次合成結果處理 ========================
    private void doSingleCombine(L1PcInstance pc, boolean isPlayAnimation, int newdoll, int chance, ArrayList<ItemConsume> consumeList) {
        if (ThreadLocalRandom.current().nextInt(100) < chance) { // 合成成功
            final L1ItemInstance item = ItemTable.get().createItem(newdoll);
            int itemId = item.getItem().getItemId();
            final String newName = item.getLogName();
            final StringBuilder stringBuilder = new StringBuilder();
            final com.lineage.server.templates.L1DollHeCheng card1 =
                    com.lineage.server.datatables.DollHeChengTable.getInstance().getTemplate(itemId);
            if (card1 != null) {
                stringBuilder.append(card1.getGfxid()).append(",");
                if (card1.getNot() != 0) {
                    World.get().broadcastPacketToAllAsync(
                            new S_SystemMessage("【公告】玩家 " + pc.getName() + " 成功合成了娃娃卡「" + newName + "」!")
                    );
                }
            }
            final String[] msg = stringBuilder.toString().split(",");
            item.setIdentified(true);
            pc.getInventory().storeItem(item);
            pc.sendPackets(new S_Sound(20360));
            if (isPlayAnimation)
                pc.sendPackets(new S_NPCTalkReturn(pc, "wwhccg", msg));
            pc.sendPackets(new S_SystemMessage("恭喜你合成了" + item.getLogName()));
            pc.sendPackets(new S_PacketBoxGree(15));
        } else { // 合成失敗返還一個消耗的道具
            pc.sendPackets(new S_SystemMessage("合成娃娃失敗了。"));
            pc.sendPackets(new S_PacketBoxGree(16));
            int itemId = consumeList.get(ThreadLocalRandom.current().nextInt(consumeList.size())).itemInstance.getItemId();
            final L1ItemInstance item = ItemTable.get().createItem(itemId);
            final StringBuilder stringBuilder = new StringBuilder();
            final com.lineage.server.templates.L1DollHeCheng card1 =
                    com.lineage.server.datatables.DollHeChengTable.getInstance().getTemplate(itemId);
            if (card1 != null) {
                stringBuilder.append(card1.getGfxid()).append(",");
            }
            final String[] msg = stringBuilder.toString().split(",");
            item.setIdentified(true);
            pc.getInventory().storeItem(item);
            pc.sendPackets(new S_Sound(20468));
            if (isPlayAnimation)
                pc.sendPackets(new S_NPCTalkReturn(pc, "wwhcsb", msg));
            pc.sendPackets(new S_SystemMessage("很遺憾合成失敗返還" + item.getLogName()));
        }
    }

    // ======================== 批量合成流程（一次性計算） ========================
    private void doBatchCombine(L1PcInstance pc, String cmd, int[] olddolls, int[] newdolls, int chance, int needcount) {
        // ========== 步驟1：一次性收集所有材料 ==========
        final ArrayList<ItemConsume> consumeList = new ArrayList<>();
        int totalAvailable = 0;

        if (olddolls != null) {
            for (int olddoll : olddolls) {
                L1ItemInstance[] dolls = pc.getInventory().findItemsId(olddoll);
                if (dolls != null) {
                    for (L1ItemInstance doll : dolls) {
                        int count = (int) doll.getCount();
                        consumeList.add(new ItemConsume(doll, count));
                        totalAvailable += count;
                    }
                }
            }
        }

        if (totalAvailable < needcount) {
            pc.sendPackets(new S_SystemMessage("你的娃娃不足【" + needcount + "】個"));
            sendBackToPage(pc, cmd);
            return;
        }

        int maxCombine = totalAvailable / needcount;

        // ========== 步驟2：一次性扣除所有材料 ==========
        for (ItemConsume ic : consumeList) {
            pc.getInventory().consumeItem(ic.itemInstance.getItemId(), ic.count);
        }

        // 重置調整參數
        pc.setDollCount(0);
        pc.setDollCount2(0);
        pc.setDollCount3(0);
        pc.setDollCount4(0);
        pc.setDollCount5(0);

        // ========== 步驟3：一次性計算結果（按成功率直接計算） ==========
        final Map<Integer, Integer> successItems = new HashMap<>();
        final Map<Integer, Integer> failReturnItems = new HashMap<>();
        final Map<Integer, String> announceCards = new HashMap<>();

        int successCount = 0;
        int failCount = 0;

        for (int i = 0; i < maxCombine; i++) {
            int newdoll = newdolls[ThreadLocalRandom.current().nextInt(newdolls.length)];
            if (ThreadLocalRandom.current().nextInt(100) < chance) {
                // 成功
                successCount++;
                successItems.merge(newdoll, 1, Integer::sum);
                // 檢查是否需要公告
                final com.lineage.server.templates.L1DollHeCheng card1 =
                        com.lineage.server.datatables.DollHeChengTable.getInstance().getTemplate(newdoll);
                if (card1 != null && card1.getNot() != 0) {
                    L1ItemInstance tempItem = ItemTable.get().createItem(newdoll);
                    if (tempItem != null && tempItem.getItem() != null) {
                        announceCards.put(newdoll, tempItem.getLogName());
                    }
                }
            } else {
                // 失敗 - 返還一階卡
                failCount++;
                int backId = olddolls[ThreadLocalRandom.current().nextInt(olddolls.length)];
                failReturnItems.merge(backId, 1, Integer::sum);
            }
        }

        // ========== 步驟4：一次性給予所有結果道具 ==========
        // 給予成功的道具
        for (Map.Entry<Integer, Integer> entry : successItems.entrySet()) {
            L1ItemInstance item = ItemTable.get().createItem(entry.getKey());
            if (item != null && item.getItem() != null) {
                item.setIdentified(true);
                if (entry.getValue() > 1) {
                    item.setCount(entry.getValue());
                }
                pc.getInventory().storeItem(item);
            }
        }

        // 給予失敗返還的道具
        for (Map.Entry<Integer, Integer> entry : failReturnItems.entrySet()) {
            L1ItemInstance item = ItemTable.get().createItem(entry.getKey());
            if (item != null && item.getItem() != null) {
                item.setIdentified(true);
                if (entry.getValue() > 1) {
                    item.setCount(entry.getValue());
                }
                pc.getInventory().storeItem(item);
            }
        }

        // ========== 步驟5：發送公告和結果 ==========
        if (!announceCards.isEmpty()) {
            StringBuilder announceMsg = new StringBuilder();
            announceMsg.append("【公告】玩家 ").append(pc.getName()).append(" 在批量合成中成功合成了");
            if (announceCards.size() == 1) {
                announceMsg.append("娃娃卡「").append(announceCards.values().iterator().next()).append("」！");
            } else {
                announceMsg.append("多張稀有娃娃卡！");
            }
            World.get().broadcastPacketToAllAsync(new S_SystemMessage(announceMsg.toString()));
        }

        pc.sendPackets(new S_Sound(successCount > 0 ? 20360 : 20468));
        pc.sendPackets(new S_SystemMessage("批量合成完成！成功: " + successCount + " 次，失敗: " + failCount + " 次"));
        if (successCount > 0) {
            pc.sendPackets(new S_PacketBoxGree(15));
        } else {
            pc.sendPackets(new S_PacketBoxGree(16));
        }

        sendBackToPage(pc, cmd);
    }

    // 返回對應的合成頁面
    private void sendBackToPage(L1PcInstance pc, String cmd) {
        String htmlPage = "";
        String[] data = new String[3];

        if (cmd.toLowerCase().contains("doll_a")) {
            htmlPage = "dollcombind2";
            data[0] = String.valueOf(ConfigDoll.CONSUME2);
        } else if (cmd.toLowerCase().contains("doll_b")) {
            htmlPage = "dollcombind3";
            data[0] = String.valueOf(ConfigDoll.CONSUME3);
        } else if (cmd.toLowerCase().contains("doll_c")) {
            htmlPage = "dollcombind4";
            data[0] = String.valueOf(ConfigDoll.CONSUME4);
        } else if (cmd.toLowerCase().contains("doll_d")) {
            htmlPage = "dollcombind5";
            data[0] = String.valueOf(ConfigDoll.CONSUME5);
        } else if (cmd.toLowerCase().contains("doll_e")) {
            htmlPage = "dollcombind6";
            data[0] = String.valueOf(ConfigDoll.CONSUME6);
        }

        if (!htmlPage.isEmpty())
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), htmlPage, data));
    }

    private static class ItemConsume {
        L1ItemInstance itemInstance;
        int count;

        ItemConsume(L1ItemInstance itemInstance, int count) {
            this.itemInstance = itemInstance;
            this.count = count;
        }
    }
}
