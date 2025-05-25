package com.lineage.server.clientpackets;

import com.lineage.config.ConfigAlt;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.GetbackTable;
import com.lineage.server.datatables.lock.ClanEmblemReading;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.*;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1EmblemIcon;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import com.lineage.server.world.WorldWar;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 要求宣戰/投降/休戰
 *
 * @author daien
 */
public class C_War extends ClientBasePacket {
    private static final Log _log = LogFactory.getLog(C_War.class);

    /*
     * public C_War() { }
     *
     * public C_War(final byte[] abyte0, final ClientExecutor client) {
     * super(abyte0); try { this.start(abyte0, client);
     *
     * } catch (final Exception e) { _log.error(e.getLocalizedMessage(), e); } }
     */
    @Override
    public void start(final byte[] decrypt, final ClientExecutor client) {
        try {
            // 資料載入
            this.read(decrypt);
            final L1PcInstance player = client.getActiveChar();
            final String playerName = player.getName();
            final String clanName = player.getClanname();
            final int clanId = player.getClanid();
            if (!player.isCrown()) { // 君主以外
                player.sendPackets(new S_ServerMessage(478)); // 478:\f1只有王子和公主才能宣戰。
                return;
            }
            if (clanId == 0) { // 沒有血盟
                player.sendPackets(new S_ServerMessage(272)); // 272:\f1若想要戰爭，首先必須創立血盟。
                return;
            }
            final L1Clan clan = WorldClan.get().getClan(clanName);
            if (clan == null) { // 沒有血盟
                return;
            }
            if (player.getId() != clan.getLeaderId()) { // 血盟主
                player.sendPackets(new S_ServerMessage(478)); // 478:\f1只有王子和公主才能宣戰。
                return;
            }
            final int type = this.readC();// 模式
            final String tgname = this.readS();
            if (clanName.toLowerCase().equals(tgname.toLowerCase())) { // 對象是自己
                return;
            }
            L1Clan enemyClan = null;// 目標血盟
            String enemyClanName = null;// 目標血盟名稱
            final Collection<L1Clan> allClans = WorldClan.get().getAllClans();
            for (final Iterator<L1Clan> iter = allClans.iterator(); iter.hasNext(); ) {
                final L1Clan checkClan = iter.next();
                if (checkClan.getClanName().toLowerCase().equals(tgname.toLowerCase())) {
                    enemyClan = checkClan;
                    enemyClanName = checkClan.getClanName();
                    break;
                }
            }
            if (enemyClan == null) { // 對手為空
                if (tgname.equals(" ")) {
                    war_castle(player, clan, null, type);
                }
                return;
            }
            if (clan.getAlliance(enemyClan.getClanId()) == enemyClan) {
                S_ServerMessage sm = new S_ServerMessage(1205);
                player.sendPackets(sm);
                return;
            }
            boolean inWar = false;
            final List<L1War> warList = WorldWar.get().getWarList(); // 全部戰爭清單
            if (enemyClan.getCastleId() == 0) {// 對方不是城盟
                for (final L1War war : warList) {
                    if (war.checkClanInWar(clanName)) { // 戰爭中
                        if (type == 0) { // 宣戰佈告
                            player.sendPackets(new S_ServerMessage(234)); // 234:\f1你的血盟已經在戰爭中。
                            return;
                        }
                        inWar = true;
                        break;
                    }
                }
            }
            if (!inWar && ((type == 2) || (type == 3))) { // 非戰爭中 投降、結束
                return;
            }
            if (clan.getCastleId() != 0) { // 已經是城盟
                if (type == 0) { // 宣戰佈告
                    player.sendPackets(new S_ServerMessage(474)); // 474:你已經擁有城堡，無法再擁有其他城堡。
                    return;
                } else if ((type == 2) || (type == 3)) { // 城盟不可宣告投降
                    return;
                }
            }
            if ((enemyClan.getCastleId() == 0) && // 對像不為城盟 王族等級小於25
                    (player.getLevel() <= 25)) {
                player.sendPackets(new S_ServerMessage(232)); // 232:\f1等級25以下的君主無法宣戰。
                return;
            }
            if (enemyClan.getCastleId() != 0) {// 對城堡宣戰
                war_castle(player, clan, enemyClan, type);
            } else { // XXX 對血盟宣戰
                // 檢查城堡戰爭狀態
                if (ServerWarExecutor.get().checkCastleWar() > 0) {
                    // 18:攻城戰期間，暫停血盟宣戰。
                    player.sendPackets(new S_HelpMessage("\\fS攻城戰期間，暫停血盟宣戰。"));
                    return;
                }
                boolean enemyInWar = false;
                for (final L1War war : warList) {
                    if (war.checkClanInWar(enemyClanName)) { // 對方戰爭中
                        switch (type) {
                            case 0: // 宣戰佈告
                                // 236 %0 血盟拒絕你的宣戰。
                                player.sendPackets(new S_ServerMessage(236, enemyClanName));
                                return;
                            case 2:// 投降
                            case 3:// 結束
                                if (!war.checkClanInSameWar(clanName, enemyClanName)) { // 自相手別戰爭
                                    return;
                                }
                                break;
                        }
                        enemyInWar = true;
                        break;
                    }
                }
                if (!enemyInWar && ((type == 2) || (type == 3))) { // 相手戰爭中以外、投降結束
                    return;
                }
                // 取回對方盟主資料
                final L1PcInstance enemyLeader = World.get().getPlayer(enemyClan.getLeaderName());
                if (enemyLeader == null) {
                    // 218：\f1%0 血盟君主不在線上。
                    player.sendPackets(new S_ServerMessage(218, enemyClanName));
                    return;
                }
                switch (type) {
                    case 0: // 宣戰佈告
                        enemyLeader.setTempID(player.getId()); // 保存對手ID
                        // 217:%0 血盟向你的血盟宣戰。是否接受？(Y/N)
                        enemyLeader.sendPackets(new S_Message_YN(217, clanName, playerName));
                        break;
                    case 2: // 投降
                        enemyLeader.setTempID(player.getId()); // 保存對手ID
                        // 221:%0 血盟要向你投降。是否接受？(Y/N)
                        enemyLeader.sendPackets(new S_Message_YN(221, clanName));
                        break;
                    case 3: // 結束
                        enemyLeader.setTempID(player.getId()); // 保存對手ID
                        // 222:%0 血盟要結束戰爭。是否接受？(Y/N)
                        enemyLeader.sendPackets(new S_Message_YN(222, clanName));
                        break;
                }
            }
        } catch (final Exception e) {
            // _log.error(e.getLocalizedMessage(), e);
        } finally {
            this.over();
        }
    }

