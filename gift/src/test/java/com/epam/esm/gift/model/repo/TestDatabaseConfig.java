package com.epam.esm.gift.model.repo;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;

public class TestDatabaseConfig {
        private static final String DB_URL = "jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
        private static final String DB_USERNAME = "h2";
        private static final String DB_PASSWORD = "password";

        public static DataSource createDataSource() {
            JdbcDataSource dataSource = new JdbcDataSource();
            dataSource.setURL(DB_URL);
            dataSource.setUser(DB_USERNAME);
            dataSource.setPassword(DB_PASSWORD);
            return dataSource;
        }
}
