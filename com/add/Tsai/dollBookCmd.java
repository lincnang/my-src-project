package com.add.Tsai;

import com.lineage.data.item_etcitem.doll.Magic_Doll;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 娃娃卡冊指令
 *
 * @author hero
 */
public class dollBookCmd {
    private static final Log _log = LogFactory.getLog(dollBookCmd.class);
    private static dollBookCmd _instance;

    public static dollBookCmd get() {
        if (_instance == null) {
            _instance = new dollBookCmd();
        }
        return _instance;
    }

    public boolean Cmd(final L1PcInstance pc, final String cmd) {
        try {
            final StringBuilder stringBuilder = new StringBuilder();
            int size = dollTable.get().dollSize();
            for (int i = 0; i <= size; i++) {
                final doll doll1 = dollTable.get().getDoll(i);
                //獲取列表id
                if (doll1 != null) {//列表ID空白不啟動
                    if (dollQuestTable.get().IsQuest(pc, doll1.getQuestId())) {
                        stringBuilder.append(String.valueOf(doll1.getAddcgfxid())).append(",");
                    } else {
                        stringBuilder.append(String.valueOf(doll1.getAddhgfxid())).append(",");
                    }
                }
            }
            final String[] msg = stringBuilder.toString().split(",");//從0開始分裂以逗號為單位
            boolean ok = false;
            switch (cmd) {
                case "D_1":
                    ok = true;
                    pc.setDollId(0);
                    pc.sendPackets(new S_NPCTalkReturn(pc, "Book_D02", msg));
                    break;
                case "D_2":
                    pc.sendPackets(new S_NPCTalkReturn(pc, "Book_D03", msg));
                    ok = true;
                    pc.setDollId(0);
                    break;
                case "D_3":
                    pc.sendPackets(new S_NPCTalkReturn(pc, "Book_D04", msg));
                    ok = true;
                    pc.setDollId(0);
                    break;
                case "D_4":
                    pc.sendPackets(new S_NPCTalkReturn(pc, "Book_D05", msg));
                    ok = true;
                    pc.setDollId(0);
                    break;
                case "D_5":
                    pc.sendPackets(new S_NPCTalkReturn(pc, "Book_D06", msg));
                    ok = true;
                    pc.setDollId(0);
                    break;
                case "D_6":
                    pc.sendPackets(new S_NPCTalkReturn(pc, "Book_D07", msg));
                    ok = true;
                    pc.setDollId(0);
                    break;
                case "D_7":
                    pc.sendPackets(new S_NPCTalkReturn(pc, "Book_D08", msg));
                    ok = true;
                    pc.setDollId(0);
                    break;
                case "dollc":
                    polyDoll(pc);
                    ok = true;
                    break;
                case "dollr":
                    doll dollr = dollTable.get().getDoll(pc.getDollId());
                    if (dollr != null) {
                        if (pc.getDoll(dollr.getPolyItemId()) != null) {
                            // 娃娃收回
                            pc.getDoll(dollr.getPolyItemId()).deleteDoll();
                        }
                    }
                    break;
                case "doolset":
                    int questId = dollTable.get().getQuestIdByDollId(pc.getDollId());
                    DollSet(pc, questId);
                    ok = true;
                    break;
                case "doolset2":
                    DOLLAllSet(pc);
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

    public void polyDoll(final L1PcInstance pc) {
        doll doll = dollTable.get().getDoll(pc.getDollId());
        if (doll != null) {
            if (dollQuestTable.get().IsQuest(pc, doll.getQuestId())) {
                if (doll.getPolyId() != 0) {
                    if (doll.getPolyItemId() != 0) {
                        if (pc.getInventory().checkItem(doll.getPolyItemId(), doll.getPolyItemCount())) {
                            // 變身娃娃的動作
                            L1ItemInstance item = new L1ItemInstance();
                            item.setId(doll.getPolyItemId());
                            item.setItemId(doll.getPolyId());
                            item.setCount(doll.getPolyItemCount());
                            Magic_Doll.get().execute(null, pc, item);
                            pc.getInventory().consumeItem(doll.getPolyItemId(), doll.getPolyItemCount());
                            // 紀錄最後一次招喚的娃娃ID
                            pc.setLastDollId(pc.getDollId());
                        } else {
                            pc.sendPackets(new S_SystemMessage("娃娃需求道具不足"));
                        }
                    }
                } else {
                    pc.sendPackets(new S_SystemMessage("無法召喚娃娃"));
                }
            }
        }
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
                    if (dollQuestTable.get().IsQuest(pc, doll.getQuestId())) {
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
                    if (dollQuestTable.get().IsQuest(pc, dolls.getQuestId())) {
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


    private void DollSet(final L1PcInstance pc, final int questId) {
        try {
            final StringBuilder stringBuilder = new StringBuilder();//建立一個新的字符串
            dollSetTable dollJketTable = dollSetTable.get();//獲取變身組合套卡
            for (int i = 0; i < dollJketTable.CardDollSize(); i++) {//檢查變身組合DB資料

                final dollPolySet dolls = dollJketTable.getDoll(i);//獲取卡片

                if (dolls != null && dolls.hasGroup()) {//檢查變身組合DB資料

                    boolean anyTaskUncompleted = false;

                    if (!dolls.hasNeedQuest(questId))
                        continue;
                    ;

                    stringBuilder.append(dolls.getMsg1()).append(",");

                    for (int j = 0; j < dolls.getNeedQuest().length; j++) {
                        if (!CardQuestTable.get().IsQuest(pc, dolls.getNeedQuest()[j])) {
                            anyTaskUncompleted = true;
                            stringBuilder.append(dolls.getNeedName()[j]).append("(未開啟),");
                        }
                    }

                    if (anyTaskUncompleted) {
                        appendStats(stringBuilder, dolls);
                    } else {

                        int lastCommaIndex = stringBuilder.lastIndexOf(dolls.getMsg1() + ",");
                        stringBuilder.delete(lastCommaIndex, stringBuilder.length());
                    }
                }
            }

            if (stringBuilder.length() > 0) {
                final String[] clientStrAry = stringBuilder.toString().split(",");
                pc.sendPackets(new S_NPCTalkReturn(pc, "Book_D10", clientStrAry));
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void appendStats(StringBuilder stringBuilder, dollPolySet cards) {
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

    public boolean PolyCmd(final L1PcInstance pc, final String cmd) {
        try {//變身卡指令
            boolean ok = false;//變身卡指令
            final StringBuilder stringBuilder = new StringBuilder();//建立一個新的字符串
            for (int i = 0; i <= dollTable.get().dollSize(); i++) {//檢查變身卡DB資料
                final doll dolls = dollTable.get().getDoll(i);//獲取卡片
                if (dolls != null) {//檢查變身卡DB資料
                    if (cmd.equals(dolls.getCmd())) {//檢查變身卡DB資料
                        pc.setDollId(i);

                        if (dollQuestTable.get().IsQuest(pc, dolls.getQuestId())) {
                            stringBuilder.append(dolls.getAddcgfxid()).append(",");    // 0
                        } else {
                            stringBuilder.append(dolls.getAddhgfxid()).append(",");    // 0
                        }
                        stringBuilder.append(dolls.getMsg2()).append(",");//1
                        stringBuilder.append(dolls.getMsg1()).append(",");//2


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