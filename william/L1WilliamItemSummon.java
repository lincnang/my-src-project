package william;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.*;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1Npc;

public class L1WilliamItemSummon {
    private int _item_id;
    private int _checkLevel;
    private int _checkClass;
    private int _checkItem;
    private int _hpConsume;
    private int _mpConsume;
    private int _material;
    private int _material_count;
    private int _summon_id;
    private int _summonCost;
    private int _onlyOne;
    private int _removeItem;

    public L1WilliamItemSummon(int item_id, int checkLevel, int checkClass, int checkItem, int hpConsume, int mpConsume, int material, int material_count, int summon_id, int summonCost, int onlyOne, int removeItem) {
        _item_id = item_id;
        _checkLevel = checkLevel;
        _checkClass = checkClass;
        _checkItem = checkItem;
        _hpConsume = hpConsume;
        _mpConsume = mpConsume;
        _material = material;
        _material_count = material_count;
        _summon_id = summon_id;
        _summonCost = summonCost;
        _onlyOne = onlyOne;
        _removeItem = removeItem;
    }

    public static int checkItemId(int itemId) {
        L1WilliamItemSummon Item_Summon = ItemSummon.getInstance().getTemplate(itemId);
        if (Item_Summon == null) {
            return 0;
        }
        int item_id = Item_Summon.getItemId();
        return item_id;
    }

