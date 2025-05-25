package com.add.Tsai;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Item;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 成就指令
 *
 * @author hero
 */
public class EquipCollectCmd {
    private static final Log _log = LogFactory.getLog(EquipCollectCmd.class);
    private static String _npcid = "100031";
    private static EquipCollectCmd _instance;
    private String _tempCmd;

    public static EquipCollectCmd get() {
        if (_instance == null) {
            _instance = new EquipCollectCmd();
        }
        return _instance;
    }

    public boolean Cmd(final L1PcInstance pc, final String cmd) {
        try {
            final StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < EquipCollectTable.getInstance().get_craftlist().size(); i++) {
                L1EquipCollect equipCollect = EquipCollectTable.getInstance().get_craftlist().get(i);
                if (equipCollect != null && pc.getQuest().get_step(equipCollect.getquest()) != 0) {
                    stringBuilder.append(String.valueOf("[已解鎖]" + ","));
                } else {
                    stringBuilder.append(String.valueOf("[未解鎖]" + ","));
                }
            }
            final String[] msg = stringBuilder.toString().split(",");//從0開始分裂以逗號為單位
            boolean ok = false;
            switch (cmd) {
                case "collect":
                case "collect0":
                case "collect1":
                case "collect2":
                case "collect3":
                case "collect4":
                case "collect5":
                    ok = true;
                    pc.sendPackets(new S_NPCTalkReturn(pc, cmd, msg));
                    break;
                case "confirm craft2": {
                    L1EquipCollect equipCollect = EquipCollectTable.getInstance().getTemplate(_tempCmd);
                    if (equipCollect != null) {
                        ok = true;
                        //						confirmCraft(pc, equipCollect);
                        equipCollect.CheckCraftItem(pc, equipCollect, 1, false);
                    }
                }
                break;
                default:
                    L1EquipCollect equipCollect = EquipCollectTable.getInstance().getTemplate(_npcid + cmd);
                    if (equipCollect != null) {
                        _tempCmd = _npcid + cmd;
                        ok = true;
                        //						final String[] detail = getCollectDetail(pc, equipCollect);
                        //						pc.sendPackets(new S_NPCTalkReturn(pc, "collectitem", detail));
                        equipCollect.ShowEquipCollectHtml(pc, equipCollect);
                    }
                    break;
            }
            if (ok) {
                return true;
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    private void confirmCraft(final L1PcInstance pc, final L1EquipCollect equipCollect) {
        boolean ok = true;
        for (int i = 0; i < equipCollect.get_materials_enchants().length; i++) {
            int itemid = equipCollect.getMaterials()[i];
            int enchant = equipCollect.get_materials_enchants()[i];
            int itemCount = equipCollect.getMaterials_count()[i];
            if (!pc.getInventory().checkEnchantItem(itemid, enchant, itemCount)) {
                L1Item item = ItemTable.get().getTemplate(itemid);
                String enchantName = enchant > 0 ? "+" + enchant : "";
                String itemName = enchantName + item.getName() + "(" + itemCount + ")不足。";
                pc.sendPackets(new S_ServerMessage("\\fR" + itemName));
                ok = false;
            }
        }
        if (!ok) {
            return;
        }
        for (int i = 0; i < equipCollect.get_materials_enchants().length; i++) {
            int itemid = equipCollect.getMaterials()[i];
            int enchant = equipCollect.get_materials_enchants()[i];
            int itemCount = equipCollect.getMaterials_count()[i];
            if (!pc.getInventory().consumeEnchantItem(itemid, enchant, itemCount)) {
                pc.sendPackets(new S_ServerMessage("\\fRXXX"));
                return;
            }
        }
        pc.getQuest().set_end(equipCollect.getquest());
        pc.sendPackets(new S_ServerMessage("\\fR成就已解鎖"));
    }

    private String[] getCollectDetail(final L1PcInstance pc, final L1EquipCollect equipCollect) {
        final StringBuilder stringBuilder = new StringBuilder();
        // #0
        stringBuilder.append(equipCollect.get_note()).append(",");
        // #1
        stringBuilder.append(equipCollect.get_givebuff()).append(",");
        // #2
        stringBuilder.append(",");
        // #3
        stringBuilder.append(",");
        // #4-#50
        for (int i = 0; i < equipCollect.get_materials_enchants().length; i++) {
            L1Item item = ItemTable.get().getTemplate(equipCollect.getMaterials()[i]);
            if (item == null) {
                stringBuilder.append("XXX" + ",");
                continue;
            }
            // +X
            String enchant = equipCollect.get_materials_enchants()[i] == 0 ? "" : "+" + equipCollect.get_materials_enchants()[i] + "";
            // 道具名稱
            String itemName = item.getName();
            // 數量
            String itemCount = "(" + equipCollect.getMaterials_count()[i] + ") 個";
            stringBuilder.append(enchant).append(itemName).append(itemCount).append(",");
        }
        for (int i = 4; i <= 50 - equipCollect.get_materials_enchants().length; i++) {
            stringBuilder.append(",");
        }
        // #51-53
        for (int i = 51; i <= 53; i++) {
            stringBuilder.append(",");
        }
        // #54
        if (pc.getQuest().get_step(equipCollect.getquest()) != 0) {
            stringBuilder.append("已登錄道具,");
        } else {
            stringBuilder.append("未登錄道具,");
        }
        return stringBuilder.toString().split(",");//從0開始分裂以逗號為單位
    }
}