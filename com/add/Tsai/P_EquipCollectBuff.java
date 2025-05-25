package com.add.Tsai;

// 引入必要的類別

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.*;
import com.lineage.server.utils.SQLUtil;

import java.sql.*;
import java.util.ArrayList;

public class P_EquipCollectBuff {
    // 定義常量分隔符
    public static final String TOKEN = ",";
    // 儲存從資料庫讀取的資料，每筆資料是一個包含多個欄位的ArrayList
    private static ArrayList<ArrayList<Object>> aData = new ArrayList<>();
    // 標記是否已經建構資料
    private static boolean BUILD_DATA = false;
    // 單例模式的實例
    private static P_EquipCollectBuff _instance;

    // 獲取單例實例的方法
    public static P_EquipCollectBuff getInstance() {
        if (_instance == null) {
            _instance = new P_EquipCollectBuff();
        }
        return _instance;
    }


    /**
     * 根據玩家的當前任務步驟加載相應的BUFF狀態
     *
     * @param pc   玩家實例
     * @param step 任務步驟
     */
    public static void loadStatus(L1PcInstance pc, int step) {
        ArrayList<Object> aTempData = null;
        // 如果資料尚未建構，則建構資料
        if (!BUILD_DATA) {
            BUILD_DATA = true;
            getData();
        }
        // 遍歷所有資料
        for (ArrayList<Object> aDatum : aData) {
            aTempData = aDatum;
            // 檢查玩家在該任務的步驟是否匹配
            if (pc.getQuest().get_step((Integer) aTempData.get(0)) == step) {
                // 根據資料中的各項屬性值，為玩家添加相應的BUFF
                if ((Integer) aTempData.get(2) != 0) {
                    pc.addDmgup((Integer) aTempData.get(2));
                }
                if ((Integer) aTempData.get(3) != 0) {
                    pc.addBowDmgup((Integer) aTempData.get(3));
                }
                if ((Integer) aTempData.get(4) != 0) {
                    pc.addHitup((Integer) aTempData.get(4));
                }
                if ((Integer) aTempData.get(5) != 0) {
                    pc.addBowHit((Integer) aTempData.get(5));
                }
                if ((Integer) aTempData.get(6) != 0) {
                    pc.addSp((Integer) aTempData.get(6));
                }
                if ((Integer) aTempData.get(7) != 0) {
                    pc.addStr((Integer) aTempData.get(7));
                }
                if ((Integer) aTempData.get(8) != 0) {
                    pc.addDex((Integer) aTempData.get(8));
                }
                if ((Integer) aTempData.get(9) != 0) {
                    pc.addInt((Integer) aTempData.get(9));
                }
                if ((Integer) aTempData.get(10) != 0) {
                    pc.addCon((Integer) aTempData.get(10));
                }
                if ((Integer) aTempData.get(11) != 0) {
                    pc.addCha((Integer) aTempData.get(11));
                }
                if ((Integer) aTempData.get(12) != 0) {
                    pc.addWis((Integer) aTempData.get(12));
                }
                if ((Integer) aTempData.get(13) != 0) {
                    // 增加當前生命值並增加最大生命值
                    pc.setCurrentHp(pc.getCurrentHp() + (Integer) aTempData.get(13));
                    pc.addMaxHp((Integer) aTempData.get(13));
                }
                if ((Integer) aTempData.get(14) != 0) {
                    // 增加當前魔法值並增加最大魔法值
                    pc.setCurrentMp(pc.getCurrentMp() + (Integer) aTempData.get(14));
                    pc.addMaxMp((Integer) aTempData.get(14));
                }
                if ((Integer) aTempData.get(15) != 0) {
                    pc.addAc((Integer) aTempData.get(15));
                }
                if ((Integer) aTempData.get(16) != 0) {
                    pc.addMr((Integer) aTempData.get(16));
                }
                if ((Integer) aTempData.get(17) != 0) {
                    pc.addHpr((Integer) aTempData.get(17));
                }
                if ((Integer) aTempData.get(18) != 0) {
                    pc.addMpr((Integer) aTempData.get(18));
                }
                if ((Integer) aTempData.get(19) != 0) {
                    pc.add_up_hp_potion((Integer) aTempData.get(19));
                }
                if ((Integer) aTempData.get(20) != 0) {
                    pc.set_expadd((Integer) aTempData.get(20));
                }
                if ((Integer) aTempData.get(21) != 0) {
                    pc.addWeightReduction((Integer) aTempData.get(21));
                }
                if ((Integer) aTempData.get(22) != 0) {
                    pc.addRegistStun((Integer) aTempData.get(22));
                }
                if ((Integer) aTempData.get(23) != 0) {
                    // 設置玩家的PVP攻擊力
                    pc.setPvpDmg((Integer) aTempData.get(23));
                }
                if ((Integer) aTempData.get(24) != 0) {
                    // 設置玩家的PVP減免傷害
                    pc.setPvpDmg_R((Integer) aTempData.get(24));
                }
                if ((Integer) aTempData.get(25) != 0) {
                    // 增加藥水回復指定量
                    pc.add_uhp_number((Integer) aTempData.get(25));
                }
                if ((Integer) aTempData.get(26) != 0) {
                    // 近距離爆擊
                    pc.addCloseCritical((Integer) aTempData.get(26));
                }
                if ((Integer) aTempData.get(27) != 0) {
                    // 遠距離爆擊
                    pc.addBowCritical((Integer) aTempData.get(27));
                }
                if ((Integer) aTempData.get(28) != 0) {  // PVE攻擊

                    pc.addDamageReductionPVE((Integer) aTempData.get(28));
                }
                if ((Integer) aTempData.get(29) != 0) {  // PVE減免

                    pc.adddollDamageReductionByArmor((Integer) aTempData.get(29));
                }

                // 發送更新封包給玩家，刷新其狀態
                pc.sendPackets(new S_SPMR(pc));
                pc.sendPackets(new S_OwnCharStatus(pc));
                pc.sendPackets(new S_OwnCharStatus2(pc));
                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
            }
        }
    }

