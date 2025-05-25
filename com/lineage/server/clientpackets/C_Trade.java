package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Trade;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.utils.FaceToFace;
import com.lineage.server.world.World;

/**
 * 要求交易(個人)
 *
 * @author daien
 */
public class C_Trade extends ClientBasePacket {
    //private static final Log _log = LogFactory.getLog(C_Trade.class);
    @Override
    public void start(byte[] decrypt, ClientExecutor client) {
        final L1PcInstance pc = client.getActiveChar();
        if (pc == null) {
            return;
        }
        try {
            if (pc.isGhost()) { // 鬼魂模式
                return;
            }
            if (pc.isDead()) { // 死亡
                return;
            }
            if (pc.isTeleport()) { // 傳送中
                return;
            }
            if (isTwoLogin(pc)) { // 相同帳號檢測
                return;
            }
            L1PcInstance target = FaceToFace.faceToFace(pc);
            // 取回交易對象(輸入指令者)
            L1PcInstance srcTrade = (L1PcInstance) World.get().findObject(pc.getTradeID());
            if (srcTrade != null) {
                L1Trade trade = new L1Trade();
                trade.tradeCancel(srcTrade);
                return;
            }
            // 取回交易對象(接收指令者)
            L1PcInstance srcTradetarget = (L1PcInstance) World.get().findObject(target.getTradeID());
            if (srcTradetarget != null) {
                L1Trade trade = new L1Trade();
                trade.tradeCancel(srcTradetarget);
                return;
            }
            // 配對
            if ((target != null) && (!target.isParalyzed())) {
                //pc.get_trade_clear();
                //target.get_trade_clear();
                pc.setTradeID(target.getId()); // 保存相互交易對象OBJID
                target.setTradeID(pc.getId());
                target.sendPackets(new S_Message_YN(pc.getName()));
            }
        } catch (final Exception e) {
        } finally {
            over();
        }
    }

    /**
     * 檢查世界玩家當中是否有兩個相同帳號的PC
     *
     * @param c
     * @return
     */
    private boolean isTwoLogin(L1PcInstance c) {
        boolean bool = false;
        for (L1PcInstance target : World.get().getAllPlayersToArray()) {
            if (c.getId() != target.getId() && !target.isPrivateShop()) {
                if (c.getNetConnection().getAccountName().equalsIgnoreCase(target.getNetConnection().getAccountName())) {
                    bool = true;
                    break;
                }
            }
        }
        return bool;
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }
}
