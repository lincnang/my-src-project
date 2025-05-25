package com.lineage.data.item_etcitem.dragon;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillMode;
import com.lineage.server.model.skill.skillmode.SkillMode;
import com.lineage.server.serverpackets.S_GmMessage;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.BinaryOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.util.Calendar;

public class DragonSignMagicStone extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(DragonSignMagicStone.class);
    private int _mode = -1;
    private int _skillid = 0;
    private int _gfxid = 0;
    private int _skill_time = -1;
    private int _itemid = 41246;
    private int _count = 0;

    public static ItemExecutor get() {
        return new DragonSignMagicStone();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        try {
            if (item == null) {
                return;
            }
            if (pc == null) {
                return;
            }
            // 取得玩家狀態剩餘時間
            //L1BuffUtil.cancelBuffStone(pc);
            int time = L1BuffUtil.cancelDragonSign(pc);
            if (time != -1) {
                pc.sendPackets(new S_GmMessage(item.getLogName() + "狀態時間還有:" + String.valueOf(time) + "秒"));
                return;
            }
            // 例外狀況:具有時間設置
            boolean isDelayEffect = false;
            final int delayEffect = 1200;// 20分鐘
            // 取得物品最後使用時間
            final Timestamp lastUsed = item.getLastUsed();
            if (lastUsed != null) {
                final Calendar cal = Calendar.getInstance();
                long useTime = (cal.getTimeInMillis() - lastUsed.getTime()) / 1000;
                if (useTime <= delayEffect) {
                    // 轉換為需等待時間
                    useTime = (delayEffect - useTime); /// 60;
                    // 時間數字 轉換為字串
                    final String useTimeS = /* useItem.getLogName() + " " + */ String.valueOf(useTime / 60);
                    // 1139 %0 分鐘之內無法使用。
                    pc.sendPackets(new S_GmMessage(useTimeS + "分鐘內之內無法使用"));
                    return;
                }
            }
            // 物品沒延遲才能使用
            isDelayEffect = true;
            if ((_count > 0) && (_itemid > 0)) {
                if (!pc.getInventory().checkItem(_itemid, _count)) {
                    L1Item temp = ItemTable.get().getTemplate(_itemid);
                    pc.sendPackets(new S_ServerMessage(337, temp.getNameId()));
                    return;
                }
                pc.getInventory().consumeItem(_itemid, _count);
            }
            // 物件使用延遲設置
            if (isDelayEffect) {
                final Timestamp ts = new Timestamp(System.currentTimeMillis());
                // 設置使用時間
                item.setLastUsed(ts);
                pc.getInventory().updateItem(item, L1PcInventory.COL_DELAY_EFFECT);
                pc.getInventory().saveItem(item, L1PcInventory.COL_DELAY_EFFECT);
            }
            if (_gfxid > 0) {
                pc.sendPacketsX8(new S_SkillSound(pc.getId(), _gfxid));
            }
            SkillMode mode = L1SkillMode.get().getSkill(_skillid);
            if (mode != null) {
                mode.start(pc, null, null, _skill_time);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void set_set(String[] set) {
        try {
            _mode = Integer.parseInt(set[1]);
            switch (_mode) {
                case 0:
                    _skillid = 4500;
                    break;
                case 1:
                    _skillid = 4501;
                    break;
                case 2:
                    _skillid = 4502;
                    break;
                case 3:
                    _skillid = 4503;
                    break;
                case 4:
                    _skillid = 4504;
                    break;
                case 5:
                    _skillid = 4505;
                    break;
                case 6:
                    _skillid = 4506;
                    break;
                case 7:
                    _skillid = 4507;
                    break;
                case 8:
                    _skillid = 4508;
                    break;
                case 9:
                    _skillid = 4509;
                    break;
                case 10:
                    _skillid = 4510;
                    break;
                case 11:
                    _skillid = 4511;
                    break;
                case 12:
                    _skillid = 4512;
                    break;
                case 13:
                    _skillid = 4513;
                    break;
                case 14:
                    _skillid = 4514;
                    break;
                case 15:
                    _skillid = 4515;
                    break;
                case 16:
                    _skillid = 4516;
                    break;
                case 17:
                    _skillid = 4517;
                    break;
                case 18:
                    _skillid = 4518;
                    break;
                case 19:
                    _skillid = 4519;
                    break;
                case 20:
                    _skillid = 4520;
                    break;
                case 21:
                    _skillid = 4521;
                    break;
                case 22:
                    _skillid = 4522;
                    break;
                case 23:
                    _skillid = 4523;
                    break;
                case 24:
                    _skillid = 4524;
                    break;
                case 25:
                    _skillid = 4525;
                    break;
                case 26:
                    _skillid = 4526;
                    break;
                case 27:
                    _skillid = 4527;
                    break;
                case 28:
                    _skillid = 4528;
                    break;
                case 29:
                    _skillid = 4529;
                    break;
                case 30:
                    _skillid = 4530;
                    break;
                case 31:
                    _skillid = 4531;
                    break;
                case 32:
                    _skillid = 4532;
                    break;
                case 33:
                    _skillid = 4533;
                    break;
                case 34:
                    _skillid = 4534;
                    break;
                case 35:
                    _skillid = 4535;
                    break;
                case 36:
                    _skillid = 4536;
                    break;
                case 37:
                    _skillid = 4537;
                    break;
                case 38:
                    _skillid = 4538;
                    break;
                case 39:
                    _skillid = 4539;
            }
        } catch (Exception localException) {
        }
        try {
            _skill_time = Integer.parseInt(set[2]);
        } catch (Exception localException1) {
        }
        try {
            _gfxid = Integer.parseInt(set[3]);
        } catch (Exception localException2) {
        }
        try {
            _itemid = Integer.parseInt(set[4]);
        } catch (Exception localException3) {
        }
        try {
            _count = Integer.parseInt(set[5]);
        } catch (Exception localException4) {
        }
    }

    public BinaryOutputStream itemStatus(L1ItemInstance item) {
        BinaryOutputStream os = new BinaryOutputStream();
        os.writeC(23);
        os.writeC(item.getItem().getMaterial());
        os.writeD(item.getWeight());
        switch (_mode) {
            case 0:
                os.writeC(14);
                os.writeH(10);
                break;
            case 1:
                os.writeC(14);
                os.writeH(20);
                break;
            case 2:
                os.writeC(14);
                os.writeH(30);
                break;
            case 3:
                os.writeC(14);
                os.writeH(40);
                break;
            case 4:
                os.writeC(14);
                os.writeH(50);
                break;
            case 5:
                os.writeC(14);
                os.writeH(50);
                os.writeC(5);
                os.writeC(1);
                break;
            case 6:
                os.writeC(14);
                os.writeH(70);
                os.writeC(5);
                os.writeC(1);
                os.writeC(39);
                os.writeS("$5539 +1");
                break;
            case 7:
                os.writeC(14);
                os.writeH(70);
                os.writeC(5);
                os.writeC(1);
                os.writeC(6);
                os.writeC(1);
                os.writeC(39);
                os.writeS("$5539 +2");
                break;
            case 8:
                os.writeC(14);
                os.writeH(90);
                os.writeC(5);
                os.writeC(2);
                os.writeC(6);
                os.writeC(1);
                os.writeC(39);
                os.writeS("$5539 +3");
                break;
            case 9:
                os.writeC(14);
                os.writeH(120);
                os.writeC(5);
                os.writeC(3);
                os.writeC(6);
                os.writeC(3);
                os.writeC(8);
                os.writeC(1);
                os.writeC(39);
                os.writeS("$5539 +6");
                break;
            case 10:
                os.writeC(14);
                os.writeH(5);
                os.writeC(32);
                os.writeC(3);
                break;
            case 11:
                os.writeC(14);
                os.writeH(10);
                os.writeC(32);
                os.writeC(6);
                break;
            case 12:
                os.writeC(14);
                os.writeH(15);
                os.writeC(32);
                os.writeC(9);
                break;
            case 13:
                os.writeC(14);
                os.writeH(20);
                os.writeC(32);
                os.writeC(12);
                break;
            case 14:
                os.writeC(14);
                os.writeH(25);
                os.writeC(32);
                os.writeC(15);
                break;
            case 15:
                os.writeC(14);
                os.writeH(25);
                os.writeC(32);
                os.writeC(15);
                os.writeC(24);
                os.writeC(1);
                break;
            case 16:
                os.writeC(14);
                os.writeH(35);
                os.writeC(32);
                os.writeC(20);
                os.writeC(24);
                os.writeC(1);
                os.writeC(39);
                os.writeS("$5539 +1");
                break;
            case 17:
                os.writeC(14);
                os.writeH(35);
                os.writeC(32);
                os.writeC(22);
                os.writeC(24);
                os.writeC(1);
                os.writeC(35);
                os.writeC(1);
                os.writeC(39);
                os.writeS("$5539 +1");
                break;
            case 18:
                os.writeC(14);
                os.writeH(40);
                os.writeC(32);
                os.writeC(30);
                os.writeC(24);
                os.writeC(2);
                os.writeC(35);
                os.writeC(1);
                os.writeC(39);
                os.writeS("$5539 +2");
                os.writeC(39);
                os.writeS("$5541 +2");
                break;
            case 19:
                os.writeC(14);
                os.writeH(60);
                os.writeC(32);
                os.writeC(43);
                os.writeC(24);
                os.writeC(3);
                os.writeC(35);
                os.writeC(3);
                os.writeC(9);
                os.writeC(1);
                os.writeC(39);
                os.writeS("$5539 +3");
                os.writeC(39);
                os.writeS("$5541 +3");
                break;
            case 20:
                os.writeC(32);
                os.writeC(10);
                break;
            case 21:
                os.writeC(32);
                os.writeC(20);
                break;
            case 22:
                os.writeC(32);
                os.writeC(30);
                break;
            case 23:
                os.writeC(32);
                os.writeC(40);
                break;
            case 24:
                os.writeC(32);
                os.writeC(50);
                break;
            case 25:
                os.writeC(32);
                os.writeC(50);
                os.writeC(39);
                os.writeS("$5541 +1");
                break;
            case 26:
                os.writeC(32);
                os.writeC(55);
                os.writeC(39);
                os.writeS("$5541 +1");
                break;
            case 27:
                os.writeC(32);
                os.writeC(55);
                os.writeC(17);
                os.writeC(1);
                os.writeC(39);
                os.writeS("$5541 +2");
                break;
            case 28:
                os.writeC(32);
                os.writeC(60);
                os.writeC(17);
                os.writeC(1);
                os.writeC(39);
                os.writeS("$5541 +3");
                break;
            case 29:
                os.writeC(32);
                os.writeC(70);
                os.writeC(17);
                os.writeC(3);
                os.writeC(12);
                os.writeC(1);
                os.writeC(39);
                os.writeS("$5541 +5");
                break;
            case 30:
                os.writeC(39);
                os.writeS("$5569 +1");
                break;
            case 31:
                os.writeC(39);
                os.writeS("$5569 +2");
                break;
            case 32:
                os.writeC(39);
                os.writeS("$5569 +3");
                break;
            case 33:
                os.writeC(39);
                os.writeS("$5569 +4");
                break;
            case 34:
                os.writeC(39);
                os.writeS("$5569 +5");
                break;
            case 35:
                os.writeC(39);
                os.writeS("$5569 +5");
                os.writeC(39);
                os.writeS("$5538 +1");
                break;
            case 36:
                os.writeC(39);
                os.writeS("$5569 +5");
                os.writeC(39);
                os.writeS("$5538 +6");
                break;
            case 37:
                os.writeC(39);
                os.writeS("$5569 +5");
                os.writeC(39);
                os.writeS("$5538 +9");
                break;
            case 38:
                os.writeC(39);
                os.writeS("$5569 +5");
                os.writeC(39);
                os.writeS("$5538 +15");
                os.writeC(39);
                os.writeS("傷害減免 +2");
                os.writeC(33);
                os.writeC(214);
                os.writeC(15);
                os.writeH(2);
                os.writeC(33);
                os.writeC(5);
                break;
            case 39:
                os.writeC(39);
                os.writeS("$5569 +5");
                os.writeC(39);
                os.writeS("$5538 +21");
                os.writeC(39);
                os.writeS("傷害減免 +4");
                os.writeC(10);
                os.writeC(1);
                os.writeC(33);
                os.writeC(214);
                os.writeC(15);
                os.writeH(5);
                os.writeC(33);
                os.writeC(5);
        }
        return os;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.dragon.DragonSignMagicStone JD-Core Version:
 * 0.6.2
 */