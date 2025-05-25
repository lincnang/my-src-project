package com.lineage.server.clientpackets;

import com.lineage.DatabaseFactory;
import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigLIN;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.RecordTable;
import com.lineage.server.datatables.lock.CharaterTradeReading;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Clan;
import com.lineage.server.serverpackets.S_DeleteCharOK;
import com.lineage.server.templates.L1CharaterTrade;
import com.lineage.server.world.WorldClan;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * 要求角色刪除
 *
 * @author daien
 */
public class C_DeleteChar extends ClientBasePacket {
    private static final Log _log = LogFactory.getLog(C_DeleteChar.class);

    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            // 資料載入
            read(decrypt);
            final String name = readS();
            if (name.isEmpty()) {
                return;
            }
            try {
                final L1PcInstance pc = CharacterTable.get().restoreCharacter(name);
                for (final L1CharaterTrade charaterTrade : CharaterTradeReading.get().getAllCharaterTradeValues()) {
                    if (charaterTrade.get_by_objId() == pc.getId() && (charaterTrade.get_state() == 0 || charaterTrade.get_state() == 1)) {
                        //如果有掛賣信息 不能刪除
                        return;
                    }
                }
                try {
                    final L1PcInstance P = CharacterTable.get().restoreCharacter(name);
                    if ((pc != null) && (pc.getLevel() >= ConfigAlt.DELETE_CHARACTER_AFTER_LV) && (ConfigAlt.DELETE_CHARACTER_AFTER_7DAYS)) {
                        if (pc.getType() < 32) {
                            if (pc.isCrown()) {
                                pc.setType(32);
                            } else if (pc.isKnight()) {
                                pc.setType(33);
                            } else if (pc.isElf()) {
                                pc.setType(34);
                            } else if (pc.isWizard()) {
                                pc.setType(35);
                            } else if (pc.isDarkelf()) {
                                pc.setType(36);
                            } else if (pc.isDragonKnight()) {
                                pc.setType(37);
                            } else if (pc.isIllusionist()) {
                                pc.setType(38);
                            } else if (pc.isWarrior()) {
                                pc.setType(39);
                            }
                            final Timestamp deleteTime = new Timestamp(System.currentTimeMillis() + 604800000); // 7日後
                            pc.setDeleteTime(deleteTime);
                            pc.save(); // 資料存檔
                        } else {
                            if (pc.isCrown()) {
                                pc.setType(0);
                            } else if (pc.isKnight()) {
                                pc.setType(1);
                            } else if (pc.isElf()) {
                                pc.setType(2);
                            } else if (pc.isWizard()) {
                                pc.setType(3);
                            } else if (pc.isDarkelf()) {
                                pc.setType(4);
                            } else if (pc.isDragonKnight()) {
                                pc.setType(5);
                            } else if (pc.isIllusionist()) {
                                pc.setType(6);
                            } else if (pc.isWarrior()) {
                                pc.setType(7);
                            }
                            pc.setDeleteTime(null);
                            pc.save(); // 資料存檔
                        }
                        client.out().encrypt(new S_DeleteCharOK(S_DeleteCharOK.DELETE_CHAR_AFTER_7DAYS));
                        return;
                    }
                    if (pc != null) {
                        L1Clan clan = WorldClan.get().getClan(pc.getClanname());
                        if (clan != null) {
                            clan.delMemberName(name);
                        }
                    }
                    // 已創人物數量
                    final int countCharacters = client.getAccount().get_countCharacters();
                    client.getAccount().set_countCharacters(countCharacters - 1);
                    // 移出已用名稱清單
                    CharObjidTable.get().charRemove(name);
                    // 刪除人物
                    CharacterTable.get().deleteCharacter(client.getAccountName(), name);
                } catch (final Exception e) {
                    System.out.println("刪除角色例外狀況");
                    client.close();
                    return;
                }
                client.out().encrypt(new S_DeleteCharOK(S_DeleteCharOK.DELETE_CHAR_NOW));
                deleteTamList(pc.getId()); // 刪除人物成長果實預約資料
                deleteMonsterBookList(pc.getId()); // 刪除人物怪物圖鑒資料
                if (ConfigLIN.Week_Quest) {
                    deleteWeekQuestList(pc.getId()); // 刪除人物周任務資料
                }
            } catch (final Exception e) {
                client.close();
                return;
            }
            client.out().encrypt(new S_DeleteCharOK(S_DeleteCharOK.DELETE_CHAR_NOW));
            RecordTable.get().recordPcChangePassWord1(client.getAccountName(), name);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            over();
        }
    }

    /**
     * 刪除人物怪物圖鑒資料
     */
    private void deleteMonsterBookList(int char_id) {
        try (Connection con = DatabaseFactory.get().getConnection(); PreparedStatement pstm = con.prepareStatement("DELETE FROM tb_user_monster_book WHERE char_id=?")) {
            pstm.setInt(1, char_id);
            pstm.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 刪除人物周任務資料
     */
    private void deleteWeekQuestList(int char_id) {
        try (Connection con = DatabaseFactory.get().getConnection(); PreparedStatement pstm = con.prepareStatement("DELETE FROM tb_user_week_quest WHERE char_id=?")) {
            pstm.setInt(1, char_id);
            pstm.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 刪除人物成長果實預約資料
     */
    private void deleteTamList(int char_id) {
        try (Connection con = DatabaseFactory.get().getConnection(); PreparedStatement pstm = con.prepareStatement("DELETE FROM tam WHERE objid=?")) {
            pstm.setInt(1, char_id);
            pstm.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
