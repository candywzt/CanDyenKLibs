package candyenk.java.utils;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Java包资源工具
 * JavaSourcesTool
 */
public class R {
    public static final Class<URL> UURL = URL.class;
    public static final Class<URI> UURI = URI.class;

    private static final Class<R> R = R.class;
    /******************************************************************************************************************/
    /************************************************资源获取重写********************************************************/
    /******************************************************************************************************************/
    public static <T> T getResource(Class<T>... classz) {
        return getResource("/", classz);
    }

    public static <T> T getResource(String filePath, Class<T>... classz) {
        return IL(R.getResource(filePath), classz);
    }

    public static InputStream getResourceAsStream(String filePath) {
        return R.getResourceAsStream(filePath);
    }
    /******************************************************************************************************************/
    /***********************************************快捷资源工具*********************************************************/
    /******************************************************************************************************************/
    /**
     * 获取资源Layout目录下文件
     * 支持xml和fxml文件可不带后缀
     */
    public static <T> T getLayout(String filename, Class<T>... classz) {
        T o = null;
        if (filename.length() > 3 && "xml".equals(filename.substring(filename.length() - 3))) {
            o = getResource("/layout/" + filename);
        }
        if (o == null) {
            o = getResource("/layout/" + filename + ".xml");
        }
        if (o == null) {
            o = getResource("/layout/" + filename + ".fxml");
        }
        return o;
    }

    /**
     * 获取资源Layout目录下文件
     * 与当前对象同名的小写layout文件
     */
    public static <T> T getLayout(Object o, Class<T>... classz) {
        String filename = o.getClass().getSimpleName().toLowerCase();
        return getLayout(filename, classz);
    }

    public static <T> T getLayout(Class<T>... classz) {
        return getResource("/layout/", classz);
    }

    /**
     * 获取资源Css目录下的文件
     * 后缀.css可带可不带
     */
    public static <T> T getCss(String filename, Class<T>... classz) {
        T o = null;
        if (filename.length() > 3 && "css".equals(filename.substring(filename.length() - 3))) {
            o = getResource("/css/" + filename);
        }
        if (o == null) {
            o = getResource("/css/" + filename + ".css");
        }
        return o;
    }

    /**
     * 获取资源CSS目录下的文件
     * 与当前对象同名的小写css文件
     */
    public static <T> T getCss(Object o, Class<T>... classz) {
        String filename = o.getClass().getSimpleName().toLowerCase();
        return getCss(filename, classz);
    }

    public static <T> T getCss(Class<T>... classz) {
        return getResource("/css/", classz);
    }


    /**
     * 获取资源目录下的图片文件
     * jpg
     */
    public static <T> T getImage(String filename, Class<T>... classz) {
        T o = null;
        if (filename.length() > 3 && "jpg".equals(filename.substring(filename.length() - 3))) {
            o = getResource("/image/" + filename);
        }
        if (o == null) {
            o = getResource("/image/" + filename + ".jpg");
        }
        return o;
    }

    public static <T> T getImage(Class<T>... classz) {
        return getResource("/image/", classz);
    }


    /**
     * 获取资源目录下的图标文件
     * png
     */
    public static <T> T getIcon(String filename, Class<T>... classz) {
        T o = null;
        if (filename.length() > 3 && "png".equals(filename.substring(filename.length() - 3))) {
            o = getResource("/icon/" + filename);
        }
        if (o == null) {
            o = getResource("/icon/" + filename + ".png");
        }
        return o;
    }

    public static <T> T getIcon(Class<T>... classz) {
        return getResource("/icon/", classz);
    }

    /**
     * 获取资源目录下的语言文件
     */
    public static <T> T getLanguage(String filename, Class<T>... classz) {
        T o = null;
        if (filename.length() > 4 && "json".equals(filename.substring(filename.length() - 4))) {
            o = getResource("/language/" + filename);
        }
        if (o == null) {
            o = getResource("/language/" + filename + ".json");
        }
        return o;
    }

    public static <T> T getLanguage(Class<T>... classz) {
        return getResource("/language/", classz);
    }
    /******************************************************************************************************************/
    /***********************************************私有静态方法*********************************************************/
    /******************************************************************************************************************/
    private static <T> T IL(URL url, Class<T>[] classz) {
        if (classz.length != 0 && classz[0].equals(UURI)) {
            URI uri = null;
            try {
                uri = url.toURI();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return (T) uri;
        }
        return (T) url;
    }
}
