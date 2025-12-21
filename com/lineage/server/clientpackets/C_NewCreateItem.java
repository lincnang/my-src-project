package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.CraftConfigTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.CraftItemForNpc;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.person.Custom_Packet;
import com.lineage.server.person.ItemCraft_NpcId;
import com.lineage.server.person.ItemCraft_Read;
import com.lineage.server.person.Read_Sha;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1CraftItem;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.ClassLimitUtils;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 樂天堂火神製作(DB化)
 */
public class C_NewCreateItem extends ClientBasePacket {
    private static final String C_NEW_CREATEITEM = "[C] C_NewCreateItem";
    private static final Log _log = LogFactory.getLog(C_NewCreateItem.class);

    // 節流機制：防止製作請求被濫用
    private static final long CRAFT_COOLDOWN_MS = 200; // 0.2秒冷卻時間
    private static final ConcurrentHashMap<Integer, Long> _lastCraftTime = new ConcurrentHashMap<>();

    // 清理機制
    private static volatile long _lastCleanupTime = 0;
    private static final long CLEANUP_INTERVAL_MS = 30 * 60 * 1000; // 30分鐘清理一次
    private static final int MAX_IDLE_TIME_MS = 10 * 60 * 1000; // 10分鐘未使用則清理

    @Override
    public void start(final byte[] decrypt, final ClientExecutor client) {
        try {
            final L1PcInstance pc = client.getActiveChar();
            if (pc == null) {
                return;
            }

            // === 節流檢查（防止製作請求濫用） ===
            int pcId = pc.getId();
            Long lastUsed = _lastCraftTime.get(pcId);
            long now = System.currentTimeMillis();
            if (lastUsed != null && now - lastUsed < CRAFT_COOLDOWN_MS) {
                return; // 冷卻中，直接返回
            }
            _lastCraftTime.put(pcId, now);

            // 定期清理過期的資料
            cleanupExpiredEntries(now);

            // 資料載入
            read(decrypt);
            final int type = this.readH();
            switch (type) {
                case 54:// 比較製作物品列表，如果不同則發送新的物品製作列表
                    int length = this.readH();
                    byte[] sha1 = readByte(length);
                    Read_Sha.readSha sha = Read_Sha.readSha.parseFrom(sha1);
                    // 校驗客戶端製作列表和服務器的是否相同
                    //System.out.println("客戶端SHA:" + CraftConfigTable.read(sha.getSha().toByteArray()));
                    //System.out.println("服務器SHA:" + CraftConfigTable.read(CraftConfigTable.get().getSha()));
                    if (Arrays.equals(sha.getSha().toByteArray(), CraftConfigTable.get().getSha())) {
                        pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.ITEMCRAFTLIST, 3));
                    } else {
                        pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.ITEMCRAFTLIST, 0));
                        for (int i = 0; i < CraftConfigTable.get().get_AllNpcMakeItemForList().size(); i++) {
                            pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.ITEMCRAFTLIST, 1, i));
                        }
                        pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.ITEMCRAFTLIST, 2));
                    }
                    break;
                case 56:// 返回NPC的製作列表
                    length = readH();
                    byte[] data = readByte(length);
                    ItemCraft_NpcId.readCraftNpcId craftNpc = ItemCraft_NpcId.readCraftNpcId.parseFrom(data);
                    L1Object obj = World.get().findObject(craftNpc.getNpcid());
                    if ((obj != null) && ((obj instanceof L1NpcInstance))) {
                        L1NpcInstance npc = (L1NpcInstance) obj;
                        Map<Integer, CraftItemForNpc> npcMakeItemActions = CraftConfigTable.get().readItemList(npc.getNpcId());
                        if (npcMakeItemActions != null) {
                            pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.NPCCRAFTLIST, npc.getNpcId(), pc));
                        } else {
                            pc.sendPackets(new S_NewCreateItem(null));
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("讀取製作NPC出錯！"));
                    }
                    break;
                case 58:// 製作道具
                    //if (pc.getInventory().calcWeightpercent() >= 90) {
                    //pc.sendPackets(new S_SystemMessage("製作失敗: 角色負重在 90%以上."));
                    //return;
                    //}
                    if (pc.getInventory().getWeight240() >= 197) { // 重量過重
                        pc.sendPackets(new S_ServerMessage(82)); // 82無法領取，請確認負重與道具欄位。
                        return;
                    }
                    if (pc.getInventory().getSize() > 180) {
                        pc.sendPackets(new S_ServerMessage(263)); // 一個角色最多可攜帶180個道具。
                        return;
                    }
                    length = this.readH();
                    byte[] data1 = readByte(length);
                    // System.out.println("道具製作封包:"+data1.length+"\r\n"+PacketPrint.get().printData(data1,data1.length));
                    ItemCraft_Read.ItemCraftRead craftList = null;
                    try {
                        craftList = ItemCraft_Read.ItemCraftRead.parseFrom(data1);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    // System.out.println("處理後的道具製作封包:" + craftList.toBuilder());
                    obj = World.get().findObject(craftList.getNpcObjId());
                    if (!(obj != null) && ((obj instanceof L1NpcInstance))) {
                        _log.error("兌換對不像不是NPC，玩家名稱:" + pc.getName());
                        return;
                    }
                    L1NpcInstance npc = (L1NpcInstance) obj;
                    // 允許遠程製作 - 移除距離和地圖檢查
                    // int difflocx = Math.abs(pc.getX() - npc.getX());
                    // int difflocy = Math.abs(pc.getY() - npc.getY());
                    // if ((pc.getMapId() != npc.getMapId()) || (difflocx > 10) || (difflocy > 10)) {
                    //     pc.sendPackets(new S_SystemMessage("您離NPC過遠，請走近後再試"));
                    //     return;
                    // }
                    Map<Integer, CraftItemForNpc> npcMakeItemActions = CraftConfigTable.get().readItemList(npc.getNpcId());
                    if (npcMakeItemActions == null) {
                        _log.error("無此兌換NPCID:" + npc.getNpcId() + "，玩家：" + pc.getName());
                        return;
                    }
                    CraftItemForNpc npcMakeItemAction = (CraftItemForNpc) npcMakeItemActions.get(craftList.getActionId());
                    if (npcMakeItemAction == null) {
                        _log.error("為止兌換物品ID：" + craftList.getActionId());
                        return;
                    }
                    if (!ClassLimitUtils.isClassAllowed(npcMakeItemAction.getClassLimit(), pc)) {
                        pc.sendPackets(new S_SystemMessage("你的職業無法製作此配方"));
                        return;
                    }
                    List<Integer> polyIds = npcMakeItemAction.getPolyList();
                    if (polyIds != null && polyIds.size() > 0) {
                        boolean istrue = false;
                        for (int polyId : polyIds) {
                            if (pc.getTempCharGfx() == polyId) {
                                istrue = true;
                                break;
                            }
                        }
                        if (!istrue) {
                            _log.info("玩家不符合變身要求：" + pc.getName());
                            pc.sendPackets(new S_SystemMessage("你不符合變身要求"));
                            return;
                        }
                    }
                    if (npcMakeItemAction.getMaxLevel() < pc.getLevel() || npcMakeItemAction.getMinLevel() > pc.getLevel()) {
                        // _log.info("玩家不符合等級要求：" + pc.getName());
                        pc.sendPackets(new S_SystemMessage("你的等級不符合要求"));
                        return;
                    }
                    if (npcMakeItemAction.getMaxLawful() < pc.getLawful() || npcMakeItemAction.getMinLawful() > pc.getLawful()) {
                        // _log.info("玩家不符合正義值要求：" + pc.getName());
                        pc.sendPackets(new S_SystemMessage("你的正義值不符合要求"));
                        return;
                    }
                    if (npcMakeItemAction.getMaxKarma() < pc.getKarma() || npcMakeItemAction.getMinKarma() > pc.getKarma()) {
                        // _log.info("玩家不符合友好度要求：" + pc.getName());
                        pc.sendPackets(new S_SystemMessage("你的友好度不符合要求"));
                        return;
                    }
                    /** 製作道具材料表 */
                    Map<Integer, L1CraftItem> materials = npcMakeItemAction.getMaterialList();
                    /** 製作道具增加成功率材料表 */
                    Map<Integer, L1CraftItem> aidMaterials = npcMakeItemAction.getAidMaterialList();
                    /** 加成道具使用數量 */
                    int sumAidCount = 0;
                    /** 刪除材料表 */
                    ArrayList<int[]> delItemList = new ArrayList<>();
                    L1PcInventory inventory = pc.getInventory();
                    if (inventory == null) {
                        _log.info("玩家背包有問題：" + pc.getName());
                        return;
                    }
                    List<ItemCraft_Read.ItemCraftRead.CraftItem> list = craftList.getItemlistList();
                    for (ItemCraft_Read.ItemCraftRead.CraftItem craftItem : list) {
                        for (L1CraftItem temp : materials.values()) {// 必備材料
                            L1Item itemInstance = ItemTable.get().getTemplate(temp.getItemId());
                            if (itemInstance.getItemDescId() == craftItem.getDescid()) {
                                delItemList.add(new int[]{itemInstance.getItemId(), (int) temp.getCount(), temp.getEnchantLevel()});
                                break;
                            }
                            if (temp.getSubstituteList() != null && temp.getSubstituteList().size() > 0) {// 必備材料-替換材料
                                for (L1CraftItem temp1 : temp.getSubstituteList()) {
                                    itemInstance = ItemTable.get().getTemplate(temp1.getItemId());
                                    if (itemInstance.getItemDescId() == craftItem.getDescid()) {
                                        delItemList.add(new int[]{itemInstance.getItemId(), (int) temp1.getCount(), temp1.getEnchantLevel()});
                                        break;
                                    }
                                }
                            }
                        }
                        if (craftItem.getItemcount() > 0) {
                            for (L1CraftItem temp : aidMaterials.values()) {// 加成材料
                                L1Item itemInstance = ItemTable.get().getTemplate(temp.getItemId());
                                if (itemInstance.getItemDescId() == craftItem.getDescid()) {
                                    delItemList.add(new int[]{itemInstance.getItemId(), craftItem.getItemcount(), temp.getEnchantLevel()});
                                    sumAidCount = craftItem.getItemcount();
                                    break;
                                }
                                if (temp.getSubstituteList() != null && temp.getSubstituteList().size() > 0) {// 加成材料-替換材料
                                    for (L1CraftItem temp1 : temp.getSubstituteList()) {
                                        itemInstance = ItemTable.get().getTemplate(temp1.getItemId());
                                        if (itemInstance.getItemDescId() == craftItem.getDescid()) {
                                            delItemList.add(new int[]{itemInstance.getItemId(), craftItem.getItemcount(), temp1.getEnchantLevel()});
                                            sumAidCount = craftItem.getItemcount();
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    for (int[] temp : delItemList) {// 檢查背包中是否有製作材料
                        L1ItemInstance x = inventory.findItemId(temp[0]);
                        if (x == null) {
                            // 錯誤的製作請求
                            pc.sendPackets(new S_ServerMessage(3562));
                            return;
                        }
                        if (x.isStackable()) {
                            if (x.getCount() < temp[1]) {
                                // 錯誤的製作請求
                                pc.sendPackets(new S_ServerMessage(3562));
                                return;
                            }
                        } else {
                            L1ItemInstance[] list1 = inventory.findItemsId(x.getItemId());
                            if (x == null || list1.length < temp[1]) {
                                // 錯誤的製作請求
                                pc.sendPackets(new S_ServerMessage(3562));
                                return;
                            }
                            if (temp[1] == 0) {// 沒有強化等級時跳過
                                continue;
                            }
                            int count1 = 0;
                            for (L1ItemInstance temp1 : list1) {
                                if (temp1.getEnchantLevel() == temp[2]) {
                                    count1++;
                                }
                            }
                            if (count1 < temp[1]) {// 符合強化條件的道具數量是否小於需要的數量
                                // 錯誤的製作請求
                                pc.sendPackets(new S_ServerMessage(3562));
                                return;
                            }
                        }
                    }
                    for (int[] temp : delItemList) {
                        L1ItemInstance item = inventory.findItemId(temp[0]);
                        if (item.isStackable()) {
                            pc.getInventory().removeItem(item, temp[1]);
                        } else {
                            L1ItemInstance[] list1 = inventory.findItemsId(item.getItemId());
                            int removed = 0;
                            for (L1ItemInstance temp1 : list1) {
                                if (removed >= temp[1]) {
                                    break;
                                }
                                if (temp1.getEnchantLevel() == temp[2]) {
                                    pc.getInventory().removeItem(temp1);
                                    removed++;
                                }
                            }
                        }
                    }
                    Random _random = new Random();
                    int random = _random.nextInt(1000000);
                    //String msg;
                    if (random <= npcMakeItemAction.getSucceedRandom() + 10000 * sumAidCount) {
                        Map<Integer, L1CraftItem> successItems = npcMakeItemAction.getItemList();
                        Map<Integer, L1CraftItem> bigsuccessItems = npcMakeItemAction.getBigSuccessItemList(); // 大成功道具
                        if (successItems.size() > 1) {// 多道具
                            int index = _random.nextInt(successItems.size());
                            L1ItemInstance item = null;
                            item = pc.getInventory().storeItem(successItems.get(index).getItemId(), (int) successItems.get(index).getCount(), successItems.get(index).getEnchantLevel());
                            pc.getInventory().saveItem(item, L1PcInventory.COL_BLESS);
                            //pc.sendPackets(new S_ServerMessage(403, item.getName()));
                            pc.sendPackets(new S_ServerMessage(3556)); // 3556道具製作成功。
                            pc.sendPackets(new S_SystemMessage("恭喜獲得 " + item.getName() + "(" + (int) successItems.get(index).getCount() + ")。"));
                            _log.info((new StringBuilder()).append("玩家 ").append(pc.getName()).append(" 成功製作 ").append(item.getName()).append(" 數量(").append((int) successItems.get(index).getCount()).append(")。").toString());
                            if (npcMakeItemAction.getShowWorld() != 0) {
                                // 使用自定義廣播文字，如果沒有設置則使用預設訊息
                                String broadcastMsg;
                                String itemDisplayName;
                                
                                // 如果物品有 name_id 就使用多語系格式，否則使用固定文字名稱
                                if (item.getItem().getNameId() != null && !item.getItem().getNameId().isEmpty()) {
                                    // 使用 name_id 多語系格式讓客戶端自動顯示
                                    itemDisplayName = item.getItem().getNameId() + "(" + (int) successItems.get(index).getCount() + ")";
                                } else {
                                    // 使用固定文字名稱
                                    itemDisplayName = item.getName() + "(" + (int) successItems.get(index).getCount() + ")";
                                }
                                
                                if (npcMakeItemAction.getShowWorldMsg() != null && !npcMakeItemAction.getShowWorldMsg().isEmpty()) {
                                    // 使用 %s 格式化：第一個 %s = 玩家名稱，第二個 %s = 製作道具名稱
                                    broadcastMsg = String.format(npcMakeItemAction.getShowWorldMsg(), pc.getName(), itemDisplayName);
                                } else {
                                    // 預設訊息
                                    broadcastMsg = "恭喜玩家 " + pc.getName() + " 成功製作 " + itemDisplayName + "。";
                                }
                                
                                // 根據 showWorldType 選擇廣播類型
                                int broadcastType = npcMakeItemAction.getShowWorldType();
                                if (broadcastType == 2) {
                                    // 中央廣播 (S_SystemMessage 色碼 26 = 螢幕頂端綠色文字)
                                    World.get().broadcastPacketToAll(new S_SystemMessage(broadcastMsg, 26));
                                } else {
                                    // 全服廣播 (S_AllChannelsChat 色碼 2 = 綠色)
                                    World.get().broadcastPacketToAll(new S_AllChannelsChat(broadcastMsg, 2));
                                }
                            }
                        } else {// 單一道具
                            L1ItemInstance item = null;
                            if (npcMakeItemAction.getBigSuccessItemList().size() > 0 && _random.nextInt(1000000) < npcMakeItemAction.getBigSuccessItemRandom()) { // 大成功
                                item = pc.getInventory().storeItem(bigsuccessItems.get(0).getItemId(), (int) bigsuccessItems.get(0).getCount(), bigsuccessItems.get(0).getEnchantLevel());
                                pc.getInventory().saveItem(item, L1PcInventory.COL_BLESS);
                                //pc.sendPackets(new S_ServerMessage(403, item.getName()));
                                pc.sendPackets(new S_ServerMessage(3556)); // 3556道具製作成功。
                                pc.sendPackets(new S_ServerMessage("\\aE恭喜獲得大成功道具 " + item.getName() + "(" + (int) bigsuccessItems.get(0).getCount() + ")。"));
                                pc.sendPackets(new S_SkillSound(pc.getId(), 2029)); // 煙花特效
                                _log.info((new StringBuilder()).append("玩家 ").append(pc.getName()).append(" 成功製作大成功道具 ").append(item.getName()).append(" 數量(").append((int) bigsuccessItems.get(0).getCount()).append(")。").toString());
                                if (npcMakeItemAction.getShowWorld() != 0) {
                                    // 使用自定義廣播文字，如果沒有設置則使用預設訊息
                                    String broadcastMsg;
                                    String itemDisplayName = item.getName() + "(" + (int) bigsuccessItems.get(0).getCount() + ")";

                                    if (npcMakeItemAction.getShowWorldMsg() != null && !npcMakeItemAction.getShowWorldMsg().isEmpty()) {
                                        // 使用 %s 格式化：第一個 %s = 玩家名稱，第二個 %s = 製作道具名稱
                                        broadcastMsg = String.format(npcMakeItemAction.getShowWorldMsg(), pc.getName(), itemDisplayName);
                                    } else {
                                        // 預設訊息 (大成功)
                                        broadcastMsg = "恭喜玩家 " + pc.getName() + " 成功製作大成功道具 " + itemDisplayName + "。";
                                    }
                                    
                                    // 根據 showWorldType 選擇廣播類型
                                    int broadcastType = npcMakeItemAction.getShowWorldType();
                                    if (broadcastType == 2) {
                                        // 中央廣播 (S_SystemMessage 色碼 26 = 螢幕頂端綠色文字)
                                        World.get().broadcastPacketToAll(new S_SystemMessage(broadcastMsg, 26));
                                    } else {
                                        // 全服廣播 (S_AllChannelsChat 色碼 2 = 綠色)
                                        World.get().broadcastPacketToAll(new S_AllChannelsChat(broadcastMsg, 2));
                                    }
                                }
                            } else {
                                item = pc.getInventory().storeItem(successItems.get(0).getItemId(), (int) successItems.get(0).getCount(), successItems.get(0).getEnchantLevel());
                                pc.getInventory().saveItem(item, L1PcInventory.COL_BLESS);
                                //pc.sendPackets(new S_ServerMessage(403, item.getName()));
                                pc.sendPackets(new S_ServerMessage(3556)); // 3556道具製作成功。
                                pc.sendPackets(new S_SystemMessage("恭喜獲得 " + item.getName() + "(" + (int) successItems.get(0).getCount() + ")。"));
                                _log.info((new StringBuilder()).append("玩家 ").append(pc.getName()).append(" 成功製作 ").append(item.getName()).append(" 數量(").append((int) successItems.get(0).getCount()).append(")。").toString());
                                if (npcMakeItemAction.getShowWorld() != 0) {
                                    // 使用自定義廣播文字，如果沒有設置則使用預設訊息
                                    String broadcastMsg;
                                    String itemDisplayName;
                                    
                                    // 如果物品有 name_id 就使用多語系格式，否則使用固定文字名稱
                                    if (item.getItem().getNameId() != null && !item.getItem().getNameId().isEmpty()) {
                                        // 使用 name_id 多語系格式讓客戶端自動顯示
                                        itemDisplayName = item.getItem().getNameId() + "(" + (int) successItems.get(0).getCount() + ")";
                                    } else {
                                        // 使用固定文字名稱
                                        itemDisplayName = item.getName() + "(" + (int) successItems.get(0).getCount() + ")";
                                    }
                                    
                                    if (npcMakeItemAction.getShowWorldMsg() != null && !npcMakeItemAction.getShowWorldMsg().isEmpty()) {
                                        // 使用 %s 格式化：第一個 %s = 玩家名稱，第二個 %s = 製作道具名稱
                                        broadcastMsg = String.format(npcMakeItemAction.getShowWorldMsg(), pc.getName(), itemDisplayName);
                                    } else {
                                        // 預設訊息
                                        broadcastMsg = "恭喜玩家 " + pc.getName() + " 成功製作 " + itemDisplayName + "。";
                                    }
                                    
                                    // 根據 showWorldType 選擇廣播類型
                                    int broadcastType = npcMakeItemAction.getShowWorldType();
                                    if (broadcastType == 2) {
                                        // 中央廣播 (S_SystemMessage 色碼 26 = 螢幕頂端綠色文字)
                                        World.get().broadcastPacketToAll(new S_SystemMessage(broadcastMsg, 26));
                                    } else {
                                        // 全服廣播 (S_AllChannelsChat 色碼 2 = 綠色)
                                        World.get().broadcastPacketToAll(new S_AllChannelsChat(broadcastMsg, 2));
                                    }
                                }
                            }
                        }
                    } else {// 失敗道具
                        //pc.sendPackets(new S_SystemMessage("製作失敗"));
                        pc.sendPackets(new S_ServerMessage(3557)); // 3557道具製作失敗。
                        Map<Integer, L1CraftItem> failItems = npcMakeItemAction.getFailItem();
                        if (failItems.size() > 1) {// 多道具
                            int index = _random.nextInt(failItems.size());
                            L1ItemInstance item = null;
                            item = pc.getInventory().storeItem(failItems.get(index).getItemId(), (int) failItems.get(index).getCount(), failItems.get(index).getEnchantLevel());
                            pc.getInventory().saveItem(item, L1PcInventory.COL_BLESS);
                            pc.sendPackets(new S_SystemMessage("獲得補償道具 " + item.getName() + "(" + (int) failItems.get(index).getCount() + ")。"));
                            _log.info((new StringBuilder()).append("玩家 ").append(pc.getName()).append(" 製作失敗，獲得 ").append(item.getName()).append(" 數量(").append((int) failItems.get(index).getCount()).append(")。").toString());
                        } else {// 單一道具
                            L1ItemInstance item = null;
                            if (failItems.size() > 0) {
                                item = pc.getInventory().storeItem(failItems.get(0).getItemId(), (int) failItems.get(0).getCount(), failItems.get(0).getEnchantLevel());
                                pc.getInventory().saveItem(item, L1PcInventory.COL_BLESS);
                                pc.sendPackets(new S_SystemMessage("獲得補償道具 " + item.getName() + "(" + (int) failItems.get(0).getCount() + ")。"));
                                _log.info((new StringBuilder()).append("玩家 ").append(pc.getName()).append(" 製作失敗，獲得 ").append(item.getName()).append(" 數量(").append((int) failItems.get(0).getCount()).append(")。").toString());
                            }
                        }
                    }
                    // 一次製作多個道具時需要重校招發送計時封包
                    if (craftList.getCrefCount() > 0) {
                        pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CRAFTTIME, 1));
                    }
                    break;
                case 92:// 製作道具延遲
                    //if (pc.getInventory().calcWeightpercent() >= 90) {
                    //pc.sendPackets(new S_SystemMessage("製作失敗: 角色負重在 90%以上."));
                    //return;
                    //}
                    if (pc.getInventory().getWeight240() >= 197) { // 重量過重
                        pc.sendPackets(new S_ServerMessage(82)); // 82無法領取，請確認負重與道具欄位。
                        return;
                    }
                    if (pc.getInventory().getSize() > 180) {
                        pc.sendPackets(new S_ServerMessage(263)); // 一個角色最多可攜帶180個道具。
                        return;
                    }
                    length = readH();
                    data = readByte(length);
                    Custom_Packet.readCustomPacket customPacket = Custom_Packet.readCustomPacket.parseFrom(data);
                    int objid = customPacket.getCustom();
                    int actionId = customPacket.getCustom1();
                    //int count = customPacket.getCustom2();
                    obj = World.get().findObject(objid);
                    npc = (L1NpcInstance) obj;
                    npcMakeItemActions = CraftConfigTable.get().readItemList(npc.getNpcId());
                    if (npcMakeItemActions == null) {
                        _log.info("無此兌換NPCID:" + npc.getNpcId() + "，玩家：" + pc.getName());
                        return;
                    }
                    npcMakeItemAction = (CraftItemForNpc) npcMakeItemActions.get(actionId);
                    if (npcMakeItemAction == null) {
                        _log.info("未知兌換物品ID：" + actionId);
                        return;
                    }
                    if (npcMakeItemAction.getCraftDelayTime() > 0) {
                        pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CRAFTTIME, false, 1));
                    }
                    break;
                //default:
                //System.out.println("未知newCraft OP:" + type);
                //break;
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            over();
        }
    }

    @Override
    public String getType() {
        return C_NEW_CREATEITEM;
    }

    /**
     * 清理過期的製作記錄
     */
    private static void cleanupExpiredEntries(long now) {
        // 檢查是否需要執行清理
        if (now - _lastCleanupTime < CLEANUP_INTERVAL_MS) {
            return;
        }

        try {
            int cleanedCount = 0;
            long expireTime = now - MAX_IDLE_TIME_MS;

            // 遍歷並移除過期條目
            java.util.Iterator<java.util.Map.Entry<Integer, Long>> iterator =
                _lastCraftTime.entrySet().iterator();

            while (iterator.hasNext()) {
                java.util.Map.Entry<Integer, Long> entry = iterator.next();
                if (entry.getValue() < expireTime) {
                    iterator.remove();
                    cleanedCount++;
                }
            }

            _lastCleanupTime = now;

            if (cleanedCount > 0 && _log.isDebugEnabled()) {
                _log.debug("[C_NewCreateItem] Cleaned up " + cleanedCount +
                          " expired craft entries. Current size: " + _lastCraftTime.size());
            }
        } catch (Exception e) {
            _log.error("[C_NewCreateItem] Error during cleanup", e);
        }
    }

    /**
     * 手動清理所有製作記錄（用於測試或維護）
     */
    public static void clearCraftCache() {
        _lastCraftTime.clear();
        _log.info("[C_NewCreateItem] All craft cache cleared");
    }

    /**
     * 獲取當前快取大小（用於監控）
     */
    public static int getCacheSize() {
        return _lastCraftTime.size();
    }
}
