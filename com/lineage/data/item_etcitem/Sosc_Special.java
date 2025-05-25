package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ShowPolyList;
import com.lineage.server.timecontroller.event.ranking.RankingHeroTimer;

public class Sosc_Special extends ItemExecutor {
    public static ItemExecutor get() {
        return new Sosc_Special();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        if (RankingHeroTimer.get_top10().containsValue(pc.getName()) || RankingHeroTimer.get_top3C().containsValue(pc.getName()) || RankingHeroTimer.get_top3K().containsValue(pc.getName()) || RankingHeroTimer.get_top3E().containsValue(pc.getName()) || RankingHeroTimer.get_top3W().containsValue(pc.getName()) || RankingHeroTimer.get_top3D().containsValue(pc.getName()) || RankingHeroTimer.get_top3G().containsValue(pc.getName()) || RankingHeroTimer.get_top3I().containsValue(pc.getName()) || pc.isGm()) {
            if (pc.get_sex() == 0 && pc.isCrown()) {// 王子
                pc.sendPackets(new S_ShowPolyList(pc.getId(), "top10polymp"));
            } else if (pc.get_sex() == 1 && pc.isCrown()) {// 公主
                pc.sendPackets(new S_ShowPolyList(pc.getId(), "top10polyfp"));
            } else if (pc.get_sex() == 0 && pc.isKnight()) {// 男騎士
                pc.sendPackets(new S_ShowPolyList(pc.getId(), "top10polymk"));
            } else if (pc.get_sex() == 1 && pc.isKnight()) {// 女騎士
                pc.sendPackets(new S_ShowPolyList(pc.getId(), "top10polyfk"));
            } else if (pc.get_sex() == 0 && pc.isElf()) {// 男精靈
                pc.sendPackets(new S_ShowPolyList(pc.getId(), "top10polyme"));
            } else if (pc.get_sex() == 1 && pc.isElf()) {// 女精靈
                pc.sendPackets(new S_ShowPolyList(pc.getId(), "top10polyfe"));
            } else if (pc.get_sex() == 0 && pc.isWizard()) {// 男法師
                pc.sendPackets(new S_ShowPolyList(pc.getId(), "top10polymm"));
            } else if (pc.get_sex() == 1 && pc.isWizard()) {// 女法師
                pc.sendPackets(new S_ShowPolyList(pc.getId(), "top10polyfm"));
            } else if (pc.get_sex() == 0 && pc.isDarkelf()) {// 男黑妖
                pc.sendPackets(new S_ShowPolyList(pc.getId(), "top10polymd"));
            } else if (pc.get_sex() == 1 && pc.isDarkelf()) {// 女黑妖
                pc.sendPackets(new S_ShowPolyList(pc.getId(), "top10polyfd"));
            } else if (pc.get_sex() == 0 && pc.isDragonKnight()) {// 男龍騎士
                pc.sendPackets(new S_ShowPolyList(pc.getId(), "top10polymr"));
            } else if (pc.get_sex() == 1 && pc.isDragonKnight()) {// 女龍騎士
                pc.sendPackets(new S_ShowPolyList(pc.getId(), "top10polyfr"));
            } else if (pc.get_sex() == 0 && pc.isIllusionist()) {// 男幻術
                pc.sendPackets(new S_ShowPolyList(pc.getId(), "top10polymi"));
            } else if (pc.get_sex() == 1 && pc.isIllusionist()) {// 女幻術
                pc.sendPackets(new S_ShowPolyList(pc.getId(), "top10polyfi"));
            } else if (pc.get_sex() == 0 && pc.isWarrior()) {// 男戰士
                pc.sendPackets(new S_ShowPolyList(pc.getId(), "top10polymk"));
            } else if (pc.get_sex() == 1 && pc.isWarrior()) {// 女戰士
                pc.sendPackets(new S_ShowPolyList(pc.getId(), "top10polyfk"));
            }
        } else {
            pc.sendPackets(new S_ShowPolyList(pc.getId(), "specialpoly"));
        }
        if (!pc.isItemPoly()) {
            pc.setSummonMonster(false);
            pc.setItemPoly(true);
            pc.setPolyScroll(item);
        }
    }
}