package com.add;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.serverpackets.*;
import com.lineage.server.types.Point;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class PeepCard {
    private static Random _random = new Random();

    public static void TeleportPc(L1PcInstance pc, L1Object target) {
        // 檢查目標是否為玩家實例
        if (target instanceof L1PcInstance) {
            L1PcInstance target_pc = (L1PcInstance) target; // 將目標轉型為玩家實例
            // 若玩家 pc 與目標玩家之間的距離大於 4 格
            if (pc.getLocation().getTileLineDistance((Point) target_pc.getLocation()) > 4) {
                // 在 pc 附近隨機產生一個新位置，距離 1 格
                L1Location newLoc = pc.getLocation().randomLocation(1, 1, false);
                // 確認 pc 是否能看到該新位置（無障礙等）
                if (pc.glanceCheck(newLoc.getX(), newLoc.getY())) {
                    // 傳送目標玩家到新位置，使用 pc 的地圖 ID、朝向 5 並顯示特效
                    L1Teleport.teleport(target_pc, newLoc.getX(), newLoc.getY(), pc.getMapId(), 5, true);
                    // 傳送技能音效封包給 pc 自己
                    pc.sendPackets((ServerBasePacket) new S_SkillSound(pc.getId(), 3932));
                    // 向周圍其他玩家廣播技能音效封包
                    pc.broadcastPacketAll((ServerBasePacket) new S_SkillSound(pc.getId(), 3932));
                }
            } else {
                // 若目標玩家與 pc 距離不夠遠，顯示無法傳送的系統訊息
                pc.sendPackets((ServerBasePacket) new S_SystemMessage("對方距離不夠遠，無法把他抓過來。"));
            }
            // 如果目標是怪物實例
        } else if (target instanceof L1MonsterInstance) {
            L1MonsterInstance target_npc = (L1MonsterInstance) target; // 將目標轉型為怪物實例
            // 若玩家 pc 與目標怪物之間的距離大於 4 格
            if (pc.getLocation().getTileLineDistance((Point) target_npc.getLocation()) > 4) {
                // 在 pc 附近隨機產生一個新位置，距離 1 格
                L1Location newLoc = pc.getLocation().randomLocation(1, 1, false);
                // 確認 pc 是否能看到該新位置（無障礙等）
                if (pc.glanceCheck(newLoc.getX(), newLoc.getY())) {
                    // 在目標怪物位置廣播技能音效封包（顯示特效）
                    target_npc.broadcastPacketAll((ServerBasePacket) new S_SkillSound(target_npc.getId(), 3932));
                    // 將怪物的位置更新為新位置
                    target_npc.setX(newLoc.getX());
                    target_npc.setY(newLoc.getY());
                    // 重設怪物的方向（實際上這裡沒改變方向，只是重設為原本的朝向）
                    target_npc.setHeading(target_npc.getHeading());
                }
            } else {
                // 若目標怪物與 pc 距離不夠遠，顯示無法傳送的系統訊息
                pc.sendPackets((ServerBasePacket) new S_SystemMessage("怪物距離不夠遠，無法把怪物抓過來。"));
            }
        }
    }

    public static void BackMagic(L1PcInstance pc, L1Object target) {
        // 計算玩家pc與目標target之間的 x 和 y 軸距離差異
        int _x = pc.getX() - target.getX();
        int _y = pc.getY() - target.getY();

        // 若玩家與目標之間的距離在 x 軸和 y 軸方向均小於5格
        if (Math.abs(_x) < 5 && Math.abs(_y) < 5) {
            int a; // 用於儲存目標新 X 座標
            int f; // 用於儲存目標新 Y 座標

            // 根據 x 軸方向差異決定新 X 座標
            if (_x > 0) {
                a = target.getX() - 1;  // 若 pc 在目標右邊，將目標往右移動1格
            } else if (_x < 0) {
                a = target.getX() + 1;  // 若 pc 在目標左邊，將目標往左移動1格
            } else {
                a = target.getX();      // 若在同一垂直線上，不改變 X 座標
            }

            // 根據 y 軸方向差異決定新 Y 座標
            if (_y > 0) {
                f = target.getY() - 1;  // 若 pc 在目標下方，將目標往下移動1格
            } else if (_y < 0) {
                f = target.getY() + 1;  // 若 pc 在目標上方，將目標往上移動1格
            } else {
                f = target.getY();      // 若在同一水平線上，不改變 Y 座標
            }

            // 確保目標不為 null
            if (target != null) {
                // 取得目標所在的地圖
                L1Map map = target.getMap();
                // 檢查新座標 (a, f) 是否是可通行區域
                if (map.isPassable(a, f, null)) {
                    // 若可通行，則更新目標的位置到新座標
                    target.setX(a);
                    target.setY(f);

                    // 如果目標是玩家實例，則執行額外的更新操作
                    if (target instanceof L1PcInstance) {
                        L1PcInstance targetPc = (L1PcInstance) target;
                        // 傳送地圖更新訊息給目標玩家，包括地圖ID和是否在水下等資訊
                        targetPc.sendPackets((ServerBasePacket) new S_MapID(targetPc.getMapId(), targetPc.getMap().isUnderwater()));
                        // 向周圍的玩家廣播目標玩家的資訊更新
                        targetPc.broadcastPacketAll((ServerBasePacket) new S_OtherCharPacks(targetPc));
                        // 更新目標玩家自己的資訊封包
                        targetPc.sendPackets((ServerBasePacket) new S_OwnCharPack(targetPc));
                        // 更新目標玩家的視覺狀態，例如外觀變化
                        targetPc.sendPackets((ServerBasePacket) new S_CharVisualUpdate(targetPc));
                        // 清除目標玩家已知的物件列表，以強制刷新周圍環境
                        targetPc.removeAllKnownObjects();
                        // 更新目標玩家對象列表，重新獲取周圍的玩家及物件
                        targetPc.updateObject();
                    }
                }
                // 無論是否更新了位置，都向周圍玩家廣播技能音效，用於視覺或音效效果
                pc.sendPacketsAll((ServerBasePacket) new S_SkillSound(target.getId(), 2181));
            }
        }
    }


    public static void TakePc(L1PcInstance pc, L1Object target) {
        // 檢查目標是否為玩家實例
        if (target instanceof L1PcInstance) {
            L1PcInstance target_pc = (L1PcInstance) target; // 將目標轉型為玩家實例

            // 生成一個 1 到 100 的隨機數，模擬偷竊成功的機率變化
            int rnd = ThreadLocalRandom.current().nextInt(100) + 1;
            // 這段檢查沒有實際意義，因為 rnd 生成的範圍已經保證非負
            if (rnd < 0) {
                rnd = 0;
            }
            // 嘗試從目標玩家的背包中取得一個受罰物品（假設為違法物品），具體方法名稱 caoPenalty()
            L1ItemInstance item = target_pc.getInventory().caoPenalty();

            // 如果目標玩家有可被偷的物品，且另一個隨機數小於 rnd，則進行偷竊
            if (item != null && ThreadLocalRandom.current().nextInt(100) + 1 < rnd) {
                // 將物品從目標玩家的背包交易至執行此方法的玩家(pc)的背包中
                // 如果物品可疊加，則偷取全部數量；否則只偷取一個
                target_pc.getInventory().tradeItem(item, item.isStackable() ? item.getCount() : 1L, (L1Inventory) pc.getInventory());

                // 傳送系統訊息給目標玩家，告知該物品已被偷走（使用對應的訊息編號638）
                target_pc.sendPackets((ServerBasePacket) new S_ServerMessage(638, item.getLogName()));

                // 傳送系統訊息給執行偷竊的玩家(pc)，告知偷竊成功及偷到的物品名稱
                pc.sendPackets((ServerBasePacket) new S_SystemMessage("成功偷到( " + item.getLogName() + " )。"));
            }
        }
    }
}
