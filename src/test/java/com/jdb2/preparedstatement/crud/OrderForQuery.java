package com.jdb2.preparedstatement.crud;

import com.jdb2.preparedstatement.util.JDBCUtils;
import com.jdbc.bean.Customer;
import com.jdbc.bean.Order;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * 针对于Order表的通用的查询操作
 * @author Lee
 * @create 2019/12/6 10:27
 */
public class OrderForQuery {


    /*
     * 针对于表的字段名与类的属性名不相同的情况：
     * 1. 必须声明sql时，使用类的属性名来命名字段的别名
     * 2. 使用ResultSetMetaData时，需要使用getColumnLabel()来替换getColumnName(),
     *    获取列的别名。
     *  说明：如果sql中没有给字段其别名，getColumnLabel()获取的就是列名
     *
     *
     */

    /*针对于order表的通用的查询操作*/
    private Order selectOrder(String sql , Object... args)
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Order order = new Order();
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
                    Field field = order.getClass().getDeclaredField(label);
                    /*
                    当isAccessible()的结果是false时不允许通过反射访问该字段
                     当该字段时private修饰时isAccessible()得到的值是false，必须要改成true才可以访问
                     */
                    field.setAccessible(true);
                    field.set(order,object);
                }


            }
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally {
            //关闭资源
            JDBCUtils.closeResource(connection,preparedStatement,resultSet);
        }
        return order;
    }

    @Test
    public void testCustomerForQuery()
    {
        String sql = "SELECT order_id orderId,order_name orderName,order_date orderDate from `order` where order_id = ?";
        Order order = this.selectOrder(sql, 1);
        System.out.println(order);
    }
}
