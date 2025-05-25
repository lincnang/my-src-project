package com.lineage.server.command.executor;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_SystemMessage;

import java.util.StringTokenizer;

/**
 * 測試常駐技能狀態圖示型號代碼
 * 1次顯示10個 例如 .指令 460 會顯示460-469之間的圖示 如測試顯示過多 可重登下消除
 */
public class L1BoxIcon implements L1CommandExecutor {
    public static L1CommandExecutor getInstance() {
        return new L1BoxIcon();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer st = new StringTokenizer(arg);
            int type = 0;// 圖示編號
            if (st.hasMoreTokens()) {
                type = Integer.parseInt(st.nextToken());
            }
            for (int i = 0; i < 10; i++) {
                pc.sendPackets(new S_PacketBox(S_PacketBox.NONE_TIME_ICON, 1, type + i));// 開啟圖示
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + " BoxIcon 請輸入 id 編碼。"));
        }
    }
}
