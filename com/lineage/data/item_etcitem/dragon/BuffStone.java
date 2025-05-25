package com.lineage.data.item_etcitem.dragon;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillMode;
import com.lineage.server.model.skill.skillmode.SkillMode;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.BinaryOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;

public class BuffStone extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(BuffStone.class);
    private int _mode = -1;
    private int _skillid = 0;
    private int _gfxid = 0;
    private int _skill_time = -1;
    private int _itemid = 41246;
    private int _count = 0;

    public static ItemExecutor get() {
        return new BuffStone();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        try {
            if (item == null) {
                return;
            }
            if (pc == null) {
                return;
            }
            int time = L1BuffUtil.cancelDragonSign(pc);
            if (time != -1) {
                pc.sendPackets(new S_ServerMessage(1139, item.getLogName() + " " + String.valueOf(time / 60)));
                return;
            }
            if ((_count > 0) && (_itemid > 0)) {
                if (!pc.getInventory().checkItem(_itemid, _count)) {
                    L1Item temp = ItemTable.get().getTemplate(_itemid);
                    pc.sendPackets(new S_ServerMessage(337, temp.getNameId()));
                    return;
                }
                pc.getInventory().consumeItem(_itemid, _count);
            }
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            item.setLastUsed(ts);
            pc.getInventory().updateItem(item, 32);
            pc.getInventory().saveItem(item, 32);
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
                case 1:
                    _skillid = 4401;
                    break;
                case 2:
                    _skillid = 4402;
                    break;
                case 3:
                    _skillid = 4403;
                    break;
                case 4:
                    _skillid = 4404;
                    break;
                case 5:
                    _skillid = 4405;
                    break;
                case 6:
                    _skillid = 4406;
                    break;
                case 7:
                    _skillid = 4407;
                    break;
                case 8:
                    _skillid = 4408;
                    break;
                case 9:
                    _skillid = 4409;
                    break;
                case 11:
                    _skillid = 4411;
                    break;
                case 12:
                    _skillid = 4412;
                    break;
                case 13:
                    _skillid = 4413;
                    break;
                case 14:
                    _skillid = 4414;
                    break;
                case 15:
                    _skillid = 4415;
                    break;
                case 16:
                    _skillid = 4416;
                    break;
                case 17:
                    _skillid = 4417;
                    break;
                case 18:
                    _skillid = 4418;
                    break;
                case 19:
                    _skillid = 4419;
                    break;
                case 21:
                    _skillid = 4421;
                    break;
                case 22:
                    _skillid = 4422;
                    break;
                case 23:
                    _skillid = 4423;
                    break;
                case 24:
                    _skillid = 4424;
                    break;
                case 25:
                    _skillid = 4425;
                    break;
                case 26:
                    _skillid = 4426;
                    break;
                case 27:
                    _skillid = 4427;
                    break;
                case 28:
                    _skillid = 4428;
                    break;
                case 29:
                    _skillid = 4429;
                    break;
                case 31:
                    _skillid = 4431;
                    break;
                case 32:
                    _skillid = 4432;
                    break;
                case 33:
                    _skillid = 4433;
                    break;
                case 34:
                    _skillid = 4434;
                    break;
                case 35:
                    _skillid = 4435;
                    break;
                case 36:
                    _skillid = 4436;
                    break;
                case 37:
                    _skillid = 4437;
                    break;
                case 38:
                    _skillid = 4438;
                    break;
                case 39:
                    _skillid = 4439;
                case 10:
                case 20:
                case 30:
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
            case 1:
                os.writeC(14);
                os.writeH(10);
                break;
            case 2:
                os.writeC(14);
                os.writeH(20);
                break;
            case 3:
                os.writeC(14);
                os.writeH(30);
                break;
            case 4:
                os.writeC(14);
                os.writeH(40);
                break;
            case 5:
                os.writeC(14);
                os.writeH(50);
                os.writeC(39);
                os.writeS("$5539 +1");
                break;
            case 6:
                os.writeC(14);
                os.writeH(50);
                os.writeC(39);
                os.writeS("$5539 +2");
                break;
            case 7:
                os.writeC(14);
                os.writeH(70);
                os.writeC(39);
                os.writeS("$5539 +3");
                break;
            case 8:
                os.writeC(14);
                os.writeH(80);
                os.writeC(5);
                os.writeC(1);
                os.writeC(39);
                os.writeS("$5539 +4");
                break;
            case 9:
                os.writeC(14);
                os.writeH(100);
                os.writeC(5);
                os.writeC(2);
                os.writeC(6);
                os.writeC(2);
                os.writeC(8);
                os.writeC(1);
                os.writeC(39);
                os.writeS("$5539 +5");
                break;
            case 11:
                os.writeC(14);
                os.writeH(5);
                os.writeC(32);
                os.writeC(3);
                break;
            case 12:
                os.writeC(14);
                os.writeH(10);
                os.writeC(32);
                os.writeC(6);
                break;
            case 13:
                os.writeC(14);
                os.writeH(15);
                os.writeC(32);
                os.writeC(10);
                break;
            case 14:
                os.writeC(14);
                os.writeH(20);
                os.writeC(32);
                os.writeC(15);
                break;
            case 15:
                os.writeC(14);
                os.writeH(25);
                os.writeC(32);
                os.writeC(20);
                break;
            case 16:
                os.writeC(14);
                os.writeH(30);
                os.writeC(32);
                os.writeC(20);
                os.writeC(39);
                os.writeS("$5539 +1");
                break;
            case 17:
                os.writeC(14);
                os.writeH(35);
                os.writeC(32);
                os.writeC(20);
                os.writeC(39);
                os.writeS("$5539 +1");
                os.writeC(39);
                os.writeS("$5541 +1");
                break;
            case 18:
                os.writeC(14);
                os.writeH(40);
                os.writeC(32);
                os.writeC(25);
                os.writeC(39);
                os.writeS("$5539 +2");
                os.writeC(39);
                os.writeS("$5541 +1");
                break;
            case 19:
                os.writeC(14);
                os.writeH(50);
                os.writeC(32);
                os.writeC(30);
                os.writeC(24);
                os.writeC(2);
                os.writeC(35);
                os.writeC(2);
                os.writeC(9);
                os.writeC(1);
                os.writeC(39);
                os.writeS("$5539 +2");
                os.writeC(39);
                os.writeS("$5541 +2");
                break;
            case 21:
                os.writeC(32);
                os.writeC(5);
                break;
            case 22:
                os.writeC(32);
                os.writeC(10);
                break;
            case 23:
                os.writeC(32);
                os.writeC(15);
                break;
            case 24:
                os.writeC(32);
                os.writeC(20);
                break;
            case 25:
                os.writeC(32);
                os.writeC(25);
                os.writeC(39);
                os.writeS("$5541 +1");
                break;
            case 26:
                os.writeC(32);
                os.writeC(30);
                os.writeC(39);
                os.writeS("$5541 +2");
                break;
            case 27:
                os.writeC(32);
                os.writeC(35);
                os.writeC(39);
                os.writeS("$5541 +3");
                break;
            case 28:
                os.writeC(32);
                os.writeC(40);
                os.writeC(39);
                os.writeS("$5541 +4");
                break;
            case 29:
                os.writeC(32);
                os.writeC(50);
                os.writeC(17);
                os.writeC(1);
                os.writeC(12);
                os.writeC(1);
                os.writeC(39);
                os.writeS("$5541 +5");
                break;
            case 31:
                os.writeC(39);
                os.writeS("$5538 +2");
                break;
            case 32:
                os.writeC(39);
                os.writeS("$5538 +4");
                break;
            case 33:
                os.writeC(39);
                os.writeS("$5538 +6");
                break;
            case 34:
                os.writeC(39);
                os.writeS("$5538 +8");
                break;
            case 35:
                os.writeC(39);
                os.writeS("$5569 +1");
                os.writeC(39);
                os.writeS("$5538 +10");
                break;
            case 36:
                os.writeC(39);
                os.writeS("$5569 +2");
                os.writeC(39);
                os.writeS("$5538 +10");
                break;
            case 37:
                os.writeC(39);
                os.writeS("$5569 +3");
                os.writeC(39);
                os.writeS("$5538 +10");
                break;
            case 38:
                os.writeC(39);
                os.writeS("$5569 +4");
                os.writeC(39);
                os.writeS("$5538 +15");
                os.writeC(39);
                os.writeS("傷害減免 +1");
                break;
            case 39:
                os.writeC(39);
                os.writeS("$5569 +5");
                os.writeC(39);
                os.writeS("$5538 +20");
                os.writeC(39);
                os.writeS("傷害減免 +3");
                os.writeC(10);
                os.writeC(1);
            case 10:
            case 20:
            case 30:
        }
        return os;
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.dragon.BuffStone JD-Core Version: 0.6.2
 */