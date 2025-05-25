package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1ItemUpgrade;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 火神合成系統
 *
 */
public class ItemUpgradeTable {// src016
    private static Log _log = LogFactory.getLog(ItemUpgradeTable.class);
    private static ItemUpgradeTable _instance;
    private final ArrayList<L1ItemUpgrade> _itemUpgradeLists;

    private ItemUpgradeTable() {
        this._itemUpgradeLists = allRefineList();
    }

    public static ItemUpgradeTable getInstance() {
        if (_instance == null) {
            _instance = new ItemUpgradeTable();
        }
        return _instance;
    }

    public static void reload() {
        ItemUpgradeTable oldInstance = _instance;
        _instance = new ItemUpgradeTable();
        oldInstance._itemUpgradeLists.clear();
    }

    private ArrayList<L1ItemUpgrade> allRefineList() {
        ArrayList<L1ItemUpgrade> itemUpgradeList = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("select * from item_upgrade");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int npcId = rs.getInt("npc_id");
                String actionId = rs.getString("action_id");
                String actionName = rs.getString("action_name");
                int upgradeChance = rs.getInt("upgrade_chance");
                int deleteChance = rs.getInt("delete_chance");
                int newItemId = rs.getInt("new_item_id");
                int mainItemId = rs.getInt("main_item_id");
                int mainItemCount = rs.getInt("main_item_count");
                String needItemId = rs.getString("need_item_id");
                String needCounts = rs.getString("need_counts");
                String plusItemId = rs.getString("plus_item_id");
                String plusCounts = rs.getString("pulus_counts");
                String plusAddChance = rs.getString("pulus_add_chance");
                String showSuccessHtmlId = rs.getString("show_success_html_id");
                String showFailureHtmlId = rs.getString("show_failure_html_id");
                String showFailureDeleteHtmlId = rs.getString("show_failure_delete_html_id");
                int producer = rs.getInt("producer");
                L1ItemUpgrade itemUpgrade = new L1ItemUpgrade();
                itemUpgrade.setNpcId(npcId);
                itemUpgrade.setActionName(actionName);
                itemUpgrade.setActionId(actionId);
                itemUpgrade.setUpgradeChance(upgradeChance);
                itemUpgrade.setDeleteChance(deleteChance);
                itemUpgrade.setNewItemId(newItemId);
                itemUpgrade.setMainItemId(mainItemId);
                itemUpgrade.setMainItemCount(mainItemCount);
                itemUpgrade.setNeedCounts(needCounts);
                itemUpgrade.setNeedItemId(needItemId);
                itemUpgrade.setPlusItemId(plusItemId);
                itemUpgrade.setPlusCounts(plusCounts);
                itemUpgrade.setPlusAddChance(plusAddChance);
                itemUpgrade.setShowSuccessHtmlId(showSuccessHtmlId);
                itemUpgrade.setShowFailureHtmlId(showFailureHtmlId);
                itemUpgrade.setShowFailureDeleteHtmlId(showFailureDeleteHtmlId);
                itemUpgrade.setProducer(producer);
                itemUpgradeList.add(itemUpgrade);
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
        return itemUpgradeList;
    }

    public L1ItemUpgrade getItemUpgrade(int npcId, String actionId) {
        for (L1ItemUpgrade itemUpgrade : this._itemUpgradeLists) {
            if ((itemUpgrade.getNpcId() == npcId) && (actionId.equals(itemUpgrade.getActionId()))) {
                return itemUpgrade;
            }
        }
        return null;
    }
}
