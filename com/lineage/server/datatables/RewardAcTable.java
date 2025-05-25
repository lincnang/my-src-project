package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * 防禦等級獎勵系統
 */
public class RewardAcTable {

    // 定義一個日誌物件，用於輸出日誌訊息
    private static final Log _log = LogFactory.getLog(RewardAcTable.class);
    // 獎勵系統是否啓動的標誌
    public static boolean START = false;
    // 用於儲存從資料庫讀取的防禦等級獎勵訊息的列表，每個元素是一個包含獎勵訊息的ArrayList
    private static ArrayList<ArrayList<Object>> _array = new ArrayList<ArrayList<Object>>();
    // 單例模式的實例變數
    private static RewardAcTable _instance;

    /**
     * 私有建構子，初始化並載入資料
     */
    private RewardAcTable() {
        getData(); // 載入獎勵資料
        _log.info("讀取->防禦等級獎勵->" + _array.size());
        if (_array.size() <= 0) { // 如果資料爲空，清除記憶體
            _array.clear();
            _array = null;
        } else {
            START = true; // 資料載入成功，設定狀態爲啓動
        }
    }

    /**
     * 獲取單例實例
     */
    public static RewardAcTable get() {
        if (_instance == null) {
            _instance = new RewardAcTable();
        }
        return _instance;
    }

    /**
     * 根據玩家的防禦等級（AC）發放獎勵
     */
    public static void forAc(final L1PcInstance pc) {
        if (_array == null) { // 如果資料爲空，返回
            return;
        }
        ArrayList<Object> aTempData = null;

        final int ac = pc.getAc() * -1; // 取得玩家的防禦等級並取負值
        final int[] data = new int[2]; // 用於儲存物理傷害減免和魔法傷害減免的資料

        int i = 0;
        int min = 0;
        for (int max = 0; i < _array.size(); i++) { // 遍歷所有獎勵資料
            aTempData = _array.get(i);
            min = ((Integer) aTempData.get(0)).intValue(); // 取得最小防禦等級
            max = ((Integer) aTempData.get(1)).intValue(); // 取得最大防禦等級

            // 根據玩家職業和防禦等級判斷是否符合條件併發放獎勵
            // 王族
            if ((pc.isCrown()) && (ac <= min) && (ac >= max) && (((Integer) aTempData.get(2)).intValue() != 0)) {
                if (((Integer) aTempData.get(10)).intValue() != 0) {
                    data[0] += ((Integer) aTempData.get(10)).intValue();
                }
                if (((Integer) aTempData.get(11)).intValue() != 0) {
                    data[1] += ((Integer) aTempData.get(11)).intValue();
                }
                // 騎士
            } else if ((pc.isKnight()) && (ac <= min) && (ac >= max) && (((Integer) aTempData.get(3)).intValue() != 0)) {
                if (((Integer) aTempData.get(10)).intValue() != 0) {
                    data[0] += ((Integer) aTempData.get(10)).intValue();
                }
                if (((Integer) aTempData.get(11)).intValue() != 0) {
                    data[1] += ((Integer) aTempData.get(11)).intValue();
                }
                // 法師
            } else if ((pc.isWizard()) && (ac <= min) && (ac >= max) && (((Integer) aTempData.get(4)).intValue() != 0)) {
                if (((Integer) aTempData.get(10)).intValue() != 0) {
                    data[0] += ((Integer) aTempData.get(10)).intValue();
                }
                if (((Integer) aTempData.get(11)).intValue() != 0) {
                    data[1] += ((Integer) aTempData.get(11)).intValue();
                }
                // 精靈
            } else if ((pc.isElf()) && (ac <= min) && (ac >= max) && (((Integer) aTempData.get(5)).intValue() != 0)) {
                if (((Integer) aTempData.get(10)).intValue() != 0) {
                    data[0] += ((Integer) aTempData.get(10)).intValue();
                }
                if (((Integer) aTempData.get(11)).intValue() != 0) {
                    data[1] += ((Integer) aTempData.get(11)).intValue();
                }
                // 黑暗妖精
            } else if ((pc.isDarkelf()) && (ac <= min) && (ac >= max) && (((Integer) aTempData.get(6)).intValue() != 0)) {
                if (((Integer) aTempData.get(10)).intValue() != 0) {
                    data[0] += ((Integer) aTempData.get(10)).intValue();
                }
                if (((Integer) aTempData.get(11)).intValue() != 0) {
                    data[1] += ((Integer) aTempData.get(11)).intValue();
                }
                // 龍騎士
            } else if ((pc.isDragonKnight()) && (ac <= min) && (ac >= max) && (((Integer) aTempData.get(7)).intValue() != 0)) {
                if (((Integer) aTempData.get(10)).intValue() != 0) {
                    data[0] += ((Integer) aTempData.get(10)).intValue();
                }
                if (((Integer) aTempData.get(11)).intValue() != 0) {
                    data[1] += ((Integer) aTempData.get(11)).intValue();
                }
                // 幻術士
            } else if ((pc.isIllusionist()) && (ac <= min) && (ac >= max) && (((Integer) aTempData.get(8)).intValue() != 0)) {
                if (((Integer) aTempData.get(10)).intValue() != 0) {
                    data[0] += ((Integer) aTempData.get(10)).intValue();
                }
                if (((Integer) aTempData.get(11)).intValue() != 0) {
                    data[1] += ((Integer) aTempData.get(11)).intValue();
                }
                // 戰士
            } else if ((pc.isWarrior()) && (ac <= min) && (ac >= max) && (((Integer) aTempData.get(9)).intValue() != 0)) {
                if (((Integer) aTempData.get(10)).intValue() != 0) {
                    data[0] += ((Integer) aTempData.get(10)).intValue();
                }
                if (((Integer) aTempData.get(11)).intValue() != 0) {
                    data[1] += ((Integer) aTempData.get(11)).intValue();
                }
            }
        }
        /**
         * 防禦等級獎勵系統
         */
        boolean up = false; // 標誌獎勵是否更新

        final int[] data2 = pc.getReward_Ac(); // 取得玩家當前的防禦獎勵資料
        if (data2[0] != 0) { // 如果當前物理傷害減免不爲0
            if (data[0] > data2[0]) { // 如果新計算的物理傷害減免大於當前值
                final int c = data[0] - data2[0];
                pc.addDmgR(c);
                pc.sendPackets(new S_SystemMessage("物理傷害減免增加 " + c + "點。"));
                up = true;
            } else if (data[0] < data2[0]) { // 如果新計算的物理傷害減免小於當前值
                final int c = data2[0] - data[0];
                pc.addDmgR(-c);
                pc.sendPackets(new S_SystemMessage("物理傷害減免減少 " + c + "點。"));
                up = true;
            }
        } else if (data[0] != 0) { // 如果當前物理傷害減免爲0但新值不爲0
            pc.addDmgR(data[0]);
            pc.sendPackets(new S_SystemMessage("物理傷害減免增加 " + data[0] + " 點。"));
            up = true;
        }

        if (data2[1] != 0) { // 如果當前魔法傷害減免不爲0
            if (data[1] > data2[1]) { // 如果新計算的魔法傷害減免大於當前值
                final int c = data[1] - data2[1];
                pc.addMagicR(c);
                pc.sendPackets(new S_SystemMessage("魔法傷害減免增加 " + c + "點。"));
                up = true;
            } else if (data[1] < data2[1]) { // 如果新計算的魔法傷害減免小於當前值
                final int c = data2[1] - data[1];
                pc.addMagicR(-c);
                pc.sendPackets(new S_SystemMessage("魔法傷害減免減少 " + c + "點。"));
                up = true;
            }
        } else if (data[1] != 0) { // 如果當前魔法傷害減免爲0但新值不爲0
            pc.addMagicR(data[1]);
            pc.sendPackets(new S_SystemMessage("魔法傷害減免增加 " + data[1] + " 點。"));
            up = true;
        }

        if (up) { // 如果獎勵發生了更新
            pc.setReward_Ac(data); // 更新玩家的獎勵資料
        }
    }

