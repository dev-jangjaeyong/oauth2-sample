package com.big.config.datasource.properties;

/**
 * @author jang jea young (wman11@bizinfogroup.co.kr)
 * @since 2018-11-09
 */
public interface DataSourceProperties {
    public String getDriverClassName();

    public String getUrl();

    public String getUsername();

    public String getPassword();

    public int getInitialSize();

    public int getMaxActive();

    public int getMaxIdle();

    public int getMinIdle();

    public int getMaxWait();

    public String getValidationQuery();
}
