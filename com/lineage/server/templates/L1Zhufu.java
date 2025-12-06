/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.lineage.server.templates;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.Zhufu;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.*;

/**
 * 祝福化系統
 * 台灣JAVA技術老爹 by
 *
 * @author Administrator
 */
public class L1Zhufu {
    private int _id;
    private int _kind;
    private int _itemid;
    private int _type;
    private boolean _zhufu;
    private boolean _zhufu2;
    private byte _addStr;
    private byte _addDex;
    private byte _addCon;
    private byte _addInt;
    private byte _addWis;
    private byte _addCha;
    private int _addAc;
    private int _addMaxHp;
    private int _addMaxMp;
    private int _addHpr;
    private int _addMpr;
    private int _addDmg;
    private int _addBowDmg;
    private int _addHit;
    private int _addBowHit;
    private int _reduction_dmg;
    private int _reduction_magic_dmg;
    private int _addMr;
    private int _addSp;
    private int _addFire;
    private int _addWind;
    private int _addEarth;
    private int _addWater;
    private double _addExp;
    private int _addwljm;
    private int _addwljmbai;
    private int _addmojm;
    private int _addmojmbai;

    public L1Zhufu(int id, int kind, int itemid, int type, boolean zhufu, boolean zhufu2, byte addStr, byte addDex, byte addCon, byte addInt, byte addWis, byte addCha,
                   int addAc, int addMaxHp, int addMaxMp, int addHpr, int addMpr, int addDmg, int addBowDmg, int addHit, int addBowHit, int reduction_dmg, int reduction_magic_dmg,
                   int addMr, int addSp, int addFire, int addWind, int addEarth, int addWater, double addExp, int addwljm, int addwljmbai, int addmojm, int addmojmbai) {
        _id = id;
        _kind = kind;
        _itemid = itemid;
        _type = type;
        _zhufu = zhufu;
        _zhufu2 = zhufu2;
        _addStr = addStr;
        _addDex = addDex;
        _addCon = addCon;
        _addInt = addInt;
        _addWis = addWis;
        _addCha = addCha;
        _addAc = addAc;
        _addMaxHp = addMaxHp;
        _addMaxMp = addMaxMp;
        _addHpr = addHpr;
        _addMpr = addMpr;
        _addDmg = addDmg;
        _addBowDmg = addBowDmg;
        _addHit = addHit;
        _addBowHit = addBowHit;
        _reduction_dmg = reduction_dmg;
        _reduction_magic_dmg = reduction_magic_dmg;
        _addMr = addMr;
        _addSp = addSp;
        _addFire = addFire;
        _addWind = addWind;
        _addEarth = addEarth;
        _addWater = addWater;
        _addExp = addExp;
        _addwljm = addwljm;
        _addwljmbai = addwljmbai;
        _addmojm = addmojm;
        _addmojmbai = addmojmbai;
    }

