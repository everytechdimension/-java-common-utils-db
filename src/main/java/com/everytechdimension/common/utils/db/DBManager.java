package com.everytechdimension.common.utils.db;

import com.everytechdimension.common.exception.AppApiException;
import com.everytechdimension.common.exception.DbException;
import com.everytechdimension.common.utils.Helper;
import com.everytechdimension.common.utils.Logging;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.json.JSONObject;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class DBManager implements DBConnection.ReturnConnection {
    private static DBManager sDBManager;
    private final HikariDataSource ds;

    public DBManager(String type, String host, int port, String databaseName, String username, String password) {
        this("jdbc:" + type + "://" + host + ":" + port + databaseName, username, password);
    }

    public DBManager(String type, String host, String databaseName, String username, String password) {
        this(type, host, 3306, databaseName, username, password);
    }

    public DBManager(String url, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setConnectionTimeout(5 * 1000);
        config.setIdleTimeout(5 * 60 * 1000);
        config.setMaximumPoolSize(3);
        config.setMinimumIdle(1);
        ds = new HikariDataSource(config);
    }

    private static boolean isFileExist(String filename) {
        File file = new File(filename);
        System.out.println("Finding File: " + file.getAbsolutePath());
        return file.exists();
    }

    private static void loadConnection() {
        try {
            String driver = "mysql";
            String ip = "127.0.0.1";
            Integer port = 3306;
            String dbname = "/BranchQMS";
            String username = "root";
            String password = "root123";
            String dbDriverClass = "com.mysql.jdbc.Driver";
            try {
                String filePath = "db-config.json";
                if (!isFileExist(filePath)) {
                    filePath = "/home/iqms/tomcat/webapps/db-config.json";
                    if (!isFileExist(filePath)) {
                        filePath = "C:/tomcat/webapps/db-config.json";
                    }
                }
                Logging.err.println("filePath");
                String db = Helper.readFile(filePath, "{}");
                Logging.err.println("filePath");
                JSONObject json = new JSONObject(db);
                if (json.has("driver"))
                    driver = json.getString("driver");
                if (json.has("ip"))
                    ip = json.getString("ip");
                if (json.has("port"))
                    port = json.getInt("port");
                if (json.has("dbname"))
                    dbname = json.getString("dbname");
                if (json.has("username"))
                    username = json.getString("username");
                if (json.has("password"))
                    password = json.getString("password");
                if (json.has("dbDriverClass"))
                    dbDriverClass = json.getString("dbDriverClass");
                Logging.err.println(db);
            } catch (Exception e) {
                Logging.err.printError(e);
                e.printStackTrace();
            }
            Logging.err.println("dbDriverClass: " + dbDriverClass);
            Class.forName(dbDriverClass);
            sDBManager = new DBManager(driver, ip, port, dbname, username, password);
        } catch (ClassNotFoundException e) {
            Logging.err.printError(e);
        }
    }

    public static DBManager getInstance() throws AppApiException {
        try {
            if (sDBManager == null)
                loadConnection();
            return sDBManager;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AppApiException(500, 505, "Could not connect to the database.", e);
        }
    }

    public DBConnection getConnection() throws DbException {
        try {
            if (ds == null) {
                Logging.out.println("Hikari instance null");
            }
            return new DBConnection(ds.getConnection(), this, false);
        } catch (SQLException e) {
            throw new DbException("connection", "taking", e);
        }
    }

    @Override
    public void returnConnection(Connection connection) {
        ds.evictConnection(connection);
    }
}