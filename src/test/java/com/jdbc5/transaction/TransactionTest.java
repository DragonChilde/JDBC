package com.jdbc5.transaction;

import com.jdbc2.preparedstatement.util.JDBCUtils;

import com.jdbc.bean.User;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;


/*
 * 1.什么叫数据库事务？
 * 事务：一组逻辑操作单元,使数据从一种状态变换到另一种状态。
 * 		> 一组逻辑操作单元：一个或多个DML操作。
 *
 * 2.事务处理的原则：保证所有事务都作为一个工作单元来执行，即使出现了故障，都不能改变这种执行方式。
 * 当在一个事务中执行多个操作时，要么所有的事务都被提交(commit)，那么这些修改就永久地保存
 * 下来；要么数据库管理系统将放弃所作的所有修改，整个事务回滚(rollback)到最初状态。
 *
 * 3.数据一旦提交，就不可回滚
 *
 * 4.哪些操作会导致数据的自动提交？
 * 		>DDL操作一旦执行，都会自动提交。
 * 			>set autocommit = false 对DDL操作失效
 * 		>DML默认情况下，一旦执行，就会自动提交。
 * 			>我们可以通过set autocommit = false的方式取消DML操作的自动提交。
 * 		>默认在关闭连接时，会自动的提交数据
 */
/**
 * @author Lee
 * @create 2019/12/9 15:22
 */
public class TransactionTest {
    @Test
    public void testUpdateWithTx() throws Exception
    {
        Connection connection = null;
        try {
            connection = JDBCUtils.getConnection();
            System.out.println(connection.getAutoCommit()); //打印可看到默认是true默认提交
            //1.取消数据的自动提交
            connection.setAutoCommit(false);
            String sql1 = "update user_table set balance = balance + 200 where user =?";
            updateSql(connection,sql1,"AA");

            //模拟网络异常
            System.out.println(10/0);

            String sql2 = "update user_table set balance = balance - 200 where user =?";
            updateSql(connection,sql2,"BB");

            //2.提交数据
            connection.commit();
        }catch (Exception e)
        {
            e.printStackTrace();
            try{
                //3.回滚数据
                connection.rollback();
            }catch (Exception e1 ){
                e1.printStackTrace();
            }
        } finally {
            try {
                //修改其为自动提交数据
                //主要针对于使用数据库连接池的使用
                connection.setAutoCommit(true);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            JDBCUtils.closeResource(connection,null);
        }


    }

    // 通用的增删改操作
    private int updateSql(Connection connection, String sql,Object... args)// sql中占位符的个数与可变形参的长度相同！
    {
        PreparedStatement preparedStatement = null;
        try {
            // 1.预编译sql语句，返回PreparedStatement的实例
             preparedStatement = connection.prepareStatement(sql);
            // 2.填充占位符
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i+1,args[i]);
            }
            // 3.执行
           return preparedStatement.executeUpdate();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            // 4.资源的关闭
            JDBCUtils.closeResource(null,preparedStatement);
        }
        return 0;
    }


    @Test
    public void testTransactionSelect() throws  Exception
    {
        Connection connection = JDBCUtils.getConnection();
        //获取当前连接的隔离级别
        System.out.println(connection.getTransactionIsolation());
        //取消自动提交数据
        connection.setAutoCommit(false);
        //设置数据库的隔离级别：
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
        String sql = "select user ,password,balance from user_table where user = ?";
        User user = getInstanceForSelectByCondition(connection,User.class, sql, "AA");
        System.out.println(user);

    }


    @Test
    public void testTransactionUpdate() throws Exception
    {
        Connection connection = JDBCUtils.getConnection();
        //取消自动提交数据
        connection.setAutoCommit(false);
        String sql = "update user_table set balance = balance + 200 where user =?";
        updateSql(connection,sql,"AA");
        Thread.sleep(10000);
        connection.commit();
        System.out.println("update success");
    }




    /*通过反射把获取Class,根据传过来的泛型返回*/
    private <T> T getInstanceForSelectByCondition(Connection connection,Class<T> clazz, String sql , Object... args) throws Exception
    {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        T t = clazz.newInstance();
        try {
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
}
