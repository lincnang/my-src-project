/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.lineage.server.datatables;
/**
 * [仿正原碼] 無限大賽掉落物品
 */

import com.lineage.DatabaseFactory;
import com.lineage.server.model.L1UbSupplie;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.utils.collections.Lists;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UbSupplies {
    private static UbSupplies _instance;
    private static Logger _log = Logger.getLogger(UbSupplies.class.getName());
    private final List<L1UbSupplie> _ubSupplies = Lists.newList();

    private UbSupplies() {
        load();
    }

    public static UbSupplies getInstance() {
        if (_instance == null) {
            _instance = new UbSupplies();
        }
        return _instance;
    }

    public void load() {
        L1UbSupplie us = null;
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("select * from ub_supplies ORDER BY ub_id,ub_round,ub_item_id");
            rs = pstm.executeQuery();
            while (rs.next()) {
                us = new L1UbSupplie();
                us.setUbId(rs.getInt("ub_id"));
                us.setUbName(rs.getString("ub_name"));
                us.setUbRound(rs.getInt("ub_round"));
                us.setUbItemId(rs.getInt("ub_item_id"));
                us.setUbItemStackCont(rs.getInt("ub_item_stackcont"));
                us.setUbItemCont(rs.getInt("ub_item_cont"));
                us.setUbItemBless(rs.getInt("ub_item_bless"));
                _ubSupplies.add(us);
            }
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public List<L1UbSupplie> getUbSupplies(int id) {
        List<L1UbSupplie> temp = Lists.newList();
        for (L1UbSupplie t : _ubSupplies) {
            if (t.getUbId() == id) {
                temp.add(t);
            }
        }
        return temp;
    }
}
