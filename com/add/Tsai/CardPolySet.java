package com.add.Tsai;

/**
 * 變身卡索引
 *
 * @author hero
 */
public class CardPolySet {
    private int _id;
    private String _msg1;
    private int[] _needids;
    private int[] _needquest;
    private String[] _needname;
    private int _questid;
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

    /**
     * @param Id            流水號
     * @param Html          對話檔名
     * @param Cmd           對話指令
     * @param QuestId       任務編號
     * @param PolyId        可變身編號
     * @param PolyTime      變身持續時間
     * @param PolyItemId    變身需求道具
     * @param PolyItemCount 變身需求道具數量
     * @param AddStr        力量
     * @param AddDex        敏捷
     * @param AddCon        體質
     * @param AddInt        智慧
     * @param AddWis        精神
     * @param AddCha        魅力
     * @param Ac            防禦
     * @param AddHp         血量
     * @param AddMp         魔量
     * @param AddHpr        回血量
     * @param AddMpr        回魔量
     * @param Dmg           額外傷害
     * @param BowDmg        弓額外傷害
     * @param Hit           命中
     * @param BowHit        弓額外命中
     * @param DmgR          減傷
     * @param MagicDmgR     魔法減傷
     * @param Sp            魔攻
     * @param MagicHit      魔法命中
     * @param Mr            魔法防禦
     * @param Fire          火防
     * @param Water         水防
     * @param Wind          風防
     * @param Earth         地防
     */
    public CardPolySet(int Id, String msg1, int[] needids, int[] needquest, String[] needname, int QuestId, int AddStr, int AddDex, int AddCon, int AddInt, int AddWis, int AddCha, int Ac, int AddHp, int AddMp, int AddHpr, int AddMpr, int Dmg, int BowDmg, int Hit, int BowHit, int DmgR, int MagicDmgR, int Sp, int MagicHit, int Mr, int Fire, int Water, int Wind, int Earth) {
        _id = Id;
        _msg1 = msg1;
        _needids = needids;
        _needquest = needquest;
        _needname = needname;
        _questid = QuestId;
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
    }

    public int getId() {
        return _id;
    }

    public String getMsg1() {
        return _msg1;
    }

    public final int[] getNeedids() {
        return _needids;
    }

    public final int[] getNeedQuest() {
        return _needquest;
    }

    public final String[] getNeedName() {
        return _needname;
    }

    public int getQuestId() {
        return _questid;
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

    public boolean hasGroup() {
        return _needids.length > 0;
    }

    public boolean hasNeedQuest(final int questId) {
        for (final int id : _needquest) {
            if (id == questId) {
                return true;
            }
        }
        return false;
    }
}