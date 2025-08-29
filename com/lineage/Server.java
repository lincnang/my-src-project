package com.lineage;

import com.lineage.commons.system.LanSecurityManager;
import com.lineage.config.*;
import com.lineage.server.GameServer;
import com.lineage.server.utils.DBClearAllUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.logging.LogManager;

public class Server {
    private static final String LOG_PROP = "./config/logging.properties";
    private static final String LOG_4J = "./config/log4j.properties";
    private static final String _loginfo = "./loginfo";
    private static final String _back = "./back";
    private static final String _licence = "Copyright (C) 2025 by 【老爹】技術\n";
    //private static final String _licence = "Copyright (C) 2008-2016 by 7.0\n";
    //private static final String _jseverip = "220.134.189.191";//src023
    //private static final String _jsevermac = "E0-3F-49-A4-CF-48";  //src023
    public static int Today = 0;
    public static int TodayX = 0;
    public static int year = 0;
    public static int month = 0;
    public static int day = 0;
    public static int Ryear = 0;
    public static int Rmonth = 0;
    public static int Rday = 0;
    public static int dayX = 0;
    public static int dayXX = 0;
    public static short MAX = 1500;
    public static int ATM = Ver_Key.Key;

    /**
     * MAIN
     *
     */
    public static void main(final String[] args) throws Exception {
		/*InetAddress ip;
		try {
			ip = InetAddress.getLocalHost();
			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
			byte[] mac = network.getHardwareAddress();
			StringBuilder sb = new StringBuilder();
			for (int i=0; i<mac.length; i++){
				sb.append(String.format("%02X%s", mac[i], (i<mac.length-1) ?"-" :""));//轉成16進位
			}
		//	System.out.println("你的實體ip:"+ip.getHostAddress());
		//	System.out.println("你的實體mac:");
		//	System.out.println(sb.toString());
			/*if ((!ip.getHostAddress().equals(_jseverip))||(!sb.toString().equals(_jsevermac))) {
				// 沒有任何事情發生
				System.out.println("\n\r       版權擁有，您未授權使用。");
				TimeUnit.MILLISECONDS.sleep(1 * 1000);// 延遲
				System.exit(0);
			}*/
		/*} catch (Exception localException1) {
		}*/
        Calendar date = Calendar.getInstance();
        // 1=周日 7=周六
        System.out.println(">>>>" + date.get(Calendar.DAY_OF_WEEK));
        System.out.println(">>>>" + date.get(Calendar.HOUR_OF_DAY));
			/*Ver_Key.load();
			// 以下為核心驗證
			System.out.println("\n\r\r       註冊碼 : " + "" + Ver_Key.Key + "");
			System.out.println("\n\r       此核心與[兩津]測試中........");//承租
			System.out.println("\n\r       若違反其他條例立即終止版本.... ");
			System.out.println("\n\r       請稍候，正在努力驗證載入中.....");
			TimeUnit.MILLISECONDS.sleep(3 * 1000);// 延遲
			getIP("http://list.onegood.me/wed_815/" + Ver_Key.Key + ".txt");
			getIP2("http://list.onegood.me/wed_381/test_381/Time.php");
			getIP3("http://list.onegood.me/wed_381/test_381/Time.txt");
			dayX += (year-Ryear)*365;
			dayX += (month-Rmonth)*31;
			dayX += (day-Rday);
			System.out.println("\n\r       今日日期 " + year + " 年 " + month + " 月 "+ day +" 日");
			System.out.println("\n\r       到期日期 " + Ryear + " 年 " + Rmonth + " 月 "+ Rday +" 日");
			if (Today <= TodayX) {
			System.out.println("\n\r       剩餘天數 " + Math.abs(dayX));
			} else {
			System.out.println("\n\r       剩餘天數 0");
			}
			//System.out.println("\n\r       允取玩家 " + MAX);
			if (Today <= TodayX) {
				System.out.println("\n\r       成功開啟 !");
				TimeUnit.MILLISECONDS.sleep(1 * 1000);// 延遲
			} else {
				// 沒有任何事情發生
				System.out.println("\n\r       已到期請延長使用期限.....");
				TimeUnit.MILLISECONDS.sleep(1 * 1000);// 延遲
				System.exit(0);
			}*/
        // 測試用核心
        try {
            // 在核心啟動命令後面加上 test 可以用來作顯示測試
            if (args[0].equalsIgnoreCase("test")) {
                Config.DEBUG = true;
            }
        } catch (final Exception e) {
            //e.printStackTrace();
        }
        //轉向改寫 20150312 Shinogame 綁定主機
			/*StringBuilder sbr = com.lineage.echo.HardwareAddress.getLocalAddress();
			//System.out.println("你的MAC" + sbr.toString());
			//機房MAC 綁定網卡
			if (sbr != null && !"00-15-5D-01-65-0A".equalsIgnoreCase(sbr.toString())) {
			System.exit(0);
			}*/
        // 壓縮舊檔案
        final CompressFile bean = new CompressFile();
        try {
            // 建立備份用資料夾
            final File file = new File(_back);
            if (!file.exists()) {
                file.mkdir();
            }
            final String nowDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
            bean.zip(_loginfo, "./back/" + nowDate + ".zip");
            final File loginfofile = new File(_loginfo);
            final String[] loginfofileList = loginfofile.list();
            for (final String fileName : loginfofileList) {
                final File readfile = new File(_loginfo + "/" + fileName);
                if (readfile.exists() && !readfile.isDirectory()) {
                    readfile.delete();
                }
            }
        } catch (final IOException e) {
            System.out.println("資料夾不存在: " + _back + " 已經自動建立!");
        }
        boolean error = false;
        try {
            final InputStream is = new BufferedInputStream(Files.newInputStream(Paths.get(LOG_PROP)));
            LogManager.getLogManager().readConfiguration(is);
            is.close();
        } catch (final IOException e) {
            System.out.println("檔案遺失: " + LOG_PROP);
            error = true;
        }
        try {
            PropertyConfigurator.configure(LOG_4J);
        } catch (final Exception e) {
            System.out.println("檔案遺失: " + LOG_4J);
            System.exit(0);
        }
        try {
            Config.load();
            ConfigAlt.load();
            ConfigCharSetting.load();
            ConfigOther.load();
            ConfigRate.load();
            ConfigSQL.load();
            ConfigRecord.load();
            //ConfigRevision.load(null);// 授權開通 //src004
            ConfigDescs.load();
            Configpoly.load();
            ConfigHoly.load();//聖物
            ConfigBad.load();
            ConfigKill.load();
            ConfigIpCheck.load();
            ConfigBoxMsg.load();
            ConfigMobKill.load();//src014
            ConfigQuest.load();//src035
            ConfigGiveVip.load();//潘朵拉商城消费给予VIP状态
            ConfigAutoAll.load();//自動輔助設置
            ConfigOtherSet2.load();
            ConfigTurn.load();
            Configcamp_war.load();
            ConfigFreeKill.load();
            ConfigThebes.load();
            ConfigWeaponryEffects.load();
            ConfigWeaponryHurt.load();
            ConfigWeaponArmor.load();
            ConfigClan.load();
            ConfigSay.load();
            ConfigDropBox.load();
            ConfigPRO.load();
            ConfigLIN.load();
            ConfigSkillWizard.load();
            ConfigSkillIllusion.load();
            ConfigSkillDarkElf.load();
            ConfigSkillDragon.load();
            ConfigSkillElf.load();
            ConfigSkillKnight.load();
            ConfigSkillWarrior.load();
            ConfigSkillCrown.load();
            Config_Occupational_Damage.load();
            ThreadPoolSetNew.load();
            ThreadPoolSet.load();
            CherOpen.load();//讀取職業開放
            ConfigDoll.load();
            ConfigMagic.load();
        } catch (final Exception e) {
            System.out.println("CONFIG 資料加載異常!" + e);
            error = true;
        }
        System.out.println(_licence + "\n");
        final Log log = LogFactory.getLog(Server.class);
        log.info(" 《現在時間" + new SimpleDateFormat("yyyy年MM月dd日HH點mm分").format(new Date()) + " 星期" + (date.get(Calendar.DAY_OF_WEEK) - 1) + "》");
        final String infoX = "\n\r#####################################################" + "\n\r       服務器 [核心版本:" + "伊薇8.15" + "/" + "天堂8.15C" + "]" + "\n\r#####################################################";
        log.info(infoX);
        final File file = new File("./lib");
        final String[] fileNameList = file.list();
        for (final String fileName : fileNameList) {
            final File readfile = new File(fileName);
            if (!readfile.isDirectory()) {
                log.info("加載引用JAR: " + fileName);
            }
        }
        if (error) {
            System.exit(0);
        }
        //log.info("訊息辨識(色彩涵義): [INFO]資訊");
        //log.debug("訊息辨識(色彩涵義): [DEBUG]除錯");
        //log.warn("訊息辨識(色彩涵義): [WARN]警告");
        //log.error("訊息辨識(色彩涵義): [ERROR]錯誤");
        //log.fatal("訊息辨識(色彩涵義): [FATAL]嚴重錯誤");
        // SQL讀取初始化
        DatabaseFactoryLogin.setDatabaseSettings();
        DatabaseFactory.setDatabaseSettings();
        DatabaseFactoryLogin.get();
        DatabaseFactory.get();
        Config.loadDB();
        if (Config.DBClearAll) {//是否開啟絕對還原設定(開新服專用)
            DBClearAllUtil.start();
        }
        // 安全管理器
        final LanSecurityManager securityManager = new LanSecurityManager();
        System.setSecurityManager(securityManager);
        log.info("加載 安全管理器: LanSecurityManager");
        final String osname = System.getProperties().getProperty("os.name");
        if (osname.lastIndexOf("Linux") != -1) {
            Config.ISUBUNTU = true;
        }
        GameServer.getInstance().initialize();
        // 啟動數據庫連接監控
        try {
            com.lineage.server.database.DatabaseMonitor.getInstance().startMonitoring();
            log.info("數據庫連接監控已啟動");
        } catch (Exception e) {
            log.error("啟動數據庫連接監控失敗，但服務器將繼續啟動: " + e.getMessage());
            // 不拋出異常，讓服務器繼續啟動
        }
        
        // 啟動斷線診斷監控
        try {
            com.lineage.server.utils.DisconnectionDiagnostics.getInstance().startMonitoring();
            log.info("斷線診斷監控已啟動");
        } catch (Exception e) {
            log.error("啟動斷線診斷監控失敗: " + e.getMessage());
        }
        
        // 啟動玩家活動監控
        try {
            com.lineage.server.utils.ActivityMonitor.getInstance();
            log.info("玩家活動監控已啟動");
        } catch (Exception e) {
            log.error("啟動玩家活動監控失敗: " + e.getMessage());
        }
    }           

