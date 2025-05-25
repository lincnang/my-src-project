package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

import java.util.Random;

import static com.lineage.server.model.skill.L1SkillId.STATUS_BRAVE3;

/**
 * 物件封包 - 本身
 *
 * @author dexc
 */
public class S_OwnCharPack extends ServerBasePacket {
    public static final Random _random = new Random();
    // private static final int STATUS_POISON = 1;
    private static final int STATUS_INVISIBLE = 2;
    private static final int STATUS_PC = 4;
    // private static final int STATUS_FREEZE = 8;
    private static final int STATUS_BRAVE = 16;
    private static final int STATUS_ELFBRAVE = 32;
    private static final int STATUS_FASTMOVABLE = 64;
    private static final int STATUS_GHOST = 128;
    private byte[] _byte = null;

    /**
     * 物件封包 - 本身
     *
     * @param pc
     */
    public S_OwnCharPack(L1PcInstance pc) {
        buildPacket(pc);
    }

    private void buildPacket(L1PcInstance pc) {
        int status = STATUS_PC;
        if (pc.isInvisble() || pc.isGmInvis()) {
            status |= STATUS_INVISIBLE;
        }
        //if (pc.isBrave()) {
        //status |= STATUS_BRAVE;
        //}
        if (pc.isSuperBrave()) { // 荒神加速
            status |= pc.getBraveSpeed() << 4;
        } else if (pc.isBrave()) {
            status |= STATUS_BRAVE;
        }
        if (pc.isElfBrave()) {
            // エルヴンワッフルの場合は、STATUS_BRAVEとSTATUS_ELFBRAVEを立てる。
            // STATUS_ELFBRAVEのみでは效果が無い？
            status |= STATUS_BRAVE;
            status |= STATUS_ELFBRAVE;
        }
        if (pc.isFastMovable()) {
            status |= STATUS_FASTMOVABLE;
        }
        if (pc.isGhost()) {
            status |= STATUS_GHOST;
        }
        writeC(S_PUT_OBJECT);
        writeH(pc.getX());
        writeH(pc.getY());
        writeD(pc.getId());
        if (pc.isDead()) {
            writeH(pc.getTempCharGfxAtDead());
        } else {
            writeH(pc.getTempCharGfx());
        }
        if (pc.isDead()) {
            writeC(pc.getStatus());
        } else {
            writeC(pc.getCurrentWeapon());
        }
        writeC(pc.getHeading());
        writeC(pc.getOwnLightSize());
        writeC(pc.getMoveSpeed());
        writeD((int) pc.getExp());
        writeH(pc.getLawful());
        final StringBuilder stringBuilder = new StringBuilder();// src009
        if (pc.get_other().get_color() != 0) {
            stringBuilder.append(pc.get_other().color());
        }
        // if (pc.isLawfulName() && pc.getLawful() >= 32767) {
        // stringBuilder.append("\\f=" + pc.getViewName());
        // } else {
        stringBuilder.append(pc.getViewName());
        // }
        writeS(stringBuilder.toString());
        writeS(pc.getTitle());
        writeC(status);
        writeD(pc.getClanid() > 0 ? pc.getClan().getEmblemId() : 0); // 盟徽編號
        writeS(pc.getClanname());
        writeS(null);
        writeC(pc.getClanRank() > 0 ? pc.getClanRank() << 4 : 0xb0); // 階級 * 16
        if (pc.isInParty()) { // パーティー中
            writeC(100 * pc.getCurrentHp() / pc.getMaxHp());
        } else {
            writeC(255);
        }
        if (pc.hasSkillEffect(STATUS_BRAVE3)) {
            writeC(8);
        } else {
            writeC(0);
        }
        writeC(0x00); // LV
        writeC(0x00);
        writeC(0xff);
        writeC(0xff);
        writeC(0);
        writeC(pc.getPolyStatus());
        writeC(0xFF);
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
        return getClass().getSimpleName();
    }
}
