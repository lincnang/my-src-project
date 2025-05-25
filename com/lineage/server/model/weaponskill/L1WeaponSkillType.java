package com.lineage.server.model.weaponskill;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Location;
import com.lineage.server.serverpackets.S_EffectLocation;
import com.lineage.server.serverpackets.S_InventoryIcon;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_UseAttackSkill;
import com.lineage.server.thread.PcOtherThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.lineage.server.model.skill.L1SkillId.IMMUNE_TO_HARM;

public abstract class L1WeaponSkillType {
    private static final Log _log = LogFactory.getLog(L1WeaponSkillType.class);
    private static final Random _random = new Random();
    protected int _type1;
    protected int _type2;
    protected int _type3;
    protected int _ac_mr;
    protected int _random1;
    protected int _random2;
    protected boolean _boss_holdout;
    protected int _gfxidIcon;
    protected int _gfxidString;
    protected int _gfxSec;
    private double _level = 1.0D;
    private int _attr;
    private int _gfxid1;
    private int _gfxid2;
    private int _gfxid3;
    private int _gfxid4;
    private int _gfxid4_count;
    private int _power;
    private int _srcdmg;
    private int _addsrcdmg;

    public double start_weapon_skill(L1PcInstance pc, L1Character target, L1ItemInstance weapon, double srcdmg) {
        return 0.0D;
    }

    /**
     * 傳回最終魔法傷害輸出百分比
     *
     * @return
     */
    public double get_level() {
        return _level;
    }

    /**
     * 設定最終魔法傷害輸出百分比
     *
     * @param level
     */
    public void set_level(int level) {
        _level = (level / 100.0D);
    }

    public int get_type1() {
        return _type1;
    }

    public void set_type1(int _type1) {
        this._type1 = _type1;
    }

    public int get_type2() {
        return _type2;
    }

    public void set_type2(int _type2) {
        this._type2 = _type2;
    }

    public int get_type3() {
        return _type3;
    }

    public void set_type3(int _type3) {
        this._type3 = _type3;
    }

    public int get_attr() {
        return _attr;
    }

    public void set_attr(int _attr) {
        this._attr = _attr;
    }

    public int get_ac_mr() {
        return _ac_mr;
    }

    public void set_ac_mr(int _ac_mr) {
        this._ac_mr = _ac_mr;
    }

    public int get_gfxid1() {
        return _gfxid1;
    }

    public void set_gfxid1(int _gfxid1) {
        this._gfxid1 = _gfxid1;
    }

    public int get_gfxid2() {
        return _gfxid2;
    }

    public void set_gfxid2(int _gfxid2) {
        this._gfxid2 = _gfxid2;
    }

    public int get_gfxid3() {
        return _gfxid3;
    }

    public void set_gfxid3(int _gfxid3) {
        this._gfxid3 = _gfxid3;
    }

    public int get_gfxid4() {
        return _gfxid4;
    }

    public void set_gfxid4(int _gfxid4) {
        this._gfxid4 = _gfxid4;
    }

    public int get_gfxid4_count() {
        return _gfxid4_count;
    }

    public void set_gfxid4_count(int _gfxid4_count) {
        this._gfxid4_count = _gfxid4_count;
    }

    public int get_power() {
        return _power;
    }

    public void set_power(int _power) {
        this._power = _power;
    }

    public int get_srcdmg() {
        return _srcdmg;
    }

    public void set_srcdmg(int _srcdmg) {
        this._srcdmg = _srcdmg;
    }

    public int get_addsrcdmg() {
        return _addsrcdmg;
    }

    public void set_addsrcdmg(int _addsrcdmg) {
        if (_addsrcdmg > 100) {
            _addsrcdmg = 100;
        }
        this._addsrcdmg = _addsrcdmg;
    }

    public int get_random1() {
        return _random1;
    }

    public void set_random1(int _random1) {
        if (_random1 > 1000) {
            _random1 = 1000;
        }
        this._random1 = _random1;
    }

    public int get_random2() {
        return _random2;
    }

