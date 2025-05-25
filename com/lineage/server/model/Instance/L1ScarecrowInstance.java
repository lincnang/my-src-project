package com.lineage.server.model.Instance;

import com.lineage.server.datatables.ExpTable;
import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_MatizCloudia;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.utils.CalcExp;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;

/**
 * 木人控制項
 *
 * @author dexc
 */
public class L1ScarecrowInstance extends L1NpcInstance {
    private static final long serialVersionUID = 1L;
    private static final Log _log = LogFactory.getLog(L1ScarecrowInstance.class);

    public L1ScarecrowInstance(final L1Npc template) {
        super(template);
    }

    @Override
    public void onAction(final L1PcInstance player) {
        try {
            final L1AttackMode attack = new L1AttackPc(player, this);
            if (attack.calcHit()) {// 命中
                final int damage = attack.calcDamage();// 最終傷害計算
                if (this.getNpcId() >= 45001 && this.getNpcId() <= 45003) {// 新版稻草人
                    player.sendPackets(new S_ServerMessage(166, "傷害輸出: " + damage));
                    player.sendPackets(new S_ServerMessage(166, "人物最大命中值: " + attack.getHit()));
                } else if (this.getNpcId() >= 72002 && this.getNpcId() <= 72009) {// 新版稻草人
                    player.sendPackets(new S_ServerMessage(166, "傷害輸出: " + damage));
                    player.sendPackets(new S_ServerMessage(166, "人物最大命中值: " + attack.getHit()));
                }
                if (player.getLevel() < 5 && getNpcId() == 45000) {
                    if (player.getLevel() == 2) {
                        player.sendPackets(new S_MatizCloudia(1));
                    }
                    player.sendPackets(new S_MatizCloudia(0, player.getLevel() + 1));
                    player.setExp_Direct(ExpTable.getExpByLevel(player.getLevel() + 1));
                } else
                    //if (player.getLevel() < 5) { // ＬＶ制限もうける場合はここを変更
                    if (player.getLevel() < 5 && getNpcId() != 4070082) {
                        final ArrayList<L1PcInstance> targetList = new ArrayList<L1PcInstance>();
                        targetList.add(player);
                        final ArrayList<Integer> hateList = new ArrayList<Integer>();
                        hateList.add(1);
                        final int exp = (int) this.getExp();
                        CalcExp.calcExp(player, this.getId(), targetList, hateList, exp);
                    } else {
                        if (ATTACK != null) {
                            ATTACK.attack(player, this);
                        }
                    }
                /*
                 * if (this.getHeading() < 7) { // 今の向きを取得
                 * this.setHeading(this.getHeading() + 1); // 今の向きを設定 } else {
                 * this.setHeading(0); // 今の向きが7 以上になると今の向きを0に戻す }
                 */
                this.broadcastPacketAll(new S_DoActionGFX(this.getId(), 2)); // 受傷動作
            } else {// 未命中
                if (this.getNpcId() >= 45001 && this.getNpcId() <= 45003) {// 新版稻草人
                    player.sendPackets(new S_ServerMessage(166, "未命中"));
                } else if (this.getNpcId() >= 72002 && this.getNpcId() <= 72009) {// 新版稻草人
                    player.sendPackets(new S_ServerMessage(166, "未命中"));
                }
            }
            attack.action();
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void onTalkAction(final L1PcInstance l1pcinstance) {
    }

    public void onFinalAction() {
    }

    public void doFinalAction() {
    }
}
