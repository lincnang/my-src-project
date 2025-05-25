package com.lineage.data.item_etcitem.shop;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 邪惡藥水
 *
 * @author dexc
 */
public class UserColorD extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(UserColorD.class);

    public static ItemExecutor get() {
        return new UserColorD();
    }

    /**
     * 道具物件執行
     *
     * @param data 參數
     * @param pc   執行者
     * @param item 物件
     */
    @Override
    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int lawful = pc.getLawful();
        //if (lawful >= 0) {
        if (lawful > -32767) {
            pc.getInventory().removeItem(item, 1L);
            pc.addLawful(-70000);
            pc.sendPacketsX8(new S_SkillSound(pc.getId(), 198));
            try {
                pc.save();
            } catch (Exception e) {
                _log.error(e.getLocalizedMessage(), e);
            }
        } else {
            //pc.sendPackets(new S_ServerMessage(79)); // 沒有任何事情發生。
            pc.sendPackets(new S_ServerMessage("邪惡值已滿，無法再使用。"));
        }
    }
}
