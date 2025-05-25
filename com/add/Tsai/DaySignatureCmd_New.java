package com.add.Tsai;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * 簽到指令
 *
 * @author hero
 */
public class DaySignatureCmd_New {
    private static final Log _log = LogFactory.getLog(DaySignatureCmd_New.class);
    private static DaySignatureCmd_New _instance;

    public static DaySignatureCmd_New get() {
        if (_instance == null) {
            _instance = new DaySignatureCmd_New();
        }
        return _instance;
    }

    public boolean Cmd(final L1PcInstance pc, final String cmd) {
        try {
            if (cmd.matches("day_signature")) {
                if (!checkDay(pc)) {
                    pc.sendPackets(new S_SystemMessage("\\aH今天已完成簽到獎勵"));
                    return false;
                }
                int day = pc.get_day_signature() + 1;
                Day_Signature_New daySignatureNew = Day_Signature_New.get().getDay(day);
                if (daySignatureNew == null) {
                    pc.set_day_signature(0);
                    day = pc.get_day_signature() + 1;
                    daySignatureNew = Day_Signature_New.get().getDay(day);
                }
                pc.getInventory().storeItem(daySignatureNew.get_itemId(), daySignatureNew.get_itemCount());
                pc.sendPackets(new S_SystemMessage("\\aH第" + day + "天簽到獲得簽到獎勵"));
                pc.set_day_signature(day);
                pc.set_day_signature_time(new Timestamp(System.currentTimeMillis()));
                return true;
            }
            return false;
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    private boolean checkDay(L1PcInstance pc) {
        if (pc.get_day_signature_time() == null) {
            return true;
        }
        // 获取日期部分的 Timestamp
        LocalDateTime oldDate = pc.get_day_signature_time().toLocalDateTime().toLocalDate().atStartOfDay();
        LocalDateTime currentDate = new Timestamp(System.currentTimeMillis()).toLocalDateTime().toLocalDate().atStartOfDay();
        // 判断是否跨越了日期
        if (!oldDate.equals(currentDate)) {
            // 检查时间部分是否在00:00:00之后
            return currentDate.toLocalDate().isAfter(oldDate.toLocalDate());
        }
        return false;
    }
}