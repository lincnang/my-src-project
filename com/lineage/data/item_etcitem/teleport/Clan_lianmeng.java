package com.lineage.data.item_etcitem.teleport;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.lock.ClanAllianceReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Alliance;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Clan;
import com.lineage.server.serverpackets.S_GreenMessage;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Clan_lianmeng extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(Clan_lianmeng.class);
    private int _remove = 0;
    private String m = null;

    /**
     *
     */
    private Clan_lianmeng() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Clan_lianmeng();
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
        try {
            //			if (!pc.getMap().chuanyunjian()) {
            //				pc.sendPackets(new S_SystemMessage("此地圖無法使用穿雲箭用" + item.getName()));
            //				return;
            //			}
            if (pc.getClanid() == 0) {
                pc.sendPackets(new S_ServerMessage("\\fY無血盟狀態無法使用"));
                return;
            }
			/*if (pc.getMapId() == L1Config._4971) {
				pc.sendPackets(new S_SystemMessage("很抱歉..此地圖無法使用此功能..請連絡線上GM"));
				return;
			}*/
            boolean error = false;
            if (m != null) {
                String map = m.replaceAll("", "");
                final String[] mapid = map.split(",");
                for (String s : mapid) {
                    int m1 = Integer.parseInt(s);
                    if (pc.getMapId() == m1) {
                        error = true;
                        break;
                    }
                }
            }
            if (error) {
                //pc.sendPackets(new S_SystemMessage("很抱歉..此地圖無法使用此道具"));
                pc.sendPackets(new S_ServerMessage("你發出穿雲箭，但你的所在地圖無法受邀，無法被召喚"));
                return;
            }
            boolean isInWarArea = false;
            final int castleId = L1CastleLocation.getCastleIdByArea(pc);
            if (castleId != 0) {
                isInWarArea = true;
                if (ServerWarExecutor.get().isNowWar(castleId)) {
                    isInWarArea = false; // 戰爭時間中旗內使用可能
                }
            }
            //final short mapId = pc.getMapId();
            if (isInWarArea) {
                // 626 因太遠以致於無法傳送到你要去的地方。
                pc.sendPackets(new S_ServerMessage("你發出穿雲箭，但你的所在地城旗子內，無法被召喚"));
                return;
            }
            L1Clan clan = WorldClan.get().getClan(pc.getClanname());
            final L1Alliance alliance = ClanAllianceReading.get().getAlliance(clan.getClanId());
            if (alliance == null) {
                pc.sendPackets(new S_SystemMessage("您還沒有加入聯盟。"));
                return;
            }
            if (alliance != null) {
                alliance.setTempID(pc.getId());
                alliance.sendPacketsAll("", new S_Message_YN(4976, ""));
                World.get().broadcastPacketToAll(new S_GreenMessage("\\f2玩家\\f3【" + pc.getName() + "\\f3】\\f2對全體聯盟玩家發出一隻穿雲箭,千軍萬馬來相見"));
            }
            pc.sendPackets(new S_ServerMessage("\\fY你已經對所有在線血盟成員發出訊息!"));
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
            m = set[2];
        } catch (Exception e) {
        }
    }
}
