package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.T_OnlineGiftTable;
import com.lineage.server.datatables.T_RankTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.*;

/**
 * 自動登入
 *
 * @author dexc
 */
public class C_AutoLogin extends ClientBasePacket {
    // 自動登錄伺服器 (省略輸入帳密的步驟)
    private static final int AUTO_LOGIN = 0x00;
    private static final int MENTOR_SYSTEM = 0x0d;

    /*
     * public C_5M() { }
     *
     * public C_5M(final byte[] abyte0, final ClientExecutor client) {
     * super(abyte0); try { this.start(abyte0, client);
     *
     * } catch (final Exception e) { _log.error(e.getLocalizedMessage(), e); } }
     */
    @Override
    public void start(final byte[] decrypt, final ClientExecutor client) {
        try {
            // 資料載入
            this.read(decrypt);
            final int mode = readC();
            // 請求使用樂豆自動登錄伺服器 (尚未實裝)
            if (mode == AUTO_LOGIN) {
                // 讀取帳號
                final String loginName = this.readS().toLowerCase();
                // 讀取密碼
                final String password = this.readS();
                C_AuthLogin authLogin = new C_AuthLogin();
                authLogin.checkLogin(client, loginName, password, true);
                return;
            }
            final L1PcInstance pc = client.getActiveChar();
            if (pc == null) {
                return;
            }
            if (mode == MENTOR_SYSTEM) {
            } else if (mode == 14) {
                T_OnlineGiftTable.get().receive(pc);
            } else if (mode == 15) {
                final int type3 = readC();
                final T_RankTable rankTable = T_RankTable.get();
                if (type3 == 0) {
                    final String name = pc.getName();
                    pc.sendPackets(new S_RankedMine(rankTable.getLevelName().indexOf(name) + 1, rankTable.getClanName().indexOf(pc.getClanname()) + 1, rankTable.getWeaponName().indexOf(name) + 1, rankTable.getWealthName().indexOf(name) + 1, rankTable.getConsumeName().indexOf(name) + 1, rankTable.getKillName().indexOf(name) + 1));
                } else if (type3 == 1) {
                    pc.sendPackets(new S_RankedLevel(rankTable.getLevelList()));
                } else if (type3 == 2) {
                    pc.sendPackets(new S_RankedClan(rankTable.getClanNameList()));
                } else if (type3 == 3) {
                    pc.sendPackets(new S_RankedWeapon(rankTable.getWeaponNameList()));
                } else if (type3 == 4) {
                    pc.sendPackets(new S_RankedWealth(rankTable.getWealthName()));
                    pc.sendPackets(new S_RankedConsumption(rankTable.getConsumeName()));
                } else if (type3 == 6) {
                    pc.sendPackets(new S_RankedKill(rankTable.getKillNameList()));
                }
            }
        } catch (final Exception e) {
            // _log.error(e.getLocalizedMessage(), e);
        } finally {
            this.over();
        }
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
