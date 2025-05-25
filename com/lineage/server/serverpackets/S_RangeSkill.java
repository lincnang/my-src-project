package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.skill.TargetStatus;
import com.lineage.server.world.World;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 範圍魔法
 *
 * @author dexc
 */
public class S_RangeSkill extends ServerBasePacket {
    public static final int TYPE_NODIR = 0;
    public static final int TYPE_DIR = 8;
    private static AtomicInteger _sequentialNumber = new AtomicInteger(9000000);
    private byte[] _byte = null;

    /**
     * 範圍魔法
     *
     * @param cha        攻擊者
     * @param targetList 被攻擊者列表
     * @param spellgfx
     * @param actionId
     * @param type
     */
    public S_RangeSkill(final L1Character cha, final ArrayList<TargetStatus> targetList, final int spellgfx, final int actionId, final int type) {
        // 0000: 07 12 be ac bf 01 c1 83 e4 7e 04 47 8f c2 01 ab
        // .........~.G....
        // 0010: 00 06 00 00 02 00 3c 20 00 00 13 00 a4 6c 00 00 ......<
        // .....l..
        // 0020: 10 00 00 1c 87 21 12 00 .....!..
        this.writeC(S_ATTACK_MANY);
        this.writeC(actionId);
        this.writeD(cha.getId());
        this.writeH(cha.getX());
        this.writeH(cha.getY());
        switch (type) {
            case TYPE_NODIR:
                this.writeC(cha.getHeading());
                break;
            case TYPE_DIR:
                final int newHeading = calcheading(cha.getX(), cha.getY(), targetList.get(0).getTarget().getX(), targetList.get(0).getTarget().getY());
                cha.setHeading(newHeading);
                this.writeC(cha.getHeading());
                break;
        }
        this.writeD(_sequentialNumber.incrementAndGet()); // 番號送。
        this.writeH(spellgfx);
        this.writeC(type); // 0:範圍 6:遠距離 8:範圍&遠距離
        this.writeH(0x0000);
        this.writeH(targetList.size());
        for (TargetStatus target : targetList) {
            // System.out.println("TG: "+target.getTarget().getName() + "/" +
            // target.isCalc());
            int targetobj = target.getTarget().getId();
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
            this.writeD(targetobj);
            if (target.isCalc()) {
                this.writeH(0x20);
            } else {
                this.writeH(0x00); // 0x00:無傷害 大於0傷害質
            }
        }
    }

    private static int calcheading(final int myx, final int myy, final int tx, final int ty) {
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