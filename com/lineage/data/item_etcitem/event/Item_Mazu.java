package com.lineage.data.item_etcitem.event;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.lineage.server.model.skill.L1SkillId.Mazu;

/**
 * 媽祖祝福<BR>
 * <p>
 * DELETE FROM `etcitem` WHERE `item_id`='49532'; INSERT INTO `etcitem` VALUES
 * (49532, '虔誠祝福', 'event.Item_Mazu', '虔誠祝福', 'other', 'normal', 'gemstone', 0,
 * 2563, 3963, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0);
 *
 * @author loli
 */
public class Item_Mazu extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(Item_Mazu.class);
    private int _mazutime;

    private Item_Mazu() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Item_Mazu();
    }

    // @Override
    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        try {
            // 例外狀況:物件為空
            if (item == null) {
                return;
            }
            // 例外狀況:人物為空
            if (pc == null) {
                return;
            }
            if (pc.hasSkillEffect(Mazu)) {
                pc.sendPackets(new S_ServerMessage("\\fV媽祖祝福效果時間尚有" + pc.getSkillEffectTimeSec(Mazu) + "秒"));
                return;
            }
            pc.getInventory().removeItem(item, 1);
            pc.set_mazu(true);
            pc.setSkillEffect(Mazu, _mazutime * 60 * 1000);
            pc.getQuest().set_step(30000, 255);
            pc.sendPacketsX8(new S_SkillSound(pc.getId(), 7321));
            pc.set_mazu_time(_mazutime * 60);
            pc.sendPackets(new S_SystemMessage("獲得媽祖狀態。"));
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void set_set(String[] set) {
        try {
            _mazutime = Integer.parseInt(set[1]);
        } catch (Exception e) {
        }
    }
}
