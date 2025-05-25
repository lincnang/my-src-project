package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.lock.CharacterConfigReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Config;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 要求紀錄快速鍵
 *
 * @author 修改快捷欄可能加載失敗的問題 by 聖子默默
 */
public class C_CharacterConfig extends ClientBasePacket {
    private static final Log _log = LogFactory.getLog(C_CharacterConfig.class);

    @Override
    public void start(final byte[] decrypt, final ClientExecutor client) {
        try {
            // 資料載入
            read(decrypt);
            final L1PcInstance pc = client.getActiveChar();
            if (pc == null) {
                return;
            }
            final int length = readD();
            if (length > 2048) {
                return;
            }
            byte[] dates = new byte[length];
            for (int i = 0; i < length; i++) {
                dates[i] = (byte) readC();
            }
            L1Config config = CharacterConfigReading.get().get(pc.getId());
            if (config != null) {
                CharacterConfigReading.get().updateCharacterConfig(pc.getId(), length, dates);
            } else {
                CharacterConfigReading.get().storeCharacterConfig(pc.getId(), length, dates);
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            over();
        }
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
