package com.lineage.server.templates;

import com.lineage.data.event.LeavesSet;
import com.lineage.server.command.GmHtml;
import com.lineage.server.model.Instance.L1ItemInstance;

import java.util.HashMap;
import java.util.Map;

public class L1PcOther {
    public static final int CLEVLE0 = 1;
    public static final int CLEVLE1 = 2;
    public static final int CLEVLE2 = 4;
    public static final int CLEVLE3 = 8;
    public static final int CLEVLE4 = 16;
    public static final int CLEVLE5 = 32;
    public static final int CLEVLE6 = 64;
    public static final int CLEVLE7 = 128;
    public static final int CLEVLE8 = 256;
    public static final int CLEVLE9 = 512;
    public static final int CLEVLE10 = 1024;
    public static final String[] ADDNAME = {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
    private static final Map<Integer, StringBuilder> _titleList = new HashMap<>();
    private static boolean _isStart = false;
    private L1ItemInstance _item = null;
    private boolean _shopSkill = false;
    private int _objid = 0;
    private int _page = 0;
    private int _usemap = 0;
    private int _usemapTime = 0;
    private int _addhp = 0;
    private int _addmp = 0;
    private int _score = 0;
    private int _color = 1;
    private int _clanskill = 0;
    private int _killCount = 0;
    private int _deathCount = 0;
    private GmHtml _gmHtml = null;
    private int _login_time;
    private int _leaves_time = 0;
    private int _leaves_time_exp = 0;
    /**
     * 傳回贊助額度
     *
     */
    private int _getbonus = 0;
    private int _Artifact = 0;
    private int _Lv_Artifact = 0;
    private int _Lv_Redmg_Artifact = 0;
    private int _Artifact1 = 0;
    // 計時掛機剩餘點數
    private int _auto_points = 0;

    // Silian astrology per-skill cooldown expiry (epoch seconds)
    private int _silian_cd1_until_s = 0;
    private int _silian_cd2_until_s = 0;
    private int _silian_cd3_until_s = 0;

    // Silian HOT status persistence (removed)

    public static void load() {
        if (!_isStart) {
            _titleList.put(1, new StringBuilder(""));
            _titleList.put(2, new StringBuilder("\\fD"));
            _titleList.put(4, new StringBuilder("\\f="));
            _titleList.put(8, new StringBuilder("\\fH"));
            _titleList.put(16, new StringBuilder("\\f_"));
            _titleList.put(32, new StringBuilder("\\f2"));
            _titleList.put(64, new StringBuilder("\\fF"));
            _titleList.put(128, new StringBuilder("\\fT"));
            _titleList.put(256, new StringBuilder("\\fE"));
            _titleList.put(512, new StringBuilder("\\f0"));
            _titleList.put(1024, new StringBuilder("\\f?"));
            _isStart = true;
        }
    }

    public L1ItemInstance get_item() {
        return _item;
    }

    public void set_item(L1ItemInstance item) {
        _item = item;
    }

    public boolean get_shopSkill() {
        return _shopSkill;
    }

    public void set_shopSkill(boolean shopSkill) {
        _shopSkill = shopSkill;
    }

    public int get_objid() {
        return _objid;
    }

    public void set_objid(int objid) {
        _objid = objid;
    }

    public int get_page() {
        return _page;
    }

    public void set_page(int page) {
        _page = page;
    }

    /**
     * 取回短時間計時地圖
     */
    public int get_usemap() {
        return _usemap;
    }

    /**
     * 短時間計時地圖
     */
    public void set_usemap(int usemap) {
        _usemap = usemap;
    }

    /**
     * 取回短時間計時地圖已用時間
     */
    public int get_usemapTime() {
        return _usemapTime;
    }

    /**
     * 設定短時間計時地圖已用時間
     */
    public void set_usemapTime(int usemapTime) {
        _usemapTime = usemapTime;
    }

    public int get_addhp() {
        return _addhp;
    }

    public void set_addhp(int addhp) {
        if (addhp < 0) {
            addhp = 0;
        }
        _addhp = addhp;
    }

    public int get_addmp() {
        return _addmp;
    }

    public void set_addmp(int addmp) {
        if (addmp < 0) {
            addmp = 0;
        }
        _addmp = addmp;
    }

    public void add_score(int score) {
        if (score < 0) {
            score = 0;
        }
        _score += score;
        if (_score < 0) {
            _score = 0;
        }
    }

    public int get_score() {
        return _score;
    }

    public void set_score(int score) {
        if (score < 0) {
            score = 0;
        }
        _score = score;
    }

    public boolean set_color(int color, int tg) {
        set_score(tg);
        _color = color;
        return true;
    }

    public int get_color() {
        return _color;
    }

    public void set_color(int color) {
        _color = color;
    }

    public String color() {
        StringBuilder stringBuilder = (StringBuilder) _titleList.get(_color);
        if (stringBuilder != null) {
            return stringBuilder.toString();
        }
        return "";
    }

    public int get_clanskill() {
        return _clanskill;
    }

    public void set_clanskill(int clanskill) {
        _clanskill = clanskill;
    }

    public void add_killCount(int i) {
        _killCount += i;
    }

    public int get_killCount() {
        return _killCount;
    }

    public void set_killCount(int i) {
        _killCount = i;
    }

    public void add_deathCount(int i) {
        _deathCount += i;
    }

    public int get_deathCount() {
        return _deathCount;
    }

    public void set_deathCount(int i) {
        _deathCount = i;
    }

    public GmHtml get_gmHtml() {
        return _gmHtml;
    }

    public void set_gmHtml(GmHtml gmHtml) {
        _gmHtml = gmHtml;
    }

    /**
     * 傳回上次登入時間(殷海薩祝福計算用)
     *
     */
    public int get_login_time() {
        return _login_time;
    }

    /**
     * 設定上次登入時間(殷海薩祝福計算用)
     *
     */
    public void set_login_time(int login_time) {
        _login_time = login_time;
    }

    /**
     * 傳回殷海薩休息時間
     *
     */
    public int get_leaves_time() {
        return _leaves_time;
    }

    /**
     * 設定殷海薩休息時間
     *
     */
    public void set_leaves_time(int leaves_time) {
        _leaves_time = leaves_time;
    }

    /**
     * 傳回殷海薩的祝福經驗額度
     *
     */
    public int get_leaves_time_exp() {
        return _leaves_time_exp;
    }

    /**
     * 設定殷海薩的祝福經驗額度
     *
     */
    public void set_leaves_time_exp(int leaves_time_exp) {
        if (leaves_time_exp < 0) {
            _leaves_time_exp = 0;
        } else {
            _leaves_time_exp = Math.min(leaves_time_exp, LeavesSet.MAXEXP);
        }
    }

    public int get_getbonus() {
        return this._getbonus;
    }

    public void set_getbonus(int getbonus) {
        this._getbonus = getbonus;
    }

    public void addArtifact(int i) {
        _Artifact += i;
    }

    public int getArtifact() {
        return this._Artifact;
    }

    public void setArtifact(int i) {
        this._Artifact = i;
    }

    public void addLv_Artifact(int i) {
        _Lv_Artifact += i;
    }

    public int getLv_Artifact() {
        return this._Lv_Artifact;
    }

    public void setLv_Artifact(int i) {
        this._Lv_Artifact = i;
    }

    public void addLv_Redmg_Artifact(int i) {
        _Lv_Redmg_Artifact += i;
    }

    public int getLv_Redmg_Artifact() {
        return this._Lv_Redmg_Artifact;
    }

    public void setLv_Redmg_Artifact(int i) {
        this._Lv_Redmg_Artifact = i;
    }

    public void addArtifact1(int i) {
        _Artifact1 += i;
    }

    public int getArtifact1() {
        return this._Artifact1;
    }

    // ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
    // 玩家鋪助類開關
    public void setArtifact1(int i) {
        this._Artifact1 = i;
    }

    /**
     * 掛機剩餘點數
     *
     */
    public int get_Auto_Points() {
        return _auto_points;
    }

    /**
     * 掛機剩餘點數
     *
     */
    public void set_Auto_Points(final int points) {
        _auto_points = points;
    }

    // ===== Silian cooldown getters/setters (epoch seconds) =====
    public int get_silian_cd1_until_s() { return _silian_cd1_until_s; }
    public int get_silian_cd2_until_s() { return _silian_cd2_until_s; }
    public int get_silian_cd3_until_s() { return _silian_cd3_until_s; }

    public void set_silian_cd1_until_s(int untilSec) { _silian_cd1_until_s = Math.max(0, untilSec); }
    public void set_silian_cd2_until_s(int untilSec) { _silian_cd2_until_s = Math.max(0, untilSec); }
    public void set_silian_cd3_until_s(int untilSec) { _silian_cd3_until_s = Math.max(0, untilSec); }

    // removed: silian_hot_until_s / silian_hot_skill_id accessors
}
