package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Object;
import com.lineage.server.utils.Random;
import com.lineage.server.world.World;

import java.util.concurrent.atomic.AtomicInteger;

public class S_UseArrowSkill extends ServerBasePacket {
    private static AtomicInteger _sequentialNumber = new AtomicInteger(0);
    private byte[] _byte = null;
    private int _actid = 1;

    /**
     * 物件攻擊 <font color="#ff0000">遠距離命中</font>
     *
     */
    public S_UseArrowSkill(L1Character cha, int targetobj, int spellgfx, int targetx, int targety, int dmg, int actId) {
        switch (cha.getTempCharGfx()) {
            case 3860:// 妖魔弓箭手
                _actid = 21;
                break;
            case 2716:// 古代亡靈
                _actid = 19;
                break;
            case 5127:// 腐爛的骷髏弓箭手
            case 10649:// 腐蝕的 骷髏弓箭手
            case 11714:// 漆黑的骷髏弓箭手
                _actid = 30;
                break;
            case 10566:// 河太郎
                _actid = 18;
                break;
        }
        writeC(S_ATTACK);
        if (Random.nextInt(100) < 25) {
            _actid = SpecialActid(cha);
        }
        if (actId > 1) {// 有指定攻擊動作編號
            _actid = actId;
        }
        writeC(_actid);
        writeD(cha.getId());
        /** 特定外形怪物不會有受傷動作 */ // TODO
        L1Object obj = World.get().findObject(targetobj);
        if (obj instanceof L1NpcInstance) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            switch (npc.getTempCharGfx()) {
                case 10913:// 巨型骷髏
                case 2544:// 林德拜爾
                    targetobj = 0;
                    break;
            }
        }
        writeD(targetobj);
        if (dmg > 0) {
            writeH(dmg);
        } else {
            writeH(0);
        }
        writeC(cha.getHeading());
        writeD(_sequentialNumber.incrementAndGet());
        writeH(spellgfx);
        writeC(0);
        writeH(cha.getX());
        writeH(cha.getY());
        writeH(targetx);
        writeH(targety);
        writeD(0);
        writeC(0);
    }

    /**
     * 物件攻擊 <font color="#ff0000">遠距離未命中</font>
     *
     */
    public S_UseArrowSkill(L1Character cha, int spellgfx, int targetx, int targety, int actId) {
        switch (cha.getTempCharGfx()) {
            case 3860:// 妖魔弓箭手
                _actid = 21;
                break;
            case 2716:// 古代亡靈
                _actid = 19;
                break;
            case 5127:// 腐爛的骷髏弓箭手
            case 10649:// 腐蝕的 骷髏弓箭手
            case 11714:// 漆黑的骷髏弓箭手
                _actid = 30;
                break;
            case 10566:// 河太郎
                _actid = 18;
                break;
        }
        writeC(S_ATTACK);
        if (Random.nextInt(100) < 25) {
            _actid = SpecialActid(cha);
        }
        if (actId > 1) {// 有指定攻擊動作編號
            _actid = actId;
        }
        writeC(_actid);
        writeD(cha.getId());
        writeD(0);
        writeH(0);
        writeC(cha.getHeading());
        writeD(_sequentialNumber.incrementAndGet());
        writeH(spellgfx);
        writeC(0);
        writeH(cha.getX());
        writeH(cha.getY());
        writeH(targetx);
        writeH(targety);
        writeD(0);
        writeC(0);
    }

    /**
     * 物件攻擊 - <font color="#ff0000">遠距離空擊</font>
     *
     */
    public S_UseArrowSkill(L1Character cha) {
        switch (cha.getTempCharGfx()) {
            case 3860:// 妖魔弓箭手
                _actid = 21;
                break;
            case 2716:// 古代亡靈
                _actid = 19;
                break;
            case 5127:// 腐爛的骷髏弓箭手
            case 10649:// 腐蝕的 骷髏弓箭手
            case 11714:// 漆黑的骷髏弓箭手
                _actid = 30;
                break;
            case 10566:// 河太郎
                _actid = 18;
                break;
        }
        this.writeC(S_ATTACK);
        if (Random.nextInt(100) < 25) {
            _actid = SpecialActid(cha);
        }
        this.writeC(_actid);// ACTION_AltAttack
        this.writeD(cha.getId());
        this.writeD(0x00);
        this.writeH(0x01); // damage
        this.writeC(cha.getHeading());
        writeD(_sequentialNumber.incrementAndGet());
        writeH(2510);
        writeC(0);
        writeH(cha.getX());
        writeH(cha.getY());
        writeH(0);
        writeH(0);
        writeD(0);
        writeC(0);
    }

    /**
     * 取回特定外型的特殊攻擊動作(遠攻用)
     *
     */
    private int SpecialActid(L1Character cha) {
        if (cha instanceof L1PcInstance) {// 執行者為PC
            L1PcInstance pc = (L1PcInstance) cha;
            int tempgfxid = cha.getTempCharGfx();
            L1ItemInstance weapon = pc.getWeapon();
            if (weapon == null) {// 空手
                return _actid;
            }
            int weapontype = weapon.getItem().getType1();
            if ((tempgfxid == 13723) && (weapontype == 20)) {// 真男精靈 拿弓
                _actid = 75;
            }
            if ((tempgfxid == 13725) && (weapontype == 20)) {// 真女精靈 拿弓
                _actid = 75;
            }
            if (tempgfxid == 13218) {// 日本變身-姬武者
                _actid = 30;
            }
            if (tempgfxid == 12314) {// 90級變身[絲莉安]
                _actid = 30;
            }
        }
        return _actid;
    }

    public byte[] getContent() {
        if (_byte == null) {
            _byte = _bao.toByteArray();
        } else {
            int seq = 0;
            synchronized (this) {
                seq = _sequentialNumber.incrementAndGet();
            }
            _byte[13] = ((byte) (seq & 0xFF));
            _byte[14] = ((byte) (seq >> 8 & 0xFF));
            _byte[15] = ((byte) (seq >> 16 & 0xFF));
            _byte[16] = ((byte) (seq >> 24 & 0xFF));
        }
        return _byte;
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.serverpackets.S_UseArrowSkill JD-Core Version: 0.6.2
 */