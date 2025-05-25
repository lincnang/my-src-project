package com.lineage.server.datatables.sql;

import com.lineage.DatabaseFactory;
import com.lineage.server.IdFactory;
import com.lineage.server.datatables.storage.ClanStorage;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Clan;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.WorldClan;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ClanTable implements ClanStorage {
    private static final Log _log = LogFactory.getLog(ClanTable.class);
    private final Map<Integer, L1Clan> _clans = new HashMap<Integer, L1Clan>();

    /**
     * 預先加載血盟資料
     */
    @Override
    public void load() {
        PerformanceTimer timer = new PerformanceTimer();
        Connection cn = null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null; // added by terry0412
        ResultSet rs = null;
        ResultSet rs2 = null; // added by terry0412
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("SELECT * FROM `clan_data` ORDER BY `clan_id`");
            rs = ps.executeQuery();
            while (rs.next()) {
                L1Clan clan = new L1Clan();
                final int clan_id = rs.getInt("clan_id");
                clan.setClanId(clan_id);
                clan.setClanName(rs.getString("clan_name"));
                clan.setLeaderId(rs.getInt("leader_id"));
                clan.setLeaderName(rs.getString("leader_name"));
                clan.setCastleId(rs.getInt("has_castle"));
                clan.setHouseId(rs.getInt("has_house"));
                //clan.setFoundDate(rs.getTimestamp("found_date"));
                clan.setEmblemId(rs.getInt("emblem_id"));
                // 7.6
                clan.setShowEmblem(rs.getInt("showEmblem"));
                clan.setJoin_open_state(rs.getBoolean("join_open_state"));
                clan.setJoin_state(rs.getInt("join_state"));
                clan.setJoin_password(rs.getString("join_password"));
                boolean clanskill = rs.getBoolean("clanskill");
                // 具有血盟技能
                if (clanskill) {
                    clan.set_clanskill(clanskill);
                    final Timestamp skilltime = rs.getTimestamp("skilltime");
                    clan.set_skilltime(skilltime);
                }
                clan.setClanNote("clannote");
                final String shownote = rs.getString("clanshownote");
                clan.setClanShowNote(shownote);
                //clan.setAnnouncement(rs.getString("announcement"));
                //clan.setEmblemStatus(rs.getInt("emblem_status"));
                ps2 = cn.prepareStatement("SELECT COUNT(*) FROM `characters` WHERE `ClanID`=? AND `LastLogin`>?");
                ps2.setInt(1, clan_id);
                ps2.setTimestamp(2, new Timestamp(System.currentTimeMillis() - 604800000));
                rs2 = ps2.executeQuery();
                if (rs2.next()) {
                    clan.setLoginLevel(rs2.getShort(1));
                }
                clan.setClanSkillId(rs.getInt("clanskill_id"));
                clan.setClanSkillLv(rs.getInt("clanskill_lv"));
                clan.addAlliance(rs.getInt("alliance1"));
                clan.addAlliance(rs.getInt("alliance2"));
                clan.addAlliance(rs.getInt("alliance3"));
                clan.addAlliance(rs.getInt("alliance4"));
                WorldClan.get().storeClan(clan);
                //_clans.put(clan.getClanId(), clan);
                _clans.put(clan_id, clan);
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs2); // added by terry0412
            SQLUtil.close(rs);
            SQLUtil.close(ps2); // added by terry0412
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        _log.info("讀取->血盟資料資料數量: " + _clans.size() + "(" + timer.get() + "ms)");
        // 加入血盟人員名稱清單
        final Collection<L1Clan> AllClan = WorldClan.get().getAllClans();
        for (final L1Clan clan : AllClan) {
            try {
                cn = DatabaseFactory.get().getConnection();
                ps = cn.prepareStatement("SELECT `char_name` FROM `characters` WHERE `ClanID`=?");
                ps.setInt(1, clan.getClanId());
                rs = ps.executeQuery();
                while (rs.next()) {
                    clan.addMemberName(rs.getString("char_name"));
                }
            } catch (final SQLException e) {
                _log.error(e.getLocalizedMessage(), e);
            } finally {
                SQLUtil.close(rs);
                SQLUtil.close(ps);
                SQLUtil.close(cn);
            }
        }
        // 加載血盟倉庫資料
        for (final L1Clan clan : AllClan) {
            clan.getDwarfForClanInventory().loadItems();
        }
    }

    /**
     * 加入虛擬血盟資料
     */
    public void addDeClan(Integer integer, L1Clan l1Clan) {
        WorldClan.get().storeClan(l1Clan);
        _clans.put(integer, l1Clan);
    }

    /**
     * 建立血盟資料
     *
     */
    @Override
    public L1Clan createClan(L1PcInstance player, String clan_name) {
        final Collection<L1Clan> allClans = WorldClan.get().getAllClans();
        for (final Iterator<L1Clan> iter = allClans.iterator(); iter.hasNext(); ) {
            final L1Clan oldClans = iter.next();
            if (oldClans.getClanName().equalsIgnoreCase(clan_name)) {
                return null;
            }
        }
        L1Clan clan = new L1Clan();
        clan.setClanId(IdFactory.get().nextId());
        clan.setClanName(clan_name);
        clan.setLeaderId(player.getId());
        clan.setLeaderName(player.getName());
        clan.setCastleId(0);
        clan.setHouseId(0);
        //clan.setFoundDate(player.getCreateTime());
        //clan.setEmblemId(0);
        clan.setEmblemId(clan.getClanId());
        clan.set_clanskill(false);
        //clan.setAnnouncement("");
        //clan.setEmblemStatus(0);
        //clan.setShowEmblem(0);
        clan.setClanNote("無");
        clan.setClanShowNote("盟主很懶，什麼都沒有留下！");
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("INSERT INTO clan_data SET clan_id=?, clan_name=?, "
                    //+ "leader_id=?, leader_name=?, has_castle=?, has_house=?, found_date=?, "
                    //+ "emblem_id=?, clanskill=?, skilltime=?, announcement=?, emblem_status=?");
                    + "leader_id=?, leader_name=?, has_castle=?, has_house=?, " + "emblem_id=?, clanskill=?, skilltime=?, clannote=?, clanshownote=?, join_open_state=?, join_state=?, join_password=?");
            pstm.setInt(1, clan.getClanId());
            pstm.setString(2, clan.getClanName());
            pstm.setInt(3, clan.getLeaderId());
            pstm.setString(4, clan.getLeaderName());
            pstm.setInt(5, clan.getCastleId());
            pstm.setInt(6, clan.getHouseId());
            //pstm.setTimestamp(7, player.getCreateTime());
            pstm.setInt(7, 0);
            pstm.setBoolean(8, clan.isClanskill());
            pstm.setTimestamp(9, clan.get_skilltime());
            pstm.setString(10, "");
            pstm.setString(11, "");
            pstm.setBoolean(12, clan.getJoin_open_state());// 7.6
            pstm.setInt(13, clan.getJoin_state());// 7.6
            pstm.setString(14, clan.getJoin_password());// 7.6
            //pstm.setInt(16, 0);
            pstm.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        WorldClan.get().storeClan(clan);
        _clans.put(Integer.valueOf(clan.getClanId()), clan);
        player.setClanid(clan.getClanId());
        player.setClanname(clan.getClanName());
        player.setClanRank(L1Clan.CLAN_RANK_PRINCE);
        // player.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED,
        // L1Clan.CLAN_RANK_PRINCE, player.getName())); // 你的階級變更為%s
        clan.addMemberName(player.getName());
        try {
            player.save();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return clan;
    }

    /**
     * 更新血盟資料
     */
    public void updateClan(L1Clan clan) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE clan_data SET clan_id=?, leader_id=?, leader_name=?,"
                    //+ " has_castle=?, has_house=?, found_date=?, emblem_id=?, clanskill=?,"
                    //+ " skilltime=?, announcement=?, emblem_status=?, "
                    //+ " alliance1=?, alliance2=?, alliance3=?, alliance4=? WHERE clan_name=?");
                    + " has_castle=?, has_house=?, emblem_id=?, clanskill=?," + " skilltime=?, clannote=?, clanshownote=? ,join_open_state=?, join_state=?, join_password=?, " + " alliance1=?, alliance2=?, alliance3=?, alliance4=?, showEmblem=? WHERE clan_name=?");
            pstm.setInt(1, clan.getClanId());
            pstm.setInt(2, clan.getLeaderId());
            pstm.setString(3, clan.getLeaderName());
            pstm.setInt(4, clan.getCastleId());
            pstm.setInt(5, clan.getHouseId());
            //pstm.setTimestamp(6, clan.getFoundDate());
            pstm.setInt(6, clan.getEmblemId());
            pstm.setBoolean(7, clan.isClanskill());
            pstm.setTimestamp(8, clan.get_skilltime());
            //pstm.setString(10, clan.getAnnouncement());
            //pstm.setInt(11, clan.getEmblemStatus());
            System.out.println("存儲公告：" + clan.getClanShowNote());
            pstm.setString(9, clan.getClanNote());
            pstm.setString(10, clan.getClanShowNote());
            pstm.setBoolean(11, clan.getJoin_open_state());// 7.6
            pstm.setInt(12, clan.getJoin_state());// 7.6
            pstm.setString(13, clan.getJoin_password());// 7.6
            int count = 14;
            for (int i : clan.Alliance()) {
                pstm.setInt(count, i);
                count++;
            }
            if (count < 18) {
                for (; count < 18; count++) {
                    pstm.setInt(count, 0);
                }
            }
            pstm.setInt(18, clan.getShowEmblem());// 是否顯示盟徽 頭頂
            pstm.setString(19, clan.getClanName());
            pstm.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    /**
     * 刪除血盟資料
     */
    public void deleteClan(String clan_name) {
        L1Clan clan = WorldClan.get().getClan(clan_name);
        if (clan == null) {
            return;
        }
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("DELETE FROM `clan_data` WHERE `clan_name`=?");
            ps.setString(1, clan_name);
            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
        clan.getDwarfForClanInventory().clearItems();
        clan.getDwarfForClanInventory().deleteAllItems();
        WorldClan.get().removeClan(clan);
        _clans.remove(Integer.valueOf(clan.getClanId()));
    }

    public void updateClanSkill(L1Clan clan) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = DatabaseFactory.get().getConnection();
            ps = cn.prepareStatement("UPDATE clan_data SET `clanskill_id`=?,`clanskill_lv`=? WHERE `clan_id`=?");
            ps.setInt(1, clan.getClanSkillId());
            ps.setInt(2, clan.getClanSkillLv());
            ps.setInt(3, clan.getClanId());
            ps.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(ps);
            SQLUtil.close(cn);
        }
    }

    public L1Clan getTemplate(int clan_id) {
        return (L1Clan) _clans.get(Integer.valueOf(clan_id));
    }

    public Map<Integer, L1Clan> get_clans() {
        return _clans;
    }
}
