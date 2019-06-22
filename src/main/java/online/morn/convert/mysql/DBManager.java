package online.morn.convert.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBManager {

    private static String driver = "com.mysql.jdbc.Driver";
    private static String url = "jdbc:mysql://127.0.0.1:3306/timing_tasks?useUnicode=true&amp;characterEncoding=utf-8";
    private static String user = "root";
    private static String password = "root";

    static{
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得连接
     * @return java.sql.Connection
     */
    public static Connection getConn(){
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 关闭连接
     * @param conn java.sql.Connection
     * @param stmt ava.sql.Statement
     * @param rs ava.sql.ResultSet
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs){
        try {
            if(rs != null){
                rs.close();
            }
            if(stmt != null){
                stmt.close();
            }
            if(conn != null){
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
