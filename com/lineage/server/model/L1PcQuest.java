package com.lineage.server.model;

import com.lineage.data.event.QuestMobSet;
import com.lineage.server.datatables.ServerQuestMobTable;
import com.lineage.server.datatables.lock.CharacterQuestReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.CharQuest;
import com.lineage.server.templates.L1ServerQuestMob;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class L1PcQuest {
    private Map<Integer, Integer> _nowStageMap = new HashMap<>();
    private Set<String> _alreadyWarnedStage = new HashSet<>();
    public static final int QUEST_OILSKINMANT = 11;
    public static final int QUEST_DOROMOND = 20;
    public static final int QUEST_RUBA = 21;
    public static final int QUEST_AREX = 22;
    public static final int QUEST_LUKEIN1 = 23;
    public static final int QUEST_TBOX1 = 24;
    public static final int QUEST_TBOX2 = 25;
    public static final int QUEST_TBOX3 = 26;
    public static final int QUEST_SIMIZZ = 27;
    public static final int QUEST_DOIL = 28;
    public static final int QUEST_RUDIAN = 29;
    public static final int QUEST_RESTA = 30;
    public static final int QUEST_CADMUS = 31;
    public static final int QUEST_KAMYLA = 32;
    public static final int QUEST_CRYSTAL = 33;
    public static final int QUEST_LIZARD = 34;
    public static final int QUEST_KEPLISHA = 35;
    public static final int QUEST_DESIRE = 36;
    public static final int QUEST_SHADOWS = 37;
    public static final int QUEST_TOSCROLL = 39;
    public static final int QUEST_MOONOFLONGBOW = 40;
    public static final int QUEST_GENERALHAMELOFRESENTMENT = 41;
    public static final int QUEST_NOT = 0; // 任務尚未開始
    public static final int QUEST_START = 1; // 任務已經開始,未結束
    public static final int QUEST_END = 255; // 任務已經結束

    public static final int QUEST_TUTOR = 300;
    public static final int QUEST_TUTOR2 = 304;
    public static final int QUEST_MARRY = 74;
    public static final int QUEST_SLOT59 = 81;
    public static final int QUEST_SLOT76 = 79;
    public static final int QUEST_SLOT81 = 80;
    public static final int QUEST_BOOKMARK = 82;
    public static final int BAO_QUEST_1 = 2001;
    public static final int BAO_QUEST_2 = 2002;
    public static final int BAO_QUEST_3 = 2003;
    public static final int BAO_QUEST_4 = 2004;
    public static final int BAO_QUEST_5 = 2005;
    public static final int Mazu_Use = 30000;
    public static final int QUEST_HAMO = 63;
    public static final int QUEST_MARBIN = 64;
    public static final int QUEST_GIVE_VIP_1 = 65;
    public static final int QUEST_GIVE_VIP_2 = 66;
    public static final int QUEST_GIVE_VIP_3 = 67;
    public static final int QUEST_GIVE_VIP_4 = 68;
    public static final int QUEST_GIVE_VIP_5 = 69;

    private static final Log _log = LogFactory.getLog(L1PcQuest.class);
    private L1PcInstance _owner;
    private Map<Integer, CharQuest> _quest = null;

    public L1PcQuest(final L1PcInstance owner) {
        this._owner = owner;
    }

    public L1PcInstance get_owner() {
        return this._owner;
    }

    /** 取得玩家對應任務的已完成階段 */
    public int get_now_stage(int quest_id) {
        CharQuest quest = _quest.get(quest_id);
        return (quest != null) ? quest.get_now_stage() : 0;
    }


    public void set_now_stage(int quest_id, int stage) {
        CharQuest quest = _quest.get(quest_id);
        if (quest == null) {
            quest = new CharQuest();
            _quest.put(quest_id, quest);
        }
        quest.set_now_stage(stage);
        _nowStageMap.put(quest_id, stage); // 補這一行
        CharacterQuestReading.get().updateQuest(_owner.getId(), quest_id, quest);
    }



    /** 取得任務進度 */
    public int get_step(int quest_id) {
        try {
            if (_quest == null) return 0;
            CharQuest step = this._quest.get(quest_id);
            if (step == null) return 0;
            return step.get_quest_step();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return 0;
    }

    /** 設定任務進度（會自動初始化怪物數組）*/
    public void set_step(int quest_id, int step) {
        try {
            if (_quest == null) {
                _quest = new HashMap<>();
            }
            CharQuest quest = this._quest.get(quest_id);
            if (quest == null) {
                quest = new CharQuest();
                quest.set_quest_step(step);
                // 初始化 mob_count 陣列（強制直接呼叫 ServerQuestMobTable）
                int[] mobArr = ServerQuestMobTable.get().getMobCount(quest_id, step);
                quest.set_mob_count(mobArr);
                // 建立資料庫紀錄
                CharacterQuestReading.get().storeQuest(this._owner.getId(), quest_id, quest);
            } else {
                // 進度變動時也要初始化 mob_count
                quest.set_quest_step(step);
                int[] mobArr = ServerQuestMobTable.get().getMobCount(quest_id, step);
                quest.set_mob_count(mobArr);
                CharacterQuestReading.get().updateQuest(this._owner.getId(), quest_id, quest);
            }
            this._quest.put(quest_id, quest);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }


    /** 結束任務 */
    public void set_end(final int quest_id) {
        try {
            CharQuest quest = this._quest.get(quest_id);
            if (quest == null) {
                quest = new CharQuest();
                set_step(quest_id, QUEST_END);
                CharacterQuestReading.get().storeQuest(this._owner.getId(), quest_id, quest);
            } else {
                quest.set_quest_step(QUEST_END);
                CharacterQuestReading.get().updateQuest(this._owner.getId(), quest_id, quest);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /** 該任務是否開始 */
    public boolean isStart(final int quest_id) {
        try {
            final int step = this.get_step(quest_id);
            if (step > QUEST_NOT && step < QUEST_END) {
                return true;
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    /** 該任務是否結束 */
    public boolean isEnd(final int quest_id) {
        try {
            final int step = this.get_step(quest_id);
            if (step == QUEST_END) {
                return true;
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    /** 載入人物任務紀錄 */
    public void load() {
        try {
            _quest = CharacterQuestReading.get().get(this._owner.getId());
            if (this._quest == null) {
                _quest = new HashMap<>();
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void set_step(final int quest_id, final int step, final int clear) {
        try {
            if (_quest == null) {
                _quest = new HashMap<>();
            }
            CharQuest quest = this._quest.get(quest_id);
            if (quest == null) {
                quest = new CharQuest();
                quest.set_quest_step(step);
                if (QuestMobSet.START) {
                    quest.set_mob_count(ServerQuestMobTable.get().getMobCount(quest_id, step));
                } else {
                    quest.set_mob_count(null);
                }
                CharacterQuestReading.get().storeQuest(this._owner.getId(), quest_id, quest, clear);
            } else {
                quest.set_quest_step(step);
                CharacterQuestReading.get().updateQuest(this._owner.getId(), quest_id, quest);
            }
            this._quest.put(quest_id, quest);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 防呆取得怪物擊殺進度陣列，不會回傳 null
     * @param quest_id 任務編號
     * @return int[] 擊殺進度，若無紀錄會依照 ServerQuestMobTable 配置長度自動補 0
     */
    public int[] get_mob_count(int quest_id) {
        CharQuest quest = this._quest.get(quest_id);
        if (quest == null || quest.get_mob_count() == null) {
            // 根據當前任務定義初始化正確長度
            L1ServerQuestMob mob = ServerQuestMobTable.get().getQuestMobById(quest_id);
            if (mob != null) {
                return new int[mob.get_mob_id().length];
            }
            return new int[0];
        }
        return quest.get_mob_count();
    }

    public CharQuest getCharQuest(int questId) {
        return this._quest.get(questId); // 或你的實際儲存 map 名稱
    }

    public void set_mob_count(int quest_id, int[] mob_count_arr) {
        if (_quest == null) return;
        CharQuest quest = this._quest.get(quest_id);
        if (quest != null) {
            quest.set_mob_count(mob_count_arr);
            CharacterQuestReading.get().updateQuest(this._owner.getId(), quest_id, quest);
        }
    }



    public boolean isValid() {
        return this._quest != null;
    }
}
