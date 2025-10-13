package com.lineage.server.model;

import com.add.NewAuto.AutoAttackUpdate;
import com.add.Tsai.Astrology.AstrologyCmd;
import com.add.Tsai.Astrology.AttonAstrologyCmd;
import com.add.Tsai.*;
import com.lineage.config.Config;
import com.lineage.config.ConfigAutoAll;
import com.lineage.config.ConfigOther;
import com.lineage.config.ConfigTurn;
import com.lineage.data.QuestClass;
import com.lineage.data.item_weapon.proficiency.L1WeaponProficiency;
import com.lineage.data.npc.Npc_DollCombind;
import com.lineage.data.npc.Npc_HolyCombind;
import com.lineage.data.npc.Npc_MagicCombind;
import com.lineage.data.npc.Npc_PolyCombind;
import com.lineage.data.npc.other2.Npc_Bao1;
import com.lineage.server.command.executor.L1ToPC;
import com.lineage.server.datatables.*;
import com.lineage.server.datatables.lock.*;
import com.lineage.server.datatables.sql.SpawnBossTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.*;
import com.lineage.server.timecontroller.server.ServerRestartTimer;
import com.lineage.server.timecontroller.server.ServerUseMapTimer;
import com.lineage.server.utils.CalcStat;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import com.lineage.server.templates.L1EmblemIcon;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import william.Honor;
import william.L1WilliamHonor;
import william.Npc_Honor;
import william.ReincarnationSkill;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.lineage.server.model.skill.L1SkillId.*;

/**
 * 對話命令來自PC的執行與判斷
 *
 * @author daien
 */
public class L1ActionPc {
    public static final String[] TYPE_CLASS = new String[]{"王族", "騎士", "精靈", "法師", "黑妖", "龍騎士", "幻術師"};
    private static final Log _log = LogFactory.getLog(L1ActionPc.class);
    private final L1PcInstance _pc;

    /**
     * 對話命令來自PC的執行與判斷
     *
     * @param pc 執行者
     */
    public L1ActionPc(final L1PcInstance pc) {
        _pc = pc;
    }

