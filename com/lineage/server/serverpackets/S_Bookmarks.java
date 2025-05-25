package com.lineage.server.serverpackets;

/**
 * 角色座標名單
 *
 * @author dexc
 */
public class S_Bookmarks extends ServerBasePacket {
    private static final String _S__1F_S_Bookmarks = "[S] S_Bookmarks";
    private byte[] _byte = null;

    /**
     * 角色座標名單
     *
     */
    public S_Bookmarks(String name, int map, int x, int y, int bookid) {
        buildPacket(name, map, x, y, bookid);
    }

    private void buildPacket(String name, int map, int x, int y, int bookid) {
        this.writeC(S_ADD_BOOKMARK);
        writeS(name);
        writeH(map);
        writeH(x);
        writeH(y);
        writeD(bookid);
        writeH(0);
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
        return _S__1F_S_Bookmarks;
    }
}
