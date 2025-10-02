package com.lineage.server.clientpackets;

import com.add.Tsai.FireDisCmd;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.T_CraftConfigTable;
import com.lineage.server.datatables.T_CraftConfigTable.NewL1NpcMakeItemAction;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1ObjectAmount;
import com.lineage.server.model.L1PcInventory;
// import com.lineage.server.model.npc.L1NpcHtml;
// import com.lineage.server.model.npc.action.L1NpcAction;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.RandomArrayList;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class C_ItemCraft extends ClientBasePacket {
    private static final Log _log = LogFactory.getLog(C_ItemCraft.class);

    @Override
    public void start(final byte[] decrypt, final ClientExecutor client) {
        final L1PcInstance maker = client.getActiveChar();
        try {
            // 資料載入
            read(decrypt);
            // 使用者
            final L1PcInstance pc = client.getActiveChar();
            if (pc == null) { // 角色為空
                return;
            }
            final int type0 = readH();
            if (type0 == 54) {
                this.readH();
                byte[] sha1 = readCraftB();
                String sha1String = T_CraftConfigTable.update(sha1);
                if (sha1String.equals(T_CraftConfigTable.get().getSHAkey())) {
                    pc.sendPackets(new S_ItemCraftList(3));
                } else {
                    Collection<NewL1NpcMakeItemAction> all = T_CraftConfigTable.get().getNpcMakeItemList().values();
                    boolean first = true;
                    S_ItemCraftList stemCraftList;
                    for (NewL1NpcMakeItemAction l1NpcMakeItemAction : all) {
                        if (first) {
                            stemCraftList = new S_ItemCraftList(l1NpcMakeItemAction, first);
                            pc.sendPackets(stemCraftList);
                            first = false;
                        } else {
                            stemCraftList = new S_ItemCraftList(l1NpcMakeItemAction, false);
                            pc.sendPackets(stemCraftList);
                        }
                    }
                    pc.sendPackets(new S_ItemCraftList(null, false));
                }
            } else if (type0 == 56) {
                this.readH();
                int npcObjId = readCraft();
                FireDisCmd.get().SendItemCraft(pc, npcObjId);
            } else if (type0 == 58) {
                this.readH();
                int npcObjId = readCraft();
                int actionId = readCraft();
                int changeCount = readCraft();
                L1Object npc = World.get().findObject(npcObjId);
                if (!(npc instanceof L1NpcInstance)) {
                    return;
                }
                //				int difflocx = Math.abs(pc.getX() - npc.getX());
                //				int difflocy = Math.abs(pc.getY() - npc.getY());
                //				if (pc.getMapId() != npc.getMapId() || (difflocx > 10)
                //						|| (difflocy > 10)) {
                //					return;
                //				}
                L1NpcInstance npcObj = (L1NpcInstance) npc;
                HashMap<Integer, NewL1NpcMakeItemAction> npcMakeItemActions = T_CraftConfigTable.get().getNpcMakeItemActionList(npcObj.getNpcId());
                if (npcMakeItemActions == null) {
                    return;
                }
                T_CraftConfigTable.NewL1NpcMakeItemAction npcMakeItemAction = npcMakeItemActions.get(actionId);
                if (npcMakeItemAction == null) {
                    return;
                }
                ArrayList<Integer> polyIds = npcMakeItemAction.getCraftPolyList();
                if ((polyIds != null) && (!polyIds.contains(pc.getTempCharGfx()))) {
                    return;
                }
                if (!npcMakeItemAction.getAmountLevelRange().includes(pc.getLevel())) {
                    return;
                }
                if (!npcMakeItemAction.getAmountLawfulRange().includes(pc.getLawful())) {
                    return;
                }
                if (!npcMakeItemAction.getAmountKarmaRange().includes(pc.getKarma())) {
                    return;
                }
                //int key = 0;
                ArrayList<Integer> materialItemDescs = new ArrayList<>();
                HashMap<Integer, Integer> aidCounts = new HashMap<>();
                while (true) {
                    byte[] materialItemArray = readCraftB();
                    if (materialItemArray == null) {
                        break;
                    }
                    if (materialItemArray.length == 0) {
                        break;
                    }
                    C_Empty pack = new C_Empty(materialItemArray);
                    //int materialItemIndex = pack.readCraft();
                    int materialItemDescId = pack.readCraft();
                    materialItemDescs.add(materialItemDescId);
                    if (!pack.jdField_do()) {
                        int aidCount = pack.readCraft();
                        if (aidCount <= 0) {
                            continue;
                        }
                        aidCounts.put(materialItemDescId, aidCount);
                    }
                }
                List<L1ObjectAmount<Integer>> materials = npcMakeItemAction.getAmountMeterialList();
                List<L1ObjectAmount<Integer>> aidMaterials = npcMakeItemAction.getAmountAidMeterialList();
                int materialsSize = materials.size();
                int aidMaterialsSize = aidMaterials.size();
                int materialsSumCount = materialsSize + aidMaterialsSize;
                if (materialItemDescs.size() != materialsSumCount) {
                    return;
                }
                ArrayList<L1ObjectAmount<Integer>> materialItemIds = new ArrayList<>();
                int descId;
                L1ObjectAmount<Integer> materialObj;
                L1ObjectAmount<Integer> substituteObj = null;
                L1ItemInstance materialItemObj;
                ArrayList<L1ObjectAmount<Integer>> substitutes;
                ItemTable itemTable = ItemTable.get();
                for (int i = 0; i < materialsSize; i++) {
                    descId = materialItemDescs.get(i);
                    materialObj = materials.get(i);
                    materialItemObj = itemTable.createItem(materialObj.getObject());
                    if (materialItemObj == null) {
                        pc.sendPackets(new S_ItemCraftList(false, null));
                        return;
                    }
                    //					if (descId == materialItemObj.getItem().getItemDescId()) {
                    if (materialItemObj.getItem().getItemId() > 0) {
                        materialItemIds.add(materialObj);
                    } else {
                        substitutes = materialObj.getAmountList();
                        if (substitutes == null) {
                            continue;
                        }
                        for (L1ObjectAmount<Integer> l1ObjectAmount : substitutes) {
                            materialItemObj = itemTable.createItem(l1ObjectAmount.getObject());
                            if (materialItemObj == null) {
                                continue;
                            }
                            if (descId == materialItemObj.getItem().getItemDescId()) {
                                substituteObj = l1ObjectAmount;
                                materialItemIds.add(substituteObj);
                                break;
                            }
                        }
                        if (substituteObj == null) {
                            return;
                        }
                    }
                }
                ArrayList<L1ObjectAmount<Integer>> aidMaterialItemIds = new ArrayList<>();
                for (int i = 0; i < aidMaterialsSize; i++) {
                    descId = materialItemDescs.get(materialsSize + i);
                    materialObj = aidMaterials.get(i);
                    materialItemObj = itemTable.createItem(materialObj.getObject());
                    if (materialItemObj == null) {
                        return;
                    }
                    if (descId == materialItemObj.getItem().getItemDescId()) {
                        if (aidCounts.get(descId) != null) {
                            aidMaterialItemIds.add(materialObj);
                        }
                    } else {
                        return;
                    }
                }
                L1PcInventory pcInv = pc.getInventory();
                L1ItemInstance[] items;
                L1ItemInstance item;
                L1ItemInstance itemTemp;
                ArrayList<L1ObjectAmount<Integer>> delItemObjIds = new ArrayList<>();
                boolean flag;
                int tempCount;
                for (L1ObjectAmount<Integer> material : materialItemIds) {
                    items = pcInv.findItemsId(material.getObject());
                    itemTemp = ItemTable.get().createItem(material.getObject());
                    if (itemTemp.isStackable()) {
                        flag = false;
                        for (L1ItemInstance l1ItemInstance : items) {
                            item = l1ItemInstance;
                            if ((item.getEnchantLevel() != material.getAmountEnchantLevel()) || ((material.getAmountBless() != 3) && (item.getBless() != material.getAmountBless())) || (item.getCount() < material.getAmount() * changeCount)) {
                                continue;
                            }
                            delItemObjIds.add(new L1ObjectAmount<>(item.getItemId(), material.getAmount() * changeCount));
                            flag = true;
                            break;
                        }
                        if (!flag) {
                            return;
                        }
                    } else {
                        tempCount = 0;
                        for (L1ItemInstance l1ItemInstance : items) {
                            item = l1ItemInstance;
                            if ((item.getEnchantLevel() != material.getAmountEnchantLevel()) || ((material.getAmountBless() != 3) && (item.getBless() != material.getAmountBless())) || (tempCount >= material.getAmount() * changeCount)) {
                                continue;
                            }
                            delItemObjIds.add(new L1ObjectAmount<>(item.getItemId(), 1));
                            tempCount++;
                        }
                        if (tempCount < material.getAmount() * changeCount) {
                            return;
                        }
                    }
                }
                ArrayList<L1ObjectAmount<Integer>> delAidItemObjIds = null;
                int tempCount1;
                if ((npcMakeItemAction.getSucceedRandom() < 1000000) && (aidMaterialItemIds != null)) {
                    delAidItemObjIds = new ArrayList<>();
                    for (L1ObjectAmount<Integer> material : aidMaterialItemIds) {
                        items = pcInv.findItemsId(material.getObject());
                        itemTemp = ItemTable.get().createItem(material.getObject());
                        if (itemTemp.isStackable()) {
                            flag = false;
                            for (L1ItemInstance l1ItemInstance : items) {
                                item = l1ItemInstance;
                                if ((item.getEnchantLevel() != material.getAmountEnchantLevel()) || ((material.getAmountBless() != 3) && (item.getBless() != material.getAmountBless())) || (item.getCount() < aidCounts.get(itemTemp.getItem().getItemDescId()))) {
                                    continue;
                                }
                                delAidItemObjIds.add(new L1ObjectAmount<>(item.getItemId(), aidCounts.get(itemTemp.getItem().getItemDescId())));
                                flag = true;
                                break;
                            }
                            if (!flag) {
                                pc.sendPackets(new S_SystemMessage("您的加成材料不足."));
                                pc.sendPackets(new S_ItemCraftList(false, null));
                                return;
                            }
                        } else {
                            tempCount1 = 0;
                            for (L1ItemInstance l1ItemInstance : items) {
                                item = l1ItemInstance;
                                if ((item.getEnchantLevel() != material.getAmountEnchantLevel()) || ((material.getAmountBless() != 3) && (item.getBless() != material.getAmountBless())) || (tempCount1 >= aidCounts.get(itemTemp.getItem().getItemDescId()))) {
                                    continue;
                                }
                                delAidItemObjIds.add(new L1ObjectAmount<>(item.getItemId(), 1));
                                tempCount1++;
                            }
                            if (tempCount1 < aidCounts.get(itemTemp.getItem().getItemDescId())) {
                                pc.sendPackets(new S_SystemMessage("您的加成材料不足."));
                                pc.sendPackets(new S_ItemCraftList(false, null));
                                return;
                            }
                        }
                    }
                }
                for (L1ObjectAmount<Integer> delItemAmount : delItemObjIds) {
                    if (!pcInv.checkItem(delItemAmount.getObject(), delItemAmount.getAmount())) {
                        pc.sendPackets(new S_SystemMessage("您的材料不足."));
                        pc.sendPackets(new S_ItemCraftList(false, null));
                        return;
                    }
                }
                int sumAidCount = 0;
                if (delAidItemObjIds != null) {
                    for (L1ObjectAmount<Integer> delAidItemAmount : delAidItemObjIds) {
                        sumAidCount += delAidItemAmount.getAmount();
                        if (!pcInv.checkItem(delAidItemAmount.getObject(), delAidItemAmount.getAmount())) {
                            pc.sendPackets(new S_SystemMessage("您的加成材料不足."));
                            pc.sendPackets(new S_ItemCraftList(false, null));
                            return;
                        }
                    }
                }
                // 逐次試作
                final List<L1ObjectAmount<Integer>> test = npcMakeItemAction.getAmountItemList();
                String itemName;
                for (final L1ObjectAmount<Integer> giveItem : test) {
                    int itemId = giveItem.getObject();
                    L1Item itemCheck = itemTable.getTemplate(itemId);
                    itemName = itemCheck.getName();
                    _log.info("玩家:" + pc.getName() + "開始製作:" + itemName + " 嘗試次數(" + changeCount + ")。");
                }

                // 先扣材料（按嘗試次數匯總扣除）
                for (L1ObjectAmount<Integer> delItemAmount : delItemObjIds) {
                    if (pcInv.checkItem(delItemAmount.getObject(), delItemAmount.getAmount())) {
                        pc.getInventory().consumeItem(delItemAmount.getObject(), delItemAmount.getAmount());
                    }
                }
                if (sumAidCount != 0) {
                    for (L1ObjectAmount<Integer> delAidItemAmount : delAidItemObjIds) {
                        int AidItemId = delAidItemAmount.getObject();
                        if (pcInv.checkItem(AidItemId, sumAidCount)) {
                            pc.getInventory().consumeItem(AidItemId, sumAidCount);
                        }
                    }
                }

                int successCount = 0;
                for (int i = 0; i < changeCount; i++) {
                    int rndTry = RandomArrayList.getInc(1000000, 1);
                    if (rndTry <= npcMakeItemAction.getSucceedRandom() + 10000 * sumAidCount) {
                        successCount++;
                    }
                }

                final List<L1ItemInstance> giveItemObjs = new ArrayList<>();
                // 發放成功產物
                if (successCount > 0) {
                    giveItemObjs.addAll(craftadditem(pc, pcInv, successCount, npcMakeItemAction.getAmountItemList(), npcMakeItemAction.isAmountBroad(), npcMakeItemAction.getSystemMessage()));
                    // 隨機加獎，按成功次數獨立擲
                    for (int i = 0; i < successCount; i++) {
                        int r = RandomArrayList.getInt(npcMakeItemAction.getAmountRandom());
                        int acc = 0;
                        for (L1ObjectAmount<Integer> objectAmount : npcMakeItemAction.getAmountRandomItemList()) {
                            acc += objectAmount.getAmountRandom();
                            if (r < acc) {
                                giveItemObjs.addAll(b(pc, pcInv, 1, objectAmount.getObject(), (int) objectAmount.getAmount(), objectAmount.getAmountEnchantLevel(), objectAmount.getAmountBless(), npcMakeItemAction.isAmountBroad(), npcMakeItemAction.getSystemMessage()));
                                break;
                            }
                        }
                    }
                }
                // 失敗補償
                int failCount = changeCount - successCount;
                if (failCount > 0) {
                    // 固定失敗物品
                    giveItemObjs.addAll(craftadditem(pc, pcInv, failCount, npcMakeItemAction.getFailItemList(), npcMakeItemAction.isAmountBroad(), npcMakeItemAction.getSystemMessage()));
                    // 失敗隨機補償（每次失敗擲一次）
                    for (int i = 0; i < failCount; i++) {
                        int r = RandomArrayList.getInt(npcMakeItemAction.getFailRandom());
                        int acc = 0;
                        for (L1ObjectAmount<Integer> msg : npcMakeItemAction.getFailAmountRandomItemList()) {
                            acc += msg.getAmountRandom();
                            if (r < acc) {
                                giveItemObjs.addAll(craftadditem(pc, pcInv, 1, npcMakeItemAction.getFailItemList(), npcMakeItemAction.isAmountBroad(), npcMakeItemAction.getSystemMessage()));
                                break;
                            }
                        }
                    }
                }
                pc.sendPackets(new S_ItemCraftList(successCount > 0, giveItemObjs));
                if (successCount <= 0) {
                    pc.sendPackets(new S_SystemMessage("製作失敗(0/" + changeCount + ")"));
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
            long count1 = maker.getInventory().countItems(80028);
            long count2 = maker.getInventory().countItems(80029);
            _log.info("玩家:" + maker.getName() + " 1:" + count1 + " 2:" + count2 + " 錯誤碼:" + e.getMessage() + "," + Arrays.toString(e.getStackTrace()));
        } finally {
            over();
        }
    }

    public List<L1ItemInstance> craftadditem(L1PcInstance pc, L1PcInventory pcInv, int changeCount, List<L1ObjectAmount<Integer>> amounts, boolean ok, String msg) {
        L1ItemInstance giveItemObj = null;
        List<L1ItemInstance> giveItemObjs = new ArrayList<>();
        for (L1ObjectAmount<Integer> giveItem : amounts) {
            giveItemObj = ItemTable.get().createItem(giveItem.getObject());
            int count = (int) giveItem.getAmount();
            int enchantLevel = giveItem.getAmountEnchantLevel();
            int bless = giveItem.getAmountBless();
            if (giveItemObj != null) {
                if (pcInv.checkAddItem(giveItemObj, count) == 0) {
                    if (giveItemObj.isStackable()) {
                        giveItemObj.setCount((long) count * changeCount);
                        giveItemObj.setIdentified(true);
                        giveItemObj.setBless(bless);
                        if (giveItemObj.getItem().getMaxUseTime() != 0) {
                            giveItemObj.startEquipmentTimer(pc);
                        }
                        pcInv.storeItem(giveItemObj);
                        giveItemObjs.add(giveItemObj);
                    } else {
                        for (int y = 0; y < count * changeCount; y++) {
                            L1ItemInstance itemTemp1 = ItemTable.get().createItem(giveItem.getObject());
                            itemTemp1.setCount(1);
                            itemTemp1.setIdentified(true);
                            itemTemp1.setEnchantLevel(enchantLevel);
                            itemTemp1.setBless(bless);
                            if (itemTemp1.getItem().getMaxUseTime() != 0) {
                                itemTemp1.startEquipmentTimer(pc);
                            }
                            pcInv.storeItem(itemTemp1);
                            giveItemObjs.add(itemTemp1);
                        }
                    }
                    if (ok) {
                        World.get().broadcastPacketToAll(new S_PacketBoxGree(0x02, String.format(msg, pc.getName(), giveItemObj.getLogName())));
                    }
                    pc.sendPackets(new S_ServerMessage(403, giveItemObj.getLogName()));
                } else {
                    pc.sendPackets(new S_SystemMessage("超過可攜帶物品數量,獲取物品[" + giveItemObj.getLogName() + "(" + count + ")]失敗!請截圖反饋至GM!"));
                }
            }
        }
        return giveItemObjs;
    }

    public List<L1ItemInstance> b(final L1PcInstance pc, final L1PcInventory pcInv, final int changeCount, final int itemId, final int amount, final int amountEnchantLevel, int amountBless, boolean ok, String msg) {
        L1ItemInstance giveItemObj;
        final List<L1ItemInstance> giveItemObjs = new ArrayList<>();
        _log.info("itemId:" + itemId + " count:" + amount + " enchantLevel:" + amountEnchantLevel + " bless:" + amountEnchantLevel);
        giveItemObj = ItemTable.get().createItem(itemId);
        if (giveItemObj != null) {
            if (pcInv.checkAddItem(giveItemObj, amount) == 0) {
                if (giveItemObj.isStackable()) {
                    giveItemObj.setCount((long) amount * changeCount);
                    giveItemObj.setIdentified(true);
                    giveItemObj.setBless(amountBless);
                    pcInv.storeTradeItem(giveItemObj);
                    giveItemObjs.add(giveItemObj);
                } else {
                    for (int y = 0; y < (amount * changeCount); y++) {
                        final L1ItemInstance itemTemp1 = ItemTable.get().createItem(itemId);
                        itemTemp1.setCount(1);
                        itemTemp1.setIdentified(true);
                        itemTemp1.setEnchantLevel(amountEnchantLevel);
                        itemTemp1.setBless(amountBless);
                        pcInv.storeTradeItem(itemTemp1);
                        giveItemObjs.add(itemTemp1);
                    }
                }
                if (ok) {
                    World.get().broadcastPacketToAll(new S_PacketBoxGree(0x02, String.format(msg, pc.getName(), giveItemObj.getLogName())));
                }
                pc.sendPackets(new S_ServerMessage(403, giveItemObj.getLogName()));
            } else {
                pc.sendPackets(new S_SystemMessage("超過可攜帶物品數量,獲取物品[" + giveItemObj.getLogName() + "(" + amount + ")]失敗!請截圖反饋至GM!"));
            }
        }
        return giveItemObjs;
    }

    public String jdMethod_else() {
        return "[C] C_ItemCraft";
    }
}