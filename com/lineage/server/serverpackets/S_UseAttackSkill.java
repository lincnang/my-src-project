package com.lineage.server.serverpackets;

import com.lineage.server.ActionCodes;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Object;
import com.lineage.server.utils.Random;
import com.lineage.server.world.World;

import java.util.concurrent.atomic.AtomicInteger;

import static com.lineage.server.model.skill.L1SkillId.SHAPE_CHANGE;

public class S_UseAttackSkill extends ServerBasePacket {
    private static AtomicInteger _sequentialNumber = new AtomicInteger(0);
    private byte[] _byte = null;

    /**
     * 物件攻擊(武器 技能使用-不需動作代號-不送出傷害)
     *
     * @param cha       執行者
     * @param targetobj 目標OBJID
     * @param spellgfx  遠程動畫編號
     * @param x         X點
     * @param y         Y點
     * @param actionId  動作代號
     * @param motion    是否具有執行者
     */
    public S_UseAttackSkill(L1Character cha, int targetobj, int spellgfx, int x, int y, int actionId, boolean motion) {
        buildPacket(cha, targetobj, spellgfx, x, y, actionId, 0, motion);
    }

    /**
     * 物件攻擊(NPC / PC 技能使用)
     *
     * @param cha       執行者
     * @param targetobj 目標OBJID
     * @param spellgfx  遠程動畫編號
     * @param x         X點
     * @param y         Y點
     * @param actionId  動作代號
     * @param dmg       傷害力
     */
    public S_UseAttackSkill(L1Character cha, int targetobj, int spellgfx, int x, int y, int actionId, int dmg) {
        buildPacket(cha, targetobj, spellgfx, x, y, actionId, dmg, true);
    }

    private static int calcheading(int myx, int myy, int tx, int ty) {
        int newheading = 0;
        if ((tx > myx) && (ty > myy)) {
            newheading = 3;
        }
        if ((tx < myx) && (ty < myy)) {
            newheading = 7;
        }
        if ((tx > myx) && (ty == myy)) {
            newheading = 2;
        }
        if ((tx < myx) && (ty == myy)) {
            newheading = 6;
        }
        if ((tx == myx) && (ty < myy)) {
            newheading = 0;
        }
        if ((tx == myx) && (ty > myy)) {
            newheading = 4;
        }
        if ((tx < myx) && (ty > myy)) {
            newheading = 5;
        }
        if ((tx > myx) && (ty < myy)) {
            newheading = 1;
        }
        return newheading;
    }

    /**
     * 取回特定外型的特殊攻擊動作(魔攻用)
     *
     */
    private int SpecialActid(final L1Character cha) {
        int actionId = 18;// 預設攻擊魔法動作
        int tempgfxid = cha.getTempCharGfx();
        if (Random.nextInt(100) < 25) {
            if (tempgfxid == 13727 || tempgfxid == 13729) {// 真法師
                actionId = 31;
            }
            if (tempgfxid == 13731 || tempgfxid == 13733) {// 真黑妖
                actionId = 31;
            }
        }
        if ((tempgfxid == 5727) || (tempgfxid == 5730)) {
            actionId = 19;
        }
        if ((tempgfxid == 5733) || (tempgfxid == 5736)) {
            actionId = 1;
        }
        return actionId;
    }

    /**
     * 物件攻擊(技能使用 - PC/NPC共用)
     *
     * @param cha            執行者
     * @param targetobj      目標OBJID
     * @param spellgfx       遠程動畫編號
     * @param x              X點
     * @param y              Y點
     * @param actionId       動作代號
     * @param dmg            傷害力
     * @param withCastMotion 是否具有執行者
     */
    private void buildPacket(L1Character cha, int targetobj, int spellgfx, int x, int y, int actionId, int dmg, boolean withCastMotion) {
        if (cha instanceof L1PcInstance) {// 執行者為PC
            if (cha.hasSkillEffect(SHAPE_CHANGE) && (actionId == ActionCodes.ACTION_SkillAttack)) {// 有向施法
                actionId = SpecialActid(cha);
            }
        }
        // 火靈之主動作代號強制變更
        if (cha.getTempCharGfx() == 4013) {
            actionId = ActionCodes.ACTION_Attack;
        }
        // 設置新面向
        final int newheading = calcheading(cha.getX(), cha.getY(), x, y);
        cha.setHeading(newheading);
        this.writeC(S_ATTACK);
        this.writeC(actionId);// 動作代號
        this.writeD(withCastMotion ? cha.getId() : 0x00000000);// 執行者OBJID
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
        this.writeD(targetobj);// 目標OBJID
        if (dmg > 0) {
            this.writeH(dmg); // 傷害值
        } else {
            this.writeH(0x0000); // 傷害值
        }
        this.writeC(newheading);// 新面向
        writeD(_sequentialNumber.incrementAndGet());
        this.writeH(spellgfx);// 遠程動畫編號
        this.writeC(0x00); // 具備飛行動畫:6, 不具備飛行動畫:0
        this.writeH(cha.getX());// 執行者X點
        this.writeH(cha.getY());// 執行者Y點
        this.writeH(x);// 目標X點
        this.writeH(y);// 目標Y點
        writeD(0);
        writeC(0);
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
 * com.lineage.server.serverpackets.S_UseAttackSkill JD-Core Version: 0.6.2
 */