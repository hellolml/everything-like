package app;

import util.PinyinUtil;
import util.Util;

import java.io.File;
import java.util.Date;
import java.util.Objects;

public class FileMeta {
    private String name;
    //文件名称
    private String path;
    //文件所在的父目录路径
    private Long size;
    //文件大小
    private Date lastModified;
    //文件上次修改时间
    private String sizeText;
    //给客户端控件使用,和app.xml中保持一致
    private String lastModifiedText;
    //和app.xml保持一致
    private Boolean isDriectory;
    private String pinyin;
    //文件拼音首字母
    private String pinyinFirst;
    //通过文件设置属性
    public FileMeta(File file){
        this(file.getName(),file.getPath(),file.isDirectory(),file.length(),new Date(file.lastModified()));
    }
//通过数据库获取数据设置FileMate
    public FileMeta(String name,String path,boolean isDriectory,long size,Date lastModified){
        this.name = name;
        this.path = path;
        this.size = size;
        this.isDriectory = isDriectory;
        this.lastModified = lastModified;
        if(PinyinUtil.containsChinese(name)){
            String[] pinyins = PinyinUtil.get(name);
            pinyin = pinyins[0];
            pinyinFirst = pinyins[1];
        }
        //各户端表格控件文件大小,文件上次修改时间的设置
        sizeText = Util.pareseSize(size);
        lastModifiedText = Util.parseDate(lastModified);
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getPinyinFirst() {
        return pinyinFirst;
    }

    public void setPinyinFirst(String pinyinFirst) {
        this.pinyinFirst = pinyinFirst;
    }

    public Boolean getDriectory() {
        return isDriectory;
    }

    public void setDriectory(Boolean driectory) {
        isDriectory = driectory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getSizeText() {
        return sizeText;
    }

    public void setSizeText(String sizeText) {
        this.sizeText = sizeText;
    }

    public String getLastModifiedText() {
        return lastModifiedText;
    }

    public void setLastModifiedText(String lastModifiedText) {
        this.lastModifiedText = lastModifiedText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileMeta meta = (FileMeta) o;
        return Objects.equals(name, meta.name) && Objects.equals(path, meta.path) && Objects.equals(isDriectory, meta.isDriectory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path, isDriectory);
    }

}
