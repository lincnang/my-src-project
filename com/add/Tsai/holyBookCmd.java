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
            Integer[] holyIds = holyTable.get().getHolyIndex();
            for (Integer id : holyIds) {
                final holy holy1 = holyTable.get().getHoly(id);
                //獲取列表id
                if (holy1 != null) {//列表ID空白不啟動
                    // 聖物系統使用共用表格 character_娃娃卡帳號
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
                    // 聖物套卡的「需求的變身卡編號」對應聖物表的流水號
                    // 需要用聖物表來取得對應的聖物任務編號 (24000+)
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
            // 聖物系統使用共用表格 character_娃娃卡帳號
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

    public void HolyAllSet(final L1PcInstance pc) {
        try {
            // 用 Map 來存能力名稱→[已獲得, 可獲得最大值]
            String[] names = {
                    "力量", "敏捷", "體質", "智力", "精神", "魅力",
                    "防禦提升", "HP", "MP", "血量回復", "魔力回復",
                    "近距離傷害", "遠距離傷害", "近距離命中", "遠距離命中",
                    "物理傷害減免", "魔法傷害減免", "魔攻", "魔法命中", "魔法防禦",
                    "火屬性防禦", "風屬性防禦", "地屬性防禦", "水屬性防禦"
            };
            // 索引順序與上方一致
            int N = names.length;
            int[] current = new int[N];
            int[] max = new int[N];

            // 索引表，方便寫入
            final int IDX_STR = 0, IDX_DEX = 1, IDX_CON = 2, IDX_INT = 3, IDX_WIS = 4, IDX_CHA = 5,
                    IDX_AC = 6, IDX_HP = 7, IDX_MP = 8, IDX_HPR = 9, IDX_MPR = 10,
                    IDX_DMG = 11, IDX_BDMG = 12, IDX_HIT = 13, IDX_BHIT = 14,
                    IDX_DR = 15, IDX_MDR = 16, IDX_SP = 17, IDX_MHIT = 18, IDX_MR = 19,
                    IDX_FIRE = 20, IDX_WIND = 21, IDX_EARTH = 22, IDX_WATER = 23;

            // 1. 先統計最大能力
            for (Integer id : holyTable.get().getHolyIndex()) {
                final holy holy = holyTable.get().getHoly(id);
                if (holy != null) {
                    max[IDX_STR] += holy.getAddStr();
                    max[IDX_DEX] += holy.getAddDex();
                    max[IDX_CON] += holy.getAddCon();
                    max[IDX_INT] += holy.getAddInt();
                    max[IDX_WIS] += holy.getAddWis();
                    max[IDX_CHA] += holy.getAddCha();
                    max[IDX_AC] += holy.getAddAc();
                    max[IDX_HP] += holy.getAddHp();
                    max[IDX_MP] += holy.getAddMp();
                    max[IDX_HPR] += holy.getAddHpr();
                    max[IDX_MPR] += holy.getAddMpr();
                    max[IDX_DMG] += holy.getAddDmg();
                    max[IDX_BDMG] += holy.getAddBowDmg();
                    max[IDX_HIT] += holy.getAddHit();
                    max[IDX_BHIT] += holy.getAddBowHit();
                    max[IDX_DR] += holy.getAddDmgR();
                    max[IDX_MDR] += holy.getAddMagicDmgR();
                    max[IDX_SP] += holy.getAddSp();
                    max[IDX_MHIT] += holy.getAddMagicHit();
                    max[IDX_MR] += holy.getAddMr();
                    max[IDX_FIRE] += holy.getAddFire();
                    max[IDX_WIND] += holy.getAddWind();
                    max[IDX_EARTH] += holy.getAddEarth();
                    max[IDX_WATER] += holy.getAddWater();
                }
            }
            for (Integer id : holySetTable.get().getHolyIds()) {
                final holyPolySet holys = holySetTable.get().getHoly(id);
                if (holys != null) {
                    max[IDX_STR] += holys.getAddStr();
                    max[IDX_DEX] += holys.getAddDex();
                    max[IDX_CON] += holys.getAddCon();
                    max[IDX_INT] += holys.getAddInt();
                    max[IDX_WIS] += holys.getAddWis();
                    max[IDX_CHA] += holys.getAddCha();
                    max[IDX_AC] += holys.getAddAc();
                    max[IDX_HP] += holys.getAddHp();
                    max[IDX_MP] += holys.getAddMp();
                    max[IDX_HPR] += holys.getAddHpr();
                    max[IDX_MPR] += holys.getAddMpr();
                    max[IDX_DMG] += holys.getAddDmg();
                    max[IDX_BDMG] += holys.getAddBowDmg();
                    max[IDX_HIT] += holys.getAddHit();
                    max[IDX_BHIT] += holys.getAddBowHit();
                    max[IDX_DR] += holys.getAddDmgR();
                    max[IDX_MDR] += holys.getAddMagicDmgR();
                    max[IDX_SP] += holys.getAddSp();
                    max[IDX_MHIT] += holys.getAddMagicHit();
                    max[IDX_MR] += holys.getAddMr();
                    max[IDX_FIRE] += holys.getAddFire();
                    max[IDX_WIND] += holys.getAddWind();
                    max[IDX_EARTH] += holys.getAddEarth();
                    max[IDX_WATER] += holys.getAddWater();
                }
            }
            // 2. 統計玩家已獲得（有解鎖的）
            for (Integer id : holyTable.get().getHolyIndex()) {
                final holy holy = holyTable.get().getHoly(id);
                if (holy != null && dollQuestTable.get().IsQuest(pc, holy.getQuestId())) {
                    current[IDX_STR] += holy.getAddStr();
                    current[IDX_DEX] += holy.getAddDex();
                    current[IDX_CON] += holy.getAddCon();
                    current[IDX_INT] += holy.getAddInt();
                    current[IDX_WIS] += holy.getAddWis();
                    current[IDX_CHA] += holy.getAddCha();
                    current[IDX_AC] += holy.getAddAc();
                    current[IDX_HP] += holy.getAddHp();
                    current[IDX_MP] += holy.getAddMp();
                    current[IDX_HPR] += holy.getAddHpr();
                    current[IDX_MPR] += holy.getAddMpr();
                    current[IDX_DMG] += holy.getAddDmg();
                    current[IDX_BDMG] += holy.getAddBowDmg();
                    current[IDX_HIT] += holy.getAddHit();
                    current[IDX_BHIT] += holy.getAddBowHit();
                    current[IDX_DR] += holy.getAddDmgR();
                    current[IDX_MDR] += holy.getAddMagicDmgR();
                    current[IDX_SP] += holy.getAddSp();
                    current[IDX_MHIT] += holy.getAddMagicHit();
                    current[IDX_MR] += holy.getAddMr();
                    current[IDX_FIRE] += holy.getAddFire();
                    current[IDX_WIND] += holy.getAddWind();
                    current[IDX_EARTH] += holy.getAddEarth();
                    current[IDX_WATER] += holy.getAddWater();
                }
            }
            for (Integer id : holySetTable.get().getHolyIds()) {
                final holyPolySet holys = holySetTable.get().getHoly(id);
                if (holys != null && dollQuestTable.get().IsQuest(pc, holys.getQuestId())) {
                    current[IDX_STR] += holys.getAddStr();
                    current[IDX_DEX] += holys.getAddDex();
                    current[IDX_CON] += holys.getAddCon();
                    current[IDX_INT] += holys.getAddInt();
                    current[IDX_WIS] += holys.getAddWis();
                    current[IDX_CHA] += holys.getAddCha();
                    current[IDX_AC] += holys.getAddAc();
                    current[IDX_HP] += holys.getAddHp();
                    current[IDX_MP] += holys.getAddMp();
                    current[IDX_HPR] += holys.getAddHpr();
                    current[IDX_MPR] += holys.getAddMpr();
                    current[IDX_DMG] += holys.getAddDmg();
                    current[IDX_BDMG] += holys.getAddBowDmg();
                    current[IDX_HIT] += holys.getAddHit();
                    current[IDX_BHIT] += holys.getAddBowHit();
                    current[IDX_DR] += holys.getAddDmgR();
                    current[IDX_MDR] += holys.getAddMagicDmgR();
                    current[IDX_SP] += holys.getAddSp();
                    current[IDX_MHIT] += holys.getAddMagicHit();
                    current[IDX_MR] += holys.getAddMr();
                    current[IDX_FIRE] += holys.getAddFire();
                    current[IDX_WIND] += holys.getAddWind();
                    current[IDX_EARTH] += holys.getAddEarth();
                    current[IDX_WATER] += holys.getAddWater();
                }
            }
            // 3. 輸出 (已獲得/最大)
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < N; i++) {
                sb.append(names[i]).append(" +").append(current[i]);
                sb.append(" (").append(current[i]).append("/").append(max[i]).append("),");
            }
            final String[] clientStrAry = sb.toString().split(",");
            pc.sendPackets(new S_NPCTalkReturn(pc, "Holy_D11", clientStrAry));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }


    private void HolySet(final L1PcInstance pc, final int questId) {
        try {
            final StringBuilder stringBuilder = new StringBuilder();//建立一個新的字符串
            holySetTable holyJketTable = holySetTable.get();//獲取變身組合套卡
            Integer[] holySetIds = holyJketTable.getHolyIds();
            for (Integer id : holySetIds) {//檢查變身組合DB資料

                final holyPolySet holy = holyJketTable.getHoly(id);//獲取卡片
                if (holy != null && holy.hasGroup()) {//檢查變身組合DB資料
                    boolean anyTaskUncompleted = false;

                    if (!holy.hasNeedQuests(questId))
                        continue;
                    stringBuilder.append(holy.getMsg1()).append(",");

                    for (int j = 0; j < holy.getNeedQuest().length; j++) {
                        // 聖物系統使用共用表格 character_娃娃卡帳號
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
            } else {
                pc.sendPackets(new S_SystemMessage("沒有找到相關的聖物套裝資訊。"));
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
            for (Integer id : holyTable.get().getHolyIndex()) {//檢查變身卡DB資料

                final holy holy = holyTable.get().getHoly(id);//獲取卡片
                if (holy != null) {//檢查變身卡DB資料
                    if (cmd.equals(holy.getCmd())) {//檢查變身卡DB資料
                        pc.setHolyId(id);

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