    public static void getItemSummon(L1PcInstance pc, L1ItemInstance l1iteminstance, int itemId) {
        L1WilliamItemSummon Item_Summon = ItemSummon.getInstance().getTemplate(itemId);
        boolean isSummon = false;
        boolean isConsumeItem = false;
        if (Item_Summon == null) {
            return;
        }
        if ((Item_Summon.getCheckLevel() != 0) && (pc.getLevel() < Item_Summon.getCheckLevel())) {
            pc.sendPackets(new S_ServerMessage(318));
            return;
        }
        if (Item_Summon.getCheckClass() != 0) {
            byte class_id = 0;
            String msg = "";
            if (pc.isCrown()) {
                class_id = 1;
            } else if (pc.isKnight()) {
                class_id = 2;
            } else if (pc.isWizard()) {
                class_id = 3;
            } else if (pc.isElf()) {
                class_id = 4;
            } else if (pc.isDarkelf()) {
                class_id = 5;
            } else if (pc.isDragonKnight()) {
                class_id = 6;
            } else if (pc.isIllusionist()) {
                class_id = 7;
            } else if (pc.isWarrior()) {
                class_id = 8;
            }
            switch (Item_Summon.getCheckClass()) {
                case 1:
                    msg = "王族";
                    break;
                case 2:
                    msg = "騎士";
                    break;
                case 3:
                    msg = "法師";
                    break;
                case 4:
                    msg = "妖精";
                    break;
                case 5:
                    msg = "黑暗妖精";
                    break;
                case 6:
                    msg = "龍騎士";
                    break;
                case 7:
                    msg = "幻術士";
                case 8:
                    msg = "戰士";
            }
            if (Item_Summon.getCheckClass() != class_id) {
                pc.sendPackets(new S_ServerMessage(166, "職業必須是" + msg + "才能使用此道具"));
                return;
            }
        }
        if (pc.getCha() < Item_Summon.getSummonCost()) {
            pc.sendPackets(new S_ServerMessage(166, "魅力最低需求 {" + Item_Summon.getSummonCost() + ")"));
            return;
        }
        if ((Item_Summon.getCheckItem() != 0) && (!pc.getInventory().checkItem(Item_Summon.getCheckItem()))) {
            L1Item temp = ItemTable.get().getTemplate(Item_Summon.getCheckItem());
            pc.sendPackets(new S_ServerMessage(166, "必須有 (" + temp.getName() + ") 才能使用此道具"));
            return;
        }
        if ((Item_Summon.getHpConsume() != 0) || (Item_Summon.getMpConsume() != 0)) {
            if (pc.getCurrentHp() < Item_Summon.getHpConsume()) {
                pc.sendPackets(new S_ServerMessage(166, "$1083", "必須有 (" + Item_Summon.getHpConsume() + ") 以上, 才能使用此道具"));
                return;
            }
            if (pc.getCurrentMp() < Item_Summon.getMpConsume()) {
                pc.sendPackets(new S_ServerMessage(166, "$1083", "必須有 (" + Item_Summon.getMpConsume() + ") 以上, 才能使用此道具"));
                return;
            }
        }
        if ((Item_Summon.getMaterial() != 0) && (Item_Summon.getMaterialCount() != 0)) {
            if (!pc.getInventory().checkItem(Item_Summon.getMaterial(), Item_Summon.getMaterialCount())) {
                L1Item temp = ItemTable.get().getTemplate(Item_Summon.getMaterial());
                pc.sendPackets(new S_ServerMessage(337, temp.getName() + "(" + (Item_Summon.getMaterialCount() - pc.getInventory().countItems(temp.getItemId())) + ")"));
                return;
            }
            isConsumeItem = true;
        }
        if (pc.getMap().isRecallPets()) {
            int summonCost = Item_Summon.getSummonCost();
            if (summonCost < 6) {
                summonCost = 6;
            }
            int petcost = 0;
            for (Object pet : pc.getPetList().values().toArray()) {
                petcost += ((L1NpcInstance) pet).getPetcost();
            }
            int charisma = pc.getCha();
            if (pc.isCrown()) {
                charisma += 6;
            } else if (pc.isElf()) {
                charisma += 12;
            } else if (pc.isWizard()) {
                charisma += 6;
            } else if (pc.isDarkelf()) {
                charisma += 6;
            } else if (pc.isDragonKnight()) {
                charisma += 6;
            } else if (pc.isIllusionist()) {
                charisma += 6;
            } else if (pc.isWarrior()) {
                charisma += 6;
            }
            charisma -= petcost;
            int summonCount = charisma / summonCost;
            if (summonCount >= 5) {
                summonCount = 5;
            } else if (summonCount <= 0) {
                if (petcost > 0) {
                    pc.sendPackets(new S_ServerMessage(489));
                    return;
                }
                pc.sendPackets(new S_ServerMessage(166, "魅力最低需求 {" + summonCost + ")"));
            }
            if (Item_Summon.getOnlyOne() != 0) {
                for (Object petNpc : pc.getPetList().values().toArray()) {
                    if ((petNpc instanceof L1SummonInstance)) {
                        L1SummonInstance _summon = (L1SummonInstance) petNpc;
                        if (_summon != null) {
                            pc.sendPackets(new S_ServerMessage(489));
                        }
                    } else if ((petNpc instanceof L1PetInstance)) {
                        L1PetInstance _pet = (L1PetInstance) petNpc;
                        if (_pet != null) {
                            pc.sendPackets(new S_ServerMessage(489));
                            return;
                        }
                    }
                }
                summonCount = 1;
            }
            L1Npc npcTemp = NpcTable.get().getTemplate(Item_Summon.getSummonId());
            for (int i = 0; i < summonCount; i++) {
                L1SummonInstance summon = new L1SummonInstance(npcTemp, pc);
                if (Item_Summon.getOnlyOne() != 0) {
                    summon.setPetcost(pc.getCha() + 7);
                } else {
                    summon.setPetcost(summonCost);
                }
                isSummon = true;
            }
        } else {
            pc.sendPackets(new S_ServerMessage(353));
        }
        if (isSummon) {
            if (isConsumeItem) {
                pc.getInventory().consumeItem(Item_Summon.getMaterial(), Item_Summon.getMaterialCount());
            }
            if (Item_Summon.getRemoveItem() != 0) {
                pc.getInventory().removeItem(l1iteminstance, 1L);
            }
            pc.setCurrentHp(pc.getCurrentHp() - Item_Summon.getHpConsume());
            pc.setCurrentMp(pc.getCurrentMp() - Item_Summon.getMpConsume());
        }
    }

    public int getItemId() {
        return _item_id;
    }

    public int getCheckLevel() {
        return _checkLevel;
    }

    public int getCheckClass() {
        return _checkClass;
    }

    public int getCheckItem() {
        return _checkItem;
    }

    public int getHpConsume() {
        return _hpConsume;
    }

    public int getMpConsume() {
        return _mpConsume;
    }

    public int getMaterial() {
        return _material;
    }

    public int getMaterialCount() {
        return _material_count;
    }

    public int getSummonId() {
        return _summon_id;
    }

    public int getSummonCost() {
        return _summonCost;
    }

    public int getOnlyOne() {
        return _onlyOne;
    }

    public int getRemoveItem() {
        return _removeItem;
    }
}
