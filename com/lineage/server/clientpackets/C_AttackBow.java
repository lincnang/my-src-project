package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.clientpackets.AcceleratorChecker.ACT_TYPE;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_UseArrowSkill;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.world.World;

import java.util.Calendar;

public class C_AttackBow extends ClientBasePacket {
    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            // 資料載入
            this.read(decrypt);
            final L1PcInstance pc = client.getActiveChar();
            pc.setFoeSlayer(false);
            if (pc.isGhost()) { // 鬼魂模式
                return;
            }
            if (pc.isDead()) { // 死亡
                return;
            }
            if (pc.isTeleport()) { // 傳送中
                return;
            }
            if (pc.isPrivateShop()) { // 商店村模式
                return;
            }
            if (pc.getInventory().getWeight240() >= 197) { // 重量過重
                // 110 \f1當負重過重的時候，無法戰鬥。
                pc.sendPackets(new S_ServerMessage(110));
                return;
            }
            final int result = pc.speed_Attack().checkInterval(ACT_TYPE.ATTACK);
            pc.getTempCharGfx();
            if (result == AcceleratorChecker.R_DISPOSED) {
                //_log.error("要求角色攻擊:速度異常(" + pc.getName() + ")");
                return;
            }
			/*final int result = pc.speed_Attack().checkInterval(AcceleratorChecker.ACT_TYPE.ATTACK);
			if (result == AcceleratorChecker.R_DISPOSED) {
				_log.error("要求角色攻擊:速度異常(" + pc.getName() + ")");
			}*/
            if (pc.isInvisble()) {
                return;
            }
            if (pc.isInvisDelay()) {
                return;
            }
            if (pc.isParalyzedX()) {
                return;
            }
            if (pc.get_weaknss() != 0) {
                long h_time = Calendar.getInstance().getTimeInMillis() / 1000L;
                if (h_time - pc.get_weaknss_t() > 16L) {
                    pc.set_weaknss(0, 0L);
                }
            }
            int targetId = 0;
            int locx = 0;
            int locy = 0;
            int targetX = 0;
            int targetY = 0;
            try {
                targetId = readD();
                locx = readH();
                locy = readH();
            } catch (Exception e) {
                over();
                return;
            }
            if (locx == 0) {
                return;
            }
            if (locy == 0) {
                return;
            }
            targetX = locx;
            targetY = locy;
            L1Object target = World.get().findObject(targetId);
            if (((target instanceof L1Character)) && (target.getMapId() != pc.getMapId())) {
                return;
            }
            CheckUtil.isUserMap(pc);
            if ((target instanceof L1NpcInstance)) {
                int hiddenStatus = ((L1NpcInstance) target).getHiddenStatus();
                if (hiddenStatus == 1) {
                    return;
                }
                if (hiddenStatus == 2) {
                    return;
                }
            }
            if (pc.hasSkillEffect(78)) {
                pc.killSkillEffectTimer(78);
                pc.startHpRegeneration();
                pc.startMpRegeneration();
            }
            // 攻擊時不再取消冥想術
            pc.delInvis();
            pc.setRegenState(1);
            if ((target != null) && (!((L1Character) target).isDead())) {// 具有目標
                if ((target instanceof L1PcInstance)) {
                    L1PcInstance tg = (L1PcInstance) target;
                    pc.setNowTarget(tg);
                }
                // 同步主人的攻擊目標給寵物/召喚
                pc.setPetTarget((L1Character) target);
                target.onAction(pc);
            } else {// 沒有目標
                pc.setHeading(pc.targetDirection(locx, locy));
                L1ItemInstance weapon = pc.getWeapon();
                if (weapon != null) {
                    int weaponType = weapon.getItem().getType1();
                    switch (weaponType) {
                        case 20:// 弓
                            L1ItemInstance arrow = pc.getInventory().getArrow();
                            if (arrow != null) {// 具有箭
                                int arrowGfxid = 66;
                                switch (pc.getTempCharGfx()) {
                                    case 8842:// 海露拜
                                    case 8900:// 海露拜
                                        arrowGfxid = 8904;
                                        break;
                                    case 8845:// 絲莉安
                                    case 8913:// 絲莉安
                                        arrowGfxid = 8916;
                                        break;
                                    case 7959:// 天上騎士
                                    case 7967:// 天上騎士
                                    case 7968:// 天上騎士
                                    case 7969:// 天上騎士
                                    case 7970:// 天上騎士
                                        arrowGfxid = 7972;
                                        break;
                                    case 13631:// 歐薇82 //src013
                                        arrowGfxid = 13657;
                                        break;
                                    case 13635:// 歐薇85
                                        arrowGfxid = 13659;
                                        break;
                                    case 13723:// 新妖精變身(男)
                                        arrowGfxid = 13658;
                                        break;
                                    case 13725:// 新妖精變身(女)
                                        arrowGfxid = 13656;
                                        break;
                                }
                                // 屬性箭矢覆蓋：妖精職 → 土25001 火25003 水25005 風25007
                                if (pc.isElf()) {
                                    int attr = pc.getElfAttr();
                                    switch (attr) {
                                        case 1: arrowGfxid = 25001; break; // 地
                                        case 2: arrowGfxid = 25003; break; // 火
                                        case 4: arrowGfxid = 25005; break; // 水
                                        case 8: arrowGfxid = 25007; break; // 風
                                        default: break;
                                    }
                                }
							/*if (pc.getTempCharGfx() >= 13715 && pc.getTempCharGfx() <= 13745) {// TOP10變身
								arrowGfxid = 11762;
							}*/
                                arrowAction(pc, arrow, arrowGfxid, targetX, targetY);
                            } else {// 沒有箭
                                if (weapon.getItem().getItemId() == 190) {// 沙哈之弓
                                    arrowAction(pc, null, 2349, targetX, targetY);
                                } else {
                                    nullAction(pc);
                                }
                            }
                            break;
                        case 62:// 鐵手甲
                            L1ItemInstance sting = pc.getInventory().getSting();
                            if (sting != null) {// 具有飛刀
                                int stingGfxid = 2989;
                                switch (pc.getTempCharGfx()) {
                                    case 8842:// 海露拜
                                    case 8900:// 海露拜
                                        stingGfxid = 8904;
                                        break;
                                    case 8845:// 絲莉安
                                    case 8913:// 絲莉安
                                        stingGfxid = 8916;
                                        break;
                                    case 7959:// 天上騎士
                                    case 7967:// 天上騎士
                                    case 7968:// 天上騎士
                                    case 7969:// 天上騎士
                                    case 7970:// 天上騎士
                                        stingGfxid = 7972;
                                        break;
                                    case 13631:// 歐薇82
                                        stingGfxid = 13656;
                                        break;
                                    case 13635:// 歐薇85
                                        stingGfxid = 13658;
                                        break;
                                }
                                if (pc.getTempCharGfx() >= 13715 && pc.getTempCharGfx() <= 13745) {// TOP10變身
                                    stingGfxid = 11762;
                                }
                                arrowAction(pc, sting, stingGfxid, targetX, targetY);
                            } else {// 沒有飛刀
                                nullAction(pc);
                            }
                            break;
                    }
                }
            }
        } catch (Exception localException1) {
        } finally {
            over();
        }
    }

    private void arrowAction(L1PcInstance pc, L1ItemInstance item, int gfx, int targetX, int targetY) {
        pc.sendPacketsAll(new S_UseArrowSkill(pc, gfx, targetX, targetY, 1));
        if (item != null) {
            pc.getInventory().removeItem(item, 1L);
        }
    }

    private void nullAction(L1PcInstance pc) {
        // System.out.println("空攻擊-沒有目標");
        pc.sendPacketsAll(new S_UseArrowSkill(pc));
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.clientpackets.C_AttackBow JD-Core Version: 0.6.2
 */