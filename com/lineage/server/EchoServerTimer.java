package com.lineage.server;

import com.lineage.config.Config;
import com.lineage.echo.ServerExecutor;
import com.lineage.server.thread.GeneralThreadPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class EchoServerTimer extends TimerTask {
    private static final Log _log = LogFactory.getLog(EchoServerTimer.class);
    private static final Map<Integer, ServerExecutor> _echoList = new HashMap<>();
    private static EchoServerTimer _instance;

    public static EchoServerTimer get() {
        if (_instance == null) {
            _instance = new EchoServerTimer();
        }
        return _instance;
    }

    public void start() {
        try {
            if (_echoList.isEmpty()) {
                startEcho();
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            if (Config.RESTART_LOGIN > 0) {
                _log.warn("監聽端口重置作業啟動 間隔時間:" + Config.RESTART_LOGIN + "分鐘。");
                int timeMillis = Config.RESTART_LOGIN * 60 * 1000;
                GeneralThreadPool.get().aiScheduleAtFixedRate(this, timeMillis, timeMillis);
            }
        }
    }

    public void run() {
        try {
            _log.warn("監聽端口重置作業!");
            try {
                stopEcho();
                startEcho();
            } catch (Exception e) {
                _log.error("重新啟動端口作業失敗!!", e);
            }
        } catch (Exception e) {
            _log.error("監聽端口重置作業失敗!!", e);
        } finally {
            _log.warn("監聽端口重置作業完成!!");
        }
    }

    public void reStart() {
        try {
            if (Shutdown.SHUTDOWN) {
                return;
            }
            if (!_echoList.isEmpty()) {
                stopEcho();
                TimeUnit.MILLISECONDS.sleep(2000L);
                startEcho();
            } else {
                _log.error("監聽端口重置作業失敗(目前無任何監聽線程)!!");
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void startEcho() {
        try {
            String[] portList = Config.GAME_SERVER_PORT.split("-");
            for (String ports : portList) {
                int key = Integer.parseInt(ports);
                ServerExecutor echoServer = new ServerExecutor(key);
                if (echoServer != null) {
                    _echoList.put(key, echoServer);
                    echoServer.stsrtEcho();
                }
                TimeUnit.MILLISECONDS.sleep(100L);
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void stopEcho() {
        try {
            if (!_echoList.isEmpty()) {
                for (Integer key : _echoList.keySet()) {
                    ServerExecutor echoServer = (ServerExecutor) _echoList.get(key);
                    if (echoServer != null) {
                        echoServer.stopEcho();
                    }
                    TimeUnit.MILLISECONDS.sleep(100L);
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public boolean isPort(int key) {
        try {
            return _echoList.get(key) != null;
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    public void stopPort(int key) {
        try {
            ServerExecutor echoServer = (ServerExecutor) _echoList.get(key);
            if (echoServer != null) {
                echoServer.stopEcho();
                _echoList.remove(key);
            } else {
                _log.warn("關閉指定監聽端口 作業失敗:該端口未在作用中!");
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void startPort(int key) {
        try {
            ServerExecutor echoServer = (ServerExecutor) _echoList.get(key);
            if (echoServer == null) {
                echoServer = new ServerExecutor(key);
                _echoList.put(key, echoServer);
                echoServer.stsrtEcho();
            } else {
                _log.warn("啟用指定監聽端口 作業失敗:該端口已在作用中!");
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.EchoServerTimer JD-Core Version: 0.6.2
 */