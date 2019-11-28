package com.jdbc1.connection;

import org.junit.Test;

import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @author Lee
 * @create 2019/11/28 15:13
 */
public class ConnectionTest {

    // 方式一：
    @Test
    public void testConnection1() throws SQLException {
        //1.提供java.sql.Driver接口实现类的对象
        Driver driver = new com.mysql.jdbc.Driver();

        //2.提供url，指明具体操作的数据
        String url = "jdbc:mysql://120.77.237.175:9306/jdbc?serverTimezone=GMT&useSSL=false&characterEncoding=utf-8";
        // jdbc:mysql:协议
        // localhost:ip地址
        // 3306：默认mysql的端口号
        // test:test数据库

        //3.提供Properties的对象，指明用户名和密码
        Properties properties = new Properties();
        properties.setProperty("user", "root");
        properties.setProperty("password", "123456");

        //4.调用driver的connect()，获取连接
        Connection connect = driver.connect(url, properties);

        System.out.println(connect);
    }


    // 方式二：对方式一的迭代:在如下的程序中不出现第三方的api,使得程序具有更好的可移植性
    @Test
    public void testConnection2() throws Exception {
        // 1.获取Driver实现类对象：使用反射
        Class<?> clazz = Class.forName("com.mysql.jdbc.Driver");
        Driver driver = (Driver) clazz.newInstance();

        //2.提供url，指明具体操作的数据
        String url = "jdbc:mysql://120.77.237.175:9306/jdbc?serverTimezone=GMT&useSSL=false&characterEncoding=utf-8";

        //3.提供Properties的对象，指明用户名和密码
        Properties properties = new Properties();
        properties.setProperty("user", "root");
        properties.setProperty("password", "123456");

        //4.调用driver的connect()，获取连接
        Connection connect = driver.connect(url, properties);

        System.out.println(connect);
    }

    //方式三：使用DriverManager替换Driver
    @Test
    public void testConnection3() throws Exception {
        // 1.获取Driver实现类对象：使用反射
        Class<?> clazz = Class.forName("com.mysql.jdbc.Driver");
        Driver driver = (Driver) clazz.newInstance();
        // 注册驱动
        DriverManager.registerDriver(driver);

        //2.提供另外三个连接的基本信息：
        String url = "jdbc:mysql://120.77.237.175:9306/jdbc?serverTimezone=GMT&useSSL=false&characterEncoding=utf-8";
        String user = "root";
        String password = "123456";

        // 获取连接
        Connection connect = DriverManager.getConnection(url, user, password);
        System.out.println(connect);
    }

    @Test
    public void testConnection4() throws Exception {

        //1.提供三个连接的基本信息：
        String url = "jdbc:mysql://120.77.237.175:9306/jdbc?serverTimezone=GMT&useSSL=false&characterEncoding=utf-8";
        String user = "root";
        String password = "123456";

        //2. 获取连接
        Connection connect = DriverManager.getConnection(url, user, password);
        System.out.println(connect);
    }

    //方式五(final版)：将数据库连接需要的4个基本信息声明在配置文件中，通过读取配置文件的方式，获取连接
    /*
     * 此种方式的好处？
     * 1.实现了数据与代码的分离。实现了解耦
     * 2.如果需要修改配置文件信息，可以避免程序重新打包。
     */
    @Test
    public void testConnection5() throws Exception {

        //1.读取配置文件中的4个基本信息
        InputStream io = this.getClass().getClassLoader().getResourceAsStream("db.properties");

        Properties properties = new Properties();
        properties.load(io);

        String driver = properties.getProperty("driver");
        String url = properties.getProperty("url");
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");

        //2.加载驱动
       Class.forName(driver);
        //3.获取连接
        Connection connect = DriverManager.getConnection(url, user, password);
        System.out.println(connect);

    }
}
