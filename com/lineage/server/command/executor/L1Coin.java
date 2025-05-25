package com.lineage.server.command.executor;

import com.lineage.config.ConfigOtherSet2;
import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.StringTokenizer;

/**
 * 變更兌換商城幣比值(參數:倍率)
 *
 * @author dexc
 */
public class L1Coin implements L1CommandExecutor {
    private static final Log _log = LogFactory.getLog(L1Coin.class);

    private L1Coin() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Coin();
    }

    @Override
    public void execute(final L1PcInstance pc, final String cmdName, final String arg) {
        try {
            final StringTokenizer st = new StringTokenizer(arg);
            final int rateXp = Integer.parseInt(st.nextToken(), 10);
            _log.warn("系統命令執行: " + cmdName + " 變更兌換商城幣比值" + arg);
            if ((int) ConfigOtherSet2.CHANGE_COUNT == rateXp) {
                _log.warn("目前商城幣已經是: " + rateXp);
                return;
            } else if (ConfigOtherSet2.CHANGE_COUNT < rateXp) {
                ConfigOtherSet2.CHANGE_COUNT = rateXp;
            } else if (ConfigOtherSet2.CHANGE_COUNT > rateXp) {
                ConfigOtherSet2.CHANGE_COUNT = rateXp;
            }
            _log.warn("目前商城幣比值變更為: " + rateXp);
        } catch (final Exception e) {
            _log.error("錯誤的命令格式: " + this.getClass().getSimpleName());
        }
    }
}
