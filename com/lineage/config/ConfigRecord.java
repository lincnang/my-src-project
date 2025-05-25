package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

/**
 * 服務器儲存設置
 *
 * @author dexc
 */
public final class ConfigRecord {
    private static final String RECORD_FILE = "./config/record.properties";
    public static boolean LOGGING_BAN_ENCHANT = false;
    public static boolean LOGGING_CHAT_NORMAL = false;
    public static boolean LOGGING_CHAT_SHOUT = false;
    public static boolean LOGGING_CHAT_WORLD = false;
    public static boolean LOGGING_CHAT_CLAN = false;
    public static boolean LOGGING_CHAT_WHISPER = false;
    public static boolean LOGGING_CHAT_PARTY = false;
    public static boolean LOGGING_CHAT_BUSINESS = false;
    public static boolean LOGGING_CHAT_COMBINED = false;
    public static boolean LOGGING_CHAT_CHAT_PARTY = false;
    public static boolean GM_OVERHEARD;
    public static boolean GM_OVERHEARD0;
    public static boolean GM_OVERHEARD2;
    public static boolean GM_OVERHEARD4;
    public static boolean GM_OVERHEARD11;
    public static boolean GM_OVERHEARD13;

    public static void load() throws ConfigErrorException {
        Properties set = new Properties();
        try {
            InputStream is = Files.newInputStream(new File(RECORD_FILE).toPath());
            set.load(is);
            is.close();
            LOGGING_BAN_ENCHANT = Boolean.parseBoolean(set.getProperty("LoggingBanEnchant", "false"));
            LOGGING_CHAT_NORMAL = Boolean.parseBoolean(set.getProperty("LoggingChatNormal", "false"));
            LOGGING_CHAT_SHOUT = Boolean.parseBoolean(set.getProperty("LoggingChatShout", "false"));
            LOGGING_CHAT_WORLD = Boolean.parseBoolean(set.getProperty("LoggingChatWorld", "false"));
            LOGGING_CHAT_CLAN = Boolean.parseBoolean(set.getProperty("LoggingChatClan", "false"));
            LOGGING_CHAT_WHISPER = Boolean.parseBoolean(set.getProperty("LoggingChatWhisper", "false"));
            LOGGING_CHAT_PARTY = Boolean.parseBoolean(set.getProperty("LoggingChatParty", "false"));
            LOGGING_CHAT_BUSINESS = Boolean.parseBoolean(set.getProperty("LoggingBusiness", "false"));
            LOGGING_CHAT_COMBINED = Boolean.parseBoolean(set.getProperty("LoggingChatCombined", "false"));
            LOGGING_CHAT_CHAT_PARTY = Boolean.parseBoolean(set.getProperty("LoggingChatChatParty", "false"));
            GM_OVERHEARD = Boolean.parseBoolean(set.getProperty("GM_OVERHEARD", "false"));
            GM_OVERHEARD0 = Boolean.parseBoolean(set.getProperty("GM_OVERHEARD0", "false"));
            GM_OVERHEARD2 = Boolean.parseBoolean(set.getProperty("GM_OVERHEARD2", "false"));
            GM_OVERHEARD4 = Boolean.parseBoolean(set.getProperty("GM_OVERHEARD4", "false"));
            GM_OVERHEARD11 = Boolean.parseBoolean(set.getProperty("GM_OVERHEARD11", "false"));
            GM_OVERHEARD13 = Boolean.parseBoolean(set.getProperty("GM_OVERHEARD13", "false"));
        } catch (Exception e) {
            throw new ConfigErrorException("設置檔案遺失: " + RECORD_FILE);
        } finally {
            set.clear();
        }
    }
    // 記錄時間格式(注意: 這裡不需重複new來作同步化, 因為是記錄同一天) by terry0412
	/*private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd");
	// 記錄文件檔 by terry0412
	public static final void recordToFiles(final String filename,
			final String info, final Timestamp timestamp) {
		// Java 1.7 新功能 by terry0412
		try (final BufferedWriter out = new BufferedWriter(new FileWriter(
				"./物品操作日誌/" + filename + "/" + sdf.format(timestamp) + ".txt",
				true))) {
			out.write(info);
			out.newLine();
			out.flush();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
	public static final void recordToFiles(final String filename,
			final String info) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter("./物品操作日誌/" + filename
					+ "/"
					+ sdf.format(new Timestamp(System.currentTimeMillis()))
					+ ".txt", true));
			out.write(info);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}*/
}
