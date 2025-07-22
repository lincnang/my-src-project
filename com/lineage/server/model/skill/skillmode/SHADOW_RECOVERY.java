package com.lineage.server.model.skill.skillmode;

import com.lineage.config.ConfigSkillDarkElf;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.sql.CharSkillTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_InventoryIcon;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Skills;

import java.sql.Timestamp;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 暗隱恢復（Shadow Recovery）
 * - HOT回復
 * - 增加臨時近戰閃避（getTempMeleeEvasion）
 * - 所有設定均從 ConfigSkillDarkElf 取得
 */
public class SHADOW_RECOVERY extends SkillMode {

    public SHADOW_RECOVERY() {}

    @Override
    public int start(final L1PcInstance pc, final L1Character cha, final L1Magic magic, final int integer) throws Exception {
        // 1. 技能冷卻秒數（從config取得，轉成毫秒）
        final int reuseDelay = ConfigSkillDarkElf.ShadowRecovery_Cooldown * 1000;
        long now = System.currentTimeMillis();
        long nextCast = pc.getSkillReuse(L1SkillId.SHADOW_RECOVERY);
        if (now < nextCast) {
            int sec = (int)((nextCast - now) / 1000) + 1;
            pc.sendPackets(new S_SystemMessage("\\fA暗隱恢復技能冷卻中，剩餘 " + sec + " 秒。"));
            return 0;
        }

        // 2. 技能消耗道具檢查（如技能設定需消耗道具）
        L1Skills skill = SkillsTable.get().getTemplate(L1SkillId.SHADOW_RECOVERY);
        int itemConsumeId = (skill != null) ? skill.getItemConsumeId() : 0;
        int itemConsumeCount = (skill != null) ? skill.getItemConsumeCount() : 0;
        if (itemConsumeId > 0 && itemConsumeCount > 0) {
            if (!pc.getInventory().checkItem(itemConsumeId, itemConsumeCount)) {
                pc.sendPackets(new S_SystemMessage("施放技能需要的道具不足！"));
                return 0;
            }
            pc.getInventory().consumeItem(itemConsumeId, itemConsumeCount);
        }

        // 3. 設定下次可施放時間（記憶體+資料庫）
        long nextTimeMillis = now + reuseDelay;
        pc.setSkillReuse(L1SkillId.SHADOW_RECOVERY, nextTimeMillis);
        CharSkillTable.get().updateSkillCooldown(pc.getId(), L1SkillId.SHADOW_RECOVERY, new Timestamp(nextTimeMillis));

        // 4. HOT回復設定值與加血量計算
        final int hotTime = ConfigSkillDarkElf.ShadowRecovery_HotTime;           // HOT持續秒數
        final int maxHp = pc.getMaxHp();
        final int totalHeal = (int)(maxHp * 0.4);                               // 回復40%最大HP
        final int healPerSec = totalHeal / hotTime;
        final int lastHeal = totalHeal - healPerSec * (hotTime - 1);

        // 5. 近戰閃避加成設定值
        final int meleeEvasion = ConfigSkillDarkElf.ShadowRecovery_EvasionMelee; // 近戰閃避加成

        // 6. 上BUFF、設置臨時閃避加成
        pc.setSkillEffect(L1SkillId.SHADOW_RECOVERY, hotTime * 1000);
        pc.addTempMeleeEvasion(meleeEvasion);
        pc.sendPackets(new S_PacketBox(S_PacketBox.UPDATE_ER, pc.getEr()));

        // 7. BUFF圖示與訊息
        pc.sendPackets(new S_InventoryIcon(10626, true, 2782, 2782, 1200));
        pc.sendPackets(new S_SystemMessage("\\fT暗隱恢復發動，開始回復HP，並獲得閃避加成。"));

        // 8. HOT回血（每秒回復）
        Timer timer = new Timer();
        for (int i = 1; i <= hotTime; i++) {
            final int heal = (i == hotTime) ? lastHeal : healPerSec;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (pc.hasSkillEffect(L1SkillId.SHADOW_RECOVERY)) {
                        int nowHp = pc.getCurrentHp();
                        int maxHp = pc.getMaxHp();
                        int newHp = Math.min(nowHp + heal, maxHp);
                        pc.setCurrentHp(newHp);
                    }
                }
            }, i * 1000L);
        }
        return 0;
    }

    @Override
    public int start(final com.lineage.server.model.Instance.L1NpcInstance npc, final L1Character cha, final L1Magic magic, final int integer) throws Exception {
        // NPC不支援此技能
        return 0;
    }

    @Override
    public void start(final L1PcInstance pc, final Object obj) throws Exception {
        // 多型備用，不處理
    }

    @Override
    public void stop(final L1Character cha) throws Exception {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.clearTempEvasion(); // BUFF結束時清除臨時近戰閃避加成
            pc.sendPackets(new S_PacketBox(S_PacketBox.UPDATE_ER, pc.getEr()));
            pc.sendPackets(new S_SystemMessage("\\aE暗隱恢復效果消失，閃避恢復正常。"));
        }
    }
}
