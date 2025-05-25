package com.lineage.server.serverpackets;

import com.lineage.server.datatables.T_CraftConfigTable;
import com.lineage.server.datatables.T_CraftConfigTable.NewL1NpcMakeItemAction;
import com.lineage.server.model.Instance.L1ItemInstance;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.List;

public class S_ItemCraftList extends ServerBasePacket {
    private static final Log _log = LogFactory.getLog(S_ItemCraftList.class);

    public S_ItemCraftList(final Collection<T_CraftConfigTable.NewL1NpcMakeItemAction> npcMakeItemActions) {
        try {
            writeC(S_EXTENDED_PROTOBUF);
            writeC(57);
            writeC(0);
            writeC(8);
            writeC(0);
            if (npcMakeItemActions != null) {
                for (final NewL1NpcMakeItemAction npcMakeItemAction : npcMakeItemActions) {
                    a(18, new S_CraftContent().jdMethod_if(npcMakeItemAction.getAmountActionID(), 0, 0));
                }
            }
            writeC(24);
            writeC(0);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public S_ItemCraftList(final int type) {
        writeC(S_EXTENDED_PROTOBUF);
        writeC(55);
        writeC(0);
        writeC(8);
        writeC(3);
        writeC(188);
        writeC(17);
    }

    public S_ItemCraftList(final NewL1NpcMakeItemAction npcMakeItemAction, final boolean first) {
        try {
            if (npcMakeItemAction != null) {
                writeC(S_EXTENDED_PROTOBUF);
                writeC(55);
                writeC(0);
                writeC(8);
                if (first) {
                    writeC(0);
                } else {
                    writeC(1);
                }
                writeByte(npcMakeItemAction.getPacket().getContent());
                writeC(1);
                writeC(5);
            } else {
                writeC(S_EXTENDED_PROTOBUF);
                writeC(55);
                writeC(0);
                writeC(8);
                writeC(2);
                writeC(3);
                writeC(116);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public S_ItemCraftList(final boolean isSuccess, final List<L1ItemInstance> giveItemObjs) {
        try {
            writeC(S_EXTENDED_PROTOBUF);
            writeC(59);
            a(8, isSuccess ? 0L : 1L);
            if (giveItemObjs != null) {
                for (final L1ItemInstance itemObj : giveItemObjs) {
                    a(18, new S_CraftContent().jdMethod_if(itemObj));
                }
            }
            writeC(3);
            writeC(116);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public byte[] getContent() {
        return _bao.toByteArray();
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}