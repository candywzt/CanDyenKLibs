package candyenk.android.tools;

import android.util.Log;
import candyenk.java.io.IO;
import candyenk.java.utils.UFile;

import java.io.Closeable;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Android 日志帮助类
 * 只有三级,别嫌少
 * 记录并打印:Loger log =  L.getInstance().getLoger(TAG);
 * 只打印 L.e(TAG,msg);
 * msg的位置可以传Exception无妨
 */
public class L implements Closeable {
    public static final String INFO = "INFO";
    public static final String DEBUG = "DEBUG";
    public static final String ERROR = "ERROR";
    private static L instance;//日志全局实例
    private final File folder;//日志文件夹
    private final List<File> fileList;//日志文件列表
    private final Map<String, Loger> map = new HashMap<>();//日志Tag表
    private long ms, mn;//日志单文件限制大小,
    private File file;//当前日志文件
    private PrintStream out;//当前日志输出流
    private boolean sign;//标记


    /**
     * 初始化Loger
     * maxSize:单文件最大占用
     * maxNumber:文件最大数量
     * folder为空则不启用记录
     */
    public static void initialize(File folder, long maxSize, int maxNumber) {
        if (instance == null) instance = new L(folder).setMaxSize(maxSize).setMaxNumber(maxNumber);
    }

    /**
     * 获取全局实例
     * 需要另外的自行创建
     */
    public static L getInstance() {
        return instance;
    }

    /**
     * Info级别日志打印
     * 只打印不记录
     * 附带返回值false
     */
    public static boolean i(String tag, Object msg) {
        return i(tag, msg, false);
    }

    /**
     * Info级别日志打印
     * 只打印不记录
     * 附带返回值boolean
     */
    public static boolean i(String tag, Object msg, boolean b) {
        return i(tag, msg, Boolean.valueOf(b));
    }

    /**
     * Info级别日志打印
     * 只打印不记录
     * 附带返回值int
     */
    public static int i(String tag, Object msg, int i) {
        return i(tag, msg, Integer.valueOf(i));
    }

    /**
     * Info级别日志打印
     * 只打印不记录
     * 附带返回值Object
     */
    public static <T> T i(String tag, Object msg, T t) {
        Log.i(tag, msg == null ? "NULL" : msg.toString());
        return t;
    }

    /**
     * Debug级别日志打印
     * 只打印不记录
     * 附带返回值false
     */
    public static boolean d(String tag, Object msg) {
        return d(tag, msg, false);
    }

    /**
     * Debug级别日志打印
     * 只打印不记录
     * 附带返回值boolean
     */
    public static boolean d(String tag, Object msg, boolean b) {
        return d(tag, msg, Boolean.valueOf(b));
    }

    /**
     * Debug级别日志打印
     * 只打印不记录
     * 附带返回值int
     */
    public static int d(String tag, Object msg, int i) {
        return d(tag, msg, Integer.valueOf(i));
    }

    /**
     * Debug级别日志打印
     * 只打印不记录
     * 附带返回值Object
     */
    public static <T> T d(String tag, Object msg, T t) {
        Log.d(tag, msg == null ? "NULL" : msg.toString());
        return t;
    }


    /**
     * Error级别日志打印
     * 只打印不记录
     * 附带返回值false
     */
    public static boolean e(String tag, Object msg) {
        return e(tag, msg, false);
    }

    /**
     * Error级别日志打印
     * 只打印不记录
     * 附带返回值boolean
     */
    public static boolean e(String tag, Object msg, boolean b) {
        return e(tag, msg, Boolean.valueOf(b));
    }

    /**
     * Error级别日志打印
     * 只打印不记录
     * 附带返回值int
     */
    public static int e(String tag, Object msg, int i) {
        return e(tag, msg, Integer.valueOf(i));
    }

    /**
     * Error级别日志打印
     * 只打印不记录
     * 附带返回值Object
     */
    public static <T> T e(String tag, Object msg, T t) {
        if (msg instanceof Exception) return e(tag, (Exception) msg, null, t);
        Log.e(tag, msg == null ? "NULL" : msg.toString());
        return t;
    }


