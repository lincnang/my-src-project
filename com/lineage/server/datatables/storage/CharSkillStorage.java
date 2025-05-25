package com.lineage.server.datatables.storage;

import com.lineage.server.templates.L1UserSkillTmp;

import java.util.ArrayList;

public interface CharSkillStorage {

    /**
     * 載入所有角色技能資料
     */
    void load();

    /**
     * 取得某角色的技能列表
     *
     * @param playerObjId 角色 ObjId
     * @return 該角色的技能清單
     */
    ArrayList<L1UserSkillTmp> skills(int playerObjId);

    /**
     * 新增一個技能紀錄
     *
     * @param playerObjId 角色 ObjId
     * @param skillId     技能ID
     * @param skillName   技能名稱
     * @param isActive    是否自動(或其他意義)
     * @param timeLeft    剩餘時間
     */
    void spellMastery(int playerObjId, int skillId, String skillName, int isActive, int timeLeft);

    /**
     * 刪除一個技能
     *
     * @param playerObjId 角色 ObjId
     * @param skillId     技能ID
     */
    void spellLost(int playerObjId, int skillId);

    /**
     * 檢查是否已有該技能
     *
     * @param playerObjId 角色 ObjId
     * @param skillId     技能ID
     * @return true=已存在
     */
    boolean spellCheck(int playerObjId, int skillId);

    /**
     * 設置自動技能狀態
     *
     * @param mode        自動or手動
     * @param playerObjId 角色 ObjId
     * @param skillId     技能ID
     */
    void setAuto(int mode, int playerObjId, int skillId);

    /**
     * (若需要) 更新技能等級
     *
     * @param playerObjId 角色 ObjId
     * @param skillId     技能ID
     * @param newLevel    新的技能等級
     */
    void updateSkillLevel(int playerObjId, int skillId, int newLevel);
}
