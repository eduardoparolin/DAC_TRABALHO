package com.dac.bank_account.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.*;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.dac.bank_account.query.repository",
        entityManagerFactoryRef = "queryEntityManagerFactory",
        transactionManagerRef = "queryTransactionManager"
)
@EntityScan(basePackages = "com.dac.bank_account.entity")
public class QueryDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.query")
    public DataSource queryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "queryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean queryEntityManagerFactory(
            @Qualifier("queryDataSource") DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.dac.bank_account.query.entity");
        em.setPersistenceUnitName("queryPU");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Properties props = new Properties();
        props.put("hibernate.hbm2ddl.auto", "update");
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.show_sql", "true");

        em.setJpaProperties(props);

        return em;
    }

    @Bean(name = "queryTransactionManager")
    public PlatformTransactionManager queryTransactionManager(
            @Qualifier("queryEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
