package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_Paralysis;

/**
 * 幻想技能 (PHANTASM) - 幻術師 62 級技能
 * 功能邏輯:
 * 1. start() - 技能發動時:
 *    - 設定技能效果 ID 212，持續時間 5000 毫秒 (5秒)
 *    - 對玩家: 發送 S_Paralysis(3, true) 封包 (TYPE_SLEEP 進入睡眠)
 *    - 對 NPC: 設定 setSleeped(true) (阻止 AI 執行)
 *
 * 2. stop() - 技能結束或被解除時:
 *    - 對玩家: 發送 S_Paralysis(3, false) 封包 (解除睡眠)
 *             發送 S_OwnCharStatus 更新狀態
 *             移除技能效果 ID 212
 *    - 對 NPC: 設定 setSleeped(false) (恢復 AI 執行)
 *             移除技能效果 ID 212
 *
 * 注意事項:
 * - 此技能使用狀態 ID 212 (必須與技能 ID 一致，才能正確觸發 stop())
 * - 睡眠狀態會被攻擊解除
 * - 與其他控制狀態（暈眩、凍結、麻痺）效果類似但不衝突
 *
 * @author dexc
 */
public class PHANTASM extends SkillMode {
    /**
     * 玩家施放技能
     *
     * @param srcpc 施放者 (幻術師)
     * @param cha 目標角色
     * @param magic 魔法計算物件
     * @param integer 技能持續時間參數 (此技能固定 5 秒，不使用此參數)
     * @return 傷害值 (此技能固定為 0)
     */
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = 0;
        if ((cha instanceof L1PcInstance)) {
            // 對玩家: 進入睡眠狀態
            L1PcInstance pc = (L1PcInstance) cha;
            pc.setSkillEffect(212, 5000);  // 設定技能效果，5秒後自動觸發 stop()
            pc.sendPackets(new S_Paralysis(3, true));  // TYPE_SLEEP: 進入睡眠

        } else if (((cha instanceof L1MonsterInstance)) ||
                   ((cha instanceof L1SummonInstance)) ||
                   ((cha instanceof L1PetInstance))) {
            // 對 NPC/召喚獸/寵物: 進入睡眠狀態
            L1NpcInstance tgnpc = (L1NpcInstance) cha;
            tgnpc.setSkillEffect(212, 5000);  // 設定技能效果，5秒後自動觸發 stop()
            tgnpc.setSleeped(true);  // 設定睡眠旗標，阻止 AI 執行
        }
        return dmg;  // 此技能無傷害
    }

    /**
     * NPC 施放技能 (預留介面，目前無實作)
     */
    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        int dmg = 0;
        return dmg;
    }

    /**
     * 技能發動預處理 (預留介面)
     */
    public void start(L1PcInstance srcpc, Object obj) throws Exception {
    }

    /**
     * 技能結束/解除
     * 當計時器倒數結束或技能被手動解除時呼叫
     *
     * @param cha 目標角色
     */
    public void stop(L1Character cha) throws Exception {
        if (cha instanceof L1PcInstance) {
            // 對玩家: 解除睡眠狀態
            L1PcInstance pc = (L1PcInstance) cha;
            pc.sendPackets(new S_Paralysis(3, false));  // TYPE_SLEEP: 解除睡眠
            pc.sendPackets(new S_OwnCharStatus(pc));    // 更新角色狀態
            pc.removeSkillEffect(212);  // 移除技能效果

        } else if (((cha instanceof L1MonsterInstance)) ||
                   ((cha instanceof L1SummonInstance)) ||
                   ((cha instanceof L1PetInstance))) {
            // 對 NPC/召喚獸/寵物: 解除睡眠狀態
            L1NpcInstance tgnpc = (L1NpcInstance) cha;
            tgnpc.removeSkillEffect(212);  // 移除技能效果
            tgnpc.setSleeped(false);  // 解除睡眠旗標，恢復 AI 執行
        }
    }
}
