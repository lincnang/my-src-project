package com.lineage.server.serverpackets;

import com.google.protobuf.ByteString;
import com.lineage.config.Config;
import com.lineage.server.datatables.CraftConfigTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.CraftItemForNpc;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.person.*;
import com.lineage.server.templates.L1CraftItem;
import com.lineage.server.templates.L1Item;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;

/**
 * 樂天堂火神製作(DB化)
 */
public class S_NewCreateItem extends ServerBasePacket {
    /**
     * 物品製作
     **/
    public static final int ITEMCRAFTLIST = 0x37; // 55
    /**
     * 物品製作清單
     **/
    public static final int NPCCRAFTLIST = 0x39; // 57
    /**
     * 釣魚
     **/
    public static final int FISH_WINDOW = 0x3F; // 63
    /**
     * 物品製作延遲
     **/
    public static final int CRAFTTIME = 0x5d; // 93
    private static final Log _log = LogFactory.getLog(S_NewCreateItem.class);
    private byte[] _byte;

    public S_NewCreateItem() {
        this._byte = null;
    }

    /**
     * 新封包-釣魚
     *
     * @param type
     * @param i
     * @param ck
     * @param time
     */
    public S_NewCreateItem(int type, int i, boolean ck, long time) {
        writeC(OpcodesServer.S_EXTENDED_PROTOBUF);
        writeH(type);
        switch (type) {
            case FISH_WINDOW:
                //Person.sendFishTime.Builder fish = Person.sendFishTime.newBuilder();
                final Fish_Time.sendFishTime.Builder fish = Fish_Time.sendFishTime.newBuilder();
                if (1 == i) {
                    fish.setType(i);
                    fish.setTime((int) time);
                    fish.setIsQuick(ck ? 2 : 1);
                } else {
                    fish.setType(i);
                }
                writeByte(fish.build().toByteArray());
                break;
            default:
                System.out.println("S_NewCreateItem未知32OP:" + type);
                break;
        }
        writeH(0);
    }

    /**
     * 沒有符合製作條件的清單
     */
    public S_NewCreateItem(final String npcMakeItemActions) {
        writeC(S_EXTENDED_PROTOBUF);
        writeC(NPCCRAFTLIST);
        writeC(0);
        writeC(8);
        writeC(0);
        writeC(24);
        writeC(0);
    }

    public S_NewCreateItem(int type, boolean b, int i) {
        writeC(S_EXTENDED_PROTOBUF);
        writeH(type);
        switch (type) {
            case CRAFTTIME: // 物品製作延遲
                Custom_Packet.sendCustomPacket.Builder custom = Custom_Packet.sendCustomPacket.newBuilder();
                custom.setType(b ? 1 : 0);
                custom.setCustom(i);
                writeByte(custom.build().toByteArray());
                break;
            default:
                System.out.println("S_NewCreateItem未知8OP:" + type);
                break;
        }
        writeH(0);
    }

    public S_NewCreateItem(int type, int subtype) {
        writeC(S_EXTENDED_PROTOBUF);
        writeH(type);
        switch (type) {
            case NPCCRAFTLIST:
                final ItemCraft_NpcId.sendCraftNpcCraftList.Builder npcCraftList = ItemCraft_NpcId.sendCraftNpcCraftList.newBuilder();
                npcCraftList.setUnknown1(0);
                npcCraftList.setUnknown2(0);
                final Map<Integer, CraftItemForNpc> npcMakeItemActionMap = CraftConfigTable.get().readItemList(subtype);
                for (final CraftItemForNpc npc : npcMakeItemActionMap.values()) {
                    final ItemCraft_NpcId.sendCraftNpcCraftList.List.Builder list = ItemCraft_NpcId.sendCraftNpcCraftList.List.newBuilder();
                    list.setActionId(npc.getActionid());
                    list.setUnknown1(0);
                    list.setUnknown2(0);
                    npcCraftList.addList(list);
                }
                this.writeByte(npcCraftList.build().toByteArray());
                break;
            case CRAFTTIME: // 物品製作延遲
                ItemCraft_Type.SendCraftType.Builder sendCraftType = ItemCraft_Type.SendCraftType.newBuilder();
                sendCraftType.setType(0);
                sendCraftType.setTimeIndex(subtype);
                writeByte(sendCraftType.build().toByteArray());
                break;
            case ITEMCRAFTLIST:
                ItemCraft_Other.SendItemCraft.Builder send = ItemCraft_Other.SendItemCraft.newBuilder();
                switch (subtype) {
                    case 0:
                        send.setIsSendPacket(0);
                        writeByte(send.build().toByteArray());
                        break;
                    case 1:
                        send.setIsSendPacket(1);
                        for (CraftItemForNpc l1NpcMakeItemAction : CraftConfigTable.get().get_AllNpcMakeItem().values()) {
                            send.addList(getStream(l1NpcMakeItemAction));
                        }
                        writeByte(send.build().toByteArray());
                        break;
                    case 2:
                        send.setIsSendPacket(2);
                        writeByte(send.build().toByteArray());
                        break;
                    case 3:
                        send.setIsSendPacket(3);
                        writeByte(send.build().toByteArray());
                        break;
                    default:
                        _log.error("未知製作系統TYPE:" + subtype);
                }
                break;
            default:
                System.out.println("S_NewCreateItem未知OP:" + type);
                break;
        }
        writeH(0);
    }

