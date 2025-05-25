package com.lineage.server.clientpackets;

import com.add.BigHot.BigHotblingTimeList;
import com.add.BigHot.L1BigHotble;
import com.add.Crack.L1Thebes;
import com.add.Crack.L1Tikal;
import com.add.Game_Fight;
import com.add.Game_ZhuduiPK;
import com.add.L1Config;
import com.add.Mobbling.L1Mobble;
import com.add.system.L1Blend;
import com.add.system.L1BlendTable;
import com.lineage.DatabaseFactory;
import com.lineage.config.*;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.event.MiniGame.MiniSiege;
import com.lineage.data.event.redknight.RedKnightSystem;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.*;
import com.lineage.server.datatables.lock.CastleReading;
import com.lineage.server.datatables.lock.CharSkillReading;
import com.lineage.server.datatables.lock.HouseReading;
import com.lineage.server.datatables.lock.PetReading;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.*;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.model.npc.L1NpcHtml;
import com.lineage.server.model.npc.action.L1NpcAction;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.*;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import william.Npc_Combind;
import william.ReincarnationSkill;
import william.WilliamBuff;

import java.sql.*;
import java.util.*;

import static com.lineage.server.model.skill.L1SkillId.*;

/**
 * 要求物件對話視窗結果
 *
 * @author daien
 */
public class C_NPCAction extends ClientBasePacket {
    private static final Log _log = LogFactory.getLog(C_NPCAction.class);
    private static BigHotblingTimeList _BigHot = BigHotblingTimeList.BigHot(); // [原碼]
    // 大樂透系統
    private static Random _random = new Random();

