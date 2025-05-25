package com.lineage.server.Controller;

import com.lineage.server.datatables.FishingTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.serverpackets.*;
import com.lineage.server.templates.L1Fishing;
import com.lineage.server.templates.L1Item;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 日版釣魚
 */
public class FishingTimeController implements Runnable {
    public static final Log _log = LogFactory.getLog(FishingTimeController.class);
    private static FishingTimeController _instance;
    private static Random _random = new Random(System.nanoTime());
    private final List<L1PcInstance> _fishingList = new ArrayList<>();

    public static FishingTimeController getInstance() {
        if (_instance == null) {
            _instance = new FishingTimeController();
        }
        return _instance;
    }

    @Override
    public void run() {
        try {
            fishing();
            GeneralThreadPool.get().schedule(this, 300);
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public void addMember(final L1PcInstance pc) {
        if (pc == null || _fishingList.contains(pc)) {
            return;
        }
        _fishingList.add(pc);
    }

    public void removeMember(final L1PcInstance pc) {
        if (pc == null || !_fishingList.contains(pc)) {
            return;
        }
        _fishingList.remove(pc);
    }

    private void fishing() {
        try {
            if (_fishingList.size() > 0) {
                final long currentTime = System.currentTimeMillis();
                L1PcInstance[] list = _fishingList.toArray(new L1PcInstance[0]);
                for (final L1PcInstance pc : list) {
                    if (pc == null) {
                        removeMember(pc);
                        continue;
                    }
                    if (World.get().getPlayer(pc.getName()) == null) {
                        removeMember(pc);
                        continue;
                    }
                    if (pc.isFishing()) {
                        if (pc.getFishingItem() == null) {
                            fishingExit(pc);
                            removeMember(pc);
                            continue;
                        }
                        final long time = pc.getFishingTime();
                        if (currentTime <= (time + 1000) && currentTime >= (time - 1000)) {
                            pc.setFishingReady(true);
                            pc.sendPackets(new S_PacketBox(S_PacketBox.FISHING));
                        } else if (currentTime > (time + 100)) {
                            final L1Fishing temp = FishingTable.get().get_item(pc.getFishingItem().getItemId());
                            if (temp != null) {
                                if (_random.nextInt(1000000) <= temp.getRandom()) {
                                    // 釣魚成功
                                    successFishing(pc, temp.getGiveItemId(), temp.getGiveCount());
                                    if (temp.getShowWorld() == 1) { // 是否公告
                                        final L1Item item = ItemTable.get().getTemplate(temp.getGiveItemId());
                                        if (item != null) {
                                            // 2綠色 3紅色 13黃色 14白色 22淺綠 44淺藍 45淺紅
                                            World.get().broadcastPacketToAll(new S_AllChannelsChat("玩家(" + pc.getName() + ")超級幸運，釣到(" + item.getName() + ")。", 2));
                                        }
                                    }
                                    if (temp.getFishGfx() != 0) { // 是否有特效
                                        final S_SkillSound sound = new S_SkillSound(pc.getId(), temp.getFishGfx());
                                        pc.sendPacketsX8(sound);
                                    }
                                } else {
                                    // 釣魚失敗
                                    if (temp.getFailItemId() != 0) { // 釣魚失敗給予的物品編號
                                        final L1ItemInstance failitem = ItemTable.get().createItem(temp.getFailItemId());
                                        pc.getInventory().storeItem(temp.getFailItemId(), temp.getFailCount()); // 釣魚失敗給予的物品編號
                                        if (temp.getFailCount() == 1) {
                                            pc.sendPackets(new S_ServerMessage(403, failitem.getName())); // 獲得%0。
                                        } else {
                                            pc.sendPackets(new S_ServerMessage(403, failitem.getName() + "(" + temp.getFailCount() + ")")); // 獲得%0。
                                        }
                                    } else {
                                        pc.sendPackets(new S_ServerMessage(1517, "")); // 釣魚：魚逃走了。
                                    }
                                    pc.getInventory().consumeItem(83002, 1); // 營養魚餌
                                    if (pc.getFishingItem().getItemId() != 83001) { // 不是高彈力釣竿
                                        pc.getFishingItem().setChargeCount(pc.getFishingItem().getChargeCount() - 1);
                                        pc.getInventory().updateItem(pc.getFishingItem(), L1PcInventory.COL_CHARGE_COUNT);
                                        if (pc.getFishingItem().getChargeCount() <= 0) {
                                            pc.getInventory().removeItem(pc.getFishingItem(), 1);
                                            pc.getInventory().storeItem(83001, 1); // 高彈力釣竿
                                            fishingExit(pc);
                                        }
                                    }
                                    if (pc.isFishing() && !pc.getInventory().checkItem(83002, 1)) { // 營養魚餌
                                        fishingExit(pc);
                                        pc.sendPackets(new S_ServerMessage(1137)); // 釣魚：缺少魚餌。
                                        removeMember(pc);
                                    }
                                }
                            } else {
                                _log.error(pc.getFishingItem().getItemId() + "釣竿不存在可釣的道具!");
                            }
                            pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.FISH_WINDOW, 2, false, 0));
                            if (pc.isFishing()) {
                                int itemFishTime = 240;
                                if (temp != null) {
                                    itemFishTime = temp.getFishTime();
                                }
                                final long fishtime = System.currentTimeMillis() + (itemFishTime * 1000L);
                                boolean ck = false;
                                if (pc.getFishingItem().getItemId() != 83001) { // 不是高彈力釣竿
                                    ck = true;
                                }
                                pc.setFishingTime(fishtime);
                                pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.FISH_WINDOW, 1, ck, (fishtime - System.currentTimeMillis()) / 1000));
                            }
                        }
                    } else {
                        removeMember(pc);
                        continue;
                    }
                }
                list = null;
            }
        } catch (final Exception e) {
            // TODO: handle exception
        }
    }

    private void fishingExit(final L1PcInstance pc) {
        pc.setFishingTime(0L);
        pc.setFishingReady(false);
        pc.setFishing(false);
        pc.setFishingItem(null);
        pc.sendPackets(new S_CharVisualUpdate(pc));
        pc.broadcastPacketAll(new S_CharVisualUpdate(pc));
    }

    private void successFishing(final L1PcInstance pc, final int itemId, final int count) {
        try {
            final boolean exp = false;
            // if (pc.getFishingItem().getItemId() == 600229) { // 加經驗的釣魚竿？
            // exp = true;
            // }
            final L1ItemInstance item = ItemTable.get().createItem(itemId);
            if (pc.getInventory().checkAddItem(item, count) != L1Inventory.OK) {
                fishingExit(pc);
                pc.sendPackets(new S_ServerMessage(1518)); // 釣魚：停止（負重過高）
                removeMember(pc);
                return;
            }
            // pc.getInventory().storeItem(item);
            pc.getInventory().storeItem(itemId, count);
            if (!exp) {
                pc.getInventory().consumeItem(83002, 1); // 營養魚餌
            }
            if (pc.getFishingItem().getItemId() != 83001) { // 不是高彈力釣竿
                pc.getFishingItem().setChargeCount(pc.getFishingItem().getChargeCount() - 1);
                pc.getInventory().updateItem(pc.getFishingItem(), L1PcInventory.COL_CHARGE_COUNT);
                if (pc.getFishingItem().getChargeCount() <= 0) {
                    pc.getInventory().removeItem(pc.getFishingItem(), 1);
                    pc.getInventory().storeItem(83001, 1); // 高彈力釣竿
                    fishingExit(pc);
                }
            }/* else if (exp) {
				int exp = 15000;
				double dragon = 1;
				int settingEXP = (int) ConfigRate.RATE_XP;
				if (pc.getAinHasad() > 10000) {
					pc.calAinHasad(-exp);
					if (pc.getAinHasad() > 2000000) {
						dragon = 2.3;
					} else {
						dragon = 2;
					}
					if (pc.增加經驗) {
						dragon += 0.20;
					}
					pc.sendPackets(new S_PacketBox(S_PacketBox.AINHASAD, pc));
				}
				if (pc.hasSkillEffect(L1SkillId.DRAGON_EME_2) && pc.getAinHasad() > 10000) {
					dragon += 0.8;
					pc.calAinHasad(-exp);
					pc.sendPackets(new S_PacketBox(S_PacketBox.AINHASAD, pc));
				} else if (pc.hasSkillEffect(L1SkillId.DRAGON_PUPLE) && pc.getAinHasad() > 10000) {
					if (pc.getLevel() >= 49 && pc.getLevel() <= 54)
						dragon += 0.53;
					else if (pc.getLevel() >= 55 && pc.getLevel() <= 59)
						dragon += 0.43;
					else if (pc.getLevel() >= 60 && pc.getLevel() <= 64)
						dragon += 0.33;
					else if (pc.getLevel() >= 65)
						dragon += 0.23;
					pc.calAinHasad(-exp);
					pc.sendPackets(new S_PacketBox(S_PacketBox.AINHASAD, pc));
					if (pc.getAinHasad() <= 10000) {
						pc.removeSkillEffect(L1SkillId.DRAGON_PUPLE);
					}
				} else if (pc.hasSkillEffect(L1SkillId.DRAGON_TOPAZ) && pc.getAinHasad() > 10000) {
					dragon += 0.8;
					pc.calAinHasad(-exp);
					pc.sendPackets(new S_PacketBox(S_PacketBox.AINHASAD, pc));
					if (pc.getAinHasad() <= 10000) {
						pc.removeSkillEffect(L1SkillId.DRAGON_TOPAZ);
					}
				}
				double exppenalty = ExpTable.getPenaltyRate(pc.getLevel());
				int add_exp = (int) (exp * settingEXP * dragon * exppenalty);
				pc.addExp(add_exp);
				pc.getFishingItem().setChargeCount(pc.getFishingItem().getChargeCount() - 1);
				pc.getInventory().updateItem(pc.getFishingItem(), L1PcInventory.COL_CHARGE_COUNT);
				if (pc.getFishingItem().getChargeCount() <= 0) {
					pc.getInventory().removeItem(pc.getFishingItem(), 1);
					pc.getInventory().storeItem(83001, 1); // 高彈力釣竿
					fishingExit(pc);
				}
				pc.save();
			}*/
            if (count == 1) {
                pc.sendPackets(new S_ServerMessage(403, item.getName())); // 獲得%0。
            } else {
                pc.sendPackets(new S_ServerMessage(403, item.getName() + "(" + count + ")")); // 獲得%0。
            }
            if (!exp && !pc.getInventory().checkItem(83002, 1)) { // 營養魚餌
                fishingExit(pc);
                pc.sendPackets(new S_ServerMessage(1137)); // 釣魚：缺少魚餌。
                removeMember(pc);
            }
        } catch (final Exception e) {
            e.printStackTrace();
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
