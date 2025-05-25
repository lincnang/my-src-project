package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.lock.AccountReading;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Account;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

public class C_Password extends ClientBasePacket {
    private static final Log _log = LogFactory.getLog(C_Password.class);
    private static final List<Integer> password = new ArrayList<>();

    static {
        password.add(0, 994303243);
        password.add(1, 994303242);
        password.add(2, 994303241);
        password.add(3, 994303240);
        password.add(4, 994303247);
        password.add(5, 994303246);
        password.add(6, 994303245);
        password.add(7, 994303244);
        password.add(8, 994303235);
        password.add(9, 994303234);
    }

    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            read(decrypt);
            L1PcInstance pc = client.getActiveChar();
            if (pc == null) {
                return;
            }
            int type = readC();
            int pass1 = password.indexOf(readD()) * 100000 + password.indexOf(readD()) * 10000 + password.indexOf(readD()) * 1000 + password.indexOf(readD()) * 100 + password.indexOf(readD()) * 10 + password.indexOf(readD());
            L1Account account = client.getAccount();
            if (type == 0) {
                int pass2 = password.indexOf(readD()) * 100000 + password.indexOf(readD()) * 10000 + password.indexOf(readD()) * 1000 + password.indexOf(readD()) * 100 + password.indexOf(readD()) * 10 + password.indexOf(readD());
                if ((pass1 < 0) && (pass2 < 0)) {
                    pc.sendPackets(new S_ServerMessage(79));
                } else if ((pass1 < 0) && (account.get_warehouse() == 0)) {
                    account.set_warehouse(pass2);
                    AccountReading.get().updateWarehouse(account.get_login(), pass2);
                    pc.sendPackets(new S_SystemMessage("倉庫密碼設定完成，請牢記您的新密碼。"));
                } else if ((pass1 > 0) && (pass1 == account.get_warehouse())) {
                    if (pass1 == pass2) {
                        pc.sendPackets(new S_ServerMessage(342));
                        stopAction(client, pc);
                        return;
                    }
                    account.set_warehouse(pass2);
                    AccountReading.get().updateWarehouse(account.get_login(), pass2);
                } else {
                    pc.sendPackets(new S_ServerMessage(835));
                    int error = client.get_error();
                    client.set_error(error + 1);
                    _log.error(pc.getName() + " 倉庫密碼輸入錯誤!!( " + client.get_error() + " 次)");
                }
            } else if (account.get_warehouse() == pass1) {
                int objid = readD();
                if (pc.getLevel() >= 5) {
                    if (type == 1) {// 個人倉庫
                        L1Object obj = World.get().findObject(objid);
                        if ((obj != null) && ((obj instanceof L1NpcInstance))) {
                            L1NpcInstance npc = (L1NpcInstance) obj;
                            switch (npc.getNpcId()) {
                                case 60028:
                                    if (!pc.isElf()) {
                                        break;
                                    }
                                    pc.sendPackets(new S_RetrieveElfList(objid, pc));
                                    break;
                                default:
                                    pc.sendPackets(new S_RetrieveList(objid, pc));
                                    break;
                            }
                        }
                    } else if (type == 2) {// 血盟倉庫
                        if (pc.getClanid() == 0) {
                            pc.sendPackets(new S_ServerMessage(208));
                            return;
                        }
                        if (pc.getClanRank() == 2) {
                            pc.sendPackets(new S_ServerMessage(728));
                            return;
                        }
                        pc.sendPackets(new S_RetrievePledgeList(objid, pc));
                    } else if (type == 4) {// 角色專屬倉庫
                        pc.sendPackets(new S_RetrieveChaList(objid, pc));
                    }
                }
            } else {
                pc.sendPackets(new S_ServerMessage(835));
            }
            stopAction(client, pc);
        } catch (Exception localException) {
        } finally {
            over();
        }
    }

    private void stopAction(ClientExecutor client, L1PcInstance pc) {
        pc.setTempID(0);
        client.set_error(0);
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.clientpackets.C_Password JD-Core Version: 0.6.2
 */