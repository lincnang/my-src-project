package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;
import javolution.util.FastTable;

public class S_Race extends ServerBasePacket {
    public static final int GameStart = 64;
    public static final int CountDown = 65;
    public static final int PlayerInfo = 66;
    public static final int Lap = 67;
    public static final int Winner = 68;
    public static final int GameOver = 69;
    public static final int GameEnd = 70;
    private byte[] _byte = null;

    public S_Race(int type) {
        writeC(S_EVENT);
        writeC(type);
        if (type == 64) {
            writeC(5);
        }
    }

    public S_Race(FastTable<L1PcInstance> playerList, L1PcInstance pc) {
        writeC(S_EVENT);
        writeC(66);
        writeH(playerList.size());
        writeH(playerList.indexOf(pc));
        for (L1PcInstance player : playerList) {
            if (player != null) {
                writeS(player.getName());
            }
        }
    }

    public S_Race(int maxLap, int lap) {
        writeC(S_EVENT);
        writeC(67);
        writeH(maxLap);
        writeH(lap);
    }

    public S_Race(String winnerName, int time) {
        writeC(S_EVENT);
        writeC(68);
        writeS(winnerName);
        writeD(time * 1000);
    }

    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    public String getType() {
        return "[S] S_Race";
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.serverpackets.S_Race JD-Core Version: 0.6.2
 */