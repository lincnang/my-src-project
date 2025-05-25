package com.add.Tsai;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import static com.lineage.server.datatables.ItemTable.WeaponTypes;

/**
 * 強化武器加成系統
 *
 * @author 台灣JAVA技術老爹
 */
public class NewEnchantSystem {
    private static final Log _log = LogFactory.getLog(NewEnchantSystem.class);
    private static final Map<Integer, NewEnchantSystem> _enchantlist = new HashMap<>();
    private static NewEnchantSystem _instance;
    private int _hit;
    private int _magicdmg;
    // 針對武器編號 2024.4.1 先拿掉
    //	public NewEnchantSystem get(final int weaponId , final int Evolevel) {
    //		try {
    //			if ( _enchantlist.size() > 0) {
    //				for (int i = 0; i <= _enchantlist.size(); i++) {
    //					final NewEnchantSystem list = _enchantlist.get(i);
    //					if (list != null) {
    //						if (list.getWeaponId() == weaponId &&
    //								list.getEnchant() == Evolevel) {
    //							return list;
    //						}
    //					}
    //				}
    //			}
    //		} catch (final Exception e) {
    //			_log.error(e.getLocalizedMessage(), e);
    //		}
    //		return null;
    //	}
    private double _critdmg;
    private int _critrate;
    private int _critgfx;
    private int _weaponid;
    private int _type;
    private int _enchant;
    private int _ran;
    private int _extradmg;
    private int _hp;
    private int _mp;
    private int _expup;
    private int _skillid;
    private int _gfx;
    private int _timegfx;
    private boolean _sjj;
    private int _sjjjilv;
    private int _sjjtexiao;

    public static NewEnchantSystem get() {
        if (_instance == null) {
            _instance = new NewEnchantSystem();
        }
        return _instance;
    }

    public void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection co = null;
        PreparedStatement pm = null;
        ResultSet rs = null;
        try {
            co = DatabaseFactory.get().getConnection();
            pm = co.prepareStatement("SELECT * FROM `系統_強化武器加乘`");
            rs = pm.executeQuery();
            while (rs.next()) {
                final int id = (rs.getInt("序號"));
                NewEnchantSystem en = new NewEnchantSystem();
                en.setWeaponId(rs.getInt("武器編號"));
                en.setType((Integer) WeaponTypes().get(rs.getString("type")));
                en.setEnchant(rs.getInt("Level"));
                en.setRan(rs.getInt("機率"));
                en.setHit(rs.getInt("物理命中提升"));
                en.setExtraDmg(rs.getInt("物理傷害提升"));
                en.setExtraMagicDmg(rs.getInt("魔法傷害提升"));
                en.setCritDmg(rs.getDouble("攻擊暴擊傷害倍率"));
                en.setCritRate(rs.getInt("攻擊暴擊發動機率"));
                en.setCritGfx(rs.getInt("攻擊暴擊發動特效"));
                en.setHp(rs.getInt("血量提升"));
                en.setMp(rs.getInt("魔力提升"));
                en.setExpUp(rs.getInt("經驗值提升"));
                en.setSkillId(rs.getInt("自動施放技能"));
                en.setGfx(rs.getInt("施放特效"));
                en.setTimeGfx(rs.getInt("定時特效"));
                en.setsjj(rs.getBoolean("是否破聖結界"));
                en.setsjjjilv(rs.getInt("破聖結界機率"));
                en.settexiao(rs.getInt("破除聖結界特效"));
                _enchantlist.put(id, en);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
        _log.info("讀取->系統_強化武器加乘數量: " + _enchantlist.size() + "(" + timer.get() + "ms)");
    }

    public NewEnchantSystem get2(final int safeEnchantLevel, final int Evolevel, final int type) {
        try {
            if (_enchantlist.size() > 0) {
                for (int i = _enchantlist.size(); i >= 0; i--) {
                    final NewEnchantSystem list = _enchantlist.get(i);
                    if (list != null) {
                        if (list.getWeaponId() == 0 && (Evolevel - safeEnchantLevel) >= list.getEnchant() && list.getType() == type) {
                            return list;
                        }
                    }
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    public int getHit() {
        return _hit;
    }

    private void setHit(int i) {
        _hit = i;
    }

    public int getExtraMagicDmg() {
        return _magicdmg;
    }

    private void setExtraMagicDmg(int i) {
        _magicdmg = i;
    }

    public double getCritDmg() {
        return _critdmg;
    }

    private void setCritDmg(double i) {
        _critdmg = i;
    }

    public int getCritRate() {
        return _critrate;
    }

    private void setCritRate(int i) {
        _critrate = i;
    }

    public int getCritGfx() {
        return _critgfx;
    }

    private void setCritGfx(int i) {
        _critgfx = i;
    }

    public int getWeaponId() {
        return _weaponid;
    }

    private void setWeaponId(int i) {
        _weaponid = i;
    }

    public int getType() {
        return _type;
    }

    private void setType(int i) {
        _type = i;
    }

    public int getEnchant() {
        return _enchant;
    }

    private void setEnchant(int i) {
        _enchant = i;
    }

    public int getRan() {
        return _ran;
    }

    private void setRan(int i) {
        _ran = i;
    }

    public int getExtraDmg() {
        return _extradmg;
    }

    private void setExtraDmg(int i) {
        _extradmg = i;
    }

    public int getHp() {
        return _hp;
    }

    private void setHp(int i) {
        _hp = i;
    }

    public int getMp() {
        return _mp;
    }

    private void setMp(int i) {
        _mp = i;
    }

    public int getExpUp() {
        return _expup;
    }

    private void setExpUp(int i) {
        _expup = i;
    }

    public int getSkillId() {
        return _skillid;
    }

    private void setSkillId(int i) {
        _skillid = i;
    }

    public int getGfx() {
        return _gfx;
    }

    private void setGfx(int i) {
        _gfx = i;
    }

    public int getTimeGfx() {
        return _timegfx;
    }

    private void setTimeGfx(int i) {
        _timegfx = i;
    }

    public boolean getsjj() {
        return _sjj;
    }

    public void setsjj(boolean i) {
        _sjj = i;
    }

    public int getsjjjilv() {
        return _sjjjilv;
    }

    public void setsjjjilv(int i) {
        _sjjjilv = i;
    }

    public int gettexiao() {
        return _sjjtexiao;
    }

    public void settexiao(int i) {
        _sjjtexiao = i;
    }

    public NewEnchantSystem get(int itemId, int enchantLevel) {
        return null;
    }

    public NewEnchantSystem get2(int enchantLevel) {
        return null;
    }
}
