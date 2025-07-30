package com.lineage.server.storage.mysql;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.lock.CharItemsReading;
import com.lineage.server.datatables.lock.CharOtherReading;
import com.lineage.server.datatables.lock.CharOtherReading1;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.storage.CharacterStorage;
import com.lineage.server.templates.L1PcOther;
import com.lineage.server.templates.L1PcOther1;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class MySqlCharacterStorage implements CharacterStorage {//src013
    private static final Log _log = LogFactory.getLog(MySqlCharacterStorage.class);

    public L1PcInstance loadCharacter(String charName) {
        L1PcInstance pc = null;
        Connection con = null;
        PreparedStatement pstm = null;
        PreparedStatement pstm2 = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM characters WHERE char_name=?");
            pstm.setString(1, charName);
            rs = pstm.executeQuery();
            if (!rs.next()) {
                return null;
            }
            pc = new L1PcInstance();

            String loginName = rs.getString("account_name").toLowerCase();
            pc.setAccountName(loginName);
            int objid = rs.getInt("objid");
            pc.setId(objid);
            pc.set_showId(-1);
            L1PcOther other = CharOtherReading.get().getOther(pc);
            if (other == null) {
                other = new L1PcOther();
                other.set_objid(objid);
            }
            pc.set_other(other);
            L1PcOther1 other2 = CharOtherReading1.get().getOther(pc);
            if (other2 == null) {
                other2 = new L1PcOther1();
                other2.set_objid(objid);
            }
            pc.set_other1(other2);
            pc.setName(rs.getString("char_name"));
            pc.setHighLevel(rs.getInt("HighLevel"));
            pc.setExp(rs.getLong("Exp"));
            pc.addBaseMaxHp(rs.getShort("MaxHp"));
            short currentHp = rs.getShort("CurHp");
            if (currentHp < 1) {
                currentHp = 1;
            }
            pc.setDead(false);
            pc.setCurrentHpDirect(currentHp);
            pc.setStatus(0);
            pc.addBaseMaxMp(rs.getShort("MaxMp"));
            pc.setCurrentMpDirect(rs.getShort("CurMp"));
            pc.addBaseStr(rs.getInt("Str"));
            pc.addBaseCon(rs.getInt("Con"));
            pc.addBaseDex(rs.getInt("Dex"));
            pc.addBaseCha(rs.getInt("Cha"));
            pc.addBaseInt(rs.getInt("Intel"));
            pc.addBaseWis(rs.getInt("Wis"));
            int status = rs.getInt("Status");
            pc.setCurrentWeapon(status);
            int classId = rs.getInt("Class");
            pc.setClassId(classId);
            pc.setTempCharGfx(classId);
            pc.setGfxId(classId);
            pc.set_sex(rs.getInt("Sex"));
            pc.setType(rs.getInt("Type"));
            int head = rs.getInt("Heading");
            if (head > 7) {
                head = 0;
            }
            pc.setHeading(head);
            pc.setX(rs.getInt("locX"));
            pc.setY(rs.getInt("locY"));
            pc.setMap(rs.getShort("MapID"));
            pc.set_food(rs.getInt("Food"));
            pc.setLawful(rs.getInt("Lawful"));
            pc.setTitle(rs.getString("Title"));
            pc.setClanid(rs.getInt("ClanID"));
            pc.setClanname(rs.getString("Clanname"));
            pc.setClanRank(rs.getInt("ClanRank"));
            pc.setRejoinClanTime(rs.getTimestamp("rejoin_clan_time"));
            pc.setBonusStats(rs.getInt("BonusStatus"));
            pc.setElixirStats(rs.getInt("ElixirStatus"));
            pc.setElfAttr(rs.getInt("ElfAttr"));
            pc.set_PKcount(rs.getInt("PKcount"));
            pc.setPkCountForElf(rs.getInt("PkCountForElf"));
            pc.setExpRes(rs.getInt("ExpRes"));
            pc.setPartnerId(rs.getInt("PartnerID"));
            pc.setAccessLevel(rs.getShort("AccessLevel"));
            if (pc.getAccessLevel() >= 200) {
                pc.setGm(true);
                pc.setMonitor(false);
            } else if (pc.getAccessLevel() == 100) {
                pc.setGm(false);
                pc.setMonitor(true);
            } else {
                pc.setGm(false);
                pc.setMonitor(false);
            }
            pc.setOnlineStatus(rs.getInt("OnlineStatus"));
            pc.setHomeTownId(rs.getInt("HomeTownID"));
            pc.setContribution(rs.getInt("Contribution"));
            pc.setPay(rs.getInt("Pay"));
            pc.setHellTime(rs.getInt("HellTime"));
            pc.setBanned(rs.getBoolean("Banned"));
            pc.setKarma(rs.getInt("Karma"));
            pc.setLastPk(rs.getTimestamp("LastPk"));
            pc.setLastPkForElf(rs.getTimestamp("LastPkForElf"));
            pc.setDeleteTime(rs.getTimestamp("DeleteTime"));
            pc.setOriginalStr(rs.getInt("OriginalStr"));
            pc.setOriginalCon(rs.getInt("OriginalCon"));
            pc.setOriginalDex(rs.getInt("OriginalDex"));
            pc.setOriginalCha(rs.getInt("OriginalCha"));
            pc.setOriginalInt(rs.getInt("OriginalInt"));
            pc.setOriginalWis(rs.getInt("OriginalWis"));
            //pc.setCreateTime(rs.getTimestamp("CreateTime"));
            pc.setBirthday(rs.getString("CreateTime"));// 7.6
            pc.setClanMemberNotes(rs.getString("ClanMemberNotes"));// 7.6血盟個人備註
            pc.setMeteLevel(rs.getInt("MeteLevel"));
            pc.setTurnLifeSkillCount(rs.getByte("ReincarnationSkillCount")); // 轉生天賦
            pc.setPunishTime(rs.getTimestamp("PunishTime"));
            pc.setBanError(rs.getInt("BanError"));
            pc.setInputBanError(rs.getInt("InputBanError"));
            pc.setSpeedError(rs.getInt("SpeedError"));
            pc.setRocksPrisonTime(rs.getInt("RocksPrisonTime"));
            pc.setIvoryTowerTime(rs.getInt("IvorytowerTime"));
            pc.setLastabardTime(rs.getInt("LastabardTime"));
            pc.setDragonValleyTime(rs.getInt("DragonValleyTime"));
            pc.setMazuTime(rs.getInt("MazuTime"));
            pc.setAITimer(rs.getInt("AI_TIMES")); // 特效驗證系統
            pc.setTamTime(rs.getTimestamp("TamEndTime")); // 成長果實系統(Tam幣)
            pc.setMark_count(rs.getInt("Mark_Count")); // 日版記憶座標
            pc.setOnlineGiftIndex(rs.getInt("OnlineGiftIndex"));
            pc.setOnlineGiftWiatEnd(rs.getBoolean("OnlineGiftWiatEnd"));
            pc.setVipStartTime(rs.getTimestamp("VipStartTime"));
            pc.setVipEndTime(rs.getTimestamp("VipEndTime"));
            pc.set_vipLevel(rs.getInt("VipLevel"));
            //額外重置能力
            pc.setOtherStats(rs.getInt("OtherStatus"));
            pc.setAddPoint(rs.getInt("AddPoint"));
            pc.setDelPoint(rs.getInt("DelPoint"));
            pc.setRingsExpansion(rs.getByte("RingsExpansion"));
            pc.setEarringsExpansion(rs.getByte("EarringsExpansion"));
            pc.setEquipmentIndexAmulet(rs.getByte("EquipmentIndexAmulet")); // src1003 手環擴充紀錄
            pc.setRedblueReward(rs.getByte("RedblueReward"));
            pc.setPaCha(rs.getInt("pacha"));//萬能魅力
            pc.setPaCon(rs.getInt("pacon"));//萬能體質
            pc.setPaDex(rs.getInt("padex"));//萬能敏捷
            pc.setPaInt(rs.getInt("paint"));//萬能智力
            pc.setPaStr(rs.getInt("pastr"));//萬能力量
            pc.setPaWis(rs.getInt("pawis"));//萬能精神
            pc.setWyType1(rs.getInt("紋樣類型1"));
            pc.setWyLevel1(rs.getInt("紋樣等級1"));
            pc.setWyType2(rs.getInt("紋樣類型2"));
            pc.setWyLevel2(rs.getInt("紋樣等級2"));
            pc.setWyType3(rs.getInt("紋樣類型3"));
            pc.setWyLevel3(rs.getInt("紋樣等級3"));
            pc.setWyType4(rs.getInt("紋樣類型4"));
            pc.setWyLevel4(rs.getInt("紋樣等級4"));
            pc.setWyType5(rs.getInt("紋樣類型5"));
            pc.setWyLevel5(rs.getInt("紋樣等級5"));
            pc.setWyType6(rs.getInt("紋樣類型6"));
            pc.setWyLevel6(rs.getInt("紋樣等級6"));
            pc.setWenyangJiFen(rs.getInt("紋樣積分"));
            pc.setLastDollId(rs.getInt("娃娃召喚紀錄"));
            pc.setLastHolyId2(rs.getInt("聖物召喚紀錄"));
            pc.set_day_signature(rs.getInt("簽到紀錄"));
            pc.set_day_signature_time(rs.getTimestamp("簽到日期紀錄"));
            pc.setHonor(rs.getInt("Honor"));//聲望
            pc.setHonorLevel(rs.getInt("HonorLevel"));//聲望
            pc.setLastPolyCardId(rs.getInt("自動變身紀錄"));
            // 🔥 新增防呆
            if (pc.getHonorLevel() < 0) {
                pc.setHonorLevel(0);
            }
            if (pc.getHonor() < 0) {
                pc.setHonor(0);
            }
            rs.close();
            pc.refresh();
            pc.setMoveSpeed(0);
            pc.setBraveSpeed(0);
            pc.setGmInvis(false);
            if (pc.getClanid() > 0) {
                pstm2 = con.prepareStatement("SELECT * FROM clan_members WHERE char_id=?");
                pstm2.setInt(1, pc.getId());
                rs = pstm2.executeQuery();
                if (!rs.next()) {
                    return null;
                }
                pc.setClanMemberId(rs.getInt("index_id"));
                pc.setClanMemberNotes(rs.getString("notes"));
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
            return null;
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        SQLUtil.close(rs);
        SQLUtil.close(pstm);
        SQLUtil.close(con);
        return pc;
    }

    public void createCharacter(L1PcInstance pc) throws InterruptedException {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            int i = 0;
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("INSERT INTO characters SET " + "account_name=?,objid=?,char_name=?,level=?,HighLevel=?,Exp=?,MaxHp=?,CurHp=?,MaxMp=?,CurMp=?,Ac=?,Str=?,Con=?,Dex=?,Cha=?,Intel=?,Wis=?,Status=?,Class=?,Sex=?,Type=?,Heading=?,LocX=?,LocY=?,MapID=?,Food=?,Lawful=?,Title=?,ClanID=?,Clanname=?,ClanRank=?,rejoin_clan_time=?,BonusStatus=?,ElixirStatus=?,ElfAttr=?,PKcount=?,PkCountForElf=?,ExpRes=?,PartnerID=?,AccessLevel=?,OnlineStatus=?,HomeTownID=?,Contribution=?,Pay=?,HellTime=?,Banned=?,Karma=?,LastPk=?,LastPkForElf=?,DeleteTime=?,CreateTime=?,ClanMemberNotes=?,MeteLevel=?,ReincarnationSkillCount=?,PunishTime=?,BanError=?,InputBanError=?,SpeedError=?,RocksPrisonTime=?,IvorytowerTime=?,LastabardTime=?,DragonValleyTime=?,MazuTime=?,AI_TIMES=?,TamEndTime=?,Mark_Count=?,pacha=?,pacon=?,padex=?,paint=?,pastr=?,pawis=?,紋樣類型1=?,紋樣等級1=?,紋樣類型2=?,紋樣等級2=?,紋樣類型3=?,紋樣等級3=?,紋樣類型4=?,紋樣等級4=?,紋樣類型5=?,紋樣等級5=?,紋樣類型6=?,紋樣等級6=?,紋樣積分=?,娃娃召喚紀錄=?,聖物召喚紀錄=?,簽到紀錄=?,簽到日期紀錄=?,Honor=?,HonorLevel=?,自動變身紀錄=?");
            pstm.setString(++i, pc.getAccountName());
            pstm.setInt(++i, pc.getId());
            pstm.setString(++i, pc.getName());
            pstm.setInt(++i, pc.getLevel());
            pstm.setInt(++i, pc.getHighLevel());
            pstm.setLong(++i, pc.getExp());
            pstm.setInt(++i, pc.getBaseMaxHp());
            int hp = pc.getCurrentHp();
            if (hp < 1) {
                hp = 1;
            }
            pstm.setInt(++i, hp);
            pstm.setInt(++i, pc.getBaseMaxMp());
            pstm.setInt(++i, pc.getCurrentMp());
            pstm.setInt(++i, pc.getAc());
            pstm.setInt(++i, pc.getBaseStr());
            pstm.setInt(++i, pc.getBaseCon());
            pstm.setInt(++i, pc.getBaseDex());
            pstm.setInt(++i, pc.getBaseCha());
            pstm.setInt(++i, pc.getBaseInt());
            pstm.setInt(++i, pc.getBaseWis());
            pstm.setInt(++i, pc.getCurrentWeapon());
            pstm.setInt(++i, pc.getClassId());
            pstm.setInt(++i, pc.get_sex());
            pstm.setInt(++i, pc.getType());
            pstm.setInt(++i, pc.getHeading());
            pstm.setInt(++i, pc.getX());
            pstm.setInt(++i, pc.getY());
            pstm.setInt(++i, pc.getMapId());
            pstm.setInt(++i, pc.get_food());
            pstm.setInt(++i, pc.getLawful());
            pstm.setString(++i, pc.getTitle());
            pstm.setInt(++i, pc.getClanid());
            pstm.setString(++i, pc.getClanname());
            pstm.setInt(++i, pc.getClanRank());
            pstm.setTimestamp(++i, pc.getRejoinClanTime());
            pstm.setInt(++i, pc.getBonusStats());
            pstm.setInt(++i, pc.getElixirStats());
            pstm.setInt(++i, pc.getElfAttr());
            pstm.setInt(++i, pc.get_PKcount());
            pstm.setInt(++i, pc.getPkCountForElf());
            pstm.setInt(++i, pc.getExpRes());
            pstm.setInt(++i, pc.getPartnerId());
            short accesslevel = pc.getAccessLevel();
            if (accesslevel > 200) {
                accesslevel = 0;
            }
            pstm.setShort(++i, accesslevel);
            pstm.setInt(++i, pc.getOnlineStatus());
            pstm.setInt(++i, pc.getHomeTownId());
            pstm.setInt(++i, pc.getContribution());
            pstm.setInt(++i, pc.getPay());
            pstm.setInt(++i, pc.getHellTime());
            pstm.setBoolean(++i, pc.isBanned());
            pstm.setInt(++i, pc.getKarma());
            pstm.setTimestamp(++i, pc.getLastPk());
            pstm.setTimestamp(++i, pc.getLastPkForElf());
            pstm.setTimestamp(++i, pc.getDeleteTime());

            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            final String times = sdf.format(System.currentTimeMillis());
            int time = Integer.parseInt(times.replace("-", ""));
            pstm.setInt(++i, time);

            pstm.setString(++i, pc.getClanMemberNotes());// 7.6血盟個人備註
            pstm.setInt(++i, pc.getMeteLevel());
            pstm.setInt(++i, pc.getTurnLifeSkillCount()); // 轉生天賦
            pstm.setTimestamp(++i, pc.getPunishTime());
            pstm.setInt(++i, pc.getBanError());
            pstm.setInt(++i, pc.getInputBanError());
            pstm.setInt(++i, pc.getSpeedError());
            pstm.setInt(++i, pc.getRocksPrisonTime());
            pstm.setInt(++i, pc.getIvoryTowerTime());
            pstm.setInt(++i, pc.getLastabardTime());
            pstm.setInt(++i, pc.getDragonValleyTime());
            pstm.setInt(++i, pc.getMazuTime());
            pstm.setInt(++i, pc.getAITimer()); // 特效驗證系統
            pstm.setTimestamp(++i, pc.getTamTime()); // 成長果實系統(Tam幣)
            pstm.setInt(++i, pc.getMark_count()); // 日版記憶座標

            // 新增屬性
            pstm.setInt(++i, pc.getPaCha());
            pstm.setInt(++i, pc.getPaCon());
            pstm.setInt(++i, pc.getPaDex());
            pstm.setInt(++i, pc.getPaInt());
            pstm.setInt(++i, pc.getPaStr());
            pstm.setInt(++i, pc.getPaWis());

            // 紋樣
            pstm.setInt(++i, pc.getWyType1());
            pstm.setInt(++i, pc.getWyLevel1());
            pstm.setInt(++i, pc.getWyType2());
            pstm.setInt(++i, pc.getWyLevel2());
            pstm.setInt(++i, pc.getWyType3());
            pstm.setInt(++i, pc.getWyLevel3());
            pstm.setInt(++i, pc.getWyType4());
            pstm.setInt(++i, pc.getWyLevel4());
            pstm.setInt(++i, pc.getWyType5());
            pstm.setInt(++i, pc.getWyLevel5());
            pstm.setInt(++i, pc.getWyType6());
            pstm.setInt(++i, pc.getWyLevel6());

            // 其餘
            pstm.setInt(++i, pc.getWenyangJiFen());
            pstm.setInt(++i, pc.getLastDollId());
            pstm.setInt(++i, pc.getLastHolyId2());
            pstm.setInt(++i, pc.get_day_signature());
            pstm.setTimestamp(++i, pc.get_day_signature_time());
            pstm.setInt(++i, pc.getHonor());
            pstm.setInt(++i, pc.getHonorLevel());
            pstm.setInt(++i, pc.getLastPolyCardId()); // ← 新增
            pstm.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    @SuppressWarnings("resource")
    public void deleteCharacter(String accountName, String charName) throws Exception {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM characters WHERE account_name=? AND char_name=?");
            pstm.setString(1, accountName);
            pstm.setString(2, charName);
            rs = pstm.executeQuery();
            if (!rs.next()) {
                throw new RuntimeException("could not delete character");
            }
            int objid = CharObjidTable.get().charObjid(charName);
            if (objid != 0) {
                CharItemsReading.get().delUserItems(objid);
            }
            pstm = con.prepareStatement("DELETE FROM character_buddys WHERE char_id IN (SELECT objid FROM characters WHERE char_name = ?)");
            pstm.setString(1, charName);
            pstm.execute();
            pstm = con.prepareStatement("DELETE FROM character_buff WHERE char_obj_id IN (SELECT objid FROM characters WHERE char_name = ?)");
            pstm.setString(1, charName);
            pstm.execute();
            pstm = con.prepareStatement("DELETE FROM character_config WHERE object_id IN (SELECT objid FROM characters WHERE char_name = ?)");
            pstm.setString(1, charName);
            pstm.execute();
            pstm = con.prepareStatement("DELETE FROM character_quests WHERE char_id IN (SELECT objid FROM characters WHERE char_name = ?)");
            pstm.setString(1, charName);
            pstm.execute();
            pstm = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id IN (SELECT objid FROM characters WHERE char_name = ?)");
            pstm.setString(1, charName);
            pstm.execute();
            pstm = con.prepareStatement("DELETE FROM character_teleport WHERE char_id IN (SELECT objid FROM characters WHERE char_name = ?)");
            pstm.setString(1, charName);
            pstm.execute();
            pstm = con.prepareStatement("DELETE FROM characters WHERE char_name=?");
            pstm.setString(1, charName);
            pstm.execute();
            pstm = con.prepareStatement("DELETE FROM clan_members WHERE char_id IN (SELECT objid FROM characters WHERE char_name = ?)");
            pstm.setString(1, charName);
            pstm.execute();
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    /* 更新VIP*/
    public void updateVipTime(L1PcInstance pc) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            int i = 0;
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE characters SET VipStartTime=?,VipEndTime=?,VipLevel=? WHERE objid=?");
            pstm.setTimestamp(++i, pc.getVipStartTime());
            pstm.setTimestamp(++i, pc.getVipEndTime());
            pstm.setInt(++i, pc.get_vipLevel());
            pstm.setInt(++i, pc.getId());
            pstm.execute();
            _log.info(pc.getName() + "----------VIP更新成功--------");
        } catch (SQLException e) {
            _log.error(pc.getName() + "----------VIP更新錯誤---------" + e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void storeCharacter(L1PcInstance pc) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            int i = 0;
            con = DatabaseFactory.get().getConnection();

            // ⚡ 1. 這裡逗號記得改掉！（WHERE前不可以有逗號）
            String sql = "UPDATE characters SET "
                    + "level=?,HighLevel=?,Exp=?,MaxHp=?,CurHp=?,MaxMp=?,CurMp=?,Ac=?,Str=?,Con=?,Dex=?,Cha=?,Intel=?,Wis=?,"
                    + "Status=?,Class=?,Sex=?,Type=?,Heading=?,LocX=?,LocY=?,MapID=?,Food=?,Lawful=?,Title=?,ClanID=?,Clanname=?,ClanRank=?,"
                    + "rejoin_clan_time=?,BonusStatus=?,ElixirStatus=?,ElfAttr=?,PKcount=?,PkCountForElf=?,ExpRes=?,PartnerID=?,AccessLevel=?,"
                    + "OnlineStatus=?,HomeTownID=?,Contribution=?,HellTime=?,Banned=?,Karma=?,LastPk=?,LastPkForElf=?,DeleteTime=?,"
                    + "ClanMemberNotes=?,MeteLevel=?,ReincarnationSkillCount=?,PunishTime=?,BanError=?,InputBanError=?,SpeedError=?,"
                    + "RocksPrisonTime=?,IvorytowerTime=?,LastabardTime=?,DragonValleyTime=?,MazuTime=?,AI_TIMES=?,TamEndTime=?,Mark_Count=?,"
                    + "OnlineGiftIndex=?,OnlineGiftWiatEnd=?,OtherStatus=?,AddPoint=?,DelPoint=?,pacha=?,pacon=?,padex=?,paint=?,pastr=?,pawis=?,"
                    + "紋樣類型1=?,紋樣等級1=?,紋樣類型2=?,紋樣等級2=?,紋樣類型3=?,紋樣等級3=?,紋樣類型4=?,紋樣等級4=?,"
                    + "紋樣類型5=?,紋樣等級5=?,紋樣類型6=?,紋樣等級6=?,紋樣積分=?,娃娃召喚紀錄=?,聖物召喚紀錄=?,"
                    + "簽到紀錄=?,簽到日期紀錄=?,Honor=?,HonorLevel=?,自動變身紀錄=?  "
                    + "WHERE objid=?"; // <-- 這裡前面逗號必須去除

            pstm = con.prepareStatement(sql);

            // ⚡ 2. 依序塞參數
            pstm.setInt(++i, pc.getLevel());
            pstm.setInt(++i, pc.getHighLevel());
            pstm.setLong(++i, pc.getExp());
            pstm.setInt(++i, pc.getBaseMaxHp());
            int hp = pc.getCurrentHp();
            if (hp < 1) hp = 1;
            pstm.setInt(++i, hp);
            pstm.setInt(++i, pc.getBaseMaxMp());
            pstm.setInt(++i, pc.getCurrentMp());
            pstm.setInt(++i, pc.getAc());
            pstm.setInt(++i, pc.getBaseStr());
            pstm.setInt(++i, pc.getBaseCon());
            pstm.setInt(++i, pc.getBaseDex());
            pstm.setInt(++i, pc.getBaseCha());
            pstm.setInt(++i, pc.getBaseInt());
            pstm.setInt(++i, pc.getBaseWis());
            pstm.setInt(++i, pc.getCurrentWeapon());
            pstm.setInt(++i, pc.getClassId());
            pstm.setInt(++i, pc.get_sex());
            pstm.setInt(++i, pc.getType());
            pstm.setInt(++i, pc.getHeading());
            pstm.setInt(++i, pc.getX());
            pstm.setInt(++i, pc.getY());
            pstm.setInt(++i, pc.getMapId());
            pstm.setInt(++i, pc.get_food());
            pstm.setInt(++i, pc.getLawful());
            pstm.setString(++i, pc.getTitle());
            pstm.setInt(++i, pc.getClanid());
            pstm.setString(++i, pc.getClanname());
            pstm.setInt(++i, pc.getClanRank());
            pstm.setTimestamp(++i, pc.getRejoinClanTime());
            pstm.setInt(++i, pc.getBonusStats());
            pstm.setInt(++i, pc.getElixirStats());
            pstm.setInt(++i, pc.getElfAttr());
            pstm.setInt(++i, pc.get_PKcount());
            pstm.setInt(++i, pc.getPkCountForElf());
            pstm.setInt(++i, pc.getExpRes());
            pstm.setInt(++i, pc.getPartnerId());
            short accesslevel = pc.getAccessLevel();
            if (accesslevel > 200) accesslevel = 0;
            pstm.setShort(++i, accesslevel);
            pstm.setInt(++i, pc.getOnlineStatus());
            pstm.setInt(++i, pc.getHomeTownId());
            pstm.setInt(++i, pc.getContribution());
            pstm.setInt(++i, pc.getHellTime());
            pstm.setBoolean(++i, pc.isBanned());
            pstm.setInt(++i, pc.getKarma());
            pstm.setTimestamp(++i, pc.getLastPk());
            pstm.setTimestamp(++i, pc.getLastPkForElf());
            pstm.setTimestamp(++i, pc.getDeleteTime());
            pstm.setString(++i, pc.getClanMemberNotes());
            pstm.setInt(++i, pc.getMeteLevel());
            pstm.setInt(++i, pc.getTurnLifeSkillCount());
            pstm.setTimestamp(++i, pc.getPunishTime());
            pstm.setInt(++i, pc.getBanError());
            pstm.setInt(++i, pc.getInputBanError());
            pstm.setInt(++i, pc.getSpeedError());
            pstm.setInt(++i, pc.getRocksPrisonTime());
            pstm.setInt(++i, pc.getIvoryTowerTime());
            pstm.setInt(++i, pc.getLastabardTime());
            pstm.setInt(++i, pc.getDragonValleyTime());
            pstm.setInt(++i, pc.getMazuTime());
            pstm.setInt(++i, pc.getAITimer());
            pstm.setTimestamp(++i, pc.getTamTime());
            pstm.setInt(++i, pc.getMark_count());
            pstm.setInt(++i, pc.getOnlineGiftIndex());
            pstm.setBoolean(++i, pc.isOnlineGiftWiatEnd());
            pstm.setInt(++i, pc.getOtherStats());
            pstm.setInt(++i, pc.getAddPoint());
            pstm.setInt(++i, pc.getDelPoint());
            // *pstm.setInt(++i, pc.getHonor());
            // *pstm.setInt(++i, pc.getHonorLevel());
            pstm.setInt(++i, pc.getPaCha());
            pstm.setInt(++i, pc.getPaCon());
            pstm.setInt(++i, pc.getPaDex());
            pstm.setInt(++i, pc.getPaInt());
            pstm.setInt(++i, pc.getPaStr());
            pstm.setInt(++i, pc.getPaWis());
            pstm.setInt(++i, pc.getWyType1());
            pstm.setInt(++i, pc.getWyLevel1());
            pstm.setInt(++i, pc.getWyType2());
            pstm.setInt(++i, pc.getWyLevel2());
            pstm.setInt(++i, pc.getWyType3());
            pstm.setInt(++i, pc.getWyLevel3());
            pstm.setInt(++i, pc.getWyType4());
            pstm.setInt(++i, pc.getWyLevel4());
            pstm.setInt(++i, pc.getWyType5());
            pstm.setInt(++i, pc.getWyLevel5());
            pstm.setInt(++i, pc.getWyType6());
            pstm.setInt(++i, pc.getWyLevel6());
            pstm.setInt(++i, pc.getWenyangJiFen());
            pstm.setInt(++i, pc.getLastDollId());
            pstm.setInt(++i, pc.getLastHolyId2());
            pstm.setInt(++i, pc.get_day_signature());
            pstm.setTimestamp(++i, pc.get_day_signature_time());
            pstm.setInt(++i, pc.getHonor());
            pstm.setInt(++i, pc.getHonorLevel());
            pstm.setInt(++i, pc.getLastPolyCardId()); // 新增
            // 最後一個 objid 為 WHERE objid=?
            pstm.setInt(++i, pc.getId());
            pstm.execute();

        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }


    @Override
    public L1PcInstance loadCharacter(final int objid) {
        L1PcInstance pc = null;
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM characters WHERE objid=?");
            pstm.setInt(1, objid);
            rs = pstm.executeQuery();
            if (!rs.next()) {
                return null;
            }
            pc = new L1PcInstance();
            pc.setAccountName(rs.getString("account_name"));
            pc.setId(rs.getInt("objid"));
            pc.setLevel(rs.getInt("level"));
            pc.setName(rs.getString("char_name"));
            pc.setExp(rs.getInt("Exp"));
            pc.setAc(rs.getInt("Ac"));
            pc.addBaseMaxHp(rs.getShort("MaxHp"));
            pc.addBaseMaxMp(rs.getShort("MaxMp"));
            pc.addBaseStr(rs.getByte("Str"));
            pc.addBaseCon(rs.getByte("Con"));
            pc.addBaseDex(rs.getByte("Dex"));
            pc.addBaseCha(rs.getByte("Cha"));
            pc.addBaseInt(rs.getByte("Intel"));
            pc.addBaseWis(rs.getByte("Wis"));
            pc.setElixirStats(rs.getInt("ElixirStatus"));
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
            return null;
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return pc;
    }
}
