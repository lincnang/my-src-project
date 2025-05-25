package com.lineage.server.serverpackets;

import com.lineage.server.datatables.ActivityNoticeTable;
import com.lineage.server.templates.ActivityNotice;

import java.util.Calendar;

/**
 * 活動
 *
 * @author dexc
 */
public class S_EventUI extends ServerBasePacket {
    private static final String S_EventUI = "[S] S_EventUI";
    private byte[] _byte = null;

    public S_EventUI(int code) {
        writeC(S_EXTENDED_PROTOBUF);
        writeH(code);
        switch (code) {
            case 141:
                writeH(264);
                writeC(16);
                writeC(1);
                for (final ActivityNotice notic : ActivityNoticeTable.get().getAllActivityNotice().values()) {
                    String http = "https://www.google.com/";
                    int type = notic.getId();
                    String name = notic.getTitle();
                    long nowtime = System.currentTimeMillis() / 1000L;
                    Calendar calendar = Calendar.getInstance();
                    if (notic.getweek() > 0) {
                        calendar.set(Calendar.DAY_OF_WEEK, notic.getweek()); // 星期幾開始活動  用數字（1~7）表示（星期日~星期六）
                    }
                    calendar.set(Calendar.HOUR_OF_DAY, notic.gethour()); // 幾點開始活動
                    calendar.set(Calendar.MINUTE, notic.getminute()); // 幾分開始活動
                    calendar.set(Calendar.SECOND, notic.getsecond()); // 幾秒開始活動
                    long startTime = calendar.getTime().getTime() / 1000L;
                    calendar.add(Calendar.MINUTE, notic.getendtime()); // 分     活動時間
                    long endTime = calendar.getTimeInMillis() / 1000L;
                    if (nowtime > endTime) {
                        if (notic.getweek() > 0) {
                            calendar.add(Calendar.DATE, 7); // 日 幾天前
                        } else {
                            calendar.add(Calendar.DATE, 1); // 日
                        }
                        endTime = calendar.getTimeInMillis() / 1000L;
                        calendar.add(Calendar.MINUTE, -notic.getendtime()); // 分
                        startTime = calendar.getTime().getTime() / 1000L;
                    }
                    int length = http.getBytes().length + name.getBytes().length + sizeBit((int) startTime) + sizeBit((int) endTime) + 19;
                    writeC(26);
                    writeBit(length);
                    writeC(8);
                    writeC(type);
                    writeC(26);
                    writeS2(http);
                    writeC(34);
                    writeS2(name);
                    writeC(40);
                    writeBit((int) startTime);
                    writeC(48);
                    writeBit((int) endTime);
                    writeC(58);
                    writeC(9);
                    writeC(10);
                    writeS2("4654");
                    writeC(16);
                    writeBit(1000L);
                }
                writeH(0);
                break;
        }
    }

    @Override
    public byte[] getContent() {
        if (this._byte == null) {
            _byte = _bao.toByteArray();
        }
        return this._byte;
    }

    @Override
    public String getType() {
        return S_EventUI;
    }
}
