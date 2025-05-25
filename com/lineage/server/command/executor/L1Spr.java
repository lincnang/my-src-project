package com.lineage.server.command.executor;

import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.SprTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 取回指定圖形速度設置(參數:圖形編號)
 *
 * @author dexc
 */
public class L1Spr implements L1CommandExecutor {
    private static final Log _log = LogFactory.getLog(L1Spr.class);

    private L1Spr() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Spr();
    }

    @Override
    public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
        try {
            final int sprid = Integer.parseInt(arg);
            final int attack1 = SprTable.get().getAttackSpeed(sprid, ActionCodes.ACTION_Attack);
            final int attack5 = SprTable.get().getAttackSpeed(sprid, ActionCodes.ACTION_SwordAttack);
            final int attack12 = SprTable.get().getAttackSpeed(sprid, ActionCodes.ACTION_AxeAttack);
            final int attack21 = SprTable.get().getAttackSpeed(sprid, ActionCodes.ACTION_BowAttack);
            final int attack25 = SprTable.get().getAttackSpeed(sprid, ActionCodes.ACTION_SpearAttack);
            final int attack41 = SprTable.get().getAttackSpeed(sprid, ActionCodes.ACTION_StaffAttack);
            final int attack47 = SprTable.get().getAttackSpeed(sprid, ActionCodes.ACTION_DaggerAttack);
            final int attack51 = SprTable.get().getAttackSpeed(sprid, ActionCodes.ACTION_TwoHandSwordAttack);
            final int attack55 = SprTable.get().getAttackSpeed(sprid, ActionCodes.ACTION_EdoryuAttack);
            final int attack59 = SprTable.get().getAttackSpeed(sprid, ActionCodes.ACTION_ClawAttack);
            final int attack63 = SprTable.get().getAttackSpeed(sprid, ActionCodes.ACTION_ThrowingKnifeAttack);
            final int move = SprTable.get().getMoveSpeed(sprid, 0);
            final int dmg = SprTable.get().getDmg(sprid);
            final int attack18 = SprTable.get().getDirSpellSpeed(sprid);
            final int attack19 = SprTable.get().getNodirSpellSpeed(sprid);
            final int attack30 = SprTable.get().getDirSpellSpeed30(sprid);
            String info = "sprid:" + sprid + "\n\r 走路速度:" + move + "\n\r 攻擊速度-空手:" + attack1 + "\n\r 攻擊速度-單手劍:" + attack5 + "\n\r 攻擊速度-斧頭:" + attack12 + "\n\r 攻擊速度-弓:" + attack21 + "\n\r 攻擊速度-矛:" + attack25 + "\n\r 攻擊速度-杖:" + attack41 + "\n\r 攻擊速度-匕首:" + attack47 + "\n\r 攻擊速度-雙手劍:" + attack51 + "\n\r 攻擊速度-雙刀:" + attack55 + "\n\r 攻擊速度-爪:" + attack59 + "\n\r 攻擊速度-鐵手甲:" + attack63 + "\n\r 受傷動作速度:" + dmg + "\n\r 有向施法速度:" + attack18 + "\n\r 無向施法速度:" + attack19 + "\n\r 特攻30速度:" + attack30;
            if (pc == null) {
                _log.warn("系統命令執行: spr" + sprid + "\n\r" + info);
            } else {
                pc.sendPackets(new S_SystemMessage(info));
            }
        } catch (final Exception e) {
            if (pc == null) {
                _log.error("錯誤的命令格式: " + this.getClass().getSimpleName());
            } else {
                _log.error("錯誤的GM指令格式: " + this.getClass().getSimpleName() + " 執行的GM:" + pc.getName());
                // 261 \f1指令錯誤。
                pc.sendPackets(new S_ServerMessage(261));
            }
        }
    }
}
