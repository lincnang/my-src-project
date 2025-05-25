/*
 * Copyright (C) 2023
 *
 */
package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 傳送GM Message
 */
public class S_GmMessage extends ServerBasePacket {
    private final static String AM = "\\aG[%s] \\aD%s: %s";
    private final static String[] TYPES = {"王族", "騎士", "妖精", "法師", "黑暗妖精", "龍騎士", "幻術師", "戰士", "管理員"};
    private final static String LOGIN = "\\aE[%s][%s](%s) 登錄。";
    private final static String SYS = "\\aD[系統] %s%s";

    /**
     *
     */
    public S_GmMessage(L1PcInstance pc, L1PcInstance target, int type, String text) {
        final String t;
        final String to;
        switch (type) {
            // 一般頻道
            case 0:
                t = "一般";
                break;
            // 私密頻道
            case 1:
                t = "私密";
                break;
            // 大喊頻道
            case 2:
                t = "大喊";
                break;
            // 全體頻道
            case 3:
                t = "全體";
                return;
            // 血盟頻道
            case 4:
                t = "血盟";
                break;
            // 隊伍頻道
            case 11:
            case 14:
                t = "隊伍";
                break;
            // 交易頻道
            case 12:
                t = "交易";
                return;
            // 聯盟頻道
            case 13:
                t = "聯盟";
                break;
            default:
                t = "未知";
                return;
        }
        to = target == null ? pc.getName() : (pc.getName() + "->" + target.getName());
        writeC(S_MESSAGE);
        writeC(9);
        writeS(String.format(AM, t, to, text));
    }

    public S_GmMessage(L1PcInstance pc) {
        String type = pc.isGm() || pc.isMonitor() ? TYPES[8] : TYPES[pc.getType()];
        writeC(S_MESSAGE);
        writeC(9);
        writeS(String.format(LOGIN, pc.getName(), type, pc.getNetConnection().getIp()));
    }

    public S_GmMessage(String text, String CLR) {
        writeC(S_MESSAGE);
        writeC(9);
        writeS(String.format(SYS, CLR, text));
    }

    public S_GmMessage(String text) {
        writeC(S_MESSAGE);
        writeC(9);
        writeS(String.format("\\aD%s%s", "\\aD", text));
    }

    @Override
    public final byte[] getContent() {
        return getBytes();
    }
}
