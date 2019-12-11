package com.jdbc6.connection;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

/**
 * @author Lee
 * @create 2019/12/11 11:37
 */
public class druidTest {

    @Test
    public void getConnection1() throws Exception
    {
        Properties properties = new Properties();
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("druid-config.properties");
        properties.load(stream);

        DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);
        Connection connection = dataSource.getConnection();
        System.out.println(connection);

    }
}
