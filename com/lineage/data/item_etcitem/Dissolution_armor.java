package com.lineage.data.item_etcitem;

// 引入所需的類別和套件

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.datatables.A_ResolventTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import william.login_Artiface1;

/**
 * 溶解劑41245
 * 此類別處理防具的分解（煉化）功能，使用特定的溶解劑來提升防具的煉化等級或值。
 */
public class Dissolution_armor extends ItemExecutor {

    /**
     * 私有建構子，防止外部直接實例化此類別。
     */
    private Dissolution_armor() {
        // 空建構子
    }

    /**
     * 獲取此執行者的實例。
     *
     * @return 新的 Dissolution_armor 實例
     */
    public static ItemExecutor get() {
        return new Dissolution_armor();
    }

    /**
     * 道具物件執行
     *
     * @param data 參數，通常包含道具的物件ID
     * @param pc   執行者，玩家實例
     * @param item 物件，溶解劑本身的實例
     */
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        final int itemobj = data[0]; // 獲取道具的物件ID
        final L1ItemInstance item1 = pc.getInventory().getItem(itemobj); // 根據物件ID從玩家的背包中獲取目標道具
        if (item1 == null) {
            return; // 如果目標道具不存在，則終止執行
        }
        this.useResolvent(pc, item1, item); // 使用溶解劑分解目標道具
    }

    /**
     * 使用溶解劑分解目標道具，提升防具的煉化等級或值。
     *
     * @param pc        執行者，玩家實例
     * @param item      目標道具，將被分解的防具
     * @param resolvent 溶解劑道具本身的實例
     */
    private void useResolvent(final L1PcInstance pc, final L1ItemInstance item, final L1ItemInstance resolvent) {
        if ((item == null) || (resolvent == null)) {
            pc.sendPackets(new S_ServerMessage(79)); // 發送消息「沒有任何事情發生。」
            return;
        }
        // 檢查目標道具的類型是否為防具（Type2 = 2）或道具（Type2 = 0）
        if ((item.getItem().getType2() == 2) || (item.getItem().getType2() == 0)) {
            // 如果道具已裝備，提示玩家先卸下裝備再分解
            if (item.isEquipped()) {
                pc.sendPackets(new S_SystemMessage("請先把裝備脫掉在分解。"));
                return;
            }
            // 從溶解劑表中獲取目標道具對應的分解值（晶體數量）
            int crystalCount = A_ResolventTable.get().getCrystalCount(item.getItem().getItemId());
            if (crystalCount == 0) {
                pc.sendPackets(new S_ServerMessage(1161)); // 發送消息「無法溶解。」
                return;
            }
            if (crystalCount != 0) {
                // 增加玩家的防具煉化值
                pc.get_other().setArtifact1(pc.get_other().getArtifact1() + crystalCount);
                // 呼叫 login_Artiface1 的 forIntensifyArmor 方法，提升防具煉化等級
                login_Artiface1.forIntensifyArmor(pc, 2);
                // 向玩家發送當前防具煉化階級和煉化值的訊息
                pc.sendPackets(new S_SystemMessage("目前防具煉化階級 : " + pc.get_other().getLv_Redmg_Artifact()));
                pc.sendPackets(new S_SystemMessage("目前防具煉化值 : " + pc.get_other().getArtifact1()));

                // 記錄煉化防具的操作日誌
                WriteLogTxt.Recording("煉化防具紀錄", "玩家" + ":【 " + pc.getName() + " 】 "
                        + "煉化分解" + "【 + " + item.getEnchantLevel() + " "
                        + item.getName() + "】- 階級:[" + pc.get_other().getLv_Redmg_Artifact()
                        + "】- 分解值:[" + pc.get_other().getArtifact1() + "].");
            }
            // 從玩家背包中移除目標道具和溶解劑
            pc.getInventory().removeItem(item, 1);
            pc.getInventory().removeItem(resolvent, 1);
        }
    }
}
