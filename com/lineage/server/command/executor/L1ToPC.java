package com.lineage.server.command.executor;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Account;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.Teleportation;
import com.lineage.server.world.World;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class L1ToPC implements L1CommandExecutor {
    private static final Log _log = LogFactory.getLog(L1ToPC.class);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
    private static List<L1PcInstance> _all_players;

    public static L1CommandExecutor getInstance() {
        return new L1ToPC();
    }

    public static void checkTPhtml(L1PcInstance pc, int page) {
        List<L1PcInstance> players = _all_players;
        if (players == null) {
            pc.sendPackets(new S_NPCTalkReturn(pc, "topcscreen"));
            return;
        }
        int length = players.size();
        int size = length / 12 + (length % 12 == 0 ? 0 : 1);
        if ((page < 0) || (page >= size)) {
            return;
        }
        pc.get_other().set_page(page);
        String[] html_data = new String[13];
        html_data[0] = (page + 1 + "/" + size);
        int index = 1;
        int i = page * 12;
        for (int end = i + 12; (i < end) && (i < length); index++) {
            L1PcInstance player = World.get().getPlayer(((L1PcInstance) players.get(i)).getName());
            if (player != null) {
                L1Account acc = player.getNetConnection().getAccount();
                if (acc != null) {
                    html_data[index] = (player.getName() + " (Lv." + player.getLevel() + ") [" + sdf.format(acc.get_lastactive()) + "]");
                }
            }
            i++;
        }
        pc.sendPackets(new S_NPCTalkReturn(pc, "topcscreen", html_data));
    }

    public static void checkTPhtmlPredicate(L1PcInstance pc, int page, boolean refreshAll) {
        List<L1PcInstance> all_players = new ArrayList<>();
        if (refreshAll) {
            for (L1PcInstance tgpc : World.get().getAllPlayers()) {
                if ((tgpc.getMap().isUsableShop() == 0) && (tgpc.getMapId() != 5143) && (tgpc.getMapId() != 5301) && (tgpc.getMapId() != 5490)) {
                    all_players.add(tgpc);
                }
            }
        } else {
            for (L1PcInstance tgpc : World.get().getAllPlayers()) {
                if (tgpc.getMapId() == pc.getMapId()) {
                    all_players.add(tgpc);
                }
            }
        }
        _all_players = all_players;
        List<L1PcInstance> players = _all_players;
        if (players == null) {
            pc.sendPackets(new S_NPCTalkReturn(pc, "topcscreen"));
            return;
        }
        int length = players.size();
        int size = length / 12 + (length % 12 == 0 ? 0 : 1);
        if ((page < 0) || (page >= size)) {
            return;
        }
        pc.get_other().set_page(page);
        String[] html_data = new String[13];
        html_data[0] = (page + 1 + "/" + size);
        int index = 1;
        int i = page * 12;
        for (int end = i + 12; (i < end) && (i < length); index++) {
            L1PcInstance player = World.get().getPlayer(((L1PcInstance) players.get(i)).getName());
            if (player != null) {
                L1Account acc = player.getNetConnection().getAccount();
                if (acc != null) {
                    html_data[index] = (player.getName() + " (Lv." + player.getLevel() + ") [" + sdf.format(acc.get_lastactive()) + "]");
                }
            }
            i++;
        }
        pc.sendPackets(new S_NPCTalkReturn(pc, "topcscreen", html_data));
    }

    public static void teleport2Player(final L1PcInstance pc, int index) {
        if (_all_players == null) {
            pc.sendPackets(new S_SystemMessage("請點選(刷新)選項。"));
            return;
        }
        int point = pc.get_other().get_page() * 12 + index;
        if ((point < 0) || (point >= _all_players.size())) {
            pc.sendPackets(new S_SystemMessage("玩家列表被變動過，請重新輸入(.topc)來取得目前列表。"));
            return;
        }
        L1PcInstance target = (L1PcInstance) _all_players.get(point);
        if (target != null) {
            final L1PcInstance player = World.get().getPlayer(target.getName());
            if (player != null) {
                GeneralThreadPool.get().schedule(() -> {
                    pc.set_showId(player.get_showId());
                    pc.sendPacketsX8(new S_SkillSound(pc.getId(), 2236));
                    pc.setTeleportX(player.getX());
                    pc.setTeleportY(player.getY());
                    pc.setTeleportMapId(player.getMapId());
                    Teleportation.teleportation(pc);
                    pc.sendPackets(new S_SystemMessage("移動至指定人物身邊: " + player.getName()));
                    L1ToPC.checkTPhtml(pc, pc.get_other().get_page());
                }, 440);
                return;
            }
        }
        pc.sendPackets(new S_SystemMessage("該人物不存在或已離線。"));
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            L1PcInstance target = World.get().getPlayer(arg);
            if (target != null) {
                pc.set_showId(target.get_showId());
                L1Teleport.teleport(pc, target.getX(), target.getY(), target.getMapId(), pc.getHeading(), false);
                pc.sendPackets(new S_SystemMessage("移動至指定人物身邊: " + arg));
            } else {
                checkTPhtml(pc, 0);
            }
        } catch (Exception e) {
            _log.error("錯誤的GM指令格式: " + getClass().getSimpleName() + " 執行的GM:" + pc.getName());
            pc.sendPackets(new S_ServerMessage(261));
        }
    }
}
