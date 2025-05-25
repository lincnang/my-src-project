package com.lineage.server.clientpackets;

import com.add.L1Config;
import com.add.Mobbling.MobblingTimeList;
import com.lineage.config.ConfigOtherSet2;
import com.lineage.data.event.ShopXSet;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.datatables.*;
import com.lineage.server.datatables.lock.*;
import com.lineage.server.datatables.sql.ServerGmCommandTable;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1PandoraItem;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.model.shop.L1Shop;
import com.lineage.server.model.shop.L1ShopBuyOrderList;
import com.lineage.server.model.shop.L1ShopSellOrderList;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.*;
import com.lineage.server.timecontroller.event.GamblingTime;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import com.lineage.system.GetNowTime;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 要求列表物品取得
 *
 * @author dexc
 */
public class C_Result extends ClientBasePacket {
    public static final Log _log = LogFactory.getLog(C_Result.class);
    public static final Random _random = new Random();
    /*
     * public C_Result() { }
     *
     * public C_Result(final byte[] abyte0, final ClientExecutor client) {
     * super(abyte0); try { this.start(abyte0, client);
     *
     * } catch (final Exception e) { _log.error(e.getLocalizedMessage(), e); } }
     */
    /**
     * [原碼] 怪物對戰系統
     */
    private static MobblingTimeList _Mob = MobblingTimeList.Mob();

