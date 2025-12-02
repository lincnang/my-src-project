/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.lineage.server.Manly;

/**
 * 紋樣資料模型
 */
public class L1WenYang {

    // 紋樣類型常量定義
    public static final int WENYANG_TYPE_1 = 1;
    public static final int WENYANG_TYPE_2 = 2;
    public static final int WENYANG_TYPE_3 = 3;
    public static final int WENYANG_TYPE_4 = 4;
    public static final int WENYANG_TYPE_5 = 5;
    public static final int WENYANG_TYPE_6 = 6;

    private int _type;              // 類型
    private int _level;             // 等級
    private String _not;            // 說明(名稱)

    private int _cost = 0;          // ★ 強化消耗點數
    private int _costup = 0;        // ★ 機率提升損耗點數(變更機率用)

    // 屬性
    private int _liliang;           // 力量
    private int _minjie;            // 敏捷
    private int _zhili;             // 智力
    private int _jingshen;          // 精神
    private int _tizhi;             // 體質
    private int _meili;             // 魅力
    private int _xue;               // HP
    private int _mo;                // MP
    private int _huixue;            // HPR
    private int _huimo;             // MPR
    private int _ewai;              // 額外傷害
    private int _chenggong;         // 攻擊成功
    private int _mogong;            // SP
    private int _mofang;            // MR
    private int _feng;              // 風屬性防禦
    private int _shui;              // 水屬性防禦
    private int _tu;                // 地屬性防禦
    private int _huo;               // 火屬性防禦
    private int _jianmian;          // 傷害減免
    private int _jingyan;           // 經驗加成

    // 額外加成
    private int _addshanbi;         // 閃避提高
    private int _addhuibi;          // 迴避提高
    private int _addyaoshui;        // 藥水增加
    private int _addfuzhong;        // 負重
    private int _add_Ac;            // 防禦
    private int _adddice_dmg;       // 機率給予爆擊率
    private int _adddmg;            // 機率給予爆擊質
    private int _addpvpdmg;         // 增加PVP傷害
    private int _addpvpdmg_r;       // 增加PVP減傷

    // Buff 顯示
    private int _buff_iconid;
    private int _buff_stringid;

    // 由 DB 控制的單次成功 +2/+3 上限(%)（實際隨機分布會受此約束）
    private int _plus2Cap = 100;
    private int _plus3Cap = 100;

    /**
     * 建構子（含 cost 與 costup；最後兩參為 +2/+3 上限）
     */
    public L1WenYang(
            int type, int level, String not, int cost, int costup,
            int liliang, int minjie, int zhili, int jingshen, int tizhi, int meili, int xue, int mo,
            int huixue, int huimo, int ewai, int chenggong, int mogong, int mofang, int feng, int shui,
            int tu, int huo, int jianmian, int jingyan, int buff_iconid, int buff_stringid,
            int shanbi, int huibi, int yaoshui, int fuzhong, int add_Ac, int adddice_dmg, int adddmg,
            int addpvpdmg, int addpvpdmg_r,
            int plus2Cap, int plus3Cap
    ) {
        _type = type;
        _level = level;
        _not = not;

        _cost = Math.max(0, cost);
        _costup = Math.max(0, costup);

        _liliang = liliang;
        _minjie = minjie;
        _zhili = zhili;
        _jingshen = jingshen;
        _tizhi = tizhi;
        _meili = meili;
        _xue = xue;
        _mo = mo;
        _huixue = huixue;
        _huimo = huimo;
        _ewai = ewai;
        _chenggong = chenggong;
        _mogong = mogong;
        _mofang = mofang;
        _feng = feng;
        _shui = shui;
        _tu = tu;
        _huo = huo;
        _jianmian = jianmian;
        _jingyan = jingyan;

        _buff_iconid = buff_iconid;
        _buff_stringid = buff_stringid;

        _addshanbi = shanbi;
        _addhuibi = huibi;
        _addyaoshui = yaoshui;
        _addfuzhong = fuzhong;
        _add_Ac = add_Ac;
        _adddice_dmg = adddice_dmg;
        _adddmg = adddmg;
        _addpvpdmg = addpvpdmg;
        _addpvpdmg_r = addpvpdmg_r;

        _plus2Cap = Math.max(0, Math.min(plus2Cap, 100));
        _plus3Cap = Math.max(0, Math.min(plus3Cap, 100));
    }

    // ====== 基本存取器 ======
    public int getType() { return _type; }
    public int getLevel() { return _level; }
    public String getNot() { return _not; }

    // cost / costup
    public int getCost() { return _cost; }                 // 強化消耗點數
    public void setCost(int v) { _cost = Math.max(0, v); }
    public int getCostUp() { return _costup; }             // 變更機率扣點
    public void setCostUp(int v) { _costup = Math.max(0, v); }

    // +2/+3 cap（百分比 0..100）
    public int getPlus2Cap() { return _plus2Cap; }
    public void setPlus2Cap(int v) { _plus2Cap = Math.max(0, Math.min(v, 100)); }
    public int getPlus3Cap() { return _plus3Cap; }
    public void setPlus3Cap(int v) { _plus3Cap = Math.max(0, Math.min(v, 100)); }

    // Buff 顯示
    public int get_buff_iconid() { return _buff_iconid; }
    public void set_buff_iconid(final int i) { _buff_iconid = i; }
    public int get_buff_stringid() { return _buff_stringid; }
    public void set_buff_stringid(final int i) { _buff_stringid = i; }

    // ====== 屬性存取器 ======
    public int getliliang() { return _liliang; }
    public int getminjie() { return _minjie; }
    public int getzhili() { return _zhili; }
    public int getjingshen() { return _jingshen; }
    public int gettizhi() { return _tizhi; }
    public int getmeili() { return _meili; }
    public int getxue() { return _xue; }
    public int getmo() { return _mo; }
    public int gethuixue() { return _huixue; }
    public int gethuimo() { return _huimo; }
    public int getewai() { return _ewai; }
    public int getchenggong() { return _chenggong; }
    public int getmogong() { return _mogong; }
    public int getmofang() { return _mofang; }
    public int getfeng() { return _feng; }
    public int getshui() { return _shui; }
    public int gettu() { return _tu; }
    public int gethuo() { return _huo; }
    public int getjianmian() { return _jianmian; }
    public int getjingyan() { return _jingyan; }

    // 額外加成
    public int getaddshanbi() { return _addshanbi; }   // 閃避提高
    public int getHuibi() { return _addhuibi; }        // 迴避提高
    public int getYaoshui() { return _addyaoshui; }    // 藥水增加
    public int getFuzhong() { return _addfuzhong; }    // 負重
    public int getAc() { return _add_Ac; }             // 防禦
    public int getDiceDmg() { return _adddice_dmg; }   // 機率給予爆擊率
    public int getDmg() { return _adddmg; }            // 機率給予爆擊質
    public int getpvpdmg() { return _addpvpdmg; }      // PVP 傷害
    public int getpvpdmg_r() { return _addpvpdmg_r; }  // PVP 減傷
}
