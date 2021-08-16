package com.big.config.datasource;

import com.big.config.datasource.properties.DataSourceProperties;
import com.big.config.datasource.properties.MasterDataSourceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * @author jang jea young (wman11@bizinfogroup.co.kr)
 * @since 2018-11-09
 */
public abstract class DataSourceConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceConfig.class);

    @Bean
    public abstract DataSource dataSource();

    protected void configureDataSource(org.apache.tomcat.jdbc.pool.DataSource dataSource, DataSourceProperties databaseProperties) {
        LOGGER.info("configureDataSource = {}", databaseProperties);
        dataSource.setDriverClassName(databaseProperties.getDriverClassName());
        dataSource.setUrl(databaseProperties.getUrl());
        dataSource.setUsername(databaseProperties.getUsername());
        dataSource.setPassword(databaseProperties.getPassword());
        dataSource.setMaxActive(10);
        dataSource.setMaxIdle(databaseProperties.getMaxIdle());
        dataSource.setMinIdle(databaseProperties.getMinIdle());
        dataSource.setMaxWait(1000);
        //dataSource.setValidationQuery(databaseProperties.getValidationQuery());
        dataSource.setValidationQuery("select 1");
        dataSource.setRemoveAbandoned(true);
        dataSource.setRemoveAbandonedTimeout(10);
        dataSource.setTestWhileIdle(true);
        dataSource.setValidationInterval(10000);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
    }
}


@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties(MasterDataSourceProperties.class)
class MasterDatabaseConfig extends DataSourceConfig {

    private final MasterDataSourceProperties masterDatabaseProperties;

    @Autowired
    public MasterDatabaseConfig(MasterDataSourceProperties masterDatabaseProperties) {
        this.masterDatabaseProperties = masterDatabaseProperties;
    }

    @Primary
    @Bean(name = "masterDataSource", destroyMethod = "close")
    public DataSource dataSource() {
        org.apache.tomcat.jdbc.pool.DataSource masterDataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        configureDataSource(masterDataSource, masterDatabaseProperties);
        return masterDataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }
}
