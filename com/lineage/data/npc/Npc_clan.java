package com.lineage.data.npc;

import com.lineage.config.ConfigClan;
import com.lineage.data.event.ClanSkillDBSet;
import com.lineage.data.event.ClanSkillSet;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ClanMembersTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.RewardClanSkillsTable;
import com.lineage.server.datatables.lock.ClanEmblemReading;
import com.lineage.server.datatables.lock.ClanReading;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.L1War;
import com.lineage.server.serverpackets.S_CharTitle;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1ClanSkills;
import com.lineage.server.templates.L1Item;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldWar;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 血盟執行人 70538
 *
 * @author dexc
 */
public class Npc_clan extends NpcExecutor {
    public static final String[] SKILLINFO = new String[]{"\\fR血盟狂暴的爆發力流遍全身!", "\\fR血盟寂靜的氣息壟罩著你!", "\\fR血盟魔擊的光緩緩的發亮!", "\\fR血盟消魔的光環保護著你!",};
    private static final Log _log = LogFactory.getLog(Npc_clan.class);
    private static final String[] _skillName = new String[]{"無", "狂暴", "寂靜", "魔擊", "消魔",};
    // 血盟技能材料
    private static final int[][] _items = new int[][]{{ConfigClan.material_1, ConfigClan.Quantity_item_1},// 鑽石 x 500<br>  //src013
            {ConfigClan.material_2, ConfigClan.Quantity_item_2},// 紅寶石 x 500<br>
            {ConfigClan.material_3, ConfigClan.Quantity_item_3},// 藍寶石 x 500<br>// 鑽石 x
    };

    /**
     *
     */
    private Npc_clan() {
        // TODO Auto-generated constructor stub
    }

    public static NpcExecutor get() {
        return new Npc_clan();
    }

