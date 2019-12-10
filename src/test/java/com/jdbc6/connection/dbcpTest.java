package com.jdbc6.connection;

import com.sun.prism.impl.BaseResourceFactory;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.junit.Test;
import org.omg.CORBA.DATA_CONVERSION;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

/**
 * @author Lee
 * @create 2019/12/10 17:45
 */
public class dbcpTest {

    @Test
    public void getConnction1() throws Exception
    {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://120.77.237.175:9306/jdbc");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");


        //还可以设置其他涉及数据库连接池管理的相关属性：
        dataSource.setInitialSize(10);
        //。。。

        Connection connection = dataSource.getConnection();
        System.out.println(connection);
    }


    @Test
    public void getConnection2() throws Exception
    {
        Properties properties = new Properties();
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("dbcp-config.properties");
        properties.load(stream);
        BasicDataSource dataSource = BasicDataSourceFactory.createDataSource(properties);
        Connection connection = dataSource.getConnection();
        System.out.println(connection);
    }

}
