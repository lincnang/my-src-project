package com.add.Tsai;

import com.lineage.server.Manly.L1WenYang;
import com.lineage.server.Manly.L1WenYangJiLu;
import com.lineage.server.Manly.WenYangJiLuTable;
import com.lineage.server.Manly.WenYangTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 紋樣指令
 *
 * @author hero
 */
public class WenYangCmd {
    private static final int RATE = 5;
    private static final Log _log = LogFactory.getLog(WenYangCmd.class);
    private static WenYangCmd _instance;
    Random _Random = new Random();

    private boolean _isAnimation = false;

    public static WenYangCmd get() {
        if (_instance == null) {
            _instance = new WenYangCmd();
        }
        return _instance;
    }

    public boolean Cmd(final L1PcInstance pc, final String cmd) {
        try {
            L1WenYang wenYang1 = WenYangTable.getInstance().getTemplate(pc.getWyType1(), pc.getWyLevel1() == 0 ? 1 : pc.getWyLevel1());
            if (wenYang1 == null) {
                return false;
            }
            L1WenYang wenYang2 = WenYangTable.getInstance().getTemplate(pc.getWyType2(), pc.getWyLevel2() == 0 ? 1 : pc.getWyLevel2());
            if (wenYang2 == null) {
                return false;
            }
            L1WenYang wenYang3 = WenYangTable.getInstance().getTemplate(pc.getWyType3(), pc.getWyLevel3() == 0 ? 1 : pc.getWyLevel3());
            if (wenYang3 == null) {
                return false;
            }
            L1WenYang wenYang4 = WenYangTable.getInstance().getTemplate(pc.getWyType4(), pc.getWyLevel4() == 0 ? 1 : pc.getWyLevel4());
            if (wenYang4 == null) {
                return false;
            }
            L1WenYang wenYang5 = WenYangTable.getInstance().getTemplate(pc.getWyType5(), pc.getWyLevel5() == 0 ? 1 : pc.getWyLevel5());
            if (wenYang5 == null) {
                return false;
            }
            L1WenYang wenYang6 = WenYangTable.getInstance().getTemplate(pc.getWyType6(), pc.getWyLevel6() == 0 ? 1 : pc.getWyLevel6());
            if (wenYang6 == null) {
                return false;
            }
            if (cmd.equals("wenyang_01")) {
                String[] data = getInfo(pc, wenYang1, pc.getWenyangRate1(), pc.getWyLevel1());
                pc.sendPackets(new S_NPCTalkReturn(pc, getHtmlId(pc.getWyType1(), pc.getWyLevel1()), data));
                return true;
            }
            if (cmd.equals("a1-1-up")) {
                int rate = pc.getWenyangRate1();
                if (rate < wenYang1.getRate()) {
                    rate = wenYang1.getRate();
                }
                if ((rate + RATE) > wenYang1.getMaxRate()) {
                    return true;
                }
                pc.setWenyangRate1(rate + RATE);
                String[] data = getInfo(pc, wenYang1, pc.getWenyangRate1(), pc.getWyLevel1());
                pc.sendPackets(new S_NPCTalkReturn(pc, getHtmlId(pc.getWyType1(), pc.getWyLevel1()), data));
                return true;
            }
            if (cmd.equals("a1-1-dn")) {
                int rate = pc.getWenyangRate1();
                if (rate <= wenYang1.getRate()) {
                    rate = wenYang1.getRate();
                }
                if ((rate - RATE) < wenYang1.getRate()) {
                    return true;
                }
                pc.setWenyangRate1(rate - RATE);
                String[] data = getInfo(pc, wenYang1, pc.getWenyangRate1(), pc.getWyLevel1());
                pc.sendPackets(new S_NPCTalkReturn(pc, getHtmlId(pc.getWyType1(), pc.getWyLevel1()), data));
                return true;
            }
            if (cmd.equals("a1-1")) {
                int cost = wenYang1.getCost();
                if (pc.getWenyangRate1() >= wenYang1.getRate()) {
                    cost += ((pc.getWenyangRate1() - wenYang1.getRate()) / 10) * wenYang1.getCostUp();
                }
                if (pc.getWenyangJiFen() < cost) {
                    pc.sendPackets(new S_SystemMessage("您的紋樣積分不足"));
                    return true;
                }
                if (pc.getWyLevel1() >= 30) {
                    pc.sendPackets(new S_SystemMessage("伺服器最高紋樣等級為30等"));
                    return true;
                }
                if (_isAnimation) {
                    pc.sendPackets(new S_SystemMessage("請稍後再試"));
                    return true;
                }

                WenYangAnimation animation = new WenYangAnimation(pc, pc.getWyType1(), wenYang1, pc.getWenyangRate1(), cost);
                GeneralThreadPool.get().execute(animation);
            }
            ///////////////////////////////////////////////////////////////////////////////////////////
            if (cmd.equals("wenyang02_01")) {
                String[] data = getInfo(pc, wenYang2, pc.getWenyangRate2(), pc.getWyLevel2());
                pc.sendPackets(new S_NPCTalkReturn(pc, getHtmlId(pc.getWyType2(), pc.getWyLevel2()), data));
                return true;
            }
            if (cmd.equals("a2-1-up")) {
                int rate = pc.getWenyangRate2();
                if (rate < wenYang2.getRate()) {
                    rate = wenYang2.getRate();
                }
                if ((rate + RATE) > wenYang2.getMaxRate()) {
                    return true;
                }
                pc.setWenyangRate2(rate + RATE);
                String[] data = getInfo(pc, wenYang2, pc.getWenyangRate2(), pc.getWyLevel2());
                pc.sendPackets(new S_NPCTalkReturn(pc, getHtmlId(pc.getWyType2(), pc.getWyLevel2()), data));
                return true;
            }
            if (cmd.equals("a2-1-dn")) {
                int rate = pc.getWenyangRate2();
                if (rate <= wenYang2.getRate()) {
                    rate = wenYang2.getRate();
                }
                if ((rate - RATE) < wenYang2.getRate()) {
                    return true;
                }
                pc.setWenyangRate2(rate - RATE);
                String[] data = getInfo(pc, wenYang2, pc.getWenyangRate2(), pc.getWyLevel2());
                pc.sendPackets(new S_NPCTalkReturn(pc, getHtmlId(pc.getWyType2(), pc.getWyLevel2()), data));
                return true;
            }
            if (cmd.equals("a2-1")) {
                int cost = wenYang2.getCost();
                if (pc.getWenyangRate2() >= wenYang2.getRate()) {
                    cost += ((pc.getWenyangRate2() - wenYang2.getRate()) / 10) * wenYang2.getCostUp();
                }
                if (pc.getWenyangJiFen() < wenYang2.getCost()) {
                    pc.sendPackets(new S_SystemMessage("您的紋樣積分不足"));
                    return true;
                }
                if (pc.getWyLevel2() >= 30) {
                    pc.sendPackets(new S_SystemMessage("伺服器最高紋樣等級為30等"));
                    return true;
                }
                if (_isAnimation) {
                    pc.sendPackets(new S_SystemMessage("請稍後再試"));
                    return true;
                }
                WenYangAnimation animation = new WenYangAnimation(pc, pc.getWyType2(), wenYang2, pc.getWenyangRate2(), cost);
                GeneralThreadPool.get().execute(animation);
            }
            ///////////////////////////////////////////////////////////////////////////////////////////
            if (cmd.equals("wenyang03_01")) {
                String[] data = getInfo(pc, wenYang3, pc.getWenyangRate3(), pc.getWyLevel3());
                pc.sendPackets(new S_NPCTalkReturn(pc, getHtmlId(pc.getWyType3(), pc.getWyLevel3()), data));
                return true;
            }
            if (cmd.equals("a3-1-up")) {
                int rate = pc.getWenyangRate3();
                if (rate < wenYang3.getRate()) {
                    rate = wenYang3.getRate();
                }
                if ((rate + RATE) > wenYang3.getMaxRate()) {
                    return true;
                }
                pc.setWenyangRate3(rate + RATE);
                String[] data = getInfo(pc, wenYang3, pc.getWenyangRate3(), pc.getWyLevel3());
                pc.sendPackets(new S_NPCTalkReturn(pc, getHtmlId(pc.getWyType3(), pc.getWyLevel3()), data));
                return true;
            }
            if (cmd.equals("a3-1-dn")) {
                int rate = pc.getWenyangRate3();
                if (rate <= wenYang3.getRate()) {
                    rate = wenYang3.getRate();
                }
                if ((rate - RATE) < wenYang3.getRate()) {
                    return true;
                }
                pc.setWenyangRate3(rate - RATE);
                String[] data = getInfo(pc, wenYang1, pc.getWenyangRate1(), pc.getWyLevel3());
                pc.sendPackets(new S_NPCTalkReturn(pc, getHtmlId(pc.getWyType3(), pc.getWyLevel3()), data));
                return true;
            }
            if (cmd.equals("a3-1")) {
                int cost = wenYang3.getCost();
                if (pc.getWenyangRate3() >= wenYang3.getRate()) {
                    cost += ((pc.getWenyangRate3() - wenYang3.getRate()) / 10) * wenYang3.getCostUp();
                }
                if (pc.getWenyangJiFen() < wenYang3.getCost()) {
                    pc.sendPackets(new S_SystemMessage("您的紋樣積分不足"));
                    return true;
                }
                if (pc.getWyLevel3() >= 30) {
                    pc.sendPackets(new S_SystemMessage("伺服器最高紋樣等級為30等"));
                    return true;
                }
                if (_isAnimation) {
                    pc.sendPackets(new S_SystemMessage("請稍後再試"));
                    return true;
                }
                WenYangAnimation animation = new WenYangAnimation(pc, pc.getWyType3(), wenYang3, pc.getWenyangRate3(), cost);
                GeneralThreadPool.get().execute(animation);
            }
            ///////////////////////////////////////////////////////////////////////////////////////////
            if (cmd.equals("wenyang04_01")) {
                String[] data = getInfo(pc, wenYang1, pc.getWenyangRate4(), pc.getWyLevel4());
                pc.sendPackets(new S_NPCTalkReturn(pc, getHtmlId(pc.getWyType4(), pc.getWyLevel4()), data));
                return true;
            }
            if (cmd.equals("a4-1-up")) {
                int rate = pc.getWenyangRate4();
                if (rate < wenYang4.getRate()) {
                    rate = wenYang4.getRate();
                }
                if ((rate + RATE) > wenYang4.getMaxRate()) {
                    return true;
                }
                pc.setWenyangRate4(rate + RATE);
                String[] data = getInfo(pc, wenYang4, pc.getWenyangRate4(), pc.getWyLevel4());
                pc.sendPackets(new S_NPCTalkReturn(pc, getHtmlId(pc.getWyType4(), pc.getWyLevel4()), data));
                return true;
            }
            if (cmd.equals("a4-1-dn")) {
                int rate = pc.getWenyangRate4();
                if (rate <= wenYang4.getRate()) {
                    rate = wenYang4.getRate();
                }
                if ((rate - RATE) < wenYang4.getRate()) {
                    return true;
                }
                pc.setWenyangRate4(rate - RATE);
                String[] data = getInfo(pc, wenYang4, pc.getWenyangRate4(), pc.getWyLevel4());
                pc.sendPackets(new S_NPCTalkReturn(pc, getHtmlId(pc.getWyType4(), pc.getWyLevel4()), data));
                return true;
            }
            if (cmd.equals("a4-1")) {
                int cost = wenYang4.getCost();
                if (pc.getWenyangRate4() >= wenYang4.getRate()) {
                    cost += ((pc.getWenyangRate4() - wenYang4.getRate()) / 10) * wenYang4.getCostUp();
                }
                if (pc.getWenyangJiFen() < wenYang4.getCost()) {
                    pc.sendPackets(new S_SystemMessage("您的紋樣積分不足"));
                    return true;
                }
                if (pc.getWyLevel4() >= 30) {
                    pc.sendPackets(new S_SystemMessage("伺服器最高紋樣等級為30等"));
                    return true;
                }
                if (_isAnimation) {
                    pc.sendPackets(new S_SystemMessage("請稍後再試"));
                    return true;
                }
                WenYangAnimation animation = new WenYangAnimation(pc, pc.getWyType4(), wenYang4, pc.getWenyangRate4(), cost);
                GeneralThreadPool.get().execute(animation);
            }
            ///////////////////////////////////////////////////////////////////////////////////////////
            if (cmd.equals("wenyang05_01")) {
                String[] data = getInfo(pc, wenYang5, pc.getWenyangRate5(), pc.getWyLevel5());
                pc.sendPackets(new S_NPCTalkReturn(pc, getHtmlId(pc.getWyType5(), pc.getWyLevel5()), data));
                return true;
            }
            if (cmd.equals("a5-1-up")) {
                int rate = pc.getWenyangRate5();
                if (rate < wenYang5.getRate()) {
                    rate = wenYang5.getRate();
                }
                if ((rate + RATE) > wenYang5.getMaxRate()) {
                    return true;
                }
                pc.setWenyangRate5(rate + RATE);
                String[] data = getInfo(pc, wenYang5, pc.getWenyangRate5(), pc.getWyLevel5());
                pc.sendPackets(new S_NPCTalkReturn(pc, getHtmlId(pc.getWyType5(), pc.getWyLevel5()), data));
                return true;
            }
            if (cmd.equals("a5-1-dn")) {
                int rate = pc.getWenyangRate5();
                if (rate <= wenYang5.getRate()) {
                    rate = wenYang5.getRate();
                }
                if ((rate - RATE) < wenYang5.getRate()) {
                    return true;
                }
                pc.setWenyangRate5(rate - RATE);
                String[] data = getInfo(pc, wenYang5, pc.getWenyangRate5(), pc.getWyLevel5());
                pc.sendPackets(new S_NPCTalkReturn(pc, getHtmlId(pc.getWyType1(), pc.getWyLevel5()), data));
                return true;
            }
            if (cmd.equals("a5-1")) {
                int cost = wenYang5.getCost();
                if (pc.getWenyangRate5() >= wenYang5.getRate()) {
                    cost += ((pc.getWenyangRate5() - wenYang5.getRate()) / 10) * wenYang5.getCostUp();
                }
                if (pc.getWenyangJiFen() < wenYang5.getCost()) {
                    pc.sendPackets(new S_SystemMessage("您的紋樣積分不足"));
                    return true;
                }
                if (pc.getWyLevel5() >= 30) {
                    pc.sendPackets(new S_SystemMessage("伺服器最高紋樣等級為30等"));
                    return true;
                }
                if (_isAnimation) {
                    pc.sendPackets(new S_SystemMessage("請稍後再試"));
                    return true;
                }
                WenYangAnimation animation = new WenYangAnimation(pc, pc.getWyType5(), wenYang5, pc.getWenyangRate5(), cost);
                GeneralThreadPool.get().execute(animation);
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            if (cmd.equals("wenyang06_01")) {
                String[] data = getInfo(pc, wenYang6, pc.getWenyangRate6(), pc.getWyLevel6());
                pc.sendPackets(new S_NPCTalkReturn(pc, getHtmlId(pc.getWyType6(), pc.getWyLevel6()), data));
                return true;
            }
            if (cmd.equals("a6-1-up")) {
                int rate = pc.getWenyangRate6();
                if (rate < wenYang6.getRate()) {
                    rate = wenYang6.getRate();
                }
                if ((rate + RATE) > wenYang6.getMaxRate()) {
                    return true;
                }
                pc.setWenyangRate6(rate + RATE);
                String[] data = getInfo(pc, wenYang6, pc.getWenyangRate6(), pc.getWyLevel6());
                pc.sendPackets(new S_NPCTalkReturn(pc, getHtmlId(pc.getWyType6(), pc.getWyLevel6()), data));
                return true;
            }
            if (cmd.equals("a6-1-dn")) {
                int rate = pc.getWenyangRate6();
                if (rate <= wenYang6.getRate()) {
                    rate = wenYang6.getRate();
                }
                if ((rate - RATE) < wenYang6.getRate()) {
                    return true;
                }
                pc.setWenyangRate6(rate - RATE);
                String[] data = getInfo(pc, wenYang1, pc.getWenyangRate1(), pc.getWyLevel6());
                pc.sendPackets(new S_NPCTalkReturn(pc, getHtmlId(pc.getWyType6(), pc.getWyLevel6()), data));
                return true;
            }
            if (cmd.equals("a6-1")) {
                int cost = wenYang6.getCost();
                if (pc.getWenyangRate6() >= wenYang6.getRate()) {
                    cost += ((pc.getWenyangRate6() - wenYang6.getRate()) / 10) * wenYang6.getCostUp();
                }
                if (pc.getWenyangJiFen() < wenYang6.getCost()) {
                    pc.sendPackets(new S_SystemMessage("您的紋樣積分不足"));
                    return true;
                }
                if (pc.getWyLevel6() >= 30) {
                    pc.sendPackets(new S_SystemMessage("伺服器最高紋樣等級為30等"));
                    return true;
                }
                WenYangAnimation animation = new WenYangAnimation(pc, pc.getWyType6(), wenYang6, pc.getWenyangRate6(), cost);
                GeneralThreadPool.get().execute(animation);
            }
            return false;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
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
            cost += ((rate - wenYang.getRate()) / RATE) * wenYang.getCostUp();
        }
        String[] data = new String[4];
        data[0] = String.valueOf(level);
        data[1] = String.valueOf(pc.getWenyangJiFen());
        data[2] = String.valueOf(rate < wenYang.getRate() ? wenYang.getRate() : rate);
        data[3] = String.valueOf(cost);
        return data;
    }

    public void playAnimation(final L1PcInstance pc, final L1NpcInstance npc, int type, L1WenYang wenYang, int rate) {
        try {
            _isAnimation = true;

            String[] data = getInfo(pc, wenYang, rate, 0);
            for (int i = 0; i < 31; i++) {
                String htmlId = String.format("wenyang%02d_%02d", type, i + 1);
                pc.sendPackets(new S_NPCTalkReturn(pc, htmlId, data));
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
        pc.sendPackets(new S_NPCTalkReturn(pc, htmlId, data));

        _isAnimation = false;
    }

    private String getHtmlId(int type, int level) {
        return String.format("wenyang%02d_%02d", type, level + 1);
    }

    class WenYangAnimation implements Runnable {
        private L1PcInstance _pc;
        private int _type;
        private L1WenYang _wenYang;
        private int _rate;
        private int _cost;

        public WenYangAnimation(final L1PcInstance pc, int type, L1WenYang wenYang, int rate, int cost) {
            _pc = pc;
            _type = type;
            _wenYang = wenYang;
            _rate = rate;
            _cost = cost;
        }

        public void run() {
            try {
                // 播放動畫
                playAnimation(_pc, null, _type, _wenYang, _rate);
                if (ThreadLocalRandom.current().nextInt(100) < _wenYang.getRate()) {
                    _pc.setWenyangJiFen(_pc.getWenyangJiFen() - _cost);
                    _pc.setWenyangTypeAndLevel(_type);
                    _pc.sendPackets(new S_SystemMessage("恭喜升級成功,重登後獲得紋樣屬性"));
                    _pc.save();
                    addLog(_pc, _type, _pc.getWyLevel(_type));
                } else {
                    _pc.setWenyangJiFen(_pc.getWenyangJiFen() - _cost);
                    _pc.sendPackets(new S_SystemMessage("很遺憾升級失敗"));
                    _pc.save();
                }
                // 顯示最後狀態
                finishAnimation(_pc, null, _type, _wenYang, _rate, _pc.getWyLevel(_type));
            } catch (Exception e) {
                _log.error(e.getLocalizedMessage(), e);
            }
        }
    }
}