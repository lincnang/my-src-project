package com.lineage.server.serverpackets;

import com.lineage.server.templates.L1Config;

import java.util.Arrays;

/**
 * 人物快速鍵紀錄檔案(S_OPCODE_PACKETBOX)
 *
 * @author dexc
 */
public class S_PacketBoxConfig extends ServerBasePacket {

    /**
     * <font color=#00f800>更新角色使用的快速鍵</font>
     */
    public static final int CHARACTER_CONFIG = 0x29;// 41
    private byte[] _byte = null;

    /**
     * 人物快速鍵紀錄檔案<br>
     * 修改快捷欄可能加載失敗的問題 by 聖子默默
     *
     * @param config 伺服端記錄
     */
    public S_PacketBoxConfig(final L1Config config) {
        final int length = config.getLength();
        // final byte[] data = config.getData();
        // 深拷贝配置数据，避免原始数据被修改
        final byte[] data = Arrays.copyOf(config.getData(), config.getData().length);
        this.writeC(S_EVENT);
        this.writeC(CHARACTER_CONFIG);
        // 写入数据到字节数组
        if (length != 0) {
            this.writeD(length);
            this.writeByte(data);
        } else {
            this.writeD(0);
        }
        this.writeH(0x00);
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
        return super.getType();
    }
}
