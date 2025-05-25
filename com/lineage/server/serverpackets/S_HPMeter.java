package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;

/**
 * 物件血条封包
 * <p>
 * 该类用于创建和发送显示在客户端的角色血量和魔法值（MP）条的封包。
 *
 * @author admin
 */
public class S_HPMeter extends ServerBasePacket {
    // 用于存储封包的字节数组
    private byte[] _byte = null;

    /**
     * 构造函数：通过对象ID、血量百分比和魔法值百分比创建血条封包
     *
     * @param objId   物件的唯一标识ID
     * @param hpRatio 血条的百分比（0-100）
     * @param mpRatio 魔法值条的百分比（0-100，或0xff表示不可见）
     */
    public S_HPMeter(int objId, int hpRatio, int mpRatio) {
        buildPacket(objId, hpRatio, mpRatio);
    }

    /**
     * 构造函数：通过L1Character对象创建血条封包
     *
     * @param cha 角色的L1Character实例
     */
    public S_HPMeter(L1Character cha) {
        final int objId = cha.getId(); // 获取角色的唯一ID
        int hpRatio = 100; // 默认血量百分比为100%

        // 计算血量百分比，避免除以0
        if (0 < cha.getMaxHp()) {
            hpRatio = (100 * cha.getCurrentHp()) / cha.getMaxHp();
        }

        // 计算魔法值百分比，如果最大魔法值为0则设为0xff（表示不可见）
        int mpRatio = cha.getMaxMp() == 0 ? 0xff : 100 * cha.getCurrentMp() / cha.getMaxMp();

        // 如果角色不是玩家实例，则魔法值条不可见
        if (!(cha instanceof L1PcInstance)) {
            mpRatio = 0xff;
        }

        buildPacket(objId, hpRatio, mpRatio);
    }

    /**
     * 构建封包内容
     *
     * @param objId   物件的唯一标识ID
     * @param hpRatio 血条的百分比
     * @param mpRatio 魔法值条的百分比
     */
    private void buildPacket(int objId, int hpRatio, int mpRatio) {
        writeC(S_HIT_RATIO); // 写入封包类型标识（假设S_HIT_RATIO是定义的常量）
        writeD(objId);       // 写入物件ID（4字节）
        writeC(hpRatio);     // 写入血量百分比（1字节）
        writeC(mpRatio);     // 写入魔法值百分比（1字节）
        writeH(0x00);        // 写入额外的短整型数据（2字节），此处为0
    }

    /**
     * 获取封包内容的字节数组
     *
     * @return 封包的字节数组
     */
    @Override
    public byte[] getContent() {
        // 如果字节数组尚未生成，则生成并缓存
        if (_byte == null) {
            _byte = _bao.toByteArray();
        }
        return _byte;
    }

    /**
     * 获取封包类型的描述
     *
     * @return 封包类型的字符串描述
     */
    @Override
    public String getType() {
        return "[S] " + this.getClass().getSimpleName() + " [S->C 发送物件血条封包]";
    }
}
