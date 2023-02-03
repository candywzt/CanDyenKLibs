package candyenk.java.io;

import candyenk.java.utils.UArrays;
import candyenk.java.utils.UFile;

import java.io.File;
import java.util.*;

/**
 * 文件信息实体类(对File类的包装拓展)
 * 方便进行文件自定义展示操作
 * 可设置文件类型文件名(图标和名称)
 */
public class FileInfo implements Comparable<FileInfo> {
    /*************************************静态变量**************************************************/
    protected static final Map<String, FileInfo> customMap = new HashMap<>();//经过自定义的FileInfoMap
    protected static final Map<String, FileInfo> infoMap = new HashMap<>();//实际存在的FileInfoMap
    public static final FileInfo superInfo = custom("../");//上级文件夹
    public static final FileInfo emptyInfo = custom("File No Found");//空文件夹
    /*************************************成员变量**************************************************/
    private File file;//文件源对象
    private String name;//文件名
    private String path;//文件路径
    private long size;//文件大小
    private long lmd;//文件最后修改时间
    private boolean isHide;//是否是隐藏文件
    private FileType type;//文件类型
    /**********************************************************************************************/
    /***********************************公共静态方法*************************************************/
    /**********************************************************************************************/
    /**
     * 从File对象创建FileInfo对象
     * 无法到达的地址将变为自定义文件
     */
    public static FileInfo create(File file) {
        FileInfo info;
        if (UFile.isEmpty(file)) {
            info = custom(file);
        } else {
            info = infoMap.get(file.getAbsolutePath());
            if (info == null) {
                info = new FileInfo(file);
                infoMap.put(info.path, info);
            }
        }
        return info;
    }

    /**
     * 以File为模板创建自定义FileInfo对象
     * null将创建随机UUID地址自定义FileInfo
     */
    public static FileInfo custom(File file) {
        FileInfo info = null;
        if (file != null) info = customMap.get(file.getAbsolutePath());
        if (file == null || info == null) {
            info = new FileInfo(file);
            info.setCustom();
        }
        return info;
    }

    /**
     * 以自定义字符串创建FileInfo对象
     */
    public static FileInfo custom(String path) {
        FileInfo info = null;
        if (path != null || !path.isEmpty()) info = customMap.get(path);
        if (info == null) {
            info = new FileInfo(null);
            info.path = info.name = path;
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
    protected FileInfo(File file) {
        if (file == null) {
            this.path = this.name = UUID.randomUUID().toString();
            this.type = FileType.DIRECTORY;
        } else {
            this.path = file.getAbsolutePath();
            this.file = file;
            this.name = file.getName();
            this.type = UFile.getTypeFile(file);
            if (file.exists()) {
                this.size = file.isDirectory() ? 0 : file.length();
                this.lmd = file.lastModified();
                this.isHide = this.name.startsWith(".");
            }
        }
    }
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
        if (obj instanceof FileInfo) {
            FileInfo info = (FileInfo) obj;
            return this.path.equals(info.path);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return getPath();
    }
    /**********************************************************************************************/
    /*************************************公共方法**************************************************/
    /**********************************************************************************************/
    /**
     * 设置文件名
     */
    public void setName(String name) {
        this.name = name;
        setCustom();
    }

    /**
     * 设置文件类型
     * 和文件显示图标有关
     */
    public void setType(FileType type) {
        this.type = type;
        setCustom();
    }

    /**
     * 获取File对象
     * 从null创建的自定义文件返回null
     */
    public File getFile() {
        return file;
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
     * 获取父级文件Info
     */
    public FileInfo getParent() {
        if (file == null || file.getParentFile() == null) return null;
        return create(file.getParentFile());
    }

    /**
     * 获取子文件Info组
     */
    public FileInfo[] listInfos(boolean showHide, boolean showMove) {
        if (file == null) return new FileInfo[0];
        String[] paths = file.list();
        int size = UArrays.isEmpty(paths) ? 2 : paths.length + 2;
        List<FileInfo> list = new ArrayList<>(size);
        if (showMove && file.getParent() != null) list.add(superInfo);
        if (size == 2) list.add(emptyInfo);
        UArrays.forEach(paths, (s) -> {
            if (showHide || !s.startsWith(".")) list.add(FileInfo.create(new File(file, s)));
        });
        return list.toArray(new FileInfo[list.size()]);
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
        infoMap.remove(this);
        customMap.put(this.path, this);
    }


}