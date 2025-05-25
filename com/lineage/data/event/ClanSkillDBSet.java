// 套件與匯入
package com.lineage.data.event;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.datatables.RewardClanSkillsTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1ClanSkills;
import com.lineage.server.templates.L1Event;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// 血盟技能設定事件（活動）處理類別
public class ClanSkillDBSet extends EventExecutor {
    private static final Log _log = LogFactory.getLog(ClanSkillDBSet.class);
    public static boolean START = false; // 控制技能是否啟動
    public static String[] _skillNameNote = null; // 技能名稱與說明備註陣列

    // 取得這個事件執行器的實體
    public static EventExecutor get() {
        return new ClanSkillDBSet();
    }

    /**
     * 加入血盟技能效果給角色
     * @param pc 玩家角色
     * @param value 技能圖示與訊息資料
     */
    public static void add(L1PcInstance pc, final L1ClanSkills value) {
        if (!START) {
            return; // 如果活動尚未啟動，不執行
        }
        int clanSkillsId = pc.getClan().getClanSkillId();
        if (clanSkillsId == 0) {
            return; // 沒有設定技能 ID，不執行
        }
        int clanSkillsLv = pc.getClan().getClanSkillLv();
        if (clanSkillsLv == 0) {
            return; // 技能等級為 0，不執行
        }

        // 從資料表取得技能內容
        L1ClanSkills skill = RewardClanSkillsTable.get().getClanSkillsList(clanSkillsId, clanSkillsLv);
        if (skill == null) {
            return; // 無此技能，結束
        }

        // 條件判斷：流星等級、角色等級
        int CheckLvturn = skill.getCheckLvturn();
        if ((CheckLvturn != 0) && (pc.getMeteLevel() < CheckLvturn)) {
            return;
        }
        int CheckLevel = skill.getCheckLevel();
        if ((CheckLevel != 0) && (pc.getHighLevel() < CheckLevel)) {
            return;
        }

        // 狀態更新旗標
        boolean OwnCharStatus = false; // 是否需要更新角色能力畫面
        boolean hp = false; // 是否需要更新 HP 顯示
        boolean mp = false; // 是否需要更新 MP 顯示
        boolean spmr = false; // 是否需要更新 SP/MR 顯示

        // 加成效果開始套用
        int AddMaxHp = skill.getAddMaxHp();
        if (AddMaxHp != 0) {
            pc.addMaxHp(AddMaxHp);
            hp = true;
        }
        int AddMaxMp = skill.getAddMaxMp();
        if (AddMaxMp != 0) {
            pc.addMaxMp(AddMaxMp);
            mp = true;
        }
        int AddHpr = skill.getAddHpr();
        if (AddHpr != 0) {
            pc.addHpr(AddHpr);
        }
        int AddMpr = skill.getAddMpr();
        if (AddMpr != 0) {
            pc.addMpr(AddMpr);
        }

        // 各屬性加成
        if (skill.getAddStr() != 0) { pc.addStr(skill.getAddStr()); OwnCharStatus = true; }
        if (skill.getAddDex() != 0) { pc.addDex(skill.getAddDex()); OwnCharStatus = true; }
        if (skill.getAddCon() != 0) { pc.addCon(skill.getAddCon()); OwnCharStatus = true; }
        if (skill.getAddInt() != 0) { pc.addInt(skill.getAddInt()); OwnCharStatus = true; }
        if (skill.getAddWis() != 0) { pc.addWis(skill.getAddWis()); OwnCharStatus = true; }
        if (skill.getAddCha() != 0) { pc.addCha(skill.getAddCha()); OwnCharStatus = true; }

        // AC、防禦相關
        if (skill.getAddAc() != 0) { pc.addAc(-skill.getAddAc()); OwnCharStatus = true; }

        // 魔法/物理攻擊/命中等
        if (skill.getAddSp() != 0) { pc.addSp(skill.getAddSp()); }
        if (skill.getAddMr() != 0) { pc.addMr(skill.getAddMr()); spmr = true; }
        if (skill.getAddDmg() != 0) { pc.addDmgup(skill.getAddDmg()); spmr = true; }
        if (skill.getAddBowDmg() != 0) { pc.addBowDmgup(skill.getAddBowDmg()); }
        if (skill.getAddHit() != 0) { pc.addHitup(skill.getAddHit()); }
        if (skill.getAddBowHit() != 0) { pc.addBowHitup(skill.getAddBowHit()); }

        // 傷害減免（物理與魔法）
        if (skill.getReductionDmg() != 0) { pc.addDamageReductionByArmor(skill.getReductionDmg()); }
        if (skill.getReductionMagicDmg() != 0) { pc.add_magic_reduction_dmg(skill.getReductionMagicDmg()); }

        // 四屬加成
        if (skill.getAddWater() != 0) { pc.addWater(skill.getAddWater()); OwnCharStatus = true; }
        if (skill.getAddWind() != 0) { pc.addWind(skill.getAddWind()); OwnCharStatus = true; }
        if (skill.getAddFire() != 0) { pc.addFire(skill.getAddFire()); OwnCharStatus = true; }
        if (skill.getAddEarth() != 0) { pc.addEarth(skill.getAddEarth()); OwnCharStatus = true; }

        // 更新畫面資訊
        if (OwnCharStatus) {
            pc.sendPackets(new S_OwnCharStatus(pc));
        }
        if (hp) {
            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
        }
        if (mp) {
            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
        }
        if (spmr) {
            pc.sendPackets(new S_SPMR(pc));
        }

        // 狀態圖示顯示設定（登入時或效果套用時出現）
        if (value.get_buff_iconid() != 0 && value.get_buff_stringid() != 0) {
            if (value.get_open_string() == 0) {
                // 不顯示訊息，僅圖示
                pc.sendPackets(new S_InventoryIcon(value.get_buff_iconid(), true, value.get_buff_stringid(), -1));
            } else {
                // 顯示圖示與訊息
                pc.sendPackets(new S_InventoryIcon(value.get_buff_iconid(), true, value.get_buff_stringid(), value.get_buff_stringid()));
            }
        }

        // 顯示技能名稱與說明文字
        String ClanSkillName = skill.getClanSkillName();
        String Note = skill.getNote();
        if ((ClanSkillName != null) && (Note != null)) {
            pc.sendPackets(new S_ServerMessage("\\fU" + ClanSkillName + ":"));
            pc.sendPackets(new S_ServerMessage("\\fU" + Note));
        }
    }

    /**
     * 執行活動初始化（啟用這個血盟技能系統）
     */
    public void execute(L1Event event) {
        try {
            if (ClanSkillSet.START) {
                // 若舊血盟技能系統正在啟用，則不能啟用新系統
                _log.info("警告!活動設置:伊薇版血盟技能已啟動狀態下無法同時啟動111項血盟技能");
            } else {
                RewardClanSkillsTable.get(); // 初始化技能資料
                _skillNameNote = RewardClanSkillsTable.get().getSkillsNameNote(); // 載入技能名稱與備註
                START = true; // 設定為啟用狀態
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e); // 記錄錯誤訊息
        }
    }
}
