package life.qbic.oncostore

import javax.inject.Singleton
import java.sql.Connection
import java.sql.DriverManager

import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires

@Singleton
@Requires(property = "app.db.host")
@Requires(property = "app.db.port")
@Requires(property = "app.db.name")
@Requires(property = "app.db.pw")
@Requires(property = "app.db.user")
@Requires(property = "app.db.driver.class")
@Requires(property = 'app.db.driver.prefix')
class DataBase {

    private final String dbURL
    private final String userName
    private final String password
    private final String driverPrefix //remove ?
    private final String driverClass
    private final Connection connection

    DataBase(@Property(name = 'app.db.host') String host,
             @Property(name = 'app.db.port') String port,
             @Property(name = 'app.db.name') String name,
             @Property(name = 'app.db.user') String user,
             @Property(name = 'app.db.pw') String pw,
             @Property(name = 'app.db.driver.class') String driverClass,
             @Property(name = 'app.db.driver.prefix') String driverPrefix) {
        this.dbURL = driverPrefix + "://" + host + "/" + name // no port?
        this.userName = user
        this.password = pw
        this.driverClass = driverClass
        this.connection = login()
    }

    private void logout(Connection conn) {
        if (conn != null)
            conn.close()
    }

    Connection getConnection() {
        return connection
    }

    String getDriverClass() {
        return driverClass
    }

    private Connection login() {
        Connection conn = null
        try {
            Class.forName(this.driverClass)
            conn = DriverManager.getConnection(this.dbURL, this.userName, this.password)
        } catch (Exception e) {
            e.printStackTrace()
        }
        return conn
    }
}

/*
 @TODO might need to be replaced by external lib (singleton), for testing purposes we will stick now to this definition
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
*/