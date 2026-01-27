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
 * 守護星盤˙指令
 * <br>
 * 修改後流程：
 * 1. 取得並建立星盤任務記錄 <br>
 * 2. 檢查玩家是否持有道具 11618（守護石）： <br>
 * - 若有，跳過前置任務檢查 <br>
 * - 若無，必須檢查前置任務是否完成 <br>
 * 3. 檢查當前任務是否完成（解鎖完成）： <br>
 * - 若尚未解鎖，則設定星盤參數並更新介面，進入 "abu" 抽卡解鎖流程 <br>
 * - 若已解鎖，則根據 AstrologyData 的 skillId 進行處理： <br>
 * • 如果 skillId == 0：直接顯示「星盤已完成」 <br>
 * • 如果 skillId == 1：若尚未激活，激活技能1；若已激活，顯示「星盤已完成」 <br>
 * • 如果 skillId == 2：同理處理技能2 <br>
 * <br>
 * 4. "abu" 指令則依據設定的 AstrologyType 與 AstrologyuId 進入抽卡解鎖流程。
 *
 * @author hero
 */
public class AstrologyCmd {
    private static final Log _log = LogFactory.getLog(AstrologyCmd.class);
    // 記錄玩家是否激活技能1
    private static final Map<Integer, Boolean> skill1ActiveMap = new ConcurrentHashMap<>();
    // 記錄玩家是否激活技能2
    private static final Map<Integer, Boolean> skill2ActiveMap = new ConcurrentHashMap<>();
    /**
     * 玩家技能激活集合
     */
    private static final Map<Integer, Integer> _ASTROLOGY_SKILLS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, String> _ASTROLOGY_SKILLS2 = new ConcurrentHashMap<>();
    /**
     * 激活對應星盤後增加角色屬性值
     */
    private static final Map<Integer, Integer> _ASTROLOGY_BUFFS = new ConcurrentHashMap<>();
    private static AstrologyCmd _instance;

    public static AstrologyCmd get() {
        if (_instance == null) {
            synchronized (AstrologyCmd.class) {
                if (_instance == null) {
                    _instance = new AstrologyCmd();
                }
            }
        }
        return _instance;
    }
    
    /**
     * 清理玩家數據，防止記憶體洩漏
     */
    public static void cleanupPlayerData(int playerId) {
        skill1ActiveMap.remove(playerId);
        skill2ActiveMap.remove(playerId);
        _ASTROLOGY_SKILLS.remove(playerId);
        _ASTROLOGY_SKILLS2.remove(playerId);
        _ASTROLOGY_BUFFS.remove(playerId);
    }

    /**
     * 檢查玩家是否啟動技能1
     */
    public boolean isSkill1Active(L1PcInstance pc) {
        return skill1ActiveMap.containsKey(pc.getId()) && skill1ActiveMap.get(pc.getId());
    }

    /**
     * 檢查玩家是否啟動技能2（修正使用 skill2ActiveMap）
     */
    public boolean isSkill2Active(L1PcInstance pc) {
        return skill2ActiveMap.containsKey(pc.getId()) && skill2ActiveMap.get(pc.getId());
    }

    /**
     * 玩家已經啓動的技能編號
     *
     * @return 列表中存在玩家ID
     */
    public int getAstrologySkillActive(L1PcInstance pc) {
        if (_ASTROLOGY_SKILLS.containsKey(pc.getId())) {
            return _ASTROLOGY_SKILLS.get(pc.getId());
        }
        return -1;
    }

