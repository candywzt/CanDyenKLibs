package candyenk.java.io;

import candyenk.java.utils.UArrays;
import candyenk.java.utils.UFile;
import candyenk.java.utils.UString;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件信息实体类(对File类的包装拓展)
 * 方便进行文件自定义展示操作
 * 可设置文件类型文件名(图标和名称)
 */
public class FileInfo implements Comparable<FileInfo> {
    /*************************************静态变量**************************************************/
    protected static final Map<String, FileInfo> customMap = new HashMap<>();//经过自定义的FileInfoMap
    public static final FileInfo superInfo = custom("../");//上级文件夹
    public static final FileInfo emptyInfo = custom("File No Found");//空文件夹
    /*************************************成员变量**************************************************/
    protected File file;//文件源对象
    protected String name;//文件名
    protected String path;//文件路径
    protected long size;//文件大小
    protected long lmd;//文件最后修改时间
    protected boolean isHide;//是否是隐藏文件
    protected FileType type;//文件类型
    /**********************************************************************************************/
    /***********************************公共静态方法*************************************************/
    /**********************************************************************************************/
    /**
     * 从File对象创建FileInfo对象
     * 文件不存在则变为emptyFileInfo
     */
    public static FileInfo create(File file) {
        if (UFile.isEmpty(file)) return emptyInfo;
        FileInfo info = new FileInfo();
        info.file = file;
        info.path = file.getAbsolutePath();
        info.name = file.getName();
        info.type = FileType.type(file);
        info.size = file.isDirectory() ? 0 : file.length();
        info.lmd = file.lastModified();
        info.isHide = info.name.startsWith(".");
        return info;
    }

    /**
     * 以自定义字符串创建FileInfo对象
     * 自定义Info都是文件夹形态
     */
    public static FileInfo custom(String path) {
        if (UString.isEmpty(path)) return emptyInfo;
        FileInfo info = customMap.get(path);
        if (info == null) {
            info = new FileInfo();
            info.path = info.name = path;
            info.type = FileType.DIRECTORY;
            info.setCustom();
        }
        return info;
    }

    /**
     * 从File数组中创建FileInfo数组
     */
    public static FileInfo[] toInfos(File[] files) {
        if (UArrays.isEmpty(files)) return new FileInfo[0];
        return UArrays.T2R(files, FileInfo.class, FileInfo::create);
    }

    /**
     * 从字符串数组中创建自定义FileInfo数组
     */
    public static FileInfo[] toInfos(String[] path) {
        if (UArrays.isEmpty(path)) return new FileInfo[0];
        return UArrays.T2R(path, FileInfo.class, FileInfo::custom);
    }
/**********************************************************************************************/
/*************************************构造方法**************************************************/
    /**********************************************************************************************/
    protected FileInfo() {}
/**********************************************************************************************/
/*************************************继承方法**************************************************/
/**********************************************************************************************/
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
    public boolean equals(Object obj) {
        if (obj instanceof FileInfo info) {
            return this.path.equals(info.path);
        } else return false;
    }

    @Override
    public String toString() {
        return getPath();
    }
/**********************************************************************************************/
/*************************************公共方法**************************************************/
/**********************************************************************************************/

    /**
     * 设置文件类型
     * 和文件显示图标有关
     */
    public void setType(FileType type) {
        this.type = type;
        setCustom();
    }


    /**
     * 获取文件名
     */
    public String getName() {
        return name;
    }

    /**
     * 获取文件地址
     * 唯一值,从null创建的自定义文件Path是UUID
     */
    public String getPath() {
        return path;
    }

    /**
     * 获取文件大小
     * 自定义文件和文件夹都为0
     */
    public long getSize() {
        return size;
    }

    /**
     * 获取文件最后修改日期
     * 自定义文件为0
     */
    public long getLmd() {
        return lmd;
    }

    /**
     * 获取文件类型
     */
    public FileType getType() {
        return type;
    }

    /**
     * 获取文件输入流(读)
     */
    public FileInputStream read() {
        if (isDirectory()) return null;
        try {
            return new FileInputStream(file);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    /**
     * 获取文件输入流(读)
     */
    public FileOutputStream write() {
        if (isDirectory()) return null;
        try {
            return new FileOutputStream(file);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    /**
     * 获取父级文件Info
     */
    public FileInfo getParent() {
        if (file == null || file.getParentFile() == null) return null;
        return create(file.getParentFile());
    }

    /**
     * 获取子文件Info组
     *
     * @param showHide 是否显示隐藏文件(夹)
     * @param showMove 是否显示返回上层
     */
    public FileInfo[] listInfos(boolean showHide, boolean showMove) {
        if (file == null) return new FileInfo[0];
        String[] paths = file.list();
        List<FileInfo> list = new ArrayList<>();
        if (showMove && file.getParent() != null) list.add(superInfo);
        if (UArrays.isEmpty(paths)) list.add(emptyInfo);
        else for (String s : paths) {
            FileInfo info = FileInfo.create(new File(s));
            if (info == null || (!showHide && info.isHide)) continue;
            list.add(info);

        }
        return list.toArray(new FileInfo[0]);
    }

    /**
     * 获取当前文件是否隐藏
     * 自定义文件都是false
     */
    public boolean isHide() {
        return isHide;
    }

    /**
     * 是不是自定义文件
     */
    public boolean isCustom() {
        return UFile.isEmpty(file);
    }

    /**
     * 是不是文件夹
     * 自定义文件默认都是文件夹
     */
    public boolean isDirectory() {
        return getType() == FileType.DIRECTORY;
    }
/**********************************************************************************************/
/*************************************私有方法**************************************************/
    /**********************************************************************************************/
    private void setCustom() {//设置自定义类型
        customMap.put(this.path, this);
        file = null;
    }
}