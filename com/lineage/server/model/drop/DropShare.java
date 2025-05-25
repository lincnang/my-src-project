package com.lineage.server.model.drop;

import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigDropBox;
import com.lineage.server.datatables.ItemMsgTable;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.ListMapUtil;
import com.lineage.server.utils.RandomArrayList;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 負責處理NPC掉落物品的分配邏輯。
 * 此類別實現了 DropShareExecutor 接口，並在NPC死亡後根據玩家的仇恨值分配掉落物品。
 *
 * @author dexc
 */
public class DropShare implements DropShareExecutor {
    private static final Log _log = LogFactory.getLog(DropShare.class);

    // 定義8個方向的X和Y坐標偏移量（順時針方向）
    private static final byte[] HEADING_TABLE_X = {0, 1, 1, 1, 0, -1, -1, -1};
    private static final byte[] HEADING_TABLE_Y = {-1, -1, 0, 1, 1, 1, 0, -1};

    /**
     * 掉落物品的分配入口方法。
     *
     * @param npc            死亡的NPC實例
     * @param acquisitorList 掉落目標的角色清單
     * @param hateList       對應的仇恨值清單
     */
    @Override
    public void dropShare(final L1NpcInstance npc, final ArrayList<L1Character> acquisitorList, final ArrayList<Integer> hateList) {
        // 創建一個新的 Runnable 任務來處理掉落物品的分配
        DropShareRunnable dropShareRunnable = new DropShareRunnable(npc, acquisitorList, hateList);
        // 將任務提交到通用線程池中立即執行
        GeneralThreadPool.get().schedule(dropShareRunnable, 0);
    }

    /**
     * 內部類別，實現 Runnable 接口，用於異步處理掉落物品的分配。
     */
    private class DropShareRunnable implements Runnable {
        private final L1NpcInstance npc;
        private final ArrayList<L1Character> acquisitorList;
        private final ArrayList<Integer> hateList;

        /**
         * 構造函數。
         *
         * @param npc            死亡的NPC實例
         * @param acquisitorList 掉落目標的角色清單
         * @param hateList       對應的仇恨值清單
         */
        public DropShareRunnable(L1NpcInstance npc, ArrayList<L1Character> acquisitorList, ArrayList<Integer> hateList) {
            this.npc = npc;
            this.acquisitorList = acquisitorList;
            this.hateList = hateList;
        }

