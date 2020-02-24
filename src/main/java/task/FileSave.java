package task;

import app.FileMeta;
import util.DBUtil;
import util.Util;


import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FileSave implements ScanCallback {
    @Override
    public void callback(File dir) {
        //文件夹下一级子文件的扫描
        //获取本地目录下一级子文件和子文件夹
        //集合框架中自定义类型,判断是否某个对象在集合存在;对比两个集合中的元素
        File[]children = dir.listFiles();
        List<FileMeta> locals = new ArrayList<>();
        if(children!=null){
            for (File child:children) {
                //System.out.println(child.getPath());
                //save(child);
               locals.add(new FileMeta(child));
            }
        }
        //获取数据库保存的dir目录下一级文件夹
        //TODO List<file>
        List<FileMeta> metas = query(dir);


        //数据库有.本地没有,做删除
        for (FileMeta meta: metas){
            if (!locals.contains(meta)){
                //meta的删除:1.删除meta信息本身
                // 2.如果meta是目录的话,还要删除meta所有的子文件,子文件夹都删除
                delete(meta);
            }
        }

        //本地有,数据库没有,做插入
        for (FileMeta meta: locals){
            if(!metas.contains(meta)){
                save(meta);
            }
        }
    }

    private void delete(FileMeta meta) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DBUtil.getConnection();
            String sql = "delete from file_meta where "+
                     "(name=? and path =? and is_directory=?)";
            if(meta.getDriectory()){
                sql+=" or path = ?"+
                " or path like ?";
            }
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,meta.getName());
            preparedStatement.setString(2,meta.getPath());
            preparedStatement.setBoolean(3,meta.getDriectory());
            if(meta.getDriectory()){
                preparedStatement.setString(4,meta.getPath());
                preparedStatement.setString(5,meta.getPath());
            }
            preparedStatement.executeUpdate();
        }catch (Exception e){
            throw  new RuntimeException("错误",e );
        }finally {
            DBinit.close(connection,preparedStatement );
        }
    }

    private List<FileMeta> query(File dir){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<FileMeta> metas = new ArrayList<>();
        try{
            //1,数据库创建数据库连接
            connection = DBUtil.getConnection();
            String sql = "select name, path, is_directory, size, last_modified"+
                    " from file_meta where path=?";
            //2,创建jdbc操作命令对象statement
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,dir.getPath());
            //3,执行sql语句
            resultSet = preparedStatement.executeQuery();
            //处理结果集
            while (resultSet.next()){
                String name = resultSet.getString("name");
                String path = resultSet.getString("path");
                Boolean isDirectory = resultSet.getBoolean("is_directory");
                Long size = resultSet.getLong("size");
                Timestamp lastModified = resultSet.getTimestamp("last_modified");
                FileMeta meta = new FileMeta(name,path,isDirectory,
                        size,new java.util.Date(lastModified.getTime()));
                System.out.printf("查询文件信息: name = %s,path = %s,is_directory= %s,size = %s,last_modified=%s\n",name,path,
                        String.valueOf(isDirectory),String.valueOf(size),Util.parseDate(new java.util.Date(lastModified.getTime())));
                metas.add(meta);
            }
            return metas;
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("查询文件信息出错",e);
        }finally {
            DBinit.close(connection,preparedStatement,resultSet );
        }
    }
    private  void save(FileMeta meta){
        //1.获取数据库的连接
        Connection connection =null;
        PreparedStatement statement = null;
        try{
            //1,连接数据库
            connection = DBUtil.getConnection();
            //添加is_directory,占位符.set值3,4,5,6 +1,添加set第三个参数is_directory
            String sql = "insert into file_meta"+"(name, path, is_directory,size, last_modified, pinyin, pinyin_first) "+"values(?,?,?,?,?,?,?)";
            //2,获取sql命令对象statement
            statement = connection.prepareStatement(sql);
            statement.setString(1,meta.getName() );
            statement.setString(2,meta.getPath() );
            statement.setBoolean(3,meta.getDriectory());
            statement.setLong(4,meta.getSize() );
            statement.setString(5,meta.getLastModifiedText());
//            String pinyin = null;
//            String pinyin_first = null;
//            //文件包含汉字,需要获取拼音和拼音首字母,并保存到数据库
//            if(PinyinUtil.containsChinese(meta.getPinyin())){
//                String []pinyins = PinyinUtil.get(meta.getPinyinFirst());
//                pinyin = pinyins[0];
//                pinyin_first = pinyins[1];
//            }
            statement.setString(6,meta.getPinyin() );
            statement.setString(7,meta.getPinyinFirst());
            System.out.println("执行文件保存操作"+sql);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("文件保存失败,检查一下sql,insert语句",e );
        }finally {
            DBinit.close(connection,statement );
        }
    }
//    public static void main(String args[]){
//            DBinit.init();
//            File file = new File("F:\\InterviewSolution\\src");
//            FileSave fileSave = new FileSave();
//            fileSave.save(file);
//            fileSave.query(file.getParentFile());
//        List<FileMeta> locals = new ArrayList<>();
//        locals.add(new FileMeta("新建文件夹", "F:\\TMP\\maven-test", true, 0, new Date()));
//        locals.add(new FileMeta("中华人民共和国", "F:\\TMP\\maven-test", true, 0, new Date()));
//        locals.add(new FileMeta("憨批.txt", "F:\\TMP\\maven-test\\中华人民共和国", true, 0, new Date()));
//
//        List<FileMeta> metas = new ArrayList<>();
//        metas.add(new FileMeta("新建文件夹", "F:\\TMP\\maven-test", true, 0, new Date()));
//        metas.add(new FileMeta("中华人民共和国", "F:\\TMP\\maven-test", true, 0, new Date()));
//        metas.add(new FileMeta("阿凡达.txt", "F:\\TMP\\maven-test\\中华人民共和国", true, 0, new Date()));
//        Boolean contains=locals.contains(new FileMeta(new File("")));
//        //集合中是否包含某个元素,不一定传入这个对象在java内存中是同一个对象的引用
//        //满足一定条件(集合中的元素类型需要从写hashcode equels
//        for (FileMeta meta : locals){
//            if(!metas.contains(meta)){
//                System.out.println(meta);
//            }
//        }
//    }
}
