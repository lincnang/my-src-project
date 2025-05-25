package com.lineage.server.model.Instance;

import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.DoorSpawnTable;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.S_NPCPack;
import com.lineage.server.serverpackets.S_SkillBrave;
import com.lineage.server.serverpackets.S_WarIcon;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 紅騎士團
 */
public class L1RedKnightInstance extends L1MonsterInstance {
    private static final Log _log = LogFactory.getLog(L1RedKnightInstance.class);
    private static final long serialVersionUID = 1L;

    public L1RedKnightInstance(L1Npc template) {
        super(template);
    }

    /**
     * TODO 接觸資訊
     */
    @Override
    public void onPerceive(L1PcInstance perceivedFrom) {
        try {
            if (perceivedFrom.get_showId() != get_showId()) {
                return;
            }
            perceivedFrom.addKnownObject(this);
            if (getCurrentHp() > 0) {
                perceivedFrom.sendPackets(new S_NPCPack(this));
                onNpcAI();
                if (getBraveSpeed() == 1) {
                    perceivedFrom.sendPackets(new S_SkillBrave(getId(), 1, 600000));
                }
                perceivedFrom.sendPackets(new S_WarIcon(this.getId(), 1)); // 紅騎士團徽章
            } else {
                perceivedFrom.sendPackets(new S_NPCPack(this));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 攻擊目標搜尋
     */
    @Override
    public void searchTarget() {
        if (getMapId() == 4) {
            // 紅騎士團打城門
            for (L1DoorInstance door : DoorSpawnTable.get().getDoorByMap(getMapId())) {
                if (door.getOpenStatus() != ActionCodes.ACTION_Close // 不是關閉
                        || door.isDead() // 目標未死亡
                        || door.getMaxHp() <= 1 || !getLocation().isInScreen(door.getLocation()) // 不在視野內
                ) {
                    continue;
                }
                _target = door;
                return;
            }
            // 紅騎士團打黑騎士團
            for (L1Object object : World.get().getVisibleObjects(this, 500)) {
                if (object instanceof L1BlackKnightInstance) {
                    L1BlackKnightInstance BlackKnight = (L1BlackKnightInstance) object;
                    if (getMapId() != BlackKnight.getMapId()) { // 不同地圖
                        continue;
                    }
                    if (//BlackKnight.getNpcId() >= 190449 && BlackKnight.getNpcId() <= 190456 && // 目標不為空
                            BlackKnight.getCurrentHp() > 0 // 目標體力大於0
                                    && !BlackKnight.isDead() // 目標未死亡
                                    && getId() != BlackKnight.getId() // 唯一ID不相同
                                    && get_showId() == BlackKnight.get_showId()) { // 副本ID相同
                        _target = BlackKnight;
                        return;
                    }
                }
            }
        }
    }
}
