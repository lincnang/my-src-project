package com.lineage.server.serverpackets;

import com.lineage.server.templates.L1Rank;

import java.util.Iterator;
import java.util.List;

public class S_PacketBoxGree extends ServerBasePacket {
    private byte[] _byte = null;

    public S_PacketBoxGree(String msg) {
        writeC(S_EVENT);
        writeC(84);
        writeC(2);
        writeS(msg);
    }

    /**
     * 螢幕上的效果
     * 1.螢幕框特效-紫
     * 2.螢幕搖晃
     * 3.煙火
     * 4.閃電後螢幕變黑--在變亮
     * 5.飽食度特效
     * 6-8螢幕標框特效-藍黃白
     * 10單純黑頻-在變亮
     * 11.飛龍掠過
     * 12.UI左側彈出競技場畫面
     * 13.燃燒大地 持續140秒
     */
    public S_PacketBoxGree(int type) {
        writeC(S_EVENT);
        writeC(83);
        writeD(type);
        writeC(0);
        writeC(0);
        writeC(0);
    }

    public S_PacketBoxGree(int type, String msg) {
        writeC(S_EVENT);
        writeC(84);
        writeC(type);
        writeS(msg);
    }

    public S_PacketBoxGree(List<?> totalList, int totalSize, int this_order, int this_score) {
        _byte = null;
        writeC(S_EVENT);
        writeC(112);
        writeD(0);
        writeD(totalSize);
        for (Object o : totalList) {
            L1Rank rank = (L1Rank) o;
            writeC(rank.getMemberSize());
            writeD(rank.getScore());
            writeS(rank.getPartyLeader());
            String memberName;
            for (Iterator<String> iterator1 = rank.getPartyMember().iterator(); iterator1.hasNext(); writeS(memberName)) {
                memberName = (String) iterator1.next();
            }
        }
        writeC(this_order);
        writeD(this_score);
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
