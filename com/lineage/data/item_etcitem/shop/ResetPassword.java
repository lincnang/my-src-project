package com.lineage.data.item_etcitem.shop;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

public class ResetPassword extends ItemExecutor {
    public static final Random _random = new Random();
    private static final Log _log = LogFactory.getLog(ResetPassword.class);

    public static ItemExecutor get() {
        return new ResetPassword();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        try {
            if (item == null) {
                return;
            }
            if (pc == null) {
                return;
            }
            if (pc.is_retitle()) {
                pc.sendPackets(new S_ServerMessage("\\fU你不是要變更封號嗎?"));
                return;
            }
            pc.repass(1);
            pc.getInventory().removeItem(item, 1L);
            pc.sendPackets(new S_NPCTalkReturn(pc, "y_pass_01", new String[]{"請輸入您的舊密碼"}));
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.shop.ResetPassword JD-Core Version: 0.6.2
 */