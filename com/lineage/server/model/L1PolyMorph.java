package com.lineage.server.model;

import com.add.Tsai.CardQuestTable;
import com.lineage.server.datatables.PolyTable;
import com.lineage.server.model.Instance.L1DeInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.*;
import com.lineage.server.timecontroller.event.ranking.RankingHeroTimer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import william.ArrowGfxid;

import java.util.HashMap;
import java.util.Map;

import static com.lineage.server.model.skill.L1SkillId.*;

/**
 * 變形控制項
 *
 * @author daien
 */
public class L1PolyMorph {
    // 變身原因示bit
    public static final int MORPH_BY_ITEMMAGIC = 1;
    public static final int MORPH_BY_GM = 2;
    public static final int MORPH_BY_NPC = 4; // 占星術師以外NPC
    public static final int MORPH_BY_KEPLISHA = 8;
    public static final int MORPH_BY_LOGIN = 0;
    private static final Log _log = LogFactory.getLog(L1PolyMorph.class);
    // weapon equip bit(2047)
    private static final int DAGGER_EQUIP = 1;
    private static final int SWORD_EQUIP = 2;
    private static final int TWOHANDSWORD_EQUIP = 4;
    private static final int AXE_EQUIP = 8;
    private static final int SPEAR_EQUIP = 16;
    private static final int STAFF_EQUIP = 32;
    private static final int EDORYU_EQUIP = 64;
    private static final int CLAW_EQUIP = 128;
    private static final int BOW_EQUIP = 256; // 包含鐵手甲
    private static final int KIRINGKU_EQUIP = 512;
    private static final int CHAINSWORD_EQUIP = 1024;
    // armor equip bit
    private static final int HELM_EQUIP = 1;
    private static final int AMULET_EQUIP = 2;
    private static final int EARRING_EQUIP = 4;
    private static final int TSHIRT_EQUIP = 8;
    private static final int ARMOR_EQUIP = 16;
    private static final int CLOAK_EQUIP = 32;
    private static final int BELT_EQUIP = 64;
    private static final int SHIELD_EQUIP = 128;
    private static final int GLOVE_EQUIP = 256;
    private static final int RING_EQUIP = 512;
    private static final int BOOTS_EQUIP = 1024;
    private static final int GUARDER_EQUIP = 2048;
    private static final Map<Integer, Integer> weaponFlgMap = new HashMap<>();
    private static final Map<Integer, Integer> armorFlgMap = new HashMap<>();

    static {
        weaponFlgMap.put(1, SWORD_EQUIP);// 劍
        weaponFlgMap.put(2, DAGGER_EQUIP);// 匕首
        weaponFlgMap.put(3, TWOHANDSWORD_EQUIP);// 雙手劍
        weaponFlgMap.put(4, BOW_EQUIP);// 弓
        weaponFlgMap.put(5, SPEAR_EQUIP);// 矛(雙手)
        weaponFlgMap.put(6, AXE_EQUIP);// 斧(單手)
        weaponFlgMap.put(7, STAFF_EQUIP);// 魔杖
        weaponFlgMap.put(8, BOW_EQUIP);// 飛刀
        weaponFlgMap.put(9, BOW_EQUIP);// 箭
        weaponFlgMap.put(10, BOW_EQUIP);// 鐵手甲
        weaponFlgMap.put(11, CLAW_EQUIP);// 鋼爪
        weaponFlgMap.put(12, EDORYU_EQUIP);// 雙刀
        weaponFlgMap.put(13, BOW_EQUIP);// 弓(單手)
        weaponFlgMap.put(14, SPEAR_EQUIP);// 矛(單手)
        weaponFlgMap.put(15, AXE_EQUIP);// 雙手斧
        weaponFlgMap.put(16, STAFF_EQUIP);// 魔杖(雙手)
        weaponFlgMap.put(17, KIRINGKU_EQUIP);// 奇古獸
        weaponFlgMap.put(18, CHAINSWORD_EQUIP);// 鎖鏈劍
    }

