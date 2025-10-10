package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.S_AttackPacketPc;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Calendar;

import static com.lineage.server.model.Instance.L1PcInstance.REGENSTATE_ATTACK;
import static com.lineage.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;

/**
 * 要求角色攻擊
 *
 * @author dexc
 */
public class C_Attack extends ClientBasePacket {
    private static final Log _log = LogFactory.getLog(C_Attack.class);

    @Override
    public void start(final byte[] decrypt, final ClientExecutor client) {
        try {
            // 資料載入
            this.read(decrypt);
            final L1PcInstance pc = client.getActiveChar();
            pc.isFoeSlayer();

            // ======= 前置安全檢查 =======
            if (pc.isGhost()) return; // 鬼魂模式
            if (pc.isDead()) return;  // 死亡
            if (pc.isTeleport()) return; // 傳送中
            if (pc.isPrivateShop()) return; // 商店村模式
            if (pc.getInventory().getWeight240() >= 197) { // 重量過重
                pc.sendPackets(new S_ServerMessage(110));
                pc.setAttackTargetId(0);
                return;
            }
            final int result = pc.speed_Attack().checkInterval(AcceleratorChecker.ACT_TYPE.ATTACK); //src042
            if (result == AcceleratorChecker.R_DISPOSED) {
                pc.setAttackTargetId(0);
                return;
            }
            if (pc.isInvisble()) { // 隱身狀態，只有黑妖可攻擊
                if (!pc.isDarkelf()) {
                    pc.setAttackTargetId(0);
                    return;
                }
            }
            if (pc.isInvisDelay()) { // 隱身延遲
                pc.setAttackTargetId(0);
                return;
            }
            if (pc.isParalyzedX()) { // 無法攻擊/使用道具/技能/回城的狀態
                pc.setAttackTargetId(0);
                return;
            }
            if (pc.get_weaknss() != 0) {
                long h_time = Calendar.getInstance().getTimeInMillis() / 1000;
                if (h_time - pc.get_weaknss_t() > 16) {
                    pc.set_weaknss(0, 0);
                }
            }
            // 攻擊座標與目標
            int targetId = 0;
            int locx = 0;
            int locy = 0;
            try {
                targetId = this.readD();
                locx = this.readH();
                locy = this.readH();
            } catch (final Exception e) {
                return;
            }
            if (locx == 0 || locy == 0) return;

            final L1Object target = World.get().findObject(targetId);

            if (target instanceof L1Character) {
                if (target.getMapId() != pc.getMapId()) {
                    pc.setAttackTargetId(0);
                    return;
                }
                int range = 1;
                if (pc.getWeapon() != null) {
                    range = pc.getWeapon().getItem().getRange();
                    range = range < 0 ? 15 : range;
                }
                if ((target.getMapId() != pc.getMapId()) ||
                        (pc.getLocation().getLineDistance(target.getLocation()) > range + 1.5)) {
                    pc.setAttackTargetId(0);
                    return;
                }
            }
            // 地圖權限檢查
            CheckUtil.isUserMap(pc);
            if (target instanceof L1NpcInstance) {
                final int hiddenStatus = ((L1NpcInstance) target).getHiddenStatus();
                if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK ||
                        hiddenStatus == L1NpcInstance.HIDDEN_STATUS_FLY) {
                    pc.setAttackTargetId(0);
                    return;
                }
            }
            // ======= 狀態解除 =======
            if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
                pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
                pc.startHpRegeneration();
                pc.startMpRegeneration();
            }
            // 攻擊時不再取消冥想術

            // =============== 攻擊主流程包裝（防黑妖提前現身） ===============
            pc.setAssassinAttackNow(true);
            try {
                pc.delInvis(); // 透明狀態の解除（黑妖刺客在攻擊流程這時不會提前現身）

                pc.setRegenState(REGENSTATE_ATTACK);

                if ((target != null) && !((L1Character) target).isDead()) {
                    if (target instanceof L1PcInstance) {
                        L1PcInstance tg = (L1PcInstance) target;
                        pc.setNowTarget(tg);
                    }
                    target.onAction(pc); // 主攻擊（這裡才會 new L1AttackPc ...）
                } else { // 空攻擊
                    pc.setHeading(pc.targetDirection(locx, locy));
                    pc.sendPacketsAll(new S_AttackPacketPc(pc));
                    pc.setAttackTargetId(0);
                }
            } catch (final Exception e) {
                _log.error(e.getLocalizedMessage(), e);
            } finally {
                pc.setAssassinAttackNow(false); // 攻擊結束還原旗標
            }
            // =============== 攻擊主流程包裝結束 ===============

        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            this.over();
        }
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
