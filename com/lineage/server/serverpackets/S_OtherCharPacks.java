package com.lineage.server.serverpackets;

import com.lineage.config.ConfigOtherSet2;
import com.lineage.server.model.Instance.L1PcInstance;

import static com.lineage.server.model.skill.L1SkillId.STATUS_BRAVE3;

/**
 * 物件封包 - 其他人物
 *
 * @author dexc
 */
public class S_OtherCharPacks extends ServerBasePacket {
    private static final int STATUS_POISON = 1;
    private static final int STATUS_INVISIBLE = 2;
    private static final int STATUS_PC = 4;
    // private static final int STATUS_FREEZE = 8;
    private static final int STATUS_BRAVE = 16;
    private static final int STATUS_ELFBRAVE = 32;
    private static final int STATUS_FASTMOVABLE = 64;
    // private static final int STATUS_GHOST = 128;
    private byte[] _byte = null;

    /**
     * 物件封包 - 其他人物
     *
     */
    public S_OtherCharPacks(final L1PcInstance pc) {
        int status = STATUS_PC;
        if (pc.getPoison() != null) { // 毒狀態
            if (pc.getPoison().getEffectId() == 1) {
                status |= STATUS_POISON;
            }
        }
        if (pc.isInvisble()) {
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
        } else if (pc.isFishing()) {
            writeC(71);
        } else if (pc.isPrivateShop()) {
            writeC(70);
        } else {
            writeC(pc.getCurrentWeapon());
        }
        writeC(pc.getHeading());
        writeC(pc.getChaLightSize());
        writeC(pc.getMoveSpeed());
        writeD(0);// exp
        writeH(pc.getLawful());
        if ((pc.getMapId() == 2006) && (pc.get_redbluejoin() == 11)) {
            writeS("\\f=復仇者聯盟");
        } else if ((pc.getMapId() == 12152) && (pc.get_redbluejoin() == 21)) {
            writeS("\\f=復仇者聯盟");
        } else if ((pc.getMapId() == 2006) && (pc.get_redbluejoin() == 12)) {
            writeS("\\f2暗殺者組織");
        } else if ((pc.getMapId() == 12152) && (pc.get_redbluejoin() == 22)) {
            writeS("\\f2暗殺者組織");
        } else if (pc.isProtector()) {
            this.writeS(ConfigOtherSet2.Protector_Name);
        } else {
            // 是否開放給其他人看見[陣營稱號]和[轉生稱號] by terry0412
            if (ConfigOtherSet2.SHOW_SP_TITLE) {// src009
                final StringBuilder stringBuilder = new StringBuilder();
                if (pc.get_other().get_color() != 0) {
                    stringBuilder.append(pc.get_other().color());
                }

                stringBuilder.append(pc.getHonorViewName());//人物顯示名稱

                if (pc.isLawfulName() && pc.getLawful() >= 32767) {
                    stringBuilder.append("\\f=").append(pc.getName());
                }
                if (pc.get_c_power() != null && pc.get_c_power().get_c1_type() != 0) { // src038
                    final String type = pc.get_c_power().get_power().get_c1_name_type();
                    stringBuilder.append(type);
                }
                if (pc.getMeteAbility() != null) {
                    stringBuilder.append(pc.getMeteAbility().getTitle());
                }
                writeS(stringBuilder.toString());
            } else {
                if (pc.get_other().get_color() != 0) {
                    this.writeS(pc.get_other().color() + pc.getName());
                } else {
                    if (pc.isLawfulName() && pc.getLawful() >= 32767) {
                        this.writeS("\\f=" + pc.getName());
                    } else {
                        this.writeS(pc.getName());
                    }
                }
            }
        }
        if (pc.getMapId() == 2006 || pc.getMapId() == 12152) {
            writeS("");
        } else {
            writeS(pc.getTitle());
        }
        writeC(status); // 狀態
        if (pc.getMapId() == 2006 || pc.getMapId() == 12152) {
            writeD(0);
        } else {
            writeD(pc.getEmblemId());
        }
        writeS(pc.getClanname());
        writeS(null);
        writeC(pc.getClanRank() << 4);// 血盟階級
        writeC(255);
        if (pc.hasSkillEffect(STATUS_BRAVE3)) {
            writeC(8);
        } else {
            writeC(0);
        }
        writeC(0);// PC = 0, Mon = Lv
        writeC(0); // ？
        writeC(0xFF);
        writeC(0xFF);
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
