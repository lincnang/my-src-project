package com.lineage.server.model;

import com.lineage.DatabaseFactory;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.LuckyLotteryTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Luckylottery;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1LuckyLottery;
import com.lineage.server.utils.RandomArrayList;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 潘朵拉幸運抽抽樂
 */
public class L1PandoraInventory {
    public static final Log _log = LogFactory.getLog(L1PandoraInventory.class);
    private static AtomicInteger _id;
    private static Object _lock = new Object();

    static {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("select max(id)+1 as nextid from (select id from character_pandora) t");
            rs = pstm.executeQuery();
            int id = 0;
            if (rs.next()) {
                id = rs.getInt("nextid");
            }
            if (id < 1) {
                id = 1;
            }
            _id = new AtomicInteger(id);
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
    }

    private final L1PcInstance _owner;
    private List<L1PandoraItem> _items = new CopyOnWriteArrayList<L1PandoraItem>();

    public L1PandoraInventory(final L1PcInstance owner) {
        this._owner = owner;
    }

    public static int nextId() {
        synchronized (_lock) {
            return _id.getAndIncrement();
        }
    }

    public void loadItems() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM character_pandora WHERE char_id = ? order by id");
            pstm.setInt(1, this._owner.getId());
            rs = pstm.executeQuery();
            while (rs.next()) {
                final L1PandoraItem item = new L1PandoraItem();
                item.setId(rs.getInt("id"));
                item.setType(rs.getInt("type"));
                item.setItemId(rs.getInt("item_id"));
                item.setCharId(rs.getInt("char_id"));
                item.setCount(rs.getInt("count"));
                item.setItemName(rs.getString("item_name"));
                item.setEnchantlvl(rs.getInt("enchantlvl"));
                this._items.add(item);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
    }

    public void insertItem(final L1PandoraItem item) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("INSERT INTO character_pandora SET id = ?, type = ?, item_id = ?, char_id = ?, count = ?, item_name = ?, enchantlvl = ?");
            pstm.setInt(1, item.getId());
            pstm.setInt(2, item.getType());
            pstm.setInt(3, item.getItemId());
            pstm.setInt(4, item.getCharId());
            pstm.setInt(5, item.getCount());
            pstm.setString(6, item.getItemName());
            pstm.setInt(7, item.getEnchantlvl());
            pstm.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        this._items.add(item);
    }

    public boolean deleteItem(final L1PandoraItem item) {
        Connection con = null;
        PreparedStatement pstm = null;
        boolean flag = false;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("DELETE FROM character_pandora WHERE id = ?");
            pstm.setInt(1, item.getId());
            pstm.execute();
            flag = true;
        } catch (SQLException e) {
            //_log.error(e.getLocalizedMessage(), e);
            _log.info("刪除潘朵拉倉庫失敗=>itemid:" + item.getItemName() + ".id:" + item.getId() + ".charid:" + item.getCharId() + "," + e.getMessage());
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        this._items.remove(this._items.indexOf(item));
        return flag;
    }

    public void clearItems() {
        this._items.clear();
    }

    public List<L1PandoraItem> getItems() {
        return this._items;
    }

    public synchronized L1PandoraItem FindItem(final int id) {
        for (final L1PandoraItem item : this._items) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    public synchronized void useLottery(final L1ItemInstance lottery, final int use_count) {
        if (use_count < 1) {
            return;
        }
        int removeCount = 0;
        if (lottery.getItemId() == 71440) {
            removeCount = use_count;
        } else if (lottery.getItemId() == 71439) {
            removeCount = use_count * 3;
        }
        if (lottery.getCount() < removeCount) {
            return;
        }
        this._owner.getInventory().removeItem(lottery, removeCount);
        final ArrayList<L1LuckyLottery> list = LuckyLotteryTable.getInstance().getList();
        for (int i = 0; i < use_count; i++) {
            final int rand = RandomArrayList.getInc(LuckyLotteryTable.getInstance().total, 1);
            L1LuckyLottery r = null;
            for (final L1LuckyLottery ll : list) {
                if (ll.getRandom() <= rand && ll.getTotal() >= rand) {
                    r = ll;
                    break;
                }
            }
            if (r == null) {
                r = list.get(0);
            }
            if (r == null) {
                break;
            }
            final L1Item item = ItemTable.get().getTemplate(r.getItem_id());
            if (item != null) {
                final L1PandoraItem pItem = new L1PandoraItem();
                pItem.setId(nextId());
                pItem.setType(1);
                pItem.setItemId(r.getItem_id());
                pItem.setCharId(this._owner.getId());
                pItem.setCount(r.getCount());
                pItem.setItemName(item.getName());
                pItem.setEnchantlvl(r.getEnchantlvl());
                this.insertItem(pItem);
                final String e = "+" + r.getEnchantlvl() + " ";
                final String itemName = String.valueOf((r.getEnchantlvl() > 0) ? e : "") + item.getName();
                this._owner.sendPackets(new S_Luckylottery(pItem.getId(), r.getCount(), item.getGfxId(), pItem.getId(), itemName));
                if (r.getIsbroad()) {
                    //World.get().broadcastPacketToPandora(new S_ServerMessage( itemName, item.getGfxId(), this._owner.getName(), 1));
                    World.get().broadcastPacketToAlldroplist(new S_ServerMessage(itemName, item.getGfxId(), this._owner.getName(), 1));  //src045
                    // World.get().broadcastPacketToAll(new S_PacketBoxGree("\\fT 恭喜" + _owner.getName() + "\\fT 在潘朵拉抽抽樂 抽到了 【" + item.getName() + "\\fT 】 ！"));
                    // L1Pandora("玩家:【 " + this._owner.getName() + " 】 抽到【 " + item.getName() + " 】時間:" + "(" + new Timestamp(System.currentTimeMillis()) + ")。");
                    WriteLogTxt.Recording("潘朵拉抽抽樂紀錄", "玩家:【 " + this._owner.getName() + " 】 抽到【 " + item.getName() + " 】.");
                }
            }
        }
    }
    /*public static void L1Pandora(final String info) {
        final int Mon = GetNowTime.GetNowMonth() + 1;
        final int Day = GetNowTime.GetNowDay();
        final String z = String.valueOf(Mon) + "月" + Day + "日";
        try {
            final BufferedWriter out = new BufferedWriter(new FileWriter("物品操作日誌/潘朵拉抽抽樂紀錄/" + z + ".txt", true));
            out.write(String.valueOf(info) + "\r\n");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
