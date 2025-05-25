package com.lineage.server.serverpackets;

/**
 * 更新血盟數據
 *
 * @author KZK
 */
public class S_ClanUpdate extends ServerBasePacket {
    private byte[] _byte = null;

    /**
     * 更新血盟數據(加入 創立)
     *
     */
    public S_ClanUpdate(final int objid, final String Clanname, final int rank) {
        writeC(S_PLEDGE); // XXX S_OPCODE_CLANUPDATE 修改為 S_OPCODE_UPDATECLANID
        writeD(objid);// 角色物件
        writeS(Clanname);// 血盟名稱
        writeS(null);// 角色封號 官方預設為NULL writeS內容 新成員入盟會直接給予封號
        writeD(0x00000000);// 用途不明
        writeC(rank);// 角色階級
    }

    /**
     * 更新血盟數據(驅逐退出解散血盟)
     *
     */
    public S_ClanUpdate(final int objid) {
        writeC(S_PLEDGE); // XXX S_OPCODE_CLANUPDATE 修改為 S_OPCODE_UPDATECLANID
        writeD(objid);// 角色物件
        writeS(null);// 血盟名稱
        writeS(null);// 角色封號 官方預設為NULL
        writeD(0x00000000);// 用途不明
        writeC(0);// 角色階級
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
        return this.getClass().getSimpleName();
    }
}
