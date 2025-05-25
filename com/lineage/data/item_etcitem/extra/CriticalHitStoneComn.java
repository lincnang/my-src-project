package com.lineage.data.item_etcitem.extra;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.datatables.ExtraCriticalHitStoneTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1CriticalHitStone;

import java.util.Random;

public class CriticalHitStoneComn extends ItemExecutor {
    private static final Random _random = new Random();
    private boolean _dontRemove;

    private CriticalHitStoneComn() {
    }

    public static ItemExecutor get() {
        return new CriticalHitStoneComn();
    }

    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        final int targObjId = data[0];
        final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
        if (tgItem == null) {
            return;
        }
        if (tgItem.getItem().getType2() != 0) {
            pc.sendPackets(new S_SystemMessage("使用對像錯誤!"));
            return;
        }
        final L1CriticalHitStone magicStone = ExtraCriticalHitStoneTable.getInstance().get(tgItem.getItemId());
        if (magicStone == null) {
            pc.sendPackets(new S_SystemMessage("使用對像錯誤!"));
            return;
        }
        if (magicStone.getNextItemId() <= 0) {
            pc.sendPackets(new S_SystemMessage("目前沒有更高的階級可以合成!"));
            return;
        }
        final L1CriticalHitStone nextStone = ExtraCriticalHitStoneTable.getInstance().get(magicStone.getNextItemId());
        if (nextStone == null) {
            return;
        }
        pc.getInventory().removeItem(item, 1);
        if (_random.nextInt(100) >= magicStone.getChance()) {
            if (!_dontRemove) {
                pc.getInventory().removeItem(tgItem, 1);
                pc.sendPackets(new S_SystemMessage("寶石合成失敗! (寶石已消失)"));
            } else {
                pc.sendPackets(new S_SystemMessage("寶石合成失敗! (寶石已保留)"));
            }
            WriteLogTxt.Recording("暴擊寶石(通用)紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【" + pc.getName() + "】的【" + item.getRecordName(item.getCount()) + "】合成失敗 (是否不刪除寶石:" + _dontRemove + ").");
            return;
        }
        pc.getInventory().removeItem(tgItem, 1);
        pc.sendPackets(new S_SystemMessage("寶石合成成功!"));
        CreateNewItem.createNewItem(pc, nextStone.getItemId(), 1);
        WriteLogTxt.Recording("暴擊寶石(通用)紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【" + pc.getName() + "】的【" + item.getRecordName(item.getCount()) + "】合成成功 (newItem:" + ItemTable.get().getTemplate(nextStone.getItemId()).getName() + ").");
    }

    @Override
    public void set_set(String[] set) {
        try {
            _dontRemove = Boolean.parseBoolean(set[1]);
        } catch (Exception e) {
        }
    }
}
