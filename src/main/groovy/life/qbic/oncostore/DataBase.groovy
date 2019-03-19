package life.qbic.oncostore

// @TODO might need to be replaced by external lib (singleton), for testing purposes we will stick now to this definition
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DataBase {

    private String url
    private String userName
    private String password
    private String driverClass
    private Connection connection

    public DataBase(String url, String userName, String password, String driverClass) {
        this.url = url
        this.userName = userName
        this.password = password
        this.driverClass = driverClass
        login()
    }

    Connection getConnection() {
        return connection
    }

    static void logout(Connection conn) {
        try {
            if (conn != null)
                conn.close()
        } catch (SQLException e) {
            e.printStackTrace()
        }
    }

    private void login() {
        Connection conn = null

        try {
            Class.forName(this.driverClass)
            conn = DriverManager.getConnection(this.url, this.userName, this.password)
        } catch (Exception e) {
            e.printStackTrace()
        }
        this.connection = conn
    }
}