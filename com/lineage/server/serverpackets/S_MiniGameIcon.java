package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;

/**
 * 遊戲各類徽章
 */
public class S_MiniGameIcon extends ServerBasePacket {
    private byte[] _byte = null;

    /**
     * 遊戲各類徽章
     * <p>
     * type= 1:葉子(暫無用)  2:骷髏(暗殺者)  3:火焰(復仇者)
     * <p>
     * type= 4:底比斯大戰專用(黃)  5:底比斯大戰專用(藍)  6:底比斯大戰專用(紅)
     */
    public S_MiniGameIcon(L1PcInstance pc) {
        writeC(S_EXTENDED_PROTOBUF);
        writeH(130);
        writeCC(1, pc.getId());
        int type = 0;
        if (pc.hasSkillEffect(L1SkillId.MiniGameIcon_1)) { // 葉子(暫無用)徽章狀態
            type = 1;
        } else if (pc.hasSkillEffect(L1SkillId.MiniGameIcon_2)) { // 骷髏(暗殺者)徽章狀態
            type = 2;
        } else if (pc.hasSkillEffect(L1SkillId.MiniGameIcon_3)) { // 火焰(復仇者)徽章狀態
            type = 3;
        } else if (pc.hasSkillEffect(L1SkillId.MiniGameIcon_4)) { // 底比斯大戰專用(黃)徽章狀態
            type = 4;
        } else if (pc.hasSkillEffect(L1SkillId.MiniGameIcon_5)) { // 底比斯大戰專用(藍)徽章狀態
            type = 5;
        } else if (pc.hasSkillEffect(L1SkillId.MiniGameIcon_6)) { // 底比斯大戰專用(紅)徽章狀態
            type = 6;
        }
        writeCC(2, type);
        writeH(0);
    }

    /**
     * 刪除徽章
     */
    public S_MiniGameIcon(int objId) {
        writeC(S_EXTENDED_PROTOBUF);
        writeH(130);
        writeCC(1, objId);
        writeCC(2, 7L);
        writeH(0);
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = _bao.toByteArray();
        }
        return _byte;
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
