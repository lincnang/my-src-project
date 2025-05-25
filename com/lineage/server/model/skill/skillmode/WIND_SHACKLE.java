package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_PacketBoxWindShackle;

public class WIND_SHACKLE extends SkillMode {

    // 這個方法處理玩家（L1PcInstance）和怪物（L1NpcInstance）應用技能的邏輯
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        if (cha.hasSkillEffect(167)) { // 如果目標已經有技能效果167（風縛效果），則不再應用技能
            return 0;
        }
        if ((cha instanceof L1PcInstance)) { // 如果目標是玩家（L1PcInstance）
            L1PcInstance pc = (L1PcInstance) cha;
            pc.sendPackets(new S_PacketBoxWindShackle(pc.getId(), integer)); // 發送風縛效果的包裝訊息
        }
        cha.setSkillEffect(167, integer * 1000); // 設定技能效果（167），持續時間爲 integer * 1000 毫秒
        return 0; // 返回0，表示操作完成
    }

    // 處理怪物（L1NpcInstance）應用技能的邏輯，和上面的方法類似
    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        if (cha.hasSkillEffect(167)) { // 如果目標已經有技能效果167，返回0
            return 0;
        }
        if ((cha instanceof L1PcInstance)) { // 如果目標是玩家
            L1PcInstance pc = (L1PcInstance) cha;
            pc.sendPackets(new S_PacketBoxWindShackle(pc.getId(), integer)); // 發送風縛效果的包裝訊息
        }
        cha.setSkillEffect(167, integer * 1000); // 設定技能效果（167），持續時間爲 integer * 1000 毫秒
        return 0; // 返回0，表示操作完成
    }

    // 這個方法看起來沒有被使用，應該是繼承自 `SkillMode` 類的方法
    public void start(L1PcInstance srcpc, Object obj) throws Exception {
    }

    // 停止技能效果，清除目標的技能效果
    public void stop(L1Character cha) throws Exception {
        if ((cha instanceof L1PcInstance)) { // 如果目標是玩家
            L1PcInstance pc = (L1PcInstance) cha;
            pc.sendPackets(new S_PacketBoxWindShackle(pc.getId(), 0)); // 發送停止風縛效果的包裝訊息
        }
    }
}