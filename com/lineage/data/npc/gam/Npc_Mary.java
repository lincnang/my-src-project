package com.lineage.data.npc.gam;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.Shutdown;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.MaryReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1Item;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Npc_Mary extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Mary.class);
    private static final Random _random = new Random();
    private static final Map<Integer, MaryTemp> _maryUsers = new HashMap<Integer, MaryTemp>();
    private static final int[] _min = {2, 4, 6, 7, 10, 12, 14, 15};
    private static int _itemid = 40308;
    private static int _count;
    private static int _x_a1 = 100;
    private static int _x_a2 = 50;
    private static int _x_b1 = 40;
    private static int _x_b2 = 2;
    private static int _x_c1 = 30;
    private static int _x_c2 = 2;
    private static int _x_d1 = 20;
    private static int _x_d2 = 2;
    private static int _x_e1 = 15;
    private static int _x_e2 = 2;
    private static int _x_f1 = 10;
    private static int _x_f2 = 2;
    private static int _x_g1 = 5;
    private static int _x_g2 = 2;
    private static int _out_prize;
    private static long _all_stake;
    private static long _all_user_prize;

    public static void set_itemid(int itemid) {
        _itemid = itemid;
    }

    public static void set_count(int count) {
        _count = count;
    }

    public static void set_x_a1(int x_a1) {
        _x_a1 = x_a1;
    }

    public static void set_x_a2(int x_a2) {
        _x_a2 = x_a2;
    }

    public static void set_x_b1(int x_b1) {
        _x_b1 = x_b1;
    }

    public static void set_x_b2(int x_b2) {
        _x_b2 = x_b2;
    }

    public static void set_x_c1(int x_c1) {
        _x_c1 = x_c1;
    }

    public static void set_x_c2(int x_c2) {
        _x_c2 = x_c2;
    }

    public static void set_x_d1(int x_d1) {
        _x_d1 = x_d1;
    }

    public static void set_x_d2(int x_d2) {
        _x_d2 = x_d2;
    }

    public static void set_x_e1(int x_e1) {
        _x_e1 = x_e1;
    }

    public static void set_x_e2(int x_e2) {
        _x_e2 = x_e2;
    }

    public static void set_x_f1(int x_f1) {
        _x_f1 = x_f1;
    }

    public static void set_x_f2(int x_f2) {
        _x_f2 = x_f2;
    }

    public static void set_x_g1(int x_g1) {
        _x_g1 = x_g1;
    }

    public static void set_x_g2(int x_g2) {
        _x_g2 = x_g2;
    }

    public static void set_out_prize(int out_prize) {
        _out_prize = out_prize;
    }

    public static void set_all_stake(long all_stake) {
        _all_stake = all_stake;
    }

    public static void set_all_user_prize(long all_user_prize) {
        _all_user_prize = all_user_prize;
    }

    public static void update() {
        MaryReading.get().update(_all_stake, _all_user_prize, _count);
    }

    private static long all_prize() {
        long out = _all_stake * _out_prize / 100L;
        long out_prize = out - _all_user_prize;
        if (out_prize <= 0L) {
            return 0L;
        }
        long out_prize1 = (int) (out_prize * 0.5D);
        if (ThreadLocalRandom.current().nextInt(100) <= 10) {
            return out_prize1 + ThreadLocalRandom.current().nextInt(20);
        }
        return out_prize1;
    }

    public static NpcExecutor get() {
        return new Npc_Mary();
    }

    public int type() {
        return 3;
    }

    public void talk(L1PcInstance pc, L1NpcInstance npc) {
        // _log.warn("小瑪莉管理員 紀錄 輸出百分比:" + _out_prize + "% 累積賭注:" + _all_stake +
        // " 累積中獎金額:" + _all_user_prize);
        MaryTemp tmp = (MaryTemp) _maryUsers.get(Integer.valueOf(pc.getId()));
        if (tmp == null) {
            tmp = new MaryTemp();
            _maryUsers.put(Integer.valueOf(pc.getId()), tmp);
        }
        tmp._x_a = 0;
        tmp._x_b = 0;
        tmp._x_c = 0;
        tmp._x_d = 0;
        tmp._x_e = 0;
        tmp._x_f = 0;
        tmp._x_g = 0;
        tmp._x_a_t = 0;
        tmp._x_b_t = 0;
        tmp._x_c_t = 0;
        tmp._x_d_t = 0;
        tmp._x_e_t = 0;
        tmp._x_f_t = 0;
        tmp._x_g_t = 0;
        tmp._x_h_i = false;
        tmp._prize_all = all_prize();
        MaryTimer maryTimer = new MaryTimer(pc, npc, 0, -1);
        maryTimer.startCmd();
    }

    public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
        MaryTemp tmp = (MaryTemp) _maryUsers.get(Integer.valueOf(pc.getId()));
        if (tmp == null) {
            tmp = new MaryTemp();
        }
        if (tmp._stop) {
            return;
        }
        if (cmd.equalsIgnoreCase("start")) {
            if (Shutdown.SHUTDOWN) {
                pc.sendPackets(new S_ServerMessage(166, "系統暫停接受下注，請盡快取回您的獎金"));
                return;
            }
            long down = tmp._x_a + tmp._x_b + tmp._x_c + tmp._x_d + tmp._x_e + tmp._x_f + tmp._x_g;
            if (down != 0L) {
                L1ItemInstance tgitem = pc.getInventory().checkItemX(_itemid, down);
                if (tgitem != null) {
                    MaryTimer maryTimer = new MaryTimer(pc, npc, 1, -1);
                    maryTimer.startCmd();
                } else {
                    L1Item tgItem = ItemTable.get().getTemplate(_itemid);
                    pc.sendPackets(new S_ServerMessage(337, tgItem.getNameId()));
                    pc.sendPackets(new S_CloseList(pc.getId()));
                }
            } else {
                pc.sendPackets(new S_ServerMessage("\\fR您還沒有下注!!"));
            }
            return;
        }
        if (cmd.equalsIgnoreCase("re")) {
            tmp._x_a = tmp._x_a_t;
            tmp._x_b = tmp._x_b_t;
            tmp._x_c = tmp._x_c_t;
            tmp._x_d = tmp._x_d_t;
            tmp._x_e = tmp._x_e_t;
            tmp._x_f = tmp._x_f_t;
            tmp._x_g = tmp._x_g_t;
        } else {
            if (cmd.equalsIgnoreCase("get")) {
                if (tmp._prize != 0L) {
                    _all_user_prize += tmp._prize;
                    _log.warn("小瑪莉管理員 紀錄：" + pc.getName() + " 領取獎金(" + tmp._prize + ")");
                    CreateNewItem.createNewItem(pc, _itemid, tmp._prize);
                    tmp._prize = 0L;
                }
                tmp._x_a_t = tmp._x_a;
                tmp._x_b_t = tmp._x_b;
                tmp._x_c_t = tmp._x_c;
                tmp._x_d_t = tmp._x_d;
                tmp._x_e_t = tmp._x_e;
                tmp._x_f_t = tmp._x_f;
                tmp._x_g_t = tmp._x_g;
                tmp._x_a = 0;
                tmp._x_b = 0;
                tmp._x_c = 0;
                tmp._x_d = 0;
                tmp._x_e = 0;
                tmp._x_f = 0;
                tmp._x_g = 0;
                tmp._x_h_i = false;
                tmp._prize = 0L;
                long tgitem_count = pc.getInventory().countItems(_itemid);
                tmp._prize_all = all_prize();
                String[] info = {"資本:" + tgitem_count + "  彩金:" + tmp._prize_all, "00", "00", "00", "00", "00", "00", "00", tmp._x10 == 1 ? "一次下注10點" : "一次下注1點"};
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bar_00", info));
                return;
            }
            if (cmd.equalsIgnoreCase("a")) {
                tmp._x_a += 1 * tmp._x10;
            } else if (cmd.equalsIgnoreCase("b")) {
                tmp._x_b += 1 * tmp._x10;
            } else if (cmd.equalsIgnoreCase("c")) {
                tmp._x_c += 1 * tmp._x10;
            } else if (cmd.equalsIgnoreCase("d")) {
                tmp._x_d += 1 * tmp._x10;
            } else if (cmd.equalsIgnoreCase("e")) {
                tmp._x_e += 1 * tmp._x10;
            } else if (cmd.equalsIgnoreCase("f")) {
                tmp._x_f += 1 * tmp._x10;
            } else if (cmd.equalsIgnoreCase("g")) {
                tmp._x_g += 1 * tmp._x10;
            } else if (cmd.equalsIgnoreCase("XL99")) {
                tmp._x_a += 99;
                tmp._x_b += 99;
                tmp._x_c += 99;
                tmp._x_d += 99;
                tmp._x_e += 99;
                tmp._x_f += 99;
                tmp._x_g += 99;
            } else if (cmd.equalsIgnoreCase("XL50")) {
                tmp._x_a += 50;
                tmp._x_b += 50;
                tmp._x_c += 50;
                tmp._x_d += 50;
                tmp._x_e += 50;
                tmp._x_f += 50;
                tmp._x_g += 50;
            } else if (cmd.equalsIgnoreCase("XL10")) {
                tmp._x_a += 10;
                tmp._x_b += 10;
                tmp._x_c += 10;
                tmp._x_d += 10;
                tmp._x_e += 10;
                tmp._x_f += 10;
                tmp._x_g += 10;
            } else if (cmd.equalsIgnoreCase("XL05")) {
                tmp._x_a += 5;
                tmp._x_b += 5;
                tmp._x_c += 5;
                tmp._x_d += 5;
                tmp._x_e += 5;
                tmp._x_f += 5;
                tmp._x_g += 5;
            } else if (cmd.equalsIgnoreCase("XL01")) {
                tmp._x_a += 1;
                tmp._x_b += 1;
                tmp._x_c += 1;
                tmp._x_d += 1;
                tmp._x_e += 1;
                tmp._x_f += 1;
                tmp._x_g += 1;
            } else {
                if (cmd.equalsIgnoreCase("h")) {
                    if (Shutdown.SHUTDOWN) {
                        pc.sendPackets(new S_ServerMessage(166, "系統暫停接受下注，請盡快取回您的獎金"));
                        return;
                    }
                    if (tmp._prize <= 0L) {
                        return;
                    }
                    if (tmp._x_h_i_count >= 5) {
                        pc.sendPackets(new S_ServerMessage("\\fS比大小最多只能連勝五次!"));
                        return;
                    }
                    tmp._x_h_i = true;
                    MaryTimer maryTimer = new MaryTimer(pc, npc, 2, -1);
                    maryTimer.startCmd();
                    return;
                }
                if (cmd.equalsIgnoreCase("i")) {
                    if (Shutdown.SHUTDOWN) {
                        pc.sendPackets(new S_ServerMessage(166, "系統暫停接受下注，請盡快取回您的獎金"));
                        return;
                    }
                    if (tmp._prize <= 0L) {
                        return;
                    }
                    if (tmp._x_h_i_count >= 5) {
                        pc.sendPackets(new S_ServerMessage("\\fS比大小最多只能連勝五次!"));
                        return;
                    }
                    tmp._x_h_i = false;
                    MaryTimer maryTimer = new MaryTimer(pc, npc, 2, -1);
                    maryTimer.startCmd();
                    return;
                }
                if (cmd.equalsIgnoreCase("x10")) {
                    if (tmp._x10 == 1) {
                        tmp._x10 = 10;
                    } else {
                        tmp._x10 = 1;
                    }
                } else if (cmd.equalsIgnoreCase("HL")) {
                    String[] info = {String.valueOf(_x_a1), String.valueOf(_x_a2), String.valueOf(_x_b1), String.valueOf(_x_b2), String.valueOf(_x_c1), String.valueOf(_x_c2), String.valueOf(_x_d1), String.valueOf(_x_d2), String.valueOf(_x_e1), String.valueOf(_x_e2), String.valueOf(_x_f1), String.valueOf(_x_f2), String.valueOf(_x_g1), String.valueOf(_x_g2)};
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bar_hl", info));
                    return;
                }
            }
        }
        if (tmp._x_a > 99) {
            tmp._x_a = 99;
        }
        if (tmp._x_b > 99) {
            tmp._x_b = 99;
        }
        if (tmp._x_c > 99) {
            tmp._x_c = 99;
        }
        if (tmp._x_d > 99) {
            tmp._x_d = 99;
        }
        if (tmp._x_e > 99) {
            tmp._x_e = 99;
        }
        if (tmp._x_f > 99) {
            tmp._x_f = 99;
        }
        if (tmp._x_g > 99) {
            tmp._x_g = 99;
        }
        long tgitem_count = pc.getInventory().countItems(_itemid);
        tmp._prize_all = all_prize();
        String[] info = {"資本:" + tgitem_count + "  彩金:" + tmp._prize_all, tmp._x_a < 10 ? String.valueOf("0" + tmp._x_a) : String.valueOf(tmp._x_a), tmp._x_b < 10 ? String.valueOf("0" + tmp._x_b) : String.valueOf(tmp._x_b), tmp._x_c < 10 ? String.valueOf("0" + tmp._x_c) : String.valueOf(tmp._x_c), tmp._x_d < 10 ? String.valueOf("0" + tmp._x_d) : String.valueOf(tmp._x_d), tmp._x_e < 10 ? String.valueOf("0" + tmp._x_e) : String.valueOf(tmp._x_e), tmp._x_f < 10 ? String.valueOf("0" + tmp._x_f) : String.valueOf(tmp._x_f), tmp._x_g < 10 ? String.valueOf("0" + tmp._x_g) : String.valueOf(tmp._x_g), tmp._x10 == 1 ? "一次下注10點" : "一次下注1點"};
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bar_00", info));
    }

    private class MaryTemp {
        private long _prize_all = 0L;
        private int _x_a = 0;
        private int _x_b = 0;
        private int _x_c = 0;
        private int _x_d = 0;
        private int _x_e = 0;
        private int _x_f = 0;
        private int _x_g = 0;
        private boolean _x_h_i = false;
        private int _x_h_i_count = 0;
        private long _prize = 0L;
        private boolean _stop = false;
        private int _x10 = 1;
        private int _x_a_t = 0;
        private int _x_b_t = 0;
        private int _x_c_t = 0;
        private int _x_d_t = 0;
        private int _x_e_t = 0;
        private int _x_f_t = 0;
        private int _x_g_t = 0;

        private MaryTemp() {
        }
    }

    private class MaryTimer implements Runnable {
        private final L1PcInstance _pc;
        private final L1NpcInstance _npc;
        private int _mode;
        private Npc_Mary.MaryTemp _tmp;
        private int _html_id = -1;

        public MaryTimer(L1PcInstance pc, L1NpcInstance npc, int mode, int src_html) {
            _pc = pc;
            _npc = npc;
            _mode = mode;
            _tmp = ((Npc_Mary.MaryTemp) Npc_Mary._maryUsers.get(Integer.valueOf(pc.getId())));
            _tmp._stop = true;
            _html_id = src_html;
        }

        public void startCmd() {
            GeneralThreadPool.get().schedule(this, 10L);
        }

        public void run() {
            int src_html = -1;
            try {
                if (_tmp == null) {
                    Npc_Mary._log.error("小瑪莉管理員 異常:找不到 人物" + _pc.getName() + " 的小瑪莉記錄!");
                    return;
                }
                long tgitem_count = _pc.getInventory().countItems(Npc_Mary._itemid);
                switch (_mode) {
                    case 0:
                        if (_tmp._prize != 0L) {
                            _pc.sendPackets(new S_ServerMessage("\\fT未領取彩金(" + _tmp._prize + ")，現在發還給您"));
                            Npc_Mary._all_user_prize += _tmp._prize;
                            Npc_Mary._log.warn("小瑪莉管理員 紀錄：" + _pc.getName() + " 發還未領取獎金(" + _tmp._prize + ")");
                            CreateNewItem.createNewItem(_pc, Npc_Mary._itemid, _tmp._prize);
                            _tmp._prize = 0L;
                        }
                        String[] info0 = {"資本:" + tgitem_count + "  彩金:" + _tmp._prize_all, "00", "00", "00", "00", "00", "00", "00", _tmp._x10 == 1 ? "一次下注10" : "一次下注1"};
                        for (int i = 6; i > 0; i--) {
                            TimeUnit.MILLISECONDS.sleep(250L);
                            if (i % 2 == 0) {
                                _pc.sendPackets(new S_NPCTalkReturn(_npc.getId(), "bar_00", info0));
                            } else {
                                _pc.sendPackets(new S_NPCTalkReturn(_npc.getId(), "bar_20", info0));
                            }
                        }
                        break;
                    case 1:
                        Npc_Mary._count += 1;
                        _tmp._x_h_i_count = 0;
                        long count = _tmp._x_a + _tmp._x_b + _tmp._x_c + _tmp._x_d + _tmp._x_e + _tmp._x_f + _tmp._x_g;
                        if (_html_id == -1) {
                            L1ItemInstance tgitem = _pc.getInventory().checkItemX(Npc_Mary._itemid, count);
                            if (tgitem != null) {
                                Npc_Mary._all_stake += count;
                                _pc.getInventory().removeItem(tgitem.getId(), count);
                            } else {
                                L1Item tgItem = ItemTable.get().getTemplate(Npc_Mary._itemid);
                                _pc.sendPackets(new S_ServerMessage(337, tgItem.getNameId()));
                                _pc.sendPackets(new S_CloseList(_pc.getId()));
                                break;
                            }
                        }
                        String[] info1 = {"資本:" + (tgitem_count - count) + "  彩金:" + _tmp._prize_all, _tmp._x_a < 10 ? String.valueOf("0" + _tmp._x_a) : String.valueOf(_tmp._x_a), _tmp._x_b < 10 ? String.valueOf("0" + _tmp._x_b) : String.valueOf(_tmp._x_b), _tmp._x_c < 10 ? String.valueOf("0" + _tmp._x_c) : String.valueOf(_tmp._x_c), _tmp._x_d < 10 ? String.valueOf("0" + _tmp._x_d) : String.valueOf(_tmp._x_d), _tmp._x_e < 10 ? String.valueOf("0" + _tmp._x_e) : String.valueOf(_tmp._x_e), _tmp._x_f < 10 ? String.valueOf("0" + _tmp._x_f) : String.valueOf(_tmp._x_f), _tmp._x_g < 10 ? String.valueOf("0" + _tmp._x_g) : String.valueOf(_tmp._x_g)};
                        int index = 1;
                        int win = check(_tmp._prize_all);
                        int count1 = 32 + win;
                        if (_html_id != -1) {
                            if (_html_id == 15) {
                                count1 += 2;
                            } else if (_html_id == 7) {
                                count1 += 10;
                            }
                            index = _html_id;
                        }
                        int html_id = -1;
                        for (int i = 0; i < count1; i++) {
                            TimeUnit.MILLISECONDS.sleep(i * 3);
                            html_id = index;
                            _pc.sendPackets(new S_NPCTalkReturn(_npc.getId(), "bar_" + (html_id < 10 ? "0" + html_id : Integer.valueOf(html_id)), info1));
                            index++;
                            if (index > 16) {
                                index = 1;
                            }
                        }
                        int add = 0;
                        String out = "";
                        switch (html_id) {
                            case 1:
                                add += _tmp._x_b * Npc_Mary._x_b1;
                                out = "(大半瓜:" + html_id + " 下注:" + _tmp._x_b + ")";
                                break;
                            case 2:
                                add += _tmp._x_b * Npc_Mary._x_b2;
                                out = "(小半瓜:" + html_id + " 下注:" + _tmp._x_b + ")";
                                break;
                            case 3:
                                add += _tmp._x_a * Npc_Mary._x_a1;
                                out = "(大BAR:" + html_id + " 下注:" + _tmp._x_a + ")";
                                break;
                            case 4:
                                add += _tmp._x_d * Npc_Mary._x_d2;
                                out = "(小西瓜:" + html_id + " 下注:" + _tmp._x_d + ")";
                                break;
                            case 5:
                                add += _tmp._x_d * Npc_Mary._x_d1;
                                out = "(大西瓜:" + html_id + " 下注:" + _tmp._x_d + ")";
                                break;
                            case 6:
                                add += _tmp._x_e * Npc_Mary._x_e2;
                                out = "(小香蕉:" + html_id + " 下注:" + _tmp._x_e + ")";
                                break;
                            case 7:
                                src_html = html_id;
                                TimeUnit.MILLISECONDS.sleep(1000L);
                                break;
                            case 8:
                                add += _tmp._x_f * Npc_Mary._x_f1;
                                out = "(大檸檬:" + html_id + " 下注:" + _tmp._x_f + ")";
                                break;
                            case 9:
                                add += _tmp._x_g * Npc_Mary._x_g1;
                                out = "(大橘子:" + html_id + " 下注:" + _tmp._x_g + ")";
                                break;
                            case 10:
                                add += _tmp._x_g * Npc_Mary._x_g2;
                                out = "(小橘子:" + html_id + " 下注:" + _tmp._x_g + ")";
                                break;
                            case 11:
                                add += _tmp._x_a * Npc_Mary._x_a2;
                                out = "(小BAR:" + html_id + " 下注:" + _tmp._x_a + ")";
                                break;
                            case 12:
                                add += _tmp._x_c * Npc_Mary._x_c2;
                                out = "(小蘋果:" + html_id + " 下注:" + _tmp._x_c + ")";
                                break;
                            case 13:
                                add += _tmp._x_c * Npc_Mary._x_c1;
                                out = "(大蘋果:" + html_id + " 下注:" + _tmp._x_c + ")";
                                break;
                            case 14:
                                add += _tmp._x_f * Npc_Mary._x_f2;
                                out = "(小檸檬:" + html_id + " 下注:" + _tmp._x_f + ")";
                                break;
                            case 15:
                                src_html = html_id;
                                TimeUnit.MILLISECONDS.sleep(1000L);
                                break;
                            case 16:
                                add += _tmp._x_e * Npc_Mary._x_e1;
                                out = "(大香蕉:" + html_id + " 下注:" + _tmp._x_e + ")";
                        }
                        if (src_html == -1) {
                            _tmp._prize += add;
                            if (add > 0) {
                                String bar = "";
                                if ((_tmp._x_a == 99) && (html_id == 3)) {
                                    _tmp._prize += _tmp._prize_all;
                                    bar = " 拉彩金:" + _tmp._prize_all;
                                    _pc.sendPacketsX8(new S_SkillSound(_pc.getId(), 5763));
                                    World.get().broadcastPacketToAll(new S_ServerMessage("\\fV恭喜玩家" + _pc.getName() + "BAR拉彩金 獎金:" + _tmp._prize));
                                }
                                Npc_Mary._log.warn("小瑪莉管理員 紀錄：" + _pc.getName() + " 下注:" + count + " 中獎:" + add + " 當時彩金:" + _tmp._prize_all + "下注細項:" + _tmp._x_a + "/" + _tmp._x_b + "/" + _tmp._x_c + "/" + _tmp._x_d + "/" + _tmp._x_e + "/" + _tmp._x_f + "/" + _tmp._x_g + " 開出" + out + bar + " (" + Npc_Mary._count + ")");
                            } else {
                                Npc_Mary._log.warn("小瑪莉管理員 紀錄：" + _pc.getName() + " 下注:" + count + " 未中獎!" + " 當時彩金:" + _tmp._prize_all + "下注細項:" + _tmp._x_a + "/" + _tmp._x_b + "/" + _tmp._x_c + "/" + _tmp._x_d + "/" + _tmp._x_e + "/" + _tmp._x_f + "/" + _tmp._x_g + " 開出" + out + " (" + Npc_Mary._count + ")");
                            }
                            String[] info1_1 = {"得獎:" + _tmp._prize + "  彩金:" + _tmp._prize_all, "00", "00", "00", "00", "00", "00", "00"};
                            _pc.sendPackets(new S_NPCTalkReturn(_npc.getId(), "bar_" + (html_id < 10 ? "0" + html_id : Integer.valueOf(html_id)), info1_1));
                            _pc.sendPackets(new S_ServerMessage(166, "下注" + _tmp._x_a + "/" + _tmp._x_b + "/" + _tmp._x_c + "/" + _tmp._x_d + "/" + _tmp._x_e + "/" + _tmp._x_f + "/" + _tmp._x_g + " 開出:" + out + " (" + Npc_Mary._count + ")"));
                        }
                        break;
                    case 2:
                        int count2 = 0;
                        String hi = "";
                        if (_tmp._x_h_i) {
                            hi = "您目前選大 ";
                            count2 = 20;
                        } else {
                            hi = "您目前選小 ";
                            count2 = 19;
                        }
                        String[] info2 = {hi + "得獎:" + _tmp._prize, "00", "00", "00", "00", "00", "00", "00"};
                        int random = random();
                        if (random != 0) {
                            int r = (int) (Math.random() * 1000.0D) + 1;
                            if (r < random) {
                                if (_tmp._x_h_i) {
                                    count2 = 19;
                                } else {
                                    count2 = 20;
                                }
                            }
                        }
                        for (int i = 0; i < count2; i++) {
                            TimeUnit.MILLISECONDS.sleep(i * 15);
                            if (i % 2 == 0) {
                                _pc.sendPackets(new S_NPCTalkReturn(_npc.getId(), "bar_18", info2));
                            } else {
                                _pc.sendPackets(new S_NPCTalkReturn(_npc.getId(), "bar_19", info2));
                            }
                        }
                        String htmlid = "";
                        if (_tmp._x_h_i) {
                            switch (count2) {
                                case 19:
                                    _tmp._prize *= 2L;
                                    htmlid = "bar_21";
                                    hi = "您贏了 ";
                                    _tmp._x_h_i_count += 1;
                                    switch (_tmp._x_h_i_count) {
                                        case 1:
                                        case 2:
                                            _pc.sendPacketsX8(new S_SkillSound(_pc.getId(), 7476));
                                            break;
                                        case 3:
                                        case 4:
                                            _pc.sendPacketsX8(new S_SkillSound(_pc.getId(), 7473));
                                    }
                                    break;
                                case 20:
                                    _tmp._prize = 0L;
                                    htmlid = "bar_19";
                                    hi = "您輸了 ";
                                default:
                                    break;
                            }
                        } else if (!_tmp._x_h_i) {
                            switch (count2) {
                                case 19:
                                    _tmp._prize = 0L;
                                    htmlid = "bar_18";
                                    hi = "您輸了 ";
                                    break;
                                case 20:
                                    _tmp._prize *= 2L;
                                    htmlid = "bar_22";
                                    hi = "您贏了 ";
                                    _tmp._x_h_i_count += 1;
                                    switch (_tmp._x_h_i_count) {
                                        case 1:
                                        case 2:
                                            _pc.sendPacketsX8(new S_SkillSound(_pc.getId(), 7476));
                                            break;
                                        case 3:
                                        case 4:
                                            _pc.sendPacketsX8(new S_SkillSound(_pc.getId(), 7473));
                                    }
                                    break;
                            }
                        }
                        if (_tmp._prize > 0L) {
                            if (_tmp._x_h_i_count >= 5) {
                                _tmp._prize *= 2L;
                                _pc.sendPacketsX8(new S_SkillSound(_pc.getId(), 7470));
                                World.get().broadcastPacketToAll(new S_ServerMessage("\\fV恭喜玩家" + _pc.getName() + "比大小過五關"));
                            }
                            Npc_Mary._log.warn("小瑪莉管理員 紀錄：" + _pc.getName() + " 比大小獲勝 金額:" + _tmp._prize + " 次數:" + _tmp._x_h_i_count + " (" + Npc_Mary._count + ")");
                        } else {
                            Npc_Mary._log.warn("小瑪莉管理員 紀錄：" + _pc.getName() + " 比大小失敗 次數:" + _tmp._x_h_i_count + " (" + Npc_Mary._count + ")");
                        }
                        String[] info2_1 = {hi + "得獎:" + _tmp._prize, "00", "00", "00", "00", "00", "00", "00"};
                        _pc.sendPackets(new S_NPCTalkReturn(_npc.getId(), htmlid, info2_1));
                        _pc.sendPackets(new S_ServerMessage("\\fT比大小" + (_tmp._prize > 0L ? "獲勝 獎金:" + _tmp._prize : "失敗") + " (" + Npc_Mary._count + ")"));
                }
            } catch (Exception e) {
                Npc_Mary._log.error(e.getLocalizedMessage(), e);
            } finally {
                _tmp._stop = false;
                if (src_html != -1) {
                    MaryTimer maryTimer = new MaryTimer(_pc, _npc, 1, src_html);
                    maryTimer.startCmd();
                }
            }
        }

        private int random() {
            try {
                long out_prize1 = Npc_Mary._all_stake * Npc_Mary._out_prize / 100L;
                long out_prize2 = out_prize1 - Npc_Mary._all_user_prize;
                if (out_prize2 <= 0L) {
                    if (Npc_Mary._random.nextInt(100) < 5) {
                        return 1000;
                    }
                    return 0;
                }
                if (_tmp._prize << 1 > out_prize2) {
                    return 0;
                }
                int lostRate = (int) ((10000L - (_tmp._prize * 10000L + 50000L) / 10000L) * 30L / 1000L);
                switch (_tmp._x_h_i_count) {
                    case 0:
                        break;
                    case 1:
                        lostRate -= 20;
                        break;
                    case 2:
                        lostRate -= 40;
                        break;
                    case 3:
                        lostRate -= 60;
                        break;
                    case 4:
                        lostRate -= 80;
                        break;
                    case 5:
                        lostRate -= 100;
                }
                if (_tmp._prize << 1 <= out_prize2 / 10L) {
                    return lostRate + 200 + Npc_Mary._random.nextInt(200);
                }
                return lostRate;
            } catch (Exception e) {
                Npc_Mary._log.error(e.getLocalizedMessage(), e);
            }
            return 0;
        }

        private int check(long prize_all) {
            try {
                long out_prize1 = Npc_Mary._all_stake * Npc_Mary._out_prize / 100L;
                long out_prize2 = out_prize1 - Npc_Mary._all_user_prize - prize_all;
                if (out_prize2 <= 0L) {
                    return Npc_Mary._min[Npc_Mary._random.nextInt(Npc_Mary._min.length)];
                }
                boolean is_out = false;
                int html_id = Npc_Mary._min[Npc_Mary._random.nextInt(Npc_Mary._min.length)];
                int count = 0;
                while (!is_out) {
                    count++;
                    html_id = Npc_Mary._random.nextInt(16) + 1;
                    switch (html_id) {
                        case 1:
                            if (_tmp._x_b * Npc_Mary._x_b1 < out_prize2) {
                                is_out = true;
                            } else if (Npc_Mary._random.nextInt(100) <= 2) {
                                is_out = true;
                            }
                            break;
                        case 2:
                            is_out = true;
                            break;
                        case 3:
                            if (_tmp._x_a * Npc_Mary._x_a1 < out_prize2) {
                                if (_tmp._x_a == 99) {
                                    if (Npc_Mary._random.nextInt(100) <= 5) {
                                        is_out = true;
                                    }
                                } else {
                                    is_out = true;
                                }
                            }
                            break;
                        case 4:
                            is_out = true;
                            break;
                        case 5:
                            if (_tmp._x_d * Npc_Mary._x_d1 < out_prize2) {
                                is_out = true;
                            } else if (Npc_Mary._random.nextInt(100) <= 6) {
                                is_out = true;
                            }
                            break;
                        case 6:
                            is_out = true;
                            break;
                        case 7:
                            is_out = true;
                            break;
                        case 8:
                            if (_tmp._x_f * Npc_Mary._x_f1 < out_prize2) {
                                is_out = true;
                            } else if (Npc_Mary._random.nextInt(100) <= 10) {
                                is_out = true;
                            }
                            break;
                        case 9:
                            if (_tmp._x_g * Npc_Mary._x_g1 < out_prize2) {
                                is_out = true;
                            } else if (Npc_Mary._random.nextInt(100) <= 15) {
                                is_out = true;
                            }
                            break;
                        case 10:
                            is_out = true;
                            break;
                        case 11:
                            if (_tmp._x_a * Npc_Mary._x_a2 < out_prize2) {
                                is_out = true;
                            }
                            break;
                        case 12:
                            is_out = true;
                            break;
                        case 13:
                            if (_tmp._x_c * Npc_Mary._x_c1 < out_prize2) {
                                is_out = true;
                            } else if (Npc_Mary._random.nextInt(100) <= 4) {
                                is_out = true;
                            }
                            break;
                        case 14:
                            is_out = true;
                            break;
                        case 15:
                            is_out = true;
                            break;
                        case 16:
                            if (_tmp._x_e * Npc_Mary._x_e1 < out_prize2) {
                                is_out = true;
                            } else if (Npc_Mary._random.nextInt(100) <= 8) {
                                is_out = true;
                            }
                            break;
                    }
                    if (count >= 50) {
                        break;
                    }
                }
                if (is_out) {
                    return html_id;
                }
            } catch (Exception e) {
                Npc_Mary._log.error(e.getLocalizedMessage(), e);
            }
            return Npc_Mary._min[Npc_Mary._random.nextInt(Npc_Mary._min.length)];
        }
    }
}
