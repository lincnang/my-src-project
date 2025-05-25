package com.lineage;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class Register {
    /**
     * 獲取主板序列號
     *
     */
    public static String getMotherboardSN() {
        String result = "";
        try {
            File file = File.createTempFile("realhowto", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);
            String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n" + "Set colItems = objWMIService.ExecQuery _ \n" + "   (\"Select * from Win32_BaseBoard\") \n" + "For Each objItem in colItems \n" + "    Wscript.Echo objItem.SerialNumber \n" + "    exit for  ' do the first cpu only! \n" + "Next \n";
            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.trim();
    }

    /**
     * 獲取硬盤序列號
     *
     * @param drive 盤符
     */
    public static String getHardDiskSN(String drive) {
        String result = "";
        try {
            File file = File.createTempFile("realhowto", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);
            String vbs = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\n" + "Set colDrives = objFSO.Drives\n" + "Set objDrive = colDrives.item(\"" + drive + "\")\n" + "Wscript.Echo objDrive.SerialNumber"; // see note
            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.trim();
    }

    /**
     * 獲取CPU序列號
     *
     */
    public static String getCPUSerial() {
        String result = "";
        try {
            File file = File.createTempFile("tmp", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);
            String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n" + "Set colItems = objWMIService.ExecQuery _ \n" + "   (\"Select * from Win32_Processor\") \n" + "For Each objItem in colItems \n" + "    Wscript.Echo objItem.ProcessorId \n" + "    exit for  ' do the first cpu only! \n" + "Next \n";
            // + "    exit for  \r\n" + "Next";
            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
            file.delete();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        if (result.trim().length() < 1 || result == null) {
            result = "錯誤。";
        }
        return result.trim();
    }

    /**
     * 獲取MAC地址
     */
    public static String getMac() {
        String result = "";
        try {
            Process process = Runtime.getRuntime().exec("ipconfig /all");
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line;
            while ((line = input.readLine()) != null) {
                if (line.indexOf("Physical Address") > 0) {
                    String MACAddr = line.substring(line.indexOf("-") - 2);
                    result = MACAddr;
                }
            }
        } catch (java.io.IOException e) {
            System.err.println("IOException " + e.getMessage());
        }
        return result;
    }

    public static String getIp() {
        Enumeration<NetworkInterface> netInterfaces = null;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                if (ips.hasMoreElements()) {
                    return ((InetAddress) ips.nextElement()).getHostAddress();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "NoIP";
    }

    public static void main(String[] args) {
        System.out.println("CPU  SN:" + Register.getCPUSerial());
        System.out.println("主板  SN:" + Register.getMotherboardSN());
        System.out.println("C盤   SN:" + Register.getHardDiskSN("c"));
        System.out.println("MAC  SN:" + Register.getMac());
        System.out.println("Ip  SN:" + Register.getIp());
    }
}