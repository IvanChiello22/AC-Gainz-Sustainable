/*
 * AC-Gainz-Sustainable - Gym focused e-commerce
 * Copyright (C) 2025 Ivan Chiello
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
