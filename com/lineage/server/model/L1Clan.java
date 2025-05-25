package com.lineage.server.model;

import com.lineage.server.datatables.ClanMembersTable;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class L1Clan {
    /**
     * 聯盟一般
     */
    public static final int CLAN_RANK_LEAGUE_PUBLIC = 2;
    /**
     * 聯盟 副君主
     */
    public static final int CLAN_RANK_LEAGUE_VICEPRINCE = 3;
    /**
     * 聯盟君主
     */
    public static final int CLAN_RANK_LEAGUE_PRINCE = 4;
    /**
     * 聯盟修習騎士
     */
    public static final int CLAN_RANK_LEAGUE_PROBATION = 5;
    /**
     * 聯盟守護騎士
     */
    public static final int CLAN_RANK_LEAGUE_GUARDIAN = 6;
    /**
     * 一般
     */
    public static final int CLAN_RANK_PUBLIC = 7;
    /**
     * 修習騎士
     */
    public static final int CLAN_RANK_PROBATION = 8;
    /**
     * 守護騎士
     */
    public static final int CLAN_RANK_GUARDIAN = 9;
    /**
     * 君主
     */
    public static final int CLAN_RANK_PRINCE = 10;
    private static final Log _log = LogFactory.getLog(L1Clan.class);
    private final Lock _lock = new ReentrantLock(true);
    private final L1DwarfForClanInventory _dwarfForClan = new L1DwarfForClanInventory(this);
    private final ArrayList<String> _membersNameList = new ArrayList<String>();
    String[] _rank = {"", "", "[聯盟一般]", "[聯盟副君主]", "[聯盟君主]", "[聯盟修習騎士]", "[聯盟守護騎士]", "[一般]", "[修習騎士]", "[守護騎士]", "[君主]"};
    private int _clanId;
    private String _clanName;
    private int _leaderId;
    private String _leaderName;
    private int _castleId;
    private int _houseId;
    private int _warehouse = 0;
    private int _ClanSkillId;//血盟技能 //SRC0629
    private int _ClanSkillLv;//血盟技能等級 //SRC0629
    // 7.6
    private boolean join_open_state;
    private int join_state = 0;
    private String join_password;
    private int _emblemId;
    private boolean _clanskill = false;
    private Timestamp _skilltime = null;
    private int _maxuser;
    private short _loginLevel;
    private ArrayList<Integer> allianceList = new ArrayList<Integer>();
    // 7.6
    private String _clanshowNote;
    private String _clanNote;
    /**
     * 是否顯示盟徽
     */
    private int showEmblem = 0;

    public int getClanId() {
        return _clanId;
    }

    public void setClanId(int clan_id) {
        _clanId = clan_id;
    }

    public String getClanName() {
        return _clanName;
    }

    public void setClanName(String clan_name) {
        _clanName = clan_name;
    }

    public int getLeaderId() {
        return _leaderId;
    }

    public void setLeaderId(int leader_id) {
        _leaderId = leader_id;
    }

    public String getLeaderName() {
        return _leaderName;
    }

    /*public void addMemberName(String member_name) {
        _lock.lock();
        try {
            if (!_membersNameList.contains(member_name))
                _membersNameList.add(member_name);
        } finally {
            _lock.unlock();
        }
    }*/
    public void setLeaderName(String leader_name) {
        _leaderName = leader_name;
    }

    /*public void delMemberName(String member_name) {
        _lock.lock();
        try {
            if (_membersNameList.contains(member_name))
                _membersNameList.remove(member_name);
        } finally {
            _lock.unlock();
        }
    }*/
    public int getCastleId() {
        return _castleId;
    }

    public void setCastleId(int hasCastle) {
        _castleId = hasCastle;
    }

    public int getHouseId() {
        return _houseId;
    }

    public void setHouseId(int hasHideout) {
        _houseId = hasHideout;
    }

    /**
     * 加入血盟成員清單
     *
     * @param member_name
     */
    public void addMemberName(final String member_name) {
        _lock.lock();
        try {
            if (!_membersNameList.contains(member_name)) {
                _membersNameList.add(member_name);
                final L1PcInstance pc = World.get().getPlayer(member_name);
                if (pc != null) {
                    L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading(), false);
                    pc.sendPackets(new S_PacketBox(S_PacketBox.PLEDGE_EMBLEM_STATUS, 0));
                }
            }
        } finally {
            _lock.unlock();
        }
    }

    /**
     * 移出血盟成員清單
     *
     * @param member_name
     */
    public void delMemberName(final String member_name) {
        _lock.lock();
        try {
            if (_membersNameList.contains(member_name)) {
                _membersNameList.remove(member_name);
                final L1PcInstance pc = World.get().getPlayer(member_name);
                if (pc != null) {
                    ClanMembersTable.getInstance().deleteMember(pc.getId());
                    L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading(), false);
                    pc.sendPackets(new S_PacketBox(S_PacketBox.PLEDGE_EMBLEM_STATUS, 0));
                }
            }
        } finally {
            _lock.unlock();
        }
    }

    /**
     * 血盟線上成員數量
     *
     * @return
     */
    public int getOnlineClanMemberSize() {
        int count = 0;
        try {
            for (String name : _membersNameList) {
                L1PcInstance pc = World.get().getPlayer(name);
                // 人員在線上
                if (pc != null) {
                    count++;
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return count;
    }

    public int getOnlineClanMemberSize50() {
        int count = 0;
        try {
            for (String name : _membersNameList) {
                L1PcInstance pc = World.get().getPlayer(name);
                if (pc != null && pc.getLevel() >= 65) {
                    count++;
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return count;
    }

    /**
     * 全部血盟成員數量
     *
     * @return
     */
    public int getAllMembersSize() {
        try {
            return _membersNameList.size();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return 0;
    }

    /**
     * 對血盟線上成員發送封包
     */
    public void sendPacketsAll(ServerBasePacket packet) {
        try {
            for (Object nameobj : _membersNameList.toArray()) {
                String name = (String) nameobj;
                L1PcInstance pc = World.get().getPlayer(name);
                if (pc != null) {
                    pc.sendPackets(packet);
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
    /**
     * 血盟線上成員名單(包含階級)
     *
     * @return
     */
	/*public StringBuilder getOnlineMembersFPWithRank() {
		// 清單緩存
		final ArrayList<String> temp = new ArrayList<String>();
		// 輸出名單
		final StringBuilder result = new StringBuilder();
		try {
			temp.addAll(this._membersNameList);
			for (final String name : temp) {
				final L1PcInstance pc = World.get().getPlayer(name);
				if (pc != null) {
					result.append(name + this.getRankString(pc) + " ");
				}
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return result;
	}*/
    /**
     * 全部血盟成員名單(包含離線)
     *
     * @return
     */
	/*public StringBuilder getAllMembersFPWithRank() {
		// 清單緩存
		final ArrayList<String> temp = new ArrayList<String>();
		// 輸出名單
		final StringBuilder result = new StringBuilder();
		try {
			temp.addAll(this._membersNameList);
			for (final String name : temp) {
				final L1PcInstance pc = CharacterTable.get().restoreCharacter(name);
				if (pc != null) {
					result.append(name + " ");
				}
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return result;
	}*/
    /**
     * 血盟階級
     *
     * @param pc
     * @return
     */
	/*private String getRankString(L1PcInstance pc) {
		if ((pc != null) && (pc.getClanRank() > 0)) {
			return _rank[pc.getClanRank()];
		}
		return "";
	}*/

    /**
     * 血盟線上成員
     *
     * @return
     */
    public L1PcInstance[] getOnlineClanMember() {
        // 清單緩存
        final ArrayList<String> temp = new ArrayList<String>();
        // 輸出清單
        final ArrayList<L1PcInstance> onlineMembers = new ArrayList<L1PcInstance>();
        try {
            temp.addAll(this._membersNameList);
            for (final String name : temp) {
                final L1PcInstance pc = World.get().getPlayer(name);
                if ((pc != null) && !onlineMembers.contains(pc)) {
                    onlineMembers.add(pc);
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return onlineMembers.toArray(new L1PcInstance[onlineMembers.size()]);
    }

    /**
     * 血盟線上成員名單
     *
     * @return
     */
    public StringBuilder getOnlineMembersFP() {
        // 清單緩存
        final ArrayList<String> temp = new ArrayList<String>();
        // 輸出名單
        final StringBuilder result = new StringBuilder();
        try {
            temp.addAll(this._membersNameList);
            for (final String name : temp) {
                final L1PcInstance pc = World.get().getPlayer(name);
                if (pc != null) {
                    result.append(name + " ");
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return result;
    }

    /**
     * 全部血盟成員名單(包含離線)
     *
     * @return
     */
    public StringBuilder getAllMembersFP() {
        // 清單緩存
        final ArrayList<String> temp = new ArrayList<String>();
        // 輸出名單
        final StringBuilder result = new StringBuilder();
        try {
            temp.addAll(this._membersNameList);
            for (final String name : temp) {
                result.append(name + " ");
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return result;
    }

    public int getEmblemId() {
        return _emblemId;
    }

    public void setEmblemId(int i) {
        _emblemId = i;
    }

    /**
     * 全部血盟成員姓名清單
     *
     * @return
     */
    public String[] getAllMembers() {
        return (String[]) _membersNameList.toArray(new String[_membersNameList.size()]);
    }

    /**
     * 血盟倉庫資料
     *
     * @return
     */
    public L1DwarfForClanInventory getDwarfForClanInventory() {
        return _dwarfForClan;
    }

    public int getWarehouseUsingChar() {// 血盟倉庫目前使用者
        return _warehouse;
    }

    public void setWarehouseUsingChar(int objid) {
        _warehouse = objid;
    }

    // 血盟技能
    public void set_clanskill(boolean boolean1) {
        _clanskill = boolean1;
    }

    public boolean isClanskill() {
        return _clanskill;
    }

    public int getClanSkillId() {
        return this._ClanSkillId;
    }

    //private Timestamp _foundDate;
    //private String _announcement;
    //private int _emblemStatus;
    //public Timestamp getFoundDate() {
    //return _foundDate;
    //}
    //public void setFoundDate(Timestamp _foundDate) {
    //this._foundDate = _foundDate;
    //}
    public void setClanSkillId(int i) {
        this._ClanSkillId = i;
    }

    public int getClanSkillLv() {
        return this._ClanSkillLv;
    }

    //public String getAnnouncement() {
    //return _announcement;
    //}
    //public void setAnnouncement(String announcement) {
    //this._announcement = announcement;
    //}
    //public int getEmblemStatus() {
    //return _emblemStatus;
    //}
    //public void setEmblemStatus(int emblemStatus) {
    //this._emblemStatus = emblemStatus;
    //}
    public void setClanSkillLv(int i) {
        this._ClanSkillLv = i;
    }

    public Timestamp get_skilltime() {
        return _skilltime;
    }

    public void set_skilltime(Timestamp skilltime) {
        _skilltime = skilltime;
    }

    public int getOnlineMaxUser() {
        return _maxuser;
    }

    public void setOnlineMaxUser(int i) {
        _maxuser = i;
    }

    public final short getLoginLevel() {
        return _loginLevel;
    }

    public void setLoginLevel(final short s) {
        _loginLevel = s;
    }

    public void addAlliance(int i) {
        if (i == 0) {
            return;
        }
        if (!allianceList.contains((Integer) i)) {
            allianceList.add((Integer) i);
        }
    }

    public void removeAlliance(int i) {
        if (i == 0) {
            return;
        }
        if (allianceList.contains((Integer) i)) {
            allianceList.remove((Integer) i);
        }
    }

    public Integer[] Alliance() {
        Integer[] i = (Integer[]) allianceList.toArray(new Integer[allianceList.size()]);
        return i;
    }

    public int AllianceSize() {
        return allianceList.size();
    }

    public void AllianceDelete() {
        if (allianceList.size() > 0) {
            allianceList.clear();
        }
    }

    public L1Clan getAlliance(int i) {
        if (allianceList.size() > 0) {
            for (int id : allianceList) {
                if (id == i) {
                    return WorldClan.get().getClan(i);
                }
            }
        }
        return null;
    }

    public String getClanShowNote() {
        return _clanshowNote;
    }

    public void setClanShowNote(final String text) {
        _clanshowNote = text;
    }

    public Date getBirthDay() {
        Date date = new Date();
        try {
            final L1PcInstance pc = CharacterTable.get().restoreCharacter(_leaderName);
            date = pc.getBirthDay();
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            return date;
            // _log.error(e.getLocalizedMessage(), e);;
        }
        return date;
    }

    /**
     * 全部血盟成員名單(包含離線)
     *
     * @return
     */
    public ArrayList<L1PcInstance> getAllMembersRank() {
        // 清單緩存
        final ArrayList<String> temp = new ArrayList<String>();
        // 輸出名單
        final ArrayList<L1PcInstance> result = new ArrayList<L1PcInstance>();
        try {
            temp.addAll(_membersNameList);
            for (final String name : temp) {
                L1PcInstance pc = World.get().getPlayer(name);
                if (pc == null) {
                    pc = CharacterTable.get().restoreCharacter(name);
                }
                if (pc != null) {
                    result.add(pc);
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return result;
    }

    public String getClanNote() {
        return _clanNote;
    }

    public void setClanNote(final String text) {
        _clanNote = text;
    }

    /**
     * 是否顯示盟徽
     */
    public int getShowEmblem() {
        return showEmblem;
    }

    /**
     * 設置是否顯示盟徽
     */
    public void setShowEmblem(int showEmblem) {
        this.showEmblem = showEmblem;
    }

    /**
     * 是否加入血盟
     */
    public boolean getJoin_open_state() {
        return join_open_state;
    }

    /**
     * 是否加入血盟
     */
    public void setJoin_open_state(boolean join_open_state) {
        this.join_open_state = join_open_state;
    }

    /**
     * 血盟加入種類 0：即時加入，1：允許加入，2：使用密碼加入
     */
    public int getJoin_state() {
        return join_state;
    }

    /**
     * 血盟加入種類 0：即時加入，1：允許加入，2：使用密碼加入
     */
    public void setJoin_state(int join_state) {
        this.join_state = join_state;
    }

    /**
     * 加入血盟密碼
     */
    public String getJoin_password() {
        return join_password;
    }

    /**
     * 加入血盟密碼
     */
    public void setJoin_password(String join_password) {
        this.join_password = join_password;
    }
}