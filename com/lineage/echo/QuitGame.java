package com.lineage.echo;

import com.add.Tsai.DragonExp;
import com.lineage.server.datatables.GetbackTable;
import com.lineage.server.model.Instance.*;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1Trade;
import com.lineage.server.serverpackets.S_NewMaster;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.templates.L1BookMark;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

public class QuitGame {
    private static final Log _log = LogFactory.getLog(QuitGame.class);

    /**
     * 人物離開遊戲的處理
     *
     */
    public static void quitGame(final L1PcInstance pc) {
        if (pc == null) {
            return;
        }
        if (pc.getOnlineStatus() == 0) {
            return;
        }
        final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
        if (clan != null) {
            if (clan.getWarehouseUsingChar() == pc.getId()) { // 使用血盟倉庫中
                clan.setWarehouseUsingChar(0); // 解除使用狀態
            }
        }
        if (!pc.getPetList().isEmpty()) {
            final Object[] petList = pc.getPetList().values().toArray();
            // 寵物 召喚獸 消除
            if (petList != null) {
                remove_pet(pc, petList);
            }
        }
        try {
            if (!pc.getDolls().isEmpty()) {
                final Object[] dolls = pc.getDolls().values().toArray();
                for (final Object obj : dolls) {
                    final L1DollInstance doll = (L1DollInstance) obj;
                    if (doll != null) {
                        doll.deleteDoll();
                    }
                }
                pc.getDolls().clear();
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        try {
            if (!pc.getDolls2().isEmpty()) {
                final Object[] dolls = pc.getDolls2().values().toArray();
                for (final Object obj : dolls) {
                    final L1DollInstance2 doll = (L1DollInstance2) obj;
                    if (doll != null) {
                        doll.deleteDoll2();
                    }
                }
                pc.getDolls2().clear();
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        try {
            if (pc.get_power_doll() != null) {
                // 超級娃娃收回
                pc.get_power_doll().deleteDoll();
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        try {
            if (!pc.get_otherList().get_illusoryList().isEmpty()) {
                // 分身消除
                final Object[] illList = pc.get_otherList().get_illusoryList().values().toArray();
                for (final Object obj : illList) {
                    final L1IllusoryInstance ill = (L1IllusoryInstance) obj;
                    if (ill != null) {
                        ill.deleteMe();
                    }
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        try {
            // 清空特殊清單全部資料
            pc.get_otherList().clearAll();
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        try {
            // 死亡狀態設置
            if (pc.isDead()) {
                final int[] loc = GetbackTable.GetBack_Location(pc, true);
                pc.setX(loc[0]);
                pc.setY(loc[1]);
                pc.setMap((short) loc[2]);
                pc.setCurrentHp(pc.getLevel());
                if (pc.get_food() > 40) {
                    pc.set_food(40);
                }
                //}
            } else if (pc.getMapId() == (short) 10502) { // 底比斯大戰
                if (pc.isSiege) {
                    if (com.lineage.data.event.MiniGame.MiniSiege.getInstance().running) {
                        switch (pc.getTeam()) {
                            case 0:
                                pc.setX(32771);
                                pc.setY(32815);
                                pc.setMap((short) 10502);
                                break;
                            case 1:
                                pc.setX(32691);
                                pc.setY(32895);
                                pc.setMap((short) 10502);
                                break;
                            case 2:
                                pc.setX(32771);
                                pc.setY(32975);
                                pc.setMap((short) 10502);
                                break;
                        }
                    } else {
                        pc.isSiege = false;
                        pc.setX(33437);
                        pc.setY(32810);
                        pc.setMap((short) 4);
                    }
                } else {
                    pc.setX(33437);
                    pc.setY(32810);
                    pc.setMap((short) 4);
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        try {
            // 交易終止
            if (pc.getTradeID() != 0) {
                final L1Trade trade = new L1Trade();
                trade.tradeCancel(pc);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        try {
            // 決鬥終止
            if (pc.getFightId() != 0) {
                pc.setFightId(0);
                final L1PcInstance fightPc = (L1PcInstance) World.get().findObject(pc.getFightId());
                if (fightPc != null) {
                    fightPc.setFightId(0);
                    fightPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, 0, 0));
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        try {
            // 移出隊伍
            if (pc.isInParty()) {
                pc.getParty().leaveMember(pc);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        try {
            // 移出聊天隊伍
            if (pc.isInChatParty()) {
                pc.getChatParty().leaveMember(pc);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        try {
            if (!pc.getFollowerList().isEmpty()) {
                // 跟隨者 主人離線 重新召喚
                final Object[] followerList = pc.getFollowerList().values().toArray();
                for (final Object obj : followerList) {
                    final L1FollowerInstance follower = (L1FollowerInstance) obj;
                    follower.setParalyzed(true);
                    follower.spawn(follower.getNpcTemplate().get_npcId(), follower.getX(), follower.getY(), follower.getHeading(), follower.getMapId());
                    follower.deleteMe();
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        try {
            // 移出各種處理清單
            pc.stopEtcMonitor();
            DragonExp.get().addStoreExp(pc);
            // _log.error("人物離開遊戲的處理-移出各種處理清單");
            // 解除登入狀態
            pc.setOnlineStatus(0);
            // _log.error("人物離開遊戲的處理-解除登入狀態");
            // 資料紀錄
            pc.save();
            // _log.error("人物離開遊戲的處理-資料紀錄");
            // 背包紀錄前，回收絲莉安啟動道具
            try {
                com.add.Tsai.Astrology.SilianAstrologyTable.get().revokeGrantItems(pc, 0);
            } catch (final Exception e) {
                _log.error(e.getLocalizedMessage(), e);
            }
            // 背包紀錄
            pc.saveInventory();
            // _log.error("人物離開遊戲的處理-背包紀錄");
            L1BookMark.WriteBookmark(pc); // 日版記憶座標
            // 人物登出前：登出清理掛鉤
            com.lineage.server.model.Instance.L1PcInstanceCleanupIntegration.onPlayerLogout(pc);
            // 人物登出
            pc.logout();
            // _log.error("人物離開遊戲的處理-人物登出");
            if (pc.getSkins() != null) {
                Map<Integer, L1SkinInstance> skinList = pc.getSkins();
                for (Integer gfxid : skinList.keySet()) {
                    pc.getSkin(gfxid).deleteMe();
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private static void remove_pet(final L1PcInstance pc, final Object[] petList) {
        try {
            for (final Object obj : petList) {
                final L1NpcInstance petObject = (L1NpcInstance) obj;
                if (petObject != null) {
                    if (petObject instanceof L1PetInstance) {
                        final L1PetInstance pet = (L1PetInstance) petObject;
                        // pet.dropItem(); // 2012-07-30 DEXC 變更人物離線寵物物品回到人物背包
                        pet.collect(true);
                        pc.removePet(pet);
                        pet.deleteMe();
                    }
                    if (petObject instanceof L1SummonInstance) {
                        final L1SummonInstance summon = (L1SummonInstance) petObject;
                        new S_NewMaster(summon);
                        if (summon != null) {
                            if (summon.destroyed()) {
                                return;
                            }
                            // 更新 2015/06/30 修正人物離線召喚獸移除
                            if (summon.tamed()) {
                                // 召喚獸解放
                                summon.liberate();
                            } else {
                                // 解散
                                summon.Death(null);
                            }
                        }
                    }
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
