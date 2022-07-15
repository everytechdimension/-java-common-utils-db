package com.everytechdimension.common.utils.db;

import com.everytechdimension.common.exception.DbException;
import com.everytechdimension.common.utils.Logging;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DBConnection {
    private final ReturnConnection rc;
    private boolean isDebuging;
    private Connection connection;

    public DBConnection(Connection conn) {
        this(conn, null);
    }

    public DBConnection(Connection conn, ReturnConnection rc) {
        this(conn, rc, false);
    }

    public DBConnection(Connection conn, ReturnConnection rc, boolean isDebuging) {
        this.connection = conn;
        this.rc = rc;
        this.isDebuging = isDebuging;
    }

    public void setDebuging(boolean debuging) {
        isDebuging = debuging;
    }

    public boolean getAutoCommit() throws DbException {
        try {
            return connection.getAutoCommit();
        } catch (SQLException e) {
            throw new DbException("Transaction", "getStatus", e);
        }
    }

    public void startTransaction() throws DbException {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new DbException("Transaction", "Starting", e);
        }
    }

    public void commit() throws DbException {
        try {
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new DbException("Transaction", "Committing", e);
        }
    }

    public void rollback() throws DbException {
        try {
            if (!connection.getAutoCommit()) {
                connection.rollback();
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DbException("Transaction", "RollingBack", e);
        }
    }

    public PreparedStatement prepareStatement(String sql) throws DbException {
        return prepareStatement("", sql);
    }

    public PreparedStatement prepareStatement(String label, String sql) throws DbException {
        try {
            markIfDebug(label, sql, new HashMap<Integer, Object>());
            return connection.prepareStatement(sql);
        } catch (SQLException e) {
            throw new DbException(label, "add", e);
        }
    }

    public int insert(String label, String sql, HashMap<Integer, Object> mapParams) throws DbException {
        checkConnection(label, "add");

        try {
            markIfDebug(label, sql, mapParams);

            PreparedStatement ps = connection.prepareStatement(sql);
            for (Map.Entry<Integer, Object> entry : mapParams.entrySet())
                ps.setObject(entry.getKey(), entry.getValue());
            int updated = ps.executeUpdate();
            if (updated == 0)
                throw new DbException(label, "add", sql);

            return updated;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DbException(label, "add", e);
        }
    }

    public int update(String label, String sql, HashMap<Integer, Object> mapParams) throws DbException {
        checkConnection(label, "edit");

        try {
            markIfDebug(label, sql, mapParams);

            PreparedStatement ps = connection.prepareStatement(sql);
            for (Map.Entry<Integer, Object> entry : mapParams.entrySet())
                ps.setObject(entry.getKey(), entry.getValue());

            return ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("_______________________________________________________");
            System.out.println("SQL: " + sql);
            System.out.println("map: ");
            for (Map.Entry<Integer, Object> s : mapParams.entrySet())
                System.out.println(s.getKey() + ": " + s.getValue());
            System.out.println("---------------------------------------------------");
            e.printStackTrace();
            throw new DbException(label, "edit", e);
        }
    }

    public boolean execute(String label, String sql) throws DbException {
        return execute(label, sql, new HashMap<Integer, Object>());
    }

    public boolean execute(String label, String sql, HashMap<Integer, Object> mapParams) throws DbException {
        checkConnection(label, "execute");

        try {
            markIfDebug(label, sql, mapParams);

            PreparedStatement ps = connection.prepareStatement(sql);
            for (Map.Entry<Integer, Object> entry : mapParams.entrySet())
                ps.setObject(entry.getKey(), entry.getValue());

            return ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DbException(label, "execute", e);
        }
    }

    public int delete(String label, String sql, HashMap<Integer, Object> mapParams) throws DbException {
        checkConnection(label, "remove");

        try {
            markIfDebug(label, sql, mapParams);

            PreparedStatement ps = connection.prepareStatement(sql);
            for (Map.Entry<Integer, Object> entry : mapParams.entrySet())
                ps.setObject(entry.getKey(), entry.getValue());

            int updated = ps.executeUpdate();
            if (updated == 0)
                throw new DbException(label, "remove", sql);

            return updated;
        } catch (SQLException e) {
            throw new DbException(label, "remove", e);
        }
    }

    public int deleteAndIgnoreCount(String label, String sql, HashMap<Integer, Object> mapParams) throws DbException {
        checkConnection(label, "remove");

        try {
            markIfDebug(label, sql, mapParams);

            PreparedStatement ps = connection.prepareStatement(sql);
            for (Map.Entry<Integer, Object> entry : mapParams.entrySet())
                ps.setObject(entry.getKey(), entry.getValue());

            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(label, "remove", e);
        }
    }

    public ResultSet query(String label, String sql) throws DbException {
        return query(label, sql, new HashMap<Integer, Object>());
    }

    public ResultSet query(String label, String sql, HashMap<Integer, Object> mapParams) throws DbException {
        checkConnection(label, "view");

        try {
            markIfDebug(label, sql, mapParams);

            PreparedStatement ps = connection.prepareStatement(sql);
            for (Map.Entry<Integer, Object> entry : mapParams.entrySet())
                ps.setObject(entry.getKey(), entry.getValue());

            if (!ps.execute())
                throw new DbException(label, "view-can't-run", sql);

            ResultSet rs = ps.getResultSet();
            if (rs == null)
                throw new DbException(label, "view", sql);

            return rs;
        } catch (SQLException e) {
            throw new DbException(label, "view", e);
        }
    }

    public void close() {
        try {
            if (!getAutoCommit())
                connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DbException e) {
            e.printStackTrace();
        }

        rc.returnConnection(connection);
        connection = null;
    }

    void checkConnection(String label, String type) throws DbException {
        if (connection == null)
            throw new DbException(label, type, new Exception("connection is closed. please contact support."));
    }

    private void markIfDebug(String label, String sql, HashMap<Integer, Object> mapParams) {
        if (isDebuging) {
            StringBuilder sb = new StringBuilder();
            sb.append("----------------------------------\n");
            sb.append(label + ": " + sql + "\n");
            for (Map.Entry<Integer, Object> entry : mapParams.entrySet())
                sb.append("key: " + entry.getKey() + ", value: " + entry.getValue() + "\n");
            sb.append("----------------------------------\n");
            Logging.debug.println(sb.toString());
        }
    }

    interface ReturnConnection {
        void returnConnection(Connection connection);
    }
}
