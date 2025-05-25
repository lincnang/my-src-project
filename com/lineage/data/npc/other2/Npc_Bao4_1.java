package com.lineage.data.npc.other2;

import com.lineage.config.ConfigQuest;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcQuest;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Item;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 寶4區任務  - 任務回報領取
 */
public class Npc_Bao4_1 extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Bao4_1.class);
    private int _getitem;

    private Npc_Bao4_1() {
    }

    public static NpcExecutor get() {
        return new Npc_Bao4_1();
    }

    @Override
    public int type() {
        return 3;
    }

    @Override
    public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
        try {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bao_map4_1"));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
        try {
            if (cmd.equalsIgnoreCase("bao_gift4")) {
                if (pc.getQuest().isEnd(L1PcQuest.BAO_QUEST_4)) {
                    pc.sendPackets(new S_SystemMessage("您今日已經解過該任務。"));
                    return;
                }
                if (!pc.getQuest().isStart(L1PcQuest.BAO_QUEST_4)) {
                    pc.sendPackets(new S_SystemMessage("您並沒有接任務。"));
                    return;
                }
                if (ConfigQuest.BAO4_QUEST_ITEM != null) {
                    boolean error = false;
                    final int size = ConfigQuest.BAO4_QUEST_ITEM.length;
                    for (int i = 0; i < size; i++) {
                        if (!pc.getInventory().checkItem(ConfigQuest.BAO4_QUEST_ITEM[i][0], ConfigQuest.BAO4_QUEST_ITEM[i][1])) {
                            final L1Item temp = ItemTable.get().getTemplate(ConfigQuest.BAO4_QUEST_ITEM[i][0]);
                            pc.sendPackets(new S_ServerMessage(337, temp.getName() + "(" + (ConfigQuest.BAO4_QUEST_ITEM[i][1] - pc.getInventory().countItems(temp.getItemId())) + ")"));
                            error = true;
                        }
                    }
                    if (error) {
                        pc.sendPackets(new S_CloseList(pc.getId()));
                    } else {
                        for (int j = 0; j < size; j++) {
                            pc.getInventory().consumeItem(ConfigQuest.BAO4_QUEST_ITEM[j][0], ConfigQuest.BAO4_QUEST_ITEM[j][1]);
                        }
                        pc.getQuest().set_end(L1PcQuest.BAO_QUEST_4);
                        CreateNewItem.createNewItem(pc, _getitem, 1);
                        pc.sendPackets(new S_CloseList(pc.getId()));
                        pc.sendPacketsX8(new S_EffectLocation(pc.getX(), pc.getY(), 14036));
                        pc.save();
                    }
                } else {
                    pc.sendPackets(new S_CloseList(pc.getId()));
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void set_set(String[] set) {
        try {
            _getitem = Integer.parseInt(set[1]);
        } catch (Exception e) {
        }
    }
}
