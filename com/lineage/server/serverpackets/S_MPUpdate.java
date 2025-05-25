package com.lineage.server.serverpackets;

// 導入 L1PcInstance 類別，代表遊戲中的玩家角色實例
import com.lineage.server.model.Instance.L1PcInstance;
// 導入工具類 RangeInt，用來限制數值範圍
import com.lineage.server.utils.RangeInt;

/**
 * 用於更新角色 MP（魔力值）的封包類別。
 * 封包格式：
 *   C: 指令代碼
 *   H: 當前 MP（限制在 0~32767）
 *   H: 最大 MP（限制在 1~32767）
 */
public class S_MPUpdate extends ServerBasePacket {

    // 定義 MP 可接受的範圍：current MP 最小是 0，最大是 32767
    private static final RangeInt _mpRangeA = new RangeInt(0, 32767);

    // 定義最大 MP 的有效範圍：最小為 1（不能為 0），最大也是 32767
    private static final RangeInt _mpRangeX = new RangeInt(1, 32767);

    // 儲存封包的 byte 資料，用於傳送至用戶端
    private byte[] _byte = null;

    /**
     * 建構子：由 current MP 與 max MP 建立封包
     */
    public S_MPUpdate(int currentmp, int maxmp) {
        buildPacket(currentmp, maxmp);
    }

    /**
     * 建構子：由玩家物件取得 current MP 與 max MP，建立封包
     */
    public S_MPUpdate(L1PcInstance pc) {
        buildPacket(pc.getCurrentMp(), pc.getMaxMp());
    }

    /**
     * 建立封包資料內容
     * @param currentmp 當前 MP
     * @param maxmp 最大 MP
     */
    private void buildPacket(int currentmp, int maxmp) {
        writeC(S_MANA_POINT); // 寫入封包類型指令碼，S_MANA_POINT 為常數（可能定義在 ServerBasePacket 中）
        writeH(_mpRangeA.ensure(currentmp)); // 寫入當前 MP（經過範圍保護）
        writeH(_mpRangeX.ensure(maxmp));     // 寫入最大 MP（經過範圍保護）
    }

    /**
     * 傳回封包的 byte 陣列（若尚未建立則先呼叫 getBytes()）
     */
    public byte[] getContent() {
        if (_byte == null) {
            _byte = getBytes();
        }
        return _byte;
    }

    /**
     * 傳回這個封包類別的名稱
     */
    public String getType() {
        return getClass().getSimpleName();
    }
}
