package com.lineage.server.clientpackets;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.datatables.RecordTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1Object;
import com.lineage.server.serverpackets.S_ChangeHeading;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

public class C_PickUpItem extends ClientBasePacket {
    public void start(byte[] decrypt, ClientExecutor client) {
        try {
            read(decrypt);
            L1PcInstance pc = client.getActiveChar();
            if (pc.isGhost())
                ;
            while ((pc.isDead()) || (pc.isTeleport()) || (pc.isPrivateShop()) || (pc.isInvisble()) || (pc.isInvisDelay())) {
                return;
            }
            int x = readH();
            int y = readH();
            int objectId = readD();
            long pickupCount = readD();
            if (pickupCount > 2147483647L) {
                pickupCount = 2147483647L;
            }
            pickupCount = Math.max(0L, pickupCount);
            L1Inventory groundInventory = World.get().getInventory(x, y, pc.getMapId());
            L1Object object = groundInventory.getItem(objectId);
            if ((object != null) && (!pc.isDead())) {
                L1ItemInstance item = (L1ItemInstance) object;
                if (item.getCount() <= 0L) {
                    return;
                }
                if ((item.getItemOwnerId() != 0) && (pc.getId() != item.getItemOwnerId())) {
                    pc.sendPackets(new S_ServerMessage(623));
                    return;
                }
                if (pc.getLocation().getTileLineDistance(item.getLocation()) > 3) {
                    return;
                }
                item.set_showId(-1);
                if ((pc.getInventory().checkAddItem(item, pickupCount) == 0) && (item.getX() != 0) && (item.getY() != 0)) {
                    groundInventory.tradeItem(item, pickupCount, pc.getInventory());
                    pc.turnOnOffLight();// 切換角色的光源狀態
                    pc.setHeading(pc.targetDirection(item.getX(), item.getY()));// 設置角色朝向物品的方向
                    if (!pc.isGmInvis()) {// 如果角色不是GM隱身狀態
                        pc.broadcastPacketAll(new S_ChangeHeading(pc)); // 廣播角色朝向改變
                        pc.sendPacketsAll(new S_DoActionGFX(pc.getId(), 15));// 發送動作特效（動作代碼15）
                    }
                    //拾取物品記錄
                    WriteLogTxt.Recording("拾取物品記錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【" + pc.getName() + "】帳號:【" + pc.getAccountName() + "】拾取 【" + "+" + item.getEnchantLevel() + " " + item.getItem().getName() + "】 物品代號【" + item.getItemId() + "】【" + item.getCount() + "】個  OBJID:" + item.getId() + "】.");

                }

                RecordTable.get().recordtakeitem(pc.getName(), item.getAllName(), (int) pickupCount, item.getId(), pc.getIp());
            }
        } catch (Exception localException) {
        } finally {
            over();
        }
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.clientpackets.C_PickUpItem JD-Core Version: 0.6.2
 */