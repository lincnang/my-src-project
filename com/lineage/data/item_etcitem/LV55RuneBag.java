package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

public class LV55RuneBag extends ItemExecutor {
    public static ItemExecutor get() {
        return new LV55RuneBag();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int itemId = item.getItemId();
        if (itemId == 49866) { // 力量符石袋
            if (pc.isCrown()) {
                CreateNewItem.createNewItem(pc, 600000, 1);
            } else if (pc.isKnight()) {
                CreateNewItem.createNewItem(pc, 600001, 1);
            } else if (pc.isElf()) {
                CreateNewItem.createNewItem(pc, 600002, 1);
            } else if (pc.isWizard()) {
                CreateNewItem.createNewItem(pc, 600003, 1);
            } else if (pc.isDarkelf()) {
                CreateNewItem.createNewItem(pc, 600004, 1);
            } else if (pc.isDragonKnight()) {
                CreateNewItem.createNewItem(pc, 600005, 1);
            } else if (pc.isIllusionist()) {
                CreateNewItem.createNewItem(pc, 600006, 1);
            } else if (pc.isWarrior()) {
                CreateNewItem.createNewItem(pc, 600007, 1);
            }
            CreateNewItem.createNewItem(pc, 49477, 1);
            pc.getInventory().removeItem(item, 1);
        } else if (itemId == 49867) { // 敏捷符石袋
            if (pc.isCrown()) {
                CreateNewItem.createNewItem(pc, 600008, 1);
            } else if (pc.isKnight()) {
                CreateNewItem.createNewItem(pc, 600009, 1);
            } else if (pc.isElf()) {
                CreateNewItem.createNewItem(pc, 600010, 1);
            } else if (pc.isWizard()) {
                CreateNewItem.createNewItem(pc, 600011, 1);
            } else if (pc.isDarkelf()) {
                CreateNewItem.createNewItem(pc, 600012, 1);
            } else if (pc.isDragonKnight()) {
                CreateNewItem.createNewItem(pc, 600013, 1);
            } else if (pc.isIllusionist()) {
                CreateNewItem.createNewItem(pc, 600014, 1);
            } else if (pc.isWarrior()) {
                CreateNewItem.createNewItem(pc, 600015, 1);
            }
            CreateNewItem.createNewItem(pc, 49477, 1);
            pc.getInventory().removeItem(item, 1);
        } else if (itemId == 49868) { // 體質符石袋
            if (pc.isCrown()) {
                CreateNewItem.createNewItem(pc, 600016, 1);
            } else if (pc.isKnight()) {
                CreateNewItem.createNewItem(pc, 600017, 1);
            } else if (pc.isElf()) {
                CreateNewItem.createNewItem(pc, 600018, 1);
            } else if (pc.isWizard()) {
                CreateNewItem.createNewItem(pc, 600019, 1);
            } else if (pc.isDarkelf()) {
                CreateNewItem.createNewItem(pc, 600020, 1);
            } else if (pc.isDragonKnight()) {
                CreateNewItem.createNewItem(pc, 600021, 1);
            } else if (pc.isIllusionist()) {
                CreateNewItem.createNewItem(pc, 600022, 1);
            } else if (pc.isWarrior()) {
                CreateNewItem.createNewItem(pc, 600023, 1);
            }
            CreateNewItem.createNewItem(pc, 49477, 1);
            pc.getInventory().removeItem(item, 1);
        } else if (itemId == 49869) { // 智力符石袋
            if (pc.isCrown()) {
                CreateNewItem.createNewItem(pc, 600024, 1);
            } else if (pc.isKnight()) {
                CreateNewItem.createNewItem(pc, 600025, 1);
            } else if (pc.isElf()) {
                CreateNewItem.createNewItem(pc, 600026, 1);
            } else if (pc.isWizard()) {
                CreateNewItem.createNewItem(pc, 600027, 1);
            } else if (pc.isDarkelf()) {
                CreateNewItem.createNewItem(pc, 600028, 1);
            } else if (pc.isDragonKnight()) {
                CreateNewItem.createNewItem(pc, 600029, 1);
            } else if (pc.isIllusionist()) {
                CreateNewItem.createNewItem(pc, 600030, 1);
            } else if (pc.isWarrior()) {
                CreateNewItem.createNewItem(pc, 600031, 1);
            }
            CreateNewItem.createNewItem(pc, 49477, 1);
            pc.getInventory().removeItem(item, 1);
        } else if (itemId == 49870) { // 精神符石袋
            if (pc.isCrown()) {
                CreateNewItem.createNewItem(pc, 600032, 1);
            } else if (pc.isKnight()) {
                CreateNewItem.createNewItem(pc, 600033, 1);
            } else if (pc.isElf()) {
                CreateNewItem.createNewItem(pc, 600034, 1);
            } else if (pc.isWizard()) {
                CreateNewItem.createNewItem(pc, 600035, 1);
            } else if (pc.isDarkelf()) {
                CreateNewItem.createNewItem(pc, 600036, 1);
            } else if (pc.isDragonKnight()) {
                CreateNewItem.createNewItem(pc, 600037, 1);
            } else if (pc.isIllusionist()) {
                CreateNewItem.createNewItem(pc, 600038, 1);
            } else if (pc.isWarrior()) {
                CreateNewItem.createNewItem(pc, 600039, 1);
            }
            CreateNewItem.createNewItem(pc, 49477, 1);
            pc.getInventory().removeItem(item, 1);
        }
    }
}