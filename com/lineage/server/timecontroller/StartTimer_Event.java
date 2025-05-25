package com.lineage.server.timecontroller;

import com.lineage.server.timecontroller.event.T_Special;

/**
 * 活动专用时间轴 初始化启动<BR>
 * 不能利用活动设置控制的例外时间轴
 *
 * @author dexc
 */
public class StartTimer_Event {
    public void start() throws InterruptedException {
        //final WorldChatTimer worldChat = new WorldChatTimer();
        //worldChat.start();
        //TimeUnit.MILLISECONDS.sleep(50);// 延迟
        T_Special.getStart();
        //TimeUnit.MILLISECONDS.sleep(50);// 延迟
    }
}
