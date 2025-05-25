package com.lineage.data.item_etcitem.wand;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.command.GmHtml;
import com.lineage.server.model.Instance.L1DeInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.thread.DeAiThreadPool;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GM_Blink extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(GM_Blink.class);
    private static final Map<Integer, L1DeInstance> _list = new HashMap<Integer, L1DeInstance>();

    public static ItemExecutor get() {
        return new GM_Blink();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        try {
            int targObjId = data[0];
            L1Object target = World.get().findObject(targObjId);
            if (target == null) {
                L1DeInstance de = pc.get_outChat();
                if (de != null) {
                    int spellsc_x = data[1];
                    int spellsc_y = data[2];
                    if (de.isShop()) {
                        pc.sendPackets(new S_ServerMessage(166, de.getNameId() + "正在商店模式"));
                        return;
                    }
                    if (de.isFishing()) {
                        pc.sendPackets(new S_ServerMessage(166, de.getNameId() + "正在釣魚模式"));
                        return;
                    }
                    if (_list.get(Integer.valueOf(de.getId())) != null) {
                        pc.sendPackets(new S_ServerMessage("該NPC還未完成上一次移動命令"));
                        return;
                    }
                    if ((de.getX() != spellsc_x) && (de.getY() != spellsc_y)) {
                        MoveTimer moveTimer = new MoveTimer(de, spellsc_x, spellsc_y);
                        moveTimer.start();
                    }
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
                return;
            }
            if ((target instanceof L1PcInstance)) {
                L1PcInstance tgpc = (L1PcInstance) target;
                if (tgpc.equals(pc)) {
                    pc.set_outChat(null);
                    pc.sendPackets(new S_ServerMessage("\\fY解除控制"));
                    pc.get_other().set_gmHtml(null);
                    return;
                }
            }
            if ((target instanceof L1DeInstance)) {
                L1DeInstance de = (L1DeInstance) target;
                pc.set_outChat(de);
                pc.sendPackets(new S_ServerMessage("\\fY控制對像:" + de.getNameId()));
                GmHtml gmHtml = new GmHtml(pc, 1);
                gmHtml.show();
                return;
            }
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    private static class MoveTimer implements Runnable {
        private final L1DeInstance _de;
        private final int _spellsc_x;
        private final int _spellsc_y;

        private MoveTimer(L1DeInstance de, int spellsc_x, int spellsc_y) {
            GM_Blink._list.put(Integer.valueOf(de.getId()), de);
            _de = de;
            _spellsc_x = spellsc_x;
            _spellsc_y = spellsc_y;
        }

        private void start() {
            DeAiThreadPool.get().execute(this);
        }

        public void run() {
            try {
                int i = 10;
                do {
                    if (_de == null) {
                        GM_Blink._list.remove(Integer.valueOf(_de.getId()));
                        break;
                    }
                    int moveDirection = _de.getMove().moveDirection(_spellsc_x, _spellsc_y);
                    int dir = _de.getMove().checkObject(moveDirection);
                    if (dir != -1) {
                        _de.getMove().setDirectionMove(dir);
                        _de.setNpcSpeed();
                    }
                    TimeUnit.MILLISECONDS.sleep(_de.calcSleepTime(_de.getPassispeed(), 0));
                    i--;
                    if (i <= 0) {
                        GM_Blink._list.remove(Integer.valueOf(_de.getId()));
                        break;
                    }
                    if (_de.getX() == _spellsc_x) {
                        break;
                    }
                } while (_de.getY() != _spellsc_y);
            } catch (Exception e) {
                GM_Blink._log.error(e.getLocalizedMessage(), e);
            } finally {
                GM_Blink._list.remove(Integer.valueOf(_de.getId()));
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.wand.GM_Blink JD-Core Version: 0.6.2
 */