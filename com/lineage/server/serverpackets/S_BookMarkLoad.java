package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 角色座標名單
 *
 * @author dexc
 */
public class S_BookMarkLoad extends ServerBasePacket {
    private static final String S_BookMarkLoad = "[S] S_BookmarkLoad";
    private static Logger _log = Logger.getLogger(S_BookMarkLoad.class.getName());
    private byte[] _byte = null;

    public S_BookMarkLoad(L1PcInstance pc) {
        try {
            int size = pc._bookmarks.size();
            int fastsize = pc._speedbookmarks.size();
            int booksize = pc.getMark_count() + 6;
            int tempsize = booksize - 1 - size - fastsize;
            writeC(S_VOICE_CHAT);
            writeC(42);
            writeC(booksize);
            writeC(0x00);
            writeC(0x02);
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    writeC(i);
                }
            }
            if (fastsize > 0) {
                for (int i = 0; i < fastsize; i++) {
                    writeC(pc._speedbookmarks.get(i).getNumId());
                }
            }
            if (tempsize > 0) {
                for (int i = 0; i < tempsize; i++) {
                    writeC(0xff);
                }
            }
            writeH(pc.getMark_count());
            writeH(size);
            for (int i = 0; i < size; i++) {
                writeD(pc._bookmarks.get(i).getNumId());
                writeS(pc._bookmarks.get(i).getName());
                writeH(pc._bookmarks.get(i).getMapId());
                writeH(pc._bookmarks.get(i).getLocX());
                writeH(pc._bookmarks.get(i).getLocY());
            }
        } catch (Exception e) {
            _log.log(Level.WARNING, "S_BookMarkLoad發生例外。", e);
        } finally {
        }
    }

    @Override
    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    @Override
    public String getType() {
        return S_BookMarkLoad;
    }
}
