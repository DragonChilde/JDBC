package com.jdbc6.connection.util;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.apache.commons.dbutils.DbUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @Description 操作数据库的工具类
 * @author Lee
 * @version
 * @create 2019/12/5 11:50
 */

public class JDBCUtils {

    /**
     *
     * @Description 获取数据库的连接
     * @author Lee
     * @date  2019/12/5 11:50
     * @return
     * @throws Exception
     */
    public static Connection getConnection() throws IOException,ClassNotFoundException, SQLException {
        //1.读取配置文件中的4个基本信息
        InputStream io = JDBCUtils.class.getClassLoader().getResourceAsStream("db.properties");

        Properties properties = new Properties();
        properties.load(io);

        String driver = properties.getProperty("driver");
        String url = properties.getProperty("url");
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");

        //2.加载驱动
        Class.forName(driver);
        //3.获取连接
        return DriverManager.getConnection(url, user, password);
    }


    /**
     *
     * @Description 使用C3P0的数据库连接池技术
     * @author shkstart
     * @date 下午3:01:25
     * @return
     * @throws SQLException
     */
    //数据库连接池只需提供一个即可
   /* private static  ComboPooledDataSource cpds = new ComboPooledDataSource("myc3p0config");
    public static Connection getC3P0Connection() throws Exception
    {
        Connection connection = cpds.getConnection();
        return connection;
    }*/

    /**
     *
     * @Description 使用DBCP数据库连接池技术获取数据库连接
     * @author shkstart
     * @date 下午3:35:25
     * @return
     * @throws Exception
     */
    //创建一个DBCP数据库连接池
    private static  BasicDataSource dataSource;

    static {
        try {

            Properties properties = new Properties();
            InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("dbcp-config.properties");
            properties.load(stream);
            dataSource = BasicDataSourceFactory.createDataSource(properties);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static Connection getDBCPConnection() throws Exception
    {
        Connection connection = dataSource.getConnection();
        return connection;
    }

    /**
     * 使用Druid数据库连接池技术
     */
    private static DataSource druidDataSource;
    static {
        try {
            Properties properties = new Properties();
            InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("druid-config.properties");
            properties.load(stream);
            druidDataSource = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static Connection getDruidConnection() throws Exception
    {
        Connection connection = dataSource.getConnection();
        return connection;
    }



    public static void closeResource(Connection connection, Statement statement)
    {
        try {
            if (statement !=null)
            {
                statement.close();
            }
        }catch (SQLException e)
        {
            e.printStackTrace();
        }

        try {
            if (connection !=null)
            {
                connection.close();
            }
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static void closeResource(Connection connection, Statement statement, ResultSet resultSet)
    {
        try {
            if (statement !=null)
            {
                statement.close();
            }
        }catch (SQLException e)
        {
            e.printStackTrace();
        }

        try {
            if (connection !=null)
            {
                connection.close();
            }
        }catch (SQLException e)
        {
            e.printStackTrace();
        }

        try {
            if (resultSet != null)
            {
                resultSet.close();
            }
        }catch (SQLException e )
        {
            e.printStackTrace();
        }
    }



    public static void closeResourceByDButil(Connection connection, Statement statement, ResultSet resultSet)
    {
        /*方法一*/
       /* try {
            DbUtils.close(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            DbUtils.close(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            DbUtils.close(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }*/

        /*方法二*/
        DbUtils.closeQuietly(connection);
        DbUtils.closeQuietly(statement);
        DbUtils.closeQuietly(resultSet);
    }

}
