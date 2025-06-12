package com.lineage.server.clientpackets;

import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigRecord;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.SpamTable;
import com.lineage.server.datatables.lock.LogChatReading;
import com.lineage.server.model.Instance.L1DeInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1ExcludingList;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.S_ChatWhisperFrom;
import com.lineage.server.serverpackets.S_ChatWhisperTo;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldNpc;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;

public class C_ChatWhisper extends ClientBasePacket {
    private static final Log _log = LogFactory.getLog(C_ChatWhisper.class);

    public static L1DeInstance getDe(String s) {
        Collection<?> allNpc = WorldNpc.get().all();
        if (allNpc.isEmpty()) {
            return null;
        }
        for (Object o : allNpc) {
            L1NpcInstance npc = (L1NpcInstance) o;
            if ((npc instanceof L1DeInstance)) {
                L1DeInstance de = (L1DeInstance) npc;
                if (de.getNameId().equalsIgnoreCase(s)) {
                    return de;
                }
            }
        }
        return null;
    }

    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            read(decrypt);
            L1PcInstance whisperFrom = client.getActiveChar();
            if (whisperFrom.hasSkillEffect(4002)) {
                whisperFrom.sendPackets(new S_ServerMessage(242));
                return;
            }
            // 等級 %0 以下無法使用密談。
            if ((whisperFrom.getLevel() < ConfigAlt.WHISPER_CHAT_LEVEL) && !whisperFrom.isGm()) {
                whisperFrom.sendPackets(new S_ServerMessage(404, String.valueOf(ConfigAlt.WHISPER_CHAT_LEVEL)));
                return;
            }
            String targetName = readS();
            String text = readS();
            if (text.length() > 52) {
                _log.warn("人物:" + whisperFrom.getName() + "對話長度超過限制:" + client.getIp().toString());
                client.set_error(client.get_error() + 1);
                return;
            }
            L1PcInstance whisperTo = World.get().getPlayer(targetName);
            L1DeInstance de = null;
            /*
             * S_GmMessage gm = new S_GmMessage(whisperFrom, whisperTo, 1,
             * text);
             *
             * for (L1PcInstance pca : World.get().getAllPlayers()) { if
             * ((pca.isGm()) && (pca != whisperFrom) && (pca != whisperTo)) {
             * pca.sendPackets(gm); } }
             */
            if (whisperTo == null) {
                de = getDe(targetName);
            }
            if (de != null) {
                whisperFrom.sendPackets(new S_ChatWhisperTo(de, text));
                return;
            }
            if (whisperTo == null) {
                whisperFrom.sendPackets(new S_ServerMessage(73, targetName));
                return;
            }
            if (whisperTo.equals(whisperFrom)) {
                return;
            }
			/*if (whisperTo.getExcludingList().contains(whisperFrom.getName())) {
				whisperFrom.sendPackets(new S_ServerMessage(117, whisperTo.getName()));
				return;
			}*/
            // 黑名單
            if (whisperTo != null) {
                L1ExcludingList spamList1 = SpamTable.getInstance().getExcludeTable(whisperTo.getId());
                if (spamList1.contains(0, whisperFrom.getName())) {
                    whisperFrom.sendPackets(new S_ServerMessage(117, whisperTo.getName()));
                    return;
                }
            }
            if (!whisperTo.isCanWhisper()) {
                whisperFrom.sendPackets(new S_ServerMessage(205, whisperTo.getName()));
                return;
            }
            if (ConfigRecord.GM_OVERHEARD) {
                for (L1Object visible : World.get().getAllPlayers()) {
                    if ((visible instanceof L1PcInstance)) {
                        L1PcInstance GM = (L1PcInstance) visible;
                        if ((GM.isGm()) && (whisperFrom.getId() != GM.getId())) {
                            GM.sendPackets(new S_SystemMessage("\\fV【密】" + whisperFrom.getName() + " -> " + targetName + ":" + text));
                        }
                    }
                }
            }
            whisperFrom.sendPackets(new S_ChatWhisperTo(whisperTo, text));
            whisperTo.sendPackets(new S_ChatWhisperFrom(whisperFrom, text));
            if (ConfigRecord.LOGGING_CHAT_WHISPER) {
                LogChatReading.get().isTarget(whisperFrom, whisperTo, text, 9);
            }
        } catch (Exception localException) {
        } finally {
            over();
        }
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
