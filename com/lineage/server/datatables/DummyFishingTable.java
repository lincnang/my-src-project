package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.IdFactory;
import com.lineage.server.model.Instance.L1FishingPc;
import com.lineage.server.templates.L1PcOther;
import com.lineage.server.templates.L1PcOtherList;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.SQLUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DummyFishingTable {
    private static final Random R = new Random();
    private static final int[][] CLASS_LIST = {{0, 61, 138, 734, 2786, 6658, 6671}, {1, 48, 37, 1186, 2796, 6661, 6650}};
    private static DummyFishingTable I;
    private ArrayList<L1FishingPc> list = new ArrayList<L1FishingPc>();
    private ArrayList<Integer> delays = new ArrayList<Integer>();

    private DummyFishingTable() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `dummy_fishing`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                L1FishingPc pc = new L1FishingPc();
                pc.setId(IdFactory.get().nextId());
                pc.setType(rs.getByte("type"));
                pc.set_sex(rs.getBoolean("sex") ? 1 : 0);
                pc.setClassId(CLASS_LIST[pc.get_sex()][pc.getType()]);
                pc.setLevel(rs.getByte("level") & 0xFF);
                pc.setName(rs.getString("name"));
                pc.setLawful(rs.getShort("lawful"));
                pc.setTitle(pc.getLevel() >= 40 ? rs.getString("title") : "");
                pc.set_other(new L1PcOther());
                pc.set_otherList(new L1PcOtherList(pc));
                int min_interval = rs.getInt("min_interval");
                int max_interval = rs.getInt("max_interval");
                int t = (R.nextInt(1 + max_interval - min_interval) + min_interval) * 1000;
                list.add(pc);
                delays.add(Integer.valueOf(t));
            }
        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
        GeneralThreadPool.get().execute(new Runnable() {
            public void run() {
                for (int i = 0; i < list.size(); i++) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(((Integer) delays.get(i)).intValue());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ((L1FishingPc) list.get(i)).join();
                }
            }
        });
    }

    public static DummyFishingTable get() {
        if (I == null) {
            I = new DummyFishingTable();
        }
        return I;
    }

    public final ArrayList<L1FishingPc> getList() {
        return list;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.DummyFishingTable JD-Core Version: 0.6.2
 */