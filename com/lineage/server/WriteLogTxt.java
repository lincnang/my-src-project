package com.lineage.server;
//import java.io.BufferedWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//import java.io.FileWriter;
public class WriteLogTxt {
    private static final Log _log = LogFactory.getLog(WriteLogTxt.class);

    public static void Recording(final String name, final String info) {
        try {
            SimpleDateFormat sdfmt = new SimpleDateFormat("yyyy-MM-dd");
            Date d = Calendar.getInstance().getTime();
            String date = " " + sdfmt.format(d);
            final String path = "物品操作記錄/Log" + date;
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }
            String filePath = name + date + ".txt";
            File file2 = new File(path, filePath);
            if (!file2.exists()) {
                file2.createNewFile();
            }
            final FileOutputStream fos = new FileOutputStream(path + "/" + filePath, true);
            fos.write((info + " 時間：" + new Timestamp(System.currentTimeMillis()) + "\r\n").getBytes());
            fos.close();
            /*
             * BufferedWriter out = new BufferedWriter(new FileWriter(
             * "AllLog/"+name+date+".txt", true)); out.write(info+" 時間："+ new
             * Timestamp(System.currentTimeMillis()) + "\r\n"); out.close();
             */
        } catch (IOException e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
