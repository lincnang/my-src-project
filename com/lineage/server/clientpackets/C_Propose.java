/**
 * License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE").
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR
 * COPYRIGHT LAW IS PROHIBITED.
 * <p>
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND
 * AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE
 * MAY BE CONSIDERED TO BE A CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 */
package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1PcQuest;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.utils.FaceToFace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

/**
 * 處理收到由客戶端傳來結婚的封包
 */
public class C_Propose extends ClientBasePacket {
    private static final Log _log = LogFactory.getLog(C_Propose.class);

    @Override
    public void start(final byte[] decrypt, final ClientExecutor client) {
        try {
            // 資料載入
            this.read(decrypt);
            final int c = this.readC();
            final L1PcInstance pc = client.getActiveChar();
            if (c == 0) { // /propose（/結婚）
                if (pc.isGhost()) {
                    return;
                }
                if (pc.getPartnerId() == 0 || pc.getQuest().get_step(L1PcQuest.QUEST_MARRY) == 0) {
                    L1PcInstance target = FaceToFace.faceToFace(pc);
                    if (target.getPartnerId() != 0 || target.getQuest().get_step(L1PcQuest.QUEST_MARRY) != 0) {
                        pc.sendPackets(new S_ServerMessage(658)); // \f1你(你)的對象已經結婚了。
                        return;
                    }
                    // 同性結婚
                    if (pc.get_sex() == target.get_sex()) {
                        pc.sendPackets(new S_ServerMessage(661)); // \f1結婚對像性別必須和您不同。
                        return;
                    }
                    /** [原碼] 結婚系統 */
                    // 檢查雙方等級總合是否大於等於50
                    if (pc.getLevel() + target.getLevel() < 50) {
                        pc.sendPackets(new S_SystemMessage("雙方總等級未等於50以上。"));
                        return;
                    }
                    // 檢查雙方是否有結婚戒指
                    if (!checkPcItem(pc)) {
                        pc.sendPackets(new S_ServerMessage(659)); // \f1你(你)沒有結婚戒指。
                        return;
                    }
                    if (!checkTargetItem(target)) {
                        pc.sendPackets(new S_ServerMessage(660)); // \f1你(你)的對象沒有結婚戒指。
                        return;
                    }
                    /** End */
                    if (pc.getX() >= 33974 && pc.getX() <= 33976 && pc.getY() >= 33362 && pc.getY() <= 33365 && pc.getMapId() == 4 && target.getX() >= 33974 && target.getX() <= 33976 && target.getY() >= 33362 && target.getY() <= 33365 && target.getMapId() == 4) {
                        target.setTempID(pc.getId()); // 暫時儲存對象的角色ID
                        target.sendPackets(new S_Message_YN(654, pc.getName())); // %0
                        // 向你(你)求婚，你(你)答應嗎?（Y/N）
                    } else {
                        pc.sendPackets(new S_SystemMessage("必須在教堂中才能進行。"));
                    }
                }
            }
            // 離婚
            else if (c == 1) { // /divorce（/離婚）
                if (pc.getPartnerId() == 0) {
                    pc.sendPackets(new S_ServerMessage(662)); // \f1結婚。
                    return;
                }
                pc.sendPackets(new S_Message_YN(653, "")); // 離婚消。離婚望？（Y/N）
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        } finally {
            this.over();
        }
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }

    // 檢查雙方是否有結婚戒指
    public boolean checkPcItem(L1PcInstance pc) {
        boolean PcRing = false;
        int[] Ties = {40901, 40902, 40903, 40904, 40905, 40906, 40907, 40908};
        for (int tie : Ties) {
            if (pc.getInventory().checkItem(tie)) {
                PcRing = true;
            }
        }
        return PcRing;
    }

    public boolean checkTargetItem(L1PcInstance target) {
        boolean TargetRing = false;
        int[] Ties = {40901, 40902, 40903, 40904, 40905, 40906, 40907, 40908};
        for (int tie : Ties) {
            if (target.getInventory().checkItem(tie)) {
                TargetRing = true;
            }
        }
        return TargetRing;
    }
}
