package util;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    public static final String DATA_PATTERN = "yyyy-MM-dd HH:mm:ss\n";

    public static String pareseSize(long size) {
        String[] danweis = {"B","KB","MB","GB"};
        int idx = 0;
        while (size>1024&&idx<danweis.length-1){
            size/=1024;
            idx++;
        }
        return size+danweis[idx];
    }
    /*解析日期为中文日期
    @param lastModiffied
    @return
    */
    public static String parseDate(Date lastModified) {
        return new SimpleDateFormat(DATA_PATTERN).format(lastModified);
    }
    public static void main(String args[]){
            System.out.println(new File("F:\\InterviewSolution\\src\\December"));
            System.out.println(pareseSize(100_000_000_000L));
    }
}