    public static void Lucky(final String info) {
        final int Mon = GetNowTime.GetNowMonth() + 1;
        final int Day = GetNowTime.GetNowDay();
        final String z = String.valueOf(Mon) + "月" + Day + "日";
        try {
            final BufferedWriter out = new BufferedWriter(new FileWriter("物品操作記錄/潘朵拉抽抽樂取回紀錄/" + z + ".txt", true));
            out.write(String.valueOf(info) + "\r\n");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(final byte[] decrypt, final ClientExecutor client) {
        try {
            // 資料載入
            this.read(decrypt);
            final L1PcInstance pc = client.getActiveChar();
            if (pc.isGhost()) { // 鬼魂模式
                return;
            }
            if (pc.isDead()) { // 死亡
                return;
            }
            if (pc.isTeleport()) { // 傳送中
                return;
            }
            if (pc.isPrivateShop()) { // 商店村模式
                return;
            }
            final int npcObjectId = this.readD();
            final int resultType = this.readC();
            final int size = this.readC();
            @SuppressWarnings("unused") final int unknown = this.readC();
            /*
             * System.out.println(npcObjectId); System.out.println(resultType);
             * System.out.println(size); System.out.println(unknown);
             */
            int npcId = 0;
            // String npcImpl = "";
            // String nameid = null;
            boolean isPrivateShop = false;
            final L1Object findObject = World.get().findObject(npcObjectId);
            if (findObject != null) {
                boolean isStop = false;
                // 對象是NPC
                if (findObject instanceof L1NpcInstance) {
                    final L1NpcInstance targetNpc = (L1NpcInstance) findObject;
                    npcId = targetNpc.getNpcTemplate().get_npcId();
                    isStop = true;
                    // 對象是PC
                } else if (findObject instanceof L1PcInstance) {
                    isPrivateShop = true;
                    isStop = true;
                }
                if (isStop) {
                    final int diffLocX = Math.abs(pc.getX() - findObject.getX());
                    final int diffLocY = Math.abs(pc.getY() - findObject.getY());
                    // 距離5格以上無效
                    if ((diffLocX > 5) || (diffLocY > 5)) {
                        return;
                    }
                }
            }
            // System.out.println("resultType:" + resultType);
            switch (resultType) {
                case 0:// 買入物品
                    /** [原碼] 怪物對戰系統 */
                    if (npcId == L1Config._2154 && L1Config._2151) {
                        L1NpcInstance targetNpc = (L1NpcInstance) findObject;
                        pcBuymobtowar(pc, size, targetNpc);
                    }
                    /** End */
                    /** [原碼] 自動練功技能 */
                    if (npcObjectId == pc.getId()) {
                        if (size > 1) {
                            pc.sendPackets(new S_ServerMessage("\\fR一次只能選擇一個技能。"));
                            return;
                        }
                        final int skillId = this.readD();
                        pc.clearAttackSkillList();
                        pc.setAttackSkillList(skillId);
                        final L1Skills skill = SkillsTable.get().getTemplate(skillId);
                        String name = skill.getName();
                        pc.sendPackets(new S_SystemMessage("成功將[" + name + "]登錄至自動施放陣列內"));
                        return;
                    }
                    /** End */
                    /** [原碼] 大樂透系統 */
                    /*
                     * if (npcId == L1Config._2162 && L1Config._2161) {
                     * L1NpcInstance targetNpc = (L1NpcInstance) findObject;
                     * pcBuyBigHot(pc, size, targetNpc); }
                     */
                    /** End */
                    if (size > 0) {
                        if (findObject instanceof L1MerchantInstance) {
                            switch (npcId) {
                                case 70535:// 托售管理員
                                    this.mode_shopS(pc, size);
                                    break;
                                default:
                                    this.mode_buy(pc, npcId, size);
                                    break;
                            }
                            return;
                        }
                        if (findObject instanceof L1GamblingInstance) {
                            this.mode_gambling(pc, npcId, size, true);
                            return;
                        }
                        if (findObject instanceof L1CnInstance) {
                            this.mode_cn_buy(pc, size);
                            return;
                        }
                        if (findObject instanceof L1DeInstance) {//虛擬假人 購買虛擬商人物品
                            final L1DeInstance de = (L1DeInstance) findObject;
                            mode_buyde(pc, de, size);
                            return;
                        }
                        if (pc.equals(findObject)) {// 是自己
                            //this.mode_cn_buy(pc, size);
                            switch (pc.getTemporary()) {
                                case 0:
                                    this.mode_cn_buy(pc, size);
                                    break;
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                    this.mode_auto_hp(pc, size, true); // 特殊商店 -> 購買自動喝水補魔道具
                                    break;
                                case 7:
                                    this.mode_remove_item(pc, size, true); // 特殊商店 -> 取消刪除物品
                                    break;
                            }
                            return;
                        }
                        if (findObject instanceof L1PcInstance) {
                            if (isPrivateShop) {// 買入個人商店物品
                                final L1PcInstance targetPc = (L1PcInstance) findObject;
                                this.mode_buypc(pc, targetPc, size);
                                return;
                            }
                        }
                    }
                    break;
                case 1:// 賣出物品
                    if (size > 0) {
                        /** [原碼] 怪物對戰系統 */
                        if (npcId == L1Config._2154) {
                            L1NpcInstance shop = (L1NpcInstance) findObject;
                            pcSellmobtowar(pc, size, shop);
                            /** [原碼] 大樂透系統 */
                        } else if (npcId == L1Config._2162) {
                            L1NpcInstance shop = (L1NpcInstance) findObject;
                            pcSellBigHot(pc, size, shop);
                        }
                        /** End */
                        if (findObject instanceof L1MerchantInstance) {
                            switch (npcId) {
                                case 70023:// 亞丁商團(垃圾回收)
                                    this.mode_sellall(pc, size);
                                    break;
                                case 81436:// 火神煉化工匠(火神結晶)
                                    this.mode_firecrystal(pc, size);
                                    break;
                                default:
                                    this.mode_sell(pc, npcId, size);
                                    break;
                            }
                            return;
                        }
                        if (findObject instanceof L1CnInstance) {
                            this.mode_cn_sell(pc, size);
                            return;
                        }
                        if (findObject instanceof L1GamblingInstance) {
                            this.mode_gambling(pc, npcId, size, false);
                            return;
                        }
                        if (pc.equals(findObject)) {// 是自己
                            if (pc.getTemporary() == 6) {
                                this.mode_removeItem_sell(pc, size);// 特殊商店 -> 添加刪除物品
                            }
                            return;
                        }
                        if (findObject instanceof L1PcInstance) {// 賣出物品給個人商店
                            if (isPrivateShop) {
                                final L1PcInstance targetPc = (L1PcInstance) findObject;
                                this.mode_sellpc(pc, targetPc, size);
                            }
                        }
                    }
                    break;
                case 2:// 帳號倉庫存入
                    if (size > 0) {
                        if (findObject instanceof L1DwarfInstance) {
                            final int level = pc.getLevel();
                            if (level >= ConfigOtherSet2.Dwarf_Level) {
                                this.mode_warehouse_in(pc, npcId, size);
                            } else {
                                pc.sendPackets(new S_SystemMessage("您未達到倉庫使用等級，使用等級為：" + ConfigOtherSet2.Dwarf_Level));
                            }
                        }
                    }
                    break;
                case 3:// 帳號倉庫取出
                    if (size > 0) {
                        if (findObject instanceof L1DwarfInstance) {
                            final int level = pc.getLevel();
                            if (level >= ConfigOtherSet2.Dwarf_Level) {
                                this.mode_warehouse_out(pc, npcId, size);
                            } else {
                                pc.sendPackets(new S_SystemMessage("您未達到倉庫使用等級，使用等級為：" + ConfigOtherSet2.Dwarf_Level));
                            }
                        }
                    }
                    break;
                case 4:// 血盟倉庫存入
                    if (size > 0) {
                        if (findObject instanceof L1DwarfInstance) {
                            final int level = pc.getLevel();
                            if (level >= ConfigOtherSet2.Dwarf_Level) {
                                this.mode_warehouse_clan_in(pc, npcId, size);
                            } else {
                                final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
                                if (clan != null) {
                                    clan.setWarehouseUsingChar(0); // 血盟倉庫使用解除
                                }
                                pc.sendPackets(new S_SystemMessage("您未達到倉庫使用等級，使用等級為：" + ConfigOtherSet2.Dwarf_Level));
                            }
                        }
                    } else {// 血盟倉庫存入中 Cancel、ESC
                        final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
                        if (clan != null) {
                            clan.setWarehouseUsingChar(0); // 血盟倉庫使用解除
                        }
                    }
                    break;
                case 5:// 血盟倉庫取出
                    if (size > 0) {
                        if (findObject instanceof L1DwarfInstance) {
                            final int level = pc.getLevel();
                            if (level >= ConfigOtherSet2.Dwarf_Level) {
                                this.mode_warehouse_clan_out(pc, npcId, size);
                            } else {
                                final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
                                if (clan != null) {
                                    clan.setWarehouseUsingChar(0); // 血盟倉庫使用解除
                                }
                                pc.sendPackets(new S_SystemMessage("您未達到倉庫使用等級，使用等級為：" + ConfigOtherSet2.Dwarf_Level));
                            }
                        }
                    } else {// 血盟倉庫取出中 Cancel、ESC
                        final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
                        if (clan != null) {
                            clan.setWarehouseUsingChar(0); // 血盟倉庫使用解除
                        }
                    }
                    break;
                case 6:// 無傳遞封包
                    break;
                case 7:// 無傳遞封包
                    break;
                case 8:// 精靈倉庫存入
                    if (size > 0) {
                        if (findObject instanceof L1DwarfInstance) {
                            final int level = pc.getLevel();
                            if ((level >= ConfigOtherSet2.Dwarf_Level) && pc.isElf()) {
                                this.mode_warehouse_elf_in(pc, npcId, size);
                            } else {
                                pc.sendPackets(new S_SystemMessage("您未達到倉庫使用等級，使用等級為：" + ConfigOtherSet2.Dwarf_Level));
                            }
                        }
                    }
                    break;
                case 9:// 精靈倉庫取出
                    if (size > 0) {
                        if (findObject instanceof L1DwarfInstance) {
                            final int level = pc.getLevel();
                            if ((level >= ConfigOtherSet2.Dwarf_Level) && pc.isElf()) {
                                this.mode_warehouse_elf_out(pc, npcId, size);
                            } else {
                                pc.sendPackets(new S_SystemMessage("您未達到倉庫使用等級，使用等級為：" + ConfigOtherSet2.Dwarf_Level));
                            }
                        }
                    }
                    break;
                case 10:// 物品強化
                    // 無法使用 -> 換成 12
				/*if (size > 0) {
					switch (npcId) {
					case 91141:// 物品升級專員
					case 91142:// 物品升級專員
					case 91143:// 物品升級專員
					case 111410:// 物品升級專員
						mode_update_item(pc, size, npcObjectId);
						break;
					default:
						break;
					}
				}*/
                    break;
                case 11:// 無傳遞封包
                    break;
                case 12:// 提煉武器/防具
                    if (size > 0) {
                        switch (npcId) {
                            case 70535:// 托售管理員
                                this.mode_shop_item(pc, size, npcObjectId);
                                break;
                            case 91141:// 物品升級專員
                            case 91142:// 物品升級專員
                            case 91143:// 物品升級專員
                            case 111410:// 物品升級專員
                                mode_update_item(pc, size, npcObjectId);
                                break;
                        }
                    }
                    break;
                case 17:// 角色專屬倉庫存入
                    if (size > 0) {
                        if (findObject instanceof L1DwarfInstance) {
                            final int level = pc.getLevel();
                            if (level >= ConfigOtherSet2.Dwarf_Level) {
                                this.mode_warehouse_cha_in(pc, npcId, size);
                            } else {
                                pc.sendPackets(new S_SystemMessage("您未達到倉庫使用等級，使用等級為：" + ConfigOtherSet2.Dwarf_Level));
                            }
                        }
                    }
                    break;
                case 18:// 角色專屬倉庫取出
                    if (size > 0) {
                        if (findObject instanceof L1DwarfInstance) {
                            final int level = pc.getLevel();
                            if (level >= ConfigOtherSet2.Dwarf_Level) {
                                this.mode_warehouse_cha_out(pc, npcId, size);
                            } else {
                                pc.sendPackets(new S_SystemMessage("您未達到倉庫使用等級，使用等級為：" + ConfigOtherSet2.Dwarf_Level));
                            }
                        }
                    }
                    break;
                case 29: /** [原碼] 潘朵拉抽抽樂 */
                    if (size > 0) {
                        this.pcLuckylottery(pc, size);
                    }
                    break;
            }
        } catch (final Exception e) {
            // _log.error(e.getLocalizedMessage(), e);
        } finally {
            this.over();
        }
    }

    private void mode_update_item(L1PcInstance pc, int size, int npcObjectId) {
        try {
            if (size != 1) {
                pc.sendPackets(new S_ServerMessage("\\fR你只能選取一樣裝備用來升級。"));
                pc.sendPackets(new S_CloseList(pc.getId()));
                return;
            }
            final int orderId = this.readD();
            final int count = Math.max(0, this.readD());// 數量
            if (count != 1) {
                pc.sendPackets(new S_CloseList(pc.getId()));
                return;
            }
            final L1ItemInstance item = pc.getInventory().getItem(orderId);
            final ArrayList<L1ItemUpdate> items = ItemUpdateTable.get().get(item.getItemId());
            final String[] names = new String[items.size()];
            for (int index = 0; index < items.size(); index++) {
                int toid = items.get(index).get_toid();
                final L1Item tgitem = ItemTable.get().getTemplate(toid);
                if (tgitem != null) {
                    names[index] = tgitem.getName();
                }
            }
            pc.set_mode_id(orderId);
            pc.sendPackets(new S_NPCTalkReturn(npcObjectId, L1ItemUpdate._html_02, names));
        } catch (Exception e) {
            _log.error("升級裝備物品數據異常: " + pc.getName());
        }
    }

    /**
     * 托售管理員(購買物品)
     *
     * @param pc
     * @param size
     */
    private void mode_shopS(L1PcInstance pc, int size) {
        try {
            final Map<Integer, Integer> sellScoreMapMap = new HashMap<Integer, Integer>();
            for (int i = 0; i < size; i++) {
                final int orderId = this.readD();
                final int count = Math.max(0, this.readD());// 數量
                if (count <= 0) {
                    _log.error("要求列表物品取得傳回數量小於等於0: " + pc.getName() + ":" + (pc.getNetConnection().kick()));
                    continue;
                }
                sellScoreMapMap.put(new Integer(orderId), new Integer(count));
            }
            pc.get_otherList().get_buyCnS(sellScoreMapMap);
        } catch (Exception e) {
            _log.error("購買人物托售物品數據異常: " + pc.getName());
        }
    }

    /**
     * 托售管理員(托售物品)
     *
     * @param pc
     * @param size
     * @param npcObjectId
     */
    private void mode_shop_item(L1PcInstance pc, int size, int npcObjectId) {
        try {
            if (size == 1) {
                final int objid = this.readD();
                final L1Object object = pc.getInventory().getItem(objid);
                boolean isError = false;
                if (object instanceof L1ItemInstance) {
                    final L1ItemInstance item = (L1ItemInstance) object;
                    if (!item.getItem().isTradable()) {// 不可轉移
                        isError = true;
                    }
                    if (item.isEquipped()) {// 使用中物件
                        isError = true;
                    }
                    if (!item.isIdentified()) {// 未鑒定物品
                        isError = true;
                    }
                    if (item.getItem().getMaxUseTime() != 0) {// 具有時間限制
                        isError = true;
                    }
                    if (item.get_time() != null) {// 具有存在時間
                        isError = true;
                    }
                    if (ShopXTable.get().getTemplate(item.getItem().getItemId()) != null) {// 不可托售物品
                        isError = true;
                    }
                    // 寵物
                    final Object[] petlist = pc.getPetList().values().toArray();
                    for (final Object petObject : petlist) {
                        if (petObject instanceof L1PetInstance) {
                            final L1PetInstance pet = (L1PetInstance) petObject;
                            if (item.getId() == pet.getItemObjId()) {
                                isError = true;
                            }
                        }
                    }
                    // 取回娃娃
                    if (pc.getDoll(item.getId()) != null) {
                        isError = true;
                    }
                    // 取回娃娃
                    if (pc.getDoll2(item.getId()) != null) {
                        isError = true;
                    }
                    // 超級娃娃
                    if (pc.get_power_doll() != null) {
                        if (pc.get_power_doll().getItemObjId() == item.getId()) {
                            isError = true;
                        }
                    }
                    if (item.getraceGamNo() != null) {// 賭狗票
                        isError = true;
                    }
                    if (item.getEnchantLevel() < 0) {// 強化為負值
                        isError = true;
                    }
                    if (item.getItem().getMaxChargeCount() != 0) {// 具有次數
                        if (item.getChargeCount() <= 0) {// 已無次數
                            isError = true;
                        }
                    }
                    if (ItemRestrictionsTable.RESTRICTIONS.contains(item.getItemId())) {// 限制轉移物品
                        isError = true;
                    }
                    if (ServerGmCommandTable.tradeControl.contains(item.getId())) {// 限制轉移物品
                        pc.sendPackets(new S_ServerMessage("獎勵物品無法轉移"));
                        return;
                    }
                    if (isError) {
                        pc.sendPackets(new S_NPCTalkReturn(npcObjectId, "y_x_e1"));
                    } else {
                        // 取回天幣數量
                        //final L1ItemInstance itemT = pc.getInventory().checkItemX(40308, ShopXSet.ADENA);
                        final L1ItemInstance itemT = pc.getInventory().checkItemX(ShopXSet.ITEMID, ShopXSet.ADENA);
                        if (itemT == null) {
                            // 337：\f1%0不足%s。 0_o"
                            //pc.sendPackets(new S_ServerMessage(337, "天幣"));
                            final L1Item shopXcash = ItemTable.get().getTemplate(ShopXSet.ITEMID);
                            pc.sendPackets(new S_ServerMessage(337, shopXcash.getName()));
                            // 關閉對話窗
                            pc.sendPackets(new S_CloseList(pc.getId()));
                            return;
                        }
                        // 暫存物件訊息
                        pc.get_other().set_item(item);
                        pc.sendPackets(new S_CnsSell(npcObjectId, "y_x_3", "ma"));
                    }
                }
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npcObjectId, "y_x_e"));
            }
        } catch (Exception e) {
            _log.error("人物托售物品數據異常: " + pc.getName());
        }
    }

