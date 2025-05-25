package com.lineage.server;

import com.lineage.list.OnlineUser;
import com.lineage.server.command.GMCommands;
import com.lineage.server.utils.SystemUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CmdEcho {
    private static final Log _log = LogFactory.getLog(CmdEcho.class);

    public CmdEcho(long timer) {
        String info = "\n\r--------------------------------------------------" + (timer != 0L ? "\n       啟動指令視窗監聽器!!遊戲伺服器啟動完成!!\n       服務器啟動耗用時間: " + timer + "ms" : new StringBuilder("\n       目前連線帳號: ").append(OnlineUser.get().size()).toString()) + "\n\r--------------------------------------------------";
        _log.warn(info);
        SystemUtil.printMemoryUsage(_log);
    }

    public void runCmd() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String str = br.readLine();
            GMCommands.getInstance().handleCommands(str);
            reRunCmd();
        } catch (IOException e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private void reRunCmd() {
        CmdEcho cmdEcho = new CmdEcho(0L);
        cmdEcho.runCmd();
    }
}
