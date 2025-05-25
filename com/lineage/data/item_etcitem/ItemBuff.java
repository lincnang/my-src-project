package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_MPUpdate;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ItemBuff extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(ItemBuff.class);
    private int _str = 0;
    private int _dex = 0;
    private int _int = 0;
    private int _con = 0;
    private int _wis = 0;
    private int _cha = 0;
    private int _magic = 0;
    private int _damage = 0;
    private int _range = 0;
    private int _hit = 0;
    private int _hp = 0;
    private int _mp = 0;
    private int _time = 0;

    public static ItemExecutor get() {
        return new ItemBuff();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        try {
            if (item == null) {
                return;
            }
            if (pc == null) {
                return;
            }
            if (pc.hasSkillEffect(item.getItem().getItemId())) {
                pc.sendPackets(new S_SystemMessage(item.getName() + " 效果正在使用中"));
                return;
            }
            if (!pc.hasSkillEffect(item.getItem().getItemId())) {
                pc.setSkillEffect(item.getItem().getItemId(), this._time * 1000);
                pc.addStr(this._str);
                pc.addDex(this._dex);
                pc.addInt(this._int);
                pc.addCon(this._con);
                pc.addWis(this._wis);
                pc.addCha(this._cha);
                pc.add_magic_plus(this._magic);
                pc.add_damage_plus(this._damage);
                pc.add_range_plus(this._range);
                pc.addHitup(this._hit);
                pc.addMaxHp(this._hp);
                pc.addMaxMp(this._mp);
                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                pc.sendPackets(new S_OwnCharStatus(pc));
                String s1 = "";
                String s2 = "";
                String s3 = "";
                String s4 = "";
                String s5 = "";
                String s6 = "";
                String s7 = "";
                String s8 = "";
                String s9 = "";
                String s10 = "";
                String s11 = "";
                String s12 = "";
                String s0 = "";
                if (this._time > 0) {
                    s0 = this._time + "秒內 ";
                }
                if (this._str > 0) {
                    s1 = " 力量+" + this._str;
                }
                if (this._dex > 0) {
                    s2 = " 敏捷+" + this._dex;
                }
                if (this._int > 0) {
                    s3 = " 智慧+" + this._int;
                }
                if (this._con > 0) {
                    s4 = " 體質+" + this._con;
                }
                if (this._wis > 0) {
                    s5 = " 精神+" + this._wis;
                }
                if (this._cha > 0) {
                    s6 = " 魅力+" + this._cha;
                }
                if (this._magic > 0) {
                    s7 = " 魔法傷害+" + this._magic;
                }
                if (this._damage > 0) {
                    pc.add_damage_plus(this._damage);
                    s8 = " 物理傷害+" + this._damage;
                }
                if (this._range > 0) {
                    s9 = " 遠距離+" + this._range;
                }
                if (this._hit > 0) {
                    s10 = " 命中+" + this._hit;
                }
                if (this._hp > 0) {
                    s11 = " HP+" + this._hp;
                }
                if (this._mp > 0) {
                    s12 = " MP+" + this._mp;
                }
                pc.sendPackets(new S_SystemMessage(s0 + s1 + s2 + s3 + s4 + s5 + s6 + s7 + s8 + s9 + s10 + s11 + s12));
                pc.getInventory().removeItem(item, 1L);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void set_set(String[] set) {
        try {
            this._str = Integer.parseInt(set[1]);
            this._dex = Integer.parseInt(set[2]);
            this._int = Integer.parseInt(set[3]);
            this._con = Integer.parseInt(set[4]);
            this._wis = Integer.parseInt(set[5]);
            this._cha = Integer.parseInt(set[6]);
            this._magic = Integer.parseInt(set[7]);
            this._damage = Integer.parseInt(set[8]);
            this._range = Integer.parseInt(set[9]);
            this._hit = Integer.parseInt(set[10]);
            this._hp = Integer.parseInt(set[11]);
            this._mp = Integer.parseInt(set[12]);
            this._time = Integer.parseInt(set[13]);
        } catch (Exception e) {
        }
    }
}
