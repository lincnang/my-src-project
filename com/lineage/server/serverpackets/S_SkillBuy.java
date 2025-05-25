package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;

public class S_SkillBuy extends ServerBasePacket {
    private static final Log _log = LogFactory.getLog(S_SkillBuy.class);
    private byte[] _byte = null;

    public S_SkillBuy(L1PcInstance pc, ArrayList<Integer> newSkillList) {
        try {
            if (newSkillList.size() <= 0) {
                writeC(S_BUYABLE_SPELL_LIST);
                writeH(0);
            } else {
                writeC(S_BUYABLE_SPELL_LIST);
                writeD(6000);
                writeH(newSkillList.size());
                for (Integer integer : newSkillList) {
                    writeD(integer.intValue());
                }
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
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
 * com.lineage.server.serverpackets.S_SkillBuy JD-Core Version: 0.6.2
 */