    /**
     * 玩家賣出物品給予萬物回收商
     *
     * @param pc
     * @param npcId
     * @param size
     */
    private void mode_sellall(final L1PcInstance pc, final int size) {
        try {
            final Map<Integer, Integer> sellallMap = new HashMap<Integer, Integer>();
            for (int i = 0; i < size; i++) {
                final int objid = this.readD();
                final int count = Math.max(0, this.readD());// 數量
                if (count <= 0) {
                    _log.error("要求列表物品取得傳回數量小於等於0: " + pc.getName() + ":" + (pc.getNetConnection().kick()));
                    continue;
                }
                sellallMap.put(new Integer(objid), new Integer(count));
            }
            pc.get_otherList().sellall(sellallMap);
        } catch (Exception e) {
            _log.error("玩家賣出物品給予萬物回收商數據異常: " + pc.getName());
        }
    }

    /**
     * 玩家買入商城物品
     *
     * @param pc
     * @param size
     */
    private void mode_cn_buy(final L1PcInstance pc, final int size) {
        try {
            final Map<Integer, Integer> cnMap = new HashMap<Integer, Integer>();
            for (int i = 0; i < size; i++) {
                final int orderId = this.readD();
                final int count = Math.max(0, this.readD());// 數量
                if (count <= 0) {
                    _log.error("要求列表物品取得傳回數量小於等於0: " + pc.getName() + ":" + (pc.getNetConnection().kick()));
                    continue;
                }
                cnMap.put(new Integer(orderId), new Integer(count));
            }
            pc.get_otherList().get_buyCn(cnMap);
        } catch (Exception e) {
            _log.error("玩家買入商城物品數據異常: " + pc.getName());
        }
    }

    /**
     * 特殊商店 -> 購買自動喝水補魔道具
     *
     * @param pc
     * @param size
     * @param isShop
     */
    private void mode_auto_hp(final L1PcInstance pc, final int size, final boolean isShop) {
        try {
            if (isShop) { // 買入
                final Map<Integer, Integer> vitMap = new HashMap<Integer, Integer>();
                for (int i = 0; i < size; i++) {
                    final int orderId = this.readD();
                    final int count = this.readD();
                    vitMap.put(new Integer(orderId), new Integer(count));
                }
                pc.get_otherList().get_buyAutoHpList(vitMap);
            }
        } catch (Exception e) {
            _log.error("特殊商店 -> 購買自動喝水補魔道具買入物品數據異常: " + pc.getName());
        }
    }

    /**
     * 特殊商店 -> 添加刪除物品
     *
     * @param pc
     * @param size
     */
    private void mode_removeItem_sell(final L1PcInstance pc, final int size) {
        try {
            final Map<Integer, L1ItemInstance> cnsellMap = new HashMap<Integer, L1ItemInstance>();
            for (int i = 0; i < size; i++) {
                final int objid = this.readD();
                final int count = Math.max(0, this.readD());// 數量
                if (count <= 0) {
                    _log.error("要求列表物品取得傳回數量小於等於0: " + pc.getName() + ":" + (pc.getNetConnection().kick()));
                    continue;
                }
                cnsellMap.put(new Integer(objid), new L1ItemInstance());
            }
            pc.get_otherList().get_buyRemoveItemSellList(cnsellMap);
        } catch (Exception e) {
            _log.error("玩家賣出物品給予商城道具回收專員數據異常: " + pc.getName());
        }
    }

    /**
     * 特殊商店 -> 取消刪除物品
     *
     * @param pc
     * @param size
     * @param isShop
     */
    private void mode_remove_item(final L1PcInstance pc, final int size, final boolean isShop) {
        try {
            if (isShop) { // 買入
                final Map<Integer, Integer> vitMap = new HashMap<Integer, Integer>();
                for (int i = 0; i < size; i++) {
                    final int orderId = this.readD();
                    final int count = this.readD();
                    vitMap.put(new Integer(orderId), new Integer(count));
                }
                pc.get_otherList().get_buyRemoveItemBuyList(vitMap);
            }
        } catch (Exception e) {
            _log.error("特殊商店 -> 取消刪除物品買入物品數據異常: " + pc.getName());
        }
    }

    /**
     * 玩家賣出物品給予商城道具回收專員
     *
     * @param pc
     * @param size
     */
    private void mode_cn_sell(final L1PcInstance pc, final int size) {
        try {
            final Map<Integer, Integer> cnsellMap = new HashMap<Integer, Integer>();
            for (int i = 0; i < size; i++) {
                final int objid = this.readD();
                final int count = Math.max(0, this.readD());// 數量
                if (count <= 0) {
                    _log.error("要求列表物品取得傳回數量小於等於0: " + pc.getName() + ":" + (pc.getNetConnection().kick()));
                    continue;
                }
                cnsellMap.put(new Integer(objid), new Integer(count));
            }
            pc.get_otherList().sellcnitem(cnsellMap);
        } catch (Exception e) {
            _log.error("玩家賣出物品給予商城道具回收專員數據異常: " + pc.getName());
        }
    }

    /**
     * 玩家賣出物品取得火神結晶
     *
     * @param pc
     * @param size
     */
    private void mode_firecrystal(final L1PcInstance pc, final int size) {
        try {
            final Map<Integer, Integer> FCMap = new HashMap<Integer, Integer>();
            for (int i = 0; i < size; i++) {
                final int objid = this.readD();
                final int count = Math.max(0, this.readD());// 數量
                if (count <= 0) {
                    _log.error("要求列表物品取得傳回數量小於等於0: " + pc.getName() + ":" + (pc.getNetConnection().kick()));
                    continue;
                }
                FCMap.put(new Integer(objid), new Integer(count));
            }
            pc.get_otherList().sellforfirecrystal(FCMap);
        } catch (Exception e) {
            _log.error("玩家賣出物品取得火神結晶數據異常: " + pc.getName());
        }
    }

    /**
     * 賭場NPC
     *
     * @param pc
     * @param npcId
     * @param size
     * @param isShop true買入 false賣出
     */
    private void mode_gambling(final L1PcInstance pc, final int npcId, final int size, final boolean isShop) {
        if (isShop) {// 買入
            if (GamblingTime.isStart()) {
                // 關閉對話窗
                pc.sendPackets(new S_CloseList(pc.getId()));
                return;
            }
            final Map<Integer, Integer> gamMap = new HashMap<Integer, Integer>();
            for (int i = 0; i < size; i++) {
                final int orderId = this.readD();
                final int count = Math.max(0, this.readD());// 數量
                if (count <= 0) {
                    _log.error("要求列表物品取得傳回數量小於等於0: " + pc.getName() + ":" + (pc.getNetConnection().kick()));
                    continue;
                }
                gamMap.put(new Integer(orderId), new Integer(count));
            }
            pc.get_otherList().get_buyGam(gamMap);
        } else {// 賣出
            for (int i = 0; i < size; i++) {
                final int objid = this.readD();
                final int count = Math.max(0, this.readD());// 數量
                if (count <= 0) {
                    _log.error("要求列表物品取得傳回數量小於等於0: " + pc.getName() + ":" + (pc.getNetConnection().kick()));
                    continue;
                }
                pc.get_otherList().get_sellGam(objid, count);
            }
        }
    }