    static {
        armorFlgMap.put(1, HELM_EQUIP);
        armorFlgMap.put(2, ARMOR_EQUIP);
        armorFlgMap.put(3, TSHIRT_EQUIP);
        armorFlgMap.put(4, CLOAK_EQUIP);
        armorFlgMap.put(5, GLOVE_EQUIP);
        armorFlgMap.put(6, BOOTS_EQUIP);
        armorFlgMap.put(7, SHIELD_EQUIP);
        armorFlgMap.put(8, AMULET_EQUIP);
        armorFlgMap.put(9, RING_EQUIP);
        armorFlgMap.put(10, BELT_EQUIP);
        armorFlgMap.put(12, EARRING_EQUIP);
        armorFlgMap.put(13, GUARDER_EQUIP);
    }

    private int _id;
    private String _name;
    private int _polyId;
    private int _minLevel;
    private int _weaponEquipFlg;
    private int _armorEquipFlg;
    private boolean _canUseSkill;
    private int _causeFlg;
    private int _questId;

    public L1PolyMorph(int id, String name, int polyId, int minLevel, int weaponEquipFlg, int armorEquipFlg, boolean canUseSkill, int causeFlg, int questId) {
        _id = id;
        _name = name;
        _polyId = polyId;
        _minLevel = minLevel;
        _weaponEquipFlg = weaponEquipFlg;
        _armorEquipFlg = armorEquipFlg;
        _canUseSkill = canUseSkill;
        _causeFlg = causeFlg;
        _questId = questId;
    }

    public static void handleCommands(L1PcInstance pc, String s) {
        if ((pc == null) || (pc.isDead())) {
            return;
        }
        L1PolyMorph poly = PolyTable.get().getTemplate(s);
        if ((poly != null) || (s.equals("none"))) {
            if (s.equals("none")) {
                if ((pc.getTempCharGfx() != 6034) && (pc.getTempCharGfx() != 6035)) {
                    undoPoly(pc);
                    pc.sendPackets(new S_CloseList(pc.getId()));
                }
            } else if ((pc.getLevel() >= poly.getMinLevel()) || (pc.isGm())) {
                if ((pc.getTempCharGfx() == 6034) || (pc.getTempCharGfx() == 6035)) {
                    pc.sendPackets(new S_ServerMessage(181));
                } else {
                    doPoly(pc, poly.getPolyId(), 7200, 1);
                    pc.sendPackets(new S_CloseList(pc.getId()));
                }
            } else {
                pc.sendPackets(new S_ServerMessage(181));
            }
        }
    }

