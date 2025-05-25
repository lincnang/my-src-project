package com.add.Tsai;

import com.lineage.DatabaseFactory;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 簽到指令
 *
 * @author hero
 */
public class DaySignatureCmd {
    private static final Log _log = LogFactory.getLog(DaySignatureCmd.class);
    private static DaySignatureCmd _instance;

    public static DaySignatureCmd get() {
        if (_instance == null) {
            _instance = new DaySignatureCmd();
        }
        return _instance;
    }

    public boolean Cmd(final L1PcInstance pc, final String cmd) {
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
                    pc.sendPackets(new S_NPCTalkReturn(pc, "y_daySing_1", msg));
                    pc.setCmd(cmd1);
                    return true;
                }
            } else if (cmd.matches("NP1")) {
                if (!pc.isGm()) {
                    pc.sendPackets(new S_SystemMessage("\\aH目前只提供GM使用!"));
                    pc.sendPackets(new S_CloseList(pc.getId()));
                    pc.setCmd(-1);
                    return false;
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
                return true;
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
                        pc.sendPackets(new S_NPCTalkReturn(pc, "y_daySing_2", msg));
                    }
                }
                return true;
            } else if (cmd.matches("NP3")) {
                if (!pc.isGm()) {
                    pc.sendPackets(new S_SystemMessage("\\aH目前只提供GM使用!"));
                    pc.sendPackets(new S_CloseList(pc.getId()));
                    pc.setCmd(-1);
                    return false;
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
                return true;
            }
            return false;
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    private void DOLLAllSet(final L1PcInstance pc) {
        try {
            int str = 0;
            int dex = 0;
            int con = 0;
            int Int = 0;
            int wis = 0;
            int cha = 0;
            int ac = 0;
            int hp = 0;
            int mp = 0;
            int hpr = 0;
            int mpr = 0;
            int dmg = 0;
            int bdmg = 0;
            int hit = 0;
            int bhit = 0;
            int dr = 0;
            int mdr = 0;
            int sp = 0;
            int mhit = 0;
            int mr = 0;
            int f = 0;
            int wind = 0;
            int w = 0;
            int e = 0;
            for (int i = 0; i <= dollTable.get().dollSize(); i++) {
                final doll doll = dollTable.get().getDoll(i);
                if (doll != null) {
                    if (pc.getQuest().get_step(doll.getQuestId()) != 0) {
                        str += doll.getAddStr();
                        dex += doll.getAddDex();
                        con += doll.getAddCon();
                        Int += doll.getAddInt();
                        wis += doll.getAddWis();
                        cha += doll.getAddCha();
                        ac += doll.getAddAc();
                        hp += doll.getAddHp();
                        mp += doll.getAddMp();
                        hpr += doll.getAddHpr();
                        mpr += doll.getAddMpr();
                        dmg += doll.getAddDmg();
                        bdmg += doll.getAddBowDmg();
                        hit += doll.getAddHit();
                        bhit += doll.getAddBowHit();
                        dr += doll.getAddDmgR();
                        mdr += doll.getAddMagicDmgR();
                        sp += doll.getAddSp();
                        mhit += doll.getAddMagicHit();
                        mr += doll.getAddMr();
                        f += doll.getAddFire();
                        wind += doll.getAddWind();
                        e += doll.getAddEarth();
                        w += doll.getAddWater();
                    }
                }
            }
            for (int i = 0; i <= dollSetTable.get().CardDollSize(); i++) {//檢查變身組合DB資料
                final dollPolySet dolls = dollSetTable.get().getDoll(i);
                if (dolls != null) {
                    if (pc.getQuest().get_step(dolls.getQuestId()) != 0) {
                        str += dolls.getAddStr();
                        dex += dolls.getAddDex();
                        con += dolls.getAddCon();
                        Int += dolls.getAddInt();
                        wis += dolls.getAddWis();
                        cha += dolls.getAddCha();
                        ac += dolls.getAddAc();
                        hp += dolls.getAddHp();
                        mp += dolls.getAddMp();
                        hpr += dolls.getAddHpr();
                        mpr += dolls.getAddMpr();
                        dmg += dolls.getAddDmg();
                        bdmg += dolls.getAddBowDmg();
                        hit += dolls.getAddHit();
                        bhit += dolls.getAddBowHit();
                        dr += dolls.getAddDmgR();
                        mdr += dolls.getAddMagicDmgR();
                        sp += dolls.getAddSp();
                        mhit += dolls.getAddMagicHit();
                        mr += dolls.getAddMr();
                        f += dolls.getAddFire();
                        wind += dolls.getAddWind();
                        e += dolls.getAddEarth();
                        w += dolls.getAddWater();
                    }
                }
            }
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("力量 +").append(str).append(",");
            stringBuilder.append("敏捷 +").append(dex).append(",");
            stringBuilder.append("體質 +").append(con).append(",");
            stringBuilder.append("智力 +").append(Int).append(",");
            stringBuilder.append("精神 +").append(wis).append(",");
            stringBuilder.append("魅力 +").append(cha).append(",");
            stringBuilder.append("防禦提升 +").append(ac).append(",");
            stringBuilder.append("HP +").append(hp).append(",");
            stringBuilder.append("MP +").append(mp).append(",");
            stringBuilder.append("血量回復 +").append(hpr).append(",");
            stringBuilder.append("魔力回復 +").append(mpr).append(",");
            stringBuilder.append("近距離傷害 +").append(dmg).append(",");
            stringBuilder.append("遠距離傷害 +").append(bdmg).append(",");
            stringBuilder.append("近距離命中 +").append(hit).append(",");
            stringBuilder.append("遠距離命中 +").append(bhit).append(",");
            stringBuilder.append("物理傷害減免 +").append(dr).append(",");
            stringBuilder.append("魔法傷害減免 +").append(mdr).append(",");
            stringBuilder.append("魔攻 +").append(sp).append(",");
            stringBuilder.append("魔法命中 +").append(mhit).append(",");
            stringBuilder.append("魔法防禦 +").append(mr).append(",");
            stringBuilder.append("火屬性防禦 +").append(f).append(",");
            stringBuilder.append("風屬性防禦 +").append(wind).append(",");
            stringBuilder.append("地屬性防禦 +").append(e).append(",");
            stringBuilder.append("水屬性防禦 +").append(w).append(",");
            final String[] clientStrAry = stringBuilder.toString().split(",");
            pc.sendPackets(new S_NPCTalkReturn(pc, "Book_D11", clientStrAry));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void DollSet(final L1PcInstance pc) {
        try {
            final StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i <= dollSetTable.get().CardDollSize(); i++) {//檢查變身組合DB資料
                final dollPolySet dolls = dollSetTable.get().getDoll(i);
                if (dolls != null) {
                    boolean IsIndex = true;
                    if (IsIndex) {
                        stringBuilder.append(dolls.getMsg1()).append(",");
                        int k = 0;
                        for (int j = 0; j < dolls.getNeedQuest().length; j++) {
                            if (pc.getQuest().get_step(dolls.getNeedQuest()[j]) != 0) {
                                stringBuilder.append(dolls.getNeedName()[j]).append("(已收藏),");
                                k++;
                            } else {
                                stringBuilder.append(dolls.getNeedName()[j]).append("(未收藏),");
                            }
                        }
                        if (k == dolls.getNeedQuest().length) {
                            if (dolls.getAddStr() != 0) {
                                stringBuilder.append("力量 +").append(dolls.getAddStr()).append(",");
                            }
                            if (dolls.getAddDex() != 0) {
                                stringBuilder.append("敏捷 +").append(dolls.getAddDex()).append(",");
                            }
                            if (dolls.getAddCon() != 0) {
                                stringBuilder.append("體質 +").append(dolls.getAddCon()).append(",");
                            }
                            if (dolls.getAddInt() != 0) {
                                stringBuilder.append("智力 +").append(dolls.getAddInt()).append(",");
                            }
                            if (dolls.getAddWis() != 0) {
                                stringBuilder.append("精神 +").append(dolls.getAddWis()).append(",");
                            }
                            if (dolls.getAddCha() != 0) {
                                stringBuilder.append("魅力 +").append(dolls.getAddCha()).append(",");
                            }
                            if (dolls.getAddAc() != 0) {
                                stringBuilder.append("防禦提升 +").append(dolls.getAddAc()).append(",");
                            }
                            if (dolls.getAddHp() != 0) {
                                stringBuilder.append("HP +").append(dolls.getAddHp()).append(",");
                            }
                            if (dolls.getAddMp() != 0) {
                                stringBuilder.append("MP +").append(dolls.getAddMp()).append(",");
                            }
                            if (dolls.getAddHpr() != 0) {
                                stringBuilder.append("血量回復 +").append(dolls.getAddHpr()).append(",");
                            }
                            if (dolls.getAddMpr() != 0) {
                                stringBuilder.append("魔力回復 +").append(dolls.getAddMpr()).append(",");
                            }
                            if (dolls.getAddDmg() != 0) {
                                stringBuilder.append("近距離傷害 +").append(dolls.getAddDmg()).append(",");
                            }
                            if (dolls.getAddBowDmg() != 0) {
                                stringBuilder.append("遠距離傷害 +").append(dolls.getAddBowDmg()).append(",");
                            }
                            if (dolls.getAddHit() != 0) {
                                stringBuilder.append("近距離命中 +").append(dolls.getAddHit()).append(",");
                            }
                            if (dolls.getAddBowHit() != 0) {
                                stringBuilder.append("遠距離命中 +").append(dolls.getAddBowHit()).append(",");
                            }
                            if (dolls.getAddDmgR() != 0) {
                                stringBuilder.append("物理傷害減免 +").append(dolls.getAddDmgR()).append(",");
                            }
                            if (dolls.getAddMagicDmgR() != 0) {
                                stringBuilder.append("魔法傷害減免 +").append(dolls.getAddMagicDmgR()).append(",");
                            }
                            if (dolls.getAddSp() != 0) {
                                stringBuilder.append("魔攻 +").append(dolls.getAddSp()).append(",");
                            }
                            if (dolls.getAddMagicHit() != 0) {
                                stringBuilder.append("魔法命中 +").append(dolls.getAddMagicHit()).append(",");
                            }
                            if (dolls.getAddMr() != 0) {
                                stringBuilder.append("魔法防禦 +").append(dolls.getAddMr()).append(",");
                            }
                            if (dolls.getAddFire() != 0) {
                                stringBuilder.append("火屬性防禦 +").append(dolls.getAddFire()).append(",");
                            }
                            if (dolls.getAddWind() != 0) {
                                stringBuilder.append("風屬性防禦 +").append(dolls.getAddWind()).append(",");
                            }
                            if (dolls.getAddEarth() != 0) {
                                stringBuilder.append("地屬性防禦 +").append(dolls.getAddEarth()).append(",");
                            }
                            if (dolls.getAddWater() != 0) {
                                stringBuilder.append("水屬性防禦 +").append(dolls.getAddWater()).append(",");
                            }
                            stringBuilder.append("<以上為此套卡能力加成>,");
                        }
                    }
                }
            }
            final String[] clientStrAry = stringBuilder.toString().split(",");
            pc.sendPackets(new S_NPCTalkReturn(pc, "Book_D10", clientStrAry));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public boolean PolyCmd(final L1PcInstance pc, final String cmd) {
        try {
            boolean ok = false;
            final StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i <= dollTable.get().dollSize(); i++) {
                final doll doll = dollTable.get().getDoll(i);
                if (doll != null) {
                    if (cmd.equals(doll.getCmd())) {
                        pc.setDollId(i);
                        stringBuilder.append(doll.getMsg2()).append(",");
                        stringBuilder.append(doll.getAddStr()).append(",");
                        stringBuilder.append(doll.getAddDex()).append(",");
                        stringBuilder.append(doll.getAddCon()).append(",");
                        stringBuilder.append(doll.getAddInt()).append(",");
                        stringBuilder.append(doll.getAddWis()).append(",");
                        stringBuilder.append(doll.getAddCha()).append(",");
                        stringBuilder.append(doll.getAddAc()).append(",");
                        stringBuilder.append(doll.getAddHp()).append(",");
                        stringBuilder.append(doll.getAddMp()).append(",");
                        stringBuilder.append(doll.getAddHpr()).append(",");
                        stringBuilder.append(doll.getAddMpr()).append(",");
                        stringBuilder.append(doll.getAddDmg()).append(",");
                        stringBuilder.append(doll.getAddBowDmg()).append(",");
                        stringBuilder.append(doll.getAddHit()).append(",");
                        stringBuilder.append(doll.getAddBowHit()).append(",");
                        stringBuilder.append(doll.getAddDmgR()).append(",");
                        stringBuilder.append(doll.getAddMagicDmgR()).append(",");
                        stringBuilder.append(doll.getAddSp()).append(",");
                        stringBuilder.append(doll.getAddMagicHit()).append(",");
                        stringBuilder.append(doll.getAddMr()).append(",");
                        stringBuilder.append(doll.getAddFire()).append(",");
                        stringBuilder.append(doll.getAddWind()).append(",");
                        stringBuilder.append(doll.getAddEarth()).append(",");
                        stringBuilder.append(doll.getAddWater()).append(",");
                        if (pc.getQuest().get_step(doll.getQuestId()) != 0) {
                            stringBuilder.append("已收藏,");
                        } else {
                            stringBuilder.append("未收藏,");
                        }
                        stringBuilder.append(doll.getShanbi()).append(",");
                        stringBuilder.append(doll.getHuibi()).append(",");
                        stringBuilder.append(doll.getYaoshui()).append(",");
                        stringBuilder.append(doll.getFuzhong()).append(",");
                        stringBuilder.append(doll.getExp()).append(",");
                        stringBuilder.append(doll.getHunmi()).append(",");
                        stringBuilder.append(doll.getZhicheng()).append(",");
                        stringBuilder.append(doll.getShihua()).append(",");
                        stringBuilder.append(doll.getHanbing()).append(",");
                        stringBuilder.append(doll.getAnhei()).append(",");
                        stringBuilder.append(doll.getShuimian()).append(",");
                        final String[] clientStrAry = stringBuilder.toString().split(",");
                        pc.sendPackets(new S_NPCTalkReturn(pc, "Book_D0", clientStrAry));
                        ok = true;
                        break;
                    }
                }
            }
            if (ok) {
                return true;
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }
}