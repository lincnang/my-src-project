package com.lineage.data.npc;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

public class Npc_Riddle extends NpcExecutor {
    public static NpcExecutor get() {
        return new Npc_Riddle();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "riddle1"));
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        String htmlid = null;
        if (cmd.equalsIgnoreCase("A")) {
            if (pc.getInventory().checkItem(49928, 1)) {
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
                pc.getInventory().consumeItem(49928, 1);
            } else if (pc.getInventory().checkItem(149928, 1)) {
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
                pc.getInventory().consumeItem(149928, 1);
            } else {
                for (L1ItemInstance Item : pc.getInventory().getItems()) {
                    if (Item.getItemId() >= 600000 && Item.getItemId() <= 600039) {
                        htmlid = "riddle2";
                        break;
                    }
                }
            }
        } else if (cmd.equalsIgnoreCase("B")) {
            if (pc.getInventory().checkItem(49928, 1)) {
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
                pc.getInventory().consumeItem(49928, 1);
            } else if (pc.getInventory().checkItem(149928, 1)) {
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
                pc.getInventory().consumeItem(149928, 1);
            } else {
                for (L1ItemInstance Item : pc.getInventory().getItems()) {
                    if (Item.getItemId() >= 600000 && Item.getItemId() <= 600039) {
                        htmlid = "riddle2";
                        break;
                    }
                }
            }
        } else if (cmd.equalsIgnoreCase("C")) {
            if (pc.getInventory().checkItem(49928, 1)) {
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
                pc.getInventory().consumeItem(49928, 1);
            } else if (pc.getInventory().checkItem(149928, 1)) {
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
                pc.getInventory().consumeItem(149928, 1);
            } else {
                for (L1ItemInstance Item : pc.getInventory().getItems()) {
                    if (Item.getItemId() >= 600000 && Item.getItemId() <= 600039) {
                        htmlid = "riddle2";
                        break;
                    }
                }
            }
        } else if (cmd.equalsIgnoreCase("D")) {
            if (pc.getInventory().checkItem(49928, 1)) {
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
                pc.getInventory().consumeItem(49928, 1);
            } else if (pc.getInventory().checkItem(149928, 1)) {
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
                pc.getInventory().consumeItem(149928, 1);
            } else {
                for (L1ItemInstance Item : pc.getInventory().getItems()) {
                    if (Item.getItemId() >= 600000 && Item.getItemId() <= 600039) {
                        htmlid = "riddle2";
                        break;
                    }
                }
            }
        } else if (cmd.equalsIgnoreCase("E")) {
            if (pc.getInventory().checkItem(49928, 1)) {
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
                pc.getInventory().consumeItem(49928, 1);
            } else if (pc.getInventory().checkItem(149928, 1)) {
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
                pc.getInventory().consumeItem(149928, 1);
            } else {
                for (L1ItemInstance Item : pc.getInventory().getItems()) {
                    if (Item.getItemId() >= 600000 && Item.getItemId() <= 600039) {
                        htmlid = "riddle2";
                        break;
                    }
                }
            }
        }
        if (htmlid != null) { // html指定場合表示
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), htmlid));
        }
    }
}
