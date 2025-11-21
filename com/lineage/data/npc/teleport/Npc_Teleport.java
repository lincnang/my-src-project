package com.lineage.data.npc.teleport;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.MapsGroupTable;
import com.lineage.server.datatables.NpcTeleportTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_PacketBoxGame;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1MapsLimitTime;
import com.lineage.server.templates.L1TeleportLoc;
import com.lineage.server.timecontroller.server.ServerUseMapTimer;
import com.lineage.server.world.WorldClan;

import java.util.*;

/**
 * 傳送師
 *
 * @author dexc
 */
public class Npc_Teleport extends NpcExecutor {
    public static NpcExecutor get() {
        return new Npc_Teleport();
    }

    /**
     * 執行傳送
     *
     */
    public static void teleport(L1PcInstance pc, Integer key) {
        final Map<Integer, L1TeleportLoc> list = pc.get_otherList().teleportMap();
        final L1TeleportLoc info = list.get(key);
        // 已經具有時間地圖使用權
        if (info.get_time() != 0) {
            Integer inMap = ServerUseMapTimer.MAP.get(pc);
            if (inMap != null) {
                if (pc.get_other().get_usemap() == info.get_mapid()) {
                    L1Teleport.teleport(pc, info.get_locx(), info.get_locy(), (short) info.get_mapid(), 5, true);
                } else {
                    pc.sendPackets(new S_ServerMessage("必須先消除其它地圖使用權才能進入這裡"));
                }
                return;
            }
        }
        boolean party = false;
        if (info.get_user() > 0) {
            if (!pc.isInParty()) {
                // 425：您並沒有參加任何隊伍。
                pc.sendPackets(new S_ServerMessage(425));
                return;
            }
            // 不是隊長
            if (!pc.getParty().isLeader(pc)) {
                pc.sendPackets(new S_ServerMessage("你必須是隊伍的領導者"));
                return;
            }
            // 人數不足
            if (pc.getParty().getNumOfMembers() < info.get_user()) {
                pc.sendPackets(new S_ServerMessage("隊伍成員必須達到" + info.get_user() + "人"));
                return;
            }
            party = true;
        }
        if (info.get_min() > pc.getLevel()) {
            pc.sendPackets(new S_ServerMessage("等級(" + pc.getLevel() + ")低於限制"));
            return;
        }
        if (info.get_max() < pc.getLevel()) {
            pc.sendPackets(new S_ServerMessage("等級(" + pc.getLevel() + ")超過限制"));
            return;
        }
        if (info.get_mapid() >= 248 && info.get_mapid() <= 251) {
            if (!checkHasCastle(pc, 4)) {
                return;
            }
        }
        /* 檢查時間限制 */
        final int startWeek = info.getStartWeek();
        final int startHour = info.getStartHour();
        final int endHour = info.getEndHour();
        final Calendar date = Calendar.getInstance();
        int today = date.get(Calendar.DAY_OF_WEEK);
        //final int nowWeek = (date.get(Calendar.DAY_OF_WEEK) - 1);
        final int nowHour = date.get(Calendar.HOUR_OF_DAY);
        int _today = 0;
        switch (startWeek) {
            case 1:
                _today = 2;
                break;
            case 2:
                _today = 3;
                break;
            case 3:
                _today = 4;
                break;
            case 4:
                _today = 5;
                break;
            case 5:
                _today = 6;
                break;
            case 6:
                _today = 7;
                break;
            case 7:
                _today = 1;
                break;
        }
        // 只限制星期
        if (startWeek > 0 && startHour < 0 && endHour < 0) {
            if (_today != today) {
                pc.sendPackets(new S_ServerMessage(166, "時間限制:星期【" + startWeek + " 】"));
                return;
            }
        }
        // 限制星期.限制時間
        else if (startWeek > 0 && startHour >= 0 && endHour >= 0) {
            if (_today != today || nowHour < startHour || nowHour >= endHour) {
                pc.sendPackets(new S_ServerMessage(166, "時間限制:星期【 " + startWeek + " 】時間為【 " + startHour + " 】點，至【 " + endHour + " 】點"));
                return;
            }
        }
        // 限制時間
        else if (startWeek < 0 && startHour >= 0 && endHour >= 0) {
            if (nowHour < startHour || nowHour >= endHour) {
                pc.sendPackets(new S_ServerMessage(166, "時間限制:時間為【 " + startHour + " 】點，至【 " + endHour + " 】點"));
                return;
            }
        }
        int itemid = info.get_itemid();
        L1ItemInstance item = pc.getInventory().checkItemX(itemid, info.get_price());
        if (item != null) {// 物品足夠
            // 地圖群組設置資料 (入場時間限制) by terry0412
            final L1MapsLimitTime mapsLimitTime = MapsGroupTable.get().findGroupMap(info.get_mapid());
            if (mapsLimitTime != null) {
                final int order_id = mapsLimitTime.getOrderId();
                final int used_time = pc.getMapsTime(order_id);
                final int limit_time = mapsLimitTime.getLimitTime() + pc.getMapsBonusTime(order_id);
                // 允許時間已到
                if (used_time > limit_time) {
                    pc.sendPackets(new S_ServerMessage("已超過該地圖的允許入場時間。"));
                    return;
                }
            }
            // 加入短時間計時地圖
            if (info.get_time() != 0) {
                pc.get_other().set_usemap(info.get_mapid());
                ServerUseMapTimer.put(pc, info.get_time());
                pc.sendPackets(new S_ServerMessage("使用時間限制:" + info.get_time() + "秒"));
            }
            pc.getInventory().removeItem(item, info.get_price());
            if (party) {
                //ConcurrentHashMap<?, ?> pcs = pc.getParty().partyUsers();
                final List<L1PcInstance> pcs = pc.getParty().getMemberList();
                if (pcs.isEmpty()) {
                    return;
                }
                for (L1PcInstance tgpc : pcs) {
                    if (info.get_time() != 0) {
                        Integer inMap = ServerUseMapTimer.MAP.get(tgpc);
                        if (inMap != null) {
                            tgpc.get_other().set_usemap(-1);
                            tgpc.get_other().set_usemapTime(0);
                            tgpc.sendPackets(new S_PacketBoxGame(72));
                            ServerUseMapTimer.MAP.remove(tgpc);
                        }
                        tgpc.get_other().set_usemap(info.get_mapid());
                        ServerUseMapTimer.put(tgpc, info.get_time());
                    }
                    L1Teleport.teleport(tgpc, info.get_locx(), info.get_locy(), (short) info.get_mapid(), 5, true);
                }
            } else {
                L1Teleport.teleport(pc, info.get_locx(), info.get_locy(), (short) info.get_mapid(), 5, true);
            }
        } else {
            // 找回物品
            L1Item itemtmp = ItemTable.get().getTemplate(itemid);
            pc.sendPackets(new S_ServerMessage(337, itemtmp.getNameId()));
        }
    }

