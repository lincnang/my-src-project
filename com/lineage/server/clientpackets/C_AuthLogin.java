package com.lineage.server.clientpackets;

import com.add.L1Config;
import com.lineage.commons.system.LanSecurityManager;
import com.lineage.config.Config;
import com.lineage.config.ConfigIpCheck;
import com.lineage.data.event.NetBarSet;
import com.lineage.echo.ClientExecutor;
import com.lineage.list.OnlineUser;
import com.lineage.server.datatables.lock.AccountReading;
import com.lineage.server.datatables.lock.IpReading;
import com.lineage.server.serverpackets.S_CommonNews;
import com.lineage.server.serverpackets.S_Disconnect;
import com.lineage.server.serverpackets.S_LoginResult;
import com.lineage.server.templates.L1Account;
import com.lineage.server.templates.L1AccountsCount;
import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 要求登入伺服器
 *
 * @author dexc
 */
public class C_AuthLogin extends ClientBasePacket {
    private static final Log _log = LogFactory.getLog(C_AuthLogin.class);
    /*
     * public C_AuthLogin() { } public C_AuthLogin(final byte[] abyte0, final
     * ClientExecutor client) { super(abyte0); try { this.start(abyte0, client); }
     * catch (final Exception e) { _log.error(e.getLocalizedMessage(), e); } }
     */
    /*
     * private static final String[] _check_accname = new String[]{ "a","b","c","d"
     * ,"e","f","g","h","i","j","k","l","m","n","o","p","q","r","s"
     * ,"t","u","v","w","x","y","z", "0","1","2","3","4","5","6","7","8","9", };
     * private static final String[] _check_pwd = new String[]{ "a","b","c","d","e"
     * ,"f","g","h","i","j","k","l","m","n","o","p","q","r","s"
     * ,"t","u","v","w","x","y","z", "0","1","2","3","4","5","6","7","8","9",
     * "!","_","=","+","-","*","^","#","?",".", };
     */
    private static final String _check_accname = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final String _check_pwd = "abcdefghijklmnopqrstuvwxyz0123456789!_=+-?.#";
    private static ArrayList<L1AccountsCount> accountslist = new ArrayList<L1AccountsCount>();

    private static boolean Ishave(String i) {
        for (L1AccountsCount j : accountslist) {
            if (i.equalsIgnoreCase(j.get_Accounts())) {
                return true;
            }
        }
        return false;
    }

    private static void update(String i) {
        for (L1AccountsCount j : accountslist) {
            if (i.equalsIgnoreCase(j.get_Accounts())) {
                j.set_TIMES1(System.currentTimeMillis());
            }
        }
    }

    private static long get(String i) {
        for (L1AccountsCount j : accountslist) {
            if (i.equalsIgnoreCase(j.get_Accounts())) {
                return j.get_TIMES1();
            }
        }
        return 0;
    }

    private static void Insert(String i) {
        L1AccountsCount ipc = new L1AccountsCount(i, System.currentTimeMillis());
        accountslist.add(ipc);
    }

    @Override
    public void start(final byte[] decrypt, final ClientExecutor client) {
        try {
            // 資料載入
            read(decrypt);
            /*
             * if (Config.Bean_Fun) { final int action = readC(); switch (action) { case
             * 0x06: // 登入請求
             */
            boolean iserror = false;
            // 登入名稱轉為小寫
            final String loginName = readS().toLowerCase();
            if (loginName.length() > 12) {
                _log.warn("不合法的帳號長度:" + client.getIp().toString() + "/" + loginName);
                client.set_error(client.get_error() + 1);
                return;
            }
            for (int i = 0; i < loginName.length(); i++) {
                final String ch = loginName.substring(i, i + 1);
                if (!_check_accname.contains(ch)) {
                    _log.warn("不被允許的帳號字元!");
                    iserror = true;
                    break;
                }
            }
            final String password = readS();
            if (password.length() > 13) {
                _log.warn("不合法的密碼長度:" + client.getIp().toString());
                client.set_error(client.get_error() + 1);
                return;
            }
            for (int i = 0; i < password.length(); i++) {
                final String ch = password.substring(i, i + 1);
                if (!_check_pwd.contains(ch.toLowerCase())) {
                    _log.warn("不被允許的密碼字元!");
                    iserror = true;
                    break;
                }
            }
            if (!iserror) {
                checkLogin(client, loginName, password, false);
            } else {
                client.out().encrypt(new S_LoginResult(S_LoginResult.EVENT_PASS_CHECK));
            }
        } catch (final Exception e) {
            // _log.error(e.getLocalizedMessage(), e);
        } finally {
            over();
        }
    }

