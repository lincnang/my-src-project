/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 要求驅逐人物離開隊伍
 *
 * @author admin
 */
public class C_BanParty extends ClientBasePacket {
    @Override
    public void start(final byte[] decrypt, final ClientExecutor client) {
        try {
            // 資料載入
            this.read(decrypt);
            L1PcInstance pc = client.getActiveChar();
            final String s = readS();
            final L1PcInstance player = client.getActiveChar();
            if (!player.getParty().isLeader(player)) {
                // 場合
                player.sendPackets(new S_ServerMessage(427)); // 追放。
                return;
            }
            while ((pc.getMapId() == 9000) || (pc.getMapId() == 9101)) {
                return;
            }
            for (L1PcInstance member : player.getParty().getMembers()) {
                if (member.getName().toLowerCase().equals(s.toLowerCase())) {
                    player.getParty().kickMember(member);
                    return;
                }
            }
            // 見
            player.sendPackets(new S_ServerMessage(426, s)); // %0。
        } catch (final Exception e) {
            // _log.error(e.getLocalizedMessage(), e);
        } finally {
            this.over();
        }
    }

    @Override
    public String getType() {
        return "[C] " + this.getClass().getSimpleName();
    }
}
