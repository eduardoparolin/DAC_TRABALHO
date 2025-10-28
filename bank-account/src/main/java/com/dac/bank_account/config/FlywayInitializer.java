package com.dac.bank_account.config;

import jakarta.annotation.PostConstruct;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class FlywayInitializer {

    private final DataSource commandDataSource;
    private final DataSource queryDataSource;

    public FlywayInitializer(@Qualifier("commandDataSource") DataSource commandDataSource,
                             @Qualifier("queryDataSource") DataSource queryDataSource) {
        this.commandDataSource = commandDataSource;
        this.queryDataSource = queryDataSource;
    }

    @PostConstruct
    public void migrate() {
        Flyway.configure()
                .dataSource(commandDataSource)
                .schemas("command_account")
                .locations("classpath:db/migration/command")
                .baselineOnMigrate(true)
                .load()
                .migrate();

        Flyway.configure()
                .dataSource(queryDataSource)
                .schemas("query_account")
                .locations("classpath:db/migration/query")
                .baselineOnMigrate(true)
                .load()
                .migrate();
    }
}

