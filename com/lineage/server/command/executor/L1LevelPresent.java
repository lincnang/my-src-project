package com.lineage.server.command.executor;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.DwarfForChaReading;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1CharName;
import com.lineage.server.templates.L1Item;
import com.lineage.server.world.World;

import java.util.StringTokenizer;

public class L1LevelPresent implements L1CommandExecutor {
    public static L1CommandExecutor getInstance() {
        return new L1LevelPresent();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer st = new StringTokenizer(arg);
            int minlvl = Integer.parseInt(st.nextToken());
            int maxlvl = Integer.parseInt(st.nextToken());
            int itemid = Integer.parseInt(st.nextToken());
            int count = Integer.parseInt(st.nextToken());
            int enchant = Integer.parseInt(st.nextToken());
            L1Item temp = ItemTable.get().getTemplate(itemid);
            if (temp == null) {
                pc.sendPackets(new S_SystemMessage("不存在的道具編號。"));
                return;
            }
            // 產生新物件
            final L1ItemInstance item = ItemTable.get().createItem(itemid);
            if (item != null) {
                item.setCount(count);
                item.setEnchantLevel(enchant);
                item.setIdentified(true);
                for (L1CharName charName : CharacterTable.get().getCharNameList()) {
                    L1PcInstance tgpc = CharacterTable.get().restoreCharacter(charName.getName());
                    if (tgpc != null) {// DB中具有指定玩家
                        if (tgpc.getLevel() >= minlvl && tgpc.getLevel() <= maxlvl) {
                            DwarfForChaReading.get().insertItem(tgpc.getName(), item);// 加入角色專屬倉庫數據
                            // 重整倉庫數據
                            tgpc.getDwarfForChaInventory().loadItems();
                            L1PcInstance onlinepc = World.get().getPlayer(charName.getName());
                            if (onlinepc != null) {// 角色在線上
                                onlinepc.sendPackets(new S_ServerMessage(166, "管理員在你的角色專屬倉庫加入物品：" + item.getLogName()));
                            }
                        }
                    }
                }
            }
            pc.sendPackets(new S_SystemMessage(item.getLogName() + "發送出去了。" + "(Lv" + minlvl + "至" + maxlvl + ")"));
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage("請輸入 minlvl maxlvl 道具編號 數量 強化等級。"));
        }
    }
}
