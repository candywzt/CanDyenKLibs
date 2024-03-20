package candyenk.android.tools;

import android.util.Log;
import candyenk.android.base.ApplicationCDK;
import candyenk.java.io.IO;
import candyenk.java.tools.JS;
import candyenk.java.utils.UArrays;
import candyenk.java.utils.UFile;
import candyenk.java.utils.UTime;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;

import java.io.*;
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
    public static final int INFO = 1;
    public static final int DEBUG = 5;
    public static final int ERROR = 10;
    public static final int NONE = 100;
    public static final String[] levelSA = {"Info", "Debug", "Error", "None"};//日志级别文本数组
    public static final Integer[] levelIA = {INFO, DEBUG, ERROR, NONE};//日志级别int数组
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
     * 获取日志级别文本
     */
    public static String getLevelString(int level) {
        int index = UArrays.indexOf(level, levelIA);
        return index == -1 ? null : levelSA[index];
    }

    /**
     * 获取日志级别数值
     */
    public static int getLevelInt(String level) {
        int index = UArrays.indexOf(level, levelSA);
        return index == -1 ? -1 : levelIA[index];
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
        //TODO:记得改回来
        Log.e(tag, msg == null ? "NULL" : msg.toString());
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
        //TODO:记得改回来
        Log.e(tag, msg == null ? "NULL" : msg.toString());
        return t;
    }


    /**
     * Error级别日志打印
     * 只打印不记录
     * msg可以使异常
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
    public static boolean e(String tag, Throwable e, Object msg) {
        return e(tag, e, msg, false);
    }

    /**
     * Error级别日志打印
     * 只打印不记录
     * 附带返回值boolean
     * 附带Exception
     */

    public static boolean e(String tag, Throwable e, Object msg, boolean b) {
        return e(tag, e, msg, Boolean.valueOf(b));
    }

    /**
     * Error级别日志打印
     * 只打印不记录
     * 附带返回值int
     * 附带Exception
     */

    public static int e(String tag, Throwable e, Object msg, int i) {
        return e(tag, e, msg, Integer.valueOf(i));
    }

    /**
     * Error级别日志打印
     * 只打印不记录
     * 附带返回值Object
     * 附带Exception
     */
    public static <T> T e(String tag, Throwable e, Object msg, T t) {
        String m = (msg == null ? "NULL" : msg.toString()) + (e == null ? "" : (":(" + e.getClass().getSimpleName() + ")" + e.getMessage()));
        Log.e(tag, m);
        if (e != null) Log.e(tag, Log.getStackTraceString(e));
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
     * 清空日志
     */
    public boolean clear() {
        fileList.clear();
        file = null;
        out = IO.close(out);
        return UFile.deleteFile(folder);
    }

    /**
     * 获取TagLoger
     * 没有会自行创建
     */
    public Loger getLoger(String tag) {
        Loger loger = map.get(tag);
        if (loger == null) loger = new Loger(this, tag);
        return loger;
    }

    /**
     * 获取所有TagLoger
     */
    public Map<String, Loger> getLogerMap() {
        return map;
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
     * 获取TagArray
     */
    public String[] getTagArray() {
        String[] array = new String[map.size()];
        map.keySet().toArray(array);
        Arrays.sort(array, String.CASE_INSENSITIVE_ORDER);
        return array;
    }

    /**
     * 获取日志列表
     * 顺序按时间排序
     * 返回字符串数组
     */
    public List<LogInfo> getLogList() {
        List<LogInfo> list = new ArrayList<>();
        for (File f : fileList) {
            if (f != null) IO.readString(UFile.getReader(f, Charset.defaultCharset()), s -> {
                LogInfo info = LogInfo.decode(s);
                if (info != null) list.add(info);
            });
        }
        return list;
    }

    public List<LogInfo> getLogList(String tag) {
        return getLoger(tag).getLogList();
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
    private void log(String tag, int level, Object msg, Throwable e) {
        if (!sign || folder == null) return;
        getOut().println(new LogInfo(tag, level, msg, e).encode());
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
                } catch (Throwable ignored) {}
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
        private final String tag;
        private int targetLevel;//目标级别

        protected Loger(L l, String tag) {
            this.l = l;
            this.tag = tag;
            l.map.put(this.tag, this);
        }

        /**
         * 设置日志纪录级别
         * 低于指定级别的日志不会记录
         * 但会打印
         */
        public Loger setTargetLevel(int level) {
            this.targetLevel = level;
            return this;
        }

        /**
         * 获取当前TAG的所有日志
         */
        public List<LogInfo> getLogList() {
            List<LogInfo> list = new ArrayList<>();
            for (LogInfo info : l.getLogList()) if (info.getTag().equals(tag)) list.add(info);
            return list;
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
            if (INFO >= targetLevel) l.log(tag, INFO, msg, null);
            return L.i(tag, msg, t);
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
            if (DEBUG >= targetLevel) l.log(tag, DEBUG, msg, null);
            return L.d(tag, msg, t);
        }

        /**
         * Error级别日志记录
         * 附带返回值false
         */
        public boolean e(Object msg) {
            return e(msg, false);
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
            if (msg instanceof Throwable) return e((Throwable) msg, null, t);
            return e(null, msg, t);
        }

        /**
         * Error级别日志记录
         * 附带参数Exception
         * 附带返回值false
         */
        public boolean e(Throwable e, Object msg) {
            return e(e, msg, false);
        }

        /**
         * Error级别日志记录
         * 附带参数Exception
         * 附带返回值boolean
         */
        public boolean e(Throwable e, Object msg, boolean b) {
            return e(e, msg, Boolean.valueOf(b));
        }

        /**
         * Error级别日志记录
         * 附带参数Exception
         * 附带返回值int
         */
        public int e(Throwable e, Object msg, int i) {
            return e(e, msg, Integer.valueOf(i));
        }

        /**
         * Error级别日志记录
         * 附带参数Exception
         * 附带返回值Object
         */
        public <T> T e(Throwable e, Object msg, T t) {
            if (ERROR >= targetLevel) l.log(tag, ERROR, msg == null ? "NULL" : msg.toString(), e);
            String m = (msg == null ? "NULL" : msg.toString()) + (e == null ? "" : (":(" + e.getClass().getSimpleName() + ")" + e.getMessage()));
            L.e(tag, e, m);
            return t;
        }
    }

    /**
     * 保存日志信息的类
     */
    public static class LogInfo implements Serializable, Comparable<LogInfo> {
        private long time;//日志产生时间
        private String tag;//日志标签
        private int level;//日志级别
        private String msg;//日志内容
        private Throwable error;//日志异常

        /**
         * 解码一条日志
         * 解码失败为null
         */
        public static LogInfo decode(String log) {
            try {
                long a = System.currentTimeMillis();
                LogInfo info = new LogInfo();
                JsonObject json = JsonParser.parseString(log).getAsJsonObject();
                info.time = json.get("time").getAsLong();
                info.tag = json.get("tag").getAsString();
                info.level = json.get("level").getAsInt();
                info.msg = json.get("msg").getAsString();
                JsonElement e = json.get("error");
                if (e != null) info.error = JS.readOfString(e.getAsString());
                return info;
            } catch (Throwable e) {
                L.e("L", e, "日志解码失败");
                return null;
            }
        }

        public LogInfo(String tag, int level, Object msg, Throwable error) {
            this.time = System.currentTimeMillis();
            this.tag = tag == null ? "NULL" : tag;
            this.level = level;
            this.msg = msg == null ? "NULL" : msg.toString();
            this.error = error;
        }

        private LogInfo() {
        }

        /**
         * 编码本条日志
         */
        public String encode() {
            try {
                JsonObject json = new JsonObject();
                json.addProperty("time", time);
                json.addProperty("tag", tag);
                json.addProperty("level", level);
                json.addProperty("msg", msg);
                if (error != null) json.addProperty("error", JS.writeToString(error));
                return ApplicationCDK.gson.toJson(json);
            } catch (Exception e) {
                L.e("L", e, "日志编码失败");
                return "";
            }
        }

        public long getTime() {
            return time;
        }

        public String getTag() {
            return tag;
        }

        public int getLevel() {
            return level;
        }

        public String getMsg() {
            return msg;
        }

        public Throwable getError() {
            return error;
        }

        /**
         * 获取时间字符串
         */
        public String getTimeString() {
            return UTime.D2S(time);
        }

        /**
         * 获取级别字符串
         */
        public CharSequence getLevelString() {
            return L.getLevelString(level);
        }

        /**
         * 获取堆栈列表
         */
        public List<String> getStackList() {
            if (error == null) return new ArrayList<>();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(out);
            error.printStackTrace(ps);
            ps.flush();
            IO.close(ps, out);
            return Arrays.asList(out.toString().split("\n"));
        }

        @NotNull
        @Override
        public String toString() {
            return "LogInfo{" +
                    "时间=" + getTimeString() +
                    ", TAG='" + tag + '\'' +
                    ", 级别='" + getLevelString() + '\'' +
                    ", 信息='" + msg + '\'' +
                    ", 异常=" + getStackList() +
                    '}';
        }

        @Override
        public int compareTo(LogInfo o) {
            return Long.compare(getTime(), o.getTime());
        }
    }
}
