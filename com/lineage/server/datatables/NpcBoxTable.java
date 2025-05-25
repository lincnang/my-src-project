package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1ItemBox;
import com.lineage.server.templates.L1NpcBox;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 寶箱NpcId編號
 *
 */
public class NpcBoxTable {
    private static final Log _log = LogFactory.getLog(NpcBoxTable.class);
    private static final Map<Integer, L1NpcBox> _datatable = new HashMap<Integer, L1NpcBox>();
    private static NpcBoxTable _instance;

    public static NpcBoxTable get() {
        if (_instance == null) {
            _instance = new NpcBoxTable();
        }
        return _instance;
    }

    private static int[] getArray(final String s) {
        final StringTokenizer st = new StringTokenizer(s, ",");
        final int iSize = st.countTokens();
        String sTemp = null;
        final int[] iReturn = new int[iSize];
        for (int i = 0; i < iSize; i++) {
            sTemp = st.nextToken();
            iReturn[i] = Integer.parseInt(sTemp);
        }
        return iReturn;
    }

    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection co = null;
        PreparedStatement pm = null;
        ResultSet rs = null;
        try {
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement("SELECT * FROM 系統_怪物多物品機率掉落");
            rs = pm.executeQuery();
            while (rs.next()) {
                final int npcId = rs.getInt("Npc編號");//寶箱NpcId編號
                if (NpcTable.get().getTemplate(npcId) == null) {
                    _log.error("NPC寶箱資料編號: " + npcId + " 不存在資料庫中!");
                } else {
                    List<Integer> mobNpcIdList = null;
                    List<L1ItemBox> createItemBoxes = null;
                    /**開出怪物的NpcId列表 (可用逗號區隔)*/
                    final String npc_list = rs.getString("開出怪物編號");
                    if (npc_list != null && !npc_list.isEmpty()) {
                        mobNpcIdList = new ArrayList<Integer>();
                        for (final String each : npc_list.split(",")) {
                            mobNpcIdList.add(Integer.parseInt(each));
                        }
                    }
                    final int[] chance = getArray(rs.getString("機率"));
                    final int[] itemid = getArray(rs.getString("獲得道具編號"));
                    final int[] count = getArray(rs.getString("數量"));
                    final int mobchance = rs.getInt("開出怪物機率");
                    final int mobtime = rs.getInt("存在時間");
                    if (chance.length == itemid.length && itemid.length == count.length) {
                        createItemBoxes = new ArrayList<L1ItemBox>();
                        createItemBoxes.add(new L1ItemBox(chance, itemid, count, mobchance, mobtime));
                    } else {
                        _log.info("NPC寶箱資料設置異常(道具數據長度不符): " + npcId);
                    }
                    final L1NpcBox npcBox = new L1NpcBox(rs.getInt("開啟道具編號"),//開啟寶箱需要的鑰匙道具編號
                            rs.getInt("重置最短時間"),//寶箱重置最短時間 (單位:秒)
                            rs.getInt("重置最長時間")//寶箱重置最長時間 (單位:秒)
                            , createItemBoxes, mobNpcIdList);
                    _datatable.put(npcId, npcBox);
                }
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
        _log.info("讀取->NPC寶箱資料數量: " + _datatable.size() + "(" + timer.get() + "ms)");
    }

    public L1NpcBox getTemplate(final int npcId) {
        return _datatable.get(npcId);
    }
}
