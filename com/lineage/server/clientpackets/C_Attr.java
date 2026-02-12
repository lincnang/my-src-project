package com.lineage.server.clientpackets;

import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigFreeKill;
import com.lineage.config.ConfigOther;
import com.lineage.config.ConfigTurn;
import com.lineage.data.quest.CrownLv45_1;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.CheckFightTimeController;
import com.lineage.server.datatables.*;
import com.lineage.server.datatables.lock.*;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.Instance.L1EffectInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.*;
import com.lineage.server.model.classes.L1ClassFeature;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.*;
import com.lineage.server.serverpackets.ability.*;
import com.lineage.server.templates.L1House;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1Pet;
import com.lineage.server.templates.L1SystemMessage;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import com.lineage.server.world.WorldWar;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import william.ReincarnationSkill;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

/**
 * 要求點選項目的結果(Y/N)
 *
 * @author dexc
 */
public class C_Attr extends ClientBasePacket {
    private static final Log _log = LogFactory.getLog(C_Attr.class);

    private static void renamePet(final L1PetInstance pet, final String name) {
        if ((pet == null) || (name == null)) {
            throw new NullPointerException();
        }
        final int petItemObjId = pet.getItemObjId();
        final L1Pet petTemplate = PetReading.get().getTemplate(petItemObjId);
        if (petTemplate == null) {
            throw new NullPointerException();
        }
        final L1PcInstance pc = (L1PcInstance) pet.getMaster();
        if (PetReading.get().isNameExists(name)) {
            // 327 同樣的名稱已經存在。
            pc.sendPackets(new S_ServerMessage(327));
            return;
        }
        final L1Npc l1npc = NpcTable.get().getTemplate(pet.getNpcId());
        if (!(pet.getName().equalsIgnoreCase(l1npc.get_name()))) {
            // 326 一旦你已決定就不能再變更。
            pc.sendPackets(new S_ServerMessage(326));
            return;
        }
        pet.setName(name);
        petTemplate.set_name(name);
        PetReading.get().storePet(petTemplate);
        final L1ItemInstance item = pc.getInventory().getItem(pet.getItemObjId());
        pc.getInventory().updateItem(item);
        pc.sendPacketsAll(new S_ChangeName(pet.getId(), name));
    }