    /**
     * 對城宣戰
     *
     * @param type      0:宣戰 2:投降 3:終止
     */
    private void war_castle(final L1PcInstance player, final L1Clan clan, final L1Clan enemyClan, final int type) {
        try {
            if (player.getLevel() < ConfigAlt.clanforwarlv) {
                // 若要攻城，君主的等級得２５以上。
                player.sendPackets(new S_ServerMessage("等級需達" + ConfigAlt.clanforwarlv + "以上才可宣戰。"));
                return;
            }
            L1EmblemIcon emblemIcon = ClanEmblemReading.get().get(clan.getClanId());
            if ((emblemIcon == null) && (!player.isGm())) {
                player.sendPackets(new S_ServerMessage(812));
                return;
            }
            String clanName = player.getClanname();
            if (enemyClan == null) {
                L1Object object = World.get().findObject(player.getTempID());
                if ((object instanceof L1NpcInstance)) {
                    int id = L1CastleLocation.getCastleIdByArea((L1NpcInstance) object);
                    if (id == 0) {
                        player.sendPackets(new S_ServerMessage(79));
                        return;
                    }
                    if (ServerWarExecutor.get().isNowWar(id)) {
                        L1PcInstance[] clanMember = clan.getOnlineClanMember();
                        boolean haswar = false;
                        for (int k = 0; k < clanMember.length; k++) {
                            if (L1CastleLocation.checkInWarArea(id, clanMember[k])) {
                                L1PcInstance clanPc = clanMember[k];
                                if (clanPc != null) {
                                    haswar = true;
                                    player.sendPackets(new S_ServerMessage("血盟成員[" + clanPc.getName() + "]站在攻城旗幟內。"));
                                    int[] loc = GetbackTable.GetBack_Location(clanPc, true);
                                    L1Teleport.teleport(clanPc, loc[0], loc[1], (short) loc[2], 5, true);
                                }
                            }
                        }
                        if (haswar) {
                            player.sendPackets(new S_ServerMessage("系統已將旗幟內的玩家傳送回城。"));
                        }
                        for (int k = 0; k < clanMember.length; k++) {
                            clanMember[k].sendPackets(new S_War(1, clanName, "NPC"));
                        }
                        player.sendPackets(new S_CloseList(player.getId()));
                    } else {
                        if (type == 0) {
                            player.sendPackets(new S_ServerMessage(476));
                        }
                        player.sendPackets(new S_CloseList(player.getId()));
                    }
                }
                return;
            }
            String enemyClanName = enemyClan.getClanName();
            if (WorldWar.get().isWar(clan.getClanName(), enemyClanName)) {
                player.sendPackets(new S_ServerMessage(234));
                return;
            }
            int castle_id = enemyClan.getCastleId();
            if (ServerWarExecutor.get().isNowWar(castle_id)) {
                L1PcInstance[] clanMember = clan.getOnlineClanMember();
                boolean haswar = false;
                for (int k = 0; k < clanMember.length; k++) {
                    if (L1CastleLocation.checkInWarArea(castle_id, clanMember[k])) {
                        L1PcInstance clanPc = clanMember[k];
                        if (clanPc != null) {
                            haswar = true;
                            player.sendPackets(new S_ServerMessage("血盟成員[" + clanPc.getName() + "]站在攻城旗幟內。"));
                            int[] loc = GetbackTable.GetBack_Location(clanPc, true);
                            L1Teleport.teleport(clanPc, loc[0], loc[1], (short) loc[2], 5, true);
                        }
                    }
                }
                if (haswar) {
                    player.sendPackets(new S_ServerMessage("系統已將旗幟內的玩家傳送回城。"));
                }
                List<L1War> warList = WorldWar.get().getWarList();
                boolean enemyInWar = false;
                for (L1War war : warList) {
                    if (war.checkClanInWar(enemyClanName)) {
                        if (type == 0) {
                            war.declareWar(clanName, enemyClanName);
                            war.addAttackClan(clanName);
                            player.sendPackets(new S_CloseList(player.getId()));
                        } else if ((type == 2) || (type == 3)) {
                            if (!war.checkClanInSameWar(clanName, enemyClanName)) {
                                return;
                            }
                            if (type == 2) {
                                war.surrenderWar(clanName, enemyClanName);
                            } else if (type == 3) {
                                war.ceaseWar(clanName, enemyClanName);
                            }
                        }
                        enemyInWar = true;
                        break;
                    }
                }
                if ((!enemyInWar) && (type == 0)) {
                    L1War war = new L1War();
                    war.handleCommands(1, clanName, enemyClanName);
                    player.sendPackets(new S_CloseList(player.getId()));
                    // C_ClanAttention.delClanAttention(clan, enemyClan);
                }
            } else {
                if (type == 0) {
                    player.sendPackets(new S_ServerMessage(476));
                }
                player.sendPackets(new S_CloseList(player.getId()));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
