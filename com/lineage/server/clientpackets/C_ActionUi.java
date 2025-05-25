package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.ExpTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_MatizCloudia;

import java.util.Random;

public class C_ActionUi extends ClientBasePacket {
    public static final int LANK_UI = 135;
    public static final int ATTEND = 0x222;
    private static final String C_ACTION_UI = "[C] C_ActionUi";
    private static final int CLAUDIA = 524;
    Random _Random = new Random(System.nanoTime());

    public void start(byte[] decrypt, ClientExecutor client) {
        // 資料載入
        read(decrypt);
        int type = readH();
        L1PcInstance pc = client.getActiveChar();
        if (pc == null || pc.isGhost()) {
            return;
        }
        switch (type) {
            case CLAUDIA:
                //0000: f1 0c 02 03 00 08 80 02 4a 0e b9 36
                //0000: f1 0c 02 03 00 08 b4 02 82 ae 02 78
                int step = readC();
                if (step == 3 && pc.cL == 0) {
                    pc.sendPackets(new S_MatizCloudia(2));
                    pc.sendPackets(new S_MatizCloudia(3));
                    pc.sendPackets(new S_MatizCloudia(1, 0));
                    pc.getInventory().storeItem(241203, 1);
                    pc.getInventory().storeItem(241212, 1);
                    pc.setExp(ExpTable.getExpByLevel(8));
                } else {
                    pc.sendPackets(new S_MatizCloudia(4));
                    pc.setExp(ExpTable.getExpByLevel(10));
                }
                break;
        }
    }

    @Override
    public String getType() {
        return C_ACTION_UI;
    }
}
