package com.add.Tsai;

/**
 * 收藏索引
 *
 * @author hero
 */
public class collect {
    private int _id;
    private String _msg1;
    private String _msg2;
    private String _cmd;
    private int _questid;
    private int _needId;
    private int _needCount;
    private int _needEnchant;
    private int _addstr;
    private int _adddex;
    private int _addcon;
    private int _addint;
    private int _addwis;
    private int _addcha;
    private int _ac;
    private int _addhp;
    private int _addmp;
    private int _addhpr;
    private int _addmpr;
    private int _adddmg;
    private int _addbowdmg;
    private int _addhit;
    private int _addbowhit;
    private int _adddmgr;
    private int _addmdmgr;
    private int _addsp;
    private int _addmhit;
    private int _addmr;
    private int _addfire;
    private int _addwater;
    private int _addwind;
    private int _addearth;
    private String _addhgfxid;
    private String _addcgfxid;
    private int _addshanbi;//閃避提高
    private int _addhuibi;//迴避提高
    private int _addyaoshui;//藥水增加
    private int _addfuzhong;//負重
    private int _addexp;//經驗
    private int _addhunmi;//昏迷耐性
    private int _addzhicheng;//支撐耐性
    private int _addshihua;//石化耐性
    private int _addhanbing;//寒冰耐性
    private int _addanhei;//暗黑耐性
    private int _addshuimian;//睡眠耐性

    /**
     * @param Id               流水號
     * @param msg1             對話檔名
     * @param Cmd              對話指令
     * @param QuestId          任務編號
     * @param needitem_id      可變身編號
     * @param needitem_count   變身持續時間
     * @param needitem_enchant 變身需求道具
     * @param AddStr           力量
     * @param AddDex           敏捷
     * @param AddCon           體質
     * @param AddInt           智慧
     * @param AddWis           精神
     * @param AddCha           魅力
     * @param Ac               防禦
     * @param AddHp            血量
     * @param AddMp            魔量
     * @param AddHpr           回血量
     * @param AddMpr           回魔量
     * @param Dmg              額外傷害
     * @param BowDmg           弓額外傷害
     * @param Hit              命中
     * @param BowHit           弓額外命中
     * @param DmgR             減傷
     * @param MagicDmgR        魔法減傷
     * @param Sp               魔攻
     * @param MagicHit         魔法命中
     * @param Mr               魔法防禦
     * @param Fire             火防
     * @param Water            水防
     * @param Wind             風防
     * @param Earth            地防
     */
    public collect(int Id, String msg1, String msg2, String Cmd, int QuestId, int needitem_id, int needitem_count, int needitem_enchant, int AddStr, int AddDex, int AddCon, int AddInt, int AddWis, int AddCha, int Ac, int AddHp, int AddMp, int AddHpr, int AddMpr, int Dmg, int BowDmg, int Hit, int BowHit, int DmgR, int MagicDmgR, int Sp, int MagicHit, int Mr, int Fire, int Water, int Wind, int Earth, String hgfx, String cgfx, int shanbi, int huibi, int yaoshui, int fuzhong, int exp, int hunmi, int zhicheng, int shihua, int hanbing, int anhei, int shuimian) {
        _id = Id;
        _msg1 = msg1;
        _msg2 = msg2;
        _cmd = Cmd;
        _questid = QuestId;
        _needId = needitem_id;
        _needCount = needitem_count;
        _needEnchant = needitem_enchant;
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

    public int get_needId() {
        return _needId;
    }

    public void set_needId(int id) {
        _needId = id;
    }

    public int get_needCount() {
        return _needCount;
    }

    public void set_needCount(int count) {
        _needCount = count;
    }

    public int get_needEnchant() {
        return _needEnchant;
    }

    public void set_needEnchants(int enchant) {
        _needEnchant = enchant;
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