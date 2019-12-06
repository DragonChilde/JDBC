package com.jdb2.preparedstatement.util;

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
}
