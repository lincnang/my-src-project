package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.Controller.FishingTimeController;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CharVisualUpdate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 要求取消釣魚
 *
 * @author daien
 */
public class C_FishClick extends ClientBasePacket {
    private static final Log _log = LogFactory.getLog(C_FishClick.class);

    @Override
    public void start(final byte[] decrypt, final ClientExecutor client) {
        try {
            // 資料載入
            // this.read(decrypt);
            final L1PcInstance pc = client.getActiveChar();
            if (!pc.isFishing()) {
                return;
            }
            pc.setFishingTime(0);
            pc.setFishingReady(false);
            pc.setFishing(false);
            pc.setFishingItem(null);
            final S_CharVisualUpdate cv = new S_CharVisualUpdate(pc);
            pc.sendPacketsAll(cv);
            FishingTimeController.getInstance().removeMember(pc);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            over();
        }
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }
}
