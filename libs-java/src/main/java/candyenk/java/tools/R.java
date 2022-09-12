package candyenk.java.tools;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Java包资源工具
 * 获取资源流
 * 获取资源URL
 * 获取Layout URL
 * 获取CSS URL
 * 获取JS URL
 * 获取Image URL
 * 获取ICON URL
 * 获取Language URL
 * URL->URI
 * URI->URL
 */
public class R {
    private static final Class<R> R = R.class;
    private static Logger l = Logger.getLogger(R.getSimpleName());
    /******************************************************************************************************************/
    /************************************************公共静态方法********************************************************/
    /******************************************************************************************************************/
    /**
     * 获取资源流
     *
     */
    public static InputStream getResourceAsStream(String filePath) {
        return R.getResourceAsStream(filePath);
    }

    /**
     * 万能资源获取器
     * 举例:/image/demo1/demo2/demo3/test.jpg
     * ["image","demo1","demo2","demo3","test","jpg"]
     * 为空直接获取资源根目录
     */
    public static URL getResource(String... pathName) {
        return R.getResource(pathSplicing(pathName));
    }

    /**
     * 获取资源Layout目录下文件
     * 支持xml和fxml文件可不带后缀
     */
    public static URL getLayout(String filename) {
        URL url = null;
        if (filename.endsWith("xml")) {
            url = getResource("layout", filename);
        }
        if (url == null) {
            url = getResource("layout", filename, ".xml");
        }
        if (url == null) {
            url = getResource("layout", filename, ".fxml");
        }
        return url;
    }

    /**
     * 获取资源Layout目录下文件
     * 与当前对象同名的小写layout文件
     */
    public static URL getLayout(Object o) {
        String filename = o.getClass().getSimpleName().toLowerCase();
        return getLayout(filename);
    }

    public static URL getLayout() {
        return getResource("layout");
    }

    /**
     * 获取资源Css目录下的文件
     * 后缀.css可带可不带
     */
    public static URL getCss(String filename) {
        URL url = null;
        if (filename.endsWith("css")) {
            url = getResource("css", filename);
        }
        if (url == null) {
            url = getResource("css", filename, ".css");
        }
        return url;
    }

    /**
     * 获取资源CSS目录下的文件
     * 与当前对象同名的小写css文件
     */
    public static URL getCss(Object o) {
        String filename = o.getClass().getSimpleName().toLowerCase();
        return getCss(filename);
    }

    public static URL getCss() {
        return getResource("css");
    }

    /**
     * 获取资源JS目录下的文件
     * 后缀.js可带可不带
     */
    public static URL getJs(String filename) {
        URL url = null;
        if (filename.endsWith("js")) {
            url = getResource("js", filename);
        }
        if (url == null) {
            url = getResource("js", filename, ".js");
        }
        return url;
    }

    /**
     * 获取资源JS目录下的文件
     * 与当前对象同名的小写js文件
     */
    public static URL getJs(Object o) {
        String filename = o.getClass().getSimpleName().toLowerCase();
        return getJs(filename);
    }

    public static URL getJs() {
        return getResource("css");
    }

    /**
     * 获取资源目录下的图片文件
     * jpg
     */
    public static URL getImage(String filename) {
        URL url = null;
        if (filename.endsWith("jpg")) {
            url = getResource("image", filename);
        }
        if (url == null) {
            url = getResource("image", filename, ".jpg");
        }
        return url;
    }

    public static URL getImage() {
        return getResource("image");
    }


    /**
     * 获取资源目录下的图标文件
     * png
     */
    public static URL getIcon(String filename) {
        URL url = null;
        if (filename.endsWith("png")) {
            url = getResource("icon", filename);
        }
        if (url == null) {
            url = getResource("icon", filename, ".png");
        }
        return url;
    }

    public static URL getIcon() {
        return getResource("icon");
    }

    /**
     * 获取资源目录下的语言文件
     */
    public static URL getLanguage(String filename) {
        URL url = null;
        if (filename.endsWith("json")) {
            url = getResource("language", filename);
        }
        if (url == null) {
            url = getResource("language", filename, ".json");
        }
        return url;
    }

    public static URL getLanguage() {
        return getResource("language");
    }


    /**
     * 快捷URL转URI工具
     */
    public static URI I(URL url) {
        return URI.create(url.toString());
    }

    /**
     * 快捷URI转URL工具
     */
    public static URL L(URI uri) {
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /******************************************************************************************************************/
    /***********************************************私有静态方法*********************************************************/
    /******************************************************************************************************************/
    /**
     * Path拼接
     */
    private static String pathSplicing(String... path) {
        StringBuilder sb = new StringBuilder("/");
        for (int i = 0; i < path.length; i++) {
            sb.append(path[i]);
            if (i < path.length - 2 || (i == path.length - 2 && !(path[path.length - 1].charAt(0) == '.'))) {
                sb.append("/");
            }
        }
        l.info("获取资源路径:" + sb);
        return sb.toString();
    }
}
