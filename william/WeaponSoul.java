package william;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 武器劍靈系統
 */
public class WeaponSoul {
    private static final Log _logx = LogFactory.getLog(WeaponSoul.class.getName());
    private static Logger _log = Logger.getLogger(WeaponSoul.class.getName());
    private static WeaponSoul _instance;
    private final HashMap<Integer, L1WeaponSoul> _weaponSoulIndex = new HashMap<>();

    private WeaponSoul() {
        loadWeaponSoul();
    }

    public static WeaponSoul getInstance() {
        if (_instance == null) {
            _instance = new WeaponSoul();
        }
        return _instance;
    }

    private void loadWeaponSoul() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM 寶_武器劍靈系統");
            rs = pstm.executeQuery();
            fillWeaponSoul(rs);
        } catch (SQLException e) {
            _log.log(Level.SEVERE, "error while creating 寶_武器劍靈系統 table", e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    private void fillWeaponSoul(ResultSet rs) throws SQLException {
        PerformanceTimer timer = new PerformanceTimer();
        while (rs.next()) {
            int weapon_id = rs.getInt("weapon_id");
            int soul_exp_limit = rs.getInt("soul_exp_limit");
            int soul_min_exp = rs.getInt("soul_min_exp");
            int soul_gfx_id = rs.getInt("soul_gfx_id");
            double soul_level = rs.getDouble("soul_level");
            double soul_Str = rs.getDouble("soul_str");
            double soul_Con = rs.getDouble("soul_con");
            double soul_Dex = rs.getDouble("soul_dex");
            double soul_Int = rs.getDouble("soul_int");
            double soul_Wis = rs.getDouble("soul_wis");
            int soul_Mp = rs.getInt("soul_mp");
            String soul_name = rs.getString("soul_name");
            L1WeaponSoul weaponSoul = new L1WeaponSoul(weapon_id, soul_exp_limit, soul_min_exp, soul_gfx_id, soul_level, soul_Str, soul_Con, soul_Dex, soul_Int, soul_Wis, soul_Mp, soul_name);
            this._weaponSoulIndex.put(weapon_id, weaponSoul);
        }
        _logx.info("讀取->寶_武器劍靈系統資料數量: " + this._weaponSoulIndex.size() + "(" + timer.get() + "ms)");
    }

    public L1WeaponSoul getTemplate(int weaponId) {
        return (L1WeaponSoul) this._weaponSoulIndex.get(weaponId);
    }

    public L1WeaponSoul[] getWeaponIdList() {
        return (L1WeaponSoul[]) this._weaponSoulIndex.values().toArray(new L1WeaponSoul[0]);
    }
}
