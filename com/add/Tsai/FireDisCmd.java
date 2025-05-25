package com.add.Tsai;

import com.lineage.data.npc.Npc_FireDis;
import com.lineage.server.datatables.T_CraftConfigTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.S_ItemCraftList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldNpc;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;

/**
 * 火神指令
 *
 * @author hero
 */
public class FireDisCmd {
    private static final Log _log = LogFactory.getLog(FireDisCmd.class);
    private static FireDisCmd _instance;

    public static FireDisCmd get() {
        if (_instance == null) {
            _instance = new FireDisCmd();
        }
        return _instance;
    }

    public boolean Cmd(final L1PcInstance pc, final String cmd) {
        try {
            final StringBuilder stringBuilder = new StringBuilder();
            boolean ok = false;
            switch (cmd) {
                case "ticf1":
                    ok = true;
                    pc.sendPackets(new S_NPCTalkReturn(pc, "ticf1"));
                    break;
                case "ticf2":
                    ok = true;
                    pc.sendPackets(new S_NPCTalkReturn(pc, "ticf2"));
                    break;
                case "ticf3":
                    ok = true;
                    pc.sendPackets(new S_NPCTalkReturn(pc, "ticf3"));
                    break;
                case "ticf4":
                    ok = true;
                    pc.sendPackets(new S_NPCTalkReturn(pc, "ticf4"));
                    break;
                case "icfw_1":
                    ok = true;
                    pc.setFireDisId(200234);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                case "icfw_2":
                    ok = true;
                    pc.setFireDisId(200235);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                case "icfw_3":
                    ok = true;
                    pc.setFireDisId(200236);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                case "icfw_4":
                    ok = true;
                    pc.setFireDisId(200237);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                case "icfw_5":
                    ok = true;
                    pc.setFireDisId(200238);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                case "icfw_6":
                    ok = true;
                    pc.setFireDisId(200239);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                case "icfw_7":
                    ok = true;
                    pc.setFireDisId(200240);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                case "icfw_8":
                    ok = true;
                    pc.setFireDisId(200241);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                case "icfw_9":
                    ok = true;
                    pc.setFireDisId(200242);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                case "icfw_10":
                    ok = true;
                    pc.setFireDisId(200243);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                case "icfw_11":
                    ok = true;
                    pc.setFireDisId(200244);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                case "icfw_12":
                    ok = true;
                    pc.setFireDisId(200245);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                case "icfw_13":
                    ok = true;
                    pc.setFireDisId(200246);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                case "icfw_14":
                    ok = true;
                    pc.setFireDisId(200247);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                case "icfw_15":
                    ok = true;
                    pc.setFireDisId(200248);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                case "icfw_16":
                    ok = true;
                    pc.setFireDisId(200249);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                case "icfw_17":
                    ok = true;
                    pc.setFireDisId(200250);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                case "icfw_18":
                    ok = true;
                    pc.setFireDisId(200251);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                case "icfw_19":
                    ok = true;
                    pc.setFireDisId(200252);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                case "icfw_20":
                    ok = true;
                    pc.setFireDisId(200253);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                case "icfw_21":
                    ok = true;
                    pc.setFireDisId(200254);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                case "icfw_22":
                    ok = true;
                    pc.setFireDisId(200255);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                case "icfw_23":
                    ok = true;
                    pc.setFireDisId(200256);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                case "icfw_24":
                    ok = true;
                    pc.setFireDisId(200257);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                case "icfw_25":
                    ok = true;
                    pc.setFireDisId(200258);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                case "icfw_26":
                    ok = true;
                    pc.setFireDisId(200259);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                case "icfw_27":
                    ok = true;
                    pc.setFireDisId(200260);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                case "icfw_28":
                    ok = true;
                    pc.setFireDisId(200261);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                case "icfw_29":
                    ok = true;
                    pc.setFireDisId(200262);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                case "icfw_30":
                    ok = true;
                    pc.setFireDisId(200263);
                    Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                    break;
                //				case "ictw_99":
                //					ok = true;
                //					Npc_FireDis.get().talk(pc, WorldNpc.get().find(pc.getFireDisId()));
                //					break;
                default:
                    break;
            }
            if (ok) {
                return true;
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    public void SendItemCraft(final L1PcInstance pc, final int npcObjId) {
        L1Object obj = World.get().findObject(npcObjId);
        if ((obj != null) && ((obj instanceof L1NpcInstance))) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            HashMap<Integer, T_CraftConfigTable.NewL1NpcMakeItemAction> npcMakeItemActionMap = T_CraftConfigTable.get().getNpcMakeItemActionList(npc.getNpcId());
            if (npcMakeItemActionMap != null) {
                pc.sendPackets(new S_ItemCraftList(npcMakeItemActionMap.values()));
            } else {
                pc.sendPackets(new S_ItemCraftList(null));
            }
        } else {
            pc.sendPackets(new S_ItemCraftList(null));
        }
    }
}