    // 祝福化附加能力
    public static void getAddZhufu(L1PcInstance pc, int itemid, int kind) {
        L1Item item = ItemTable.get().getTemplate(itemid);
        if (item == null) {
            return;
        }

        // 只查詢精確匹配，不使用TYPE通用匹配
        L1Zhufu zhufuhua = Zhufu.getInstance().getTemplate(itemid, kind);

        if (zhufuhua != null) {
            if (zhufuhua.getAddStr() != 0) { // 力量
                pc.addStr(zhufuhua.getAddStr());
            }
            if (zhufuhua.getAddDex() != 0) { // 敏捷
                pc.addDex(zhufuhua.getAddDex());
            }
            if (zhufuhua.getAddCon() != 0) { // 體質
                pc.addCon(zhufuhua.getAddCon());
            }
            if (zhufuhua.getAddInt() != 0) { // 智力
                pc.addInt(zhufuhua.getAddInt());
            }
            if (zhufuhua.getAddWis() != 0) { // 精神
                pc.addWis(zhufuhua.getAddWis());
            }
            if (zhufuhua.getAddCha() != 0) { // 魅力
                pc.addCha(zhufuhua.getAddCha());
            }
            if (zhufuhua.getAddAc() != 0) { // 防禦
                pc.addAc(-zhufuhua.getAddAc());
            }
            if (zhufuhua.getAddMaxHp() != 0) { // HP
                pc.addMaxHp(zhufuhua.getAddMaxHp());
                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            }
            if (zhufuhua.getAddMaxMp() != 0) { // MP
                pc.addMaxMp(zhufuhua.getAddMaxMp());
                pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
            }
            if (zhufuhua.getAddHpr() != 0) { // 回血
                pc.addHpr(zhufuhua.getAddHpr());
                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            }
            if (zhufuhua.getAddMpr() != 0) { // 回魔
                pc.addMpr(zhufuhua.getAddMpr());
                pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
            }
            if (zhufuhua.getAddDmg() != 0) { // 進戰傷害
                pc.addDmgup(zhufuhua.getAddDmg());
            }
            if (zhufuhua.getAddHit() != 0) { // 進戰命中
                pc.addHitup(zhufuhua.getAddHit());
            }
            if (zhufuhua.getAddBowDmg() != 0) { // 遠戰傷害
                pc.addBowDmgup(zhufuhua.getAddBowDmg());
            }
            if (zhufuhua.getAddBowHit() != 0) { // 遠戰命中
                pc.addBowHitup(zhufuhua.getAddBowHit());
            }
            if (zhufuhua.getReduction_dmg() != 0) { // 減免物理傷害
                pc.addDamageReductionByArmor(zhufuhua.getReduction_dmg());
            }
            /*if (gfxIdOrginal.getReduction_magic_dmg() != 0) { // 減免魔法傷害
                pc.addMagicReductionDmgByA(gfxIdOrginal.getReduction_magic_dmg());
            }*/
            if (zhufuhua.getAddMr() != 0) { // 抗魔
                pc.addMr(zhufuhua.getAddMr());
            }
            if (zhufuhua.getAddSp() != 0) { // 魔攻
                pc.addSp(zhufuhua.getAddSp());
            }
            if (zhufuhua.getAddFire() != 0) { // 抗火屬性
                pc.addFire(zhufuhua.getAddFire());
            }
            if (zhufuhua.getAddWind() != 0) { // 抗風屬性
                pc.addWind(zhufuhua.getAddWind());
            }
            if (zhufuhua.getAddEarth() != 0) { // 抗地屬性
                pc.addEarth(zhufuhua.getAddEarth());
            }
            if (zhufuhua.getAddWater() != 0) { // 抗水屬性
                pc.addWater(zhufuhua.getAddWater());
            }
            if (zhufuhua.getwljm() != 0) {//PVP物理減免
                pc.setZhufuPvp(zhufuhua.getwljm());
            }
            if (zhufuhua.getwljmbai() != 0) {//PVP物理減免百分比
                pc.setzhufuPvpbai(zhufuhua.getwljmbai());
            }
            if (zhufuhua.getmojm() != 0) {//PVP魔法傷害減免
                pc.setzhufuMoPvp(zhufuhua.getmojm());
            }
            if (zhufuhua.getmojmbai() != 0) {//PVP魔法傷害減免百分比
                pc.setzhufuMoPvpbai(zhufuhua.getmojmbai());
            }
            pc.sendPackets(new S_SPMR(pc));
            pc.sendPackets(new S_OwnCharStatus(pc));
            pc.sendPackets(new S_OwnCharStatus2(pc));
        }
    }

