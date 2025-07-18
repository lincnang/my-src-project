package com.lineage.server.model.skill.skillmode;

import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_IconConfig;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 泰坦之暈（DB設計相容版）
 * 1. 範圍用 ranged 欄位
 * 2. 命中率為 probability_value + [0 ~ probability_dice]，浮動
 * 3. 暈眩秒數隨機1~6
 * 4. 判斷不穿牆
 * 5. 施法者6秒內分批回血1000
 */
public class TITAN_STUN extends SkillMode {

    public TITAN_STUN() {}

    @Override
    public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
            throws Exception {
        // 1. 技能設定從DB取得
        L1Skills skill = SkillsTable.get().getTemplate(L1SkillId.TITAN_STUN);
        int range = (skill != null) ? skill.getRanged() : 5;
        int baseChance = (skill != null) ? skill.getProbabilityValue() : 30;
        int diceChance = (skill != null) ? skill.getProbabilityDice() : 0;
        int itemConsumeId = (skill != null) ? skill.getItemConsumeId() : 0;
        int itemConsumeCount = (skill != null) ? skill.getItemConsumeCount() : 0;
        if (itemConsumeId > 0 && itemConsumeCount > 0) {
            if (!srcpc.getInventory().checkItem(itemConsumeId, itemConsumeCount)) {
                // 可選加訊息提醒
                // srcpc.sendPackets(new S_ServerMessage("施法需要道具不足!"));
                return 0;
            }
            srcpc.getInventory().consumeItem(itemConsumeId, itemConsumeCount);
        }
        // 2. 施法者6秒內分批回血，共1000
        int hotTime = 10, totalHeal = 3000;
        int healPerSec = totalHeal / hotTime;
        int lastHeal = totalHeal - healPerSec * (hotTime - 1);
        srcpc.setSkillEffect(L1SkillId.TITAN_STUN_HEAL, hotTime * 1000);
        Timer timer = new Timer();
        for (int i = 1; i <= hotTime; i++) {
            final int heal = (i == hotTime) ? lastHeal : healPerSec;
            int delay = i * 1000;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (srcpc.hasSkillEffect(L1SkillId.TITAN_STUN_HEAL)) {
                        int nowHp = srcpc.getCurrentHp();
                        int maxHp = srcpc.getMaxHp();
                        int newHp = Math.min(nowHp + heal, maxHp);
                        srcpc.setCurrentHp(newHp);
                        // srcpc.sendPackets(new S_SystemMessage("HOT回復HP：" + heal));
                    }
                }
            }, delay);
        }

        // 3. 範圍暈眩所有對象（不穿牆）
        for (Object obj : World.get().getVisibleObjects(srcpc, range)) {
            if (obj == null) continue;
            L1Object target = (L1Object) obj;
            // 跳過自己
            if (target instanceof L1PcInstance && ((L1PcInstance)target).getId() == srcpc.getId()) continue;
            // 判斷牆
            if (!World.get().isCanSee(srcpc, target)) continue;
            // 浮動命中率：固定+隨機
            int chance = baseChance + (diceChance > 0 ? new Random().nextInt(diceChance + 1) : 0);
            if (new Random().nextInt(100) >= chance) continue;
            // 暈眩秒數1~6
            int stunSec = new Random().nextInt(6) + 1;

            // 玩家暈眩
            if (target instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) target;
                pc.setSkillEffect(L1SkillId.TITAN_STUN, stunSec * 1000);
                pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
                pc.sendPackets(new S_IconConfig(S_IconConfig.SKILL_ICON, 26, stunSec, false, false));
                L1SpawnUtil.spawnEffect(200426, stunSec, pc.getX(), pc.getY(), pc.getMapId(), pc, 0);
            }
            // NPC暈眩
            else if (target instanceof L1MonsterInstance || target instanceof L1SummonInstance ||
                    target instanceof L1PetInstance || target instanceof L1GuardianInstance ||
                    target instanceof L1GuardInstance) {
                L1NpcInstance npc = (L1NpcInstance) target;
                npc.setSkillEffect(L1SkillId.TITAN_STUN, stunSec * 1000);
                npc.setParalyzed(true);
                L1SpawnUtil.spawnEffect(200426, stunSec, npc.getX(), npc.getY(), npc.getMapId(), npc, 0);
            }
        }
        return 0;
    }

    @Override
    public int start(final L1NpcInstance npc, final L1Character cha, final L1Magic magic, final int integer) throws Exception {
        return 0;
    }

    @Override
    public void start(final L1PcInstance srcpc, final Object obj) throws Exception {}

    @Override
    public void stop(final L1Character cha) throws Exception {
        if (cha instanceof L1PcInstance) {
            ((L1PcInstance) cha).sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, false));
        } else if (cha instanceof L1MonsterInstance || cha instanceof L1SummonInstance ||
                cha instanceof L1PetInstance || cha instanceof L1GuardianInstance ||
                cha instanceof L1GuardInstance) {
            L1NpcInstance npc = (L1NpcInstance) cha;
            npc.setParalyzed(false);
        }
    }
}