    public S_NewCreateItem(int type, int action, int id) {
        writeC(S_EXTENDED_PROTOBUF);
        writeH(type);
        switch (type) {
            case ITEMCRAFTLIST:
                ItemCraft_Other.SendItemCraft.Builder send = ItemCraft_Other.SendItemCraft.newBuilder();
                send.setIsSendPacket(1);
                CraftItemForNpc l1NpcMakeItemAction = CraftConfigTable.get().get_AllNpcMakeItemForList().get(id);
                send.addList(getStream(l1NpcMakeItemAction));
                writeByte(send.build().toByteArray());
                break;
            default:
                System.out.println("S_NewCreateItem未知7OP:" + type);
                break;
        }
        writeH(0);
    }

    public ItemCraft_Other.SendItemCraftList.Builder getStream(CraftItemForNpc craftAction) {
        ItemCraft_Other.SendItemCraftList.Builder sendItemCraft = ItemCraft_Other.SendItemCraftList.newBuilder();
        sendItemCraft.setActionid(craftAction.getActionid());
        ItemCraft_Other.SendItemCraftList.CraftItemCondition.Builder condition = ItemCraft_Other.SendItemCraftList.CraftItemCondition.newBuilder();
        int makingItemId = craftAction.getItemList().get(0).getItemId();
        L1Item template = ItemTable.get().getTemplate(makingItemId);
        int nameId = 3003;
		/*try {
			nameId = Integer.valueOf(template.getNameId().substring(1));
		} catch (Exception e) {
		}*/
        if (craftAction.getCraftNameID() != 0) {
            nameId = craftAction.getCraftNameID();
        } else {
            try {
                nameId = Integer.valueOf(template.getNameId().substring(1)); // 去除$
            } catch (final Exception e) {
            }
        }
        condition.setNameId(nameId);
        condition.setMinLevel(craftAction.getMinLevel());
        condition.setMaxLevel(craftAction.getMaxLevel());
        condition.setUnknown1(2);
        condition.setMinLawful(craftAction.getMinLawful());
        condition.setMaxLawful(craftAction.getMaxLawful());
        condition.setMinKarma(craftAction.getMinKarma());
        condition.setMaxKarma(craftAction.getMaxKarma());
        condition.setMaxCount(craftAction.getMaxCount());
        condition.setIsShowChance(1);
        sendItemCraft.setCondition(condition);
        sendItemCraft.setUnknown1(0);
        ItemCraft_Other.SendItemCraftList.unQuest.Builder quest = ItemCraft_Other.SendItemCraftList.unQuest.newBuilder();
        quest.setUnknown1(1);
        quest.setUnknown2(0);
        ItemCraft_Other.SendItemCraftList.QuestModel.Builder questModel = ItemCraft_Other.SendItemCraftList.QuestModel.newBuilder();
        questModel.setQuestId(0);
        questModel.setQuestStepId(0);
        quest.setQuest(questModel);
        sendItemCraft.setQuest(quest);
        ItemCraft_Other.SendItemCraftList.PolyModel.Builder polyModel = ItemCraft_Other.SendItemCraftList.PolyModel.newBuilder();
        if (craftAction.getPolyList() != null && craftAction.getPolyList().size() > 0) {
            polyModel.setPcountcount(craftAction.getPolyList().size());
            for (final int polyId : craftAction.getPolyList()) {
                polyModel.addPolyId(polyId);
            }
        } else {
            polyModel.setPcountcount(0);
        }
        sendItemCraft.setPoly(polyModel);
        ItemCraft_Other.SendItemCraftList.unknownModel2.Builder unknown1 = ItemCraft_Other.SendItemCraftList.unknownModel2.newBuilder();
        unknown1.setType(0);
        unknown1.setCount(0);
        sendItemCraft.setUnknown2(unknown1);
        ItemCraft_Other.SendItemCraftList.Material.Builder materialList = ItemCraft_Other.SendItemCraftList.Material.newBuilder();
        Map<Integer, L1CraftItem> materials = craftAction.getMaterialList();
        int i = 1;
        for (L1CraftItem material1 : materials.values()) {
            makingItemId = material1.getItemId();
            template = ItemTable.get().getTemplate(makingItemId);
            if (template == null) {
                _log.error("製作系統-製作材料：" + makingItemId + "不存在");
            } else {
                L1ItemInstance item = new L1ItemInstance();
                item.setItem(template);
                ItemCraft_Other.SendItemCraftList.Material.MaterialModel.Builder material2 = ItemCraft_Other.SendItemCraftList.Material.MaterialModel.newBuilder();
                material2.setItemDescId(template.getItemDescId());
                material2.setCount((int) material1.getCount());
                material2.setWindowNum(i++);
                material2.setEnchantLevel(material1.getEnchantLevel());
                material2.setBless(material1.getBless());
                try {
                    material2.setName(ByteString.copyFrom(item.getLogName().getBytes(Config.CLIENT_LANGUAGE_CODE)));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                material2.setGfxId(item.get_gfxid());
                material2.setUnknow1(0);
                material2.setUnknow2(0);
                material2.setUnknow3(0);
                materialList.addMaterial(material2);
                if ((material1.getSubstituteList() != null) && (material1.getSubstituteList().size() > 0)) {
                    ArrayList<L1CraftItem> list = material1.getSubstituteList();
                    for (L1CraftItem substitute : list) {
                        makingItemId = substitute.getItemId();
                        template = ItemTable.get().getTemplate(makingItemId);
                        if (template == null) {
                            _log.error("製作系統-製作替換材料：" + makingItemId + "不存在");
                        } else {
                            item = new L1ItemInstance();
                            item.setItem(template);
                            material2 = ItemCraft_Other.SendItemCraftList.Material.MaterialModel.newBuilder();
                            material2.setItemDescId(template.getItemDescId());
                            material2.setCount((int) substitute.getCount());
                            material2.setWindowNum(i - 1);
                            material2.setEnchantLevel(substitute.getEnchantLevel());
                            material2.setBless(substitute.getBless());
                            try {
                                material2.setName(ByteString.copyFrom(item.getLogName().getBytes(Config.CLIENT_LANGUAGE_CODE)));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            material2.setGfxId(item.get_gfxid());
                            material2.setUnknow1(0);
                            material2.setUnknow2(0);
                            material2.setUnknow3(0);
                            materialList.addMaterial(material2);
                        }
                    }
                }
            }
        }
        materials = craftAction.getAidMaterialList();
        ArrayList<L1CraftItem> list;
        for (L1CraftItem material1 : materials.values()) {
            makingItemId = material1.getItemId();
            template = ItemTable.get().getTemplate(makingItemId);
            if (template == null) {
                _log.error("製作系統-加成材料：" + makingItemId + "不存在");
            } else {
                L1ItemInstance item = new L1ItemInstance();
                item.setItem(template);
                ItemCraft_Other.SendItemCraftList.Material.MaterialModel.Builder material2 = ItemCraft_Other.SendItemCraftList.Material.MaterialModel.newBuilder();
                material2.setItemDescId(template.getItemDescId());
                material2.setCount((int) material1.getCount());
                material2.setWindowNum(i++);
                material2.setEnchantLevel(material1.getEnchantLevel());
                material2.setBless(material1.getBless());
                try {
                    material2.setName(ByteString.copyFrom(item.getLogName().getBytes(Config.CLIENT_LANGUAGE_CODE)));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                material2.setGfxId(item.get_gfxid());
                material2.setUnknow1(0);
                material2.setUnknow2(0);
                material2.setUnknow3(0);
                materialList.addAddMaterial(material2);
                if ((material1.getSubstituteList() != null) && (material1.getSubstituteList().size() > 0)) {
                    list = material1.getSubstituteList();
                    for (L1CraftItem substitute : list) {
                        makingItemId = substitute.getItemId();
                        template = ItemTable.get().getTemplate(makingItemId);
                        if (template == null) {
                            _log.error("製作系統-加成替換材料：" + makingItemId + "不存在");
                        } else {
                            item = new L1ItemInstance();
                            item.setItem(template);
                            material2 = ItemCraft_Other.SendItemCraftList.Material.MaterialModel.newBuilder();
                            material2.setItemDescId(template.getItemDescId());
                            material2.setCount((int) substitute.getCount());
                            material2.setWindowNum(i - 1);
                            material2.setEnchantLevel(substitute.getEnchantLevel());
                            material2.setBless(substitute.getBless());
                            try {
                                material2.setName(ByteString.copyFrom(item.getLogName().getBytes(Config.CLIENT_LANGUAGE_CODE)));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            material2.setGfxId(item.get_gfxid());
                            material2.setUnknow1(0);
                            material2.setUnknow2(0);
                            material2.setUnknow3(0);
                            materialList.addAddMaterial(material2);
                        }
                    }
                }
            }
        }
        sendItemCraft.setMaterial(materialList);
        ItemCraft_Other.SendItemCraftList.CraftResults.Builder results = ItemCraft_Other.SendItemCraftList.CraftResults.newBuilder();
        ItemCraft_Other.SendItemCraftList.CraftResult.Builder result = ItemCraft_Other.SendItemCraftList.CraftResult.newBuilder();
        ItemCraft_Other.SendItemCraftList.unknownModel1.Builder unknown2 = ItemCraft_Other.SendItemCraftList.unknownModel1.newBuilder();
        unknown2.setUnknow1(0L);
        unknown2.setUnknow2(0L);
        result.setUnknown1(unknown2);
        unknown2 = ItemCraft_Other.SendItemCraftList.unknownModel1.newBuilder();
        unknown2.setUnknow1(4294967295L);
        unknown2.setUnknow2(4294967295L);
        result.setUnknown2(unknown2);
        result.setItemSize(craftAction.getItemList().size());
        result.setItemtype(0);
        if ((craftAction.getItemList() != null) && (craftAction.getItemList().size() > 0)) {
            for (L1CraftItem craftItem : craftAction.getItemList().values()) {
                makingItemId = craftItem.getItemId();
                template = ItemTable.get().getTemplate(makingItemId);
                if (template == null) {
                    _log.error("製作系統-" + makingItemId + "道具不存在！");
                } else {
                    L1ItemInstance item2 = new L1ItemInstance();
                    item2.setItem(template);
                    item2.setEnchantLevel(craftItem.getEnchantLevel());
                    ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder model = ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.newBuilder();
                    model.setItemDescId(template.getItemDescId());
                    model.setCount((int) craftItem.getCount());
                    model.setUnknown1(-1L);
                    model.setEnchantLevel(craftItem.getEnchantLevel());
                    model.setBless(craftItem.getBless());
                    model.setUnknown2(0);
                    model.setUnknown3(0);
                    model.setName(ByteString.copyFrom(item2.getLogName().getBytes()));
                    model.setUnknown4(0);
                    model.setUnknown5(0);
                    model.setGfxId(item2.get_gfxid());
                    model.setSuccedMessage(ByteString.EMPTY);
                    item2.setIdentified(true);
                    item2.getItemId();
                    byte[] data = item2.getStatusBytes();
                    if (data == null) {
                        return null;
                    }
                    model.setItemStatusBytes(ByteString.copyFrom(data));
                    model.setUnknown6(0);
                    model.setUnknown7(0);
                    model.setIsGreatItem(0);
                    if (craftAction.getItemList().size() > 1) {
                        result.addRandomCraftItem(model); // 隨機模式
                    } else {
                        // 大成功
                        if (craftAction.getBigSuccessItemList().size() > 0) {
                            for (L1CraftItem craftbigsuccessItem : craftAction.getBigSuccessItemList().values()) {
                                makingItemId = craftbigsuccessItem.getItemId();
                                template = ItemTable.get().getTemplate(makingItemId);
                                L1ItemInstance bigsuccessitem = new L1ItemInstance();
                                bigsuccessitem.setItem(template);
                                model.setIsGreatItem(1);
                                result.addSingleCraftItem(model);
                                ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem.Builder bigsuccessitemid = ItemCraft_Other.SendItemCraftList.CraftResult.GreatItem.newBuilder();
                                bigsuccessitemid.setUn(1);
                                bigsuccessitemid.setUn1(0);
                                bigsuccessitemid.setUn2(1);
                                model = ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.newBuilder();
                                model.setItemDescId(template.getItemDescId());
                                model.setCount((int) craftbigsuccessItem.getCount());
                                model.setUnknown1(-1L);
                                model.setEnchantLevel(craftbigsuccessItem.getEnchantLevel());
                                model.setBless(craftbigsuccessItem.getBless());
                                model.setUnknown2(0);
                                model.setUnknown3(0);
                                model.setName(ByteString.copyFrom(bigsuccessitem.getLogName().getBytes()));
                                model.setUnknown4(0);
                                model.setUnknown5(0);
                                model.setGfxId(bigsuccessitem.get_gfxid());
                                model.setSuccedMessage(ByteString.EMPTY);
                                bigsuccessitem.setIdentified(true);
                                model.setItemStatusBytes(ByteString.EMPTY);
                                model.setUnknown6(0);
                                model.setUnknown7(0);
                                bigsuccessitemid.setItem(model);
                                result.setGreatItem(bigsuccessitemid);
                            }
                        } else {
                            result.addSingleCraftItem(model);
                        }
                    }
                }
            }
        }
        results.setSucceedList(result);
        result = ItemCraft_Other.SendItemCraftList.CraftResult.newBuilder();
        unknown2 = ItemCraft_Other.SendItemCraftList.unknownModel1.newBuilder();
        unknown2.setUnknow1(0L);
        unknown2.setUnknow2(0L);
        result.setUnknown1(unknown2);
        unknown2 = ItemCraft_Other.SendItemCraftList.unknownModel1.newBuilder();
        unknown2.setUnknow1(4294967295L);
        unknown2.setUnknow2(4294967295L);
        result.setUnknown2(unknown2);
        result.setItemSize(craftAction.getFailItem().size());
        result.setItemtype(craftAction.getFailItem().size() > 0 ? 1 : 0);
        for (L1CraftItem craftItem : craftAction.getFailItem().values()) {
            makingItemId = craftItem.getItemId();
            template = ItemTable.get().getTemplate(makingItemId);
            if (template == null) {
                _log.error("製作系統-" + makingItemId + "道具不存在！");
            } else {
                L1ItemInstance item2 = new L1ItemInstance();
                item2.setItem(template);
                ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.Builder model = ItemCraft_Other.SendItemCraftList.CraftResult.CraftResultModel.newBuilder();
                model.setItemDescId(template.getItemDescId());
                model.setCount((int) craftItem.getCount());
                model.setUnknown1(-1L);
                model.setEnchantLevel(craftItem.getEnchantLevel());
                model.setBless(craftItem.getBless());
                model.setUnknown2(0);
                model.setUnknown3(0);
                model.setName(ByteString.copyFrom(item2.getLogName().getBytes()));
                model.setUnknown4(0);
                model.setUnknown5(0);
                model.setGfxId(item2.get_gfxid());
                model.setSuccedMessage(ByteString.EMPTY);
                model.setItemStatusBytes(ByteString.EMPTY);
                model.setIsGreatItem(0);
                result.addSingleCraftItem(model);
            }
        }
        results.setFailList(result);
        results.setSuccessRatio(craftAction.getSucceedRandom());
        sendItemCraft.setResults(results);
        sendItemCraft.setCraftDelayTime(craftAction.getCraftDelayTime());
        return sendItemCraft;
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = _bao.toByteArray();
        }
        return _byte;
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