    // 變身編號效果移除附加的能力
    public static void getRedzhufuhua(L1PcInstance pc, int itemid, int kind) {
        L1Item item = ItemTable.get().getTemplate(itemid);
        if (item == null) {
            return;
        }

        // 只查詢精確匹配，不使用TYPE通用匹配
        L1Zhufu zhufuhua = Zhufu.getInstance().getTemplate(itemid, kind);

        if (zhufuhua != null) {
            if (zhufuhua.getAddStr() != 0) { // 力量
                pc.addStr(-zhufuhua.getAddStr());
            }
            if (zhufuhua.getAddDex() != 0) { // 敏捷
                pc.addDex(-zhufuhua.getAddDex());
            }
            if (zhufuhua.getAddCon() != 0) { // 體質
                pc.addCon(-zhufuhua.getAddCon());
            }
            if (zhufuhua.getAddInt() != 0) { // 智力
                pc.addInt(-zhufuhua.getAddInt());
            }
            if (zhufuhua.getAddWis() != 0) { // 精神
                pc.addWis(-zhufuhua.getAddWis());
            }
            if (zhufuhua.getAddCha() != 0) { // 魅力
                pc.addCha(-zhufuhua.getAddCha());
            }
            if (zhufuhua.getAddAc() != 0) { // 防禦
                pc.addAc(zhufuhua.getAddAc());
            }
            if (zhufuhua.getAddMaxHp() != 0) { // HP
                pc.addMaxHp(-zhufuhua.getAddMaxHp());
            }
            if (zhufuhua.getAddMaxMp() != 0) { // MP
                pc.addMaxMp(-zhufuhua.getAddMaxMp());
            }
            if (zhufuhua.getAddHpr() != 0) { // 回血
                pc.addHpr(-zhufuhua.getAddHpr());
            }
            if (zhufuhua.getAddMpr() != 0) { // 回魔
                pc.addMpr(-zhufuhua.getAddMpr());
            }
            if (zhufuhua.getAddDmg() != 0) { // 進戰傷害
                pc.addDmgup(-zhufuhua.getAddDmg());
            }
            if (zhufuhua.getAddHit() != 0) { // 進戰命中
                pc.addHitup(-zhufuhua.getAddHit());
            }
            if (zhufuhua.getAddBowDmg() != 0) { // 遠戰傷害
                pc.addBowDmgup(-zhufuhua.getAddBowDmg());
            }
            if (zhufuhua.getAddBowHit() != 0) { // 遠戰命中
                pc.addBowHitup(-zhufuhua.getAddBowHit());
            }
            if (zhufuhua.getReduction_dmg() != 0) { // 減免物理傷害
                pc.addDamageReductionByArmor(-zhufuhua.getReduction_dmg());
            }
            /*if (gfxIdOrginal.getReduction_magic_dmg() != 0) { // 減免魔法傷害
                pc.addMagicReductionDmgByA(-gfxIdOrginal.getReduction_magic_dmg());
            }*/
            if (zhufuhua.getAddMr() != 0) { // 抗魔
                pc.addMr(-zhufuhua.getAddMr());
            }
            if (zhufuhua.getAddSp() != 0) { // 魔攻
                pc.addSp(-zhufuhua.getAddSp());
            }
            if (zhufuhua.getAddFire() != 0) { // 抗火屬性
                pc.addFire(-zhufuhua.getAddFire());
            }
            if (zhufuhua.getAddWind() != 0) { // 抗風屬性
                pc.addWind(-zhufuhua.getAddWind());
            }
            if (zhufuhua.getAddEarth() != 0) { // 抗地屬性
                pc.addEarth(-zhufuhua.getAddEarth());
            }
            if (zhufuhua.getAddWater() != 0) { // 抗水屬性
                pc.addWater(-zhufuhua.getAddWater());
            }
            if (zhufuhua.getwljm() != 0) {//PVP物理減免
                pc.setZhufuPvp(0);
            }
            if (zhufuhua.getwljmbai() != 0) {//PVP物理減免百分比
                pc.setzhufuPvpbai(0);
            }
            if (zhufuhua.getmojm() != 0) {//PVP魔法傷害減免
                pc.setzhufuMoPvp(0);
            }
            if (zhufuhua.getmojmbai() != 0) {//PVP魔法傷害減免百分比
                pc.setzhufuMoPvpbai(0);
            }
            pc.sendPackets(new S_SPMR(pc));
            pc.sendPackets(new S_OwnCharStatus(pc));
            pc.sendPackets(new S_OwnCharStatus2(pc));
        }
    }

    public int getId() {
        return _id;
    }

    public int getKind() {
        return _kind;
    }

    public int getItemid() {
        return _itemid;
    }

    public int getType() {
        return _type;
    }

    public boolean getzhufu() {
        return _zhufu;
    }

    public boolean getzhufu2() {
        return _zhufu2;
    }

    public byte getAddStr() {
        return _addStr;
    }

    public byte getAddDex() {
        return _addDex;
    }

    public byte getAddCon() {
        return _addCon;
    }

    public byte getAddInt() {
        return _addInt;
    }

    public byte getAddWis() {
        return _addWis;
    }

    public byte getAddCha() {
        return _addCha;
    }

    public int getAddAc() {
        return _addAc;
    }

    public int getAddMaxHp() {
        return _addMaxHp;
    }

    public int getAddMaxMp() {
        return _addMaxMp;
    }

    public int getAddHpr() {
        return _addHpr;
    }

    public int getAddMpr() {
        return _addMpr;
    }

    public int getAddDmg() {
        return _addDmg;
    }

    public int getAddBowDmg() {
        return _addBowDmg;
    }

    public int getAddHit() {
        return _addHit;
    }

    public int getAddBowHit() {
        return _addBowHit;
    }

    public int getReduction_dmg() {
        return _reduction_dmg;
    }

    public int getReduction_magic_dmg() {
        return _reduction_magic_dmg;
    }

    public int getAddMr() {
        return _addMr;
    }

    public int getAddSp() {
        return _addSp;
    }

    public int getAddFire() {
        return _addFire;
    }

    public int getAddWind() {
        return _addWind;
    }

    public int getAddEarth() {
        return _addEarth;
    }

    public int getAddWater() {
        return _addWater;
    }

    public double getAddExp() {
        return _addExp;
    }

    public int getwljm() {
        return _addwljm;
    }

    public int getwljmbai() {
        return _addwljmbai;
    }

    public int getmojm() {
        return _addmojm;
    }

    public int getmojmbai() {
        return _addmojmbai;
    }
}
