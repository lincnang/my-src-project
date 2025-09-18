/**
 * License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE").
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR
 * COPYRIGHT LAW IS PROHIBITED.
 */
package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Clan;
import com.lineage.server.world.WorldClan;

import java.util.Collection;
import java.util.Iterator;

/**
 * 血盟注視清單（開啟後客戶端會顯示所有血盟徽章）
 */
public class S_ClanMarkSee extends ServerBasePacket {

    private static final String S_ClanMarkSee = "[S] S_ClanMarkSee";

    /**
     * 依血盟自訂注視清單
     * @param clan 血盟
     * @param type 0:加入清單 1:移出清單 2:載入清單
     */
    public S_ClanMarkSee(L1Clan clan, int type) {
        if (clan == null) {
            return;
        }
        buildPacket(clan, type);
    }

    /**
     * 開啟血盟注視（type=2: 全部血盟）
     */
    public S_ClanMarkSee(int type) {
        buildPacketAll(type);
    }

    /**
     * 關閉血盟注視
     */
    public S_ClanMarkSee() {
        writeC(117); // S_PLEDGE_WATCH
        writeH(2);
        writeD(0);
    }

    private void buildPacketAll(int type) {
        writeC(117); // S_PLEDGE_WATCH
        writeH(type);
        if (type == 2) {
            // 所有人血盟注視：送出全部血盟名稱
            final Collection<L1Clan> allClans = WorldClan.get().getAllClans();
            int size = allClans.size();
            writeD(size);
            for (final Iterator<L1Clan> iter = allClans.iterator(); iter.hasNext(); ) {
                final L1Clan clan = iter.next();
                writeS(clan.getClanName());
            }
        }
        writeH(0);
    }

    private void buildPacket(L1Clan clan, int type) {
        writeC(117); // S_PLEDGE_WATCH
        writeH(type);
        // 現行專案未使用個別清單，統一寫入 0
        writeD(0x00);
        writeH(0);
    }

    @Override
    public byte[] getContent() {
        return getBytes();
    }

    @Override
    public String getType() {
        return S_ClanMarkSee;
    }
}