    @Override
    public void start(final byte[] decrypt, final ClientExecutor client) {
        try {
            // System.out.println("資料載入");
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
            final int objid = this.readD();
            final String cmd = this.readS();
            int[] materials = null;
            int[] counts = null;
            int[] createitem = null;
            int[] createcount = null;
            String htmlid = null;
            String success_htmlid = null;
            String failure_htmlid = null;
            String[] htmldata = null;
            L1Npc npctemp = null;
            final L1Object obj = World.get().findObject(objid);
            if (obj == null) {
                _log.error("該OBJID編號的 NPC已經不存在世界中: " + objid);
                return;
            }
            // 命令來自於NPC
            if (obj instanceof L1NpcInstance) {
                // System.out.println("命令來自於NPC");
                final L1NpcInstance npc = (L1NpcInstance) obj;
                if (WilliamBuff.giveBuff(pc, npc, cmd)) { // NPC魔法輔助DB化系統
                    return;
                }
                npctemp = npc.getNpcTemplate();
                String s2 = null;
                try {
                    if (npctemp.get_classname().equalsIgnoreCase("other.Npc_AuctionBoard")) {
                        s2 = this.readS();
                    } else if (npctemp.get_classname().equalsIgnoreCase("other.Npc_Board")) {
                        s2 = this.readS();
                    }
                } catch (Exception ignored) {
                }
                if (obj instanceof L1PetInstance) {
                    final L1PetInstance pet = (L1PetInstance) obj;
                    pc.getActionPet().action(pet, cmd);
                    return;
                } else if (obj instanceof L1SummonInstance) {
                    final L1SummonInstance summon = (L1SummonInstance) obj;
                    pc.getActionSummon().action(summon, cmd);
                    return;
                } else {
                    final int difflocx = Math.abs(pc.getX() - npc.getX());
                    final int difflocy = Math.abs(pc.getY() - npc.getY());
                    // 5格以上距離無效
                    if ((difflocx > 5) || (difflocy > 5)) {
                        return;
                    }
                    /* 顯示可製造的物品列表 */
                    if (cmd.equalsIgnoreCase("request craft")) {
                        ShowCraftList(pc, npc);
                        return;
                    }
                    if (cmd.equalsIgnoreCase("request firecrystal")) {
                        pc.sendPackets(new S_ShopBuyListFireSmith(pc, npc));
                    }
                    /* END */
                    /* 顯示條件清單 */
                    String craftkey = npctemp.get_npcId() + cmd;// 製造命令
                    L1Blend ItemBlend = L1BlendTable.getInstance().getTemplate(craftkey);
                    if (ItemBlend != null) {
                        ItemBlend.ShowCraftHtml(pc, npc, ItemBlend);// 顯示條件清單
                        npc.set_craftkey(craftkey);// 暫存製造命令
                        return;
                    }
                    /* END */
                    /* 確認或取消製造道具 */
                    String craftkey2 = npc.get_craftkey();// 取回製造命令
                    L1Blend ItemBlend2 = L1BlendTable.getInstance().getTemplate(craftkey2);
                    if (ItemBlend2 != null) {
                        if (cmd.equalsIgnoreCase("confirm craft")) {// 確認製造道具
                            ItemBlend2.CheckCraftItem(pc, npc, ItemBlend2, 1, false);
                            return;
                        } else if (cmd.equalsIgnoreCase("cancel craft")) {// 取消製造道具
                            pc.sendPackets(new S_CloseList(pc.getId()));
                            npc.set_craftkey(null);// 清空製造命令
                            return;
                        }
                    }
                    /* END */

                    if (npc.ACTION != null) {
                        if (s2 != null && s2.length() > 0) {
                            npc.ACTION.action(pc, npc, cmd + "," + s2, 0);
                            return;
                        }
                        npc.ACTION.action(pc, npc, cmd, 0);
                        return;
                    }
                    if (Npc_Combind.forNpcQuest(cmd, pc, (L1NpcInstance) obj, ((L1NpcInstance) obj)
                            .getNpcTemplate().get_npcId(), objid)) {
                        htmlid = "";
                        return;
                    }
                    npc.onFinalAction(pc, cmd);
                }
                //				/** 紋樣系統調用cmd */
                //		String wenyang = npctemp.get_npcId() + cmd;
                //		L1WenYangCmd wenyangCmd = WenYangCmdTable.getInstance().getTemplate(wenyang);
                //		if (wenyangCmd != null) {
                //                L1WenYang wenYangd = WenYangTable.getInstance().getTemplate(wenyangCmd.getType(), wenyangCmd.getLevel());
                //                 if (wenYangd != null) {
                //                	if (wenYangd.() != 0) {
                //*                   		if (pc.getWenyangJiFen() < wenYangd.getJiFen()) {
                //                 			pc.sendPackets(new S_SystemMessage("您的積分不足"));
                //						return;
                //					}
                //
                //                  		L1WenYangJiLu wenYangJiLu = WenYangJiLuTable.getInstance().getTemplate(pc.getId());
                //                 		if (wenyangCmd.getLevel() <= wenYangJiLu.getLevel()) {
                //                  			pc.sendPackets(new S_SystemMessage("不需要升級"));
                //						return;
                ///				}
                //
                //                    		if (random.nextInt(100) < wenyangCmd.getRun()) {
                //                			wenYangJiLu.setPcid(pc.getId());
                ///                 			wenYangJiLu.setType(wenyangCmd.getType());
                //                			wenYangJiLu.setLevel(wenyangCmd.getLevel());
                //*                   			pc.setWenyangJiFen(pc.getWenyangJiFen() - wenYangd.getJiFen());
                ///                		    	pc.sendPackets(new S_SystemMessage("恭喜成功升級"+ wenYangd.getNot() +"紋樣"));
                //                  			WenYangJiLuTable.getInstance().storeItem(pc,wenYangJiLu);
                //                  			WenYangJiLuTable.getInstance().updateItem(pc,wenYangJiLu);
                //                  		} else {
                //*                   			pc.setWenyangJiFen(pc.getWenyangJiFen() - wenYangd.getJiFen());
                //                  			pc.sendPackets(new S_SystemMessage("很遺憾升級失敗"));
                //                 		}
                //                  	}
                //                 }
                //		}
                // 命令來自於PC
            } else if (obj instanceof L1PcInstance) {
                final L1PcInstance target = (L1PcInstance) obj;
                target.getAction().action(cmd, 0);
                return;
            }
            // XML化
            final L1NpcAction action = NpcActionTable.getInstance().get(cmd, pc, obj);
            if (action != null) {
                final L1NpcHtml result = action.execute(cmd, pc, obj, this.readByte());
                if (result != null) {
                    pc.sendPackets(new S_NPCTalkReturn(obj.getId(), result));
                }
                return;
            }
            /* 其他命令處理 */
            if (cmd.equalsIgnoreCase("buy")) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                if (isNpcSellOnly(npc)) {
                    return;
                }
                /* [原碼] 怪物對戰系統 */
                int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
                if (npcid == L1Config._2154) {
                    L1Mobble.getInstance().buytickets(npc, pc);
                } else {
                    // 商店販賣的物品清單
                    pc.sendPackets(new S_ShopSellList(objid));
                }
            } else if (cmd.equalsIgnoreCase("sell")) {
                int npcid = npctemp.get_npcId();
                if ((npcid == 70523) || (npcid == 70805)) {
                    htmlid = "ladar2";
                } else if ((npcid == 70537) || (npcid == 70807)) {
                    htmlid = "farlin2";
                } else if ((npcid == 70525) || (npcid == 70804)) {
                    htmlid = "lien2";
                } else if ((npcid == 50527) || (npcid == 50505) || (npcid == 50519) || (npcid == 50545) || (npcid == 50531) || (npcid == 50529) || (npcid == 50516) || (npcid == 50538) || (npcid == 50518) || (npcid == 50509) || (npcid == 50536) || (npcid == 50520) || (npcid == 50543) || (npcid == 50526) || (npcid == 50512) || (npcid == 50510) || (npcid == 50504) || (npcid == 50525) || (npcid == 50534) || (npcid == 50540) || (npcid == 50515) || (npcid == 50513) || (npcid == 50528) || (npcid == 50533) || (npcid == 50542) || (npcid == 50511) || (npcid == 50501) || (npcid == 50503) || (npcid == 50508) || (npcid == 50514) || (npcid == 50532) || (npcid == 50544) || (npcid == 50524) || (npcid == 50535) || (npcid == 50521) || (npcid == 50517) || (npcid == 50537) || (npcid == 50539) || (npcid == 50507) || (npcid == 50530) || (npcid == 50502) || (npcid == 50506) || (npcid == 50522) || (npcid == 50541) || (npcid == 50523) || (npcid == 50620) || (npcid == 50623) || (npcid == 50619) || (npcid == 50621) || (npcid == 50622) || (npcid == 50624) || (npcid == 50617) || (npcid == 50614) || (npcid == 50618) || (npcid == 50616) || (npcid == 50615) || (npcid == 50626) || (npcid == 50627) || (npcid == 50628) || (npcid == 50629) || (npcid == 50630) || (npcid == 50631)) {
                    String sellHouseMessage = sellHouse(pc, objid, npcid);
                    if (sellHouseMessage != null) {
                        htmlid = sellHouseMessage;
                    }
                } else { // 一般商人
                    /** [原碼] 怪物對戰系統 */
                    if (npcid == L1Config._2154) {
                        L1NpcInstance npc;
                        npc = (L1NpcInstance) obj;
                        L1Mobble.getInstance().selltickets(npc, pc);
                        /*   */
                    }
                    /** [原碼] 大樂透系統 */
                    else if (npcid == L1Config._2162) {
                        L1NpcInstance npc;
                        npc = (L1NpcInstance) obj;
                        L1BigHotble.getInstance().selltickets(npc, pc);
                    } else {
                        // 商店收購的物品清單
                        pc.sendPackets(new S_ShopBuyList(objid, pc));
                    }
                }
            } else if (npctemp.get_npcId() == 91002) {
                if (cmd.equalsIgnoreCase("ent")) {
                    L1PolyRace.getInstance().enterGame(pc);
                }
            } else if (cmd.equalsIgnoreCase("retrieve")) { //「個人倉庫：領取物品」
                // 限制倉庫使用等級設定
                if (pc.getLevel() >= ConfigOtherSet2.Dwarf_Level) {
                    // 具有倉庫密碼數據
                    if (client.getAccount().get_warehouse() > 0) {
                        if (isTwoLogin(pc)) { // 相同帳號檢測
                            return;
                        }
                        // 送出倉庫密碼視窗
                        pc.sendPackets(new S_ServerMessage(834));
                    } else {
                        if (isTwoLogin(pc)) { // 相同帳號檢測
                            return;
                        }
                        // 取回PC一般倉庫的數據
                        final int size = pc.getDwarfInventory().getSize();
                        if (size > 0) {
                            pc.sendPackets(new S_RetrieveList(objid, pc));
                        } else {
                            pc.sendPackets(new S_NPCTalkReturn(objid, "noitemret"));
                        }
                    }
                } else {
                    pc.sendPackets(new S_SystemMessage("您未達到倉庫使用等級，使用等級為：" + ConfigOtherSet2.Dwarf_Level));
                }
            } else if (cmd.equalsIgnoreCase("retrieve-char-1")) { //「角色專屬倉庫：領取物品」 原本"retrieve-char"
                // 限制倉庫使用等級設定
                if (pc.getLevel() >= ConfigOtherSet2.Dwarf_Level) {
                    // 具有倉庫密碼數據
                    if (client.getAccount().get_warehouse() > 0) {
                        if (isTwoLogin(pc)) { // 相同帳號檢測
                            return;
                        }
                        // 送出倉庫密碼視窗
                        pc.sendPackets(new S_ServerMessage(834));
                    } else {
                        if (isTwoLogin(pc)) { // 相同帳號檢測
                            return;
                        }
                        // 取回PC一般倉庫的數據
                        int size = pc.getDwarfForChaInventory().getItems().size(); // 角色專屬倉庫
                        if (size > 0) {
                            pc.sendPackets(new S_RetrieveChaList(objid, pc)); // 角色專屬倉庫
                        } else {
                            pc.sendPackets(new S_NPCTalkReturn(objid, "noitemret"));
                        }
                    }
                } else {
                    pc.sendPackets(new S_SystemMessage("您未達到倉庫使用等級，使用等級為：" + ConfigOtherSet2.Dwarf_Level));
                }
            } else if (cmd.equalsIgnoreCase("retrieve-elven")) { //「妖精倉庫：領取物品」
                // 限制妖精倉庫使用等級設定
                if (pc.getLevel() >= ConfigOtherSet2.Dwarf_Level && pc.isElf()) {
                    // 具有倉庫密碼數據
                    if (client.getAccount().get_warehouse() > 0) {
                        if (isTwoLogin(pc)) { // 相同帳號檢測
                            return;
                        }
                        // 送出倉庫密碼視窗
                        pc.sendPackets(new S_ServerMessage(834));
                    } else {
                        if (isTwoLogin(pc)) { // 相同帳號檢測
                            return;
                        }
                        // 取回PC妖精倉庫的數據
                        final int size = pc.getDwarfForElfInventory().getSize();
                        if (size > 0) {
                            pc.sendPackets(new S_RetrieveElfList(objid, pc));
                        } else {
                            pc.sendPackets(new S_NPCTalkReturn(objid, "noitemret"));
                        }
                    }
                } else {
                    pc.sendPackets(new S_SystemMessage("您未達到倉庫使用等級，使用等級為：" + ConfigOtherSet2.Dwarf_Level));
                }
            } else if (cmd.equalsIgnoreCase("retrieve-pledge")) { //「血盟倉庫：領取物品」
				/*if (pc.getLevel() >= ConfigAlt.Dwarf_Level) {
					if (pc.getClanid() == 0) {
                        // \f1倉庫：無法使用血盟倉庫（未加入血盟）
						pc.sendPackets(new S_ServerMessage(208));
						return;
					}
					int rank = pc.getClanRank();
					if ((rank != L1Clan.CLAN_RANK_PROBATION) && (rank != L1Clan.CLAN_RANK_GUARDIAN)
							&& (rank != L1Clan.CLAN_RANK_PRINCE) && (rank != L1Clan.CLAN_RANK_LEAGUE_PROBATION)
							&& (rank != L1Clan.CLAN_RANK_LEAGUE_GUARDIAN)
							&& (rank != L1Clan.CLAN_RANK_LEAGUE_VICEPRINCE)
							&& (rank != L1Clan.CLAN_RANK_LEAGUE_PRINCE)) {
						// 倉庫：無法使用血盟倉庫（一般階級）
						pc.sendPackets(new S_ServerMessage(728));
						return;
					}
					if (client.getAccount().get_warehouse() > 0) {
						pc.sendPackets(new S_ServerMessage(834));
					} else {
						pc.sendPackets(new S_RetrievePledgeList(objid, pc));
					}
				} else {
					pc.sendPackets(new S_SystemMessage("等級未滿("+ConfigAlt.Dwarf_Level+")級無法使用倉庫！"));
				}*/
                // 限制倉庫使用等級設定
                if (pc.getLevel() >= ConfigOtherSet2.Dwarf_Level) {
                    if (pc.getClanid() == 0) {
                        if (isTwoLogin(pc)) { // 相同帳號檢測
                            return;
                        }
                        pc.sendPackets(new S_ServerMessage(208));
                        return;
                    }
                    int rank = pc.getClanRank();
                    if (rank == L1Clan.CLAN_RANK_PUBLIC // 一般血盟[一般]
                            || rank == L1Clan.CLAN_RANK_LEAGUE_PUBLIC) { // 聯合血盟[一般]
                        pc.sendPackets(new S_ServerMessage(728)); // 倉庫：無法使用血盟倉庫（一般階級）
                        return;
                    }
                    // 具有倉庫密碼數據
                    if (client.getAccount().get_warehouse() > 0) {
                        if (isTwoLogin(pc)) { // 相同帳號檢測
                            return;
                        }
                        // 送出倉庫密碼視窗
                        pc.sendPackets(new S_ServerMessage(834));
                    } else {
                        if (isTwoLogin(pc)) { // 相同帳號檢測
                            return;
                        }
                        final int size = pc.getClan().getDwarfForClanInventory().getSize();
                        if (size > 0) {
                            pc.sendPackets(new S_RetrievePledgeList(objid, pc));
                        } else {
                            pc.sendPackets(new S_NPCTalkReturn(objid, "noitemret"));
                        }
                    }
                } else {
                    pc.sendPackets(new S_SystemMessage("您未達到倉庫使用等級，使用等級為：" + ConfigOtherSet2.Dwarf_Level));
                }
            } else if (cmd.equalsIgnoreCase("history")) {
                pc.sendPackets(new S_PledgeWarehouseHistory(pc.getClanid()));
            } else if (cmd.equalsIgnoreCase("get")) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                int npcId = npc.getNpcTemplate().get_npcId();
                if ((npcId == 70099) || (npcId == 70796)) {
                    L1ItemInstance item = pc.getInventory().storeItem(20081, 1L);
                    String npcName = npc.getNpcTemplate().get_name();
                    String itemName = item.getItem().getName();
                    pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                    pc.getQuest().set_end(11);
                    htmlid = "";
                }
            } else if (cmd.equalsIgnoreCase("room")) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                int npcId = npc.getNpcTemplate().get_npcId();
                boolean canRent = false;// 是否可以租房
                boolean findRoom = false;// 是否找到空房
                boolean isRented = false;// 是否已有租房
                boolean isHall = false;// 是否為會議室
                int roomNumber = 0;// 房號
                byte roomCount = 0;// 已佔用房數
                for (int i = 0; i < 16; i++) {
                    L1Inn inn = InnTable.getInstance().getTemplate(npcId, i);
                    if (inn != null) {
                        if (inn.isHall()) {// 是會議室
                            isHall = true;
                        }
                        Timestamp dueTime = inn.getDueTime();
                        Calendar cal = Calendar.getInstance();
                        long checkDueTime = (cal.getTimeInMillis() - dueTime.getTime()) / 1000L;
                        if ((inn.getLodgerId() == pc.getId()) && (checkDueTime < 0L)) {// 還有租房資料及剩餘時間
                            isRented = true;
                            break;
                        }
                        if (pc.getInventory().checkItem(40312, 1)) {// 身上有旅館鑰匙
                            isRented = true;
                            break;
                        }
                        if ((!findRoom) && (!isRented)) {
                            if (checkDueTime >= 0L) {// 已超過時間
                                canRent = true;
                                findRoom = true;
                                roomNumber = inn.getRoomNumber();
                            } else if (!inn.isHall()) {// 不是會議室
                                roomCount = (byte) (roomCount + 1);// 已佔用房數
                            }
                        }
                    }
                }
                if (isRented) {
                    if (isHall) {// 是會議室
                        htmlid = "inn15";
                    } else {
                        htmlid = "inn5";
                    }
                } else if (roomCount >= 12) {// 已佔用房數
                    htmlid = "inn6";
                } else if (canRent) {
                    pc.setInnRoomNumber(roomNumber);
                    pc.setHall(false);
                    pc.sendPackets(new S_HowManyKey(npc, 300, 1, 8, "inn2"));
                }
            } else if ((cmd.equalsIgnoreCase("hall")) && ((obj instanceof L1MerchantInstance))) {
                if (pc.isCrown()) {
                    L1NpcInstance npc = (L1NpcInstance) obj;
                    int npcId = npc.getNpcTemplate().get_npcId();
                    boolean canRent = false;// 是否可以租房
                    boolean findRoom = false;// 是否找到空房
                    boolean isRented = false;// 是否已有租房
                    boolean isHall = false;// 是否為會議室
                    int roomNumber = 0;// 房號
                    byte roomCount = 0;// 已佔用房數
                    for (int i = 0; i < 16; i++) {
                        L1Inn inn = InnTable.getInstance().getTemplate(npcId, i);
                        if (inn != null) {
                            if (inn.isHall()) {// 是會議室
                                isHall = true;
                            }
                            Timestamp dueTime = inn.getDueTime();
                            Calendar cal = Calendar.getInstance();
                            long checkDueTime = (cal.getTimeInMillis() - dueTime.getTime()) / 1000L;
                            if ((inn.getLodgerId() == pc.getId()) && (checkDueTime < 0L)) {// 還有租房資料及剩餘時間
                                isRented = true;
                                break;
                            }
                            if (pc.getInventory().checkItem(40312, 1)) {// 身上有旅館鑰匙
                                isRented = true;
                                break;
                            }
                            if ((!findRoom) && (!isRented)) {
                                if (checkDueTime >= 0L) {// 已超過時間
                                    canRent = true;
                                    findRoom = true;
                                    roomNumber = inn.getRoomNumber();
                                } else if (inn.isHall()) {
                                    roomCount = (byte) (roomCount + 1);
                                }
                            }
                        }
                    }
                    if (isRented) {
                        if (isHall) {
                            htmlid = "inn15";
                        } else {
                            htmlid = "inn5";
                        }
                    } else if (roomCount >= 4) {// 已佔用會議室數
                        htmlid = "inn16";
                    } else if (canRent) {
                        pc.setInnRoomNumber(roomNumber);
                        pc.setHall(true);
                        pc.sendPackets(new S_HowManyKey(npc, 300, 1, 16, "inn12"));
                    }
                } else {
                    htmlid = "inn10";
                }
            } else if (cmd.equalsIgnoreCase("return")) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                int npcId = npc.getNpcTemplate().get_npcId();
                int price = 0;
                boolean isBreak = false;
                L1Inn inn;
                for (int i = 0; i < 16; i++) {
                    inn = InnTable.getInstance().getTemplate(npcId, i);
                    if ((inn != null) && (inn.getLodgerId() == pc.getId())) {
                        Timestamp dueTime = inn.getDueTime();
                        if (dueTime != null) {
                            Calendar cal = Calendar.getInstance();
                            if ((cal.getTimeInMillis() - dueTime.getTime()) / 1000L < 0L) {// 尚未到期
                                isBreak = true;
                                price += 60;// 尚未到期則加60元
                            }
                        }
                        Timestamp ts = new Timestamp(System.currentTimeMillis());
                        inn.setDueTime(ts);
                        inn.setLodgerId(0);
                        inn.setKeyId(0);
                        inn.setHall(false);
                        InnTable.getInstance().updateInn(inn);// 更新DB資料
                        break;
                    }
                }
                for (L1ItemInstance item : pc.getInventory().getItems()) {
                    if (item.getInnNpcId() == npcId) {
                        price = (int) (price + 20L * item.getCount());
                        InnKeyTable.DeleteKey(item);
                        pc.getInventory().removeItem(item);
                        isBreak = true;
                    }
                }
                if (isBreak) {// 是否退租
                    htmldata = new String[]{npc.getName(), String.valueOf(price)};
                    htmlid = "inn20";
                    pc.getInventory().storeItem(40308, price);
                } else {
                    htmlid = "";
                }
            } else if (cmd.equalsIgnoreCase("enter")) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                int npcId = npc.getNpcTemplate().get_npcId();
                for (L1ItemInstance item : pc.getInventory().getItems()) {
                    if (item.getInnNpcId() == npcId) {// 對應的npcid相同
                        for (int i = 0; i < 16; i++) {
                            L1Inn inn = InnTable.getInstance().getTemplate(npcId, i);
                            if (inn.getKeyId() == item.getKeyId()) {// 鑰匙編號相同
                                Timestamp dueTime = item.getDueTime();
                                if (dueTime != null) {
                                    Calendar cal = Calendar.getInstance();
                                    if ((cal.getTimeInMillis() - dueTime.getTime()) / 1000L < 0L) {// 還未到退房時間
                                        int[] data = (int[]) null;
                                        switch (npcId) {
                                            case 70012:
                                                data = new int[]{32745, 32803, 16384, 32743, 32808, 16896};
                                                break;
                                            case 70019:
                                                data = new int[]{32743, 32803, 17408, 32744, 32807, 17920};
                                                break;
                                            case 70031:
                                                data = new int[]{32744, 32803, 18432, 32744, 32807, 18944};
                                                break;
                                            case 70065:
                                                data = new int[]{32744, 32803, 19456, 32744, 32807, 19968};
                                                break;
                                            case 70070:
                                                data = new int[]{32744, 32803, 20480, 32744, 32807, 20992};
                                                break;
                                            case 70075:
                                                data = new int[]{32744, 32803, 21504, 32744, 32807, 22016};
                                                break;
                                            case 70084:
                                                data = new int[]{32744, 32803, 22528, 32744, 32807, 23040};
                                                break;
                                            case 70054:
                                                data = new int[]{32744, 32803, 23552, 32744, 32807, 24064};
                                                break;
                                            case 70096:
                                                data = new int[]{32744, 32803, 24576, 32744, 32807, 25088};
                                                break;
                                        }
                                        if (!item.checkRoomOrHall()) {// 一般房間
                                            pc.set_showId(item.getKeyId());// 設置副本編號
                                            L1Teleport.teleport(pc, data[0], data[1], (short) data[2], 6, false);
                                            break;
                                        } else {// 會議室
                                            pc.set_showId(item.getKeyId());// 設置副本編號
                                            L1Teleport.teleport(pc, data[3], data[4], (short) data[5], 6, false);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (cmd.equalsIgnoreCase("openigate")) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                openCloseGate(pc, npc.getNpcTemplate().get_npcId(), true);
                htmlid = "";
            } else if (cmd.equalsIgnoreCase("closeigate")) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                openCloseGate(pc, npc.getNpcTemplate().get_npcId(), false);
                htmlid = "";
            } else if (cmd.equalsIgnoreCase("askwartime")) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                if (npc.getNpcTemplate().get_npcId() == 60514) {
                    htmldata = makeWarTimeStrings(1);
                    htmlid = "ktguard7";
                } else if (npc.getNpcTemplate().get_npcId() == 60560) {
                    htmldata = makeWarTimeStrings(2);
                    htmlid = "orcguard7";
                } else if (npc.getNpcTemplate().get_npcId() == 60552) {
                    htmldata = makeWarTimeStrings(3);
                    htmlid = "wdguard7";
                } else if ((npc.getNpcTemplate().get_npcId() == 60524) || (npc.getNpcTemplate().get_npcId() == 60525) || (npc.getNpcTemplate().get_npcId() == 60529)) {
                    htmldata = makeWarTimeStrings(4);
                    htmlid = "grguard7";
                } else if (npc.getNpcTemplate().get_npcId() == 70857) {
                    htmldata = makeWarTimeStrings(5);
                    htmlid = "heguard7";
                } else if ((npc.getNpcTemplate().get_npcId() == 60530) || (npc.getNpcTemplate().get_npcId() == 60531)) {
                    htmldata = makeWarTimeStrings(6);
                    htmlid = "dcguard7";
                } else if ((npc.getNpcTemplate().get_npcId() == 60533) || (npc.getNpcTemplate().get_npcId() == 60534)) {
                    htmldata = makeWarTimeStrings(7);
                    htmlid = "adguard7";
                } else if (npc.getNpcTemplate().get_npcId() == 81156) {
                    htmldata = makeWarTimeStrings(8);
                    htmlid = "dfguard3";
                }
            } else if (cmd.equalsIgnoreCase("inex")) {
                L1Clan clan = WorldClan.get().getClan(pc.getClanname());
                if (clan != null) {
                    int castle_id = clan.getCastleId();
                    if (castle_id != 0) {
                        L1Castle l1castle = CastleReading.get().getCastleTable(castle_id);
                        pc.sendPackets(new S_ServerMessage(309, l1castle.getName(), String.valueOf(l1castle.getPublicMoney())));
                        htmlid = "";
                    }
                }
            } else if (cmd.equalsIgnoreCase("tax")) {
                pc.sendPackets(new S_TaxRate(pc.getId()));
            } else if (cmd.equalsIgnoreCase("withdrawal")) {
                L1Clan clan = WorldClan.get().getClan(pc.getClanname());
                if (clan != null) {
                    int castle_id = clan.getCastleId();
                    if (castle_id != 0) {
                        L1Castle l1castle = CastleReading.get().getCastleTable(castle_id);
                        pc.sendPackets(new S_Drawal(pc.getId(), l1castle.getPublicMoney()));
                    }
                }
            } else if (cmd.equalsIgnoreCase("cdeposit")) {
                pc.sendPackets(new S_Deposit(pc.getId()));
            } else if (cmd.equalsIgnoreCase("employ")) {
            } else if (cmd.equalsIgnoreCase("arrange")) {
            } else if (cmd.equalsIgnoreCase("castlegate")) {
                repairGate(pc);
                htmlid = "";
            } else if (cmd.equalsIgnoreCase("encw")) {
                if (pc.getWeapon() == null) {
                    pc.sendPackets(new S_ServerMessage(79));
                } else {
                    for (L1ItemInstance item : pc.getInventory().getItems()) {
                        if (pc.getWeapon().equals(item)) {
                            L1SkillUse l1skilluse = new L1SkillUse();
                            l1skilluse.handleCommands(pc, 12, item.getId(), 0, 0, 0, 2);
                            break;
                        }
                    }
                }
                htmlid = "";
            } else if (cmd.equalsIgnoreCase("enca")) {
                L1ItemInstance item = pc.getInventory().getItemEquipped(2, 2);
                if (item != null) {
                    L1SkillUse l1skilluse = new L1SkillUse();
                    l1skilluse.handleCommands(pc, 21, item.getId(), 0, 0, 0, 2);
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
                htmlid = "";
            } else if (cmd.equalsIgnoreCase("depositnpc")) {
                final Object[] petList = pc.getPetList().values().toArray();
                for (final Object petObject : petList) {
                    if ((petObject instanceof L1PetInstance)) {
                        L1PetInstance pet = (L1PetInstance) petObject;
                        pet.collect(true);
                        pc.removePet(pet);
                        pet.deleteMe();
                    }
                }
                htmlid = "";
            } else if (cmd.equalsIgnoreCase("withdrawnpc")) {
                pc.sendPackets(new S_PetList(objid, pc));
            } else if ((cmd.equalsIgnoreCase("open")) || (cmd.equalsIgnoreCase("close"))) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                openCloseDoor(pc, npc, cmd);
                htmlid = "";
            } else if (cmd.equalsIgnoreCase("expel")) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                expelOtherClan(pc, npc.getNpcTemplate().get_npcId());
                htmlid = "";
            } else if (cmd.equalsIgnoreCase("pay")) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                htmldata = makeHouseTaxStrings(pc, npc);
                htmlid = "agpay";
            } else if (cmd.equalsIgnoreCase("payfee")) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                payFee(pc, npc);
                htmlid = "";
            } else if (cmd.equalsIgnoreCase("name")) {
                L1Clan clan = WorldClan.get().getClan(pc.getClanname());
                if (clan != null) {
                    int houseId = clan.getHouseId();
                    if (houseId != 0) {
                        L1House house = HouseReading.get().getHouseTable(houseId);
                        int keeperId = house.getKeeperId();
                        L1NpcInstance npc = (L1NpcInstance) obj;
                        if (npc.getNpcTemplate().get_npcId() == keeperId) {
                            pc.setTempID(houseId);
                            pc.sendPackets(new S_Message_YN(512));
                        }
                    }
                }
                htmlid = "";
            } else if (cmd.equalsIgnoreCase("rem")) {
            } else if ((cmd.equalsIgnoreCase("tel0")) || (cmd.equalsIgnoreCase("tel1")) || (cmd.equalsIgnoreCase("tel2")) || (cmd.equalsIgnoreCase("tel3"))) {
                L1Clan clan = WorldClan.get().getClan(pc.getClanname());
                if (clan != null) {
                    int houseId = clan.getHouseId();
                    if (houseId != 0) {
                        L1House house = HouseReading.get().getHouseTable(houseId);
                        int keeperId = house.getKeeperId();
                        L1NpcInstance npc = (L1NpcInstance) obj;
                        if (npc.getNpcTemplate().get_npcId() == keeperId) {
                            int[] loc = new int[3];
                            if (cmd.equalsIgnoreCase("tel0")) {
                                loc = L1HouseLocation.getHouseTeleportLoc(houseId, 0);
                            } else if (cmd.equalsIgnoreCase("tel1")) {
                                loc = L1HouseLocation.getHouseTeleportLoc(houseId, 1);
                            } else if (cmd.equalsIgnoreCase("tel2")) {
                                loc = L1HouseLocation.getHouseTeleportLoc(houseId, 2);
                            } else if (cmd.equalsIgnoreCase("tel3")) {
                                loc = L1HouseLocation.getHouseTeleportLoc(houseId, 3);
                            }
                            L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);
                        }
                    }
                }
                htmlid = "";
            } else if (cmd.equalsIgnoreCase("upgrade")) {
                L1Clan clan = WorldClan.get().getClan(pc.getClanname());
                if (clan != null) {
                    int houseId = clan.getHouseId();
                    if (houseId != 0) {
                        L1House house = HouseReading.get().getHouseTable(houseId);
                        int keeperId = house.getKeeperId();
                        if (keeperId >= 50626 && keeperId <= 50631) {
                            pc.sendPackets(new S_ServerMessage("此小屋無法創建地下盟屋"));
                            return;
                        }
                        L1NpcInstance npc = (L1NpcInstance) obj;
                        if (npc.getNpcTemplate().get_npcId() == keeperId) {
                            if ((pc.isCrown()) && (pc.getId() == clan.getLeaderId())) {
                                if (house.isPurchaseBasement()) {
                                    pc.sendPackets(new S_ServerMessage(1135));
                                } else if (pc.getInventory().consumeItem(40308, 5000000L)) {
                                    house.setPurchaseBasement(true);
                                    HouseReading.get().updateHouse(house);
                                    pc.sendPackets(new S_ServerMessage(1099));
                                } else {
                                    pc.sendPackets(new S_ServerMessage(189));
                                }
                            } else {
                                pc.sendPackets(new S_ServerMessage(518));
                            }
                        }
                    }
                }
                htmlid = "";
            } else if ((cmd.equalsIgnoreCase("hall")) && ((obj instanceof L1HousekeeperInstance))) {
                L1Clan clan = WorldClan.get().getClan(pc.getClanname());
                if (clan != null) {
                    int houseId = clan.getHouseId();
                    if (houseId != 0) {
                        L1House house = HouseReading.get().getHouseTable(houseId);
                        int keeperId = house.getKeeperId();
                        L1NpcInstance npc = (L1NpcInstance) obj;
                        if (npc.getNpcTemplate().get_npcId() == keeperId) {
                            if (house.isPurchaseBasement()) {
                                int[] loc = new int[3];
                                loc = L1HouseLocation.getBasementLoc(houseId);
                                L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);
                            } else {
                                pc.sendPackets(new S_ServerMessage(1098));
                            }
                        }
                    }
                }
                htmlid = "";
            } else if (cmd.equalsIgnoreCase("fire")) {
                if (pc.isElf()) {
                    if (pc.getElfAttr() != 0) {
                        return;
                    }
                    pc.setElfAttr(2);
                    pc.save();
                    pc.sendPackets(new S_PacketBox(15, 1));
                    htmlid = "";
                }
            } else if (cmd.equalsIgnoreCase("water")) {
                if (pc.isElf()) {
                    if (pc.getElfAttr() != 0) {
                        return;
                    }
                    pc.setElfAttr(4);
                    pc.save();
                    pc.sendPackets(new S_PacketBox(15, 2));
                    htmlid = "";
                }
            } else if (cmd.equalsIgnoreCase("air")) {
                if (pc.isElf()) {
                    if (pc.getElfAttr() != 0) {
                        return;
                    }
                    pc.setElfAttr(8);
                    pc.save();
                    pc.sendPackets(new S_PacketBox(15, 3));
                    htmlid = "";
                }
            } else if (cmd.equalsIgnoreCase("earth")) {
                if (pc.isElf()) {
                    if (pc.getElfAttr() != 0) {
                        return;
                    }
                    pc.setElfAttr(1);
                    pc.save();
                    pc.sendPackets(new S_PacketBox(15, 4));
                    htmlid = "";
                }
            } else if (cmd.equalsIgnoreCase("init")) {
                if (pc.isElf()) {
                    if (pc.getElfAttr() == 0) {
                        return;
                    }
                    for (int cnt = 129; cnt <= 176; cnt++) {
                        L1Skills l1skills1 = SkillsTable.get().getTemplate(cnt);
                        int skill_attr = l1skills1.getAttr();
                        if (skill_attr != 0) {
                            CharSkillReading.get().spellLost(pc.getId(), l1skills1.getSkillId());
                        }
                    }
                    if (pc.hasSkillEffect(147)) {
                        pc.removeSkillEffect(147);
                    }
                    pc.sendPackets(new S_DelSkill(pc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 248, 252, 252, 255, 0, 0, 0, 0, 0, 0));
                    pc.setElfAttr(0);
                    pc.save();
                    pc.sendPackets(new S_ServerMessage(678));
                    htmlid = "";
                }
            } else if (cmd.equalsIgnoreCase("exp")) {
                if (pc.getExpRes() == 1) {
                    int cost = 0;
                    int level = pc.getLevel();
                    int lawful = pc.getLawful();
                    if (level < 45) {
                        cost = level * level * 100;
                    } else {
                        cost = level * level * 200;
                    }
                    if (lawful >= 0) {
                        cost /= 2;
                    }
                    pc.sendPackets(new S_Message_YN(738, String.valueOf(cost)));
                } else {
                    pc.sendPackets(new S_ServerMessage(739));
                    htmlid = "";
                }
            } else if (cmd.equalsIgnoreCase("ent")) {
                int npcId = ((L1NpcInstance) obj).getNpcId();
                if (npcId == 80085) {
                    htmlid = enterHauntedHouse(pc);
                } else if (npcId == 80088) {
                    String s2 = readS();
                    htmlid = enterPetMatch(pc, Integer.valueOf(s2).intValue());
                } else if ((npcId == 50038) || (npcId == 50042) || (npcId == 50029) || (npcId == 50019) || (npcId == 50062)) {
                    htmlid = watchUb(pc, npcId);
                } else if (npcId == 80086) { // 庫山
                    if (pc.getLevel() > 29 && pc.getLevel() < 52) {
                        // L1DeathMatch.get().enterGame(pc);
                    } else {
                        pc.sendPackets(new S_ServerMessage(1273, "30", "51"));
                    }
                } else if (npcId == 80087) { // 多都
                    if (pc.getLevel() > 51) {
                        L1DeathMatch.getInstance().enterGame(pc);
                    } else {
                        pc.sendPackets(new S_ServerMessage(1273, "52", "99"));
                    }
                } else {
                    htmlid = enterUb(pc, npcId);
                }
            } else if (cmd.equalsIgnoreCase("par")) {
                htmlid = enterUb(pc, ((L1NpcInstance) obj).getNpcId());
            } else if (cmd.equalsIgnoreCase("info")) {
                htmlid = "colos2";
            } else if (cmd.equalsIgnoreCase("sco")) { // UB關連「高得點者一覽確認」
                /** [原碼] 無限大戰計分系統 */
                L1NpcInstance npc = (L1NpcInstance) obj;
                UbRank(pc, npc);
            } else if (cmd.equalsIgnoreCase("haste")) {
                L1NpcInstance l1npcinstance = (L1NpcInstance) obj;
                int npcid = l1npcinstance.getNpcTemplate().get_npcId();
                if (npcid == 70514) {
                    pc.sendPackets(new S_ServerMessage(183));
                    pc.sendPackets(new S_SkillHaste(pc.getId(), 1, 1600));
                    pc.broadcastPacketAll(new S_SkillHaste(pc.getId(), 1, 0));
                    pc.sendPacketsX8(new S_SkillSound(pc.getId(), 755));
                    pc.setMoveSpeed(1);
                    pc.setSkillEffect(1001, 1600000);
                    htmlid = "";
                }
            } else if (cmd.equalsIgnoreCase("death 80")) {
                poly(client, 12681);
                htmlid = "";
            } else if (cmd.equalsIgnoreCase("skeleton nbmorph")) {
                poly(client, 2374);
                htmlid = "";
            } else if (cmd.equalsIgnoreCase("lycanthrope nbmorph")) {
                poly(client, 3874);
                htmlid = "";
            } else if (cmd.equalsIgnoreCase("shelob nbmorph")) {
                poly(client, 95);
                htmlid = "";
            } else if (cmd.equalsIgnoreCase("ghoul nbmorph")) {
                poly(client, 3873);
                htmlid = "";
            } else if (cmd.equalsIgnoreCase("ghast nbmorph")) {
                poly(client, 3875);
                htmlid = "";
            } else if (cmd.equalsIgnoreCase("atuba orc nbmorph")) {
                poly(client, 3868);
                htmlid = "";
            } else if (cmd.equalsIgnoreCase("skeleton axeman nbmorph")) {
                poly(client, 2376);
                htmlid = "";
            } else if (cmd.equalsIgnoreCase("troll nbmorph")) {
                poly(client, 3878);
                htmlid = "";
            } else if (npctemp.get_npcId() == 71038) {
                if (cmd.equalsIgnoreCase("A")) {
                    L1NpcInstance npc = (L1NpcInstance) obj;
                    L1ItemInstance item = pc.getInventory().storeItem(41060, 1L);
                    String npcName = npc.getNpcTemplate().get_name();
                    String itemName = item.getItem().getNameId();
                    pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                    htmlid = "orcfnoname9";
                } else if ((cmd.equalsIgnoreCase("Z")) && (pc.getInventory().consumeItem(41060, 1L))) {
                    htmlid = "orcfnoname11";
                }
            } else if (npctemp.get_npcId() == 71039) {
                if (cmd.equalsIgnoreCase("teleportURL")) {
                    htmlid = "orcfbuwoo2";
                }
            } else if (npctemp.get_npcId() == 71040) {
                if (cmd.equalsIgnoreCase("A")) {
                    L1NpcInstance npc = (L1NpcInstance) obj;
                    L1ItemInstance item = pc.getInventory().storeItem(41065, 1L);
                    String npcName = npc.getNpcTemplate().get_name();
                    String itemName = item.getItem().getNameId();
                    pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                    htmlid = "orcfnoa4";
                } else if ((cmd.equalsIgnoreCase("Z")) && (pc.getInventory().consumeItem(41065, 1L))) {
                    htmlid = "orcfnoa7";
                }
            } else if (npctemp.get_npcId() == 71041) {
                if (cmd.equalsIgnoreCase("A")) {
                    L1NpcInstance npc = (L1NpcInstance) obj;
                    L1ItemInstance item = pc.getInventory().storeItem(41064, 1L);
                    String npcName = npc.getNpcTemplate().get_name();
                    String itemName = item.getItem().getNameId();
                    pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                    htmlid = "orcfhuwoomo4";
                } else if ((cmd.equalsIgnoreCase("Z")) && (pc.getInventory().consumeItem(41064, 1L))) {
                    htmlid = "orcfhuwoomo6";
                }
            } else if (npctemp.get_npcId() == 71042) {
                if (cmd.equalsIgnoreCase("A")) {
                    L1NpcInstance npc = (L1NpcInstance) obj;
                    L1ItemInstance item = pc.getInventory().storeItem(41062, 1L);
                    String npcName = npc.getNpcTemplate().get_name();
                    String itemName = item.getItem().getNameId();
                    pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                    htmlid = "orcfbakumo4";
                } else if ((cmd.equalsIgnoreCase("Z")) && (pc.getInventory().consumeItem(41062, 1L))) {
                    htmlid = "orcfbakumo6";
                }
            } else if (npctemp.get_npcId() == 71043) {
                if (cmd.equalsIgnoreCase("A")) {
                    L1NpcInstance npc = (L1NpcInstance) obj;
                    L1ItemInstance item = pc.getInventory().storeItem(41063, 1L);
                    String npcName = npc.getNpcTemplate().get_name();
                    String itemName = item.getItem().getNameId();
                    pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                    htmlid = "orcfbuka4";
                } else if ((cmd.equalsIgnoreCase("Z")) && (pc.getInventory().consumeItem(41063, 1L))) {
                    htmlid = "orcfbuka6";
                }
            } else if (npctemp.get_npcId() == 71044) {
                if (cmd.equalsIgnoreCase("A")) {
                    L1NpcInstance npc = (L1NpcInstance) obj;
                    L1ItemInstance item = pc.getInventory().storeItem(41061, 1L);
                    String npcName = npc.getNpcTemplate().get_name();
                    String itemName = item.getItem().getNameId();
                    pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                    htmlid = "orcfkame4";
                } else if ((cmd.equalsIgnoreCase("Z")) && (pc.getInventory().consumeItem(41061, 1L))) {
                    htmlid = "orcfkame6";
                }
            } else if (npctemp.get_npcId() == 71078) {
                if (cmd.equalsIgnoreCase("teleportURL")) {
                    htmlid = "usender2";
                }
            } else if (npctemp.get_npcId() == 71080) {
                if (cmd.equalsIgnoreCase("teleportURL")) {
                    htmlid = "amisoo2";
                }
            } else if (npctemp.get_npcId() == 80048) {
                if (cmd.equalsIgnoreCase("2")) {
                    htmlid = "";
                }
            } else if (npctemp.get_npcId() == 80049) {
                if ((cmd.equalsIgnoreCase("1")) && (pc.getKarma() <= -10000000)) {
                    pc.setKarma(1000000);
                    pc.sendPackets(new S_ServerMessage(1078));
                    htmlid = "betray13";
                }
            } else if (npctemp.get_npcId() == 80050) {
                if (cmd.equalsIgnoreCase("1")) {
                    htmlid = "meet105";
                } else if (cmd.equalsIgnoreCase("2")) {
                    if (pc.getInventory().checkItem(40718)) {
                        htmlid = "meet106";
                    } else {
                        htmlid = "meet110";
                    }
                } else if (cmd.equalsIgnoreCase("a")) {
                    if (pc.getInventory().consumeItem(40718, 1L)) {
                        pc.addKarma((int) (-100.0D * ConfigRate.RATE_KARMA));
                        pc.sendPackets(new S_ServerMessage(1079));
                        htmlid = "meet107";
                    } else {
                        htmlid = "meet104";
                    }
                } else if (cmd.equalsIgnoreCase("b")) {
                    if (pc.getInventory().consumeItem(40718, 10L)) {
                        pc.addKarma((int) (-1000.0D * ConfigRate.RATE_KARMA));
                        pc.sendPackets(new S_ServerMessage(1079));
                        htmlid = "meet108";
                    } else {
                        htmlid = "meet104";
                    }
                } else if (cmd.equalsIgnoreCase("c")) {
                    if (pc.getInventory().consumeItem(40718, 100L)) {
                        pc.addKarma((int) (-10000.0D * ConfigRate.RATE_KARMA));
                        pc.sendPackets(new S_ServerMessage(1079));
                        htmlid = "meet109";
                    } else {
                        htmlid = "meet104";
                    }
                } else if (cmd.equalsIgnoreCase("d")) {
                    if ((pc.getInventory().checkItem(40615)) || (pc.getInventory().checkItem(40616))) {
                        htmlid = "";
                    } else {
                        L1Teleport.teleport(pc, 32683, 32895, (short) 608, 5, true);
                    }
                }
            } else if (npctemp.get_npcId() == 80052) {
                if (cmd.equalsIgnoreCase("a")) {
                    if (pc.hasSkillEffect(4003)) {
                        pc.removeSkillEffect(4003);
                    }
                    if (pc.hasSkillEffect(4007)) {
                        pc.removeSkillEffect(4007);
                    }
                    if (pc.hasSkillEffect(4006)) {
                        pc.sendPackets(new S_ServerMessage(79));
                    } else {
                        pc.setSkillEffect(4005, 1500000);
                        pc.sendPacketsX8(new S_SkillSound(pc.getId(), 7246));
                        pc.sendPackets(new S_ServerMessage(1127));
                    }
                }
            } else if (npctemp.get_npcId() == 80053) {
                int karmaLevel = pc.getKarmaLevel();
                if (cmd.equalsIgnoreCase("a")) {
                    int aliceMaterialId = 0;
                    int[] aliceMaterialIdList = {40991, 196, 197, 198, 199, 200, 201, 202, 203};
                    for (final int id : aliceMaterialIdList) {
                        if (pc.getInventory().checkItem(id)) {
                            aliceMaterialId = id;
                            break;
                        }
                    }
                    if (aliceMaterialId == 0) {
                        htmlid = "alice_no";
                    } else if (aliceMaterialId == 40991) {
                        if (karmaLevel <= -1) {
                            materials = new int[]{40995, 40718, 40991};
                            counts = new int[]{100, 100, 1};
                            createitem = new int[]{196};
                            createcount = new int[]{1};
                            success_htmlid = "alice_1";
                            failure_htmlid = "alice_no";
                        } else {
                            htmlid = "aliceyet";
                        }
                    } else if (aliceMaterialId == 196) {
                        if (karmaLevel <= -2) {
                            materials = new int[]{40997, 40718, 196};
                            counts = new int[]{100, 100, 1};
                            createitem = new int[]{197};
                            createcount = new int[]{1};
                            success_htmlid = "alice_2";
                            failure_htmlid = "alice_no";
                        } else {
                            htmlid = "alice_1";
                        }
                    } else if (aliceMaterialId == 197) {
                        if (karmaLevel <= -3) {
                            materials = new int[]{40990, 40718, 197};
                            counts = new int[]{100, 100, 1};
                            createitem = new int[]{198};
                            createcount = new int[]{1};
                            success_htmlid = "alice_3";
                            failure_htmlid = "alice_no";
                        } else {
                            htmlid = "alice_2";
                        }
                    } else if (aliceMaterialId == 198) {
                        if (karmaLevel <= -4) {
                            materials = new int[]{40994, 40718, 198};
                            counts = new int[]{50, 100, 1};
                            createitem = new int[]{199};
                            createcount = new int[]{1};
                            success_htmlid = "alice_4";
                            failure_htmlid = "alice_no";
                        } else {
                            htmlid = "alice_3";
                        }
                    } else if (aliceMaterialId == 199) {
                        if (karmaLevel <= -5) {
                            materials = new int[]{40993, 40718, 199};
                            counts = new int[]{50, 100, 1};
                            createitem = new int[]{200};
                            createcount = new int[]{1};
                            success_htmlid = "alice_5";
                            failure_htmlid = "alice_no";
                        } else {
                            htmlid = "alice_4";
                        }
                    } else if (aliceMaterialId == 200) {
                        if (karmaLevel <= -6) {
                            materials = new int[]{40998, 40718, 200};
                            counts = new int[]{50, 100, 1};
                            createitem = new int[]{201};
                            createcount = new int[]{1};
                            success_htmlid = "alice_6";
                            failure_htmlid = "alice_no";
                        } else {
                            htmlid = "alice_5";
                        }
                    } else if (aliceMaterialId == 201) {
                        if (karmaLevel <= -7) {
                            materials = new int[]{40996, 40718, 201};
                            counts = new int[]{10, 100, 1};
                            createitem = new int[]{202};
                            createcount = new int[]{1};
                            success_htmlid = "alice_7";
                            failure_htmlid = "alice_no";
                        } else {
                            htmlid = "alice_6";
                        }
                    } else if (aliceMaterialId == 202) {
                        if (karmaLevel <= -8) {
                            materials = new int[]{40992, 40718, 202};
                            counts = new int[]{10, 100, 1};
                            createitem = new int[]{203};
                            createcount = new int[]{1};
                            success_htmlid = "alice_8";
                            failure_htmlid = "alice_no";
                        } else {
                            htmlid = "alice_7";
                        }
                    } else if (aliceMaterialId == 203) {
                        htmlid = "alice_8";
                    }
                }
            } else if (npctemp.get_npcId() == 80055) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                htmlid = getYaheeAmulet(pc, npc, cmd);
            } else if (npctemp.get_npcId() == 80056) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                if (pc.getKarma() <= -10000000) {
                    getBloodCrystalByKarma(pc, npc, cmd);
                }
                htmlid = "";
            } else if (npctemp.get_npcId() == 80063) {
                if (cmd.equalsIgnoreCase("a")) {
                    if (pc.getInventory().checkItem(40921)) {
                        L1Teleport.teleport(pc, 32674, 32832, (short) 603, 2, true);
                    } else {
                        htmlid = "gpass02";
                    }
                }
            } else if (npctemp.get_npcId() == 80064) {
                if (cmd.equalsIgnoreCase("1")) {
                    htmlid = "meet005";
                } else if (cmd.equalsIgnoreCase("2")) {
                    if (pc.getInventory().checkItem(40678)) {
                        htmlid = "meet006";
                    } else {
                        htmlid = "meet010";
                    }
                } else if (cmd.equalsIgnoreCase("a")) {
                    if (pc.getInventory().consumeItem(40678, 1L)) {
                        pc.addKarma((int) (100.0D * ConfigRate.RATE_KARMA));
                        pc.sendPackets(new S_ServerMessage(1078));
                        htmlid = "meet007";
                    } else {
                        htmlid = "meet004";
                    }
                } else if (cmd.equalsIgnoreCase("b")) {
                    if (pc.getInventory().consumeItem(40678, 10L)) {
                        pc.addKarma((int) (1000.0D * ConfigRate.RATE_KARMA));
                        pc.sendPackets(new S_ServerMessage(1078));
                        htmlid = "meet008";
                    } else {
                        htmlid = "meet004";
                    }
                } else if (cmd.equalsIgnoreCase("c")) {
                    if (pc.getInventory().consumeItem(40678, 100L)) {
                        pc.addKarma((int) (10000.0D * ConfigRate.RATE_KARMA));
                        pc.sendPackets(new S_ServerMessage(1078));
                        htmlid = "meet009";
                    } else {
                        htmlid = "meet004";
                    }
                } else if (cmd.equalsIgnoreCase("d")) {
                    if ((pc.getInventory().checkItem(40909)) || (pc.getInventory().checkItem(40910)) || (pc.getInventory().checkItem(40911)) || (pc.getInventory().checkItem(40912)) || (pc.getInventory().checkItem(40913)) || (pc.getInventory().checkItem(40914)) || (pc.getInventory().checkItem(40915)) || (pc.getInventory().checkItem(40916)) || (pc.getInventory().checkItem(40917)) || (pc.getInventory().checkItem(40918)) || (pc.getInventory().checkItem(40919)) || (pc.getInventory().checkItem(40920)) || (pc.getInventory().checkItem(40921))) {
                        htmlid = "";
                    } else {
                        L1Teleport.teleport(pc, 32674, 32832, (short) 602, 2, true);
                    }
                }
            } else if (npctemp.get_npcId() == 80066) {
                if ((cmd.equalsIgnoreCase("1")) && (pc.getKarma() >= 10000000)) {
                    pc.setKarma(-1000000);
                    pc.sendPackets(new S_ServerMessage(1079));
                    htmlid = "betray03";
                }
            } else if (npctemp.get_npcId() == 80071) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                htmlid = getBarlogEarring(pc, npc, cmd);
            } else if (npctemp.get_npcId() == 80073) {
                if (cmd.equalsIgnoreCase("a")) {
                    if (pc.hasSkillEffect(4003)) {
                        pc.removeSkillEffect(4003);
                    }
                    if (pc.hasSkillEffect(4007)) {
                        pc.removeSkillEffect(4007);
                    }
                    if (pc.hasSkillEffect(4005)) {
                        pc.sendPackets(new S_ServerMessage(79));
                    } else {
                        pc.setSkillEffect(4006, 1020000);
                        pc.sendPacketsX8(new S_SkillSound(pc.getId(), 7247));
                        pc.sendPackets(new S_ServerMessage(1127));
                    }
                }
            } else if (npctemp.get_npcId() == 80072) {
                int karmaLevel = pc.getKarmaLevel();
                if (cmd.equalsIgnoreCase("0")) {
                    htmlid = "lsmitha";
                } else if (cmd.equalsIgnoreCase("1")) {
                    htmlid = "lsmithb";
                } else if (cmd.equalsIgnoreCase("2")) {
                    htmlid = "lsmithc";
                } else if (cmd.equalsIgnoreCase("3")) {
                    htmlid = "lsmithd";
                } else if (cmd.equalsIgnoreCase("4")) {
                    htmlid = "lsmithe";
                } else if (cmd.equalsIgnoreCase("5")) {
                    htmlid = "lsmithf";
                } else if (cmd.equalsIgnoreCase("6")) {
                    htmlid = "";
                } else if (cmd.equalsIgnoreCase("7")) {
                    htmlid = "lsmithg";
                } else if (cmd.equalsIgnoreCase("8")) {
                    htmlid = "lsmithh";
                } else if ((cmd.equalsIgnoreCase("a")) && (karmaLevel >= 1)) {
                    materials = new int[]{20158, 40669, 40678};
                    counts = new int[]{1, 50, 100};
                    createitem = new int[]{20083};
                    createcount = new int[]{1};
                    success_htmlid = "";
                    failure_htmlid = "lsmithaa";
                } else if ((cmd.equalsIgnoreCase("b")) && (karmaLevel >= 2)) {
                    materials = new int[]{20144, 40672, 40678};
                    counts = new int[]{1, 50, 100};
                    createitem = new int[]{20131};
                    createcount = new int[]{1};
                    success_htmlid = "";
                    failure_htmlid = "lsmithbb";
                } else if ((cmd.equalsIgnoreCase("c")) && (karmaLevel >= 3)) {
                    materials = new int[]{20075, 40671, 40678};
                    counts = new int[]{1, 50, 100};
                    createitem = new int[]{20069};
                    createcount = new int[]{1};
                    success_htmlid = "";
                    failure_htmlid = "lsmithcc";
                } else if ((cmd.equalsIgnoreCase("d")) && (karmaLevel >= 4)) {
                    materials = new int[]{20183, 40674, 40678};
                    counts = new int[]{1, 20, 100};
                    createitem = new int[]{20179};
                    createcount = new int[]{1};
                    success_htmlid = "";
                    failure_htmlid = "lsmithdd";
                } else if ((cmd.equalsIgnoreCase("e")) && (karmaLevel >= 5)) {
                    materials = new int[]{20190, 40674, 40678};
                    counts = new int[]{1, 40, 100};
                    createitem = new int[]{20209};
                    createcount = new int[]{1};
                    success_htmlid = "";
                    failure_htmlid = "lsmithee";
                } else if ((cmd.equalsIgnoreCase("f")) && (karmaLevel >= 6)) {
                    materials = new int[]{20078, 40674, 40678};
                    counts = new int[]{1, 5, 100};
                    createitem = new int[]{20290};
                    createcount = new int[]{1};
                    success_htmlid = "";
                    failure_htmlid = "lsmithff";
                } else if ((cmd.equalsIgnoreCase("g")) && (karmaLevel >= 7)) {
                    materials = new int[]{20078, 40670, 40678};
                    counts = new int[]{1, 1, 100};
                    createitem = new int[]{20261};
                    createcount = new int[]{1};
                    success_htmlid = "";
                    failure_htmlid = "lsmithgg";
                } else if ((cmd.equalsIgnoreCase("h")) && (karmaLevel >= 8)) {
                    materials = new int[]{40719, 40673, 40678};
                    counts = new int[]{1, 1, 100};
                    createitem = new int[]{20031};
                    createcount = new int[]{1};
                    success_htmlid = "";
                    failure_htmlid = "lsmithhh";
                }
            } else if (npctemp.get_npcId() == 80074) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                if (pc.getKarma() >= 10000000) {
                    getSoulCrystalByKarma(pc, npc, cmd);
                }
                htmlid = "";
            } else if (npctemp.get_npcId() == 80057) {
                htmlid = karmaLevelToHtmlId(pc.getKarmaLevel());
                htmldata = new String[]{String.valueOf(pc.getKarmaPercent())};
            } else if ((npctemp.get_npcId() == 80059) || (npctemp.get_npcId() == 80060) || (npctemp.get_npcId() == 80061) || (npctemp.get_npcId() == 80062)) {
                htmlid = talkToDimensionDoor(pc, (L1NpcInstance) obj, cmd);
            } else if ((cmd.equalsIgnoreCase("pandora6")) || (cmd.equalsIgnoreCase("cold6")) || (cmd.equalsIgnoreCase("balsim3")) || (cmd.equalsIgnoreCase("mellin3")) || (cmd.equalsIgnoreCase("glen3"))) {
                htmlid = cmd;
                int npcid = npctemp.get_npcId();
                int taxRatesCastle = L1CastleLocation.getCastleTaxRateByNpcId(npcid);
                htmldata = new String[]{String.valueOf(taxRatesCastle)};
            } else if (npctemp.get_npcId() == 70512) {
                if ((cmd.equalsIgnoreCase("0")) || (cmd.equalsIgnoreCase("fullheal"))) {
                    int hp = _random.nextInt(21) + 70;
                    pc.setCurrentHp(pc.getCurrentHp() + hp);
                    pc.sendPackets(new S_ServerMessage(77));
                    pc.sendPackets(new S_SkillSound(pc.getId(), 830));
                    pc.sendPackets(new S_HPUpdate(pc));
                    htmlid = "";
                }
            } else if (npctemp.get_npcId() == 71037) {
                if (cmd.equalsIgnoreCase("0")) {
                    pc.setCurrentHp(pc.getMaxHp());
                    pc.setCurrentMp(pc.getMaxMp());
                    pc.sendPackets(new S_ServerMessage(77));
                    pc.sendPackets(new S_SkillSound(pc.getId(), 830));
                    pc.sendPackets(new S_HPUpdate(pc));
                    pc.sendPackets(new S_MPUpdate(pc));
                }
            } else if (npctemp.get_npcId() == 71030) {
                if (cmd.equalsIgnoreCase("fullheal")) {
                    if (pc.getInventory().checkItem(40308, 5L)) {
                        pc.getInventory().consumeItem(40308, 5L);
                        pc.setCurrentHp(pc.getMaxHp());
                        pc.setCurrentMp(pc.getMaxMp());
                        pc.sendPackets(new S_ServerMessage(77));
                        pc.sendPackets(new S_SkillSound(pc.getId(), 830));
                        pc.sendPackets(new S_HPUpdate(pc));
                        pc.sendPackets(new S_MPUpdate(pc));
                        if (pc.isInParty()) {
                            pc.getParty().updateMiniHP(pc);
                        }
                    } else {
                        pc.sendPackets(new S_ServerMessage(337, "$4"));
                    }
                }
            } else if (npctemp.get_npcId() == 71002) {
                if ((cmd.equalsIgnoreCase("0")) && (pc.getLevel() <= 13)) {
                    L1SkillUse skillUse = new L1SkillUse();
                    skillUse.handleCommands(pc, 44, pc.getId(), pc.getX(), pc.getY(), 0, 3, (L1NpcInstance) obj);
                    htmlid = "";
                }
            } else if (npctemp.get_npcId() == 71025) {
                if (cmd.equalsIgnoreCase("0")) {
                    int[] item_ids = {41225};
                    int[] item_amounts = {1};
                    for (int i = 0; i < item_ids.length; i++) {
                        L1ItemInstance item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
                        pc.sendPackets(new S_ServerMessage(143, npctemp.get_name(), item.getItem().getNameId()));
                    }
                    htmlid = "jpe0083";
                }
            } else if (npctemp.get_npcId() == 71055) {
                if (cmd.equalsIgnoreCase("0")) {
                    int[] item_ids = {40701};
                    int[] item_amounts = {1};
                    for (int i = 0; i < item_ids.length; i++) {
                        L1ItemInstance item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
                        pc.sendPackets(new S_ServerMessage(143, npctemp.get_name(), item.getItem().getNameId()));
                    }
                    pc.getQuest().set_step(23, 1);
                    htmlid = "lukein8";
                } else if (cmd.equalsIgnoreCase("2")) {
                    htmlid = "lukein12";
                    pc.getQuest().set_step(30, 3);
                }
            } else if (npctemp.get_npcId() == 70028) {
                if (cmd.equalsIgnoreCase("NUL")) {
                    materials = new int[]{40308, 40014};
                    counts = new int[]{1000, 3};
                    createitem = new int[]{42026};
                    createcount = new int[]{1};
                    htmlid = "";
                } else if (cmd.equalsIgnoreCase("SI")) {
                    materials = new int[]{40308, 40068};
                    counts = new int[]{1000, 3};
                    createitem = new int[]{42025};
                    createcount = new int[]{1};
                    htmlid = "";
                } else if (cmd.equalsIgnoreCase("RS")) {
                    materials = new int[]{40308, 40016};
                    counts = new int[]{1000, 3};
                    createitem = new int[]{42028};
                    createcount = new int[]{1};
                    htmlid = "";
                } else if (cmd.equalsIgnoreCase("B")) {
                    materials = new int[]{40308, 40015};
                    counts = new int[]{1000, 3};
                    createitem = new int[]{42027};
                    createcount = new int[]{1};
                    htmlid = "";
                } else if (cmd.equalsIgnoreCase("F")) {
                    materials = new int[]{40308, 40013};
                    counts = new int[]{1000, 3};
                    createitem = new int[]{42023};
                    createcount = new int[]{1};
                    htmlid = "";
                } else if (cmd.equalsIgnoreCase("K")) {
                    materials = new int[]{40308, 40032};
                    counts = new int[]{1000, 3};
                    createitem = new int[]{42024};
                    createcount = new int[]{1};
                    htmlid = "";
                } else if (cmd.equalsIgnoreCase("Z")) {
                    materials = new int[]{40308, 40088};
                    counts = new int[]{1000, 3};
                    createitem = new int[]{42029};
                    createcount = new int[]{1};
                    htmlid = "";
                }
            } else if (npctemp.get_npcId() == 71063) {
                if (cmd.equalsIgnoreCase("0")) {
                    materials = new int[]{40701};
                    counts = new int[]{1};
                    createitem = new int[]{40702};
                    createcount = new int[]{1};
                    htmlid = "maptbox1";
                    pc.getQuest().set_end(24);
                    int[] nextbox = {1, 2, 3};
                    int pid = _random.nextInt(nextbox.length);
                    int nb = nextbox[pid];
                    if (nb == 1) {
                        pc.getQuest().set_step(23, 2);
                    } else if (nb == 2) {
                        pc.getQuest().set_step(23, 3);
                    } else if (nb == 3) {
                        pc.getQuest().set_step(23, 4);
                    }
                }
            } else if ((npctemp.get_npcId() == 71064) || (npctemp.get_npcId() == 71065) || (npctemp.get_npcId() == 71066)) {
                if (cmd.equalsIgnoreCase("0")) {
                    materials = new int[]{40701};
                    counts = new int[]{1};
                    createitem = new int[]{40702};
                    createcount = new int[]{1};
                    htmlid = "maptbox1";
                    pc.getQuest().set_end(25);
                    int[] nextbox2 = {1, 2, 3, 4, 5, 6};
                    int pid = _random.nextInt(nextbox2.length);
                    int nb2 = nextbox2[pid];
                    if (nb2 == 1) {
                        pc.getQuest().set_step(23, 5);
                    } else if (nb2 == 2) {
                        pc.getQuest().set_step(23, 6);
                    } else if (nb2 == 3) {
                        pc.getQuest().set_step(23, 7);
                    } else if (nb2 == 4) {
                        pc.getQuest().set_step(23, 8);
                    } else if (nb2 == 5) {
                        pc.getQuest().set_step(23, 9);
                    } else if (nb2 == 6) {
                        pc.getQuest().set_step(23, 10);
                    }
                }
            } else if (npctemp.get_npcId() == 71056) {
                if (cmd.equalsIgnoreCase("a")) {
                    pc.getQuest().set_step(27, 1);
                    htmlid = "SIMIZZ7";
                } else if (cmd.equalsIgnoreCase("b")) {
                    if ((pc.getInventory().checkItem(40661)) && (pc.getInventory().checkItem(40662)) && (pc.getInventory().checkItem(40663))) {
                        htmlid = "SIMIZZ8";
                        pc.getQuest().set_step(27, 2);
                        materials = new int[]{40661, 40662, 40663};
                        counts = new int[]{1, 1, 1};
                        createitem = new int[]{20044};
                        createcount = new int[]{1};
                    } else {
                        htmlid = "SIMIZZ9";
                    }
                } else if (cmd.equalsIgnoreCase("d")) {
                    htmlid = "SIMIZZ12";
                    pc.getQuest().set_step(27, 255);
                }
            } else if (npctemp.get_npcId() == 71057) {
                if (cmd.equalsIgnoreCase("3")) {
                    htmlid = "doil4";
                } else if (cmd.equalsIgnoreCase("6")) {
                    htmlid = "doil6";
                } else if (cmd.equalsIgnoreCase("1")) {
                    if (pc.getInventory().checkItem(40714)) {
                        htmlid = "doil8";
                        materials = new int[]{40714};
                        counts = new int[]{1};
                        createitem = new int[]{40647};
                        createcount = new int[]{1};
                        pc.getQuest().set_step(28, 255);
                    } else {
                        htmlid = "doil7";
                    }
                }
            } else if (npctemp.get_npcId() == 71059) {
                if (cmd.equalsIgnoreCase("A")) {
                    htmlid = "rudian6";
                    int[] item_ids = {40700};
                    int[] item_amounts = {1};
                    for (int i = 0; i < item_ids.length; i++) {
                        L1ItemInstance item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
                        pc.sendPackets(new S_ServerMessage(143, npctemp.get_name(), item.getItem().getNameId()));
                    }
                    pc.getQuest().set_step(29, 1);
                } else if (cmd.equalsIgnoreCase("B")) {
                    if (pc.getInventory().checkItem(40710)) {
                        htmlid = "rudian8";
                        materials = new int[]{40700, 40710};
                        counts = new int[]{1, 1};
                        createitem = new int[]{40647};
                        createcount = new int[]{1};
                        pc.getQuest().set_step(29, 255);
                    } else {
                        htmlid = "rudian9";
                    }
                }
            } else if (npctemp.get_npcId() == 71060) {
                if (cmd.equalsIgnoreCase("A")) {
                    if (pc.getQuest().get_step(29) == 255) {
                        htmlid = "resta6";
                    } else {
                        htmlid = "resta4";
                    }
                } else if (cmd.equalsIgnoreCase("B")) {
                    htmlid = "resta10";
                    pc.getQuest().set_step(30, 2);
                }
            } else if (npctemp.get_npcId() == 71061) {
                if (cmd.equalsIgnoreCase("A")) {
                    if (pc.getInventory().checkItem(40647, 3L)) {
                        htmlid = "cadmus6";
                        pc.getInventory().consumeItem(40647, 3L);
                        pc.getQuest().set_step(31, 2);
                    } else {
                        htmlid = "cadmus5";
                        pc.getQuest().set_step(31, 1);
                    }
                }
            } else if (npctemp.get_npcId() == 71036) {
                if (cmd.equalsIgnoreCase("a")) {
                    htmlid = "kamyla7";
                    pc.getQuest().set_step(32, 1);
                } else if (cmd.equalsIgnoreCase("c")) {
                    htmlid = "kamyla10";
                    pc.getInventory().consumeItem(40644, 1L);
                    pc.getQuest().set_step(32, 3);
                } else if (cmd.equalsIgnoreCase("e")) {
                    htmlid = "kamyla13";
                    pc.getInventory().consumeItem(40630, 1L);
                    pc.getQuest().set_step(32, 4);
                } else if (cmd.equalsIgnoreCase("i")) {
                    htmlid = "kamyla25";
                } else if (cmd.equalsIgnoreCase("b")) {
                    if (pc.getQuest().get_step(32) == 1) {
                        L1Teleport.teleport(pc, 32679, 32742, (short) 482, 5, true);
                    }
                } else if (cmd.equalsIgnoreCase("d")) {
                    if (pc.getQuest().get_step(32) == 3) {
                        L1Teleport.teleport(pc, 32736, 32800, (short) 483, 5, true);
                    }
                } else if ((cmd.equalsIgnoreCase("f")) && (pc.getQuest().get_step(32) == 4)) {
                    L1Teleport.teleport(pc, 32746, 32807, (short) 484, 5, true);
                }
            } else if (npctemp.get_npcId() == 71089) {
                if (cmd.equalsIgnoreCase("a")) {
                    htmlid = "francu10";
                    int[] item_ids = {40644};
                    int[] item_amounts = {1};
                    for (int i = 0; i < item_ids.length; i++) {
                        L1ItemInstance item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
                        pc.sendPackets(new S_ServerMessage(143, npctemp.get_name(), item.getItem().getNameId()));
                        pc.getQuest().set_step(32, 2);
                    }
                }
            } else if (npctemp.get_npcId() == 71090) {
                if (cmd.equalsIgnoreCase("a")) {
                    htmlid = "";
                    int[] item_ids = {246, 247, 248, 249, 40660};
                    int[] item_amounts = {1, 1, 1, 1, 5};
                    for (int i = 0; i < item_ids.length; i++) {
                        L1ItemInstance item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
                        pc.sendPackets(new S_ServerMessage(143, npctemp.get_name(), item.getItem().getNameId()));
                        pc.getQuest().set_step(33, 1);
                    }
                } else if (cmd.equalsIgnoreCase("b")) {
                    if ((pc.getInventory().checkEquipped(246)) || (pc.getInventory().checkEquipped(247)) || (pc.getInventory().checkEquipped(248)) || (pc.getInventory().checkEquipped(249))) {
                        htmlid = "jcrystal5";
                    } else if (pc.getInventory().checkItem(40660)) {
                        htmlid = "jcrystal4";
                    } else {
                        pc.getInventory().consumeItem(246, 1L);
                        pc.getInventory().consumeItem(247, 1L);
                        pc.getInventory().consumeItem(248, 1L);
                        pc.getInventory().consumeItem(249, 1L);
                        pc.getInventory().consumeItem(40620, 1L);
                        pc.getQuest().set_step(33, 2);
                        L1Teleport.teleport(pc, 32801, 32895, (short) 483, 4, true);
                    }
                } else if (cmd.equalsIgnoreCase("c")) {
                    if ((pc.getInventory().checkEquipped(246)) || (pc.getInventory().checkEquipped(247)) || (pc.getInventory().checkEquipped(248)) || (pc.getInventory().checkEquipped(249))) {
                        htmlid = "jcrystal5";
                    } else {
                        pc.getInventory().checkItem(40660);
                        L1ItemInstance l1iteminstance = pc.getInventory().findItemId(40660);
                        long sc = l1iteminstance.getCount();
                        if (sc > 0L) {
                            pc.getInventory().consumeItem(40660, sc);
                        }
                        pc.getInventory().consumeItem(246, 1L);
                        pc.getInventory().consumeItem(247, 1L);
                        pc.getInventory().consumeItem(248, 1L);
                        pc.getInventory().consumeItem(249, 1L);
                        pc.getInventory().consumeItem(40620, 1L);
                        pc.getQuest().set_step(33, 0);
                        L1Teleport.teleport(pc, 32736, 32800, (short) 483, 4, true);
                    }
                }
            } else if (npctemp.get_npcId() == 71091) {
                if (cmd.equalsIgnoreCase("a")) {
                    htmlid = "";
                    pc.getInventory().consumeItem(40654, 1L);
                    pc.getQuest().set_step(33, 255);
                    L1Teleport.teleport(pc, 32744, 32927, (short) 483, 4, true);
                }
            } else if (npctemp.get_npcId() == 71074) {
                if (cmd.equalsIgnoreCase("A")) {
                    htmlid = "lelder5";
                    pc.getQuest().set_step(34, 1);
                } else if (cmd.equalsIgnoreCase("B")) {
                    htmlid = "lelder10";
                    pc.getInventory().consumeItem(40633, 1L);
                    pc.getQuest().set_step(34, 3);
                } else if (cmd.equalsIgnoreCase("C")) {
                    htmlid = "lelder13";
                    pc.getQuest().get_step(34);
                    materials = new int[]{40634};
                    counts = new int[]{1};
                    createitem = new int[]{20167};
                    createcount = new int[]{1};
                    pc.getQuest().set_step(34, 255);
                }
            } else if (npctemp.get_npcId() == 80079) {
                if (cmd.equalsIgnoreCase("0")) {
                    if (!pc.getInventory().checkItem(41312)) {
                        L1ItemInstance item = pc.getInventory().storeItem(41312, 1L);
                        if (item != null) {
                            pc.sendPackets(new S_ServerMessage(143, npctemp.get_name(), item.getItem().getNameId()));
                            pc.getQuest().set_end(35);
                        }
                        htmlid = "keplisha7";
                    }
                } else if (cmd.equalsIgnoreCase("1")) {
                    if (!pc.getInventory().checkItem(41314)) {
                        if (pc.getInventory().checkItem(40308, 1000L)) {
                            materials = new int[]{40308, 41313};
                            counts = new int[]{1000, 1};
                            createitem = new int[]{41314};
                            createcount = new int[]{1};
                            int htmlA = _random.nextInt(3) + 1;
                            int htmlB = _random.nextInt(100) + 1;
                            switch (htmlA) {
                                case 1:
                                    htmlid = "horosa" + htmlB;
                                    break;
                                case 2:
                                    htmlid = "horosb" + htmlB;
                                    break;
                                case 3:
                                    htmlid = "horosc" + htmlB;
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            htmlid = "keplisha8";
                        }
                    }
                } else if (cmd.equalsIgnoreCase("2")) {
                    if (pc.getTempCharGfx() != pc.getClassId()) {
                        htmlid = "keplisha9";
                    } else if (pc.getInventory().checkItem(41314)) {
                        pc.getInventory().consumeItem(41314, 1L);
                        int html = _random.nextInt(9) + 1;
                        int PolyId = 6180 + _random.nextInt(64);
                        polyByKeplisha(client, PolyId);
                        switch (html) {
                            case 1:
                                htmlid = "horomon11";
                                break;
                            case 2:
                                htmlid = "horomon12";
                                break;
                            case 3:
                                htmlid = "horomon13";
                                break;
                            case 4:
                                htmlid = "horomon21";
                                break;
                            case 5:
                                htmlid = "horomon22";
                                break;
                            case 6:
                                htmlid = "horomon23";
                                break;
                            case 7:
                                htmlid = "horomon31";
                                break;
                            case 8:
                                htmlid = "horomon32";
                                break;
                            case 9:
                                htmlid = "horomon33";
                                break;
                            default:
                                break;
                        }
                    }
                } else if (cmd.equalsIgnoreCase("3")) {
                    if (pc.getInventory().checkItem(41312)) {
                        pc.getInventory().consumeItem(41312, 1L);
                        htmlid = "";
                    }
                    if (pc.getInventory().checkItem(41313)) {
                        pc.getInventory().consumeItem(41313, 1L);
                        htmlid = "";
                    }
                    if (pc.getInventory().checkItem(41314)) {
                        pc.getInventory().consumeItem(41314, 1L);
                        htmlid = "";
                    }
                }
            } else if (npctemp.get_npcId() == 80084) {
                if (cmd.equalsIgnoreCase("q")) {
                    if (pc.getInventory().checkItem(41356, 1L)) {
                        htmlid = "rparum4";
                    } else {
                        L1ItemInstance item = pc.getInventory().storeItem(41356, 1L);
                        if (item != null) {
                            pc.sendPackets(new S_ServerMessage(143, npctemp.get_name(), item.getItem().getNameId()));
                        }
                        htmlid = "rparum3";
                    }
                }
            } else if (npctemp.get_npcId() == 80105) {
                if ((cmd.equalsIgnoreCase("c")) && (pc.isCrown()) && (pc.getInventory().checkItem(20383, 1L))) {
                    if (pc.getInventory().checkItem(40308, 100000L)) {
                        L1ItemInstance item = pc.getInventory().findItemId(20383);
                        if ((item != null) && (item.getChargeCount() != 50)) {
                            item.setChargeCount(50);
                            pc.getInventory().updateItem(item, 128);
                            pc.getInventory().consumeItem(40308, 100000L);
                            htmlid = "";
                        }
                    } else {
                        pc.sendPackets(new S_ServerMessage(337, "$4"));
                    }
                }
            } else if (npctemp.get_npcId() == 71126) {
                if (cmd.equalsIgnoreCase("B")) {
                    if (pc.getInventory().checkItem(41007, 1L)) {
                        htmlid = "eris10";
                    } else {
                        L1NpcInstance npc = (L1NpcInstance) obj;
                        L1ItemInstance item = pc.getInventory().storeItem(41007, 1L);
                        String npcName = npc.getNpcTemplate().get_name();
                        String itemName = item.getItem().getNameId();
                        pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                        htmlid = "eris6";
                    }
                } else if (cmd.equalsIgnoreCase("C")) {
                    if (pc.getInventory().checkItem(41009, 1L)) {
                        htmlid = "eris10";
                    } else {
                        L1NpcInstance npc = (L1NpcInstance) obj;
                        L1ItemInstance item = pc.getInventory().storeItem(41009, 1L);
                        String npcName = npc.getNpcTemplate().get_name();
                        String itemName = item.getItem().getNameId();
                        pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                        htmlid = "eris8";
                    }
                } else if (cmd.equalsIgnoreCase("A")) {
                    if (pc.getInventory().checkItem(41007, 1L)) {
                        if (pc.getInventory().checkItem(40969, 20L)) {
                            htmlid = "eris18";
                            materials = new int[]{40969, 41007};
                            counts = new int[]{20, 1};
                            createitem = new int[]{41008};
                            createcount = new int[]{1};
                        } else {
                            htmlid = "eris5";
                        }
                    } else {
                        htmlid = "eris2";
                    }
                } else if (cmd.equalsIgnoreCase("E")) {
                    if (pc.getInventory().checkItem(41010, 1L)) {
                        htmlid = "eris19";
                    } else {
                        htmlid = "eris7";
                    }
                } else if (cmd.equalsIgnoreCase("D")) {
                    if (pc.getInventory().checkItem(41010, 1L)) {
                        htmlid = "eris19";
                    } else if (pc.getInventory().checkItem(41009, 1L)) {
                        if (pc.getInventory().checkItem(40959, 1L)) {
                            htmlid = "eris17";
                            materials = new int[]{40959, 41009};
                            counts = new int[]{1, 1};
                            createitem = new int[]{41010};
                            createcount = new int[]{1};
                        } else if (pc.getInventory().checkItem(40960, 1L)) {
                            htmlid = "eris16";
                            materials = new int[]{40960, 41009};
                            counts = new int[]{1, 1};
                            createitem = new int[]{41010};
                            createcount = new int[]{1};
                        } else if (pc.getInventory().checkItem(40961, 1L)) {
                            htmlid = "eris15";
                            materials = new int[]{40961, 41009};
                            counts = new int[]{1, 1};
                            createitem = new int[]{41010};
                            createcount = new int[]{1};
                        } else if (pc.getInventory().checkItem(40962, 1L)) {
                            htmlid = "eris14";
                            materials = new int[]{40962, 41009};
                            counts = new int[]{1, 1};
                            createitem = new int[]{41010};
                            createcount = new int[]{1};
                        } else if (pc.getInventory().checkItem(40635, 10L)) {
                            htmlid = "eris12";
                            materials = new int[]{40635, 41009};
                            counts = new int[]{10, 1};
                            createitem = new int[]{41010};
                            createcount = new int[]{1};
                        } else if (pc.getInventory().checkItem(40638, 10L)) {
                            htmlid = "eris11";
                            materials = new int[]{40638, 41009};
                            counts = new int[]{10, 1};
                            createitem = new int[]{41010};
                            createcount = new int[]{1};
                        } else if (pc.getInventory().checkItem(40642, 10L)) {
                            htmlid = "eris13";
                            materials = new int[]{40642, 41009};
                            counts = new int[]{10, 1};
                            createitem = new int[]{41010};
                            createcount = new int[]{1};
                        } else if (pc.getInventory().checkItem(40667, 10L)) {
                            htmlid = "eris13";
                            materials = new int[]{40667, 41009};
                            counts = new int[]{10, 1};
                            createitem = new int[]{41010};
                            createcount = new int[]{1};
                        } else {
                            htmlid = "eris8";
                        }
                    } else {
                        htmlid = "eris7";
                    }
                }
            } else if (npctemp.get_npcId() == 80076) {
                if (cmd.equalsIgnoreCase("A")) {
                    int[] diaryno = {49082, 49083};
                    int pid = _random.nextInt(diaryno.length);
                    int di = diaryno[pid];
                    if (di == 49082) {
                        htmlid = "voyager6a";
                        L1NpcInstance npc = (L1NpcInstance) obj;
                        L1ItemInstance item = pc.getInventory().storeItem(di, 1L);
                        String npcName = npc.getNpcTemplate().get_name();
                        String itemName = item.getItem().getNameId();
                        pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                    } else if (di == 49083) {
                        htmlid = "voyager6b";
                        L1NpcInstance npc = (L1NpcInstance) obj;
                        L1ItemInstance item = pc.getInventory().storeItem(di, 1L);
                        String npcName = npc.getNpcTemplate().get_name();
                        String itemName = item.getItem().getNameId();
                        pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                    }
                }
            } else if (npctemp.get_npcId() == 71128) {
                switch (cmd) {
                    case "A":
                        if (pc.getInventory().checkItem(41010, 1L)) {
                            htmlid = "perita2";
                        } else {
                            htmlid = "perita3";
                        }
                        break;
                    case "p":
                        if ((pc.getInventory().checkItem(40987, 1L)) && (pc.getInventory().checkItem(40988, 1L)) && (pc.getInventory().checkItem(40989, 1L))) {
                            htmlid = "perita43";
                        } else if ((pc.getInventory().checkItem(40987, 1L)) && (pc.getInventory().checkItem(40989, 1L))) {
                            htmlid = "perita44";
                        } else if ((pc.getInventory().checkItem(40987, 1L)) && (pc.getInventory().checkItem(40988, 1L))) {
                            htmlid = "perita45";
                        } else if ((pc.getInventory().checkItem(40988, 1L)) && (pc.getInventory().checkItem(40989, 1L))) {
                            htmlid = "perita47";
                        } else if (pc.getInventory().checkItem(40987, 1L)) {
                            htmlid = "perita46";
                        } else if (pc.getInventory().checkItem(40988, 1L)) {
                            htmlid = "perita49";
                        } else if (pc.getInventory().checkItem(40987, 1L)) {
                            htmlid = "perita48";
                        } else {
                            htmlid = "perita50";
                        }
                        break;
                    case "q":
                        if ((pc.getInventory().checkItem(41173, 1L)) && (pc.getInventory().checkItem(41174, 1L)) && (pc.getInventory().checkItem(41175, 1L))) {
                            htmlid = "perita54";
                        } else if ((pc.getInventory().checkItem(41173, 1L)) && (pc.getInventory().checkItem(41175, 1L))) {
                            htmlid = "perita55";
                        } else if ((pc.getInventory().checkItem(41173, 1L)) && (pc.getInventory().checkItem(41174, 1L))) {
                            htmlid = "perita56";
                        } else if ((pc.getInventory().checkItem(41174, 1L)) && (pc.getInventory().checkItem(41175, 1L))) {
                            htmlid = "perita58";
                        } else if (pc.getInventory().checkItem(41174, 1L)) {
                            htmlid = "perita57";
                        } else if (pc.getInventory().checkItem(41175, 1L)) {
                            htmlid = "perita60";
                        } else if (pc.getInventory().checkItem(41176, 1L)) {
                            htmlid = "perita59";
                        } else {
                            htmlid = "perita61";
                        }
                        break;
                    case "s":
                        if ((pc.getInventory().checkItem(41161, 1L)) && (pc.getInventory().checkItem(41162, 1L)) && (pc.getInventory().checkItem(41163, 1L))) {
                            htmlid = "perita62";
                        } else if ((pc.getInventory().checkItem(41161, 1L)) && (pc.getInventory().checkItem(41163, 1L))) {
                            htmlid = "perita63";
                        } else if ((pc.getInventory().checkItem(41161, 1L)) && (pc.getInventory().checkItem(41162, 1L))) {
                            htmlid = "perita64";
                        } else if ((pc.getInventory().checkItem(41162, 1L)) && (pc.getInventory().checkItem(41163, 1L))) {
                            htmlid = "perita66";
                        } else if (pc.getInventory().checkItem(41161, 1L)) {
                            htmlid = "perita65";
                        } else if (pc.getInventory().checkItem(41162, 1L)) {
                            htmlid = "perita68";
                        } else if (pc.getInventory().checkItem(41163, 1L)) {
                            htmlid = "perita67";
                        } else {
                            htmlid = "perita69";
                        }
                        break;
                    case "B":
                        if ((pc.getInventory().checkItem(40651, 10L)) && (pc.getInventory().checkItem(40643, 10L)) && (pc.getInventory().checkItem(40618, 10L)) && (pc.getInventory().checkItem(40645, 10L)) && (pc.getInventory().checkItem(40676, 10L)) && (pc.getInventory().checkItem(40442, 5L)) && (pc.getInventory().checkItem(40051, 1L))) {
                            htmlid = "perita7";
                            materials = new int[]{40651, 40643, 40618, 40645, 40676, 40442, 40051};
                            counts = new int[]{10, 10, 10, 10, 20, 5, 1};
                            createitem = new int[]{40925};
                            createcount = new int[]{1};
                        } else {
                            htmlid = "perita8";
                        }
                        break;
                    case "G":
                    case "h":
                    case "i":
                        if ((pc.getInventory().checkItem(40651, 5L)) && (pc.getInventory().checkItem(40643, 5L)) && (pc.getInventory().checkItem(40618, 5L)) && (pc.getInventory().checkItem(40645, 5L)) && (pc.getInventory().checkItem(40676, 5L)) && (pc.getInventory().checkItem(40675, 5L)) && (pc.getInventory().checkItem(40049, 3L)) && (pc.getInventory().checkItem(40051, 1L))) {
                            htmlid = "perita27";
                            materials = new int[]{40651, 40643, 40618, 40645, 40676, 40675, 40049, 40051};
                            counts = new int[]{5, 5, 5, 5, 10, 10, 3, 1};
                            createitem = new int[]{40926};
                            createcount = new int[]{1};
                        } else {
                            htmlid = "perita28";
                        }
                        break;
                    case "H":
                    case "j":
                    case "k":
                        if ((pc.getInventory().checkItem(40651, 10L)) && (pc.getInventory().checkItem(40643, 10L)) && (pc.getInventory().checkItem(40618, 10L)) && (pc.getInventory().checkItem(40645, 10L)) && (pc.getInventory().checkItem(40676, 20L)) && (pc.getInventory().checkItem(40675, 10L)) && (pc.getInventory().checkItem(40048, 3L)) && (pc.getInventory().checkItem(40051, 1L))) {
                            htmlid = "perita29";
                            materials = new int[]{40651, 40643, 40618, 40645, 40676, 40675, 40048, 40051};
                            counts = new int[]{10, 10, 10, 10, 20, 10, 3, 1};
                            createitem = new int[]{40927};
                            createcount = new int[]{1};
                        } else {
                            htmlid = "perita30";
                        }
                        break;
                    case "I":
                    case "l":
                    case "m":
                        if ((pc.getInventory().checkItem(40651, 20L)) && (pc.getInventory().checkItem(40643, 20L)) && (pc.getInventory().checkItem(40618, 20L)) && (pc.getInventory().checkItem(40645, 20L)) && (pc.getInventory().checkItem(40676, 30L)) && (pc.getInventory().checkItem(40675, 10L)) && (pc.getInventory().checkItem(40050, 3L)) && (pc.getInventory().checkItem(40051, 1L))) {
                            htmlid = "perita31";
                            materials = new int[]{40651, 40643, 40618, 40645, 40676, 40675, 40050, 40051};
                            counts = new int[]{20, 20, 20, 20, 30, 10, 3, 1};
                            createitem = new int[]{40928};
                            createcount = new int[]{1};
                        } else {
                            htmlid = "perita32";
                        }
                        break;
                    case "J":
                    case "n":
                    case "o":
                        if ((pc.getInventory().checkItem(40651, 30L)) && (pc.getInventory().checkItem(40643, 30L)) && (pc.getInventory().checkItem(40618, 30L)) && (pc.getInventory().checkItem(40645, 30L)) && (pc.getInventory().checkItem(40676, 30L)) && (pc.getInventory().checkItem(40675, 20L)) && (pc.getInventory().checkItem(40052, 1L)) && (pc.getInventory().checkItem(40051, 1L))) {
                            htmlid = "perita33";
                            materials = new int[]{40651, 40643, 40618, 40645, 40676, 40675, 40052, 40051};
                            counts = new int[]{30, 30, 30, 30, 30, 20, 1, 1};
                            createitem = new int[]{40928};
                            createcount = new int[]{1};
                        } else {
                            htmlid = "perita34";
                        }
                        break;
                    case "K":
                        int earinga = 0;
                        int earingb = 0;
                        if ((pc.getInventory().checkEquipped(21014)) || (pc.getInventory().checkEquipped(21006)) || (pc.getInventory().checkEquipped(21007))) {
                            htmlid = "perita36";
                        } else if (pc.getInventory().checkItem(21014, 1L)) {
                            earinga = 21014;
                            earingb = 41176;
                        } else if (pc.getInventory().checkItem(21006, 1L)) {
                            earinga = 21006;
                            earingb = 41177;
                        } else if (pc.getInventory().checkItem(21007, 1L)) {
                            earinga = 21007;
                            earingb = 41178;
                        } else {
                            htmlid = "perita36";
                        }
                        if (earinga > 0) {
                            materials = new int[]{earinga};
                            counts = new int[]{1};
                            createitem = new int[]{earingb};
                            createcount = new int[]{1};
                        }
                        break;
                    case "L":
                        if (pc.getInventory().checkEquipped(21015)) {
                            htmlid = "perita22";
                        } else if (pc.getInventory().checkItem(21015, 1L)) {
                            materials = new int[]{21015};
                            counts = new int[]{1};
                            createitem = new int[]{41179};
                            createcount = new int[]{1};
                        } else {
                            htmlid = "perita22";
                        }
                        break;
                    case "M":
                        if (pc.getInventory().checkEquipped(21016)) {
                            htmlid = "perita26";
                        } else if (pc.getInventory().checkItem(21016, 1L)) {
                            materials = new int[]{21016};
                            counts = new int[]{1};
                            createitem = new int[]{41182};
                            createcount = new int[]{1};
                        } else {
                            htmlid = "perita26";
                        }
                        break;
                    case "b":
                        if (pc.getInventory().checkEquipped(21009)) {
                            htmlid = "perita39";
                        } else if (pc.getInventory().checkItem(21009, 1L)) {
                            materials = new int[]{21009};
                            counts = new int[]{1};
                            createitem = new int[]{41180};
                            createcount = new int[]{1};
                        } else {
                            htmlid = "perita39";
                        }
                        break;
                    case "d":
                        if (pc.getInventory().checkEquipped(21012)) {
                            htmlid = "perita41";
                        } else if (pc.getInventory().checkItem(21012, 1L)) {
                            materials = new int[]{21012};
                            counts = new int[]{1};
                            createitem = new int[]{41183};
                            createcount = new int[]{1};
                        } else {
                            htmlid = "perita41";
                        }
                        break;
                    case "a":
                        if (pc.getInventory().checkEquipped(21008)) {
                            htmlid = "perita38";
                        } else if (pc.getInventory().checkItem(21008, 1L)) {
                            materials = new int[]{21008};
                            counts = new int[]{1};
                            createitem = new int[]{41181};
                            createcount = new int[]{1};
                        } else {
                            htmlid = "perita38";
                        }
                        break;
                    case "c":
                        if (pc.getInventory().checkEquipped(21010)) {
                            htmlid = "perita40";
                        } else if (pc.getInventory().checkItem(21010, 1L)) {
                            materials = new int[]{21010};
                            counts = new int[]{1};
                            createitem = new int[]{41184};
                            createcount = new int[]{1};
                        } else {
                            htmlid = "perita40";
                        }
                        break;
                }
            } else if (npctemp.get_npcId() == 71129) {
                switch (cmd) {
                    case "Z":
                        htmlid = "rumtis2";
                        break;
                    case "Y":
                        if (pc.getInventory().checkItem(41010, 1L)) {
                            htmlid = "rumtis3";
                        } else {
                            htmlid = "rumtis4";
                        }
                        break;
                    case "q":
                        htmlid = "rumtis92";
                        break;
                    case "A":
                        if (pc.getInventory().checkItem(41161, 1L)) {
                            htmlid = "rumtis6";
                        } else {
                            htmlid = "rumtis101";
                        }
                        break;
                    case "B":
                        if (pc.getInventory().checkItem(41164, 1L)) {
                            htmlid = "rumtis7";
                        } else {
                            htmlid = "rumtis101";
                        }
                        break;
                    case "C":
                        if (pc.getInventory().checkItem(41167, 1L)) {
                            htmlid = "rumtis8";
                        } else {
                            htmlid = "rumtis101";
                        }
                        break;
                    case "T":
                        if (pc.getInventory().checkItem(41167, 1L)) {
                            htmlid = "rumtis9";
                        } else {
                            htmlid = "rumtis101";
                        }
                        break;
                    case "w":
                        if (pc.getInventory().checkItem(41162, 1L)) {
                            htmlid = "rumtis14";
                        } else {
                            htmlid = "rumtis101";
                        }
                        break;
                    case "x":
                        if (pc.getInventory().checkItem(41165, 1L)) {
                            htmlid = "rumtis15";
                        } else {
                            htmlid = "rumtis101";
                        }
                        break;
                    case "y":
                        if (pc.getInventory().checkItem(41168, 1L)) {
                            htmlid = "rumtis16";
                        } else {
                            htmlid = "rumtis101";
                        }
                        break;
                    case "z":
                        if (pc.getInventory().checkItem(41171, 1L)) {
                            htmlid = "rumtis17";
                        } else {
                            htmlid = "rumtis101";
                        }
                        break;
                    case "U":
                        if (pc.getInventory().checkItem(41163, 1L)) {
                            htmlid = "rumtis10";
                        } else {
                            htmlid = "rumtis101";
                        }
                        break;
                    case "V":
                        if (pc.getInventory().checkItem(41166, 1L)) {
                            htmlid = "rumtis11";
                        } else {
                            htmlid = "rumtis101";
                        }
                        break;
                    case "W":
                        if (pc.getInventory().checkItem(41169, 1L)) {
                            htmlid = "rumtis12";
                        } else {
                            htmlid = "rumtis101";
                        }
                        break;
                    case "X":
                        if (pc.getInventory().checkItem(41172, 1L)) {
                            htmlid = "rumtis13";
                        } else {
                            htmlid = "rumtis101";
                        }
                        break;
                    case "D":
                    case "E":
                    case "F":
                    case "G":
                        int insn = 0;
                        int bacn = 0;
                        int me = 0;
                        int mr = 0;
                        int mj = 0;
                        int an = 0;
                        int men = 0;
                        int mrn = 0;
                        int mjn = 0;
                        int ann = 0;
                        if ((pc.getInventory().checkItem(40959, 1L)) && (pc.getInventory().checkItem(40960, 1L)) && (pc.getInventory().checkItem(40961, 1L)) && (pc.getInventory().checkItem(40962, 1L))) {
                            insn = 1;
                            me = 40959;
                            mr = 40960;
                            mj = 40961;
                            an = 40962;
                            men = 1;
                            mrn = 1;
                            mjn = 1;
                            ann = 1;
                        } else if ((pc.getInventory().checkItem(40642, 10L)) && (pc.getInventory().checkItem(40635, 10L)) && (pc.getInventory().checkItem(40638, 10L)) && (pc.getInventory().checkItem(40667, 10L))) {
                            bacn = 1;
                            me = 40642;
                            mr = 40635;
                            mj = 40638;
                            an = 40667;
                            men = 10;
                            mrn = 10;
                            mjn = 10;
                            ann = 10;
                        }
                        if ((pc.getInventory().checkItem(40046, 1L)) && (pc.getInventory().checkItem(40618, 5L)) && (pc.getInventory().checkItem(40643, 5L)) && (pc.getInventory().checkItem(40645, 5L)) && (pc.getInventory().checkItem(40651, 5L)) && (pc.getInventory().checkItem(40676, 5L))) {
                            if ((insn == 1) || (bacn == 1)) {
                                htmlid = "rumtis60";
                                materials = new int[]{me, mr, mj, an, 40046, 40618, 40643, 40651, 40676};
                                counts = new int[]{men, mrn, mjn, ann, 1, 5, 5, 5, 5, 5};
                                createitem = new int[]{40926};
                                createcount = new int[]{1};
                            } else {
                                htmlid = "rumtis18";
                            }
                        }
                        break;
                }
            } else if (npctemp.get_npcId() == 71119) {
                if (cmd.equalsIgnoreCase("request las history book")) {
                    materials = new int[]{41019, 41020, 41021, 41022, 41023, 41024, 41025, 41026};
                    counts = new int[]{1, 1, 1, 1, 1, 1, 1, 1};
                    createitem = new int[]{41027};
                    createcount = new int[]{1};
                    htmlid = "";
                }
            } else if (npctemp.get_npcId() == 71170) {
                if (cmd.equalsIgnoreCase("request las weapon manual")) {
                    materials = new int[]{41027};
                    counts = new int[]{1};
                    createitem = new int[]{40965};
                    createcount = new int[]{1};
                    htmlid = "";
                }
            } else if (npctemp.get_npcId() == 71168) {
                if ((cmd.equalsIgnoreCase("a")) && (pc.getInventory().checkItem(41028, 1L))) {
                    L1Teleport.teleport(pc, 32648, 32921, (short) 535, 6, true);
                    pc.getInventory().consumeItem(41028, 1L);
                }
            } else if (npctemp.get_npcId() == 80067) {
                if (cmd.equalsIgnoreCase("n")) {
                    htmlid = "";
                    poly(client, 6034);
                    int[] item_ids = {41132, 41133, 41134};
                    int[] item_amounts = {1, 1, 1};
                    for (int i = 0; i < item_ids.length; i++) {
                        L1ItemInstance item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
                        pc.sendPackets(new S_ServerMessage(143, npctemp.get_name(), item.getItem().getNameId()));
                        pc.getQuest().set_step(36, 1);
                    }
                } else if (cmd.equalsIgnoreCase("d")) {
                    htmlid = "minicod09";
                    pc.getInventory().consumeItem(41130, 1L);
                    pc.getInventory().consumeItem(41131, 1L);
                } else if (cmd.equalsIgnoreCase("k")) {
                    htmlid = "";
                    pc.getInventory().consumeItem(41132, 1L);
                    pc.getInventory().consumeItem(41133, 1L);
                    pc.getInventory().consumeItem(41134, 1L);
                    pc.getInventory().consumeItem(41135, 1L);
                    pc.getInventory().consumeItem(41136, 1L);
                    pc.getInventory().consumeItem(41137, 1L);
                    pc.getInventory().consumeItem(41138, 1L);
                    pc.getQuest().set_step(36, 0);
                } else if (cmd.equalsIgnoreCase("e")) {
                    if ((pc.getQuest().get_step(36) == 255) || (pc.getKarmaLevel() >= 1)) {
                        htmlid = "";
                    } else if (pc.getInventory().checkItem(41138)) {
                        htmlid = "";
                        pc.addKarma((int) (1600.0D * ConfigRate.RATE_KARMA));
                        pc.getInventory().consumeItem(41130, 1L);
                        pc.getInventory().consumeItem(41131, 1L);
                        pc.getInventory().consumeItem(41138, 1L);
                        pc.getQuest().set_step(36, 255);
                    } else {
                        htmlid = "minicod04";
                    }
                } else if (cmd.equalsIgnoreCase("g")) {
                    htmlid = "";
                    int[] item_ids = {41130};
                    int[] item_amounts = {1};
                    for (int i = 0; i < item_ids.length; i++) {
                        L1ItemInstance item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
                        pc.sendPackets(new S_ServerMessage(143, npctemp.get_name(), item.getItem().getNameId()));
                    }
                }
            } else if (npctemp.get_npcId() == 81202) {
                if (cmd.equalsIgnoreCase("n")) {
                    htmlid = "";
                    poly(client, 6035);
                    int[] item_ids = {41123, 41124, 41125};
                    int[] item_amounts = {1, 1, 1};
                    for (int i = 0; i < item_ids.length; i++) {
                        L1ItemInstance item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
                        pc.sendPackets(new S_ServerMessage(143, npctemp.get_name(), item.getItem().getNameId()));
                        pc.getQuest().set_step(37, 1);
                    }
                } else if (cmd.equalsIgnoreCase("d")) {
                    htmlid = "minitos09";
                    pc.getInventory().consumeItem(41121, 1L);
                    pc.getInventory().consumeItem(41122, 1L);
                } else if (cmd.equalsIgnoreCase("k")) {
                    htmlid = "";
                    pc.getInventory().consumeItem(41123, 1L);
                    pc.getInventory().consumeItem(41124, 1L);
                    pc.getInventory().consumeItem(41125, 1L);
                    pc.getInventory().consumeItem(41126, 1L);
                    pc.getInventory().consumeItem(41127, 1L);
                    pc.getInventory().consumeItem(41128, 1L);
                    pc.getInventory().consumeItem(41129, 1L);
                    pc.getQuest().set_step(37, 0);
                } else if (cmd.equalsIgnoreCase("e")) {
                    if ((pc.getQuest().get_step(37) == 255) || (pc.getKarmaLevel() >= 1)) {
                        htmlid = "";
                    } else if (pc.getInventory().checkItem(41129)) {
                        htmlid = "";
                        pc.addKarma((int) (-1600.0D * ConfigRate.RATE_KARMA));
                        pc.getInventory().consumeItem(41121, 1L);
                        pc.getInventory().consumeItem(41122, 1L);
                        pc.getInventory().consumeItem(41129, 1L);
                        pc.getQuest().set_step(37, 255);
                    } else {
                        htmlid = "minitos04";
                    }
                } else if (cmd.equalsIgnoreCase("g")) {
                    htmlid = "";
                    int[] item_ids = {41121};
                    int[] item_amounts = {1};
                    for (int i = 0; i < item_ids.length; i++) {
                        L1ItemInstance item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
                        pc.sendPackets(new S_ServerMessage(143, npctemp.get_name(), item.getItem().getNameId()));
                    }
                }
            }
            // 皮爾斯 製作破壞雙刀 破壞鋼爪
            else if (npctemp.get_npcId() == 70908) {
                int[] oldweapon = {81, 162, 177, 194, 13};
                int newWeapon = 0;
                boolean success = false;
                if (cmd.equalsIgnoreCase("A")) {
                    newWeapon = 410162;
                    for (int i = 0; i < oldweapon.length; i++) {
                        if ((pc.getInventory().checkEnchantItem(oldweapon[i], 8, 1L)) && (pc.getInventory().checkItem(40308, 5000000L))) {
                            pc.getInventory().consumeEnchantItem(oldweapon[i], 8, 1L);
                            pc.getInventory().consumeItem(40308, 5000000L);
                            final L1ItemInstance item = ItemTable.get().createItem(newWeapon);
                            item.setEnchantLevel(7);
                            item.setIdentified(true);
                            pc.getInventory().storeItem(item);
                            pc.sendPackets(new S_ServerMessage(143, npctemp.get_name(), item.getLogName()));
                            success = true;
                            pc.sendPackets(new S_CloseList(pc.getId()));
                            break;
                        }
                    }
                } else if (cmd.equalsIgnoreCase("B")) {
                    newWeapon = 410161;
                    for (int i = 0; i < oldweapon.length; i++) {
                        if ((pc.getInventory().checkEnchantItem(oldweapon[i], 8, 1L)) && (pc.getInventory().checkItem(40308, 5000000L))) {
                            pc.getInventory().consumeEnchantItem(oldweapon[i], 8, 1L);
                            pc.getInventory().consumeItem(40308, 5000000L);
                            final L1ItemInstance item = ItemTable.get().createItem(newWeapon);
                            item.setEnchantLevel(7);
                            item.setIdentified(true);
                            pc.getInventory().storeItem(item);
                            pc.sendPackets(new S_ServerMessage(143, npctemp.get_name(), item.getLogName()));
                            success = true;
                            pc.sendPackets(new S_CloseList(pc.getId()));
                            break;
                        }
                    }
                } else if (cmd.equalsIgnoreCase("C")) {
                    newWeapon = 410162;
                    for (int i = 0; i < oldweapon.length; i++) {
                        if ((pc.getInventory().checkEnchantItem(oldweapon[i], 9, 1L)) && (pc.getInventory().checkItem(40308, 10000000L))) {
                            pc.getInventory().consumeEnchantItem(oldweapon[i], 9, 1L);
                            pc.getInventory().consumeItem(40308, 10000000L);
                            final L1ItemInstance item = ItemTable.get().createItem(newWeapon);
                            item.setEnchantLevel(8);
                            item.setIdentified(true);
                            pc.getInventory().storeItem(item);
                            pc.sendPackets(new S_ServerMessage(143, npctemp.get_name(), item.getLogName()));
                            success = true;
                            pc.sendPackets(new S_CloseList(pc.getId()));
                            break;
                        }
                    }
                } else if (cmd.equalsIgnoreCase("D")) {
                    newWeapon = 410161;
                    for (int i = 0; i < oldweapon.length; i++) {
                        if ((pc.getInventory().checkEnchantItem(oldweapon[i], 9, 1L)) && (pc.getInventory().checkItem(40308, 10000000L))) {
                            pc.getInventory().consumeEnchantItem(oldweapon[i], 9, 1L);
                            pc.getInventory().consumeItem(40308, 10000000L);
                            final L1ItemInstance item = ItemTable.get().createItem(newWeapon);
                            item.setEnchantLevel(8);
                            item.setIdentified(true);
                            pc.getInventory().storeItem(item);
                            pc.sendPackets(new S_ServerMessage(143, npctemp.get_name(), item.getLogName()));
                            success = true;
                            pc.sendPackets(new S_CloseList(pc.getId()));
                            break;
                        }
                    }
                }
                if (!success) {
                    htmlid = "piers04";
                }
            }
            // 卷軸商^夏納
            else if (((L1NpcInstance) obj).getNpcId() == 81246) {
                if (pc.getLevel() < 30) {
                    htmlid = "sharna4";
                } else if (pc.getLevel() >= 30 && pc.getLevel() <= 39) {
                    createitem = new int[]{82225}; // 變身（30）
                } else if (pc.getLevel() >= 40 && pc.getLevel() <= 51) {
                    createitem = new int[]{82226}; // 變身（40）
                } else if (pc.getLevel() >= 52 && pc.getLevel() <= 54) {
                    createitem = new int[]{82227}; // 變身（52）
                } else if (pc.getLevel() >= 55 && pc.getLevel() <= 59) {
                    createitem = new int[]{82228}; // 變身（55）
                } else if (pc.getLevel() >= 60 && pc.getLevel() <= 64) {
                    createitem = new int[]{82229}; // 變身（60）
                } else if (pc.getLevel() >= 65 && pc.getLevel() <= 69) {
                    createitem = new int[]{82230}; // 變身（65）
                } else if (pc.getLevel() >= 70 && pc.getLevel() <= 74) {
                    createitem = new int[]{82231}; // 變身（70）
                } else if (pc.getLevel() >= 75 && pc.getLevel() <= 79) {
                    createitem = new int[]{82232}; // 變身（75）
                } else if (pc.getLevel() >= 80) {
                    createitem = new int[]{82233}; // 變身（80）
                }
                int createCount = 0;
                if (cmd.equalsIgnoreCase(":")) { // 11
                    createCount = 11;
                } else if (cmd.equalsIgnoreCase(";")) { // 12
                    createCount = 12;
                } else if (cmd.equalsIgnoreCase("<")) { // 13
                    createCount = 13;
                } else if (cmd.equalsIgnoreCase("=")) { // 14
                    createCount = 14;
                } else if (cmd.equalsIgnoreCase(">")) { // 15
                    createCount = 15;
                } else if (cmd.equalsIgnoreCase("?")) { // 16
                    createCount = 16;
                } else if (cmd.equalsIgnoreCase("@")) { // 17
                    createCount = 17;
                } else if (cmd.equalsIgnoreCase("A")) { // 18
                    createCount = 18;
                } else if (cmd.equalsIgnoreCase("B")) { // 19
                    createCount = 19;
                } else if (cmd.equalsIgnoreCase("C")) { // 20
                    createCount = 20;
                } else {
                    createCount = Integer.parseInt(cmd) + 1;
                }
                if (createitem != null) {
                    materials = new int[]{40308};
                    createcount = new int[]{createCount};
                    counts = new int[]{2500 * createCount};
                }
                success_htmlid = "sharna3";
                failure_htmlid = "sharna5";
            }
            // 開放欄位專家^史奈普
            else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 81445) {
                // System.out.println("cmd ==" + cmd);
                if (cmd.equalsIgnoreCase("A")) {
                    // System.out.println("slot76");
                    if (pc.getQuest().isEnd(L1PcQuest.QUEST_SLOT76)) {
                        pc.sendPackets(new S_ServerMessage(3254));
                    } else {
                        pc.setSlot(76);
                        pc.sendPackets(new S_Message_YN(3312));
                    }
                } else if (cmd.equalsIgnoreCase("B")) {
                    // System.out.println("slot81");
                    if (pc.getQuest().isEnd(L1PcQuest.QUEST_SLOT81)) {
                        pc.sendPackets(new S_ServerMessage(3254));
                    } else {
                        pc.setSlot(81);
                        pc.sendPackets(new S_Message_YN(3313));
                    }
                } else if (cmd.equalsIgnoreCase("D")) {
                    // System.out.println("slot59");
                    if (pc.getQuest().isEnd(L1PcQuest.QUEST_SLOT59)) {
                        pc.sendPackets(new S_ServerMessage(3254));
                    } else {
                        pc.setSlot(59);
                        pc.sendPackets(new S_Message_YN(3589));
                    }
                }
            }
            // 底比斯沙漠-宙斯之石頭高侖
            else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71253) {
                if (cmd.equalsIgnoreCase("A")) {// 製作龜裂之核
                    if (pc.getInventory().checkItem(49101, 100)) {
                        materials = new int[]{49101};
                        counts = new int[]{100};
                        createitem = new int[]{49092};
                        createcount = new int[]{1};
                        htmlid = "joegolem18";
                    } else {
                        htmlid = "joegolem19";
                    }
                } else if (cmd.equalsIgnoreCase("B")) {// 傳送至亞丁
                    if (pc.getInventory().checkItem(49101, 1)) {
                        pc.getInventory().consumeItem(49101, 1);
                        L1Teleport.teleport(pc, 33966, 33253, (short) 4, 5, true);
                        htmlid = "";
                    } else {
                        htmlid = "joegolem20";
                    }
                }
            }
            /** 歐西裡斯祭壇守門人 */
            else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71255) {
                if (cmd.equalsIgnoreCase("e")) {
                    L1Thebes.enterGame(pc, objid);
                }
            }
            /** 庫庫爾坎祭壇守門人 */
            else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 81241) {
                if (cmd.equalsIgnoreCase("e")) {
                    L1Tikal.enterGame(pc, objid);
                }
            }
            /** 神女艾利爾 */
            else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70702) {
                if (cmd.equalsIgnoreCase("chg")) {
                    if (!pc.getInventory().checkItem(40308, 1000)) {
                        pc.sendPackets(new S_ServerMessage(189)); // f1金幣不足。
                        return;
                    }
                    for (L1ItemInstance item : pc.getInventory().getItems()) {
                        if (item.getItemId() >= 40901 && item.getItemId() <= 40908 && item.getId() == pc.getQuest().get_step(L1PcQuest.QUEST_MARRY)) {
                            int AddCount = 0;
                            if (item.getItemId() == 40903) { // 結婚戒指(藍寶石)
                                AddCount = 1;
                            } else if (item.getItemId() == 40904) { // 結婚戒指(綠寶石)
                                AddCount = 2;
                            } else if (item.getItemId() == 40905) { // 結婚戒指(紅寶石)
                                AddCount = 3;
                            } else if (item.getItemId() == 40906) { // 結婚戒指(鑽石)
                                AddCount = 5;
                            } else if (item.getItemId() == 40907 || item.getItemId() == 40908) { // 西瑪戒指、歐林戒指
                                AddCount = 20;
                            }
                            if (item.getChargeCount() < AddCount) {
                                item.setChargeCount(AddCount);
                                pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
                                pc.getInventory().consumeItem(L1ItemId.ADENA, 1000);
                            }
                        }
                    }
                    htmlid = "";
                }
            }
            // 紅騎士團
            else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 100657) {
                if (cmd.equalsIgnoreCase("j")) { // 加入
                    if ((ServerWarExecutor.get().isNowWar()) && ((pc.getClan() == null) || (pc.getClan().getCastleId() == 0))) {
                        if (pc.getLevel() < ConfigOtherSet2.Red_Knight_Lv) {
                            pc.sendPackets(new S_ServerMessage("等級小于(" + (ConfigOtherSet2.Red_Knight_Lv) + ")無法加入紅騎士團。"));
                            pc.sendPackets(new S_CloseList(pc.getId())); // 關閉對話檔
                            return;
                        }
                        if (pc.hasSkillEffect(Red_Knight)) {
                            pc.sendPackets(new S_ServerMessage(3765)); // 3765=攻城：加入失敗(已加入狀態)
                            pc.sendPackets(new S_CloseList(pc.getId())); // 關閉對話檔
                            return;
                        }
                        pc.setSkillEffect(Red_Knight, 60 * 60 * 1000); // 1小時
                        pc.sendPackets(new S_WarIcon(pc.getId(), 1));
                        pc.broadcastPacketAll(new S_WarIcon(pc.getId(), 1));
                        pc.sendPackets(new S_ServerMessage(3764)); // 3764=攻城：成功加入紅騎士團
                        pc.sendPackets(new S_CloseList(pc.getId())); // 關閉對話檔
                    } else {
                        //pc.sendPackets(new S_ServerMessage(3763)); // 3763=攻城：加入失敗(強制退出)
                        pc.sendPackets(new S_ServerMessage("擁有城堡的玩家無法加入紅騎士團。"));
                        pc.sendPackets(new S_CloseList(pc.getId())); // 關閉對話檔
                    }
                } else if ((cmd.equalsIgnoreCase("d"))) { // 退出
                    if (!pc.hasSkillEffect(Red_Knight)) {
                        pc.sendPackets(new S_ServerMessage("無加入紅騎士團或已經退出。"));
                        pc.sendPackets(new S_CloseList(pc.getId())); // 關閉對話檔
                        return;
                    }
                    pc.killSkillEffectTimer(Red_Knight);
                    pc.sendPackets(new S_WarIcon(pc.getId()));
                    pc.broadcastPacketAll(new S_WarIcon(pc.getId()));
                    pc.sendPackets(new S_ServerMessage(3762)); // 3762=攻城：強制退出
                    pc.sendPackets(new S_CloseList(pc.getId())); // 關閉對話檔
                }
            }
            // 紅騎士團 END
            // 紅騎士團攻城傳送
            else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 100659) {
                if (cmd.equalsIgnoreCase("A")) {
                    if (ServerWarExecutor.get().isNowWar(1)) { // 肯特
                        L1Teleport.teleport(pc, 33080, 32769, (short) 4, 5, true);
                    } else if (ServerWarExecutor.get().isNowWar(2)) { // 妖魔
                        L1Teleport.teleport(pc, 32781, 32354, (short) 4, 5, true);
                    } else if (ServerWarExecutor.get().isNowWar(4)) { // 奇岩
                        L1Teleport.teleport(pc, 33630, 32760, (short) 4, 5, true);
                    }
                }
            }
            // 紅騎士團攻城傳送 END
            // 紅騎士訓練副本傳送
            if ((((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 100645)) {// 紅騎士 (傳令)
                if (cmd.equalsIgnoreCase("A")) {// 遠征地區
                    if (!pc.getInventory().checkItem(40308, 15000)) {
                        pc.sendPackets(new S_SystemMessage("身上金幣不足"));
                        return;
                    }
                    //if (!pc.isInParty()) {
                    //	pc.sendPackets(new S_SystemMessage("您尚未組隊。"));
                    //	return;
                    // }
                    //if (pc.getParty().getNumOfMembers() < 1) {
                    //   pc.sendPackets(new S_SystemMessage("組隊成員最少需:1人。"));
                    //   return;
                    //}
                    // if (pc.getParty().getNumOfMembers() > 100) {
                    //   pc.sendPackets(new S_SystemMessage("組隊成員數限制:100人。"));
                    //   return;
                    //}
                    // if (!pc.getParty().isLeader(pc)) {
                    //   pc.sendPackets(new S_SystemMessage("你不是隊長。"));
                    //   return;
                    //}
                    pc.getInventory().removeItem(40308, 15000);
                    L1Teleport.teleport(pc, 32767, 32830, (short) 2301, 4, true);
                    RedKnightSystem.getInstance().KnightAttachPc(pc, objid);
                }
            }// 紅騎士訓練副本傳送 END
            // 底比斯大戰的次元之門傳送
            else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 4070087) {
                if (cmd.equalsIgnoreCase("a")) {
                    if (MiniSiege.getInstance().running && MiniSiege.getInstance().getStage() == 0) {
                        if (!pc.isSiege) {
                            pc.isSiege = true;
                        }
                        if (pc.isInParty()) { // 如果組隊中就離開隊伍，防止組隊錯亂
                            pc.getParty().leaveMember(pc);
                        }
                        MiniSiege.getInstance().EnterTeam(pc);
                    } else {
                        pc.sendPackets(new S_ServerMessage("\\fT遊戲報名時間已過，下次請及時。"));
                    }
                }
            }
            // 底比斯大戰的次元之門傳送 END
            // 轉生天賦技能專員
            else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 4070088) {
                if (cmd.equalsIgnoreCase("rei_1")) {
                    ReincarnationSkill.getInstance().addCheck(pc, 0);
                    return;
                }
                if (cmd.equalsIgnoreCase("rei_2")) {
                    ReincarnationSkill.getInstance().addCheck(pc, 1);
                    return;
                }
                if (cmd.equalsIgnoreCase("rei_3")) {
                    ReincarnationSkill.getInstance().addCheck(pc, 2);
                    return;
                }
                if (cmd.equalsIgnoreCase("rei_reset")) {
                    final L1Item reicash = ItemTable.get().getTemplate(ConfigTurn.ReiItemId);
                    if (reicash == null) {
                        return;
                    }
                    if (pc.getInventory().checkItem(ConfigTurn.ReiItemId, ConfigTurn.ReiItemCount)) {
                        pc.sendPackets(new S_Message_YN(2761));
                        //pc.sendPackets(new S_Message_YN(2761, "您確定要將轉生技能點數重置嗎？"));
                        pc.sendPackets(new S_SystemMessage(L1SystemMessage.ShowMessage(857))); // 轉生技能點數重置成功 10秒後將會斷線。
                        pc.setSkillEffect(L1SkillId.ReiSkill_Disconnect, 10 * 1000);
                    } else {
                        pc.sendPackets(new S_SystemMessage(reicash.getName() + "[" + ConfigTurn.ReiItemCount + "]不足，無法重置。"));
                    }
                    //return;
                }
            }
            // 轉生天賦技能專員end
            // 屍魂塔
            else if ((((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 190028)) { // 天堂與地獄的境界->進入噬魂塔封印空間
                if (cmd.equalsIgnoreCase("b")) {
                    if (!pc.getInventory().checkItem(640327, 1)) {
                        if (pc.getInventory().consumeItem(40308, 150000) // 金幣15萬
                                && pc.getInventory().consumeItem(640326, 1)) { // 藍色屍魂水晶
                            CreateNewItem.createNewItem(pc, 640327, 1); // 給予封印的屍魂水晶
                            L1Teleport.teleport(pc, 32808, 32800, (short) 4000, 4, true);
                            htmlid = "";
                        }
                    } else {
                        htmlid = "shhon1";
                    }
                }
            } else if ((((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 190054)) { // 屍魂塔傳送師->奇岩村
                if (cmd.equalsIgnoreCase("a")) {
                    L1Teleport.teleport(pc, 33448, 32793, (short) 4, 4, true);
                }
            } else if ((((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 190042)) { // 噬魂塔入口->進入天堂與地獄的境界
                if (cmd.equalsIgnoreCase("a")) {
                    L1Teleport.teleport(pc, 32757, 32846, (short) 2400, 4, true);
                }
            }
            // 屍魂塔end
            /** [原碼] 對伍對決系統 */
            else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 989958) {
                if (cmd.equalsIgnoreCase("A")) {
                    htmlid = Game_Fight.getInstance().enterA(pc);
                } else if (cmd.equalsIgnoreCase("B")) {
                    htmlid = Game_Fight.getInstance().enterB(pc);
                } else if (cmd.equalsIgnoreCase("LookATeam")) {
                    htmlid = Game_Fight.getInstance().LookATeam(pc);
                } else if (cmd.equalsIgnoreCase("LookBTeam")) {
                    htmlid = Game_Fight.getInstance().LookBTeam(pc);
                }
            }
            /** [原碼] 血盟對決系統 */
            else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 989959) {
                if (cmd.equalsIgnoreCase("clanWar")) {
                    htmlid = Game_ZhuduiPK.getInstance().enterZhuduiPK(pc);
                }
            } else if (npctemp.get_npcId() == 80099) {
                if (cmd.equalsIgnoreCase("A")) {
                    if (pc.getInventory().checkItem(40308, 300L)) {
                        pc.getInventory().consumeItem(40308, 300L);
                        pc.getInventory().storeItem(41315, 1L);
                        pc.getQuest().set_step(41, 1);
                        htmlid = "rarson16";
                    } else if (!pc.getInventory().checkItem(40308, 300L)) {
                        htmlid = "rarson7";
                    }
                } else if (cmd.equalsIgnoreCase("B")) {
                    if (pc.getQuest().get_step(41) == 1) {
                        if (pc.getInventory().checkItem(41325, 1L)) {
                            pc.getInventory().consumeItem(41325, 1L);
                            pc.getInventory().storeItem(40308, 2000L);
                            pc.getInventory().storeItem(41317, 1L);
                            pc.getQuest().set_step(41, 2);
                            htmlid = "rarson9";
                        }
                    }
                    htmlid = "rarson10";
                } else if (cmd.equalsIgnoreCase("C")) {
                    if (pc.getQuest().get_step(41) == 4) {
                        if (pc.getInventory().checkItem(41326, 1L)) {
                            pc.getInventory().storeItem(40308, 30000L);
                            pc.getInventory().consumeItem(41326, 1L);
                            htmlid = "rarson12";
                            pc.getQuest().set_step(41, 5);
                        }
                    }
                    htmlid = "rarson17";
                } else if (cmd.equalsIgnoreCase("D")) {
                    if ((pc.getQuest().get_step(41) <= 1) || (pc.getQuest().get_step(41) == 5)) {
                        if (pc.getInventory().checkItem(40308, 300L)) {
                            pc.getInventory().consumeItem(40308, 300L);
                            pc.getInventory().storeItem(41315, 1L);
                            pc.getQuest().set_step(41, 1);
                            htmlid = "rarson16";
                        } else if (!pc.getInventory().checkItem(40308, 300L)) {
                            htmlid = "rarson7";
                        }
                    } else if (pc.getQuest().get_step(41) >= 2) {
                        if (pc.getQuest().get_step(41) <= 4) {
                            if (pc.getInventory().checkItem(40308, 300L)) {
                                pc.getInventory().consumeItem(40308, 300L);
                                pc.getInventory().storeItem(41315, 1L);
                                htmlid = "rarson16";
                            } else if (!pc.getInventory().checkItem(40308, 300L)) {
                                htmlid = "rarson7";
                            }
                        }
                    }
                }
            } else if (npctemp.get_npcId() == 80101) {
                if (cmd.equalsIgnoreCase("request letter of kuen")) {
                    if (pc.getQuest().get_step(41) == 2) {
                        if (pc.getInventory().checkItem(41317, 1L)) {
                            pc.getInventory().consumeItem(41317, 1L);
                            pc.getInventory().storeItem(41318, 1L);
                            pc.getQuest().set_step(41, 3);
                            htmlid = "";
                        }
                    }
                    htmlid = "";
                } else if (cmd.equalsIgnoreCase("request holy mithril dust")) {
                    if (pc.getQuest().get_step(41) == 3) {
                        if ((pc.getInventory().checkItem(41315, 1L)) && (pc.getInventory().checkItem(40494, 30L)) && (pc.getInventory().checkItem(41318, 1L))) {
                            pc.getInventory().consumeItem(41315, 1L);
                            pc.getInventory().consumeItem(41318, 1L);
                            pc.getInventory().consumeItem(40494, 30L);
                            pc.getInventory().storeItem(41316, 1L);
                            pc.getQuest().set_step(41, 4);
                            htmlid = "";
                        }
                    }
                    htmlid = "";
                }
            } else if (npctemp.get_npcId() == 81255) {
                int level = pc.getLevel();
                char s1 = cmd.charAt(0);
                if (level < 13) {
                    switch (s1) {
                        case 'A':
                        case 'a':
                            if ((level > 1) && (level < 5)) {
                                htmlid = "tutorp1";
                            } else if ((level > 4) && (level < 8)) {
                                htmlid = "tutorp2";
                            } else if ((level > 7) && (level < 10)) {
                                htmlid = "tutorp3";
                            } else if ((level > 9) && (level < 12)) {
                                htmlid = "tutorp4";
                            } else if ((level > 11) && (level < 13)) {
                                htmlid = "tutorp5";
                            } else if (level > 12) {
                                htmlid = "tutorp6";
                            } else {
                                htmlid = "tutorend";
                            }
                            break;
                        case 'B':
                        case 'b':
                            if ((level > 1) && (level < 5)) {
                                htmlid = "tutork1";
                            } else if ((level > 4) && (level < 8)) {
                                htmlid = "tutork2";
                            } else if ((level > 7) && (level < 10)) {
                                htmlid = "tutork3";
                            } else if ((level > 9) && (level < 13)) {
                                htmlid = "tutork4";
                            } else if (level > 12) {
                                htmlid = "tutork5";
                            } else {
                                htmlid = "tutorend";
                            }
                            break;
                        case 'C':
                        case 'c':
                            if ((level > 1) && (level < 5)) {
                                htmlid = "tutore1";
                            } else if ((level > 4) && (level < 8)) {
                                htmlid = "tutore2";
                            } else if ((level > 7) && (level < 10)) {
                                htmlid = "tutore3";
                            } else if ((level > 9) && (level < 12)) {
                                htmlid = "tutore4";
                            } else if ((level > 11) && (level < 13)) {
                                htmlid = "tutore5";
                            } else if (level > 12) {
                                htmlid = "tutore6";
                            } else {
                                htmlid = "tutorend";
                            }
                            break;
                        case 'D':
                        case 'd':
                            if ((level > 1) && (level < 5)) {
                                htmlid = "tutorm1";
                            } else if ((level > 4) && (level < 8)) {
                                htmlid = "tutorm2";
                            } else if ((level > 7) && (level < 10)) {
                                htmlid = "tutorm3";
                            } else if ((level > 9) && (level < 12)) {
                                htmlid = "tutorm4";
                            } else if ((level > 11) && (level < 13)) {
                                htmlid = "tutorm5";
                            } else if (level > 12) {
                                htmlid = "tutorm6";
                            } else {
                                htmlid = "tutorend";
                            }
                            break;
                        case 'E':
                        case 'e':
                            if ((level > 1) && (level < 5)) {
                                htmlid = "tutord1";
                            } else if ((level > 4) && (level < 8)) {
                                htmlid = "tutord2";
                            } else if ((level > 7) && (level < 10)) {
                                htmlid = "tutord3";
                            } else if ((level > 9) && (level < 12)) {
                                htmlid = "tutord4";
                            } else if ((level > 11) && (level < 13)) {
                                htmlid = "tutord5";
                            } else if (level > 12) {
                                htmlid = "tutord6";
                            } else {
                                htmlid = "tutorend";
                            }
                            break;
                        case 'F':
                        case 'f':
                            if ((level > 1) && (level < 5)) {
                                htmlid = "tutordk1";
                            } else if ((level > 4) && (level < 8)) {
                                htmlid = "tutordk2";
                            } else if ((level > 7) && (level < 10)) {
                                htmlid = "tutordk3";
                            } else if ((level > 9) && (level < 13)) {
                                htmlid = "tutordk4";
                            } else if (level > 12) {
                                htmlid = "tutordk5";
                            } else {
                                htmlid = "tutorend";
                            }
                            break;
                        case 'G':
                        case 'g':
                            if ((level > 1) && (level < 5)) {
                                htmlid = "tutori1";
                            } else if ((level > 4) && (level < 8)) {
                                htmlid = "tutori2";
                            } else if ((level > 7) && (level < 10)) {
                                htmlid = "tutori3";
                            } else if ((level > 9) && (level < 13)) {
                                htmlid = "tutori4";
                            } else if (level > 12) {
                                htmlid = "tutori5";
                            } else {
                                htmlid = "tutorend";
                            }
                            break;
                        case 'H':
                        case 'h':
                            L1Teleport.teleport(pc, 32575, 32945, (short) 0, 5, true);
                            htmlid = "";
                            break;
                        case 'I':
                        case 'i':
                            L1Teleport.teleport(pc, 32579, 32923, (short) 0, 5, true);
                            htmlid = "";
                            break;
                        case 'J':
                        case 'j':
                            createitem = new int[]{42099};
                            createcount = new int[]{1};
                            L1Teleport.teleport(pc, 32676, 32813, (short) 2005, 5, true);
                            htmlid = "";
                            break;
                        case 'K':
                        case 'k':
                            L1Teleport.teleport(pc, 32562, 33082, (short) 0, 5, true);
                            htmlid = "";
                            break;
                        case 'L':
                        case 'l':
                            L1Teleport.teleport(pc, 32792, 32820, (short) 75, 5, true);
                            htmlid = "";
                            break;
                        case 'M':
                        case 'm':
                            L1Teleport.teleport(pc, 32877, 32904, (short) 304, 5, true);
                            htmlid = "";
                            break;
                        case 'N':
                        case 'n':
                            L1Teleport.teleport(pc, 32759, 32884, (short) 1000, 5, true);
                            htmlid = "";
                            break;
                        case 'O':
                        case 'o':
                            L1Teleport.teleport(pc, 32605, 32837, (short) 2005, 5, true);
                            htmlid = "";
                            break;
                        case 'P':
                        case 'p':
                            L1Teleport.teleport(pc, 32733, 32902, (short) 2005, 5, true);
                            htmlid = "";
                            break;
                        case 'Q':
                        case 'q':
                            L1Teleport.teleport(pc, 32559, 32843, (short) 2005, 5, true);
                            htmlid = "";
                            break;
                        case 'R':
                        case 'r':
                            L1Teleport.teleport(pc, 32677, 32982, (short) 2005, 5, true);
                            htmlid = "";
                            break;
                        case 'S':
                        case 's':
                            L1Teleport.teleport(pc, 32781, 32854, (short) 2005, 5, true);
                            htmlid = "";
                            break;
                        case 'T':
                        case 't':
                            L1Teleport.teleport(pc, 32674, 32739, (short) 2005, 5, true);
                            htmlid = "";
                            break;
                        case 'U':
                        case 'u':
                            L1Teleport.teleport(pc, 32578, 32737, (short) 2005, 5, true);
                            htmlid = "";
                            break;
                        case 'V':
                        case 'v':
                            L1Teleport.teleport(pc, 32542, 32996, (short) 2005, 5, true);
                            htmlid = "";
                            break;
                        case 'W':
                        case 'w':
                            L1Teleport.teleport(pc, 32794, 32973, (short) 2005, 5, true);
                            htmlid = "";
                            break;
                        case 'X':
                        case 'x':
                            L1Teleport.teleport(pc, 32803, 32789, (short) 2005, 5, true);
                            htmlid = "";
                            break;
                        case 'Y':
                        case 'Z':
                        case '[':
                        case '\\':
                        case ']':
                        case '^':
                        case '_':
                        case '`':
                        default:
                            break;
                    }
                }
            } else if (npctemp.get_npcId() == 81256) {
                int quest_step = pc.getQuest().get_step(304);
                int level = pc.getLevel();
                if ((cmd.equalsIgnoreCase("A")) && (level > 4) && (quest_step == 2)) {
                    createitem = new int[]{20028, 20126, 20173, 20206, 20232, 40029, 40030, 40098, 40099, 42099};
                    createcount = new int[]{1, 1, 1, 1, 1, 50, 5, 20, 30, 5};
                }
                htmlid = "";
            } else if (npctemp.get_npcId() == 81257) {
                int level = pc.getLevel();
                char s1 = cmd.charAt(0);
                if (level < 46) {
                    switch (s1) {
                        case 'A':
                        case 'a':
                            L1Teleport.teleport(pc, 32562, 33082, (short) 0, 5, true);
                            htmlid = "";
                            break;
                        case 'B':
                        case 'b':
                            L1Teleport.teleport(pc, 33119, 32933, (short) 4, 5, true);
                            htmlid = "";
                            break;
                        case 'C':
                        case 'c':
                            L1Teleport.teleport(pc, 32887, 32652, (short) 4, 5, true);
                            htmlid = "";
                            break;
                        case 'D':
                        case 'd':
                            L1Teleport.teleport(pc, 32792, 32820, (short) 75, 5, true);
                            htmlid = "";
                            break;
                        case 'E':
                        case 'e':
                            L1Teleport.teleport(pc, 32789, 32851, (short) 76, 5, true);
                            htmlid = "";
                            break;
                        case 'F':
                        case 'f':
                            L1Teleport.teleport(pc, 32750, 32847, (short) 76, 5, true);
                            htmlid = "";
                            break;
                        case 'G':
                        case 'g':
                            if (pc.isDarkelf()) {
                                L1Teleport.teleport(pc, 32877, 32904, (short) 304, 5, true);
                                htmlid = "";
                            } else {
                                htmlid = "lowlv40";
                            }
                            break;
                        case 'H':
                        case 'h':
                            if (pc.isDragonKnight()) {
                                L1Teleport.teleport(pc, 32811, 32873, (short) 1001, 5, true);
                                htmlid = "";
                            } else {
                                htmlid = "lowlv41";
                            }
                            break;
                        case 'I':
                        case 'i':
                            if (pc.isIllusionist()) {
                                L1Teleport.teleport(pc, 32759, 32884, (short) 1000, 5, true);
                                htmlid = "";
                            } else {
                                htmlid = "lowlv42";
                            }
                            break;
                        case 'J':
                        case 'j':
                            L1Teleport.teleport(pc, 32509, 32867, (short) 0, 5, true);
                            htmlid = "";
                            break;
                        case 'K':
                        case 'k':
                            if (level > 34) {
                                createitem = new int[]{20282, 21139};
                                createcount = new int[2];
                                boolean isOK = false;
                                for (int i = 0; i < createitem.length; i++) {
                                    if (!pc.getInventory().checkItem(createitem[i], 1L)) {
                                        createcount[i] = 1;
                                        isOK = true;
                                    }
                                }
                                if (isOK) {
                                    success_htmlid = "lowlv43";
                                } else {
                                    htmlid = "lowlv45";
                                }
                            } else {
                                htmlid = "lowlv44";
                            }
                            break;
                        case '0':
                            if (level < 13) {
                                htmlid = "lowlvS1";
                            } else if ((level > 12) && (level < 46)) {
                                htmlid = "lowlvS2";
                            } else {
                                htmlid = "lowlvno";
                            }
                            break;
                        case '1':
                            if (level < 13) {
                                htmlid = "lowlv14";
                            } else if ((level > 12) && (level < 46)) {
                                htmlid = "lowlv15";
                            } else {
                                htmlid = "lowlvno";
                            }
                            break;
                        case '2':
                            createitem = new int[]{20028, 20126, 20173, 20206, 20232, 21138, 42007};
                            createcount = new int[7];
                            boolean isOK = false;
                            for (int i = 0; i < createitem.length; i++) {
                                if (createitem[i] == 42007) {
                                    L1ItemInstance item = pc.getInventory().findItemId(createitem[i]);
                                    if (item != null) {
                                        if (item.getCount() < 1000L) {
                                            createcount[i] = ((int) (1000L - item.getCount()));
                                            isOK = true;
                                        }
                                    } else {
                                        createcount[i] = 1000;
                                        isOK = true;
                                    }
                                } else if (!pc.getInventory().checkItem(createitem[i], 1L)) {
                                    createcount[i] = 1;
                                    isOK = true;
                                }
                            }
                            if (isOK) {
                                success_htmlid = "lowlv16";
                            } else {
                                htmlid = "lowlv17";
                            }
                            break;
                        case '5':
                            break;
                        case '6':
                            if ((!pc.getInventory().checkItem(42010, 1L)) && (!pc.getInventory().checkItem(42011, 1L))) {
                                createitem = new int[]{42010};
                                createcount = new int[]{2};
                                materials = new int[]{40308};
                                counts = new int[]{2000};
                                success_htmlid = "lowlv22";
                                failure_htmlid = "lowlv20";
                            } else if ((pc.getInventory().checkItem(42010, 1L)) || (pc.getInventory().checkItem(42011, 1L))) {
                                htmlid = "lowlv23";
                            } else {
                                htmlid = "lowlvno";
                            }
                            break;
                        case '3':
                        case '4':
                        case '7':
                        case '8':
                        case '9':
                        case ':':
                        case ';':
                        case '<':
                        case '=':
                        case '>':
                        case '?':
                        case '@':
                        case 'L':
                        case 'M':
                        case 'N':
                        case 'O':
                        case 'P':
                        case 'Q':
                        case 'R':
                        case 'S':
                        case 'T':
                        case 'U':
                        case 'V':
                        case 'W':
                        case 'X':
                        case 'Y':
                        case 'Z':
                        case '[':
                        case '\\':
                        case ']':
                        case '^':
                        case '_':
                        case '`':
                        default:
                            break;
                    }
                }
            }
            /** [原碼] 大樂透系統 */
            else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == L1Config._2162) {
                if (cmd.equalsIgnoreCase("BuyDo")) {
                    if (!(pc.getInventory().checkItem(L1Config._2167, L1Config._2163))) {
                        pc.sendPackets(new S_SystemMessage("需要幣值不足，無法購買。"));
                        return;
                    }
                    if (!(_BigHot.get_isWaiting())) {
                        pc.sendPackets(new S_SystemMessage("尚未開放購買彩票。"));
                        return;
                    }
                    if (_BigHot.get_isStart()) {
                        pc.sendPackets(new S_SystemMessage("大樂透正在開獎，請等下一場再購買。"));
                        return;
                    }
                    pc.setSkillEffect(BigHot, 30000);
                    pc.set_star(7);
                    pc.sendPackets(new S_SystemMessage("請輸入第一個號碼。"));
                    htmlid = "";
                } else if (cmd.equalsIgnoreCase("BuyAuto")) {
                    if (!(pc.getInventory().checkItem(L1Config._2167, L1Config._2163))) {
                        pc.sendPackets(new S_SystemMessage("需要幣值不足，無法購買。"));
                        return;
                    }
                    if (!(_BigHot.get_isWaiting())) {
                        pc.sendPackets(new S_SystemMessage("尚未開放購買彩票。"));
                        return;
                    }
                    if (_BigHot.get_isStart()) {
                        pc.sendPackets(new S_SystemMessage("大樂透正在開獎，請等下一場再購買。"));
                        return;
                    }
                    pc.setSkillEffect(BigHot, 30000);
                    pc.set_star(5);
                    pc.sendPackets(new S_SystemMessage("請輸入購買數量。"));
                    htmlid = "";
                } else if (cmd.equalsIgnoreCase("LookMoney")) {
                    if (!(_BigHot.get_isWaiting())) {
                        pc.sendPackets(new S_SystemMessage("頭獎彩金正在重新計算中.."));
                        return;
                    }
                    L1BigHotble.getInstance().LookMoney(pc);
                    htmlid = "";
                } else if (cmd.equalsIgnoreCase("LookMoney1")) {
                    if (!(_BigHot.get_isWaiting())) {
                        pc.sendPackets(new S_SystemMessage("壹獎彩金正在重新計算中.."));
                        return;
                    }
                    L1BigHotble.getInstance().LookMoney1(pc);
                    htmlid = "";
                } else if (cmd.equalsIgnoreCase("LookMoney2")) {
                    if (!(_BigHot.get_isWaiting())) {
                        pc.sendPackets(new S_SystemMessage("貳獎彩金正在重新計算中.."));
                        return;
                    }
                    L1BigHotble.getInstance().LookMoney2(pc);
                    htmlid = "";
                } else if (cmd.equalsIgnoreCase("BuyLook")) {
                    pc.set_star(6);
                    pc.sendPackets(new S_SystemMessage("請輸入欲查詢的期數。"));
                    htmlid = "";
                }
            }
            /** [原碼] 怪物對戰系統 */
            else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == L1Config._2154) {
                if (cmd.equalsIgnoreCase("LookMob1")) {
                    L1Mobble.getInstance().Mobstatus((L1NpcInstance) obj, pc);
                } else if (cmd.equalsIgnoreCase("LookMob2")) {
                    L1Mobble.getInstance().watchMobFight(pc);
                }
            } else if (npctemp.get_npcId() == 81278) {
                if (cmd.equalsIgnoreCase("a")) {
                    if ((pc.getInventory().checkItem(40308, 10000000L)) && (!pc.getInventory().checkItem(47010))) {
                        pc.getInventory().consumeItem(40308, 10000000L);
                        pc.getInventory().storeItem(47010, 1L);
                        htmlid = "okveil";
                    } else {
                        htmlid = "noveil";
                    }
                } else if (cmd.equalsIgnoreCase("b")) {
                    if ((pc.getInventory().checkItem(40308, 10000000L)) && (!pc.getInventory().checkItem(47011))) {
                        pc.getInventory().consumeItem(40308, 10000000L);
                        pc.getInventory().storeItem(47011, 1L);
                        htmlid = "okveil";
                    } else {
                        htmlid = "noveil";
                    }
                }
            } else if (npctemp.get_npcId() == 70936) {
                int level = pc.getLevel();
                char s1 = cmd.charAt(0);
                if (cmd.equalsIgnoreCase("0")) {
                    if ((level >= 30) && (level <= 51)) {
                        L1Teleport.teleport(pc, 32820, 32904, (short) 1002, 5, true);
                        htmlid = "";
                    } else {
                        htmlid = "dsecret3";
                    }
                } else if (level >= 52) {
                    switch (s1) {
                        case '1':
                            L1Teleport.teleport(pc, 32904, 32627, (short) 1002, 5, true);
                            break;
                        case '2':
                            L1Teleport.teleport(pc, 32793, 32593, (short) 1002, 5, true);
                            break;
                        case '3':
                            L1Teleport.teleport(pc, 32874, 32785, (short) 1002, 5, true);
                            break;
                        case '4':
                            L1Teleport.teleport(pc, 32993, 32716, (short) 1002, 4, true);
                            break;
                        case '5':
                            L1Teleport.teleport(pc, 32698, 32664, (short) 1002, 6, true);
                            break;
                        case '6':
                            L1Teleport.teleport(pc, 32710, 32759, (short) 1002, 6, true);
                            break;
                        case '7':
                            L1Teleport.teleport(pc, 32986, 32630, (short) 1002, 4, true);
                    }
                    htmlid = "";
                } else {
                    htmlid = "dsecret3";
                }
            } else if ((npctemp.get_npcId() == 70077) || (npctemp.get_npcId() == 81290)) {
                int consumeItem = 0;
                int consumeItemCount = 0;
                int petNpcId = 0;
                int petItemId = 0;
                int upLv = 0;
                long lvExp = 0L;
                String msg = "";
                if (cmd.equalsIgnoreCase("buy 1")) {
                    petNpcId = 45042;
                    consumeItem = 40308;
                    consumeItemCount = 50000;
                    petItemId = 40314;
                    upLv = 5;
                    lvExp = ExpTable.getExpByLevel(upLv);
                    msg = "金幣";
                } else if (cmd.equalsIgnoreCase("buy 2")) {
                    petNpcId = 45034;
                    consumeItem = 40308;
                    consumeItemCount = 50000;
                    petItemId = 40314;
                    upLv = 5;
                    lvExp = ExpTable.getExpByLevel(upLv);
                    msg = "金幣";
                } else if (cmd.equalsIgnoreCase("buy 3")) {
                    petNpcId = 45046;
                    consumeItem = 40308;
                    consumeItemCount = 50000;
                    petItemId = 40314;
                    upLv = 5;
                    lvExp = ExpTable.getExpByLevel(upLv);
                    msg = "金幣";
                } else if (cmd.equalsIgnoreCase("buy 4")) {
                    petNpcId = 45047;
                    consumeItem = 40308;
                    consumeItemCount = 50000;
                    petItemId = 40314;
                    upLv = 5;
                    lvExp = ExpTable.getExpByLevel(upLv);
                    msg = "金幣";
                } else if (cmd.equalsIgnoreCase("buy 7")) {
                    petNpcId = 97023;
                    consumeItem = 42531;
                    consumeItemCount = 1;
                    petItemId = 40314;
                    upLv = 5;
                    lvExp = ExpTable.getExpByLevel(upLv);
                    msg = "淘氣幼龍蛋";
                } else if (cmd.equalsIgnoreCase("buy 8")) {
                    petNpcId = 97022;
                    consumeItem = 42513;
                    consumeItemCount = 1;
                    petItemId = 40314;
                    upLv = 5;
                    lvExp = ExpTable.getExpByLevel(upLv);
                    msg = "頑皮幼龍蛋";
                } else if (cmd.equalsIgnoreCase("gmbuy 1")) {
                    if (pc.isGm()) {
                        petNpcId = 91149;
                        consumeItem = 40308;
                        consumeItemCount = 1;
                        petItemId = 40316;
                        upLv = 48;
                        lvExp = ExpTable.getExpByLevel(upLv);
                        msg = "高等猴子";
                    }
                } else if (cmd.equalsIgnoreCase("gmbuy 2")) {
                    if (pc.isGm()) {
                        petNpcId = 45695;
                        consumeItem = 40308;
                        consumeItemCount = 1;
                        petItemId = 40316;
                        upLv = 48;
                        lvExp = ExpTable.getExpByLevel(upLv);
                        msg = "高等暴走兔";
                    }
                } else if (cmd.equalsIgnoreCase("gmbuy 3")) {
                    if (pc.isGm()) {
                        petNpcId = 46046;
                        consumeItem = 40308;
                        consumeItemCount = 1;
                        petItemId = 40316;
                        upLv = 48;
                        lvExp = ExpTable.getExpByLevel(upLv);
                        msg = "黃金龍";
                    }
                } else if (cmd.equalsIgnoreCase("gmbuy 4")) {
                    if (pc.isGm()) {
                        petNpcId = 97024;
                        consumeItem = 40308;
                        consumeItemCount = 1;
                        petItemId = 40316;
                        upLv = 48;
                        lvExp = ExpTable.getExpByLevel(upLv);
                        msg = "高等頑皮幼龍";
                    }
                } else if (cmd.equalsIgnoreCase("gmbuy 5")) {
                    if (pc.isGm()) {
                        petNpcId = 97025;
                        consumeItem = 40308;
                        consumeItemCount = 1;
                        petItemId = 40316;
                        upLv = 48;
                        lvExp = ExpTable.getExpByLevel(upLv);
                        msg = "高等淘氣幼龍";
                    }
                }
                if (petNpcId > 0) {
                    if (!pc.getInventory().checkItem(consumeItem, consumeItemCount)) {
                        pc.sendPackets(new S_ServerMessage(337, msg));
                    } else if (pc.getInventory().getSize() > 180) {
                        pc.sendPackets(new S_ServerMessage(337, "身上空間"));
                    } else if (pc.getInventory().checkItem(consumeItem, consumeItemCount)) {
                        pc.getInventory().consumeItem(consumeItem, consumeItemCount);
                        L1PcInventory inv = pc.getInventory();
                        L1ItemInstance petamu = inv.storeItem(petItemId, 1L);
                        if (petamu != null) {
                            PetReading.get().buyNewPet(petNpcId, petamu.getId() + 1, petamu.getId(), upLv, lvExp);
                            pc.sendPackets(new S_ItemName(petamu));
                            pc.sendPackets(new S_ServerMessage(403, petamu.getName()));
                        }
                    }
                } else {
                    pc.sendPackets(new S_SystemMessage("對話檔版本不符，請下載更新"));
                    htmlid = "";
                }
            }
            // 投石器
            else if (npctemp.get_npcId() >= 90327 && npctemp.get_npcId() <= 90337) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                int pcX = 0;
                int pcY = 0;
                if (pc.getClanRank() != 10 && pc.getClanRank() != 4) {
                    // 只有血盟君主才可。
                    pc.sendPackets(new S_ServerMessage(2498));
                    return;
                }
                // 奇巖 cgirana
                switch (cmd) {
                    case "0-5":  // 往外城門方向發射!
                        pcX = _random.nextInt(6) + 33629;
                        pcY = _random.nextInt(4) + 32730;
                        ShellDamage(npc, 12205, pcX, pcY, pc);
                        break;
                    case "0-6":  // 往內城門方向發射!
                        pcX = _random.nextInt(8) + 33629;
                        pcY = _random.nextInt(4) + 32698;
                        ShellDamage(npc, 12205, pcX, pcY, pc);
                        break;
                    case "0-7":  // 往守護塔方向發射!
                        pcX = _random.nextInt(6) + 33629;
                        pcY = _random.nextInt(6) + 32675;
                        ShellDamage(npc, 12205, pcX, pcY, pc);
                        break;
                    case "1-16":  // 往外城門方向發射沉默炮彈!
                        pcX = _random.nextInt(6) + 33629;
                        pcY = _random.nextInt(4) + 32730;
                        ShellsSilence(npc, 12205, pcX, pcY, pc);
                        break;
                    case "1-17":  // 往內城門前面發射沉默炮彈!
                        pcX = _random.nextInt(8) + 33629;
                        pcY = _random.nextInt(4) + 32698;
                        ShellsSilence(npc, 12205, pcX, pcY, pc);
                        break;
                    case "1-18":  // 往內城門左側方向發射沉默炮彈!
                        pcX = _random.nextInt(7) + 33626;
                        pcY = _random.nextInt(4) + 32704;
                        ShellsSilence(npc, 12205, pcX, pcY, pc);
                        break;
                    case "1-19":  // 往內城門右側方向發射沉默炮彈!
                        pcX = _random.nextInt(7) + 33632;
                        pcY = _random.nextInt(4) + 32704;
                        ShellsSilence(npc, 12205, pcX, pcY, pc);
                        break;
                    case "1-20":  // 往守護塔方向發射沉默炮彈!
                        pcX = _random.nextInt(6) + 33629;
                        pcY = _random.nextInt(6) + 32675;
                        ShellsSilence(npc, 12205, pcX, pcY, pc);
                        break;
                    // 奇巖 cgirand
                    case "0-10":  // 往外城門方向發射!
                        pcX = _random.nextInt(6) + 33629;
                        pcY = _random.nextInt(4) + 32735;
                        ShellDamage(npc, 12193, pcX, pcY, pc);
                        break;
                    // 肯特 ckenta
                    case "0-1":  // 往外城門方向發射!
                        pcX = _random.nextInt(5) + 33106;
                        pcY = _random.nextInt(5) + 32768;
                        ShellDamage(npc, 12201, pcX, pcY, pc);
                        break;
                    case "0-2":  // 往守護塔方向發射!
                        pcX = _random.nextInt(8) + 33164;
                        pcY = _random.nextInt(9) + 32776;
                        ShellDamage(npc, 12201, pcX, pcY, pc);
                        break;
                    case "1-11":  // 往外城門方向發射沉默炮彈!
                        pcX = _random.nextInt(5) + 33106;
                        pcY = _random.nextInt(5) + 32768;
                        ShellsSilence(npc, 12201, pcX, pcY, pc);
                        break;
                    case "1-12":  // 往外城門後面發射沉默炮彈!
                        pcX = _random.nextInt(5) + 33112;
                        pcY = _random.nextInt(5) + 32768;
                        ShellsSilence(npc, 12201, pcX, pcY, pc);
                        break;
                    case "1-13":  // 往守護塔右側發射沉默炮彈!
                        pcX = _random.nextInt(8) + 33164;
                        pcY = _random.nextInt(9) + 32785;
                        ShellsSilence(npc, 12201, pcX, pcY, pc);
                        break;
                    // 肯特 ckentd
                    case "0-8":  // 往外城門方向發射!
                        pcX = _random.nextInt(5) + 33106;
                        pcY = _random.nextInt(5) + 32768;
                        ShellDamage(npc, 12197, pcX, pcY, pc);
                        break;
                    // 妖堡 corca
                    case "0-3":  // 往外城門方向發射!
                        pcX = _random.nextInt(7) + 32792;
                        pcY = _random.nextInt(4) + 32321;
                        ShellDamage(npc, 12205, pcX, pcY, pc);
                        break;
                    case "0-4":  // 往守護塔方向發射!
                        pcX = _random.nextInt(8) + 32794;
                        pcY = _random.nextInt(8) + 32281;
                        ShellDamage(npc, 12205, pcX, pcY, pc);
                        break;
                    case "1-14":  // 往外城門方向發射沉默炮彈!
                        pcX = _random.nextInt(7) + 32792;
                        pcY = _random.nextInt(4) + 32321;
                        ShellsSilence(npc, 12205, pcX, pcY, pc);
                        break;
                    case "1-15":  // 往守護塔方向發射沉默炮彈!
                        pcX = _random.nextInt(8) + 32794;
                        pcY = _random.nextInt(8) + 32281;
                        ShellsSilence(npc, 12205, pcX, pcY, pc);
                        break;
                    // 妖堡 corcd
                    case "0-9":  // 往外城門方向發射!
                        pcX = _random.nextInt(7) + 32792;
                        pcY = _random.nextInt(4) + 32321;
                        ShellDamage(npc, 12193, pcX, pcY, pc);
                        break;
                }
            }
            // 時空裂痕
            else if (npctemp.get_npcId() == 99000) {
                if (cmd.equalsIgnoreCase("a")) {
                    L1Teleport.teleport(pc, 32931, 32867, (short) 8001, 5, true);
                    htmlid = "";
                } else if (cmd.equalsIgnoreCase("b")) {
                    L1Teleport.teleport(pc, 32931, 32867, (short) 8002, 5, true);
                    htmlid = "";
                } else if (cmd.equalsIgnoreCase("c")) {
                    L1Teleport.teleport(pc, 32931, 32867, (short) 8003, 5, true);
                    htmlid = "";
                }
            }
            //金之吉
            else if (npctemp.get_npcId() == 99020) {
                if (cmd.equalsIgnoreCase("a")) {
                    L1Teleport.teleport(pc, 32814, 32801, (short) 8000, 5, true);
                    htmlid = "";
                }
            }
            //雅博
            else if (npctemp.get_npcId() == 99028) {
                if (cmd.equalsIgnoreCase("a")) {
                    L1Teleport.teleport(pc, 33418, 32815, (short) 4, 5, true);
                    htmlid = "";
                }
            }
            // 武司
            else if (npctemp.get_npcId() == 99021) {
                String npcName = npctemp.get_name();
                // 兌換異界之箱
                if (cmd.equalsIgnoreCase("a")) {
                    // 檢查可換取數量
                    long xcount = CreateNewItem.checkNewItem(pc, 85000, 108);
                    if (xcount == 1L) {
                        pc.getInventory().consumeItem(85000, 108); // 封印著妖怪的魂
                        CreateNewItem.createNewItem_NPC(pc, npcName, 85008, 1);// 異界之箱
                        htmlid = "tw_takesi2";
                    } else if (xcount > 1L) {
                        pc.sendPackets(new S_ItemCount(objid, (int) xcount, "a1"));
                    } else if (xcount < 1L) {
                        htmlid = "tw_takesi3";
                    }
                }
                // 兌換日輪寶箱
                else if (cmd.equalsIgnoreCase("e")) {
                    if (pc.getInventory().consumeItem(85000, 108)) {// 封印著妖怪的魂
                        CreateNewItem.createNewItem_NPC(pc, npcName, 85011, 1);// 日輪冠冕寶盒
                        htmlid = "tw_takesi2";
                    } else {
                        htmlid = "tw_takesi3";
                    }
                }
            }
            // 剛之助
            else if (npctemp.get_npcId() == 99023) {
                String npcName = npctemp.get_name();
                // 兌換神聖的丸子(5分)
                if (cmd.equalsIgnoreCase("a")) {
                    if (pc.getInventory().consumeItem(85001, 108)) {// 有惡靈寄宿的丸子
                        CreateNewItem.createNewItem_NPC(pc, npcName, 85002, 1);// 神聖的丸子(5分)
                        htmlid = "tw_sanojos1";
                    } else {
                        htmlid = "tw_sanojof1";
                    }
                }
                // 直接購買神聖的丸子(30分)
                else if (cmd.equalsIgnoreCase("b")) {
                    if (pc.getInventory().consumeItem(40308, 500000)) {
                        CreateNewItem.createNewItem_NPC(pc, npcName, 85003, 1);// 神聖的丸子(30分)
                        htmlid = "tw_sanojos2";
                    } else {
                        htmlid = "tw_sanojof2";
                    }
                }
                // 想回到城墎地區
                else if (cmd.equalsIgnoreCase("c")) {
                    L1Teleport.teleport(pc, 32935, 32867, (short) 8000, 5, true);
                    htmlid = "";
                }
                // 想擴張輔助欄位
                /*
                 * else if (cmd.equalsIgnoreCase("D")) { if
                 * (pc.getInventory().consumeItem(40308, 20000000)) { htmlid =
                 * "tw_sanojo4"; } else { htmlid = "tw_sanojo3"; } }
                 */
            }
            // 無名之魂
            else if (npctemp.get_npcId() == 99022) {
                // 交付巨型骷髏的妖魂
                if (cmd.equalsIgnoreCase("a")) {
                    if (pc.getInventory().consumeItem(85007, 1)) {// 巨型骷髏的妖魂
                        double addExp = 10;// ExpTable.calcPercentageExp(49, 5);
                        // // 49換算5%
                        double penaltyrate = 10;// ExpTable.getPenaltyRate(pc.getLevel(),
                        // pc.getMeteLevel());
                        // System.out.println("penaltyrate == " + penaltyrate);
                        pc.addExp((long) (addExp * penaltyrate));
                        htmlid = "jp_noname1a";
                    } else {
                        htmlid = "jp_noname1b";
                    }
                }
                // 知道四魂是什麼嗎
                else if (cmd.equalsIgnoreCase("b")) {
                    htmlid = "jp_noname1c";
                }
            }
            // 彌九郎
            else if (npctemp.get_npcId() == 99026) {
                String npcName = npctemp.get_name();
                // 身上的沙金全部交給他
                if (cmd.equalsIgnoreCase("a")) {
                    if (pc.getInventory().checkItem(85004)) {// 沙金
                        L1ItemInstance item = pc.getInventory().findItemId(85004);// 沙金
                        if (item != null) {
                            int adena = (int) (item.getCount() * 1000);
                            pc.getInventory().removeItem(item);
                            CreateNewItem.createNewItem_NPC(pc, npcName, 40308, adena);
                            htmlid = "jp_yakuro6";
                        }
                    } else {
                        htmlid = "jp_yakuro5";
                    }
                }
                // 身上的金條全部交給他
                else if (cmd.equalsIgnoreCase("b")) {
                    if (pc.getInventory().checkItem(85005)) {// 金條
                        L1ItemInstance item = pc.getInventory().findItemId(85005);// 金條
                        if (item != null) {
                            int adena = (int) (item.getCount() * 10000);
                            pc.getInventory().removeItem(item);
                            CreateNewItem.createNewItem_NPC(pc, npcName, 40308, adena);
                            htmlid = "jp_yakuro6";
                        }
                    } else {
                        htmlid = "jp_yakuro5";
                    }
                }
                // 身上的老舊茶杯全部交給他
                else if (cmd.equalsIgnoreCase("c")) {
                    if (pc.getInventory().checkItem(85006)) {// 老舊的茶杯
                        L1ItemInstance item = pc.getInventory().findItemId(85006);// 老舊的茶杯
                        if (item != null) {
                            int adena = (int) (item.getCount() * 150000);
                            pc.getInventory().removeItem(item);
                            pc.getInventory().storeItem(40308, adena);
                            CreateNewItem.createNewItem_NPC(pc, npcName, 40308, adena);
                            htmlid = "jp_yakuro6";
                        }
                    } else {
                        htmlid = "jp_yakuro5";
                    }
                }
                // -----------------冰之女王---------------------
            } else if (npctemp.get_npcId() == 80503) {// 冰之女王-象牙塔的秘密間諜
                if (cmd.equalsIgnoreCase("a")) {// 給火焰魔杖
                    if (pc.getInventory().checkItem(41781, 1)) {
                        htmlid = "icqwand2";
                    } else {
                        L1ItemInstance item = ItemTable.get().createItem(41781);
                        if (item != null) {
                            item.setIdentified(true);
                            item.setChargeCount(100);
                            pc.getInventory().insertItem(item);
                            pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                            htmlid = "icqwand5";
                        }
                    }
                }
                if (cmd.equalsIgnoreCase("b")) {// 給象牙塔恢復藥水
                    if (pc.getInventory().checkItem(41782, 1)) {
                        htmlid = "icqwand4";
                    } else {
                        L1ItemInstance item = ItemTable.get().createItem(41782);
                        if (item != null) {
                            item.setCount(80);
                            item.setIdentified(true);
                            pc.getInventory().insertItem(item);
                            pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
                            htmlid = "icqwand3";
                        }
                    }
                }
            } else if (npctemp.get_npcId() == 70751) {// 冰之女王-布朗德
                if (cmd.equalsIgnoreCase("a")) {// 傳送到冰之女王的宮殿
                    L1Teleport.teleport(pc, 32743, 32833, (short) 278, 5, true);
                } else if (cmd.equalsIgnoreCase("b")) {// 傳送到冰之女王的城堡
                    if (pc.getInventory().consumeItem(41783, 1)) {
                        L1Teleport.teleport(pc, 32797, 32799, (short) 2100, 5, true);
                    } else {
                        htmlid = "newbrad3";
                    }
                }
            }
            //
            // --------------冰之女王副本結束------------
            // -------------噬魂塔-------------
			/*if ((((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 82511)) {// 噬魂塔-天使^艾澤奇爾
				if (cmd.equalsIgnoreCase("r")) {// 噬魂塔排名
					SoulTowerTable.get().getSoulTowerRanked(pc);
				}
			}
			if ((((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 82517)) {// 天堂與地獄的境界-進入噬魂塔
				// if (pc.getMapTime(4100) > 0) {
				// pc.sendPackets(new S_SystemMessage("你今天已經闖過噬魂塔，請明天再試"));
				// return;
				// }
				L1ItemInstance item = pc.getInventory().findItemId(42946);
				if (item != null) {
					pc.sendPackets(new S_SystemMessage("屍魂水晶還在封印中"));
					return;
				}
				item = pc.getInventory().findItemId(42944);
				if (item == null) {
					pc.sendPackets(new S_SystemMessage("請先找天使領取屍魂水晶"));
					return;
				}
				if (!pc.getInventory().checkItem(40308, 150000)) {
					pc.sendPackets(new S_SystemMessage("身上金幣不足"));
					return;
				}
				pc.getInventory().removeItem(40308, 150000);
				pc.getInventory().removeItem(item);
				item = ItemTable.get().createItem(42946);
				// Timestamp time=new Timestamp(System.currentTimeMillis()+12*60*60*1000);
				// item.set_time(time);
				pc.getInventory().storeItem(item);
				if (cmd.equalsIgnoreCase("a")) {// 下層簡單難度
					pc.setSoulTower(1);
					// pc.set_tmp(1);//設置噬魂塔層
					L1Teleport.teleport(pc, 32800, 32797, (short) 4000, 3, true);
				}
				if (cmd.equalsIgnoreCase("b")) {// 下層普通難度
					pc.setSoulTower(1);
					// pc.set_tmp(1);//設置噬魂塔層
					L1Teleport.teleport(pc, 32800, 32797, (short) 4000, 3, true);
				}
				if (cmd.equalsIgnoreCase("c")) {// 下層困難難度
					pc.setSoulTower(1);
					// pc.set_tmp(1);//設置噬魂塔層
					L1Teleport.teleport(pc, 32800, 32797, (short) 4000, 3, true);
				}
				if (cmd.equalsIgnoreCase("d")) {// 中層簡單難度
				}
				if (cmd.equalsIgnoreCase("e")) {// 中層普通難度
				}
				if (cmd.equalsIgnoreCase("f")) {// 中層困難難度
				}
			}
			if ((((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 82518)) {// 封印空間-進入噬魂塔
				if (s.equalsIgnoreCase("enter")) {// 進入噬魂塔
					// CharMapTimeReading.get().load(pc);
					ConcurrentHashMap<Integer, Integer> map = pc.getMapTime();
					if (map.get(4001) != null) {
						map.put(4001, 0);
						pc.setMapTime(map);
					}
					L1SoulTower.get().soulTowerStart(pc);
				}
			}*/
            // if ((((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 190045)) {// 噬魂塔-詭異商人
            // if (s.equalsIgnoreCase("buy")) {//進入噬魂塔
            // L1SoulTower.get().soulTowerStart(pc);
            // }
            // }
            // ------------噬魂塔結束-----------
            if ((htmlid != null) && (htmlid.equalsIgnoreCase("colos2"))) {
                htmldata = makeUbInfoStrings(npctemp.get_npcId());
            }
            if ((htmlid != null) && (htmlid.equalsIgnoreCase("colos2"))) {
                htmldata = makeUbInfoStrings(npctemp.get_npcId());
            }
            // 製造道具
            if (createitem != null) {
                boolean isCreate = true;
                for (int j = 0; j < materials.length; j++) {
                    if (!pc.getInventory().checkItemNotEquipped(materials[j], counts[j])) {
                        L1Item temp = ItemTable.get().getTemplate(materials[j]);
                        pc.sendPackets(new S_ServerMessage(337, temp.getNameId()));
                        isCreate = false;
                    }
                }
                if (isCreate) {
                    int create_count = 0;
                    int create_weight = 0;
                    for (int k = 0; k < createitem.length; k++) {
                        L1Item temp = ItemTable.get().getTemplate(createitem[k]);
                        if (temp.isStackable()) {
                            if (!pc.getInventory().checkItem(createitem[k])) {
                                create_count++;
                            }
                        } else {
                            create_count += createcount[k];
                        }
                        create_weight += temp.getWeight() * createcount[k] / 1000;
                    }
                    if (pc.getInventory().getSize() + create_count > 180) {
                        pc.sendPackets(new S_ServerMessage(263));
                        return;
                    }
                    if (pc.getMaxWeight() < pc.getInventory().getWeight() + create_weight) {
                        pc.sendPackets(new S_ServerMessage(82));
                        return;
                    }
                    for (int j = 0; j < materials.length; j++) {
                        pc.getInventory().consumeItem(materials[j], counts[j]);
                    }
                    for (int k = 0; k < createitem.length; k++) {
                        L1ItemInstance item = pc.getInventory().storeItem(createitem[k], createcount[k]);
                        if (item != null) {
                            String itemName = ItemTable.get().getTemplate(createitem[k]).getNameId();
                            String createrName = "";
                            if ((obj instanceof L1NpcInstance)) {
                                createrName = ((L1NpcInstance) obj).getNpcTemplate().get_name();
                            }
                            if (createcount[k] > 1) {
                                pc.sendPackets(new S_ServerMessage(143, createrName, itemName + " (" + createcount[k] + ")"));
                            } else {
                                pc.sendPackets(new S_ServerMessage(143, createrName, itemName));
                            }
                        }
                    }
                    if (success_htmlid != null) {
                        pc.sendPackets(new S_NPCTalkReturn(objid, success_htmlid, htmldata));
                    }
                } else if (failure_htmlid != null) {
                    pc.sendPackets(new S_NPCTalkReturn(objid, failure_htmlid, htmldata));
                }
            }
            if (htmlid != null) {
                pc.sendPackets(new S_NPCTalkReturn(objid, htmlid, htmldata));
            }
        } catch (Exception localException2) {
        } finally {
            over();
        }
    }

    /**
     * 顯示道具製造清單
     *
     * @param pc
     * @param npc
     */
    private void ShowCraftList(L1PcInstance pc, L1NpcInstance npc) {
        String msg0 = "";
        String msg1 = "";
        String msg2 = "";
        String msg3 = "";
        String msg4 = "";
        String msg5 = "";
        String msg6 = "";
        String msg7 = "";
        String msg8 = "";
        String msg9 = "";
        String msg10 = "";
        String msg11 = "";
        String msg12 = "";
        String msg13 = "";
        String msg14 = "";
        String msg15 = "";
        String msg16 = "";
        String msg17 = "";
        String msg18 = "";
        String msg19 = "";
        String msg20 = "";
        String msg21 = "";
        String msg22 = "";
        String msg23 = "";
        String msg24 = "";
        String msg25 = "";
        String msg26 = "";
        String msg27 = "";
        String msg28 = "";
        String msg29 = "";
        String msg30 = "";
        String msg31 = "";
        String msg32 = "";
        String msg33 = "";
        String msg34 = "";
        String msg35 = "";
        String msg36 = "";
        String msg37 = "";
        String msg38 = "";
        String msg39 = "";
        String msg40 = "";
        int npcid = npc.getNpcId();
        Map<String, String> craftlist = L1BlendTable.getInstance().get_craftlist();
        if (!craftlist.isEmpty()) {
            msg0 = craftlist.get(npcid + "A");
            msg1 = craftlist.get(npcid + "B");
            msg2 = craftlist.get(npcid + "C");
            msg3 = craftlist.get(npcid + "D");
            msg4 = craftlist.get(npcid + "E");
            msg5 = craftlist.get(npcid + "F");
            msg6 = craftlist.get(npcid + "G");
            msg7 = craftlist.get(npcid + "H");
            msg8 = craftlist.get(npcid + "I");
            msg9 = craftlist.get(npcid + "J");
            msg10 = craftlist.get(npcid + "K");
            msg11 = craftlist.get(npcid + "L");
            msg12 = craftlist.get(npcid + "M");
            msg13 = craftlist.get(npcid + "N");
            msg14 = craftlist.get(npcid + "O");
            msg15 = craftlist.get(npcid + "P");
            msg16 = craftlist.get(npcid + "Q");
            msg17 = craftlist.get(npcid + "R");
            msg18 = craftlist.get(npcid + "S");
            msg19 = craftlist.get(npcid + "T");
            msg20 = craftlist.get(npcid + "U");
            msg21 = craftlist.get(npcid + "V");
            msg22 = craftlist.get(npcid + "W");
            msg23 = craftlist.get(npcid + "X");
            msg24 = craftlist.get(npcid + "Y");
            msg25 = craftlist.get(npcid + "Z");
            msg26 = craftlist.get(npcid + "a1");
            msg27 = craftlist.get(npcid + "a2");
            msg28 = craftlist.get(npcid + "a3");
            msg29 = craftlist.get(npcid + "a4");
            msg30 = craftlist.get(npcid + "a5");
            msg31 = craftlist.get(npcid + "a6");
            msg32 = craftlist.get(npcid + "a7");
            msg33 = craftlist.get(npcid + "a8");
            msg34 = craftlist.get(npcid + "a9");
            msg35 = craftlist.get(npcid + "a10");
            msg36 = craftlist.get(npcid + "a11");
            msg37 = craftlist.get(npcid + "a12");
            msg38 = craftlist.get(npcid + "a13");
            msg39 = craftlist.get(npcid + "a14");
            msg40 = craftlist.get(npcid + "a15");
        }
        String msgs[] = {msg0, msg1, msg2, msg3, msg4, msg5, msg6, msg7, msg8, msg9, msg10, msg11, msg12, msg13, msg14, msg15, msg16, msg17, msg18, msg19, msg20, msg21, msg22, msg23, msg24, msg25, msg26, msg27, msg28, msg29, msg30, msg31, msg32, msg33, msg34, msg35, msg36, msg37, msg38, msg39, msg40};
        if (msg0 != null) {// 至少有設定一項道具製造資料
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "smithitem1", msgs));
        } else {
            pc.sendPackets(new S_SystemMessage("沒有可以製作的道具。"));
            return;
        }
    }

    private String karmaLevelToHtmlId(int level) {
        if ((level == 0) || (level < -7) || (7 < level)) {
            return "";
        }
        String htmlid = "";
        if (level > 0) {
            htmlid = "vbk" + level;
        } else if (level < 0) {
            htmlid = "vyk" + Math.abs(level);
        }
        return htmlid;
    }

    private String watchUb(L1PcInstance pc, int npcId) {
        L1UltimateBattle ub = UBTable.getInstance().getUbForNpcId(npcId);
        L1Location loc = ub.getLocation();
        if (pc.getInventory().consumeItem(40308, 100L)) {
            try {
                pc.save();
                pc.beginGhost(loc.getX(), loc.getY(), (short) loc.getMapId(), true, 600);
            } catch (Exception e) {
                _log.error(e.getLocalizedMessage(), e);
            }
        } else {
            pc.sendPackets(new S_ServerMessage(189));
        }
        return "";
    }

    private boolean isNpcSellOnly(L1NpcInstance npc) {
        int npcId = npc.getNpcTemplate().get_npcId();
        if (npcId == 70027 || npcId == 70023) {
            return true;
        }
        return false;
    }

    private String enterUb(L1PcInstance pc, int npcId) {
        L1UltimateBattle ub = UBTable.getInstance().getUbForNpcId(npcId);
        if ((!ub.isActive()) || (!ub.canPcEnter(pc))) {
            return "colos2";
        }
        if (ub.isNowUb()) {
            return "colos1";
        }
        if (ub.getMembersCount() >= ub.getMaxPlayer()) {
            return "colos4";
        }
        ub.addMember(pc);
        L1Location loc = ub.getLocation().randomLocation(10, false);
        L1Teleport.teleport(pc, loc.getX(), loc.getY(), ub.getMapId(), 5, true);
        return "";
    }

    private String enterHauntedHouse(L1PcInstance pc) {
        if (L1HauntedHouse.getInstance().getHauntedHouseStatus() == 2) {
            pc.sendPackets(new S_ServerMessage(1182));
            return "";
        }
        if (L1HauntedHouse.getInstance().getMembersCount() >= 10) {
            pc.sendPackets(new S_ServerMessage(1184));
            return "";
        }
        L1HauntedHouse.getInstance().addMember(pc);
        L1Teleport.teleport(pc, 32722, 32830, (short) 5140, 2, true);
        return "";
    }

    private String enterPetMatch(L1PcInstance pc, int objid2) {
        if (pc.getPetList().values().size() > 0) {
            pc.sendPackets(new S_ServerMessage(1187));
            return "";
        }
        if (!L1PetMatch.getInstance().enterPetMatch(pc, objid2)) {
            pc.sendPackets(new S_ServerMessage(1182));
        }
        return "";
    }

    private void poly(ClientExecutor clientthread, int polyId) {
        L1PcInstance pc = clientthread.getActiveChar();
        int awakeSkillId = pc.getAwakeSkillId();
        if ((awakeSkillId == 185) || (awakeSkillId == 190) || (awakeSkillId == 195)) {
            pc.sendPackets(new S_ServerMessage(1384));
            return;
        }
        if (pc.getInventory().checkItem(40308, 100L)) {
            pc.getInventory().consumeItem(40308, 100L);
            L1PolyMorph.doPoly(pc, polyId, 1800, 4);
        } else {
            pc.sendPackets(new S_ServerMessage(337, "$4"));
        }
    }

    private void polyByKeplisha(ClientExecutor clientthread, int polyId) {
        L1PcInstance pc = clientthread.getActiveChar();
        int awakeSkillId = pc.getAwakeSkillId();
        if ((awakeSkillId == 185) || (awakeSkillId == 190) || (awakeSkillId == 195)) {
            pc.sendPackets(new S_ServerMessage(1384));
            return;
        }
        if (pc.getInventory().checkItem(40308, 100L)) {
            pc.getInventory().consumeItem(40308, 100L);
            L1PolyMorph.doPoly(pc, polyId, 1800, 8);
        } else {
            pc.sendPackets(new S_ServerMessage(337, "$4"));
        }
    }

    private String sellHouse(L1PcInstance pc, int objectId, int npcId) {
        L1Clan clan = WorldClan.get().getClan(pc.getClanname());
        if (clan == null) {
            return "";
        }
        int houseId = clan.getHouseId();
        if (houseId == 0) {
            return "";
        }
        L1House house = HouseReading.get().getHouseTable(houseId);
        int keeperId = house.getKeeperId();
        if (npcId != keeperId) {
            return "";
        }
        if (!pc.isCrown()) {
            pc.sendPackets(new S_ServerMessage(518));
            return "";
        }
        if (pc.getId() != clan.getLeaderId()) {
            pc.sendPackets(new S_ServerMessage(518));
            return "";
        }
        if (house.isOnSale()) {
            return "agonsale";
        }
        pc.sendPackets(new S_SellHouse(objectId, String.valueOf(houseId)));
        return null;
    }

    private void openCloseDoor(L1PcInstance pc, L1NpcInstance npc, String cmd) {
        L1Clan clan = WorldClan.get().getClan(pc.getClanname());
        if (clan != null) {
            int houseId = clan.getHouseId();
            if (houseId != 0) {
                L1House house = HouseReading.get().getHouseTable(houseId);
                int keeperId = house.getKeeperId();
                if (npc.getNpcTemplate().get_npcId() == keeperId) {
                    L1DoorInstance door1 = null;
                    L1DoorInstance door2 = null;
                    L1DoorInstance door3 = null;
                    L1DoorInstance door4 = null;
                    for (L1DoorInstance door : DoorSpawnTable.get().getDoorList()) {
                        if (door.getKeeperId() == keeperId) {
                            if (door1 == null) {
                                door1 = door;
                            } else if (door2 == null) {
                                door2 = door;
                            } else if (door3 == null) {
                                door3 = door;
                            } else if (door4 == null) {
                                door4 = door;
                                break;
                            }
                        }
                    }
                    if (door1 != null) {
                        if (cmd.equalsIgnoreCase("open")) {
                            door1.open();
                        } else if (cmd.equalsIgnoreCase("close")) {
                            door1.close();
                        }
                    }
                    if (door2 != null) {
                        if (cmd.equalsIgnoreCase("open")) {
                            door2.open();
                        } else if (cmd.equalsIgnoreCase("close")) {
                            door2.close();
                        }
                    }
                    if (door3 != null) {
                        if (cmd.equalsIgnoreCase("open")) {
                            door3.open();
                        } else if (cmd.equalsIgnoreCase("close")) {
                            door3.close();
                        }
                    }
                    if (door4 != null) {
                        if (cmd.equalsIgnoreCase("open")) {
                            door4.open();
                        } else if (cmd.equalsIgnoreCase("close")) {
                            door4.close();
                        }
                    }
                }
            }
        }
    }

    private void openCloseGate(L1PcInstance pc, int keeperId, boolean isOpen) {
        boolean isNowWar = false;
        int pcCastleId = 0;
        if (pc.getClanid() != 0) {
            L1Clan clan = WorldClan.get().getClan(pc.getClanname());
            if (clan != null) {
                pcCastleId = clan.getCastleId();
            }
        }
        if ((keeperId == 70656) || (keeperId == 70549) || (keeperId == 70985)) {
            if ((isExistDefenseClan(1)) && (pcCastleId != 1)) {
                return;
            }
            isNowWar = ServerWarExecutor.get().isNowWar(1);
        } else if (keeperId == 70600) {
            if ((isExistDefenseClan(2)) && (pcCastleId != 2)) {
                return;
            }
            isNowWar = ServerWarExecutor.get().isNowWar(2);
        } else if ((keeperId == 70778) || (keeperId == 70987) || (keeperId == 70687)) {
            if ((isExistDefenseClan(3)) && (pcCastleId != 3)) {
                return;
            }
            isNowWar = ServerWarExecutor.get().isNowWar(3);
        } else if ((keeperId == 70817) || (keeperId == 70800) || (keeperId == 70988) || (keeperId == 70990) || (keeperId == 70989) || (keeperId == 70991)) {
            if ((isExistDefenseClan(4)) && (pcCastleId != 4)) {
                return;
            }
            isNowWar = ServerWarExecutor.get().isNowWar(4);
        } else if ((keeperId == 70863) || (keeperId == 70992) || (keeperId == 70862)) {
            if ((isExistDefenseClan(5)) && (pcCastleId != 5)) {
                return;
            }
            isNowWar = ServerWarExecutor.get().isNowWar(5);
        } else if ((keeperId == 70995) || (keeperId == 70994) || (keeperId == 70993)) {
            if ((isExistDefenseClan(6)) && (pcCastleId != 6)) {
                return;
            }
            isNowWar = ServerWarExecutor.get().isNowWar(6);
        } else if (keeperId == 70996) {
            if ((isExistDefenseClan(7)) && (pcCastleId != 7)) {
                return;
            }
            isNowWar = ServerWarExecutor.get().isNowWar(7);
        }
        for (L1DoorInstance door : DoorSpawnTable.get().getDoorList()) {
            if ((door.getKeeperId() == keeperId) && ((!isNowWar) || (door.getMaxHp() <= 1))) {
                if (isOpen) {
                    door.open();
                } else {
                    door.close();
                }
            }
        }
    }

    private boolean isExistDefenseClan(int castleId) {
        boolean isExistDefenseClan = false;
        Collection<?> allClans = WorldClan.get().getAllClans();
        for (Iterator<?> iter = allClans.iterator(); iter.hasNext(); ) {
            L1Clan clan = (L1Clan) iter.next();
            if (castleId == clan.getCastleId()) {
                isExistDefenseClan = true;
                break;
            }
        }
        return isExistDefenseClan;
    }

    private void expelOtherClan(L1PcInstance clanPc, int keeperId) {
        int houseId = 0;
        Collection<L1House> houseList = HouseReading.get().getHouseTableList().values();
        for (L1House house : houseList) {
            if (house.getKeeperId() == keeperId) {
                houseId = house.getHouseId();
            }
        }
        if (houseId == 0) {
            return;
        }
        int[] loc = new int[3];
        for (L1PcInstance pc : World.get().getAllPlayers()) {
            if ((L1HouseLocation.isInHouseLoc(houseId, pc.getX(), pc.getY(), pc.getMapId())) && (clanPc.getClanid() != pc.getClanid())) {
                loc = L1HouseLocation.getHouseTeleportLoc(houseId, 0);
                if (pc != null) {
                    L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);
                }
            }
        }
    }

    private void repairGate(L1PcInstance pc) {
        L1Clan clan = WorldClan.get().getClan(pc.getClanname());
        if (clan != null) {
            int castleId = clan.getCastleId();
            if (castleId != 0) {
                if (!ServerWarExecutor.get().isNowWar(castleId)) {
                    L1DoorInstance[] arrayOfL1DoorInstance;
                    int j = (arrayOfL1DoorInstance = DoorSpawnTable.get().getDoorList()).length;
                    for (int i = 0; i < j; i++) {
                        L1DoorInstance door = arrayOfL1DoorInstance[i];
                        if (L1CastleLocation.checkInWarArea(castleId, door)) {
                            door.repairGate();
                        }
                    }
                    pc.sendPackets(new S_ServerMessage(990));
                } else {
                    pc.sendPackets(new S_ServerMessage(991));
                }
            }
        }
    }

    private void payFee(L1PcInstance pc, L1NpcInstance npc) {
        L1Clan clan = WorldClan.get().getClan(pc.getClanname());
        if (clan != null) {
            int houseId = clan.getHouseId();
            if (houseId != 0) {
                L1House house = HouseReading.get().getHouseTable(houseId);
                int keeperId = house.getKeeperId();
                if (npc.getNpcTemplate().get_npcId() == keeperId) {
                    if (pc.getInventory().checkItem(40308, ConfigAlt.HOUSE_TAX_ADENA)) {
                        pc.getInventory().consumeItem(40308, ConfigAlt.HOUSE_TAX_ADENA);
                        TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
                        Calendar cal = Calendar.getInstance(tz);
                        cal.add(5, ConfigAlt.HOUSE_TAX_INTERVAL);
                        cal.set(12, 0);
                        cal.set(13, 0);
                        house.setTaxDeadline(cal);
                        HouseReading.get().updateHouse(house);
                    } else {
                        pc.sendPackets(new S_ServerMessage(189));
                    }
                }
            }
        }
    }

    private String[] makeHouseTaxStrings(L1PcInstance pc, L1NpcInstance npc) {
        String name = npc.getNpcTemplate().get_name();
        String[] result = {name, "2000", "1", "1", "00"};
        L1Clan clan = WorldClan.get().getClan(pc.getClanname());
        if (clan != null) {
            int houseId = clan.getHouseId();
            if (houseId != 0) {
                L1House house = HouseReading.get().getHouseTable(houseId);
                int keeperId = house.getKeeperId();
                if (npc.getNpcTemplate().get_npcId() == keeperId) {
                    Calendar cal = house.getTaxDeadline();
                    int month = cal.get(2) + 1;
                    int day = cal.get(5);
                    int hour = cal.get(11);
                    result = new String[]{name, "2000", String.valueOf(month), String.valueOf(day), String.valueOf(hour)};
                }
            }
        }
        return result;
    }

    private String[] makeWarTimeStrings(int castleId) {
        L1Castle castle = CastleReading.get().getCastleTable(castleId);
        if (castle == null) {
            return null;
        }
        Calendar warTime = castle.getWarTime();
        int year = warTime.get(1);
        int month = warTime.get(2) + 1;
        int day = warTime.get(5);
        int hour = warTime.get(11);
        int minute = warTime.get(12);
        String[] result;
        if (castleId == 2) {
            result = new String[]{String.valueOf(year), String.valueOf(month), String.valueOf(day), String.valueOf(hour), String.valueOf(minute)};
        } else {
            result = new String[]{"", String.valueOf(year), String.valueOf(month), String.valueOf(day), String.valueOf(hour), String.valueOf(minute)};
        }
        return result;
    }

    private String getYaheeAmulet(L1PcInstance pc, L1NpcInstance npc, String cmd) {
        int[] amuletIdList = {20358, 20359, 20360, 20361, 20362, 20363, 20364, 20365};
        int amuletId = 0;
        L1ItemInstance item = null;
        String htmlid = null;
        if (cmd.equalsIgnoreCase("1")) {
            amuletId = amuletIdList[0];
        } else if (cmd.equalsIgnoreCase("2")) {
            amuletId = amuletIdList[1];
        } else if (cmd.equalsIgnoreCase("3")) {
            amuletId = amuletIdList[2];
        } else if (cmd.equalsIgnoreCase("4")) {
            amuletId = amuletIdList[3];
        } else if (cmd.equalsIgnoreCase("5")) {
            amuletId = amuletIdList[4];
        } else if (cmd.equalsIgnoreCase("6")) {
            amuletId = amuletIdList[5];
        } else if (cmd.equalsIgnoreCase("7")) {
            amuletId = amuletIdList[6];
        } else if (cmd.equalsIgnoreCase("8")) {
            amuletId = amuletIdList[7];
        }
        if (amuletId != 0) {
            item = pc.getInventory().storeItem(amuletId, 1L);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
            }
            for (int id : amuletIdList) {
                if (id == amuletId) {
                    break;
                }
                if (pc.getInventory().checkItem(id)) {
                    pc.getInventory().consumeItem(id, 1L);
                }
            }
            htmlid = "";
        }
        return htmlid;
    }

    private String getBarlogEarring(L1PcInstance pc, L1NpcInstance npc, String cmd) {
        int[] earringIdList = {21020, 21021, 21022, 21023, 21024, 21025, 21026, 21027};
        int earringId = 0;
        L1ItemInstance item = null;
        String htmlid = null;
        if (cmd.equalsIgnoreCase("1")) {
            earringId = earringIdList[0];
        } else if (cmd.equalsIgnoreCase("2")) {
            earringId = earringIdList[1];
        } else if (cmd.equalsIgnoreCase("3")) {
            earringId = earringIdList[2];
        } else if (cmd.equalsIgnoreCase("4")) {
            earringId = earringIdList[3];
        } else if (cmd.equalsIgnoreCase("5")) {
            earringId = earringIdList[4];
        } else if (cmd.equalsIgnoreCase("6")) {
            earringId = earringIdList[5];
        } else if (cmd.equalsIgnoreCase("7")) {
            earringId = earringIdList[6];
        } else if (cmd.equalsIgnoreCase("8")) {
            earringId = earringIdList[7];
        }
        if (earringId != 0) {
            item = pc.getInventory().storeItem(earringId, 1L);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
            }
            for (int id : earringIdList) {
                if (id == earringId) {
                    break;
                }
                if (pc.getInventory().checkItem(id)) {
                    pc.getInventory().consumeItem(id, 1L);
                }
            }
            htmlid = "";
        }
        return htmlid;
    }

    private String[] makeUbInfoStrings(int npcId) {
        L1UltimateBattle ub = UBTable.getInstance().getUbForNpcId(npcId);
        return ub.makeUbInfoStrings();
    }

    private String talkToDimensionDoor(L1PcInstance pc, L1NpcInstance npc, String cmd) {
        String htmlid = "";
        int protectionId = 0;
        int sealId = 0;
        int locX = 0;
        int locY = 0;
        short mapId = 0;
        if (npc.getNpcTemplate().get_npcId() == 80059) {
            protectionId = 40909;
            sealId = 40913;
            locX = 32773;
            locY = 32835;
            mapId = 607;
        } else if (npc.getNpcTemplate().get_npcId() == 80060) {
            protectionId = 40912;
            sealId = 40916;
            locX = 32757;
            locY = 32842;
            mapId = 606;
        } else if (npc.getNpcTemplate().get_npcId() == 80061) {
            protectionId = 40910;
            sealId = 40914;
            locX = 32830;
            locY = 32822;
            mapId = 604;
        } else if (npc.getNpcTemplate().get_npcId() == 80062) {
            protectionId = 40911;
            sealId = 40915;
            locX = 32835;
            locY = 32822;
            mapId = 605;
        }
        if (cmd.equalsIgnoreCase("a")) {
            L1Teleport.teleport(pc, locX, locY, mapId, 5, true);
            htmlid = "";
        } else if (cmd.equalsIgnoreCase("b")) {
            L1ItemInstance item = pc.getInventory().storeItem(protectionId, 1L);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
            }
            htmlid = "";
        } else if (cmd.equalsIgnoreCase("c")) {
            htmlid = "wpass07";
        } else if (cmd.equalsIgnoreCase("d")) {
            if (pc.getInventory().checkItem(sealId)) {
                L1ItemInstance item = pc.getInventory().findItemId(sealId);
                pc.getInventory().consumeItem(sealId, item.getCount());
            }
        } else if (cmd.equalsIgnoreCase("e")) {
            htmlid = "";
        } else if (cmd.equalsIgnoreCase("f")) {
            if (pc.getInventory().checkItem(protectionId)) {
                pc.getInventory().consumeItem(protectionId, 1L);
            }
            if (pc.getInventory().checkItem(sealId)) {
                L1ItemInstance item = pc.getInventory().findItemId(sealId);
                pc.getInventory().consumeItem(sealId, item.getCount());
            }
            htmlid = "";
        }
        return htmlid;
    }

    private void getBloodCrystalByKarma(L1PcInstance pc, L1NpcInstance npc, String cmd) {
        L1ItemInstance item = null;
        if (cmd.equalsIgnoreCase("1")) {
            pc.addKarma((int) (500.0D * ConfigRate.RATE_KARMA));
            item = pc.getInventory().storeItem(40718, 1L);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
            }
            pc.sendPackets(new S_ServerMessage(1081));
        } else if (cmd.equalsIgnoreCase("2")) {
            pc.addKarma((int) (5000.0D * ConfigRate.RATE_KARMA));
            item = pc.getInventory().storeItem(40718, 10L);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
            }
            pc.sendPackets(new S_ServerMessage(1081));
        } else if (cmd.equalsIgnoreCase("3")) {
            pc.addKarma((int) (50000.0D * ConfigRate.RATE_KARMA));
            item = pc.getInventory().storeItem(40718, 100L);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
            }
            pc.sendPackets(new S_ServerMessage(1081));
        }
    }

    private void getSoulCrystalByKarma(L1PcInstance pc, L1NpcInstance npc, String cmd) {
        L1ItemInstance item = null;
        if (cmd.equalsIgnoreCase("1")) {
            pc.addKarma((int) (-500.0D * ConfigRate.RATE_KARMA));
            item = pc.getInventory().storeItem(40678, 1L);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
            }
            pc.sendPackets(new S_ServerMessage(1080));
        } else if (cmd.equalsIgnoreCase("2")) {
            pc.addKarma((int) (-5000.0D * ConfigRate.RATE_KARMA));
            item = pc.getInventory().storeItem(40678, 10L);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
            }
            pc.sendPackets(new S_ServerMessage(1080));
        } else if (cmd.equalsIgnoreCase("3")) {
            pc.addKarma((int) (-50000.0D * ConfigRate.RATE_KARMA));
            item = pc.getInventory().storeItem(40678, 100L);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate().get_name(), item.getLogName()));
            }
            pc.sendPackets(new S_ServerMessage(1080));
        }
    }

    /**
     * [原碼] 無限大戰計分系統
     */
    private void UbRank(L1PcInstance pc, L1NpcInstance npc) {
        L1UltimateBattle ub = UBTable.getInstance().getUbForNpcId(npc.getNpcTemplate().get_npcId());
        String[] htmldata = null;
        htmldata = new String[11];
        htmldata[0] = npc.getNpcTemplate().get_name();
        String htmlid = "colos3";
        int i = 1;
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM ub_rank WHERE ub_id=? order by score desc limit 10");
            pstm.setInt(1, ub.getUbId());
            rs = pstm.executeQuery();
            while (rs.next()) {
                htmldata[i] = rs.getString(2) + " : " + String.valueOf(rs.getInt(3));
                i++;
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        pc.sendPackets(new S_NPCTalkReturn(pc, htmlid, htmldata));
    }

    private void ShellDamage(L1NpcInstance npc, int effect, int x, int y, L1PcInstance pc) {
        long curtime = System.currentTimeMillis() / 1000;
        if ((npc.getShellsDamageTime() + 10 > curtime) || (npc.getShellsSilenceTime() + 10 > curtime)) {
            // 使用投石器: 失敗(需要再次上子彈時間)
            pc.sendPackets(new S_ServerMessage(3680));
            return;
        } else if (!pc.getInventory().checkItem(82500, 1)) {
            pc.sendPackets(new S_SystemMessage("投石器需要炸彈才能發射。 "));
            return;
        }
        pc.getInventory().consumeItem(40308, 1);
        npc.setShellsDamageTime(curtime);
        Collection<L1PcInstance> list = null;
        list = World.get().getAllPlayers();
        for (L1PcInstance player : list) {
            if (L1CastleLocation.checkInAllWarArea(player.getX(), player.getY(), player.getMapId())) {
                player.sendPackets(new S_EffectLocation(x, y, effect));
                player.sendPackets(new S_DoActionGFX(npc.getId(), 1));
                if ((player.getX() >= x - 2 && player.getX() <= x + 2) && (player.getY() >= y - 2 && player.getY() <= y + 2)) {
                    player.receiveDamage(npc, 300, false, true);
                    player.sendPackets(new S_DoActionGFX(player.getId(), ActionCodes.ACTION_Damage));
                    npc.broadcastPacketAll(new S_DoActionGFX(player.getId(), ActionCodes.ACTION_Damage));
                }
                continue;
            } else {
                continue;
            }
        }
    }

    private void ShellsSilence(L1NpcInstance npc, int effect, int x, int y, L1PcInstance pc) {
        long curtime = System.currentTimeMillis() / 1000;
        if ((npc.getShellsDamageTime() + 10 > curtime) || (npc.getShellsSilenceTime() + 10 > curtime)) {
            // 使用投石器: 失敗(需要再次上子彈時間)
            pc.sendPackets(new S_ServerMessage(3680));
            return;
        } else if (!pc.getInventory().checkItem(82500, 1)) {
            pc.sendPackets(new S_SystemMessage("投石器需要炸彈才能發射。 "));
            return;
        }
        pc.getInventory().consumeItem(40308, 1);
        npc.setShellsSilenceTime(curtime);
        Collection<L1PcInstance> list = null;
        list = World.get().getAllPlayers();
        for (L1PcInstance player : list) {
            if (L1CastleLocation.checkInAllWarArea(player.getX(), player.getY(), player.getMapId())) {
                player.sendPackets(new S_EffectLocation(x, y, effect));
                player.sendPackets(new S_DoActionGFX(npc.getId(), 1));
                if ((player.getX() >= x - 2 && player.getX() <= x + 2) && (player.getY() >= y - 2 && player.getY() <= y + 2)) {
                    player.killSkillEffectTimer(SILENCE);
                    player.setSkillEffect(SILENCE, 15000);
                    player.sendPacketsAll(new S_SkillSound(player.getId(), 2177));
                    npc.broadcastPacketAll(new S_SkillSound(player.getId(), 2177));
                }
                continue;
            } else {
                continue;
            }
        }
    }

    /**
     * 檢查世界玩家當中是否有兩個相同帳號的PC
     *
     * @param c
     * @return
     */
    private boolean isTwoLogin(L1PcInstance c) {
        boolean bool = false;
        for (L1PcInstance target : World.get().getAllPlayersToArray()) {
            if (c.getId() != target.getId() && !target.isPrivateShop()) {
                if (c.getNetConnection().getAccountName().equalsIgnoreCase(target.getNetConnection().getAccountName())) {
                    bool = true;
                    break;
                }
            }
        }
        return bool;
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }
}
