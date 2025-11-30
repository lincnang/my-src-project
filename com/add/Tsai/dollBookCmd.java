package com.add.Tsai;

import com.lineage.data.item_etcitem.doll.Magic_Doll;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
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

    public void DOLLAllSet(final L1PcInstance pc) {
        try {
            // 屬性名稱
            String[] attributes = { "力量", "敏捷", "體質", "智力", "精神", "魅力", "防禦提升", "HP", "MP", "血量回復", "魔力回復", "近距離傷害", "遠距離傷害", "近距離命中", "遠距離命中", "物理傷害減免", "魔法傷害減免", "魔攻", "魔法命中", "魔法防禦", "火屬性防禦", "風屬性防禦", "地屬性防禦", "水屬性防禦" };

            // 統計最大值與玩家目前已獲得值
            Map<String, Integer> maxMap = new LinkedHashMap<>();
            Map<String, Integer> playerMap = new LinkedHashMap<>();
            for (String attr : attributes) {
                maxMap.put(attr, 0);
                playerMap.put(attr, 0);
            }

            int setCount = dollSetTable.get().CardDollSize();
            for (int i = 0; i <= setCount; i++) {
                final dollPolySet set = dollSetTable.get().getDoll(i);
                if (set == null) continue;

                // 計算最大值
                maxMap.put("力量", maxMap.get("力量") + set.getAddStr());
                maxMap.put("敏捷", maxMap.get("敏捷") + set.getAddDex());
                maxMap.put("體質", maxMap.get("體質") + set.getAddCon());
                maxMap.put("智力", maxMap.get("智力") + set.getAddInt());
                maxMap.put("精神", maxMap.get("精神") + set.getAddWis());
                maxMap.put("魅力", maxMap.get("魅力") + set.getAddCha());
                maxMap.put("防禦提升", maxMap.get("防禦提升") + set.getAddAc());
                maxMap.put("HP", maxMap.get("HP") + set.getAddHp());
                maxMap.put("MP", maxMap.get("MP") + set.getAddMp());
                maxMap.put("血量回復", maxMap.get("血量回復") + set.getAddHpr());
                maxMap.put("魔力回復", maxMap.get("魔力回復") + set.getAddMpr());
                maxMap.put("近距離傷害", maxMap.get("近距離傷害") + set.getAddDmg());
                maxMap.put("遠距離傷害", maxMap.get("遠距離傷害") + set.getAddBowDmg());
                maxMap.put("近距離命中", maxMap.get("近距離命中") + set.getAddHit());
                maxMap.put("遠距離命中", maxMap.get("遠距離命中") + set.getAddBowHit());
                maxMap.put("物理傷害減免", maxMap.get("物理傷害減免") + set.getAddDmgR());
                maxMap.put("魔法傷害減免", maxMap.get("魔法傷害減免") + set.getAddMagicDmgR());
                maxMap.put("魔攻", maxMap.get("魔攻") + set.getAddSp());
                maxMap.put("魔法命中", maxMap.get("魔法命中") + set.getAddMagicHit());
                maxMap.put("魔法防禦", maxMap.get("魔法防禦") + set.getAddMr());
                maxMap.put("火屬性防禦", maxMap.get("火屬性防禦") + set.getAddFire());
                maxMap.put("風屬性防禦", maxMap.get("風屬性防禦") + set.getAddWind());
                maxMap.put("地屬性防禦", maxMap.get("地屬性防禦") + set.getAddEarth());
                maxMap.put("水屬性防禦", maxMap.get("水屬性防禦") + set.getAddWater());

                // 計算玩家目前已獲得（有解鎖才加）
                if (dollQuestTable.get().IsQuest(pc, set.getQuestId())) {
                    playerMap.put("力量", playerMap.get("力量") + set.getAddStr());
                    playerMap.put("敏捷", playerMap.get("敏捷") + set.getAddDex());
                    playerMap.put("體質", playerMap.get("體質") + set.getAddCon());
                    playerMap.put("智力", playerMap.get("智力") + set.getAddInt());
                    playerMap.put("精神", playerMap.get("精神") + set.getAddWis());
                    playerMap.put("魅力", playerMap.get("魅力") + set.getAddCha());
                    playerMap.put("防禦提升", playerMap.get("防禦提升") + set.getAddAc());
                    playerMap.put("HP", playerMap.get("HP") + set.getAddHp());
                    playerMap.put("MP", playerMap.get("MP") + set.getAddMp());
                    playerMap.put("血量回復", playerMap.get("血量回復") + set.getAddHpr());
                    playerMap.put("魔力回復", playerMap.get("魔力回復") + set.getAddMpr());
                    playerMap.put("近距離傷害", playerMap.get("近距離傷害") + set.getAddDmg());
                    playerMap.put("遠距離傷害", playerMap.get("遠距離傷害") + set.getAddBowDmg());
                    playerMap.put("近距離命中", playerMap.get("近距離命中") + set.getAddHit());
                    playerMap.put("遠距離命中", playerMap.get("遠距離命中") + set.getAddBowHit());
                    playerMap.put("物理傷害減免", playerMap.get("物理傷害減免") + set.getAddDmgR());
                    playerMap.put("魔法傷害減免", playerMap.get("魔法傷害減免") + set.getAddMagicDmgR());
                    playerMap.put("魔攻", playerMap.get("魔攻") + set.getAddSp());
                    playerMap.put("魔法命中", playerMap.get("魔法命中") + set.getAddMagicHit());
                    playerMap.put("魔法防禦", playerMap.get("魔法防禦") + set.getAddMr());
                    playerMap.put("火屬性防禦", playerMap.get("火屬性防禦") + set.getAddFire());
                    playerMap.put("風屬性防禦", playerMap.get("風屬性防禦") + set.getAddWind());
                    playerMap.put("地屬性防禦", playerMap.get("地屬性防禦") + set.getAddEarth());
                    playerMap.put("水屬性防禦", playerMap.get("水屬性防禦") + set.getAddWater());
                }
            }

            // 組合前端顯示
            StringBuilder stringBuilder = new StringBuilder();
            for (String attr : attributes) {
                int now = playerMap.get(attr);
                int max = maxMap.get(attr);
                if (max == 0) continue; // 這個能力在所有組合都沒有
                stringBuilder.append(attr)
                        .append(" +").append(now)
                        .append(" (").append(now).append("/").append(max).append("),");
            }
            final String[] clientStrAry = stringBuilder.toString().split(",");
            pc.sendPackets(new S_NPCTalkReturn(pc, "Book_D11", clientStrAry));
        } catch (final Exception e) {
            e.printStackTrace();
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
                        // 修復：娃娃套卡應該使用 dollQuestTable 檢查任務狀態，而不是 CardQuestTable
                        if (!dollQuestTable.get().IsQuest(pc, dolls.getNeedQuest()[j])) {
                            anyTaskUncompleted = true;
                            stringBuilder.append(dolls.getNeedName()[j]).append("(未開啟),");
                        } else {
                            // 任務已完成，顯示已開啟狀態（可選）
                            // stringBuilder.append(dolls.getNeedName()[j]).append("(已開啟),");
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