    @Override
    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        try {
            if (ClanSkillSet.START) {
                int clanid = pc.getClanid();
                if (pc.isCrown()) {
                    if (clanid == 0) {
                        if (pc.getLevel() < 5) {
                            pc.sendPackets(new S_ServerMessage("等級沒到5等不要跟我說話。"));
                            return;
                        }
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c1"));
                    } else if (!pc.getClan().isClanskill()) {
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c1b"));
                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Timestamp time = pc.getClan().get_skilltime();
                        String[] times = {sdf.format(time)};
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c1aL", times));
                    }
                } else if (clanid != 0) {
                    if (!pc.getClan().isClanskill()) {
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c1g"));
                    } else {
                        Timestamp time = pc.getClan().get_skilltime();
                        int index = time.toString().indexOf(".");
                        String[] times = {time.toString().substring(0, index)};
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c1a", times));
                    }
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c1c"));
                }
            } else if (ClanSkillDBSet.START) {
                int clanid = pc.getClanid();
                if (pc.isCrown()) {
                    if (clanid == 0) {
                        if (pc.getLevel() < 5) {
                            pc.sendPackets(new S_ServerMessage("等級沒到5等不要跟我說話。"));
                            return;
                        }
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_c1"));
                    } else {
                        int clanSkillId = pc.getClan().getClanSkillId();
                        if (clanSkillId == 0) {
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_c1b"));
                        } else {
                            int clanSkillLv = pc.getClan().getClanSkillLv();
                            L1ClanSkills clanSkills = RewardClanSkillsTable.get().getClanSkillsList(clanSkillId, clanSkillLv);
                            String skillName = clanSkills.getClanSkillName();
                            String note = clanSkills.getNote();
                            if (clanSkillLv < 10) {
                                String NextSkillName = RewardClanSkillsTable.get().getSkillsName(clanSkillId, clanSkillLv + 1);
                                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_c1a", new String[]{skillName, note, NextSkillName}));
                            } else {
                                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_c1a2", new String[]{skillName, note}));
                            }
                        }
                    }
                } else if (clanid != 0) {
                    int clanSkillId = pc.getClan().getClanSkillId();
                    if (clanSkillId == 0) {
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_c1g"));
                    } else {
                        int clanSkillLv = pc.getClan().getClanSkillLv();
                        L1ClanSkills clanSkills = RewardClanSkillsTable.get().getClanSkillsList(clanSkillId, clanSkillLv);
                        String skillName = clanSkills.getClanSkillName();
                        String note = clanSkills.getNote();
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_c1a2", new String[]{skillName, note}));
                    }
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_c1c"));
                }
            } else if ((pc.isCrown()) && (pc.getClanid() == 0)) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bpledge2_no_use"));
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bpledge1_no_use"));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
        try {
            String htmlid = null;
            boolean html = false;
            boolean updatePc = false;
            int clanid = pc.getClanid();
            if (ClanSkillSet.START) {
                if (clanid == 0) {
                    // 王族角色
                    if (pc.isCrown()) {
                        htmlid = "y_c1d";
                    } else {
                        htmlid = "y_c1e";
                    }
                }
                if (htmlid != null) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), htmlid));
                    return;
                }
                if (cmd.equals("1")) {// 學習血盟技能
                    if (pc.getClan().isClanskill()) {
                        html = true;
                        htmlid = "y_c3";
                    } else {
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c1g"));
                    }
                } else if (cmd.equals("2")) {// 創立血盟技能
                    // 王族角色
                    if (pc.isCrown()) {
                        if (!pc.getClan().isClanskill()) {
                            get_clanSkill(pc, npc);
                        }
                    } else {
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c1f"));
                    }
                } else if (cmd.equals("3")) {// 解散血盟
                    if (pc.isCrown()) {
                        if (pc.getId() == pc.getClan().getLeaderId()) {
                            delClan(pc);
                            pc.sendPackets(new S_CloseList(pc.getId()));
                        } else {
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c1f"));
                        }
                    }
                } else if (cmd.equals("a")) {// 狂暴(增加物理攻擊力)
                    if (pc.getClan().isClanskill()) {
                        pc.get_other().set_clanskill(1);
                        updatePc = true;
                    }
                } else if (cmd.equals("b")) {// 寂靜(增加物理傷害減免)
                    if (pc.getClan().isClanskill()) {
                        pc.get_other().set_clanskill(2);
                        updatePc = true;
                    }
                } else if (cmd.equals("c")) {// 魔擊(增加魔法攻擊力)
                    if (pc.getClan().isClanskill()) {
                        pc.get_other().set_clanskill(4);
                        updatePc = true;
                    }
                } else if (cmd.equals("d")) {// 消魔(增加魔法傷害減免)
                    if (pc.getClan().isClanskill()) {
                        pc.get_other().set_clanskill(8);
                        updatePc = true;
                    }
                }
                if (updatePc) {
                    try {
                        html = true;
                        htmlid = "y_c3a";
                        // 資料庫更新
                        pc.save();
                    } catch (Exception e) {
                        _log.error(e.getLocalizedMessage(), e);
                    }
                }
                if (html) {
                    final int clanMan = pc.getClan().getOnlineClanMemberSize();
                    String skillName = "";
                    String start1 = " ";
                    String start2 = " ";
                    String start3 = " ";
                    String start4 = " ";
                    switch (pc.get_other().get_clanskill()) {
                        case 0:// 無
                            skillName = _skillName[0];
                            break;
                        case 1:// 血盟線上人數 每增加4人 物理攻擊力+1
                            skillName = _skillName[1];
                            start1 = "啟用";
                            break;
                        case 2:// 血盟線上人數 每增加4人 物理傷害減免+1
                            skillName = _skillName[2];
                            start2 = "啟用";
                            break;
                        case 4:// 血盟線上人數 每增加4人 魔法攻擊力+1
                            skillName = _skillName[3];
                            start3 = "啟用";
                            break;
                        case 8:// 血盟線上人數 每增加4人 魔法傷害減免+1
                            skillName = _skillName[4];
                            start4 = "啟用";
                            break;
                    }
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), htmlid, new String[]{skillName, String.valueOf(clanMan), start1, start2, start3, start4, _skillName[1], _skillName[2], _skillName[3], _skillName[4],}));
                }
            } else if (ClanSkillDBSet.START) {
                // int clanid = pc.getClanid();
                if (clanid == 0) {
                    if (pc.isCrown()) {
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_c1d"));
                    } else {
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_c1e"));
                    }
                    return;
                }
                if (!pc.isCrown()) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_c1f"));
                    return;
                }
                if (cmd.equals("1")) {
                    int clanSkillId = pc.getClan().getClanSkillId();
                    int clanSkillLv = pc.getClan().getClanSkillLv();
                    if ((clanSkillId != 0) && (clanSkillLv < 10)) {
                        String MaterialName = RewardClanSkillsTable.get().getMaterialName(clanSkillId, clanSkillLv + 1);
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_c3", new String[]{MaterialName}));
                    } else {
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_c1g"));
                    }
                } else if (cmd.equals("2")) {
                    int clanSkillId = pc.getClan().getClanSkillId();
                    if (clanSkillId == 0) {
                        if (pc.getAmount() == 0) {
                            clanSkillId = 1;
                        } else {
                            clanSkillId = pc.getAmount();
                        }
                    }
                    pc.setAmount(0);
                    int clanSkillLv = pc.getClan().getClanSkillLv() + 1;
                    if (clanSkillId <= 10) {
                        Queue<String> itemListX = new ConcurrentLinkedQueue<String>();
                        L1ClanSkills clanSkills = RewardClanSkillsTable.get().getClanSkillsList(clanSkillId, clanSkillLv);
                        if (clanSkills != null) {
                            int[] Material = clanSkills.getMaterial();
                            int[] MaterialCount = clanSkills.getMaterialCount();
                            int[] MaterialLevel = clanSkills.getMaterialLevel();
                            if ((Material != null) && (MaterialCount != null) && (MaterialLevel != null)) {
                                for (int i = 0; i < Material.length; i++) {
                                    L1ItemInstance item = pc.getInventory().checkItemX_Lv(Material[i], MaterialLevel[i], MaterialCount[i]);
                                    if (item == null) {
                                        long countX = pc.getInventory().countItems(Material[i]);
                                        L1Item itemX2 = ItemTable.get().getTemplate(Material[i]);
                                        if (countX > 0L) {
                                            if (MaterialLevel[i] != 0) {
                                                itemListX.offer("+" + MaterialLevel[i] + " " + itemX2.getNameId() + " (" + (MaterialCount[i] - countX) + ")");
                                            } else {
                                                itemListX.offer(itemX2.getNameId() + " (" + (MaterialCount[i] - countX) + ")");
                                            }
                                        } else if (MaterialLevel[i] != 0) {
                                            itemListX.offer("+" + MaterialLevel[i] + " " + itemX2.getNameId() + " (" + MaterialCount[i] + ")");
                                        } else {
                                            itemListX.offer(itemX2.getNameId() + " (" + MaterialCount[i] + ")");
                                        }
                                    }
                                }
                                if (itemListX.size() > 0) {
                                    for (Iterator<String> iter = itemListX.iterator(); iter.hasNext(); ) {
                                        String tgitem = (String) iter.next();
                                        pc.sendPackets(new S_ServerMessage(337, tgitem));
                                    }
                                    itemListX.clear();
                                    pc.sendPackets(new S_CloseList(pc.getId()));
                                    return;
                                }
                                for (int i = 0; i < Material.length; i++) {
                                    L1ItemInstance item = pc.getInventory().checkItemX_Lv(Material[i], MaterialLevel[i], MaterialCount[i]);
                                    if (item != null) {
                                        if (MaterialLevel[i] != 0) {
                                            pc.getInventory().consumeEnchantItem(Material[i], MaterialLevel[i], MaterialCount[i]);
                                        } else {
                                            pc.getInventory().consumeItem(Material[i], MaterialCount[i]);
                                        }
                                    }
                                }
                            }
                        }
                        pc.getClan().setClanSkillId(clanSkillId);
                        pc.getClan().setClanSkillLv(clanSkillLv);
                        ClanReading.get().updateClanSkill(pc.getClan());
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_c2a"));
                    }
                } else if (cmd.equals("3")) {
                    if (pc.getId() == pc.getClan().getLeaderId()) {
                        delClan(pc);
                        pc.sendPackets(new S_CloseList(pc.getId()));
                    } else {
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_c1f"));
                    }
                } else if (cmd.equals("4")) {
                    int clanSkillId = pc.getClan().getClanSkillId();
                    int clanSkillLv = pc.getClan().getClanSkillLv();
                    if ((clanSkillId != 0) && (clanSkillLv != 0)) {
                        String NextSkillName = RewardClanSkillsTable.get().getSkillsName(clanSkillId, clanSkillLv);
                        pc.getClan().setClanSkillId(0);
                        pc.getClan().setClanSkillLv(0);
                        ClanReading.get().updateClanSkill(pc.getClan());
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_c2a"));
                        pc.sendPackets(new S_ServerMessage(166, "成功遺忘掉血盟技能:" + NextSkillName));
                        pc.sendPackets(new S_CloseList(pc.getId()));
                    } else {
                        pc.sendPackets(new S_ServerMessage(166, "您尚未學習血盟技能"));
                    }
                } else if (cmd.equals("s")) {
                    int clanskillId = pc.getClan().getClanSkillId();
                    if (clanskillId == 0) {
                        int clanskillLv = pc.getClan().getClanSkillLv();
                        if (clanskillLv == 0) {
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_c2", ClanSkillDBSet._skillNameNote));
                        }
                    }
                } else if (cmd.equals("a")) {
                    int clanskillId = pc.getClan().getClanSkillId();
                    if (clanskillId == 0) {
                        int clanskillLv = pc.getClan().getClanSkillLv();
                        if (clanskillLv == 0) {
                            String MaterialName = RewardClanSkillsTable.get().getMaterialName(1, 1);
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_c3", new String[]{MaterialName}));
                            pc.setAmount(1);
                        }
                    }
                } else if (cmd.equals("b")) {
                    int clanskillId = pc.getClan().getClanSkillId();
                    if (clanskillId == 0) {
                        int clanskillLv = pc.getClan().getClanSkillLv();
                        if (clanskillLv == 0) {
                            //String MaterialName = RewardClanSkillsTable.get().getMaterialName(2, 2);
                            String MaterialName = RewardClanSkillsTable.get().getMaterialName(2, 1);
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_c3", new String[]{MaterialName}));
                            pc.setAmount(2);
                        }
                    }
                } else if (cmd.equals("c")) {
                    int clanskillId = pc.getClan().getClanSkillId();
                    if (clanskillId == 0) {
                        int clanskillLv = pc.getClan().getClanSkillLv();
                        if (clanskillLv == 0) {
                            //String MaterialName = RewardClanSkillsTable.get().getMaterialName(3, 3);
                            String MaterialName = RewardClanSkillsTable.get().getMaterialName(3, 1);
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_c3", new String[]{MaterialName}));
                            pc.setAmount(3);
                        }
                    }
                } else if (cmd.equals("d")) {
                    int clanskillId = pc.getClan().getClanSkillId();
                    if (clanskillId == 0) {
                        int clanskillLv = pc.getClan().getClanSkillLv();
                        if (clanskillLv == 0) {
                            //String MaterialName = RewardClanSkillsTable.get().getMaterialName(4, 4);
                            String MaterialName = RewardClanSkillsTable.get().getMaterialName(4, 1);
                            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "j_c3", new String[]{MaterialName}));
                            pc.setAmount(4);
                        }
                    }
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 創立血盟技能
     *
     * @param pc
     * @param npc
     */
    private void get_clanSkill(L1PcInstance pc, L1NpcInstance npc) {
        // 道具不足的顯示清單
        Queue<String> itemListX = new ConcurrentLinkedQueue<String>();
        if (!pc.isGm()) {
            // 檢查所需物品數量
            for (int itemid[] : _items) {
                final L1ItemInstance item = pc.getInventory().checkItemX(itemid[0], itemid[1]);
                if (item == null) {
                    long countX = pc.getInventory().countItems(itemid[0]);
                    final L1Item itemX2 = ItemTable.get().getTemplate(itemid[0]);
                    if (countX > 0) {
                        itemListX.offer(itemX2.getNameId() + " (" + (itemid[1] - countX) + ")");
                    } else {
                        itemListX.offer(itemX2.getNameId() + " (" + itemid[1] + ")");
                    }
                }
            }
            if (itemListX.size() > 0) {
                for (final Iterator<String> iter = itemListX.iterator(); iter.hasNext(); ) {
                    final String tgitem = iter.next();// 返回迭代的下一個元素。
                    // 337：\f1%0不足%s。 0_o"
                    pc.sendPackets(new S_ServerMessage(337, tgitem));
                }
                itemListX.clear();
                // 關閉對話窗
                pc.sendPackets(new S_CloseList(pc.getId()));
                return;
            }
            itemListX.clear();
            // 檢查所需物品數量
            for (int itemid[] : _items) {
                int ritemid = itemid[0];// 道具編號
                int rcount = itemid[1];// 耗用數量
                final L1ItemInstance item = pc.getInventory().checkItemX(ritemid, rcount);
                if (item != null) {
                    long rcountx = pc.getInventory().removeItem(item, rcount);
                    if (rcountx != rcount) {// 刪除道具
                        // 關閉對話窗
                        pc.sendPackets(new S_CloseList(pc.getId()));
                        return;
                    }
                }
            }
        }
        long time = System.currentTimeMillis();// 目前時間豪秒
        long x1 = 30 * 24 * 60 * 60;// 30天耗用秒數
        long x2 = x1 * 1000;// 轉為豪秒
        long upTime = x2 + time;// 目前時間 加上 30天
        // 目前時間
        final Timestamp ts = new Timestamp(upTime);
        pc.getClan().set_clanskill(true);
        pc.getClan().set_skilltime(ts);
        // 更新血盟資料
        ClanReading.get().updateClan(pc.getClan());
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c2a"));
    }

    /**
     * 解散血盟
     *
     * @param pc
     */
    private void delClan(L1PcInstance pc) {
        final L1Clan clan = pc.getClan();
        if (clan != null) {
            final int clan_id = clan.getClanId();
            final String player_name = pc.getName();
            final String clan_name = clan.getClanName();
            final String clan_member_name[] = clan.getAllMembers();
            final int castleId = clan.getCastleId();
            final int houseId = clan.getHouseId();
            if (castleId != 0) {
                // \f1擁有城堡與血盟小屋的狀態下無法解散血盟。
                pc.sendPackets(new S_ServerMessage(665));
                return;
            }
            if (houseId != 0) {
                // \f1擁有城堡與血盟小屋的狀態下無法解散血盟。
                pc.sendPackets(new S_ServerMessage(665));
                return;
            }
            for (final L1War war : WorldWar.get().getWarList()) {
                // 戰爭中
                if (war.checkClanInWar(clan_name)) {
                    // \f1無法解散。
                    pc.sendPackets(new S_ServerMessage(302));
                    return;
                }
            }
            try {
                for (int i = 0; i < clan_member_name.length; i++) { // 血盟成員訊息發送
                    final L1PcInstance online_pc = World.get().getPlayer(clan_member_name[i]);
                    L1PcInstance tg_pc = null;
                    boolean isReTitle = false;
                    if (online_pc != null) { // 線上成員
                        tg_pc = online_pc;
                        online_pc.sendPacketsAll(new S_CharTitle(online_pc.getId()));
                        isReTitle = true;
                        // %1血盟的盟主%0%s解散了血盟。
                        online_pc.sendPackets(new S_ServerMessage(269, player_name, clan_name));
                    } else { // 離線成員
                        tg_pc = CharacterTable.get().restoreCharacter(clan_member_name[i]);
                        isReTitle = true;
                    }
                    if (tg_pc != null) {
                        tg_pc.setClanid(0);
                        tg_pc.setClanname("");
                        tg_pc.setClanRank(0);
                        if (isReTitle) {
                            tg_pc.setTitle("");
                        }
                        tg_pc.save();// 資料庫更新
                    }
                }
                // 資料刪除
                ClanEmblemReading.get().deleteIcon(clan_id);
                ClanReading.get().deleteClan(clan_name);
                ClanMembersTable.getInstance().deleteAllMember(clan.getClanId());
                // 更新畫面
                // L1PcUnlock.Pc_Unlock(pc);
                L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading(), false);
            } catch (Exception e) {
                _log.error(e.getLocalizedMessage(), e);
            }
        }
    }
}
