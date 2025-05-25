package com.lineage.server.serverpackets;

public class S_SkillIconGFX extends ServerBasePacket {
    /**
     * 角色動作切換封包
     *
     * @param tempgfx      外型編號
     * @param changeWeapon 是否切換武器
     */
    public S_SkillIconGFX(int tempgfx, boolean changeWeapon) {
        // writeC(S_OPCODE_SKILLICONGFX);
        writeC(0xa0);
        if (changeWeapon == true) {
            writeC(1);
        } else {
            writeC(0);
        }
        writeH(0);
        writeC(2);
        writeH(tempgfx);
    }

    public byte[] getContent() {
        return getBytes();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.serverpackets.S_SkillIconGFX JD-Core Version: 0.6.2
 */