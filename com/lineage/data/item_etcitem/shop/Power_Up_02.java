package com.lineage.data.item_etcitem.shop;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.datatables.CharItemPowerTable;
import com.lineage.server.datatables.ItemPowerUpdateTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.CharItemBlessReading;
import com.lineage.server.datatables.lock.CharItemPowerReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1ItemPowerUpdate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.Random;

/**
 * 附魔石類型
 */
public class Power_Up_02 extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(Power_Up_02.class);
    private static final Random _random = new Random();

    /**
     *
     */
    private Power_Up_02() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Power_Up_02();
    }

    /**
     * 道具物件執行
     *
     * @param data 參數
     * @param pc   執行者
     * @param item 物件
     */
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        try {
            // 對像OBJID
            final int targObjId = data[0];
            final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
            if (tgItem == null) {
                return;
            }
            // 取回物件屬性
            final String key = tgItem.getItemId() + "/" + item.getItemId();
            final L1ItemPowerUpdate info = ItemPowerUpdateTable.get().get(key);
            if (info == null) {
                // 79：\f1沒有任何事情發生。
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }
            if (tgItem.isEquipped()) {
                pc.sendPackets(new S_ServerMessage("(預防誤點機制啟動)裝備中無法強化"));// 沒有任何事發生
                return;
            }
            if (info.get_mode() == 5) {// 不能再強化
                // 79：\f1沒有任何事情發生。
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }
            if (info.get_nedid() != item.getItemId()) {
                // 79：\f1沒有任何事情發生。
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }
            // 同組物品清單
            final Map<Integer, L1ItemPowerUpdate> tmplist = ItemPowerUpdateTable.get().get_type_id(key);
            if (tmplist.isEmpty()) {
                // 79：\f1沒有任何事情發生。
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }
            final int order_id = info.get_order_id();// 排序
            final L1ItemPowerUpdate tginfo = tmplist.get(order_id + 1);// 取回下一個排序資料
            if (tginfo == null) {
                // 79：\f1沒有任何事情發生。
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }
            // 刪除卷軸
            pc.getInventory().removeItem(item, 1);
            if (_random.nextInt(1000) <= tginfo.get_random()) {
                // 強化成功
                pc.getInventory().removeItem(tgItem, 1);
                // CreateNewItem.createNewItem(pc, tginfo.get_itemid(), 1);
                // 產生新物件
                final L1ItemInstance tginfo_item = ItemTable.get().createItem(tginfo.get_itemid());
                if (tginfo_item != null) {
                    tginfo_item.setIdentified(true);
                    tginfo_item.setCount(1);
                    // 保留數值
                    tginfo_item.setEnchantLevel(tgItem.getEnchantLevel());
                    // tginfo_item.setIdentified(tgItem.isIdentified());
                    tginfo_item.setIdentified(false); // 防止訊息太多掉線 設置為未鑒定狀態
                    tginfo_item.set_durability(tgItem.get_durability());
                    tginfo_item.setChargeCount(tgItem.getChargeCount());
                    tginfo_item.setRemainingTime(tgItem.getRemainingTime());
                    tginfo_item.setLastUsed(tgItem.getLastUsed());
                    tginfo_item.setAttrEnchantKind(tgItem.getAttrEnchantKind());
                    tginfo_item.setAttrEnchantLevel(tgItem.getAttrEnchantLevel());
                    tginfo_item.setItemAttack(tgItem.getItemAttack());
                    tginfo_item.setItemBowAttack(tgItem.getItemBowAttack());
                    tginfo_item.setItemReductionDmg(tgItem.getItemReductionDmg());
                    tginfo_item.setItemSp(tgItem.getItemSp());
                    tginfo_item.setItemprobability(tgItem.getItemprobability());
                    tginfo_item.setItemStr(tgItem.getItemStr());
                    tginfo_item.setItemDex(tgItem.getItemDex());
                    tginfo_item.setItemInt(tgItem.getItemInt());
                    // 強化擴充能力
                    if (CharItemPowerTable.get().getPower(tgItem) != null) {
                        tginfo_item.setUpdateStr(tgItem.getUpdateStr());
                        tginfo_item.setUpdateDex(tgItem.getUpdateDex());
                        tginfo_item.setUpdateCon(tgItem.getUpdateCon());
                        tginfo_item.setUpdateWis(tgItem.getUpdateWis());
                        tginfo_item.setUpdateInt(tgItem.getUpdateInt());
                        tginfo_item.setUpdateCha(tgItem.getUpdateCha());
                        tginfo_item.setUpdateHp(tgItem.getUpdateHp());
                        tginfo_item.setUpdateMp(tgItem.getUpdateMp());
                        tginfo_item.setUpdateEarth(tgItem.getUpdateEarth());
                        tginfo_item.setUpdateWind(tgItem.getUpdateWind());
                        tginfo_item.setUpdateWater(tgItem.getUpdateWater());
                        tginfo_item.setUpdateFire(tgItem.getUpdateFire());
                        tginfo_item.setUpdateMr(tgItem.getUpdateMr());
                        tginfo_item.setUpdateAc(tgItem.getUpdateAc());
                        tginfo_item.setUpdateHpr(tgItem.getUpdateHpr());
                        tginfo_item.setUpdateMpr(tgItem.getUpdateMpr());
                        tginfo_item.setUpdateSp(tgItem.getUpdateSp());
                        tginfo_item.setUpdateDmgModifier(tgItem.getUpdateDmgModifier());
                        tginfo_item.setUpdateHitModifier(tgItem.getUpdateHitModifier());
                        tginfo_item.setUpdateBowDmgModifier(tgItem.getUpdateBowDmgModifier());
                        tginfo_item.setUpdateBowHitModifier(tgItem.getUpdateBowHitModifier());
                        tginfo_item.setUpdatePVPdmg(tgItem.getUpdatePVPdmg());
                        tginfo_item.setUpdatePVPdmg_R(tgItem.getUpdatePVPdmg_R());
                        if (CharItemPowerTable.get().getPower(tginfo_item) == null) {
                            CharItemPowerTable.get().storeItem(tginfo_item);
                            CharItemPowerTable.get().deleteItemUpdate(tgItem.getId());
                        } else {
                            CharItemPowerTable.get().updateItem(tginfo_item);
                            CharItemPowerTable.get().deleteItemUpdate(tgItem.getId());
                        }
                    }
                    // 強化擴充能力 end
                    if (tgItem.get_power_name() != null) {
                        tginfo_item.set_power_name(tgItem.get_power_name());
                        // 新建資料
                        CharItemPowerReading.get().storeItem(tginfo_item.getId(), tginfo_item.get_power_name());
                        // 刪除資料
                        CharItemPowerReading.get().delItem(tgItem.getId());
                    }
                    if (tgItem.get_power_bless() != null) {
                        tginfo_item.set_power_bless(tgItem.get_power_bless());
                        // 新建資料
                        CharItemBlessReading.get().storeItem(tginfo_item.getId(), tginfo_item.get_power_bless());
                        // 刪除資料
                        CharItemBlessReading.get().delItem(tgItem.getId());
                    }
                    pc.getInventory().storeItem(tginfo_item);
                    tginfo_item.setBless(tgItem.getBless());
                    pc.getInventory().updateItem(tginfo_item, L1PcInventory.COL_BLESS);
                    // 對\f1%0附加強大的魔法力量成功。
                    pc.sendPackets(new S_ServerMessage(1410, tgItem.getName()));
                    WriteLogTxt.Recording("道具升級紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【 " + pc.getName() + " 】的【" + tgItem.getRecordName(tgItem.getCount()) + "(ObjectId: " + tgItem.getId() + ")," + "】升級成功至" + tginfo_item.getRecordName(tginfo_item.getCount()) + "(ObjectId: " + tginfo_item.getId() + ").");
                    return;
                } else {
                    _log.error("給予物件失敗 原因: 指定編號物品不存在(" + tginfo.get_itemid() + ")");
                    return;
                }
            } else {
                // 強化失敗
                switch (info.get_mode()) {
                    case 0:
                        pc.sendPackets(new S_ServerMessage(160, tgItem.getLogName(), "$252", "$248"));
                        WriteLogTxt.Recording("道具升級紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【 " + pc.getName() + " 】的【" + tgItem.getRecordName(tgItem.getCount()) + "】升級失敗，沒任何事情發生。 (ObjectId: " + tgItem.getId() + ").");
                        break;
                    case 1:
                        L1ItemPowerUpdate ole1 = (L1ItemPowerUpdate) tmplist.get(order_id - 1);
                        pc.sendPackets(new S_ServerMessage("\\fR" + tgItem.getName() + "升級失敗!"));
                        pc.getInventory().removeItem(tgItem, 1L);
                        // CreateNewItem.createNewItem(pc, ole1.get_itemid(), 1L);
                        L1ItemInstance tginfo_item2 = ItemTable.get().createItem(ole1.get_itemid());
                        if (tginfo_item2 != null) {
                            tginfo_item2.setCount(1L);
                            tginfo_item2.setEnchantLevel(tgItem.getEnchantLevel());
                            // tginfo_item2.setIdentified(tgItem.isIdentified());
                            tginfo_item2.setIdentified(false); // 防止訊息太多掉線 設置為未鑒定狀態
                            tginfo_item2.set_durability(tgItem.get_durability());
                            tginfo_item2.setChargeCount(tgItem.getChargeCount());
                            tginfo_item2.setRemainingTime(tgItem.getRemainingTime());
                            tginfo_item2.setLastUsed(tgItem.getLastUsed());
                            tginfo_item2.setBless(tgItem.getBless());
                            tginfo_item2.setAttrEnchantKind(tgItem.getAttrEnchantKind());
                            tginfo_item2.setAttrEnchantLevel(tgItem.getAttrEnchantLevel());
                            tginfo_item2.setItemAttack(tgItem.getItemAttack());
                            tginfo_item2.setItemBowAttack(tgItem.getItemBowAttack());
                            tginfo_item2.setItemReductionDmg(tgItem.getItemReductionDmg());
                            tginfo_item2.setItemSp(tgItem.getItemSp());
                            tginfo_item2.setItemprobability(tgItem.getItemprobability());
                            tginfo_item2.setItemStr(tgItem.getItemStr());
                            tginfo_item2.setItemDex(tgItem.getItemDex());
                            tginfo_item2.setItemInt(tgItem.getItemInt());
                            // 強化擴充能力
                            if (CharItemPowerTable.get().getPower(tgItem) != null) {
                                tginfo_item2.setUpdateStr(tgItem.getUpdateStr());
                                tginfo_item2.setUpdateDex(tgItem.getUpdateDex());
                                tginfo_item2.setUpdateCon(tgItem.getUpdateCon());
                                tginfo_item2.setUpdateWis(tgItem.getUpdateWis());
                                tginfo_item2.setUpdateInt(tgItem.getUpdateInt());
                                tginfo_item2.setUpdateCha(tgItem.getUpdateCha());
                                tginfo_item2.setUpdateHp(tgItem.getUpdateHp());
                                tginfo_item2.setUpdateMp(tgItem.getUpdateMp());
                                tginfo_item2.setUpdateEarth(tgItem.getUpdateEarth());
                                tginfo_item2.setUpdateWind(tgItem.getUpdateWind());
                                tginfo_item2.setUpdateWater(tgItem.getUpdateWater());
                                tginfo_item2.setUpdateFire(tgItem.getUpdateFire());
                                tginfo_item2.setUpdateMr(tgItem.getUpdateMr());
                                tginfo_item2.setUpdateAc(tgItem.getUpdateAc());
                                tginfo_item2.setUpdateHpr(tgItem.getUpdateHpr());
                                tginfo_item2.setUpdateMpr(tgItem.getUpdateMpr());
                                tginfo_item2.setUpdateSp(tgItem.getUpdateSp());
                                tginfo_item2.setUpdateDmgModifier(tgItem.getUpdateDmgModifier());
                                tginfo_item2.setUpdateHitModifier(tgItem.getUpdateHitModifier());
                                tginfo_item2.setUpdateBowDmgModifier(tgItem.getUpdateBowDmgModifier());
                                tginfo_item2.setUpdateBowHitModifier(tgItem.getUpdateBowHitModifier());
                                tginfo_item2.setUpdatePVPdmg(tgItem.getUpdatePVPdmg());
                                tginfo_item2.setUpdatePVPdmg_R(tgItem.getUpdatePVPdmg_R());
                                if (CharItemPowerTable.get().getPower(tginfo_item2) == null) {
                                    CharItemPowerTable.get().storeItem(tginfo_item2);
                                    CharItemPowerTable.get().deleteItemUpdate(tgItem.getId());
                                } else {
                                    CharItemPowerTable.get().updateItem(tginfo_item2);
                                    CharItemPowerTable.get().deleteItemUpdate(tgItem.getId());
                                }
                            }
                            // 強化擴充能力 end
                            if (tgItem.get_power_name() != null) {
                                tginfo_item2.set_power_name(tgItem.get_power_name());
                                CharItemPowerReading.get().storeItem(tginfo_item2.getId(), tginfo_item2.get_power_name());
                                CharItemPowerReading.get().delItem(tgItem.getId());
                            }
                            if (tgItem.get_power_bless() != null) {
                                tginfo_item2.set_power_bless(tgItem.get_power_bless());
                                CharItemBlessReading.get().storeItem(tginfo_item2.getId(), tginfo_item2.get_power_bless());
                                CharItemBlessReading.get().delItem(tgItem.getId());
                            }
                            pc.getInventory().storeItem(tginfo_item2);
                            tginfo_item2.setBless(tgItem.getBless());
                            pc.getInventory().updateItem(tginfo_item2, L1PcInventory.COL_BLESS);
                            WriteLogTxt.Recording("道具升級紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【 " + pc.getName() + " 】的【" + tgItem.getRecordName(tgItem.getCount()) + "】升級失敗，退階了。 (ObjectId: " + tgItem.getId() + ").");
                        } else {
                            _log.error("給予物件失敗 原因: 指定編號物品不存在(" + ole1.get_itemid() + ")");
                        }
                        break;
                    case 2:
                        pc.sendPackets(new S_ServerMessage(164, tgItem.getLogName(), "$252"));
                        pc.getInventory().removeItem(tgItem, 1L);
                        WriteLogTxt.Recording("道具升級紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【 " + pc.getName() + " 】的【" + tgItem.getRecordName(tgItem.getCount()) + "】升級失敗，道具消失了。 (ObjectId: " + tgItem.getId() + ").");
                        break;
                    case 3:
                        if (_random.nextBoolean()) {
                            L1ItemPowerUpdate ole2 = (L1ItemPowerUpdate) tmplist.get(order_id - 1);
                            pc.getInventory().removeItem(tgItem, 1L);
                            L1ItemInstance tginfo_item3 = ItemTable.get().createItem(ole2.get_itemid());
                            if (tginfo_item3 != null) {
                                tginfo_item3.setCount(1L);
                                tginfo_item3.setEnchantLevel(tgItem.getEnchantLevel());
                                // tginfo_item3.setIdentified(tgItem.isIdentified());
                                tginfo_item3.setIdentified(false); // 防止訊息太多掉線
                                // 設置為未鑒定狀態
                                tginfo_item3.set_durability(tgItem.get_durability());
                                tginfo_item3.setChargeCount(tgItem.getChargeCount());
                                tginfo_item3.setRemainingTime(tgItem.getRemainingTime());
                                tginfo_item3.setLastUsed(tgItem.getLastUsed());
                                tginfo_item3.setBless(tgItem.getBless());
                                tginfo_item3.setAttrEnchantKind(tgItem.getAttrEnchantKind());
                                tginfo_item3.setAttrEnchantLevel(tgItem.getAttrEnchantLevel());
                                tginfo_item3.setItemAttack(tgItem.getItemAttack());
                                tginfo_item3.setItemBowAttack(tgItem.getItemBowAttack());
                                tginfo_item3.setItemReductionDmg(tgItem.getItemReductionDmg());
                                tginfo_item3.setItemSp(tgItem.getItemSp());
                                tginfo_item3.setItemprobability(tgItem.getItemprobability());
                                tginfo_item3.setItemStr(tgItem.getItemStr());
                                tginfo_item3.setItemDex(tgItem.getItemDex());
                                tginfo_item3.setItemInt(tgItem.getItemInt());
                                // 強化擴充能力
                                if (CharItemPowerTable.get().getPower(tgItem) != null) {
                                    tginfo_item3.setUpdateStr(tgItem.getUpdateStr());
                                    tginfo_item3.setUpdateDex(tgItem.getUpdateDex());
                                    tginfo_item3.setUpdateCon(tgItem.getUpdateCon());
                                    tginfo_item3.setUpdateWis(tgItem.getUpdateWis());
                                    tginfo_item3.setUpdateInt(tgItem.getUpdateInt());
                                    tginfo_item3.setUpdateCha(tgItem.getUpdateCha());
                                    tginfo_item3.setUpdateHp(tgItem.getUpdateHp());
                                    tginfo_item3.setUpdateMp(tgItem.getUpdateMp());
                                    tginfo_item3.setUpdateEarth(tgItem.getUpdateEarth());
                                    tginfo_item3.setUpdateWind(tgItem.getUpdateWind());
                                    tginfo_item3.setUpdateWater(tgItem.getUpdateWater());
                                    tginfo_item3.setUpdateFire(tgItem.getUpdateFire());
                                    tginfo_item3.setUpdateMr(tgItem.getUpdateMr());
                                    tginfo_item3.setUpdateAc(tgItem.getUpdateAc());
                                    tginfo_item3.setUpdateHpr(tgItem.getUpdateHpr());
                                    tginfo_item3.setUpdateMpr(tgItem.getUpdateMpr());
                                    tginfo_item3.setUpdateSp(tgItem.getUpdateSp());
                                    tginfo_item3.setUpdateDmgModifier(tgItem.getUpdateDmgModifier());
                                    tginfo_item3.setUpdateHitModifier(tgItem.getUpdateHitModifier());
                                    tginfo_item3.setUpdateBowDmgModifier(tgItem.getUpdateBowDmgModifier());
                                    tginfo_item3.setUpdateBowHitModifier(tgItem.getUpdateBowHitModifier());
                                    tginfo_item3.setUpdatePVPdmg(tgItem.getUpdatePVPdmg());
                                    tginfo_item3.setUpdatePVPdmg_R(tgItem.getUpdatePVPdmg_R());
                                    if (CharItemPowerTable.get().getPower(tginfo_item3) == null) {
                                        CharItemPowerTable.get().storeItem(tginfo_item3);
                                        CharItemPowerTable.get().deleteItemUpdate(tgItem.getId());
                                    } else {
                                        CharItemPowerTable.get().updateItem(tginfo_item3);
                                        CharItemPowerTable.get().deleteItemUpdate(tgItem.getId());
                                    }
                                }
                                // 強化擴充能力 end
                                if (tgItem.get_power_name() != null) {
                                    tginfo_item3.set_power_name(tgItem.get_power_name());
                                    CharItemPowerReading.get().storeItem(tginfo_item3.getId(), tginfo_item3.get_power_name());
                                    CharItemPowerReading.get().delItem(tgItem.getId());
                                }
                                if (tgItem.get_power_bless() != null) {
                                    tginfo_item3.set_power_bless(tgItem.get_power_bless());
                                    CharItemBlessReading.get().storeItem(tginfo_item3.getId(), tginfo_item3.get_power_bless());
                                    CharItemBlessReading.get().delItem(tgItem.getId());
                                }
                                pc.getInventory().storeItem(tginfo_item3);
                                tginfo_item3.setBless(tgItem.getBless());
                                pc.getInventory().updateItem(tginfo_item3, L1PcInventory.COL_BLESS);
                                WriteLogTxt.Recording("道具升級紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【 " + pc.getName() + " 】的【" + tgItem.getRecordName(tgItem.getCount()) + "】升級失敗，退階了。 (ObjectId: " + tgItem.getId() + ").");
                            } else {
                                _log.error("給予物件失敗 原因: 指定編號物品不存在(" + ole2.get_itemid() + ")");
                            }
                        } else {
                            pc.sendPackets(new S_ServerMessage(160, tgItem.getLogName(), "$252", "$248"));
                        }
                        break;
                    case 4:
                        if (_random.nextBoolean()) {
                            L1ItemPowerUpdate ole2 = (L1ItemPowerUpdate) tmplist.get(order_id - 1);
                            pc.getInventory().removeItem(tgItem, 1L);
                            L1ItemInstance tginfo_item4 = ItemTable.get().createItem(ole2.get_itemid());
                            if (tginfo_item4 != null) {
                                tginfo_item4.setCount(1L);
                                tginfo_item4.setEnchantLevel(tgItem.getEnchantLevel());
                                // tginfo_item4.setIdentified(tgItem.isIdentified());
                                tginfo_item4.setIdentified(false); // 防止訊息太多掉線
                                // 設置為未鑒定狀態
                                tginfo_item4.set_durability(tgItem.get_durability());
                                tginfo_item4.setChargeCount(tgItem.getChargeCount());
                                tginfo_item4.setRemainingTime(tgItem.getRemainingTime());
                                tginfo_item4.setLastUsed(tgItem.getLastUsed());
                                tginfo_item4.setBless(tgItem.getBless());
                                tginfo_item4.setAttrEnchantKind(tgItem.getAttrEnchantKind());
                                tginfo_item4.setAttrEnchantLevel(tgItem.getAttrEnchantLevel());
                                tginfo_item4.setItemAttack(tgItem.getItemAttack());
                                tginfo_item4.setItemBowAttack(tgItem.getItemBowAttack());
                                tginfo_item4.setItemReductionDmg(tgItem.getItemReductionDmg());
                                tginfo_item4.setItemSp(tgItem.getItemSp());
                                tginfo_item4.setItemprobability(tgItem.getItemprobability());
                                tginfo_item4.setItemStr(tgItem.getItemStr());
                                tginfo_item4.setItemDex(tgItem.getItemDex());
                                tginfo_item4.setItemInt(tgItem.getItemInt());
                                // 強化擴充能力
                                if (CharItemPowerTable.get().getPower(tgItem) != null) {
                                    tginfo_item4.setUpdateStr(tgItem.getUpdateStr());
                                    tginfo_item4.setUpdateDex(tgItem.getUpdateDex());
                                    tginfo_item4.setUpdateCon(tgItem.getUpdateCon());
                                    tginfo_item4.setUpdateWis(tgItem.getUpdateWis());
                                    tginfo_item4.setUpdateInt(tgItem.getUpdateInt());
                                    tginfo_item4.setUpdateCha(tgItem.getUpdateCha());
                                    tginfo_item4.setUpdateHp(tgItem.getUpdateHp());
                                    tginfo_item4.setUpdateMp(tgItem.getUpdateMp());
                                    tginfo_item4.setUpdateEarth(tgItem.getUpdateEarth());
                                    tginfo_item4.setUpdateWind(tgItem.getUpdateWind());
                                    tginfo_item4.setUpdateWater(tgItem.getUpdateWater());
                                    tginfo_item4.setUpdateFire(tgItem.getUpdateFire());
                                    tginfo_item4.setUpdateMr(tgItem.getUpdateMr());
                                    tginfo_item4.setUpdateAc(tgItem.getUpdateAc());
                                    tginfo_item4.setUpdateHpr(tgItem.getUpdateHpr());
                                    tginfo_item4.setUpdateMpr(tgItem.getUpdateMpr());
                                    tginfo_item4.setUpdateSp(tgItem.getUpdateSp());
                                    tginfo_item4.setUpdateDmgModifier(tgItem.getUpdateDmgModifier());
                                    tginfo_item4.setUpdateHitModifier(tgItem.getUpdateHitModifier());
                                    tginfo_item4.setUpdateBowDmgModifier(tgItem.getUpdateBowDmgModifier());
                                    tginfo_item4.setUpdateBowHitModifier(tgItem.getUpdateBowHitModifier());
                                    tginfo_item4.setUpdatePVPdmg(tgItem.getUpdatePVPdmg());
                                    tginfo_item4.setUpdatePVPdmg_R(tgItem.getUpdatePVPdmg_R());
                                    if (CharItemPowerTable.get().getPower(tginfo_item4) == null) {
                                        CharItemPowerTable.get().storeItem(tginfo_item4);
                                        CharItemPowerTable.get().deleteItemUpdate(tgItem.getId());
                                    } else {
                                        CharItemPowerTable.get().updateItem(tginfo_item4);
                                        CharItemPowerTable.get().deleteItemUpdate(tgItem.getId());
                                    }
                                }
                                // 強化擴充能力 end
                                if (tgItem.get_power_name() != null) {
                                    tginfo_item4.set_power_name(tgItem.get_power_name());
                                    CharItemPowerReading.get().storeItem(tginfo_item4.getId(), tginfo_item4.get_power_name());
                                    CharItemPowerReading.get().delItem(tgItem.getId());
                                }
                                if (tgItem.get_power_bless() != null) {
                                    tginfo_item4.set_power_bless(tgItem.get_power_bless());
                                    CharItemBlessReading.get().storeItem(tginfo_item4.getId(), tginfo_item4.get_power_bless());
                                    CharItemBlessReading.get().delItem(tgItem.getId());
                                }
                                pc.getInventory().storeItem(tginfo_item4);
                                tginfo_item4.setBless(tgItem.getBless());
                                pc.getInventory().updateItem(tginfo_item4, L1PcInventory.COL_BLESS);
                                WriteLogTxt.Recording("道具升級紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【 " + pc.getName() + " 】的【" + tgItem.getRecordName(tgItem.getCount()) + "】升級失敗，退階了。 (ObjectId: " + tgItem.getId() + ").");
                            } else {
                                _log.error("給予物件失敗 原因: 指定編號物品不存在(" + ole2.get_itemid() + ")");
                            }
                        } else {
                            pc.sendPackets(new S_ServerMessage(164, tgItem.getLogName(), "$252"));
                            pc.getInventory().removeItem(tgItem, 1L);
                        }
                        break;
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}