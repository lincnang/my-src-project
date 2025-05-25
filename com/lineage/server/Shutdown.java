package com.lineage.server;

import com.lineage.data.event.MarySet;
import com.lineage.data.npc.gam.Npc_Mary;
import com.lineage.echo.ClientExecutor;
import com.lineage.echo.QuitGame;
import com.lineage.list.OnlineUser;
import com.lineage.server.datatables.InvSwapTable;
import com.lineage.server.datatables.lock.AccountReading;
import com.lineage.server.datatables.lock.CharMapTimeReading;
import com.lineage.server.datatables.lock.ServerReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Disconnect;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Account;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * 關機管理
 *
 * @author dexc
 */
public class Shutdown extends Thread {
    public static final int SIGTERM = 0;
    public static final int GM_SHUTDOWN = 1;
    public static final int GM_RESTART = 2;
    public static final int ABORT = 3;
    private static final Log _log = LogFactory.getLog(Shutdown.class);
    public static boolean SHUTDOWN = false;
    private static Shutdown _instance;
    private static Shutdown _counterInstance = null;
    private static boolean _isMsg = false;
    private int _secondsShut;
    private int _shutdownMode;
    private int _overTime = 5;

    public Shutdown() {
        _secondsShut = -1;
        _shutdownMode = 0;
    }

    /**
     * <font color=#00800>執行關機讀秒</font>
     *
     * @param seconds 時間:單位(秒)
     * @param mode    關機模式(true關機後重新啟動 false關機後不重新啟動)
     */
    public Shutdown(int seconds, boolean restart) {
        if (seconds < 0) {
            seconds = 0;
        }
        _secondsShut = seconds;
        if (restart) {
            _shutdownMode = 2;
        } else {
            _shutdownMode = 1;
        }
    }

    public static Shutdown getInstance() {
        if (_instance == null) {
            _instance = new Shutdown();
        }
        return _instance;
    }

    @Override
    public void run() {
        if (this == _instance) {
            if (_shutdownMode != 3) {
                try {
                    saveData();
                    TimeUnit.MILLISECONDS.sleep(3000L);
                    while (_overTime > 0) {
                        _log.warn("距離核心完全關閉 : " + _overTime + "秒!");
                        _overTime--;
                        TimeUnit.MILLISECONDS.sleep(1000L);
                    }
                    int list = OnlineUser.get().size();
                    _log.warn("核心關閉殘餘連線帳號數量: " + list);
                    TimeUnit.MILLISECONDS.sleep(1000L);
                    if (list <= 0) {
                        return;
                    }
                    for (String account : OnlineUser.get().map().keySet()) {
                        _log.warn("核心關閉殘留帳號: " + account);
                    }
                } catch (InterruptedException e) {
                    _log.error(e.getLocalizedMessage(), e);
                }
            }
        } else {
            countdown();
            if (_shutdownMode != 3) {
                switch (_shutdownMode) {
                    case 1:
                        _instance.setMode(1);
                        //InvSwapController.getInstance().initDB(); // 裝備切換
                        System.exit(0);
                        break;
                    case 2:
                        _instance.setMode(2);
                        System.exit(1);
                }
            }
        }
    }

    /**
     * <font color=#00800>啟動關機計時</font>
     *
     * @param pc      指令執行者
     * @param seconds 時間:單位(秒)
     * @param mode    關機模式(true關機後重新啟動 false關機後不重新啟動)
     */
    public void startShutdown(L1PcInstance activeChar, int seconds, boolean restart) {
        if (_counterInstance != null) {
            return;
        }
        if (activeChar != null) {
            _log.warn(activeChar.getName() + " 啟動關機計時: " + seconds + " 秒!");
        }
        if (_counterInstance != null) {
            _counterInstance._abort();
        }
        _counterInstance = new Shutdown(seconds, restart);
        GeneralThreadPool.get().execute(_counterInstance);
    }

    /**
     * <font color=#00800>取消關機計時</font>
     *
     * @param pc 指令執行者
     */
    public void abort(L1PcInstance activeChar) {
        if (_counterInstance != null) {
            _counterInstance._abort();
        }
    }

    /**
     * <font color=#00800>設定關機模式為(取消)</font>
     */
    private void setMode(int mode) {
        _shutdownMode = mode;
    }

    /**
     * set shutdown mode to ABORT
     */
    private void _abort() {
        _shutdownMode = 3;
    }

    private void countdown() {
        try {
            SHUTDOWN = true;
            World.get().broadcastPacketToAll(new S_ServerMessage(72, String.valueOf(_secondsShut)));
            while (_secondsShut > 0) {
                switch (_secondsShut) {
                    case 180:
                    case 240:
                    case 300:
                        EchoServerTimer.get().stopEcho();
                        break;
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 10:
                    case 30:
                    case 60:
                    case 120:
                    case 150:
                        _log.warn("關機倒數: " + _secondsShut + " 秒!");
                        World.get().broadcastPacketToAll(new S_ServerMessage(72, String.valueOf(_secondsShut)));
                }
                _secondsShut--;
                TimeUnit.MILLISECONDS.sleep(1000L);
                if ((_shutdownMode == 3) && (_secondsShut > 5)) {
                    SHUTDOWN = false;
                    EchoServerTimer.get().reStart();
                    World.get().broadcastPacketToAll(new S_ServerMessage(166, "取消關機倒數!!遊戲將會正常執行!!"));
                    return;
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            if (_shutdownMode != 3) {
                saveData();
            } else {
                _secondsShut = -1;
                _shutdownMode = 0;
                _counterInstance = null;
            }
        }
    }

    private synchronized void saveData() {
        try {
            if (MarySet.START) {
                Npc_Mary.update();
            }
            World.get().broadcastPacketToAll(new S_Disconnect());
            InvSwapTable.getInstance().initDB(); // 裝備切換
            Collection<ClientExecutor> list = OnlineUser.get().all();
            if (!_isMsg) {
                _isMsg = true;
                _log.info("人物/物品 資料的存檔 - 關閉核心前連線帳號數量: " + list.size());
            }
            for (ClientExecutor client : list) {
                L1PcInstance tgpc = client.getActiveChar();
                if (tgpc != null) {
                    QuitGame.quitGame(tgpc);
                    client.setActiveChar(null);
                    client.kick();
                    client.close();
                }
                L1Account value = client.getAccount();
                if (value != null) {
                    if (value.is_isLoad()) {
                        OnlineUser.get().remove(client.getAccountName());
                    }
                    value.set_isLoad(false);
                    AccountReading.get().updateLan(client.getAccountName(), false);
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            AccountReading.get().updateLan();
            CharMapTimeReading.get().saveAllTime();
            ServerReading.get().isStop();
        }
    }
}