    /**
     * Error级别日志打印
     * 只打印不记录
     * 附带返回值false
     * 附带Exception
     */
    public static boolean e(String tag, Exception e, Object msg) {
        return e(tag, e, msg, false);
    }

    /**
     * Error级别日志打印
     * 只打印不记录
     * 附带返回值boolean
     * 附带Exception
     */

    public static boolean e(String tag, Exception e, Object msg, boolean b) {
        return e(tag, e, msg, Boolean.valueOf(b));
    }

    /**
     * Error级别日志打印
     * 只打印不记录
     * 附带返回值int
     * 附带Exception
     */

    public static int e(String tag, Exception e, Object msg, int i) {
        return e(tag, e, msg, Integer.valueOf(i));
    }

    /**
     * Error级别日志打印
     * 只打印不记录
     * 附带返回值Object
     * 附带Exception
     */
    public static <T> T e(String tag, Exception e, Object msg, T t) {
        String m = (msg == null ? "NULL" : msg.toString()) + (e == null ? "" : (":(" + e.getClass().getSimpleName() + ")" + e.getMessage()));
        Log.e(tag, m);
        if (e != null) e.printStackTrace(System.err);
        return t;
    }

    public L(File folder) {
        this.folder = folder;
        if (folder != null && !folder.isDirectory()) {
            UFile.deleteFile(folder);
            UFile.createFolder(folder);
        }
        this.fileList = new ArrayList<>();
        if (folder == null) return;
        String[] list = folder.list();
        if (list != null) for (String s : list) fileList.add(new File(folder, s));
        Collections.sort(fileList);
        startRecycler();
    }

    /**
     * 关闭日志
     * 使其不再记录(不影响打印)
     */
    @Override
    public void close() {
        this.sign = false;
        IO.close(out);
    }

    /**
     * 获取TagLoger
     * 没有会自行创建
     */
    public Loger getLoger(String TAG) {
        Loger loger = map.get(TAG);
        if (loger == null) loger = new Loger(this, TAG);
        return loger;
    }

    /**
     * 设置单文件最大占用
     */
    public L setMaxSize(long size) {
        this.ms = size;
        return this;
    }

    /**
     * 设置文件最大数量
     * 超过最大将自动清理旧日志
     */
    public L setMaxNumber(int size) {
        this.mn = size;
        return this;
    }

    /**
     * 获取日志列表
     * 顺序按时间排序
     * 返回字符串数组
     */
    public List<String[]> getLogList(int index) {
        List<String[]> list = new ArrayList<>();
        int i = fileList.size() - 1 - index;
        File f = fileList.get(i);
        if (f != null) IO.readString(UFile.getReader(f, Charset.defaultCharset()), s -> list.add(s.split("●")));
        return list;
    }

    /**
     * 获取日志文件数量
     */
    public int getCount() {
        String[] list = folder.list();
        return list == null ? 0 : list.length;
    }


    /*** 创建一个新的日志文件 ***/
    private File createFile() {
        File f = new File(folder, String.valueOf(System.currentTimeMillis()));
        UFile.createFile(f);
        fileList.add(f);
        return f;
    }

    /*** 获取日志输出流 ***/
    private PrintStream getOut() {
        if (file == null && fileList.size() > 0) file = fileList.get(fileList.size() - 1);
        if (file == null || file.length() > ms) {
            file = createFile();
            out = IO.close(out);
        }
        if (out == null) out = new PrintStream(UFile.getOutputStream(file, true));
        return out;
    }


    /*** 写入一条日志 日志记录的关键所在 ***/
    private void log(String tag, String level, Object msg) {
        if (!sign || folder == null) return;
        getOut().println(System.currentTimeMillis() + "●" + tag + "●" + level + "●" + (msg == null ? "NULL" : msg.toString()));
    }

