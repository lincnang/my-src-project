package com.lineage.data.item_etcitem.card;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.lock.CharItemPowerReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemName;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1ItemPower_name;

/**
 * 56056 附魔去除劑
 */
public class Reset_Hole extends ItemExecutor {
    /**
     *
     */
    private Reset_Hole() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Reset_Hole();
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
        // 對像OBJID
        final int targObjId = data[0];
        // 目標物品
        final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
        if (tgItem == null) {
            return;
        }
        if (tgItem.isEquipped()) {
            pc.sendPackets(new S_ServerMessage("\\fR你必須先解除物品裝備!"));
            return;
        }
        L1ItemPower_name power = null;
        boolean update = false;
        switch (tgItem.getItem().getUseType()) {
            case 1:// 武器
            case 2:// 盔甲
            case 18:// T恤
            case 19:// 斗篷
            case 20:// 手套
            case 21:// 靴
            case 22:// 頭盔
            case 25:// 盾牌、臂甲
            case 43:// 輔助左
            case 44:// 輔助右
            case 45:// 輔助中
            case 70:// 脛甲
            case 48:// 六芒星護身符
            case 49:// 蒂蜜特祝福系列
            case 51:// 蒂蜜特的符文
                if (tgItem.get_powerdata() != null) {
                    power = tgItem.get_powerdata();
                    update = true;
                } else {
                    pc.sendPackets(new S_ServerMessage("此物品沒有附魔欄位。"));
                }
                break;
        }
        if (power != null) {
            power.set_item_obj_id(tgItem.getId());
            power.set_power_id(0);
            tgItem.set_powerdata(power);
            if (update) {
                CharItemPowerReading.get().updateItem(tgItem.getId(), tgItem.get_powerdata());
                pc.getInventory().removeItem(item, 1);// 移除道具
            }
            pc.sendPackets(new S_ItemStatus(tgItem));
            pc.sendPackets(new S_ItemName(tgItem));
        }
    }
}
