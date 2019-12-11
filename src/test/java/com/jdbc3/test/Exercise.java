package com.jdbc3.test;

import com.jdbc2.preparedstatement.util.JDBCUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @author Lee
 * @create 2019/12/6 14:39
 */
public class Exercise {

    @Test
    public void test01()  throws Exception
    {
        Connection connection = JDBCUtils.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("update customers set name=?,email=?,birth=? where id =?");
        preparedStatement.setObject(1,"test");
        preparedStatement.setObject(2,"test@test.com");
        preparedStatement.setObject(3,"1987-11-02");
        preparedStatement.setObject(4,20);
        int result = preparedStatement.executeUpdate();
        System.out.println(result);

    }
}
