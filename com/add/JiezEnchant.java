package com.add;

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

/**
 * 強化戒指加成系統
 *

 */
public class JiezEnchant {
    private static final Log _log = LogFactory.getLog(JiezEnchant.class);
    private static final Map<Integer, JiezEnchant> _enchantlist = new HashMap<>();
    private static JiezEnchant _instance;
    private int _armorid;
    private int _enchant;
    private int _str;
    private int _dex;
    private int _con;
    private int _int;
    private int _wis;
    private int _cha;
    private int _ac;
    private int _dmgr;
    private int _hp;
    private int _mp;

    public static JiezEnchant get() {
        if (_instance == null) {
            _instance = new JiezEnchant();
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
            pm = co.prepareStatement("SELECT * FROM `系統_強化戒指加乘`");
            rs = pm.executeQuery();
            while (rs.next()) {
                final int id = (rs.getInt("序號"));
                JiezEnchant ae = new JiezEnchant();
                ae.setArmorId(rs.getInt("防具編號"));
                ae.setEnchant(rs.getInt("強化值"));
                ae.setStr(rs.getInt("力量增加"));
                ae.setCon(rs.getInt("體質增加"));
                ae.setDex(rs.getInt("敏捷增加"));
                ae.setInt(rs.getInt("智力增加"));
                ae.setWis(rs.getInt("精神增加"));
                ae.setCha(rs.getInt("魅力增加"));
                ae.setAc(rs.getInt("增加防禦"));
                ae.setDmgR(rs.getInt("傷害減免"));
                ae.setHp(rs.getInt("血量提升"));
                ae.setMp(rs.getInt("魔力提升"));
                //ae.setExpUp(rs.getInt("經驗值提升"));
                //ae.setTimeGfx(rs.getInt("定時特效"));
                _enchantlist.put(id, ae);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pm);
            SQLUtil.close(co);
        }
        _log.info("讀取->[系統_強化戒指加乘]: " + _enchantlist.size() + "(" + timer.get() + "ms)");
    }

    public JiezEnchant get(final int armor, final int Evolevel) {
        try {
            if (Evolevel == 0) {
                return null;
            }
            if (_enchantlist.size() > 0) {
                for (int i = 0; i <= _enchantlist.size(); i++) {
                    final JiezEnchant list = _enchantlist.get(i);
                    if (list != null) {
                        if (list.getArmorId() == armor && list.getEnchant() == Evolevel) {
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

    public JiezEnchant get2(final int Evolevel) {
        try {
            if (Evolevel == 0) {
                return null;
            }
            if (_enchantlist.size() > 0) {
                for (int i = 0; i <= _enchantlist.size(); i++) {
                    final JiezEnchant list = _enchantlist.get(i);
                    if (list != null) {
                        if (list.getArmorId() == 0 && list.getEnchant() == Evolevel) {
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

    public int getArmorId() {
        return _armorid;
    }

    private void setArmorId(int i) {
        _armorid = i;
    }

    public int getEnchant() {
        return _enchant;
    }

    private void setEnchant(int i) {
        _enchant = i;
    }

    public int getStr() {
        return _str;
    }

    private void setStr(int i) {
        _str = i;
    }

    public int getDex() {
        return _dex;
    }

    private void setDex(int i) {
        _dex = i;
    }

    public int getCon() {
        return _con;
    }

    private void setCon(int i) {
        _con = i;
    }

    public int getInt() {
        return _int;
    }

    private void setInt(int i) {
        _int = i;
    }

    public int getWis() {
        return _wis;
    }

    private void setWis(int i) {
        _wis = i;
    }

    public int getCha() {
        return _cha;
    }

    private void setCha(int i) {
        _cha = i;
    }

    public int getAc() {
        return _ac;
    }

    private void setAc(int i) {
        _ac = i;
    }

    public int getDmgR() {
        return _dmgr;
    }

    private void setDmgR(int i) {
        _dmgr = i;
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
	/*private int _expup;
	public int getExpUp() {
		return _expup;
	}
	private void setExpUp(int i) {
		_expup = i;
	}
	private int _timegfx;
	public int getTimeGfx() {
		return _timegfx;
	}
	private void setTimeGfx(int i) {
		_timegfx = i;
	}*/
}