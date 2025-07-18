package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.world.World;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.templates.L1Skills;

import java.util.ArrayList;
import java.util.List;
/**
 * 流星之箭
 *
 * @author 老爹
 */
public class Skill_MeteorArrow extends SkillMode {

    private static final int METEOR_ANIMATION = 19095;
    private static final int METEOR_MAIN_DMG = 50; // 主目標傷害
    private static final int METEOR_AREA_DMG = 10;  // 其他範圍怪物傷害

    @Override
    public int start(L1PcInstance pc, L1Character target, L1Magic magic, int dmg) {
        if (target == null) return 0;

        // 這裡加主目標隔牆判斷
        if (!isLinePassable(pc, target)) {
            // 可以加系統訊息給玩家：目標無法攻擊
//            pc.sendPackets(new S_ServerMessage(280)); // XX無法選擇該目標
            return 0;
        }

        int skillId = 139;
        L1Skills skill = SkillsTable.get().getTemplate(skillId);
        int actionId = (skill != null && skill.getActionId() > 0) ? skill.getActionId() : 18;

        // 施法動作動畫
        pc.sendPackets(new S_DoActionGFX(pc.getId(), actionId));
        pc.broadcastPacketAll(new S_DoActionGFX(pc.getId(), actionId));

        int range = 6;
        int count = 0;

        // 主目標先處理
        if (target instanceof L1MonsterInstance) {
            ((L1MonsterInstance) target).receiveDamage(pc, METEOR_MAIN_DMG);
            ((L1MonsterInstance) target).broadcastPacketAll(new S_SkillSound(target.getId(), METEOR_ANIMATION));
            count++;
        }

        // 範圍目標不包含主目標自己
        List<L1Character> targets = getTargetsInRange(target, range);
        for (L1Character t : targets) {
            if (t == target) continue;
            if (t instanceof L1MonsterInstance && isLinePassable(pc, t)) {
                ((L1MonsterInstance) t).receiveDamage(pc, METEOR_AREA_DMG);
                ((L1MonsterInstance) t).broadcastPacketAll(new S_SkillSound(t.getId(), METEOR_ANIMATION));
                count++;
            }
        }
        return count;
    }

    @Override
    public int start(L1NpcInstance npc, L1Character target, L1Magic magic, int dmg) {
        if (target == null) return 0;

        if (!isLinePassable(npc, target)) {
            return 0;
        }

        int range = 10;
        int count = 0;

        if (target instanceof L1MonsterInstance) {
            ((L1MonsterInstance) target).receiveDamage(npc, METEOR_MAIN_DMG);
            ((L1MonsterInstance) target).broadcastPacketAll(new S_SkillSound(target.getId(), METEOR_ANIMATION));
            count++;
        }

        List<L1Character> targets = getTargetsInRange(target, range);
        for (L1Character t : targets) {
            if (t == target) continue;
            if (t instanceof L1MonsterInstance && isLinePassable(npc, t)) {
                ((L1MonsterInstance) t).receiveDamage(npc, METEOR_AREA_DMG);
                ((L1MonsterInstance) t).broadcastPacketAll(new S_SkillSound(t.getId(), METEOR_ANIMATION));
                count++;
            }
        }
        return count;
    }

    @Override
    public void stop(L1Character cha) {}

    @Override
    public void start(L1PcInstance pc, Object obj) {}

    /**
     * 取得以 center 為中心、range 格內所有 L1Character 物件
     */
    private List<L1Character> getTargetsInRange(L1Character center, int range) {
        List<L1Character> result = new ArrayList<>();
        for (Object obj : World.get().getVisibleObjects(center, range)) {
            if (obj instanceof L1Character) {
                result.add((L1Character) obj);
            }
        }
        return result;
    }

    /**
     * 判斷 from 到 to 之間是否可以直線通過（不可穿牆）
     */
    private boolean isLinePassable(L1Character from, L1Character to) {
        int x1 = from.getX(), y1 = from.getY();
        int x2 = to.getX(), y2 = to.getY();
        L1Map map = from.getMap();
        int dx = Math.abs(x2 - x1), dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1, sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;
        boolean first = true;
        while (x1 != x2 || y1 != y2) {
            if (!first && !map.isPassable(x1, y1)) return false;
            first = false;
            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x1 += sx; }
            if (e2 < dx) { err += dx; y1 += sy; }
        }
        return true;
    }
}
