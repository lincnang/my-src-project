package com.lineage.data.item_etcitem.power;

import com.lineage.config.ConfigAlt;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_OwnCharStatus2;
import com.lineage.server.serverpackets.S_ServerMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PanaceaCon extends ItemExecutor {
    private static final Log _log = LogFactory.getLog(PanaceaCon.class);

    public static ItemExecutor get() {
        return new PanaceaCon();
    }

    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        int itemId = item.getItemId();
        boolean save = false;
        boolean me = false;
        boolean po = false;
        switch (itemId) {
            case 40033:
                if (pc.getBaseStr() < ConfigAlt.POWERMEDICINE) {
                    if (pc.getElixirStats() < ConfigAlt.MEDICINE) {
                        pc.addBaseStr(1);
                        pc.setElixirStats(pc.getElixirStats() + 1);
                        pc.getInventory().removeItem(item, 1L);
                        pc.sendPackets(new S_OwnCharStatus2(pc));
                        pc.sendDetails();
                        save = true;
                    } else {
                        me = true;
                    }
                } else {
                    po = true;
                }
                break;
            case 40034:
                if (pc.getBaseCon() < ConfigAlt.POWERMEDICINE) {
                    if (pc.getElixirStats() < ConfigAlt.MEDICINE) {
                        pc.addBaseCon(1);
                        pc.setElixirStats(pc.getElixirStats() + 1);
                        pc.getInventory().removeItem(item, 1L);
                        pc.sendPackets(new S_OwnCharStatus2(pc));
                        pc.sendDetails();
                        save = true;
                    } else {
                        me = true;
                    }
                } else {
                    po = true;
                }
                break;
            case 40035:
                if (pc.getBaseDex() < ConfigAlt.POWERMEDICINE) {
                    if (pc.getElixirStats() < ConfigAlt.MEDICINE) {
                        pc.addBaseDex(1);
                        pc.resetBaseAc();
                        pc.setElixirStats(pc.getElixirStats() + 1);
                        pc.getInventory().removeItem(item, 1L);
                        pc.sendPackets(new S_OwnCharStatus2(pc));
                        pc.sendDetails();
                        save = true;
                    } else {
                        me = true;
                    }
                } else {
                    po = true;
                }
                break;
            case 40036:
                if (pc.getBaseInt() < ConfigAlt.POWERMEDICINE) {
                    if (pc.getElixirStats() < ConfigAlt.MEDICINE) {
                        pc.addBaseInt(1);
                        pc.setElixirStats(pc.getElixirStats() + 1);
                        pc.getInventory().removeItem(item, 1L);
                        pc.sendPackets(new S_OwnCharStatus2(pc));
                        pc.sendDetails();
                        save = true;
                    } else {
                        me = true;
                    }
                } else {
                    po = true;
                }
                break;
            case 40037:
                if (pc.getBaseWis() < ConfigAlt.POWERMEDICINE) {
                    if (pc.getElixirStats() < ConfigAlt.MEDICINE) {
                        pc.addBaseWis(1);
                        pc.resetBaseMr();
                        pc.setElixirStats(pc.getElixirStats() + 1);
                        pc.getInventory().removeItem(item, 1L);
                        pc.sendPackets(new S_OwnCharStatus2(pc));
                        pc.sendDetails();
                        save = true;
                    } else {
                        me = true;
                    }
                } else {
                    po = true;
                }
                break;
            case 40038:
                if (pc.getBaseCha() < ConfigAlt.POWERMEDICINE) {
                    if (pc.getElixirStats() < ConfigAlt.MEDICINE) {
                        pc.addBaseCha(1);
                        pc.setElixirStats(pc.getElixirStats() + 1);
                        pc.getInventory().removeItem(item, 1L);
                        pc.sendPackets(new S_OwnCharStatus2(pc));
                        save = true;
                    } else {
                        me = true;
                    }
                } else {
                    po = true;
                }
                break;
        }
        if (po) {
            pc.sendPackets(new S_ServerMessage(481));
        }
        if (me) {
            pc.sendPackets(new S_ServerMessage(79));
        }
        if (save) {
            try {
                pc.save();
            } catch (Exception e) {
                _log.error(e.getLocalizedMessage(), e);
            }
        }
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.data.item_etcitem.power.PanaceaCon JD-Core Version: 0.6.2
 */