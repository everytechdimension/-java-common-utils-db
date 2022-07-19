package com.everytechdimension.common.utils.db;

import com.everytechdimension.common.exception.AppApiException;
import com.everytechdimension.common.exception.DbException;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws AppApiException, DbException, SQLException {
        Package pack = Main.class.getPackage();
        System.out.println(pack.getImplementationTitle() + ":" + pack.getImplementationVersion() + " by " + pack.getImplementationVendor() + "(" + pack.getName() + ")");

        try (DBConnection conn = DBManager.getInstance().getConnection()) {
            try (ResultSet rs = conn.query("testing connection", "SELECT now() as time FROM SetBranch limit 1")) {
                while (rs.next()) {
                    System.out.println("time from sql: "+rs.getString("time"));
                }
            }
        }
    }
}
