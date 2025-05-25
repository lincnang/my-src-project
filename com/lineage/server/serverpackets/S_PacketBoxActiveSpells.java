package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

public class S_PacketBoxActiveSpells extends ServerBasePacket {
    private byte[] _byte = null;
    private int icon_list_size = 104;

    public S_PacketBoxActiveSpells(L1PcInstance pc) {
        writeC(S_EVENT);
        writeC(S_PacketBox.ICON_ACTIVESPELLS);
        writeD(icon_list_size);
        int[] type = activeSpells(pc);
        for (int i : type) {
            switch (i) {
                case 72:
                    writeD((int) (System.currentTimeMillis() / 1000L));
                    break;
                default:
                    writeC(i);
            }
        }
    }

    private int[] activeSpells(L1PcInstance pc) {
        int[] data = new int[icon_list_size];
        if (pc.hasSkillEffect(1017)) {// 生命之樹果實
            data[61] = (pc.getSkillEffectTimeSec(1017) >> 2);
        }
        if (pc.hasSkillEffect(111)) {// 迴避提升
            data[17] = (pc.getSkillEffectTimeSec(111) >> 2);
        }
        if (pc.hasSkillEffect(188)) {// 恐懼無助
            data[57] = (pc.getSkillEffectTimeSec(188) >> 2);
        }
        /** 附魔石效果 START */
        int[] bs_gx = {4401, 4402, 4403, 4404, 4405, 4406, 4407, 4408, 4409};
        for (int i = 0; i < bs_gx.length; i++) {
            if (pc.hasSkillEffect(bs_gx[i])) {
                data[102] = (pc.getSkillEffectTimeSec(bs_gx[i]) >> 5);
                if (data[102] != 0) {
                    data[103] = (bs_gx[i] - 4317);
                }
            }
        }
        int[] bs_ax = {4411, 4412, 4413, 4414, 4415, 4416, 4417, 4418, 4419};
        for (int i = 0; i < bs_ax.length; i++) {
            if (pc.hasSkillEffect(bs_ax[i])) {
                data[102] = (pc.getSkillEffectTimeSec(bs_ax[i]) >> 5);
                if (data[102] != 0) {
                    data[103] = (bs_ax[i] - 4318);
                }
            }
        }
        int[] bs_wx = {4421, 4422, 4423, 4424, 4425, 4426, 4427, 4428, 4429};
        for (int i = 0; i < bs_wx.length; i++) {
            if (pc.hasSkillEffect(bs_wx[i])) {
                data[102] = (pc.getSkillEffectTimeSec(bs_wx[i]) >> 5);
                if (data[102] != 0) {
                    data[103] = (bs_wx[i] - 4319);
                }
            }
        }
        int[] bs_asx = {4431, 4432, 4433, 4434, 4435, 4436, 4437, 4438, 4439};
        for (int i = 0; i < bs_asx.length; i++) {
            if (pc.hasSkillEffect(bs_asx[i])) {
                data[102] = (pc.getSkillEffectTimeSec(bs_asx[i]) >> 5);
                if (data[102] != 0) {
                    data[103] = (bs_asx[i] - 4320);
                }
            }
        }
        /** 附魔石效果 END */
        /** 魔眼效果 START */
        if (pc.hasSkillEffect(6683)) {
            data[78] = (pc.getSkillEffectTimeSec(6683) >> 5);
            if (data[78] != 0) {
                data[79] = 49;
            }
        }
        if (pc.hasSkillEffect(6684)) {
            data[78] = (pc.getSkillEffectTimeSec(6684) >> 5);
            if (data[78] != 0) {
                data[79] = 46;
            }
        }
        if (pc.hasSkillEffect(6685)) {
            data[78] = (pc.getSkillEffectTimeSec(6685) >> 5);
            if (data[78] != 0) {
                data[79] = 47;
            }
        }
        if (pc.hasSkillEffect(6686)) {
            data[78] = (pc.getSkillEffectTimeSec(6686) >> 5);
            if (data[78] != 0) {
                data[79] = 48;
            }
        }
        if (pc.hasSkillEffect(6687)) {
            data[78] = (pc.getSkillEffectTimeSec(6687) >> 5);
            if (data[78] != 0) {
                data[79] = 52;
            }
        }
        if (pc.hasSkillEffect(6688)) {
            data[78] = (pc.getSkillEffectTimeSec(6688) >> 5);
            if (data[78] != 0) {
                data[79] = 50;
            }
        }
        if (pc.hasSkillEffect(6689)) {
            data[78] = (pc.getSkillEffectTimeSec(6689) >> 5);
            if (data[78] != 0) {
                data[79] = 51;
            }
        }
        /** 魔眼效果 END */
        if (pc.hasSkillEffect(4009)) {// 卡瑞的祝福(地龍副本)
            data[76] = (pc.getSkillEffectTimeSec(4009) >> 5);
            if (data[76] != 0) {
                data[77] = 45;
            }
        }
        if (pc.hasSkillEffect(4010)) {// 莎爾的祝福(水龍副本)
            data[76] = (pc.getSkillEffectTimeSec(4010) >> 5);
            if (data[76] != 0) {
                data[77] = 60;
            }
        }
        if (pc.hasSkillEffect(8060)) {// 強化戰鬥卷軸
            data[46] = (pc.getSkillEffectTimeSec(8060) / 16);
            if (data[46] != 0) {
                data[47] = 2;
            }
        }
        return data;
    }

    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.serverpackets.S_PacketBoxActiveSpells JD-Core Version:
 * 0.6.2
 */