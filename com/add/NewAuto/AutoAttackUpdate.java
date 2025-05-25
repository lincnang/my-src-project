package com.add.NewAuto;
//	 import static com.lineage.server.model.skill.L1SkillId.CHILL_TOUCH; //	 * 毒咒
//import static com.lineage.server.model.skill.L1SkillId.FIRE_ARROW;
//import static com.lineage.server.model.skill.L1SkillId.ICE_DAGGER;
//import static com.lineage.server.model.skill.L1SkillId.STALAC;
//import static com.lineage.server.model.skill.L1SkillId.WIND_CUTTER;

import com.lineage.config.ThreadPoolSetNew;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.MapsTable;
import com.lineage.server.datatables.NewAutoPractice;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.lock.CharSkillReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1Skills;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.lineage.server.model.skill.L1SkillId.*;

/**
 * 選單內掛訊息
 *
 * @author 老爹
 */
public class AutoAttackUpdate {
    private static final int[] _auto_buy_item = {0,     // 尚未設定
            40010, // 治癒藥水
            40011, // 強力治癒藥水
            40012, // 終極治癒藥水

    };
    private static final int[] _auto_buy_item_adena = {0,     // 尚未設定
            ThreadPoolSetNew.ITEMADENA1, ThreadPoolSetNew.ITEMADENA2, ThreadPoolSetNew.ITEMADENA3, ThreadPoolSetNew.ITEMADENA4, ThreadPoolSetNew.ITEMADENA5, ThreadPoolSetNew.ITEMADENA6,};
    private static final int[] _auto_buy_item2 = {0,    // 尚未設定
            40014,// 勇敢藥水
            40015,// 加速魔力回復藥水
            40018,// 強化 自我加速藥水
            49138,// 巧克力蛋糕
    };
    private static final int[] _auto_buy_item_adena2 = {0,    // 尚未設定
            ThreadPoolSetNew.ITEMADENA7, ThreadPoolSetNew.ITEMADENA8, ThreadPoolSetNew.ITEMADENA9, ThreadPoolSetNew.ITEMADENA10, ThreadPoolSetNew.ITEMADENA11,};
    /**
     * 可紀錄的攻擊技能
     */
    private static final int[] _autoskill_attack = {
            // 王族
            KINGDOM_STUN,
            // 騎士
            SHOCK_STUN,
            // 法師
            ENERGY_BOLT/* ICE_DAGGER, WIND_CUTTER/*,CHILL_TOUCH*/, /*FIRE_ARROW, STALAC,*/ FROZEN_CLOUD, FIREBALL, CALL_LIGHTNING, CONE_OF_COLD, ERUPTION, SUNBURST, TORNADO/*BLIZZARD*/ /*LIGHTNING_STORM*/, FIRE_STORM, METEOR_STRIKE, DISINTEGRATE,
            // 妖精
            TRIPLE_ARROW,
            // 黑妖
            FINAL_BURN,
            // 龍騎
            MAGMA_BREATH, FOE_SLAYER, THUNDER_GRAB, FREEZING_BREATH,
            // 幻術
            JOY_OF_PAIN};
    private static Log _log = LogFactory.getLog(AutoAttackUpdate.class);
    private static AutoAttackUpdate _instance;
    private int _autoBuyItemIndex1 = 0;
    private int _autoBuyItemNumIndex1 = 0;
    private int _autoBuyItemIndex2 = 0;
    private int _autoBuyItemNumIndex2 = 0;

    public static AutoAttackUpdate getInstance() {
        if (_instance == null) {
            _instance = new AutoAttackUpdate();
        }
        return _instance;
    }

