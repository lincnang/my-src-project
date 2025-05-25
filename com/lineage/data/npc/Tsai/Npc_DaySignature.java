package com.lineage.data.npc.Tsai;

import com.add.Tsai.Day_Signature;
import com.lineage.DatabaseFactory;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 簽到獎勵
 *
 * @author Manly
 * by 2019 / 8 / 2
 */
public class Npc_DaySignature extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_DaySignature.class);
    Random random = new Random();

    private Npc_DaySignature() {
        // TODO Auto-generated constructor stub
    }

    public static NpcExecutor get() {
        return new Npc_DaySignature();
    }

    @Override
    public int type() {
        return 3;
    }

    @Override
    public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
        pc.setCmd(-1);
        final int size = Day_Signature.get().DaySize();
        if (size > 0) {
            final StringBuilder s = new StringBuilder();
            for (int i = 0; i <= size; i++) {
                Day_Signature Day = Day_Signature.get().getDay(i);
                if (Day != null) {
                    final int questid = Day.getDay() + 33000;
                    if (pc.getQuest().isEnd(questid)) {//已經領過
                        s.append(Day.getMsg()).append("『已領取』,");
                    } else {
                        s.append(Day.getMsg()).append(",");
                    }
                }
            }
            final String[] msg = s.toString().split(",");
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_daySing", msg));
        } else {
            pc.sendPackets(new S_SystemMessage("\\aH並無任何簽到獎勵!"));
            pc.sendPackets(new S_CloseList(pc.getId()));
        }
        Connection co = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            co = DatabaseFactory.get().getConnection();
            ps = co.prepareStatement("SELECT * FROM `server_board` ORDER BY `id`");
            rs = ps.executeQuery();
            while (rs.next()) {
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(ps);
            SQLUtil.close(co);
        }
    }

    @Override
    public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
        try {
            if (cmd.matches("[0-999]+")) {
                final int cmd1 = Integer.parseInt(cmd) + 1;
                Day_Signature Day = Day_Signature.get().getDay(cmd1);
                if (Day != null) {
                    final StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("【").append(Day.getMsg()).append("】,");
                    if (Day.getItem() != null) {
                        int index = 0;
                        final String[] item = Day.getItem().split(",");
                        for (String _item : item) {
                            final int itemid = Integer.parseInt(_item);
                            L1Item items = ItemTable.get().getTemplate(itemid);
                            final String[] Enchant = Day.getEnchant().split(",");
                            String enchant = Enchant[index];
                            final String[] Count = Day.getCount().split(",");
                            String count = Count[index];
                            index++;
                            stringBuilder.append("+【").append(enchant).append("】").append(items.getName()).append("(").append(count).append("),");
                        }
                    } else {
                        stringBuilder.append("無道具可領取,");
                    }
                    final String[] msg = stringBuilder.toString().split(",");
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_daySing_1", msg));
                    pc.setCmd(cmd1);
                }
            } else if (cmd.matches("NP1")) {
                if (!pc.isGm()) {
                    pc.sendPackets(new S_SystemMessage("\\aH目前只提供GM使用!"));
                    pc.sendPackets(new S_CloseList(pc.getId()));
                    pc.setCmd(-1);
                    return;
                }
                Day_Signature Day = Day_Signature.get().getDay(pc.getCmd());
                if (Day != null) {
                    SimpleDateFormat sdFormat = new SimpleDateFormat("MMdd");
                    Date date = new Date();
                    String strDate = sdFormat.format(date);
                    if (Integer.parseInt(strDate) > Day.getDay()) {
                        pc.sendPackets(new S_SystemMessage("\\aH你選擇的簽到獎勵已經過期只能補簽喔!"));
                        pc.sendPackets(new S_CloseList(pc.getId()));
                        pc.setCmd(-1);
                    } else if (Integer.parseInt(strDate) < Day.getDay()) {
                        pc.sendPackets(new S_SystemMessage("\\aH你選擇的簽到獎勵領取日期還沒到喔!"));
                        pc.sendPackets(new S_CloseList(pc.getId()));
                        pc.setCmd(-1);
                    } else {
                        final int questid = Day.getDay() + 33000;
                        if (pc.getQuest().isEnd(questid)) {
                            pc.sendPackets(new S_SystemMessage("\\aH今天的簽到獎勵已經領過囉!"));
                            pc.sendPackets(new S_CloseList(pc.getId()));
                            pc.setCmd(-1);
                        } else {
                            if (Day.getItem() != null) {
                                int index = 0;
                                final String[] item = Day.getItem().split(",");
                                for (String _item : item) {
                                    final int itemid = Integer.parseInt(_item);
                                    final String[] Enchant = Day.getEnchant().split(",");
                                    String enchant = Enchant[index];
                                    final String[] Count = Day.getCount().split(",");
                                    String count = Count[index];
                                    index++;
                                    final L1ItemInstance ItemX = ItemTable.get().createItem(itemid);
                                    if (ItemX.isStackable()) {
                                        pc.getInventory().storeItem(itemid, Integer.parseInt(count));
                                    } else {
                                        ItemX.setEnchantLevel(Integer.parseInt(enchant));
                                        ItemX.setIdentified(true);
                                        pc.getInventory().storeItem(ItemX);
                                    }
                                    pc.sendPackets(new S_SystemMessage("\\aH恭喜您完成本次簽到獲得簽到獎勵"));
                                    WriteLogTxt.Recording("簽到紀錄", "玩家" + ":【 " + pc.getName() + "】獲得  " + Day.getDay() + " 簽到獎勵(時間" + new Timestamp(System.currentTimeMillis()) + ")。");
                                }
                                pc.getQuest().set_end(questid);
                                String add = pc.getName() + "的(" + strDate + ")簽到領取紀錄";
                                Connection co = null;
                                PreparedStatement pm = null;
                                try {
                                    co = DatabaseFactory.get().getConnection();
                                    pm = co.prepareStatement("UPDATE `character_quests` SET `note`=? WHERE `char_id`=? AND `quest_id`=?");
                                    int i = 0;
                                    pm.setString(++i, add);// 任務進度
                                    pm.setInt(++i, pc.getId());// 人物OBJID
                                    pm.setInt(++i, questid);// 任務編號
                                    pm.execute();
                                } catch (final SQLException e) {
                                    _log.error(e.getLocalizedMessage(), e);
                                } finally {
                                    SQLUtil.close(pm);
                                    SQLUtil.close(co);
                                }
                            }
                        }
                    }
                } else {
                    pc.sendPackets(new S_SystemMessage("\\aH簽到資料異常!"));
                    pc.sendPackets(new S_CloseList(pc.getId()));
                    pc.setCmd(-1);
                }
            } else if (cmd.matches("NP2")) {
                Day_Signature Day = Day_Signature.get().getDay(pc.getCmd());
                if (Day != null) {
                    SimpleDateFormat sdFormat = new SimpleDateFormat("MMdd");
                    Date date = new Date();
                    String strDate = sdFormat.format(date);
                    if (Integer.parseInt(strDate) < Day.getDay()) {
                        pc.sendPackets(new S_SystemMessage("\\aH你選擇的簽到獎勵領取日期還沒到喔!"));
                        pc.sendPackets(new S_CloseList(pc.getId()));
                        pc.setCmd(-1);
                    } else if (Integer.parseInt(strDate) == Day.getDay()) {
                        pc.sendPackets(new S_SystemMessage("\\aH你選擇的簽到獎勵可以直接領取不需要補簽!"));
                        pc.sendPackets(new S_CloseList(pc.getId()));
                        pc.setCmd(-1);
                    } else {
                        L1Item MakeItem = ItemTable.get().getTemplate(Day.getMakeUp());
                        final StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("補簽需要消耗的道具【").append(MakeItem.getName()).append("(").append(Day.getMakeUpC()).append(")】,");
                        stringBuilder.append("【").append(Day.getMsg()).append("】,");
                        if (Day.getItem() != null) {
                            int index = 0;
                            final String[] item = Day.getItem().split(",");
                            for (String _item : item) {
                                final int itemid = Integer.parseInt(_item);
                                L1Item items = ItemTable.get().getTemplate(itemid);
                                final String[] Enchant = Day.getEnchant().split(",");
                                String enchant = Enchant[index];
                                final String[] Count = Day.getCount().split(",");
                                String count = Count[index];
                                index++;
                                stringBuilder.append("+【").append(enchant).append("】").append(items.getName()).append("(").append(count).append("),");
                            }
                        } else {
                            stringBuilder.append("無道具可領取,");
                        }
                        final String[] msg = stringBuilder.toString().split(",");
                        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_daySing_2", msg));
                    }
                }
            } else if (cmd.matches("NP3")) {
                if (!pc.isGm()) {
                    pc.sendPackets(new S_SystemMessage("\\aH目前只提供GM使用!"));
                    pc.sendPackets(new S_CloseList(pc.getId()));
                    pc.setCmd(-1);
                    return;
                }
                Day_Signature Day = Day_Signature.get().getDay(pc.getCmd());
                if (Day != null) {
                    final int questid = Day.getDay() + 33000;
                    if (pc.getQuest().isEnd(questid)) {
                        pc.sendPackets(new S_SystemMessage("\\aH你選擇的簽到獎勵已經領過囉!"));
                        pc.sendPackets(new S_CloseList(pc.getId()));
                        pc.setCmd(-1);
                    } else {
                        if (Day.getItem() != null) {
                            if (pc.getInventory().checkItem(Day.getMakeUp(), Day.getMakeUpC())) {
                                pc.getInventory().consumeItem(Day.getMakeUp(), Day.getMakeUpC());
                                int index = 0;
                                final String[] item = Day.getItem().split(",");
                                for (String _item : item) {
                                    final int itemid = Integer.parseInt(_item);
                                    final String[] Enchant = Day.getEnchant().split(",");
                                    String enchant = Enchant[index];
                                    final String[] Count = Day.getCount().split(",");
                                    String count = Count[index];
                                    index++;
                                    final L1ItemInstance ItemX = ItemTable.get().createItem(itemid);
                                    if (ItemX.isStackable()) {
                                        pc.getInventory().storeItem(itemid, Integer.parseInt(count));
                                    } else {
                                        ItemX.setEnchantLevel(Integer.parseInt(enchant));
                                        ItemX.setIdentified(true);
                                        pc.getInventory().storeItem(ItemX);
                                    }
                                    pc.sendPackets(new S_SystemMessage("\\aH恭喜您完成本次簽到獲得簽到獎勵"));
                                    WriteLogTxt.Recording("簽到紀錄", "玩家" + ":【 " + pc.getName() + "】進行補簽獲得  " + Day.getDay() + " 簽到獎勵(時間" + new Timestamp(System.currentTimeMillis()) + ")。");
                                }
                                pc.getQuest().set_end(questid);
                                String add = pc.getName() + "的(" + Day.getDay() + ")簽到領取紀錄";
                                Connection co = null;
                                PreparedStatement pm = null;
                                try {
                                    co = DatabaseFactory.get().getConnection();
                                    pm = co.prepareStatement("UPDATE `character_quests` SET `note`=? WHERE `char_id`=? AND `quest_id`=?");
                                    int i = 0;
                                    pm.setString(++i, add);// 任務進度
                                    pm.setInt(++i, pc.getId());// 人物OBJID
                                    pm.setInt(++i, questid);// 任務編號
                                    pm.execute();
                                } catch (final SQLException e) {
                                    _log.error(e.getLocalizedMessage(), e);
                                } finally {
                                    SQLUtil.close(pm);
                                    SQLUtil.close(co);
                                }
                            } else {
                                pc.sendPackets(new S_SystemMessage("\\aH需求道具不足無法進行補簽!"));
                                pc.sendPackets(new S_CloseList(pc.getId()));
                            }
                        }
                    }
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}