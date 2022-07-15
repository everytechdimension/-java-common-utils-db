package com.everytechdimension.common.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logging {
    private final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final static DateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final static String dateTime = dateFormat.format(new Date());

    private final static String basePath = File.separator.equals("\\") ? "logs\\java-app\\" : "logs/java-app/";
    public final static Logging out = new Logging(basePath + "out-" + dateTime + ".log");
    public final static Logging err = new Logging(basePath + "err-" + dateTime + ".log");
    public final static Logging debug = new Logging(basePath + "debug-" + dateTime + ".log");
    public final static Logging api = new Logging(basePath + "api-" + dateTime + ".log");

    static {
        File folder = new File(basePath);
        if (!folder.exists())
            folder.mkdirs();
    }

    private final String fileName;

    public Logging(String fileName) {
        this.fileName = fileName;
    }

    public void println(String str) {
        print(datetimeFormat.format(new Date())+ " "+ str + "\n");
    }

    public void printError(Exception err) {
        try {
            err.printStackTrace();
            FileWriter fw = new FileWriter(fileName, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(datetimeFormat.format(new Date())+ " --------");
            err.printStackTrace(pw);
            pw.flush();
            fw.flush();
            fw.close();
        } catch (IOException e) {
            System.out.println("exception occurred" + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void print(String str) {
        System.out.print(str);
        try {
            FileWriter fw = new FileWriter(fileName, true);
            fw.append(str);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            System.out.println("exception occurred" + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}