    @SuppressWarnings("unlikely-arg-type")
    public void checkLogin(final ClientExecutor client, final String loginName, final String password, final boolean auto) {
        try {
            if (loginName == null) {
                return;
            }
            if (loginName.equals("")) {
                return;
            }
            if (password == null) {
                return;
            }
            if (password.equals("")) {
                return;
            }
            final StringBuilder ip = client.getIp();
            StringBuilder mac = client.getMac();
            // 帳號禁止登入
            if (LanSecurityManager.BANNAMEMAP.containsKey(loginName)) {
                _log.warn("禁止登入帳號位置: account=" + loginName + " host=" + client.getIp());
                client.out().encrypt(new S_LoginResult(S_LoginResult.EVENT_CANT_USE));
                final KickTimeController kickTime = new KickTimeController(client, null);
                kickTime.schedule();
                return;
            }
            boolean isError = false;
            L1Account account = AccountReading.get().getAccount(loginName);
            /** [原碼] IP登入數限制 */
            int tongip = L1Config._4001;
            if (NetBarSet.EXIPLIST.containsKey(ip.toString())) {// 網咖登入例外清單中
                tongip = NetBarSet.EXIPLIST.get(ip.toString());
            }
            if (tongip != 0) {
                int k = 0;
                for (ClientExecutor tempClient : OnlineUser.get().all()) {
                    if (ip.toString().equalsIgnoreCase(tempClient.getIp().toString())) {
                        k++;
                        if (k > (tongip - 1)) {
                            client.out().encrypt(new S_LoginResult(S_LoginResult.EVENT_LAN_ERROR));
                            return;
                        }
                    }
                }
            }
            /** End */
            if (account == null) {
                if (Config.AUTO_CREATE_ACCOUNTS) {
                    if (mac == null) {
                        mac = ip;
                    }
                    account = AccountReading.get().create(loginName, password, ip.toString(), mac.toString(), "未建立超級密碼");
                } else {
                    if (auto) {
                        client.out().encrypt(new S_LoginResult(S_LoginResult.EVENT_ERROR_USER));
                        return;
                    }
                    client.out().encrypt(new S_LoginResult(S_LoginResult.REASON_ACCESS_FAILED));
                    isError = true;
                }
            }
            // 驗證密碼
            if (!account.get_password().equals(password) && !isError) {
                if (auto) {
                    client.out().encrypt(new S_LoginResult(S_LoginResult.EVENT_ERROR_PASS));
                } else {
                    client.out().encrypt(new S_LoginResult(S_LoginResult.REASON_ACCESS_FAILED));
                }
                isError = true;
            }
            // 人數上限
            if (OnlineUser.get().isMax() && !isError) {
                _log.info("人數已達上限");
                client.out().encrypt(new S_LoginResult(S_LoginResult.EVENT_LAN_ERROR));
                isError = true;
            }
            if (isError) {
                final int error = client.get_error();
                client.set_error(error + 1);
                return;
            }
            final ClientExecutor inGame = OnlineUser.get().get(loginName);
            if (inGame != null) {
                _log.info("相同帳號重複登入: account=" + loginName + " host=" + ip);
                client.out().encrypt(new S_LoginResult(S_LoginResult.REASON_ACCOUNT_IN_USE));
                final KickTimeController kickTime = new KickTimeController(client, inGame);
                kickTime.schedule();
            } else {
                if ((account.get_server_no() != 0) && (account.get_server_no() != Config.SERVERNO)) {
                    _log.info("帳號登入其他服務器: account=" + loginName + " host=" + ip + " 已經登入:" + account.get_server_no() + "伺服器");
                    client.out().encrypt(new S_LoginResult(S_LoginResult.REASON_ACCOUNT_IN_USE));
                    final KickTimeController kickTime = new KickTimeController(client, null);
                    kickTime.schedule();
                    return;
                }
                // 帳號已經登入
                if (account.is_isLoad()) {
                    _log.info("相同帳號重複登入: account=" + loginName + " host=" + ip);
                    client.out().encrypt(new S_LoginResult(S_LoginResult.REASON_ACCOUNT_IN_USE));
                    final KickTimeController kickTime = new KickTimeController(client, null);
                    kickTime.schedule();
                    return;
                }
                if (Ishave(loginName)) {// 已在登入間隔時間檢測時間列表的帳號清單內
                    long lasttime = get(loginName);
                    long nowtime = System.currentTimeMillis();
                    if ((lasttime + (ConfigIpCheck.ACCLOGINTIMEMILLIS * 1000)) < nowtime) {
                        // 增加連線用戶資料
                        if (OnlineUser.get().addClient(account, client)) {
                            _log.info("增加連線用戶資料: account=" + loginName + " host=" + ip);
                            update(loginName);// 小於間隔時間視為正常登入更新帳號清單內的時間
                            account.set_ip(ip.toString());
                            if (mac != null) {
                                account.set_mac(mac.toString());
                            }
                            AccountReading.get().updateLastActive(account);
                            client.setAccount(account);
                            client.out().encrypt(new S_LoginResult(S_LoginResult.REASON_LOGIN_OK));
                            if (Config.NEWS) {
                                // 顯示公告
                                client.out().encrypt(new S_CommonNews());
                            } else {
                                // 進入人物列表
                                final C_CommonClick common = new C_CommonClick();
                                common.start(null, client);
                            }
                        }
                    } else {// 不滿足間隔時間 偵測啟動
                        if (ConfigIpCheck.ACCLOGINBANIP) {
                            if (!LanSecurityManager.BANIPMAP.containsKey(ip)) {
                                // 加入IP封鎖
                                IpReading.get().add(ip.toString(), "帳號登入間隔限制封鎖IP");
                            }
                            if (!LanSecurityManager.BANNAMEMAP.containsKey(account.get_login())) {
                                // 加入封鎖帳號
                                IpReading.get().add(account.get_login(), "帳號登入間隔限制封鎖帳號");
                            }
                        }
                        client.kick();
                        _log.info("拒絕帳號多登(登入間隔時間偵測)。帳號：" + loginName + " 客戶端IP：" + ip);
                    }
                } else {
                    // 增加連線用戶資料
                    if (OnlineUser.get().addClient(account, client)) {
                        _log.info("增加連線用戶資料: account=" + loginName + " host=" + ip);
                        Insert(loginName);// 加入帳號登入間隔限制時間列表
                        account.set_ip(ip.toString());
                        if (mac != null) {
                            account.set_mac(mac.toString());
                        }
                        AccountReading.get().updateLastActive(account);
                        client.setAccount(account);
                        client.out().encrypt(new S_LoginResult(S_LoginResult.REASON_LOGIN_OK));
                        if (Config.NEWS) {
                            // 顯示公告
                            client.out().encrypt(new S_CommonNews());
                        } else {
                            // 進入人物列表
                            final C_CommonClick common = new C_CommonClick();
                            common.start(null, client);
                        }
                    }
                }
                // 增加連線用戶資料
                /*
                 * if (OnlineUser.get().addClient(account, client)) {
                 * _log.info("增加連線用戶資料: account=" + loginName + " host=" + ip);
                 * account.set_ip(ip.toString()); if (mac != null) {
                 * account.set_mac(mac.toString()); }
                 * AccountReading.get().updateLastActive(account);
                 *
                 * client.setAccount(account); // 註冊測試 // client.out().encrypt(new //
                 * S_LoginResult(S_LoginResult.EVENT_REGISTER_ACCOUNTS));
                 *
                 * client.out().encrypt(new S_LoginResult(S_LoginResult.REASON_LOGIN_OK));
                 *
                 * if (Config.NEWS) { // 顯示公告 client.out().encrypt(new S_CommonNews());
                 *
                 * } else { // 進入人物列表 final C_CommonClick common = new C_CommonClick();
                 * common.start(null, client); }
                 *
                 * // 測試公告視窗 // ATest test = new ATest(); test.start(client);//
                 *
                 * }
                 */
            }
        } catch (final Exception e) {
            // _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }

    /**
     * 斷線延遲軸
     *
     * @author dexc
     */
    private static class KickTimeController implements Runnable {
        private ClientExecutor _kick1 = null;
        private ClientExecutor _kick2 = null;

        private KickTimeController(final ClientExecutor kick1, final ClientExecutor kick2) {
            _kick1 = kick1;
            _kick2 = kick2;
        }

        private void schedule() {
            GeneralThreadPool.get().execute(this);
        }

        @Override
        public void run() {
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
                _kick1.out().encrypt(new S_Disconnect());
                TimeUnit.MILLISECONDS.sleep(1000);
                _kick1.set_error(10);
                if (_kick2 != null) {
                    _kick2.set_error(10);
                }
                // XXX
                // this.finalize();
            } catch (final InterruptedException e) {
                _log.error(e.getLocalizedMessage(), e);
                /*
                 * } catch (Throwable e) { _log.error(e.getLocalizedMessage(), e);
                 */
            }
        }
    }
}