    @Override
    public void start(final byte[] decrypt, final ClientExecutor client) {
        try {
            // 資料載入
            this.read(decrypt);
            final L1PcInstance pc = client.getActiveChar();
            if (pc.isGhost()) { // 鬼魂模式
                return;
            }
            if (pc.isTeleport()) { // 傳送中
                return;
            }
            int mode = readH();
            @SuppressWarnings("unused") int tgobjid = 0;
            if (mode == 0) {
                tgobjid = readD();
                mode = readH();
            }
            int c;
            switch (mode) {
			/*case 1210: // 解除同盟? (Y/N)
				if (readC() == 1) {
					if (pc.getClan() != null && pc.getClan().AllianceSize() > 0) {
						for (int clanid : pc.getClan().Alliance()) {
							if (clanid == 0){
								continue;
							}
							clan = WorldClan.get().getClan(clanid);
							if (clan == null){
								continue;
							}
							for (L1PcInstance tempPc : clan.getOnlineClanMember()) {
								tempPc.sendPackets(new S_ServerMessage(1204, pc.getClan().getClanName()));
							}
							clan.removeAlliance(pc.getClanid());
							ClanReading.get().updateClan(clan);
						}
					}
					pc.sendPackets(new S_SystemMessage(pc.getClanname()	+ " 已經離開了同盟."));
					pc.getClan().AllianceDelete();
					ClanReading.get().updateClan(pc.getClan());
				}
				break;
			case 223: // 要求同盟? (Y/N)
				c = readC();
				L1PcInstance allianceLeader = (L1PcInstance) World.get().findObject(pc.getTempID());
				if (allianceLeader == null){
					return;
				}
				pc.setTempID(0);
				int PcClanId = pc.getClanid();
				int TargetClanId = allianceLeader.getClanid();
				String PcClanName = pc.getClanname();
				String TargetClanName = allianceLeader.getClanname();
				L1Clan PcClan = WorldClan.get().getClan(PcClanName);
				L1Clan TargegClan = WorldClan.get().getClan(TargetClanName);
				if (c == 1) { // Yes
					PcClan.addAlliance(TargetClanId);
					// PcClan.setAlliance(TargetClanId);
					// TargegClan.setAlliance(PcClanId);
					TargegClan.addAlliance(PcClanId);
					ClanReading.get().updateClan(PcClan);
					ClanReading.get().updateClan(TargegClan);
					pc.sendPackets(new S_ServerMessage(1200, TargetClanName));
					allianceLeader.sendPackets(new S_ServerMessage(224,	PcClanName, TargetClanName));
				} else if (c == 0) { // No
					pc.sendPackets(new S_SystemMessage("對方血盟拒絕同盟."));
				}
				break;
			case 97: // \f3%0%s 想加入你的血盟。你接受嗎。(Y/N)
				c = this.readC();
				L1PcInstance joinPc = (L1PcInstance) World.get().findObject(pc.getTempID());
				pc.setTempID(0);
				if (joinPc != null) {
					if (c == 0) { // No
						joinPc.sendPackets(new S_ServerMessage(96, pc.getName())); // 拒絕你的請求。
					} else if (c == 1) { // YES
						L1ClanJoin.getInstance().ClanJoin(pc, joinPc);
					}
				}
				break;
			case 1906: // 於血戰中任意退出時會被處以3小時的血盟加入限制。(請求君主同意Y，任意退出N)
				c = this.readC();
				if (c == 0) { // No
					leaveClan(pc, false);
				} else { // Yes
					loginLeader = false;
					clan_name = pc.getClanname();
					clan = WorldClan.get().getClan(clan_name);
					clanMember = clan.getOnlineClanMember();
					for (int i = 0; i < clanMember.length; i++) {
						if (clanMember[i].getClanRank() == L1Clan.CLAN_RANK_LEAGUE_PRINCE
								|| clanMember[i].getClanRank() == L1Clan.CLAN_RANK_LEAGUE_VICEPRINCE
								|| clanMember[i].getClanRank() == L1Clan.CLAN_RANK_PRINCE) {
							// %0%s已申請退出血盟。請問要同意嗎？
							clanMember[i].setTempID(pc.getId()); // 相手ID保存
							clanMember[i].sendPackets(new S_Message_YN(1908, pc.getName()));
							loginLeader = true;
						}
					}
					if (loginLeader) {
						// 跟君主/副君主請求允許退出血盟中，請稍候。
						pc.sendPackets(new S_ServerMessage(1907));
					} else {
						// 君主/副君主為非連線中，請問要任意退出嗎？
						pc.sendPackets(new S_Message_YN(1914));
					}
				}
				break;
			case 1908: // %0%s已申請退出血盟。請問要同意嗎？
				L1PcInstance leavePc = (L1PcInstance) World.get().findObject(pc.getTempID());
				c = this.readC();
				if (c == 0) { // No
					clan_name = pc.getClanname();
					clan = WorldClan.get().getClan(clan_name);
					clanMember = clan.getOnlineClanMember();
					for (int i = 0; i < clanMember.length; i++) {
						// %0君主(副君主)已拒絕%1退出血盟。
						clanMember[i].sendPackets(new S_ServerMessage(1917, pc.getName(), leavePc.getName()));
					}
					// 君主已拒絕您退出血盟，請問要任意退出嗎？
					leavePc.sendPackets(new S_Message_YN(1912));
				} else { // Yes
					clan_name = pc.getClanname();
					clan = WorldClan.get().getClan(clan_name);
					clanMember = clan.getOnlineClanMember();
					for (int i = 0; i < clanMember.length; i++) {
						// %0君主(副君主)已同意%1退出血盟。
						clanMember[i].sendPackets(new S_ServerMessage(1916, pc.getName(), leavePc.getName()));
					}
					leaveClan(leavePc, true);
				}
				break;
			case 1912: // 君主已拒絕您退出血盟，請問要任意退出嗎？
			case 1914: // 君主/副君主為非連線中，請問要任意退出嗎？
				c = this.readC();
				if (c == 0) { // No
					return;
				}
				leaveClan(pc, false);
				break;
								// 同盟系統 by terry0412
				case 223: // %0 血盟要與你同盟。是否接受？(Y/N)
					final L1PcInstance alliancePc = (L1PcInstance) World.get().findObject(pc.getTempID());
					pc.setTempID(0);
					if (alliancePc == null) {
						return;
					}
					c = readC();
					if (c == 1) {
						final L1Clan clan = pc.getClan();
						if ((clan == null) || (pc.getId() != clan.getLeaderId())) {
							// 血盟君主才可使用此命令。
							pc.sendPackets(new S_ServerMessage(518));
							return;
						}
						final L1Clan target_clan = alliancePc.getClan();
						if (target_clan == null) {
							return;
						}
						L1Alliance alliance = ClanAllianceReading.get().getAlliance(clan.getClanId());
						if (alliance == null) {
							alliance = new L1Alliance(clan.getClanId(), clan, target_clan);
							ClanAllianceReading.get().insertAlliance(alliance);
						} else {
							if (!alliance.checkSize()) {
								alliancePc.sendPackets(new S_ServerMessage(1201));
								return;
							}
							alliance.addAlliance(target_clan);
							ClanAllianceReading.get().updateAlliance(alliance.getOrderId(),
									alliance.getTotalList());
						}
						World.get().broadcastPacketToAll(
								new S_ServerMessage(224, clan.getClanName(), target_clan.getClanName()));
						alliance.sendPacketsAll("", new S_ServerMessage(1200, target_clan.getClanName()));
					} else if (c == 0) {
						alliancePc.sendPackets(new S_ServerMessage(1198));
					}
					break;
				case 1210: // 確定要退出同盟嗎? (Y/N)
					if (readC() == 1) {
						final L1Clan clan = pc.getClan();
						if ((clan == null) || (pc.getId() != clan.getLeaderId())) {
							// 血盟君主才可使用此命令。
							pc.sendPackets(new S_ServerMessage(518));
							return;
						}
						final L1Alliance alliance = ClanAllianceReading.get().getAlliance(clan.getClanId());
						if (alliance == null) {
							return;
						}
						for (final L1Clan l1clan : alliance.getTotalList()) {
							if (l1clan.getClanId() == clan.getClanId()) {
								alliance.getTotalList().remove(l1clan);
								break;
							}
						}
						if (alliance.getTotalList().size() < 2) {
							ClanAllianceReading.get().deleteAlliance(alliance.getOrderId());
						} else {
							ClanAllianceReading.get().updateAlliance(alliance.getOrderId(),
									alliance.getTotalList());
						}
						World.get().broadcastPacketToAll(new S_ServerMessage(225,
								alliance.getTotalList().get(0).getClanName(), clan.getClanName()));
						alliance.sendPacketsAll("", new S_ServerMessage(1204, clan.getClanName()));
					}
					break;*/
                case 180: // 變形術變身
                    readC();
                    String polys = readS();
                    L1PolyMorph.handleCommands(pc, polys);
                    break;
            /*case 622:
                c = readC();
                if (pc.isSiege) {
                    switch (c) {
                        case 0: //拒絶
                            break;
                        case 1:
                            if (MiniSiege.getInstance().running && MiniSiege.getInstance().getStage() == 0) {
                                if (pc.isInParty()) {
                                	pc.getParty().leaveMember(pc);
                                }
                                MiniSiege.getInstance().EnterTeam(pc);
                            } else {
                                pc.sendPackets(new S_ServerMessage("\\fT遊戲報名時間已過，下次請及時。"));
                            }
                            break;
                    }
                }
                break;*/
                // 7.6
                case 97: // \f3%0%s 想加入你的血盟。你接受嗎。(Y/N)
                    c = readC();
                    final L1PcInstance joinPc = (L1PcInstance) World.get().findObject(pc.getTempID());
                    pc.setTempID(0);
                    if (joinPc != null) {
                        if (c == 0) { // No
                            // 96 \f1%0%s 拒絕你的請求
                            joinPc.sendPackets(new S_ServerMessage(96, pc.getName()));
                        } else if (c == 1) { // Yes
                            final int clan_id = pc.getClanid();
                            final String clanName = pc.getClanname();
                            final L1Clan clan = WorldClan.get().getClan(clanName);
                            if (clan != null) {
                                int maxMember = 0;
                                final int charisma = pc.getCha();
                                boolean lv45quest = false;
                                if (pc.getQuest().isEnd(CrownLv45_1.QUEST.get_id())) {
                                    lv45quest = true;
                                }
                                if (pc.getLevel() >= 50) { // Lv50以上
                                    if (lv45quest == true) { // Lv45濟
                                        maxMember = charisma * 9;
                                    } else {
                                        maxMember = charisma * 3;
                                    }
                                } else { // Lv50未滿
                                    if (lv45quest == true) { // Lv45濟
                                        maxMember = charisma * 6;
                                    } else {
                                        maxMember = charisma * 2;
                                    }
                                }
                                if (ConfigOther.CLANCOUNT != 0) {
                                    maxMember = ConfigOther.CLANCOUNT;
                                }
                                if (joinPc.getClanid() == 0) { // 未加入
                                    final String clanMembersName[] = clan.getAllMembers();
                                    if (maxMember <= clanMembersName.length) {
                                        // 188 %0%s 無法接受你成為該血盟成員。
                                        joinPc.sendPackets(new S_ServerMessage(188, pc.getName()));
                                        return;
                                    }
                                    for (final L1PcInstance clanMembers : clan.getOnlineClanMember()) {
                                        // 94 \f1你接受%0當你的血盟成員。
                                        clanMembers.sendPackets(new S_ServerMessage(94, joinPc.getName()));
                                    }
                                    joinPc.setClanid(clan_id);
                                    joinPc.setClanname(clanName);
                                    joinPc.setClanRank(L1Clan.CLAN_RANK_PROBATION);
                                    joinPc.save();
                                    // 新入盟成員發送更新血盟數據
                                    joinPc.sendPackets(new S_ClanUpdate(joinPc.getId(), joinPc.getClanname(), joinPc.getClanRank()));
                                    clan.addMemberName(joinPc.getName());
                                    ClanMembersTable.getInstance().newMember(joinPc);
                                    // 在線上的血盟成員發送新加入成員血盟數據
                                    for (final L1PcInstance clanMembers : clan.getOnlineClanMember()) {
                                        clanMembers.sendPackets(new S_ClanUpdate(joinPc.getId(), joinPc.getClanname(), joinPc.getClanRank()));
                                    }
                                    //SRC0822 修改創盟立即更新
                                    joinPc.sendPackets(new S_PacketBox(S_PacketBox.PLEDGE_EMBLEM_STATUS, joinPc.getClan().getShowEmblem()));
                                    joinPc.sendPackets(new S_PledgeName(joinPc.getClanname(), joinPc.getClanRank()));
                                    joinPc.sendPackets(new S_ServerMessage("\\fT加入血盟，請重新登入進行更新血盟。"));
                                    // 95 \f1加入%0血盟。
                                    joinPc.sendPackets(new S_ServerMessage(95, clanName));
                                    // 王族發送加入血盟更新列表
                                    pc.sendPackets(new S_PacketBoxPledge(3, null, joinPc.getName(), joinPc.getClanRank()));
                                } else { // 加入濟（連合）
                                    if (ConfigAlt.CLAN_ALLIANCE) {
                                        // 同盟系統 by terry0412
                                        final L1Clan clan2 = joinPc.getClan();
                                        if (clan2 != null) {
                                            // 取得指定同盟資料
                                            if (ClanAllianceReading.get().getAlliance(clan2.getClanId()) != null) {
                                                // 同盟中無法加入聯合血盟.
                                                joinPc.sendPackets(new S_ServerMessage(2063));
                                                return;
                                            }
                                        }
                                        changeClan(client, pc, joinPc, maxMember);
                                    } else {
                                        // 89 \f1你已經有血盟了。
                                        joinPc.sendPackets(new S_ServerMessage(89));
                                    }
                                }
                            }
                        }
                    }
                    break;
                case 217: // %0 血盟向你的血盟宣戰。是否接受？(Y/N)
                case 221: // %0 血盟要向你投降。是否接受？(Y/N)
                case 222: // %0 血盟要結束戰爭。是否接受？(Y/N)
                    c = this.readC();
                    // 宣戰者
                    final L1PcInstance enemyLeader = (L1PcInstance) World.get().findObject(pc.getTempID());
                    if (enemyLeader == null) {
                        return;
                    }
                    pc.setTempID(0);
                    final String clanName = pc.getClanname();
                    final String enemyClanName = enemyLeader.getClanname();// 宣戰盟
                    if (c == 0) { // No
                        if (mode == 217) {
                            // 236 %0 血盟拒絕你的宣戰。
                            enemyLeader.sendPackets(new S_ServerMessage(236, clanName));
                        } else if ((mode == 221) || (mode == 222)) {
                            // 237 %0 血盟拒絕你的提案。
                            enemyLeader.sendPackets(new S_ServerMessage(237, clanName));
                        }
                    } else if (c == 1) { // Yes
                        if (mode == 217) {
                            final L1War war = new L1War();
                            war.handleCommands(2, enemyClanName, clanName); // 模擬戰開始
                        } else if ((mode == 221) || (mode == 222)) {
                            // 取回全部戰爭清單
                            for (final L1War war : WorldWar.get().getWarList()) {
                                if (war.checkClanInWar(clanName)) { // 戰爭中
                                    if (mode == 221) {
                                        war.surrenderWar(enemyClanName, clanName); // 投降
                                    } else if (mode == 222) {
                                        war.ceaseWar(enemyClanName, clanName); // 結束
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    break;
                case 252: // \f2%0%s 要與你交易。願不願交易？ (Y/N)
                    c = this.readC();
                    final L1PcInstance trading_partner = (L1PcInstance) World.get().findObject(pc.getTradeID());
                    if (trading_partner != null) {
                        if (c == 0) {// No
                            // 253 %0%d 拒絕與你交易。
                            trading_partner.sendPackets(new S_ServerMessage(253, pc.getName()));
                            pc.setTradeID(0);
                            trading_partner.setTradeID(0);
                        } else if (c == 1) {// Yes
                            pc.sendPackets(new S_Trade(trading_partner.getName()));
                            trading_partner.sendPackets(new S_Trade(pc.getName()));
                        }
                    }
                    break;
                case 321: // 321 是否要復活？ (Y/N)
                    c = this.readC();
                    final L1PcInstance resusepc1 = (L1PcInstance) World.get().findObject(pc.getTempID());
                    pc.setTempID(0);
                    if (resusepc1 != null) { // 復活
                        if (c == 0) { // No
                            ;
                        } else if (c == 1) { // Yes
                            // 刪除人物墓碑
                            L1EffectInstance tomb = pc.get_tomb();
                            if (tomb != null) {
                                tomb.broadcastPacketAll(new S_DoActionGFX(tomb.getId(), 8));
                                tomb.deleteMe();
                            }
                            pc.sendPacketsX8(new S_SkillSound(pc.getId(), '\346'));
                            // pc.resurrect(pc.getLevel());
                            // pc.setCurrentHp(pc.getLevel());
                            pc.resurrect(pc.getMaxHp() / 2);
                            pc.setCurrentHp(pc.getMaxHp() / 2);
                            pc.startHpRegeneration();
                            pc.startMpRegeneration();
                            pc.stopPcDeleteTimer();
                            pc.sendPacketsAll(new S_Resurrection(pc, resusepc1, 0));
                            pc.sendPacketsAll(new S_CharVisualUpdate(pc));
                            if (ConfigFreeKill.FREE_FIGHT_SWITCH)//src015
                            {
                                if (CheckFightTimeController.getInstance().isFightMap(pc.getMapId())) {
                                    pc.broadcastPacketAll(new S_PinkName(pc.getId(), -1));
                                }
                            }
                        }
                    }
                    break;
                case 322: // 322 是否要復活？ (Y/N)
                    c = this.readC();
                    final L1PcInstance resusepc2 = (L1PcInstance) World.get().findObject(pc.getTempID());
                    pc.setTempID(0);
                    if (resusepc2 != null) { // 祝福 復活、、 
                        if (c == 0) { // No
                            ;
                        } else if (c == 1) { // Yes
                            // 刪除人物墓碑
                            L1EffectInstance tomb = pc.get_tomb();
                            if (tomb != null) {
                                tomb.broadcastPacketAll(new S_DoActionGFX(tomb.getId(), 8));
                                tomb.deleteMe();
                            }
                            pc.sendPacketsX8(new S_SkillSound(pc.getId(), '\346'));
                            pc.resurrect(pc.getMaxHp());
                            pc.setCurrentHp(pc.getMaxHp());
                            pc.startHpRegeneration();
                            pc.startMpRegeneration();
                            pc.stopPcDeleteTimer();
                            pc.sendPacketsAll(new S_Resurrection(pc, resusepc2, 0));
                            pc.sendPacketsAll(new S_CharVisualUpdate(pc));
                            // EXP、G-RES掛、EXP死亡
                            // 全滿場合EXP復舊
                            if ((pc.getExpRes() == 1) && pc.isGres() && pc.isGresValid()) {
                                pc.resExp();
                                pc.setExpRes(0);
                                pc.setGres(false);
                            }
                            if (ConfigFreeKill.FREE_FIGHT_SWITCH) {
                                if (CheckFightTimeController.getInstance().isFightMap(pc.getMapId())) {
                                    pc.broadcastPacketAll(new S_PinkName(pc.getId(), -1));
                                }
                            }
                        }
                    }
                    break;
                case 325: // 你想叫它什麼名字？
                    c = readC();
                    String petName = readS();
                    if (pc.is_rname()) {
                        String name = Matcher.quoteReplacement(petName);
                        name = name.replaceAll("\\s", "");
                        name = name.replaceAll("　", "");
                        name = name.substring(0, 1).toUpperCase() + name.substring(1);
                        for (String ban : C_CreateChar.BANLIST) {
                            if (name.contains(ban)) {
                                name = "";
                                break;
                            }
                        }
                        if (name.length() == 0) {
                            // 53 無效的名字。 若您擅自修改，將無法進行遊戲。
                            pc.sendPackets(new S_ServerMessage(53));
                            return;
                        }
                        // 名稱是否包含禁止字元
                        if (!C_CreateChar.isInvalidName(name)) {
                            // 53 無效的名字。 若您擅自修改，將無法進行遊戲。
                            pc.sendPackets(new S_ServerMessage(53));
                            return;
                        }
                        // 檢查名稱是否以被使用
                        if (CharObjidTable.get().charObjid(name) != 0) {
                            // 58 已經有同樣的角色名稱。請重新輸入。
                            pc.sendPackets(new S_ServerMessage(58));
                            return;
                        }
                        // String srcname = pc.getName();
                        World.get().removeObject(pc);// 移出世界
                        pc.getInventory().consumeItem(41227, 1);
                        RecordTable.get().recordPcChangeName(pc.getId(), pc.getAccountName(), pc.getName(), name, pc.getIp());
                        pc.setName(name);
                        CharObjidTable.get().reChar(pc.getId(), name);
                        CharacterTable.get().newCharName(pc.getId(), name);
                        World.get().storeObject(pc);// 重新加入世界
                        // 改變顯示(復原正常)
                        pc.sendPacketsAll(new S_ChangeName(pc, true));
                        pc.sendPackets(new S_ServerMessage(166, "由於人物名稱異動!請重新登入遊戲!將於5秒後強制斷線!"));
                        final KickPc kickPc = new KickPc(pc);
                        kickPc.start_cmd();
                    } else {
                        final L1PetInstance pet = (L1PetInstance) World.get().findObject(pc.getTempID());
                        pc.setTempID(0);
                        renamePet(pet, petName);
                    }
                    pc.rename(false);
                    break;
                case 512: // 請輸入血盟小屋名稱?
                    c = this.readC(); // ?
                    String houseName = this.readS();
                    final int houseId = pc.getTempID();
                    pc.setTempID(0);
                    if (houseName.length() <= 16) {
                        final L1House house = HouseReading.get().getHouseTable(houseId);
                        house.setHouseName(houseName);
                        HouseReading.get().updateHouse(house); // DB書迂
                    } else {
                        pc.sendPackets(new S_ServerMessage(513)); // 血盟小屋名稱太長。
                    }
                    break;
                case 630: // %0%s 要與你決鬥。你是否同意？(Y/N)
                    c = this.readC();
                    final L1PcInstance fightPc = (L1PcInstance) World.get().findObject(pc.getFightId());
                    if (c == 0) {
                        pc.setFightId(0);
                        fightPc.setFightId(0);
                        // 631 %0%d 拒絕了與你的決鬥。
                        fightPc.sendPackets(new S_ServerMessage(631, pc.getName()));
                    } else if (c == 1) {
                        fightPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, fightPc.getFightId(), fightPc.getId()));
                        pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, pc.getFightId(), pc.getId()));
                    }
                    break;
                case 653: // 若你離婚，你的結婚戒指將會消失。你決定要離婚嗎？(Y/N)
                    c = this.readC();
                    final L1PcInstance target653 = (L1PcInstance) World.get().findObject(pc.getPartnerId());
                    if (c == 0) { // No
                        return;
                    } else if (c == 1) { // Yes
                        if (target653 != null) {
                            target653.getQuest().set_step(L1PcQuest.QUEST_MARRY, 0);
                            target653.setPartnerId(0); // 設定結婚戒指編號為 0
                            target653.save();
                            target653.sendPackets(new S_ServerMessage(662)); // \f1你(你)目前未婚。
                        } else {
                            CharacterTable.get();
                            CharacterTable.updatePartnerId(pc.getPartnerId());
                            //CharacterQuestReading.get().updateQuest(pc.getPartnerId(), L1PcQuest.QUEST_MARRY, 0);
                        }
                    }
                    /** [原碼] 結婚系統 */
                    // 刪除結婚戒指(與結婚任務進度相同的那顆戒指)
                    for (L1ItemInstance item : pc.getInventory().getItems()) {
                        if ((item.getItemId() >= 40901 && item.getItemId() <= 40908 && item.getId() == pc.getQuest().get_step(L1PcQuest.QUEST_MARRY))) {
                            pc.getInventory().removeItem(item);
                        }
                    }
                    /** end */
                    pc.getQuest().set_step(L1PcQuest.QUEST_MARRY, 0);// 設定結婚任務進度為0
                    pc.setPartnerId(0);
                    pc.save(); // 將玩家資料儲存到資料庫中
                    pc.sendPackets(new S_ServerMessage(662)); // \f1你(你)目前未婚。
                    break;
                case 654: // %0 向你(你)求婚，你(你)答應嗎?
                    c = this.readC();
                    final L1PcInstance partner = (L1PcInstance) World.get().findObject(pc.getTempID());
                    pc.setTempID(0);
                    if (partner != null) {
                        if (c == 0) { // No
                            // 656 %0 拒絕你(你)的求婚。
                            partner.sendPackets(new S_ServerMessage(656, pc.getName()));
                        } else if (c == 1) { // Yes
                            /** [原碼] 結婚系統 */
                            // 寫入任務進度為結婚戒指編號
                            for (L1ItemInstance item : pc.getInventory().getItems()) {
                                if (item.getItemId() >= 40901 && item.getItemId() <= 40908) {
                                    pc.getQuest().set_step(L1PcQuest.QUEST_MARRY, item.getId());
                                }
                            }
                            /** end */
                            pc.setPartnerId(partner.getId());
                            pc.save();
                            pc.sendPackets(new S_ServerMessage(790)); // 倆人的結婚在所有人的祝福下完成
                            pc.sendPackets(new S_ServerMessage(655, partner.getName())); // 恭喜!!
                            // %0
                            // 已接受你(你)的求婚。
                            /** [原碼] 結婚系統 */
                            // 寫入任務進度為結婚戒指編號
                            for (L1ItemInstance item : partner.getInventory().getItems()) {
                                if (item.getItemId() >= 40901 && item.getItemId() <= 40908) {
                                    partner.getQuest().set_step(L1PcQuest.QUEST_MARRY, item.getId());
                                }
                            }
                            /** end */
                            partner.setPartnerId(pc.getId());
                            partner.save();
                            partner.sendPackets(new S_ServerMessage(790));// 倆人的結婚在所有人的祝福下完成
                            partner.sendPackets(new S_ServerMessage(655, pc.getName())); // 恭喜!!
                            // %0
                            // 已接受你(你)的求婚。
                            /** [原碼] 結婚公告 */
                            for (L1PcInstance allpc : World.get().getAllPlayers()) {
                                if (allpc != null) {
                                    allpc.sendPackets(new S_SystemMessage("恭喜 " + pc.getName() + " 與 " + partner.getName() + " 結為夫妻。"));
                                    allpc.sendPackets(new S_SystemMessage("讓我們一起為這對新人送上最真摯的祝福。"));
                                }
                            }
                        }
                    }
                    break;
                case 729: // 盟主正在呼喚你，你要接受他的呼喚嗎？(Y/N)
                    c = this.readC();
                    if (c == 0) { // No
                        ;
                    } else if (c == 1) { // Yes
                        this.callClan(pc);
                    }
                    break;
                case 738: // 恢復經驗值需消耗%0金幣。想要恢復經驗值嗎?
                    c = this.readC();
                    if (c == 0) { // No
                        ;
                    } else if ((c == 1) && (pc.getExpRes() == 1)) { // Yes
                        int cost = 0;
                        final int level = pc.getLevel();
                        final int lawful = pc.getLawful();
                        if (level < 45) {
                            cost = level * level * 100;
                        } else {
                            cost = level * level * 200;
                        }
                        if (lawful >= 0) {
                            cost = (cost / 2);
                        }
                        if (pc.getInventory().consumeItem(L1ItemId.ADENA, cost)) {
                            pc.resExp();
                            pc.setExpRes(0);
                        } else {
                            // 189 \f1金幣不足。
                            pc.sendPackets(new S_ServerMessage(189));
                        }
                    }
                    break;
                case 748: // 你的血盟成員想要傳送你。你答應嗎？(Y/N)
                    c = this.readC();
                    if (c == 0) { // No
                        ;
                    } else if (c == 1) { // Yes
                        final int newX = pc.getTeleportX();
                        final int newY = pc.getTeleportY();
                        final short mapId = pc.getTeleportMapId();
                        L1Teleport.teleport(pc, newX, newY, mapId, 5, true);
                    }
                    break;
                case 951: // 您要接受玩家 %0%s 提出的隊伍對話邀請嗎？(Y/N)
                    c = this.readC();
                    final L1PcInstance chatPc = (L1PcInstance) World.get().findObject(pc.getPartyID());
                    if (chatPc != null) {
                        if (c == 0) { // No
                            // 423 %0%s 拒絕了您的邀請。
                            chatPc.sendPackets(new S_ServerMessage(423, pc.getName()));
                            pc.setPartyID(0);
                        } else if (c == 1) { // Yes
                            if (chatPc.isInChatParty()) {
                                if (chatPc.getChatParty().isVacancy() || chatPc.isGm()) {
                                    chatPc.getChatParty().addMember(pc);
                                } else {
                                    // 417 你的隊伍已經滿了，無法再接受隊員。
                                    chatPc.sendPackets(new S_ServerMessage(417));
                                }
                            } else {
                                final L1ChatParty chatParty = new L1ChatParty();
                                chatParty.addMember(chatPc);
                                chatParty.addMember(pc);
                                // 424 %0%s 加入了您的隊伍。
                                chatPc.sendPackets(new S_ServerMessage(424, pc.getName()));
                            }
                        }
                    }
                    break;
                case 953: // 玩家 %0%s 邀請您加入隊伍？(Y/N)
                    c = this.readC();
                    final L1PcInstance target = (L1PcInstance) World.get().findObject(pc.getPartyID());
                    if (target != null) {
                        if (c == 0) {// No
                            // 423 %0%s 拒絕了您的邀請。
                            target.sendPackets(new S_ServerMessage(423, pc.getName()));
                            pc.setPartyID(0);
                        } else if (c == 1) {// Yes
                            if (target.isInParty()) {
                                // 邀請加入者 已成立隊伍
                                if (target.getParty().isVacancy()) {
                                    // 加入新的隊伍成員
                                    target.getParty().addMember(pc);
                                    // 師徒系統
                                    //L1Master.getInstance().joinParty(pc);
                                } else {
                                    // 417：你的隊伍已經滿了，無法再接受隊員。
                                    target.sendPackets(new S_ServerMessage(417));
                                }
                            } else {
                                // 邀請加入者 尚未成立隊伍
                                final L1Party party = new L1Party();
                                party.addMember(target);// 第一個加入隊伍者將為隊長
                                party.addMember(pc);
                                // 424：%0%s 加入了您的隊伍。
                                target.sendPackets(new S_ServerMessage(424, pc.getName()));
                                // 師徒系統
                                //L1Master.getInstance().creatParty(target);
                                //L1Master.getInstance().creatParty(pc);
                            }
                        }
                    }
                    break;
			/*case 479: // 你想提升那一種屬性?
				c = this.readC();
				if (c == 1) {
					final String s = this.readS();
					if (!(pc.getLevel() - 50 > pc.getBonusStats())) {
						return;
					}
					if (s.equalsIgnoreCase("str")) {
						if (pc.getBaseStr() < ConfigAlt.POWER) {
							pc.addBaseStr((byte) 1); // 素STR值+1
							pc.setBonusStats(pc.getBonusStats() + 1);
							pc.sendPackets(new S_OwnCharStatus2(pc));
							pc.sendPackets(new S_CharVisualUpdate(pc));
							pc.save(); // 人物資料記錄
						} else {
							// 481 \f1屬性最大值只能到35。 請重試一次。
							pc.sendPackets(new S_ServerMessage(481));
						}
					} else if (s.equalsIgnoreCase("dex")) {
						if (pc.getBaseDex() < ConfigAlt.POWER) {
							pc.addBaseDex((byte) 1); // 素DEX值+1
							pc.resetBaseAc();
							pc.setBonusStats(pc.getBonusStats() + 1);
							pc.sendPackets(new S_OwnCharStatus2(pc));
							pc.sendPackets(new S_CharVisualUpdate(pc));
							pc.save(); // 人物資料記錄
						} else {
							// 481 \f1屬性最大值只能到35。 請重試一次。
							pc.sendPackets(new S_ServerMessage(481));
						}
					} else if (s.equalsIgnoreCase("con")) {
						if (pc.getBaseCon() < ConfigAlt.POWER) {
							pc.addBaseCon((byte) 1); // 素CON值+1
							pc.setBonusStats(pc.getBonusStats() + 1);
							pc.sendPackets(new S_OwnCharStatus2(pc));
							pc.sendPackets(new S_CharVisualUpdate(pc));
							pc.save(); // 人物資料記錄
						} else {
							pc.sendPackets(new S_ServerMessage(481));
						}
					} else if (s.equalsIgnoreCase("int")) {
						if (pc.getBaseInt() < ConfigAlt.POWER) {
							pc.addBaseInt((byte) 1); // 素INT值+1
							pc.setBonusStats(pc.getBonusStats() + 1);
							pc.sendPackets(new S_OwnCharStatus2(pc));
							pc.sendPackets(new S_CharVisualUpdate(pc));
							pc.save(); // 人物資料記錄
						} else {
							// 481 \f1屬性最大值只能到35。 請重試一次。
							pc.sendPackets(new S_ServerMessage(481));
						}
					} else if (s.equalsIgnoreCase("wis")) {
						if (pc.getBaseWis() < ConfigAlt.POWER) {
							pc.addBaseWis((byte) 1); // 素WIS值+1
							pc.resetBaseMr();
							pc.setBonusStats(pc.getBonusStats() + 1);
							pc.sendPackets(new S_OwnCharStatus2(pc));
							pc.sendPackets(new S_CharVisualUpdate(pc));
							pc.save(); // 人物資料記錄
						} else {
							// 481 \f1屬性最大值只能到35。 請重試一次。
							pc.sendPackets(new S_ServerMessage(481));
						}
					} else if (s.equalsIgnoreCase("cha")) {
						if (pc.getBaseCha() < ConfigAlt.POWER) {
							pc.addBaseCha((byte) 1); // 素CHA值+1
							pc.setBonusStats(pc.getBonusStats() + 1);
							pc.sendPackets(new S_OwnCharStatus2(pc));
							pc.sendPackets(new S_CharVisualUpdate(pc));
							pc.save(); // 人物資料記錄
						} else {
							// 481 \f1屬性最大值只能到35。 請重試一次。
							pc.sendPackets(new S_ServerMessage(481));
						}
					}
				}
				break;*/
                // 7.6
                case 479: // 你想提升那一種屬性?
                    c = readC();
                    if (c == 1) {
                        final String s = readS();
                        if (!((pc.getLevel() - 50) > pc.getBonusStats())) {
                            return;
                        }
                        if (s.equalsIgnoreCase("str")) {
                            if (pc.getBaseStr() < ConfigAlt.POWER) {
                                pc.addBaseStr((byte) 1); // 素STR值+1
                                pc.setBonusStats(pc.getBonusStats() + 1);
                                // XXX 能力基本資訊-力量
                                pc.sendPackets(new S_StrDetails(2, L1ClassFeature.calcStrDmg(pc.getStr(), pc.getBaseStr()), L1ClassFeature.calcStrHit(pc.getStr(), pc.getBaseStr()), L1ClassFeature.calcStrDmgCritical(pc.getStr(), pc.getBaseStr()), L1ClassFeature.calcAbilityMaxWeight(pc.getStr(), pc.getCon())));
                                // XXX 重量程度資訊
                                pc.sendPackets(new S_WeightStatus(pc.getInventory().getWeight() * 100 / (int) pc.getMaxWeight(), pc.getInventory().getWeight(), (int) pc.getMaxWeight()));
                                // XXX 純能力資訊
                                pc.sendPackets(new S_BaseAbility(pc.getBaseStr(), pc.getBaseInt(), pc.getBaseWis(), pc.getBaseDex(), pc.getBaseCon(), pc.getBaseCha()));
                                pc.sendPackets(new S_OwnCharStatus(pc));
                                ;
                                pc.sendPackets(new S_OwnCharStatus2(pc));
                                pc.sendPackets(new S_CharVisualUpdate(pc));
                                pc.save(); // 人物資料記錄
                            } else {
                                // 481 \f1屬性最大值只能到35。 請重試一次。
                                pc.sendPackets(new S_ServerMessage("\\aH屬性最大值只能到25 ，請重試一次。"));
                            }
                        } else if (s.equalsIgnoreCase("dex")) {
                            if (pc.getBaseDex() < ConfigAlt.POWER) {
                                pc.addBaseDex((byte) 1); // 素DEX值+1
                                // XXX 7.6 ADD
                                pc.sendPackets(new S_PacketBoxCharEr(pc));// 角色迴避率更新
                                pc.resetBaseAc();
                                pc.setBonusStats(pc.getBonusStats() + 1);
                                // XXX 能力基本資訊-敏捷
                                pc.sendPackets(new S_DexDetails(2, L1ClassFeature.calcDexDmg(pc.getDex(), pc.getBaseDex()), L1ClassFeature.calcDexHit(pc.getDex(), pc.getBaseDex()), L1ClassFeature.calcDexDmgCritical(pc.getDex(), pc.getBaseDex()), L1ClassFeature.calcDexAc(pc.getDex()), L1ClassFeature.calcDexEr(pc.getDex())));
                                // XXX 純能力資訊
                                pc.sendPackets(new S_BaseAbility(pc.getBaseStr(), pc.getBaseInt(), pc.getBaseWis(), pc.getBaseDex(), pc.getBaseCon(), pc.getBaseCha()));
                                pc.sendPackets(new S_OwnCharStatus(pc));
                                pc.sendPackets(new S_OwnCharStatus2(pc));
                                pc.sendPackets(new S_CharVisualUpdate(pc));
                                pc.save(); // 人物資料記錄
                            } else {
                                // 481 \f1屬性最大值只能到35。 請重試一次。
                                pc.sendPackets(new S_ServerMessage("\\aH屬性最大值只能到25 ，請重試一次。"));
                            }
                        } else if (s.equalsIgnoreCase("con")) {
                            if (pc.getBaseCon() < ConfigAlt.POWER) {
                                pc.addBaseCon((byte) 1); // 素CON值+1
                                pc.setBonusStats(pc.getBonusStats() + 1);
                                // XXX 能力基本資訊-體質
                                pc.sendPackets(new S_ConDetails(2, L1ClassFeature.calcConHpr(pc.getCon(), pc.getBaseCon()), L1ClassFeature.calcConPotionHpr(pc.getCon(), pc.getBaseCon()), L1ClassFeature.calcAbilityMaxWeight(pc.getStr(), pc.getCon()), L1ClassFeature.calcBaseClassLevUpHpUp(pc.getType()) + L1ClassFeature.calcBaseConLevUpExtraHpUp(pc.getType(), pc.getBaseCon())));
                                // XXX 重量程度資訊
                                pc.sendPackets(new S_WeightStatus(pc.getInventory().getWeight() * 100 / (int) pc.getMaxWeight(), pc.getInventory().getWeight(), (int) pc.getMaxWeight()));
                                // XXX 純能力資訊
                                pc.sendPackets(new S_BaseAbility(pc.getBaseStr(), pc.getBaseInt(), pc.getBaseWis(), pc.getBaseDex(), pc.getBaseCon(), pc.getBaseCha()));
                                pc.sendPackets(new S_OwnCharStatus(pc));
                                pc.sendPackets(new S_OwnCharStatus2(pc));
                                pc.sendPackets(new S_CharVisualUpdate(pc));
                                pc.save(); // 人物資料記錄
                            } else {
                                pc.sendPackets(new S_ServerMessage("\\aH屬性最大值只能到25 ，請重試一次。"));
                            }
                        } else if (s.equalsIgnoreCase("int")) {
                            if (pc.getBaseInt() < ConfigAlt.POWER) {
                                pc.addBaseInt((byte) 1); // 素INT值+1
                                pc.setBonusStats(pc.getBonusStats() + 1);
                                // XXX 能力基本資訊-智力
                                pc.sendPackets(new S_IntDetails(2, L1ClassFeature.calcIntMagicDmg(pc.getInt(), pc.getBaseInt()), L1ClassFeature.calcIntMagicHit(pc.getInt(), pc.getBaseInt()), L1ClassFeature.calcIntMagicCritical(pc.getInt(), pc.getBaseInt()), L1ClassFeature.calcIntMagicBonus(pc.getType(), pc.getInt()), L1ClassFeature.calcIntMagicConsumeReduction(pc.getInt())));
                                // XXX 純能力資訊
                                pc.sendPackets(new S_BaseAbility(pc.getBaseStr(), pc.getBaseInt(), pc.getBaseWis(), pc.getBaseDex(), pc.getBaseCon(), pc.getBaseCha()));
                                pc.sendPackets(new S_OwnCharStatus(pc));
                                pc.sendPackets(new S_OwnCharStatus2(pc));
                                pc.sendPackets(new S_CharVisualUpdate(pc));
                                pc.save(); // 人物資料記錄
                            } else {
                                // 481 \f1屬性最大值只能到35。 請重試一次。
                                pc.sendPackets(new S_ServerMessage("\\aH屬性最大值只能到25 ，請重試一次。"));
                            }
                        } else if (s.equalsIgnoreCase("wis")) {
                            if (pc.getBaseWis() < ConfigAlt.POWER) {
                                pc.addBaseWis((byte) 1); // 素WIS值+1
                                pc.resetBaseMr();
                                pc.setBonusStats(pc.getBonusStats() + 1);
                                // XXX 能力基本資訊-精神
                                pc.sendPackets(new S_WisDetails(2, L1ClassFeature.calcWisMpr(pc.getWis(), pc.getBaseWis()), L1ClassFeature.calcWisPotionMpr(pc.getWis(), pc.getBaseWis()), L1ClassFeature.calcStatMr(pc.getWis()) + L1ClassFeature.newClassFeature(pc.getType()).getClassOriginalMr(), L1ClassFeature.calcBaseWisLevUpMpUp(pc.getType(), pc.getBaseWis())));
                                // XXX 純能力資訊
                                pc.sendPackets(new S_BaseAbility(pc.getBaseStr(), pc.getBaseInt(), pc.getBaseWis(), pc.getBaseDex(), pc.getBaseCon(), pc.getBaseCha()));
                                pc.sendPackets(new S_OwnCharStatus(pc));
                                pc.sendPackets(new S_OwnCharStatus2(pc));
                                pc.sendPackets(new S_CharVisualUpdate(pc));
                                pc.save(); // 人物資料記錄
                            } else {
                                // 481 \f1屬性最大值只能到35。 請重試一次。
                                pc.sendPackets(new S_ServerMessage("\\aH屬性最大值只能到25 ，請重試一次。"));
                            }
                        } else if (s.equalsIgnoreCase("cha")) {
                            if (pc.getBaseCha() < ConfigAlt.POWER) {
                                pc.addBaseCha((byte) 1); // 素CHA值+1
                                pc.setBonusStats(pc.getBonusStats() + 1);
                                // XXX 純能力資訊
                                pc.sendPackets(new S_BaseAbility(pc.getBaseStr(), pc.getBaseInt(), pc.getBaseWis(), pc.getBaseDex(), pc.getBaseCon(), pc.getBaseCha()));
                                pc.sendPackets(new S_OwnCharStatus(pc));
                                pc.sendPackets(new S_OwnCharStatus2(pc));
                                pc.sendPackets(new S_CharVisualUpdate(pc));
                                pc.save(); // 人物資料記錄
                            } else {
                                // 481 \f1屬性最大值只能到35。 請重試一次。
                                pc.sendPackets(new S_ServerMessage("\\aH屬性最大值只能到25 ，請重試一次。"));
                            }
                        }
                        // 判斷是否還有剩餘獎勵點數
                        if ((pc.getLevel() >= 51 && pc.getLevel() - 50 > pc.getBonusStats()) || (pc.getLevel() >= 51 && pc.getLevel() - 50 > pc.getBonusStats() - 49)) {
                            if ((pc.getBaseStr() + pc.getBaseDex() + pc.getBaseCon() + pc.getBaseInt() + pc.getBaseWis() + pc.getBaseCha()) < (ConfigAlt.POWER * 6)) { // 設定能力值上限
                                int bonus = (pc.getLevel() - 50) - pc.getBonusStats();// 可以點的點數 XXX 7.6C ADD
                                pc.sendPackets(new S_Message_YN(479, bonus));
                            }
                        }
                    }
                    break;
                // 轉生天賦
                case 2760:
                    c = readC();
                    if (c == 1) {
                        int si = pc.getSi();
                        int rei_pt = 0;
                        for (int pt : pc.getReincarnationSkill()) {
                            rei_pt += pt;
                        }
                        rei_pt = pc.getTurnLifeSkillCount() - rei_pt;
                        if (rei_pt > 0) {
                            ReincarnationSkill.getInstance().addPoint(pc, si);
                            pc.sendPackets(new S_ReincarnationHtml(pc));
                        }
                    }
                    pc.setSi(-1);
                    break;
                case 2761:
                    c = readC();
                    if (c == 1) {
                        int rei_pt = 0;
                        for (int pt : pc.getReincarnationSkill()) {
                            rei_pt += pt;
                        }
                        rei_pt = pc.getTurnLifeSkillCount() - rei_pt;
                        if ((rei_pt != pc.getMeteLevel()) && (pc.getInventory().checkItem(ConfigTurn.ReiItemId, ConfigTurn.ReiItemCount))) {
                            pc.getInventory().consumeItem(ConfigTurn.ReiItemId, ConfigTurn.ReiItemCount);
                            ReincarnationSkill.getInstance().resetPoint(pc);
                            pc.sendPackets(new S_ReincarnationHtml(pc));
                            pc.sendPackets(new S_SystemMessage(L1SystemMessage.ShowMessage(857))); // 轉生技能點數重置成功 10秒後將會斷線。
                            pc.setSkillEffect(L1SkillId.ReiSkill_Disconnect, 10 * 1000);
                        }
                    }
                    pc.setSi(-1);
                    break;
                // 轉生天賦end
                /** 師徒系統 取消
                 case 2958:
                 L1PcInstance targetPc = (L1PcInstance) World.get().findObject(pc.getTempID());
                 pc.setTempID(0);
                 if (targetPc == null) {
                 return;
                 }
                 if (readC() == 1) {
                 L1PcInstance pc_2;
                 L1PcInstance pc_1;
                 if (pc.getLevel() > targetPc.getLevel()) {
                 pc_1 = pc;
                 pc_2 = targetPc;
                 } else {
                 pc_1 = targetPc;
                 pc_2 = pc;
                 }
                 L1Apprentice apprentice = CharApprenticeTable.getInstance().getApprentice(pc_1);
                 if (apprentice != null) {
                 if (apprentice.checkSize()) {
                 apprentice.getTotalList().add(pc_2);
                 pc_1.setApprentice(apprentice);
                 pc_1.setPunishTime(null);
                 pc_2.setApprentice(apprentice);
                 pc_2.setPunishTime(null);
                 CharApprenticeTable.getInstance().updateApprentice(pc_1.getId(), apprentice.getTotalList());
                 pc_1.sendPackets(new S_ServerMessage(2964));
                 pc_2.sendPackets(new S_ServerMessage(2964));
                 }
                 } else {
                 apprentice = new L1Apprentice(pc_1, new L1PcInstance[] { pc_2 });
                 pc_1.setApprentice(apprentice);
                 pc_1.setPunishTime(null);
                 pc_2.setApprentice(apprentice);
                 pc_2.setPunishTime(null);
                 CharApprenticeTable.getInstance().insertApprentice(apprentice);
                 pc_1.sendPackets(new S_ServerMessage(2964));
                 pc_2.sendPackets(new S_ServerMessage(2964));
                 return;
                 }
                 } else {
                 pc.sendPackets(new S_ServerMessage(2965));
                 targetPc.sendPackets(new S_ServerMessage(2965));
                 }
                 break;
                 case 2967:
                 case 2968:
                 if (readC() == 1) {
                 targetPc = (L1PcInstance) World.get().findObject(pc.getTempID());
                 pc.setTempID(0);
                 if (targetPc == null) {
                 return;
                 }
                 targetPc.setTempID(pc.getId());
                 targetPc.sendPackets(new S_Message_YN(2958, pc.getName()));
                 }
                 break;
                 case 2967: // 要將 %0 奉為師父嗎？
                 if (readC() == 1) { // yes
                 L1Object obj = World.get().findObject(pc.getTempID());
                 if (obj instanceof L1PcInstance) {
                 L1PcInstance master = (L1PcInstance) obj;
                 // 要接受 %0 為弟子嗎？
                 master.sendPackets(new S_Message_YN(2968, pc.getName()));
                 master.setTempID(pc.getId());
                 }
                 } else {// no
                 // 師徒締結失敗。
                 pc.sendPackets(new S_ServerMessage(2965));
                 }
                 pc.setTempID(0);
                 break;
                 case 2968: // 要接受 %0 為弟子嗎？
                 L1Object obj = World.get().findObject(pc.getTempID());
                 if (obj instanceof L1PcInstance) {
                 L1PcInstance disciple = (L1PcInstance) obj;
                 if (readC() == 1) { // yes
                 disciple.setMasterID(pc.getId());
                 pc.setMasterID(-1);
                 L1Master.getInstance().creatRelation(pc.getId(), disciple);
                 // 師徒締結成功。
                 pc.sendPackets(new S_ServerMessage(2964));
                 disciple.sendPackets(new S_ServerMessage(2964));
                 } else {
                 // 師徒締結失敗。
                 pc.sendPackets(new S_ServerMessage(2965));
                 disciple.sendPackets(new S_ServerMessage(2965));
                 }
                 }
                 pc.setTempID(0);
                 break;
                 */
                case 3312:// 76級戒指欄位
                    c = readC();
                    if (c == 1) {
                        if (!pc.getQuest().isEnd(L1PcQuest.QUEST_SLOT76) && pc.getSlot() == 76) {
                            if (pc.getInventory().checkItem(40308, 10000000) && pc.getLevel() >= 76) {
                                pc.getInventory().consumeItem(40308, 10000000);// 扣除金幣
                                pc.getQuest().set_end(L1PcQuest.QUEST_SLOT76);
                                pc.sendPackets(new S_NPCTalkReturn(pc, "slot9"));
                            } else {
                                pc.sendPackets(new S_ServerMessage(3253));
                                pc.sendPackets(new S_NPCTalkReturn(pc, "slot3"));
                            }
                        }
                    }
                    break;
                case 3313:// 81級戒指欄位
                    c = readC();
                    if (c == 1) {
                        if (!pc.getQuest().isEnd(L1PcQuest.QUEST_SLOT81) && pc.getSlot() == 81) {
                            if (pc.getInventory().checkItem(40308, 30000000) && pc.getLevel() >= 81) {
                                pc.getInventory().consumeItem(40308, 30000000);// 扣除金幣
                                pc.getQuest().set_end(L1PcQuest.QUEST_SLOT81);
                                pc.sendPackets(new S_NPCTalkReturn(pc, "slot9"));
                            } else {
                                pc.sendPackets(new S_ServerMessage(3253));
                                pc.sendPackets(new S_NPCTalkReturn(pc, "slot3"));
                            }
                        }
                    }
                    break;
                case 3589: // 是否開啟飾品欄位
                    c = readC();
                    if (c == 1) {
                        if (!pc.getQuest().isEnd(L1PcQuest.QUEST_SLOT59) && pc.getSlot() == 59) {
                            if (pc.getInventory().checkItem(40308, 20000000) && pc.getLevel() >= 59) {
                                pc.getInventory().consumeItem(40308, 20000000);// 扣除金幣
                                pc.getQuest().set_end(L1PcQuest.QUEST_SLOT59);
                                pc.sendPackets(new S_NPCTalkReturn(pc, "slot9"));
                            } else {
                                pc.sendPackets(new S_ServerMessage(3253));
                                pc.sendPackets(new S_NPCTalkReturn(pc, "slot3"));
                            }
                        }
                    }
                    break;
                case 1256:// 寵物競速 預約名單回應
                    L1PolyRace.getInstance().requsetAttr(pc, readC());
                    break;
                case 1268: // TODO 死亡競賽系統
                    if (pc.getLevel() > 29 && pc.getLevel() < 52) {
                        // L1DeathMatch.getInstance().requsetAttr(pc, readC());
                    } else if (pc.getLevel() > 51) {
                        L1DeathMatch.getInstance().requsetAttr(pc, readC());
                    }
                default:
                    break;
            }
        } catch (final Exception e) {
            // _log.error(e.getLocalizedMessage(), e);
        } finally {
            this.over();
        }
    }

    // 7.6刪除
	/*private void leaveClan(L1PcInstance leavePc, boolean isApproved) {
		String clan_name = leavePc.getClanname();
		L1Clan clan = WorldClan.get().getClan(clan_name);
		L1PcInstance clanMember[] = clan.getOnlineClanMember();
		for (int i = 0; i < clanMember.length; i++) {
			clanMember[i].sendPackets(new S_ServerMessage(178, leavePc.getName(), clan_name));
			// \f1%0%1血盟脫退。
		}
		if (clan.getWarehouseUsingChar() == leavePc.getId()) { // 血盟成員使用血盟倉庫中
			clan.setWarehouseUsingChar(0); // 移除使用血盟倉庫的成員
		}
		try {
			long time = 0; // 再加入時間(秒)
			if (isApproved) { // 君主副君主承認濟脫退
				time = 0;
			} else { // 任意脫退
				time = 60 * 60 * 3 * 1000; // 3小時
			}
			leavePc.setClanid(0);
			leavePc.setClanname("");
			leavePc.setClanRank(0);
			leavePc.setClanMemberId(0);
			leavePc.setClanMemberNotes("");
			leavePc.sendPacketsAll(new S_CharTitle(leavePc.getId(), ""));
			leavePc.sendPacketsAll(new S_CharReset(leavePc.getId(), 0));
			leavePc.save(); // 儲存玩家資料到資料庫中
			// 原地更新畫面
			L1PcUnlock.Pc_Unlock(leavePc);
			clan.delMemberName(leavePc.getName());
			ClanMembersTable.getInstance().deleteMember(leavePc.getId());
			leavePc.setRejoinClanTime(new Timestamp(System.currentTimeMillis() + time));
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}*/
    // 7.6
    private void changeClan(final ClientExecutor clientthread, final L1PcInstance pc, final L1PcInstance joinPc, final int maxMember) {
        final int clanId = pc.getClanid();
        final String clanName = pc.getClanname();
        final L1Clan clan = WorldClan.get().getClan(clanName);
        final String clanMemberName[] = clan.getAllMembers();
        final int clanNum = clanMemberName.length;
        final int oldClanId = joinPc.getClanid();
        final String oldClanName = joinPc.getClanname();
        final L1Clan oldClan = WorldClan.get().getClan(oldClanName);
        final String oldClanMemberName[] = oldClan.getAllMembers();
        final int oldClanNum = oldClanMemberName.length;
        if ((clan != null) && (oldClan != null) && joinPc.isCrown() && // 自分君主
                (joinPc.getId() == oldClan.getLeaderId())) {
            if (maxMember < (clanNum + oldClanNum)) { // 空
                // 188 %0%s 無法接受你成為該血盟成員。
                joinPc.sendPackets(new S_ServerMessage(188, pc.getName()));
                return;
            }
            final L1PcInstance clanMember[] = clan.getOnlineClanMember();
            for (L1PcInstance l1PcInstance : clanMember) {
                // 94 \f1你接受%0當你的血盟成員。
                l1PcInstance.sendPackets(new S_ServerMessage(94, joinPc.getName()));
            }
            for (String s : oldClanMemberName) {
                final L1PcInstance oldClanMember = World.get().getPlayer(s);
                if (oldClanMember != null) { // 中舊
                    ClanMembersTable.getInstance().deleteMember(oldClanMember.getId());
                    oldClanMember.setClanid(clanId);
                    oldClanMember.setClanname(clanName);
                    // 血盟連合加入君主
                    // 君主連血盟員見習
                    if (oldClanMember.getId() == joinPc.getId()) {
                        oldClanMember.setClanRank(L1Clan.CLAN_RANK_GUARDIAN);
                    } else {
                        oldClanMember.setClanRank(L1Clan.CLAN_RANK_LEAGUE_PROBATION);
                    }
                    try {
                        // 資料存檔
                        oldClanMember.save();
                    } catch (final Exception e) {
                        _log.error(e.getLocalizedMessage(), e);
                    }
                    clan.addMemberName(oldClanMember.getName());
                    ClanMembersTable.getInstance().newMember(oldClanMember); // 加入成員資料
                    // 95 \f1加入%0血盟。
                    oldClanMember.sendPackets(new S_ServerMessage(95, clanName));
                } else { // 中舊
                    try {
                        final L1PcInstance offClanMember = CharacterTable.get().restoreCharacter(s);
                        offClanMember.setClanid(clanId);
                        offClanMember.setClanname(clanName);
                        offClanMember.setClanRank(L1Clan.CLAN_RANK_PUBLIC);
                        offClanMember.save(); // 資料存檔
                        clan.addMemberName(offClanMember.getName());
                        ClanMembersTable.getInstance().newMember(offClanMember); // 加入成員資料
                    } catch (final Exception e) {
                        _log.error(e.getLocalizedMessage(), e);
                    }
                }
            }
            // 資料刪除
            ClanEmblemReading.get().deleteIcon(oldClanId);
            /*
             * final String emblem_file = String.valueOf(oldClanId); final File
             * file = new File("emblem/" + emblem_file); file.delete();
             */
            ClanReading.get().deleteClan(oldClanName);
        }
    }

    /**
     * 呼喚盟友
     *
     * @param pc 被招喚的盟友
     */
    public void callClan(final L1PcInstance pc) {
        final L1PcInstance leader = (L1PcInstance) World.get().findObject(pc.getTempID());
        pc.setTempID(0);
        // 無法攻擊/使用道具/技能/回城的狀態 XXX
        if (pc.isParalyzedX()) {
            return;
        } // */
        if (leader == null) {
            return;
        }
        if (!pc.getMap().isEscapable() && !pc.isGm()) {
            // 647 這附近的能量影響到瞬間移動。在此地無法使用瞬間移動。
            pc.sendPackets(new S_ServerMessage(647));
            return;
        }
        //		if (pc.getId() != leader.getCallClanId()) {
        //			return;
        //		}
        boolean isInWarArea = false;
        final int castleId = L1CastleLocation.getCastleIdByArea(leader);
        if (castleId != 0) {
            isInWarArea = true;
            if (ServerWarExecutor.get().isNowWar(castleId)) {
                isInWarArea = false; // 戰爭時間中旗內使用可能
            }
        }
        final short mapId = leader.getMapId();
        if (((mapId != 0) && (mapId != 4) && (mapId != 304)) || isInWarArea) {
            // 626 因太遠以致於無法傳送到你要去的地方。
            pc.sendPackets(new S_ServerMessage(629));
            return;
        }
        // 副本地圖中判斷
        if (QuestMapTable.get().isQuestMap(pc.getMapId())) {
            // 626 因太遠以致於無法傳送到你要去的地方。
            pc.sendPackets(new S_ServerMessage(629));
            return;
        }
        
        final L1Map map = leader.getMap();
        int locX = leader.getX();
        int locY = leader.getY();
        int heading = leader.getHeading();

        // 隨機範圍2格
        locX += (int)(Math.random() * 5) - 2;
        locY += (int)(Math.random() * 5) - 2;

        if (!map.isPassable(locX, locY, null)) {
            locX = leader.getX();
            locY = leader.getY();
        }
        
        L1Teleport.teleport(pc, locX, locY, mapId, heading, true, L1Teleport.CALL_CLAN);
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }

    private static class KickPc implements Runnable {
        private ClientExecutor _client;

        private KickPc(L1PcInstance pc) {
            _client = pc.getNetConnection();
        }

        private void start_cmd() {
            GeneralThreadPool.get().execute(this);
        }

        @Override
        public void run() {
            try {
                TimeUnit.MILLISECONDS.sleep(5000);
                _client.kick();
            } catch (InterruptedException e) {
                _log.error(e.getLocalizedMessage(), e);
            }
        }
    }
}