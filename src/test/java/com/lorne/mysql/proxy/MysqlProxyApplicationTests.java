package com.lorne.mysql.proxy;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@SpringBootTest
class MysqlProxyApplicationTests {

    @Test
    void contextLoads() {
        try {
            Class.forName(com.mysql.jdbc.Driver.class.getName());//指定链接类型
            String url = "jdbc:mysql://localhost:13306/test";
            String user = "root";
            String password = "root";
            Connection conn = DriverManager.getConnection(url, user, password);//url为代理服务器的地址
            PreparedStatement pst = conn.prepareStatement("select * from test;");//准备执行语句
            ResultSet resultSet = pst.executeQuery();
            while (resultSet.next()) {
                System.out.println(resultSet.getLong(1) + ": " + resultSet.getString(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
