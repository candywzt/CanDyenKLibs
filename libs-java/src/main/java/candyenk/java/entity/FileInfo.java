package candyenk.java.entity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import candyenk.java.utils.UArrays;
import candyenk.java.utils.UFile;

/**
 * 文件信息实体类
 * 方便进行文件自定义展示操作
 */
public class FileInfo implements Comparable<FileInfo> {
    public static final FileInfo superInfo = new FileInfo(null);//返回上级的预留值
    public static final FileInfo emptyInfo = new FileInfo(null);//空的预留值
    private static final Map<String, FileInfo> customMap = new HashMap<>();//经过自定义的FileInfoMap
    private static final Map<String, FileInfo> infoMap = new HashMap<>();//实际存在的FileInfoMap

    private File file;//文件源对象
    private String name = "unknown";//文件名
    private String path;//文件路径
    private long size;//文件大小
    private long lmd;//文件最后修改时间
    private FileType type = FileType.UNKNOWN;//文件类型
    private boolean isHide;//是否是隐藏文件

    private FileInfo(File file) {
        if (file != null) {
            this.path = file.getAbsolutePath();
            if (file.exists()) {
                this.file = file;
                this.name = file.getName();
                this.size = file.length();
                this.lmd = file.lastModified();
                this.type = UFile.getTypeFile(file);
                this.isHide = this.name.startsWith(".");
            }
        } else {
            this.path = "";
        }
    }

    private void setCustom() {//设置自定义类型
        infoMap.remove(this);
        customMap.put(this.path, this);
    }

    /**
     * 从File对象(绝对路径)创建FileInfo对象
     * file为空或file文件不存在则返回NULL
     * 自定义文件请使用另一个静态create方法
     */

    public static FileInfo create(String absolutePath) {
        return create(new File(absolutePath));
    }

    public static FileInfo create(File file) {
        if (UFile.isEmpty(file)) return emptyInfo;
        FileInfo info = infoMap.get(file.getAbsolutePath());
        if (info == null) {
            info = new FileInfo(file);
            infoMap.put(info.path, info);
        }
        return info;
    }


    /**
     * 从指定绝对路径创建自定义FileInfo对象
     */
    public static FileInfo custom(String absolutePath) {
        return custom(new File(absolutePath));
    }

    public static FileInfo custom(File file) {
        if (file == null) return emptyInfo;
        FileInfo info = customMap.get(file.getAbsolutePath());
        if (info == null) {
            info = new FileInfo(file);
            info.setCustom();
        }
        return info;
    }

    /**
     * 从File数组中创建FileInfo数组
     */
    public static FileInfo[] toInfos(File[] files) {
        if (files == null || files.length == 0) return new FileInfo[0];
        return UArrays.toArray(files, (Function<File, FileInfo>) FileInfo::create);
    }


    public File getFile() {
        if (file == null) file = new File(path);
        return file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        setCustom();
    }

    public String getPath() {
        return path;
    }

    public long getSize() {
        return size;
    }

    public long getLmd() {
        return lmd;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
        setCustom();
    }

    public boolean isHide() {
        return isHide;
    }

    public boolean isCustom() {
        return UFile.isEmpty(file);
    }

    public boolean isDirectory() {
        return getType() == FileType.DIRECTORY;
    }

    /**
     * 默认排序方案
     * 目录优先
     */
    @Override
    public int compareTo(FileInfo f) {
        boolean tid = this.isDirectory();
        boolean fid = f.isDirectory();
        if (tid == fid) {
            return getPath().compareTo(f.getPath());
        } else {
            return tid ? -1 : 1;
        }
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "file=" + file +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", size=" + size +
                ", lmd=" + lmd +
                ", type=" + type +
                ", isHide=" + isHide +
                '}';
    }
}