        @Override
        public void run() {
            try {
                // 獲取NPC的物品庫存
                final L1Inventory inventory = npc.getInventory();
                if (inventory == null || inventory.getSize() <= 0) {
                    return; // 如果庫存為空，則不處理
                }

                // 確保掉落目標清單與仇恨值清單的大小相同
                if (acquisitorList.size() != hateList.size()) {
                    // _log.info("acquisitorList.size() != hateList.size()");
                    return;
                }

                // 計算總仇恨值，同時過濾掉不符合條件的掉落目標
                int totalHate = 0;
                for (int i = acquisitorList.size() - 1; i >= 0; i--) {
                    L1Character acquisitor = acquisitorList.get(i);
                    // 如果配置為自動分配2，則排除召喚獸和寵物
                    if (ConfigAlt.AUTO_LOOT == 2 && (acquisitor instanceof L1SummonInstance || acquisitor instanceof L1PetInstance)) {
                        acquisitorList.remove(i);
                        hateList.remove(i);
                    }
                    // 檢查角色是否在相同地圖且在可掉落範圍內
                    else if (acquisitor != null
                            && acquisitor.getMapId() == npc.getMapId()
                            && acquisitor.getLocation().getTileLineDistance(npc.getLocation()) <= ConfigAlt.LOOTING_RANGE) {
                        totalHate += hateList.get(i);
                    } else {
                        // 不符合條件的掉落目標從清單中移除
                        // _log.info("NPC掉落物品分配無對象 刪除掉落物: " + npc.getName());
                        acquisitorList.remove(i);
                        hateList.remove(i);
                    }
                }

                // 確保有有效的掉落目標
                if (acquisitorList.isEmpty()) {
                    return;
                }

                // 獲取掉落物品清單
                final List<L1ItemInstance> items = inventory.getItems();
                if (items.isEmpty()) {
                    return;
                }

                // 初始化隨機數生成器
                final Random random = new Random();
                L1Inventory targetInventory = null;
                L1PcInstance player;

                for (L1ItemInstance item : items) {
                    int itemId = item.getItemId();

                    // 如果物品是照明道具，則關閉其照明效果
                    if (item.getItem().getType2() == 0 && item.getItem().getType() == 2) {
                        item.setNowLighting(false);
                    }

                    // 處理特定NPC和物品ID的平均分配邏輯
                    if (isSpecialDrop(npc, itemId)) {
                        handleSpecialDrop(npc, acquisitorList, itemId);
                        continue;
                    }

                    long itemCount = item.getCount();
                    boolean isDropGround = npc.getNpcTemplate().getDropGround() != 0;

                    // 判斷是否進行按仇恨值分配
                    if ((ConfigAlt.AUTO_LOOT != 0 || itemId == L1ItemId.ADENA) && totalHate > 0 && !isDropGround) {
                        // 隨機選擇一個仇恨值範圍內的角色
                        int randomInt = random.nextInt(totalHate);
                        int chanceHate = 0;
                        for (int j = hateList.size() - 1; j >= 0; j--) {
                            TimeUnit.MILLISECONDS.sleep(1); // 防止同時生成相同的隨機數
                            chanceHate += hateList.get(j);
                            if (chanceHate > randomInt) {
                                L1Character acquisitor = acquisitorList.get(j);
                                // 檢查角色的背包是否有空間
                                if (acquisitor.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
                                    targetInventory = acquisitor.getInventory();
                                    // 如果角色是玩家，則處理消息和特殊物品效果
                                    if (acquisitor instanceof L1PcInstance) {
                                        player = (L1PcInstance) acquisitor;
                                        handlePlayerDrop(player, item, itemId, itemCount);
                                    }
                                } else {
                                    // 如果背包已滿，則將物品掉落在地面
                                    item.set_showId(npc.get_showId());
                                    targetInventory = World.get().getInventory(acquisitor.getX(), acquisitor.getY(), acquisitor.getMapId());
                                }
                                break;
                            }
                        }
                    } else {
                        // 按地面隨機分配物品
                        targetInventory = getGroundInventory(npc, isDropGround);
                    }

                    // 將物品從NPC的庫存轉移到目標庫存
                    inventory.tradeItem(item, item.getCount(), targetInventory);
                }

                // 清理物品列表
                ListMapUtil.clear(items);
            } catch (final Exception e) {
                // 處理異常情況（可選：記錄錯誤日誌）
                // _log.error(e.getLocalizedMessage(), e);
            } finally {
                // 最後，清空掉落目標和仇恨值清單
                ListMapUtil.clear(acquisitorList);
                ListMapUtil.clear(hateList);
            }
        }

        /**
         * 判斷是否為特殊掉落物品，需要平均分配。
         *
         * @param npc    NPC實例
         * @param itemId 物品ID
         * @return 如果是特殊掉落物品，返回 true
         */
        private boolean isSpecialDrop(L1NpcInstance npc, int itemId) {
            // 定義需要平均分配的NPC ID 和 物品 ID 對應
            switch (npc.getNpcId()) {
                case 97259: // 沙蟲
                    return itemId == 80026;
                case 97258: // 巨蟻女皇
                    return itemId == 80024;
                case 107034: // 傑羅斯
                    return itemId == 82239;
                case 107035: // 巨大飛龍
                    return itemId == 82236;
                case 99019: // 巨型骷髏
                    return itemId == 85010;
                case 71016: // 副本安塔瑞斯
                    return itemId == 80015;
                case 71028: // 副本法利昂
                    return itemId == 80016;
                case 99012: // 赤鬼
                    return itemId == 56313;
                default:
                    return false;
            }
        }

        /**
         * 處理特殊掉落物品的平均分配邏輯。
         *
         * @param npc           NPC實例
         * @param recipientList 受贈者清單
         * @param itemId        物品ID
         */
        private void handleSpecialDrop(L1NpcInstance npc, ArrayList<L1Character> recipientList, int itemId) {
            if (itemId == 56313) {
                // 特殊處理赤鬼掉落的物品，僅判斷是否在畫面中
                equalityDropScreen(npc, itemId);
            } else {
                // 一般特殊掉落物品，按畫面內且在仇恨清單中的角色平均分配
                equalityDrop(npc, recipientList, itemId);
            }
        }

