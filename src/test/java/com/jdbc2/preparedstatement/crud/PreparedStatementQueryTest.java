package com.jdbc2.preparedstatement.crud;

import com.jdbc2.preparedstatement.util.JDBCUtils;
import com.jdbc.bean.Customer;
import com.jdbc.bean.Order;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lee
 * @create 2019/12/5 15:30
 */
public class PreparedStatementQueryTest {

    /*通过反射把获取Class,根据传过来的泛型返回*/
    private <T> T getInstanceForSelectByCondition(Class<T> clazz, String sql , Object... args) throws Exception
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        T t = clazz.newInstance();
        try {
            connection = JDBCUtils.getConnection();

            preparedStatement = connection.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i+1,args[i]);
            }
            //执行,并返回结果集
            resultSet = preparedStatement.executeQuery();
            //获取结果集的元数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            //通过ResultSetMetaData获取结果集中的列数
            int columnCount = metaData.getColumnCount();

            //处理结果集
            if (resultSet.next())   //next()判断结果集的下一条是否有数据,如果有数据返回true,否则返回false
            {

                //处理结果集一行数据中的每一个列
                for (int i = 0; i < columnCount; i++) {
                    //获取列值
                    Object object = resultSet.getObject(i+1);
                    //获取每个列的列名
                    //通过ResultSetMetaData
                    //获取列的列名：getColumnName() --不推荐使用
                    //获取列的别名：getColumnLabel()
                    String label = metaData.getColumnLabel(i+1);
                    //通过反射，将对象指定名columnName的属性赋值为指定的值columnValue
                    Field field = clazz.getDeclaredField(label);
                    /*
                    当isAccessible()的结果是false时不允许通过反射访问该字段
                     当该字段时private修饰时isAccessible()得到的值是false，必须要改成true才可以访问
                     */
                    field.setAccessible(true);
                    field.set(t,object);
                }


            }
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally {
            //关闭资源
            JDBCUtils.closeResource(connection,preparedStatement,resultSet);
        }
        return t;
    }


    @Test
    public void getInstanceForSelectByConditionTest() throws Exception
    {
        String sql1 = "SELECT order_id orderId,order_name orderName,order_date orderDate from `order` where order_id = ?";
        Order order = this.getInstanceForSelectByCondition(Order.class,sql1, 1);
        System.out.println(order);

        String sql2 = "SELECT id,name,email,birth from customers where name = ?";
        Customer customer = this.getInstanceForSelectByCondition(Customer.class,sql2, "三上老师");
        System.out.println(customer);
    }


    /*通过反射把获取Class,根据传过来的泛型返回*/
    private <T> List<T> getInstanceForSelectToList(Class<T> clazz, String sql , Object... args) throws Exception
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<T> list = new ArrayList<>();
        try {
            connection = JDBCUtils.getConnection();

            preparedStatement = connection.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i+1,args[i]);
            }
            //执行,并返回结果集
            resultSet = preparedStatement.executeQuery();
            //获取结果集的元数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            //通过ResultSetMetaData获取结果集中的列数
            int columnCount = metaData.getColumnCount();

            //处理结果集
            while (resultSet.next())   //next()判断结果集的下一条是否有数据,如果有数据返回true,否则返回false
            {
                T t = clazz.newInstance();
                //处理结果集一行数据中的每一个列
                for (int i = 0; i < columnCount; i++) {
                    //获取列值
                    Object object = resultSet.getObject(i+1);
                    //获取每个列的列名
                    //通过ResultSetMetaData
                    //获取列的列名：getColumnName() --不推荐使用
                    //获取列的别名：getColumnLabel()
                    String label = metaData.getColumnLabel(i+1);
                    //通过反射，将对象指定名columnName的属性赋值为指定的值columnValue
                    Field field = clazz.getDeclaredField(label);
                    /*
                    当isAccessible()的结果是false时不允许通过反射访问该字段
                     当该字段时private修饰时isAccessible()得到的值是false，必须要改成true才可以访问
                     */
                    field.setAccessible(true);
                    field.set(t,object);

                }

                list.add(t);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally {
            //关闭资源
            JDBCUtils.closeResource(connection,preparedStatement,resultSet);
        }
        return list;
    }

    @Test
    public void getInstanceForSelectToListTest() throws Exception{
        String sql1 = "SELECT order_id orderId,order_name orderName,order_date orderDate from `order`";
        List<Order> orders = this.getInstanceForSelectToList(Order.class, sql1);
       orders.forEach(System.out::println);

        String sql2 = "SELECT id,name,email,birth from customers";
        List<Customer> customers = this.getInstanceForSelectToList(Customer.class,sql2);
       customers.forEach(System.out::println);
    }

}
