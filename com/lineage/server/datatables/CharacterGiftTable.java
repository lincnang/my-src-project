package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * 精靈的祝賀禮物
 *
 * @author srwh
 */
public class CharacterGiftTable {
    private static final Log _log = LogFactory.getLog(CharacterGiftTable.class);
    private static CharacterGiftTable _instance;
    private final HashMap<Integer, GiftData> _char_gift_list = new HashMap<>();
    private final HashMap<String, LevelGiftData> _gift_list = new HashMap<>();

    public CharacterGiftTable() {
        loadGiftTable();
        loadCharGiftTable();
    }

    public static CharacterGiftTable getInstance() {
        if (_instance == null) {
            _instance = new CharacterGiftTable();
        }
        return _instance;
    }

    private void loadGiftTable() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM 功能_精靈的祝賀禮物設置");
            rs = pstm.executeQuery();
            while (rs.next()) {
                final int index = rs.getInt("index");
                final int itemid = rs.getInt("itemid");
                final String key = index + "-" + itemid;
                if (_gift_list.containsKey(key)) {
                    continue;
                }
                final LevelGiftData data = new LevelGiftData();
                data.index = index;
                data.itemid = itemid;
                data.count = rs.getInt("count");
                data.enchant = rs.getInt("enchant");
                data.activate = rs.getString("activate");
                _gift_list.put(key, data);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
        _log.info("讀取->功能_精靈的祝賀禮物設置數量: " + _gift_list.size() + "(" + timer.get() + "ms)");
    }

    private void loadCharGiftTable() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM character_elf_gift");
            rs = pstm.executeQuery();
            while (rs.next()) {
                final int objid = rs.getInt("objid");
                final byte[] data = rs.getBytes("data");
                if (!_char_gift_list.containsKey(objid)) {
                    final GiftData gift = new GiftData(objid);
                    gift.data = data;
                    _char_gift_list.put(objid, gift);
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
    }

    private void sendGift(final L1PcInstance pc, final int index) {
        final String classType = pc.getClassFeature().getClassToken();
        for (final LevelGiftData data : _gift_list.values()) {
            if (data.index != index) {
                continue;
            }
            // 職業不符
            // 全職=A,王=P,騎=K,妖=E,法=W,黑=D,龍=R,幻=I,戰=O
            if (!data.activate.contains(classType) && !data.activate.contains("A")) {
                continue;
            }
            // creat item
            CreateNewItem.createNewItem_LV(pc, data.itemid, data.count, data.enchant);
        }
    }

    public void sendPacket(final L1PcInstance pc) {
        GiftData gift;
        if (_char_gift_list.containsKey(pc.getId())) {
            gift = _char_gift_list.get(pc.getId());
        } else {
            gift = new GiftData(pc.getId());
            insert(gift);
        }
        pc.sendPackets(new S_PacketBox(S_PacketBox.GIFT_512, gift.data));
    }

    public void completed(final L1PcInstance pc, final int index) {
        if (!_char_gift_list.containsKey(pc.getId())) {
            System.out.println("CharacterGiftTable has some error , ID=" + pc.getId());
            return;
        }
        final GiftData gift = _char_gift_list.get(pc.getId());
        if (index < gift.data.length && gift.data[index] == 0) {
            gift.data[index] = 1;
            update(gift);
            sendGift(pc, index);
        }
    }

    private void insert(final GiftData gift) {
        if (_char_gift_list.containsKey(gift.objid)) {
            return;
        }
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("INSERT INTO character_elf_gift SET objid=?, data=?");
            pstm.setInt(1, gift.objid);
            pstm.setBytes(2, gift.data);
            pstm.execute();
            _char_gift_list.put(gift.objid, gift);
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    private void update(final GiftData gift) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE character_elf_gift SET  data=? WHERE objid=" + gift.objid);
            pstm.setBytes(1, gift.data);
            pstm.execute();
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    private class GiftData {
        public int objid = 0;
        public byte[] data = new byte[512];

        private GiftData(final int _objid) {
            objid = _objid;
        }
    }

    private class LevelGiftData {
        public int index;
        public int itemid;
        public int count;
        public int enchant;
        public String activate;
    }
}
