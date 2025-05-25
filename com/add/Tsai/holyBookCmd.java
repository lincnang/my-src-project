package com.add.Tsai;

import com.lineage.data.item_etcitem.doll.Magic_Doll2;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 聖物指令
 *
 * @author hero
 */
public class holyBookCmd {
    private static final Log _log = LogFactory.getLog(holyBookCmd.class);
    private static holyBookCmd _instance;

    public static holyBookCmd get() {
        if (_instance == null) {
            _instance = new holyBookCmd();
        }
        return _instance;
    }

    public boolean Cmd(final L1PcInstance pc, final String cmd) {
        try {
            final StringBuilder stringBuilder = new StringBuilder();
            int size = holyTable.get().holySize();
            for (int i = 0; i <= size; i++) {
                final holy holy1 = holyTable.get().getHoly(i);
                //獲取列表id
                if (holy1 != null) {//列表ID空白不啟動
                    if (dollQuestTable.get().IsQuest(pc, holy1.getQuestId())) {
                        stringBuilder.append(String.valueOf(holy1.getAddcgfxid())).append(",");
                    } else {
                        stringBuilder.append(String.valueOf(holy1.getAddhgfxid())).append(",");
                    }
                }
            }
            final String[] msg = stringBuilder.toString().split(",");//從0開始分裂以逗號為單位
            boolean ok = false;
            switch (cmd) {
                case "H_1":
                    ok = true;
                    pc.setHolyId(0);
                    pc.sendPackets(new S_NPCTalkReturn(pc, "Holy_D02", msg));
                    break;
                case "H_2":
                    pc.sendPackets(new S_NPCTalkReturn(pc, "Holy_D03", msg));
                    ok = true;
                    pc.setHolyId(0);
                    break;
                case "H_3":
                    pc.sendPackets(new S_NPCTalkReturn(pc, "Holy_D04", msg));
                    ok = true;
                    pc.setHolyId(0);
                    break;
                case "H_4":
                    pc.sendPackets(new S_NPCTalkReturn(pc, "Holy_D05", msg));
                    ok = true;
                    pc.setHolyId(0);
                    break;
                case "H_5":
                    pc.sendPackets(new S_NPCTalkReturn(pc, "Holy_D06", msg));
                    ok = true;
                    pc.setHolyId(0);
                    break;
                case "H_6":
                    pc.sendPackets(new S_NPCTalkReturn(pc, "Holy_D07", msg));
                    ok = true;
                    pc.setHolyId(0);
                    break;
                case "H_7":
                    pc.sendPackets(new S_NPCTalkReturn(pc, "Holy_D08", msg));
                    ok = true;
                    pc.setHolyId(0);
                    break;
                case "holyc":
                    polyHoly(pc);
                    ok = true;
                    break;
                case "holyr":
                    holy holyr = holyTable.get().getHoly(pc.getHolyId());
                    if (holyr != null) {
                        if (pc.getDoll2(holyr.getPolyItemId()) != null) {
                            // 娃娃收回
                            pc.getDoll2(holyr.getPolyItemId()).deleteDoll2();
                        }
                    }
                    break;
                case "holyset":
                    int questId = holyTable.get().getQuestIdByHolyId(pc.getHolyId());
                    HolySet(pc, questId);
                    ok = true;
                    break;
                case "holyset2":
                    HolyAllSet(pc);
                    ok = true;
                    break;
            }
            if (ok) {
                return true;
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    public void polyHoly(final L1PcInstance pc) {
        holy holy = holyTable.get().getHoly(pc.getHolyId());
        if (holy != null) {
            if (dollQuestTable.get().IsQuest(pc, holy.getQuestId())) {
                if (holy.getPolyId() != 0) {
                    if (holy.getPolyItemId() != 0) {
                        if (pc.getInventory().checkItem(holy.getPolyItemId(), holy.getPolyItemCount())) {
                            // 變身娃娃的動作
                            L1ItemInstance item = new L1ItemInstance();
                            item.setId(holy.getPolyItemId());
                            item.setItemId(holy.getPolyId());
                            item.setCount(holy.getPolyItemCount());
                            Magic_Doll2.get().execute(null, pc, item);
                            pc.getInventory().consumeItem(holy.getPolyItemId(), holy.getPolyItemCount());
                            // 紀錄最後一次招喚的娃娃ID
                            pc.setLastHolyId2(pc.getHolyId());
                        } else {
                            pc.sendPackets(new S_SystemMessage("聖物需求道具不足"));
                        }
                    }
                } else {
                    pc.sendPackets(new S_SystemMessage("無法召喚聖物"));
                }
            }
        }
    }

    private void HolyAllSet(final L1PcInstance pc) {
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
            for (int i = 0; i <= holyTable.get().holySize(); i++) {
                final holy holy = holyTable.get().getHoly(i);
                if (holy != null) {
                    if (dollQuestTable.get().IsQuest(pc, holy.getQuestId())) {
                        str += holy.getAddStr();
                        dex += holy.getAddDex();
                        con += holy.getAddCon();
                        Int += holy.getAddInt();
                        wis += holy.getAddWis();
                        cha += holy.getAddCha();
                        ac += holy.getAddAc();
                        hp += holy.getAddHp();
                        mp += holy.getAddMp();
                        hpr += holy.getAddHpr();
                        mpr += holy.getAddMpr();
                        dmg += holy.getAddDmg();
                        bdmg += holy.getAddBowDmg();
                        hit += holy.getAddHit();
                        bhit += holy.getAddBowHit();
                        dr += holy.getAddDmgR();
                        mdr += holy.getAddMagicDmgR();
                        sp += holy.getAddSp();
                        mhit += holy.getAddMagicHit();
                        mr += holy.getAddMr();
                        f += holy.getAddFire();
                        wind += holy.getAddWind();
                        e += holy.getAddEarth();
                        w += holy.getAddWater();
                    }
                }
            }
            for (int i = 0; i <= holySetTable.get().HolySize(); i++) {//檢查變身組合DB資料
                final holyPolySet holys = holySetTable.get().getHoly(i);
                if (holys != null) {
                    if (dollQuestTable.get().IsQuest(pc, holys.getQuestId())) {
                        str += holys.getAddStr();
                        dex += holys.getAddDex();
                        con += holys.getAddCon();
                        Int += holys.getAddInt();
                        wis += holys.getAddWis();
                        cha += holys.getAddCha();
                        ac += holys.getAddAc();
                        hp += holys.getAddHp();
                        mp += holys.getAddMp();
                        hpr += holys.getAddHpr();
                        mpr += holys.getAddMpr();
                        dmg += holys.getAddDmg();
                        bdmg += holys.getAddBowDmg();
                        hit += holys.getAddHit();
                        bhit += holys.getAddBowHit();
                        dr += holys.getAddDmgR();
                        mdr += holys.getAddMagicDmgR();
                        sp += holys.getAddSp();
                        mhit += holys.getAddMagicHit();
                        mr += holys.getAddMr();
                        f += holys.getAddFire();
                        wind += holys.getAddWind();
                        e += holys.getAddEarth();
                        w += holys.getAddWater();
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
            pc.sendPackets(new S_NPCTalkReturn(pc, "Holy_D11", clientStrAry));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void HolySet(final L1PcInstance pc, final int questId) {
        try {
            final StringBuilder stringBuilder = new StringBuilder();//建立一個新的字符串
            holySetTable holyJketTable = holySetTable.get();//獲取變身組合套卡
            for (int i = 0; i < holyJketTable.HolySize(); i++) {//檢查變身組合DB資料

                final holyPolySet holy = holyJketTable.getHoly(i);//獲取卡片
                if (holy != null && holy.hasGroup()) {//檢查變身組合DB資料
                    boolean anyTaskUncompleted = false;
                    if (!holy.hasNeedQuests(questId))
                        continue;
                    ;
                    stringBuilder.append(holy.getMsg1()).append(",");

                    for (int j = 0; j < holy.getNeedQuest().length; j++) {
                        if (!dollQuestTable.get().IsQuest(pc, holy.getNeedQuest()[j])) {
                            anyTaskUncompleted = true;
                            stringBuilder.append(holy.getNeedName()[j]).append("(未開啟),");
                        }
                    }
                    if (anyTaskUncompleted) {
                        appendStats(stringBuilder, holy);
                    } else {
                        int lastCommaIndex = stringBuilder.lastIndexOf(holy.getMsg1() + ",");
                        stringBuilder.delete(lastCommaIndex, stringBuilder.length());
                    }
                }
            }
            if (stringBuilder.length() > 0) {
                final String[] clientStrAry = stringBuilder.toString().split(",");
                pc.sendPackets(new S_NPCTalkReturn(pc, "Holy_D10", clientStrAry));
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void appendStats(StringBuilder stringBuilder, holyPolySet cards) {
        Map<String, Integer> attributes = new HashMap<>();
        attributes.put("力量", cards.getAddStr());
        attributes.put("敏捷", cards.getAddDex());
        attributes.put("體質", cards.getAddCon());
        attributes.put("智力", cards.getAddInt());
        attributes.put("精神", cards.getAddWis());
        attributes.put("魅力", cards.getAddCha());
        attributes.put("防禦提升", cards.getAddAc());
        attributes.put("HP", cards.getAddHp());
        attributes.put("MP", cards.getAddMp());
        attributes.put("血量回復", cards.getAddHpr());
        attributes.put("魔力回復", cards.getAddMpr());
        attributes.put("近距離傷害", cards.getAddDmg());
        attributes.put("遠距離傷害", cards.getAddBowDmg());
        attributes.put("近距離命中", cards.getAddHit());
        attributes.put("遠距離命中", cards.getAddBowHit());
        attributes.put("物理傷害減免", cards.getAddDmgR());
        attributes.put("魔法傷害減免", cards.getAddMagicDmgR());
        attributes.put("魔攻", cards.getAddSp());
        attributes.put("魔法命中", cards.getAddMagicHit());
        attributes.put("魔法防禦", cards.getAddMr());
        attributes.put("火屬性防禦", cards.getAddFire());
        attributes.put("風屬性防禦", cards.getAddWind());
        attributes.put("地屬性防禦", cards.getAddEarth());
        attributes.put("水屬性防禦", cards.getAddWater());

        for (Map.Entry<String, Integer> attribute : attributes.entrySet()) {
            if (attribute.getValue() != 0) {
                stringBuilder.append(attribute.getKey()).append(" +").append(attribute.getValue()).append(",");
            }
        }
        stringBuilder.append("<---------------------------->,");
    }


    public boolean PolyCmd(final L1PcInstance pc, final String cmd) { //變身卡指令
        try {//變身卡指令
            boolean ok = false;//變身卡指令
            final StringBuilder stringBuilder = new StringBuilder();//建立一個新的字符串
            for (int i = 0; i <= holyTable.get().holySize(); i++) {//檢查變身卡DB資料

                final holy holy = holyTable.get().getHoly(i);//獲取卡片
                if (holy != null) {//檢查變身卡DB資料
                    if (cmd.equals(holy.getCmd())) {//檢查變身卡DB資料
                        pc.setHolyId(i);

                        if (dollQuestTable.get().IsQuest(pc, holy.getQuestId())) {
                            stringBuilder.append(holy.getAddcgfxid()).append(",");    // 0
                        } else {
                            stringBuilder.append(holy.getAddhgfxid()).append(",");    // 0
                        }
                        stringBuilder.append(holy.getMsg2()).append(",");//1
                        stringBuilder.append(holy.getMsg1()).append(",");//2


                        final String[] clientStrAry = stringBuilder.toString().split(",");//從0開始分裂以逗號為單位
                        pc.sendPackets(new S_NPCTalkReturn(pc, "Holy_D0", clientStrAry));
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