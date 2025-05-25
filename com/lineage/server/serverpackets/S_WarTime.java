package com.lineage.server.serverpackets;

import com.lineage.config.Config;
import com.lineage.server.datatables.lock.CastleReading;
import com.lineage.server.templates.L1Castle;

import java.util.Calendar;

public class S_WarTime extends ServerBasePacket {
    private byte[] _byte = null;

    public S_WarTime(Calendar cal) {
        Calendar base_cal = Calendar.getInstance();
        base_cal.set(1997, Calendar.JANUARY, 1, 17, 0);
        long base_millis = base_cal.getTimeInMillis();
        long millis = cal.getTimeInMillis();
        long diff = millis - base_millis;
        diff -= 72000000L;
        diff /= 60000L;
        int time = (int) (diff / 182L);
        writeC(S_SELECTABLE_TIME_LIST);//S_OPCODE_WARTIME
        writeH(6);
        writeS(Config.TIME_ZONE);
        writeC(0);
        writeC(0);
        writeC(0);
        writeD(time);
        writeC(0);
        writeD(time - 1);
        writeC(0);
        writeD(time - 2);
        writeC(0);
        writeD(time - 3);
        writeC(0);
        writeD(time - 4);
        writeC(0);
        writeD(time - 5);
        writeC(0);
    }

    public S_WarTime(int op) {
        L1Castle l1castle = CastleReading.get().getCastleTable(5);
        Calendar cal = l1castle.getWarTime();
        Calendar base_cal = Calendar.getInstance();
        base_cal.set(1997, Calendar.JANUARY, 1, 17, 0);
        long base_millis = base_cal.getTimeInMillis();
        long millis = cal.getTimeInMillis();
        long diff = millis - base_millis;
        diff -= 72000000L;
        diff /= 60000L;
        int time = (int) (diff / 182L);
        writeC(op);
        writeH(6);
        writeS(Config.TIME_ZONE);
        writeC(0);
        writeC(0);
        writeC(0);
        writeD(time);
        writeC(0);
        writeD(time - 1);
        writeC(0);
        writeD(time - 2);
        writeC(0);
        writeD(time - 3);
        writeC(0);
        writeD(time - 4);
        writeC(0);
        writeD(time - 5);
        writeC(0);
    }

    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.serverpackets.S_WarTime JD-Core Version: 0.6.2
 */