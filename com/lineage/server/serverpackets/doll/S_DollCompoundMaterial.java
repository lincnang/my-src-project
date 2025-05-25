package com.lineage.server.serverpackets.doll;

import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.templates.L1Item;

import java.util.ArrayList;

/**
 * @author kyo
 */
public class S_DollCompoundMaterial extends ServerBasePacket {
    private byte[] _byte = null;

    public S_DollCompoundMaterial() {
    }

    public S_DollCompoundMaterial(int i, int level, final ArrayList<L1Item> levelList) {
        this.writeByteArray(2, new S_DollCompoundMaterial(level, levelList).getContent());
    }

    public S_DollCompoundMaterial(int level, final ArrayList<L1Item> levelList) {
        this.writeInt32(1, level);
        for (final L1Item tmp : levelList) {
            this.writeByteArray(2, build(tmp));
        }
    }

    private byte[] build(final L1Item tmp) {
        S_DollCompoundMaterial m = new S_DollCompoundMaterial();
        m.writeInt32(1, tmp.getItemDescId());
        return m.getContent();
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = _bao.toByteArray();
        }
        return _byte;
    }

    @Override
    public String getType() {
        return "[S] " + this.getClass().getSimpleName();
    }
}