        /**
         * 處理玩家獲得掉落物品的相關邏輯，如消息提示和特殊效果。
         *
         * @param player    玩家實例
         * @param item      掉落物品實例
         * @param itemId    物品ID
         * @param itemCount 物品數量
         */
        private void handlePlayerDrop(L1PcInstance player, L1ItemInstance item, int itemId, long itemCount) {
            // 處理特定物品（如40308）的特殊效果
            if (itemId == 40308) {
                double addadena = player.getGF();
                if (addadena > 0.0D) {
                    itemCount += itemCount * addadena;
                    item.setCount(itemCount);
                }
            }

            // 發送消息給玩家及其隊伍成員
            if (player.isInParty()) {
                // 獲取隊伍成員列表
                final Object[] partyMembers = player.getParty().getMemberList().toArray();
                if (partyMembers.length > 0) {
                    for (Object obj : partyMembers) {
                        if (obj instanceof L1PcInstance) {
                            final L1PcInstance tgpc = (L1PcInstance) obj;
                            // 813: 隊員%2%s 從%0 取得 %1%o
                            tgpc.sendPackets(new S_ServerMessage(813, npc.getNameId(), item.getLogName(), player.getName()));
                        }
                    }
                }
            } else {
                // 143: \f1%0%s 給你 %1%o 。
                player.sendPackets(new S_ServerMessage(143, npc.getNameId(), item.getLogName()));
            }

            // 根據配置，發送額外的消息
            if (ConfigDropBox.ISMSG) {
                if (ItemMsgTable.get().contains(item.getItemId()) && player.getWeapon() != null) {
                    if (!player.isGm() && player.hasSkillEffect(1691)) {
                        ConfigDropBox.msg(player.getName(), npc.getNameId(), item.getLogName());
                    }
                }
            }
        }

        /**
         * 獲取物品掉落到地面的庫存位置。
         *
         * @param npc          NPC實例
         * @param isDropGround 是否按地面隨機分配
         * @return 目標庫存位置
         * @throws InterruptedException 如果線程被中斷
         */
        private L1Inventory getGroundInventory(L1NpcInstance npc, boolean isDropGround) throws InterruptedException {
            List<Integer> dirList = new ArrayList<>();
            for (int j = 0; j < 8; j++) {
                dirList.add(j);
            }

            int x = 0;
            int y = 0;
            int dir = 0;
            Random random = new Random();

            while (true) {
                if (dirList.isEmpty()) {
                    // 如果所有方向都不可通行，則物品不掉落
                    x = 0;
                    y = 0;
                    break;
                }

                // 隨機選擇一個方向
                int randomIndex = random.nextInt(dirList.size());
                dir = dirList.remove(randomIndex);
                TimeUnit.MILLISECONDS.sleep(1); // 防止過快循環

                if (!isDropGround) {
                    // 根據方向表計算偏移量
                    x = HEADING_TABLE_X[dir];
                    y = HEADING_TABLE_Y[dir];
                } else {
                    // 按地面隨機範圍內的偏移量
                    int dropRange = npc.getNpcTemplate().getDropGround();
                    x = RandomArrayList.getInt(dropRange * 2) - dropRange;
                    y = RandomArrayList.getInt(dropRange * 2) - dropRange;
                }

                // 檢查目標位置是否可通行
                if (npc.getMap().isPassable(npc.getX(), npc.getY(), dir, null)) {
                    break;
                }
            }

            // 設置物品的顯示ID為NPC的顯示ID
            npc.getInventory().getItems().forEach(item -> item.set_showId(npc.get_showId()));

            // 獲取地面上的庫存位置
            return World.get().getInventory(npc.getX() + x, npc.getY() + y, npc.getMapId());
        }

