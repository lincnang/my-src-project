package com.lineage.data.item_etcitem.teleport;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Clan;
import com.lineage.server.serverpackets.S_BlueMessage;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import com.lineage.server.datatables.MapsTable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Clan_Teleprot extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(Clan_Teleprot.class);
    private int _remove = 0;
    private String m = null;

    /**
     *
     */
    private Clan_Teleprot() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Clan_Teleprot();
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
            // 放寬：允許在任何地圖使用穿雲箭
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
            if (clan != null) {
                for (String member : clan.getAllMembers()) {
                    final L1PcInstance clan_pc = World.get().getPlayer(member);
                    if (clan_pc != null) {
                        if (pc.getId() == clan_pc.getId()) {
                            continue;
                        }
                        if (clan_pc != null) {
                            clan_pc.setTempID(pc.getId()); // 暫存盟主ID
                            clan_pc.setCallClan(true);
                            // 729 盟主正在呼喚你，你要接受他的呼喚嗎？(Y/N)
                            clan_pc.sendPackets(new S_Message_YN(729));
                        }
                    }
                }
            }
            // 與隊伍穿雲一致的全服公告樣式
            int mapid = pc.getMapId();
            pc.sendPacketsAll(new com.lineage.server.serverpackets.S_SkillSound(pc.getId(), 729));
            World.get().broadcastPacketToAll(new S_BlueMessage(166,
                    "\\f3 大神 【" + pc.getName() + "】在【" + MapsTable.get().locationname(mapid) +
                    "】. \\f4 使用了一隻穿雲箭！\\f2 千軍萬馬來相見！"));
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
