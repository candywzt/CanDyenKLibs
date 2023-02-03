package candyenk.android.shell.file;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import candyenk.android.shell.*;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * Android Shell操作的文件类
 */
public class SFile implements Serializable, Comparable<SFile> {
    private final String absolutePath;//当前文件的绝对路径
    private final String name;//文件名
    private final String parent;//父级路径

    /**
     * 初始化文件操作使用的Shell
     *
     * @param isShizuku true:使用ShizukuShell
     *                  false:使用RootShell
     */
    public static void initShell(boolean isShizuku) {
        SFiles.initShell(isShizuku);
    }

    /**
     * 通过绝对路径创建
     */
    public SFile(String pathname) {
        if (!pathname.startsWith(File.separator)) pathname = File.separator + pathname;
        if (pathname.endsWith(File.separator)) pathname = pathname.substring(0, pathname.length() - 1);
        this.absolutePath = pathname;
        int index = absolutePath.lastIndexOf(File.separatorChar);
        this.name = absolutePath.substring(index + 1);
        this.parent = absolutePath.substring(0, absolutePath.length() - name.length() - 1);
    }

    /**
     * 通过父级文件和相对路径创建
     */
    public SFile(SFile parent, String name) {
        this(parent.getAbsolutePath() + (name.startsWith(File.separator) ? name : File.separator + name));
    }

    /**
     * 获取当前文件的绝对路径
     */
    public String getAbsolutePath() {
        return absolutePath;
    }

    /**
     * 获取当前文件的绝对路径
     */
    public String getPath() {
        return getAbsolutePath();
    }

    /**
     * 获取当前文件名
     */
    public String getName() {
        return name;
    }

    /**
     * 获取父级路径
     */
    public String getParent() {
        return parent;
    }

    /**
     * 获取父级文件对象
     */
    public SFile getParentFile() {
        return new SFile(getParent());
    }

    /**
     * 是否可读
     */
    public boolean canRead() {
        return false;
    }

    /**
     * 是否可写
     */
    public boolean canWrite() {
        return false;
    }

    /**
     * 是否可执行
     */
    public boolean canExecute() {
        return false;
    }

    /**
     * 文件或文件夹是否存在
     */
    public boolean exists() {
        return false;
    }

    /**
     * 是不是文件夹
     */
    public boolean isDirectory() {
        return false;
    }

    /**
     * 是不是文件
     */
    public boolean isFile() {
        return false;
    }

    /**
     * 是否隐藏
     */
    public boolean isHidden() {
        return false;
    }

    /**
     * 最近修改时间
     */
    public long lastModified() {
        return 0;
    }

    /**
     * 文件长度
     */
    public long length() {
        return 0;
    }

    /**
     * 创建文件
     */
    public boolean createNewFile() {
        return false;
    }

    /**
     * 删除文件
     */
    public boolean delete() {
        return false;
    }

    /**
     * 列出所有子文件和文件夹
     */
    public String[] list() {
        return null;
    }

    /**
     * 创建文件夹
     */
    public boolean mkdir() {
        return false;
    }

    /**
     * 创建文件夹,顺带创建所有需要的父级文件夹
     */
    public boolean mkdirs() {
        return false;
    }

    /**
     * 重命名文件夹
     * 成功后当前文件将不存在
     */
    public boolean renameTo(SFile file) {
        if (file.exists()) return false;
        return false;
    }

    /**
     * 设置当前代表的文件的最近修改时间
     */
    public boolean setLastModified(long time) {
        return false;
    }

    /**
     * 设置当前文件写权限
     *
     * @param writable  是否可写
     * @param ownerOnly 是否everyone
     */
    public boolean setWritable(boolean writable, boolean ownerOnly) {
        return false;
    }

    /**
     * 设置当前文件everyone写权限
     */
    public boolean setWritable(boolean writable) {
        return setWritable(writable, true);
    }

    /**
     * 设置当前文件读权限
     *
     * @param readable  是否可读
     * @param ownerOnly 是否everyone
     */
    public boolean setReadable(boolean readable, boolean ownerOnly) {
        return false;
    }

    /**
     * 设置当前文件everyone读权限
     */
    public boolean setReadable(boolean readable) {
        return false;
    }

    /**
     * 设置当前文件执行权限
     *
     * @param executable 是否可执行
     * @param ownerOnly  是否everyone
     */
    public boolean setExecutable(boolean executable, boolean ownerOnly) {
        return false;
    }

    /**
     * 设置当前文件ereryone执行权限
     *
     * @param executable
     * @return
     */
    public boolean setExecutable(boolean executable) {
        return setExecutable(executable, true);
    }

    /**
     * 读取文件
     * 请先检查读取权限
     */
    public InputStream read() {
        return new SFileInputStream(this);
    }

    /**
     * 写入文件
     * 请先检查写入权限
     */
    public OutputStream write() {
        return null;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof SFile) {
            return getAbsolutePath().equals(((SFile) obj).getAbsolutePath());
        } else return false;
    }

    @Override
    public int compareTo(SFile o) {
        return getAbsolutePath().compareTo(o.getAbsolutePath());
    }

    @NonNull
    @Override
    public String toString() {
        return getPath();
    }


}