    public void set_random2(int _random2) {
        if (_random2 > 1000) {
            _random2 = 1000;
        }
        this._random2 = _random2;
    }

    /**
     * 是否對BOSS無效
     *
     * @return
     */
    public boolean get_boss_holdout() {
        return _boss_holdout;
    }

    /**
     * 設定是否對BOSS無效
     *
     * @param boss_holdout
     */
    public void set_boss_holdout(boolean boss_holdout) {
        _boss_holdout = boss_holdout;
    }

    public int get_gfxid_Icon() {
        return _gfxidIcon;
    }

    public void set_gfxid_Icon(int gfxidIcon) {
        _gfxidIcon = gfxidIcon;
    }

    public int get_gfxid_String() {
        return _gfxidString;
    }

    public void set_gfxid_String(int gfxidString) {
        _gfxidString = gfxidString;
    }

    public int get_gfx_Sec() {
        return _gfxSec;
    }

    public void set_gfx_Sec(int gfxSec) {
        _gfxSec = gfxSec;
    }

    /**
     * 計算發動機率
     *
     * @param weapon
     * @return
     */
    protected int random(L1ItemInstance weapon) {
        try {
            int int1 = _random1;
            if ((_random2 != 0) && (weapon.getEnchantLevel() > 0)) {
                return (int1 + weapon.getEnchantLevel()) * _random2;
            } else {
                return int1 * 10;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return 0;
    }

    /**
     * 額外增加傷害
     *
     * @return
     */
    protected int dmg1() {
        try {
            if (_srcdmg != 0) {
                return _srcdmg;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return 0;
    }

    /**
     * 額外增加傷害%數
     *
     * @param srcdmg
     * @return
     */
    protected double dmg2(double srcdmg) {
        try {
            if (_addsrcdmg != 0) {
                return srcdmg * _addsrcdmg / 100.0D;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return 0.0D;
    }

    /**
     * 額外增加傷害%數
     *
     * @param srcdmg
     * @return
     */
    protected double bon() {
        try {
            if (_addsrcdmg != 0) {
                return _addsrcdmg / 100.0D;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return 1.0D;
    }

    /**
     * 人物能力值增加傷害
     *
     * @param pc
     * @return
     */
    protected int dmg3(L1PcInstance pc) {
        try {
            if (_power > 0) {
                int int1 = 0;
                int tmp = _power;
                int i = 0;
                if (tmp >= 32) {
                    i++;
                    tmp -= 32;
                    int1 += pc.getCon();
                }
                if (tmp >= 16) {
                    i++;
                    tmp -= 16;
                    int1 += pc.getCha();
                }
                if (tmp >= 8) {
                    i++;
                    tmp -= 8;
                    int1 += pc.getWis();
                }
                if (tmp >= 4) {
                    i++;
                    tmp -= 4;
                    int1 += pc.getInt();
                }
                if (tmp >= 2) {
                    i++;
                    tmp -= 2;
                    int1 += pc.getDex();
                }
                if (tmp >= 1) {
                    i++;
                    tmp--;
                    int1 += pc.getStr();
                }
                return int1 / i >> 1;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return 0;
    }

    /**
     * 各類減傷計算(包含聖界減傷)
     *
     * @param pc
     * @param target
     * @param srcdmg
     * @param weapon
     * @return
     */
    protected double calc_dmg(L1PcInstance pc, L1Character target, double srcdmg, L1ItemInstance weapon) {
        if (target.hasSkillEffect(IMMUNE_TO_HARM)) {// 對手身上有聖界效果
            srcdmg /= 2.0D;
        }
        try {
            switch (_ac_mr) {
                case 0:
                    break;
                case 1:
                    srcdmg -= DmgAcMr.calcDefense(target);
                    break;
                case 2:
                    srcdmg = DmgAcMr.calcMrDefense(pc, target, srcdmg, weapon);
                    break;
                case 3:
                    srcdmg -= DmgAcMr.calcDefense(target);
                    srcdmg = DmgAcMr.calcMrDefense(pc, target, srcdmg, weapon);
            }
            int resist = 0;
            switch (_attr) {
                case 1:
                    resist = target.getEarth();
                    break;
                case 2:
                    resist = target.getFire();
                    break;
                case 4:
                    resist = target.getWater();
                    break;
                case 8:
                    resist = target.getWind();
                    break;
            }
            int resistFloor = (int) (0.16 * Math.abs(resist));
            if (resist >= 0) {
                resistFloor *= 1;
            } else {
                resistFloor *= -1;
            }
            double attrDeffence = resistFloor / 32.0;
            srcdmg = (1.0D - attrDeffence) * srcdmg;
            return srcdmg * _level;// 最終傷害係數計算
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return 0.0D;
    }

    /**
     * 抗魔係數
     *
     * @param pc
     * @param target
     * @param srcdmg
     * @param weapon
     * @return
     */
    protected double calc_mr(L1PcInstance pc, L1Character target, double srcdmg, L1ItemInstance weapon) {
        try {
            switch (_ac_mr) {
                case 0:
                    break;
                case 1:
                    srcdmg -= DmgAcMr.calcDefense(target);
                    break;
                case 2:
                    srcdmg = DmgAcMr.calcMrDefense(pc, target, srcdmg, weapon);
                    break;
                case 3:
                    srcdmg -= DmgAcMr.calcDefense(target);
                    srcdmg = DmgAcMr.calcMrDefense(pc, target, srcdmg, weapon);
                    break;
                case 4:
                    srcdmg = DmgAcMr.calcMrDefense2(pc, target, srcdmg, weapon);
                    break;
            }
            return srcdmg;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return 0.0D;
    }

    /**
     * 屬抗參數
     *
     * @param pc
     * @param target
     * @param srcdmg
     * @param weapon
     * @return
     */
    protected int calc_resist(L1Character target) {
        try {
            int resist = 0;
            switch (_attr) {
                case 1:
                    resist = target.getEarth();
                    break;
                case 2:
                    resist = target.getFire();
                    break;
                case 4:
                    resist = target.getWater();
                    break;
                case 8:
                    resist = target.getWind();
                    break;
            }
            int resistFloor = 0;
            if (resist <= 3) {
                resistFloor = 0;
            } else if (resist > 3 && resist <= 8) {
                resistFloor = 1;
            } else if (resist == 9) {
                resistFloor = 2;
            } else if (resist > 9 && resist <= 14) {
                resistFloor = 3;
            } else if (resist > 14 && resist <= 17) {
                resistFloor = 4;
            } else if (resist > 17 && resist <= 20) {
                resistFloor = 5;
            } else if (resist > 20 && resist <= 24) {
                resistFloor = 6;
            } else if (resist > 24 && resist <= 27) {
                resistFloor = 7;
            } else if (resist > 27 && resist <= 29) {
                resistFloor = 8;
            } else if (resist > 29 && resist <= 30) {
                resistFloor = 9;
            } else if (resist > 30 && resist <= 35) {
                resistFloor = 10;
            } else if (resist > 35 && resist <= 40) {
                resistFloor = 11;
            } else if (resist > 40 && resist <= 44) {
                resistFloor = 12;
            } else if (resist > 44 && resist <= 48) {
                resistFloor = 13;
            } else if (resist > 48 && resist <= 52) {
                resistFloor = 14;
            } else if (resist > 52 && resist <= 54) {
                resistFloor = 15;
            } else if (resist > 54 && resist <= 56) {
                resistFloor = 16;
            } else if (resist > 56 && resist <= 58) {
                resistFloor = 17;
            } else if (resist > 58 && resist <= 60) {
                resistFloor = 18;
            } else if (resist > 60 && resist <= 64) {
                resistFloor = 19;
            } else if (resist > 64 && resist <= 68) {
                resistFloor = 20;
            } else if (resist > 68 && resist <= 72) {
                resistFloor = 21;
            } else if (resist > 72 && resist <= 76) {
                resistFloor = 22;
            } else if (resist > 76 && resist <= 80) {
                resistFloor = 23;
            } else if (resist > 80 && resist <= 84) {
                resistFloor = 24;
            } else if (resist > 84 && resist <= 88) {
                resistFloor = 25;
            } else if (resist > 92 && resist <= 96) {
                resistFloor = 26;
            } else if (resist > 100 && resist <= 104) {
                resistFloor = 27;
            } else if (resist > 104 && resist <= 108) {
                resistFloor = 28;
            } else if (resist > 108 && resist <= 112) {
                resistFloor = 29;
            } else if (resist > 112 && resist <= 120) {
                resistFloor = 30;
            } else if (resist > 120 && resist <= 129) {
                resistFloor = 31;
            } else if (resist > 129) {
                resistFloor = 32;
            }
            return resistFloor;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return 0;
    }

    /**
     * 顯示攻擊動畫
     *
     * @param pc
     * @param target
     */
    protected void show(L1PcInstance pc, L1Character target) {
        try {
            // System.out.println("test gfx");
            if (_gfxid1 != 0) {// 動畫出現在自己身上
                pc.sendPacketsAll(new S_SkillSound(pc.getId(), _gfxid1));
            }
            if (_gfxid2 != 0) {// 動畫出現在對方身上
                target.broadcastPacketAll(new S_SkillSound(target.getId(), _gfxid2));
                if ((target instanceof L1PcInstance)) {
                    L1PcInstance targetPc = (L1PcInstance) target;
                    targetPc.sendPackets(new S_SkillSound(target.getId(), _gfxid2));
                }
            }
            if (_gfxid3 != 0) {// 飛行類動畫
                S_UseAttackSkill packet = new S_UseAttackSkill(pc, target.getId(), _gfxid3, target.getX(), target.getY(), 1, false);
                pc.sendPacketsAll(packet);
            }
            if ((_gfxid4 != 0) && (_gfxid4_count > 0)) {// 周圍隨機出現動畫
                WanponSkill wanponSkill = new WanponSkill(pc);
                wanponSkill.start_skill();
            }
            if (_gfxidIcon != 0) {// 動畫出現在對方身上
                if ((target instanceof L1PcInstance)) {
                    L1PcInstance targetPc = (L1PcInstance) target;
                    //					targetPc.sendPackets(new S_NewSkillIcon(DEATH_HEAL, true, _gfxSec));
                    targetPc.sendPackets(new S_InventoryIcon(_gfxidIcon, true, _gfxidString, _gfxSec));
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private class WanponSkill implements Runnable {
        private L1PcInstance _pc;

        private WanponSkill(L1PcInstance pc) {
            _pc = pc;
        }

        public void start_skill() {
            PcOtherThreadPool.get().execute(this);
        }

        public void run() {
            try {
                int x = _pc.getX();
                int y = _pc.getY();
                int mapId = _pc.getMapId();
                for (int i = 0; i < _gfxid4_count; i++) {
                    TimeUnit.MILLISECONDS.sleep(30L);
                    int i1 = L1WeaponSkillType._random.nextInt(4);
                    int i2 = L1WeaponSkillType._random.nextInt(4);
                    switch (L1WeaponSkillType._random.nextInt(4)) {
                        case 0:
                            _pc.sendPacketsAll(new S_EffectLocation(new L1Location(x + i1, y - i2, mapId), _gfxid4));
                            break;
                        case 1:
                            _pc.sendPacketsAll(new S_EffectLocation(new L1Location(x - i1, y + i2, mapId), _gfxid4));
                            break;
                        case 2:
                            _pc.sendPacketsAll(new S_EffectLocation(new L1Location(x + i1, y + i2, mapId), _gfxid4));
                            break;
                        case 3:
                            _pc.sendPacketsAll(new S_EffectLocation(new L1Location(x - i1, y - i2, mapId), _gfxid4));
                    }
                }
            } catch (Exception e) {
                L1WeaponSkillType._log.error(e.getLocalizedMessage(), e);
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.weaponskill.L1WeaponSkillType JD-Core Version: 0.6.2
 */