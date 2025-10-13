package com.lineage.managerUI;

import com.lineage.server.world.World;
import com.sun.management.OperatingSystemMXBean;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.BufferPoolMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ServerResourceDialog extends JDialog {
    private JLabel lblTotalMemory, lblUsedMemory, lblMaxMemory, lblThreadCount, lblOnlineCount, lblCpu, lblProcCpu, lblProcMemPct, lblGcCount, lblGcTime, lblDeadlock;
    private JButton btnRefresh, btnGC, btnThreadInfo;
    private Timer autoRefreshTimer;
    private ChartPanel piePanel; // 圓餅圖 Panel

    // 執行緒詳細資料字串
    private String threadInfoString = "";

    // 狀態中文對應表
    private static final Map<Thread.State, String> stateZhMap = new HashMap<>();
    static {
        stateZhMap.put(Thread.State.RUNNABLE, "執行中");
        stateZhMap.put(Thread.State.WAITING, "等待中");
        stateZhMap.put(Thread.State.TIMED_WAITING, "計時等待");
        stateZhMap.put(Thread.State.BLOCKED, "阻塞中");
        stateZhMap.put(Thread.State.NEW, "新建");
        stateZhMap.put(Thread.State.TERMINATED, "已結束");
    }

    public ServerResourceDialog(JFrame owner) {
        super(owner, "伺服器資源監控", true);
        setLayout(new GridBagLayout());
        setSize(400, 700);
        setResizable(false);
        setLocationRelativeTo(owner);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // 基本資訊標籤
        add(new JLabel("JVM總記憶體(MB):"), gbc);
        gbc.gridy++;
        add(new JLabel("已使用記憶體(MB):"), gbc);
        gbc.gridy++;
        add(new JLabel("JVM最大記憶體(MB):"), gbc);
        gbc.gridy++;
        add(new JLabel("執行緒數:"), gbc);
        gbc.gridy++;
        add(new JLabel("線上玩家數:"), gbc);
        gbc.gridy++;
        add(new JLabel("系統CPU(%):"), gbc);
        gbc.gridy++;
        add(new JLabel("JVM進程CPU(%):"), gbc);
        gbc.gridy++;
        add(new JLabel("JVM記憶體佔整機(%):"), gbc);
        gbc.gridy++;
        add(new JLabel("GC次數:"), gbc);
        gbc.gridy++;
        add(new JLabel("GC總停頓(秒):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        lblTotalMemory = new JLabel();
        lblTotalMemory.setFont(new Font("Consolas", Font.BOLD, 14));
        lblTotalMemory.setForeground(new Color(230, 230, 230));
        add(lblTotalMemory, gbc);
        gbc.gridy++;
        lblUsedMemory = new JLabel();
        lblUsedMemory.setFont(new Font("Consolas", Font.BOLD, 14));
        lblUsedMemory.setForeground(new Color(230, 230, 230));
        add(lblUsedMemory, gbc);
        gbc.gridy++;
        lblMaxMemory = new JLabel();
        lblMaxMemory.setFont(new Font("Consolas", Font.BOLD, 14));
        lblMaxMemory.setForeground(new Color(230, 230, 230));
        add(lblMaxMemory, gbc);
        gbc.gridy++;
        lblThreadCount = new JLabel();
        lblThreadCount.setFont(new Font("Consolas", Font.BOLD, 14));
        lblThreadCount.setForeground(new Color(230, 230, 230));
        add(lblThreadCount, gbc);
        gbc.gridy++;
        lblOnlineCount = new JLabel();
        lblOnlineCount.setFont(new Font("Consolas", Font.BOLD, 14));
        lblOnlineCount.setForeground(new Color(230, 230, 230));
        add(lblOnlineCount, gbc);
        gbc.gridy++;
        lblCpu = new JLabel();
        lblCpu.setFont(new Font("Consolas", Font.BOLD, 14));
        lblCpu.setForeground(new Color(230, 230, 230));
        add(lblCpu, gbc);
        gbc.gridy++;
        lblProcCpu = new JLabel();
        lblProcCpu.setFont(new Font("Consolas", Font.BOLD, 14));
        lblProcCpu.setForeground(new Color(230, 230, 230));
        add(lblProcCpu, gbc);
        gbc.gridy++;
        lblProcMemPct = new JLabel();
        lblProcMemPct.setFont(new Font("Consolas", Font.BOLD, 14));
        lblProcMemPct.setForeground(new Color(230, 230, 230));
        add(lblProcMemPct, gbc);
        gbc.gridy++;
        lblGcCount = new JLabel();
        lblGcCount.setFont(new Font("Consolas", Font.BOLD, 14));
        lblGcCount.setForeground(new Color(230, 230, 230));
        add(lblGcCount, gbc);
        gbc.gridy++;
        lblGcTime = new JLabel();
        lblGcTime.setFont(new Font("Consolas", Font.BOLD, 14));
        lblGcTime.setForeground(new Color(230, 230, 230));
        add(lblGcTime, gbc);

        // 死鎖偵測
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        lblDeadlock = new JLabel("死鎖偵測：尚未檢查");
        lblDeadlock.setFont(new Font("Dialog", Font.BOLD, 12));
        lblDeadlock.setForeground(Color.BLUE);
        add(lblDeadlock, gbc);

        // ====== 圓餅圖（執行緒狀態分布）縮小版 ======
        DefaultPieDataset dataset = new DefaultPieDataset();
        JFreeChart chart = ChartFactory.createPieChart("執行緒緒狀態分布圖", dataset, false, true, false);
        piePanel = new ChartPanel(chart, false, false, false, false, false);

        int pieWidth = 320;
        int pieHeight = 200;

        piePanel.setPreferredSize(new Dimension(pieWidth, pieHeight));
        piePanel.setMaximumSize(new Dimension(pieWidth, pieHeight));
        piePanel.setMinimumSize(new Dimension(pieWidth, pieHeight));

// 2. 外層 Panel 也同樣設定
        JPanel pieWrapper = new JPanel();
        pieWrapper.setLayout(new FlowLayout(FlowLayout.CENTER));
        pieWrapper.setPreferredSize(new Dimension(pieWidth + 10, pieHeight + 10));
        pieWrapper.setMaximumSize(new Dimension(pieWidth + 10, pieHeight + 10));
        pieWrapper.setMinimumSize(new Dimension(pieWidth + 10, pieHeight + 10));
        pieWrapper.add(piePanel);

// 3. GridBagConstraint 不要給 weight
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        add(pieWrapper, gbc);

        // 恢復預設，避免影響下面的區塊
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;

        // 下方按鈕
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        btnRefresh = new JButton("刷新");
        btnGC = new JButton("GC觸發");
        btnThreadInfo = new JButton("顯示執行緒詳細"); // 新增這個按鈕
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnGC);
        buttonPanel.add(btnThreadInfo); // 加到下方

        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(18, 10, 14, 10);
        add(buttonPanel, gbc);

        // 事件
        btnRefresh.addActionListener(e -> updateData());
        btnGC.addActionListener(e -> {
            System.gc();
            JOptionPane.showMessageDialog(this, "已手動觸發GC！", "GC", JOptionPane.INFORMATION_MESSAGE);
            updateData();
        });
        btnThreadInfo.addActionListener(e -> showThreadInfoDialog()); // 顯示對話框

        autoRefreshTimer = new Timer(1000, e -> updateData());
        autoRefreshTimer.start();
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (autoRefreshTimer != null) autoRefreshTimer.stop();
            }
        });

        updateData();
    }

    /**
     * 彈窗顯示執行緒詳細
     */
    private void showThreadInfoDialog() {
        JTextArea area = new JTextArea(threadInfoString, 20, 50);
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setEditable(false);
        area.setLineWrap(false);
        JScrollPane scrollPane = new JScrollPane(area);
        JOptionPane.showMessageDialog(this, scrollPane, "執行緒詳細狀態", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 更新數據並刷新圓餅圖
     */
    private void updateData() {
        Runtime rt = Runtime.getRuntime();
        long total = rt.totalMemory() / 1024 / 1024;
        long used = (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024;
        long max = rt.maxMemory() / 1024 / 1024;
        lblTotalMemory.setText(String.valueOf(total));
        lblUsedMemory.setText(String.valueOf(used));
        lblMaxMemory.setText(String.valueOf(max));

        ThreadMXBean tmbean = ManagementFactory.getThreadMXBean();
        lblThreadCount.setText(String.valueOf(tmbean.getThreadCount()));
        int online = 0;
        try {
             online = World.get().getAllPlayers().size();
        } catch (Exception ignore) {}
        lblOnlineCount.setText(String.valueOf(online));
        try {
            OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            double sysCpuLoad = osBean.getSystemCpuLoad();
            if (sysCpuLoad < 0) sysCpuLoad = 0;
            lblCpu.setText(String.format("%.1f", sysCpuLoad * 100));

            double procCpuLoad = osBean.getProcessCpuLoad();
            if (procCpuLoad < 0) {
                lblProcCpu.setText("不支援");
            } else {
                lblProcCpu.setText(String.format("%.1f", procCpuLoad * 100));
            }

            // 計算 JVM 記憶體(估算：Heap使用 + Non-Heap使用 + Direct/Mapped Buffers) 佔整機比例
            long totalPhysical = 0;
            try {
                totalPhysical = (long) osBean.getTotalPhysicalMemorySize();
            } catch (Throwable ignore) {}

            if (totalPhysical > 0) {
                MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
                MemoryUsage nonHeap = memoryMXBean.getNonHeapMemoryUsage();
                long usedHeapBytes = rt.totalMemory() - rt.freeMemory();
                long usedNonHeapBytes = nonHeap.getUsed();
                long bufferBytes = 0;
                for (BufferPoolMXBean pool : ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class)) {
                    bufferBytes += pool.getMemoryUsed();
                }
                long jvmUsedBytes = usedHeapBytes + usedNonHeapBytes + bufferBytes;
                double pct = (jvmUsedBytes * 100.0) / totalPhysical;
                lblProcMemPct.setText(String.format("%.1f", pct));
            } else {
                lblProcMemPct.setText("不支援");
            }

            // 彙總 GC 次數與停頓時間(秒)
            long gcCount = 0;
            long gcTimeMs = 0;
            for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
                long c = gc.getCollectionCount();
                long t = gc.getCollectionTime();
                if (c > 0) gcCount += c;
                if (t > 0) gcTimeMs += t;
            }
            lblGcCount.setText(String.valueOf(gcCount));
            lblGcTime.setText(String.format("%.1f", gcTimeMs / 1000.0));
        } catch (Exception ex) {
            lblCpu.setText("不支援");
            lblProcCpu.setText("不支援");
            lblProcMemPct.setText("不支援");
            lblGcCount.setText("—");
            lblGcTime.setText("—");
        }

        // 死鎖偵測
        long[] deadlocked = tmbean.findDeadlockedThreads();
        if (deadlocked != null && deadlocked.length > 0) {
            lblDeadlock.setText("死鎖偵測：⚠️ 發現死鎖 (" + deadlocked.length + "個)！");
            lblDeadlock.setForeground(Color.RED);
        } else {
            lblDeadlock.setText("死鎖偵測：✅ 沒有死鎖");
            lblDeadlock.setForeground(new Color(0, 153, 51));
        }

        // 執行緒詳細狀態與圓餅圖分布
        ThreadInfo[] threadInfos = tmbean.dumpAllThreads(false, false);
        StringBuilder sb = new StringBuilder();
        EnumMap<Thread.State, Integer> stateMap = new EnumMap<>(Thread.State.class);

        // 統計各執行緒狀態
        for (ThreadInfo info : threadInfos) {
            sb.append(String.format("[%2d] %-32s  %-18s\n",
                    info.getThreadId(), info.getThreadName(), info.getThreadState()));
            stateMap.put(info.getThreadState(), stateMap.getOrDefault(info.getThreadState(), 0) + 1);
        }
        threadInfoString = sb.toString(); // 更新數據內容

        // 建立圓餅圖資料（顯示中文）
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Thread.State state : Thread.State.values()) {
            int count = stateMap.getOrDefault(state, 0);
            if (count > 0) {
                String zh = stateZhMap.getOrDefault(state, state.toString());
                dataset.setValue(zh + " (" + count + ")", count);
            }
        }
        JFreeChart chart = ChartFactory.createPieChart(
                "執行緒狀態分布圖", dataset, false, true, false
        );
        // ==== 【這段是重點！設置中文字型】====
        PiePlot plot = (PiePlot) chart.getPlot();
        // Pie圖標籤字型
        plot.setLabelFont(new Font("Microsoft JhengHei", Font.BOLD, 13));
        // 圖表標題字型
        chart.getTitle().setFont(new Font("Microsoft JhengHei", Font.BOLD, 15));
        piePanel.setChart(chart);
    }
}
