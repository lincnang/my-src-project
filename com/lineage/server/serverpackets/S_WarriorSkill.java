package com.lineage.server.serverpackets;

import java.io.IOException;

/**
 * 戰士技能 or 黑妖新技能 熾烈鬥志
 *
 * @author simlin
 */
public class S_WarriorSkill extends ServerBasePacket {
    public static final int LOGIN = 0x91;

    public static final int ADD = 0x92;
    boolean warriorSkill_1 = false;// 粉碎
    boolean warriorSkill_2 = false;// 狂暴
    boolean warriorSkill_3 = false;// 迅猛雙斧
    boolean warriorSkill_5 = false;// 護甲身軀
    boolean warriorSkill_6 = false;// 泰坦：岩石
    boolean warriorSkill_7 = false;// 泰坦：子彈
    boolean warriorSkill_8 = false;// 泰坦：魔法
    boolean warriorSkill_9 = false;// 熾烈鬥志

    /**
     * 學習技能時
     */
    public S_WarriorSkill(final int subcode, final int type) {
        writeC(S_EXTENDED_PROTOBUF); // XXX S_OPCODE_EXTENDED_PROTOBUF 修改為
        // S_OPCODE_CRAFTSYSTEM
        writeC(subcode);
        switch (subcode) {
		/*case LOGIN:
			writeC(0x01);
			writeC(0x0a);
			writeC(0x02);
			writeC(0x08);
			writeC(type);
			writeC(0x82);
			writeC(0x0b);
			break;
		case ADD:
			writeC(0x01);
			writeC(0x08);
			writeC(type);
			writeC(0x10);
			writeC(0x0a);
			writeH(0x33c2);
			break;*/
            case LOGIN: // 登錄
                // b3 91 01 0a 02 08 03 8c c7
                //---------------------------- 以下 2025.1.27 修改
                //                writeC(0x01);
                //                writeC(0x0a);
                //                writeC(0x04);
                //                writeC(0x08);
                //                writeC(type);
                //                //            if (type == 5) { // 護甲身軀
                //                //
                //                //				writeC(0x10);
                //                //				writeC(0x0a);
                //                //			}
                //                writeH(0xf18d);
                //                break;
                //---------------------------- END
                writeC(0x01); // 1
                writeC(0x0a); // 10
                writeC(type != 5 ? 0x02 : 0x04); // 2:4
                writeC(0x08); // 8
                writeC(type);
                if (type == 5) { // アーマーガード(護甲身軀)
                    writeC(0x10);
                    writeC(0x0a);
                }
                writeH(0xf18d); // 61837
                break;
            case ADD:
                // b3 92 01 08 03 c2 33
                //---------------------------- 以下 2025.1.27 修改
                //                writeC(0x01);
                //                writeC(0x08);
                //                writeC(type);
                //                if (type == 5) { // 護甲身軀
                //                    writeC(0x10);
                //                    writeC(0x0a);
                //                }
                //                writeH(0x00);
                //                break;
                //---------------------------- END
                writeC(0x01);
                writeC(0x08);
                writeC(type);
                writeC(0x10); // 16
                writeC(0x0a); // 10
                writeH(0x33c2); // 13250
                break;
        }
    }

    /**
     * 重新登入時
     */
    public S_WarriorSkill(final int subcode, int[] skills) {
        writeC(S_EXTENDED_PROTOBUF); // XXX S_OPCODE_EXTENDED_PROTOBUF 修改為
        // S_OPCODE_CRAFTSYSTEM
        writeC(subcode);
        writeC(0x01);
        for (int i = 0; i < skills.length; i++) {
            if (skills[i] == 1) {
                warriorSkill_1 = true;
            } else if (skills[i] == 2) {
                warriorSkill_2 = true;
            } else if (skills[i] == 3) {
                warriorSkill_3 = true;
            } else if (skills[i] == 5) {
                warriorSkill_5 = true;
            } else if (skills[i] == 6) {
                warriorSkill_6 = true;
            } else if (skills[i] == 7) {
                warriorSkill_7 = true;
            } else if (skills[i] == 8) {
                warriorSkill_8 = true;
            } else if (skills[i] == 9) {// 熾烈鬥志
                warriorSkill_9 = true;
            }
        }
        if (warriorSkill_1) {
            writeC(0x0a);
            writeC(0x02);
            writeC(0x08);
            writeC(0x01);
        } else {
            writeC(0);
            writeC(0);
            writeC(0);
            writeC(0);
        }
        if (warriorSkill_2) {
            writeC(0x0a);
            writeC(0x02);
            writeC(0x08);
            writeC(0x02);
        } else {
            writeC(0);
            writeC(0);
            writeC(0);
            writeC(0);
        }
        if (warriorSkill_3) {
            writeC(0x0a);
            writeC(0x02);
            writeC(0x08);
            writeC(0x03);
        } else {
            writeC(0);
            writeC(0);
            writeC(0);
            writeC(0);
        }
        if (warriorSkill_5) {
            writeC(0x0a);
            writeC(0x04);
            writeC(0x08);
            writeC(0x05);
            writeC(0x10);
            writeC(0x0a);
        } else {
            writeC(0);
            writeC(0);
            writeC(0);
            writeC(0);
            writeC(0);
            writeC(0);
        }
        if (warriorSkill_6) {
            writeC(0x0a);
            writeC(0x02);
            writeC(0x08);
            writeC(0x06);
        } else {
            writeC(0);
            writeC(0);
            writeC(0);
            writeC(0);
        }
        if (warriorSkill_7) {
            writeC(0x0a);
            writeC(0x02);
            writeC(0x08);
            writeC(0x07);
        } else {
            writeC(0);
            writeC(0);
            writeC(0);
            writeC(0);
        }
        if (warriorSkill_8) {
            writeC(0x0a);
            writeC(0x02);
            writeC(0x08);
            writeC(0x08);
        } else {
            writeC(0);
            writeC(0);
            writeC(0);
            writeC(0);
        }
        if (warriorSkill_9) {// 熾烈鬥志
            writeC(0x0a);
            writeC(0x02);
            writeC(0x08);
            writeC(0x09);
        } else {
            writeC(0);
            writeC(0);
            writeC(0);
            writeC(0);
        }
        writeC(0x00);
        writeC(0x00);
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }

    @Override
    public byte[] getContent() throws IOException {
        return _bao.toByteArray();
    }
}
