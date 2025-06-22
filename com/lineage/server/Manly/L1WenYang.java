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
 * 裝備溶解系統
 *
 * @author Administrator
 */
public class L1WenYang {
    private int _type;//類型
    private int _level;//等級
    private int _rate; //機率
    private String _not;//說明
    private int _cost; //強化消耗點數
    private int _costup; //機率提升損耗點數
    private int _maxRate; //最大機率
    private int _liliang;//力量
    private int _minjie;//敏捷
    private int _zhili;//智力
    private int _jingshen;//精神
    private int _tizhi;//體質
    private int _meili;//魅力
    private int _xue;//HP
    private int _mo;//MP
    private int _huixue;//HPR
    private int _huimo;//MPR
    private int _ewai;//攻擊額外
    private int _chenggong;//攻擊成功
    private int _mogong;//SP
    private int _mofang;//MR
    private int _feng;//風屬性防禦
    private int _shui;//水屬性防禦
    private int _tu;//地屬性防禦
    private int _huo;//火屬性防禦
    private int _jianmian;//傷害減免
    private int _jingyan;//經驗
    private int _addshanbi;//閃避提高
    private int _addhuibi;//迴避提高
    private int _addyaoshui;//藥水增加
    private int _addfuzhong;//負重
    private int _add_Ac;//防禦
    private int _adddice_dmg;// 機率給予爆擊率
    private int _adddmg; //機率給予傷害值
    private int _addpvpdmg; // 增加PVP傷害
    private int _addpvpdmg_r; // 增加PVP減傷
    private int _buff_iconid;
    private int _buff_stringid;

    public L1WenYang(int type, int level, int rate, String not, int cost, int costup, int maxRate, int liliang, int minjie, int zhili, int jingshen, int tizhi, int meili, int xue, int mo, int huixue, int huimo, int ewai, int chenggong, int mogong, int mofang, int feng, int shui, int tu, int huo, int jianmian, int jingyan, int buff_buff_iconid, int buff_stringid, int shanbi, int huibi, int yaoshui, int fuzhong, int add_Ac, int adddice_dmg, int adddmg, int addpvpdmg, int addpvpdmg_r) {
        _type = type;
        _level = level;
        _not = not;
        _rate = rate;
        _cost = cost;
        _costup = costup;
        _maxRate = maxRate;
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
        _buff_iconid = buff_buff_iconid;
        _buff_stringid = buff_stringid;
        _addshanbi = shanbi; //0115
        _addhuibi = huibi;
        _addyaoshui = yaoshui;
        _addfuzhong = fuzhong;
        _add_Ac = add_Ac; //0122
        _adddice_dmg = adddice_dmg;
        _adddmg = adddmg;
        _addpvpdmg = addpvpdmg;
        _addpvpdmg_r = addpvpdmg_r;
    }

    public int get_buff_iconid() {
        return this._buff_iconid;
    }   //*圖檔tbl編號

    public void set_buff_iconid(final int i) {
        this._buff_iconid = i;
    } //*圖檔tbl編號

    public int get_buff_stringid() {
        return this._buff_stringid;
    } //*偵測圖檔string編號

    public void set_buff_stringid(final int i) {
        this._buff_stringid = i;
    } //*偵測圖檔string編號

    /**
     * 閃避
     */
    public int getaddshanbi() {
        return _addshanbi;
    }

    /**
     * 迴避
     */
    public int getHuibi() {
        return _addhuibi;
    }

    /**
     * 藥水增加
     */
    public int getYaoshui() {
        return _addyaoshui;
    }

    /**
     * 負重
     */
    public int getFuzhong() {
        return _addfuzhong;
    }

    /**
     * 防禦
     */
    public int getAc() {
        return _add_Ac;
    }

    /**
     * 機率給予爆擊
     */
    public int gatDiceDmg() {
        return _adddice_dmg;
    }

    /**
     * 機率給予爆擊質
     */
    public int getDmg() {
        return _adddmg;
    }

    /**
     * 增加PVP傷害
     */
    public int getpvpdmg() {
        return _addpvpdmg;
    }

    /**
     * 增加PVP減傷
     */
    public int getpvpdmg_r() {
        return _addpvpdmg_r;
    }

    public int getType() {
        return _type;
    }

    public int getRate() {
        return _rate;
    }

    public int getLevel() {
        return _level;
    }

    public String getNot() {
        return _not;
    }

    public int getCost() {
        return _cost;
    }

    public int getCostUp() {
        return _costup;
    }

    public int getMaxRate() {
        return _maxRate;
    }

    public int getliliang() {
        return _liliang;
    }

    public int getminjie() {
        return _minjie;
    }

    public int getzhili() {
        return _zhili;
    }

    public int getjingshen() {
        return _jingshen;
    }

    public int gettizhi() {
        return _tizhi;
    }

    public int getmeili() {
        return _meili;
    }

    public int getxue() {
        return _xue;
    }

    public int getmo() {
        return _mo;
    }

    public int gethuixue() {
        return _huixue;
    }

    public int gethuimo() {
        return _huimo;
    }

    public int getewai() {
        return _ewai;
    }

    public int getchenggong() {
        return _chenggong;
    }

    public int getmogong() {
        return _mogong;
    }

    public int getmofang() {
        return _mofang;
    }

    public int getfeng() {
        return _feng;
    }

    public int getshui() {
        return _shui;
    }

    public int gettu() {
        return _tu;
    }

    public int gethuo() {
        return _huo;
    }

    public int getjianmian() {
        return _jianmian;
    }

    public int getjingyan() {
        return _jingyan;
    }
}
