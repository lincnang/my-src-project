package com.lineage.data.item_etcitem.brave;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillBrave;
import com.lineage.server.serverpackets.S_SkillSound;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.lineage.server.model.skill.L1SkillId.STATUS_BRAVE2;

/**
 * 荒神藥水類型<br>
 */
public class StatusThirdSpeed extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(StatusThirdSpeed.class);
    private int _time;

    /**
     *
     */
    private StatusThirdSpeed() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new StatusThirdSpeed();
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
        // 例外狀況:物件為空
        if (item == null) {
            return;
        }
        // 例外狀況:人物為空
        if (pc == null) {
            return;
        }
        if (L1BuffUtil.stopPotion(pc)) {
            // 血之渴望
            if (pc.hasSkillEffect(L1SkillId.BLOODLUST)) {
                // 1,413：目前情況是無法使用。
                pc.sendPackets(new S_ServerMessage(1413));
                return;
            }
            // 具有生命之樹果實效果
            if (pc.hasSkillEffect(L1SkillId.STATUS_RIBRAVE)) {
                // 1,413：目前情況是無法使用。
                pc.sendPackets(new S_ServerMessage(1413));
                return;
            }
            // 解除魔法技能絕對屏障
            L1BuffUtil.cancelAbsoluteBarrier(pc);
            // 勇敢效果 抵銷對應技能
            L1BuffUtil.braveStart(pc);
            pc.getInventory().removeItem(item, 1);
            pc.sendPackets(new S_SkillBrave(pc.getId(), 5, _time));
            pc.broadcastPacketAll(new S_SkillBrave(pc.getId(), 5, 0));
            pc.sendPackets(new S_SkillSound(pc.getId(), 751));
            pc.broadcastPacketAll(new S_SkillSound(pc.getId(), 751));
            pc.setSkillEffect(STATUS_BRAVE2, _time * 1000);
            //pc.sendPackets(new S_SystemMessage("你速度像火箭一樣！"));
            pc.setBraveSpeed(5);
        }
    }

    @Override
    public void set_set(final String[] set) {
        try {
            _time = Integer.parseInt(set[1]);
            if (_time <= 0) {
                _log.error("StatusThirdSpeed 設置錯誤:技能效果時間小於等於0! 使用預設300秒");
                _time = 300;
            }
        } catch (Exception localException) {
        }
    }
}