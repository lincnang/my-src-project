package com.lineage.server.model.skill;

import com.lineage.config.ConfigFreeKill;
import com.lineage.config.ConfigOtherSet2;
import com.lineage.data.event.EffectAISet;
import com.lineage.data.event.ItemBuffSet;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.CheckFightTimeController;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.datatables.*;
import com.lineage.server.datatables.lock.IpReading;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1HouseLocation;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.skill.skillmode.SkillMode;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1SkillEnhance;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.types.Point;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.utils.URandom;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.lineage.server.model.skill.L1SkillId.*;

/**
 * 技能停止
 *
 * @author dexc
 */
public class L1SkillStop {
    private static final Log _log = LogFactory.getLog(L1SkillStop.class);
    // 強制踢除
    private static ClientExecutor _client;
    private static final Set<Integer> UI_REFRESH_SKILL_IDS = new HashSet<>(Arrays.asList(
            SHAPE_CHANGE, POLYBOOKBUFF, TOP10BUFF, LV90BUFF
    ));


    public static void broadcastPacketWorld(final ServerBasePacket packet) { // 特效驗證系統
        try {
            for (L1PcInstance pc : World.get().getAllPlayers()) {
                if (pc != null) {
                    pc.sendPackets(packet);
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public static void stopSkill(L1Character cha, int skillId) {
        try {
            SkillMode mode = L1SkillMode.get().getSkill(skillId);
            if (mode != null) {// 具有SkillMode
                mode.stop(cha);
                if ((cha instanceof L1PcInstance)) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    if (pc.getNESkillId() == skillId || pc.getNESkillId2() == skillId) {
                        if (pc.getNESkillId() != 0) {
                            final L1Skills skill = SkillsTable.get().getTemplate(pc.getNESkillId());
                            final L1SkillUse skillUse = new L1SkillUse();
                            skillUse.handleCommands(pc, pc.getNESkillId(), pc.getId(), pc.getX(), pc.getY(), skill.getBuffDuration(), L1SkillUse.TYPE_GMBUFF);
                        }
                        if (pc.getNESkillId2() != 0) {
                            final L1Skills skill = SkillsTable.get().getTemplate(pc.getNESkillId2());
                            final L1SkillUse skillUse = new L1SkillUse();
                            skillUse.handleCommands(pc, pc.getNESkillId2(), pc.getId(), pc.getX(), pc.getY(), skill.getBuffDuration(), L1SkillUse.TYPE_GMBUFF);
                        }
                    }
                }
            } else {// 沒有SkillMode
                if ((cha instanceof L1PcInstance)) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    for (int i = 0; i < ItemTable.itembuff.size(); i++) {
                        if (skillId == (Integer) ItemTable.itembuff.get(i)) {
                            L1Item temper = ItemTable.get().getTemplate(skillId);
                            String[] status = ((String) ItemTable.itembuffs.get(i)).split(" ");
                            int _str = Integer.parseInt(status[1]);
                            int _dex = Integer.parseInt(status[2]);
                            int _int = Integer.parseInt(status[3]);
                            int _con = Integer.parseInt(status[4]);
                            int _wis = Integer.parseInt(status[5]);
                            int _cha = Integer.parseInt(status[6]);
                            int _magic = Integer.parseInt(status[7]);
                            int _damage = Integer.parseInt(status[8]);
                            int _range = Integer.parseInt(status[9]);
                            int _hit = Integer.parseInt(status[10]);
                            int _hp = Integer.parseInt(status[11]);
                            int _mp = Integer.parseInt(status[12]);
                            pc.addStr(-_str);
                            pc.addDex(-_dex);
                            pc.addInt(-_int);
                            pc.addCon(-_con);
                            pc.addWis(-_wis);
                            pc.addCha(-_cha);
                            pc.add_magic_plus(-_magic);
                            pc.add_damage_plus(-_damage);
                            pc.add_range_plus(-_range);
                            pc.addHitup(-_hit);
                            pc.addMaxHp(-_hp);
                            pc.addMaxMp(-_mp);
                            pc.sendPackets(new S_HPUpdate(pc));
                            pc.sendPackets(new S_MPUpdate(pc));
                            pc.sendPackets(new S_OwnCharStatus(pc));
                            pc.sendPackets(new S_SystemMessage(temper.getName() + " 效果已結束。"));
                        }
                    }
                }
                // 道具狀態系統
                if (ItemBuffSet.START && ItemBuffTable.get().checkItem(skillId)) {
                    if (cha instanceof L1PcInstance) {
                        final L1PcInstance pc = (L1PcInstance) cha;
                        ItemBuffTable.get().remove(pc, skillId);
                    }
                }
                switch (skillId) {
                    // 特效驗證系統
                    case AIFORSTART:
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            Random _random = new Random();
                            int rndxx = pc.getX() + _random.nextInt(20) - 10;
                            int rndyy = pc.getY() + _random.nextInt(20) - 10;
                            while (CheckUtil.checkPassable(pc, rndxx, rndyy, pc.getMapId()) || !pc.getMap().isInMap(rndxx, rndyy) || pc.getMap().getOriginalTile(rndxx, rndyy) == 0 || pc.getX() == rndxx && pc.getY() == rndyy || !pc.glanceCheck(rndxx, rndyy) || !pc.getMap().isPassable(rndxx, rndyy, 0, null) || !pc.getMap().isPassable(rndxx, rndyy, 1, null) || !pc.getMap().isPassable(rndxx, rndyy, 2, null) || !pc.getMap().isPassable(rndxx, rndyy, 3, null) || !pc.getMap().isPassable(rndxx, rndyy, 4, null) || !pc.getMap().isPassable(rndxx, rndyy, 5, null) || !pc.getMap().isPassable(rndxx, rndyy, 6, null) || !pc.getMap().isPassable(rndxx, rndyy, 7, null)) {
                                rndxx = pc.getX() + _random.nextInt(20) - 10;
                                rndyy = pc.getY() + _random.nextInt(20) - 10;
                            }
                            int[] xyz = {rndxx, rndyy};
                            pc.set_aixyz(xyz);
                            // }
                            pc.sendPackets(new S_PacketBoxGree(0x01));
                            pc.sendPackets(new S_EffectLocation(pc.get_aixyz()[0], pc.get_aixyz()[1], ServerAIEffectTable.getEffectId()));
                            String msg = "請 " + (EffectAISet.AI_ANSWER_TIME / 1000) + " 秒內移動至指定位置完成驗證。";
                            // pc.sendPackets(new S_BlueMessage(166, "\\f3" + msg));
                            pc.sendPackets(new S_AllChannelsChat(msg, 3));
                            pc.sendPackets(new S_ServerMessage("\n\r" + msg + "\n\r" + msg + "\n\r" + msg + "\n\r" + msg + "\n\r" + msg + "\n\r" + msg));
                            pc.setSkillEffect(AIFOREND, EffectAISet.AI_ANSWER_TIME);
                        }
                        break;
                    case AIFOREND:
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addAIERROR();
                            int error = pc.getAIERROR();
                            if (error >= EffectAISet.AI_ERROR_COUNT) {
                                int type = EffectAISet.AI_ERROR_TYPE;
                                if (type == 3) {
                                    WriteLogTxt.Recording("未通過驗證紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【" + pc.getName() + "】未通過驗證 已封鎖帳號.");
                                    broadcastPacketWorld(new S_BlueMessage(166, "\\f=玩家 " + pc.getName() + " 經過系統驗證未通過 所以被GM大大羈押了！"));
                                    IpReading.get().add(pc.getAccountName().toString(), "驗證失敗" + ":封鎖帳號");
                                    pc.getNetConnection().kick();
                                }
                                if (type == 2) {
                                    WriteLogTxt.Recording("未通過驗證紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【" + pc.getName() + "】未通過驗證 已封鎖IP.");
                                    broadcastPacketWorld(new S_BlueMessage(166, "\\f=玩家 " + pc.getName() + " 經過系統驗證未通過 所以被GM大大羈押了！"));
                                    IpReading.get().add(pc.getNetConnection().getIp().toString(), "驗證失敗" + ":封鎖IP");
                                    pc.getNetConnection().kick();
                                } else if (type == 1) {
                                    pc.setAIERROR();
                                    L1Teleport.teleport(pc, EffectAISet.AI_LOCX, EffectAISet.AI_LOCY, EffectAISet.AI_MAPID, pc.getHeading(), false);
                                }
                            } else {
                                pc.setSkillEffect(AIFORSTART, 1000);
                            }
                        }
                        break;
                    case ADENA_CHECK_TIMER:
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            final long adenaCount = pc.getInventory().countItems(44070);
                            if (adenaCount > 0) {
                                final long difference = adenaCount - pc.getShopAdenaRecord();
                                if (difference >= ConfigOtherSet2.ADENA_CHECK_COUNT_DIFFER) {
                                    WriteLogTxt.Recording("元寶差異紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【" + pc.getName() + "】的在線元寶數量增加:【" + difference + "】個.");
                                } else if (difference <= -ConfigOtherSet2.ADENA_CHECK_COUNT_DIFFER) {
                                    WriteLogTxt.Recording("元寶差異紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【" + pc.getName() + "】的在線元寶數量減少:【" + Math.abs(difference) + "】個.");
                                }
                                pc.setShopAdenaRecord(adenaCount);
                            }
                            pc.setSkillEffect(ADENA_CHECK_TIMER, ConfigOtherSet2.ADENA_CHECK_TIME_SEC * 1000);
                        }
                        break;
                    /* [原碼] 定時外掛檢測 */
                    case AI_1:// 檢測延遲時間
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (!ConfigOtherSet2.NO_AI_MAP_LIST.contains((int) pc.getMapId())) {
                                // 城堡戰爭區域
                                if (L1CastleLocation.checkInAllWarArea(pc.getX(), pc.getY(), pc.getMapId())) {
                                    return;
                                }
                                // 盟屋內座標
                                if (L1HouseLocation.isInHouse(pc.getX(), pc.getY(), pc.getMapId())) {
                                    return;
                                }
                                // 世界樹下
                                if ((pc.getMapId() == 4) && (pc.getLocation().isInScreen(new Point(33055, 32336)))) {
                                    return;
                                }
                                // 新人房間
                                if (pc.getMapId() == 99) {
                                    return;
                                }
                                // 等級低於30
                                if (pc.getLevel() <= 30) {
                                    return;
                                }
                                // 安全區域不判斷
                                if (pc.isSafetyZone()) {
                                    return;
                                }
                                String[] randomStr = {"請輸入以下答案 ", "康熙來了  ", "１８歲不睡  ", "ＭＯＭＯ購物台 ", "東森幼幼台 ", "蔡依林考考你 ", "小叮噹問問題", "中秋節快樂 ", "數學老師來了 ", "今天心情好嗎 ", "晚上好 ", "好孩子不掛機唷 "};
                                setAICheck(pc);
                                pc.sendPackets(new S_BlueMessage(166, "\\f3" + randomStr[URandom.nextInt(randomStr.length)] + "\\f2" + pc.getAICheck()));
                                pc.sendPackets(new S_ServerMessage("\\f3" + randomStr[URandom.nextInt(randomStr.length)] + "\\f2" + pc.getAICheck()));//src025
                                pc.sendPackets(new S_ServerMessage("\\f3" + randomStr[URandom.nextInt(randomStr.length)] + "\\f2" + pc.getAICheck()));//src025
                                pc.sendPackets(new S_ServerMessage("\\f3" + randomStr[URandom.nextInt(randomStr.length)] + "\\f2" + pc.getAICheck()));//src025
                                pc.sendPackets(new S_ServerMessage("\\f3" + randomStr[URandom.nextInt(randomStr.length)] + "\\f2" + pc.getAICheck()));//src025
                                pc.sendPackets(new S_ServerMessage("\\f3" + randomStr[URandom.nextInt(randomStr.length)] + "\\f2" + pc.getAICheck()));//src025
                                pc.sendPackets(new S_ServerMessage("\\f3" + randomStr[URandom.nextInt(randomStr.length)] + "\\f2" + pc.getAICheck()));//src025
                                pc.setSkillEffect(AI_2, 4 * 60 * 1000); // 4分鐘
                            }
                        }
                        break;
                    case AI_2:// 輸入答案等待時間
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (!ConfigOtherSet2.NO_AI_MAP_LIST.contains((int) pc.getMapId())) {
                                // 城堡戰爭區域
                                if (L1CastleLocation.checkInAllWarArea(pc.getX(), pc.getY(), pc.getMapId())) {
                                    return;
                                }
                                // 盟屋內座標
                                if (L1HouseLocation.isInHouse(pc.getX(), pc.getY(), pc.getMapId())) {
                                    return;
                                }
                                // 世界樹下
                                if ((pc.getMapId() == 4) && (pc.getLocation().isInScreen(new Point(33055, 32336)))) {
                                    return;
                                }
                                // 新人房間
                                if (pc.getMapId() == 99) {
                                    return;
                                }
                                // 等級低於30
                                if (pc.getLevel() <= 30) {
                                    return;
                                }
                                // 安全區域不判斷
                                if (pc.isSafetyZone()) {
                                    return;
                                }
                                pc.setBanError(pc.getBanError() + 1);// 逾時封鎖判斷次數+1
                                pc.sendPackets(new S_SystemMessage("未通過檢測。"));
                                pc.saveInventory();
                                pc.sendPackets(new S_Disconnect());
                                World.get().broadcastServerMessage("玩家 : " + pc.getName() + " 因逾時未通過外掛偵測，已強制切斷其連線。");
                                _log.info(String.format("玩家 : %s 因逾時未通過外掛偵測，已強制切斷其連線", pc.getName()));
                                if (pc.getBanError() >= 5) {// 逾時斷線5次
                                    long time = 60 * 60 * 1000 * 24 * 7;// 七日
                                    Timestamp UnbanTime = new Timestamp(System.currentTimeMillis() + time);
                                    IpReading.get().setUnbanTime(UnbanTime);
                                    IpReading.get().add(pc.getAccountName(), "定時外掛檢測 自動封鎖帳號七日 當日逾時五次");
                                    pc.saveInventory();
                                    pc.sendPackets(new S_Disconnect());
                                    World.get().broadcastServerMessage("玩家 : " + pc.getName() + " 因當日逾時五次未通過外掛偵測，已封鎖其帳號七日。");
                                    _log.info(String.format("玩家 : %s 因當日逾時五次未通過外掛偵測，已封鎖其帳號七日。", pc.getName()));
                                }
                            }
                        }
                        break;
                    // 重置天賦技能掉線狀態
                    case ReiSkill_Disconnect:
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            if (!pc.isGm()) {
                                pc.sendPackets(new S_Disconnect());
                            }
                        }
                        break;
                    // 虛弱-攻擊減半狀態
                    case negativeId15:
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 0, 488));// 關閉圖示
                        }
                        break;
                    // 戰士天賦技能冰封心智狀態 or 放逐-使對方無法攻擊
                    case ReiSkill_2:
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 0, 566));// 關閉圖示
                        }
                        break;
                    // 破甲-無減傷-PVP減傷減半-狀態
                    case negativeId13:
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 0, 561));// 關閉圖示
                        }
                        break;
                    // 刪除紅騎士團徽章狀態
                    case Red_Knight:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_WarIcon(pc.getId())); // 刪除紅騎士團徽
                            pc.broadcastPacketAll(new S_WarIcon(pc.getId())); // 刪除紅騎士團徽
                        }
                        break;
                    // 刪除遊戲各類徽章
                    case MiniGameIcon_1:
                    case MiniGameIcon_2:
                    case MiniGameIcon_3:
                    case MiniGameIcon_4:
                    case MiniGameIcon_5:
                    case MiniGameIcon_6:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_MiniGameIcon(pc.getId())); // 刪除遊戲各類徽章
                            pc.broadcastPacketAll(new S_MiniGameIcon(pc.getId())); // 刪除遊戲各類徽章
                        }
                        break;
                    // 下層戰鬥強化卷軸
                    case LOWER_FLOOR_GREATER_BATTLE_SCROLL:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addHitup(-30); // 攻擊成功 -30
                            pc.addDmgup(-30); // 額外攻擊點數 -30
                            pc.addBowHitup(-30); // 遠距離命中率 -30
                            pc.addBowDmgup(-30); // 遠距離攻擊力 -30
                            pc.addSp(-30); // 魔攻 -30
                            pc.sendPackets(new S_SPMR(pc));
                            pc.sendPackets(new S_SystemMessage("下層戰鬥強化卷軸效果消失！"));
                        }
                        break;
                    // 下層防禦強化卷軸
                    case LOWER_FLOOR_GREATER_DEFENSE_SCROLL:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addAc(50);// 防禦
                            pc.sendPackets(new S_SystemMessage("下層防禦強化卷軸效果消失！"));
                        }
                        break;
                    // 升級經驗獎勵狀態
                    case LEVEL_UP_BONUS:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_PacketBox(0, true, true));
                        }
                        break;
                    case Mazu:
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_SystemMessage("媽祖狀態結束。"));
                            pc.set_mazu(false);
                            pc.set_mazu_time(0);
                        }
                        break;
                    case 9964:
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_ServerMessage("魔法占卜效果消失"));
                            pc.addStr(-6);
                            pc.sendPackets(new S_OwnCharStatus2(pc));
                        }
                        break;
                    case 9965:
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_ServerMessage("魔法占卜效果消失"));
                            pc.addDex(-6);
                            pc.sendPackets(new S_OwnCharStatus2(pc));
                        }
                        break;
                    case 9966:
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_ServerMessage("魔法占卜效果消失"));
                            pc.addInt(-6);
                            pc.sendPackets(new S_OwnCharStatus2(pc));
                        }
                        break;
                    case 9967:
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_ServerMessage("魔法占卜效果消失"));
                            pc.addDmgup(-15);
                            pc.addBowDmgup(-12);
                        }
                        break;
                    case 9968:
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_ServerMessage("魔法占卜效果消失"));
                            pc.addHitup(-10);
                            pc.addBowHitup(-7);
                        }
                        break;
                    case 9969:
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_ServerMessage("魔法占卜效果消失"));
                            pc.addSp(-5);
                            pc.sendPackets(new S_SPMR(pc));
                        }
                        break;
                    case LIGHT:// 日光術
                        if (((cha instanceof L1PcInstance)) && (!cha.isInvisble())) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.turnOnOffLight();
                        }
                        break;
                    case GLOWING_AURA:// 灼熱武器 //(灼熱靈氣：1階段)
                        cha.addHitup(-5);
                        cha.addDmgup(-5);
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_PacketBoxIconAura(113, 0));
                        }
                        break;
                    case SHINING_AURA://閃亮之盾 //(灼熱靈氣：2階段)
                        cha.addAc(8);
                        cha.addMr(-10);
                        cha.addHitup(-2);
                        cha.addDmgup(-2);
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_SPMR(pc));
                            pc.sendPackets(new S_PacketBoxIconAura(114, 0));
                        }
                        break;
                    case BRAVE_AURA://勇猛意志
                        // cha.addDmgup(-5);
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_PacketBoxIconAura(116, 0));
                        }
                        break;
                    case GLOWING_AURA_2:// 勇猛武器
                        cha.addHitup(-10);
                        cha.addDmgup(-10);
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_SPMR(pc));
                            pc.sendPackets(new S_PacketBoxIconAura(117, 0));
                        }
                        break;
                    case Armor_Valor://勇猛盔甲
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addDamageReductionByArmor(-7);
                            pc.sendPackets(new S_SPMR(pc));
                            pc.sendPackets(new S_PacketBoxIconAura(118, 0));
                        }
                        break;
                    case DEATH_HEAL: // 法師新技能 治癒逆行
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.removeSkillEffect(DEATH_HEAL);
                            pc.sendPackets(new S_NewSkillIcon(DEATH_HEAL, false, -1));
                        }
                        break;
                    case ABSOLUTE_BLADE: // 騎士新技能 絕禦之刃
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.removeSkillEffect(ABSOLUTE_BLADE);
                            pc.sendPackets(new S_NewSkillIcon(ABSOLUTE_BLADE, false, -1));
                        }
                        break;
                    case ASSASSIN: // 黑妖新技能 暗殺者
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.removeSkillEffect(ASSASSIN);
                            pc.sendPackets(new S_NewSkillIcon(ASSASSIN, false, -1));
                        }
                        break;
                    case BLAZING_SPIRITS: // 黑妖新技能 熾烈鬥志
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.removeSkillEffect(BLAZING_SPIRITS);
                            pc.sendPackets(new S_NewSkillIcon(BLAZING_SPIRITS, false, -1));
                        }
                        break;
                    case GRACE_AVATAR: // 王族新技能 恩典庇護
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addRegistSustain(-10 + (pc.getGraceLv() * -1)); // 支撐耐性
                            pc.addRegistStun(-10 + (pc.getGraceLv() * -1)); // 暈眩耐性
                            //pc.getResistance().addDESPERADO(-10 + (pc.getGraceLv() * -1)); // 恐怖耐性
                            pc.removeSkillEffect(GRACE_AVATAR);
                            pc.sendPackets(new S_NewSkillIcon(GRACE_AVATAR, false, -1));
                        }
                        break;
                    case SOUL_BARRIER: // 精靈新技能 魔力護盾
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.removeSkillEffect(SOUL_BARRIER);
                            pc.sendPackets(new S_NewSkillIcon(SOUL_BARRIER, false, -1));
                        }
                        break;
                    case DESTROY: // 龍騎士新技能 撕裂護甲
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.removeSkillEffect(DESTROY);
                            pc.sendPackets(new S_NewSkillIcon(DESTROY, false, -1));
                        }
                        break;
                    case IMPACT: // 幻術師新技能 衝突強化
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.removeSkillEffect(IMPACT);
                            pc.sendPackets(new S_NewSkillIcon(IMPACT, false, -1));
                            pc.setImpactUp(0);
                        }
                        break;
                    case TITANL_RISING: // 狂戰士新技能 泰坦狂暴
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.removeSkillEffect(TITANL_RISING);
                            pc.sendPackets(new S_NewSkillIcon(TITANL_RISING, false, -1));
                            pc.setRisingUp(0);
                        }
                        break;
                    case SHIELD://保護罩
                        cha.addAc(2);
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_SkillIconShield(5, 0));
                        }
                        break;
                    case BLIND_HIDING://暗隱術
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.delBlindHiding();
                        }
                        break;
                    case STATUS_FREEZE://魔法傚果:凍結
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, false));
                        } else if (cha instanceof L1MonsterInstance || cha instanceof L1SummonInstance || cha instanceof L1PetInstance) {
                            L1NpcInstance npc = (L1NpcInstance) cha;
                            npc.setParalyzed(false);
                        }
                        break;
                    case DRESS_DEXTERITY:// 敏捷提升
                        cha.addDex(-3);
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_Dexup(pc, 3, 0));
                        }
                        break;
                    case DRESS_MIGHTY:// 力量提升
                        cha.addStr(-3);
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_Strup(pc, 3, 0));
                        }
                        break;
                    case EARTH_BLESS: // 大地的護衛
                        // cha.addAc(7);
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_SkillIconShield(7, 0));
                        }
                        break;
                    case RESIST_MAGIC:
                        cha.addMr(-10);
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_SPMR(pc));
                        }
                        break;
                    case CLEAR_MIND:
                        cha.addWis(-3);
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.resetBaseMr();
                        }
                        break;
                    case RESIST_ELEMENTAL:
                        cha.addWind(-10);
                        cha.addWater(-10);
                        cha.addFire(-10);
                        cha.addEarth(-10);
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_OwnCharAttrDef(pc));
                        }
                        break;
                    case ELEMENTAL_PROTECTION:
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            int attr = pc.getElfAttr();
                            if (attr == 1) {
                                cha.addEarth(-50);
                            } else if (attr == 2) {
                                cha.addFire(-50);
                            } else if (attr == 4) {
                                cha.addWater(-50);
                            } else if (attr == 8) {
                                cha.addWind(-50);
                            }
                            pc.sendPackets(new S_OwnCharAttrDef(pc));
                        }
                        break;
                    case ERASE_MAGIC: // 魔法消除
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_PacketBoxIconAura(152, 0));
                        }
                        break;
                    case WATER_LIFE://水之元氣
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_PacketBoxWaterLife());
                        }
                        break;
                    case IRON_SKIN://鋼鐵防護
                        cha.addAc(10);
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_SkillIconShield(10, 0));
                        }
                        break;
                    case EARTH_SKIN://大地防護
                        cha.addAc(6);
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_SkillIconShield(6, 0));
                        }
                        break;
                    case PHYSICAL_ENCHANT_STR://體魄強健術
                        cha.addStr(-5);
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            //pc.sendPackets(new S_Strup(pc, 5, 0));
                            pc.sendPackets(new S_Strup(pc, 1, 0));
                        }
                        break;
                    case PHYSICAL_ENCHANT_DEX://通暢氣脈術
                        cha.addDex(-5);
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            //pc.sendPackets(new S_Dexup(pc, 5, 0));
                            pc.sendPackets(new S_Dexup(pc, 1, 0));
                        }
                        break;
                    case PHYSICAL_ENCHANT_INT_DEX_STR://體力強化
                        cha.addInt(-1);
                        cha.addDex(-1);
                        cha.addStr(-1);
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            //pc.sendPackets(new S_Strup(pc, 5, 0));
                            //						pc.sendPackets(new S_Intup(pc, 1, 0));
                            pc.sendPackets(new S_Dexup(pc, 1, 0));
                            pc.sendPackets(new S_Strup(pc, 1, 0));
                            //						pc.sendPackets(new S_InventoryIcon(10534, false, 2747, 0));
                        }
                        break;
                    case FIRE_WEAPON://火焰武器
                        cha.addDmgup(-4);
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_PacketBoxIconAura(147, 0));
                        }
                        break;
                    case FIRE_BLESS://舞躍之火
                        // cha.addDmgup(-4);
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_PacketBoxIconAura(154, 0));
                        }
                        break;
                    case BURNING_WEAPON://烈炎武器
                        cha.addDmgup(-6);
                        cha.addHitup(-3);
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_PacketBoxIconAura(162, 0));
                        }
                        break;
                    case WIND_SHOT://風之神射
                        cha.addBowHitup(-6);
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_PacketBoxIconAura(148, 0));
                        }
                        break;
                    case STORM_EYE://暴風之眼
                        cha.addBowHitup(-2);
                        cha.addBowDmgup(-3);
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_PacketBoxIconAura(155, 0));
                        }
                        break;
                    case STORM_SHOT://暴風神射
                        cha.addBowDmgup(-5);
                        cha.addBowHitup(1);
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_PacketBoxIconAura(165, 0));
                        }
                        break;
                    case BERSERKERS: // 狂暴術停止時
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            // 取得玩家狂暴術技能等級
                            int rawLv = pc.getSkillLevel(BERSERKERS);
                            // 只有吃書後才取強化
                            L1SkillEnhance enhanceData = SkillEnhanceTable.get().getEnhanceData(BERSERKERS, rawLv);
                            // 預設移除數值（移除狂暴術時要恢復防禦，扣除攻擊加成）
                            int removeAc = 10;       // AC -10 (恢復防禦)
                            int removeDmgup = 5;     // 傷害 -5 (移除攻擊加成)
                            // 若有強化資料則以設定值為準
                            if (enhanceData != null) {
                                removeAc = enhanceData.getSetting1();
                                removeDmgup = enhanceData.getSetting2();
                                // 值域保護，避免DB異常
                                if (removeAc < -100) removeAc = -100;
                                if (removeAc > 100) removeAc = 100;
                                if (removeDmgup < -200) removeDmgup = -200;
                                if (removeDmgup > 200) removeDmgup = 200;
                            }
                            // 恢復效果：減少AC（提升防禦），減少傷害
                            cha.addAc(-removeAc);       // 減少AC（恢復防禦）
                            cha.addDmgup(-removeDmgup); // 減少傷害
                            // 重新啟動 HP 回復
                            pc.startHpRegeneration();
                        } else {
                            // 若角色不是 L1PcInstance，恢復預設值
                            cha.addAc(-10);  // 減少AC（恢復防禦）
                            cha.addDmgup(-5); // 減少傷害
                        }
                        break;
                    case HASTE://加速術
                    case GREATER_HASTE://強力加速術
                        cha.setMoveSpeed(0);
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPacketsAll(new S_SkillHaste(pc.getId(), 0, 0));
                        }
                        break;
                    case HOLY_WALK://神聖疾走
                    case MOVING_ACCELERATION://行走加速
                    case WIND_WALK://風之疾走
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.hasSkillEffect(STATUS_BRAVE)) {
                                pc.killSkillEffectTimer(STATUS_BRAVE);
                            }
                        }
                        cha.setBraveSpeed(0);
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
                        }
                        break;
                    case ILLUSION_OGRE://幻覺：歐吉
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addDmgup(-4);
                            pc.addHitup(-4);
                        }
                        break;
                    case ILLUSION_DIA_GOLEM://幻覺：鑽石高侖
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addAc(8);
                        }
                        break;
                    case WEAKNESS://弱化術
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addDmgup(5);
                            pc.addHitup(1);
                        }
                        break;
                    case DISEASE: // 疾病術
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;

                            // 注意：不要在這裡調用 removeSkillEffect，因為它已經在被移除的過程中
                            // removeSkillEffect 是由呼叫者（通常是 L1Character.removeSkillEffect）處理的

                            // 恢復效果
                            pc.addDmgup(6); // 恢復傷害加成
                            pc.addAc(-12);  // 恢復 AC 減益

                            // 強制發送 AC 更新封包給客戶端
                            pc.sendPackets(new S_OwnCharAttrDef(pc));
                            pc.sendPackets(new S_OwnCharStatus(pc));

                            // 關閉自訂圖示
                            pc.sendPackets(new S_InventoryIcon(760, false, 4585, 7));
                        }
                        break;
                    //                    case ICE_LANCE://冰矛圍籬
                    case FREEZING_BLIZZARD://寒冰尖刺
                    case FREEZING_BREATH://寒冰噴吐
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPacketsAll(new S_Poison(pc.getId(), 0));
                            pc.sendPackets(new S_Paralysis(4, false));
                        } else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1PetInstance))) {
                            L1NpcInstance npc = (L1NpcInstance) cha;
                            npc.broadcastPacketAll(new S_Poison(npc.getId(), 0));
                            npc.setParalyzed(false);
                        }
                        break;
                    case FOG_OF_SLEEPING://魔法大師
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            // 移除魔法大師狀態
                            pc.setMagicMaster(false);
                        }
                        break;
                    case ABSOLUTE_BARRIER://絕對屏障
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.startHpRegeneration();
                            pc.startMpRegeneration();
                        }
                        break;
                    case SLOW://緩速術
                        //*				case MASS_SLOW://集體緩速術
                    case ENTANGLE://地面障礙
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPacketsAll(new S_SkillHaste(pc.getId(), 0, 0));
                        }
                        cha.setMoveSpeed(0);
                        break;
                    case GUARD_BRAKE://護衛毀滅
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addAc(-10);
                        }
                        break;
                    case HORROR_OF_DEATH://驚悚死神
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addStr(3);
                            pc.addInt(3);
                        }
                        break;
                    case STATUS_CUBE_IGNITION_TO_ALLY://幻術師技能(立方：燃燒)
                        cha.addFire(-30);
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_OwnCharAttrDef(pc));
                        }
                        break;
                    case STATUS_CUBE_QUAKE_TO_ALLY://幻術師技能(立方：地裂)
                        cha.addEarth(-30);
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_OwnCharAttrDef(pc));
                        }
                        break;
                    case STATUS_CUBE_SHOCK_TO_ALLY://幻術師技能(立方：衝擊)
                        cha.addWind(-30);
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_OwnCharAttrDef(pc));
                        }
                        break;
                    case 1019:
                    case 1021:
                    case 1023:
                    case 1024:
                    case 1025:
                        break;
                    case EXP13: // 第一段經驗加倍效果
                    case EXP15: // 第一段經驗加倍效果
                    case EXP17: // 第一段經驗加倍效果
                    case EXP20: // 第一段經驗加倍效果
                    case EXP25: // 第一段經驗加倍效果
                    case EXP30: // 第一段經驗加倍效果
                    case EXP35: // 第一段經驗加倍效果
                    case EXP40: // 第一段經驗加倍效果
                    case EXP45: // 第一段經驗加倍效果
                    case EXP50: // 第一段經驗加倍效果
                    case EXP55: // 第一段經驗加倍效果
                    case EXP60: // 第一段經驗加倍效果
                    case EXP65: // 第一段經驗加倍效果
                    case EXP70: // 第一段經驗加倍效果
                    case EXP75: // 第一段經驗加倍效果
                    case EXP80: // 第一段經驗加倍效果
                    case EXP85: // 第一段經驗加倍效果
                    case EXP90: // 第一段經驗加倍效果
                    case EXP95: // 第一段經驗加倍效果
                    case EXP100: // 第一段經驗加倍效果
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            // 2402 經驗直加倍效果消失！
                            pc.sendPackets(new S_ServerMessage("第一段，經驗加倍效果消失！"));
                            pc.sendPackets(new S_PacketBoxCooking(pc, 32, 0));
                        }
                        break;
                    case SEXP13: // 第二段經驗加倍效果
                    case SEXP30: // 第二段經驗加倍效果
                    case SEXP150: // 第二段經驗加倍效果
                    case SEXP175: // 第二段經驗加倍效果
                    case SEXP200: // 第二段經驗加倍效果
                    case SEXP225: // 第二段經驗加倍效果
                    case SEXP250: // 第二段經驗加倍效果
                    case SEXP300: // 第二段經驗加倍效果
                    case SEXP500: // 第二段經驗加倍效果
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_ServerMessage("第二段經驗加倍效果消失！"));
                        }
                        break;
                    case STATUS_HASTE://加速藥水效果
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;

                            // ✅ 智能移動速度管理：檢查其他加速效果
                            int remainingMoveSpeed = 0;

                            // 檢查加速術
                            if (pc.hasSkillEffect(HASTE)) {
                                remainingMoveSpeed = 1;
                            }
                            // 檢查強力加速術
                            else if (pc.hasSkillEffect(GREATER_HASTE)) {
                                remainingMoveSpeed = 1;
                            }

                            pc.setMoveSpeed(remainingMoveSpeed);
                            pc.sendPacketsAll(new S_SkillHaste(pc.getId(), remainingMoveSpeed, 0));
                        } else {
                            cha.setMoveSpeed(0);
                        }
                        break;
                    case STATUS_RIBRAVE://生命之樹果實效果
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.setBraveSpeed(0);
                            // pc.sendPacketsAll(new S_SkillHaste(pc.getId(), 0, 0));
                            pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
                        } else {
                            cha.setBraveSpeed(0);
                        }
                        break;
                    case STATUS_ELFBRAVE:// 二段精靈餅乾
                    case STATUS_BRAVE2: // 荒神加速
                    case STATUS_BRAVE:// 二段加速物品效果
                    case STATUS_BRAVE3://三段加速
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;

                            // ✅ 智能速度管理：檢查其他速度效果
                            int remainingSpeed = 0;

                            // 檢查三段加速 (最高優先級)
                            if (skillId != STATUS_BRAVE3 && pc.hasSkillEffect(STATUS_BRAVE3)) {
                                remainingSpeed = 5;
                            }
                            // 檢查荒神加速
                            else if (skillId != STATUS_BRAVE2 && pc.hasSkillEffect(STATUS_BRAVE2)) {
                                remainingSpeed = 5;
                            }
                            // 檢查精靈餅乾
                            else if (skillId != STATUS_ELFBRAVE && pc.hasSkillEffect(STATUS_ELFBRAVE)) {
                                remainingSpeed = 1;
                            }
                            // 檢查勇敢藥水
                            else if (skillId != STATUS_BRAVE && pc.hasSkillEffect(STATUS_BRAVE)) {
                                remainingSpeed = 1;
                            }

                            pc.setBraveSpeed(remainingSpeed);
                            pc.sendPacketsAll(new S_Liquor(pc.getId(), remainingSpeed > 0 ? 8 : 0));

                            if (skillId == STATUS_BRAVE3) {
                                pc.sendPackets(new S_PacketBoxThirdSpeed(0));
                            }
                        }
                        break;
                    case STATUS_BLUE_POTION://魔力回覆藥水效果
                        break;
                    case STATUS_UNDERWATER_BREATH://伊娃的祝福藥水效果
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_SkillIconBlessOfEva(pc.getId(), 0));
                        }
                        break;
                    case STATUS_WISDOM_POTION:// 慎重藥水效果
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addSp(-2);
                            pc.addMpr(-2);
                            pc.sendPackets(new S_SPMR(pc));
                            pc.sendPackets(new S_PacketBoxWisdomPotion(0));
                        }
                        break;
                    case STATUS_CHAT_PROHIBITED:// 禁言效果
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_ServerMessage(288));
                        }
                        break;
                    case STATUS_POISON:// 普通毒素效果
                    case STATUS_POISON_SILENCE:// 沉默毒素效果
                        cha.curePoison();
                        break;
                    case 3000:
                    case 3008:
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addWind(-10);
                            pc.addWater(-10);
                            pc.addFire(-10);
                            pc.addEarth(-10);
                            pc.sendPackets(new S_OwnCharAttrDef(pc));
                            pc.sendPackets(new S_PacketBoxCooking(pc, 0, 0));
                            pc.setCookingId(0);
                        }
                        break;
                    case 3001:
                    case 3009:
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addMaxHp(-30);
                            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                            if (pc.isInParty()) {
                                pc.getParty().updateMiniHP(pc);
                            }
                            pc.sendPackets(new S_PacketBoxCooking(pc, 1, 0));
                            pc.setCookingId(0);
                        }
                        break;
                    case 3002:
                    case 3010:
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_PacketBoxCooking(pc, 2, 0));
                            pc.setCookingId(0);
                        }
                        break;
                    case 3003:
                    case 3011:
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addAc(1);
                            pc.sendPackets(new S_PacketBoxCooking(pc, 3, 0));
                            pc.setCookingId(0);
                        }
                        break;
                    case 3004:
                    case 3012:
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addMaxMp(-20);
                            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                            pc.sendPackets(new S_PacketBoxCooking(pc, 4, 0));
                            pc.setCookingId(0);
                        }
                        break;
                    case 3005:
                    case 3013:
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_PacketBoxCooking(pc, 5, 0));
                            pc.setCookingId(0);
                        }
                        break;
                    case 3006:
                    case 3014:
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addMr(-5);
                            pc.sendPackets(new S_SPMR(pc));
                            pc.sendPackets(new S_PacketBoxCooking(pc, 6, 0));
                            pc.setCookingId(0);
                        }
                        break;
                    case 3007:
                    case 3015:
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_PacketBoxCooking(pc, 7, 0));
                            pc.setDessertId(0);
                        }
                        break;
                    case 3016:
                    case 3024:
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_PacketBoxCooking(pc, 16, 0));
                            pc.setCookingId(0);
                        }
                        break;
                    case 3017:
                    case 3025:
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addMaxHp(-30);
                            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                            if (pc.isInParty()) {
                                pc.getParty().updateMiniHP(pc);
                            }
                            pc.addMaxMp(-30);
                            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                            pc.sendPackets(new S_PacketBoxCooking(pc, 17, 0));
                            pc.setCookingId(0);
                        }
                        break;
                    case 3018:
                    case 3026:
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addAc(2);
                            pc.sendPackets(new S_PacketBoxCooking(pc, 18, 0));
                            pc.setCookingId(0);
                        }
                        break;
                    case 3019:
                    case 3027:
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_PacketBoxCooking(pc, 19, 0));
                            pc.setCookingId(0);
                        }
                        break;
                    case 3020:
                    case 3028:
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_PacketBoxCooking(pc, 20, 0));
                            pc.setCookingId(0);
                        }
                        break;
                    case 3021:
                    case 3029:
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addMr(-10);
                            pc.sendPackets(new S_SPMR(pc));
                            pc.sendPackets(new S_PacketBoxCooking(pc, 21, 0));
                            pc.setCookingId(0);
                        }
                        break;
                    case 3022:
                    case 3030:
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addSp(-1);
                            pc.sendPackets(new S_SPMR(pc));
                            pc.sendPackets(new S_PacketBoxCooking(pc, 22, 0));
                            pc.setCookingId(0);
                        }
                        break;
                    case 3023:
                    case 3031:
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_PacketBoxCooking(pc, 32, 0));
                            pc.setDessertId(0);
                        }
                        break;
                    case 3032:
                    case 3040:
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_PacketBoxCooking(pc, 37, 0));
                            pc.setCookingId(0);
                        }
                        break;
                    case 3033:
                    case 3041:
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addMaxHp(-50);
                            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                            if (pc.isInParty()) {
                                pc.getParty().updateMiniHP(pc);
                            }
                            pc.addMaxMp(-50);
                            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                            pc.sendPackets(new S_PacketBoxCooking(pc, 38, 0));
                            pc.setCookingId(0);
                        }
                        break;
                    case 3034:
                    case 3042:
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_PacketBoxCooking(pc, 39, 0));
                            pc.setCookingId(0);
                        }
                        break;
                    case 3035:
                    case 3043:
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addAc(3);
                            pc.sendPackets(new S_PacketBoxCooking(pc, 40, 0));
                            pc.setCookingId(0);
                        }
                        break;
                    case STRIKER_GALE://精準射擊
                        if ((cha instanceof L1PcInstance)) { // src041
                            final L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_PacketBox(S_PacketBox.UPDATE_ER, pc.getEr()));
                        }
                        break;
                    case 3036:
                    case 3044:
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addMr(-15);
                            pc.sendPackets(new S_SPMR(pc));
                            pc.addWind(-10);
                            pc.addWater(-10);
                            pc.addFire(-10);
                            pc.addEarth(-10);
                            pc.sendPackets(new S_OwnCharAttrDef(pc));
                            pc.sendPackets(new S_PacketBoxCooking(pc, 41, 0));
                            pc.setCookingId(0);
                        }
                        break;
                    case 3037:
                    case 3045:
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addSp(-2);
                            pc.sendPackets(new S_SPMR(pc));
                            pc.sendPackets(new S_PacketBoxCooking(pc, 42, 0));
                            pc.setCookingId(0);
                        }
                        break;
                    case 3038:
                    case 3046:
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addMaxHp(-30);
                            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                            if (pc.isInParty()) {
                                pc.getParty().updateMiniHP(pc);
                            }
                            pc.sendPackets(new S_PacketBoxCooking(pc, 43, 0));
                            pc.setCookingId(0);
                        }
                        break;
                    case 3039:
                    case 3047:
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_PacketBoxCooking(pc, 44, 0));
                            pc.setDessertId(0);
                        }
                        break;
                    case COOKING_4_0_N: // 強壯的牛排
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addMr(-10);
                            pc.addEarth(-10);
                            pc.addWater(-10);
                            pc.addFire(-10);
                            pc.addWind(-10);
                            pc.addHpr(-2);
                            pc.addMpr(-2);
                            pc.sendPackets(new S_SPMR(pc));
                            pc.sendPackets(new S_OwnCharAttrDef(pc));
                            pc.sendPackets(new S_PacketBoxCooking(pc, 157, 0));
                            pc.setCookingId(0);
                        }
                        break;
                    case COOKING_4_1_N: // 敏捷的煎鮭魚
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addMr(-10);
                            pc.addEarth(-10);
                            pc.addWater(-10);
                            pc.addFire(-10);
                            pc.addWind(-10);
                            pc.addHpr(-2);
                            pc.addMpr(-2);
                            pc.sendPackets(new S_SPMR(pc));
                            pc.sendPackets(new S_OwnCharAttrDef(pc));
                            pc.sendPackets(new S_PacketBoxCooking(pc, 158, 0));
                            pc.setCookingId(0);
                        }
                        break;
                    case COOKING_4_2_N: // 炭烤的火雞
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addSp(-2);
                            pc.addMr(-10);
                            pc.addEarth(-10);
                            pc.addWater(-10);
                            pc.addFire(-10);
                            pc.addWind(-10);
                            pc.addHpr(-2);
                            pc.addMpr(-3);
                            pc.sendPackets(new S_SPMR(pc));
                            pc.sendPackets(new S_OwnCharAttrDef(pc));
                            pc.sendPackets(new S_PacketBoxCooking(pc, 159, 0));
                            pc.setCookingId(0);
                        }
                        break;
                    case COOKING_4_3_N: // 養生的雞湯
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_PacketBoxCooking(pc, 160, 0));
                            pc.setDessertId(0);
                        }
                        break;
                    case POLYBOOKBUFF:// 神秘的魔法變身書
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addMaxHp(-20);
                            pc.addMaxMp(-20);
                            pc.addDmgup(-3);
                            pc.addBowDmgup(-3);
                            pc.addSp(-3);
                            pc.addDamageReductionByArmor(-2);
                            pc.addHpr(-2);
                            pc.addMpr(-2);
                            pc.sendPackets(new S_HPUpdate(pc));
                            if (pc.isInParty()) {
                                pc.getParty().updateMiniHP(pc);
                            }
                            pc.sendPackets(new S_MPUpdate(pc));
                            pc.sendPackets(new S_SPMR(pc));
                            // System.out.println("刪除buff");
                        }
                        break;
                    case TOP10BUFF:// 排行榜變身
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addMaxHp(-100);
                            pc.addMaxMp(-100);
                            pc.addDmgup(-6);
                            pc.addBowDmgup(-6);
                            pc.addSp(-4);
                            pc.addDamageReductionByArmor(-4);
                            pc.sendPackets(new S_HPUpdate(pc));
                            if (pc.isInParty()) {
                                pc.getParty().updateMiniHP(pc);
                            }
                            pc.sendPackets(new S_MPUpdate(pc));
                            pc.sendPackets(new S_SPMR(pc));
                            // System.out.println("刪除buff");
                        }
                        break;
                    case LV90BUFF:// 90級變身
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addMaxHp(-100);
                            pc.addMaxMp(-100);
                            pc.addHpr(-5);
                            pc.addMpr(-5);
                            pc.addStr(-1);
                            pc.addDex(-1);
                            pc.addCon(-1);
                            pc.addInt(-1);
                            pc.addWis(-1);
                            pc.addCha(-1);
                            pc.addOriginalEr(-5);
                            pc.add_dodge(-1);
                            pc.addDmgup(-5);
                            pc.addBowDmgup(-5);
                            pc.addSp(-3);
                            pc.addAc(-5);
                            pc.addDamageReductionByArmor(-5);
                            pc.sendPackets(new S_OwnCharStatus2(pc));// 能力素質更新
                            pc.sendPackets(new S_PacketBox(S_PacketBox.UPDATE_ER, pc.getEr()));// 迴避率更新
                            pc.sendPackets(new S_PacketBoxIcon1(true, pc.get_dodge()));// 閃避率更新
                            pc.sendPackets(new S_HPUpdate(pc));
                            if (pc.isInParty()) {
                                pc.getParty().updateMiniHP(pc);
                            }
                            pc.sendPackets(new S_MPUpdate(pc));
                            pc.sendPackets(new S_SPMR(pc));
                            // System.out.println("刪除buff");
                        }
                        break;
                    case EFFECT_ENCHANTING_BATTLE:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addHitup(-3);
                            pc.addDmgup(-3);
                            pc.addBowHitup(-3);
                            pc.addBowDmgup(-3);
                            pc.addSp(-3);
                            pc.sendPackets(new S_SPMR(pc));
                        }
                        break;
                    case ONLINE_GIFT:
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            pc.setOnlineGiftWiatEnd(true);
                            T_OnlineGiftTable.get().check(pc);
                        }
                        break;
                    case VIP:
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            pc.endVipStatus();
                        }
                        break;
                    case Pandora_Magic_Stone_1:
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            pc.addMaxHp(-50); // 體力上限-50
                            pc.addDmgup(-2); // 近距離傷害-2
                            pc.addHpr(-3); // 體力回復量-3
                            pc.addStr(-1); // 力量-1
                            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                            if (pc.isInParty()) { // 組隊中更新血條
                                pc.getParty().updateMiniHP(pc);
                            }
                            pc.sendDetails(); // 能力更新
                            pc.sendPackets(new S_OwnCharStatus2(pc)); // 能力更新
                        }
                        break;
                    case Pandora_Magic_Stone_2:
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            pc.addMaxHp(-25); // 體力上限-25
                            pc.addBowDmgup(-2); // 遠距離傷害-2
                            pc.addMaxMp(-25); // 魔力上限-25
                            pc.addHpr(-1); // 體力回復量-1
                            pc.addMpr(-1); // 魔力回復量-1
                            pc.addDex(-1); // 敏捷-1
                            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                            if (pc.isInParty()) { // 組隊中更新血條
                                pc.getParty().updateMiniHP(pc);
                            }
                            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp())); // 魔力更新
                            pc.sendDetails(); // 能力更新
                            pc.sendPackets(new S_OwnCharStatus2(pc)); // 能力更新
                        }
                        break;
                    case Pandora_Magic_Stone_3:
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            pc.addMaxMp(-50); // 魔力上限-50
                            pc.addMpr(-3); // 魔力回復量-3
                            pc.addInt(-1); // 智力-1
                            pc.addSp(-2); // 魔攻-2
                            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp())); // 魔力更新
                            pc.sendDetails(); // 能力更新
                            pc.sendPackets(new S_OwnCharStatus2(pc)); // 能力更新
                            pc.sendPackets(new S_SPMR(pc)); // 魔攻魔防更新
                        }
                        break;
                    case Pandora_Magic_Stone_4:
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            pc.addMaxHp(-30); // 體力上限-30
                            pc.addMaxMp(-30); // 魔力上限-30
                            pc.addAc(5); // 額外防禦+5
                            pc.addMr(-10); // 魔法防禦-10
                            pc.addDamageReductionByArmor(-1); // 傷害減免+1
                            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                            if (pc.isInParty()) { // 組隊中更新血條
                                pc.getParty().updateMiniHP(pc);
                            }
                            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp())); // 魔力更新
                            pc.sendPackets(new S_OwnCharStatus(pc)); // 防禦更新
                            pc.sendPackets(new S_SPMR(pc)); // 魔攻魔防更新
                        }
                        break;
                    case Ancient_Secretn: // 古老秘藥
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            pc.addMaxHp(-100); // 體力上限-100
                            pc.addMaxMp(-100); // 魔力上限-100
                            pc.addHitup(-10); // 近距離命中-10
                            pc.addDmgup(-5); // 近距離傷害-5
                            pc.addBowHitup(-10); // 遠距離命中-10
                            pc.addBowDmgup(-5); // 遠距離傷害-5
                            pc.addSp(-5); // 魔攻-5
                            pc.addAc(10); // 防禦+10
                            pc.addMr(-10); // 魔法防禦-10
                            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                            if (pc.isInParty()) { // 組隊中更新血條
                                pc.getParty().updateMiniHP(pc);
                            }
                            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp())); // 魔力更新
                            pc.sendPackets(new S_OwnCharStatus(pc)); // 防禦更新
                            pc.sendPackets(new S_SPMR(pc)); // 魔攻魔防更新
                            // pc.sendPackets(new S_ServerMessage("古老秘藥效果消失！"));
                        }
                        break;
                    // 成長果實系統(Tam幣)
                    case Tam_Fruit1:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addAc(1);
                            pc.sendPackets(new S_OwnCharStatus(pc));
                            int tamcount = pc.tamcount();
                            if (tamcount > 0) {
                                long tamtime = pc.TamTime();
                                if (tamcount == 1) {
                                    pc.setSkillEffect(Tam_Fruit1, (int) tamtime);
                                    pc.sendPackets(new S_TamWindow(6100, true, 4181, (int) tamtime));
                                    pc.addAc(-1);
                                } else if (tamcount == 2) {
                                    pc.setSkillEffect(Tam_Fruit2, (int) tamtime);
                                    pc.sendPackets(new S_TamWindow(6547, true, 4182, (int) tamtime));
                                    pc.addAc(-2);
                                } else if (tamcount == 3) {
                                    pc.setSkillEffect(Tam_Fruit3, (int) tamtime);
                                    pc.sendPackets(new S_TamWindow(6546, true, 4183, (int) tamtime));
                                    pc.addAc(-3);
                                }
                                pc.sendPackets(new S_OwnCharStatus(pc));
                            }
                        }
                        break;
                    case Tam_Fruit2:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addAc(2);
                            pc.sendPackets(new S_OwnCharStatus(pc));
                            int tamcount = pc.tamcount();
                            if (tamcount > 0) {
                                long tamtime = pc.TamTime();
                                if (tamcount == 1) {
                                    pc.setSkillEffect(Tam_Fruit1, (int) tamtime);
                                    pc.sendPackets(new S_TamWindow(6100, true, 4181, (int) tamtime));
                                    pc.addAc(-1);
                                } else if (tamcount == 2) {
                                    pc.setSkillEffect(Tam_Fruit2, (int) tamtime);
                                    pc.sendPackets(new S_TamWindow(6547, true, 4182, (int) tamtime));
                                    pc.addAc(-2);
                                } else if (tamcount == 3) {
                                    pc.setSkillEffect(Tam_Fruit3, (int) tamtime);
                                    pc.sendPackets(new S_TamWindow(6546, true, 4183, (int) tamtime));
                                    pc.addAc(-3);
                                }
                                pc.sendPackets(new S_OwnCharStatus(pc));
                            }
                        }
                        break;
                    case Tam_Fruit3:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addAc(3);
                            pc.sendPackets(new S_OwnCharStatus(pc));
                            int tamcount = pc.tamcount();
                            if (tamcount > 0) {
                                long tamtime = pc.TamTime();
                                if (tamcount == 1) {
                                    pc.setSkillEffect(Tam_Fruit1, (int) tamtime);
                                    pc.sendPackets(new S_TamWindow(6100, true, 4181, (int) tamtime));
                                    pc.addAc(-1);
                                } else if (tamcount == 2) {
                                    pc.setSkillEffect(Tam_Fruit2, (int) tamtime);
                                    pc.sendPackets(new S_TamWindow(6547, true, 4182, (int) tamtime));
                                    pc.addAc(-2);
                                } else if (tamcount == 3) {
                                    pc.setSkillEffect(Tam_Fruit3, (int) tamtime);
                                    pc.sendPackets(new S_TamWindow(6546, true, 4183, (int) tamtime));
                                    pc.addAc(-3);
                                }
                                pc.sendPackets(new S_OwnCharStatus(pc));
                            }
                        }
                        break;
                    // 成長果實系統(Tam幣)end
				/*case 44301://王者加護 SRC0820
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addMr(-6);
						pc.sendPackets(new S_SPMR(pc));
						pc.setSkillEffect(44304, 3 * 1000);
					}
					break;
				case 44302://王者加護
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addMr(-10);
						pc.addRegistStun(-2);
						pc.sendPackets(new S_SPMR(pc));
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.setSkillEffect(44304, 3 * 1000);
					}
					break;
				case 44303://王者加護
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addMr(-15);
						pc.addRegistStun(-2);
						pc.addRegistSustain(-2);
						pc.sendPackets(new S_SPMR(pc));
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.setSkillEffect(44304, 3 * 1000);
					}
					break;
				case 44304://王者加護
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
					final L1Party party = pc.getParty();
					if (pc.isCrown() && pc.isInParty() &&pc.getParty().isLeader(pc)) { //隊長 隊伍中
					for (final L1PcInstance member : party.getMemberList()) {
						float PartyNumOfMember = pc.getParty().getNumOfMembers();
						if (member.getLocation().isInScreen(pc.getLocation()) && PartyNumOfMember >= 2 &&  PartyNumOfMember <= 4) {
							member.addMr(6);
							//member.setSkillEffect(119, 29 * 1000);
							member.setSkillEffect(44301, 25 * 1000);
							member.sendPackets(new S_SPMR(member));
							member.sendPackets(new S_SkillSound(member.getId(), 9009));
							member.broadcastPacketX10(new S_SkillSound(member.getId(), 9009));
						}else if (member.getLocation().isInScreen(pc.getLocation()) && PartyNumOfMember >= 5 &&  PartyNumOfMember <= 6) {
							member.addMr(10);
							member.addRegistStun(2);
							//member.setSkillEffect(119, 29 * 1000);
							member.setSkillEffect(44302, 25 * 1000);
							member.sendPackets(new S_OwnCharStatus2(member));
							member.sendPackets(new S_SPMR(member));
							member.sendPackets(new S_SkillSound(member.getId(), 9009));
							member.broadcastPacketX10(new S_SkillSound(member.getId(), 9009));
						}else if (member.getLocation().isInScreen(pc.getLocation()) && PartyNumOfMember >= 7 &&  PartyNumOfMember <= 8) {
							member.addMr(15);
							member.addRegistStun(2);
							member.addRegistSustain(2);
							//member.setSkillEffect(119, 29 * 1000);
							member.setSkillEffect(44303, 25 * 1000);
							member.sendPackets(new S_OwnCharStatus2(member));
							member.sendPackets(new S_SPMR(member));
							member.sendPackets(new S_SkillSound(member.getId(), 9009));
							member.broadcastPacketX10(new S_SkillSound(member.getId(), 9009));
							}
						}
					}
					}
					break;*/
                    case FREE_FIGHT_SHOW_INFO: // src015
                        if ((cha instanceof L1PcInstance)) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            if (ConfigFreeKill.FREE_FIGHT_SWITCH) {
                                if (CheckFightTimeController.getInstance().isFightMap(pc.getMapId())) {
                                    pc.sendPackets(new S_BlueMessage(166, "\\f2警告: 此地圖目前為掃街區"));
                                    pc.setSkillEffect(skillId, 1200000);
                                }
                            }
                        }
                        break;
                    case ASTROLOGY_DG_ER:
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.add_dodge_down(-pc.getAstrologyDG());
                            pc.addEr(pc.getAstrologyER());
                            pc.setAstrologyDG(0);
                            pc.setAstrologyER(0);
                            pc.sendPackets(new S_PacketBoxIcon1(false, pc.get_dodge_down()));
                            pc.sendPackets(new S_PacketBoxCharEr(pc));// 角色迴避率更新
                        }
                        break;
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }

        //變身卷軸UI一直跳動的問題
        if ((cha instanceof L1PcInstance)) {
            L1PcInstance pc = (L1PcInstance) cha;
            sendStopMessage(pc, skillId);

            // 只針對「變身buff」這類才刷新UI
            if (UI_REFRESH_SKILL_IDS.contains(skillId)) {
                pc.sendPackets(new S_OwnCharStatus(pc));
            }
        }

    }

    private static void sendStopMessage(L1PcInstance charaPc, int skillid) {
        L1Skills l1skills = SkillsTable.get().getTemplate(skillid);
        if ((l1skills == null) || (charaPc == null)) {
            return;
        }
        int msgID = l1skills.getSysmsgIdStop();
        if (msgID > 0) {
            charaPc.sendPackets(new S_ServerMessage(msgID));
        }
    }

    public static String getDateTime() { // 無參數=傳回現在時間
        Calendar c = Calendar.getInstance();
        return getYMDHMS(c);
    }

    public static String getDateTime(int Y, int M, int D, int H, int m, int S) { // 指定時間
        Calendar c = Calendar.getInstance();
        c.set(Y, --M, D, H, m, S); // 傳進來的實際月份要減 1
        return getYMDHMS(c);
    }

    public static String getYMDHMS(Calendar c) { // 輸出格式製作
        int[] a = {c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND)};
        StringBuilder sb = new StringBuilder();
        sb.append(a[0]);
        if (a[1] < 9) {
            sb.append("-0").append(a[1] + 1);
        } // 加 1 才會得到實際月份
        else {
            sb.append("-").append(a[1] + 1);
        }
        if (a[2] < 10) {
            sb.append("-0").append(a[2]);
        } else {
            sb.append("-").append(a[2]);
        }
        if (a[3] < 10) {
            sb.append(" 0").append(a[3]);
        } else {
            sb.append(" ").append(a[3]);
        }
        if (a[4] < 10) {
            sb.append(":0").append(a[4]);
        } else {
            sb.append(":").append(a[4]);
        }
        if (a[5] < 10) {
            sb.append(":0").append(a[5]);
        } else {
            sb.append(":").append(a[5]);
        }
        return sb.toString();
    }

    private static void setAICheck(L1PcInstance srcpc) {
        if (srcpc == null) {
            return;
        }
        L1ItemInstance item = ItemTable.get().createItem(20000);
        if (srcpc.getInventory().checkItem(20000)) {
            srcpc.getInventory().removeItem(item);
        }
        int count = URandom.nextInt(100) + 1;
        srcpc.getInventory().storeItem(20000, count);
    }

    @SuppressWarnings("unused")
    private static void KickPc(L1PcInstance pc) {
        _client = pc.getNetConnection();
        try {
            TimeUnit.MILLISECONDS.sleep(5000);
            _client.kick();
        } catch (InterruptedException e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
