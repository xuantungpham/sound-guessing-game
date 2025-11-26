package database;

import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        Connection conn = DatabaseConnector.getConnection();
        if (conn != null) {
            System.out.println("Kết nối Database thành công!");
        } else {
            System.out.println("Kết nối Database thất bại.");
        }
    }
}

