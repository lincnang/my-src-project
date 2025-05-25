package com.lineage.data.npc.event;

import com.lineage.config.ConfigOtherSet2;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.npc.teleport.Npc_Teleport;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.datatables.C1_Name_Table;
import com.lineage.server.datatables.NpcTeleportTable;
import com.lineage.server.datatables.lock.CharacterC1Reading;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1TeleportLoc;
import com.lineage.server.templates.L1User_Power;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;

public class Npc_Camp extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Camp.class);

    public static NpcExecutor get() {
        return new Npc_Camp();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            String type;
            String score;
            if ((pc.get_c_power() != null) && (pc.get_c_power().get_c1_type() != 0)) {
                type = C1_Name_Table.get().get(pc.get_c_power().get_c1_type());
                score = String.valueOf(pc.get_other().get_score());
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_camp_01", new String[]{type, score}));
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_camp_00"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        try {
            boolean isClose = false;
            if (cmd.equalsIgnoreCase("exit1")) {
                if (pc.get_c_power() != null) {
                    if (pc.get_c_power().get_c1_type() == 0) {
                        pc.sendPackets(new S_GmMessage("您目前沒有陣營", "\\aG"));
                        // pc.sendPackets(new S_ServerMessage("\\fR您目前沒有陣營"));
                    } else {
                        //陣營退出記錄
                        WriteLogTxt.Recording("陣營退出記錄", "玩家:【" + pc.getName() + "】帳號:【" + pc.getAccountName() + "】退出前擁有的積分:【" + pc.get_other().get_score() + "】.");
                        pc.get_c_power().set_c1_type(0);
                        pc.get_c_power().set_note("無");
                        pc.get_other().set_score(0);
                        CharacterC1Reading.get().updateCharacterC1(pc.getId(), 0, "無");
                        pc.get_c_power().set_power(pc, false);
                        pc.sendPacketsAll(new S_ChangeName(pc, true));
                        pc.save();
                        // pc.sendPackets(new S_ServerMessage("\\fR已經退出陣營"));
                        pc.sendPackets(new S_GmMessage("已經退出陣營", "\\aG"));
                    }
                } else {
                    // pc.sendPackets(new S_ServerMessage("\\fR您目前沒有陣營"));
                    pc.sendPackets(new S_GmMessage("您目前沒有陣營", "\\aG"));
                }
                isClose = true;
            } else if (cmd.equalsIgnoreCase("exit2")) {
                isClose = true;
            } else if (cmd.matches("[0-9]+")) {
                String pagecmd = pc.get_other().get_page() + cmd;
                Npc_Teleport.teleport(pc, Integer.valueOf(pagecmd));
            } else if (cmd.startsWith("add")) {
                String newString = cmd.substring(3);
                add_type(newString, pc);
                isClose = true;
            } else {
                pc.get_other().set_page(0);
                HashMap<Integer, L1TeleportLoc> teleportMap = NpcTeleportTable.get().get_teles(pc.get_c_power().get_c1_type() + "AA" + cmd);
                if (teleportMap != null) {
                    if (teleportMap.size() <= 0) {
                        pc.sendPackets(new S_ServerMessage(1447));
                        return;
                    }
                    pc.get_otherList().teleport(teleportMap);
                    Npc_Teleport.showPage(pc, npc, 0);
                } else {
                    pc.sendPackets(new S_GmMessage("只能前往自身陣營地圖"));
                }
            }
            if (isClose) {
                pc.sendPackets(new S_CloseList(pc.getId()));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void add_type(String cmd, L1PcInstance pc) {
        int type = Integer.parseInt(cmd);
        String typeName = C1_Name_Table.get().get(type);
        // 151221新增限制等級低於55以上不能加入陣營
        if (pc.getLevel() < ConfigOtherSet2.CAMPLEVEL) {
            pc.sendPackets(new S_ServerMessage("\\aG等級太低！您還不能選擇陣營.....。"));
            return;
        }
        if (pc.get_c_power() == null) {
            L1User_Power power = new L1User_Power();
            power.set_object_id(pc.getId());
            power.set_c1_type(type);
            power.set_note(typeName);
            pc.set_c_power(power);
            CharacterC1Reading.get().storeCharacterC1(pc);
        } else {
            pc.get_c_power().set_c1_type(type);
            pc.get_c_power().set_note(typeName);
            CharacterC1Reading.get().updateCharacterC1(pc.getId(), type, typeName);
        }
        pc.get_c_power().set_power(pc, false);
        pc.sendPacketsAll(new S_ChangeName(pc, true));
        // 151221新增加入陣營後給予特效顯示
        final S_SkillSound sound = new S_SkillSound(pc.getId(), ConfigOtherSet2.CAMPGFX);//12335
        pc.sendPackets(sound);
        // pc.sendPackets(new S_ServerMessage("\\fR成功加入陣營: " + typeName));
        // pc.sendPackets(new S_GmMessage("成功加入陣營: ["+ typeName +"]"));
        pc.sendPackets(new S_GmMessage("成功加入陣營: [" + typeName + "]", "\\aG"));
        L1Teleport.teleport(pc, pc.getX(), pc.getY(), (short) pc.getMapId(), pc.getHeading(), true);
    }
}
