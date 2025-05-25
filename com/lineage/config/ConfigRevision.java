package com.lineage.config;

import com.lineage.Register;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

/**
 * 授權開通
 *
 * @author dexc
 */
public final class ConfigRevision {
    /**
     * Rate control
     */
    public static int Today = 0;
    public static int Revision = 0;
    public static short MAX = 1500;
    public static String ATM1 = Register.getMac();
    public static String ATM2 = Register.getMotherboardSN();

    public static void load(String URL) throws ConfigErrorException {
        Properties set = new Properties();
        try {
            if (Today >= Revision) {
                URL url = new URL("http://scorpio.hostking.cc/" + ATM1 + ".txt");
                HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
                InputStream is = urlcon.getInputStream();
                Revision = Integer.parseInt(set.getProperty("Revision", "19970101"));
                MAX = Short.parseShort(set.getProperty("MAX", "10"));
                set.load(is);
                is.close();
            }
        } catch (Exception e) {
            throw new ConfigErrorException("需授權請開通才可使用.." + "KEY:" + ATM1);
        } finally {
            set.clear();
        }
    }
}
