package com.lineage.server.model.skill.skillmode;

import com.lineage.config.ConfigSkillKnight;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.utils.L1SpawnUtil;

import java.util.Random;

public class KINGDOM_STUN extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        // 技能ID固定為 87（沖暈技能）
        L1Skills skill = SkillsTable.get().getTemplate(87);
        int itemConsumeId = (skill != null) ? skill.getItemConsumeId() : 0;
        int itemConsumeCount = (skill != null) ? skill.getItemConsumeCount() : 0;
        int range = (skill != null) ? skill.getRanged() : 1; // 沒設則預設近戰1格
        int baseChance = (skill != null) ? skill.getProbabilityValue() : 100; // 沒設就必中
        int diceChance = (skill != null) ? skill.getProbabilityDice() : 0;

        // 1. 判斷距離
        int dist = srcpc.getLocation().getTileLineDistance(cha.getLocation());
        if (dist > range) {
            srcpc.sendPackets(new S_ServerMessage("目標太遠，無法施放王者之劍！"));
            return 0;
        }

        // 2. 判斷消耗道具
        if (itemConsumeId > 0 && itemConsumeCount > 0) {
            if (!srcpc.getInventory().checkItem(itemConsumeId, itemConsumeCount)) {
                srcpc.sendPackets(new S_ServerMessage("王者之劍所需道具不足！"));
                return 0;
            }
            srcpc.getInventory().consumeItem(itemConsumeId, itemConsumeCount);
        }

        // 3. 判斷施法命中（DB機率+浮動骰子）
        int chance = baseChance + (diceChance > 0 ? new Random().nextInt(diceChance + 1) : 0);
        if (new Random().nextInt(100) >= chance) {
            srcpc.sendPackets(new S_ServerMessage("王者之劍未命中！"));
            return 0;
        }

        // 4. 暈眩邏輯
        Random random = new Random();
        int shock = random.nextInt(3) + 2; // 2~4秒
        if (((cha instanceof L1PcInstance)) && (cha.hasSkillEffect(87))) {
            shock += cha.getSkillEffectTimeSec(87);
        }
        if (srcpc.isKnight() && srcpc.getMeteLevel() >= 2) {
            shock += ConfigSkillKnight.K2;
        }
        if (shock > 4) shock = 4;
        cha.setSkillEffect(87, shock * 1000);
        L1SpawnUtil.spawnEffect(200300, shock, cha.getX(), cha.getY(), srcpc.getMapId(), srcpc, 0);

        if ((cha instanceof L1PcInstance)) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.sendPackets(new S_Paralysis(5, true));
        }
        else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) {
            L1NpcInstance tgnpc = (L1NpcInstance) cha;
            tgnpc.setParalyzed(true);
        }
        return 0;
    }

    // NPC 版本略，可依需求加入判斷

    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        // 不強制加距離判斷（NPC範圍/射程看情境要不要）
        Random random = new Random();
        String[] sec = ConfigSkillKnight.STUN_SEC.split("~");
        int shock = random.nextInt(Integer.parseInt(sec[1]) - Integer.parseInt(sec[0]) + 1) + Integer.parseInt(sec[0]);
        if ((cha instanceof L1PcInstance) && cha.hasSkillEffect(87)) {
            shock += cha.getSkillEffectTimeSec(87);
        }
        if (shock > 8) {
            shock = 8;
        }
        cha.setSkillEffect(87, shock * 1000);
        L1SpawnUtil.spawnEffect(81162, shock, cha.getX(), cha.getY(), npc.getMapId(), npc, 0);
        if ((cha instanceof L1PcInstance)) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.sendPackets(new S_Paralysis(5, true));
        }
        else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance)
                || (cha instanceof L1GuardianInstance) || (cha instanceof L1GuardInstance) || (cha instanceof L1PetInstance)) {
            L1NpcInstance tgnpc = (L1NpcInstance) cha;
            tgnpc.setParalyzed(true);
        }
        return 0;
    }

    public void start(L1PcInstance srcpc, Object obj) throws Exception {
    }

    public void stop(L1Character cha) throws Exception {
        if ((cha instanceof L1PcInstance)) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.sendPackets(new S_Paralysis(5, false));
        }
        else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance)
                || (cha instanceof L1GuardianInstance) || (cha instanceof L1GuardInstance) || (cha instanceof L1PetInstance)) {
            L1NpcInstance npc = (L1NpcInstance) cha;
            npc.setParalyzed(false);
        }
    }
}
