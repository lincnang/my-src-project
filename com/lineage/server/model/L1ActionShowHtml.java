package com.lineage.server.model;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1Quest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

public class L1ActionShowHtml {
    private static final Log _log = LogFactory.getLog(L1ActionShowHtml.class);
    private final L1PcInstance _pc;

    public L1ActionShowHtml(L1PcInstance pc) {
        _pc = pc;
    }

    public void showQuestMap(int page) {
        try {
            Map<Integer, L1Quest> list = _pc.get_otherList().QUESTMAP;
            if (list == null) {
                return;
            }
            int allpage = list.size() / 10;
            if ((page > allpage) || (page < 0)) {
                page = 0;
            }
            if (list.size() % 10 != 0) {
                allpage++;
            }
            _pc.get_other().set_page(page);
            int showId = page * 10;
            StringBuilder stringBuilder = new StringBuilder();
            for (int key = showId; key < showId + 10; key++) {
                L1Quest quest = (L1Quest) list.get(Integer.valueOf(key));
                if (quest != null) {
                    stringBuilder.append(quest.get_questname() + ",");
                } else {
                    stringBuilder.append(" ,");
                }
            }
            String[] clientStrAry = stringBuilder.toString().split(",");
            if (allpage == 1) {
                _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "y_qp0", clientStrAry));
            } else if (page < 1) {
                _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "y_qp1", clientStrAry));
            } else if (page >= allpage - 1) {
                _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "y_qp3", clientStrAry));
            } else {
                _pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "y_qp2", clientStrAry));
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.model.L1ActionShowHtml JD-Core Version: 0.6.2
 */