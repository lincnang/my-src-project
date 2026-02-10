package com.lineage.server.utils;

import com.lineage.config.ConfigOtherSet2;
import com.lineage.config.ConfigRate;
import com.lineage.data.event.LeavesSet;
import com.lineage.data.item_weapon.proficiency.L1WeaponProficiency;
import com.lineage.server.datatables.ExpMeteUpTable;
import com.lineage.server.datatables.ExpTable;
import com.lineage.server.datatables.MapExpTable;
import com.lineage.server.datatables.lock.PetReading;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_ItemName;
import com.lineage.server.serverpackets.S_NPCPack_Pet;
import com.lineage.server.serverpackets.S_PacketBoxExp;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Pet;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Calendar;

public class CalcExp {
    private static final Log _log = LogFactory.getLog(CalcExp.class);

    public static void calcExp(L1PcInstance srcpc, int targetid, ArrayList<?> acquisitorList, ArrayList<Integer> hateList, long exp) {
        try {
            int i = 0;
            double party_level = 0.0D;
            double dist = 0.0D;
            long member_exp = 0L;
            int member_lawful = 0;
            L1Object object = World.get().findObject(targetid);
            L1NpcInstance npc = null;
            if ((object instanceof L1NpcInstance)) {
                npc = (L1NpcInstance) object;
            } else {
                return;
            }
            int hate = 0;
            long acquire_exp = 0L;
            int acquire_lawful = 0;
            long party_exp = 0L;
            int party_lawful = 0;
            long totalHateExp = 0L;
            int totalHateLawful = 0;
            long partyHateExp = 0L;
            int partyHateLawful = 0;
            long ownHateExp = 0L;
            if (acquisitorList.size() != hateList.size()) {
                return;
            }
            for (i = hateList.size() - 1; i >= 0; i--) {
                L1Character acquisitor = (L1Character) acquisitorList.get(i);
                hate = hateList.get(i);
                boolean isRemove = acquisitor instanceof L1IllusoryInstance;
                if ((acquisitor instanceof L1EffectInstance)) {
                    isRemove = true;
                }
                if (isRemove) {
                    acquisitorList.remove(i);
                    hateList.remove(i);
                } else if ((acquisitor != null) && (!acquisitor.isDead())) {
                    totalHateExp += hate;
                    if ((acquisitor instanceof L1PcInstance)) {
                        totalHateLawful += hate;
                    }
                } else {
                    acquisitorList.remove(i);
                    hateList.remove(i);
                }
            }
            if (totalHateExp == 0L) {
                return;
            }
            if (!(npc instanceof L1PetInstance) && !(npc instanceof L1SummonInstance)) {
                if ((World.get().isProcessingContributionTotal()) && (srcpc.getHomeTownId() > 0)) {
                    int contribution = npc.getLevel() / 10;
                    srcpc.addContribution(contribution);
                }
                int lawful = npc.getLawful();
                if (srcpc.isInParty()) {
                    for (i = hateList.size() - 1; i >= 0; i--) {
                        L1Character acquisitor = (L1Character) acquisitorList.get(i);
                        hate = hateList.get(i);
                        if ((acquisitor instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) acquisitor;
                            if (pc == srcpc) {
                                partyHateExp += hate;
                                partyHateLawful += hate;
                            } else if (srcpc.getParty().isMember(pc)) {
                                partyHateExp += hate;
                                partyHateLawful += hate;
                            } else {
                                if (totalHateExp > 0L) {
                                    acquire_exp = exp * hate / totalHateExp;
                                }
                                if (totalHateLawful > 0) {
                                    acquire_lawful = lawful * hate / totalHateLawful;
                                }
                                addExp(pc, acquire_exp, acquire_lawful);
                            }
                        } else if ((acquisitor instanceof L1PetInstance)) {
                            L1PetInstance pet = (L1PetInstance) acquisitor;
                            L1PcInstance master = (L1PcInstance) pet.getMaster();
                            if (master == srcpc) {
                                partyHateExp += hate;
                            } else if (srcpc.getParty().isMember(master)) {
                                partyHateExp += hate;
                            } else {
                                if (totalHateExp > 0L) {
                                    acquire_exp = exp * hate / totalHateExp;
                                }
                                addExpPet(pet, acquire_exp);
                            }
                        } else if ((acquisitor instanceof L1SummonInstance)) {
                            L1SummonInstance summon = (L1SummonInstance) acquisitor;
                            L1PcInstance master = (L1PcInstance) summon.getMaster();
                            if (master == srcpc) {
                                partyHateExp += hate;
                            } else if (srcpc.getParty().isMember(master)) {
                                partyHateExp += hate;
                            }
                        }
                    }
                    if (totalHateExp > 0L) {
                        party_exp = exp * partyHateExp / totalHateExp;
                    }
                    if (totalHateLawful > 0) {
                        party_lawful = lawful * partyHateLawful / totalHateLawful;
                    }
                    double pri_bonus = 0.0D;
                    L1PcInstance leader = srcpc.getParty().getLeader();
                    if ((leader.isCrown()) && ((srcpc.knownsObject(leader)) || (srcpc.equals(leader)))) {
                        pri_bonus = 0.059D;
                    }
                    // Object[] pcs = srcpc.getParty().partyUsers().values().toArray();
                    final Object[] pcs = srcpc.getParty().getMemberList().toArray();// 7.6
                    double pt_bonus = 0.0D;
                    for (final Object obj : pcs) {
                        if ((obj instanceof L1PcInstance)) {
                            L1PcInstance each = (L1PcInstance) obj;
                            if (!each.isDead()) {
                                if ((srcpc.knownsObject(each)) || (srcpc.equals(each))) {
                                    party_level += each.getLevel() * each.getLevel();
                                }
                                if (srcpc.knownsObject(each)) {
                                    pt_bonus += 0.04D;
                                }
                            }
                        }
                    }
                    party_exp = (long) (party_exp * (1.0D + pt_bonus + pri_bonus));
                    if (party_level > 0.0D) {
                        dist = srcpc.getLevel() * srcpc.getLevel() / party_level;
                    }
                    member_exp = (long) (party_exp * dist);
                    member_lawful = (int) (party_lawful * dist);
                    for (i = hateList.size() - 1; i >= 0; i--) {
                        L1Character acquisitor = (L1Character) acquisitorList.get(i);
                        hate = hateList.get(i);
                        if ((acquisitor instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) acquisitor;
                            if (pc == srcpc) {
                                ownHateExp += hate;
                            }
                        } else if ((acquisitor instanceof L1PetInstance)) {
                            L1PetInstance pet = (L1PetInstance) acquisitor;
                            L1PcInstance master = (L1PcInstance) pet.getMaster();
                            if (master == srcpc) {
                                ownHateExp += hate;
                            }
                        } else if ((acquisitor instanceof L1SummonInstance)) {
                            L1SummonInstance summon = (L1SummonInstance) acquisitor;
                            L1PcInstance master = (L1PcInstance) summon.getMaster();
                            if (master == srcpc) {
                                ownHateExp += hate;
                            }
                        }
                    }
                    if (ownHateExp != 0L) {
                        for (i = hateList.size() - 1; i >= 0; i--) {
                            L1Character acquisitor = (L1Character) acquisitorList.get(i);
                            hate = hateList.get(i);
                            if ((acquisitor instanceof L1PcInstance)) {
                                L1PcInstance pc = (L1PcInstance) acquisitor;
                                if (pc == srcpc) {
                                    if (ownHateExp > 0L) {
                                        acquire_exp = member_exp * hate / ownHateExp;
                                    }
                                    addExp(pc, acquire_exp, member_lawful);
                                }
                            } else if ((acquisitor instanceof L1PetInstance)) {
                                L1PetInstance pet = (L1PetInstance) acquisitor;
                                L1PcInstance master = (L1PcInstance) pet.getMaster();
                                if (master == srcpc) {
                                    if (ownHateExp > 0L) {
                                        acquire_exp = member_exp * hate / ownHateExp;
                                    }
                                    addExpPet(pet, acquire_exp);
                                }
                            } else {
                                if (acquisitor instanceof L1SummonInstance)
                                    ;
                            }
                        }
                    } else {
                        addExp(srcpc, member_exp, member_lawful);
                    }
                    for (final Object obj : pcs) {
                        if ((obj instanceof L1PcInstance)) {
                            L1PcInstance tgpc = (L1PcInstance) obj;
                            if (!tgpc.isDead()) {
                                if (srcpc.knownsObject(tgpc)) {
                                    if (party_level > 0.0D) {
                                        dist = tgpc.getLevel() * tgpc.getLevel() / party_level;
                                    }
                                    member_exp = (int) (party_exp * dist);
                                    member_lawful = (int) (party_lawful * dist);
                                    ownHateExp = 0L;
                                    for (i = hateList.size() - 1; i >= 0; i--) {
                                        L1Character acquisitor = (L1Character) acquisitorList.get(i);
                                        hate = hateList.get(i);
                                        if ((acquisitor instanceof L1PcInstance)) {
                                            L1PcInstance pc = (L1PcInstance) acquisitor;
                                            if (pc == tgpc) {
                                                ownHateExp += hate;
                                            }
                                        } else if ((acquisitor instanceof L1PetInstance)) {
                                            L1PetInstance pet = (L1PetInstance) acquisitor;
                                            L1PcInstance master = (L1PcInstance) pet.getMaster();
                                            if (master == tgpc) {
                                                ownHateExp += hate;
                                            }
                                        } else if ((acquisitor instanceof L1SummonInstance)) {
                                            L1SummonInstance summon = (L1SummonInstance) acquisitor;
                                            L1PcInstance master = (L1PcInstance) summon.getMaster();
                                            if (master == tgpc) {
                                                ownHateExp += hate;
                                            }
                                        }
                                    }
                                    if (ownHateExp != 0L) {
                                        for (i = hateList.size() - 1; i >= 0; i--) {
                                            L1Character acquisitor = (L1Character) acquisitorList.get(i);
                                            hate = hateList.get(i);
                                            if ((acquisitor instanceof L1PcInstance)) {
                                                L1PcInstance pc = (L1PcInstance) acquisitor;
                                                if (pc == tgpc) {
                                                    if (ownHateExp > 0L) {
                                                        acquire_exp = member_exp * hate / ownHateExp;
                                                    }
                                                    addExp(pc, acquire_exp, member_lawful);
                                                }
                                            } else if ((acquisitor instanceof L1PetInstance)) {
                                                L1PetInstance pet = (L1PetInstance) acquisitor;
                                                L1PcInstance master = (L1PcInstance) pet.getMaster();
                                                if (master == tgpc) {
                                                    if (ownHateExp > 0L) {
                                                        acquire_exp = member_exp * hate / ownHateExp;
                                                    }
                                                    addExpPet(pet, acquire_exp);
                                                }
                                            } else {
                                                if (acquisitor instanceof L1SummonInstance)
                                                    ;
                                            }
                                        }
                                    } else {
                                        addExp(tgpc, member_exp, member_lawful);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    for (i = hateList.size() - 1; i >= 0; i--) {
                        L1Character acquisitor = (L1Character) acquisitorList.get(i);
                        hate = hateList.get(i);
                        acquire_exp = exp * hate / totalHateExp;
                        if (((acquisitor instanceof L1PcInstance)) && (totalHateLawful > 0)) {
                            acquire_lawful = lawful * hate / totalHateLawful;
                        }
                        if ((acquisitor instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) acquisitor;
                            addExp(pc, acquire_exp, acquire_lawful);
                        } else if ((acquisitor instanceof L1PetInstance)) {
                            L1PetInstance pet = (L1PetInstance) acquisitor;
                            addExpPet(pet, acquire_exp);
                        } else {
                            if (acquisitor instanceof L1SummonInstance)
                                ;
                        }
                    }
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private static void addExp(L1PcInstance pc, long exp, int lawful) {
        try {
            int add_lawful = (int) (lawful * ConfigRate.RATE_LA) * -1;
            pc.addLawful(add_lawful);
            if (pc.getLevel() >= ExpTable.MAX_LEVEL) {
                return;
            }
            /*
             * if ((ConfigAlt.APPRENTICE_SWITCH) && (pc.getApprentice() != null) &&
             * (pc.getApprentice().getMaster().getId() != pc.getId()) &&
             * (pc.getApprentice().getMaster().getMapId() == pc.getMapId())) { L1Party party
             * = pc.getParty(); if (party != null) { int checkType =
             * party.checkMentor(pc.getApprentice()); if (checkType > 4) { double expBonus =
             * exp * exp_rate;
             *
             * double exppenalty =
             * ExpTable.getPenaltyRate(pc.getApprentice().getMaster().getLevel(),
             * pc.getApprentice().getMaster().getMeteLevel());
             *
             * if (exppenalty < 1.0D) { expBonus *= exppenalty; }
             *
             * expBonus = expBonus * (ConfigAlt.APPRENTICE_EXP_BONUS / 100 * (checkType -
             * 4));
             *
             * pc.getApprentice().getMaster().addExp((long) expBonus); } } }
             */
            // 師徒系統 師父經驗加成 取消
            /*
             * if (pc.getMasterID() == -1) { exp *= 1 + (0.1 *
             * L1Master.getInstance().getDiscipleCount(pc.getId())); }
             */
            double addExp = exp; // src013
            if (pc.getExpRateToPc() > 0.0) {
                addExp *= 1 + pc.getExpRateToPc();
            }
            double exppenalty = ExpTable.getPenaltyRate(pc.getLevel());
            if (exppenalty < 1.0D) {
                addExp *= exppenalty;
            }
            final Calendar cal = Calendar.getInstance();
            final int day_of_week = cal.get(Calendar.DAY_OF_WEEK);
            if (ConfigRate.RATE_XP > 1.0) {
                if (day_of_week == Calendar.SUNDAY || day_of_week == Calendar.SATURDAY) {
                    addExp *= (ConfigRate.RATE_XP + ConfigRate.RATE_XP_WEEK);
                } else {
                    addExp *= ConfigRate.RATE_XP;
                }
            }
            /*
             * if (exp_rate > 1.0D) { addExp *= exp_rate; }
             */
            // [修正] 處理 殷海薩(Leaves) 與 龍之祝福(DragonExp) 的經驗加成
            int totalBonusExp = 0;

            // 1. 處理傳統殷海薩 (LeavesSet)
            if ((LeavesSet.START) && (pc.get_other().get_leaves_time_exp() > 0)) {
                int bonus = (int) (addExp * 0.77D); // 額外獲得 77%
                int remaining = pc.get_other().get_leaves_time_exp() - bonus;
                if (remaining > 0) {
                    pc.get_other().set_leaves_time_exp(remaining);
                    pc.sendPackets(new S_PacketBoxExp(pc.get_other().get_leaves_time_exp() / LeavesSet.EXP));
                } else {
                    bonus = pc.get_other().get_leaves_time_exp();
                    pc.get_other().set_leaves_time_exp(0);
                    pc.sendPackets(new S_PacketBoxExp());
                }
                totalBonusExp += bonus;
            }

            // 2. 處理龍之祝福 (DragonExp) - 優先順序視需求而定，此處累加
            if (pc.getDragonExp() > 0) {
                int bonus = (int) (addExp * 0.77D); // 龍之祝福同樣給予 77% (可依需求調整)
                int currentDragonExp = pc.getDragonExp();
                if (currentDragonExp >= bonus) {
                    pc.setDragonExp(currentDragonExp - bonus);
                    totalBonusExp += bonus;
                } else {
                    totalBonusExp += currentDragonExp;
                    pc.setDragonExp(0);
                }
                // 同步更新 DragonExp 快取/DB (定時任務也會存，但此處建議即時同步重要變動)
                // com.add.Tsai.DragonExp.get().flushFromPc(pc);
            }

            addExp += totalBonusExp;

            addExp *= add(pc);// 經驗加倍道具
            addExp *= ExpMeteUpTable.get().getRate(pc.getMeteLevel());
            /* [原碼] 修正經驗值大於2147483647會變負值.暴等 */
            if (addExp < 0) {
                return;
            }
            //if (addExp > 36065092) {
            //    addExp = 36065092;
            //}
            int finalExp = (int) Math.min(addExp, 18032546);
            pc.addExp(finalExp);
            L1WeaponProficiency proficiency = pc.getProficiency();
            if (proficiency != null) {//TODO 玩家武器熟練度經驗
                pc.getProficiency().addProficiencyExp(finalExp / ConfigOtherSet2.Weapon_Exp_Rate);// 請自行設定經驗值獲取方式
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 經驗加倍計算
     *
     */
    private static double add(L1PcInstance pc) {
        try {
            double add_exp = 1.0D;
            if (pc.is_mazu()) {// 媽祖祝福增加100%
                add_exp += ConfigOtherSet2.Mazu_Exp; // 1.0D
            }
            if (pc.hasSkillEffect(L1SkillId.LEVEL_UP_BONUS)) { // 升級經驗獎勵狀態
                add_exp += ConfigOtherSet2.LEVEL_UP_EXP; // 2.23D
            }
            if (pc.getExpPoint() > 0) {// 裝備增加EXP
                add_exp += pc.getExpPoint() / 100.0D;
            }
            if (pc.getInventory().checkItem(30078) && !pc.getInventory().checkEquipped(30078)) {
                add_exp += 0.05D;
            }
            // 2017/04/25
            double a1 = 0.0D;
            if (pc.hasSkillEffect(9975)) {
                a1 = 0.02D;
            }
            if (a1 > 0.0D) {
                add_exp += a1;
            }
            double foodBonus = 0.0D;
            if ((pc.hasSkillEffect(3007)) || (pc.hasSkillEffect(3015))) {
                foodBonus = 0.01D;
            }
            if ((pc.hasSkillEffect(3023)) || (pc.hasSkillEffect(3031))) {
                foodBonus = 0.02D;
            }
            if ((pc.hasSkillEffect(3039)) || (pc.hasSkillEffect(3047))) {
                foodBonus = 0.03D;
            }
            if ((pc.hasSkillEffect(3048)) || (pc.hasSkillEffect(3049)) || (pc.hasSkillEffect(3050))) {// 新料理
                foodBonus = 0.02D;
            }
            if (pc.hasSkillEffect(3051)) {// 新料理
                foodBonus = 0.04D;
            }
            /* 1段經驗加倍效果 */
            if (pc.hasSkillEffect(6668)) {
                foodBonus = 0.3D;
            }
            if (pc.hasSkillEffect(6669)) {
                foodBonus = 0.5D;
            }
            if (pc.hasSkillEffect(6670)) {
                foodBonus = 0.7D;
            }
            if (pc.hasSkillEffect(6666)) {
                foodBonus = 1.0D;
            }
            if (pc.hasSkillEffect(6667)) {
                foodBonus = 1.5D;
            }
            if (pc.hasSkillEffect(6671)) {
                foodBonus = 2.0D;
            }
            if (pc.hasSkillEffect(6672)) {
                foodBonus = 2.5D;
            }
            if (pc.hasSkillEffect(6673)) {
                foodBonus = 3.0D;
            }
            if (pc.hasSkillEffect(6674)) {
                foodBonus = 3.5D;
            }
            if (pc.hasSkillEffect(6675)) {
                foodBonus = 4.0D;
            }
            if (pc.hasSkillEffect(6676)) {
                foodBonus = 4.5D;
            }
            if (pc.hasSkillEffect(6677)) {
                foodBonus = 5.0D;
            }
            if (pc.hasSkillEffect(6678)) {
                foodBonus = 5.5D;
            }
            if (pc.hasSkillEffect(6679)) {
                foodBonus = 6.0D;
            }
            if (pc.hasSkillEffect(6680)) {
                foodBonus = 6.5D;
            }
            if (pc.hasSkillEffect(6681)) {
                foodBonus = 7.0D;
            }
            if (foodBonus > 0.0D) {
                add_exp += foodBonus;
            }
            if (pc.getLevel() > ConfigOtherSet2.partyexplv && pc.getParty() != null && ConfigOtherSet2.partyexp) {
                int partyUsers = pc.getParty().partyUserInMap(pc.getMapId());
                if (partyUsers > 0 && partyUsers <= 10) {
                    add_exp += partyUsers * ConfigOtherSet2.partynum10 / 100.0D;
                }
                if (partyUsers > 10 && partyUsers <= 20) {
                    add_exp += partyUsers * ConfigOtherSet2.partynum20 / 100.0D;
                }
                if (partyUsers > 20 && partyUsers <= 30) {
                    add_exp += partyUsers * ConfigOtherSet2.partynum30 / 100.0D;
                }
                if (partyUsers > 30) {
                    add_exp += 30 * ConfigOtherSet2.partynum30 / 100.0D;
                }
            }
            add_exp += pc.getExpAdd();// 娃娃增加EXP
            /* 2段經驗加倍效果 */
            double s2_exp = 0.0D;
            if (pc.hasSkillEffect(L1SkillId.SEXP13)) {
                s2_exp = 0.3D;
            }
            if (pc.hasSkillEffect(L1SkillId.SEXP30)) {
                s2_exp = 2.0D;
            }
            if (pc.hasSkillEffect(L1SkillId.SEXP150)) {
                s2_exp = 0.5D;
            }
            if (pc.hasSkillEffect(L1SkillId.SEXP175)) {
                s2_exp = 0.75D;
            }
            if (pc.hasSkillEffect(L1SkillId.SEXP200)) {
                s2_exp = 1.0D;
            }
            if (pc.hasSkillEffect(L1SkillId.SEXP225)) {
                s2_exp = 1.25D;
            }
            if (pc.hasSkillEffect(L1SkillId.SEXP250)) {
                s2_exp = 1.5D;
            }
            if (pc.hasSkillEffect(L1SkillId.SEXP300)) {
                s2_exp = 2.0D;
            }
            if (pc.hasSkillEffect(L1SkillId.SEXP500)) {
                s2_exp = 4.0D;
            }
            if (s2_exp > 0.0D) {
                add_exp += s2_exp;
            }
            final int mapid = pc.getMapId();
            // 地圖經驗加倍
            if (MapExpTable.get().get_level(mapid, pc.getLevel())) {
                add_exp += (MapExpTable.get().get_exp(mapid) - 1.0D);
            }
            if (pc.getNEExpUp() != 0) {
                add_exp += (pc.getNEExpUp() / 100D);
            }
            return add_exp;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return 1.0D;
    }

    /**
     * 寵物獲得經驗值判斷
     *
     */
    private static void addExpPet(L1PetInstance pet, long exp) {
        try {
            if (pet == null) {
                return;
            }
            if (pet.getPetType() == null) {
                return;
            }
            L1PcInstance pc = (L1PcInstance) pet.getMaster();
            if (pc == null) {
                return;
            }
            int petItemObjId = pet.getItemObjId();
            int levelBefore = pet.getLevel();
            long totalExp = (long) (exp * ConfigRate.RATE_XP_PET + pet.getExp());
            int maxLevel = ConfigRate.Pet_High_Level + 1;// 寵物等級限制
            if (totalExp >= ExpTable.getExpByLevel(maxLevel)) {
                totalExp = ExpTable.getExpByLevel(maxLevel) - 1L;
            }
            pet.setExp(totalExp);
            pet.setLevel(ExpTable.getLevelByExp(totalExp));
            int expPercentage = ExpTable.getExpPercentage(pet.getLevel(), totalExp);
            int gap = pet.getLevel() - levelBefore;
            for (int i = 1; i <= gap; i++) {
                RangeInt hpUpRange = pet.getPetType().getHpUpRange();
                RangeInt mpUpRange = pet.getPetType().getMpUpRange();
                pet.addMaxHp(hpUpRange.randomValue());
                pet.addMaxMp(mpUpRange.randomValue());
            }
            pet.setExpPercent(expPercentage);
            pc.sendPackets(new S_NPCPack_Pet(pet, pc));
            if (gap != 0) {
                L1Pet petTemplate = PetReading.get().getTemplate(petItemObjId);
                if (petTemplate == null) {
                    return;
                }
                petTemplate.set_exp((int) pet.getExp());
                petTemplate.set_level(pet.getLevel());
                petTemplate.set_hp(pet.getMaxHp());
                petTemplate.set_mp(pet.getMaxMp());
                PetReading.get().storePet(petTemplate);
                pc.sendPackets(new S_ServerMessage(320, pet.getName()));
                L1ItemInstance item = WorldItem.get().getItem(pet.getItemObjId());// 找回項圈
                pc.sendPackets(new S_ItemName(item));// 更新項圈訊息
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
