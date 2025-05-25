/**
 * License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE").
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR
 * COPYRIGHT LAW IS PROHIBITED.
 * <p>
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND
 * AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE
 * MAY BE CONSIDERED TO BE A CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 */
package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Clan;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.FaceToFace;
import com.lineage.server.world.WorldClan;
// Referenced classes of package com.lineage.server.clientpackets:
// ClientBasePacket

/**
 * 處理收到由客戶端傳來加入血盟的封包
 */
public class C_JoinClan extends ClientBasePacket {
    private static final String C_JOIN_CLAN = "[C] C_JoinClan";

    public void start(final byte[] decrypt, final ClientExecutor client) {
        L1PcInstance pc = client.getActiveChar();
        if ((pc == null) || pc.isGhost()) {
            return;
        }
        L1PcInstance target = FaceToFace.faceToFace(pc);
        if (target != null) {
            JoinClan(pc, target);
        }
    }

    private void JoinClan(L1PcInstance player, L1PcInstance target) {
        // 如果面對的對象不是王族或守護騎士
        if (!target.isCrown() && (target.getClanRank() != L1Clan.CLAN_RANK_GUARDIAN && target.getClanRank() != L1Clan.CLAN_RANK_LEAGUE_GUARDIAN)) {
            player.sendPackets(new S_ServerMessage(92, target.getName())); // \f1%0。
            return;
        }
        // 如果面對的對象為守護騎士,則不能收其它王族
        if (player.isCrown() && (target.getClanRank() == L1Clan.CLAN_RANK_GUARDIAN || target.getClanRank() == L1Clan.CLAN_RANK_LEAGUE_GUARDIAN)) {
            return;
        }
        // 如果面對的對象為副盟主則不能收其它王族
        if (player.isCrown() && target.getClanRank() == L1Clan.CLAN_RANK_LEAGUE_VICEPRINCE) {
            player.sendPackets(new S_ServerMessage(2504));
            return;
        }
        if (player.getClanid() == target.getClanid()) {
            // 同一血盟
            player.sendPackets(new S_ServerMessage("已經是加入血盟狀態。"));
            return;
        }
        int clan_id = target.getClanid();
        String clan_name = target.getClanname();
        if (clan_id == 0) { // 面對的對象沒有創立血盟
            player.sendPackets(new S_ServerMessage(90, target.getName())); // \f1%0血盟創設狀態。
            return;
        }
        L1Clan clan = WorldClan.get().getClan(clan_name);
        if (clan == null) {
            return;
        }
        if (target.getClanRank() != L1Clan.CLAN_RANK_PRINCE && target.getClanRank() != L1Clan.CLAN_RANK_GUARDIAN && target.getClanRank() != L1Clan.CLAN_RANK_LEAGUE_GUARDIAN && target.getClanRank() != L1Clan.CLAN_RANK_LEAGUE_PRINCE && target.getClanRank() != L1Clan.CLAN_RANK_LEAGUE_VICEPRINCE) {
            // 面對的對象不是盟主或守護騎士
            player.sendPackets(new S_ServerMessage(92, target.getName()));
            return;
        }
        if (player.getClanid() != 0) { // 已經加入血盟
            if (player.isCrown()) { // 自己是盟主
                String player_clan_name = player.getClanname();
                L1Clan player_clan = WorldClan.get().getClan(player_clan_name);
                if (player_clan == null) {
                    return;
                }
                if (player.getId() != player_clan.getLeaderId()) { // 已經加入其他血盟
                    player.sendPackets(new S_ServerMessage(89)); // \f1血盟加入。
                    return;
                }
                if ((player_clan.getCastleId() != 0) || // 有城堡或有血盟小屋
                        (player_clan.getHouseId() != 0)) {
                    player.sendPackets(new S_ServerMessage(665)); // \f1城所有狀態血盟解散。
                    return;
                }
            } else {
                player.sendPackets(new S_ServerMessage(89)); // \f1血盟加入。
                return;
            }
        }
        if (player.getRejoinClanTime() != null && player.getRejoinClanTime().getTime() > System.currentTimeMillis()) {
            // 血盟戰中脫退、%0時間血盟加入。
            int time = (int) (player.getRejoinClanTime().getTime() - System.currentTimeMillis()) / (60 * 60 * 1000);
            player.sendPackets(new S_ServerMessage(1925, time + ""));
            return;
        }
        target.setTempID(player.getId()); // 暫時保存面對的人的ID
        target.sendPackets(new S_Message_YN(97, player.getName())); // %0血盟加入。承諾？（Y/N）
    }

    @Override
    public String getType() {
        return C_JOIN_CLAN;
    }
}
