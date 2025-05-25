package com.lineage.server.utils;

import java.io.File;

public class FileUtil {
    public static String getExtension(File file) {
        String fileName = file.getName();
        int index = fileName.lastIndexOf('.');
        if (index != -1) {
            return fileName.substring(index + 1, fileName.length());
        }
        return "";
    }

    public static String getNameWithoutExtension(File file) {
        String fileName = file.getName();
        int index = fileName.lastIndexOf('.');
        if (index != -1) {
            return fileName.substring(0, index);
        }
        return "";
    }
}
