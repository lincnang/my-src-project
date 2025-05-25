package william;

// 引入必要的外部類和工具

import com.lineage.DatabaseFactory;
import com.lineage.Server;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.SQLUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

// 箭矢特效的管理類
public class ArrowGfxid {

    // 用於存儲數據的列表
    private static ArrayList aData17 = new ArrayList();
    // 防止多次獲取數據的標誌
    private static boolean NO_MORE_GET_DATA17 = false;

    // 類的構造方法
    public ArrowGfxid() {
    }

    // 主函數，程序入口點
    public static void main(String a[]) {
        try {
            // 啟動服務器主程序
            Server.main(null);
        } catch (Exception exception) {
            // 捕捉並忽略例外
        }
    }

    // 處理物品使用的功能
    public static void forItemUSe(L1PcInstance user, int poly) {
        if (!NO_MORE_GET_DATA17) {
            NO_MORE_GET_DATA17 = true;
            getData15b();
        }

        for (int i = 0; i < aData17.size(); i++) {
            ArrayList aTempData = (ArrayList) aData17.get(i);

            if (((Integer) aTempData.get(0)).intValue() == poly) {
                if (!user.hasSkillEffect(64)) {
                    user.setpolyarrow(0);
                    continue;
                }

                if (user.getpolyarrow() != ((Integer) aTempData.get(1)).intValue()) {
                    user.setpolyarrow(((Integer) aTempData.get(1)).intValue());
                    break;
                }
            }
        }
    }

    // 獲取數據的方法
    private static void getData15b() {
        Connection con = null;
        try {
            con = DatabaseFactory.get().getConnection();
            Statement stat = con.createStatement();
            ResultSet rset = stat.executeQuery("SELECT * FROM 系統_變身箭矢特效");

            while (rset.next()) {
                ArrayList aReturn = new ArrayList();
                aReturn.add(0, rset.getInt("polyid"));
                aReturn.add(1, rset.getInt("arrowgfxid"));
                aData17.add(aReturn);
            }

            SQLUtil.close(rset);
            SQLUtil.close(stat);
            SQLUtil.close(con);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
