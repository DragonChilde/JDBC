package com.jdbc2.preparedstatement.crud;

import com.jdbc2.preparedstatement.util.JDBCUtils;
import com.jdbc.bean.Customer;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * 针对于Customers表的查询操作
 * @author Lee
 * @create 2019/12/6 10:26
 */
public class CustomerForQuery {

    @Test
    public void selectCustomerById()
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = JDBCUtils.getConnection();
            String sql = "SELECT id,name,email,birth from customers where id = ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setObject(1,20);

            //执行,并返回结果集
            resultSet = preparedStatement.executeQuery();
            //处理结果集
            if (resultSet.next())   //next()判断结果集的下一条是否有数据,如果有数据返回true,否则返回false
            {
                Integer id = (Integer)resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                java.util.Date birth = resultSet.getDate("birth");

                /*ORM编程思想
                 * 一个数据表对应一个JAVA类
                 * 表中的一条记录对应JAVA类的一个对象
                 * 表中的一个字段对应JAVA类的一个属性
                 */
                Customer customer = new Customer(id, name, email, birth);
                System.out.println(customer);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(connection,preparedStatement,resultSet);
        }

    }



    /*针对于customers表的通用的查询操作*/
    private Customer selectCustomer(String sql , Object... args)
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Customer customer = new Customer();
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
                    String label = metaData.getColumnLabel(i+1);
                    //通过反射，将对象指定名columnName的属性赋值为指定的值columnValue
                    Field field = customer.getClass().getDeclaredField(label);
                    /*
                    当isAccessible()的结果是false时不允许通过反射访问该字段
                     当该字段时private修饰时isAccessible()得到的值是false，必须要改成true才可以访问
                     */
                    field.setAccessible(true);
                    field.set(customer,object);
                }


            }
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally {
            //关闭资源
            JDBCUtils.closeResource(connection,preparedStatement,resultSet);
        }
        return customer;
    }

    @Test
    public void testCustomerForQuery()
    {

      /*  String sql = "SELECT id,name,email,birth from customers where id = ?";
        Customer customer = this.selectCustomer(sql, 20);
        System.out.println(customer);*/

        String sql = "SELECT id,name,email,birth from customers where name = ?";
        Customer customer = this.selectCustomer(sql, "三上老师");
        System.out.println(customer);
    }
}
