package com.jdbc4.batch;

import com.jdbc2.preparedstatement.util.JDBCUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;



/*
 * 使用PreparedStatement实现批量数据的操作
 *
 * update、delete本身就具有批量操作的效果。
 * 此时的批量操作，主要指的是批量插入。使用PreparedStatement如何实现更高效的批量插入？
 *
 * 题目：向goods表中插入20000条数据
 * CREATE TABLE goods(
	id INT PRIMARY KEY AUTO_INCREMENT,
	NAME VARCHAR(25)
   );
 * 方式一：使用Statement
 * Connection conn = JDBCUtils.getConnection();
 * Statement st = conn.createStatement();
 * for(int i = 1;i <= 20000;i++){
 * 		String sql = "insert into goods(name)values('name_" + i + "')";
 * 		st.execute(sql);
 * }
 *
 */
/**
 * @author Lee
 * @create 2019/12/9 13:44
 */
public class BatchTest {

    //批量插入的方式：使用PreparedStatement
    @Test
    public void batch1() throws Exception
    {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            long start = System.currentTimeMillis();
            connection = JDBCUtils.getConnection();
            preparedStatement = connection.prepareStatement("insert into goods (`name`) values (?)");
            for (int i = 1; i <= 20000; i++) {
                preparedStatement.setObject(1, i + "");
                preparedStatement.executeUpdate();
            }
            long end = System.currentTimeMillis();
            System.out.println("花费的时间为：" + (end - start));
        } catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            JDBCUtils.closeResource(connection,preparedStatement);
        }


    }

    /*
     * 批量插入的方式三：
     * 1.addBatch()、executeBatch()、clearBatch()
     * 2.mysql服务器默认是关闭批处理的，我们需要通过一个参数，让mysql开启批处理的支持。
     * 		 ?rewriteBatchedStatements=true 写在配置文件的url后面
     * 3.使用更新的mysql 驱动：mysql-connector-java-5.1.37-bin.jar
     */
    @Test
    public void batch2() throws Exception
    {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            long start = System.currentTimeMillis();
            connection = JDBCUtils.getConnection();
            preparedStatement = connection.prepareStatement("insert into goods (`name`) values (?)");
            for (int i = 1; i <= 100000; i++) {
                preparedStatement.setObject(1, i + "");
                //1."攒"sql
               preparedStatement.addBatch();

               if (i%500 == 0){
                   //2.执行batch
                   preparedStatement.executeBatch();
                   //3.清空batch
                   preparedStatement.clearBatch();
               }

            }
            long end = System.currentTimeMillis();
            System.out.println("花费的时间为：" + (end - start));  //20000--1478   100000--5347

        } catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            JDBCUtils.closeResource(connection,preparedStatement);
        }

    }

    //批量插入的方式四：设置连接不允许自动提交数据
    @Test
    public void batch3() throws Exception
    {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            long start = System.currentTimeMillis();
            connection = JDBCUtils.getConnection();
            //设置不允许自动提交数据
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("insert into goods (`name`) values (?)");
            for (int i = 1; i <= 100000; i++) {
                preparedStatement.setObject(1, i + "");
                //1."攒"sql
                preparedStatement.addBatch();

                if (i%500 == 0){
                    //2.执行batch
                    preparedStatement.executeBatch();
                    //3.清空batch
                    preparedStatement.clearBatch();
                }
            }
            connection.commit();

            long end = System.currentTimeMillis();
            System.out.println("花费的时间为：" + (end - start));

        } catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            JDBCUtils.closeResource(connection,preparedStatement);
        }

    }
}
