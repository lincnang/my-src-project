package com.lineage.data.npc.shop;

import com.lineage.config.ConfigRecord;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.LogEnchantReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Item;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import william.GiveBack;

import java.util.ArrayList;
import java.util.List;

public class Npc_GiveBack extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_GiveBack.class);

    public static NpcExecutor get() {
        return new Npc_GiveBack();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < GiveBack.savepcid.size(); i++) {
                if (GiveBack.savepcid.get(i) == pc.getId()) {
                    indices.add(i);
                }
            }

            // 如果超過 3 筆，移除最舊的多餘資料
            while (indices.size() > 3) {
                int removeIndex = indices.get(0);
                GiveBack.savepcid.remove(removeIndex);
                GiveBack.saveweapon.remove(removeIndex);
                GiveBack.savename.remove(removeIndex);

                // 移除後所有索引值往前移，所以重新建立 index 清單
                indices.clear();
                for (int i = 0; i < GiveBack.savepcid.size(); i++) {
                    if (GiveBack.savepcid.get(i) == pc.getId()) {
                        indices.add(i);
                    }
                }
            }

            // 準備送出前三筆裝備資訊
            List<String> items = new ArrayList<>();
            for (int index : indices) {
                items.add(GiveBack.savename.get(index));
            }

            String[] msg = new String[3];
            msg[0] = items.size() > 0 ? items.get(0) : "";
            msg[1] = items.size() > 1 ? items.get(1) : "";
            msg[2] = items.size() > 2 ? items.get(2) : "";

            if (items.size() > 0) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "giveback", msg));
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "nogiveback", msg));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        try {
            int selectedIndex = -1;
            try {
                selectedIndex = Integer.parseInt(cmd);
            } catch (NumberFormatException ignore) {}

            if (selectedIndex >= 0) {
                int count = 0;
                for (int i = 0; i < GiveBack.savepcid.size(); i++) {
                    if (GiveBack.savepcid.get(i) == pc.getId()) {
                        if (count == selectedIndex) {
                            int itemid = GiveBack.npc_itemid.getOrDefault(npc.getNpcId(), 40308);
                            int itemcount = GiveBack.npc_itemcount.getOrDefault(npc.getNpcId(), 30);
                            if (pc.getInventory().checkItem(itemid, itemcount)) {
                                pc.getInventory().consumeItem(itemid, itemcount);
                                L1ItemInstance olditem = GiveBack.saveweapon.get(i);

                                GiveBack.savepcid.remove(i);
                                GiveBack.saveweapon.remove(i);
                                GiveBack.savename.remove(i);

                                if (ConfigRecord.LOGGING_BAN_ENCHANT) {
                                    LogEnchantReading.get().resetEnchant(pc, olditem);
                                }
                                pc.getInventory().storeItem(olditem, 1);
                                pc.sendPackets(new S_ServerMessage("\fV 您的 \fV " + olditem.getNumberedViewName(1L) + "\fV  已贖回。"));
                                pc.set_backpage(1);
                                pc.sendPackets(new S_CloseList(pc.getId()));
                            } else {
                                L1Item temper = ItemTable.get().getTemplate(itemid);
                                pc.sendPackets(new S_ServerMessage(temper.getName() + " 不足。"));
                                pc.set_backpage(1);
                                pc.sendPackets(new S_CloseList(pc.getId()));
                            }
                            return;
                        }
                        count++;
                    }
                }
            }
        } catch (Exception e) {
            _log.error("執行贖回時發生錯誤: " + e.getMessage(), e);
        }
    }
}