package com.lineage.server.model.skill.skillmode;

import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_IconConfig;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.types.Point;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;

import java.util.Random;

/**
 * Shadow Daze 暗影暈眩（自動推進到目標前一格，遇障礙/目標即停，對目標判定暈眩）
 */
public class Shadow_Daze extends SkillMode {

    private static final Random _random = new Random();

    @Override
    public int start(final L1PcInstance srcpc, final L1Character target, final L1Magic magic, final int skillId) throws Exception {
        // 1. 技能資料
        L1Skills skill = SkillsTable.get().getTemplate(L1SkillId.Shadow_Daze);
        int itemConsumeId = (skill != null) ? skill.getItemConsumeId() : 0;
        int itemConsumeCount = (skill != null) ? skill.getItemConsumeCount() : 0;
        int baseChance = (skill != null) ? skill.getProbabilityValue() : 100;
        int diceChance = (skill != null) ? skill.getProbabilityDice() : 0;

        // 2. 道具消耗判斷
        if (itemConsumeId > 0 && itemConsumeCount > 0) {
            if (!srcpc.getInventory().checkItem(itemConsumeId, itemConsumeCount)) {
                srcpc.sendPackets(new S_SystemMessage("施展暗影暈眩需要消耗指定道具！"));
                return 0;
            }
            srcpc.getInventory().consumeItem(itemConsumeId, itemConsumeCount);
        }

        // 3. 若無目標直接結束
        if (target == null) {
            srcpc.sendPackets(new S_SystemMessage("未指定目標。"));
            return 0;
        }

        // 4. 推進到目標前一格（自動朝向目標，不判斷角色面向）
        int steps = 5; // 最多推進5格
        Point current = new Point(srcpc.getX(), srcpc.getY());
        short mapId = srcpc.getMapId();

        for (int i = 0; i < steps; i++) {
            // 已在目標相鄰8格內，則停下
            if (isAdjacent(current, target)) break;

            int heading = calcHeading(current.getX(), current.getY(), target.getX(), target.getY());
            Point next = calcForwardPosition(current.getX(), current.getY(), heading);

            // 障礙物阻擋、有人阻擋則停下
            if (!isWalkable(next.getX(), next.getY(), mapId)) break;
            if (getCharacterAt(next.getX(), next.getY(), mapId, srcpc) != null) break;

            current = next;
        }

        // 5. 檢查終點可到達（視線）
        if (!srcpc.glanceCheck(current.getX(), current.getY())) {
            srcpc.sendPackets(new S_SystemMessage("你無法看到前方的地點！"));
            return -1;
        }

        // 6. 傳送玩家到目標前一格
        L1Teleport.teleport(srcpc, current.getX(), current.getY(), mapId, srcpc.getHeading(), false, 0);

        // 7. 對目標本身進行暈眩判定
        int chance = baseChance + (diceChance > 0 ? _random.nextInt(diceChance + 1) : 0);
        int roll = _random.nextInt(100);
        if (roll < chance) {
            int stunSec = _random.nextInt(4) + 2; // 2~5秒
            if (target instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) target;
                pc.setSkillEffect(L1SkillId.Shadow_Daze, stunSec * 1000);
                pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
                pc.sendPackets(new S_IconConfig(S_IconConfig.SKILL_ICON, 30, stunSec, false, false));
                L1SpawnUtil.spawnEffect(200426, stunSec, pc.getX(), pc.getY(), pc.getMapId(), pc, 0);
            } else if (target instanceof L1NpcInstance) {
                L1NpcInstance npc = (L1NpcInstance) target;
                npc.setSkillEffect(L1SkillId.Shadow_Daze, stunSec * 1000);
                npc.setParalyzed(true);
                L1SpawnUtil.spawnEffect(200426, stunSec, npc.getX(), npc.getY(), npc.getMapId(), npc, 0);
            }
        } else {
        }

        return 0;
    }

    @Override
    public void start(final L1PcInstance srcpc, final Object obj) throws Exception {
        if (obj instanceof L1Character) {
            this.start(srcpc, (L1Character) obj, null, L1SkillId.Shadow_Daze);
        }
    }

    // 判斷兩格是否相鄰（8方向）
    private boolean isAdjacent(Point a, L1Character b) {
        int dx = Math.abs(a.getX() - b.getX());
        int dy = Math.abs(a.getY() - b.getY());
        return dx <= 1 && dy <= 1 && !(dx == 0 && dy == 0);
    }

    // 計算朝向目標的heading
    private int calcHeading(int myX, int myY, int targetX, int targetY) {
        int dx = targetX - myX;
        int dy = targetY - myY;
        if (dx > 0 && dy < 0) return 1; // 右上
        if (dx > 0 && dy == 0) return 2; // 右
        if (dx > 0 && dy > 0) return 3; // 右下
        if (dx == 0 && dy > 0) return 4; // 下
        if (dx < 0 && dy > 0) return 5; // 左下
        if (dx < 0 && dy == 0) return 6; // 左
        if (dx < 0 && dy < 0) return 7; // 左上
        if (dx == 0 && dy < 0) return 0; // 上
        return 0; // 預設北
    }

    // 取得往前一格座標（heading同天堂官方）
    private Point calcForwardPosition(int x, int y, int heading) {
        final int[][] DIRECTION_OFFSETS = {
                {0, -1},    // 0: 北(上)
                {1, -1},    // 1: 右上
                {1, 0},     // 2: 右
                {1, 1},     // 3: 右下
                {0, 1},     // 4: 南(下)
                {-1, 1},    // 5: 左下
                {-1, 0},    // 6: 左
                {-1, -1}    // 7: 左上
        };
        int dx = DIRECTION_OFFSETS[heading][0];
        int dy = DIRECTION_OFFSETS[heading][1];
        return new Point(x + dx, y + dy);
    }

    // 判斷是否可通行
    private boolean isWalkable(int x, int y, short mapId) {
        // TODO: 可接你的地形判斷
        return true;
    }

    // 判斷該格是否有玩家或敵方（排除自己）
    private L1Character getCharacterAt(int x, int y, short mapId, L1PcInstance self) {
        // 玩家
        for (L1PcInstance pc : World.get().getAllPlayers()) {
            if (pc.getMapId() == mapId && pc.getX() == x && pc.getY() == y && pc != self) {
                return pc;
            }
        }
        // NPC、怪物、守衛、召喚獸、寵物
        for (L1Object obj : World.get().getObject()) {
            if (obj instanceof L1MonsterInstance || obj instanceof L1SummonInstance ||
                    obj instanceof L1PetInstance || obj instanceof L1GuardianInstance ||
                    obj instanceof L1GuardInstance) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                if (npc.getMapId() == mapId && npc.getX() == x && npc.getY() == y) {
                    return npc;
                }
            }
        }
        return null;
    }

    @Override
    public int start(final L1NpcInstance npc, final L1Character cha, final L1Magic magic, final int integer) throws Exception {
        return 0;
    }

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
