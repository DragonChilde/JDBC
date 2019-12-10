package com.jdbc6.connection;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.junit.Test;

import java.sql.Connection;

/**
 * @author Lee
 * @create 2019/12/10 17:02
 */
public class c3p0Test {

    //方式一：
    @Test
    public void getConnection1() throws Exception
    {
        //获取c3p0数据库连接池
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        cpds.setDriverClass( "com.mysql.jdbc.Driver" );
        cpds.setJdbcUrl( "jdbc:mysql://120.77.237.175:9306/jdbc" );
        cpds.setUser("root");
        cpds.setPassword("123456");

        //通过设置相关的参数，对数据库连接池进行管理：
        //设置初始时数据库连接池中的连接数
        cpds.setInitialPoolSize(10);

        Connection connection = cpds.getConnection();
        System.out.println(connection);

        //销毁c3p0数据库连接池(一般都不会销毁连接池)
        //DataSources.destroy( cpds );
    }


    @Test
    public void getConnection2() throws Exception
    {
        ComboPooledDataSource cpds = new ComboPooledDataSource("myc3p0config");
        Connection connection = cpds.getConnection();
        System.out.println(connection);
    }
}
