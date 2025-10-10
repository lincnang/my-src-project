package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.serverpackets.S_PacketBoxIcon3;
import com.lineage.server.serverpackets.S_InventoryIcon;

import static com.lineage.server.model.skill.L1SkillId.MEDITATION;

/**
 * 冥想術 (MEDITATION) - BUFF型實作
 * 啟動後給予玩家BUFF效果，並開始倒數BUFF時間
 * BUFF期間增加MP回復速度
 */
public class MEDITATION extends SkillMode {
    
    public MEDITATION() {}

    /**
     * 玩家施放冥想術
     */
    @Override
    public int start(L1PcInstance pc, L1Character cha, L1Magic magic, int buffDuration) throws Exception {
        // 檢查是否已經有冥想術效果
        if (cha.hasSkillEffect(MEDITATION)) {
            // 已有效果，刷新持續時間
            cha.setSkillEffect(MEDITATION, buffDuration * 1000);
            if (cha instanceof L1PcInstance) {
                L1PcInstance targetPc = (L1PcInstance) cha;
                targetPc.sendPackets(new S_SystemMessage("\\aG[冥想術] 效果已更新，持續時間：" + buffDuration + " 秒"));
                // 追加：發送 10531 的倒數圖示（時間採用DB buffDuration秒）
                targetPc.sendPackets(new S_InventoryIcon(10531, true, 910, buffDuration));
            }
        } else {
            // 新增冥想術效果
            cha.setSkillEffect(MEDITATION, buffDuration * 1000);
            
            if (cha instanceof L1PcInstance) {
                L1PcInstance targetPc = (L1PcInstance) cha;
                // 發送系統訊息
                targetPc.sendPackets(new S_SystemMessage("\\aG[冥想術] 啟動成功！MP回復速度提升，持續 " + buffDuration + " 秒"));
                // 追加：發送 10531 的倒數圖示（時間採用DB buffDuration秒）
                targetPc.sendPackets(new S_InventoryIcon(10531, true, 910, buffDuration));
            }
        }
        
        // 技能圖示會由系統自動處理（根據setSkillEffect）
        return 0;
    }

    /**
     * NPC施放冥想術
     */
    @Override
    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int buffDuration) throws Exception {
        // NPC施放時的簡化處理
        if (!cha.hasSkillEffect(MEDITATION)) {
            cha.setSkillEffect(MEDITATION, buffDuration * 1000);
        }
        return 0;
    }

    /**
     * 冥想術效果結束時的處理
     */
    @Override
    public void stop(L1Character cha) throws Exception {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            // 發送效果結束訊息
            pc.sendPackets(new S_SystemMessage("\\aH[冥想術] 效果已結束"));
            // 移除倒數圖示：再次送出相同圖示但時間為0（部分客戶端需要）
            pc.sendPackets(new S_PacketBoxIcon3(0, 795));
            // 關閉 10531 倒數圖示
            pc.sendPackets(new S_InventoryIcon(10531, false, 910, 0));
        }
        // 技能圖示移除會由系統自動處理
    }

    @Override
    public void start(L1PcInstance srcpc, Object obj) throws Exception {
        // 不需要實作
    }
}

