package com.lineage.data.npc.other;

import com.lineage.data.cmd.NpcWorkMove;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.SprTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.*;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.types.Point;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 安東<BR>
 * 70614
 *
 * @author dexc
 */
public class Npc_Anton extends NpcExecutor {
    private static final Log _log = LogFactory.getLog(Npc_Anton.class);
    private static Random _random = new Random();

    /**
     *
     */
    private Npc_Anton() {
        // TODO Auto-generated constructor stub
    }

    public static NpcExecutor get() {
        return new Npc_Anton();
    }

    @Override
    public int type() {
        return 19;
    }

    @Override
    public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
        // 請你不要妨礙我，我正在全力以赴的完成我一生中最偉大的作品。
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "anton3"));
    }

    @Override
    public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
        int mdefarmor = 0;
        int oldarmor = 0;
        int material = 0;
        int newarmor = 0;
        boolean success = false;
        if (cmd.equalsIgnoreCase("A")) {
            mdefarmor = 20110;
            oldarmor = 20095;
            material = 41246;
            newarmor = 401031;
            if ((pc.getInventory().checkEnchantItem(mdefarmor, 7, 1L) || pc.getInventory().checkEnchantItem(120110, 7, 1L)) && pc.getInventory().checkItem(oldarmor, 1L) && pc.getInventory().checkItem(material, 100000L)) {
                pc.getInventory().consumeEnchantItem(mdefarmor, 7, 1L);
                pc.getInventory().consumeItem(oldarmor, 1L);
                pc.getInventory().consumeItem(material, 100000L);
                final L1ItemInstance item = ItemTable.get().createItem(newarmor);
                item.setEnchantLevel(0);
                item.setIdentified(true);
                pc.getInventory().storeItem(item);
                String npcName = npc.getNpcTemplate().get_name();
                String itemName = item.getLogName();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                success = true;
                pc.sendPackets(new S_CloseList(pc.getId()));
            }
        } else if (cmd.equalsIgnoreCase("B")) {
            mdefarmor = 20110;
            oldarmor = 20095;
            material = 41246;
            newarmor = 401031;
            if ((pc.getInventory().checkEnchantItem(mdefarmor, 8, 1L) || pc.getInventory().checkEnchantItem(120110, 8, 1L)) && pc.getInventory().checkItem(oldarmor, 1L) && pc.getInventory().checkItem(material, 100000L)) {
                pc.getInventory().consumeEnchantItem(mdefarmor, 8, 1L);
                pc.getInventory().consumeItem(oldarmor, 1L);
                pc.getInventory().consumeItem(material, 100000L);
                final L1ItemInstance item = ItemTable.get().createItem(newarmor);
                item.setEnchantLevel(1);
                item.setIdentified(true);
                pc.getInventory().storeItem(item);
                String npcName = npc.getNpcTemplate().get_name();
                String itemName = item.getLogName();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                success = true;
                pc.sendPackets(new S_CloseList(pc.getId()));
            }
        } else if (cmd.equalsIgnoreCase("C")) {
            mdefarmor = 20110;
            oldarmor = 20095;
            material = 41246;
            newarmor = 401031;
            if ((pc.getInventory().checkEnchantItem(mdefarmor, 9, 1L) || pc.getInventory().checkEnchantItem(120110, 9, 1L)) && pc.getInventory().checkItem(oldarmor, 1L) && pc.getInventory().checkItem(material, 100000L)) {
                pc.getInventory().consumeEnchantItem(mdefarmor, 9, 1L);
                pc.getInventory().consumeItem(oldarmor, 1L);
                pc.getInventory().consumeItem(material, 100000L);
                final L1ItemInstance item = ItemTable.get().createItem(newarmor);
                item.setEnchantLevel(2);
                item.setIdentified(true);
                pc.getInventory().storeItem(item);
                String npcName = npc.getNpcTemplate().get_name();
                String itemName = item.getLogName();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                success = true;
                pc.sendPackets(new S_CloseList(pc.getId()));
            }
        } else if (cmd.equalsIgnoreCase("D")) {
            mdefarmor = 20110;
            oldarmor = 20095;
            material = 41246;
            newarmor = 401031;
            if ((pc.getInventory().checkEnchantItem(mdefarmor, 10, 1L) || pc.getInventory().checkEnchantItem(120110, 10, 1L)) && pc.getInventory().checkItem(oldarmor, 1L) && pc.getInventory().checkItem(material, 100000L)) {
                pc.getInventory().consumeEnchantItem(mdefarmor, 10, 1L);
                pc.getInventory().consumeItem(oldarmor, 1L);
                pc.getInventory().consumeItem(material, 100000L);
                final L1ItemInstance item = ItemTable.get().createItem(newarmor);
                item.setEnchantLevel(3);
                item.setIdentified(true);
                pc.getInventory().storeItem(item);
                String npcName = npc.getNpcTemplate().get_name();
                String itemName = item.getLogName();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                success = true;
                pc.sendPackets(new S_CloseList(pc.getId()));
            }
        } else if (cmd.equalsIgnoreCase("E")) {
            mdefarmor = 20110;
            oldarmor = 20094;
            material = 41246;
            newarmor = 401030;
            if ((pc.getInventory().checkEnchantItem(mdefarmor, 7, 1L) || pc.getInventory().checkEnchantItem(120110, 7, 1L)) && pc.getInventory().checkItem(oldarmor, 1L) && pc.getInventory().checkItem(material, 100000L)) {
                pc.getInventory().consumeEnchantItem(mdefarmor, 7, 1L);
                pc.getInventory().consumeItem(oldarmor, 1L);
                pc.getInventory().consumeItem(material, 100000L);
                final L1ItemInstance item = ItemTable.get().createItem(newarmor);
                item.setEnchantLevel(0);
                item.setIdentified(true);
                pc.getInventory().storeItem(item);
                String npcName = npc.getNpcTemplate().get_name();
                String itemName = item.getLogName();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                success = true;
                pc.sendPackets(new S_CloseList(pc.getId()));
            }
        } else if (cmd.equalsIgnoreCase("F")) {
            mdefarmor = 20110;
            oldarmor = 20094;
            material = 41246;
            newarmor = 401030;
            if ((pc.getInventory().checkEnchantItem(mdefarmor, 8, 1L) || pc.getInventory().checkEnchantItem(120110, 8, 1L)) && pc.getInventory().checkItem(oldarmor, 1L) && pc.getInventory().checkItem(material, 100000L)) {
                pc.getInventory().consumeEnchantItem(mdefarmor, 8, 1L);
                pc.getInventory().consumeItem(oldarmor, 1L);
                pc.getInventory().consumeItem(material, 100000L);
                final L1ItemInstance item = ItemTable.get().createItem(newarmor);
                item.setEnchantLevel(1);
                item.setIdentified(true);
                pc.getInventory().storeItem(item);
                String npcName = npc.getNpcTemplate().get_name();
                String itemName = item.getLogName();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                success = true;
                pc.sendPackets(new S_CloseList(pc.getId()));
            }
        } else if (cmd.equalsIgnoreCase("G")) {
            mdefarmor = 20110;
            oldarmor = 20094;
            material = 41246;
            newarmor = 401030;
            if ((pc.getInventory().checkEnchantItem(mdefarmor, 9, 1L) || pc.getInventory().checkEnchantItem(120110, 9, 1L)) && pc.getInventory().checkItem(oldarmor, 1L) && pc.getInventory().checkItem(material, 100000L)) {
                pc.getInventory().consumeEnchantItem(mdefarmor, 9, 1L);
                pc.getInventory().consumeItem(oldarmor, 1L);
                pc.getInventory().consumeItem(material, 100000L);
                final L1ItemInstance item = ItemTable.get().createItem(newarmor);
                item.setEnchantLevel(2);
                item.setIdentified(true);
                pc.getInventory().storeItem(item);
                String npcName = npc.getNpcTemplate().get_name();
                String itemName = item.getLogName();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                success = true;
                pc.sendPackets(new S_CloseList(pc.getId()));
            }
        } else if (cmd.equalsIgnoreCase("H")) {
            mdefarmor = 20110;
            oldarmor = 20094;
            material = 41246;
            newarmor = 401030;
            if ((pc.getInventory().checkEnchantItem(mdefarmor, 10, 1L) || pc.getInventory().checkEnchantItem(120110, 10, 1L)) && pc.getInventory().checkItem(oldarmor, 1L) && pc.getInventory().checkItem(material, 100000L)) {
                pc.getInventory().consumeEnchantItem(mdefarmor, 10, 1L);
                pc.getInventory().consumeItem(oldarmor, 1L);
                pc.getInventory().consumeItem(material, 100000L);
                final L1ItemInstance item = ItemTable.get().createItem(newarmor);
                item.setEnchantLevel(3);
                item.setIdentified(true);
                pc.getInventory().storeItem(item);
                String npcName = npc.getNpcTemplate().get_name();
                String itemName = item.getLogName();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                success = true;
                pc.sendPackets(new S_CloseList(pc.getId()));
            }
        } else if (cmd.equalsIgnoreCase("I")) {
            mdefarmor = 20110;
            oldarmor = 20092;
            material = 41246;
            newarmor = 401028;
            if ((pc.getInventory().checkEnchantItem(mdefarmor, 7, 1L) || pc.getInventory().checkEnchantItem(120110, 7, 1L)) && pc.getInventory().checkItem(oldarmor, 1L) && pc.getInventory().checkItem(material, 100000L)) {
                pc.getInventory().consumeEnchantItem(mdefarmor, 7, 1L);
                pc.getInventory().consumeItem(oldarmor, 1L);
                pc.getInventory().consumeItem(material, 100000L);
                final L1ItemInstance item = ItemTable.get().createItem(newarmor);
                item.setEnchantLevel(0);
                item.setIdentified(true);
                pc.getInventory().storeItem(item);
                String npcName = npc.getNpcTemplate().get_name();
                String itemName = item.getLogName();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                success = true;
                pc.sendPackets(new S_CloseList(pc.getId()));
            }
        } else if (cmd.equalsIgnoreCase("J")) {
            mdefarmor = 20110;
            oldarmor = 20092;
            material = 41246;
            newarmor = 401028;
            if ((pc.getInventory().checkEnchantItem(mdefarmor, 8, 1L) || pc.getInventory().checkEnchantItem(120110, 8, 1L)) && pc.getInventory().checkItem(oldarmor, 1L) && pc.getInventory().checkItem(material, 100000L)) {
                pc.getInventory().consumeEnchantItem(mdefarmor, 8, 1L);
                pc.getInventory().consumeItem(oldarmor, 1L);
                pc.getInventory().consumeItem(material, 100000L);
                final L1ItemInstance item = ItemTable.get().createItem(newarmor);
                item.setEnchantLevel(1);
                item.setIdentified(true);
                pc.getInventory().storeItem(item);
                String npcName = npc.getNpcTemplate().get_name();
                String itemName = item.getLogName();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                success = true;
                pc.sendPackets(new S_CloseList(pc.getId()));
            }
        } else if (cmd.equalsIgnoreCase("K")) {
            mdefarmor = 20110;
            oldarmor = 20092;
            material = 41246;
            newarmor = 401028;
            if ((pc.getInventory().checkEnchantItem(mdefarmor, 9, 1L) || pc.getInventory().checkEnchantItem(120110, 9, 1L)) && pc.getInventory().checkItem(oldarmor, 1L) && pc.getInventory().checkItem(material, 100000L)) {
                pc.getInventory().consumeEnchantItem(mdefarmor, 9, 1L);
                pc.getInventory().consumeItem(oldarmor, 1L);
                pc.getInventory().consumeItem(material, 100000L);
                final L1ItemInstance item = ItemTable.get().createItem(newarmor);
                item.setEnchantLevel(2);
                item.setIdentified(true);
                pc.getInventory().storeItem(item);
                String npcName = npc.getNpcTemplate().get_name();
                String itemName = item.getLogName();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                success = true;
                pc.sendPackets(new S_CloseList(pc.getId()));
            }
        } else if (cmd.equalsIgnoreCase("L")) {
            mdefarmor = 20110;
            oldarmor = 20092;
            material = 41246;
            newarmor = 401028;
            if ((pc.getInventory().checkEnchantItem(mdefarmor, 10, 1L) || pc.getInventory().checkEnchantItem(120110, 10, 1L)) && pc.getInventory().checkItem(oldarmor, 1L) && pc.getInventory().checkItem(material, 100000L)) {
                pc.getInventory().consumeEnchantItem(mdefarmor, 10, 1L);
                pc.getInventory().consumeItem(oldarmor, 1L);
                pc.getInventory().consumeItem(material, 100000L);
                final L1ItemInstance item = ItemTable.get().createItem(newarmor);
                item.setEnchantLevel(3);
                item.setIdentified(true);
                pc.getInventory().storeItem(item);
                String npcName = npc.getNpcTemplate().get_name();
                String itemName = item.getLogName();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                success = true;
                pc.sendPackets(new S_CloseList(pc.getId()));
            }
        } else if (cmd.equalsIgnoreCase("M")) {
            mdefarmor = 20110;
            oldarmor = 20093;
            material = 41246;
            newarmor = 401029;
            if ((pc.getInventory().checkEnchantItem(mdefarmor, 7, 1L) || pc.getInventory().checkEnchantItem(120110, 7, 1L)) && pc.getInventory().checkItem(oldarmor, 1L) && pc.getInventory().checkItem(material, 100000L)) {
                pc.getInventory().consumeEnchantItem(mdefarmor, 7, 1L);
                pc.getInventory().consumeItem(oldarmor, 1L);
                pc.getInventory().consumeItem(material, 100000L);
                final L1ItemInstance item = ItemTable.get().createItem(newarmor);
                item.setEnchantLevel(0);
                item.setIdentified(true);
                pc.getInventory().storeItem(item);
                String npcName = npc.getNpcTemplate().get_name();
                String itemName = item.getLogName();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                success = true;
                pc.sendPackets(new S_CloseList(pc.getId()));
            }
        } else if (cmd.equalsIgnoreCase("N")) {
            mdefarmor = 20110;
            oldarmor = 20093;
            material = 41246;
            newarmor = 401029;
            if ((pc.getInventory().checkEnchantItem(mdefarmor, 8, 1L) || pc.getInventory().checkEnchantItem(120110, 8, 1L)) && pc.getInventory().checkItem(oldarmor, 1L) && pc.getInventory().checkItem(material, 100000L)) {
                pc.getInventory().consumeEnchantItem(mdefarmor, 8, 1L);
                pc.getInventory().consumeItem(oldarmor, 1L);
                pc.getInventory().consumeItem(material, 100000L);
                final L1ItemInstance item = ItemTable.get().createItem(newarmor);
                item.setEnchantLevel(1);
                item.setIdentified(true);
                pc.getInventory().storeItem(item);
                String npcName = npc.getNpcTemplate().get_name();
                String itemName = item.getLogName();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                success = true;
                pc.sendPackets(new S_CloseList(pc.getId()));
            }
        } else if (cmd.equalsIgnoreCase("O")) {
            mdefarmor = 20110;
            oldarmor = 20093;
            material = 41246;
            newarmor = 401029;
            if ((pc.getInventory().checkEnchantItem(mdefarmor, 9, 1L) || pc.getInventory().checkEnchantItem(120110, 9, 1L)) && pc.getInventory().checkItem(oldarmor, 1L) && pc.getInventory().checkItem(material, 100000L)) {
                pc.getInventory().consumeEnchantItem(mdefarmor, 9, 1L);
                pc.getInventory().consumeItem(oldarmor, 1L);
                pc.getInventory().consumeItem(material, 100000L);
                final L1ItemInstance item = ItemTable.get().createItem(newarmor);
                item.setEnchantLevel(2);
                item.setIdentified(true);
                pc.getInventory().storeItem(item);
                String npcName = npc.getNpcTemplate().get_name();
                String itemName = item.getLogName();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                success = true;
                pc.sendPackets(new S_CloseList(pc.getId()));
            }
        } else if (cmd.equalsIgnoreCase("P")) {
            mdefarmor = 20110;
            oldarmor = 20093;
            material = 41246;
            newarmor = 401029;
            if ((pc.getInventory().checkEnchantItem(mdefarmor, 10, 1L) || pc.getInventory().checkEnchantItem(120110, 10, 1L)) && pc.getInventory().checkItem(oldarmor, 1L) && pc.getInventory().checkItem(material, 100000L)) {
                pc.getInventory().consumeEnchantItem(mdefarmor, 10, 1L);
                pc.getInventory().consumeItem(oldarmor, 1L);
                pc.getInventory().consumeItem(material, 100000L);
                final L1ItemInstance item = ItemTable.get().createItem(newarmor);
                item.setEnchantLevel(3);
                item.setIdentified(true);
                pc.getInventory().storeItem(item);
                String npcName = npc.getNpcTemplate().get_name();
                String itemName = item.getLogName();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                success = true;
                pc.sendPackets(new S_CloseList(pc.getId()));
            }
        }
        if (!success) {
            pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "anton9"));
        }
    }

    @Override
    public int workTime() {
        return 20;
    }

    @Override
    public void work(final L1NpcInstance npc) {
        final Work work = new Work(npc);
        work.getStart();
    }

    private class Work implements Runnable {
        private L1NpcInstance _npc;
        private int _spr;
        private NpcWorkMove _npcMove;
        private Point[] _point = new Point[]{new Point(33451, 32741), new Point(33449, 32743), new Point(33448, 32742)// 雕刻
        };

        private Work(final L1NpcInstance npc) {
            this._npc = npc;
            this._spr = SprTable.get().getMoveSpeed(npc.getTempCharGfx(), 0);
            this._npcMove = new NpcWorkMove(npc);
        }

        /**
         * 啟動線程
         */
        public void getStart() {
            GeneralThreadPool.get().schedule(this, 10);
        }

        @Override
        public void run() {
            try {
                Point point = null;
                final int t = _random.nextInt(this._point.length);
                if (!this._npc.getLocation().isSamePoint(this._point[t])) {
                    point = this._point[t];
                }
                boolean isWork = true;
                while (isWork) {
                    TimeUnit.MILLISECONDS.sleep(this._spr);
                    if (point != null) {
                        isWork = this._npcMove.actionStart(point);
                    } else {
                        isWork = false;
                    }
                    if (this._npc.getLocation().isSamePoint(this._point[2])) {
                        this._npc.setHeading(6);
                        this._npc.broadcastPacketX8(new S_ChangeHeading(this._npc));
                        TimeUnit.MILLISECONDS.sleep(this._spr);
                        this._npc.broadcastPacketX8(new S_DoActionGFX(this._npc.getId(), 17));
                    }
                }
            } catch (final Exception e) {
                _log.error(e.getLocalizedMessage(), e);
            }
        }
    }
}
