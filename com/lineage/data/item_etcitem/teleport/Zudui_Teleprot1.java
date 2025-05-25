package com.lineage.data.item_etcitem.teleport;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.MapsTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.serverpackets.*;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Zudui_Teleprot1 extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(Zudui_Teleprot1.class);
    private int _remove = 0;

    /**
     *
     */
    private Zudui_Teleprot1() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Zudui_Teleprot1();
    }

    /**
     * 組隊穿雲箭
     *
     * @param data 參數
     * @param pc   執行者
     * @param item 物件
     */
    @Override
    public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
        boolean isNowWar = false;
        int castleId = 0;
        castleId = L1CastleLocation.getCastleIdByArea(pc);
        if (castleId != 0) {
            isNowWar = ServerWarExecutor.get().isNowWar(castleId);
        }
        try {
            if (!pc.getMap().chuanyunjian()) {
                pc.sendPackets(new S_SystemMessage("此地圖無法使用穿雲箭用" + item.getName()));
                return;
            }
            if (!pc.isInParty()) {
                pc.sendPackets(new S_ServerMessage("\\fY目前沒有組隊，因此無法使用"));
                return;
            }
            if (isNowWar) {
                pc.sendPackets(new S_SystemMessage("城戰旗幟內無法使用."));
                return;
            }
            if (pc.getMapId() == 99) {
                pc.sendPackets(new S_SystemMessage("GM房間內無法使用."));
                return;
            }
            for (L1PcInstance find_pc : pc.getParty().partyUsers()) {
                //				if(!find_pc.iszudui()) {
                //					return;
                //				}
                // 組隊與人員
                if (find_pc.getId() != pc.getId()) {
                    find_pc.setTempID(pc.getId()); // 暫存盟主ID
                    // 729 盟主正在呼喚你，你要接受他的呼喚嗎？(Y/N)
                    find_pc.sendPackets(new S_Message_YN(729));
                }
            }
            int mapid = pc.getMapId();
            pc.sendPacketsAll(new S_SkillSound(pc.getId(), 729));
            World.get().broadcastPacketToAll(new S_BlueMessage(166, (new StringBuilder("\\f3 大神" + " 【").append(pc.getName()).append("】在【").append(MapsTable.get().locationname(mapid)).append("】. \\f4 使用了一隻穿雲箭！\\f2 千軍萬馬來相見！").toString())));
            pc.sendPackets(new S_ServerMessage("\\fY你已經對所有在線隊伍成員發出訊息!"));
            if (_remove == 1) {
                pc.getInventory().removeItem(item, 1);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void set_set(String[] set) {
        try {
            _remove = Integer.parseInt(set[1]);
        } catch (Exception e) {
        }
    }
}
