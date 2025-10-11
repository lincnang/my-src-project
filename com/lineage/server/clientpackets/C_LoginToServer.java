package com.lineage.server.clientpackets;

import com.add.MJBookQuestSystem.Loader.UserMonsterBookLoader;
import com.add.MJBookQuestSystem.Loader.UserWeekQuestLoader;
import com.add.Tsai.*;
import com.lineage.DatabaseFactory;
import com.lineage.config.*;
import com.lineage.data.event.*;
import com.lineage.data.event.SoulQueen.SoulTowerThread;
import com.lineage.data.event.ValakasRoom.ValakasRoomSystem;
import com.lineage.data.event.ice.IceQueenThread;
import com.lineage.data.npc.Npc_clan;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.CheckFightTimeController;
import com.lineage.server.Controller.StrBonusManager;
import com.lineage.server.Manly.L1WenYang;
import com.lineage.server.Manly.WenYangTable;
import com.lineage.server.datatables.*;
import com.lineage.server.datatables.lock.*;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.*;
import com.lineage.server.model.classes.L1ClassFeature;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.*;
import com.lineage.server.serverpackets.ability.*;
import com.lineage.server.templates.*;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.timecontroller.pc.MapTimerThread;
import com.lineage.server.timecontroller.server.ServerUseMapTimer;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.utils.SublimationRestoreUtil;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import com.lineage.server.world.WorldSummons;
import com.lineage.server.world.WorldWar;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import william.L1WilliamHonor;
import william.ReincarnationSkill;
import william.login_Artiface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class C_LoginToServer extends ClientBasePacket {
    private static final Log _log = LogFactory.getLog(C_LoginToServer.class);

    /**
     * 物品資料
     *
     */
    public static void items(L1PcInstance pc) {
        try {
            pc.getProficiency().loadProficiency();//TODO 玩家武器熟練度
            CharacterTable.restoreInventory(pc);
            /* [原碼] 潘朵拉抽抽樂 */
            pc.sendPackets(new S_Luckylottery(pc.getPandoraInventory().getItems()));
            List<L1ItemInstance> items = pc.getInventory().getItems();
            if (!items.isEmpty()) {
                pc.sendPackets(new S_InvList(items));
                /*for (final L1ItemInstance item : items) {
					if (item.getItem().getType2() == 0) {
						continue;
					}
				}*/
                pc.getInventory().equippedLoad();
                pc.getInventory().viewItem();
            }
            // 登入時恢復冷卻，並顯示對應技能 ICON 倒數
            try {
                if (pc.get_other() != null) {
                    final long nowMs0 = System.currentTimeMillis();
                    int cd1s0 = pc.get_other().get_silian_cd1_until_s();
                    int cd2s0 = pc.get_other().get_silian_cd2_until_s();
                    int cd3s0 = pc.get_other().get_silian_cd3_until_s();
                    long cd1ms0 = ((long) cd1s0) * 1000L;
                    long cd2ms0 = ((long) cd2s0) * 1000L;
                    long cd3ms0 = ((long) cd3s0) * 1000L;
                    if (cd1ms0 > nowMs0) pc.setSilianCooldown1Until(cd1ms0); else pc.setSilianCooldown1Until(0L);
                    if (cd2ms0 > nowMs0) pc.setSilianCooldown2Until(cd2ms0); else pc.setSilianCooldown2Until(0L);
                    if (cd3ms0 > nowMs0) pc.setSilianCooldown3Until(cd3ms0); else pc.setSilianCooldown3Until(0L);

                    // 顯示冷卻 ICON（各技能各自顯示）
                    if (cd1ms0 > nowMs0) {
                        int left = (int) ((cd1ms0 - nowMs0) / 1000L);
                        pc.sendPackets(new S_InventoryIcon(9700, true, 2783, left));
                    }
                    if (cd2ms0 > nowMs0) {
                        int left = (int) ((cd2ms0 - nowMs0) / 1000L);
                        pc.sendPackets(new S_InventoryIcon(9708, true, 2783, left));
                    }
                    if (cd3ms0 > nowMs0) {
                        int left = (int) ((cd3ms0 - nowMs0) / 1000L);
                        pc.sendPackets(new S_InventoryIcon(9718, true, 2783, left));
                    }
                }
            } catch (Throwable ignore) {}
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    // 檢查身上是否有守護者之魂
    public static void checkforProtector(L1PcInstance pc) {
        for (L1ItemInstance item : pc.getInventory().getItems()) {
            if (item.getItemId() == ProtectorSet.ITEM_ID) {// 身上有守護者靈魂
                if (!pc.isProtector()) {// 沒有守護者效果
                    pc.setProtector(true);
                    break;
                }
            }
        }
    }

    // 檢查身上是否有戰神之魂
    public static void checkforMars(L1PcInstance pc) {
        for (L1ItemInstance item : pc.getInventory().getItems()) {
            if (item.getItemId() == 56152) {// 身上有戰神之魂
                if (!pc.isMars()) {// 沒有戰神之魂效果
                    pc.setMars(true);
                    break;
                }
            }
        }
    }

    // 檢查身上的妲蒂斯魔石持有狀態
    public static void checkforDADISStone(L1PcInstance pc) {
        for (L1ItemInstance item : pc.getInventory().getItems()) {
            if (item.getItemId() == 56147) {// 身上有真 妲蒂斯魔石
                // 身上沒有真妲蒂斯魔石效果
                if (!pc.isEffectDADIS()) {
                    pc.setDADIS(true);
                    break;
                }
            } else if (item.getItemId() == 56148) {// 身上有妲蒂斯魔石
                // 身上沒有妲蒂斯魔石效果也沒有真妲蒂斯魔石效果
                if (!pc.isEffectGS() && !pc.isEffectDADIS()) {
                    pc.setGS(true);
                    break;
                }
            }
        }
    }


    /**
     * 身上持有道具給予能力系統
     *
     */
    public static void CheckItemPower(L1PcInstance pc) {
        for (L1ItemInstance item : pc.getInventory().getItems()) {
            if (CheckItemPowerTable.get().checkItem(item.getItemId())) {
                CheckItemPowerTable.get().givepower(pc, item.getItemId());
            }
        }
    }

    /**
     * 刪除任務道具
     */
    public static void deleteIceItem(L1PcInstance _pc) {
        if (_pc != null && _pc.getLevel() >= 1) {
            L1ItemInstance[] item = _pc.getInventory().findItemsId(5010);
            if (item != null) {
                for (L1ItemInstance l1ItemInstance : item) {
                    _pc.getInventory().removeItem(l1ItemInstance);
                }
            }
        }
    }

    /**
     * 刪除屍魂副本道具
     *
     */
    private static void deleteSoulTowerItem(L1PcInstance _pc) {
        //刪除副本道具
        // 640319=下層雷擊爆彈
        // 640320=下層旋風爆彈
        // 640321=下層戰鬥強化卷軸
        // 640322=下層防禦強化卷軸
        // 640323=下層治癒藥水
        // 640324=下層強力治癒藥水
        // 640325=下層魔力藥水
        SoulTowerThread.removeSoulTowerPoint(_pc);
    }

    private static void addHolySetStatus(L1PcInstance pc) {
        try {
            for (Integer i : holySetTable.get().getHolyIds()) {
                //for (int i = 0; i <= holySetTable.get().HolySize(); i++) {//檢查收藏系統DB資料
                final holyPolySet holyPolySet = holySetTable.get().getHoly(i);
                if (holyPolySet != null) {
                    if (pc.getQuest().get_step(holyPolySet.getQuestId()) != 0) {
                        pc.addStr(holyPolySet.getAddStr());
                        pc.addDex(holyPolySet.getAddDex());
                        pc.addCon(holyPolySet.getAddCon());
                        pc.addInt(holyPolySet.getAddInt());
                        pc.addWis(holyPolySet.getAddWis());
                        pc.addCha(holyPolySet.getAddCha());
                        pc.addAc(holyPolySet.getAddAc());
                        pc.addMaxHp(holyPolySet.getAddHp());
                        pc.addMaxMp(holyPolySet.getAddMp());
                        pc.addHpr(holyPolySet.getAddHpr());
                        pc.addMpr(holyPolySet.getAddMpr());
                        pc.addDmgup(holyPolySet.getAddDmg());
                        pc.addBowDmgup(holyPolySet.getAddBowDmg());
                        pc.addHitup(holyPolySet.getAddHit());
                        pc.addBowHitup(holyPolySet.getAddBowHit());
                        pc.addDamageReductionByArmor(holyPolySet.getAddDmgR());
                        pc.addMagicDmgReduction(holyPolySet.getAddMagicDmgR());
                        pc.addSp(holyPolySet.getAddSp());
                        pc.addMagicDmgUp(holyPolySet.getAddMagicHit());
                        pc.addMr(holyPolySet.getAddMr());
                        pc.addFire(holyPolySet.getAddFire());
                        pc.addWater(holyPolySet.getAddWater());
                        pc.addWind(holyPolySet.getAddWind());
                        pc.addEarth(holyPolySet.getAddEarth());
                        //						pc.sendPackets(new S_SystemMessage(cards.getMsg1()));
                        pc.sendPackets(new S_OwnCharStatus(pc));
                        TimeUnit.MILLISECONDS.sleep(5);
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addHolysStatus(L1PcInstance pc) {
        //聖物系統
        try {
            for (Integer i : holyTable.get().getHolyIndex()) {
                //for (int i = 0; i <= holyTable.get().holySize(); i++) {
                final holy holy = holyTable.get().getHoly(i);
                if (holy != null) {
                    if (pc.getQuest().get_step(holy.getQuestId()) != 0) {
                        pc.addStr(holy.getAddStr());
                        pc.addDex(holy.getAddDex());
                        pc.addCon(holy.getAddCon());
                        pc.addInt(holy.getAddInt());
                        pc.addWis(holy.getAddWis());
                        pc.addCha(holy.getAddCha());
                        pc.addAc(holy.getAddAc());
                        pc.addMaxHp(holy.getAddHp());
                        pc.addMaxMp(holy.getAddMp());
                        pc.addHpr(holy.getAddHpr());
                        pc.addMpr(holy.getAddMpr());
                        pc.addDmgup(holy.getAddDmg());
                        pc.addBowDmgup(holy.getAddBowDmg());
                        pc.addHitup(holy.getAddHit());
                        pc.addBowHitup(holy.getAddBowHit());
                        pc.addDamageReductionByArmor(holy.getAddDmgR());
                        pc.addMagicDmgReduction(holy.getAddMagicDmgR());
                        pc.addSp(holy.getAddSp());
                        pc.addMagicDmgUp(holy.getAddMagicHit());
                        pc.addMr(holy.getAddMr());
                        pc.addFire(holy.getAddFire());
                        pc.addWater(holy.getAddWater());
                        pc.addWind(holy.getAddWind());
                        pc.addEarth(holy.getAddEarth());
                        pc.add_dodge(holy.getShanbi());
                        pc.set_evasion(holy.getHuibi());
                        pc.add_up_hp_potion(holy.getYaoshui());
                        pc.addWeightReduction(holy.getFuzhong());
                        pc.setExpPoint(holy.getExp());
                        pc.addRegistStun(holy.getHunmi());
                        pc.addRegistSustain(holy.getZhicheng());
                        pc.addRegistStone(holy.getShihua());
                        pc.add_regist_freeze(holy.getHanbing());
                        pc.addRegistBlind(holy.getAnhei());
                        pc.addRegistSleep(holy.getShuimian());
                        //						pc.sendPackets(new S_SystemMessage(card.getMsg1()));
                        pc.sendPackets(new S_OwnCharStatus(pc));
                        TimeUnit.MILLISECONDS.sleep(5);
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addDollSetStatus(L1PcInstance pc) {
        try {
            for (Integer i : dollSetTable.get().getDollIds()) {
                //for (int i = 0; i <= dollSetTable.get().CardDollSize(); i++) {//檢查收藏系統DB資料
                final dollPolySet dollPolySet = dollSetTable.get().getDoll(i);
                if (dollPolySet != null) {
                    if (dollQuestTable.get().IsQuest(pc, dollPolySet.getQuestId())) {
                        pc.addStr(dollPolySet.getAddStr());
                        pc.addDex(dollPolySet.getAddDex());
                        pc.addCon(dollPolySet.getAddCon());
                        pc.addInt(dollPolySet.getAddInt());
                        pc.addWis(dollPolySet.getAddWis());
                        pc.addCha(dollPolySet.getAddCha());
                        pc.addAc(dollPolySet.getAddAc());
                        pc.addMaxHp(dollPolySet.getAddHp());
                        pc.addMaxMp(dollPolySet.getAddMp());
                        pc.addHpr(dollPolySet.getAddHpr());
                        pc.addMpr(dollPolySet.getAddMpr());
                        pc.addDmgup(dollPolySet.getAddDmg());
                        pc.addBowDmgup(dollPolySet.getAddBowDmg());
                        pc.addHitup(dollPolySet.getAddHit());
                        pc.addBowHitup(dollPolySet.getAddBowHit());
                        pc.addDamageReductionByArmor(dollPolySet.getAddDmgR());
                        pc.addMagicDmgReduction(dollPolySet.getAddMagicDmgR());
                        pc.addSp(dollPolySet.getAddSp());
                        pc.addMagicDmgUp(dollPolySet.getAddMagicHit());
                        pc.addMr(dollPolySet.getAddMr());
                        pc.addFire(dollPolySet.getAddFire());
                        pc.addWater(dollPolySet.getAddWater());
                        pc.addWind(dollPolySet.getAddWind());
                        pc.addEarth(dollPolySet.getAddEarth());
                        //						pc.sendPackets(new S_SystemMessage(cards.getMsg1()));
                        pc.sendPackets(new S_OwnCharStatus(pc));
                        TimeUnit.MILLISECONDS.sleep(5);
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addDollsStatus(L1PcInstance pc) {
        //娃娃系統
        try {
            for (Integer i : dollTable.get().getDollsIndex()) {
                //for (int i = 0; i <= dollTable.get().dollSize(); i++) {
                final doll doll = dollTable.get().getDoll(i);
                if (doll != null) {
                    if (dollQuestTable.get().IsQuest(pc, doll.getQuestId())) {
                        pc.addStr(doll.getAddStr());
                        pc.addDex(doll.getAddDex());
                        pc.addCon(doll.getAddCon());
                        pc.addInt(doll.getAddInt());
                        pc.addWis(doll.getAddWis());
                        pc.addCha(doll.getAddCha());
                        pc.addAc(doll.getAddAc());
                        pc.addMaxHp(doll.getAddHp());
                        pc.addMaxMp(doll.getAddMp());
                        pc.addHpr(doll.getAddHpr());
                        pc.addMpr(doll.getAddMpr());
                        pc.addDmgup(doll.getAddDmg());
                        pc.addBowDmgup(doll.getAddBowDmg());
                        pc.addHitup(doll.getAddHit());
                        pc.addBowHitup(doll.getAddBowHit());
                        pc.addDamageReductionByArmor(doll.getAddDmgR());
                        pc.addMagicDmgReduction(doll.getAddMagicDmgR());
                        pc.addSp(doll.getAddSp());
                        pc.addMagicDmgUp(doll.getAddMagicHit());
                        pc.addMr(doll.getAddMr());
                        pc.addFire(doll.getAddFire());
                        pc.addWater(doll.getAddWater());
                        pc.addWind(doll.getAddWind());
                        pc.addEarth(doll.getAddEarth());
                        pc.add_dodge(doll.getShanbi());
                        pc.set_evasion(doll.getHuibi());
                        pc.add_up_hp_potion(doll.getYaoshui());
                        pc.addWeightReduction(doll.getFuzhong());
                        pc.setExpPoint(doll.getExp());
                        pc.addRegistStun(doll.getHunmi());
                        pc.addRegistSustain(doll.getZhicheng());
                        pc.addRegistStone(doll.getShihua());
                        pc.add_regist_freeze(doll.getHanbing());
                        pc.addRegistBlind(doll.getAnhei());
                        pc.addRegistSleep(doll.getShuimian());
                        //						pc.sendPackets(new S_SystemMessage(card.getMsg1()));
                        pc.sendPackets(new S_OwnCharStatus(pc));
                        TimeUnit.MILLISECONDS.sleep(5);
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addCollectSetStatus(L1PcInstance pc) {
        try {
            for (Integer i : collectSetTable.get().getCollectIds()) {
                //for (int i = 0; i <= collectSetTable.get().getCollectSize(); i++) {//檢查收藏系統DB資料
                final collectPolySet cards = collectSetTable.get().getCard(i);
                if (cards != null) {
                    if (pc.getQuest().get_step(cards.getQuestId()) != 0) {
                        pc.addStr(cards.getAddStr());
                        pc.addDex(cards.getAddDex());
                        pc.addCon(cards.getAddCon());
                        pc.addInt(cards.getAddInt());
                        pc.addWis(cards.getAddWis());
                        pc.addCha(cards.getAddCha());
                        pc.addAc(cards.getAddAc());
                        pc.addMaxHp(cards.getAddHp());
                        pc.addMaxMp(cards.getAddMp());
                        pc.addHpr(cards.getAddHpr());
                        pc.addMpr(cards.getAddMpr());
                        pc.addDmgup(cards.getAddDmg());
                        pc.addBowDmgup(cards.getAddBowDmg());
                        pc.addHitup(cards.getAddHit());
                        pc.addBowHitup(cards.getAddBowHit());
                        pc.addDamageReductionByArmor(cards.getAddDmgR());
                        pc.addMagicDmgReduction(cards.getAddMagicDmgR());
                        pc.addSp(cards.getAddSp());
                        pc.addMagicDmgUp(cards.getAddMagicHit());
                        pc.addMr(cards.getAddMr());
                        pc.addFire(cards.getAddFire());
                        pc.addWater(cards.getAddWater());
                        pc.addWind(cards.getAddWind());
                        pc.addEarth(cards.getAddEarth());
                        // pc.sendPackets(new S_SystemMessage(cards.getMsg1()));
                        pc.sendPackets(new S_OwnCharStatus(pc));
                        TimeUnit.MILLISECONDS.sleep(5);
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addCollectsStatus(L1PcInstance pc) {
        //收藏系統
        try {
            for (Integer i : collectTable.get().getCollectIds()) {
                //for (int i = 0; i <= collectTable.get().CollectSize(); i++) {
                final collect card = collectTable.get().getCollect(i);
                if (card != null) {
                    if (pc.getQuest().get_step(card.getQuestId()) != 0) {
                        pc.addStr(card.getAddStr());
                        pc.addDex(card.getAddDex());
                        pc.addCon(card.getAddCon());
                        pc.addInt(card.getAddInt());
                        pc.addWis(card.getAddWis());
                        pc.addCha(card.getAddCha());
                        pc.addAc(card.getAddAc());
                        pc.addMaxHp(card.getAddHp());
                        pc.addMaxMp(card.getAddMp());
                        pc.addHpr(card.getAddHpr());
                        pc.addMpr(card.getAddMpr());
                        pc.addDmgup(card.getAddDmg());
                        pc.addBowDmgup(card.getAddBowDmg());
                        pc.addHitup(card.getAddHit());
                        pc.addBowHitup(card.getAddBowHit());
                        pc.addDamageReductionByArmor(card.getAddDmgR());
                        pc.addMagicDmgReduction(card.getAddMagicDmgR());
                        pc.addSp(card.getAddSp());
                        pc.addMagicDmgUp(card.getAddMagicHit());
                        pc.addMr(card.getAddMr());
                        pc.addFire(card.getAddFire());
                        pc.addWater(card.getAddWater());
                        pc.addWind(card.getAddWind());
                        pc.addEarth(card.getAddEarth());
                        pc.add_dodge(card.getShanbi());
                        pc.set_evasion(card.getHuibi());
                        pc.add_up_hp_potion(card.getYaoshui());
                        pc.addWeightReduction(card.getFuzhong());
                        pc.setExpPoint(card.getExp());
                        pc.addRegistStun(card.getHunmi());
                        pc.addRegistSustain(card.getZhicheng());
                        pc.addRegistStone(card.getShihua());
                        pc.add_regist_freeze(card.getHanbing());
                        pc.addRegistBlind(card.getAnhei());
                        pc.addRegistSleep(card.getShuimian());
                        // pc.sendPackets(new S_SystemMessage(card.getMsg1()));
                        pc.sendPackets(new S_OwnCharStatus(pc));
                        TimeUnit.MILLISECONDS.sleep(5);
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addCardSetStatus(L1PcInstance pc) {
        try {
            for (Integer i : CardSetTable.get().getCardIds()) {
                //for (int i = 0; i <= CardSetTable.get().CardCardSize(); i++) {//檢查變身組合DB資料
                final CardPolySet cards = CardSetTable.get().getCard(i);
                if (cards != null) {
                    if (CardQuestTable.get().IsQuest(pc, cards.getQuestId())) {
                        pc.addStr(cards.getAddStr());
                        pc.addDex(cards.getAddDex());
                        pc.addCon(cards.getAddCon());
                        pc.addInt(cards.getAddInt());
                        pc.addWis(cards.getAddWis());
                        pc.addCha(cards.getAddCha());
                        pc.addAc(cards.getAddAc());
                        pc.addMaxHp(cards.getAddHp());
                        pc.addMaxMp(cards.getAddMp());
                        pc.addHpr(cards.getAddHpr());
                        pc.addMpr(cards.getAddMpr());
                        pc.addDmgup(cards.getAddDmg());
                        pc.addBowDmgup(cards.getAddBowDmg());
                        pc.addHitup(cards.getAddHit());
                        pc.addBowHitup(cards.getAddBowHit());
                        pc.addDamageReductionByArmor(cards.getAddDmgR());
                        pc.addMagicDmgReduction(cards.getAddMagicDmgR());
                        pc.addSp(cards.getAddSp());
                        pc.addMagicDmgUp(cards.getAddMagicHit());
                        pc.addMr(cards.getAddMr());
                        pc.addFire(cards.getAddFire());
                        pc.addWater(cards.getAddWater());
                        pc.addWind(cards.getAddWind());
                        pc.addEarth(cards.getAddEarth());
                        // pc.sendPackets(new S_SystemMessage(cards.getMsg1()));
                        pc.sendPackets(new S_OwnCharStatus(pc));
                        TimeUnit.MILLISECONDS.sleep(5);
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addCardsStatus(L1PcInstance pc) {
        //卡片鑲嵌系統
        try {
            for (Integer i : ACardTable.get().getCardIndex()) {
                //for (int i = 0; i <= ACardTable.get().ACardSize(); i++) {
                final ACard card = ACardTable.get().getCard(i);
                if (card != null) {
                    if (CardQuestTable.get().IsQuest(pc, card.getQuestId())) {
                        pc.addStr(card.getAddStr());
                        pc.addDex(card.getAddDex());
                        pc.addCon(card.getAddCon());
                        pc.addInt(card.getAddInt());
                        pc.addWis(card.getAddWis());
                        pc.addCha(card.getAddCha());
                        pc.addAc(card.getAddAc());
                        pc.addMaxHp(card.getAddHp());
                        pc.addMaxMp(card.getAddMp());
                        pc.addHpr(card.getAddHpr());
                        pc.addMpr(card.getAddMpr());
                        pc.addDmgup(card.getAddDmg());
                        pc.addBowDmgup(card.getAddBowDmg());
                        pc.addHitup(card.getAddHit());
                        pc.addBowHitup(card.getAddBowHit());
                        pc.addDamageReductionByArmor(card.getAddDmgR());
                        pc.addMagicDmgReduction(card.getAddMagicDmgR());
                        pc.addSp(card.getAddSp());
                        pc.addMagicDmgUp(card.getAddMagicHit());
                        pc.addMr(card.getAddMr());
                        pc.addFire(card.getAddFire());
                        pc.addWater(card.getAddWater());
                        pc.addWind(card.getAddWind());
                        pc.addEarth(card.getAddEarth());
                        pc.add_dodge(card.getShanbi());
                        pc.set_evasion(card.getHuibi());
                        pc.add_up_hp_potion(card.getYaoshui());
                        pc.addWeightReduction(card.getFuzhong());
                        pc.setExpPoint(card.getExp());
                        pc.addRegistStun(card.getHunmi());
                        pc.addRegistSustain(card.getZhicheng());
                        pc.addRegistStone(card.getShihua());
                        pc.add_regist_freeze(card.getHanbing());
                        pc.addRegistBlind(card.getAnhei());
                        pc.addRegistSleep(card.getShuimian());
                        // pc.sendPackets(new S_SystemMessage(card.getMsg1()));
                        pc.sendPackets(new S_OwnCharStatus(pc));
                        TimeUnit.MILLISECONDS.sleep(5);
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addWyType6(L1PcInstance pc) {
        if (pc.getWyType6() == 6 && pc.getWyLevel6() > 0) {
            L1WenYang wenYang = WenYangTable.getInstance().getTemplate(pc.getWyType6(), pc.getWyLevel6());
            if (wenYang != null) {
                int liliang = wenYang.getliliang();
                if (liliang != 0) {
                    pc.addStr(liliang);
                }
                int minjie = wenYang.getminjie();
                if (minjie != 0) {
                    pc.addDex(minjie);
                }
                int zhili = wenYang.getzhili();
                if (zhili != 0) {
                    pc.addInt(zhili);
                }
                int jingshen = wenYang.getjingshen();
                if (jingshen != 0) {
                    pc.addWis(jingshen);
                }
                int tizhi = wenYang.gettizhi();
                if (tizhi != 0) {
                    pc.addCon(tizhi);
                }
                int meili = wenYang.getmeili();
                if (meili != 0) {
                    pc.addCha(meili);
                }
                int xue = wenYang.getxue();
                if (xue != 0) {
                    pc.addMaxHp(xue);
                }
                int mo = wenYang.getmo();
                if (mo != 0) {
                    pc.addMaxMp(mo);
                }
                int huixue = wenYang.gethuixue();
                if (huixue != 0) {
                    pc.addHpr(huixue);
                }
                int huimo = wenYang.gethuimo();
                if (huimo != 0) {
                    pc.addMpr(huimo);
                }
                int ewai = wenYang.getewai();
                if (ewai != 0) {
                    pc.addDmgup(ewai);
                    pc.addBowDmgup(ewai);
                }
                int chenggong = wenYang.getchenggong();
                if (chenggong != 0) {
                    pc.addHitup(chenggong);
                    pc.addBowHit(chenggong);
                }
                int mogong = wenYang.getmogong();
                if (mogong != 0) {
                    pc.addSp(mogong);
                }
                int mofang = wenYang.getmofang();
                if (mofang != 0) {
                    pc.addMr(mofang);
                }
                int feng = wenYang.getfeng();
                if (feng != 0) {
                    pc.addWind(feng);
                }
                int shui = wenYang.getshui();
                if (shui != 0) {
                    pc.addWater(shui);
                }
                int tu = wenYang.gettu();
                if (tu != 0) {
                    pc.addEarth(tu);
                }
                int huo = wenYang.gethuo();
                if (huo != 0) {
                    pc.addFire(huo);
                }
                int jianmian = wenYang.getjianmian();
                if (jianmian != 0) {
                    pc.addDamageReductionByArmor(jianmian);
                }
                int jingyan = wenYang.getjingyan();
                if (jingyan != 0) {
                    pc.setExpRateToPc(jingyan);
                }
                pc.sendPackets(new S_SystemMessage("\\aB獲得" + wenYang.getNot() + "紋樣" + pc.getWyLevel6() + "階"));
                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                pc.sendPackets(new S_OwnCharStatus(pc));
                pc.sendPackets(new S_OwnCharStatus2(pc));
                pc.sendPackets(new S_SPMR(pc));
                pc.sendPackets(new S_InventoryIcon(wenYang.get_buff_iconid(), true, wenYang.get_buff_stringid(), -1)); // 重登不會有訊息
            }
        }
    }

    private static void addWyType5(L1PcInstance pc) {
        if (pc.getWyType5() == 5 && pc.getWyLevel5() > 0) {
            L1WenYang wenYang = WenYangTable.getInstance().getTemplate(pc.getWyType5(), pc.getWyLevel5());
            if (wenYang != null) {
                int liliang = wenYang.getliliang();
                if (liliang != 0) {
                    pc.addStr(liliang);
                }
                int minjie = wenYang.getminjie();
                if (minjie != 0) {
                    pc.addDex(minjie);
                }
                int zhili = wenYang.getzhili();
                if (zhili != 0) {
                    pc.addInt(zhili);
                }
                int jingshen = wenYang.getjingshen();
                if (jingshen != 0) {
                    pc.addWis(jingshen);
                }
                int tizhi = wenYang.gettizhi();
                if (tizhi != 0) {
                    pc.addCon(tizhi);
                }
                int meili = wenYang.getmeili();
                if (meili != 0) {
                    pc.addCha(meili);
                }
                int xue = wenYang.getxue();
                if (xue != 0) {
                    pc.addMaxHp(xue);
                }
                int mo = wenYang.getmo();
                if (mo != 0) {
                    pc.addMaxMp(mo);
                }
                int huixue = wenYang.gethuixue();
                if (huixue != 0) {
                    pc.addHpr(huixue);
                }
                int huimo = wenYang.gethuimo();
                if (huimo != 0) {
                    pc.addMpr(huimo);
                }
                int ewai = wenYang.getewai();
                if (ewai != 0) {
                    pc.addDmgup(ewai);
                    pc.addBowDmgup(ewai);
                }
                int chenggong = wenYang.getchenggong();
                if (chenggong != 0) {
                    pc.addHitup(chenggong);
                    pc.addBowHit(chenggong);
                }
                int mogong = wenYang.getmogong();
                if (mogong != 0) {
                    pc.addSp(mogong);
                }
                int mofang = wenYang.getmofang();
                if (mofang != 0) {
                    pc.addMr(mofang);
                }
                int feng = wenYang.getfeng();
                if (feng != 0) {
                    pc.addWind(feng);
                }
                int shui = wenYang.getshui();
                if (shui != 0) {
                    pc.addWater(shui);
                }
                int tu = wenYang.gettu();
                if (tu != 0) {
                    pc.addEarth(tu);
                }
                int huo = wenYang.gethuo();
                if (huo != 0) {
                    pc.addFire(huo);
                }
                int jianmian = wenYang.getjianmian();
                if (jianmian != 0) {
                    pc.addDamageReductionByArmor(jianmian);
                }
                int jingyan = wenYang.getjingyan();
                if (jingyan != 0) {
                    pc.setExpRateToPc(jingyan);
                }
                pc.sendPackets(new S_SystemMessage("\\aB獲得" + wenYang.getNot() + "紋樣" + pc.getWyLevel5() + "階"));
                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                pc.sendPackets(new S_OwnCharStatus(pc));
                pc.sendPackets(new S_OwnCharStatus2(pc));
                pc.sendPackets(new S_SPMR(pc));
                pc.sendPackets(new S_InventoryIcon(wenYang.get_buff_iconid(), true, wenYang.get_buff_stringid(), -1)); // 重登不會有訊息
            }
        }
    }

    private static void addWyType4(L1PcInstance pc) {
        if (pc.getWyType4() == 4 && pc.getWyLevel4() > 0) {
            L1WenYang wenYang = WenYangTable.getInstance().getTemplate(pc.getWyType4(), pc.getWyLevel4());
            if (wenYang != null) {
                int liliang = wenYang.getliliang();
                if (liliang != 0) {
                    pc.addStr(liliang);
                }
                int minjie = wenYang.getminjie();
                if (minjie != 0) {
                    pc.addDex(minjie);
                }
                int zhili = wenYang.getzhili();
                if (zhili != 0) {
                    pc.addInt(zhili);
                }
                int jingshen = wenYang.getjingshen();
                if (jingshen != 0) {
                    pc.addWis(jingshen);
                }
                int tizhi = wenYang.gettizhi();
                if (tizhi != 0) {
                    pc.addCon(tizhi);
                }
                int meili = wenYang.getmeili();
                if (meili != 0) {
                    pc.addCha(meili);
                }
                int xue = wenYang.getxue();
                if (xue != 0) {
                    pc.addMaxHp(xue);
                }
                int mo = wenYang.getmo();
                if (mo != 0) {
                    pc.addMaxMp(mo);
                }
                int huixue = wenYang.gethuixue();
                if (huixue != 0) {
                    pc.addHpr(huixue);
                }
                int huimo = wenYang.gethuimo();
                if (huimo != 0) {
                    pc.addMpr(huimo);
                }
                int ewai = wenYang.getewai();
                if (ewai != 0) {
                    pc.addDmgup(ewai);
                    pc.addBowDmgup(ewai);
                }
                int chenggong = wenYang.getchenggong();
                if (chenggong != 0) {
                    pc.addHitup(chenggong);
                    pc.addBowHit(chenggong);
                }
                int mogong = wenYang.getmogong();
                if (mogong != 0) {
                    pc.addSp(mogong);
                }
                int mofang = wenYang.getmofang();
                if (mofang != 0) {
                    pc.addMr(mofang);
                }
                int feng = wenYang.getfeng();
                if (feng != 0) {
                    pc.addWind(feng);
                }
                int shui = wenYang.getshui();
                if (shui != 0) {
                    pc.addWater(shui);
                }
                int tu = wenYang.gettu();
                if (tu != 0) {
                    pc.addEarth(tu);
                }
                int huo = wenYang.gethuo();
                if (huo != 0) {
                    pc.addFire(huo);
                }
                int jianmian = wenYang.getjianmian();
                if (jianmian != 0) {
                    pc.addDamageReductionByArmor(jianmian);
                }
                int jingyan = wenYang.getjingyan();
                if (jingyan != 0) {
                    pc.setExpRateToPc(jingyan);
                }
                pc.sendPackets(new S_SystemMessage("\\aB獲得" + wenYang.getNot() + "紋樣" + pc.getWyLevel4() + "階"));
                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                pc.sendPackets(new S_OwnCharStatus(pc));
                pc.sendPackets(new S_OwnCharStatus2(pc));
                pc.sendPackets(new S_SPMR(pc));
                pc.sendPackets(new S_InventoryIcon(wenYang.get_buff_iconid(), true, wenYang.get_buff_stringid(), -1)); // 重登不會有訊息
            }
        }
    }

    private static void addWyType3(L1PcInstance pc) {
        if (pc.getWyType3() == 3 && pc.getWyLevel3() > 0) {
            L1WenYang wenYang = WenYangTable.getInstance().getTemplate(pc.getWyType3(), pc.getWyLevel3());
            if (wenYang != null) {
                int liliang = wenYang.getliliang();
                if (liliang != 0) {
                    pc.addStr(liliang);
                }
                int minjie = wenYang.getminjie();
                if (minjie != 0) {
                    pc.addDex(minjie);
                }
                int zhili = wenYang.getzhili();
                if (zhili != 0) {
                    pc.addInt(zhili);
                }
                int jingshen = wenYang.getjingshen();
                if (jingshen != 0) {
                    pc.addWis(jingshen);
                }
                int tizhi = wenYang.gettizhi();
                if (tizhi != 0) {
                    pc.addCon(tizhi);
                }
                int meili = wenYang.getmeili();
                if (meili != 0) {
                    pc.addCha(meili);
                }
                int xue = wenYang.getxue();
                if (xue != 0) {
                    pc.addMaxHp(xue);
                }
                int mo = wenYang.getmo();
                if (mo != 0) {
                    pc.addMaxMp(mo);
                }
                int huixue = wenYang.gethuixue();
                if (huixue != 0) {
                    pc.addHpr(huixue);
                }
                int huimo = wenYang.gethuimo();
                if (huimo != 0) {
                    pc.addMpr(huimo);
                }
                int ewai = wenYang.getewai();
                if (ewai != 0) {
                    pc.addDmgup(ewai);
                    pc.addBowDmgup(ewai);
                }
                int chenggong = wenYang.getchenggong();
                if (chenggong != 0) {
                    pc.addHitup(chenggong);
                    pc.addBowHit(chenggong);
                }
                int mogong = wenYang.getmogong();
                if (mogong != 0) {
                    pc.addSp(mogong);
                }
                int mofang = wenYang.getmofang();
                if (mofang != 0) {
                    pc.addMr(mofang);
                }
                int feng = wenYang.getfeng();
                if (feng != 0) {
                    pc.addWind(feng);
                }
                int shui = wenYang.getshui();
                if (shui != 0) {
                    pc.addWater(shui);
                }
                int tu = wenYang.gettu();
                if (tu != 0) {
                    pc.addEarth(tu);
                }
                int huo = wenYang.gethuo();
                if (huo != 0) {
                    pc.addFire(huo);
                }
                int jianmian = wenYang.getjianmian();
                if (jianmian != 0) {
                    pc.addDamageReductionByArmor(jianmian);
                }
                int jingyan = wenYang.getjingyan();
                if (jingyan != 0) {
                    pc.setExpRateToPc(jingyan);
                }
                pc.sendPackets(new S_SystemMessage("\\aB獲得" + wenYang.getNot() + "紋樣" + pc.getWyLevel3() + "階"));
                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                pc.sendPackets(new S_OwnCharStatus(pc));
                pc.sendPackets(new S_OwnCharStatus2(pc));
                pc.sendPackets(new S_SPMR(pc));
                pc.sendPackets(new S_InventoryIcon(wenYang.get_buff_iconid(), true, wenYang.get_buff_stringid(), -1)); // 重登不會有訊息
            }
        }
    }

    private static void addWyType2(L1PcInstance pc) {
        if (pc.getWyType2() == 2 && pc.getWyLevel2() > 0) {
            L1WenYang wenYang = WenYangTable.getInstance().getTemplate(pc.getWyType2(), pc.getWyLevel2());
            if (wenYang != null) {
                int liliang = wenYang.getliliang();
                if (liliang != 0) {
                    pc.addStr(liliang);
                }
                int minjie = wenYang.getminjie();
                if (minjie != 0) {
                    pc.addDex(minjie);
                }
                int zhili = wenYang.getzhili();
                if (zhili != 0) {
                    pc.addInt(zhili);
                }
                int jingshen = wenYang.getjingshen();
                if (jingshen != 0) {
                    pc.addWis(jingshen);
                }
                int tizhi = wenYang.gettizhi();
                if (tizhi != 0) {
                    pc.addCon(tizhi);
                }
                int meili = wenYang.getmeili();
                if (meili != 0) {
                    pc.addCha(meili);
                }
                int xue = wenYang.getxue();
                if (xue != 0) {
                    pc.addMaxHp(xue);
                }
                int mo = wenYang.getmo();
                if (mo != 0) {
                    pc.addMaxMp(mo);
                }
                int huixue = wenYang.gethuixue();
                if (huixue != 0) {
                    pc.addHpr(huixue);
                }
                int huimo = wenYang.gethuimo();
                if (huimo != 0) {
                    pc.addMpr(huimo);
                }
                int ewai = wenYang.getewai();
                if (ewai != 0) {
                    pc.addDmgup(ewai);
                    pc.addBowDmgup(ewai);
                }
                int chenggong = wenYang.getchenggong();
                if (chenggong != 0) {
                    pc.addHitup(chenggong);
                    pc.addBowHit(chenggong);
                }
                int mogong = wenYang.getmogong();
                if (mogong != 0) {
                    pc.addSp(mogong);
                }
                int mofang = wenYang.getmofang();
                if (mofang != 0) {
                    pc.addMr(mofang);
                }
                int feng = wenYang.getfeng();
                if (feng != 0) {
                    pc.addWind(feng);
                }
                int shui = wenYang.getshui();
                if (shui != 0) {
                    pc.addWater(shui);
                }
                int tu = wenYang.gettu();
                if (tu != 0) {
                    pc.addEarth(tu);
                }
                int huo = wenYang.gethuo();
                if (huo != 0) {
                    pc.addFire(huo);
                }
                int jianmian = wenYang.getjianmian();
                if (jianmian != 0) {
                    pc.addDamageReductionByArmor(jianmian);
                }
                int jingyan = wenYang.getjingyan();
                if (jingyan != 0) {
                    pc.setExpRateToPc(jingyan);
                }
                pc.sendPackets(new S_SystemMessage("\\aB獲得" + wenYang.getNot() + "紋樣" + pc.getWyLevel2() + "階"));
                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                pc.sendPackets(new S_OwnCharStatus(pc));
                pc.sendPackets(new S_OwnCharStatus2(pc));
                pc.sendPackets(new S_SPMR(pc));
                pc.sendPackets(new S_InventoryIcon(wenYang.get_buff_iconid(), true, wenYang.get_buff_stringid(), -1)); // 重登不會有訊息
            }
        }
    }

    private static void addWyType1(L1PcInstance pc) {
        if (pc.getWyType1() == 1 && pc.getWyLevel1() > 0) {
            L1WenYang wenYang = WenYangTable.getInstance().getTemplate(pc.getWyType1(), pc.getWyLevel1());
            if (wenYang != null) {
                int liliang = wenYang.getliliang();
                if (liliang != 0) {
                    pc.addStr(liliang);
                }
                int minjie = wenYang.getminjie();
                if (minjie != 0) {
                    pc.addDex(minjie);
                }
                int zhili = wenYang.getzhili();
                if (zhili != 0) {
                    pc.addInt(zhili);
                }
                int jingshen = wenYang.getjingshen();
                if (jingshen != 0) {
                    pc.addWis(jingshen);
                }
                int tizhi = wenYang.gettizhi();
                if (tizhi != 0) {
                    pc.addCon(tizhi);
                }
                int meili = wenYang.getmeili();
                if (meili != 0) {
                    pc.addCha(meili);
                }
                int xue = wenYang.getxue();
                if (xue != 0) {
                    pc.addMaxHp(xue);
                }
                int mo = wenYang.getmo();
                if (mo != 0) {
                    pc.addMaxMp(mo);
                }
                int huixue = wenYang.gethuixue();
                if (huixue != 0) {
                    pc.addHpr(huixue);
                }
                int huimo = wenYang.gethuimo();
                if (huimo != 0) {
                    pc.addMpr(huimo);
                }
                int ewai = wenYang.getewai();
                if (ewai != 0) {
                    pc.addDmgup(ewai);
                    pc.addBowDmgup(ewai);
                }
                int chenggong = wenYang.getchenggong();
                if (chenggong != 0) {
                    pc.addHitup(chenggong);
                    pc.addBowHit(chenggong);
                }
                int mogong = wenYang.getmogong();
                if (mogong != 0) {
                    pc.addSp(mogong);
                }
                int mofang = wenYang.getmofang();
                if (mofang != 0) {
                    pc.addMr(mofang);
                }
                int feng = wenYang.getfeng();
                if (feng != 0) {
                    pc.addWind(feng);
                }
                int shui = wenYang.getshui();
                if (shui != 0) {
                    pc.addWater(shui);
                }
                int tu = wenYang.gettu();
                if (tu != 0) {
                    pc.addEarth(tu);
                }
                int huo = wenYang.gethuo();
                if (huo != 0) {
                    pc.addFire(huo);
                }
                int jianmian = wenYang.getjianmian();
                if (jianmian != 0) {
                    pc.addDamageReductionByArmor(jianmian);
                }
                int jingyan = wenYang.getjingyan();
                if (jingyan != 0) {
                    pc.setExpRateToPc(jingyan);
                }
                int shanbi = wenYang.getaddshanbi(); //0115/**閃避*/
                if (shanbi != 0) {
                    pc.add_dodge(shanbi);
                }
                int huibi = wenYang.getHuibi(); //0115/**迴避*/
                if (huibi != 0) {
                    pc.addOriginalEr(huibi);
                }
                int yaoshui = wenYang.getYaoshui(); //0115//**藥水增加*/
                if (yaoshui != 0) {
                    pc.add_up_hp_potion(yaoshui);
                }
                int fuzhong = wenYang.getFuzhong(); //0115/**負重*/
                if (fuzhong != 0) {
                    pc.addWeightReduction(fuzhong);
                }
                int setAc = wenYang.getAc(); //0122/**防禦*/
                if (setAc != 0) {
                    pc.addAc(setAc);
                }
                int dice_dmg = wenYang.gatDiceDmg(); //0122/機率給予爆擊
                int dmg = wenYang.getDmg(); //0122/**// 機率給予爆擊質
                pc.set_dmgAdd(dmg, dice_dmg);
                int pvpdmg = wenYang.getpvpdmg(); //0122/**// 增加PVP傷害
                if (pvpdmg != 0) {
                    pc.setPvpDmg(pvpdmg);
                }
                int pvpdmg_r = wenYang.getpvpdmg_r(); //0122/**// 增加PVP減免
                if (pvpdmg_r != 0) {
                    pc.setPvpDmg_R(pvpdmg_r);
                }
                pc.sendPackets(new S_SystemMessage("\\aB獲得" + wenYang.getNot() + "紋樣" + pc.getWyLevel1() + "階"));
                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                pc.sendPackets(new S_OwnCharStatus(pc));
                pc.sendPackets(new S_OwnCharStatus2(pc));
                pc.sendPackets(new S_OwnCharStatus2(pc));
                pc.sendPackets(new S_SPMR(pc));
                pc.sendPackets(new S_InventoryIcon(wenYang.get_buff_iconid(), true, wenYang.get_buff_stringid(), -1)); // 重登不會有訊息
            }
        }
    }

    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            read(decrypt);
            //int r = 0;
            String loginName = client.getAccountName();
            if (client.getActiveChar() != null) {
                _log.error("帳號重複登入人物: " + loginName + "強制中斷連線");
                client.kick();
                return;
            }
            String charName = readS();
            L1PcInstance pc = L1PcInstance.load(charName);
            if (pc == null || !loginName.equals(pc.getAccountName())) {
                _log.info("無效登入要求: " + charName + " 帳號(" + loginName + ", " + client.getIp() + ")");
                client.kick();
                return;
            }
            try {
                java.sql.Timestamp gaiaNextTime = com.lineage.server.datatables.sql.CharSkillTable.get()
                        .getSkillCooldown(pc.getId(), com.lineage.server.model.skill.L1SkillId.GAIA);
                if (gaiaNextTime != null && gaiaNextTime.getTime() > System.currentTimeMillis()) {
                    // 資料庫記錄的冷卻時間尚未過
                    pc.setSkillReuse(com.lineage.server.model.skill.L1SkillId.GAIA, gaiaNextTime.getTime());
                }
            } catch (Exception e) {
                _log.error("恢復GAIA技能冷卻時發生錯誤", e);
            }
            int currentHpAtLoad = pc.getCurrentHp();
            int currentMpAtLoad = pc.getCurrentMp();
            // 重置錯誤次數
            client.set_error(0);
            pc.clearSkillMastery();// 清除技能資訊
            pc.setOnlineStatus(1);// 設定連線狀態
            CharacterTable.updateOnlineStatus(pc);
            World.get().storeObject(pc);
            pc.setNetConnection(client);// 登記封包接收組
            pc.setPacketOutput(client.out());// 登記封包發送組
            pc.sendPackets(new S_EnterGame(pc));// 宣告進入遊戲
            client.setActiveChar(pc);// 登記玩家資料
            // 登入清理掛鉤：通知清理管理器玩家已登入
            com.lineage.server.model.Instance.L1PcInstanceCleanupIntegration.onPlayerLogin(pc);
            pc.sendPackets(new S_InitialAbilityGrowth(pc));// 初始點數獎勵
            items(pc);// 讀取角色道具
            //昇華系統
            SublimationRestoreUtil.restoreAll(pc);
            bookmarks(pc);// 取得記憶座標資料
            backRestart(pc);// 判斷回村座標資料
            getFocus(pc);// 遊戲焦點及人物狀態更新
            getOther(pc);// 額外紀錄資料
            // 【龍之祝福】登入回灌（避免重啟後 DragonExp 變 0）
            try {
                com.add.Tsai.DragonExp.get().restoreToPcOnLogin(pc);
            } catch (Exception e) {
                _log.error("DragonExp restore error: " + pc.getName(), e);
            }
            pc.sendVisualEffectAtLogin();// 人物中毒、麻痺狀態顯示
            skills(pc);// 取得角色魔法技能資料
            buff(pc);// 人物保留的BUFF資料
            pc.loadSkillLevelsFromDB(); //強化技能
            pc.turnOnOffLight();
            if (pc.getCurrentHp() > 0) {
                pc.setDead(false);
                pc.setStatus(0);
            } else {
                pc.setDead(true);
                pc.setStatus(8);
            }
            pc.sendPackets(new S_PacketBox(32));
            Reward_stats(pc); // 7.6 空身體質額外獎勵
            ReincarnationSkill.getInstance().getPoint(pc); // 轉生天賦
            // 7.6
            // XXX 7.6 ADD
            pc.sendPackets(new S_PacketBoxCharEr(pc));// 角色迴避率更新
            // XXX 7.6 能力基本資訊-力量
            pc.sendPackets(new S_StrDetails(2, L1ClassFeature.calcStrDmg(pc.getStr(), pc.getBaseStr()), L1ClassFeature.calcStrHit(pc.getStr(), pc.getBaseStr()), L1ClassFeature.calcStrDmgCritical(pc.getStr(), pc.getBaseStr()), L1ClassFeature.calcAbilityMaxWeight(pc.getStr(), pc.getCon())));
            // XXX 7.6 重量程度資訊
            pc.sendPackets(new S_WeightStatus(pc.getInventory().getWeight100(), pc.getInventory().getWeight(), (int) pc.getMaxWeight()));
            // XXX 7.6 能力基本資訊-智力
            pc.sendPackets(new S_IntDetails(2, L1ClassFeature.calcIntMagicDmg(pc.getInt(), pc.getBaseInt()), L1ClassFeature.calcIntMagicHit(pc.getInt(), pc.getBaseInt()), L1ClassFeature.calcIntMagicCritical(pc.getInt(), pc.getBaseInt()), L1ClassFeature.calcIntMagicBonus(pc.getType(), pc.getInt()), L1ClassFeature.calcIntMagicConsumeReduction(pc.getInt())));
            // XXX 7.6 能力基本資訊-精神
            pc.sendPackets(new S_WisDetails(2, L1ClassFeature.calcWisMpr(pc.getWis(), pc.getBaseWis()), L1ClassFeature.calcWisPotionMpr(pc.getWis(), pc.getBaseWis()), L1ClassFeature.calcStatMr(pc.getWis()) + L1ClassFeature.newClassFeature(pc.getType()).getClassOriginalMr(), L1ClassFeature.calcBaseWisLevUpMpUp(pc.getType(), pc.getBaseWis())));
            // XXX 7.6 能力基本資訊-敏捷
            pc.sendPackets(new S_DexDetails(2, L1ClassFeature.calcDexDmg(pc.getDex(), pc.getBaseDex()), L1ClassFeature.calcDexHit(pc.getDex(), pc.getBaseDex()), L1ClassFeature.calcDexDmgCritical(pc.getDex(), pc.getBaseDex()), L1ClassFeature.calcDexAc(pc.getDex()), L1ClassFeature.calcDexEr(pc.getDex())));
            // XXX 7.6 能力基本資訊-體質
            pc.sendPackets(new S_ConDetails(2, L1ClassFeature.calcConHpr(pc.getCon(), pc.getBaseCon()), L1ClassFeature.calcConPotionHpr(pc.getCon(), pc.getBaseCon()), L1ClassFeature.calcAbilityMaxWeight(pc.getStr(), pc.getCon()), L1ClassFeature.calcBaseClassLevUpHpUp(pc.getType()) + L1ClassFeature.calcBaseConLevUpExtraHpUp(pc.getType(), pc.getBaseCon())));
            // XXX 7.6 重量程度資訊
            pc.sendPackets(new S_WeightStatus(pc.getInventory().getWeight() * 100 / (int) pc.getMaxWeight(), pc.getInventory().getWeight(), (int) pc.getMaxWeight()));
            // XXX 7.6 純能力詳細資訊 階段:25
            pc.sendPackets(new S_BaseAbilityDetails(25));
            // XXX 7.6 純能力詳細資訊 階段:35
            pc.sendPackets(new S_BaseAbilityDetails(35));
            // XXX 7.6 純能力詳細資訊 階段:45
            pc.sendPackets(new S_BaseAbilityDetails(45));
            // XXX 7.6 純能力資訊
            pc.sendPackets(new S_BaseAbility(pc.getBaseStr(), pc.getBaseInt(), pc.getBaseWis(), pc.getBaseDex(), pc.getBaseCon(), pc.getBaseCha()));
            // XXX 7.6 萬能藥使用數量
            pc.sendPackets(new S_ElixirCount(pc.getElixirStats()));
            pc.sendPackets(new S_PacketBox(189));
            serchSummon(pc);// 殘留寵物資料
            ServerWarExecutor.get().checkCastleWar(pc);// 發佈城戰訊息
            war(pc);// 血盟與盟戰資料
            marriage(pc);// 婚姻資料
            // ClanMatching(pc);// 血盟推薦
            // 給予殷海薩祝福指數的處理
            if (LeavesSet.START) { // 殷海薩的祝福-休息系統
                int logintime = (int) (System.currentTimeMillis() / 60 / 1000);// 目前時間換算為(分)
                int minute = logintime - pc.get_other().get_login_time();// 間隔時間(分)
                if (minute > 0 && minute / LeavesSet.TIME > 0) {
                    final int addexp = minute / LeavesSet.TIME * LeavesSet.EXP;
                    pc.get_other().set_leaves_time_exp(addexp);
                    pc.sendPackets(new S_PacketBoxExp(pc.get_other().get_leaves_time_exp() / LeavesSet.EXP));
                }
            }
            if (currentHpAtLoad > pc.getCurrentHp()) {
                pc.setCurrentHp(currentHpAtLoad);
            }
            if (currentMpAtLoad > pc.getCurrentMp()) {
                pc.setCurrentMp(currentMpAtLoad);
            }
            pc.startHpRegeneration();
            pc.startMpRegeneration();
            pc.startObjectAutoUpdate();// PC 可見物更新處理
            pc.beginExpMonitor(); // l1j-tw連續攻擊
            crown(pc);// 送出王冠資料
            pc.save();// 資料回存
            if (pc.getHellTime() > 0) {// 返回地獄的判斷
                pc.beginHell(false);
            }
            //pc.sendPackets(new S_CharResetInfo(pc));// 送出人物屬性資料
            S_CharReset statusinfo = new S_CharReset(pc, 0x04);// 初始能力加成顯示 // 7.6
            pc.sendPackets(statusinfo);
            statsReward(pc);// 點數獎勵 7.6
            pc.load_src();// 經驗值、正義值、好友度
            pc.getQuest().load();// 任務進度
            pc.sendPackets(new S_EquipmentSlot(1, 16));
            pc.showWindows();

//            if (QuestMobSet.START) {
//                ServerQuestMobTable.get().getQuestMobNote(pc);
//            }
            if (pc.get_food() >= 225) {
                Calendar cal = Calendar.getInstance();
                long h_time = cal.getTimeInMillis() / 1000L;
                pc.set_h_time(h_time);
            }
            if (pc.getLevel() <= ConfigOther.ENCOUNTER_LV) {// 新手保護
                pc.sendPackets(new S_PacketBoxProtection(6, 1));
            }
            pc.lawfulUpdate();// 戰鬥特化狀態圖示更新
            if (EffectAISet.START) { // 特效驗證系統
                if (EffectAISet.AI_TIME_RANDOM != 0 && pc.getAITimer() == 0) {
                    pc.setAITimer(ThreadLocalRandom.current().nextInt(EffectAISet.AI_TIME_RANDOM) + EffectAISet.AI_TIME);
                }
            }
            if (ConfigOtherSet2.WHO_ONLINE_MSG_ON) {
                Collection<L1PcInstance> allplayer = World.get().getAllPlayers();
                for (L1PcInstance object : allplayer) {
                    if (object != null) {
                        if (object.getAccessLevel() == 100 || object.getAccessLevel() == 200) {
                            /*
                             * String msg = ""; if (pc.isCrown()) { msg = "王子";
                             * } else if (pc.isKnight()) { msg = "騎士"; } else if
                             * (pc.isElf()) { msg = "妖精"; } else if
                             * (pc.isWizard()) { msg = "法師"; } else if
                             * (pc.isDarkelf()) { msg = "黑妖"; } else if
                             * (pc.isDragonKnight()) { msg = "龍騎士"; } else if
                             * (pc.isIllusionist()) { msg = "幻術士"; }
                             */
                            object.sendPackets(new S_GmMessage(pc));
                            /*
                             * GM.sendPackets(new S_SystemMessage("(玩家" +
                             * pc.getName() + ")(帳號" + client.getAccountName() +
                             * ")" + "(IP" + client.getIp() + ")" + "(職業" + msg
                             * + ")(上線)"));
                             */
                        }
                    }
                }
            }
            // GM 上線後自動隱身
            if (ConfigOtherSet2.ALT_GM_HIDE) {
                if (pc.isGm() || pc.isMonitor()) {
                    pc.setGmInvis(true);
                    pc.sendPackets(new S_Invis(pc.getId(), 1));
                    pc.broadcastPacketAll(new S_RemoveObject(pc));
                    pc.sendPackets(new S_SystemMessage("\\aG啟用線上GM隱身模式。"));
                }
            }

            if (CardSet.START) {
                CardSet.load_card_mode(pc);
            }
            if (CardSet.START) {
                CardSet.load_card_mode(pc);
            }
            T_OnlineGiftTable.get().check(pc);
            //pc.setVipStatus();
            if (CampSet.CAMPSTART) { //src011
                L1User_Power value = CharacterC1Reading.get().get(pc.getId());
                if (value != null) {
                    pc.set_c_power(value);
                    if (value.get_c1_type() != 0) {
                        pc.get_c_power().set_power(pc, true);
                        pc.sendPacketsAll(new S_ChangeName(pc, true));
                        // type 僅供訊息時使用，移除未使用變數
                    }
                } /* else if (pc.getMapId() == 99) {
					pc.sendPackets(new S_PacketBoxGree(2, "選擇陣營後即可進入遊戲!"));
				} */
            }
            /*師徒系統取消
			if (ConfigAlt.APPRENTICE_SWITCH) {
				// 檢查是否有收徒弟
				L1Apprentice apprentice = CharApprenticeTable.getInstance().getApprentice(pc);
				// System.out.println(apprentice.getMaster().getName());
				if (apprentice != null) {
					pc.setApprentice(apprentice);
					pc.checkEffect();
				}
			}

			L1Master.getInstance().login(pc);*/

            if (ConfigOtherSet2.ADENA_CHECK_SWITCH) { // 元寶差異紀錄
                final long adenaCount = pc.getInventory().countItems(44070);
                if (adenaCount > 0) {
                    pc.setShopAdenaRecord(adenaCount);
                }
                pc.setSkillEffect(L1SkillId.ADENA_CHECK_TIMER, ConfigOtherSet2.ADENA_CHECK_TIME_SEC * 1000);
            }
            // 7.6
            if (pc.getClanid() != 0) { // 具有血盟
                final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
                if (clan != null) {
                    if (pc.getClanid() == clan.getClanId() && pc.getClanname().equalsIgnoreCase(clan.getClanName())) {
                        // XXX 7.6C ADD
                        pc.sendPackets(new S_PledgeName(pc.getClanname(), pc.getClanRank()));
                    }
                }
            }
            Tam_Window(pc); // 成長果實系統(Tam幣)
            // 怪物圖鑒171020
            UserMonsterBookLoader.load(pc);
            pc.getMonsterBook().sendList();
            if (ConfigLIN.Week_Quest) {
                UserWeekQuestLoader.load(pc);
            }
            // 黑名單
            L1ExcludingList exList = SpamTable.getInstance().getExcludeTable(pc.getId());
            if (exList != null) {
                setExcludeList(pc, exList);
            }
            // 安全區域右下顯示死亡懲罰狀態圖示
            safetyzone(pc);
            InvSwapTable.getInstance().toWorldJoin(pc); // 裝備切換
            pc.sendPackets(new S_PacketBox(S_PacketBox.INVENTORY_SAVE)); // 裝備切換
            pc.sendPackets(new S_EventUI(141)); // 活動UI
            securityBuff(pc); // 安全防禦
            // 給予城堡額外附加能力效果 by terry0412
            CastleWarGiftTable.get().login_gift(pc);
            // 守護者系統
            checkforProtector(pc);
            // 戰魂系統
            checkforMars(pc);
            // 檢查身上的妲蒂斯魔石持有狀態
            checkforDADISStone(pc);
            CheckItemPower(pc); // 身上持有道具給予能力系統
            // 取消商店變身
            L1PolyMorph.undoPolyPrivateShop(pc);
            try {
                // 只有 polyId > 0 才啟動循環自動變身
                if (pc.getLastPolyCardId() > 0) {
                    int polyTime = 1800; // 秒
                    // 若先前已啟動過，startLoginPolyLoop 內部會先 stop 再重啟（依你實作）
                    L1PolyMorph.startLoginPolyLoop(pc, polyTime);
                } else {
                    // 沒卡片就保險停一下
                    L1PolyMorph.stopLoginPolyLoop(pc);
                }
            } catch (Exception e) {
                _log.error("登入時自動變身循環啟動失敗", e);
            }
            pc.removeAICheck(20000, pc.getAICheck());// 刪除AI檢測
            login_Artiface.forIntensifyArmor(pc);// 登入時裝備強化

            StrBonusManager.get().reapply(pc);

            if (ConfigFreeKill.FREE_FIGHT_SWITCH) { //隨機掃街啟動開關
                StringBuilder sbr = CheckFightTimeController.getInstance().getMapList2();
                if (sbr != null) {
                    pc.sendPackets(new S_ServerMessage(sbr.toString()));
                }
            }
            /* [原碼] 定時外掛檢測 */
            if (com.add.L1Config._2226 && !pc.isGm() && !ConfigOtherSet2.NO_AI_MAP_LIST.contains((int) pc.getMapId())) {// 不檢測的地圖
                C_KeepALIVE.setErrorForAI(pc);
            }
            if (CheckMail(pc) > 0) {// 檢查未讀信件狀態
                pc.sendPackets(new S_SkillSound(pc.getId(), 1091));
                pc.sendPackets(new S_ServerMessage(428));
            }
            //L1QuestNew.getInstance();
            // 精靈的祝賀禮物
            CharacterGiftTable.getInstance().sendPacket(pc);
            // 角色資料、背包、裝備、登入還原的 BUFF 都完成後 → 依《能力力量設置》回收+套用加成（內含面板刷新）
            StrBonusManager.get().reapply(pc);
            getUpdate(pc);// 其他狀態更新
            //TODO 修改以下部分無法正常被加載的問題 by 聖子默默
            // 斷點測試結果是原來的代碼寫法以下所有內容均無法載入
            ArrayList<L1UserSkillTmp> skillTemps = CharSkillReading.get().skills(pc.getId());
            Optional.ofNullable(skillTemps).ifPresent(skills -> {
                for (L1UserSkillTmp skillTmp : skillTemps) {
                    L1Skills l1Skills = SkillsTable.get().getTemplate(skillTmp.get_skill_id());
                    if (l1Skills != null && l1Skills.get_buff_iconid() != 0) {
                        pc.sendPackets(new S_InventoryIcon(l1Skills.get_buff_iconid(), true, l1Skills.get_buff_stringid(), -1));
                    }
                }
            });
            // 成就系統
            P_EquipCollectBuff.loadStatus(pc, 2);
            // 更新負重
            pc.sendPackets(new S_WeightStatus(pc.getInventory().getWeight() * 100 / (int) pc.getMaxWeight(), pc.getInventory().getWeight(), (int) pc.getMaxWeight()));
            /*
             * if (pc.getLevel() <= 10) { pc.sendPackets(new
             * S_GmMessage("若有圖檔對話不足問題請先吃檔")); }
             *
             * L1Account account = AccountReading.get().getAccount(loginName);
             * if (account != null && account.get_access_level() == 1) {
             * AccountReading.get().updateAccessLevel(pc.getAccountName());
             *
             * final L1ItemInstance item = ItemTable.get().createItem(140733);
             *
             * if (item != null) {
             *
             * item.setCount(50);
             *
             * // 加入角色專屬倉庫 DwarfReading.get().insertItem(loginName, item); //
             * 重整倉庫數據 pc.sendPackets(new S_ServerMessage(166, "管理員在你的個人倉庫加入物品："
             * + item.getLogName()));
             *
             * pc.getDwarfInventory().loadItems();
             *
             * } }
             */
            /*
             * r=Random.nextInt(30) + 1;
             * pc.setSuper2(r);
             * pc.sendPackets(new S_GmMessage("您目前VIP等級: " + pc.get_vipLevel() +
             * "鑽石VIP", "\\aH"));
             * pc.sendPackets(new S_GmMessage("歡迎進入[幻想世界]仿正伺服器。"));
             * pc.sendPackets(new S_GmMessage("請牢記!您的幸運數字為:"+r, "\\aE"));
             * _log.info("玩家:"+pc.getName()+" 幸運數字:"+pc.getSuper2());
             */
            /*
             * if(!pc.getInventory().checkItem(44125, 1)){
             * pc.getInventory().storeItem(44125, 1); pc.sendPackets(new
             * S_GmMessage("獲得市場中心傳送符。")); //src035 }
             */
            IceQueenThread.deleteIceItem(pc);
            ValakasRoomSystem.deleteIceItem(pc);
            deleteSoulTowerItem(pc);//刪除屍魂副本道具
            deleteIceItem(pc);
            NewAutoPractice.get().addEnemy(pc);
            // 新手任務
            if (pc.getLevel() <= 5) {
                pc.sendPackets(new S_MatizCloudia(0, pc.getLevel()));
                pc.sendPackets(new S_MatizCloudia(1));
            } else if (pc.getLevel() == 8) {
                pc.sendPackets(new S_MatizCloudia(1, 0));
            }
            if (pc.getMapId() == 7783) {
                pc.sendPackets(new S_TestPacket(S_TestPacket.a, 7072, 3810, "00 ff ff"));// 屏幕顯示歡迎來到天堂世界
            }
//            pc.sendPackets(new S_SystemMessage("歡迎來到【" + Config.SERVERNAME + "】遊戲世界", 19));

            _log.info("登入遊戲: " + charName + "(" + pc.getLevel() + ") 帳號(" + loginName + ", " + client.getIp() + ")");

            if (ConfigOtherSet2.Prestigesnatch) {
                GeneralThreadPool.get().schedule(() -> {
                    if (pc != null && !pc.isDead()) {
                        int honorLevel = pc.getHonorLevel();
                        if (honorLevel > 0) {
                            L1WilliamHonor.showHonorSkill(pc, honorLevel); // 補顯示
                            L1WilliamHonor.getHonorSkill(pc);
                            pc.sendPackets(new S_ChangeName(pc, true)); //
                        }
                    }
                }, 1500); // 延遲 1.5 秒（避免在狀態尚未初始化前送出）
            }
            // 1.5秒後加載快捷欄記錄
            final LoadDelayTime delayTime = new LoadDelayTime(pc);
            GeneralThreadPool.get().schedule(delayTime, 3000);
            // 記憶魔法載入 by 老爹
            if (pc.get_other1() != null && pc.get_other1().get_type11() != 0) {
                int skillId = pc.get_other1().get_type11();
                pc.setAttackSkillList(skillId);

                final L1Skills skill = SkillsTable.get().getTemplate(skillId);
                if (skill != null) {
                    String name = skill.getName();
                    pc.sendPackets(new S_SystemMessage("目前將 [" + name + "] 登錄至自動施放陣列內"));
                } else {
                    pc.sendPackets(new S_SystemMessage(" 找不到技能資料: " + skillId));
                }
            }

        } catch (Exception ignored) {
        } finally {
            over();
        }
    }

    /**
     * 檢查是否有未讀信件
     *
     */
    private int CheckMail(L1PcInstance pc) {
        int count = 0;
        Connection con = null;
        PreparedStatement pstm1 = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            //pstm1 = con.prepareStatement(" SELECT count(*) as cnt FROM mails where receiver =? AND read_status = 0");
            pstm1 = con.prepareStatement(" SELECT count(*) as cnt FROM character_mail where receiver =? AND read_status = 0");
            pstm1.setString(1, pc.getName());
            rs = pstm1.executeQuery();
            if (rs.next()) {
                count = rs.getInt("cnt");
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm1);
            SQLUtil.close(con);
        }
        return count;
    }

    /**
     * 王冠資料
     *
     */
    private void crown(L1PcInstance pc) {
        try {
            Map<Integer, L1Clan> map = L1CastleLocation.mapCastle();
            for (Integer key : map.keySet()) {
                L1Clan clan = (L1Clan) map.get(key);
                if (clan != null) {
                    if (key.equals(2)) {
                        pc.sendPackets(new S_CastleMaster(8, clan.getLeaderId()));
                    } else {
                        pc.sendPackets(new S_CastleMaster(key, clan.getLeaderId()));
                    }
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 遊戲焦點及人物狀態更新
     *
     */
    private void getFocus(L1PcInstance pc) {//src014
        try {
            pc.set_showId(-1);// 重置副本編號
            World.get().addVisibleObject(pc);// 將物件增加到MAP世界裡
            if (ConfigTurn.METE_LEVEL > 0 && pc.getMeteLevel() > 0) {// 轉生能力
                pc.resetMeteAbility();
            }
            //            pc.resetPolyPower();
            pc.sendPackets(new S_OwnCharStatus(pc));// 角色資訊
            pc.sendPackets(new S_MapID(pc, pc.getMapId(), pc.getMap().isUnderwater()));// 更新角色所在的地圖
            pc.sendPackets(new S_OwnCharPack(pc));// 物件封包(本身)
            boolean isTimingmap = MapsTable.get().isTimingMap(pc.getMapId());// 傳送地圖是否為限時地圖
            /* 加入限時地圖清單 */
            if (isTimingmap) { // 是限時地圖
                // 地圖限制時間(秒數)
                final int maxMapUsetime = MapsTable.get().getMapTime(pc.getMapId()) * 60;
                // 已使用秒數
                int usedtime = pc.getMapUseTime(pc.getMapId());
                // 剩餘時間(秒)
                int leftTime = maxMapUsetime - usedtime;
                MapTimerThread.put(pc, leftTime);
                // System.out.println("加入限時地圖清單");
            } else if (MapTimerThread.TIMINGMAP.get(pc) != null) {// 在清單中
                MapTimerThread.TIMINGMAP.remove(pc);// 移出清單
                // System.out.println("移出清單");
            }
            ArrayList<L1PcInstance> otherPc = World.get().getVisiblePlayer(pc);
            if (!otherPc.isEmpty()) {
                for (L1PcInstance tg : otherPc) {
                    tg.sendPackets(new S_OtherCharPacks(pc));//// 物件封包(其他人物)
                }
            }
            pc.setVipStatus();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 其他狀態更新
     *
     */
    private void getUpdate(L1PcInstance pc) {
        pc.sendPackets(new S_Mail(pc, 0));
        pc.sendPackets(new S_Mail(pc, 1));
        pc.sendPackets(new S_Mail(pc, 2));
        pc.sendPackets(new S_SPMR(pc));
        pc.sendPackets(new S_Karma(pc));
        pc.sendPackets(new S_Weather(World.get().getWeather()));
        pc.sendPackets(new S_PacketBox(S_PacketBox.UPDATE_ER, pc.getEr()));// 迴避率更新
        pc.sendPackets(new S_PacketBoxIcon1(true, pc.get_dodge()));// 閃避率更新
    }

    /**
     * 婚姻資料
     *
     */
    private void marriage(L1PcInstance pc) {
        try {
            if (pc.getPartnerId() != 0) {
                L1PcInstance partner = (L1PcInstance) World.get().findObject(pc.getPartnerId());
                if (partner != null && partner.getPartnerId() != 0 && pc.getPartnerId() == partner.getId() && partner.getPartnerId() == pc.getId()) {
                    pc.sendPackets(new S_ServerMessage(548));
                    partner.sendPackets(new S_ServerMessage(549));
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 其他數據
     *
     */
    private void getOther(L1PcInstance pc) throws Exception {
        try {
            pc.set_otherList(new L1PcOtherList(pc));
            pc.addMaxHp(pc.get_other().get_addhp());
            pc.addMaxMp(pc.get_other().get_addmp());
            // 掛賣獎勵
            OnlineGiftSet.add(pc);
            int time = pc.get_other().get_usemapTime();
            if (time > 0) {
                // 限時地圖
                if (pc.get_other().get_usemap() != -1 && pc.getMapId() != pc.get_other().get_usemap()) {
                    pc.get_other().set_usemapTime(0);
                    pc.get_other().set_usemap(-1);
                    pc.sendPackets(new S_PacketBoxGame(S_PacketBoxGame.STARTTIMECLEAR));
                } else {
                    ServerUseMapTimer.put(pc, time);
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 升級點數獎勵
     *
     */
    private void statsReward(final L1PcInstance pc) { // 點數獎勵
        if (pc.getLevel() >= 51 && pc.getLevel() - 50 > pc.getBonusStats() || pc.getLevel() >= 51 && pc.getLevel() - 50 > pc.getBonusStats() - 49) {
            if (pc.getBaseStr() + pc.getBaseDex() + pc.getBaseCon() + pc.getBaseInt() + pc.getBaseWis() + pc.getBaseCha() < ConfigAlt.POWER * 6) { // 設定能力值上限
                //pc.sendPackets(new S_bonusstats(pc.getId(), 1));
                int bonus = pc.getLevel() - 50 - pc.getBonusStats();// 可以點的點數 XXX 7.6C ADD
                pc.sendPackets(new S_Message_YN(479, bonus));
            }
        }
    }

    /**
     * 玩家登入時處理血盟資訊與血盟戰狀態
     *
     * @param pc 玩家角色
     */
    private void war(final L1PcInstance pc) {
        try {
            // 如果角色有加入血盟（血盟 ID 不為 0）
            if (pc.getClanid() == 0) {
                return;
            }

            // 取得血盟資料
            final L1Clan clan = WorldClan.get().getClan(pc.getClanname());

            if (clan == null) {
                // 血盟不存在，清除角色血盟資訊
                pc.setClanid(0);
                pc.setClanname("");
                pc.setClanRank(0);
                pc.save();
                return;
            }

            // 驗證角色與血盟資料是否一致
            if (pc.getClanid() != clan.getClanId() || !pc.getClanname().equalsIgnoreCase(clan.getClanName())) {
                return;
            }

            // 通知血盟其他線上成員此角色上線
            for (final L1PcInstance member : clan.getOnlineClanMember()) {
                if (member.getId() != pc.getId()) {
                    member.sendPackets(new S_ServerMessage(843, pc.getName())); // 血盟成員 %0 剛進入遊戲
                }
            }

            // 傳送血盟徽章顯示狀態：個人偏好或在攻城戰區域時顯示
            boolean shouldShowEmblem = pc.isClanGfx() ||
                    com.lineage.server.model.L1CastleLocation.checkInAllWarArea(pc.getLocation());
            pc.sendPackets(new S_PacketBox(S_PacketBox.PLEDGE_EMBLEM_STATUS, shouldShowEmblem ? 1 : 0));
            if (shouldShowEmblem) {
                // 與 381 一致：打開全血盟注視名單，確保客戶端顯示所有血盟徽章
                pc.sendPackets(new com.lineage.server.serverpackets.S_ClanMarkSee(2));
            }

            // 顯示線上血盟成員人數
            int onlineCount = clan.getOnlineClanMember().length;
            pc.sendPackets(new S_ServerMessage("\\fU線上血盟成員:" + onlineCount));

            // 若血盟開啟技能系統，顯示技能提示
            if (clan.isClanskill()) {
                int skillId = pc.get_other().get_clanskill();
                switch (skillId) {
                    case 1:
                        pc.sendPackets(new S_ServerMessage(Npc_clan.SKILLINFO[0]));
                        break; // 狂暴
                    case 2:
                        pc.sendPackets(new S_ServerMessage(Npc_clan.SKILLINFO[1]));
                        break; // 寂靜
                    case 4:
                        pc.sendPackets(new S_ServerMessage(Npc_clan.SKILLINFO[2]));
                        break; // 魔擊
                    case 8:
                        pc.sendPackets(new S_ServerMessage(Npc_clan.SKILLINFO[3]));
                        break; // 消魔
                }
            }

            // 加入血盟技能效果

            L1ClanSkills skill = RewardClanSkillsTable.get().getClanSkillsList(clan.getClanSkillId(), clan.getClanSkillLv());
            if (skill != null) {
                ClanSkillDBSet.add(pc, skill);
            }

            // 檢查是否正在血盟戰
            for (final L1War war : WorldWar.get().getWarList()) {
                if (war.checkClanInWar(pc.getClanname())) {
                    final String enemyClan = war.getEnemyClanName(pc.getClanname());
                    if (enemyClan != null) {
                        // 傳送戰爭狀態給玩家
                        pc.sendPackets(new S_War(8, pc.getClanname(), enemyClan)); // 你與 %0 血盟交戰中
                    }
                    break;
                }
            }

        } catch (final Exception e) {
            _log.error("處理 war 方法時發生錯誤: " + e.getLocalizedMessage(), e);
        }
    }


    /**
     * 所在座標位置資料判斷
     *
     */
    private void backRestart(final L1PcInstance pc) {
        try {
            // 副本強制回村
            if (pc.getMapId() >= 4001 && pc.getMapId() <= 4050) {// 屍魂塔
                pc.setX(33705);
                pc.setY(32504);
                pc.setMap((short) 4);
            }
            // 指定MAP回村設置
            final L1GetBackRestart gbr = GetBackRestartTable.get().getGetBackRestart(pc.getMapId());
            if (gbr != null) {
                pc.setX(gbr.getLocX());
                pc.setY(gbr.getLocY());
                pc.setMap(gbr.getMapId());
            }
            // 戰爭區域回村設置
            final int castle_id = L1CastleLocation.getCastleIdByArea(pc);
            if (castle_id > 0) {
                if (ServerWarExecutor.get().isNowWar(castle_id)) {
                    final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
                    if (clan != null) {
                        if (clan.getCastleId() != castle_id) {
                            // 城主
                            int[] loc = new int[3];
                            loc = L1CastleLocation.getGetBackLoc(castle_id);
                            pc.setX(loc[0]);
                            pc.setY(loc[1]);
                            pc.setMap((short) loc[2]);
                        }
                    } else {
                        // クランに所属して居ない場合は帰還
                        int[] loc;
                        loc = L1CastleLocation.getGetBackLoc(castle_id);
                        pc.setX(loc[0]);
                        pc.setY(loc[1]);
                        pc.setMap((short) loc[2]);
                    }
                }
            }
            //pc.setOleLocX(pc.getX());
            //pc.setOleLocY(pc.getY());
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 取得記憶座標資料
     *
     */
    private void bookmarks(final L1PcInstance pc) {
        /*try {
			final ArrayList<L1BookMark> bookList = CharBookReading.get().getBookMarks(pc);
			if (bookList != null) {
				final L1BookConfig config = CharBookConfigReading.get().get(pc.getId());
				final int maxSize = ConfigAlt.CHAR_BOOK_INIT_COUNT + (config != null ? config.getMaxSize() : 0);
				pc.sendPackets(new S_Bookmarks(config != null ? config.getData() : null, maxSize, bookList));
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}*/
        L1BookMark.bookmarkDB(pc); // 日版記憶座標
        pc.sendPackets(new S_BookMarkLoad(pc)); // 日版記憶座標
    }

    /**
     * 人物已學習技能資料
     *
     */
    private void skills(final L1PcInstance pc) {
        try {
            final ArrayList<L1UserSkillTmp> skillList = CharSkillReading.get().skills(pc.getId());
            final int[] skills = new int[31];
            if (skillList != null) {
                if (!skillList.isEmpty()) {
                    for (final L1UserSkillTmp userSkillTmp : skillList) {
                        // 取得魔法資料
                        final L1Skills skill = SkillsTable.get().getTemplate(userSkillTmp.get_skill_id());
                        skills[skill.getSkillLevel() - 1] += skill.getId();
                        //if (skill.getSkillId() >= 233 && skill.getSkillId() <= 239 || skill.getSkillId() == 241) { //  || skill.getSkillId() == 241
                        if (skill.getSkillId() >= 241 && skill.getSkillId() <= 248) {
                            pc.sendPackets(new S_WarriorSkill(S_WarriorSkill.LOGIN, skill.getSkillNumber()));
                        }
                    }
                    // 送出資料
                    pc.sendPackets(new S_AddSkill(pc, skills));
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 殘留的寵物資料
     *
     */
    private void serchSummon(L1PcInstance pc) {
        try {
            Collection<L1SummonInstance> summons = WorldSummons.get().all();
            if (!summons.isEmpty()) {
                for (L1SummonInstance summon : summons) {
                    if (summon.getMaster().getId() == pc.getId()) {
                        summon.setMaster(pc);
                        pc.addPet(summon);
                        S_NewMaster packet = new S_NewMaster(pc.getName(), summon);
                        for (L1PcInstance visiblePc : World.get().getVisiblePlayer(summon)) {
                            visiblePc.sendPackets(packet);
                        }
                    }
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
    /**
     * 血盟推薦資料
     *
     * @param pc
     */
    /*private void ClanMatching(L1PcInstance pc) {
		L1ClanMatching cml = L1ClanMatching.getInstance();
		if (pc.getClanid() == 0) {// 沒有血盟
			if (!pc.isCrown()) {// 不是王族
				// cml.loadClanMatchingApcList_User(pc);
				if (!cml.getMatchingList().isEmpty()) {
					pc.sendPackets(new S_ServerMessage(3245)); // 目前有血盟等待著您。
				}
			} else {
				pc.sendPackets(new S_ServerMessage(3247)); // 請創設血盟並簡單的告知
			}
		} else {// 有血盟
			switch (pc.getClanRank()) {
			case L1Clan.CLAN_RANK_LEAGUE_VICEPRINCE:
			case L1Clan.CLAN_RANK_LEAGUE_PRINCE:
			case L1Clan.CLAN_RANK_LEAGUE_GUARDIAN:
			case L1Clan.CLAN_RANK_GUARDIAN:
			case L1Clan.CLAN_RANK_PRINCE:
				// cml.loadClanMatchingApcList_Crown(pc);
				if (!pc.getInviteList().isEmpty()) {
					pc.sendPackets(new S_ServerMessage(3246)); // 目前有血盟成員等待著您
				}
				break;
			}
			pc.sendPackets(new S_PacketBox(S_PacketBox.PLEDGE_EMBLEM_STATUS, pc.getClan().getEmblemStatus()));// 盟徽識別狀態
		}
	}*/

    /**
     * 保留的BUFF資料
     *
     */
    private void buff(L1PcInstance pc) {
        try {
            CharBuffReading.get().buff(pc);
            pc.sendPackets(new S_PacketBoxActiveSpells(pc));
            CharMapTimeReading.get().getTime(pc);
            // 補發三段加速圖示（避免登入後客戶端未顯示）
            if (pc.hasSkillEffect(L1SkillId.STATUS_BRAVE3)) {
                int left = pc.getSkillEffectTimeSec(L1SkillId.STATUS_BRAVE3);
                if (left > 0) {
                    pc.sendPackets(new S_PacketBoxThirdSpeed(left));
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 黑名單
     *
     */
    private void setExcludeList(L1PcInstance pc, L1ExcludingList exList) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM character_exclude WHERE char_id = ?");
            pstm.setInt(1, pc.getId());
            rs = pstm.executeQuery();
            while (rs.next()) {
                int type = rs.getInt("type");
                String name = rs.getString("exclude_name");
                if (!exList.contains(type, name)) {
                    exList.add(type, name);
                }
            }
        } catch (SQLException e) {
            // _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    /**
     * 安全區域右下顯示死亡懲罰狀態圖示
     *
     */
    private void safetyzone(L1PcInstance pc) {
        if (pc.getZoneType() == 0) {
            if (pc.getSafetyZone()) {
                pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.SAFETYZONE, false));
                pc.setSafetyZone(false);
            }
        } else {
            if (!pc.getSafetyZone()) {
                pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.SAFETYZONE, true));
                pc.setSafetyZone(true);
            }
        }
    }

    /**
     * 安全防禦
     *
     */
    private void securityBuff(L1PcInstance pc) {
        pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_SECURITY_SERVICES));
        pc.addAc(-1);
        pc.sendPackets(new S_OwnCharAttrDef(pc));
    }

    /**
     * 7.6空身屬性額外獎勵
     *
     */
    private void Reward_stats(L1PcInstance pc) {
        // 7.6 空身力量額外獎勵
        if (pc.getBaseStr() >= 25 && pc.getBaseStr() <= 34) {
            pc.addDmgup(1); // 近距離傷害+1
            pc.addHitup(1); // 近距離命中+1
        } else if (pc.getBaseStr() >= 35 && pc.getBaseStr() <= 44) {
            pc.addDmgup(2); // 近距離傷害+2
            pc.addHitup(2); // 近距離命中+2
        } else if (pc.getBaseStr() >= 45) {
            pc.addDmgup(3); // 近距離傷害+3
            pc.addHitup(3); // 近距離命中+3
        }
        // 7.6 空身敏捷額外獎勵
        if (pc.getBaseDex() >= 25 && pc.getBaseDex() <= 34) {
            pc.addBowDmgup(1); // 远距離傷害+1
            pc.addBowHitup(1); // 远距離命中+1
        } else if (pc.getBaseDex() >= 35 && pc.getBaseDex() <= 44) {
            pc.addBowDmgup(2); // 远距離傷害+2
            pc.addBowHitup(2); // 远距離命中+2
        } else if (pc.getBaseDex() >= 45) {
            pc.addBowDmgup(3); // 远距離傷害+3
            pc.addBowHitup(3); // 远距離命中+3
        }
        // 7.6 空身體質額外獎勵
        if (pc.getBaseCon() >= 25 && pc.getBaseCon() <= 34) {
            pc.addHpr(1); // 體力回復量+1
        } else if (pc.getBaseCon() >= 35 && pc.getBaseCon() <= 44) {
            pc.addHpr(2); // 體力回復量+2
        } else if (pc.getBaseCon() >= 45) {
            pc.addHpr(3); // 體力回復量+3
        }
        // 7.6 空身智力額外獎勵
        if (pc.getBaseInt() >= 25 && pc.getBaseInt() <= 34) {
            pc.addSp(1); // 魔攻+1
        } else if (pc.getBaseInt() >= 35 && pc.getBaseInt() <= 44) {
            pc.addSp(2); // 魔攻+2
        } else if (pc.getBaseInt() >= 45) {
            pc.addSp(3); // 魔攻+3
        }
        // 7.6 空身精神額外獎勵
        if (pc.getBaseWis() >= 25 && pc.getBaseWis() <= 34) {
            pc.addMpr(1); // 魔力回復量+1
        } else if (pc.getBaseWis() >= 35 && pc.getBaseWis() <= 44) {
            pc.addMpr(2); // 魔力回復量+2
        } else if (pc.getBaseWis() >= 45) {
            pc.addMpr(3); // 魔力回復量+3
        }
    }

    /**
     * 成長果實系統(Tam幣)
     *
     */
    private void Tam_Window(L1PcInstance pc) {
        pc.sendPackets(new S_TamWindow(pc)); // 顯示TAM幣點數
        int tamcount = pc.tamcount();
        if (tamcount > 0) {
            long tamtime = pc.TamTime();
            int aftertamtime = (int) tamtime;
            if (aftertamtime < 0) {
                aftertamtime = 0;
            }
            if (tamcount == 1) {
                pc.setSkillEffect(L1SkillId.Tam_Fruit1, aftertamtime);
                pc.sendPackets(new S_TamWindow(6100, true, 4181, aftertamtime));
                pc.addAc(-1);
            } else if (tamcount == 2) {
                pc.setSkillEffect(L1SkillId.Tam_Fruit2, aftertamtime);
                pc.sendPackets(new S_TamWindow(6547, true, 4182, aftertamtime));
                pc.addAc(-2);
            } else if (tamcount == 3) {
                pc.setSkillEffect(L1SkillId.Tam_Fruit3, aftertamtime);
                pc.sendPackets(new S_TamWindow(6546, true, 4183, aftertamtime));
                pc.addAc(-3);
            }
            pc.sendPackets(new S_OwnCharStatus(pc));
        }
    }

    public String getType() {
        return super.getType();
    }

    /**
     * 登陆游戏后延迟处理项目
     */
    private static class LoadDelayTime implements Runnable {

        private final L1PcInstance _pc;

        /**
         * 登陆游戏后延迟处理项目
         *
         * @param pc 执行对象
         */
        public LoadDelayTime(L1PcInstance pc) {
            _pc = pc;
        }
        @Override
        public void run() {
            if (_pc.isOutGame()) {
                return;
            }
            try {
                // 1. 加載玩家的快捷鍵設定
                final L1Config config = CharacterConfigReading.get().get(_pc.getId());
                Optional.ofNullable(config).ifPresent(u -> _pc.sendPackets(new S_PacketBoxConfig(config)));
                _pc.sendPackets(new S_SystemMessage("快捷欄資料加載完成", 3));
                // 2. 處理排行榜名次與能力獎勵（呼叫新方法）
                this.loadRankingBonus(_pc);
            } catch (final Exception e) {
                // 統一由外層的 try-catch 處理所有預期外的錯誤，並寫入日誌
                _log.error("玩家 " + _pc.getName() + " 登入後加載初始設定時發生錯誤", e);
            } finally {
                // 3. 準備加載後續的狀態（無論前面是否成功，這一步都會執行）
                _pc.sendPackets(new S_SystemMessage("準備加載其他框架屬性點加成", 17));
                LoadOtherStatus status = new LoadOtherStatus(_pc);
                GeneralThreadPool.get().schedule(status, 2000);
            }
        }

        /**
         * 為指定玩家加載排行榜名次與對應的能力獎勵。
         *
         * @param pc 玩家實體物件
         */
        private void loadRankingBonus(final L1PcInstance pc) {
            try {
                final String name = pc.getName();
                final int realRank = com.lineage.server.datatables.T_RankTable.get().getRankOfPlayer(name);
                pc.setRanking(realRank); // 設定玩家的排名資訊
                // 如果排名無效（<=0），則直接返回，不執行後續操作
                if (realRank <= 0) {
                    return;
                }
                // 根據排名獲取獎勵物件
                final com.lineage.system.RankingAbilityBonus.AbilityBonus bonus = com.lineage.system.RankingAbilityBonus.getBonusByRank(realRank);
                // 確保獎勵物件存在才執行能力賦予
                if (bonus != null) {
                    pc.addStr(bonus.str);
                    pc.addDex(bonus.dex);
                    pc.addCon(bonus.con);
                    pc.addWis(bonus.wis);
                    pc.addCha(bonus.cha);
                    pc.addInt(bonus.intel);
                    pc.setPvpDmg(bonus.pvpDamageIncrease);
                    pc.setPvpDmg_R(bonus.pvpDamageReduction);
                    pc.addMaxHp(bonus.hp);
                    pc.addMaxMp(bonus.mp);
                    pc.sendPackets(new S_SystemMessage("你獲得了排行榜第 " + realRank + " 名的能力獎勵！", 4));
                    // 處理狀態圖示顯示
                    if (bonus._buff_iconid != 0 && bonus._buff_stringid != 0) {
                        if (bonus._time_string == 0) {
                            pc.sendPackets(new S_InventoryIcon(bonus._buff_iconid, true, bonus._buff_stringid, -1));
                        } else {
                            pc.sendPackets(new S_InventoryIcon(bonus._buff_iconid, true, bonus._buff_stringid, bonus._buff_stringid));
                        }
                    }
                }
            } catch (final Exception e) {
                // 使用 _log 記錄錯誤，更符合伺服器開發規範
                _log.error("為玩家 " + pc.getName() + " 加載排行榜獎勵時發生錯誤", e);
            }
        }
        /**
         * 延遲順序加載千奇百怪的屬性點加成資料
         */
        private static class LoadOtherStatus implements Runnable {

            private final L1PcInstance _pc;

            public LoadOtherStatus(L1PcInstance pc) {
                _pc = pc;
            }

            @Override
            public void run() {
                if (_pc.isOutGame()) {
                    return;
                }
                try {
                    for (int i = 0; i < 20; i++) {
                        switch (i) {
                            case 0:
                                addWyType1(_pc);
                                break;
                            case 1:
                                addWyType2(_pc);
                                break;
                            case 2:
                                addWyType3(_pc);
                                break;
                            case 3:
                                addWyType4(_pc);
                                break;
                            case 4:
                                addWyType5(_pc);
                                break;
                            case 5:
                                addWyType6(_pc);
                                break;
                            case 6:
                                addCardsStatus(_pc);
                                break;
                            case 7:
                                addCardSetStatus(_pc);
                                break;
                            case 8:
                                addCollectsStatus(_pc);
                                break;
                            case 9:
                                addCollectSetStatus(_pc);
                                break;
                            case 10:
                                addDollsStatus(_pc);
                                break;
                            case 11:
                                addDollSetStatus(_pc);
                                break;
                            case 12:
                                addHolysStatus(_pc);
                                break;
                            case 13:
                                addHolySetStatus(_pc);
                                break;
                            case 14:
                                _pc.addAstrologyPower();// 宙斯·守護星盤(舊)所有已解鎖卡牌能力值加載
                                _pc.addAttonAstrologyPowers(); // 阿頓星盤：自動套用所有非技能節點
                                _pc.addSilianAstrologyPowers(); // 絲莉安星盤：自動套用所有非技能節點
                                _pc.addGritAstrologyPowers();   // 格立特星盤：自動套用所有非技能節點
                                _pc.addYishidiAstrologyPowers(); // 依詩蒂星盤：自動套用所有非技能節點
                                // 讀回絲莉安各技能冷卻（若仍在未來，轉回毫秒放入記憶體）
                                try {
                                    if (_pc.get_other() != null) {
                                        final long nowMs = System.currentTimeMillis();
                                        int cd1s = _pc.get_other().get_silian_cd1_until_s();
                                        int cd2s = _pc.get_other().get_silian_cd2_until_s();
                                        int cd3s = _pc.get_other().get_silian_cd3_until_s();
                                        long cd1ms = ((long) cd1s) * 1000L;
                                        long cd2ms = ((long) cd2s) * 1000L;
                                        long cd3ms = ((long) cd3s) * 1000L;
                                        if (cd1ms > nowMs) _pc.setSilianCooldown1Until(cd1ms); else _pc.setSilianCooldown1Until(0L);
                                        if (cd2ms > nowMs) _pc.setSilianCooldown2Until(cd2ms); else _pc.setSilianCooldown2Until(0L);
                                        if (cd3ms > nowMs) _pc.setSilianCooldown3Until(cd3ms); else _pc.setSilianCooldown3Until(0L);

                                        // 不再恢復 HOT/ICON（ICON 僅在施放時顯示冷卻秒）
                                    }
                                } catch (Throwable ignore) {}
                                _pc.sendPackets(new S_SystemMessage("\\aB獲得星盤能力加成。", 17));
                                break;
                        }
                        TimeUnit.SECONDS.sleep(1);
                    /* check
                    addWyType1(_pc);
                    addWyType2(_pc);
                    addWyType3(_pc);
                    addWyType4(_pc);
                    addWyType5(_pc);
                    addWyType6(_pc);
                    addCardsStatus(_pc);
                    addCardSetStatus(_pc);
                    addCollectsStatus(_pc);
                    addCollectSetStatus(_pc);
                    addDollsStatus(_pc);
                    addDollSetStatus(_pc);
                    addHolysStatus(_pc);
                    addHolySetStatus(_pc);
                    _pc.addAstrologyPower();
                    */
                    }
                } catch (InterruptedException e) {
                    _log.error(e.getLocalizedMessage(), e);
                }
            }
        }
    }
}