package william;

// 引入所需的類別和套件

import com.lineage.DatabaseFactory;
import com.lineage.Server;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_BlueMessage;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

// 定義 login_Artiface1 類別
public class login_Artiface1 {
    // 定義常量 TOKEN，作為分隔符
    public static final String TOKEN = ",";

    // 用於存儲從數據庫讀取的數據，每個子列表代表一條記錄
    private static ArrayList<ArrayList<Object>> aData = new ArrayList<>();

    // 標誌位，表示是否已經構建數據
    private static boolean BUILD_DATA = false;

    // 單例模式的實例
    private static login_Artiface1 _instance;

    // 獲取單例實例的方法
    public static login_Artiface1 getInstance() {
        if (_instance == null) {
            _instance = new login_Artiface1();
        }
        return _instance;
    }

    // 主方法，啟動伺服器
    public static void main(String a[]) {
        while (true) {
            try {
                Server.main(null);
            } catch (Exception ex) {
                // 捕捉並忽略所有異常，避免伺服器崩潰
            }
        }
    }

    /**
     * 強化裝甲的方法
     *
     * @param pc   玩家實例
     * @param type 強化類型（1：武器，2：防具）
     */
    public static void forIntensifyArmor(L1PcInstance pc, int type) {
        ArrayList<Object> aTempData = null;

        // 如果數據尚未構建，則從數據庫讀取數據
        if (!BUILD_DATA) {
            BUILD_DATA = true;
            getData();
        }

        // 遍歷所有數據記錄
        for (ArrayList<Object> aDatum : aData) {
            aTempData = (ArrayList<Object>) aDatum;
            switch (type) {
                case 1: // 處理武器強化
                    // 檢查是否已達到停止強化的等級，並發送停止消息
                    if ((Integer) aTempData.get(12) == 1
                            && pc.get_other().getLv_Artifact() == (Integer) aTempData.get(0)
                            && (Integer) aTempData.get(10) > 0) {
                        pc.sendPackets(new S_SystemMessage((String) aTempData.get(11)));
                        return;
                    }

                    // 檢查當前等級是否符合強化條件，並發送提示消息
                    if (pc.get_other().getLv_Artifact() == (Integer) aTempData.get(0)
                            && (Integer) aTempData.get(12) == 1) {
                        pc.sendPackets(new S_ServerMessage((String) aTempData.get(6)));
                    }

                    // 如果條件滿足，進行強化操作
                    if (pc.get_other().getLv_Artifact() == (Integer) aTempData.get(0)
                            && pc.get_other().getArtifact() >= (Integer) aTempData.get(1)
                            && (Integer) aTempData.get(12) == 1) {

                        // 增加武器等級
                        pc.get_other().setLv_Artifact(pc.get_other().getLv_Artifact() + 1);
                        // 減少所需的武器經驗值
                        pc.get_other().setArtifact(pc.get_other().getArtifact() - (Integer) aTempData.get(1));

                        // 向全服廣播強化成功的消息
                        World.get().broadcastPacketToAll(new S_BlueMessage(166, pc.getName() + (String) aTempData.get(9)));

                        // 發送相關消息給玩家
                        pc.sendPackets(new S_ServerMessage((String) aTempData.get(6)));
                        pc.sendPackets(new S_ServerMessage((String) aTempData.get(7)));
                    }
                    break;

                case 2: // 處理防具強化
                    // 檢查是否已達到停止強化的等級，並發送停止消息
                    if ((Integer) aTempData.get(12) == 0
                            && pc.get_other().getLv_Redmg_Artifact() == (Integer) aTempData.get(2)
                            && (Integer) aTempData.get(10) > 0) {
                        pc.sendPackets(new S_SystemMessage((String) aTempData.get(11)));
                        return;
                    }

                    // 檢查當前等級是否符合強化條件，並發送提示消息
                    if (pc.get_other().getLv_Redmg_Artifact() == (Integer) aTempData.get(2)
                            && (Integer) aTempData.get(12) == 0) {
                        pc.sendPackets(new S_ServerMessage((String) aTempData.get(6)));
                    }

                    // 如果條件滿足，進行強化操作
                    if (pc.get_other().getLv_Redmg_Artifact() == (Integer) aTempData.get(2)
                            && pc.get_other().getArtifact1() >= (Integer) aTempData.get(3)
                            && (Integer) aTempData.get(12) == 0) {

                        // 增加防具等級
                        pc.get_other().setLv_Redmg_Artifact(pc.get_other().getLv_Redmg_Artifact() + 1);
                        // 減少所需的防具經驗值
                        pc.get_other().setArtifact1(pc.get_other().getArtifact1() - (Integer) aTempData.get(3));

                        // 向全服廣播強化成功的消息
                        World.get().broadcastPacketToAll(new S_BlueMessage(166, pc.getName() + (String) aTempData.get(9)));

                        // 發送相關消息給玩家
                        pc.sendPackets(new S_ServerMessage((String) aTempData.get(7)));
                    }
                    break;
            }
        }
    }

    /**
     * 從數據庫獲取強化等級和相關設置數據
     */
    private static void getData() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // 獲取數據庫連接
            conn = DatabaseFactory.get().getConnection();
            // 準備 SQL 查詢語句，這裡查詢的是「寶_武防歷練值階級設置」表
            pstmt = conn.prepareStatement("SELECT * FROM 寶_武防歷練值階級設置");
            rs = pstmt.executeQuery();
            ArrayList<Object> aReturn = null;

            if (rs != null) {
                // 遍歷查詢結果，將每條記錄存入 aData 列表
                while (rs.next()) {
                    aReturn = new ArrayList<>();
                    aReturn.add(0, rs.getInt("Lv_Artifact_weapon")); // 武器等級
                    aReturn.add(1, rs.getInt("Artifact_weapon")); // 武器所需經驗
                    aReturn.add(2, rs.getInt("Lv_Artifact_armor")); // 防具等級
                    aReturn.add(3, rs.getInt("Artifact_armor")); // 防具所需經驗
                    aReturn.add(4, rs.getInt("Lv_Artifact_other")); // 其他等級（可能用於擴展）
                    aReturn.add(5, rs.getInt("Artifact_other")); // 其他所需經驗
                    aReturn.add(6, rs.getString("message")); // 提示消息
                    aReturn.add(7, rs.getString("message1")); // 成功消息
                    aReturn.add(8, rs.getInt("check_world_mesage")); // 是否檢查全服消息
                    aReturn.add(9, rs.getString("world_message")); // 全服廣播消息
                    aReturn.add(10, rs.getInt("lv_stop")); // 停止強化的等級
                    aReturn.add(11, rs.getString("stop_message")); // 停止強化的消息
                    aReturn.add(12, rs.getInt("type")); // 類型（1：武器，0：防具）

                    // 將該記錄加入數據列表
                    aData.add(aReturn);
                }
            }
        } catch (SQLException e) {
            // 處理 SQL 異常（此處未具體處理，可加入日誌或錯誤處理）
        } finally {
            // 關閉結果集、聲明和連接，釋放資源
            SQLUtil.close(rs);
            SQLUtil.close(pstmt);
            SQLUtil.close(conn);
        }
    }
}
