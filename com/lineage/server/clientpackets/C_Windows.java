package com.lineage.server.clientpackets;

import com.lineage.DatabaseFactory;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.CharacterGiftTable;
import com.lineage.server.datatables.SoulTowerTable;
import com.lineage.server.datatables.lock.ClanReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1BookMark;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class C_Windows extends ClientBasePacket {
    private static final Log _log = LogFactory.getLog(C_Windows.class);

    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            read(decrypt);
            L1PcInstance pc = client.getActiveChar();
            int type = readC();
            switch (type) {
                case 0: // 申訴
                    /*
                     * int objid = readD(); L1Object obj =
                     * World.get().findObject(objid); if ((obj instanceof
                     * L1PcInstance)) { L1PcInstance tgpc = (L1PcInstance)obj;
                     * _log.warn("玩家:" + pc.getName() + " 申訴:(" + objid + ")" +
                     * tgpc.getName()); } else { _log.warn("玩家:" + pc.getName() +
                     * " 申訴:NPC(" + objid + ")"); }
                     */
                    break;
                case 6: // 龍之門扉 6.1 捨棄
                    int itemobjid = readD();
                    int selectdoor = readC();
                    L1ItemInstance item = pc.getInventory().getItem(itemobjid);
                    if (item == null) {
                        return;
                    }
                    switch (selectdoor) {
                        case 0:
                            pc.getInventory().removeItem(item, 1);
                            L1SpawnUtil.spawn(pc, 70932, 0, 7200);
                            break;
                        case 1:
                            pc.getInventory().removeItem(item, 1);
                            L1SpawnUtil.spawn(pc, 70937, 0, 7200);
                            break;
                        case 2:
                            pc.getInventory().removeItem(item, 1);
                            L1SpawnUtil.spawn(pc, 70934, 0, 7200);
                            break;
                        default:
                            break;
                    }
                    break;
                case 11: // 發送角色座標
                    String name = readS();
                    int mapid = readH();
                    int x = readH();
                    int y = readH();
                    int zone = readD();
                    L1PcInstance target = World.get().getPlayer(name);
                    if (target != null) {
                        target.sendPackets(new S_PacketBoxLoc(pc.getName(), mapid, x, y, zone));
                        pc.sendPackets(new S_ServerMessage(1783, name));
                    } else {
                        pc.sendPackets(new S_ServerMessage(1782));
                    }
                    break;
                case 0x22: // 紀錄座標設置
                    readC();
                    int num;
                    int size = pc._bookmarks.size();
                    for (int i = 0; i < size; i++) {
                        num = readC();
                        pc._bookmarks.get(i).setTemp_id(num);
                    }
                    pc._speedbookmarks.clear();
                    for (int i = 0; i < 5; i++) {
                        num = readC();
                        if (num == 255) {
                            return;
                        }
                        pc._bookmarks.get(num).setSpeed_id(i);
                        pc._speedbookmarks.add(pc._bookmarks.get(num));
                    }
                    break;
                case 0x27: // 變更記憶座標的顏色或是名稱
                    int sizeColor = readD();
                    int Numid;
                    String bookname;
                    Connection con = null;
                    PreparedStatement pstm = null;
                    try {
                        if (sizeColor != 0) {
                            con = DatabaseFactory.get().getConnection();
                        }
                        for (int i = 0; i < sizeColor; i++) {
                            Numid = readD();
                            int id = 0;
                            for (L1BookMark book : pc.getBookMarkArray()) {
                                if (book.getNumId() == Numid) {
                                    id = book.getId();
                                }
                            }
                            bookname = readS();
                            bookname = bookname.replace("\\", "\\\\");
                            pstm = con.prepareStatement("UPDATE character_teleport SET name='" + bookname + "' WHERE id='" + id + "'");
                            pstm.execute();
                        }
                    } catch (SQLException e) {
                    } finally {
                        SQLUtil.close(pstm);
                        SQLUtil.close(con);
                    }
                    break;
                case 44: // 每日成就重整
                    pc.setKillCount(0);
                    pc.sendPackets(new S_OwnCharStatus(pc));
                    break;
                case 9: // 更新Ctrl+Q的顯示時間
                    pc.sendPackets(new S_MapTimerOut(pc));
                    break;
                case 0x2e: // 識別盟徽 狀態
                    // 如果不是君主或聯盟王
                    if ((pc.getClanRank() != L1Clan.CLAN_RANK_PRINCE) && (pc.getClanRank() != L1Clan.CLAN_RANK_LEAGUE_PRINCE)) {
                        return;
                    }
                    final int emblemStatus = readC(); // 0: 關閉 1:開啟
                    final L1Clan clan = pc.getClan();
                    clan.setShowEmblem(emblemStatus);
                    ClanReading.get().updateClan(clan);
                    clan.sendPacketsAll(new S_PacketBox(S_PacketBox.PLEDGE_EMBLEM_STATUS, emblemStatus));
                    break;
                case 0x30: // 村莊便利傳送
                    int mapIndex = readH(); // 1: 亞丁 2:古魯丁 3: 奇巖
                    int point = readH();
                    int locx = 0;
                    int locy = 0;
                    if (mapIndex == 1) {
                        if (point == 0) { // 亞丁-村莊北邊地區
                            // X34079 Y33136 右下角 X 34090 Y 33150
                            locx = 34079 + (int) (Math.random() * 12);
                            locy = 33136 + (int) (Math.random() * 15);
                        } else if (point == 1) { // 亞丁-村莊中心地區
                            // 左上角 X 33970 Y 33243 右下角 X33979 Y33256
                            locx = 33970 + (int) (Math.random() * 10);
                            locy = 33243 + (int) (Math.random() * 14);
                        } else if (point == 2) { // 亞丁-村莊教堂地區
                            // 左上 X33925 Y33351 右下 X33938 Y33359
                            locx = 33925 + (int) (Math.random() * 14);
                            locy = 33351 + (int) (Math.random() * 9);
                        }
                    } else if (mapIndex == 2) {
                        if (point == 0) { // 古魯丁-北邊地區
                            // 左上 X32615 Y32719 右下 X32625 Y32725
                            locx = 32615 + (int) (Math.random() * 11);
                            locy = 32719 + (int) (Math.random() * 7);
                        } else if (point == 1) { // 古魯丁-南邊地區
                            // 左上 X32621 Y32788 右下 X32629 Y32800
                            locx = 32621 + (int) (Math.random() * 9);
                            locy = 32788 + (int) (Math.random() * 13);
                        }
                    } else if (mapIndex == 3) {
                        if (point == 0) { // 奇巖-北邊地區
                            // 左上 X33501 Y32765 右下 X33511 Y32773
                            locx = 33501 + (int) (Math.random() * 11);
                            locy = 32765 + (int) (Math.random() * 9);
                        } else if (point == 1) { // 奇巖-南邊地區
                            // 左上 X33440 Y32784 右下 X33450 Y32794
                            locx = 33440 + (int) (Math.random() * 11);
                            locy = 32784 + (int) (Math.random() * 11);
                        }
                    } else if (mapIndex == 4) { // 市場
                        int loc[][] = {{32838, 32886}, {32800, 32874}, {32755, 32899}, {32741, 32938}, {32740, 32964}, {32801, 32982}, {32845, 32986}, {32852, 32932}, {32799, 32927}};
                        locx = loc[point][0];
                        locy = loc[point][1];
                    }
                    L1Teleport.teleport(pc, locx, locy, pc.getMapId(), pc.getHeading(), true);
                    pc.sendPackets(new S_PacketBox(S_PacketBox.TOWN_TELEPORT, pc));
                    break;
                case 0x37: // 精靈的祝賀禮物
                    final int index = readC();
                    CharacterGiftTable.getInstance().completed(pc, index);
                    break;
                case 0x3a: // 屍魂塔查看排名
                    SoulTowerTable.getInstance().showRank(pc);
                    break;
            }
            // 媽祖的處理
            if (pc != null) {
                if (pc.get_mazu_time() != 0) {
                    if (pc.is_mazu()) {
                        if (2400 - pc.get_mazu_time() >= 2400) {// 2400秒 = 40分鐘
                            pc.set_mazu_time(0);
                            pc.set_mazu(false);
                        }
                    }
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            over();
        }
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }
}
