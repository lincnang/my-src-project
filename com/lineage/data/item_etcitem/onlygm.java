package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;

/**
 * 怪物偷窺卷	Peeping_card	怪物偷窺卷	other	spell_long	paper	0	229	3963	0	1	0	0	0	0	0	1	0	1	0	0	0	0	0
 * 偷窺卷	Peeping_card	偷窺卷	other	spell_long	paper	0	194	3963	0	1	0	0	0	0	0	1	0	1	0	0	0	0	0
 * 反偷窺卷	0	反偷窺卷	other	normal	paper	0	198	3963	0	1	0	0	0	0	0	1	0	1	0	0	0	0	0
 * [原碼] 偷窺
 */
public class onlygm extends ItemExecutor {
    /**
     *
     */
    private onlygm() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new onlygm();
    }

    /**
     * 道具物件執行
     *
     * @param data 參數
     * @param pc   執行者
     * @param item 物件
     */
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        final int spellsc_objid = data[0];
        final int itemId = item.getItemId();
        //if ((itemId >= 44334 && itemId <= 44335)) {
        //  if (itemId == 44444) { // 玩家偷窺卡
        L1Object target = World.get().findObject(spellsc_objid);
        L1PcInstance target_pc = (L1PcInstance) target;
        if (itemId == 240309) {
            target_pc.setSkillEffect(9990, 21600 * 1000);//6小時
            L1Teleport.teleport(target_pc, 32768, 32773, (short) 7787, 5, true);
            target_pc.sendPackets(new S_SystemMessage("因為您未通過線上GM驗證，即刻起將入監6個小時。"));
            target_pc.sendPackets(new S_ServerMessage("\\fX請保持角色在線以扣除監禁時間。"));
        } else if (itemId == 240310) {
            target_pc.setSkillEffect(9990, 43200 * 1000);//12小時
            L1Teleport.teleport(target_pc, 32768, 32773, (short) 7787, 5, true);
            target_pc.sendPackets(new S_SystemMessage("因為您未通過線上GM驗證，即刻起將入監12個小時。"));
            target_pc.sendPackets(new S_ServerMessage("\\fX請保持角色在線以扣除監禁時間。"));
        } else if (itemId == 240311) {
            target_pc.setSkillEffect(9990, 86400 * 1000);//24小時
            L1Teleport.teleport(target_pc, 32768, 32773, (short) 7787, 5, true);
            target_pc.sendPackets(new S_SystemMessage("因為您未通過線上GM驗證，即刻起將入監24個小時。"));
            target_pc.sendPackets(new S_ServerMessage("\\fX請保持角色在線以扣除監禁時間。"));
        } else if (itemId == 240312) {
            if (target_pc.hasSkillEffect(9990)) {
                target_pc.killSkillEffectTimer(9990);
                target_pc.sendPackets(new S_ServerMessage("\\fX您的監禁時間已解除。"));
            }
        }
    }
}
