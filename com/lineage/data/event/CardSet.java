package com.lineage.data.event;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.templates.L1Event;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CardSet extends EventExecutor {
    private static final Log _log = LogFactory.getLog(CardSet.class);
    public static boolean START = false;
    public static int USE_TIME;
    public static int USE_TIME2;

    public static EventExecutor get() {
        return new CardSet();
    }

    public static void load_card_mode(L1PcInstance pc) {
        try {
            for (L1ItemInstance item : pc.getInventory().getItems()) {
                String classname = item.getItem().getclassname();
                if (classname.startsWith("shop.VIP_Card_")) {
                    if (item.get_card_use() != 1) {
                        item.setEquipped(false);
                    } else {
                        int card_id;
                        try {
                            String cardmode = classname.substring(14);
                            card_id = Integer.parseInt(cardmode);
                        } catch (Exception e) {
                            String cardmode = classname.substring(15);
                            card_id = Integer.parseInt(cardmode);
                        }
                        if (card_id == 0) {
                            return;
                        }
                        item.setEquipped(true);
                        switch (card_id) {
                            case 1:
                                pc.set_VIP1(true);
                                pc.set_expadd(10);
                                pc.addRegistStun(3);
                                pc.add_regist_freeze(3);
                                pc.addRegistStone(3);
                                pc.addRegistSleep(3);
                                break;
                            case 2:
                                pc.set_VIP2(true);
                                pc.set_expadd(20);
                                pc.addStr(1);
                                pc.addDex(1);
                                pc.addCon(1);
                                pc.addWis(1);
                                pc.addInt(1);
                                pc.addCha(1);
                                break;
                            case 3:
                                pc.set_VIP3(true);
                                pc.set_expadd(30);
                                pc.addStr(2);
                                pc.addDex(2);
                                pc.addCon(2);
                                pc.addWis(2);
                                pc.addInt(2);
                                pc.addCha(2);
                                break;
                            case 4:
                                pc.set_expadd(40);
                                pc.addStr(3);
                                pc.addDex(3);
                                pc.addCon(3);
                                pc.addWis(3);
                                pc.addInt(3);
                                pc.addCha(3);
                                pc.addHpr(5);
                                pc.addMpr(5);
                                break;
                            case 5:
                                pc.set_VIP4(true);
                                pc.set_expadd(50);
                                pc.addStr(4);
                                pc.addDex(4);
                                pc.addCon(4);
                                pc.addWis(4);
                                pc.addInt(4);
                                pc.addCha(4);
                                pc.addHpr(10);
                                pc.addMpr(10);
                        }
                        pc.sendPackets(new S_OwnCharStatus(pc));
                        pc.sendPackets(new S_SPMR(pc));
                    }
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public static void set_card_mode(L1PcInstance pc, L1ItemInstance item) {
        if (!START) {
            return;
        }
        try {
            String classname = item.getItem().getclassname();
            if (!classname.startsWith("shop.VIP_Card_")) {
                return;
            }
            int card_id = 0;
            try {
                String cardmode = classname.substring(14);
                card_id = Integer.parseInt(cardmode);
            } catch (Exception e) {
                String cardmode = classname.substring(15);
                card_id = Integer.parseInt(cardmode);
            }
            if (card_id == 0) {
                return;
            }
            item.setEquipped(true);
            switch (card_id) {
                case 1:
                    pc.set_VIP1(true);
                    pc.set_expadd(10);
                    pc.addRegistStun(3);
                    pc.add_regist_freeze(3);
                    pc.addRegistStone(3);
                    pc.addRegistSleep(3);
                    break;
                case 2:
                    pc.set_VIP2(true);
                    pc.set_expadd(20);
                    pc.addStr(1);
                    pc.addDex(1);
                    pc.addCon(1);
                    pc.addWis(1);
                    pc.addInt(1);
                    pc.addCha(1);
                    break;
                case 3:
                    pc.set_VIP3(true);
                    pc.set_expadd(30);
                    pc.addStr(2);
                    pc.addDex(2);
                    pc.addCon(2);
                    pc.addWis(2);
                    pc.addInt(2);
                    pc.addCha(2);
                    break;
                case 4:
                    pc.set_expadd(40);
                    pc.addStr(3);
                    pc.addDex(3);
                    pc.addCon(3);
                    pc.addWis(3);
                    pc.addInt(3);
                    pc.addCha(3);
                    pc.addHpr(5);
                    pc.addMpr(5);
                    break;
                case 5:
                    pc.set_VIP4(true);
                    pc.set_expadd(50);
                    pc.addStr(4);
                    pc.addDex(4);
                    pc.addCon(4);
                    pc.addWis(4);
                    pc.addInt(4);
                    pc.addCha(4);
                    pc.addHpr(10);
                    pc.addMpr(10);
            }
            pc.sendPackets(new S_OwnCharStatus(pc));
            pc.sendPackets(new S_SPMR(pc));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public static void remove_card_mode(L1PcInstance pc, L1ItemInstance item) {
        if (!START) {
            return;
        }
        try {
            String classname = item.getItem().getclassname();
            if (!classname.startsWith("shop.VIP_Card_")) {
                return;
            }
            int card_id = 0;
            try {
                String cardmode = classname.substring(14);
                card_id = Integer.parseInt(cardmode);
            } catch (Exception e) {
                String cardmode = classname.substring(15);
                card_id = Integer.parseInt(cardmode);
            }
            if (card_id == 0) {
                return;
            }
            item.setEquipped(false);
            switch (card_id) {
                case 1:
                    pc.set_VIP1(false);
                    pc.set_expadd(-10);
                    pc.addRegistStun(-3);
                    pc.add_regist_freeze(-3);
                    pc.addRegistStone(-3);
                    pc.addRegistSleep(-3);
                    break;
                case 2:
                    pc.set_VIP2(false);
                    pc.set_expadd(-20);
                    pc.addStr(-1);
                    pc.addDex(-1);
                    pc.addCon(-1);
                    pc.addWis(-1);
                    pc.addInt(-1);
                    pc.addCha(-1);
                    break;
                case 3:
                    pc.set_VIP3(false);
                    pc.set_expadd(-30);
                    pc.addStr(-2);
                    pc.addDex(-2);
                    pc.addCon(-2);
                    pc.addWis(-2);
                    pc.addInt(-2);
                    pc.addCha(-2);
                    break;
                case 4:
                    pc.set_expadd(-40);
                    pc.addStr(-3);
                    pc.addDex(-3);
                    pc.addCon(-3);
                    pc.addWis(-3);
                    pc.addInt(-3);
                    pc.addCha(-3);
                    pc.addHpr(-5);
                    pc.addMpr(-5);
                    break;
                case 5:
                    pc.set_VIP4(false);
                    pc.set_expadd(-50);
                    pc.addStr(-4);
                    pc.addDex(-4);
                    pc.addCon(-4);
                    pc.addWis(-4);
                    pc.addInt(-4);
                    pc.addCha(-4);
                    pc.addHpr(-10);
                    pc.addMpr(-10);
            }
            pc.sendPackets(new S_OwnCharStatus(pc));
            pc.sendPackets(new S_SPMR(pc));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void execute(L1Event event) {
        try {
            START = true;
            String[] set = event.get_eventother().split(",");
            try {
                USE_TIME = Integer.parseInt(set[0]);
            } catch (Exception e) {
                USE_TIME = 720;
                _log.error("未設定月卡使用期限(使用預設720小時)");
            }
            try {
                USE_TIME2 = Integer.parseInt(set[1]);
            } catch (Exception e) {
                USE_TIME2 = 24;
                _log.error("未設定日卡使用期限(使用預設24小時)");
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.event.CardSet JD-Core Version: 0.6.2
 */