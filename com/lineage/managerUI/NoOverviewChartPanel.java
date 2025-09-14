package com.lineage.managerUI;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import java.awt.Graphics2D;

public class NoOverviewChartPanel extends ChartPanel {
    public NoOverviewChartPanel(JFreeChart chart, boolean properties, boolean save, boolean print, boolean zoom, boolean tooltips) {
        super(chart, properties, save, print, zoom, tooltips);
    }

    // 注意這裡不要加 @Override
    protected void drawZoomRectangle(Graphics2D g2, boolean xor) {
        // 什麼都不做
    }
}
