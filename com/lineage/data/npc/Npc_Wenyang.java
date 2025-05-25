package com.lineage.data.npc;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.Manly.L1WenYang;
import com.lineage.server.Manly.L1WenYangJiLu;
import com.lineage.server.Manly.WenYangJiLuTable;
import com.lineage.server.Manly.WenYangTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 紋樣系統
 * 大陸Manly製作
 *
 * @author Administrator
 */
public class Npc_Wenyang extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Wenyang.class);
    Random _Random = new Random();

    private Npc_Wenyang() {
        // TODO Auto-generated constructor stub
    }

    public static NpcExecutor get() {
        return new Npc_Wenyang();
    }

    @Override
    public int type() {
        return 3;
    }

    @Override
    public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
        String[] data = new String[6];
        if (pc.getWyType1() == 1 && pc.getWyLevel1() > 0) {
            L1WenYang wenYang = WenYangTable.getInstance().getTemplate(pc.getWyType1(), pc.getWyLevel1());
            if (wenYang != null) {
                data[0] = String.valueOf("目前【" + wenYang.getNot() + "】紋樣等級為【" + pc.getWyLevel1() + "】");
            }
        } else {
            data[0] = "還未獲得紋樣";
        }
        if (pc.getWyType2() == 2 && pc.getWyLevel2() > 0) {
            L1WenYang wenYang = WenYangTable.getInstance().getTemplate(pc.getWyType2(), pc.getWyLevel2());
            if (wenYang != null) {
                data[1] = String.valueOf("目前【" + wenYang.getNot() + "】紋樣等級為【" + pc.getWyLevel2() + "】");
            }
        } else {
            data[1] = "還未獲得紋樣";
        }
        if (pc.getWyType3() == 3 && pc.getWyLevel3() > 0) {
            L1WenYang wenYang = WenYangTable.getInstance().getTemplate(pc.getWyType3(), pc.getWyLevel3());
            if (wenYang != null) {
                data[2] = String.valueOf("目前【" + wenYang.getNot() + "】紋樣等級為【" + pc.getWyLevel3() + "】");
            }
        } else {
            data[2] = "還未獲得紋樣";
        }
        if (pc.getWyType4() == 4 && pc.getWyLevel4() > 0) {
            L1WenYang wenYang = WenYangTable.getInstance().getTemplate(pc.getWyType4(), pc.getWyLevel4());
            if (wenYang != null) {
                data[3] = String.valueOf("目前【" + wenYang.getNot() + "】紋樣等級為【" + pc.getWyLevel4() + "】");
            }
        } else {
            data[3] = "還未獲得紋樣";
        }
        if (pc.getWyType5() == 5 && pc.getWyLevel5() > 0) {
            L1WenYang wenYang = WenYangTable.getInstance().getTemplate(pc.getWyType5(), pc.getWyLevel5());
            if (wenYang != null) {
                data[4] = String.valueOf("目前【" + wenYang.getNot() + "】紋樣等級為【" + pc.getWyLevel5() + "】");
            }
        } else {
            data[4] = "還未獲得紋樣";
        }
        if (pc.getWyType6() == 6 && pc.getWyLevel5() > 0) {
            L1WenYang wenYang = WenYangTable.getInstance().getTemplate(pc.getWyType6(), pc.getWyLevel6());
            if (wenYang != null) {
                data[5] = String.valueOf("目前【" + wenYang.getNot() + "】紋樣等級為【" + pc.getWyLevel6() + "】");
            }
        } else {
            data[5] = "還未獲得紋樣";
        }
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "wenyang_00", data));
    }

    private void addLog(final L1PcInstance pc, final int type, final int level) {
        L1WenYangJiLu wenYangJiLu = new L1WenYangJiLu();
        wenYangJiLu.setPcid(pc.getId());
        wenYangJiLu.setType(type);
        wenYangJiLu.setLevel(level);
        WenYangJiLuTable.getInstance().storeItem(pc, wenYangJiLu);
    }

    private String[] getInfo(final L1PcInstance pc, L1WenYang wenYang, int rate, int level) {
        int cost = wenYang.getCost();
        if (rate >= wenYang.getRate()) {
            cost += ((rate - wenYang.getRate()) / 10) * wenYang.getCostUp();
        }
        String[] data = new String[4];
        data[0] = String.valueOf(level);
        data[1] = String.valueOf(pc.getWenyangJiFen());
        data[2] = String.valueOf(rate < wenYang.getRate() ? wenYang.getRate() : rate);
        data[3] = String.valueOf(cost);
        return data;
    }

    @Override
    public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
        try {
            L1WenYang wenYang1 = WenYangTable.getInstance().getTemplate(pc.getWyType1(), pc.getWyLevel1() == 0 ? 1 : pc.getWyLevel1());
            if (wenYang1 == null) {
                return;
            }
            L1WenYang wenYang2 = WenYangTable.getInstance().getTemplate(pc.getWyType2(), pc.getWyLevel2() == 0 ? 1 : pc.getWyLevel2());
            if (wenYang2 == null) {
                return;
            }
            L1WenYang wenYang3 = WenYangTable.getInstance().getTemplate(pc.getWyType3(), pc.getWyLevel3() == 0 ? 1 : pc.getWyLevel3());
            if (wenYang3 == null) {
                return;
            }
            L1WenYang wenYang4 = WenYangTable.getInstance().getTemplate(pc.getWyType4(), pc.getWyLevel4() == 0 ? 1 : pc.getWyLevel4());
            if (wenYang4 == null) {
                return;
            }
            L1WenYang wenYang5 = WenYangTable.getInstance().getTemplate(pc.getWyType5(), pc.getWyLevel5() == 0 ? 1 : pc.getWyLevel5());
            if (wenYang5 == null) {
                return;
            }
            L1WenYang wenYang6 = WenYangTable.getInstance().getTemplate(pc.getWyType6(), pc.getWyLevel6() == 0 ? 1 : pc.getWyLevel6());
            if (wenYang6 == null) {
                return;
            }
            if (cmd.equals("wenyang_01")) {
                String[] data = getInfo(pc, wenYang1, pc.getWenyangRate1(), pc.getWyLevel1());
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), getHtmlId(pc.getWyType1(), pc.getWyLevel1()), data));
                return;
            }
            if (cmd.equals("a1-1-up")) {
                int rate = pc.getWenyangRate1();
                if (rate < wenYang1.getRate()) {
                    rate = wenYang1.getRate();
                }
                if ((rate + 10) > wenYang1.getMaxRate()) {
                    return;
                }
                pc.setWenyangRate1(rate + 10);
                String[] data = getInfo(pc, wenYang1, pc.getWenyangRate1(), pc.getWyLevel1());
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), getHtmlId(pc.getWyType1(), pc.getWyLevel1()), data));
                return;
            }
            if (cmd.equals("a1-1-dn")) {
                int rate = pc.getWenyangRate1();
                if (rate <= wenYang1.getRate()) {
                    rate = wenYang1.getRate();
                }
                if ((rate - 10) < wenYang1.getRate()) {
                    return;
                }
                pc.setWenyangRate1(rate - 10);
                String[] data = getInfo(pc, wenYang1, pc.getWenyangRate1(), pc.getWyLevel1());
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), getHtmlId(pc.getWyType1(), pc.getWyLevel1()), data));
                return;
            }
            if (cmd.equals("a1-1")) {
                int cost = wenYang1.getCost();
                if (pc.getWenyangRate1() >= wenYang1.getRate()) {
                    cost += ((pc.getWenyangRate1() - wenYang1.getRate()) / 10) * wenYang1.getCostUp();
                }
                if (pc.getWenyangJiFen() < cost) {
                    pc.sendPackets(new S_SystemMessage("您的紋樣積分不足"));
                    return;
                }
                if (pc.getWyLevel1() >= 30) {
                    pc.sendPackets(new S_SystemMessage("伺服器最高紋樣等級為30等"));
                    return;
                }
                // 播放動畫
                playAnimation(pc, npc, pc.getWyType1(), wenYang1, pc.getWenyangRate1());
                if (ThreadLocalRandom.current().nextInt(100) < wenYang1.getRate()) {
                    pc.setWenyangJiFen(pc.getWenyangJiFen() - cost);
                    pc.setWyType1(1);
                    pc.setWyLevel1(pc.getWyLevel1() + 1);
                    pc.sendPackets(new S_SystemMessage("恭喜升級成功,重登後獲得紋樣屬性"));
                    pc.save();
                    addLog(pc, pc.getWyType1(), pc.getWyLevel1());
                } else {
                    pc.setWenyangJiFen(pc.getWenyangJiFen() - cost);
                    pc.sendPackets(new S_SystemMessage("很遺憾升級失敗"));
                    pc.save();
                }
                // 顯示最後狀態
                finishAnimation(pc, npc, pc.getWyType1(), wenYang1, pc.getWenyangRate1(), pc.getWyLevel1());
            }
            ///////////////////////////////////////////////////////////////////////////////////////////
            if (cmd.equals("wenyang02_01")) {
                String[] data = getInfo(pc, wenYang2, pc.getWenyangRate2(), pc.getWyLevel2());
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), getHtmlId(pc.getWyType2(), pc.getWyLevel2()), data));
                return;
            }
            if (cmd.equals("a2-1-up")) {
                int rate = pc.getWenyangRate2();
                if (rate < wenYang2.getRate()) {
                    rate = wenYang2.getRate();
                }
                if ((rate + 10) > wenYang2.getMaxRate()) {
                    return;
                }
                pc.setWenyangRate2(rate + 10);
                String[] data = getInfo(pc, wenYang2, pc.getWenyangRate2(), pc.getWyLevel2());
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), getHtmlId(pc.getWyType2(), pc.getWyLevel2()), data));
                return;
            }
            if (cmd.equals("a2-1-dn")) {
                int rate = pc.getWenyangRate2();
                if (rate <= wenYang2.getRate()) {
                    rate = wenYang2.getRate();
                }
                if ((rate - 10) < wenYang2.getRate()) {
                    return;
                }
                pc.setWenyangRate2(rate - 10);
                String[] data = getInfo(pc, wenYang2, pc.getWenyangRate2(), pc.getWyLevel2());
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), getHtmlId(pc.getWyType2(), pc.getWyLevel2()), data));
                return;
            }
            if (cmd.equals("a2-1")) {
                if (pc.getWenyangJiFen() < wenYang2.getCost()) {
                    pc.sendPackets(new S_SystemMessage("您的紋樣積分不足"));
                    return;
                }
                if (pc.getWyLevel2() >= 30) {
                    pc.sendPackets(new S_SystemMessage("伺服器最高紋樣等級為30等"));
                    return;
                }
                // 播放動畫
                playAnimation(pc, npc, pc.getWyType2(), wenYang2, pc.getWenyangRate2());
                if (ThreadLocalRandom.current().nextInt(100) < wenYang2.getRate()) {
                    pc.setWenyangJiFen(pc.getWenyangJiFen() - wenYang2.getCost());
                    pc.setWyType2(2);
                    pc.setWyLevel2(pc.getWyLevel2() + 1);
                    pc.sendPackets(new S_SystemMessage("恭喜升級成功,重登後獲得紋樣屬性"));
                    pc.save();
                    addLog(pc, pc.getWyType2(), pc.getWyLevel2());
                } else {
                    pc.setWenyangJiFen(pc.getWenyangJiFen() - wenYang2.getCost());
                    pc.sendPackets(new S_SystemMessage("很遺憾升級失敗"));
                    pc.save();
                }
                // 顯示最後狀態
                finishAnimation(pc, npc, pc.getWyType2(), wenYang2, pc.getWenyangRate2(), pc.getWyLevel2());
            }
            ///////////////////////////////////////////////////////////////////////////////////////////
            if (cmd.equals("wenyang03_01")) {
                String[] data = getInfo(pc, wenYang3, pc.getWenyangRate3(), pc.getWyLevel3());
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), getHtmlId(pc.getWyType3(), pc.getWyLevel3()), data));
                return;
            }
            if (cmd.equals("a3-1-up")) {
                int rate = pc.getWenyangRate3();
                if (rate < wenYang3.getRate()) {
                    rate = wenYang3.getRate();
                }
                if ((rate + 10) > wenYang3.getMaxRate()) {
                    return;
                }
                pc.setWenyangRate3(rate + 10);
                String[] data = getInfo(pc, wenYang3, pc.getWenyangRate3(), pc.getWyLevel3());
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), getHtmlId(pc.getWyType3(), pc.getWyLevel3()), data));
                return;
            }
            if (cmd.equals("a3-1-dn")) {
                int rate = pc.getWenyangRate3();
                if (rate <= wenYang3.getRate()) {
                    rate = wenYang3.getRate();
                }
                if ((rate - 10) < wenYang3.getRate()) {
                    return;
                }
                pc.setWenyangRate3(rate - 10);
                String[] data = getInfo(pc, wenYang1, pc.getWenyangRate1(), pc.getWyLevel3());
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), getHtmlId(pc.getWyType3(), pc.getWyLevel3()), data));
                return;
            }
            if (cmd.equals("a3-1")) {
                if (pc.getWenyangJiFen() < wenYang3.getCost()) {
                    pc.sendPackets(new S_SystemMessage("您的紋樣積分不足"));
                    return;
                }
                if (pc.getWyLevel3() >= 30) {
                    pc.sendPackets(new S_SystemMessage("伺服器最高紋樣等級為30等"));
                    return;
                }
                // 播放動畫
                playAnimation(pc, npc, pc.getWyType3(), wenYang3, pc.getWenyangRate3());
                if (ThreadLocalRandom.current().nextInt(100) < wenYang3.getRate()) {
                    pc.setWenyangJiFen(pc.getWenyangJiFen() - wenYang3.getCost());
                    pc.setWyType3(3);
                    pc.setWyLevel3(pc.getWyLevel3() + 1);
                    pc.sendPackets(new S_SystemMessage("恭喜升級成功,重登後獲得紋樣屬性"));
                    pc.save();
                    addLog(pc, pc.getWyType3(), pc.getWyLevel3());
                } else {
                    pc.setWenyangJiFen(pc.getWenyangJiFen() - wenYang3.getCost());
                    pc.sendPackets(new S_SystemMessage("很遺憾升級失敗"));
                    pc.save();
                }
                // 顯示最後狀態
                finishAnimation(pc, npc, pc.getWyType3(), wenYang3, pc.getWenyangRate3(), pc.getWyLevel3());
            }
            ///////////////////////////////////////////////////////////////////////////////////////////
            if (cmd.equals("wenyang04_01")) {
                String[] data = getInfo(pc, wenYang1, pc.getWenyangRate4(), pc.getWyLevel4());
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), getHtmlId(pc.getWyType4(), pc.getWyLevel4()), data));
                return;
            }
            if (cmd.equals("a4-1-up")) {
                int rate = pc.getWenyangRate4();
                if (rate < wenYang4.getRate()) {
                    rate = wenYang4.getRate();
                }
                if ((rate + 10) > wenYang4.getMaxRate()) {
                    return;
                }
                pc.setWenyangRate4(rate + 10);
                String[] data = getInfo(pc, wenYang4, pc.getWenyangRate4(), pc.getWyLevel4());
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), getHtmlId(pc.getWyType4(), pc.getWyLevel4()), data));
                return;
            }
            if (cmd.equals("a4-1-dn")) {
                int rate = pc.getWenyangRate4();
                if (rate <= wenYang4.getRate()) {
                    rate = wenYang4.getRate();
                }
                if ((rate - 10) < wenYang4.getRate()) {
                    return;
                }
                pc.setWenyangRate4(rate - 10);
                String[] data = getInfo(pc, wenYang4, pc.getWenyangRate4(), pc.getWyLevel4());
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), getHtmlId(pc.getWyType4(), pc.getWyLevel4()), data));
                return;
            }
            if (cmd.equals("a4-1")) {
                if (pc.getWenyangJiFen() < wenYang4.getCost()) {
                    pc.sendPackets(new S_SystemMessage("您的紋樣積分不足"));
                    return;
                }
                if (pc.getWyLevel4() >= 30) {
                    pc.sendPackets(new S_SystemMessage("伺服器最高紋樣等級為30等"));
                    return;
                }
                // 播放動畫
                playAnimation(pc, npc, pc.getWyType4(), wenYang4, pc.getWenyangRate4());
                if (ThreadLocalRandom.current().nextInt(100) < wenYang4.getRate()) {
                    pc.setWenyangJiFen(pc.getWenyangJiFen() - wenYang4.getCost());
                    pc.setWyType4(4);
                    pc.setWyLevel4(pc.getWyLevel4() + 1);
                    pc.sendPackets(new S_SystemMessage("恭喜升級成功,重登後獲得紋樣屬性"));
                    pc.save();
                    addLog(pc, pc.getWyType4(), pc.getWyLevel4());
                } else {
                    pc.setWenyangJiFen(pc.getWenyangJiFen() - wenYang4.getCost());
                    pc.sendPackets(new S_SystemMessage("很遺憾升級失敗"));
                    pc.save();
                }
                // 顯示最後狀態
                finishAnimation(pc, npc, pc.getWyType4(), wenYang4, pc.getWenyangRate4(), pc.getWyLevel4());
            }
            ///////////////////////////////////////////////////////////////////////////////////////////
            if (cmd.equals("wenyang05_01")) {
                String[] data = getInfo(pc, wenYang5, pc.getWenyangRate5(), pc.getWyLevel5());
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), getHtmlId(pc.getWyType5(), pc.getWyLevel5()), data));
                return;
            }
            if (cmd.equals("a5-1-up")) {
                int rate = pc.getWenyangRate5();
                if (rate < wenYang5.getRate()) {
                    rate = wenYang5.getRate();
                }
                if ((rate + 10) > wenYang5.getMaxRate()) {
                    return;
                }
                pc.setWenyangRate5(rate + 10);
                String[] data = getInfo(pc, wenYang5, pc.getWenyangRate5(), pc.getWyLevel5());
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), getHtmlId(pc.getWyType5(), pc.getWyLevel5()), data));
                return;
            }
            if (cmd.equals("a5-1-dn")) {
                int rate = pc.getWenyangRate5();
                if (rate <= wenYang5.getRate()) {
                    rate = wenYang5.getRate();
                }
                if ((rate - 10) < wenYang5.getRate()) {
                    return;
                }
                pc.setWenyangRate5(rate - 10);
                String[] data = getInfo(pc, wenYang5, pc.getWenyangRate5(), pc.getWyLevel5());
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), getHtmlId(pc.getWyType1(), pc.getWyLevel5()), data));
                return;
            }
            if (cmd.equals("a5-1")) {
                if (pc.getWenyangJiFen() < wenYang5.getCost()) {
                    pc.sendPackets(new S_SystemMessage("您的紋樣積分不足"));
                    return;
                }
                if (pc.getWyLevel5() >= 30) {
                    pc.sendPackets(new S_SystemMessage("伺服器最高紋樣等級為30等"));
                    return;
                }
                // 播放動畫
                playAnimation(pc, npc, pc.getWyType5(), wenYang5, pc.getWenyangRate5());
                if (ThreadLocalRandom.current().nextInt(100) < wenYang5.getRate()) {
                    pc.setWenyangJiFen(pc.getWenyangJiFen() - wenYang5.getCost());
                    pc.setWyType5(5);
                    pc.setWyLevel5(pc.getWyLevel5() + 1);
                    pc.sendPackets(new S_SystemMessage("恭喜升級成功,重登後獲得紋樣屬性"));
                    pc.save();
                    addLog(pc, pc.getWyType5(), pc.getWyLevel5());
                } else {
                    pc.setWenyangJiFen(pc.getWenyangJiFen() - wenYang5.getCost());
                    pc.sendPackets(new S_SystemMessage("很遺憾升級失敗"));
                    pc.save();
                }
                // 顯示最後狀態
                finishAnimation(pc, npc, pc.getWyType5(), wenYang5, pc.getWenyangRate5(), pc.getWyLevel5());
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            if (cmd.equals("wenyang06_01")) {
                String[] data = getInfo(pc, wenYang6, pc.getWenyangRate6(), pc.getWyLevel6());
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), getHtmlId(pc.getWyType6(), pc.getWyLevel6()), data));
                return;
            }
            if (cmd.equals("a6-1-up")) {
                int rate = pc.getWenyangRate6();
                if (rate < wenYang6.getRate()) {
                    rate = wenYang6.getRate();
                }
                if ((rate + 10) > wenYang6.getMaxRate()) {
                    return;
                }
                pc.setWenyangRate6(rate + 10);
                String[] data = getInfo(pc, wenYang6, pc.getWenyangRate6(), pc.getWyLevel6());
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), getHtmlId(pc.getWyType6(), pc.getWyLevel6()), data));
                return;
            }
            if (cmd.equals("a6-1-dn")) {
                int rate = pc.getWenyangRate6();
                if (rate <= wenYang6.getRate()) {
                    rate = wenYang6.getRate();
                }
                if ((rate - 10) < wenYang6.getRate()) {
                    return;
                }
                pc.setWenyangRate6(rate - 10);
                String[] data = getInfo(pc, wenYang1, pc.getWenyangRate1(), pc.getWyLevel6());
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), getHtmlId(pc.getWyType6(), pc.getWyLevel6()), data));
                return;
            }
            if (cmd.equals("a6-1")) {
                if (pc.getWenyangJiFen() < wenYang6.getCost()) {
                    pc.sendPackets(new S_SystemMessage("您的紋樣積分不足"));
                    return;
                }
                if (pc.getWyLevel6() >= 30) {
                    pc.sendPackets(new S_SystemMessage("伺服器最高紋樣等級為30等"));
                    return;
                }
                // 播放動畫
                playAnimation(pc, npc, pc.getWyType6(), wenYang6, pc.getWenyangRate6());
                if (ThreadLocalRandom.current().nextInt(100) < wenYang6.getRate()) {
                    pc.setWenyangJiFen(pc.getWenyangJiFen() - wenYang6.getCost());
                    pc.setWyType6(6);
                    pc.setWyLevel6(pc.getWyLevel6() + 1);
                    pc.sendPackets(new S_SystemMessage("恭喜升級成功,重登後獲得紋樣屬性"));
                    pc.save();
                    addLog(pc, pc.getWyType6(), pc.getWyLevel6());
                } else {
                    pc.setWenyangJiFen(pc.getWenyangJiFen() - wenYang6.getCost());
                    pc.sendPackets(new S_SystemMessage("很遺憾升級失敗"));
                    pc.save();
                }
                // 顯示最後狀態
                finishAnimation(pc, npc, pc.getWyType6(), wenYang6, pc.getWenyangRate6(), pc.getWyLevel6());
            }
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void playAnimation(final L1PcInstance pc, final L1NpcInstance npc, int type, L1WenYang wenYang, int rate) {
        try {
            String[] data = getInfo(pc, wenYang, rate, 0);
            for (int i = 0; i < 31; i++) {
                String htmlId = String.format("wenyang%02d_%02d", type, i + 1);
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), htmlId, data));
                TimeUnit.MILLISECONDS.sleep(50);
            }
        } catch (InterruptedException e) {
            // TODO 自動生成的 catch 塊
            e.printStackTrace();
        }
    }

    public void finishAnimation(final L1PcInstance pc, final L1NpcInstance npc, int type, L1WenYang wenYang, int rate, int level) {
        String[] data = getInfo(pc, wenYang, rate, level);
        String htmlId = getHtmlId(type, level);
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), htmlId, data));
    }

    private String getHtmlId(int type, int level) {
        return String.format("wenyang%02d_%02d", type, level + 1);
    }
    //	private class Work implements Runnable {
    //		private L1PcInstance _pc;
    //		private L1NpcInstance _npc;
    //		private int _type;
    //		private L1WenYang _wenyang;
    //		private int _rate;
    //		private int _level;
    //
    //		private Work(L1PcInstance pc, L1NpcInstance npc, int type, L1WenYang wenYang, int rate, int level) {
    //			_pc = pc;
    //			_npc = npc;
    //			_type = type;
    //			_wenyang = wenYang;
    //			_rate = rate;
    //			_level = level;
    //		}
    //
    //		public void getStart() {
    //			GeneralThreadPool.get().schedule(this, 10L);
    //		}
    //
    //		public void run() {
    //			try {
    //				Point point = null;
    //				int t = Npc_Philip._random.nextInt(_point.length);
    //				if (!_npc.getLocation().isSamePoint(_point[t])) {
    //					point = _point[t];
    //				}
    //
    //				boolean isWork = true;
    //				while (isWork) {
    //					TimeUnit.MILLISECONDS.sleep(_spr);
    //
    //					if (point != null)
    //						isWork = _npcMove.actionStart(point);
    //					else {
    //						isWork = false;
    //					}
    //					if (_npc.getLocation().isSamePoint(_point[2])) {
    //						_npc.setHeading(4);
    //						_npc.broadcastPacketX8(new S_ChangeHeading(_npc));
    //						TimeUnit.MILLISECONDS.sleep(_spr);
    //						for (int i = 0; i < 3; i++) {
    //							_npc.broadcastPacketX8(new S_DoActionGFX(_npc.getId(), 7));
    //							TimeUnit.MILLISECONDS.sleep(_spr);
    //						}
    //					}
    //				}
    //			} catch (Exception e) {
    //				Npc_Philip._log.error(e.getLocalizedMessage(), e);
    //			}
    //		}
    //	}
}
