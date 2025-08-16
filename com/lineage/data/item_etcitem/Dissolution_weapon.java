package com.lineage.data.item_etcitem;

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
 */
public class Dissolution_weapon extends ItemExecutor {
    /**
     *
     */
    private Dissolution_weapon() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Dissolution_weapon();
    }

    /**
     * 道具物件執行
     *
     * @param data 參數
     * @param pc   執行者
     * @param item 物件
     */
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        final int itemobj = data[0];
        final L1ItemInstance item1 = pc.getInventory().getItem(itemobj);
        if (item1 == null) {
            return;
        }
        this.useResolvent(pc, item1, item);
    }

    private void useResolvent(final L1PcInstance pc, final L1ItemInstance item, final L1ItemInstance resolvent) {
        if ((item == null) || (resolvent == null)) {
            pc.sendPackets(new S_ServerMessage(79)); // 沒有任何事情發生。
            return;
        }
        if ((item.getItem().getType2() == 1)) { //防具
            if (item.isEquipped()) {
                pc.sendPackets(new S_SystemMessage("請先把裝備脫掉在分解。"));
                return;
            }
            int crystalCount = A_ResolventTable.get().getCrystalCount(item.getItem().getItemId());
            if (crystalCount == 0) {
                pc.sendPackets(new S_ServerMessage(1161)); // 無法溶解。
                return;
            }
            if (crystalCount != 0) {
                pc.get_other().setArtifact(pc.get_other().getArtifact() + crystalCount);//+溶解值
                login_Artiface1.forIntensifyArmor(pc, 1);
                pc.sendPackets(new S_SystemMessage("目前武器煉化階級 : " + pc.get_other().getLv_Artifact()));
                pc.sendPackets(new S_SystemMessage("目前武器煉化值 : " + pc.get_other().getArtifact()));

                WriteLogTxt.Recording("煉化武器紀錄", "玩家" + ":【 " + pc.getName() + " 】 " + "煉化分解" + "【 + " + item.getEnchantLevel() + " " + item.getName() + "】- 階級:[" + pc.get_other().getLv_Artifact() + "】- 分解值:[" + pc.get_other().getArtifact() + "].");
            }
            pc.getInventory().removeItem(item, 1);
            pc.getInventory().removeItem(resolvent, 1);
        }
    }

}
