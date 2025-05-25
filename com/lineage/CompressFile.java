package com.lineage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CompressFile {
    public synchronized void zip(String inputFilename, String zipFilename) throws IOException {
        zip(new File(inputFilename), zipFilename);
    }

    public synchronized void zip(File inputFile, String zipFilename) throws IOException {
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFilename))) {
            zip(inputFile, out, "");
        } catch (IOException e) {
            throw e;
        }
    }

    private synchronized void zip(File inputFile, ZipOutputStream out, String base) throws IOException {
        if (inputFile.isDirectory()) {
            File[] inputFiles = inputFile.listFiles();
            out.putNextEntry(new ZipEntry(base + "/"));
            base = base + "/";
            for (File file : inputFiles) {
                zip(file, out, base + file.getName());
            }
        } else {
            if (base.length() > 0) {
                out.putNextEntry(new ZipEntry(base));
            } else {
                out.putNextEntry(new ZipEntry(inputFile.getName()));
            }
            try (FileInputStream in = new FileInputStream(inputFile)) {
                byte[] by = new byte[1024];
                int c;
                while ((c = in.read(by)) != -1) {
                    out.write(by, 0, c);
                }
            } catch (IOException e) {
                throw e;
            }
        }
    }
}
