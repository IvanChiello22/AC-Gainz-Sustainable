package model;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.TimeZone;

public class ConPool {

    private static DataSource dataSource;

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            String dbHost = System.getenv("DB_HOST");
            if (dbHost == null)
                dbHost = "localhost";

            String dbPort = System.getenv("DB_PORT");
            if (dbPort == null)
                dbPort = "3306";

            String dbName = System.getenv("DB_NAME");
            if (dbName == null)
                dbName = "Progetto_TSW_Dependability";

            String dbUser = System.getenv("DB_USER");
            if (dbUser == null)
                dbUser = "root";

            String dbPass = System.getenv("DB_PASS");
            if (dbPass == null)
                dbPass = "123456789";

            final PoolProperties p = new PoolProperties();
            p.setUrl("jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?serverTimezone="
                    + TimeZone.getDefault().getID());
            p.setDriverClassName("com.mysql.cj.jdbc.Driver");
            p.setUsername(dbUser);
            p.setPassword(dbPass);
            p.setMaxActive(100);
            p.setInitialSize(10);
            p.setMinIdle(10);
            p.setRemoveAbandonedTimeout(60);
            p.setRemoveAbandoned(true);
            dataSource = new DataSource();
            dataSource.setPoolProperties(p);
        }
        return dataSource.getConnection();
    }
}
