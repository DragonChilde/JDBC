package com.jdb2.preparedstatement.crud;

import com.jdb2.preparedstatement.util.JDBCUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Properties;

/**
 * @author Lee
 * @create 2019/12/4 17:43
 */
public class PreparedStatementCrudTest {




    @Test
    public void testInsert() throws Exception
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = JDBCUtils.getConnection();

            String sql = "Insert Into customers (name,email,birth) VALUES (? , ? ,?)";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,"苍老师");
            preparedStatement.setString(2,"test@gmial.com");

            LocalDateTime localDateTime = LocalDateTime.of(1986, 11, 02, 0, 0);
            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);

            Date date = Date.from(zonedDateTime.toInstant());

            preparedStatement.setDate(3,new java.sql.Date(date.getTime()));
            preparedStatement.execute();
        }catch (Exception e)
        {
            e.printStackTrace();
        }finally {
           JDBCUtils.closeResource(connection,preparedStatement);
        }


    }

    @Test
    public void testUpdate()
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            //1.获取数据库的连接
            connection = JDBCUtils.getConnection();

            //2.预编译sql语句，返回PreparedStatement的实例
            String sql = "UPDATE customers SET name =?,email=?,birth=? where id = ?";

            preparedStatement = connection.prepareStatement(sql);
            //3.填充占位符
            preparedStatement.setString(1,"三上老师");
            preparedStatement.setString(2,"ss@gmial.com");

            LocalDateTime localDateTime = LocalDateTime.of(1986, 11, 01, 0, 0);
            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);

            Date date = Date.from(zonedDateTime.toInstant());

            preparedStatement.setDate(3,new java.sql.Date(date.getTime()));
            preparedStatement.setInt(4,21);
            //4.执行
            preparedStatement.execute();
        }catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            //5.资源的关闭
            JDBCUtils.closeResource(connection,preparedStatement);
        }

    }

    //通用的增删改操作
    private void curd(String sql,Object... args)
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            //1.获取数据库的连接
            connection = JDBCUtils.getConnection();

            preparedStatement = connection.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i+1,args[i]);
            }
            //4.执行
            preparedStatement.execute();
        }catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            //5.资源的关闭
            JDBCUtils.closeResource(connection,preparedStatement);
        }
    }

    @Test
    public void TestCurd()
    {
       /* String sql = "DELETE FROM customers WHERE id =?";
        curd(sql,21);*/

       String sql = "UPDATE customers SET name =?,email=?,birth=? where id = ?";
       curd(sql,"三上老师","ss@gmail.com","1986-11-01",20);
    }


}
