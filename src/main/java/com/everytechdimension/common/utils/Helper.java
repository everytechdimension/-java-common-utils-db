package com.everytechdimension.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Helper {

    public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.00");
    public final static DecimalFormat DECIMAL_FORMAT_PER = new DecimalFormat("#0.0000");
    public final static SimpleDateFormat DATETIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public final static SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    public final static SimpleDateFormat TIME_FORMATER = new SimpleDateFormat("HH:mm:ss");
    private final static Calendar sTempCalender = Calendar.getInstance();

    static {
//        TIME_FORMATER.setTimeZone(TimeZone.getTimeZone("UTC"));
        isoDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private Helper() {
    }

    public static Calendar getToday() {
        return Calendar.getInstance();
    }

    public static String getToday(String format) {
        return formatDate(new Date(), format);
    }

    public static String formatDate(Date d, String format) {
        try {
            return (new SimpleDateFormat(format)).format(d);
        } catch (Exception e) {
            return "";
        }
    }

    public static String readFile(String fileAddress) {
        return readFile(fileAddress, "");
    }

    public static String readFile(String fileAddress, String defaultStr) {
        Scanner scanner = null;
        try {
            File f = new File(fileAddress);
            if (!f.exists()) {
                Logging.out.println("file not found: " + f.getAbsolutePath());
                return defaultStr;
            }
            scanner = new Scanner(new FileInputStream(f));
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();

            return hasInput ? scanner.next() : defaultStr;
        } catch (Exception e) {
            Logging.err.printError(e);
            return defaultStr;
        } finally {
            if (scanner != null)
                scanner.close();
        }
    }

    public static void writeFile(String fileAddress, String data) throws IOException {
        FileOutputStream f = new FileOutputStream(fileAddress);
        f.write(data.getBytes());
        f.flush();
        f.close();
    }

    public static String checkDate(String dateStr) {
        try {
            DATE_FORMAT.parse(dateStr);
            return dateStr;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getTimeStr(int timeInSec) {
        return getTimeStr(timeInSec * 1000L);
    }

    public static String getTimeStr(Long timeInSec) {
        sTempCalender.setTimeInMillis(timeInSec);
        return TIME_FORMATER.format(sTempCalender.getTime());
    }

    public static String getPackageDetails(Class classObj) {
        Package pack = classObj.getPackage();
        return pack.getImplementationTitle() + ":" + pack.getImplementationVersion() + "(" + pack.getName() + ")";
    }

    public static String getJarVersion(Class classObj) {
        return classObj.getPackage().getImplementationVersion();
    }

    public static String getUniqueId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
