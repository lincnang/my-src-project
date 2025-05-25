package com.add.Tsai;

/**
 * 聖物索引
 *
 * @author hero
 */
public class holy {
    private final int _id;
    private final int _group;
    private final String _msg1;
    private final String _msg2;
    private final String _cmd;
    private final int _questid;
    private final int _holyiditemid;
    private final int _holyiditemcount;
    private final int _addstr;
    private final int _adddex;
    private final int _addcon;
    private final int _addint;
    private final int _addwis;
    private final int _addcha;
    private final int _ac;
    private final int _addhp;
    private final int _addmp;
    private final int _addhpr;
    private final int _addmpr;
    private final int _adddmg;
    private final int _addbowdmg;
    private final int _addhit;
    private final int _addbowhit;
    private final int _adddmgr;
    private final int _addmdmgr;
    private final int _addsp;
    private final int _addmhit;
    private final int _addmr;
    private final int _addfire;
    private final int _addwater;
    private final int _addwind;
    private final int _addearth;
    private final String _addhgfxid; //圖片檔
    private final String _addcgfxid; //圖片檔
    private final int _addshanbi;//閃避提高
    private final int _addhuibi;//迴避提高
    private final int _addyaoshui;//藥水增加
    private final int _addfuzhong;//負重
    private final int _addexp;//經驗
    private final int _addhunmi;//昏迷耐性
    private final int _addzhicheng;//支撐耐性
    private final int _addshihua;//石化耐性
    private final int _addhanbing;//寒冰耐性
    private final int _addanhei;//暗黑耐性
    private final int _addshuimian;//睡眠耐性
    private int _holyid;

    /**
     * @param Id              流水號
     *                        / * @param Html
     *                        對話檔名
     * @param Cmd             對話指令
     * @param QuestId         任務編號
     * @param holyid          可變身編號
     * @param holyidItemId    變身需求道具
     * @param holyidItemCount 變身需求道具數量
     * @param AddStr          力量
     * @param AddDex          敏捷
     * @param AddCon          體質
     * @param AddInt          智慧
     * @param AddWis          精神
     * @param AddCha          魅力
     * @param Ac              防禦
     * @param AddHp           血量
     * @param AddMp           魔量
     * @param AddHpr          回血量
     * @param AddMpr          回魔量
     * @param Dmg             額外傷害
     * @param BowDmg          弓額外傷害
     * @param Hit             命中
     * @param BowHit          弓額外命中
     * @param DmgR            減傷
     * @param MagicDmgR       魔法減傷
     * @param Sp              魔攻
     * @param MagicHit        魔法命中
     * @param Mr              魔法防禦
     * @param Fire            火防
     * @param Water           水防
     * @param Wind            風防
     * @param Earth           地防
     */
    public holy(int Id, int Group, String msg1, String msg2, String Cmd, int QuestId, int holyid, int holyidItemId, int holyidItemCount, int AddStr, int AddDex, int AddCon, int AddInt, int AddWis, int AddCha, int Ac, int AddHp, int AddMp, int AddHpr, int AddMpr, int Dmg, int BowDmg, int Hit, int BowHit, int DmgR, int MagicDmgR, int Sp, int MagicHit, int Mr, int Fire, int Water, int Wind, int Earth, String hgfx, String cgfx, int shanbi, int huibi, int yaoshui, int fuzhong, int exp, int hunmi, int zhicheng, int shihua, int hanbing, int anhei, int shuimian) {
        _id = Id;
        _group = Group;
        _msg1 = msg1;
        _msg2 = msg2;
        _cmd = Cmd;
        _questid = QuestId;
        _holyid = holyid;
        _holyiditemid = holyidItemId;
        _holyiditemcount = holyidItemCount;
        _addstr = AddStr;
        _adddex = AddDex;
        _addcon = AddCon;
        _addint = AddInt;
        _addwis = AddWis;
        _addcha = AddCha;
        _ac = Ac;
        _addhp = AddHp;
        _addmp = AddMp;
        _addhpr = AddHpr;
        _addmpr = AddMpr;
        _adddmg = Dmg;
        _addbowdmg = BowDmg;
        _addhit = Hit;
        _addbowhit = BowHit;
        _adddmgr = DmgR;
        _addmdmgr = MagicDmgR;
        _addsp = Sp;
        _addmhit = MagicHit;
        _addmr = Mr;
        _addfire = Fire;
        _addwater = Water;
        _addwind = Wind;
        _addearth = Earth;
        _addhgfxid = hgfx;
        _addcgfxid = cgfx;
        _addshanbi = shanbi;
        _addhuibi = huibi;
        _addyaoshui = yaoshui;
        _addfuzhong = fuzhong;
        _addexp = exp;
        _addhunmi = hunmi;
        _addzhicheng = zhicheng;
        _addshihua = shihua;
        _addhanbing = hanbing;
        _addanhei = anhei;
        _addshuimian = shuimian;
    }

