package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

public class LV70RuneBag extends ItemExecutor {
    public static ItemExecutor get() {
        return new LV70RuneBag();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int itemId = item.getItemId();
        if (itemId == 49871) { // 力量符石袋
            if (pc.isCrown()) {
                CreateNewItem.createNewItem(pc, 600041, 1);
            } else if (pc.isKnight()) {
                CreateNewItem.createNewItem(pc, 600042, 1);
            } else if (pc.isElf()) {
                CreateNewItem.createNewItem(pc, 600043, 1);
            } else if (pc.isWizard()) {
                CreateNewItem.createNewItem(pc, 600044, 1);
            } else if (pc.isDarkelf()) {
                CreateNewItem.createNewItem(pc, 600045, 1);
            } else if (pc.isDragonKnight()) {
                CreateNewItem.createNewItem(pc, 600046, 1);
            } else if (pc.isIllusionist()) {
                CreateNewItem.createNewItem(pc, 600047, 1);
            } else if (pc.isWarrior()) {
                CreateNewItem.createNewItem(pc, 600048, 1);
            }
            pc.getInventory().removeItem(item, 1);
        } else if (itemId == 49872) { // 敏捷符石袋
            if (pc.isCrown()) {
                CreateNewItem.createNewItem(pc, 600049, 1);
            } else if (pc.isKnight()) {
                CreateNewItem.createNewItem(pc, 600050, 1);
            } else if (pc.isElf()) {
                CreateNewItem.createNewItem(pc, 600051, 1);
            } else if (pc.isWizard()) {
                CreateNewItem.createNewItem(pc, 600052, 1);
            } else if (pc.isDarkelf()) {
                CreateNewItem.createNewItem(pc, 600053, 1);
            } else if (pc.isDragonKnight()) {
                CreateNewItem.createNewItem(pc, 600054, 1);
            } else if (pc.isIllusionist()) {
                CreateNewItem.createNewItem(pc, 600055, 1);
            } else if (pc.isWarrior()) {
                CreateNewItem.createNewItem(pc, 600056, 1);
            }
            pc.getInventory().removeItem(item, 1);
        } else if (itemId == 49873) { // 體質符石袋
            if (pc.isCrown()) {
                CreateNewItem.createNewItem(pc, 600057, 1);
            } else if (pc.isKnight()) {
                CreateNewItem.createNewItem(pc, 600058, 1);
            } else if (pc.isElf()) {
                CreateNewItem.createNewItem(pc, 600059, 1);
            } else if (pc.isWizard()) {
                CreateNewItem.createNewItem(pc, 600060, 1);
            } else if (pc.isDarkelf()) {
                CreateNewItem.createNewItem(pc, 600061, 1);
            } else if (pc.isDragonKnight()) {
                CreateNewItem.createNewItem(pc, 600062, 1);
            } else if (pc.isIllusionist()) {
                CreateNewItem.createNewItem(pc, 600063, 1);
            } else if (pc.isWarrior()) {
                CreateNewItem.createNewItem(pc, 600064, 1);
            }
            pc.getInventory().removeItem(item, 1);
        } else if (itemId == 49874) { // 智力符石袋
            if (pc.isCrown()) {
                CreateNewItem.createNewItem(pc, 600065, 1);
            } else if (pc.isKnight()) {
                CreateNewItem.createNewItem(pc, 600066, 1);
            } else if (pc.isElf()) {
                CreateNewItem.createNewItem(pc, 600067, 1);
            } else if (pc.isWizard()) {
                CreateNewItem.createNewItem(pc, 600068, 1);
            } else if (pc.isDarkelf()) {
                CreateNewItem.createNewItem(pc, 600069, 1);
            } else if (pc.isDragonKnight()) {
                CreateNewItem.createNewItem(pc, 600070, 1);
            } else if (pc.isIllusionist()) {
                CreateNewItem.createNewItem(pc, 600071, 1);
            } else if (pc.isWarrior()) {
                CreateNewItem.createNewItem(pc, 600072, 1);
            }
            pc.getInventory().removeItem(item, 1);
        } else if (itemId == 49875) { // 精神符石袋
            if (pc.isCrown()) {
                CreateNewItem.createNewItem(pc, 600073, 1);
            } else if (pc.isKnight()) {
                CreateNewItem.createNewItem(pc, 600074, 1);
            } else if (pc.isElf()) {
                CreateNewItem.createNewItem(pc, 600075, 1);
            } else if (pc.isWizard()) {
                CreateNewItem.createNewItem(pc, 600076, 1);
            } else if (pc.isDarkelf()) {
                CreateNewItem.createNewItem(pc, 600077, 1);
            } else if (pc.isDragonKnight()) {
                CreateNewItem.createNewItem(pc, 600078, 1);
            } else if (pc.isIllusionist()) {
                CreateNewItem.createNewItem(pc, 600079, 1);
            } else if (pc.isWarrior()) {
                CreateNewItem.createNewItem(pc, 600080, 1);
            }
            pc.getInventory().removeItem(item, 1);
        }
    }
}