package task;


import util.DBUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 1，初始化数据库，数据库文件预定好，放在
 * 2，并且读取sql文件，
 * 3，再执行sql语句来初始化表
 */
public class DBinit {

    public static String[] readSQL(){
        try {
            //通过classLoader获取流，或者通过FileInputStream获取
            InputStream is = DBinit.class.getClassLoader().getResourceAsStream("init.sql");
            // 字节流转换为字符串：需要通过字节字符转换流来操作
            BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null){
                if(line.contains("--")){
                    //去掉--注释的代码
                    line = line.substring(0,line.indexOf("--"));
                }
                sb.append(line);
            }
            String[] sqls = sb.toString().split(";");
            return sqls;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("读取sql文件错误",e);
        }
    }

    public static void init(){
        //数据库jdbc操作：SQL语句的执行
        Connection connection = null;
        Statement statement = null;
        try{
            //1，建立数据库连接connection
            connection = DBUtil.getConnection();
            //2，创建SQL语句执行对象statement
            statement = connection.createStatement();
            String[] sqls = readSQL();
            for(String sql : sqls){
                //3，执行sql语句
                statement.executeUpdate(sql);
            }
            //4，如果是查询操作，获取结果集Resultset,处理结果集
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("初始化数据库表操作失败",e);
        }finally {
            //5，释放资源
            DBinit.close(connection,statement);
        }
    }

    public static void close(Connection connection, Statement statement) {
        close(connection,statement,null);
    }

    public static void close(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            if(connection != null){
                connection.close();
            }
            if(statement != null){
                statement.close();
            }
            if(resultSet != null){
                resultSet.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("释放数据库资源错误",e);
        }
    }
}
