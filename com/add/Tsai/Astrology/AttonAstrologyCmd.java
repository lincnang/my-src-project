package com.add.Tsai.Astrology;

import com.lineage.server.datatables.lock.AstrologyQuestReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 新守護星盤 AttonAstrologyCmd
 * 完全獨立於舊星盤邏輯的專用指令與資料存取
 *
 * @author hero
 */
public class AttonAstrologyCmd {

    private static final Log _log = LogFactory.getLog(AttonAstrologyCmd.class);

    // 玩家技能啟動紀錄
    private static final Map<Integer, Integer> _ASTROLOGY_SKILLS = new ConcurrentHashMap<>();
    private static final Map<Integer, String> _ASTROLOGY_SKILLS2 = new ConcurrentHashMap<>();


    private static AttonAstrologyCmd _instance;

    public static AttonAstrologyCmd get() {
        if (_instance == null) {
            _instance = new AttonAstrologyCmd();
        }
        return _instance;
    }

    /**
     * 取得玩家已啟動技能編號
     */
    public int getAstrologySkillActive(L1PcInstance pc) {
        return _ASTROLOGY_SKILLS.getOrDefault(pc.getId(), -1);
    }

    /**
     * 指令入口
     */
    public boolean Cmd(final L1PcInstance pc, final String cmd) {


        if (!"astr2".equals(cmd) && pc.getAstrologyPlateType() != 1) return false;
        try {
            final String[] msg = "".split(",");
            // 主畫面
            if ("astr2".equals(cmd)) {
//                pc.setAstrologyPlateType(1);  // ★★★★★
//                _log.warn("[AttonAstrologyCmd] astr2設型態: plateType=" + pc.getAstrologyPlateType());
//                updateUI(pc, "t_atton");
                pc.sendPackets(new S_SystemMessage("阿頓星盤功能 尚未開啟",1)); // 直接給訊息
                return true;
            }
            _log.warn("[AttonAstrologyCmd] 收到指令: " + cmd + ", plateType=" + pc.getAstrologyPlateType());

            // 點擊星盤節點
            if (cmd.startsWith("tat_")) {
                int id = Integer.parseInt(cmd.substring(4));
                AttonAstrologyData data = AttonAstrologyTable.get().getData(id);
                if (data == null) {
                    pc.sendPackets(new S_SystemMessage("星盤編號異常:" + id + "，請通知管理員"));
                    return true;
                }
                if (checkEndAstrologyQuest(pc, id)) {
                    return true;
                }
                AstrologyQuest quest = AstrologyQuestReading.get().get(pc.getId(), id);
                if (quest == null) {
                    quest = new AstrologyQuest(pc.getId(), id, data.get_cards());
                    AstrologyQuestReading.get().storeQuest(pc.getId(), id, data.get_cards());
                }
                // 檢查守護石
                if (!pc.getInventory().checkItem(11618, 1)) {
                    if (data.getQuestId() > 0 && !pc.getQuest().isEnd(data.getNeedQuestId())) {
                        pc.sendPackets(new S_SystemMessage("前置任務未完成"));
                        updateUI(pc, "t_atton");
                        return true;
                    }
                }
                // 已完成
                if (pc.getQuest().isEnd(data.getQuestId())) {
                    pc.addAstrologyPower(data, id);
                    if (data.getSkillId() > 0 && getAstrologySkillActive(pc) == data.getSkillId()) {
                        pc.sendPackets(new S_SystemMessage("星盤已完成"));
                        updateUI(pc, "t_atton");
                        return true;
                    } else {
                        _ASTROLOGY_SKILLS.put(pc.getId(), data.getSkillId());
                        _ASTROLOGY_SKILLS2.put(pc.getId(), data.getNote());
                        pc.sendPackets(new S_SystemMessage("星盤技能：" + data.getNote() + "已激活！", 1));
                        return true;
                    }
                }
                pc.setAstrologyType(id);
                pc.sendPackets(new S_NPCTalkReturn(pc, "t_but" + quest.getNum(), msg));
                return true;
            }

            // 抽卡解鎖
            if (cmd.startsWith("abu")) {
                int astrologyType = pc.getAstrologyType();
                AstrologyQuest quest = AstrologyQuestReading.get().get(pc.getId(), astrologyType);
                if (quest == null) {
                    return true;
                }
                if (!pc.getInventory().checkItem(11618, 1)) {
                    pc.sendPackets(new S_SystemMessage("缺少守護石，無法啟用！"));
                    pc.sendPackets(new S_NPCTalkReturn(pc, "t_but" + quest.getNum(), msg));
                    return true;
                }
                pc.getInventory().consumeItem(11618, 1);
                int rnd = ThreadLocalRandom.current().nextInt(100) + 1;
                if (quest.getNum() == 1) {
                    rnd = 100;
                }
                if (rnd < 85) {
                    pc.sendPackets(new S_SystemMessage("開啟守護星，失敗"));
                    AstrologyQuestReading.get().updateQuest(pc.getId(), astrologyType, quest.getNum() - 1);
                    pc.sendPackets(new S_NPCTalkReturn(pc, "t_but" + (quest.getNum() - 1), msg));
                    return true;
                }
                AttonAstrologyData data = AttonAstrologyTable.get().getData(astrologyType);
                if (data != null) {
                    boolean unlockSuccess = false;
                    if (data.getNeedItemId() != null && !data.getNeedItemId().isEmpty()) {
                        int needItemId = Integer.parseInt(data.getNeedItemId());
                        int needItemNum = Integer.parseInt(data.getNeedItemCount());
                        if (pc.getInventory().checkItem(needItemId, needItemNum)) {
                            pc.getInventory().consumeItem(needItemId, needItemNum);
                            unlockSuccess = true;
                        } else {
                            pc.sendPackets(new S_SystemMessage("道具不足"));
                        }
                    } else {
                        unlockSuccess = true;
                    }
                    if (unlockSuccess) {
                        pc.getQuest().set_step(data.getQuestId(), 255);
                        AstrologyQuestReading.get().updateQuest(pc.getId(), astrologyType, 1);
                        AstrologyQuestReading.get().delQuest(pc.getId(), astrologyType);
                    }
                    updateUI(pc, "t_atton");
                    pc.sendPackets(new S_PacketBoxGree(1));
                }
                return true;
            }

            // 其他不認識指令
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    /**
     * 判斷前置星盤是否解鎖
     */
    public boolean checkEndAstrologyQuest(L1PcInstance pc, int key) {
        int prevType = calcPrevType(key);
        if (prevType < 0) return false;
        AttonAstrologyData data = AttonAstrologyTable.get().getData(prevType);
        if (data != null && !pc.getQuest().isEnd(data.getQuestId())) {
            pc.sendPackets(new S_SystemMessage("請先解鎖[" + data.getNote() + "]"));
            return true;
        }
        return false;
    }

    /**
     * 計算前置星盤編號
     */
    private int calcPrevType(int key) {
        switch (key) {
            case 0: return -1;
            case 1: return 0;
            case 2: return 1;
            case 5: return 2;
            case 4: case 6: case 9: return 5;
            case 3: return 4;
            case 7: return 6;
            case 8: return 3;
            default:
                if (key >= 10 && key <= 28) return key - 3;
        }
        return -1;
    }

    /**
     * 更新主UI
     */
    public void updateUI(L1PcInstance pc, String htmlId) {
        try {
            StringBuilder builder = new StringBuilder();
            int size = AttonAstrologyTable.get().size();
            for (int i = 0; i < size; i++) {
                AttonAstrologyData data = AttonAstrologyTable.get().getData(i);
                if (data != null) {
                    builder.append(
                            pc.getQuest().isEnd(data.getQuestId())
                                    ? data.getCompleteGfxId()
                                    : data.getIncompleteGfxId()
                    ).append(",");
                }
            }
            String skillName = _ASTROLOGY_SKILLS2.getOrDefault(pc.getId(), "");
            builder.append(skillName);
            final String[] msg = builder.toString().split(",");
            pc.sendPackets(new S_NPCTalkReturn(pc, htmlId, msg));
        } catch (Exception e) {
            _log.error("AttonAstrologyCmd UI 更新錯誤: " + e.getLocalizedMessage(), e);
        }
    }
}
