package com.lineage.data.npc;

import com.lineage.config.ConfigMagic;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.MagicHeChengTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1MagicHeCheng;
import com.lineage.server.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.lineage.server.thread.GeneralThreadPool;

/**
 * 魔法卡合成NPC主控類
 * 指令全部改為 magic_ 開頭，跟娃娃/聖物統一
 * 技術老爹 製作
 */
public class Npc_MagicCombind extends NpcExecutor {
    // 供合成流程使用的簡單結構
    private static class ItemConsume {
        L1ItemInstance itemInstance;
        int count;
        ItemConsume(L1ItemInstance itemInstance, int count) {
            this.itemInstance = itemInstance;
            this.count = count;
        }
    }

    private final int[] Magic1 = ConfigMagic.Magic_LIST_1;
    private final int[] Magic2 = ConfigMagic.Magic_LIST_2;
    private final int[] Magic3 = ConfigMagic.Magic_LIST_3;
    private final int[] Magic4 = ConfigMagic.Magic_LIST_4;
    private final int[] Magic5 = ConfigMagic.Magic_LIST_5;

    public static NpcExecutor get() {
        return new Npc_MagicCombind();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "magicchang1"));
    }

    /** 靜態指令入口，建議一律走這裡！ */
    public static boolean Cmd(L1PcInstance pc, String cmd) {
        // 首頁
        if ("magiccombind1".equalsIgnoreCase(cmd)) {
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magicchang1"));
            return true;
        }
        if ("magicchang1".equalsIgnoreCase(cmd)) {
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magicchang1"));
            return true;
        }
        // 分頁刷新
        if ("magic_A1".equalsIgnoreCase(cmd)) {
            String[] data = { String.valueOf(ConfigMagic.CONSUME2 + pc.getMagicrun2()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magicchang2", data));
            return true;
        }
        if ("magic_B1".equalsIgnoreCase(cmd)) {
            String[] data = { String.valueOf(ConfigMagic.CONSUME3 + pc.getMagicrun3()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magicchang3", data));
            return true;
        }
        if ("magic_C1".equalsIgnoreCase(cmd)) {
            String[] data = { String.valueOf(ConfigMagic.CONSUME4 + pc.getMagicrun4()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magicchang4", data));
            return true;
        }
        if ("magic_D1".equalsIgnoreCase(cmd)) {
            String[] data = { String.valueOf(ConfigMagic.CONSUME5 + pc.getMagicrun5()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magicchang5", data));
            return true;
        }
        // 機率調整
        if ("magic_A11".equalsIgnoreCase(cmd)) {
            if (ConfigMagic.CONSUME2 + pc.getMagicrun2() < ConfigMagic.ZUIDAJILV2)
                pc.setMagicrun2(pc.getMagicrun2() + ConfigMagic.ZENGJIAJILV2);
            String[] data = { String.valueOf(ConfigMagic.CONSUME2 + pc.getMagicrun2()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magicchang2", data));
            return true;
        }
        if ("magic_A12".equalsIgnoreCase(cmd)) {
            if (ConfigMagic.CONSUME2 + pc.getMagicrun2() > ConfigMagic.ZUIXIAOJILV2)
                pc.setMagicrun2(pc.getMagicrun2() - ConfigMagic.ZENGJIAJILV2);
            String[] data = { String.valueOf(ConfigMagic.CONSUME2 + pc.getMagicrun2()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magicchang2", data));
            return true;
        }
        if ("magic_B11".equalsIgnoreCase(cmd)) {
            if (ConfigMagic.CONSUME3 + pc.getMagicrun3() < ConfigMagic.ZUIDAJILV3)
                pc.setMagicrun3(pc.getMagicrun3() + ConfigMagic.ZENGJIAJILV3);
            String[] data = { String.valueOf(ConfigMagic.CONSUME3 + pc.getMagicrun3()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magicchang3", data));
            return true;
        }
        if ("magic_B12".equalsIgnoreCase(cmd)) {
            if (ConfigMagic.CONSUME3 + pc.getMagicrun3() > ConfigMagic.ZUIXIAOJILV3)
                pc.setMagicrun3(pc.getMagicrun3() - ConfigMagic.ZENGJIAJILV3);
            String[] data = { String.valueOf(ConfigMagic.CONSUME3 + pc.getMagicrun3()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magicchang3", data));
            return true;
        }
        if ("magic_C11".equalsIgnoreCase(cmd)) {
            if (ConfigMagic.CONSUME4 + pc.getMagicrun4() < ConfigMagic.ZUIDAJILV4)
                pc.setMagicrun4(pc.getMagicrun4() + ConfigMagic.ZENGJIAJILV4);
            String[] data = { String.valueOf(ConfigMagic.CONSUME4 + pc.getMagicrun4()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magicchang4", data));
            return true;
        }
        if ("magic_C12".equalsIgnoreCase(cmd)) {
            if (ConfigMagic.CONSUME4 + pc.getMagicrun4() > ConfigMagic.ZUIXIAOJILV4)
                pc.setMagicrun4(pc.getMagicrun4() - ConfigMagic.ZENGJIAJILV4);
            String[] data = { String.valueOf(ConfigMagic.CONSUME4 + pc.getMagicrun4()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magicchang4", data));
            return true;
        }
        if ("magic_D11".equalsIgnoreCase(cmd)) {
            if (ConfigMagic.CONSUME5 + pc.getMagicrun5() < ConfigMagic.ZUIDAJILV5)
                pc.setMagicrun5(pc.getMagicrun5() + ConfigMagic.ZENGJIAJILV5);
            String[] data = { String.valueOf(ConfigMagic.CONSUME5 + pc.getMagicrun5()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magicchang5", data));
            return true;
        }
        if ("magic_D12".equalsIgnoreCase(cmd)) {
            if (ConfigMagic.CONSUME5 + pc.getMagicrun5() > ConfigMagic.ZUIXIAOJILV5)
                pc.setMagicrun5(pc.getMagicrun5() - ConfigMagic.ZENGJIAJILV5);
            String[] data = { String.valueOf(ConfigMagic.CONSUME5 + pc.getMagicrun5()), "", "" };
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "magicchang5", data));
            return true;
        }
        // 合成主流程
        if (cmd.equalsIgnoreCase("magic_A") || cmd.equalsIgnoreCase("magic_A_total")
                || cmd.equalsIgnoreCase("magic_B") || cmd.equalsIgnoreCase("magic_B_total")
                || cmd.equalsIgnoreCase("magic_C") || cmd.equalsIgnoreCase("magic_C_total")
                || cmd.equalsIgnoreCase("magic_D") || cmd.equalsIgnoreCase("magic_D_total")) {
            new Npc_MagicCombind().action(pc, null, cmd, 0);
            return true;
        }
        return false;
    }

    /** 合成主邏輯，支援 magic_A, magic_B, magic_C, magic_D 指令 */
    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        int[] oldMagics = null;
        int[] newMagics = null;
        int chance = 0;
        int needcount = 0;
        switch (cmd) {
            case "magic_A":
            case "magic_A_total":
                chance = ConfigMagic.CONSUME2 + pc.getMagicrun2();
                needcount = ConfigMagic.SHULIANG2 + pc.getMagicCount();
                oldMagics = Magic1;
                newMagics = Magic2;
                break;
            case "magic_B":
            case "magic_B_total":
                chance = ConfigMagic.CONSUME3 + pc.getMagicrun3();
                needcount = ConfigMagic.SHULIANG3 + pc.getMagicCount2();
                oldMagics = Magic2;
                newMagics = Magic3;
                break;
            case "magic_C":
            case "magic_C_total":
                chance = ConfigMagic.CONSUME4 + pc.getMagicrun4();
                needcount = ConfigMagic.SHULIANG4 + pc.getMagicCount3();
                oldMagics = Magic3;
                newMagics = Magic4;
                break;
            case "magic_D":
            case "magic_D_total":
                chance = ConfigMagic.CONSUME5 + pc.getMagicrun5();
                needcount = ConfigMagic.SHULIANG5 + pc.getMagicCount4();
                oldMagics = Magic4;
                newMagics = Magic5;
                break;
            default:
                pc.sendPackets(new S_SystemMessage("指令錯誤!"));
                return;
        }

        // 判斷是否為批量合成模式
        boolean isTotalMode = cmd.toLowerCase().contains("_total");

        // 批量合成模式：異步執行避免卡頓
        if (isTotalMode) {
            final int[] fOldMagics = oldMagics;
            final int[] fNewMagics = newMagics;
            final int fChance = chance;
            final int fNeedcount = needcount;
            final String fCmd = cmd;
            GeneralThreadPool.get().schedule(() -> doBatchCombine(pc, fCmd, fOldMagics, fNewMagics, fChance, fNeedcount), 100);
            return;
        }

        // ========== 單次合成模式（原有邏輯） ==========
        boolean enough = false;
        ArrayList<ItemConsume> consumeList = new ArrayList<>();
        if (oldMagics != null) {
            int totalCount = 0;
            outer:
            for (int oldMagic : oldMagics) {
                L1ItemInstance[] cards = pc.getInventory().findItemsId(oldMagic);
                if (cards != null) {
                    for (L1ItemInstance card : cards) {
                        int remain = needcount - totalCount;
                        int stackCount = (int) card.getCount();
                        int consumeCount = Math.min(remain, stackCount);
                        consumeList.add(new ItemConsume(card, consumeCount));
                        totalCount += consumeCount;
                        if (totalCount >= needcount) break outer;
                    }
                }
            }
            enough = (totalCount >= needcount);
        }

        if (!enough) {
            pc.sendPackets(new S_SystemMessage("你的技能卡不足【" + needcount + "】個"));
            return;
        }

        // 材料足夠才進入合成
        for (ItemConsume ic : consumeList) {
            pc.getInventory().removeItem(ic.itemInstance, ic.count);
        }

        // 合成完後重置參數
        pc.setMagicCount(0);
        pc.setMagicCount2(0);
        pc.setMagicCount3(0);
        pc.setMagicCount4(0);
        pc.setMagicrun2(0);
        pc.setMagicrun3(0);
        pc.setMagicrun4(0);
        pc.setMagicrun5(0);

        // 執行單次合成
        int newMagic = newMagics[ThreadLocalRandom.current().nextInt(newMagics.length)];
        doSingleCombine(pc, newMagic, chance, consumeList);
    }

    // ======================== 單次合成結果處理 ========================
    private void doSingleCombine(L1PcInstance pc, int newMagic, int chance, ArrayList<ItemConsume> consumeList) {
        if (ThreadLocalRandom.current().nextInt(100) < chance) {
            // 成功
            final L1ItemInstance item = ItemTable.get().createItem(newMagic);
            int itemId = item.getItem().getItemId();
            final StringBuilder stringBuilder = new StringBuilder();
            final L1MagicHeCheng card1 = MagicHeChengTable.getInstance().getTemplate(itemId);
            if (card1 != null) {
                stringBuilder.append(card1.getGfxid()).append(",");
                if (card1.getNot() != 0) {
                    World.get().broadcastPacketToAllAsync(
                            new S_BlueMessage(166, "\\f=恭喜玩家\\fN【" + pc.getName() + "】\\f=合成了技能卡\\fN【" + item.getLogName() + "】"));
                }
            }
            item.setIdentified(true);
            pc.getInventory().storeItem(item);
            pc.sendPacketsX8(new S_Sound(20360));
            pc.sendPackets(new S_PacketBoxGree(15));
            pc.sendPackets(new S_SystemMessage("恭喜你合成了" + item.getLogName()));
        } else {
            // 失敗返還隨機素材卡
            pc.sendPackets(new S_SystemMessage("合成技能卡失敗了。"));
            pc.sendPackets(new S_PacketBoxGree(16));
            int itemId = consumeList.get(ThreadLocalRandom.current().nextInt(consumeList.size())).itemInstance.getItemId();
            final L1ItemInstance item = ItemTable.get().createItem(itemId);
            item.setIdentified(true);
            pc.getInventory().storeItem(item);
            pc.sendPacketsX8(new S_Sound(20468));
            pc.sendPackets(new S_SystemMessage("很遺憾合成失敗返還" + item.getLogName()));
        }
    }

    // ======================== 批量合成流程（一次性計算） ========================
    private void doBatchCombine(L1PcInstance pc, String cmd, int[] oldMagics, int[] newMagics, int chance, int needcount) {
        // ========== 步驟1：一次性收集所有材料 ==========
        final ArrayList<ItemConsume> consumeList = new ArrayList<>();
        int totalAvailable = 0;

        if (oldMagics != null) {
            for (int oldMagic : oldMagics) {
                L1ItemInstance[] cards = pc.getInventory().findItemsId(oldMagic);
                if (cards != null) {
                    for (L1ItemInstance card : cards) {
                        int count = (int) card.getCount();
                        consumeList.add(new ItemConsume(card, count));
                        totalAvailable += count;
                    }
                }
            }
        }

        if (totalAvailable < needcount) {
            pc.sendPackets(new S_SystemMessage("你的技能卡不足【" + needcount + "】個"));
            sendBackToPage(pc, cmd);
            return;
        }

        int maxCombine = totalAvailable / needcount;

        // ========== 步驟2：一次性扣除所有材料 ==========
        for (ItemConsume ic : consumeList) {
            pc.getInventory().removeItem(ic.itemInstance, ic.count);
        }

        // 重置全部階段加成
        pc.setMagicCount(0);
        pc.setMagicCount2(0);
        pc.setMagicCount3(0);
        pc.setMagicCount4(0);
        pc.setMagicrun2(0);
        pc.setMagicrun3(0);
        pc.setMagicrun4(0);
        pc.setMagicrun5(0);

        // ========== 步驟3：一次性計算結果（按成功率直接計算） ==========
        final Map<Integer, Integer> successItems = new HashMap<>();
        final Map<Integer, Integer> failReturnItems = new HashMap<>();
        final Map<Integer, String> announceCards = new HashMap<>();

        int successCount = 0;
        int failCount = 0;

        for (int i = 0; i < maxCombine; i++) {
            int newMagic = newMagics[ThreadLocalRandom.current().nextInt(newMagics.length)];
            if (ThreadLocalRandom.current().nextInt(100) < chance) {
                // 成功
                successCount++;
                successItems.merge(newMagic, 1, Integer::sum);
                // 檢查是否需要公告
                final L1MagicHeCheng card1 = MagicHeChengTable.getInstance().getTemplate(newMagic);
                if (card1 != null && card1.getNot() != 0) {
                    L1ItemInstance tempItem = ItemTable.get().createItem(newMagic);
                    if (tempItem != null && tempItem.getItem() != null) {
                        announceCards.put(newMagic, tempItem.getLogName());
                    }
                }
            } else {
                // 失敗 - 返還一階卡
                failCount++;
                int backId = oldMagics[ThreadLocalRandom.current().nextInt(oldMagics.length)];
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
            for (String cardName : announceCards.values()) {
                World.get().broadcastPacketToAllAsync(
                        new S_BlueMessage(166, "\\f=恭喜玩家\\fN【" + pc.getName() + "】\\f=合成了技能卡\\fN【" + cardName + "】"));
            }
        }

        pc.sendPacketsX8(new S_Sound(successCount > 0 ? 20360 : 20468));
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

        if (cmd.toLowerCase().contains("magic_a")) {
            htmlPage = "magicchang2";
            data[0] = String.valueOf(ConfigMagic.CONSUME2);
        } else if (cmd.toLowerCase().contains("magic_b")) {
            htmlPage = "magicchang3";
            data[0] = String.valueOf(ConfigMagic.CONSUME3);
        } else if (cmd.toLowerCase().contains("magic_c")) {
            htmlPage = "magicchang4";
            data[0] = String.valueOf(ConfigMagic.CONSUME4);
        } else if (cmd.toLowerCase().contains("magic_d")) {
            htmlPage = "magicchang5";
            data[0] = String.valueOf(ConfigMagic.CONSUME5);
        }

        if (!htmlPage.isEmpty())
            pc.sendPackets(new S_NPCTalkReturn(pc.getId(), htmlPage, data));
    }
}
