package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.serverpackets.S_SkillBrave;

import static com.lineage.server.model.skill.L1SkillId.STATUS_BRAVE;

public class HASTE3X extends SkillMode {
    public int start(L1PcInstance srcpc, L1Character cha, L1Magic magic, int integer) throws Exception {
        // 以 DB 的 buffDuration 秒數為準
        final int seconds = Math.max(1, integer);
        srcpc.setSkillEffect(STATUS_BRAVE, seconds * 1000);
        srcpc.setBraveSpeed(1);
        // 顯示勇敢狀態圖示（第一階段勇水圖示）
        srcpc.sendPacketsAll(new S_SkillBrave(srcpc.getId(), 1, seconds));
        return 0;
    }

    @Override
    public int start(L1NpcInstance npc, L1Character cha, L1Magic magic, int integer) throws Exception {
        return 0;
    }

    public void start(L1PcInstance srcpc, Object obj) throws Exception {
    }

    public void stop(L1Character cha) throws Exception {
    }
}