    public static void showPage(L1PcInstance pc, int page) {
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
            pc.sendPackets(new S_NPCTalkReturn(pc, "y_t_1", clientStrAry));
        } else if (page < 1) {
            pc.sendPackets(new S_NPCTalkReturn(pc, "y_t_3", clientStrAry));
        } else if (page >= allpage - 1) {
            pc.sendPackets(new S_NPCTalkReturn(pc, "y_t_4", clientStrAry));
        } else {
            pc.sendPackets(new S_NPCTalkReturn(pc, "y_t_2", clientStrAry));
        }
    }

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
                if (pc.hasSkillEffect(PC_TEL_LOCK)) {
                    pc.removeSkillEffect(PC_TEL_LOCK);
                }
                //				pc.setNewTeleport(false);
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
            if (!checkHasCastle(pc)) {
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
                final int limit_time = mapsLimitTime.getLimitTime();
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

    private static boolean checkHasCastle(L1PcInstance pc) {
        boolean isExistDefenseClan = false;
        Collection<?> allClans = WorldClan.get().getAllClans();
        for (Object allClan : allClans) {
            L1Clan clan = (L1Clan) allClan;
            if (4 == clan.getCastleId()) {
                isExistDefenseClan = true;
                break;
            }
        }
        if (!isExistDefenseClan) {
            return true;
        }
        if (pc.getClanid() != 0) {
            L1Clan clan = WorldClan.get().getClan(pc.getClanname());
            return (clan != null) && (clan.getCastleId() == 4);
        }
        return false;
    }

    /**
     * 查看執行中任務
     *
     */
    public static void showStartQuest(L1PcInstance pc, int objid) {
        try {
            // 清空暫存任務清單
            pc.get_otherList().QUESTMAP.clear();
            int key = 0;
            for (int i = QuestTable.MINQID; i <= QuestTable.MAXQID; i++) {
                final L1Quest value = QuestTable.get().getTemplate(i);
                if (value != null) {
                    // 該任務已經結束
                    if (pc.getQuest().isEnd(value.get_id())) {
                        continue;
                    }
                    // 執行中任務判斷
                    if (pc.getQuest().isStart(value.get_id())) {
                        pc.get_otherList().QUESTMAP.put(key++, value);
                    }
                }
            }
            if (pc.get_otherList().QUESTMAP.size() <= 0) {
                // 很抱歉!!你並沒有任何執行中的任務!
                pc.sendPackets(new S_NPCTalkReturn(objid, "y_q_not7"));
            } else {
                final L1ActionShowHtml show = new L1ActionShowHtml(pc);
                show.showQuestMap(0);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 可執行任務
     *
     */
    public static void showQuest(L1PcInstance pc, int objid) {
        try {
            // 清空暫存任務清單
            pc.get_otherList().QUESTMAP.clear();
            int key = 0;
            for (int i = QuestTable.MINQID; i <= QuestTable.MAXQID; i++) {
                final L1Quest value = QuestTable.get().getTemplate(i);
                if (value != null) {
                    // 大於可執行等級
                    if (pc.getLevel() >= value.get_questlevel()) {
                        // 該任務已經結束
                        if (pc.getQuest().isEnd(value.get_id())) {
                            continue;
                        }
                        // 該任務已經開始
                        if (pc.getQuest().isStart(value.get_id())) {
                            continue;
                        }
                        // 可執行職業判斷
                        if (value.check(pc)) {
                            pc.get_otherList().QUESTMAP.put(key++, value);
                        }
                    }
                }
            }
            if (pc.get_otherList().QUESTMAP.size() <= 0) {
                // 很抱歉!!目前你的任務已經全部完成!
                pc.sendPackets(new S_NPCTalkReturn(objid, "y_q_not4"));
            } else {
                final L1ActionShowHtml show = new L1ActionShowHtml(pc);
                show.showQuestMap(0);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 全部任務
     *
     */
    public static void showQuestAll(L1PcInstance pc, int objid) {
        try {
            // 清空暫存任務清單
            pc.get_otherList().QUESTMAP.clear();
            int key = 0;
            for (int i = QuestTable.MINQID; i <= QuestTable.MAXQID; i++) {
                final L1Quest value = QuestTable.get().getTemplate(i);
                if (value != null) {
                    // 可執行職業判斷
                    if (value.check(pc)) {
                        pc.get_otherList().QUESTMAP.put(key++, value);
                    }
                }
            }
            final L1ActionShowHtml show = new L1ActionShowHtml(pc);
            show.showQuestMap(0);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 傳回執行命令者
     *
     */
    public L1PcInstance get_pc() {
        return _pc;
    }

    /**
     * 選單命令執行
     *
     */
    public void action(final String cmd, final long amount) {
        try {
            if (_pc.hasSkillEffect(PC_TEL_LOCK)) { // pc傳送選擇鎖定指令
                //            if (_pc.isNewTeleport()) {
                if (cmd.equals("up")) {
                    int page = _pc.get_other().get_page() - 1;
                    showPage(_pc, page);
                } else if (cmd.equals("dn")) {
                    int page = _pc.get_other().get_page() + 1;
                    showPage(_pc, page);
                } else if (cmd.equals("del")) {
                    Integer inMap = ServerUseMapTimer.MAP.get(_pc);
                    if (inMap != null) {
                        _pc.get_other().set_usemap(-1);
                        _pc.get_other().set_usemapTime(0);
                        _pc.sendPackets(new S_PacketBoxGame(72));
                        ServerUseMapTimer.MAP.remove(_pc);
                    }
                } else if (cmd.matches("[0-9]+")) {
                    String pagecmd = _pc.get_other().get_page() + cmd;
                    teleport(_pc, Integer.valueOf(pagecmd));
                    _pc.setShapeChange(false);
                } else {
                    _pc.get_other().set_page(0);
                    HashMap<Integer, L1TeleportLoc> teleportMap = NpcTeleportTable.get().get_teles(cmd);
                    if (teleportMap != null) {
                        if (teleportMap.isEmpty()) {
                            _pc.sendPackets(new S_ServerMessage(1447));
                            return;
                        }
                        _pc.get_otherList().teleport(teleportMap);
                        showPage(_pc, 0);
                    } else {
                        _pc.sendPackets(new S_ServerMessage(1447));
                    }
                }
                //                _pc.sendPackets(new S_ServerMessage("\\f3傳送選擇鎖定中，其他指令將無法生效。"));
                return;
            }
            if (ConfigAutoAll.Auto_All) {
                if (cmd.equalsIgnoreCase("jlyxxm")) { // 展示遊戲小蜜對話檔
                    _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "jlyxxm")); // jlyxxm遊戲小蜜對話檔
                    return;
                }
                // ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
                // 輔助(自動補血)
                if (cmd.equalsIgnoreCase("yxbx")) { // 展示輔助(自動補血)對話檔
                    _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "zdbx")); // zdbx自動補血對話檔
                    show_zdbx(_pc);
                } else if (cmd.equalsIgnoreCase("bxkg")) { // 輔助(自動補血)總開關 -> 目前
                    if (!_pc.isAutoHpAll()) {
                        _pc.setAutoHpAll(true);// 自動補血判定 開
                    } else {
                        _pc.setAutoHpAll(false);// 自動補血判定 關
                    }
                    show_zdbx(_pc);
                    // ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
                    // 輔助(自動補血) -> 第一組
                } else if (cmd.equalsIgnoreCase("bx1")) { // 輔助(自動補血) -> 第一組補血開關
                    if (!_pc.isAutoHp1()) {
                        _pc.setAutoHp1(true);// 自動補血判定 開
                        final L1Item item = ItemTable.get().getTemplate(_pc.getAutoItemId1());
                        _pc.startSkillSound_autoHP1(item.get_delaytime());
                    } else {
                        _pc.stopSkillSound_autoHP1();// 自動補血判定 關
                    }
                    show_zdbx(_pc);
                } else if (cmd.equalsIgnoreCase("bxaa")) { // 輔助(自動補血) -> 加
                    if (_pc.getTextHp1() >= 98) {
                        _pc.sendPackets(new S_ServerMessage("\\aE第一組最高隻能設置至98%"));
                        return;
                    }
                    _pc.setTextHp1(_pc.getTextHp1() + 1);
                    show_zdbx(_pc);
                } else if (cmd.equalsIgnoreCase("bxab")) { // 輔助(自動補血) -> 減
                    if (_pc.getTextHp1() <= 75) {
                        _pc.sendPackets(new S_ServerMessage("\\aE第一組最低只能設置至75%"));
                        return;
                    }
                    _pc.setTextHp1(_pc.getTextHp1() - 1);
                    show_zdbx(_pc);
                } else if (cmd.equalsIgnoreCase("bxac")) { // 輔助(自動補血) -> 點選
                    _pc.setTemporary(1);
                    _pc.sendPackets(new S_AutoHpShopSellList(_pc, 1));
                    _pc.stopSkillSound_autoHP1();// 自動補血判定 關
                    _pc.sendPackets(new S_CloseList(_pc.getId()));
                    // 輔助(自動補血) -> 第一組 end
                    // ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
                    // 輔助(自動補血) -> 第二組
                } else if (cmd.equalsIgnoreCase("bx2")) { // 輔助(自動補血) -> 第二組補血開關
                    if (!_pc.isAutoHp2()) {
                        _pc.setAutoHp2(true);// 自動補血判定 開
                        final L1Item item = ItemTable.get().getTemplate(_pc.getAutoItemId2());
                        _pc.startSkillSound_autoHP2(item.get_delaytime());
                    } else {
                        _pc.stopSkillSound_autoHP2();// 自動補血判定 關
                    }
                    show_zdbx(_pc);
                } else if (cmd.equalsIgnoreCase("bxba")) { // 輔助(自動補血) -> 加
                    if (_pc.getTextHp2() >= 65) {
                        _pc.sendPackets(new S_ServerMessage("\\aE第二組最高隻能設置至65%"));
                        return;
                    }
                    _pc.setTextHp2(_pc.getTextHp2() + 1);
                    show_zdbx(_pc);
                } else if (cmd.equalsIgnoreCase("bxbb")) { // 輔助(自動補血) -> 減
                    if (_pc.getTextHp2() <= 20) {
                        _pc.sendPackets(new S_ServerMessage("\\aE第二組最低只能設置至20%"));
                        return;
                    }
                    _pc.setTextHp2(_pc.getTextHp2() - 1);
                    show_zdbx(_pc);
                } else if (cmd.equalsIgnoreCase("bxbc")) { // 輔助(自動補血) -> 點選
                    _pc.setTemporary(2);
                    _pc.sendPackets(new S_AutoHpShopSellList(_pc, 2));
                    _pc.stopSkillSound_autoHP2();// 自動補血判定 關
                    _pc.sendPackets(new S_CloseList(_pc.getId()));
                    // 輔助(自動補血) -> 第二組 end
                    // ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
                    // 輔助(自動補血) -> 第三組
                } else if (cmd.equalsIgnoreCase("bx3")) { // 輔助(自動補血) -> 第三組補血開關
                    if (!_pc.isAutoHp3()) {
                        _pc.setAutoHp3(true);// 自動補血判定 開
                        final L1Item item = ItemTable.get().getTemplate(_pc.getAutoItemId3());
                        _pc.startSkillSound_autoHP3(item.get_delaytime());
                    } else {
                        _pc.stopSkillSound_autoHP3();// 自動補血判定 關
                    }
                    show_zdbx(_pc);
                } else if (cmd.equalsIgnoreCase("bxca")) { // 輔助(自動補血) -> 加
                    if (_pc.getTextHp3() >= 85) {
                        _pc.sendPackets(new S_ServerMessage("\\aE第三組最高隻能設置至85%"));
                        return;
                    }
                    _pc.setTextHp3(_pc.getTextHp3() + 1);
                    show_zdbx(_pc);
                } else if (cmd.equalsIgnoreCase("bxcb")) { // 輔助(自動補血) -> 減
                    if (_pc.getTextHp3() <= 20) {
                        _pc.sendPackets(new S_ServerMessage("\\aE第三組最低只能設置至20%"));
                        return;
                    }
                    _pc.setTextHp3(_pc.getTextHp3() - 1);
                    show_zdbx(_pc);
                } else if (cmd.equalsIgnoreCase("bxcc")) { // 輔助(自動補血) -> 點選
                    _pc.setTemporary(3);
                    _pc.sendPackets(new S_AutoHpShopSellList(_pc, 3));
                    _pc.stopSkillSound_autoHP3();// 自動補血判定 關
                    _pc.sendPackets(new S_CloseList(_pc.getId()));
                    // 輔助(自動補血) -> 第三組 end
                    // ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
                    // 輔助(自動回城)
                } else if (cmd.equalsIgnoreCase("hcsz")) { // 輔助(自動回城) -> 回城符開關
                    if (!_pc.isAutoBackHome()) {
                        _pc.setAutoBackHome(true);// 自動回城判定 開
                        _pc.startSkillSound_autoBackHome();
                    } else {
                        _pc.setAutoBackHome(false);// 自動回城判定 關
                    }
                    show_zdbx(_pc);
                } else if (cmd.equalsIgnoreCase("hca")) { // 輔助(自動回城) -> 加
                    if (_pc.getTextBh() >= 40) {
                        _pc.sendPackets(new S_ServerMessage("\\aE自動回城最高隻能設置至40%"));
                        return;
                    }
                    _pc.setTextBh(_pc.getTextBh() + 1);
                    show_zdbx(_pc);
                } else if (cmd.equalsIgnoreCase("hcb")) { // 輔助(自動回城) -> 減
                    if (_pc.getTextBh() <= 5) {
                        _pc.sendPackets(new S_ServerMessage("\\aE自動回城最低只能設置至5%"));
                        return;
                    }
                    _pc.setTextBh(_pc.getTextBh() - 1);
                    show_zdbx(_pc);
                    // 輔助(自動回城) end
                    // ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
                    // 輔助(自動補魔) -> 第一組
                } else if (cmd.equalsIgnoreCase("bm1")) { // 輔助(自動補魔) -> 第一組補魔開關
                    if (!_pc.isAutoMp1()) {
                        _pc.setAutoMp1(true);// 自動魂體判定 開
                        _pc.startSkillSound_autoMP1();
                    } else {
                        _pc.stopSkillSound_autoMP1();// 自動補魔判定 關
                    }
                    show_zdbx(_pc);
                } else if (cmd.equalsIgnoreCase("bmaa")) { // 輔助(自動補魔) -> 加
                    if (_pc.getTextMp1() >= 95) {
                        _pc.sendPackets(new S_ServerMessage("\\aE自動補魔最高隻能設置至95%"));
                        return;
                    }
                    _pc.setTextMp1(_pc.getTextMp1() + 1);
                    show_zdbx(_pc);
                } else if (cmd.equalsIgnoreCase("bmab")) { // 輔助(自動補魔) -> 減
                    if (_pc.getTextMp1() <= 5) {
                        _pc.sendPackets(new S_ServerMessage("\\aE自動補魔最低只能設置至5%"));
                        return;
                    }
                    _pc.setTextMp1(_pc.getTextMp1() - 1);
                    show_zdbx(_pc);
                } else if (cmd.equalsIgnoreCase("bmac")) { // 輔助(自動補魔) -> 點選
                    _pc.setTemporary(4);
                    _pc.sendPackets(new S_AutoHpShopSellList(_pc, 4));
                    _pc.sendPackets(new S_CloseList(_pc.getId()));
                    // 輔助(自動補魔) -> 第一組 end
                    // ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
                    // 輔助(自動補魔) -> 第二組
                } else if (cmd.equalsIgnoreCase("bm2")) { // 輔助(自動魂體) -> 第二組補魔開關
                    if (!_pc.isElf()) {
                        _pc.sendPackets(new S_SystemMessage("\\aE你的職業無法使用"));
                        return;
                    }
                    if (!_pc.isAutoMp2()) {
                        _pc.setAutoMp2(true);// 自動魂體判定 開
                        _pc.startSkillSound_autoMP2();
                    } else {
                        _pc.setAutoMp2(false);// 自動魂體判定 關
                    }
                    show_zdbx(_pc);
                } else if (cmd.equalsIgnoreCase("bmba")) { // 輔助(自動魂體) -> 加
                    if (!_pc.isElf()) {
                        _pc.sendPackets(new S_SystemMessage("\\aE你的職業無法使用"));
                        return;
                    }
                    if (_pc.getTextMp2() >= 95) {
                        _pc.sendPackets(new S_ServerMessage("\\aE自動魂體最高隻能設置至95%"));
                        return;
                    }
                    _pc.setTextMp2(_pc.getTextMp2() + 1);
                    show_zdbx(_pc);
                } else if (cmd.equalsIgnoreCase("bmbb")) { // 輔助(自動魂體) -> 減
                    if (!_pc.isElf()) {
                        _pc.sendPackets(new S_SystemMessage("\\aE你的職業無法使用"));
                        return;
                    }
                    if (_pc.getTextMp2() <= 5) {
                        _pc.sendPackets(new S_ServerMessage("\\aE自動魂體最低只能設置至5%"));
                        return;
                    }
                    _pc.setTextMp2(_pc.getTextMp2() - 1);
                    show_zdbx(_pc);
                } else if (cmd.equalsIgnoreCase("bmbc")) { // 輔助(自動魂體) -> 點選
                    if (!_pc.isElf()) {
                        _pc.sendPackets(new S_SystemMessage("\\aE你的職業無法使用"));
                        return;
                    }
                    _pc.sendPackets(new S_ServerMessage("\\aE只支持魂體轉換並得學習"));
                    // 輔助(自動補魔) -> 第二組 end
                    return;
                }
                // 輔助(自動補血) end
                // ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
                // 輔助狀態(道具)
                if (cmd.equalsIgnoreCase("yxdj")) { // 展示輔助狀態(道具)對話檔
                    _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "yxngxt")); // yxngxt輔助狀態(道具)對話檔
                    show_yxdj(_pc);
                } else if (cmd.equalsIgnoreCase("eff0")) { // 輔助狀態(道具)總開關 -> 目前djxtkg
                    if (!_pc.isAutoItemAll()) {
                        _pc.setAutoItemAll(true);// 開
                        _pc.startSkillSound_autoItemAll();
                    } else {
                        //_pc.setAutoItemAll(false);// 關
                        _pc.stopSkillSound_autoItemAll();
                    }
                    show_yxdj(_pc);
                } else if (cmd.equalsIgnoreCase("eff1")) { // 輔助狀態(道具) -> 解毒藥水
                    if (!_pc.isAutoItem1()) {
                        _pc.setAutoItem1(true);// 開
                        _pc.sendPackets(new S_ServerMessage("\\aE只支持解毒藥水，其它藥水需手動，木詛不支持自動解毒"));
                    } else {
                        _pc.setAutoItem1(false);// 關
                    }
                    show_yxdj(_pc);
                } else if (cmd.equalsIgnoreCase("eff2")) { // 輔助狀態(道具) -> 藍色藥水
                    if (!_pc.isAutoItem2()) {
                        _pc.setAutoItem2(true);// 開
                        _pc.sendPackets(new S_ServerMessage("\\aE只支持藍色藥水，其它需手動"));
                    } else {
                        _pc.setAutoItem2(false);// 關
                    }
                    show_yxdj(_pc);
                } else if (cmd.equalsIgnoreCase("eff3")) { // 輔助狀態(道具) -> 慎重藥水
                    if (!_pc.isWizard()) {
                        _pc.sendPackets(new S_SystemMessage("\\aE你的職業無法使用"));
                        return;
                    }
                    if (!_pc.isAutoItem3()) {
                        _pc.setAutoItem3(true);// 開
                        _pc.sendPackets(new S_ServerMessage("\\aE只支持慎重藥水，其它需手動"));
                    } else {
                        _pc.setAutoItem3(false);// 關
                    }
                    show_yxdj(_pc);
                } else if (cmd.equalsIgnoreCase("eff4")) { // 輔助狀態(道具) -> 綠色藥水
                    if (!_pc.isAutoItem4()) {
                        _pc.setAutoItem4(true);// 開
                        _pc.sendPackets(new S_ServerMessage("\\aE只支持大小綠藥水，其它需手動"));
                    } else {
                        _pc.setAutoItem4(false);// 關
                    }
                    show_yxdj(_pc);
                } else if (cmd.equalsIgnoreCase("eff5")) { // 輔助狀態(道具) -> 名譽貨幣
                    if (!_pc.isAutoItem5()) {
                        _pc.setAutoItem5(true);// 開
                        _pc.sendPackets(new S_ServerMessage("\\aE只支持名譽貨幣，其它需手動"));
                    } else {
                        _pc.setAutoItem5(false);// 關
                    }
                    show_yxdj(_pc);
                } else if (cmd.equalsIgnoreCase("eff6")) { // 輔助狀態(道具) -> 巧克力蛋糕
                    if (!_pc.isAutoItem6()) {
                        _pc.setAutoItem6(true);// 開
                        //_pc.sendPackets(new S_ServerMessage("\\aE只支持巧克力蛋糕，其它需手動"));
                    } else {
                        _pc.setAutoItem6(false);// 關
                    }
                    show_yxdj(_pc);
                } else if (cmd.equalsIgnoreCase("eff7")) { // 輔助狀態(道具) -> 自動變形卷軸
                    if (!_pc.isAutoItem7()) {
                        _pc.setAutoItem7(true);// 開
                    } else {
                        _pc.setAutoItem7(false);// 關
                    }
                    show_yxdj(_pc);
                } else if (cmd.equalsIgnoreCase("eff8")) { // 輔助狀態(道具) -> 自動聖結界卷軸
                    if (!_pc.isAutoItem8()) {
                        _pc.setAutoItem8(true);// 開
                    } else {
                        _pc.setAutoItem8(false);// 關
                    }
                    show_yxdj(_pc);
                } else if (cmd.equalsIgnoreCase("eff9")) { // 輔助狀態(道具) -> 自動魔法娃娃召喚
                    /*if (!_pc.isAutoItem9()) {
						_pc.setAutoItem9(true);// 開
					} else {
						_pc.setAutoItem9(false);// 關
					}
					show_yxdj(_pc);*/
                    _pc.sendPackets(new S_ServerMessage("\\aE待加入..."));
                } else if (cmd.equalsIgnoreCase("eff10")) { // 輔助狀態(道具) -> 自動一段經驗藥水
                    if (!_pc.isAutoItem10()) {
                        _pc.setAutoItem10(true);// 開
                        _pc.sendPackets(new S_ServerMessage("\\aE目前只支持150%跟200%的，優先自動使用200%的"));
                    } else {
                        _pc.setAutoItem10(false);// 關
                    }
                    show_yxdj(_pc);
                } else if (cmd.equalsIgnoreCase("eff11")) { // 輔助狀態(道具) -> 自動二段經驗藥水
                    if (!_pc.isAutoItem11()) {
                        _pc.setAutoItem11(true);// 開
                        _pc.sendPackets(new S_ServerMessage("\\aE目前只支持150%跟200%的，優先自動使用200%的"));
                    } else {
                        _pc.setAutoItem11(false);// 關
                    }
                    show_yxdj(_pc);
                } else if (cmd.equalsIgnoreCase("eff12")) { // 輔助狀態(道具) -> 飽食度瞬滿(5萬金幣)
                    if (!_pc.isAutoItem12()) {
                        _pc.setAutoItem12(true);// 開
                        _pc.sendPackets(new S_ServerMessage("\\aE飽食度低于18%瞬滿，需5萬金幣"));
                    } else {
                        _pc.setAutoItem12(false);// 關
                    }
                    show_yxdj(_pc);
                } else if (cmd.equalsIgnoreCase("eff13")) { // 輔助狀態(道具) -> 自動魔法卷軸(魔法屏障)
                    if (!_pc.isAutoItem13()) {
                        _pc.setAutoItem13(true);// 開
                    } else {
                        _pc.setAutoItem13(false);// 關
                    }
                    show_yxdj(_pc);
                    return;
                }
                // 輔助狀態(道具) end
                // ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
                // 輔助狀態(魔法)
                if (cmd.equalsIgnoreCase("yxmf")) { // 展示輔助狀態(魔法)對話檔
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfxtkg")) { // 輔助狀態(魔法)總開關 -> 目前
                    if (!_pc.isAutoSkillAll()) {
                        _pc.setAutoSkillAll(true);// 開
                        _pc.startSkillSound_autoSkillAll();
                        _pc.sendPackets(new S_SystemMessage("\\aE當有些技能無法自動時，請檢查技能需求是否滿足"));
                    } else {
                        //_pc.setAutoSkillAll(false);// 關
                        _pc.stopSkillSound_autoSkillAll();
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz01")) { // 輔助狀態(魔法)開關 -> 加速術
                    if (!_pc.isAutoSkill_1()) {
                        _pc.setAutoSkill_1(true);// 開
                        if (_pc.isWizard()) {// 法師
                            _pc.setAutoSkill_11(false);// 關 -> 強力加速術
                        }
                    } else {
                        _pc.setAutoSkill_1(false);// 關
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz02")) { // 輔助狀態(魔法)開關 -> 擬似魔法武器
                    if (!_pc.isAutoSkill_2()) {
                        _pc.setAutoSkill_2(true);// 開
                    } else {
                        _pc.setAutoSkill_2(false);// 關
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz03")) { // 輔助狀態(魔法)開關 -> 保護罩
                    if (!_pc.isAutoSkill_3()) {
                        _pc.setAutoSkill_3(true);// 開
                        if (_pc.isElf()) {// 精靈
                            _pc.setAutoSkill_16(false);// 關 -> 大地防護
                            _pc.setAutoSkill_18(false);// 關 -> 鋼鐵防護
                        }
                    } else {
                        _pc.setAutoSkill_3(false);// 關
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz04")) { // 輔助狀態(魔法)開關 -> 祝福魔法武器
                    if (!_pc.isAutoSkill_4()) {
                        _pc.setAutoSkill_4(true);// 開
                        _pc.setAutoSkill_5(false);// 關 -> 神聖武器
                    } else {
                        _pc.setAutoSkill_4(false);// 關
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz05")) { // 輔助狀態(魔法)開關 -> 神聖武器
                    if (!_pc.isAutoSkill_5()) {
                        _pc.setAutoSkill_5(true);// 開
                        _pc.setAutoSkill_4(false);// 關 -> 祝福魔法武器
                    } else {
                        _pc.setAutoSkill_5(false);// 關
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz06")) { // 輔助狀態(魔法)開關 -> 通暢氣脈術
                    if (!_pc.isAutoSkill_6()) {
                        _pc.setAutoSkill_6(true);// 開
                    } else {
                        _pc.setAutoSkill_6(false);// 關
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz07")) { // 輔助狀態(魔法)開關 -> 鎧甲護持
                    if (!_pc.isAutoSkill_7()) {
                        _pc.setAutoSkill_7(true);// 開
                    } else {
                        _pc.setAutoSkill_7(false);// 關
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz08")) { // 輔助狀態(魔法)開關 -> 體魄強健術
                    if (!_pc.isAutoSkill_8()) {
                        _pc.setAutoSkill_8(true);// 開
                    } else {
                        _pc.setAutoSkill_8(false);// 關
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz09")) { // 輔助狀態(魔法)開關 -> 優先使用魔法卷軸
                    /*if (!_pc.isAutoSkill_9()) {
						_pc.setAutoSkill_9(true);// 開
					} else {
						_pc.setAutoSkill_9(false);// 關
					}
					show_ngxtAll(_pc);*/
                    _pc.sendPackets(new S_ServerMessage("\\aE待加入..."));
                } else if (cmd.equalsIgnoreCase("mfpz10")) { // 輔助狀態(魔法)開關 ->
                    if (!_pc.isAutoSkill_10()) {
                        _pc.setAutoSkill_10(true);// 開
                    } else {
                        _pc.setAutoSkill_10(false);// 關
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz11")) { // 輔助狀態(魔法)開關 ->
                    if (!_pc.isAutoSkill_11()) {
                        _pc.setAutoSkill_11(true);// 開
                        if (_pc.isWizard()) {// 法師
                            _pc.setAutoSkill_1(false);// 關 -> 加速術
                        }
                    } else {
                        _pc.setAutoSkill_11(false);// 關
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz12")) { // 輔助狀態(魔法)開關 ->
                    if (!_pc.isAutoSkill_12()) {
                        _pc.setAutoSkill_12(true);// 開
                    } else {
                        _pc.setAutoSkill_12(false);// 關
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz13")) { // 輔助狀態(魔法)開關 ->
                    if (_pc.isWizard()) {// 法師 -> 13 自動絕對屏障
                        _pc.sendPackets(new S_ServerMessage("\\aE暂不开放..."));
                        return;
                    }
                    if (!_pc.isAutoSkill_13()) {
                        _pc.setAutoSkill_13(true);// 開
                    } else {
                        _pc.setAutoSkill_13(false);// 關
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz14")) { // 輔助狀態(魔法)開關 ->
                    if (!_pc.isAutoSkill_14()) {
                        _pc.setAutoSkill_14(true);// 開
                    } else {
                        _pc.setAutoSkill_14(false);// 關
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz15")) { // 輔助狀態(魔法)開關 ->
                    if (!_pc.isAutoSkill_15()) {
                        _pc.setAutoSkill_15(true);// 開
                        if (_pc.isDragonKnight()) {// 龍騎
                            _pc.setAutoSkill_16(false);// 關 -> 自動覺醒：安塔瑞斯
                            _pc.setAutoSkill_17(false);// 關 -> 自動覺醒：巴拉卡斯
                        }
                    } else {
                        _pc.setAutoSkill_15(false);// 關
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz16")) { // 輔助狀態(魔法)開關 ->
                    if (!_pc.isAutoSkill_16()) {
                        _pc.setAutoSkill_16(true);// 開
                        if (_pc.isElf()) {// 精靈
                            _pc.setAutoSkill_3(false); // 關 -> 保護罩
                            _pc.setAutoSkill_18(false);// 關 -> 鋼鐵防護
                        }
                        if (_pc.isDragonKnight()) {// 龍騎
                            _pc.setAutoSkill_15(false);// 關 -> 自動覺醒：法利昂
                            _pc.setAutoSkill_17(false);// 關 -> 自動覺醒：巴拉卡斯
                        }
                    } else {
                        _pc.setAutoSkill_16(false);// 關
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz17")) { // 輔助狀態(魔法)開關 ->
                    if (!_pc.isAutoSkill_17()) {
                        _pc.setAutoSkill_17(true);// 開
                        if (_pc.isDragonKnight()) {// 龍騎
                            _pc.setAutoSkill_15(false);// 關 -> 自動覺醒：法利昂
                            _pc.setAutoSkill_16(false);// 關 -> 自動覺醒：安塔瑞斯
                        }
                    } else {
                        _pc.setAutoSkill_17(false);// 關
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz18")) { // 輔助狀態(魔法)開關 ->
                    if (!_pc.isAutoSkill_18()) {
                        _pc.setAutoSkill_18(true);// 開
                        if (_pc.isElf()) {// 精靈
                            _pc.setAutoSkill_3(false); // 關 -> 保護罩
                            _pc.setAutoSkill_16(false);// 關 -> 大地防護
                        }
                    } else {
                        _pc.setAutoSkill_18(false);// 關
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz19")) { // 輔助狀態(魔法)開關 ->
                    if (!_pc.isAutoSkill_19()) {
                        _pc.setAutoSkill_19(true);// 開
                        if (_pc.isElf()) {// 精靈
                            _pc.setAutoSkill_24(false);// 關 -> 能量激發
                        }
                    } else {
                        _pc.setAutoSkill_19(false);// 關
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz20")) { // 輔助狀態(魔法)開關 ->
                    if (!_pc.isAutoSkill_20()) {
                        _pc.setAutoSkill_20(true);// 開
                        if (_pc.isElf()) {// 精靈
                            _pc.setAutoSkill_21(false); // 關 -> 烈炎武器
                            _pc.setAutoSkill_29(false); // 關 -> 風之神射
                            _pc.setAutoSkill_30(false); // 關 -> 暴風之眼
                            _pc.setAutoSkill_31(false); // 關 -> 暴風神射
                        }
                    } else {
                        _pc.setAutoSkill_20(false);// 關
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz21")) { // 輔助狀態(魔法)開關 ->
                    if (!_pc.isAutoSkill_21()) {
                        _pc.setAutoSkill_21(true);// 開
                        if (_pc.isElf()) {// 精靈
                            _pc.setAutoSkill_20(false); // 關 -> 火焰武器
                            _pc.setAutoSkill_29(false); // 關 -> 風之神射
                            _pc.setAutoSkill_30(false); // 關 -> 暴風之眼
                            _pc.setAutoSkill_31(false); // 關 -> 暴風神射
                        }
                    } else {
                        _pc.setAutoSkill_21(false);// 關
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz22")) { // 輔助狀態(魔法)開關 ->
                    if (!_pc.isAutoSkill_22()) {
                        _pc.setAutoSkill_22(true);// 開
                    } else {
                        _pc.setAutoSkill_22(false);// 關
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz23")) { // 輔助狀態(魔法)開關 ->
                    if (!_pc.isAutoSkill_23()) {
                        _pc.setAutoSkill_23(true);// 開
                    } else {
                        _pc.setAutoSkill_23(false);// 關
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz24")) { // 輔助狀態(魔法)開關 ->
                    if (!_pc.isAutoSkill_24()) {
                        _pc.setAutoSkill_24(true);// 開
                        if (_pc.isElf()) {// 精靈
                            _pc.setAutoSkill_19(false);// 關 -> 體能激發
                        }
                    } else {
                        _pc.setAutoSkill_24(false);// 關
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz25")) { // 輔助狀態(魔法)開關 ->
                    if (!_pc.isAutoSkill_25()) {
                        _pc.setAutoSkill_25(true);// 開
                    } else {
                        _pc.setAutoSkill_25(false);// 關
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz26")) { // 輔助狀態(魔法)開關 ->
                    if (!_pc.isAutoSkill_26()) {
                        _pc.setAutoSkill_26(true);// 開
                    } else {
                        _pc.setAutoSkill_26(false);// 關
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz27")) { // 輔助狀態(魔法)開關 ->
                    if (!_pc.isAutoSkill_27()) {
                        _pc.setAutoSkill_27(true);// 開
                    } else {
                        _pc.setAutoSkill_27(false);// 關
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz28")) { // 輔助狀態(魔法)開關 ->
                    if (!_pc.isAutoSkill_28()) {
                        _pc.setAutoSkill_28(true);// 開
                    } else {
                        _pc.setAutoSkill_28(false);// 關
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz29")) { // 輔助狀態(魔法)開關 ->
                    if (!_pc.isAutoSkill_29()) {
                        _pc.setAutoSkill_29(true);// 開
                        if (_pc.isElf()) {// 精靈
                            _pc.setAutoSkill_20(false); // 關 -> 火焰武器
                            _pc.setAutoSkill_21(false); // 關 -> 烈炎武器
                            _pc.setAutoSkill_30(false); // 關 -> 暴風之眼
                            _pc.setAutoSkill_31(false); // 關 -> 暴風神射
                        }
                    } else {
                        _pc.setAutoSkill_29(false);// 關
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz30")) { // 輔助狀態(魔法)開關 ->
                    if (!_pc.isAutoSkill_30()) {
                        _pc.setAutoSkill_30(true);// 開
                        if (_pc.isElf()) {// 精靈
                            _pc.setAutoSkill_20(false); // 關 -> 火焰武器
                            _pc.setAutoSkill_21(false); // 關 -> 烈炎武器
                            _pc.setAutoSkill_29(false); // 關 -> 風之神射
                            _pc.setAutoSkill_31(false); // 關 -> 暴風神射
                        }
                    } else {
                        _pc.setAutoSkill_30(false);// 關
                    }
                    show_ngxtAll(_pc);
                } else if (cmd.equalsIgnoreCase("mfpz31")) { // 輔助狀態(魔法)開關 ->
                    if (!_pc.isAutoSkill_31()) {
                        _pc.setAutoSkill_31(true);// 開
                        if (_pc.isElf()) {// 精靈
                            _pc.setAutoSkill_20(false); // 關 -> 火焰武器
                            _pc.setAutoSkill_21(false); // 關 -> 烈炎武器
                            _pc.setAutoSkill_29(false); // 關 -> 風之神射
                            _pc.setAutoSkill_30(false); // 關 -> 暴風之眼
                        }
                    } else {
                        _pc.setAutoSkill_31(false);// 關
                    }
                    show_ngxtAll(_pc);
                    return;
                }
                // 輔助狀態(魔法) end
                // ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
                // 輔助(自動刪物)
                if (cmd.equalsIgnoreCase("yxsw")) { // 展示輔助(自動刪物)對話檔
                    //_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "yxzdsw")); // yxzdsw輔助(自動刪物)對話檔
                    show_yxzdsw(_pc);
                } else if (cmd.equalsIgnoreCase("swkg")) { // 輔助(自動刪物)開關 -> 目前
                    if (!_pc.isAutoRemoveItem()) {
                        _pc.setAutoRemoveItem(true);// 自動刪物判定 開
                        _pc.startSkillSound_autoRemoveItem();
                        _pc.sendPackets(new S_SystemMessage("\\aE注意：添加或取消每次只能1樣"));
                    } else {
                        //_pc.setAutoRemoveItem(false);// 自動刪物判定 關
                        _pc.stopSkillSound_autoRemoveItem();
                    }
                    show_yxzdsw(_pc);
                } else if (cmd.equalsIgnoreCase("tjsw")) { // 輔助(自動刪物) -> 添加刪物
                    _pc.setTemporary(6);
                    _pc.sendPackets(new S_RemoveItemShopSellList(_pc, _pc.getId()));
                } else if (cmd.equalsIgnoreCase("qxsw")) { // 輔助(自動刪物) -> 取消添加
                    _pc.setTemporary(7);
                    _pc.sendPackets(new S_RemoveItemShopBuyList(_pc, _pc.getId()));
                    return;
                }
                // 輔助(自動刪物) end
            } else {
                // 核心自動輔助未開啟
                if (cmd.equalsIgnoreCase("yxbx")) { // 展示輔助(自動補血)對話檔
                    _pc.sendPackets(new S_SystemMessage("\\aE輔助暫未開啟..."));
                } else if (cmd.equalsIgnoreCase("yxdj")) { // 展示輔助狀態(道具)對話檔
                    _pc.sendPackets(new S_SystemMessage("\\aE輔助暫未開啟..."));
                } else if (cmd.equalsIgnoreCase("yxmf")) { // 展示輔助狀態(魔法)對話檔
                    _pc.sendPackets(new S_SystemMessage("\\aE輔助暫未開啟..."));
                } else if (cmd.equalsIgnoreCase("yxsw")) { // 展示輔助(自動刪物)對話檔
                    _pc.sendPackets(new S_SystemMessage("\\aE輔助暫未開啟..."));
                    return;
                }
            }
            // ■■■■■■■■■■■■■■■■■■■■■■■■■以上核心輔助結束■■■■■■■■■■■■■■■■■■■■■■■■■
            // ■■■■■■■■■■■■■■■■■■■■■■■■■以上核心輔助結束■■■■■■■■■■■■■■■■■■■■■■■■■
            // ■■■■■■■■■■■■■■■■■■■■■■■■■以上核心輔助結束■■■■■■■■■■■■■■■■■■■■■■■■■
            // ■■■■■■■■■■■■■■■■■■■■■■■■■以上核心輔助結束■■■■■■■■■■■■■■■■■■■■■■■■■
            // ■■■■■■■■■■■■■■■■■■■■■■■■■以上核心輔助結束■■■■■■■■■■■■■■■■■■■■■■■■■
            // 轉生天賦
            if (cmd.equalsIgnoreCase("rei_0")) {
                _pc.sendPackets(new S_ReincarnationHtml(_pc));
                return;
            }
            if (cmd.equalsIgnoreCase("rei_1")) {
                if (_pc.getReincarnationSkill()[0] >= ConfigTurn.ReiPointLv_1) {
                    _pc.sendPackets(new S_SystemMessage("此轉生技能已達最大上限等級" + ConfigTurn.ReiPointLv_1 + "，請選擇其它轉生技能。"));
                    return;
                }
                ReincarnationSkill.getInstance().addCheck(_pc, 0);
                return;
            }
            if (cmd.equalsIgnoreCase("rei_2")) {
                if (_pc.getReincarnationSkill()[1] >= ConfigTurn.ReiPointLv_2) {
                    _pc.sendPackets(new S_SystemMessage("此轉生技能已達最大上限等級" + ConfigTurn.ReiPointLv_2 + "，請選擇其它轉生技能。"));
                    return;
                }
                ReincarnationSkill.getInstance().addCheck(_pc, 1);
                return;
            }
            if (cmd.equalsIgnoreCase("rei_3")) {
                if (_pc.getReincarnationSkill()[2] >= ConfigTurn.ReiPointLv_3) {
                    _pc.sendPackets(new S_SystemMessage("此轉生技能已達最大上限等級" + ConfigTurn.ReiPointLv_3 + "，請選擇其它轉生技能。"));
                    return;
                }
                ReincarnationSkill.getInstance().addCheck(_pc, 2);
                return;
            }
            if (cmd.equalsIgnoreCase("rei_reset")) {
                final L1Item reicash = ItemTable.get().getTemplate(ConfigTurn.ReiItemId);
                if (reicash == null) {
                    return;
                }
                if (_pc.getInventory().checkItem(ConfigTurn.ReiItemId, ConfigTurn.ReiItemCount)) {
                    _pc.sendPackets(new S_Message_YN(2761));
                    //_pc.sendPackets(new S_Message_YN(2761, "您確定要將轉生技能點數重置嗎？"));
                } else {
                    _pc.sendPackets(new S_SystemMessage(reicash.getName() + "[" + ConfigTurn.ReiItemCount + "]不足，無法重置。"));
                }
                return;
            }
            // 轉生天賦end
            // 卡冊
            if (CardBookCmd.get().Cmd(_pc, cmd)) {
                return;
            }
            if (CardBookCmd.get().PolyCmd(_pc, cmd)) {
                return;
            }
            if (Npc_PolyCombind.get().Cmd(_pc, cmd)) {
                return;
            }
            // 娃娃卡冊
            if (dollBookCmd.get().Cmd(_pc, cmd)) {
                return;
            }
           // 娃娃隨身呼叫
            if (Npc_DollCombind.Cmd(_pc, cmd)) {
                return;
            }
            // 成就
            if (EquipCollectCmd.get().Cmd(_pc, cmd)) {
                return;
            }
            if (dollBookCmd.get().PolyCmd(_pc, cmd)) {
                return;
            }
            // 聖物
            if (holyBookCmd.get().Cmd(_pc, cmd)) {
                return;
            }
            if (holyBookCmd.get().PolyCmd(_pc, cmd)) {
                return;
            }
            if (Npc_HolyCombind.get().Cmd(_pc, cmd)) {
                return;
            }
            // 簽到
            if (DaySignatureCmd_New.get().Cmd(_pc, cmd)) {
                return;
            }
            // 收藏
            if (collectBookCmd.get().Cmd(_pc, cmd)) {
                return;
            }
            if (WenYangCmd.get().Cmd(_pc, cmd)) {
                return;
            }
            // 火神
            if (FireDisCmd.get().Cmd(_pc, cmd)) {
                return;
            }
            // 領取獎勵
            if (PromotionCmd.get().Cmd(_pc, cmd)) {
                return;
            }
            // 守護星盤
            if (AstrologyCmd.get().Cmd(_pc, cmd)) {

                return;
            }

            if (AttonAstrologyCmd.get().Cmd(_pc, cmd)) {
                return;
            }
            if (com.add.Tsai.Astrology.SilianAstrologyCmd.get().Cmd(_pc, cmd)) {
                return;
            }
            if (com.add.Tsai.Astrology.GritAstrologyCmd.get().Cmd(_pc, cmd)) {
                return;
            }
            if (com.add.Tsai.Astrology.YishidiAstrologyCmd.get().Cmd(_pc, cmd)) {
                return;
            }

            if(Npc_MagicCombind.Cmd(_pc, cmd)) {
                return;
            }

            // 武器熟練度
            if (L1WeaponProficiency.Cmd(_pc, cmd)) {
                return;
            }
            // 任務
            if (Npc_Bao1.Cmd(_pc, cmd)) {
                return;
            }
            // 主線任務進度查詢
            if (ServerQuestMobTable.get().Cmd(_pc, cmd)) {
                return;
            }
            // 技能強化
            if (SkillEnhanceTable.Cmd(_pc, cmd)) {
                return;
            }

            if (Npc_Honor.Cmd(_pc, cmd)) {
                return;
            }
            // 展開變身控制選單
            if (_pc.isShapeChange()) {
                // 解除GM管理狀態
                _pc.get_other().set_gmHtml(null);
                final int awakeSkillId = _pc.getAwakeSkillId();
                if ((awakeSkillId == AWAKEN_ANTHARAS) || (awakeSkillId == AWAKEN_FAFURION) || (awakeSkillId == AWAKEN_VALAKAS)) {
                    // 目前狀態中無法變身。
                    _pc.sendPackets(new S_ServerMessage(1384));
                    return;
                }
                L1PolyMorph.handleCommands(_pc, cmd);
                _pc.setShapeChange(false);
                _pc.setSummonMonster(false);
                return;
            }
            //金幣交易開始
            else if (cmd.equalsIgnoreCase("adena_trade_up")) {
                if (_pc.getPage() <= 0) {
                    _pc.sendPackets(new S_SystemMessage("已經是第一頁了。"));
                    return;
                }
                _pc.setPage(_pc.getPage() - 1);
                ShowAdenaTrade(_pc.getPage());
            } else if (cmd.equalsIgnoreCase("adena_trade_down")) {
                final int listsize = _pc.getAdenaTradeList().size();
                int pageamount = listsize / 10;
                if (listsize % 10 != 0) {
                    pageamount += 1;
                }
                if (_pc.getPage() + 1 >= pageamount) {
                    _pc.sendPackets(new S_SystemMessage("已經是最後一頁了。"));
                    return;
                }
                _pc.setPage(_pc.getPage() + 1);
                ShowAdenaTrade(_pc.getPage());
            } else if (cmd.equalsIgnoreCase("adena_trade_up1")) {
                if (_pc.getPage() <= 0) {
                    _pc.sendPackets(new S_SystemMessage("已經是第一頁了。"));
                    return;
                }
                _pc.setPage(_pc.getPage() - 1);
                ShowAdenaTradePC(_pc.getPage());
            } else if (cmd.equalsIgnoreCase("adena_trade_down1")) {
                final int listsize = _pc.getAdenaTradeList().size();
                int pageamount = listsize / 10;
                if (listsize % 10 != 0) {
                    pageamount += 1;
                }
                if (_pc.getPage() + 1 >= pageamount) {
                    _pc.sendPackets(new S_SystemMessage("已經是最後一頁了。"));
                    return;
                }
                _pc.setPage(_pc.getPage() + 1);
                ShowAdenaTradePC(_pc.getPage());
            } else if (cmd.startsWith("_")) {
                adenaTrade(cmd, amount);
            }
            //金幣買賣結束
            //角色買賣系統
            else if (cmd.startsWith("characterTrade_")) {
                CharaterTrade(cmd, amount);
            } else if (cmd.startsWith("bindcharIndex")) {
                bindTradeChar(cmd);
            } else if (cmd.startsWith("rebindchar")) {
                rebindTradeChar();
            } else if (cmd.startsWith("pasea")) {
                final String[] data = new String[6];
                if (_pc.getPaCha() > 0) {
                    final String pacha = String.valueOf((int) (_pc.getPaCha()));
                    data[0] = pacha;
                }
                if (_pc.getPaCon() > 0) {
                    final String pacon = String.valueOf((int) (_pc.getPaCon()));
                    data[1] = pacon;
                }
                if (_pc.getPaDex() > 0) {
                    final String padex = String.valueOf((int) (_pc.getPaDex()));
                    data[2] = padex;
                }
                if (_pc.getPaInt() > 0) {
                    final String paint = String.valueOf((int) (_pc.getPaInt()));
                    data[3] = paint;
                }
                if (_pc.getPaStr() > 0) {
                    final String pastr = String.valueOf((int) (_pc.getPaStr()));
                    data[4] = pastr;
                }
                if (_pc.getPaWis() > 0) {
                    final String pawis = String.valueOf((int) (_pc.getPaWis()));
                    data[5] = pawis;
                }
                _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "wnchaxun", data));
            } else if (cmd.startsWith("zuduiopen")) {
                if (_pc.is_zudui()) {
                    _pc.rezudui(false);
                    _pc.sendPackets(new S_ServerMessage("組隊掉落訊息已關閉"));
                } else {
                    _pc.rezudui(true);
                    _pc.sendPackets(new S_ServerMessage("組隊掉落訊息已開啟"));
                }
            } else if (cmd.startsWith("vipchaxun")) {
                final String[] data = new String[1];
                if (_pc.getVipLevel() != 0) {
                    final String viplevel = String.valueOf((int) (_pc.getVipLevel()));
                    data[0] = viplevel;
                }
                _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "vipchaxun", data));
            }
            //角色買賣系統結束
            if (_pc.isItemPoly()) {// 是否使用道具變身
                L1ItemInstance item = _pc.getPolyScroll();// 取回自訂變身卷軸道具
                L1PolyMorph poly = PolyTable.get().getTemplate(cmd);
                if ((poly != null) || cmd.equals("none")) {
                    if (item.getItemId() == 44212) {// 神秘的魔法變身書
                        usePolyBook(_pc, item, cmd);
                        return;
                    } else {
                        usePolyScroll(_pc, item, cmd);
                        return;
                    }
                }
            }
            //是否為內掛指令
            if (AutoAttackUpdate.getInstance().PcCommand(_pc, cmd)) {
                return;
            }
            if (_pc.isPhantomTeleport()) {// 是否正在使用幻象的傲慢之塔移動傳送符
                usePhantomTeleport(_pc, cmd);
                return;
            }
            // GM選單不為空
            if (_pc.get_other().get_gmHtml() != null) {
                _pc.get_other().get_gmHtml().action(cmd);
                return;
            }
            // 解除GM管理狀態
            _pc.get_other().set_gmHtml(null);
            // GM跟隨
            if (this._pc.isGm()) {
                if (cmd.equals("tp_refresh")) {
                    L1ToPC.checkTPhtmlPredicate(this._pc, 0, true);
                } else if (cmd.equals("tp_refresh_map")) {
                    L1ToPC.checkTPhtmlPredicate(this._pc, 0, false);
                } else if (cmd.equals("tp_page_up")) {
                    L1ToPC.checkTPhtml(this._pc, this._pc.get_other().get_page() - 1);
                } else if (cmd.equals("tp_page_down")) {
                    L1ToPC.checkTPhtml(this._pc, this._pc.get_other().get_page() + 1);
                } else if (cmd.matches("tp_[0-9]+")) {
                    int index = Integer.parseInt(cmd.substring(3));
                    L1ToPC.teleport2Player(this._pc, index);
                }
            }
            // 任務選單 FIXME
            if (cmd.equalsIgnoreCase("power")) {// 能力選取視窗
                // 判斷是否出現能力選取視窗
                if (_pc.power()) {
                    //_pc.sendPackets(new S_Bonusstats(_pc.getId()));
                }
                /*
                 * } else if (cmd.equalsIgnoreCase("shop")) {// 道具商城
                 * _pc.sendPackets(new S_ShopSellListCnX(_pc, _pc.getId()));
                 */
            } else if (cmd.equalsIgnoreCase("index")) {// 任務查詢系統
                _pc.isWindows();
            } else if (cmd.equalsIgnoreCase("locerr1")) {// 解除人物卡點
                if (_pc.getHellTime() > 0) {
                    _pc.sendPackets(new S_SystemMessage("在地獄中無法使用此功能"));
                    return;
                }
                if (!_pc.getMap().isEscapable()) {
                    _pc.sendPackets(new S_SystemMessage("此地圖中無法使用此功能"));
                    return;
                }
                _pc.set_unfreezingTime(10);// 延遲10秒
            } else if (cmd.equalsIgnoreCase("locerr2")) {// 修正人物錯位
                _pc.set_misslocTime(5);// 延遲5秒
            } else if (cmd.equalsIgnoreCase("qt")) {// 查看執行中任務
                showStartQuest(_pc, _pc.getId());
            } else if (cmd.equalsIgnoreCase("quest")) {// 查看可執行任務
                showQuest(_pc, _pc.getId());
            } else if (cmd.equalsIgnoreCase("questa")) {// 查看全部任務
                showQuestAll(_pc, _pc.getId());
            } else if (cmd.equalsIgnoreCase("i")) {// 任務介紹
                final L1Quest quest = QuestTable.get().getTemplate(_pc.getTempID());
                _pc.setTempID(0);
                // 確認該任務存在
                if (quest == null) {
                    return;
                }
                QuestClass.get().showQuest(_pc, quest.get_id());
            } else if (cmd.equalsIgnoreCase("d")) {// 任務回收
                final L1Quest quest = QuestTable.get().getTemplate(_pc.getTempID());
                _pc.setTempID(0);
                // 確認該任務存在
                if (quest == null) {
                    return;
                }
                // 任務已經完成
                if (_pc.getQuest().isEnd(quest.get_id())) {
                    questDel(quest);
                    return;
                }
                // 任務尚未開始
                if (!_pc.getQuest().isStart(quest.get_id())) {
                    // 很抱歉!!你並未開始執行這個任務!
                    _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "y_q_not6"));
                    return;
                }
                // 執行中 未完成任務
                questDel(quest);
            } else if (cmd.equalsIgnoreCase("dy")) {// 任務移除
                final L1Quest quest = QuestTable.get().getTemplate(_pc.getTempID());
                _pc.setTempID(0);
                // 確認該任務存在
                if (quest == null) {
                    return;
                }
                // 任務已經完成
                if (_pc.getQuest().isEnd(quest.get_id())) {
                    isDel(quest);
                    return;
                }
                // 任務尚未開始
                if (!_pc.getQuest().isStart(quest.get_id())) {
                    // 很抱歉!!你並未開始執行這個任務!
                    _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "y_q_not6"));
                    return;
                }
                // 執行中 未完成任務
                isDel(quest);
            } else if (cmd.equalsIgnoreCase("up")) {// 上一頁(管理)
                final int page = _pc.get_other().get_page() - 1;
                final L1ActionShowHtml show = new L1ActionShowHtml(_pc);
                show.showQuestMap(page);
            } else if (cmd.equalsIgnoreCase("dn")) {// 下一頁(管理)
                final int page = _pc.get_other().get_page() + 1;
                final L1ActionShowHtml show = new L1ActionShowHtml(_pc);
                show.showQuestMap(page);
            } else if (cmd.equalsIgnoreCase("q0")) {// 頁面內指定位置
                final int key = (_pc.get_other().get_page() * 10);
                showPage(key);
            } else if (cmd.equalsIgnoreCase("q1")) {// 頁面內指定位置
                final int key = (_pc.get_other().get_page() * 10) + 1;
                showPage(key);
            } else if (cmd.equalsIgnoreCase("q2")) {// 頁面內指定位置
                final int key = (_pc.get_other().get_page() * 10) + 2;
                showPage(key);
            } else if (cmd.equalsIgnoreCase("q3")) {// 頁面內指定位置
                final int key = (_pc.get_other().get_page() * 10) + 3;
                showPage(key);
            } else if (cmd.equalsIgnoreCase("q4")) {// 頁面內指定位置
                final int key = (_pc.get_other().get_page() * 10) + 4;
                showPage(key);
            } else if (cmd.equalsIgnoreCase("q5")) {// 頁面內指定位置
                final int key = (_pc.get_other().get_page() * 10) + 5;
                showPage(key);
            } else if (cmd.equalsIgnoreCase("q6")) {// 頁面內指定位置
                final int key = (_pc.get_other().get_page() * 10) + 6;
                showPage(key);
            } else if (cmd.equalsIgnoreCase("q7")) {// 頁面內指定位置
                final int key = (_pc.get_other().get_page() * 10) + 7;
                showPage(key);
            } else if (cmd.equalsIgnoreCase("q8")) {// 頁面內指定位置
                final int key = (_pc.get_other().get_page() * 10) + 8;
                showPage(key);
            } else if (cmd.equalsIgnoreCase("q9")) {// 頁面內指定位置
                final int key = (_pc.get_other().get_page() * 10) + 9;
                showPage(key);
            }
            // 開關 FIXME
            if (cmd.equalsIgnoreCase("attackheyes")) {//
                _pc.setattackhe(true);
                this._pc.sendPackets(new S_SystemMessage("" + "武器特效開啟。"));
                //	showCulture2(_pc);
            } else if (cmd.equalsIgnoreCase("attackheno")) {//
                _pc.setattackhe(false);
                this._pc.sendPackets(new S_SystemMessage("\\aG關閉武器特效。"));
                //	showCulture2(_pc);
            }
            if (cmd.equalsIgnoreCase("armorheyes")) {//
                _pc.setarmorhe(true);
                this._pc.sendPackets(new S_SystemMessage("\\aG裝備特效開啟。"));
                //	showCulture2(_pc);
            } else if (cmd.equalsIgnoreCase("armorheno")) {//
                _pc.setarmorhe(false);
                this._pc.sendPackets(new S_SystemMessage("\\aG關閉裝備特效。"));
                //	showCulture2(_pc);
            }
            if (cmd.equalsIgnoreCase("droplistyes")) {//
                _pc.setdroplist(true);
                this._pc.sendPackets(new S_SystemMessage("\\aG開啟掉落寶物公告。"));
                //	showCulture2(_pc);
            } else if (cmd.equalsIgnoreCase("droplistno")) {//
                _pc.setdroplist(false);
                this._pc.sendPackets(new S_SystemMessage("\\aG關閉掉落寶物公告。"));
                //	showCulture2(_pc);
            }
            if (cmd.equalsIgnoreCase("killyes")) {//
                _pc.setkill(true);
                this._pc.sendPackets(new S_SystemMessage("\\aG開啟殺人公告。"));
                //	showCulture2(_pc);
            } else if (cmd.equalsIgnoreCase("killno")) {//
                _pc.setkill(false);
                this._pc.sendPackets(new S_SystemMessage("\\aG關閉殺人公告。"));
                //	showCulture2(_pc);
            } else if (cmd.equalsIgnoreCase("Status11")) { // 檢查個人狀態
                showStatus11(_pc);
            } else if (cmd.equalsIgnoreCase("bossseach")) {
                _pc.setBossSeachPage(0);
                BossShowPage(_pc.getBossSeachPage());
            } else if (cmd.startsWith("bo_")) {
                int bossId = Integer.parseInt(cmd.substring(3));
                TeleportBoss(_pc, bossId);
            }
            //			else if (cmd.equalsIgnoreCase("uppu")) {
            //				int v = _pc.getBossSeachPage();
            //				if (v <= 0) {
            //					_pc.sendPackets(new S_SystemMessage("已經到頁首了喔"));
            //					return;
            //				}
            //				_pc.setBossSeachPage(_pc.getBossSeachPage() - 1);
            //				BossShowPage(_pc.getBossSeachPage());
            //			} else if (cmd.equalsIgnoreCase("downdo")) {
            //				_pc.setBossSeachPage(_pc.getBossSeachPage() + 1);
            //				BossShowPage(_pc.getBossSeachPage());
            //			}
            if (cmd.equalsIgnoreCase("War_Part")) {// 邀請屏幕內所有盟友組隊
                if (_pc.isInParty()) {
                    if (!_pc.getParty().isLeader(_pc)) {
                        // 只有領導者才能邀請其他的成員。
                        _pc.sendPackets(new S_ServerMessage(416));
                        return;
                    }
                }
                if (_pc.getClanid() == 0) {
                    _pc.sendPackets(new S_SystemMessage("您還沒有血盟哦,快去加入一個血盟吧"));
                    return;
                }
                _pc.sendPackets(new S_SystemMessage("屏幕血盟組隊邀請已發送!等待入隊..."));
                for (final L1Object obj : World.get().getVisibleObjects(_pc)) {
                    if (obj instanceof L1PcInstance) {
                        final L1PcInstance tgpc = (L1PcInstance) obj;
                        if (tgpc.isGhost()) { // 鬼魂模式
                            continue;
                        }
                        if (tgpc.isDead()) { // 死亡
                            continue;
                        }
                        if (tgpc.isTeleport()) { // 傳送中
                            continue;
                        }
                        if (tgpc.getClanid() == 0) { // 沒加入血盟
                            continue;
                        }
                        if (tgpc.getClanid() != _pc.getClanid()) { // 不屬於一個血盟
                            continue;
                        }
                        if (tgpc.isPrivateShop()) {
                            continue;
                        }
                        if (tgpc.isInParty()) {
                            // 避免當前玩家沒有隊伍時呼叫 _pc.getParty() 造成 NPE
                            if (_pc.isInParty() && _pc.getParty() != null && _pc.getParty().isMember(tgpc)) { // 是自己的隊員
                                continue;
                            } else {
                                _pc.sendPackets(new S_SystemMessage(tgpc.getName() + " 已在其他隊伍中"));
                                continue;
                            }
                        }
                        tgpc.setPartyID(_pc.getId());
                        // 玩家 %0%s 邀請您加入隊伍？(Y/N)
                        tgpc.sendPackets(new S_Message_YN(953, _pc.getName()));
                    }
                }
            } else if (cmd.equalsIgnoreCase("ClanGfx")) {
                if (_pc.isClanGfx() == false) {
                    _pc.set_isClanGfx(true);
                    _pc.sendPackets(new S_SystemMessage("血盟圖示已開啟"));
                    _pc.sendPackets(new S_PacketBox(S_PacketBox.PLEDGE_EMBLEM_STATUS, 1));
                    // 與 381 一樣：打開全血盟注視清單，讓客戶端顯示所有血盟徽章
                    _pc.sendPackets(new S_ClanMarkSee(2));
                    
                    // 為所有可見的有血盟玩家發送盟徽
                    for (L1Object obj : _pc.getKnownObjects()) {
                        if (obj instanceof L1PcInstance) {
                            L1PcInstance target = (L1PcInstance) obj;
                            // 先重送對象的物件封包，讓客戶端拿到正確的 EmblemId 並觸發下載
                            _pc.sendPackets(new S_OtherCharPacks(target));
                            if (target.getClanid() > 0) {
                                L1Clan targetClan = target.getClan();
                                if (targetClan != null) {
                                    L1EmblemIcon emblemIcon = ClanEmblemReading.get().get(targetClan.getClanId());
                                    if (emblemIcon != null) {
                                        _pc.sendPackets(new S_Emblem(targetClan.getClanId()));
                                    }
                                }
                            }
                        }
                    }
                } else {
                    _pc.set_isClanGfx(false);
                    _pc.sendPackets(new S_SystemMessage("血盟圖示已關閉"));
                    _pc.sendPackets(new S_PacketBox(S_PacketBox.PLEDGE_EMBLEM_STATUS, 0));
                    // 關閉注視
                    _pc.sendPackets(new S_ClanMarkSee());
                }
                return;
            } else if (cmd.equalsIgnoreCase("War1_Part")) {// 邀請屏幕內所有盟友組隊
                if (_pc.isInParty()) {
                    if (!_pc.getParty().isLeader(_pc)) {
                        // 只有領導者才能邀請其他的成員。
                        _pc.sendPackets(new S_ServerMessage(416));
                        return;
                    }
                }
                L1WilliamHonor Honor1 = Honor.getInstance().getTemplate(_pc.getHonorLevel() + 1);
                if (Honor1 != null) {
                    int next_lv_honor1 = Honor1.getHonorMax() - _pc.getHonor();//升到下級所需聲望
                    if (next_lv_honor1 <= 0) {
                        _pc.setHonor(_pc.getHonor());
                        _pc.sendPackets(new S_SystemMessage("你的聲望階級升級了，請重新登入獲取新的能力。"));
                        _pc.sendPackets(new S_SystemMessage("重登前切記把寵物收回唷。"));
                        _pc.save();
                    } else {
                        _pc.sendPackets(new S_SystemMessage("聲望值還需要「" + next_lv_honor1 + "」點才能升級聲望階級。"));
                    }
                } else {
                    _pc.sendPackets(new S_SystemMessage("你的聲望階級已經達到最高等級不能再升級了。"));
                }
                _pc.sendPackets(new S_CloseList(_pc.getId()));
            } else if (cmd.equalsIgnoreCase("Honor_Q")) {
                _pc.sendPackets(new S_ServerMessage("目前威望為：" + _pc.getHonor() + " 。"));//"\\fV目前累積儲值金額"+_pc.getpaycount()+"元"20170122
                _pc.sendPackets(new S_SystemMessage("屏幕組隊邀請已發送!等待入隊..."));
                for (final L1Object obj : World.get().getVisibleObjects(_pc)) {
                    if (obj instanceof L1PcInstance) {
                        final L1PcInstance tgpc = (L1PcInstance) obj;
                        if (tgpc.isGhost()) { // 鬼魂模式
                            continue;
                        }
                        if (tgpc.isDead()) { // 死亡
                            continue;
                        }
                        if (tgpc.isTeleport()) { // 傳送中
                            continue;
                        }
                        if (tgpc.isPrivateShop()) {
                            continue;
                        }
                        if (tgpc.isInParty()) {
                            // 避免當前玩家沒有隊伍時呼叫 _pc.getParty() 造成 NPE
                            if (_pc.isInParty() && _pc.getParty() != null && _pc.getParty().isMember(tgpc)) { // 是自己的隊員
                                continue;
                            } else {
                                _pc.sendPackets(new S_SystemMessage(tgpc.getName() + " 已在其他隊伍中"));
                                continue;
                            }
                        }
                        tgpc.setPartyID(_pc.getId());
                        // 玩家 %0%s 邀請您加入隊伍？(Y/N)
                        tgpc.sendPackets(new S_Message_YN(953, _pc.getName()));
                    }
                }
                return;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private Boolean IsBossSpawn(L1Spawn spawn) {
        long existTime = (long) spawn.get_existTime() * 60 * 1000;
        long spawnTime = spawn.get_nextSpawnTime().getTimeInMillis();
        long nowTime = System.currentTimeMillis();
        return !(existTime + spawnTime > nowTime);
    }

    private void BossShowPage(int key) {
        try {
            final StringBuilder s = new StringBuilder();
            for (int i = 1; i <= SpawnBossTable.get().BossSeachSize(); i++) {
                L1Spawn spawn = SpawnBossTable.get().getBossSeach(i);
                if (spawn != null) {
                    String name = spawn.getName();
                    long spawnTime = spawn.get_nextSpawnTime().getTimeInMillis();
                    String status = "";
                    if (!IsBossSpawn(spawn)) {
                        final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
                        final String fm = sdf.format(spawnTime);
                        status = "狀態:" + fm;
                    } else {
                        status = "狀態:(存活中)";
                    }
                    s.append(name).append(status).append(",");
                    //*			s.append(".........................,");
                }
            }
            final String[] info = s.toString().split(",");
            _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "bosspage", info));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void TeleportBoss(L1PcInstance pc, int bossId) {
        L1Spawn spawn = SpawnBossTable.get().getBossSeach(bossId);
        if (!IsBossSpawn(spawn)) {
            _pc.sendPackets(new S_SystemMessage("尚未重生"));
            return;
        }
        if (!pc.getInventory().consumeItem(ConfigOther.BOSS_MATERIAL, ConfigOther.BOSS_COUNT)) {
            _pc.sendPackets(new S_SystemMessage("材料不足"));
            return;
        }
        L1Teleport.teleport(pc, spawn.getLocX(), spawn.getLocY(), spawn.getMapId(), pc.getHeading(), true);
    }

    /**
     * 使用自訂變身卷軸
     *
     */
    private void usePolyScroll(L1PcInstance pc, L1ItemInstance item, String s) {
        try {
            L1PolyMorph poly = PolyTable.get().getTemplate(s);
            int time = 1800;
            if (item.getBless() == 0) {
                time = 2100;
            }
            if (item.getBless() == 128) {
                time = 2100;
            }
            boolean isUseItem = false;
            if (s.equals("none")) {
                if (pc.getTempCharGfx() == 6034 || pc.getTempCharGfx() == 6035) {
                    isUseItem = true;
                } else {
                    L1PolyMorph.undoPoly(pc);
                    isUseItem = true;
                }
            } else if (poly.getMinLevel() <= pc.getLevel() || pc.isGm()) {// 符合等級限制或是GM
                if ((poly.getPolyId() == 13715) && (pc.get_sex() != 0 || !pc.isCrown())) {// 不符合變身真王子條件
                    isUseItem = false;
                } else if ((poly.getPolyId() == 13717) && (pc.get_sex() != 1 || !pc.isCrown())) {// 不符合變身真公主條件
                    isUseItem = false;
                } else if ((poly.getPolyId() == 13719) && (pc.get_sex() != 0 || !pc.isKnight())) {// 不符合變身真騎士條件
                    isUseItem = false;
                } else if ((poly.getPolyId() == 13721) && (pc.get_sex() != 1 || !pc.isKnight())) {// 不符合變身真女騎士條件
                    isUseItem = false;
                } else if ((poly.getPolyId() == 13723) && (pc.get_sex() != 0 || !pc.isElf())) {// 不符合變身真妖精條件
                    isUseItem = false;
                } else if ((poly.getPolyId() == 13725) && (pc.get_sex() != 1 || !pc.isElf())) {// 不符合變身真女妖精條件
                    isUseItem = false;
                } else if ((poly.getPolyId() == 13727) && (pc.get_sex() != 0 || !pc.isWizard())) {// 不符合變身真法師條件
                    isUseItem = false;
                } else if ((poly.getPolyId() == 13729) && (pc.get_sex() != 1 || !pc.isWizard())) {// 不符合變身真女法師條件
                    isUseItem = false;
                } else if ((poly.getPolyId() == 13731) && (pc.get_sex() != 0 || !pc.isDarkelf())) {// 不符合變身真黑妖條件
                    isUseItem = false;
                } else if ((poly.getPolyId() == 13733) && (pc.get_sex() != 1 || !pc.isDarkelf())) {// 不符合變身真女黑妖條件
                    isUseItem = false;
                } else if ((poly.getPolyId() == 13735) && (pc.get_sex() != 0 || !pc.isDragonKnight())) {// 不符合變身真龍騎條件
                    isUseItem = false;
                } else if ((poly.getPolyId() == 13737) && (pc.get_sex() != 1 || !pc.isDragonKnight())) {// 不符合變身真女龍騎條件
                    isUseItem = false;
                } else if ((poly.getPolyId() == 13739) && (pc.get_sex() != 0 || !pc.isIllusionist())) {// 不符合變身真幻術條件
                    isUseItem = false;
                } else if ((poly.getPolyId() == 13741) && (pc.get_sex() != 1 || !pc.isIllusionist())) {// 不符合變身真女幻術條件
                    isUseItem = false;
                } else {// 符合所有變身條件
                    // 執行變身
                    L1PolyMorph.doPoly(pc, poly.getPolyId(), time, L1PolyMorph.MORPH_BY_ITEMMAGIC);
                    isUseItem = true;
                }
            }
            if (isUseItem) {
                pc.getInventory().removeItem(item, 1);// 刪除道具
                pc.sendPackets(new S_CloseList(pc.getId()));
            } else {
                pc.sendPackets(new S_ServerMessage(181)); // 181:\f1無法變成你指定的怪物。
                pc.sendPackets(new S_CloseList(pc.getId()));
            }
            pc.setItemPoly(false);
            pc.setPolyScroll(null);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);

        }
    }

    /**
     * 使用神秘的魔法變身書
     *
     */
    private void usePolyBook(L1PcInstance pc, L1ItemInstance item, String s) {
        try {
            L1PolyMorph poly = PolyTable.get().getTemplate(s);
            int time = 1800;
            if (item.getBless() == 0) {
                time = 2100;
            }
            if (item.getBless() == 128) {
                time = 2100;
            }
            boolean isUseItem = false;
            if (s.equals("none")) {
                if (pc.getTempCharGfx() == 6034 || pc.getTempCharGfx() == 6035) {
                    isUseItem = true;
                } else {
                    L1PolyMorph.undoPoly(pc);
                    isUseItem = true;
                }
            } else if ((poly.getMinLevel() <= pc.getLevel() || item.getItemId() == 44212) || pc.isGm()) {
                // 執行變身
                L1PolyMorph.doPoly(pc, poly.getPolyId(), time, L1PolyMorph.MORPH_BY_ITEMMAGIC);
                isUseItem = true;
            }
            if (isUseItem) {
                pc.sendPackets(new S_CloseList(pc.getId()));
            } else {
                pc.sendPackets(new S_ServerMessage(181)); // \f1變身。
                pc.sendPackets(new S_CloseList(pc.getId()));
            }
            pc.setItemPoly(false);
            pc.setPolyScroll(null);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 使用幻象的傲慢之塔傳送符進行移動
     *
     */
    private void usePhantomTeleport(L1PcInstance pc, String cmd) {
        try {
            int x = 0;
            int y = 0;
            short mapid = 0;
            switch (cmd) {
                case "a":
                    x = 32797;
                    y = 32799;
                    mapid = 3301;
                    break;
                case "b":
                    x = 32797;
                    y = 32799;
                    mapid = 3302;
                    break;
                case "c":
                    x = 32797;
                    y = 32799;
                    mapid = 3303;
                    break;
                case "d":
                    x = 32668;
                    y = 32864;
                    mapid = 3304;
                    break;
                case "e":
                    x = 32668;
                    y = 32864;
                    mapid = 3305;
                    break;
                case "f":
                    x = 32717;
                    y = 32871;
                    mapid = 3306;
                    break;
                case "g":
                    x = 32668;
                    y = 32864;
                    mapid = 3307;
                    break;
                case "h":
                    x = 32668;
                    y = 32864;
                    mapid = 3308;
                    break;
                case "i":
                    x = 32668;
                    y = 32864;
                    mapid = 3309;
                    break;
                case "j":
                    x = 32797;
                    y = 32799;
                    mapid = 3310;
                    break;
                case "k":
                    x = 32760;
                    y = 32894;
                    mapid = 7100;
                    break;
                case "l":
                    x = 32692;
                    y = 32903;
                    mapid = 7100;
                    break;
            }
            L1Teleport.teleport(pc, x, y, mapid, pc.getHeading(), true);
            pc.setPhantomTeleport(false);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 任務解除執行
     *
     */
    private void questDel(final L1Quest quest) {
        try {
            if (quest.is_del()) {
                _pc.setTempID(quest.get_id());
                String over = null;
                // 該任務完成
                if (_pc.getQuest().isEnd(quest.get_id())) {
                    over = "完成任務";// 完成任務!
                } else {
                    over = _pc.getQuest().get_step(quest.get_id()) + " / " + quest.get_difficulty();
                }
                final String[] info = new String[]{quest.get_questname(), // 任務名稱
                        Integer.toString(quest.get_questlevel()), // 任務等級
                        over, // 任務進度
                        // 額外說明
                };
                _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "y_qi2", info));
            } else {
                // 任務不可刪除
                _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "y_q_not5"));
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 確定解除任務執行
     *
     */
    private void isDel(final L1Quest quest) {
        try {
            if (quest.is_del()) {
                // 任務終止
                QuestClass.get().stopQuest(_pc, quest.get_id());
                CharacterQuestReading.get().delQuest(_pc.getId(), quest.get_id());
                final String[] info = new String[]{quest.get_questname(), // 任務名稱
                        Integer.toString(quest.get_questlevel()), // 任務等級
                };
                // 刪除任務
                _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "y_qi3", info));
            } else {
                // 任務不可刪除
                _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "y_q_not5"));
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     */
    private void adenaTrade(final String cmd, final long amount) {
        final int type = Integer.parseInt(cmd.substring(12));
        if (type == 1) {// 輸入金幣數量
            final L1ItemInstance adenaItem = _pc.getInventory().findItemId(40308);
            if (adenaItem == null || adenaItem.getCount() < 100000) {
                _pc.sendPackets(new S_SystemMessage("\\aD金幣不足10W"));
                _pc.sendPackets(new S_CloseList(_pc.getId()));
                return;
            }
            _pc.sendPackets(new S_HowManyMake(_pc.getId(), 100000, adenaItem.getCount(), "adena_trade_2", "adenatrade1"));
        } else if (type == 2) {// 確認金幣數量 輸入元寶數量
            System.out.println("11111");
            if (amount <= 0) {
                _pc.sendPackets(new S_CloseList(_pc.getId()));
                return;
            }
            if (amount > 2000000000) {
                _pc.sendPackets(new S_CloseList(_pc.getId()));
                return;
            }
            final L1ItemInstance adenaItem = _pc.getInventory().findItemId(40308);
            if (adenaItem == null || adenaItem.getCount() < amount) {
                _pc.sendPackets(new S_SystemMessage("\\aD金幣不足。"));
                _pc.sendPackets(new S_CloseList(_pc.getId()));
                return;
            }
            _pc.setAdenaTradeCount(amount);
            _pc.sendPackets(new S_HowManyMake(_pc.getId(), 1, 2000000000, "adena_trade_3", "adenatrade2"));
        } else if (type == 3) {// 確認元寶數量
            final int adena_count = (int) _pc.getAdenaTradeCount();
            if (amount <= 0) {
                _pc.sendPackets(new S_CloseList(_pc.getId()));
                return;
            }
            if (amount > 2000000000) {
                _pc.sendPackets(new S_CloseList(_pc.getId()));
                return;
            }
            if (adena_count < 100000) {
                _pc.sendPackets(new S_CloseList(_pc.getId()));
                return;
            }
            final L1ItemInstance adenaItem = _pc.getInventory().findItemId(40308);
            if (adenaItem == null || adenaItem.getCount() < adena_count) {
                _pc.sendPackets(new S_SystemMessage("\\aD金幣不足。"));
                _pc.sendPackets(new S_CloseList(_pc.getId()));
                return;
            }
            _pc.setAdenaTradeAmount(amount);
            _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "adenatrade3", new String[]{String.valueOf(adena_count), String.valueOf(amount)}));
        } else if (type == 4) {// 賣金幣 最終確認
            final int adena_count = (int) _pc.getAdenaTradeCount();
            final int adena_amount = (int) _pc.getAdenaTradeAmount();
            if (adena_count < 100000 || adena_amount <= 0) {
                return;
            }
            _pc.setAdenaTradeCount(0);
            _pc.setAdenaTradeAmount(0);
            final L1ItemInstance adenaItem = _pc.getInventory().findItemId(40308);
            if (adenaItem == null || adenaItem.getCount() < adena_count) {
                _pc.sendPackets(new S_SystemMessage("\\aD金幣不足。"));
                _pc.sendPackets(new S_CloseList(_pc.getId()));
                return;
            }
            final L1CharacterAdenaTrade adenaTrade = new L1CharacterAdenaTrade();
            adenaTrade.set_Id(CharacterAdenaTradeReading.get().nextId());
            adenaTrade.set_adena_count(adena_count);
            adenaTrade.set_over(0);
            adenaTrade.set_count(adena_amount);
            adenaTrade.set_objid(_pc.getId());
            adenaTrade.set_name(_pc.getName());
            if (_pc.getInventory().removeItem(adenaItem, adena_count) >= adena_count) {
                if (CharacterAdenaTradeReading.get().createAdenaTrade(adenaTrade)) {
                    _pc.sendPackets(new S_SystemMessage("\\aD掛賣成功。"));
                } else {
                    _pc.sendPackets(new S_SystemMessage("\\aD掛賣失敗。"));
                }
            }
            _pc.sendPackets(new S_CloseList(_pc.getId()));
        } else if (type == 5) {// 確認購買
            final int adenaTrade_id = _pc.getAdenaTradeId();
            final Map<Integer, L1CharacterAdenaTrade> adenaTeadeMap = CharacterAdenaTradeReading.get().getAdenaTrades();
            synchronized (adenaTeadeMap) {
                final L1CharacterAdenaTrade adenaTrade = adenaTeadeMap.get(adenaTrade_id);
                if (adenaTrade == null) {
                    _pc.sendPackets(new S_CloseList(_pc.getId()));
                    return;
                }
                if (adenaTrade.get_over() != 0) {
                    if (adenaTrade.get_over() == 1) {
                        _pc.sendPackets(new S_SystemMessage("\\aD該單已出售。"));
                    } else if (adenaTrade.get_over() == 2) {
                        _pc.sendPackets(new S_SystemMessage("\\aD該單已撤銷。"));
                    } else if (adenaTrade.get_over() == 3) {
                        _pc.sendPackets(new S_SystemMessage("\\aD該單已出售。"));
                    }
                    _pc.sendPackets(new S_CloseList(_pc.getId()));
                    return;
                }
                if (!_pc.getInventory().checkItem(44070, adenaTrade.get_count())) {
                    _pc.sendPackets(new S_SystemMessage("\\F2貨幣不足。"));
                    _pc.sendPackets(new S_CloseList(_pc.getId()));
                    return;
                }
                if (_pc.getInventory().consumeItem(44070, adenaTrade.get_count())) {
                    if (CharacterAdenaTradeReading.get().updateAdenaTrade(adenaTrade_id, 1)) {
                        _pc.getInventory().storeItem(40308, adenaTrade.get_adena_count());
                        _pc.sendPackets(new S_SystemMessage(String.format("\\F2獲得金幣(%d)", adenaTrade.get_adena_count())));
                        _pc.sendPackets(new S_CloseList(_pc.getId()));
                    }
                }
            }
        } else if (type == 6) {// 查看自己的掛賣清單
            _pc.getAdenaTradeList().clear();
            for (final L1CharacterAdenaTrade adenaTrade : CharacterAdenaTradeReading.get().getAllCharacterAdenaTrades()) {
                if (adenaTrade.get_objid() == _pc.getId()) {
                    if (adenaTrade.get_over() == 0 || adenaTrade.get_over() == 1) {
                        _pc.addAdenaTradeItem(adenaTrade);
                    }
                }
            }
            if (_pc.getAdenaTradeList().size() <= 0) {
                _pc.sendPackets(new S_SystemMessage("\\F2你沒有掛賣。"));
                _pc.sendPackets(new S_CloseList(_pc.getId()));
                return;
            }
            _pc.setPage(0);
            ShowAdenaTradePC(0);
        } else if (type == 7) {// 撤銷
            final int adenaTrade_id = _pc.getAdenaTradeId();
            final Map<Integer, L1CharacterAdenaTrade> adenaTeadeMap = CharacterAdenaTradeReading.get().getAdenaTrades();
            synchronized (adenaTeadeMap) {
                final L1CharacterAdenaTrade adenaTrade = adenaTeadeMap.get(adenaTrade_id);
                if (adenaTrade == null) {
                    _pc.sendPackets(new S_CloseList(_pc.getId()));
                    return;
                }
                if (adenaTrade.get_objid() != _pc.getId()) {
                    _pc.sendPackets(new S_CloseList(_pc.getId()));
                    return;
                }
                if (adenaTrade.get_over() != 0) {
                    if (adenaTrade.get_over() == 1) {
                        _pc.sendPackets(new S_SystemMessage("\\aD該單已出售。"));
                    } else if (adenaTrade.get_over() == 2) {
                        _pc.sendPackets(new S_SystemMessage("\\aD該單已撤銷。"));
                    } else if (adenaTrade.get_over() == 3) {
                        _pc.sendPackets(new S_SystemMessage("\\aD該單已提取。"));
                    }
                    _pc.sendPackets(new S_CloseList(_pc.getId()));
                    return;
                }
                if (CharacterAdenaTradeReading.get().updateAdenaTrade(adenaTrade_id, 2)) {
                    _pc.getInventory().storeItem(40308, adenaTrade.get_adena_count());
                    _pc.sendPackets(new S_SystemMessage(String.format("\\aD撤銷成功獲得金幣(%d)", adenaTrade.get_adena_count())));
                    _pc.sendPackets(new S_CloseList(_pc.getId()));
                }
            }
        } else if (type == 8) {// 提取
            final int adenaTrade_id = _pc.getAdenaTradeId();
            final L1CharacterAdenaTrade adenaTrade = CharacterAdenaTradeReading.get().getCharacterAdenaTrade(adenaTrade_id);
            if (adenaTrade == null) {
                _pc.sendPackets(new S_CloseList(_pc.getId()));
                return;
            }
            if (adenaTrade.get_objid() != _pc.getId()) {
                _pc.sendPackets(new S_CloseList(_pc.getId()));
                return;
            }
            if (adenaTrade.get_over() != 1) {
                if (adenaTrade.get_over() == 0) {
                    _pc.sendPackets(new S_SystemMessage("\\aD該單還未出售。"));
                } else if (adenaTrade.get_over() == 2) {
                    _pc.sendPackets(new S_SystemMessage("\\aD該單已撤銷。"));
                } else if (adenaTrade.get_over() == 3) {
                    _pc.sendPackets(new S_SystemMessage("\\aD該單已提取。"));
                }
                _pc.sendPackets(new S_CloseList(_pc.getId()));
                return;
            }
            if (CharacterAdenaTradeReading.get().updateAdenaTrade(adenaTrade_id, 3)) {
                _pc.getInventory().storeItem(44070, adenaTrade.get_count());
                _pc.sendPackets(new S_SystemMessage(String.format("\\aD提取成功獲得元寶(%d)", adenaTrade.get_count())));
                _pc.sendPackets(new S_CloseList(_pc.getId()));
            }
        } else if (type == 0) {
            _pc.getAdenaTradeList().clear();
            for (final L1CharacterAdenaTrade adenaTrade : CharacterAdenaTradeReading.get().getAllCharacterAdenaTrades()) {
                if (adenaTrade.get_over() == 0) {
                    _pc.addAdenaTradeItem(adenaTrade);
                }
            }
            if (_pc.getAdenaTradeList().size() > 0) {
                _pc.getAdenaTradeList().sort(new DataComparatorAdenaTrade<L1CharacterAdenaTrade>());
            }
            _pc.setPage(0);
            ShowAdenaTrade(0);
        } else if (type >= 10 && type <= 19) {
            final int select_index = type - 10;
            if (select_index >= _pc.getAdenaTradeIndexList().size()) {
                _pc.sendPackets(new S_CloseList(_pc.getId()));
                return;
            }
            final int adenaTrade_id = _pc.getAdenaTradeIndexList().get(select_index);
            final L1CharacterAdenaTrade adenaTrade = CharacterAdenaTradeReading.get().getCharacterAdenaTrade(adenaTrade_id);
            if (adenaTrade == null) {
                _pc.sendPackets(new S_CloseList(_pc.getId()));
                return;
            }
            _pc.setAdenaTradeId(adenaTrade_id);
            final String[] data = new String[2];
            String msg = "";
            if (adenaTrade.get_over() == 0) {
                msg = "待售";
            } else if (adenaTrade.get_over() == 1) {
                msg = "已出售";
            } else if (adenaTrade.get_over() == 2) {
                msg = "已撤銷";
            } else if (adenaTrade.get_over() == 3) {
                msg = "已出售";
            }
            data[0] = String.format("[%d]金幣數量:%d(%s)", adenaTrade.get_Id(), adenaTrade.get_adena_count(), msg);
            data[1] = String.format("價格(%d)", adenaTrade.get_count());
            _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "adenatrade4", data));
        } else if (type >= 20 && type <= 29) {
            final int select_index = type - 20;
            if (select_index >= _pc.getAdenaTradeIndexList().size()) {
                _pc.sendPackets(new S_CloseList(_pc.getId()));
                return;
            }
            final int adenaTrade_id = _pc.getAdenaTradeIndexList().get(select_index);
            final L1CharacterAdenaTrade adenaTrade = CharacterAdenaTradeReading.get().getCharacterAdenaTrade(adenaTrade_id);
            if (adenaTrade == null) {
                _pc.sendPackets(new S_CloseList(_pc.getId()));
                return;
            }
            _pc.setAdenaTradeId(adenaTrade_id);
            final String[] data = new String[2];
            String msg = "";
            if (adenaTrade.get_over() == 0) {
                msg = "待售";
            } else if (adenaTrade.get_over() == 1) {
                msg = "領取元寶";
            } else if (adenaTrade.get_over() == 2) {
                msg = "已撤銷";
            } else if (adenaTrade.get_over() == 3) {
                msg = "交易成功";
            }
            data[0] = String.format("[%d]金幣數量:%d(%s)", adenaTrade.get_Id(), adenaTrade.get_adena_count(), msg);
            data[1] = String.format("價格(%d)", adenaTrade.get_count());
            _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "adenatrade6", data));
        }
    }

    private void ShowAdenaTradePC(final int index) {
        final List<L1CharacterAdenaTrade> adenaTrades = _pc.getAdenaTradeList();
        final String[] data = new String[30];
        int i = 0;
        _pc.clearAdenaTradeIndexList();
        for (int n = index * 10; n < adenaTrades.size(); n++) {
            if (i >= 30) {
                break;
            }
            String msg = "";
            if (adenaTrades.get(n).get_over() == 0) {
                msg = "待售";
            } else if (adenaTrades.get(n).get_over() == 1) {
                msg = "領取元寶";
            } else if (adenaTrades.get(n).get_over() == 2) {
                msg = "已撤銷";
            } else if (adenaTrades.get(n).get_over() == 3) {
                msg = "交易成功";
            }
            data[i] = String.format("[%d]金幣數量:%d(%s)", adenaTrades.get(n).get_Id(), adenaTrades.get(n).get_adena_count(), msg);
            data[i + 1] = String.format("價格(%d)", adenaTrades.get(n).get_count());
            data[i + 2] = " ";
            i += 3;
            _pc.addAdenaTradeIndex(adenaTrades.get(n).get_Id());
        }
        for (; i < 30; i++) {
            data[i] = " ";
        }
        _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "adenatrade5", data));
    }

    private void ShowAdenaTrade(final int index) {
        final List<L1CharacterAdenaTrade> adenaTrades = _pc.getAdenaTradeList();
        final String[] data = new String[30];
        int i = 0;
        _pc.clearAdenaTradeIndexList();
        for (int n = index * 10; n < adenaTrades.size(); n++) {
            if (i >= 30) {
                break;
            }
            data[i] = String.format("[%d]金幣數量:%d(待售)", adenaTrades.get(n).get_Id(), adenaTrades.get(n).get_adena_count());
            final double r = adenaTrades.get(n).get_adena_count() / adenaTrades.get(n).get_count();
            data[i + 1] = String.format("-->價格(%d) 1:%s", adenaTrades.get(n).get_count(), String.format("%.2f", r));
            data[i + 2] = " ";
            i += 3;
            _pc.addAdenaTradeIndex(adenaTrades.get(n).get_Id());
        }
        for (; i < 30; i++) {
            data[i] = " ";
        }
        _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "adenatrade0", data));
    }

    private void bindTradeChar(final String cmd) {
        try {
            final int index = Integer.parseInt(cmd.substring(13));
            if (_pc.getTempID() == 0) {
                _pc.clearTempObjects();
                return;
            }
            if (index < 0 || index >= _pc.getTempObjects().size()) {
                _pc.setTempID(0);
                _pc.clearTempObjects();
                return;
            }
            final L1ItemInstance tradeItem = _pc.getInventory().getItem(_pc.getTempID());
            if (tradeItem == null) {
                _pc.setTempID(0);
                _pc.clearTempObjects();
                return;
            }
            if (tradeItem.getItemCharaterTrade() != null) {
                _pc.setTempID(0);
                _pc.clearTempObjects();
                return;
            }
            final L1CharaterTrade tradechar = (L1CharaterTrade) _pc.getTempObjects().get(index);
            if (tradechar != null) {
                System.out.println("123123");
                if (CharaterTradeReading.get().updateBindChar(tradechar.get_char_objId(), 1)) {
                    tradeItem.setItemCharaterTrade(tradechar);
                    try {
                        CharItemsReading.get().updateItemCharTrade(tradeItem);
                        _pc.sendPackets(new S_ItemName(tradeItem));
                    } catch (Exception e) {
                        tradeItem.setItemCharaterTrade(null);
                    }
                    final ArrayList<String> list = AccountReading.get().loadCharacterItems(tradechar.get_char_objId());
                    _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "tradechar1", tradechar.getName() + "[綁定]", tradechar.getLevel(), list));
                }
            }
            _pc.clearTempObjects();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void rebindTradeChar() {
        if (_pc.getTempID() <= 0) {
            return;
        }
        final L1ItemInstance tradeItem = _pc.getInventory().getItem(_pc.getTempID());
        if (tradeItem == null) {
            _pc.setTempID(0);
            return;
        }
        final L1CharaterTrade charaterTrade = tradeItem.getItemCharaterTrade();
        if (charaterTrade == null) {
            _pc.setTempID(0);
            return;
        }
        if (_pc.getNetConnection().getAccount().get_countCharacters() >= 8) {
            _pc.setTempID(0);
            _pc.sendPackets(new S_SystemMessage("\\F1你當前賬號內人物太多 ."));
            _pc.sendPackets(new S_CloseList(_pc.getId()));
            return;
        }
        if (AccountReading.get().updaterecharBind(charaterTrade.get_char_objId())) {
            _pc.getInventory().removeItem(tradeItem);
            _pc.sendPackets(new S_SystemMessage("\\F1該人物已成功綁定至你賬號內 請小退查看"));
            _pc.sendPackets(new S_CloseList(_pc.getId()));
        }
    }

    private boolean isCheckTempObjects(final int tradeindex) {
        if (tradeindex < 0) {
            return false;
        }
        if (_pc.getTempObjects().isEmpty() || _pc.getTempObjects().size() <= 0) {
            return false;
        }
        if (tradeindex >= _pc.getTempObjects().size()) {
            return false;
        }
        return true;
    }

    private void showCharacterTradeHtml(final int index) {
        try {
            final String[] charData = new String[30];
            int n = 0;
            _pc.setPage(index);
            if (_pc.getTempObjects().size() > 0) {
                if (_pc.getTempObjects().get(0) instanceof L1CharaterTrade) {
                    for (int i = index * 5; i < _pc.getTempObjects().size(); i++) {
                        if (n >= 30) {
                            break;
                        }
                        // final int charTradeId =
                        // ((L1CharaterTrade)_pc.getTempObjects().get(i)).intValue();
                        final L1CharaterTrade charaterTrade = (L1CharaterTrade) _pc.getTempObjects().get(i);
                        charData[n] = charaterTrade.getName();
                        charData[n + 1] = String.valueOf(charaterTrade.getLevel());
                        charData[n + 2] = TYPE_CLASS[charaterTrade.get_Type()];
                        charData[n + 3] = (charaterTrade.get_Sex() == 0) ? "男" : "女";
                        charData[n + 4] = String.valueOf(charaterTrade.get_money_count());
                        charData[n + 5] = "查看信息";
                        n += 6;
                    }
                }
            }
            for (; n < 30; n++) {
                charData[n] = " ";
            }
            _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "character4", charData));// 顯示主頁
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void CharaterTrade(String cmd, long amount) {
        try {
            final int tradetype = Integer.parseInt(cmd.substring(15));
            switch (tradetype) {
                case 0:// 查看所有的角色交易列表
                    _pc.clearTempObjects();
                    for (final L1CharaterTrade charaterTrade : CharaterTradeReading.get().getAllCharaterTradeValues()) {
                        if (charaterTrade.get_state() == 0) {
                            _pc.addTempObject(charaterTrade);
                        }
                    }
                    if (_pc.getTempObjects().size() > 0) {
                        _pc.getTempObjects().sort(new DataComparatorCharacterTrade<>());
                        ;
                    }
                    showCharacterTradeHtml(0);
                    break;
                case 1:// 掛賣角色
                    _pc.clearTempObjects();
                    CharaterTradeReading.get().loadCharacterName(_pc);
                    if (_pc.getTempObjects().isEmpty() || _pc.getTempObjects().size() <= 0) {
                        _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "character0"));// 沒有額外的角色
                        return;
                    }
                    final String[] data = new String[7];
                    int i = 0;
                    for (final Object obj : _pc.getTempObjects()) {
                        final L1CharaterTrade tmp = (L1CharaterTrade) obj;
                        if (i >= 7) {
                            break;
                        }
                        data[i] = tmp.getName();
                        i++;
                    }
                    for (; i < 7; i++) {
                        data[i] = " ";
                    }
                    _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "character1", data));// 顯示要掛賣的角色列表
                    break;
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:// 選擇要掛賣的角色
                    final int tradeindex = tradetype - 2;
                    if (!isCheckTempObjects(tradeindex)) {
                        return;
                    }
                    if (_pc.getTempObjects().get(tradeindex) instanceof L1CharaterTrade) {
                        final L1CharaterTrade selCharTrade = (L1CharaterTrade) _pc.getTempObjects().get(tradeindex);
                        if (selCharTrade.getMapid() != 4) {
                            _pc.sendPackets(new S_SystemMessage("\\F2您掛賣的角色必須在大陸地圖"));
                            _pc.sendPackets(new S_CloseList(_pc.getId()));
                            return;
                        }
                        if (selCharTrade.getLevel() < 50) {
                            _pc.sendPackets(new S_SystemMessage("\\F2掛賣的角色不能低於50級"));
                            _pc.sendPackets(new S_CloseList(_pc.getId()));
                            return;
                        }
                        for (final L1CharaterTrade charaterTrade : CharaterTradeReading.get().getAllCharaterTradeValues()) {
                            if (charaterTrade.get_by_objId() == selCharTrade.get_char_objId() && (charaterTrade.get_state() == 0 || charaterTrade.get_state() == 1)) {
                                _pc.sendPackets(new S_SystemMessage("\\F2掛賣的角色已掛賣了其他角色"));
                                _pc.sendPackets(new S_CloseList(_pc.getId()));
                                return;
                            }
                        }
                        _pc.setTempID(tradeindex);
                        _pc.sendPackets(new S_HowManyMake(_pc.getId(), 0, 2000000000, "characterTrade_9", "character2", new String[]{selCharTrade.getName(), String.valueOf(selCharTrade.getLevel())}));
                    }
                    break;
                case 9:// 確認價格
                    final int selIndex = _pc.getTempID();
                    if (!isCheckTempObjects(selIndex)) {
                        return;
                    }
                    if (amount <= 0 || amount >= 2000000000) {
                        return;
                    }
                    if (_pc.getTempObjects().get(selIndex) instanceof L1CharaterTrade) {
                        final L1CharaterTrade selTmp = (L1CharaterTrade) _pc.getTempObjects().get(selIndex);
                        if (selTmp.getLevel() < 50) {
                            _pc.sendPackets(new S_SystemMessage("\\F2掛賣的角色不能低於50級"));
                            _pc.sendPackets(new S_CloseList(_pc.getId()));
                            return;
                        }
                        for (final L1CharaterTrade charaterTrade : CharaterTradeReading.get().getAllCharaterTradeValues()) {
                            if (charaterTrade.get_by_objId() == selTmp.get_char_objId() && (charaterTrade.get_state() == 0 || charaterTrade.get_state() == 1)) {
                                _pc.sendPackets(new S_SystemMessage("\\F2掛賣的角色已掛賣了其他角色"));
                                _pc.sendPackets(new S_CloseList(_pc.getId()));
                                return;
                            }
                        }
                        if (CharaterTradeReading.get().updateBindChar(selTmp.get_char_objId(), 1)) {
                            final L1CharaterTrade tmp = new L1CharaterTrade();
                            tmp.setName(selTmp.getName());
                            tmp.setLevel(selTmp.getLevel());
                            tmp.set_Type(selTmp.get_Type());
                            tmp.set_Sex(selTmp.get_Sex());
                            tmp.set_char_objId(selTmp.get_char_objId());
                            tmp.set_by_objId(_pc.getId());
                            tmp.set_id(CharaterTradeReading.get().get_nextId());
                            tmp.set_money_count((int) amount);
                            tmp.set_state(0);
                            if (CharaterTradeReading.get().addCharaterTrade(tmp)) {
                                _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "character3"));// 掛賣成功
                            } else {
                                _pc.sendPackets(new S_SystemMessage("\\F2掛賣失敗"));
                                _pc.sendPackets(new S_CloseList(_pc.getId()));
                            }
                        }
                    }
                    break;
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:// 確認選擇要購買的角色
                    final int Seltradeindex = (_pc.getPage() * 5) + (tradetype - 10);
                    if (!isCheckTempObjects(Seltradeindex)) {
                        return;
                    }
                    if (_pc.getTempObjects().get(Seltradeindex) instanceof L1CharaterTrade) {
                        // final int charTradeId =
                        // ((L1CharaterTrade)_pc.getTempObjects().get(Seltradeindex)).get_id();
                        final L1CharaterTrade selBuyCharTrade = (L1CharaterTrade) _pc.getTempObjects().get(Seltradeindex);
                        final String[] charData = new String[5];
                        charData[0] = selBuyCharTrade.getName();
                        charData[1] = String.valueOf(selBuyCharTrade.getLevel());
                        charData[2] = TYPE_CLASS[selBuyCharTrade.get_Type()];
                        charData[3] = selBuyCharTrade.get_Sex() == 0 ? "男" : "女";
                        charData[4] = String.valueOf(selBuyCharTrade.get_money_count());
                        _pc.setTempID(selBuyCharTrade.get_id());
                        _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "character5", charData));// 顯示選擇購買的角色
                    }
                    break;
                case 15:
                    // 上一頁
                    if (_pc.getPage() <= 0) {
                        _pc.sendPackets(new S_SystemMessage("\\F2已經是第一頁了"));
                        return;
                    }
                    showCharacterTradeHtml(_pc.getPage() - 1);
                    break;
                case 16:
                    // 下一頁
                    int pages = _pc.getTempObjects().size() / 5;
                    if (_pc.getTempObjects().size() % 5 != 0) {
                        pages++;
                    }
                    if (_pc.getPage() + 1 >= pages) {
                        _pc.sendPackets(new S_SystemMessage("\\F2已經是最後一頁了"));
                        return;
                    }
                    showCharacterTradeHtml(_pc.getPage() + 1);
                    break;
                case 17:// 顯示屬性
                case 18:// 顯示背包道具
                case 19:// 確認購買
                    final L1CharaterTrade charaterTradeItems = CharaterTradeReading.get().getCharaterTrade(_pc.getTempID());
                    if (charaterTradeItems == null) {
                        return;
                    }
                    final L1PcInstance target_pc = CharaterTradeReading.get().getPcInstance(charaterTradeItems.get_char_objId());
                    if (target_pc == null) {
                        return;
                    }
                    if (tradetype == 17) {
                        final String[] msg = new String[95];
                        msg[0] = target_pc.getName();
                        msg[1] = String.valueOf(target_pc.getLevel());
                        msg[2] = String.valueOf(target_pc.getMaxHp());
                        msg[3] = String.valueOf(target_pc.getMaxMp());
                        msg[4] = String.valueOf(target_pc.getStr());
                        msg[5] = String.valueOf(target_pc.getCon());
                        msg[6] = String.valueOf(target_pc.getDex());
                        msg[7] = String.valueOf(target_pc.getWis());
                        msg[8] = String.valueOf(target_pc.getInt());
                        msg[9] = String.valueOf(target_pc.getCha());
                        msg[10] = String.valueOf(target_pc.getAc());
                        msg[11] = String.valueOf(target_pc.getElixirStats());
                        int n = 12;
                        final ArrayList<L1UserSkillTmp> skillList = CharSkillReading.get().skills(target_pc.getId());
                        if (skillList != null && !skillList.isEmpty()) {
                            for (final L1UserSkillTmp skillTmp : skillList) {
                                if (n >= 95) {
                                    break;
                                }
                                msg[n] = skillTmp.get_skill_name();
                                n++;
                            }
                        }
                        for (; n < 95; n++) {
                            msg[n] = "";
                        }
                        _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "character6", msg));// 人物屬性頁面
                    } else if (tradetype == 18) {
                        _pc.sendPackets(new S_RetrieveList(0, target_pc));
                    } else if (tradetype == 19) {
                        if (_pc.getNetConnection().getAccount().get_countCharacters() >= 8) {
                            _pc.sendPackets(new S_SystemMessage("\\F2你賬號無法新建跟多角色"));
                            _pc.sendPackets(new S_CloseList(_pc.getId()));
                            return;
                        }
                        synchronized (charaterTradeItems) {
                            if (charaterTradeItems.get_state() == 0) {
                                final L1ItemInstance ybItem = _pc.getInventory().findItemId(44070);
                                if (ybItem == null || ybItem.getCount() < charaterTradeItems.get_money_count()) {
                                    _pc.sendPackets(new S_SystemMessage("\\F2貨幣不足"));
                                    _pc.sendPackets(new S_CloseList(_pc.getId()));
                                    return;
                                }
                                _pc.getInventory().removeItem(ybItem, charaterTradeItems.get_money_count());
                                CharaterTradeReading.get().updateCharaterTrade(charaterTradeItems, 1);
                                CharaterTradeReading.get().updateCharAccountName(charaterTradeItems.get_char_objId(), _pc.getAccountName());
                                _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "character7"));// 購買成功頁面
                            } else {
                                _pc.sendPackets(new S_SystemMessage("\\F2該角色已被出售"));
                                _pc.sendPackets(new S_CloseList(_pc.getId()));
                            }
                        }
                    }
                    break;
                case 20:// 查看自己的掛賣信息
                    characterTrade0();
                    break;
                case 21:// 操作自己掛賣的角色
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                    characterTrade1(tradetype - 21);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            // _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 顯示自己掛賣的角色列表
     */
    private void characterTrade0() {
        _pc.clearTempObjects();
        ;
        for (final L1CharaterTrade charaterTrade : CharaterTradeReading.get().getAllCharaterTradeValues()) {
            if (charaterTrade.get_by_objId() == _pc.getId() && (charaterTrade.get_state() == 0 || charaterTrade.get_state() == 1)) {
                _pc.addTempObject(charaterTrade.get_id());
            }
        }
        if (_pc.getTempObjects().isEmpty() && _pc.getTempObjects().size() <= 0) {
            _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "character9"));// 沒有掛賣任何角色
            return;
        }
        if (_pc.getTempObjects().get(0) instanceof Integer) {
            final String[] data1 = new String[28];
            int m = 0;
            for (int n = 0; n < _pc.getTempObjects().size(); n++) {
                if (m >= 28) {
                    break;
                }
                final int charTradeId = (Integer) _pc.getTempObjects().get(n);
                final L1CharaterTrade charaterTrade = CharaterTradeReading.get().getCharaterTrade(charTradeId);
                data1[m] = charaterTrade.getName();
                data1[m + 1] = String.valueOf(charaterTrade.get_money_count());
                if (charaterTrade.get_state() == 0) {
                    data1[m + 2] = "未出售";
                    data1[m + 3] = "撤銷";
                } else if (charaterTrade.get_state() == 1) {
                    data1[m + 2] = "已出售未領取";
                    data1[m + 3] = "領取元寶";
                } else {
                    data1[m + 2] = " ";
                    data1[m + 3] = " ";
                }
                m += 4;
            }
            for (; m < 28; m++) {
                data1[m] = " ";
            }
            _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "character8", data1));
        }
    }

    /**
     * 操作自己掛賣的角色
     */
    private void characterTrade1(final int index) {
        if (!isCheckTempObjects(index)) {
            return;
        }
        if (_pc.getTempObjects().get(index) instanceof Integer) {
            final int charTradeId = (Integer) _pc.getTempObjects().get(index);
            final L1CharaterTrade charaterTrade = CharaterTradeReading.get().getCharaterTrade(charTradeId);
            synchronized (charaterTrade) {
                if (charaterTrade.get_state() == 0) {
                    // 撤銷
                    CharaterTradeReading.get().updateCharaterTrade(charaterTrade, 3);
                    CharaterTradeReading.get().updateBindChar(charaterTrade.get_char_objId(), 0);
                    _pc.sendPackets(new S_CloseList(_pc.getId()));
                } else if (charaterTrade.get_state() == 1) {
                    // 領取元寶
                    CharaterTradeReading.get().updateCharaterTrade(charaterTrade, 2);
                    int piceCount = charaterTrade.get_money_count();
                    if (piceCount >= 10) {
                        int preaxCount = (int) (piceCount * 0.1);
                        if (preaxCount > 100) {
                            preaxCount = 100;
                        }
                        piceCount = piceCount - preaxCount;
                    }
                    _pc.getInventory().storeItem(44070, piceCount);
                    _pc.sendPackets(new S_SystemMessage(String.format("\\F2獲得貨幣(%d)", piceCount)));
                    _pc.sendPackets(new S_CloseList(_pc.getId()));
                }
            }
        }
    }

    /**
     * 展示指定任務進度資料
     *
     */
    private void showPage(int key) {
        try {
            final L1Quest quest = _pc.get_otherList().QUESTMAP.get(key);
            _pc.setTempID(quest.get_id());
            String over = null;
            // 該任務完成
            if (_pc.getQuest().isEnd(quest.get_id())) {
                over = "完成任務";// 完成任務!
            } else {
                over = _pc.getQuest().get_step(quest.get_id()) + " / " + quest.get_difficulty();
            }
            final String[] info = new String[]{quest.get_questname(), // 任務名稱
                    Integer.toString(quest.get_questlevel()), // 任務等級
                    over, // 任務進度
                    ""// 額外說明
            };
            _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "y_qi1", info));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 檢查個人狀態
     *
     */
    private void showStatus11(final L1PcInstance pc) {
        try {
            //目前已吃萬靈藥
            String n0 = String.valueOf(pc.getElixirStats());
            //你的基本力量點數
            String n1 = String.valueOf(pc.getBaseStr());
            //你的基本敏捷點數
            String n2 = String.valueOf(pc.getBaseDex());
            //你的基本智力點數
            String n3 = String.valueOf(pc.getBaseInt());
            //你的基本魅力點數
            String n4 = String.valueOf(pc.getBaseCha());
            //你的基本體質點數
            String n5 = String.valueOf(pc.getBaseCon());
            //你的基本精神點數
            String n6 = String.valueOf(pc.getBaseWis());
            //額外增加力量點數
            String n7 = String.valueOf(pc.getStr() - pc.getBaseStr());
            //額外增加敏捷點數
            String n8 = String.valueOf(pc.getDex() - pc.getBaseDex());
            //額外增加智力點數
            String n9 = String.valueOf(pc.getInt() - pc.getBaseInt());
            //額外增加魅力點數
            String n10 = String.valueOf(pc.getCha() - pc.getBaseCha());
            //額外增加體質點數
            String n11 = String.valueOf(pc.getCon() - pc.getBaseCon());
            //額外增加精神點數
            String n12 = String.valueOf(pc.getWis() - pc.getBaseWis());
            //人物基本魔攻點數
            String n13 = String.valueOf(pc.getTrueSp());
            //額外增加魔攻點數
            String n14 = String.valueOf(pc.getSp() - pc.getTrueSp());
            //基本血量
            String n15 = String.valueOf(pc.getBaseMaxHp());
            //額外血量
            String n16 = String.valueOf(pc.getMaxHp() - pc.getBaseMaxHp());
            //基本魔量
            String n17 = String.valueOf(pc.getBaseMaxMp());
            //額外魔量
            String n18 = String.valueOf(pc.getMaxMp() - pc.getBaseMaxMp());
            //近距離命中加成
            String n19 = String.valueOf(pc.getHitup());
            //遠距離命中加成
            String n20 = String.valueOf(pc.getBowHitup());
            //近距離傷害加成
            String n21 = String.valueOf(pc.getDmgup());
            //遠距離傷害加成
            String n22 = String.valueOf(pc.getBowDmgup());
            // 魔法命中加成
            String n23 = String.valueOf(pc.getOriginalMagicHit());
            // 防禦加成
            String n24 = String.valueOf(CalcStat.calcAc(pc.getType(), pc.getLevel(), pc.getBaseDex()));
            final String[] info = new String[]{n0, n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20, n21, n22, n23, n24};
            pc.sendPackets(new S_NPCTalkReturn(pc, "y_status_1", info));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 遊戲便捷優化
     *
     */
    private void showCulture2(final L1PcInstance pc) {
        try {
            // 在取得一次最新資料
            String attackhe = "";
            //
            if (_pc.attackhe() == true) {
                attackhe = "[ 開啟中 ]";
            } else {
                attackhe = "[ 關閉中 ]";
            }
            String armorhe = "";
            //
            if (_pc.armorhe() == true) {
                armorhe = "[ 開啟中 ]";
            } else {
                armorhe = "[ 關閉中 ]";
            }
            String droplist = "";
            //
            if (_pc.droplist() == true) {
                droplist = "[ 開啟中 ]";
            } else {
                droplist = "[ 關閉中 ]";
            }
            String kill = "";
            //
            if (_pc.kill() == true) {
                kill = "[ 開啟中 ]";
            } else {
                kill = "[ 關閉中 ]";
            }
            final String nowDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
            final String[] info = new String[]{Config.SERVERNAME, String.valueOf(attackhe), String.valueOf(armorhe), String.valueOf(droplist), String.valueOf(kill), nowDate, // 目前時間
                    ServerRestartTimer.get_restartTime()// , // 重啟時間
            };
            pc.sendPackets(new S_NPCTalkReturn(pc, "y_who", info));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 輔助(自動補血)
     *
     */
    private void show_zdbx(final L1PcInstance pc) {
        try {
            // 輔助(自動補血)界面總開關
            String AutoHpAllOpen = "";
            String AutoHpAllStop = "";
            if (pc.isAutoHpAll()) {
                AutoHpAllOpen = "已啟動";
            } else {
                AutoHpAllStop = "未啟動";
            }
            // 輔助(自動補血)第一組開關
            String AutoHp1Open = "";
            String AutoHp1Stop = "";
            if (pc.isAutoHp1()) {
                AutoHp1Open = "開";
            } else {
                AutoHp1Stop = "關";
            }
            // 輔助(自動補血)第二組開關
            String AutoHp2Open = "";
            String AutoHp2Stop = "";
            if (pc.isAutoHp2()) {
                AutoHp2Open = "開";
            } else {
                AutoHp2Stop = "關";
            }
            // 輔助(自動補血)第三組開關
            String AutoHp3Open = "";
            String AutoHp3Stop = "";
            if (pc.isAutoHp3()) {
                AutoHp3Open = "開";
            } else {
                AutoHp3Stop = "關";
            }
            // 輔助(自動回城)開關
            String AutoBackHomeOpen = "";
            String AutoBackHomeStop = "";
            if (pc.isAutoBackHome()) {
                AutoBackHomeOpen = "開";
            } else {
                AutoBackHomeStop = "關";
            }
            // 輔助(自動補魔->道具)開關
            String AutoMP1Open = "";
            String AutoMP1Stop = "";
            if (pc.isAutoMp1()) {
                AutoMP1Open = "開";
            } else {
                AutoMP1Stop = "關";
            }
            // 輔助(自動補魔->魂體)開關
            String AutoMP2Open = "";
            String AutoMP2Stop = "";
            if (pc.isAutoMp2()) {
                AutoMP2Open = "開";
            } else {
                AutoMP2Stop = "關";
            }
            final L1Item item1 = ItemTable.get().getTemplate(pc.getAutoItemId1());
            final L1Item item2 = ItemTable.get().getTemplate(pc.getAutoItemId2());
            final L1Item item3 = ItemTable.get().getTemplate(pc.getAutoItemId3());
            final L1Item item4 = ItemTable.get().getTemplate(pc.getAutoItemId4());
            String n0 = String.valueOf(AutoHpAllStop); // 輔助(自動補血)界面總開關
            String n1 = String.valueOf(AutoHpAllOpen); // 輔助(自動補血)界面總開關
            String n2 = String.valueOf(AutoHp1Open); // 輔助(自動補血)第一組開關
            String n3 = String.valueOf(AutoHp1Stop); // 輔助(自動補血)第一組開關
            String n4 = String.valueOf(pc.getTextHp1()); // 第一組補血%
            String n5 = String.valueOf(item1.getName()); // 第一組補血道具
            String n6 = String.valueOf(AutoHp2Open); // 輔助(自動補血)第二組開關
            String n7 = String.valueOf(AutoHp2Stop); // 輔助(自動補血)第二組開關
            String n8 = String.valueOf(pc.getTextHp2()); // 第二組補血%
            String n9 = String.valueOf(item2.getName()); // 第二組補血道具
            String n10 = String.valueOf(AutoHp3Open); // 輔助(自動補血)第三組開關 待加入...
            String n11 = String.valueOf(AutoHp3Stop); // 輔助(自動補血)第三組開關 待加入...
            String n12 = String.valueOf(pc.getTextHp3()); // 第三組補血% 待加入...
            String n13 = String.valueOf(item3.getName()); // 第三組補血道具 待加入...
            String n14 = String.valueOf(AutoBackHomeOpen); // 輔助(自動回城)開關
            String n15 = String.valueOf(AutoBackHomeStop); // 輔助(自動回城)開關
            String n16 = String.valueOf(pc.getTextBh()); // 自動回城%
            String n17 = String.valueOf(AutoMP1Open); // 輔助(自動補魔->道具)開關
            String n18 = String.valueOf(AutoMP1Stop); // 輔助(自動補魔->道具)開關
            String n19 = String.valueOf(pc.getTextMp1()); // 輔助(自動補魔->道具)%
            String n20 = String.valueOf(item4.getName()); // 輔助(自動補魔->道具)
            String n21 = String.valueOf(AutoMP2Open); // 輔助(自動補魔->魂體)開關
            String n22 = String.valueOf(AutoMP2Stop); // 輔助(自動補魔->魂體)開關
            String n23 = String.valueOf(pc.getTextMp2()); // 輔助(自動補魔->魂體)%
            String n24 = String.valueOf("魂體轉換"); // 輔助(自動補魔->魂體)
            final String[] info = new String[]{n0, n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20, n21, n22, n23, n24};
            pc.sendPackets(new S_NPCTalkReturn(pc, "zdbx", info));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 輔助狀態(道具)
     *
     */
    private void show_yxdj(final L1PcInstance pc) {
        try {
            // 輔助狀態(道具)界面總開關
            String AutoItemAllOpen = "";
            String AutoItemAllStop = "";
            if (pc.isAutoItemAll()) {
                AutoItemAllOpen = "已啟動";
            } else {
                AutoItemAllStop = "未啟動";
            }
            // 輔助狀態(道具) -> 解毒藥水
            String AutoItem1Open = "";
            String AutoItem1Stop = "";
            if (pc.isAutoItem1()) {
                AutoItem1Open = "開";
            } else {
                AutoItem1Stop = "關";
            }
            // 輔助狀態(道具) -> 藍色藥水
            String AutoItem2Open = "";
            String AutoItem2Stop = "";
            if (pc.isAutoItem2()) {
                AutoItem2Open = "開";
            } else {
                AutoItem2Stop = "關";
            }
            // 輔助狀態(道具) -> 慎重藥水
            String AutoItem3Open = "";
            String AutoItem3Stop = "";
            if (pc.isAutoItem3()) {
                AutoItem3Open = "開";
            } else {
                AutoItem3Stop = "關";
            }
            // 輔助狀態(道具) -> 綠色藥水
            String AutoItem4Open = "";
            String AutoItem4Stop = "";
            if (pc.isAutoItem4()) {
                AutoItem4Open = "開";
            } else {
                AutoItem4Stop = "關";
            }
            // 輔助狀態(道具) -> 名譽貨幣
            String AutoItem5Open = "";
            String AutoItem5Stop = "";
            if (pc.isAutoItem5()) {
                AutoItem5Open = "開";
            } else {
                AutoItem5Stop = "關";
            }
            // 輔助狀態(道具) -> 巧克力蛋糕
            String AutoItem6Open = "";
            String AutoItem6Stop = "";
            if (pc.isAutoItem6()) {
                AutoItem6Open = "開";
            } else {
                AutoItem6Stop = "關";
            }
            // 輔助狀態(道具) -> 自動變形卷軸
            String AutoItem7Open = "";
            String AutoItem7Stop = "";
            if (pc.isAutoItem7()) {
                AutoItem7Open = "開";
            } else {
                AutoItem7Stop = "關";
            }
            // 輔助狀態(道具) -> 自動聖結界卷軸
            String AutoItem8Open = "";
            String AutoItem8Stop = "";
            if (pc.isAutoItem8()) {
                AutoItem8Open = "開";
            } else {
                AutoItem8Stop = "關";
            }
            // 輔助狀態(道具) -> 自動魔法娃娃召喚
            String AutoItem9Open = "";
            String AutoItem9Stop = "";
            if (pc.isAutoItem9()) {
                AutoItem9Open = "開";
            } else {
                AutoItem9Stop = "關";
            }
            // 輔助狀態(道具) -> 自動一段經驗藥水
            String AutoItem10Open = "";
            String AutoItem10Stop = "";
            if (pc.isAutoItem10()) {
                AutoItem10Open = "開";
            } else {
                AutoItem10Stop = "關";
            }
            // 輔助狀態(道具) -> 自動二段經驗藥水
            String AutoItem11Open = "";
            String AutoItem11Stop = "";
            if (pc.isAutoItem11()) {
                AutoItem11Open = "開";
            } else {
                AutoItem11Stop = "關";
            }
            // 輔助狀態(道具) -> 飽食度瞬滿(5萬金幣)
            String AutoItem12Open = "";
            String AutoItem12Stop = "";
            if (pc.isAutoItem12()) {
                AutoItem12Open = "開";
            } else {
                AutoItem12Stop = "關";
            }
            // 輔助狀態(道具) -> 自動魔法卷軸(魔法屏障)
            String AutoItem13Open = "";
            String AutoItem13Stop = "";
            if (pc.isAutoItem13()) {
                AutoItem13Open = "開";
            } else {
                AutoItem13Stop = "關";
            }
            String n0 = String.valueOf(AutoItemAllStop); // 輔助狀態(道具)界面總開關
            String n1 = String.valueOf(AutoItemAllOpen); // 輔助狀態(道具)界面總開關
            String n2 = String.valueOf(AutoItem1Open); // 輔助狀態(道具) -> 解毒藥水
            String n3 = String.valueOf(AutoItem1Stop); // 輔助狀態(道具) -> 解毒藥水
            String n4 = String.valueOf(AutoItem2Open); // 輔助狀態(道具) -> 藍色藥水
            String n5 = String.valueOf(AutoItem2Stop); // 輔助狀態(道具) -> 藍色藥水
            String n6 = String.valueOf(AutoItem3Open); // 輔助狀態(道具) -> 慎重藥水
            String n7 = String.valueOf(AutoItem3Stop); // 輔助狀態(道具) -> 慎重藥水
            String n8 = String.valueOf(AutoItem4Open); // 輔助狀態(道具) -> 綠色藥水
            String n9 = String.valueOf(AutoItem4Stop); // 輔助狀態(道具) -> 綠色藥水
            String n10 = String.valueOf(AutoItem5Open); // 輔助狀態(道具) -> 名譽貨幣
            String n11 = String.valueOf(AutoItem5Stop); // 輔助狀態(道具) -> 名譽貨幣
            String n12 = String.valueOf(AutoItem6Open); // 輔助狀態(道具) -> 巧克力蛋糕
            String n13 = String.valueOf(AutoItem6Stop); // 輔助狀態(道具) -> 巧克力蛋糕
            String n14 = String.valueOf(AutoItem7Open); // 輔助狀態(道具) -> 自動變形卷軸
            String n15 = String.valueOf(AutoItem7Stop); // 輔助狀態(道具) -> 自動變形卷軸
            String n16 = String.valueOf(AutoItem8Open); // 輔助狀態(道具) -> 自動聖結界卷軸
            String n17 = String.valueOf(AutoItem8Stop); // 輔助狀態(道具) -> 自動聖結界卷軸
            String n18 = String.valueOf(AutoItem9Open); // 輔助狀態(道具) -> 自動魔法娃娃召喚
            String n19 = String.valueOf(AutoItem9Stop); // 輔助狀態(道具) -> 自動魔法娃娃召喚
            String n20 = String.valueOf(AutoItem10Open); // 輔助狀態(道具) -> 自動一段經驗藥水
            String n21 = String.valueOf(AutoItem10Stop); // 輔助狀態(道具) -> 自動一段經驗藥水
            String n22 = String.valueOf(AutoItem11Open); // 輔助狀態(道具) -> 自動二段經驗藥水
            String n23 = String.valueOf(AutoItem11Stop); // 輔助狀態(道具) -> 自動二段經驗藥水
            String n24 = String.valueOf(AutoItem12Open); // 輔助狀態(道具) -> 飽食度瞬滿(5萬金幣)
            String n25 = String.valueOf(AutoItem12Stop); // 輔助狀態(道具) -> 飽食度瞬滿(5萬金幣)
            String n26 = String.valueOf(AutoItem13Open); // 輔助狀態(道具) -> 自動魔法卷軸(魔法屏障)
            String n27 = String.valueOf(AutoItem13Stop); // 輔助狀態(道具) -> 自動魔法卷軸(魔法屏障)
            final String[] info = new String[]{n0, n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20, n21, n22, n23, n24, n25, n26, n27};
            pc.sendPackets(new S_NPCTalkReturn(pc, "yxngxt", info));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 輔助狀態(魔法)
     *
     */
    private void show_ngxtAll(final L1PcInstance pc) {
        try {
            // 輔助狀態(魔法) -> 總開關
            String AutoSkillAllOpen = "";
            String AutoSkillAllStop = "";
            if (pc.isAutoSkillAll()) {
                AutoSkillAllOpen = "開";
            } else {
                AutoSkillAllStop = "關";
            }
            // 輔助狀態(魔法) -> 1
            String AutoSkill_1_Open = "";
            String AutoSkill_1_Stop = "";
            if (pc.isAutoSkill_1()) {
                AutoSkill_1_Open = "開";
            } else {
                AutoSkill_1_Stop = "關";
            }
            // 輔助狀態(魔法) -> 2
            String AutoSkill_2_Open = "";
            String AutoSkill_2_Stop = "";
            if (pc.isAutoSkill_2()) {
                AutoSkill_2_Open = "開";
            } else {
                AutoSkill_2_Stop = "關";
            }
            // 輔助狀態(魔法) -> 3
            String AutoSkill_3_Open = "";
            String AutoSkill_3_Stop = "";
            if (pc.isAutoSkill_3()) {
                AutoSkill_3_Open = "開";
            } else {
                AutoSkill_3_Stop = "關";
            }
            // 輔助狀態(魔法) -> 4
            String AutoSkill_4_Open = "";
            String AutoSkill_4_Stop = "";
            if (pc.isAutoSkill_4()) {
                AutoSkill_4_Open = "開";
            } else {
                AutoSkill_4_Stop = "關";
            }
            // 輔助狀態(魔法) -> 5
            String AutoSkill_5_Open = "";
            String AutoSkill_5_Stop = "";
            if (pc.isAutoSkill_5()) {
                AutoSkill_5_Open = "開";
            } else {
                AutoSkill_5_Stop = "關";
            }
            // 輔助狀態(魔法) -> 6
            String AutoSkill_6_Open = "";
            String AutoSkill_6_Stop = "";
            if (pc.isAutoSkill_6()) {
                AutoSkill_6_Open = "開";
            } else {
                AutoSkill_6_Stop = "關";
            }
            // 輔助狀態(魔法) -> 7
            String AutoSkill_7_Open = "";
            String AutoSkill_7_Stop = "";
            if (pc.isAutoSkill_7()) {
                AutoSkill_7_Open = "開";
            } else {
                AutoSkill_7_Stop = "關";
            }
            // 輔助狀態(魔法) -> 8
            String AutoSkill_8_Open = "";
            String AutoSkill_8_Stop = "";
            if (pc.isAutoSkill_8()) {
                AutoSkill_8_Open = "開";
            } else {
                AutoSkill_8_Stop = "關";
            }
            // 輔助狀態(魔法) -> 9
            String AutoSkill_9_Open = "";
            String AutoSkill_9_Stop = "";
            if (pc.isAutoSkill_9()) {
                AutoSkill_9_Open = "開";
            } else {
                AutoSkill_9_Stop = "關";
            }
            // 輔助狀態(魔法) -> 10
            String AutoSkill_10_Open = "";
            String AutoSkill_10_Stop = "";
            if (pc.isAutoSkill_10()) {
                AutoSkill_10_Open = "開";
            } else {
                AutoSkill_10_Stop = "關";
            }
            // 輔助狀態(魔法) -> 11
            String AutoSkill_11_Open = "";
            String AutoSkill_11_Stop = "";
            if (pc.isAutoSkill_11()) {
                AutoSkill_11_Open = "開";
            } else {
                AutoSkill_11_Stop = "關";
            }
            // 輔助狀態(魔法) -> 12
            String AutoSkill_12_Open = "";
            String AutoSkill_12_Stop = "";
            if (pc.isAutoSkill_12()) {
                AutoSkill_12_Open = "開";
            } else {
                AutoSkill_12_Stop = "關";
            }
            // 輔助狀態(魔法) -> 13
            String AutoSkill_13_Open = "";
            String AutoSkill_13_Stop = "";
            if (pc.isAutoSkill_13()) {
                AutoSkill_13_Open = "開";
            } else {
                AutoSkill_13_Stop = "關";
            }
            // 輔助狀態(魔法) -> 14
            String AutoSkill_14_Open = "";
            String AutoSkill_14_Stop = "";
            if (pc.isAutoSkill_14()) {
                AutoSkill_14_Open = "開";
            } else {
                AutoSkill_14_Stop = "關";
            }
            // 輔助狀態(魔法) -> 15
            String AutoSkill_15_Open = "";
            String AutoSkill_15_Stop = "";
            if (pc.isAutoSkill_15()) {
                AutoSkill_15_Open = "開";
            } else {
                AutoSkill_15_Stop = "關";
            }
            // 輔助狀態(魔法) -> 16
            String AutoSkill_16_Open = "";
            String AutoSkill_16_Stop = "";
            if (pc.isAutoSkill_16()) {
                AutoSkill_16_Open = "開";
            } else {
                AutoSkill_16_Stop = "關";
            }
            // 輔助狀態(魔法) -> 17
            String AutoSkill_17_Open = "";
            String AutoSkill_17_Stop = "";
            if (pc.isAutoSkill_17()) {
                AutoSkill_17_Open = "開";
            } else {
                AutoSkill_17_Stop = "關";
            }
            // 輔助狀態(魔法) -> 18
            String AutoSkill_18_Open = "";
            String AutoSkill_18_Stop = "";
            if (pc.isAutoSkill_18()) {
                AutoSkill_18_Open = "開";
            } else {
                AutoSkill_18_Stop = "關";
            }
            // 輔助狀態(魔法) -> 19
            String AutoSkill_19_Open = "";
            String AutoSkill_19_Stop = "";
            if (pc.isAutoSkill_19()) {
                AutoSkill_19_Open = "開";
            } else {
                AutoSkill_19_Stop = "關";
            }
            // 輔助狀態(魔法) -> 20
            String AutoSkill_20_Open = "";
            String AutoSkill_20_Stop = "";
            if (pc.isAutoSkill_20()) {
                AutoSkill_20_Open = "開";
            } else {
                AutoSkill_20_Stop = "關";
            }
            // 輔助狀態(魔法) -> 21
            String AutoSkill_21_Open = "";
            String AutoSkill_21_Stop = "";
            if (pc.isAutoSkill_21()) {
                AutoSkill_21_Open = "開";
            } else {
                AutoSkill_21_Stop = "關";
            }
            // 輔助狀態(魔法) -> 22
            String AutoSkill_22_Open = "";
            String AutoSkill_22_Stop = "";
            if (pc.isAutoSkill_22()) {
                AutoSkill_22_Open = "開";
            } else {
                AutoSkill_22_Stop = "關";
            }
            // 輔助狀態(魔法) -> 23
            String AutoSkill_23_Open = "";
            String AutoSkill_23_Stop = "";
            if (pc.isAutoSkill_23()) {
                AutoSkill_23_Open = "開";
            } else {
                AutoSkill_23_Stop = "關";
            }
            // 輔助狀態(魔法) -> 24
            String AutoSkill_24_Open = "";
            String AutoSkill_24_Stop = "";
            if (pc.isAutoSkill_24()) {
                AutoSkill_24_Open = "開";
            } else {
                AutoSkill_24_Stop = "關";
            }
            // 輔助狀態(魔法) -> 25
            String AutoSkill_25_Open = "";
            String AutoSkill_25_Stop = "";
            if (pc.isAutoSkill_25()) {
                AutoSkill_25_Open = "開";
            } else {
                AutoSkill_25_Stop = "關";
            }
            // 輔助狀態(魔法) -> 26
            String AutoSkill_26_Open = "";
            String AutoSkill_26_Stop = "";
            if (pc.isAutoSkill_26()) {
                AutoSkill_26_Open = "開";
            } else {
                AutoSkill_26_Stop = "關";
            }
            // 輔助狀態(魔法) -> 27
            String AutoSkill_27_Open = "";
            String AutoSkill_27_Stop = "";
            if (pc.isAutoSkill_27()) {
                AutoSkill_27_Open = "開";
            } else {
                AutoSkill_27_Stop = "關";
            }
            // 輔助狀態(魔法) -> 28
            String AutoSkill_28_Open = "";
            String AutoSkill_28_Stop = "";
            if (pc.isAutoSkill_28()) {
                AutoSkill_28_Open = "開";
            } else {
                AutoSkill_28_Stop = "關";
            }
            // 輔助狀態(魔法) -> 29
            String AutoSkill_29_Open = "";
            String AutoSkill_29_Stop = "";
            if (pc.isAutoSkill_29()) {
                AutoSkill_29_Open = "開";
            } else {
                AutoSkill_29_Stop = "關";
            }
            // 輔助狀態(魔法) -> 30
            String AutoSkill_30_Open = "";
            String AutoSkill_30_Stop = "";
            if (pc.isAutoSkill_30()) {
                AutoSkill_30_Open = "開";
            } else {
                AutoSkill_30_Stop = "關";
            }
            // 輔助狀態(魔法) -> 31
            String AutoSkill_31_Open = "";
            String AutoSkill_31_Stop = "";
            if (pc.isAutoSkill_31()) {
                AutoSkill_31_Open = "開";
            } else {
                AutoSkill_31_Stop = "關";
            }
            String n0 = String.valueOf(AutoSkillAllOpen); // 輔助狀態(魔法) -> 總開關
            String n1 = String.valueOf(AutoSkillAllStop); // 輔助狀態(魔法) -> 總開關
            String n2 = String.valueOf(AutoSkill_1_Open); // 輔助狀態(魔法) -> 1
            String n3 = String.valueOf(AutoSkill_1_Stop); // 輔助狀態(魔法) -> 1
            String n4 = String.valueOf(AutoSkill_2_Open); // 輔助狀態(魔法) -> 2
            String n5 = String.valueOf(AutoSkill_2_Stop); // 輔助狀態(魔法) -> 2
            String n6 = String.valueOf(AutoSkill_3_Open); // 輔助狀態(魔法) -> 3
            String n7 = String.valueOf(AutoSkill_3_Stop); // 輔助狀態(魔法) -> 3
            String n8 = String.valueOf(AutoSkill_4_Open); // 輔助狀態(魔法) -> 4
            String n9 = String.valueOf(AutoSkill_4_Stop); // 輔助狀態(魔法) -> 4
            String n10 = String.valueOf(AutoSkill_5_Open); // 輔助狀態(魔法) -> 5
            String n11 = String.valueOf(AutoSkill_5_Stop); // 輔助狀態(魔法) -> 5
            String n12 = String.valueOf(AutoSkill_6_Open); // 輔助狀態(魔法) -> 6
            String n13 = String.valueOf(AutoSkill_6_Stop); // 輔助狀態(魔法) -> 6
            String n14 = String.valueOf(AutoSkill_7_Open); // 輔助狀態(魔法) -> 7
            String n15 = String.valueOf(AutoSkill_7_Stop); // 輔助狀態(魔法) -> 7
            String n16 = String.valueOf(AutoSkill_8_Open); // 輔助狀態(魔法) -> 8
            String n17 = String.valueOf(AutoSkill_8_Stop); // 輔助狀態(魔法) -> 8
            String n18 = String.valueOf(AutoSkill_9_Open); // 輔助狀態(魔法) -> 9
            String n19 = String.valueOf(AutoSkill_9_Stop); // 輔助狀態(魔法) -> 9
            String n20 = String.valueOf(AutoSkill_10_Open); // 輔助狀態(魔法) -> 10
            String n21 = String.valueOf(AutoSkill_10_Stop); // 輔助狀態(魔法) -> 10
            String n22 = String.valueOf(AutoSkill_11_Open); // 輔助狀態(魔法) -> 11
            String n23 = String.valueOf(AutoSkill_11_Stop); // 輔助狀態(魔法) -> 11
            String n24 = String.valueOf(AutoSkill_12_Open); // 輔助狀態(魔法) -> 12
            String n25 = String.valueOf(AutoSkill_12_Stop); // 輔助狀態(魔法) -> 12
            String n26 = String.valueOf(AutoSkill_13_Open); // 輔助狀態(魔法) -> 13
            String n27 = String.valueOf(AutoSkill_13_Stop); // 輔助狀態(魔法) -> 13
            String n28 = String.valueOf(AutoSkill_14_Open); // 輔助狀態(魔法) -> 14
            String n29 = String.valueOf(AutoSkill_14_Stop); // 輔助狀態(魔法) -> 14
            String n30 = String.valueOf(AutoSkill_15_Open); // 輔助狀態(魔法) -> 15
            String n31 = String.valueOf(AutoSkill_15_Stop); // 輔助狀態(魔法) -> 15
            String n32 = String.valueOf(AutoSkill_16_Open); // 輔助狀態(魔法) -> 16
            String n33 = String.valueOf(AutoSkill_16_Stop); // 輔助狀態(魔法) -> 16
            String n34 = String.valueOf(AutoSkill_17_Open); // 輔助狀態(魔法) -> 17
            String n35 = String.valueOf(AutoSkill_17_Stop); // 輔助狀態(魔法) -> 17
            String n36 = String.valueOf(AutoSkill_18_Open); // 輔助狀態(魔法) -> 18
            String n37 = String.valueOf(AutoSkill_18_Stop); // 輔助狀態(魔法) -> 18
            String n38 = String.valueOf(AutoSkill_19_Open); // 輔助狀態(魔法) -> 19
            String n39 = String.valueOf(AutoSkill_19_Stop); // 輔助狀態(魔法) -> 19
            String n40 = String.valueOf(AutoSkill_20_Open); // 輔助狀態(魔法) -> 20
            String n41 = String.valueOf(AutoSkill_20_Stop); // 輔助狀態(魔法) -> 20
            String n42 = String.valueOf(AutoSkill_21_Open); // 輔助狀態(魔法) -> 21
            String n43 = String.valueOf(AutoSkill_21_Stop); // 輔助狀態(魔法) -> 21
            String n44 = String.valueOf(AutoSkill_22_Open); // 輔助狀態(魔法) -> 22
            String n45 = String.valueOf(AutoSkill_22_Stop); // 輔助狀態(魔法) -> 22
            String n46 = String.valueOf(AutoSkill_23_Open); // 輔助狀態(魔法) -> 23
            String n47 = String.valueOf(AutoSkill_23_Stop); // 輔助狀態(魔法) -> 23
            String n48 = String.valueOf(AutoSkill_24_Open); // 輔助狀態(魔法) -> 24
            String n49 = String.valueOf(AutoSkill_24_Stop); // 輔助狀態(魔法) -> 24
            String n50 = String.valueOf(AutoSkill_25_Open); // 輔助狀態(魔法) -> 25
            String n51 = String.valueOf(AutoSkill_25_Stop); // 輔助狀態(魔法) -> 25
            String n52 = String.valueOf(AutoSkill_26_Open); // 輔助狀態(魔法) -> 26
            String n53 = String.valueOf(AutoSkill_26_Stop); // 輔助狀態(魔法) -> 26
            String n54 = String.valueOf(AutoSkill_27_Open); // 輔助狀態(魔法) -> 27
            String n55 = String.valueOf(AutoSkill_27_Stop); // 輔助狀態(魔法) -> 27
            String n56 = String.valueOf(AutoSkill_28_Open); // 輔助狀態(魔法) -> 28
            String n57 = String.valueOf(AutoSkill_28_Stop); // 輔助狀態(魔法) -> 28
            String n58 = String.valueOf(AutoSkill_29_Open); // 輔助狀態(魔法) -> 29
            String n59 = String.valueOf(AutoSkill_29_Stop); // 輔助狀態(魔法) -> 29
            String n60 = String.valueOf(AutoSkill_30_Open); // 輔助狀態(魔法) -> 30
            String n61 = String.valueOf(AutoSkill_30_Stop); // 輔助狀態(魔法) -> 30
            String n62 = String.valueOf(AutoSkill_31_Open); // 輔助狀態(魔法) -> 31
            String n63 = String.valueOf(AutoSkill_31_Stop); // 輔助狀態(魔法) -> 31
            final String[] info = new String[]{n0, n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20, n21, n22, n23, n24, n25, n26, n27, n28, n29, n30, n31, n32, n33, n34, n35, n36, n37, n38, n39, n40, n41, n42, n43, n44, n45, n46, n47, n48, n49, n50, n51, n52, n53, n54, n55, n56, n57, n58, n59, n60, n61, n62, n63};
            if (_pc.isCrown()) {// 王族
                pc.sendPackets(new S_NPCTalkReturn(pc, "ngxt0", info));
            } else if (_pc.isKnight()) {// 騎士
                pc.sendPackets(new S_NPCTalkReturn(pc, "ngxt1", info));
            } else if (_pc.isElf()) {// 精靈
                pc.sendPackets(new S_NPCTalkReturn(pc, "ngxt2", info));
            } else if (_pc.isWizard()) {// 法師
                pc.sendPackets(new S_NPCTalkReturn(pc, "ngxt3", info));
            } else if (_pc.isDarkelf()) {// 黑妖
                pc.sendPackets(new S_NPCTalkReturn(pc, "ngxt4", info));
            } else if (_pc.isDragonKnight()) {// 龍騎
                pc.sendPackets(new S_NPCTalkReturn(pc, "ngxt5", info));
            } else if (_pc.isIllusionist()) {// 幻術
                pc.sendPackets(new S_NPCTalkReturn(pc, "ngxt6", info));
            } else if (_pc.isWarrior()) {// 戰士
                pc.sendPackets(new S_NPCTalkReturn(pc, "ngxt7", info));
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 輔助(自動刪物)
     *
     */
    private void show_yxzdsw(final L1PcInstance pc) {
        try {
            // 輔助(自動刪物)
            String AutoRemoveItemOpen = "";
            String AutoRemoveItemStop = "";
            if (pc.isAutoRemoveItem()) {
                AutoRemoveItemOpen = "開";
            } else {
                AutoRemoveItemStop = "關";
            }
            String n0 = String.valueOf(AutoRemoveItemOpen); // 輔助(自動刪物) -> 總開關
            String n1 = String.valueOf(AutoRemoveItemStop); // 輔助(自動刪物) -> 總開關
            String n2 = String.valueOf(ConfigAutoAll.Remove_Item_Max); // 自動刪物可添加的上限
            final StringBuilder name = new StringBuilder();
            String AutoRemoveItem = "";
            // 取回PC刪物名單
            final int shopItems = pc.getRemoveItemInventory().getSize();
            if (shopItems > 0) {
                for (final Object itemObject : pc.getRemoveItemInventory().getItems()) {
                    final L1ItemInstance itemTemplate = (L1ItemInstance) itemObject;
                    name.append("【").append(itemTemplate.getName()).append("】");
                    AutoRemoveItem = name.toString(); // 輔助(自動刪物)->刪物的物品
                }
            } else {
                AutoRemoveItem = "";
            }
            String n3 = String.valueOf(AutoRemoveItem);
            final String[] info = new String[]{n0, n1, n2, n3};
            pc.sendPackets(new S_NPCTalkReturn(pc, "yxzdsw", info));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private static class DataComparatorAdenaTrade<T> implements Comparator<L1CharacterAdenaTrade> {
        @Override
        public int compare(L1CharacterAdenaTrade s1, L1CharacterAdenaTrade s2) {
            final int n1 = s1.get_adena_count() / s1.get_count();
            final int n2 = s2.get_adena_count() / s2.get_count();
            return n2 - n1;
        }
    }

    private static class DataComparatorCharacterTrade<T> implements Comparator<Object> {
        @Override
        public int compare(Object s1, Object s2) {
            final L1CharaterTrade temp1 = (L1CharaterTrade) s1;
            final L1CharaterTrade temp2 = (L1CharaterTrade) s2;
            final int num1 = temp2.getLevel() - temp1.getLevel();
            int num = num1 == 0 ? temp1.get_money_count() - temp2.get_money_count() : num1;
            return num;
        }
    }
}