    /**
     * 訊息更新
     *
     */
    public void MsgUpdate(final L1PcInstance pc) {
        try {
            final StringBuilder s = new StringBuilder();
            if (pc.IsEnemyTeleport()) {
                s.append("開啟,");// 0
            } else {
                s.append("關閉,");// 0
            }
            String m;
            if (pc.getDeathReturnX() != 0) {
                m = "返回地圖:" + MapsTable.get().locationname(pc.getDeathReturnMap());
            } else {
                m = "無設定返回地圖";
            }
            s.append(m).append(",");// 1
            if (pc.IsBuyArrow()) {
                s.append("開啟,");// 2
            } else {
                s.append("關閉,");// 2
            }
            if (pc.IsAttackTeleport()) {
                s.append("瞬移,");//3
            } else {
                s.append("不瞬移,");//3
            }
            if (pc.IsAutoTeleport()) {
                s.append("瞬移,");// 4
            } else {
                s.append("不瞬移,");// 4
            }
            if (pc.getchecksummid()) {
                s.append("開啟,");// 5
            } else {
                s.append("關閉,");// 5
            }
            if (pc.getsummon_skillid() == 0) {//6
                s.append("空,");
            } else {
                if (pc.getsummon_skillid() == 1) {
                    s.append("初治癒,");
                }
                if (pc.getsummon_skillid() == 19) {
                    s.append("中治癒,");
                }
                if (pc.getsummon_skillid() == 35) {
                    s.append("高治癒,");
                }
                if (pc.getsummon_skillid() == 57) {
                    s.append("全治癒,");
                }
            }
            if (pc.getchecksummidhp()) {
                s.append("開啟,");// 7
            } else {
                s.append("關閉,");// 7
            }
            if (pc.getH() != 0) {
                s.append(pc.getH()).append(",");//8
            }
            if (pc.getM() != 0) {
                s.append(pc.getM()).append(",");//9
            }
            if (pc.getBossfei()) {
                s.append("開啟,");// 10
            } else {
                s.append("關閉,");// 10
            }
            if (pc.getHeping()) {
                s.append("開啟,");// 11
            } else {
                s.append("關閉,");// 11
            }
            if (pc.get_autoBuyItem1() > 0) {
                L1Item item = ItemTable.get().getTemplate(pc.get_autoBuyItem1());
                s.append(item.getName()).append(",");// 12
            } else {
                s.append("尚未設定,");  // 12
            }
            if (pc.get_autoBuyItemNum1() > 0) {
                s.append(pc.get_autoBuyItemNum1()).append(",");// 13
            } else {
                s.append("0,");// 13
            }
            if (pc.get_autoBuyItem2() > 0) {
                L1Item item = ItemTable.get().getTemplate(pc.get_autoBuyItem2());
                s.append(item.getName()).append(",");// 14
            } else {
                s.append("尚未設定,");// 14
            }
            if (pc.get_autoBuyItemNum2() > 0) {
                s.append(pc.get_autoBuyItemNum2()).append(",");// 15
            } else {
                s.append("0,");// 15
            }
            s.append(pc.getLsRange()).append(",");// 16
            final String[] c = s.toString().split(",");
            pc.sendPackets(new S_NPCTalkReturn(pc, "x_auto1", c));
        } catch (Throwable e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 內掛選單判斷
     *
     */
    public boolean PcCommand(final L1PcInstance _pc, final String cmd) {
        try {
            boolean HaveCmd = true;
            switch (cmd) {
                case "Au_1":
                    _pc.setIsAttackTeleport(!_pc.IsAttackTeleport());//是否開啟內掛被攻擊瞬移狀態
                    MsgUpdate(_pc);
                    break;
                case "Au_2":
                    _pc.setIsEnemyTeleport(!_pc.IsEnemyTeleport());
                    MsgUpdate(_pc);
                    break;
                case "Au_3":
                    _pc.setKeyInEnemy(true);
                    _pc.sendPackets(new S_ServerMessage("\\fU請輸入玩家名稱。"));
                    break;
                case "Au_4":
                    _pc.setKeyOutEnemy(true);
                    _pc.sendPackets(new S_ServerMessage("\\fU請輸入玩家名稱。"));
                    break;
                case "Au_5":
                    EnemyList(_pc);
                    break;
                case "Au_6":
                    NewAutoPractice.get().SearchAutoLog(_pc);
                    break;
                case "Au_7":
                    _pc.setDeathReturn(!_pc.IsDeathReturn());
                    MsgUpdate(_pc);
                    break;
                case "Au_8":
                    _pc.setBuyArrow(!_pc.IsBuyArrow());
                    MsgUpdate(_pc);
                    break;
                case "Au_9"://是否開啟自動瞬移
                    _pc.setAutoTeleport(!_pc.IsAutoTeleport());
                    MsgUpdate(_pc);
                    break;
                case "Au_14":
                    CanSkillList(_pc);
                    break;
                case "Au_15":
                    SkillList(_pc);
                    break;
                case "Au_16":
                    _pc.sendPackets(new S_ShopSellList(_pc));
                    break;
                case "Au_17":
                    if (_pc.isActivated()) {
                        _pc.stopPcAI();
                    } else {
                        _pc.startPcAI();
                        _pc.sendPackets(new S_CloseList(_pc.getId()));
                    }
                    /*
                     * 变更AI线程
                    if (_pc.getWeapon() == null) {
                        _pc.sendPackets(new S_ServerMessage("\\fU手上並未裝備任何武器無法開始自動練功。"));
                    } else {
                        if (_pc.IsAuto()) {
                            _pc.setIsAuto(false);
                            _pc.setRestartAuto(0);
                            _pc.setRestartAutoStartSec(0);
                            if (_pc.IsDeathReturn()) {
                                _pc.setDeathReturn(false);
                            }
                            L1Teleport.teleport(_pc, _pc.getX(), _pc.getY(), _pc.getMapId(), 5, true);
                        } else {
                            if (_pc.getLsRange() > 0) {
                                _pc.setLsLocX(_pc.getX());
                                _pc.setLsLocY(_pc.getY());
                                _pc.setLsOpen(true);
                            }
                            _pc.setHH((int) ((_pc.getMaxHp() * _pc.getH()) * 0.01));
                            _pc.setMM((int) ((_pc.getMaxMp() * _pc.getM()) * 0.01));
                            _pc.setIsAuto(true);
                            _pc.setRestartAuto(ThreadPoolSetNew.RESTART_AUTO);
                            AutoAttack2020_1 auto = new AutoAttack2020_1(_pc);
                            auto.begin();
                        }
                        _pc.sendPackets(new S_CloseList(_pc.getId()));
                    }
                    */
                    break;
                case "Au_18":
                    MsgUpdate(_pc);
                    break;
                case "Au_19":
                    NewAutoPractice.get().ClearAutoLog(_pc.getId());
                    _pc.sendPackets(new S_CloseList(_pc.getId()));
                    _pc.sendPackets(new S_ServerMessage("\\fU紀錄已刪除。"));
                    break;
                case "Au_21":
                    _pc.clearAttackSkillList();
                    MsgUpdate(_pc);
                    _pc.sendPackets(new S_ServerMessage("\\fU紀錄已刪除。"));
                    break;
                case "Au_22":
                    if (!_pc.isActivated()) {
                        _pc.addH(1);
                        MsgUpdate(_pc);
                    } else {
                        _pc.sendPackets(new S_ServerMessage("\\fU關閉內掛後才可以調整。"));
                    }
                    break;
                case "Au_23":
                    if (!_pc.isActivated()) {
                        _pc.addH(-1);
                        MsgUpdate(_pc);
                    } else {
                        _pc.sendPackets(new S_ServerMessage("\\fU關閉內掛後才可以調整。"));
                    }
                    break;
                case "Au_24":
                    if (!_pc.isActivated()) {
                        _pc.addM(1);
                        MsgUpdate(_pc);
                    } else {
                        _pc.sendPackets(new S_ServerMessage("\\fU關閉內掛後才可以調整。"));
                    }
                    break;
                case "Au_25":
                    if (!_pc.isActivated()) {
                        _pc.addM(-1);
                        MsgUpdate(_pc);
                    } else {
                        _pc.sendPackets(new S_ServerMessage("\\fU關閉內掛後才可以調整。"));
                    }
                    break;
                case "Au_26":
                    if (!_pc.getInventory().checkEquipped(20284)) {
                        _pc.sendPackets(new S_SystemMessage("未裝備[召喚控制戒指]"));
                        return false;
                    }
                    _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "guajisummon"));
                    break;
                case "Au_27":
                    if (!CharSkillReading.get().spellCheck(_pc.getId(), 51)) {
                        _pc.sendPackets(new S_SystemMessage("您尚未學習召喚術"));
                        return false;
                    }
                    if (_pc.getInventory().checkEquipped(20284) && _pc.getzhaohuan() == null) {
                        _pc.sendPackets(new S_SystemMessage("攜帶召戒,請先設定要召喚的怪物"));
                        return false;
                    }
                    _pc.setchecksummid(!_pc.getchecksummid());
                    MsgUpdate(_pc);
                    break;
                case "Au_28":
                    if (_pc.getsummon_skillid() == 1) {
                        if (!CharSkillReading.get().spellCheck(_pc.getId(), 19)) {
                            _pc.sendPackets(new S_SystemMessage("您尚未學習中治"));
                            return false;
                        }
                        _pc.setsummon_skillid(19);
                        _pc.setsummon_skillidmp(15);
                    } else if (_pc.getsummon_skillid() == 19) {
                        if (!CharSkillReading.get().spellCheck(_pc.getId(), 35)) {
                            _pc.sendPackets(new S_SystemMessage("您尚未學習高治"));
                            return false;
                        }
                        _pc.setsummon_skillid(35);
                        _pc.setsummon_skillidmp(20);
                    } else if (_pc.getsummon_skillid() == 35) {
                        if (!CharSkillReading.get().spellCheck(_pc.getId(), 57)) {
                            _pc.sendPackets(new S_SystemMessage("您尚未學習全治"));
                            return false;
                        }
                        _pc.setsummon_skillid(57);
                        _pc.setsummon_skillidmp(48);
                    } else if (_pc.getsummon_skillid() == 57) {
                        _pc.setsummon_skillid(1);
                        _pc.setsummon_skillidmp(4);
                    } else if (_pc.getsummon_skillid() == 0) {
                        if (!CharSkillReading.get().spellCheck(_pc.getId(), 1)) {
                            _pc.sendPackets(new S_SystemMessage("您尚未學習初治"));
                            return false;
                        }
                        _pc.setsummon_skillid(1);
                        _pc.setsummon_skillidmp(4);
                    }
                    MsgUpdate(_pc);
                    break;
                case "Au_29":
                    //是否開啟補血給寵物
                    _pc.setchecksummidhp(!_pc.getchecksummidhp());
                    MsgUpdate(_pc);
                    break;
                case "Au_30": {
                    if (_pc.isActivated()) {
                        _pc.sendPackets(new S_ServerMessage("內掛中,無法進行此操作。"));
                        return false;
                    }
                    _pc.setLsOpen(false);
                    _pc.setLsRange(_pc.getLsRange() + 5);
                    MsgUpdate(_pc);
                }
                break;
                case "Au_31": {
                    if (_pc.isActivated()) {
                        _pc.sendPackets(new S_ServerMessage("內掛中,無法進行此操作。"));
                        return false;
                    }
                    _pc.setLsOpen(false);
                    _pc.setLsRange(_pc.getLsRange() - 5);
                    if (_pc.getLsRange() <= 0) {
                        _pc.setLsRange(0);
                        _pc.setLsLocX(0);
                        _pc.setLsLocY(0);
                    }
                    MsgUpdate(_pc);
                }
                break;
                case "Au_p2": {
                    _autoBuyItemIndex1--;
                    if (_autoBuyItemIndex1 < 0) {
                        _autoBuyItemIndex1 = 0;
                    }
                    _pc.set_autoBuyItem1(_auto_buy_item[_autoBuyItemIndex1]);
                    _pc.set_autoBuyItemAdena1(_auto_buy_item_adena[_autoBuyItemIndex1]);
                    MsgUpdate(_pc);
                }
                break;
                case "Au_p1": {
                    _autoBuyItemIndex1++;
                    if (_autoBuyItemIndex1 >= _auto_buy_item.length) {
                        _autoBuyItemIndex1 = _auto_buy_item.length - 1;
                    }
                    _pc.set_autoBuyItem1(_auto_buy_item[_autoBuyItemIndex1]);
                    _pc.set_autoBuyItemAdena1(_auto_buy_item_adena[_autoBuyItemIndex1]);
                    MsgUpdate(_pc);
                }
                break;
                case "Au_p4": {
                    _autoBuyItemNumIndex1 = _autoBuyItemNumIndex1 - 10;
                    if (_autoBuyItemNumIndex1 < 0) {
                        _autoBuyItemNumIndex1 = 0;
                    }
                    _pc.set_autoBuyItemNum1(_autoBuyItemNumIndex1);
                    MsgUpdate(_pc);
                }
                break;
                case "Au_p3": {
                    _autoBuyItemNumIndex1 = _autoBuyItemNumIndex1 + 10;
                    if (_autoBuyItemNumIndex1 > 200) {
                        _autoBuyItemNumIndex1 = 200;
                    }
                    _pc.set_autoBuyItemNum1(_autoBuyItemNumIndex1);
                    MsgUpdate(_pc);
                }
                break;
                case "Au_p6": {
                    _autoBuyItemIndex2--;
                    if (_autoBuyItemIndex2 < 0) {
                        _autoBuyItemIndex2 = 0;
                    }
                    _pc.set_autoBuyItem2(_auto_buy_item2[_autoBuyItemIndex2]);
                    _pc.set_autoBuyItemAdena2(_auto_buy_item_adena2[_autoBuyItemIndex2]);
                    MsgUpdate(_pc);
                }
                break;
                case "Au_p5": {
                    _autoBuyItemIndex2++;
                    if (_autoBuyItemIndex2 >= _auto_buy_item2.length) {
                        _autoBuyItemIndex2 = _auto_buy_item2.length - 1;
                    }
                    _pc.set_autoBuyItem2(_auto_buy_item2[_autoBuyItemIndex2]);
                    _pc.set_autoBuyItemAdena2(_auto_buy_item_adena2[_autoBuyItemIndex2]);
                    MsgUpdate(_pc);
                }
                break;
                case "Au_p8": {
                    _autoBuyItemNumIndex2 = _autoBuyItemNumIndex2 - 10;
                    if (_autoBuyItemNumIndex2 < 0) {
                        _autoBuyItemNumIndex2 = 0;
                    }
                    _pc.set_autoBuyItemNum2(_autoBuyItemNumIndex2);
                    MsgUpdate(_pc);
                }
                break;
                case "Au_p7": {
                    _autoBuyItemNumIndex2 = _autoBuyItemNumIndex2 + 10;
                    if (_autoBuyItemNumIndex2 > 200) {
                        _autoBuyItemNumIndex2 = 200;
                    }
                    _pc.set_autoBuyItemNum2(_autoBuyItemNumIndex2);
                    MsgUpdate(_pc);
                }
                break;
                case "summ_7":
                    _pc.setzhaohuan("7");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_263":
                    _pc.setzhaohuan("263");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_519":
                    _pc.setzhaohuan("519");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_8":
                    _pc.setzhaohuan("8");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_264":
                    _pc.setzhaohuan("264");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_520":
                    _pc.setzhaohuan("520");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_9":
                    _pc.setzhaohuan("9");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_265":
                    _pc.setzhaohuan("265");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_521":
                    _pc.setzhaohuan("521");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_10":
                    _pc.setzhaohuan("10");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_266":
                    _pc.setzhaohuan("266");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_522":
                    _pc.setzhaohuan("522");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_11":
                    _pc.setzhaohuan("11");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_267":
                    _pc.setzhaohuan("267");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_523":
                    _pc.setzhaohuan("523");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_12":
                    _pc.setzhaohuan("12");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_268":
                    _pc.setzhaohuan("268");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_524":
                    _pc.setzhaohuan("524");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_13":
                    _pc.setzhaohuan("13");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_269":
                    _pc.setzhaohuan("269");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_525":
                    _pc.setzhaohuan("525");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_14":
                    _pc.setzhaohuan("14");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_270":
                    _pc.setzhaohuan("270");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_526":
                    _pc.setzhaohuan("526");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_15":
                    _pc.setzhaohuan("15");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_271":
                    _pc.setzhaohuan("271");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_527":
                    _pc.setzhaohuan("527");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_16":
                    _pc.setzhaohuan("16");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_17":
                    _pc.setzhaohuan("17");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_18":
                    _pc.setzhaohuan("18");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "summ_274":
                    _pc.setzhaohuan("274");
                    _pc.sendPackets(new S_ServerMessage("\\fU設定召戒召喚怪物完成。"));
                    break;
                case "Au_223":
                    _pc.setBossfei(!_pc.getBossfei());
                    MsgUpdate(_pc);
                    break;
                case "Au_224":
                    _pc.setHeping(!_pc.getHeping());
                    MsgUpdate(_pc);
                    break;
                case "x_auto1":
                    AutoAttackUpdate.getInstance().MsgUpdate(_pc);
                    break;
                case "Skill_select":
                    getSkillInfo(_pc);
                    break;
                case "na335":
                    _pc.setAutoAttackMP(10);
                    getSkillInfo(_pc);
                    _pc.sendPackets(new S_SystemMessage("設定魔法10%以下"));
                    break;
                case "na336":
                    _pc.setAutoAttackMP(20);
                    getSkillInfo(_pc);
                    _pc.sendPackets(new S_SystemMessage("設定魔法20%以下"));
                    break;
                case "na337":
                    _pc.setAutoAttackMP(30);
                    getSkillInfo(_pc);
                    _pc.sendPackets(new S_SystemMessage("設定魔法30%以下"));
                    break;
                case "na332":
                    _pc.setAutoAttackSecond(1);
                    getSkillInfo(_pc);
                    break;
                case "na333":
                    _pc.setAutoAttackSecond(2);
                    getSkillInfo(_pc);
                    break;
                case "na334":
                    _pc.setAutoAttackSecond(3);
                    getSkillInfo(_pc);
                    break;
                default:
                    HaveCmd = false;
                    break;
            }
            if (HaveCmd) {
                return true;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    public void getSkillInfo(final L1PcInstance _pc) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(",");
        stringBuilder.append(",");
        stringBuilder.append(",");
        // #3 技能名稱
        if (_pc.AutoAttackNew().size() > 0) {
            final L1Skills skill = SkillsTable.get().getTemplate(_pc.AutoAttackNew().get(0));
            String name = skill.getName();
            stringBuilder.append(name).append(",");
        } else {
            stringBuilder.append("無,");
        }
        // #4 MP釋放%
        stringBuilder.append(_pc.getAutoAttackMP()).append(",");
        // #5 間格秒數
        stringBuilder.append(_pc.getAutoAttackSecond()).append(",");
//        final String[] msg = stringBuilder.toString().split(",");//從0開始分裂以逗號為單位
//        _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "Skill_select", msg));
    }

    /**
     * 查詢可記錄技能清單
     *
     */
    public void CanSkillList(final L1PcInstance pc) {
        final StringBuilder msg = new StringBuilder();
        for (int j : _autoskill_attack) {
            if (CharSkillReading.get().spellCheck(pc.getId(), j)) {
                final L1Skills skill = SkillsTable.get().getTemplate(j);
                msg.append(skill.getName()).append(",");
            }
        }
        final String[] c = msg.toString().split(",");
        pc.sendPackets(new S_NPCTalkReturn(pc, "x_auto3", c));
    }

    /**
     * 查詢已登錄技能清單
     *
     */
    public void SkillList(final L1PcInstance pc) {
        final StringBuilder msg = new StringBuilder();
        for (Integer id : pc.AttackSkillList()) {
            final L1Skills skill = SkillsTable.get().getTemplate(id);
            msg.append(skill.getName()).append(",");
        }
        final String[] c = msg.toString().split(",");
        pc.sendPackets(new S_NPCTalkReturn(pc, "x_auto3", c));
    }

    /**
     * 查詢仇人名單
     *
     */
    public void EnemyList(final L1PcInstance pc) {
        final StringBuilder msg = new StringBuilder();
        for (String name : pc.InEnemyList()) {
            msg.append(name).append(",");
        }
        final String[] c = msg.toString().split(",");
        pc.sendPackets(new S_NPCTalkReturn(pc, "x_auto2", c));
    }

    /**
     * 檢測是否為可以記錄的技能
     *
     */
    public boolean isAttackSkill(int Skillid) {
        for (final int skillId : _autoskill_attack) {
            if (skillId == Skillid) {
                return true;
            }
        }
        return false;
    }
}