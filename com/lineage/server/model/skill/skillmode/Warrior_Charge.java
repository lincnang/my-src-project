package com.lineage.server.model.skill.skillmode;

import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_IconConfig;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.types.Point;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;

import java.util.Random;

/**
 * 戰士衝鋒（撞到玩家/NPC停在對方面前，對目標造成暈眩，含道具消耗/機率）
 */
public class Warrior_Charge extends SkillMode {

    private static final Random _random = new Random();

    @Override
    public int start(final L1PcInstance srcpc, final L1Character target, final L1Magic magic, final int skillId) throws Exception {

        // 1. 技能資料(道具消耗、暈眩機率)從DB取得
        L1Skills skill = SkillsTable.get().getTemplate(L1SkillId.Warrior_Charge);
        int itemConsumeId = (skill != null) ? skill.getItemConsumeId() : 0;
        int itemConsumeCount = (skill != null) ? skill.getItemConsumeCount() : 0;
        int baseChance = (skill != null) ? skill.getProbabilityValue() : 100; // 預設100%必定成功
        int diceChance = (skill != null) ? skill.getProbabilityDice() : 0;

        // 2. 檢查道具消耗
        if (itemConsumeId > 0 && itemConsumeCount > 0) {
            if (!srcpc.getInventory().checkItem(itemConsumeId, itemConsumeCount)) {
                srcpc.sendPackets(new S_SystemMessage("施展衝鋒技能需要消耗指定道具！"));
                return 0;
            }
            srcpc.getInventory().consumeItem(itemConsumeId, itemConsumeCount);
        }

        int destX = srcpc.getX();
        int destY = srcpc.getY();
        int destHeading = srcpc.getHeading();
        short destMapId = srcpc.getMapId();

        int steps = 5;
        Point current = new Point(destX, destY);
        Point next;

        // 3. 前進，遇到障礙或目標就停在前一格
        for (int i = 0; i < steps; i++) {
            next = calcForwardPosition(current.getX(), current.getY(), destHeading);
            if (!isWalkable(next.getX(), next.getY(), destMapId)) {
                break;
            }
            if (getCharacterAt(next.getX(), next.getY(), destMapId, srcpc) != null) {
                break;
            }
            current = next;
        }

        // 4. 停下來後，「面前一格」有目標就對該對象進行機率暈眩
        Point front = calcForwardPosition(current.getX(), current.getY(), destHeading);
        L1Character targetInFront = getCharacterAt(front.getX(), front.getY(), destMapId, srcpc);

        if (targetInFront != null) {
            // 機率計算：baseChance + 0~diceChance
            int chance = baseChance + (diceChance > 0 ? _random.nextInt(diceChance + 1) : 0);
            int roll = _random.nextInt(100);
            if (roll < chance) {
                int stunSec = _random.nextInt(5) + 1;
                if (targetInFront instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) targetInFront;
                    pc.setSkillEffect(L1SkillId.Warrior_Charge, stunSec * 1000);
                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
                    pc.sendPackets(new S_IconConfig(S_IconConfig.SKILL_ICON, 26, stunSec, false, false));
                    L1SpawnUtil.spawnEffect(200426, stunSec, pc.getX(), pc.getY(), pc.getMapId(), pc, 0);
//                    srcpc.sendPackets(new S_SystemMessage("你撞擊到 " + pc.getName() + " 並造成暈眩!"));
                } else if (targetInFront instanceof L1MonsterInstance || targetInFront instanceof L1SummonInstance ||
                        targetInFront instanceof L1PetInstance || targetInFront instanceof L1GuardianInstance ||
                        targetInFront instanceof L1GuardInstance) {
                    L1NpcInstance npc = (L1NpcInstance) targetInFront;
                    npc.setSkillEffect(L1SkillId.Warrior_Charge, stunSec * 1000);
                    npc.setParalyzed(true);
                    L1SpawnUtil.spawnEffect(200426, stunSec, npc.getX(), npc.getY(), npc.getMapId(), npc, 0);
//                    srcpc.sendPackets(new S_SystemMessage("你撞擊到 " + npc.getName() + " 並造成暈眩!"));
                }
            } else {
//                srcpc.sendPackets(new S_SystemMessage("衝鋒未能使目標暈眩。"));
            }
        }

        // 5. 檢查目標地點是否可到達
        if (!srcpc.glanceCheck(current.getX(), current.getY())) {
            srcpc.sendPackets(new S_SystemMessage("你無法看到前方的地點！"));
            return -1;
        }

        // 6. 傳送自己到最終位置
        L1Teleport.teleport(srcpc, current.getX(), current.getY(), destMapId, destHeading, false, 0);
        return 0;
    }

    @Override
    public void start(final L1PcInstance srcpc, final Object obj) throws Exception {
        this.start(srcpc, (L1Character) null, null, L1SkillId.Warrior_Charge);
    }

    // 前進一格計算
    private Point calcForwardPosition(int x, int y, int heading) {
        final int[][] DIRECTION_OFFSETS = {
                {0, -1}, {1, -1}, {1, 0}, {1, 1},
                {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}
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
