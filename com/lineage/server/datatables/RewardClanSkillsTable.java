package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1ClanSkills;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class RewardClanSkillsTable {
    private static final Log _log = LogFactory.getLog(RewardClanSkillsTable.class);
    private static final Map<Integer, HashMap<Integer, L1ClanSkills>> _list = new HashMap<>();
    public static boolean START = false;
    private static RewardClanSkillsTable _instance;

    private RewardClanSkillsTable() {
        load();
    }

    public static RewardClanSkillsTable get() {
        if (_instance == null) {
            _instance = new RewardClanSkillsTable();
        }
        return _instance;
    }

    private static int[] getArrayInt(String s) {
        if ((s == null) || (s.isEmpty()) || (s.equals(""))) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(s, ",");
        int iSize = st.countTokens();
        String sTemp = null;
        int[] iReturn = new int[iSize];
        for (int i = 0; i < iSize; i++) {
            sTemp = st.nextToken();
            iReturn[i] = Integer.parseInt(sTemp);
        }
        return iReturn;
    }

    private void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM `系統_血盟技能DB化設置`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int ClanSkillId = rs.getInt("血盟技能編號");
                int ClanSkillLv = rs.getInt("血盟技能等級");
                String ClanSkillName = rs.getString("血盟技能名稱");
                String Note = rs.getString("效果介紹");
                int[] Material = getArrayInt(rs.getString("技能所需材料編號"));
                int[] MaterialCount = getArrayInt(rs.getString("技能所需材料數量"));
                int[] MaterialLevel = getArrayInt(rs.getString("技能所需材料等級"));
                int CheckLvturn = rs.getInt("限制幾轉以上");
                int CheckLevel = rs.getInt("限制幾等以上");
                int AddMaxHp = rs.getInt("額外增加HP");
                int AddMaxMp = rs.getInt("額外增加MP");
                int AddHpr = rs.getInt("回血量");
                int AddMpr = rs.getInt("回魔量");
                int AddStr = rs.getInt("力量");
                int AddBowDmg = rs.getInt("遠距離攻擊");
                int AddHit = rs.getInt("命中");
                int AddBowHit = rs.getInt("遠距離命中");
                int ReductionDmg = rs.getInt("減傷");
                int AddDex = rs.getInt("敏捷");
                int AddCon = rs.getInt("體質");
                int AddInt = rs.getInt("智力");
                int AddWis = rs.getInt("精神");
                int AddCha = rs.getInt("魅力");
                int AddAc = rs.getInt("防禦");
                int AddSp = rs.getInt("魔攻");
                int AddMr = rs.getInt("魔防");
                int AddDmg = rs.getInt("攻擊");
                int ReductionMagicDmg = rs.getInt("魔法減傷");
                int AddWater = rs.getInt("風屬性");
                int AddWind = rs.getInt("水屬性");
                int AddFire = rs.getInt("火屬性");
                int AddEarth = rs.getInt("地屬性");
                int buff_iconid = rs.getInt("偵測圖檔tbl編號");
                int buff_stringid = rs.getInt("偵測圖檔string編號");
                L1ClanSkills skill = new L1ClanSkills();
                skill.setClanSkillName(ClanSkillName);
                skill.setNote(Note);
                skill.setMaterial(Material);
                skill.setMaterialCount(MaterialCount);
                skill.setMaterialLevel(MaterialLevel);
                skill.setCheckLvturn(CheckLvturn);
                skill.setCheckLevel(CheckLevel);
                skill.setAddMaxHp(AddMaxHp);
                skill.setAddMaxMp(AddMaxMp);
                skill.setAddHpr(AddHpr);
                skill.setAddMpr(AddMpr);
                skill.setAddStr(AddStr);
                skill.setAddBowDmg(AddBowDmg);
                skill.setAddHit(AddHit);
                skill.setAddBowHit(AddBowHit);
                skill.setReductionDmg(ReductionDmg);
                skill.setAddDex(AddDex);
                skill.setAddCon(AddCon);
                skill.setAddInt(AddInt);
                skill.setAddWis(AddWis);
                skill.setAddCha(AddCha);
                skill.setAddAc(AddAc);
                skill.setAddSp(AddSp);
                skill.setAddMr(AddMr);
                skill.setAddDmg(AddDmg);
                skill.setReductionMagicDmg(ReductionMagicDmg);
                skill.setAddWater(AddWater);
                skill.setAddWind(AddWind);
                skill.setAddFire(AddFire);
                skill.setAddEarth(AddEarth);
                skill.set_buff_iconid(buff_iconid);
                skill.set_buff_stringid(buff_stringid);
                HashMap<Integer, L1ClanSkills> _map = (HashMap<Integer, L1ClanSkills>) _list.get(ClanSkillId);
                if (_map == null) {
                    _map = new HashMap<>();
                    _map.put(ClanSkillLv, skill);
                    _list.put(ClanSkillId, _map);
                } else {
                    _map.put(ClanSkillLv, skill);
                }
            }
        } catch (SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("讀取->[其他]_血盟技能DB化設置資料數量:" + _list.size() + "(" + timer.get() + "ms)");
    }

    public String getSkillsName(int skillId, int skillLv) {
        if ((!_list.isEmpty()) && (_list.containsKey(skillId))) {
            HashMap<Integer, L1ClanSkills> list = (HashMap<Integer, L1ClanSkills>) _list.get(skillId);
            if (list.containsKey(skillLv)) {
                return ((L1ClanSkills) list.get(skillLv)).getClanSkillName();
            }
        }
        return null;
    }

    public String[] getSkillsNameNote() {
        String[] name = (String[]) null;
        if (!_list.isEmpty()) {
            int size = _list.size() * 2;
            name = new String[size];
            int i = 0;
            for (Integer clanSkillsId : _list.keySet()) {
                HashMap<Integer, L1ClanSkills> list = (HashMap<Integer, L1ClanSkills>) _list.get(clanSkillsId);
                for (Integer clanSkillsLv : list.keySet()) {
                    if (clanSkillsLv == 1) {
                        name[i] = ((L1ClanSkills) list.get(clanSkillsLv)).getClanSkillName();
                        i++;
                        name[i] = ((L1ClanSkills) list.get(clanSkillsLv)).getNote();
                        i++;
                        break;
                    }
                }
            }
        }
        return name;
    }

    public String getMaterialName(int skillId, int skillLv) {
        StringBuilder note = null;
        if ((!_list.isEmpty()) && (_list.containsKey(skillId))) {
            HashMap<Integer, L1ClanSkills> list = (HashMap<Integer, L1ClanSkills>) _list.get(skillId);
            if (list.containsKey(skillLv)) {
                L1ClanSkills clanSkills = (L1ClanSkills) list.get(skillLv);
                int[] Material = clanSkills.getMaterial();
                int[] MaterialCount = clanSkills.getMaterialCount();
                int[] MaterialLevel = clanSkills.getMaterialLevel();
                if ((Material != null) && (MaterialCount != null) && (MaterialLevel != null)) {
                    note = new StringBuilder();
                    for (int i = 0; i < Material.length; i++) {
                        int k = 0;
                        StringBuilder itemName = new StringBuilder();
                        L1Item tgItem = ItemTable.get().getTemplate(Material[i]);
                        if (MaterialLevel[i] != 0) {
                            itemName.append("+");
                            itemName.append(MaterialLevel[i]);
                            itemName.append(" ");
                        }
                        itemName.append(tgItem.getName());
                        k = tgItem.getName().length();
                        if (MaterialCount[i] != 0) {
                            itemName.append("x");
                            itemName.append(MaterialCount[i]);
                        }
                        k += itemName.length();
                        for (; k < 30; k++) {
                            if (k < 30) {
                                itemName.append("　");
                                k++;
                            }
                        }
                        note.append(itemName);
                    }
                }
            }
        }
        return note.toString();
    }

    public L1ClanSkills getClanSkillsList(int skillId, int skillLv) {
        if ((!_list.isEmpty()) && (_list.containsKey(skillId))) {
            HashMap<Integer, L1ClanSkills> list = (HashMap<Integer, L1ClanSkills>) _list.get(skillId);
            if (list.containsKey(skillLv)) {
                return (L1ClanSkills) list.get(skillLv);
            }
        }
        return null;
    }
}
