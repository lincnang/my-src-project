package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.list.OnlineUser;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Account;

public class C_ReturnToLogin extends ClientBasePacket {
    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            L1PcInstance pc = client.getActiveChar();
            if (pc != null) {
                client.quitGame();
            }
            L1Account account = client.getAccount();
            if (account != null) {
                OnlineUser.get().remove(account.get_login());
            }
        } catch (Exception ignored) {
        } finally {
            over();
        }
    }

    public String getType() {
        return super.getType();
    }
}