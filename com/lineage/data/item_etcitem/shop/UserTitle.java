package com.lineage.data.item_etcitem.shop;

import com.lineage.config.ConfigOther;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UserTitle extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(UserTitle.class);

    public static ItemExecutor get() {
        return new UserTitle();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (item == null) {
            return;
        }
        if (pc == null) {
            return;
        }
        if (pc.isGhost()) {
            return;
        }
        if (pc.isDead()) {
            return;
        }
        if (pc.isTeleport()) {
            return;
        }
        if (pc.is_repass() != 0) {
            pc.sendPackets(new S_ServerMessage("\\fU你不是要變更密碼嗎?"));
            return;
        }
        if (!ConfigOther.CLANTITLE) {
            pc.sendPackets(new S_ServerMessage("\\fT尚未開放"));
            return;
        }
        if (pc.isPrivateShop()) {
            pc.sendPackets(new S_ServerMessage("\\fT請先結束商店村模式!"));
            return;
        }
        try {
            pc.retitle(true);
            pc.sendPackets(new S_ServerMessage("\\fR輸入封號後直接按下ENTER"));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.shop.UserTitle JD-Core Version: 0.6.2
 */