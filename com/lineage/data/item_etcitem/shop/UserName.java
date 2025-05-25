package com.lineage.data.item_etcitem.shop;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UserName extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(UserName.class);

    public static ItemExecutor get() {
        return new UserName();
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
        if (pc.getLawful() < 32767) {
            pc.sendPackets(new S_ServerMessage("\\fT正義質必須為32767才可以使用"));
            return;
        }
        if (pc.isPrivateShop()) {
            pc.sendPackets(new S_ServerMessage("\\fT請先結束商店村模式!"));
            return;
        }
        Object[] petList = pc.getPetList().values().toArray();
        if (petList.length > 0) {
            pc.sendPackets(new S_ServerMessage("\\fT請先回收寵物!"));
            return;
        }
        if (!pc.getDolls().isEmpty()) {
            pc.sendPackets(new S_ServerMessage("\\fT請先回收魔法娃娃!"));
            return;
        }
        if (!pc.getDolls2().isEmpty()) {
            pc.sendPackets(new S_ServerMessage("\\fT請先回收特殊魔法娃娃!"));
            return;
        }
        if (pc.get_power_doll() != null) {
            pc.sendPackets(new S_ServerMessage("\\fT請先回收超級娃娃!"));
            return;
        }
        if (pc.getParty() != null) {
            pc.sendPackets(new S_ServerMessage("\\fT請先退出隊伍!"));
            return;
        }
        if (pc.getClanid() != 0) {
            pc.sendPackets(new S_ServerMessage("\\fT請先退出血盟!"));
            return;
        }
        try {
            pc.sendPackets(new S_Message_YN(325));
            pc.rename(true);
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
