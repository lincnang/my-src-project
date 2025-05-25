package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Clan;
import com.lineage.server.utils.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

public class S_PassiveSpells extends ServerBasePacket {
    /**
     * 血盟加入狀態
     */
    public static final int CLANJOIN = 67;
    /**
     * 攻城戰時間
     */
    public static final int WARTIME = 76;
    /**
     * 血盟設置狀態
     */
    public static final int CLANCONFIG = 77;
    /**
     * 道具製作時間
     */
    public static final int CRAFTTIME = 93;
    /**
     * 潘多拉彩票列表
     */
    public static final int LotteryList = 101;
    /**
     * 帶圖片的信息
     */
    public static final int IMGSERVERMESSAGE = 103;
    /**
     * 被動技能狀態/自定義技能
     */
    public static final int PASSIVESPELLSTATE = 110;
    /**
     * 添加寵物
     */
    public static final int ADDSUMMON = 119;
    /**
     * 魔法娃娃合成列表
     */
    public static final int DOLLCOMPOSELIST = 123;
    /**
     * 魔法娃娃合成狀態返回
     */
    public static final int DOLLCOMPOSEITEM = 125;
    /**
     * 魔法娃娃合成對話框
     */
    public static final int DOLLCOMPOSE = 128;
    /**
     * 添加人物背動技能
     */
    public static final int ADDPASSIVESPELL = 145;
    /**
     * 戰斧投擲
     */
    public static final int TOMAHAWK = 147;
    // 未知
    public static final int a = 194;
    /**
     * 城堡稅收
     */
    public static final int CASTLETAXES = 318;
    /**
     * 人物表情
     */
    public static final int EXPRESSION = 320;
    /**
     * 血盟加入窗口信息
     */
    public static final int CLANJOINWINDOWNINFO = 325;
    /**
     * 噬魂塔排名
     */
    public static final int SOULTOWERRANKED = 335;
    /**
     * 組隊勛章設置
     */
    public static final int PARTYMEDALICON = 339;
    /**
     * 人物背包重量信息
     */
    public static final int INVWEIGHT = 485;
    /**
     * 萬能藥使用次數
     */
    public static final int ABILITYELIXIR = 489;
    /**
     * 同盟說話
     */
    public static final int ALLIANCECHAT = 516;
    /**
     * 人物血盟名稱和階級
     */
    public static final int ClanNameAndRank = 537;
    private static final Log _log = LogFactory.getLog(S_PassiveSpells.class);
    private byte[] _byte = null;

    public S_PassiveSpells(String clanname, int clanRank) {
        writeC(S_EXTENDED_PROTOBUF);
        writeC(25);
        this.writeC(0x02);
        this.writeC(0x0a);
        int length = 0;
        if (clanname != null) {
            length = clanname.getBytes().length;
        }
        if (length > 0) {
            this.writeC(length);
            this.writeByte(clanname.getBytes());
            this.writeC(0x10);
            this.writeC(clanRank);
        } else {
            this.writeC(0);
        }
        this.writeH(0);
    }

    public S_PassiveSpells(int type, L1Clan clan) {
        writeC(S_EXTENDED_PROTOBUF);
        switch (type) {
            case CLANJOIN:
                writeC(type);
                writeC(0x01);
                writeCC(1, 0x02);
                writeCS(2, clan.getClanName());
                writeCC(3, clan.getJoin_state());
                writeH(0);
                break;
        }
    }

    /**
     * 發送技能信息
     */
    public S_PassiveSpells(int type, L1Character pc) {
        // public S_PassiveSpells(L1PcInstance pc, int type) {
        writeC(S_EXTENDED_PROTOBUF);
        if (type > 127) {
            writeH(type);
        } else {
            writeC(type);
            writeC(0);
        }
        switch (type) {
            case ClanNameAndRank:
                writeCS(1, ((L1PcInstance) pc).getClanname());
                writeCC(2, ((L1PcInstance) pc).getClanRank());
                writeH(0);
                break;
            case CLANCONFIG:
                L1Clan clan = ((L1PcInstance) pc).getClan();
                writeCC(1, 1);
                writeCC(2, clan.getJoin_open_state() ? 1 : 0);
                writeCC(3, clan.getJoin_state());
                if (clan.getJoin_password() == null || clan.getJoin_password().length() <= 0) {
                    byte[] pass = new byte[20];
                    writeByte(4, pass);
                } else {
                    writeByte(4, StringUtil.encode(clan.getJoin_password()));
                }
                writeH(0);
                break;
            default:
                _log.info("新製作系統、被動技能系統位置TYPE1:" + type);
                break;
        }
    }

    @Override
    public byte[] getContent() throws IOException {
        if (_byte == null) {
            //_byte = getBytes();
            _byte = _bao.toByteArray();
        }
        return _byte;
    }
}
