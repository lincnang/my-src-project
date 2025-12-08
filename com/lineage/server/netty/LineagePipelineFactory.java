package com.lineage.server.netty;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.handler.timeout.IdleStateHandler;

/**
 * Lineage Pipeline 工廠
 * 組裝 Netty 處理鏈: Decoder → Encoder → Handler
 * 
 * @author Netty Migration
 */
public class LineagePipelineFactory implements ChannelPipelineFactory {
    
    // 共用的 Timer 實例，避免每個連線產生一個執行緒
    private static final HashedWheelTimer SHARED_TIMER = new HashedWheelTimer();

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();
        
        // 0. Idle 逾時偵測（讀/寫/全閒置）
        // 使用共用的 Timer
        pipeline.addLast("idle", new IdleStateHandler(SHARED_TIMER, 600, 600, 900));

        // 1. 解碼器 (處理接收封包)
        pipeline.addLast("decoder", new LineageDecoder());
        
        // 2. 編碼器 (處理發送封包)
        pipeline.addLast("encoder", new LineageEncoder());
        
        // 3. 業務處理器
        pipeline.addLast("handler", new GameClientHandler());
        
        return pipeline;
    }
}
