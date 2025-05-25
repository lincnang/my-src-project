package com.lineage.server.serverpackets;

import com.add.MJBookQuestSystem.Compensator.WeekQuestCompensator;
import com.add.MJBookQuestSystem.Loader.MonsterBookCompensateLoader;
import com.lineage.server.model.Instance.L1PcInstance;

import java.util.ArrayList;

/**
 * 周任務
 **/
public class S_WeekQuest extends ServerBasePacket {
    private static final byte[] WQLIST_ANONYMOUSE = new byte[]{0x1A, 0x0B, 0x08, 0x01, 0x10, (byte) 0x84, (byte) 0xA0, (byte) 0xB8, 0x03, 0x18, (byte) 0xC0, (byte) 0x87, 0x01, 0x1A, 0x0B, 0x08, 0x01, 0x10, (byte) 0xB5, (byte) 0xBF, (byte) 0xF0, 0x06, 0x18, (byte) 0xD8, (byte) 0x87, 0x01,};
    private byte[] _byte = null;

    public S_WeekQuest() {
    }

    /**
     * pc 發送實例的每周任務列表
     **/
    public void writeWQList(L1PcInstance pc) {
        MonsterBookCompensateLoader compensator = MonsterBookCompensateLoader.getInstance();
        ArrayList<WeekQuestCompensator> list = compensator.getWeekCompensators();
        try {
            byte[] comp1 = list.get(0).getSerialize();
            byte[] comp2 = list.get(1).getSerialize();
            byte[] comp3 = list.get(2).getSerialize();
            byte[] progress = pc.getWeekQuest().getSerialize();
            int size = 0;
            size += WQLIST_ANONYMOUSE.length;
            size += comp1.length + comp2.length + comp3.length;
            size += progress.length;
            size += 2;
            writeC(S_EXTENDED_PROTOBUF);
            writeH(0x032A);
            writeC(0x0A);
            writeC(size);
            writeC(0x01);
            writeC(0x0A);
            writeC(0x55);
            writeByte(comp1);
            writeByte(comp2);
            writeByte(comp3);
            writeByte(WQLIST_ANONYMOUSE);
            writeByte(progress);
            writeH(0x00);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通知當前任務的更新. difficulty : 0, 1, 2 難度，section : 0, 1, 2 節
     * count : 捕獲次數（自動標記清除的最大/最大值）
     **/
    public void writeWQUpdate(int difficulty, int section, int count) {
        writeSignature(0x032D);
        writeByte(new byte[]{0x08, (byte) (difficulty & 0xff), 0x10, (byte) (section & 0xff), 0x18, (byte) (count & 0xff), 0x00, 0x00});
    }
    /** 發送行清除相關的數據包. **/
    /**
     * difficulty : 困難 status : 狀態值 (1=僅啟用，3 =發送清除消息（至事件通知）並激活補償按鈕,
     * 5=顯示行清除並禁用補償按鈕) (4=禁用該行......您可以將其用於每周任務更新)
     **/
    public void writeWQLineClear(int difficulty, int status) {
        writeSignature(0x032E);
        writeByte(new byte[]{0x08, (byte) (difficulty & 0xff), 0x10, (byte) (status & 0xff), 0x00, 0x00});
    }

    private void writeSignature(int type) {
        writeC(S_EXTENDED_PROTOBUF);
        writeH(type);
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = _bao.toByteArray();
        }
        return _byte;
    }

    @Override
    public String getType() {
        return "S_WeekQuest";
    }
}
