package com.lineage.data.npc.quest;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.timecontroller.event.ranking.RankingHeroTimer;

/**
 * 英雄風雲榜 80029 UPDATE `npc` SET `nameid`='英雄風雲榜' WHERE `npcid`='80029';#
 *
 * @author dexc
 */
public class Npc_HeroRanking extends NpcExecutor {//src013

    /**
     * 英雄風雲榜
     */
    private Npc_HeroRanking() {
        // TODO Auto-generated constructor stub
    }

    public static NpcExecutor get() {
        return new Npc_HeroRanking();
    }

    @Override
    public int type() {
        return 3;
    }

    @Override
    public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_h_1"));
    }

    @Override
    public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
        final String[] userName = new String[11];
        switch (cmd) {
            case "c": {// 王族風雲榜
                String[] names = RankingHeroTimer.userNameC();
                System.arraycopy(names, 0, userName, 0, names.length);
                userName[10] = "王族";
                break;
            }
            case "k": {// 騎士風雲榜
                final String[] names = RankingHeroTimer.userNameK();
                System.arraycopy(names, 0, userName, 0, names.length);
                userName[10] = "騎士";
                break;
            }
            case "e": {// 精靈風雲榜
                final String[] names = RankingHeroTimer.userNameE();
                System.arraycopy(names, 0, userName, 0, names.length);
                userName[10] = "精靈";
                break;
            }
            case "w": {// 法師風雲榜
                final String[] names = RankingHeroTimer.userNameW();
                System.arraycopy(names, 0, userName, 0, names.length);
                userName[10] = "法師";
                break;
            }
            case "d": {// 黑妖風雲榜
                final String[] names = RankingHeroTimer.userNameD();
                System.arraycopy(names, 0, userName, 0, names.length);
                userName[10] = "黑暗精靈";
                break;
            }
            case "g": {// 龍騎風雲榜
                final String[] names = RankingHeroTimer.userNameG();
                System.arraycopy(names, 0, userName, 0, names.length);
                userName[10] = "龍騎士";
                break;
            }
            case "i": {// 幻術風雲榜
                final String[] names = RankingHeroTimer.userNameI();
                System.arraycopy(names, 0, userName, 0, names.length);
                userName[10] = "幻術師";
                break;
            }
            case "warrior": {// 幻術風雲榜
                final String[] names = RankingHeroTimer.userNameWarrior();
                System.arraycopy(names, 0, userName, 0, names.length);
                userName[10] = "戰士";
                break;
            }
            case "a": {// 全職業風雲榜
                final String[] names = RankingHeroTimer.userNameAll();
                System.arraycopy(names, 0, userName, 0, names.length);
                userName[10] = "全職業";
                break;
            }
        }
        if (userName != null) {
            final String htmlid = "y_h_2";
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), htmlid, userName));
        }
    }
}
