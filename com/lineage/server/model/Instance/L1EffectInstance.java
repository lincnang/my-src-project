package com.lineage.server.model.Instance;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1GroundInventory;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.poison.L1DamagePoison;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldQuest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;

public class L1EffectInstance extends L1NpcInstance {
    public static final int FW_DAMAGE_INTERVAL = 1650;
    public static final int CUBE_INTERVAL = 500;
    public static final int CUBE_TIME = 8000;
    public static final int OTHER = 500;
    private static final long serialVersionUID = 1L;
    private static final Log _log = LogFactory.getLog(L1EffectInstance.class);
    private L1EffectType _effectType;
    private int _skillId;

    public L1EffectInstance(L1Npc template) {
        super(template);
        int npcId = getNpcTemplate().get_npcId();
        switch (npcId) {
            case 81157:
                _effectType = L1EffectType.isFirewall;
                break;
            case 80149:
                _effectType = L1EffectType.isCubeBurn;
                break;
            case 80150:
                _effectType = L1EffectType.isCubeEruption;
                break;
            case 80151:
                _effectType = L1EffectType.isCubeShock;
                break;
            case 80152:
                _effectType = L1EffectType.isCubeHarmonize;
                break;
            case 86125:// 毒霧
                PoisonTimer poisonTimer = new PoisonTimer(this);
                poisonTimer.start();
                break;
            default:
                _effectType = L1EffectType.isOther;
        }
    }

    /**
     * 效果類型
     *
     */
    public L1EffectType effectType() {
        return _effectType;
    }

    /**
     * 接觸資訊
     */
    public void onPerceive(L1PcInstance perceivedFrom) {
        try {
            if (perceivedFrom.get_showId() != get_showId()) {
                return;
            }
            perceivedFrom.addKnownObject(this);
            perceivedFrom.sendPackets(new S_NPCPack_Eff(this));
            if (getBraveSpeed() > 0) {
                perceivedFrom.sendPackets(new S_SkillBrave(getId(), getBraveSpeed(), 600000));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 受到攻擊的處理
     */
    public void onAction(L1PcInstance pc) {
    }

    /**
     * 跟隨目標變更移動/速度狀態
     */
    public void setFollowSpeed(L1Character target) {
        if ((target != null) && (target.isInvisble())) {// 目標隱身
            deleteMe();
            return;
        }
        if (target.isDead()) {// 目標死亡
            deleteMe();
            return;
        }
        if (target.getMoveSpeed() != this.getMoveSpeed()) {
            setMoveSpeed(target.getMoveSpeed());
        }
        if (target.getBraveSpeed() != this.getBraveSpeed()) {
            setBraveSpeed(target.getBraveSpeed());
        }
    }

    /**
     * 跟隨目標
     *
     */
    public void Follow(L1Character target) {
        try {
            if (target == null) {// 失去目標
                deleteMe();
                return;
            }
            if (target.isInvisble()) {// 目標隱身
                deleteMe();
                return;
            }
            if (target.isDead()) {// 目標死亡
                deleteMe();
                return;
            }
            if (!this.destroyed()) {// 尚未進行刪除
                while (!this.getLocation().isSamePoint(target.getLocation())) {// 不在同一座標點上
                    // System.out.println("不在同一座標點上");
                    // 取回行進方向
                    final int dir = this.targetDirection(target.getX(), target.getY());
                    if (dir != -1) {
                        this.setDirectionMove(dir);
                    }
                }
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 往指定面向移動1格(無障礙設置)
     */
    private void setDirectionMove(final int heading) {
        // System.out.println("往指定面向移動1格: "+this.getNpcId() + " 面向:" + heading);
        int locx = this.getX();
        int locy = this.getY();
        locx += HEADING_TABLE_X[heading];
        locy += HEADING_TABLE_Y[heading];
        this.setHeading(heading);
        this.setX(locx);
        this.setY(locy);
        this.broadcastPacketAll(new S_MoveCharPacket(this));
    }

    /**
     * 物件刪除的處理
     */
    public void deleteMe() {
        try {
            _destroyed = true;
            if (getInventory() != null) {
                getInventory().clearItems();
            }
            allTargetClear();
            _master = null;
            int showid = get_showId();
            if (WorldQuest.get().isQuest(showid)) {
                WorldQuest.get().remove(showid, this);
            }
            World.get().removeVisibleObject(this);
            World.get().removeObject(this);
            for (L1PcInstance pc : World.get().getRecognizePlayer(this)) {
                if (pc != null) {
                    if (this.getNpcId() == 86131) {// 精準目標效果
                        pc.sendPackets(new S_DoActionGFX(this.getId(), 11));// 發送結束動畫
                    }
                    pc.removeKnownObject(this);
                    pc.sendPackets(new S_RemoveObject(this));
                }
            }
            for (L1Object obj : World.get().getVisibleObjects(this)) {
                if (obj != null) {
                    if (obj instanceof L1PcInstance || obj instanceof L1NpcInstance) {
                        L1Character cha = (L1Character) obj;
                        ArrayList<L1EffectInstance> effectlist = cha.get_TrueTargetEffectList();// 取回對像身上的精準目標效果
                        if (effectlist.contains(this)) {
                            effectlist.remove(this);// 移出列表
                        }
                    }
                }
            }
            removeAllKnownObjects();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public int getSkillId() {
        return _skillId;
    }

    public void setSkillId(int i) {
        _skillId = i;
    }

    /**
     * 毒霧計時器 - 事件驅動架構
     * 
     * 重構日期: 2025-10-12
     * 重構原因: 原 while-loop 阻塞模式導致線程浪費
     * 架構模式: Event-Driven (schedule-based) 非阻塞執行
     * 
     * 優勢:
     * - 20 個毒霧效果從佔用 20 個線程 → 共享 2-3 個線程
     * - 線程使用時間: 永久佔用 → 每次僅 5-10ms
     */
    class PoisonTimer implements Runnable {
        private static final int POISON_INTERVAL = 1000;
        private final L1EffectInstance _effect;
        private ScheduledFuture<?> _future;

        public PoisonTimer(L1EffectInstance effect) {
            _effect = effect;
        }

        /**
         * 啟動毒霧計時器
         */
        public void start() {
            schedule(0); // 立即開始第一次執行
        }

        /**
         * 停止毒霧計時器
         */
        public void stop() {
            if (_future != null && !_future.isCancelled()) {
                _future.cancel(false);
                _future = null;
            }
        }

        /**
         * 執行一次毒霧效果判定
         */
        @Override
        public void run() {
            try {
                // 檢查效果是否已銷毀
                if (_destroyed) {
                    stop();
                    return;
                }

                // 對範圍內所有角色施加中毒
                for (L1Object objects : World.get().getVisibleObjects(_effect, 0)) {
                    if ((!(objects instanceof L1MonsterInstance)) 
                        && (!(objects instanceof L1GroundInventory))) {
                        L1Character cha = (L1Character) objects;
                        L1DamagePoison.doInfection(_effect, cha, 3000, 100);
                    }
                }

                // 重新排程下次執行
                schedule(POISON_INTERVAL);

            } catch (Exception e) {
                _log.error("PoisonTimer error for effect: " + _effect.getId(), e);
                stop();
            }
        }

        /**
         * 排程下次執行
         * @param delay 延遲時間 (毫秒)
         */
        private void schedule(long delay) {
            try {
                _future = GeneralThreadPool.get().schedule(this, delay);
            } catch (Exception e) {
                _log.error("Failed to schedule PoisonTimer", e);
                stop();
            }
        }
    }
}
