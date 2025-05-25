package com.lineage.data.npc.shop;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.ItemUpdateTable;
import com.lineage.server.datatables.lock.CharItemsReading;
import com.lineage.server.datatables.lock.CharShiftingReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ItemUpdate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

public class NPC_ItemUpdate extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(NPC_ItemUpdate.class);

    public static NpcExecutor get() {
        return new NPC_ItemUpdate();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        pc.set_mode_id(0);
        pc.set_check_item(false);
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_up_i0"));
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        if (cmd.matches("[0-9]+")) {
            if ((pc.get_mode_id() != 0) && (!pc.get_check_item())) {
                check_item(pc, npc, cmd);
            }
        } else if (cmd.equalsIgnoreCase("up")) {
            pc.set_mode_id(0);
            show_item(pc, npc);
        } else if ((cmd.startsWith("ut")) && (pc.get_check_item())) {
            get_item(pc, npc, cmd);
        }
    }

    private void get_item(L1PcInstance pc, L1NpcInstance npc, String cmd) {
        try {
            int index = Integer.parseInt(cmd.substring(2));
            L1ItemInstance tgitem = pc.getInventory().getItem(pc.get_mode_id());
            if (tgitem == null) {
                _log.error("升級道具 對像 OBJID異常 背包中無該道具:" + pc.get_mode_id());
                pc.sendPackets(new S_CloseList(pc.getId()));
                return;
            }
            ArrayList<L1ItemUpdate> list = ItemUpdateTable.get().get(tgitem.getItemId());
            L1ItemUpdate newitem = (L1ItemUpdate) list.get(index);
            if (newitem == null) {
                _log.error("升級道具 對像 ITEMID 找不到升級模組:" + tgitem.getItemId());
                pc.sendPackets(new S_CloseList(pc.getId()));
                return;
            }
            int toid = newitem.get_toid();
            int[] needids = newitem.get_needids();
            int[] needcounts = newitem.get_needcounts();
            if (CreateNewItem.checkNewItem(pc, needids, needcounts) >= 1L) {
                if (CreateNewItem.delItems(pc, needids, needcounts, 1L)) {
                    L1Item l1item = ItemTable.get().getTemplate(toid);
                    String src_name = tgitem.getItem().getName();
                    pc.sendPackets(new S_DeleteInventoryItem(tgitem.getId()));
                    tgitem.setItemId(toid);
                    tgitem.setItem(l1item);
                    tgitem.setBless(l1item.getBless());
                    CharItemsReading.get().updateItemId_Name(tgitem);
                    pc.sendPackets(new S_AddItem(tgitem));
                    pc.sendPackets(new S_ServerMessage(403, tgitem.getLogName()));
                    CharShiftingReading.get().newShifting(pc, 0, src_name, tgitem.getId(), tgitem.getItem(), tgitem, 0);
                }
            } else {
                pc.sendPackets(new S_CloseList(pc.getId()));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void check_item(L1PcInstance pc, L1NpcInstance npc, String cmd) {
        try {
            int index = Integer.parseInt(cmd);
            L1ItemInstance tgitem = pc.getInventory().getItem(pc.get_mode_id());
            if (tgitem == null) {
                _log.error("升級道具 對像 OBJID異常 背包中無該道具:" + pc.get_mode_id());
                pc.sendPackets(new S_CloseList(pc.getId()));
                return;
            }
            ArrayList<L1ItemUpdate> list = ItemUpdateTable.get().get(tgitem.getItemId());
            L1ItemUpdate newitem = (L1ItemUpdate) list.get(index);
            if (newitem == null) {
                _log.error("升級道具 對像 ITEMID 找不到升級模組:" + tgitem.getItemId());
                pc.sendPackets(new S_CloseList(pc.getId()));
                return;
            }
            int toids = newitem.get_toid();
            int[] needids = newitem.get_needids();
            int[] needcounts = newitem.get_needcounts();
            L1Item getitem = ItemTable.get().getTemplate(toids);
            String need = "";
            for (int i = 0; i < needids.length; i++) {
                L1Item l1item = ItemTable.get().getTemplate(needids[i]);
                need = need + l1item.getName() + "(" + needcounts[i] + ")  ";
            }
            String[] date = {tgitem.getItem().getName(), getitem.getName(), need};
            pc.set_check_item(true);
            pc.sendPackets(new S_ItemCount(npc.getId(), 1, 1, "y_up_i2", "ut" + index, date));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void show_item(L1PcInstance pc, L1NpcInstance npc) {
        try {
            ArrayList<L1ItemInstance> list = new ArrayList<L1ItemInstance>();
            List<L1ItemInstance> items = pc.getInventory().getItems();
            for (L1ItemInstance item : items) {
                if (!item.getItem().isStackable()) {
                    if (!item.isEquipped()) {
                        if (ItemUpdateTable.get().get(item.getItemId()) != null) {
                            list.add(item);
                        }
                    }
                }
            }
            if (list.size() <= 0) {
                pc.sendPackets(new S_ServerMessage("\\fR找不到可以升級的裝備。"));
                pc.sendPackets(new S_CloseList(pc.getId()));
                return;
            }
            pc.sendPackets(new S_PowerItemList(pc, npc.getId(), list));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.npc.shop.NPC_ItemUpdate JD-Core Version: 0.6.2
 */