    /*** 启动回收器 ***/
    private void startRecycler() {
        new Thread(() -> {
            sign = true;
            while (sign) {
                try {
                    Thread.sleep(5000);
                    if (out != null) out.flush();
                    if (fileList.size() > mn) UFile.deleteFile(fileList.remove(fileList.size() - 1));
                } catch (Exception ignored) {}
            }
        }).start();
    }

    /**
     * 用来代表相同TAG的Loger
     * TAG不会重复
     * msg的位置可以传Exception无妨
     */
    public static class Loger {
        private final L l;
        private final String TAG;

        protected Loger(L l, String tag) {
            this.l = l;
            this.TAG = tag;
            l.map.put(TAG, this);
        }

        /**
         * Info级别日志记录
         * 附带返回值false
         */
        public boolean i(Object msg) {
            return i(msg, false);
        }

        /**
         * Info级别日志记录
         * 附带返回值boolean
         */
        public boolean i(Object msg, boolean b) {
            return i(msg, Boolean.valueOf(b));
        }

        /**
         * Info级别日志记录
         * 附带返回值int
         */
        public int i(Object msg, int i) {
            return i(msg, Integer.valueOf(i));
        }

        /**
         * Info级别日志记录
         * 附带返回值Object
         */
        public <T> T i(Object msg, T t) {
            l.log(TAG, INFO, msg);
            return L.i(TAG, msg, t);
        }

        /**
         * Debug级别日志记录
         * 附带返回值false
         */
        public boolean d(Object msg) {
            return d(msg, false);
        }

        /**
         * Debug级别日志记录
         * 附带返回值boolean
         */
        public boolean d(Object msg, boolean b) {
            return d(msg, Boolean.valueOf(b));
        }

        /**
         * Debug级别日志记录
         * 附带返回值int
         */
        public int d(Object msg, int i) {
            return d(msg, Integer.valueOf(i));
        }

        /**
         * Debug级别日志记录
         * 附带返回值Object
         */
        public <T> T d(Object msg, T t) {
            l.log(TAG, DEBUG, msg);
            return L.d(TAG, msg, t);
        }

        /**
         * Error级别日志记录
         * 附带返回值false
         */
        public boolean e(Object msg) {
            l.log(TAG, ERROR, msg == null ? "NULL" : msg.toString());
            return L.e(TAG, msg);
        }

        /**
         * Error级别日志记录
         * 附带返回值boolean
         */
        public boolean e(Object msg, boolean b) {
            return e(msg, Boolean.valueOf(b));
        }

        /**
         * Error级别日志记录
         * 附带返回值int
         */
        public int e(Object msg, int i) {
            return e(msg, Integer.valueOf(i));
        }

        /**
         * Error级别日志记录
         * 附带返回值Object
         */
        public <T> T e(Object msg, T t) {
            if (msg instanceof Exception) return e((Exception) msg, null, t);
            l.log(TAG, ERROR, msg == null ? "NULL" : msg.toString());
            return L.e(TAG, msg, t);
        }

        /**
         * Error级别日志记录
         * 附带参数Exception
         * 附带返回值false
         */
        public boolean e(Exception e, Object msg) {
            return e(e, msg, false);
        }

        /**
         * Error级别日志记录
         * 附带参数Exception
         * 附带返回值boolean
         */
        public boolean e(Exception e, Object msg, boolean b) {
            return e(e, msg, Boolean.valueOf(b));
        }

        /**
         * Error级别日志记录
         * 附带参数Exception
         * 附带返回值int
         */
        public int e(Exception e, Object msg, int i) {
            return e(e, msg, Integer.valueOf(i));
        }

        /**
         * Error级别日志记录
         * 附带参数Exception
         * 附带返回值Object
         */
        public <T> T e(Exception e, Object msg, T t) {
            String m = (msg == null ? "NULL" : msg.toString()) + (e == null ? "" : (":(" + e.getClass().getSimpleName() + ")" + e.getMessage()));
            return e(m, t);
        }
    }
}