    /**
     * 展示指定頁面
     *
     */
    public static void showPage(L1PcInstance pc, L1NpcInstance npc, int page) {
        final Map<Integer, L1TeleportLoc> list = pc.get_otherList().teleportMap();
        int allpage = list.size() / 10;
        if ((page > allpage) || (page < 0)) {
            page = 0;
        }
        if (list.size() % 10 != 0) {
            allpage++;
        }
        pc.get_other().set_page(page);
        int showId = page * 10;
        StringBuilder stringBuilder = new StringBuilder();
        for (int key = showId; key < showId + 10; key++) {
            L1TeleportLoc info = list.get(key);
            if (info != null) {
                L1Item itemtmp = ItemTable.get().getTemplate(info.get_itemid());
                if (itemtmp != null) {
                    stringBuilder.append(info.get_name());
                    if (info.get_user() > 0) {
                        stringBuilder.append("隊伍:").append(info.get_user());
                    }
                    stringBuilder.append(" (").append(itemtmp.getName()).append("-").append(Integer.toString(info.get_price())).append("),");
                }
            }
        }
        String[] clientStrAry = stringBuilder.toString().split(",");
        if (allpage == 1) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_t_1", clientStrAry));
        } else if (page < 1) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_t_3", clientStrAry));
        } else if (page >= allpage - 1) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_t_4", clientStrAry));
        } else {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_t_2", clientStrAry));
        }
    }

    private static boolean checkHasCastle(L1PcInstance pc, int castleId) {
        boolean isExistDefenseClan = false;
        Collection<?> allClans = WorldClan.get().getAllClans();
        for (Object allClan : allClans) {
            L1Clan clan = (L1Clan) allClan;
            if (castleId == clan.getCastleId()) {
                isExistDefenseClan = true;
                break;
            }
        }
        if (!isExistDefenseClan) {
            return true;
        }
        if (pc.getClanid() != 0) {
            L1Clan clan = WorldClan.get().getClan(pc.getClanname());
            return (clan != null) && (clan.getCastleId() == castleId);
        }
        return false;
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        switch (npc.getNpcId()) {
            case 94000:
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_t_5801"));
                break;
            case 300154:
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "test001"));
                break;
            case 8000101:
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_t_6"));
                break;
            case 8000102:
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_t_7"));
                break;
            case 8000103:
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_t_8"));
                break;
            case 8000104:
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_t_9"));
                break;
            case 8000105:
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_t_10"));
                break;
            default:
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_t_0"));
                break;
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        if (cmd.equals("up")) {
            int page = pc.get_other().get_page() - 1;
            showPage(pc, npc, page);
        } else if (cmd.equals("dn")) {
            int page = pc.get_other().get_page() + 1;
            showPage(pc, npc, page);
        } else if (cmd.equals("del")) {
            Integer inMap = ServerUseMapTimer.MAP.get(pc);
            if (inMap != null) {
                pc.get_other().set_usemap(-1);
                pc.get_other().set_usemapTime(0);
                pc.sendPackets(new S_PacketBoxGame(72));
                ServerUseMapTimer.MAP.remove(pc);
            }
        } else if (cmd.matches("[0-9]+")) {
            String pagecmd = pc.get_other().get_page() + cmd;
            teleport(pc, Integer.valueOf(pagecmd));
        } else {
            pc.get_other().set_page(0);
            HashMap<Integer, L1TeleportLoc> teleportMap = NpcTeleportTable.get().get_teles(cmd);
            if (teleportMap != null) {
                if (teleportMap.isEmpty()) {
                    pc.sendPackets(new S_ServerMessage(1447));
                    return;
                }
                pc.get_otherList().teleport(teleportMap);
                showPage(pc, npc, 0);
            } else {
                pc.sendPackets(new S_ServerMessage(1447));
            }
        }
    }
}