        /**
         * 物品平均分配給符合條件的角色（同畫面且在仇恨清單中）。
         *
         * @param npc           NPC實例
         * @param recipientList 受贈者清單
         * @param itemId        物品ID
         */
        public void equalityDrop(L1NpcInstance npc, ArrayList<?> recipientList, int itemId) {
            // 獲取NPC的物品庫存
            L1Inventory inventory = npc.getInventory();
            if (inventory.getSize() == 0) {
                return;
            }

            // 過濾掉不符合條件的掉落目標
            L1Location npcLocation = npc.getLocation();
            for (int i = recipientList.size() - 1; i >= 0; i--) {
                L1Character recipient = (L1Character) recipientList.get(i);
                if (recipient instanceof L1SummonInstance || recipient instanceof L1PetInstance || recipient == null
                        || npc.getMapId() != recipient.getMapId()
                        || (!npcLocation.isInScreen(recipient.getLocation()) && npc.getNpcId() != 99019)) { // 巨型骷髏除外
                    recipientList.remove(i);
                }
            }

            // 分配掉落物品
            for (L1ItemInstance drop : inventory.getItems()) {
                if (drop.getItemId() != itemId) {
                    continue; // 只處理指定物品ID的掉落
                }

                for (int i = recipientList.size() - 1; i >= 0; i--) {
                    L1PcInstance pc = (L1PcInstance) recipientList.get(i);
                    if (pc.getInventory().checkAddItem(drop, drop.getCount()) == L1Inventory.OK) {
                        pc.getInventory().storeItem(drop.getItemId(), drop.getCount());

                        // 發送消息給玩家及其隊伍成員
                        if (pc.isInParty()) {
                            final Object[] pcs = pc.getParty().getMemberList().toArray();
                            if (pcs.length > 0) {
                                for (Object obj : pcs) {
                                    if (obj instanceof L1PcInstance) {
                                        final L1PcInstance tgpc = (L1PcInstance) obj;
                                        // 813: 隊員%2%s 從%0 取得 %1%o
                                        tgpc.sendPackets(new S_ServerMessage(813, npc.getNameId(), drop.getLogName(), pc.getName()));
                                    }
                                }
                            }
                        } else {
                            // 143: \f1%0%s 給你 %1%o 。
                            pc.sendPackets(new S_ServerMessage(143, npc.getNameId(), drop.getLogName()));
                        }
                    } else {
                        // 如果背包已滿，則將物品掉落在地面
                        drop.set_showId(npc.get_showId());
                        L1Inventory ground = World.get().getInventory(pc.getX(), pc.getY(), pc.getMapId());
                        ground.storeItem(drop.getItemId(), drop.getCount());
                    }
                }
            }
        }

        /**
         * 物品平均分配給在畫面中的玩家。
         *
         * @param npc    NPC實例
         * @param itemId 物品ID
         */
        public void equalityDropScreen(L1NpcInstance npc, int itemId) {
            // 獲取NPC的物品庫存
            L1Inventory inventory = npc.getInventory();
            if (inventory.getSize() == 0) {
                return;
            }

            // 獲取在NPC畫面內可見的玩家
            ArrayList<L1PcInstance> recipientList = World.get().getVisiblePlayer(npc);

            // 分配掉落物品
            for (L1ItemInstance drop : inventory.getItems()) {
                if (drop.getItemId() != itemId) {
                    continue; // 只處理指定物品ID的掉落
                }

                for (int i = recipientList.size() - 1; i >= 0; i--) {
                    L1PcInstance pc = recipientList.get(i);
                    if (pc.getInventory().checkAddItem(drop, drop.getCount()) == L1Inventory.OK) {
                        pc.getInventory().storeItem(drop.getItemId(), drop.getCount());

                        // 發送消息給玩家及其隊伍成員
                        if (pc.isInParty()) {
                            final Object[] pcs = pc.getParty().getMemberList().toArray();
                            if (pcs.length > 0) {
                                for (Object obj : pcs) {
                                    if (obj instanceof L1PcInstance) {
                                        final L1PcInstance tgpc = (L1PcInstance) obj;
                                        // 813: 隊員%2%s 從%0 取得 %1%o
                                        tgpc.sendPackets(new S_ServerMessage(813, npc.getNameId(), drop.getLogName(), pc.getName()));
                                    }
                                }
                            }
                        } else {
                            // 143: \f1%0%s 給你 %1%o 。
                            pc.sendPackets(new S_ServerMessage(143, npc.getNameId(), drop.getLogName()));
                        }
                    } else {
                        // 如果背包已滿，則將物品掉落在地面
                        drop.set_showId(npc.get_showId());
                        L1Inventory ground = World.get().getInventory(pc.getX(), pc.getY(), pc.getMapId());
                        ground.storeItem(drop.getItemId(), drop.getCount());
                    }
                }
            }
        }
    }
}
