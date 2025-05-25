package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Object;
import com.lineage.server.utils.Random;
import com.lineage.server.world.World;

/**
 * 物件攻擊(PC 用)
 *
 * @author dexc
 */
public class S_AttackPacketPc extends ServerBasePacket {
    private byte[] _byte = null;
    private int _actid = 1;// 預設攻擊動作

    /**
     * 物件攻擊 - <font color="#ff0000">命中</font>(PC 用 - 近距離)
     *
     * @param pc     執行者
     * @param target 目標
     * @param type   0x00:none 0x02:暴擊 0x04:雙擊 0x08:鏡反射
     * @param dmg    傷害力
     */
    public S_AttackPacketPc(final L1PcInstance pc, L1Character target, final int type, final int dmg) {
        /*
         * 0000: 5e 01 be ac bf 01 a4 6c 00 00 01 00 04 00 00 00
         * ^......l........ 0010: 00 00 44 00 01 00 aa 30 ..D....0
         *
         * 0000: 5e 01 be ac bf 01 a4 6c 00 00 00 00 04 00 00 00
         * ^......l........ 0010: 00 00 39 38 00 00 40 97 ..98..@.
         *
         * 0000: 5e 01 be ac bf 01 3c 20 00 00 01 00 05 00 00 00 ^.....<
         * ........ 0010: 00 00 f7 00 35 34 91 ba ....54..
         */
        this.writeC(S_ATTACK);
        if (Random.nextInt(100) < 25) {
            _actid = SpecialActid(pc);
        }
        this.writeC(_actid);// ACTION_AltAttack
        this.writeD(pc.getId());
        int targetobj = target.getId();
        /** 特定外形怪物不會有受傷動作 */ // TODO
        L1Object obj = World.get().findObject(targetobj);
        if (obj instanceof L1NpcInstance) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            switch (npc.getTempCharGfx()) {
                case 10913:// 巨型骷髏
                case 2544:// 林德拜爾
                    targetobj = 0;
                    pc.sendPackets(new S_Sound(1151));// 物理攻擊命中的聲音
                    break;
            }
        }
        this.writeD(targetobj);
        if (dmg > 0) {
            this.writeH(dmg); // 傷害值
        } else {
            this.writeH(0x00); // 傷害值
        }
        this.writeC(pc.getHeading());
        this.writeH(0x00); // target x
        this.writeH(0x00); // target y
        this.writeC(type); // 0x00:none 0x02:暴擊 0x04:雙擊 0x08:鏡反射
    }

    /**
     * 物件攻擊 - <font color="#ff0000">未命中</font>(PC 用 - 近距離)
     *
     * @param pc
     * @param target
     */
    public S_AttackPacketPc(final L1PcInstance pc, final L1Character target) {
        this.writeC(S_ATTACK);
        if (Random.nextInt(100) < 25) {
            _actid = SpecialActid(pc);
        }
        this.writeC(_actid);// ACTION_AltAttack
        this.writeD(pc.getId());
        this.writeD(target.getId());
        this.writeH(0x00); // damage
        this.writeC(pc.getHeading());
        this.writeH(0x00); // target x
        this.writeH(0x00); // target y
        this.writeC(0x00); // 0x00:none 0x02:暴擊 0x04:雙擊 0x08:鏡反射
    }

    /**
     * 物件攻擊 - <font color="#ff0000">空擊</font>(PC 用 - 近距離)
     *
     * @param pc
     */
    public S_AttackPacketPc(final L1PcInstance pc) {
        this.writeC(S_ATTACK);
        if (Random.nextInt(100) < 25) {
            _actid = SpecialActid(pc);
        }
        this.writeC(_actid);// ACTION_AltAttack
        this.writeD(pc.getId());
        this.writeD(0x00);
        this.writeH(0x00); // damage
        this.writeC(pc.getHeading());
        this.writeH(0x00); // target x
        this.writeH(0x00); // target y
        this.writeC(0x00); // 0x00:none 0x02:暴擊 0x04:雙擊 0x08:鏡反射
    }

    /**
     * 取回特定外型的特殊攻擊動作(近戰用)
     *
     * @param pc
     * @return
     */
    private int SpecialActid(final L1PcInstance pc) {
        int tempgfxid = pc.getTempCharGfx();
        L1ItemInstance weapon = pc.getWeapon();
        if (weapon == null) {// 空手
            return _actid;
        }
        int weapontype = weapon.getItem().getType1();
        if ((tempgfxid == 13715) && (weapontype == 4)) {// 真王子 拿單手劍
            _actid = 73;
        }
        if ((tempgfxid == 13717) && (weapontype == 4)) {// 真公主 拿單手劍
            _actid = 73;
        }
        if ((tempgfxid == 13719) && (weapontype == 50)) {// 真男騎士 拿雙手劍
            _actid = 79;
        }
        if ((tempgfxid == 13721) && (weapontype == 50)) {// 真女騎士 拿雙手劍
            _actid = 79;
        }
        if ((tempgfxid == 13723) && (weapontype == 4)) {// 真男精靈 拿單手劍
            _actid = 73;
        }
        if ((tempgfxid == 13725) && (weapontype == 4)) {// 真女精靈 拿單手劍
            _actid = 73;
        }
        if ((tempgfxid == 13727) && (weapontype == 40)) {// 真男法師 拿杖
            _actid = 77;
        }
        if ((tempgfxid == 13729) && (weapontype == 40)) {// 真女法師 拿杖
            _actid = 77;
        }
        if ((tempgfxid == 13731) && (weapontype == 54)) {// 真男黑妖 拿雙刀
            _actid = 80;
        }
        if ((tempgfxid == 13731) && (weapontype == 58)) {// 真男黑妖 拿爪
            _actid = 81;
        }
        if ((tempgfxid == 13733) && (weapontype == 54)) {// 真女黑妖 拿雙刀
            _actid = 80;
        }
        if ((tempgfxid == 13733) && (weapontype == 58)) {// 真女黑妖 拿爪
            _actid = 81;
        }
        if ((tempgfxid == 13735) && (weapontype == 24)) {// 真男龍騎士 拿鎖煉劍
            _actid = 41;
        }
        if ((tempgfxid == 13737) && (weapontype == 24)) {// 真女龍騎士 拿鎖煉劍
            _actid = 41;
        }
        if ((tempgfxid == 13741) && (weapontype == 40)) {// 真女幻術師 拿杖
            _actid = 77;
        }
        if ((tempgfxid == 13743) && (weapontype == 11)) {// 真男戰士 拿斧頭
            _actid = 93;
        }
        if ((tempgfxid == 13743) && (weapontype == 24)) {// 真男戰士 拿矛
            _actid = 76;
        }
        if ((tempgfxid == 13745) && (weapontype == 11)) {// 真女戰士 拿斧頭
            _actid = 93;
        }
        if ((tempgfxid == 13745) && (weapontype == 24)) {// 真女戰士 拿矛
            _actid = 76;
        }
        if (tempgfxid >= 13216 && tempgfxid <= 13220) {// 日本變身
            _actid = 30;
        }
        if (tempgfxid == 12280) {// 90級變身[格立特]
            _actid = 30;
        }
        if (tempgfxid == 12283) {// 90級變身[特羅斯]
            _actid = 30;
        }
        if (tempgfxid == 12286) {// 90級變身[依詩蒂]
            _actid = 30;
        }
        if (tempgfxid == 12295) {// 90級變身[喬]
            _actid = 30;
        }
        return _actid;
    }

    @Override
    public byte[] getContent() {
        if (this._byte == null) {
            this._byte = this.getBytes();
        }
        return this._byte;
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