    /**
     * 返回所有防禦等級獎勵的資料列表
     */
    public static ArrayList<ArrayList<Object>> getSetList() {
        return _array;
    }

    /**
     * 從資料庫中讀取防禦等級獎勵資料並填充到ArrayList中
     */
    private void getData() {
        Connection cn = null; // 資料庫連接物件
        Statement ps = null; // SQL語句執行物件
        ResultSet rset = null; // SQL查詢結果集
        try {
            cn = DatabaseFactory.get().getConnection(); // 取得資料庫連接
            ps = cn.createStatement(); // 建立SQL執行語句
            rset = ps.executeQuery("SELECT * FROM 系統_強化自身防禦加乘"); // 執行查詢語句

            while (rset.next()) { // 遍歷結果集
                final ArrayList<Object> aReturn = new ArrayList<Object>(); // 建立一個新的ArrayList用於儲存每條記錄的資料

                // 將查詢結果存入ArrayList
                aReturn.add(0, new Integer(rset.getInt("MinAc"))); // 最小AC值
                aReturn.add(1, new Integer(rset.getInt("MaxAc"))); // 最大AC值
                aReturn.add(2, new Integer(rset.getInt("Give_Royal"))); // 王族獎勵
                aReturn.add(3, new Integer(rset.getInt("Give_Knight"))); // 騎士獎勵
                aReturn.add(4, new Integer(rset.getInt("Give_Mage"))); // 法師獎勵
                aReturn.add(5, new Integer(rset.getInt("Give_Elf"))); // 精靈獎勵
                aReturn.add(6, new Integer(rset.getInt("Give_Darkelf"))); // 黑暗妖精獎勵
                aReturn.add(7, new Integer(rset.getInt("Give_DragonKnight"))); // 龍騎士獎勵
                aReturn.add(8, new Integer(rset.getInt("Give_Illusionist"))); // 幻術士獎勵
                aReturn.add(9, new Integer(rset.getInt("Give_Warrior"))); // 戰士獎勵
                aReturn.add(10, new Integer(rset.getInt("ReductionDmg"))); // 物理傷害減免
                aReturn.add(11, new Integer(rset.getInt("MagicReductionDmg"))); // 魔法傷害減免
                _array.add(aReturn); // 將每條記錄的ArrayList添加到主列表中
            }

        } catch (final SQLException e) { // 捕捉SQL異常
            _log.error(e.getLocalizedMessage(), e);

        } finally {
            SQLUtil.close(rset); // 關閉結果集
            SQLUtil.close(ps); // 關閉執行物件
            SQLUtil.close(cn); // 關閉連接物件
        }
    }
}