package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.URandom;
import william.L1WilliamSystemMessage;

/**
 * 全頻聊天字串(全頻廣播器)
 *
 * @author user
 */
public final class S_AllChannelsChat extends ServerBasePacket {
    private byte[] _byte = null;

    /**
     * 全頻道聊天字串 隨機顏色
     * @param chat 文字肇
     */
    public S_AllChannelsChat(String chat) {
        writeC(S_MESSAGE);
        writeC(18);
        writeS(chat);
        writeH(URandom.nextInt(100));
    }

    /**
     * 全頻道聊天字串(全頻廣播器)
     *
     * @param pc    pc -- name
     * @param chat  字符串
     * @param color 2绿色 3红色  13黄色 14白色<br> 22浅绿 44浅蓝 45浅红 84粉红
     */
    public S_AllChannelsChat(L1PcInstance pc, String chat, int color) {
        writeC(S_MESSAGE);
        writeC(18);
        String message = String.format(L1WilliamSystemMessage.ShowMessage(887), pc.getName(), chat);
        writeS(message);
        writeH(color);
    }

    /**
     * 全頻道聊天字串(全頻廣播器)
     *
     * @param chat  字符串
     * @param color 2绿色 3红色  13黄色 14白色<br> 22浅绿 44浅蓝 45浅红 84粉红
     */
    public S_AllChannelsChat(String chat, int color) {
        writeC(S_MESSAGE);
        writeC(18);
        writeS(chat);
        writeH(color);
    }

    @Override
    public String getType() {
        return "[S] " + getClass().getSimpleName();
    }

    @Override
    public byte[] getContent() {
        if (this._byte == null) {
            this._byte = getBytes();
        }
        return this._byte;
    }
}
