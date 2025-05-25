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

import com.lineage.config.ConfigOther;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Clan;
import com.lineage.server.serverpackets.S_CharTitle;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;

import java.util.logging.Level;
import java.util.logging.Logger;
// Referenced classes of package com.lineage.server.clientpackets:
// ClientBasePacket

/**
 * 處理收到由客戶端傳來變更稱號的封包
 */
public class C_Title extends ClientBasePacket {
    private static final String C_TITLE = "[C] C_Title";
    private static Logger _log = Logger.getLogger(C_Title.class.getName());

    @Override
    public void start(final byte[] decrypt, final ClientExecutor client) {
        // 資料載入
        this.read(decrypt);
        L1PcInstance pc = client.getActiveChar();
        if (pc == null) {
            return;
        }
        String charName = readS();
        String title = readS();
        if (charName.isEmpty() || title.isEmpty()) {
            // \f1次入力：「/title \f0名 呼稱\f1」
            pc.sendPackets(new S_ServerMessage(196));
            return;
        }
        L1PcInstance target = World.get().getPlayer(charName);
        if (target == null) {
            return;
        }
        if (pc.isGm()) {
            changeTitle(target, title);
            return;
        }
        if (isClanLeader(pc)) { // 血盟主
            if (pc.getId() == target.getId()) { // 自己
                if (pc.getLevel() < 10) {
                    // \f1血盟員場合、呼稱持10以上。
                    pc.sendPackets(new S_ServerMessage(197));
                    return;
                }
                changeTitle(pc, title);
            } else { // 他人
                if (pc.getClanid() != target.getClanid()) {
                    // \f1%0%d不是你的血盟成員。
                    pc.sendPackets(new S_ServerMessage(201));
                    return;
                }
                if (target.getLevel() < 10) {
                    // \f1%010未滿呼稱與。
                    pc.sendPackets(new S_ServerMessage(202, charName));
                    return;
                }
                changeTitle(target, title);
                L1Clan clan = WorldClan.get().getClan(pc.getClanname());
                if (clan != null) {
                    for (L1PcInstance clanPc : clan.getOnlineClanMember()) {
                        // \f1%0%1「%2」呼稱與。
                        clanPc.sendPackets(new S_ServerMessage(203, pc.getName(), charName, title));
                    }
                }
            }
        } else {
            if (pc.getId() == target.getId()) { // 自分
                if (pc.getClanid() != 0 && !ConfigOther.CLANTITLE) {
                    // \f1血盟員呼稱與。
                    pc.sendPackets(new S_ServerMessage(198));
                    return;
                }
                if (target.getLevel() < 40) {
                    // \f1血盟員呼稱持、40以上。
                    pc.sendPackets(new S_ServerMessage(200));
                    return;
                }
                changeTitle(pc, title);
            } else { // 他人
                if (pc.isCrown()) { // 連合所屬君主
                    if (pc.getClanid() == target.getClanid()) {
                        if (target.getClanRank() == L1Clan.CLAN_RANK_PRINCE || target.getClanRank() == L1Clan.CLAN_RANK_LEAGUE_PRINCE || target.getClanRank() == L1Clan.CLAN_RANK_LEAGUE_VICEPRINCE) {
                            // 只能對比自己低階級才能給予稱號.
                            pc.sendPackets(new S_ServerMessage(2065));
                            return;
                        }
                        changeTitle(target, title);
                        L1Clan clan = WorldClan.get().getClan(pc.getClanname());
                        if (clan != null) {
                            for (L1PcInstance clanPc : clan.getOnlineClanMember()) {
                                // \f1%0%1「%2」呼稱與。
                                clanPc.sendPackets(new S_ServerMessage(203, pc.getName(), charName, title));
                            }
                        }
                    }
                } else {
                    if (pc.getClanid() == target.getClanid()) {
                        if (pc.getClanRank() == L1Clan.CLAN_RANK_GUARDIAN || pc.getClanRank() == L1Clan.CLAN_RANK_LEAGUE_GUARDIAN) {
                            pc.sendPackets(new S_ServerMessage(2474));
                        } else {
                            pc.sendPackets(new S_ServerMessage(2143));
                        }
                        return;
                    }
                }
            }
        }
    }

    private void changeTitle(L1PcInstance pc, String title) {
        int objectId = pc.getId();
        pc.setTitle(title);
        pc.sendPackets(new S_CharTitle(objectId, title));
        pc.broadcastPacketAll(new S_CharTitle(objectId, title));
        try {
            pc.save(); // 儲存玩家的資料到資料庫中
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    private boolean isClanLeader(L1PcInstance pc) {
        boolean isClanLeader = false;
        if (pc.getClanid() != 0) { // 有血盟
            L1Clan clan = WorldClan.get().getClan(pc.getClanname());
            if (clan != null) {
                if (pc.isCrown() && pc.getId() == clan.getLeaderId()) { // 君主、、血盟主
                    isClanLeader = true;
                }
            }
        }
        return isClanLeader;
    }

    @Override
    public String getType() {
        return C_TITLE;
    }
}
