package main.dao;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {

    private static Connection connection;

    private static String dbName = "search_engine";
    private static String dbUser = "sa";
    private static String dbPass = "";

    private static int capacity = 3_000_000; // Packet for query 4_194_303
    private static StringBuilder stringBuilder = new StringBuilder(capacity);

    public static StringBuilder getStringBuilder() {
        return stringBuilder;
    }

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/" + dbName +
                        "?user=" + dbUser + "&password=" + dbPass);
                connection.createStatement().execute("DROP TABLE IF EXISTS page");
                connection.createStatement().execute("CREATE TABLE page(" +
                    "id INT NOT NULL AUTO_INCREMENT, " +
                    "path TEXT NOT NULL, " +
                    "code INT NOT NULL, " +
                    "content MEDIUMTEXT NOT NULL, " +
                    "PRIMARY KEY(id), KEY(path(50)))");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    @SneakyThrows
    public static void executeMultiInsert() {
        String sql = "INSERT INTO page(path, code, content) " +
                "VALUES" + stringBuilder.toString();
        DBConnection.getConnection().createStatement().execute(sql);
    }

    @SneakyThrows
    public static void addLink(String path, int code, String content) {
        stringBuilder.append((stringBuilder.length() == 0 ? "" : ",") +
                "('" + path + "', '" + code + "', '" + content + "')");
        if (stringBuilder.length() > capacity){
            DBConnection.executeMultiInsert();
            stringBuilder = new StringBuilder(capacity);
        }
    }

    @SneakyThrows
    public static boolean findLine(String path) {
        String sql = "SELECT * FROM page WHERE path='" + path + "'";
        ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(sql);
        return rs.next();
    }
}
