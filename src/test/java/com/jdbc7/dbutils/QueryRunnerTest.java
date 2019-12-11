package com.jdbc7.dbutils;

import com.jdbc.bean.Customer;
import com.jdbc6.connection.util.JDBCUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.junit.Test;

import java.sql.Connection;
import java.util.*;

/**
 * @author Lee
 * @create 2019/12/11 14:20
 */
public class QueryRunnerTest {

    @Test
    public void update()
    {
        Connection connection = null;
        try{
            QueryRunner queryRunner = new QueryRunner();
            connection = JDBCUtils.getDruidConnection();
            String sql = "insert into customers (`name`,email,birth) values (?,?,?)";
            int result = queryRunner.update(connection, sql, "三上老师", "ss@gmail.com", "1988-11-02");
            System.out.println(result);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(connection,null);
        }
    }

    /*
     * BeanHander:是ResultSetHandler接口的实现类，用于封装表中的一条记录。
     */
    @Test
    public void queryForBeanHandler()
    {
        Connection connection = null;
        try{
            QueryRunner queryRunner = new QueryRunner();
            connection = JDBCUtils.getDruidConnection();
            String sql = "select name,email,birth from customers where id = ?";
            BeanHandler<Customer> resultSetHandler = new BeanHandler<>(Customer.class);
            Object object = queryRunner.query(connection, sql, resultSetHandler, 25);
            System.out.println(object);
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(connection,null);
        }

    }

    /*
     * BeanListHandler:是ResultSetHandler接口的实现类，用于封装表中的多条记录构成的集合。
     */
    @Test
    public void queryForBeanListHandler()
    {
        Connection connection = null;
        try{
            QueryRunner queryRunner = new QueryRunner();
            connection = JDBCUtils.getDruidConnection();
            String sql = "select name,email,birth from customers ";
            BeanListHandler<Customer> beanListHandler = new BeanListHandler<>(Customer.class);
           List<Customer> list =  queryRunner.query(connection, sql, beanListHandler);
           list.forEach(System.out::println);
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(connection,null);
        }
    }


    /*
     * MapHander:是ResultSetHandler接口的实现类，对应表中的一条记录。
     * 将字段及相应字段的值作为map中的key和value
     */
    @Test
    public void queryForMapHandler()
    {  Connection connection = null;
        try{
            QueryRunner queryRunner = new QueryRunner();
            connection = JDBCUtils.getDruidConnection();
            String sql = "select name,email,birth from customers where id =?";

            MapHandler mapHandler = new MapHandler();
            Map<String, Object> map = queryRunner.query(connection, sql, mapHandler,25);
            System.out.println(map);
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(connection,null);
        }
    }


    /*
     * MapListHander:是ResultSetHandler接口的实现类，对应表中的多条记录。
     * 将字段及相应字段的值作为map中的key和value。将这些map添加到List中
     */
    @Test
    public void queryForMapListHandler()
    {  Connection connection = null;
        try{
            QueryRunner queryRunner = new QueryRunner();
            connection = JDBCUtils.getDruidConnection();
            String sql = "select name,email,birth from customers";

            MapListHandler mapListHandler = new MapListHandler();
            List<Map<String, Object>> maps = queryRunner.query(connection, sql, mapListHandler);
            maps.forEach(System.out::println);
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(connection,null);
        }
    }
}
