/**
 * License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE").
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR
 * COPYRIGHT LAW IS PROHIBITED.
 * <p>
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND
 * AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE
 * MAY BE CONSIDERED TO BE A CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 */
package william;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.SQLUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DB化訊息
 *
 * @author william
 */
public class SystemMessage {
    private static Logger _log = Logger.getLogger(SystemMessage.class.getName());
    private static SystemMessage _instance;
    private final HashMap<Integer, L1WilliamSystemMessage> _itemIdIndex = new HashMap<>();

    private SystemMessage() {
        loadSystemMessage();
    }

    public static SystemMessage getInstance() {
        if (_instance == null) {
            _instance = new SystemMessage();
        }
        return _instance;
    }

    private void loadSystemMessage() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM 其他_能力天賦設置");
            rs = pstm.executeQuery();
            fillSystemMessage(rs);
        } catch (SQLException e) {
            _log.log(Level.SEVERE, "error while creating 其他_能力天賦設置 table", e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    private void fillSystemMessage(ResultSet rs) throws SQLException {
        while (rs.next()) {
            int Id = rs.getInt("代碼");
            String Message = rs.getString("文字");
            L1WilliamSystemMessage System_Message = new L1WilliamSystemMessage(Id, Message);
            _itemIdIndex.put(Id, System_Message);
        }
    }

    public L1WilliamSystemMessage getTemplate(int Id) {
        return _itemIdIndex.get(Id);
    }
}
