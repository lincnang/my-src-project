package com.lineage.server.model.skill.skillmode;

import com.lineage.config.ConfigSkillKnight;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.utils.L1SpawnUtil;

import java.util.Random;

public class KINGDOM_STUN extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        // 註解：當玩家觸發「沖暈」技能時執行此方法。

        Random random = new Random();
        int shock = random.nextInt(3) + 2;
        // 註解：隨機生成基礎暈眩時間，範圍為 2 到 4 秒。

        if (((cha instanceof L1PcInstance)) && (cha.hasSkillEffect(87))) {
            shock += cha.getSkillEffectTimeSec(87);
        }
        // 註解：如果目標是玩家，並且已經被施加「暈眩」效果，則將剩餘暈眩時間累加到當前效果。

        if (srcpc.isKnight() && srcpc.getMeteLevel() >= 2) {   // SRC0808
            shock += ConfigSkillKnight.K2;
        }
        // 註解：如果釋放技能的玩家是騎士且「流星等級」大於等於 2，則額外增加暈眩時間（取自伺服器配置）。

        if (shock > 4) {
            shock = 4;
        }
        // 註解：暈眩時間上限為 4 秒，超過則設置為 4 秒。

        cha.setSkillEffect(87, shock * 1000);
        // 註解：對目標角色設置技能效果 ID 為 87（暈眩），持續時間為 shock 秒。

        L1SpawnUtil.spawnEffect(200300, shock, cha.getX(), cha.getY(), srcpc.getMapId(), srcpc, 0);
        // 註解：在地圖上目標位置生成技能特效，持續時間與暈眩時間一致。

        if ((cha instanceof L1PcInstance)) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.sendPackets(new S_Paralysis(5, true));
        }
        // 註解：如果目標是玩家，向其發送暈眩狀態的封包。

        else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1PetInstance))) {
            L1NpcInstance tgnpc = (L1NpcInstance) cha;
            tgnpc.setParalyzed(true);
        }
        // 註解：如果目標是怪物、召喚物或寵物，則設置目標為「癱瘓狀態」。

        return 0;
        // 註解：技能執行完畢，返回 0。
    }


    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        // 註解：當 NPC 觸發「沖暈」技能時執行此方法。

        Random random = new Random();
        String[] sec = ConfigSkillKnight.STUN_SEC.split("~");
        int shock = random.nextInt(Integer.valueOf(sec[1]).intValue() - Integer.valueOf(sec[0]).intValue() + 1) + Integer.valueOf(sec[0]).intValue();
        // 註解：從伺服器配置中讀取暈眩時間範圍，並隨機生成具體時間。

        if ((cha instanceof L1PcInstance) && cha.hasSkillEffect(87)) {
            shock += cha.getSkillEffectTimeSec(87);
        }
        // 註解：如果目標是玩家且已被施加暈眩效果，則累加剩餘暈眩時間。

        if (shock > 8) {
            shock = 8;
        }
        // 註解：暈眩時間上限為 8 秒，超過則設置為 8 秒。

        cha.setSkillEffect(87, shock * 1000);
        // 註解：對目標角色設置技能效果 ID 為 87（暈眩），持續時間為 shock 秒。

        L1SpawnUtil.spawnEffect(81162, shock, cha.getX(), cha.getY(), npc.getMapId(), npc, 0);
        // 註解：在地圖上目標位置生成技能特效。

        if ((cha instanceof L1PcInstance)) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.sendPackets(new S_Paralysis(5, true));
        }
        // 註解：如果目標是玩家，向其發送暈眩狀態的封包。

        else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1GuardianInstance)) || ((cha instanceof L1GuardInstance)) || ((cha instanceof L1PetInstance))) {
            L1NpcInstance tgnpc = (L1NpcInstance) cha;
            tgnpc.setParalyzed(true);
        }
        // 註解：如果目標是怪物、守護 NPC 或寵物，則設置為癱瘓狀態。

        return 0;
    }


    public void start(L1PcInstance srcpc, Object obj) throws Exception {
    }

    public void stop(L1Character cha) throws Exception {
        // 註解：停止「沖暈」技能效果，移除目標的暈眩狀態。

        if ((cha instanceof L1PcInstance)) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.sendPackets(new S_Paralysis(5, false));
        }
        // 註解：如果目標是玩家，向其發送解除暈眩狀態的封包。

        else if (((cha instanceof L1MonsterInstance)) || ((cha instanceof L1SummonInstance)) || ((cha instanceof L1GuardianInstance)) || ((cha instanceof L1GuardInstance)) || ((cha instanceof L1PetInstance))) {
            L1NpcInstance npc = (L1NpcInstance) cha;
            npc.setParalyzed(false);
        }
        // 註解：如果目標是怪物、守護 NPC 或寵物，移除癱瘓狀態。
    }
}
