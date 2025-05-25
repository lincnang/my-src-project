package com.lineage.server.model.Instance;

import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.S_NPCPack;
import com.lineage.server.serverpackets.S_SkillBrave;
import com.lineage.server.serverpackets.S_WarIcon;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.types.Point;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 黑騎士團
 */
public class L1BlackKnightInstance extends L1MonsterInstance {
    private static final Log _log = LogFactory.getLog(L1BlackKnightInstance.class);
    private static final long serialVersionUID = 1L;

    public L1BlackKnightInstance(L1Npc template) {
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
                perceivedFrom.sendPackets(new S_WarIcon(this.getId(), 2)); // 黑騎士團徽章
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
        // 黑騎士團打紅騎士團
        if (getMapId() == 4) {
            Point RedKnightpt = getLocation();
            for (L1Object object : World.get().getVisibleObjects(this, -1)) {
                if (object instanceof L1RedKnightInstance) {
                    L1RedKnightInstance RedKnight = (L1RedKnightInstance) object;
                    if (getMapId() != RedKnight.getMapId() // 不同地圖
                            || !RedKnightpt.isInScreen(RedKnight.getLocation())) { // 不在視野內
                        continue;
                    }
                    if (//RedKnight.getNpcId() >= 190355 && RedKnight.getNpcId() <= 190364 && // 目標不為空
                            RedKnight.getCurrentHp() > 0 // 目標體力大於0
                                    && !RedKnight.isDead() // 目標未死亡
                                    && getId() != RedKnight.getId() // 唯一ID不相同
                                    && get_showId() == RedKnight.get_showId()) { // 副本ID相同
                        _target = RedKnight;
                        return;
                    }
                }
            }
        }
    }
}
