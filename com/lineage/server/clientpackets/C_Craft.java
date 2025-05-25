package com.lineage.server.clientpackets;

import com.lineage.DatabaseFactory;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.DeNameTable;
import com.lineage.server.datatables.InvSwapTable;
import com.lineage.server.datatables.SpamTable;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1ExcludingList;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.S_ACTION_UI;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.DeName;
import com.lineage.server.templates.L1CharName;
import com.lineage.server.utils.SQLUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class C_Craft extends ClientBasePacket {
    private static final int Exclude = 0x1f; // 黑名單
    private static final int InvSwap = 0x21; // 裝備切換
    private static final int DUNGEON_TIME = 0x22; // 顯示計時地圖剩餘時間
    private static final int SEAL = 0x39; // 背包快捷封印道具按鈕

    public void start(final byte[] decrypt, final ClientExecutor client) {
        // 資料載入
        read(decrypt);
        if (client == null) {
            return;
        }
        // 使用者
        final L1PcInstance pc = client.getActiveChar();
        final int type = readC();
        switch (type) {
            case Exclude: // 黑名單
                readC();
                readC();
                readH();
                if (pc == null) {
                    return;
                }
                final L1ExcludingList exList = SpamTable.getInstance().getExcludeTable(pc.getId());
                final int Type = readC(); // 0：リスト、1：追加、2：削除
                if (Type == 0) {
                    pc.sendPackets(new S_PacketBox(S_PacketBox.SHOW_LIST_EXCLUDE, exList.getExcludeList(0), 0));
                    pc.sendPackets(new S_PacketBox(S_PacketBox.SHOW_LIST_EXCLUDE, exList.getExcludeList(1), 1));
                } else {
                    readC();
                    final int subType = readC();
                    while (true) {
                        final int dummy = readC();
                        if (dummy == 0 || dummy == 64) {
                            break;
                        }
                        final int enamelength = readC();
                        if (enamelength == 0 || enamelength > 12) {
                            break;
                        }
                        final String charName = readS2(enamelength);
                        if (charName.equalsIgnoreCase(pc.getName())) {
                            pc.sendPackets(new S_SystemMessage("\\aD不能將自己加入黑名單。"));
                            break;
                        }
                        if (exList.contains(subType, charName)) {
                            delExclude(pc, subType, charName);
                            exList.remove(subType, charName);
                            pc.sendPackets(new S_PacketBox(S_PacketBox.REM_EXCLUDE, subType, charName));
                        } else {
                            // 玩家
                            for (final L1CharName cn : CharacterTable.get().getCharNameList()) {
                                if (charName.equalsIgnoreCase(cn.getName())) {
                                    final int objId = cn.getId();
                                    final String name = cn.getName();
                                    exList.add(subType, name);
                                    insertExclude(pc, subType, objId, name);
                                    pc.sendPackets(new S_PacketBox(S_PacketBox.ADD_EXCLUDE, subType, charName));
                                    return;
                                }
                            }
                            // 假人
                            for (final DeName de : DeNameTable.get().getDeNameList()) {
                                if (charName.equalsIgnoreCase(de.get_name())) {
                                    final int objId = de.get_deobjid();
                                    final String name = de.get_name();
                                    exList.add(subType, name);
                                    insertExclude(pc, subType, objId, name);
                                    pc.sendPackets(new S_PacketBox(S_PacketBox.ADD_EXCLUDE, subType, charName));
                                    return;
                                }
                            }
                        }
                    }
                }
                break;
            case InvSwap: // 裝備切換
                readH();
                readC();
                final int index = readC();
                final int code = readC();
                if (index == 0x08) {
                    InvSwapTable.getInstance().toSaveSet(pc, code);
                } else if (index == 0x10) {
                    InvSwapTable.getInstance().toChangeSet(pc, code);
                }
                break;
            case DUNGEON_TIME: // 顯示計時地圖剩餘時間    8.1用不着-先注销
                pc.sendPackets(new S_ACTION_UI(pc, S_ACTION_UI.DUNGEON_TIME));
                break;
            case SEAL: // 背包快捷封印道具按鈕
                readH();
                readH();
                final L1ItemInstance l1iteminstance1 = pc.getInventory().getItem(read4(read_size()));
                if (l1iteminstance1.getBless() == 0 || l1iteminstance1.getBless() == 1 || l1iteminstance1.getBless() == 2 || l1iteminstance1.getBless() == 3) {
                    int Bless = 0;
                    switch (l1iteminstance1.getBless()) {
                        case 0:
                            Bless = 128;
                            break;
                        case 1:
                            Bless = 129;
                            break;
                        case 2:
                            Bless = 130;
                            break;
                        case 3:
                            Bless = 131;
                            break;
                    }
                    l1iteminstance1.setBless(Bless);
                    int st = 0;
                    if (l1iteminstance1.isIdentified()) {
                        st += 1;
                    }
                    if (!l1iteminstance1.getItem().isTradable()) {
                        st += 2;
                    }
                    if (l1iteminstance1.getItem().isCantDelete()) {
                        st += 4;
                    }
                    if (l1iteminstance1.getItem().get_safeenchant() < 0) {
                        st += 8;
                    }
                    if (l1iteminstance1.getBless() >= 128) {
                        st = 32;
                        if (l1iteminstance1.isIdentified()) {
                            st += 15;
                        } else {
                            st += 14;
                        }
                    }
                    pc.sendPackets(new S_PacketBox(S_PacketBox.ITEM_STATUS, l1iteminstance1, st));
                    pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_IS_ID);
                    pc.getInventory().saveItem(l1iteminstance1, L1PcInventory.COL_IS_ID);
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
                break;
            default:
                break;
        }
    }

    private void insertExclude(final L1PcInstance pc, final int subType, final int objId, final String name) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("INSERT INTO character_exclude SET char_id=?, type=?, exclude_id=?, exclude_name=?");
            pstm.setInt(1, pc.getId());
            pstm.setInt(2, subType);
            pstm.setInt(3, objId);
            pstm.setString(4, name);
            pstm.execute();
        } catch (final SQLException e) {
            // _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    private void delExclude(final L1PcInstance pc, final int subType, final String name) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("DELETE FROM character_exclude WHERE char_id=? AND type=? AND exclude_name=?");
            pstm.setInt(1, pc.getId());
            pstm.setInt(2, subType);
            pstm.setString(3, name);
            pstm.execute();
        } catch (final SQLException e) {
            // _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    @Override
    public String getType() {
        return "[C] C_Craft";
    }
}
