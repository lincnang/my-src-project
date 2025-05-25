package com.lineage.server.clientpackets;

import com.lineage.DatabaseFactory;
import com.lineage.config.ConfigGiveVip;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.IdFactory;
import com.lineage.server.datatables.*;
import com.lineage.server.datatables.lock.ClanReading;
import com.lineage.server.datatables.lock.ServerGmCommandReading;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.datatables.sql.ServerGmCommandTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MerchantInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.*;
import com.lineage.server.serverpackets.S_PledgeUI;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_ShopItemRetrieList;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1ItemUpgrade;
import com.lineage.server.templates.L1Refine;
import com.lineage.server.templates.T_GameMallModel;
import com.lineage.server.templates.T_ShopWarehouseModel;
import com.lineage.server.utils.RandomArrayList;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import william.server_lv;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class C_PledgeContent extends ClientBasePacket {
    private static final Log _log = LogFactory.getLog(C_PledgeContent.class);

    @Override
    public void start(final byte[] decrypt, final ClientExecutor client) {
        // 資料載入
        read(decrypt);
        final L1PcInstance pc = client.getActiveChar();
        final int data = readC();
        switch (data) {
            case 7:
                pc.sendPackets(T_GameMallTable.get().getPaketList());
                pc.updateGameMallMoney();
                break;
            case 8:
                final int un = readD();
                if (un == 1) {
                    final int id = readD();
                    readD();
                    int buyCount = readD();
                    int sumPrice = readD();
                    readD();
                    final int buyType = readC();
                    if (buyType == 0) {
                        final T_GameMallModel model = T_GameMallTable.get().getMallList(id);
                        if (model == null) {
                            pc.sendPackets(new S_ServerMessage(2746));
                            return;
                        }
                        if ((model.getVipLevel() > 0) && (pc.get_vipLevel() <= 0)) {
                            pc.sendPackets(new S_ServerMessage(2743));
                            return;
                        }
                        buyCount = Math.min(Math.abs(buyCount), 100);
                        sumPrice = buyCount * model.getPrice();
                        L1ItemInstance itemCheck = pc.getInventory().findItemId(44070);
                        if (!pc.getInventory().consumeItem(44070, sumPrice)) {
                            pc.sendPackets(new S_ServerMessage(2742));
                            return;
                        }
                        boolean tradeControl = false;
                        if (itemCheck != null && ServerGmCommandTable.tradeControl.contains(itemCheck.getId())) {// 限制轉移物品
                            tradeControl = true;
                        }
                        final L1DwarfForGameMallInventry dwarfForGameMallInventory = pc.getDwarfForGameMall();
                        synchronized (dwarfForGameMallInventory._key) {
                            final L1ItemInstance itemTemp = model.getMallItem();
                            if (itemTemp.isStackable()) {
                                final T_ShopWarehouseModel shopWarehouseModel = new T_ShopWarehouseModel(IdFactory.get().nextId(), pc.getAccountName(), pc.getId(), itemTemp.getItemId(), itemTemp.getItem().getName(), (int) (itemTemp.getCount() * buyCount), itemTemp.getBless(), itemTemp.getEnchantLevel());
                                pc.getDwarfForGameMall().insertOnLine(shopWarehouseModel);
                                if (tradeControl) {
                                    ServerGmCommandReading.get().createTradeControl(shopWarehouseModel.getId(), itemCheck.getId() + "商城購物");
                                }
                            } else {
                                T_ShopWarehouseModel shopWarehouseModel = null;
                                for (int i = 0; i < buyCount; i++) {
                                    shopWarehouseModel = new T_ShopWarehouseModel(IdFactory.get().nextId(), pc.getAccountName(), pc.getId(), itemTemp.getItemId(), itemTemp.getItem().getName(), (int) itemTemp.getCount(), itemTemp.getBless(), itemTemp.getEnchantLevel());
                                    pc.getDwarfForGameMall().insertOnLine(shopWarehouseModel);
                                    if (tradeControl) {
                                        ServerGmCommandReading.get().createTradeControl(shopWarehouseModel.getId(), itemCheck.getId() + "商城購物");
                                    }
                                }
                            }
                            pc.sendPackets(new S_ServerMessage(2745));
                            pc.updateGameMallMoney();
                            T_GameMallTable.get().insertMallRecord(pc.getId(), pc.getId(), itemTemp.getItemId(), itemTemp.getItem().getName(), buyCount, sumPrice);
                            if (ConfigGiveVip.Give_Vip) { // 消費總額，判斷VIP等級
                                getGiveVip(pc);
                            }
                        }
                    } else if (buyType == 1) {
                        final String friendName = readS();
                        boolean friendIsOnline = true;
                        final T_GameMallModel model = T_GameMallTable.get().getMallList(id);
                        if (model == null) {
                            pc.sendPackets(new S_ServerMessage(2746));
                            return;
                        }
                        final L1ItemInstance itemControl = model.getMallItem();
                        if (itemControl.getItemId() == 56152) {
                            pc.sendPackets(new S_ServerMessage("戰神之魂無法贈送給其他玩家"));
                            return;
                        }
                        if ((model.getVipLevel() > 0) && (pc.get_vipLevel() <= 0)) {
                            pc.sendPackets(new S_ServerMessage(2743));
                            return;
                        }
                        L1ItemInstance itemCheck = pc.getInventory().findItemId(44070);
                        if (ServerGmCommandTable.tradeControl.contains(itemCheck.getId())) {// 限制轉移物品
                            pc.sendPackets(new S_ServerMessage("獎勵物品無法轉移"));
                            return;
                        }
                        buyCount = Math.min(Math.abs(buyCount), 100);
                        sumPrice = buyCount * model.getPrice();
                        if (!pc.getInventory().consumeItem(44070, sumPrice)) {
                            pc.sendPackets(new S_ServerMessage(2742));
                            return;
                        }
                        L1PcInstance friend = World.get().getPlayer(friendName);
                        L1DwarfForGameMallInventry dwarfForGameMallInventory = null;
                        if (friend == null) {
                            try {
                                friend = CharacterTable.get().restoreCharacter(friendName);
                            } catch (final Exception e) {
                                e.printStackTrace();
                            }
                            friendIsOnline = false;
                            dwarfForGameMallInventory = new L1DwarfForGameMallInventry(null);
                        } else {
                            dwarfForGameMallInventory = friend.getDwarfForGameMall();
                        }
                        synchronized (dwarfForGameMallInventory._key) {
                            final L1ItemInstance itemTemp = model.getMallItem();
                            if (itemTemp.isStackable()) {
                                final T_ShopWarehouseModel shopWarehouseModel = new T_ShopWarehouseModel(IdFactory.get().nextId(), friend.getAccountName(), pc.getId(), itemTemp.getItemId(), itemTemp.getItem().getName(), (int) (itemTemp.getCount() * buyCount), itemTemp.getBless(), itemTemp.getEnchantLevel());
                                if (friendIsOnline) {
                                    dwarfForGameMallInventory.insertOnLine(shopWarehouseModel);
                                } else {
                                    dwarfForGameMallInventory.insertOffLine(shopWarehouseModel);
                                }
                            } else {
                                T_ShopWarehouseModel shopWarehouseModel = null;
                                for (int i = 0; i < buyCount; i++) {
                                    shopWarehouseModel = new T_ShopWarehouseModel(IdFactory.get().nextId(), friend.getAccountName(), pc.getId(), itemTemp.getItemId(), itemTemp.getItem().getName(), (int) itemTemp.getCount(), itemTemp.getBless(), itemTemp.getEnchantLevel());
                                    if (friendIsOnline) {
                                        dwarfForGameMallInventory.insertOnLine(shopWarehouseModel);
                                    } else {
                                        dwarfForGameMallInventory.insertOffLine(shopWarehouseModel);
                                    }
                                }
                            }
                            pc.sendPackets(new S_ServerMessage(2745));
                            pc.updateGameMallMoney();
                            T_GameMallTable.get().insertMallRecord(pc.getId(), friend.getId(), itemTemp.getItemId(), itemTemp.getItem().getName(), buyCount, sumPrice);
                            if (ConfigGiveVip.Give_Vip) { // 消費總額，判斷VIP等級
                                getGiveVip(pc);
                            }
                        }
                    } else if (buyType == 2) {
                        final T_GameMallModel model = T_GameMallTable.get().getMallList(id);
                        if (model == null) {
                            pc.sendPackets(new S_ServerMessage(2746));
                            return;
                        }
                        readS();
                    } else if (buyType == 3) {
                        readS();
                    }
                }
                break;
            case 9:
                pc.sendPackets(new S_ShopItemRetrieList(pc));
                break;
            case 10:
                final int size = readD();
                if (size > 0) {
                    final L1DwarfForGameMallInventry dwarfForGameMallInventory = pc.getDwarfForGameMall();
                    synchronized (dwarfForGameMallInventory._key) {
                        final List<T_ShopWarehouseModel> items = dwarfForGameMallInventory.getWareHouseList();
                        final List<T_ShopWarehouseModel> giveItems = new ArrayList<T_ShopWarehouseModel>(size);
                        final int[] allIndex = new int[size];
                        int index = -1;
                        for (int i = 0; i < size; i++) {
                            index = readD();
                            readD();
                            if (index >= items.size()) {
                                giveItems.clear();
                                return;
                            }
                            allIndex[i] = index;
                            giveItems.add(items.get(index));
                        }
                        L1ItemInstance item = null;
                        final L1PcInventory pcInv = pc.getInventory();
                        for (final T_ShopWarehouseModel model : giveItems) {
                            if (pcInv.checkAddItem(model.getItemId(), model.getItemCount()) == 0) {
                                item = ItemTable.get().createItem(model.getItemId());
                                item.setCount(model.getItemCount());
                                item.setEnchantLevel(model.getEnchantLevel());
                                item.setBless(model.getItemBless());
                                server_lv.forIntensifyArmor(pc, item); //SRC0719
                                pcInv.storeItem(item);
                                dwarfForGameMallInventory.deleteItems(model);
                            }
                        }
                    }
                }
                break;
            case 13:// 火神精煉
                // System.out.println(this.getClass().getSimpleName() + ":type=13");
                int objId = readD();
                int refineItemId = readD();
                int assistItemId = readD();
                L1Object obj = World.get().findObject(objId);
                if ((obj != null) && ((obj instanceof L1MerchantInstance))) {
                    L1MerchantInstance npc = (L1MerchantInstance) obj;
                    if ((npc.getNpcId() == 80626) || (npc.getNpcId() == 80625)) {
                        L1ItemInstance refineItem = pc.getInventory().getItem(refineItemId);
                        if (refineItem == null) {
                            return;
                        }
                        L1Refine refine = RefineTable.getInstance().getRefine(refineItem.getItemId(), refineItem.getEnchantLevel());
                        if ((refine != null) && (pc.getInventory().removeItem(refineItem, 1) > 0)) {
                            int newItemCount = refine.getNewItemCount();
                            if (assistItemId > 0) {
                                L1ItemInstance assistItem = pc.getInventory().getItem(assistItemId);
                                if ((assistItem != null) && (assistItem.getItemId() == 80058) && (pc.getInventory().consumeItem(assistItem.getItemId(), 1))) {
                                    newItemCount = newItemCount * 120 / 100;
                                } else {
                                    pc.sendPackets(new S_SystemMessage("無效的輔助道具。"));
                                }
                            }
                            L1ItemInstance newItem = ItemTable.get().createItem(refine.getNewItemId());
                            if ((newItem != null) && (pc.getInventory().checkAddItem(newItem, newItemCount) == 0)) {
                                newItem.setCount(newItemCount);
                                pc.getInventory().storeItem(newItem);
                                pc.sendPackets(new S_SystemMessage("獲得" + newItemCount + "個火神結晶體。"));
                            }
                        }
                    }
                }
                break;
            case 14:// 火神物品合成
                // System.out.println(this.getClass().getSimpleName() + ":type=14");
                objId = readD();
                int actionId = readH();
                assistItemId = readD();
                obj = World.get().findObject(objId);
                if ((obj != null) && ((obj instanceof L1MerchantInstance))) {
                    L1MerchantInstance npc = (L1MerchantInstance) obj;
                    if ((npc.getNpcId() == 80626) || (npc.getNpcId() == 80625)) {//火神工匠
                        L1ItemUpgrade itemUpgrade = ItemUpgradeTable.getInstance().getItemUpgrade(npc.getNpcId(), String.valueOf(actionId));
                        if (itemUpgrade != null) {
                            List<?> neededItemIdList = itemUpgrade.getNeedItemIdList();
                            List<?> neededCountsList = itemUpgrade.getNeedCountsList();
                            for (int i = 0; i < neededItemIdList.size(); i++) {
                                if (!pc.getInventory().checkItem(((Integer) neededItemIdList.get(i)).intValue(), ((Integer) neededCountsList.get(i)).intValue())) {
                                    pc.sendPackets(new S_SystemMessage("裝備製作材料不足。"));
                                    return;
                                }
                            }
                            for (int i = 0; i < neededItemIdList.size(); i++) {
                                if (!pc.getInventory().consumeItem(((Integer) neededItemIdList.get(i)).intValue(), ((Integer) neededCountsList.get(i)).intValue())) {
                                    pc.sendPackets(new S_SystemMessage("裝備製作失敗。"));
                                    return;
                                }
                            }
                            int upgradeChance = itemUpgrade.getUpgradeChance();
                            if (assistItemId > 0) {
                                int assistItemCount = readH();
                                L1ItemInstance assistItem = pc.getInventory().getItem(assistItemId);
                                if ((assistItem != null) && (assistItem.getItemId() == 80027) && (assistItemCount <= 10) && (pc.getInventory().consumeItem(assistItem.getItemId(), assistItemCount))) {
                                    upgradeChance += assistItemCount;
                                } else {
                                    pc.sendPackets(new S_SystemMessage("無效的輔助道具。"));
                                }
                            }
                            if (RandomArrayList.getInc(100, 1) < upgradeChance) {
                                L1ItemInstance newItem = ItemTable.get().createItem(itemUpgrade.getNewItemId());
                                if (newItem != null) {
                                    if (pc.getInventory().checkAddItem(newItem, 1) == 0) {
                                        newItem.setCount(1);
                                        pc.getInventory().storeItem(newItem);
                                        pc.sendPackets(new S_SystemMessage("裝備製作成功。"));
                                    }
                                } else {
                                    pc.sendPackets(new S_SystemMessage("裝備製作失敗。"));
                                }
                            } else {
                                pc.sendPackets(new S_SystemMessage("裝備製作失敗。"));
                            }
                        }
                    }
                }
                break;
            case 15:// 寫入血盟公告
                // 讀取公告字串封包
                if (pc.getClanid() == 0) {
                    return;
                }
                if (pc.getClan().getLeaderId() != pc.getId()) {
                    return;
                }
                String announce = readS();//讀取公告字串封包
                final L1Clan clan = pc.getClan();
                clan.setClanShowNote(announce);
                ClanReading.get().updateClan(clan);//更新L1Clan物件
                pc.sendPackets(new S_PledgeUI(clan, S_PledgeUI.ES_PLEDGE_NOTI));
                break;
            case 16:// 寫入個人備註
                if (pc.getClanid() == 0) {
                    return;
                }
                // 讀取備註字串封包
                String notes = readS();
                /* 更新角色備註資料 */
                pc.setClanMemberNotes(notes);
                /* 寫入備註資料到資料庫 */
                ClanMembersTable.getInstance().updateMemberNotes(pc, notes);
                /* 送出寫入備註更新封包 */
                pc.sendPackets(new S_PledgeUI(pc));// 7.6
                break;
            default:
                break;
        }
    }

    /**
     * 消費總額，判斷VIP等級
     */
    private void getGiveVip(L1PcInstance pc) {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT sum(購買總價) as price FROM character_潘朵拉商城 where 道具objid=?");
            ps.setInt(1, pc.getId());
            rs = ps.executeQuery();
            while (rs.next()) {
                L1PcQuest quest = pc.getQuest();
                int questStep1 = quest.get_step(L1PcQuest.QUEST_GIVE_VIP_1);
                int questStep2 = quest.get_step(L1PcQuest.QUEST_GIVE_VIP_2);
                int questStep3 = quest.get_step(L1PcQuest.QUEST_GIVE_VIP_3);
                int questStep4 = quest.get_step(L1PcQuest.QUEST_GIVE_VIP_4);
                int questStep5 = quest.get_step(L1PcQuest.QUEST_GIVE_VIP_5);
                if (rs.getInt("price") >= ConfigGiveVip.Give_Vip_Price_1 && rs.getInt("price") < ConfigGiveVip.Give_Vip_Price_2 && questStep1 != L1PcQuest.QUEST_END && pc.get_vipLevel() < ConfigGiveVip.Give_Vip_Level_1) { // (第1組)
                    pc.addVipStatus(ConfigGiveVip.Give_Vip_Day_1, ConfigGiveVip.Give_Vip_Level_1); // VIP時間 VIP等級
                    pc.getQuest().set_step(L1PcQuest.QUEST_GIVE_VIP_1, 255);
                    pc.sendPackets(new S_SystemMessage("\\aE恭喜消費滿(" + ConfigGiveVip.Give_Vip_Price_1 + "),贈送VIP(" + ConfigGiveVip.Give_Vip_Level_1 + ")級,(" + ConfigGiveVip.Give_Vip_Day_1 + ")天,請重新登入"));
                    Save_Vip(pc); // 儲存VIP狀態
                } else if (rs.getInt("price") >= ConfigGiveVip.Give_Vip_Price_2 && rs.getInt("price") < ConfigGiveVip.Give_Vip_Price_3 && questStep2 != L1PcQuest.QUEST_END && pc.get_vipLevel() < ConfigGiveVip.Give_Vip_Level_2) { // (第2組)
                    pc.addVipStatus(ConfigGiveVip.Give_Vip_Day_2, ConfigGiveVip.Give_Vip_Level_2); // VIP時間 VIP等級
                    pc.getQuest().set_step(L1PcQuest.QUEST_GIVE_VIP_2, 255);
                    pc.sendPackets(new S_SystemMessage("\\aE恭喜消費滿(" + ConfigGiveVip.Give_Vip_Price_2 + "),贈送VIP(" + ConfigGiveVip.Give_Vip_Level_2 + ")級,(" + ConfigGiveVip.Give_Vip_Day_2 + ")天,請重新登入"));
                    Save_Vip(pc); // 儲存VIP狀態
                } else if (rs.getInt("price") >= ConfigGiveVip.Give_Vip_Price_3 && rs.getInt("price") < ConfigGiveVip.Give_Vip_Price_4 && questStep3 != L1PcQuest.QUEST_END && pc.get_vipLevel() < ConfigGiveVip.Give_Vip_Level_3) { // (第3組)
                    pc.addVipStatus(ConfigGiveVip.Give_Vip_Day_3, ConfigGiveVip.Give_Vip_Level_3); // VIP時間 VIP等級
                    pc.getQuest().set_step(L1PcQuest.QUEST_GIVE_VIP_3, 255);
                    pc.sendPackets(new S_SystemMessage("\\aE恭喜消費滿(" + ConfigGiveVip.Give_Vip_Price_3 + "),贈送VIP(" + ConfigGiveVip.Give_Vip_Level_3 + ")級,(" + ConfigGiveVip.Give_Vip_Day_3 + ")天,請重新登入"));
                    Save_Vip(pc); // 儲存VIP狀態
                } else if (rs.getInt("price") >= ConfigGiveVip.Give_Vip_Price_4 && rs.getInt("price") < ConfigGiveVip.Give_Vip_Price_5 && questStep4 != L1PcQuest.QUEST_END && pc.get_vipLevel() < ConfigGiveVip.Give_Vip_Level_4) { // (第4組)
                    pc.addVipStatus(ConfigGiveVip.Give_Vip_Day_4, ConfigGiveVip.Give_Vip_Level_4); // VIP時間 VIP等級
                    pc.getQuest().set_step(L1PcQuest.QUEST_GIVE_VIP_4, 255);
                    pc.sendPackets(new S_SystemMessage("\\aE恭喜消費滿(" + ConfigGiveVip.Give_Vip_Price_4 + "),贈送VIP(" + ConfigGiveVip.Give_Vip_Level_4 + ")級,(" + ConfigGiveVip.Give_Vip_Day_4 + ")天,請重新登入"));
                    Save_Vip(pc); // (第4組)儲存VIP狀態
                } else if (rs.getInt("price") >= ConfigGiveVip.Give_Vip_Price_5 && questStep5 != L1PcQuest.QUEST_END && pc.get_vipLevel() < ConfigGiveVip.Give_Vip_Level_5) { // (第5組)
                    pc.addVipStatus(ConfigGiveVip.Give_Vip_Day_5, ConfigGiveVip.Give_Vip_Level_5); // VIP時間 VIP等級
                    pc.getQuest().set_step(L1PcQuest.QUEST_GIVE_VIP_5, 255);
                    pc.sendPackets(new S_SystemMessage("\\aE恭喜消費滿(" + ConfigGiveVip.Give_Vip_Price_5 + "),贈送VIP(" + ConfigGiveVip.Give_Vip_Level_5 + ")級,(" + ConfigGiveVip.Give_Vip_Day_5 + ")天,請重新登入"));
                    Save_Vip(pc); // 儲存VIP狀態
                }
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    /**
     * 儲存VIP狀態
     */
    private void Save_Vip(final L1PcInstance pc) {
        try {
            pc.saveVip();
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
