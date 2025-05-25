package com.lineage.config;

/**
 * 服務器限制設置
 *
 * @author dexc
 */
public final class ConfigWenyang {
    //	private static final Log _log = LogFactory.getLog(ConfigWenyang.class);
    //
    //
    //
    //	private static final String ALT_SETTINGS_FILE = "./config/紋樣設定.properties";
    //
    //
    //
    //	public static int JIFEN1;//紋樣積分1
    //
    //	public static int JIFEN2;//紋樣積分2
    //
    //	public static int JIFEN3;//紋樣積分3
    //
    //	public static int JIFEN4;//紋樣積分4
    //
    //	public static int JIFEN5;//紋樣積分5
    //
    //
    //
    //	public static int JILV1;//紋樣幾率1
    //
    //	public static int JILV2;//紋樣幾率1
    //
    //	public static int JILV3;//紋樣幾率1
    //
    //	public static int JILV4;//紋樣幾率1
    //
    //	public static int JILV5;//紋樣幾率1
    //
    //
    //
    //
    //	public static void load() throws ConfigErrorException {
    //		final PerformanceTimer timer = new PerformanceTimer();
    //		final Properties set = new Properties();
    //		try {
    //			InputStream is = new FileInputStream(new File(ALT_SETTINGS_FILE));
    //			InputStreamReader isr = new InputStreamReader(is, "utf-8");
    //			set.load(isr);
    //			is.close();
    //
    //
    //			JIFEN1 = Integer.parseInt(set.getProperty("JIFEN1", "2"));
    //
    //			JIFEN2 = Integer.parseInt(set.getProperty("JIFEN2", "2"));
    //
    //			JIFEN3 = Integer.parseInt(set.getProperty("JIFEN3", "2"));
    //
    //			JIFEN4 = Integer.parseInt(set.getProperty("JIFEN4", "2"));
    //
    //			JIFEN5 = Integer.parseInt(set.getProperty("JIFEN5", "2"));
    //
    //
    //
    //			JILV1 = Integer.parseInt(set.getProperty("JILV1", "2"));
    //
    //			JILV2 = Integer.parseInt(set.getProperty("JILV2", "2"));
    //
    //			JILV3 = Integer.parseInt(set.getProperty("JILV3", "2"));
    //
    //			JILV4 = Integer.parseInt(set.getProperty("JILV4", "2"));
    //
    //			JILV5 = Integer.parseInt(set.getProperty("JILV5", "2"));
    //
    //
    //
    //		} catch (Exception e) {
    //			throw new ConfigErrorException("設置檔案遺失: " + ALT_SETTINGS_FILE);
    //		} finally {
    //			set.clear();
    //			_log.info("Config/紋樣設定讀取完成 (" + timer.get() + "ms)");
    //		}
    //	}
    //
    //	public static int[] toIntArray(final String text, final String type) {
    //		StringTokenizer st = new StringTokenizer(text, type);
    //		int[] iReturn = new int[st.countTokens()];
    //		for (int i = 0; i < iReturn.length; i++) {
    //			iReturn[i] = Integer.parseInt(st.nextToken());
    //		}
    //		return iReturn;
    //	}
}