    /**
     * 精靈倉庫取出 XXX
     *
     * @param pc
     * @param npcId
     * @param size
     */
    private void mode_warehouse_elf_out(final L1PcInstance pc, final int npcId, final int size) {
        int objectId, count;
        L1ItemInstance item;
        for (int i = 0; i < size; i++) {
            objectId = this.readD();
            count = Math.max(0, this.readD());
            if (count <= 0) {
                _log.error("要求精靈倉庫取出傳回數量小於等於0: " + pc.getName() + (pc.getNetConnection().kick()));
                break;
            }
            item = pc.getDwarfForElfInventory().getItem(objectId);
            if (item == null) {
                _log.error("精靈倉庫取出數據異常(物品為空): " + pc.getName() + "/" + pc.getNetConnection().hashCode());
                break;
            }
            if (!DwarfForElfReading.get().getUserItems(pc.getAccountName(), objectId, count)) {
                _log.error("精靈倉庫取出數據異常(該倉庫指定數據有誤): " + pc.getName() + "/" + pc.getNetConnection().hashCode());
                break;
            }
            if (item.getItemId() == 44070 && ServerGmCommandTable.tradeControl.contains(objectId) && pc.getInventory().checkItem(44070)) {// 限制轉移物品
                pc.sendPackets(new S_ServerMessage("獎勵物品無法轉移"));
                _log.error("角色專屬倉庫取出獎勵物品異常: " + pc.getName() + ":" + objectId);
                break;
            }
            L1ItemInstance wareitem = pc.getInventory().findItemId(44070);
            if (wareitem != null && item.getItemId() == 44070 && ServerGmCommandTable.tradeControl.contains(wareitem.getId())) {
                pc.sendPackets(new S_ServerMessage("獎勵物品無法轉移"));
                _log.error("角色專屬倉庫取出獎勵物品異常: " + pc.getName() + ":" + objectId);
                break;
            }
            if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) { // 容量重量確認及送信
                if (pc.getInventory().consumeItem(40494, 2)) { // 
                    pc.getDwarfForElfInventory().tradeItem(item, count, pc.getInventory());
                    //精靈倉庫取出記錄
                    WriteLogTxt.Recording("精靈倉庫取出記錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【" + pc.getName() + "】帳號:【" + pc.getAccountName() + "】精靈倉庫取出【" + "+" + item.getEnchantLevel() + " " + item.getItem().getName() + "】 物品【" + item.getCount() + "】個" + "OBJID:" + item.getId() + "】.");
                    RecordTable.get().recordeWarehouse_elf(pc.getName(), "領取", "妖倉", item.getAllName(), count, item.getId(), pc
                            .getIp());
                } else {
                    pc.sendPackets(new S_ServerMessage(337, "$767")); // \f1%0不足。
                    break;
                }
            } else {
                pc.sendPackets(new S_ServerMessage(270)); // \f1持重取引。
                break;

            }
        }
    }

    /**
     * 精靈倉庫存入
     *
     * @param pc
     * @param npcId
     * @param size
     */
    private void mode_warehouse_elf_in(final L1PcInstance pc, final int npcId, final int size) {
        int objectId, count;
        for (int i = 0; i < size; i++) {
            objectId = this.readD();
            count = Math.max(0, this.readD());
            if (count <= 0) {
                _log.error("要求精靈倉庫存入傳回數量小於等於0: " + pc.getName() + (pc.getNetConnection().kick()));
                break;
            }
            final L1Object object = pc.getInventory().getItem(objectId);
            if (object == null) {
                _log.error("人物背包資料取出數據異常(物品為空): " + pc.getName() + "/" + pc.getNetConnection().hashCode());
                break;
            }
            if (!CharItemsReading.get().getUserItems(pc.getId(), objectId, count)) {
                _log.error("人物背包資料取出數據異常(該倉庫指定數據有誤): " + pc.getName() + "/" + pc.getNetConnection().hashCode());
                break;
            }
            final L1ItemInstance item = (L1ItemInstance) object;
            if (ServerGmCommandTable.tradeControl.contains(item.getId())) {// 限制轉移物品
                pc.sendPackets(new S_ServerMessage("獎勵物品無法轉移"));
                return;
            }
            if (!item.getItem().isTradable()) {
                // 210 \f1%0%d是不可轉移的…
                pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
                break;
            }
            if (item.get_time() != null) {
                // \f1%0%d是不可轉移的…
                pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
                break;
            }
            if (item.getItemId() == 80033 && pc.getLevel() < ConfigOtherSet2.R_80033) {
                pc.sendPackets(new S_ServerMessage("預防作弊啟動，85級以下無法做此動作"));
                return;
            }
            // 寵物
            final Object[] petlist = pc.getPetList().values().toArray();
            for (final Object petObject : petlist) {
                if (petObject instanceof L1PetInstance) {
                    final L1PetInstance pet = (L1PetInstance) petObject;
                    if (item.getId() == pet.getItemObjId()) {
                        // \f1%0%d是不可轉移的…
                        pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
                        break;
                    }
                }
            }
            // 取回娃娃
            if (pc.getDoll(item.getId()) != null) {
                // 1,181：這個魔法娃娃目前正在使用中。
                pc.sendPackets(new S_ServerMessage(1181));
                break;
            }
            // 取回娃娃
            if (pc.getDoll2(item.getId()) != null) {
                // 1,181：這個魔法娃娃目前正在使用中。
                pc.sendPackets(new S_ServerMessage(1181));
                break;
            }
            // 超級娃娃
            if (pc.get_power_doll() != null) {
                if (pc.get_power_doll().getItemObjId() == item.getId()) {
                    // 1,181：這個魔法娃娃目前正在使用中。
                    pc.sendPackets(new S_ServerMessage(1181));
                    break;
                }
            }
            if (pc.getDwarfForElfInventory().checkAddItemToWarehouse(item, count, L1Inventory.WAREHOUSE_TYPE_PERSONAL) == L1Inventory.SIZE_OVER) {
                pc.sendPackets(new S_ServerMessage(75)); // \f1以上置場所。
                break;
            }
            pc.getInventory().tradeItem(objectId, count, pc.getDwarfForElfInventory());
            // pc.turnOnOffLight();
            //精靈倉庫存入記錄
            WriteLogTxt.Recording("精靈倉庫存入記錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【" + pc.getName() + "】帳號:【" + pc.getAccountName() + "】精靈倉存入【" + "+" + item.getEnchantLevel() + " " + item.getItem().getName() + "】 物品【" + item.getCount() + "】個" + "OBJID:" + item.getId() + "】.");
            RecordTable.get().recordeWarehouse_elf(pc.getName(), "存入", "妖倉", item.getAllName(), count, item.getId(), pc
                    .getIp());
        }
    }

    /**
     * 血盟倉庫取出 XXX
     *
     * @param pc
     * @param npcId
     * @param size
     */
    private void mode_warehouse_clan_out(final L1PcInstance pc, final int npcId, final int size) {
        int objectId, count;
        L1ItemInstance item;
        final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
        try {
            if (clan != null) {
                for (int i = 0; i < size; i++) {
                    objectId = this.readD();
                    count = Math.max(0, this.readD());
                    if (count <= 0) {
                        _log.error("要求血盟倉庫取出傳回數量小於等於0: " + pc.getName() + (pc.getNetConnection().kick()));
                        break;
                    }
                    item = clan.getDwarfForClanInventory().getItem(objectId);
                    if (item == null) {
                        _log.error("血盟倉庫取出數據異常(物品為空): " + pc.getName() + "/" + pc.getNetConnection().hashCode());
                        break;
                    }
                    if (!DwarfForClanReading.get().getUserItems(pc.getClanname(), objectId, count)) {
                        _log.error("血盟倉庫取出數據異常(該倉庫指定數據有誤): " + pc.getName() + "/" + pc.getNetConnection().hashCode());
                        break;
                    }
                    if (item.getItemId() == 44070 && ServerGmCommandTable.tradeControl.contains(objectId) && pc.getInventory().checkItem(44070)) {// 限制轉移物品
                        pc.sendPackets(new S_ServerMessage("獎勵物品無法轉移"));
                        _log.error("角色專屬倉庫取出獎勵物品異常: " + pc.getName() + ":" + objectId);
                        break;
                    }
                    L1ItemInstance wareitem = pc.getInventory().findItemId(44070);
                    if (wareitem != null && item.getItemId() == 44070 && ServerGmCommandTable.tradeControl.contains(wareitem.getId())) {
                        pc.sendPackets(new S_ServerMessage("獎勵物品無法轉移"));
                        _log.error("角色專屬倉庫取出獎勵物品異常: " + pc.getName() + ":" + objectId);
                        break;
                    }
                    if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) { // 容量重量確認及送信
                        if (pc.getInventory().consumeItem(L1ItemId.ADENA, 30)) {
                            clan.getDwarfForClanInventory().tradeItem(item, count, pc.getInventory());
                            clan.getDwarfForClanInventory().writeHistory(pc, item, count, 1); // 血盟倉庫領出紀錄
                            //血盟倉庫取出記錄
                            WriteLogTxt.Recording("血盟倉庫取出記錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【" + pc.getName() + "】帳號:【" + pc.getAccountName() + "】血盟倉庫取出【" + "+" + item.getEnchantLevel() + " " + item.getItem().getName() + "】 物品【" + item.getCount() + "】個" + "OBJID:" + item.getId() + "】.");
                        } else {
                            pc.sendPackets(new S_ServerMessage(189)); // 189
                            // \f1金幣不足。
                            break;
                        }
                    } else {
                        pc.sendPackets(new S_ServerMessage(270)); // \f1持重取引。
                        break;
                    }
                    RecordTable.get().recordeWarehouse_clan(pc.getName(), "領取", "盟倉", item.getAllName(), count, item
                            .getId(), pc.getIp());
                }
                clan.setWarehouseUsingChar(0); // 解除盟倉使用狀態

            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 血盟倉庫存入
     *
     * @param pc
     * @param npcId
     * @param size
     */
    private void mode_warehouse_clan_in(final L1PcInstance pc, final int npcId, final int size) {
        int objectId, count;
        final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
        try {
            if (clan != null) { // 所屬
                for (int i = 0; i < size; i++) {
                    objectId = this.readD();
                    count = Math.max(0, this.readD());
                    if (count <= 0) {
                        _log.error("要求血盟倉庫存入傳回數量小於等於0: " + pc.getName() + (pc.getNetConnection().kick()));
                        break;
                    }
                    final L1Object object = pc.getInventory().getItem(objectId);
                    if (object == null) {
                        _log.error("人物背包資料取出數據異常(物品為空): " + pc.getName() + "/" + pc.getNetConnection().hashCode());
                        break;
                    }
                    if (!CharItemsReading.get().getUserItems(pc.getId(), objectId, count)) {
                        _log.error("人物背包資料取出數據異常(該倉庫指定數據有誤): " + pc.getName() + "/" + pc.getNetConnection().hashCode());
                        break;
                    }
                    final L1ItemInstance item = (L1ItemInstance) object;
                    if (!item.getItem().isTradable()) {
                        // 210 \f1%0%d是不可轉移的…
                        pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
                        break;
                    }
                    if (item.get_time() != null) {
                        // \f1%0%d是不可轉移的…
                        pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
                        break;
                    }
                    if (ItemRestrictionsTable.RESTRICTIONS.contains(item.getItemId())) {
                        // \f1%0%d是不可轉移的…
                        pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
                        break;
                    }
                    if (ServerGmCommandTable.tradeControl.contains(item.getId())) {// 限制轉移物品
                        pc.sendPackets(new S_ServerMessage("獎勵物品無法轉移"));
                        return;
                    }
                    if (item.getItemId() == 80033 && pc.getLevel() < ConfigOtherSet2.R_80033) {
                        pc.sendPackets(new S_ServerMessage("預防作弊啟動，85級以下無法做此動作"));
                        return;
                    }
                    // 寵物
                    final Object[] petlist = pc.getPetList().values().toArray();
                    for (final Object petObject : petlist) {
                        if (petObject instanceof L1PetInstance) {
                            final L1PetInstance pet = (L1PetInstance) petObject;
                            if (item.getId() == pet.getItemObjId()) {
                                // 210 \f1%0%d是不可轉移的…
                                pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
                                break;
                            }
                        }
                    }
                    // 取回娃娃
                    if (pc.getDoll(item.getId()) != null) {
                        // 1,181：這個魔法娃娃目前正在使用中。
                        pc.sendPackets(new S_ServerMessage(1181));
                        break;
                    }
                    // 取回娃娃
                    if (pc.getDoll2(item.getId()) != null) {
                        // 1,181：這個魔法娃娃目前正在使用中。
                        pc.sendPackets(new S_ServerMessage(1181));
                        break;
                    }
                    // 超級娃娃
                    if (pc.get_power_doll() != null) {
                        if (pc.get_power_doll().getItemObjId() == item.getId()) {
                            // 1,181：這個魔法娃娃目前正在使用中。
                            pc.sendPackets(new S_ServerMessage(1181));
                            break;
                        }
                    }
                    if (clan.getDwarfForClanInventory().checkAddItemToWarehouse(item, count, L1Inventory.WAREHOUSE_TYPE_CLAN) == L1Inventory.SIZE_OVER) {
                        pc.sendPackets(new S_ServerMessage(75)); // \f1以上置場所。
                        break;
                    }
                    RecordTable.get().recordeWarehouse_clan(pc.getName(), "領取", "盟倉", item.getAllName(), count, item
                            .getId(), pc.getIp());
                    pc.getInventory().tradeItem(objectId, count, clan.getDwarfForClanInventory());
                    clan.getDwarfForClanInventory().writeHistory(pc, item, count, 0); // 血盟倉庫存入紀錄
                    // pc.turnOnOffLight();
                    //血盟倉庫存入記錄
                    WriteLogTxt.Recording("血盟倉庫存入記錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【" + pc.getName() + "】帳號:【" + pc.getAccountName() + "】血盟倉庫存入【" + "+" + item.getEnchantLevel() + " " + item.getItem().getName() + "】 物品【" + item.getCount() + "】個" + "OBJID:" + item.getId() + "】.");
                }
                clan.setWarehouseUsingChar(0); // 解除盟倉使用狀態
            } else {
                pc.sendPackets(new S_ServerMessage(208)); // \f1血盟倉庫使用血盟加入。
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 帳號倉庫取出 XXX
     *
     * @param pc
     * @param npcId
     * @param size
     */
    private void mode_warehouse_out(final L1PcInstance pc, final int npcId, final int size) {
        int objectId, count;
        L1ItemInstance item;
        for (int i = 0; i < size; i++) {
            objectId = this.readD();
            count = Math.max(0, this.readD());
            if (count <= 0) {
                _log.error("要求帳號倉庫取出傳回數量小於等於0: " + pc.getName() + (pc.getNetConnection().kick()));
                break;
            }
            item = pc.getDwarfInventory().getItem(objectId);
            if (item == null) {
                _log.error("帳號倉庫取出數據異常(物品為空): " + pc.getName() + "/" + pc.getNetConnection().hashCode());
                break;
            }
            if (!DwarfReading.get().getUserItems(pc.getAccountName(), objectId, count)) {
                _log.error("帳號倉庫取出數據異常(該倉庫指定數據有誤): " + pc.getName() + "/" + pc.getNetConnection().hashCode());
                break;
            }
            if (item.getItemId() == 44070 && ServerGmCommandTable.tradeControl.contains(objectId) && pc.getInventory().checkItem(44070)) {// 限制轉移物品
                pc.sendPackets(new S_ServerMessage("獎勵物品無法轉移"));
                _log.error("角色專屬倉庫取出獎勵物品異常: " + pc.getName() + ":" + objectId);
                break;
            }
            L1ItemInstance wareitem = pc.getInventory().findItemId(44070);
            if (wareitem != null && item.getItemId() == 44070 && ServerGmCommandTable.tradeControl.contains(wareitem.getId())) {
                pc.sendPackets(new S_ServerMessage("獎勵物品無法轉移"));
                _log.error("角色專屬倉庫取出獎勵物品異常: " + pc.getName() + ":" + objectId);
                break;
            }
            if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {// 容量重量確認
                if (pc.getInventory().consumeItem(L1ItemId.ADENA, 30)) {
                    pc.getDwarfInventory().tradeItem(item, count, pc.getInventory());
                    RecordTable.get().recordeWarehouse_pc(pc.getName(), "領取", "個倉", item.getAllName(), count, item.getId(), pc
                            .getIp());
                    //帳號倉庫取出記錄
                    WriteLogTxt.Recording("帳號倉庫取出記錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【" + pc.getName() + "】帳號:【" + pc.getAccountName() + "】帳號倉庫取出【" + "+" + item.getEnchantLevel() + " " + item.getItem().getName() + "】 物品【" + item.getCount() + "】個" + "OBJID:" + item.getId() + "】.");
                } else {
                    // \f1金幣不足。
                    pc.sendPackets(new S_ServerMessage(189));
                    break;
                }
            } else {
                // 270 \f1當你負擔過重時不能交易。
                pc.sendPackets(new S_ServerMessage(270));
                break;
            }
        }
    }

    /**
     * 帳號倉庫存入
     *
     * @param pc
     * @param npcId
     * @param size
     */
    private void mode_warehouse_in(final L1PcInstance pc, final int npcId, final int size) {
        int objectId, count;
        for (int i = 0; i < size; i++) {
            objectId = this.readD();
            count = Math.max(0, this.readD());
            if (count <= 0) {
                _log.error("要求帳號倉庫存入傳回數量小於等於0: " + pc.getName() + (pc.getNetConnection().kick()));
                break;
            }
            final L1Object object = pc.getInventory().getItem(objectId);
            if (object == null) {
                _log.error("人物背包資料取出數據異常(物品為空): " + pc.getName() + "/" + pc.getNetConnection().hashCode());
                break;
            }
            if (!CharItemsReading.get().getUserItems(pc.getId(), objectId, count)) {
                _log.error("人物背包資料取出數據異常(該倉庫指定數據有誤): " + pc.getName() + "/" + pc.getNetConnection().hashCode());
                break;
            }
            final L1ItemInstance item = (L1ItemInstance) object;
            if (ServerGmCommandTable.tradeControl.contains(item.getId())) {// 限制轉移物品
                pc.sendPackets(new S_ServerMessage("獎勵物品無法轉移"));
                return;
            }
            if (item.getCount() <= 0) {
                break;
            }
            if (!item.getItem().isTradable()) {
                // 210 \f1%0%d是不可轉移的…
                pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
                break;
            }
            if (item.get_time() != null) {
                // \f1%0%d是不可轉移的…
                pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
                break;
            }
            if (item.getItemId() == 80033 && pc.getLevel() < ConfigOtherSet2.R_80033) {
                pc.sendPackets(new S_ServerMessage("預防作弊啟動，85級以下無法做此動作"));
                return;
            }
            // 寵物
            final Object[] petlist = pc.getPetList().values().toArray();
            for (final Object petObject : petlist) {
                if (petObject instanceof L1PetInstance) {
                    final L1PetInstance pet = (L1PetInstance) petObject;
                    if (item.getId() == pet.getItemObjId()) {
                        // 210 \f1%0%d是不可轉移的…
                        pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
                        break;
                    }
                }
            }
            // 取回娃娃
            if (pc.getDoll(item.getId()) != null) {
                // 1,181：這個魔法娃娃目前正在使用中。
                pc.sendPackets(new S_ServerMessage(1181));
                break;
            }
            // 取回娃娃
            if (pc.getDoll2(item.getId()) != null) {
                // 1,181：這個魔法娃娃目前正在使用中。
                pc.sendPackets(new S_ServerMessage(1181));
                break;
            }
            // 超級娃娃
            if (pc.get_power_doll() != null) {
                if (pc.get_power_doll().getItemObjId() == item.getId()) {
                    // 1,181：這個魔法娃娃目前正在使用中。
                    pc.sendPackets(new S_ServerMessage(1181));
                    break;
                }
            }
            if (pc.getDwarfInventory().checkAddItemToWarehouse(item, count, L1Inventory.WAREHOUSE_TYPE_PERSONAL) == L1Inventory.SIZE_OVER) {
                pc.sendPackets(new S_ServerMessage(75)); // \f1以上置場所。
                break;
            }
            if (pc.getDwarfInventory().checkAddItemToWarehouse(item, count, L1Inventory.WAREHOUSE_TYPE_PERSONAL) == L1Inventory.WAREHOUSE_OVER) {
                pc.sendPackets(new S_ServerMessage(item.getName() + "此物品無法存入倉庫")); // \f1以上置場所。
                break;
            }
            //帳號倉庫存入記錄
            WriteLogTxt.Recording("帳號倉庫存入記錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【" + pc.getName() + "】帳號:【" + pc.getAccountName() + "】帳號倉庫存入【" + "+" + item.getEnchantLevel() + " " + item.getItem().getName() + "】 物品【" + item.getCount() + "】個" + "OBJID:" + item.getId() + "】.");
            pc.getInventory().tradeItem(objectId, count, pc.getDwarfInventory());
            RecordTable.get().recordeWarehouse_pc(pc.getName(), "存入", "個倉", item.getAllName(), count, item.getId(), pc
                    .getIp());
            // pc.turnOnOffLight();
        }
    }

    /**
     * 角色專屬倉庫取出 XXX
     *
     * @param pc
     * @param npcId
     * @param size
     */
    private void mode_warehouse_cha_out(final L1PcInstance pc, final int npcId, final int size) {
        int objectId, count;
        L1ItemInstance item;
        for (int i = 0; i < size; i++) {
            objectId = this.readD();
            count = Math.max(0, this.readD());
            if (count <= 0) {
                _log.error("要求角色專屬倉庫取出傳回數量小於等於0: " + pc.getName() + (pc.getNetConnection().kick()));
                break;
            }
            item = pc.getDwarfForChaInventory().getItem(objectId);
            if (item == null) {
                _log.error("角色專屬倉庫取出數據異常(物品為空): " + pc.getName() + "/" + pc.getNetConnection().hashCode());
                break;
            }
            if (item.getItemId() == 44070 && ServerGmCommandTable.tradeControl.contains(objectId) && pc.getInventory().checkItem(44070)) {// 限制轉移物品
                pc.sendPackets(new S_ServerMessage("獎勵物品無法轉移"));
                _log.error("角色專屬倉庫取出獎勵物品異常: " + pc.getName() + ":" + objectId);
                break;
            }
            L1ItemInstance wareitem = pc.getInventory().findItemId(44070);
            if (wareitem != null && item.getItemId() == 44070 && ServerGmCommandTable.tradeControl.contains(wareitem.getId())) {
                pc.sendPackets(new S_ServerMessage("獎勵物品無法轉移"));
                _log.error("角色專屬倉庫取出獎勵物品異常: " + pc.getName() + ":" + objectId);
                break;
            }
            if (!DwarfForChaReading.get().getUserItems(pc.getName(), objectId, count)) {
                _log.error("角色專屬倉庫取出數據異常(該倉庫指定數據有誤): " + pc.getName() + "/" + pc.getNetConnection().hashCode());
                break;
            }
            if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {// 容量重量確認
                if (pc.getInventory().consumeItem(L1ItemId.ADENA, 30)) {
                    RecordTable.get().recordWarehouse_char_pc(pc.getName(), "領取", "角倉", item.getAllName(), count, item.getId(), pc
                            .getIp());
                    pc.getDwarfForChaInventory().tradeItem(item, count, pc.getInventory());
                    //角色倉庫取出記錄
                    WriteLogTxt.Recording("角色倉庫取出記錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【" + pc.getName() + "】帳號:【" + pc.getAccountName() + "】角色倉庫取出【" + "+" + item.getEnchantLevel() + " " + item.getItem().getName() + "】 物品【" + item.getCount() + "】個" + "OBJID:" + item.getId() + "】.");
                } else {
                    // \f1金幣不足。
                    pc.sendPackets(new S_ServerMessage(189));
                    break;
                }
            } else {
                // 270 \f1當你負擔過重時不能交易。
                pc.sendPackets(new S_ServerMessage(270));
                break;
            }
        }
    }

    /**
     * 角色專屬倉庫存入
     *
     * @param pc
     * @param npcId
     * @param size
     */
    private void mode_warehouse_cha_in(final L1PcInstance pc, final int npcId, final int size) {
        int objectId, count;
        for (int i = 0; i < size; i++) {
            objectId = this.readD();
            count = Math.max(0, this.readD());
            if (count <= 0) {
                _log.error("要求角色專屬倉庫存入傳回數量小於等於0: " + pc.getName() + (pc.getNetConnection().kick()));
                break;
            }
            final L1Object object = pc.getInventory().getItem(objectId);
            if (object == null) {
                _log.error("人物背包資料取出數據異常(物品為空): " + pc.getName() + "/" + pc.getNetConnection().hashCode());
                break;
            }
            if (!CharItemsReading.get().getUserItems(pc.getId(), objectId, count)) {
                _log.error("人物背包資料取出數據異常(該人物背包指定數據有誤): " + pc.getName() + "/" + pc.getNetConnection().hashCode());
                break;
            }
            final L1ItemInstance item = (L1ItemInstance) object;
            if (ServerGmCommandTable.tradeControl.contains(item.getId())) {// 限制轉移物品
                pc.sendPackets(new S_ServerMessage("獎勵物品無法轉移"));
                return;
            }
            if (item.getCount() <= 0) {
                break;
            }
            if (!item.getItem().isTradable()) {
                // 210 \f1%0%d是不可轉移的…
                pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
                break;
            }
            if (item.get_time() != null) {
                // \f1%0%d是不可轉移的…
                pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
                break;
            }
            // 寵物
            final Object[] petlist = pc.getPetList().values().toArray();
            for (final Object petObject : petlist) {
                if (petObject instanceof L1PetInstance) {
                    final L1PetInstance pet = (L1PetInstance) petObject;
                    if (item.getId() == pet.getItemObjId()) {
                        // 210 \f1%0%d是不可轉移的…
                        pc.sendPackets(new S_ServerMessage(210, item.getItem().getNameId()));
                        break;
                    }
                }
            }
            // 取回娃娃
            if (pc.getDoll(item.getId()) != null) {
                // 1,181：這個魔法娃娃目前正在使用中。
                pc.sendPackets(new S_ServerMessage(1181));
                break;
            }
            // 取回娃娃
            if (pc.getDoll2(item.getId()) != null) {
                // 1,181：這個魔法娃娃目前正在使用中。
                pc.sendPackets(new S_ServerMessage(1181));
                break;
            }
            // 超級娃娃
            if (pc.get_power_doll() != null) {
                if (pc.get_power_doll().getItemObjId() == item.getId()) {
                    // 1,181：這個魔法娃娃目前正在使用中。
                    pc.sendPackets(new S_ServerMessage(1181));
                    break;
                }
            }
            if (pc.getDwarfForChaInventory().checkAddItemToWarehouse(item, count, L1Inventory.WAREHOUSE_TYPE_PERSONAL) == L1Inventory.SIZE_OVER) {
                pc.sendPackets(new S_ServerMessage(75)); // \f1以上置場所。
                break;
            }
            //角色倉庫存入記錄
            WriteLogTxt.Recording("角色倉庫存入記錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【" + pc.getName() + "】帳號:【" + pc.getAccountName() + "】角色倉庫存入【" + "+" + item.getEnchantLevel() + " " + item.getItem().getName() + "】 物品【" + item.getCount() + "】個" + "OBJID:" + item.getId() + "】.");
            RecordTable.get().recordWarehouse_char_pc(pc.getName(), "存入", "角倉", item.getAllName(), count, item.getId(), pc
                    .getIp());
            pc.getInventory().tradeItem(objectId, count, pc.getDwarfForChaInventory());
            // pc.turnOnOffLight();
        }
    }

    /**
     * 賣出物品給個人商店
     *
     * @param pc       賣出物品的玩家
     * @param targetPc 設置商店的玩家
     * @param size     數量
     */
    private void mode_sellpc(final L1PcInstance pc, final L1PcInstance targetPc, final int size) {
        int count;
        int order;
        ArrayList<L1PrivateShopBuyList> buyList;
        L1PrivateShopBuyList psbl;
        int itemObjectId;
        L1ItemInstance item;// 賣出物品
        int buyItemObjectId;
        long buyPrice;
        int buyTotalCount;
        int buyCount;
        // L1ItemInstance targetItem;
        final boolean[] isRemoveFromList = new boolean[8];
        // 正在執行個人商店交易
        if (targetPc.isTradingInPrivateShop()) {
            return;
        }
        // 該地圖是否可擺設商店  src017 src018
        final int adenaItemId = targetPc.getMap().isUsableShop();
        if (adenaItemId <= 0 || ItemTable.get().getTemplate(adenaItemId) == null) {
            return;
        }
        targetPc.setTradingInPrivateShop(true);
        buyList = targetPc.getBuyList();
        for (int i = 0; i < size; i++) {
            itemObjectId = this.readD();
            count = this.readCH();
            count = Math.max(0, count);
            if (count <= 0) {
                _log.error("要求列表物品取得傳回數量小於等於0: " + pc.getName() + (pc.getNetConnection().kick()));
                break;
            }
            order = this.readC();
            item = pc.getInventory().getItem(itemObjectId);
            if (item == null) {
                continue;
            }
            if (item.get_time() != null) {
                // 具有存在期限
                continue;
            }
            //System.out.println("物品名稱:"+item.getName());
            psbl = null;
            for (int k = 0; k < buyList.size(); k++) {
                psbl = buyList.get(k);
                //System.out.println("物品名稱檢查:"+psbl.getBuyItemId()+","+item.getItemId());
                if (psbl.getBuyItemId() == item.getItemId()) {
                    order = k;
                    break;
                }
            }
            buyItemObjectId = psbl.getItemObjectId();// 傳回要購買的物品OBJID(商店玩家身上)
            buyPrice = psbl.getBuyPrice();// 回收價格
            buyTotalCount = psbl.getBuyTotalCount(); // 預計買入數量
            buyCount = psbl.getBuyCount(); // 買入數量累計
            if (count > buyTotalCount - buyCount) {
                count = buyTotalCount - buyCount;
            }
            if (item.isEquipped()) {
                // 無法販賣裝備中的道具。
                pc.sendPackets(new S_ServerMessage(905));
                continue;
            }
            if (item.getItemId() == 80033) {// 限制轉移物品
                pc.sendPackets(new S_ServerMessage("銀幣無法掛賣轉移"));
                continue;
            }
            final L1ItemInstance srcItem = targetPc.getInventory().getItem(buyItemObjectId);
            if (srcItem.get_time() != null) {
                // 具有存在期限
                continue;
            }
            if (srcItem.getItemId() == 80033) {// 限制轉移物品
                pc.sendPackets(new S_ServerMessage("銀幣無法掛賣轉移"));
                continue;
            }
            if (ServerGmCommandTable.tradeControl.contains(buyItemObjectId) || ServerGmCommandTable.tradeControl.contains(itemObjectId)) {// 限制轉移物品
                pc.sendPackets(new S_ServerMessage("獎勵物品無法轉移"));
                _log.error("角色販賣獎勵物品異常: " + pc.getName() + ":" + item.getId());
                continue;
            }
            if ((item.getItemId() == srcItem.getItemId()) && (item.getEnchantLevel() == srcItem.getEnchantLevel())) {
                if (targetPc.getInventory().checkAddItem(item, count) == L1Inventory.OK) { // 容量重量確認及送信
                    for (int j = 0; j < count; j++) {
                        if (buyPrice * j > 2000000000) {
                            // 總共販賣價格無法超過 %d金幣。
                            targetPc.sendPackets(new S_ServerMessage(904, "2000000000"));
                            return;
                        }
                    }
                    // 判斷回收者身上金幣數量是否足夠
                    if (targetPc.getInventory().checkItem(adenaItemId, count * buyPrice)) {
                        // 取回金幣資料
                        final L1ItemInstance adena = targetPc.getInventory().findItemId(adenaItemId);
                        // 金幣足夠
                        if (adena != null) {
                            // 出售者物件不足
                            if (item.getCount() < count) {
                                // 989：無法與開設個人商店的玩家進行交易。
                                pc.sendPackets(new S_ServerMessage(989));
                                _log.error("可能使用bug進行交易 人物名稱(賣出道具給予個人商店/交易數量不吻合): " + pc.getName() + " objid:" + pc.getId());
                                continue;
                            }
                            // 賣出物品給個人商店紀錄
                            OtherUserSellReading.get().add(item.getItem().getName(), item.getId(), (int) buyPrice, count, pc.getId(), pc.getName(), targetPc.getId(), targetPc.getName());
                            // 移動回收者物件
                            targetPc.getInventory().tradeItem(adena, (count * buyPrice), pc.getInventory());
                            // 移動出售者物件
                            pc.getInventory().tradeItem(item, count, targetPc.getInventory());
                            // 產生訊息
                            final String message = item.getItem().getName() + " (" + String.valueOf(count) + ")";
                            // 877 將 %1%o 賣給 %0。
                            pc.sendPackets(new S_ServerMessage(877, targetPc.getName(), message));
                            targetPc.sendPackets(new S_GmMessage("收購" + message + "成功"));
                            psbl.setBuyCount(count + buyCount);
                            buyList.set(order, psbl);
                            if (psbl.getBuyCount() == psbl.getBuyTotalCount()) { // 購買數量已達到
                                isRemoveFromList[order] = true;
                            }
                        }
                    } else {
                        // \f1金幣不足。
                        targetPc.sendPackets(new S_ServerMessage(189));
                        break;
                    }
                } else {
                    // \f1對方攜帶的物品過重，無法交易。
                    pc.sendPackets(new S_ServerMessage(271));
                    break;
                }
                // 交易條件不吻合
            } else {
                _log.error("可能使用bug進行交易 人物名稱(賣出道具給予個人商店/交易條件不吻合): " + pc.getName() + " objid:" + pc.getId());
                return;
            }
        }
        // 買切末尾削除
        for (int i = 7; i >= 0; i--) {
            if (isRemoveFromList[i]) {
                buyList.remove(i);
            }
        }
        targetPc.setTradingInPrivateShop(false);
    }

    /**
     * 買入個人商店物品
     *
     * @param pc       買入商店物品的玩家
     * @param targetPc 設置商店的玩家
     * @param size     買入物品種類清單
     */
    private void mode_buypc(final L1PcInstance pc, final L1PcInstance targetPc, final int size) {
        int order;
        int count;
        int price;
        ArrayList<L1PrivateShopSellList> sellList;
        L1PrivateShopSellList pssl;
        int itemObjectId;
        int sellPrice;
        int sellTotalCount;
        int sellCount;
        L1ItemInstance item;
        final boolean[] isRemoveFromList = new boolean[8];
        // 正在執行交易
        if (targetPc.isTradingInPrivateShop()) {
            return;
        }
        //該地圖是否可擺設商店  src017 src018
        final int adenaItemId = targetPc.getMap().isUsableShop();
        if (adenaItemId <= 0 || ItemTable.get().getTemplate(adenaItemId) == null) {
            return;
        }
        // 擺放個人商店的全部物品種類清單
        sellList = targetPc.getSellList();
        synchronized (sellList) {
            // 售出數量異常
            if (pc.getPartnersPrivateShopItemCount() != sellList.size()) {
                return;
            }
            targetPc.setTradingInPrivateShop(true);
            for (int i = 0; i < size; i++) { // 購入予定商品
                order = this.readD();
                count = Math.max(0, this.readD());
                if (count <= 0) {
                    _log.error("要求買入個人商店物品傳回數量小於等於0: " + pc.getName() + (pc.getNetConnection().kick()));
                    break;
                }
                // 取回商店賣出的道具資訊
                pssl = sellList.get(order);
                //System.out.println(order);
                itemObjectId = pssl.getItemObjectId();// 物品objid
                sellPrice = pssl.getSellPrice();// 售價
                sellTotalCount = pssl.getSellTotalCount(); // 預計賣出的數量
                sellCount = pssl.getSellCount(); // 已經賣出數量的累計
                // 取回賣出物品資料
                item = targetPc.getInventory().getItem(itemObjectId);
                if (item == null) {
                    // 該物件為空的狀態
                    continue;
                }
                if (item.get_time() != null) {
                    // 具有存在期限
                    continue;
                }
                if (item.getItemId() == 80033) {// 限制轉移物品
                    pc.sendPackets(new S_ServerMessage("銀幣無法掛賣轉移"));
                    continue;
                }
                // 賣出物品 買方選取超出數量
                if (count > sellTotalCount - sellCount) {
                    count = sellTotalCount - sellCount;
                }
                // 賣出物品數量為零
                if (count <= 0) {
                    continue;
                }
                if (ServerGmCommandTable.tradeControl.contains(itemObjectId)) {// 限制轉移物品
                    pc.sendPackets(new S_ServerMessage("獎勵物品無法轉移"));
                    _log.error("角色販賣獎勵物品異常: " + pc.getName() + ":" + item.getId());
                    continue;
                }
                if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) { // 容量重量確認
                    for (int j = 0; j < count; j++) { // 計算收入
                        if (sellPrice * j > 2000000000) {
                            // 總共販賣價格無法超過 %d金幣。
                            pc.sendPackets(new S_ServerMessage(904, "2000000000"));
                            targetPc.setTradingInPrivateShop(false);
                            return;
                        }
                    }
                    // 所需花費
                    price = count * sellPrice;
                    // 取回金幣資料
                    final L1ItemInstance adena = pc.getInventory().findItemId(adenaItemId);
                    if (adena == null) {
                        if (adenaItemId == L1ItemId.ADENA) {
                            // \f1金幣不足。
                            pc.sendPackets(new S_ServerMessage(189));
                        } else {
                            // \f1%0不足%s。 added by terry0412
                            pc.sendPackets(new S_ServerMessage(337, ItemTable.get().getTemplate(adenaItemId).getName()));
                        }
                        continue;
                    }
                    // 買入物品玩家金幣數量不足
                    if (adena.getCount() < price) {
                        if (adenaItemId == L1ItemId.ADENA) {
                            // \f1金幣不足。
                            pc.sendPackets(new S_ServerMessage(189));
                        } else {
                            // \f1%0不足%s。 added by terry0412
                            pc.sendPackets(new S_ServerMessage(337, ItemTable.get().getTemplate(adenaItemId).getName()));
                        }
                        continue;
                    }
                    // 商店玩家對像對像不為空
                    if (targetPc != null) {
                        // 賣出物品數量不足
                        if (item.getCount() < count) {
                            // 989：無法與開設個人商店的玩家進行交易。
                            pc.sendPackets(new S_ServerMessage(989));
                            continue;
                        }
                        // 買入個人商店物品紀錄
                        OtherUserBuyReading.get().add(item.getItem().getName(), item.getId(), sellPrice, count, pc.getId(), pc.getName(), targetPc.getId(), targetPc.getName());
                        // 轉移道具給予購買者
                        targetPc.getInventory().tradeItem(item, count, pc.getInventory());
                        // 把花費的金幣轉移給設置為商店的玩家
                        pc.getInventory().tradeItem(adena, price, targetPc.getInventory());
                        // 產生訊息
                        final String message = item.getItem().getName() + " (" + String.valueOf(count) + ")";
                        // 877 將 %1%o 賣給 %0。
                        targetPc.sendPackets(new S_ServerMessage(877, pc.getName(), message));
                        pc.sendPackets(new S_GmMessage("購買" + message + "成功"));
                        // 設置物品已賣出數量
                        pssl.setSellCount(count + sellCount);
                        sellList.set(order, pssl);
                        // 販賣物件已售完
                        if (pssl.getSellCount() == pssl.getSellTotalCount()) {
                            isRemoveFromList[order] = true;
                        }
                    }
                } else {
                    // \f1當你負擔過重時不能交易。
                    pc.sendPackets(new S_ServerMessage(270));
                    break;
                }
            }
            // 該道具販賣結束移出販賣清單
            for (int i = 7; i >= 0; i--) {
                if (isRemoveFromList[i]) {
                    sellList.remove(i);
                }
            }
            targetPc.setTradingInPrivateShop(false);
        }
    }

    /**
     * 購買虛擬商人物品
     * 虛擬假人
     *
     * @param pc   買入者
     * @param de
     * @param size
     */
    private void mode_buyde(L1PcInstance pc, L1DeInstance de, int size) {
        Map<L1ItemInstance, Integer> sellList = de.get_sellList();
        Queue<L1ItemInstance> removeList = new ConcurrentLinkedQueue<L1ItemInstance>();
        //該地圖是否可擺設商店
        final int adenaItemId = de.getMap().isUsableShop(); // 取回地圖商店貨幣
        if (adenaItemId <= 0 || ItemTable.get().getTemplate(adenaItemId) == null) {
            return;
        }
        for (int i = 0; i < size; i++) { // 購入予定の商品
            int order = this.readD();
            int count = Math.max(0, this.readD());
            if (count <= 0) {
                _log.error("要求買入個人商店物品傳回數量小於等於0: " + pc.getName() + (pc.getNetConnection().kick()));
                break;
            }
            Map<Integer, L1ItemInstance> list = pc.get_otherList().DELIST;
            for (int key : list.keySet()) {
                if (order == key) {
                    L1ItemInstance item = list.get(key);
                    if (item == null) {
                        // 該物件為空的狀態
                        continue;
                    }
                    if (item.getCount() < count) {
                        continue;
                    }
                    if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
                        // 取回售價
                        long price = count * sellList.get(item);// 所需花費
                        if (pc.getInventory().consumeItem(adenaItemId, price)) {
                            // 買入物品
                            if (item.isStackable()) { // 可以堆疊
                                pc.getInventory().storeItem(item.getItemId(), count);
                                final String message = item.getItem().getName() + " (" + String.valueOf(count) + ")";
                                pc.sendPackets(new S_GmMessage("購買" + message + "成功"));
                                long newCount = item.getCount() - count;
                                if (newCount > 0) {
                                    item.setCount(newCount);
                                } else {
                                    removeList.add(item);
                                }
                            } else {
                                pc.getInventory().storeTradeItem(item);
                                final String message = item.getItem().getName() + " (" + String.valueOf(count) + ")";
                                pc.sendPackets(new S_GmMessage("購買" + message + "成功"));
                                removeList.add(item);
                            }
                        } else {
                            if (adenaItemId == L1ItemId.ADENA) {
                                // \f1金幣不足。
                                pc.sendPackets(new S_ServerMessage(189));
                            } else {
                                // \f1%0不足%s。 added by terry0412
                                pc.sendPackets(new S_ServerMessage(337, ItemTable.get().getTemplate(adenaItemId).getName()));
                            }
                            break;
                        }
                    } else {
                        // 270 \f1當你負擔過重時不能交易。
                        pc.sendPackets(new S_ServerMessage(270));
                        break;
                    }
                }
            }
        }
        if (!removeList.isEmpty()) {
            for (final Iterator<L1ItemInstance> iter = removeList.iterator(); iter.hasNext(); ) {
                final L1ItemInstance reitem = iter.next();// 返回迭代的下一個元素。
                // 從迭代器指向的 collection 中移除迭代器返回的最後一個元素
                iter.remove();
                sellList.remove(reitem);
            }
        }
        // 清空臨時資料
        pc.get_otherList().DELIST.clear();
    }

    /**
     * 玩家賣出物品給予商店
     *
     * @param pc
     * @param npcId
     * @param size
     */
    private void mode_sell(final L1PcInstance pc, final int npcId, final int size) {
        final L1Shop shop = ShopTable.get().get(npcId);
        if (shop != null) {
            final L1ShopSellOrderList orderList = shop.newSellOrderList(pc);
            for (int i = 0; i < size; i++) {
                final int objid = this.readD();
                final int count = Math.max(0, this.readD());// 數量
                if (count <= 0) {
                    _log.error("要求列表物品取得傳回數量小於等於0: " + pc.getName() + ":" + (pc.getNetConnection().kick()));
                    continue;
                }
                orderList.add(objid, count);
            }
            shop.buyItems(orderList, pc);
            // 沒有販售資料
        } else {
            // 關閉對話窗
            pc.sendPackets(new S_CloseList(pc.getId()));
        }
    }

    /**
     * 玩家從商店買入物品
     *
     * @param pc
     * @param npcId
     * @param size
     */
    private void mode_buy(final L1PcInstance pc, final int npcId, final int size) {
        final L1Shop shop = ShopTable.get().get(npcId);
        if (shop != null) {
            final L1ShopBuyOrderList orderList = shop.newBuyOrderList();
            for (int i = 0; i < size; i++) {
                final int orderId = this.readD();
                final int count = Math.max(0, this.readD());// 數量
                if (count <= 0) {
                    _log.error("要求列表物品取得傳回數量小於等於0: " + pc.getName() + ":" + (pc.getNetConnection().kick()));
                    continue;
                }
                orderList.add(orderId, count);
            }
            shop.sellItems(pc, orderList, npcId);
            // 沒有販售資料
        } else {
            // 關閉對話窗
            pc.sendPackets(new S_CloseList(pc.getId()));
        }
    }

    private void pcBuymobtowar(L1PcInstance pc, int size, L1NpcInstance targetNpc) {
        String npcImpl = targetNpc.getNpcTemplate().getImpl();
        if ((!(npcImpl.equalsIgnoreCase("L1Merchant"))) || (targetNpc.getNpcId() != L1Config._2154)) {
            return;
        }
        for (int i = 0; i < size; ++i) {
            int order = readD();
            int count = readD();
            if (!(_Mob.get_isWaiting())) {
                return;
            }
            if (_Mob.get_isStart()) {
                return;
            }
            pc.getMobSplist().addMobItem(order, count);
        }
        pc.getMobSplist().checkMobShop();
    }

    private void pcSellmobtowar(L1PcInstance pc, int size, L1NpcInstance targetNpc) {
        String npcImpl = targetNpc.getNpcTemplate().getImpl();
        if ((!(npcImpl.equalsIgnoreCase("L1Merchant"))) || (targetNpc.getNpcId() != L1Config._2154)) {
            return;
        }
        for (int i = 0; i < size; ++i) {
            int order = readD();
            int count = readD();
            pc.getMobSplist().addSellMobItem(order, count);
        }
        pc.getMobSplist().checkMobSell();
    }

    /**
     * [原碼] 大樂透系統
     */
    private void pcSellBigHot(L1PcInstance pc, int size, L1NpcInstance targetNpc) {
        String npcImpl = targetNpc.getNpcTemplate().getImpl();
        if ((!(npcImpl.equalsIgnoreCase("L1Merchant"))) || (targetNpc.getNpcId() != L1Config._2162)) {
            return;
        }
        for (int i = 0; i < size; ++i) {
            int order = readD();
            int count = readD();
            pc.getBigHotSplist().addSellBigHotItem(order, count);
        }
        pc.getBigHotSplist().checkBigHotSell();
    }

    /**
     * @**抽抽樂**
     * @作者:冰雕寵兒
     */
    private void pcLuckylottery(final L1PcInstance pc, final int size) {
        if (pc != null) {
            boolean isOut = false;
            System.out.println("size:" + size);
            for (int i = 0; i < size; ++i) {
                final int objectId = this.readD();
                final int objectCount = this.readD();
                final L1PandoraItem pItem = pc.getPandoraInventory().FindItem(objectId);
                if (pItem != null) {
                    final L1ItemInstance newItem = ItemTable.get().createItem(pItem.getItemId());
                    if (newItem != null) {
                        newItem.setCount(pItem.getCount());
                        newItem.setEnchantLevel(pItem.getEnchantlvl());
                        if (pc.getInventory().checkAddItem(newItem, newItem.getCount()) == 0) {
                            boolean successful = pc.getPandoraInventory().deleteItem(pItem);
                            if (successful) {
                                pc.getInventory().storeItem(newItem);
                                _log.info("玩家:" + pc.getName() + "取回道具:" + newItem.getName() + "數量:" + objectCount);
                                Lucky("玩家:【 " + pc.getName() + " 】 取回道具【 " + newItem.getName() + "】數量:【" + objectCount + "】時間:" + "(" + new Timestamp(System.currentTimeMillis()) + ")。");
                                WriteLogTxt.Recording("潘朵拉抽抽樂取回紀錄", "玩家:【 " + pc.getName() + " 】 取回道具【 " + newItem.getName() + "】數量:【" + objectCount + "】.");
                                isOut = true;
                            } else {
                                pc.sendPackets(new S_ServerMessage("請截圖回報GM此問題:" + pItem.getId() + "," + pItem.getItemName()));
                            }
                        }
                    }
                }
            }
            if (isOut) {
                pc.sendPackets(new S_ServerMessage(3727));
                pc.sendPackets(new S_Luckylottery(pc.getPandoraInventory().getItems()));
            } else {
                pc.sendPackets(new S_ServerMessage(271));
            }
        }
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
