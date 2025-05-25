package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1DeInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;

public class S_WhoCharinfo extends ServerBasePacket {
    private byte[] _byte = null;

    public S_WhoCharinfo(L1Character cha) {
        String lawfulness = "";
        int lawful = cha.getLawful();
        if (lawful < 0) {
            lawfulness = "($1503)";
        } else if ((lawful >= 0) && (lawful < 500)) {
            lawfulness = "($1502)";
        } else if (lawful >= 500) {
            lawfulness = "($1501)";
        }
        writeC(S_MESSAGE_CODE);
        writeH(166);
        writeC(1);
        String title = "";
        String clan = "";
        if (!cha.getTitle().equalsIgnoreCase("")) {
            title = cha.getTitle() + " ";
        }
        String name = "";
        if ((cha instanceof L1DeInstance)) {
            L1DeInstance de = (L1DeInstance) cha;
            name = de.getNameId();
            if (de.getClanid() > 0) {
                clan = "[" + de.getClanname() + "]";
            }
        } else if ((cha instanceof L1PcInstance)) {
            L1PcInstance pc = (L1PcInstance) cha;
            name = pc.getName();
            if (pc.getClanid() > 0) {
                clan = "[" + pc.getClanname() + "]";
            }
        }
        writeS(title + name + " " + lawfulness + " " + clan);
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
 * com.lineage.server.serverpackets.S_WhoCharinfo JD-Core Version: 0.6.2
 */