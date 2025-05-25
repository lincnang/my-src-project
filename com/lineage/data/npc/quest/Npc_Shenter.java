package com.lineage.data.npc.quest;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.SoulTowerTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 屍魂塔-天使^艾澤奇爾
 *
 * @author
 */
public class Npc_Shenter extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Shenter.class);

    public static NpcExecutor get() {
        return new Npc_Shenter();
    }

    public int type() {
        return 3;
    }

    // shenter1
    // <body>
    // <font fg=ffffff><p align=left>埃塞基耶爾：</p></font>
    // <br>
    // 這裡是關押具有很強邪念的靈魂的監獄。<br>
    // 可是，最近異界支配者吉爾塔斯復活了，
    // 屍魂之塔的封印遭到了破壞。<br>
    // 勇士啊……請你一定要守住亞丁啊。
    // <br><br>
    // <a action="a">收下屍魂水晶。</a><br>
    // </body>
    // shenter2
    // <body>
    // <font fg=ffffff><p align=left>埃塞基耶爾：</p></font>
    // <br>
    // 惡靈不斷地涌來。<br>
    // 在我們完成封印魔法之前，請你再堅持一會兒。<br>
    // 我們會給每周最優秀的勇士發放特別的獎勵。
    // <br><br>
    // <a action="r">查看排名。</a><br>
    // <a link="shenter3">屍魂水晶是什麼？</a><br>
    // </body>
    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (!pc.getInventory().checkItem(640326) && !pc.getInventory().checkItem(640327)) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "shenter1"));// 顯示領取屍魂水晶
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "shenter2"));// 顯示排行
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        try {
            if (cmd.equalsIgnoreCase("r")) { // 查看排名
                SoulTowerTable.getInstance().showRank(pc);
            } else if (cmd.equalsIgnoreCase("a")) {// 接受屍魂的水晶
                if (pc.getInventory().checkItem(640327)) { // 封印的屍魂水晶
                    pc.sendPackets(new S_SystemMessage("屍魂水晶還在封印中"));
                    return;
                }
                if (pc.getInventory().checkItem(640326)) { // 藍色屍魂水晶
                    pc.sendPackets(new S_SystemMessage("你已經領取過了"));
                    return;
                }
                if (pc.getInventory().getSize() > 175) {
                    pc.sendPackets(new S_SystemMessage("你身上持有道具過多，領取失敗"));
                    return;
                }
                //pc.getInventory().storeItem(640326, 1); // 給予藍色屍魂水晶
                //pc.sendPackets(new S_SystemMessage("獲得藍色屍魂水晶"));
                CreateNewItem.createNewItem(pc, 640326, 1); // 給予藍色屍魂水晶
                pc.sendPackets(new S_CloseList(pc.getId())); // 關閉對話檔
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