    public boolean Cmd(final L1PcInstance pc, final String cmd) {
        if (!"astr1".equals(cmd) && pc.getAstrologyPlateType() != 0) return false;
        try {
            final String[] msg = "".split(",");
            // 只要有命中指令，最後都return true
            if (cmd.equals("astr1")) {
                pc.setAstrologyPlateType(0);
                UpdateInfo(pc, "t_zeus");
                return true;
            }

            if (cmd.startsWith("tza_")) {
                int id = Integer.parseInt(cmd.substring(4));
                AstrologyData data = Astrology1Table.get().getAstrology(id);
                if (data == null) {
                    pc.sendPackets(new S_SystemMessage("星盤編號異常:" + id + ",請通知管理員"));
                    return true; // 攔截
                }
                // 前置檢查統一由 checkEndAstrologyQuest 處理
                if (checkEndAstrologyQuest(pc, id)) {
                    return true; // 攔截
                }
                AstrologyQuest quest = AstrologyQuestReading.get().get(pc.getId(), id);
                if (quest == null) {
                    quest = new AstrologyQuest(pc.getId(), id, data.get_cards());
                    AstrologyQuestReading.get().storeQuest(pc.getId(), id, data.get_cards());
                }
                // 檢查需求道具（依資料庫設定）
                if (data.getNeedItemID() != 0) {
                    if (!pc.getInventory().checkItem(data.getNeedItemID(), data.getNeedItemNum())) {
                        pc.sendPackets(new S_SystemMessage("需求道具不足"));
                        UpdateInfo(pc, "t_zeus");
                        return true; // 攔截
                    }
                }
                if (AstrologyHistoryTable.get().isUnlocked(pc.getId(), data.get_questId())) {
                    // 非技能節點：再次點選顯示「星盤已解鎖」
                    if (data.get_skillId() == 0) {
                        pc.sendPackets(new S_SystemMessage("星盤已解鎖"));
                        UpdateInfo(pc, "t_zeus");
                        return true; // 攔截
                    }
                    // 技能節點：做切換/維持開啟提示
                    if (getAstrologySkillActive(pc) != data.get_skillId()) {
                        _ASTROLOGY_SKILLS.put(pc.getId(), data.get_skillId());
                        _ASTROLOGY_SKILLS2.put(pc.getId(), data.getName());
                    }
                    pc.sendPackets(new S_SystemMessage("星盤技能：" + data.getName() + "已開啟！", 1));
                    UpdateInfo(pc, "t_zeus");
                    return true; // 攔截
                }
                pc.setAstrologyType(id);
                pc.sendPackets(new S_NPCTalkReturn(pc, "t_but" + quest.getNum(), msg));
                return true; // 攔截
            }

            if (cmd.startsWith("abu")) {
                int astrologyType = pc.getAstrologyType();
                AstrologyQuest quest = AstrologyQuestReading.get().get(pc.getId(), astrologyType);
                if (quest == null) {
                    return true; // 攔截
                }
                AstrologyData data = Astrology1Table.get().getAstrology(astrologyType);
                if (data == null) {
                    return true; // 攔截
                }
                // 檢查並扣除需求道具（依資料庫設定，無論成功失敗都扣除）
                if (data.getNeedItemID() != 0) {
                    if (!pc.getInventory().checkItem(data.getNeedItemID(), data.getNeedItemNum())) {
                        pc.sendPackets(new S_SystemMessage("需求道具不足，無法啟用！"));
                        pc.sendPackets(new S_NPCTalkReturn(pc, "t_but" + quest.getNum(), msg));
                        return true; // 攔截
                    }
                    pc.getInventory().consumeItem(data.getNeedItemID(), data.getNeedItemNum());
                }
                int rnd = ThreadLocalRandom.current().nextInt(100) + 1;
                if (quest.getNum() == 1) {
                    rnd = 100;
                }
                if (rnd < 85) {
                    pc.sendPackets(new S_SystemMessage("開啟守護星，失敗"));
                    AstrologyQuestReading.get().updateQuest(pc.getId(), astrologyType, quest.getNum() - 1);
                    pc.sendPackets(new S_NPCTalkReturn(pc, "t_but" + (quest.getNum() - 1), msg));
                    return true; // 攔截
                }
                // 成功
                // pc.getQuest().set_step(data.get_questId(), 255); // 改用 AstrologyHistoryTable 記錄
                AstrologyQuestReading.get().updateQuest(pc.getId(), astrologyType, 1);
                AstrologyQuestReading.get().delQuest(pc.getId(), astrologyType);
                // 新增解鎖紀錄
                AstrologyHistoryTable.get().add(pc.getId(), data.get_questId());

                pc.sendPackets(new S_SystemMessage("恭喜您解鎖[" + data.getName() + "]"));                // 解鎖即永久：非技能節點立即套用能力
                if (data.get_skillId() == 0) {
                    pc.addAstrologyPower(data, astrologyType);
                    pc.sendPackets(new S_SystemMessage("星盤已解鎖"));
                }
                UpdateInfo(pc, "t_zeus");
                pc.sendPackets(new S_PacketBoxGree(1));
                return true; // 攔截
            }

            // 其他未處理才走這裡
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }


    /**
     * 判斷當前任務卡牌是否已經解锁 by 聖子默默
     *
     * @param pc  玩家
     * @param key 星盤編號<br>
     *            key = 0 時默認 false
     * @return 上一級星盤卡牌是否已經解鎖 <br>
     * true = 未解锁<br>
     * false = 已经解锁
     */
    public boolean checkEndAstrologyQuest(L1PcInstance pc, int key) {
        int astrologyType;
        switch (key) {
            case 0:
                return false;
            case 1:
                astrologyType = 0;
                break;
            case 2:
                astrologyType = 1;
                break;
            case 5:
                astrologyType = 2;
                break;
            case 4:
            case 6:
            case 9:
                astrologyType = 5;
                break;
            case 3:
                astrologyType = 4;
                break;
            case 7:
                astrologyType = 6;
                break;
            case 8:
                astrologyType = 3;
                break;
            case 12:
            case 15:
            case 18:
            case 21:
            case 24:
            case 27:
            case 10:
            case 13:
            case 16:
            case 19:
            case 22:
            case 25:
            case 28:
            case 11:
            case 14:
            case 17:
            case 20:
            case 23:
            case 26:
                astrologyType = key - 3;
                break;
            default:
                pc.sendPackets(new S_SystemMessage("玩家星盤已解鎖編號檢測異常:" + key + ",請通知管理員"));
                return true;

        }
        AstrologyData data = Astrology1Table.get().getAstrology(astrologyType);
        if (AstrologyHistoryTable.get().isUnlocked(pc.getId(), data.get_questId())) { // 已解鎖 任務完成已記錄
            return false;
        }
        pc.sendPackets(new S_SystemMessage("請先解鎖[" + data.getName() + "]", 1));
        return true;
    }

    public void UpdateInfo(final L1PcInstance pc, String htmlId) {
        try {
            final StringBuilder builder = new StringBuilder();
            int size = Astrology1Table.get().Size();
            for (int i = 0; i < size; i++) {
                final AstrologyData data = Astrology1Table.get().getAstrology(i);
                if (data != null) {
                    // 根據任務完成狀態選擇顯示的圖片編號
                    if (AstrologyHistoryTable.get().isUnlocked(pc.getId(), data.get_questId())) {
                        builder.append(data.getAddcgfxid()).append(",");
                    } else {
                        builder.append(data.getAddhgfxid()).append(",");
                    }
                }
            }
            builder.append(_ASTROLOGY_SKILLS2.get(pc.getId()));
            final String[] msg = builder.toString().split(",");
            pc.sendPackets(new S_NPCTalkReturn(pc, htmlId, msg));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