    /**
     * 執行變身
     *
     */
    public static void doPoly(L1Character cha, int polyId, int timeSecs, int cause) {  //src014
        try {
            if ((cha == null) || (cha.isDead())) {
                return;
            }
            if ((cha instanceof L1PcInstance)) {
                L1PcInstance pc = (L1PcInstance) cha;
                if ((pc.getMapId() == 5490) || (pc.getMapId() == 9000) || (pc.getMapId() == 9100) || (pc.getMapId() == 9101) || (pc.getMapId() == 9102) || (pc.getMapId() == 9202)) {
                    // 這裡不可以變身。
                    pc.sendPackets(new S_ServerMessage(1170));
                    return;
                }
                if ((pc.getTempCharGfx() == 6034) || (pc.getTempCharGfx() == 6035)) {
                    // 181:\f1無法變成你指定的怪物。
                    pc.sendPackets(new S_ServerMessage(181));
                    return;
                }
                if (!isMatchCause(polyId, cause)) {
                    // 181:\f1無法變成你指定的怪物。
                    pc.sendPackets(new S_ServerMessage(181));
                    return;
                }
                if (!pc.isGm() && !isQuestIdEnd(pc, polyId)) {
                    pc.sendPackets(new S_ServerMessage("請您開通卡片解鎖"));
                    return;
                }
                boolean isPoly = true;// 是否符合變身條件
                if (polyId >= 13715 && polyId <= 13745) {// TOP10變身
                    if ((!RankingHeroTimer.get_top10().containsValue(pc.getName()) && !RankingHeroTimer.get_top3C().containsValue(pc.getName()) && !RankingHeroTimer.get_top3K().containsValue(pc.getName()) && !RankingHeroTimer.get_top3E().containsValue(pc.getName()) && !RankingHeroTimer.get_top3W().containsValue(pc.getName()) && !RankingHeroTimer.get_top3D().containsValue(pc.getName()) && !RankingHeroTimer.get_top3G().containsValue(pc.getName()) && !RankingHeroTimer.get_top3I().containsValue(pc.getName())) && (!pc.isGm())) {// 不在排行榜內
                        isPoly = false;
                    }
                }
                if ((polyId == 13715) && (pc.get_sex() != 0 || !pc.isCrown())) {// 不符合變身真王子條件
                    isPoly = false;
                } else if ((polyId == 13717) && (pc.get_sex() != 1 || !pc.isCrown())) {// 不符合變身真公主條件
                    isPoly = false;
                } else if ((polyId == 13719) && (pc.get_sex() != 0 || !pc.isKnight())) {// 不符合變身真騎士條件
                    isPoly = false;
                } else if ((polyId == 13721) && (pc.get_sex() != 1 || !pc.isKnight())) {// 不符合變身真女騎士條件
                    isPoly = false;
                } else if ((polyId == 13723) && (pc.get_sex() != 0 || !pc.isElf())) {// 不符合變身真妖精條件
                    isPoly = false;
                } else if ((polyId == 13725) && (pc.get_sex() != 1 || !pc.isElf())) {// 不符合變身真女妖精條件
                    isPoly = false;
                } else if ((polyId == 13727) && (pc.get_sex() != 0 || !pc.isWizard())) {// 不符合變身真法師條件
                    isPoly = false;
                } else if ((polyId == 13729) && (pc.get_sex() != 1 || !pc.isWizard())) {// 不符合變身真女法師條件
                    isPoly = false;
                } else if ((polyId == 13731) && (pc.get_sex() != 0 || !pc.isDarkelf())) {// 不符合變身真黑妖條件
                    isPoly = false;
                } else if ((polyId == 13733) && (pc.get_sex() != 1 || !pc.isDarkelf())) {// 不符合變身真女黑妖條件
                    isPoly = false;
                } else if ((polyId == 13735) && (pc.get_sex() != 0 || !pc.isDragonKnight())) {// 不符合變身真龍騎條件
                    isPoly = false;
                } else if ((polyId == 13737) && (pc.get_sex() != 1 || !pc.isDragonKnight())) {// 不符合變身真女龍騎條件
                    isPoly = false;
                } else if ((polyId == 13739) && (pc.get_sex() != 0 || !pc.isIllusionist())) {// 不符合變身真幻術條件
                    isPoly = false;
                } else if ((polyId == 13741) && (pc.get_sex() != 1 || !pc.isIllusionist())) {// 不符合變身真女幻術條件
                    isPoly = false;
                } else if ((polyId >= 13216 && polyId <= 13220) && (!pc.getInventory().checkItem(44212, 1))) {// 不符合變身書條件
                    isPoly = false;
                }
                if (!isPoly && !pc.isGm()) {// 不符合以上變身條件且不是GM
                    // 181:\f1無法變成你指定的怪物。
                    pc.sendPackets(new S_ServerMessage(181));
                    return;
                }
                ArrowGfxid.forItemUSe(pc, polyId);
                pc.removeSkillEffect(SHAPE_CHANGE);// 刪除變身效果
                pc.setSkillEffect(SHAPE_CHANGE, timeSecs * 1000);// 重新給予變身效果
                /*switch (polyId) {  //src014
				case 12280:// 90級變身
				case 12283:
				case 12286:
				case 12295:
				case 12314:
					if (!pc.hasSkillEffect(LV90BUFF)) {// 身上沒有LV90變身buff
						pc.setSkillEffect(LV90BUFF, timeSecs * 1000);
						pc.addMaxHp(100);
						pc.addMaxMp(100);
						pc.addHpr(5);
						pc.addMpr(5);
						pc.addStr(1);
						pc.addDex(1);
						pc.addCon(1);
						pc.addInt(1);
						pc.addWis(1);
						pc.addCha(1);
						pc.addOriginalEr(5);
						pc.add_dodge(1);
						pc.addDmgup(5);
						pc.addBowDmgup(5);
						pc.addSp(3);
						pc.addAc(5);
						pc.addDamageReductionByArmor(5);
						pc.sendPackets(new S_OwnCharStatus2(pc));// 能力素質更新
						pc.sendPackets(new S_PacketBox(S_PacketBox.UPDATE_ER, pc.getEr()));// 迴避率更新
						pc.sendPackets(new S_PacketBoxIcon1(true, pc.get_dodge()));// 閃避率更新
						pc.sendPackets(new S_HPUpdate(pc));
						if (pc.isInParty()) {
							pc.getParty().updateMiniHP(pc);
						}
						pc.sendPackets(new S_MPUpdate(pc));
						pc.sendPackets(new S_SPMR(pc));
						// System.out.println("給予buff");
					}
					break;
				}*/
            /*	if (polyId >= 13715 && polyId <= 13745) {// TOP10變身
					if (!pc.hasSkillEffect(TOP10BUFF)) {// 身上沒有TOP10變身BUFF
						pc.setSkillEffect(TOP10BUFF, timeSecs * 1000);
						pc.addMaxHp(100);
						pc.addMaxMp(100);
						pc.addDmgup(6);
						pc.addBowDmgup(6);
						pc.addSp(4);
						pc.addDamageReductionByArmor(4);
						pc.sendPackets(new S_HPUpdate(pc));
						if (pc.isInParty()) {
							pc.getParty().updateMiniHP(pc);
						}
						pc.sendPackets(new S_MPUpdate(pc));
						pc.sendPackets(new S_SPMR(pc));
						// System.out.println("給予buff");
					}
				}*/
            /*	if (polyId >= 13216 && polyId <= 13220) {// 變身書變身
					if ((pc.getInventory().checkItem(44212, 1)) // 身上有神秘的魔法變身書
							&& (!pc.hasSkillEffect(POLYBOOKBUFF))// 身上沒有變身書BUFF
							&& (!pc.hasSkillEffect(Baphomet))) {// 身上沒有巴風特雕像buff
						pc.setSkillEffect(POLYBOOKBUFF, timeSecs * 1000);
						pc.addMaxHp(20);
						pc.addMaxMp(20);
						pc.addDmgup(3);
						pc.addBowDmgup(3);
						pc.addSp(3);
						pc.addDamageReductionByArmor(2);
						pc.addHpr(2);
						pc.addMpr(2);
						pc.sendPackets(new S_HPUpdate(pc));
						if (pc.isInParty()) {
							pc.getParty().updateMiniHP(pc);
						}
						pc.sendPackets(new S_MPUpdate(pc));
						pc.sendPackets(new S_SPMR(pc));
						// System.out.println("給予buff");
					}
				}*/
                if (pc.getTempCharGfx() != polyId) {
                    L1ItemInstance weapon = pc.getWeapon();
                    boolean weaponTakeoff = (weapon != null) && (!isEquipableWeapon(polyId, weapon.getItem().getType()));
                    // if (pc.getloginpoly() == 1) {
                    //     L1PolyPower.removePolyEffects(pc, pc.getTempCharGfx());
                    // }
                    pc.setTempCharGfx(polyId);
                    pc.sendPackets(new S_ChangeShape(pc, polyId, weaponTakeoff));
                    if ((!pc.isGmInvis()) && (!pc.isInvisble())) {
                        pc.broadcastPacketAll(new S_ChangeShape(pc, polyId));
                    }

                    pc.getInventory().takeoffEquip(polyId);
                    if (weapon != null) {
                        pc.sendPacketsAll(new S_CharVisualUpdate(pc));
                    }
                    boolean check = false;
                    int range = 1;
                    int type = 1;
                    if (weapon == null) {
                        pc.sendPackets(new S_PacketBox(S_PacketBox.WEAPON_RANGE, range, 0, check));
                    } else {
                        if (weapon.getItem().getType() == 4) {
                            range = 17;
                        } else if ((weapon.getItem().getType() == 10) || (weapon.getItem().getType() == 13)) {
                            range = 14;
                        } else if ((weapon.getItem().getType() == 5) || (weapon.getItem().getType() == 14) || (weapon.getItem().getType() == 18)) {
                            range = 1;
                            /*if ((polyId == 11330) || (polyId == 11344) || (polyId == 11351) || (polyId == 11368)
									|| (polyId == 12240) || (polyId == 12237) || (polyId == 11447) || (polyId == 11408)
									|| (polyId == 11409) || (polyId == 11410) || (polyId == 11411) || (polyId == 11418)
									|| (polyId == 11419) || (polyId == 12613) || (polyId == 12614)) {*/
                            if ((polyId == 11330) || (polyId == 11344) || (polyId == 11351) || (polyId == 11368) || (polyId == 12240) || (polyId == 12237) || (polyId == 11447) || (polyId == 11408) || (polyId == 11409) || (polyId == 11410) || (polyId == 11411) || (polyId == 11418) || (polyId == 15531) || (polyId == 15832) || (polyId == 15833) || (polyId == 15539) || (polyId == 15537) || (polyId == 15534) || (polyId == 15599) || (polyId == 13152) || (polyId == 13153) || (polyId == 12681) || (polyId == 12702) || (polyId == 11419) || (polyId == 12613) || (polyId == 12614)) {
                                range = 2;
                            } else if (!pc.hasSkillEffect(L1SkillId.SHAPE_CHANGE)) {
                                range = 2;
                            }
                        }
                        if (pc.isKnight()) {
                            if (weapon.getItem().getType() == 3) {
                                check = true;
                            }
                        } else if (pc.isElf()) {
                            if (pc.hasSkillEffect(L1SkillId.FIRE_BLESS)) {
                                check = true;
                            }
                            if (((weapon.getItem().getType() == 4) || (weapon.getItem().getType() == 13)) && (weapon.getItem().getType1() == 20)) {
                                type = 3;
                                check = true;
                            }
                        } else if (pc.isDragonKnight()) {
                            check = true;
                            if ((weapon.getItem().getType() == 14) || (weapon.getItem().getType() == 18)) {
                                type = 10;
                            }
                        }
                        if ((weapon.getItem().getType1() != 20) && (weapon.getItem().getType1() != 62)) {
                            pc.sendPackets(new S_PacketBox(S_PacketBox.WEAPON_RANGE, range, type, check));
                        } else {
                            pc.sendPackets(new S_PacketBox(S_PacketBox.WEAPON_RANGE, range, 3, check));
                        }
                        pc.setRange(range);
                    }
                    // pc.resetPolyPower();
                }
                // if (pc.getloginpoly() == 1) {
                //     System.out.println("loginpoly is 1, applying poly effects.");
                //     L1PolyPower.addPolyEffects(pc, pc.getTempCharGfx());
                // }

                if (timeSecs != 0) {  //src013
                    ArrowGfxid.forItemUSe(pc, polyId);
                    pc.sendPackets(new S_PacketBox(35, timeSecs));
                }
                pc.resetPolyPower();
            } else if ((cha instanceof L1MonsterInstance)) {
                L1MonsterInstance mob = (L1MonsterInstance) cha;
                mob.removeSkillEffect(SHAPE_CHANGE);// 刪除變身效果
                mob.setSkillEffect(SHAPE_CHANGE, timeSecs * 1000);
                if (mob.getTempCharGfx() != polyId) {
                    mob.setTempCharGfx(polyId);
                    mob.broadcastPacketAll(new S_ChangeShape(mob, polyId));
                }
            } else if ((cha instanceof L1DeInstance)) {
                L1DeInstance de = (L1DeInstance) cha;
                de.removeSkillEffect(SHAPE_CHANGE);// 刪除變身效果
                de.setSkillEffect(SHAPE_CHANGE, timeSecs * 1000);
                if (de.getTempCharGfx() != polyId) {
                    de.setTempCharGfx(polyId);
                    de.broadcastPacketAll(new S_ChangeShape(de, polyId));
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 取消變身
     *
     */
    public static void undoPoly(L1Character cha) {//src014
        try {
            if ((cha instanceof L1PcInstance)) {
                L1PcInstance pc = (L1PcInstance) cha;
                // if (pc.getloginpoly() == 1) {
                //     L1PolyPower.removePolyEffects(pc, pc.getTempCharGfx());
                // }
                ArrowGfxid.forItemUSe(pc, pc.getTempCharGfx());
                int classId = pc.getClassId();
                pc.setTempCharGfx(classId);
                pc.sendPacketsAll(new S_ChangeShape(pc, classId));
                // pc.resetPolyPower();
                if (pc.hasSkillEffect(POLYBOOKBUFF)) {
                    pc.removeSkillEffect(POLYBOOKBUFF);
                    // System.out.println("刪除BUFF-取消變身");
                }
                if (pc.hasSkillEffect(TOP10BUFF)) {
                    pc.removeSkillEffect(TOP10BUFF);
                    // System.out.println("刪除BUFF-取消變身");
                }
                if (pc.hasSkillEffect(LV90BUFF)) {
                    pc.removeSkillEffect(LV90BUFF);
                    // System.out.println("刪除BUFF-取消變身");
                }
                L1ItemInstance weapon = pc.getWeapon();
                if (weapon != null) {
                    pc.sendPacketsAll(new S_CharVisualUpdate(pc));
                }
                boolean check = false;
                int range = 1;
                int polyId = pc.getTempCharGfx();
                int type = 1;
                if (weapon == null) {
                    pc.sendPackets(new S_PacketBox(S_PacketBox.WEAPON_RANGE, range, 0, check));
                } else {
                    if (weapon.getItem().getType() == 4) {
                        range = 17;
                        //range = 15;
                    } else if ((weapon.getItem().getType() == 10) || (weapon.getItem().getType() == 13)) {
                        range = 14;
                    } else if ((weapon.getItem().getType() == 5) || (weapon.getItem().getType() == 14) || (weapon.getItem().getType() == 18)) {
                        range = 1;
                        /*if ((polyId == 11330) || (polyId == 11344) || (polyId == 11351) || (polyId == 11368)
								|| (polyId == 12240) || (polyId == 12237) || (polyId == 11447) || (polyId == 11408)
								|| (polyId == 11409) || (polyId == 11410) || (polyId == 11411) || (polyId == 11418)
								|| (polyId == 11419) || (polyId == 12613) || (polyId == 12614)) {*/
                        if ((polyId == 11330) || (polyId == 11344) || (polyId == 11351) || (polyId == 11368) || (polyId == 12240) || (polyId == 12237) || (polyId == 11447) || (polyId == 11408) || (polyId == 11409) || (polyId == 11410) || (polyId == 11411) || (polyId == 11418) || (polyId == 15531) || (polyId == 15832) || (polyId == 15833) || (polyId == 15539) || (polyId == 15537) || (polyId == 15534) || (polyId == 15599) || (polyId == 13152) || (polyId == 13153) || (polyId == 12681) || (polyId == 12702) || (polyId == 11419) || (polyId == 12613) || (polyId == 12614)) {
                            range = 2;
                        } else if (!pc.hasSkillEffect(L1SkillId.SHAPE_CHANGE)) {
                            range = 2;
                        }
                    }
                    if (pc.isKnight()) {
                        if (weapon.getItem().getType() == 3) {
                            check = true;
                        }
                    } else if (pc.isElf()) {
                        if (pc.hasSkillEffect(L1SkillId.FIRE_BLESS)) {
                            check = true;
                        }
                        if (((weapon.getItem().getType() == 4) || (weapon.getItem().getType() == 13)) && (weapon.getItem().getType1() == 20)) {
                            type = 3;
                            check = true;
                        }
                    } else if (pc.isDragonKnight()) {
                        check = true;
                        if ((weapon.getItem().getType() == 14) || (weapon.getItem().getType() == 18)) {
                            type = 10;
                        }
                    }
                    if ((weapon.getItem().getType1() != 20) && (weapon.getItem().getType1() != 62)) {
                        pc.sendPackets(new S_PacketBox(S_PacketBox.WEAPON_RANGE, range, type, check));
                    } else {
                        pc.sendPackets(new S_PacketBox(S_PacketBox.WEAPON_RANGE, range, 3, check));
                    }
                    pc.setRange(range);
                }
                pc.resetPolyPower();
            } else if ((cha instanceof L1MonsterInstance)) {
                L1MonsterInstance mob = (L1MonsterInstance) cha;
                mob.setTempCharGfx(0);
                mob.broadcastPacketAll(new S_ChangeShape(mob, mob.getGfxId()));
            } else if ((cha instanceof L1DeInstance)) {
                L1DeInstance de = (L1DeInstance) cha;
                de.setTempCharGfx(0);
                de.broadcastPacketAll(new S_ChangeShape(de, de.getGfxId()));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 是否為可裝備的武器
     *
     */
    public static boolean isEquipableWeapon(int polyId, int weaponType) {
        try {
            L1PolyMorph poly = PolyTable.get().getTemplate(polyId);
            if (poly == null) {
                return true;
            }
            Integer flg = (Integer) weaponFlgMap.get(weaponType);
            if (flg != null) {
                return (poly.getWeaponEquipFlg() & flg) != 0;
            }
            return true;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return true;
    }

    /**
     * 是否為可裝備的防具
     *
     */
    public static boolean isEquipableArmor(int polyId, int armorType) {
        try {
            L1PolyMorph poly = PolyTable.get().getTemplate(polyId);
            if (poly == null) {
                return true;
            }
            Integer flg = (Integer) armorFlgMap.get(armorType);
            if (flg != null) {
                return (poly.getArmorEquipFlg() & flg) != 0;
            }
            return true;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return true;
    }

    /**
     * 變身原因
     *
     * @return true 可以變身 ; false 無法變身
     */
    public static boolean isMatchCause(final int polyId, final int cause) {
        try {
            final L1PolyMorph poly = PolyTable.get().getTemplate(polyId);
            if (poly == null) {
                return true;
            }
            if (cause == MORPH_BY_LOGIN) {
                return true;
            }
            return 0 != (poly.getCauseFlg() & cause);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    /**
     * 變身原因 (檢查任務ID)
     *
     * @return true 可以變身 ; false 無法變身
     */
    public static boolean isQuestIdEnd(final L1PcInstance pc, final int polyId) {
        try {
            final L1PolyMorph poly = PolyTable.get().getTemplate(polyId);
            if (poly == null) {
                return true;
            }
            return CardQuestTable.get().IsQuest(pc, poly.getQuestId());
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    /**
     * 3.80 更動(個人商店變身)
     */
    public static void doPolyPraivateShop(L1Character cha, int polyIndex) {
        if ((cha == null) || cha.isDead()) {
            return;
        }
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            int PolyList[] = {11479, 11427, 10047, 9688, 11322, 10069, 10034, 10032};
            if (pc.getTempCharGfx() != PolyList[polyIndex - 1]) {// 外型編號不相同
                L1ItemInstance weapon = pc.getWeapon();
                boolean weaponTakeoff = (weapon != null && !isEquipableWeapon(PolyList[polyIndex - 1], weapon.getItem().getType()));
                pc.setTempCharGfx(PolyList[polyIndex - 1]);
                pc.sendPackets(new S_ChangeShape(pc, PolyList[polyIndex - 1], weaponTakeoff));
                if ((!pc.isGmInvis()) && (!pc.isInvisble())) {
                    pc.broadcastPacketAll(new S_ChangeShape(pc, PolyList[polyIndex - 1]));
                }
                pc.getInventory().takeoffEquip(PolyList[polyIndex - 1]); // 是否將裝備的武器強制解除。
                pc.sendPacketsAll(new S_CharVisualUpdate(pc, 70));
            }
        }
    }

    /**
     * 3.80 更動(個人商店取消變身)
     */
    public static void undoPolyPrivateShop(L1Character cha) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            if (pc.getTempCharGfx() == 11479 || pc.getTempCharGfx() == 11427 || pc.getTempCharGfx() == 10047 || pc.getTempCharGfx() == 9688 || pc.getTempCharGfx() == 11322 || pc.getTempCharGfx() == 10069 || pc.getTempCharGfx() == 10034 || pc.getTempCharGfx() == 10032) {
                int classId = pc.getClassId();
                pc.setTempCharGfx(classId);
                if (!pc.isDead()) {
                    pc.sendPacketsAll(new S_ChangeShape(pc, classId));
                    pc.sendPacketsAll(new S_CharVisualUpdate(pc, pc.getCurrentWeapon()));
                }
            }
        }
    }

    public int getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public int getPolyId() {
        return _polyId;
    }

    public int getMinLevel() {
        return _minLevel;
    }

    public int getWeaponEquipFlg() {
        return _weaponEquipFlg;
    }

    public int getArmorEquipFlg() {
        return _armorEquipFlg;
    }

    public boolean canUseSkill() {
        return _canUseSkill;
    }

    public int getCauseFlg() {
        return _causeFlg;
    }

    public int getQuestId() {
        return _questId;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.L1PolyMorph JD-Core Version: 0.6.2
 */