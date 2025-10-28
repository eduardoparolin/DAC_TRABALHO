package com.dac.bank_account.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean(name = "commandDataSource")
    public DataSource commandDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver")
                .url("jdbc:postgresql://shared-postgres:5432/sharedpostgres")
                .username("postgres")
                .password("1234")
                .build();
    }

    @Bean(name = "queryDataSource")
    public DataSource queryDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver")
                .url("jdbc:postgresql://shared-postgres:5432/sharedpostgres")
                .username("postgres")
                .password("1234")
                .build();
    }
}