    /**
     * 加載全部config配置
     */
    public static void StartConfig() {
        try {
            Config.load();
            ConfigAlt.load();
            ConfigCharSetting.load();
            ConfigOther.load();
            ConfigRate.load();
            ConfigSQL.load();
            ConfigRecord.load();
            ConfigDescs.load();
            ConfigBad.load();
            ConfigKill.load();
            ConfigIpCheck.load();
            ConfigBoxMsg.load();
            //----------------------
            ConfigOtherSet2.load();
            ConfigDropBox.load();
            //			ConfigMagic_Arrow.load();
            ConfigTurn.load();
            //			ConfigPlug_in.load();
            //			Config_Magic_weapon_effects.load();
            //			ConfigMagic_Special.load();
            //			Config_Occupational_injury.load();
            ConfigFreeKill.load();
            ConfigDoll.load();
            ConfigMagic.load();
            //			ConfigSoul.load();
            ThreadPoolSet.load();
            ThreadPoolSetNew.load();
            //			ConfigSkill.load();
            //			Config_Reply.load();
            ConfigGuardTower.load();//防衛塔
        } catch (Exception e) {
            System.out.println("CONFIG 資料加載異常!" + e);
        }
    }

    // 以下為核心驗證
    @SuppressWarnings("unused")
    private static void getIP(String strUrl) throws IOException {
        URL url = new URL(strUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        InputStream input = new DataInputStream(conn.getInputStream());
        Properties pack = new Properties();
        pack.load(input);
        input.close();
        TodayX = Integer.parseInt(pack.getProperty("TodayX", "21000101"));
        Ryear = Integer.parseInt(pack.getProperty("Ryear", "2016"));
        Rmonth = Integer.parseInt(pack.getProperty("Rmonth", "01"));
        Rday = Integer.parseInt(pack.getProperty("Rday", "01"));
        MAX = Short.parseShort(pack.getProperty("MAX", "10"));
        pack.clear();
    }

    // 以下為核心驗證
    @SuppressWarnings("unused")
    private static void getIP2(String strUrl) throws IOException {
        URL url = new URL(strUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        InputStream input = new DataInputStream(conn.getInputStream());
        Properties pack = new Properties();
        pack.load(input);
        input.close();
        pack.clear();
    }

    // 以下為核心驗證
    @SuppressWarnings("unused")
    private static void getIP3(String strUrl) throws IOException {
        URL url = new URL(strUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        InputStream input = new DataInputStream(conn.getInputStream());
        Properties pack = new Properties();
        pack.load(input);
        input.close();
        Today = Integer.parseInt(pack.getProperty("Today", "21000101"));
        year = Integer.parseInt(pack.getProperty("year", "2016"));
        month = Integer.parseInt(pack.getProperty("month", "01"));
        day = Integer.parseInt(pack.getProperty("day", "01"));
        pack.clear();
    }
}