    /**
     * 從資料庫加載BUFF資料並存入aData
     */
    private static void getData() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // 獲取資料庫連接
            conn = DatabaseFactory.get().getConnection();
            // 準備SQL查詢語句，選取所有BUFF相關的資料
            pstmt = conn.prepareStatement("SELECT * FROM `系統_成就系統_buff`");
            rs = pstmt.executeQuery();
            ArrayList<Object> aReturn = null;
            if (rs != null) {
                // 遍歷結果集，將每一行資料加入aData
                while (rs.next()) {
                    aReturn = new ArrayList<>();
                    aReturn.add(0, rs.getInt("Quest")); // 任務ID
                    aReturn.add(1, 0); // 預留欄位，初始為0
                    aReturn.add(2, rs.getInt("近距離傷害")); // 近戰傷害加成
                    aReturn.add(3, rs.getInt("遠距離傷害")); // 弓箭傷害加成
                    aReturn.add(4, rs.getInt("近距離命中")); // 近戰命中加成
                    aReturn.add(5, rs.getInt("遠距離命中")); // 弓箭命中加成
                    aReturn.add(6, rs.getInt("魔功")); // 魔力加成
                    aReturn.add(7, rs.getInt("力量")); // 力量加成
                    aReturn.add(8, rs.getInt("敏捷")); // 敏捷加成
                    aReturn.add(9, rs.getInt("智力")); // 智力加成
                    aReturn.add(10, rs.getInt("體質")); // 體質加成
                    aReturn.add(11, rs.getInt("魅力")); // 魅力加成
                    aReturn.add(12, rs.getInt("精神")); // 精神加成
                    aReturn.add(13, rs.getInt("血量")); // 生命值加成
                    aReturn.add(14, rs.getInt("魔量")); // 魔法值加成
                    aReturn.add(15, rs.getInt("防禦")); // 防禦等級加成
                    aReturn.add(16, rs.getInt("魔防")); // 魔法防禦加成
                    aReturn.add(17, rs.getInt("回血量")); // 生命恢復速度加成
                    aReturn.add(18, rs.getInt("回魔量")); // 魔法恢復速度加成
                    aReturn.add(19, rs.getInt("增加回復量%")); // 增加藥水回復量%
                    aReturn.add(20, rs.getInt("exp")); // 經驗值加成
                    aReturn.add(21, rs.getInt("重量")); // 負重減免
                    aReturn.add(22, rs.getInt("昏迷抗性")); // 暈眩抵抗
                    aReturn.add(23, rs.getInt("PVP攻擊")); // PVP攻擊力
                    aReturn.add(24, rs.getInt("PVP減免傷害")); // PVP傷害減免
                    aReturn.add(25, rs.getInt("增加回復指定量")); // 藥水回復指定量
                    aReturn.add(26, rs.getInt("近距離爆擊")); // 近距離爆擊
                    aReturn.add(27, rs.getInt("遠距離爆擊")); // 遠距離爆擊
                    aReturn.add(28, rs.getInt("PVE_攻擊"));
                    aReturn.add(29, rs.getInt("PVE_減免"));
                    // 將該筆資料加入aData
                    aData.add(aReturn);
                }
            }
        } catch (SQLException var7) {
            // 捕捉SQL異常（可進一步添加日誌記錄）
        } finally {
            // 確保資源被正確關閉
            SQLUtil.close(rs);
            SQLUtil.close((Statement) pstmt);
            SQLUtil.close(conn);
        }
    }
}
