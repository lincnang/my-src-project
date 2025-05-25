package com.lineage.data.npc;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
//

/**
 * 兌換商人-勇者徽章<BR>
 * 85029<BR>
 *
 * @author dexc
 */
public class Npc_Action2 extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Action2.class);

    private Npc_Action2() {
        // TODO Auto-generated constructor stub
    }

    public static NpcExecutor get() {
        return new Npc_Action2();
    }

    @Override
    public int type() {
        return 3;
    }

    @Override
    public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
        try {
            final String nowDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
            final String[] info = new String[]{nowDate};// 目前時間
            // 活動商人對話。
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_action_2", info));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void action(final L1PcInstance pc, final L1NpcInstance npc, final String s, final long amount) {
        // 83057 羊角
        // 83058 惡魔羊鬃毛
        if (s.equalsIgnoreCase("y_action0")) {
            getScore(pc, 83056, 1, 83057, 1, 40308, 200000);
        } else if (s.equalsIgnoreCase("y_action1")) {
            getScore(pc, 83056, 1, 83058, 50, 40308, 200000);
        }
    }

    /**
     * 兌換積分
     *
     * @param pc:玩家
     * @param ietmid:給予物品ID
     * @param count:給予物品數量
     * @param delscore:需求積分
     */
    private void getScore(final L1PcInstance pc, int toitemid, int toitemcount, int needitemid1, int needcount1, int needitemid2, int needcount2) {
        try {
            final L1ItemInstance toitem = ItemTable.get().createItem(toitemid);
            // 需要的物件確認
            final L1ItemInstance needitem1 = pc.getInventory().checkItemX(needitemid1, needcount1);
            final L1ItemInstance needitem2 = pc.getInventory().checkItemX(needitemid2, needcount2);
            if (needitem1 == null || needitem2 == null) {
                pc.sendPackets(new S_ServerMessage(" \\fR材料不足!"));
                pc.sendPackets(new S_CloseList(pc.getId()));
                return;
            }
            if (toitem != null) {
                toitem.setCount(toitemcount);
                // 加入背包成功
                if (pc.getInventory().checkAddItem(toitem, toitemcount) == 0) {
                    pc.getInventory().storeItem(toitem);
                    // 送出訊息
                    pc.sendPackets(new S_ServerMessage(403, toitem.getLogName()));
                    pc.getInventory().removeItem(needitem1, needcount1);// 刪除道具
                    pc.getInventory().removeItem(needitem2, needcount2);// 刪除道具
                    toGmMsg2(pc, toitem);
                }
            }
            // 關閉對話窗
            pc.sendPackets(new S_CloseList(pc.getId()));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 通知GM
     */
    private void toGmMsg2(final L1PcInstance pc, final L1ItemInstance element) {
        try {
            final Collection<L1PcInstance> allPc = World.get().getAllPlayers();
            for (L1PcInstance tgpc : allPc) {
                if (tgpc.isGm()) {
                    final StringBuilder topc = new StringBuilder();
                    topc.append("人物:" + pc.getName() + " 兌換道具:" + element.getLogName());
                    tgpc.sendPackets(new S_ServerMessage(166, topc.toString()));
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
