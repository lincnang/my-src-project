package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.lock.AccountReading;
import com.lineage.server.serverpackets.S_LoginResult;
import com.lineage.server.templates.L1Account;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class C_NewAccess extends ClientBasePacket {
    private static final Log _log = LogFactory.getLog(C_NewAccess.class);

    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            read(decrypt);
            String loginName = readS();
            String password = readS();
            StringBuilder ip = client.getIp();
            StringBuilder mac = client.getMac();
            if (mac == null) {
                mac = ip;
            }
            _log.info("E-MAIL: " + readS());
            String spwd = readS();
            _log.info("TYPE: " + readC());
            _log.info("ADDRESS: " + readS());
            _log.info("PHONE: " + readS());
            _log.info("FAX: " + readS());
            L1Account account = AccountReading.get().getAccount(loginName);
            if (account == null) {
                account = AccountReading.get().create(loginName, password, ip.toString(), mac.toString(), spwd);
                client.out().encrypt(new S_LoginResult(4));
            }
            client.out().encrypt(new S_LoginResult(7));
        } catch (Exception localException) {
        } finally {
            over();
        }
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.clientpackets.C_NewAccess JD-Core Version: 0.6.2
 */