    /**
     * 閃避
     *
     */
    public int getShanbi() {
        return _addshanbi;
    }

    /**
     * 迴避
     *
     */
    public int getHuibi() {
        return _addhuibi;
    }

    /**
     * 藥水增加
     *
     */
    public int getYaoshui() {
        return _addyaoshui;
    }

    /**
     * 負重
     *
     */
    public int getFuzhong() {
        return _addfuzhong;
    }

    /**
     * 經驗
     *
     */
    public int getExp() {
        return _addexp;
    }

    /**
     * 昏迷耐性
     *
     */
    public int getHunmi() {
        return _addhunmi;
    }

    /**
     * 支撐耐性
     *
     */
    public int getZhicheng() {
        return _addzhicheng;
    }

    /**
     * 石化耐性
     *
     */
    public int getShihua() {
        return _addshihua;
    }

    /**
     * 睡眠耐性
     *
     */
    public int getShuimian() {
        return _addshuimian;
    }

    /**
     * 寒冰耐性
     *
     */
    public int getHanbing() {
        return _addhanbing;
    }

    /**
     * 暗黑耐性
     *
     */
    public int getAnhei() {
        return _addanhei;
    }

    public int getId() {
        return _id;
    }

    public int getGroup() {
        return _group;
    }

    public String getMsg1() {
        return _msg1;
    }

    public String getMsg2() {
        return _msg2;
    }

    public String getCmd() {
        return _cmd;
    }

    public int getQuestId() {
        return _questid;
    }

    public int getPolyId() {
        return _holyid;
    }

    public void setPolyId(int i) {
        _holyid = i;
    }

    public int getPolyItemId() {
        return _holyiditemid;
    }

    public int getPolyItemCount() {
        return _holyiditemcount;
    }

    public int getAddStr() {
        return _addstr;
    }

    public int getAddDex() {
        return _adddex;
    }

    public int getAddCon() {
        return _addcon;
    }

    public int getAddInt() {
        return _addint;
    }

    public int getAddWis() {
        return _addwis;
    }

    public int getAddCha() {
        return _addcha;
    }

    public int getAddAc() {
        return _ac;
    }

    public int getAddHp() {
        return _addhp;
    }

    public int getAddMp() {
        return _addmp;
    }

    public int getAddHpr() {
        return _addhpr;
    }

    public int getAddMpr() {
        return _addmpr;
    }

    public int getAddDmg() {
        return _adddmg;
    }

    public int getAddBowDmg() {
        return _addbowdmg;
    }

    public int getAddHit() {
        return _addhit;
    }

    public int getAddBowHit() {
        return _addbowhit;
    }

    public int getAddDmgR() {
        return _adddmgr;
    }

    public int getAddMagicDmgR() {
        return _addmdmgr;
    }

    public int getAddSp() {
        return _addsp;
    }

    public int getAddMagicHit() {
        return _addmhit;
    }

    public int getAddMr() {
        return _addmr;
    }

    public int getAddFire() {
        return _addfire;
    }

    public int getAddWater() {
        return _addwater;
    }

    public int getAddWind() {
        return _addwind;
    }

    public int getAddEarth() {
        return _addearth;
    }

    public String getAddhgfxid() {
        return _addhgfxid;
    }

    public String getAddcgfxid() {
        return _addcgfxid;
    }
}