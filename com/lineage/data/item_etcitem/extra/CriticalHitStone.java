package com.lineage.data.item_etcitem.extra;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.datatables.ExtraCriticalHitStoneTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.CharItemPowerReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1CriticalHitStone;
import com.lineage.server.templates.L1ItemPower_name;

import java.util.Random;

public class CriticalHitStone extends ItemExecutor {
    private static final Random _random = new Random();

    private CriticalHitStone() {
    }

    public static ItemExecutor get() {
        return new CriticalHitStone();
    }

    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        final int targObjId = data[0];
        final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
        if (tgItem == null) {
            return;
        }
        if (tgItem.isEquipped()) {
            pc.sendPackets(new S_SystemMessage("你必須先解除物品裝備!"));
            return;
        }
        if (tgItem.getItem().getType2() == 2) {
            pc.sendPackets(new S_SystemMessage("使用對像錯誤!"));
            return;
        }
        final L1CriticalHitStone magicStone = ExtraCriticalHitStoneTable.getInstance().get(item.getItemId());
        if (magicStone == null) {
            return;
        }
        if (item.getId() == tgItem.getId()) {
            if (magicStone.getNextItemId() <= 0) {
                pc.sendPackets(new S_SystemMessage("目前沒有更高的階級可以合成!"));
                return;
            }
            if (item.getCount() < 2) {
                pc.sendPackets(new S_SystemMessage("合成必須至少擁有兩顆!"));
                return;
            }
            final L1CriticalHitStone nextStone = ExtraCriticalHitStoneTable.getInstance().get(magicStone.getNextItemId());
            if (nextStone != null) {
                pc.getInventory().removeItem(item, 2);
                if (_random.nextInt(100) >= magicStone.getChance()) {
                    pc.sendPackets(new S_SystemMessage("合成失敗!"));
                    WriteLogTxt.Recording("暴擊寶石紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【" + pc.getName() + "】的【" + item.getRecordName(item.getCount()) + "】合成失敗.");
                    return;
                }
                pc.sendPackets(new S_SystemMessage("合成成功!"));
                CreateNewItem.createNewItem(pc, nextStone.getItemId(), 1);
                WriteLogTxt.Recording("暴擊寶石紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【" + pc.getName() + "】的【" + item.getRecordName(item.getCount()) + "】合成成功 (newItem:" + ItemTable.get().getTemplate(nextStone.getItemId()).getName() + ").");
            }
            return;
        }
        if (tgItem.getItem().getType2() != 1) {
            pc.sendPackets(new S_SystemMessage("只能鑲嵌在武器上面!"));
            return;
        }
        pc.getInventory().removeItem(item, 1);
        L1ItemPower_name power = tgItem.get_power_name();
        if (power == null) {
            power = new L1ItemPower_name();
            power.set_item_obj_id(tgItem.getId());
            tgItem.set_power_name(power);
            power.set_critical_hit_stone(magicStone);
            CharItemPowerReading.get().storeItem(tgItem.getId(), power);
            pc.sendPackets(new S_SystemMessage("寶石鑲嵌成功!"));
            WriteLogTxt.Recording("暴擊寶石紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【" + pc.getName() + "】的【" + item.getRecordName(item.getCount()) + "】成功鑲嵌到【" + tgItem.getRecordName(tgItem.getCount()) + ", (ObjectId: " + tgItem.getId() + ")】.");
        } else {
            power.set_critical_hit_stone(magicStone);
            CharItemPowerReading.get().updateItem(tgItem.getId(), power);
            pc.sendPackets(new S_SystemMessage("寶石鑲嵌(覆蓋)成功!"));
            WriteLogTxt.Recording("暴擊寶石紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【" + pc.getName() + "】的【" + item.getRecordName(item.getCount()) + "】成功鑲嵌(覆蓋)到【" + tgItem.getRecordName(tgItem.getCount()) + ", (ObjectId: " + tgItem.getId() + ")】.");
        }
        pc.sendPackets(new S_ItemStatus(tgItem));
    }
}
