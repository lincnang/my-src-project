package com.lineage.server.netty;

import com.lineage.echo.ClientExecutor;
import com.lineage.echo.PacketHandler;
import org.jboss.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 共用的 Channel 資源註冊器，避免直接使用 Channel 附加屬性。
 */
public final class NettyChannelRegistry {
    private NettyChannelRegistry() {}

    private static final ConcurrentMap<Channel, GameClient> CLIENTS = new ConcurrentHashMap<Channel, GameClient>();
    private static final ConcurrentMap<Channel, PacketHandler> HANDLERS = new ConcurrentHashMap<Channel, PacketHandler>();
    private static final ConcurrentMap<Channel, ClientExecutor> LEGACY = new ConcurrentHashMap<Channel, ClientExecutor>();
    private static final ConcurrentMap<ClientExecutor, Channel> LEGACY_REVERSE = new ConcurrentHashMap<ClientExecutor, Channel>();

    public static GameClient client(Channel ch) { return CLIENTS.get(ch); }
    public static void client(Channel ch, GameClient c) { if (c == null) CLIENTS.remove(ch); else CLIENTS.put(ch, c); }

    public static PacketHandler handler(Channel ch) { return HANDLERS.get(ch); }
    public static void handler(Channel ch, PacketHandler h) { if (h == null) HANDLERS.remove(ch); else HANDLERS.put(ch, h); }

    public static ClientExecutor legacy(Channel ch) { return LEGACY.get(ch); }
    public static void legacy(Channel ch, ClientExecutor c) {
        if (c == null) {
            ClientExecutor removed = LEGACY.remove(ch);
            if (removed != null) LEGACY_REVERSE.remove(removed);
        } else {
            LEGACY.put(ch, c);
            LEGACY_REVERSE.put(c, ch);
        }
    }

    public static Channel channelOf(ClientExecutor legacy) { return LEGACY_REVERSE.get(legacy); }

    /** 標記該連線已完成版本驗證（若為 Netty GameClient）。*/
    public static void markVersionVerified(ClientExecutor legacy) {
        Channel ch = LEGACY_REVERSE.get(legacy);
        if (ch == null) return;
        GameClient gc = CLIENTS.get(ch);
        if (gc != null) {
            gc.setVersionVerified(true);
        }
    }
}
