package com.lineage.server.command.executor;

import com.lineage.config.Config;
import com.lineage.config.ConfigPRO;
import com.lineage.server.clientpackets.AcceleratorChecker;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class L1SpeedCheck implements L1CommandExecutor {//src042
    private static final Log _log = LogFactory.getLog(L1SpeedCheck.class);

    private L1SpeedCheck() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1SpeedCheck();
    }

    public Calendar getRealTime() {
        final TimeZone _tz = TimeZone.getTimeZone(Config.TIME_ZONE);
        final Calendar cal = Calendar.getInstance(_tz);
        return cal;
    }

    @Override
    public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
        try {
            final StringTokenizer stringtokenizer = new StringTokenizer(arg);
            final int move = Integer.parseInt(stringtokenizer.nextToken());
            final int attack = Integer.parseInt(stringtokenizer.nextToken());
            final int type = Integer.parseInt(stringtokenizer.nextToken());
            final int time = Integer.parseInt(stringtokenizer.nextToken());
            ConfigPRO.CHECK_MOVE_STRICTNESS = move;
            AcceleratorChecker.Setspeed();
            pc.sendPackets(new S_SystemMessage("目前移動防加速誤差值為 :" + ConfigPRO.CHECK_MOVE_STRICTNESS));
            ConfigPRO.CHECK_STRICTNESS = attack;
            AcceleratorChecker.Setspeed();
            pc.sendPackets(new S_SystemMessage("目前動作防加速誤差值為 :" + ConfigPRO.CHECK_STRICTNESS));
            ConfigPRO.PUNISHMENT_TYPE = type;
            pc.sendPackets(new S_SystemMessage("\\aG目前防加速的懲罰類行為 :" + ConfigPRO.PUNISHMENT_TYPE));
            ConfigPRO.PUNISHMENT_TIME = time;
            pc.sendPackets(new S_SystemMessage("\\aH目前防加速的懲罰時間為 :" + ConfigPRO.PUNISHMENT_TIME));
        } catch (final Exception e) {
            _log.error("錯誤的 GM 指令格式: " + this.getClass().getSimpleName() + " 執行 GM :" + pc.getName());
            pc.sendPackets(new S_SystemMessage("\\aG請輸入  移動/攻擊/類型/時間 參數。"));
        }
    }
}
