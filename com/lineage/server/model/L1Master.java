package com.lineage.server.model;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_OwnCharAttrDef;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.lineage.server.model.skill.L1SkillId.*;

public class L1Master {
    private static L1Master _instance;
    private static HashMap<Integer, ArrayList<L1PcInstance>> list = new HashMap<Integer, ArrayList<L1PcInstance>>();
    private static HashMap<Integer, String> master_name_list = new HashMap<Integer, String>();

    private L1Master() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM characters ");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int objid = rs.getInt("objid");
                int masterID = rs.getInt("master_id");
                String charName = rs.getString("char_name");
                if (masterID > 0) {
                    L1PcInstance disciple = L1PcInstance.load(charName);
                    creatRelation(masterID, disciple);
                } else if (masterID < 0) {
                    master_name_list.put(objid, charName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public static L1Master getInstance() {
        if (_instance == null) {
            _instance = new L1Master();
        }
        return _instance;
    }

    private String getNameFromSQL(int objid) {
        String name = null;
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("SELECT * FROM characters  WHERE objid=?");
            pstm.setInt(1, objid);
            rs = pstm.executeQuery();
            if (rs.next()) {
                name = rs.getString("char_name");
                master_name_list.put(objid, name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return name;
    }

    public void resetMasterID(String name) {
        Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = DatabaseFactory.get().getConnection();
            pstm = con.prepareStatement("UPDATE characters SET master_id = 0 WHERE char_name =?");
            pstm.setString(1, name);
            pstm.execute();
            pstm.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    // 師父的名字
    public String getMasterName(int masterID) {
        if (master_name_list.containsKey(masterID)) {
            return master_name_list.get(masterID);
        }
        return getNameFromSQL(masterID);
    }

    // 弟子清單
    public ArrayList<L1PcInstance> getDiscipleList(int masterID) {
        return list.get(masterID);
    }

    // 線上弟子清單
    public ArrayList<L1PcInstance> getOnlineDiscipleList(int masterID) {
        ArrayList<L1PcInstance> onlineList = new ArrayList<L1PcInstance>();
        for (L1PcInstance disciple : list.get(masterID)) {
            L1Object obj = World.get().findObject(disciple.getId());
            if (obj instanceof L1PcInstance) {
                onlineList.add((L1PcInstance) obj);
            }
        }
        return onlineList;
    }

    // 弟子數量
    public int getDiscipleCount(int masterID) {
        return (list.containsKey(masterID)) ? list.get(masterID).size() : 0;
    }

    // 師徒關係成立
    public void creatRelation(int masterID, L1PcInstance disciple) {
        if (list.containsKey(masterID)) {
            list.get(masterID).add(disciple);
        } else {
            ArrayList<L1PcInstance> disciple_list = new ArrayList<L1PcInstance>();
            disciple_list.add(disciple);
            list.put(masterID, disciple_list);
        }
        giveBuff(disciple, EFFECT_NO_MASTER_1);
    }

    // 師徒關係終止
    public void terminateRelation(int masterID, String name) {
        for (int i = 0; i < list.get(masterID).size(); i++) {
            L1PcInstance temp = list.get(masterID).get(i);
            if (temp.getName().equalsIgnoreCase(name)) {
                list.get(masterID).remove(i);
                break;
            }
        }
        try {
            L1PcInstance disciple = World.get().getPlayer(name);
            if (disciple == null) { // not online
                resetMasterID(name);
            } else { // online
                disciple.setMasterID(0);
                disciple.save();
                disciple.sendPackets(new S_ServerMessage(2977)); // 師徒關係已刪除
                removeMasterBuff(disciple);
            }
            L1Object obj = World.get().findObject(masterID);
            if (obj instanceof L1PcInstance) {
                ((L1PcInstance) obj).sendPackets(new S_ServerMessage(2977)); // 師徒關係已刪除
            }
            // 門下沒有弟子了
            if (L1Master.getInstance().getDiscipleCount(masterID) <= 0) {
                if (obj instanceof L1PcInstance) {
                    ((L1PcInstance) obj).setMasterID(0);
                    ((L1PcInstance) obj).save();
                } else {
                    String masterName = L1Master.getInstance().getMasterName(masterID);
                    L1Master.getInstance().resetMasterID(masterName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logout(L1PcInstance pc) {
        if (pc.getMasterID() == -1) {// master
            for (L1PcInstance disciple : getOnlineDiscipleList(pc.getId())) {
                removeMasterBuff(disciple); // disciple remove effect
            }
        }
    }

    public void login(L1PcInstance pc) {
        if (pc.getMasterID() == -1) {// master
            for (L1PcInstance disciple : getOnlineDiscipleList(pc.getId())) {
                int count = 0;
                if (disciple.isInParty()) {
                    //for (L1PcInstance menber : disciple.getParty().partyUsers().values()) {
                    for (final L1PcInstance menber : disciple.getParty().getMemberList()) {// 7.6
                        if (menber.getMasterID() == pc.getId()) {
                            count++;
                        }
                    }
                } else {
                    count = 1;
                }
                int skillid = EFFECT_NO_MASTER_1 - 1 + count;
                giveBuff(disciple, skillid);
            }
        } else if (pc.getMasterID() > 0) { // disciple
            L1Object obj = World.get().findObject(pc.getMasterID());
            if (obj != null) { // master is online
                giveBuff(pc, EFFECT_NO_MASTER_1);
            }
        }
    }

    public void leaveParty(L1PcInstance pc) {
        if (pc.getMasterID() < 0) {// master
            for (L1PcInstance disciple : getOnlineDiscipleList(pc.getId())) {
                // EFFECT_WITH_MASTER_1~4 --> EFFECT_NO_MASTER_1~4
                if (pc.getParty().isMember(disciple)) {
                    changeBuff(disciple, -4);
                }
            }
        } else if (pc.getMasterID() > 0) { // disciple
            for (L1PcInstance disciple : getOnlineDiscipleList(pc.getMasterID())) {
                if (disciple.getId() == pc.getId()) {
                    changeBuff(disciple, -8);
                } else if (pc.getParty().isMember(disciple)) { // 隊友裡有同門弟子
                    if (pc.getParty().isLeader(pc)) { // 隊長離隊 = 解散隊伍
                        changeBuff(disciple, -8);
                    } else {
                        // EFFECT -1
                        changeBuff(disciple, -1);
                    }
                }
            }
        }
    }

    public void creatParty(L1PcInstance pc) {
        if (pc.getMasterID() == -1) {// master
            for (L1PcInstance disciple : getOnlineDiscipleList(pc.getId())) {
                if (pc.getParty().isMember(disciple)) {
                    // EFFECT_NO_MASTER--->EFFECT_WITH_MASTER
                    changeBuff(disciple, 4);
                }
            }
        } else if (pc.getMasterID() > 0) { // disciple
            for (L1PcInstance disciple : getOnlineDiscipleList(pc.getMasterID())) {
                if (pc.getId() != disciple.getId() && pc.getParty().isMember(disciple)) {
                    // EFFECT +1
                    changeBuff(disciple, 1);
                }
            }
        }
    }

    public void joinParty(L1PcInstance pc) {
        if (pc.getMasterID() == -1) {// master
            for (L1PcInstance disciple : getOnlineDiscipleList(pc.getId())) {
                if (pc.getParty().isMember(disciple)) {
                    // EFFECT_NO_MASTER--->EFFECT_WITH_MASTER
                    changeBuff(disciple, 4);
                }
            }
        } else if (pc.getMasterID() > 0) { // disciple
            int skillid = 0;
            for (L1PcInstance disciple : getOnlineDiscipleList(pc.getMasterID())) {
                if (pc.getId() != disciple.getId() && pc.getParty().isMember(disciple)) {
                    // EFFECT +1
                    skillid = changeBuff(disciple, 1);
                }
            }
            if (skillid != 0) {
                removeBuff(pc, EFFECT_NO_MASTER_1);
                giveBuff(pc, skillid);
            }
        }
    }

    private int changeBuff(L1PcInstance pc, int diff) {
        int newid = 0;
        for (int id = EFFECT_NO_MASTER_1; id <= EFFECT_WITH_MASTER_4; id++) {
            if (pc.hasSkillEffect(id)) {
                newid = Math.min(Math.max(EFFECT_NO_MASTER_1, id + diff), EFFECT_WITH_MASTER_4);
                removeBuff(pc, id);
                giveBuff(pc, newid);
                break;
            }
        }
        return newid;
    }

    private void giveBuff(L1PcInstance pc, int skillid) {
        pc.setSkillEffect(skillid, 0);
        pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_NO_TIMER, skillid - EFFECT_NO_MASTER_1, 1));
        if (skillid == EFFECT_NO_MASTER_4 || skillid == EFFECT_WITH_MASTER_4) {
            pc.sendPackets(new S_PacketBox(S_PacketBox.UPDATE_ER, pc.getEr()));
        }
        if (skillid >= EFFECT_NO_MASTER_3 && skillid <= EFFECT_NO_MASTER_4) {
            pc.addWind(2);
            pc.addWater(2);
            pc.addFire(2);
            pc.addEarth(2);
        }
        if (skillid >= EFFECT_NO_MASTER_2 && skillid <= EFFECT_NO_MASTER_4) {
            pc.addMr(1);
            pc.sendPackets(new S_SPMR(pc));
        }
        if (skillid >= EFFECT_NO_MASTER_1 && skillid <= EFFECT_NO_MASTER_4) {
            pc.addAc(-1);
            pc.sendPackets(new S_OwnCharAttrDef(pc));
        }
        if (skillid >= EFFECT_WITH_MASTER_3 && skillid <= EFFECT_WITH_MASTER_4) {
            pc.addWind(6);
            pc.addWater(6);
            pc.addFire(6);
            pc.addEarth(6);
        }
        if (skillid >= EFFECT_WITH_MASTER_2 && skillid <= EFFECT_WITH_MASTER_4) {
            pc.addMr(3);
            pc.sendPackets(new S_SPMR(pc));
        }
        if (skillid >= EFFECT_WITH_MASTER_1 && skillid <= EFFECT_WITH_MASTER_4) {
            pc.addAc(-3);
            pc.sendPackets(new S_OwnCharAttrDef(pc));
        }
    }

    private void removeBuff(L1PcInstance pc, int skillid) {
        pc.removeSkillEffect(skillid);
        pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_NO_TIMER, skillid - EFFECT_NO_MASTER_1, 0));
        if (skillid == EFFECT_NO_MASTER_4 || skillid == EFFECT_WITH_MASTER_4) {
            pc.sendPackets(new S_PacketBox(S_PacketBox.UPDATE_ER, pc.getEr()));
        }
        if (skillid >= EFFECT_NO_MASTER_3 && skillid <= EFFECT_NO_MASTER_4) {
            pc.addWind(-2);
            pc.addWater(-2);
            pc.addFire(-2);
            pc.addEarth(-2);
        }
        if (skillid >= EFFECT_NO_MASTER_2 && skillid <= EFFECT_NO_MASTER_4) {
            pc.addMr(-1);
            pc.sendPackets(new S_SPMR(pc));
        }
        if (skillid >= EFFECT_NO_MASTER_1 && skillid <= EFFECT_NO_MASTER_4) {
            pc.addAc(1);
            pc.sendPackets(new S_OwnCharAttrDef(pc));
        }
        if (skillid >= EFFECT_WITH_MASTER_3 && skillid <= EFFECT_WITH_MASTER_4) {
            pc.addWind(-6);
            pc.addWater(-6);
            pc.addFire(-6);
            pc.addEarth(-6);
        }
        if (skillid >= EFFECT_WITH_MASTER_2 && skillid <= EFFECT_WITH_MASTER_4) {
            pc.addMr(-3);
            pc.sendPackets(new S_SPMR(pc));
        }
        if (skillid >= EFFECT_WITH_MASTER_1 && skillid <= EFFECT_WITH_MASTER_4) {
            pc.addAc(3);
            pc.sendPackets(new S_OwnCharAttrDef(pc));
        }
    }

    private void removeMasterBuff(L1PcInstance pc) {
        for (int id = EFFECT_NO_MASTER_1; id <= EFFECT_WITH_MASTER_4; id++) {
            if (pc.hasSkillEffect(id)) {
                removeBuff(pc, id);
            }
        }
    }
}
