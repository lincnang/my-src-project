package com.add.Tsai.Astrology;

/**
 * 守護星盤索引
 *
 * @author hero
 */
public class AstrologyData {
    private final int _id;
    private final String _name;
    private final int _needQuestId;
    private final int _questId;
    private final int _cards;
    private final int _skillId;
    private final int _addhgfxid; //圖片檔
    private final int _addcgfxid; //圖片檔
    private final int _needItemID;
    private final int _needItemNum;
    private final int _addstr;
    private final int _adddex;
    private final int _addcon;
    private final int _addint;
    private final int _addwis;
    private final int _addcha;
    private final int _ac;
    private final int _sp;
    private final int _addhp;
    private final int _addmp;
    private final int _adddmg;
    private final int _addbowdmg;
    private final int _addhit;
    private final int _addbowhit;
    private final int _adddmgr;
    private final int _addmdmgr;
    private final int _addfuzhong;//負重
    private final int _stunHit;
    private final int _addMagicDmgUp;
    private final int _pvpDmg;
    private final int _damageReductionByArmor;
    private final int _addfire;//火
    private final int _addwater;//水
    private final int _addwind;//風
    private final int _addearth;//土
    private final int _addshanbi;//閃避提高
    private final int _addhuibi;//迴避提高
    private final int _addyaoshui;//藥水增加
    private final int _addexp;//經驗
    private final int _addhunmi;//昏迷耐性

    public AstrologyData(int id, String name, int needQuestId, int questId, int cards, int skillId, int addhgfxid, int addcgfxid, int needItemID, int needItemNum, int addstr, int adddex, int addcon, int addint, int addwis, int addcha, int ac, int sp, int addhp, int addmp, int adddmg, int addbowdmg, int addhit, int addbowhit, int adddmgr, int addmdmgr, int addfuzhong, int stunHit, int addMagicDmgUp, int pvpDmg, int damageReductionByArmor, int addfire, int addwater, int addwind, int addearth, int addshanbi, int addhuibi, int addyaoshui, int addexp, int addhunmi) {
        _id = id;
        _name = name;
        _needQuestId = needQuestId;
        _questId = questId;
        _cards = cards;
        _skillId = skillId;
        _addhgfxid = addhgfxid;
        _addcgfxid = addcgfxid;
        _needItemID = needItemID;
        _needItemNum = needItemNum;
        _addstr = addstr;
        _adddex = adddex;
        _addcon = addcon;
        _addint = addint;
        _addwis = addwis;
        _addcha = addcha;
        _ac = ac;
        _sp = sp;
        _addhp = addhp;
        _addmp = addmp;
        _adddmg = adddmg;
        _addbowdmg = addbowdmg;
        _addhit = addhit;
        _addbowhit = addbowhit;
        _adddmgr = adddmgr;
        _addmdmgr = addmdmgr;
        _addfuzhong = addfuzhong;
        _stunHit = stunHit;
        _addMagicDmgUp = addMagicDmgUp;
        _pvpDmg = pvpDmg;
        _damageReductionByArmor = damageReductionByArmor;
        _addfire = addfire;
        _addwater = addwater;
        _addwind = addwind;
        _addearth = addearth;
        _addshanbi = addshanbi;
        _addhuibi = addhuibi;
        _addyaoshui = addyaoshui;
        _addexp = addexp;
        _addhunmi = addhunmi;
    }

    public int getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public int get_needQuestId() {
        return _needQuestId;
    }

    public int get_questId() {
        return _questId;
    }

    public int get_cards() {
        return _cards;
    }

    public int get_skillId() {
        return _skillId;
    }

    public int getAddhgfxid() {
        return _addhgfxid;
    }

    public int getAddcgfxid() {
        return _addcgfxid;
    }

    public int getNeedItemID() {
        return _needItemID;
    }

    public int getNeedItemNum() {
        return _needItemNum;
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

    public int get_sp() {
        return _sp;
    }

    public int getAddHp() {
        return _addhp;
    }

    public int getAddMp() {
        return _addmp;
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

    public int getFuzhong() {
        return _addfuzhong;
    }

    public int getStunHit() {
        return _stunHit;
    }

    public int get_addMagicDmgUp() {
        return _addMagicDmgUp;
    }

    public int get_pvpDmg() {
        return _pvpDmg;
    }

    public int get_damageReductionByArmor() {
        return _damageReductionByArmor;
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

    /**
     * 閃避
     *
     * @return
     */
    public int getShanbi() {
        return _addshanbi;
    }

    /**
     * 迴避
     *
     * @return
     */
    public int getHuibi() {
        return _addhuibi;
    }

    /**
     * 藥水增加
     *
     * @return
     */
    public int getYaoshui() {
        return _addyaoshui;
    }

    /**
     * 經驗
     *
     * @return
     */
    public int getExp() {
        return _addexp;
    }

    /**
     * 昏迷耐性
     *
     * @return
     */
    public int getHunmi() {
        return _addhunmi;
    }
}