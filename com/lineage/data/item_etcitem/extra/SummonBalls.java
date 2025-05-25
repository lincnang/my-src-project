package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Npc;

public class SummonBalls extends ItemExecutor {
    private int _mobId;
    private int _mobCount;
    private int _time;
    private int _petCost;
    private boolean _isRemovable;

    private SummonBalls() {
    }

    public static ItemExecutor get() {
        return new SummonBalls();
    }

    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        if (!pc.getMap().isRecallPets()) {
            pc.sendPackets(new S_ServerMessage(353));
            return;
        }
        if (!pc.getInventory().consumeItem(40308, 50000)) {
            pc.sendPackets(new S_ServerMessage("金幣不足。"));
            return;
        } else {
            pc.sendPackets(new S_ServerMessage("召喚小幫手成功，扣除50000金幣。"));
        }
        int petcost = 0;
        int mob_count = 0;
        for (final L1NpcInstance pet : pc.getPetList().values()) {
            petcost += pet.getPetcost();
            if (pet.getNpcId() == _mobId) {
                mob_count++;
            }
        }
        if (mob_count >= _mobCount) {
            pc.sendPackets(new S_SystemMessage("你已經召喚了足夠數量。"));
            return;
        }
        final int charisma = pc.getCha() + 6 - petcost;
        final int summoncount = Math.min(charisma / _petCost - mob_count, _mobCount);
        if (mob_count >= _mobCount) {
            pc.sendPackets(new S_SystemMessage("魅力不足無法召喚更多怪物。"));
            return;
        }
        if (_isRemovable) {
            if (item.getChargeCount() > 1) {
                item.setChargeCount(item.getChargeCount() - 1);
                pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
            } else {
                pc.getInventory().removeItem(item, 1);
            }
        }
        final L1Npc npcTemp = NpcTable.get().getTemplate(_mobId);
        for (int i = 0; i < summoncount; i++) {
            final L1SummonInstance summon = new L1SummonInstance(npcTemp, pc, _time);
            summon.setPetcost(_petCost);
        }
    }

    @Override
    public void set_set(String[] set) {
        try {
            _mobId = Integer.parseInt(set[1]);
            _mobCount = Integer.parseInt(set[2]);
            _time = Integer.parseInt(set[3]);
            _petCost = Integer.parseInt(set[4]);
            _isRemovable = Boolean.parseBoolean(set[5]);
        } catch (Exception e) {
        }
    }
}
