package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.MapsGroupTable;
import com.lineage.server.datatables.MapsTable;
import com.lineage.server.datatables.lock.ClanAllianceReading;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Alliance;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1War;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1MapsLimitTime;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import com.lineage.server.world.WorldWar;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Calendar;
import java.util.Collection;
import java.util.Random;

public class C_Rank extends ClientBasePacket {
    private static final Log _log = LogFactory.getLog(C_Rank.class);

    @Override
    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            // 資料載入
            this.read(decrypt);
            int data = 0;
            int rank = 0;
            //String name = "";
            try {
                data = this.readC();
                rank = this.readC();
                //name = this.readS();
            } catch (final Exception e) {
                return;
            }
            final L1PcInstance pc = client.getActiveChar();
            if (pc == null) {
                return;
            }
            L1Clan clan = WorldClan.get().getClan(pc.getClanname());
            //String clanname = pc.getClanname();
            switch (data) {
			/*case 1:
				// 請求授予血盟RANK
				L1PcInstance targetPc = World.get().getPlayer(name);// 取回對像資料
				if (targetPc == null) {// 對像不在線上
					targetPc = CharacterTable.get().restoreCharacter(name);// 取回角色資料
					if (targetPc == null) {
						pc.sendPackets(new S_ServerMessage(109, name)); // %0名前人。
						return;
					}
				}
				clan = WorldClan.get().getClan(pc.getClanname());
				if (clan == null) {
					return;
				}
				int userRank = pc.getClanRank(); // 授予者的階級
				// 聯盟君主賦予聯盟階級
				if (userRank == L1Clan.CLAN_RANK_LEAGUE_PRINCE) {// 聯盟君主
					switch (rank) {
					case L1Clan.CLAN_RANK_PUBLIC:
						_giverank = L1Clan.CLAN_RANK_LEAGUE_PUBLIC;
						break;
					case L1Clan.CLAN_RANK_PROBATION:
						_giverank = L1Clan.CLAN_RANK_LEAGUE_PROBATION;
						break;
					case L1Clan.CLAN_RANK_GUARDIAN:
						_giverank = L1Clan.CLAN_RANK_LEAGUE_GUARDIAN;
						break;
					}
				} else {// 未聯盟狀態
					_giverank = rank;
				}
				if ((targetPc.getClanRank() == L1Clan.CLAN_RANK_LEAGUE_PRINCE)
						|| (targetPc.getClanRank() == L1Clan.CLAN_RANK_PRINCE)) {// 對象是盟主
					// 只能對比自己低階級才能給予稱號.
					pc.sendPackets(new S_ServerMessage(2065));
					return;
				}
				if (targetPc.getClanRank() == L1Clan.CLAN_RANK_LEAGUE_VICEPRINCE) {// 對象是副盟主
					// 無法變更副王族階級.
					pc.sendPackets(new S_ServerMessage(2064));
					return;
				}
				if (targetPc.getClanRank() == pc.getClanRank()) {// 對象與自己同階級
					// 只能對比自己低階級才能給予稱號.
					pc.sendPackets(new S_ServerMessage(2065));
					return;
				}
				if ((userRank != L1Clan.CLAN_RANK_LEAGUE_VICEPRINCE) && (userRank != L1Clan.CLAN_RANK_LEAGUE_PRINCE)
						&& (userRank != L1Clan.CLAN_RANK_LEAGUE_GUARDIAN) && (userRank != L1Clan.CLAN_RANK_GUARDIAN)
						&& (userRank != L1Clan.CLAN_RANK_PRINCE)) {
					pc.sendPackets(new S_ServerMessage("守護騎士以上階級才能使用此命令。"));
					return;
				}
				if (name.equalsIgnoreCase(pc.getName())) {
					// 對自己較低階級才能變更.
					pc.sendPackets(new S_ServerMessage(2068));
					return;
				}
				if (_giverank == 0 || _giverank > 10) {
					System.out.println("階級錯誤");
					// \f1請輸入以下內容: "/階級 \f0角色名稱 階級[守護騎士, 修習騎士, 一般]\f1"
					pc.sendPackets(new S_ServerMessage(2149));
					return;
				}
				// 可以授予的階級清單
				List<Integer> rankList = new ArrayList<Integer>();
				// 賦予者階級
				switch (userRank) {
				case L1Clan.CLAN_RANK_LEAGUE_VICEPRINCE:
					if (pc.getLevel() < 25) {
						// 如果要獲得該權限，需要25等級以上。
						pc.sendPackets(new S_ServerMessage(2471));
						return;
					}
					rankList.add(L1Clan.CLAN_RANK_LEAGUE_PUBLIC);
					rankList.add(L1Clan.CLAN_RANK_LEAGUE_PROBATION);
					rankList.add(L1Clan.CLAN_RANK_LEAGUE_GUARDIAN);
					break;
				case L1Clan.CLAN_RANK_LEAGUE_PRINCE:
					if (pc.getLevel() < 25) {
						// 如果要獲得該權限，需要25等級以上。
						pc.sendPackets(new S_ServerMessage(2471));
						return;
					}
					rankList.add(L1Clan.CLAN_RANK_LEAGUE_PUBLIC);
					rankList.add(L1Clan.CLAN_RANK_LEAGUE_VICEPRINCE);
					rankList.add(L1Clan.CLAN_RANK_LEAGUE_PROBATION);
					rankList.add(L1Clan.CLAN_RANK_LEAGUE_GUARDIAN);
					break;
				case L1Clan.CLAN_RANK_LEAGUE_GUARDIAN:
					if (pc.getLevel() < 40) {
						// 如果要獲得該權限，需要 40等級以上。
						pc.sendPackets(new S_ServerMessage(2472));
						return;
					}
					rankList.add(L1Clan.CLAN_RANK_LEAGUE_PUBLIC);
					rankList.add(L1Clan.CLAN_RANK_LEAGUE_PROBATION);
					break;
				case L1Clan.CLAN_RANK_GUARDIAN:
					if (pc.getLevel() < 40) {
						// 如果要獲得該權限，需要 40等級以上。
						pc.sendPackets(new S_ServerMessage(2472));
						return;
					}
					rankList.add(L1Clan.CLAN_RANK_PUBLIC);
					rankList.add(L1Clan.CLAN_RANK_PROBATION);
					break;
				case L1Clan.CLAN_RANK_PRINCE:
					if (pc.getLevel() < 25) {
						// 如果要獲得該權限，需要25等級以上。
						pc.sendPackets(new S_ServerMessage(2471));
						return;
					}
					rankList.add(L1Clan.CLAN_RANK_PUBLIC);
					rankList.add(L1Clan.CLAN_RANK_PROBATION);
					rankList.add(L1Clan.CLAN_RANK_GUARDIAN);
					break;
				}
				// 賦予階級
				switch (_giverank) {
				case 2:// 聯盟一般
					if (!rankList.contains(2)) {
						// 只能對比自己低階級才能給予稱號.
						pc.sendPackets(new S_ServerMessage(2065));
						return;
					}
					break;
				case 5:// 聯盟修習騎士
					if (!rankList.contains(5)) {
						// 只能對比自己低階級才能給予稱號.
						pc.sendPackets(new S_ServerMessage(2065));
						return;
					}
					break;
				case 6:// 聯盟守護騎士
					if (targetPc.getLevel() < 40) {
						// 守護騎士階級要到40等級以上才能賦予血盟員資格。
						pc.sendPackets(new S_ServerMessage(2473));
						return;
					}
					if (!rankList.contains(6)) {
						// 只能對比自己低階級才能給予稱號.
						pc.sendPackets(new S_ServerMessage(2065));
						return;
					}
					break;
				case 7:// 一般
					if (!rankList.contains(7)) {
						// 只能對比自己低階級才能給予稱號.
						pc.sendPackets(new S_ServerMessage(2065));
						return;
					}
					break;
				case 8:// 修習騎士
					if (!rankList.contains(8)) {
						// 只能對比自己低階級才能給予稱號.
						pc.sendPackets(new S_ServerMessage(2065));
						return;
					}
					break;
				case 9:// 守護騎士
					if (targetPc.getLevel() < 40) {
						// 守護騎士階級要到40等級以上才能賦予血盟員資格。
						pc.sendPackets(new S_ServerMessage(2473));
						return;
					}
					if (!rankList.contains(9)) {
						// 只能對比自己低階級才能給予稱號.
						pc.sendPackets(new S_ServerMessage(2065));
						return;
					}
					break;
				}
				if (pc.getClanid() == targetPc.getClanid()) { // 同血盟
					try {
						targetPc.setClanRank(_giverank);
						targetPc.save(); // 儲存玩家的資料到資料庫中
						pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED, _giverank, name)); // 你的階級變更為%s
						if (targetPc.getOnlineStatus() == 1) {// 玩家在線上
							L1PcUnlock.Pc_Unlock(targetPc);// 原地更新畫面
							targetPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED, _giverank, name)); // 你的階級變更為%s
						}
					} catch (Exception e) {
						_log.error(e.getLocalizedMessage(), e);
					}
				} else {// 不同血盟
					// 201：\f1%0%d不是你的血盟成員。
					pc.sendPackets(new S_ServerMessage(201, name));
					return;
				}
				break;
			case 2:
				//pc.sendPackets(new S_ServerMessage(74, "同盟目錄"));
				if (clan.AllianceSize() > 0) {
					S_PacketBox pb2 = new S_PacketBox(pc,S_PacketBox.PLEDGE_UNION);
					pc.sendPackets(pb2);
				}
				break;
			case 3:
				//pc.sendPackets(new S_ServerMessage(74, "加入同盟"));
				L1PcInstance allianceLeader = FaceToFace.faceToFace(pc);
				if (pc.getLevel() < 25 || !pc.isCrown()) {
					S_ServerMessage sm = new S_ServerMessage(1206);
					pc.sendPackets(sm);
					return;
				}
				for (L1War war : WorldWar.get().getWarList()) {
					if (war.checkClanInWar(clanname)) {
						S_ServerMessage sm = new S_ServerMessage(1234);
						pc.sendPackets(sm);
						return;
					}
				}
				if (clan.AllianceSize() > 4) {
					S_SystemMessage sm = new S_SystemMessage("最多只能4個血盟進行聯盟");
					pc.sendPackets(sm);
					return;
				}
				if (allianceLeader != null) {
					if (allianceLeader.getLevel() > 24 && allianceLeader.isCrown()) {
						allianceLeader.setTempID(pc.getId());
						S_Message_YN yn = new S_Message_YN(223, pc.getName());
						allianceLeader.sendPackets(yn);
					} else {
						S_ServerMessage sm = new S_ServerMessage(1201);
						pc.sendPackets(sm);
					}
				}
				break;
			case 4:
				//pc.sendPackets(new S_ServerMessage(74, "退出同盟"));
				for (L1War war : WorldWar.get().getWarList()) {
					if (war.checkClanInWar(clanname)) {
						S_ServerMessage sm = new S_ServerMessage(1203);
						pc.sendPackets(sm);
						return;
					}
				}
				if (clan.AllianceSize() > 0) {
					S_Message_YN yn = new S_Message_YN(1210, "");
					pc.sendPackets(yn);
				} else {
					S_ServerMessage sm = new S_ServerMessage(1233);
					pc.sendPackets(sm);
				}
				break;*/
                // 7.6
                case 0: // 查詢血盟成員清單
                    if (clan == null) {
                        return;
                    }
                    final S_PacketBoxPledge Pledge2 = new S_PacketBoxPledge(2, pc, null, 0);
                    pc.sendPackets(Pledge2);
                    break;
                case 1:// 階級
                    String name = readS();
                    rank(pc, rank, name);
                    break;
                case 2:// 觀看同盟目錄(XXX 7.6取消)
				/*final L1Clan clan1 = pc.getClan();
				if (clan1 == null) {
					pc.sendPackets(new S_ServerMessage(1064));
					return;
				}
				final L1Alliance alliance1 = ClanAllianceReading.get().getAlliance(clan1.getClanId());
				if (alliance1 != null) {
					final StringBuffer sbr = new StringBuffer();
					for (final L1Clan l1clan : alliance1.getTotalList()) {
						if (clan1.getClanId() != l1clan.getClanId()) {
							sbr.append(l1clan.getClanName()).append(" ");
						}
					}
					pc.sendPackets(new S_PacketBox(S_PacketBox.ALLIANCE_LIST, sbr.toString()));
				} else {
					pc.sendPackets(new S_ServerMessage(1233));
				}
				break;*/
                case 3:
				/*final L1Clan clan2 = pc.getClan();
				if ((clan2 == null) || (pc.getId() != clan2.getLeaderId())) {
					pc.sendPackets(new S_ServerMessage(518));
					return;
				}
				if ((pc.getLevel() < 25) || !pc.isCrown()) {
					pc.sendPackets(new S_ServerMessage(1206));
					return;
				}
				if (ClanAllianceReading.get().getAlliance(clan2.getClanId()) != null) {
					pc.sendPackets(new S_ServerMessage(1202));
					return;
				}
				for (final L1War war : WorldWar.get().getWarList()) {
					if (war.checkClanInWar(clan2.getClanName())) {
						pc.sendPackets(new S_ServerMessage(1234));
						return;
					}
				}
				final L1PcInstance alliancePc = FaceToFace.faceToFace(pc);
				if (alliancePc != null) {
					if (!alliancePc.isCrown()) {
						pc.sendPackets(new S_ServerMessage(92, alliancePc.getName()));
						return;
					}
					if ((alliancePc.getClanid() == 0) || (alliancePc.getClan() == null)) {
						pc.sendPackets(new S_ServerMessage(90, alliancePc.getName()));
						return;
					}
					alliancePc.setTempID(pc.getId());
					alliancePc.sendPackets(new S_Message_YN(223, pc.getName()));
				}*/
                    break;
                case 4:
                    final L1Clan clan3 = pc.getClan();
                    if ((clan3 == null) || (pc.getId() != clan3.getLeaderId())) {
                        pc.sendPackets(new S_ServerMessage(518));
                        return;
                    }
                    for (final L1War war : WorldWar.get().getWarList()) {
                        if (war.checkClanInWar(clan3.getClanName())) {
                            pc.sendPackets(new S_ServerMessage(1203));
                            return;
                        }
                    }
                    final L1Alliance alliance3 = ClanAllianceReading.get().getAlliance(clan3.getClanId());
                    if (alliance3 != null) {
                        pc.sendPackets(new S_Message_YN(1210));
                    } else {
                        pc.sendPackets(new S_ServerMessage(1233));
                    }
                    break;
                case 5:
                    if (pc.get_food() >= 225) {
                        if (pc.getWeapon() != null) {
                            Random random = new Random();
                            long time = pc.get_h_time();
                            Calendar cal = Calendar.getInstance();
                            long h_time = cal.getTimeInMillis() / 1000L;
                            int n = (int) ((h_time - time) / 60L);
                            int addhp = 0;
                            if (n <= 0) {
                                // 1974：還無法使用生存的吶喊。
                                pc.sendPackets(new S_ServerMessage(1974));
                            } else if ((n >= 1) && (n <= 29)) {
                                addhp = (int) (pc.getMaxHp() * (n / 100.0D));
                            } else if (n >= 30) {
                                int lv = pc.getWeapon().getEnchantLevel();
                                switch (lv) {
                                    case 0:
                                    case 1:
                                    case 2:
                                    case 3:
                                    case 4:
                                    case 5:
                                    case 6:
                                        pc.sendPacketsAll(new S_SkillSound(pc.getId(), 8907));
                                        pc.sendPacketsAll(new S_SkillSound(pc.getId(), 8684));
                                        addhp = (int) (pc.getMaxHp() * ((random.nextInt(20) + 20) / 100.0D));
                                        break;
                                    case 7:
                                    case 8:
                                        pc.sendPacketsAll(new S_SkillSound(pc.getId(), 8909));
                                        pc.sendPacketsAll(new S_SkillSound(pc.getId(), 8685));
                                        addhp = (int) (pc.getMaxHp() * ((random.nextInt(20) + 30) / 100.0D));
                                        break;
                                    case 9:
                                    case 10:
                                        pc.sendPacketsAll(new S_SkillSound(pc.getId(), 8910));
                                        pc.sendPacketsAll(new S_SkillSound(pc.getId(), 8773));
                                        addhp = (int) (pc.getMaxHp() * ((random.nextInt(10) + 50) / 100.0D));
                                        break;
                                    case 11:
                                    default:
                                        pc.sendPacketsAll(new S_SkillSound(pc.getId(), 8908));
                                        pc.sendPacketsAll(new S_SkillSound(pc.getId(), 8686));
                                        addhp = (int) (pc.getMaxHp() * 0.7D);
                                }
                            }
                            if (addhp != 0) {
                                pc.set_food(0);
                                pc.sendPackets(new S_PacketBox(11, 0));
                                pc.setCurrentHp(pc.getCurrentHp() + addhp);
                            }
                        } else {
                            // 1973：必須裝備上武器才可使用。
                            pc.sendPackets(new S_ServerMessage(1973));
                        }
                    }
                    break;
                case 6:
                    if (pc.getWeapon() != null) {
                        int lv = pc.getWeapon().getEnchantLevel();
                        int gfx = 8684;
                        switch (lv) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                                gfx = 8684;
                                break;
                            case 7:
                            case 8:
                                gfx = 8685;
                                break;
                            case 9:
                            case 10:
                                gfx = 8773;
                                break;
                            case 11:
                            default:
                                gfx = 8686;
                        }
                        pc.sendPacketsAll(new S_SkillSound(pc.getId(), gfx));
                    } else {
                        // 1973：必須裝備上武器才可使用。
                        pc.sendPackets(new S_ServerMessage(1973));
                    }
                    break;
                case 8: // 查詢指令(/入場時間)
                    final Collection<L1MapsLimitTime> mapLimitList = MapsGroupTable.get().getGroupMaps().values();
                    for (final L1MapsLimitTime mapLimit : mapLimitList) {
                        final int used_time = pc.getMapsTime(mapLimit.getOrderId());
                        final int time_str = (mapLimit.getLimitTime() - used_time) / 60;
                        pc.sendPackets(new S_ServerMessage(2535, mapLimit.getMapName(), String.valueOf(time_str)));
                    }
                    final int g_entertime = pc.getRocksPrisonTime();
                    // 56:奇巖地監4F
                    final int g_maxTime = MapsTable.get().getMapTime(56) * 60; // 轉換為秒數
                    int g_newTime = (g_maxTime - g_entertime) / 60;
                    if (g_newTime <= 0) {
                        g_newTime = 0;
                    }
                    final S_ServerMessage rocksPrison = new S_ServerMessage(2535, "奇巖/古魯丁地監", String.valueOf(g_newTime)); // [奇巖/古魯丁地監]
                    pc.sendPackets(rocksPrison); // %0 : %0 : 剩餘時間 %1 分
                    final int i_entertime = pc.getIvoryTowerTime();
                    // 82:象牙塔8F
                    final int i_maxTime = MapsTable.get().getMapTime(82) * 60; // 轉換為秒數
                    int i_newTime = (i_maxTime - i_entertime) / 60;
                    if (i_newTime <= 0) {
                        i_newTime = 0;
                    }
                    final S_ServerMessage ivorytower = new S_ServerMessage(2535, "象牙塔", String.valueOf(i_newTime)); // [象牙塔]
                    pc.sendPackets(ivorytower); // %0 : %0 : 剩餘時間 %1 分
                    final int l_entertime = pc.getLastabardTime();
                    // 479:拉斯塔巴德中央廣場
                    final int l_maxTime = MapsTable.get().getMapTime(479) * 60; // 轉換為秒數
                    int l_newTime = (l_maxTime - l_entertime) / 60;
                    if (l_newTime <= 0) {
                        l_newTime = 0;
                    }
                    final S_ServerMessage lastabard = new S_ServerMessage(2535, "拉斯塔巴德地監", String.valueOf(l_newTime)); // [拉斯塔巴德地監]
                    pc.sendPackets(lastabard); // %0 : %0 : 剩餘時間 %1 分
                    final int d_entertime = pc.getDragonValleyTime();
                    // 37:龍之谷地監 7樓
                    final int d_maxTime = MapsTable.get().getMapTime(37) * 60; // 轉換為秒數
                    int d_newTime = (d_maxTime - d_entertime) / 60;
                    if (d_newTime <= 0) {
                        d_newTime = 0;
                    }
                    final S_ServerMessage dragonvalley = new S_ServerMessage(2535, "龍之谷地監", String.valueOf(d_newTime)); // [龍之谷地監]
                    pc.sendPackets(dragonvalley); // %0 : %0 : 剩餘時間 %1 分
                    break;
                case 9:// 地圖剩餘使用時間
                    pc.sendPackets(new S_MapTimerOut(pc));
                    break;
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            this.over();
        }
    }

    // 7.6
    private void rank(final L1PcInstance pc, final int rank, final String name) {
        final L1PcInstance targetPc = World.get().getPlayer(name);
        final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
        if (clan == null) {
            return;
        }
        boolean isOK = false;
        // rank 2:一般 3:副君主 4:聯盟君主 5:修習騎士 6:守護騎士 7:一般 8:修習騎士 9:守護騎士 10:聯盟君主
        // 12精銳騎士 13 精銳騎士
        if ((rank >= 2) && (rank <= 13)) {
            isOK = true;
        }
        if (!isOK) {
            // \f1請輸入以下內容: "/階級 \f0角色名稱 階級[守護騎士, 修習騎士, 一般]\f1"
            pc.sendPackets(new S_ServerMessage(2149));
            return;
        }
        if (pc.isCrown()) { // 君主
            if (pc.getId() != clan.getLeaderId()) { // 血盟主
                // 785 你不再是君主了
                pc.sendPackets(new S_ServerMessage(785));
                return;
            }
        } else {
            // 518 血盟君主才可使用此命令。
            pc.sendPackets(new S_ServerMessage(518));
            return;
        }
        if (targetPc != null) {
            try {
                if (pc.getId() == targetPc.getId()) {
                    return;
                }
                if (pc.getClanid() == targetPc.getClanid()) {
                    targetPc.setClanRank(rank);
                    targetPc.save();
                    //SRC0822 修改血盟階級立即更新
                    pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED, targetPc.getClanRank(), targetPc.getName()));
                    if (targetPc.getOnlineStatus() == 1) {
                        targetPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED, targetPc.getClanRank(), targetPc.getName()));
                    }
                    // 你的階級變更為%s
                    //targetPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED, targetPc.getClanRank(),
                    //targetPc.getName()));
                } else {
                    // 201：\f1%0%d不是你的血盟成員。
                    pc.sendPackets(new S_ServerMessage(201, name));
                    return;
                }
            } catch (final Exception e) {
                _log.error(e.getLocalizedMessage(), e);
            }
        } else { // 線上無此人物
            try {
                final L1PcInstance restorePc = CharacterTable.get().restoreCharacter(name);
                if (pc.getId() == restorePc.getId()) {
                    return;
                }
                if ((restorePc != null) && (restorePc.getClanid() == pc.getClanid())) { // 相同血盟
                    restorePc.setClanRank(rank);
                    restorePc.save();
                } else {
                    // 109 沒有叫%0的人。
                    pc.sendPackets(new S_ServerMessage(109, name));
                    return;
                }
            } catch (final Exception e) {
                _log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }
}
